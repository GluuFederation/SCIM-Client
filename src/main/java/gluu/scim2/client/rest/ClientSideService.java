/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2017, Gluu
 */
package gluu.scim2.client.rest;

import org.gluu.oxtrust.model.scim2.SearchRequest;
import org.gluu.oxtrust.model.scim2.bulk.BulkRequest;
import org.gluu.oxtrust.ws.rs.scim2.IUserWebService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.gluu.oxtrust.model.scim2.Constants.*;

/**
 * A conglomerate interface that exhibits a rich amount of methods to manipulate User, Group, and FidoDevice resources
 * via the SCIM API. It also has support to call service provider configuration endpoints (see section 4 of RFC 7644).
 *
 * <p>The <i>ClientSide*</i> super interfaces add methods to actual interfaces used in server side implementation (those
 * in package {@link org.gluu.oxtrust.ws.rs.scim2 org.gluu.oxtrust.ws.rs.scim2}) enabling a more straightforward
 * interaction with the service by supplying Json payloads directly. This brings developers an alternative to the
 * objectual approach.</p>
 */
/*
 * Created by jgomer on 2017-09-04.
 */
public interface ClientSideService extends ClientSideUserService, ClientSideGroupService, ClientSideFidoDeviceService {

    /**
     * Performs a GET to the /ServiceProviderConfig endpoint that returns a JSON structure that describes the SCIM
     * specification features available on the target service implementation. See sections 5 and 8.5 of RFC 7643.
     * @return An object abstracting the response obtained from the server to this request.
     * A succesful response for this request should contain a status code of 200
     */
    @Path("/scim/v2/ServiceProviderConfig")
    @GET
    @Produces(MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT)
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    @FreelyAccessible
    Response getServiceProviderConfig();

    /**
     * Performs a GET to the /ResourceTypes endpoint that allows to discover the types of resources available on the
     * target service provider. See sections 6 and 8.6 of RFC 7643.
     * @return An object abstracting the response obtained from the server to this request.
     * A succesful response for this request should contain a status code of 200
     */
    @Path("/scim/v2/ResourceTypes")
    @GET
    @Produces(MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT)
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    @FreelyAccessible
    Response getResourceTypes();

    /**
     * Performs a GET to the /Schemas endpoint that allows to retrieve information about resource schemas supported by
     * target service provider. See sections 7 and 8.7 of RFC 7643.
     * @return An object abstracting the response obtained from the server to this request.
     * A succesful response for this request should contain a status code of 200
     */
    @Path("/scim/v2/Schemas")
    @GET
    @Produces(MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT)
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    @FreelyAccessible
    Response getSchemas();

    /**
     * Executes a system-wide query using HTTP POST. The results obtained can be of different resource types.
     * See section 3.4.3 of RFC 7644
     * @param searchRequest An object containing the parameters for the query to execute. These are the same parameters
     *                      passed in the URL for searches, for example in
     *                      {@link org.gluu.oxtrust.ws.rs.scim2.IUserWebService#searchUsers(String, Integer, Integer, String, String, String, String) IUserWebService#searchUsers(String, Integer, Integer, String, String, String, String)}
     * @return An object abstracting the response obtained from the server to this request.
     * A succesful response for this request should contain a status code of 200
     */
    @Path("/scim/v2/.search")
    @POST
    @Consumes({MEDIA_TYPE_SCIM_JSON, MediaType.APPLICATION_JSON})
    @Produces({MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT, MediaType.APPLICATION_JSON + UTF8_CHARSET_FRAGMENT})
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    Response searchResourcesPost(SearchRequest searchRequest);

    /**
     * Executes a system-wide query using HTTP POST. This is analog to
     * {@link #searchResourcesPost(SearchRequest) searchResourcesPost(SearchRequest)}
     * using a Json String to represent the org.gluu.oxtrust.model.scim2.SearchRequest object.
     * @param searchRequestJson A string in Json format containing the parameters for the query to execute. These are the same parameters
     *                      passed in the URL for searches, for example in
     *                      {@link org.gluu.oxtrust.ws.rs.scim2.IUserWebService#searchUsers(String, Integer, Integer, String, String, String, String) IUserWebService#searchUsers(String, Integer, Integer, String, String, String, String)}
     * @return An object abstracting the response obtained from the server to this request.
     * A succesful response for this request should contain a status code of 200
     */
    @Path("/scim/v2/.search")
    @POST
    @Consumes({MEDIA_TYPE_SCIM_JSON, MediaType.APPLICATION_JSON})
    @Produces({MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT, MediaType.APPLICATION_JSON + UTF8_CHARSET_FRAGMENT})
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    Response searchResourcesPost(String searchRequestJson);

    /**
     * Sends a bulk request as per section 3.7 of RFC 7644. This operation enables clients to send a potentially large collection of resource operations
     * in a single request.
     * @param request The object describing the request. Depending on the use case, constructing an instance of
     *                org.gluu.oxtrust.model.scim2.bulk.BulkRequest might be cumbersome. A more agile approach is using a
     *                Json string by calling {@link #processBulkOperations(String) processBulkOperations(String)}
     * @return An object abstracting the response obtained from the server to this request. It should contain the result
     * of every processed operation (taking into account parameters such as org.gluu.oxtrust.model.scim2.bulk.BulkRequest#failOnErrors)
     * A succesful response for this request should contain a status code of 200
     */
    @Path("/scim/v2/Bulk")
    @POST
    @Consumes({MEDIA_TYPE_SCIM_JSON, MediaType.APPLICATION_JSON})
    @Produces({MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT, MediaType.APPLICATION_JSON + UTF8_CHARSET_FRAGMENT})
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    Response processBulkOperations(BulkRequest request);

    /**
     * The analog to {@link #processBulkOperations(BulkRequest) processBulkOperations(BulkRequest)} using a Json String as input.
     * @param requestJson BulkRequest written in json format
     * @return Response object
     */
    @Path("/scim/v2/Bulk")
    @POST
    @Consumes({MEDIA_TYPE_SCIM_JSON, MediaType.APPLICATION_JSON})
    @Produces({MEDIA_TYPE_SCIM_JSON + UTF8_CHARSET_FRAGMENT, MediaType.APPLICATION_JSON + UTF8_CHARSET_FRAGMENT})
    @HeaderParam("Accept") @DefaultValue(MEDIA_TYPE_SCIM_JSON)
    Response processBulkOperations(String requestJson);

}
