package com.ec.survey.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
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
import com.ec.survey.model.Setting;
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
import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.TranslationService;

@Service("archiveExecutor")
@Scope("prototype")
public class ArchiveExecutor implements Runnable {
	
	@Resource(name="fileService")
	private FileService fileService;
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="settingsService")
	private SettingsService settingsService;
	
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
	
	public void createArchive(Survey survey, User user, Archive archive) throws Exception
	{
		Session session = sessionFactory.getCurrentSession();
		logger.info("starting archiving of survey " + survey.getShortname());
		
		java.io.File folder = fileService.getArchiveFolder(survey.getUniqueId());		
		java.io.File zip = surveyService.exportSurvey(survey.getShortname(), surveyService, true);				
		java.io.File target = new java.io.File(folder.getPath() + Constants.PATH_DELIMITER + survey.getUniqueId());
		
		if (folder.exists())
		{
			FileUtils.deleteDirectory(folder);
		}
		
		FileUtils.copyFile(zip, target);
		
		Survey published = surveyService.getSurvey(survey.getShortname(), false, false, false, true, survey.getLanguage().getCode(), true, false);
		
		if (published != null) 
		{
			logger.info("archiving PDF of survey " + survey.getShortname());			
			pdfService.createSurveyPDF(published, published.getLanguage().getCode(), new java.io.File(folder.getPath() + Constants.PATH_DELIMITER + published.getUniqueId() + ".pdf"));
			
			Form form = new Form();
			form.setSurvey(published);
			
			ResultFilter resultFilter = new ResultFilter();
			resultFilter.setSurveyId(published.getId());
			for (Element element : published.getElementsRecursive(false))
			{
				resultFilter.getVisibleQuestions().add(element.getId().toString());
				resultFilter.getExportedQuestions().add(element.getId().toString());
				
				if (element.isDelphiElement()) {
					resultFilter.getVisibleExplanations().add(element.getId().toString());
					resultFilter.getExportedExplanations().add(element.getId().toString());
					resultFilter.getVisibleDiscussions().add(element.getId().toString());
					resultFilter.getExportedDiscussions().add(element.getId().toString());
				}
			}
			
			form.setStatistics(answerService.getStatisticsOrStartCreator(survey, resultFilter, true, false, false));
			
			logger.info("archiving statistics (Excel) of survey " + survey.getShortname());	
			Export exportstats = new Export();
			exportstats.setForArchiving(true);	
			exportstats.setDate(new Date());
			exportstats.setState(ExportState.Pending);		
			exportstats.setUserId(user.getId());
			exportstats.setName("archiveStats");
			exportstats.setType(ExportType.Statistics);
			exportstats.setFormat(ExportFormat.xls);
			exportstats.setSurvey(published);
			exportstats.setResultFilter(resultFilter);
			exportService.startExport(form, exportstats, true, resources,new Locale("en"), null, folder.getPath() + Constants.PATH_DELIMITER + published.getUniqueId() + "statistics.xls", true);
			if (exportstats.getState() == ExportState.Failed)
			{
				throw new MessageException("export failed, abort archiving");
			}
			
			logger.info("archiving results (Excel) of survey " + survey.getShortname());	
			Export export = new Export();
			export.setDate(new Date());
			export.setState(ExportState.Pending);		
			export.setUserId(user.getId());
			export.setName("archiveXLS");
			export.setType(ExportType.Content);
			export.setFormat(ExportFormat.xls);
			export.setSurvey(published);
			export.setResultFilter(resultFilter);
			export.setForArchiving(true);			
			exportService.startExport(form, export, true, resources,new Locale("en"), null, folder.getPath() + Constants.PATH_DELIMITER + published.getUniqueId() + "results.xls", true);
			if (export.getState() == ExportState.Failed)
			{
				throw new MessageException("export failed, abort archiving");
			}
			
			logger.info("archiving statistics (PDF) of survey " + survey.getShortname());
			Export exportstatspdf = new Export();			
			exportstatspdf.setForArchiving(true);
			exportstatspdf.setDate(new Date());
			exportstatspdf.setState(ExportState.Pending);		
			exportstatspdf.setUserId(user.getId());
			exportstatspdf.setName("archiveStats");
			exportstatspdf.setType(ExportType.Statistics);
			exportstatspdf.setFormat(ExportFormat.pdf);
			exportstatspdf.setSurvey(published);
			exportstatspdf.setResultFilter(resultFilter);
			exportstatspdf = exportService.update(exportstatspdf); // needed as the pdf creation loads the export from the db	
			exportService.startExport(form, exportstatspdf, true, resources,new Locale("en"), null, folder.getPath() + Constants.PATH_DELIMITER + published.getUniqueId() + "statistics.pdf", true);
			if (exportstatspdf.getState() == ExportState.Failed)
			{
				throw new MessageException("export failed, abort archiving");
			}
			
			
		} else {
			logger.info("archiving PDF of survey " + survey.getShortname());
			
			pdfService.createSurveyPDF(survey, survey.getLanguage().getCode(), new java.io.File(folder.getPath() + Constants.PATH_DELIMITER + survey.getUniqueId() + ".pdf"));
		}
		
		session.flush();
		
		logger.info("deleting survey " + survey.getShortname());
		
		//make sure the archive exists before finally deleting the survey
		if (target.exists())
		{
			surveyService.markDeleted(survey.getId(), survey.getOwner().getId(), survey.getShortname(), survey.getUniqueId(), !survey.getIsDraft() || survey.getIsPublished());
		} else {
			throw new MessageException("archive file not found, abort archiving");
		}
		
		archive.setFinished(true);
		
		archive = (Archive) session.merge(archive);
		archiveService.update(archive);
		
		session.flush();
	}
	
	public void handleException(Exception e, Archive archive, Survey survey)
	{
		logger.error(e.getLocalizedMessage(), e);			
		archive.setError(e.getLocalizedMessage());
				
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
				
		archive = (Archive) session.merge(archive);
		session.update(archive);
		
		survey = (Survey) session.merge(survey);
		survey.setArchived(false);
		session.update(survey);
		
		t.commit();
		session.close();
	}
	
	@Transactional
	public void run()
	{		
		Survey lastSurvey = null;
		Archive lastArchive = null;
		try {		
			List<Survey> surveys = surveyService.getSurveysMarkedArchived(200);
			String limitSeconds = settingsService.get(Setting.NightlyTaskLimit);
			Date currentDate = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(currentDate);
			c.add(Calendar.SECOND, Integer.parseInt(limitSeconds));
			Date endDate = c.getTime();
			
			for (Survey survey : surveys) {
				currentDate = new Date();
				if (currentDate.after(endDate)) {
					break;
				}				
				
				lastSurvey = survey;
				try {
					lastArchive = archiveService.getActiveArchive(survey.getShortname());
					
					if (lastArchive == null || lastArchive.getFinished() || lastArchive.getError() != null) {
						continue;
					}
					
					createArchive(survey, survey.getOwner(), lastArchive);
				} catch (Exception e) {		
					handleException(e, lastArchive, lastSurvey);					
					logger.error("Error during archiving of Survey " + lastSurvey.getId() + " " + e.getLocalizedMessage());
					break;
				}	
			}			
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}		
		
		logger.info("start flagging old surveys as archived");
		
		try {
			List<Survey> surveys = surveyService.getSurveysToBeMarkedArchived();
			logger.info("found " + surveys.size() + "surveys to be archived");
			
			for (Survey survey : surveys) {
				lastSurvey = survey;
				
				try {
					archiveService.archiveSurvey(survey, survey.getOwner());
				} catch (Exception e) {		
					logger.error("Error during archiving of Survey " + lastSurvey.getId() + " " + e.getLocalizedMessage());					
				}				
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}	
	
		logger.info("archiving of surveys finished");
	}
	
}
