package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim2.client.factory.ScimClientFactory;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;

/**
 * Created by jgomer on 2017-07-14.
 */
public class TestModeTests extends BaseScimTest {

    ScimClient client;

    @Parameters({ "domainURL", "OIDCMetadataUrl" })
    @BeforeTest
    public void init(String domainURL, String OIDCMetadataUrl){

        try {
            System.out.println("Testing the test mode of SCIM-client with params: " + Arrays.asList(domainURL, OIDCMetadataUrl).toString());
            client = ScimClientFactory.getTestClient(domainURL, OIDCMetadataUrl);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void retrieveAllUsers() throws IOException {
        BaseClientResponse<ListResponse> response = client.retrieveAllUsers();
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get a list of all users, status != 200");
    }

    @Test
    public void retrieveAllGroups() throws IOException {
        BaseClientResponse<ListResponse> response = client.retrieveAllGroups();
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get a list of all groups, status != 200");
    }

}
