/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import org.gluu.oxtrust.model.scim2.Group;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Shekhar Laad
 */
public class GroupWebServiceTestCase extends BaseScimTest {

	Group groupToAdd;
	Group groupToUpdate;
	String id;
	Scim2Client client;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId", "groupwebservice.add.displayname", "groupwebservice.update.displayname" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId, final String displayName, final String updateDisplayName) throws IOException {

		System.out.println(" displayName : " + displayName + "   updateDisplayName : " + updateDisplayName);
		
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
		groupToAdd = new Group();
		groupToUpdate = new Group();
		groupToAdd.setDisplayName(displayName);
		groupToUpdate.equals(groupToAdd);
		groupToUpdate.setDisplayName(updateDisplayName);
	}

	@Test
	public void createGroupTest() throws Exception {

		BaseClientResponse<Group> response = client.createGroup(groupToAdd, MediaType.APPLICATION_JSON);
		assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode(), "Could not add the group, status != 201");
		Group group = response.getEntity();
		this.id = group.getId();
		assertEquals(group.getDisplayName(), groupToAdd.getDisplayName(), "Username MisMatch");
	}

	@Test(dependsOnMethods = "createGroupTest")
	public void updateGroupTest() throws Exception {

		BaseClientResponse<Group> response = client.updateGroup(groupToUpdate, this.id, MediaType.APPLICATION_JSON);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not update the group, status != 200");
		Group group = response.getEntity();
		assertEquals(group.getDisplayName(), groupToUpdate.getDisplayName(), "could not update the user");
	}

	@Test(dependsOnMethods = "updateGroupTest")
	public void deleteGroupTest() throws Exception {

		BaseClientResponse response = client.deleteGroup(this.id);
		assertEquals(response.getStatus(), Response.Status.NO_CONTENT.getStatusCode(), "Could not delete the group; status != 204");
	}

	@Parameters({ "group1Inum" })
	@Test
	public void retrieveGroupTest(final String group1Inum) throws IOException {

		BaseClientResponse<Group> response = client.retrieveGroup(group1Inum, MediaType.APPLICATION_JSON);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get the group, status != 200");
	}

	@Test
	public void retrieveAllGroupsTest() throws IOException {
		BaseClientResponse<ListResponse> response = client.retrieveAllGroups();
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get a list of all groups, status != 200");
	}
}
