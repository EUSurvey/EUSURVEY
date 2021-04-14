package com.ec.survey.tools;

import java.nio.file.Files;
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
import com.ec.survey.model.Archive;
import com.ec.survey.model.WebserviceTask;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;

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
			
			if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("true"))
			{
				List<Archive> archives = archiveService.getArchivesToRestart();
				for (Archive archive : archives)
				{
					Survey survey = surveyService.getSurveyByUniqueId(archive.getSurveyUID(), false, true);
					User u = administrationService.getUser(archive.getUserId());
					ArchiveExecutor export = (ArchiveExecutor) context.getBean("archiveExecutor"); 
					export.init(archive, survey, u);
					if (export.prepare()) {					
						//it's not allowed to override existing archive files so we have to delete them first
						java.io.File target = new java.io.File(archiveFileDir + survey.getUniqueId());
						Files.deleteIfExists(target.toPath());
						taskExecutorLong.execute(export);
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
