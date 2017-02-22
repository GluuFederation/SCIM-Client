/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client;

import gluu.BaseScimTest;
import gluu.scim.client.model.ScimPerson;
import gluu.scim.client.model.ScimPersonEmails;
import gluu.scim.client.utils.Util;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import static org.testng.Assert.assertEquals;

/**
 * For the use case where email is updated in oxTrust GUI and retrieved via SCIM 1.1.
 * Disabled by default.
 *
 * @author Val Pecaoco
 */
public class EmailSyncRetrieveTests extends BaseScimTest {

    ScimClient client;

    String id = "@!0211.A669.234B.5C2B!0001!BFB2.68C5!0000!XXXX.AAAA.1111";  // Supply record

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        client = ScimClient.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
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
