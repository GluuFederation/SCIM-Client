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
 * Check first if /install/community-edition-setup/templates/test/scim-client/data/scim-test-data.ldif
 * has been loaded to LDAP.
 *
 * @author Val Pecaoco
 */
public class PersonFiltersMainTests extends BaseScimTest {

    ScimClient client;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        client = ScimClient.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test
    public void testSearchPersons1() throws Exception {

        String[] filters = new String[] {
            "name[givenName co \"aaaa\" and name[familyName ew \"test\"]]",
            "name[givenName co \"aaaa\" and emails[value ew \"111@test.email\"]]",
            "name[givenName co \"bbb\" and emails[value sw \"bbb\"]]",
            "name[name[givenName sw \"aaaa\"]]",
            "id sw \"@\"",
            "groups sw \"inum=@\"",
            "externalId sw \"1\"",
            "externalId ew \"3000\"",
            "birthdate ge \"20140101000000Z\" and externalId co \"2\"",
            "userName sw \"aaaa\"",
            "(name.givenName sw \"aaaa 1111\" and name.familyName ew \"test\")",
            "emails.type pr",
            "not emails.type pr",
            "emails.type ne \"work\"",
            "emails.type eq \"work\"",
            "emails.type sw \"work\"",
            "emails.type ew \"work\"",
            "emails.type co \"work\"",
            "emails[primary eq \"true\"]",
            "emails[type eq \"work\" and value co \"test\"]",
            "emails[type eq \"home\" and value sw \"bbb\"]",
            "emails[type ne \"home\" and value ew \"111@test.email\"]",
            "name[displayName co \"1111\"] and (name.familyName co \"filter\")",
            "name[givenName sw \"aaaa 1111\" and familyName ew \"test\"]"
        };

        int startIndex = 1;
        int count = 20;
        String sortBy = "userName";
        String sortOrder = "ascending";
        String[] attributes = null;

        for (int i = 0; i < filters.length; i++) {

            ScimResponse response = client.searchPersons(filters[i], startIndex, count, sortBy, sortOrder, attributes);

            System.out.println(" testSearchPersons1 response (" + i + ") = " + response.getResponseBodyString());
            assertEquals(response.getStatusCode(), 200, "Status != 200");

            Map<String, Object> objectMap = (Map<String, Object>)Util.jsonToObject(response, Map.class);

            int totalResults = (Integer) objectMap.get("totalResults");

            System.out.println(" filter = " + filters[i] + ", totalResults = " + totalResults + "\n");
            Assert.assertTrue(totalResults > 0);
        }
    }

    @Test
    public void testSearchPersons2() throws Exception {

        String[] filters = new String[] {
            SCIM1_CORE_SCHEMA_ID + ":userName sw \"aaaa1111\"",
            SCIM1_CORE_SCHEMA_ID + ":userName ew \"1111\" and emails.type eq \"work\"",
            SCIM1_CORE_SCHEMA_ID + ":name.givenName co \"2222\" and addresses.type eq \"work\"",
            SCIM1_CORE_SCHEMA_ID + ":addresses.type pr",
            SCIM1_CORE_SCHEMA_ID + ":addresses pr",
            "emails.type pr",
            "emails pr",
            "addresses.type eq \"work\"",
            "not emails.value pr",
            "not " + SCIM1_CORE_SCHEMA_ID + ":emails.type eq \"home\""
        };

        int startIndex = 1;
        int count = 20;
        String sortBy = "name.givenName";
        String sortOrder = "descending";
        String[] attributes = new String[] {
            "name.honorificPrefix", "name.givenName", "name.middleName", "name.familyName", "name.honorificSuffix",
            "emails.value", "emails.type",
            "addresses.formatted", "addresses.country", "addresses.type"
        };

        for (int i = 0; i < filters.length; i++) {

            ScimResponse response = client.searchPersons(filters[i], startIndex, count, sortBy, sortOrder, attributes);

            System.out.println(" testSearchPersons2 response (" + i + ") = " + response.getResponseBodyString());
            assertEquals(response.getStatusCode(), 200, "Status != 200");

            Map<String, Object> objectMap = (Map<String, Object>)Util.jsonToObject(response, Map.class);

            int totalResults = (Integer) objectMap.get("totalResults");

            System.out.println(" filter = " + filters[i] + ", totalResults = " + totalResults + "\n");
            Assert.assertTrue(totalResults > 0);
        }
    }

    @Test
    public void testSearchPersonsPaging() throws Exception {

        String filter = "externalId pr";
        int startIndex = 1;
        int count = 5;
        String sortBy = "displayName";
        String sortOrder = "ascending";
        String[] attributes = null;

        // Completely page through all the results
        while (true) {

            ScimResponse response = client.searchPersons(filter, startIndex, count, sortBy, sortOrder, attributes);

            System.out.println(" testSearchPersonsPaging response = " + response.getResponseBodyString());
            assertEquals(response.getStatusCode(), 200, "Status != 200");

            Map<String, Object> objectMap = (Map<String, Object>)Util.jsonToObject(response, Map.class);

            int totalResults = (Integer) objectMap.get("totalResults");
            int itemsPerPage = (Integer) objectMap.get("itemsPerPage");

            System.out.println(" totalResults = " + totalResults);
            System.out.println(" startIndex = " + startIndex);
            System.out.println(" itemsPerPage = " + itemsPerPage);

            int diff = totalResults - (startIndex + count);
            if (diff < 0) {
                break;
            } else if (diff > 0) {
                startIndex += count;
            } else {
                startIndex = diff;
            }
        }

        System.out.println(" Paging done! ");
    }
}
