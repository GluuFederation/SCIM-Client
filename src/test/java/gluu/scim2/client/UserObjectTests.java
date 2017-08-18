/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim2.client.factory.ScimClientFactory;
import org.gluu.oxtrust.model.scim2.*;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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
public class UserObjectTests extends BaseScimTest {

    ScimClient client;
    String id;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        client = ScimClientFactory.getClient(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test(groups = "a")
    public void testCreateUser() throws Exception {

        System.out.println("IN testCreateUser...");

        User user = createDummyUser();

        BaseClientResponse<User> response = client.createPerson(user, MediaType.APPLICATION_JSON);

        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode(), "Could not add user, status != 201");

        User userCreated = response.getEntity();
        this.id = userCreated.getId();

        System.out.println("userCreated.getId() = " + userCreated.getId());
        System.out.println("userCreated.getDisplayName() = " + userCreated.getDisplayName());

        System.out.println("LEAVING testCreateUser..." + "\n");
    }

    @Test(groups = "b", dependsOnGroups = "a")
    public void testRetrieveNewUser() throws Exception {

        System.out.println("IN testRetrieveNewUser...");

        BaseClientResponse<User> response = client.retrievePerson(this.id, MediaType.APPLICATION_JSON);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        User userRetrieved = response.getEntity();
        assertEquals(userRetrieved.getId(), this.id, "User could not be retrieved");

        System.out.println("userRetrieved.getId() = " + userRetrieved.getId());
        System.out.println("userRetrieved.getDisplayName() = " + userRetrieved.getDisplayName());

        System.out.println("LEAVING testRetrieveNewUser..." + "\n");
    }

    @Test(groups = "c", dependsOnGroups = "b")
    public void testUpdateNewUser() throws Exception {

        System.out.println("IN testUpdateNewUser...");

        Thread.sleep(3000);  // Sleep for 3 seconds

        BaseClientResponse<User> response = client.retrievePerson(this.id, MediaType.APPLICATION_JSON);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        User userRetrieved = response.getEntity();

        userRetrieved.setDisplayName(userRetrieved.getDisplayName() + " UPDATED");
        userRetrieved.setPassword(null);

        BaseClientResponse<User> responseUpdated = client.updatePerson(userRetrieved, this.id, MediaType.APPLICATION_JSON);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), responseUpdated.getStatus());

        User userUpdated = responseUpdated.getEntity();

        assertEquals(userUpdated.getId(), this.id, "User could not be retrieved");
        assert(userUpdated.getMeta().getLastModified().getTime() > userUpdated.getMeta().getCreated().getTime());

        System.out.println("userUpdated.getId() = " + userUpdated.getId());
        System.out.println("userUpdated.getDisplayName() = " + userUpdated.getDisplayName());
        System.out.println("userUpdated.getMeta().getLastModified().getTime() = " + userUpdated.getMeta().getLastModified().getTime());
        System.out.println("userUpdated.getMeta().getCreated().getTime() = " + userUpdated.getMeta().getCreated().getTime());

        System.out.println("LEAVING testUpdateNewUser..." + "\n");
    }

    @Test(groups = "d", dependsOnGroups = "c")
    public void testUpdateUserNameDifferentId() throws Exception {

        System.out.println("IN testUpdateUserNameDifferentId...");

        BaseClientResponse<User> response = client.retrievePerson(this.id, MediaType.APPLICATION_JSON);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        User userRetrieved = response.getEntity();

        userRetrieved.setUserName("aaaa1111");
        userRetrieved.setPassword(null);

        BaseClientResponse<User> responseUpdated = client.updatePerson(userRetrieved, this.id, MediaType.APPLICATION_JSON);

        Assert.assertEquals(Response.Status.CONFLICT.getStatusCode(), responseUpdated.getStatus());

        System.out.println("LEAVING testUpdateUserNameDifferentId..." + "\n");
    }

    @Test(groups = "e", dependsOnGroups = "d", alwaysRun = true)
    public void testDeleteUser() throws Exception {

        System.out.println("IN testDeleteUser...");

        BaseClientResponse response = client.deletePerson(this.id);
        assertEquals(response.getStatus(), Response.Status.NO_CONTENT.getStatusCode(), "User could not be deleted; status != 204");

        System.out.println("LEAVING testDeleteUser..." + "\n");
    }

    @Test(dependsOnGroups = "e", alwaysRun = true)
    public void testUserDeserializerGroups() throws Exception {

        System.out.println("IN testUserDeserializerGroups...");

        String filter = "userName eq \"admin\"";
        int startIndex = 1;
        int count = 1;
        String sortBy = "";
        String sortOrder = "";
        String[] attributes = null;

        // GET search on /scim/v2/Users
        BaseClientResponse<ListResponse> response = client.searchUsers(filter, startIndex, count, sortBy, sortOrder, attributes);

        Assert.assertEquals(200, response.getStatus());

        ListResponse listResponse = response.getEntity();
        assertEquals(listResponse.getTotalResults(), 1);

        User userRetrieved = (User) listResponse.getResources().get(0);
        assertEquals(userRetrieved.getUserName(), "admin", "User could not be retrieved");

        System.out.println("userRetrieved.getId() = " + userRetrieved.getId());
        System.out.println("userRetrieved.getDisplayName() = " + userRetrieved.getDisplayName());

        List<GroupRef> groups = userRetrieved.getGroups();
        for (GroupRef group : groups) {
            System.out.println("group inum = " + group.getValue());
            System.out.println("group $ref = " + group.getReference());
            Assert.assertNotNull(group.getReference());
        }

        System.out.println("LEAVING testUserDeserializerGroups..." + "\n");
    }

    private User createDummyUser() {

        User user = new User();

        Name name = new Name();
        name.setGivenName("Jose Raul");
        name.setMiddleName("Graupera");
        name.setFamilyName("Capablanca");
        user.setName(name);

        user.setActive(true);

        user.setUserName("chessMachine_" + new Date().getTime());
        user.setPassword("worldChampion");
        user.setDisplayName("Jose Raul Capablanca");
        user.setNickName("Capa");
        user.setProfileUrl("");
        user.setLocale("en");
        user.setPreferredLanguage("US_en");
        user.setTitle("GM");

        List<Email> emails = new ArrayList<Email>();
        Email email = new Email();
        email.setOperation("CREATE");
        email.setPrimary(true);
        email.setValue("a@b.com");
        email.setDisplay("a@b.com");
        email.setType(Email.Type.WORK);
        email.setReference("");
        emails.add(email);
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
        address.setDisplay("Havana, Cuba");
        address.setType(Address.Type.WORK);
        address.setReference("");
        address.setStreetAddress("Havana");
        address.setLocality("Havana");
        address.setPostalCode("12345");
        address.setRegion("Cuba");
        address.setCountry("CU");
        address.setFormatted("Havana, Cuba");
        addresses.add(address);
        user.setAddresses(addresses);

        return user;
    }
}
