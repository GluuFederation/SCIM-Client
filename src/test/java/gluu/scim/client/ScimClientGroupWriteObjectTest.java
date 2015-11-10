package gluu.scim.client;

import static org.testng.Assert.assertEquals;
import gluu.scim.client.model.ScimGroup;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * SCIM Client group Create,Update,Delete as an object tests
 *
 * @author Reda Zerrad Date: 06.05.2012
 */
public class ScimClientGroupWriteObjectTest {
	ScimGroup groupToAdd;
	ScimGroup groupToUpdate;
	String id;
	ScimClient client;
	ScimResponse response;
	ScimGroup group;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientSecret" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientSecret) {
		client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientSecret);
		response = null;
		group = null;
		groupToAdd = new ScimGroup();
		groupToUpdate = new ScimGroup();
		groupToAdd.getSchemas().add("urn:scim:schemas:core:1.0");
		groupToAdd.setDisplayName("ScimObjecttesting");
		groupToUpdate.equals(groupToAdd);
		groupToUpdate.setDisplayName("ScimObjecttesting1");
	}

	@Test(groups = "a")
	public void createGroupTest() throws Exception {

		response = client.createGroup(groupToAdd, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 201, "cold not Add the group, status != 201");
		String responseStr = response.getResponseBodyString();
		group = (ScimGroup) jsonToObject(responseStr, ScimGroup.class);
		this.id = group.getId();

	}

	@Test(groups = "a")
	public void updateGroupTest() throws Exception {

		response = client.updateGroup(groupToUpdate, this.id, MediaType.APPLICATION_JSON);

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
