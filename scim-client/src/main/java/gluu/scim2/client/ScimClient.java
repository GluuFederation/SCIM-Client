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

    //Creates a person with User as input
    @Deprecated
    BaseClientResponse<User> createPerson(User user, String mediaType) throws IOException, JAXBException;

    //Retrieves a person by its id
    @Deprecated
    BaseClientResponse<User> retrievePerson(String id, String mediaType) throws IOException;

    //Updates a person with User as input
    @Deprecated
    BaseClientResponse<User> updatePerson(User user, String id, String mediaType) throws IOException, JAXBException;

    BaseClientResponse<User> createUser(User user, String[] attributesArray) throws IOException;

    BaseClientResponse<User> retrieveUser(String id, String[] attributesArray) throws IOException;

    BaseClientResponse<User> updateUser(User user, String id, String[] attributesArray) throws IOException;

    //Deletes a person by its id
    BaseClientResponse deletePerson(String id) throws IOException;

    //Creates a Group with Group as input
    @Deprecated
    BaseClientResponse<Group> createGroup(Group group, String mediaType) throws IOException, JAXBException;

    //Retrieves a group by its id
    @Deprecated
    BaseClientResponse<Group> retrieveGroup(String id, String mediaType) throws IOException;

    //Updates a Group with Group as input
    @Deprecated
    BaseClientResponse<Group> updateGroup(Group group, String id, String mediaType) throws IOException, JAXBException;

    BaseClientResponse<Group> createGroup(Group group, String[] attributesArray) throws IOException;

    BaseClientResponse<Group> retrieveGroup(String id, String[] attributesArray) throws IOException;

    BaseClientResponse<Group> updateGroup(Group group, String id, String[] attributesArray) throws IOException;

    //Deletes a group by its id
    BaseClientResponse deleteGroup(String id) throws IOException;

    //Bulk operation with BulkRequest as input
    BaseClientResponse<BulkResponse> processBulkOperation(BulkRequest bulkRequest) throws IOException;

    //Bulk operation with String as input
    BaseClientResponse<BulkResponse> processBulkOperationString(String bulkRequestString) throws IOException;

    //Creates a person with a String as input
    @Deprecated
    BaseClientResponse<User> createPersonString(String person, String mediaType) throws IOException, JAXBException;

    //Updates a person with a String as input
    @Deprecated
    BaseClientResponse<User> updatePersonString(String person, String id, String mediaType) throws IOException, JAXBException;

    //Creates a group with a String as input
    @Deprecated
    BaseClientResponse<Group> createGroupString(String group, String mediaType) throws IOException, JAXBException;

    //Updates a group with a String as input
    @Deprecated
    BaseClientResponse<Group> updateGroupString(String group, String id, String mediaType) throws IOException, JAXBException;

    //User search via a filter with pagination and sorting
    BaseClientResponse<ListResponse> searchUsers(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    //POST User search on /.search via a filter with pagination and sorting
    BaseClientResponse<ListResponse> searchUsersPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    //Group search via a filter with pagination and sorting
    BaseClientResponse<ListResponse> searchGroups(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    //POST Group search on /.search via a filter with pagination and sorting
    BaseClientResponse<ListResponse> searchGroupsPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    //Retrieves all users
    BaseClientResponse<ListResponse> retrieveAllUsers() throws IOException;

    //Retrieves all groups
    BaseClientResponse<ListResponse> retrieveAllGroups() throws IOException;

    //Retrieves the User Extension Schema
    BaseClientResponse<UserExtensionSchema> getUserExtensionSchema() throws Exception;

    //FIDO devices search via a filter with pagination and sorting
    BaseClientResponse<ListResponse> searchFidoDevices(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    //POST FIDO devices search on /.search via a filter with pagination and sorting
    BaseClientResponse<ListResponse> searchFidoDevicesPost(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

    //Retrieves a FIDO device
    BaseClientResponse<FidoDevice> retrieveFidoDevice(String id, String userId, String[] attributesArray) throws IOException;

    //Updates a FIDO device
    BaseClientResponse<FidoDevice> updateFidoDevice(FidoDevice fidoDevice, String[] attributesArray) throws IOException;

    //Deletes a FIDO device
    BaseClientResponse deleteFidoDevice(String id) throws IOException;

}
