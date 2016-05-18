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
import org.gluu.oxtrust.model.scim2.User;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * SCIM Client Person test
 *
 * @author Yuriy Movchan Date: 03/17/2016
 */
public class ScimClientPersonWriteOperationsTest extends BaseScimTest {

	private String uid;
	private Scim2Client client;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws IOException {
		String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
	}

	@Test
	@Parameters({ "scim2.person.create_json" })
	public void createPersonTest(String createJson) throws Exception {

		System.out.println("createPersonTest createJson: " + createJson);

		ScimResponse response = client.createPersonString(createJson, MediaType.APPLICATION_JSON);
		System.out.println("createPersonTest response json: " + response.getResponseBodyString());

		assertEquals(response.getStatusCode(), 201, "cold not Add the user, status != 201");

		User user = Util.toUser(response, client.getUserExtensionSchema());
		this.uid = user.getId();
	}

	@Test(dependsOnMethods = "createPersonTest")
	@Parameters({ "scim2.person.update_json", "userjson.update.givenname" })
	public void updatePersonTest(String updateJson, String updateGivenName) throws Exception {

		System.out.println("updatePersonTest updateJson: " + updateJson);

		ScimResponse response = client.updatePersonString(updateJson, this.uid, MediaType.APPLICATION_JSON);
		System.out.println("updatePersonTest response json: " + response.getResponseBodyString());

		assertEquals(response.getStatusCode(), 200, "cold not update the user, status != 200");

		User user = Util.toUser(response, client.getUserExtensionSchema());
		assertEquals(user.getName().getGivenName(), updateGivenName, "Could not update the user");
	}

	@Test(dependsOnMethods = "updatePersonTest")
	public void deletePersonTest() throws Exception {
		ScimResponse response = client.deletePerson(this.uid);
		System.out.println("deletePersonTest response json: " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not delete the user, status != 200");
	}
}