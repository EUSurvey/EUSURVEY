package com.ec.survey.tools;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.LdapDBService;
import com.ec.survey.service.LdapService;
import com.ec.survey.service.SchemaService;

@Service("ecasWorker")
@Scope("singleton")
public class EcasUserUpdater implements Runnable {

	protected static final Logger logger = Logger.getLogger(EcasUserUpdater.class);

	@Resource(name = "ldapService")
	private LdapService ldapService;
	
	@Resource(name = "ldapDBService")
	private LdapDBService ldapDBService;	
	
	@Resource(name = "schemaService")
	private SchemaService schemaService;	
	
	@Resource(name = "administrationService")
	private AdministrationService administrationService;	
	
	@Override
	public void run() {
		try {
			StopWatch stopWatch = new StopWatch();
			
			Date lastLDAPSynchronizationDate = schemaService.getLastLDAPSynchronizationDate();
			Date currentDate = new Date();
						
			logger.info("EcasUserUpdater started");
			stopWatch.start("get LDAP data");
			List<EcasUser> usersInLDAP = ldapService.getAllEcasUsers(lastLDAPSynchronizationDate);
			stopWatch.stop();
			logger.info("Ldap users count: "+usersInLDAP.size());
			
			logger.info("EcasUserUpdater: Users read");
			stopWatch.start("get database data");
			Map<String,EcasUser> usersInDB = ldapDBService.getAllECASLogins();
			logger.info("Database users count: "+usersInDB.size());
			stopWatch.stop();

			stopWatch.start("update Data");

			// Go through the users in the LDAP modified since last run and add them again in the DB
			for (EcasUser userInLDAP: usersInLDAP)
			{
				if (usersInDB.containsKey(userInLDAP.getName()))
				{
					Integer userInDBID = usersInDB.get(userInLDAP.getName()).getId();
					userInLDAP.setId(userInDBID);
					administrationService.removeUserGroups(userInDBID);
				}
				
				administrationService.add(userInLDAP);
				
				if (!usersInDB.containsKey(userInLDAP.getName())) {
					usersInDB.put(userInLDAP.getName(), userInLDAP);
				}				
			}
			
			stopWatch.stop();
			schemaService.saveLastLDAPSynchronizationDate(currentDate);
			logger.info(stopWatch.prettyPrint());
			
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}	
		logger.info("EcasUserUpdater completed");
	}
	
}
