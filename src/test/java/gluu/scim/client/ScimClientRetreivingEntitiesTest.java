package gluu.scim.client;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * SCIM Client tests
 *
 * @author Reda Zerrad Date: 05.24.2012
 */
public class ScimClientRetreivingEntitiesTest {

	ScimClient client; 
	ScimResponse response;
	
	
	@Parameters({"userName","passWord","domainURL","clientID","clientSecret","oxAuthDomain"})
	@BeforeTest
	public void init(final String userName, final String passWord ,final String domainURL,final String clientID,final String clientSecret,final String oxAuthDomain){
     //client = ScimClient.basicInstance(userName,passWord,domainURL);
	 client = ScimClient.oAuthInstance(userName, passWord, clientID,clientSecret, domainURL, oxAuthDomain);
	 response = null;
	}
	
	@Parameters({"uid"})
	@Test
	public void  retrievePersonTest(final String uid) throws HttpException, IOException{

		response = client.retrievePerson(uid, MediaType.APPLICATION_JSON);
		
		 assertEquals(response.getStatusCode(),200,"cold not get the person, status != 200");
	}
	
	
	@Test
	public void  retrieveAllPersonsTest() throws HttpException, IOException{
	
		response = client.retrieveAllPersons( MediaType.APPLICATION_JSON);
		
		 assertEquals(response.getStatusCode(),200,"cold not get a list of all persons, status != 200");
	}
	@Parameters({"id"})
	@Test
	public void  retrieveGroupTest(final String id) throws HttpException, IOException{
		
		response = client.retrieveGroup(id, MediaType.APPLICATION_JSON);
		
		 assertEquals(response.getStatusCode(),200,"cold not get the group, status != 200");
	}
	
	
	@Test
	public void  retrieveAllGroupsTest() throws HttpException, IOException{

		response = client.retrieveAllGroups( MediaType.APPLICATION_JSON);
		
		 assertEquals(response.getStatusCode(),200,"cold not get a list of all groups, status != 200");
	}
}
