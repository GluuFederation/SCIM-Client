/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.xdi.oxauth.client.TokenRequest;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.UmaTokenService;
import org.xdi.oxauth.client.uma.wrapper.UmaClient;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.crypto.OxAuthCryptoProvider;
import org.xdi.oxauth.model.uma.UmaMetadata;
import org.xdi.oxauth.model.uma.UmaTokenResponse;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.util.StringHelper;

import gluu.scim.client.exception.ScimInitializationException;

// import org.xdi.oxauth.model.util.JwtUtil;

/**
 * SCIM UMA client
 *
 * @author Yuriy Movchan
 * @author Yuriy Zabrovarnyy
 */
public class UmaScimClient extends AbstractScimClient {

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

    public UmaScimClient(String domain, String umaMetaDataUrl, String umaAatClientId, String umaAatClientJksPath, String umaAatClientJksPassword, String umaAatClientKeyId) {
        super(domain);
        this.umaMetaDataUrl = umaMetaDataUrl;
        this.umaAatClientId = umaAatClientId;
        this.umaAatClientJksPath = umaAatClientJksPath;
        this.umaAatClientJksPassword = umaAatClientJksPassword;
        this.umaAatClientKeyId = umaAatClientKeyId;
    }

    @Override
    protected void prepareRequest() {
        initUmaAuthentication();
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
        String[] headerKeyValues = StringHelper.split(permissionTicketResponse.toString(), ",");
        for (int i = 0; i < headerKeyValues.length; i++) {
        	if (headerKeyValues[i].startsWith("ticket=")) {
        		permissionTicket = headerKeyValues[i].substring(7);
        	}
		}
		
		if (StringHelper.isEmpty(permissionTicket)) {
			return false;
		}

        return obtainAuthorizedRpt(permissionTicket);
    }

    private void initUmaAuthentication() {
        long now = System.currentTimeMillis();

        // Get new access token only if is the previous one is missing or expired
        if (!isValidToken(now)) {
            lock.lock();
            try {
                now = System.currentTimeMillis();
                if (!isValidToken(now)) {
                    initUmaAat();
                    Calendar calendar = Calendar.getInstance();
                    if (this.umaAat.getExpiresIn() != null) {
                        calendar.add(Calendar.SECOND, this.umaAat.getExpiresIn());
                        calendar.add(Calendar.SECOND, -10); // Subtract 10 seconds to avoid
                    }
                    this.umaAatAccessTokenExpiration = calendar.getTimeInMillis();
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

    private void initUmaAat() {
        //the same as gluu.scim.client.auth.UmaScimClientImpl.initAccessToken() !?
        // Remove old tokens
        this.umaAat = null;

        // Get metadata configuration
        this.umaMetadata = UmaClientFactory.instance().createMetadataService(umaMetaDataUrl).getMetadata();

        if (umaMetadata == null) {
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

    private String getAuthorizedRpt(String ticket) {
        try {
            //No need for claims token. See comments on issue https://github.com/GluuFederation/SCIM-Client/issues/22
            String claimTokenFormat = "http://openid.net/specs/openid-connect-core-1_0.html#IDToken";
            String authzHeader = "Bearer " + umaAat.getAccessToken();

            UmaTokenService tokenService = UmaClientFactory.instance().createTokenService(umaMetadata);
            UmaTokenResponse rptResponse = tokenService.requestRpt(authzHeader, GrantType.OXAUTH_UMA_TICKET.getValue(), ticket, null, claimTokenFormat, null, null, null);

            if (rptResponse == null) {
                throw new ScimInitializationException("UMA RPT token response is invalid");
            }

            if (StringUtils.isBlank(rptResponse.getAccessToken())) {
                throw new ScimInitializationException("UMA RPT is invalid");
            }

            return rptResponse.getAccessToken();
        }
        catch (Exception ex) {
            throw new ScimInitializationException(ex.getMessage(), ex);
        }
    }

    private boolean obtainAuthorizedRpt(String ticket) {
        try {
            return StringUtils.isNotBlank(getAuthorizedRpt(ticket));
        } catch (Exception ex) {
            throw new ScimInitializationException(ex.getMessage(), ex);
        }
    }
}
