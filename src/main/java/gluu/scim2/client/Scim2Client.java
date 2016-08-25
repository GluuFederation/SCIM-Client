/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.scim.client.ScimResponse;
import gluu.scim2.client.auth.UmaScim2ClientImpl;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.bind.JAXBException;

import org.gluu.oxtrust.model.scim2.BulkRequest;
import org.gluu.oxtrust.model.scim2.Group;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;
import org.xdi.oxauth.model.util.SecurityProviderUtility;

/**
 * SCIM Client
 * 
 * @author Reda Zerrad Date: 05.24.2012
 * @author Yuriy Movchan Date: 08/08/2013
 */
public class Scim2Client implements BaseScim2Client, Serializable {

	private static final long serialVersionUID = 5919055665311654721L;

	private BaseScim2Client scimClient;

	public Scim2Client(BaseScim2Client baseClient) {
		this.scimClient = baseClient;
	}

	public static Scim2Client umaInstance(String domain, String umaMetaDataUrl, String umaAatClientId, String umaAatClientJksPath, String umaAatClientJksPassword, String umaAatClientKeyId) {
		SecurityProviderUtility.installBCProvider();
		BaseScim2Client baseClient = new UmaScim2ClientImpl(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
		return new Scim2Client(baseClient);
	}

	/*
	 * @see gluu.scim.client.ScimClientService#retrievePerson(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@Deprecated
	public ScimResponse retrievePerson(String id, String mediaType) throws IOException {
		return scimClient.retrieveUser(id, new String[]{});
	}

	/**
	 * @param id
	 * @param attributesArray
	 * @return
	 * @throws IOException
	 */
	@Override
	public ScimResponse retrieveUser(String id, String[] attributesArray) throws IOException {
		return scimClient.retrieveUser(id, attributesArray);
	}

	/*
     * @see
     * gluu.scim.client.ScimClientService#createPerson(gluu.scim2.client.model.User, java.lang.String)
     */
	@Override
	@Deprecated
	public ScimResponse createPerson(User user, String mediaType) throws IOException, JAXBException {
		return scimClient.createUser(user, new String[]{});
	}

	/**
	 * @param user
	 * @param attributesArray
	 * @return
	 * @throws IOException
	 */
	@Override
	public ScimResponse createUser(User user, String[] attributesArray) throws IOException {
		return scimClient.createUser(user, attributesArray);
	}

	/*
     * @see
     * gluu.scim.client.ScimClientService#updatePerson(gluu.scim.client.model.User, java.lang.String, java.lang.String)
     */
	@Override
	@Deprecated
	public ScimResponse updatePerson(User user, String id, String mediaType) throws IOException, JAXBException {
		return scimClient.updateUser(user, id, new String[]{});
	}

	/**
	 * @param user
	 * @param id
	 * @param attributesArray
	 * @return
	 * @throws IOException
	 */
	@Override
	public ScimResponse updateUser(User user, String id, String[] attributesArray) throws IOException {
		return scimClient.updateUser(user, id, attributesArray);
	}

	/*
	 * @see gluu.scim.client.ScimClientService#deletePerson(java.lang.String)
	 */
	@Override
	public ScimResponse deletePerson(String id) throws IOException {
		return scimClient.deletePerson(id);
	}

	/*
	 * @see gluu.scim.client.ScimClientService#retrieveGroup(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@Deprecated
	public ScimResponse retrieveGroup(String id, String mediaType) throws IOException {
		return scimClient.retrieveGroup(id, new String[]{});
	}

	/*
     * @see
     * gluu.scim.client.ScimClientService#createGroup(gluu.scim.client.model.Group, java.lang.String)
     */
	@Override
	@Deprecated
	public ScimResponse createGroup(Group group, String mediaType) throws IOException, JAXBException {
		return scimClient.createGroup(group, new String[]{});
	}

	/**
	 * @param group
	 * @param attributesArray
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@Override
	public ScimResponse createGroup(Group group, String[] attributesArray) throws IOException {
		return scimClient.createGroup(group, attributesArray);
	}

	/**
	 * @param id
	 * @param attributesArray
	 * @return
	 * @throws IOException
	 */
	@Override
	public ScimResponse retrieveGroup(String id, String[] attributesArray) throws IOException {
		return scimClient.retrieveGroup(id, attributesArray);
	}

	/*
	 * @see
	 * gluu.scim.client.ScimClientService#updateGroup(gluu.scim.client.model.Group, java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	public ScimResponse updateGroup(Group group, String id, String mediaType) throws IOException, JAXBException {
		return scimClient.updateGroup(group, id, new String[]{});
	}

	/**
	 * @param group
	 * @param id
	 * @param attributesArray
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@Override
	public ScimResponse updateGroup(Group group, String id, String[] attributesArray) throws IOException {
		return scimClient.updateGroup(group, id, attributesArray);
	}

	/*
	 * @see gluu.scim.client.ScimClientService#deleteGroup(java.lang.String)
	 */
	@Override
	public ScimResponse deleteGroup(String id) throws IOException {
		return scimClient.deleteGroup(id);
	}

	/*
	 * @see
	 * gluu.scim.client.ScimClientService#createPersonString(java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	public ScimResponse createPersonString(String person, String mediaType) throws IOException, JAXBException {
		return scimClient.createPersonString(person, mediaType);
	}

	/*
	 * @see
	 * gluu.scim.client.ScimClientService#updatePersonString(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	public ScimResponse updatePersonString(String person, String id, String mediaType) throws IOException, JAXBException {
		return scimClient.updatePersonString(person, id, mediaType);
	}

	/*
	 * @see
	 * gluu.scim.client.ScimClientService#createGroupString(java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	public ScimResponse createGroupString(String group, String mediaType) throws IOException, JAXBException {
		return scimClient.createGroupString(group, mediaType);
	}

	/*
	 * @see
	 * gluu.scim.client.ScimClientService#updateGroupString(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	public ScimResponse updateGroupString(String group, String id, String mediaType) throws IOException, JAXBException {
		return scimClient.updateGroupString(group, id, mediaType);
	}

	/*
	 * @see
	 * gluu.scim.client.ScimClientService#bulkOperation(gluu.scim.client.model.ScimBulkOperation, java.lang.String)
	 */
	@Override
	public ScimResponse processBulkOperation(BulkRequest bulkRequest) throws IOException {
		return scimClient.processBulkOperation(bulkRequest);
	}

	/*
	 * @see
	 * gluu.scim.client.ScimClientService#bulkOperationString(java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse processBulkOperationString(String bulkRequestString) throws IOException {
		return scimClient.processBulkOperationString(bulkRequestString);
	}

	/*
	 * @see
	 * gluu.scim.client.ScimClientService#retrieveAllUsers()
	 */
	@Override
	public ScimResponse retrieveAllUsers() throws IOException {
		return scimClient.retrieveAllUsers();
	}

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
	@Override
	public ScimResponse searchUsers(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {
		return scimClient.searchUsers(filter, startIndex, count, sortBy, sortOrder, attributesArray);
	}

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
    @Override
    public ScimResponse searchUsersPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {
        return scimClient.searchUsersPost(filter, startIndex, count, sortBy, sortOrder, attributesArray);
    }

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
	@Override
	public ScimResponse searchGroups(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {
		return scimClient.searchGroups(filter, startIndex, count, sortBy, sortOrder, attributesArray);
	}

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
    @Override
    public ScimResponse searchGroupsPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {
        return scimClient.searchGroupsPost(filter, startIndex, count, sortBy, sortOrder, attributesArray);
    }

	/*
     * @see
     * gluu.scim.client.ScimClientService#retrieveAllGroups(java.lang.String)
     */
	@Override
	public ScimResponse retrieveAllGroups() throws IOException {
		return scimClient.retrieveAllGroups();
	}

	@Override
	public ScimResponse retrieveServiceProviderConfig() throws IOException {
		return scimClient.retrieveServiceProviderConfig();
	}

	@Override
	public ScimResponse retrieveResourceTypes() throws IOException {
		return scimClient.retrieveResourceTypes();
	}

	@Override
	public UserExtensionSchema getUserExtensionSchema() throws Exception {
		return scimClient.getUserExtensionSchema();
	}

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
	@Override
	public ScimResponse searchFidoDevices(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {
		return scimClient.searchFidoDevices(userId, filter, startIndex, count, sortBy, sortOrder, attributesArray);
	}

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
	@Override
	public ScimResponse searchFidoDevicesPost(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {
		return scimClient.searchFidoDevicesPost(userId, filter, startIndex, count, sortBy, sortOrder, attributesArray);
	}

	/**
	 * Retrieves a FIDO device
	 *
	 * @param id
	 * @param userId
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
	 */
	@Override
	public ScimResponse retrieveFidoDevice(String id, String userId, String[] attributesArray) throws IOException {
		return scimClient.retrieveFidoDevice(id, userId, attributesArray);
	}

	/**
	 * Updates a FIDO device
	 *
	 * @param fidoDevice
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
	 */
	@Override
	public ScimResponse updateFidoDevice(FidoDevice fidoDevice, String[] attributesArray) throws IOException {
		return scimClient.updateFidoDevice(fidoDevice, attributesArray);
	}

	/**
	 * Deletes a FIDO device
	 *
	 * @param id
	 * @return ScimResponse
	 * @throws IOException
	 */
	@Override
	public ScimResponse deleteFidoDevice(String id) throws IOException {
		return scimClient.deleteFidoDevice(id);
	}
}
