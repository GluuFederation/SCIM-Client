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
     * @param user
     * @param mediaType
     * @return ScimResponse
     * @throws Exception
     */
	@Deprecated
	ScimResponse createPerson(User user, String mediaType) throws IOException, JAXBException;

	/**
	 * @param user
	 * @param attributesArray
	 * @return
	 * @throws IOException
     */
	ScimResponse createUser(User user, String[] attributesArray) throws IOException;

	/**
	 * @param id
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
	 */
	ScimResponse retrieveUser(String id, String[] attributesArray) throws IOException;

	/**
     * Updates a person with User as input
     * @param user
	 * @param id
     * @param mediaType
     * @return ScimResponse
     * @throws Exception
     */
	@Deprecated
	ScimResponse updatePerson(User user, String id, String mediaType) throws IOException, JAXBException;

	/**
	 * @param user
	 * @param id
	 * @param attributesArray
	 * @return
	 * @throws IOException
     */
	ScimResponse updateUser(User user, String id, String[] attributesArray) throws IOException;

	/**
     * Creates a Group with Group as input
     * @param group
     * @param mediaType
     * @return ScimResponse
     * @throws Exception
     */
	@Deprecated
	ScimResponse createGroup(Group group, String mediaType) throws IOException, JAXBException;

	/**
	 * @param group
	 * @param attributesArray
	 * @return
	 * @throws IOException
	 * @throws JAXBException
     */
	ScimResponse createGroup(Group group, String[] attributesArray) throws IOException;

	/**
	 * @param id
	 * @param attributesArray
	 * @return
	 * @throws IOException
     */
	ScimResponse retrieveGroup(String id, String[] attributesArray) throws IOException;

	/**
     * Updates a Group with Group as input
     * @param group
     * @param mediaType
     * @return ScimResponse
     * @throws Exception
     */
	@Deprecated
	ScimResponse updateGroup(Group group, String id, String mediaType) throws IOException, JAXBException;

	/**
	 * @param group
	 * @param id
	 * @param attributesArray
	 * @return
	 * @throws IOException
     * @throws JAXBException
     */
	ScimResponse updateGroup(Group group, String id, String[] attributesArray) throws IOException;

	/**
	 * Bulk operation with BulkOperation as input
	 * @param bulkRequest
	 * @param mediaType
	 * @return
	 * @throws IOException
	 * @throws JAXBException
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
