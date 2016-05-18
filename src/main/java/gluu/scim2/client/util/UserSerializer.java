/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client.util;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.Extension;
import org.gluu.oxtrust.model.scim2.ExtensionFieldType;
import org.gluu.oxtrust.model.scim2.User;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Custom SCIM-Client serializer for the SCIM 2.0 User class with User Extensions.
 *
 * @author Val Pecaoco
 */
public class UserSerializer extends JsonSerializer<User> {

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        try {

            jsonGenerator.writeStartObject();

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

            JsonNode rootNode = mapper.convertValue(user, JsonNode.class);

            Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.getFields();
            while (iterator.hasNext()) {

                Map.Entry<String, JsonNode> rootNodeEntry = iterator.next();

                jsonGenerator.writeFieldName(rootNodeEntry.getKey());

                if (rootNodeEntry.getKey().equals(Constants.USER_EXT_SCHEMA_ID)) {

                    Extension extension = user.getExtension(rootNodeEntry.getKey());

                    Map<String, Object> list = new HashMap<String, Object>();
                    for (Map.Entry<String, Extension.Field> extEntry : extension.getFields().entrySet()) {

                        if (extEntry.getValue().isMultiValued()) {

                            if (extEntry.getValue().getType().equals(ExtensionFieldType.STRING)) {

                                List<String> stringList = Arrays.asList(mapper.readValue(extEntry.getValue().getValue(), String[].class));
                                list.put(extEntry.getKey(), stringList);

                            } else if (extEntry.getValue().getType().equals(ExtensionFieldType.DATE_TIME)) {

                                List<Date> dateList = Arrays.asList(mapper.readValue(extEntry.getValue().getValue(), Date[].class));
                                List<String> stringList = new ArrayList<String>();
                                DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();
                                for (Date date : dateList) {
                                    String dateString = dateTimeFormatter.print(date.getTime());
                                    stringList.add(dateString);
                                }
                                list.put(extEntry.getKey(), stringList);

                            } else if (extEntry.getValue().getType().equals(ExtensionFieldType.DECIMAL)) {

                                List<BigDecimal> numberList = Arrays.asList(mapper.readValue(extEntry.getValue().getValue(), BigDecimal[].class));
                                list.put(extEntry.getKey(), numberList);
                            }

                        } else {
                            list.put(extEntry.getKey(), extEntry.getValue().getValue());
                        }
                    }

                    jsonGenerator.writeObject(list);

                } else {

                    jsonGenerator.writeObject(rootNodeEntry.getValue());
                }
            }

            jsonGenerator.writeEndObject();

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Unexpected processing error; please check the input parameters.");
        }
    }
}
