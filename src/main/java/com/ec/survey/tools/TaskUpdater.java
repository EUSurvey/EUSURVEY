package com.ec.survey.tools;

import java.util.List;

import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.ArchiveService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.WebserviceService;
import com.ec.survey.model.WebserviceTask;

@Service("taskWorker")
@Scope("singleton")
public class TaskUpdater implements Runnable, BeanFactoryAware {

	protected static final Logger logger = Logger.getLogger(TaskUpdater.class);

	@Resource(name = "webserviceService")
	private WebserviceService webserviceService;
	
	@Resource(name = "surveyService")
	private SurveyService surveyService;
	
	@Resource(name = "administrationService")
	private AdministrationService administrationService;
	
	@Resource(name = "archiveService")
	private ArchiveService archiveService;
	
	@Resource(name = "taskExecutorLong")
	private TaskExecutor taskExecutorLong;
	
	private @Value("${archive.fileDir}") String archiveFileDir;
	private @Value("${isworkerserver}") String isworkerserver;
	private @Value("${useworkerserver}") String useworkerserver;
	
	protected BeanFactory context;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		context = beanFactory;		
	}
	
	@Override
	@Transactional
	public void run() {
		try {		
			List<WebserviceTask> tasks = webserviceService.getTasksToRestart();
			
			for (WebserviceTask task : tasks) {
				if (task.getCounter() < 10)
				{
					webserviceService.restartTask(task);
				} else {
					webserviceService.setError(task.getId(), "The task has been restarted 10 times but did not finish");
				}
			}
						
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
