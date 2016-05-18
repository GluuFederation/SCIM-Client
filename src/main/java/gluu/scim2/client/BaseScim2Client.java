/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
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
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;

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
	ScimResponse createPerson(User person,String mediaType) throws IOException, JAXBException;
	
	/**
     * Updates a person with User as input
     * @param User person
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	ScimResponse updatePerson(User person,String uid,String mediaType) throws IOException, JAXBException;
	
	/**
     * Creates a Group with Group as input
     * @param ScimGroup group
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	ScimResponse createGroup(Group group,String mediaType) throws IOException, JAXBException;
	
	/**
     * Updates a Group with Group as input
     * @param ScimGroup group
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	ScimResponse updateGroup(Group group,String id, String mediaType) throws IOException, JAXBException;
	
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
	ScimResponse bulkOperation(BulkRequest bulkRequest, String mediaType) throws IOException, JAXBException ;

	ScimResponse retrieveServiceProviderConfig(String mediaType) throws IOException;

	ScimResponse retrieveResourceTypes(String mediaType) throws	IOException;
	
	ScimResponse searchPersons(String attribute, String value, String mediaType) throws IOException, JAXBException;

	/**
	 * User search via a filter with pagination and sorting
	 *
	 * @param filter
	 * @param startIndex
	 * @param count
	 * @param sortBy
	 * @param sortOrder
	 * @param attributesArray
	 * @return
     * @throws IOException
     */
	ScimResponse searchUsers(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

	/**
	 * Group search via a filter with pagination and sorting
	 *
	 * @param filter
	 * @param startIndex
	 * @param count
	 * @param sortBy
	 * @param sortOrder
	 * @param attributesArray
	 * @return
     * @throws IOException
     */
	ScimResponse searchGroups(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

	/**
	 * Retrieves the User Extension Schema
	 *
	 * @return
	 * @throws Exception
     */
	UserExtensionSchema getUserExtensionSchema() throws Exception;
}
