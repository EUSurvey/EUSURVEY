package com.ec.survey.service;

import com.ec.survey.model.Setting;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("settingsService")
public class SettingsService extends BasicService {
	
	@Transactional(readOnly = true)
	public String get( String key ) {
		Session session = sessionFactory.getCurrentSession();		
		Query query = session.createQuery("SELECT value FROM Setting WHERE key = :key").setString("key", key);
		return (String) query.uniqueResult();		
	}
	
	@Transactional(readOnly = true)
	public List<Integer> getEnabledActivityLoggingIds() {
		List<Integer> ids = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Setting WHERE key LIKE :key AND value LIKE :val").setString("key", "%ActivityEnabled").setString("val","true");
		for (Object s : query.list())
		{
			ids.add(Integer.parseInt(((Setting)s).getKey().substring(0, 3)));
		}
		return ids;
	}
	
	@Transactional(readOnly = false)
	public void add(String key, String value, String format) {
		Session session = sessionFactory.getCurrentSession();		
		Setting setting = new Setting();
		setting.setKey(key);
		setting.setValue(value);
		setting.setFormat(format);
		session.save(setting);
	}

	@Transactional(readOnly = false)
	public void update(String key, String val) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("UPDATE Setting SET value = :value WHERE key = :key").setString("key", key).setString("value", val);
		query.executeUpdate();		
	}
	
}
