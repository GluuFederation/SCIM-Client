/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client;

import gluu.BaseScimTest;
import gluu.scim.client.utils.Util;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Map;

import static org.gluu.oxtrust.model.scim2.Constants.SCIM1_CORE_SCHEMA_ID;
import static org.testng.Assert.assertEquals;

/**
 * README:
 *
 * 1. Check first if /install/community-edition-setup/templates/test/scim-client/data/scim-test-data.ldif
 *    has been loaded to LDAP.
 * 2. This uses multithreaded tests so UMA RPT connection pooling must also be turned on in oxTrust (under
 *    "Configuration" -> "JSON Configuration" -> "OxTrust Configuration").
 *
 * @author Val Pecaoco
 */
public class GroupFiltersMainTests extends BaseScimTest {

    ScimClient client;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        client = ScimClient.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test(threadPoolSize = 5, invocationCount = 15, timeOut = 10000)
    public void testSearchGroups1() throws Exception {

        String[] filters = new String[] {
            "id sw \"@\"",
            "id pr and displayName co \"group\"",
            "(id co \"!\" and displayName pr)",
            "displayName pr",
            "displayName ew \"group\"",
            "displayName co \"group\"",
            SCIM1_CORE_SCHEMA_ID + ":member co \"@\""
        };

        int startIndex = 1;
        int count = 20;
        String sortBy = "displayName";
        String sortOrder = "ascending";
        String[] attributes = null;

        for (int i = 0; i < filters.length; i++) {

            ScimResponse response = client.searchGroups(filters[i], startIndex, count, sortBy, sortOrder, attributes);

            System.out.println(" testSearchGroups1 response (" + i + ") = " + response.getResponseBodyString());
            assertEquals(response.getStatusCode(), 200, "Status != 200");

            Map<String, Object> objectMap = (Map<String, Object>)Util.jsonToObject(response, Map.class);

            int totalResults = (Integer) objectMap.get("totalResults");
            System.out.println(" totalResults = " + totalResults);
            Assert.assertTrue(totalResults > 0);
        }
    }

    @Test(threadPoolSize = 5, invocationCount = 15, timeOut = 10000)
    public void testSearchGroups2() throws Exception {

        String filter = "(id co \"!\" and displayName pr)";

        int startIndex = 1;
        int count = 20;
        String sortBy = "displayName";
        String sortOrder = "ascending";
        String[] attributes = new String[]{"id", "members.value", "members.display"};

        ScimResponse response = client.searchGroups(filter, startIndex, count, sortBy, sortOrder, attributes);

        System.out.println(" testSearchGroups2 response = " + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "Status != 200");

        Map<String, Object> objectMap = (Map<String, Object>)Util.jsonToObject(response, Map.class);

        int totalResults = (Integer) objectMap.get("totalResults");
        System.out.println(" totalResults = " + totalResults);
        Assert.assertTrue(totalResults > 0);
    }
}
