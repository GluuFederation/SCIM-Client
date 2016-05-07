/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * @author Val Pecaoco
 */
public class GroupFiltersMainTests extends BaseScimTest {

    String domainURL;
    Scim2Client client;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws Exception {
        this.domainURL = domainURL;
        String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
        client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
    }

    @Test
    public void testSearchGroups() throws Exception {

        String[] filters = new String[] {
            "id sw \"@\"",
            "id pr and displayName co \"group\"",
            "(id co \"!\" and displayName pr)",
            "displayName pr",
            "displayName ew \"group\"",
            "displayName co \"group\""
        };

        int startIndex = 1;
        int count = 20;
        String sortBy = "displayName";
        String sortOrder = "ascending";
        String[] attributes = null;

        for (int i = 0; i < filters.length; i++) {

            ScimResponse response = client.searchGroups(filters[i], startIndex, count, sortBy, sortOrder, attributes);

            System.out.println(" testSearchGroups response (" + i + ") = " + response.getResponseBodyString());

            assertEquals(response.getStatusCode(), 200, "Status != 200");

            byte[] bytes = response.getResponseBody();
            String responseStr = new String(bytes);

            Map<String, Object> objectMap = (Map<String, Object>)jsonToObject(responseStr, Map.class);

            int totalResults = ((Integer)objectMap.get("totalResults")).intValue();

            Assert.assertTrue(totalResults > 0);
            System.out.println(" filter = " + filters[i] + ", totalResults = " + totalResults + "\n");
        }
    }

    private Object jsonToObject(String json, Class<?> clazz) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        Object clazzObject = mapper.readValue(json, clazz);
        return clazzObject;
    }
}
