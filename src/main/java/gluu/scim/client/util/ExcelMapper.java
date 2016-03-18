/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client.util;

import gluu.scim.client.excel.ExcelService;
import gluu.scim.client.excel.Table;
import gluu.scim.client.model.BulkRequests;
import gluu.scim.client.model.ScimBulkOperation;
import gluu.scim.client.model.ScimData;
import gluu.scim.client.model.ScimGroup;
import gluu.scim.client.model.ScimGroupMembers;
import gluu.scim.client.model.ScimPersonAddresses;
import gluu.scim.client.model.ScimPersonEmails;
import gluu.scim.client.model.ScimPersonPhones;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ExcelMapper
 *
 * @author Reda Zerrad Date: 06.04.2012
 */
public class ExcelMapper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2223616845585428654L;
	private static final Logger log = LoggerFactory.getLogger(ExcelMapper.class);
	
	/**
	 * Parses an XLS file into a User's ScimBulkOperation
	 * @param String fileLocation
	 * @return ScimBulkOperation
	 * @throws Exception
	 */
	public static ScimBulkOperation mapUsers(String fileLocation){
		
		try{
			String bulkId = "ScimClient";
			String version = "ScimClient";
			
			
		ExcelService excelService = new ExcelService();
		File excelFile = new File(fileLocation);
		Table table = excelService.readExcelFile(excelFile);
		ScimBulkOperation operation = new ScimBulkOperation();

		operation.getSchemas().add("urn:scim:schemas:core:1.0");

		for(int i = 2; i<= table.getCountRows()-1;i++){
		
			BulkRequests oneRequest = new BulkRequests();
			ScimData person = new ScimData();
			person.getSchemas().add("urn:scim:schemas:core:1.0");
			
			if(table.getCellValue(0, i) != null && table.getCellValue(0, i).length() > 0)person.setUserName(table.getCellValue(0, i));

			if(table.getCellValue(1, i) != null && table.getCellValue(1, i).length() > 0)person.setExternalId(table.getCellValue(1, i));
			if(table.getCellValue(2, i) != null && table.getCellValue(2, i).length() > 0)person.setPassword(table.getCellValue(2, i));
			if(table.getCellValue(3, i) != null && table.getCellValue(3, i).length() > 0)person.getName().setGivenName(table.getCellValue(3, i));
			if(table.getCellValue(4, i) != null && table.getCellValue(4, i).length() > 0)person.getName().setMiddleName(table.getCellValue(4, i));
			if(table.getCellValue(5, i) != null && table.getCellValue(5, i).length() > 0)person.getName().setFamilyName(table.getCellValue(5, i));
            
			List<ScimPersonEmails> listEmails = new ArrayList<ScimPersonEmails>();
			
			if(table.getCellValue(6, i) != null && table.getCellValue(6, i).length() > 0){
				
				ScimPersonEmails email1 = new ScimPersonEmails();
				if(table.getCellValue(6, i) != null && table.getCellValue(6, i).length() > 0){ email1.setValue(table.getCellValue(6, i));}
				if(table.getCellValue(7, i) != null && table.getCellValue(7, i).length() > 0){email1.setType(table.getCellValue(7, i));}
				if(table.getCellValue(8, i) != null && table.getCellValue(8, i).length() > 0){email1.setPrimary(table.getCellValue(8, i));}
				listEmails.add(email1);
		
			}
			
                if(table.getCellValue(9, i) != null && table.getCellValue(9, i).length() > 0){
				
				ScimPersonEmails email2 = new ScimPersonEmails();
				if(table.getCellValue(9, i) != null && table.getCellValue(9, i).length() > 0){ email2.setValue(table.getCellValue(9, i));}
				if(table.getCellValue(10, i) != null && table.getCellValue(10, i).length() > 0){email2.setType(table.getCellValue(10, i));}
				if(table.getCellValue(11, i) != null && table.getCellValue(11, i).length() > 0){email2.setPrimary(table.getCellValue(11, i));}
				listEmails.add(email2);
			}
                
                if(table.getCellValue(12, i) != null && table.getCellValue(12, i).length() > 0){
    				
    				ScimPersonEmails email3 = new ScimPersonEmails();
    				if(table.getCellValue(12, i) != null && table.getCellValue(12, i).length() > 0){ email3.setValue(table.getCellValue(12, i));}
    				if(table.getCellValue(13, i) != null && table.getCellValue(13, i).length() > 0){email3.setType(table.getCellValue(13, i));}
    				if(table.getCellValue(14, i) != null && table.getCellValue(14, i).length() > 0){email3.setPrimary(table.getCellValue(14, i));}
    				listEmails.add(email3);
    			}
                
                if(listEmails != null && listEmails.size() > 0){person.setEmails(listEmails);}
                
                List<ScimPersonAddresses> listAddresses = new ArrayList<ScimPersonAddresses>();
		        
                if(table.getCellValue(15, i) != null && table.getCellValue(15, i).length() > 0){
                	
                	ScimPersonAddresses address1 = new ScimPersonAddresses();
                	if(table.getCellValue(15, i) != null && table.getCellValue(15, i).length() > 0){ address1.setStreetAddress(table.getCellValue(15, i));}
                	if(table.getCellValue(16, i) != null && table.getCellValue(16, i).length() > 0){ address1.setLocality(table.getCellValue(16, i));}
                	if(table.getCellValue(17, i) != null && table.getCellValue(17, i).length() > 0){ address1.setRegion(table.getCellValue(17, i));}
                	if(table.getCellValue(18, i) != null && table.getCellValue(18, i).length() > 0){ address1.setPostalCode(table.getCellValue(18, i));}
                	if(table.getCellValue(19, i) != null && table.getCellValue(19, i).length() > 0){ address1.setCountry(table.getCellValue(19, i));}
                	if(table.getCellValue(20, i) != null && table.getCellValue(20, i).length() > 0){ address1.setPrimary(table.getCellValue(20, i));}
                	if(table.getCellValue(21, i) != null && table.getCellValue(21, i).length() > 0){ address1.setType(table.getCellValue(21, i));}

                	address1.setFormatted(address1.getStreetAddress() +","+ address1.getLocality()+","+address1.getPostalCode()+","+address1.getRegion() + "," + address1.getCountry());
                	
                	listAddresses.add(address1);

                }
                
                    if(table.getCellValue(22, i) != null && table.getCellValue(22, i).length() > 0){
                	
                	ScimPersonAddresses address2 = new ScimPersonAddresses();
                	if(table.getCellValue(22, i) != null && table.getCellValue(22, i).length() > 0){ address2.setStreetAddress(table.getCellValue(22, i));}
                	if(table.getCellValue(23, i) != null && table.getCellValue(23, i).length() > 0){ address2.setLocality(table.getCellValue(23, i));}
                	if(table.getCellValue(24, i) != null && table.getCellValue(24, i).length() > 0){ address2.setRegion(table.getCellValue(24, i));}
                	if(table.getCellValue(25, i) != null && table.getCellValue(25, i).length() > 0){ address2.setPostalCode(table.getCellValue(25, i));}
                	if(table.getCellValue(26, i) != null && table.getCellValue(26, i).length() > 0){ address2.setCountry(table.getCellValue(26, i));}
                	if(table.getCellValue(27, i) != null && table.getCellValue(27, i).length() > 0){ address2.setPrimary(table.getCellValue(27, i));}
                	if(table.getCellValue(28, i) != null && table.getCellValue(28, i).length() > 0){ address2.setType(table.getCellValue(28, i));}

                	address2.setFormatted(address2.getStreetAddress() +","+ address2.getLocality()+","+address2.getPostalCode()+","+address2.getRegion() + "," + address2.getCountry());
                	
                	listAddresses.add(address2);

                }
                    
                if(listAddresses != null && listAddresses.size() > 0){person.setAddresses(listAddresses);}
                
                List<ScimPersonPhones> listPhones = new ArrayList<ScimPersonPhones>();
    			
    			if(table.getCellValue(29, i) != null && table.getCellValue(29, i).length() > 0){
    				
    				ScimPersonPhones phone1 = new ScimPersonPhones();
    				if(table.getCellValue(29, i) != null && table.getCellValue(29, i).length() > 0){phone1.setValue(table.getCellValue(29, i));}
    				if(table.getCellValue(30, i) != null && table.getCellValue(30, i).length() > 0){phone1.setType(table.getCellValue(30, i));}
    				listPhones.add(phone1);
    		
    			}
    			
    			if(table.getCellValue(31, i) != null && table.getCellValue(31, i).length() > 0){
    				
    				ScimPersonPhones phone2 = new ScimPersonPhones();
    				if(table.getCellValue(31, i) != null && table.getCellValue(31, i).length() > 0){phone2.setValue(table.getCellValue(31, i));}
    				if(table.getCellValue(32, i) != null && table.getCellValue(32, i).length() > 0){phone2.setType(table.getCellValue(32, i));}
    				listPhones.add(phone2);
    		
    			}
    			
    			if(table.getCellValue(33, i) != null && table.getCellValue(33, i).length() > 0){
    				
    				ScimPersonPhones phone3 = new ScimPersonPhones();
    				if(table.getCellValue(33, i) != null && table.getCellValue(33, i).length() > 0){phone3.setValue(table.getCellValue(33, i));}
    				if(table.getCellValue(34, i) != null && table.getCellValue(34, i).length() > 0){phone3.setType(table.getCellValue(34, i));}
    				listPhones.add(phone3);
    		
    			}
                if(listPhones != null && listPhones.size() > 0){person.setPhoneNumbers(listPhones);}
                if(table.getCellValue(36, i) == null || table.getCellValue(36, i).length() <= 0 
                		|| !table.getCellValue(36, i).equalsIgnoreCase("Add")
                		&& !table.getCellValue(36, i).equalsIgnoreCase("Update")
                		&& !table.getCellValue(36, i).equalsIgnoreCase("Delete"))
                {
               
                	log.error("Error, no operation specified in cell 36-{}", i);
                	return null;
                }
                if(table.getCellValue(36, i) != null && table.getCellValue(36, i).length() > 0){
                	if(table.getCellValue(36, i).equalsIgnoreCase("Add")){
                		oneRequest.setBulkId(bulkId);
                		oneRequest.setPath("/Users");
                		oneRequest.setMethod("POST");
                	}
                	
                	if(table.getCellValue(36, i).equalsIgnoreCase("Update")){
                		oneRequest.setVersion(version);
                		oneRequest.setPath("/Users/@" + table.getCellValue(35, i));
                		oneRequest.setMethod("PUT");
                	}
                	
                	if(table.getCellValue(36, i).equalsIgnoreCase("Delete")){
                		oneRequest.setVersion(version);
                		oneRequest.setPath("/Users/@" + table.getCellValue(35, i));
                		oneRequest.setMethod("DELETE");
                	}
                }
                oneRequest.setData(person);
                operation.getOperations().add(oneRequest);
		}
		
		return operation;
		}catch(Exception ex){
			log.error("an Error occured , could not parse Excel file " , ex);
			return null;
		}
	}
	
	/**
	 * Parses an XLS file into a Groups's ScimBulkOperation
	 * @param String fileLocation
	 * @return ScimBulkOperation
	 * @throws Exception
	 */
	public static ScimBulkOperation mapGroups(String fileLocation){
		try{
		String bulkId = "ScimClient";
		String version = "ScimClient";
		
		ExcelService excelService = new ExcelService();
		File excelFile = new File(fileLocation);
		Table table = excelService.readExcelFile(excelFile);
		ScimBulkOperation operation = new ScimBulkOperation();
		List<ScimGroupMembers> listMembers = new ArrayList<ScimGroupMembers>();

		operation.getSchemas().add("urn:scim:schemas:core:1.0");

		for(int i = 1;i<=table.getCountCols()-1;i++){
	        ScimGroup group = new ScimGroup();
	        BulkRequests oneRequest = new BulkRequests();
	        group.getSchemas().add("urn:scim:schemas:core:1.0");
	        if(table.getCellValue(i, 0) != null && table.getCellValue(i, 0).length() > 0){group.setDisplayName(table.getCellValue(i, 0));}
	        if(table.getCellValue(i, 1) != null && table.getCellValue(i, 1).length() > 0){group.setId("@"+table.getCellValue(i, 1));}
	        if(table.getCellValue(i, 2) == null || table.getCellValue(i, 2).length() <= 0 
            		|| !table.getCellValue(i, 2).equalsIgnoreCase("Add")
            		&& !table.getCellValue(i, 2).equalsIgnoreCase("Update")
            		&& !table.getCellValue(i, 2).equalsIgnoreCase("Delete"))
            {
           
	        	
            	log.error("Error, no operation specified in cell {}-2", i);
            	return null;
            } 

	        if(table.getCellValue(i, 2) != null && table.getCellValue(i, 2).length() > 0){
            	if(table.getCellValue(i, 2).equalsIgnoreCase("Add")){
            		oneRequest.setBulkId(bulkId);
            		oneRequest.setPath("/Groups");
            		oneRequest.setMethod("POST");
            	}
            	
            	if(table.getCellValue(i, 2).equalsIgnoreCase("Update")){
            		oneRequest.setVersion(version);
            		oneRequest.setPath("/Groups/@" + table.getCellValue(i, 1));
            		oneRequest.setMethod("PUT");
            	}
            	
            	if(table.getCellValue(i, 2).equalsIgnoreCase("Delete")){
            		oneRequest.setVersion(version);
            		oneRequest.setPath("/Groups/@" + table.getCellValue(i, 1));
            		oneRequest.setMethod("DELETE");
            	}
            }
	       
	        for(int j = 4; j<=table.getCountRows()-1 ;j++){
	        	ScimGroupMembers member = new ScimGroupMembers();

	        	if(table.getCellValue(i, j) != null && table.getCellValue(i, j).length()>0){member.setValue("@"+ table.getCellValue(i, j));}
	            listMembers.add(member);
	        }
	        
	        if(listMembers != null && listMembers.size() > 0){group.setMembers(listMembers);}

	        ScimData data = BulkTool.copy(group, null);

	        oneRequest.setData(data);

	        operation.getOperations().add(oneRequest);

	        
		}
		return operation;
		}catch(Exception ex){
			log.error("an Error occured , could not parse Excel file " , ex);
			return null;
		}
		

	}
}
