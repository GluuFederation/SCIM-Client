/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2017, Gluu
 */
package gluu.scim2.client;

import javax.ws.rs.core.Response;

/**
 * This type of client is useful for developers of SCIM server code only.
 * <p><b>Note:</b> Do not instantiate this class in your code. To interact with the service, call the corresponding method in
 * class {@link gluu.scim2.client.factory.ScimClientFactory ScimClientFactory} that returns a proxy object wrapping this client
 * @param <T> Type parameter of superclass
 */
/*
 * Use this client to make the development process in oxTrust faster:
 * 1- Comment the code that handles authorization in org.gluu.oxtrust.ws.rs.scim2.AuthorizationProcessingFilter#filter
 * 2- Use this client instead of the test-mode client in gluu.scim2.client.BaseTest#setupClient(java.util.Map)
 * 3- Run the tests passing -Dtestmode=true in maven command line interface
 * Warning: This is bypassing security, so DO NOT FORGET to revert the changes
 *
 * Created by jgomer on 2017-10-19.
 */
public class DummyClient<T> extends AbstractScimClient<T> {

    public DummyClient(Class<T> serviceClass, String serviceurl){
        super(serviceurl, serviceClass);
    }

    @Override
    protected String getAuthenticationHeader(){
        return null;
    }

    @Override
    protected boolean authorize(Response response) {
        return false;
    }

}
