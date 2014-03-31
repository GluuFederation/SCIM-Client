package gluu.scim.client;

import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.httpclient.Header;
/**
 * SCIM Response 
 *
 * @author Reda Zerrad Date: 05.28.2012
 */
public class ScimResponse implements Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -1278941723910631845L;
	private String status;
	private int statusCode;
	private byte[] responseBody;
	private String responseBodyString;
	private Header[] responseHeaders;
	private InputStream responseBodyAsStream;
	
    public String getStatus(){
    	return this.status;
    }
    
    public void setStatus(String status){
    	this.status = status;
    }
	
    public int getStatusCode(){
    	return this.statusCode;
    }
    
    public void setStatusCode(int statusCode){
    	this.statusCode = statusCode;
    	
    }
    
    public String getResponseBodyString(){
    	return this.responseBodyString;
    }
    
    public void setResponseBodyString(String responseBodyString){
    	this.responseBodyString = responseBodyString;
    	
    }
    
    public byte[] getResponseBody(){
    	return this.responseBody;
    }
    
    public void setResponseBody(byte[] responseBody){
    	this.responseBody = responseBody;
    	
    }
    
    public Header[] getResponseHeaders(){
    	return this.responseHeaders;
    }
    
    public void setResponseHeaders(Header[] responseHeaders){
    	 this.responseHeaders = responseHeaders;
    }
    
    public Header getHeader(String headerName){
    	 for(Header header : responseHeaders){
    		 if(header.getName().equalsIgnoreCase(headerName)){
    			 return header;
    		 }
    	 }
    	 
    	 return null;
    }
    
    public InputStream getResponseBodyAsStream(){
    	return this.responseBodyAsStream;
    }
    
    public void setResponseBodyAsStream(InputStream responseBodyAsStream){
    	this.responseBodyAsStream = responseBodyAsStream;
    }
}
