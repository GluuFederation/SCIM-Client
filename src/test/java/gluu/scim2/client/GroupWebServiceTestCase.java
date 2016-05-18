/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import static org.testng.Assert.assertEquals;
import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import gluu.scim2.client.util.Util;
import org.apache.commons.io.FileUtils;
import org.gluu.oxtrust.model.scim2.Group;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Shekhar Laad
 */
public class GroupWebServiceTestCase extends BaseScimTest {

	Group groupToAdd;
	Group groupToUpdate;
	String id;
	Scim2Client client;
	ScimResponse response;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId",
			"groupwebservice.add.displayname", "groupwebservice.update.displayname" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks,
			@Optional final String umaAatClientKeyId, final String displayName, final String updateDisplayName) throws IOException {
		System.out.println(" displayName : " + displayName + "   updateDisplayName : " + updateDisplayName);
		String jwks = FileUtils.readFileToString(new File(umaAatClientJwks));
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, jwks, umaAatClientKeyId);
		response = null;
		groupToAdd = new Group();
		groupToUpdate = new Group();
		groupToAdd.setDisplayName(displayName);
		groupToUpdate.equals(groupToAdd);
		groupToUpdate.setDisplayName(updateDisplayName);
	}

	@Test
	public void createGroupTest() throws Exception {
		response = client.createGroup(groupToAdd, MediaType.APPLICATION_JSON);
		System.out.println("GroupWebServiceTestCase :  createGroupTest :  response : " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 201, "cold not Add the group, status != 201");
		Group group = (Group) Util.toGroup(response);
		this.id = group.getId();
		System.out.println("response : " + response.getResponseBodyString());
		assertEquals(group.getDisplayName(), groupToAdd.getDisplayName(), "Username MisMatch");
	}

	@Test(dependsOnMethods = "createGroupTest")
	public void updateGroupTest() throws Exception {
		response = client.updateGroup(groupToUpdate, this.id, MediaType.APPLICATION_JSON);
		System.out.println("GroupWebServiceTestCase updateGroupTest :response : " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not update the group, status != 200");
		Group group = (Group) Util.toGroup(response);
		assertEquals(group.getDisplayName(), groupToUpdate.getDisplayName(), "could not update the user");
	}

	@Test(dependsOnMethods = "updateGroupTest")
	public void deleteGroupTest() throws Exception {
		response = client.deleteGroup(this.id);
		System.out.println("GroupWebServiceTestCase deleteGroupTest :response : " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not delete the Group, status != 200");
	}

	@Parameters({ "group1Inum" })
	@Test
	public void retrieveGroupTest(final String group1Inum) throws IOException {
		response = client.retrieveGroup(group1Inum, MediaType.APPLICATION_JSON);
		System.out.println("GroupWebServiceTestCase retrieveGroupTest :response : " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not get the group, status != 200");
	}

	@Test
	public void retrieveAllGroupsTest() throws IOException {
		response = client.retrieveAllGroups(MediaType.APPLICATION_JSON);
		System.out.println("GroupWebServiceTestCase retrieveAllGroupsTest :response : " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not get a list of all groups, status != 200");
	}
}
