/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2017, Gluu
 */
package gluu.scim2.client.search;

import gluu.scim2.client.UserBaseTest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.gluu.oxtrust.model.scim2.BaseScimResource;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.SearchRequest;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.gluu.oxtrust.model.scim2.util.IntrospectUtil;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.*;
import static org.testng.Assert.*;

/**
 * Created by jgomer on 2017-10-23.
 */
public class ComplexSearchUserTest extends UserBaseTest {

    private UserResource user;

    @Parameters("user_average_create")
    @Test
    public void create(String json){
        logger.debug("Creating user from json...");
        user=createUserFromJson(json);
    }

    @Test(dependsOnMethods="create", groups = "search")
    public void searchNoAttributesParam(){

        final String ims="Skype";
        logger.debug("Searching users with attribute nickName existent or ims.value={} using POST verb", ims);

        SearchRequest sr=new SearchRequest();
        sr.setFilter("nickName pr or ims.value eq \"" + ims + "\"");
        Response response=client.searchUsersPost(sr);
        assertEquals(response.getStatus(), OK.getStatusCode());

        ListResponse listResponse=response.readEntity(ListResponse.class);
        assertTrue(listResponse.getResources().size()>0);

        for (BaseScimResource resource : listResponse.getResources()) {
            UserResource other = (UserResource) resource;

            boolean c1=other.getNickName()!=null;
            boolean c2=true;
            if (other.getIms()!=null)
                c2=other.getIms().stream().anyMatch(im -> im.getValue().equals(ims));

            assertTrue(c1 || c2);
        }

    }

    @Test(dependsOnMethods="create", groups = "search")
    public void searchAttributesParam() throws Exception{

        int count=3;
        List<String> attrList=Arrays.asList("name.familyName", "active");
        logger.debug("Searching at most {} users using POST verb", count);
        logger.debug("Sorted by family name descending");
        logger.debug("Retrieving only the attributes {}", attrList);

        SearchRequest sr=new SearchRequest();
        sr.setFilter("name.familyName pr");
        sr.setSortBy("name.familyName");
        sr.setSortOrder("descending");
        //Generate a string with the attributes desired to be returned separated by comma
        sr.setAttributes(attrList.toString().replaceFirst("\\[","").replaceFirst("]",""));
        sr.setCount(count);

        Response response=client.searchUsersPost(sr);
        assertEquals(response.getStatus(), OK.getStatusCode());

        ListResponse listResponse=response.readEntity(ListResponse.class);
        if (listResponse.getResources().size()<count)
            logger.warn("Less than {} users satisfying the criteria. TESTER please check manually", count);
        else{
            //Obtain an array of results
            UserResource users[]=listResponse.getResources().stream().map(usrClass::cast)
                    .collect(Collectors.toList()).toArray(new UserResource[]{});
            assertEquals(users.length, count);

            //Build a set of all attributes that should not appear in the response
            Set<String> check=new HashSet<>();
            check.addAll(IntrospectUtil.allAttrs.get(usrClass));

            //Remove from the ALL list, those requested plus its "parents"
            for (String attr : attrList){
                String part=attr;

                for (int i=part.length(); i>0; i = part.lastIndexOf(".")){
                    part=part.substring(0, i);
                    check.remove(part);
                }
            }
            //Remove those that are ALWAYS present (per spec)
            check.removeAll(IntrospectUtil.alwaysCoreAttrs.get(usrClass).keySet());

            //Confirm for every user, those attributes are not there
            for (UserResource user : users)
                for (String path : check){
                    String val=null;
                    try {
                        val=BeanUtils.getProperty(user, path);
                    }
                    catch (NestedNullException nne){
                        //Intentionally left empty
                    }
                    finally {
                        assertNull(val);
                    }
                }

            for (int i=1;i<users.length;i++) {
                String familyName=users[i-1].getName().getFamilyName().toLowerCase();
                String familyName2=users[i].getName().getFamilyName().toLowerCase();

                //Check if first string is greater or equal than second
                assertFalse(familyName.compareTo(familyName2)<0);
            }
        }
    }

    @Test(dependsOnMethods="create", groups = "search")
    public void searchExcludedAttributesParam() throws Exception {

        int count=3;
        List<String> attrList=Arrays.asList("x509Certificates", "entitlements", "roles", "ims", "phoneNumbers",
                "addresses", "emails", "groups");
        logger.debug("Searching at most {} users using POST verb", count);
        logger.debug("Sorted by displayName ascending");
        logger.debug("Excluding the attributes {}", attrList);

        SearchRequest sr=new SearchRequest();
        sr.setFilter("displayName pr");
        sr.setSortBy("displayName");
        //Generate a string with the attributes to exclude
        sr.setExcludedAttributes(attrList.toString().replaceFirst("\\[","").replaceFirst("]",""));
        sr.setCount(count);

        Response response=client.searchUsersPost(sr);
        assertEquals(response.getStatus(), OK.getStatusCode());

        ListResponse listResponse=response.readEntity(ListResponse.class);
        if (listResponse.getResources().size()<count)
            logger.warn("Less than {} users satisfying the criteria. TESTER please check manually", count);
        else {
            //Obtain an array of results
            UserResource users[] = listResponse.getResources().stream().map(usrClass::cast)
                    .collect(Collectors.toList()).toArray(new UserResource[]{});
            assertEquals(users.length, count);

            //Verify attributes were excluded
            for (UserResource u : users){
                assertNull(u.getX509Certificates());
                assertNull(u.getEntitlements());
                assertNull(u.getRoles());
                assertNull(u.getIms());
                assertNull(u.getPhoneNumbers());
                assertNull(u.getAddresses());
                assertNull(u.getEmails());
            }

            for (int i=1;i<users.length;i++) {
                String displayName=users[i-1].getDisplayName().toLowerCase();
                String displayName2 =users[i].getDisplayName().toLowerCase();

                //Check if second string is greater or equal than first
                assertFalse(displayName2.compareTo(displayName)<0);
            }

        }
    }

    @Test(dependsOnGroups = "search", alwaysRun = true)
    public void delete(){
        deleteUser(user);
    }

}
