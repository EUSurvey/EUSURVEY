package com.ec.survey.service;

import com.ec.survey.model.Activity;
import com.ec.survey.model.ActivityFilter;
import com.ec.survey.model.Setting;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.activity.ActivityRegistry;
import org.apache.commons.lang3.Range;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Service("activityService")
public class ActivityService extends BasicService {
	
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
			for (Entry<Integer, String[]> entry : activitiesToLog.entrySet())
			{
				int logId = entry.getKey();
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
					for (Entry<String, String> entry : oldInfo.entrySet()) {
						String oldValue = entry.getValue();
						String newValue = info.getOrDefault(entry.getKey(), "");
						
						if (!oldValue.equalsIgnoreCase(newValue))
						{
							Activity activity = new Activity();
							activity.setDate(new Date());
							activity.setLogID(activityCode);
							activity.setOldValue(code + " " + entry.getKey() + ": " + oldValue);
							activity.setNewValue(code + " " + entry.getKey() + ": " + newValue);
							
							checkValueSizes(activity);
							
							activity.setUserId(userId);
							activity.setSurveyUID(surveyUID);
							session.save(activity);
						}
					}
				}
				
				//then new ones
				for (Entry<String, String> entry : info.entrySet()) {
					if (oldInfo == null || !oldInfo.containsKey(entry.getKey()))
					{
						Activity activity = new Activity();
						activity.setDate(new Date());
						activity.setLogID(activityCode);
						activity.setOldValue(code + " " + entry.getKey() + ":");
						activity.setNewValue(code + " " + entry.getKey() + ": " + entry.getValue());
						
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
			Range<Integer> range = ActivityRegistry.getObjectRange(filter.getObject());

			if (range.getMaximum() > 0){
				hql += " AND logID >= :rangemin AND logID <= :rangemax";
				params.put("rangemin", range.getMinimum());
				params.put("rangemax", range.getMaximum());
			}
		}
		
		if (filter.getProperty() != null)
		{
			Integer[] logIds = ActivityRegistry.getPropertyIds(filter.getProperty());

			if (logIds.length > 0) {
				hql += " AND logID IN :logids";
				params.put("logids", logIds);
			}
		}
		
		if (filter.getEvent() != null)
		{
			Integer[] logIds2 = ActivityRegistry.getEventIds(filter.getEvent());

			if (logIds2.length > 0) {
				hql += " AND logID IN :logids2";
				params.put("logids2", logIds2);
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
		
		for (Entry<String, Object> entry : params.entrySet())
		{
			if (entry.getValue() instanceof Integer)
			{
				query.setInteger(entry.getKey(), (Integer)entry.getValue());
			} else if (entry.getValue() instanceof Integer[])
			{
				query.setParameterList(entry.getKey(), (Integer[])entry.getValue());
			} else if (entry.getValue() instanceof String)
			{
				query.setString(entry.getKey(), (String)entry.getValue());
			} else if (entry.getValue() instanceof Date)
			{
				query.setDate(entry.getKey(), (Date)entry.getValue());
			}
		}
		
		return query.setFirstResult((page > 1 ? page - 1 : 0)*rowsPerPage).setMaxResults(rowsPerPage).list();
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
