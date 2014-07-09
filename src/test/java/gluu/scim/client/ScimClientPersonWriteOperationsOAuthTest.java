package gluu.scim.client;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

/**
 * SCIM OAuth Client person Create,Update,Delete tests
 *
 * @author Reda Zerrad Date: 06.01.2012
 */
public class ScimClientPersonWriteOperationsOAuthTest extends ScimClientPersonWriteOperationsBaseTest {

	@Parameters({ "userName", "passWord", "domainURL", "clientID", "clientSecret", "oxAuthDomain" })
	@BeforeTest
	public void init(final String userName, final String passWord, final String domainURL, final String clientID,
			final String clientSecret, final String oxAuthDomain) {
		client = ScimClient.oAuthInstance(userName, passWord, clientID, clientSecret, domainURL, oxAuthDomain);
		response = null;
		person = null;
	}

}
