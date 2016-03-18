/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client;

import static org.testng.Assert.assertEquals;
import gluu.BaseScimTest;
import gluu.scim.client.model.ScimGroup;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * SCIM Client group Create,Update,Delete as an object tests
 *
 * @author Reda Zerrad Date: 06.05.2012
 * @author Yuriy Movchan Date: 03/17/2016
 */
public class ScimClientGroupWriteObjectTest extends BaseScimTest {

	private ScimClient client;

	private ScimGroup groupToAdd;
	private ScimGroup groupToUpdate;

	private String id;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws IOException {
		String jwks = FileUtils.readFileToString(new File(umaAatClientJwks));				
		client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaAatClientId, jwks, umaAatClientKeyId);
		
		groupToAdd = new ScimGroup();
		groupToUpdate = new ScimGroup();
		groupToAdd.getSchemas().add("urn:scim:schemas:core:1.0");
		groupToAdd.setDisplayName("ScimObjecttesting");
		groupToUpdate.equals(groupToAdd);
		groupToUpdate.setDisplayName("ScimObjecttesting1");
	}

	@Test
	public void createGroupTest() throws Exception {
		ScimResponse response = client.createGroup(groupToAdd, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 201, "cold not Add the group, status != 201");
		String responseStr = response.getResponseBodyString();
		ScimGroup group = (ScimGroup) jsonToObject(responseStr, ScimGroup.class);
		this.id = group.getId();

	}

	@Test(dependsOnMethods = "createGroupTest")
	public void updateGroupTest() throws Exception {
		ScimResponse response = client.updateGroup(groupToUpdate, this.id, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 200, "cold not update the group, status != 200");

	}

	@Test(dependsOnMethods = "updateGroupTest")
	public void deleteGroupTest() throws Exception {
		ScimResponse response = client.deleteGroup(this.id);

		assertEquals(response.getStatusCode(), 200, "cold not delete the Group, status != 200");

	}

	private Object jsonToObject(String json, Class<?> clazz) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}

}
