package gluu.scim2.client.factory;

import gluu.scim2.client.TestModeScimClient;
import gluu.scim2.client.UmaScimClient;
import gluu.scim2.client.DummyClient;
import gluu.scim2.client.rest.ClientSideService;
import org.xdi.oxauth.model.util.SecurityProviderUtility;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by eugeniuparvan on 2/20/17.
 * Updated by jgomer on 2017-07-13
 */
public class ScimClientFactory {

    private static Class<ClientSideService> defaultInterface;

    static {
        SecurityProviderUtility.installBCProvider();
        defaultInterface=ClientSideService.class;
    }

    public static <T> T getClient(Class <T> interfaceClass, String domain, String umaAatClientId, String umaAatClientJksPath, String umaAatClientJksPassword, String umaAatClientKeyId) {
        InvocationHandler handler = new UmaScimClient<>(interfaceClass, domain, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
        return typedProxy(interfaceClass, handler);
    }

    public static ClientSideService getClient(String domain, String umaAatClientId, String umaAatClientJksPath, String umaAatClientJksPassword, String umaAatClientKeyId) {
        return getClient(defaultInterface, domain, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    /**
     *
     * @param domain protocol + host of resource server
     * @param OIDCMetadataUrl URL of authorization servers' metadata document
     * @return An object for using SCIM service in test mode only (not UMA protected)
     * @throws Exception Instantiation abnormality
     * @since 3.1.0
     */
    public static <T> T getTestClient(Class <T> interfaceClass, String domain, String OIDCMetadataUrl) throws Exception {
        InvocationHandler handler = new TestModeScimClient<>(interfaceClass, domain, OIDCMetadataUrl);
        return typedProxy(interfaceClass, handler);
    }

    public static ClientSideService getTestClient(String domain, String OIDCMetadataUrl) throws Exception {
        return getTestClient(defaultInterface, domain, OIDCMetadataUrl);
    }

    public static ClientSideService getDummyClient(String domain) throws Exception{
        InvocationHandler handler = new DummyClient<>(defaultInterface, domain);
        return typedProxy(defaultInterface, handler);
    }

    private static <T> T typedProxy(Class <T> interfaceClass, InvocationHandler handler){
        return interfaceClass.cast(Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, handler));
    }

}
