package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim2.client.factory.ScimClientFactory;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.Name;
import org.gluu.oxtrust.model.scim2.User;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

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

    //@Test
    public void retrieveAllUsers() throws IOException {
        BaseClientResponse<ListResponse> response = client.retrieveAllUsers();
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get a list of all users, status != 200");
    }

    //@Test
    public void retrieveAllGroups() throws IOException {
        BaseClientResponse<ListResponse> response = client.retrieveAllGroups();
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get a list of all groups, status != 200");
    }

    /**
     * See https://github.com/GluuFederation/SCIM-Client/issues/21
     * @throws Exception
     */

    @Test
    public void nullMiddleNameUser() throws Exception{

        User user = createDummyUser();
        String newUsrId=user.getId();

        //Retrieve user
        BaseClientResponse<User>response = client.retrievePerson(newUsrId, MediaType.APPLICATION_JSON);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        User userRetrieved = response.getEntity();

        //... and check it has correct middle name
        assertEquals(userRetrieved.getId(), newUsrId);
        assertEquals(userRetrieved.getName().getMiddleName(), user.getName().getMiddleName());

        //Check if setting to empty string or null is really flushing the middle name attr
        userRetrieved.getName().setMiddleName("");
        response=client.updateUser(userRetrieved, newUsrId, new String[]{});

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        userRetrieved = response.getEntity();
        assertNull(userRetrieved.getName().getMiddleName());

        //Check if setting the null attribute to non-empty takes effect
        String newMiddleName=UUID.randomUUID().toString();
        userRetrieved.getName().setMiddleName(newMiddleName);

        response=client.updateUser(userRetrieved, newUsrId, new String[]{});
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        userRetrieved = response.getEntity();

        assertEquals(userRetrieved.getName().getMiddleName(), newMiddleName);

        deleteDummyUser(newUsrId);

    }

    /**
     * See https://github.com/GluuFederation/SCIM-Client/issues/19
     * @throws Exception
     */
    @Test
    public void PPIDSearch() throws Exception{

        //Create a dummy user
        User user = createDummyUser();
        String newUsrId=user.getId();

        //Set it with two random ppids
        List<String> ppids=Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        user.setPairwiseIdentitifers(ppids);

        //Update him
        BaseClientResponse<User> response=client.updateUser(user, newUsrId, new String[]{});
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        //Search for one ppid
        String filter=String.format("oxPPID eq \"%s\"", ppids.get(0));
        BaseClientResponse<ListResponse> list = client.searchUsers(filter, 0, Constants.MAX_COUNT, null, null, null);
        assertEquals(Response.Status.OK.getStatusCode(), list.getStatus());
        assertEquals(list.getEntity().getTotalResults(),1);

        deleteDummyUser(newUsrId);

    }

    private void deleteDummyUser(String id) throws Exception{
        //Delete dummy
        BaseClientResponse<User>response=client.deletePerson(id);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    private User createDummyUser() throws Exception{

        User user=new User();
        Name name = new Name();

        name.setGivenName("Jose");
        name.setMiddleName("Graupera");
        name.setFamilyName("Capablanca");

        user.setName(name);
        user.setDisplayName("Dummy");
        user.setUserName("j"+ new Date().getTime());
        user.setActive(true);

        //Create a dummy user for testing
        BaseClientResponse<User> response = client.createPerson(user, MediaType.APPLICATION_JSON);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode(), "Fail to add user " + user.getDisplayName());

        return response.getEntity();

    }

}