package com.ec.survey.tools.export;

import com.ec.survey.model.Export;
import com.ec.survey.model.ExportCache;
import com.ec.survey.model.Export.ExportState;
import com.ec.survey.model.Form;
import com.ec.survey.service.*;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.FileUtils;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public abstract class ExportCreator implements Runnable {

	protected static final Logger logger = Logger.getLogger(ExportCreator.class);

	@Resource(name = "attendeeService")
	protected AttendeeService attendeeService;	
	
	@Resource(name = "participationService")
	protected ParticipationService participationService;
	
	@Resource(name = "administrationService")
	protected AdministrationService administrationService;	
	
	@Resource(name = "exportService")
	protected ExportService exportService;	
	
	@Resource(name = "answerService")
	protected AnswerService answerService;	
	
	@Resource(name = "surveyService")
	protected SurveyService surveyService;	
	
	@Resource(name = "pdfService")
	protected PDFService pdfService;	
	
	@Resource(name = "sessionFactory")
	protected SessionFactory sessionFactory;
	
	@Resource(name = "activityService")
	protected ActivityService activityService;
	
	@Resource(name = "translationService")
	protected TranslationService translationService;
	
	@Resource(name="mailService")
	protected MailService mailService;
	
	@Resource(name = "fileService")
	protected FileService fileService;
	
	@Resource(name = "reportingServiceProxy")
	protected ReportingServiceProxy reportingService;
	
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${sender}") String sender;
	
	protected @Autowired ServletContext servletContext;
		
	protected int userId;
	protected Form form;
	protected Export export;
	protected OutputStream outputStream;	
	protected MessageSource resources;
	protected Locale locale;
	protected String exportFilePath;
	protected String uid;
	protected String host;
	
	public void init(int userId, Form form, Export export, String exportFilePath, MessageSource resources, Locale locale, String uid, String host) throws FileNotFoundException {
		this.userId = userId;
		this.form = form;
		this.export = export;
		this.exportFilePath = exportFilePath;
		this.outputStream = new FileOutputStream(exportFilePath);
		this.resources = resources;
		this.locale = locale;
		this.uid = uid;
		this.host = host;
		init();
	}
	
	public void init(int userId, Form form, Export export, OutputStream outputStream, MessageSource resources, Locale locale) {
		this.userId = userId;
		this.form = form;
		this.export = export;
		this.exportFilePath = "";
		this.outputStream = outputStream;
		this.resources = resources;
		this.locale = locale;
		init();
	}
	
	void init()
	{}

	abstract void ExportContent(boolean sync) throws Exception;
	abstract void ExportStatistics() throws Exception;
	abstract void ExportStatisticsQuiz() throws Exception;	
	abstract void ExportAddressBook() throws Exception;
	abstract void ExportActivities() throws Exception;
	abstract void ExportTokens() throws Exception;
	
	private void initAnswers() throws Exception
	{
		form.setStatistics(answerService.getStatistics(export.getSurvey(), export.getResultFilter(), true, export.isAllAnswers(), false));
	}
	
	@Transactional
	public void run() {
		runBasic(true);
	}

	@Transactional
	public void runSync() {
		runBasic(true);
	}
	
	private void runBasic(boolean sync)
	{
		
		if (export == null) return;
		
		try {
		
			innerRunBasic(sync, export);
			
			export.setValid(true);
			export.setState(ExportState.Finished);
			if (export.getSurvey() != null)
			{
				activityService.log(310, null, export.getId().toString(), -1, export.getSurvey().getUniqueId());
			}
									
			if (export.getEmail() != null)
			{
				//cache it
				ExportCache ec = new ExportCache();
				ec.setSurveyId(form.getSurvey().getId());
				ec.setFilterHash(export.getResultFilter().getHash(export.isAllAnswers()));
				String cacheType = "statexport" + export.getType() + export.getFormat();
				ec.setType(cacheType);
				ec.setUid(uid);
				String name =  FileUtils.cleanFilename(String.format("%s_Published_Results_%s.%s", export.getSurveyShortname(), export.getType(), export.getFormat()));
				
				com.ec.survey.model.survey.base.File f = new com.ec.survey.model.survey.base.File();
				f.setUid(ec.getUid());
				f.setName(name);
				fileService.addNewTransaction(f);			
				fileService.saveNewTransaction(ec);
				
				String link = host + "files/" + uid + Constants.PATH_DELIMITER;		
				
				if (export.getSurvey() != null)
				{
					link = host + "files/" + export.getSurvey().getUniqueId() + Constants.PATH_DELIMITER + uid + Constants.PATH_DELIMITER;		
				}
								
				String body = "Dear EUSurvey user,<br /><br />The export you requested from the published results of the survey '<b>" + export.getSurvey().cleanTitle() + "</b>' is now finished. You can download it here:<br /><br /> <a href=\"" + link + "\">" + name + "</a><br /><br />Your EUSurvey team";
				String subject = "Copy of your requested export from '" + export.getSurvey().cleanTitleForMailSubject() + "'";
				InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
				String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]",host);
				
				mailService.SendHtmlMail(export.getEmail(), sender, sender, subject, text, null);		
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			
			if (e instanceof GenericJDBCException)
			{
				GenericJDBCException ge = (GenericJDBCException)e;
				logger.error("caused by " + ge.getSQL());
			}
			
			export.setState(ExportState.Failed);
		}
		finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {					
					logger.error(e.getLocalizedMessage(), e);
				}
			}
		}
		
		exportService.update(export);
	}
	
	private void innerRunBasic(boolean sync, Export export) throws Exception
	{
		switch (export.getType()) {
			case Content: ExportContent(sync); break;
			case Statistics: initAnswers(); ExportStatistics(); break;
			case StatisticsQuiz: initAnswers(); ExportStatisticsQuiz(); break;
			case AddressBook: ExportAddressBook(); break;
			case Activity: ExportActivities(); break;
			case Tokens: ExportTokens(); break;
			case Files: ExportContent(sync); break;
			case Survey: ExportContent(sync); break;
		default:
			break;
		}
	}
	
}
