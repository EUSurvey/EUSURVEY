package com.ec.survey.service;

import java.util.Calendar;
import java.util.Date;
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
	private DomainUpdater domainWorker;
	
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
	
	@Resource(name = "settingsService")
	private SettingsService settingsService;
	
	@Resource(name = "schemaService")
	private SchemaService schemaService;

	public @Value("${showecas}") String showecas;	
		
	public boolean isShowEcas()
	{
		return showecas != null && showecas.equalsIgnoreCase("true");
	}

	
	@Scheduled(fixedDelay=600000) //every 10 minutes
	public void migrateFSSchedule() {	
		if(!isHost2ExecuteScheduledTask())
			return;
		
		try {
			String start = settingsService.get(Setting.SurveyMigrateStart);
			String time = settingsService.get(Setting.SurveyMigrateTime);
			
			if (start == null || start.length() == 0 || time == null || time.length() == 0) return;
			
			String hours = start.substring(0, start.indexOf(":"));
			String minutes = start.substring(start.indexOf(":")+1);
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
				
				logger.debug("Finished fs migration");
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	private Date lastDeleteAnswerPDFExecuted = null;
	
	@Scheduled(fixedDelay=600000) //every 10 minutes
	public void deleteAnswerPDFSchedule() {
		
		if(!isHost2ExecuteScheduledTask())
			return;
		
		try {
			String start = settingsService.get(Setting.AnswerPDFDeletionStart);
			String time = settingsService.get(Setting.AnswerPDFDeletionTime);
			
			if (start == null || start.length() == 0 || time == null || time.length() == 0) return;
			
			String hours = start.substring(0, start.indexOf(":"));
			String minutes = start.substring(start.indexOf(":")+1);
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
		
		if(!isHost2ExecuteScheduledTask())
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
		
		if(!isHost2ExecuteScheduledTask())
			return;
		
		logger.debug("Try Start ldap sync schedule host compaptible to launch this task");
		 
		//|| !isCasOss()
		if (!isShowEcas() ) return;
		
		logger.debug("Start ldap sync schedule try to execute");
		
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
					
					domainWorker.run();
					departmentWorker.run();
					ecasWorker.run();		
				    
					logger.debug("End ldap sync schedule");
				}	
			}
		
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	 }
	
	@Scheduled(fixedDelay=600000) //every 10 minutes
	public void migrateReportingSchedule() {	
		if(!isHost2ExecuteScheduledTask())
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
			
			String hours = start.substring(0, start.indexOf(":"));
			String minutes = start.substring(start.indexOf(":")+1);
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
	
	@Scheduled(cron="0 0 * * * *") //every hour
	public void doHourlySchedule() {	
		if(!isHost2ExecuteScheduledTask())
			return;

		logger.debug("Start hourly schedule");
	  
		surveyWorker.run();
		fileWorker.run();
		
		logger.debug("End hourly schedule");
	 }
	
	@Scheduled(cron="0 0 4 * * *") //every night at 4 pm
	public void doNightlySchedule() {
		if(!isHost2ExecuteScheduledTask())
			return;

		logger.debug("Start nightly schedule");
	  
		exportWorker.run();
		validCodesRemover.run();
		deleteSurveysWorker.run();
		deleteDraftsWorker.run();
		deleteTemporaryFoldersWorker.run();
		deleteInvalidStatisticsWorker.run();
		sendReportedSurveysWorker.run();
		deleteUserAccountsWorker.run();
		
		logger.debug("End nightly schedule");
	 }

}
