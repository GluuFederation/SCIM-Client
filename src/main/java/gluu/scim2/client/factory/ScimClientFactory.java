package gluu.scim2.client.factory;

import gluu.scim2.client.RPInfo;
import gluu.scim2.client.ScimClient;
import gluu.scim2.client.TestModeScimClient;
import gluu.scim2.client.UmaScimClient;
import org.xdi.oxauth.model.util.SecurityProviderUtility;

/**
 * Created by eugeniuparvan on 2/20/17.
 * Updated by jgomer2001 on 2017-07-13
 */
public class ScimClientFactory {

    public static ScimClient getClient(String domain, String umaMetaDataUrl, String umaAatClientId, String umaAatClientJksPath, String umaAatClientJksPassword, String umaAatClientKeyId) {
        SecurityProviderUtility.installBCProvider();
        RPInfo info=new RPInfo(umaAatClientId, umaAatClientKeyId, umaAatClientJksPath, umaAatClientJksPassword);
        return new UmaScimClient(domain, umaMetaDataUrl, info);
    }

    /**
     *
     * @param domain See TestModeScimClient constructor
     * @param OIDCMetadataUrl
     * @return An object for using SCIM service in test mode only (not UMA protected)
     * @throws Exception Instantiation abnormality
     * @since 3.1.0
     */
    public static ScimClient getTestClient(String domain, String OIDCMetadataUrl) throws Exception {
        SecurityProviderUtility.installBCProvider();
        return new TestModeScimClient(domain, OIDCMetadataUrl);
    }

}
