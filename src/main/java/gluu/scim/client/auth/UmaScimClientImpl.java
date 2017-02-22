/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client.auth;

import gluu.scim.client.BaseScimClientImpl;
import gluu.scim.client.ScimResponse;
import gluu.scim.client.exception.ScimInitializationException;
import gluu.scim.client.model.ScimBulkOperation;
import gluu.scim.client.model.ScimGroup;
import gluu.scim.client.model.ScimPerson;
import org.apache.commons.httpclient.HttpMethodBase;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.xdi.oxauth.client.TokenRequest;
import org.xdi.oxauth.client.uma.CreateRptService;
import org.xdi.oxauth.client.uma.RptAuthorizationRequestService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.wrapper.UmaClient;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.crypto.OxAuthCryptoProvider;
import org.xdi.oxauth.model.crypto.signature.SignatureAlgorithm;
import org.xdi.oxauth.model.uma.*;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.util.StringHelper;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SCIM UMA client
 * 
 * @author Yuriy Movchan
 * @author Yuriy Zabrovarnyy
 */
public class UmaScimClientImpl extends BaseScimClientImpl {

	private static final long serialVersionUID = 7099883500099353832L;

	// UMA
	private UmaConfiguration metadataConfiguration;
	private Token umaAat;
	private RPTResponse umaRpt;

	private String umaMetaDataUrl;

	private String umaAatClientId;
	private String umaAatClientKeyId;
	private String umaAatClientJksPath;
	private String umaAatClientJksPassword;
	
	private long umaAatAccessTokenExpiration = 0l; // When the "accessToken" will expire;

	private final ReentrantLock lock = new ReentrantLock();

	public UmaScimClientImpl(String domain, String umaMetaDataUrl, String umaAatClientId, String umaAatClientJksPath, String umaAatClientJksPassword, String umaAatClientKeyId) {
		super(domain);
		this.umaMetaDataUrl = umaMetaDataUrl;
		this.umaAatClientId = umaAatClientId;
		this.umaAatClientJksPath = umaAatClientJksPath;
		this.umaAatClientJksPassword = umaAatClientJksPassword;
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
		if ((this.umaAat == null) || (this.umaAat.getAccessToken() == null) ||
				(this.umaAatAccessTokenExpiration <= now)) {
			return false;
		}

		return true;
	}

	private void initUmaRpt() {
		// Remove old tokens
 		this.umaAat = null;
		this.umaRpt = null;

		// Get metadata configuration
		this.metadataConfiguration = UmaClientFactory.instance().createMetaDataConfigurationService(umaMetaDataUrl).getMetadataConfiguration();

		if ((metadataConfiguration == null) || !StringHelper.equals(metadataConfiguration.getVersion(), "1.0")) {
			throw new ScimInitializationException("Failed to load valid UMA metadata configuration");
		}

		// Get AAT
		org.xdi.oxauth.model.crypto.PrivateKey privateKey = null;
		try {
			if (StringHelper.isEmpty(umaAatClientJksPath) || StringHelper.isEmpty(umaAatClientJksPassword)) {
				throw new ScimInitializationException("UMA JKS keystore path or password is empty");
			}
			OxAuthCryptoProvider cryptoProvider;
			try {
				cryptoProvider = new OxAuthCryptoProvider(umaAatClientJksPath, umaAatClientJksPassword, null);
			} catch (Exception ex) {
				throw new ScimInitializationException("Failed to initialize crypto provider");
			}

			String keyId = umaAatClientKeyId;
	        if (StringHelper.isEmpty(keyId)) {
	        	// Get first key
	        	List<String> aliases = cryptoProvider.getKeyAliases();
	        	if (aliases.size() > 0) {
	        		keyId = aliases.get(0);
	        	}
	        }

	        if (StringHelper.isEmpty(keyId)) {
				throw new ScimInitializationException("UMA keyId is empty");
			}
	        
	        SignatureAlgorithm algorithm = cryptoProvider.getSignatureAlgorithm(keyId);
			TokenRequest tokenRequest = TokenRequest.builder().aat().grantType(GrantType.CLIENT_CREDENTIALS).build();

			tokenRequest.setAuthenticationMethod(AuthenticationMethod.PRIVATE_KEY_JWT);
	        tokenRequest.setAuthUsername(umaAatClientId);
	        tokenRequest.setCryptoProvider(cryptoProvider);
	        tokenRequest.setAlgorithm(algorithm);
	        tokenRequest.setKeyId(keyId);
	        tokenRequest.setAudience(metadataConfiguration.getTokenEndpoint());

			this.umaAat = UmaClient.request(metadataConfiguration.getTokenEndpoint(), tokenRequest);
		} catch (Exception ex) {
			throw new ScimInitializationException("Failed to get AAT token", ex);
		}

		if (this.umaAat == null) {
			throw new ScimInitializationException("Failed to get UMA AAT token");
		}

		CreateRptService rptService = UmaClientFactory.instance().createRequesterPermissionTokenService(metadataConfiguration);

		// Get RPT
		this.umaRpt = null;
		try {
			umaRpt = rptService.createRPT("Bearer " + this.umaAat.getAccessToken(), getHost(metadataConfiguration.getIssuer()));
		}catch (MalformedURLException ex) {
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
            RptAuthorizationRequest rptAuthorizationRequest = new RptAuthorizationRequest(umaRpt.getRpt(), ticket);
			RptAuthorizationRequestService authorizationRequestService = UmaClientFactory.instance().createAuthorizationRequestService(metadataConfiguration);

			RptAuthorizationResponse rptAuthorizationResponse = authorizationRequestService.requestRptPermissionAuthorization(
                    "Bearer " + umaAat.getAccessToken(),
                    getHost(metadataConfiguration.getIssuer()),
                    rptAuthorizationRequest);
            if (rptAuthorizationResponse == null) {
            	throw new ScimInitializationException("UMA ticket authorization response is invalid");
            }
            
            return true;
        }catch (MalformedURLException ex) {
			throw new ScimInitializationException("Failed to determine host by URI", ex);
		} catch (Exception ex) {
			throw new ScimInitializationException(ex.getMessage(), ex);
		}
	}

	@Override
	public ScimResponse retrievePerson(String uid, String mediaType) throws IOException {
		ScimResponse scimResponse = super.retrievePerson(uid, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.retrievePerson(uid, mediaType);
		}
		
		return scimResponse;
	}

	@Override
	public ScimResponse createPerson(ScimPerson person, String mediaType) throws IOException, JAXBException {
		ScimResponse scimResponse = super.createPerson(person, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.createPerson(person, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse updatePerson(ScimPerson person, String uid, String mediaType) throws IOException, JAXBException {
		ScimResponse scimResponse = super.updatePerson(person, uid, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.updatePerson(person, uid, mediaType);
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
	public ScimResponse retrieveGroup(String id, String mediaType) throws IOException {
		ScimResponse scimResponse = super.retrieveGroup(id, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.retrieveGroup(id, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse createGroup(ScimGroup group, String mediaType) throws IOException, JAXBException {
		ScimResponse scimResponse = super.createGroup(group, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.createGroup(group, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse updateGroup(ScimGroup group, String id, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {
		ScimResponse scimResponse = super.updateGroup(group, id, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.updateGroup(group, id, mediaType);
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
	public ScimResponse updatePersonString(String person, String uid, String mediaType) throws JsonGenerationException,
			JsonMappingException, UnsupportedEncodingException, IOException, JAXBException {
		ScimResponse scimResponse = super.updatePersonString(person, uid, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.updatePersonString(person, uid, mediaType);
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
	public ScimResponse retrieveAllPersons() throws IOException {
		ScimResponse scimResponse = super.retrieveAllPersons();
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.retrieveAllPersons();
		}

		return scimResponse;
	}

	/**
	 * Person search via a filter with pagination and sorting
	 *
	 * @param filter
	 * @param startIndex
	 * @param count
	 * @param sortBy
	 * @param sortOrder
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
	 */
	@Override
	public ScimResponse searchPersons(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {

		ScimResponse scimResponse = super.searchPersons(filter, startIndex, count, sortBy, sortOrder, attributesArray);

		if (autorizeRpt(scimResponse)) {
			scimResponse = super.searchPersons(filter, startIndex, count, sortBy, sortOrder, attributesArray);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse retrieveAllGroups() throws IOException {
		ScimResponse scimResponse = super.retrieveAllGroups();
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.retrieveAllGroups();
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
	 * @return ScimResponse
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

	/*
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
	public ScimResponse searchPersons(String attribute, String value,
			String mediaType) throws JsonGenerationException,
			JsonMappingException, IOException, JAXBException {
		ScimResponse scimResponse = super.searchPersons(attribute, value, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.searchPersons(attribute, value, mediaType);
		}

		return scimResponse;
	}
	*/
}
