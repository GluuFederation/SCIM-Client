package gluu.scim.client;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

/**
 * SCIM UMA Client person Create,Update,Delete tests
 *
 * @author Reda Zerrad Date: 06.01.2012
 */
public class ScimClientPersonWriteOperationsUmaTest extends ScimClientPersonWriteOperationsBaseTest {

	@Parameters({ "domainURL", "umaMetaDataUrl", "userName", "passWord", "umaAatClientId", "umaAatClientSecret", "umaRedirectUri" })
	@BeforeTest
	public void init(final String domain, final String umaMetaDataUrl, final String umaUserId, final String umaUserSecret, final String umaAatClientId, final String umaAatClientSecret, final String umaRedirectUri) {
		client = ScimClient.umaInstance(domain, umaMetaDataUrl, umaUserId, umaUserSecret, umaAatClientId, umaAatClientSecret, umaRedirectUri);
		response = null;
		person = null;
	}

}
