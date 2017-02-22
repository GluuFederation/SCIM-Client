/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client.fido;

import gluu.BaseScimTest;
import gluu.scim2.client.ScimClient;
import gluu.scim2.client.ScimClientFactory;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.Resource;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

/**
 * Before running this test, first register test devices data via the FIDO API (e.g., Super Gluu, etc.).
 *
 * @author Val Pecaoco
 */
public class FidoDeviceFiltersMainTests extends BaseScimTest {

	ScimClient client;

	@BeforeTest
	@Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
	public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
		client = ScimClientFactory.getClient(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
	}

	@Test
	public void testSearchGet() throws Exception {

		String filter = "id pr";
		int startIndex = 1;
		int count = 20;
		String sortBy = "id";
		String sortOrder = "ascending";
		String[] attributes = null;

		BaseClientResponse<ListResponse> response = client.searchFidoDevices("", filter, startIndex, count, sortBy, sortOrder, attributes);

		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Status != 200");

		ListResponse listResponse = response.getEntity();

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

		BaseClientResponse<ListResponse> response = client.searchFidoDevicesPost("", filter, startIndex, count, sortBy, sortOrder, attributes);

		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Status != 200");

		ListResponse listResponse = response.getEntity();

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

		BaseClientResponse<ListResponse> response = client.searchFidoDevices("", filter, startIndex, count, sortBy, sortOrder, attributes);

		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Status != 200");

		ListResponse listResponse = response.getEntity();

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
