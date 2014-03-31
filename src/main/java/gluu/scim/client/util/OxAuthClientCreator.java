package gluu.scim.client.util;

import gluu.scim.client.model.CreationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.client.RegisterClient;
import org.xdi.oxauth.client.RegisterResponse;
import org.xdi.oxauth.model.register.ApplicationType;
import org.xdi.oxauth.model.util.StringUtils;

import java.io.Serializable;

/**
 * OxAuthClientCreator , dynamically creates a client
 *
 * @author Reda Zerrad Date: 06.04.2012
 */
public class OxAuthClientCreator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4502638508457728982L;
	private static final Logger log = LoggerFactory.getLogger(OxAuthClientCreator.class);

	private static final String scopes = "openid profile address email";
	
	public static CreationResult create(String applicationName,String registerUrl,String redirectUris  ){
		try{
		
		RegisterClient registerClient = new RegisterClient(registerUrl);
//		RegisterResponse response =  registerClient.execRegister(Operation.CLIENT_REGISTER, ApplicationType.WEB,
		RegisterResponse response =  registerClient.execRegister(ApplicationType.WEB,
                applicationName, StringUtils.spaceSeparatedToList(redirectUris));
		CreationResult result = ResponseMapper.map(response,null);
		return result;
		}catch(Exception ex){
			log.error(" Could not create an oxAuth client : " , ex);
		}
		
		return null;
	}
      
}
