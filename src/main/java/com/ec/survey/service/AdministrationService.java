package com.ec.survey.service;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.*;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.LoginAlreadyExistsException;
import com.ec.survey.tools.Tools;

import org.apache.commons.io.IOUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

@Service("administrationService")
public class AdministrationService extends BasicService {

	@Autowired
	private SqlQueryService sqlQueryService;

	private @Value("${admin.user}") String adminuser;
	private @Value("${admin.password}") String adminpassword;
	private @Value("${stress.user}") String stressuser;
	private @Value("${stress.password}") String stresspassword;

	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${sender}") String sender;
	private @Value("${server.prefix}") String host;

	public String getAdminUser() {
		return adminuser;
	}

	public String getAdminPassword() {
		return adminpassword;
	}

	public String getStressUser() {
		return stressuser;
	}

	public String getStressPassword() {
		return stresspassword;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Role> getAllRoles() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Role");
		return query.list();
	}

	@Transactional(readOnly = true)
	public Map<Integer, Role> getAllRolesAsMap() {
		List<Role> roles = getAllRoles();
		Map<Integer, Role> result = new HashMap<>();
		for (Role role : roles) {
			result.put(role.getId(), role);
		}
		return result;
	}

	@Transactional(readOnly = true)
	public Role getRole(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		return (Role) session.get(Role.class, id);
	}

	@Transactional
	public void createRole(Role role) {
		Session session = sessionFactory.getCurrentSession();
		session.save(role);
	}

	@Transactional
	public void updateRole(Role role) {
		Session session = sessionFactory.getCurrentSession();
		session.update(role);
	}

	@Transactional
	public void deleteRole(int id) {
		Role role = getRole(id);
		Session session = sessionFactory.getCurrentSession();
		session.delete(role);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM User");
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Integer> getAllUserIDs() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT u.id FROM User u");
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> getUsers(UserFilter filter, SqlPagination sqlPagination) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		HashMap<String, Object> parameters = new HashMap<>();
		Query query = session.createQuery(getHql(filter, parameters));
		sqlQueryService.setParameters(query, parameters);

		return query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).setFirstResult(sqlPagination.getFirstResult()).setMaxResults(sqlPagination.getMaxResult()).list();
	}

	@Transactional(readOnly = true)
	public User getUser(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		return (User) session.get(User.class, id);
	}

	@Transactional
	public void createUser(User user) throws LoginAlreadyExistsException {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("FROM User u where u.login = :login").setString("login", user.getLogin());
		@SuppressWarnings("unchecked")
		List<User> list = query.list();

		if (!list.isEmpty())
		{
			throw new LoginAlreadyExistsException();
		}
		
		session.save(user);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateUser(User user) {
		Session session = sessionFactory.getCurrentSession();
		user = (User) session.merge(user);

		String disabled = settingsService.get(Setting.CreateSurveysForExternalsDisabled);
		if (disabled.equalsIgnoreCase("true") && user.getGlobalPrivileges().get(GlobalPrivilege.ECAccess) == 0) {
			user.setCanCreateSurveys(false);
		}

		session.setReadOnly(user, false);
		session.update(user);
	}

	@Transactional
	public boolean checkUserPassword(User user, String rawPassword) {
		logger.debug("CHECKUSERPASSWORD CALLED " + user.getPassword() + " " + rawPassword);

		String md5hash = Tools.md5hash(rawPassword);

		if (user.getPassword().equals(md5hash)) {
			// replace md5 hash by salted SHA-512 hash
			Session session = sessionFactory.getCurrentSession();
			user.setPasswordSalt(Tools.newSalt());
			user.setPassword(Tools.hash(rawPassword + user.getPasswordSalt()));
			session.update(user);
			return true;
		}

		return false;
	}
	
	@Transactional
	public String setUserDeleteRequested(int id) throws NumberFormatException, Exception {
		Session session = sessionFactory.getCurrentSession();
		User user = (User) session.get(User.class, id);
		String login = user.getLogin();
		String code = UUID.randomUUID().toString();
		
		String url = host + "deleteaccount/" + user.getId() + "/" + code;
		
		StringBuilder body = new StringBuilder();
		body.append("Dear ").append(user.getName()).append(",<br /><br />Please confirm the deletion of your account by clicking the following link:<br /><br/>");
		body.append("<a href='").append(url).append("'>").append(url).append("</a><br /><br />");
		body.append("This link will remain valid for three days. If the deletion is not finalised in this time, your account will remain active.");
		body.append("<br /><br /><div style='text-align: center; border-top: 1px solid #999; border-bottom: 1px solid #999; padding: 10px; margin-top: 20px; margin-bottom: 10px; color: #999'>Please do not reply to this email</div>");
		
		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body.toString()).replace("[HOST]",host);
		
		mailService.SendHtmlMail(user.getEmail(), sender, sender, "Please confirm the deletion of your EUSurvey account", text, smtpServer, Integer.parseInt(smtpPort), null, null, null, false);

		user.setDeleteCode(code);
		user.setDeleteDate(new Date());
		user.setDeleteRequested(true);		
		session.update(user);
		return login;
	}
	
	@Transactional
	public void confirmUserDeleteRequest(int id, String code) throws MessageException {
		Session session = sessionFactory.getCurrentSession();
		User user = (User) session.get(User.class, id);
		
		if (user == null)
		{
			throw new MessageException("User unknown");
		}
		
		if (!user.getDeleteCode().equals(code))
		{
			throw new MessageException("Wrong code");
		}
		
		Calendar cal = Calendar.getInstance();  
		cal.setTime(new Date());  
		cal.add(Calendar.DAY_OF_YEAR, -3);  
		Date threedaysago = cal.getTime(); 
		
		if (user.getDeleteDate().before(threedaysago))
		{
			throw new MessageException("Request too old");
		}
		
		user.setDeleted(true);		
		session.update(user);
	}
	
	@Transactional
	public List<Integer> getUserAccountsForDeletion() {
		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session.createSQLQuery("SELECT USER_ID FROM USERS WHERE USER_DELETED = 1 AND USER_DELDATE < NOW() - INTERVAL 7 DAY");
		
		@SuppressWarnings("rawtypes")
		List users = query.list();
		List<Integer> result = new ArrayList<>();
		
		for (Object o: users)
		{
			result.add(ConversionTools.getValue(o));
		}
		
		return result;
	}

	@Transactional
	public String deleteUser(int id) {
		Session session = sessionFactory.getCurrentSession();
		User user = (User) session.get(User.class, id);
		String login = user.getLogin();
		session.delete(user);
		return login;
	}

	@Transactional(readOnly = true)
	public String[] getLoginsForPrefix(String term, String emailterm, boolean forPrivileges) {
		Session session = sessionFactory.getCurrentSession();

		Query query = null;
		if (term.length() > 0 && (emailterm != null && emailterm.length() > 0)) {
			query = session.createQuery("FROM User u where u.login like :login and u.email like :email and u.type = :type order by u.login asc").setString("type", User.SYSTEM)
					.setString("login", "%" + term + "%").setString("email", "%" + emailterm + "%");
		} else if (emailterm != null && emailterm.length() > 0) {
			query = session.createQuery("FROM User u where u.email like :email and u.type = :type order by u.login asc").setString("type", User.SYSTEM).setString("email", "%" + emailterm + "%");
		} else {
			query = session.createQuery("FROM User u where u.login like :login and u.type = :type order by u.login asc").setString("type", User.SYSTEM).setString("login", "%" + term + "%");
		}

		@SuppressWarnings("unchecked")
		List<User> list = query.setMaxResults(100).list();
		String[] result = new String[list.size()];
		int counter = 0;
		for (User user : list) {
			if (forPrivileges) {
				result[counter++] = "<tr data-id='" + user.getId() + "' id='" + user.getLogin() + "'><td>" + user.getLogin() + "</td><td>"
						+ (user.getGivenName() == null ? "&nbsp;" : user.getGivenName()) + "</td><td>" + (user.getSurName() == null ? "&nbsp;" : user.getSurName()) + "</td><td>&nbsp;</td></tr>";
			} else {
				result[counter++] = user.getLogin();
			}
		}

		return result;
	}

	@Transactional(readOnly = true)
	public User getUserForLogin(String login) {
		Session session = sessionFactory.getCurrentSession();

		String hql = "FROM User u where u.login = :login";

		Query query = session.createQuery(hql).setString("login", login);

		@SuppressWarnings("unchecked")
		List<User> list = query.list();

		if (list.size() > 0)
			return list.get(0);

		return null;
	}

	@Transactional(readOnly = true)
	public Map<String, String> getECASUserLoginsByEmail() {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createSQLQuery("SELECT USER_EMAIL, USER_LOGIN FROM USERS WHERE USER_TYPE = 'ECAS'");

		@SuppressWarnings("rawtypes")
		List res = query.list();

		HashMap<String, String> result = new HashMap<>();

		for (Object o : res) {
			Object[] a = (Object[]) o;
			if (!result.containsKey((String) a[0])) {
				result.put((String) a[0], (String) a[1]);
			}
		}

		return result;
	}

	@Transactional(readOnly = true)
	public User getUserForLogin(String login, boolean ecas) throws Exception {

		logger.debug("getUserForLogin".toUpperCase() + " START CHECK USER " + login + " IS ECAS " + ecas);
		Session session = sessionFactory.getCurrentSession();

		String hql = "FROM User u where u.login = :login  AND u.type = :type";

		Query query = session.createQuery(hql).setString("login", login);

		if (ecas) {
			query.setString("type", User.ECAS);
		} else {
			query.setString("type", User.SYSTEM);
		}

		logger.debug("getUserForLogin".toUpperCase() + " START CHECK USER LAUNCH QUERY ");

		@SuppressWarnings("unchecked")
		List<User> list = query.list();

		logger.debug("getUserForLogin".toUpperCase() + " START CHECK USER QUERY  EXECUTED WITH RESULT SIZE " + list.size());

		if (list.isEmpty())
			throw new MessageException("No user found for login " + login);
		if (list.size() > 1)
			throw new MessageException("Multiple users found for login " + login);

		return list.get(0);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void save(UsersConfiguration userConfiguration) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(userConfiguration);
	}

	@Transactional(readOnly = true)
	public UsersConfiguration getUsersConfiguration(int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM UsersConfiguration c where c.userId = :userId").setInteger("userId", userId);

		@SuppressWarnings("unchecked")
		List<UsersConfiguration> list = query.list();
		if (list.isEmpty())
		{
			return null;
		}
		return list.get(0);
	}

	@Transactional(readOnly = false)
	public void sendValidationEmail(User user) throws NumberFormatException, Exception {
		Session session = sessionFactory.getCurrentSession();

		user.setValidationCode(UUID.randomUUID().toString());
		user.setValidationCodeGeneration(new Date());
		session.update(user);

		String link = host + "validate/" + user.getId() + "/" + user.getValidationCode();

		String body = "Dear " + user.getLogin() + ",<br /><br />Please validate your account by clicking the link below: <br /><br /> <a href=\"" + link + "\">" + link + "</a>";

		mailService.SendHtmlMail(user.getEmail(), sender, sender, "EUSurvey Registration", body, smtpServer, Integer.parseInt(smtpPort), null);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean sendNewEmailAdressValidationEmail(User user) {
		try {
			Session session = sessionFactory.getCurrentSession();
			user.setValidationCode(UUID.randomUUID().toString());
			user.setValidationCodeGeneration(new Date());
			session.update(user);

			String link = host + "validateNewEmail/" + user.getId() + "/" + user.getValidationCode();

			String body = "Dear " + user.getLogin() + ",<br /><br />Please confirm your email change by clicking the link below: <br /><br /> <a href=\"" + link + "\">" + link + "</a>";

			InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
			String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]", host);

			mailService.SendHtmlMail(user.getEmailToValidate(), sender, sender, "EUSurvey Confirmation", text, smtpServer, Integer.parseInt(smtpPort), null);
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage(), ex);
			return false;
		}
		return true;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean validateUser(int id, String code) {
		Session session = sessionFactory.getCurrentSession();
		User user = (User) session.get(User.class, id);

		if (user != null && user.getValidationCode() != null && user.getValidationCode().equalsIgnoreCase(code)) {
			user.setValidated(true);
			session.update(user);

			return true;
		}

		return false;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean validateNewEmail(HttpServletRequest request, int id, String code) {
		Session session = sessionFactory.getCurrentSession();
		User user = (User) session.get(User.class, id);

		if (user != null && user.getValidationCode() != null && user.getValidationCode().equalsIgnoreCase(code) && user.getEmailToValidate() != null) {
			String oldEmail = user.getEmail();
			if (user.getOtherEmail() == null) {
				user.setOtherEmail(oldEmail);
			} else {
				if (!user.getOtherEmail().endsWith(";")) {
					user.setOtherEmail(user.getOtherEmail() + ";" + oldEmail);
				} else {
					user.setOtherEmail(user.getOtherEmail() + oldEmail);
				}
			}

			user.setEmail(user.getEmailToValidate());
			user.setEmailToValidate(null);
			user.setValidationCode(null);
			session.update(user);
			sessionService.setCurrentUser(request, user);
			return true;
		}

		return false;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public OneTimePasswordResetCode createOneTimePasswordResetCode(User user) {
		OneTimePasswordResetCode code = new OneTimePasswordResetCode(user);
		Session session = sessionFactory.getCurrentSession();
		session.save(code);
		return code;
	}

	@Transactional(readOnly = true)
	public OneTimePasswordResetCode getOneTimePasswordResetCode(String code) throws MessageException {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("FROM OneTimePasswordResetCode c where c.code = :code").setString("code", code);
		@SuppressWarnings("unchecked")
		List<OneTimePasswordResetCode> list = query.list();
		if (list.isEmpty())
		{
			throw new MessageException("No item found for code " + code);
		}
		if (list.size() > 1)
		{
			throw new MessageException("Multiple items found for code " + code);
		}
		
		return list.get(0);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void add(EcasUser eu) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(eu);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeUserGroups(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery("DELETE FROM  ECASGROUPS  where  eg_id = :id").setInteger("id", id);
		query.executeUpdate();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deactivateEcasUser(int id) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("UPDATE EcasUser u SET u.deactivated = true WHERE u.id = :id").setInteger("id", id);
		query.executeUpdate();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deactivateEcasUsers(List<Integer> ids) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("UPDATE EcasUser u SET u.deactivated = true WHERE u.id = :id");
		int counter = 0;
		for (int id : ids) {
			query.setInteger("id", id);
			query.executeUpdate();
			counter += 1;
			if (counter % 10000 == 0) {
				logger.info(counter + " EcasUsers deactivated");
			}
		}
	}

	@Transactional(readOnly = true)
	public int getNumberOfUsers(UserFilter filter) {
		Session session = sessionFactory.getCurrentSession();

		HashMap<String, Object> parameters = new HashMap<>();
		Query query = session.createQuery(getHql(filter, parameters));

		for (Entry<String, Object> entry : parameters.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof String) {
				query.setString(entry.getKey(), (String) value);
			} else if (value instanceof Integer) {
				query.setInteger(entry.getKey(), (Integer) value);
			} else if (value instanceof Date) {
				query.setDate(entry.getKey(), (Date) value);
			}
		}

		return query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list().size();
	}

	private String getHql(UserFilter filter, HashMap<String, Object> parameters) {
		StringBuilder hql = new StringBuilder("SELECT DISTINCT u FROM User u LEFT JOIN u.roles as r WHERE u.id > 0");

		if (filter.getLogin() != null && filter.getLogin().length() > 0) {
			hql.append(" AND u.login like :login");
			parameters.put("login", "%" + filter.getLogin() + "%");
		}

		if (filter.getEmail() != null && filter.getEmail().length() > 0) {
			hql.append(" AND u.email like :email");
			parameters.put("email", "%" + filter.getEmail() + "%");
		}

		if (filter.getComment() != null && filter.getComment().length() > 0) {
			hql.append(" AND u.comment like :comment");
			parameters.put("comment", "%" + filter.getComment() + "%");
		}

		if (filter.getLanguages() != null) {
			int i = 0;
			hql.append(" AND (");
			for (String lang : filter.getLanguages()) {
				if (lang.trim().length() > 0) {
					String l = "lang" + i++;

					if (i > 1) {
						hql.append(" OR");
					}

					hql.append(" ( u.language like :").append(l).append(")");
					parameters.put(l, lang.trim());
				}
			}
			hql.append(" )");
		}

		if (filter.getECAS() != null && filter.getSystem() != null) {
			if (filter.getECAS() && !filter.getSystem()) {
				hql.append(" AND u.type = 'ECAS'");
			} else if (!filter.getECAS() && filter.getSystem()) {
				hql.append(" AND u.type = 'SYSTEM'");
			}
		}
		
		if (filter.getBanned() != null && filter.getBanned() && filter.getUnbanned() != null && filter.getUnbanned()) {
			//show all
		} else if (filter.getBanned() != null && filter.getBanned()) {
			hql.append(" AND u.frozen = true");
		} else if (filter.getUnbanned() != null && filter.getUnbanned()) {
			hql.append(" AND u.frozen = false");
		}

		if (filter.getRoles() != null) {
			int i = 0;
			hql.append(" AND (");
			for (String role : filter.getRoles()) {
				if (role.trim().length() > 0) {
					String l = "role" + i++;

					if (i > 1) {
						hql.append(" OR");
					}

					hql.append(" r.id like :").append(l);

					parameters.put(l, Integer.parseInt(role.trim()));
				}
			}
			hql.append(" )");
		}

		if (filter.getECASaccess() != null && filter.getNoECASaccess() != null)
			if (filter.getECASaccess() && !filter.getNoECASaccess()) {
				hql.append(" AND u.canAccessEcasFunctionality = true");
			} else if (!filter.getECASaccess() && filter.getNoECASaccess()) {
				hql.append(" AND u.canAccessEcasFunctionality = false");
			}

		if (filter.getECaccess() != null && filter.getNoECaccess() != null)
			if (filter.getECaccess() && !filter.getNoECaccess()) {
				hql.append(" AND u.canAccessECFunctionality = true");
			} else if (!filter.getECaccess() && filter.getNoECaccess()) {
				hql.append(" AND u.canAccessECFunctionality = false");
			}

		if (filter.getSortKey() != null && filter.getSortKey().length() > 0) {

			hql.append(" ORDER BY u.").append(filter.getSortKey());

			if (filter.getSortOrder() != null && filter.getSortOrder().length() > 0) {
				hql.append(" ").append(filter.getSortOrder());
			} else {
				hql.append(" DESC");
			}

		}

		return hql.toString();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void createDummyEcasUsers(int counter) {
		Session session = sessionFactory.getCurrentSession();

		for (int i = 0; i < 100000; i++) {
			EcasUser eu = new EcasUser();
			eu.setDepartmentNumber("Department1");
			eu.setEcMoniker("newuserz" + counter + "#" + i);
			eu.setEmail("test@clam.dialogika.de");
			eu.setName("newnamez" + counter + "#" + i);
			eu.setUserLDAPGroups(new HashSet<>());
			eu.getUserLDAPGroups().add("Department1");
			session.saveOrUpdate(eu);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void createDummySurAccess() throws Exception {
		Session session = sessionFactory.getCurrentSession();

		User user = getUser(8);

		SurveyFilter filter = new SurveyFilter();
		filter.setUser(getUser(1));
		SqlPagination sqlPagination = new SqlPagination(0, 5000);
		List<Survey> surveys = surveyService.getSurveys(filter, sqlPagination);

		for (Survey survey : surveys) {
			Access a = new Access();
			a.setSurvey(survey);
			a.setUser(user);
			a.getLocalPrivileges().put(LocalPrivilege.FormManagement, 1);
			session.saveOrUpdate(a);
		}
	}

	public boolean isSmtpServerConfigured() {
		return !StringUtils.isEmpty(smtpServer);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public User setLastEditedSurvey(User user, Integer surveyid) {
		Session session = sessionFactory.getCurrentSession();
		user = (User) session.merge(user);

		String disabled = settingsService.get(Setting.CreateSurveysForExternalsDisabled);
		if (disabled.equalsIgnoreCase("true") && user.getGlobalPrivileges().get(GlobalPrivilege.ECAccess) == 0) {
			user.setCanCreateSurveys(false);
		}

		user.setLastEditedSurvey(surveyid);
		session.saveOrUpdate(user);
		return user;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void banUser(String userId, String mailText) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		User user = getUser(Integer.parseInt(userId));

		if (user == null) {
			throw new MessageException("user does not exist");
		}

		user.setFrozen(true);
		session.saveOrUpdate(user);

		// send email to user
		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String mailtemplate = IOUtils.toString(inputStream, "UTF-8");
		String mailtext = mailtemplate.replace("[CONTENT]", mailText).replace("[HOST]", host);
		mailService.SendHtmlMail(user.getEmail(), sender, sender, "Your account has been banned", mailtext, smtpServer, Integer.parseInt(smtpPort), null);

		// send email to admins
		String recipients = settingsService.get(Setting.BannedUserRecipients);
		mailtext = settingsService.get(Setting.FreezeUserTextAdminBan).replace("[LOGIN]", user.getLogin()).replace("[EMAIL]", user.getEmail());
		mailtext = mailtemplate.replace("[CONTENT]", mailtext).replace("[HOST]", host);

		String[] emails = recipients.split(";");
		for (String recipient : emails) {
			if (recipient.trim().length() > 0) {
				mailService.SendHtmlMail(recipient, sender, sender, "User banned", mailtext, smtpServer, Integer.parseInt(smtpPort), null);
			}
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void unbanUser(String userId) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		User user = getUser(Integer.parseInt(userId));

		if (user == null) {
			throw new MessageException("user does not exist");
		}

		user.setFrozen(false);
		session.saveOrUpdate(user);

		// send email to user
		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String mailtemplate = IOUtils.toString(inputStream, "UTF-8");

		String content = settingsService.get(Setting.FreezeUserTextUnban);

		String mailtext = mailtemplate.replace("[CONTENT]", content).replace("[HOST]", host);
		mailService.SendHtmlMail(user.getEmail(), sender, sender, "Your account has been unbanned", mailtext, smtpServer, Integer.parseInt(smtpPort), null);

		// send email to admins
		String recipients = settingsService.get(Setting.BannedUserRecipients);
		mailtext = settingsService.get(Setting.FreezeUserTextAdminUnban).replace("[LOGIN]", user.getLogin()).replace("[EMAIL]", user.getEmail());
		mailtext = mailtemplate.replace("[CONTENT]", mailtext).replace("[HOST]", host);

		String[] emails = recipients.split(";");
		for (String recipient : emails) {
			if (recipient.trim().length() > 0) {
				mailService.SendHtmlMail(recipient, sender, sender, "User unbanned", mailtext, smtpServer, Integer.parseInt(smtpPort), null);
			}
		}
	}

	@Transactional(readOnly = true)
	public boolean checkEmailsNotBanned(List<String> allEmailAddresses) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("SELECT COUNT(u.id) FROM User u WHERE u.frozen = true AND u.email IN (:emails)");
		query.setParameterList("emails", allEmailAddresses);
		
		int count = ConversionTools.getValue(query.uniqueResult());		

		return count == 0;
	}

}
