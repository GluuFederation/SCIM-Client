package gluu.scim2.client.jackson;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.Group;
import org.gluu.oxtrust.model.scim2.Resource;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Created by eugeniuparvan on 2/18/17.
 */
@Provider
@Produces({MediaType.APPLICATION_JSON, Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
@Consumes({MediaType.APPLICATION_JSON, Constants.MEDIA_TYPE_SCIM_JSON + "; charset=utf-8"})
public class ScimContextResolver implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper;

    public ScimContextResolver() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

        AnnotationIntrospector annotationIntrospector = AnnotationIntrospector.pair(new ScimAnnotationIntrospector(), new JaxbAnnotationIntrospector());
        objectMapper.getDeserializationConfig().setAnnotationIntrospector(annotationIntrospector);
        objectMapper.getSerializationConfig().setAnnotationIntrospector(annotationIntrospector);

        SimpleModule scimModule = new SimpleModule("scimModule", Version.unknownVersion());
        scimModule.addAbstractTypeMapping(Resource.class, Group.class);
        scimModule.addAbstractTypeMapping(Resource.class, FidoDevice.class);
        scimModule.addAbstractTypeMapping(Resource.class, User.class);
        objectMapper.registerModule(scimModule);
    }

    @Override
    public ObjectMapper getContext(Class<?> arg0) {
        return objectMapper;
    }
}
