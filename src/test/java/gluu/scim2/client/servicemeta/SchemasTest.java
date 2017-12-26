/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2017, Gluu
 */
package gluu.scim2.client.servicemeta;

import gluu.scim2.client.BaseTest;
import org.gluu.oxtrust.model.scim2.BaseScimResource;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.fido.FidoDeviceResource;
import org.gluu.oxtrust.model.scim2.group.GroupResource;
import org.gluu.oxtrust.model.scim2.schema.SchemaAttribute;
import org.gluu.oxtrust.model.scim2.schema.SchemaResource;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.gluu.oxtrust.model.scim2.util.ScimResourceUtil;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.gluu.oxtrust.model.scim2.Constants.USER_EXT_SCHEMA_ID;

import static org.testng.Assert.*;

/**
 * Created by jgomer on 2017-10-21.
 *
 * Important: Method inspectUserExtensionSchema is a server-dependant test. It requires the scimCustom 1st, 2nd and 3rd attributes with
 * the usual data types as in https://github.com/GluuFederation/community-edition-setup/templates/test/scim-client/data/scim-test-data.ldif
 * and https://github.com/GluuFederation/community-edition-setup/templates/test/scim-client/schema/scim_test.schema
 */
public class SchemasTest extends BaseTest {

    private ListResponse listResponse;

    @BeforeTest
    public void init() throws Exception {
        Response response = client.getSchemas();
        listResponse = response.readEntity(ListResponse.class);
        assertTrue(listResponse.getTotalResults() > 0);
    }

    @Test
    public void checkSchemasExistence() {

        List<String> schemas = new ArrayList<>();
        schemas.add(USER_EXT_SCHEMA_ID);

        List<Class<? extends BaseScimResource>> classes = Arrays.asList(UserResource.class, GroupResource.class, FidoDeviceResource.class);
        classes.forEach(cls -> schemas.add(ScimResourceUtil.getSchemaAnnotation(cls).id()));

        //Verifies default schemas for the 3 main SCIM resources + user extension are part of /Schemas endpoint
        listResponse.getResources().forEach(res -> assertTrue(schemas.contains(res.getId())));

    }

    @Test(dependsOnMethods = "checkSchemasExistence")
    public void inspectUserExtensionSchema(){

        Optional<SchemaResource> userExtSchemaOpt=listResponse.getResources().stream().map(SchemaResource.class::cast)
                .filter(sr -> sr.getId().contains(USER_EXT_SCHEMA_ID)).findFirst();

        if (userExtSchemaOpt.isPresent()){
            String name=userExtSchemaOpt.get().getName();
            assertNotNull(name);
            logger.info("Found User Schema Extension: {}", name);

            Boolean foundAttr[]=new Boolean[3];
            for (SchemaAttribute attribute : userExtSchemaOpt.get().getAttributes()){
                switch (attribute.getName()) {
                    case "scimCustomFirst":
                        foundAttr[0] = true;
                        logger.info("scimCustomFirst found");
                        assertEquals(attribute.getType(),"string");
                        assertFalse(attribute.isMultiValued());
                        break;
                    case "scimCustomSecond":
                        foundAttr[1] = true;
                        logger.info("scimCustomSecond found");
                        assertEquals(attribute.getType(),"dateTime");
                        assertTrue(attribute.isMultiValued());
                        break;
                    case "scimCustomThird":
                        foundAttr[2] = true;
                        logger.info("scimCustomThird found");
                        assertEquals(attribute.getType(),"decimal");
                        assertFalse(attribute.isMultiValued());
                        break;
                }
            }
            Arrays.asList(foundAttr).forEach(org.testng.Assert::assertTrue);

        }
        else
            logger.warn("There is no Schema Resource associated to User Schema Extension ({})", USER_EXT_SCHEMA_ID);

    }

}