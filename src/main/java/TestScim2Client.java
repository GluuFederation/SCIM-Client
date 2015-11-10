import gluu.scim.client.ScimResponse;
import gluu.scim2.client.Scim2Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.gluu.oxtrust.model.scim2.Address;
import org.gluu.oxtrust.model.scim2.BulkOperation;
import org.gluu.oxtrust.model.scim2.BulkRequest;
import org.gluu.oxtrust.model.scim2.Email;
import org.gluu.oxtrust.model.scim2.Group;
import org.gluu.oxtrust.model.scim2.Name;
import org.gluu.oxtrust.model.scim2.Role;
import org.gluu.oxtrust.model.scim2.User;
import org.xdi.oxauth.client.RegisterClient;
import org.xdi.oxauth.client.RegisterRequest;
import org.xdi.oxauth.client.RegisterResponse;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.SubjectType;
import org.xdi.oxauth.model.crypto.signature.SignatureAlgorithm;
import org.xdi.oxauth.model.register.ApplicationType;
import org.xdi.oxauth.model.util.StringUtils;

public class TestScim2Client {

	private static final String USER_NAME = "admin";
	private static final String PASSWORD = "pwd";
	/*
    "client_id": "@!0035.934F.1A51.77B0!0001!402D.66D0!0008!8500.F76D",
    "client_secret": "3ca2854c-69e9-41e0-9068-b1a0edaaf11f",
	 */
	static String userInum = "@!0035.934F.1A51.77B0!0001!402D.66D0!0000!B17F.1FC8";
	static String clientInum = "@!0035.934F.1A51.77B0!0001!402D.66D0!0008!0C74.90D3";
	static String clientSecret = "5ada7186-5cc9-4a61-9f73-eab15218dcc2";
	static Scim2Client scimClient ;
	public static void main(String[] args) {

		createClient();
//		registerClient("My Application","scim client");		
//		retrieveServiceProviderConfig();
		retrieveResourceTypes();

//		 getPerson();
//		 getAllPersons();
		//getAllGroups();
//		 bulkOperation();
		//deleteGroup();
		//updateGroup();
		//getGroup();
		//createGroup();
		 //deletePerson();
		// createPerson();
		// updatePerson();
		
	}
	
	private static void createClient(){
        scimClient = Scim2Client.umaInstance("domain", "umaMetaDataUrl", clientInum, clientSecret);
	}
	private static void retrieveServiceProviderConfig() {
		
		try {
			ScimResponse response1 = scimClient.retrieveServiceProviderConfig(MediaType.APPLICATION_JSON);
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void retrieveResourceTypes() {
		
		try {
			ScimResponse response1 = scimClient.retrieveResourceTypes(MediaType.APPLICATION_JSON);
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void bulkOperation() {
		
		try {
			BulkRequest bulkRequest = new BulkRequest();
			User user=new User();
			user.setDisplayName("Umar Gul");
			
			Name name = new Name();
			name.setGivenName("UserGivenName");
			name.setFamilyName("UserFamilyName");
			user.setName(name);

			user.setUserName("Umar");
			user.setTitle("Mr.");
			user.setPassword("rahat");
			user.setActive(true);
			List<Email> emails = new ArrayList<Email>();
			Email email = new Email();
			email.setDisplay("Rahat");
			email.setValue("rahat@gmail.com");
			emails.add(email);

			email = new Email();
			email.setDisplay("Rahat");
			email.setValue("rahat2@gmail.com");
			emails.add(email);

			user.setEmails(emails);
			List<Address> addresses = new ArrayList<Address>();
			Address address = new Address();
			address.setCountry("Pakistan");
			address.setDisplay("Something something");
			address.setFormatted("formatted");
			address.setPostalCode("46000");
			address.setRegion("ABCD");

			addresses.add(address);
			user.setAddresses(addresses);
			List<Role> roles = new ArrayList<Role>();
			Role role = new Role();
			role.setDisplay("ADMIN");
			role.setOperation("Operation");
			role.setValue("ADMIN");
			roles.add(role);
			user.setRoles(roles);
			
			BulkOperation createUser = new BulkOperation();
			createUser.setBulkId("abcd");
			createUser.setMethod("POST");
			createUser.setPath("/Users");
			createUser.setData(user);
			bulkRequest.getOperations().add(createUser);
			
			BulkOperation cerateGroup = new BulkOperation();
			cerateGroup.setBulkId("abcdefg");
			cerateGroup.setMethod("POST");
			cerateGroup.setPath("/Groups");
			Group group = new Group();
			group.setDisplayName("Bulk Group");
			group.setExternalId("externalid");
			cerateGroup.setData(group);
			bulkRequest.getOperations().add(cerateGroup);
			ScimResponse response = scimClient.bulkOperation(bulkRequest, MediaType.APPLICATION_JSON);
			System.out.println(response.getResponseBodyString());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void getPerson() {
		
		try {
			ScimResponse response1 = scimClient.retrievePerson(userInum,
					MediaType.APPLICATION_JSON);
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void getGroup() {
		
		try {
			ScimResponse response1 = scimClient.retrieveGroup("@!0035.934F.1A51.77B0!0001!402D.66D0!0003!4E80.079E",
					MediaType.APPLICATION_JSON);
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void deletePerson() {
		
		try {
			ScimResponse response1 = scimClient
					.deletePerson("@!0035.934F.1A51.77B0!0001!402D.66D0!0000!0F1B.D2FD");
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void getAllPersons() {
		
		try {
			ScimResponse response1 = scimClient
					.retrieveAllPersons(MediaType.APPLICATION_JSON);
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void getAllGroups() {
		
		try {
			ScimResponse response1 = scimClient
					.retrieveAllGroups(MediaType.APPLICATION_JSON);
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void updatePerson() {
		
		try {

			User newUser = new User();

			Name name = new Name();
			name.setGivenName("UserGivenName");
			name.setFamilyName("UserFamilyName");
			newUser.setName(name);

			newUser.setUserName("NewTestUser");
			newUser.setDisplayName("NewTestUserDisplayName");
			newUser.setTitle("TestTitle112233");
			newUser.setPassword("rahat");
			newUser.setActive(true);
			List<Email> emails = new ArrayList<Email>();
			Email email = new Email();
			email.setDisplay("Rahat");
			email.setValue("rahat.jaan@gmail.com");
			emails.add(email);

			email = new Email();
			email.setDisplay("Rahat");
			email.setValue("rahatjaan@gmail.com");
			emails.add(email);

			newUser.setEmails(emails);
			List<Address> addresses = new ArrayList<Address>();
			Address address = new Address();
			address.setCountry("Pakistan");
			address.setDisplay("Something something");
			address.setFormatted("formatted");
			address.setPostalCode("46000");
			address.setRegion("ABCD");

			addresses.add(address);
			newUser.setAddresses(addresses);
			List<Role> roles = new ArrayList<Role>();
			Role role = new Role();
			role.setDisplay("ADMIN");
			role.setOperation("Operation");
			role.setValue("ADMIN");
			roles.add(role);
			newUser.setRoles(roles);

			ScimResponse response1 = scimClient.updatePerson(newUser,
					"@!0035.934F.1A51.77B0!0001!402D.66D0!0000!B865.F744",
					MediaType.APPLICATION_JSON);
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void createPerson() {
		try {

			User newUser = new User();
			Name name = new Name();
			name.setGivenName("Anwar");
			name.setFamilyName("Ali");
			newUser.setName(name);

			newUser.setUserName("AnwarAli");
			newUser.setDisplayName("Anwar Ali");
			newUser.setTitle("Mr");
			newUser.setPassword("rahat");
			newUser.setActive(true);
			List<Email> emails = new ArrayList<Email>();
			Email email = new Email();
			email.setDisplay("Rahat");
			email.setValue("rahat.jaan@gmail.com");
			emails.add(email);

			email = new Email();
			email.setDisplay("Rahat");
			email.setValue("rahatjaan@gmail.com");
			emails.add(email);

			newUser.setEmails(emails);
			List<Address> addresses = new ArrayList<Address>();
			Address address = new Address();
			address.setCountry("Pakistan");
			address.setDisplay("Something something");
			address.setFormatted("formatted");
			address.setPostalCode("46000");
			address.setRegion("ABCD");

			addresses.add(address);
			newUser.setAddresses(addresses);
			List<Role> roles = new ArrayList<Role>();
			Role role = new Role();
			role.setDisplay("ADMIN");
			role.setOperation("Operation");
			role.setValue("ADMIN");
			roles.add(role);
			newUser.setRoles(roles);
			ScimResponse response1 = scimClient.createPerson(newUser,
					MediaType.APPLICATION_JSON);
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void createGroup() {
		try {

			Group newGroup = new Group();
			newGroup.setDisplayName("NewTestGroup2");			
			ScimResponse response1 = scimClient.createGroup(newGroup,
					MediaType.APPLICATION_JSON);
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void updateGroup() {
		try {

			Group newGroup = new Group();
			newGroup.setDisplayName("NewTestGroup112233");

			ScimResponse response1 = scimClient.updateGroup(newGroup,
					"@!0035.934F.1A51.77B0!0001!402D.66D0!0003!4E80.079E",
					MediaType.APPLICATION_JSON);
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void deleteGroup() {

		try {
			ScimResponse response1 = scimClient
					.deleteGroup("@!0035.934F.1A51.77B0!0001!402D.66D0!0003!4E80.079E");
			System.out.println("response status:" + response1.getStatus());
			System.out.println(response1.getResponseBodyString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void registerClient(String applicationName, String displayName) {
		// Parameters
		String registrationEndpoint = "http://localhost:8085/oxauth/seam/resource/restv1/oxauth/register";
		String redirectUris = "https://gluu.org"; // List of space-separated
													// redirect URIs
		String scopes = "openid user_name profile address email"; // List of
																	// space-separated
																	// scopes
		RegisterRequest registerRequest = new RegisterRequest(
				ApplicationType.WEB, applicationName,
				StringUtils.spaceSeparatedToList(redirectUris));
		registerRequest.setContacts(Arrays.asList("rahat@gluu.org",
				"rahat@gmail.com"));
		registerRequest
				.setLogoUri("http://www.gluu.org/wp-content/themes/gluursn/images/logo.png");
		registerRequest
				.setTokenEndpointAuthMethod(AuthenticationMethod.CLIENT_SECRET_BASIC);
		registerRequest.setPolicyUri("http://www.gluu.org/policy");
		registerRequest.setJwksUri("http://www.gluu.org/jwks");
		registerRequest.setSectorIdentifierUri("");
		registerRequest.setSubjectType(SubjectType.PUBLIC);
		registerRequest.setRequestObjectSigningAlg(SignatureAlgorithm.RS256);
		registerRequest.setRequestUris(Arrays
				.asList("http://www.gluu.org/request"));
		registerRequest.setClientName(displayName);
		registerRequest.setClientUri("http://www.gluu.org/request");
		registerRequest
				.setAuthenticationMethod(AuthenticationMethod.CLIENT_SECRET_JWT);
		registerRequest.setDefaultMaxAge(999999999);
		// Call the service
		RegisterClient registerClient = new RegisterClient(registrationEndpoint);
		registerClient.setRequest(registerRequest);
		RegisterResponse response = registerClient.exec();

		// Successful response
		int status = response.getStatus(); // 200 if succeed
		String clientId = response.getClientId(); // The client's identifier
													// INUM
		String clientSecret = response.getClientSecret(); // The client's
															// password
		String registrationAccessToken = response.getRegistrationAccessToken();
		String registrationClientUri = response.getRegistrationClientUri();
		Date clientSecretExpiresAt = response.getClientSecretExpiresAt();
		System.out.println("clientId:" + clientId);
		System.out.println("clientSecret:" + clientSecret);
		System.out
				.println("registrationAccessToken:" + registrationAccessToken);
		System.out.println("status:" + status);
		System.out.println("response:" + response.getEntity());

		// Error response
		status = response.getStatus(); // HTTP error code
		String description = response.getErrorDescription(); //
	}
}