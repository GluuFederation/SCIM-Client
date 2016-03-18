/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client.util;

import gluu.scim.client.ScimResponse;
import gluu.scim.client.model.CreationResult;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.xdi.oxauth.client.RegisterResponse;

/**
 * SCIM CopyUtil 
 *
 * @author Reda Zerrad Date: 05.28.2012
 */
public class ResponseMapper implements Serializable {
	
  /**
	 * 
	 */
	private static final long serialVersionUID = 4525792263966335875L;
	
	/**
     * Mapps PostMethod to ScimResponse
     * @param PostMethod source
     * @param ScimResponse destination
     * @return ScimResponse
     * @throws Exception
     */
public static ScimResponse map(PostMethod source,ScimResponse destination) throws IOException{
	
	if(destination == null){
		 destination = new ScimResponse();
	}
	  if(source.getResponseBodyAsStream() != null){destination.setResponseBodyAsStream(source.getResponseBodyAsStream());}
	  if(source.getResponseBodyAsString() != null){destination.setResponseBodyString(source.getResponseBodyAsString());}
	  if(source.getResponseBody() != null){destination.setResponseBody(source.getResponseBody());}
	  if(source.getResponseHeaders() != null){destination.setResponseHeaders(source.getResponseHeaders());}
	  if(source.getStatusText() != null){destination.setStatus(source.getStatusText());}
	  destination.setStatusCode(source.getStatusLine().getStatusCode());
	 
	  return destination;
	  
  }
/**
 * Mapps GetMethod to ScimResponse
 * @param GetMethod source
 * @param ScimResponse destination
 * @return ScimResponse
 * @throws Exception
 */
  public static ScimResponse map(GetMethod source,ScimResponse destination) throws IOException{
	 
	  if(destination == null){
			 destination = new ScimResponse();
		}
	  if(source.getResponseBodyAsStream() != null){destination.setResponseBodyAsStream(source.getResponseBodyAsStream());}
	  if(source.getResponseBodyAsString() != null){destination.setResponseBodyString(source.getResponseBodyAsString());}
	  if(source.getResponseBody() != null){destination.setResponseBody(source.getResponseBody());}
	  if(source.getResponseHeaders() != null){destination.setResponseHeaders(source.getResponseHeaders());}
	  if(source.getStatusText() != null){destination.setStatus(source.getStatusText());}
	 destination.setStatusCode(source.getStatusLine().getStatusCode());
	  return destination;
	  
  }
  
  /**
   * Mapps PutMethod to ScimResponse
   * @param PutMethod source
   * @param ScimResponse destination
   * @return ScimResponse
   * @throws Exception
   */
  public static ScimResponse map(PutMethod source,ScimResponse destination) throws IOException{
	 
	  if(destination == null){
			 destination = new ScimResponse();
		}
	  if(source.getResponseBodyAsStream() != null){destination.setResponseBodyAsStream(source.getResponseBodyAsStream());}
	  if(source.getResponseBodyAsString() != null){destination.setResponseBodyString(source.getResponseBodyAsString());}
	  if(source.getResponseBody() != null){destination.setResponseBody(source.getResponseBody());}
	  if(source.getResponseHeaders() != null){destination.setResponseHeaders(source.getResponseHeaders());}
	  if(source.getStatusText() != null){destination.setStatus(source.getStatusText());}
	  destination.setStatusCode(source.getStatusLine().getStatusCode());
	  return destination;
	  
  }
  /**
   * Mapps DeleteMethod to ScimResponse
   * @param DeleteMethod source
   * @param ScimResponse destination
   * @return ScimResponse
   * @throws Exception
   */
  public static ScimResponse map(DeleteMethod source,ScimResponse destination) throws IOException{
	  
	  if(destination == null){
			 destination = new ScimResponse();
		}
	  if(source.getResponseBodyAsStream() != null){destination.setResponseBodyAsStream(source.getResponseBodyAsStream());}
	  if(source.getResponseBodyAsString() != null){destination.setResponseBodyString(source.getResponseBodyAsString());}
	  if(source.getResponseBody() != null){destination.setResponseBody(source.getResponseBody());}
	  if(source.getResponseHeaders() != null){destination.setResponseHeaders(source.getResponseHeaders());}
	  if(source.getStatusText() != null){destination.setStatus(source.getStatusText());}
	  destination.setStatusCode(source.getStatusLine().getStatusCode());
	  return destination;
	  
  }
public static CreationResult map(RegisterResponse source, CreationResult destination) {
	 if(destination == null){
		 destination = new CreationResult();
	}
	 if(source.getClientId() != null){destination.setClientId(source.getClientId());}
	 if(source.getClientSecret() != null){destination.setClientSecret(source.getClientSecret());}
	 if(source.getErrorDescription() != null){destination.setErrorDescription(source.getErrorDescription());}
	 if(source.getErrorType() != null){destination.setErrorType(source.getErrorType());}
	 if(source.getErrorUri() != null){destination.setErrorUri(source.getErrorUri());}
	 if(source.getClientSecretExpiresAt() != null){destination.setExpiresAt(source.getClientSecretExpiresAt());}
	 if(source.getEntity() != null){destination.setEntity(source.getEntity());}
	 if(source.getStatus() != 0){destination.setStatus(source.getStatus());}
	 
	 return destination;
}
}
