package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;
import gluu.scim.client.util.ResponseMapper;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.schema.AttributeHolder;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import java.io.File;

import static org.testng.Assert.assertEquals;

/**
 * README:
 *
 * Before running this test, you first need to manually add three custom attributes in oxTrust:
 * 1. customFirst - Text, not multi-valued
 * 2. customSecond - Date, multi-valued
 * 3. customThird - Numeric, not multi-valued
 * You also need to set them as "SCIM Attribute = True".
 *
 * @author Val Pecaoco
 */
public class UserExtensionsJsonTest extends BaseScimTest {

    String domainURL;
    String uid;
    Scim2Client client;
    User person;

    String username = "userjson.add.username";
    String updateGivenName = "userjson.update.givenname";
    String CREATEJSON = "{\"schemas\":[\"urn:ietf:params:scim:schemas:core:2.0:User\",\"urn:ietf:params:scim:schemas:extension:gluu:2.0:User\"],\"urn:ietf:params:scim:schemas:extension:gluu:2.0:User\": {\"customFirst\":\"[1000,2000]\",\"customSecond\":[\"2016-02-23T15:35:22Z\"],\"customThird\":3000},\"externalId\":\"scimclient\",\"userName\":\""+username+"\",\"name\":{\"givenName\":\"json\",\"familyName\":\"json\",\"middleName\":\"N/A\",\"honorificPrefix\":\"N/A\",\"honorificSuffix\":\"N/A\"},\"displayName\":\"json json\",\"nickName\":\"json\",\"profileUrl\":\"http://www.gluu.org/\",\"emails\":[{\"value\":\"json@gluu.org\",\"type\":\"work\",\"primary\":\"true\"},{\"value\":\"json2@gluu.org\",\"type\":\"home\",\"primary\":\"false\"}],\"addresses\":[{\"type\":\"work\",\"streetAddress\":\"621 East 6th Street Suite 200\",\"locality\":\"Austin\",\"region\":\"TX\",\"postalCode\":\"78701\",\"country\":\"US\",\"formatted\":\"621 East 6th Street Suite 200  Austin , TX 78701 US\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"646-345-2346\",\"type\":\"work\"}],\"ims\":[{\"value\":\"nynytest_user\",\"type\":\"Skype\"}],\"userType\":\"CEO\",\"title\":\"CEO\",\"preferredLanguage\":\"en-us\",\"locale\":\"en_US\",\"active\":\"true\",\"password\":\"secret\",\"roles\":[{\"value\":\"Owner\"}],\"entitlements\":[{\"value\":\"full access\"}],\"x509Certificates\":[{\"value\":\"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFa MH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bc1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5iPSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQG3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAUdGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pKfTiDz1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1+GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"}],\"meta\":{\"created\":\"2010-01-23T04:56:22Z\",\"lastModified\":\"2011-05-13T04:42:34Z\",\"version\":\"aversion\",\"location\":\"http://localhost:8080/identity/seam/resource/restv1/Users/8c4b6c26-efaf-4840-bddf-c0146a8eb2a9\"}}";
    String UPDATEJSON = "{\"schemas\":[\"urn:ietf:params:scim:schemas:core:2.0:User\",\"urn:ietf:params:scim:schemas:extension:gluu:2.0:User\"],\"urn:ietf:params:scim:schemas:extension:gluu:2.0:User\": {\"customFirst\":\"4000\",\"customSecond\":[\"2016-02-23T03:35:22Z\",\"2016-02-23T05:16:16Z\"],\"customThird\":5000},\"externalId\":\"scimclient\",\"userName\":\""+username+"\",\"name\":{\"givenName\":\""+updateGivenName+"\",\"familyName\":\"scimclient\",\"honorificPrefix\":\"N/A\",\"honorificSuffix\":\"N/A\"},\"displayName\":\"json json\",\"nickName\":\"json2\",\"profileUrl\":\"http://www.gluu.org/\",\"emails\":[{\"value\":\"json@gluu.org\",\"type\":\"work\",\"primary\":\"true\"},{\"value\":\"json2@gluu.org\",\"type\":\"home\",\"primary\":\"false\"}],\"addresses\":[{\"type\":\"work\",\"streetAddress\":\"621 East 6th Street Suite 200\",\"locality\":\"Austin\",\"region\":\"TX\",\"postalCode\":\"78701\",\"country\":\"US\",\"formatted\":\"621 East 6th Street Suite 200  Austin , TX 78701 US\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"646-345-2346\",\"type\":\"work\"}],\"ims\":[{\"value\":\"nynytest_user\",\"type\":\"Skype\"}],\"userType\":\"CEO\",\"title\":\"CEO\",\"preferredLanguage\":\"en-us\",\"active\":\"true\",\"password\":\"secret\",\"roles\":[{\"value\":\"Owner\"}],\"entitlements\":[{\"value\":\"full access\"}],\"x509Certificates\":[{\"value\":\"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFa MH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bc1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5iPSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQG3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAUdGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pKfTiDz1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1+GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"}],\"meta\":{\"created\":\"2010-01-23T04:56:22Z\",\"lastModified\":\"2011-05-13T04:42:34Z\",\"version\":\"aversion\",\"location\":\"http://localhost:8080/oxTrust/seam/resource/restv1/Users/8c4b6c26-efaf-4840-bddf-c0146a8eb2a9\"}}";

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws Exception {

        this.domainURL = domainURL;
        String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
        client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
    }

    @Test(groups = "a")
    public void checkIfExtensionsExist() throws Exception {

        GetMethod get = new GetMethod(this.domainURL + "/scim/v2/Schemas/" + Constants.USER_EXT_SCHEMA_ID);
        get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);

        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(get);

        ScimResponse response = ResponseMapper.map(get, null);

        byte[] bytes = response.getResponseBody();
        String json = new String(bytes);

        UserExtensionSchema userExtensionSchema = (UserExtensionSchema)jsonToObject(json, UserExtensionSchema.class);
        assertEquals(userExtensionSchema.getId(), Constants.USER_EXT_SCHEMA_ID);

        boolean customFirstExists = false;
        boolean customSecondExists = false;
        boolean customThirdExists = false;
        for (AttributeHolder attributeHolder : userExtensionSchema.getAttributes()) {

            if (attributeHolder.getName().equals("customFirst")) {

                customFirstExists = true;
                assert(attributeHolder.getType().equals("string"));
                assert(attributeHolder.getMultiValued().equals(Boolean.FALSE));

            } else if (attributeHolder.getName().equals("customSecond")) {

                customSecondExists = true;
                assert (attributeHolder.getType().equals("dateTime"));
                assert (attributeHolder.getMultiValued().equals(Boolean.TRUE));

            } else if (attributeHolder.getName().equals("customThird")) {

                customThirdExists = true;
                assert(attributeHolder.getType().equals("decimal"));
                assert(attributeHolder.getMultiValued().equals(Boolean.FALSE));
            }
        }
        assertEquals(customFirstExists, true, "Custom attribute \"customFirst\" not found.");
        assertEquals(customSecondExists, true, "Custom attribute \"customSecond\" not found.");
        assertEquals(customThirdExists, true, "Custom attribute \"customThird\" not found.");
    }

    @Test(groups = "b", dependsOnGroups = "a")
    public void createPersonTest() throws Exception {

        ScimResponse response = client.createPersonString(CREATEJSON, MediaType.APPLICATION_JSON);

        System.out.println(" createPersonTest() RESPONSE = " + response.getResponseBodyString());

        assertEquals(response.getStatusCode(), 201, "cold not Add the person, status != 201");

        byte[] bytes = response.getResponseBody();
        String responseStr = new String(bytes);

        person = (User) jsonToObject(responseStr, User.class);
        this.uid = person.getId();
    }

    @Test(groups = "c", dependsOnGroups = "b")
    public void updatePersonTest() throws Exception {

        ScimResponse response = client.updatePersonString(UPDATEJSON, this.uid, MediaType.APPLICATION_JSON);

        System.out.println(" updatePersonTest() RESPONSE = " + response.getResponseBodyString());

        assertEquals(response.getStatusCode(), 200, "cold not update the person, status != 200");

        byte[] bytes = response.getResponseBody();
        String responseStr = new String(bytes);

        person = (User) jsonToObject(responseStr, User.class);
        assertEquals(person.getName().getGivenName(), updateGivenName, "could not update the user");
    }

    @Test(groups = "d", dependsOnGroups = "c")
    public void retrievePersonTest() throws Exception {
        ScimResponse response = client.retrievePerson(this.uid, MediaType.APPLICATION_JSON);
        System.out.println(" retrievePersonTest() RESPONSE = "  + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "cold not get the person, status != 200");
    }

	@Test(dependsOnGroups = "d")
	public void deletePersonTest() throws Exception {
        ScimResponse response = client.deletePerson(this.uid);
		System.out.println(" deletePersonTest() RESPONSE = " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "cold not delete the person, status != 200");
	}

    private Object jsonToObject(String json, Class<?> clazz) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        Object clazzObject = mapper.readValue(json, clazz);
        return clazzObject;
    }

    public UserExtensionsJsonTest() {
        super();
    }
}
