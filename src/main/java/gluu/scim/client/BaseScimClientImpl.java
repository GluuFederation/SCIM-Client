package gluu.scim.client;

import gluu.scim.client.model.ScimBulkOperation;
import gluu.scim.client.model.ScimGroup;
import gluu.scim.client.model.ScimPerson;
import gluu.scim.client.model.ScimPersonSearch;
import gluu.scim.client.util.ResponseMapper;
import gluu.scim.client.util.Util;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SCIM default client
 * 
 * @author Yuriy Movchan Date: 08/23/2013
 */
public abstract class BaseScimClientImpl implements BaseScimClient, Serializable {

	private static final long serialVersionUID = 9098930517944520482L;

	private static final Logger log = LoggerFactory.getLogger(BaseScimClientImpl.class);

	private String domain;
	
	public BaseScimClientImpl(String domain) {
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
			calendar.add(Calendar.SECOND, -10); // Subtract 10 seconds to avoid expirations during executing request
		}

		return calendar.getTimeInMillis();
	}

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#retrievePerson(java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse retrievePerson(String uid, String mediaType) throws HttpException, IOException {
		init();
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v1//Users/" + uid);
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#createPerson(gluu.scim.client.model.ScimPerson, java.lang.String)
	 */
	@Override
	public ScimResponse createPerson(ScimPerson person, String mediaType) throws JsonGenerationException, JsonMappingException,
			IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v1//Users/");
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#updatePerson(gluu.scim.client.model.ScimPerson, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse updatePerson(ScimPerson person, String uid, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v1//Users/" + uid);
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#deletePerson(java.lang.String)
	 */
	@Override
	public ScimResponse deletePerson(String uid) throws HttpException, IOException {

		init();

		HttpClient httpClient = new HttpClient();

		DeleteMethod delete = new DeleteMethod(this.domain + "/scim/v1//Users/" + uid);
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#retrieveGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse retrieveGroup(String id, String mediaType) throws HttpException, IOException {

		init();
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v1//Groups/" + id);
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#createGroup(gluu.scim.client.model.ScimGroup, java.lang.String)
	 */
	@Override
	public ScimResponse createGroup(ScimGroup group, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v1//Groups/");
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#updateGroup(gluu.scim.client.model.ScimGroup, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse updateGroup(ScimGroup group, String id, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v1//Groups/" + id);
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#deleteGroup(java.lang.String)
	 */
	@Override
	public ScimResponse deleteGroup(String id) throws HttpException, IOException {

		init();

		HttpClient httpClient = new HttpClient();

		DeleteMethod delete = new DeleteMethod(this.domain + "/scim/v1//Groups/" + id);
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#createPersonString(java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse createPersonString(String person, String mediaType) throws JsonGenerationException, JsonMappingException,
			IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v1//Users/");
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#updatePersonString(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse updatePersonString(String person, String uid, String mediaType) throws JsonGenerationException,
			JsonMappingException, UnsupportedEncodingException, IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v1//Users/" + uid);
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#createGroupString(java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse createGroupString(String group, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v1//Groups/");
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#updateGroupString(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse updateGroupString(String group, String id, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v1//Groups/" + id);
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#bulkOperation(gluu.scim.client.model.ScimBulkOperation, java.lang.String)
	 */
	@Override
	public ScimResponse bulkOperation(ScimBulkOperation operation, String mediaType) throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException, JAXBException {
		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v1//Bulk/");
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#bulkOperationString(java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse bulkOperationString(String operation, String mediaType) throws HttpException, IOException {
		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v1//Bulk/");
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#retrieveAllPersons(java.lang.String)
	 */
	@Override
	public ScimResponse retrieveAllPersons(String mediaType) throws HttpException, IOException {
		init();
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v1//Users/");
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#retrieveAllGroups(java.lang.String)
	 */
	@Override
	public ScimResponse retrieveAllGroups(String mediaType) throws HttpException, IOException {
		init();
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(this.domain + "/scim/v1//Groups/");
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#personSearch(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse personSearch(String attribute, String value, String mediaType) throws JsonGenerationException,
			JsonMappingException, IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v1//Users/Search");
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

	/* (non-Javadoc)
	 * @see gluu.scim.client.ScimClientService#personSearchByObject(java.lang.String, java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public ScimResponse personSearchByObject(String attribute, Object value, String valueMediaType, String outPutMediaType)
			throws JsonGenerationException, JsonMappingException, IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v1//Users/Search");
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

	protected abstract void init();

	protected abstract void addAuthenticationHeader(HttpMethodBase httpMethod);

}
