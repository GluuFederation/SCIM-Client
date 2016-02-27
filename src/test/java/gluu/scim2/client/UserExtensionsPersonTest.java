package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;
import gluu.scim.client.util.ResponseMapper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.*;
import org.gluu.oxtrust.model.scim2.schema.AttributeHolder;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.testng.Assert.assertEquals;

/**
 * README:
 *
 * Before running this test, you first need to manually add three custom attributes in oxTrust:
 * 1. customFirst - Text, not multi-valued
 * 2. customSecond - Date, multi-valued
 * 3. customThird - Numeric, not multi-valued
 * You also need to set them as "SCIM Attribute = True".
 *
 * @author Val Pecaoco
 */
public class UserExtensionsPersonTest extends BaseScimTest {

    String domainURL;
    String uid;
    Scim2Client client;
    User personToAdd;
    User personToUpdate;

    String username = "userjson.add.username";
    String updateDisplayName = "update.Scim2DisplayName";

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, final String umaAatClientKeyId) throws Exception {

        this.domainURL = domainURL;
        String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
        client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);

        personToAdd = new User();
        personToUpdate = new User();

        personToAdd.setUserName(username);
        personToAdd.setPassword("test");
        personToAdd.setDisplayName("Scim2DisplayName");

        Email email = new Email();
        email.setValue("scim@gluu.org");
        email.setType(org.gluu.oxtrust.model.scim2.Email.Type.WORK);
        email.setPrimary(true);
        personToAdd.getEmails().add(email);

        PhoneNumber phone = new PhoneNumber();
        phone.setType(org.gluu.oxtrust.model.scim2.PhoneNumber.Type.WORK);
        phone.setValue("654-6509-263");
        personToAdd.getPhoneNumbers().add(phone);

        org.gluu.oxtrust.model.scim2.Address address = new org.gluu.oxtrust.model.scim2.Address();
        address.setCountry("US");
        address.setStreetAddress("random street");
        address.setLocality("Austin");
        address.setPostalCode("65672");
        address.setRegion("TX");
        address.setPrimary(true);
        address.setType(org.gluu.oxtrust.model.scim2.Address.Type.WORK);
        address.setFormatted(address.getStreetAddress() + " " + address.getLocality() + " " + address.getPostalCode() + " " + address.getRegion() + " "
                + address.getCountry());
        personToAdd.getAddresses().add(address);

        personToAdd.setPreferredLanguage("US_en");

        org.gluu.oxtrust.model.scim2.Name name = new  org.gluu.oxtrust.model.scim2.Name();
        name.setFamilyName("SCIM");
        name.setGivenName("SCIM");
        personToAdd.setName(name);

        // User Extensions
        Extension.Builder extensionBuilder = new Extension.Builder(Constants.USER_EXT_SCHEMA_ID);
        extensionBuilder.setField("customFirst", "valueOne");
        extensionBuilder.setFieldAsList("customSecond", Arrays.asList(new String[]{"2016-02-23T03:35:22Z", "2016-02-24T01:52:05Z"}));
        extensionBuilder.setField("customThird", new BigDecimal(3000));
        personToAdd.addExtension(extensionBuilder.build());
    }

    @Test(groups = "a")
    public void checkIfExtensionsExist() throws Exception {

        GetMethod get = new GetMethod(this.domainURL + "/scim/v2/Schemas/" + Constants.USER_EXT_SCHEMA_ID);
        get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);

        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(get);

        ScimResponse response = ResponseMapper.map(get, null);

        byte[] bytes = response.getResponseBody();
        String json = new String(bytes);

        UserExtensionSchema userExtensionSchema = (UserExtensionSchema)jsonToObject(json, UserExtensionSchema.class);
        assertEquals(userExtensionSchema.getId(), Constants.USER_EXT_SCHEMA_ID);

        boolean customFirstExists = false;
        boolean customSecondExists = false;
        boolean customThirdExists = false;
        for (AttributeHolder attributeHolder : userExtensionSchema.getAttributes()) {

            if (attributeHolder.getName().equals("customFirst")) {

                customFirstExists = true;
                assert(attributeHolder.getType().equals("string"));
                assert(attributeHolder.getMultiValued().equals(Boolean.FALSE));

            } else if (attributeHolder.getName().equals("customSecond")) {

                customSecondExists = true;
                assert(attributeHolder.getType().equals("dateTime"));
                assert(attributeHolder.getMultiValued().equals(Boolean.TRUE));

            } else if (attributeHolder.getName().equals("customThird")) {

                customThirdExists = true;
                assert(attributeHolder.getType().equals("decimal"));
                assert(attributeHolder.getMultiValued().equals(Boolean.FALSE));
            }
        }
        assertEquals(customFirstExists, true, "Custom attribute \"customFirst\" not found.");
        assertEquals(customSecondExists, true, "Custom attribute \"customSecond\" not found.");
        assertEquals(customThirdExists, true, "Custom attribute \"customThird\" not found.");
    }

    @Test(groups = "b", dependsOnGroups = "a")
    public void createPersonTest() throws Exception {

        ScimResponse response = client.createPerson(personToAdd, MediaType.APPLICATION_JSON);

        System.out.println(" createPersonTest() RESPONSE = " + response.getResponseBodyString());

        assertEquals(response.getStatusCode(), 201, "cold not Add the person, status != 201");

        byte[] bytes = response.getResponseBody();
        String responseStr = new String(bytes);

        User person = (User) jsonToObject(responseStr, User.class);
        this.uid = person.getId();
    }

    @Test(groups = "c", dependsOnGroups = "b")
    public void updatePersonTest() throws Exception {

        personToUpdate = personToAdd;
        personToUpdate.setDisplayName(updateDisplayName);

        // User Extensions
        Extension.Builder extensionBuilder = new Extension.Builder(Constants.USER_EXT_SCHEMA_ID);
        extensionBuilder.setField("customFirst", "valueUpdated");
        // extensionBuilder.setFieldAsList("customSecond", Arrays.asList(new String[]{"1969-02-23T03:35:22Z"}));
        extensionBuilder.setFieldAsList("customSecond", Arrays.asList(new Date[]{(new DateTime("1969-01-02")).toDate(), (new DateTime("1970-02-27")).toDate()}));
        extensionBuilder.setField("customThird", new BigDecimal(5000));
        personToUpdate.addExtension(extensionBuilder.build());

        ScimResponse response = client.updatePerson(personToUpdate, this.uid, MediaType.APPLICATION_JSON);

        System.out.println(" updatePersonTest() RESPONSE = " + response.getResponseBodyString());

        assertEquals(response.getStatusCode(), 200, "cold not update the person, status != 200");

        byte[] bytes = response.getResponseBody();
        String responseStr = new String(bytes);

        User person = (User)jsonToObject(responseStr, User.class);
        assertEquals(person.getDisplayName(), updateDisplayName, "could not update the user");
    }

    @Test(groups = "d", dependsOnGroups = "c")
    public void retrievePersonTest() throws Exception {
        ScimResponse response = client.retrievePerson(this.uid, MediaType.APPLICATION_JSON);
        System.out.println(" retrievePersonTest() RESPONSE = "  + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "cold not get the person, status != 200");
    }

    @Test(dependsOnGroups = "d")
    public void deletePersonTest() throws Exception {
        ScimResponse response = client.deletePerson(this.uid);
        System.out.println(" deletePersonTest() RESPONSE = " + response.getResponseBodyString());
        assertEquals(response.getStatusCode(), 200, "cold not delete the person, status != 200");
    }

    private Object jsonToObject(String json, Class<?> clazz) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        Object clazzObject = mapper.readValue(json, clazz);
        return clazzObject;
    }

    public UserExtensionsPersonTest() {
        super();
    }
}
