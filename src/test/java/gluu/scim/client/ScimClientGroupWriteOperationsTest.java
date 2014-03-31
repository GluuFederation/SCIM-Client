package gluu.scim.client;

import static org.testng.Assert.assertEquals;
import gluu.scim.client.model.ScimGroup;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


/**
 * SCIM Client group Create,Update,Delete tests
 *
 * @author Reda Zerrad Date: 06.01.2012
 */
public class ScimClientGroupWriteOperationsTest {
	
	final String CREATEJSON = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"displayName\":\"Gluu Testing GroupSCIMCLIENT\",\"members\":[{\"value\":\"@!1111!0000!D4E7\",\"display\":\"Micheal Schwartz\"}]}";
	final String UPDATEJSON = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"displayName\":\"Gluu Testing GroupSCIMCLIENTUpdate\",\"members\":[{\"value\":\"@!1111!0000!D4E7\",\"display\":\"Micheal Schwartz\"}]}";
	String id;
	ScimClient client; 
	ScimResponse response;
	ScimGroup group;
	
	@Parameters({"userName","passWord","domainURL","clientID","clientSecret","oxAuthDomain"})
	@BeforeTest
	public void init(final String userName, final String passWord ,final String domainURL,final String clientID,final String clientSecret,final String oxAuthDomain){
     //client = ScimClient.basicInstance(userName,passWord,domainURL);
	 client = ScimClient.oAuthInstance(userName, passWord, clientID,clientSecret, domainURL, oxAuthDomain);
	 response = null;
	 group = null;
	}
	
	@Test(groups = "a")
	public void  createGroupTest() throws Exception{

		response = client.createGroupString(CREATEJSON, MediaType.APPLICATION_JSON);
		
		 assertEquals(response.getStatusCode(),201,"cold not Add the group, status != 201");
		 String responseStr = response.getResponseBodyString();
	     group = (ScimGroup) jsonToObject(responseStr, ScimGroup.class);
	     this.id = group.getId();
	     
	}
	
	@Test(groups = "a")
	public void updateGroupTest() throws Exception{

		response = client.updateGroupString(UPDATEJSON,this.id, MediaType.APPLICATION_JSON);
		
		 assertEquals(response.getStatusCode(),200,"cold not update the group, status != 200");
		   
	}
	
	@Test(dependsOnGroups = "a")
	public void  deleteGroupTest() throws Exception{

		response = client.deleteGroup(this.id);
		
		 assertEquals(response.getStatusCode(),200,"cold not delete the Group, status != 200");
		   
	}
	
	private Object jsonToObject(String json, Class<?> clazz) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}
}
