/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.scim2.client.rest.ClientSideService;
import gluu.scim2.client.rest.provider.ListResponseProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gluu.oxtrust.model.scim2.util.IntrospectUtil;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;


/**
 * SCIM default client
 *
 * @author Yuriy Movchan Date: 08/23/2013
 * Re-engineered by jgomer on 2017-09-14.
 */
public abstract class AbstractScimClient implements InvocationHandler {

    private Logger logger = LogManager.getLogger(getClass());

    /*
     All method calls using scimService will return a javax.ws.rs.core.Response object. The underlying data can be read
     using the readEntity method which automatically closes the unconsumed original response entity data stream if open,
     so there is no need to create a finally block with close() call at every usage of the service.
     One can inspect the "raw" response with Response.readEntity(String.class)
     */
    ClientSideService scimService;

    public AbstractScimClient(String domain){
        /*
         Configures a proxy to interact with the service using the new JAX-RS 2.0 Client API, see section
         "Resteasy Proxy Framework" of RESTEasy JAX-RS user guide
         */
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(domain);
        scimService = target.proxy(ClientSideService.class);
        target.register(ListResponseProvider.class);

    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{

        //Set authorization header if needed
        int i = IntrospectUtil.indexOfAuthzHeader(method.getParameterAnnotations());
        Response response;

        if (i>=0 && method.getParameters()[i].getType().equals(String.class)) {

            args[i]=getAuthenticationHeader();
            response=(Response) method.invoke(scimService, args);

            if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                if (authorize(response)) {
                    args[i] = getAuthenticationHeader();
                    response = (Response) method.invoke(scimService, args);
                }
                else
                    logger.error("Could not get authorization for current request: {}", method.getName());
            }
        }
        else {
            logger.warn("No authorization header applicable for method {} of interface", method.getName());
            Arrays.asList(proxy.getClass().getInterfaces()).stream().forEach(cl -> logger.warn(" {}", cl.getCanonicalName()));

            response=(Response) method.invoke(scimService, args);
        }
        return response;

    }

    protected abstract String getAuthenticationHeader();

    protected abstract boolean authorize(Response response);

}
