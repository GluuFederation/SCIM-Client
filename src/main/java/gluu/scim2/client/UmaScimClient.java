/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class UmaScimClient extends AbstractScimClient {

    private static final long serialVersionUID = 7099883500099353832L;

    private Logger logger = LogManager.getLogger(getClass());
    // UMA
    private String rpt;

    private String umaAatClientId;
    private String umaAatClientKeyId;
    private String umaAatClientJksPath;
    private String umaAatClientJksPassword;

    public UmaScimClient(String domain, String umaAatClientId, String umaAatClientJksPath, String umaAatClientJksPassword, String umaAatClientKeyId) {
        super(domain);
        this.umaAatClientId = umaAatClientId;
        this.umaAatClientJksPath = umaAatClientJksPath;
        this.umaAatClientJksPassword = umaAatClientJksPassword;
        this.umaAatClientKeyId = umaAatClientKeyId;
    }

    @Override
    protected String getAuthenticationHeader() {
    	return StringHelper.isEmpty(rpt) ?  null : "Bearer " + rpt;
    }

    @Override
    protected boolean authorize(Response response) {

        boolean value = false;

        if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {

            try {
                String permissionTicketResponse = response.getHeaderString("WWW-Authenticate");
                String permissionTicket = null;
                String asUri = null;

                String[] headerKeyValues = StringHelper.split(permissionTicketResponse, ",");
                for (String headerKeyValue : headerKeyValues) {
                    if (headerKeyValue.startsWith("ticket=")) {
                        permissionTicket = headerKeyValue.substring(7);
                    }
                    if (headerKeyValue.startsWith("as_uri=")) {
                        asUri = headerKeyValue.substring(7);
                    }
                }
                value= !StringHelper.isEmpty(asUri) && !StringHelper.isEmpty(permissionTicket) && obtainAuthorizedRpt(asUri, permissionTicket);
            }
            catch (Exception e) {
                throw new ScimInitializationException(e.getMessage(), e);
            }
        }

        return value;
    }

    private boolean obtainAuthorizedRpt(String asUri, String ticket) {

        try {
            return StringUtils.isNotBlank(getAuthorizedRpt(asUri, ticket));
        }
        catch (Exception e) {
            throw new ScimInitializationException(e.getMessage(), e);
        }

    }

    private String getAuthorizedRpt(String asUri, String ticket) {

        try {
        	// Get metadata configuration
        	UmaMetadata umaMetadata = UmaClientFactory.instance().createMetadataService(asUri).getMetadata();
            if (umaMetadata == null) {
                throw new ScimInitializationException(String.format("Failed to load valid UMA metadata configuration from: %s", asUri));
            }

        	TokenRequest tokenRequest = getAuthorizationTokenRequest(umaMetadata);
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

    private TokenRequest getAuthorizationTokenRequest(UmaMetadata umaMetadata) {

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
        }
        catch (Exception ex) {
            throw new ScimInitializationException("Failed to get client token", ex);
        }

    }

}
