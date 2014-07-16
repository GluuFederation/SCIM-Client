package gluu.scim.client.auth;

import gluu.scim.client.BaseScimClientImpl;
import gluu.scim.client.ScimResponse;
import gluu.scim.client.exception.ScimInitializationException;
import gluu.scim.client.model.ScimBulkOperation;
import gluu.scim.client.model.ScimGroup;
import gluu.scim.client.model.ScimPerson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.locks.ReentrantLock;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.xdi.oxauth.client.uma.AuthorizationRequestService;
import org.xdi.oxauth.client.uma.RequesterPermissionTokenService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.wrapper.UmaClient;
import org.xdi.oxauth.model.uma.AuthorizationResponse;
import org.xdi.oxauth.model.uma.MetadataConfiguration;
import org.xdi.oxauth.model.uma.RequesterPermissionTokenResponse;
import org.xdi.oxauth.model.uma.ResourceSetPermissionTicket;
import org.xdi.oxauth.model.uma.RptAuthorizationRequest;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.util.StringHelper;

/**
 * SCIM UMA client
 * 
 * @author Yuriy Movchan Date: 08/23/2013
 */
public class UmaScimClientImpl extends BaseScimClientImpl {

	private static final long serialVersionUID = 7099883500099353832L;

	// UMA
	private MetadataConfiguration metadataConfiguration;
	private Token umaAat;
	private RequesterPermissionTokenResponse umaRpt;

	private String umaMetaDataUrl;

	private String umaAatClientId;
	private String umaAatClientSecret;
	
	private long umaAatAccessTokenExpiration = 0l; // When the "accessToken" will expire;

	private final ReentrantLock lock = new ReentrantLock();

	public UmaScimClientImpl(String domain, String umaMetaDataUrl, String umaAatClientId, String umaAatClientSecret) {
		super(domain);
		this.umaMetaDataUrl = umaMetaDataUrl;
		this.umaAatClientId = umaAatClientId;
		this.umaAatClientSecret = umaAatClientSecret;
	}

	@Override
	protected void init() {
		initUmaAuthentication();
	}

	@Override
	protected void addAuthenticationHeader(HttpMethodBase httpMethod) {
		httpMethod.setRequestHeader("Authorization", "Bearer " + this.umaRpt.getToken());
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
		this.metadataConfiguration = UmaClientFactory.instance().createMetaDataConfigurationService(umaMetaDataUrl)
				.getMetadataConfiguration();

		if ((metadataConfiguration == null) || !StringHelper.equals(metadataConfiguration.getVersion(), "1.0")) {
			throw new ScimInitializationException("Failed to load valid UMA metadata configuration");
		}

		// Get AAT
		try {
			this.umaAat = UmaClient.requestAat(metadataConfiguration.getTokenEndpoint(), umaAatClientId, umaAatClientSecret);
		} catch (ClientResponseFailure ex) {
			String errorMessage = (String) ex.getResponse().getEntity(String.class);
			throw new ScimInitializationException("Failed to get AAT token. Error: " + errorMessage, ex);
		} catch (Exception ex) {
			throw new ScimInitializationException("Failed to get AAT token", ex);
		}

		if (this.umaAat == null) {
			throw new ScimInitializationException("Failed to get UMA AAT token");
		}

		RequesterPermissionTokenService rptService = UmaClientFactory.instance().createRequesterPermissionTokenService(metadataConfiguration);

		// Get RPT
		this.umaRpt = null;
		try {
			umaRpt = rptService.getRequesterPermissionToken("Bearer " + this.umaAat.getAccessToken(), getHost(metadataConfiguration.getIssuer()));
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
        	
            final ResourceSetPermissionTicket resourceSetPermissionTicket;
			try {
				resourceSetPermissionTicket = (new ObjectMapper()).readValue(scimResponse.getResponseBody(), ResourceSetPermissionTicket.class);
			} catch (Exception ex) {
    			throw new ScimInitializationException("UMA ticket is invalid", ex);
			}

			authorizeRpt(resourceSetPermissionTicket.getTicket());

			if ((resourceSetPermissionTicket == null) || StringHelper.isEmpty(resourceSetPermissionTicket.getTicket())) {
    			throw new ScimInitializationException("UMA ticket is invalid");
            }
            
            return true;
        }

		return false;
	}

	private boolean authorizeRpt(String ticket) {
        // Authorize RPT token to access permission ticket
        ClientResponse<AuthorizationResponse> clientAuthorizationResponse = null;
        try {
            RptAuthorizationRequest rptAuthorizationRequest = new RptAuthorizationRequest(umaRpt.getToken(), ticket);
            AuthorizationRequestService authorizationRequestService = UmaClientFactory.instance().createAuthorizationRequestService(metadataConfiguration); 

            clientAuthorizationResponse = authorizationRequestService.requestRptPermissionAuthorization(
                    "Bearer " + umaAat.getAccessToken(),
                    getHost(metadataConfiguration.getIssuer()),
                    rptAuthorizationRequest);
            AuthorizationResponse authorizationResponse = clientAuthorizationResponse.getEntity(AuthorizationResponse.class);
            if ((authorizationResponse == null) || (clientAuthorizationResponse.getStatus() != Response.Status.OK.getStatusCode())) {
            	throw new ScimInitializationException("UMA ticket authorization response is invalid");
            }
            
            return true;
        } catch (ClientResponseFailure ex) {
			String errorMessage = (String) ex.getResponse().getEntity(String.class);
			throw new ScimInitializationException("Failed to authorize UMA ticket. Error: " + errorMessage, ex);
        } catch (MalformedURLException ex) {
			throw new ScimInitializationException("Failed to determine host by URI", ex);
		}
	}

	@Override
	public ScimResponse retrievePerson(String uid, String mediaType) throws HttpException, IOException {
		ScimResponse scimResponse = super.retrievePerson(uid, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.retrievePerson(uid, mediaType);
		}
		
		return scimResponse;
	}

	@Override
	public ScimResponse createPerson(ScimPerson person, String mediaType) throws JsonGenerationException, JsonMappingException,
			IOException, JAXBException {
		ScimResponse scimResponse = super.createPerson(person, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.createPerson(person, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse updatePerson(ScimPerson person, String uid, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {
		ScimResponse scimResponse = super.updatePerson(person, uid, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.updatePerson(person, uid, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse deletePerson(String uid) throws HttpException, IOException {
		ScimResponse scimResponse = super.deletePerson(uid);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.deletePerson(uid);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse retrieveGroup(String id, String mediaType) throws HttpException, IOException {
		ScimResponse scimResponse = super.retrieveGroup(id, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.retrieveGroup(id, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse createGroup(ScimGroup group, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {
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
	public ScimResponse deleteGroup(String id) throws HttpException, IOException {
		ScimResponse scimResponse = super.deleteGroup(id);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.deleteGroup(id);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse createPersonString(String person, String mediaType) throws JsonGenerationException, JsonMappingException,
			IOException, JAXBException {
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
	public ScimResponse createGroupString(String group, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {
		ScimResponse scimResponse = super.createGroupString(group, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.createGroupString(group, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse updateGroupString(String group, String id, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {
		ScimResponse scimResponse = super.updateGroupString(group, id, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.updateGroupString(group, id, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse bulkOperation(ScimBulkOperation operation, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {
		ScimResponse scimResponse = super.bulkOperation(operation, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.bulkOperation(operation, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse bulkOperationString(String operation, String mediaType) throws HttpException, IOException {
		ScimResponse scimResponse = super.bulkOperationString(operation, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.bulkOperationString(operation, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse retrieveAllPersons(String mediaType) throws HttpException, IOException {
		ScimResponse scimResponse = super.retrieveAllPersons(mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.retrieveAllPersons(mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse retrieveAllGroups(String mediaType) throws HttpException, IOException {
		ScimResponse scimResponse = super.retrieveAllGroups(mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.retrieveAllGroups(mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse personSearch(String attribute, String value, String mediaType) throws JsonGenerationException,
			JsonMappingException, IOException, JAXBException {
		ScimResponse scimResponse = super.personSearch(attribute, value, mediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.personSearch(attribute, value, mediaType);
		}

		return scimResponse;
	}

	@Override
	public ScimResponse personSearchByObject(String attribute, Object value, String valueMediaType, String outPutMediaType)
			throws JsonGenerationException, JsonMappingException, IOException, JAXBException {
		ScimResponse scimResponse = super.personSearchByObject(attribute, value, valueMediaType, outPutMediaType);
		if (autorizeRpt(scimResponse)) {
            scimResponse = super.personSearchByObject(attribute, value, valueMediaType, outPutMediaType);
		}

		return scimResponse;
	}

}
