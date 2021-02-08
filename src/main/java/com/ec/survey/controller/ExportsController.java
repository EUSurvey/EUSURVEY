package com.ec.survey.controller;

import com.ec.survey.model.*;
import com.ec.survey.model.Export.ExportFormat;
import com.ec.survey.model.Export.ExportState;
import com.ec.survey.model.Export.ExportType;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.MailService;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.NotAgreedToPsException;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.WeakAuthenticationException;

import org.apache.maven.surefire.shade.org.apache.maven.shared.utils.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/exports")
public class ExportsController extends BasicController {

	@Resource(name = "mailService")
	protected MailService mailService;

	@PostMapping(value = "/start/{type}/{format}")
	public @ResponseBody String startExport(@RequestParam("exportName") String exportName,
			@RequestParam("allAnswers") String allAnswers, @RequestParam("showShortnames") String showShortnames,
			@PathVariable("type") String type, @PathVariable("format") String format,
			@RequestParam("group") String group, HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		try {
			final Form form = sessionService.getFormOrNull(request, null, true);
			String uid = "";

			Export export = new Export();
			export.setDate(new Date());
			export.setState(ExportState.Pending);
			export.setUserId(sessionService.getCurrentUser(request).getId());
			export.setName(exportName);

			if (type.startsWith("Files")) {
				export.setType(ExportType.Files);
				uid = type.substring(5).replace("true", "").replace("false", "");

			} else {
				export.setType(ExportType.valueOf(type));
			}

			export.setFormat(ExportFormat.valueOf(format));

			if (allAnswers != null && allAnswers.equalsIgnoreCase("true")) {
				export.setAllAnswers(true);
			}

			if (export.getType().equals(ExportType.AddressBook)) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> filter = (HashMap<String, String>) request.getSession()
						.getAttribute("attendees-filter");
				ResultFilter resultFilter = new ResultFilter();
				resultFilter.setFilterValues(filter);
				export.setResultFilter(resultFilter);
			} else if (export.getType().equals(ExportType.Activity)) {
				ActivityFilter filter = sessionService.getLastActivityFilter(request);
				export.setActivityFilter(filter);
				export.setSurvey(form.getSurvey());
			} else if (export.getType().equals(ExportType.Tokens)) {
				export.setParticipationGroup(Integer.parseInt(group));
				export.setSurvey(form.getSurvey());
			} else if (export.getType().equals(ExportType.Files)) {
				boolean active = (Boolean) request.getSession().getAttribute("results-source-active");
				Survey survey = form.getSurvey();
				survey = surveyService.getSurvey(survey.getShortname(), !active, false, false, false, null, true,
						false);
				export.setSurvey(survey);
				export.setShowShortnames(showShortnames != null && showShortnames.equalsIgnoreCase("true"));
				ResultFilter filter = answerService.initialize(sessionService.getLastResultFilter(request)).copy();
				filter.getVisibleQuestions().clear();
				filter.getVisibleQuestions().add(uid);
				export.setResultFilter(filter);
				form.setSurvey(survey);
			} else if (export.getType().equals(ExportType.Survey)) {
				User u = sessionService.getCurrentUser(request);
				if (u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2) {
					return "notallowed";
				}

				Survey survey = null;

				String shortname = request.getParameter(Constants.SHORTNAME);
				if (shortname != null && shortname.length() > 0) {
					survey = surveyService.getSurveyByShortname(shortname, true, u, request, false, false, true, false);
				} else {
					survey = form.getSurvey();
				}

				export.setSurvey(survey);
				export.setResultFilter(new ResultFilter());
				form.setSurvey(survey);
			} else {
				boolean active = (Boolean) request.getSession().getAttribute("results-source-active");
				Survey survey = form.getSurvey();
				survey = surveyService.getSurvey(survey.getShortname(), !active, false, false, false, null, true,
						false);
				export.setSurvey(survey);
				export.setShowShortnames(showShortnames != null && showShortnames.equalsIgnoreCase("true"));

				ResultFilter origFilter = answerService.initialize(sessionService.getLastResultFilter(request));

				export.setResultFilter(origFilter.copy());
				form.setSurvey(survey);
			}

			exportService.prepareExport(form, export);
			exportService.startExport(form, export, false, resources, locale, null, null, false);
			sessionService.setCheckExport(request, "true");
			return "success";

		} catch (Exception ex) {
			logger.error("Export request failed.");
			logger.error(ex.getLocalizedMessage(), ex);
			return "failure";
		}
	}

	@RequestMapping(value = "/checkNewMails", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody String checkNewMails(HttpServletRequest request, HttpServletResponse response, Locale locale) {

		SessionInfo info = (SessionInfo) request.getSession().getAttribute("sessioninfo");

		if (info != null) {
			Survey survey = surveyService.getSurvey(info.getSurvey());

			if (survey != null) {
				MailTask task = mailService.getFirstFinishedMailTask(survey.getUniqueId());

				if (task != null) {
					task.setNotified(true);
					mailService.save(task);

					String message = "";
					if (task.getState().equalsIgnoreCase(MailTask.ERROR)) {
						message = resources.getMessage("error.Mails", null,
								"There was an error during sending of the mails", locale);
					} else if (task.getState().equalsIgnoreCase(MailTask.FINISHED)) {
						message = resources.getMessage("info.MailsFinished",
								new String[] { Integer.toString(task.getMailsSent()) },
								"X mails have been sent out successfully", locale);
					}

					return "{\"state\": \"" + task.getState() + "\",\"message\":\"" + message + "\"}";
				}
			}
		}

		return "{\"state\": \"NONE\"}";
	}

	@RequestMapping(value = "/checkNew", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody String checkNew(@RequestParam("uid") String uid, HttpServletRequest request,
			HttpServletResponse response) {

		if (uid == null || !StringUtils.isNumeric(uid)) {
			return "{\"newname\": \"0\",\"checkExport\":false}";
		}

		int userID = Integer.parseInt(uid);
		Boolean hasPendingExports = exportService.hasPendingExports(userID);
		String checkExport = hasPendingExports.toString().toLowerCase();
		try {
			if (hasPendingExports) {
				List<Export> exports = exportService.getExports(userID, "name", true, false, true);
				if (!exports.isEmpty()) {
					Export export = exports.get(0);
					sessionService.setCheckExport(request, checkExport);
					exportService.setNotified(export.getId());
					hasPendingExports = exportService.hasPendingExports(userID);
					checkExport = hasPendingExports.toString().toLowerCase();
					return "{\"newname\": \"" + export.getName() + "\",\"checkExport\":" + checkExport + "}";
				}
			} else {
				sessionService.setCheckExport(request, checkExport);
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return "{\"newname\": \"0\",\"checkExport\":" + checkExport + "}";
	}

	@RequestMapping(value = "/list")
	public ModelAndView root(HttpServletRequest request)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		sessionService.getCurrentUser(request);

		// default
		String sortKey = "date";
		String sortOrder = "DESC";

		// after recreate
		if (request.getSession().getAttribute("ExportsSortKey") != null)
			sortKey = (String) request.getSession().getAttribute("ExportsSortKey");
		if (request.getSession().getAttribute("ExportsSortOrder") != null)
			sortOrder = (String) request.getSession().getAttribute("ExportsSortOrder");
		// sorting
		if (request.getParameter("sortKey") != null)
			sortKey = Tools.escapeHTML(request.getParameter("sortKey"));
		if (request.getParameter("sortOrder") != null)
			sortOrder = Tools.escapeHTML(request.getParameter("sortOrder"));

		ModelAndView result = new ModelAndView("exports/exports");

		result.addObject("sortKey", sortKey);
		result.addObject("sortOrder", sortOrder);

		request.getSession().setAttribute("ExportsSortKey", sortKey);
		request.getSession().setAttribute("ExportsSortOrder", sortOrder);

		return result;
	}

	@RequestMapping(value = "/exportsjson", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<Export> exportsjson(HttpServletRequest request)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {

		int itemsPerPage = -1;
		int page = -1;
		String sortKey = "date";
		String sortOrder = "DESC";

		// after recreate
		if (request.getSession().getAttribute("ExportsSortKey") != null)
			sortKey = (String) request.getSession().getAttribute("ExportsSortKey");
		if (request.getSession().getAttribute("ExportsSortOrder") != null)
			sortOrder = (String) request.getSession().getAttribute("ExportsSortOrder");
		// sorting
		if (request.getParameter("sortKey") != null)
			sortKey = request.getParameter("sortKey");
		if (request.getParameter("sortOrder") != null)
			sortOrder = request.getParameter("sortOrder");

		if (request.getParameter("rows") != null && request.getParameter("page") != null) {
			String itemsPerPageValue = request.getParameter("rows");
			itemsPerPage = Integer.parseInt(itemsPerPageValue);

			String pageValue = request.getParameter("page");
			page = Integer.parseInt(pageValue);
		}

		User user = sessionService.getCurrentUser(request);
		List<Export> exports = null;

		if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement).equals(2)) {
			exports = exportService.getExports(-1, sortKey, sortOrder.equalsIgnoreCase("asc"), page, itemsPerPage, true,
					true, false, true);
		} else {
			exports = exportService.getExports(user.getId(), sortKey, sortOrder.equalsIgnoreCase("asc"), page,
					itemsPerPage, true, true, false, true);
		}
		return exports;
	}

	@RequestMapping(value = "/download/{exportId}", method = { RequestMethod.GET, RequestMethod.HEAD })
	@ResponseBody
	public ResponseEntity<byte[]> downloadExport(@PathVariable int exportId, HttpServletRequest request,
			HttpServletResponse response) {

		final HttpHeaders headers = new HttpHeaders();

		try {

			Export export = exportService.getExport(exportId, false);
			if (export == null || !(sessionService.checkUser(export.getUserId(), request) || sessionService
					.getCurrentUser(request).getGlobalPrivileges().get(GlobalPrivilege.FormManagement).equals(2))) {
				String msg = "ERROR: Access denied.";
				headers.setContentType(MediaType.TEXT_PLAIN);
				return new ResponseEntity<>(msg.getBytes(), headers, HttpStatus.FORBIDDEN);
			}
			String filePath = exportService.getTempExportFilePath(export, null);

			if (export.getZipped() != null && export.getZipped()) {
				filePath += ".zip";
			}

			java.io.File file = new java.io.File(filePath);

			if (!file.exists()) {
				filePath = tempFileDir + file.getName();
				file = new java.io.File(filePath);
			}

			FileInputStream inputStream = new FileInputStream(filePath);

			if (export.getZipped() != null && export.getZipped()) {
				response.setContentType("application/zip");
			} else {
				switch (export.getFormat()) {
				case xls:
					response.setContentType("application/vnd.ms-excel");
					break;
				case ods:
					response.setContentType("application/vnd.oasis.opendocument.spreadsheet");
					break;
				case doc:
					response.setContentType("application/msword");
					break;
				case odt:
					response.setContentType("application/vnd.oasis.opendocument.text");
					break;
				case pdf:
					if (export.getType() == ExportType.Content) {				
						response.setContentType("application/zip");
					} else {
						response.setContentType("application/pdf");
					}

					break;
				case xml:
					response.setContentType("application/xml");
					break;
				default:
					break;
				}
			}
			String fileAttHeader = String.format("attachment;filename=%s", exportService.getReturnFileName(export));
			response.setHeader("Content-Disposition", fileAttHeader);

			Long fileSize = file.length();
			response.setContentLength(fileSize.intValue());

			IOUtils.copy(inputStream, response.getOutputStream());
			inputStream.close();
			response.getOutputStream().flush();
			response.getOutputStream().close();

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<>(e.getLocalizedMessage().getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return null;

	}

	@PostMapping(value = "/delete/{exportId}")
	public @ResponseBody String deleteExport(@PathVariable int exportId, HttpServletRequest request) {
		try {
			Export export = exportService.getExport(exportId, false);
			if (export == null || !(sessionService.checkUser(export.getUserId(), request) || sessionService
					.getCurrentUser(request).getGlobalPrivileges().get(GlobalPrivilege.FormManagement).equals(2))) {
				return "Access denied";
			}

			exportService.deleteExport(export);
			if (export.getSurvey() != null) {
				activityService.log(311, export.getId().toString(), null,
						sessionService.getCurrentUser(request).getId(), export.getSurvey().getUniqueId());
			}

			return "success";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "Deletion failed.";
		}
	}

	@RequestMapping(value = "/recreate/{exportId}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView recreateExport(@PathVariable int exportId, HttpServletRequest request, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, IOException {
		Export export = exportService.getExport(exportId, true);
		if (export == null || !(sessionService.checkUser(export.getUserId(), request) || sessionService
				.getCurrentUser(request).getGlobalPrivileges().get(GlobalPrivilege.FormManagement).equals(2))) {
			return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, "Access denied");
		}
		exportService.recreateExport(export, locale, resources);
		return new ModelAndView("redirect:/exports/list");
	}

	@RequestMapping(value = "/recreateMany/{exportIdList}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView recreateExport(@PathVariable String exportIdList, HttpServletRequest request, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, IOException {
		String[] exportIds = exportIdList.split("-");
		List<Export> exports = new ArrayList<>();

		for (String exportId : exportIds) {
			Export export = exportService.getExport(Integer.parseInt(exportId), true);

			if (export != null) {

				if (!(sessionService.checkUser(export.getUserId(), request) || sessionService.getCurrentUser(request)
						.getGlobalPrivileges().get(GlobalPrivilege.FormManagement).equals(2))) {
					return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE,
							resources.getMessage("message.AccessDenied", null, "Access denied", locale));
				}
				exports.add(export);
			}
		}

		for (Export export : exports) {
			exportService.recreateExport(export, locale, resources);
		}

		return new ModelAndView("redirect:/exports/list");
	}
}
