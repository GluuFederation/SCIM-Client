/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;
import gluu.scim2.client.util.Util;
import org.apache.commons.io.FileUtils;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;

import static org.gluu.oxtrust.model.scim2.Constants.GROUP_CORE_SCHEMA_ID;
import static org.testng.Assert.assertEquals;

/**
 * @author Val Pecaoco
 */
public class GroupFiltersMainTests extends BaseScimTest {

    String domainURL;
    Scim2Client client;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        this.domainURL = domainURL;
        
        client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test
    public void testSearchGroups() throws Exception {

        String[] filters = new String[] {
            "id sw \"@\"",
            "id pr and displayName co \"group\"",
            "(id co \"!\" and displayName pr)",
            "displayName pr",
            "displayName ew \"group\"",
            "displayName co \"group\"",
            GROUP_CORE_SCHEMA_ID + ":member co \"@\""
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

            ListResponse listResponse = Util.toListResponseGroup(response);

            System.out.println(" filter = " + filters[i] + ", totalResults = " + listResponse.getTotalResults() + "\n");
            Assert.assertTrue(listResponse.getTotalResults() > 0);
        }
    }
}
