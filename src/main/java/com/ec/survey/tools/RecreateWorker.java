package com.ec.survey.tools;

import com.ec.survey.model.FileFilter;
import com.ec.survey.model.FileResult;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.SurveyService;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

@Service("recreateWorker")
@Scope("prototype")
public class RecreateWorker implements Runnable {

	protected static final Logger logger = Logger.getLogger(RecreateWorker.class);
	
	@Autowired
	protected MessageSource resources;	

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
	
	private @Autowired ServletContext servletContext;
	
	private String[] files;
	private FileFilter filter;
	private String email;
	
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${sender}") String sender;
	private @Value("${server.prefix}") String host;
	private @Value("${export.tempFileDir}") String tempFileDir;
	private @Value("${export.fileDir}") String fileDir;
	private @Value("${archive.fileDir}") String archiveFileDir;
	
	public void init(String[] files, FileFilter filter, String email)
	{
		this.files = files;
		this.filter = filter;
		this.email = email;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void run() {
		runBasic();
	}

	@Transactional
	public void runSync() {
		runBasic();
	}
	
	private void runBasic()
	{
		try {
			int counter = 0;
			
			Locale locale = new Locale("en");
			
			if (files == null)
			{
				List<FileResult> fileresults = fileService.getFiles(filter);
				for (FileResult fileresult : fileresults)
				{
					java.io.File file = new java.io.File(fileresult.getFilePath());
					if (file.exists() && fileService.recreate(file, locale, resources))
					{
						counter++;
					}		
				}
			} else {			
				for (String path : files)
				{
					java.io.File file = new java.io.File(path);
					if (file.exists() && fileService.recreate(file, locale, resources))
					{
						counter++;
					}
				}
			}
			
			String body = "Your recreation process has finished. " + counter + " files have been recreated.";
			
			InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
			String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]",host);
					
			mailService.SendHtmlMail(email, sender, sender, "EUSurvey file recreation finished", text, null);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

}
