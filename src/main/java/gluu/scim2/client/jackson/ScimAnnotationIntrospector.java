package gluu.scim2.client.jackson;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.AnnotatedParameter;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;

import java.lang.annotation.Annotation;

/**
 * Created by eugeniuparvan on 2/18/17.
 * <p>
 * This class overrides `old` JacksonAnnotationIntrospector in order to allow combining annotations from `org.codehaus` and `com.fasterxml` packages.
 * Generally we should avoid using old version of jackson libraries and use only new version aka `com.fasterxml`, then we can get rid of this class.
 */
public class ScimAnnotationIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public boolean isHandled(Annotation ann) {
        Class<? extends Annotation> acls = ann.annotationType();
        return acls.getAnnotation(org.codehaus.jackson.annotate.JacksonAnnotation.class) != null || acls.getAnnotation(JacksonAnnotation.class) != null;
    }

    @Override
    public String findSerializablePropertyName(AnnotatedField af) {
        JsonProperty pann = af.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        if (af.hasAnnotation(JsonSerialize.class) || af.hasAnnotation(JsonView.class)) {
            return "";
        }
        return super.findSerializablePropertyName(af);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String findGettablePropertyName(AnnotatedMethod am) {
        JsonProperty pann = am.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        JsonGetter ann = am.getAnnotation(JsonGetter.class);
        if (ann != null) {
            return ann.value();
        }
        if (am.hasAnnotation(JsonSerialize.class) || am.hasAnnotation(JsonView.class)) {
            return "";
        }
        return super.findGettablePropertyName(am);
    }

    @Override
    public String findSettablePropertyName(AnnotatedMethod am) {
        JsonProperty pann = am.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        JsonSetter ann = am.getAnnotation(JsonSetter.class);
        if (ann != null) {
            return ann.value();
        }
        if (am.hasAnnotation(JsonDeserialize.class)
                || am.hasAnnotation(JsonView.class)
                || am.hasAnnotation(JsonBackReference.class)
                || am.hasAnnotation(JsonManagedReference.class)
                ) {
            return "";
        }
        return super.findSettablePropertyName(am);
    }

    @Override
    public String findDeserializablePropertyName(AnnotatedField af) {
        JsonProperty pann = af.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        if (af.hasAnnotation(JsonDeserialize.class)
                || af.hasAnnotation(JsonView.class)
                || af.hasAnnotation(JsonBackReference.class)
                || af.hasAnnotation(JsonManagedReference.class)
                ) {
            return "";
        }
        return super.findDeserializablePropertyName(af);
    }

    @Override
    public String findPropertyNameForParam(AnnotatedParameter param) {
        if (param != null) {
            JsonProperty pann = param.getAnnotation(JsonProperty.class);
            if (pann != null) {
                return pann.value();
            }
        }
        return super.findPropertyNameForParam(param);
    }
}