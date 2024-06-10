package com.ec.survey.tools;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Draft;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.AttendeeService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.PDFService;
import com.ec.survey.service.ParticipationService;
import com.ec.survey.service.SurveyService;

@Service("answerExecutor")
@Scope("prototype")
public class AnswerExecutor implements Runnable {

	@Resource(name="pdfService")
	private PDFService pdfService;
	
	@Resource(name="mailService")
	private MailService mailService;
	
	@Resource(name="answerService")
	private AnswerService answerService;
	
	@Resource(name="attendeeService")
	private AttendeeService attendeeService;
	
	@Resource(name="participationService")
	private ParticipationService participationService;
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	public @Autowired ServletContext servletContext;
	
	private AnswerSet answerSet;
	private String email;
	private String from;
	private String host;
	private boolean pwAuthenticated;
	
	private static final Logger logger = Logger.getLogger(AnswerExecutor.class);
	
	public void init(AnswerSet answerSet, String email, String from, String host, boolean pwAuthenticated)
	{
		this.answerSet = answerSet;
		this.email = email;
		this.from = from;
		this.host = host;
		this.pwAuthenticated = pwAuthenticated;
	}
	
	public void init(AnswerSet answerSet)
	{
		this.answerSet = answerSet;
	}
	
	public void run()
	{
		try {
			String contributionordraft = answerSet.getIsDraft() ? "draft" : "contribution";
			java.io.File file;
			
			String code = answerSet.getUniqueCode();
			String draftid = "";
			
			if (answerSet.getIsDraft())
			{
				Draft draft = answerService.getDraftByAnswerUID(answerSet.getUniqueCode());
				code = draft.getUniqueId();
				draftid = draft.getUniqueId();
			}
			
			file = pdfService.createAnswerPDF(answerSet.getId(), code, answerSet.getSurvey().getUniqueId(), answerSet.getIsDraft());
	    				
			if (file != null && this.email != null && this.email.length() > 0)
			{
				Survey survey = surveyService.getSurveyInOriginalLanguage(answerSet.getSurvey().getId(), answerSet.getSurvey().getShortname(), answerSet.getSurvey().getUniqueId());
				String body = "Dear EUSurvey user,<br /><br />A PDF copy of your " + contributionordraft + " to survey '<b>" + survey.cleanTitle() + "</b>' has been created and is attached to this email.";
			
				if (answerSet.getIsDraft())
				{
					String link = answerService.getDraftURL(answerSet, draftid, pwAuthenticated);
					body += "<br /><br />If you want to continue working on your draft contribution, please click on the link below.<br />";
					body += "<a href=\"" + link + "\">" + link + "</a>";
				}
				
				body += "<br /><br />Your EUSurvey team";
				
				InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
				String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]",host);
								
				logger.info("finished creation of answer pdf for " + contributionordraft + " " + answerSet.getUniqueCode() + " to be sent to " + email);
				mailService.SendHtmlMail(email, from, from, "Copy of your PDF " + contributionordraft, text, file, answerSet.getUniqueCode());
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
