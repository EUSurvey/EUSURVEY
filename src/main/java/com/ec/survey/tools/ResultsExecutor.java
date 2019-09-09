package com.ec.survey.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ec.survey.model.ExportCache;
import com.ec.survey.model.Form;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.tools.export.OdfExportCreator;
import com.ec.survey.tools.export.XlsExportCreator;

@Service("resultsExecutor")
@Scope("prototype")
public class ResultsExecutor implements Runnable, BeanFactoryAware{

	private Survey survey;
	private ResultFilter filter;
	private String email;
	private String from;
	private String server;
	private String smtpPort;
	private String host;
	
	@Resource(name="mailService")
	private MailService mailService;
	
	protected @Value("${export.deleteexportstimeout}") String deleteexportstimeout;
	
	public @Autowired ServletContext servletContext;
	
	private BeanFactory context;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		context = beanFactory;		
	}
	
	@Resource(name = "fileService")
	private FileService fileService;	
	
	@Resource(name = "surveyService")
	private SurveyService surveyService;	
	
	@Resource(name = "answerService")
	private AnswerService answerService;	
	
	private String fileDir;
	private String type;

	private MessageSource resources;
	private Locale locale;
	
	private String question;
	
	private static final Logger logger = Logger.getLogger(ResultsExecutor.class);
	
	public void init(Survey survey, ResultFilter filter, String email, String from, String server, String smtpPort, String host, String fileDir, String type, MessageSource resources, Locale locale, String question)
	{
		this.survey = survey;
		this.filter = filter;
		this.email = email;
		this.from = from;
		this.server = server;
		this.smtpPort = smtpPort;
		this.host = host;
		this.fileDir = fileDir;
		this.type = type;
		this.resources = resources;
		this.locale = locale;
		this.question = question;
	}
	
	@Transactional
	public void run()
	{
		try {
			
			String uid = UUID.randomUUID().toString();	
			
			Survey dbsurvey = surveyService.getSurvey(survey.getId(), false, true);
			filter = dbsurvey.getPublication().getFilter();
			String filename = "results";
			
			Calendar cal = Calendar.getInstance();
			int days = Integer.parseInt(deleteexportstimeout);
			cal.add(Calendar.DATE, days);
			
			if (type.equalsIgnoreCase("files"))
			{
				File archive = new File();
		    	archive.setUid(uid);
		    	archive.setName("files.zip");
		    	archive.setDeletionDate(cal.getTime());
		    	archive.setComment(survey.getUniqueId());
		    	fileService.add(archive);
		    	
				List<String[]> files; 
				if (question.endsWith("true"))
				{
					files = answerService.getFilesForQuestion(question.replace("true", ""), true);
				} else if (question.endsWith("false"))
				{
					files = answerService.getFilesForQuestion(question.replace("false", ""), false);
				} else {
					throw new Exception("invalid question id");
				}
				
				java.io.File temp = fileService.getSurveyExportFile(survey.getUniqueId(), uid);	
			
				final OutputStream out = new FileOutputStream(temp);
				final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);
					
				for (Object[] item : files)
				{				
					File f = fileService.get(item[1].toString());
					
					java.io.File source = fileService.getSurveyFile(survey.getUniqueId(), f.getUid());
					
					if (!source.exists())
					{					
						source = new java.io.File(fileDir + f.getUid());
						if (source.exists())
						{
							fileService.LogOldFileSystemUse(fileDir + f.getUid());
						}
					}
					
			    	if (source.exists())
			    	{
				    	os.putArchiveEntry(new ZipArchiveEntry(item[0].toString() + "_" + item[1].toString() + "_" + item[2].toString()));
				    	IOUtils.copy(new FileInputStream(source), os);
					    os.closeArchiveEntry();
			    	}				
				}
								
				os.close();
				out.close();
				
				filename = "files.zip";
			} else {
				if (type.equalsIgnoreCase("xls"))
				{
					filename = survey.getShortname() + "_Published_Results.xls";
				} else if (type.equalsIgnoreCase("ods"))
				{
					filename = survey.getShortname() + "_Published_Results.ods";
				}				
				
				//try to get cached export
				ExportCache c = fileService.getCachedExport(survey.getId(), filter.getHash(false), type);
				boolean cachedExportExists = false;
				
				if (c != null)
				{
					uid = c.getUid();
					java.io.File export = fileService.getSurveyExportFile(survey.getUniqueId(), uid, false);
					if (export.exists())
					{
						cachedExportExists = true;
					}					
				}
				
				if (!cachedExportExists) {			
											
					File f = new File();
					f.setDeletionDate(cal.getTime());
					f.setComment(survey.getUniqueId());
			    	f.setUid(uid);
					
			    	Form form = new Form(resources);
			    	
			    	//List<AnswerSet> answerSets = answerService.getAllAnswers(survey.getId(), filter);
			    	
					//form.setAnswerSets(answerSets);	    		    	
			    	form.setSurvey(survey);
			    	form.setPublicationMode(true);
			    	
					if (type.equalsIgnoreCase("xls"))
					{
						XlsExportCreator xlsExportCreator = (XlsExportCreator) context.getBean("xlsExportCreator");
						xlsExportCreator.init(0,form,null, fileService.getSurveyExportFile(survey.getUniqueId(), uid).getPath(), resources, locale, "", "");			
						xlsExportCreator.ExportContent(dbsurvey.getPublication(), false);
						f.setName(filename);
					} else if (type.equalsIgnoreCase("ods"))
					{
						OdfExportCreator odfExportCreator = (OdfExportCreator) context.getBean("odfExportCreator");
						odfExportCreator.init(0,form,null,fileService.getSurveyExportFile(survey.getUniqueId(), uid).getPath(), resources, locale, "", "");	
						odfExportCreator.ExportContent(dbsurvey.getPublication(), false);
						f.setName(filename);
					}
					
					fileService.addNewTransaction(f);
					
					java.io.File target = fileService.getSurveyExportFile(survey.getUniqueId(), uid);
					java.io.File zipped = new java.io.File(target.getPath() + ".zip");
					if (zipped.exists())
					{
						filename += ".zip";
					}
					
					//cache it
					ExportCache ec = new ExportCache();
					ec.setSurveyId(survey.getId());
					ec.setFilterHash(filter.getHash(false));
					ec.setType(type);
					ec.setUid(uid);
					fileService.save(ec);
				}
			}
			
			String link = host + "files/" + survey.getUniqueId() + "/" + uid + "/";			
			String body = "Dear EUSurvey user,<br /><br />The export you requested from the published results of the survey '<b>" + survey.cleanTitle() + "</b>' is now finished. You can download it here:<br /><br /> <a href=\"" + link + "\">" + filename + "</a><br /><br />Your EUSurvey team";
			
			String subject = "Copy of your requested export from '" + survey.cleanTitleForMailSubject() + "'";
			
			InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
			String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]",host);
						
			mailService.SendHtmlMail(email, from, from, subject, text, server, Integer.parseInt(smtpPort), null);
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
