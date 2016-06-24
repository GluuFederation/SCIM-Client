/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
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

import gluu.scim2.client.util.Util;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * SCIM Client person Create,Update,Delete as an object tests
 *
 * @author Reda Zerrad Date: 06.05.2012
 * @author Yuriy Movchan Date: 03/17/2016
 */
public class ScimClientPersonWriteObjectTest extends BaseScimTest {

	private ScimClient client;

	private ScimPerson personToAdd;
	private ScimPerson personToUpdate;

	private String uid;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath" , "umaAatClientJksPassword" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws IOException {
						
		client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);

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
		address.setFormatted(address.getStreetAddress() + " " + address.getLocality() + " " + address.getPostalCode() + " " + address.getRegion() + " " + address.getCountry());
		personToAdd.getAddresses().add(address);
		personToAdd.setPreferredLanguage("US_en");
		personToAdd.getName().setFamilyName("SCIM");
		personToAdd.getName().setGivenName("SCIM");

		personToUpdate = personToAdd;
		personToUpdate.setDisplayName("SCIM");
	}

	@Test
	public void createPersonTest() throws Exception {

		ScimResponse response = client.createPerson(personToAdd, MediaType.APPLICATION_JSON);
		assertEquals(response.getStatusCode(), 201, "Could not add person, status != 201");

		ScimPerson person = (ScimPerson) Util.jsonToObject(response, ScimPerson.class);
		this.uid = person.getId();
	}

	@Test(dependsOnMethods = "createPersonTest")
	public void updatePersonTest() throws Exception {

		ScimResponse response = client.updatePerson(personToUpdate, this.uid, MediaType.APPLICATION_JSON);
		assertEquals(response.getStatusCode(), 200, "Could not update person, status != 200");
	}

	@Test(dependsOnMethods = "updatePersonTest")
	public void testUpdateUserNameDifferentId() throws Exception {

		System.out.println("IN testUpdateUserNameDifferentId...");

		ScimResponse response = client.retrievePerson(this.uid, MediaType.APPLICATION_JSON);
		System.out.println("response body = " + response.getResponseBodyString());

		Assert.assertEquals(200, response.getStatusCode());

		ScimPerson personRetrieved = (ScimPerson) Util.jsonToObject(response, ScimPerson.class);

		personRetrieved.setUserName("admin");
		personRetrieved.setPassword(null);

		ScimResponse responseUpdated = client.updatePerson(personRetrieved, this.uid, MediaType.APPLICATION_JSON);

		Assert.assertEquals(400, responseUpdated.getStatusCode());
		System.out.println("UPDATED response body = " + responseUpdated.getResponseBodyString());

		System.out.println("LEAVING testUpdateUserNameDifferentId..." + "\n");
	}

	@Test(dependsOnMethods = "testUpdateUserNameDifferentId", alwaysRun = true)
	public void deletePersonTest() throws Exception {

		ScimResponse response = client.deletePerson(this.uid);
		assertEquals(response.getStatusCode(), 200, "Could not delete person, status != 200");
	}
}
