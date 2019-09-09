package com.ec.survey.service;

import com.ec.survey.model.ValidCode;
import com.ec.survey.model.survey.Survey;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("validCodesService")
public class ValidCodesService extends BasicService {
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ValidCode> getAll() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM ValidCode");
		return query.list();
	}
	
	@Transactional(readOnly = true)
	public ValidCode get( Integer id ) {
		Session session = sessionFactory.getCurrentSession();
		return (ValidCode) session.get(ValidCode.class, id);
	}
	
	@Transactional
	public void add(String uniqueCode, Survey survey) {
		Session session = sessionFactory.getCurrentSession();
		session.evict(survey);
		ValidCode validCode = new ValidCode(uniqueCode, survey.getUniqueId());
		session.save(validCode);
	}

	@Transactional
	public void delete(String uniqueCode) {
		Session session = sessionFactory.getCurrentSession();	
		Query query = session.createQuery("DELETE FROM ValidCode WHERE code = :code").setString("code", uniqueCode);
		query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public boolean CheckValid(String uniqueCode, String surveyUid) {
		Session session = sessionFactory.getCurrentSession();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Query query = session.createQuery("FROM ValidCode WHERE code = :code AND (surveyUid is null OR surveyUid = :surveyUid) AND created  > :yesterday").setString("surveyUid", surveyUid).setString("code", uniqueCode).setDate("yesterday", cal.getTime());
		return query.list().size() == 1;
	}
	
	@Transactional
	public void revalidate(String uniqueCode, Survey survey)
	{
		Session session = sessionFactory.getCurrentSession();	
		session.evict(survey);
		Query query = session.createQuery("FROM ValidCode WHERE code = :code AND (surveyUid is null OR surveyUid = :surveyUid)").setString("surveyUid", survey.getUniqueId()).setString("code", uniqueCode);
		int numcodes = query.list().size();
		
		if (numcodes == 0)
		{
			add(uniqueCode, survey);	
		} else {	
			query = session.createQuery("UPDATE ValidCode SET created = :created WHERE code = :code").setString("code", uniqueCode).setTimestamp("created", new Date());
			query.executeUpdate();
		}
	}
	
	@Transactional
	public void invalidate(String uniqueCode)
	{
		Session session = sessionFactory.getCurrentSession();	
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -2);
		Query query = session.createQuery("UPDATE ValidCode SET created = :created WHERE code = :code").setString("code", uniqueCode).setTimestamp("created", cal.getTime());
		query.executeUpdate();		
	}

	@Transactional
	public void removeOldCodes() {
		Session session = sessionFactory.getCurrentSession();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Query query = session.createQuery("DELETE FROM ValidCode WHERE created < :lastmonth").setDate("lastmonth", cal.getTime());
		query.executeUpdate();
	}
}
