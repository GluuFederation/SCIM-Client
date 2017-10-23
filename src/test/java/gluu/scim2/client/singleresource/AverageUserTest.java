package gluu.scim2.client.singleresource;

import gluu.scim2.client.BaseTest;
import org.gluu.oxtrust.model.scim2.user.Group;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;

import static javax.ws.rs.core.Response.Status.*;

/**
 * Created by jgomer on 2017-10-22.
 */
public class AverageUserTest extends BaseTest {

    private UserResource user;
    private static final Class<UserResource> usrClass=UserResource.class;

    @Parameters("user_average_create")
    @Test
    public void create(String json){

        logger.debug("Creating user from json...");
        Response response=client.createUser(json, null, null, null);
        Assert.assertEquals(response.getStatus(), CREATED.getStatusCode());

        user=response.readEntity(usrClass);
        Assert.assertNotNull(user.getMeta());
        logger.debug("User created with id {}", user.getId());

    }

    @Parameters("user_average_update")
    @Test(dependsOnMethods="create")
    public void updateWithJson(String json){

        logger.debug("Updating user {} with json", user.getUserName());
        Response response=client.updateUser(json, user.getId(), null, null, null);
        Assert.assertEquals(response.getStatus(), OK.getStatusCode());

        user=response.readEntity(usrClass);
        Assert.assertTrue(user.getRoles().size()>0);
        Assert.assertTrue(user.getPhoneNumbers().size()>1);
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
        Response response=client.updateUser(clone, clone.getId(), null, null, null);
        Assert.assertEquals(response.getStatus(), OK.getStatusCode());

        user=response.readEntity(usrClass);
        Assert.assertNotNull(user.getPreferredLanguage());
        Assert.assertEquals(user.getPreferredLanguage(), clone.getPreferredLanguage());
        Assert.assertEquals(user.getPhoneNumbers().size(), clone.getPhoneNumbers().size());
        Assert.assertTrue(user.getAddresses().size()>0);
        Assert.assertNull(user.getRoles());
        Assert.assertNull(user.getGroups());
        logger.debug("Updated user {}", user.getName().getGivenName());

    }

}
