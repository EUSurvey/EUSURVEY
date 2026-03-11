package com.ec.survey.service;

import com.ec.survey.model.LdapSearchResult;
import com.ec.survey.tools.DepartmentUpdater;
import com.ec.survey.tools.EcasHelper;
import com.ec.survey.tools.Tools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;

@Service("ldapService")
public class LdapService extends BasicService {

    public static final String LDAP_CONSTANT_PREFIX = "$";
    
    @Resource(name="ldapDBService")
	private LdapDBService ldapDBService;
    
    @Autowired
	@Qualifier("departmentWorker")
	private DepartmentUpdater departmentWorker;

    private static final Logger logger = Logger.getLogger(LdapService.class);
    
    private @Value("${LdapUrl:}") String url;
    private @Value("${LdapContextFactory:}") String contextFactory;
    private @Value("${LdapSecurityPrincipal:}") String securityPrincipal;
    private @Value("${LdapSecurityCredentials:}") String securityCredentials;
    private @Value("${LdapSecurityAuthentication:}") String securityAuthentication;
    private @Value("${ldap.search.user.format:uid\\=%s, ou\\=People}") String ldapSearchUserFormat;
    private @Value("${ldap.search.mail.format:mail\\=%s, ou\\=People}") String ldapSearchMailFormat;
    private @Value("${ldap.search.format:ou\\=People}") String ldapSearchFormat;
   
    private @Value("${ldap.mapping.user.departmentNumber:departmentNumber}") String ldapMappingUserDepartmentNumber;
    private @Value("${ldap.mapping.user.sn:sn:sn}") String ldapMappingUserSn;
    private @Value("${ldap.mapping.user.uid:uid}") String ldapMappingUserUid;
    private @Value("${ldap.mapping.user.ecMoniker:ecMoniker}") String ldapMappingUserEcMoniker;
    private @Value("${ldap.mapping.user.o:o}") String ldapMappingUserO;
    private @Value("${ldap.mapping.user.dg:dg}") String ldapMappingUserDg;
    private @Value("${ldap.mapping.user.givenName:givenName}") String ldapMappingUserGivenName;
    private @Value("${ldap.mapping.user.mail:mail}") String ldapMappingUserMail;
    private @Value("${ldap.mapping.user.telephoneNumber:telephoneNumber}") String ldapMappingUserTelephoneNumber;
    private @Value("${ldap.mapping.user.employeeType:employeeType}") String ldapMappingUserEmployeeType;
    private @Value("${ldap.mapping.user.recordStatus:recordStatus}") String ldapMappingUserRecordStatus;
    private @Value("${ldap.mapping.user.modifyTimstamp:modifyTimstamp}") String ldapMappingUserModifyTimstamp;
    
    private @Value("${ldap.mapping.domain.o:o}") String ldapMappingDomainO;
    private @Value("${ldap.mapping.domain.description:description}") String ldapMappingDomainDescription;
    private @Value("${ldap.search.domains.format:@null}") String ldapSearchDomainFormat;
        
    public @Value("${casoss:false}") String cassOss;

	public boolean isCasOss()
	{
		return cassOss != null && cassOss.equalsIgnoreCase("true");
	}
   
    private DirContext initialize() throws NamingException {

    	if (securityAuthentication == null || securityAuthentication.length() == 0) securityAuthentication = "simple";
    	
    	// Create a Directory Context
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
        env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
            
        return new InitialDirContext(env);
    }
      
    public String getEmail(String userName) throws NamingException {
    	
    	userName = Tools.encodeForLDAP(userName);
    	
        String email = "";
        DirContext ctx = initialize();
        try {
        	String searchValue= String.format(ldapSearchUserFormat, userName);
            Attributes attrs = ctx.getAttributes(searchValue);
            email = (String) attrs.get("mail").get();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        
        ctx.close();
        return email;
    }
    
	public String getMoniker(String login) throws NamingException {
		String moniker = "";
		DirContext ctx = initialize();
        try {
        	login = Tools.encodeForLDAP(login);
        	String searchValue= String.format(ldapSearchUserFormat, login);
            Attributes attrs = ctx.getAttributes(searchValue);
            moniker = (String) attrs.get(ldapMappingUserEcMoniker).get();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
	    ctx.close();
        return moniker;
	}
	
	public String getLoginForEmail(String email) throws NamingException {
		String login = "";
		DirContext ctx = initialize();		
		
		SearchControls sc = getSearchControls(LdapSearchTypeEnum.MAIL);
		NamingEnumeration<SearchResult> ne = null; 
		Attributes userAttributes;
		String searchString = "(mail=" + Tools.encodeForLDAP(email) + ")";
		
		try{
			ne = ctx.search(ldapSearchFormat, searchString, sc);
			
			while(ne.hasMore()){
				SearchResult nextSearchResult = ne.next();  
				userAttributes = nextSearchResult.getAttributes();
				login = (String) userAttributes.get(ldapMappingUserUid).get();
			}
		
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}	
	 
	    ctx.close();
        return login;
	}

	public List<String> getOrganisationForEmail(String email) throws NamingException {
		DirContext ctx = initialize();

		SearchControls sc = getSearchControls(LdapSearchTypeEnum.MAIL);
		NamingEnumeration<SearchResult> ne = null;
		Attributes userAttributes;
		String searchString = "(mail=" + Tools.encodeForLDAP(email) + ")";

		List<String> organisations = new ArrayList<>();

		try{
			ne = ctx.search(ldapSearchFormat, searchString, sc);

			while(ne.hasMore()){
				SearchResult nextSearchResult = ne.next();
				userAttributes = nextSearchResult.getAttributes();

				if (userAttributes.get(ldapMappingUserO) != null) {
					String organisation = (String) userAttributes.get(ldapMappingUserO).get();

					if (organisation.equalsIgnoreCase("eu.europa.ec") && (userAttributes.get(ldapMappingUserDg == null ? "dg" : ldapMappingUserDg) != null)) {
						String dg = (String) userAttributes.get(ldapMappingUserDg == null ? "dg" : ldapMappingUserDg).get();
						organisations.add(dg);
					} else if (userAttributes.get(ldapMappingUserDepartmentNumber == null ? "departmentNumber" : ldapMappingUserDepartmentNumber) != null) {
						String departmentNumber = (String) userAttributes.get(ldapMappingUserDepartmentNumber == null ? "departmentNumber" : ldapMappingUserDepartmentNumber).get();
						if (departmentNumber != null && departmentNumber.contains(".")) {
							organisations.add(departmentNumber.substring(0, departmentNumber.indexOf(".")));
						} else if (departmentNumber != null) {
							organisations.add(departmentNumber);
						}
					} else {
						organisations.add(organisation);
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		ctx.close();
		return organisations;
	}
   
    public List<String> getUserLDAPGroups(String username) {

        List<String> groups = new ArrayList<>();
        try {
        	DirContext ctx = initialize();
            username = Tools.encodeForLDAP(username);
            String searchValue= String.format(ldapSearchUserFormat, username);
            Attributes attrs = ctx.getAttributes(searchValue);
            
            // get the attributes
            String department="";
            
            if (isAttributeEligible(ldapMappingUserDepartmentNumber)){
            	department= getAttributeValue(attrs, ldapMappingUserDepartmentNumber,true);
            }else if(!StringUtils.isEmpty(ldapMappingUserDepartmentNumber) && ldapMappingUserDepartmentNumber.startsWith(LDAP_CONSTANT_PREFIX)){
            	department=ldapMappingUserDepartmentNumber.replace(LDAP_CONSTANT_PREFIX, "");
            }
            	            	            
            String employeeType="";
            if (!isCasOss())
            	employeeType = getAttributeValue(attrs, ldapMappingUserEmployeeType,true);
            
            String o ="";
            if(isAttributeEligible(ldapMappingUserO)){
            	o=getAttributeValue(attrs, "o",false); 
            }else if(!StringUtils.isEmpty(ldapMappingUserO)&& ldapMappingUserO.startsWith(LDAP_CONSTANT_PREFIX)){
            	o= ldapMappingUserO.replace(LDAP_CONSTANT_PREFIX, "");
            }
            
            if (o != null && !o.isEmpty()) {
            	groups.add(o.replace("eu.europa.", ""));
            }
            if (department != null) {
                String[] tabDep = department.split("\\.");
                if (tabDep[0].equals("ECA"))
                	groups.add("ecaroot");
                else
                	groups.add("ALL-COM");
                
                groups.add(tabDep[0]);
                
                String prefix = tabDep[0];
                for (int i = 1; i < tabDep.length; i++)
                {
                	prefix += "." + tabDep[i];
                	groups.add(prefix);
                }
                
            } else if (Objects.equals(employeeType, "f") || Objects.equals(employeeType, "x") || Objects.equals(employeeType, "i") || Objects.equals(employeeType, "xf") || Objects.equals(employeeType, "q")) {
                //internal                
            } else {
            	groups.add("external");
            }
            
            ctx.close();
           
        } catch (javax.naming.NameNotFoundException e1)
        {
        	//ignore, this just means the user was not found
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return groups;
    }
    
    private static String getAttributeValue(Attributes attrs, String name , boolean convertToUpper ) throws NamingException {
    	
    	name = Tools.encodeForLDAP(name);
        	
        Attribute att = attrs.get(name);
        String value = null;
        if (att != null) {
            value = ((String) att.get());
            value = value == null ? null : (convertToUpper ?  value.toUpperCase() : value );
        }
        return value;
    }

	public String[] getECASLogins(String name, String department, String type, String first, String last, String email, String order, int limit) throws NamingException {
		
		name = Tools.encodeForLDAP(name);
		department = Tools.encodeForLDAP(department);
		type = Tools.encodeForLDAP(type);
		first = Tools.encodeForLDAP(first);
		last = Tools.encodeForLDAP(last);
		email = Tools.encodeForLDAP(email);		
		
		List<LdapSearchResult> ldqpUsers = new ArrayList<>();
		
		DirContext ctx = initialize();
		
		try {
			SearchControls sc = getSearchControls(LdapSearchTypeEnum.LOGIN);
			
			sc.setCountLimit(limit);
			sc.setTimeLimit(60000);
			
			NamingEnumeration<SearchResult> ne = null; 
			Attributes set_att;

			String searchString = "(& (objectClass=*)";
						
			if (name != null && name.length() > 0){
				searchString += getFilterContainsOr("uid", ldapMappingUserEcMoniker, name);			
			}
			
			if (isAttributeEligible(ldapMappingUserGivenName) && first != null && first.length() > 0){
				searchString += getFilterContains(ldapMappingUserGivenName, first);			
			}
			
			if (isAttributeEligible(ldapMappingUserSn) && last != null && last.length() > 0){
				searchString += getFilterContains(ldapMappingUserSn, last);
			}
			
			if (isAttributeEligible(ldapMappingUserMail) && email != null && email.length() > 0){
				searchString += getFilterContains(ldapMappingUserMail, email);			
			}			
			
			if (isAttributeEligible(ldapMappingUserDepartmentNumber) && department != null && department.length() > 0 && !department.equalsIgnoreCase("undefined")){
				searchString += getFilterStartsWith(ldapMappingUserDepartmentNumber, department);			
			}			

			if(isAttributeEligible(ldapMappingUserO) && type != null && type.length() > 0){
				searchString += getFilterEquals(ldapMappingUserO, type); 			
			}
			
			if(!isCasOss()){
				searchString += getFilterNotEquals("recordStatus", "d");			
				searchString += getFilterNotEquals("employeeType", "g");			
			}			
			
			searchString += " )";
			
			try{
				// ou=People
				ne = ctx.search(ldapSearchFormat,searchString,sc);
				
				while(ne.hasMore()){
					SearchResult sr = ne.next();  
					set_att = sr.getAttributes();					

					String login = getAttributeValue(set_att, "uid",false) ;
					String displayName="";
					if(set_att.get(ldapMappingUserEcMoniker)!=null) {
						displayName = getAttributeValue(set_att, ldapMappingUserEcMoniker,false) ;
					}
					String organisation="";
					if(set_att.get(ldapMappingUserO)!=null) {
						organisation = getAttributeValue(set_att, ldapMappingUserO,false) ;
					}
					String group="";
					if(set_att.get(ldapMappingUserDepartmentNumber)!=null) {
						group = getAttributeValue(set_att, ldapMappingUserDepartmentNumber,false) ;
					}
					String fname="";
					if(set_att.get(ldapMappingUserGivenName)!=null)
					{
						fname = getAttributeValue(set_att, ldapMappingUserGivenName,false) ;
					}
					String lname="";
					if(set_att.get(ldapMappingUserSn)!=null)
					{
						lname = getAttributeValue(set_att, ldapMappingUserSn,false) ;
					}
					String mail = "";
					if (set_att.get(ldapMappingUserMail) != null) {
						mail = getAttributeValue(set_att, ldapMappingUserMail, false);
					}

					String employeeType = "";
					if (set_att.get(ldapMappingUserEmployeeType) != null) {
						employeeType = getAttributeValue(set_att, ldapMappingUserEmployeeType, false);
					}
					ldqpUsers.add(new LdapSearchResult(login, displayName, organisation, group, fname, lname, mail, !EcasHelper.isEmployeeTypePrivileged(employeeType)));
				}
			} catch (javax.naming.SizeLimitExceededException se) {
				//this one is thrown when the configured limit is reached, so everything is as expected
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}	
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}		

		if (order.equalsIgnoreCase("first"))
		{
			ldqpUsers.sort(LdapSearchResult.Comparators.FIRST);
		} else if (order.equalsIgnoreCase("last"))
		{
			ldqpUsers.sort(LdapSearchResult.Comparators.LAST);
		} else if (order.equalsIgnoreCase("department"))
		{
			ldqpUsers.sort(LdapSearchResult.Comparators.GROUP);
		} else if (order.equalsIgnoreCase("mail")) {
			ldqpUsers.sort(LdapSearchResult.Comparators.MAIL);
		} else {
			ldqpUsers.sort(LdapSearchResult.Comparators.DISPLAYNAME);
		}		
		
		List<String> resultString = new ArrayList<>(ldqpUsers.size());
		
		for (LdapSearchResult ldapSearchResult : ldqpUsers) {
			String displayName =ldapSearchResult.getDisplayName() ;
			String login = ldapSearchResult.getLogin();
			String organisation = ldapSearchResult.getOrganisation();
			String group=  ldapSearchResult.getGroup();
			String fname =  ldapSearchResult.getFname();
			String lname =  ldapSearchResult.getLname();
			String mail = ldapSearchResult.getMail();
			boolean external = ldapSearchResult.getIsExternal();
			
			if ( displayName == null || displayName.length() == 0) displayName = login;
			
			if (organisation.equalsIgnoreCase("external"))
			{
				displayName += " (EXT)";				
			} else {
				displayName += " (" +  organisation.replace("eu.europa.", "").toUpperCase() + ")"   ;	
			}
			
			if (group == null || group.equals("null")) group = "";
			
			resultString.add("<tr id='" + login + "' " + (external ? "class='externaluser'" : "") + "><td>" + mail + "</td><td>" + displayName + "</td><td>" + fname + "</td><td>" + lname + "</td><td>" + group + "</td></tr>");
		}		
		
		ctx.close();
		
		return resultString.toArray(new String[resultString.size()]);
	}
	
	private String getFilterContains(final String key, final String search) {
		String result="";
		if(search != null && search.length() != 0) {
				result = "(" + key + "=*" + search + "*)";
		} else {
			result = "(" + key + "=*)";
		}
		return result;
	}
	
	private String getFilterContainsOr(final String key, final String key2, final String search) {
		String result = "(|(" + key + "=*" + search + "*)(" + key2 + "=*" + search + "*))";		
		return result;
	}
	
	private String getFilterStartsWith(final String key, final String search) {
		String result="";
		if(search != null && search.length() != 0) {
				result = "(" + key + "=" + search + "*)";
		} else {
			result = "(" + key + "=*)";
		}
		return result;
	}
	
	private String getFilterEquals(final String key, final String search) {
		String result="";
		if(search != null && search.length() != 0) {
				result = "(" + key + "=" + search + ")";
		} 
		return result;
	}
	
	
	private String getFilterNotEquals(final String key, final String search) {
		String result="";
		if(search != null && search.length() != 0) {
				result = "(!(" + key + "=" + search + "))";
		} 
		return result;
	}
		
	
	private SearchControls getSearchControls(LdapSearchTypeEnum typeSearch){
		List<String> lstAttr= new ArrayList<>();
		
		switch (typeSearch) {
		case LOGIN:
			if (isAttributeEligible(ldapMappingUserUid))
				lstAttr.add(ldapMappingUserUid);
			if (isAttributeEligible(ldapMappingUserEcMoniker))
				lstAttr.add(ldapMappingUserEcMoniker);			
			if (isAttributeEligible(ldapMappingUserDepartmentNumber))
				lstAttr.add(ldapMappingUserDepartmentNumber);
			if (isAttributeEligible(ldapMappingUserO))
				lstAttr.add(ldapMappingUserO);
			if (isAttributeEligible(ldapMappingUserSn))
				lstAttr.add(ldapMappingUserSn);
			if (isAttributeEligible(ldapMappingUserGivenName))
				lstAttr.add(ldapMappingUserGivenName);
			if (isAttributeEligible(ldapMappingUserMail))
				lstAttr.add(ldapMappingUserMail);
			break;
		case USERNAME:
			if (isAttributeEligible(ldapMappingUserUid))
				lstAttr.add(ldapMappingUserUid);
			if (isAttributeEligible(ldapMappingUserRecordStatus))
				lstAttr.add(ldapMappingUserRecordStatus);
			break;
		case DOMAIN:
			if (isAttributeEligible(ldapMappingDomainDescription))
				lstAttr.add(ldapMappingDomainDescription);
			if (isAttributeEligible(ldapMappingUserO))
				lstAttr.add(ldapMappingUserO);
			break;
		case DEPARTMENT:
			if (isAttributeEligible(ldapMappingUserDepartmentNumber))
				lstAttr.add(ldapMappingUserDepartmentNumber);
			if (isAttributeEligible(ldapMappingUserO))
				lstAttr.add(ldapMappingUserO);
			break;
		case USER:
			if (isAttributeEligible(ldapMappingUserUid))
				lstAttr.add(ldapMappingUserUid);
			if (isAttributeEligible(ldapMappingUserMail))
				lstAttr.add(ldapMappingUserMail);
			if (isAttributeEligible(ldapMappingUserGivenName))
				lstAttr.add(ldapMappingUserGivenName);
			if (isAttributeEligible(ldapMappingUserSn))
				lstAttr.add(ldapMappingUserSn);
			if (isAttributeEligible(ldapMappingUserDepartmentNumber))
				lstAttr.add(ldapMappingUserDepartmentNumber);
			if (isAttributeEligible(ldapMappingUserTelephoneNumber))
				lstAttr.add(ldapMappingUserTelephoneNumber);
			if (isAttributeEligible(ldapMappingUserEcMoniker))
				lstAttr.add(ldapMappingUserEcMoniker);
			if (isAttributeEligible(ldapMappingUserEmployeeType))
				lstAttr.add(ldapMappingUserEmployeeType);
			if (isAttributeEligible(ldapMappingUserO))
				lstAttr.add(ldapMappingUserO);
			if (isAttributeEligible(ldapMappingUserRecordStatus))
				lstAttr.add(ldapMappingUserRecordStatus);
			if (isAttributeEligible(ldapMappingUserModifyTimstamp))
				lstAttr.add(ldapMappingUserModifyTimstamp);
			break;
		case MAIL:
			if (isAttributeEligible(ldapMappingUserUid))
				lstAttr.add(ldapMappingUserUid);
			if (isAttributeEligible(ldapMappingUserO))
				lstAttr.add(ldapMappingUserO);
			if (isAttributeEligible(ldapMappingUserDg == null ? "dg" : ldapMappingUserDg))
				lstAttr.add(ldapMappingUserDg == null ? "dg" : ldapMappingUserDg);
			if (isAttributeEligible(ldapMappingUserDepartmentNumber == null ? "departmentNumber" : ldapMappingUserDepartmentNumber))
				lstAttr.add(ldapMappingUserDepartmentNumber == null ? "departmentNumber" : ldapMappingUserDepartmentNumber);
		default:
			break;
		}		
		
		SearchControls sc = new SearchControls(SearchControls.ONELEVEL_SCOPE,
				0,//1L, //count limit
				0,  //time limit
				lstAttr.toArray(new String[lstAttr.size()]),
				//new String[] {"departmentNumber","o"},//null,//attributes (null = all)
				false,// return object ?
				false);// dereference links? 
		sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);

		return sc;
	}
	
	private boolean isAttributeEligible(String attr){
		if (StringUtils.isEmpty(attr))
			return false;
		return !attr.startsWith("$");
		// this is a constant value to search for it		
	}

}
