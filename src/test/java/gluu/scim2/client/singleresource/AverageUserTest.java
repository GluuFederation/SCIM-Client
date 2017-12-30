/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2017, Gluu
 */
package gluu.scim2.client.singleresource;

import gluu.scim2.client.UserBaseTest;
import org.gluu.oxtrust.model.scim2.user.Group;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;

import static javax.ws.rs.core.Response.Status.*;

import static org.testng.Assert.*;

/**
 * Created by jgomer on 2017-10-22.
 */
public class AverageUserTest extends UserBaseTest {

    private UserResource user;

    @Parameters("user_average_create")
    @Test
    public void create(String json){
        logger.debug("Creating user from json...");
        user=createUserFromJson(json);
    }

    @Parameters("user_average_update")
    @Test(dependsOnMethods="create")
    public void updateWithJson(String json){

        logger.debug("Updating user {} with json", user.getUserName());
        Response response=client.updateUser(json, user.getId(), null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        user=response.readEntity(usrClass);
        assertTrue(user.getRoles().size()>0);
        assertTrue(user.getPhoneNumbers().size()>1);

        //Attest there is at most ONE primary phone number despite any data passed
        long primaryTrueCount=user.getPhoneNumbers().stream().map(pn -> pn.getPrimary()==null ? false : pn.getPrimary()).
                filter(Boolean::booleanValue).count();
        assertTrue(primaryTrueCount < 2);

        logger.debug("Updated user {}", user.getName().getGivenName());

    }

    @Test(dependsOnMethods="updateWithJson")
    public void updateWithObject() throws Exception{

        UserResource clone=getDeepCloneUsr(user);
        clone.setPreferredLanguage("en_US");
        clone.getPhoneNumbers().remove(0);
        clone.setAddresses(null);   //Means no change
        clone.setRoles(new ArrayList<>());  //Means role will have to disappear

        Group group=new Group();
        group.setValue("Dummy ID");
        clone.setGroups(Arrays.asList(group));  //will be ignored: group membership changes MUST be applied via /Groups endpoint

        logger.debug("Updating user {}", clone.getUserName());
        Response response=client.updateUser(clone, clone.getId(), null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        user=response.readEntity(usrClass);
        assertNotNull(user.getPreferredLanguage());
        assertEquals(user.getPreferredLanguage(), clone.getPreferredLanguage());
        assertEquals(user.getPhoneNumbers().size(), clone.getPhoneNumbers().size());
        assertTrue(user.getAddresses().size()>0);
        assertNull(user.getRoles());
        assertNull(user.getGroups());

        logger.debug("Updated user {}", user.getName().getGivenName());

    }

    @Test(dependsOnMethods="updateWithObject", alwaysRun = true)
    public void delete(){
        deleteUser(user);
    }

}
