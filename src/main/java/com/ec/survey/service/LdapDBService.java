package com.ec.survey.service;

import com.ec.survey.model.Department;
import com.ec.survey.model.DepartmentItem;
import com.ec.survey.model.Domain;
import com.ec.survey.model.KeyValue;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
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
		
		SQLQuery query = session.createSQLQuery("SELECT DISTINCT DOMAIN_CODE domainCode, NAME name FROM DEPARTMENTS");
		@SuppressWarnings("unchecked")
		List<DepartmentItem> existingDepartments = query.setResultTransformer(Transformers.aliasToBean(DepartmentItem.class)) .list();
				
		logger.debug("departments retrieved");
		
		//create new departments
		for (DepartmentItem department: departments)
		{
			if (!existingDepartments.contains(department))
			{
				session.save(new Department(department.getName(),department.getDomainCode()));
			}
		}
		
		logger.debug("new departments saved");
		
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
		
		logger.debug("old departments deleted");
	}
	
	@Transactional(timeout=3000)
	public void reloadDomains(Map<String,String> ldapDomains) {
		Session session = sessionFactory.getCurrentSession();	
		
		logger.info("reload Domains started");
		
		Query query = session.createQuery("FROM Domain d");	

		@SuppressWarnings("unchecked")
		List<Domain> dbDomains = query.list();
		
		Map<String, Domain> dbDomainCodes = new HashMap<>();
		for (Domain domain : dbDomains)
		{
			dbDomainCodes.put(domain.getCode(), domain);			
		}			
		
		//create new departments
		for ( Entry<String, String> ldapDomain : ldapDomains.entrySet()) {
			
			if (!dbDomainCodes.containsKey(ldapDomain.getKey()))
			{
				session.save(new Domain(ldapDomain.getKey(),ldapDomain.getValue()));
			} else if (!dbDomainCodes.get(ldapDomain.getKey()).getDescription().equals(ldapDomain.getValue()))
			{
				Domain domain = dbDomainCodes.get(ldapDomain.getKey());
				domain.setDescription(ldapDomain.getValue());
				session.saveOrUpdate(domain);
			}
		}
		
		logger.info("new domains saved");
		
		//remove domains that don't exist anymore
		for (String domain: dbDomainCodes.keySet())
		{
			if (!ldapDomains.containsKey(domain))
			{
				query = session.createQuery("delete from Domain d where d.code = :code");
				query.setString("code", domain);
				query.executeUpdate();
			}
		}
		
		logger.info("old domains deleted");
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
	public Map<String,EcasUser> getAllECASLogins() {
		Session session = sessionFactory.getCurrentSession();
		
		String hql = "SELECT u.name, u.id, u.deactivated FROM EcasUser u";
		
		Query query = session.createQuery(hql);	
		
		@SuppressWarnings("rawtypes")
		List list = query.list();
		
		Map<String,EcasUser> result = new HashMap<>(list.size());
		
		for (Object o: list)
		{
			EcasUser user = new EcasUser();
			Object[] a = (Object[]) o;
			user.setName(((String) a[0]).trim());
			user.setId(ConversionTools.getValue(a[1]));
			user.setDeactivated((Boolean) a[2]);
			result.put(((String) a[0]).trim(), user);	
		}
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<EcasUser> getECASUsers(String name, String department, String email, String domain, int page, int rowsPerPage) {
		Session session = sessionFactory.getCurrentSession();
		
		String hql = "SELECT DISTINCT u FROM EcasUser as u WHERE (u.deactivated IS NULL OR u.deactivated = false)";
				
		if (name != null && name.length() > 0)
		{
			hql += " AND CONCAT(u.givenName,' ', u.surname) LIKE :name";			
		}
		
		if (email != null && email.length() > 0)
		{
			hql += " AND u.email LIKE :email";			
		}
		
		if (department != null && department.length() > 0 && !department.equalsIgnoreCase("undefined"))
		{
			hql += " AND u.departmentNumber LIKE :department";			
		}
		
		if (domain != null && domain.length() > 0)
		{
			hql += " AND u.organisation LIKE :domain";	
		}
			
		hql += " ORDER BY u.id ASC";
		
		Query query = session.createQuery(hql);
		if (name != null && name.length() > 0)
		{
			query.setString("name", "%" + name + "%");
		}
		if (email != null && email.length() > 0)
		{
			query.setString(Constants.EMAIL, "%" + email + "%");
		}
		if (department != null && department.length() > 0 && !department.equalsIgnoreCase("undefined"))
		{
			query.setString("department", "%" + department + "%");
		}
		if (domain != null && domain.length() > 0)
		{
			query.setString("domain", domain);
		}
	
		@SuppressWarnings("unchecked")
		List<EcasUser> res = query.setFirstResult((page - 1)*rowsPerPage).setMaxResults(rowsPerPage).list();
				
		return res;
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
	@Transactional(readOnly = true)
	public List<KeyValue> getDomains(boolean includeExternal , boolean inludeSystem, MessageSource resources, Locale locale) {
		
		Session session = sessionFactory.getCurrentSession();		
		Query query =session.createQuery( "select  d.code as key , d.description as value From Domain d order by  d.description ");
		@SuppressWarnings("unchecked")
		List<KeyValue> domainsList = query.setResultTransformer( Transformers.aliasToBean(KeyValue.class)).list() ;
		if (!includeExternal) {
			KeyValue external =  new KeyValue("external" ,"External");
			domainsList.remove(external);
		}
		if (inludeSystem){
			KeyValue system =  new KeyValue("system" ,"System");
			domainsList.add(system);
		}
		
		for (KeyValue kv : domainsList)
		{
			kv.setValue(resources.getMessage("domain." + kv.getKey(), null, kv.getValue(), locale));
		}
		
    	return domainsList;
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
