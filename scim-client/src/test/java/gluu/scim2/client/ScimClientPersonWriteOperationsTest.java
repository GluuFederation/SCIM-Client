/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim2.client.factory.ScimClientFactory;
import org.gluu.oxtrust.model.scim2.User;
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
 * SCIM Client Person test
 *
 * @author Yuriy Movchan Date: 03/17/2016
 */
public class ScimClientPersonWriteOperationsTest extends BaseScimTest {

	private String id;
	private ScimClient client;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws IOException {
		client = ScimClientFactory.getClient(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
	}

	@Test
	@Parameters({ "scim2.person.create_json" })
	public void createPersonTest(String createJson) throws Exception {

		System.out.println("createPersonTest createJson: " + createJson);

		BaseClientResponse<User> response = client.createPersonString(createJson, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode(), "Could not add user, status != 201");

		User user = response.getEntity();
		this.id = user.getId();
	}

	@Test(dependsOnMethods = "createPersonTest")
	@Parameters({ "scim2.person.update_json", "userjson.update.givenname" })
	public void updatePersonTest(String updateJson, String updateGivenName) throws Exception {

		System.out.println("updatePersonTest updateJson: " + updateJson);

		BaseClientResponse<User> response = client.updatePersonString(updateJson, this.id, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not update user, status != 200");

		User user = response.getEntity();
		assertEquals(user.getName().getGivenName(), updateGivenName, "Could not update the user");
	}

	@Test(dependsOnMethods = "updatePersonTest")
	public void deletePersonTest() throws Exception {
		BaseClientResponse response = client.deletePerson(this.id);
		assertEquals(response.getStatus(), Response.Status.NO_CONTENT.getStatusCode(), "Could not delete user; status != 204");
	}
}