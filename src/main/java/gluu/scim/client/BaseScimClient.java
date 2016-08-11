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

import javax.xml.bind.JAXBException;

/**
 * BaseClient
 *
 * @author Reda Zerrad Date: 05.28.2012
 */
public interface BaseScimClient extends Serializable {
	
	/**
	 * Retrieves a person by his uid
	 *
	 * @param uid
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
     */
	ScimResponse retrievePerson(String uid, String mediaType) throws IOException;

	/**
	 * Creates a person with ScimPerson as input
	 *
	 * @param person
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 * @throws JAXBException
     */
	ScimResponse createPerson(ScimPerson person, String mediaType) throws IOException, JAXBException;

	/**
	 * Updates a person with ScimPerson as input
	 *
	 * @param person
	 * @param uid
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
     * @throws JAXBException
     */
	ScimResponse updatePerson(ScimPerson person, String uid, String mediaType) throws IOException, JAXBException;

	/**
	 * Deletes a person by his uid
	 *
	 * @param uid
	 * @return ScimResponse
	 * @throws IOException
     */
	ScimResponse deletePerson(String uid) throws IOException;

	/**
	 * Retrieves a group by his id
	 *
	 * @param id
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
     */
	ScimResponse retrieveGroup(String id, String mediaType) throws IOException;

	/**
	 * Creates a Group with ScimGroup as input
	 *
	 * @param group
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 * @throws JAXBException
     */
	ScimResponse createGroup(ScimGroup group, String mediaType) throws IOException, JAXBException;

	/**
	 * Updates a Group with ScimGroup as input
	 *
	 * @param group
	 * @param id
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
     * @throws JAXBException
     */
	ScimResponse updateGroup(ScimGroup group, String id, String mediaType) throws IOException, JAXBException;

	/**
	 * Deletes a group by his id
	 *
	 * @param id
	 * @return ScimResponse
	 * @throws IOException
     */
	ScimResponse deleteGroup(String id) throws IOException;

	/**
	 * Creates a person with a String as input
	 *
	 * @param person
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 * @throws JAXBException
     */
    ScimResponse createPersonString(String person, String mediaType) throws IOException, JAXBException;

	/**
	 * Updates a person with a String as input
	 *
	 * @param person
	 * @param uid
	 * @param mediaType
	 * @return
	 * @throws IOException
     * @throws JAXBException
     */
	ScimResponse updatePersonString(String person, String uid, String mediaType) throws IOException, JAXBException;

	/**
	 * Creates a group with a String as input
	 *
	 * @param group
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 * @throws JAXBException
     */
	ScimResponse createGroupString(String group, String mediaType) throws IOException, JAXBException;

	/**
	 * Updates a group with a String as input
	 *
	 * @param group
	 * @param id
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
     * @throws JAXBException
     */
	ScimResponse updateGroupString(String group, String id, String mediaType) throws IOException, JAXBException;

	/**
	 * Bulk operation with ScimBulkOperation as input
	 *
	 * @param operation
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
	 * @throws JAXBException
     */
	ScimResponse bulkOperation(ScimBulkOperation operation, String mediaType) throws IOException, JAXBException;
	
	/**
	 * Bulk operation with String as input
	 *
	 * @param operation
	 * @param mediaType
	 * @return ScimResponse
	 * @throws IOException
     */
	ScimResponse bulkOperationString(String operation,String mediaType) throws IOException;

	/**
	 * Person search via a filter with pagination and sorting
	 *
	 * @param filter
	 * @param startIndex
	 * @param count
	 * @param sortBy
	 * @param sortOrder
	 * @param attributesArray
	 * @return
	 * @throws IOException
	 */
	ScimResponse searchPersons(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

	/**
	 * Group search via a filter with pagination and sorting
	 *
	 * @param filter
	 * @param startIndex
	 * @param count
	 * @param sortBy
	 * @param sortOrder
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
	 */
	ScimResponse searchGroups(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException;

	/**
	 * Retrieves all persons
	 *
	 * @return ScimResponse
	 * @throws IOException
     */
	ScimResponse retrieveAllPersons() throws IOException;

	/**
	 * Retrieves all groups
	 *
	 * @return ScimResponse
	 * @throws IOException
     */
	ScimResponse retrieveAllGroups() throws IOException;

	/*
	ScimResponse personSearch(String attribute, String value, String mediaType) throws IOException, JAXBException;

	ScimResponse personSearchByObject(String attribute, Object value, String valueMediaType, String outPutMediaType) throws IOException, JAXBException;

	ScimResponse searchPersons(String attribute, String value, String mediaType) throws IOException, JAXBException;
	*/
}
