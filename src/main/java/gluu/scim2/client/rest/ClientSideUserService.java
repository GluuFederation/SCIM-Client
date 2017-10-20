package gluu.scim2.client.rest;

import org.gluu.oxtrust.ws.rs.scim2.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.gluu.oxtrust.model.scim2.Constants.*;

/**
 * Created by jgomer on 2017-09-14.
 */

public interface ClientSideUserService extends UserService {

    @Path("/scim/v2/Users")
    @POST
    @Consumes({MEDIA_TYPE_SCIM_JSON, MediaType.APPLICATION_JSON})
    @Produces({MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT, MediaType.APPLICATION_JSON + UTF8_CHARSET_FRAGMENT})
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    Response createUser(
            String jsonUser,
            @QueryParam(QUERY_PARAM_ATTRIBUTES) String attributesArray,
            @QueryParam(QUERY_PARAM_EXCLUDED_ATTRS) String excludedAttrsArray,
            @HeaderParam("Authorization") String authorization) throws Exception;
    //Always pass null for authorization param (see gluu.scim2.client.AbstractScimClient.invoke())

}
