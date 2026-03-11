package com.ec.survey.service;

import com.ec.survey.model.Department;
import com.ec.survey.model.DepartmentItem;
import com.ec.survey.model.Domain;
import com.ec.survey.model.KeyValue;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import org.hibernate.query.Query;
import org.hibernate.query.NativeQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;

@Service("ldapDBService")
public class LdapDBService extends BasicService {
    
	@Transactional
	public void add(Department department) {
		Session session = sessionFactory.getCurrentSession();		
		session.save(department);
	}
	
	@Transactional(timeout=3000)
	public void reload(Set<DepartmentItem> departments) {
		Session session = sessionFactory.getCurrentSession();	
		
		logger.info("reload started");		
		
		NativeQuery query = session.createSQLQuery("SELECT DISTINCT DOMAIN_CODE domainCode, NAME name FROM DEPARTMENTS");
		@SuppressWarnings("unchecked")
		List<DepartmentItem> existingDepartments = query.setResultTransformer(Transformers.aliasToBean(DepartmentItem.class)) .list();
				
		//create new departments
		for (DepartmentItem department: departments)
		{
			if (!existingDepartments.contains(department))
			{
				session.save(new Department(department.getName(),department.getDomainCode()));
			}
		}
		
		Query deleteQuery = session.createQuery("delete from Department d where d.domainCode = :domainCode and d.name = :department");
		
		//remove departments that don't exist anymore
		for (DepartmentItem department: existingDepartments)
		{
			if (!departments.contains(department))
			{
				
				deleteQuery.setString("department", department.getName());
				deleteQuery.setString("domainCode", department.getDomainCode());
				deleteQuery.executeUpdate();
			}
		}
	}
	
	@Transactional(readOnly = true)
	public String[] getECASLoginsForPrefix(String term) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM EcasUser d where d.name like :term and d.deactivated != true ORDER by d.name").setString("term", "%" + term + "%");
		@SuppressWarnings("unchecked")
		List<EcasUser> list = query.setMaxResults(100).list();
		String[] result = new String[list.size()];
		int counter = 0;
		for (EcasUser u : list) {
			result[counter++] = u.getName();
		}
		return result;
	}

	@Transactional(readOnly = true)
	public List<EcasUser> getExclusiveECASVoteUsersWithIds(String surveyUid, Collection<Integer> ids) {
		Session session = sessionFactory.getCurrentSession();

		//Select all users whose id is in the collection
		String hql = "SELECT DISTINCT u FROM EcasUser as u WHERE (u.deactivated IS NULL OR u.deactivated = false) AND u.id in :ids";

		//Where a voter with this ecMoniker does not already exist.
		//This must be checked because the ecMoniker is constrained unique
		//Which means all users which already are a voter for this survey are ignored
		hql += " AND u.ecMoniker NOT IN (SELECT v.ecMoniker from Voter as v WHERE v.surveyUid = :surveyUid)";

		Query<EcasUser> query = session.createQuery(hql, EcasUser.class);
		query.setParameter("ids", ids);
		query.setParameter("surveyUid", surveyUid);

		return query.list();
	}

	@Transactional(readOnly = true)
	public String[] getDepartments(String domain , String term, boolean prefix, boolean removeTerm) {
		Session session = sessionFactory.getCurrentSession();		
		Query query;
		
		if (term != null && !prefix)
		{
			query = session.createQuery("SELECT DISTINCT d.name FROM Department d WHERE d.name like :name ORDER BY d.name ASC").setString("name", term);
		} else if (term != null)
		{
			query = session.createQuery("SELECT DISTINCT d.name FROM Department d WHERE d.name like :nameDot ORDER BY d.name ASC").setString("nameDot", term + ".%");
		} else {
			query = session.createQuery("SELECT DISTINCT d.name FROM Department d WHERE d.domainCode = :domainCode  ORDER BY d.name ASC").setString("domainCode", domain) ;	
		}
		
		@SuppressWarnings("unchecked")
		List<String> list = query.list();
		
		if (removeTerm && list.contains(term))
		{
			list.remove(term);
		}
		
		return list.toArray(new String[list.size()]);
		
	}

	@Transactional
	public void UpdateDomains(TreeMap<String, String> domains) {
		Session session = sessionFactory.getCurrentSession();

		if (domains == null || domains.isEmpty()) {
			return;
		}

		Query query = session.createQuery("delete from Domain d where d.id > 0");
		query.executeUpdate();

		for (String code : domains.keySet()) {
			Domain d = new Domain(code, domains.get(code));
			session.save(d);
		}
	}

	@Transactional(readOnly = true)
	public List<KeyValue> getDomains(boolean includeExternal, boolean inludeSystem, MessageSource resources, Locale locale) {
		
		Session session = sessionFactory.getCurrentSession();		
		Query query = session.createQuery( "From Domain order by description ");

		List<Domain> domains = query.list() ;

		List<KeyValue> result = new ArrayList<>();
		for (Domain domain : domains) {
			result.add(new KeyValue(domain.getCode(), domain.getDescription()));
		}

		if (includeExternal) {
			KeyValue external =  new KeyValue("external" ,"External");
			result.add(external);
		}
		if (inludeSystem){
			KeyValue system =  new KeyValue("system" ,"System");
			result.add(system);
		}

		if (!locale.equals(Locale.ENGLISH)) {
			for (KeyValue kv : result) {
				kv.setValue(resources.getMessage("domain." + kv.getKey(), null, kv.getValue(), locale));
			}
		}
		
		// this creates at least the EC entry if COMREF synchronization does not work
		if (result.size() == 1) {
			KeyValue ec =  new KeyValue("eu.europa.ec" ,"European Commission");
			result.add(ec);
		}
		
    	return result;
	}
	
	@Transactional(readOnly = true)
	public List<String> getDomainKeySufixes() {
		
		Session session = sessionFactory.getCurrentSession();		
		Query query = session.createSQLQuery( "select REPLACE(DOMAINS.CODE ,'eu.europa.','') code_sufix  from DOMAINS ");
		@SuppressWarnings("unchecked")
		List<String> domainKeysList = query.list() ;
		domainKeysList.add("system");
    	return domainKeysList;
	}
}
