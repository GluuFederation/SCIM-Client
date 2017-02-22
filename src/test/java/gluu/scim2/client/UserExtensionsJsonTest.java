/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim2.client.factory.ScimClientFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.Extension;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.schema.AttributeHolder;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    String id;
    ScimClient client;
    User user;

    String updateGivenName = "userjson.update.givenname";

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        client = ScimClientFactory.getClient(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test(groups = "a")
    public void checkIfExtensionsExist() throws Exception {

        BaseClientResponse<UserExtensionSchema> response = client.getUserExtensionSchema();
        UserExtensionSchema userExtensionSchema = response.getEntity();
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
    public void createUserTest(String createJson) throws Exception {

        BaseClientResponse<User> response = client.createPersonString(createJson, MediaType.APPLICATION_JSON);

        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode(), "Could not add the user, status != 201");

        user = response.getEntity();
        this.id = user.getId();
    }

    @Test(groups = "c", dependsOnGroups = "b")
    @Parameters({ "scim2.userext.update_json" })
    public void updateUserTest(String updateJson) throws Exception {

        BaseClientResponse<User> response = client.updatePersonString(updateJson, this.id, MediaType.APPLICATION_JSON);

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not update the user, status != 200");

        user = response.getEntity();
        assertEquals(user.getName().getGivenName(), updateGivenName, "Could not update the user");
    }

    @Test(groups = "d", dependsOnGroups = "c")
    public void retrieveUserTest() throws Exception {

        BaseClientResponse<User> response = client.retrieveUser(this.id, new String[]{});

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not get the user, status != 200");

        User user = response.getEntity();

        Extension extension = user.getExtension(Constants.USER_EXT_SCHEMA_ID);
        Assert.assertNotNull("(Deserialization) Custom extension not deserialized.", extension);

        Extension.Field customFirstField = extension.getFields().get("scimCustomFirst");
        Assert.assertNotNull("(Deserialization) \"scimCustomFirst\" field not deserialized.", customFirstField);
        System.out.println("##### (Deserialization) customFirstField.getValue() = " + customFirstField.getValue());
        Assert.assertEquals("4000", customFirstField.getValue());

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

        Extension.Field customSecondField = extension.getFields().get("scimCustomSecond");
        Assert.assertNotNull("(Deserialization) \"scimCustomSecond\" field not deserialized.", customSecondField);
        List<Date> dateList = Arrays.asList(mapper.readValue(customSecondField.getValue(), Date[].class));
        Assert.assertEquals(2, dateList.size());
        System.out.println("##### (Deserialization) dateList.get(0) = " + dateList.get(0));
        System.out.println("##### (Deserialization) dateList.get(1) = " + dateList.get(1));

        Extension.Field customThirdField = extension.getFields().get("scimCustomThird");
        Assert.assertNotNull("(Deserialization) \"scimCustomThird\" field not deserialized.", customThirdField);
        System.out.println("##### (Deserialization) customThirdField.getValue() = " + customThirdField.getValue());
        Assert.assertEquals(new BigDecimal(5000), new BigDecimal(customThirdField.getValue()));
    }

	@Test(dependsOnGroups = "d", alwaysRun = true)
	public void deleteUserTest() throws Exception {
        BaseClientResponse response = client.deletePerson(this.id);
		assertEquals(response.getStatus(), Response.Status.NO_CONTENT.getStatusCode(), "Could not delete the user; status != 204");
	}
}
