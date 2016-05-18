/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client.util;

import gluu.scim.client.ScimResponse;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;
import org.gluu.oxtrust.model.scim2.Group;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;

import java.io.IOException;

/**
 * @author Val Pecaoco
 */
public class Util {

    public static Object jsonToObject(String json, Class<?> clazz) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

        Object clazzObject = mapper.readValue(json, clazz);

        return clazzObject;
    }

    /**
     * For an SCIM 2.0 User class with extensions.
     *
     * @param person
     * @return
     * @throws IOException
     */
    public static String getJSONStringUser(User person) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

        SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1, 0, 0, ""));
        simpleModule.addSerializer(User.class, new UserSerializer());
        mapper.registerModule(simpleModule);

        String value = mapper.writeValueAsString(person);

        return value;
    }

    public static User jsonToUser(String json, UserExtensionSchema userExtensionSchema) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

        UserDeserializer userDeserializer = new UserDeserializer();
        userDeserializer.setUserExtensionSchema(userExtensionSchema);

        SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1, 0, 0, ""));
        simpleModule.addDeserializer(User.class, userDeserializer);
        mapper.registerModule(simpleModule);

        User user = mapper.readValue(json, User.class);

        return user;
    }

    public static Group jsonToGroup(String json) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

        GroupDeserializer groupDeserializer = new GroupDeserializer();

        SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1, 0, 0, ""));
        simpleModule.addDeserializer(Group.class, groupDeserializer);
        mapper.registerModule(simpleModule);

        Group group = mapper.readValue(json, Group.class);

        return group;
    }

    public static ListResponse toListResponseUser(ScimResponse scimResponse, UserExtensionSchema userExtensionSchema) throws Exception {

        byte[] bytes = scimResponse.getResponseBody();
        String responseStr = new String(bytes);

        ListResponse listResponse = (ListResponse) jsonToListResponseUser(responseStr, userExtensionSchema);

        return listResponse;
    }

    public static ListResponse toListResponseGroup(ScimResponse scimResponse) throws Exception {

        byte[] bytes = scimResponse.getResponseBody();
        String responseStr = new String(bytes);

        ListResponse listResponse = (ListResponse) jsonToListResponseGroup(responseStr);

        return listResponse;
    }

    public static ListResponse jsonToListResponseUser(String json, UserExtensionSchema userExtensionSchema) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

        ListResponseUserDeserializer listResponseUserDeserializer = new ListResponseUserDeserializer();
        listResponseUserDeserializer.setUserExtensionSchema(userExtensionSchema);

        SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1, 0, 0, ""));
        simpleModule.addDeserializer(ListResponse.class, listResponseUserDeserializer);
        mapper.registerModule(simpleModule);

        ListResponse listResponseUser = mapper.readValue(json, ListResponse.class);

        return listResponseUser;
    }

    private static ListResponse jsonToListResponseGroup(String json) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

        ListResponseGroupDeserializer listResponseGroupDeserializer = new ListResponseGroupDeserializer();

        SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1, 0, 0, ""));
        simpleModule.addDeserializer(ListResponse.class, listResponseGroupDeserializer);
        mapper.registerModule(simpleModule);

        ListResponse listResponseGroup = mapper.readValue(json, ListResponse.class);

        return listResponseGroup;
    }
}
