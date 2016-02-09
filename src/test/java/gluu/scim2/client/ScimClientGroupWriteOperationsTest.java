package gluu.scim2.client;

import static org.testng.Assert.assertEquals;
import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.Group;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Shekhar Laad 
 */
public class ScimClientGroupWriteOperationsTest   extends BaseScimTest{
	//Please increment this value if create method response status 400 may be this display value already exist.


	String CREATEJSON ;
	String UPDATEJSON ;
	String updatedDisplayName;	 
	String id;
	Scim2Client client;
	ScimResponse response;
	Group group;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId" , "groupjson.displayName" , "groupjson.updateddisplayname" , "groupjson.inum" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, final String umaAatClientKeyId , String displayName ,  String updatedDisplayName ,  String inum) throws IOException {
		System.out.println("displayName :"+  displayName +" updatedDisplayName : " + updatedDisplayName +" inum : "+   inum);
		this.updatedDisplayName = updatedDisplayName ; 
		CREATEJSON = "{\"schemas\":[\"urn:scim:schemas:core:2.0:Group\"],\"displayName\":\""+displayName+"\",\"members\":[{\"value\":\""+inum+"\",\"display\":\"Micheal Schwartz\"}]}";
		UPDATEJSON = "{\"schemas\":[\"urn:scim:schemas:core:2.0:Group\"],\"displayName\":\""+updatedDisplayName+"\",\"members\":[{\"value\":\""+inum+"\",\"display\":\"Micheal Schwartz\"}]}";
		String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
		response = null;
		group = null;
	}

	@Test(groups = "a")
	public void createGroupTest() throws Exception {

		response = client.createGroupString(CREATEJSON, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 201, "cold not Add the group, status != 201");
		String responseStr = response.getResponseBodyString();
		System.out.println("createGroupTest + responseStr"  + response.getResponseBodyString());
		group = (Group) jsonToObject(responseStr, Group.class);
		this.id = group.getId();

	}

	@Test(groups = "a")
	public void updateGroupTest() throws Exception {
		response = client.updateGroupString(UPDATEJSON, this.id, MediaType.APPLICATION_JSON);
		System.out.println("updateGroupTest + responseStr" + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not update the group, status != 200");
		String responseStr = response.getResponseBodyString();
		group = (Group) jsonToObject(responseStr, Group.class);
		assertEquals(group.getDisplayName(), updatedDisplayName, "could not update the group");
	}

	@Test(dependsOnGroups = "a")
	public void deleteGroupTest() throws Exception {

		response = client.deleteGroup(this.id);
		System.out.println("deleteGroupTest + responseStr" + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not delete the Group, status != 200");

	}

	private Object jsonToObject(String json, Class<?> clazz) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}
}
