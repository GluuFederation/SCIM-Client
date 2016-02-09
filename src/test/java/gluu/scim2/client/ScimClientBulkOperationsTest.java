package gluu.scim2.client;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;
import gluu.scim.client.model.ScimBulkResponse;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.BulkResponse;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * SCIM Client Bulk operation test
 *
 * @author Reda Zerrad Date: 06.02.2012
 */
public class ScimClientBulkOperationsTest  extends BaseScimTest{
	String REQUESTJSON ;
	
	Scim2Client client;
	ScimResponse response;
	BulkResponse BulkResponse;
	String uid;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId" , "ScimClientBulkOperationsTest.username" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, final String umaAatClientKeyId , final String username) throws IOException {
		System.out.println("username :"+username);
		REQUESTJSON = "{\"schemas\": [\"urn:ietf:params:scim:api:messages:2.0:BulkRequest\"],\"failOnErrors\": \"11\",\"operations\": [{\"bulkId\": \"userbulkId\",\"version\": \"aversion\",\"method\": \"POST\",\"path\": \"/Users\",\"location\": \"/Users/@!90AF.4554.38D5.8D7B!0001!12A8.BB2E!0000!1B09.989E\",\"data\": {\"externalId\": \"scimclient\",\"userName\": \""+username+"\",\"meta\": {\"created\": \"2010-01-23T04:56:22Z\",\"lastModified\": \"2011-05-13T04:42:34Z\",\"version\": \"aversion\",\"location\": \"/Users/@!90AF.4554.38D5.8D7B!0001!12A8.BB2E!0000!1B09.989E\"},\"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\"name\": {\"givenName\": \"json\",\"familyName\": \"json\",\"middleName\": \"N/A\",\"honorificPrefix\": \"N/A\",\"honorificSuffix\": \"N/A\"},\"displayName\": \"displaye name\",\"nickName\": \""+username+"\",\"profileUrl\": \"http://www.gluu.org/\",\"userType\": \"CEO\",\"title\": \"CEO\",\"preferredLanguage\": \"en-us\",\"locale\": \"en_US\",\"active\": \"true\",\"password\": \"secret\",\"emails\": [{\"value\": \"json@gluu.org\",\"type\": \"work\",\"primary\": \"true\"},{\"value\": \"json2@gluu.org\",\"type\": \"home\",\"primary\": \"false\"}],\"phoneNumbers\": [{\"value\": \"646-345-2346\",\"type\": \"work\"}],\"ims\": [{\"value\": \"nynytest_user\",\"type\": \"Skype\"}],\"photos\": [],\"addresses\": [{\"type\": \"work\",\"streetAddress\": \"621 East 6th Street Suite 200\",\"locality\": \"Austin\",\"region\": \"TX\",\"postalCode\": \"78701\",\"country\": \"US\",\"formatted\": \"621 East 6th Street Suite 200  Austin , TX 78701 US\",\"primary\": \"true\"}],\"groups\": [{\"display\": \"Gluu Manager Group\",\"value\": \"@!90AF.4554.38D5.8D7B!0001!12A8.BB2E!0003!4742.FF5D\"}],\"roles\": [{\"value\": \"Owner\"}],\"entitlements\": [{\"value\": \"full access\"}],\"x509Certificates\": [{\"value\": \"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFa MH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bc1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5iPSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQG3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAUdGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pKfTiDz1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1+GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"}],\"extensions\": {}}},{\"bulkId\": \"groupbulkId\",\"method\": \"PUT\",\"path\": \"/Users/@!90AF.4554.38D5.8D7B!0001!12A8.BB2E!0000!62BF.1C3C\",\"version\": \"oneversion\",\"data\": {\"externalId\": \"externalId\",\"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\"name\": {\"givenName\": \"json\",\"familyName\": \"json\",\"honorificPrefix\": \"N/A\",\"honorificSuffix\": \"N/A\"},\"displayName\": \"groupdisplayName\"}}]}";
		String jwks = FileUtils.readFileToString(new File(umaAatClientJwks));;
		client = Scim2Client.umaInstance(domain, umaMetaDataUrl, umaAatClientId, jwks, umaAatClientKeyId);
		response = null;
		BulkResponse = null;
	}

	@Test(groups = "a")
	public void bulkOperationTest() throws Exception {
		System.out.println("REQUESTJSON   :  "+ REQUESTJSON);
		response = client.bulkOperationString(REQUESTJSON, MediaType.APPLICATION_JSON);
		System.out.println("reponse : " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not Add the person, status != 200");
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		BulkResponse = (BulkResponse) jsonToObject(responseStr, BulkResponse.class);
		String location = BulkResponse.getOperations().get(0).getLocation();
		this.uid = getUID(location.split("/"));

	}

	@Test(dependsOnGroups = "a")
	public void cleanUp() throws Exception {
		System.out.println("delete " + this.uid);
		response = client.deletePerson(this.uid);
		assertEquals(response.getStatusCode(), 200, "cold not delete the person, status != 200");
		System.out.println("reponse : " + response.getResponseBodyString());
	}

	private String getUID(String[] strings) {
		for (String str : strings) {
			if (str.startsWith("@")) {
				return str;
			}
		}

		return null;
	}

	private Object jsonToObject(String json, Class<?> clazz) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}

}
