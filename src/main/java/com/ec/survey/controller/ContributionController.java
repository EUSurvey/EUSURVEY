package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.exception.MessageException;
import com.ec.survey.exception.httpexception.ForbiddenException;
import com.ec.survey.exception.httpexception.InternalServerErrorException;
import com.ec.survey.exception.httpexception.NotFoundException;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Draft;
import com.ec.survey.model.Form;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.selfassessment.SACriterion;
import com.ec.survey.model.selfassessment.SAReportConfiguration;
import com.ec.survey.model.selfassessment.SAResult;
import com.ec.survey.model.selfassessment.SATargetDataset;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.exception.ECFException;
import com.ec.survey.model.ECFProfile;
import com.ec.survey.model.survey.ecf.ECFIndividualResult;
import com.ec.survey.service.SelfAssessmentService;
import com.ec.survey.service.ValidCodesService;
import com.ec.survey.tools.*;

import com.ec.survey.tools.activity.ActivityRegistry;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Controller
public class ContributionController extends BasicController {

	private @Value("${server.prefix}") String host;

	@Resource(name = "validCodesService")
	private ValidCodesService validCodesService;
	
	@Resource(name = "selfassessmentService")
	protected SelfAssessmentService selfassessmentService;

	public AnswerSet getAnswerSet(String code, HttpServletRequest request)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		AnswerSet answerSetOrNull = null;
		User user;
		answerSetOrNull = answerService.get(code);
		
		if (answerSetOrNull != null && answerSetOrNull.getSurvey().getEcasSecurity() && request.getRequestURI().contains("editcontribution"))
		{
			user = sessionService.getCurrentUser(request, false);
		} else {
			user = sessionService.getCurrentUser(request);
		}

		if (answerSetOrNull == null) {
			return null;
		}

		if (user == null) {
			// comes from "Access a Contribution" -> check
			return answerSetOrNull;
		}

		if (sessionService.userIsAnswerer(answerSetOrNull, user)) {
			return answerSetOrNull;
		}

		if (answerSetOrNull.getSurvey() != null) {
			Survey draftSurvey = surveyService.getSurveyByUniqueId(answerSetOrNull.getSurvey().getUniqueId(), false,
					true);
			
			if (sessionService.userCanEditResults(draftSurvey, user, request)) {
				return answerSetOrNull;
			}
			
			// if participants are allowed to change their contribution
			if (draftSurvey != null && draftSurvey.getChangeContribution()) {
				return answerSetOrNull;
			}
		}
		return null;
	}

	@RequestMapping(value = "/preparequizresults/{code}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView quizresults(@PathVariable String code, Locale locale, HttpServletRequest request)
			throws Exception {
		AnswerSet answerSet = getAnswerSet(code, request);

		Set<String> invisibleElements = new HashSet<>();

		// this is needed to initialize the invisibleElements
		SurveyHelper.validateAnswerSet(answerSet, answerService, invisibleElements, resources, locale, null, null, true,
				null, fileService);

		ModelAndView result = new ModelAndView("runner/quizResult", Constants.UNIQUECODE, answerSet.getUniqueCode());
		Form form = new Form(resources, surveyService.getLanguage(answerSet.getLanguageCode().toUpperCase()),
				translationService.getActiveTranslationsForSurvey(answerSet.getSurvey().getId()), contextpath);

		String lang = answerSet.getLanguageCode();
		Survey translated = SurveyHelper.createTranslatedSurvey(answerSet.getSurvey().getId(), lang, surveyService,
				translationService, true);
		form.setSurvey(translated);
		form.setLanguage(surveyService.getLanguage(lang));

		form.getAnswerSets().add(answerSet);
		result.addObject(form);
		result.addObject("surveyprefix", answerSet.getSurvey().getId());
		result.addObject("quiz", QuizHelper.getQuizResult(answerSet, invisibleElements));
		form.setForPDF(true);
		result.addObject("forpdf", "true");
		result.addObject("invisibleElements", invisibleElements);
		return result;
	}

	@RequestMapping(value = "/preparesaresults/{code}/{dataset}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView saresults(@PathVariable String code, @PathVariable int dataset, Locale locale, HttpServletRequest request)
			throws Exception {
		AnswerSet answerSet = getAnswerSet(code, request);

		Set<String> invisibleElements = new HashSet<>();

		// this is needed to initialize the invisibleElements
		SurveyHelper.validateAnswerSet(answerSet, answerService, invisibleElements, resources, locale, null, null, true,
				null, fileService);

		ModelAndView result = new ModelAndView("runner/saResult", Constants.UNIQUECODE, answerSet.getUniqueCode());
		Form form = new Form(resources, surveyService.getLanguage(answerSet.getLanguageCode().toUpperCase()),
				translationService.getActiveTranslationsForSurvey(answerSet.getSurvey().getId()), contextpath);

		String lang = answerSet.getLanguageCode();
		Survey translated = SurveyHelper.createTranslatedSurvey(answerSet.getSurvey().getId(), lang, surveyService,
				translationService, true);
		form.setSurvey(translated);
		form.setLanguage(surveyService.getLanguage(lang));

		form.getAnswerSets().add(answerSet);
		result.addObject(form);
		result.addObject("surveyprefix", answerSet.getSurvey().getId());
		form.setForPDF(true);
		result.addObject("forpdf", "true");
		result.addObject("invisibleElements", invisibleElements);
		
		result.addObject("issaresultpage", true);
		
		SAReportConfiguration config = selfassessmentService.getReportConfiguration(answerSet.getSurvey().getUniqueId());
		result.addObject("SAReportConfiguration", config);
		
		List<SATargetDataset> datasets = selfassessmentService.getTargetDatasets(answerSet.getSurvey().getUniqueId());
		result.addObject("SATargetDatasets", datasets);
		
		List<SACriterion> criteria = selfassessmentService.getCriteria(answerSet.getSurvey().getUniqueId());
		result.addObject("SACriteria", criteria);
		
		SAResult saresult = selfassessmentService.getSAResult(dataset, answerSet.getUniqueCode());
		result.addObject("SAResult", saresult);
		
		SATargetDataset comparisonDataset = null;
		if (dataset > 0) {
			comparisonDataset = selfassessmentService.getTargetDataset(dataset);
		}
		
		result.addObject("ComparisonDataset", comparisonDataset);
		
		String chartsuid = request.getParameter("charts");
		
		if (chartsuid != null && chartsuid.length() > 0) {	
			java.io.File folder = fileService.getSurveyExportsFolder(answerSet.getSurvey().getUniqueId());
			java.io.File chartstarget = new java.io.File(String.format("%s/sa%s.dat", folder.getPath(), chartsuid ));
			
			String charts = FileUtils.readFileToString(chartstarget, Charset.forName("UTF-8"));			
			result.addObject("charts", charts.split(",") );		
		}
		
		return result;
	}
	
	@RequestMapping(value = "/preparecontribution/{code}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView preparecontribution(@PathVariable String code, Locale locale, HttpServletRequest request)
			throws Exception {
		ModelAndView result = editContributionInner(code, locale, request, false, false, false);

		Form f = (Form) result.getModel().get("form");
		SurveyHelper.calcTableWidths(f.getSurvey(), f);
		f.setForPDF(true);

		AnswerSet answerSet = this.answerService.get(code);
		if (answerSet.getSurvey().getIsECF()) {
			ECFIndividualResult individualResult = ecfService.getECFIndividualResult(answerSet.getSurvey(), answerSet);
			List<String> base64ECFSpiderCharts = ecfService.spiderChartsB64ByECFType(individualResult);
			result.addObject("base64ECFSpiderCharts", base64ECFSpiderCharts);
			result.addObject("ecfIndividualResult", individualResult);
		}

		result.addObject("forpdf", "true");
		result.addObject("submit", "false");
		return result;
	}

	@RequestMapping(value = "/preparedraft/{code}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView preparedraft(@PathVariable String code, Locale locale, HttpServletRequest request)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenURLException,
			InvalidURLException, InterruptedException, IOException, ECFException {
		ModelAndView result = editContributionInner(code, locale, request, false, false, true);

		Form f = (Form) result.getModel().get("form");
		SurveyHelper.calcTableWidths(f.getSurvey(), f);
		f.setForPDF(true);
		result.addObject("forpdf", "true");
		result.addObject("submit", "false");
		return result;
	}

	@RequestMapping(value = "/preparepublishedcontribution/{id}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView showforpublishedpdf(@PathVariable String id, Locale locale, HttpServletRequest request)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenURLException,
			InvalidURLException, InterruptedException, IOException, ECFException {
		AnswerSet answerSet = answerService.get(Integer.parseInt(id));
		ModelAndView result = editContributionInner(answerSet.getUniqueCode(), locale, request, false, false, false);
		result.addObject("forpdf", "true");
		result.addObject("submit", "false");

		Form form = (Form) result.getModel().get("form");
		form.setForPDF(true);

		if (answerSet.getSurvey().getIsECF()) {
			ECFIndividualResult individualResult = ecfService.getECFIndividualResult(answerSet.getSurvey(), answerSet);
			List<String> base64SpiderCharts = ecfService.spiderChartsB64ByECFType(individualResult);
			result.addObject("base64ECFSpiderCharts", base64SpiderCharts);
			result.addObject("ecfIndividualResult", individualResult);
		}

		result.addObject("publication", form.getSurvey().getPublication());
		return result;
	}

	@RequestMapping(value = "/editcontribution/{code}/back", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView editcontributionfrombackoffice(@PathVariable String code, Locale locale,
			HttpServletRequest request) throws NotAgreedToTosException, WeakAuthenticationException,
			NotAgreedToPsException, ForbiddenURLException, InvalidURLException, InterruptedException, IOException {
		return editContributionInner(code, locale, request, true, true, false);
	}

	@RequestMapping(value = "/editcontribution/{code}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView editcontribution(@PathVariable String code, Locale locale, HttpServletRequest request)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenURLException,
			InvalidURLException, InterruptedException, IOException {
		return editContributionInner(code, locale, request, false, true, false);
	}

	private ModelAndView editContributionInner(String code, Locale locale, HttpServletRequest request,
			boolean fromBackOffice, boolean useNewestSurvey, boolean isdraft)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, ForbiddenURLException,
			InvalidURLException, InterruptedException, IOException {

		if (!Tools.isUUID(code)) {
			throw new ForbiddenURLException();
		}

		AnswerSet answerSet;
		String draftid = "";
		if (isdraft) {
			Draft draft = answerService.getDraft(code);
			answerSet = draft.getAnswerSet();
			draftid = draft.getUniqueId();
		} else {
			answerSet = getAnswerSet(code, request);
		}

		Set<String> invisibleElements = new HashSet<>();
		if (answerSet != null) {
			if (answerSet.getSurvey().getIsEVote()){
				ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
				model.addObject(Constants.MESSAGE,
						resources.getMessage("error.EVoteContributionViewNotAllowed", null,
								"The survey has been closed and it is not possible to access the contribution anymore.",
								locale));
				return model;

			}
			// participants can only access it if the survey is still active
			User u;
			
			if (answerSet.getSurvey().getEcasSecurity() && request.getRequestURI().contains("editcontribution"))
			{
				u = sessionService.getCurrentUser(request, false);
			} else {
				u = sessionService.getCurrentUser(request);
			}

			Survey draft = surveyService.getSurveyByUniqueId(answerSet.getSurvey().getUniqueId(), false, true);

			if (u != null) {
				try {
					sessionService.upgradePrivileges(draft, u, request);
				} catch (ForbiddenURLException e) {
					u = null;
				}
			}
			
			boolean isPDF = request.getRequestURI().contains("preparecontribution")
					|| request.getRequestURI().contains("preparepublishedcontribution")
					|| request.getRequestURI().contains("preparedraft");

			if (isPDF) {
				// ignore authorization for PDF export
			} else if (u == null && !draft.getIsActive()) {
				ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
				model.addObject(Constants.MESSAGE,
						resources.getMessage("error.ContributionClosedSurvey", null,
								"The survey has been closed and it is not possible to access the contribution anymore.",
								locale));
				return model;
			} else if (u == null && !draft.getChangeContribution()) {
				ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
				model.addObject(Constants.MESSAGE,
						resources.getMessage("error.ContributionEditNotAllowed", null,
								"The survey has been closed and it is not possible to access the contribution anymore.",
								locale));
				return model;
			} else if (u == null) {
				// ok
			} else {
				boolean isDraft = request != null && request.getParameter("results-source") != null
						&& request.getParameter("results-source").equalsIgnoreCase("draft");
				if (!u.getId().equals(answerSet.getSurvey().getOwner().getId())
						&& u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
						&& u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1
						&& !(isDraft && u.getLocalPrivileges().get(LocalPrivilege.AccessDraft) == 2)
						&& (u.getResultAccess() == null || u.getResultAccess().isReadonly())) {
					throw new ForbiddenURLException();
				}
			}

			// this call raises an exception if the survey is archived
			if (draft.getArchived() && !request.getRequestURI().contains("preparecontribution")) {
				throw new InvalidURLException();
			}

			Survey newestSurvey = answerSet.getSurvey();

			if (useNewestSurvey) {
				newestSurvey = surveyService.getSurveyByUniqueId(answerSet.getSurvey().getUniqueId(), false,
						answerSet.getSurvey().getIsDraft());
				if (newestSurvey.getId() != answerSet.getSurveyId()) {
					answerSet.setSurvey(newestSurvey);
				}
			}
			surveyService.initializeSurvey(newestSurvey);

			// this is needed to initialize the invisibleElements
			SurveyHelper.validateAnswerSet(answerSet, answerService, invisibleElements, resources, locale, null, null,
					true, null, fileService);

			Form f = new Form(newestSurvey, translationService.getTranslationsForSurvey(newestSurvey.getId(), false),
					newestSurvey.getLanguage(), resources, contextpath);
			String lang = answerSet.getLanguageCode();

			Survey translated = SurveyHelper.createTranslatedSurvey(newestSurvey.getId(), lang, surveyService,
					translationService, true);
			f.setSurvey(translated);
			f.setLanguage(surveyService.getLanguage(lang));

			f.getAnswerSets().add(answerSet);
			f.setWcagCompliance(answerSet.getWcagMode() != null && answerSet.getWcagMode());

			ModelAndView model = new ModelAndView("contributions/edit", "form", f);
			
			if (!isPDF && newestSurvey.getIsDelphi() && newestSurvey.getIsDelphiShowStartPage() && request.getParameter("startDelphi") == null) {
				model = new ModelAndView("runner/delphi", "form", f);
				model.addObject("isdelphipage", true);
				model.addObject("iseditcontribution", true);
			}
			
			if (f.getSurvey().getIsSelfAssessment() && isPDF) {
				selfassessmentService.initializeForm(f, invisibleElements);
			}

			model.addObject("submit", true);
			model.addObject("answerSet", answerSet.getId());
			model.addObject("invisibleElements", invisibleElements);
			java.util.Date submittedDate = answerSet.getUpdateDate() != null ? answerSet.getUpdateDate()
					: answerSet.getDate();
			model.addObject("submittedDate", ConversionTools.getFullString(submittedDate));
			model.addObject("invitationToken", answerSet.getInvitationId());
			model.addObject("isEcf", answerSet.getSurvey().getIsECF());
			model.addObject("theUniqueCode", answerSet.getUniqueCode());
			model.addObject("surveyShortname", answerSet.getSurvey().getShortname());
			
			if (!fromBackOffice)
			{
				model.addObject("runnermode", true);
			}

			if (request.getParameter("mode") != null && request.getParameter("mode").equalsIgnoreCase("dialog")) {
				model.addObject("dialogmode", true);
			}

			// this code will be used as an identifier (for uploaded files etc)
			model.addObject(Constants.UNIQUECODE, answerSet.getUniqueCode());

			if (isdraft) {
				model.addObject("draftid", draftid);
			}

			validCodesService.revalidate(answerSet.getUniqueCode(), newestSurvey);

			SurveyHelper.recreateUploadedFiles(answerSet, translated, fileService, answerExplanationService);

			return model;
		} else {
			ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
			model.addObject(Constants.MESSAGE, resources.getMessage("error.ContributionNotLoaded", null,
					"The contribution could not be loaded.", locale));
			return model;
		}
	}

	@PostMapping(value = "/editcontribution/{code}")
	public ModelAndView processSubmit(@PathVariable String code, HttpServletRequest request, Locale locale) {

		try {
			Survey origsurvey = surveyService.getSurvey(Integer.parseInt(request.getParameter("survey.id")), false,
					true);
			User u = sessionService.getCurrentUser(request, !origsurvey.getEcasSecurity());
			String answerSetId = request.getParameter("IdAnswerSet");
			AnswerSet oldAnswerSet = answerService.get(Integer.parseInt(answerSetId));

			boolean dialogmode = request.getParameter("dialogmode") != null
					&& request.getParameter("dialogmode").equalsIgnoreCase("true");

			if (oldAnswerSet != null) {
				List<String> oldFileUIDs = oldAnswerSet.getAllFiles();

				if (oldAnswerSet.getSurvey().getIsEVote()){
					ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
					model.addObject(Constants.MESSAGE,
							resources.getMessage("error.EVoteContributionEditNotAllowed", null,
									"The survey has been closed and it is not possible to access the contribution anymore.",
									locale));
					return model;
				}

				String uniqueCode = request.getParameter(Constants.UNIQUECODE);
				SurveyHelper.parseAndMergeAnswerSet(request, origsurvey, uniqueCode, oldAnswerSet,
						oldAnswerSet.getLanguageCode(), null, fileService);

				Set<String> invisibleElements = new HashSet<>();

				Map<Element, String> validation = SurveyHelper.validateAnswerSet(oldAnswerSet, answerService,
						invisibleElements, resources, locale, request.getParameter("draftid"), request, true, u,
						fileService);
				if (validation.size() > 0) {
					Survey survey = origsurvey;
					if (request.getParameter("language.code") != null
							&& request.getParameter("language.code").length() == 2) {
						survey = surveyService.getSurvey(origsurvey.getId(), oldAnswerSet.getLanguageCode());
					}
					Form f = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), false),
							survey.getLanguage(), resources, contextpath);
					f.getAnswerSets().add(oldAnswerSet);
					f.setValidation(validation);
					f.setWcagCompliance(oldAnswerSet.getWcagMode() != null && oldAnswerSet.getWcagMode());
					ModelAndView model = new ModelAndView("contributions/edit", "form", f);
					model.addObject("submit", true);
					model.addObject("runnermode", true);
					model.addObject(Constants.UNIQUECODE, uniqueCode);
					model.addObject("answerSet", oldAnswerSet.getId());
					if (dialogmode) {
						model.addObject("dialogmode", dialogmode);
					}
					model.addObject(Constants.MESSAGE, resources.getMessage("error.CheckValidation", null,
							"Please check for validation errors.", locale));
					return model;
				}

				if (oldAnswerSet.getSurvey().getCaptcha() && !checkCaptcha(request)) {
					Survey survey = origsurvey;
					if (request.getParameter("language.code") != null
							&& request.getParameter("language.code").length() == 2) {
						survey = surveyService.getSurvey(origsurvey.getId(), oldAnswerSet.getLanguageCode());
					}
					Form f = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), false),
							survey.getLanguage(), resources, contextpath);
					f.getAnswerSets().add(oldAnswerSet);
					ModelAndView model = new ModelAndView("runner/runner", "form", f);
					surveyService.initializeSkin(f.getSurvey());
					model.addObject("submit", true);
					model.addObject("runnermode", true);
					model.addObject(Constants.UNIQUECODE, uniqueCode);
					model.addObject("answerSet", oldAnswerSet.getId());
					if (dialogmode) {
						model.addObject("dialogmode", dialogmode);
					}
					model.addObject("wrongcaptcha", "true");
					return model;
				}

				saveAnswerSet(oldAnswerSet, fileDir, null, u == null ? -1 : u.getId(), request);

				List<String> newFileUIDs = oldAnswerSet.getAllFiles();

				for (String uid : oldFileUIDs) {
					if (!newFileUIDs.contains(uid)) {
						fileService.deleteIfNotReferenced(uid, origsurvey.getUniqueId());
					}
				}

				if (dialogmode) {
					return new ModelAndView("close", "surveyprefix", origsurvey.getId());
				}

				if (origsurvey.getIsQuiz()  &&
								!(origsurvey.getConfirmationPageLink() != null && origsurvey.getConfirmationPageLink()
								&& origsurvey.getConfirmationLink() != null && origsurvey.getConfirmationLink().length() > 0)) {
					String lang = locale.getLanguage();
					if (request.getParameter("language.code") != null && request.getParameter("language.code").length() == 2) {
						lang = request.getParameter("language.code");
					}

					AnswerSet answerSet = answerService.automaticParseAnswerSet(request, origsurvey, uniqueCode, false, lang, u);
					ModelAndView result = new ModelAndView("runner/quizResult", Constants.UNIQUECODE, answerSet.getUniqueCode());
					Form form = new Form(resources, surveyService.getLanguage(lang),
							translationService.getActiveTranslationsForSurvey(answerSet.getSurvey().getId()), contextpath);
					sessionService.setFormStartDate(request, form, uniqueCode);
					form.setSurvey(origsurvey);
					form.getAnswerSets().add(answerSet);
					result.addObject(form);
					result.addObject("surveyprefix", origsurvey.getId());
					result.addObject("quiz", QuizHelper.getQuizResult(answerSet, invisibleElements));
					result.addObject("isquizresultpage", true);
					result.addObject("runnermode", true);
					result.addObject("invisibleElements", invisibleElements);

					return result;
				}
				
				if (origsurvey.getIsSelfAssessment()) {
					String lang = locale.getLanguage();
					if (request.getParameter("language.code") != null && request.getParameter("language.code").length() == 2) {
						lang = request.getParameter("language.code");
					}

					AnswerSet answerSet = answerService.automaticParseAnswerSet(request, origsurvey, uniqueCode, false, lang, u);
					
					ModelAndView result = new ModelAndView("runner/saResult", Constants.UNIQUECODE,
							answerSet.getUniqueCode());
					Form form = new Form(resources, surveyService.getLanguage(locale.getLanguage().toUpperCase()),
							translationService.getActiveTranslationsForSurvey(answerSet.getSurvey().getId()), contextpath);
					form.setSurvey(origsurvey);
					form.getAnswerSets().add(answerSet);
					result.addObject("invisibleElements", invisibleElements);
					result.addObject(form);
					result.addObject("surveyprefix", origsurvey.getId());
					result.addObject("issaresultpage", true);
					
					SAReportConfiguration config = selfassessmentService.getReportConfiguration(answerSet.getSurvey().getUniqueId());
					result.addObject("SAReportConfiguration", config);
					
					List<SATargetDataset> datasets = selfassessmentService.getTargetDatasets(answerSet.getSurvey().getUniqueId());
					result.addObject("SATargetDatasets", datasets);
								
					return result;
				}

				ModelAndView result = new ModelAndView("thanks", Constants.UNIQUECODE, oldAnswerSet.getUniqueCode());

				result.addObject("runnermode", true);

				Form form = new Form(resources, surveyService.getLanguage(oldAnswerSet.getLanguageCode()),
						translationService.getActiveTranslationsForSurvey(origsurvey.getId()), contextpath);
				form.setSurvey(origsurvey);
				
				if (origsurvey.getIsOPC()) {
					result.addObject("opcredirection",
							form.getFinalConfirmationLink(opcredirect, oldAnswerSet.getLanguageCode(), oldAnswerSet));
				}

				if(!origsurvey.getConfirmationPageLink()){
					form.getAnswerSets().add(oldAnswerSet);
				}

				result.addObject("form", form);
				result.addObject("text", origsurvey.getConfirmationPage());

				if (origsurvey.getConfirmationPageLink() != null && origsurvey.getConfirmationPageLink()
						&& origsurvey.getConfirmationLink() != null && origsurvey.getConfirmationLink().length() > 0) {
					result.addObject("redirect", form.getFinalConfirmationLink(oldAnswerSet.getLanguageCode(), oldAnswerSet));
				} else if (origsurvey.getEcasSecurity() && request.getParameter("passwordauthenticated") == null
						&& oldAnswerSet.getInvitationId() == null) {
					result.addObject("asklogout", true);
				}

				result.addObject("surveyprefix", origsurvey.getId());
				return result;
			} else {
				ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
				model.addObject(Constants.MESSAGE, resources.getMessage("error.ProblemDuringSave", null,
						"There was a problem during the save process.", locale));
				return model;
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ModelAndView("redirect:/errors/500.html");
		}
	}
	
	@RequestMapping(value = "/ecfResultJSON", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody ECFIndividualResult ecfResultJSON(HttpServletRequest request, Locale locale)
	throws NotFoundException, InternalServerErrorException {
		String answerSetIdOrNull = request.getParameter("answerSetId");
		String profileUUIDOrNull = request.getParameter("profileUUID");
		if (answerSetIdOrNull == null) {
			throw new NotFoundException();
		}
		AnswerSet answerSet = answerService.get(answerSetIdOrNull);
		
		ECFIndividualResult ecfResult;
		try {
			if (profileUUIDOrNull != null && !profileUUIDOrNull.isEmpty()) {
				ECFProfile profile = this.ecfService.getECFProfileByUUID(profileUUIDOrNull);
				ecfResult = this.ecfService.getECFIndividualResult(answerSet.getSurvey(), answerSet, profile);
			} else {
				ecfResult = this.ecfService.getECFIndividualResult(answerSet.getSurvey(), answerSet);
			}
		} catch (ECFException e) {
			throw new InternalServerErrorException(e);
		}

		return ecfResult;
	}

	@GetMapping(value = "/contribution/{uid}/preview")
	public ModelAndView preview(@PathVariable String uid, HttpServletRequest request, Locale locale)
			throws InterruptedException, IOException, InvalidURLException, ECFException {
		if (uid != null && uid.length() > 0) {
			AnswerSet answerSet = answerService.get(uid);
			if (answerSet != null) {
				if (answerSet.getSurvey().getIsEVote()){
					ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
					model.addObject(Constants.MESSAGE,
							resources.getMessage("error.EVoteContributionViewNotAllowed", null,
									"The survey has been closed and it is not possible to access the contribution anymore.",
									locale));
					return model;
				}
				Form form = new Form(resources);
				Set<String> invisibleElements = new HashSet<>();
				SurveyHelper.validateAnswerSet(answerSet, answerService, invisibleElements, resources, locale, null,
						request, true, null, fileService);

				if (answerSet.getLanguageCode() != null && !answerSet.getSurvey().getLanguage().getCode()
						.equalsIgnoreCase(answerSet.getLanguageCode())) {
					Survey translated = SurveyHelper.createTranslatedSurvey(answerSet.getSurvey().getId(), answerSet.getLanguageCode(),
							surveyService, translationService, true);
					form.setSurvey(translated);
					form.setLanguage(surveyService.getLanguage(answerSet.getLanguageCode()));
				} else {
					form.setSurvey(answerSet.getSurvey());
					form.setLanguage(answerSet.getSurvey().getLanguage());
				}
				form.getAnswerSets().add(answerSet);
				ModelAndView contributionsPrintModel = new ModelAndView("contributions/print", "form", form);
				contributionsPrintModel.addObject("answerSet", answerSet.getId());
				contributionsPrintModel.addObject("code", uid);
				contributionsPrintModel.addObject("isEcf", answerSet.getSurvey().getIsECF());
				java.util.Date submittedDate = answerSet.getUpdateDate() != null ? answerSet.getUpdateDate()
						: answerSet.getDate();
				contributionsPrintModel.addObject("submittedDate", ConversionTools.getFullString(submittedDate));
				contributionsPrintModel.addObject("print", true);
				

				if (answerSet.getSurvey().getIsECF()) {
					ECFIndividualResult individualResult = ecfService.getECFIndividualResult(answerSet.getSurvey(), answerSet);

					List<String> base64SpiderCharts =  ecfService.spiderChartsB64ByECFType(individualResult);
					contributionsPrintModel.addObject("base64ECFSpiderCharts", base64SpiderCharts);

					String b64 = ecfService.individualResultToSpiderChartB64(individualResult);
					contributionsPrintModel.addObject("base64ECFSpiderChart", b64);
					contributionsPrintModel.addObject("ecfIndividualResult", individualResult);
				}
				
				contributionsPrintModel.addObject("serverprefix", serverPrefix);
				contributionsPrintModel.addObject("invisibleElements", invisibleElements);
				contributionsPrintModel.addObject("launchPrint", false);
				return contributionsPrintModel;
			}
		}
		throw new InvalidURLException();
	}


	@RequestMapping(value = "/printcontribution", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView print(HttpServletRequest request, Locale locale) {

		String code = request.getParameter("code");
		Set<String> invisibleElements = new HashSet<>();

		if (code != null && code.length() > 0) {
			AnswerSet answerSet;
			try {
				answerSet = answerService.get(code);
				if (answerSet != null) {
					
					User currentUser = null;					
					try {
						currentUser = this.sessionService.getCurrentUser(request, false, false);
					} catch (NotAgreedToTosException | WeakAuthenticationException | NotAgreedToPsException e1) {
						//ignore
					}

					boolean authorized = currentUser != null && sessionService.userIsAnswerer(answerSet, currentUser); //userIsAnswerer is also true if user is owner of the survey
					
					if (!authorized) {
						if (answerSet.getSurvey() != null) {
							Survey draftSurvey = surveyService.getSurveyByUniqueId(answerSet.getSurvey().getUniqueId(), false,
									true);

							//check if user is form manager or is allowed to access results or form preview (including test anwers)
							if (currentUser != null && sessionService.userIsResultOrDraftReadAuthorized(draftSurvey, request)) {
								authorized = true;
							}
							
							if (!authorized) {						
								//check session token								
								String uniqueCode = (String) request.getSession().getAttribute(Constants.UNIQUECODE);

								if (uniqueCode != null && uniqueCode.equals(code)) {
									authorized = true;
								}
							}
						}
					}
					
					if (!authorized) {
						throw new ForbiddenException();
					}
					
					Form form = new Form(resources);
					String lang = answerSet.getLanguageCode();

					if (answerSet.getSurvey().getIsEVote()){
						ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
						model.addObject(Constants.MESSAGE,
								resources.getMessage("error.EVoteContributionViewNotAllowed", null,
										"The survey has been closed and it is not possible to access the contribution anymore.",
										locale));
						return model;
					}

					SurveyHelper.validateAnswerSet(answerSet, answerService, invisibleElements, resources, locale, null,
							request, true, null, fileService);

					if (lang != null && !answerSet.getSurvey().getLanguage().getCode().equalsIgnoreCase(lang)) {
						Survey translated = SurveyHelper.createTranslatedSurvey(answerSet.getSurvey().getId(), lang,
								surveyService, translationService, true);
						form.setSurvey(translated);
						form.setLanguage(surveyService.getLanguage(lang));
					} else {
						form.setSurvey(answerSet.getSurvey());
						form.setLanguage(answerSet.getSurvey().getLanguage());
					}

					form.getAnswerSets().add(answerSet);
					
					if (form.getSurvey().getIsSelfAssessment()) {
						selfassessmentService.initializeForm(form, invisibleElements);
					}

					ModelAndView contributionsPrintModel = new ModelAndView("contributions/print", "form", form);
					contributionsPrintModel.addObject("answerSet", answerSet.getId());
					contributionsPrintModel.addObject("code", code);
					contributionsPrintModel.addObject("isEcf", answerSet.getSurvey().getIsECF());
					if (answerSet.getSurvey().getIsECF()) {
						contributionsPrintModel.addObject("ecfIndividualResult", this.ecfService.getECFIndividualResult(answerSet.getSurvey(), answerSet));
					}

					java.util.Date submittedDate = answerSet.getUpdateDate() != null ? answerSet.getUpdateDate() : answerSet.getDate();
					contributionsPrintModel.addObject("submittedDate", ConversionTools.getFullString(submittedDate));
					contributionsPrintModel.addObject("print", true);
					contributionsPrintModel.addObject("serverprefix", serverPrefix);
					contributionsPrintModel.addObject("invisibleElements", invisibleElements);
					contributionsPrintModel.addObject("launchPrint", true); 

					return contributionsPrintModel;
				}
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}

		return new ModelAndView("redirect:/errors/500.html");

	}

	@DeleteMapping(value = "/contribution/{code}")
	public @ResponseBody String deleteContribution(@PathVariable String code, HttpServletRequest request, Locale locale)
			throws NotFoundException, ForbiddenException, InternalServerErrorException {
		User currentUser = null;
		AnswerSet answerSet = null;
		try {
			answerSet = this.getAnswerSet(code, request);
			currentUser = this.sessionService.getCurrentUser(request);
		} catch (NotAgreedToTosException | WeakAuthenticationException | NotAgreedToPsException e1) {
			throw new ForbiddenException();
		}

		if (answerSet == null) {
			throw new NotFoundException();
		}

		try {
			this.answerService.deleteAnswer(answerSet);
		} catch (IOException | MessageException e) {
			throw new InternalServerErrorException(e);
		}

		if (answerSet.getSurvey().getIsDraft()) {
			this.activityService.log(ActivityRegistry.ID_TEST_DELETE, answerSet.getUniqueCode(), null, currentUser.getId(),
					answerSet.getSurvey().getUniqueId());
		} else {
			this.activityService.log(ActivityRegistry.ID_CONTRIBUTION_DELETE, null, answerSet.getUniqueCode(), currentUser.getId(),
					answerSet.getSurvey().getUniqueId());
		}
		return "success";
	}

	@RequestMapping(value = "/downloadcontribution/{code}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView download(@PathVariable String code, HttpServletRequest request, HttpServletResponse response) {
		try {
			AnswerSet answerSet = getAnswerSet(code, request);
			if (answerSet != null) {
				ITextRenderer renderer = new ITextRenderer();
				renderer.setDocument(host + "showcontribution/" + code);
				renderer.layout();

				response.setHeader("Content-Disposition", "attachment;filename=answer.pdf");
				renderer.createPDF(response.getOutputStream());
				response.flushBuffer();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

}
