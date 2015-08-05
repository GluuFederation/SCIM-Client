package gluu.scim2.client;

import gluu.scim.client.BaseScimClient;
import gluu.scim.client.ScimResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.gluu.oxtrust.model.scim2.BulkRequest;
import org.gluu.oxtrust.model.scim2.Group;
import org.gluu.oxtrust.model.scim2.User;

/**
 * BaseClient
 *
 * @author Reda Zerrad Date: 05.28.2012
 */
public interface BaseScim2Client extends BaseScimClient {
	
	/**
     * Creates a person with User as input
     * @param User person
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse createPerson(User person,String mediaType) throws JsonGenerationException, JsonMappingException, IOException, JAXBException;
	
	/**
     * Updates a person with User as input
     * @param User person
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse updatePerson(User person,String uid,String mediaType) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException, JAXBException;
	
	/**
     * Creates a Group with Group as input
     * @param ScimGroup group
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse createGroup(Group group,String mediaType) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException, JAXBException;
	
	/**
     * Updates a Group with Group as input
     * @param ScimGroup group
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse updateGroup(Group group,String id, String mediaType) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException, JAXBException;
	
	/**
     * Bulk operation with BulkOperation as input
     * @param ScimBulkOperation operation
     * @param String mediaType
     * @return ScimResponse
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * @throws JAXBException 
     * @throws Exception
     */
	public ScimResponse bulkOperation(BulkRequest bulkRequest, String mediaType) throws JsonGenerationException, JsonMappingException,
	UnsupportedEncodingException, IOException, JAXBException ;
}
