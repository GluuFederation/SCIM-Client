/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import org.gluu.oxtrust.model.scim2.Email;
import org.gluu.oxtrust.model.scim2.User;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

/**
 * For the use case where email is updated in oxTrust GUI and retrieved via SCIM 2.0.
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
        client = ScimClientFactory.getClient(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test
    public void testRetrieveEmail() throws Exception {

        System.out.println("IN testRetrieveEmail...");

        BaseClientResponse<User> response = client.retrieveUser(this.id, new String[]{});

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not retrieve user, status != 200");

        User userRetrieved = response.getEntity();

        for (Email emailRetrieved : userRetrieved.getEmails()) {
            Assert.assertNotNull(emailRetrieved);
            System.out.println("emailRetrieved.getValue() = " + emailRetrieved.getValue());
            System.out.println("emailRetrieved.getDisplay() = " + emailRetrieved.getDisplay());
            System.out.println("emailRetrieved.getType() = " + emailRetrieved.getType().getValue());
            System.out.println("emailRetrieved.getReference() = " + emailRetrieved.getReference());
            System.out.println("emailRetrieved.getOperation() = " + emailRetrieved.getOperation());
        }

        System.out.println("LEAVING testRetrieveEmail..." + "\n");
    }
}
