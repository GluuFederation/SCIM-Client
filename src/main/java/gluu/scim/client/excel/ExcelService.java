/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim.client.excel;

import java.io.File;
import java.io.Serializable;

import jxl.Sheet;
import jxl.Workbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExcelService
 * 
 * @author Réda Zerrad Date: 06.06.2012
 */

public class ExcelService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 377450587320517192L;
	
	private static final Logger log = LoggerFactory.getLogger(ExcelService.class);
	
	public Table readExcelFile(File excelFile) {
		Table result = null;

		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(excelFile);
			
			Sheet sheet = workbook.getSheet(0);

			result = new Table();
		
			for (int j = 0; j < sheet.getColumns(); j++) {
				for (int i = 0; i < sheet.getRows(); i++) {
					result.addCell(new Cell(j, i, sheet.getCell(j, i).getContents()));
				}
			}
		} catch (Exception ex) {
			log.error("Error occured, could not read Excel file", ex);
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}

		return result;
	}

	
}
