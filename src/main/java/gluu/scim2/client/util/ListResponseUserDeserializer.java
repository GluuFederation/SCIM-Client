/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.node.ArrayNode;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.Resource;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom deserializer for the SCIM 2.0 ListResponse+User class.
 *
 * @author Val Pecaoco
 */
public class ListResponseUserDeserializer extends JsonDeserializer<ListResponse> {

    private UserExtensionSchema userExtensionSchema;

    @Override
    public ListResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        System.out.println(" IN ListResponseUserDeserializer.toListResponseUser()... ");

        try {

            JsonNode rootNode = jsonParser.readValueAsTree();

            ListResponse listResponseUser = new ListResponse();

            JsonNode totalResultsNode = rootNode.get("totalResults");
            JsonNode startIndexNode = rootNode.get("startIndex");
            JsonNode itemsPerPageNode = rootNode.get("itemsPerPage");

            JsonNode schemasNode = rootNode.get("schemas");

            JsonNode resourcesNode = rootNode.get("Resources");

            listResponseUser.setTotalResults(totalResultsNode.asInt());
            listResponseUser.setStartIndex(startIndexNode.asInt());
            listResponseUser.setItemsPerPage(itemsPerPageNode.asInt());

            ArrayNode arraySchemasNode = (ArrayNode) schemasNode;
            List<String> schemas = new ArrayList<String>();
            for (int i = 0; i < arraySchemasNode.size(); i++) {
                JsonNode schemaNode = arraySchemasNode.get(i);
                schemas.add(schemaNode.asText());
            }
            listResponseUser.setSchemas(schemas);

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

            UserDeserializer userDeserializer = new UserDeserializer();
            userDeserializer.setUserExtensionSchema(userExtensionSchema);

            SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1, 0, 0, ""));
            simpleModule.addDeserializer(User.class, userDeserializer);
            mapper.registerModule(simpleModule);

            ArrayNode arrayUsersNode = (ArrayNode) resourcesNode;  // Must be an ArrayNode
            List<Resource> resources = new ArrayList<Resource>();
            for (int i = 0; i < arrayUsersNode.size(); i++) {

                JsonNode userNode = arrayUsersNode.get(i);
                User user = mapper.readValue(userNode.toString(), User.class);
                resources.add(user);
            }
            listResponseUser.setResources(resources);

            System.out.println(" LEAVING ListResponseUserDeserializer.toListResponseUser()... ");

            return listResponseUser;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Unexpected processing error: " + e.getMessage());
        }
    }

    public void setUserExtensionSchema(UserExtensionSchema userExtensionSchema) {
        this.userExtensionSchema = userExtensionSchema;
    }
}
