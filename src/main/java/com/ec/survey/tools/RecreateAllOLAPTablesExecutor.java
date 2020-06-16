package com.ec.survey.tools;

import java.util.List;

import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.ec.survey.service.ReportingService;
import com.ec.survey.service.ReportingServiceProxy;
import com.ec.survey.service.SurveyService;

@Service("recreateAllOLAPTablesExecutor")
@Scope("prototype")
public class RecreateAllOLAPTablesExecutor implements Runnable {

	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="reportingServiceProxy")
	private ReportingServiceProxy reportingService;	

	private static final Logger logger = Logger.getLogger(RecreateAllOLAPTablesExecutor.class);
	
	public void run()
	{
		try {
			logger.info("RecreateAllOLAPTablesExecutor started");
			
			List<String> surveyUIDs = surveyService.getAllSurveyUIDs(false);
			
			reportingService.removeAllToDos();
				
			for (String uid : surveyUIDs)
			{
				reportingService.deleteOLAPTable(uid, true, true);
			}
			
			for (String uid : surveyUIDs)
			{
				try {
					reportingService.createOLAPTable(uid, true, false);
					reportingService.createOLAPTable(uid, false, true);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					logger.error("last query: " + ReportingService.lastQuery);
				}
			}
				
			logger.info("RecreateAllOLAPTablesExecutor finished");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
