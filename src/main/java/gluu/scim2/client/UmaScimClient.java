/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.scim.client.exception.ScimInitializationException;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.xdi.oxauth.client.TokenRequest;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.UmaTokenService;
import org.xdi.oxauth.client.uma.wrapper.UmaClient;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.crypto.OxAuthCryptoProvider;
import org.xdi.oxauth.model.uma.UmaMetadata;
import org.apache.commons.lang.StringUtils;
import org.xdi.oxauth.model.uma.UmaTokenResponse;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.util.StringHelper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SCIM UMA client
 *
 * @author Yuriy Movchan
 * @author Yuriy Zabrovarnyy
 * updated by jgomer on 2017-08-06.
 */
public class UmaScimClient extends AbstractScimClient{

    private static final long serialVersionUID = 7099883500099353832L;

    private UmaMetadata umaMetadata;
    private RPInfo rpInfo;
    private String rpt;

    public UmaScimClient(String protectedResource, String umaMetaDataUrl, RPInfo reqPartyInfo){
        super(protectedResource);
        this.rpInfo=reqPartyInfo;
        rpInfo.setRegisteredScopes(Collections.singletonList("uma_authorization"));
        umaMetadata = UmaClientFactory.instance().createMetadataService(umaMetaDataUrl).getMetadata();

        if (umaMetadata == null) {
            throw new ScimInitializationException("Failed to load valid UMA metadata configuration");
        }

    }

    protected String getAuthenticationHeader(){
        return (rpt==null) ? null : "Bearer " + rpt;
    }

    protected boolean authorizeIfNeeded(BaseClientResponse response) {

        try {
            ScimInitializationException t = null;
            Status status = Response.Status.fromStatusCode(response.getStatus());
            String headerValue = null;
            boolean authzPerformed = true;

            switch (status) {
                case UNAUTHORIZED:  //See https://docs.kantarainitiative.org/uma/ed/oauth-uma-grant-2.0-04.html#rfc.section.3.2.1
                    String ticket = null;
                    /*
                    getHeaderString causes error...
                    headerValue = response.getHeaderString("WWW-Authenticate");
                    */
                    headerValue=response.getHeaders().get("WWW-Authenticate").toString();
                    //capture ticket value surrounded by double quotes
                    Matcher m = Pattern.compile("ticket=\"(.+)\"").matcher(headerValue);
                    if (m.find()) {
                        ticket = m.group(1);
                        if (StringUtils.isNotEmpty(ticket))
                            rpt = getAuthorizedRpt(ticket);
                    }
//System.out.println("rpt null? " + (rpt == null) + " ticket " + ticket);
                    if (rpt == null)
                        t = new ScimInitializationException("Error getting RPT from ticket " + ticket);

                    break;
                case FORBIDDEN: //Abnormal response
                    authzPerformed = false;
                    //Try refreshing RPT for the next call
                    rpt = null;
                    break;
                default:    //This should be a normal (authorized response)
                    authzPerformed = false;
            }
            if (t == null)
                return authzPerformed;
            else
                throw t;
        }
        finally {
            response.releaseConnection();
        }

    }

    private String getAccessToken() {

        Token token=null;
        try {
            OxAuthCryptoProvider cryptoProvider;
            try {
                cryptoProvider = new OxAuthCryptoProvider(rpInfo.getClientJksPath(), rpInfo.getClientJksPassword(), null);
            }
            catch (Exception ex) {
                throw new ScimInitializationException("Failed to initialize crypto provider");
            }

            String keyId = rpInfo.getClientKeyId();
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
            tokenRequest.setAuthUsername(rpInfo.getClientId());
            tokenRequest.setCryptoProvider(cryptoProvider);
            tokenRequest.setAlgorithm(cryptoProvider.getSignatureAlgorithm(keyId));
            tokenRequest.setKeyId(keyId);
            tokenRequest.setAudience(umaMetadata.getTokenEndpoint());

            token = UmaClient.request(umaMetadata.getTokenEndpoint(), tokenRequest);
        }
        catch (Exception ex) {
            throw new ScimInitializationException("Failed to get access token", ex);
        }
        if (token == null) {
            throw new ScimInitializationException("Failed to get UMA access token");
        }
        return token.getAccessToken();

    }

    private String getAuthorizedRpt(String ticket){

        //See https://docs.kantarainitiative.org/uma/ed/oauth-uma-grant-2.0-04.html#rfc.section.3.3.1
        try {
            //No need for claims token. See comments on issue https://github.com/GluuFederation/SCIM-Client/issues/22
            //String claimTokenFormat = "http://openid.net/specs/openid-connect-core-1_0.html#IDToken";

            UmaTokenService tokenService = UmaClientFactory.instance().createTokenService(umaMetadata);
            UmaTokenResponse rptResponse = tokenService.requestRpt("Bearer " + getAccessToken(), GrantType.OXAUTH_UMA_TICKET.getValue(),
                    ticket,null, null, null, null, rpInfo.scopesAsString());    //null "scim_access"

            if (rptResponse == null)
                throw new ScimInitializationException("UMA RPT token response is invalid");
            else {
                String newRpt=rptResponse.getAccessToken();
                if (StringUtils.isEmpty(newRpt))
                    throw new ScimInitializationException("UMA RPT is invalid");
                return newRpt;
            }
        }
        catch (Exception ex) {
            throw new ScimInitializationException(ex.getMessage(), ex);
        }
    }

}