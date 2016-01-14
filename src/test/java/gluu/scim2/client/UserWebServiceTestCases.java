package gluu.scim2.client;

import static org.testng.Assert.assertEquals;
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
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.Email;
import org.gluu.oxtrust.model.scim2.PhoneNumber;
import org.gluu.oxtrust.model.scim2.User;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Shekhar Laad 
 */
public class UserWebServiceTestCases {	

	User personToAdd;
	User personToUpdate;
	User userAdd;
	User userToUpdate;
	
	String uid;//"@!90AF.4554.38D5.8D7B!0001!12A8.BB2E!0000!607F.2973";
	Scim2Client client;
	ScimResponse response;
	String username = "scimClientTestUser5";

	
	public void init1(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, final String umaAatClientKeyId) throws IOException {
		String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
		response = null;
		//List<String> schema = new ArrayList<String>();
		//schema.add("urn:scim:schemas:core:2.0");
		//schema.add("urn:ietf:params:scim:api:messages:2.0:ListResponse");
		personToAdd = new User();
		personToUpdate = new User();
		//personToAdd.setSchemas(schema);
		personToAdd.setUserName(username);
		personToAdd.setPassword("test");
		personToAdd.setDisplayName("scimClientTestPerson");
		Email email = new Email();
		email.setValue("scim@gluu.org");
		email.setType(org.gluu.oxtrust.model.scim2.Email.Type.WORK);
		email.setPrimary(true);
		personToAdd.getEmails().add(email);
		PhoneNumber phone = new PhoneNumber();
		phone.setType(org.gluu.oxtrust.model.scim2.PhoneNumber.Type.WORK);
		phone.setValue("654-6509-263");
		personToAdd.getPhoneNumbers().add(phone);
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
		
		personToAdd.getAddresses().add(address);
		personToAdd.setPreferredLanguage("US_en");
		org.gluu.oxtrust.model.scim2.Name name = new  org.gluu.oxtrust.model.scim2.Name();		
		name.setFamilyName("SCIM");//getName().setFamilyName("SCIM");
		name.setGivenName("SCIM");
		personToAdd.setName(name);

		personToUpdate = personToAdd;
		personToUpdate.setDisplayName("SCIM");

	}	
	
	
	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, final String umaAatClientKeyId) throws IOException {
		String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
		response = null;
		//person = null;
		List<String> schema = new ArrayList<String>();
		//schema.add("urn:scim:schemas:core:2.0");
		//schema.add("urn:ietf:params:scim:api:messages:2.0:ListResponse");
		userAdd = new User();
		userToUpdate = new User();
		//personToAdd.setSchemas(schema);
		userAdd.setUserName("scimClientTestPerson19");
		userAdd.setPassword("test");
		userAdd.setDisplayName("scimClientTestPerson6");
		Email email = new Email();
		//org.gluu.oxtrust.model.scim2.Email.Type emailType = new org.gluu.oxtrust.model.scim2.Email.Type("Work");
		email.setValue("scim@gluu.org");
		email.setType(org.gluu.oxtrust.model.scim2.Email.Type.WORK);
		email.setPrimary(true);
		userAdd.getEmails().add(email);
		PhoneNumber phone = new PhoneNumber();
		//org.gluu.oxtrust.model.scim2.PhoneNumber.Type phoneType = new org.gluu.oxtrust.model.scim2.PhoneNumber.Type("Work");
		
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
		userToUpdate.setDisplayName("SCIM");
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(userAdd);
		System.out.println("jsonInString   :  "+jsonInString);
		
		//ObjectMapper mapper1 = new ObjectMapper();
		//String jsonInString = "{'name' : 'mkyong'}";

		//JSON from String to Object
		//User userDeser = mapper1.readValue(jsonInString, User.class);
		//System.out.println("userDeser   :  "+userDeser.getDisplayName());
		

	}
	
	@Test
	public void retrieveAllPersonsTest() throws HttpException, IOException {

		response = client.retrieveAllPersons(MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases : retrieveAllPersonsTest response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not get a list of all persons, status != 200");
		
	}
	
	@Parameters({ "uid" })
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

		response = client.personSearch("mail", "abc123@cc.com", MediaType.APPLICATION_JSON);
		System.out.println("UserWebServiceTestCases :personSearchListByAttribute :response " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "unable to retrive person, status != 200");
	}
	
	private Object jsonToObject(String json, Class<?> clazz) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}
}
