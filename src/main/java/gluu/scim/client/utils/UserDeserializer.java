/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client.utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.NullNode;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.Extension;
import org.gluu.oxtrust.model.scim2.GroupRef;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Custom deserializer for the SCIM 2.0 User class.
 *
 * @author Val Pecaoco
 * @link User
 */
public class UserDeserializer extends JsonDeserializer<User> {

    private static final Logger log = LoggerFactory.getLogger(UserDeserializer.class);

    private UserExtensionSchema userExtensionSchema;

    @Override
    public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        log.debug(" IN UserDeserializer.deserialize()... ");

        try {

            JsonNode rootNode = jsonParser.readValueAsTree();

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

            User user = mapper.readValue(rootNode.toString(), User.class);

            if (user.getSchemas() == null) {

                throw new IllegalArgumentException("Required field \"schemas\" is null or missing.");

            } else if (!user.getSchemas().contains(Constants.USER_CORE_SCHEMA_ID)) {

                throw new IllegalArgumentException("User Core schema is required.");

            } else if (user.getSchemas().contains(Constants.USER_EXT_SCHEMA_ID)) {

                JsonNode userExtensionNode = rootNode.get(Constants.USER_EXT_SCHEMA_ID);

                if (userExtensionNode != null) {

                    ExtensionDeserializer deserializer = new ExtensionDeserializer();
                    deserializer.setId(Constants.USER_EXT_SCHEMA_ID);
                    deserializer.setUserExtensionSchema(userExtensionSchema);

                    SimpleModule extensionDeserializerModule = new SimpleModule("ExtensionDeserializerModule", new Version(1, 0, 0, ""));
                    extensionDeserializerModule.addDeserializer(Extension.class, deserializer);
                    mapper.registerModule(extensionDeserializerModule);

                    Extension extension = mapper.readValue(userExtensionNode.toString(), Extension.class);

                    user.addExtension(extension);

                } else {
                    throw new IllegalArgumentException("User Extension schema is indicated, but value body is absent.");
                }
            }

            // Process groups

            List<GroupRef> groups = new ArrayList<GroupRef>();

            JsonNode groupsNode = rootNode.get("groups");

            if (groupsNode != null && !(groupsNode instanceof NullNode)) {

                ArrayNode arrayGroupsNode = (ArrayNode) groupsNode;  // Must be an ArrayNode

                for (int i = 0; i < arrayGroupsNode.size(); i++) {

                    JsonNode arrayNode = arrayGroupsNode.get(i);

                    Iterator<Map.Entry<String, JsonNode>> iterator = arrayNode.getFields();

                    GroupRef group = new GroupRef();

                    while (iterator.hasNext()) {

                        Map.Entry<String, JsonNode> entry = iterator.next();

                        if (!(entry.getValue() instanceof NullNode) && !entry.getValue().asText().isEmpty()) {

                            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("value")) {
                                group.setValue(entry.getValue().asText());
                            } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("display")) {
                                group.setDisplay(entry.getValue().asText());
                            } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("type")) {
                                if (GroupRef.Type.DIRECT.getValue().equalsIgnoreCase(entry.getValue().asText())) {
                                    group.setType(GroupRef.Type.DIRECT);
                                } else if (GroupRef.Type.INDIRECT.getValue().equalsIgnoreCase(entry.getValue().asText())) {
                                    group.setType(GroupRef.Type.INDIRECT);
                                }
                            } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("$ref")) {
                                group.setReference(entry.getValue().asText());
                            } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("operation")) {
                                group.setOperation(entry.getValue().asText());
                            }
                        }
                    }

                    groups.add(group);
                }
            }

            user.setGroups(groups);

            log.debug(" LEAVING UserDeserializer.deserialize()... ");

            return user;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Unexpected processing error: " + e.getMessage());
        }
    }

    public void setUserExtensionSchema(UserExtensionSchema userExtensionSchema) {
        this.userExtensionSchema = userExtensionSchema;
    }
}
