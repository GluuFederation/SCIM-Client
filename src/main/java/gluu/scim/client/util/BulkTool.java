/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client.util;

import gluu.scim.client.model.ScimData;
import gluu.scim.client.model.ScimGroup;

import java.io.Serializable;


/**
 * BulkTool
 *
 * @author Reda Zerrad Date: 06.07.2012
 */
public class BulkTool implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5348292746577045084L;


	/**
     * Copy data from ScimData object to ScimGroup object
     * @param source
     * @param destination
     * @return ScimGroup
     * @throws Exception
     */
public static ScimGroup copy(ScimData source, ScimGroup destination) {
	if (source == null) {
        return null;
    }
    if (destination == null) {
        destination = new ScimGroup();
    }
    
    if(source.getId() != null && source.getId().length() > 0){destination.setId(source.getId());}
    if(source.getDisplayName() != null && source.getDisplayName().length() > 0){destination.setDisplayName(source.getDisplayName());}
    if(source.getSchemas() != null && source.getSchemas().size() > 0){destination.setSchemas(source.getSchemas());}
    if(source.getMembers() != null && source.getMembers().size() > 0){destination.setMembers(source.getMembers());}
    
    return destination;
    
    
}


/**
 * Copy data from ScimGroup object to ScimData object
 * @param source
 * @param destination
 * @return ScimData
 * @throws Exception
 */
public static ScimData copy(ScimGroup source, ScimData destination) {
if (source == null) {
    return null;
}
if (destination == null) {
    destination = new ScimData();
}

if(source.getId() != null && source.getId().length() > 0){destination.setId(source.getId());}
if(source.getDisplayName() != null && source.getDisplayName().length() > 0){destination.setDisplayName(source.getDisplayName());}
if(source.getSchemas() != null && source.getSchemas().size() > 0){destination.setSchemas(source.getSchemas());}
if(source.getMembers() != null && source.getMembers().size() > 0){destination.setMembers(source.getMembers());}

return destination;


}
}
