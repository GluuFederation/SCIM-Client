/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim2.client.factory.ScimClientFactory;
import org.gluu.oxtrust.model.scim2.*;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * @author Val Pecaoco
 */
public class GroupObjectAttributesFilterTests extends BaseScimTest {

    ScimClient client;

    String id;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        client = ScimClientFactory.getClient(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test(groups = "a")
    public void testCreateGroup() throws Exception {

        System.out.println("IN testCreateGroup...");

        String displayName = "Test Group " + new Date().getTime();

        Group group = new Group();
        group.setDisplayName(displayName);

        String[] attributesArray = new String[]{"urn:ietf:params:scim:schemas:core:2.0:Group:displayName"};
        BaseClientResponse<Group> response = client.createGroup(group, attributesArray);

        Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Group groupCreated = response.getEntity();
        assertEquals(groupCreated.getDisplayName(), displayName, "Group not added or retrieved");

        this.id = groupCreated.getId();

        System.out.println("groupCreated.getId() = " + groupCreated.getId());
        System.out.println("groupCreated.getDisplayName() = " + groupCreated.getDisplayName());

        System.out.println("LEAVING testCreateGroup..." + "\n");
    }

    @Test(groups = "b", dependsOnGroups = "a")
    public void testRetrieveNewGroup() throws Exception {

        System.out.println("IN testRetrieveNewGroup...");

        String[] attributesArray = new String[]{"displayName"};
        BaseClientResponse<Group> response = client.retrieveGroup(this.id, attributesArray);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Group groupRetrieved = response.getEntity();
        assertEquals(groupRetrieved.getId(), this.id, "Group could not be retrieved");

        System.out.println("groupRetrieved.getId() = " + groupRetrieved.getId());
        System.out.println("groupRetrieved.getDisplayName() = " + groupRetrieved.getDisplayName());

        System.out.println("LEAVING testRetrieveNewGroup..." + "\n");
    }

    @Test(groups = "c", dependsOnGroups = "b")
    public void testUpdateNewGroup() throws Exception {

        System.out.println("IN testUpdateNewGroup...");

        Thread.sleep(3000);  // Sleep for 3 seconds

        String[] attributesArray = new String[]{"displayName", "meta"};
        BaseClientResponse<Group> response = client.retrieveGroup(this.id, attributesArray);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Group groupRetrieved = response.getEntity();

        groupRetrieved.setDisplayName(groupRetrieved.getDisplayName() + " UPDATED");

        BaseClientResponse<Group> responseUpdated = client.updateGroup(groupRetrieved, this.id, attributesArray);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), responseUpdated.getStatus());

        Group groupUpdated = responseUpdated.getEntity();

        assertEquals(groupUpdated.getId(), this.id, "Group could not be retrieved");
        assert(groupUpdated.getMeta().getLastModified().getTime() > groupUpdated.getMeta().getCreated().getTime());

        System.out.println("groupUpdated.getId() = " + groupUpdated.getId());
        System.out.println("groupUpdated.getDisplayName() = " + groupUpdated.getDisplayName());
        System.out.println("groupUpdated.getMeta().getLastModified().getTime() = " + groupUpdated.getMeta().getLastModified().getTime());
        System.out.println("groupUpdated.getMeta().getCreated().getTime() = " + groupUpdated.getMeta().getCreated().getTime());

        System.out.println("LEAVING testUpdateNewGroup..." + "\n");
    }

    @Test(groups = "d", dependsOnGroups = "c")
    public void testUpdateDisplayNameDifferentId() throws Exception {

        System.out.println("IN testUpdateDisplayNameDifferentId...");

        String[] attributesArray = new String[]{"displayName"};
        BaseClientResponse<Group> response = client.retrieveGroup(this.id, attributesArray);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Group groupRetrieved = response.getEntity();

        groupRetrieved.setDisplayName("Gluu Manager Group");

        BaseClientResponse<Group> responseUpdated = client.updateGroup(groupRetrieved, this.id, attributesArray);

        Assert.assertEquals(Response.Status.CONFLICT.getStatusCode(), responseUpdated.getStatus());

        System.out.println("LEAVING testUpdateDisplayNameDifferentId..." + "\n");
    }

    @Test(groups = "e", dependsOnGroups = "d", alwaysRun = true)
    public void testDeleteGroup() throws Exception {

        System.out.println("IN testDeleteGroup...");

        BaseClientResponse response = client.deleteGroup(this.id);
        assertEquals(response.getStatus(), Response.Status.NO_CONTENT.getStatusCode(), "Group could not be deleted; status != 204");

        System.out.println("LEAVING testDeleteGroup..." + "\n");
    }

    @Test(dependsOnGroups = "e", alwaysRun = true)
    public void testGroupDeserializerUsers() throws Exception {

        System.out.println("IN testGroupDeserializerUsers...");

        String filter = "userName eq \"admin\"";
        int startIndex = 1;
        int count = 2;
        String sortBy = "";
        String sortOrder = "";
        String[] attributes = null;

        // POST search on /scim/v2/Users/.search
        BaseClientResponse<ListResponse> response = client.searchUsers(filter, startIndex, count, sortBy, sortOrder, attributes);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ListResponse listResponse = response.getEntity();
        assertEquals(listResponse.getTotalResults(), 1);

        User userRetrieved = (User) listResponse.getResources().get(0);
        assertEquals(userRetrieved.getUserName(), "admin", "User could not be retrieved");

        System.out.println("userRetrieved.getId() = " + userRetrieved.getId());
        System.out.println("userRetrieved.getDisplayName() = " + userRetrieved.getDisplayName());

        List<GroupRef> groups = userRetrieved.getGroups();
        for (GroupRef group : groups) {

            if (group.getDisplay().startsWith("Gluu")) {

                System.out.println("group display = " + group.getDisplay());
                System.out.println("group inum = " + group.getValue());
                System.out.println("group $ref = " + group.getReference());
                Assert.assertNotNull(group.getReference());

                String[] attributesArray = new String[]{"displayName,members.value,urn:ietf:params:scim:schemas:core:2.0:Group:members.$ref"};
                BaseClientResponse<Group> groupRetrievedResponse = client.retrieveGroup(group.getValue(), attributesArray);

                Assert.assertEquals(Response.Status.OK.getStatusCode(), groupRetrievedResponse.getStatus());

                Group adminGroup = groupRetrievedResponse.getEntity();

                Set<MemberRef> members = adminGroup.getMembers();
                for (MemberRef member : members) {
                    System.out.println("member inum = " + member.getValue());
                    System.out.println("member $ref = " + member.getReference());
                    Assert.assertNotNull(member.getReference());
                }

                break;
            }
        }

        System.out.println("LEAVING testGroupDeserializerUsers..." + "\n");
    }
}
