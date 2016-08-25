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
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.Resource;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Before running this test, first register test devices data via the FIDO API (e.g., Super Gluu, etc.).
 *
 * @author Val Pecaoco
 */
public class FidoDeviceFiltersMainTests extends BaseScimTest {

	Scim2Client client;

	@BeforeTest
	@Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
	public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
		client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
	}

	@Test
	public void testSearchGet() throws Exception {

		String filter = "id pr";
		int startIndex = 1;
		int count = 20;
		String sortBy = "id";
		String sortOrder = "ascending";
		String[] attributes = null;

		ScimResponse response = client.searchFidoDevices("", filter, startIndex, count, sortBy, sortOrder, attributes);

		System.out.println(" testSearchGet response = " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Status != 200");

		ListResponse listResponse = Util.toListResponseFidoDevice(response);

		System.out.println(" filter = " + filter + ", totalResults = " + listResponse.getTotalResults() + "\n");
		Assert.assertTrue(listResponse.getTotalResults() > 0);
	}

	@Test
	public void testSearchPost() throws Exception {

		String filter = "id pr";
		int startIndex = 1;
		int count = 20;
		String sortBy = "id";
		String sortOrder = "ascending";
		String[] attributes = null;

		ScimResponse response = client.searchFidoDevicesPost("", filter, startIndex, count, sortBy, sortOrder, attributes);

		System.out.println(" testSearchPost response = " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Status != 200");

		ListResponse listResponse = Util.toListResponseFidoDevice(response);

		System.out.println(" filter = " + filter + ", totalResults = " + listResponse.getTotalResults() + "\n");
		Assert.assertTrue(listResponse.getTotalResults() > 0);
	}

	@Test
	public void testSearchGetFilterAttributes() throws Exception {

		String filter = "id pr";
		int startIndex = 1;
		int count = 3;
		String sortBy = "id";
		String sortOrder = "ascending";
		String[] attributes = new String[] {"displayName", "description"};

		ScimResponse response = client.searchFidoDevices("", filter, startIndex, count, sortBy, sortOrder, attributes);

		System.out.println(" testSearchGetFilterAttributes response = " + response.getResponseBodyString());
		assertEquals(response.getStatusCode(), 200, "Status != 200");

		ListResponse listResponse = Util.toListResponseFidoDevice(response);

		for (Resource resource : listResponse.getResources()) {

			FidoDevice fidoDevice = (FidoDevice) resource;

			System.out.println(" id = " + fidoDevice.getId());
			System.out.println(" displayName = " + fidoDevice.getDisplayName());
			System.out.println(" description = " + fidoDevice.getDescription());
			System.out.println(" deviceKeyHandle = " + fidoDevice.getDeviceKeyHandle());

			Assert.assertNotNull(fidoDevice.getId());
			Assert.assertNull(fidoDevice.getDeviceKeyHandle());
		}

		System.out.println(" filter = " + filter + ", totalResults = " + listResponse.getTotalResults() + "\n");
		Assert.assertTrue(listResponse.getTotalResults() > 0);
	}
}
