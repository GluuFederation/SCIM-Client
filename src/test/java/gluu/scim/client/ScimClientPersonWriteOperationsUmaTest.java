package gluu.scim.client;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * SCIM UMA Client person Create,Update,Delete tests
 *
 * @author Reda Zerrad Date: 06.01.2012
 */
public class ScimClientPersonWriteOperationsUmaTest extends ScimClientPersonWriteOperationsBaseTest {

	@Parameters({ "domainURL", "umaMetaDataUrl", "umaAatClientId" , "umaAatClientJwks" , "umaAatClientKeyId" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJwks, @Optional final String umaAatClientKeyId) {
		try {
			String jwks = FileUtils.readFileToString(new File(umaAatClientJwks));		
			client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaAatClientId, jwks, umaAatClientKeyId);
			response = null;
			person = null;
		} catch (IOException e) {
			System.out.println("exception in reading fle " + e.getMessage());
		};
	}

}
