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
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.Resource;
import org.gluu.oxtrust.model.scim2.Group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom deserializer for the SCIM 2.0 ListResponse+Group class.
 * 
 * @author Val Pecaoco
 */
public class ListResponseGroupDeserializer extends JsonDeserializer<ListResponse> {

    @Override
    public ListResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        System.out.println(" IN ListResponseGroupDeserializer.deserialize()... ");

        try {

            JsonNode rootNode = jsonParser.readValueAsTree();

            ListResponse listResponseGroup = new ListResponse();

            JsonNode totalResultsNode = rootNode.get("totalResults");
            JsonNode startIndexNode = rootNode.get("startIndex");
            JsonNode itemsPerPageNode = rootNode.get("itemsPerPage");

            JsonNode schemasNode = rootNode.get("schemas");

            JsonNode resourcesNode = rootNode.get("Resources");

            listResponseGroup.setTotalResults(totalResultsNode.asInt());
            listResponseGroup.setStartIndex(startIndexNode.asInt());
            listResponseGroup.setItemsPerPage(itemsPerPageNode.asInt());

            ArrayNode arraySchemasNode = (ArrayNode) schemasNode;
            List<String> schemas = new ArrayList<String>();
            for (int i = 0; i < arraySchemasNode.size(); i++) {
                JsonNode schemaNode = arraySchemasNode.get(i);
                schemas.add(schemaNode.asText());
            }
            listResponseGroup.setSchemas(schemas);

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

            GroupDeserializer groupDeserializer = new GroupDeserializer();

            SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1, 0, 0, ""));
            simpleModule.addDeserializer(Group.class, groupDeserializer);
            mapper.registerModule(simpleModule);

            ArrayNode arrayGroupsNode = (ArrayNode) resourcesNode;  // Must be an ArrayNode
            List<Resource> resources = new ArrayList<Resource>();
            for (int i = 0; i < arrayGroupsNode.size(); i++) {

                JsonNode groupNode = arrayGroupsNode.get(i);
                Group group = mapper.readValue(groupNode.toString(), Group.class);
                resources.add(group);
            }
            listResponseGroup.setResources(resources);

            System.out.println(" LEAVING ListResponseGroupDeserializer.deserialize()... ");

            return listResponseGroup;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Unexpected processing error: " + e.getMessage());
        }
    }
}
