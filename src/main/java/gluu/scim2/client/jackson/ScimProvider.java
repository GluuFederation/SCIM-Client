package gluu.scim2.client.jackson;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.gluu.oxtrust.model.scim2.Constants;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

/**
 * Created by eugeniuparvan on 2/21/17.
 */
@Provider
@Consumes({MediaType.APPLICATION_JSON, Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
@Produces({MediaType.APPLICATION_JSON, Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
public class ScimProvider extends JacksonJsonProvider {}
