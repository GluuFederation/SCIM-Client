package gluu.scim2.client;

import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.testng.Assert;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.*;

import static org.testng.Assert.*;

/**
 * Created by jgomer on 2017-10-23.
 */
public class UserBaseTest extends BaseTest {

    protected static final Class<UserResource> usrClass=UserResource.class;

    public UserResource createUserFromJson(String json){

        Response response=client.createUser(json, null, null, null);
        assertEquals(response.getStatus(), CREATED.getStatusCode());

        UserResource user=response.readEntity(usrClass);
        assertNotNull(user.getMeta());
        logger.debug("User created with id {}", user.getId());

        return user;
    }

    public void deleteUser(UserResource user){

        logger.debug("Deleting user {}", user.getUserName());
        Response response=client.deleteUser(user.getId(), null);
        assertEquals(response.getStatus(), NO_CONTENT.getStatusCode());
        response.close();
        logger.debug("deleted");

    }

}
