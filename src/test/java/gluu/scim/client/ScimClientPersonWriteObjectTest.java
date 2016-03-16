package gluu.scim.client;

import static org.testng.Assert.assertEquals;
import gluu.BaseScimTest;
import gluu.scim.client.model.ScimPerson;
import gluu.scim.client.model.ScimPersonAddresses;
import gluu.scim.client.model.ScimPersonEmails;
import gluu.scim.client.model.ScimPersonPhones;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * SCIM Client person Create,Update,Delete as an object tests
 *
 * @author Reda Zerrad Date: 06.05.2012
 */
public class ScimClientPersonWriteObjectTest extends BaseScimTest {

	ScimPerson personToAdd;
	ScimPerson personToUpdate;
	String uid;
	ScimClient client;
	ScimResponse response;
	ScimPerson person;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) {
		try {
			String jwks = FileUtils.readFileToString(new File(umaAatClientJwks));				
			client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaAatClientId, jwks, umaAatClientKeyId);
			response = null;
		
		person = null;
		List<String> schema = new ArrayList<String>();
		schema.add("urn:scim:schemas:core:1.0");
		personToAdd = new ScimPerson();
		personToUpdate = new ScimPerson();
		personToAdd.setSchemas(schema);
		personToAdd.setUserName("scimClientTestPerson");
		personToAdd.setPassword("test");
		personToAdd.setDisplayName("scimClientTestPerson");
		ScimPersonEmails email = new ScimPersonEmails();
		email.setValue("scim@gluu.org");
		email.setType("Work");
		email.setPrimary("true");
		personToAdd.getEmails().add(email);
		ScimPersonPhones phone = new ScimPersonPhones();
		phone.setType("Work");
		phone.setValue("654-6509-263");
		personToAdd.getPhoneNumbers().add(phone);
		ScimPersonAddresses address = new ScimPersonAddresses();
		address.setCountry("US");
		address.setStreetAddress("random street");
		address.setLocality("Austin");
		address.setPostalCode("65672");
		address.setRegion("TX");
		address.setPrimary("true");
		address.setType("Work");
		address.setFormatted(address.getStreetAddress() + " " + address.getLocality() + " " + address.getPostalCode() + " " + address.getRegion() + " "
				+ address.getCountry());
		personToAdd.getAddresses().add(address);
		personToAdd.setPreferredLanguage("US_en");
		personToAdd.getName().setFamilyName("SCIM");
		personToAdd.getName().setGivenName("SCIM");

		personToUpdate = personToAdd;
		personToUpdate.setDisplayName("SCIM");
		} catch (IOException e) {
			System.out.println("exception in reading fle " + e.getMessage());
		}

	}

	@Test(groups = "a")
	public void createPersonTest() throws Exception {

		response = client.createPerson(personToAdd, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 201, "cold not Add the person, status != 201");
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		person = (ScimPerson) jsonToObject(responseStr, ScimPerson.class);
		this.uid = person.getId();

	}

	@Test(groups = "a")
	public void updatePersonTest() throws Exception {

		response = client.updatePerson(personToUpdate, this.uid, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 200, "cold not update the person, status != 200");
	}

	@Test(dependsOnGroups = "a")
	public void deletePersonTest() throws Exception {

		response = client.deletePerson(this.uid);

		assertEquals(response.getStatusCode(), 200, "cold not delete the person, status != 200");

	}

	private Object jsonToObject(String json, Class<?> clazz) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}
}
