/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client;

import gluu.BaseScimTest;
import gluu.scim.client.model.ScimPerson;
import gluu.scim.client.model.ScimPersonEmails;
import gluu.scim2.client.util.Util;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.File;

import static org.testng.Assert.assertEquals;

/**
 * For the use case where email is updated in oxTrust GUI and retrieved via SCIM 1.1.
 * Disabled by default.
 *
 * @author Val Pecaoco
 */
public class EmailSyncRetrieveTests extends BaseScimTest {

    String domainURL;
    ScimClient client;

    // String id = "@!D58A.A2C6.9B5E.11DA!0001!C69B.EF8E!0000!33B1.C9E9";  // Supply record
    // String id = "@!D58A.A2C6.9B5E.11DA!0001!C69B.EF8E!0000!F82A.8E7D";
    // String id = "@!D58A.A2C6.9B5E.11DA!0001!C69B.EF8E!0000!33B1.C9E9";
    // String id = "@!D58A.A2C6.9B5E.11DA!0001!C69B.EF8E!0000!8D5A.226C";
    String id = "@!D58A.A2C6.9B5E.11DA!0001!C69B.EF8E!0000!07F5.DFC9";

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws Exception {
        this.domainURL = domainURL;
        String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
        client = ScimClient.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
    }

    @Test
    public void testRetrieveEmail() throws Exception {

        System.out.println("IN testRetrieveEmail...");

        ScimResponse response = client.retrievePerson(this.id, MediaType.APPLICATION_JSON);
        System.out.println("response body = " + response.getResponseBodyString());

        assertEquals(response.getStatusCode(), 200, "Could not retrieve user, status != 200");

        ScimPerson personRetrieved = (ScimPerson) Util.jsonToObject(response, ScimPerson.class);

        for (ScimPersonEmails emailRetrieved : personRetrieved.getEmails()) {
            Assert.assertNotNull(emailRetrieved);
            System.out.println("emailRetrieved.getValue() = " + emailRetrieved.getValue());
            System.out.println("emailRetrieved.getType() = " + emailRetrieved.getType());
        }

        System.out.println("LEAVING testRetrieveEmail..." + "\n");
    }
}
