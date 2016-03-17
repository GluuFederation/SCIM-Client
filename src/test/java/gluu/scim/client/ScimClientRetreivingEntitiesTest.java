package gluu.scim.client;

import static org.testng.Assert.assertEquals;
import gluu.BaseScimTest;

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
 * SCIM Client tests
 *
 * @author Reda Zerrad Date: 05.24.2012
 * @author Yuriy Movchan Date: 03/17/2016
 */
public class ScimClientRetreivingEntitiesTest extends BaseScimTest{

	private ScimClient client;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws IOException {
		String jwks = FileUtils.readFileToString(new File(umaAatClientJwks));				
		client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaAatClientId, jwks, umaAatClientKeyId);
	}

	@Parameters({ "userInum" })
	@Test
	public void retrievePersonTest(final String uid) throws HttpException, IOException {
		ScimResponse response = client.retrievePerson(uid, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 200, "cold not get the person, status != 200");
	}

	@Test
	public void retrieveAllPersonsTest() throws HttpException, IOException {
		ScimResponse response = client.retrieveAllPersons(MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 200, "cold not get a list of all persons, status != 200");
	}

	@Parameters({ "group1Inum" })
	@Test
	public void retrieveGroupTest(final String group1Inum) throws HttpException, IOException {
		ScimResponse response = client.retrieveGroup(group1Inum, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 200, "cold not get the group, status != 200");
	}

	@Test
	public void retrieveAllGroupsTest() throws HttpException, IOException {
		ScimResponse response = client.retrieveAllGroups(MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 200, "cold not get a list of all groups, status != 200");
	}
}
