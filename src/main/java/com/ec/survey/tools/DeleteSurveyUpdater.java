package com.ec.survey.tools;

import com.ec.survey.model.Setting;
import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.SystemService;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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
	
	@Override
	public void run() {
		int lastId = 0;
		
		String limitSeconds = settingsService.get(Setting.NightlyTaskLimit);
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
					surveyService.delete(id, true, true);
					currentDate = new Date();
					if (currentDate.after(endDate)) {
						break;
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
					systemService.sendAdminErrorMessage("Error during deletion of Survey " + lastId + " " + e.getLocalizedMessage());
				}	
			}			
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
