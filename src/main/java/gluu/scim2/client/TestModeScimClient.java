package gluu.scim2.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.xdi.oxauth.client.*;
import org.xdi.oxauth.model.common.*;
import org.xdi.oxauth.model.util.Util;
import org.xdi.oxauth.model.register.ApplicationType;

import java.net.URL;
import java.util.*;

/**
 * Created by jgomer on 2017-07-13.
 */
public class TestModeScimClient extends AbstractScimClient {

    //private String authz_code;
    //private String authzEndpoint;
    private String access_token;
    private String refresh_token;

    private String tokenEndpoint;   //Url of authorization's server token endpoint i.e. https://<host:port>/oxauth/restv1/token
    private String registrationEndpoint; //OpenId Connect endpoint for registration, i.e. https://<host:port>/oxauth/restv1/register

    private static long clientExpiration=0;
    private static String clientId;
    private static String password;
    private static ObjectMapper mapper=new ObjectMapper();

    //private static final List<String> SCOPES=Arrays.asList("openid", "profile");
    private static final List<ResponseType> RESPONSE_TYPES=Arrays.asList(ResponseType.TOKEN);
    private static final String REDIRECT_URI="http://localhost/";    //a dummy value just to stay in compliance with specs (see redirect uris for native clients)

    /**
     * Constructs a TestModeScimClient instance
     * @param serviceUrl A string denoting the root URL of the protected resource, i.e. something like
     *                   {@code https://<host:port>/identity/restv1}
     * @param OIDCMetadataUrl String url of the openId connect metadata document
     */
    public TestModeScimClient(String serviceUrl, String OIDCMetadataUrl) throws Exception {

        super(serviceUrl);

        //Extract token, registration, and authz endpoints from metadata URL
        JsonNode tree=mapper.readTree(new URL(OIDCMetadataUrl));
        this.registrationEndpoint= tree.get("registration_endpoint").asText();
        this.tokenEndpoint=tree.get("token_endpoint").asText();
        //this.authzEndpoint=tree.get("authorization_endpoint").asText();

        if (Util.allNotBlank(registrationEndpoint, tokenEndpoint /*, authzEndpoint*/)) {
            triggerRegistrationIfNeeded();
            updateTokens(GrantType.CLIENT_CREDENTIALS);
        }
        else
            throw new Exception("Couldn't extract endpoints from OIDC metadata URL: " + OIDCMetadataUrl);

    }

    private boolean triggerRegistrationIfNeeded() throws Exception{

        boolean flag=false;

        //registration example at org.xdi.oxauth.ws.rs.RegistrationRestWebServiceHttpTest
        long now=new Date().getTime();
        if (clientExpiration<now) {  //registration must take place
            RegisterRequest request = new RegisterRequest(ApplicationType.NATIVE, "SCIM-Client", new ArrayList<String>());
            //request.setScopes(SCOPES);
            request.setResponseTypes(RESPONSE_TYPES);
            request.setRedirectUris(Arrays.asList(REDIRECT_URI));
            request.setAuthenticationMethod(AuthenticationMethod.CLIENT_SECRET_BASIC);
            request.setSubjectType(SubjectType.PAIRWISE);
            request.setGrantTypes(Arrays.asList(GrantType.CLIENT_CREDENTIALS));

            RegisterClient registerClient = new RegisterClient(registrationEndpoint);
            registerClient.setRequest(request);

            RegisterResponse response = registerClient.exec();
            clientId=response.getClientId();
            password=response.getClientSecret();
            clientExpiration=response.getClientSecretExpiresAt().getTime();

            flag=true;
        }
        return flag;    //returns if registration was triggered

    }

    private void updateTokens(GrantType grant) {

        access_token=null;
        /*
        Ideally validation of access_token should take place here, however, as no id_token nor refresh_token is issued when
        using Grant type = client credentials (the only applicable for this client - see OAuth2 spec), not much is done here
         */
        //String id_token=response.getIdToken();      //this is null
        //refresh_token = response.getRefreshToken(); //this is null
        access_token=getTokens(grant).getAccessToken();
        System.out.println("tokens: " + access_token);

    }

    private TokenResponse getTokens(GrantType grant){

        TokenRequest tokenRequest = new TokenRequest(grant);
        //tokenRequest.setScope("openid profile");
        tokenRequest.setAuthUsername(clientId);

        switch (grant){
            case CLIENT_CREDENTIALS:
                tokenRequest.setAuthPassword(password);
                //tokenRequest.setCode(authz_code);
                break;
            case REFRESH_TOKEN:
                tokenRequest.setRefreshToken(refresh_token);
                //how about refreshing this way: tokenClient.execRefreshToken() ?
                break;
        }
        tokenRequest.setAuthenticationMethod(AuthenticationMethod.CLIENT_SECRET_BASIC);

        TokenClient tokenClient = new TokenClient(tokenEndpoint);
        tokenClient.setRequest(tokenRequest);
        return tokenClient.exec();

    }

    @Override
    protected void prepareRequest(){
    }

    @Override
    protected String getAuthenticationHeader(){
        return "Bearer " + access_token;
    }

    @Override
    protected boolean authorize(BaseClientResponse response){
        /*
        This method is called if the attempt to use the service returned forbidden (status = 403), so here we check if
        client expired to generate a new one & ask for another token, or else leave it that way (forbidden)
         */
        try {
            triggerRegistrationIfNeeded();
            updateTokens(GrantType.CLIENT_CREDENTIALS);
            //If a new token was asked, an additional call to the service will be made (see method isNeededToAuthorize)
            return (access_token!=null);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;   //do not make an additional attempt, e.g. getAuthenticationHeader is not called once more
        }
    }

}