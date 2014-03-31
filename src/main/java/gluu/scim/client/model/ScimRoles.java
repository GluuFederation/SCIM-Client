package gluu.scim.client.model;


/**
 * SCIM Roles 
 *
 * @author Reda Zerrad Date: 04.23.2012
 */

public class ScimRoles {
	
	private String value;
	
	public ScimRoles(){
		value = new String();
	}
	
	public String getValue(){
		return this.value;
	}
	
	public void setValue(String value){
		this.value = value;
	}

}
