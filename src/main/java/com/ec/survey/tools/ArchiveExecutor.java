package com.ec.survey.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.Archive;
import com.ec.survey.model.Export;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.Export.ExportFormat;
import com.ec.survey.model.Export.ExportState;
import com.ec.survey.model.Export.ExportType;
import com.ec.survey.model.Form;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.ArchiveService;
import com.ec.survey.service.ExportService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.PDFService;
import com.ec.survey.service.SessionService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.TranslationService;

@Service("archiveExecutor")
@Scope("prototype")
public class ArchiveExecutor implements Runnable {
	
	@Resource(name="fileService")
	private FileService fileService;
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="exportService")
	private ExportService exportService;
	
	@Resource(name="answerService")
	private AnswerService answerService;
	
	@Resource(name="archiveService")
	private ArchiveService archiveService;
	
	@Resource(name="sessionService")
	private SessionService sessionService;
	
	@Resource(name="mailService")
	private MailService mailService;
	
	@Resource(name="pdfService")
	private PDFService pdfService;
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	
	@Resource(name="translationService")
	private TranslationService translationService;
	
	@Autowired
	protected MessageSource resources;	

	private static final Logger logger = Logger.getLogger(ArchiveExecutor.class);
	
	private Archive archive;
	private Survey survey;
	private User user;
	private Export export = new Export();
	private Export exportstats = new Export();
	private Export exportstatspdf = new Export();
	private Survey published = null;
	private Form form = null;	
	private java.io.File target = null;	
	
	public void init(Archive archive, Survey survey, User user)
	{
		this.archive = archive;
		this.survey = survey;
		this.user = user;
	}
	
	@Transactional
	public boolean prepare()
	{
		if (survey == null)
		{
			logger.error("survey is null");
			return false;
		}
		
		if (survey.getLanguage() == null)
		{
			logger.error("survey.language is null");
			return false;
		}
		
		published = surveyService.getSurvey(survey.getShortname(), false, false, false, true, survey.getLanguage().getCode(), true, false);
		
		if (published != null) 
		{
			form = new Form();
			form.setSurvey(published);
			
			ResultFilter resultFilter = new ResultFilter();
			resultFilter.setSurveyId(published.getId());
			for (Element element : published.getElementsRecursive(false))
			{
				if (!resultFilter.getVisibleQuestions().contains(element.getId().toString()))
				{
					resultFilter.getVisibleQuestions().add(element.getId().toString());
				}
								
				if (!resultFilter.getExportedQuestions().contains(element.getId().toString()))
				{
					resultFilter.getExportedQuestions().add(element.getId().toString());
				}
			}
			
			export.setDate(new Date());
			export.setState(ExportState.Pending);		
			export.setUserId(user.getId());
			export.setName("archiveXLS");
			export.setType(ExportType.Content);
			export.setFormat(ExportFormat.xls);
			export.setSurvey(published);
			export.setResultFilter(resultFilter);
			exportService.prepareExport(null, export);
						
			exportstats.setDate(new Date());
			exportstats.setState(ExportState.Pending);		
			exportstats.setUserId(user.getId());
			exportstats.setName("archiveStats");
			exportstats.setType(ExportType.Statistics);
			exportstats.setFormat(ExportFormat.xls);
			exportstats.setSurvey(published);
			exportstats.setResultFilter(resultFilter);
			exportService.prepareExport(null, exportstats);
			
			exportstatspdf.setDate(new Date());
			exportstatspdf.setState(ExportState.Pending);		
			exportstatspdf.setUserId(user.getId());
			exportstatspdf.setName("archiveStats");
			exportstatspdf.setType(ExportType.Statistics);
			exportstatspdf.setFormat(ExportFormat.pdf);
			exportstatspdf.setSurvey(published);
			exportstatspdf.setResultFilter(resultFilter);
			exportService.prepareExport(null, exportstatspdf);
		
		}
		
		surveyService.markAsArchived(survey.getUniqueId());
		
		return true;
	}

	public void createArchive() throws Exception
	{
		logger.info("starting archiving of survey " + survey.getShortname());
		
		java.io.File folder = fileService.getArchiveFolder(survey.getUniqueId());		
		
		java.io.File zip = surveyService.exportSurvey(survey.getShortname(), surveyService, true);
				
		target = new java.io.File(folder.getPath() + "/" + survey.getUniqueId());
		
		if (target.exists())
		{
			throw new MessageException("Survey cannot be archived as archive file already exists: " + survey.getShortname());
		}
		
		FileUtils.copyFile(zip, target);
		
		if (published != null) 
		{
			logger.info("archiving PDF of survey " + survey.getShortname());
			
			pdfService.createSurveyPDF(published, published.getLanguage().getCode(), new java.io.File(folder.getPath() + "/" + published.getUniqueId() + ".pdf"));
			
			logger.info("archiving results (Excel) of survey " + survey.getShortname());
			
			exportService.startExport(form, export, true, resources,new Locale("en"), null, folder.getPath() + "/" + published.getUniqueId() + "results.xls", true);
			if (export.getState() == ExportState.Failed)
			{
				throw new MessageException("export failed, abort archiving");
			}
			
			logger.info("archiving statistics (Excel) of survey " + survey.getShortname());
			
			exportService.startExport(form, exportstats, true, resources,new Locale("en"), null, folder.getPath() + "/" + published.getUniqueId() + "statistics.xls", true);
			if (exportstats.getState() == ExportState.Failed)
			{
				throw new MessageException("export failed, abort archiving");
			}
			
			logger.info("archiving statistics (PDF) of survey " + survey.getShortname());
		
			exportService.startExport(form, exportstatspdf, true, resources,new Locale("en"), null, folder.getPath() + "/" + published.getUniqueId() + "statistics.pdf", true);
			if (exportstatspdf.getState() == ExportState.Failed)
			{
				throw new MessageException("export failed, abort archiving");
			}
		} else {
			logger.info("archiving PDF of survey " + survey.getShortname());
			
			pdfService.createSurveyPDF(survey, survey.getLanguage().getCode(), new java.io.File(folder.getPath() + "/" + survey.getUniqueId() + ".pdf"));
		}
		
		logger.info("deleting survey " + survey.getShortname());
		
		//make sure the archive exists before finally deleting the survey
		if (target.exists())
		{
			surveyService.deleteNoTransaction(survey.getId(), false, true);
		} else {
			throw new MessageException("archive file not found, abort archiving");
		}
		
		archive.setFinished(true);
		
		archiveService.update(archive);
	}
	
	public void handleException(Exception e)
	{
		logger.error(e.getLocalizedMessage(), e);			
		archive.setError(e.getLocalizedMessage());
		
		//delete files
		if (target != null)
		{
			try {
				Files.deleteIfExists(target.toPath());
			} catch (IOException e1) {
				//ignore
			}
		}
		
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		
		Query query = session.createQuery("UPDATE Survey s SET s.archived = 0 WHERE s.uniqueId = :uid").setString("uid", survey.getUniqueId());
		query.executeUpdate();	
		
		archive = (Archive) session.merge(archive);
		session.update(archive);
		
		t.commit();
		session.close();
	}
	
	@Transactional
	public void run()
	{		
		try {
			createArchive();
		} catch (Exception e) {
			handleException(e);
		}
	
		logger.info("archiving of survey " + survey.getShortname() + " finished");
	}
	
}
