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
 * @author Yuriy Movchan Date: 03/17/2016
 */
public class ScimClientGroupWriteOperationsTest extends BaseScimTest {

	private Scim2Client client;
	private String id;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws IOException {
		String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
	}

	@Test
	@Parameters({ "scim2.group.create_json" })
	public void createGroupTest(String createJson) throws Exception {

		System.out.println("createGroupTest createJson: " + createJson);

		ScimResponse response = client.createGroupString(createJson, MediaType.APPLICATION_JSON);
		System.out.println("createGroupTest response json: " + response.getResponseBodyString());

		assertEquals(response.getStatusCode(), 201, "Could not add group, status != 201");

		Group group = Util.toGroup(response);
		this.id = group.getId();

	}

	@Test(dependsOnMethods = "createGroupTest")
	@Parameters({ "scim2.group.update_json", "groupjson.updateddisplayname" })
	public void updateGroupTest(String updateJson, String updatedDisplayName) throws Exception {

		System.out.println("updateGroupTest updateJson: " + updateJson);
		ScimResponse response = client.updateGroupString(updateJson, this.id, MediaType.APPLICATION_JSON);

		System.out.println("updateGroupTest + responseStr" + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Could not update group, status != 200");

		Group group = Util.toGroup(response);
		assertEquals(group.getDisplayName(), updatedDisplayName, "Could not update the group");
	}

	@Test(dependsOnMethods = "updateGroupTest")
	public void deleteGroupTest() throws Exception {
		ScimResponse response = client.deleteGroup(this.id);
		System.out.println("deleteGroupTest + responseStr" + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Could not delete group, status != 200");
	}
}
