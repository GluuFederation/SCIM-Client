package gluu.scim2.client.servicemeta;

import gluu.scim2.client.BaseTest;
import org.gluu.oxtrust.model.scim2.BaseScimResource;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.annotations.Schema;
import org.gluu.oxtrust.model.scim2.fido.FidoDeviceResource;
import org.gluu.oxtrust.model.scim2.group.GroupResource;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.gluu.oxtrust.model.scim2.Constants.USER_EXT_SCHEMA_ID;

import static org.testng.Assert.*;

/**
 * Created by jgomer on 2017-10-21.
 */
public class SchemasTest extends BaseTest {

    private ListResponse listResponse;

    @BeforeTest
    public void init() throws Exception {
        Response response = client.getSchemas();
        listResponse = response.readEntity(ListResponse.class);
    }

    @Test
    public void checkSchemasExistence() {
        assertTrue(listResponse.getTotalResults() > 0);

        List<String> schemas = new ArrayList<>();
        List<Class<? extends BaseScimResource>> classes = Arrays.asList(UserResource.class, GroupResource.class, FidoDeviceResource.class);
        classes.stream().forEach(cls -> schemas.add(cls.getAnnotation(Schema.class).id()));
        schemas.add(USER_EXT_SCHEMA_ID);

        listResponse.getResources().forEach(res -> assertTrue(schemas.contains(res.getId())));
    }

}