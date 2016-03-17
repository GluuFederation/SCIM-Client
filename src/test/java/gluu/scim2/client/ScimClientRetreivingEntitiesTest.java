package gluu.scim2.client;

import static org.testng.Assert.assertEquals;
import gluu.BaseScimTest;
import gluu.scim.client.ScimClient;
import gluu.scim.client.ScimResponse;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Shekhar Laad 
 */
public class ScimClientRetreivingEntitiesTest  extends BaseScimTest {

	Scim2Client client;
	ScimResponse response;
	
	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws IOException {
		String jwks = FileUtils.readFileToString(new File(umaAatClientJwks));;
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, jwks, umaAatClientKeyId);
		response = null;
	}

	@Parameters({ "userInum" })
	@Test
	public void retrievePersonTest(final String uid) throws HttpException, IOException {

		response = client.retrievePerson(uid, MediaType.APPLICATION_JSON);
		System.out.println("retrievePersonTest + responseStr"  + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not get the person, status != 200");
	}

	@Test
	public void retrieveAllPersonsTest() throws HttpException, IOException {

		response = client.retrieveAllPersons(MediaType.APPLICATION_JSON);
		System.out.println("retrieveAllPersonsTest + responseStr"  + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not get a list of all persons, status != 200");
	}

	@Parameters({ "group1Inum" })
	@Test
	public void retrieveGroupTest(final String group1Inum) throws HttpException, IOException {

		response = client.retrieveGroup(group1Inum, MediaType.APPLICATION_JSON);
		System.out.println("retrieveGroupTest + responseStr"  + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not get the group, status != 200");
	}

	@Test
	public void retrieveAllGroupsTest() throws HttpException, IOException {

		response = client.retrieveAllGroups(MediaType.APPLICATION_JSON);
		System.out.println("retrieveAllGroupsTest + responseStr"  + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not get a list of all groups, status != 200");
	}
}
