package com.ec.survey.tools;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.ec.survey.model.administration.User;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.ArchiveService;
import com.ec.survey.service.AttendeeService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.SurveyService;

@Service("testDataGenerator")
public class TestDataGenerator implements Runnable {

	protected static final Logger logger = Logger.getLogger(TestDataGenerator.class);

	@Resource(name = "surveyService")
	protected SurveyService surveyService;	
	
	@Resource(name = "archiveService")
	protected ArchiveService archiveService;	

	@Resource(name = "answerService")
	protected AnswerService answerService;	
	
	@Resource(name="mailService")
	private MailService mailService;
	
	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;
	
	@Resource(name = "fileService")
	private FileService fileService;
	
	@Resource(name = "attendeeService")
	private AttendeeService attendeeService;
	
	@Autowired
	protected MessageSource resources;	
	
	private String fileDir;
	private String sender;
	private String smtpServer;
	private String smtpPort;
	
	private User user;
	private int answers;
	private int files;
	private Integer questions;
	private String email;
	private String shortname;
	private String filetype;
	private BeanFactory context;
	private int surveys;
	private int contacts;
	
	private boolean archive = false;
	
	public void init(User user, int answers, String fileDir, String sender, String smtpServer, String smtpPort, String email, String shortname, Integer questions, boolean archive, BeanFactory context, int files, String filetype, int surveys, int contacts)
	{
		this.user = user;
		this.answers = answers;
		this.questions = questions;
		this.fileDir = fileDir;
		this.sender = sender;
		this.smtpServer = smtpServer;
		this.smtpPort = smtpPort;
		this.email = email;
		this.shortname = shortname;
		this.archive = archive;
		this.context = context;
		this.files = files;
		this.filetype = filetype;
		this.surveys = surveys;
		this.contacts = contacts;
	}
	
	@Override
	public void run() {
		try {
			logger.debug("testDataGenerator started");
			
			if (contacts > 0)
			{
				attendeeService.createDummyAttendees(contacts, user.getId());
				if (email != null) mailService.SendHtmlMail(email, sender, sender, "Test data generated", contacts + " contacts have been generated", smtpServer, Integer.parseInt(smtpPort), null);
			} else if (files > 0)
			{
				fileService.createDummyFiles(files, filetype);
				if (email != null) mailService.SendHtmlMail(email, sender, sender, "Test data generated", files + " files have been generated", smtpServer, Integer.parseInt(smtpPort), null);
			} else {
				if (shortname != null && shortname.length() > 0)
				{
					ApplicationListenerBean.createDummyAnswers(shortname, answers, user, fileDir, answerService, surveyService, true, resources, Locale.ENGLISH, fileService);
				} else {
					for (int i = 0; i < surveys; i++)
					{
						if (surveys > 1 && i > 0)
						{
							answers = (int) Math.floor(150000/i - 1);
						}
						ApplicationListenerBean.createSurvey(answers, user, surveyService.getLanguage("EN"), surveyService, answerService, fileDir, false, resources, Locale.ENGLISH, questions, archive, archiveService, context, taskExecutor, fileService);
					}
				}
				
				if (email != null) mailService.SendHtmlMail(email, sender, sender, "Test data generated", "The test survey with " + answers + " answers has been generated", smtpServer, Integer.parseInt(smtpPort), null);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}		
		logger.debug("testDataGenerator completed");
	}
	
}
