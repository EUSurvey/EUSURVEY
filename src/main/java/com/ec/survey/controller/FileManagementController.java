package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.exception.MessageException;
import com.ec.survey.model.FileFilter;
import com.ec.survey.model.FileResult;
import com.ec.survey.model.Paging;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Controller
@RequestMapping("/administration/files")
public class FileManagementController extends BasicController {
	
	public @Value("${ui.enablefilemanagement}") String enablefilemanagement;
	
	protected @Value("${filesystem.surveys}") String surveysDir;
	protected @Value("${filesystem.users}") String usersDir;
	protected @Value("${filesystem.archive}") String archiveDir;
	
	private void addStatistics(ModelAndView result)
	{
		result.addObject("filetypes", FileService.filetypes );
		result.addObject("fileextensions", FileService.fileextensions );
		
		File surveysFileDir = new java.io.File(surveysDir);
				
		long total = surveysFileDir.getTotalSpace();
		long free = surveysFileDir.getFreeSpace();
		
		result.addObject("totalDirSize", total - free);
		result.addObject("totalDirSizeNice", ConversionTools.getStringForBytes(total - free));
		result.addObject("totalDirFree", free);
		result.addObject("totalDirFreeNice",  ConversionTools.getStringForBytes(free));
	}
	
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView files(HttpServletRequest request, Model model, Locale locale) throws Exception {
		ModelAndView result = new ModelAndView("administration/filemanagement");
		addStatistics(result);
		
		FileFilter filter = (FileFilter) request.getSession().getAttribute("lastfilefilter");
		if (filter == null) filter = new FileFilter();
		
		String mode = (String)request.getSession().getAttribute("lastfilemode");
		if (mode == null) mode = "surveys";
		
		if (request.getParameter(Constants.DELETED) == null)
		{		
			filter.setSurveyFiles(true);
			filter.setSystemExports(true);
			filter.setTemporaryFiles(true);
		}
		result.addObject(Constants.FILTER, filter);
		result.addObject("mode", mode);
		
		if (request.getParameter(Constants.DELETED) != null)
		{
			if (!request.getParameter(Constants.DELETED).equalsIgnoreCase(Constants.ERROR))
			{
				result.addObject("info", resources.getMessage("info.FileDeleted", new Object[] {request.getParameter(Constants.DELETED)}, "Deleted", locale));
			} else if (request.getParameter(Constants.DELETED).equalsIgnoreCase(Constants.ERROR))
			{
				result.addObject(Constants.ERROR, resources.getMessage("error.OperationFailed", null, "Delete error", locale));
			}
			
			List<FileResult> resultfiles = fileService.getFiles2(filter);
			
			Paging<FileResult> paging = new Paging<>();		
			paging.setCurrentPage(filter.getPage());
			paging.setItemsPerPage(100);
			paging.setItems(resultfiles);
			if (paging.getItems().size() < paging.getItemsPerPage())
			{
				paging.setNumberOfItems((filter.getPage() - 1) * paging.getItemsPerPage() + paging.getItems().size());
			} else {
				paging.setNumberOfItems(Integer.MAX_VALUE);
			}
			paging.moveTo(Integer.toString(filter.getPage()));
			paging.setHideNumberOfItems(true);
			paging.setEnableGoToLastPage(false);
								
			result.addObject("paging", paging);
			result.addObject(Constants.FILTER, filter);
			
		} else if (request.getParameter("recreationstarted") != null)
		{
			if (request.getParameter("recreationstarted").equalsIgnoreCase("started"))
			{
				result.addObject("info", resources.getMessage("info.RecreateStarted2", null, "Recreate started", locale));
			} else if (request.getParameter("recreationstarted").equalsIgnoreCase(Constants.ERROR))
			{
				result.addObject(Constants.ERROR, resources.getMessage("error.OperationFailed", null, "Recreate error", locale));
			}
		} else if (request.getParameter("recreated") != null)
		{
			if (!request.getParameter("recreated").equalsIgnoreCase(Constants.ERROR))
			{
				result.addObject("info", resources.getMessage("info.FileRecreated", new Object[] {request.getParameter("recreated")}, "Recreated", locale));
			} else if (request.getParameter("recreated").equalsIgnoreCase(Constants.ERROR))
			{
				result.addObject(Constants.ERROR, resources.getMessage("error.OperationFailed", null, "Recreate error", locale));
			}
		}
		
		return result;
	}
	
	@PostMapping
	public ModelAndView filesPOST(HttpServletRequest request, Model model, Locale locale) throws Exception {	
		
		if (enablefilemanagement == null || !enablefilemanagement.equalsIgnoreCase("true"))
		{
			throw new InvalidURLException();
		}
		
		User user = sessionService.getCurrentUser(request);
		if (user == null || user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2) throw new ForbiddenURLException();
	
		
		ModelAndView result = new ModelAndView("administration/filemanagement");
		addStatistics(result);
		
		String uid = request.getParameter("surveyuid");
		String alias = request.getParameter("surveyalias");
		String mode = request.getParameter("mode");
		
		String newPage = request.getParameter("newPage");
		
		boolean exports = request.getParameter("surveyexports") != null && request.getParameter("surveyexports").equalsIgnoreCase("true");
		boolean files = request.getParameter("surveyfiles") != null && request.getParameter("surveyfiles").equalsIgnoreCase("true");
		boolean temp = request.getParameter("surveytemp") != null && request.getParameter("surveytemp").equalsIgnoreCase("true");
		boolean searchInFileSystem = request.getParameter("surveytarget") != null && request.getParameter("surveytarget").equalsIgnoreCase("fs");
		
		FileFilter inputFilter = new FileFilter();
		inputFilter.setSearchInFileSystem(searchInFileSystem);
		
		boolean fileerror = false;
		
		if (mode.equalsIgnoreCase("archive"))
		{
			uid = request.getParameter("archivesurveyuid");
			alias = request.getParameter("archivesurveyalias");
			inputFilter.setSurveyShortname(alias);
			inputFilter.setSurveyUid(uid);
			inputFilter.setArchivedSurveys(true);
		} else if (mode.equalsIgnoreCase("surveys"))
		{
			inputFilter.setSurveyShortname(alias);
			inputFilter.setSurveyUid(uid);
			inputFilter.setSystemExports(exports);
			inputFilter.setTemporaryFiles(temp);
			inputFilter.setSurveyFiles(files);
		} else if (mode.equalsIgnoreCase("users"))
		{
			if (request.getParameter("userid") != null && request.getParameter("userid").length() > 0)
			{
				inputFilter.setUserId(Integer.parseInt(request.getParameter("userid")));
			}
		} else if (mode.endsWith("bulkdownload"))
		{
			boolean checkall = request.getParameter("checkall") != null && request.getParameter("checkall").equalsIgnoreCase("true") ;
			String[] files2export = request.getParameterValues("checkfile");
			inputFilter = (FileFilter) request.getSession().getAttribute("lastfilesfilter");
			mode = mode.replace("bulkdownload", "");
			
			if (!checkall && (files2export == null || files2export.length == 0))
			{
				result.addObject(Constants.ERROR, "Please select at least one file");
				fileerror = true;
				inputFilter = (FileFilter) request.getSession().getAttribute("lastfilefilter");
				if (inputFilter == null)
				{
					inputFilter = new FileFilter();
					inputFilter.setSearchInFileSystem(searchInFileSystem);
				}
			} else {					
				inputFilter.setPage(1);
				inputFilter.setItemsPerPage(Integer.MAX_VALUE);
				fileService.startExport(inputFilter, checkall ? null : files2export, user);
				result.addObject("info", resources.getMessage("info.DownloadStarted", null, "Exported", locale));
			}
		} else if (mode.endsWith("bulkdelete"))
		{
			boolean checkall = request.getParameter("checkall") != null && request.getParameter("checkall").equalsIgnoreCase("true") ;
			String[] files2delete = request.getParameterValues("checkfile");
			inputFilter = (FileFilter) request.getSession().getAttribute("lastfilesfilter");
			mode = mode.replace("bulkdelete", "");
			
			if (!checkall && (files2delete == null || files2delete.length == 0))
			{
				result.addObject(Constants.ERROR, "Please select at least one file");
				fileerror = true;
				inputFilter = (FileFilter) request.getSession().getAttribute("lastfilefilter");
				if (inputFilter == null)
				{
					inputFilter = new FileFilter();
					inputFilter.setSearchInFileSystem(searchInFileSystem);
				}
			} else {					
				inputFilter.setPage(1);
				inputFilter.setItemsPerPage(Integer.MAX_VALUE);
				int deleted = fileService.deleteAll(inputFilter, checkall ? null : files2delete);
				result.addObject("info", resources.getMessage("info.FilesDeleted", new String[] { Integer.toString(deleted) }, "Deleted", locale));
			}
		} else if (mode.endsWith("reset")) {
	 		mode = mode.replace("reset", "");
	 	 	request.getSession().removeAttribute("lastfilefilter");
	 	 	request.getSession().setAttribute("lastfilemode", mode);
	 	 	return files(request, model, locale);
        }
		
		int page = newPage == null || newPage.length() == 0 || newPage.equalsIgnoreCase("first") ? 1 : Integer.parseInt(newPage);
		
		inputFilter.setPage(page);
		inputFilter.setItemsPerPage(100);
		
		request.getSession().setAttribute("lastfilefilter", inputFilter);
		request.getSession().setAttribute("lastfilemode", mode);
		
		if (uid == null || uid.length() == 0)
		{
			if (alias != null && alias.length() > 0)
			{
				try {
					Survey survey = surveyService.getSurveyByShortname(alias, true, user, request, false, false, false, false);
					if (survey != null) uid = survey.getUniqueId();
					inputFilter.setSurveyUid(uid);
				} catch (InvalidURLException ie) {
					
					if (mode.equalsIgnoreCase("archive"))
					{
						//survey already archived
						uid = archiveService.getSurveyUIDForArchivedSurveyShortname(alias);
					}
					
					if (uid == null)
					{
						result = files(request, model, locale);
						result.addObject(Constants.ERROR, "No survey with this alias found!");
						return result;
					}
				}
				
				inputFilter.setSurveyUid(uid);
			}
		} else {
			try{
			    UUID.fromString(uid);
			} catch (IllegalArgumentException exception){
				result = files(request, model, locale);
				result.addObject(Constants.ERROR, "The UID is not valid!");
				return result;
			}
		}
		
		if (mode.equals("surveys") && (uid == null || uid.length() == 0) && (alias == null || alias.length() == 0))
		{
			result = files(request, model, locale);
			if (!fileerror)
			{
				result.addObject(Constants.ERROR, "Please provide a survey alias or uid!");
			} else {
				result.addObject(Constants.ERROR, "Please select at least one file");
			}
			result.addObject(Constants.FILTER, inputFilter);
			result.addObject("mode", mode);
			return result;
		}
		
		List<FileResult> resultfiles = fileService.getFiles2(inputFilter);
		
		Paging<FileResult> paging = new Paging<>();		
		paging.setCurrentPage(page);
		paging.setItemsPerPage(100);
		paging.setItems(resultfiles);
		if (paging.getItems().size() < paging.getItemsPerPage())
		{
			paging.setNumberOfItems((page > 1 ? page - 1 : 0) * paging.getItemsPerPage() + paging.getItems().size());
		} else {
			paging.setNumberOfItems(Integer.MAX_VALUE);
		}
		paging.moveTo(newPage == null ? "first" : newPage);
		paging.setHideNumberOfItems(true);
		paging.setEnableGoToLastPage(false);
		
		if (!mode.equalsIgnoreCase("surveys"))
		{
			inputFilter.setSystemExports(exports);
			inputFilter.setTemporaryFiles(temp);
			inputFilter.setSurveyFiles(files);
		}
				
		result.addObject("paging", paging);
		result.addObject(Constants.FILTER, inputFilter);
		result.addObject("mode", mode);
		request.getSession().setAttribute("lastfilesfilter", inputFilter);
		
		return result;
	}

	@RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Object get(HttpServletRequest request, HttpServletResponse response) throws InvalidURLException, MessageException {
		
		if (enablefilemanagement == null || !enablefilemanagement.equalsIgnoreCase("true"))
		{
			throw new InvalidURLException();
		}
		
		String path = request.getParameter("path");
		if (!fileService.validateFilesPath(path)) {
			throw new MessageException("Invalid path found");
		}
		java.io.File file = new java.io.File(path);
		
		if (file.exists())
		{
			response.setContentLength((int) file.length());
			response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
			if (file.getName().toLowerCase().endsWith("pdf"))
			{
				response.setContentType("application/pdf");
			} else if (file.getName().toLowerCase().endsWith("xml"))
			{
				response.setContentType("application/xml");
			} else if (file.getName().toLowerCase().endsWith("xls") || file.getName().toLowerCase().endsWith("xlsx"))
			{
				response.setContentType("application/msexcel");
			} else if (file.getName().toLowerCase().endsWith("doc") || file.getName().toLowerCase().endsWith("docx"))
			{
				response.setContentType("application/msword");
			}
			try {
				FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		
		return null;
	}
	
	@RequestMapping(value = "/recreate", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Object recreate(HttpServletRequest request, HttpServletResponse response, Locale locale) throws InvalidURLException, MessageException {
		
		if (enablefilemanagement == null || !enablefilemanagement.equalsIgnoreCase("true"))
		{
			throw new InvalidURLException();
		}
		
		String path = request.getParameter("path");
		if (!fileService.validateFilesPath(path)) {
			throw new MessageException("Invalid path found");
		}
		java.io.File file = new java.io.File(path);
		
		try {		
			if (file.exists() && fileService.recreate(file, locale, resources))
			{
				return new ModelAndView("redirect:/administration/files?recreated=1");
			}		
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return new ModelAndView("redirect:/administration/files?recreated=error");
	}
	
	@RequestMapping(value = "/download/{uid}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Object download(@PathVariable String uid, HttpServletRequest request, HttpServletResponse response) throws InvalidURLException {
		
		if (enablefilemanagement == null || !enablefilemanagement.equalsIgnoreCase("true"))
		{
			throw new InvalidURLException();
		}
		
		if (uid == null || uid.length() == 0) return null;
		
		java.io.File file = new java.io.File(fileDir + "files" + uid + ".zip");
		
		if (file.exists())
		{
			fileService.logOldFileSystemUse(fileDir + "files" + uid + ".zip");
			
			response.setContentLength((int) file.length());
			response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
			response.setContentType("application/zip");
			
			try {
				FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		
		return null;
	}
	
	@RequestMapping(value = "/download/{userid}/{uid}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Object downloaduser(@PathVariable String userid, @PathVariable String uid, HttpServletRequest request, HttpServletResponse response) throws InvalidURLException {
		
		if (enablefilemanagement == null || !enablefilemanagement.equalsIgnoreCase("true"))
		{
			throw new InvalidURLException();
		}
		
		if (uid == null || uid.length() == 0) return null;
				
		java.io.File folder = fileService.getUsersFolder(Integer.parseInt(userid));
		java.io.File file = new java.io.File(String.format("%s/files%s.%s", folder.getPath(), uid, "zip"));
		
		if (!file.exists())
		{
			file = new java.io.File(fileDir + "files" + uid + ".zip");
			if (file.exists())
			{
				fileService.logOldFileSystemUse(fileDir + "files" + uid + ".zip");
			}
		}
		
		if (file.exists())
		{
			response.setContentLength((int) file.length());
			response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
			response.setContentType("application/zip");
			
			try {
				FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		
		return null;
	}
	
	@PostMapping(value = "/delete")
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws InvalidURLException, MessageException {
		
		if (enablefilemanagement == null || !enablefilemanagement.equalsIgnoreCase("true"))
		{
			throw new InvalidURLException();
		}
		
		String path = request.getParameter("path");
		if (!fileService.validateFilesPath(path)) {
			throw new MessageException("Invalid path found");
		}
		java.io.File file = new java.io.File(path);
		
		try {
			if (Files.deleteIfExists(file.toPath()))
			{
				return new ModelAndView("redirect:/administration/files?deleted=1");
			}
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return new ModelAndView("redirect:/administration/files?deleted=error");
	}
		
}
