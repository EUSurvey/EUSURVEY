package com.ec.survey.service;

import com.ec.survey.model.Activity;
import com.ec.survey.model.ActivityFilter;
import com.ec.survey.model.Setting;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("activityService")
public class ActivityService extends BasicService {
	
	@Resource(name="settingsService")
	private SettingsService settingsService;
	
	@Autowired
	private SqlQueryService sqlQueryService;	
	
	@Transactional
	public void deleteLogsForSurvey(String uniqueId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("DELETE FROM Activity WHERE surveyUID = :uid").setString("uid", uniqueId);
		query.executeUpdate();
	}
	
	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void log(Map<Integer, String[]> activitiesToLog, Integer userId, String surveyUID) {
		Session session = sessionFactory.getCurrentSession();
		String loggingenabled = settingsService.get(Setting.ActivityLoggingEnabled);
		if (loggingenabled.equalsIgnoreCase("true"))
		{
			for (int logId : activitiesToLog.keySet())
			{
				String[] oldnew = activitiesToLog.get(logId);
				String enabled = settingsService.get(logId + "ActivityEnabled");
				
				int counter = 0;
				
				while (oldnew.length > counter)
				{
					if (enabled.equalsIgnoreCase("true"))
					{
						Activity activity = new Activity();
						activity.setDate(new Date());
						activity.setLogID(logId);
						activity.setOldValue(oldnew[counter++]);
						activity.setNewValue(oldnew[counter++]);
						
						checkValueSizes(activity);
						
						activity.setUserId(userId);
						activity.setSurveyUID(surveyUID);
						session.save(activity);
					}
				}
			}
		}
	}

	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void logTranslations(int activityCode, String code, Map<String, String> oldInfo, Map<String, String> info, int userId, String surveyUID) {
		Session session = sessionFactory.getCurrentSession();
		String loggingenabled = settingsService.get(Setting.ActivityLoggingEnabled);
		if (loggingenabled != null && loggingenabled.equalsIgnoreCase("true"))
		{
			String enabled = settingsService.get(activityCode + "ActivityEnabled");
			
			if (enabled != null && enabled.equalsIgnoreCase("true"))
			{
				//first changed ones
				if (oldInfo != null)
				{
					for (String key : oldInfo.keySet()) {
						String oldValue = oldInfo.get(key);
						String newValue = info.getOrDefault(key, "");
						
						if (!oldValue.equalsIgnoreCase(newValue))
						{
							Activity activity = new Activity();
							activity.setDate(new Date());
							activity.setLogID(activityCode);
							activity.setOldValue(code + " " + key + ": " + oldValue);
							activity.setNewValue(code + " " + key + ": " + newValue);
							
							checkValueSizes(activity);
							
							activity.setUserId(userId);
							activity.setSurveyUID(surveyUID);
							session.save(activity);
						}
					}
				}
				
				//then new ones
				for (String key : info.keySet()) {
					if (oldInfo == null || !oldInfo.containsKey(key))
					{
						Activity activity = new Activity();
						activity.setDate(new Date());
						activity.setLogID(activityCode);
						activity.setOldValue(code + " " + key + ":");
						activity.setNewValue(code + " " + key + ": " + info.get(key));
						
						checkValueSizes(activity);
						
						activity.setUserId(userId);
						activity.setSurveyUID(surveyUID);
						session.save(activity);
					}
				}
			}
		}		
	}

	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public boolean isLogEnabled(int activityCode) {
		if (activityCode > 1000)
		{
			return true;
		} else {
			String loggingenabled = settingsService.get(Setting.ActivityLoggingEnabled);
			if (loggingenabled != null && loggingenabled.equalsIgnoreCase("true"))
			{
				String enabled = settingsService.get(activityCode + "ActivityEnabled");
				
				if (enabled != null && enabled.equalsIgnoreCase("true"))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void log(int activityCode, String oldValue, String newValue, int userId, String surveyUID, String type) {
		Session session = sessionFactory.getCurrentSession();
		
		if (activityCode > 1000)
		{
			//global log
			Activity activity = new Activity();
			activity.setDate(new Date());
			activity.setLogID(activityCode);
			activity.setOldValue(oldValue);
			activity.setNewValue(newValue);
			
			checkValueSizes(activity);
			
			activity.setUserId(userId);
			activity.setSurveyUID(surveyUID);
			activity.setType(type);
			session.save(activity);
		} else {		
			String loggingenabled = settingsService.get(Setting.ActivityLoggingEnabled);
			if (loggingenabled != null && loggingenabled.equalsIgnoreCase("true"))
			{
				String enabled = settingsService.get(activityCode + "ActivityEnabled");
				
				if (enabled != null && enabled.equalsIgnoreCase("true"))
				{
					Activity activity = new Activity();
					activity.setDate(new Date());
					activity.setLogID(activityCode);
					activity.setOldValue(oldValue);
					activity.setNewValue(newValue);
					
					checkValueSizes(activity);
					
					activity.setUserId(userId);
					activity.setSurveyUID(surveyUID);
					activity.setType(type);
					session.save(activity);
				}
			}
		}		
	}
	
	private void checkValueSizes(Activity activity)
	{
		if (activity.getOldValue() != null && activity.getOldValue().length() > 65000)
		{
			activity.setOldValue(activity.getOldValue().substring(0, 65000) + "...");
		}
		
		if (activity.getNewValue() != null && activity.getNewValue().length() > 65000)
		{
			activity.setNewValue(activity.getNewValue().substring(0, 65000) + "...");
		}
	}
	
	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void log(int activityCode, String oldValue, String newValue, int userId, String surveyUID)
	{
		log(activityCode, oldValue, newValue, userId, surveyUID, null);
	}
	
	private String getHQL(ActivityFilter filter, Map<String, Object> params)
	{
		String hql = "FROM Activity WHERE surveyUID = :uid";
		
		if (filter.getLogId() > 0)
		{
			hql += " AND logID = :logid";
			params.put("logid", filter.getLogId());
		}
		
		if (filter.getObject() != null)
		{
			switch (filter.getObject()) {
				case "Survey":
					hql += " AND logID > 100 AND logID < 300";
					break;
				case "DraftSurvey":
					hql += " AND logID > 200 AND logID < 300";
					break;
				case "Results":
					hql += " AND logID > 300 AND logID < 400";
					break;
				case "Contribution":
					hql += " AND logID > 400 AND logID < 500";
					break;
				case "GuestList":
					hql += " AND logID > 500 AND logID < 600";
					break;
				case "Privileges":
					hql += " AND logID > 600 AND logID < 700";
					break;
				case "Messages":
					hql += " AND logID > 700 AND logID < 800";
					break;
			}
		}
		
		if (filter.getProperty() != null)
		{
			switch(filter.getProperty())
			{
				case "Alias":
					hql += " AND logID = 109"; break;
				case "Anonymity":
					hql += " AND logID = 116"; break;
				case "AutoNumberingQuestions":
					hql += " AND logID = 216"; break;
				case "AutoNumberingSections":
					hql += " AND logID = 215"; break;
				case "Autopublish":
					hql += " AND logID = 210"; break;
				case "BackgroundDocument":
					hql += " AND (logID = 205 OR logID = 206)"; break;
				case "Captcha":
					hql += " AND logID = 118"; break;
				case "ConfirmationPage":
					hql += " AND logID = 225"; break;
				case "Contact":
					hql += " AND logID = 209"; break;
				case "ContactCreation":
					hql += " AND logID = 113"; break;
				case "EditContribution":
					hql += " AND logID = 119"; break;
				case "ElementOrder":
					hql += " AND logID = 217"; break;
				case "EndDate":
					hql += " AND logID = 212"; break;
				case "EndNotificationMessage":
					hql += " AND logID = 701"; break;
				case "EndNotificationReach":
					hql += " AND logID = 112"; break;
				case "EndNotificationState":
					hql += " AND logID = 110"; break;
				case "EndNotificationValue":
					hql += " AND logID = 111"; break;
				case "WCAGCompliance":
					hql += " AND logID = 122"; break;
				case "EscapePage":
					hql += " AND logID = 226"; break;
				case "Export":
					hql += " AND (logID = 310 OR logID = 311)"; break;
				case "ExportCharts":
					hql += " AND logID = 309"; break;
				case "ExportContent":
					hql += " AND logID = 308"; break;
				case "ExportStatistics":
					hql += " AND logID = 307"; break;
				case "ExportActivities":
					hql += " AND logID = 312"; break;
				case "Invitations":
					hql += " AND logID = 506"; break;
				case "Logo":
					hql += " AND logID = 213"; break;
				case "MultiPaging":
					hql += " AND logID = 120"; break;
				case "n/a":
					hql += " AND logID IN :logids"; 
					Integer[] logids = {101,102,103,104,401,402,403,404,405,406,601,602,603};
					params.put("logids", logids);
					break;
				case "PageWiseValidation":
					hql += " AND logID = 121"; break;
				case "Password":
					hql += " AND logID = 115"; break;
				case "PendingChanges":
					hql += " AND (logID = 107 OR logID = 108)"; break;
				case "PivotLanguage":
					hql += " AND logID = 208"; break;
				case "Privacy":
					hql += " AND logID = 117"; break;
				case "Properties":
					hql += " AND logID = 202"; break;
				case "PublicSearch":
					hql += " AND logID = 304"; break;
				case "PublishAnswerSelection":
					hql += " AND logID = 306"; break;
				case "PublishCharts":
					hql += " AND logID = 302"; break;
				case "PublishIndividual":
					hql += " AND logID = 301"; break;
				case "PublishQuestionSelection":
					hql += " AND logID = 305"; break;
				case "PublishStatistics":
					hql += " AND logID = 303"; break;
				case "Security":
					hql += " AND logID = 114"; break;
				case "Skin":
					hql += " AND logID = 214"; break;
				case "StartDate":
					hql += " AND logID = 211"; break;
				case "State":
					hql += " AND (logID = 105 OR logID = 106)"; break;
				case "SurveyElement":
					hql += " AND (logID = 218 OR logID = 219 OR logID = 220)"; break;
				case "Title":
					hql += " AND logID = 207"; break;
				case "Token/Contacts/Department":
					hql += " AND logID > 500 AND logID < 506"; break;
				case "Translation":
					hql += " AND (logID > 220 AND logID < 225) OR logID = 227 OR logID = 228"; break;
				case "UsefulLink":
					hql += " AND (logID = 203 OR logID = 204)"; break;
			}			
		}
		
		if (filter.getEvent() != null)
		{
			switch(filter.getEvent())
			{
				case "Added":
					hql += " AND logID IN :logids2"; 
					Integer[] logids2 = {203,205,218,221,601};
					params.put("logids2", logids2);
					break;
				case "Applied":
					hql += " AND logID = 107"; break;
				case "Created":
					hql += " AND logID IN :logids3"; 
					Integer[] logids3 = {101,102,103,501};
					params.put("logids3", logids3);
					break;
				case "Deleted":
					hql += " AND logID IN :logids4"; 
					Integer[] logids4 = {104,219,222,311,402,405,502};
					params.put("logids4", logids4);
					break;
				case "Disabled":
					hql += " AND logID = 224"; break;
				case "Discarded":
					hql += " AND logID = 108"; break;
				case "Enabled":
					hql += " AND logID = 223"; break;
				case "Modified":
					hql += " AND logID IN :logids5"; 
					Integer[] logids5 = {602,105,106,109,110,111,112,113,114,115,116,117,118,119,120,121,122,207,208,209,210,211,212,213,214,215,216,217,220,225,226,301,302,303,304,305,306,403,505};
					params.put("logids5", logids5);
					break;
				case "Opened":
					hql += " AND logID = 201"; break;
				case "Paused":
					hql += " AND logID = 503"; break;
				case "Removed":
					hql += " AND logID IN :logids6"; 
					Integer[] logids6 = {204,206,603};
					params.put("logids6", logids6);
					break;
				case "Returned":
					hql += " AND logID = 310"; break;
				case "Requested":
					hql += " AND logID = 228"; break;
				case "Saved":
					hql += " AND logID = 202"; break;
				case "Sent":
					hql += " AND (logID = 506 OR logID = 701)"; break;
				case "Started":
					hql += " AND logID IN :logids7"; 
					Integer[] logids7 = {307,308,309,312,504};
					params.put("logids7", logids7);
					break;
				case "Submitted":
					hql += " AND logID = 401"; break;
			}
		}
		
		if (filter.getUserId() > 0)
		{
			hql += " AND userId = :userid";
			params.put("userid", filter.getUserId());
		}
		
		if (filter.getOldValue() != null && filter.getOldValue().length() > 0)
		{
			hql += " AND oldValue LIKE :old";
			params.put("old", "%" + filter.getOldValue() + "%");
		}
		
		if (filter.getNewValue() != null && filter.getNewValue().length() > 0)
		{
			hql += " AND newValue LIKE :new";
			params.put("new", "%" + filter.getNewValue() + "%");
		}
		
		if (filter.getDateFrom() != null)
		{
			hql += " AND date >= :dateFrom";
			params.put("dateFrom", filter.getDateFrom());
		}
		
		if (filter.getDateTo() != null)
		{
			hql += " AND date <= :dateTo";
			params.put("dateTo", Tools.getFollowingDay(filter.getDateTo()));
		}
		
		return hql;
	}

	@Transactional(readOnly = true)
	public int getNumberActivities(ActivityFilter filter) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		
		Map<String, Object> params = new HashMap<>();
		
		String hql = "SELECT count(*) " + getHQL(filter, params);
		Query query = session.createQuery(hql).setString("uid", filter.getSurveyUid());
		sqlQueryService.setParameters(query, params);
		
		return ConversionTools.getValue(query.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Activity> get(ActivityFilter filter, int page, int rowsPerPage) {
		Session session = sessionFactory.getCurrentSession();
		
		Map<String, Object> params = new HashMap<>();
		
		String hql = getHQL(filter, params);
		hql += " ORDER BY id DESC";
		Query query = session.createQuery(hql).setString("uid", filter.getSurveyUid());
		
		for (String key : params.keySet())
		{
			Object param = params.get(key);
			if (param instanceof Integer)
			{
				query.setInteger(key, (Integer)param);
			} else if (param instanceof Integer[])
			{
				query.setParameterList(key, (Integer[])param);
			} else if (param instanceof String)
			{
				query.setString(key, (String)param);
			} else if (param instanceof Date)
			{
				query.setDate(key, (Date)param);
			}
		}
		
		return query.setFirstResult((page - 1)*rowsPerPage).setMaxResults(rowsPerPage).list();
	}

	@Transactional(readOnly = true)
	public boolean isEnabled(int activityCode) {
		String loggingenabled = settingsService.get(Setting.ActivityLoggingEnabled);
		if (loggingenabled.equalsIgnoreCase("true"))
		{
			String enabled = settingsService.get(activityCode + "ActivityEnabled");
			return enabled.equalsIgnoreCase("true");
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Integer> getAllUsers(String uniqueId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT DISTINCT userId FROM Activity WHERE surveyUID = :uid").setString("uid", uniqueId);
		return query.list();
	}
	
}
