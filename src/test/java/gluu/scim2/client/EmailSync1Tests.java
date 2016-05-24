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
import org.gluu.oxtrust.model.scim2.*;
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
    Scim2Client client;

    String id;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws Exception {
        this.domainURL = domainURL;
        String umaAatClientJwksData = FileUtils.readFileToString(new File(umaAatClientJwks));
        client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJwksData, umaAatClientKeyId);
    }

    @Test(groups = "a")
    public void testCreateOneAndUpdateWithTwo() throws Exception {

        System.out.println("IN testCreateOneAndUpdateWithTwo...");

        User user = createDummyUser();

        ScimResponse response = client.createPerson(user, MediaType.APPLICATION_JSON);

        assertEquals(response.getStatusCode(), 201, "Could not add user, status != 201");

        User userCreated = Util.toUser(response, client.getUserExtensionSchema());
        this.id = userCreated.getId();

        Assert.assertEquals(userCreated.getEmails().size(), user.getEmails().size());

        Email emailCreated = userCreated.getEmails().get(0);
        Assert.assertNotNull(emailCreated);
        Assert.assertEquals(userCreated.getEmails().get(0).getValue(), emailCreated.getValue());
        System.out.println("emailCreated.getValue() = " + emailCreated.getValue());

        userCreated.setDisplayName(userCreated.getDisplayName() + " UPDATED");
        userCreated.setPassword(null);

        List<Email> emails = new ArrayList<Email>();
        for (int i = 1; i <= 2; i++) {
            Email email = new Email();
            email.setOperation("UPDATE");
            email.setPrimary(i == 1 ? true : false);
            email.setValue(i == 1 ? "c@d.com" : "e@f.com");
            email.setDisplay(i == 1 ? "c@d.com" : "e@f.com");
            email.setType(i == 1 ? Email.Type.WORK : Email.Type.HOME);
            email.setReference("");
            emails.add(email);
        }
        userCreated.setEmails(emails);

        ScimResponse responseUpdated = client.updatePerson(userCreated, this.id, MediaType.APPLICATION_JSON);

        Assert.assertEquals(200, responseUpdated.getStatusCode());

        User userUpdated = Util.toUser(responseUpdated, client.getUserExtensionSchema());

        assertEquals(userUpdated.getId(), this.id, "User could not be retrieved");

        List<Email> emailsUpdated = userUpdated.getEmails();
        Assert.assertEquals(emails.size(), emailsUpdated.size());

        for (Email emailUpdated : emailsUpdated) {
            Assert.assertNotNull(emailUpdated);
            System.out.println("emailUpdated.getValue() = " + emailUpdated.getValue());
        }

        System.out.println("UPDATED response body = " + responseUpdated.getResponseBodyString());
        System.out.println("userUpdated.getId() = " + userUpdated.getId());
        System.out.println("userUpdated.getDisplayName() = " + userUpdated.getDisplayName());
        System.out.println("userUpdated.getMeta().getLastModified().getTime() = " + userUpdated.getMeta().getLastModified().getTime());
        System.out.println("userUpdated.getMeta().getCreated().getTime() = " + userUpdated.getMeta().getCreated().getTime());

        System.out.println("LEAVING testCreateOneAndUpdateWithTwo..." + "\n");
    }

    @Test(groups = "b", dependsOnGroups = "a", alwaysRun = true)
    public void testDeleteUser() throws Exception {

        System.out.println("IN testDeleteUser...");

        ScimResponse response = client.deletePerson(this.id);
        assertEquals(response.getStatusCode(), 200, "User could not be deleted, status != 200");

        System.out.println("LEAVING testDeleteUser..." + "\n");
    }

    private User createDummyUser() {

        User user = new User();

        Name name = new Name();
        name.setGivenName("Ursa");
        name.setFamilyName("Warrior");
        user.setName(name);

        user.setActive(true);

        user.setUserName("ursa_" + new Date().getTime());
        user.setPassword("ursa");
        user.setDisplayName("Ursa Warrior");
        user.setNickName("Fuzzy Wuzzy");
        user.setProfileUrl("");
        user.setLocale("en");
        user.setPreferredLanguage("US_en");
        user.setTitle("Ursa");

        Email email = new Email();
        email.setOperation("CREATE");
        email.setPrimary(true);
        email.setValue("a@b.com");
        email.setDisplay("a@b.com");
        email.setType(Email.Type.WORK);
        email.setReference("");
        user.getEmails().add(email);

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setOperation("CREATE");
        phoneNumber.setPrimary(true);
        phoneNumber.setValue("123-456-7890");
        phoneNumber.setDisplay("123-456-7890");
        phoneNumber.setType(PhoneNumber.Type.WORK);
        phoneNumber.setReference("");
        phoneNumbers.add(phoneNumber);
        user.setPhoneNumbers(phoneNumbers);

        List<Address> addresses = new ArrayList<Address>();
        Address address = new Address();
        address.setOperation("CREATE");
        address.setPrimary(true);
        address.setValue("test");
        address.setDisplay("Baku, Azerbaijan");
        address.setType(Address.Type.WORK);
        address.setReference("");
        address.setStreetAddress("Baku");
        address.setLocality("Baku");
        address.setPostalCode("12345");
        address.setRegion("Baku");
        address.setCountry("Azerbaijan");
        address.setFormatted("Baku, Azerbaijan");
        addresses.add(address);
        user.setAddresses(addresses);

        return user;
    }
}
