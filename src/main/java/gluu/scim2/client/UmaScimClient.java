/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.xdi.oxauth.client.TokenRequest;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.UmaTokenService;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.crypto.OxAuthCryptoProvider;
import org.xdi.oxauth.model.token.ClientAssertionType;
import org.xdi.oxauth.model.uma.UmaMetadata;
import org.xdi.oxauth.model.uma.UmaTokenResponse;
import org.xdi.util.StringHelper;

import gluu.scim2.client.exception.ScimInitializationException;

/**
 * SCIM UMA client
 *
 * @author Yuriy Movchan
 * @author Yuriy Zabrovarnyy
 * Updated by jgomer on 2017-10-19
 */
public abstract class UmaScimClient extends AbstractScimClient {
    public UmaScimClient(String abc){
        super(abc);
    }
/*
    private static final long serialVersionUID = 7099883500099353832L;

    // UMA
    private String rpt;

    private String umaAatClientId;
    private String umaAatClientKeyId;
    private String umaAatClientJksPath;
    private String umaAatClientJksPassword;

    private long umaAatAccessTokenExpiration = 0l; // When the "accessToken" will expire;

    private final ReentrantLock lock = new ReentrantLock();

    public UmaScimClient(String domain, String umaAatClientId, String umaAatClientJksPath, String umaAatClientJksPassword, String umaAatClientKeyId) {
        super(domain);
        this.umaAatClientId = umaAatClientId;
        this.umaAatClientJksPath = umaAatClientJksPath;
        this.umaAatClientJksPassword = umaAatClientJksPassword;
        this.umaAatClientKeyId = umaAatClientKeyId;
    }

    @Override
    protected void prepareRequest() {
    }

    @Override
    protected String getAuthenticationHeader() {
    	if (StringHelper.isEmpty(rpt)) {
    		return null;
    	}
        
    	return "Bearer " + rpt;
    }

    @Override
    protected boolean authorize(BaseClientResponse response) {
        if (response.getStatus() != Response.Status.UNAUTHORIZED.getStatusCode()) {
            return false;
        }

        // Forbidden : RPT is not authorized yet
        Object permissionTicketResponse = null;
        try {
        	permissionTicketResponse = response.getResponseHeader("WWW-Authenticate");
        } catch (Exception ex) {
            throw new ScimInitializationException("UMA permissions response is invalid", ex);
        }
        
        String permissionTicket = null;
        String asUri = null;
        String[] headerKeyValues = StringHelper.split(permissionTicketResponse.toString(), ",");
        for (int i = 0; i < headerKeyValues.length; i++) {
        	if (headerKeyValues[i].startsWith("ticket=")) {
        		permissionTicket = headerKeyValues[i].substring(7);
        	}
        	if (headerKeyValues[i].startsWith("as_uri=")) {
        		asUri = headerKeyValues[i].substring(7);
        	}
		}
		
		if (StringHelper.isEmpty(asUri) || StringHelper.isEmpty(permissionTicket)) {
			return false;
		}

        return obtainAuthorizedRpt(asUri, permissionTicket);
    }

    private TokenRequest getAuthorizationTokerRequest(UmaMetadata umaMetadata) {
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

            return tokenRequest;
        } catch (Exception ex) {
            throw new ScimInitializationException("Failed to get client token", ex);
        }
    }

    private String getAuthorizedRpt(String asUri, String ticket) {
        try {
        	// Get metadata configuration
        	UmaMetadata umaMetadata = UmaClientFactory.instance().createMetadataService(asUri).getMetadata();
            if (umaMetadata == null) {
                throw new ScimInitializationException(String.format("Failed to load valid UMA metadata configuration from: %s", asUri));
            }

        	TokenRequest tokenRequest = getAuthorizationTokerRequest(umaMetadata);
            //No need for claims token. See comments on issue https://github.com/GluuFederation/SCIM-Client/issues/22

            UmaTokenService tokenService = UmaClientFactory.instance().createTokenService(umaMetadata);
            UmaTokenResponse rptResponse = tokenService.requestJwtAuthorizationRpt(ClientAssertionType.JWT_BEARER.toString(), tokenRequest.getClientAssertion(), GrantType.OXAUTH_UMA_TICKET.getValue(), ticket, null, null, null, null, null); //ClaimTokenFormatType.ID_TOKEN.getValue()

            if (rptResponse == null) {
                throw new ScimInitializationException("UMA RPT token response is invalid");
            }

            if (StringUtils.isBlank(rptResponse.getAccessToken())) {
                throw new ScimInitializationException("UMA RPT is invalid");
            }
            
            this.rpt = rptResponse.getAccessToken();

            return rpt;
        }
        catch (Exception ex) {
            throw new ScimInitializationException(ex.getMessage(), ex);
        }
    }

    private boolean obtainAuthorizedRpt(String asUri, String ticket) {
        try {
            return StringUtils.isNotBlank(getAuthorizedRpt(asUri, ticket));
        } catch (Exception ex) {
            throw new ScimInitializationException(ex.getMessage(), ex);
        }
    }
    */
}
