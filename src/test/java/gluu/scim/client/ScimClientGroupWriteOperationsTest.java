package gluu.scim.client;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import gluu.BaseScimTest;
import gluu.scim.client.model.ScimGroup;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * SCIM Client group Create,Update,Delete tests
 *
 * @author Reda Zerrad Date: 06.01.2012
 */
public class ScimClientGroupWriteOperationsTest extends BaseScimTest{

	String CREATEJSON = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"displayName\":\"Gluu Testing GroupSCIMCLIENT\",\"members\":[{\"value\":\"<test_user_inum>\",\"display\":\"Micheal Schwartz\"}]}";
	String UPDATEJSON = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"displayName\":\"Gluu Testing GroupSCIMCLIENTUpdate\",\"members\":[{\"value\":\"<test_user_inum>\",\"display\":\"Micheal Schwartz\"}]}";
	String id;
	ScimClient client;
	ScimResponse response;
	ScimGroup group;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId", "userInum" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId, final String userInum) {
		try {
			String jwks = FileUtils.readFileToString(new File(umaAatClientJwks));				
			client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaAatClientId, jwks, umaAatClientKeyId);
			response = null;
		} catch (IOException e) {
			System.out.println("exception in reading fle " + e.getMessage());
		}

		this.CREATEJSON = CREATEJSON.replaceAll("<test_user_inum>", userInum);
		this.UPDATEJSON = UPDATEJSON.replaceAll("<test_user_inum>", userInum);
	}

	@Test(groups = "a")
	public void createGroupTest() throws Exception {

		response = client.createGroupString(CREATEJSON, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 201, "cold not Add the group, status != 201");
		String responseStr = response.getResponseBodyString();
		group = (ScimGroup) jsonToObject(responseStr, ScimGroup.class);
		this.id = group.getId();

	}

	@Test(groups = "a")
	public void updateGroupTest() throws Exception {

		response = client.updateGroupString(UPDATEJSON, this.id, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 200, "cold not update the group, status != 200");

	}

	@Test(dependsOnGroups = "a")
	public void deleteGroupTest() throws Exception {

		response = client.deleteGroup(this.id);

		assertEquals(response.getStatusCode(), 200, "cold not delete the Group, status != 200");

	}

	private Object jsonToObject(String json, Class<?> clazz) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}
}
