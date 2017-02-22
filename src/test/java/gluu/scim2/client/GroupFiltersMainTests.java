/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.gluu.oxtrust.model.scim2.Constants.GROUP_CORE_SCHEMA_ID;
import static org.testng.Assert.assertEquals;

/**
 * @author Val Pecaoco
 */
public class GroupFiltersMainTests extends BaseScimTest {

    //TODO: test this
    ScimClient client;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        client = ScimClientFactory.getClient(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
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

            BaseClientResponse<ListResponse> response = client.searchGroups(filters[i], startIndex, count, sortBy, sortOrder, attributes);

            assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Status != 200");

            ListResponse listResponse = response.getEntity();

            System.out.println(" filter = " + filters[i] + ", totalResults = " + listResponse.getTotalResults() + "\n");
            Assert.assertTrue(listResponse.getTotalResults() > 0);
        }
    }
}
