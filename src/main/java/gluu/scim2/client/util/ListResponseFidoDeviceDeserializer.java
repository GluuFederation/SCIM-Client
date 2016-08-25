/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.node.ArrayNode;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.Resource;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Val Pecaoco
 */
public class ListResponseFidoDeviceDeserializer extends JsonDeserializer<ListResponse> {

	@Override
	public ListResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

		System.out.println(" IN ListResponseFidoDeviceDeserializer.deserialize()... ");

		try {

			JsonNode rootNode = jsonParser.readValueAsTree();

			ListResponse listResponseFidoDevice = new ListResponse();

			JsonNode totalResultsNode = rootNode.get("totalResults");
			JsonNode startIndexNode = rootNode.get("startIndex");
			JsonNode itemsPerPageNode = rootNode.get("itemsPerPage");

			JsonNode schemasNode = rootNode.get("schemas");

			JsonNode resourcesNode = rootNode.get("Resources");

			listResponseFidoDevice.setTotalResults(totalResultsNode.asInt());
			listResponseFidoDevice.setStartIndex(startIndexNode.asInt());
			listResponseFidoDevice.setItemsPerPage(itemsPerPageNode.asInt());

			ArrayNode arraySchemasNode = (ArrayNode) schemasNode;
			List<String> schemas = new ArrayList<String>();
			for (int i = 0; i < arraySchemasNode.size(); i++) {
				JsonNode schemaNode = arraySchemasNode.get(i);
				schemas.add(schemaNode.asText());
			}
			listResponseFidoDevice.setSchemas(schemas);

			ArrayNode arrayFidoDevicesNode = (ArrayNode) resourcesNode;  // Must be an ArrayNode
			List<Resource> resources = new ArrayList<Resource>();
			for (int i = 0; i < arrayFidoDevicesNode.size(); i++) {

				JsonNode fidoDeviceNode = arrayFidoDevicesNode.get(i);
				FidoDevice fidoDevice = (FidoDevice) Util.jsonToObject(fidoDeviceNode.toString(), FidoDevice.class);
				resources.add(fidoDevice);
			}
			listResponseFidoDevice.setResources(resources);

			System.out.println(" LEAVING ListResponseFidoDeviceDeserializer.deserialize()... ");

			return listResponseFidoDevice;

		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Unexpected processing error: " + e.getMessage());
		}
	}
}
