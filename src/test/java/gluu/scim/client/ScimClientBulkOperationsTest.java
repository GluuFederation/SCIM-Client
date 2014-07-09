package gluu.scim.client;

import static org.testng.Assert.assertEquals;
import gluu.scim.client.model.ScimBulkResponse;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * SCIM Client Bulk operation test
 *
 * @author Reda Zerrad Date: 06.02.2012
 */
public class ScimClientBulkOperationsTest {
	
	final String REQUESTJSON = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"Operations\":[{\"method\":\"POST\",\"path\":\"/Users\",\"data\":{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"externalId\":\"clientbulk\",\"userName\":\"clientbulk\",\"name\":{\"givenName\":\"clientbulk\",\"familyName\":\"clientbulk\",\"middleName\":\"N/A\",\"honorificPrefix\":\"N/A\",\"honorificSuffix\":\"N/A\"},\"displayName\":\"bulk bulk\",\"nickName\":\"bulk\",\"profileUrl\":\"http://www.gluu.org/\",\"emails\":[{\"value\":\"bulk@gluu.org\",\"type\":\"work\",\"primary\":\"true\"},{\"value\":\"bulk2@gluu.org\",\"type\":\"home\",\"primary\":\"false\"}],\"addresses\":[{\"type\":\"work\",\"streetAddress\":\"621 East 6th Street Suite 200\",\"locality\":\"Austin\",\"region\":\"TX\",\"postalCode\":\"78701\",\"country\":\"US\",\"formatted\":\"621 East 6th Street Suite 200  Austin , TX 78701 US\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"646-345-2346\",\"type\":\"work\"}],\"ims\":[{\"value\":\"nynytest_user\",\"type\":\"Skype\"}],\"photos\":[{\"value\":\"http://www.gluu.org/wp-content/themes/SaaS-II/images/logo.png\",\"type\":\"gluu photo\"}],\"userType\":\"CEO\",\"title\":\"CEO\",\"preferredLanguage\":\"en-us\",\"locale\":\"en_US\",\"timezone\":\"America/Chicago\",\"active\":\"true\",\"password\":\"secret\",\"groups\":[{\"display\":\"Gluu Manager Group\",\"value\":\"@!1111!0003!B2C6\"},{\"display\":\"Gluu Owner Group\",\"value\":\"@!1111!0003!D9B4\"}],\"roles\":[{\"value\":\"Owner\"}],\"entitlements\":[{\"value\":\"full access\"}],\"x509Certificates\":[{\"value\":\"MIIDQzCCAqygAwIBAgICEAAwDQYJKoZIhvcNAQEFBQAwTjELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFDASBgNVBAoMC2V4YW1wbGUuY29tMRQwEgYDVQQDDAtleGFtcGxlLmNvbTAeFw0xMTEwMjIwNjI0MzFaFw0xMjEwMDQwNjI0MzFa MH8xCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRQwEgYDVQQKDAtleGFtcGxlLmNvbTEhMB8GA1UEAwwYTXMuIEJhcmJhcmEgSiBKZW5zZW4gSUlJMSIwIAYJKoZIhvcNAQkBFhNiamVuc2VuQGV4YW1wbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Kr+Dcds/JQ5GwejJFcBIP682X3xpjis56AK02bc1FLgzdLI8auoR+cC9/Vrh5t66HkQIOdA4unHh0AaZ4xL5PhVbXIPMB5vAPKpzz5iPSi8xO8SL7I7SDhcBVJhqVqr3HgllEG6UClDdHO7nkLuwXq8HcISKkbT5WFTVfFZzidPl8HZ7DhXkZIRtJwBweq4bvm3hM1Os7UQH05ZS6cVDgweKNwdLLrT51ikSQG3DYrl+ft781UQRIqxgwqCfXEuDiinPh0kkvIi5jivVu1Z9QiwlYEdRbLJ4zJQBmDrSGTMYn4lRc2HgHO4DqB/bnMVorHB0CC6AV1QoFK4GPe1LwIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU8pD0U0vsZIsaA16lL8En8bx0F/gwHwYDVR0jBBgwFoAUdGeKitcaF7gnzsNwDx708kqaVt0wDQYJKoZIhvcNAQEFBQADgYEAA81SsFnOdYJtNg5Tcq+/ByEDrBgnusx0jloUhByPMEVkoMZ3J7j1ZgI8rAbOkNngX8+pKfTiDz1RC4+dx8oU6Za+4NJXUjlL5CvV6BEYb1+QAEJwitTVvxB/A67g42/vzgAtoRUeDov1+GFiBZ+GNF/cAYKcMtGcrs2i97ZkJMo=\"}],\"meta\":{\"created\":\"2010-01-23T04:56:22Z\",\"lastModified\":\"2011-05-13T04:42:34Z\",\"version\":\"aversion\",\"location\":\"http://localhost:8080/oxTrust/seam/resource/restv1/Users/@!1111!0000!D4E7\"}},\"bulkId\":\"onebunk\"},{\"method\":\"PUT\",\"path\":\"/Users/@!1111!0000!C4C4\", \"version\":\"oneversion\",\"data\":{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"displayName\":\"bulk person\",\"externalId\":\"bulk\"}}]}";
	ScimClient client; 
	ScimResponse response;
	ScimBulkResponse scimBulkResponse;
	String uid;

	@Parameters({"userName","passWord","domainURL","clientID","clientSecret","oxAuthDomain"})
	@BeforeTest
	public void init(final String userName, final String passWord ,final String domainURL,final String clientID,final String clientSecret,final String oxAuthDomain){
	 client = ScimClient.oAuthInstance(userName, passWord, clientID,clientSecret, domainURL, oxAuthDomain);
	 response = null;
	 scimBulkResponse = null;
	}
	
	
	
	@Test(groups = "a")
	public void  bulkOperationTest() throws Exception{

		response = client.bulkOperationString(REQUESTJSON, MediaType.APPLICATION_JSON);
		
		 assertEquals(response.getStatusCode(),200,"cold not Add the person, status != 200");
		 byte[] bytes = response.getResponseBody();
		 String responseStr = new String(bytes);
		 scimBulkResponse = (ScimBulkResponse) jsonToObject(responseStr,ScimBulkResponse.class);		 
		 String location = scimBulkResponse.getOperations().get(0).getLocation();
		 this.uid = getUID(location.split("/"));
	     
	}
	
	@Test(dependsOnGroups = "a")
	public void  cleanUp() throws Exception{

		response = client.deletePerson(this.uid);
		
		 assertEquals(response.getStatusCode(),200,"cold not delete the person, status != 200");
		   
	}

	
	private String getUID(String[] strings) {
        
		for(String str : strings){
			if(str.startsWith("@")){
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
