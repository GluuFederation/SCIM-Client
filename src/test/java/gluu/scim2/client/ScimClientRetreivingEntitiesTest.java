/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import static org.testng.Assert.assertEquals;
import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;

import java.io.IOException;
import javax.ws.rs.core.MediaType;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Shekhar Laad 
 */
public class ScimClientRetreivingEntitiesTest extends BaseScimTest {

	Scim2Client client;
	ScimResponse response;
	
	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath" , "umaAatClientJksPassword" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws IOException {
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
	}

	@Parameters({ "userInum" })
	@Test
	public void retrievePersonTest(final String id) throws IOException {

		response = client.retrievePerson(id, MediaType.APPLICATION_JSON);
		System.out.println("retrievePersonTest + responseStr = "  + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Could not get the user, status != 200");
	}

	@Test
	public void retrieveAllUsersTest() throws IOException {

		response = client.retrieveAllUsers();
		System.out.println("response body = "  + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Could not get the list of all users, status != 200");
	}

    @Parameters({ "userInum" })
    @Test
    public void testSearchUsersPost(final String id) throws IOException {

        String filter = "id eq \"" + id + "\"";
        response = client.searchUsersPost(filter, 1, 1, "", "", null);
        System.out.println("response body = "  + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "Could not get the user, status != 200");
    }

    @Parameters({ "group1Inum" })
	@Test
	public void retrieveGroupTest(final String id) throws IOException {

		response = client.retrieveGroup(id, MediaType.APPLICATION_JSON);
		System.out.println("retrieveGroupTest + responseStr = "  + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Could not get the group, status != 200");
	}

	@Test
	public void retrieveAllGroupsTest() throws IOException {

		response = client.retrieveAllGroups();
		System.out.println("response body = "  + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Could not get a list of all groups, status != 200");
	}

    @Test
    public void testSearchGroupsPost() throws IOException {

        String filter = "id pr";
        response = client.searchGroupsPost(filter, 1, 10, "", "", null);
        System.out.println("response body = "  + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "Could not get the user, status != 200");
    }
}
