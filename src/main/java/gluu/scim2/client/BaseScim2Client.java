package gluu.scim2.client;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.gluu.oxtrust.model.scim2.User;

import gluu.scim.client.BaseScimClient;
import gluu.scim.client.ScimResponse;
import gluu.scim.client.model.ScimPerson;

/**
 * BaseClient
 *
 * @author Reda Zerrad Date: 05.28.2012
 */
public interface BaseScim2Client extends BaseScimClient {
	
	/**
     * Creates a person with ScimPerson as input
     * @param ScimPerson person
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse createPerson(User person,String mediaType) throws JsonGenerationException, JsonMappingException, IOException, JAXBException;
	
}
