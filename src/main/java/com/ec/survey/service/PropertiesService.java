package com.ec.survey.service;

import com.ec.survey.model.Property;
import com.ec.survey.model.Setting;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("propertiesService")
public class PropertiesService extends BasicService {

	@Transactional(readOnly = true)
	public String get(String key) {
		Session session = sessionFactory.getCurrentSession();		
		Query<String> query = session.createQuery("SELECT value FROM Property WHERE key = :key", String.class).setParameter("key", key);
		return query.uniqueResult();
	}

	@Transactional(readOnly = false)
	public void add(String key, String value) {
		Session session = sessionFactory.getCurrentSession();		
		Property property = new Property();
		property.setKey(key);
		property.setValue(value);
		session.save(property);
	}

	@Transactional(readOnly = false)
	public void update(String key, String val) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		Query<Setting> query = session.createQuery("UPDATE Property SET value = :value WHERE key = :key")
				.setParameter("key", key).setParameter("value", val);
		query.executeUpdate();		
	}
	
}
