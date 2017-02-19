/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import org.gluu.oxtrust.model.scim2.Group;
import org.gluu.oxtrust.model.scim2.ListResponse;
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
 * @author Shekhar Laad 
 */
public class ScimClientRetreivingEntitiesTest extends BaseScimTest {

	Scim2Client client;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath" , "umaAatClientJksPassword" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws IOException {
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
	}

	@Parameters({ "userInum" })
	@Test
	public void retrievePersonTest(final String id) throws IOException {
		BaseClientResponse<User> response = client.retrievePerson(id, MediaType.APPLICATION_JSON);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get the user, status != 200");
	}

	@Test
	public void retrieveAllUsersTest() throws IOException {
		BaseClientResponse<ListResponse> response = client.retrieveAllUsers();
		assertEquals(response.getStatus(), 200, "Could not get the list of all users, status != 200");
	}

    @Parameters({ "userInum" })
    @Test
    public void testSearchUsersPost(final String id) throws IOException {

        String filter = "id eq \"" + id + "\"";
        BaseClientResponse<ListResponse> response = client.searchUsersPost(filter, 1, 1, "", "", null);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get the user, status != 200");
    }

    @Parameters({ "group1Inum" })
	@Test
	public void retrieveGroupTest(final String id) throws IOException {

		BaseClientResponse<Group> response = client.retrieveGroup(id, MediaType.APPLICATION_JSON);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get the group, status != 200");
	}

	@Test
	public void retrieveAllGroupsTest() throws IOException {

		BaseClientResponse<ListResponse> response = client.retrieveAllGroups();
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get a list of all groups, status != 200");
	}

    @Test
    public void testSearchGroupsPost() throws IOException {

        String filter = "id pr";
        BaseClientResponse<ListResponse> response = client.searchGroupsPost(filter, 1, 10, "", "", null);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get the user, status != 200");
    }
}
