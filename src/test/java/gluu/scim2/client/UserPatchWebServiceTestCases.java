/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim2.client.factory.ScimClientFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.*;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.Assert;
import org.testng.annotations.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

// import gluu.scim2.client.util.Util;

/**
 * @author Shekhar Laad
 */
public class UserPatchWebServiceTestCases extends BaseScimTest {

    User userAdd;

    String id;
    String username;
    ScimClient client;
    User createdUser;

    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId", "userwebservice.add.username", "userwebservice.update.displayname"})
    @BeforeTest
    public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId, final String username, final String updateDisplayName) throws IOException {

        System.out.println(" username :  " + username + " updateDisplayName :" + updateDisplayName);

        client = ScimClientFactory.getClient(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
        initalizeUsers(username, updateDisplayName);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(userAdd);
        System.out.println("jsonInString   :  " + jsonInString);
    }

    @Test()
    public void createPersonTest() throws Exception {

        BaseClientResponse<User> response = client.createPerson(userAdd, MediaType.APPLICATION_JSON);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode(), "Could not add the user, status != 201");

        createdUser = response.getEntity();
        this.id = createdUser.getId();
        System.out.println("create id  " + id);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(createdUser);
        System.out.println("jsonInString   :  " + jsonInString);

    }

    @Test(dependsOnMethods = "createPersonTest")
    public void replacePatchPersonTest() throws Exception {
        ScimPatchUser scimPatchUser = new ScimPatchUser();
        User userPatch = patchUsers();
        Operation ope = new Operation();
        ope.setOperationName("replace");
        ope.setValue(userPatch);
        List<Operation> operations = new ArrayList<Operation>();
        operations.add(ope);
        scimPatchUser.setOperatons(operations);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(scimPatchUser);
        System.out.println("replacePatchPersonTest request    :  " + jsonInString);

        BaseClientResponse<User> response = client.patchUser(scimPatchUser, id, null);//(userToUpdate, id, MediaType.APPLICATION_JSON);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not update the user, status != 200");
        User user = response.getEntity();
        assertEquals(user.getDisplayName(), userPatch.getDisplayName(), "Could not update the user");
    }

    @Test(dependsOnMethods = "replacePatchPersonTest")
    public void addPatchPersonTest() throws Exception {
        ScimPatchUser scimPatchUser = new ScimPatchUser();
        User userPatch = patchAddUsers();
        Operation ope = new Operation();
        ope.setOperationName("add");
        ope.setValue(userPatch);
        List<Operation> operations = new ArrayList<Operation>();
        operations.add(ope);
        scimPatchUser.setOperatons(operations);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(scimPatchUser);
        System.out.println("addPatchPersonTest request    :  " + jsonInString);

        BaseClientResponse<User> response = client.patchUser(scimPatchUser, id, null);//(userToUpdate, id, MediaType.APPLICATION_JSON);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not update the user, status != 200");
        User user = response.getEntity();
        System.out.println("addPatchPersonTest : size of updated user email count :" + user.getEmails().size() + ":::     old User" + createdUser.getEmails().size());
        Assert.assertTrue(user.getEmails().size() > createdUser.getEmails().size(), "Patch is not added.");
        createdUser = user;
    }


    @Test(dependsOnMethods = "addPatchPersonTest")
    public void removePatchPersonTest() throws Exception {
        ScimPatchUser scimPatchUser = new ScimPatchUser();
        User userPatch = patchRemoveUsers();
        Operation ope = new Operation();
        ope.setOperationName("remove");
        ope.setValue(userPatch);
        List<Operation> operations = new ArrayList<Operation>();
        operations.add(ope);
        scimPatchUser.setOperatons(operations);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(scimPatchUser);
        System.out.println("removePatchPersonTest request   :  " + jsonInString);

        BaseClientResponse<User> response = client.patchUser(scimPatchUser, id, null);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not update the user, status != 200");
        User user = response.getEntity();
        System.out.println("removePatchPersonTest : size of updated user email " + user.getEmails().size() + " ::   old User   :  " + createdUser.getEmails().size());
        Assert.assertTrue(user.getEmails().size() < createdUser.getEmails().size(), "Patch is not removed.");

    }

    @AfterTest
    public void deletePersonTest() throws Exception {
        BaseClientResponse response = client.deletePerson(this.id);
        assertEquals(response.getStatus(), Response.Status.NO_CONTENT.getStatusCode(), "Could not delete the user, status != 200");
    }

    public void initalizeUsers(String username, String updateDisplayName) {

        userAdd = new User();
        userAdd.setUserName(username);
        userAdd.setPassword("testcreate1");
        userAdd.setDisplayName("Scim2DisplayName1");
        Email email = new Email();
        email.setValue("scim@gluu.org");
        email.setType(org.gluu.oxtrust.model.scim2.Email.Type.WORK);
        email.setPrimary(true);
        userAdd.getEmails().add(email);
        PhoneNumber phone = new PhoneNumber();
        phone.setType(org.gluu.oxtrust.model.scim2.PhoneNumber.Type.WORK);
        phone.setValue("654-6509-263");
        userAdd.getPhoneNumbers().add(phone);
        org.gluu.oxtrust.model.scim2.Address address = new org.gluu.oxtrust.model.scim2.Address();
        address.setCountry("US");
        address.setStreetAddress("random street");
        address.setLocality("Austin");
        address.setPostalCode("65672");
        address.setRegion("TX");
        address.setPrimary(true);
        address.setType(org.gluu.oxtrust.model.scim2.Address.Type.WORK);
        address.setFormatted(address.getStreetAddress() + " " + address.getLocality() + " " + address.getPostalCode() + " " + address.getRegion() + " " + address.getCountry());
        userAdd.getAddresses().add(address);
        userAdd.setPreferredLanguage("US_en");
        org.gluu.oxtrust.model.scim2.Name name = new org.gluu.oxtrust.model.scim2.Name();
        name.setFamilyName("SCIM");
        name.setGivenName("SCIM");
        userAdd.setName(name);
        userAdd.setDisplayName("dispalyname");

        Im im = new Im();
        im.setDisplay("imscreate");
        im.setPrimary(true);
        im.setValue("imsvalue");
        List<Im> ims = new ArrayList<Im>();
        ims.add(im);
        userAdd.setIms(ims);

        X509Certificate x509Certificate = new X509Certificate();
        x509Certificate.setDisplay("x509CertificateDisplay");
        x509Certificate.setValue("x509Certificatevalue");
        List<X509Certificate> x509Certificates = new ArrayList<X509Certificate>();
        x509Certificates.add(x509Certificate);
        userAdd.setX509Certificates(x509Certificates);
    }

    public User patchUsers() {

        userAdd = new User();
        userAdd.setPassword("test");
        userAdd.setDisplayName("Scim2patchDisplayName");
        Email email = new Email();
        email.setValue("scimpatch@gluu.org");
        email.setType(org.gluu.oxtrust.model.scim2.Email.Type.WORK);
        email.setPrimary(true);
        userAdd.getEmails().add(email);
        PhoneNumber phone = new PhoneNumber();
        phone.setType(org.gluu.oxtrust.model.scim2.PhoneNumber.Type.WORK);
        phone.setValue("654-6509-263-444");
        userAdd.getPhoneNumbers().add(phone);
        org.gluu.oxtrust.model.scim2.Address address = new org.gluu.oxtrust.model.scim2.Address();
        address.setCountry("US");
        address.setStreetAddress("patch random street");
        address.setLocality("patch Austin");
        address.setPostalCode("65672444");
        address.setRegion("patch TX");
        address.setPrimary(true);
        address.setType(org.gluu.oxtrust.model.scim2.Address.Type.WORK);
        address.setFormatted(address.getStreetAddress() + " "
                + address.getLocality() + " " + address.getPostalCode() + " "
                + address.getRegion() + " " + address.getCountry());
        userAdd.getAddresses().add(address);
        userAdd.setPreferredLanguage("US_e patch n");
        org.gluu.oxtrust.model.scim2.Name name = new org.gluu.oxtrust.model.scim2.Name();
        name.setFamilyName("patch SCIM");// getName().setFamilyName("SCIM");
        name.setGivenName("patch SCIM");
        userAdd.setName(name);

        Entitlement entitlement = new Entitlement();
        entitlement.setType(new org.gluu.oxtrust.model.scim2.Entitlement.Type(
                "WORK"));
        entitlement.setDisplay("patch");
        entitlement.setPrimary(true);
        entitlement.setValue("patchvalue");
        List<Entitlement> entitlements = new ArrayList<Entitlement>();
        userAdd.setEntitlements(entitlements);

        X509Certificate x509Certificate = new X509Certificate();
        x509Certificate.setDisplay("patch replace");
        x509Certificate.setValue("patch cert");
        List<X509Certificate> x509Certificates = new ArrayList<X509Certificate>();
        x509Certificates.add(x509Certificate);
        userAdd.setX509Certificates(x509Certificates);

        Im im = new Im();
        im.setDisplay("imspatch");
        im.setPrimary(true);
        im.setValue("imspatch");
        List<Im> ims = new ArrayList<Im>();
        ims.add(im);
        userAdd.setIms(ims);
        return userAdd;
    }

    public User patchRemoveUsers() {

        userAdd = new User();
        userAdd.setDisplayName("Scim2patchDisplayName");
        Email email = new Email();
        email.setValue("scimpatch@gluu.org");
        email.setType(org.gluu.oxtrust.model.scim2.Email.Type.WORK);
        email.setPrimary(true);
        userAdd.getEmails().add(email);
        PhoneNumber phone = new PhoneNumber();
        phone.setType(org.gluu.oxtrust.model.scim2.PhoneNumber.Type.WORK);
        phone.setValue("654-6509-263-444");
        userAdd.getPhoneNumbers().add(phone);
        org.gluu.oxtrust.model.scim2.Address address = new org.gluu.oxtrust.model.scim2.Address();
        address.setCountry("US");
        address.setStreetAddress("patch random street");
        address.setLocality("patch Austin");
        address.setPostalCode("65672444");
        address.setRegion("patch TX");
        address.setPrimary(true);
        address.setType(org.gluu.oxtrust.model.scim2.Address.Type.WORK);
        address.setFormatted(address.getStreetAddress() + " "
                + address.getLocality() + " " + address.getPostalCode() + " "
                + address.getRegion() + " " + address.getCountry());
        userAdd.getAddresses().add(address);
        userAdd.setPreferredLanguage("US_e patch n");
        org.gluu.oxtrust.model.scim2.Name name = new org.gluu.oxtrust.model.scim2.Name();
        name.setFamilyName("patch SCIM");
        name.setGivenName("patch SCIM");
        userAdd.setName(name);
        userAdd.setDisplayName("patchdispalyname");

        Entitlement entitlement = new Entitlement();
        entitlement.setType(new org.gluu.oxtrust.model.scim2.Entitlement.Type(
                "WORK"));
        entitlement.setDisplay("patch");
        entitlement.setPrimary(true);
        entitlement.setValue("patchvalue");
        List<Entitlement> entitlements = new ArrayList<Entitlement>();
        userAdd.setEntitlements(entitlements);

        Im im = new Im();
        im.setDisplay("imspatch");
        im.setPrimary(true);
        im.setValue("imspatch");
        List<Im> ims = new ArrayList<Im>();
        ims.add(im);
        userAdd.setIms(ims);
        return userAdd;
    }

    public User patchAddUsers() {

        userAdd = new User();
        userAdd.setDisplayName("Scim2addDisplayName");
        Email email = new Email();
        email.setValue("scimadd@gluu.org");
        email.setType(org.gluu.oxtrust.model.scim2.Email.Type.WORK);
        email.setPrimary(true);
        userAdd.getEmails().add(email);
        PhoneNumber phone = new PhoneNumber();
        phone.setType(org.gluu.oxtrust.model.scim2.PhoneNumber.Type.WORK);
        phone.setValue("654-6509-263-444");
        userAdd.getPhoneNumbers().add(phone);
        org.gluu.oxtrust.model.scim2.Address address = new org.gluu.oxtrust.model.scim2.Address();
        address.setCountry("US");
        address.setStreetAddress("add random street");
        address.setLocality("add Austin");
        address.setPostalCode("65672444");
        address.setRegion("add TX");
        address.setPrimary(true);
        address.setType(org.gluu.oxtrust.model.scim2.Address.Type.WORK);
        address.setFormatted(address.getStreetAddress() + " "
                + address.getLocality() + " " + address.getPostalCode() + " "
                + address.getRegion() + " " + address.getCountry());
        userAdd.getAddresses().add(address);
        userAdd.setPreferredLanguage("US_e add n");
        org.gluu.oxtrust.model.scim2.Name name = new org.gluu.oxtrust.model.scim2.Name();
        name.setFamilyName("add SCIM");
        name.setGivenName("add SCIM");
        userAdd.setName(name);
        userAdd.setDisplayName("adddispalyname");

        Entitlement entitlement = new Entitlement();
        entitlement.setType(new org.gluu.oxtrust.model.scim2.Entitlement.Type(
                "WORK"));
        entitlement.setDisplay("add");
        entitlement.setPrimary(true);
        entitlement.setValue("addvalue");
        List<Entitlement> entitlements = new ArrayList<Entitlement>();
        userAdd.setEntitlements(entitlements);

        X509Certificate x509Certificate = new X509Certificate();
        x509Certificate.setDisplay("add replace");
        x509Certificate.setValue("add cert");
        List<X509Certificate> x509Certificates = new ArrayList<X509Certificate>();
        x509Certificates.add(x509Certificate);
        userAdd.setX509Certificates(x509Certificates);

        Im im = new Im();
        im.setDisplay("imsadd");
        im.setPrimary(true);
        im.setValue("imsadd");
        List<Im> ims = new ArrayList<Im>();
        ims.add(im);
        userAdd.setIms(ims);
        return userAdd;
    }

}
