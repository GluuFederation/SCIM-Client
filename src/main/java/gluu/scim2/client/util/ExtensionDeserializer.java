/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.NullNode;
import org.gluu.oxtrust.model.scim2.Extension;
import org.gluu.oxtrust.model.scim2.ExtensionFieldType;
import org.gluu.oxtrust.model.scim2.schema.AttributeHolder;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Custom deserializer for the SCIM 2.0 User Extension class.
 *
 * @author Val Pecaoco
 */
public class ExtensionDeserializer extends JsonDeserializer<Extension> {

    private String id;

    private UserExtensionSchema userExtensionSchema;

    @Override
    public Extension deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        System.out.println(" IN ExtensionDeserializer.deserialize()... ");

        try {

            if (id == null || id.isEmpty()) {
                throw new IllegalArgumentException("The URN cannot be null or empty");
            }

            JsonNode rootNode = jsonParser.readValueAsTree();
            if (!rootNode.isObject()) {
                throw new IllegalArgumentException("Extension is of wrong JSON type");
            }

            Extension.Builder extensionBuilder = new Extension.Builder(id);

            Iterator<Map.Entry<String, JsonNode>> fieldIterator = rootNode.getFields();

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

            while (fieldIterator.hasNext()) {

                Map.Entry<String, JsonNode> entry = fieldIterator.next();

                if (entry.getValue() != null && !(entry.getValue() instanceof NullNode)) {

                    for (AttributeHolder attributeHolder : userExtensionSchema.getAttributes()) {

                        if (entry.getKey().equals(attributeHolder.getName())) {

                            if (attributeHolder.getMultiValued().equals(Boolean.TRUE)) {

                                ArrayNode arrayNode = (ArrayNode)entry.getValue();

                                if (attributeHolder.getType().equals("string") || attributeHolder.getType().equals("reference")) {
                                    List<String> stringList = Arrays.asList(mapper.readValue(arrayNode, String[].class));
                                    extensionBuilder.setFieldAsList(entry.getKey(), stringList);
                                } else if (attributeHolder.getType().equals("dateTime")) {
                                    List<Date> dateList = Arrays.asList(mapper.readValue(arrayNode, Date[].class));  // For validation
                                    extensionBuilder.setFieldAsList(entry.getKey(), Arrays.asList(mapper.readValue(arrayNode, String[].class)));
                                } else if (attributeHolder.getType().equals("decimal")) {
                                    List<BigDecimal> numberList = Arrays.asList(mapper.readValue(arrayNode, BigDecimal[].class));
                                    extensionBuilder.setFieldAsList(entry.getKey(), numberList);
                                }

                            } else {

                                if (attributeHolder.getType().equals("string") || attributeHolder.getType().equals("reference")) {
                                    handleString(extensionBuilder, entry);
                                } else if (attributeHolder.getType().equals("dateTime")) {
                                    handleDateTime(extensionBuilder, entry);
                                } else if (attributeHolder.getType().equals("decimal")) {
                                    handleNumber(extensionBuilder, entry);
                                }
                            }

                            break;
                        }
                    }
                }
            }

            System.out.println(" LEAVING ExtensionDeserializer.deserialize()... ");

            return extensionBuilder.build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Unexpected processing error: " + e.getMessage());
        }
    }

    private void handleNumber(Extension.Builder extensionBuilder, Map.Entry<String, JsonNode> entry) {
        BigDecimal value = ExtensionFieldType.DECIMAL.fromString(entry.getValue().asText());
        extensionBuilder.setField(entry.getKey(), value);
    }

    private void handleString(Extension.Builder extensionBuilder, Map.Entry<String, JsonNode> entry) {
        String value = ExtensionFieldType.STRING.fromString(entry.getValue().asText());
        extensionBuilder.setField(entry.getKey(), value);
    }

    private void handleDateTime(Extension.Builder extensionBuilder, Map.Entry<String, JsonNode> entry) {
        Date value = ExtensionFieldType.DATE_TIME.fromString(entry.getValue().asText());
        extensionBuilder.setField(entry.getKey(), value);
    }

    void setId(String id) {
        this.id = id;
    }

    public void setUserExtensionSchema(UserExtensionSchema userExtensionSchema) {
        this.userExtensionSchema = userExtensionSchema;
    }
}