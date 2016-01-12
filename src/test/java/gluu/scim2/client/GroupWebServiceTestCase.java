package gluu.scim2.client;

import static org.testng.Assert.assertEquals;
import gluu.scim.client.ScimResponse;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.Group;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Shekhar Laad 
 */
public class GroupWebServiceTestCase {
	
	Group groupToAdd;
	Group groupToUpdate;
	String id;
	Scim2Client client;
	ScimResponse response;
	Group group;
	
	//Please increment this value if create method response status 400 may be this display value alreasy exist.
	String DisplayName = "ScimObjecttesting11";

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, final String umaAatClientKeyId) throws IOException {
		String jwks = FileUtils.readFileToString(new File(umaAatClientJwks));;
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, jwks, umaAatClientKeyId);
		response = null;
		group = null;
		groupToAdd = new Group();
		groupToUpdate = new Group();
		groupToAdd.setDisplayName(DisplayName);
		groupToUpdate.equals(groupToAdd);
		groupToUpdate.setDisplayName("ScimObjecttesting1");
	}

@Test(groups = "a")
	public void createGroupTest() throws Exception {

		response = client.createGroup(groupToAdd, MediaType.APPLICATION_JSON);
		System.out.println("GroupWebServiceTestCase :  createGroupTest :  response : " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 201, "cold not Add the group, status != 201");
		String responseStr = response.getResponseBodyString();
		group = (Group) jsonToObject(responseStr, Group.class);
		this.id = group.getId();
		System.out.println("response : " + response.getResponseBodyString());
	}

	@Test(groups = "a")
	public void updateGroupTest() throws Exception {
		response = client.updateGroup(groupToUpdate, this.id, MediaType.APPLICATION_JSON);
		System.out.println("GroupWebServiceTestCase updateGroupTest :response : " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not update the group, status != 200");
		
	}

	@Test(dependsOnGroups = "a")
	public void deleteGroupTest() throws Exception {

		response = client.deleteGroup(this.id);
		System.out.println("GroupWebServiceTestCase deleteGroupTest :response : " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not delete the Group, status != 200");
	}
	
	@Parameters({ "id" })
	@Test
	public void retrieveGroupTest(final String id) throws HttpException, IOException {

		response = client.retrieveGroup(id, MediaType.APPLICATION_JSON);
		System.out.println("GroupWebServiceTestCase retrieveGroupTest :response : " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not get the group, status != 200");
		
	}

	@Test
	public void retrieveAllGroupsTest() throws HttpException, IOException {

		response = client.retrieveAllGroups(MediaType.APPLICATION_JSON);
		System.out.println("GroupWebServiceTestCase retrieveAllGroupsTest :response : " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not get a list of all groups, status != 200");
		
	}

	private Object jsonToObject(String json, Class<?> clazz) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}

}
