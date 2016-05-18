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
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.schema.AttributeHolder;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import java.io.File;

import static org.testng.Assert.assertEquals;

/**
 * README:
 *
 * Check first if /install/community-edition-setup/templates/test/scim-client/data/scim-test-data.ldif
 * has been loaded to LDAP.
 *
 * @author Val Pecaoco
 */
public class UserExtensionsJsonTest extends BaseScimTest {

    String domainURL;
    String uid;
    Scim2Client client;
    User person;

    String updateGivenName = "userjson.update.givenname";

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws Exception {

        this.domainURL = domainURL;
        String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
        client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
    }

    @Test(groups = "a")
    public void checkIfExtensionsExist() throws Exception {

        UserExtensionSchema userExtensionSchema = client.getUserExtensionSchema();

        assertEquals(userExtensionSchema.getId(), Constants.USER_EXT_SCHEMA_ID);

        boolean customFirstExists = false;
        boolean customSecondExists = false;
        boolean customThirdExists = false;
        for (AttributeHolder attributeHolder : userExtensionSchema.getAttributes()) {

            if (attributeHolder.getName().equals("scimCustomFirst")) {

                customFirstExists = true;
                assert(attributeHolder.getType().equals("string"));
                assert(attributeHolder.getMultiValued().equals(Boolean.FALSE));

            } else if (attributeHolder.getName().equals("scimCustomSecond")) {

                customSecondExists = true;
                assert (attributeHolder.getType().equals("dateTime"));
                assert (attributeHolder.getMultiValued().equals(Boolean.TRUE));

            } else if (attributeHolder.getName().equals("scimCustomThird")) {

                customThirdExists = true;
                assert(attributeHolder.getType().equals("decimal"));
                assert(attributeHolder.getMultiValued().equals(Boolean.FALSE));
            }
        }
        assertEquals(customFirstExists, true, "Custom attribute \"scimCustomFirst\" not found.");
        assertEquals(customSecondExists, true, "Custom attribute \"scimCustomSecond\" not found.");
        assertEquals(customThirdExists, true, "Custom attribute \"scimCustomThird\" not found.");
    }

    @Test(groups = "b", dependsOnGroups = "a")
    @Parameters({ "scim2.userext.create_json" })
    public void createPersonTest(String createJson) throws Exception {

        ScimResponse response = client.createPersonString(createJson, MediaType.APPLICATION_JSON);

        System.out.println(" createPersonTest() RESPONSE = " + response.getResponseBodyString());

        assertEquals(response.getStatusCode(), 201, "Could not Add the person, status != 201");

        person = (User) Util.toUser(response, client.getUserExtensionSchema());
        this.uid = person.getId();
    }

    @Test(groups = "c", dependsOnGroups = "b")
    @Parameters({ "scim2.userext.update_json" })
    public void updatePersonTest(String updateJson) throws Exception {

        ScimResponse response = client.updatePersonString(updateJson, this.uid, MediaType.APPLICATION_JSON);

        System.out.println(" updatePersonTest() RESPONSE = " + response.getResponseBodyString());

        assertEquals(response.getStatusCode(), 200, "Could not update the person, status != 200");

        person = (User) Util.toUser(response, client.getUserExtensionSchema());
        assertEquals(person.getName().getGivenName(), updateGivenName, "could not update the user");
    }

    @Test(groups = "d", dependsOnGroups = "c")
    public void retrievePersonTest() throws Exception {
        ScimResponse response = client.retrievePerson(this.uid, MediaType.APPLICATION_JSON);
        System.out.println(" retrievePersonTest() RESPONSE = "  + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "Could not get the person, status != 200");
    }

	@Test(dependsOnGroups = "d")
	public void deletePersonTest() throws Exception {
        ScimResponse response = client.deletePerson(this.uid);
		System.out.println(" deletePersonTest() RESPONSE = " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Could not delete the person, status != 200");
	}

    public UserExtensionsJsonTest() {
        super();
    }
}
