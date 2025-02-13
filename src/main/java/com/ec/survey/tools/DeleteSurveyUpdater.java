package com.ec.survey.tools;

import com.ec.survey.model.Archive;
import com.ec.survey.model.Setting;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.SystemService;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

@Service("deleteSurveysWorker")
@Scope("singleton")
public class DeleteSurveyUpdater implements Runnable {

	protected static final Logger logger = Logger.getLogger(DeleteSurveyUpdater.class);
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="systemService")
	private SystemService systemService;
	
	@Resource(name="settingsService")
	private SettingsService settingsService;
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;	
	
	@Override
	public void run() {
		int lastId = 0;
		
		logger.info("deleting of surveys started");
		
		String limitSeconds = settingsService.get(Setting.NightlyTaskLimit);
		
		if (limitSeconds.equals("0")) {
			return;
		}
		
		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.SECOND, Integer.parseInt(limitSeconds));
		Date endDate = c.getTime();
		
		try {		
			List<Integer> surveys = surveyService.getSurveysMarkedDeleted();
			
			for (Integer id : surveys) {
				lastId = id;
				try {
					logger.info("deleting survey " + id);
					surveyService.delete(id, true, true);
					currentDate = new Date();
					if (currentDate.after(endDate)) {
						break;
					}
				} catch (Exception e) {
					handleException(e, lastId);	
				}	
			}			
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		logger.info("deleting of surveys finished");
	}
	
	public void handleException(Exception e, int surveyId) throws ParseException
	{
		logger.error("Error during deletion of Survey " + surveyId + ": " + e.getLocalizedMessage(), e);		
				
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
				
		Survey survey = session.get(Survey.class, surveyId);
		survey.setDeleted(new SimpleDateFormat("yyyy-MM-dd").parse("2099-01-01"));
		session.update(survey);
		
		t.commit();
		session.close();
	}
	
}
