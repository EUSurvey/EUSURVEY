package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Draft;
import com.ec.survey.model.Form;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.NotAgreedToPsException;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.QuizHelper;
import com.ec.survey.tools.SurveyHelper;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.WeakAuthenticationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Controller
public class ContributionController extends BasicController {
	
	private @Value("${server.prefix}") String host;	
	
	public AnswerSet getAnswerSet(String code, HttpServletRequest request) throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException
	{
		AnswerSet answerSet = null;
		User user = sessionService.getCurrentUser(request);
		try {
			answerSet = answerService.get(code);
			
			if (user == null) {
				//comes from "Access a Contribution" -> check
				return answerSet;
			}
			
			if (answerSet != null && answerSet.getSurvey() != null && answerSet.getSurvey().getOwner().getId().equals(user.getId()))
			{
				//owner is always allowed to retrieve data
				return answerSet;
			}
			
			if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2 || (answerSet != null && answerSet.getResponderEmail() != null && user.getEmail() != null && (answerSet.getResponderEmail().equalsIgnoreCase(user.getEmail()) || answerSet.getResponderEmail().equalsIgnoreCase(Tools.md5hash(user.getEmail()))))) {
				return answerSet;
			}
			
			Survey draft = surveyService.getSurvey(answerSet.getSurvey().getShortname(), true, false, false, false, null, true, false);
			
			if (draft == null) draft = surveyService.getSurveyByUniqueId(answerSet.getSurvey().getUniqueId(), false, true);
			
			//if participants are allowed to change their contribution
			if (draft != null && draft.getChangeContribution())
			{
				return answerSet;
			}	
			
			sessionService.upgradePrivileges(draft, user, request);
			
			if (user.getLocalPrivileges().get(LocalPrivilege.AccessResults) == 2 ) {
				return answerSet;
			}
		} catch (Exception e) {			
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
	
	
	@RequestMapping(value = "/preparequizresults/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView quizresults(@PathVariable String code, Locale locale, HttpServletRequest request) throws Exception {	
		AnswerSet answerSet = getAnswerSet(code, request);
		
		Set<String> invisibleElements = new HashSet<>();
		
		//this is needed to initialize the invisibleElements
		SurveyHelper.validateAnswerSet(answerSet, answerService, invisibleElements, resources, locale, null, null, true, null, fileService);	
		
		ModelAndView result = new ModelAndView("runner/quizResult", "uniqueCode", answerSet.getUniqueCode());
		Form form = new Form(resources, surveyService.getLanguage(answerSet.getLanguageCode().toUpperCase()), translationService.getActiveTranslationsForSurvey(answerSet.getSurvey().getId()),contextpath);
	
		String lang = answerSet.getLanguageCode();
		Survey translated = SurveyHelper.createTranslatedSurvey(answerSet.getSurvey().getId(), lang, surveyService, translationService, true);
		form.setSurvey(translated);
		form.setLanguage(surveyService.getLanguage(lang));
	
		form.getAnswerSets().add(answerSet);
		result.addObject(form);
		result.addObject("surveyprefix", answerSet.getSurvey().getId() + ".");
		result.addObject("quiz", QuizHelper.getQuizResult(answerSet));
		form.setForPDF(true);
		result.addObject("forpdf", "true");
		result.addObject("invisibleElements", invisibleElements);
		return result;
	}
	
	@RequestMapping(value = "/preparecontribution/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView preparecontribution(@PathVariable String code, Locale locale, HttpServletRequest request) throws Exception {	
		ModelAndView result = editContribution(code, locale, request, false, false, true, false);
		
		Form f = (Form)result.getModel().get("form");
		SurveyHelper.calcTableWidths(f.getSurvey(), surveyService, f);
		f.setForPDF(true);
		result.addObject("forpdf", "true");
		result.addObject("submit", "false");	
		return result;
	}
	
	@RequestMapping(value = "/preparedraft/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView preparedraft(@PathVariable String code, Locale locale, HttpServletRequest request) throws Exception {	
		ModelAndView result = editContribution(code, locale, request, false, false, true, true);
		
		Form f = (Form)result.getModel().get("form");
		SurveyHelper.calcTableWidths(f.getSurvey(), surveyService, f);
		f.setForPDF(true);
		result.addObject("forpdf", "true");
		result.addObject("submit", "false");	
		return result;
	}
	
	@RequestMapping(value = "/preparepublishedcontribution/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView showforpublishedpdf(@PathVariable String id, Locale locale, HttpServletRequest request) throws Exception {	
		AnswerSet answerSet = answerService.get(Integer.parseInt(id));
		ModelAndView result = editContribution(answerSet.getUniqueCode(), locale, request, false, false, true, false);
		result.addObject("forpdf", "true");
		result.addObject("submit", "false");
		
		Form form = (Form) result.getModel().get("form");
		form.setForPDF(true);
		
		result.addObject("publication", form.getSurvey().getPublication());
		return result;
	}
	
	@RequestMapping(value = "/editcontribution/{code}/back", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView editcontributionfrombackoffice(@PathVariable String code, Locale locale, HttpServletRequest request) throws Exception {		
		return editContribution(code, locale, request, true, true, false, false);
	}
	
	@RequestMapping(value = "/editcontribution/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView editcontribution(@PathVariable String code, Locale locale, HttpServletRequest request) throws Exception {		
		return editContribution(code, locale, request, false, true, false, false);
	}
	
	private ModelAndView editContribution(String code, Locale locale, HttpServletRequest request, boolean fromBackOffice, boolean useNewestSurvey, boolean forpdf, boolean isdraft) throws Exception {	
		
		AnswerSet answerSet;
		String draftid = "";
		if (isdraft)
		{
			Draft draft = answerService.getDraft(code);
			answerSet = draft.getAnswerSet();
			draftid = draft.getUniqueId();
		} else {
			answerSet = getAnswerSet(code, request);	
		}

		Set<String> invisibleElements = new HashSet<>();
		if (answerSet != null)
		{
			//participants can only access it if the survey is still active
			User u = sessionService.getCurrentUser(request);
			
			Survey draft = surveyService.getSurveyByUniqueId(answerSet.getSurvey().getUniqueId(), false, true);
			
			if (u != null)
			{
				try {
					sessionService.upgradePrivileges(draft, u, request);
				} catch (ForbiddenURLException e) {
					u = null;
				}
			}				
			
			if (request.getRequestURI().contains("preparecontribution") || request.getRequestURI().contains("preparepublishedcontribution") || request.getRequestURI().contains("preparedraft"))
			{
				//ignore authorization for PDF export
			} else if (u == null && !draft.getIsActive())
			{
				ModelAndView model = new ModelAndView("error/generic");
				model.addObject("message", resources.getMessage("error.ContributionClosedSurvey", null, "The survey has been closed and it is not possible to access the contribution anymore.", locale));
				return model;
			} else if (u == null && !draft.getChangeContribution())
			{
				ModelAndView model = new ModelAndView("error/generic");
				model.addObject("message", resources.getMessage("error.ContributionEditNotAllowed", null, "The survey has been closed and it is not possible to access the contribution anymore.", locale));
				return model;
			}
			else if (u == null)
			{
				//ok
			} else {
				if (!u.getId().equals(answerSet.getSurvey().getOwner().getId()))
				{
					if (u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2)
					{
						if (u.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1)
						{
							throw new ForbiddenURLException();
						}
					}
				}
			}
			
			//this call raises an exception if the survey is archived
			if (draft.getArchived() && !request.getRequestURI().contains("preparecontribution"))
			{
				throw new InvalidURLException();
			}
						
			Survey newestSurvey = answerSet.getSurvey();
			
			if (useNewestSurvey)
			{
				newestSurvey = surveyService.getSurveyByUniqueId(answerSet.getSurvey().getUniqueId(), false, answerSet.getSurvey().getIsDraft());
				if (newestSurvey.getId() != answerSet.getSurveyId())
				{
					answerSet.setSurvey(newestSurvey);
				}
			}
			
			//this is needed to initialize the invisibleElements
			SurveyHelper.validateAnswerSet(answerSet, answerService, invisibleElements, resources, locale, null, null, true, null, fileService, forpdf);	
			
			Form f = new Form(newestSurvey, translationService.getTranslationsForSurvey(newestSurvey.getId(), false), newestSurvey.getLanguage(), resources,contextpath);
			String lang = answerSet.getLanguageCode();
			
			Survey translated = SurveyHelper.createTranslatedSurvey(newestSurvey.getId(), lang, surveyService, translationService, true);
			f.setSurvey(translated);
			f.setLanguage(surveyService.getLanguage(lang));
		
			f.getAnswerSets().add(answerSet);
			f.setWcagCompliance(answerSet.getWcagMode() != null && answerSet.getWcagMode());
						
			ModelAndView model = new ModelAndView("contributions/edit", "form", f);
			
			model.addObject("submit", true);
			model.addObject("answerSet", answerSet.getId());
			model.addObject("invisibleElements", invisibleElements);
			java.util.Date submittedDate = answerSet.getUpdateDate() != null ? answerSet.getUpdateDate() : answerSet.getDate();
			model.addObject("submittedDate", ConversionTools.getFullString(submittedDate));
			model.addObject("invitationToken", answerSet.getInvitationId());
			
			if (!fromBackOffice)
			{
				model.addObject("runnermode", true);
			}
			
			if (request.getParameter("mode") != null && request.getParameter("mode").equalsIgnoreCase("dialog"))
			{
				model.addObject("dialogmode", true);
			}					
					
			// this code will be used as an identifier (for uploaded files etc)
			model.addObject("uniqueCode", answerSet.getUniqueCode()); 
			
			if (isdraft) {
				model.addObject("draftid", draftid);
			}
			
			//recreate uploaded files
			SurveyHelper.recreateUploadedFiles(answerSet, fileDir, translated, fileService);
			
			return model;
		} else {
			ModelAndView model = new ModelAndView("error/generic");
			model.addObject("message", resources.getMessage("error.ContributionNotLoaded", null, "The contribution could not be loaded.", locale));
			return model;
		}
	}	
	
	@RequestMapping(value = "/editcontribution/{code}", method = RequestMethod.POST)
	public ModelAndView processSubmit(@PathVariable String code, HttpServletRequest request, Locale locale) {

		try {
			User u = sessionService.getCurrentUser(request);
			Survey origsurvey = surveyService.getSurvey(Integer.parseInt(request.getParameter("survey.id")), false, true);		
			String answerSetId = request.getParameter("IdAnswerSet");			
			AnswerSet oldAnswerSet = answerService.get(Integer.parseInt(answerSetId));
			
			boolean dialogmode = request.getParameter("dialogmode") != null && request.getParameter("dialogmode").equalsIgnoreCase("true");
						
			if (oldAnswerSet != null)
			{
				List<String> oldFileUIDs = oldAnswerSet.getAllFiles();				
				
				String uniqueCode = request.getParameter("uniqueCode");				
				SurveyHelper.parseAndMergeAnswerSet(request, origsurvey, fileDir, uniqueCode, oldAnswerSet, oldAnswerSet.getLanguageCode(), null, fileService);			
							
				Set<String> invisibleElements = new HashSet<>();

				HashMap<Element, String> validation = SurveyHelper.validateAnswerSet(oldAnswerSet, answerService, invisibleElements, resources, locale, request.getParameter("draftid"), request, true, u, fileService);
				if (validation.size() > 0) {
					Survey survey = origsurvey;
					if (request.getParameter("language.code") != null && request.getParameter("language.code").length() == 2) {
						survey = surveyService.getSurvey(origsurvey.getId(), oldAnswerSet.getLanguageCode());
					}
					Form f = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), true), survey.getLanguage(), resources,contextpath);
					f.getAnswerSets().add(oldAnswerSet);
					f.setValidation(validation);
					f.setWcagCompliance(oldAnswerSet.getWcagMode() != null && oldAnswerSet.getWcagMode());
					ModelAndView model = new ModelAndView("contributions/edit", "form", f);
					model.addObject("submit", true);
					model.addObject("runnermode", true);
					model.addObject("uniqueCode", uniqueCode);
					model.addObject("answerSet", oldAnswerSet.getId());
					if (dialogmode)
					{
						model.addObject("dialogmode", dialogmode);
					}
					model.addObject("message", resources.getMessage("error.CheckValidation", null, "Please check for validation errors.", locale));
					return model;
				}
				
				if (oldAnswerSet.getSurvey().getCaptcha()) {
					if (!checkCaptcha(request)) {
						Survey survey = origsurvey;
						if (request.getParameter("language.code") != null && request.getParameter("language.code").length() == 2) {
							survey = surveyService.getSurvey(origsurvey.getId(), oldAnswerSet.getLanguageCode());
						}
						Form f = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), true), survey.getLanguage(), resources,contextpath);
						f.getAnswerSets().add(oldAnswerSet);
						ModelAndView model = new ModelAndView("runner/runner", "form", f);
						surveyService.initializeSkin(f.getSurvey());
						model.addObject("submit", true);
						model.addObject("runnermode", true);
						model.addObject("uniqueCode", uniqueCode);
						model.addObject("answerSet", oldAnswerSet.getId());
						if (dialogmode)
						{
							model.addObject("dialogmode", dialogmode);
						}
						model.addObject("wrongcaptcha", "true");
						return model;
					}
				}

				saveAnswerSet(oldAnswerSet, fileDir, null, u == null ? -1 : u.getId());
				
				List<String> newFileUIDs = oldAnswerSet.getAllFiles();
				
				for (String uid : oldFileUIDs)
				{
					if (!newFileUIDs.contains(uid))
					{
						fileService.deleteIfNotReferenced(uid, origsurvey.getUniqueId());						
					}
				}
				
				if (dialogmode)
				{
					return new ModelAndView("close", "surveyprefix", origsurvey.getId() + ".");
				}
				
				ModelAndView result = new ModelAndView("thanks", "uniqueCode", oldAnswerSet.getUniqueCode());
				
				if (origsurvey.getIsOPC())
				{
					result.addObject("opcredirection", origsurvey.getFinalConfirmationLink(opcredirect, oldAnswerSet.getLanguageCode()));
				}
				
				result.addObject("runnermode", true);
				
				Form form = new Form(resources, surveyService.getLanguage(oldAnswerSet.getLanguageCode()), translationService.getActiveTranslationsForSurvey(origsurvey.getId()),contextpath);
				form.setSurvey(origsurvey);
				
				result.addObject("form", form);
				result.addObject("text", origsurvey.getConfirmationPage());	
					
				if (origsurvey.getConfirmationPageLink() != null && origsurvey.getConfirmationPageLink() && origsurvey.getConfirmationLink() != null && origsurvey.getConfirmationLink().length() > 0) {
					result.addObject("redirect", origsurvey.getFinalConfirmationLink(oldAnswerSet.getLanguageCode()));
				} else if (origsurvey.getEcasSecurity() && request.getParameter("passwordauthenticated") == null && oldAnswerSet.getInvitationId() == null) {
					result.addObject("asklogout", true);
				}
				result.addObject("surveyprefix", origsurvey.getId() + ".");
				return result;
			} else {
				ModelAndView model = new ModelAndView("error/generic");
				model.addObject("message", resources.getMessage("error.ProblemDuringSave", null, "There was a problem during the save process.", locale));
				return model;
			}
			
		
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ModelAndView("redirect:/errors/500.html");
		}
	}
	
	@RequestMapping(value = "/printcontribution", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView print(HttpServletRequest request, Locale locale) {

		String code = request.getParameter("code");
		Set<String> invisibleElements = new HashSet<>();
		
		if (code != null && code.length() > 0)
		{
			AnswerSet answerSet;
			try {
				answerSet = answerService.get(code);
				
				if (answerSet != null)
				{
					Form f = new Form(resources);
					String lang = answerSet.getLanguageCode();
					
					SurveyHelper.validateAnswerSet(answerSet, answerService, invisibleElements, resources, locale, null, request, true, null, fileService);
					
					if (lang != null && !answerSet.getSurvey().getLanguage().getCode().equalsIgnoreCase(lang))
					{
						Survey translated = SurveyHelper.createTranslatedSurvey(answerSet.getSurvey().getId(), lang, surveyService, translationService, true);
						f.setSurvey(translated);
						f.setLanguage(surveyService.getLanguage(lang));
					} else {
						f.setSurvey(answerSet.getSurvey());
						f.setLanguage(answerSet.getSurvey().getLanguage());
					}
					
					f.getAnswerSets().add(answerSet);
				
					ModelAndView model = new ModelAndView("contributions/print", "form", f);
					model.addObject("answerSet", answerSet.getId());
					model.addObject("code", code);
					java.util.Date submittedDate = answerSet.getUpdateDate() != null ? answerSet.getUpdateDate() : answerSet.getDate();
					model.addObject("submittedDate", ConversionTools.getFullString(submittedDate));
					model.addObject("print", true);
					model.addObject("serverprefix", serverPrefix);
					model.addObject("invisibleElements", invisibleElements);
					
					return model;
				}
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}

		return new ModelAndView("redirect:/errors/500.html");

	}
	

	@RequestMapping(value = "/deletecontribution/{code}", method = RequestMethod.POST)
	public @ResponseBody String delete(@PathVariable String code, HttpServletRequest request, Locale locale) {	
		try {
			AnswerSet answerSet = getAnswerSet(code, request);
			if (answerSet != null)
			{
				answerService.delete(answerSet);
				if (answerSet.getSurvey().getIsDraft())
				{
					activityService.log(405, answerSet.getUniqueCode(), null, sessionService.getCurrentUser(request).getId(), answerSet.getSurvey().getUniqueId());
				} else {
					activityService.log(402, null, answerSet.getUniqueCode(), sessionService.getCurrentUser(request).getId(), answerSet.getSurvey().getUniqueId());
				}
				return "success";
			}	
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);			
		}
		
		return resources.getMessage("error.DeletionFailed", null, "Deletion failed.", locale);
	}
	
	@RequestMapping(value = "/downloadcontribution/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView download(@PathVariable String code, HttpServletRequest request, HttpServletResponse response) {	
		try {
			AnswerSet answerSet = getAnswerSet(code, request);
			if (answerSet != null)
			{
				ITextRenderer renderer = new ITextRenderer();
				renderer.setDocument(host + "showcontribution/" + code);
				renderer.layout();
					
				response.setHeader("Content-Disposition", "attachment;filename=answer.pdf");
				renderer.createPDF(response.getOutputStream());
				response.flushBuffer();				
			
				return null;
			}	
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);			
		}
		return null;
	}
	
}
