
import gluu.scim.client.ScimClient;
import gluu.scim.client.ScimResponse;

import java.util.Arrays;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.xdi.oxauth.client.RegisterClient;
import org.xdi.oxauth.client.RegisterRequest;
import org.xdi.oxauth.client.RegisterResponse;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.SubjectType;
import org.xdi.oxauth.model.crypto.signature.SignatureAlgorithm;
import org.xdi.oxauth.model.register.ApplicationType;
import org.xdi.oxauth.model.util.StringUtils;

public class TestScimClient {


   	static String userInum = "@!0035.934F.1A51.77B0!0001!402D.66D0!0000!B17F.1FC8";
	static String clientInum = "@!0035.934F.1A51.77B0!0001!402D.66D0!0008!5BAD.32E4";
	static String clientSecret = "9e9fef43-0d97-4383-863f-0e828ff0a408";

    public static void main(String[] args) {
    	getPerson();
//    	registerClient("My Application","uma client");
    }

    private static void getPerson() {
 
    	
//        final ScimClient scimClient = ScimClient.oAuthInstance("admin", "rahat", clientInum, clientSecret,
//                "http://localhost:8085/oxtrust-server/seam/resource/restv1", "http://localhost:8085/oxauth/seam/resource/restv1/oxauth/token");
    	final ScimClient scimClient = ScimClient.oAuthInstance("admin", "yourpwd", clientInum, clientSecret,
                "http://localhost:8085/oxtrust-server/seam/resource/restv1", "http://localhost:8085/oxauth/seam/resource/restv1/oxauth/token");
        try {
            ScimResponse response1 = scimClient.retrievePerson(userInum, MediaType.APPLICATION_JSON);
            System.out.println("response status:"+response1.getStatus());
            System.out.println(response1.getResponseBodyString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}

	public static void registerClient(String applicationName,String displayName){
    	// Parameters
    	String registrationEndpoint = "http://localhost:8085/oxauth/seam/resource/restv1/oxauth/register";
    	String redirectUris = "https://gluu.org"; // List of space-separated redirect URIs
    	String scopes = "openid user_name profile address email"; // List of space-separated scopes    	 
    	RegisterRequest registerRequest = new RegisterRequest(ApplicationType.WEB, applicationName,
    	                StringUtils.spaceSeparatedToList(redirectUris));
    	registerRequest.setContacts(Arrays.asList("rahat@gluu.org", "rahat@gmail.com"));
    	registerRequest.setLogoUri("http://www.gluu.org/wp-content/themes/gluursn/images/logo.png");
    	registerRequest.setTokenEndpointAuthMethod(AuthenticationMethod.CLIENT_SECRET_BASIC);
    	registerRequest.setPolicyUri("http://www.gluu.org/policy");
    	registerRequest.setJwksUri("http://www.gluu.org/jwks");
    	registerRequest.setSectorIdentifierUri("");
    	registerRequest.setSubjectType(SubjectType.PUBLIC);
    	registerRequest.setRequestObjectSigningAlg(SignatureAlgorithm.RS256);
    	registerRequest.setRequestUris(Arrays.asList("http://www.gluu.org/request"));
    	registerRequest.setClientName(displayName);
    	registerRequest.setClientUri("http://www.gluu.org/request");
    	registerRequest.setAuthenticationMethod(AuthenticationMethod.CLIENT_SECRET_JWT);    	 
    	// Call the service
    	RegisterClient registerClient = new RegisterClient(registrationEndpoint);
    	registerClient.setRequest(registerRequest);
    	RegisterResponse response = registerClient.exec();
    	 
    	// Successful response
    	int status = response.getStatus(); // 200 if succeed
    	String clientId = response.getClientId(); // The client's identifier INUM
    	String clientSecret = response.getClientSecret(); // The client's password
    	String registrationAccessToken = response.getRegistrationAccessToken();
    	String registrationClientUri = response.getRegistrationClientUri();
    	Date clientSecretExpiresAt = response.getClientSecretExpiresAt();
    	System.out.println("clientId:"+clientId);
    	System.out.println("clientSecret:"+clientSecret);
    	System.out.println("registrationAccessToken:"+registrationAccessToken);
    	System.out.println("status:"+status);
    	System.out.println("response:"+response.getEntity());
    	
    	// Error response
    	status = response.getStatus(); // HTTP error code
    	String description = response.getErrorDescription(); // 
    }
}