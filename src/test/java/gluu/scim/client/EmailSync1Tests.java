/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client;

import gluu.BaseScimTest;
import gluu.scim.client.model.*;
import gluu.scim2.client.util.Util;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Val Pecaoco
 */
public class EmailSync1Tests extends BaseScimTest {

    String domainURL;
    ScimClient client;

    String id;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws Exception {
        this.domainURL = domainURL;
        String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
        client = ScimClient.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
    }

    @Test(groups = "a")
    public void testCreateOneAndUpdateWithTwo() throws Exception {

        System.out.println("IN testCreateOneAndUpdateWithTwo...");

        ScimPerson person = createDummyPerson();

        ScimResponse response = client.createPerson(person, MediaType.APPLICATION_JSON);

        assertEquals(response.getStatusCode(), 201, "Could not add person, status != 201");

        ScimPerson personCreated = (ScimPerson) Util.jsonToObject(response, ScimPerson.class);
        this.id = personCreated.getId();

        Assert.assertEquals(personCreated.getEmails().size(), person.getEmails().size());

        ScimPersonEmails emailCreated = personCreated.getEmails().get(0);
        Assert.assertNotNull(emailCreated);
        Assert.assertEquals(personCreated.getEmails().get(0).getValue(), emailCreated.getValue());
        System.out.println("emailCreated.getValue() = " + emailCreated.getValue());

        personCreated.setDisplayName(personCreated.getDisplayName() + " UPDATED");
        personCreated.setPassword(null);

        List<ScimPersonEmails> emails = new ArrayList<ScimPersonEmails>();
        for (int i = 1; i <= 2; i++) {
            ScimPersonEmails scimPersonEmail = new ScimPersonEmails();
            scimPersonEmail.setPrimary(i == 1 ? "true" : "false");
            scimPersonEmail.setValue(i == 1 ? "c@d.com" : "e@f.com");
            scimPersonEmail.setType("other");
            emails.add(scimPersonEmail);
        }
        personCreated.setEmails(emails);

        ScimResponse responseUpdated = client.updatePerson(personCreated, this.id, MediaType.APPLICATION_JSON);

        Assert.assertEquals(200, responseUpdated.getStatusCode());

        ScimPerson personUpdated = (ScimPerson) Util.jsonToObject(responseUpdated, ScimPerson.class);

        assertEquals(personUpdated.getId(), this.id, "Person could not be retrieved");

        List<ScimPersonEmails> emailsUpdated = personUpdated.getEmails();
        Assert.assertEquals(emails.size(), emailsUpdated.size());

        for (ScimPersonEmails emailUpdated : emailsUpdated) {
            Assert.assertNotNull(emailUpdated);
            System.out.println("emailUpdated.getValue() = " + emailUpdated.getValue());
        }

        System.out.println("UPDATED response body = " + responseUpdated.getResponseBodyString());
        System.out.println("personUpdated.getId() = " + personUpdated.getId());
        System.out.println("personUpdated.getDisplayName() = " + personUpdated.getDisplayName());

        System.out.println("LEAVING testCreateOneAndUpdateWithTwo..." + "\n");
    }

    @Test(groups = "b", dependsOnGroups = "a", alwaysRun = true)
    public void testDeletePerson() throws Exception {

        System.out.println("IN testDeletePerson...");

        ScimResponse response = client.deletePerson(this.id);
        assertEquals(response.getStatusCode(), 200, "Person could not be deleted, status != 200");

        System.out.println("LEAVING testDeletePerson..." + "\n");
    }

    private ScimPerson createDummyPerson() {

        ScimPerson person = new ScimPerson();

        List<String> schema = new ArrayList<String>();
        schema.add("urn:scim:schemas:core:1.0");
        person.setSchemas(schema);

        ScimName name = new ScimName();
        name.setGivenName("Alexander");
        name.setFamilyName("Alekhine");
        person.setName(name);

        person.setActive("active");

        person.setUserName("sasha_" + new Date().getTime());
        person.setPassword("test");
        person.setDisplayName("Alexander Alekhine");
        person.setNickName("Sasha");
        person.setProfileUrl("");
        person.setLocale("en");
        person.setPreferredLanguage("US_en");
        person.setTitle("Super GM");

        ScimPersonEmails email = new ScimPersonEmails();
        email.setValue("a@b.com");
        email.setType("work");
        email.setPrimary("true");
        person.getEmails().add(email);

        ScimPersonPhones phone = new ScimPersonPhones();
        phone.setType("work");
        phone.setValue("123-4567-890");
        person.getPhoneNumbers().add(phone);

        ScimPersonAddresses address = new ScimPersonAddresses();
        address.setCountry("Russia");
        address.setStreetAddress("Moskva");
        address.setLocality("Moskva");
        address.setPostalCode("12345");
        address.setRegion("Moskva");
        address.setPrimary("true");
        address.setType("work");
        address.setFormatted(address.getStreetAddress() + " " + address.getLocality() + " " + address.getPostalCode() + " " + address.getRegion() + " " + address.getCountry());
        person.getAddresses().add(address);

        return person;
    }
}
