/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.scim.client.ScimResponse;
import gluu.scim.client.util.ResponseMapper;
import gluu.scim.client.util.Util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

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
import org.apache.commons.lang.StringUtils;
import org.gluu.oxtrust.model.scim2.*;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;
import org.gluu.oxtrust.model.scim2.schema.extension.UserExtensionSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gluu.oxtrust.model.scim2.Constants.MAX_COUNT;

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

	protected abstract void init();

	protected abstract void addAuthenticationHeader(HttpMethodBase httpMethod);

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
	public ScimResponse retrieveServiceProviderConfig() throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		GetMethod get = new GetMethod(this.domain + "/scim/v2/ServiceProviderConfig");
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);
		get.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		/*
		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_XML);
		}
		*/

		try {

			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
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
	public ScimResponse retrieveResourceTypes() throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		GetMethod get = new GetMethod(this.domain + "/scim/v2/ResourceTypes");
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);
		get.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		/*
		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			get.setRequestHeader("Accept", MediaType.APPLICATION_XML);
		}
		*/

		try {

			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
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
	@Deprecated
	public ScimResponse retrievePerson(String id, String mediaType) throws IOException {
		return retrieveUser(id, new String[]{});
	}

	@Override
	public ScimResponse retrieveUser(String id, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		GetMethod get = new GetMethod(this.domain + "/scim/v2/Users/" + id);
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);
		get.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		get.setQueryString(new NameValuePair[] {
			new NameValuePair("attributes", ((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null))
		});

		try {

			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			get.releaseConnection();
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
	@Deprecated
	public ScimResponse createPerson(User user, String mediaType) throws IOException, JAXBException {
		return createUser(user, new String[]{});
	}

	@Override
	public ScimResponse createUser(User user, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Users");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);
		post.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		post.setQueryString(new NameValuePair[] {
			new NameValuePair("attributes", ((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null))
		});

		// post.setRequestEntity(new StringRequestEntity(Util.getJSONString(person), "application/json", "utf-8"));
		post.setRequestEntity(new StringRequestEntity(gluu.scim2.client.util.Util.getJSONStringUser(user), Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		try {

			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			post.releaseConnection();
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
	@Deprecated
	public ScimResponse updatePerson(User user, String id, String mediaType) throws IOException, JAXBException {
		return updateUser(user, id, new String[]{});
	}

	@Override
	public ScimResponse updateUser(User user, String id, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/Users/" + id);
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);
		put.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		put.setQueryString(new NameValuePair[] {
			new NameValuePair("attributes", ((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null))
		});

		// put.setRequestEntity(new StringRequestEntity(Util.getJSONString(person), "application/json", "utf-8"));
		put.setRequestEntity(new StringRequestEntity(gluu.scim2.client.util.Util.getJSONStringUser(user), Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		try {

			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
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
		delete.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		try {

			httpClient.executeMethod(delete);

			ScimResponse response = ResponseMapper.map(delete, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
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
	@Deprecated
	public ScimResponse retrieveGroup(String id, String mediaType) throws IOException {
		return retrieveGroup(id, new String[]{});
	}

	@Override
	public ScimResponse retrieveGroup(String id, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		GetMethod get = new GetMethod(this.domain + "/scim/v2/Groups/" + id);
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);
		get.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		get.setQueryString(new NameValuePair[] {
			new NameValuePair("attributes", ((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null))
		});

		try {

			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
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
	@Deprecated
	public ScimResponse createGroup(Group group, String mediaType) throws IOException, JAXBException {
		return createGroup(group, new String[]{});
	}

	@Override
	public ScimResponse createGroup(Group group, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Groups");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);
		post.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		post.setQueryString(new NameValuePair[] {
			new NameValuePair("attributes", ((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null))
		});

		post.setRequestEntity(new StringRequestEntity(Util.getJSONString(group), Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		try {

			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			post.releaseConnection();
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
	@Deprecated
	public ScimResponse updateGroup(Group group, String id, String mediaType) throws IOException, JAXBException {
		return updateGroup(group, id, new String[]{});
	}

	@Override
	public ScimResponse updateGroup(Group group, String id, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/Groups/" + id);
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);
		put.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		put.setQueryString(new NameValuePair[] {
			new NameValuePair("attributes", ((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null))
		});

		put.setRequestEntity(new StringRequestEntity(Util.getJSONString(group), Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		try {

			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
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
		delete.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		try {

			httpClient.executeMethod(delete);

			ScimResponse response = ResponseMapper.map(delete, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
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
	@Deprecated
	public ScimResponse createPersonString(String person, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Users/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);
		post.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		post.setRequestEntity(new StringRequestEntity(person, Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		/*
		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(person, "application/json", "utf-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(person, "text/xml", "utf-8"));
		}
		*/

		try {

			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
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
	@Deprecated
	public ScimResponse updatePersonString(String person, String id, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/Users/" + id);
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);
		put.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		put.setRequestEntity(new StringRequestEntity(person, Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		/*
		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			put.setRequestEntity(new StringRequestEntity(person, "application/json", "utf-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			put.setRequestEntity(new StringRequestEntity(person, "text/xml", "utf-8"));
		}
		*/

		try {

			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
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
	@Deprecated
	public ScimResponse createGroupString(String group, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Groups/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);
		post.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		post.setRequestEntity(new StringRequestEntity(group, Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		/*
		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(group, "application/json", "utf-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(group, "text/xml", "utf-8"));
		}
		*/

		try {

			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
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
	@Deprecated
	public ScimResponse updateGroupString(String group, String id, String mediaType) throws IOException, JAXBException {

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/Groups/" + id);
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);
		put.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		put.setRequestEntity(new StringRequestEntity(group, Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		/*
		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			put.setRequestEntity(new StringRequestEntity(group, "application/json", "utf-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			put.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			put.setRequestEntity(new StringRequestEntity(group, "text/xml", "utf-8"));
		}
		*/

		try {

			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			put.releaseConnection();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#processBulkOperation(gluu.scim.client.model.BulkRequest)
	 */
	@Override
	public ScimResponse processBulkOperation(BulkRequest bulkRequest) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Bulk/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);
		post.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		post.setRequestEntity(new StringRequestEntity(Util.getJSONString(bulkRequest), Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		/*
		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(Util.getJSONString(bulkRequest), "application/json", "utf-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(Util.getXMLString(bulkRequest, BulkRequest.class), "text/xml", "utf-8"));
		}
		*/

		try {

			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#processBulkOperationString(java.lang.String)
	 */
	@Override
	public ScimResponse processBulkOperationString(String bulkRequestString) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/Bulk/");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);
		post.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		post.setRequestEntity(new StringRequestEntity(bulkRequestString, Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		/*
		if (mediaType.equals(MediaType.APPLICATION_JSON)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_JSON);
			post.setRequestEntity(new StringRequestEntity(bulkRequestString, "application/json", "utf-8"));
		}

		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			post.setRequestHeader("Accept", MediaType.APPLICATION_XML);
			post.setRequestEntity(new StringRequestEntity(bulkRequestString, "text/xml", "utf-8"));
		}
		*/

		try {

			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gluu.scim.client.ScimClientService#retrieveAllUsers()
	 */
	@Override
	public ScimResponse retrieveAllUsers() throws IOException {

		return searchUsers("", 1, MAX_COUNT, "", "", new String[]{});
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
	 * @return ScimResponse
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
			new NameValuePair("attributes", ((attributesArray != null) ? StringUtils.join(attributesArray, ',') : null))
			// new NameValuePair("attributes", ((attributesArray != null) ? new ObjectMapper().writeValueAsString(attributesArray) : null))
		});

		addAuthenticationHeader(get);

		// SCIM 2.0 uses JSON only
		get.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		try {

			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			get.releaseConnection();
		}

		return null;
	}

    /**
     * POST User search on /.search via a filter with pagination and sorting
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
    @Override
    public ScimResponse searchUsersPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {

        init();

        HttpClient httpClient = new HttpClient();

        PostMethod post = new PostMethod(this.domain + "/scim/v2/Users/.search");
        post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

        addAuthenticationHeader(post);
        post.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setFilter(filter);
        searchRequest.setStartIndex(startIndex);
        searchRequest.setCount(count);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortOrder(sortOrder);
        searchRequest.setAttributesArray(((attributesArray != null) ? StringUtils.join(attributesArray, ',') : null));

        post.setRequestEntity(new StringRequestEntity(gluu.scim2.client.util.Util.getObjectMapper().writeValueAsString(searchRequest), Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

        try {

            httpClient.executeMethod(post);

            ScimResponse response = ResponseMapper.map(post, new ScimResponse());

            return response;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            post.releaseConnection();
        }

        return null;
    }

    /*
         * (non-Javadoc)
         * @see
         * gluu.scim.client.ScimClientService#retrieveAllGroups(java.lang.String)
         */
	@Override
	public ScimResponse retrieveAllGroups() throws IOException {

		return searchGroups("", 1, MAX_COUNT, "", "", new String[]{});
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
	 * @return ScimResponse
	 * @throws IOException
	 */
	@Override
	public ScimResponse searchGroups(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		GetMethod get = new GetMethod(this.domain + "/scim/v2/Groups/");

		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);

		// SCIM 2.0 uses JSON only
		get.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		get.setQueryString(new NameValuePair[] {
			new NameValuePair("filter", filter),
			new NameValuePair("startIndex", String.valueOf(startIndex)),
			new NameValuePair("count", String.valueOf(count)),
			new NameValuePair("sortBy", sortBy),
			new NameValuePair("sortOrder", sortOrder),
			new NameValuePair("attributes", ((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null))
			// new NameValuePair("attributes", ((attributesArray != null) ? new ObjectMapper().writeValueAsString(attributesArray) : null))
		});

		try {

			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			get.releaseConnection();
		}

		return null;
	}

    /**
     * POST Group search on /.search via a filter with pagination and sorting
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
    @Override
    public ScimResponse searchGroupsPost(String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {

        init();

        HttpClient httpClient = new HttpClient();

        PostMethod post = new PostMethod(this.domain + "/scim/v2/Groups/.search");
        post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

        addAuthenticationHeader(post);
        post.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setFilter(filter);
        searchRequest.setStartIndex(startIndex);
        searchRequest.setCount(count);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortOrder(sortOrder);
        searchRequest.setAttributesArray(((attributesArray != null) ? StringUtils.join(attributesArray, ',') : null));

        post.setRequestEntity(new StringRequestEntity(gluu.scim2.client.util.Util.getObjectMapper().writeValueAsString(searchRequest), Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

        try {

            httpClient.executeMethod(post);

            ScimResponse response = ResponseMapper.map(post, new ScimResponse());

            return response;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            post.releaseConnection();
        }

        return null;
    }

	@Override
	public UserExtensionSchema getUserExtensionSchema() throws Exception {

		GetMethod get = new GetMethod(this.domain + "/scim/v2/Schemas/" + Constants.USER_EXT_SCHEMA_ID);
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		get.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		HttpClient httpClient = new HttpClient();
		httpClient.executeMethod(get);

		ScimResponse response = ResponseMapper.map(get, new ScimResponse());

		UserExtensionSchema userExtensionSchema = (UserExtensionSchema) gluu.scim2.client.util.Util.jsonToObject(response, UserExtensionSchema.class);

		return userExtensionSchema;
	}

	/**
	 * FIDO devices search via a filter with pagination and sorting
	 *
	 * @param userId
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
	public ScimResponse searchFidoDevices(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		GetMethod get = new GetMethod(this.domain + "/scim/v2/FidoDevices/");

		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);

		// SCIM 2.0 uses JSON only
		get.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		get.setQueryString(new NameValuePair[] {
			new NameValuePair("userId", userId),
			new NameValuePair("filter", filter),
			new NameValuePair("startIndex", String.valueOf(startIndex)),
			new NameValuePair("count", String.valueOf(count)),
			new NameValuePair("sortBy", sortBy),
			new NameValuePair("sortOrder", sortOrder),
			new NameValuePair("attributes", ((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null))
		});

		try {

			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			get.releaseConnection();
		}

		return null;
	}

	/**
	 * POST FIDO devices search on /.search via a filter with pagination and sorting
	 *
	 * @param userId
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
	public ScimResponse searchFidoDevicesPost(String userId, String filter, int startIndex, int count, String sortBy, String sortOrder, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(this.domain + "/scim/v2/FidoDevices/.search");
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(post);
		post.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		post.setQueryString(new NameValuePair[] {
			new NameValuePair("userId", userId)
		});

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setFilter(filter);
		searchRequest.setStartIndex(startIndex);
		searchRequest.setCount(count);
		searchRequest.setSortBy(sortBy);
		searchRequest.setSortOrder(sortOrder);
		searchRequest.setAttributesArray(((attributesArray != null) ? StringUtils.join(attributesArray, ',') : null));

		post.setRequestEntity(new StringRequestEntity(gluu.scim2.client.util.Util.getObjectMapper().writeValueAsString(searchRequest), Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		try {

			httpClient.executeMethod(post);

			ScimResponse response = ResponseMapper.map(post, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return null;
	}

	/**
	 * Retrieves a FIDO device
	 *
	 * @param id
	 * @param userId
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
	 */
	@Override
	public ScimResponse retrieveFidoDevice(String id, String userId, String[] attributesArray) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		GetMethod get = new GetMethod(this.domain + "/scim/v2/FidoDevices/" + id);
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(get);
		get.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		get.setQueryString(new NameValuePair[] {
			new NameValuePair("userId", userId),
			new NameValuePair("attributes", ((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null))
		});

		try {

			httpClient.executeMethod(get);

			ScimResponse response = ResponseMapper.map(get, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			get.releaseConnection();
		}

		return null;
	}

	/**
	 * Updates a FIDO device
	 *
	 * @param fidoDevice
	 * @param attributesArray
	 * @return ScimResponse
	 * @throws IOException
	 */
	@Override
	public ScimResponse updateFidoDevice(FidoDevice fidoDevice, String[] attributesArray) throws IOException {

		if (fidoDevice.getId() == null || fidoDevice.getId().isEmpty()) {
			throw new IllegalArgumentException("FIDO device id is null or an empty string");
		}

		init();

		HttpClient httpClient = new HttpClient();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/FidoDevices/" + fidoDevice.getId());
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);
		put.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		put.setQueryString(new NameValuePair[] {
			new NameValuePair("attributes", ((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null))
		});

		put.setRequestEntity(new StringRequestEntity(Util.getJSONString(fidoDevice), Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		try {

			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			put.releaseConnection();
		}

		return null;
	}

	/**
	 * Deletes a FIDO device
	 *
	 * @param id
	 * @return ScimResponse
	 * @throws IOException
	 */
	@Override
	public ScimResponse deleteFidoDevice(String id) throws IOException {

		init();

		HttpClient httpClient = new HttpClient();

		DeleteMethod delete = new DeleteMethod(this.domain + "/scim/v2/FidoDevices/" + id);
		delete.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(delete);
		delete.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		try {

			httpClient.executeMethod(delete);

			ScimResponse response = ResponseMapper.map(delete, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			delete.releaseConnection();
		}

		return null;
	}
	
	
	@Override
	public ScimResponse patchUser(ScimPatchUser scimPatchUser, String id, String[] attributesArray) throws IOException {

		init();

		PutMethod put = new PutMethod(this.domain + "/scim/v2/Users/patch/" + id);
		put.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		addAuthenticationHeader(put);
		put.setRequestHeader("Accept", Constants.MEDIA_TYPE_SCIM_JSON);

		put.setQueryString(new NameValuePair[] {
			new NameValuePair("attributes", ((attributesArray != null && attributesArray.length > 0) ? StringUtils.join(attributesArray, ',') : null))
		});

		// put.setRequestEntity(new StringRequestEntity(Util.getJSONString(person), "application/json", "utf-8"));
		put.setRequestEntity(new StringRequestEntity(gluu.scim2.client.util.Util.getJSONStringUserPatch(scimPatchUser), Constants.MEDIA_TYPE_SCIM_JSON, "utf-8"));

		try {
			HttpClient httpClient = new HttpClient();
			httpClient.executeMethod(put);

			ScimResponse response = ResponseMapper.map(put, new ScimResponse());

			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			put.releaseConnection();
		}

		return null;
	}
}
