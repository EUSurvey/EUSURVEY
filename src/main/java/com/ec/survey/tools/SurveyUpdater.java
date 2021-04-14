package com.ec.survey.tools;

import com.ec.survey.model.Access;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.ActivityService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.SurveyService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("surveyWorker")
@Scope("singleton")
public class SurveyUpdater implements Runnable {

	protected static final Logger logger = Logger.getLogger(SurveyUpdater.class);

	@Resource(name = "surveyService")
	private SurveyService surveyService;
	
	@Resource(name = "activityService")
	private ActivityService activityService;
	
	@Resource(name="mailService")
	private MailService mailService;
	
	public @Autowired ServletContext servletContext;
	
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${sender}") String sender;
	private @Value("${server.prefix}") String host;
	
	@Autowired
	protected MessageSource resources;
	
	@Override
	public void run() {
		try {
			logger.info("SurveyUpdater started");
			
			//publish
			List<Survey> surveys = surveyService.getSurveysToStart();
			for (Survey survey: surveys)
			{				
				Survey published = surveyService.getSurvey(survey.getShortname(), false, false, false, true, null, true, false);
				
				if (published != null) {
					try {
						surveyService.applyChanges(survey, false, -1, true);
					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				} else {
					surveyService.publish(survey, -1, -1, false, -1, false, true);
				}
			
				try {
					surveyService.activate(survey, false, -1);
					activityService.log(106, "unpublished", "published", -1, survey.getUniqueId());
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}				
			}
			
			logger.info("Starting completed, starting notifications");
			
			//notify
			try {
			surveys = surveyService.getSurveysToNotify();
			for (Survey survey: surveys)
			{
				StringBuilder usermails = new StringBuilder(" ");
				
				//skip sending mails for surveys whose end date has exceeded (e.g. when there was a problem with the automatic unpublishing)
				Date today = DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH);
				if (!survey.getEnd().before(today))
				{
					List<User> users = new ArrayList<>();
					users.add(survey.getOwner());
					if (survey.getNotifyAll())
					{
						List<Access> accesses = surveyService.getAccesses(survey.getId());
						for (Access access: accesses)
						{
							if (access.getLocalPrivileges().get(LocalPrivilege.FormManagement) > 0 && access.getUser() != null)
							{
								users.add(access.getUser());
							}
						}				
					}
			
					for (User user: users)
					{
						if (user != null && user.getEmail() != null && user.getEmail().trim().length() > 0)
						{
							String body = "Dear " + user.getFirstLastName() + ",<br /><br />Your survey '<b>" + survey.cleanTitle() + "</b>' will end on the " + Tools.formatDate(survey.getEnd(), ConversionTools.DateFormat) + " at " + Tools.formatDate(survey.getEnd(), "HH:mm") + ".<br />";
							body += "At this time your survey will automatically be unpublished. <br />";
							body += "To open your form managing area directly, please follow this link:<br />";
							body += "<a href='[HOST]" + survey.getShortname() + "/management/overview'>[HOST]" + survey.getShortname() + "/management/overview</a><br /><br />";
							body += "Your EUSurvey team";
							
							InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
							String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]",host);
							
							String subject = "Your survey '" + survey.cleanTitleForMailSubject() + "' will end soon";
							
							mailService.SendHtmlMail(user.getEmail(), sender, sender, subject, text, null);
							usermails.append(user.getEmail()).append(";");
						} else {
							logger.error("User " + user.getName() + " has no email address! Writing notification email not possible");
						}
					}
				}
				
				survey.setNotified(true);
				surveyService.update(survey, true);
				
				activityService.log(701, null, ConversionTools.getFullString(survey.getEnd()) + usermails, -1, survey.getUniqueId());
			}
			} catch (Exception e)
			{
				logger.error(e.getLocalizedMessage(), e);
			}
			
			logger.info("Notifications completed, starting unpublishing");
			
			//unpublish
			surveys = surveyService.getSurveysToStop();
			for (Survey survey: surveys)
			{
				surveyService.unpublish(survey, true, -1);
				activityService.log(106, "published", "unpublished", -1, survey.getUniqueId());
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
