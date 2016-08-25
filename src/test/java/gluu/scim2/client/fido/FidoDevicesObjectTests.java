/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client.fido;

import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;
import gluu.scim2.client.Scim2Client;
import gluu.scim2.client.util.Util;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;

/**
 * Before running this test, first register test devices data via the FIDO API (e.g., Super Gluu, etc.) and fill up
 * the values of the "id" and "userId" variables.
 *
 * @author Val Pecaoco
 */
public class FidoDevicesObjectTests extends BaseScimTest {

	Scim2Client client;
	FidoDevice fidoDevice;

	String id = "testId";
	String userId = "testUserId";

	@BeforeTest
	@Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
	public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
		client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
	}

	@Test(groups = "a")
	public void testRetrieveFidoDevice() throws Exception {

		System.out.println("IN testRetrieveFidoDevice...");

		ScimResponse response = client.retrieveFidoDevice(id, userId, new String[]{});
		System.out.println("response body = " + response.getResponseBodyString());

		Assert.assertEquals(200, response.getStatusCode());

		fidoDevice = (FidoDevice) Util.jsonToObject(response, FidoDevice.class);

		System.out.println("id = " + fidoDevice.getId());
		System.out.println("userId = " + fidoDevice.getUserId());

		Assert.assertEquals(id, fidoDevice.getId());
		Assert.assertEquals(userId, fidoDevice.getUserId());

		System.out.println("LEAVING testRetrieveFidoDevice..." + "\n");
	}

	@Test(groups = "b", dependsOnGroups = "a")
	public void testUpdateFidoDevice() throws Exception {

		System.out.println("IN testUpdateFidoDevice...");

		String testDisplayName = "test display name";
		String testDescription = "test description";
		String testDeviceKeyHandle = "test device key handle " + new Date().getTime();

		fidoDevice.setDisplayName(testDisplayName);
		fidoDevice.setDescription(testDescription);
		fidoDevice.setDeviceKeyHandle(testDeviceKeyHandle);

		ScimResponse response = client.updateFidoDevice(fidoDevice, new String[]{});

		Assert.assertEquals(200, response.getStatusCode());

		fidoDevice = (FidoDevice) Util.jsonToObject(response, FidoDevice.class);

		System.out.println("displayName = " + fidoDevice.getDisplayName());
		System.out.println("description = " + fidoDevice.getDescription());
		System.out.println("deviceKeyHandle = " + fidoDevice.getDeviceKeyHandle());

		Assert.assertEquals(testDisplayName, fidoDevice.getDisplayName());
		Assert.assertEquals(testDescription, fidoDevice.getDescription());
		Assert.assertNotEquals(testDeviceKeyHandle, fidoDevice.getDeviceKeyHandle());

		System.out.println("LEAVING testUpdateFidoDevice..." + "\n");
	}

	@Test(groups = "c", dependsOnGroups = "b")
	public void testDeleteFidoDevice() throws Exception {

		System.out.println("IN testDeleteFidoDevice...");

		ScimResponse response = client.deleteFidoDevice(id);
		assertEquals(response.getStatusCode(), 204, "Device could not be deleted; status != 204");

		System.out.println("LEAVING testDeleteFidoDevice..." + "\n");
	}
}
