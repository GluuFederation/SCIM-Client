package gluu.scim2.client.singleresource;

import gluu.scim2.client.BaseTest;
import org.apache.commons.beanutils.BeanUtils;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.fido.FidoDeviceResource;
import org.gluu.oxtrust.model.scim2.util.IntrospectUtil;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.*;

import static org.testng.Assert.*;

/**
 * NOTES:
 * Before running this test, first register at least one devices via the FIDO API (e.g., Super Gluu, u2f, etc.)
 * and fill up the values for userId attribute (the inum of device owner)
 *
 * Created by jgomer on 2017-10-21.
 * Based on former Val Pecaoco's gluu.scim2.client.fido.FidoDevicesObjectTests
 */
public class FidoDeviceTest extends BaseTest {

    private FidoDeviceResource device;
    private static final Class<FidoDeviceResource> fidoClass=FidoDeviceResource.class;

    @Test
    public void search(){

        logger.debug("Searching all fido devices");
        Response response=client.searchDevices("application pr", null, null, null, null, null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        ListResponse listResponse=response.readEntity(ListResponse.class);
        //Work upon the first device of the list only
        device=(FidoDeviceResource) listResponse.getResources().get(0);
        assertNotNull(device);
        logger.debug("First device {} picked", device.getId());

    }

    @Test(dependsOnMethods = "search")
    public void retrieve(){

        logger.debug("Retrieving same device by id");
        Response response=client.getDeviceById(device.getId(), device.getUserId(), null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        FidoDeviceResource same=response.readEntity(fidoClass);
        assertEquals(device.getId(), same.getId());
        assertEquals(device.getApplication(), same.getApplication());
        assertEquals(device.getCounter(), same.getCounter());
        assertEquals(device.getDeviceKeyHandle(), same.getDeviceKeyHandle());
    }

    @Test(dependsOnMethods = "retrieve")
    public void updateWithJson() throws Exception{

        //shallow clone device
        FidoDeviceResource clone=(FidoDeviceResource) BeanUtils.cloneBean(device);

        clone.setDisplayName(Double.toString(Math.random()));
        clone.setStatus("compromised");
        String json=mapper.writeValueAsString(clone);

        logger.debug("Updating device with json");
        Response response=client.updateDevice(json, device.getId(), null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        FidoDeviceResource updated=response.readEntity(fidoClass);
        assertNotEquals(updated.getDisplayName(), device.getDisplayName());
        assertEquals(updated.getStatus(), "compromised");

    }

    @Test(dependsOnMethods = "updateWithJson")
    public void updateWithObject() throws Exception{

        logger.debug("Updating device to original attributes");
        Response response=client.updateDevice(device, device.getId(), null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());

        FidoDeviceResource updated=response.readEntity(fidoClass);

        //Naively compare (property-to-property) the original and new object. It's feasible since all of them are strings
        for (String path : IntrospectUtil.allAttrs.get(fidoClass))
            if (!path.startsWith("meta"))   //Exclude metas since they diverge
                assertEquals(BeanUtils.getProperty(updated, path), BeanUtils.getProperty(device, path));

    }

    @Test(dependsOnMethods = "updateWithObject")
    public void delete(){

        logger.debug("Deleting device");
        Response response=client.deleteDevice(device.getId(), null);
        assertEquals(response.getStatus(), NO_CONTENT.getStatusCode());
        response.close();

    }

}
