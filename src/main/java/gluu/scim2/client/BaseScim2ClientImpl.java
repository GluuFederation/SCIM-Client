/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.scim.client.ScimResponse;
import gluu.scim.client.model.ScimBulkOperation;
import gluu.scim.client.model.ScimGroup;
import gluu.scim.client.model.ScimPerson;
import gluu.scim.client.model.ScimPersonSearch;
import gluu.scim.client.util.Converter;
import gluu.scim.client.util.ResponseMapper;
import gluu.scim.client.util.Util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.gluu.oxtrust.model.scim2.BulkRequest;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.Group;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SCIM default client
 * 
 * @author Yuriy Movchan Date: 08/23/2013
 */
public abstract class BaseScim2ClientImpl implements BaseScim2Client {

	private static final long serialVersionUID = 9098930517944520482L;

	private static final Logger log = LoggerFactory.getLogger(BaseScim2ClientImpl.class);

	private String domain;

	public BaseScim2ClientImpl(String domain) {
		this.domain = domain;
	}

	protected String getHost(String uri) throws MalformedURLException {
		URL url = new URL(uri);

		return url.getHost();
	}

	protected long computeAccessTokenExpirationTime(Integer expiresIn) {
		// Compute "accessToken" expiration timestamp
		Calendar calendar = Calendar.getInstance();
		if (expiresIn != null) {
			calendar.add(Calendar.SECOND, expiresIn);
			calendar.add(Calendar.SECOND, -10); // Subtract 10 seconds to avoid
												// expirations during executing
												// request
		}

		return calendar.getTimeInMillis();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#retrieveServiceProviderConfig(java
	 * .lang.String)
	 */
	@Override
	public ScimResponse retrieveServiceProviderConfig(String mediaType) throws IOException {
		init();
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v2/ServiceProviderConfig");
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);

		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_XML);

		}

		try {

			httpClient.executeMethod(get);
			ScimResponse response = ResponseMapper.map(get, null);
			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			get.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#retrieveServiceProviderConfig(java
	 * .lang.String)
	 */
	@Override
	public ScimResponse retrieveResourceTypes(String mediaType) throws IOException {
		init();
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v2/ResourceTypes");
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		addAuthenticationHeader(get);
		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_XML);
		}

		try {
			httpClient.executeMethod(get);
			ScimResponse response = ResponseMapper.map(get, null);
			return response;
		} catch (Exception ex) {
			log.error(" an Error occured : ", ex);

		} finally {
			get.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#retrievePerson(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ScimResponse retrievePerson(String id, String mediaType) throws IOException {
		init();
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v2/Users/" + id);
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);

		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_XML);

		}

		try {

			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			get.releaseConnection();

		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#createPerson(gluu.scim.client.model
	 * .ScimPerson, java.lang.String)
	 */
	@Override
	public ScimResponse createPerson(ScimPerson person, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Users/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(Util.getJSONString(person), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(Util.getXMLString(person, ScimPerson.class), "text/xml", "UTF-8"));

		}
		try {
			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;

		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#createPerson(gluu.scim2.client.model
	 * .User, java.lang.String)
	 */
	@Override
	public ScimResponse createPerson(User person, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Users/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			// post.setRequestEntity(new StringRequestEntity(Util.getJSONString(person), "application/json", "UTF-8"));
			post.setRequestEntity(new StringRequestEntity(gluu.scim2.client.util.Util.getJSONStringUser(person), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(Util.getXMLString(person, User.class), "text/xml", "UTF-8"));

		}
		try {
			System.out.println("request payload:" + post.getRequestEntity().toString());
			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;

		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#updatePerson(gluu.scim.client.model
	 * .ScimPerson, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse updatePerson(ScimPerson person, String id, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/Users/" + id);
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			put.setRequestEntity(new StringRequestEntity(Util.getJSONString(person), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			put.setRequestEntity(new StringRequestEntity(Util.getXMLString(person, ScimPerson.class), "text/xml", "UTF-8"));

		}
		try {
			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			put.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#updatePerson(gluu.scim.client.model
	 * .User, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse updatePerson(User person, String id, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/Users/" + id);
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			// put.setRequestEntity(new StringRequestEntity(Util.getJSONString(person), "application/json", "UTF-8"));
			put.setRequestEntity(new StringRequestEntity(gluu.scim2.client.util.Util.getJSONStringUser(person), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			put.setRequestEntity(new StringRequestEntity(Util.getXMLString(person, User.class), "text/xml", "UTF-8"));

		}
		try {
			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			put.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#deletePerson(java.lang.String)
	 */
	@Override
	public ScimResponse deletePerson(String id) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		DeleteMethod delete = new DeleteMethod(this.domain + "/scim/v2/Users/" + id);
		delete.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(delete);

		try {
			httpClient.executeMethod(delete);

			ScimResponse response = ResponseMapper.map(delete, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			delete.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#retrieveGroup(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ScimResponse retrieveGroup(String id, String mediaType) throws IOException {

		init();
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v2/Groups/" + id);
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);
		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);

		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_XML);

		}

		try {
			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			get.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#createGroup(gluu.scim.client.model
	 * .ScimGroup, java.lang.String)
	 */
	@Override
	public ScimResponse createGroup(ScimGroup group, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Groups/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(Util.getJSONString(group), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(Util.getXMLString(group, ScimGroup.class), "text/xml", "UTF-8"));

		}

		try {
			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#createGroup(gluu.scim.client.model
	 * .ScimGroup, java.lang.String)
	 */
	@Override
	public ScimResponse createGroup(Group group, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Groups/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(Util.getJSONString(group), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(Util.getXMLString(group, Group.class), "text/xml", "UTF-8"));

		}

		try {
			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#updateGroup(gluu.scim.client.model
	 * .ScimGroup, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse updateGroup(ScimGroup group, String id, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/Groups/" + id);
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			put.setRequestEntity(new StringRequestEntity(Util.getJSONString(group), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			put.setRequestEntity(new StringRequestEntity(Util.getXMLString(group, ScimGroup.class), "text/xml", "UTF-8"));

		}

		try {
			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			put.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#updateGroup(gluu.scim.client.model
	 * .Group, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse updateGroup(Group group, String id, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/Groups/" + id);
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			put.setRequestEntity(new StringRequestEntity(Util.getJSONString(group), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			put.setRequestEntity(new StringRequestEntity(Util.getXMLString(group, Group.class), "text/xml", "UTF-8"));

		}

		try {
			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			put.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#deleteGroup(java.lang.String)
	 */
	@Override
	public ScimResponse deleteGroup(String id) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		DeleteMethod delete = new DeleteMethod(this.domain + "/scim/v2/Groups/" + id);
		delete.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(delete);

		try {
			httpClient.executeMethod(delete);

			ScimResponse response = ResponseMapper.map(delete, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			delete.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#createPersonString(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ScimResponse createPersonString(String person, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Users/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(person, "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(person, "text/xml", "UTF-8"));

		}

		try {

			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#updatePersonString(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse updatePersonString(String person, String id, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/Users/" + id);
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			put.setRequestEntity(new StringRequestEntity(person, "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			put.setRequestEntity(new StringRequestEntity(person, "text/xml", "UTF-8"));

		}

		try {
			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, null);
			put.releaseConnection();

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			put.releaseConnection();

		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#createGroupString(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ScimResponse createGroupString(String group, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Groups/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(group, "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(group, "text/xml", "UTF-8"));

		}
		try {
			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#updateGroupString(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse updateGroupString(String group, String id, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/Groups/" + id);
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			put.setRequestEntity(new StringRequestEntity(group, "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			put.setRequestEntity(new StringRequestEntity(group, "text/xml", "UTF-8"));

		}

		try {
			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, null);
			put.releaseConnection();

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			put.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#bulkOperation(gluu.scim.client.model
	 * .ScimBulkOperation, java.lang.String)
	 */
	@Override
	public ScimResponse bulkOperation(ScimBulkOperation operation, String mediaType) throws IOException, JAXBException {
		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Bulk/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(Util.getJSONString(operation), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(Util.getXMLString(operation, ScimGroup.class), "text/xml", "UTF-8"));

		}
		try {
			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#bulkOperation(gluu.scim.client.model
	 * .BulkOperation, java.lang.String)
	 */
	@Override
	public ScimResponse bulkOperation(BulkRequest bulkRequest, String mediaType) throws IOException, JAXBException {
		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Bulk/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(Util.getJSONString(bulkRequest), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(Util.getXMLString(bulkRequest, BulkRequest.class), "text/xml", "UTF-8"));

		}
		try {
			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#bulkOperationString(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ScimResponse bulkOperationString(String operation, String mediaType) throws IOException {
		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Bulk/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(operation, "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(operation, "text/xml", "UTF-8"));

		}

		try {
			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#retrieveAllPersons(java.lang.String)
	 */
	@Override
	public ScimResponse retrieveAllPersons(String mediaType) throws IOException {
		init();
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v2/Users/");
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);

		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_XML);

		}

		try {
			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			get.releaseConnection();

		}
		return null;
	}

	/**
	 * User search via a filter with pagination and sorting
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
	@Override
	public ScimResponse searchUsers(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v2/Users/");

		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		get.setQueryString(new NameValuePair[] {
			new NameValuePair("filter", filter),
			new NameValuePair("startIndex", String.valueOf(startIndex)),
			new NameValuePair("count", String.valueOf(count)),
			new NameValuePair("sortBy", sortBy),
			new NameValuePair("sortOrder", sortOrder),
			new NameValuePair("attributes", ((attributesArray != null) ? new ObjectMapper().writeValueAsString(attributesArray) : null))
		});

		addAuthenticationHeader(get);

		// SCIM 2.0 uses JSON only
		get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);

		try {

			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, null);

			return response;

		} catch (Exception ex) {

			log.error(" An error occured : ", ex);

		} finally {
			get.releaseConnection();
		}

		return null;
	}

	/*
     * (non-Javadoc)
     * @see
     * gluu.scim.client.ScimClientService#retrieveAllGroups(java.lang.String)
     */
	@Override
	public ScimResponse retrieveAllGroups(String mediaType) throws IOException {
		init();
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v2/Groups/");
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);

		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_XML);

		}

		try {
			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, null);

			return response;
		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			get.releaseConnection();

		}
		return null;
	}

	/**
	 * Group search via a filter with pagination and sorting
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
	@Override
	public ScimResponse searchGroups(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v2/Groups/");

		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		get.setQueryString(new NameValuePair[] {
			new NameValuePair("filter", filter),
			new NameValuePair("startIndex", String.valueOf(startIndex)),
			new NameValuePair("count", String.valueOf(count)),
			new NameValuePair("sortBy", sortBy),
			new NameValuePair("sortOrder", sortOrder),
			new NameValuePair("attributes", ((attributesArray != null) ? new ObjectMapper().writeValueAsString(attributesArray) : null))
		});

		addAuthenticationHeader(get);

		// SCIM 2.0 uses JSON only
		get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);

		try {

			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, null);

			return response;

		} catch (Exception ex) {

			log.error(" An error occured : ", ex);

		} finally {
			get.releaseConnection();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#personSearch(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse personSearch(String attribute, String value, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Users/Search");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		ScimPersonSearch searchPattern = new ScimPersonSearch();
		searchPattern.setAttribute(attribute);
		searchPattern.setValue(value);

		addAuthenticationHeader(post);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(Util.getJSONString(searchPattern), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(Util.getXMLString(searchPattern, ScimPersonSearch.class), "text/xml", "UTF-8"));

		}
		try {
			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;

		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#personSearchByObject(java.lang.String,
	 * java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse personSearchByObject(String attribute, Object value, String valueMediaType, String outPutMediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Users/Search");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		ScimPersonSearch searchPattern = new ScimPersonSearch();
		searchPattern.setAttribute(attribute);
		String stringValue = new String();
		if (valueMediaType.equals(MediaType.APPLICATION_JSON)) {
			stringValue = Util.getJSONString(value);
		}

		if (valueMediaType.equals(MediaType.APPLICATION_XML)) {
			stringValue = Util.getXMLString(value, value.getClass());
		}

		searchPattern.setValue(stringValue);

		addAuthenticationHeader(post);

		if (outPutMediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(Util.getJSONString(searchPattern), "application/json", "UTF-8"));
		}

		if (outPutMediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(Util.getXMLString(searchPattern, ScimPersonSearch.class), "text/xml", "UTF-8"));

		}
		try {
			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;

		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#personSearch(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse searchPersons(String attribute, String value, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Users/SearchPersons");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		ScimPersonSearch searchPattern = new ScimPersonSearch();
		searchPattern.setAttribute(attribute);
		searchPattern.setValue(value);

		addAuthenticationHeader(post);

		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(Util.getJSONString(searchPattern), "application/json", "UTF-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(Util.getXMLString(searchPattern, ScimPersonSearch.class), "text/xml", "UTF-8"));

		}
		try {
			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, null);

			return response;

		} catch (Exception ex) {

			log.error(" an Error occured : ", ex);

		} finally {
			post.releaseConnection();

		}
		return null;
	}

	@Override
	public UserExtensionSchema getUserExtensionSchema() throws Exception {

		GetMethod get = new GetMethod(this.domain + "/scim/v2/Schemas/" + Constants.USER_EXT_SCHEMA_ID);
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);

		HttpClient httpClient = new HttpClient();
		httpClient.executeMethod(get);

		ScimResponse response = ResponseMapper.map(get, null);

		byte[] bytes = response.getResponseBody();
		String json = new String(bytes);

		UserExtensionSchema userExtensionSchema = (UserExtensionSchema) gluu.scim2.client.util.Util.jsonToObject(json, UserExtensionSchema.class);

		return userExtensionSchema;
	}

	protected abstract void init();

	protected abstract void addAuthenticationHeader(HttpMethodBase httpMethod);

}
