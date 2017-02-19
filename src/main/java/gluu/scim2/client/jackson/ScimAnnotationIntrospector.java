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
 */
public class ScimAnnotationIntrospector extends JacksonAnnotationIntrospector {

    /*
    /**********************************************************
    /* General annotation properties
    /**********************************************************
    */
    @Override
    public boolean isHandled(Annotation ann)
    {
        Class<? extends Annotation> acls = ann.annotationType();

        /* 16-May-2009, tatu: used to check this like so...
           final String JACKSON_PKG_PREFIX = "org.codehaus.jackson";

           Package pkg = acls.getPackage();
           return (pkg != null) && (pkg.getName().startsWith(JACKSON_PKG_PREFIX));
        */

        // but this is more reliable, now that we have tag annotation:
        return acls.getAnnotation(org.codehaus.jackson.annotate.JacksonAnnotation.class) != null || acls.getAnnotation(JacksonAnnotation.class) != null;
    }

    /*
    /**********************************************************
    /* Serialization: field annotations
    /**********************************************************
    */
    @Override
    public String findSerializablePropertyName(AnnotatedField af) {
        JsonProperty pann = af.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        // Also: having JsonSerialize implies it is such a property
        // 09-Apr-2010, tatu: Ditto for JsonView
        if (af.hasAnnotation(JsonSerialize.class) || af.hasAnnotation(JsonView.class)) {
            return "";
        }
        return super.findSerializablePropertyName(af);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String findGettablePropertyName(AnnotatedMethod am) {
        /* 22-May-2009, tatu: JsonProperty is the primary annotation
         *   to check for
         */
        JsonProperty pann = am.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        /* 22-May-2009, tatu: JsonGetter is deprecated as of 1.1
         *    but still supported
         */
        JsonGetter ann = am.getAnnotation(JsonGetter.class);
        if (ann != null) {
            return ann.value();
        }
        /* 22-May-2009, tatu: And finally, JsonSerialize implies
         *   that there is a property, although doesn't define name
         */
        // 09-Apr-2010, tatu: Ditto for JsonView
        if (am.hasAnnotation(JsonSerialize.class) || am.hasAnnotation(JsonView.class)) {
            return "";
        }
        return super.findGettablePropertyName(am);
    }

    @Override
    public String findSettablePropertyName(AnnotatedMethod am) {
        /* 16-Apr-2010, tatu: Existing priority (since 1.1) is that
         *   @JsonProperty is checked first; and @JsonSetter next.
         *   This is not quite optimal now that @JsonSetter is un-deprecated.
         *   However, it is better to have stable behavior rather than
         *   cause compatibility problems by fine-tuning.
         */
        JsonProperty pann = am.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        JsonSetter ann = am.getAnnotation(JsonSetter.class);
        if (ann != null) {
            return ann.value();
        }
        /* 22-May-2009, tatu: And finally, JsonSerialize implies
         *   that there is a property, although doesn't define name
         */
        // 09-Apr-2010, tatu: Ditto for JsonView
        // 19-Oct-2011, tatu: And JsonBackReference/JsonManagedReference
        if (am.hasAnnotation(JsonDeserialize.class)
                || am.hasAnnotation(JsonView.class)
                || am.hasAnnotation(JsonBackReference.class)
                || am.hasAnnotation(JsonManagedReference.class)
                ) {
            return "";
        }
        return super.findSettablePropertyName(am);
    }


    /*
    /**********************************************************
    /* Deserialization: field annotations
    /**********************************************************
    */
    @Override
    public String findDeserializablePropertyName(AnnotatedField af) {
        JsonProperty pann = af.getAnnotation(JsonProperty.class);
        if (pann != null) {
            return pann.value();
        }
        // Also: having JsonDeserialize implies it is such a property
        // 09-Apr-2010, tatu: Ditto for JsonView
        if (af.hasAnnotation(JsonDeserialize.class)
                || af.hasAnnotation(JsonView.class)
                || af.hasAnnotation(JsonBackReference.class)
                || af.hasAnnotation(JsonManagedReference.class)
                ) {
            return "";
        }
        return super.findDeserializablePropertyName(af);
    }

    /*
    /**********************************************************
    /* Deserialization: parameters annotations
    /**********************************************************
     */

    @Override
    public String findPropertyNameForParam(AnnotatedParameter param) {
        if (param != null) {
            JsonProperty pann = param.getAnnotation(JsonProperty.class);
            if (pann != null) {
                return pann.value();
            }
            /* And can not use JsonDeserialize as we can not use
             * name auto-detection (names of local variables including
             * parameters are not necessarily preserved in bytecode)
             */
        }
        return super.findPropertyNameForParam(param);
    }
}