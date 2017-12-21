/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.scim2.client.rest.FreelyAccessible;
import gluu.scim2.client.rest.provider.AuthorizationInjectionFilter;
import gluu.scim2.client.rest.provider.ListResponseProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * SCIM default client
 *
 * @author Yuriy Movchan Date: 08/23/2013
 * Re-engineered by jgomer on 2017-09-14.
 */
public abstract class AbstractScimClient<T> implements InvocationHandler {

    private Logger logger = LogManager.getLogger(getClass());

    /*
     All method calls using scimService (with exception of close) will return a javax.ws.rs.core.Response object.
     The underlying data can be read using the readEntity method which automatically closes the unconsumed original
     response entity data stream if open, so there is no need to create a finally block with close() call at every usage
     of the service. You can inspect the "raw" response with Response.readEntity(String.class)
     */
    private T scimService;

    private ResteasyClient client;

    public AbstractScimClient(String domain, Class<T> serviceClass){
        /*
         Configures a proxy to interact with the service using the new JAX-RS 2.0 Client API, see section
         "Resteasy Proxy Framework" of RESTEasy JAX-RS user guide
         */
        client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(domain);

        scimService = target.proxy(serviceClass);
        target.register(ListResponseProvider.class);
        target.register(AuthorizationInjectionFilter.class);

        ClientMap.update(client, null);
    }

    private Response invokeServiceMethod(Method method, Object[] args) throws ReflectiveOperationException{

        logger.trace("Sending service request for method {}", method.getName());
        Response response = (Response) method.invoke(scimService, args);
        logger.trace("Received response entity was{} buffered", response.bufferEntity() ? "" : " not");
        logger.trace("Response status code was {}", response.getStatus());
        return response;

    }

    /**
     * All client invocations are "intercepted and dispatched" here
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{

        String methodName=method.getName();

        if (methodName.equals("close")) {
            logger.info("Closing RestEasy client");
            ClientMap.remove(client);
            return null;
        }
        else{
            Response response;
            FreelyAccessible unprotected=method.getAnnotation(FreelyAccessible.class);

            //Set authorization header if needed
            if (unprotected!=null){
                response = invokeServiceMethod(method, args);
            }
            else{
                ClientMap.update(client, getAuthenticationHeader());
                response = invokeServiceMethod(method, args);

                if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                    if (authorize(response)) {
                        logger.trace("Trying second attempt of request (former received unauthorized response code)");
                        ClientMap.update(client, getAuthenticationHeader());
                        response = invokeServiceMethod(method, args);
                    }
                    else
                        logger.error("Could not get access token for current request: {}", methodName);
                }
            }
            return response;
        }

    }

    protected abstract String getAuthenticationHeader();

    protected abstract boolean authorize(Response response);

}
