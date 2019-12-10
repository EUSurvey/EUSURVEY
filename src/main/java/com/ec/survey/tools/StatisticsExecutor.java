package com.ec.survey.tools;

import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ec.survey.model.Export;
import com.ec.survey.model.ExportCache;
import com.ec.survey.model.Form;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.Export.ExportFormat;
import com.ec.survey.model.Export.ExportState;
import com.ec.survey.model.Export.ExportType;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.ExportService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.SurveyService;

@Service("statisticsExecutor")
@Scope("prototype")
public class StatisticsExecutor implements Runnable {
	
	@Resource(name="fileService")
	private FileService fileService;
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="exportService")
	private ExportService exportService;
	
	@Resource(name="mailService")
	private MailService mailService;
	
	@Autowired
	protected MessageSource resources;
	
	public @Autowired ServletContext servletContext;
		
	private Survey survey;
	private String type;
	private String format;
	private String email;
	private String from;
	private String server;
	private String smtpPort;
	private String host;
	private String hash;
	private Locale locale;
	
	private static final Logger logger = Logger.getLogger(StatisticsExecutor.class);
	
	public void init(Survey survey, String type, String format, String hash, String email, String from, String server, String smtpPort, String host, Locale locale)
	{
		this.survey = survey;
		this.type = type;
		this.format = format;
		this.email = email;
		this.from = from;
		this.server = server;
		this.smtpPort = smtpPort;
		this.host = host;
		this.locale = locale;
		this.hash = hash;
	}
	
	public void run()
	{
		try {
			
			String cacheType = "statexport" + type + format;
			survey = surveyService.getSurvey(survey.getShortname(), false, false, false, true, null, true, false);
			ExportCache c = fileService.getCachedExport(survey.getId(), hash, cacheType);
			if (c != null)
			{
				String name =  FileUtils.cleanFilename(String.format("%s_Export_%s.%s", type, "Published Results Export",format));	
				String link = host + "files/" + survey.getUniqueId() + "/" + c.getUid() + "/";					
				String body = "Dear EUSurvey user,<br /><br />The export you requested from the published results of the survey '<b>" + survey.cleanTitle() + "</b>' is now finished. You can download it here:<br /><br /> <a href=\"" + link + "\">" + name + "</a><br /><br />Your EUSurvey team";
				String subject = "Copy of your requested export from '" + survey.cleanTitleForMailSubject() + "'";
				InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
				String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]",host);
				
				mailService.SendHtmlMail(email, from, from, subject, text, server, Integer.parseInt(smtpPort.trim()), null);				
			} else {	
							
				ResultFilter filter = survey.getPublication().getFilter().copy();
				filter.setExportedQuestions(filter.getVisibleQuestions());
				
				String uid = UUID.randomUUID().toString();
				
				final Form form = new Form(resources);
				survey = surveyService.getSurvey(survey.getShortname(), false, false, false, false, null, true, false);

				Export export = new Export();
				export.setDate(new Date());
				export.setState(ExportState.Pending);
				export.setSurvey(survey);
				export.setUserId(-1);
				export.setName("Published Results Export");				
				export.setType(ExportType.valueOf(type));
				export.setFormat(ExportFormat.valueOf(format));				
				export.setResultFilter(filter.copy());
				export.setEmail(email);
				form.setSurvey(survey);

				exportService.prepareExport(form, export);
				exportService.startExport(form, export, true, resources, locale, uid, null, false);
				
			}				
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
