package gluu.scim2.client;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.User;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class ScimClientPersonWriteOperationsBaseTest  extends BaseScimTest{

	String UPDATEJSON = "";
	String CREATEJSON = "";
	protected String uid ;
	protected Scim2Client client;
	protected ScimResponse response;
	protected User person;
	
	String updateGivenName;
	
	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId" , "userjson.add.username" , "userjson.update.givenname", "id"})
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId ,  String username,  String updateGivenName, final String groupName) throws IOException {
		
		System.out.println(" String username : "+username+ " updateGivenName :"+updateGivenName);
		this.updateGivenName=updateGivenName;
		UPDATEJSON = "{\"schemas\":[\"urn:ietf:params:scim:schemas:core:2.0:User\"],\"externalId\":\"scimclient\",\"userName\":\""+username+"\",\"name\":{\"givenName\":\""+updateGivenName+"\",\"familyName\":\"scimclient\",\"honorificPrefix\":\"N/A\",\"honorificSuffix\":\"N/A\"},\"displayName\":\"json json\",\"nickName\":\"json2\",\"profileUrl\":\"http://www.gluu.org/\",\"emails\":[{\"value\":\"json@gluu.org\",\"type\":\"work\",\"primary\":\"true\"},{\"value\":\"json2@gluu.org\",\"type\":\"home\",\"primary\":\"false\"}],\"addresses\":[{\"type\":\"work\",\"streetAddress\":\"621 East 6th Street Suite 200\",\"locality\":\"Austin\",\"region\":\"TX\",\"postalCode\":\"78701\",\"country\":\"US\",\"formatted\":\"621 East 6th Street Suite 200  Austin , TX 78701 US\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"646-345-2346\",\"type\":\"work\"}],\"ims\":[{\"value\":\"nynytest_user\",\"type\":\"Skype\"}],\"userType\":\"CEO\",\"title\":\"CEO\",\"preferredLanguage\":\"en-us\",\"active\":\"true\",\"password\":\"secret\",\"roles\":[{\"value\":\"Owner\"}],\"entitlements\":[{\"value\":\"full access\"}],\"x509Certificates\":[{\"value\":\"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFa MH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bc1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5iPSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQG3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAUdGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pKfTiDz1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1+GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"}],\"meta\":{\"created\":\"2010-01-23T04:56:22Z\",\"lastModified\":\"2011-05-13T04:42:34Z\",\"version\":\"aversion\",\"location\":\"http://localhost:8080/oxTrust/seam/resource/restv1/Users/8c4b6c26-efaf-4840-bddf-c0146a8eb2a9\"}}";
		CREATEJSON = "{\"schemas\":[\"urn:ietf:params:scim:schemas:core:2.0:User\"],\"externalId\":\"scimclient\",\"userName\":\""+username+"\",\"name\":{\"givenName\":\"json\",\"familyName\":\"json\",\"middleName\":\"N/A\",\"honorificPrefix\":\"N/A\",\"honorificSuffix\":\"N/A\"},\"displayName\":\"json json\",\"nickName\":\"json\",\"profileUrl\":\"http://www.gluu.org/\",\"emails\":[{\"value\":\"json@gluu.org\",\"type\":\"work\",\"primary\":\"true\"},{\"value\":\"json2@gluu.org\",\"type\":\"home\",\"primary\":\"false\"}],\"addresses\":[{\"type\":\"work\",\"streetAddress\":\"621 East 6th Street Suite 200\",\"locality\":\"Austin\",\"region\":\"TX\",\"postalCode\":\"78701\",\"country\":\"US\",\"formatted\":\"621 East 6th Street Suite 200  Austin , TX 78701 US\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"646-345-2346\",\"type\":\"work\"}],\"ims\":[{\"value\":\"nynytest_user\",\"type\":\"Skype\"}],\"userType\":\"CEO\",\"title\":\"CEO\",\"preferredLanguage\":\"en-us\",\"locale\":\"en_US\",\"active\":\"true\",\"password\":\"secret\",\"groups\":[{\"display\":\"Gluu Test Group\",\"value\":\"<test_group_inum>\"}],\"roles\":[{\"value\":\"Owner\"}],\"entitlements\":[{\"value\":\"full access\"}],\"x509Certificates\":[{\"value\":\"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFa MH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bc1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5iPSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQG3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAUdGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pKfTiDz1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1+GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"}],\"meta\":{\"created\":\"2010-01-23T04:56:22Z\",\"lastModified\":\"2011-05-13T04:42:34Z\",\"version\":\"aversion\",\"location\":\"http://localhost:8080/identity/seam/resource/restv1/Users/8c4b6c26-efaf-4840-bddf-c0146a8eb2a9\"}}";
		CREATEJSON = CREATEJSON.replaceAll("<test_group_inum>", groupName);
		System.out.println("UPDATEJSON" + UPDATEJSON);
		System.out.println("CREATEJSON" + CREATEJSON);
		String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
		response = null;
		person = null;
	}

	public ScimClientPersonWriteOperationsBaseTest() {
		super();
	}

	@Test(groups = "a")
	public void createPersonTest()  {
		try{
		System.out.println("create json" + CREATEJSON);
		response = client.createPersonString(CREATEJSON, MediaType.APPLICATION_JSON);
		System.out.println(" createPersonTest  :   " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 201, "cold not Add the person, status != 201");
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		person = (User) jsonToObject(responseStr, User.class);
		this.uid = person.getId();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test(groups = "a")
	public void updatePersonTest() throws Exception {
		response = client.updatePersonString(UPDATEJSON, this.uid, MediaType.APPLICATION_JSON);
		System.out.println("updatePersonTest response json" + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not update the person, status != 200");
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		person = (User) jsonToObject(responseStr, User.class);
		assertEquals(person.getName().getGivenName(), updateGivenName, "could not update the user");
	}

	@Test(dependsOnGroups = "a")
	public void deletePersonTest() throws Exception {
		response = client.deletePerson(this.uid);
		System.out.println("deletePersonTest response json" + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not delete the person, status != 200");
	}

	private Object jsonToObject(String json, Class<?> clazz) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}
}