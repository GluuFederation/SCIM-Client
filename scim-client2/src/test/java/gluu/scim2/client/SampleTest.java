/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2017, Gluu
 */
package gluu.scim2.client;

import gluu.scim2.client.factory.ScimClientFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.CustomAttributes;
import org.gluu.oxtrust.model.scim2.ErrorResponse;
import org.gluu.oxtrust.model.scim2.SearchRequest;
import org.gluu.oxtrust.model.scim2.user.Email;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.gluu.oxtrust.ws.rs.scim2.IUserWebService;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.Collections;

import javax.ws.rs.core.Response.Status;

/**
 * Created by jgomer on 2017-09-14.
 */
public class SampleTest extends BaseTest {

    private Logger logger = LogManager.getLogger(getClass());

    @Parameters({"domainURL", "OIDCMetadataUrl"})
    @Test
    public void smallerClient(String domain, String url) throws Exception{
        IUserWebService service =ScimClientFactory.getTestClient(IUserWebService.class, domain, url);

        SearchRequest sr=new SearchRequest();
        sr.setFilter("userName eq \"admin\"");
        Response r= service.searchUsersPost(sr);

        UserResource u=r.readEntity(UserResource.class);
        logger.debug("Admin AKA {}", u.getDisplayName());

    }

    @Test
    public void mytest() throws Exception{

        Response response=client.getUserById("...", null, null);
        logger.debug(response.readEntity(String.class));

    }

}

