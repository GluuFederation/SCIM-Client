package gluu.scim2.client.patch;

import gluu.scim2.client.UserBaseTest;
import org.gluu.oxtrust.model.scim2.user.UserResource;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.*;

import static org.testng.Assert.*;

/**
 * Created by jgomer on 2017-11-02.
 */
public class PatchDeleteUserTest extends UserBaseTest{

    private UserResource user;

    @Parameters({"user_full_create"})
    @Test
    public void create(String json){
        logger.debug("Creating user from json...");
        user=createUserFromJson(json);
    }

    @Parameters({"user_patchdelete"})
    @Test(dependsOnMethods = "create")
    public void delete1(String patchRequest){

        Response response = client.patchUser(patchRequest, user.getId(), null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        UserResource other=response.readEntity(usrClass);
        assertNull(other.getName().getMiddleName());
        assertNull(other.getNickName());
        assertNull(other.getEntitlements());

        assertNull(other.getAddresses().get(0).getPostalCode());
        assertNull(other.getAddresses().get(0).getLocality());
        assertNotNull(other.getAddresses().get(0).getStreetAddress());
    }

    @Test(dependsOnMethods = "delete1", alwaysRun = true)
    public void delete(){
        deleteUser(user);
    }

}
