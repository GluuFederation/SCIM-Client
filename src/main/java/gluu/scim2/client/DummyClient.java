package gluu.scim2.client;

import gluu.scim2.client.AbstractScimClient;

import javax.ws.rs.core.Response;

/**
 * This type of client is useful for developers of SCIM server code. Use this client to make the development process in
 * oxTrust faster, by commenting the code that handles authorization in class  ???
 * and running tests using this client. This bypasses security, so DO NOT FORGET to revert the change in ???
 * Created by jgomer on 2017-10-19.
 */
//TODO: class name above
public class DummyClient extends AbstractScimClient {

    public DummyClient(String serviceurl){
        super(serviceurl);
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
