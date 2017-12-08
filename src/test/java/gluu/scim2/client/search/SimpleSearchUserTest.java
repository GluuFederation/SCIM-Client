package gluu.scim2.client.search;

import gluu.scim2.client.UserBaseTest;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.SearchRequest;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.*;

import static org.testng.Assert.*;

/**
 * Created by jgomer on 2017-10-23.
 */
public class SimpleSearchUserTest extends UserBaseTest {

    private UserResource user;

    @Parameters("user_average_create")
    @Test
    public void create(String json){
        logger.debug("Creating user from json...");
        user=createUserFromJson(json);
    }

    @Test(dependsOnMethods="create")
    public void searchSimpleAttrGet(){

        String locale=user.getLocale();
        logger.debug("Searching user with attribute locale = {} using GET verb", locale);

        Response response=client.searchUsers("locale eq \"" + locale + "\"",
                null, null, null, null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        ListResponse listResponse=response.readEntity(ListResponse.class);
        assertTrue(listResponse.getResources().size()>0);

        //Retrieve first user in results
        UserResource same=listResponse.getResources().stream().map(usrClass::cast).findFirst().get();
        assertEquals(same.getLocale(), locale);

    }

    @Test(dependsOnMethods="create")
    public void searchComplexAttrPost(){

        String givenName=user.getName().getGivenName();
        logger.debug("Searching user with attribute givenName = {} using POST verb", givenName);

        SearchRequest sr=new SearchRequest();
        sr.setFilter("name.givenName eq \""+ givenName + "\"");
        Response response=client.searchUsersPost(sr);
        assertEquals(response.getStatus(), OK.getStatusCode());

        ListResponse listResponse=response.readEntity(ListResponse.class);
        assertTrue(listResponse.getResources().size()>0);

        //Retrieve first user in results
        UserResource other =listResponse.getResources().stream().map(usrClass::cast).findFirst().get();
        assertEquals(other.getName().getGivenName(), givenName);

    }

    @Test(dependsOnMethods="create")
    public void searchComplexMultivaluedPost(){

        String ghost = user.getEmails().get(0).getValue();
        final String host = ghost.substring(ghost.indexOf("@")+1);
        logger.debug("Searching user with attribute emails.value like {} using POST verb", host);

        SearchRequest sr=new SearchRequest();
        sr.setFilter("emails.value ew \"" + host + "\"");
        Response response=client.searchUsersPost(sr);
        assertEquals(response.getStatus(), OK.getStatusCode());

        ListResponse listResponse=response.readEntity(ListResponse.class);
        assertTrue(listResponse.getResources().size()>0);

        //Retrieve first user in results
        UserResource other=listResponse.getResources().stream().map(usrClass::cast).findFirst().get();
        assertTrue(other.getEmails().stream().anyMatch(mail -> mail.getValue().endsWith(host)));

    }

    @Test(dependsOnMethods="create", groups = "search")
    public void searchNoResults(){

        logger.debug("Calculating the total number of users");
        //Pass count=0 so no results are retrieved (only total)
        Response response=client.searchUsers("userName pr", null, 0, null, null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        ListResponse listResponse=response.readEntity(ListResponse.class);
        assertNull(listResponse.getResources());

        assertTrue(listResponse.getTotalResults()>0);
        logger.debug("There are {} users!", listResponse.getTotalResults());

    }

    @Test(dependsOnGroups = "search", alwaysRun = true)
    public void delete(){
        deleteUser(user);
    }

}
