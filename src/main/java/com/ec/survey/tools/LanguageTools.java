package com.ec.survey.tools;

import com.ec.survey.model.Language;
import com.ec.survey.service.SurveyService;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LanguageTools {
	
	private static final Logger logger = Logger.getLogger(LanguageTools.class);
			
	public static void loadLanguages(SurveyService surveyService, ServletContext servletContext) throws IOException
	{
		 InputStream inputStream = null;
	     try {
	        inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/EC/iso639-1.xls");
	           
	        List<String> codes = surveyService.getLanguageCodes();
         	List<Language> newLanguages = new ArrayList<>();
         	
         	HSSFWorkbook wb;
			wb = new  HSSFWorkbook(inputStream);
				            	
         	HSSFSheet sheet = wb.getSheetAt(0);
         	int rows = sheet.getPhysicalNumberOfRows();
         	
         	for (int r = 0; r < rows; r++) {
					HSSFRow row = sheet.getRow(r);
					if (row == null) {
						continue;
					}
					String enName = row.getCell(0).getStringCellValue().trim();
					String localName = row.getCell(1).getStringCellValue().trim();
					
					String code = row.getCell(2).getStringCellValue().trim().toUpperCase();
					double official = 0;
					if (row.getCell(3) != null) official = row.getCell(3).getNumericCellValue();
					
					if (!codes.contains(code))
					{
						newLanguages.add(new Language(code.toUpperCase(), localName, enName, official > 0));
						codes.add(code);
					}
         	}
         	
         	wb.close();
         	surveyService.saveLanguages(newLanguages);
        
	        } catch (IOException e) {
				logger.error(e.getLocalizedMessage(), e);          
	        } finally {
	            if (inputStream != null) {
	               inputStream.close();
	            }
	        }
	}

	

}
