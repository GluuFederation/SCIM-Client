package gluu.scim2.client;

import static org.testng.Assert.assertEquals;
import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;
import gluu.scim.client.model.ScimPerson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.Email;
import org.gluu.oxtrust.model.scim2.PhoneNumber;
import org.gluu.oxtrust.model.scim2.User;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Shekhar Laad 
 */
public class UserWebServiceTestCases extends BaseScimTest {	

	User personToAdd;
	User personToUpdate;
	User userAdd;
	User userToUpdate;
	
	String uid;
	Scim2Client client;
	ScimResponse response;
	
	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId","userwebservice.add.username","userwebservice.update.displayname" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId,final String username ,final String updateDisplayName ) throws IOException {
		System.out.println(" username :  "+username +" updateDisplayName :" + updateDisplayName);
		String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
		response = null;
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
		address.setFormatted(address.getStreetAddress() + " " + address.getLocality() + " " + address.getPostalCode() + " " + address.getRegion() + " "
				+ address.getCountry());
		
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
	public void retrieveAllPersonsTest() throws HttpException, IOException {

		response = client.retrieveAllPersons(MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases : retrieveAllPersonsTest response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not get a list of all persons, status != 200");
		
	}
	
	/*@Parameters({ "uid" })
	@Test
	public void retrievePersonTest(final String uid) throws HttpException, IOException {

		response = client.retrievePerson(uid, MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases : retrievePersonTest : response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not get the person, status != 200");
		
	}
	
	@Test(groups = "a")
	public void createPersonTest() throws Exception {
		response = client.createPerson(userAdd, MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases createPersonTest :response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 201, "cold not Add the person, status != 201");
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		User person = (User) jsonToObject(responseStr, User.class);
		this.uid = person.getId();
		System.out.println("create uid  " + uid);

	}

	@Test(groups = "a")
	public void updatePersonTest() throws Exception {

		response = client.updatePerson(userToUpdate, uid, MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases :updatePersonTest: response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not update the person, status != 200");
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		User person = (User) jsonToObject(responseStr, User.class);
		assertEquals(person.getDisplayName(), userToUpdate.getDisplayName(), "could not update the user");
	}

	@Test(dependsOnGroups = "a")
	public void deletePersonTest() throws Exception {

		response = client.deletePerson(this.uid);
		System.out.println("UserWebServiceTestCases :deletePersonTest :response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not delete the person, status != 200");

	}
	
	
	@Test
	public void personSearchByAttribute() throws Exception {

		response = client.personSearch("mail", "abc123@cc.com", MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases :response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "unable to retrive person, status != 200");
	}
	
	@Test
	public void personSearchListByAttribute() throws Exception {

		response = client.searchPersons("mail", "abc123@cc.com", MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases :personSearchListByAttribute :response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "unable to retrive person, status != 200");
	}*/
	
	private Object jsonToObject(String json, Class<?> clazz) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}
}
