/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client;

import gluu.BaseScimTest;
import gluu.scim.client.model.ScimPerson;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.File;

import static org.testng.Assert.assertEquals;

/**
 * @author Val Pecaoco
 */
public class PersonRequiredParamsTests extends BaseScimTest {

    String domainURL;
    ScimClient client;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws Exception {
        this.domainURL = domainURL;
        String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
        client = ScimClient.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
    }

    @Test
    public void testPersonRequiredParams() throws Exception {

        System.out.println("IN testPersonRequiredParams...");

        ScimPerson person = new ScimPerson();

        ScimResponse response = client.createPerson(person, MediaType.APPLICATION_JSON);

        assertEquals(response.getStatusCode(), 400, "Status code is not equal to 400");
        assert response.getResponseBodyString().contains("There are missing required parameters");

        System.out.println("response.getStatusCode() = " + response.getStatusCode());
        System.out.println("response.getResponseBodyString() = " + response.getResponseBodyString());

        System.out.println("LEAVING testPersonRequiredParams...");
    }
}
