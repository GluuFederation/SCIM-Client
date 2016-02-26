package gluu.scim.client.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.gluu.oxtrust.model.scim2.User;

/**
 * SCIM Utils 
 *
 * @author Reda Zerrad Date: 05.29.2012
 */

public class Util implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4493708632437502015L;

	public static String getJSONString(Object person) throws  JsonGenerationException, JsonMappingException, IOException {
	 	StringWriter sw = new StringWriter();
	 	ObjectMapper mapper = new ObjectMapper();
	 	mapper.writeValue(sw, person);
	     return sw.toString();
	 }

	/**
	 * For an SCIM 2.0 User class with extensions.
	 *
	 * @param person
	 * @return
	 * @throws IOException
     */
	public static String getJSONStringUser(User person) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1, 0, 0, ""));
		simpleModule.addSerializer(User.class, new UserSerializer());
		mapper.registerModule(simpleModule);
		String value = mapper.writeValueAsString(person);
		return value;
	}

	 public static String getXMLString(Object person,Class<?> clazz) throws JAXBException {
		 StringWriter sw = new StringWriter();
		 JAXBContext context = JAXBContext.newInstance(clazz);
	     context.createMarshaller().marshal(person, sw);
	     String output =sw.toString();
	     System.out.println(output);
	     return output;
	 }
	 
	 
}
