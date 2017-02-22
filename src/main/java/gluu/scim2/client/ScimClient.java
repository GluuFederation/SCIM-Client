/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import org.gluu.oxtrust.model.scim2.*;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;
import org.gluu.oxtrust.model.scim2.provider.ResourceType;
import org.gluu.oxtrust.model.scim2.provider.ServiceProviderConfig;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;
import org.jboss.resteasy.client.core.BaseClientResponse;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;

/**
 * BaseClient
 *
 * @author Reda Zerrad Date: 05.28.2012
 */
public interface ScimClient extends Serializable {

    BaseClientResponse<ServiceProviderConfig> retrieveServiceProviderConfig();

    BaseClientResponse<ResourceType> retrieveResourceTypes() throws IOException;

    /**
     * Creates a person with User as input
     *
     * @param user
     * @param mediaType
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    @Deprecated
    BaseClientResponse<User> createPerson(User user, String mediaType) throws IOException, JAXBException;

    /**
     * Retrieves a person by its id
     *
     * @param id
     * @param mediaType
     * @return
     * @throws IOException
     */
    @Deprecated
    BaseClientResponse<User> retrievePerson(String id, String mediaType) throws IOException;

    /**
     * Updates a person with User as input
     *
     * @param user
     * @param id
     * @param mediaType
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    @Deprecated
    BaseClientResponse<User> updatePerson(User user, String id, String mediaType) throws IOException, JAXBException;

    /**
     * @param user
     * @param attributesArray
     * @return
     * @throws IOException
     */
    BaseClientResponse<User> createUser(User user, String[] attributesArray) throws IOException;

    /**
     * @param id
     * @param attributesArray
     * @return
     * @throws IOException
     */
    BaseClientResponse<User> retrieveUser(String id, String[] attributesArray) throws IOException;

    /**
     * @param user
     * @param id
     * @param attributesArray
     * @return
     * @throws IOException
     */
    BaseClientResponse<User> updateUser(User user, String id, String[] attributesArray) throws IOException;

    /**
     * Deletes a person by its id
     *
     * @param id
     * @return
     * @throws IOException
     */
    BaseClientResponse deletePerson(String id) throws IOException;

    /**
     * Creates a Group with Group as input
     *
     * @param group
     * @param mediaType
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    @Deprecated
    BaseClientResponse<Group> createGroup(Group group, String mediaType) throws IOException, JAXBException;

    /**
     * Retrieves a group by its id
     *
     * @param id
     * @param mediaType
     * @return
     * @throws IOException
     */
    @Deprecated
    BaseClientResponse<Group> retrieveGroup(String id, String mediaType) throws IOException;

    /**
     * Updates a Group with Group as input
     *
     * @param group
     * @param id
     * @param mediaType
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    @Deprecated
    BaseClientResponse<Group> updateGroup(Group group, String id, String mediaType) throws IOException, JAXBException;

    /**
     * @param group
     * @param attributesArray
     * @return
     * @throws IOException
     */
    BaseClientResponse<Group> createGroup(Group group, String[] attributesArray) throws IOException;

    /**
     * @param id
     * @param attributesArray
     * @return
     * @throws IOException
     */
    BaseClientResponse<Group> retrieveGroup(String id, String[] attributesArray) throws IOException;

    /**
     * @param group
     * @param id
     * @param attributesArray
     * @return
     * @throws IOException
     */
    BaseClientResponse<Group> updateGroup(Group group, String id, String[] attributesArray) throws IOException;

    /**
     * Deletes a group by its id
     *
     * @param id
     * @return
     * @throws IOException
     */
    BaseClientResponse deleteGroup(String id) throws IOException;

    /**
     * Bulk operation with BulkRequest as input
     *
     * @param bulkRequest
     * @return
     * @throws IOException
     */
    BaseClientResponse<BulkResponse> processBulkOperation(BulkRequest bulkRequest) throws IOException;

    /**
     * Bulk operation with String as input
     *
     * @param bulkRequestString
     * @return
     * @throws IOException
     */
    BaseClientResponse<BulkResponse> processBulkOperationString(String bulkRequestString) throws IOException;

    /**
     * Creates a person with a String as input
     *
     * @param person
     * @param mediaType
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    @Deprecated
    BaseClientResponse<User> createPersonString(String person, String mediaType) throws IOException, JAXBException;

    /**
     * Updates a person with a String as input
     *
     * @param person
     * @param id
     * @param mediaType
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    @Deprecated
    BaseClientResponse<User> updatePersonString(String person, String id, String mediaType) throws IOException, JAXBException;

    /**
     * Creates a group with a String as input
     *
     * @param group
     * @param mediaType
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    @Deprecated
    BaseClientResponse<Group> createGroupString(String group, String mediaType) throws IOException, JAXBException;

    /**
     * Updates a group with a String as input
     *
     * @param group
     * @param id
     * @param mediaType
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    @Deprecated
    BaseClientResponse<Group> updateGroupString(String group, String id, String mediaType) throws IOException, JAXBException;

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
    BaseClientResponse<ListResponse> searchUsers(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    /**
     * POST User search on /.search via a filter with pagination and sorting
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
    BaseClientResponse<ListResponse> searchUsersPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

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
    BaseClientResponse<ListResponse> searchGroups(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    /**
     * POST Group search on /.search via a filter with pagination and sorting
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
    BaseClientResponse<ListResponse> searchGroupsPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    /**
     * Retrieves all users
     *
     * @return
     * @throws IOException
     */
    BaseClientResponse<ListResponse> retrieveAllUsers() throws IOException;

    /**
     * Retrieves all groups
     *
     * @return
     * @throws IOException
     */
    BaseClientResponse<ListResponse> retrieveAllGroups() throws IOException;

    /**
     * Retrieves the User Extension Schema
     *
     * @return
     * @throws Exception
     */
    BaseClientResponse<UserExtensionSchema> getUserExtensionSchema() throws Exception;

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
    BaseClientResponse<ListResponse> searchFidoDevices(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

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
    BaseClientResponse<ListResponse> searchFidoDevicesPost(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    /**
     * Retrieves a FIDO device
     *
     * @param id
     * @param userId
     * @param attributesArray
     * @return
     * @throws IOException
     */
    BaseClientResponse<FidoDevice> retrieveFidoDevice(String id, String userId, String[] attributesArray) throws IOException;

    /**
     * Updates a FIDO device
     *
     * @param fidoDevice
     * @param attributesArray
     * @return
     * @throws IOException
     */
    BaseClientResponse<FidoDevice> updateFidoDevice(FidoDevice fidoDevice, String[] attributesArray) throws IOException;

    /**
     * Deletes a FIDO device
     *
     * @param id
     * @return
     * @throws IOException
     */
    BaseClientResponse deleteFidoDevice(String id) throws IOException;

    /**
     * @param scimPatchUser
     * @param id
     * @param attributesArray
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    BaseClientResponse<User> patchUser(ScimPatchUser scimPatchUser, String id, String[] attributesArray) throws IOException, URISyntaxException;

}
