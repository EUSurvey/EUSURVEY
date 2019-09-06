package com.ec.survey.tools;

import com.ec.survey.service.AnswerService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.SurveyService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.Date;

@Service("cleanupWorker")
@Scope("prototype")
public class CleanupWorker implements Runnable {

	protected static final Logger logger = Logger.getLogger(CleanupWorker.class);

	@Resource(name = "surveyService")
	protected SurveyService surveyService;	
	
	@Resource(name = "answerService")
	protected AnswerService answerService;
	
	@Resource(name = "fileService")
	protected FileService fileService;
	
	@Resource(name = "mailService")
	protected MailService mailService;	
	
	@Resource(name = "sessionFactory")
	protected SessionFactory sessionFactory;
	
	public @Autowired ServletContext servletContext;
	
	private String[] options;
	private Date pdfbefore;
	private Date tempbefore;
	private String email;
	
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	public @Value("${sender}") String sender;
	public @Value("${server.prefix}") String host;
	private @Value("${contextpath}") String contextpath;
	
	public void init(String[] options, Date pdfbefore, Date tempbefore, String email)
	{
		this.options = options;
		this.pdfbefore = pdfbefore;
		this.tempbefore = tempbefore;
		this.email = email;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void run() {
		runBasic(true);
	}

	@Transactional
	public void runSync() {
		runBasic(true);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	private void runBasic(boolean sync)
	{
		try {
			int counter = 0;
			
			for (String option : options)
			{
				switch (option) {
					case "archived":
						counter += fileService.deleteFilesForArchivedSurveys();
						break;
					case "deleted":
						counter += fileService.deleteFilesForDeletedElements();
						break;
					case "pdfbefore":
						counter += fileService.deleteContributions(pdfbefore);
						break;
					case "tempbefore":
						counter += fileService.deleteTemporaryFiles(tempbefore);
						break;
				}
			}
			
			String body = "Your cleanup process has finished. " + counter + " files have been deleted.";
			
			InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
			String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]",host);
					
			mailService.SendHtmlMail(email, sender, sender, "EUSurvey file cleanup finished", text, smtpServer, Integer.parseInt(smtpPort), null);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

}
