package gluu.scim2.client.servicemeta;

import gluu.scim2.client.BaseTest;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

/**
 * Created by jgomer on 2017-10-21.
 */
public class ResourceTypesTest extends BaseTest {

    private ListResponse listResponse;

    @BeforeTest
    public void init() throws Exception{
        Response response=client.getResourceTypes();
        listResponse=response.readEntity(ListResponse.class);
    }

    @Test
    public void checkResourcesExistence(){
        Assert.assertTrue(listResponse.getTotalResults()>0);

        listResponse.getResources().stream()
                .forEach(res -> Assert.assertTrue(res.getSchemas().contains("urn:ietf:params:scim:schemas:core:2.0:ResourceType")));

    }
}
