package gluu.scim2.client.rest;

import javax.ws.rs.GET;

/**
 * This allows the client obtained with ScimClientFactory to be closeable.
 * Created by jgomer on 2017-11-25.
 */
public interface CloseableClient {

    //Annotation here is dummy (any method can be used). Calling close will not issue an HTTP request but releasing resources
    @GET
    void close();
}
