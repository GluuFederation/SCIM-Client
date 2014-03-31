package gluu.scim.client.auth;

import gluu.scim.client.BaseScimClientImpl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpMethodBase;

/**
 * SCIM basic client
 * 
 * @author Yuriy Movchan Date: 08/23/2013
 */
public class BasicScimClientImpl extends BaseScimClientImpl {

	private static final long serialVersionUID = 7099883500099353832L;

	// Basic
	private String userName;
	private String passWord;
	private String basicCredentials;

	public BasicScimClientImpl(String userName, String passWord, String domain) {
		super(domain);
		this.userName = userName;
		this.passWord = passWord;
	}

	@Override
	protected void init() {
		initBasicAuthentication();
	}

	@Override
	protected void addAuthenticationHeader(HttpMethodBase httpMethod) {
		httpMethod.setRequestHeader("Authorization", "Basic " + this.basicCredentials);
	}

	private void initBasicAuthentication() {
		String userCredentials = this.userName + ":" + this.passWord;
		this.basicCredentials = Base64.encodeBase64String(userCredentials.getBytes());
	}

}
