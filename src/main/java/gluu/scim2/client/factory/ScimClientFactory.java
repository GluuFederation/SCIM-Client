package gluu.scim2.client.factory;

import gluu.scim2.client.TestModeScimClient;
import gluu.scim2.client.UmaScimClient;
import gluu.scim2.client.DummyClient;
import org.xdi.oxauth.model.util.SecurityProviderUtility;
//import org.xdi.oxauth.model.util.SecurityProviderUtility;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by eugeniuparvan on 2/20/17.
 * Updated by jgomer on 2017-07-13
 */
public class ScimClientFactory<T> {

    private Class<T> serviceInterface;
    private Class<?> interfaces[];

    public ScimClientFactory(Class <T> interfaceClass){
        SecurityProviderUtility.installBCProvider();
        serviceInterface=interfaceClass;
        interfaces=new Class<?>[]{serviceInterface};
    }

    public T getClient(String domain, String umaAatClientId, String umaAatClientJksPath, String umaAatClientJksPassword, String umaAatClientKeyId) {
        InvocationHandler handler = new UmaScimClient(domain, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
        return serviceInterface.cast(Proxy.newProxyInstance(serviceInterface.getClassLoader(), interfaces, handler));
    }

    /**
     *
     * @param domain protocol + host of resource server
     * @param OIDCMetadataUrl URL of authorization servers' metadata document
     * @return An object for using SCIM service in test mode only (not UMA protected)
     * @throws Exception Instantiation abnormality
     * @since 3.1.0
     */
    public T getTestClient(String domain, String OIDCMetadataUrl) throws Exception {
        InvocationHandler handler = new TestModeScimClient(domain, OIDCMetadataUrl);
        return serviceInterface.cast(Proxy.newProxyInstance(serviceInterface.getClassLoader(), interfaces, handler));
    }

    public T getDummyClient(String domain) throws Exception{
        InvocationHandler handler = new DummyClient(domain);
        return serviceInterface.cast(Proxy.newProxyInstance(serviceInterface.getClassLoader(), interfaces, handler));
    }

}
