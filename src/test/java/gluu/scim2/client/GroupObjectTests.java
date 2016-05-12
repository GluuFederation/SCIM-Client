package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.Group;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.Date;

import static org.testng.Assert.assertEquals;

/**
 * @author Val Pecaoco
 */
public class GroupObjectTests extends BaseScimTest {

    String domainURL;
    Scim2Client client;

    String id;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws Exception {
        this.domainURL = domainURL;
        String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
        client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
    }

    @Test(groups = "a")
    public void testCreateGroup() throws Exception {

        System.out.println("IN testCreateGroup...");

        String displayName = "Test Group " + new Date().getTime();

        Group group = new Group();
        group.getSchemas().add(Constants.GROUP_CORE_SCHEMA_ID);
        group.setDisplayName(displayName);

        ScimResponse response = client.createGroup(group, MediaType.APPLICATION_JSON);
        System.out.println("body string = " + response.getResponseBodyString());

        Assert.assertEquals(201, response.getStatusCode());

        byte[] bytes = response.getResponseBody();
        String responseStr = new String(bytes);

        Group groupCreated = (Group)jsonToObject(responseStr, Group.class);
        assertEquals(groupCreated.getDisplayName(), displayName, "Group not added or retrieved");

        this.id = groupCreated.getId();

        System.out.println("responseStr = " + responseStr);
        System.out.println("groupCreated.getId() = " + groupCreated.getId());
        System.out.println("groupCreated.getDisplayName() = " + groupCreated.getDisplayName());

        System.out.println("LEAVING testCreateGroup..." + "\n");
    }

    @Test(groups = "b", dependsOnGroups = "a")
    public void testRetrieveNewGroup() throws Exception {

        System.out.println("IN testRetrieveNewGroup...");

        ScimResponse response = client.retrieveGroup(this.id, MediaType.APPLICATION_JSON);
        System.out.println("body string = " + response.getResponseBodyString());

        Assert.assertEquals(200, response.getStatusCode());

        byte[] bytes = response.getResponseBody();
        String responseStr = new String(bytes);

        Group groupRetrieved = (Group)jsonToObject(responseStr, Group.class);
        assertEquals(groupRetrieved.getId(), this.id, "Group could not be retrieved");

        System.out.println("responseStr = " + responseStr);
        System.out.println("groupRetrieved.getId() = " + groupRetrieved.getId());
        System.out.println("groupRetrieved.getDisplayName() = " + groupRetrieved.getDisplayName());

        System.out.println("LEAVING testRetrieveNewGroup..." + "\n");
    }

    @Test(groups = "c", dependsOnGroups = "b")
    public void testUpdateNewGroup() throws Exception {

        System.out.println("IN testUpdateNewGroup...");

        Thread.sleep(5000);  // Sleep for 5 seconds

        ScimResponse response = client.retrieveGroup(this.id, MediaType.APPLICATION_JSON);
        System.out.println("body string = " + response.getResponseBodyString());

        Assert.assertEquals(200, response.getStatusCode());

        byte[] bytes = response.getResponseBody();
        String responseStr = new String(bytes);

        Group groupRetrieved = (Group)jsonToObject(responseStr, Group.class);

        groupRetrieved.setDisplayName(groupRetrieved.getDisplayName() + " UPDATED");

        ScimResponse responseUpdated = client.updateGroup(groupRetrieved, this.id, MediaType.APPLICATION_JSON);

        Assert.assertEquals(200, responseUpdated.getStatusCode());

        bytes = responseUpdated.getResponseBody();
        responseStr = new String(bytes);

        Group groupUpdated = (Group)jsonToObject(responseStr, Group.class);

        assertEquals(groupUpdated.getId(), this.id, "Group could not be retrieved");
        assert(groupUpdated.getMeta().getLastModified().getTime() > groupUpdated.getMeta().getCreated().getTime());

        System.out.println("UPDATED responseStr = " + responseStr);
        System.out.println("groupUpdated.getId() = " + groupUpdated.getId());
        System.out.println("groupUpdated.getDisplayName() = " + groupUpdated.getDisplayName());
        System.out.println("groupUpdated.getMeta().getLastModified().getTime() = " + groupUpdated.getMeta().getLastModified().getTime());
        System.out.println("groupUpdated.getMeta().getCreated().getTime() = " + groupUpdated.getMeta().getCreated().getTime());

        System.out.println("LEAVING testUpdateNewGroup..." + "\n");
    }

    @Test(groups = "d", dependsOnGroups = "c", alwaysRun = true)
    public void testDeleteGroup() throws Exception {

        System.out.println("IN testDeleteGroup...");

        ScimResponse response = client.deleteGroup(this.id);
        assertEquals(response.getStatusCode(), 200, "Group could not be deleted, status != 200");

        System.out.println("LEAVING testDeleteGroup..." + "\n");
    }

    private Object jsonToObject(String json, Class<?> clazz) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        Object clazzObject = mapper.readValue(json, clazz);
        return clazzObject;
    }
}
