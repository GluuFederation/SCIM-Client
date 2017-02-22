/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.Email;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.PhoneNumber;
import org.gluu.oxtrust.model.scim2.User;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Shekhar Laad 
 */
public class UserWebServiceTestCases extends BaseScimTest {	

	User userAdd;
	User userToUpdate;
	
	String id;
	ScimClient client;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath" , "umaAatClientJksPassword" , "umaAatClientKeyId","userwebservice.add.username","userwebservice.update.displayname" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId,final String username ,final String updateDisplayName ) throws IOException {

		System.out.println(" username :  "+username +" updateDisplayName :" + updateDisplayName);
		
		client = ScimClientFactory.getClient(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
		userAdd = new User();
		userToUpdate = new User();
		userAdd.setUserName(username);
		userAdd.setPassword("test");
		userAdd.setDisplayName("Scim2DisplayName");
		Email email = new Email();
		email.setValue("scim@gluu.org");
		email.setType(org.gluu.oxtrust.model.scim2.Email.Type.WORK);
		email.setPrimary(true);
		userAdd.getEmails().add(email);
		PhoneNumber phone = new PhoneNumber();		
		phone.setType(org.gluu.oxtrust.model.scim2.PhoneNumber.Type.WORK);
		phone.setValue("654-6509-263");
		userAdd.getPhoneNumbers().add(phone);
		org.gluu.oxtrust.model.scim2.Address address = new org.gluu.oxtrust.model.scim2.Address();
		address.setCountry("US");
		address.setStreetAddress("random street");
		address.setLocality("Austin");
		address.setPostalCode("65672");
		address.setRegion("TX");
		address.setPrimary(true);
		address.setType(org.gluu.oxtrust.model.scim2.Address.Type.WORK);
		address.setFormatted(address.getStreetAddress() + " " + address.getLocality() + " " + address.getPostalCode() + " " + address.getRegion() + " "	+ address.getCountry());
		userAdd.getAddresses().add(address);
		userAdd.setPreferredLanguage("US_en");
		org.gluu.oxtrust.model.scim2.Name name = new  org.gluu.oxtrust.model.scim2.Name();		
		name.setFamilyName("SCIM");//getName().setFamilyName("SCIM");
		name.setGivenName("SCIM");
		userAdd.setName(name);

		userToUpdate = userAdd;
		userToUpdate.setDisplayName(updateDisplayName);
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(userAdd);
		System.out.println("jsonInString   :  "+jsonInString);
	}
	
	@Test
	public void retrieveAllUsersTest() throws IOException {
		BaseClientResponse<ListResponse> response = client.retrieveAllUsers();
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get a list of all users, status != 200");
	}
	
	/*@Parameters({ "userInum" })
	@Test
	public void retrievePersonTest(final String id) throws Exception {

		response = client.retrievePerson(id, MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases : retrievePersonTest : response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Could not get the user, status != 200");
		
	}
	
	@Test
	public void createPersonTest() throws Exception {

		response = client.createPerson(userAdd, MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases createPersonTest :response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 201, "Could not add the user, status != 201");
		User user = Util.toUser(response, client.getUserExtensionSchema());
		this.id = user.getId();
		System.out.println("create id  " + id);

	}

	@Test
	public void updatePersonTest() throws Exception {

		response = client.updatePerson(userToUpdate, id, MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases :updatePersonTest: response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Could not update the user, status != 200");
		User user = Util.toUser(response, client.getUserExtensionSchema());
		assertEquals(user.getDisplayName(), userToUpdate.getDisplayName(), "Could not update the user");
	}

	@Test
	public void deletePersonTest() throws Exception {

		response = client.deletePerson(this.id);
		System.out.println("UserWebServiceTestCases :deletePersonTest :response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Could not delete the user, status != 200");

	}
	
	
	@Test
	public void personSearchByAttribute() throws Exception {

		response = client.personSearch("mail", "abc123@cc.com", MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases :response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Unable to retrieve user, status != 200");
	}
	
	@Test
	public void personSearchListByAttribute() throws Exception {

		response = client.searchPersons("mail", "abc123@cc.com", MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases :personSearchListByAttribute :response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Unable to retrieve user, status != 200");
	}*/
}
