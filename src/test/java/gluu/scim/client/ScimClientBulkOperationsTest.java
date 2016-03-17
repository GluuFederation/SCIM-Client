package gluu.scim.client;

import static org.testng.Assert.assertEquals;
import gluu.BaseScimTest;
import gluu.scim.client.model.ScimBulkResponse;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * SCIM Client Bulk operation test
 *
 * @author Reda Zerrad Date: 06.02.2012
 * @author Yuriy Movchan Date: 03/17/2016
 */
public class ScimClientBulkOperationsTest extends BaseScimTest {

	private ScimClient client;
	private String uid;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJwks" , "umaAatClientKeyId"})
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) throws IOException {
		String jwks = FileUtils.readFileToString(new File(umaAatClientJwks));				
		client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaAatClientId, jwks, umaAatClientKeyId);
	}

	@Test
	@Parameters({ "scim1.builk.request_json" })
	public void bulkOperationTest(String REQUESTJSON) throws Exception {
		ScimResponse response = client.bulkOperationString(REQUESTJSON, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 200, "cold not Add the person, status != 200");
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		ScimBulkResponse scimBulkResponse = (ScimBulkResponse) jsonToObject(responseStr, ScimBulkResponse.class);
		String location = scimBulkResponse.getOperations().get(0).getLocation();
		this.uid = getUID(location.split("/"));

	}

	@Test(dependsOnMethods = "bulkOperationTest")
	public void cleanUp() throws Exception {
		ScimResponse response = client.deletePerson(this.uid);

		assertEquals(response.getStatusCode(), 200, "cold not delete the person, status != 200");
	}

	private String getUID(String[] strings) {
		for (String str : strings) {
			if (str.startsWith("@")) {
				return str;
			}
		}

		return null;
	}

	private Object jsonToObject(String json, Class<?> clazz) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}

}
