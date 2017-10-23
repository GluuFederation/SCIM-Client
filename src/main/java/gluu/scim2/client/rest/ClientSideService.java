package gluu.scim2.client.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static org.gluu.oxtrust.model.scim2.Constants.*;

/**
 * Created by jgomer on 2017-09-04.
 * This is a mashup of interfaces so that a proxy to access the service can be built, see
 * The ClientSide-prefixed interfaces add some methods to actual server side interfaces that allow a more accurate
 * testing of SCIM standard conformance
 *
 * IMPORTANT NOTES!!!
 * - Always pass null for the authorization param if the method to invoke has such parameter (the true value is "injected"
 *   automatically - see gluu.scim2.client.AbstractScimClient.invoke )
 */
public interface ClientSideService extends ClientSideUserService, ClientSideGroupService, ClientSideFidoDeviceService {

    @Path("/scim/v2/ServiceProviderConfig")
    @GET
    @Produces(MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT)
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    Response getServiceProviderConfig();

    @Path("/scim/v2/ResourceTypes")
    @GET
    @Produces(MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT)
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    Response getResourceTypes();

    @Path("/scim/v2/Schemas")
    @GET
    @Produces(MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT)
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    Response getSchemas();

}
