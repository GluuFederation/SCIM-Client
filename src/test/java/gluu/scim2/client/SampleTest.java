package gluu.scim2.client;

import gluu.scim2.client.factory.ScimClientFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.SearchRequest;
import org.gluu.oxtrust.model.scim2.user.Email;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.gluu.oxtrust.ws.rs.scim2.IUserWebService;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.Collections;

/**
 * Created by jgomer on 2017-09-14.
 */
public class SampleTest extends BaseTest {

    private Logger logger = LogManager.getLogger(getClass());

    @Parameters("domainURL")
    @Test
    public void smallerClient(String domain) throws Exception{
        IUserWebService serv=ScimClientFactory.getDummyClient(IUserWebService.class, domain);
        Response r=serv.getUserById("@!3245.DF39.6A34.9E97!0001!513A.9888!0000!0E09.B2B4.52F0.88EA", null, null);
        UserResource u=r.readEntity(UserResource.class);
        logger.debug(u.getDisplayName());
    }

}

