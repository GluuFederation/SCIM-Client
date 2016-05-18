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

import javax.ws.rs.core.MediaType;
import java.io.File;

import static org.gluu.oxtrust.model.scim2.Constants.MAX_COUNT;
import static org.testng.Assert.assertEquals;

/**
 * @author Val Pecaoco
 */
public class GroupFiltersNegativeTests extends BaseScimTest {

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
    public void testRetrieveAllGroups() throws Exception {

        // This is the old method
        ScimResponse response = client.retrieveAllGroups(MediaType.APPLICATION_JSON);

        System.out.println(" testRetrieveAllGroups response = " + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "Status != 200");

        ListResponse listResponse = Util.toListResponseGroup(response);

        System.out.println(" listResponseRetrieved.getTotalResults() = " + listResponse.getTotalResults());
        Assert.assertTrue(listResponse.getTotalResults() > 0);
    }

    @Test
    public void testNullFilterParams() throws Exception {

        ScimResponse response = client.searchGroups(null, 0, 0, null, null, null);

        System.out.println(" testNullFilterParams response = " + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "Status != 200");

        ListResponse listResponse = Util.toListResponseGroup(response);

        System.out.println(" listResponseRetrieved.getTotalResults() = " + listResponse.getTotalResults());
        Assert.assertTrue(listResponse.getTotalResults() > 0);
    }

    @Test
    public void testEmptyFilterParams() throws Exception {

        ScimResponse response = client.searchGroups("", 0, 0, "", "", new String[]{""});

        System.out.println(" testEmptyFilterParams response = " + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "Status != 200");

        ListResponse listResponse = Util.toListResponseGroup(response);

        System.out.println(" listResponseRetrieved.getTotalResults() = " + listResponse.getTotalResults());
        Assert.assertTrue(listResponse.getTotalResults() > 0);
    }

    @Test
    public void testMoreThanMaxCount() throws Exception {

        ScimResponse response = client.searchGroups("", 0, (MAX_COUNT + 1), "", "", new String[]{""});

        System.out.println(" testMoreThanMaxCount response = " + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 400, "Status != 400");
    }
}
