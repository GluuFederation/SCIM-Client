package gluu.scim2.client.patch;

import gluu.scim2.client.BaseTest;
import org.gluu.oxtrust.model.scim2.*;
import org.gluu.oxtrust.model.scim2.group.GroupResource;
import org.gluu.oxtrust.model.scim2.group.Member;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.gluu.oxtrust.model.scim2.util.ScimResourceUtil;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.*;

import static org.testng.Assert.*;

/**
 * NOTES:
 * Check first if /install/community-edition-setup/templates/test/scim-client/data/scim-test-data.ldif has been loaded to LDAP.
 *
 * Created by jgomer on 2017-11-13.
 */
public class PatchGroupTest extends BaseTest{

    private GroupResource group;
    private static final Class<GroupResource> groupCls=GroupResource.class;
    private static final Class<UserResource> usrClass=UserResource.class;

    @Parameters("group_minimal_create")
    @Test
    public void create(String json){

        logger.debug("Creating group from json...");
        Response response = client.createGroup(json, null, null, null);
        assertEquals(response.getStatus(), CREATED.getStatusCode());

        group=response.readEntity(groupCls);
    }

    @Parameters("group_patch")
    @Test(dependsOnMethods = "create")
    public void patch1(String jsonPatch){

        Response response=client.patchGroup(jsonPatch, group.getId(), null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        GroupResource newerGroup=response.readEntity(groupCls);
        //Verify displayName changed
        assertNotEquals(newerGroup.getDisplayName(), group.getDisplayName());
        //Verify externalId appeared
        assertNotNull(newerGroup.getExternalId());

    }

    @Test(dependsOnMethods = "patch1")
    public void patch2() throws Exception{

        List<UserResource> users=getTestUsers("aaa");
        assertTrue(users.size()>0);

        //Define one "add" operation to insert the users retrieved in the created group
        PatchOperation operation=new PatchOperation();
        operation.setOperation("add");
        operation.setPath("members");

        List<Member> memberList=new ArrayList<>();
        users.stream().forEach(u -> {
            Member m=new Member();
            m.setType(ScimResourceUtil.getType(usrClass));
            m.setValue(u.getId());
            m.setDisplay(u.getDisplayName());
            m.setRef("/scim/v2/Users/" + u.getId());
            memberList.add(m);
        });

        operation.setValue(memberList);

        //Apply the patch to the group
        PatchRequest pr=new PatchRequest();
        pr.setOperations(Collections.singletonList(operation));

        Response response=client.patchGroup(pr, group.getId(), null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        group=response.readEntity(groupCls);
        //Verify the new users are there
        Set<Member> members=group.getMembers();
        assertNotNull(members);
        assertTrue(members.stream()
                .allMatch(m -> m.getDisplay()!=null && m.getType()!=null && m.getValue()!=null && m.getRef()!=null));

        //Verify the Ids are the same (both provided and returned)
        Set<String> userIds=users.stream().map(UserResource::getId).collect(Collectors.toSet());
        assertTrue(members.stream().map(Member::getValue).collect(Collectors.toSet()).equals(userIds));

    }

    @Test(dependsOnMethods = "patch2")
    public void delete(){
        Response response=client.deleteGroup(group.getId(), null);
        assertEquals(response.getStatus(), NO_CONTENT.getStatusCode());
        response.close();

    }

    private List<UserResource> getTestUsers(String displayNamePattern){

        SearchRequest sr=new SearchRequest();
        sr.setFilter(String.format("displayName co \"%s\"", displayNamePattern));
        Response response=client.searchUsersPost(sr, null);
        ListResponse listResponse=response.readEntity(ListResponse.class);
        return listResponse.getResources().stream().map(usrClass::cast).collect(Collectors.toList());

    }

}
