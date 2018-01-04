/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2017, Gluu
 */
package gluu.scim2.client.singleresource;

import gluu.scim2.client.UserBaseTest;
import org.gluu.oxtrust.model.scim2.CustomAttributes;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.*;

import static org.testng.Assert.*;

import static org.gluu.oxtrust.model.scim2.Constants.USER_EXT_SCHEMA_ID;
/**
 * Created by jgomer on 2017-11-01.
 */
public class FullUserTest extends UserBaseTest {

    private UserResource user;

    @Parameters("user_full_create")
    @Test
    public void create(String json){
        logger.debug("Creating user from json...");
        user=createUserFromJson(json);

        //Confirm extended attrs info is there
        //For help on usage of org.gluu.oxtrust.model.scim2.CustomAttributes class, read its api docs (oxtrust-scim maven project)
        CustomAttributes custAttrs=user.getCustomAttributes(USER_EXT_SCHEMA_ID);

        assertNotNull(custAttrs.getValue("scimCustomFirst", String.class));
        assertNotNull(custAttrs.getValues("scimCustomSecond", Date.class));
        assertNotNull(custAttrs.getValue("scimCustomThird", Integer.class));
        assertEquals(custAttrs.getValues("scimCustomSecond", Date.class).size(), 1);

    }

    @Parameters("user_full_update")
    @Test(dependsOnMethods = "create")
    public void update(String json){

        logger.debug("Updating user {} with json", user.getUserName());
        Response response=client.updateUser(json, user.getId(), null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        UserResource other=response.readEntity(usrClass);
        CustomAttributes custAttrs1=user.getCustomAttributes(USER_EXT_SCHEMA_ID);
        CustomAttributes custAttrs2=other.getCustomAttributes(USER_EXT_SCHEMA_ID);

        //Verify scimCustomFirst changed
        assertNotEquals(custAttrs1.getValue("scimCustomFirst", String.class), custAttrs2.getValue("scimCustomFirst", String.class));

        //Verify a new scimCustomSecond value
        List<Date> col1=custAttrs1.getValues("scimCustomSecond", Date.class);
        List<Date> col2=custAttrs2.getValues("scimCustomSecond", Date.class);
        assertNotEquals(col1.size(), col2.size());

        //Verify scimCustomThird is the same
        assertEquals(custAttrs1.getValue("scimCustomThird", Integer.class), custAttrs2.getValue("scimCustomThird", Integer.class));

        //Verify change in emails, addresses and phoneNumbers
        assertNotEquals(user.getEmails().size(), other.getEmails().size());
        assertNotEquals(user.getAddresses().size(), other.getAddresses().size());
        assertNotEquals(user.getPhoneNumbers().size(), other.getPhoneNumbers().size());

        //Verify x509Certificates disappeared
        assertNull(other.getX509Certificates());

        //Verify no change in user type
        assertEquals(user.getUserType(), other.getUserType());

        user=other;

    }

    @Test(dependsOnMethods="update")
    public void updateNonExisting(){

        //Set values missing in the user so far
        user.setPreferredLanguage("en-us");
        user.setLocale("en_US");

        Response response=client.updateUser(user, user.getId(), "preferredLanguage, locale", null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        user=response.readEntity(usrClass);
        assertNotNull(user.getPreferredLanguage());
        assertNotNull(user.getLocale());

    }

    @Test(dependsOnMethods = "updateNonExisting", alwaysRun = true)
    public void delete(){
        deleteUser(user);
    }

}
