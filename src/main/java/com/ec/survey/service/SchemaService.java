package com.ec.survey.service;

import com.ec.survey.model.*;
import com.ec.survey.model.administration.ComplexityParameters;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.tools.DomainUpdater;
import com.ec.survey.tools.SkinCreator;
import com.ec.survey.tools.SurveyCreator;
import org.apache.commons.io.IOUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("schemaService")
public class SchemaService extends BasicService {

	@Resource(name = "skinService")
	private SkinService skinService;

	@Resource(name = "settingsService")
	private SettingsService settingsService;

	@Resource(name = "administrationService")
	private AdministrationService administrationService;

	private @Value("${showecas}") String showecas;

	// OCAS
	public @Value("${casoss}") String cassOss;

	public boolean isCasOss() {
		return cassOss != null && cassOss.equalsIgnoreCase("true");
	}

	public @Value("${oss}") String oss;

	public boolean isOss() {
		return oss != null && oss.equalsIgnoreCase("true");
	}

	@Resource(name = "domainWorker")
	private DomainUpdater domaintWorker;

	@Transactional
	public void step93() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		settingsService.add(Setting.TrustValueCreatorInternal, "500", "int");
		settingsService.add(Setting.TrustValuePastSurveys, "500", "int");
		settingsService.add(Setting.TrustValuePrivilegedUser, "100", "int");
		settingsService.add(Setting.TrustValueNbContributions, "50", "int");
		settingsService.add(Setting.TrustValueMinimumPassMark, "100", "int");

		status.setDbversion(93);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step92() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String text = "<p>Please be informed that the following user [LOGIN] having the email address: [EMAIL] has been banned from EUSurvey.</p><p>For more information please contact the EUSurvey team.</p>";
		settingsService.add(Setting.FreezeUserTextAdminBan, text, "text");

		text = "<p>Please be informed that the following user [LOGIN] having the email address: [EMAIL] has been unbanned from EUSurvey.</p><p>For more information please contact the EUSurvey team.</p>";
		settingsService.add(Setting.FreezeUserTextAdminUnban, text, "text");

		text = "<p>Dear Sir or Madam,</p><p>You have been banned from EUSurvey application due to infrigiment to our policy.</p><p>Reason: to specify</p><p>Please refer to our <a href=\"https://ec.europa.eu/eusurvey/home/tos\">Terms of Service</a> for more information.</p> <p>Kind regards,<br />The EUSurvey Team</p>";
		settingsService.add(Setting.FreezeUserTextBan, text, "text");

		text = "<p>Dear Sir or Madam,</p><p>You have just been unbanned and got back your access to the EUSurvey application. You can now connect to EUSurvey</p> <p>Kind regards,<br />The EUSurvey Team</p>";
		settingsService.add(Setting.FreezeUserTextUnban, text, "text");

		Setting s = new Setting();
		s.setKey(Setting.BannedUserRecipients);
		s.setValue("");
		s.setFormat("email addresses separated by ;");
		session.saveOrUpdate(s);

		status.setDbversion(92);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step91() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("ALTER TABLE SETTINGS MODIFY SETTINGS.SETTINGS_VALUE TEXT");
		query.executeUpdate();

		String newReportText = "<p>The following survey:<br />" + "<table>"
				+ "<tr><td>Published survey link:</td><td>[LINK]</td></tr>" + "<tr><td>Alias:</td><td>[ALIAS]</td></tr>"
				+ "<tr><td>Title:</td><td>[TITLE]</td></tr>" + "</table>"
				+ "has been reported as infringing our policy by [EMAIL] at [DATE].</p>"
				+ "<p>The reason provided is the following: [TYPE].</p>"
				+ "<p>So far, it has been reported [COUNT] time(s).</p>";

		settingsService.update(Setting.ReportText, newReportText);

		status.setDbversion(91);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step90() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();
		String existing = settingsService.get(Setting.MaxReports);
		if (existing == null) {
			Setting s = new Setting();
			s.setKey(Setting.MaxReports);
			s.setValue("5");
			s.setFormat("int");
			session.saveOrUpdate(s);

			s = new Setting();
			s.setKey(Setting.ReportText);
			s.setValue(
					"Survey [ALIAS] <br /> [TITLE] has been reported as infringing our policy by [EMAIL] at [DATE]. The reason provided is the following: [TYPE].<br />So far, it has been reported [COUNT] time(s).");
			s.setFormat("text");
			session.saveOrUpdate(s);

			s = new Setting();
			s.setKey(Setting.ReportRecipients);
			s.setValue("");
			s.setFormat("email addresses separated by ;");
			session.saveOrUpdate(s);
		}
		status.setDbversion(90);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step89() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();
		String existing = settingsService.get(Setting.WeakAuthenticationDisabled);
		if (existing == null) {
			Setting s = new Setting();
			s.setKey(Setting.WeakAuthenticationDisabled);
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);
		}
		status.setDbversion(89);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step88() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();
		String existing = settingsService.get(Setting.ReportingMigrationEnabled);
		if (existing == null) {
			Setting s = new Setting();
			s.setKey(Setting.ReportingMigrationEnabled);
			s.setValue("false");
			s.setFormat("true / false");
			session.saveOrUpdate(s);

			s = new Setting();
			s.setKey(Setting.ReportingMigrationStart);
			s.setValue("20:00");
			s.setFormat("HH:mm");
			session.saveOrUpdate(s);

			s = new Setting();
			s.setKey(Setting.ReportingMigrationTime);
			s.setValue("60");
			s.setFormat("runtime in minutes");
			session.saveOrUpdate(s);

			s = new Setting();
			s.setKey(Setting.ReportingMigrationSurveyToMigrate);
			s.setValue("");
			s.setFormat("uid of the survey");
			session.saveOrUpdate(s);
		}
		status.setDbversion(88);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step87() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();
		String existing = settingsService.get(Setting.CreateSurveysForExternalsDisabled);
		if (existing == null) {
			Setting s = new Setting();
			s.setKey(Setting.CreateSurveysForExternalsDisabled);
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);
		}
		status.setDbversion(87);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step86() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();
		if (!isOss()) {
			Skin standardskin = skinService.get(1);
			Skin ecaskin = new Skin();
			ecaskin.setName("ECA Skin");
			ecaskin.setOwner(standardskin.getOwner());
			ecaskin.setIsPublic(true);
			ecaskin.setUpdateDate(new Date());
			ecaskin.getElements().clear();
			for (SkinElement element : standardskin.getElements()) {
				ecaskin.getElements().add(element.copy());
			}
			skinService.add(ecaskin);
		}
		status.setDbversion(86);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step85(ServletContext servletContext) throws IOException {
		// copying file to new file system

		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		InputStream is = servletContext.getResourceAsStream("/WEB-INF/classes/antisamy-esapi.xml");
		java.io.File target = fileService.getUsersFile(0, "antisamy-esapi.xml");
		FileOutputStream fos = new FileOutputStream(target);
		IOUtils.copy(is, fos);

		status.setDbversion(85);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step84() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		int id = surveyService.getHighestSurveyId();

		String existing = settingsService.get(Setting.LastSurveyToDeleteAnswerPDFs);
		if (existing == null) {
			Setting s = new Setting();
			s.setKey(Setting.LastSurveyToDeleteAnswerPDFs);
			s.setValue(Integer.toString(id));
			s.setFormat("id of newest survey that has to be checked for old answer pdfs");
			session.saveOrUpdate(s);
		}

		existing = settingsService.get(Setting.AnswerPDFDeletionStart);
		if (existing == null) {
			Setting s = new Setting();
			s.setKey(Setting.AnswerPDFDeletionStart);
			s.setValue("04:00");
			s.setFormat("HH:mm");
			session.saveOrUpdate(s);
		}

		existing = settingsService.get(Setting.AnswerPDFDeletionTime);
		if (existing == null) {
			Setting s = new Setting();
			s.setKey(Setting.AnswerPDFDeletionTime);
			s.setValue("60");
			s.setFormat("runtime in minutes");
			session.saveOrUpdate(s);
		}

		status.setDbversion(84);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step83() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String existing = settingsService.get("123ActivityEnabled");
		if (existing == null) {
			Setting s = new Setting();
			s.setKey("123ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);
		}

		status.setDbversion(83);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step82() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String view = "CREATE OR REPLACE VIEW SURVEYS_NUMBERPUBLISHEDANSWERS AS "
				+ "SELECT s.SURVEY_UID AS SURVEYUID, COUNT(ANSWERS_SET.ANSWER_SET_ID) AS PUBLISHEDANSWERS, MAX(ANSWERS_SET.ANSWER_SET_DATE) AS LASTANSWER "
				+ "FROM ANSWERS_SET ,SURVEYS s "
				+ "WHERE ANSWERS_SET.ISDRAFT = 0 AND ANSWERS_SET.survey_Id =s.survey_id AND s.ISDRAFT = 0 group by s.SURVEY_UID";

		session.doWork(con -> con.createStatement().execute(view));

		final String index = "ALTER TABLE INVITATIONS ADD INDEX inv_attendee_id (ATTENDEE_ID)";
		session.doWork(con -> {
			con.createStatement().execute(index);
		});

		status.setDbversion(82);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step81() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		int id = surveyService.getHighestSurveyId();

		String existing = settingsService.get(Setting.LastSurveyToMigrate);
		if (existing == null) {
			Setting s = new Setting();
			s.setKey(Setting.LastSurveyToMigrate);
			s.setValue(Integer.toString(id));
			s.setFormat("id of newest survey that still uses old file system");
			session.saveOrUpdate(s);
		}

		existing = settingsService.get(Setting.SurveyMigrateStart);
		if (existing == null) {
			Setting s = new Setting();
			s.setKey(Setting.SurveyMigrateStart);
			s.setValue("02:00");
			s.setFormat("HH:mm");
			session.saveOrUpdate(s);
		}

		existing = settingsService.get(Setting.SurveyMigrateTime);
		if (existing == null) {
			Setting s = new Setting();
			s.setKey(Setting.SurveyMigrateTime);
			s.setValue("120");
			s.setFormat("runtime in minutes");
			session.saveOrUpdate(s);
		}

		status.setDbversion(81);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step80() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		Query query = session.createSQLQuery("ALTER TABLE SCORINGITEMS MODIFY FEEDBACK TEXT");
		query.executeUpdate();

		status.setDbversion(80);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step79() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String existing = settingsService.get("disablewebservicelimit");
		if (existing == null) {
			Setting s = new Setting();
			s.setKey("disablewebservicelimit");
			s.setValue("false");
			s.setFormat("true / false");
			session.saveOrUpdate(s);
		}

		status.setDbversion(79);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step78() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();
		if (!isOss()) {
			Skin standardskin = skinService.get(1);

			Skin copy = new Skin();
			copy.setName("New Official EC Skin");
			copy.setOwner(standardskin.getOwner());
			copy.setIsPublic(true);
			copy.setUpdateDate(new Date());
			copy.getElements().clear();
			for (SkinElement element : standardskin.getElements()) {
				copy.getElements().add(element.copy());
			}
			skinService.add(copy);
		}
		status.setDbversion(78);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step77() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("UPDATE SURVEYS SET SURVEYSKIN = 1 WHERE SURVEYSKIN is null");
		query.executeUpdate();

		User admin;
		try {
			admin = administrationService.getUserForLogin(administrationService.getAdminUser(), false);

			Skin s = SkinCreator.createNewDefaultSkin(admin);
			skinService.save(s);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		status.setDbversion(77);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step76() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String existing = settingsService.get("uisessiontimeout");
		if (existing == null) {
			Setting s = new Setting();
			s.setKey("uisessiontimeout");
			s.setValue("60");
			s.setFormat("minutes");
			session.saveOrUpdate(s);
		}

		status.setDbversion(76);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step75() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from DRAFTS where Key_name = 'IDX_DRAFTS_DRAFT_UID'";
		final String createkIndex = "CREATE INDEX IDX_DRAFTS_DRAFT_UID  ON DRAFTS (DRAFT_UID);";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createkIndex).executeUpdate();
		}

		status.setDbversion(75);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step74() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String update = "UPDATE ELEMENTS children INNER JOIN ELEMENTS parents ON parents.ID = children.childElements_ID SET children.QOPTIONAL = 1 WHERE children.type = 'TEXT' AND parents.type = 'TABLE'";

		session.createSQLQuery(update).executeUpdate();

		status.setDbversion(74);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step73() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String existing = settingsService.get("captcha");
		if (existing == null) {
			Setting s = new Setting();
			s.setKey("captcha");
			s.setValue(isOss() ? "internal" : "recaptcha");
			s.setFormat("recaptcha / internal / off");
			session.saveOrUpdate(s);
		}

		status.setDbversion(73);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step72() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from VALIDCODE where Key_name = 'IDX_VALIDCODE_SURVEY_UID'";
		final String createkIndex = "CREATE INDEX IDX_VALIDCODE_SURVEY_UID on VALIDCODE (VALIDCODE_SURVEYUID )";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createkIndex).executeUpdate();
		}

		status.setDbversion(72);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step71() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from LANGUAGES where Key_name = 'IDX_LANGUAGES_LANGUAGE_CODE'";
		final String createkIndex = "CREATE INDEX IDX_LANGUAGES_LANGUAGE_CODE on LANGUAGES (LANGUAGE_CODE)";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			logger.info(
					"SchemaService Step71 Execute SQL CREATE INDEX IDX_LANGUAGES_LANGUAGE_CODE on LANGUAGES (LANGUAGE_CODE)");
			session.createSQLQuery(createkIndex).executeUpdate();
		} else {
			logger.info("SchemaService Step71 INDEX IDX_LANGUAGES_LANGUAGE_CODE already exist");
		}

		final String checkIndexExists2 = "show index from TRANSLATIONS where Key_name = 'IDX_TRANSLATIONS_SURVEY_ID'";
		final String createkIndex2 = "CREATE INDEX IDX_TRANSLATIONS_SURVEY_ID on TRANSLATIONS (SURVEY_ID)";

		query = session.createSQLQuery(checkIndexExists2);
		if (query.list().size() <= 0) {
			logger.info(
					"SchemaService Step71 Execute SQL CREATE INDEX IDX_TRANSLATIONS_SURVEY_ID on TRANSLATIONS (SURVEY_ID)");
			session.createSQLQuery(createkIndex2).executeUpdate();
		} else {
			logger.info("SchemaService Step71 INDEX IDX_TRANSLATIONS_SURVEY_ID already exist");
		}

		final String checkIndexExists3 = "show index from SURVEYS where Key_name = 'IDX_SURVEYS_SURVEY_UID'";
		final String createkIndex3 = "CREATE INDEX IDX_SURVEYS_SURVEY_UID on SURVEYS (SURVEY_UID)";

		query = session.createSQLQuery(checkIndexExists3);
		if (query.list().size() <= 0) {
			logger.info("SchemaService Step71 Execute SQL CREATE INDEX IDX_SURVEYS_SURVEY_UID on SURVEYS (SURVEY_UID)");
			session.createSQLQuery(createkIndex3).executeUpdate();
		} else {
			logger.info("SchemaService Step71 INDEX IDX_SURVEYS_SURVEY_UID already exist");
		}

		status.setDbversion(71);
		session.saveOrUpdate(status);
	}

	@Transactional
	public Status getStatus() {
		Session session = sessionFactory.getCurrentSession();
		Query statusQuery = session.createQuery("FROM Status");
		@SuppressWarnings("unchecked")
		List<Status> states = statusQuery.list();

		if (states.size() == 0)
			return null;

		session.setReadOnly(states.get(0), false);

		return states.get(0);
	}

	@Transactional
	public void step70() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String existing = settingsService.get("507ActivityEnabled");
		if (existing == null) {
			Setting s = new Setting();
			s.setKey("507ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);
		}

		status.setDbversion(70);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step69() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from FILES where Key_name = 'IDX_FILE_UID'";
		final String createkIndex = "CREATE INDEX IDX_FILE_UID on FILES (FILE_UID)";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createkIndex).executeUpdate();
		}

		status.setDbversion(69);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step68() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from ARCHIVE where Key_name = 'IDX_ARCHIVE_FINISHED'";
		final String createkIndex = "CREATE INDEX IDX_ARCHIVE_FINISHED on ARCHIVE (ARCHIVE_FINISHED, ARCHIVE_ERROR)";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createkIndex).executeUpdate();
		}

		status.setDbversion(68);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step67() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session
				.createSQLQuery("UPDATE MESSAGE_TYPES SET MT_CSS = 'message-success' WHERE MT_CSS = 'alert-success';");
		query.executeUpdate();

		query = session.createSQLQuery("UPDATE MESSAGE_TYPES SET MT_CSS = 'message-info' WHERE MT_CSS = 'alert-info';");
		query.executeUpdate();

		query = session
				.createSQLQuery("UPDATE MESSAGE_TYPES SET MT_CSS = 'message-error' WHERE MT_CSS = 'alert-danger';");
		query.executeUpdate();

		status.setDbversion(67);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step66() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from Survey_backgroundDocuments where Key_name = 'IDX_BACK_DOCS'";
		final String createkIndex = "CREATE INDEX IDX_BACK_DOCS on Survey_backgroundDocuments (BACKGROUNDDOCUMENTS)";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createkIndex).executeUpdate();
		}

		status.setDbversion(66);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step65() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from ELEMENTS where Key_name = 'IDX_ELEMENT_URL'";
		final String createkIndex = "CREATE INDEX IDX_ELEMENT_URL on ELEMENTS (URL)";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createkIndex).executeUpdate();
		}

		status.setDbversion(65);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step64() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from ACTIVITY where Key_name = 'IDX_SURVEY_UID'";
		final String createkIndex = "CREATE INDEX IDX_SURVEY_UID on ACTIVITY (ACTIVITY_SUID)";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createkIndex).executeUpdate();
		}

		status.setDbversion(64);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step63() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		try {
			SQLQuery query = session.createSQLQuery(
					"UPDATE ARCHIVE SET ARCHIVE_ERROR = ACTIVITY_ERROR, ARCHIVE_FINISHED = ACTIVITY_FINISHED, ARCHIVE_SLANGS = ACTIVITY_SLANGS, ARCHIVE_SOWNER = ACTIVITY_SOWNER, ARCHIVE_SREPLIES = ACTIVITY_SREPLIES, ARCHIVE_SSHORTNAME = ACTIVITY_SSHORTNAME, ARCHIVE_STITLE = ACTIVITY_STITLE, ARCHIVE_SUID = ACTIVITY_SUID, ARCHIVE_USER = ACTIVITY_USER;");
			query.executeUpdate();

			final String dropcolumn = "ALTER TABLE ARCHIVE DROP COLUMN ACTIVITY_ERROR, DROP COLUMN ACTIVITY_FINISHED, DROP COLUMN ACTIVITY_SLANGS, DROP COLUMN ACTIVITY_SOWNER, DROP COLUMN ACTIVITY_SREPLIES, DROP COLUMN ACTIVITY_SSHORTNAME, DROP COLUMN ACTIVITY_STITLE, DROP COLUMN ACTIVITY_SUID, DROP COLUMN ACTIVITY_USER;";

			session.doWork(con -> con.createStatement().execute(dropcolumn));
		} catch (SQLGrammarException se) {
			// this means the wrong columns do not exist (probably because it's a new
			// database)
		}

		status.setDbversion(63);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step62() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String existing = settingsService.get("313ActivityEnabled");
		if (existing == null) {
			Setting s = new Setting();
			s.setKey("313ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);
		}
		existing = settingsService.get("314ActivityEnabled");
		if (existing == null) {
			Setting s = new Setting();
			s.setKey("314ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);
		}

		status.setDbversion(62);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step61() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String existing = settingsService.get("312ActivityEnabled");
		if (existing == null) {
			Setting s = new Setting();
			s.setKey("312ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);
		}

		status.setDbversion(61);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step60() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		Role role = new Role();
		role.setName("Support");
		role.getGlobalPrivileges().put(GlobalPrivilege.RightManagement, 0);
		role.getGlobalPrivileges().put(GlobalPrivilege.UserManagement, 2);
		role.getGlobalPrivileges().put(GlobalPrivilege.FormManagement, 2);
		role.getGlobalPrivileges().put(GlobalPrivilege.ContactManagement, 2);
		role.getGlobalPrivileges().put(GlobalPrivilege.SystemManagement, 1);
		if (showecas != null && showecas.equalsIgnoreCase("true")) {
			role.getGlobalPrivileges().put(GlobalPrivilege.ECAccess, 1);
		}
		administrationService.createRole(role);

		status.setDbversion(60);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step59() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String existing = settingsService.get("122ActivityEnabled");
		if (existing == null) {
			Setting s = new Setting();
			s.setKey("122ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);
		}

		status.setDbversion(59);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step58() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String existing = settingsService.get("228ActivityEnabled");
		if (existing == null) {

			Setting s = new Setting();
			s.setKey("228ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);

		}

		status.setDbversion(58);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step57() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String existing = settingsService.get("404ActivityEnabled");
		if (existing == null) {

			Setting s = new Setting();
			s.setKey("404ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);

			s = new Setting();
			s.setKey("405ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);

			s = new Setting();
			s.setKey("406ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);

		}

		status.setDbversion(57);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step56() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String event = "ALTER EVENT SUNC_MV_SURVEYS_NUMBERPUBLISHEDANSWERS ON SCHEDULE EVERY 10 MINUTE DO "
				+ "BEGIN " + "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED; "
				+ "CREATE TABLE IF NOT EXISTS MV_SURVEYS_NUMBERPUBLISHEDANSWERS AS SELECT 1 AS DUMMY ; "
				+ "DROP TABLE IF EXISTS MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW; "
				+ "CREATE TABLE IF NOT EXISTS MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW (MW_TIMESTAMP DATETIME) AS "
				+ "SELECT SURVEYS_NUMBERPUBLISHEDANSWERS.*,NOW() MW_TIMESTAMP FROM SURVEYS_NUMBERPUBLISHEDANSWERS; "
				+ "ALTER TABLE MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW ADD INDEX MV_SURVEYS_IND (SURVEYUID, PUBLISHEDANSWERS); "
				+ "RENAME TABLE MV_SURVEYS_NUMBERPUBLISHEDANSWERS TO MV_TEMP_TABLE, MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW TO MV_SURVEYS_NUMBERPUBLISHEDANSWERS, "
				+ "MV_TEMP_TABLE TO MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW; "
				+ "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED; " + "END";

		session.doWork(con -> con.createStatement().execute(event));

		status.setDbversion(56);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step55() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from ANSWERS_SET where Key_name = 'IDX_SURVEYID_DATE'";
		final String createIndex = "ALTER TABLE ANSWERS_SET ADD INDEX IDX_SURVEYID_DATE (SURVEY_ID ASC, ANSWER_SET_DATE ASC)";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createIndex).executeUpdate();
		}

		status.setDbversion(55);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step54() {
		try {
			Survey survey = SurveyCreator.createNewSelfRegistrationSurvey(
					administrationService.getUserForLogin(administrationService.getAdminUser(), false),
					surveyService.getLanguage("EN"), surveyService.getLanguages());
			surveyService.add(survey, -1);
			surveyService.publish(survey, -1, -1, false, -1, false, false);
			surveyService.activate(survey, false, -1);

			Session session = sessionFactory.getCurrentSession();
			Status status = getStatus();
			status.setDbversion(54);
			status.setUpdateDate(new Date());
			session.saveOrUpdate(status);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@Transactional
	public void step53() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();
		SQLQuery query = session.createSQLQuery("ALTER TABLE ECASGROUPS CHANGE GRPS GRPS VARCHAR(255) BINARY");
		query.executeUpdate();

		status.setDbversion(53);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step52() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from EXPORTS where Key_name = 'CHECKNEW'";
		final String createIndex = "ALTER TABLE EXPORTS ADD INDEX CHECKNEW (USER_ID ASC, EXPORT_STATE ASC, EXPORT_NOT ASC)";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createIndex).executeUpdate();
		}

		status.setDbversion(52);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step51() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("UPDATE ATTENDEE SET OWNER_ID = 0 WHERE OWNER_ID IS NULL");
		query.executeUpdate();

		status.setDbversion(51);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step50() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();
		if (this.tableExists("GROUPDEPARTMENTS")) {
			deleteDuplicateRowsNoId("GROUPDEPARTMENTS", "gd_id", "DEPS");
			createUniqueConstraint("GROUPDEPARTMENTS", "UC_GROUPDEPARTMENTS", "gd_id", "DEPS");
		}
		status.setDbversion(50);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step49() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		for (ComplexityParameters cp : ComplexityParameters.values()) {
			Setting scp = new Setting();
			scp.setKey(cp.getKey());
			scp.setValue(cp.getDefaultValue().toString());
			scp.setFormat("Integer");
			session.saveOrUpdate(scp);
		}

		status.setDbversion(49);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step48() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery(
				"update ANSWERS a join ELEMENTS e on e.ID = a.QUESTION_ID set a.QUESTION_UID = e.ELEM_UID where a.QUESTION_UID is null and e.ELEM_UID is not null");
		query.executeUpdate();
		query = session.createSQLQuery(
				"update ANSWERS a join ELEMENTS e on e.ID = a.PA_ID set a.PA_UID = e.ELEM_UID where a.PA_UID is null and e.ELEM_UID is not null");
		query.executeUpdate();

		status.setDbversion(48);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step47() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery deleteQuery = session.createSQLQuery("DELETE FROM Statistics_requestedRecords;");
		deleteQuery.executeUpdate();
		deleteQuery = session.createSQLQuery("DELETE FROM Statistics_requestedRecordsPercent;");
		deleteQuery.executeUpdate();
		deleteQuery = session.createSQLQuery("DELETE FROM Statistics_totalsPercent;");
		deleteQuery.executeUpdate();
		deleteQuery = session.createSQLQuery("DELETE FROM STATISTICS;");
		deleteQuery.executeUpdate();

		status.setDbversion(47);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step46() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		dropIndexIfExists("ANSWERS_SET", "surveyIndex", session);
		dropIndexIfExists("ANSWERS_FILES", "FK3BD115EDA02270B1", session);
		dropIndexIfExists("ATTENDEE_ATTRIBUTES", "FK2E1AD5FC999EC0EF", session);
		// dropIndexIfExists("DASHBOARDS_WIDGETS", "FK91CCA6AFC6A2F52D", session);
		dropIndexIfExists("ECASGROUPS", "eg_id", session);
		dropIndexIfExists("ELEMENTS", "FK2E26B0F7B81F47CB", session);
		dropIndexIfExists("ELEMENTS_ELEMENTS", "FKC0C7573FE592223", session);
		dropIndexIfExists("ELEMENTS_FILES", "FK729F010F1A5FA3D", session);
		dropIndexIfExists("MATRIX_DEP", "FKA8AEFA71EFA3DB62", session);
		dropIndexIfExists("ResultFilter_filterValues", "FK1B684A4C31C9897", session);
		dropIndexIfExists("SKINS_SKINELEM", "FK92491E35A72F09", session);
		dropIndexIfExists("Statistics_requestedRecords", "FKFA712A701B0AE33", session);
		dropIndexIfExists("Statistics_requestedRecordsPercent", "FK331033B51B0AE33", session);
		dropIndexIfExists("Statistics_totalsPercent", "FKDA8FE99A1B0AE33", session);
		dropIndexIfExists("SURACCESS", "FKF46D87949F345ACB", session);
		dropIndexIfExists("SURVEYS_ELEMENTS", "FKAEF3423DB8E815E1", session);
		dropIndexIfExists("Survey_backgroundDocuments", "FKDD0DB0E54078FF13", session);
		dropIndexIfExists("Survey_usefulLinks", "FKC081917E4078FF13", session);

		status.setDbversion(46);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	private void dropIndexIfExists(String table, String index, Session session) {
		final String checkIndexExists = "show index from " + table + " where Key_name = '" + index + "'";
		final String createIndex = "ALTER TABLE " + table + " DROP INDEX " + index + ";";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() > 0) {
			session.createSQLQuery(createIndex).executeUpdate();
		}
	}

	@Transactional
	public void step45() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery deleteQuery = session
				.createSQLQuery("UPDATE ANSWERS_SET SET UNIQUECODE = UUID() WHERE length(UNIQUECODE) > 36");
		deleteQuery.executeUpdate();

		SQLQuery query = session.createSQLQuery("ALTER TABLE ANSWERS_SET MODIFY UNIQUECODE VARCHAR(36)");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE SURVEYS MODIFY TITLE TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE SURVEYS MODIFY CONFIRMATION TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE SURVEYS MODIFY ESCAPE TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE SURVEYS MODIFY INTRODUCTION TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE ATTRIBUTE MODIFY ATTRIBUTE_VALUE TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE PARTICIPANTS MODIFY TEMPL1 TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE PARTICIPANTS MODIFY TEMPL2 TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE ELEMENTS MODIFY ETITLE TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE ELEMENTS MODIFY QHELP TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE TRANSLATION MODIFY LABEL TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE MESSAGES MODIFY M_TEXT TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE ACTIVITY MODIFY ACTIVITY_NEW TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE ACTIVITY MODIFY ACTIVITY_OLD TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE FILES MODIFY FILE_COMMENT TEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE ResultFilter_filterValues MODIFY filterValues TEXT");
		query.executeUpdate();

		status.setDbversion(45);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step44() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		deleteDuplicateRowsNoId("ActivityFilter_exportedColumns", "ActivityFilter_ACFILTER_ID", "exportedColumns");
		createUniqueConstraint("ActivityFilter_exportedColumns", "UC_ActivityFilter_exportedColumns",
				"ActivityFilter_ACFILTER_ID", "exportedColumns");

		deleteDuplicateRowsNoId("ActivityFilter_visibleColumns", "ActivityFilter_ACFILTER_ID", "visibleColumns");
		createUniqueConstraint("ActivityFilter_visibleColumns", "UC_ActivityFilter_visibleColumns",
				"ActivityFilter_ACFILTER_ID", "visibleColumns");

		deleteDuplicateRowsNoId("ATTENDEEFILTER_ATTRIBUTES", "ATTENDEEFILTER_ID", "ATTRIBUTE_ID");
		createUniqueConstraint("ATTENDEEFILTER_ATTRIBUTES", "UC_ATTENDEEFILTER_ATTRIBUTES", "ATTENDEEFILTER_ID",
				"ATTRIBUTE_ID");

		deleteDuplicateRowsNoId("ECASGROUPS", "eg_id", "GRPS");
		createUniqueConstraint("ECASGROUPS", "UC_ECASGROUPS", "eg_id", "GRPS");

		deleteDuplicateRowsNoId("ELEMENTS_FILES", "ELEMENTS_ID", "files_FILE_ID");
		createUniqueConstraint("ELEMENTS_FILES", "UC_ELEMENTS_FILES", "ELEMENTS_ID", "files_FILE_ID");

		deleteDuplicateRowsNoId("PARTICIPANTS_ATTENDEE", "attendees_ATTENDEE_ID", "PARTICIPANTS_PARTICIPATION_ID");
		createUniqueConstraint("PARTICIPANTS_ATTENDEE", "UC_PARTICIPANTS_ATTENDEE", "attendees_ATTENDEE_ID",
				"PARTICIPANTS_PARTICIPATION_ID");

		deleteDuplicateRowsNoId("PARTICIPANTS_ECASUSERS", "ecasUsers_USER_ID", "PARTICIPANTS_PARTICIPATION_ID");
		createUniqueConstraint("PARTICIPANTS_ECASUSERS", "UC_PARTICIPANTS_ECASUSERS", "ecasUsers_USER_ID",
				"PARTICIPANTS_PARTICIPATION_ID");

		deleteDuplicateRowsNoId("POSSIBLEANSWER_ELEMENT", "dependentElements_ID", "DEPITEMS_ID");
		createUniqueConstraint("POSSIBLEANSWER_ELEMENT", "UC_POSSIBLEANSWER_ELEMENT", "dependentElements_ID",
				"DEPITEMS_ID");

		deleteDuplicateRowsNoId("ResultFilter_exportedQuestions", "ResultFilter_RESFILTER_ID", "exportedQuestions");
		createUniqueConstraint("ResultFilter_exportedQuestions", "UC_ResultFilter_exportedQuestions",
				"ResultFilter_RESFILTER_ID", "exportedQuestions");

		deleteDuplicateRowsNoId("ResultFilter_visibleQuestions", "ResultFilter_RESFILTER_ID", "visibleQuestions");
		createUniqueConstraint("ResultFilter_visibleQuestions", "UC_ResultFilter_visibleQuestions",
				"ResultFilter_RESFILTER_ID", "visibleQuestions");

		deleteDuplicateRowsNoId("ResultFilter_languages", "languages", "ResultFilter_RESFILTER_ID");
		createUniqueConstraint("ResultFilter_languages", "UC_ResultFilter_languages", "languages",
				"ResultFilter_RESFILTER_ID");

		deleteDuplicateRowsNoId("SHARES_ATTENDEE", "SHARES_SHARE_ID", "attendees_ATTENDEE_ID");
		createUniqueConstraint("SHARES_ATTENDEE", "UC_SHARES_ATTENDEE", "SHARES_SHARE_ID", "attendees_ATTENDEE_ID");

		deleteDuplicateRowsNoId("USERS_ATTRIBUTENAME", "USERS_USER_ID", "selectedAttributes_AN_ID");
		createUniqueConstraint("USERS_ATTRIBUTENAME", "UC_USERS_ATTRIBUTENAME", "USERS_USER_ID",
				"selectedAttributes_AN_ID");

		deleteDuplicateRowsNoId("USERS_GLOBALROLES", "USERS_USER_ID", "roles_ROLE_ID");
		createUniqueConstraint("USERS_GLOBALROLES", "UC_USERS_GLOBALROLES", "USERS_USER_ID", "roles_ROLE_ID");

		status.setDbversion(44);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step43() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String drop = "DROP TABLE IF EXISTS ANSWERS_SET_ANSWERS;";
		final String drop2 = "DROP TABLE IF EXISTS TRANSLATIONS_TRANSLATION;";

		session.doWork(con -> {
			con.createStatement().execute(drop);
			con.createStatement().execute(drop2);
		});

		status.setDbversion(43);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step42() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from ANSWERS where Key_name = 'v_ft_idx'";
		final String createIndex = "ALTER TABLE ANSWERS ADD FULLTEXT INDEX v_ft_idx (VALUE);";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createIndex).executeUpdate();
		}

		status.setDbversion(42);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step41() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from ANSWERS_SET where Key_name = 'INX_RESPONDER_EMAIL'";
		final String createkIndex = "ALTER TABLE ANSWERS_SET	ADD INDEX INX_RESPONDER_EMAIL (RESPONDER_EMAIL);";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createkIndex).executeUpdate();
		}

		status.setDbversion(41);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step40() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String checkIndexExists = "show index from ELEMENTS where Key_name = 'IDX_ELEMENT_UID'";
		final String createkIndex = "CREATE INDEX IDX_ELEMENT_UID on ELEMENTS (ELEM_UID)";

		SQLQuery query = session.createSQLQuery(checkIndexExists);
		if (query.list().size() <= 0) {
			session.createSQLQuery(createkIndex).executeUpdate();
		}

		status.setDbversion(40);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step39() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String event = "ALTER EVENT SUNC_MV_SURVEYS_NUMBERPUBLISHEDANSWERS ON SCHEDULE EVERY 5 MINUTE DO "
				+ "BEGIN " + "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED; "
				+ "CREATE TABLE IF NOT EXISTS MV_SURVEYS_NUMBERPUBLISHEDANSWERS AS SELECT 1 AS DUMMY ; "
				+ "DROP TABLE IF EXISTS MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW; "
				+ "CREATE TABLE IF NOT EXISTS MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW AS "
				+ "SELECT SURVEYS_NUMBERPUBLISHEDANSWERS.*,NOW() MW_TIMESTAMP FROM SURVEYS_NUMBERPUBLISHEDANSWERS; "
				+ "ALTER TABLE MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW ADD INDEX MV_SURVEYS_IND (SURVEYUID, PUBLISHEDANSWERS); "
				+ "RENAME TABLE MV_SURVEYS_NUMBERPUBLISHEDANSWERS TO MV_TEMP_TABLE, MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW TO MV_SURVEYS_NUMBERPUBLISHEDANSWERS, "
				+ "MV_TEMP_TABLE TO MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW; "
				+ "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ; " + "END";

		session.doWork(con -> con.createStatement().execute(event));

		status.setDbversion(39);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step38() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		try {
			SQLQuery queryCreateIndex = session.createSQLQuery("ALTER TABLE ELEMENTS_FILES DROP INDEX files_FILE_ID;");
			queryCreateIndex.executeUpdate();
		} catch (Exception e) {
			// the index only exists for older installations
		}

		status.setDbversion(38);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step37() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery queryCreateIndex = session
				.createSQLQuery("ALTER TABLE ANSWERS_SET ADD INDEX INX_ANSWER_SET_INVID (ANSWER_SET_INVID)");
		queryCreateIndex.executeUpdate();
		status.setDbversion(37);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step36() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		String existing = settingsService.get("227ActivityEnabled");
		if (existing == null) {
			Setting s = new Setting();
			s.setKey("227ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);
		}

		status.setDbversion(36);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step35() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();
		if (!isOss()) {
			Skin standardskin = skinService.get(1);

			Skin copy = new Skin();
			copy.setName("Official EC Skin");
			copy.setOwner(standardskin.getOwner());
			copy.setIsPublic(true);
			copy.setUpdateDate(new Date());
			copy.getElements().clear();
			for (SkinElement element : standardskin.getElements()) {
				copy.getElements().add(element.copy());
			}
			skinService.add(copy);
		}
		status.setDbversion(35);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step34() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		Setting enabled = new Setting();
		enabled.setKey(Setting.ActivityLoggingEnabled);
		enabled.setValue("false");
		enabled.setFormat("true / false");
		session.saveOrUpdate(enabled);

		List<Integer> ids = Setting.ActivityLoggingIds();
		for (Integer i : ids) {
			Setting s = new Setting();
			s.setKey(i + "ActivityEnabled");
			s.setValue("true");
			s.setFormat("true / false");
			session.saveOrUpdate(s);
		}

		status.setDbversion(34);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step33() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("ALTER TABLE ATTRIBUTE MODIFY ATTRIBUTE.ATTRIBUTE_VALUE LONGTEXT");
		query.executeUpdate();

		// create indexes
		SQLQuery queryCreateIndex = session
				.createSQLQuery("CREATE INDEX INX_DEP_ORG ON ECASUSERS(USER_DEPARTMENT ASC, USER_ORGANISATION ASC)");
		queryCreateIndex.executeUpdate();

		if (showecas.equalsIgnoreCase("true") && !isCasOss()) {
			System.out.println("Start CopyEcas");
			domaintWorker.run();
			copyEcasData();
		}
		// delete duplicates
		deleteDuplicateRows("DEPARTMENTS", "DEP_ID", "NAME", "DOMAIN_CODE");
		// put constraint

		status.setDbversion(33);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step32() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("ALTER TABLE Statistics_totalsPercent MODIFY totalsPercent DOUBLE");
		query.executeUpdate();

		query = session
				.createSQLQuery("ALTER TABLE Statistics_requestedRecordsPercent MODIFY requestedRecordsPercent DOUBLE");
		query.executeUpdate();

		status.setDbversion(32);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step31() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		Setting enabled = new Setting();
		enabled.setKey(Setting.LDAPsync2Enabled);
		enabled.setValue("true");
		enabled.setFormat("true / false");
		session.saveOrUpdate(enabled);

		Setting frequency = new Setting();
		frequency.setKey(Setting.LDAPsync2Frequency);
		frequency.setValue("1w");
		frequency.setFormat("number followed by 'd' or 'w' (days or weeks)");
		session.saveOrUpdate(frequency);

		Setting start = new Setting();
		start.setKey(Setting.LDAPsync2Start);
		start.setValue("01/01/2014 23:30:00");
		start.setFormat("dd/MM/yyyy HH:mm:ss");
		session.saveOrUpdate(start);

		Setting time = new Setting();
		time.setKey(Setting.LDAPsync2Time);
		time.setValue("23:30");
		time.setFormat("HH:mm");
		session.saveOrUpdate(time);

		status.setDbversion(31);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step30() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String index = "ALTER TABLE INVITATIONS DROP INDEX inv_group;";
		final String index2 = "CREATE INDEX inv_group ON INVITATIONS(PARTICIPATIONGROUP_ID ASC, INV_DEACTIVATED ASC);";

		session.doWork(con -> {
			try {
				con.createStatement().execute(index);
			} catch (Exception e) {
				// index might not exist in all enviroments
			}
			con.createStatement().execute(index2);
		});

		status.setDbversion(30);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step29() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("ALTER TABLE ResultFilter_filterValues MODIFY filterValues LONGTEXT");
		query.executeUpdate();

		status.setDbversion(29);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step28(ServletContext servletContext) throws IOException {
		// there have been changes in that file so it needs to be overridden

		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		InputStream is = servletContext.getResourceAsStream("/WEB-INF/classes/antisamy-esapi.xml");
		FileOutputStream fos = new FileOutputStream(new java.io.File(fileDir + "antisamy-esapi.xml"));
		IOUtils.copy(is, fos);

		status.setDbversion(28);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step27(ServletContext servletContext) throws IOException {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		InputStream is = servletContext.getResourceAsStream("/WEB-INF/classes/antisamy-esapi.xml");
		FileOutputStream fos = new FileOutputStream(new java.io.File(fileDir + "antisamy-esapi.xml"));
		IOUtils.copy(is, fos);

		status.setDbversion(27);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step26() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("UPDATE ELEMENTS SET MINNUMBER = null WHERE MINNUMBER = 0;");
		query.executeUpdate();

		query = session.createSQLQuery("UPDATE ELEMENTS SET MAXNUMBER = null WHERE MAXNUMBER = 0;");
		query.executeUpdate();

		status.setDbversion(26);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step25() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String index = "create index idx_quid_v_d on ANSWERS(QUESTION_UID, VALUE(10), ANSWER_ISDRAFT);";
		session.doWork(con -> con.createStatement().execute(index));

		status.setDbversion(25);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step24() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		Setting enabled = new Setting();
		enabled.setKey(Setting.LDAPsyncEnabled);
		enabled.setValue("true");
		enabled.setFormat("true / false");
		session.saveOrUpdate(enabled);

		Setting frequency = new Setting();
		frequency.setKey(Setting.LDAPsyncFrequency);
		frequency.setValue("1d");
		frequency.setFormat("number followed by 'd' or 'w' (days or weeks)");
		session.saveOrUpdate(frequency);

		Setting start = new Setting();
		start.setKey(Setting.LDAPsyncStart);
		start.setValue("01/01/2014 23:00:00");
		start.setFormat("dd/MM/yyyy HH:mm:ss");
		session.saveOrUpdate(start);

		Setting time = new Setting();
		time.setKey(Setting.LDAPsyncTime);
		time.setValue("23:00");
		time.setFormat("HH:mm");
		session.saveOrUpdate(time);

		status.setDbversion(24);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step23() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		MessageType confirmation = new MessageType();
		confirmation.setCriticality(1);
		confirmation.setDefaultTime(20);
		confirmation.setIcon("check.png");
		confirmation.setLabel("Confirmation");
		confirmation.setCss("alert-success");
		session.saveOrUpdate(confirmation);

		MessageType information = new MessageType();
		information.setCriticality(2);
		information.setDefaultTime(20);
		information.setIcon("info.png");
		information.setLabel("Information");
		information.setCss("alert-info");
		session.saveOrUpdate(information);

		MessageType warning = new MessageType();
		warning.setCriticality(3);
		warning.setDefaultTime(0);
		warning.setIcon("warning.png");
		warning.setLabel("Warning");
		warning.setCss("alert-danger");
		session.saveOrUpdate(warning);

		status.setDbversion(23);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step22() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String index = "create index idx_q_v_d on ANSWERS(QUESTION_ID, VALUE(10), ANSWER_ISDRAFT);";
		session.doWork(con -> con.createStatement().execute(index));

		status.setDbversion(22);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step21() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String triggers1 = "DROP TRIGGER IF EXISTS update_invitations1;";
		final String triggers2 = "DROP TRIGGER IF EXISTS update_invitations2;";
		final String triggers3 = "DROP TRIGGER IF EXISTS update_invitations3;";
		final String triggers4 = "DROP TRIGGER IF EXISTS update_survey_replies1;";
		final String triggers5 = "DROP TRIGGER IF EXISTS update_survey_replies2;";
		final String triggers6 = "DROP TRIGGER IF EXISTS update_live_statistics1;";
		final String triggers7 = "DROP TRIGGER IF EXISTS check_number_invitations;";

		session.doWork(con -> {
			con.createStatement().execute(triggers1);
			con.createStatement().execute(triggers2);
			con.createStatement().execute(triggers3);
			con.createStatement().execute(triggers4);
			con.createStatement().execute(triggers5);
			con.createStatement().execute(triggers6);
			con.createStatement().execute(triggers7);
		});

		status.setDbversion(21);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step20() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		try {

			logger.info("STARTING STEP 20 MV_SURVEYS_NUMBERPUBLISHEDANSWERS");
			final String event = "CREATE EVENT IF NOT EXISTS SUNC_MV_SURVEYS_NUMBERPUBLISHEDANSWERS ON SCHEDULE EVERY 5 MINUTE DO "
					+ "BEGIN " + "CREATE TABLE IF NOT EXISTS MV_SURVEYS_NUMBERPUBLISHEDANSWERS AS SELECT 1 AS DUMMY ; "
					+ "DROP TABLE IF EXISTS MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW; "
					+ "CREATE TABLE IF NOT EXISTS MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW AS "
					+ "SELECT SURVEYS_NUMBERPUBLISHEDANSWERS.*,NOW() MW_TIMESTAMP FROM SURVEYS_NUMBERPUBLISHEDANSWERS; "
					+ "ALTER TABLE MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW ADD INDEX MV_SURVEYS_IND (SURVEYUID, PUBLISHEDANSWERS); "
					+ "RENAME TABLE MV_SURVEYS_NUMBERPUBLISHEDANSWERS TO MV_TEMP_TABLE, MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW TO MV_SURVEYS_NUMBERPUBLISHEDANSWERS, "
					+ "MV_TEMP_TABLE TO MV_SURVEYS_NUMBERPUBLISHEDANSWERS_NEW; " + "END";

			session.doWork(con -> con.createStatement().execute(event));

			status.setDbversion(20);
			session.saveOrUpdate(status);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@Transactional
	public void step19() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String view = "CREATE OR REPLACE VIEW SURVEYS_NUMBERPUBLISHEDANSWERS AS "
				+ "SELECT s.SURVEY_UID AS SURVEYUID, COUNT(ANSWERS_SET.ANSWER_SET_ID) AS PUBLISHEDANSWERS "
				+ "FROM ANSWERS_SET ,SURVEYS s "
				+ "WHERE ANSWERS_SET.ISDRAFT = 0 AND ANSWERS_SET.survey_Id =s.survey_id AND s.ISDRAFT = 0 group by s.SURVEY_UID";

		session.doWork(con -> con.createStatement().execute(view));

		status.setDbversion(19);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step18() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String index1 = "ALTER TABLE ANSWERS ADD INDEX PA_UID_IDX (PA_UID ASC, PA_ID ASC, AS_ID ASC);";
		final String index2 = "ALTER TABLE ANSWERS ADD INDEX Q_UID_IDX (QUESTION_UID ASC, QUESTION_ID ASC, AS_ID ASC);";

		session.doWork(con -> {
			con.createStatement().execute(index1);
			con.createStatement().execute(index2);
		});

		status.setDbversion(18);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step17() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("UPDATE ELEMENTS SET SOURCE_ID = ID WHERE SOURCE_ID IS NULL");
		query.executeUpdate();

		query = session.createSQLQuery("UPDATE ELEMENTS SET ELEM_UID = SOURCE_ID");
		query.executeUpdate();

		status.setDbversion(17);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step16() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		final String trigger = "CREATE TRIGGER check_number_invitations " + "BEFORE INSERT ON INVITATIONS "
				+ "FOR EACH ROW BEGIN " + " DECLARE msg varchar(255); " + " DECLARE count INT; "
				+ " SELECT PARTICIPATION_INVITED FROM PARTICIPANTS WHERE PARTICIPATION_ID = NEW.PARTICIPATIONGROUP_ID INTO count; "
				+ " IF (count>999999) " + " THEN "
				+ "	SET msg = 'maximum number of invitations per guestlist exceeded'; "
				+ "	SIGNAL sqlstate '45000' SET message_text = msg; " + " END IF;  " + "END";

		session.doWork(con -> {
			con.createStatement().execute("DROP TRIGGER IF EXISTS check_number_invitations");
			con.createStatement().execute(trigger);
		});

		status.setDbversion(16);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step15() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session
				.createSQLQuery("UPDATE INVITATIONS SET INV_DEACTIVATED = 0 WHERE INV_DEACTIVATED IS NULL");
		query.executeUpdate();

		status.setDbversion(15);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step12() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery(
				"UPDATE PARTICIPANTS p SET PARTICIPATION_SURVEY_UID = (SELECT SURVEY_UID FROM SURVEYS WHERE SURVEY_ID = p.PARTICIPATION_SURVEY_ID)");
		query.executeUpdate();

		status.setDbversion(12);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step11() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery(
				"UPDATE SURVEYS s SET s.SURVEY_UID = UUID() WHERE s.SURVEY_UID is null or s.SURVEY_UID = '' AND s.ISDRAFT = 1");
		query.executeUpdate();

		query = session.createSQLQuery(
				"UPDATE SURVEYS s INNER JOIN (SELECT SURVEY_UID, SURVEYNAME FROM SURVEYS s2 WHERE s2.ISDRAFT = 1) as s2 SET s.SURVEY_UID = s2.SURVEY_UID WHERE s.SURVEY_UID is null or s.SURVEY_UID = '' AND s.ISDRAFT = 0 AND s2.SURVEYNAME = s.SURVEYNAME");
		query.executeUpdate();

		status.setDbversion(11);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step10() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("UPDATE WEBSERVICETASK SET WST_COUNTER = 0 WHERE WST_COUNTER IS NULL");
		query.executeUpdate();

		status.setDbversion(10);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step8() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("ALTER TABLE PARTICIPANTS MODIFY PARTICIPANTS.TEMPL1 LONGTEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE PARTICIPANTS MODIFY PARTICIPANTS.TEMPL2 LONGTEXT");
		query.executeUpdate();

		status.setDbversion(8);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step7() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("UPDATE EXPORTS SET EXPORT_VALID = 0 WHERE EXPORT_VALID IS NULL");
		query.executeUpdate();

		status.setDbversion(7);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step6() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("ALTER TABLE ELEMENTS MODIFY ELEMENTS.QHELP LONGTEXT");
		query.executeUpdate();

		status.setDbversion(6);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step5() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("UPDATE SURVEYS SET DBVERSION = 0 WHERE DBVERSION IS NULL");
		query.executeUpdate();

		status.setDbversion(5);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step4() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("ALTER TABLE ANSWERS MODIFY ANSWERS.VALUE LONGTEXT");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE SURACCESS MODIFY ACCESS_DEPARTMENT VARCHAR(255)");
		query.executeUpdate();

		status.setDbversion(4);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step2() {
		Session session = sessionFactory.getCurrentSession();
		Status status = getStatus();

		SQLQuery query = session.createSQLQuery("ALTER TABLE SURVEYS MODIFY SURVEY_START_DATE DATETIME");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE SURVEYS MODIFY SURVEY_END_DATE DATETIME");
		query.executeUpdate();

		query = session.createSQLQuery("ALTER TABLE TRANSLATION MODIFY LABEL LONGTEXT");
		query.executeUpdate();

		status.setDbversion(2);
		session.saveOrUpdate(status);
	}

	@Transactional
	public void step1() {
		Session session = sessionFactory.getCurrentSession();

		SQLQuery query = session.createSQLQuery("UPDATE SURVEYS SET NOTIFYALL = false WHERE NOTIFYALL IS NULL");
		query.executeUpdate();

		query = session.createSQLQuery("UPDATE SURVEYS SET NOTIFIED = false WHERE NOTIFIED IS NULL");
		query.executeUpdate();

		query = session.createSQLQuery("UPDATE SURVEYS SET ACTIVE = true WHERE ACTIVE IS NULL");
		query.executeUpdate();

		query = session.createSQLQuery("UPDATE ELEMENTS SET QATT = true WHERE QATT IS NULL");
		query.executeUpdate();

		query = session.createSQLQuery("UPDATE ELEMENTS SET QOPTIONAL = true WHERE QOPTIONAL IS NULL");
		query.executeUpdate();

		Status status = new Status();
		status.setDbversion(1);
		status.setUpdateDate(new Date());
		session.saveOrUpdate(status);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void copyEcasData() {

		Session session = sessionFactory.getCurrentSession();
		// copy data
		SQLQuery queryEcasData = session.createSQLQuery(
				"SELECT DISTINCT USER_DEPARTMENT, USER_ORGANISATION FROM ECASUSERS WHERE USER_DEPARTMENT IS NOT NULL AND USER_ORGANISATION IS NOT NULL AND USER_DEPARTMENT <> '' ORDER BY USER_DEPARTMENT, USER_ORGANISATION");

		@SuppressWarnings("unchecked")
		List<Object> deparmentDomainList = queryEcasData.list();
		HashMap<String, String> deparmentDomainMap = new HashMap<>();
		for (Object item : deparmentDomainList) {
			Object[] a = (Object[]) item;
			deparmentDomainMap.put((String) a[0], a[1].toString());
		}

		Query queryDeparementData = session.createQuery("SELECT DISTINCT d.name FROM Department d");
		@SuppressWarnings("unchecked")
		List<String> existingdepartments = queryDeparementData.list();
		Query updateQuery = session
				.createQuery("update Department d  set  d.domainCode = :domainCode where  d.name = :department");
		for (String department : existingdepartments) {
			if (deparmentDomainMap.containsKey(department)) {
				updateQuery.setString("department", department);
				updateQuery.setString("domainCode", deparmentDomainMap.get(department));
				updateQuery.executeUpdate();
			}

		}

		SQLQuery deleteQuery = session.createSQLQuery(
				"DELETE FROM DEPARTMENTS WHERE  DEPARTMENTS.NAME IS NULL OR DEPARTMENTS.DOMAIN_CODE IS NULL");
		deleteQuery.executeUpdate();

		SQLQuery insertEcasGroupQuery = session.createSQLQuery(
				"INSERT INTO  ECASGROUPS SELECT ECASUSERS.USER_ID, REPLACE(ECASUSERS.USER_ORGANISATION,'eu.europa.','') FROM ECASUSERS WHERE ECASUSERS.USER_ORGANISATION <> 'external'");
		insertEcasGroupQuery.executeUpdate();

	}

	@Transactional
	public Date getLastLDAPSynchronizationDate() {
		Session session = sessionFactory.getCurrentSession();

		Query statusQuery = session.createQuery("FROM Status");
		@SuppressWarnings("unchecked")
		List<Status> states = statusQuery.setReadOnly(true).list();

		if (states.size() > 0) {
			return states.get(0).getLastLDAPSynchronizationDate();
		}

		return null;
	}

	@Transactional
	public void saveLastLDAPSynchronizationDate(Date syncDate) {
		Session session = sessionFactory.getCurrentSession();

		Query statusQuery = session.createQuery("FROM Status");
		@SuppressWarnings("unchecked")
		List<Status> states = statusQuery.list();

		if (states.size() > 0) {
			Status status = states.get(0);
			session.setReadOnly(status, false);
			status.setLastLDAPSynchronizationDate(syncDate);
			session.saveOrUpdate(status);
		}
	}

	@Transactional
	public Date getLastLDAPSynchronization2Date() {
		Session session = sessionFactory.getCurrentSession();

		Query statusQuery = session.createQuery("FROM Status");
		@SuppressWarnings("unchecked")
		List<Status> states = statusQuery.setReadOnly(true).list();

		if (states.size() > 0) {
			return states.get(0).getLastLDAPSynchronization2Date();
		}

		return null;
	}

	@Transactional
	public void saveLastLDAPSynchronization2Date(Date syncDate) {
		Session session = sessionFactory.getCurrentSession();

		Query statusQuery = session.createQuery("FROM Status");
		@SuppressWarnings("unchecked")
		List<Status> states = statusQuery.list();

		if (states.size() > 0) {
			Status status = states.get(0);
			session.setReadOnly(status, false);
			status.setLastLDAPSynchronization2Date(syncDate);
			session.saveOrUpdate(status);
		}
	}

	@Transactional
	public void deleteDuplicateRows(String tableName, String idColumnName, String... valueColumns) {
		int length = valueColumns.length;
		if (length == 0) {
			throw new IllegalArgumentException("please specify valueColumns.");
		}

		String sqlQueryString = buildSelectQuery(tableName, idColumnName, valueColumns);
		String sqlDeleteString = buildDeleteQuery(tableName, idColumnName, valueColumns);

		Session session = sessionFactory.getCurrentSession();

		SQLQuery selectQuery = session.createSQLQuery(sqlQueryString);

		List<?> duplicateRows = selectQuery.list();

		SQLQuery deleteQuery = session.createSQLQuery(sqlDeleteString);

		if (duplicateRows.size() > 0) {
			for (Object row : duplicateRows) {
				Object[] a = (Object[]) row;
				for (int i = 0; i < length; i++) {
					deleteQuery.setString(valueColumns[i], a[i].toString());
				}
				deleteQuery.setInteger(idColumnName, (Integer) a[length]);
				deleteQuery.executeUpdate();
			}
		}
	}

	private String buildSelectQuery(String tableName, String idColumnName, String[] valueColumns) {
		StringBuilder result = new StringBuilder("SELECT ");
		for (String valueColumn : valueColumns) {
			result.append(valueColumn).append(", ");
		}
		result.append(" MIN(").append(idColumnName).append(")").append(" MIN_ID ,");
		result.append(" COUNT(*) COuNT ");
		result.append(" FROM ").append(tableName).append(" ");
		result.append(" GROUP BY ");
		int length = valueColumns.length;
		for (int i = 0; i < length; i++) {
			result.append(valueColumns[i]);
			if (i + 1 == length) {
				continue;
			} else {
				result.append(", ");
			}
		}

		result.append(" having count(*) > 1 ");

		return result.toString();
	}

	private String buildDeleteQuery(String tableName, String idColumnName, String[] valueColumns) {
		StringBuilder result = new StringBuilder("DELETE FROM  ");
		result.append(tableName);
		result.append(" WHERE ");

		for (String valueColumn : valueColumns) {
			result.append(valueColumn).append("= :").append(valueColumn).append(" AND ");
		}

		result.append(" ").append(idColumnName).append("= :").append(idColumnName);

		return result.toString();
	}

	public void createUniqueConstraint(String tableName, String constraintName, String... columns) {
		int length = columns.length;
		if (length == 0) {
			throw new IllegalArgumentException("please specify columns.");
		}
		try {
			String sqlQueryString = buildCreateUniqueContraint(tableName, constraintName, columns);
			Session session = sessionFactory.getCurrentSession();
			SQLQuery query = session.createSQLQuery(sqlQueryString);
			query.executeUpdate();
		} catch (SQLGrammarException e) {
			logger.error(e);
			// can happen if constraint already exists
		}

	}

	private String buildCreateUniqueContraint(String tableName, String constraintName, String... columns) {
		StringBuilder result = new StringBuilder("ALTER TABLE ");
		result.append(tableName).append(" ").append(" ADD CONSTRAINT ").append(constraintName).append(" UNIQUE ");
		result.append("(");
		boolean isFirstColumn = true;
		for (String valueColumn : columns) {
			if (isFirstColumn) {
				isFirstColumn = false;
				result.append(valueColumn);
			} else {
				result.append(",").append(valueColumn);
			}
		}
		result.append(")");
		return result.toString();
	}

	@Transactional
	public void deleteDuplicateRowsNoId(String tableName, String... valueColumns) {
		int length = valueColumns.length;
		if (length == 0) {
			throw new IllegalArgumentException("please specify valueColumns.");
		}

		String sqlQueryString = buildSelectQueryNoId(tableName, valueColumns);
		String baseSqlDeleteString = buildDeleteQueryNoId(tableName, valueColumns);

		Session session = sessionFactory.getCurrentSession();

		SQLQuery selectQuery = session.createSQLQuery(sqlQueryString);

		List<?> duplicateRows = selectQuery.list();

		if (duplicateRows.size() > 0) {
			for (Object row : duplicateRows) {
				Object[] a = (Object[]) row;
				String sqlDeleteString = baseSqlDeleteString + " LIMIT " + a[length];
				SQLQuery deleteQuery = session.createSQLQuery(sqlDeleteString);

				for (int i = 0; i < length; i++) {
					deleteQuery.setString(valueColumns[i], a[i].toString());
				}
				deleteQuery.executeUpdate();
			}
		}
	}

	private String buildSelectQueryNoId(String tableName, String[] valueColumns) {
		StringBuilder result = new StringBuilder("SELECT ");
		for (String valueColumn : valueColumns) {
			result.append(valueColumn).append(", ");
		}
		result.append(" COUNT(*) -1  COUNT ");
		result.append(" FROM ").append(tableName).append(" ");
		result.append(" GROUP BY ");
		int length = valueColumns.length;
		for (int i = 0; i < length; i++) {
			result.append(valueColumns[i]);
			if (i + 1 == length) {
				continue;
			} else {
				result.append(", ");
			}
		}

		result.append(" having count(*) > 1 ");

		return result.toString();
	}

	private String buildDeleteQueryNoId(String tableName, String[] valueColumns) {
		StringBuilder result = new StringBuilder("DELETE FROM  ");
		result.append(tableName);
		result.append(" WHERE ");
		boolean isFirstColumn = true;
		for (String valueColumn : valueColumns) {
			if (isFirstColumn) {
				isFirstColumn = false;
				result.append(valueColumn).append("= :").append(valueColumn);
			} else {
				result.append(" AND ").append(valueColumn).append("= :").append(valueColumn);
			}

		}

		return result.toString();
	}

	@Transactional
	public void createAnswerFullTextForOss() {

		if (!showecas.equalsIgnoreCase("true")) {
			logger.error("STARTING UPDATE FULL TEXT INDEX FOR ANSWER");
			Session session = sessionFactory.getCurrentSession();

			final String checkIndexExists = "show index from ANSWERS where Key_name = 'v_ft_idx'";
			final String createIndex = "ALTER TABLE ANSWERS ADD FULLTEXT INDEX v_ft_idx (VALUE);";

			SQLQuery query = session.createSQLQuery(checkIndexExists);
			if (query.list().size() <= 0) {
				logger.error("Special update full text not existing create them");
				session.createSQLQuery(createIndex).executeUpdate();
			}

		}
	}

	@Transactional
	public boolean tableExists(String tableName) {
		Session session = sessionFactory.getCurrentSession();
		String queryString = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + tableName + "' AND TABLE_SCHEMA = database();";
		SQLQuery query = session.createSQLQuery(queryString);
		return !query.list().isEmpty();
	}

}