/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim2.client.factory.ScimClientFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.gluu.oxtrust.model.scim2.BulkOperation;
import org.gluu.oxtrust.model.scim2.BulkRequest;
import org.gluu.oxtrust.model.scim2.BulkResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * SCIM Client Bulk operation test
 *
 * @author Reda Zerrad Date: 06.02.2012
 * @author Yuriy Movchan Date: 03/17/2016
 */
public class ScimClientBulkOperationsTest extends BaseScimTest {

	private ScimClient client;
	private BulkRequest bulkRequest;

    @Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId" })
	@BeforeTest
    public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws IOException {
        client = ScimClientFactory.getClient(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
	}

	@Test(groups = "a")
	@Parameters({ "scim2.bulk.request_json" })
	public void testProcessBulkOperationString(String bulkRequestString) throws Exception {

        // bulkRequestString should include cleanup (DELETE operations)
		System.out.println("bulkRequestString = " + bulkRequestString);

		BaseClientResponse<BulkResponse> response = client.processBulkOperationString(bulkRequestString);

		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not process bulk operation string, status != 200");

        bulkRequest = bulkRequestFromString(bulkRequestString);
		BulkResponse bulkResponse = response.getEntity();

        assert(bulkResponse.getOperations().size() > 0);
        assertEquals(bulkRequest.getOperations().size(), bulkResponse.getOperations().size());

        checkResults(bulkResponse);
	}

    @Test(groups = "b", dependsOnGroups = "a")
    public void testProcessBulkOperation() throws Exception {

        BaseClientResponse<BulkResponse> response = client.processBulkOperation(bulkRequest);

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(), "Could not process bulk request, status != 200");

        BulkResponse bulkResponse = response.getEntity();

        System.out.println("Request operations count = " + bulkRequest.getOperations().size());
        System.out.println("Response operations count = " + bulkResponse.getOperations().size());

        assert(bulkResponse.getOperations().size() > 0);
        assertEquals(bulkRequest.getOperations().size(), bulkResponse.getOperations().size());

        checkResults(bulkResponse);
    }

    private void checkResults(BulkResponse bulkResponse) {

        int i = 1;
        for (BulkOperation bulkOperation : bulkResponse.getOperations()) {

            String status = bulkOperation.getStatus();
            System.out.println("["+i+"]Path = " + bulkOperation.getPath());
            System.out.println("["+i+"]Method = " + bulkOperation.getMethod());
            System.out.println("["+i+"]Status = " + bulkOperation.getStatus());

            assert(status.equalsIgnoreCase(String.valueOf(Response.Status.CREATED.getStatusCode())) || status.equalsIgnoreCase(String.valueOf(Response.Status.OK.getStatusCode())));
            i++;
        }
    }

    private BulkRequest bulkRequestFromString(String bulkRequestString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(bulkRequestString, BulkRequest.class);
    }
}
