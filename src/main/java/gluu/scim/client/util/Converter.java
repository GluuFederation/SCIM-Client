/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client.util;

import gluu.scim.client.model.ScimBulkOperation;
import gluu.scim.client.model.ScimBulkResponse;
import gluu.scim.client.model.ScimGroup;
import gluu.scim.client.model.ScimPerson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * SCIM format converter
 *
 * @author Reda Zerrad Date: 06.01.2012
 */
public class Converter implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2673025645297827236L;

	/**
	   * converts an XML format  person to JSON
	   * @param String xml
	   * @return String response
	   * @throws Exception
	   */
	public static String xmlToJsonPerson(String xml) throws Exception{
		
		ScimPerson person = (ScimPerson) xmlToObject(xml,ScimPerson.class);
		String response = getJSONString(person);
		return response;
		
		
	}
	
	
	/**
	   * converts a JSON format person to XML
	   * @param String json
	   * @return String response
	   * @throws Exception
	   */
	public static String jsonToXMLPerson(String json) throws Exception{
		
		ScimPerson person = (ScimPerson) jsonToObject(json,ScimPerson.class);
		String response = getXMLString(person,ScimPerson.class);
		return response;
		
		
	}
	
	
	/**
	   * converts an XML format group to JSON
	   * @param String xml
	   * @return String response
	   * @throws Exception
	   */
	public static String xmlToJsonGroup(String xml) throws Exception{
		
		ScimGroup group = (ScimGroup) xmlToObject(xml,ScimGroup.class);
		String response = getJSONString(group);
		return response;
		
		
	}
	
	/**
	   * converts a JSON format group to XML
	   * @param String json
	   * @return String response
	   * @throws Exception
	   */
	public static String jsonToXMLGroup(String json) throws Exception{
		
		ScimGroup group = (ScimGroup) jsonToObject(json,ScimGroup.class);
		String response = getXMLString(group,ScimGroup.class);
		return response;
		
		
	}
	
	/**
	   * converts an XML format ScimBulkOperation to JSON
	   * @param String xml
	   * @return String response
	   * @throws Exception
	   */
	public static String xmlToJsonBulkOperation(String xml) throws Exception{
		
		ScimBulkOperation operation = (ScimBulkOperation) xmlToObject(xml,ScimBulkOperation.class);
		String response = getJSONString(operation);
		return response;
		
		
	}
	
	/**
	   * converts a JSON format ScimBulkOperation to XML
	   * @param String json
	   * @return String response
	   * @throws Exception
	   */
	public static String jsonToXMLBulkOperation(String json) throws Exception{
		
		ScimBulkOperation operation = (ScimBulkOperation) jsonToObject(json,ScimBulkOperation.class);
		String response = getXMLString(operation,ScimBulkOperation.class);
		return response;
		
		
	}
	
	
	/**
	   * converts an XML format ScimBulkResponse to JSON
	   * @param String xml
	   * @return String response
	   * @throws Exception
	   */
	public static String xmlToJsonScimBulkResponse(String xml) throws Exception{
		
		ScimBulkResponse operation = (ScimBulkResponse) xmlToObject(xml,ScimBulkResponse.class);
		String response = getJSONString(operation);
		return response;
		
		
	}
	
	
	/**
	   * converts a JSON format ScimBulkResponse to XML
	   * @param String json
	   * @return String response
	   * @throws Exception
	   */
	public static String jsonToXMLScimBulkResponse(String json) throws Exception{
		
		ScimBulkResponse operation = (ScimBulkResponse) jsonToObject(json,ScimBulkResponse.class);
		String response = getXMLString(operation,ScimBulkResponse.class);
		return response;
		
		
	}
	
	/**
	   * converts an XML format  person to ScimPerson
	   * @param String xml
	   * @return ScimPerson
	   * @throws Exception
	   */
	public static ScimPerson xmlToScimPerson(String xml) throws Exception{
		
		ScimPerson person = (ScimPerson) xmlToObject(xml,ScimPerson.class);
		return person;
		
		
	}
	
	/**
	   * converts a JSON format person to ScimPerson
	   * @param String json
	   * @return ScimPerson
	   * @throws Exception
	   */
	public static ScimPerson jsonToScimPerson(String json) throws Exception{
		
		ScimPerson person = (ScimPerson) jsonToObject(json,ScimPerson.class);
		return person;
		
		
	}
	
	/**
	   * converts an XML format group to ScimGroup
	   * @param String xml
	   * @return ScimGroup
	   * @throws Exception
	   */
	public static ScimGroup xmlToScimGroup(String xml) throws Exception{
		
		ScimGroup group = (ScimGroup) xmlToObject(xml,ScimGroup.class);
		return group;
		
		
	}
	
	
	/**
	   * converts a JSON format group to XML
	   * @param String json
	   * @return ScimGroup
	   * @throws Exception
	   */
	public static ScimGroup jsonToScimGroup(String json) throws Exception{
		
		ScimGroup group = (ScimGroup) jsonToObject(json,ScimGroup.class);
		return group;
		
		
	}
	
	/**
	   * converts an XML format to ScimBulkResponse
	   * @param String xml
	   * @return ScimBulkResponse response
	   * @throws Exception
	   */
	public static ScimBulkResponse xmlToScimBulkResponse(String xml) throws Exception{
		
		ScimBulkResponse operation = (ScimBulkResponse) xmlToObject(xml,ScimBulkResponse.class);
		
		return operation;
		
		
	}
	
	
	/**
	   * converts a JSON format to ScimBulkResponse 
	   * @param String json
	   * @return ScimBulkResponse response
	   * @throws Exception
	   */
	public static ScimBulkResponse jsonToScimBulkResponse(String json) throws Exception{
		
		ScimBulkResponse operation = (ScimBulkResponse) jsonToObject(json,ScimBulkResponse.class);
		return operation;
		
		
	}
	
	 private static Object xmlToObject(String xml, Class<?> clazz) throws Exception {
	    	
		 ByteArrayInputStream input = new ByteArrayInputStream (xml.getBytes()); 
		     	
		     	JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		     	 
		          Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		         
		 		Object clazzObject = jaxbUnmarshaller.unmarshal(input);
		 		return clazzObject;
		         
		     }
	 
	 private static String getJSONString(Object entity) throws  JsonGenerationException, JsonMappingException, IOException {
		 	StringWriter sw = new StringWriter();
		 	ObjectMapper mapper = new ObjectMapper();
		 	mapper.writeValue(sw, entity);
		     return sw.toString();
		 }
	 
	 private static Object jsonToObject(String json, Class<?> clazz) throws Exception {
	    	
	    	ObjectMapper mapper = new ObjectMapper();
	    	Object clazzObject = mapper.readValue(json, clazz);
	    	return clazzObject;
	    }
	 
	 private static String getXMLString(Object entity,Class<?> clazz) throws JAXBException {
		 StringWriter sw = new StringWriter();
		 JAXBContext context = JAXBContext.newInstance(clazz);
	     context.createMarshaller().marshal(entity, sw);
	     return sw.toString();
	 }
}
