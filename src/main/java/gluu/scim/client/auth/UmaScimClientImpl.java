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
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.xdi.oxauth.client.TokenRequest;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.UmaTokenService;
import org.xdi.oxauth.client.uma.wrapper.UmaClient;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.crypto.OxAuthCryptoProvider;
import org.xdi.oxauth.model.uma.PermissionTicket;
import org.xdi.oxauth.model.uma.UmaMetadata;
import org.xdi.oxauth.model.uma.UmaTokenResponse;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.util.StringHelper;

import javax.xml.bind.JAXBException;
import java.io.IOException;
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
    private UmaMetadata umaMetadata;
    private Token umaAat;
    private String rpt;

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
        httpMethod.setRequestHeader("Authorization", "Bearer " + rpt);
    }

    private void initUmaAuthentication() {
        long now = System.currentTimeMillis();

        // Get new access token only if is the previous one is missing or expired
        if (!isValidToken(now)) {
            lock.lock();
            try {
                now = System.currentTimeMillis();
                if (!isValidToken(now)) {
                    initAccessToken();
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
        return this.umaAat != null && this.umaAat.getAccessToken() != null && !(this.umaAatAccessTokenExpiration <= now);

    }

    private void initAccessToken() {
        // Remove old tokens
        this.umaAat = null;

        // Get metadata configuration
        this.umaMetadata = UmaClientFactory.instance().createMetadataService(umaMetaDataUrl).getMetadata();

        if (umaMetadata == null) { // || !StringHelper.equals(umaMetadata.getVersion(), "1.0")
            throw new ScimInitializationException("Failed to load valid UMA metadata configuration");
        }

        // Get AAT
        try {
            if (StringHelper.isEmpty(umaAatClientJksPath) || StringHelper.isEmpty(umaAatClientJksPassword)) {
                throw new ScimInitializationException("UMA JKS keystore path or password is empty");
            }
            OxAuthCryptoProvider cryptoProvider;
            try {
                cryptoProvider = new OxAuthCryptoProvider(umaAatClientJksPath, umaAatClientJksPassword, null);
            }
            catch (Exception ex) {
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

            TokenRequest tokenRequest = new TokenRequest(GrantType.CLIENT_CREDENTIALS);
            tokenRequest.setAuthenticationMethod(AuthenticationMethod.PRIVATE_KEY_JWT);
            tokenRequest.setAuthUsername(umaAatClientId);
            tokenRequest.setCryptoProvider(cryptoProvider);
            tokenRequest.setAlgorithm(cryptoProvider.getSignatureAlgorithm(keyId));
            tokenRequest.setKeyId(keyId);
            tokenRequest.setAudience(umaMetadata.getTokenEndpoint());

            this.umaAat = UmaClient.request(umaMetadata.getTokenEndpoint(), tokenRequest);
        } catch (Exception ex) {
            throw new ScimInitializationException("Failed to get AAT token", ex);
        }

        if (this.umaAat == null) {
            throw new ScimInitializationException("Failed to get UMA AAT token");
        }
    }

    private boolean obtainAutorizedRpt(ScimResponse scimResponse) {
        if (scimResponse.getStatusCode() == 403) {
            // Forbidden : RPT is not authorized yet

            final PermissionTicket ticket;
            try {
                ticket = (new ObjectMapper()).readValue(scimResponse.getResponseBody(), PermissionTicket.class);
            } catch (Exception ex) {
                throw new ScimInitializationException("UMA ticket is invalid", ex);
            }

            if (StringHelper.isEmpty(ticket.getTicket())) {
                throw new ScimInitializationException("UMA ticket is invalid");
            }

            rpt = obtainAuthorizedRpt(ticket.getTicket());
            return StringUtils.isNotBlank(rpt);
        }

        return false;
    }

    private String obtainAuthorizedRpt(String ticket) {
        try {
            //No need for claims token. See comments on issue https://github.com/GluuFederation/SCIM-Client/issues/22
            String claimTokenFormat = "http://openid.net/specs/openid-connect-core-1_0.html#IDToken";
            String authzHeader="Bearer " + umaAat.getAccessToken();

            //UmaMetadata metadata=UmaClientFactory.instance().createMetadataService(umaMetaDataUrl).getMetadata();
            UmaTokenService tokenService = UmaClientFactory.instance().createTokenService(umaMetadata);
            UmaTokenResponse rptResponse = tokenService.requestRpt(authzHeader, GrantType.OXAUTH_UMA_TICKET.getValue(), ticket, null, claimTokenFormat, null, null, null);

            if (rptResponse == null)
                throw new ScimInitializationException("UMA RPT token response is invalid");
            else
            if (StringUtils.isBlank(rptResponse.getAccessToken()))
                throw new ScimInitializationException("UMA RPT is invalid");
System.out.println("@obtainAuthorizedRpt "+ ticket + " - " + rptResponse.getAccessToken());
            return rptResponse.getAccessToken();
        }
        catch (Exception ex) {
            throw new ScimInitializationException(ex.getMessage(), ex);
        }
    }

    @Override
    public ScimResponse retrievePerson(String uid, String mediaType) throws IOException {
        ScimResponse scimResponse = super.retrievePerson(uid, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.retrievePerson(uid, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse createPerson(ScimPerson person, String mediaType) throws IOException, JAXBException {
        ScimResponse scimResponse = super.createPerson(person, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.createPerson(person, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse updatePerson(ScimPerson person, String uid, String mediaType) throws IOException, JAXBException {
        ScimResponse scimResponse = super.updatePerson(person, uid, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.updatePerson(person, uid, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse deletePerson(String uid) throws IOException {
        ScimResponse scimResponse = super.deletePerson(uid);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.deletePerson(uid);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse retrieveGroup(String id, String mediaType) throws IOException {
        ScimResponse scimResponse = super.retrieveGroup(id, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.retrieveGroup(id, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse createGroup(ScimGroup group, String mediaType) throws IOException, JAXBException {
        ScimResponse scimResponse = super.createGroup(group, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.createGroup(group, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse updateGroup(ScimGroup group, String id, String mediaType) throws IOException, JAXBException {
        ScimResponse scimResponse = super.updateGroup(group, id, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.updateGroup(group, id, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse deleteGroup(String id) throws IOException {
        ScimResponse scimResponse = super.deleteGroup(id);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.deleteGroup(id);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse createPersonString(String person, String mediaType) throws IOException, JAXBException {
        ScimResponse scimResponse = super.createPersonString(person, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.createPersonString(person, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse updatePersonString(String person, String uid, String mediaType) throws JsonGenerationException,
            IOException, JAXBException {
        ScimResponse scimResponse = super.updatePersonString(person, uid, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.updatePersonString(person, uid, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse createGroupString(String group, String mediaType) throws IOException, JAXBException {
        ScimResponse scimResponse = super.createGroupString(group, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.createGroupString(group, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse updateGroupString(String group, String id, String mediaType) throws IOException, JAXBException {
        ScimResponse scimResponse = super.updateGroupString(group, id, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.updateGroupString(group, id, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse bulkOperation(ScimBulkOperation operation, String mediaType) throws IOException, JAXBException {
        ScimResponse scimResponse = super.bulkOperation(operation, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.bulkOperation(operation, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse bulkOperationString(String operation, String mediaType) throws IOException {
        ScimResponse scimResponse = super.bulkOperationString(operation, mediaType);
        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.bulkOperationString(operation, mediaType);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse retrieveAllPersons() throws IOException {
        ScimResponse scimResponse = super.retrieveAllPersons();
        if (obtainAutorizedRpt(scimResponse)) {
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

        if (obtainAutorizedRpt(scimResponse)) {
            scimResponse = super.searchPersons(filter, startIndex, count, sortBy, sortOrder, attributesArray);
        }

        return scimResponse;
    }

    @Override
    public ScimResponse retrieveAllGroups() throws IOException {
        ScimResponse scimResponse = super.retrieveAllGroups();
        if (obtainAutorizedRpt(scimResponse)) {
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

        if (obtainAutorizedRpt(scimResponse)) {
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
