package com.ec.survey.tools;

import com.ec.survey.service.SurveyService;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("sendReportedSurveysWorker")
@Scope("singleton")
public class SendReportedSurveysWorker implements Runnable {

	protected static final Logger logger = Logger.getLogger(SendReportedSurveysWorker.class);
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
		
	@Override
	public void run() {
		try {
			logger.info("SendReportedSurveysWorker started");
			
			surveyService.sendAbuseReportsMailForYesterday();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}		
		logger.info("SendReportedSurveysWorker completed");
	}
	
}
