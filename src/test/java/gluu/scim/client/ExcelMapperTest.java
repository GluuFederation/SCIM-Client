package gluu.scim.client;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import gluu.scim.client.model.ScimBulkOperation;
import gluu.scim.client.util.ExcelMapper;

import java.io.File;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * ExcelMapper tests
 *
 * @author Reda Zerrad Date: 06.06.2012
 */
public class ExcelMapperTest {
	
	ScimBulkOperation operation;
	
	@BeforeTest
	public void init(){
		operation = null;
	}

	@Parameters({"excelFileLocationUsers"})
	@Test
	public void excelFileParsingUsersTest(final String excelFileLocationUsers){
		
		operation = ExcelMapper.mapUsers(System.getProperty("user.dir") + File.separator + excelFileLocationUsers);
		 assertNotNull(operation.getOperations(),"Unexpected result: operation is null");
		 assertEquals(operation.getOperations().get(0).getData().getUserName(),"test_user","Unexpected result: userName does not match");
		 assertEquals(operation.getOperations().get(0).getMethod(),"POST","Unexpected result: method does not match");

	}
	
	@Parameters({"excelFileLocationGroups"})
	@Test
	public void excelFileParsingGroupTest(final String excelFileLocationGroups){
		
		operation = ExcelMapper.mapGroups(System.getProperty("user.dir") + File.separator + excelFileLocationGroups);
		 assertNotNull(operation.getOperations(),"Unexpected result: operation is null");
		 assertEquals(operation.getOperations().get(0).getData().getId(),"@!1111!0003!D9B4","Unexpected result: Id does not match");
		 assertEquals(operation.getOperations().get(0).getMethod(),"PUT","Unexpected result: method does not match");

	}
}
