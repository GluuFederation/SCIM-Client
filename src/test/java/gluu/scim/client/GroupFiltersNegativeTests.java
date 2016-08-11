/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client;

import gluu.BaseScimTest;
import gluu.scim2.client.util.Util;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Map;

import static org.gluu.oxtrust.model.scim2.Constants.MAX_COUNT;
import static org.testng.Assert.assertEquals;

/**
 * README:
 *
 * Check first if /install/community-edition-setup/templates/test/scim-client/data/scim-test-data.ldif
 * has been loaded to LDAP.
 *
 * @author Val Pecaoco
 */
public class GroupFiltersNegativeTests extends BaseScimTest {

    ScimClient client;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        client = ScimClient.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test
    public void testRetrieveAllGroups() throws Exception {

        // This is the old method
        ScimResponse response = client.retrieveAllGroups();

        System.out.println(" testRetrieveAllGroups response = " + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "Status != 200");

        Map<String, Object> objectMap = (Map<String, Object>)Util.jsonToObject(response, Map.class);

        int totalResults = (Integer) objectMap.get("totalResults");
        System.out.println(" totalResults = " + totalResults);
        Assert.assertTrue(totalResults > 0);
    }

    @Test
    public void testNullFilterParams() throws Exception {

        ScimResponse response = client.searchGroups(null, 0, 0, null, null, null);

        System.out.println(" testNullFilterParams response = " + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "Status != 200");

        Map<String, Object> objectMap = (Map<String, Object>)Util.jsonToObject(response, Map.class);

        int totalResults = (Integer) objectMap.get("totalResults");
        System.out.println(" totalResults = " + totalResults);
        Assert.assertTrue(totalResults > 0);
    }

    @Test
    public void testEmptyFilterParams() throws Exception {

        ScimResponse response = client.searchGroups("", 0, 0, "", "", new String[]{""});

        System.out.println(" testEmptyFilterParams response = " + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "Status != 200");

        Map<String, Object> objectMap = (Map<String, Object>)Util.jsonToObject(response, Map.class);

        int totalResults = (Integer) objectMap.get("totalResults");
        System.out.println(" totalResults = " + totalResults);
        Assert.assertTrue(totalResults > 0);
    }

    @Test
    public void testMoreThanMaxCount() throws Exception {

        ScimResponse response = client.searchGroups("", 0, (MAX_COUNT + 1), "", "", new String[]{""});

        System.out.println(" testMoreThanMaxCount response = " + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 400, "Status != 400");
    }
}
