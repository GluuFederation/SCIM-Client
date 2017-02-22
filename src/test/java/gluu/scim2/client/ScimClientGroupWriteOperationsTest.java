/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import org.gluu.oxtrust.model.scim2.Group;
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
 * @author Yuriy Movchan Date: 03/17/2016
 */
public class ScimClientGroupWriteOperationsTest extends BaseScimTest {

	private ScimClient client;
	private String id;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws IOException {
		client = ScimClientFactory.getClient(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
	}

	@Test
	@Parameters({ "scim2.group.create_json" })
	public void createGroupTest(String createJson) throws Exception {

		System.out.println("createGroupTest createJson: " + createJson);

		BaseClientResponse<Group> response = client.createGroupString(createJson, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode(), "Could not add group, status != 201");

		Group group = response.getEntity();
		this.id = group.getId();

	}

	@Test(dependsOnMethods = "createGroupTest")
	@Parameters({ "scim2.group.update_json", "groupjson.updateddisplayname" })
	public void updateGroupTest(String updateJson, String updatedDisplayName) throws Exception {

		System.out.println("updateGroupTest updateJson: " + updateJson);
		BaseClientResponse<Group> response = client.updateGroupString(updateJson, this.id, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not update group, status != 200");

		Group group = response.getEntity();
		assertEquals(group.getDisplayName(), updatedDisplayName, "Could not update the group");
	}

	@Test(dependsOnMethods = "updateGroupTest")
	public void deleteGroupTest() throws Exception {
		BaseClientResponse response = client.deleteGroup(this.id);
		assertEquals(response.getStatus(), Response.Status.NO_CONTENT.getStatusCode(), "Could not delete group; status != 204");
	}
}
