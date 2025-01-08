package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.model.*;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.ReportingService.ToDoItem;
import com.ec.survey.tools.ArchiveExecutor;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.CreateAllOLAPTablesExecutor;
import com.ec.survey.tools.FileUpdater;
import com.ec.survey.tools.NotAgreedToPsException;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.RecreateAllOLAPTablesExecutor;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.UpdateAllOLAPTablesExecutor;
import com.ec.survey.tools.WeakAuthenticationException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/administration")
public class AdministrationController extends BasicController {
	
	@Resource(name = "fileWorker")
	private FileUpdater fileWorker;
	
	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;
	
	@PostMapping(value = "/checkPasswordNotWeak")
	public @ResponseBody String checkPasswordNotWeak(HttpServletRequest request, Locale locale) {	
		String password = request.getParameter("password");
		if (Tools.isPasswordWeak(password))
		{
			return "weak";
		}
		
		return "";
	}
	
	@GetMapping(value = "/checkLoginFree")
	public @ResponseBody String checkLoginFree(HttpServletRequest request, Locale locale) {	
		String login = request.getParameter("login");
		try {
			if (administrationService.getUserForLogin(login, false) != null)
			{
				return "exists";
			}
		} catch (Exception e) {
			//happens when the user does not exist
		}
		
		return "free";
	}
		
	@PostMapping(value = "/saveUserConfiguration")
	public @ResponseBody String saveUserConfiguration(HttpServletRequest request) throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		int userId = sessionService.getCurrentUser(request).getId();
		UsersConfiguration usersConfiguration = administrationService.getUsersConfiguration(userId);
		
		if (usersConfiguration == null) {
			usersConfiguration = new UsersConfiguration();
			usersConfiguration.setUserId(userId);
		}
		
		String name = Tools.escapeHTML(request.getParameter("name"));
		String email = Tools.escapeHTML(request.getParameter(Constants.EMAIL));
		String otherEmail = Tools.escapeHTML(request.getParameter("otherEmail"));
		String language = Tools.escapeHTML(request.getParameter("ulang"));
		
		if (language != null && (language.length() != 4 || !StringUtils.isAlphanumeric(language)))
		{
			language = "false";
		}		
		
		String roles = Tools.escapeHTML(request.getParameter("roles"));
		String comment = Tools.escapeHTML(request.getParameter("comment"));
		
		usersConfiguration.setShowName(name != null && name.equalsIgnoreCase("true"));
		usersConfiguration.setShowEmail(email != null && email.equalsIgnoreCase("true"));
		usersConfiguration.setShowOtherEmail(otherEmail != null && otherEmail.equalsIgnoreCase("true"));
		usersConfiguration.setShowLanguage(language != null && language.equalsIgnoreCase("true"));
		usersConfiguration.setShowRoles(roles != null && roles.equalsIgnoreCase("true"));
		usersConfiguration.setShowComment(comment != null && comment.equalsIgnoreCase("true"));
		
		administrationService.save(usersConfiguration);
		
		return "ok";
	}
	
	@RequestMapping(value = "/synchronizeLDAP", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView synchronizeLDAP(HttpServletRequest request) {
		ldapService.reloadDepartments();
		ldapService.reloadEcasUser();
		return new ModelAndView("error/info", Constants.MESSAGE, "synchronization started");
	}
	
	@RequestMapping(value = "/synchronizeDomains", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView synchronizeDomains(HttpServletRequest request) {
		ldapService.reloadDomains();
		return new ModelAndView("error/info", Constants.MESSAGE, "synchronization started");
	}
	
	@RequestMapping(value = "/migrateFileSystemForSurvey/{survey}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView migrateFileSystemForSurvey(@PathVariable String survey, HttpServletRequest request) throws Exception {
		long tStart = System.currentTimeMillis();
		Survey draft = surveyService.getSurveyByUniqueIdToWrite(survey);		
		if (draft == null) throw new InvalidURLException();
		
		fileService.migrateAllSurveyFiles(draft);		

		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;
		
		return new ModelAndView("error/info", Constants.MESSAGE, "files for survey migrated, it took " + elapsedSeconds + " seconds");
	}
	
	@RequestMapping(value = "/migrateFileSystemForUser/{user}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView migrateFileSystemForUser(@PathVariable String user, HttpServletRequest request) throws IOException {
		int userId = Integer.parseInt(user);	
		long tStart = System.currentTimeMillis();
		
		fileService.migrateAllUserFiles(userId);
		
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;
		
		return new ModelAndView("error/info", Constants.MESSAGE, "files for user migrated, it took " + elapsedSeconds + " seconds");
	}
	
	@RequestMapping(value = "/migrateFileSystemForUsers", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView migrateFileSystemForUsers(HttpServletRequest request) throws IOException {
		long tStart = System.currentTimeMillis();
		
		fileService.migrateAllUserFiles();
		
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;
		
		return new ModelAndView("error/info", Constants.MESSAGE, "files for users migrated, it took " + elapsedSeconds + " seconds");
	}
	
	@RequestMapping(value = "/migrateFileSystemForArchives", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView migrateFileSystemForArchives(HttpServletRequest request) throws Exception {
		long tStart = System.currentTimeMillis();
		
		fileService.migrateAllArchiveFiles();
		
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;
		
		return new ModelAndView("error/info", Constants.MESSAGE, "files for archives migrated, it took " + elapsedSeconds + " seconds");
	}	
	
	@RequestMapping(value = "/startfileworker", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView startfileworker(HttpServletRequest request) {
		fileWorker.run();
		return new ModelAndView("error/info", Constants.MESSAGE, "file worker started");
	}
	
	@RequestMapping(value = "/startarchiving", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView startarchiving(HttpServletRequest request) {
		ArchiveExecutor executor = (ArchiveExecutor) context.getBean("archiveExecutor");
		taskExecutor.execute(executor);		
		
		return new ModelAndView("error/info", Constants.MESSAGE, "archive executor started");
	}
	
	@RequestMapping(value = "/deletetempfiles", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView deletetempfiles(HttpServletRequest request) {
		int deletedfiles = fileService.deleteOldTempFiles(new Date());
		return new ModelAndView("error/info", Constants.MESSAGE, deletedfiles +  " files deleted");
	}
	
	@RequestMapping(value = "/languages", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView languages(HttpServletRequest request) {
		return new ModelAndView("administration/languages","uploadItem", new UploadItem());
	}
		
	@PostMapping(value = "/languages")
	public ModelAndView languagesPost(UploadItem uploadItem, BindingResult bindingresult, HttpServletRequest request, HttpServletResponse response, Locale locale) {	
		        
	    List<String> messages = new ArrayList<>();
	
	    try {
	        MultipartFile file = uploadItem.getFileData();
	                  
	        InputStream inputStream = null;
	 
	        if (file.getSize() > 0) {
       	 
	        	inputStream = file.getInputStream();
	               
	            try {
	            	
	            	List<String> codes = surveyService.getLanguageCodes();
	            	List<Language> newLanguages = new ArrayList<>();
	            	
	            	HSSFWorkbook wb = new  HSSFWorkbook(inputStream);	            	
	            	HSSFSheet sheet = wb.getSheetAt(0);
	            	int rows = sheet.getPhysicalNumberOfRows();
	            	
	            	for (int r = 0; r < rows; r++) {
						HSSFRow row = sheet.getRow(r);
						if (row == null) {
							continue;
						}
						String enName = row.getCell(0).getStringCellValue().trim();
						String localName = ""; 
						if (row.getCell(1) != null) localName = row.getCell(1).getStringCellValue().trim();
						String code = row.getCell(2).getStringCellValue().trim().toUpperCase();
						double official = 0;
						if (row.getCell(3) != null) official = row.getCell(3).getNumericCellValue();
						
						if (!codes.contains(code))
						{
							newLanguages.add(new Language(code, localName, enName, official > 0));
							codes.add(code);
						}
	            	}
	            	
	            	surveyService.saveLanguages(newLanguages);
	            	
	            	messages.add(newLanguages.size() + " " + resources.getMessage("message.LanguagesCreated", null, "languages have been created.", locale));		
		               	              
	            	wb.close();
		            inputStream.close();
	               
               } catch (Exception e)
               {
               		logger.error(e.getLocalizedMessage(), e);
               		messages.add(resources.getMessage("error.FileNotValid", null, "The file is not valid.", locale));
               }
           }       
	        
	    } catch (Exception e) {
	    	logger.error(e.getLocalizedMessage(), e);
	    	messages.add(resources.getMessage("error.ProblemDuringImport", null, "There was a problem during the import process.", locale));		
	    }
	 	return new ModelAndView("administration/languages", "messages", messages);		
	}
	
	@RequestMapping(value = "/reporting", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView reporting(HttpServletRequest request) {
		ModelAndView result = new ModelAndView("administration/reporting");
		result.addObject("totaltodos", reportingService.getNumberOfToDos());
		result.addObject("totaltables", reportingService.getNumberOfTables());
		
		int drafts = surveyService.getNumberOfSurveys(true);
		int published = surveyService.getNumberOfSurveys(false);
		
		result.addObject("totaldraftsurveys", drafts - published);
		result.addObject("totalpublishedsurveys", published);		
		
		String enabled = settingsService.get(Setting.ReportingMigrationEnabled);
		result.addObject("enabled", enabled != null && enabled.equalsIgnoreCase("true"));
		
		String start = settingsService.get(Setting.ReportingMigrationStart);
		result.addObject("start", start);
		
		String time = settingsService.get(Setting.ReportingMigrationTime);
		result.addObject("time", time);
		
		String survey = settingsService.get(Setting.ReportingMigrationSurveyToMigrate);
		if (survey.length() == 0)
		{
			survey = "-";
		}
		result.addObject(Constants.SURVEY, survey);
		
		return result;
	}
	
	@PostMapping(value = "/reporting")
	public ModelAndView reportingPOST(HttpServletRequest request) {
		String enabled = Tools.escapeHTML(request.getParameter("enabled"));
		String start = Tools.escapeHTML(request.getParameter("start"));
		String time = Tools.escapeHTML(request.getParameter("time"));
		
		settingsService.update(Setting.ReportingMigrationEnabled, enabled != null && enabled.equalsIgnoreCase("true") ? "true" : "false");
		settingsService.update(Setting.ReportingMigrationStart, start);
		settingsService.update(Setting.ReportingMigrationTime, time);
		
		return new ModelAndView("redirect:/administration/reporting");
	}
	
	@RequestMapping(value = "/todosjson", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody List<ToDoItem> exportsjson(HttpServletRequest request) {	
		
		int itemsPerPage = -1;
		int page = -1;
				
		if(request.getParameter("rows") != null && request.getParameter("page") != null)
		{
			String itemsPerPageValue = request.getParameter("rows");		
			itemsPerPage = Integer.parseInt(itemsPerPageValue);
			
			String pageValue = request.getParameter("page");		
			page = Integer.parseInt(pageValue);
		}
		
		List<ToDoItem> items = null;
		
		items = reportingService.getToDos(page, itemsPerPage);
		return items;
	}
	
	@RequestMapping(value = "/executeToDo/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String executeToDo(@PathVariable String id) {
		try {
			int todoId = Integer.parseInt(id);
			ToDoItem todo = reportingService.getToDo(todoId);
			reportingService.executeToDo(todo, false);
			return "{\"success\": true}";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "{\"success\": false}";
	}
	
	@RequestMapping(value = "/deleteToDo/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String deleteToDo(@PathVariable String id) {
		try {
			int todoId = Integer.parseInt(id);
			ToDoItem todo = reportingService.getToDo(todoId);
			reportingService.removeToDo(todo, true);
			return "{\"success\": true}";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "{\"success\": false}";
	}
	
	@RequestMapping(value = "/recreateAllOLAPTables", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String recreateAllOLAPTables(HttpServletRequest request) {
		
		RecreateAllOLAPTablesExecutor executor = (RecreateAllOLAPTablesExecutor) context.getBean("recreateAllOLAPTablesExecutor");
		taskExecutor.execute(executor);
		
		return "started";	
	}
	
	@RequestMapping(value = "/createAllOLAPTables", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String createAllOLAPTables(HttpServletRequest request) {
		CreateAllOLAPTablesExecutor executor = (CreateAllOLAPTablesExecutor) context.getBean("createAllOLAPTablesExecutor");
		taskExecutor.execute(executor);
		
		return "started";
	}
	
	@RequestMapping(value = "/createOLAPTable/{shortname}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public  @ResponseBody String createOLAPTable(@PathVariable String shortname, HttpServletRequest request) throws Exception {
		try {
			reportingService.createOLAPTable(shortname, true, true);
			return "executed";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Constants.ERROR;
	}
	
	@RequestMapping(value = "/updateAllOLAPTables", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String updateAllOLAPTables(HttpServletRequest request) {
		UpdateAllOLAPTablesExecutor executor = (UpdateAllOLAPTablesExecutor) context.getBean("updateAllOLAPTablesExecutor");
		taskExecutor.execute(executor);
		
		return "started";		
	}
	
	@RequestMapping(value = "/updateOLAPTable/{shortname}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String updateOLAPTable(@PathVariable String shortname, HttpServletRequest request) throws Exception {
		try {
			reportingService.updateOLAPTable(shortname, true, true);
			return "executed";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Constants.ERROR;
	}
	
	@RequestMapping(value = "/recreateOLAPTable/{shortname}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public  @ResponseBody String recreateOLAPTable(@PathVariable String shortname, HttpServletRequest request) throws Exception {
		try {
			reportingService.recreateOLAPTable(shortname, true, true);
			return "executed";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Constants.ERROR;
	}
	
	@RequestMapping(value = "/organisations", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView organisations(HttpServletRequest request) throws ForbiddenURLException {
		if (!isChargeBackeEnabled()) throw new ForbiddenURLException();
		
		return new ModelAndView("administration/organisations");
	}
	
	@RequestMapping(value = "/organisation/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody OrganisationResult organisation(HttpServletRequest request, @PathVariable String code) throws ForbiddenURLException {
		if (!isChargeBackeEnabled()) throw new ForbiddenURLException();
		
		return surveyService.getSurveysByOrganisation(code);
	}
	
	
	@RequestMapping(value = "/organisationreport/{code}/{format}/{year}/{month}/{monthEnd}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ResponseEntity<byte[]> organisationReport(HttpServletRequest request, HttpServletResponse response, @PathVariable String code, @PathVariable String format, @PathVariable int year, @PathVariable int month, @PathVariable int monthEnd) throws ForbiddenURLException, IOException {
		if (!isChargeBackeEnabled()) throw new ForbiddenURLException();
		
		if (format.equalsIgnoreCase("csv")) {
			List<List<String>> surveys = surveyService.getSurveysByOrganisation(code, year, month);
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment;filename=report.csv");
			response.getWriter().print(organisationReportCSV(surveys));
			response.getWriter().flush();
		} else if (format.equalsIgnoreCase("json")) {
			int min = 0;
			String minPublished = request.getParameter("min");
			if (minPublished != null && minPublished .length() > 0) {
				min = Integer.parseInt(minPublished);
			}			
			
			response.setContentType("text/json");
			response.setHeader("Content-Disposition", "attachment;filename=report.json");
			response.getWriter().print(surveyService.organisationReportJSON(code, year, month, monthEnd, min));
			response.getWriter().flush();
		}

		return null;
	}
	
	private String organisationReportCSV(List<List<String>> surveys) {
		StringBuilder csv = new StringBuilder();
		
		for (List<String> survey : surveys) {
			boolean first = true;
			for (String entry : survey) {
				if (first) {
					first = false;
				} else {
					csv.append(",");			
				}
				
				if (entry != null && entry.contains(",")) {
					csv.append("\"").append(entry.replace("\"", "'")).append("\"");
				} else {				
					csv.append(entry == null ? "" : entry);
				}

			}
			csv.append("\n");
		}
		
		return csv.toString();
	}
}
