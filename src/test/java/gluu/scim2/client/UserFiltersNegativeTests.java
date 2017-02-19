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
public class UserFiltersNegativeTests extends BaseScimTest {

    Scim2Client client;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test
    public void testRetrieveAllUsers() throws Exception {

        // This is the old method
        BaseClientResponse<ListResponse> response = client.retrieveAllUsers();

        assertEquals(response.getStatus(), 200, "Status != 200");

        ListResponse listResponse = response.getEntity();

        System.out.println(" listResponseRetrieved.getTotalResults() = " + listResponse.getTotalResults());
        Assert.assertTrue(listResponse.getTotalResults() > 0);
    }

    @Test
    public void testNullFilterParams() throws Exception {

        BaseClientResponse<ListResponse> response = client.searchUsers(null, 0, 0, null, null, null);

        assertEquals(response.getStatus(), 200, "Status != 200");

        ListResponse listResponse = response.getEntity();

        System.out.println(" listResponseRetrieved.getTotalResults() = " + listResponse.getTotalResults());
        Assert.assertTrue(listResponse.getTotalResults() > 0);
    }

    @Test
    public void testEmptyFilterParams() throws Exception {

        BaseClientResponse<ListResponse> response = client.searchUsers("", 0, 0, "", "", new String[]{""});

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Status != 200");

        ListResponse listResponse = response.getEntity();

        System.out.println(" listResponseRetrieved.getTotalResults() = " + listResponse.getTotalResults());
        Assert.assertTrue(listResponse.getTotalResults() > 0);
    }

    @Test
    public void testMoreThanMaxCount() throws Exception {
        BaseClientResponse<ListResponse> response = client.searchUsers("", 0, (MAX_COUNT + 1), "", "", new String[]{""});
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode(), "Status != 400");
    }
}
