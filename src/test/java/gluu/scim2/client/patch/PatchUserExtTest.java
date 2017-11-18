package gluu.scim2.client.patch;

import gluu.scim2.client.UserBaseTest;

import org.gluu.oxtrust.model.scim2.PatchOperation;
import org.gluu.oxtrust.model.scim2.PatchRequest;
import org.gluu.oxtrust.model.scim2.user.PhoneNumber;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.gluu.oxtrust.model.scim2.util.DateUtil;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.ws.rs.core.Response.Status.*;

import static org.gluu.oxtrust.model.scim2.Constants.USER_EXT_SCHEMA_ID;
import static org.testng.Assert.*;

/**
 * Created by jgomer on 2017-11-02.
 */
public class PatchUserExtTest extends UserBaseTest {

    private UserResource user;

    @Parameters({"user_full_create"})
    @Test
    public void create(String json) {
        logger.debug("Creating user from json...");
        user=createUserFromJson(json);
    }

    @Parameters({"user_patch_ext"})
    @Test(dependsOnMethods = "create")
    public void patchJson(String patchRequest){

        Response response = client.patchUser(patchRequest, user.getId(), null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        UserResource other=response.readEntity(usrClass);

        Map<String, Object> custAttrs=other.getExtendedAttributes(USER_EXT_SCHEMA_ID);

        //Verify new items appeared in scimCustomSecond
        Collection scimCustomSecond=(Collection)custAttrs.get("scimCustomSecond");
        assertEquals(scimCustomSecond.size(), 6);

        //Verify change in value of scimCustomThird
        Integer scimCustomThird=(Integer)custAttrs.get("scimCustomThird");
        assertEquals(new Integer(1), scimCustomThird);

        //Verify scimCustomFirst disappeared
        assertNull(custAttrs.get("scimCustomFirst"));

        //Verify some others disappeared too
        assertNull(other.getAddresses().get(0).getType());
        assertNull(other.getName().getGivenName());

        Stream<String> types=other.getPhoneNumbers().stream().map(PhoneNumber::getType);
        assertTrue(types.map(Optional::ofNullable).noneMatch(Optional::isPresent));

    }

    @Test(dependsOnMethods = "patchJson")
    public void patchObject(){

        PatchOperation operation=new PatchOperation();
        operation.setOperation("replace");
        operation.setPath("urn:ietf:params:scim:schemas:extension:gluu:2.0:User:scimCustomSecond");

        long now=System.currentTimeMillis();
        List<String> someDates= Arrays.asList(now, now+1000, now+2000, now+3000).stream()
                .map(DateUtil::millisToISOString).collect(Collectors.toList());
        operation.setValue(someDates);

        PatchRequest pr=new PatchRequest();
        pr.setOperations(Collections.singletonList(operation));

        Response response=client.patchUser(pr, user.getId(), null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        UserResource other=response.readEntity(usrClass);
        Map<String, Object> custAttrs=other.getExtendedAttributes(USER_EXT_SCHEMA_ID);

        //Verify different dates appeared in scimCustomSecond
        List<String> scimCustomSecond=(List<String>) custAttrs.get("scimCustomSecond");
        assertEquals(scimCustomSecond.size(), someDates.size());

        String aStrDate=scimCustomSecond.get(0);    //This is coming in ISO format with offset (see DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        long instant=Instant.from(OffsetDateTime.parse(aStrDate)).toEpochMilli();
        assertEquals(now, instant);

    }

    //@Test(dependsOnMethods = "patchObject")
    public void delete(){
        deleteUser(user);
    }

}