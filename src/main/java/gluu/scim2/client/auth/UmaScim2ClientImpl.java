/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client.auth;

import gluu.scim.client.ScimResponse;
import gluu.scim.client.exception.ScimInitializationException;
import gluu.scim.client.model.ScimBulkOperation;
import gluu.scim2.client.BaseScim2ClientImpl;
import org.apache.commons.httpclient.HttpMethodBase;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.Group;
import org.gluu.oxtrust.model.scim2.User;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.xdi.oxauth.client.TokenRequest;
import org.xdi.oxauth.client.uma.CreateRptService;
import org.xdi.oxauth.client.uma.RptAuthorizationRequestService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.wrapper.UmaClient;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.crypto.signature.ECDSAPrivateKey;
import org.xdi.oxauth.model.crypto.signature.RSAPrivateKey;
import org.xdi.oxauth.model.uma.*;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.oxauth.model.util.JwksUtil;
// import org.xdi.oxauth.model.util.JwtUtil;
import org.xdi.util.StringHelper;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SCIM UMA client
 * 
 * @author Yuriy Movchan
 * @author Yuriy Zabrovarnyy
 */
public class UmaScim2ClientImpl extends BaseScim2ClientImpl {

	private static final long serialVersionUID = 7099883500099353832L;

	// UMA
	private UmaConfiguration metadataConfiguration;
	private Token umaAat;
	private RPTResponse umaRpt;

	private String umaMetaDataUrl;

	private String umaAatClientId;
	private String umaAatClientKeyId;
	private String umaAatClientJwks;

    protected ClientExecutor executor;
	
	private long umaAatAccessTokenExpiration = 0l; // When the "accessToken" will expire;

	private final ReentrantLock lock = new ReentrantLock();

	public UmaScim2ClientImpl(String domain, String umaMetaDataUrl, String umaAatClientId, String umaAatClientJwks, String umaAatClientKeyId) {
		super(domain);
		this.umaMetaDataUrl = umaMetaDataUrl;
		this.umaAatClientId = umaAatClientId;
		this.umaAatClientJwks = umaAatClientJwks;
		this.umaAatClientKeyId = umaAatClientKeyId;
	}

	@Override
	protected void init() {
		initUmaAuthentication();
	}

	@Override
	protected void addAuthenticationHeader(HttpMethodBase httpMethod) {
		httpMethod.setRequestHeader("Authorization", "Bearer " + this.umaRpt.getRpt());
	}

	private void initUmaAuthentication() {
		long now = System.currentTimeMillis();

		// Get new access token only if is the previous one is missing or expired
		if (!isValidToken(now)) {
			lock.lock();
			try {
				now = System.currentTimeMillis();
				if (!isValidToken(now)) {
					initUmaRpt();
					this.umaAatAccessTokenExpiration = computeAccessTokenExpirationTime(this.umaAat.getExpiresIn());
				}
			} catch (Exception ex) {
				throw new ScimInitializationException("Could not get accessToken", ex);
			} finally {
				  lock.unlock();
			}
		}
	}

	private boolean isValidToken(final long now) {
		if ((this.umaAat == null) || (this.umaAat.getAccessToken() == null) || (this.umaAatAccessTokenExpiration <= now)) {
			return false;
		}

		return true;
	}

	private void initUmaRpt() {
		// Remove old tokens
		this.umaAat = null;
		this.umaRpt = null;

		// Get metadata configuration
		if (this.executor == null) {
			this.metadataConfiguration = UmaClientFactory.instance().createMetaDataConfigurationService(umaMetaDataUrl)
				.getMetadataConfiguration();
		}else{
			this.metadataConfiguration = UmaClientFactory.instance().createMetaDataConfigurationService(umaMetaDataUrl,executor)
					.getMetadataConfiguration();
		}

		if ((metadataConfiguration == null) || !StringHelper.equals(metadataConfiguration.getVersion(), "1.0")) {
			throw new ScimInitializationException("Failed to load valid UMA metadata configuration");
		}

		// Get AAT
		org.xdi.oxauth.model.crypto.PrivateKey privateKey = null;
		try {
			// privateKey = JwtUtil.getPrivateKey(null, umaAatClientJwks, umaAatClientKeyId);
			privateKey = JwksUtil.getPrivateKey(null, umaAatClientJwks, umaAatClientKeyId);
			if (privateKey == null) {
				throw new ScimInitializationException("There is no keyId in JWKS");
			}

			TokenRequest tokenRequest = TokenRequest.builder().aat().grantType(GrantType.CLIENT_CREDENTIALS).build();
			if (privateKey instanceof ECDSAPrivateKey) {
				tokenRequest.setEcPrivateKey((ECDSAPrivateKey) privateKey);
			} else if (privateKey instanceof RSAPrivateKey) {
				tokenRequest.setRsaPrivateKey((RSAPrivateKey) privateKey);
			}

			tokenRequest.setAuthenticationMethod(AuthenticationMethod.PRIVATE_KEY_JWT);
	        tokenRequest.setAuthUsername(umaAatClientId);
	        tokenRequest.setAlgorithm(privateKey.getSignatureAlgorithm());
	        tokenRequest.setKeyId(privateKey.getKeyId());
	        tokenRequest.setAudience(metadataConfiguration.getTokenEndpoint());

			this.umaAat = UmaClient.request(metadataConfiguration.getTokenEndpoint(), tokenRequest);
		} catch (ClientResponseFailure ex) {
			String errorMessage = (String) ex.getResponse().getEntity(String.class);
			throw new ScimInitializationException("Failed to get AAT token. Error: " + errorMessage, ex);
		} catch (Exception ex) {
			throw new ScimInitializationException("Failed to get AAT token", ex);
		}

		if (this.umaAat == null) {
			throw new ScimInitializationException("Failed to get UMA AAT token");
		}
		
		CreateRptService rptService = null;
		if (this.executor == null) {
			rptService = UmaClientFactory.instance().createRequesterPermissionTokenService(metadataConfiguration);
		}else{
			rptService = UmaClientFactory.instance().createRequesterPermissionTokenService(metadataConfiguration,executor);	
		}
		// Get RPT
		this.umaRpt = null;
		try {
			umaRpt = rptService.createRPT("Bearer " + this.umaAat.getAccessToken(), getHost(metadataConfiguration.getIssuer()));
		} catch (ClientResponseFailure ex) {
			String errorMessage = (String) ex.getResponse().getEntity(String.class);
			throw new ScimInitializationException("Failed to get RPT token. Error: " + errorMessage, ex);
        } catch (MalformedURLException ex) {
			throw new ScimInitializationException("Failed to determine host by URI", ex);
		}

		if (this.umaRpt == null) {
			throw new ScimInitializationException("Failed to get UMA RPT token");
		}
	}

	private boolean autorizeRpt(ScimResponse scimResponse) {
		if (scimResponse.getStatusCode() == 403) {
        	// Forbidden : RPT is not authorized yet
        	
            final PermissionTicket resourceSetPermissionTicket;
			try {
				resourceSetPermissionTicket = (new ObjectMapper()).readValue(scimResponse.getResponseBody(), PermissionTicket.class);
			} catch (Exception ex) {
    			throw new ScimInitializationException("UMA ticket is invalid", ex);
			}

			authorizeRpt(resourceSetPermissionTicket.getTicket());

			if (StringHelper.isEmpty(resourceSetPermissionTicket.getTicket())) {
    			throw new ScimInitializationException("UMA ticket is invalid");
            }
            
            return true;
        }

		return false;
	}

	private boolean authorizeRpt(String ticket) {
        try {
        	RptAuthorizationRequestService authorizationRequestService = null;
            RptAuthorizationRequest rptAuthorizationRequest = new RptAuthorizationRequest(umaRpt.getRpt(), ticket);
            
            if (this.executor == null) {
            	authorizationRequestService = UmaClientFactory.instance().createAuthorizationRequestService(metadataConfiguration);
			}else{
				authorizationRequestService = UmaClientFactory.instance().createAuthorizationRequestService(metadataConfiguration,executor);				
			}
            
			RptAuthorizationResponse rptAuthorizationResponse = authorizationRequestService.requestRptPermissionAuthorization(
                    "Bearer " + umaAat.getAccessToken(),
                    getHost(metadataConfiguration.getIssuer()),
                    rptAuthorizationRequest);
            if (rptAuthorizationResponse == null) {
            	throw new ScimInitializationException("UMA ticket authorization response is invalid");
            }
            
            return true;
        } catch (ClientResponseFailure ex) {
			String errorMessage = (String) ex.getResponse().getEntity(String.class);
			throw new ScimInitializationException("Failed to authorize UMA ticket. Error: " + errorMessage, ex);
        } catch (MalformedURLException ex) {
			throw new ScimInitializationException("Failed to determine host by URI", ex);
		} catch (Exception ex) {
			throw new ScimInitializationException(ex.getMessage(), ex);
		}
	}

	@Override
	@Deprecated
	public ScimResponse retrievePerson(String id, String mediaType) throws IOException {
		return retrieveUser(id, new String[]{});
	}

	@Override
	public ScimResponse retrieveUser(String id, String[] attributesArray) throws IOException {

		ScimResponse scimResponse = super.retrieveUser(id, attributesArray);

		if (autorizeRpt(scimResponse)) {
			scimResponse = super.retrieveUser(id, attributesArray);
		}

		return scimResponse;
	}

	@Override
	@Deprecated
	public ScimResponse createPerson(User user, String mediaType) throws IOException, JAXBException {
		return createUser(user, new String[]{});
	}

	@Override
	public ScimResponse createUser(User user, String[] attributesArray) throws IOException {

		ScimResponse scimResponse = super.createUser(user, attributesArray);

		if (autorizeRpt(scimResponse)) {
			scimResponse = super.createUser(user, attributesArray);
		}

		return scimResponse;
	}

	@Override
	@Deprecated
	public ScimResponse updatePerson(User user, String id, String mediaType) throws IOException, JAXBException {
		return updateUser(user, id, new String[]{});
	}

	@Override
	public ScimResponse updateUser(User user, String id, String[] attributesArray) throws IOException {

		ScimResponse scimResponse = super.updateUser(user, id, attributesArray);

		if (autorizeRpt(scimResponse)) {
            scimResponse = super.updateUser(user, id, attributesArray);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse deletePerson(String uid) throws IOException {
		ScimResponse scimResponse = super.deletePerson(uid);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.deletePerson(uid);
		}

		return scimResponse;
	}

	@Override
	@Deprecated
	public ScimResponse retrieveGroup(String id, String mediaType) throws IOException {
		return retrieveGroup(id, new String[]{});
	}

	@Override
	public ScimResponse retrieveGroup(String id, String[] attributesArray) throws IOException {

		ScimResponse scimResponse = super.retrieveGroup(id, attributesArray);

		if (autorizeRpt(scimResponse)) {
			scimResponse = super.retrieveGroup(id, attributesArray);
		}

		return scimResponse;
	}

	@Override
	@Deprecated
	public ScimResponse createGroup(Group group, String mediaType) throws IOException, JAXBException {
		return createGroup(group, new String[]{});
	}

	@Override
	public ScimResponse createGroup(Group group, String[] attributesArray) throws IOException {

		ScimResponse scimResponse = super.createGroup(group, attributesArray);

		if (autorizeRpt(scimResponse)) {
			scimResponse = super.createGroup(group, attributesArray);
		}

		return scimResponse;
	}

	@Override
	@Deprecated
	public ScimResponse updateGroup(Group group, String id, String mediaType) throws IOException, JAXBException {
		return updateGroup(group, id, new String[]{});
	}

	@Override
	public ScimResponse updateGroup(Group group, String id, String[] attributesArray) throws IOException {

		ScimResponse scimResponse = super.updateGroup(group, id, attributesArray);

		if (autorizeRpt(scimResponse)) {
			scimResponse = super.updateGroup(group, id, attributesArray);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse deleteGroup(String id) throws IOException {
		ScimResponse scimResponse = super.deleteGroup(id);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.deleteGroup(id);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse createPersonString(String person, String mediaType) throws IOException, JAXBException {
		ScimResponse scimResponse = super.createPersonString(person, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.createPersonString(person, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse updatePersonString(String person, String id, String mediaType) throws IOException, JAXBException {
		ScimResponse scimResponse = super.updatePersonString(person, id, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.updatePersonString(person, id, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse createGroupString(String group, String mediaType) throws IOException, JAXBException {
		ScimResponse scimResponse = super.createGroupString(group, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.createGroupString(group, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse updateGroupString(String group, String id, String mediaType) throws IOException, JAXBException {
		ScimResponse scimResponse = super.updateGroupString(group, id, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.updateGroupString(group, id, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse bulkOperation(ScimBulkOperation operation, String mediaType) throws IOException, JAXBException {
		ScimResponse scimResponse = super.bulkOperation(operation, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.bulkOperation(operation, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse bulkOperationString(String operation, String mediaType) throws IOException {
		ScimResponse scimResponse = super.bulkOperationString(operation, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.bulkOperationString(operation, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse retrieveAllPersons(String mediaType) throws IOException {
		ScimResponse scimResponse = super.retrieveAllPersons(mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.retrieveAllPersons(mediaType);
		}

		return scimResponse;
	}

	/**
	 * User search via a filter with pagination and sorting
	 *
	 * @param filter
	 * @param startIndex
	 * @param count
	 * @param sortBy
	 * @param sortOrder
	 * @param attributesArray
	 * @return
     * @throws IOException
     */
	@Override
	public ScimResponse searchUsers(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {

		ScimResponse scimResponse = super.searchUsers(filter, startIndex, count, sortBy, sortOrder, attributesArray);

		if (autorizeRpt(scimResponse)) {
			scimResponse = super.searchUsers(filter, startIndex, count, sortBy, sortOrder, attributesArray);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse retrieveAllGroups(String mediaType) throws IOException {
		ScimResponse scimResponse = super.retrieveAllGroups(mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.retrieveAllGroups(mediaType);
		}

		return scimResponse;
	}

	/**
	 * Group search via a filter with pagination and sorting
	 *
	 * @param filter
	 * @param startIndex
	 * @param count
	 * @param sortBy
	 * @param sortOrder
	 * @param attributesArray
	 * @return
	 * @throws IOException
	 */
	@Override
	public ScimResponse searchGroups(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {

		ScimResponse scimResponse = super.searchGroups(filter, startIndex, count, sortBy, sortOrder, attributesArray);

		if (autorizeRpt(scimResponse)) {
			scimResponse = super.searchGroups(filter, startIndex, count, sortBy, sortOrder, attributesArray);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse personSearch(String attribute, String value, String mediaType) throws IOException, JAXBException {
		ScimResponse scimResponse = super.personSearch(attribute, value, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.personSearch(attribute, value, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse personSearchByObject(String attribute, Object value, String valueMediaType, String outPutMediaType)
			throws IOException, JAXBException {
		ScimResponse scimResponse = super.personSearchByObject(attribute, value, valueMediaType, outPutMediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.personSearchByObject(attribute, value, valueMediaType, outPutMediaType);
		}

		return scimResponse;
	}
	
	@Override
	public ScimResponse searchPersons(String attribute, String value, String mediaType) throws IOException, JAXBException {
		ScimResponse scimResponse = super.personSearch(attribute, value, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.searchPersons(attribute, value, mediaType);
		}

		return scimResponse;
	}

	public ClientExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(ClientExecutor executor) {
		this.executor = executor;
	}

}
