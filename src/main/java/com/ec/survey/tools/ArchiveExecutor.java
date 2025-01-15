package com.ec.survey.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.ec.survey.model.Archive;
import com.ec.survey.model.Setting;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.ArchiveService;
import com.ec.survey.service.SessionService;
import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SurveyService;

@Service("archiveExecutor")
@Scope("prototype")
public class ArchiveExecutor implements Runnable {
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="settingsService")
	private SettingsService settingsService;
	
	@Resource(name="archiveService")
	private ArchiveService archiveService;
	
	@Resource(name="sessionService")
	private SessionService sessionService;
		
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;	

	private static final Logger logger = Logger.getLogger(ArchiveExecutor.class);
	
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
	
	public void run()
	{		
		Survey lastSurvey = null;
		Archive lastArchive = null;
		
		String limitSeconds = settingsService.get(Setting.NightlyTaskLimit);
		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.SECOND, Integer.parseInt(limitSeconds));
		Date endDate = c.getTime();
		
		try {		
			List<Survey> surveys = surveyService.getSurveysMarkedArchived(200);			
			
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
					
					archiveService.createArchive(survey, survey.getOwner(), lastArchive);
				} catch (Exception e) {		
					handleException(e, lastArchive, lastSurvey);					
					logger.error("Error during archiving of Survey " + lastSurvey.getId() + " " + e.getLocalizedMessage());
					break;
				}	
			}			
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}			
		
		logger.info("archiving of surveys finished");
	}
	
}
