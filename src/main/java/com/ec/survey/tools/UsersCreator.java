package com.ec.survey.tools;

import java.util.List;

import org.apache.log4j.Logger;

import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.service.AdministrationService;

public class UsersCreator {
	private static final Logger logger = Logger.getLogger(UsersCreator.class);

	public static void createDefaultUsers(AdministrationService administrationService, boolean createStressTestData,
			String sender) throws LoginAlreadyExistsException {

		List<Role> roles = administrationService.getAllRoles();
		Role adminRole = null;
		Role managerRole = null;
		Role applicantRole = null;
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase("Administrator"))
				adminRole = role;
			if (role.getName().equalsIgnoreCase("Form Manager"))
				managerRole = role;
		}

		// create users
		User user = new User();
		user.setValidated(true);
		user.setLogin(administrationService.getAdminUser());
		user.setEmail(sender);
		user.setComment("The admin user");
		user.setLanguage("EN");
		user.setPasswordSalt(Tools.newSalt());
		user.setPassword(Tools.hash(administrationService.getAdminPassword() + user.getPasswordSalt()));
		user.setType(User.SYSTEM);
		user.getRoles().add(adminRole);
		administrationService.createUser(user);
		logger.warn("Created a default admin user");

		// this one is used for edit skin, so please do not remove it!
		user = new User();
		user.setValidated(true);
		user.setLogin("dummy");
		user.setEmail("dummy@company.org");
		user.setComment("A dummy user");
		user.setLanguage("EN");
		user.setPasswordSalt(Tools.newSalt());
		user.setPassword(Tools.hash(Tools.newSalt() + user.getPasswordSalt()));
		user.getRoles().add(applicantRole);
		user.setType(User.SYSTEM);
		administrationService.createUser(user);
		logger.warn("Created a default dummy user");

		if (createStressTestData) {
			user = new User();
			user.setValidated(true);
			user.setLogin(administrationService.getStressUser());
			user.setEmail(sender);
			user.setComment("The analyst user");
			user.setLanguage("EN");
			user.setPasswordSalt(Tools.newSalt());
			user.setPassword(Tools.hash(administrationService.getStressPassword() + user.getPasswordSalt()));
			user.getRoles().add(managerRole);
			user.setType(User.SYSTEM);
			administrationService.createUser(user);
			logger.warn("Created a default stress test user");
		}
	}

}
