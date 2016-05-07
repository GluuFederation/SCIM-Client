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
 * README:
 *
 * Check first if /install/community-edition-setup/templates/test/scim-client/data/scim-test-data.ldif
 * has been loaded to LDAP.
 *
 * @author Val Pecaoco
 */
public class UserFiltersMainTests extends BaseScimTest {

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
    public void testSearchUsers1() throws Exception {

        String[] filters = new String[] {
            "id sw \"@\"",
            "groups sw \"inum=@\"",
            "externalId sw \"1\"",
            "externalId ew \"3000\"",
            "birthdate ge \"2014-01-01\" and externalId co \"2\"",
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
            "name[displayName co \"1111\"] and (name.familyName co \"filter\")",
            "name[givenName sw \"aaaa 1111\" and familyName ew \"test\"]"
        };

        int startIndex = 1;
        int count = 20;
        String sortBy = "userName";
        String sortOrder = "ascending";
        String[] attributes = null;
        // String[] attributes = new String[] {"name","emails","addresses"};  // Not supported as per spec
        // String[] attributes = new String[] {"name.givenName","emails.type","emails.primary","emails.value"};  // Supported

        for (int i = 0; i < filters.length; i++) {

            ScimResponse response = client.searchUsers(filters[i], startIndex, count, sortBy, sortOrder, attributes);

            System.out.println(" testSearchUsers1 response (" + i + ") = " + response.getResponseBodyString());

            assertEquals(response.getStatusCode(), 200, "Status != 200");

            byte[] bytes = response.getResponseBody();
            String responseStr = new String(bytes);

            Map<String, Object> objectMap = (Map<String, Object>)jsonToObject(responseStr, Map.class);

            int totalResults = ((Integer)objectMap.get("totalResults")).intValue();

            Assert.assertTrue(totalResults > 0);
            System.out.println(" filter = " + filters[i] + ", totalResults = " + totalResults + "\n");
        }
    }

    @Test
    public void testSearchUsers2() throws Exception {

        String[] filters = new String[] {
            "urn:ietf:params:scim:schemas:core:2.0:User:name.userName sw \"aaaa1111\"",
            "urn:ietf:params:scim:schemas:core:2.0:User:name.userName ew \"1111\" and emails.type eq \"work\"",
            "urn:ietf:params:scim:schemas:core:2.0:User:name.givenName co \"2222\" and addresses.type eq \"work\"",
            "urn:ietf:params:scim:schemas:core:2.0:User:addresses.primary pr",
            "urn:ietf:params:scim:schemas:core:2.0:User:addresses pr",
            "emails.type pr",
            "emails pr",
            "addresses.primary eq \"true\"",
            "not emails.display pr",
            "not urn:ietf:params:scim:schemas:core:2.0:User:emails.primary eq \"true\""
        };

        int startIndex = 1;
        int count = 20;
        String sortBy = "name.givenName";
        String sortOrder = "descending";
        String[] attributes = new String[] {
            "name.honorificPrefix", "name.givenName", "name.middleName", "name.familyName", "name.honorificSuffix",
            "emails.value", "emails.type", "emails.primary",
            "addresses.formatted","addresses.country","addresses.type"
        };

        for (int i = 0; i < filters.length; i++) {

            ScimResponse response = client.searchUsers(filters[i], startIndex, count, sortBy, sortOrder, attributes);

            System.out.println(" testSearchUsers2 response (" + i + ") = " + response.getResponseBodyString());

            assertEquals(response.getStatusCode(), 200, "Status != 200");

            byte[] bytes = response.getResponseBody();
            String responseStr = new String(bytes);

            Map<String, Object> objectMap = (Map<String, Object>)jsonToObject(responseStr, Map.class);

            int totalResults = ((Integer)objectMap.get("totalResults")).intValue();

            Assert.assertTrue(totalResults > 0);
            System.out.println(" filter = " + filters[i] + ", totalResults = " + totalResults + "\n");
        }
    }

    @Test
    public void testSearchUsersPaging() throws Exception {

        String filter = "externalId pr";
        int startIndex = 1;
        int count = 5;
        String sortBy = "displayName";
        String sortOrder = "ascending";
        String[] attributes = null;

        // Completely page through all the results
        while (true) {

            ScimResponse response = client.searchUsers(filter, startIndex, count, sortBy, sortOrder, attributes);

            System.out.println(" testSearchUsersPaging response = " + response.getResponseBodyString());

            assertEquals(response.getStatusCode(), 200, "Status != 200");

            byte[] bytes = response.getResponseBody();
            String responseStr = new String(bytes);

            Map<String, Object> objectMap = (Map<String, Object>)jsonToObject(responseStr, Map.class);

            int totalResults = ((Integer)objectMap.get("totalResults")).intValue();
            int itemsPerPage = ((Integer)objectMap.get("itemsPerPage")).intValue();

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

    private Object jsonToObject(String json, Class<?> clazz) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        Object clazzObject = mapper.readValue(json, clazz);
        return clazzObject;
    }
}
