package gluu.scim.client;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import gluu.BaseScimTest;
import gluu.scim.client.model.ScimPerson;
import gluu.scim2.client.Scim2Client;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class ScimClientPersonWriteOperationsUmaTest extends BaseScimTest{

	String CREATEJSON = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"externalId\":\"scimclient\",\"userName\":\"json\",\"name\":{\"givenName\":\"json\",\"familyName\":\"json\",\"honorificPrefix\":\"N/A\",\"honorificSuffix\":\"N/A\"},\"displayName\":\"json json\",\"nickName\":\"json\",\"profileUrl\":\"http://www.gluu.org/\",\"emails\":[{\"value\":\"json@gluu.org\",\"type\":\"work\",\"primary\":\"true\"},{\"value\":\"json2@gluu.org\",\"type\":\"home\",\"primary\":\"false\"}],\"addresses\":[{\"type\":\"work\",\"streetAddress\":\"621 East 6th Street Suite 200\",\"locality\":\"Austin\",\"region\":\"TX\",\"postalCode\":\"78701\",\"country\":\"US\",\"formatted\":\"621 East 6th Street Suite 200  Austin , TX 78701 US\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"646-345-2346\",\"type\":\"work\"}],\"ims\":[{\"value\":\"nynytest_user\",\"type\":\"Skype\"}],\"photos\":[{\"value\":\"http://www.gluu.org/wp-content/themes/SaaS-II/images/logo.png\",\"type\":\"gluu photo\"}],\"userType\":\"CEO\",\"title\":\"CEO\",\"preferredLanguage\":\"en-us\",\"locale\":\"en_US\",\"timezone\":\"America/Chicago\",\"active\":\"true\",\"password\":\"secret\",\"groups\":[{\"display\":\"Gluu Test Group\",\"value\":\"<test_group_inum>\"},{\"display\":\"Gluu Test Group 2\",\"value\":\"<test_group2_inum>\"}],\"roles\":[{\"value\":\"Owner\"}],\"entitlements\":[{\"value\":\"full access\"}],\"x509Certificates\":[{\"value\":\"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFa MH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bc1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5iPSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQG3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAUdGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pKfTiDz1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1+GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"}],\"meta\":{\"created\":\"2010-01-23T04:56:22Z\",\"lastModified\":\"2011-05-13T04:42:34Z\",\"version\":\"aversion\",\"location\":\"http://localhost:8080/oxTrust/seam/resource/restv1/Users/@!1111!0000!D4E7\"}}";
	String UPDATEJSON = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"externalId\":\"scimclient\",\"userName\":\"scimclient\",\"name\":{\"givenName\":\"scimclient\",\"familyName\":\"scimclient\",\"middleName\":\"N/A\",\"honorificPrefix\":\"N/A\",\"honorificSuffix\":\"N/A\"},\"displayName\":\"scimclient\",\"nickName\":\"scimclient\",\"profileUrl\":\"http://www.gluu.org/\",\"emails\":[{\"value\":\"scimclient@gluu.org\",\"type\":\"work\",\"primary\":\"true\"},{\"value\":\"scimclient2@gluu.org\",\"type\":\"home\",\"primary\":\"false\"}],\"addresses\":[{\"type\":\"work\",\"streetAddress\":\"621 East 6th Street Suite 200\",\"locality\":\"Austin\",\"region\":\"TX\",\"postalCode\":\"78701\",\"country\":\"US\",\"formatted\":\"621 East 6th Street Suite 200  Austin , TX 78701 US\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"646-345-2346\",\"type\":\"work\"}],\"ims\":[{\"value\":\"nynytest_user\",\"type\":\"Skype\"}],\"photos\":[{\"value\":\"http://www.gluu.org/wp-content/themes/SaaS-II/images/logo.png\",\"type\":\"gluu photo\"}],\"userType\":\"CEO\",\"title\":\"CEO\",\"preferredLanguage\":\"en-us\",\"locale\":\"en_US\",\"timezone\":\"America/Chicago\",\"active\":\"true\",\"password\":\"secret\",\"active\":\"true\",\"groups\":[{\"display\":\"Gluu Test Group\",\"value\":\"<test_group_inum>\"},{\"display\":\"Gluu Test Group 2\",\"value\":\"<test_group2_inum>\"}],\"roles\":[{\"value\":\"Owner\"}],\"entitlements\":[{\"value\":\"full access\"}],\"x509Certificates\":[{\"value\":\"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFa MH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bc1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5iPSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQG3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAUdGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pKfTiDz1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1+GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"}],\"meta\":{\"created\":\"2010-01-23T04:56:22Z\",\"lastModified\":\"2011-05-13T04:42:34Z\",\"version\":\"aversion\",\"location\":\"http://localhost:8080/oxTrust/seam/resource/restv1/Users/@!1111!0000!D4E7\"}}";

	protected String uid;
	protected ScimClient client;
	protected ScimResponse response;
	protected ScimPerson person;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId", "group1Inum", "group2Inum" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId, final String group1Inum, final String group2Inum) throws IOException {
		String jwks = FileUtils.readFileToString(new File(umaAatClientJwks));
		this.client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaAatClientId, jwks, umaAatClientKeyId);

		this.CREATEJSON = CREATEJSON.replaceAll("<test_group_inum>", group1Inum).replaceAll("<test_group2_inum>", group2Inum);
		this.UPDATEJSON = UPDATEJSON.replaceAll("<test_group_inum>", group1Inum).replaceAll("<test_group2_inum>", group2Inum);
	}

	@Test(groups = "a")
	public void createPersonTest() throws Exception {
		response = client.createPersonString(CREATEJSON, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 201, "cold not Add the person, status != 201");
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		person = (ScimPerson) jsonToObject(responseStr, ScimPerson.class);
		this.uid = person.getId();

	}

	@Test(groups = "a", dependsOnMethods = "createPersonTest")
	public void updatePersonTest() throws Exception {
		System.out.println(CREATEJSON);
		System.out.println(UPDATEJSON);
		response = client.updatePersonString(UPDATEJSON, this.uid, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 200, "cold not update the person, status != 200");
	}

	@Test(dependsOnGroups = "a", dependsOnMethods = "updatePersonTest")
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