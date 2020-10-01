package com.ec.survey.service;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.DepartmentItem;
import com.ec.survey.model.KeyValue;
import com.ec.survey.model.LdapSearchResult;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.tools.DepartmentUpdater;
import com.ec.survey.tools.DomainUpdater;
import com.ec.survey.tools.EcasUserUpdater;
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
    private DirContext ctx;
    
    public static final String LDAP_CONSTANT_PREFIX = "$";
    
    @Resource(name="ldapDBService")
	private LdapDBService ldapDBService;
    
    @Autowired
	@Qualifier("departmentWorker")
	private DepartmentUpdater departmentWorker;
        
    @Autowired
	@Qualifier("domainWorker")
	private DomainUpdater domainWorker;
	
	@Autowired
	@Qualifier("ecasWorker")
	private EcasUserUpdater ecasWorker;
    
    private static final Logger logger = Logger.getLogger(LdapService.class);
    
    private @Value("${LdapUrl}") String url;
    private @Value("${LdapContextFactory}") String contextFactory;
    private @Value("${LdapSecurityPrincipal}") String securityPrincipal;
    private @Value("${LdapSecurityCredentials}") String securityCredentials;
    private @Value("${LdapSecurityAuthentication}") String securityAuthentication;
    private @Value("${ldap.search.user.format:@null}") String ldapSearchUserFormat;
    private @Value("${ldap.search.format:@null}") String ldapSearchFormat;
   
    private @Value("${ldap.mapping.user.departmentNumber:@null}") String ldapMappingUserDepartmentNumber;
    private @Value("${ldap.mapping.user.sn:@null}") String ldapMappingUserSn;
    private @Value("${ldap.mapping.user.uid:@null}") String ldapMappingUserUid;
    private @Value("${ldap.mapping.user.ecMoniker:@null}") String ldapMappingUserEcMoniker;
    private @Value("${ldap.mapping.user.o:@null}") String ldapMappingUserO;
    private @Value("${ldap.mapping.user.givenName:@null}") String ldapMappingUserGivenName;
    private @Value("${ldap.mapping.user.mail:@null}") String ldapMappingUserMail;
    private @Value("${ldap.mapping.user.telephoneNumber:@null}") String ldapMappingUserTelephoneNumber;
    private @Value("${ldap.mapping.user.employeeType:@null}") String ldapMappingUserEmployeeType;
    private @Value("${ldap.mapping.user.recordStatus:@null}") String ldapMappingUserRecordStatus;
    private @Value("${ldap.mapping.user.modifyTimstamp:@null}") String ldapMappingUserModifyTimstamp;
    
    private @Value("${ldap.mapping.domain.o:@null}") String ldapMappingDomainO;
    private @Value("${ldap.mapping.domain.description:@null}") String ldapMappingDomainDescription;
    private @Value("${ldap.search.domains.format:@null}") String ldapSearchDomainFormat;
        
    public @Value("${casoss}") String cassOss;

	public boolean isCasOss()
	{
		return cassOss != null && cassOss.equalsIgnoreCase("true");
	}
   
    private void initialize() {
        try {
        	if (securityAuthentication == null || securityAuthentication.length() == 0) securityAuthentication = "simple";
        	
        	logger.debug("LDAP INITIALIZE Context " + contextFactory +" URL " + url + " SecuAuth " + securityAuthentication +" SecuPrinc " + securityPrincipal );
            // Create a Directory Context
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
            env.put(Context.PROVIDER_URL, url);
            env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
            env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
            env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
            
            logger.debug("LDAP SERVICE INITIALIZE " + env.toString());
            ctx = new InitialDirContext(env);
            logger.info("LDAP SERVICE INITIALIZECONTEXT DONE ");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }
      
    public String getEmail(String userName) {
    	
    	userName = Tools.encodeForLDAP(userName);
    	
        String email = "";
        initialize();
        try {
        	String searchValue= String.format(ldapSearchUserFormat, userName);
            Attributes attrs = ctx.getAttributes(searchValue);
            email = (String) attrs.get("mail").get();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return email;
    }
    
	public String getMoniker(String login) {
		  String moniker = "";
	        initialize();
	        try {
	        	login = Tools.encodeForLDAP(login);
	        	String searchValue= String.format(ldapSearchUserFormat, login);
	            Attributes attrs = ctx.getAttributes(searchValue);
	            moniker = (String) attrs.get(ldapMappingUserEcMoniker).get();
	        } catch (Exception e) {
	            logger.error(e.getLocalizedMessage(), e);
	        }
	        return moniker;
	}
   
    public List<String> getUserLDAPGroups(String username) {

        List<String> groups = new ArrayList<>();
        try {
            initialize();
            username = Tools.encodeForLDAP(username);
            String searchValue= String.format(ldapSearchUserFormat, username);
            Attributes attrs = ctx.getAttributes(searchValue);
            logger.debug("getUserLDAPGroups CALLED FOR USER " + username);
            
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
                
            } else if (Objects.equals(employeeType, "f") || Objects.equals(employeeType, "x") || Objects.equals(employeeType, "i")) {
                //internal                
            } else {
            	groups.add("external");
            }
           
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
    
    public Set<DepartmentItem> getAllDepartments()
    {
    	Set<DepartmentItem> departmentNumbers = new HashSet<>();
    	
    	try {
	
			initialize();

	
			// check if a constant is used in this case then just get the constant value instead 
			// searching through the Ldap server
			if(StringUtils.isEmpty(ldapMappingUserDepartmentNumber)){
				String message = String.format(
						"The property %s from the spring.properties file MUST have a value",
						"ldap.mapping.user.departmentNumber");
				throw new MessageException(message);				
			}
			
			if (StringUtils.isEmpty(ldapMappingUserO)){
				String message = String.format(
						"The property %s from the spring.properties file MUST have a value",
						"ldap.mapping.user.o");
				throw new MessageException(message);				
			}				
			
			String departmentNumber="";
			// if the departement is a constant the Domain MUST also be an constant
			if(ldapMappingUserDepartmentNumber.startsWith(LDAP_CONSTANT_PREFIX)){
				departmentNumber= ldapMappingUserDepartmentNumber.replace(LDAP_CONSTANT_PREFIX, "");
					
				if(!ldapMappingDomainO.startsWith(LDAP_CONSTANT_PREFIX)){
					String message = String.format(
							"The property %s from the spring.properties file MUST be a constant (staring with '%s' value)",
							"ldap.mapping.domain.o",  ldapMappingDomainO);
					throw new MessageException(message);						
				}else{					
					String domainCode= ldapMappingDomainO.replace(LDAP_CONSTANT_PREFIX, "");
					DepartmentItem currentDepartmentItem = new DepartmentItem(domainCode, departmentNumber);
					departmentNumbers.add(currentDepartmentItem);
					return departmentNumbers;					
				}
			}
							
			SearchControls sc= getSearchControls(LdapSearchTypeEnum.DEPARTMENT);
			NamingEnumeration<SearchResult> ne = null; 
			Attributes set_att;
			String searchString = "(objectClass=*)";
	
			String domainCode = "";
			DepartmentItem currentDepartmentItem = null ;
			try{
				logger.debug("START SEARCH FOR DEPARTNMENTNUMBER");								
				ne = ctx.search(ldapSearchFormat,searchString,sc);
				logger.debug("SEARCH FOR DEPARTNMENTNUMBER DONE START LOOPING " );
	
				while(ne.hasMore()){
					departmentNumber="";
					domainCode="";
					SearchResult sr = ne.next();  
					set_att = sr.getAttributes();
					
					if(set_att.get(ldapMappingUserDepartmentNumber)!=null)
						departmentNumber = getAttributeValue(set_att, ldapMappingUserDepartmentNumber,false) ;


					if(ldapMappingUserO.startsWith(LDAP_CONSTANT_PREFIX)){
						domainCode= ldapMappingUserO.replace(LDAP_CONSTANT_PREFIX, "");
					}else if (set_att.get(ldapMappingUserO)!=null)
					{
						domainCode = getAttributeValue(set_att, ldapMappingUserO,false) ;
					}
												            
		            logger.debug("SEARCH DEPARTMENTNUMBER COUNT ATTRIBUTES NAME  IS " + set_att.size()  );

					if (!StringUtils.isEmpty(departmentNumber)  && !StringUtils.isEmpty(domainCode))
					{						
						currentDepartmentItem = new DepartmentItem(domainCode, departmentNumber) ;
						if (!departmentNumbers.contains(currentDepartmentItem))
						{
							departmentNumbers.add(currentDepartmentItem);
						}
					}
				}

				for (DepartmentItem next : departmentNumbers) {
					String nextName = next.getName();
					if (next != null && nextName != null && nextName.length() > 0) {
						next.setName(nextName.replace("&", "AND").replace("'", "").replace("<", "").replace(">", ""));
					}
				}
	
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}	
    	} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		
		logger.debug("RETURN DEPARTNMENTNUMBER WITH  " + departmentNumbers.size() );
		return departmentNumbers;
    }
    
    
    public Map<String,String> getAllDomains()
    {
    	Map<String,String> domains = new HashMap<>();
    	
    	try {
	
			initialize();
			// if empty then one domain mut be set as constant in the other domainField
			if(StringUtils.isEmpty(ldapSearchDomainFormat)){
				if (StringUtils.isEmpty(ldapMappingDomainDescription)){
					String message = String.format(
							"Unable to retrieve the necessary information from the config file for the domain value, the property (%s) MUST contain a constant value starting with (%s) but this value is found (%s)",
							"ldap.mapping.domain.description", LDAP_CONSTANT_PREFIX,ldapMappingDomainDescription);
					throw new MessageException(message);
				}

				// check that the value is a constant value start with $
				if (!ldapMappingDomainDescription.startsWith(LDAP_CONSTANT_PREFIX)){
					String message = String.format(
							"Unable to retrieve the necessary information from the config file for the domain value, the property (%s) MUST contain a constant value starting with (%s) but this value is found (%s)",
							"ldap.mapping.domain.description", LDAP_CONSTANT_PREFIX,ldapMappingDomainDescription);
					throw new MessageException(message);
	
				}
				
				if (StringUtils.isEmpty(ldapMappingDomainO)){
					String message = String.format(
							"Unable to retrieve the necessary information from the config file for the domain value, the property (%s) MUST contain a constant value staring with (%s) but this value is found (%s)",
							"ldap.mapping.domain.o",LDAP_CONSTANT_PREFIX ,ldapMappingDomainO);
					throw new MessageException(message);
				}
				
				// check that the value is a constant value start with $
				if (!ldapMappingDomainO.startsWith(LDAP_CONSTANT_PREFIX)){
					String message = String.format(
							"Unable to retrieve the necessary information from the config file for the domain value, the property (%s) MUST contain a constant value starting with (%s) but this value is found (%s)",
							"ldap.mapping.domain.o", LDAP_CONSTANT_PREFIX,ldapMappingDomainO);
					throw new MessageException(message);
					
				}
				
				String domainO =ldapMappingDomainO.replace(LDAP_CONSTANT_PREFIX, "");
				String domainDesc=ldapMappingDomainDescription.replace(LDAP_CONSTANT_PREFIX, "");
				logger.debug("ADD NEW DOMAIN WITH O " + domainO + " DESC " + domainDesc);
				domains.put(domainO, domainDesc);						
			}else{
				SearchControls sc = getSearchControls(LdapSearchTypeEnum.DOMAIN);
			NamingEnumeration<SearchResult> ne = null; 
	
			Attributes setOfAttributes;
			Attribute attributeOrganization;
			Attribute attributeDescription;
	
			String searchString = "(objectClass=*)";
			try{
					ne = ctx.search(ldapSearchDomainFormat,searchString,sc);
				while(ne.hasMore()){
					SearchResult sr = ne.next();  
					setOfAttributes = sr.getAttributes();
						attributeOrganization = setOfAttributes.get(ldapMappingDomainO);
						attributeDescription=setOfAttributes.get(ldapMappingDomainDescription);
						logger.debug("GetAllDomains get Domain with o value " + attributeOrganization.get());
					if(attributeOrganization != null && attributeDescription != null)
					{
						domains.put((String)attributeOrganization.get(), (String) attributeDescription.get());
					}
				}
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}	
				
			}
    	} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return domains;
    }

	
	public void reloadEcasUser()
	{
		logger.warn("START ECASWORKER");
		getPool().execute(ecasWorker);
	}
	
	public void reloadDepartments()
	{
		logger.warn("START DEPARTMENTWORKER");
		getPool().execute(departmentWorker);
	}
	
	public void reloadDomains()
	{
		logger.warn("START DOMAINWORKER");
		getPool().execute(domainWorker);
	}
	
	public List<EcasUser> getAllEcasUsers(Date lastLDAPSynchronizationDate)
	{
		List<EcasUser> result = new ArrayList<>();
	
		initialize();
		
		try {

			SearchControls sc= getSearchControls(LdapSearchTypeEnum.USER);
			NamingEnumeration<SearchResult> namingEnumeration = null; 

			Attributes userAttributes;

			String searchString = "(objectClass=*)";
			
			// set timestamp criteria only if such a field has been set in property file
			if (lastLDAPSynchronizationDate != null && isAttributeEligible(ldapMappingUserModifyTimstamp))
			{
				searchString = "(modifyTimestamp>=" + Tools.formatDate(lastLDAPSynchronizationDate, "yyyyMMddHHmmss") + ".0Z)";
			}

			try{
				namingEnumeration = ctx.search(ldapSearchFormat,searchString,sc);
				
				while(namingEnumeration.hasMore()){
					
					SearchResult searchResult = namingEnumeration.next();  
					userAttributes = searchResult.getAttributes();
			
					if (StringUtils.isEmpty(ldapMappingUserUid) || userAttributes.get(ldapMappingUserUid)==null ){
						String message =String.format("Missing required attribute %s or equivalent, either is empty or not defined, name of the attribute found in the property file is %s,  please check","uid",ldapMappingUserUid);
						throw new MessageException(message);
					}
					
					String name = ((String)userAttributes.get(ldapMappingUserUid).get()).trim();
					String email = "";
					if (userAttributes.get(ldapMappingUserMail) != null) email = (String)userAttributes.get(ldapMappingUserMail).get();		
					
					String givenName = "";
					if (userAttributes.get(ldapMappingUserGivenName) != null) givenName = (String)userAttributes.get(ldapMappingUserGivenName).get();		
					
					String ecMoniker = "";
					if (userAttributes.get(ldapMappingUserEcMoniker) != null) ecMoniker = ((String)userAttributes.get(ldapMappingUserEcMoniker).get()).trim();
					
					String employeeType = "";
					if (userAttributes.get(ldapMappingUserEmployeeType) != null) employeeType = (String)userAttributes.get(ldapMappingUserEmployeeType).get();
					
					String o = "";
					if(!StringUtils.isEmpty(ldapMappingUserO)){
						if(ldapMappingUserO.startsWith(LDAP_CONSTANT_PREFIX)){
							o = ldapMappingUserO.replace(LDAP_CONSTANT_PREFIX, "");
						}else{
							o = (String)userAttributes.get(ldapMappingUserO).get();
						}							
					}					
					
					String surname = "";
					if (userAttributes.get(ldapMappingUserSn) != null) surname = (String)userAttributes.get(ldapMappingUserSn).get();
					
					String phone = "";
					if (userAttributes.get(ldapMappingUserTelephoneNumber) != null) phone = (String)userAttributes.get(ldapMappingUserTelephoneNumber).get();
					
					String modifyTimestamp = "";
					Date modified = new Date();
					if (userAttributes.get(ldapMappingUserModifyTimstamp) != null) {
						modifyTimestamp = (String)userAttributes.get(ldapMappingUserModifyTimstamp).get();
						modified = Tools.parseDateString(modifyTimestamp.replace("Z", ""), "yyyyMMddHHmmss");
					}					
									
					boolean deactivated = false;
					if (userAttributes.get(ldapMappingUserRecordStatus) != null)
					{
						String recordStatus = (String)userAttributes.get(ldapMappingUserRecordStatus).get();
						deactivated = recordStatus != null && recordStatus.equalsIgnoreCase("d");
					}
										
					String department = "";
					Set<String> groups = new HashSet<>();
					if (!o.isEmpty() && !o.equals("external") )
					{
						groups.add(o.replace("eu.europa.", ""));
					}
					
					if (!StringUtils.isEmpty(ldapMappingUserDepartmentNumber) && ldapMappingUserDepartmentNumber.startsWith(LDAP_CONSTANT_PREFIX)){
						if(ldapMappingUserDepartmentNumber.startsWith(LDAP_CONSTANT_PREFIX))
							department = ldapMappingUserDepartmentNumber.replace(LDAP_CONSTANT_PREFIX, "");													
					} else {
						if (userAttributes.get(ldapMappingUserDepartmentNumber) != null)
							department = getAttributeValue(userAttributes, ldapMappingUserDepartmentNumber,true);						
					}
				            
		            if (department != null) {
		                String[] tabDep = department.split("\\.");
		                if (tabDep[0].equals("ECA"))
		                	{
		                		groups.add("ecaroot");
		                	}
		                else
		                	{
		                	groups.add("ALL-COM");
		                	}
		                if(!groups.contains(tabDep[0])) {
		                	groups.add(tabDep[0]);
		                }
		                String prefix = tabDep[0];
		                for (int i = 1; i < tabDep.length; i++)
		                {
		                	prefix += "." + tabDep[i];
		                	if(!groups.contains(prefix)){
		                		groups.add(prefix);
		                	}
		                }
		            }
								            
					result.add(new EcasUser(name, email, givenName, surname, phone, groups, ecMoniker, employeeType, o, department, deactivated, modified));            
				}
			
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}	
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return result;
	}
	
	public List<String> getAllEcasUserNames()
	{
		List<String> ecasUserNamesStillActivated = new ArrayList<>();
	
		initialize();
		
		try {

			SearchControls sc = getSearchControls(LdapSearchTypeEnum.USERNAME);
			NamingEnumeration<SearchResult> ne = null; 
			Attributes userAttributes;
			String searchString = "(objectClass=*)";
			
			try{
				ne = ctx.search(ldapSearchFormat, searchString, sc);
				
				while(ne.hasMore()){
					SearchResult nextSearchResult = ne.next();  
					userAttributes = nextSearchResult.getAttributes();
					
					if (StringUtils.isEmpty(ldapMappingUserUid) || userAttributes.get(ldapMappingUserUid)==null ){
						String message =String.format("Missing required attribute %s or equivalent, either is empty or not defined, name of the attribute found in the property file is %s,  please check","uid",ldapMappingUserUid);
						throw new MessageException(message);
					}
									
					
					String name = (String) userAttributes.get(ldapMappingUserUid).get();						
									
					boolean deactivated = false;
					// check if deactivated only if not from cas version
					if (!isCasOss() && userAttributes.get(ldapMappingUserRecordStatus) != null) {
						String recordStatus = (String)userAttributes.get(ldapMappingUserRecordStatus).get();
						deactivated = recordStatus != null && recordStatus.equalsIgnoreCase("d");
					}
										
					if (!deactivated) {        
						ecasUserNamesStillActivated.add(name);
					}
				}
			
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}	
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return ecasUserNamesStillActivated;		
	}
	
	public List<KeyValue> getTopDepartments(String domain) {
		List<KeyValue> result = new ArrayList<>();
		Set<String> added = new HashSet<>();
		
		String[] list;
		if(!StringUtils.isEmpty(ldapMappingUserDepartmentNumber) && ldapMappingUserDepartmentNumber.startsWith(LDAP_CONSTANT_PREFIX)){
			String name=ldapMappingUserDepartmentNumber.replace(LDAP_CONSTANT_PREFIX, "");
			list = new String[]{name};
		}else{
			list = ldapDBService.getDepartments(domain,null, false, false);	
		}		
		
		for (String department : list) {
			int indexOfDot = department.indexOf('.');
			if (indexOfDot > -1)
			{
				String prefix = department.substring(0, indexOfDot);
				if (!added.contains(prefix))
				{
					result.add(new KeyValue(prefix, "0"));
					added.add(prefix);
				} else {
					for (KeyValue kv: result)
					{
						if (kv.getKey().equals(prefix))
						{
							kv.setValue("0");
							break;
						}
					}
				}
			} else {
				result.add(new KeyValue(department, "1"));
				added.add(department);
			}
		}
		return result;
	}

	public List<KeyValue> getDepartments(String term) {
		List<KeyValue> result = new ArrayList<>();
		Set<String> added = new HashSet<>();
		
		String[] names;
		// CHECK IF CONSTANT VALUE THEN RETURN ONLY ONE ITEM FROM THE 
		if (!StringUtils.isEmpty(ldapMappingUserDepartmentNumber)&& ldapMappingUserDepartmentNumber.startsWith(LDAP_CONSTANT_PREFIX)){
			String name =ldapMappingUserDepartmentNumber.replace(LDAP_CONSTANT_PREFIX, "");
			names = new String[]{name};
		}else{
			names = ldapDBService.getDepartments(null,term, true, true);	
		}
		
		int points = StringUtils.countOccurrencesOf(term, ".");
		
		for (String department : names) {
			
			int pointslocal = StringUtils.countOccurrencesOf(department, ".");
			
			if (pointslocal > points + 1)
			{
				int index = department.indexOf('.', term.length()+2);
			
				String prefix = department.substring(0,index );
				if (!added.contains(prefix))
				{
					result.add(new KeyValue(prefix, "0"));
					added.add(prefix);
				} else {
					for (KeyValue kv: result)
					{
						if (kv.getKey().equals(prefix))
						{
							kv.setValue("0");
							break;
						}
					}
				}
			} else {
				result.add(new KeyValue(department, "1"));
				added.add(department);
			}
		}
		return result;
	}

	public String[] getECASLogins(String name, String department, String type, String first, String last, String email, String order) {
		
		name = Tools.encodeForLDAP(name);
		department = Tools.encodeForLDAP(department);
		type = Tools.encodeForLDAP(type);
		first = Tools.encodeForLDAP(first);
		last = Tools.encodeForLDAP(last);
		email = Tools.encodeForLDAP(email);		
		
		List<LdapSearchResult> ldqpUsers = new ArrayList<>();
		
		initialize();
		
		try {
			SearchControls sc = getSearchControls(LdapSearchTypeEnum.LOGIN);
			
			sc.setCountLimit(100);
			sc.setTimeLimit(60000);
			
			NamingEnumeration<SearchResult> ne = null; 
			Attributes set_att;

			String searchString = "(& (objectClass=*)";
						
			if (isAttributeEligible(ldapMappingUserEcMoniker) && name != null && name.length() > 0){
				searchString += getFilterContains(ldapMappingUserEcMoniker, name);			
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
					ldqpUsers.add(new LdapSearchResult(login, displayName, organisation, group, fname, lname));				
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
			
			if ( displayName == null || displayName.length() == 0) displayName = login;
			
			if (organisation.equalsIgnoreCase("external"))
			{
				displayName += " (EXT)";				
			} else {
				displayName += " (" +  organisation.replace("eu.europa.", "").toUpperCase() + ")"   ;	
			}
			
			if (group == null || group.equals("null")) group = "";
			
			resultString.add("<tr id='" + login + "'><td>" + displayName + "</td><td>" + fname + "</td><td>" + lname + "</td><td>" + group + "</td></tr>");
		}		
		
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
