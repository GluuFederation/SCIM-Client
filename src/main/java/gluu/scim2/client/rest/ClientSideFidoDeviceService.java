/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2017, Gluu
 */
package gluu.scim2.client.rest;

import org.gluu.oxtrust.ws.rs.scim2.IFidoDeviceWebService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.gluu.oxtrust.model.scim2.Constants.*;

/**
 * An interface that exhibits operations to manipulate FidoDevice SCIM resources.
 */
/*
 * Created by jgomer on 2017-10-21.
 */
public interface ClientSideFidoDeviceService extends IFidoDeviceWebService, CloseableClient {

    /**
     * Invokes a service method that allows to update a FidoDevice resource via PUT (as per section 3.5.1 of RFC 7644)
     * @param jsonDevice A String with the payload for the update
     * @param id The id of the resource to update
     * @param attrsList attributes query param (see section 3.9 of RFC 7644). A comma separated list of strings that
     *                  contains the attributes to return overriding the default attribute set for this type of resource
     * @param excludedAttrsList excludedAttributes query param (see section 3.9 of RFC 7644). A comma separated list of
     *                          strings that contains the attributes that should be excluded from the default attribute
     *                          set for this type of resource
     * @return An object abstracting the response obtained from the server to this request.
     * A succesful response for this operation should contain a status code of 200
     */
    @Path("/scim/v2/FidoDevices/{id}")
    @PUT
    @Consumes({MEDIA_TYPE_SCIM_JSON, MediaType.APPLICATION_JSON})
    @Produces({MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT, MediaType.APPLICATION_JSON + UTF8_CHARSET_FRAGMENT})
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    Response updateDevice(
            String jsonDevice,
            @PathParam("id") String id,
            @QueryParam(QUERY_PARAM_ATTRIBUTES) String attrsList,
            @QueryParam(QUERY_PARAM_EXCLUDED_ATTRS) String excludedAttrsList);

}
