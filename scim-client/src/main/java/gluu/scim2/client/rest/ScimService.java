package gluu.scim2.client.rest;

import org.gluu.oxtrust.model.scim2.*;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;
import org.gluu.oxtrust.model.scim2.provider.ResourceType;
import org.gluu.oxtrust.model.scim2.provider.ServiceProviderConfig;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;
import org.jboss.resteasy.client.core.BaseClientResponse;

import javax.ws.rs.*;

/**
 * Created by eugeniuparvan on 2/14/17.
 */
public interface ScimService {

    @Path("/scim/v2/ServiceProviderConfig")
    @GET
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<ServiceProviderConfig> retrieveServiceProviderConfig(@HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/ResourceTypes")
    @GET
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<ResourceType> retrieveResourceTypes(@HeaderParam("Authorization") String authorization);

    @Deprecated
    @Path("/scim/v2/Users")
    @POST
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<User> createPerson(User user, @HeaderParam("Authorization") String authorization);

    @Deprecated
    @Path("/scim/v2/Users/{id}")
    @GET
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<User> retrievePerson(@PathParam("id") String id, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Deprecated
    @Path("/scim/v2/Users/{id}")
    @PUT
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<User> updatePerson(User user, @PathParam("id") String id, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Users")
    @POST
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<User> createUser(User user, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Users/{id}")
    @GET
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<User> retrieveUser(@PathParam("id") String id, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Users/{id}")
    @PUT
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<User> updateUser(User user, @PathParam("id") String id, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Users/{id}")
    @DELETE
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse deletePerson(@PathParam("id") String id, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Groups")
    @POST
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<Group> createGroup(Group group, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Groups/{id}")
    @GET
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<Group> retrieveGroup(@PathParam("id") String id, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Groups/{id}")
    @PUT
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<Group> updateGroup(Group group, @PathParam("id") String id, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Groups/{id}")
    @DELETE
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse deleteGroup(@PathParam("id") String id, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Bulk")
    @POST
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<BulkResponse> processBulkOperation(BulkRequest bulkRequest, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Bulk")
    @POST
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<BulkResponse> processBulkOperationString(String bulkRequestString, @HeaderParam("Authorization") String authorization);

    @Deprecated
    @Path("/scim/v2/Users")
    @POST
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<User> createPersonString(String person, @HeaderParam("Authorization") String authorization);

    @Deprecated
    @Path("/scim/v2/Users/{id}")
    @PUT
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<User> updatePersonString(String person, @PathParam("id") String id, @HeaderParam("Authorization") String authorization);

    @Deprecated
    @Path("/scim/v2/Groups")
    @POST
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<Group> createGroupString(String group, @HeaderParam("Authorization") String authorization);

    @Deprecated
    @Path("/scim/v2/Groups/{id}")
    @PUT
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<Group> updateGroupString(String group, @PathParam("id") String id, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Users")
    @GET
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<ListResponse> searchUsers(@QueryParam("filter") String filter, @QueryParam("startIndex") int startIndex, @QueryParam("count") int count, @QueryParam("sortBy") String sortBy, @QueryParam("sortOrder") String sortOrder, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Users/.search")
    @POST
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<ListResponse> searchUsersPost(SearchRequest searchRequest, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Groups")
    @GET
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<ListResponse> searchGroups(@QueryParam("filter") String filter, @QueryParam("startIndex") int startIndex, @QueryParam("count") int count, @QueryParam("sortBy") String sortBy, @QueryParam("sortOrder") String sortOrder, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Groups/.search")
    @POST
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<ListResponse> searchGroupsPost(SearchRequest searchRequest, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/Schemas/{id}")
    @GET
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<UserExtensionSchema> getUserExtensionSchema(@PathParam("id") String id);

    @Path("/scim/v2/FidoDevices")
    @GET
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<ListResponse> searchFidoDevices(@QueryParam("userId") String userId, @QueryParam("filter") String filter, @QueryParam("startIndex") int startIndex, @QueryParam("count") int count, @QueryParam("sortBy") String sortBy, @QueryParam("sortOrder") String sortOrder, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/FidoDevices/.search")
    @POST
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<ListResponse> searchFidoDevicesPost(@QueryParam("userId") String userId, SearchRequest searchRequest, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/FidoDevices/{id}")
    @GET
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<FidoDevice> retrieveFidoDevice(@PathParam("id") String id, @QueryParam("userId") String userId, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/FidoDevices/{id}")
    @PUT
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse<FidoDevice> updateFidoDevice(@PathParam("id") String id, FidoDevice fidoDevice, @QueryParam("attributes") String attributesArray, @HeaderParam("Authorization") String authorization);

    @Path("/scim/v2/FidoDevices/{id}")
    @DELETE
    @Produces({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    @Consumes({Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
    BaseClientResponse deleteFidoDevice(@PathParam("id") String id, @HeaderParam("Authorization") String authorization);

}
