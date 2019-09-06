package com.ec.survey.service;

import com.ec.survey.model.Department;
import com.ec.survey.model.DepartmentItem;
import com.ec.survey.model.Domain;
import com.ec.survey.model.KeyValue;
import com.ec.survey.model.administration.EcasUser;
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
		
		Map<String, Domain> dbDomainCodes = new HashMap<String, Domain>();
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
			result.put(((String) a[0]).trim(), user); //ConversionTools.getValue(a[1]));			
		}
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public String[] getECASLogins(String name, String department, String type, String first, String last, String email, String order) {
		Session session = sessionFactory.getCurrentSession();
		
		String sql = "SELECT u.USER_LOGIN, u.USER_ECMONIKER, u.USER_ORGANISATION, u.USER_DEPARTMENT, u.USER_GN, u.USER_SN, u.USER_EMAIL FROM ECASUSERS u WHERE (u.USER_DEACTIVATED IS NULL OR u.USER_DEACTIVATED = false)";
				
		if (name != null && name.length() > 0)
		{
			sql += " AND u.USER_ECMONIKER LIKE :name";			
		}
		
		if (first != null && first.length() > 0)
		{
			sql += " AND u.USER_GN LIKE :first";			
		}
		
		if (last != null && last.length() > 0)
		{
			sql += " AND u.USER_SN LIKE :last";			
		}
		
		if (email != null && email.length() > 0)
		{
			sql += " AND u.USER_EMAIL LIKE :email";			
		}
		
		if (department != null && department.length() > 0 && !department.equalsIgnoreCase("undefined"))
		{
			sql += " AND u.USER_ID IN (SELECT eg_id FROM ECASGROUPS WHERE GRPS LIKE :department)";			
		}
		
		if (type != null && type.length() > 0 )
		{
			sql += " AND u.USER_ORGANISATION= :organisation AND u.USER_EMPLOYEETYPE != 'g'"; 			
		}
		
		if (order.equalsIgnoreCase("first"))
		{
			sql +=  " ORDER BY u.USER_GN ASC";
		} else if (order.equalsIgnoreCase("last"))
		{
			sql +=  " ORDER BY u.USER_SN ASC";
		} else if (order.equalsIgnoreCase("department"))
		{
			sql +=  " ORDER BY u.USER_DEPARTMENT ASC";
		} else {		
			sql +=  " ORDER BY u.USER_ECMONIKER ASC";
		}
		
		Query query = session.createSQLQuery(sql);
		if (name != null && name.length() > 0)
		{
			query.setString("name", "%" + name + "%");
		}
		if (first != null && first.length() > 0)
		{
			query.setString("first", "%" + first + "%");
		}
		if (last != null && last.length() > 0)
		{
			query.setString("last", "%" + last + "%");
		}
		if (email != null && email.length() > 0)
		{
			query.setString("email", "%" + email + "%");
		}
		if (department != null && department.length() > 0 && !department.equalsIgnoreCase("undefined"))
		{
			query.setString("department", "%" + department + "%");
		}
		if (type != null && type.length() > 0 )
		{
			query.setString("organisation", type);
		} 
		
		@SuppressWarnings("rawtypes")
		List res = query.setMaxResults(100).list();
			
		List<String> result = new ArrayList<>();
	
		for (Object o: res)
		{
			Object[] a = (Object[]) o;
			String login = (String)a[0];
			String displayName = (String)a[1];
			String organisation = (String)a[2];
			String group = (String) a[3];
			String fname = (String) a[4];
			String lname = (String) a[5];
			
			if (displayName == null || displayName.length() == 0) displayName = login;
			
			if (organisation.equalsIgnoreCase("external"))
			{
				displayName += " (EXT)";				
			} else {
				displayName += " (" +  organisation.replace("eu.europa.", "").toUpperCase() + ")"   ;	
			}
			
			if (group == null || group.equals("null")) group = "";
			
			result.add("<tr id='" + login + "'><td>" + displayName + "</td><td>" + fname + "</td><td>" + lname + "</td><td>" + group + "</td></tr>");			
		}
		
		return result.toArray(new String[result.size()]);
	}

	@Transactional(readOnly = true)
	public String[] getDepartments(String domain , String term, boolean prefix, boolean removeTerm) {
		Session session = sessionFactory.getCurrentSession();		
		Query query;
		
		if (term != null && !prefix)
		{
			query = session.createQuery("SELECT DISTINCT d.name FROM Department d WHERE d.name like :name ORDER BY d.name ASC").setString("name", term);
		} else if (term != null && prefix)
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
