package gluu.scim2.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.BeanUtil;
import gluu.BaseScimTest;
import gluu.scim2.client.factory.ScimClientFactory;
import org.apache.commons.beanutils.BeanUtils;
import org.gluu.oxtrust.model.scim2.*;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Created by jgomer on 2017-07-14.
 * This class contains cases related to very minor fixes applied to the project, generally focused on single attributes
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

    /**
     * See https://github.com/GluuFederation/SCIM-Client/issues/18
     * @throws Exception
     */
    @Test
    public void countryISOCode() throws Exception{

        User user = createDummyUser();
        String newUsrId=user.getId();

        //Update user to add a couple of addresses
        List<Address> list=new ArrayList<Address>();

        Address addr=new Address();
        addr.setStreetAddress("random street");
        addr.setCountry("US");
        addr.setType(Address.Type.WORK);
        list.add(addr);

        addr=(Address)BeanUtils.cloneBean(addr);
        addr.setCountry("Colombia");
        list.add(addr);

        user.setAddresses(list);

        BaseClientResponse<User> response=client.updateUser(user, newUsrId, new String[]{});
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        //Update address with an ISO code and repeat for success
        addr.setCountry("CO");

        response=client.updateUser(user, newUsrId, new String[]{});
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        deleteDummyUser(newUsrId);

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