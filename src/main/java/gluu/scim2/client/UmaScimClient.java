/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.scim.client.exception.ScimInitializationException;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.xdi.oxauth.client.TokenRequest;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.UmaTokenService;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.uma.PermissionTicket;
import org.xdi.oxauth.model.uma.UmaMetadata;
import org.xdi.oxauth.model.uma.UmaTokenResponse;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.util.StringHelper;

import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.concurrent.locks.ReentrantLock;

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
        return "Bearer " + rpt;
    }

    @Override
    protected boolean authorize(BaseClientResponse response) {
        if (response.getStatus() != Response.Status.FORBIDDEN.getStatusCode())
            return false;

        // Forbidden : RPT is not authorized yet
        PermissionTicket permissionTicket = null;
        try {
            permissionTicket = (PermissionTicket) response.getEntity(PermissionTicket.class);
        } catch (Exception ex) {
            throw new ScimInitializationException("UMA ticket is invalid", ex);
        }

        return obtainAuthorizedRpt(permissionTicket.getTicket());
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

    private void initUmaRpt() {
        // Remove old tokens
        this.umaAat = null;

        // Get metadata configuration
        this.umaMetadata = UmaClientFactory.instance().createMetadataService(umaMetaDataUrl).getMetadata();

        if ((umaMetadata == null)) {
            throw new ScimInitializationException("Failed to load valid UMA metadata configuration");
        }

        // Get AAT
        try {
            if (StringHelper.isEmpty(umaAatClientJksPath) || StringHelper.isEmpty(umaAatClientJksPassword)) {
                throw new ScimInitializationException("UMA JKS keystore path or password is empty");
            }

            TokenRequest tokenRequest = new TokenRequest(GrantType.CLIENT_CREDENTIALS);
            tokenRequest.setAuthenticationMethod(AuthenticationMethod.PRIVATE_KEY_JWT);
            tokenRequest.setAuthUsername(umaAatClientId);

            // todo UMA 2 : obtain token correctly
//            tokenRequest.setCryptoProvider(cryptoProvider);
//            tokenRequest.setAlgorithm(cryptoProvider.getSignatureAlgorithm(keyId));
//            tokenRequest.setKeyId(keyId);
//            tokenRequest.setAudience(umaMetadata.getTokenEndpoint());
//
//            this.umaAat = UmaClient.request(umaMetadata.getTokenEndpoint(), tokenRequest);
            this.umaAat = null;
        } catch (Exception ex) {
            throw new ScimInitializationException("Failed to get AAT token", ex);
        }

        if (this.umaAat == null) {
            throw new ScimInitializationException("Failed to get UMA AAT token");
        }
    }

    private String getAuthorizedRpt(String ticket) {
        try {

            // oxauth 3.1.0 supports only UMA 2 (no UMA 1.0.1 anymore),
            // Spec: https://docs.kantarainitiative.org/uma/ed/oauth-uma-grant-2.0-04.html#rfc.section.3.3.1
            // Since it is back-channel call (no user interaction) claimToken must contain all cliams that are used in RPT Authorization Policy Script
            // in our case cliamsToken is idToken. Please obtain id_token with all claims that are required by RPT script.
            String idToken = null; // todo id token with all claims that are used by RPT Authorization Policy script.
            String claimTokenFormat = "http://openid.net/specs/openid-connect-core-1_0.html#IDToken";

            UmaTokenService tokenService = UmaClientFactory.instance().createTokenService(umaMetaDataUrl);
            UmaTokenResponse rptResponse = tokenService.requestRpt("Bearer " + umaAat.getAccessToken(), GrantType.OXAUTH_UMA_TICKET.getValue(),
                    ticket, idToken, claimTokenFormat, null, null, null);

            if (rptResponse == null) {
                throw new ScimInitializationException("UMA RPT token response is invalid");
            }
            if (StringUtils.isBlank(rptResponse.getAccessToken())) {
                throw new ScimInitializationException("UMA RPT is invalid");
            }

            rpt = rptResponse.getAccessToken();
            return rpt;
        } catch (Exception ex) {
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
