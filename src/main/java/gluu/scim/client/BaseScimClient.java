/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client;

import gluu.scim.client.model.ScimBulkOperation;
import gluu.scim.client.model.ScimGroup;
import gluu.scim.client.model.ScimPerson;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

/**
 * BaseClient
 *
 * @author Reda Zerrad Date: 05.28.2012
 */
public interface BaseScimClient extends Serializable {
	
	 /**
     * Retrieves a person by his uid
     * @param String uid
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse retrievePerson(String uid,String mediaType) throws HttpException, IOException;

	/**
     * Creates a person with ScimPerson as input
     * @param ScimPerson person
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse createPerson(ScimPerson person,String mediaType) throws JsonGenerationException, JsonMappingException, IOException, JAXBException;
	/**
     * Updates a person with ScimPerson as input
     * @param ScimPerson person
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse updatePerson(ScimPerson person,String uid,String mediaType) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException, JAXBException;
	 /**
     * Deletes a person by his uid
     * @param String uid
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse deletePerson(String uid) throws HttpException, IOException;
	 /**
     * Retrieves a group by his id
     * @param String id
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse retrieveGroup(String id,String mediaType) throws HttpException, IOException;
	/**
     * Creates a Group with ScimGroup as input
     * @param ScimGroup group
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse createGroup(ScimGroup group,String mediaType) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException, JAXBException;
	/**
     * Updates a Group with ScimGroup as input
     * @param ScimGroup group
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse updateGroup(ScimGroup group,String id, String mediaType) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException, JAXBException;
	 /**
     * Deletes a group by his id
     * @param String ui
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse deleteGroup(String id) throws HttpException, IOException;
	/**
     * Creates a person with a String as input
     * @param ScimPerson person
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
    public ScimResponse createPersonString(String person,String mediaType) throws JsonGenerationException, JsonMappingException, IOException, JAXBException;
    /**
     * Updates a person with a String as input
     * @param ScimPerson person
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse updatePersonString(String person,String uid,String mediaType) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException, JAXBException;
	/**
     * Creates a group with a String as input
     * @param ScimGroup group
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse createGroupString(String group,String mediaType) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException, JAXBException;
	/**
     * Updates a group with a String as input
     * @param ScimGroup group
     * @param String mediaType
     * @return ScimResponse
     * @throws Exception
     */
	public ScimResponse updateGroupString(String group,String id, String mediaType) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException, JAXBException;
	/**
     * Bulk operation with ScimBulkOperation as input
     * @param ScimBulkOperation operation
     * @param String mediaType
     * @return ScimResponse
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * @throws JAXBException 
     * @throws Exception
     */
	public ScimResponse bulkOperation(ScimBulkOperation operation,String mediaType) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException, JAXBException;
	
	/**
     * Bulk operation with String as input
     * @param String operation
     * @param String mediaType
     * @return ScimResponse
	 * @throws UnsupportedEncodingException 
	 * @throws IOException 
	 * @throws HttpException 
     * @throws Exception
     */
	public ScimResponse bulkOperationString(String operation,String mediaType) throws UnsupportedEncodingException, HttpException, IOException;
	/**
     * Retrieves All persons
     * @param String mediaType
     * @return ScimResponse
	 * @throws IOException 
	 * @throws HttpException 
     * @throws Exception
     */
	public ScimResponse retrieveAllPersons(String mediaType) throws HttpException, IOException;
	/**
     * Retrieves All groups
     * @param String mediaType
     * @return ScimResponse
	 * @throws IOException 
	 * @throws HttpException 
     * @throws Exception
     */
	public ScimResponse retrieveAllGroups(String mediaType) throws HttpException, IOException;

	public ScimResponse personSearch(String attribute, String value, String mediaType) throws JsonGenerationException,
		JsonMappingException, IOException, JAXBException;

	public ScimResponse personSearchByObject(String attribute, Object value, String valueMediaType, String outPutMediaType)
			throws JsonGenerationException, JsonMappingException, IOException, JAXBException;

	public ScimResponse searchPersons(String attribute, String value, String mediaType) throws JsonGenerationException,
	JsonMappingException, IOException, JAXBException;

}
