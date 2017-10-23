package gluu.scim2.client.singleresource;

import gluu.scim2.client.BaseTest;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.*;

/**
 * Created by jgomer on 2017-10-21.
 */
public class MinimalUserTest extends BaseTest {

    private UserResource user;
    private static final Class<UserResource> usrClass=UserResource.class;

    @Parameters("user_minimal_create")
    @Test
    public void create(String json){

        logger.debug("Creating mimimal user from json...");
        Response response=client.createUser(json, null, null, null);
        Assert.assertEquals(response.getStatus(), CREATED.getStatusCode());

        user=response.readEntity(usrClass);
        Assert.assertNotNull(user.getMeta());
        logger.debug("User created with id {}", user.getId());

    }

    @Parameters("user_minimal_update")
    @Test(dependsOnMethods="create")
    public void update(String json){

        logger.debug("Updating user {} with json", user.getUserName());
        Response response=client.updateUser(json, user.getId(), null, null, null);
        Assert.assertEquals(response.getStatus(), OK.getStatusCode());

        user=response.readEntity(usrClass);
        Assert.assertNotNull(user.getName());
        Assert.assertTrue(user.isActive());
        logger.debug("Updated user {}", user.getName().getGivenName());

    }

    @Test(dependsOnMethods="update")
    public void delete(){

        logger.debug("Deleting user {}", user.getUserName());
        Response response=client.deleteUser(user.getId(), null);
        Assert.assertEquals(response.getStatus(), NO_CONTENT.getStatusCode());
        logger.debug("deleted");

    }

}
