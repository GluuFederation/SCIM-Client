package gluu.scim2.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgomer on 2017-08-06.
 * Encapsulates information of requesting party
 */
public class RPInfo {

    private String clientId;
    private String clientKeyId;
    private String clientJksPath;
    private String clientJksPassword;
    private List<String> registeredScopes;

    public RPInfo(String clientId, String clientKeyId, String clientJksPath, String clientJksPassword){

        this.clientId=clientId;
        this.clientKeyId=clientKeyId;
        this.clientJksPath=clientJksPath;
        this.clientJksPassword=clientJksPassword;

//        if (Util.allNotBlank(clientId, clientJksPath, clientJksPassword))
//            throw new ScimInitializationException("RP client Id, JKS keystore path or password is empty");
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientKeyId() {
        return clientKeyId;
    }

    public String getClientJksPath() {
        return clientJksPath;
    }

    public String getClientJksPassword() {
        return clientJksPassword;
    }

    public List<String> getRegisteredScopes() {
        return registeredScopes;
    }

    public void setRegisteredScopes(List<String> registeredScopes) {
        this.registeredScopes = registeredScopes;
    }

    /**
     * Returns a space-separated string from the list of scopes of this RP client
     * @return An empty string if there are no specified scopes
     */
    public String scopesAsString(){
        String scopes="";

        if (registeredScopes==null)
            registeredScopes=new ArrayList<String>();
        scopes=registeredScopes.toString().replaceAll(",\\s*", " ");
        return scopes.replaceFirst("\\[", "").replaceFirst("]", "");
    }

}
