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
 * <p>
 * This class intercepts creation of objectMapper in MessageBodyReaderContext and helps to configure it.
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


        UniquePropertyPolymorphicDeserializer<Resource> deserializer = new UniquePropertyPolymorphicDeserializer<Resource>(Resource.class);
        deserializer.register("userName", User.class);
        deserializer.register("members", Group.class);
        deserializer.register("deviceHashCode", FidoDevice.class);
        deserializer.register("userId",FidoDevice.class);
        SimpleModule scimModule = new SimpleModule("scimModule", Version.unknownVersion());
        scimModule.addDeserializer(Resource.class, deserializer);

        objectMapper.registerModule(scimModule);
    }

    @Override
    public ObjectMapper getContext(Class<?> arg0) {
        return objectMapper;
    }
}
