/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client;

import static org.testng.Assert.assertEquals;
import gluu.BaseScimTest;
import gluu.scim.client.model.ScimGroup;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import gluu.scim2.client.util.Util;
import org.junit.Assert;
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

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath" , "umaAatClientJksPassword" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws IOException {
						
		client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
		
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
		assertEquals(response.getStatusCode(), 201, "Could not add group, status != 201");

		ScimGroup group = (ScimGroup) Util.jsonToObject(response, ScimGroup.class);
		this.id = group.getId();
	}

	@Test(dependsOnMethods = "createGroupTest")
	public void updateGroupTest() throws Exception {

		ScimResponse response = client.updateGroup(groupToUpdate, this.id, MediaType.APPLICATION_JSON);
		assertEquals(response.getStatusCode(), 200, "Could not update group, status != 200");
	}

	@Test(dependsOnMethods = "updateGroupTest")
	public void testUpdateDisplayNameDifferentId() throws Exception {

		System.out.println("IN testUpdateDisplayNameDifferentId...");

		ScimResponse response = client.retrieveGroup(this.id, MediaType.APPLICATION_JSON);
		System.out.println("response body = " + response.getResponseBodyString());

		Assert.assertEquals(200, response.getStatusCode());

		ScimGroup groupRetrieved = (ScimGroup) Util.jsonToObject(response, ScimGroup.class);

		groupRetrieved.setDisplayName("Gluu Manager Group");

		ScimResponse responseUpdated = client.updateGroup(groupRetrieved, this.id, MediaType.APPLICATION_JSON);

		Assert.assertEquals(400, responseUpdated.getStatusCode());
		System.out.println("UPDATED response body = " + responseUpdated.getResponseBodyString());

		System.out.println("LEAVING testUpdateDisplayNameDifferentId..." + "\n");
	}

	@Test(dependsOnMethods = "testUpdateDisplayNameDifferentId", alwaysRun = true)
	public void deleteGroupTest() throws Exception {

		ScimResponse response = client.deleteGroup(this.id);
		assertEquals(response.getStatusCode(), 200, "Could not delete group, status != 200");
	}
}
