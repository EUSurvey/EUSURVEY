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
			List<String> usersList = ldapService.getAllEcasUserNames();
			Set<String> users = new HashSet<>(usersList);
			stopWatch.stop();
			logger.info("Ldap users count: "+users.size());
			
			logger.info("EcasUserDeactivator: Users read");
			stopWatch.start("get database data");
			Map<String,EcasUser> existingusers = ldapDBService.getAllECASLogins();
			logger.info("Database users count: "+existingusers.size());
			stopWatch.stop();

			stopWatch.start("update Data");		
			List<Integer> ids = new ArrayList<>();
			String name;
			EcasUser ecasUser;
			for (Entry<String, EcasUser> item : existingusers.entrySet())
			{
				name = item.getKey();
				ecasUser = item.getValue();
				if (!users.contains(name) && (ecasUser.getDeactivated() == null || !ecasUser.getDeactivated()))
				{
					ids.add(existingusers.get(name).getId());
				}				
			}
			if (!ids.isEmpty())
			{
				administrationService.deactivateEcasUsers(ids);
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
