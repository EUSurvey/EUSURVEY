package com.ec.survey.tools;

import java.util.List;

import javax.annotation.Resource;

import com.ec.survey.service.*;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ec.survey.model.WebserviceTask;

@Service("taskWorker")
@Scope("singleton")
public class TaskUpdater implements Runnable {

	protected static final Logger logger = Logger.getLogger(TaskUpdater.class);

	@Resource(name = "webserviceService")
	private WebserviceService webserviceService;

	@Resource(name = "schedulerService")
	private SchedulerService schedulerService;

	@Override
	@Transactional
	public void run() {
		try {
			if (schedulerService.isHost2ExecuteStandardTask()) {
				List<WebserviceTask> tasks = webserviceService.getTasksToRestart();

				for (WebserviceTask task : tasks) {
					if (task.getCounter() < 10) {
						webserviceService.restartTask(task);
					} else {
						webserviceService.setError(task.getId(), "The task has been restarted 10 times but did not finish");
					}
				}
			}
						
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
