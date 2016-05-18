/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.codehaus.jackson.map.ObjectMapper;

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

	public static String getJSONString(Object person) throws IOException {
		StringWriter sw = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(sw, person);
		return sw.toString();
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
