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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.model.Setting;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.ReportingService.ToDoItem;
import com.ec.survey.tools.AnswerSetAnonymWorker;
import com.ec.survey.tools.ArchiveExecutor;
import com.ec.survey.tools.ArchiveFlagExecutor;
import com.ec.survey.tools.DeleteDraftsUpdater;
import com.ec.survey.tools.DeleteInvalidStatisticsWorker;
import com.ec.survey.tools.DeleteSurveyUpdater;
import com.ec.survey.tools.DeleteTemporaryFolderUpdater;
import com.ec.survey.tools.DeleteUserAccountsWorker;
import com.ec.survey.tools.DepartmentUpdater;
import com.ec.survey.tools.DomainUpdater;
import com.ec.survey.tools.EcasUserDeactivator;
import com.ec.survey.tools.EcasUserUpdater;
import com.ec.survey.tools.ExportUpdater;
import com.ec.survey.tools.FileUpdater;
import com.ec.survey.tools.SendReportedSurveysWorker;
import com.ec.survey.tools.SurveyUpdater;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.ValidCodesRemover;

@Service
@Configurable
public class SchedulerService extends BasicService {
	
	@Resource(name = "departmentWorker")
	private DepartmentUpdater departmentWorker;
	
	@Resource(name = "domainWorker")
	private DomainUpdater domaintWorker;
	
	@Resource(name = "ecasWorker")
	private EcasUserUpdater ecasWorker;
	
	@Resource(name = "ecasDeactivator")
	private EcasUserDeactivator ecasDeactivator;

	@Resource(name = "fileWorker")
	private FileUpdater fileWorker;
	
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

	@Resource(name= "answerSetAnonymWorker")
	private AnswerSetAnonymWorker answerSetAnonymWorker;
	
	@Resource(name = "schemaService")
	private SchemaService schemaService;

	public @Value("${showecas}") String showecas;	
	public @Value("${host.executing.task:#{null}}") String hostExecutingTask;
	public @Value("${host.executing.todotask:#{null}}") String hostExecutingTODOTask;
	public @Value("${host.executing.ldaptask:#{null}}") String hostExecutingLDAPTask;
		
	public boolean isShowEcas()
	{
		return showecas != null && showecas.equalsIgnoreCase("true");
	}
	
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
					Survey survey = surveyService.getSurvey(id);
					
					if (survey != null && survey.getIsDraft())
					{
						long tStart = System.currentTimeMillis();
						int deletedfiles = fileService.deleteOldAnswerPDFs(survey.getUniqueId(), lastmonth);		
	
						long tEnd = System.currentTimeMillis();
						long tDelta = tEnd - tStart;
						double elapsedSeconds = tDelta / 1000.0;
						
						if(deletedfiles > 0)
                        {
							logger.info(deletedfiles + " old answer pdfs of survey " + survey.getId() + " deleted, it took " + elapsedSeconds + " seconds");
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

	@Scheduled(fixedDelay=600000) //every 10 minutes
	public void doLDAPRemoveDeletedUsersSyncSchedule() {
		
		if(!isHost2ExecuteLDAPTask())
			return;

		if (!isShowEcas()) return;	
		
		try {
			
			String enabled = settingsService.get(Setting.LDAPsync2Enabled);
			
			//check if feature is enabled
			if (StringUtils.isNotEmpty(enabled) && enabled.equalsIgnoreCase("true"))
			{
				String frequency = settingsService.get(Setting.LDAPsync2Frequency);
				String start = settingsService.get(Setting.LDAPsync2Start);
				String time = settingsService.get(Setting.LDAPsync2Time);
				
				Date lastSyncDate = schemaService.getLastLDAPSynchronization2Date();
				Date startDate = Tools.parseDateString(start,"dd/MM/yyyy HH:mm:ss");
				Date currentDate = new Date();
				
				//check if global start date is over
				if  (currentDate.after(startDate))
				{
					Calendar c = Calendar.getInstance();
					
					//compute next sync date
					if (lastSyncDate != null)
					{
						int days = 0;
						int prefix = Integer.parseInt(frequency.substring(0, frequency.length() -1));
						if (frequency.toLowerCase().endsWith("w"))
						{
							days = 7 * prefix;
						} else {
							days = prefix;
						}
						c.setTime(lastSyncDate);
						c.add(Calendar.DAY_OF_MONTH, days);
					} else {
						//if there was no sync before, do it today
						c.setTime(currentDate);
					}
					
					//set time
					int hours = Integer.parseInt(time.substring(0,2));
					int minutes = Integer.parseInt(time.substring(3,5));
					
					c.set(Calendar.HOUR_OF_DAY, hours);
					c.set(Calendar.MINUTE, minutes);
					c.set(Calendar.SECOND, 0);
					
					Date nextSyncDate = c.getTime();
					
					//skip if we have to wait
					if (nextSyncDate.after(currentDate))
					{
						return;
					}				
					
					ecasDeactivator.run();
					logger.info("Finished ldap deleted users sync schedule");
				}	
			}
		
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	@Scheduled(fixedDelay=600000) //every 10 minutes
	public void doLDAPSyncSchedule() {
		
		if(!isHost2ExecuteLDAPTask())
			return;
		 
		//|| !isCasOss()
		if (!isShowEcas() ) return;
		
		try {
		
			String enabled = settingsService.get(Setting.LDAPsyncEnabled);
			
			//check if feature is enabled
			if (StringUtils.isNotEmpty(enabled) && enabled.equalsIgnoreCase("true"))
			{
				String frequency = settingsService.get(Setting.LDAPsyncFrequency);
				String start = settingsService.get(Setting.LDAPsyncStart);
				String time = settingsService.get(Setting.LDAPsyncTime);
				
				Date lastSyncDate = schemaService.getLastLDAPSynchronizationDate();
				
				Date startDate = Tools.parseDateString(start, "dd/MM/yyyy HH:mm:ss");
				Date currentDate = new Date();
				
				//check if global start date is over
				if  (currentDate.after(startDate))
				{
					Calendar c = Calendar.getInstance();
					
					//compute next sync date
					if (lastSyncDate != null)
					{
						int days = 0;
						int prefix = Integer.parseInt(frequency.substring(0, frequency.length() -1));
						if (frequency.toLowerCase().endsWith("w"))
						{
							days = 7 * prefix;
						} else {
							days = prefix;
						}
						c.setTime(lastSyncDate);
						c.add(Calendar.DAY_OF_MONTH, days);
					} else {
						//if there was no sync before, do it today
						c.setTime(currentDate);
					}
					
					//set time
					int hours = Integer.parseInt(time.substring(0,2));
					int minutes = Integer.parseInt(time.substring(3,5));
					
					c.set(Calendar.HOUR_OF_DAY, hours);
					c.set(Calendar.MINUTE, minutes);
					c.set(Calendar.SECOND, 0);
					
					Date nextSyncDate = c.getTime();
					
					//skip if we have to wait
					if (nextSyncDate.after(currentDate))
					{
						return;
					}				
					
					domaintWorker.run();
					departmentWorker.run();
					ecasWorker.run();				  
				}	
			}
		
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
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
	 }
	
	private boolean isHost2ExecuteLDAPTask() {
		return isHost2ExecuteTask(true, false);
	}

	private boolean isHost2ExecuteTODOTask() {
		return isHost2ExecuteTask(false, true);
	}
	
	private boolean isHost2ExecuteStandardTask() {
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
