package gluu.scim.client.auth;

import java.util.concurrent.locks.ReentrantLock;

import gluu.scim.client.AuthMode;
import gluu.scim.client.BaseScimClientImpl;
import gluu.scim.client.exception.ScimInitializationException;

import org.apache.commons.httpclient.HttpMethodBase;
import org.xdi.oxauth.client.TokenClient;
import org.xdi.oxauth.client.TokenResponse;

/**
 * SCIM OAuth client
 * 
 * @author Yuriy Movchan Date: 08/23/2013
 */
public class OAuthScimClientImpl extends BaseScimClientImpl {

	private static final long serialVersionUID = 7099883500099353832L;

	// OAuth
	private String userName;
	private String passWord;

	private String oAuthTokenEndpoint;
	private String clientID;
	private String clientSecret;
	private String scope = "openid user_name";

	private TokenResponse oAuthToken;
	private long oAuthAccessTokenExpiration = 0l; // When the "accessToken" will expire;

	private final ReentrantLock lock = new ReentrantLock();

	public OAuthScimClientImpl(String userName, String passWord, String clientID, String clientSecret, String domain,
			String oAuthTokenEndpoint) {
		super(domain);
		this.userName = userName;
		this.passWord = passWord;
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		this.oAuthTokenEndpoint = oAuthTokenEndpoint;
	}

	public OAuthScimClientImpl(String accessToken, long accessTokenExpiration, String domain, String oAuthTokenEndpoint) {
		super(domain);
		this.oAuthToken = new TokenResponse();
        this.oAuthToken.setStatus(0);
		this.oAuthToken.setAccessToken(accessToken);
		this.oAuthAccessTokenExpiration = accessTokenExpiration;
		this.oAuthTokenEndpoint = oAuthTokenEndpoint;
	}

	@Override
	protected void init() {
		initOAuthAuthentication();
	}

	@Override
	protected void addAuthenticationHeader(HttpMethodBase httpMethod) {
		httpMethod.setRequestHeader("Authorization", "Bearer " + this.oAuthToken.getAccessToken());
		httpMethod.setRequestHeader(AuthMode.BEARER_TOKEN_TYPE_HEADER, "oxauth");
	}

	private void initOAuthAuthentication() {
		long now = System.currentTimeMillis();

		// Get new access token only if is the previous one is missing or expired
		if (!isValidToken(now)) {
			lock.lock();
			try {
				now = System.currentTimeMillis();
				if (!isValidToken(now)) {
					this.oAuthToken = getOAuthAccessToken();
					this.oAuthAccessTokenExpiration = computeAccessTokenExpirationTime(this.oAuthToken.getExpiresIn());
				}
			} catch (Exception ex) {
				throw new ScimInitializationException("Could not get accessToken", ex);
			} finally {
				  lock.unlock();
			}
		}
	}

	private boolean isValidToken(final long now) {
		if ((this.oAuthToken == null) || (this.oAuthToken.getAccessToken() == null) || (this.oAuthAccessTokenExpiration <= now)) {
			return false;
		}
		
		return true;
	}

	private TokenResponse getOAuthAccessToken() {
		TokenClient tokenClient = new TokenClient(this.oAuthTokenEndpoint);
		TokenResponse tokenResponse = tokenClient.execResourceOwnerPasswordCredentialsGrant(this.userName, this.passWord,
				this.scope, this.clientID, this.clientSecret);
		if (tokenResponse == null) {
			throw new ScimInitializationException("Failed to get access token");
		}

		return tokenResponse;
	}

}
