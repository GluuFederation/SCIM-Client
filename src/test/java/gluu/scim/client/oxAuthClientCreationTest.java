package gluu.scim.client;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import gluu.scim.client.model.CreationResult;
import gluu.scim.client.util.OxAuthClientCreator;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
* oxAuthClientCreationTest
*
* @author Reda Zerrad Date: 06.04.2012
*/
public class oxAuthClientCreationTest {

	private static String redirectUris = "https://seed.gluu.org/oxTrust/";
	private static String applicationName = "ScimTest" + randomString();
	CreationResult response;
	
	
	
	@BeforeTest
	public void init(){
		response = null;
	}
	
	@Parameters({"registerUrl"})
	@Test
	public void oxAuthclientRegistrationTest(String registerUrl){
		
		response = OxAuthClientCreator.create( applicationName, registerUrl, redirectUris);
		assertEquals(response.getStatus(), 200, "Unexpected response code: " + response.getEntity());
        assertNotNull(response.getClientId(), "Unexpected result: clientId is null");
        assertNotNull(response.getClientSecret(), "Unexpected result: clientSecret is null");
        assertNotNull(response.getExpiresAt(), "Unexpected result: expiresAt is null");
        
	}
	
	public static String randomString()
	  {
		SecureRandom random = new SecureRandom();

	    return new BigInteger(130, random).toString(32);
	  }

	
}
