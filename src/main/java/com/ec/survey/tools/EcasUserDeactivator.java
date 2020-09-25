package com.ec.survey.tools;

import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.LdapDBService;
import com.ec.survey.service.LdapService;
import com.ec.survey.service.SchemaService;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;

@Service("ecasDeactivator")
@Scope("singleton")
public class EcasUserDeactivator implements Runnable {
	protected static final Logger logger = Logger.getLogger(EcasUserDeactivator.class);

	@Resource(name = "ldapService")
	private LdapService ldapService;
	
	@Resource(name = "ldapDBService")
	private LdapDBService ldapDBService;	
	
	@Resource(name = "administrationService")
	private AdministrationService administrationService;
	
	@Resource(name = "schemaService")
	private SchemaService schemaService;
	
	@Override
	public void run() {
		try {
			StopWatch stopWatch = new StopWatch();
						
			logger.info("EcasUserDeactivator started");
			stopWatch.start("get LDAP data");
			List<String> activatedUsersList = ldapService.getAllEcasUserNames();
			Set<String> activatedUsersSet = new HashSet<>(activatedUsersList);
			stopWatch.stop();
			logger.info("Ldap users count: "+activatedUsersSet.size());
			
			logger.info("EcasUserDeactivator: Users read");
			stopWatch.start("get database data");
			Map<String,EcasUser> usersInDB = ldapDBService.getAllECASLogins();
			logger.info("Database users count: "+usersInDB.size());
			stopWatch.stop();

			stopWatch.start("update Data");		
			List<Integer> userIdsToDeactivate = new ArrayList<>();
			String name;
			EcasUser ecasUser;

			// Go through all users in DB and compare them to the activated users in the LDAP
			for (Entry<String, EcasUser> userInDB : usersInDB.entrySet())
			{
				name = userInDB.getKey();
				ecasUser = userInDB.getValue();
				if (!activatedUsersSet.contains(name) && (ecasUser.getDeactivated() == null || !ecasUser.getDeactivated()))
				{
					userIdsToDeactivate.add(usersInDB.get(name).getId());
				}				
			}
			if (!userIdsToDeactivate.isEmpty())
			{
				administrationService.deactivateEcasUsers(userIdsToDeactivate);
			}
			schemaService.saveLastLDAPSynchronization2Date(new Date());
			
			stopWatch.stop();
			logger.info(stopWatch.prettyPrint());						
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}	
		logger.info("EcasUserDeactivator completed");
	}	
}
