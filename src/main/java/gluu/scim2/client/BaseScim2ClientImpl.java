/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.scim2.client.jackson.ScimContextResolver;
import org.apache.commons.lang.StringUtils;
import org.gluu.oxtrust.model.scim2.*;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;
import org.gluu.oxtrust.model.scim2.provider.ResourceType;
import org.gluu.oxtrust.model.scim2.provider.ServiceProviderConfig;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.gluu.oxtrust.model.scim2.Constants.MAX_COUNT;

/**
 * SCIM default client
 *
 * @author Yuriy Movchan Date: 08/23/2013
 */
public abstract class BaseScim2ClientImpl implements BaseScim2Client {

    private static final long serialVersionUID = 9098930517944520482L;

    private static final Logger log = LoggerFactory.getLogger(BaseScim2ClientImpl.class);

    private ScimService scimService;

    public BaseScim2ClientImpl(String domain) {
        ResteasyProviderFactory resteasyProviderFactory = ResteasyProviderFactory.getInstance();
        resteasyProviderFactory.registerProvider(ScimContextResolver.class);
        this.scimService = ProxyFactory.create(ScimService.class, ProxyFactory.createUri(domain), ClientRequest.getDefaultExecutor(), resteasyProviderFactory);
    }

    protected abstract void prepareRequest();

    protected abstract String getAuthenticationHeader();

    protected abstract boolean authorize(BaseClientResponse response);

    protected boolean isNeededToAuthorize(BaseClientResponse response) {
        if (response.getStatus() != Response.Status.FORBIDDEN.getStatusCode())
            return false;
        try {
            return authorize(response);
        } finally {
            if (response != null)
                response.releaseConnection(); // then close InputStream
        }
    }

    @Override
    public final BaseClientResponse<ServiceProviderConfig> retrieveServiceProviderConfig() {
        BaseClientResponse<ServiceProviderConfig> response = null;
        prepareRequest();
        try {
            response = scimService.retrieveServiceProviderConfig(getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.retrieveServiceProviderConfig(getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    public final BaseClientResponse<ResourceType> retrieveResourceTypes() {
        BaseClientResponse<ResourceType> response = null;
        prepareRequest();
        try {
            response = scimService.retrieveResourceTypes(getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.retrieveResourceTypes(getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    @Deprecated
    public final BaseClientResponse<User> retrievePerson(String id, String mediaType) {
        return retrieveUser(id, new String[]{});
    }

    @Override
    public final BaseClientResponse<User> retrieveUser(String id, String[] attributesArray) {
        BaseClientResponse<User> response = null;
        prepareRequest();

        String attributes = (attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null;
        try {
            response = scimService.retrieveUser(id, attributes, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.retrieveUser(id, attributes, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    @Deprecated
    public final BaseClientResponse<User> createPerson(User user, String mediaType) {
        return createUser(user, new String[]{});
    }

    @Override
    public final BaseClientResponse<User> createUser(User user, String[] attributesArray) {
        BaseClientResponse<User> response = null;
        prepareRequest();
        try {
            response = scimService.createUser(user, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.createUser(user, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    @Deprecated
    public final BaseClientResponse<User> updatePerson(User user, String id, String mediaType) {
        return updateUser(user, id, new String[]{});
    }

    @Override
    public final BaseClientResponse<User> updateUser(User user, String id, String[] attributesArray) {
        BaseClientResponse<User> response = null;
        prepareRequest();

        String attributes = (attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null;
        try {
            response = scimService.updateUser(user, id, attributes, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.updateUser(user, id, attributes, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see gluu.scim.client.ScimClientService#deletePerson(java.lang.String)
     */
    @Override
    public final BaseClientResponse deletePerson(String id) {
        BaseClientResponse<User> response = null;
        prepareRequest();
        try {
            response = scimService.deletePerson(id, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.deletePerson(id, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see gluu.scim.client.ScimClientService#retrieveGroup(java.lang.String,
     * java.lang.String)
     */
    @Override
    @Deprecated
    public final BaseClientResponse<Group> retrieveGroup(String id, String mediaType) {
        return retrieveGroup(id, new String[]{});
    }

    @Override
    public final BaseClientResponse<Group> retrieveGroup(String id, String[] attributesArray) {
        BaseClientResponse<Group> response = null;
        prepareRequest();

        String attributes = (attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null;
        try {
            response = scimService.retrieveGroup(id, attributes, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.retrieveGroup(id, attributes, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * gluu.scim.client.ScimClientService#createGroup(gluu.scim.client.model
     * .ScimGroup, java.lang.String)
     */
    @Override
    @Deprecated
    public final BaseClientResponse<Group> createGroup(Group group, String mediaType) {
        return createGroup(group, new String[]{});
    }

    @Override
    public final BaseClientResponse<Group> createGroup(Group group, String[] attributesArray) {
        BaseClientResponse<Group> response = null;
        prepareRequest();

        String attributes = (attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null;
        try {
            response = scimService.createGroup(group, attributes, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.createGroup(group, attributes, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    @Deprecated
    public final BaseClientResponse<Group> updateGroup(Group group, String id, String mediaType) {
        return updateGroup(group, id, new String[]{});
    }

    @Override
    public final BaseClientResponse<Group> updateGroup(Group group, String id, String[] attributesArray) {
        BaseClientResponse<Group> response = null;
        prepareRequest();

        String attributes = (attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null;
        try {
            response = scimService.updateGroup(group, id, attributes, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.updateGroup(group, id, attributes, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    public final BaseClientResponse deleteGroup(String id) {
        BaseClientResponse response = null;
        prepareRequest();

        try {
            response = scimService.deleteGroup(id, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.deleteGroup(id, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    @Deprecated
    public final BaseClientResponse<User> createPersonString(String person, String mediaType) {

        BaseClientResponse<User> response = null;
        prepareRequest();

        try {
            response = scimService.createPersonString(person, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.createPersonString(person, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    @Deprecated
    public final BaseClientResponse<User> updatePersonString(String person, String id, String mediaType) {
        BaseClientResponse<User> response = null;
        prepareRequest();

        try {
            response = scimService.updatePersonString(person, id, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.updatePersonString(person, id, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    @Deprecated
    public final BaseClientResponse<Group> createGroupString(String group, String mediaType) {
        BaseClientResponse<Group> response = null;
        prepareRequest();

        try {
            response = scimService.createGroupString(group, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.createGroupString(group, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    @Deprecated
    public final BaseClientResponse<Group> updateGroupString(String group, String id, String mediaType) {
        BaseClientResponse<Group> response = null;
        prepareRequest();

        try {
            response = scimService.updateGroupString(group, id, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.updateGroupString(group, id, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    public final BaseClientResponse<BulkResponse> processBulkOperation(BulkRequest bulkRequest) {
        BaseClientResponse<BulkResponse> response = null;
        prepareRequest();

        try {
            response = scimService.processBulkOperation(bulkRequest, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.processBulkOperation(bulkRequest, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    public final BaseClientResponse<BulkResponse> processBulkOperationString(String bulkRequestString) {
        BaseClientResponse<BulkResponse> response = null;
        prepareRequest();

        try {
            response = scimService.processBulkOperationString(bulkRequestString, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.processBulkOperationString(bulkRequestString, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();// caching entity
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    public final BaseClientResponse<ListResponse> retrieveAllUsers() {
        return searchUsers("", 1, MAX_COUNT, "", "", new String[]{});
    }

    @Override
    public final BaseClientResponse<ListResponse> searchUsers(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) {
        BaseClientResponse<ListResponse> response = null;
        prepareRequest();

        String attributes = (attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null;
        try {
            response = scimService.searchUsers(filter, startIndex, count, sortBy, sortOrder, attributes, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.searchUsers(filter, startIndex, count, sortBy, sortOrder, attributes, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    public final BaseClientResponse<ListResponse> searchUsersPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) {
        BaseClientResponse<ListResponse> response = null;
        prepareRequest();

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setAttributesArray((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null);
        searchRequest.setCount(count);
        searchRequest.setFilter(filter);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortOrder(sortOrder);
        searchRequest.setStartIndex(startIndex);

        try {
            response = scimService.searchUsersPost(searchRequest, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.searchUsersPost(searchRequest, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    public final BaseClientResponse<ListResponse> retrieveAllGroups() {
        return searchGroups("", 1, MAX_COUNT, "", "", new String[]{});
    }

    @Override
    public final BaseClientResponse<ListResponse> searchGroups(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) {
        BaseClientResponse<ListResponse> response = null;
        prepareRequest();

        String attributes = (attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null;
        try {
            response = scimService.searchGroups(filter, startIndex, count, sortBy, sortOrder, attributes, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.searchGroups(filter, startIndex, count, sortBy, sortOrder, attributes, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    public final BaseClientResponse<ListResponse> searchGroupsPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) {
        BaseClientResponse<ListResponse> response = null;
        prepareRequest();

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setAttributesArray((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null);
        searchRequest.setCount(count);
        searchRequest.setFilter(filter);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortOrder(sortOrder);
        searchRequest.setStartIndex(startIndex);

        try {
            response = scimService.searchGroupsPost(searchRequest, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.searchGroupsPost(searchRequest, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    public final BaseClientResponse<UserExtensionSchema> getUserExtensionSchema() {
        return scimService.getUserExtensionSchema(getAuthenticationHeader());
    }

    public final BaseClientResponse<ListResponse> searchFidoDevices(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) {
        BaseClientResponse<ListResponse> response = null;
        prepareRequest();

        String attributes = (attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null;
        try {
            response = scimService.searchFidoDevices(userId, filter, startIndex, count, sortBy, sortOrder, attributes, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.searchFidoDevices(userId, filter, startIndex, count, sortBy, sortOrder, attributes, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    public final BaseClientResponse<ListResponse> searchFidoDevicesPost(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) {
        BaseClientResponse<ListResponse> response = null;
        prepareRequest();

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setAttributesArray((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null);
        searchRequest.setCount(count);
        searchRequest.setFilter(filter);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortOrder(sortOrder);
        searchRequest.setStartIndex(startIndex);

        try {
            response = scimService.searchFidoDevicesPost(userId, searchRequest, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.searchFidoDevicesPost(userId, searchRequest, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    @Override
    public final BaseClientResponse<FidoDevice> retrieveFidoDevice(String id, String userId, String[] attributesArray) {
        BaseClientResponse<FidoDevice> response = null;
        prepareRequest();
        String attributes = (attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null;
        try {
            response = scimService.retrieveFidoDevice(id, userId, attributes, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.retrieveFidoDevice(id, userId, attributes, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();
                response.releaseConnection(); // then close InputStream
            }
        }
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
    public final BaseClientResponse<FidoDevice> updateFidoDevice(FidoDevice fidoDevice, String[] attributesArray) {
        BaseClientResponse<FidoDevice> response = null;
        prepareRequest();
        String attributes = (attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null;
        try {
            response = scimService.updateFidoDevice(fidoDevice.getId(), fidoDevice, attributes, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.updateFidoDevice(fidoDevice.getId(), fidoDevice, attributes, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();
                response.releaseConnection(); // then close InputStream
            }
        }
    }

    /**
     * Deletes a FIDO device
     *
     * @param id
     * @return ScimResponse
     * @throws IOException
     */
    @Override
    public final BaseClientResponse deleteFidoDevice(String id) {
        BaseClientResponse response = null;
        prepareRequest();
        try {
            response = scimService.deleteFidoDevice(id, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.deleteFidoDevice(id, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.releaseConnection(); // then close InputStream
            }
        }
    }


    @Override
    public final BaseClientResponse<User> patchUser(ScimPatchUser scimPatchUser, String id, String[] attributesArray) {
        BaseClientResponse<User> response = null;
        prepareRequest();
        String attributes = (attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null;
        try {
            response = scimService.patchUser(id, scimPatchUser, attributes, getAuthenticationHeader());
            if (isNeededToAuthorize(response))
                response = scimService.patchUser(id, scimPatchUser, attributes, getAuthenticationHeader());
            return response;
        } finally {
            if (response != null) {
                response.getEntity();
                response.releaseConnection(); // then close InputStream
            }
        }
    }
}
