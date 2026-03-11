package com.ec.survey.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Resource;

import com.ec.survey.replacements.Pair;
import com.ec.survey.tools.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.model.Setting;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.ReportingService.ToDoItem;

@Service
@Configurable
public class SchedulerService extends BasicService {
	
	@Resource(name = "departmentWorker")
	private DepartmentUpdater departmentWorker;

	@Resource(name = "fileWorker")
	private FileUpdater fileWorker;

    @Resource(name = "automaticSurveyDeleteWorker")
    private AutomaticSurveyDeleteWorker automaticSurveyDeleteWorker;

	@Resource(name = "exportWorker")
	private ExportUpdater exportWorker;
	
	@Resource(name = "validCodesRemover")
	private ValidCodesRemover validCodesRemover;
	
	@Resource(name = "deleteSurveysWorker")
	private DeleteSurveyUpdater deleteSurveysWorker;
	
	@Resource(name = "archiveExecutor")
	private ArchiveExecutor archiveExecutor;
	
	@Resource(name = "archiveFlagExecutor")
	private ArchiveFlagExecutor archiveFlagExecutor;
	
	@Resource(name = "deleteDraftsWorker")
	private DeleteDraftsUpdater deleteDraftsWorker;
	
	@Resource(name = "deleteTemporaryFoldersWorker")
	private DeleteTemporaryFolderUpdater deleteTemporaryFoldersWorker;
	
	@Resource(name = "deleteInvalidStatisticsWorker")
	private DeleteInvalidStatisticsWorker deleteInvalidStatisticsWorker;
	
	@Resource(name = "deleteUserAccountsWorker")
	private DeleteUserAccountsWorker deleteUserAccountsWorker;
		
	@Resource(name = "sendReportedSurveysWorker")
	private SendReportedSurveysWorker sendReportedSurveysWorker;
	
	@Resource(name = "surveyWorker")
	private SurveyUpdater surveyWorker;

	@Resource(name = "fsCheckWorker")
	private FsCheckWorker fsCheckWorker;

	@Resource(name= "answerSetAnonymWorker")
	private AnswerSetAnonymWorker answerSetAnonymWorker;

	public @Value("${showecas:false}") String showecas;
	public @Value("${host.executing.task:#{null}}") String hostExecutingTask;
	public @Value("${host.executing.todotask:#{null}}") String hostExecutingTODOTask;
	public @Value("${host.executing.ldaptask:#{null}}") String hostExecutingLDAPTask;

	@Scheduled(fixedDelay=600000) //every 10 minutes
	public void migrateFSSchedule() {	
		if(!isHost2ExecuteStandardTask())
			return;
		
		try {
			String start = settingsService.get(Setting.SurveyMigrateStart);
			String time = settingsService.get(Setting.SurveyMigrateTime);
			
			if (start == null || start.length() == 0 || time == null || time.length() == 0) return;
			
			String hours = start.substring(0, start.indexOf(':'));
			String minutes = start.substring(start.indexOf(':')+1);
			Date currentDate = new Date();
			
			Calendar c = Calendar.getInstance();
			c.setTime(currentDate);
			c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
			c.set(Calendar.MINUTE, Integer.parseInt(minutes));
			c.set(Calendar.SECOND, 0);
			
			Date startDate = c.getTime();
			
			c.add(Calendar.MINUTE, Integer.parseInt(time));
			Date endDate = c.getTime();
			
			if (currentDate.after(startDate) && currentDate.before(endDate))
			{
				String surveyid = settingsService.get(Setting.LastSurveyToMigrate);
				if (surveyid == null || surveyid.length() == 0 || surveyid.equals("0")) 
				{
					//all surveys are migrated
					logger.debug("all surveys are migrated");
				} else {				
					int id = Integer.parseInt(surveyid);
					
					while (currentDate.before(endDate) && id > 0)
					{																	
						Survey survey = surveyService.getSurvey(id);
						
						if (survey != null && survey.getIsDraft())
						{
							long tStart = System.currentTimeMillis();
							Survey draft = surveyService.getSurveyByUniqueIdToWrite(survey.getUniqueId());		
							if (draft == null) throw new InvalidURLException();
							
							fileService.migrateAllSurveyFiles(draft);		
		
							long tEnd = System.currentTimeMillis();
							long tDelta = tEnd - tStart;
							double elapsedSeconds = tDelta / 1000.0;
							
							logger.info("files for survey " + draft.getId() + " migrated, it took " + elapsedSeconds + " seconds");
						}
						
						id--;
						currentDate = new Date();
					}	
					
					settingsService.update(Setting.LastSurveyToMigrate, Integer.toString(id));
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	private Date lastDeleteAnswerPDFExecuted = null;
	
	@Scheduled(fixedDelay=600000) //every 10 minutes
	public void deleteAnswerPDFSchedule() {
		
		if(!isHost2ExecuteStandardTask())
			return;
		
		try {
			String start = settingsService.get(Setting.AnswerPDFDeletionStart);
			String time = settingsService.get(Setting.AnswerPDFDeletionTime);
			
			if (start == null || start.length() == 0 || time == null || time.length() == 0) return;
			
			String hours = start.substring(0, start.indexOf(':'));
			String minutes = start.substring(start.indexOf(':')+1);
			Date currentDate = new Date();
			
			Calendar c = Calendar.getInstance();
			c.setTime(currentDate);
			c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
			c.set(Calendar.MINUTE, Integer.parseInt(minutes));
			c.set(Calendar.SECOND, 0);
			
			Date startDate = c.getTime();
			
			c.add(Calendar.MINUTE, Integer.parseInt(time));
			Date endDate = c.getTime();
			
			if (currentDate.after(startDate) && currentDate.before(endDate) && (lastDeleteAnswerPDFExecuted == null || startDate.after(lastDeleteAnswerPDFExecuted)))
			{
				boolean circlecompleted = false;
				int id = Integer.parseInt(settingsService.get(Setting.LastSurveyToDeleteAnswerPDFs));
				
				c = Calendar.getInstance();
				c.setTime(currentDate);
				c.add(Calendar.MONTH, -1);
				Date lastmonth = c.getTime();
				
				if (id == 0)
				{
					id = surveyService.getHighestSurveyId();
				}
				
				while (currentDate.before(endDate) && id > 0)
				{			
					Pair<Boolean, String> isDraftAndUniqueId = surveyService.getIsDraftAndUniqueIDForSurveyId(id);
					
					if (isDraftAndUniqueId != null && isDraftAndUniqueId.getKey())
					{
						long tStart = System.currentTimeMillis();
						int deletedfiles = fileService.deleteOldAnswerPDFs(isDraftAndUniqueId.getValue(), lastmonth);
	
						long tEnd = System.currentTimeMillis();
						long tDelta = tEnd - tStart;
						double elapsedSeconds = tDelta / 1000.0;
						
						if(deletedfiles > 0)
                        {
							logger.info(deletedfiles + " old answer pdfs of survey " + id + " deleted, it took " + elapsedSeconds + " seconds");
                        }
					}
					
					id--;
					
					if (id == 0)
					{
						if (circlecompleted)
						{
							break;
						}
						
						id = surveyService.getHighestSurveyId();
						circlecompleted = true;
					}
					
					currentDate = new Date();
				}	
				
				settingsService.update(Setting.LastSurveyToDeleteAnswerPDFs, Integer.toString(id));
				logger.info("Finished deleting old answer pdfs");
				lastDeleteAnswerPDFExecuted = new Date();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@Scheduled(cron="0 0 2 * * *") //every night at 2 a.m.
	public void synchronizeCOMREF() {
		if(!isHost2ExecuteStandardTask())
			return;

		departmentWorker.run();
	}

	
	@Scheduled(fixedDelay=600000) //every 10 minutes
	public void migrateReportingSchedule() {	
		if(!isHost2ExecuteTODOTask())
			return;
		
		try {
			String enabled = settingsService.get(Setting.ReportingMigrationEnabled);
			if (enabled == null || !enabled.equalsIgnoreCase("true"))
			{
				return;
			}
			
			String start = settingsService.get(Setting.ReportingMigrationStart);
			String time = settingsService.get(Setting.ReportingMigrationTime);
			
			if (start == null || start.length() == 0 || time == null || time.length() == 0) return;
			
			String hours = start.substring(0, start.indexOf(':'));
			String minutes = start.substring(start.indexOf(':')+1);
			Date currentDate = new Date();
			
			Calendar c = Calendar.getInstance();
			c.setTime(currentDate);
			c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
			c.set(Calendar.MINUTE, Integer.parseInt(minutes));
			c.set(Calendar.SECOND, 0);
			
			Date startDate = c.getTime();
			
			c.add(Calendar.MINUTE, Integer.parseInt(time));
			Date endDate = c.getTime();
			
			if (currentDate.after(startDate) && currentDate.before(endDate))
			{
				logger.info("Start reporting migration");
				
				List<String> surveyUIDs = surveyService.getAllSurveyUIDs(false);
				for (String uid : surveyUIDs)
				{
					try {
						if (!reportingService.OLAPTableExists(uid, true))
						{
							reportingService.createOLAPTable(uid, true, false);
						} else {
							reportingService.updateOLAPTable(uid, true, false);
						}
						if (!reportingService.OLAPTableExists(uid, false))
						{
							reportingService.createOLAPTable(uid, false, true);
						} else {
							reportingService.updateOLAPTable(uid, false, true);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						logger.error("last query: " + ReportingService.lastQuery);
					}
					
					if ((new Date()).after(endDate))
					{
						break;
					}
				}				
				
				logger.info("Finished reporting migration");
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	@Scheduled(fixedDelay=10000) //wait for 10 seconds between calls
	public void doToDosSchedule() throws Exception {
		
		if (!isReportingDatabaseEnabled()) return;
		
		if(!isHost2ExecuteTODOTask())
			return;
		
		List<ToDoItem> todos = reportingService.getToDos();
		
		if (!todos.isEmpty())
		{
			logger.info("Start executing " + todos.size() + " todos");
			
			for (ToDoItem todo : todos) {
				try {
					reportingService.executeToDo(todo, true);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}

			logger.info("Finished executing " + todos.size() + " todos");
		}		
	}
	
	@Scheduled(cron="0 0 * * * *") //every hour
	public void doHourlySchedule() {
		if(!isHost2ExecuteStandardTask())
			return;
	  
		surveyWorker.run();
		fileWorker.run();
		fsCheckWorker.run();
	 }
	
	@Scheduled(cron="0 */5 * * * *") //every 5 minutes
	public void doNightlySchedule() {
		if(!isHost2ExecuteStandardTask())
			return;
		
		String start = settingsService.get(Setting.NightlyTaskStart);
		
		String hours = start.substring(0, start.indexOf(':'));
		String minutes = start.substring(start.indexOf(':')+1);
		Date currentDate = new Date();
		
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
		c.set(Calendar.MINUTE, Integer.parseInt(minutes));
		c.set(Calendar.SECOND, 0);		
		Date startDate = c.getTime();
		
		long seconds = Math.abs(currentDate.getTime()-startDate.getTime())/1000;
		if (seconds > 60) {
			return;
		}		

        automaticSurveyDeleteWorker.run();
		exportWorker.run();
		validCodesRemover.run();
		archiveFlagExecutor.run();
		archiveExecutor.run();
		deleteSurveysWorker.run();
		deleteDraftsWorker.run();
		deleteTemporaryFoldersWorker.run();
		deleteInvalidStatisticsWorker.run();
		sendReportedSurveysWorker.run();
		deleteUserAccountsWorker.run();
		answerSetAnonymWorker.run();

		surveyService.deleteAllOutdatedChangeOwnerRequests();
	 }

	@Scheduled(cron="0 0 1 * * *") //every night at 1 a.m.
	public void sendStatisticalEmails() throws Exception {
		if (!isHost2ExecuteStandardTask())
			return;

		logger.info("Start sendStatisticalEmails");

		Calendar cal = Calendar.getInstance();

		try {

			// daily emails
			surveyService.sendStatisticalEmails(Survey.ReportEmailFrequency.Daily);

			// weekly emails: only if today is Monday
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
				surveyService.sendStatisticalEmails(Survey.ReportEmailFrequency.Weekly);
			}

			// weekly emails: only if today is first day of the month
			if (cal.get(Calendar.DAY_OF_MONTH) == 1) {
				surveyService.sendStatisticalEmails(Survey.ReportEmailFrequency.Monthly);
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		logger.info("Finished sendStatisticalEmails");
	}
	
	public boolean isHost2ExecuteTODOTask() {
		return isHost2ExecuteTask(false, true);
	}

	public boolean isHost2ExecuteStandardTask() {
		return isHost2ExecuteTask(false, false);
	}

	private boolean isHost2ExecuteTask(boolean ldap, boolean todo){
		
		if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("true"))
		{
			return false;
		}
		
		String host = hostExecutingTask;		
		
		if (ldap) {
			host = hostExecutingLDAPTask;
			if (StringUtils.isEmpty(host)){
				logger.debug("The property host.executing.ldaptask is empty and scheduler will be executed");
				return true;
			}
		} else if (todo) {
			host = hostExecutingTODOTask;
			if (StringUtils.isEmpty(host)){
				logger.debug("The property host.executing.todotask is empty and scheduler will be executed");
				return true;
			}
		} else {
			if (StringUtils.isEmpty(host)){
				logger.debug("The property host.executing.task is empty and scheduler will be executed");
				return true;
			}
		}

		Enumeration<NetworkInterface> ipAddresses=null;
		try {
			 ipAddresses=NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			logger.error(e1);
		}
		
		for(NetworkInterface netint : Collections.list(ipAddresses)){						
			Enumeration<InetAddress> inetAddrs = netint.getInetAddresses();
			for(InetAddress inetAddr: Collections.list(inetAddrs)){
				if (StringUtils.contains(inetAddr.getHostName().toLowerCase(), host.toLowerCase())){
					return true;
				}
					
			}
		}

		if (StringUtils.isEmpty(hostExecutingTask)){
			logger.warn("Unable to determine if should be execute the Task on this host, no server name set in hostExecutingTask property");	
		}else{
			logger.debug("no server name found with this value: " + hostExecutingTask);
		}
		
		return false;
	}
}
