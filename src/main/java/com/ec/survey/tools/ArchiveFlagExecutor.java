package com.ec.survey.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.ec.survey.model.Setting;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.ArchiveService;
import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SurveyService;

@Service("archiveFlagExecutor")
@Scope("prototype")
public class ArchiveFlagExecutor implements Runnable {
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="settingsService")
	private SettingsService settingsService;
	
	@Resource(name="archiveService")
	private ArchiveService archiveService;		
		
	@Autowired
	protected MessageSource resources;	

	private static final Logger logger = Logger.getLogger(ArchiveFlagExecutor.class);
		
	public void run()
	{		
		String limitSeconds = settingsService.get(Setting.NightlyTaskLimitArchiving);
		
		if (limitSeconds.equals("0")) {
			return;
		}
		
		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.SECOND, Integer.parseInt(limitSeconds));
		Date endDate = c.getTime();
		
		logger.info("start flagging old surveys as archived");
		
		try {
			List<Survey> surveys = surveyService.getSurveysToBeMarkedArchived();
			logger.info("found " + surveys.size() + " surveys to be archived");
			
			for (Survey survey : surveys) {
				currentDate = new Date();
				if (currentDate.after(endDate)) {
					break;
				}
				
				try {
					archiveService.archiveSurvey(survey, survey.getOwner());
				} catch (Exception e) {		
					logger.error("Error during flagging of Survey " + survey.getId() + " " + e.getLocalizedMessage(), e);					
				}				
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}	
	
		logger.info("flagging of surveys finished");
	}
	
}
