package com.ec.survey.tools;

import com.ec.survey.service.AnswerService;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

@Service("deleteDraftsWorker")
@Scope("singleton")
public class DeleteDraftsUpdater implements Runnable {

	protected static final Logger logger = Logger.getLogger(DeleteDraftsUpdater.class);
	
	@Resource(name="answerService")
	private AnswerService answerService;
		
	@Override
	public void run() {
		try {
			logger.info("DeleteDraftsUpdater started");
			Calendar cal = Calendar.getInstance();			
			cal.add(Calendar.MONTH, -6);
			
			Calendar calEnd = Calendar.getInstance();
			calEnd.add(Calendar.HOUR, 1);
			
			int deleteddrafts = answerService.deleteOldDrafts(cal.getTime());
			
			while (deleteddrafts >= 1000)
			{
				Date currentTime = new Date();
				if (currentTime.after(calEnd.getTime()))
				{
					break;
				}
				
				deleteddrafts = answerService.deleteOldDrafts(cal.getTime());
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}		
		logger.info("DeleteDraftsUpdater completed");
	}
	
}
