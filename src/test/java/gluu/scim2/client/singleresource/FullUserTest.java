package gluu.scim2.client.singleresource;

import gluu.scim2.client.UserBaseTest;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.Collection;
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

        //Confirm extension info is there
        Map<String, Object> custAttrs=user.getExtendedAttributes(USER_EXT_SCHEMA_ID);
        //assertEquals(custAttrs.keySet().size(),1);

        assertNotNull(custAttrs.get("scimCustomFirst"));
        assertTrue(custAttrs.get("scimCustomFirst") instanceof String);
        assertTrue(custAttrs.get("scimCustomSecond") instanceof Collection);
        assertTrue(custAttrs.get("scimCustomThird") instanceof Integer);

        Collection col=(Collection) custAttrs.get("scimCustomSecond");
        assertEquals(col.size(), 1);

    }

    @Parameters("user_full_update")
    @Test(dependsOnMethods = "create")
    public void update(String json){

        logger.debug("Updating user {} with json", user.getUserName());
        Response response=client.updateUser(json, user.getId(), null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        UserResource other=response.readEntity(usrClass);
        Map<String, Object> custAttrs1=user.getExtendedAttributes(USER_EXT_SCHEMA_ID);
        Map<String, Object> custAttrs2=other.getExtendedAttributes(USER_EXT_SCHEMA_ID);

        //Verify scimCustomFirst changed
        assertNotEquals(custAttrs1.get("scimCustomFirst"), custAttrs2.get("scimCustomFirst"));

        //Verify a new scimCustomSecond value
        Collection col1=(Collection) custAttrs1.get("scimCustomSecond");
        Collection col2=(Collection) custAttrs2.get("scimCustomSecond");
        assertNotEquals(col1.size(), col2.size());

        //Verify scimCustomThird is the same
        assertEquals(custAttrs1.get("scimCustomThird"), custAttrs2.get("scimCustomThird"));

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

        Response response=client.updateUser(user, user.getId(), "preferredLanguage, locale", null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        user=response.readEntity(usrClass);
        assertNotNull(user.getPreferredLanguage());
        assertNotNull(user.getLocale());

    }

    //@Test(dependsOnMethods = "updateNonExisting")
    public void delete(){
        deleteUser(user);
    }

}
