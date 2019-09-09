package com.ec.survey.tools;

import java.util.List;

import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.ec.survey.service.ReportingService;
import com.ec.survey.service.SurveyService;

@Service("createAllOLAPTablesExecutor")
@Scope("prototype")
public class CreateAllOLAPTablesExecutor implements Runnable {

	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="reportingService")
	private ReportingService reportingService;	

	private static final Logger logger = Logger.getLogger(CreateAllOLAPTablesExecutor.class);
	
	public void run()
	{
		try {
			logger.info("CreateAllOLAPTablesExecutor started");
			
			List<String> surveyUIDs = surveyService.getAllSurveyUIDs(false);
			for (String uid : surveyUIDs)
			{
				try {
					if (!reportingService.OLAPTableExists(uid, true))
					{
						reportingService.createOLAPTable(uid, true, false);
					}
					if (!reportingService.OLAPTableExists(uid, false))
					{
						reportingService.createOLAPTable(uid, false, true);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					logger.error("last query: " + ReportingService.lastQuery);
				}
			}
				
			logger.info("CreateAllOLAPTablesExecutor finished");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
