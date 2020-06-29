package com.ec.survey.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.MailService;
import com.ec.survey.tools.Ucs2Utf8;

@Controller
@RequestMapping("/administration/publicsurveys")
public class PublicSurveysController extends BasicController {
	
	@Resource(name="mailService")
	private MailService mailService;
	
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView publicsurveys(HttpServletRequest request, Model model, Locale locale) {	
		String filteralias = request.getParameter("filteralias");		
		if (filteralias == null) filteralias = (String) request.getSession().getAttribute("publicsurveysfilteralias");
		
		String filterowner = request.getParameter("filterowner");
		if (filterowner == null) filterowner = (String) request.getSession().getAttribute("publicsurveysfilterowner");
		
		String filterrequestdatefrom = request.getParameter("filterrequestdatefrom");
		if (filterrequestdatefrom == null) filterrequestdatefrom = (String) request.getSession().getAttribute("publicsurveysfilterrequestdatefrom");
		
		String filterrequestdateto = request.getParameter("filterrequestdateto");
		if (filterrequestdateto == null) filterrequestdateto = (String) request.getSession().getAttribute("publicsurveysfilterrequestdateto");
		
		List<Survey> surveys = surveyService.getPublicSurveysForValidation(filteralias, filterowner, filterrequestdatefrom, filterrequestdateto);		
    	ModelAndView m =  new ModelAndView("administration/publicsurveys", "surveys", surveys);
    	m.addObject("sender",sender);
    	String error = request.getParameter("error");
    	if (error != null) m.addObject("error",resources.getMessage("error.OperationFailed", null, "", locale));
    	
    	m.addObject("filteralias", filteralias);
    	m.addObject("filterowner", filterowner);
    	m.addObject("filterrequestdatefrom", filterrequestdatefrom);
    	m.addObject("filterrequestdateto", filterrequestdateto);
    	m.addObject("host",serverPrefix);
    	
    	request.getSession().setAttribute("publicsurveysfilteralias", filteralias);
    	request.getSession().setAttribute("publicsurveysfilterowner", filterowner);
    	request.getSession().setAttribute("publicsurveysfilterrequestdatefrom", filterrequestdatefrom);
    	request.getSession().setAttribute("publicsurveysfilterrequestdateto", filterrequestdateto);
    	
    	return m;
	}
	
	@PostMapping(value = "/accept", headers = "Accept=*/*")
	public String accept(HttpServletRequest request, HttpServletResponse response) {
		try {
			HashMap<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
			
			String id = parameters.get("id")[0];
			Survey survey = surveyService.getSurvey(Integer.parseInt(id), false);
		
			if (survey != null) {
				
				survey.setListFormValidated(true);
				surveyService.update(survey, false);
				
				String email = survey.getOwner().getEmail();
								
				String body = parameters.get("text")[0] + "<br /><br />" + parameters.get("signature")[0];
				String replyto = parameters.get("replyto")[0];
				String subject = parameters.get("subject")[0];
				
				InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
				String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]",serverPrefix);
								
				mailService.SendHtmlMail(email, sender, replyto, subject, text, smtpServer, Integer.parseInt(smtpPort), null);
			
				return "redirect:/administration/publicsurveys?done=accept";
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return "redirect:/administration/publicsurveys?error=accept";
	}
	
	@PostMapping(value = "/decline", headers = "Accept=*/*")
	public String decline(HttpServletRequest request, HttpServletResponse response) {
		try {
			HashMap<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
			
			String id = parameters.get("id")[0];
			Survey survey = surveyService.getSurvey(Integer.parseInt(id), false);
		
			if (survey != null) {
				
				survey.setListForm(false);
				surveyService.update(survey, false);
				
				String email = survey.getOwner().getEmail();
								
				String body = parameters.get("text")[0] + "<br /><br />" + parameters.get("signature")[0];
				String replyto = parameters.get("replyto")[0];			
				String subject = parameters.get("subject")[0];
				
				InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
				String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]",serverPrefix);
								
				mailService.SendHtmlMail(email, sender, replyto, subject, text, smtpServer, Integer.parseInt(smtpPort), null);
			
				return "redirect:/administration/publicsurveys?done=decline";
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return "redirect:/administration/publicsurveys?error=accept";
	}
		
}
