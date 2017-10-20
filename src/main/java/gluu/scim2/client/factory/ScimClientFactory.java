package gluu.scim2.client.factory;

import gluu.scim2.client.TestModeScimClient;
import gluu.scim2.client.rest.ClientSideService;
import gluu.scim2.client.DummyClient;
//import org.xdi.oxauth.model.util.SecurityProviderUtility;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by eugeniuparvan on 2/20/17.
 * Updated by jgomer on 2017-07-13
 */
public class ScimClientFactory {

    //TODO: reduce to a single getClient method
    //TODO: installBCProvider()
    public static ClientSideService getClient(String domain, String umaMetaDataUrl, String umaAatClientId,
                                              String umaAatClientJksPath, String umaAatClientJksPassword, String umaAatClientKeyId) {
        //SecurityProviderUtility.installBCProvider();
        //return new UmaScimClient(domain, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
        return null;
    }

    /**
     *
     * @param domain See TestModeScimClient constructor
     * @param OIDCMetadataUrl
     * @return An object for using SCIM service in test mode only (not UMA protected)
     * @throws Exception Instantiation abnormality
     * @since 3.1.0
     */
    public static ClientSideService getTestClient(String domain, String OIDCMetadataUrl) throws Exception {
        //SecurityProviderUtility.installBCProvider();
        Class clazz=ClientSideService.class;
        InvocationHandler handler = new TestModeScimClient(domain, OIDCMetadataUrl);
        return (ClientSideService) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, handler);

    }

    public static ClientSideService getDummyClient(String domain) throws Exception{

        Class clazz=ClientSideService.class;
        InvocationHandler handler = new DummyClient(domain);
        return (ClientSideService) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, handler);

    }

}
