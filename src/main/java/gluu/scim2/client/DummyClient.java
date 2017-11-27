package gluu.scim2.client;

import javax.ws.rs.core.Response;

/**
 * This type of client is useful for developers of SCIM server code only. Use this client to make the development
 * process in oxTrust faster:
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
