/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client;

import gluu.BaseScimTest;
import gluu.scim.client.model.ScimPerson;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import static org.testng.Assert.assertEquals;

/**
 * @author Val Pecaoco
 */
public class PersonRequiredParamsTests extends BaseScimTest {

    ScimClient client;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        client = ScimClient.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test
    public void testPersonRequiredParams() throws Exception {

        System.out.println("IN testPersonRequiredParams...");

        ScimPerson person = new ScimPerson();

        ScimResponse response = client.createPerson(person, MediaType.APPLICATION_JSON);
        System.out.println("response body = " + response.getResponseBodyString());

        assertEquals(response.getStatusCode(), 400, "Status code is not equal to 400");
        assert response.getResponseBodyString().contains("There are missing required parameters");

        System.out.println("response.getStatusCode() = " + response.getStatusCode());

        System.out.println("LEAVING testPersonRequiredParams...");
    }
}
