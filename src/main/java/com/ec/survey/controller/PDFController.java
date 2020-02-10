package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.ParticipationGroup;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.NotAgreedToPsException;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.SurveyExecutor;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.WeakAuthenticationException;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.HTTPUtilities;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

@Controller
@RequestMapping("/pdf")
public class PDFController extends BasicController {
	
	@Resource(name="pdfService")
	private PDFService pdfService;
	
	@Resource(name = "validCodesService")
	private ValidCodesService validCodesService;
	
	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;
	
	@RequestMapping(value = "/pubsurvey/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView pubsurvey(@PathVariable String id, HttpServletRequest request, Locale locale, HttpServletResponse response) throws ForbiddenURLException, InvalidURLException {
		try {
			String language = locale.getLanguage();
			Survey survey = surveyService.getSurvey(Integer.parseInt(id), false, true);
			
			if (survey == null)
			{
				throw new InvalidURLException();
			}
			
			if (survey.getIsDraft())
			{
				Survey published = surveyService.getSurvey(survey.getUniqueId(), false, false, false, false, survey.getLanguage().getCode(), true, false);
			
				if (published == null)
				{
					throw new InvalidURLException();
				}
			
				if (!survey.getShowPDFOnUnavailabilityPage())
				{
					throw new ForbiddenURLException();
				}
			}
			
			SendFile(survey, request, locale, response, language, id);
			
		} catch (NumberFormatException ne){
			throw new InvalidURLException();
		} catch (ForbiddenURLException | InvalidURLException fe) {
			throw fe;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);			
		}
		return null;
	}
	
	@RequestMapping(value = "/survey/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView survey(@PathVariable String id, HttpServletRequest request, Locale locale, HttpServletResponse response) throws ForbiddenURLException, InvalidURLException {
		try {
						
			String language = locale.getLanguage();
			
			Survey survey = surveyService.getSurvey(Integer.parseInt(id), false, true);
			
			if (survey == null)
			{
				throw new InvalidURLException();
			}
			
			if (!survey.getIsDraft())
			{
				//check if survey is published and user has access
				Survey draft = surveyService.getSurveyByUniqueId(survey.getUniqueId(), false, true);
				if (!draft.getIsPublished())
				{
					throw new ForbiddenURLException();
				}
				
				if (!draft.getSecurity().equalsIgnoreCase("open"))
				{
					String uniquecode = request.getParameter("unique");
					if (uniquecode == null)
					{
						throw new ForbiddenURLException();
					}
					
					//check password secured surveys
					if (!validCodesService.CheckValid(uniquecode, survey.getUniqueId())) {
						//check invitation based security					
						Invitation invitation = attendeeService.getInvitationByUniqueId(uniquecode);
						if (invitation == null || (invitation.getDeactivated() != null && invitation.getDeactivated()))
						{
							throw new ForbiddenURLException();
						}
						
						ParticipationGroup participationGroup = participationService.get(invitation.getParticipationGroupId());
						if (participationGroup == null || !participationGroup.getActive()) {
							throw new ForbiddenURLException();
						}
					}
				}
			} else {
				//check if user has access to draft
				User u = sessionService.getCurrentUser(request);
				
				if (u == null)
				{
					throw new ForbiddenURLException();
				}
				
				sessionService.upgradePrivileges(survey, u, request);
				
				if (!u.getId().equals(survey.getOwner().getId()))
				{
					if (u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2)
					{
						if (u.getLocalPrivileges().get(LocalPrivilege.AccessDraft) < 1 && u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 1)
						{
							throw new ForbiddenURLException();
						}
					}
				}
			}

			SendFile(survey, request, locale, response, language, id);

			return null;
			
		} catch (NumberFormatException ne){
			throw new InvalidURLException();
		} catch (ForbiddenURLException | InvalidURLException fe) {
			throw fe;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);			
		}
		return null;
	}
	
	private void SendFile(Survey survey, HttpServletRequest request, Locale locale, HttpServletResponse response, String language, String id) throws IOException
	{
			String lang = request.getParameter("lang");
			if (lang != null && lang.length() == 2 && StringUtils.isAlpha(lang)) {
				language = lang;
			}
			String name , value ;
			name = "Content-Disposition";
			if (survey.getIsDraft())
			{
				value =  "attachment;filename=" + survey.getShortname() + "_" + Tools.formatDate(new Date(), ConversionTools.DateFormat) + "_" + language + "_draft.pdf";
			} else {			
				value = "attachment;filename=" + survey.getShortname() + "_" + Tools.formatDate(survey.getUpdated(), ConversionTools.DateFormat) + "_" + language + ".pdf";
			}
			
			HTTPUtilities httpUtilities = ESAPI.httpUtilities();
			httpUtilities.setCurrentHTTP(request, response);
			httpUtilities.setHeader(name, value);
			
			response.setContentType("application/pdf");
			
			java.io.File folder = fileService.getSurveyExportsFolder(survey.getUniqueId());							
			java.io.File result = new java.io.File(String.format("%s/survey%s%s.pdf", folder.getPath(), survey.getId(), lang));	
			
			try {
			
				FileInputStream inputStream = new FileInputStream(result);
				IOUtils.copy(inputStream, response.getOutputStream());
				inputStream.close();
				
				response.flushBuffer();		
			} catch (ClientAbortException | FileNotFoundException c)
			{
				// ClientAbortException can happen if the user closes the browser during download
				// FileNotFoundException can happen as we create the files asynchronously and it might not have been created yet
			}
		}
	
	@RequestMapping(value = "/surveyexists/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String surveyexists(@PathVariable String id, HttpServletRequest request, Locale locale, HttpServletResponse response) {
		try {
			
			String lang = request.getParameter("lang");
			
			Survey survey = surveyService.getSurvey(Integer.parseInt(id), true);
			java.io.File folder = fileService.getSurveyExportsFolder(survey.getUniqueId());
						
			java.io.File target = new java.io.File(String.format("%s/survey%s%s.pdf", folder.getPath(), id, lang));	
			if (target.exists() && target.length() > 0)
			{
				return "exists";
			}
			
			SurveyExecutor export = (SurveyExecutor) context.getBean("surveyExecutor");
			export.init(surveyService.getSurvey(Integer.parseInt(id)), lang);
			taskExecutor.execute(export);
			
			return "wait";
			
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			return "error";
		}
	}
	
	@RequestMapping(value = "/surveyready/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String surveyready(@PathVariable String id, HttpServletRequest request, Locale locale, HttpServletResponse response) {
		try {
			String lang = request.getParameter("lang");
			
			Survey survey = surveyService.getSurvey(Integer.parseInt(id), true);
			java.io.File folder = fileService.getSurveyExportsFolder(survey.getUniqueId());
						
			java.io.File target = new java.io.File(String.format("%s/survey%s%s.pdf", folder.getPath(), id, lang));	
			if (target.exists() && target.length() > 0)
			{
				return "exists";
			}
			
			return "wait";
			
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			return "error";
		}
	}
	
	@RequestMapping(value = "/answerexists/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String answerexists(@PathVariable String code, HttpServletRequest request, Locale locale, HttpServletResponse response) {
		try {
			java.io.File target = new java.io.File(String.format("%sanswer%s.pdf", tempFileDir, code));
			if (target.exists() && target.length() > 0)
			{
				return "exists";
			}
			
			pdfService.createAnswerPDF(code, null);
			
			return "wait";
			
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			return "error";
		}
	}
	
	@RequestMapping(value = "/answerready/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String answerready(@PathVariable String code, HttpServletRequest request, Locale locale, HttpServletResponse response) {
		try {
			java.io.File target = new java.io.File(String.format("%sanswer%s.pdf", tempFileDir, code));
			if (target.exists() && target.length() > 0)
			{
				return "exists";
			}
			
			AnswerSet answerSet = answerService.get(code);
			
			if (answerSet == null) return "error";
			
			java.io.File folder = fileService.getSurveyExportsFolder(answerSet.getSurvey().getUniqueId());							
			target = new java.io.File(String.format("%s/answer%s.pdf", folder.getPath(), code));		

			if (target.exists() && target.length() > 0)
			{
				return "exists";
			}
			
			return "wait";
			
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			return "error";
		}
	}
		
	@RequestMapping(value = "/answer/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView answer(@PathVariable String code, HttpServletRequest request, Locale locale, HttpServletResponse response) throws InvalidURLException, NotAgreedToTosException, ForbiddenURLException, WeakAuthenticationException, NotAgreedToPsException {
		User user = sessionService.getCurrentUser(request);
		
		if (user == null)
		{
			throw new ForbiddenURLException();
		}
		
		if (code == null || !StringUtils.isAlphanumeric(code.replace("-", "")))
		{
			throw new InvalidURLException();
		}
		
		try {
			
			java.io.File result = new java.io.File(String.format("%sanswer%s.pdf", tempFileDir, code)); //pdfService.createAnswerPDF(answerSet, null);
			
			AnswerSet answerSet = answerService.get(code);
			
			if (!result.exists() && answerSet != null)
			{
				java.io.File folder = fileService.getSurveyExportsFolder(answerSet.getSurvey().getUniqueId());							
				result = new java.io.File(String.format("%s/answer%s.pdf", folder.getPath(), code));		
			}
			
			if (result.exists())
			{				
				Survey survey = surveyService.getSurveyByUniqueId(answerSet.getSurvey().getUniqueId(), false, true);
		
				try {
					sessionService.upgradePrivileges(survey, user, request);
				} catch (ForbiddenURLException fex) {
					//user is no form manager
				}
				
				if (!user.getId().equals(survey.getOwner().getId()))
				{
					if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2)
					{
						if (user.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1)
						{
							if (user.getLocalPrivileges().get(LocalPrivilege.AccessDraft) < 2)
							{
								if (!(answerSet.getResponderEmail() != null && user.getEmail() != null && (answerSet.getResponderEmail().equalsIgnoreCase(user.getEmail()) || answerSet.getResponderEmail().equalsIgnoreCase(Tools.md5hash(user.getEmail())))))
								{
									throw new ForbiddenURLException();
								}
							}
						}
					}
				}				
				
				response.setHeader("Content-Disposition", "attachment;filename=" + answerSet.getSurvey().getShortname() + "_" + code + ".pdf");
				response.setContentType("application/pdf");
								
				FileInputStream inputStream = new FileInputStream(result);
				IOUtils.copy(inputStream, response.getOutputStream());
				inputStream.close();
				
				response.flushBuffer();			
			} else {
				throw new InvalidURLException();
			}
		
			return null;
		} catch (ForbiddenURLException fe) {
			throw fe;
		} catch (InvalidURLException ie) {
			throw ie;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);			
		}
		throw new InvalidURLException();
	}	
		
}
