/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client.model;

import java.io.Serializable;
import java.util.Date;

import org.xdi.oxauth.model.register.RegisterErrorResponseType;



/**
 * CreationResult , the result an oxAuth client creation
 * @author Reda Zerrad Date: 06.04.2012
 */
public class CreationResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8921080388686993042L;
	String clientId;
	String clientSecret;
	Date expiresAt;
	String errorDescription;
	RegisterErrorResponseType errorType;
	String errorUri;
	int status;
	String entity;
	
	
	
	public String getClientId(){
		return this.clientId;
	}
	
	public void setClientId(String clientId){
		this.clientId = clientId;
	}
	
	public String getClientSecret(){
		return this.clientSecret;
	}
	
	public void setClientSecret(String clientSecret){
		this.clientSecret = clientSecret;
	}
    
	public Date getExpiresAt(){
		return this.expiresAt;
	}
	
	public void setExpiresAt(Date expiresAt){
		this.expiresAt = expiresAt;
	}
	
	public String getErrorDescription(){
		return this.errorDescription;
	}
	
	public void setErrorDescription(String errorDescription){
		this.errorDescription = errorDescription;
	}
	public String getErrorUri(){
		return this.errorUri;
	}
	
	public void setErrorUri(String errorUri){
		this.errorUri = errorUri;
	}
	
	public RegisterErrorResponseType getErrorType(){
		return this.errorType;
	}
	
	public void setErrorType(RegisterErrorResponseType errorType){
		this.errorType = errorType;
	}
	
	public int getStatus(){
		return this.status;
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	
	public String getEntity(){
		return this.entity;
	}
	
	public void setEntity(String entity){
		this.entity = entity;
	}

}
