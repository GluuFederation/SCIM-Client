/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.scim.client.ScimResponse;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.gluu.oxtrust.model.scim2.BulkRequest;
import org.gluu.oxtrust.model.scim2.Group;
import org.gluu.oxtrust.model.scim2.ScimPatchUser;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;

/**
 * BaseClient
 *
 * @author Reda Zerrad Date: 05.28.2012
 */
public interface BaseScim2Client extends Serializable {

    ScimResponse retrieveServiceProviderConfig() throws IOException;

    ScimResponse retrieveResourceTypes() throws	IOException;

    /**
     * Creates a person with User as input
     *
     * @param user
     * @param mediaType
     * @return ScimResponse
     * @throws Exception
     */
    @Deprecated
    ScimResponse createPerson(User user, String mediaType) throws IOException, JAXBException;

    /**
	 * Retrieves a person by its id
	 *
	 * @param id
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 */
	@Deprecated
	ScimResponse retrievePerson(String id, String mediaType) throws IOException;

    /**
     * Updates a person with User as input
     *
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
	 * @param attributesArray
	 * @return ScimResponse
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
	 * @param user
	 * @param id
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
     */
	ScimResponse updateUser(User user, String id, String[] attributesArray) throws IOException;

	/**
	 * Deletes a person by its id
	 *
	 * @param id
	 * @return ScimResponse
	 * @throws IOException
	 */
	ScimResponse deletePerson(String id) throws IOException;

    /**
     * Creates a Group with Group as input
     *
     * @param group
     * @param mediaType
     * @return ScimResponse
     * @throws Exception
     */
    @Deprecated
    ScimResponse createGroup(Group group, String mediaType) throws IOException, JAXBException;

	/**
	 * Retrieves a group by its id
	 *
	 * @param id
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 */
	@Deprecated
	ScimResponse retrieveGroup(String id, String mediaType) throws IOException;

    /**
     * Updates a Group with Group as input
     *
     * @param group
     * @param mediaType
     * @return ScimResponse
     * @throws Exception
     */
    @Deprecated
    ScimResponse updateGroup(Group group, String id, String mediaType) throws IOException, JAXBException;

	/**
	 * @param group
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
	 * @throws JAXBException
     */
	ScimResponse createGroup(Group group, String[] attributesArray) throws IOException;

	/**
	 * @param id
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
     */
	ScimResponse retrieveGroup(String id, String[] attributesArray) throws IOException;

    /**
     * @param group
     * @param id
     * @param attributesArray
     * @return ScimResponse
     * @throws IOException
     * @throws JAXBException
     */
    ScimResponse updateGroup(Group group, String id, String[] attributesArray) throws IOException;

	/**
	 * Deletes a group by its id
	 *
	 * @param id
	 * @return ScimResponse
	 * @throws IOException
	 */
	ScimResponse deleteGroup(String id) throws IOException;

    /**
     * Bulk operation with BulkRequest as input
     *
     * @param bulkRequest
     * @return ScimResponse
     * @throws IOException
     */
    ScimResponse processBulkOperation(BulkRequest bulkRequest) throws IOException;

    /**
     * Bulk operation with String as input
     *
     * @param bulkRequestString
     * @return ScimResponse
     * @throws IOException
     */
    ScimResponse processBulkOperationString(String bulkRequestString) throws IOException;

	/**
	 * Creates a person with a String as input
	 *
	 * @param person
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 * @throws JAXBException
	 */
	@Deprecated
	ScimResponse createPersonString(String person, String mediaType) throws IOException, JAXBException;

	/**
	 * Updates a person with a String as input
	 *
	 * @param person
	 * @param id
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 * @throws JAXBException
	 */
	@Deprecated
	ScimResponse updatePersonString(String person, String id, String mediaType) throws IOException, JAXBException;

	/**
	 * Creates a group with a String as input
	 *
	 * @param group
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 * @throws JAXBException
	 */
	@Deprecated
	ScimResponse createGroupString(String group, String mediaType) throws IOException, JAXBException;

	/**
	 * Updates a group with a String as input
	 *
	 * @param group
	 * @param id
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 * @throws JAXBException
	 */
	@Deprecated
	ScimResponse updateGroupString(String group, String id, String mediaType) throws IOException, JAXBException;

	/**
	 * User search via a filter with pagination and sorting
	 *
	 * @param filter
	 * @param startIndex
	 * @param count
	 * @param sortBy
	 * @param sortOrder
	 * @param attributesArray
	 * @return ScimResponse
     * @throws IOException
     */
	ScimResponse searchUsers(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    /**
     * POST User search on /.search via a filter with pagination and sorting
     *
     * @param filter
     * @param startIndex
     * @param count
     * @param sortBy
     * @param sortOrder
     * @param attributesArray
     * @return ScimResponse
     * @throws IOException
     */
    ScimResponse searchUsersPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

	/**
	 * Group search via a filter with pagination and sorting
	 *
	 * @param filter
	 * @param startIndex
	 * @param count
	 * @param sortBy
	 * @param sortOrder
	 * @param attributesArray
	 * @return ScimResponse
     * @throws IOException
     */
	ScimResponse searchGroups(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    /**
     * POST Group search on /.search via a filter with pagination and sorting
     *
     * @param filter
     * @param startIndex
     * @param count
     * @param sortBy
     * @param sortOrder
     * @param attributesArray
     * @return ScimResponse
     * @throws IOException
     */
    ScimResponse searchGroupsPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    /**
     * Retrieves all users
     *
     * @return ScimResponse
     * @throws IOException
     */
    ScimResponse retrieveAllUsers() throws IOException;

    /**
     * Retrieves all groups
     *
     * @return ScimResponse
     * @throws IOException
     */
    ScimResponse retrieveAllGroups() throws IOException;

    /**
     * Retrieves the User Extension Schema
     *
     * @return
     * @throws Exception
     */
    UserExtensionSchema getUserExtensionSchema() throws Exception;

	/**
	 * FIDO devices search via a filter with pagination and sorting
	 *
	 * @param userId
	 * @param filter
	 * @param startIndex
	 * @param count
	 * @param sortBy
	 * @param sortOrder
	 * @param attributesArray
	 * @return
	 * @throws IOException
	 */
	ScimResponse searchFidoDevices(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

	/**
	 * POST FIDO devices search on /.search via a filter with pagination and sorting
	 *
	 * @param userId
	 * @param filter
	 * @param startIndex
	 * @param count
	 * @param sortBy
	 * @param sortOrder
	 * @param attributesArray
	 * @return
	 * @throws IOException
	 */
	ScimResponse searchFidoDevicesPost(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

	/**
	 * Retrieves a FIDO device
	 *
	 * @param id
	 * @param userId
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
	 */
	ScimResponse retrieveFidoDevice(String id, String userId, String[] attributesArray) throws IOException;

	/**
	 * Updates a FIDO device
	 *
	 * @param fidoDevice
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
	 */
	ScimResponse updateFidoDevice(FidoDevice fidoDevice, String[] attributesArray) throws IOException;

	/**
	 * Deletes a FIDO device
	 *
	 * @param id
	 * @return ScimResponse
	 * @throws IOException
	 */
	ScimResponse deleteFidoDevice(String id) throws IOException;
	
	/**
	 * @param user
	 * @param id
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
	 * @throws URISyntaxException 
     */
	ScimResponse patchUser(ScimPatchUser scimPatchUser, String id, String[] attributesArray) throws IOException, URISyntaxException;

	/**
	 * @param user
	 * @param id
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 * @throws URISyntaxException 
     */	
	ScimResponse patchUser(ScimPatchUser scimPatchUser, String id, String mediaType) throws IOException, JAXBException, URISyntaxException ;
	
	
	
}
