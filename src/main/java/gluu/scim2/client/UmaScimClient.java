/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.scim.client.exception.ScimInitializationException;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.xdi.oxauth.client.uma.CreateRptService;
import org.xdi.oxauth.client.uma.RptAuthorizationRequestService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.wrapper.UmaClient;
import org.xdi.oxauth.model.uma.*;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.util.StringHelper;

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
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
        return "Bearer " + this.umaRpt.getRpt();
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

        return authorizeRpt(permissionTicket.getTicket());
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
            if (StringHelper.isEmpty(umaAatClientJksPath) || StringHelper.isEmpty(umaAatClientJksPassword)) {
                throw new ScimInitializationException("UMA JKS keystore path or password is empty");
            }
            this.umaAat = UmaClient.requestAat(metadataConfiguration.getTokenEndpoint(), umaAatClientJksPath, umaAatClientJksPassword, umaAatClientId, umaAatClientKeyId);
        } catch (Exception ex) {
            throw new ScimInitializationException("Failed to get AAT token", ex);
        }

        if (this.umaAat == null) {
            throw new ScimInitializationException("Failed to get UMA AAT token");
        }

        CreateRptService rptService = null;
        rptService = UmaClientFactory.instance().createRequesterPermissionTokenService(metadataConfiguration);
        // Get RPT
        this.umaRpt = null;
        try {
            umaRpt = rptService.createRPT("Bearer " + this.umaAat.getAccessToken(), new URL(metadataConfiguration.getIssuer()).getHost());
        } catch (MalformedURLException ex) {
            throw new ScimInitializationException("Failed to determine host by URI", ex);
        }

        if (this.umaRpt == null) {
            throw new ScimInitializationException("Failed to get UMA RPT token");
        }
    }

    private boolean authorizeRpt(String ticket) {
        try {
            RptAuthorizationRequestService authorizationRequestService = null;
            RptAuthorizationRequest rptAuthorizationRequest = new RptAuthorizationRequest(umaRpt.getRpt(), ticket);

            authorizationRequestService = UmaClientFactory.instance().createAuthorizationRequestService(metadataConfiguration);

            RptAuthorizationResponse rptAuthorizationResponse = authorizationRequestService.requestRptPermissionAuthorization(
                    "Bearer " + umaAat.getAccessToken(),
                    new URL(metadataConfiguration.getIssuer()).getHost(),
                    rptAuthorizationRequest);
            if (rptAuthorizationResponse == null) {
                throw new ScimInitializationException("UMA ticket authorization response is invalid");
            }

            return true;
        } catch (MalformedURLException ex) {
            throw new ScimInitializationException("Failed to determine host by URI", ex);
        } catch (Exception ex) {
            throw new ScimInitializationException(ex.getMessage(), ex);
        }
    }
}
