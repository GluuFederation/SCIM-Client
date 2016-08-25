/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;
import gluu.scim2.client.util.Util;
import org.gluu.oxtrust.model.scim2.*;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Val Pecaoco
 */
public class EmailSync2Tests extends BaseScimTest {

    Scim2Client client;
    String id;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test(groups = "a")
    public void testCreateTwoAndUpdateWithOne() throws Exception {

        System.out.println("IN testCreateTwoAndUpdateWithOne...");

        User user = createDummyUser();

        ScimResponse response = client.createUser(user, new String[]{});
        System.out.println("response body = " + response.getResponseBodyString());

        assertEquals(response.getStatusCode(), 201, "Could not add user, status != 201");

        User userCreated = Util.toUser(response, client.getUserExtensionSchema());
        this.id = userCreated.getId();

        List<Email> emailsCreated = userCreated.getEmails();
        Assert.assertEquals(emailsCreated.size(), user.getEmails().size());

        for (Email emailCreated : emailsCreated) {
            Assert.assertNotNull(emailCreated);
            System.out.println("emailCreated.getValue() = " + emailCreated.getValue());
        }

        userCreated.setDisplayName(userCreated.getDisplayName() + " UPDATED");
        userCreated.setPassword(null);

        List<Email> emails = new ArrayList<Email>();
        Email email = new Email();
        email.setOperation("UPDATE");
        email.setPrimary(true);
        email.setValue("e@f.com");
        email.setDisplay("e@f.com");
        email.setType(Email.Type.WORK);
        email.setReference("");
        emails.add(email);
        userCreated.setEmails(emails);

        ScimResponse responseUpdated = client.updateUser(userCreated, this.id, new String[]{});
        System.out.println("UPDATED response body = " + responseUpdated.getResponseBodyString());

        Assert.assertEquals(200, responseUpdated.getStatusCode());

        User userUpdated = Util.toUser(responseUpdated, client.getUserExtensionSchema());

        assertEquals(userUpdated.getId(), this.id, "User could not be retrieved");
        assert(userUpdated.getMeta().getLastModified().getTime() > userUpdated.getMeta().getCreated().getTime());

        Email emailUpdated = userUpdated.getEmails().get(0);
        Assert.assertNotNull(emailUpdated);
        Assert.assertEquals(emails.size(), userUpdated.getEmails().size());
        Assert.assertEquals(emails.get(0).getValue(), emailUpdated.getValue());
        System.out.println("emailUpdated.getValue() = " + emailUpdated.getValue());

        System.out.println("userUpdated.getId() = " + userUpdated.getId());
        System.out.println("userUpdated.getDisplayName() = " + userUpdated.getDisplayName());
        System.out.println("userUpdated.getMeta().getLastModified().getTime() = " + userUpdated.getMeta().getLastModified().getTime());
        System.out.println("userUpdated.getMeta().getCreated().getTime() = " + userUpdated.getMeta().getCreated().getTime());

        System.out.println("LEAVING testCreateTwoAndUpdateWithOne..." + "\n");
    }

    @Test(groups = "b", dependsOnGroups = "a", alwaysRun = true)
    public void testDeleteUser() throws Exception {

        System.out.println("IN testDeleteUser...");

        ScimResponse response = client.deletePerson(this.id);
        assertEquals(response.getStatusCode(), 204, "User could not be deleted; status != 204");

        System.out.println("LEAVING testDeleteUser..." + "\n");
    }

    private User createDummyUser() {

        User user = new User();

        Name name = new Name();
        name.setGivenName("Axe");
        name.setMiddleName("Mogul");
        name.setFamilyName("Khan");
        user.setName(name);

        user.setActive(true);

        user.setUserName("axe_" + new Date().getTime());
        user.setPassword("axe");
        user.setDisplayName("Axe Mogul Khan");
        user.setNickName("Axe");
        user.setProfileUrl("");
        user.setLocale("en");
        user.setPreferredLanguage("US_en");
        user.setTitle("Axe");

        List<Email> emails = new ArrayList<Email>();
        for (int i = 1; i <= 2; i++) {
            Email email = new Email();
            email.setOperation("CREATE");
            email.setPrimary(i == 1 ? true : false);
            email.setValue(i == 1 ? "a@b.com" : "c@d.com");
            email.setDisplay(i == 1 ? "a@b.com" : "c@d.com");
            email.setType(i == 1 ? Email.Type.WORK : Email.Type.HOME);
            email.setReference("");
            emails.add(email);
        }
        user.setEmails(emails);

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
        address.setDisplay("Tonsberg, Norway");
        address.setType(Address.Type.WORK);
        address.setReference("");
        address.setStreetAddress("Tonsberg");
        address.setLocality("Tonsberg");
        address.setPostalCode("12345");
        address.setRegion("Tonsberg");
        address.setCountry("Norway");
        address.setFormatted("Tonsberg, Norway");
        addresses.add(address);
        user.setAddresses(addresses);

        return user;
    }
}
