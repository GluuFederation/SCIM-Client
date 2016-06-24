/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client;

import static org.testng.Assert.assertEquals;
import gluu.BaseScimTest;
import gluu.scim.client.model.ScimPerson;

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
 * SCIM Client Person test
 *
 * @author Yuriy Movchan Date: 03/17/2016
 */
public class ScimClientPersonWriteOperationsTest extends BaseScimTest{

	private ScimClient client;
	private String id;

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath" , "umaAatClientJksPassword" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws IOException {
		
		this.client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
	}

	@Test
	@Parameters({ "scim1.person.create_json" })
	public void createPersonTest(String CREATEJSON) throws Exception {
		ScimResponse response = client.createPersonString(CREATEJSON, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 201, "cold not Add the person, status != 201");
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		ScimPerson person = (ScimPerson) jsonToObject(responseStr, ScimPerson.class);
		this.id = person.getId();

	}

	@Test(dependsOnMethods = "createPersonTest")
	@Parameters({ "scim1.person.update_json" })
	public void updatePersonTest(String UPDATEJSON) throws Exception {
		System.out.println(UPDATEJSON);
		ScimResponse response = client.updatePersonString(UPDATEJSON, this.id, MediaType.APPLICATION_JSON);

		assertEquals(response.getStatusCode(), 200, "cold not update the person, status != 200");
	}

	@Test(dependsOnMethods = "updatePersonTest")
	public void deletePersonTest() throws Exception {
		ScimResponse response = client.deletePerson(this.id);

		assertEquals(response.getStatusCode(), 200, "cold not delete the person, status != 200");
	}

	private Object jsonToObject(String json, Class<?> clazz) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Object clazzObject = mapper.readValue(json, clazz);
		return clazzObject;
	}

}