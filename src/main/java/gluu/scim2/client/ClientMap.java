package gluu.scim2.client;

import javax.ws.rs.client.Client;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jgomer on 2017-11-25.
 */
public class ClientMap {

    private static ClientMap map=new ClientMap();

    private static Map<Client, String> mappings=new HashMap<>();

    public static void update(Client client, String value){
        mappings.put(client, value);
    }

    public static void remove(Client client){
        //Frees the resources associated to this RestEasy client
        client.close();
        mappings.remove(client);
    }

    public static String getValue(Client client){
        return mappings.get(client);
    }

    public static void clean(){
        for (Client client : mappings.keySet())
            remove(client);
    }

}
