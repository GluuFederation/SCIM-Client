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
import org.gluu.oxtrust.model.scim2.Group;
import org.gluu.oxtrust.model.scim2.MemberRef;

import java.io.IOException;
import java.util.*;

/**
 * Custom deserializer for the SCIM 2.0 Group class.
 *
 * @author Val Pecaoco
 * @link Group
 */
public class GroupDeserializer extends JsonDeserializer<Group> {

    @Override
    public Group deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        System.out.println(" IN GroupDeserializer.deserialize()... ");

        try {

            JsonNode rootNode = jsonParser.readValueAsTree();

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

            Group group = mapper.readValue(rootNode.toString(), Group.class);

            // Process members

            Set<MemberRef> members = new HashSet<MemberRef>();

            JsonNode membersNode = rootNode.get("members");

            if (membersNode != null && !(membersNode instanceof NullNode)) {

                ArrayNode arrayMembersNode = (ArrayNode) membersNode;  // Must be an ArrayNode

                for (int i = 0; i < arrayMembersNode.size(); i++) {

                    JsonNode arrayNode = arrayMembersNode.get(i);

                    Iterator<Map.Entry<String, JsonNode>> iterator = arrayNode.getFields();

                    MemberRef member = new MemberRef();

                    while (iterator.hasNext()) {

                        Map.Entry<String, JsonNode> entry = iterator.next();

                        if (!(entry.getValue() instanceof NullNode) && !entry.getValue().asText().isEmpty()) {

                            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("value")) {
                                member.setValue(entry.getValue().asText());
                            } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("display")) {
                                member.setDisplay(entry.getValue().asText());
                            } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("type")) {
                                if (MemberRef.Type.USER.getValue().equalsIgnoreCase(entry.getValue().asText())) {
                                    member.setType(MemberRef.Type.USER);
                                } else if (MemberRef.Type.GROUP.getValue().equalsIgnoreCase(entry.getValue().asText())) {
                                    member.setType(MemberRef.Type.GROUP);
                                }
                            } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("$ref")) {
                                member.setReference(entry.getValue().asText());
                            } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("operation")) {
                                member.setOperation(entry.getValue().asText());
                            }
                        }
                    }

                    members.add(member);
                }
            }

            group.setMembers(members);

            System.out.println(" LEAVING GroupDeserializer.deserialize()... ");

            return group;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Unexpected processing error: " + e.getMessage());
        }
    }
}
