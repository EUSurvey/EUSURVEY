package com.ec.survey.service;

import com.ec.survey.model.ValidCode;
import com.ec.survey.model.survey.Survey;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("validCodesService")
public class ValidCodesService extends BasicService {

	@Transactional(readOnly = true)
	public List<ValidCode> getAll() {
		Session session = sessionFactory.getCurrentSession();
		Query<ValidCode> query = session.createQuery("FROM ValidCode", ValidCode.class);
		return query.list();
	}
	
	@Transactional(readOnly = true)
	public ValidCode get( Integer id ) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(ValidCode.class, id);
	}
	
	@Transactional
	public void add(String uniqueCode, Survey survey) {
		if (checkValid(uniqueCode, survey.getUniqueId())) {
			return;
		}		
		
		Session session = sessionFactory.getCurrentSession();
		session.evict(survey);
		ValidCode validCode = new ValidCode(uniqueCode, survey.getUniqueId());
		session.save(validCode);
	}

	@Transactional
	public void delete(String uniqueCode) {
		Session session = sessionFactory.getCurrentSession();	
		@SuppressWarnings("unchecked")
		Query<ValidCode> query = session.createQuery("DELETE FROM ValidCode WHERE code = :code").setParameter("code", uniqueCode);
		query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public boolean checkValid(String uniqueCode, String surveyUid) {
		Session session = sessionFactory.getCurrentSession();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Query<ValidCode> query = session.createQuery("FROM ValidCode WHERE code = :code AND (surveyUid is null OR surveyUid = :surveyUid) AND created  > :yesterday", ValidCode.class).setParameter("surveyUid", surveyUid).setParameter("code", uniqueCode).setParameter("yesterday", cal.getTime());
		return query.list().size() == 1;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public void revalidate(String uniqueCode, Survey survey)
	{
		Session session = sessionFactory.getCurrentSession();	
		session.evict(survey);
		Query<ValidCode> query = session.createQuery("FROM ValidCode WHERE code = :code AND (surveyUid is null OR surveyUid = :surveyUid)", ValidCode.class).setParameter("surveyUid", survey.getUniqueId()).setParameter("code", uniqueCode);
		int numcodes = query.list().size();
		
		if (numcodes == 0)
		{
			this.add(uniqueCode, survey);	
		} else {	
			query = session.createQuery("UPDATE ValidCode SET created = :created WHERE code = :code").setParameter("code", uniqueCode).setParameter("created", new Date());
			query.executeUpdate();
		}
		session.flush();
	}
	
	@Transactional
	public void invalidate(String uniqueCode)
	{
		Session session = sessionFactory.getCurrentSession();	
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -2);
		@SuppressWarnings("unchecked")
		Query<ValidCode> query = session.createQuery("UPDATE ValidCode SET created = :created WHERE code = :code").setParameter("code", uniqueCode).setParameter("created", cal.getTime());
		query.executeUpdate();		
	}

	@Transactional
	public void removeOldCodes() {
		Session session = sessionFactory.getCurrentSession();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		@SuppressWarnings("unchecked")
		Query<ValidCode> query = session.createQuery("DELETE FROM ValidCode WHERE created < :lastmonth").setParameter("lastmonth", cal.getTime());
		query.executeUpdate();
	}
}
