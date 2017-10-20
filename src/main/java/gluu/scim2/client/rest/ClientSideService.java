package gluu.scim2.client.rest;

/**
 * Created by jgomer on 2017-09-04.
 * This is a mashup of interfaces so that a proxy to access the service can be built, see
 * The ClientSide-prefixed interfaces add some methods to actual server side interfaces that allow a more accurate
 * testing of SCIM standard conformance
 */
public interface ClientSideService extends ClientSideUserService, ClientSideGroupService {

}
