package com.ec.survey.controller;

import com.ec.survey.exception.BadRequestException;
import com.ec.survey.exception.InternalServerErrorException;
import com.ec.survey.exception.ECFException;
import com.ec.survey.exception.httpexception.NotFoundException;
import com.ec.survey.exception.ForbiddenException;
import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.FrozenSurveyException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.exception.MessageException;
import com.ec.survey.exception.NoFormLoadedException;
import com.ec.survey.model.*;
import com.ec.survey.model.Export.ExportState;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.delphi.DelphiMedian;
import com.ec.survey.model.evote.SeatCounting;
import com.ec.survey.model.evote.eVoteListResult;
import com.ec.survey.model.evote.eVoteResults;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.ECService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.SurveyException;
import com.ec.survey.service.ReportingService.ToDo;
import com.ec.survey.service.mapping.PaginationMapper;
import com.ec.survey.tools.*;
import com.ec.survey.tools.activity.ActivityRegistry;
import com.ec.survey.tools.export.XlsxExportCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.Image;
import java.awt.*;
import java.beans.PropertyEditorSupport;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ec.survey.model.survey.ecf.ECFIndividualResult;
import com.ec.survey.model.survey.ecf.ECFProfileResult;
import com.ec.survey.model.survey.ecf.ECFGlobalResult;
import com.ec.survey.model.survey.ecf.ECFOrganizationalResult;
import com.ec.survey.model.survey.ecf.ECFSummaryResult;

@Controller
@RequestMapping("/{shortname}/management")
public class ManagementController extends BasicController {

	@Autowired
	protected PaginationMapper paginationMapper;

	public @Value("${opc.users}") String opcusers;
	public @Value("${opc.department:@null}") String opcdepartments;
	public @Value("${opc.template}") String opctemplatesurvey;
	public @Value("${ecf.template}") String ecfTemplateSurvey;
	public @Value("${codaCreateDashboardLink:#{null}}") String codaCreateDashboardLink;
	public @Value("${codaApiKey:#{null}}") String codaApiKey;
	public @Value("${evote.template-lux:#{null}}") String evoteLuxTemplate;
	public @Value("${evote.template-bru:#{null}}") String evoteBruTemplate;
	public @Value("${evote.template-ispra:#{null}}") String evoteIspraTemplate;
	public @Value("${evote.template-outside:#{null}}") String evoteOutsideTemplate;
	public @Value("${evote.template-president:#{null}}") String evotePresidenTemplate;
	
	private final String LastEVoteTestResult = "LastEVoteTestResult";
	
	@Resource(name = "ecService")
	private ECService ecService;

	private static final String[] KNOWN_RESULTTYPES = {
			"content", "charts", "statistics", "ecf", "ecf2", "ecf3", "statistics-delphi", "statistics-quiz"
	};

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(ConversionTools.DateFormat);
		CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
		binder.registerCustomEditor(Date.class, editor);

		binder.registerCustomEditor(Language.class, "survey.language", new PropertyEditorSupport() {

			@Override
			public void setAsText(String text) {
				Language lang = surveyService.getLanguage(text);
				setValue(lang);
			}
		});
	}

	@GetMapping(value = "/confirmation")
	public ModelAndView confirmation(HttpServletRequest request, HttpServletResponse response, Locale locale,
			Model modelMap) throws InvalidURLException {
		String code = request.getParameter("code");
		String lang = request.getParameter("surveylanguage");

		if (code == null || lang == null)
			throw new InvalidURLException();

		AnswerSet answerSet = answerService.get(code);

		if (answerSet == null)
			throw new InvalidURLException();

		Survey survey = surveyService.getSurvey(answerSet.getSurveyId(), lang);

		ModelAndView result = new ModelAndView("thanksloggedin", Constants.UNIQUECODE, code);

		if (!survey.isAnonymous() && answerSet.getResponderEmail() != null) {
			result.addObject("participantsemail", answerSet.getResponderEmail());
		}
	
		result.addObject("isthankspage", true);
		result.addObject("runnermode", true);
		result.addObject(new Form(resources, surveyService.getLanguage(lang),
				translationService.getActiveTranslationsForSurvey(survey.getId()), contextpath));
		result.addObject("text", survey.getConfirmationPage());
		if (answerSet.getSurvey().getConfirmationPageLink() != null && survey.getConfirmationPageLink()
				&& survey.getConfirmationLink() != null && survey.getConfirmationLink().length() > 0) {
	
			//only needed to compute FinalConfirmationLink
			Form form = new Form(resources, surveyService.getLanguage(locale.getLanguage().toUpperCase()),
					translationService.getActiveTranslationsForSurvey(answerSet.getSurvey().getId()), contextpath);
			form.setSurvey(survey);
			form.getAnswerSets().add(answerSet);
			
			result.addObject("redirect", form.getFinalConfirmationLink(lang, answerSet));
		}
		result.addObject("surveyprefix", survey.getId());

		return result;
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.HEAD })
	public String root(HttpServletRequest request) {
		return "redirect:/noform/management/overview";
	}

	@RequestMapping(value = "/repairxhtml", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView repairxhtml(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws Exception {

		Form form;
		form = sessionService.getForm(request, shortname, false, false);

		int repairedlabels = surveyService.repairXHTML(form.getSurvey().getId());

		String from = request.getParameter("from");
		if (from != null && from.equals("surveysearch")) {
			return new ModelAndView("redirect:/administration/surveysearch?repairedlabels=" + repairedlabels);
		}

		return new ModelAndView("redirect:/" + shortname + "/management/overview?repairedlabels=" + repairedlabels);
	}

	@RequestMapping(value = "/checkInternetConnection", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView checkInternetConnection(@PathVariable String shortname, HttpServletRequest request,
			Locale locale) {

		String message = "";

		try {

			sessionService.initializeProxy();

			URL url = new URL("http://www.google.de/images/srpr/logo4w.png");
			URLConnection connection = url.openConnection();

			if (connection.getContentLength() == -1) {
				message += "connection.getContentLength() == -1<br />";
			} else if (connection.getContent() instanceof InputStream) {
				InputStream is = (InputStream) connection.getContent();
				StringWriter writer = new StringWriter();
				IOUtils.copy(is, writer);
				message += writer.toString();
			} else {
				message += connection.getContent();
			}
		} catch (IOException e) {
			logger.error("Failed to open a connection:" + e.getLocalizedMessage(), e);
			message += "connection failed: " + e.getLocalizedMessage() + "<br />";
		}

		return new ModelAndView("error/info", Constants.MESSAGE, message);
	}

	public ModelAndView clearchanges(String shortname, HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		form = sessionService.getForm(request, shortname, false, false);
		User u = sessionService.getCurrentUser(request);
		if (!u.getId().equals(form.getSurvey().getOwner().getId())
				&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
				&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
			throw new ForbiddenURLException();
		}

		Survey newSurvey = surveyService.clearChanges(form.getSurvey().getShortname(), u.getId());

		activityService.log(ActivityRegistry.ID_PENDING_DISCARDED, "n/a", "n/a", u.getId(), newSurvey.getUniqueId());

		return overview(shortname, request, locale);
	}

	@RequestMapping(value = "/overview", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView overview(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws ForbiddenURLException, InvalidURLException, NotAgreedToTosException, WeakAuthenticationException,
			NotAgreedToPsException {
		User user = sessionService.getCurrentUser(request);

		Form form;
		Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);

		if (this.isReportingDatabaseEnabled()) {
			survey.setNumberOfAnswerSetsPublished(reportingService.getCount(false, survey.getUniqueId()));
		} else {
			survey.setNumberOfAnswerSetsPublished(
					surveyService.getNumberPublishedAnswersFromMaterializedView(survey.getUniqueId()));
		}

		survey.setNumberOfReports(surveyService.getAbuseReportsForSurvey(survey.getUniqueId()));

		form = new Form(resources);

		sessionService.upgradePrivileges(survey, user, request);
		form.setSurvey(survey);

		if (!form.getSurvey().getOwner().getId().equals(user.getId())
				&& user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
				&& user.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 1) {
			if (user.getLocalPrivileges().get(LocalPrivilege.AccessDraft) > 0) {
				return new ModelAndView("redirect:/" + form.getSurvey().getShortname() + "/management/test");
			}

			if (user.getLocalPrivileges().get(LocalPrivilege.AccessResults) > 0) {
				return new ModelAndView("redirect:/" + form.getSurvey().getShortname() + "/management/results");
			}

			if (user.getLocalPrivileges().get(LocalPrivilege.ManageInvitations) > 0) {
				return new ModelAndView("redirect:/" + form.getSurvey().getShortname() + "/management/participants");
			}
			
			if (user.getResultAccess() != null) {
				return new ModelAndView("redirect:/" + form.getSurvey().getShortname() + "/management/results");
			}

			ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
			String message = resources.getMessage("error.NoAccessToSurvey", null, "No Access", locale);
			model.addObject(Constants.MESSAGE, message);
			return model;
		}

		if (survey.getIsPublished()) {
			try {
				surveyService.getSurveyByShortname(shortname, false, user, request, false, true, true, false);
			} catch (InvalidURLException e) {
				// repair wrong "isPublished" flags in the database
				survey.setIsPublished(false);
			}
		}

		ModelAndView overviewPage = new ModelAndView("management/overview", "form", form);
		overviewPage.addObject("useUILanguage", true);
		overviewPage.addObject("isPublished", form.getSurvey().getIsPublished());

		List<Element> newElements = new ArrayList<>();
		List<Element> changedElements = new ArrayList<>();
		List<Element> deletedElements = new ArrayList<>();
		Map<Element, Integer> pendingChanges = surveyService.getPendingChanges(form.getSurvey());
		for (Entry<Element, Integer> entry : pendingChanges.entrySet()) {
			switch (entry.getValue()) {
			case 0:
				newElements.add(entry.getKey());
				break;
			case 1:
				changedElements.add(entry.getKey());
				break;
			case 2:
				deletedElements.add(entry.getKey());
				break;
			default:
				break;
			}
		}

		boolean pending = (pendingChanges.size() > 0);
		if (form.getSurvey().getHasPendingChanges() != pending) {
			form.getSurvey().setHasPendingChanges(pending);
			if (pending) {
				surveyService.makeDirty(form.getSurvey().getId());
			} else {
				surveyService.makeClean(form.getSurvey().getId());
			}
		}

		overviewPage.addObject("newElements", newElements);
		overviewPage.addObject("changedElements", changedElements);
		overviewPage.addObject("deletedElements", deletedElements);

		List<ParticipationGroup> group = participationService.getAll(survey.getUniqueId());
		if (group.size() > 0) {
			boolean active = group.get(0).getActive();
			overviewPage.addObject("active", active);
		}

		if (request.getParameter("repairedlabels") != null) {
			try {
				int repaired = Integer.parseInt(request.getParameter("repairedlabels"));
				overviewPage.addObject("repairedlabels", repaired);
			} catch (Exception e) {
				// ignore
			}
		}

		return overviewPage;
	}

	@PostMapping(value = "/overview")
	public ModelAndView overviewPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws Exception {
		String target = request.getParameter("target");

		if (target != null) {
			if (target.equals("publish")) {
				return publish(shortname, request, locale);
			} else if (target.equals("unpublish")) {
				return unpublish(shortname, request, locale);
			} else if (target.equals("activate")) {
				return activate(shortname, request, locale);
			} else if (target.equals("applyChanges")) {
				return applyChanges(shortname, request, locale);
			} else if (target.equals("clearchanges")) {
				return clearchanges(shortname, request, locale);
			}
		}

		return overview(shortname, request, locale);
	}

	@RequestMapping(value = "/exportSurvey/{answers}/{shortname}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView exportSurvey(@PathVariable String answers, @PathVariable String shortname,
			HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		Form form = sessionService.getForm(request, shortname, false, false);
		Survey survey = form.getSurvey();

		String delete = request.getParameter("delete");
		if (delete == null) {
			delete = Constants.FALSE;
		}
		User u = sessionService.getCurrentUser(request);
		
		sessionService.upgradePrivileges(survey, u, request);
		
		if (!u.getId().equals(survey.getOwner().getId())
				&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
				&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
				&& (u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 1
						|| answers != null && answers.equalsIgnoreCase("true"))) {
			throw new ForbiddenURLException();
		}

		boolean acceptMissingFiles = request.getParameter("acceptMissingFiles") != null
				&& request.getParameter("acceptMissingFiles").equalsIgnoreCase("true");
		boolean fromforms = request.getParameter("fromforms") != null
				&& request.getParameter("fromforms").equalsIgnoreCase("true");

		if (answers != null && answers.equalsIgnoreCase("true") && delete.equalsIgnoreCase("true")) {
			if (!acceptMissingFiles) {
				// check for missing files
				Map<String, String> missingFiles = fileService.getMissingFiles(survey.getUniqueId());
				if (missingFiles.size() > 0) {
					ModelAndView result = new ModelAndView("management/overview", "form", form);

					if (fromforms) {
						SurveyFilter filter = sessionService.getSurveyFilter(request, true);
						Paging<Survey> paging = new Paging<>();
						paging.setItemsPerPage(10);
						int numberOfSurveys = 0;
						paging.setNumberOfItems(numberOfSurveys);
						paging.moveTo("1");

						SqlPagination sqlPagination = paginationMapper.toSqlPagination(paging);
						List<Survey> surveys = surveyService.getSurveysIncludingTranslationLanguages(filter,
								sqlPagination, false, false);
						paging.setItems(surveys);

						result = new ModelAndView("forms/forms", "paging", paging);
						result.addObject(Constants.FILTER, filter);

						if (filter.getGeneratedFrom() != null || filter.getGeneratedTo() != null
								|| filter.getStartFrom() != null || filter.getStartTo() != null
								|| filter.getEndFrom() != null || filter.getEndTo() != null) {
							result.addObject("showDates", true);
						}
					}

					result.addObject("missingFilesSurvey", survey.getShortname());
					result.addObject("missingFiles", missingFiles);
					return result;
				}
			}

			if (!archiveSurvey(survey, u)) {
				throw new MessageException("archiving failed!");
			}

			request.getSession().removeAttribute("sessioninfo");

			return new ModelAndView("redirect:/dashboard?archived=" + shortname);
		}

		java.io.File zip = surveyService.exportSurvey(shortname, surveyService,
				answers != null && answers.equalsIgnoreCase("true"));

		response.setContentLength((int) zip.length());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + shortname + ".eus\"");
		try {
			FileCopyUtils.copy(new FileInputStream(zip), response.getOutputStream());
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	@PostMapping(value = "/importSurvey")
	public void importSurvey(HttpServletRequest request, HttpServletResponse response, Locale locale) {

		PrintWriter writer = null;
		InputStream is = null;
		FileOutputStream fos = null;
		String filename = null;

		try {
			User u = sessionService.getCurrentUser(request);

			if (u == null || u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 0) {
				throw new ForbiddenURLException();
			}

			String disabled = settingsService.get(Setting.CreateSurveysForExternalsDisabled);
			if (disabled.equalsIgnoreCase("true") && u.getType().equalsIgnoreCase(User.ECAS)
					&& u.getGlobalPrivileges().get(GlobalPrivilege.ECAccess) == 0) {
				throw new ForbiddenURLException();
			}

			writer = response.getWriter();

			if (request instanceof DefaultMultipartHttpServletRequest) {
				DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest) request;
				is = r.getFile("qqfile").getInputStream();
				filename = com.ec.survey.tools.FileUtils.cleanFilename(r.getFile("qqfile").getOriginalFilename());
			} else {
				is = request.getInputStream();
				filename = com.ec.survey.tools.FileUtils.cleanFilename(request.getHeader("X-File-Name"));
			}

			String uuid = UUID.randomUUID().toString().replace(Constants.PATH_DELIMITER, "");
			java.io.File file = null;
			ImportResult result = null;
			file = fileService.createTempFile("import" + uuid, null);
			fos = new FileOutputStream(file);
			IOUtils.copy(is, fos);
			fos.close();

			if (filename.toLowerCase().endsWith("zip")) {
				result = SurveyExportHelper.importIPMSurvey(file, surveyService, sessionService.getCurrentUser(request),
						fileService, servletContext, u.getEmail());
				request.getSession().setAttribute("IMPORTORIGIN", "IPM");
			} else {
				result = SurveyExportHelper.importSurvey(file, fileService, u.getEmail());
				request.getSession().setAttribute("IMPORTORIGIN", "EUSURVEY");
			}

			String contact = u.getEmail();
			String login = u.getLogin();
			String lang = result.getSurvey().getLanguage().getCode();

			if (result != null && result.getSurvey() != null) {
				Survey existing = surveyService.getSurvey(result.getSurvey().getShortname(), true, false, false, false,
						null, true, false);
				response.setStatus(HttpServletResponse.SC_OK);
				if (existing != null) {
					writer.print("{\"success\": true, \"exists\": true, \"uuid\": \"" + uuid + "\", \"shortname\": \""
							+ result.getSurvey().getShortname() + "\", \"title\": \""
							+ Tools.encodeForJSON(result.getSurvey().getTitle()) + "\", \"contact\": \"" + contact
							+ "\", \"login\": \"" + login + "\", \"language\": \"" + lang + "\"}");
				} else {
					writer.print("{\"success\": true, \"exists\": false, \"uuid\": \"" + uuid + "\", \"shortname\": \""
							+ result.getSurvey().getShortname() + "\", \"title\": \""
							+ Tools.encodeForJSON(result.getSurvey().getTitle()) + "\", \"contact\": \"" + contact
							+ "\", \"login\": \"" + login + "\", \"language\": \"" + lang + "\"}");
				}

				java.io.File target = fileService.getUsersFile(u.getId(), "import" + uuid);
				FileUtils.copyFile(file, target);
			} else {
				response.setStatus(HttpServletResponse.SC_OK);
				String message = resources.getMessage("message.FileCouldNotBeImported", null,
						"The file could not be imported.", locale);
				writer.print("{\"success\": false, \"exists\": false, \"message\": \"" + message + "\"}");
			}

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response.setStatus(HttpServletResponse.SC_OK);
			String message = resources.getMessage("message.FileCouldNotBeImported", null,
					"The file could not be imported.", locale);
			writer.print("{\"success\": false, \"exists\": false, \"message\": \"" + message + "\"}");
		} finally {
			try {
				fos.close();
				is.close();
			} catch (IOException ex2) {
				logger.error(ex2.getMessage(), ex2);
			}
		}

		writer.flush();
		writer.close();
	}

	public ModelAndView activate(String shortname, HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		form = sessionService.getForm(request, shortname, false, false);

		if (form.getSurvey().getIsFrozen()) {
			throw new FrozenSurveyException();
		}

		User u = sessionService.getCurrentUser(request);
		if (!u.getId().equals(form.getSurvey().getOwner().getId())
				&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
				&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
			throw new ForbiddenURLException();
		}

		Survey published = surveyService.getSurvey(form.getSurvey().getShortname(), false, false, false, false, null,
				true, false);

		if (published != null) {
			try {
				surveyService.applyChanges(form.getSurvey(), true, u.getId(), false);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		} else {
			surveyService.publish(form.getSurvey(), -1, -1, true, u.getId(), false, false);
		}

		try {
			surveyService.activate(form.getSurvey(), true, u.getId());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		activityService.log(ActivityRegistry.ID_SURVEY_STATE_MANUAL, "unpublished", "published", u.getId(), form.getSurvey().getUniqueId());

		return overview(shortname, request, locale);
	}

	public ModelAndView publish(String shortname, HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		form = sessionService.getForm(request, shortname, false, false);

		if (form.getSurvey().getIsFrozen()) {
			throw new FrozenSurveyException();
		}

		if (!form.getSurvey().getIsActive()) {
			User u = sessionService.getCurrentUser(request);
			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
				throw new ForbiddenURLException();
			}

			surveyService.activate(form.getSurvey(), true, u.getId());
			activityService.log(ActivityRegistry.ID_SURVEY_STATE_MANUAL, "unpublished", "published", u.getId(), form.getSurvey().getUniqueId());
		}
		ModelAndView result = overview(shortname, request, locale);

		result.addObject("published", true);

		return result;

	}

	public ModelAndView unpublish(String shortname, HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		form = sessionService.getForm(request, shortname, false, false);

		User u = sessionService.getCurrentUser(request);
		if (!u.getId().equals(form.getSurvey().getOwner().getId())
				&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
				&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
			throw new ForbiddenURLException();
		}

		surveyService.unpublish(form.getSurvey(), true, u.getId(), false);
		activityService.log(ActivityRegistry.ID_SURVEY_STATE_MANUAL, "published", "unpublished", u.getId(), form.getSurvey().getUniqueId());
		return overview(shortname, request, locale);
	}

	public ModelAndView applyChanges(String shortname, HttpServletRequest request, Locale locale) throws Exception {
		Form form;
		form = sessionService.getForm(request, shortname, false, false);

		try {

			User u = sessionService.getCurrentUser(request);
			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
				throw new ForbiddenURLException();
			}
			
			String oldValue = null;
			if (activityService.isEnabled(ActivityRegistry.ID_PENDING_APPLIED)) {
				Survey published = surveyService.getSurvey(form.getSurvey().getShortname(), false, false, false, false, null,
						true, false);
				
				if (published != null) {
					oldValue = published.serialize(false);
				}
			}
			
			surveyService.applyChanges(form.getSurvey(), false, u.getId(), false);

			if (activityService.isEnabled(ActivityRegistry.ID_PENDING_APPLIED)) {
				activityService.log(ActivityRegistry.ID_PENDING_APPLIED, oldValue, form.getSurvey().serialize(false), u.getId(),
						form.getSurvey().getUniqueId());
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
			String message = resources.getMessage("error.OperationFailed", null, "Operation failed.", locale);
			model.addObject(Constants.MESSAGE, message);
			return model;
		}
		return overview(shortname, request, locale);
	}

	@RequestMapping(value = "/properties", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView properties(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, InvalidURLException,
			ForbiddenURLException {
		Form form;
		User user = sessionService.getCurrentUser(request);

		Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);

		List<Language> completed = new ArrayList<>();
		List<Translations> translations = translationService.getTranslationsForSurvey(survey.getId(), false, true,
				false);
		for (Translations translation : translations) {
			if (translation.getComplete()) {
				completed.add(translation.getLanguage());
			}
		}
		survey.setCompleteTranslations(completed);

		form = new Form();
		form.setResources(resources);
		form.setSurvey(survey);

		User u = sessionService.getCurrentUser(request);
		if (!sessionService.userIsFormManager(form.getSurvey(), u, request)) {
			throw new ForbiddenURLException();
		}

		form.setUploadItem(new UploadItem());
		ModelAndView result = new ModelAndView("management/properties", "form", form);
		result.addObject("useUILanguage", true);
		form.getSurvey().setFileNamesForBackgroundDocuments(
				fileService.getFileNamesForBackgroundDocuments(form.getSurvey().getBackgroundDocuments()));

		String tab = request.getParameter("tab");
		try {
			if (tab != null) {
				result.addObject("tab", Integer.parseInt(tab));
			}
		} catch (Exception e) {
			// ignore
		}

		String editelem = request.getParameter("editelem");
		if (editelem != null && !editelem.equalsIgnoreCase("autopub") && !editelem.equalsIgnoreCase("showContent")
				&& !editelem.equalsIgnoreCase("edit-prop-tabs-7")) {
			// this is for security reasons to prevent xss attacks
			editelem = null;
		}
		if (editelem != null)
			result.addObject("editelem", editelem);

		result.addObject("serverprefix", serverPrefix);

		List<Skin> skins;
		if (isOss()) {
			skins = skinService.getAllButEC(user.getId());
		} else {
			if (user.getGlobalPrivileges().get(GlobalPrivilege.ECAccess) >= 1) {
				skins = skinService.getAll(user.getId());
			} else {
				skins = skinService.getAllButEC(user.getId());
			}
		}

		Skin newdefault = null;
		for (Skin skin : skins) {
			if (skin.getIsPublic() && skin.getName().equals("EUSurveyNew.css")) {
				newdefault = skin;
				break;
			}
		}

		if (newdefault != null) {
			skins.remove(newdefault);
			skins.add(0, newdefault);
		}

		result.addObject("skins", skins);

		if (form.getSurvey().getStart() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(form.getSurvey().getStart());
			form.setStartHour(cal.get(Calendar.HOUR_OF_DAY));
		}

		if (form.getSurvey().getEnd() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(form.getSurvey().getEnd());
			form.setEndHour(cal.get(Calendar.HOUR_OF_DAY));
		}

		// check if name and email questions exist
		boolean nameFound = false;
		boolean emailFound = false;
		for (Element element : form.getSurvey().getElements()) {
			if (element instanceof Question) {
				Question question = (Question) element;

				if (!question.getOptional()) {
					if (question.getAttributeName().equalsIgnoreCase("name"))
						nameFound = true;
					if (question.getAttributeName().equalsIgnoreCase(Constants.EMAIL))
						emailFound = true;
					if (question.getShortname().equalsIgnoreCase("name"))
						nameFound = true;
					if (question.getShortname().equalsIgnoreCase(Constants.EMAIL))
						emailFound = true;
					if (nameFound && emailFound)
						break;
				}
			}
		}

		result.addObject("validregform", nameFound && emailFound);

		if (form.getSurvey().getSecurity().contains("anonymous") && form.getSurvey().getIsPublished()
				&& answerService.getHasPublishedAnswers(form.getSurvey().getUniqueId())) {
			result.addObject("haspublishedanswers", "true");
		}

		return result;
	}

	@PostMapping(value = "/properties")
	public ModelAndView propertiesPost(@ModelAttribute Form form, BindingResult bindingresult,
			HttpServletRequest request, Locale locale) throws Exception {
		if (bindingresult.hasErrors()) {
			for (ObjectError error : bindingresult.getAllErrors()) {
				logger.error(error);
			}
		}

		return updateSurvey(form, request, false, locale);
	}

	@PostMapping(value = "createNewSurvey")
	public ModelAndView createNewSurvey(HttpServletRequest request, HttpServletResponse response, Locale locale)
			throws Exception {
		User u = sessionService.getCurrentUser(request);

		if (u == null || u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 0) {
			throw new ForbiddenURLException();
		}

		String disabled = settingsService.get(Setting.CreateSurveysForExternalsDisabled);
		if (disabled.equalsIgnoreCase("true") && u.getType().equalsIgnoreCase(User.ECAS)
				&& u.getGlobalPrivileges().get(GlobalPrivilege.ECAccess) == 0) {
			throw new ForbiddenURLException();
		}

		surveyService.checkSurveyCreationLimit(u.getId());		
		
		if (request.getParameter("uuid") != null && request.getParameter("uuid").length() > 0) {
			// Case 1: import survey
			Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
			String uuid = request.getParameter("uuid").replace(Constants.PATH_DELIMITER, "");

			try {
				java.io.File file = fileService.getUsersFile(u.getId(), "import" + uuid);

				ImportResult result;
				if (request.getSession().getAttribute("IMPORTORIGIN") != null
						&& request.getSession().getAttribute("IMPORTORIGIN").toString().equalsIgnoreCase("IPM")) {
					result = SurveyExportHelper.importIPMSurvey(file, surveyService,
							sessionService.getCurrentUser(request), fileService, servletContext, u.getEmail());
				} else {
					result = SurveyExportHelper.importSurvey(file, fileService, u.getEmail());
				}

				result.getSurvey().setShortname(Tools.escapeHTML(parameters.get(Constants.SHORTNAME)[0]));
				result.getSurvey().setTitle(Tools.filterHTML(parameters.get("title")[0]));
				Language objLang = surveyService.getLanguage(Tools.escapeHTML(parameters.get("surveylanguage")[0]));
				if (objLang == null) {
					objLang = new Language(parameters.get("surveylanguage")[0], parameters.get("surveylanguage")[0],
							parameters.get("surveylanguage")[0], false);
				}
				result.getSurvey().setLanguage(objLang);
				result.getSurvey().setListForm(request.getParameter("listform") != null
						&& request.getParameter("listform").equalsIgnoreCase("true"));
				result.getSurvey().setContact(Tools.escapeHTML(parameters.get("contact")[0]));
				result.getSurvey().setContactLabel(Tools.escapeHTML(parameters.get("contactlabel")[0]));
				result.getSurvey().setAudience(Tools.escapeHTML(parameters.get("audience")[0]));

				if (result.getActiveSurvey() != null) {
					result.getActiveSurvey().setShortname(Tools.escapeHTML(parameters.get(Constants.SHORTNAME)[0]));
					result.getActiveSurvey().setTitle(Tools.filterHTML(parameters.get("title")[0]));
					result.getActiveSurvey().setLanguage(objLang);
					result.getActiveSurvey().setListForm(request.getParameter("listform") != null
							&& request.getParameter("listform").equalsIgnoreCase("true"));
					result.getActiveSurvey().setContact(Tools.escapeHTML(parameters.get("contact")[0]));
					result.getActiveSurvey().setContactLabel(Tools.escapeHTML(parameters.get("contactlabel")[0]));
					result.getActiveSurvey().setAudience(Tools.escapeHTML(parameters.get("audience")[0]));
				}

				if (result != null && result.getSurvey() != null) {
					int id = surveyService.importSurvey(result, sessionService.getCurrentUser(request), true);
					activityService.log(ActivityRegistry.ID_SURVEY_IMPORTED, null, Integer.toString(id), u.getId(), result.getSurvey().getUniqueId());
					try {
						Files.delete(file.toPath());
					} catch (IOException e) {
						logger.error(e.getLocalizedMessage(), e);
					}
					if (request.getParameter("origin") != null
							&& request.getParameter("origin").startsWith(contextpath)) {
						return new ModelAndView(
								"redirect:" + request.getParameter("origin").substring(contextpath.length())
										+ "?imported=" + Tools.escapeHTML(parameters.get(Constants.SHORTNAME)[0])
										+ "&invalidCodeFound=" + result.isInvalidCodeFound());
					} else {
						return new ModelAndView(
								"redirect:/forms?imported=" + Tools.escapeHTML(parameters.get(Constants.SHORTNAME)[0])
										+ "&invalidCodeFound=" + result.isInvalidCodeFound());
					}

				} else {
					String message = resources.getMessage("message.FileCouldNotBeImported", null,
							"The file could not be imported.", locale);
					logger.error(message);
					ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
					model.addObject(Constants.MESSAGE, message);
					return model;
				}

			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				String message = resources.getMessage("message.FileCouldNotBeImported", null,
						"The file could not be imported.", locale);
				ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
				model.addObject(Constants.MESSAGE, message);

				// if the survey import failed after the survey was already created
				// we have to delete it here
				if (ex instanceof SurveyException) {
					surveyService.delete(((SurveyException) ex).getSurveyID(), true, false);
				}

				return model;
			}

		} else if (request.getParameter("original") != null && request.getParameter("original").length() > 0) {
			// Case 2: copy survey
			int surveyId = Integer.parseInt(request.getParameter("original"));
			Survey original = surveyService.getSurvey(surveyId, false, true);

			if (original != null) {
				if (!original.getOwner().getId().equals(u.getId())
						&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2) {
					Access access = surveyService.getAccess(original.getId(), u.getId());
					if (!(access == null || !access.hasAnyPrivileges())) {
						u.setLocalPrivileges(access.getLocalPrivileges());
					}

					// check access by ecas group
					if (u.getType().equalsIgnoreCase("ecas")) {
						List<String> groups = ldapService.getUserLDAPGroups(u.getLogin());
						for (String group : groups) {
							access = surveyService.getGroupAccess(original.getId(), group);
							if (access != null) {
								u.upgradePrivileges(access.getLocalPrivileges());
							}
						}
					}
				}

				if (!u.getId().equals(original.getOwner().getId())
						&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
						&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 1) {
					throw new ForbiddenURLException();
				}
				Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);

				String newShortName = Tools.escapeHTML(parameterMap.get(Constants.SHORTNAME)[0]);

				// check if shortname already exists
				Survey existingSurvey = surveyService.getSurvey(newShortName, true, false, false, false, null, true,
						false);
				if (existingSurvey != null && !existingSurvey.getIsDeleted()) {

					String message = resources.getMessage("message.ShortnameAlreadyExists", null,
							"A survey with this shortname already exists.", locale);
					return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, message);
				}

				Survey copy = original.copy(surveyService, sessionService.getCurrentUser(request), fileDir, false, -1,
						-1, false, false, false, newShortName, UUID.randomUUID().toString());

				if (copy.getIsEVote()) {
					//change all multiple choice eVote Lists to the right style
					String template = request.getParameter("evotetemplate");
					copy.seteVoteTemplate(template);
				}

				try {

					Map<String, String> convertedUIDs = surveyService.copyFiles(copy, new HashMap<>(), false, null, original.getUniqueId());

					Map<String, String> oldToNewUniqueIds = new HashMap<>();
					oldToNewUniqueIds.put("", ""); // leave blank for old surveys that have no uniqueIds

					// recreate unique ids
					for (Element elem : copy.getElementsRecursive(true)) {
						String newUniqueId = UUID.randomUUID().toString();
						if (!oldToNewUniqueIds.containsKey(elem.getUniqueId())) {
							oldToNewUniqueIds.put(elem.getUniqueId(), newUniqueId);
						}
						elem.setUniqueId(newUniqueId);
					}

					copy.setNumberOfAnswerSets(0);
					copy.setNumberOfAnswerSetsPublished(0);

					copy.setTitle(Tools.filterHTML(parameterMap.get("title")[0]));
					boolean newTitle = !copy.getTitle().equals(original.getTitle());

					Language objLang = surveyService.getLanguage(parameterMap.get("surveylanguage")[0]);
					if (objLang == null) {
						objLang = new Language(parameterMap.get("surveylanguage")[0],
								parameterMap.get("surveylanguage")[0], parameterMap.get("surveylanguage")[0], false);
					}

					copy.setLanguage(objLang);
					copy.setListForm(request.getParameter("listform") != null
							&& request.getParameter("listform").equalsIgnoreCase("true"));
					copy.setSecurity(Tools.escapeHTML(parameterMap.get("security")[0]));
					copy.setContact(Tools.escapeHTML(parameterMap.get("contact")[0]));
					copy.setContactLabel(Tools.escapeHTML(parameterMap.get("contactlabel")[0]));
					copy.setAudience(Tools.escapeHTML(parameterMap.get("audience")[0]));

					copy.setAutomaticPublishing(false);
					copy.setPreventGoingBack(false);
					copy.setStart(null);
					copy.setEnd(null);
					copy.setNotificationValue(null);

					copy.setIsQuiz(request.getParameter("quiz") != null
							&& request.getParameter("quiz").equalsIgnoreCase("true"));
					copy.setIsOPC(request.getParameter("opc") != null
							&& request.getParameter("opc").equalsIgnoreCase("true"));
					copy.setIsDelphi(request.getParameter("delphi") != null
							&& request.getParameter("delphi").equalsIgnoreCase("true"));
					copy.setIsECF(request.getParameter("ecf") != null
							&& request.getParameter("ecf").equalsIgnoreCase("true"));
					copy.setIsEVote(request.getParameter("evote") != null
							&& request.getParameter("evote").equalsIgnoreCase("true"));
					copy.setSaveAsDraft(!copy.getIsQuiz() && !copy.getIsDelphi() && !copy.getIsEVote());

					surveyService.update(copy, false, true, true, u.getId());

					List<Translations> translations = translationService.getTranslationsForSurvey(original.getId(),
							false, false);
					surveyService.copyTranslations(translations, copy, oldToNewUniqueIds, null, newTitle, convertedUIDs);

					Form form = new Form(resources);
					form.setSurvey(copy);
					form.setUploadItem(new UploadItem());
					sessionService.updateSessionInfo(copy, u, request);
					request.getSession().setAttribute("SURVEYCOPIED", form.getSurvey().getShortname());

					activityService.log(ActivityRegistry.ID_SURVEY_COPIED, original.getId().toString(), copy.getId().toString(), u.getId(),
							copy.getUniqueId());
					Map<String, Translations> translationToCreate = new HashMap<>();
					ensurePropertiesDependingOnSurveyType(copy, false, translationToCreate);
					return new ModelAndView("redirect:/" + form.getSurvey().getShortname() + "/management/edit");

				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
					surveyService.delete(copy);
				}
			}

			ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
			String message = resources.getMessage("message.ProblemDuringCopy", null, "There was a problem.", locale);
			model.addObject(Constants.MESSAGE, message);
			return model;

		} else {
			// Case 3: new (empty) survey
			return updateSurvey(new Form(resources), request, true, locale);
		}
	}

	/** checks if the type of the given survey is only one of the possibilities (normal, quiz, opc, delphi, evote)
	*/
	private boolean checkConclusiveSurveyType(Survey survey) {
		boolean isQuiz = survey.getIsQuiz();
		boolean isOPC = survey.getIsOPC();
		boolean isDelphi = survey.getIsDelphi();
		boolean isEVote = survey.getIsEVote();
		boolean isNormal = !(isQuiz || isOPC || isDelphi || isEVote);
		if ( isNormal && !isQuiz && !isOPC && !isDelphi && !isEVote) return true;
		if (!isNormal &&  isQuiz && !isOPC && !isDelphi && !isEVote) return true;
		if (!isNormal && !isQuiz &&  isOPC && !isDelphi && !isEVote) return true;
		if (!isNormal && !isQuiz && !isOPC &&  isDelphi && !isEVote) return true;
		if (!isNormal && !isQuiz && !isOPC &&  !isDelphi && isEVote) return true;
		return false;
	}

	/** when a particular Survey type has some conditions on properties, they are set here
	 * @throws ValidationException 
	*/
	private void ensurePropertiesDependingOnSurveyType(Survey survey, boolean creation, Map<String, Translations> translationsToCreate) throws ValidationException, IOException {
		if (survey.getIsDelphi()) {
			survey.setChangeContribution(true); // should always be activated for delphi surveys
			survey.setSaveAsDraft(false); // should always be deactivated for delphi surveys
			survey.setDownloadContribution(false); // should always be deactivated for delphi surveys
		}
		
		if (survey.getIsEVote()) {
			survey.setChangeContribution(false);
			survey.setIsUseMaxNumberContribution(false);
			survey.setSaveAsDraft(false);
			survey.setDownloadContribution(false);
			survey.setSecurity("securedanonymous");
			survey.setAllowedContributionsPerUser(1);
			survey.setDedicatedResultPrivileges(false);
			survey.setProgressBar(false);
			survey.setEcasSecurity(true);
			survey.setEcasMode("all");
			survey.setPassword("");
			
			if (creation) {
				String templateUid;
				switch (survey.geteVoteTemplate()){
					case "b":
						templateUid = evoteBruTemplate;
						break;
					case "i":
						templateUid = evoteIspraTemplate;
						break;
					case "l":
						templateUid = evoteLuxTemplate;
						break;
					case "o":
						templateUid = evoteOutsideTemplate;
						break;
					case "p":
						templateUid = evotePresidenTemplate;
						break;
					default:
						templateUid = null;
						break;
				}

				if (templateUid != null && templateUid.length() > 0){
					Survey templateSurvey = applyTemplate(survey, templateUid, translationsToCreate);

					survey.setQuorum(templateSurvey.getQuorum());
					survey.setMaxPrefVotes(templateSurvey.getMaxPrefVotes());
					survey.setSeatsToAllocate(templateSurvey.getSeatsToAllocate());
					survey.setMinListPercent(templateSurvey.getMinListPercent());
					survey.setShowResultsTestPage(templateSurvey.getShowResultsTestPage());
					survey.setConfirmationPage(templateSurvey.getConfirmationPage());
				}
			}
		}
		
		if (survey.getIsOPC()) {
			survey.setSecurity("secured");
			survey.setEcasSecurity(true);
			survey.setEcasMode("all");
			survey.setCaptcha(false);
			survey.getPublication().setShowContent(false);
			survey.getPublication().setShowUploadedDocuments(false);
			survey.getPublication().setShowStatistics(false);
			survey.getPublication().setShowCharts(false);
					
			if (survey.getSkin() == null || !survey.getSkin().getName().equalsIgnoreCase("New Official EC Skin")) {
				Skin ecSkin = skinService.getSkin("New Official EC Skin");
				survey.setSkin(ecSkin);
			}

			if (creation) {
				survey.setWcagCompliance(true);
				survey.setShowPDFOnUnavailabilityPage(true);
				if (opctemplatesurvey != null && opctemplatesurvey.length() > 0) {
					applyTemplate(survey, opctemplatesurvey, translationsToCreate);
				}
			}
		}
	}

	private Survey applyTemplate(Survey survey, String templateUid, Map<String, Translations> translationsToCreate) throws ValidationException, IOException{
		Survey template = surveyService.getSurveyByUniqueId(templateUid, false, true);
		template.copyElements(survey, surveyService, !survey.getIsEVote());

		List<Translations> translations = translationService.getActiveTranslationsForSurvey(template.getId());
		Map<String, Map<String, Translation>> oldTranslations = new HashMap<>();

		// copy published translations
		for (Translations trans : translations) {
			if (trans.getComplete() && !trans.getLanguage().getCode().equals(survey.getLanguage().getCode())) {
				oldTranslations.put(trans.getLanguage().getCode(), trans.getTranslationsByKey());
				Translations trans2 = new Translations();
				trans2.setLanguage(trans.getLanguage());
				translationsToCreate.put(trans2.getLanguage().getCode(), trans2);

				String confirmation = resources.getMessage("message.confirmationWithTitle", null, Survey.CONFIRMATIONTEXT,
						new Locale(trans.getLanguage().getCode()));
				trans2.getTranslations().add(new Translation(Survey.CONFIRMATIONPAGE, confirmation,
						trans.getLanguage().getCode(), null, trans2));

				String escape = resources.getMessage("message.escape", null, Survey.ESCAPETEXT,
						new Locale(trans.getLanguage().getCode()));
				trans2.getTranslations().add(new Translation(Survey.ESCAPEPAGE, escape, trans.getLanguage().getCode(),
						null, trans2));
			}
		}

		//Create and set ids and uids
		surveyService.add(survey, false, survey.getOwner().getId());

		//Copy files eg for Gallery Question
		surveyService.copyFiles(survey, new HashMap<>(), true, new HashMap<String, String>(), templateUid);

		// recreate unique ids
		for (Element elem : survey.getElementsRecursive(true)) {
			String newUniqueId = UUID.randomUUID().toString();
			String oldUniqueId = elem.getUniqueId();

			elem.setUniqueId(newUniqueId);

			Map<String, String> possibleKeys = new HashMap<>();
			possibleKeys.put(oldUniqueId, newUniqueId);
			possibleKeys.put(oldUniqueId + "help", newUniqueId + "help");
			possibleKeys.put(oldUniqueId + Question.FEEDBACK, newUniqueId + Question.FEEDBACK);
			possibleKeys.put(oldUniqueId + Section.TABTITLE, newUniqueId + Section.TABTITLE);
			possibleKeys.put(oldUniqueId + NumberQuestion.UNIT, newUniqueId + NumberQuestion.UNIT);
			possibleKeys.put(oldUniqueId + NumberQuestion.MINLABEL, newUniqueId + NumberQuestion.MINLABEL);
			possibleKeys.put(oldUniqueId + NumberQuestion.MAXLABEL, newUniqueId + NumberQuestion.MAXLABEL);
			possibleKeys.put(oldUniqueId + Confirmation.TEXT, newUniqueId + Confirmation.TEXT);
			possibleKeys.put(oldUniqueId + Confirmation.LABEL, newUniqueId + Confirmation.LABEL);
			possibleKeys.put(oldUniqueId + MatrixOrTable.FIRSTCELL, newUniqueId + MatrixOrTable.FIRSTCELL);
			possibleKeys.put(oldUniqueId + GalleryQuestion.TEXT, newUniqueId + GalleryQuestion.TEXT);
			possibleKeys.put(oldUniqueId + GalleryQuestion.TITLE, newUniqueId + GalleryQuestion.TITLE);
			possibleKeys.put(oldUniqueId + "RESULTTEXT", newUniqueId + "RESULTTEXT");

			for (String languageCode : oldTranslations.keySet()) {
				Map<String, Translation> translationsByKey = oldTranslations.get(languageCode);
				Translations trans2 = translationsToCreate.get(languageCode);

				for (String possibleOldKey : possibleKeys.keySet()) {
					if (translationsByKey.containsKey(possibleOldKey)) {
						Translation value = translationsByKey.get(possibleOldKey);
						Translation value2 = new Translation();
						value2.setKey(possibleKeys.get(possibleOldKey));
						value2.setLabel(value.getLabel());
						value2.setLanguage(value.getLanguage());
						value2.setTranslations(trans2);
						trans2.getTranslations().add(value2);
					}
				}
			}
		}
		return template;
	}

	private ModelAndView updateSurvey(Form form, HttpServletRequest request, boolean creation, Locale locale)
			throws Exception {
		Survey uploadedSurvey = new Survey();
		Survey survey = new Survey();

		boolean hasPendingChanges = false;
		boolean shortnameChanged = false;

		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
		Map<Integer, String[]> activitiesToLog = new HashMap<>();
		Map<String, String> oldBackgroundDocuments = new HashMap<>();

		List<String> keyTranslationsToAdd = new LinkedList<>();

		File oldlogo = null;

		if (creation) {
			uploadedSurvey.setShortname(Tools.escapeHTML(parameterMap.get(Constants.SHORTNAME)[0]));
			uploadedSurvey.setTitle(Tools.filterHTML(parameterMap.get("title")[0]));

			Language objLang = surveyService.getLanguage(parameterMap.get("surveylanguage")[0]);
			if (objLang == null) {
				objLang = new Language(parameterMap.get("surveylanguage")[0], parameterMap.get("surveylanguage")[0],
						parameterMap.get("surveylanguage")[0], false);
			}

			uploadedSurvey.setLanguage(objLang);
			uploadedSurvey.setListForm(request.getParameter("listform") != null
					&& request.getParameter("listform").equalsIgnoreCase("true"));
			uploadedSurvey.setSecurity(Tools.escapeHTML(parameterMap.get("security")[0]));
			uploadedSurvey.setContact(Tools.escapeHTML(parameterMap.get("contact")[0]));
			uploadedSurvey.setContactLabel(Tools.escapeHTML(parameterMap.get("contactlabel")[0]));
			uploadedSurvey.setAudience(Tools.escapeHTML(parameterMap.get("audience")[0]));
			uploadedSurvey.setIsQuiz(
					request.getParameter("quiz") != null && request.getParameter("quiz").equalsIgnoreCase("true"));
			uploadedSurvey.setIsOPC(
					request.getParameter("opc") != null && request.getParameter("opc").equalsIgnoreCase("true"));
			uploadedSurvey.setIsDelphi(request.getParameter("delphi") != null
					&& request.getParameter("delphi").equalsIgnoreCase("true"));
			uploadedSurvey.setIsECF(
					request.getParameter("ecf") != null && request.getParameter("ecf").equalsIgnoreCase("true"));
			uploadedSurvey.setIsEVote(
					request.getParameter("evote") != null && request.getParameter("evote").equalsIgnoreCase("true"));
			
			if (uploadedSurvey.getIsEVote()) {
				uploadedSurvey.seteVoteTemplate(request.getParameter("evotetemplate"));
			}
			
			uploadedSurvey.setSaveAsDraft(!uploadedSurvey.getIsQuiz());

			if (uploadedSurvey.getTitle() != null
					&& !XHTMLValidator.validate(uploadedSurvey.getTitle(), servletContext, null)) {
				throw new InvalidXHTMLException(uploadedSurvey.getTitle(), uploadedSurvey.getTitle());
			}

			// check for mutual exclusion of types quiz, opc, delphi, evote (or normal)
			if (!checkConclusiveSurveyType(uploadedSurvey)) {
				throw new MessageException("multiple selected survey types at once");
			}

			// check if shortname already exists
			Survey existingSurvey = surveyService.getSurvey(uploadedSurvey.getShortname(), true, false, false, false,
					null, true, false);
			if (existingSurvey != null && !existingSurvey.getIsDeleted()) {

				String message = resources.getMessage("message.ShortnameAlreadyExists", null,
						"A survey with this shortname already exists.", locale);
				return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, message);
			}

		} else {
			uploadedSurvey = form.getSurvey();
			survey = surveyService.getSurvey(form.getSurvey().getId(), false, false);
			hasPendingChanges = survey.getHasPendingChanges();

			oldlogo = survey.getLogo();

			User u = sessionService.getCurrentUser(request);

			if (!survey.getOwner().getId().equals(u.getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2) {
				Access access = surveyService.getAccess(survey.getId(), u.getId());
				if (!(access == null || !access.hasAnyPrivileges())) {
					u.setLocalPrivileges(access.getLocalPrivileges());
				}

				// check access by ecas group
				if (u.getType().equalsIgnoreCase("ecas")) {
					List<String> groups = ldapService.getUserLDAPGroups(u.getLogin());
					for (String group : groups) {
						access = surveyService.getGroupAccess(survey.getId(), group);
						if (access != null) {
							u.upgradePrivileges(access.getLocalPrivileges());
						}
					}
				}
			}

			if (!u.getId().equals(survey.getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
				throw new ForbiddenURLException();
			}

			// check if shortname already exists
			Survey existingSurvey = surveyService.getSurvey(uploadedSurvey.getShortname(), true, false, false, false,
					null, true, false);
			if (existingSurvey != null && !existingSurvey.getId().equals(uploadedSurvey.getId()) && !existingSurvey.getIsDeleted()) {

				String message = resources.getMessage("message.ShortnameAlreadyExists", null,
						"A survey with this shortname already exists.", locale);
				return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, message);
			}

			if (!uploadedSurvey.getShortname().equals(survey.getShortname())) {
				String[] oldnew = { survey.getShortname(), uploadedSurvey.getShortname() };
				activitiesToLog.put(ActivityRegistry.ID_ALIAS, oldnew);
				shortnameChanged = true;
			}
		}

		if (uploadedSurvey.getShortname() == null || uploadedSurvey.getShortname().trim().length() == 0) {
			String message = resources.getMessage("message.SpecifyShortname", null, "Please specify a short name.",
					locale);
			return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, message);
		}

		if (invalid(uploadedSurvey.getShortname())) {
			String message = resources.getMessage("validation.name2", null,
					"Alias must be composed of lowercase and uppercase letters (a-z and A-Z), numbers (0-9), hyphens and underscores only.",
					locale);
			return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, message);
		}

		if (uploadedSurvey.getShortname().contains("__")) {
			String message = resources.getMessage("validation.shortname2", null,
					"Please do not use mora than one hyphen in a row.", locale);
			return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, message);
		}

		if (uploadedSurvey.getTitle() == null || uploadedSurvey.getTitle().trim().length() == 0) {
			String message = resources.getMessage("validation.notitle", null, "Please specify a title.", locale);
			return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, message);
		}

		if (uploadedSurvey.getTitle() != null
				&& !XHTMLValidator.validate(uploadedSurvey.getTitle(), servletContext, null)) {
			throw new InvalidXHTMLException(uploadedSurvey.getTitle(), uploadedSurvey.getTitle());
		}

		if (uploadedSurvey.getContact() == null || uploadedSurvey.getContact().trim().length() == 0) {
			String message = resources.getMessage("validation.nocontact", null, "Please specify a contact.", locale);
			return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, message);
		}

		if (uploadedSurvey.getLanguage() == null) {
			uploadedSurvey.setLanguage(surveyService.getLanguage(request.getParameter("survey.language")));
		}

		if (uploadedSurvey.getLanguage() == null || uploadedSurvey.getLanguage().getCode().trim().length() == 0) {
			String message = resources.getMessage("validation.nolanguage", null, "Please specify a language.", locale);
			return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, message);
		}

		if (uploadedSurvey.getRegistrationForm()) {
			boolean nameFound = false;
			boolean emailFound = false;

			// check if name and email questions exist
			for (Element element : survey.getElements()) {
				if (element instanceof Question) {
					Question question = (Question) element;

					if (question.getIsAttribute() && !question.getOptional()) {
						if (question.getAttributeName().equalsIgnoreCase("name"))
							nameFound = true;
						if (question.getAttributeName().equalsIgnoreCase(Constants.EMAIL))
							emailFound = true;
						if (nameFound && emailFound)
							break;
					}

				}
			}
			if (!nameFound || !emailFound) {
				for (Element element : survey.getElements()) {
					if (element instanceof Question) {
						Question question = (Question) element;

						if (!question.getOptional()) {
							if (!nameFound && question.getShortname().equalsIgnoreCase("name")) {
								nameFound = true;
								question.setAttributeName("name");
								question.setIsAttribute(true);
							}
							if (!emailFound && question.getShortname().equalsIgnoreCase(Constants.EMAIL)) {
								emailFound = true;
								question.setAttributeName(Constants.EMAIL);
								question.setIsAttribute(true);
							}
							if (nameFound && emailFound)
								break;
						}
					}
				}
			}

			if (!emailFound) {
				EmailQuestion email = new EmailQuestion(Constants.EMAIL, Constants.EMAIL, UUID.randomUUID().toString());
				email.setOptional(false);
				email.setPosition(0);
				email.setHelp("");
				email.setIsAttribute(true);

				for (Element element : survey.getElements()) {
					element.setPosition(element.getPosition() + 1);
				}

				survey.getElements().add(email);
			}

			if (!nameFound) {
				FreeTextQuestion name = new FreeTextQuestion("Name", "name", UUID.randomUUID().toString());
				name.setOptional(false);
				name.setPosition(0);
				name.setHelp("");
				name.setMinCharacters(0);
				name.setMaxCharacters(0);
				name.setNumRows(1);
				name.setIsAttribute(true);
				name.setIsPassword(false);
				name.setIsUnique(false);
				name.setIsComparable(false);

				for (Element element : survey.getElements()) {
					element.setPosition(element.getPosition() + 1);
				}

				survey.getElements().add(name);
			}
		}

		survey.setIsDraft(true);
		String originalShortname = survey.getShortname();
		survey.setShortname(uploadedSurvey.getShortname());

		boolean languageChanged = survey.getLanguage() != null
				&& !survey.getLanguage().getCode().equalsIgnoreCase(uploadedSurvey.getLanguage().getCode());

		if (languageChanged) {
			String[] oldnew = { survey.getLanguage().getCode(), uploadedSurvey.getLanguage().getCode() };
			activitiesToLog.put(ActivityRegistry.ID_MAIN_LANGUAGE, oldnew);
		}

		if (!Tools.isEqual(survey.getTitle(), uploadedSurvey.getTitle())) {
			hasPendingChanges = true;

			if (!creation) {
				String[] oldnew = { survey.getTitle(), uploadedSurvey.getTitle() };
				activitiesToLog.put(ActivityRegistry.ID_SURVEY_TITLE, oldnew);
			}
		}

		if (survey.getLanguage() == null || uploadedSurvey.getLanguage() == null
				|| !Tools.isEqual(survey.getLanguage().getCode(), uploadedSurvey.getLanguage().getCode()))
			hasPendingChanges = true;
		if (!Tools.isEqual(survey.getNiceContact(), uploadedSurvey.getNiceContact())) {
			hasPendingChanges = true;

			if (!creation) {
				String[] oldnew = { survey.getNiceContact(), uploadedSurvey.getNiceContact() };
				activitiesToLog.put(ActivityRegistry.ID_CONTACT_INFO, oldnew);
			}
		}

		if (!hasPendingChanges) {
			hasPendingChanges = PropertiesHelper.checkForPendingChanges(survey, uploadedSurvey,
					Survey::getMultiPaging,
					Survey::getProgressBar,
					Survey::getMotivationPopup,
					Survey::getMotivationText,
					Survey::getMotivationPopupTitle,
					Survey::getMotivationTriggerProgress,
					Survey::getMotivationTriggerTime,
					Survey::getMotivationType,
					Survey::getProgressDisplay,
					Survey::getValidatedPerPage,
					Survey::getPreventGoingBack,
					Survey::getWcagCompliance,
					Survey::getSectionNumbering,
					Survey::getRegistrationForm,
					Survey::getQuestionNumbering,
					Survey::getIsQuiz,
					Survey::getIsOPC,
					Survey::getIsDelphi,
					Survey::getIsEVote,
					Survey::getQuorum,
					Survey::getMinListPercent,
					Survey::getMaxPrefVotes,
					Survey::getSeatsToAllocate,
					Survey::getShowResultsTestPage,
					Survey::getShowTotalScore,
					Survey::getShowQuizIcons,
					Survey::getIsUseMaxNumberContribution,
					Survey::getIsUseMaxNumberContributionLink,
					Survey::getMaxNumberContributionText,
					Survey::getMaxNumberContributionLink,
					Survey::getMaxNumberContribution,
					Survey::getTimeLimit,
					Survey::getShowCountdown,
					Survey::getScoresByQuestion
			);
		}

		if (!Tools.isFileEqual(survey.getLogo(), uploadedSurvey.getLogo()))
			hasPendingChanges = true;

		if (!Tools.isEqual(survey.getEscapePage(), uploadedSurvey.getEscapePage())){
			hasPendingChanges = true;
			keyTranslationsToAdd.add(Survey.ESCAPEPAGE);
		}
		if (!Tools.isEqual(survey.getEscapeLink(), uploadedSurvey.getEscapeLink())){
			hasPendingChanges = true;
			keyTranslationsToAdd.add(Survey.ESCAPELINK);
		}
		if (!Tools.isEqual(survey.getConfirmationPage(), uploadedSurvey.getConfirmationPage())){
			hasPendingChanges = true;
			keyTranslationsToAdd.add(Survey.CONFIRMATIONPAGE);
		}
		if (!Tools.isEqual(survey.getConfirmationLink(), uploadedSurvey.getConfirmationLink())){
			hasPendingChanges = true;
			keyTranslationsToAdd.add(Survey.CONFIRMATIONLINK);
		}
		if (!Tools.isEqualIgnoreEmptyString(survey.getQuizWelcomeMessage(), uploadedSurvey.getQuizWelcomeMessage()))
			hasPendingChanges = true;
		if (!Tools.isEqualIgnoreEmptyString(survey.getQuizResultsMessage(), uploadedSurvey.getQuizResultsMessage()))
			hasPendingChanges = true;

		if (!uploadedSurvey.getShowTotalScore()) {
			uploadedSurvey.setScoresByQuestion(false);
		}

		survey.setLanguage(uploadedSurvey.getLanguage());
		survey.setTitle(Tools.filterHTML(uploadedSurvey.getTitle()));

		boolean sendListFormMail = false;

		if (uploadedSurvey.getListForm() != survey.getListForm()) {
			String[] oldnew = { survey.getListForm() ? "enabled" : "disabled",
					uploadedSurvey.getListForm() ? "enabled" : "disabled" };
			activitiesToLog.put(ActivityRegistry.ID_PRIVACY_SETTINGS, oldnew);

			survey.setListFormValidated(false);
			if (uploadedSurvey.getListForm()) {
				survey.setPublicationRequestedDate(new Date());
				sendListFormMail = true;
			}
		}

		survey.setListForm(uploadedSurvey.getListForm());
		survey.setContact(Tools.escapeHTML(uploadedSurvey.getContact()));
		survey.setContactLabel(Tools.escapeHTML(uploadedSurvey.getContactLabel()));
		survey.setIsQuiz(uploadedSurvey.getIsQuiz());
		survey.setIsOPC(uploadedSurvey.getIsOPC());
		survey.setIsDelphi(uploadedSurvey.getIsDelphi());
		survey.setIsECF(uploadedSurvey.getIsECF());
		survey.setIsEVote(uploadedSurvey.getIsEVote());
		survey.seteVoteTemplate(uploadedSurvey.geteVoteTemplate());

		survey.setSaveAsDraft(uploadedSurvey.getSaveAsDraft());
		survey.setShowQuizIcons(uploadedSurvey.getShowQuizIcons());
		survey.setShowTotalScore(uploadedSurvey.getShowTotalScore());
		survey.setScoresByQuestion(uploadedSurvey.getScoresByQuestion());
		survey.setTimeLimit(uploadedSurvey.getTimeLimit());
		survey.setShowCountdown(uploadedSurvey.getShowCountdown());

		survey.setIsDelphiShowAnswersAndStatisticsInstantly(uploadedSurvey.getIsDelphiShowAnswersAndStatisticsInstantly());
		survey.setIsDelphiShowStartPage(uploadedSurvey.getIsDelphiShowStartPage());
		survey.setIsDelphiShowAnswers(uploadedSurvey.getIsDelphiShowAnswers());
		survey.setMinNumberDelphiStatistics(uploadedSurvey.getMinNumberDelphiStatistics());
		if (survey.getIsECF() && creation) {
			if (ecfTemplateSurvey != null && ecfTemplateSurvey.length() > 0) {
				Survey template = surveyService.getSurveyByAlias(ecfTemplateSurvey, true);
				template.copyElements(survey, surveyService, true);

				// recreate unique ids
				for (Element elem : survey.getElementsRecursive(true)) {
					String newUniqueId = UUID.randomUUID().toString();
					elem.setUniqueId(newUniqueId);
				}

				// recreate the ecf elements
				survey = this.ecfService.copySurveyECFElements(survey);
			}
		}
		
		if (!creation) {
			if (!uploadedSurvey.getSecurity().equals(survey.getSecurity())) {
				if (survey.getSecurity().startsWith("open") && !uploadedSurvey.getSecurity().startsWith("open")) {
					String[] oldnew = { "open", "secured" };
					activitiesToLog.put(ActivityRegistry.ID_SECURITY_SETTINGS, oldnew);
				} else if (!survey.getSecurity().startsWith("open")
						&& uploadedSurvey.getSecurity().startsWith("open")) {
					String[] oldnew = { "secured", "open" };
					activitiesToLog.put(ActivityRegistry.ID_SECURITY_SETTINGS, oldnew);
				}

				if (survey.getSecurity().endsWith("anonymous") && !uploadedSurvey.getSecurity().endsWith("anonymous")) {
					String[] oldnew = { "enabled", "disabled" };
					activitiesToLog.put(ActivityRegistry.ID_ANONYMITY_SETTINGS, oldnew);
				} else if (!survey.getSecurity().endsWith("anonymous")
						&& uploadedSurvey.getSecurity().endsWith("anonymous")) {
					String[] oldnew = { "disabled", "enabled" };
					activitiesToLog.put(ActivityRegistry.ID_ANONYMITY_SETTINGS, oldnew);
				}
			}

			if (uploadedSurvey.getMultiPaging() != survey.getMultiPaging()) {
				String[] oldnew = { survey.getMultiPaging() ? "enabled" : "disabled",
						uploadedSurvey.getMultiPaging() ? "enabled" : "disabled" };
				activitiesToLog.put(ActivityRegistry.ID_MULTI_PAGING_SETTINGS, oldnew);
			}
			
			if (uploadedSurvey.getProgressBar() != survey.getProgressBar()) {
				String[] oldnew = { survey.getProgressBar() ? "enabled" : "disabled",
						uploadedSurvey.getProgressBar() ? "enabled" : "disabled" };
				activitiesToLog.put(ActivityRegistry.ID_PROGRESS_BAR, oldnew);
			}

			if(uploadedSurvey.getMotivationPopup() != survey.getMotivationPopup()) {
				String[] oldnew = { survey.getMotivationPopup() ? "enabled" : "disabled",
						uploadedSurvey.getMotivationPopup() ? "enabled" : "disabled" };
				activitiesToLog.put(ActivityRegistry.ID_MOTIVATION_POPUP, oldnew);
			}

			if(!Objects.equals(uploadedSurvey.getQuorum(), survey.getQuorum())) {
				String[] oldnew = { survey.getQuorum() + "",
						uploadedSurvey.getQuorum() + ""};
				activitiesToLog.put(ActivityRegistry.ID_EVOTE_QUORUM, oldnew);
			}
			if(!Objects.equals(uploadedSurvey.getMinListPercent(), survey.getMinListPercent())) {
				String[] oldnew = { survey.getMinListPercent() + "",
						uploadedSurvey.getMinListPercent() + ""};
				activitiesToLog.put(ActivityRegistry.ID_EVOTE_LIST_PORTION, oldnew);
			}
			if(!Objects.equals(uploadedSurvey.getMaxPrefVotes(), survey.getMaxPrefVotes())) {
				String[] oldnew = { survey.getMaxPrefVotes() + "",
						uploadedSurvey.getMaxPrefVotes() + ""};
				activitiesToLog.put(ActivityRegistry.ID_EVOTE_NUM_PREFERENTIAL_VOTES, oldnew);
			}
			if(!Objects.equals(uploadedSurvey.getSeatsToAllocate(), survey.getSeatsToAllocate())) {
				String[] oldnew = { survey.getSeatsToAllocate() + "",
						uploadedSurvey.getSeatsToAllocate() + ""};
				activitiesToLog.put(ActivityRegistry.ID_EVOTE_SEATS, oldnew);
			}

			if(uploadedSurvey.getShowResultsTestPage() != survey.getShowResultsTestPage()) {
				String[] oldnew = { survey.getShowResultsTestPage() ? "enabled" : "disabled",
						uploadedSurvey.getShowResultsTestPage() ? "enabled" : "disabled" };
				activitiesToLog.put(ActivityRegistry.ID_EVOTE_TEST_PAGE, oldnew);
			}
		}

		survey.setQuorum(uploadedSurvey.getQuorum());
		survey.setMinListPercent(uploadedSurvey.getMinListPercent());
		survey.setMaxPrefVotes(uploadedSurvey.getMaxPrefVotes());
		survey.setSeatsToAllocate(uploadedSurvey.getSeatsToAllocate());
		survey.setShowResultsTestPage(uploadedSurvey.getShowResultsTestPage());

		if (!survey.getIsOPC()) {
			survey.setSecurity(uploadedSurvey.getSecurity());
		}
		
		survey.setMultiPaging(uploadedSurvey.getMultiPaging());
		survey.setProgressBar(uploadedSurvey.getProgressBar());
		survey.setProgressDisplay(uploadedSurvey.getProgressDisplay());

		survey.setMotivationPopup(uploadedSurvey.getMotivationPopup());
		survey.setMotivationText(uploadedSurvey.getMotivationText());
		survey.setMotivationPopupTitle(uploadedSurvey.getMotivationPopupTitle());
		survey.setMotivationTriggerProgress(uploadedSurvey.getMotivationTriggerProgress());
		survey.setMotivationTriggerTime(uploadedSurvey.getMotivationTriggerTime());
		survey.setMotivationType(uploadedSurvey.getMotivationType());

		survey.setConfirmationPageLink(uploadedSurvey.getConfirmationPageLink());
		survey.setEscapePageLink(uploadedSurvey.getEscapePageLink());
		survey.setConfirmationLink(uploadedSurvey.getConfirmationLink());
		survey.setEscapeLink(uploadedSurvey.getEscapeLink());
		survey.setAudience(uploadedSurvey.getAudience());

		survey.setQuizWelcomeMessage(Tools.filterHTML(uploadedSurvey.getQuizWelcomeMessage()));
		survey.setQuizResultsMessage(Tools.filterHTML(uploadedSurvey.getQuizResultsMessage()));

		survey.setShowPDFOnUnavailabilityPage(uploadedSurvey.getShowPDFOnUnavailabilityPage());
		survey.setShowDocsOnUnavailabilityPage(uploadedSurvey.getShowDocsOnUnavailabilityPage());
		survey.setAllowQuestionnaireDownload(uploadedSurvey.getAllowQuestionnaireDownload());

		User u = sessionService.getCurrentUser(request);

		if (creation) {
			survey.setOwner(u);
			survey.setDownloadContribution(!survey.getIsDelphi());
		}

		if (!creation) {
			if (!survey.getIsOPC()) {
				if (uploadedSurvey.getPassword() != null
						&& !uploadedSurvey.getPassword().equalsIgnoreCase("********")) {
					if (!uploadedSurvey.getPassword().equals(survey.getPassword())) {
						String[] oldnew = { survey.getPassword(), uploadedSurvey.getPassword() };
						activitiesToLog.put(ActivityRegistry.ID_GLOBAL_PASSWORD, oldnew);
					}

					survey.setPassword(uploadedSurvey.getPassword());
				}

				if (!Objects.equals(uploadedSurvey.getEcasSecurity(), survey.getEcasSecurity())) {
					survey.setEcasSecurity(uploadedSurvey.getEcasSecurity());
				}

				if (!Objects.equals(uploadedSurvey.getEcasMode(), survey.getEcasMode())) {
					survey.setEcasMode(uploadedSurvey.getEcasMode());
				}
			}

			if (uploadedSurvey.getValidatedPerPage() != survey.getValidatedPerPage()) {
				String[] oldnew = { survey.getValidatedPerPage() ? "enabled" : "disabled",
						uploadedSurvey.getValidatedPerPage() ? "enabled" : "disabled" };
				activitiesToLog.put(ActivityRegistry.ID_PAGEWISE_VALIDATION, oldnew);
			}
		
			survey.setValidatedPerPage(uploadedSurvey.getValidatedPerPage());
			
			survey.setPreventGoingBack(uploadedSurvey.getPreventGoingBack());
			
			survey.setAllowedContributionsPerUser(uploadedSurvey.getAllowedContributionsPerUser());

			if (!Objects.equals(uploadedSurvey.getWcagCompliance(), survey.getWcagCompliance())) {
				String[] oldnew = { survey.getWcagCompliance() ? "enabled" : "disabled",
						uploadedSurvey.getWcagCompliance() ? "enabled" : "disabled" };
				activitiesToLog.put(ActivityRegistry.ID_WCAG_COMPLIANCE, oldnew);
			}

			survey.setWcagCompliance(uploadedSurvey.getWcagCompliance());

			if (uploadedSurvey.getSectionNumbering() != survey.getSectionNumbering()) {
				String[] oldnew = { getNumberingLabel(survey.getSectionNumbering()),
						getNumberingLabel(uploadedSurvey.getSectionNumbering()) };
				activitiesToLog.put(ActivityRegistry.ID_AUTO_NUM_SECTIONS, oldnew);
			}

			survey.setSectionNumbering(uploadedSurvey.getSectionNumbering());

			if (uploadedSurvey.getQuestionNumbering() != survey.getQuestionNumbering()) {
				String[] oldnew = { getNumberingLabel(survey.getQuestionNumbering()),
						getNumberingLabel(uploadedSurvey.getQuestionNumbering()) };
				activitiesToLog.put(ActivityRegistry.ID_AUTO_NUM_QUESTIONS, oldnew);
			}

			survey.setQuestionNumbering(uploadedSurvey.getQuestionNumbering());

			if (uploadedSurvey.getAutomaticPublishing() != survey.getAutomaticPublishing()) {
				String[] oldnew = { survey.getAutomaticPublishing() ? "enabled" : "disabled",
						uploadedSurvey.getAutomaticPublishing() ? "enabled" : "disabled" };
				activitiesToLog.put(ActivityRegistry.ID_AUTO_SUBMIT, oldnew);
				if (!uploadedSurvey.getAutomaticPublishing()){
					uploadedSurvey.setStart(null);
					uploadedSurvey.setEnd(null);
				}
			}
			
			survey.setSendConfirmationEmail(uploadedSurvey.getSendConfirmationEmail());

			survey.setAutomaticPublishing(uploadedSurvey.getAutomaticPublishing());
			
			survey.setDedicatedResultPrivileges(uploadedSurvey.getDedicatedResultPrivileges());

			Date start = uploadedSurvey.getStart();
			if (start != null && form.getStartHour() > 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(start);
				cal.add(Calendar.HOUR_OF_DAY, form.getStartHour());
				uploadedSurvey.setStart(cal.getTime());
			}

			Date end = uploadedSurvey.getEnd();
			if (end != null && form.getEndHour() > 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(end);
				cal.add(Calendar.HOUR_OF_DAY, form.getEndHour());
				uploadedSurvey.setEnd(cal.getTime());
			}

			if (uploadedSurvey.getNotificationValue() != null && uploadedSurvey.getNotificationValue().equals("-1")) {
				uploadedSurvey.setNotificationValue(null);
				uploadedSurvey.setNotificationUnit(null);
			}

			if (uploadedSurvey.getNotificationUnit() != null && uploadedSurvey.getNotificationUnit().equals("-1")) {
				uploadedSurvey.setNotificationValue(null);
				uploadedSurvey.setNotificationUnit(null);
			}

			if (uploadedSurvey.getNotificationValue() == null && survey.getNotificationValue() != null) {
				String[] oldnew = { "enabled", "disabled" };
				activitiesToLog.put(ActivityRegistry.ID_END_NOTIFICATION_SETTINGS, oldnew);
			} else if (uploadedSurvey.getNotificationValue() != null && survey.getNotificationValue() == null) {
				String[] oldnew = { "disabled", "enabled" };
				activitiesToLog.put(ActivityRegistry.ID_END_NOTIFICATION_SETTINGS, oldnew);
			}

			if (!Tools.isEqual(uploadedSurvey.getNotificationUnit(), survey.getNotificationUnit())
					|| !Tools.isEqual(uploadedSurvey.getNotificationValue(), survey.getNotificationValue())) {
				String[] oldnew = {
						survey.getNotificationValue() != null
								? survey.getNotificationValue() + " " + survey.getNiceNotificationUnit()
								: "",
						uploadedSurvey.getNotificationValue() + " " + uploadedSurvey.getNiceNotificationUnit() };
				activitiesToLog.put(ActivityRegistry.ID_END_NOTIFICATION_VALUES, oldnew);
			}

			// the feature to limit the notification to the form owner has been removed
			uploadedSurvey.setNotifyAll(true);

			if (uploadedSurvey.getNotifyAll() != survey.getNotifyAll()) {
				if (uploadedSurvey.getNotifyAll()) {
					String[] oldnew = { "disabled", "enabled" };
					activitiesToLog.put(ActivityRegistry.ID_END_NOTIFICATION_REACH, oldnew);
				} else {
					String[] oldnew = { "enabled", "disabled" };
					activitiesToLog.put(ActivityRegistry.ID_END_NOTIFICATION_REACH, oldnew);
				}
			}

			if (uploadedSurvey.getRegistrationForm() != survey.getRegistrationForm()) {
				if (uploadedSurvey.getRegistrationForm()) {
					String[] oldnew = { "disabled", "enabled" };
					activitiesToLog.put(ActivityRegistry.ID_CONTACT_CREATION, oldnew);
				} else {
					String[] oldnew = { "enabled", "disabled" };
					activitiesToLog.put(ActivityRegistry.ID_CONTACT_CREATION, oldnew);
				}
			}

			if (!survey.getIsOPC()) {
				if (uploadedSurvey.getStart() != null && !ConversionTools.getFullString(uploadedSurvey.getStart())
						.equals(ConversionTools.getFullString(survey.getStart()))) {
					String[] oldnew = { ConversionTools.getFullString(survey.getStart()),
							ConversionTools.getFullString(uploadedSurvey.getStart()) };
					activitiesToLog.put(ActivityRegistry.ID_START_DATE, oldnew);
				}

				survey.setStart(uploadedSurvey.getStart());
			}

			if (uploadedSurvey.getEnd() != null && !ConversionTools.getFullString(uploadedSurvey.getEnd())
					.equals(ConversionTools.getFullString(survey.getEnd()))) {
				String[] oldnew = { ConversionTools.getFullString(survey.getEnd()),
						ConversionTools.getFullString(uploadedSurvey.getEnd()) };
				activitiesToLog.put(ActivityRegistry.ID_END_DATE, oldnew);
			}

			survey.setEnd(uploadedSurvey.getEnd());

			survey.setNotificationValue(uploadedSurvey.getNotificationValue());
			survey.setNotificationUnit(uploadedSurvey.getNotificationUnit());
			survey.setNotifyAll(uploadedSurvey.getNotifyAll());

			if (uploadedSurvey.getChangeContribution() != survey.getChangeContribution()) {
				String[] oldnew = { survey.getChangeContribution() ? "enabled" : "disabled",
						uploadedSurvey.getChangeContribution() ? "enabled" : "disabled" };
				activitiesToLog.put(ActivityRegistry.ID_EDIT_CONTRIBUTION, oldnew);
			}

			survey.setChangeContribution(uploadedSurvey.getChangeContribution());
			survey.setSaveAsDraft(uploadedSurvey.getSaveAsDraft());
			survey.setDownloadContribution(uploadedSurvey.getDownloadContribution());
			survey.setRegistrationForm(uploadedSurvey.getRegistrationForm());
			survey.setConfirmationPage(Tools.filterHTML(uploadedSurvey.getConfirmationPage()));
			survey.setEscapePage(Tools.filterHTML(uploadedSurvey.getEscapePage()));
			survey.setIsUseMaxNumberContribution(uploadedSurvey.getIsUseMaxNumberContribution());
			survey.setIsUseMaxNumberContributionLink(uploadedSurvey.getIsUseMaxNumberContributionLink());
			survey.setMaxNumberContributionText(Tools.filterHTML(uploadedSurvey.getMaxNumberContributionText()));
			survey.setMaxNumberContributionLink(Tools.filterHTML(uploadedSurvey.getMaxNumberContributionLink()));
			survey.setMaxNumberContribution(uploadedSurvey.getMaxNumberContribution());

			if (uploadedSurvey.getCaptcha() != survey.getCaptcha()) {
				String[] oldnew = { survey.getCaptcha() ? "enabled" : "disabled",
						uploadedSurvey.getCaptcha() ? "enabled" : "disabled" };
				activitiesToLog.put(ActivityRegistry.ID_CAPTCHA_SETTINGS, oldnew);
			}

			if (!survey.getIsOPC())
				survey.setCaptcha(uploadedSurvey.getCaptcha());
			Map<String, String> oldLinks = new HashMap<>();
			oldLinks.putAll(survey.getUsefulLinks());

			// useful links
			survey.getUsefulLinks().clear();
			for (Entry<String, String[]> entry : parameterMap.entrySet()) {
				if (entry.getKey().startsWith("linklabel")) {
					String number = entry.getKey().substring(9);
					String label = number + "#" + Tools.escapeHTML(Tools.filterHTML(entry.getValue()[0]));
					String url = parameterMap.get(entry.getKey().replace("label", "url"))[0];

					if (StringUtils.hasText(label) && StringUtils.hasText(url)) {

						if (label != null && !XHTMLValidator.validate(label, servletContext, null)) {
							throw new InvalidXHTMLException(label, label);
						}

						if (!oldLinks.containsKey(label) || !oldLinks.get(label).equals(url)) {
							hasPendingChanges = true;
						}

						survey.getUsefulLinks().put(label, url);
					}
				}
			}

			for (String label : survey.getUsefulLinks().keySet()) {
				if (!oldLinks.containsKey(label)) {
					hasPendingChanges = true;
					String[] oldnew = { null, label.contains("#") ? label.substring(label.indexOf('#') + 1)
							: label + ":" + survey.getUsefulLinks().get(label) };

					if (activitiesToLog.containsKey(ActivityRegistry.ID_USEFUL_LINK_ADD)) {
						activitiesToLog.put(ActivityRegistry.ID_USEFUL_LINK_ADD, ArrayUtils.addAll(activitiesToLog.get(ActivityRegistry.ID_USEFUL_LINK_ADD), oldnew));
					} else {
						activitiesToLog.put(ActivityRegistry.ID_USEFUL_LINK_ADD, oldnew);
					}
				}
			}
			for (Entry<String, String> entry : oldLinks.entrySet()) {
				String label = entry.getKey();
				if (!survey.getUsefulLinks().containsKey(label)) {
					hasPendingChanges = true;
					String[] oldnew = { label.contains("#") ? label.substring(label.indexOf('#') + 1)
							: label + ":" + entry.getValue(), null };

					if (activitiesToLog.containsKey(ActivityRegistry.ID_USEFUL_LINK_REMOVE)) {
						activitiesToLog.put(ActivityRegistry.ID_USEFUL_LINK_REMOVE, ArrayUtils.addAll(activitiesToLog.get(ActivityRegistry.ID_USEFUL_LINK_REMOVE), oldnew));
					} else {
						activitiesToLog.put(ActivityRegistry.ID_USEFUL_LINK_REMOVE, oldnew);
					}
				}
			}

			oldBackgroundDocuments.putAll(survey.getBackgroundDocuments());

			// background documents
			survey.getBackgroundDocuments().clear();
			for (Entry<String, String[]> entry : parameterMap.entrySet()) {
				if (entry.getKey().startsWith("doclabel")) {
					String label = Tools.escapeHTML(Tools.filterHTML(entry.getValue()[0]));
					String url = parameterMap.get(entry.getKey().replace("label", "url"))[0];

					if (StringUtils.hasText(label) && StringUtils.hasText(url)) {
						if (label != null && !XHTMLValidator.validate(label, servletContext, null)) {
							throw new InvalidXHTMLException(label, label);
						}

						if (!oldBackgroundDocuments.containsKey(label)
								|| !oldBackgroundDocuments.get(label).equals(url)) {
							hasPendingChanges = true;
						}

						survey.getBackgroundDocuments().put(label, url);
					}
				}
			}
			// background documents without labels
			for (Entry<String, String[]> entry : parameterMap.entrySet()) {
				if (entry.getKey().startsWith("docurl")) {
					String url = Tools.escapeHTML(entry.getValue()[0]);
					String label = parameterMap.get(entry.getKey().replace("url", "label"))[0];

					if (StringUtils.hasText(url) && !StringUtils.hasText(label)) {
						String uid = url.substring(url.lastIndexOf('/') + 1);
						File f = fileService.get(uid);

						if (f != null) {
							label = f.getName();
						}
						if (StringUtils.hasText(label) && StringUtils.hasText(url)) {
							if (label != null && !XHTMLValidator.validate(label, servletContext, null)) {
								throw new InvalidXHTMLException(label, label);
							}

							if (!oldBackgroundDocuments.containsKey(label)
									|| !oldBackgroundDocuments.get(label).equals(url)) {
								hasPendingChanges = true;
							}

							survey.getBackgroundDocuments().put(label, url);
						}
					}
				}
			}

			for (String label : survey.getBackgroundDocuments().keySet()) {
				if (!oldBackgroundDocuments.containsKey(label)) {
					hasPendingChanges = true;
					String[] oldnew = { null, label + ":" + survey.getBackgroundDocuments().get(label) };

					if (activitiesToLog.containsKey(ActivityRegistry.ID_BACKGROUND_DOC_ADD)) {
						activitiesToLog.put(ActivityRegistry.ID_BACKGROUND_DOC_ADD, ArrayUtils.addAll(activitiesToLog.get(ActivityRegistry.ID_BACKGROUND_DOC_ADD), oldnew));
					} else {
						activitiesToLog.put(ActivityRegistry.ID_BACKGROUND_DOC_ADD, oldnew);
					}
				}
			}
			for (Entry<String, String> entry : oldBackgroundDocuments.entrySet()) {
				if (!survey.getBackgroundDocuments().containsKey(entry.getKey())) {
					hasPendingChanges = true;
					String[] oldnew = { entry.getKey() + ":" + entry.getValue(), null };
					if (activitiesToLog.containsKey(ActivityRegistry.ID_BACKGROUND_DOC_REMOVE)) {
						activitiesToLog.put(ActivityRegistry.ID_BACKGROUND_DOC_REMOVE, ArrayUtils.addAll(activitiesToLog.get(ActivityRegistry.ID_BACKGROUND_DOC_REMOVE), oldnew));
					} else {
						activitiesToLog.put(ActivityRegistry.ID_BACKGROUND_DOC_REMOVE, oldnew);
					}

					fileService.deleteIfNotReferenced(entry.getValue(), survey.getUniqueId());
				}
			}

			String logo = request.getParameter("logo");
			if (logo != null && logo.length() > 0) {
				if (logo.equalsIgnoreCase(Constants.DELETED)) {
					if (survey.getLogo() != null) {
						String[] oldnew = {survey.getLogo().getName(), Constants.DELETED};
						activitiesToLog.put(ActivityRegistry.ID_LOGO, oldnew);

						survey.setLogo(null);
						hasPendingChanges = true;
					}
				} else {
					File f;
					try {
						f = fileService.get(logo);

						String[] oldnew = { survey.getLogo() != null ? survey.getLogo().getName() : null, f.getName() };
						activitiesToLog.put(ActivityRegistry.ID_LOGO, oldnew);

						survey.setLogo(f);
						hasPendingChanges = true;
					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}
			}

			if (!uploadedSurvey.getLogoInInfo().equals(survey.getLogoInInfo())) {
				hasPendingChanges = true;
			}
			
			survey.setLogoInInfo(uploadedSurvey.getLogoInInfo());

			if (!Objects.equals(uploadedSurvey.getLogoText(), survey.getLogoText())) {
				hasPendingChanges = true;
				keyTranslationsToAdd.add(Survey.LOGOTEXT);
			}

			survey.setLogoText(ConversionTools.escape(uploadedSurvey.getLogoText()));

			if (survey.getPublication().isShowContent() != uploadedSurvey.getPublication().isShowContent()) {
				String[] oldnew = { survey.getPublication().isShowContent() ? "published" : "unpublished",
						uploadedSurvey.getPublication().isShowContent() ? "published" : "unpublished" };
				activitiesToLog.put(ActivityRegistry.ID_CONTENT_PUBLICATION, oldnew);
			}

			if (!Objects.equals(survey.getPublication().getShowUploadedDocuments(),
					uploadedSurvey.getPublication().getShowUploadedDocuments())) {
				String[] oldnew = { survey.getPublication().getShowUploadedDocuments() ? "published" : "unpublished",
						uploadedSurvey.getPublication().getShowUploadedDocuments() ? "published" : "unpublished" };
				activitiesToLog.put(ActivityRegistry.ID_UPLOADED_ELEMENTS_PUBLISH, oldnew);
			}

			if (uploadedSurvey.getPublication().getPassword() != null
					&& !uploadedSurvey.getPublication().getPassword().equalsIgnoreCase("********")) {
				survey.getPublication().setPassword(uploadedSurvey.getPublication().getPassword());
			}
			survey.getPublication().setShowContent(uploadedSurvey.getPublication().isShowContent());
			survey.getPublication()
					.setShowUploadedDocuments(uploadedSurvey.getPublication().getShowUploadedDocuments());

			if (survey.getPublication().isShowStatistics() != uploadedSurvey.getPublication().isShowStatistics()) {
				String[] oldnew = { survey.getPublication().isShowStatistics() ? "published" : "unpublished",
						uploadedSurvey.getPublication().isShowStatistics() ? "published" : "unpublished" };
				activitiesToLog.put(ActivityRegistry.ID_STATISTICS_PUBLICATION, oldnew);
			}

			survey.getPublication().setShowStatistics(uploadedSurvey.getPublication().isShowStatistics());

			if (survey.getPublication().isShowCharts() != uploadedSurvey.getPublication().isShowCharts()) {
				String[] oldnew = { survey.getPublication().isShowCharts() ? "published" : "unpublished",
						uploadedSurvey.getPublication().isShowCharts() ? "published" : "unpublished" };
				activitiesToLog.put(ActivityRegistry.ID_CHARTS_PUBLICATION, oldnew);
			}

			survey.getPublication().setShowCharts(uploadedSurvey.getPublication().isShowCharts());

			if (survey.getPublication().isShowSearch() != uploadedSurvey.getPublication().isShowSearch()) {
				String[] oldnew = { survey.getPublication().isShowSearch() ? "enabled" : "disabled",
						uploadedSurvey.getPublication().isShowSearch() ? "enabled" : "disabled" };
				activitiesToLog.put(ActivityRegistry.ID_PUBLIC_RESULTS_SEARCH, oldnew);
			}

			StringBuilder oldQuestions = new StringBuilder();
			StringBuilder newQuestions = new StringBuilder();
			StringBuilder oldContributions = new StringBuilder();
			StringBuilder newContributions = new StringBuilder();

			if (survey.getPublication().isAllQuestions()) {
				oldQuestions = new StringBuilder("all");
			} else {
				for (String id : survey.getPublication().getFilter().getVisibleQuestions()) {
					oldQuestions.append(id).append(";");
				}
			}

			if (survey.getPublication().isAllContributions()) {
				oldContributions = new StringBuilder("all");
			} else {
				for (String id : survey.getPublication().getFilter().getFilterValues().keySet()) {
					oldContributions.append(id).append(":")
							.append(survey.getPublication().getFilter().getFilterValues().get(id)).append(";");
				}
			}

			survey.getPublication().setShowSearch(uploadedSurvey.getPublication().isShowSearch());
			survey.getPublication().setAllQuestions(uploadedSurvey.getPublication().isAllQuestions());
			survey.getPublication().setAllContributions(uploadedSurvey.getPublication().isAllContributions());

			survey.getPublication().getFilter().getVisibleQuestions().clear();
			survey.getPublication().getFilter().getFilterValues().clear();

			int contributionfiltercounter = 0;

			for (Entry<String, String[]> entry : parameterMap.entrySet()) {
				String key = entry.getKey();
				if (key.startsWith("question")) {
					String value = entry.getValue()[0];
					survey.getPublication().getFilter().getVisibleQuestions().add(value);
					newQuestions.append(value).append(";");
				} else if (key.startsWith("contribution")) {
					String[] values = entry.getValue();
					String value = StringUtils.arrayToDelimitedString(values, ";");
					key = key.substring(12);
					survey.getPublication().getFilter().getFilterValues().put(key, value);
					newContributions.append(key).append(":").append(value).append(";");
					contributionfiltercounter++;

					if (contributionfiltercounter > 4) {
						String message = resources.getMessage("validation.atmost3Selections", null,
								"Please select at most 3 answers", locale);
						return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, message);
					}
				} else if (key.equalsIgnoreCase("newskin") && !survey.getIsOPC()
						&& entry.getValue()[0].trim().length() > 0) {
					int skinId = Integer.parseInt(entry.getValue()[0]);
					Skin skin = skinService.get(skinId);

					if (survey.getSkin() != null && !Tools.isEqual(survey.getSkin(), skin)) {
						hasPendingChanges = true;

						String[] oldnew = { survey.getSkin().getName(), skin.getName() };
						activitiesToLog.put(ActivityRegistry.ID_SKIN, oldnew);
					}

					survey.setSkin(skin);
				}
			}

			if (uploadedSurvey.getPublication().isAllQuestions()) {
				newQuestions = new StringBuilder("all");
			}

			if (uploadedSurvey.getPublication().isAllContributions()) {
				newContributions = new StringBuilder("all");
			}

			if (!newQuestions.toString().equals(oldQuestions.toString())) {
				String[] oldnew = { oldQuestions.toString(), newQuestions.toString() };
				activitiesToLog.put(ActivityRegistry.ID_PUBLISH_QUESTION_SET, oldnew);
			}

			if (!newContributions.toString().equals(oldContributions.toString())) {
				String[] oldnew = { oldContributions.toString(), newContributions.toString() };
				activitiesToLog.put(ActivityRegistry.ID_PUBLISH_ANSWER_SET, oldnew);
			}
		
		}

		Map<String, Translations> translationToCreate = new HashMap<>();
		ensurePropertiesDependingOnSurveyType(survey, creation, translationToCreate);
		
		form.setSurvey(survey);
		form.setLanguage(survey.getLanguage());

		if (creation) {
			// set the constant messages of the survey to the main language selected in the creation dialogue
			initializeMessageConstantsForLanguage(survey, locale);

			surveyService.add(survey, u.getId());
			if (!translationToCreate.isEmpty()) {
				for (Translations trans : translationToCreate.values()) {
					trans.setSurveyId(survey.getId());
					trans.setSurveyUid(survey.getUniqueId());
					
					for (Translation t : trans.getTranslations()) {
						t.setSurveyId(survey.getId());	
					}
					
					translationService.add(trans);
				}				
			}
			sessionService.updateSessionInfo(survey, u, request);
			activityService.log(ActivityRegistry.ID_SURVEY_CREATED, null, form.getSurvey().getId().toString(), u.getId(), survey.getUniqueId());

			surveyService.setBrpAccess(survey);

			return new ModelAndView("redirect:/" + form.getSurvey().getShortname() + "/management/edit");
		} else {

			for (Translations t : translationService.getTranslationsForSurvey(survey.getId(), true)){
				boolean changed = false;
				if (languageChanged && t.getLanguage().getCode().equals(survey.getLanguage().getCode())){
					t.setActive(true);
					changed = true;
				}

				changed |= TranslationsHelper.includeNewKeyTranslations(survey, t, keyTranslationsToAdd);

				if (changed)
					translationService.save(t);
			}

			if (languageChanged) {
				Translations translations = translationService.getTranslations(survey.getId(),
						survey.getLanguage().getCode());
				TranslationsHelper.synchronizePivot(survey, translations);
			}

			survey = surveyService.update(survey, hasPendingChanges, !languageChanged, true, u.getId());
			form.setSurvey(survey);

			if (survey.getIsOPC() && opcusers != null && opcusers.length() > 0) {
				String[] users = opcusers.split(";");
				int counter = 1;
				for (String user : users) {
					if (user.length() > 0) {
						User opcuser = administrationService.getUserForLogin(user);
						if (opcuser != null) {
							Access a = surveyService.getAccess(survey.getId(), opcuser.getId());
							if (a == null) {
								a = new Access();
								a.setUser(opcuser);
								a.setSurvey(survey);
							}
							
							if (counter == 1) {
								a.getLocalPrivileges().put(LocalPrivilege.AccessResults, 1);
								a.getLocalPrivileges().put(LocalPrivilege.FormManagement, 2);
							} else if (counter == 2) {
								a.getLocalPrivileges().put(LocalPrivilege.AccessResults, 1);
								a.getLocalPrivileges().put(LocalPrivilege.FormManagement, 1);
							} else {
								a.getLocalPrivileges().put(LocalPrivilege.FormManagement, 1);
							}											
							
							counter++;
							surveyService.saveAccess(a);
						}
					}
				}
			}

			for (String url : oldBackgroundDocuments.values()) {
				if (!survey.getBackgroundDocuments().containsValue(url)) {
					String uid = url.replace(contextpath + "/files/", "");
					fileService.deleteIfNotReferenced(uid, survey.getUniqueId());
				}
			}

			if (oldlogo != null
					&& (survey.getLogo() == null || !Objects.equals(survey.getLogo().getUid(), oldlogo.getUid()))) {
				fileService.deleteIfNotReferenced(oldlogo.getUid(), survey.getUniqueId());
			}

			if (survey.getAutomaticPublishing()) {
				if (survey.getIsActive() && survey.getAutomaticPublishing() && survey.getEnd() != null
						&& survey.getEnd().before(new Date())) {
					survey.setIsActive(false);
					surveyService.update(survey, hasPendingChanges, !languageChanged, true, u.getId());
				} else if (!survey.getIsActive() && survey.getAutomaticPublishing() && survey.getStart() != null
						&& survey.getStart().before(new Date())) {
					if (survey.getEnd() == null || survey.getEnd().after(new Date())) {
						if (!survey.getIsPublished()) {
							surveyService.publish(survey, -1, -1, false, u.getId(), false, false);
						}
						survey.setIsActive(true);
						survey.setIsPublished(true);
						survey.setNotified(false);
						surveyService.update(survey, false, !languageChanged, true, u.getId());
					}
				} else if (survey.getIsActive() && survey.getAutomaticPublishing() && survey.getStart() != null
						&& survey.getStart().after(new Date())) {
					survey.setIsActive(false);
					surveyService.update(survey, hasPendingChanges, !languageChanged, true, u.getId());
				}
			}

			// propagate security settings, advanced settings and publish results changes
			Survey publishedSurvey = surveyService.getSurvey(originalShortname, false, false, false, false, null, false,
					false);
			if (publishedSurvey != null) {
				publishedSurvey.setShortname(survey.getShortname());

				publishedSurvey.setSecurity(survey.getSecurity());
				publishedSurvey.setListForm(survey.getListForm());
				publishedSurvey.setListFormValidated(survey.isListFormValidated());
				publishedSurvey.setCaptcha(survey.getCaptcha());
				publishedSurvey.setChangeContribution(survey.getChangeContribution());
				publishedSurvey.setSaveAsDraft(survey.getSaveAsDraft());
				publishedSurvey.setDownloadContribution(survey.getDownloadContribution());
				publishedSurvey.setPassword(survey.getPassword());
				publishedSurvey.setDedicatedResultPrivileges(survey.getDedicatedResultPrivileges());

				publishedSurvey.setMaxNumberContribution(survey.getMaxNumberContribution());
				publishedSurvey.setMaxNumberContributionLink(survey.getMaxNumberContributionLink());
				publishedSurvey.setMaxNumberContributionText(survey.getMaxNumberContributionText());
				publishedSurvey.setIsUseMaxNumberContribution(survey.getIsUseMaxNumberContribution());
				publishedSurvey.setIsUseMaxNumberContributionLink(survey.getIsUseMaxNumberContributionLink());

				publishedSurvey.setEcasSecurity(survey.getEcasSecurity());
				publishedSurvey.setEcasMode(survey.getEcasMode());
				publishedSurvey.setAllowedContributionsPerUser(survey.getAllowedContributionsPerUser());

				publishedSurvey.setAutomaticPublishing(survey.getAutomaticPublishing());
				publishedSurvey.setStart(survey.getStart());
				publishedSurvey.setEnd(survey.getEnd());
				publishedSurvey.setNotificationValue(survey.getNotificationValue());
				publishedSurvey.setNotificationUnit(survey.getNotificationUnit());
				publishedSurvey.setNotifyAll(survey.getNotifyAll());
				publishedSurvey.setRegistrationForm(survey.getRegistrationForm());
				publishedSurvey.setAllowQuestionnaireDownload(survey.getAllowQuestionnaireDownload());
				
				publishedSurvey.getPublication().setPassword(survey.getPublication().getPassword());

				publishedSurvey.getPublication().setShowContent(survey.getPublication().isShowContent());
				publishedSurvey.getPublication().setShowStatistics(survey.getPublication().isShowStatistics());
				publishedSurvey.getPublication().setShowCharts(survey.getPublication().isShowCharts());
				publishedSurvey.getPublication().setShowSearch(survey.getPublication().isShowSearch());
				publishedSurvey.getPublication().setAllQuestions(survey.getPublication().isAllQuestions());
				publishedSurvey.getPublication().setAllContributions(survey.getPublication().isAllContributions());
				publishedSurvey.getPublication()
						.setShowUploadedDocuments(survey.getPublication().getShowUploadedDocuments());
				
				publishedSurvey.getPublication().getFilter().getVisibleQuestions().clear();
				publishedSurvey.getPublication().getFilter().getFilterValues().clear();

				publishedSurvey.setQuorum(survey.getQuorum());
				publishedSurvey.setMinListPercent(survey.getMinListPercent());
				publishedSurvey.setMaxPrefVotes(survey.getMaxPrefVotes());
				publishedSurvey.setSeatsToAllocate(survey.getSeatsToAllocate());
				publishedSurvey.setShowResultsTestPage(survey.getShowResultsTestPage());
				
				Map<Integer, Element> originalElementsById = survey.getElementsById();
				Map<String, Element> elementsByUniqueId = publishedSurvey.getElementsByUniqueId();

				for (Entry<String, String[]> entry : parameterMap.entrySet()) {
					if (entry.getKey().startsWith("question")) {
						String value = entry.getValue()[0];
						String uid = originalElementsById.get(Integer.parseInt(value)).getUniqueId();

						if (elementsByUniqueId.containsKey(uid)) {
							publishedSurvey.getPublication().getFilter().getVisibleQuestions()
									.add(elementsByUniqueId.get(uid).getId().toString());
						}
					} else if (entry.getKey().startsWith("contribution")) {
						String value = StringUtils.arrayToDelimitedString(entry.getValue(), ";");
						String key = entry.getKey().substring(12);
						publishedSurvey.getPublication().getFilter().getFilterValues().put(key, value);
					}
				}

				surveyService.update(publishedSurvey, true, true, true, u.getId());
			}

			if (shortnameChanged) {
				surveyService.propagateNewShortname(survey);
			}

			sessionService.updateSessionInfo(survey, u, request);
			String[] emptyOldNew = { null, null };
			activitiesToLog.put(ActivityRegistry.ID_PROPERTIES, emptyOldNew);
			activityService.log(activitiesToLog, u.getId(), survey.getUniqueId());

			if (sendListFormMail && enablepublicsurveys.equalsIgnoreCase("true")) {
				surveyService.sendListFormMail(survey);
			}

			if (request.getParameter("origin") != null && request.getParameter("origin").equalsIgnoreCase("overview")) {
				return new ModelAndView("redirect:/" + survey.getShortname() + "/management/overview");
			}

			return properties(form.getSurvey().getShortname(), request, locale);
		}
	}

	/**
	 * Sets the values of a Survey's default constants to the given language
	 */
	public void initializeMessageConstantsForLanguage(Survey survey, Locale locale){
		Locale localeWithNewMainLanguage = new Locale(survey.getLanguage().getCode());

		// confirmation text
		survey.setConfirmationPage(resources.getMessage("message.confirmationWithTitle", null, survey.CONFIRMATIONTEXT, localeWithNewMainLanguage));

		// escape page message
		survey.setEscapePage(resources.getMessage("message.escape", null, survey.ESCAPETEXT, localeWithNewMainLanguage));
	}

	private String getNumberingLabel(int value) {
		switch (value) {
		case 0:
			return "disabled";
		case 1:
			return "numbers";
		case 2:
			return "smalls";
		case 3:
			return "capitals";
		case 4:
			return "numbers";
		case 5:
			return "smalls";
		case 6:
			return "capitals";
		default:
			return "disabled";
		}
	}

	private boolean invalid(String term) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9-_]+$");
		Matcher m = p.matcher(term);
		return !m.find();
	}

	@RequestMapping(value = "/edit", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView edit(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws Exception {
		Form form;
		form = sessionService.getForm(request, shortname, false, true);
		form.setResources(resources);

		User u = sessionService.getCurrentUser(request);
		if (!u.getId().equals(form.getSurvey().getOwner().getId())
				&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
				&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
			throw new ForbiddenURLException();
		}

		ModelAndView result = new ModelAndView("management/edit", "form", form);
		result.addObject("templates", surveyService.getTemplates(sessionService.getCurrentUser(request).getId()));
		result.addObject("activeTranslations",
				translationService.getTranslationsForSurvey(form.getSurvey().getId(), true));

		if (request.getParameter("saved") != null && request.getParameter("saved").equalsIgnoreCase("true")) {
			result.addObject("saved", true);
		}

		if (form.getSurvey().getIsEVote()) {
			boolean invalidEVote = parseEVoteSurvey(form.getSurvey());
			if (invalidEVote) {
				result.addObject("invalidevote", true);
			}
		}

		return result;
	}

	@PostMapping(value = "/checkXHTML")
	public @ResponseBody XHTMLValidation checkXHTML(@RequestBody String data, HttpServletRequest request) {
		return new XHTMLValidation(data, !XHTMLValidator.validate(data, servletContext, null));
	}

	@PostMapping(value = "/edit")
	public ModelAndView editPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws Exception {
		Form form;
		form = sessionService.getForm(request, shortname, false, false);

		User u = sessionService.getCurrentUser(request);
		if (!u.getId().equals(form.getSurvey().getOwner().getId())
				&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
				&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
			throw new ForbiddenURLException();
		}

		Survey survey = editSave(form.getSurvey(), request);
		answerService.deleteStatisticsForSurvey(survey.getId());

		reportingService.addToDo(ToDo.CHANGEDDRAFTSURVEY, survey.getUniqueId(), null);

		u = administrationService.setLastEditedSurvey(u, survey.getId());
		sessionService.setCurrentUser(request, u);

		String editorredirect = request.getParameter("editorredirect");
		if (editorredirect != null && editorredirect.trim().length() > 0) {
			if (editorredirect.startsWith(Constants.PATH_DELIMITER)) {
				editorredirect = editorredirect.substring(1);
				editorredirect = editorredirect.substring(editorredirect.indexOf('/') + 1);
			} else if (editorredirect.contains("?")) {
				editorredirect = editorredirect.substring(editorredirect.indexOf("?"));
				editorredirect = form.getSurvey().getShortname() + "/management/edit?saved=true&" + editorredirect.substring(1);
			}

			request.getSession().setAttribute("surveyeditorsaved", survey.getId());
			return new ModelAndView("redirect:/" + editorredirect);
		}

		return new ModelAndView("redirect:/" + form.getSurvey().getShortname() + "/management/edit?saved=true");
	}

	@PostMapping(value = "/saveTemplate")
	public @ResponseBody List<Template> saveTemplate(@PathVariable String shortname, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {

		Form form;
		try {
			form = sessionService.getForm(request, shortname, false, false);

			Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);

			String name = Tools.escapeHTML(parameterMap.get("template-name")[0]);
			String id = Tools.escapeHTML(parameterMap.get("template-id")[0]);

			Element element = SurveyHelper.parseElement(request, fileService, id, form.getSurvey(), servletContext,
					activityService.isEnabled(ActivityRegistry.ID_ELEMENT_UPDATED));

			Template template = new Template();
			template.setName(name);
			template.setOwner(sessionService.getCurrentUser(request));
			template.setElement(element);

			surveyService.save(template);

			return surveyService.getTemplates(sessionService.getCurrentUser(request).getId());

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;

	}

	@PostMapping(value = "/upload")
	public void upload(@PathVariable String shortname, HttpServletRequest request, HttpServletResponse response) {
		upload(request, response, false);
	}

	@PostMapping(value = "/uploadimage")
	public void uploadImage(@PathVariable String shortname, HttpServletRequest request, HttpServletResponse response) {
		upload(request, response, true);
	}

	private void upload(HttpServletRequest request, HttpServletResponse response, boolean isImage) {

		PrintWriter writer = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			writer = response.getWriter();
		} catch (IOException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
		}

		String filename;
		boolean error = false;

		try {

			if (request instanceof DefaultMultipartHttpServletRequest) {
				DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest) request;
				filename = com.ec.survey.tools.FileUtils
						.cleanFilename(java.net.URLDecoder.decode(r.getFile("qqfile").getOriginalFilename(), "UTF-8"));
				is = r.getFile("qqfile").getInputStream();
			} else {
				filename = com.ec.survey.tools.FileUtils
						.cleanFilename(java.net.URLDecoder.decode(request.getHeader("X-File-Name"), "UTF-8"));
				is = request.getInputStream();
			}

			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
				throw new ForbiddenURLException();
			}

			String filenamelc = filename.toLowerCase();

			if (isImage && !filenamelc.endsWith("png") && !filenamelc.endsWith("jpg") && !filenamelc.endsWith("gif")
					&& !filenamelc.endsWith("bmp")) {
				error = true;
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.print("{\"success\": false}");
			}

			if (!error) {
				String uid = UUID.randomUUID().toString();

				java.io.File file = fileService.getSurveyFile(form.getSurvey().getUniqueId(), uid);

				fos = new FileOutputStream(file);
				IOUtils.copy(is, fos);
				File f = new File();
				f.setUid(uid);
				f.setName(filename);

				if (filenamelc.endsWith("png") || filenamelc.endsWith("jpg") || filenamelc.endsWith("gif")
						|| filenamelc.endsWith("bmp")) {
					try {
						Image image = Toolkit.getDefaultToolkit().getImage(file.getPath());
						ImageIcon icon = new ImageIcon(image);
						int width = icon.getIconWidth();
						f.setWidth(width);
					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}

				fileService.add(f);

				response.setStatus(HttpServletResponse.SC_OK);
				writer.print("{\"success\": true, \"id\": '" + f.getUid() + "', \"uid\": '" + f.getUid()
						+ "', \"longdesc\": '" + f.getLongdesc() + "', \"comment\": '" + f.getComment()
						+ "', \"width\": '" + f.getWidth() + "', \"name\": '" + filename + "'}");
			}
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writer.print("{\"success\": false}");
			logger.error(ex.getMessage(), ex);
		} finally {
			try {
				fos.close();
				is.close();
			} catch (IOException ignored) {
				// ignore
			}
		}

		writer.flush();
		writer.close();
	}

	@RequestMapping(value = "/copyfile", method = { RequestMethod.GET, RequestMethod.HEAD })
	public void copyfile(HttpServletRequest request, HttpServletResponse response) {
		String uid = request.getParameter("uid");

		PrintWriter writer = null;
		try {
			writer = response.getWriter();

			Form form = sessionService.getForm(request, null, false, false);

			User u = sessionService.getCurrentUser(request);
			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
				throw new ForbiddenURLException();
			}

			File copy = fileService.copyFile(uid, form.getSurvey().getUniqueId());

			response.setStatus(HttpServletResponse.SC_OK);
			writer.print("{\"success\": true, \"newuid\": \"" + copy.getUid() + "\"}");

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			response.setStatus(HttpServletResponse.SC_OK);
			writer.print("{\"success\": false}");
		}
		writer.flush();
		writer.close();
	}

	@RequestMapping(value = "/deleteFile", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody String deleteFile(HttpServletRequest request, HttpServletResponse response) {

		try {
			String uid = request.getParameter("uid");
			String suid = request.getParameter("suid");
			fileService.deleteIfNotReferenced(uid, suid);
			return "{\"success\": true}";

		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return "{\"success\": false}";
	}

	@RequestMapping(value = "/deleteDownloadFile", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody String deleteDownloadFile(HttpServletRequest request, HttpServletResponse response) {

		try {
			String uid = request.getParameter("uid");
			String suid = request.getParameter("suid");
			String eid = request.getParameter("eid");
			File file = fileService.get(uid);

			try {
				Element element = surveyService.getElement(Integer.parseInt(eid));
				if (element instanceof Download) {
					Download download = (Download) element;
					download.getFiles().remove(file);
				} else if (element instanceof Confirmation) {
					Confirmation confirmation = (Confirmation) element;
					confirmation.getFiles().remove(file);
				}
				surveyService.update(element);
			} catch (NumberFormatException e) {
				// ignore, this happens if the element was never saved to the database
			}

			fileService.delete(file);
			fileService.deleteIfNotReferenced(uid, suid);
			return "{\"success\": true}";

		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		}

		return "{\"success\": false}";
	}

	@RequestMapping(value = "/test", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView test(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws InvalidURLException, NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException,
			ForbiddenURLException, FrozenSurveyException {
		User u = sessionService.getCurrentUser(request);
		Survey survey = surveyService.getSurveyByShortname(shortname, true, u, request, true, true, true, false);

		if (survey.getIsFrozen()) {
			throw new FrozenSurveyException();
		}

		sessionService.upgradePrivileges(survey, u, request);

		Form form = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), true),
				survey.getLanguage(), resources, contextpath);

		if (!u.getId().equals(form.getSurvey().getOwner().getId())
				&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
				&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 2
				&& u.getLocalPrivileges().get(LocalPrivilege.AccessDraft) < 1
				&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
			throw new ForbiddenURLException();
		}

		String lang = request.getParameter("surveylanguage");

		if (lang != null && form.translationIsValid(lang)) {
			Survey translated = SurveyHelper.createTranslatedSurvey(form.getSurvey().getId(), lang, surveyService,
					translationService, true);
			form.setSurvey(translated);
			form.setLanguage(surveyService.getLanguage(lang));
			sessionService.updateSessionInfo(translated, sessionService.getCurrentUser(request), request);
		}

		String wcag = request.getParameter("wcag");
		if (wcag != null && wcag.equalsIgnoreCase("enabled")) {
			form.setWcagCompliance(true);
		} else if (wcag != null && wcag.equalsIgnoreCase("disabled")) {
			form.setWcagCompliance(false);
		}

		ModelAndView result = new ModelAndView("management/test", "form", form);

		// this code will be used as an identifier (for uploaded files etc)
		String uniqueCode = UUID.randomUUID().toString();

		String draftid = request.getParameter("draftid");
		if (draftid == null || draftid.trim().length() == 0) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("draft" + form.getSurvey().getId().toString())) {
						draftid = cookie.getValue();
						break;
					}
				}
			}
		}
		if (draftid != null && draftid.trim().length() > 0) {
			try {

				Draft draft = answerService.getDraft(draftid);
				form.getAnswerSets().add(draft.getAnswerSet());
				form.setWcagCompliance(
						draft.getAnswerSet().getWcagMode() != null && draft.getAnswerSet().getWcagMode());

				SurveyHelper.recreateUploadedFiles(draft.getAnswerSet(), form.getSurvey(), fileService, answerExplanationService);
				uniqueCode = draft.getAnswerSet().getUniqueCode();

				ModelAndView err = testDraftAlreadySubmittedByUniqueCode(uniqueCode, locale);
				if (err != null)
					return err;

				lang = draft.getAnswerSet().getLanguageCode();
				Survey translated = SurveyHelper.createTranslatedSurvey(form.getSurvey().getId(), lang, surveyService,
						translationService, true);
				form.setSurvey(translated);
				form.setLanguage(surveyService.getLanguage(lang));
				sessionService.updateSessionInfo(translated, sessionService.getCurrentUser(request), request);

				Set<String> invisibleElements = new HashSet<>();
				SurveyHelper.validateAnswerSet(draft.getAnswerSet(), answerService, invisibleElements, resources,
						locale, null, null, true, u, fileService);
				result.addObject("invisibleElements", invisibleElements);

				result.addObject("draftid", draftid);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		} else {
			if (survey.getIsQuiz() && request.getParameter("startQuiz") == null) {
				result = new ModelAndView("management/testQuiz", "form", form);
				result.addObject("isquizpage", true);
			} else if (survey.getIsDelphi() && survey.getIsDelphiShowStartPage() && request.getParameter("startDelphi") == null) {
				result = new ModelAndView("management/testDelphi", "form", form);
				result.addObject("isdelphipage", true);
			}
		}

		result.addObject("submit", true);
		result.addObject(Constants.UNIQUECODE, uniqueCode);
		
		sessionService.setFormStartDate(request, form, uniqueCode);

		return result;
	}

	@PostMapping(value = "/test")
	public ModelAndView testPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws Exception {

		Survey survey = surveyService.getSurvey(Integer.parseInt(request.getParameter("survey.id")), false, true);

		User user = sessionService.getCurrentUser(request);

		String uniqueCode = request.getParameter(Constants.UNIQUECODE);
		
		String lang = locale.getLanguage();
		if (request.getParameter("language.code") != null && request.getParameter("language.code").length() == 2) {
			lang = request.getParameter("language.code");
		}
		
		if (!survey.getIsDelphi()) {
			ModelAndView err = testDraftAlreadySubmittedByUniqueCode(uniqueCode, locale);
			if (err != null)
				return err;
		}
		
		AnswerSet answerSet = answerService.automaticParseAnswerSet(request, survey, uniqueCode, false, lang, user);
	
		String newlang = request.getParameter("newlang");
		String newlangpost = request.getParameter("newlangpost");
		String newcss = request.getParameter("newcss");
		String newviewpost = request.getParameter("newviewpost");

		Set<String> invisibleElements = new HashSet<>();

		Map<Element, String> validation = SurveyHelper.validateAnswerSet(answerSet, answerService, invisibleElements,
				resources, locale, request.getParameter("draftid"), request, false, user, fileService);

		if (newlangpost != null && newlangpost.equalsIgnoreCase("true")) {
			survey = surveyService.getSurvey(survey.getId(), newlang);
			Form f = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), true),
					survey.getLanguage(), resources, contextpath);
			sessionService.setFormStartDate(request, f, uniqueCode);
			f.getAnswerSets().add(answerSet);
			f.setWcagCompliance(answerSet.getWcagMode() != null && answerSet.getWcagMode());

			ModelAndView model = new ModelAndView("management/test", "form", f);

			model.addObject("submit", true);
			model.addObject(Constants.UNIQUECODE, uniqueCode);
			model.addObject("invisibleElements", invisibleElements);

			return model;
		} else if (newviewpost != null && newviewpost.equalsIgnoreCase("true")) {
			survey = surveyService.getSurvey(survey.getId(), newlang);
			Form f = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), true),
					survey.getLanguage(), resources, contextpath);
			sessionService.setFormStartDate(request, f, uniqueCode);
			f.getAnswerSets().add(answerSet);
			if (newcss != null && newcss.equalsIgnoreCase("wcag")) {
				answerSet.setWcagMode(true);
			} else if (newcss != null && newcss.equalsIgnoreCase("standard")) {
				answerSet.setWcagMode(false);
			}
			f.setWcagCompliance(answerSet.getWcagMode() != null && answerSet.getWcagMode());
			ModelAndView model = new ModelAndView("management/test", "form", f);

			model.addObject("submit", true);
			model.addObject(Constants.UNIQUECODE, uniqueCode);
			model.addObject("invisibleElements", invisibleElements);

			return model;
		}

		if (validation.size() > 0) {
			// load form
			if (request.getParameter("language.code") != null && request.getParameter("language.code").length() == 2) {
				survey = surveyService.getSurvey(survey.getId(), lang);
			}
			Form f = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), true),
					survey.getLanguage(), resources, contextpath);
			sessionService.setFormStartDate(request, f, uniqueCode);
			f.getAnswerSets().add(answerSet);
			f.setWcagCompliance(answerSet.getWcagMode() != null && answerSet.getWcagMode());
			f.setValidation(validation);

			SurveyHelper.recreateUploadedFiles(answerSet, survey, fileService, answerExplanationService);

			ModelAndView model = new ModelAndView("management/test", "form", f);
			model.addObject("submit", true);
			model.addObject(Constants.UNIQUECODE, uniqueCode);

			String message = resources.getMessage("error.CheckValidation", null, "Please check for validation errors.",
					locale);
			model.addObject(Constants.MESSAGE, message);

			return model;
		}

		if (answerSet.getSurvey().getCaptcha() && !checkCaptcha(request)) {
			if (request.getParameter("language.code") != null && request.getParameter("language.code").length() == 2) {
				survey = surveyService.getSurvey(survey.getId(), lang);
			}
			Form f = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), true),
					survey.getLanguage(), resources, contextpath);
			sessionService.setFormStartDate(request, f, uniqueCode);
			f.getAnswerSets().add(answerSet);
			ModelAndView model = new ModelAndView("management/test", "form", f);
			model.addObject("submit", true);
			model.addObject(Constants.UNIQUECODE, uniqueCode);
			model.addObject("wrongcaptcha", "true");
			return model;
		}

		if (survey.isAnonymous()) {
			answerSet.setResponderEmail(Tools.md5hash(user.getEmail()));
		} else {
			answerSet.setResponderEmail(user.getEmail());
		}

		saveAnswerSet(answerSet, fileDir, request.getParameter("draftid"), -1, request);

		survey = surveyService.getSurvey(survey.getId(), lang);

		if (survey.getIsQuiz()) {
			ModelAndView result = new ModelAndView("management/testQuizResult", Constants.UNIQUECODE,
					answerSet.getUniqueCode());
			Form form = new Form(resources, surveyService.getLanguage(locale.getLanguage().toUpperCase()),
					translationService.getActiveTranslationsForSurvey(answerSet.getSurvey().getId()), contextpath);
			sessionService.setFormStartDate(request, form, uniqueCode);
			form.setSurvey(survey);
			form.getAnswerSets().add(answerSet);

			result.addObject("invisibleElements", invisibleElements);
			result.addObject(form);
			result.addObject("surveyprefix", survey.getId());
			result.addObject("quiz", QuizHelper.getQuizResult(answerSet, invisibleElements));
			result.addObject("isquizresultpage", true);
			return result;
		}

		ModelAndView result = new ModelAndView("thanksloggedin", Constants.UNIQUECODE, answerSet.getUniqueCode());

		if (!survey.isAnonymous() && answerSet.getResponderEmail() != null) {
			result.addObject("participantsemail", answerSet.getResponderEmail());
		}

		result.addObject("isthankspage", true);
		result.addObject("runnermode", true);
		result.addObject("text", survey.getConfirmationPage());

		Form form = new Form(resources, surveyService.getLanguage(locale.getLanguage().toUpperCase()),
				translationService.getActiveTranslationsForSurvey(answerSet.getSurvey().getId()), contextpath);
		sessionService.setFormStartDate(request, form, uniqueCode);
		form.setSurvey(survey);
		
		form.getAnswerSets().add(answerSet);
		
		result.addObject(form);

		if (survey.getConfirmationPageLink() != null && survey.getConfirmationPageLink()
				&& survey.getConfirmationLink() != null && survey.getConfirmationLink().length() > 0) {
			result.addObject("redirect", form.getFinalConfirmationLink(lang, answerSet));
		}
		result.addObject("surveyprefix", survey.getId());
		return result;
	}

	@RequestMapping(value = "/recalculateScore")
	public ModelAndView recalculateScore(@PathVariable String shortname, @RequestParam String id,
			HttpServletRequest request, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenURLException {
		Survey survey = null;
		User u = sessionService.getCurrentUser(request);

		survey = surveyService.getSurvey(Integer.parseInt(id));
		sessionService.upgradePrivileges(survey, u, request);

		if (!u.getId().equals(survey.getOwner().getId())
				&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
				&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1
				&& u.getLocalPrivileges().get(LocalPrivilege.AccessDraft) < 2) {
			throw new ForbiddenURLException();
		}

		RecalculateScoreExecutor recalculateScoreExecutor = (RecalculateScoreExecutor) context
				.getBean("recalculateScoreExecutor");
		recalculateScoreExecutor.init(survey.getId());
		taskExecutorLong.execute(recalculateScoreExecutor);

		return new ModelAndView(
				"redirect:/" + survey.getShortname() + "/management/results?message=recalculatestarted");
	}

	@RequestMapping(value = "/results/access", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> results_access_log(@PathVariable String shortname, HttpServletRequest request, Locale locale, @RequestParam("type") String type)
			throws Exception {
		Survey survey = null;
		User u = sessionService.getCurrentUser(request);

		survey = surveyService.getSurveyByShortname(shortname, true, u, request, false, true, true, false);
		sessionService.upgradePrivileges(survey, u, request);
		if (!u.getId().equals(survey.getOwner().getId())
				&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
				&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1
				&& u.getLocalPrivileges().get(LocalPrivilege.AccessDraft) < 2
				&& u.getResultAccess() == null) {
			throw new ForbiddenURLException();
		}

		activityService.log(ActivityRegistry.ID_RESULTS_ACCESS, null, type, u.getId(), survey.getUniqueId());

		return new ResponseEntity<Object>(null,HttpStatus.OK);
	}

	@RequestMapping(value = "/results")
	public ModelAndView results(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws Exception {
		Survey survey = null;
		User u = sessionService.getCurrentUser(request);

		survey = surveyService.getSurveyByShortname(shortname, true, u, request, false, true, true, false);
		sessionService.upgradePrivileges(survey, u, request);
		if (!u.getId().equals(survey.getOwner().getId())
				&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
				&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1
				&& u.getLocalPrivileges().get(LocalPrivilege.AccessDraft) < 2
				&& u.getResultAccess() == null) {
			throw new ForbiddenURLException();
		}

		return results(survey, Ucs2Utf8.requestToHashMap(request), request, null, false, null, false);
	}

	private ModelAndView results(Survey survey, Map<String, String[]> parameters, HttpServletRequest request,
			Boolean draft, Boolean pallanswers, ResultFilter resultFilter, boolean forPDF) throws Exception {
		String shortname = survey.getShortname();
		String languagecode = survey.getLanguage().getCode();
		String uid = survey.getUniqueId();

		User user = sessionService.getCurrentUser(request);

		boolean active = !survey.getIsDraft();
		if (!active)
			active = survey.getIsPublished();

		if (request != null && request.getParameter("results-source") != null) {
			active = request.getParameter("results-source").equalsIgnoreCase("active");
		}
		
		boolean published = survey.getIsActive() && survey.getIsPublished();

		boolean showAssignedValues = false;
		if (request != null && request.getParameter("dialog-show-assigned-values") != null)
			showAssignedValues = request.getParameter("dialog-show-assigned-values").equalsIgnoreCase("true");

		boolean allanswers = request != null && request.getParameter("results-source") != null
				&& request.getParameter("results-source").equalsIgnoreCase("allanswers");

		if (pallanswers)
			allanswers = true;

		if (allanswers)
			active = true;

		if (draft != null)
			active = !draft;

		if (request != null) {
			if (request.getMethod().equalsIgnoreCase("POST")) {
				request.getSession().setAttribute(uid + "lastSource", request.getParameter("results-source"));
			} else {
				String lastSource = (String) request.getSession().getAttribute(uid + "lastSource");
				if (lastSource != null) {
					switch (lastSource) {
					case "allanswers":
						allanswers = true;
						active = true;
						break;
					case "active":
						allanswers = false;
						active = true;
						break;
					default:
						allanswers = false;
						active = false;
						break;
					}
				}
			}
		}

		boolean multidelete = request != null && request.getParameter("operation") != null
				&& request.getParameter("operation").equalsIgnoreCase("multidelete");
		
		boolean deletecolumn = request != null && request.getParameter("operation") != null
				&& request.getParameter("operation").equalsIgnoreCase("deletecolumn"); 
		
		List<String> deletedAnswers = null;

		if (active) {
			survey = surveyService.getSurvey(uid, false, false, false, false, languagecode, true, true);

			if (survey == null) {
				survey = surveyService.getSurvey(shortname, false, false, false, false, languagecode, true, true);
			}

			if (survey == null) {
				active = false;
				survey = surveyService.getSurvey(shortname, true, false, false, false, null, true, true);
			} else if (request != null && !user.getId().equals(survey.getOwner().getId())
					&& user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& user.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1
					&& user.getResultAccess() == null) {
				active = false;
				allanswers = false;
				survey = surveyService.getSurvey(shortname, true, false, false, false, null, true, true);
			}

		} else {
			survey = surveyService.getSurvey(shortname, true, false, false, false, null, true, true);
		}

		ResultFilter filter = user != null ? sessionService.getLastResultFilter(request, user.getId(), survey.getId()) : null;

		if (resultFilter != null)
			filter = resultFilter;
		
		String oldActive = null;
		String oldAllAnswers = null;
		if (request != null) {
			oldActive = request.getParameter("active");
			oldAllAnswers = request.getParameter("allAnswers");
		}

		boolean ignorePostParameters = oldActive != null && oldAllAnswers != null
				&& ((oldActive.equalsIgnoreCase("true") && !active) || (oldActive.equalsIgnoreCase("false") && active)
						|| (oldAllAnswers.equalsIgnoreCase("true") && !allanswers)
						|| (oldAllAnswers.equalsIgnoreCase("false") && allanswers));

		if (filter == null || filter.getSurveyId() != survey.getId()) {
			filter = new ResultFilter();
		} else if (filter == null || filter.getSurveyId() != survey.getId()
				|| (request != null && request.getMethod().equalsIgnoreCase("POST") && !ignorePostParameters)
				|| parameters.containsKey("reset")) {
			filter.clearResultFilter();
		}

		boolean filtered = false;
		final String SELECTEDEXPLANATION = "selectedexplanation"; 
		final String EXPORTSELECTEDEXPLANATION = "exportselectedexplanation";
		final String SELECTEDDISCUSSION = "selecteddiscussion"; 
		final String EXPORTSELECTEDDISCUSSION = "exportselecteddiscussion";
		
		if (!ignorePostParameters) {
			if (request != null && request.getMethod().equalsIgnoreCase("POST")) {
				filter.clearSelectedQuestions();
			}

			for (Entry<String, String[]> entry : parameters.entrySet()) {
				String v = entry.getValue()[0];

				if (v != null && v.trim().length() > 0) {
					final String key = entry.getKey();
					if (key.equalsIgnoreCase("metafilterinvitation")) {
						filter.setInvitation(parameters.get("metafilterinvitation")[0].trim());
						filtered = true;
					} else if (key.equalsIgnoreCase("metafiltercase")) {
						filter.setCaseId(parameters.get("metafiltercase")[0].trim());
						filtered = true;
					} else if (key.equalsIgnoreCase("metafilteruser")) {
						filter.setUser(parameters.get("metafilteruser")[0].trim());
						filtered = true;
					} else if (key.equalsIgnoreCase("metafilterdatefrom")) {
						filter.setGeneratedFrom(
								ConversionTools.getDate(parameters.get("metafilterdatefrom")[0].trim()));
						filtered = true;
					} else if (key.equalsIgnoreCase("metafilterdateto")) {
						filter.setGeneratedTo(ConversionTools.getDate(parameters.get("metafilterdateto")[0].trim()));
						filtered = true;
					} else if (key.equalsIgnoreCase("metafilterupdatefrom")) {
						filter.setUpdatedFrom(
								ConversionTools.getDate(parameters.get("metafilterupdatefrom")[0].trim()));
						filtered = true;
					} else if (key.equalsIgnoreCase("metafilterupdateto")) {
						filter.setUpdatedTo(ConversionTools.getDate(parameters.get("metafilterupdateto")[0].trim()));
						filtered = true;
					} else if (key.equalsIgnoreCase("metafilterlanguage")) {
						Set<String> languages = new HashSet<>();
						String[] langs = request.getParameterValues("metafilterlanguage");
						if (langs != null && langs.length > 0) {
							Collections.addAll(languages, langs);
						}
						filter.setLanguages(languages);
						filtered = true;
					} else if (key.startsWith(Constants.FILTER)) {
						String questionId = key.substring(6);
						String[] values = entry.getValue();
						String value = StringUtils.arrayToDelimitedString(values, ";");
						filter.getFilterValues().put(questionId, value);
						filtered = true;
					} else if (key.startsWith(SELECTEDEXPLANATION)) {
						filter.getVisibleExplanations().add(key.substring(SELECTEDEXPLANATION.length()));
					} else if (key.startsWith(EXPORTSELECTEDEXPLANATION)) {
						filter.getExportedExplanations().add(key.substring(EXPORTSELECTEDEXPLANATION.length()));
					} else if (key.startsWith(SELECTEDDISCUSSION)) {
						filter.getVisibleDiscussions().add(key.substring(SELECTEDDISCUSSION.length()));
					} else if (key.startsWith(EXPORTSELECTEDDISCUSSION)) {
						filter.getExportedDiscussions().add(key.substring(EXPORTSELECTEDDISCUSSION.length()));		
					} else if (key.startsWith("selected")) {
						filter.getVisibleQuestions().add(key.substring(8));
					} else if (key.startsWith("exportselected")) {
						filter.addExportedQuestion(key.substring(14));
					} else if (key.equalsIgnoreCase("sort")) {
						String sorting = entry.getValue()[0].trim();
						if (sorting.equalsIgnoreCase("scoreDesc")) {
							filter.setSortKey("score");
							filter.setSortOrder("desc");
						} else if (sorting.equalsIgnoreCase("scoreAsc")) {
							filter.setSortKey("score");
							filter.setSortOrder("asc");
						}
					}
				}
			}
		}
		
		if (user != null && user.getResultAccess() != null &&  user.getResultAccess().getResultFilter() != null)
		{
			filter.getReadOnlyFilterQuestions().clear();			
			filter.mergeResultAccess(user.getResultAccess(), survey);
		}

		if (request != null) {
			request.getSession().setAttribute("results-source-active", active);
			request.getSession().setAttribute("results-source-allanswers", allanswers);
			request.getSession().setAttribute("resultsShowAssignedValues", showAssignedValues);
		}

		if (multidelete) {
			if (!user.getId().equals(survey.getOwner().getId())
					&& user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& user.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 2) {
				throw new ForbiddenURLException();
			}

			List<String> answerSetsToDelete = new ArrayList<>();
			if (request.getParameter("check-all-delete") != null
					&& (request.getParameter("check-all-delete").equalsIgnoreCase("true")
							|| request.getParameter("check-all-delete").equalsIgnoreCase("on"))) {
				deletedAnswers = answerService.deleteAnswers(survey.getId(), filter);
			} else {
				Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
				for (String key : parameterMap.keySet()) {
					if (key.startsWith("delete")) {
						String uniqueCode = key.substring(6);
						answerSetsToDelete.add(uniqueCode);
					}
				}
				deletedAnswers = answerService.deleteAnswers(answerSetsToDelete, survey.getId());
			}

			for (String uniqueCode : deletedAnswers) {
				if (survey.getIsDraft()) {
					activityService.log(ActivityRegistry.ID_TEST_DELETE, uniqueCode, null, sessionService.getCurrentUser(request).getId(),
							survey.getUniqueId());
				} else {
					activityService.log(ActivityRegistry.ID_CONTRIBUTION_DELETE, null, uniqueCode, sessionService.getCurrentUser(request).getId(),
							survey.getUniqueId());
				}
			}
		}
		
		boolean columnDeleted = false;
		
		if (deletecolumn) {
			if (!user.getId().equals(survey.getOwner().getId())
					&& user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& user.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 2) {
				throw new ForbiddenURLException();
			}
			
			String questionUID = request.getParameter("deleteColumnUID");
			
			if (questionUID != null) {
				if (questionUID.contains("#"))
				{
					// a table
					String suffix = questionUID.substring(questionUID.indexOf("#")+1);
					questionUID = questionUID.substring(0, questionUID.indexOf("#"));
					
					String row = suffix.substring(0, suffix.indexOf("#"));
					String col = suffix.substring(suffix.indexOf("#")+1);
					
					Table table = (Table)survey.getQuestionMapByUniqueId().get(questionUID);
					String quid = table.getQuestions().get(Integer.parseInt(row)-1).getUniqueId();
					String auid = table.getAnswers().get(Integer.parseInt(col)-1).getUniqueId();
					
					answerService.clearAnswersForQuestion(survey, filter, quid, auid, user.getId());					
				} else {				
					answerService.clearAnswersForQuestion(survey, filter, questionUID, null, user.getId());
				}
				columnDeleted = true;
			}
		}

		if (filter.getVisibleQuestions().isEmpty()) {
			// preselect first 20 questions
			int counter = 0;
			for (Element question : survey.getQuestions()) {
				if (question.isUsedInResults()) {
					if (counter < 20) {
						filter.getVisibleQuestions().add(question.getId().toString());
					} else {
						break;
					}
					counter++;
				}
			}
		}
		if (filter.getExportedQuestions().isEmpty()) {
			// preselect ALL
			for (Element question : survey.getQuestionsAndSections()) {
				if (question.isUsedInResults()) {
					filter.getExportedQuestions().add(question.getId().toString());
				}
			}
		}

		if (request != null) {
			filter.setSurveyId(survey.getId());
		}

		Integer itemsPerPage = 50;
		if (request != null)
			itemsPerPage = ConversionTools.getInt(request.getParameter("itemsPerPage"), 50);

		Paging<AnswerSet> paging = new Paging<>();
		paging.setItemsPerPage(itemsPerPage);
		String newPage = null;

		if (request != null)
			newPage = request.getParameter("newPage");
		paging.moveTo(newPage == null ? "first" : newPage);

		List<AnswerSet> answerSets = new ArrayList<>();

		if (active && allanswers) {
			if (!survey.isMissingElementsChecked())
				surveyService.checkAndRecreateMissingElements(survey, filter);
		} else {
			survey.clearMissingData();
		}

		Form form = new Form(resources);
		form.setSurvey(survey);

		List<Translations> lTrans = translationService.getTranslationsForSurvey(survey.getId(), true);
		Map<String, Language> languages = new HashMap<>();

		for (Translations translation : lTrans) {
			languages.put(translation.getLanguage().getCode(), translation.getLanguage());
		}

		form.setLanguages(languages);
		form.setCodaEnabled(settingsService.get("Coda").equals("true"));
		
		paging.setItems(answerSets);

		ModelAndView result = new ModelAndView("management/results", "form", form);
		result.addObject("paging", paging);
		result.addObject("active", active);
		result.addObject("published", published);
		result.addObject("allanswers", allanswers);
		result.addObject("filtered", filtered);
		if (user != null) {
			result.addObject("userid", user.getId());
		}
		
		if (columnDeleted) {
			result.addObject("columnDeleted", true);
		}

		if (forPDF) {
			Statistics statistics = answerService.getStatisticsOrStartCreator(survey, filter, false, active && allanswers, false);
			result.addObject("statistics", statistics);
			Set<String> newids = new HashSet<>();
			newids.addAll(filter.getExportedQuestions());
			filter.setVisibleQuestions(newids);
		}
		
		if (survey.getIsECF()) {
			// Set<String> newids = new HashSet<>();
			// newids.addAll(filter.getExportedQuestions());
			// filter.setVisibleQuestions(newids);
			SqlPagination sqlPagination = new SqlPagination(1, 10);
			Set<ECFProfile> ecfProfiles = this.ecfService.getECFProfiles(survey);
			result.addObject("ecfProfiles", ecfProfiles.stream().sorted().collect(Collectors.toList()));
			
			ECFGlobalResult ecfGlobalResult = this.ecfService.getECFGlobalResult(survey, sqlPagination);
			ECFSummaryResult ecfSummaryResult = this.ecfService.getECFSummaryResult(survey);
			ECFProfileResult ecfProfileResult = this.ecfService.getECFProfileResult(survey);
			ECFOrganizationalResult ecfOrganizationalResult = this.ecfService.getECFOrganizationalResult(survey);
			result.addObject("ecfGlobalResult", ecfGlobalResult);
			result.addObject("ecfProfileResult", ecfProfileResult);
			result.addObject("ecfOrganizationalResult", ecfOrganizationalResult);
			result.addObject("ecfSummaryResult", ecfSummaryResult);
			
			result.addObject("surveyShortname", shortname);
		}

		result.addObject(Constants.FILTER, filter);

		if (request != null) {
			request.getSession().setAttribute("resultsform", form);

			if (!ignorePostParameters && !request.getMethod().equalsIgnoreCase("GET")
					&& (request.getParameter("resultsFormMode") == null
							|| request.getParameter("resultsFormMode").length() == 0)) {
				result.addObject("reloadScrollPosition", "true");
			}

			if (multidelete) {
				result.addObject("deletedAnswers", deletedAnswers.size());
			}
		}

		String resultType = null;
		if (request != null)
			resultType = request.getParameter("resultType");
		resultType = resultType == null ? "content" : resultType;

		// this is for security (prevent xss attack)
		if (!ArrayUtils.contains(KNOWN_RESULTTYPES, resultType.toLowerCase())){
			resultType = "content";
		}

		result.addObject("resultType", resultType);
		result.addObject("questionswithuploadedfiles", answerService.getQuestionsWithUploadedFiles(survey));
		if (active) {
			int answers = 0;
			if (this.isReportingDatabaseEnabled()) {
				answers = reportingService.getCount(false, survey.getUniqueId());
			} else {
				answers = surveyService.getNumberPublishedAnswersFromMaterializedView(survey.getUniqueId());
			}
			if (answers > 100000)
				result.addObject("skipstatistics", true);
		}

		String message = null;
		if (request != null)
			message = request.getParameter(Constants.MESSAGE);
		if (message != null && message.length() > 0) {
			result.addObject(Constants.MESSAGE, message);
		}

		if (request != null) {
			try {
				sessionService.setLastResultFilter(request, filter, user.getId(), survey.getId());
			} catch (Exception e) {
				logger.warn(e.getLocalizedMessage(), e);
			}
			
			if (request.getMethod().equalsIgnoreCase("GET")) {
				activityService.log(ActivityRegistry.ID_RESULTS_ACCESS, null, "content", user.getId(), survey.getUniqueId());
			}
		}
		result.addObject("contextpath", contextpath);
		
		return result;
	}
	
	@RequestMapping(value = "/seatCounting", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody SeatCounting seatCounting(HttpServletRequest request, Locale locale) {
		try {
			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1) {
				throw new ForbiddenURLException();
			}
			
			String uid = request.getParameter("surveyuid");
			if (!uid.equals(form.getSurvey().getUniqueId())) {
				return null;
			}
			
			activityService.log(ActivityRegistry.ID_DISPLAY_RESULTS, null, null, u.getId(), uid);
			SeatCounting result = eVoteService.getCounting(form.getSurvey().getUniqueId(), null, resources, locale, false);
		
			return result;
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}
	
	@RequestMapping(value = "/seatAllocation", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody SeatCounting seatAllocation(HttpServletRequest request, Locale locale) {
		try {
			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1) {
				throw new ForbiddenURLException();
			}
			
			String uid = request.getParameter("surveyuid");
			if (!uid.equals(form.getSurvey().getUniqueId())) {
				return null;
			}
			
			activityService.log(ActivityRegistry.ID_ALLOCATE_SEATS, null, null, u.getId(), uid);
			SeatCounting result = eVoteService.getCounting(form.getSurvey().getUniqueId(), null, resources, locale, true);
		
			return result;
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}
	
	@RequestMapping(value = "/seatTestDownload", method = { RequestMethod.GET, RequestMethod.HEAD })
	@ResponseBody
	public ResponseEntity<byte[]> seatTestDownload(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		final org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();

		try {

			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 2) {
				throw new ForbiddenURLException();
			}
			
			SeatCounting result = eVoteService.getCounting(form.getSurvey().getUniqueId(), null, resources, locale, true);
			byte[] file = XlsxExportCreator.createSeatTestSheet(result);

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment;filename=seattest.xlsx");
			response.setContentLength(file.length);
			response.getOutputStream().write(file);			
			response.getOutputStream().flush();
			response.getOutputStream().close();

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<>(e.getLocalizedMessage().getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return null;
	}
	
	@PostMapping(value = "/uploadSeatTest")
	public @ResponseBody eVoteResults uploadSeatTest(@PathVariable String shortname, HttpServletRequest request, HttpServletResponse response) {
		InputStream is = null;
		
		try {

			if (request instanceof DefaultMultipartHttpServletRequest) {
				DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest) request;
				is = r.getFile("qqfile").getInputStream();
			} else {
				is = request.getInputStream();
			}

			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 2) {
				throw new ForbiddenURLException();
			}
			
			eVoteResults results = eVoteService.importSeatTestSheet(form.getSurvey(), is);	
			response.setStatus(HttpServletResponse.SC_OK);
			return results;	
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(ex.getMessage(), ex);
		} finally {
			try {
				is.close();
			} catch (IOException ignored) {
				// ignore
			}
		}

		return null;
	}

	@RequestMapping(value = "/seatTestPossible", method = { RequestMethod.GET, RequestMethod.HEAD })
	@ResponseBody
	public ResponseEntity<byte[]> seatTestPossible(HttpServletRequest request) {
		final org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		Form form;

		try {
			form = sessionService.getForm(request, null, false, false);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<>(e.getLocalizedMessage().getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (form != null) {
			String uid = request.getParameter("surveyuid");
			if (!uid.equals(form.getSurvey().getUniqueId())) {
				return new ResponseEntity<>(("").getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return null;
	}
	
	@RequestMapping(value = "/seatExport", method = { RequestMethod.GET, RequestMethod.HEAD })
	@ResponseBody
	public ResponseEntity<byte[]> seatExport(HttpServletRequest request, HttpServletResponse response) {
		final org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();

		try {

			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 2) {
				throw new ForbiddenURLException();
			}
			
			activityService.log(ActivityRegistry.ID_EXPORT_SEATS, null, null, u.getId(), form.getSurvey().getUniqueId());
			
			SeatCounting result = null;
			String testdata = request.getParameter("testdata");
			String surveyUid = request.getParameter("surveyuid");
			if (testdata != null && testdata.equalsIgnoreCase("true")) {
				result = (SeatCounting) request.getSession().getAttribute(LastEVoteTestResult + surveyUid);
			}
			if (result == null) {
				result = eVoteService.getCounting(form.getSurvey().getUniqueId(), null, resources, new Locale("EN"), true);
			}
			byte[] file = XlsxExportCreator.createSeatContribution(result, resources);

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment;filename=seatdistribution.xlsx");
			response.setContentLength(file.length);
			response.getOutputStream().write(file);			
			response.getOutputStream().flush();
			response.getOutputStream().close();

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<>(e.getLocalizedMessage().getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return null;
	}
	
	private int getValue(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null)
		{
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				//ignore
			}
		}
		
		return 0;
	}
	
	@RequestMapping(value = "/seatCountingTest", method = { RequestMethod.POST })
	public @ResponseBody SeatCounting seatCountingTest(HttpServletRequest request, Locale locale) {
		try {
			Form form = sessionService.getForm(request, null, false, false);
			User u = sessionService.getCurrentUser(request);
			sessionService.upgradePrivileges(form.getSurvey(), u, request);

			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1) {
				throw new ForbiddenURLException();
			}
			
			eVoteResults results = eVoteService.getEmptyListResult(form.getSurvey());
			results.setBlankVotes(getValue(request, "blankvotes"));
			results.setSpoiltVotes(getValue(request, "spoiltvotes"));
			results.setPreferentialVotes(getValue(request, "preferentialvotes"));
			
			for (Entry<String, eVoteListResult> listResultEntry : results.getLists().entrySet()) { // key is uid of the question
				eVoteListResult listResult = listResultEntry.getValue();
				listResult.setListVotes(getValue(request, "listvotes" + listResultEntry.getKey()));
				
				Set<String> pauids = listResultEntry.getValue().getCandidateVotes().keySet();
				int i = 1;
				int luxListVotes = 0;
				for (String pauid : pauids) {
					int v = getValue(request, listResultEntry.getKey() + "-" + i++);
					listResultEntry.getValue().getCandidateVotes().put(pauid, v);
					luxListVotes += v;
				}
				
				listResult.setLuxListVotes(luxListVotes);
			}
			
			SeatCounting result = eVoteService.getCounting(form.getSurvey().getUniqueId(), results, resources, locale, true);
			
			if (locale.getLanguage().equals("en")) {
				request.getSession().setAttribute(LastEVoteTestResult + form.getSurvey().getUniqueId(), result);
			} else {
				request.getSession().setAttribute(LastEVoteTestResult + form.getSurvey().getUniqueId(), eVoteService.getCounting(form.getSurvey().getUniqueId(), results, resources, new Locale("EN"), true));
			}
			
			return result;
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	@RequestMapping(value = "/resultsJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<String> resultsJSON(@PathVariable String shortname, HttpServletRequest request) {

		try {
			User user = sessionService.getCurrentUser(request);

			String rows = request.getParameter("rows");
			String page = request.getParameter("page");
			String discussionSortingOption = request.getParameter("discussionSortingOption");

			if (page == null || rows == null) {
				return null;
			}

			int itemsPerPage = Integer.parseInt(rows);

			ResultFilter filter = sessionService.getLastResultFilter(request);

			Form form = (Form) request.getSession().getAttribute("resultsform");

			String ppublicationmode = request.getParameter("publicationmode");
			boolean publicationmode = ppublicationmode != null && ppublicationmode.equalsIgnoreCase("true");

			boolean showuploadedfiles = true;

			if (publicationmode) {
				filter = null;
				ResultFilter userFilter = (ResultFilter) request.getSession()
						.getAttribute("lastPublishedFilter" + shortname);
				if (userFilter != null)
					filter = userFilter;
				Survey survey = surveyService.getSurvey(shortname, false, false, false, false, null, true, true);
				form = new Form(resources);
				form.setSurvey(survey);

				if (survey.getPublication() != null) {
					showuploadedfiles = survey.getPublication().getShowUploadedDocuments();
				}

				if (filter == null) {
					if (survey.getPublication() != null && survey.getPublication().isActive()) {
						filter = survey.getPublication().getFilter();
						filter.setSurveyId(survey.getId());
					} else {
						return null;
					}
				}
			}

			boolean isOwner = user != null && form.getSurvey() != null
					&& Objects.equals(form.getSurvey().getOwner().getId(), user.getId());

			List<AnswerSet> answerSets;

			SqlPagination sqlPagination = new SqlPagination(Integer.parseInt(page), itemsPerPage);

			Survey draft = surveyService.getSurvey(shortname, true, false, false, false, null, true, false);
			Survey survey = surveyService.getSurvey(filter.getSurveyId(), draft.getLanguage().getCode());

			if (user != null) {
				sessionService.upgradePrivileges(draft, user, request);
			}
			
			boolean active = publicationmode || (boolean) request.getSession().getAttribute("results-source-active");
			boolean allanswers = !publicationmode
					&& (boolean) request.getSession().getAttribute("results-source-allanswers");

			List<String> result = new ArrayList<>();

			if (active && allanswers) {
				surveyService.checkAndRecreateMissingElements(survey, filter);
			} else {
				survey.clearMissingData();
			}

			boolean addlinks = isOwner || user == null || user.getFormPrivilege() > 1
					|| user.getLocalPrivilegeValue("AccessResults") > 1
					|| (form.getSurvey().getIsDraft() && user.getLocalPrivilegeValue("AccessDraft") > 0);
			filter = answerService.initialize(filter);			
			
			List<List<String>> answersets = reportingService.getAnswerSets(survey, filter, sqlPagination, addlinks,
					false, showuploadedfiles, false, false, true);
			 
			if (answersets != null) {
				Date updateDate = reportingService.getLastUpdate(survey);
				result.add(updateDate == null ? "" : ConversionTools.getFullString(updateDate));
				for (List<String> row : answersets) {
					result.addAll(row);
				}
				return result;
			}
			
			Map<String, String> usersByUid = answerExplanationService.getUserAliases(survey.getUniqueId());

			answerSets = answerService.getAnswers(survey, filter, sqlPagination, false, true, active && !allanswers);

			boolean surveyExists = survey != null;

			if (!surveyExists) {
				logger.error("Survey not set: " + request.getRequestURL());
			}
			
			List<Element> visibleQuestions = new ArrayList<>();
			for (String id : filter.getVisibleQuestions()) {
				try {
					Element el = survey.getElementsById().get(Integer.parseInt(id));
					if (el instanceof Matrix) {
						visibleQuestions.add(el);
						visibleQuestions.addAll(((Matrix) el).getQuestions());
					} else if (el.isUsedInResults()) {
						visibleQuestions.add(el);
					}
				} catch (Exception e) {
					// ignore
				}
			}

			result.add("");

			for (AnswerSet answerSet : answerSets) {
				result.add(answerSet.getUniqueCode());
				result.add(answerSet.getId().toString());

				for (Question question : survey.getQuestions()) {
					if (visibleQuestions.contains(question) || survey.getMissingElements().contains(question)) {
						if (question instanceof Matrix) {
							for (Element matrixQuestion : ((Matrix) question).getQuestions()) {
								StringBuilder s = new StringBuilder();
								for (Answer answer : answerSet.getAnswers(
										matrixQuestion.getUniqueId())) {
									if (surveyExists) {
										answer.setTitle(form.getAnswerTitle(answer));
									}
									if (s.length() > 0) {
										s.append("; ");
									}
									s.append(answer.getTitle());
									
									s.append("<span class='assignedValue hideme'>").append(form.getAnswerShortname(answer))
									.append("</span>");
								}
								result.add(s.toString());
							}
						} else if (question instanceof Table) {
							Table table = (Table) question;
							for (int r = 1; r < table.getAllRows(); r++) {
								for (int c = 1; c < table.getAllColumns(); c++) {
									result.add(ConversionTools.escape(answerSet.getTableAnswer(question, r, c, false)));
								}
							}
						} else if (question instanceof ComplexTable) {
							ComplexTable table = (ComplexTable) question;
							for (ComplexTableItem childQuestion : table.getQuestionChildElements()) {
								StringBuilder s = new StringBuilder();
								for (Answer answer : answerSet.getAnswers(childQuestion.getUniqueId())) {
									if (s.length() > 0) {
										s.append("; ");
									}
									
									if (childQuestion.getCellType() == ComplexTableItem.CellType.SingleChoice || childQuestion.getCellType() == ComplexTableItem.CellType.MultipleChoice) {
										s.append(form.getAnswerTitle(answer));
									} else {
										s.append(ConversionTools.escape(form.getAnswerTitle(answer)));
									}
								}
								result.add(s.toString());
							}
						} else if (question instanceof GalleryQuestion) {
							GalleryQuestion gallery = (GalleryQuestion) question;
							StringBuilder s = new StringBuilder();
							
							for (Answer answer : answerSet.getAnswers(question.getUniqueId())) {
								File file = null;
								if (!StringUtils.isEmpty(answer.getPossibleAnswerUniqueId())) {
									file = gallery.getFileByUid(answer.getPossibleAnswerUniqueId());									
								} else {
									try {
										file = gallery.getFiles().get(Integer.parseInt(answer.getValue()));
									} catch (IndexOutOfBoundsException e) {
										//ignore
									}
								}
								
								if (file != null) {
									if (s.length() > 0) {
										s.append("; ");
									}
									s.append(file.getName());
								}
							}
							result.add(s.toString());
						} else if (question instanceof Upload) {
							if (showuploadedfiles) {
								StringBuilder s = new StringBuilder();
								for (Answer answer : answerSet.getAnswers(question.getUniqueId())) {
									for (File file : answer.getFiles()) {
										if (isOwner || user == null || user.getFormPrivilege() > 1
												|| user.getLocalPrivilegeValue("AccessResults") > 1
												|| (survey.getIsDraft()
														&& user.getLocalPrivilegeValue("AccessDraft") > 0)) {
											s.append("<a target='blank' href='").append(contextpath).append("/files/")
													.append(survey.getUniqueId()).append(Constants.PATH_DELIMITER)
													.append(file.getUid()).append("'>").append(file.getName())
													.append("</a><br />");
										} else {
											s.append(file.getName()).append("<br />");
										}
									}
								}
								result.add(s.toString());
							}
						} else if (question instanceof RatingQuestion) {
							for (Element childQuestion : ((RatingQuestion) question).getQuestions()) {
								StringBuilder s = new StringBuilder();
								for (Answer answer : answerSet.getAnswers(childQuestion.getUniqueId())) {
									if (s.length() > 0) {
										s.append("; ");
									}
									s.append(answer.getValue());
								}
								result.add(s.toString());
							}
						} else if (question instanceof com.ec.survey.model.survey.Image || question instanceof Text
								|| question instanceof Ruler || question instanceof Confirmation) {
							// these elements are not displayed
						} else {
							StringBuilder s = new StringBuilder();
							for (Answer answer : answerSet.getAnswers(question.getUniqueId())) {
								if (s.length() > 0) {
									s.append("; ");
								}

								if (question instanceof ChoiceQuestion || question instanceof RankingQuestion) {
									if ("EVOTE-ALL".equals(answer.getValue())){
										s.append(form.getMessage("label.EntireList"));
									} else {
										s.append(form.getAnswerTitle(answer));
									}
								} else {
									s.append(ConversionTools.escape(form.getAnswerTitle(answer)));
								}

								s.append("<span class='assignedValue hideme'>").append(form.getAnswerShortname(answer))
										.append("</span>");
							}
							result.add(s.toString());
						}
						
						if (survey.getIsDelphi() && question.getIsDelphiQuestion()) {

							if (filter.getVisibleExplanations().contains(question.getId().toString())) {
								try {
									final String explanation = answerExplanationService.getFormattedExplanationWithFiles(
											answerSet.getId(), question.getUniqueId(), survey.getUniqueId(), true);
									result.add(explanation);

									int likes = answerExplanationService.getLikesForExplanation(
											answerSet.getId(), question.getUniqueId());
									result.add(String.valueOf(likes));
								} catch (NoSuchElementException ex) {
									result.add("");
									result.add("");
								}
							}
							
							if (filter.getVisibleDiscussions().contains(question.getId().toString())) {
								try {
									String discussion = answerExplanationService.getDiscussion(answerSet.getId(), question.getUniqueId(), true, usersByUid);
									result.add(discussion);
								} catch (NoSuchElementException ex) {
									result.add("");
								}
							}
						}
					}
				}

				if (filter.getVisibleQuestions().contains("invitation")) {
					result.add(answerSet.getInvitationId() != null ? answerSet.getInvitationId() : "");
				}
				if (filter.getVisibleQuestions().contains("case")) {
					result.add(answerSet.getUniqueCode());
				}
				if (filter.getVisibleQuestions().contains("user")) {
					result.add(answerSet.getResponderEmail() != null ? answerSet.getResponderEmail() : "");
				}
				if (filter.getVisibleQuestions().contains("created")) {
					result.add(ConversionTools.getFullString(answerSet.getDate()));
				}
				if (filter.getVisibleQuestions().contains("updated")) {
					result.add(ConversionTools.getFullString(answerSet.getUpdateDate()));
				}
				if (filter.getVisibleQuestions().contains("languages")) {
					result.add(answerSet.getLanguageCode());
				}

				if (survey.getIsQuiz()) {
					result.add(answerSet.getScore() != null ? answerSet.getScore().toString() : "");
				}
			}

			return result;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	@RequestMapping(value = "/ecfGlobalResultsJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody ECFGlobalResult ecfGlobalResultsJSON(@PathVariable String shortname, HttpServletRequest request) 
			throws NotFoundException, BadRequestException, InternalServerErrorException, NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenException {
		// PARAMS
		String pageNumberOrNull = request.getParameter("pageNumber");
		String pageSizeOrNull = request.getParameter("pageSize");
		String profileComparisonOrNull = request.getParameter("profileComparison");
		String profileFilterOrNull = request.getParameter("profileFilter");
		String orderByOrNull = request.getParameter("orderBy");
		if (pageNumberOrNull == null || pageSizeOrNull == null) {
			throw new BadRequestException();
		}
		
		Integer pageNumber = Integer.valueOf(pageNumberOrNull);
		Integer pageSize = Integer.valueOf(pageSizeOrNull);
		

		SqlPagination sqlPagination = new SqlPagination(pageNumber, pageSize);
		
		ResultFilter filter = sessionService.getLastResultFilter(request);
		Survey survey = (filter != null) 
				? surveyService.getSurvey(filter.getSurveyId(), true) 
				: surveyService.getSurvey(shortname, false, true, false, false, null, true, false);
				
		this.sessionService.userIsResultReadAuthorized(survey, request);
		User user = this.sessionService.getCurrentUser(request);

		// ACTUAL CODE
		try {
			filter.setAnsweredECFProfileUID(profileFilterOrNull);
			filter.setCompareToECFProfileUID(profileComparisonOrNull);
			
			if (orderByOrNull != null) {
				ResultFilter.ResultFilterOrderBy resultFilterOrderBy = ResultFilter.ResultFilterOrderBy.parse(orderByOrNull);
				String ascOrDesc = resultFilterOrderBy.toAscOrDesc();
				String key = resultFilterOrderBy.toResultFilterSortKey().value();
				filter.setSortOrder(ascOrDesc);
				filter.setSortKey(key);
			}
			
			
			this.sessionService.setLastResultFilter(request, filter, user.getId(), survey.getId());
			return this.ecfService.getECFGlobalResult(survey, sqlPagination, filter);
		} catch (NotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	@RequestMapping(value = "/ecfProfileAssessmentResultsJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody ECFProfileResult ecfProfileAssessmentResultsJSON(@PathVariable String shortname, HttpServletRequest request) 
			throws NotFoundException, InternalServerErrorException, NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenException {
		String profileOrNull = request.getParameter("profile");

		ResultFilter filter = sessionService.getLastResultFilter(request);
		Survey survey = (filter != null) 
				? surveyService.getSurvey(filter.getSurveyId(), true) 
				: surveyService.getSurvey(shortname, false, true, false, false, null, true, false);
				
		filter.setCompareToECFProfileUID(profileOrNull);
		this.sessionService.userIsResultReadAuthorized(survey, request);
		
		try {
			this.sessionService.setLastResultFilter(request, filter, sessionService.getCurrentUser(request).getId(), survey.getId());
			return this.ecfService.getECFProfileResult(survey, filter);
		} catch (NotFoundException e) {
			throw e;
		} catch (Exception e1) {
			throw new InternalServerErrorException(e1);
		}
	}
	
	@RequestMapping(value = "/ecfOrganizationalResultsJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody ECFOrganizationalResult ecfOrganizationalResultsJSON(@PathVariable String shortname, HttpServletRequest request) 
			throws NotFoundException, BadRequestException, InternalServerErrorException, NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenException {
		ResultFilter filter = sessionService.getLastResultFilter(request);
		Survey survey = (filter != null) 
				? surveyService.getSurvey(filter.getSurveyId(), true) 
				: surveyService.getSurvey(shortname, false, true, false, false, null, true, false);
		this.sessionService.userIsResultReadAuthorized(survey, request);
		
		try {
			return this.ecfService.getECFOrganizationalResult(survey);
		} catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}

	@RequestMapping(value = "/ecfResultJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody ECFIndividualResult ecfResultJSON(@PathVariable String shortname, HttpServletRequest request)
	throws NotFoundException, InternalServerErrorException {
		String answerSetIdOrNull = request.getParameter("answerSetId");
		if (answerSetIdOrNull == null) {
			throw new NotFoundException();
		}
		AnswerSet answerSet = answerService.get(answerSetIdOrNull);

		ECFIndividualResult ecfResult;
		try {
			Survey survey = surveyService.getSurvey(shortname, false, true, false, false, null, true, false);
			ecfResult = this.ecfService.getECFIndividualResult(survey, answerSet);
		} catch (ECFException e) {
			throw new InternalServerErrorException(e);
		}

		return ecfResult;
	}

	@RequestMapping(value = "/statisticsJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody Statistics statisticsJSON(@PathVariable String shortname, HttpServletRequest request) {

		try {
			// Load latest filter from Sessions
			ResultFilter filter = sessionService.getLastResultFilter(request);

			String active = request.getParameter("active");
			String allanswers = request.getParameter("allanswers");
			String publicationmode = request.getParameter("publicationmode");
			String statisticsrequestid = request.getParameter("statisticsrequestid");

			if (statisticsrequestid == null) {
				if (publicationmode != null && publicationmode.equalsIgnoreCase("true")) {
					filter = null;
					ResultFilter userFilter = (ResultFilter) request.getSession()
							.getAttribute("lastPublishedFilter" + shortname);
					if (userFilter != null)
						filter = userFilter;
				}

				Survey survey = null;
				if (filter == null || filter.getSurveyId() == 0) {
					// can only happen in case of published results
					survey = surveyService.getSurvey(shortname, false, true, false, false, null, true, false);
					if (survey != null && survey.getPublication() != null && survey.getIsActive()
							&& survey.getPublication().isActive()) {
						filter = survey.getPublication().getFilter();
						filter.setSurveyId(survey.getId());
					} else {
						return null;
					}
				} else {
					survey = surveyService.getSurvey(filter.getSurveyId(), false, true);
				}

				if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("false")) {
					Statistics statistics = answerService.getStatisticsForFilterHash(survey.getId(),
							filter.getHash(active.equalsIgnoreCase("true") && allanswers.equalsIgnoreCase("true")),
							false);

					if (statistics != null) {
						return statistics;
					}

					StatisticsRequest statisticsRequest = new StatisticsRequest();
					statisticsRequest.setAllanswers(active.equalsIgnoreCase("true") && allanswers.equalsIgnoreCase("true"));
					statisticsRequest.setSurveyId(survey.getId());
					statisticsRequest.setFilter(filter.copy());
					answerService.saveStatisticsRequest(statisticsRequest);

					logger.info("calling worker server for statistics");

					try {
						URL workerurl = new URL(workerserverurl + "worker/startStatistics/" + statisticsRequest.getId());
						URLConnection wc = workerurl.openConnection();
						BufferedReader in = new BufferedReader(new InputStreamReader(wc.getInputStream()));
						String inputLine;
						StringBuilder result = new StringBuilder();
						while ((inputLine = in.readLine()) != null)
							result.append(inputLine);
						in.close();

						if (result.toString().equals("OK")) {
							Statistics s = new Statistics();
							s.setRequestID(statisticsRequest.getId());
							return s;
						} else {
							logger.error(
									"calling worker server for statisticsrequest " + statisticsRequest.getId() + " returned" + result);
							return null;
						}
					} catch (ConnectException e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}

				return answerService.getStatisticsOrStartCreator(survey, filter, false,
						active.equalsIgnoreCase("true") && allanswers.equalsIgnoreCase("true"), true);

			} else {
				return answerService.getStatistics(Integer.parseInt(statisticsrequestid));
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}
	
	@RequestMapping(value = "/statisticsDelphiJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody Map<String, String> statisticsDelphiJSON(@PathVariable String shortname, HttpServletRequest request) {
		
		try {
			ResultFilter filter = sessionService.getLastResultFilter(request);
			if (filter == null || filter.getSurveyId() == 0) {
				return Collections.emptyMap();
			}
			
			//the changes shall not be saved to the database 
			filter = filter.copy();
						
			filter.getVisibleExplanations().clear();
			filter.getVisibleDiscussions().clear();

			Survey survey = surveyService.getSurvey(filter.getSurveyId(), false, true);
			if (survey != null) {
				return answerService.getCompletionRates(survey, filter);
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return Collections.emptyMap();
	}
	
	@RequestMapping(value = "/statisticsDelphiMedianJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody Map<String, String> statisticsDelphiMedianJSON(@PathVariable String shortname, HttpServletRequest request) {
	
		try {
			ResultFilter filter = sessionService.getLastResultFilter(request);
			if (filter == null || filter.getSurveyId() == 0) {
				return Collections.emptyMap();
			}

			Survey survey = surveyService.getSurvey(filter.getSurveyId(), false, true);
			if (survey == null) {
			   return Collections.emptyMap();
			}

			Map<String, String> result = new HashMap<>();
			
			for (Element element : survey.getElements()) {
				if (element instanceof SingleChoiceQuestion) {
					SingleChoiceQuestion singleChoiceQuestion = (SingleChoiceQuestion)element;
					
					if (!singleChoiceQuestion.getUseLikert() || singleChoiceQuestion.getMaxDistance() <= -1) {
					   continue;
					}

					DelphiMedian median = answerService.getMedian(survey, singleChoiceQuestion, null, filter);
					if (median == null) {
						continue;
					}
					List<String> values = new ArrayList<>();
					for (String uid : median.getMedianUids()) {
						PossibleAnswer pa = singleChoiceQuestion.getPossibleAnswerByUniqueId(uid);
						if (pa != null) {
							values.add(pa.getStrippedTitle());
						}
					}
					if (!values.isEmpty()) {
						result.put(element.getUniqueId(), String.join(", ", values));
					}
				}
				
				if (element instanceof NumberQuestion) {
					NumberQuestion numberQuestion = (NumberQuestion)element;
					
					if (!numberQuestion.isSlider() || numberQuestion.getMaxDistance() <= -1) {
					   continue;
					}
					
					DelphiMedian median = answerService.getMedian(survey, numberQuestion, null, filter);
					if (median == null) {
						continue;
					}
					result.put(element.getUniqueId(), Double.toString(median.getMedian()));
				}
			}
			
			return result;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return Collections.emptyMap();
	}

	@RequestMapping(value = "/preparecharts/{id}/{exportId}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView preparecharts(@PathVariable String id, @PathVariable String exportId, Locale locale) {

		try {

			Export export = exportService.getExport(Integer.parseInt(exportId), false);
			if (export == null || export.getState() != ExportState.Pending
					|| !export.getSurvey().getId().equals(Integer.parseInt(id)))
				return null;

			Survey survey = surveyService.getSurvey(Integer.parseInt(id), false, true);

			surveyService.initializeSurvey(survey);

			ModelAndView results = results(survey, new HashMap<>(), null, survey.getIsDraft(), export.isAllAnswers(),
					export.getResultFilter(), true);

			Publication publication = new Publication();
			publication.setAllQuestions(false);
			publication.setFilter(export.getResultFilter());

			publication.getFilter().setVisibleQuestions(publication.getFilter().getExportedQuestions());
			results.addObject("publication", publication);

			if (export.getShowShortnames() != null && export.getShowShortnames()) {
				results.addObject("showShortnames", true);
			}

			results.setViewName("management/chartspdf");
			results.addObject("forpdf", true);
			return results;

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}
	/**
	 * Used to prepare a web page for PDF creation of the ECF Global Results
	 */
	@RequestMapping(value = "/prepareECFGlobalResults/{id}/{exportId}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView prepareECFGlobalResults(@PathVariable String id, @PathVariable String exportId, Locale locale, HttpServletRequest request) throws Exception {
		Export export = exportService.getExport(Integer.parseInt(exportId), false);
		if (export == null) {
			logger.error("export is null");
			return null;
		}
		
		if (export.getState() != ExportState.Pending) {
			logger.error("export state is " + export.getState());
			return null;
		}
		
		if (!export.getSurvey().getId().equals(Integer.parseInt(id))) {
			logger.error("mismatch: " + export.getSurvey().getId() + " : " + id );
			return null;
		}
		
		ECFGlobalResult ecfGlobalResult = null;
		return new ModelAndView("management/ecfGlobalResultsPDF", "ecfGlobalResult", ecfGlobalResult);
	}

	@RequestMapping(value = "/preparestatistics/{id}/{exportId}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView preparestatistics(@PathVariable String id, @PathVariable String exportId, Locale locale,
			HttpServletRequest request) throws Exception {

		Export export = exportService.getExport(Integer.parseInt(exportId), false);

		if (export == null) {
			logger.error("export is null");
			return null;
		}

		if (export.getState() != ExportState.Pending) {
			logger.error("export state is " + export.getState());
			return null;
		}

		if (!export.getSurvey().getId().equals(Integer.parseInt(id))) {
			logger.error("mismatch: " + export.getSurvey().getId() + " : " + id);
			return null;
		}

		Survey s = surveyService.getSurvey(Integer.parseInt(id), false, true);

		Survey survey = surveyService.getSurveyInOriginalLanguage(s.getId(), s.getShortname(), s.getUniqueId());

		surveyService.initializeSurvey(survey);

		ResultFilter filter = export.getResultFilter().copy();

		if (filter.getLanguages() != null && filter.getLanguages().isEmpty()) {
			filter.setLanguages(null);
		}

		ModelAndView results = results(survey, new HashMap<>(), null, survey.getIsDraft(), export.isAllAnswers(),
				filter, true);

		Publication publication = new Publication();
		publication.setFilter(export.getResultFilter());

		publication.getFilter().setVisibleQuestions(publication.getFilter().getExportedQuestions());

		publication.setAllQuestions(publication.getFilter().getVisibleQuestions().isEmpty());
		results.addObject("publication", publication);

		if (export.getShowShortnames() != null && export.getShowShortnames()) {
			results.addObject("showShortnames", true);
		}

		results.setViewName("management/statisticspdf");
		results.addObject("forpdf", true);
		return results;
	}
		
	@RequestMapping(value = "/preparepdfreport/{id}/{exportId}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView preparepdfreport(@PathVariable String id, @PathVariable String exportId, Locale locale,
			HttpServletRequest request) throws Exception {

		Export export = exportService.getExport(Integer.parseInt(exportId), false);

		if (export == null) {
			logger.error("export is null");
			return null;
		}

		if (export.getState() != ExportState.Pending) {
			logger.error("export state is " + export.getState());
			return null;
		}

		if (!export.getSurvey().getId().equals(Integer.parseInt(id))) {
			logger.error("mismatch: " + export.getSurvey().getId() + " : " + id);
			return null;
		}

		Survey s = surveyService.getSurvey(Integer.parseInt(id), false, true);

		Survey survey = surveyService.getSurveyInOriginalLanguage(s.getId(), s.getShortname(), s.getUniqueId());

		surveyService.initializeSurvey(survey);

		ResultFilter filter = export.getResultFilter().copy();

		if (filter.getLanguages() != null && filter.getLanguages().isEmpty()) {
			filter.setLanguages(null);
		}

		ModelAndView results = results(survey, new HashMap<>(), null, survey.getIsDraft(), export.isAllAnswers(),
				filter, true);

		Publication publication = new Publication();
		publication.setFilter(export.getResultFilter());

		publication.getFilter().setVisibleQuestions(publication.getFilter().getExportedQuestions());

		publication.setAllQuestions(publication.getFilter().getVisibleQuestions().isEmpty());
		results.addObject("publication", publication);

		if (export.getShowShortnames() != null && export.getShowShortnames()) {
			results.addObject("showShortnames", true);
		}

		results.setViewName("management/statisticspdf");
		results.addObject("forpdf", true);
		
		results.addObject("charts", export.getChartsByQuestionUID());
		
		return results;
	}

	@RequestMapping(value = "/preparestatisticsquiz/{id}/{exportId}", method = { RequestMethod.GET,
			RequestMethod.HEAD })
	public ModelAndView preparestatisticsquiz(@PathVariable String id, @PathVariable String exportId, Locale locale,
			HttpServletRequest request) throws Exception {

		Export export = exportService.getExport(Integer.parseInt(exportId), false);
		if (export == null || export.getState() != ExportState.Pending
				|| !export.getSurvey().getId().equals(Integer.parseInt(id)))
			return null;

		Survey survey = surveyService.getSurvey(Integer.parseInt(id), false, true);

		surveyService.initializeSurvey(survey);

		ResultFilter filter = export.getResultFilter().copy();
		Set<String> newids = new HashSet<>();
		newids.addAll(filter.getExportedQuestions());
		filter.setVisibleQuestions(newids);

		if (filter.getLanguages() != null && filter.getLanguages().isEmpty()) {
			filter.setLanguages(null);
		}

		ModelAndView results = results(survey, new HashMap<>(), null, survey.getIsDraft(), export.isAllAnswers(),
				filter, true);

		Publication publication = new Publication();
		publication.setFilter(export.getResultFilter());

		Set<String> newids2 = new HashSet<>();
		newids.addAll(publication.getFilter().getExportedQuestions());
		publication.getFilter().setVisibleQuestions(newids2);

		publication.setAllQuestions(publication.getFilter().getVisibleQuestions().isEmpty());
		results.addObject("publication", publication);

		if (export.getShowShortnames() != null && export.getShowShortnames()) {
			results.addObject("showShortnames", true);
		}

		results.setViewName("management/statisticsquizpdf");
		results.addObject("forpdf", true);
		return results;
	}

	@RequestMapping(value = "/prepareECFIndividualResults/{id}/{exportId}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView prepareECFIndividualResults(@PathVariable String id, @PathVariable String exportId, Locale locale, HttpServletRequest request) throws Exception {
		return null;
	}

	@RequestMapping(value = "/access", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView access(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, InvalidURLException,
			ForbiddenURLException {
		User u = sessionService.getCurrentUser(request);

		Form form;
		Survey survey = surveyService.getSurveyByShortname(shortname, true, u, request, false, true, true, false);
		form = new Form(resources);
		form.setSurvey(survey);

		if (!sessionService.userIsFormManager(form.getSurvey(), u, request) && u.getResultAccess() == null) {
			throw new ForbiddenURLException();
		}

		List<Access> accesses = surveyService.getAccesses(form.getSurvey().getId());

		List<User> lstopcusers = null;
		List<String> lstopcdepartments = null;

		if (survey.getIsOPC() && opcusers != null && opcusers.length() > 0) {
			lstopcusers = new ArrayList<>();
			String[] users = opcusers.split(";");
			for (String user : users) {
				if (user.length() > 0) {
					User opcuser = administrationService.getUserForLogin(user);
					if (opcuser != null) {
						lstopcusers.add(opcuser);
					}
				}
			}
		}

		if (survey.getIsOPC() && opcdepartments != null && opcdepartments.length() > 0) {
			lstopcdepartments = new ArrayList<>();
			String[] departments = opcdepartments.split(";");
			for (String department : departments) {
				if (department.length() > 0) {
					lstopcdepartments.add(department);
				}
			}
		}

		for (Access access : accesses) {
			if (access.getUser() != null && access.getUser().getType().equalsIgnoreCase(User.ECAS)) {
				access.getUser().setDepartments(ldapService.getUserLDAPGroups(access.getUser().getLogin()));
			}

			if (survey.getIsOPC() && access.getUser() != null && lstopcusers != null
					&& lstopcusers.contains(access.getUser())) {
				access.setReadonly(true);
			}

			if (survey.getIsOPC() && access.getDepartment() != null && lstopcdepartments != null
					&& lstopcdepartments.contains(access.getDepartment())) {
				access.setReadonly(true);
			}
		}

		ModelAndView result = new ModelAndView("management/access", "accesses", accesses);
		result.addObject("useUILanguage", true);
		result.addObject("form", form);

		List<KeyValue> domains = ldapDBService.getDomains(true, true, resources, locale);
		result.addObject("domains", domains);
		
		boolean resultsPrivilege = request.getParameter("resultMode") != null && request.getParameter("resultMode").equalsIgnoreCase("true");
		if (resultsPrivilege) {
			result.addObject("selectSecondTab", true);
		}
		
		boolean showFirstTab = sessionService.userIsFormManager(form.getSurvey(), u, request);
		boolean showSecondTab = form.getSurvey().getDedicatedResultPrivileges() && (showFirstTab || u.getResultAccess() != null);
		boolean readOnlyResultPrivileges = !sessionService.userIsFormAdmin(form.getSurvey(), u, request) && (u.getResultAccess() == null || u.getResultAccess().isReadonly());
		result.addObject("showFirstTab", showFirstTab);
		result.addObject("showSecondTab", showSecondTab);
		result.addObject("readOnlyResultPrivileges", readOnlyResultPrivileges);		
		
		int page = 1;
		if (request.getParameter("page") != null) {			
			try {
				page = Integer.parseInt(request.getParameter("page"));
			} catch (NumberFormatException nfe) {
				logger.error(nfe.getLocalizedMessage(), nfe);
			}
		}
		result.addObject("resultspage", page);		
		
		return result;
	}
	
	@RequestMapping(value = "/resultAccessesJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<ResultAccess> resultAccessesJSON(@PathVariable String shortname, HttpServletRequest request, Locale locale) throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenURLException, InvalidURLException {
		User u = sessionService.getCurrentUser(request);
		
		Survey survey = surveyService.getSurveyByShortname(shortname, true, u, request, false, true, true, false);
		if (!sessionService.userIsFormManager(survey, u, request) && u.getResultAccess() == null) {
			throw new ForbiddenURLException();
		}		
		
		String rows = request.getParameter("rows");
		String page = request.getParameter("page");

		if (page == null || rows == null) {
			return null;
		}
		
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String order = request.getParameter("order");
		
		List<ResultAccess> result = surveyService.getResultAccesses(u.getResultAccess(), shortname, Integer.parseInt(page), Integer.parseInt(rows), name, email, order, locale);
		sessionService.upgradePrivileges(survey, u, request);

		if (sessionService.userIsFormAdmin(survey, u, request) || u.getLocalPrivileges().get(LocalPrivilege.FormManagement) >= 2)
		{
			//form admins can edit all filtered questions
			//Privileged Users too
			for (ResultAccess resultAccess : result) {
				resultAccess.setReadonlyFilterQuestions("");
			}
		} else if (u.getResultAccess() != null && u.getResultAccess().getResultFilter() != null) {
			//users with result access can edit all filtered questions but their own
			String ownRestriction = String.join(";", u.getResultAccess().getResultFilter().getFilterValues().keySet());
						
			for (ResultAccess resultAccess : result) {
				resultAccess.setReadonlyFilterQuestions(ownRestriction);
			}
		}
		
		return result;
	}
	
	@PostMapping(value = "/updateResultAccessTypeJSON")
	public @ResponseBody boolean updateResultAccessTypeJSON(@PathVariable String shortname, HttpServletRequest request) throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenURLException {
		User u = sessionService.getCurrentUser(request);
				
		String id = request.getParameter("accessid");
		String readonly = request.getParameter("readonly");
		
		ResultAccess access = surveyService.getResultAccess(Integer.parseInt(id));
		
		if (access == null) {
			logger.error("ResultAccess with id " + id + " not found.");
			return false;
		}
		
		Survey survey = surveyService.getSurveyByUniqueId(access.getSurveyUID(), false, true);
		boolean userIsFormManager = sessionService.userIsFormAdmin(survey, u, request);
		
		if (!userIsFormManager && (!u.getId().equals(access.getOwner()))) {
			throw new ForbiddenURLException();
		}	
		
		access.setReadonly(readonly.equalsIgnoreCase("true"));
		surveyService.saveResultAccess(access);

		return true;
	}	
	
	@PostMapping(value = "/updateResultFilter")
	public ModelAndView updateResultFilter(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws Exception {
		
		User u = sessionService.getCurrentUser(request);
		String id = request.getParameter("accessid");		
		ResultAccess access = surveyService.getResultAccess(Integer.parseInt(id));
		
		if (access == null) {
			ModelAndView result = access(shortname, request, locale);
			result.addObject(Constants.MESSAGE, "Privilege not found");
			return result;
		}
		
		Survey survey = surveyService.getSurveyByUniqueId(access.getSurveyUID(), false, true);
		
		boolean userIsFormManager = sessionService.userIsFormAdmin(survey, u, request);
		
		if (!userIsFormManager && !u.getId().equals(access.getOwner())) {
			throw new ForbiddenURLException();
		}		
		
		Map<String, String> readonlyFilterValues = new HashMap<>();
		if (access.getResultFilter() == null) {
			access.setResultFilter(new ResultFilter());
			access.getResultFilter().setSurveyUid(access.getSurveyUID());
		} else {			
			// if the user himself has restrictions, he cannot remove them from "children"
			if (!sessionService.userIsFormAdmin(survey, u, request) && u.getResultAccess() != null && u.getResultAccess().getResultFilter() != null) {			
				for (String uid : u.getResultAccess().getResultFilter().getFilterValues().keySet()) {
					readonlyFilterValues.put(uid, access.getResultFilter().getFilterValues().get(uid));
				}
			}
			
			access.getResultFilter().clearResultFilter();
		}
		
		Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		
		for (Element question : survey.getElementsForResultAccessFilter().keySet()) {
			if (readonlyFilterValues.containsKey(question.getUniqueId())) {
				access.getResultFilter().getFilterValues().put(question.getUniqueId(), readonlyFilterValues.get(question.getUniqueId()));
			} else {
				if (parameters.containsKey(question.getUniqueId())) {
					String[] values = parameters.get(question.getUniqueId());
					String value = StringUtils.arrayToDelimitedString(values, ";");
					if (value.length() > 0) {
						access.getResultFilter().getFilterValues().put(question.getUniqueId(), value);
					}
				}
			}
		}		
		
		surveyService.saveResultAccess(access);	
		
		return access(shortname, request, locale);
	}

	@PostMapping(value = "/access")
	public ModelAndView accessPOST(@PathVariable String shortname, HttpServletRequest request, Locale locale)
			throws Exception {

		String target = request.getParameter("target");
		if (target != null) {
			if (target.equals("addUser")) {
				return addUser(shortname, request.getParameter("login"), request.getParameter("ecas"), request, locale);
			} else if (target.equals("addUserEmail")) {
				return addUserEmail(shortname, request.getParameter("emails"), request, locale);
			} else if (target.equals("removeUser")) {
				return removeUser(shortname, request.getParameter("id"), request, locale);
			} else if (target.equals("addGroup")) {
				return addGroup(shortname, request.getParameter("groupname"), request, locale);
			}
		}

		String id = request.getParameter("id");
		String privilege = request.getParameter("privilege");
		String value = request.getParameter("value");

		Access access = surveyService.getAccess(Integer.parseInt(id));

		if (access != null) {
			Form form = sessionService.getForm(request, shortname, false, false);
			User u = sessionService.getCurrentUser(request);
			if (!sessionService.userIsFormAdmin(form.getSurvey(), u, request)) {
				throw new ForbiddenURLException();
			}

			String oldInfo = access.getInfo();

			if (privilege.equalsIgnoreCase("AccessDraft"))
				access.getLocalPrivileges().put(LocalPrivilege.AccessDraft, Integer.parseInt(value));
			else if (privilege.equalsIgnoreCase("AccessResults"))
				access.getLocalPrivileges().put(LocalPrivilege.AccessResults, Integer.parseInt(value));
			else if (privilege.equalsIgnoreCase("FormManagement"))
				access.getLocalPrivileges().put(LocalPrivilege.FormManagement, Integer.parseInt(value));
			else if (privilege.equalsIgnoreCase("ManageInvitations"))
				access.getLocalPrivileges().put(LocalPrivilege.ManageInvitations, Integer.parseInt(value));

			surveyService.saveAccess(access);

			String newInfo = access.getInfo();
			if (!newInfo.equalsIgnoreCase(oldInfo)) {
				activityService.log(ActivityRegistry.ID_PRIVILEGES_EDIT, oldInfo, newInfo, u.getId(), access.getSurvey().getUniqueId());
			}

			if (!sessionService.userIsFormAdmin(form.getSurvey(), u, request)) {
				// the user removed his privileges for this survey -> redirect him to surveys
				// page
				return new ModelAndView("redirect:/forms");
			}

			return access(shortname, request, locale);
		} else {
			ModelAndView result = access(shortname, request, locale);

			String message = resources.getMessage("message.AccessNotFound", null, "Access not found", locale);
			result.addObject(Constants.MESSAGE, message);

			return result;
		}
	}

	public ModelAndView addUser(String shortname, String login, String ecas, HttpServletRequest request, Locale locale)
			throws Exception {

		User user = null;
		Form form;
		boolean isEcasUser = ecas != null && ecas.equalsIgnoreCase("true");
		boolean resultsPrivilege = request.getParameter("resultMode") != null && request.getParameter("resultMode").equalsIgnoreCase("true");
		
		form = sessionService.getForm(request, shortname, false, false);

		User u = sessionService.getCurrentUser(request);
		
		boolean userIsFormManager = sessionService.userIsFormAdmin(form.getSurvey(), u, request);
		
		if (!userIsFormManager && (!resultsPrivilege || u.getResultAccess() == null)) {
			throw new ForbiddenURLException();
		}		

		try {
			user = administrationService.getUserForLogin(login, isEcasUser);
		} catch (Exception e) {
			// ignore
		}

		if (user == null && isEcasUser) {
			user = new User();
			user.setLogin(login);
			user.setDisplayName(ldapService.getMoniker(login));
			user.setEmail(ldapService.getEmail(login));
			user.setDepartments(ldapService.getUserLDAPGroups(user.getLogin()));
			user.setType(User.ECAS);

			Role ecRole = null;
			for (Role role : administrationService.getAllRoles()) {
				if (role.getName().equalsIgnoreCase("Form Manager (EC)"))
					ecRole = role;
			}

			user.getRoles().add(ecRole);
			try {
				administrationService.createUser(user);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
				user = null;
			}
		}

		if (user != null) {			
			if (resultsPrivilege) {				
				ResultAccess access = surveyService.getResultAccess(form.getSurvey().getUniqueId(), user.getId());
				if (access != null) {
					ModelAndView result = access(shortname, request, locale);
					result.addObject(Constants.MESSAGE, "This user has already been added by you or another user.");
					return result;
				}
				
				ResultAccess resAccess = new ResultAccess();
				resAccess.setSurveyUID(form.getSurvey().getUniqueId());
				resAccess.setUser(user.getId());
				resAccess.setOwner(u.getId());
				
				if (u.getResultAccess() != null && u.getResultAccess().isReadonly()) {
					resAccess.setReadonly(true);
				}
				
				String readonlyQuestionUIDs = "";
				if (!userIsFormManager && u.getResultAccess() != null && u.getResultAccess().getResultFilter() != null) {
					resAccess.setResultFilter(u.getResultAccess().getResultFilter().copy());
					
					for (String questionUID : resAccess.getResultFilter().getFilterValues().keySet()) {
						if (readonlyQuestionUIDs.length() > 0) {
							readonlyQuestionUIDs += ";";
						}
						readonlyQuestionUIDs += questionUID;
					}
				}
				if (readonlyQuestionUIDs.length() > 0) {
					resAccess.setReadonlyFilterQuestions(readonlyQuestionUIDs);
				}
				
				surveyService.saveResultAccess(resAccess);				
			} else {				
				List<Access> accesses = surveyService.getAccesses(form.getSurvey().getId());
				for (Access access : accesses) {
					if (access.getUser() != null && access.getUser().getId().equals(user.getId())) {
						ModelAndView result = access(shortname, request, locale);
						result.addObject(Constants.MESSAGE, "User already exists");
						return result;
					}
				}
	
				Access access = new Access();
				access.setSurvey(form.getSurvey());
				access.setUser(user);
				surveyService.saveAccess(access);
				activityService.log(ActivityRegistry.ID_DELEGATE_MANAGER_ADD, null, user.getName() + " - no privileges",
						sessionService.getCurrentUser(request).getId(), form.getSurvey().getUniqueId());				
			}
			
			return access(shortname, request, locale);
		} else {
			ModelAndView result = access(shortname, request, locale);
			result.addObject(Constants.MESSAGE, "User not found");
			return result;
		}		
	}

	public ModelAndView addUserEmail(String shortname, String emails, HttpServletRequest request, Locale locale)
			throws Exception {

		if (emails.isEmpty()) {
			ModelAndView result = access(shortname, request, locale);
			String message = resources.getMessage("label.AtLeastOneMail", null,
					"Please enter at least one email address", locale);
			result.addObject(Constants.MESSAGE, message);
			return result;
		}

		List<User> usersToAdd = new ArrayList<>();
		String[] splittedEmails = Arrays.stream(emails.split(",")).map(String::trim).toArray(String[]::new);
		boolean resultsPrivilege = request.getParameter("resultMode") != null && request.getParameter("resultMode").equalsIgnoreCase("true");

		Form form;
		form = sessionService.getForm(request, shortname, false, false);
		User u = sessionService.getCurrentUser(request);
		boolean userIsFormManager = sessionService.userIsFormAdmin(form.getSurvey(), u, request);

		if (!userIsFormManager && (!resultsPrivilege || u.getResultAccess() == null)) {
			throw new ForbiddenURLException();
		}

		try {
			splittedEmails = new HashSet<String>(Arrays.asList(splittedEmails)).toArray(new String[0]);
			for(String email : splittedEmails) {
				//validate mail with same regex as in frontend
				if (MailService.isValidEmailAddress(email)) {
					usersToAdd.addAll(administrationService.getUserLoginsByEmail(email, 5));
				}
			}
		} catch (Exception e) {
			// ignore
		}

		if(usersToAdd == null || usersToAdd.size() <= 0) {
			ModelAndView result = access(shortname, request, locale);
			String message = resources.getMessage("label.NoUsersFound", null,
					"No Users found", locale);
			result.addObject(Constants.MESSAGE, message);
			return result;
		}
		
		int counter = 0;

		for (User user : usersToAdd) {
			if (resultsPrivilege) {
				ResultAccess access = surveyService.getResultAccess(form.getSurvey().getUniqueId(), user.getId());
				if (access != null) {
					continue;
				}

				ResultAccess resAccess = new ResultAccess();
				resAccess.setSurveyUID(form.getSurvey().getUniqueId());
				resAccess.setUser(user.getId());
				resAccess.setOwner(u.getId());

				if (u.getResultAccess() != null && u.getResultAccess().isReadonly()) {
					resAccess.setReadonly(true);
				}

				String readonlyQuestionUIDs = "";
				if (!userIsFormManager && u.getResultAccess() != null && u.getResultAccess().getResultFilter() != null) {
					resAccess.setResultFilter(u.getResultAccess().getResultFilter().copy());

					for (String questionUID : resAccess.getResultFilter().getFilterValues().keySet()) {
						if (readonlyQuestionUIDs.length() > 0) {
							readonlyQuestionUIDs += ";";
						}
						readonlyQuestionUIDs += questionUID;
					}
				}
				if (readonlyQuestionUIDs.length() > 0) {
					resAccess.setReadonlyFilterQuestions(readonlyQuestionUIDs);
				}
				surveyService.saveResultAccess(resAccess);
			} else {
				List<Access> accesses = surveyService.getAccesses(form.getSurvey().getId());
				boolean accessAlreadyGranted = false;
				for (Access access : accesses) {
					accessAlreadyGranted = accessAlreadyGranted || (access.getUser() != null && access.getUser().getId().equals(user.getId()));
				}

				if(accessAlreadyGranted) {
					continue;
				}

				Access newAccess = new Access();
				newAccess.setSurvey(form.getSurvey());
				newAccess.setUser(user);
				surveyService.saveAccess(newAccess);
				
				activityService.log(ActivityRegistry.ID_DELEGATE_MANAGER_ADD, null, user.getName() + " - no privileges",
				sessionService.getCurrentUser(request).getId(), form.getSurvey().getUniqueId());
				
				counter++;
				if (counter > 4) break;
			}
		}
		return access(shortname, request, locale);
	}

	public ModelAndView addGroup(String shortname, String groupname, HttpServletRequest request, Locale locale)
			throws Exception {

		Form form;
		form = sessionService.getForm(request, shortname, false, false);

		User u = sessionService.getCurrentUser(request);
		if (!sessionService.userIsFormAdmin(form.getSurvey(), u, request)) {
			throw new ForbiddenURLException();
		}

		String[] departments = new String[1];

		List<String> ecasInternalDomains = ldapDBService.getDomainKeySufixes();

		if (groupname.equalsIgnoreCase("ALL-COM") || (ecasInternalDomains.contains(groupname))) {
			departments[0] = groupname;
		} else {
			departments = ecService.getDepartments();
		}

		if (departments != null && departments.length > 0) {
			List<Access> accesses = surveyService.getAccesses(form.getSurvey().getId());
			for (Access access : accesses) {
				if (access.getDepartment() != null && access.getDepartment().equals(groupname)) {
					ModelAndView result = access(shortname, request, locale);
					result.addObject(Constants.MESSAGE, "Group already exists");
					return result;
				}
			}

			Access access = new Access();
			access.setSurvey(form.getSurvey());
			access.setDepartment(groupname);
			surveyService.saveAccess(access);
			activityService.log(ActivityRegistry.ID_DELEGATE_MANAGER_ADD, null, groupname + " - no privileges",
					sessionService.getCurrentUser(request).getId(), form.getSurvey().getUniqueId());

		} else {
			ModelAndView result = access(shortname, request, locale);
			result.addObject(Constants.MESSAGE, "Group not found");
			return result;
		}

		return access(shortname, request, locale);
	}

	public ModelAndView removeUser(String shortname, String id, HttpServletRequest request, Locale locale)
			throws Exception {
				
		boolean resultsPrivilege = request.getParameter("resultMode") != null && request.getParameter("resultMode").equalsIgnoreCase("true");
		
		Form form = sessionService.getForm(request, shortname, false, false);
		User u = sessionService.getCurrentUser(request);
		
		boolean userIsFormAdmin = sessionService.userIsFormAdmin(form.getSurvey(), u, request);
		
		if (!userIsFormAdmin && (!resultsPrivilege || u.getResultAccess() == null)) {
			throw new ForbiddenURLException();
		}
		
		if (resultsPrivilege) {
			ResultAccess access = surveyService.getResultAccess(Integer.parseInt(id));
			
			if (access != null) {
				
				if (!userIsFormAdmin && !u.getId().equals(access.getOwner())) {
					throw new ForbiddenURLException();
				}
				
				surveyService.deleteResultAccess(access);
				return access(shortname, request, locale);
			} else {
				ModelAndView result = access(shortname, request, locale);
				result.addObject(Constants.MESSAGE, "Access not found");
				return result;
			}
		} else {		
			Access access = surveyService.getAccess(Integer.parseInt(id));
	
			if (access != null) {
					
				surveyService.deleteAccess(access);
				activityService.log(ActivityRegistry.ID_PRIVILEGES_DELETE, access.getInfo(), null, sessionService.getCurrentUser(request).getId(),
						access.getSurvey().getUniqueId());
	
				if (!sessionService.userIsFormAdmin(form.getSurvey(), u, request)) {
					// the user removed his privileges for this survey -> redirect him to surveys
					// page
					request.getSession().removeAttribute("sessioninfo");
					return new ModelAndView("redirect:/forms");
				}
	
				return access(shortname, request, locale);
			} else {
				ModelAndView result = access(shortname, request, locale);
				result.addObject(Constants.MESSAGE, "Access not found");
				return result;
			}
		}
	}

	@RequestMapping(value = "/searchAndReplace", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody SearchAndReplaceResult forms(@PathVariable String shortname, HttpServletRequest request,
			HttpServletResponse response, Locale locale, Model model) {
		try {
			Form form;
			try {
				form = sessionService.getForm(request, shortname, false, false);
			} catch (NoFormLoadedException ne) {
				logger.error(ne.getLocalizedMessage(), ne);
				return null;
			}

			User u = sessionService.getCurrentUser(request);
			if (!u.getId().equals(form.getSurvey().getOwner().getId())
					&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
					&& u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 2) {
				return null;
			}

			Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
			String search = parameterMap.get("search")[0];
			search = java.net.URLDecoder.decode(search, "UTF-8");
			String replace = parameterMap.get("replace")[0];
			replace = java.net.URLDecoder.decode(replace, "UTF-8");

			String translationId = request.getParameter("translationId");

			int id = Integer.parseInt(translationId);

			Translations translations = null;

			if (id == 0) {
				translations = TranslationsHelper.getTranslations(form.getSurvey(), false);
			} else {
				translations = translationService.getTranslations(id);
			}

			List<String> searchResults = new ArrayList<>();
			List<String> replaceResults = new ArrayList<>();

			for (Translation translation : translations.getTranslations()) {
				if (!translation.getLocked() && translation.getLabel() != null && translation.getLabel().contains(search)) {
					searchResults.add(translation.getLabel().replace(search,
							"<span style='background: #FFEDA8'>" + search + "</span>"));

					String replaced = translation.getLabel().replace(search, replace);

					if (replaced.length() == 0) {
						// it is not allowed to create empty labels
						SearchAndReplaceResult result = new SearchAndReplaceResult();
						result.setEmptyLabels(true);
						return result;
					}

					replaceResults
							.add(replaced.replace(replace, "<span style='background: #FFEDA8'>" + replace + "</span>"));
				}
			}

			SearchAndReplaceResult result = new SearchAndReplaceResult();
			result.setTranslationId(id);
			result.setSearchResults(searchResults.toArray(new String[searchResults.size()]));
			result.setReplaceResults(replaceResults.toArray(new String[replaceResults.size()]));

			return result;

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	@GetMapping(value = "/closeCurrentForm")
	public ModelAndView closeCurrentForm(HttpServletRequest request, Locale locale) {
		request.getSession().removeAttribute("sessioninfo");
		return new ModelAndView("redirect:/dashboard");
	}

	@GetMapping(value = "/pendingChangesForPublishing", headers = "Accept=*/*")
	public @ResponseBody boolean pendingChangesForPublishing(@PathVariable String shortname, HttpServletRequest request,
			HttpServletResponse response) {
		Survey draft = surveyService.getSurvey(shortname, true, false, false, false, null, true, false);
		Map<Element, Integer> changes = surveyService.getPendingChanges(draft);
		return !changes.isEmpty();
	}

	@PostMapping(value = "/requestCodaDashboard")
	public @ResponseBody boolean requestCodaDashboard(@PathVariable String shortname, HttpServletRequest request) throws Exception {
		User u = sessionService.getCurrentUser(request);

		Survey survey = surveyService.getSurveyByShortname(shortname, true, u, request, false, true, true, false);

		if (survey != null && survey.getOwner().getId().equals(u.getId())){

			if (codaCreateDashboardLink == null || survey.getCodaWaiting() || (survey.getCodaLink() != null && survey.getCodaLink().length() > 0)){
				return false;
			}

			sessionService.initializeProxy();
			HttpClient httpClient = HttpClients.createSystem();
			HttpPost httpPost = new HttpPost(codaCreateDashboardLink.trim());
			if (codaApiKey != null){
				httpPost.setHeader(HttpHeaders.AUTHORIZATION, codaApiKey);
			}

			Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("Alias", shortname);

			String type = "Standard";
			if (survey.getIsQuiz()){
				type = "Quiz";
			} else if (survey.getIsECF()){
				type = "ECF";
			} else if (survey.getIsDelphi()){
				type = "Delphi";
			}

			jsonMap.put("Type", type);
			jsonMap.put("DashboardType", "Standard");

			List<Map<String, String>> users = new LinkedList<>();
			List<Access> accesses = surveyService.getAccesses(survey.getId());

			for (Access access : accesses){
				//This decision code might have to be adjusted
				if (!access.hasAnyPrivileges() || access.getUser() == null){
					continue;
				}
				User user = access.getUser();
				HashMap<String, String> userMap = new HashMap<>();
				userMap.put("Username", user.getLogin());
				if (access.getLocalPrivilegeValue("FormManagement") > 1){
					userMap.put("Role", "Executive");
				} else if (access.getLocalPrivilegeValue("AccessResults") >= 1) {
					userMap.put("Role", "Analyst");
				} else {
					continue;
				}

				userMap.put("User_email", user.getEmail());
				userMap.put("Firstname", user.getGivenName());
				userMap.put("Surname", user.getSurName());
				users.add(userMap);
			}
			User owner = survey.getOwner();
			HashMap<String, String> ownerMap = new HashMap<>();
			ownerMap.put("Username", owner.getLogin());
			ownerMap.put("Role", "Executive");
			ownerMap.put("User_email", owner.getEmail());
			ownerMap.put("Firstname", owner.getGivenName());
			ownerMap.put("Surname", owner.getSurName());
			users.add(ownerMap);
			jsonMap.put("Users", users);

			ObjectMapper mapper = new ObjectMapper();
			String stringResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMap);
			
			logger.info(stringResult);

			StringEntity jsonEntity = new StringEntity(stringResult);
			jsonEntity.setContentType("application/json");
			httpPost.addHeader("Content-type", "application/json");
			httpPost.setEntity(jsonEntity);

			try {
			
				HttpResponse response = httpClient.execute(httpPost);

				if (response != null) {
					int code = response.getStatusLine().getStatusCode();
					logger.info("CODA response code: " + code);
					
					HttpEntity entity = response.getEntity();

					if (entity != null) {
						String strResponse = EntityUtils.toString(entity, "UTF-8");
						logger.info(strResponse);
					}
					
					if (code >= 200 && code < 300){
						surveyService.setCodaWaiting(survey.getUniqueId(), true);
						return true;
					}
				}
			
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage(), e);				
			}
		}

		return false;
	}
}
