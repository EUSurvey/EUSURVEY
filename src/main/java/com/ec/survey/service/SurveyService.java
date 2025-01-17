package com.ec.survey.service;

import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.exception.MessageException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.chargeback.MonthlyCharge;
import com.ec.survey.model.chargeback.OrganisationCharge;
import com.ec.survey.model.chargeback.PublishedSurvey;
import com.ec.survey.model.selfassessment.SACriterion;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.ReportingService.ToDo;
import com.ec.survey.tools.*;
import com.ec.survey.tools.activity.ActivityRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils;
import org.hibernate.Hibernate;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.NativeQuery;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service("surveyService")
public class SurveyService extends BasicService {

	private @Value("${publicsurveynotification}") String publicsurveynotification;
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${sender}") String sender;
	private @Value("${server.prefix}") String host;
	public @Value("${opc.notify}") String opcnotify;
	private @Value("${monitoring.recipient}") String monitoringEmail;
	private @Value("${opc.users}") String opcusers;
	private @Value("${opc.department:@null}") String opcdepartments;

	@Autowired
	protected SqlQueryService sqlQueryService;

	@Resource(name = "ldapService")
	protected LdapService ldapService;

	@Resource(name = "ldapDBService")
	protected LdapDBService ldapDBService;

	@Resource(name = "utilsService")
	protected UtilsService utilsService;

	@Transactional(readOnly = true)
	public int getNumberPublishedAnswersFromMaterializedView(String uid) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT npa.PUBLISHEDANSWERS FROM MV_SURVEYS_NUMBERPUBLISHEDANSWERS npa WHERE npa.SURVEYUID = :uid";
		NativeQuery query = session.createSQLQuery(sql);
		query.setParameter("uid", uid);
		List<?> res = (List<?>) query.list();
		if (!res.isEmpty())
			return ConversionTools.getValue(res.get(0));
		return 0;
	}

	private Map<Integer, Language> getLanguageMap() {
		return getLanguages().stream().collect(Collectors.toMap(Language::getId, l -> l));
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> loadSurveysfromDatabase(String sql, HashMap<String, Object> parameters,
			SqlPagination sqlPagination) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		NativeQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		return query.setFirstResult(sqlPagination.getFirstResult()).setMaxResults(sqlPagination.getMaxResult()).list();
	}

	@Transactional(readOnly = true)
	public List<Survey> getSurveys(SurveyFilter filter, SqlPagination sqlPagination) throws Exception {

		//No StringBuilder needed the Compiler handles this well
		String sql = "SELECT s.SURVEY_ID" + // 0
				" ,s.SURVEY_UID" +// 1
				" ,s.SURVEYNAME" +// 2
				" ,s.TITLE" +// 3
				" ,s.SURVEY_CREATED" +// 4
				" ,s.SURVEY_END_DATE" +// 5
				" ,s.SURVEY_START_DATE" +// 6
				" ,s.ISPUBLISHED" +// 7
				" ,s.LANGUAGE" +
				" ,npa.PUBLISHEDANSWERS as replies" +
				" ,s.ACTIVE" +
				" ,s.OWNER" +
				" ,s.CONTACT" +
				" ,(SELECT USER_LOGIN FROM USERS u WHERE u.USER_ID = s.OWNER) as ownerlogin" +
				" ,(SELECT USER_DISPLAYNAME FROM USERS u WHERE u.USER_ID = s.OWNER) as ownername" +
				" ,s.AUTOMATICPUBLISHING" +
				" ,s.CONTACTLABEL" +
				" ,s.SURVEYSECURITY" +
				" ,s.QUIZ" +
				" ,s.OPC" +
				" ,s.HASPENDINGCHANGES" +
				" ,s.DELPHI" +
				" ,s.ECF" +
				" ,s.EVOTE" +
				" ,s.SELFASSESSMENT" +
				" from SURVEYS s" +
				" LEFT JOIN MV_SURVEYS_NUMBERPUBLISHEDANSWERS npa on s.SURVEY_UID = npa.SURVEYUID" +
				" where s.ISDRAFT = 1 and (s.ARCHIVED = 0 or s.ARCHIVED is null) and (s.DELETED = 0 or s.DELETED is null)";
		HashMap<String, Object> parameters = new HashMap<>();
		sql += getSql(filter, parameters);

		List<Survey> surveys = new ArrayList<>();
		Map<Integer, Language> languageMap = getLanguageMap();
		for (Object[] row : loadSurveysfromDatabase(sql, parameters, sqlPagination)) {
			Survey survey = new Survey();
			int rowIndex = 0;
			survey.setId(ConversionTools.getValue(row[rowIndex++])); // 0

			survey.setUniqueId((String) row[rowIndex++]);// 1
			survey.setShortname((String) row[rowIndex++]);// 2
			survey.setTitle((String) row[rowIndex++]);// 3
			survey.setCreated((Date) row[rowIndex++]);// 4
			survey.setEnd((Date) row[rowIndex++]);// 5
			survey.setStart((Date) row[rowIndex++]);// 6
			survey.setIsPublished((Boolean) row[rowIndex++]);// 7
			survey.setLanguage(languageMap.get(ConversionTools.getValue(row[rowIndex++])));// 8

			int mainCount = ConversionTools.getValue(row[rowIndex++]);
			
			if (this.isReportingDatabaseEnabled()) {				
				survey.setNumberOfAnswerSetsPublished(this.reportingService.getCount(false, survey.getUniqueId()));
				if (survey.getNumberOfAnswerSetsPublished() == 0) {
					survey.setNumberOfAnswerSetsPublished(mainCount);// 9
				}
			} else {
				survey.setNumberOfAnswerSetsPublished(mainCount);// 9
			}

			survey.setIsActive((Boolean) row[rowIndex++]);// 10

			User user = new User();
			user.setId(ConversionTools.getValue(row[rowIndex++]));// 11
			survey.setContact((String) row[rowIndex++]);// 12

			user.setLogin((String) row[rowIndex++]);// 13
			user.setDisplayName((String) row[rowIndex++]);// 14
			survey.setOwner(user);

			survey.setAutomaticPublishing((Boolean) row[rowIndex++]);// 15
			survey.setContactLabel((String) row[rowIndex++]);// 16

			survey.setSecurity((String) row[rowIndex++]);// 17
			survey.setIsQuiz((Boolean) row[rowIndex++]);// 18
			survey.setIsOPC((Boolean) row[rowIndex++]);// 19
				
			survey.setHasPendingChanges((Boolean) row[rowIndex++]);// 19 or 20
			survey.setIsDelphi((Boolean) row[rowIndex++]);// 21
			survey.setIsECF((Boolean) row[rowIndex++]);
			survey.setIsEVote((Boolean) row[rowIndex++]);	
			survey.setIsSelfAssessment((Boolean) row[rowIndex]);	

			surveys.add(survey);
		}
		return surveys;
	}
	
	@Transactional(readOnly = true)
	public List<Survey> getSurveysForDashboard(SurveyFilter filter, SqlPagination sqlPagination, User u) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT s.SURVEY_ID");
		stringBuilder.append(" ,s.SURVEY_UID");
		stringBuilder.append(" ,s.SURVEYNAME");
		stringBuilder.append(" ,s.TITLE");
		stringBuilder.append(" ,s.AUTOMATICPUBLISHING");
		stringBuilder.append(" ,s.SURVEY_END_DATE");
		stringBuilder.append(" ,s.SURVEY_START_DATE");
		stringBuilder.append(" ,s.ISPUBLISHED");
		stringBuilder.append(" ,s.LANGUAGE");
		stringBuilder.append(" ,s.EVOTE");
		
		if (!this.isReportingDatabaseEnabled() || filter.getSortKey().equalsIgnoreCase("REPLIES")) {
			stringBuilder.append(" ,npa.PUBLISHEDANSWERS as replies");
		}
		
		stringBuilder.append(" ,s.ACTIVE");
		stringBuilder.append(" ,s.OWNER");
		stringBuilder.append(" ,s.SURVEYSECURITY");
		stringBuilder.append(" from SURVEYS s");
		
		if (!this.isReportingDatabaseEnabled() || filter.getSortKey().equalsIgnoreCase("REPLIES")) {
			stringBuilder.append(" LEFT JOIN MV_SURVEYS_NUMBERPUBLISHEDANSWERS npa on s.SURVEY_UID = npa.SURVEYUID");
		}
		
		stringBuilder.append(
				" where s.ISDRAFT = 1 and (s.ARCHIVED = 0 or s.ARCHIVED is null) and (s.DELETED = 0 or s.DELETED is null)");

		String sql = stringBuilder.toString();
		HashMap<String, Object> parameters = new HashMap<>();
		
		sql +=  getSql(filter, parameters);

		List<Survey> surveys = new ArrayList<>();
		Map<Integer, Language> languageMap = getLanguageMap();
		for (Object[] row : loadSurveysfromDatabase(sql, parameters, sqlPagination)) {
			Survey survey = new Survey();
			int rowIndex = 0;
			survey.setId(ConversionTools.getValue(row[rowIndex++]));

			survey.setUniqueId((String) row[rowIndex++]);
			survey.setShortname((String) row[rowIndex++]);
			survey.setTitle((String) row[rowIndex++]);
			survey.setAutomaticPublishing((Boolean) row[rowIndex++]);
			survey.setEnd((Date) row[rowIndex++]);
			survey.setStart((Date) row[rowIndex++]);
			survey.setIsPublished((Boolean) row[rowIndex++]);
			survey.setLanguage(languageMap.get(ConversionTools.getValue(row[rowIndex++])));// 8
			survey.setIsEVote((Boolean) row[rowIndex++]);
			
			if (this.isReportingDatabaseEnabled() && !filter.getSortKey().equalsIgnoreCase("REPLIES")) {				
				survey.setNumberOfAnswerSetsPublished(this.reportingService.getCount(false, survey.getUniqueId()));				
			} else {
				int mainCount = ConversionTools.getValue(row[rowIndex++]);
				survey.setNumberOfAnswerSetsPublished(mainCount);
			}
			
			survey.setIsActive((Boolean) row[rowIndex++]);

			int userId = ConversionTools.getValue(row[rowIndex++]);
			
			User user = administrationService.getUser(userId);
			survey.setOwner(user);

			survey.setSecurity((String) row[rowIndex]);

			surveys.add(survey);
		}
		
		for (Survey survey : surveys) {
			survey.setTranslations(translationService.getTranslationLanguagesForSurvey(survey.getId(), false));
			survey.setCompleteTranslations(this.getCompletedTranslations(survey));
		}
		
		generateAccessInformation(surveys, u);
		
		return surveys;
	}
	
	@Transactional(readOnly = true)
	public List<Survey> getSurveysForSurveySearch(SurveyFilter filter, SqlPagination sqlPagination, boolean initDrafts, boolean initReports) throws Exception {
		if (filter.getSurveyTypes() != null && filter.getSurveyTypes().size() == 0) {
			return new ArrayList<>();
		}

		StringBuilder stringBuilder = new StringBuilder(1024);
		stringBuilder.append("SELECT s.SURVEY_ID");
		stringBuilder.append(", s.SURVEY_UID");
		stringBuilder.append(", s.SURVEYNAME");
		stringBuilder.append(", s.TITLE");
		stringBuilder.append(", s.OWNER");
		stringBuilder.append(", s.LANGUAGE");
		stringBuilder.append(", s.ORGANISATION");

		stringBuilder.append(", s.OPC");
		stringBuilder.append(", s.QUIZ");
		stringBuilder.append(", s.DELPHI");
		stringBuilder.append(", s.EVOTE");
		stringBuilder.append(", s.ECF");
		stringBuilder.append(", s.SELFASSESSMENT");

		if (!this.isReportingDatabaseEnabled() || filter.getSortKey().equalsIgnoreCase("REPLIES")) {
			stringBuilder.append(", npa.PUBLISHEDANSWERS as replies");// 7
		}
		
		stringBuilder.append(", s.SURVEY_DELETED");
		stringBuilder.append(", s.SURVEY_CREATED");
		stringBuilder.append(", s.FROZEN");
		
		if (initReports && filter.getSortKey().equalsIgnoreCase("reported")) {
			stringBuilder.append(", (SELECT COUNT(DISTINCT SURABUSE_ID) FROM SURABUSE WHERE SURABUSE_SURVEY = s.SURVEY_UID) as reported");// 15
		}

		stringBuilder.append(
				", (SELECT MIN(SURVEY_CREATED) FROM SURVEYS WHERE ISDRAFT = 0 AND SURVEY_UID = s.SURVEY_UID) as firstPublished");
		stringBuilder.append(
				", (SELECT MAX(SURVEY_CREATED) FROM SURVEYS WHERE ISDRAFT = 0 AND SURVEY_UID = s.SURVEY_UID) as published");

		stringBuilder.append(" from SURVEYS s");
		
		if (!this.isReportingDatabaseEnabled() || filter.getSortKey().equalsIgnoreCase("REPLIES")) {		
			stringBuilder.append(" LEFT JOIN MV_SURVEYS_NUMBERPUBLISHEDANSWERS npa on s.SURVEY_UID = npa.SURVEYUID");
		}
		
		stringBuilder.append(" where s.ISDRAFT = 1 AND ");
		String sql = stringBuilder.toString();

		if (filter.getSurveys() != null && filter.getSurveys().equalsIgnoreCase("ARCHIVED")) {
			sql += "(s.ARCHIVED = 1)";
		} else if (filter.getSurveys() != null && filter.getSurveys().equalsIgnoreCase("DELETED")) {
			sql += "(s.DELETED = 1)";
		} else if (filter.getSurveys() != null && filter.getSurveys().equalsIgnoreCase("REPORTED")) {
			sql += "(s.SURVEY_ID > 0)";
			// handled inside getSql
		} else {
			sql += "(s.ARCHIVED = 0 or s.ARCHIVED is null) and (s.DELETED = 0 or s.DELETED is null)";
		}

		HashMap<String, Object> parameters = new HashMap<>();
		sql += getSql(filter, parameters);

		List<Survey> surveys = new ArrayList<>();
		Map<Integer, Language> languageMap = getLanguageMap();
		for (Object[] row : loadSurveysfromDatabase(sql, parameters, sqlPagination)) {
			Survey survey = new Survey();
			int columnNum = 0;
			survey.setId(ConversionTools.getValue(row[columnNum++]));
			survey.setUniqueId((String) row[columnNum++]);
			survey.setShortname((String) row[columnNum++]);
			survey.setTitle((String) row[columnNum++]);

			int userId = ConversionTools.getValue(row[columnNum++]);
			User user = administrationService.getUser(userId);
			survey.setOwner(user);
			survey.setOrganisation(utilsService.getAllOrganisations(new Locale(languageMap.get(row[columnNum++]).toString())).get(row[columnNum++]));

			survey.setIsOPC((Boolean) row[columnNum++]);
			survey.setIsQuiz((Boolean) row[columnNum++]);
			survey.setIsDelphi((Boolean) row[columnNum++]);
			survey.setIsEVote((Boolean) row[columnNum++]);
			survey.setIsECF((Boolean) row[columnNum++]);
			survey.setIsSelfAssessment((Boolean) row[columnNum++]);

			if (this.isReportingDatabaseEnabled() && !filter.getSortKey().equalsIgnoreCase("REPLIES")) {
				survey.setNumberOfAnswerSetsPublished(this.reportingService.getCount(false, survey.getUniqueId()));
			} else {
				survey.setNumberOfAnswerSetsPublished(ConversionTools.getValue(row[columnNum++]));
			}

			survey.setDeleted((Date) row[columnNum++]);
			survey.setCreated((Date) row[columnNum++]);
			survey.setIsFrozen((Boolean) row[columnNum++]);
			
			initPublishedDates(survey);
			
			if (initReports) {
				initNumberOfReports(survey);
			}

			survey.setTitle(survey.cleanTitle());
			
			if (initDrafts) {
				survey.setNumberOfDrafts(answerService.getNumberOfDrafts(survey.getId()));
			}
			
			surveys.add(survey);
		}
				
		return surveys;
	}
	
	private void initPublishedDates(Survey survey) {
		Session session = sessionFactory.getCurrentSession();
		NativeQuery query = session.createSQLQuery("SELECT MIN(SURVEY_CREATED), MAX(SURVEY_CREATED) FROM SURVEYS WHERE ISDRAFT = 0 AND SURVEY_UID = :SURVEY_UID");
		
		@SuppressWarnings("unchecked")
		List<Object> datesList = query.setParameter("SURVEY_UID", survey.getUniqueId()).list();
		
		Object[] dates = (Object[]) datesList.get(0);
		
		survey.setFirstPublished((Date) dates[0]);
		survey.setPublished((Date) dates[1]);
	}
	
	private void initNumberOfReports(Survey survey) {
		Session session = sessionFactory.getCurrentSession();
		NativeQuery query = session.createSQLQuery("SELECT COUNT(DISTINCT SURABUSE_ID) FROM SURABUSE WHERE SURABUSE_SURVEY = :SURVEY_UID");
		
		Object count = query.setParameter("SURVEY_UID", survey.getUniqueId()).uniqueResult();
		
		survey.setNumberOfReports(ConversionTools.getValue(count));
	}	

	@Transactional(readOnly = true)
	public List<Survey> getSurveysIncludingTranslationLanguages(SurveyFilter filter, SqlPagination sqlPagination,
			boolean addInvitedAndDrafts, boolean addNumberOfReports) throws Exception {
		List<Survey> surveys = getSurveys(filter, sqlPagination);
		for (Survey survey : surveys) {
			survey.setTranslations(translationService.getTranslationLanguagesForSurvey(survey.getId(), false));
			survey.setCompleteTranslations(this.getCompletedTranslations(survey));

			if (addInvitedAndDrafts) {
				survey.setNumberOfInvitations(participationService.getNumberOfOpenInvitations(survey.getUniqueId()));
				survey.setNumberOfDrafts(answerService.getNumberOfDrafts(survey.getId()));
			} else {
				survey.setNumberOfInvitations(-1);
				survey.setNumberOfDrafts(-1);
			}

			if (addNumberOfReports) {
				survey.setNumberOfReports(surveyService.getAbuseReportsForSurvey(survey.getUniqueId()));
			}
		}
		return surveys;
	}

	@Transactional
	public List<Integer> getSurveysWithPrivilegesForUser(int userid) {
		Session session = sessionFactory.getCurrentSession();
		NativeQuery query = session.createSQLQuery("Select a.SURVEY FROM SURACCESS a WHERE a.ACCESS_USER = :id");

		@SuppressWarnings("rawtypes")
		List surveys = query.setParameter("id", userid).list();
		List<Integer> result = new ArrayList<>();

		for (Object o : surveys) {
			result.add(ConversionTools.getValue(o));
		}

		return result;
	}

	private List<Language> getCompletedTranslations(Survey survey) {
		return translationService.getTranslationsForSurvey(survey.getId(), false).stream()
				.filter(Translations::getActive).map(Translations::getLanguage).collect(toList());
	}

	public List<Survey> getSurveysIncludingPublicationDates(SurveyFilter filter, SqlPagination sqlPagination)
			throws Exception {
		StringBuilder stringBuilder = new StringBuilder(1024);
		stringBuilder.append("SELECT s.SURVEY_ID"); // 0
		stringBuilder.append(", s.SURVEY_UID"); // 1
		stringBuilder.append(", s.SURVEYNAME"); // 2
		stringBuilder.append(", s.TITLE"); // 3
		stringBuilder.append(", s.OWNER"); // 4
		stringBuilder.append(", (SELECT USER_LOGIN FROM USERS u WHERE u.USER_ID = s.OWNER) as ownerlogin"); // 5
		stringBuilder.append(", (SELECT USER_DISPLAYNAME FROM USERS u WHERE u.USER_ID = s.OWNER) as ownername");// 6
		stringBuilder.append(", s.SURVEYSECURITY");// 8
		stringBuilder.append(", s.ACTIVE");// 9
		stringBuilder.append(", s.FROZEN");// 10
		stringBuilder.append(
				", (SELECT MIN(SURVEY_CREATED) FROM SURVEYS WHERE ISDRAFT = 0 AND SURVEY_UID = s.SURVEY_UID) as firstPublished");// 11
		stringBuilder.append(
				", (SELECT MAX(SURVEY_CREATED) FROM SURVEYS WHERE ISDRAFT = 0 AND SURVEY_UID = s.SURVEY_UID) as published");// 12
		stringBuilder.append(" ,s.ISPUBLISHED");
		if (!this.isReportingDatabaseEnabled()) {
			stringBuilder.append(", npa.PUBLISHEDANSWERS as replies");// 7
		}
		stringBuilder.append(", s.SURVEY_DELETED");// 13
		stringBuilder.append(", s.SURVEY_CREATED");// 14
		stringBuilder.append(", (SELECT COUNT(DISTINCT SURABUSE_ID) FROM SURABUSE WHERE SURABUSE_SURVEY = s.SURVEY_UID) as reported");// 15
		stringBuilder.append(" from SURVEYS s");
		stringBuilder.append(" LEFT JOIN MV_SURVEYS_NUMBERPUBLISHEDANSWERS npa on s.SURVEY_UID = npa.SURVEYUID");
		stringBuilder.append(" where s.ISDRAFT = 1 AND ");
		String sql = stringBuilder.toString();

		if (filter.getSurveys() != null && filter.getSurveys().equalsIgnoreCase("ARCHIVED")) {
			sql += "(s.ARCHIVED = 1)";
		} else if (filter.getSurveys() != null && filter.getSurveys().equalsIgnoreCase("DELETED")) {
			sql += "(s.DELETED = 1)";
		} else if (filter.getSurveys() != null && filter.getSurveys().equalsIgnoreCase("REPORTED")) {
			sql += "(s.SURVEY_ID > 0)";
			// handled inside getSql
		} else {
			sql += "(s.ARCHIVED = 0 or s.ARCHIVED is null) and (s.DELETED = 0 or s.DELETED is null)";
		}

		HashMap<String, Object> parameters = new HashMap<>();
		sql += getSql(filter, parameters);

		List<Survey> surveys = new ArrayList<>();
		for (Object[] row : loadSurveysfromDatabase(sql, parameters, sqlPagination)) {
			Survey survey = new Survey();
			int columnNum = 0;
			survey.setId(ConversionTools.getValue(row[columnNum++]));
			survey.setUniqueId((String) row[columnNum++]);
			survey.setShortname((String) row[columnNum++]);
			survey.setTitle((String) row[columnNum++]);

			User user = new User();
			user.setId(ConversionTools.getValue(row[columnNum++]));
			user.setLogin((String) row[columnNum++]);
			user.setDisplayName((String) row[columnNum++]);
			survey.setOwner(user);

			survey.setSecurity((String) row[columnNum++]);
			survey.setIsActive((Boolean) row[columnNum++]);
			survey.setIsFrozen((Boolean) row[columnNum++]);

			survey.setFirstPublished((Date) row[columnNum++]);
			survey.setPublished((Date) row[columnNum++]);

			survey.setIsPublished((Boolean) row[columnNum++]);

			if (this.isReportingDatabaseEnabled()) {
				survey.setNumberOfAnswerSetsPublished(this.reportingService.getCount(false, survey.getUniqueId()));
			} else {
				survey.setNumberOfAnswerSetsPublished(ConversionTools.getValue(row[columnNum++]));
			}

			survey.setDeleted((Date) row[columnNum++]);
			survey.setCreated((Date) row[columnNum++]);

			survey.setNumberOfReports(ConversionTools.getValue(row[columnNum]));

			surveys.add(survey);
		}

		return surveys;
	}

	@Transactional(readOnly = true)
	public List<Survey> getPopularSurveys(SurveyFilter filter) throws Exception {
		filter.setSortKey("replies");
		SqlPagination sqlPagination = new SqlPagination(1, 5);
		return getSurveys(filter, sqlPagination);
	}

	private String getSql(SurveyFilter filter, HashMap<String, Object> oQueryParameters) {

		StringBuilder sql = new StringBuilder();

		if (filter == null) {
			return sql.toString();
		}

		if (filter.getUid() != null && filter.getUid().length() > 0) {
			sql.append(" AND s.SURVEY_UID like :uid");
			oQueryParameters.put("uid", "%" + filter.getUid().trim() + "%");
		}

		if (filter.getShortname() != null && filter.getShortname().length() > 0) {
			sql.append(" AND s.SURVEYNAME like :shortname");
			oQueryParameters.put(Constants.SHORTNAME, "%" + filter.getShortname().trim() + "%");
		}

		if (filter.getTitle() != null && filter.getTitle().length() > 0) {
			sql.append(" AND s.TITLE like :title");
			oQueryParameters.put("title", "%" + filter.getTitle().trim() + "%");
		}

		if (filter.getAccess() != null && filter.getAccess().length() > 0) {
			sql.append(" AND s.SURVEYSECURITY like :access");
			oQueryParameters.put("access", filter.getAccess());
		}

		if (filter.getType() != null && filter.getType().length() > 0 && !filter.getType().equalsIgnoreCase("all")) {

			switch (filter.getType()) {
				case "quiz":
					sql.append(" AND s.QUIZ = 1");
					break;
				case "standard":
					sql.append(" AND s.QUIZ = 0 AND s.OPC = 0");
					break;
				case "brp":
					sql.append(" AND s.OPC = 1");
					break;
				case "delphi":
					sql.append(" AND s.DELPHI = 1");
					break;
			}
		}

		if (filter.getOrganisations() != null && !filter.getOrganisations().isEmpty()) {			
			boolean firstLoop = true;			
			for (String organisation : filter.getOrganisations()) {
				
				if (firstLoop) {
					sql.append(" AND (");
					firstLoop = false;
				} else {
					sql.append(" OR");
				}
								
				if (organisation.startsWith("deleted")) {
					sql.append(" s.ORGANISATION = '" + organisation.replace("deleted", "") + "'");
				} else if (organisation.startsWith("frozen")) {
					sql.append(" s.ORGANISATION = '" + organisation.replace("frozen", "") + "'");
				} else if (organisation.startsWith("reported")) {
					sql.append(" s.ORGANISATION = '" + organisation.replace("reported", "") + "'");
				} else {
					sql.append(" s.ORGANISATION = '" + organisation + "'");
				}
			};
			
			sql.append(")");
		}

		if (filter.getSurveyTypes() != null && !filter.areAllSurveyTypes() && !filter.getSurveyTypes().isEmpty()) {
			boolean firstLoop = true;
			for (String type : filter.getSurveyTypes()) {
				if (firstLoop) {
					sql.append(" AND (");
					firstLoop = false;
				} else {
					sql.append(" OR");
				}

				switch (type) {
					case "standard":
						sql.append(" (s.QUIZ = 0 AND s.ECF = 0 AND s.EVOTE = 0 AND s.OPC = 0 AND s.DELPHI = 0 AND s.SELFASSESSMENT = 0) ");
						break;
					default:
						sql.append(" s." + type.toUpperCase() + " = 1");
						break;
				}
			}
			if (!filter.getSurveyTypes().isEmpty()) {
				sql.append(")");
			}
		}

		if (filter.getStatus() != null) {
			boolean unpublished = filter.getStatus().contains("Unpublished");
			boolean published = filter.getStatus().contains("Published");

			if (published && unpublished) {
				// return everything
			} else if (published) {
				sql.append(" AND s.ISPUBLISHED = 1 AND s.ACTIVE = 1");
			} else if (unpublished) {
				sql.append(" AND s.ACTIVE = 0");
			}
		}

		if (filter.getGeneratedFrom() != null) {
			sql.append(" AND s.SURVEY_CREATED >= :generatedFrom");
			oQueryParameters.put("generatedFrom", filter.getGeneratedFrom());
		}

		if (filter.getGeneratedTo() != null) {
			sql.append(" AND s.SURVEY_CREATED < :generatedTo");
			oQueryParameters.put("generatedTo", Tools.getFollowingDay(filter.getGeneratedTo()));
		}

		if (filter.getDeletedFrom() != null) {
			sql.append(" AND s.SURVEY_DELETED >= :deletedFrom");
			oQueryParameters.put("deletedFrom", filter.getDeletedFrom());
		}

		if (filter.getDeletedTo() != null) {
			sql.append(" AND s.SURVEY_DELETED < :deletedTo");
			oQueryParameters.put("deletedTo", Tools.getFollowingDay(filter.getDeletedTo()));
		}

		if (filter.getStartFrom() != null) {
			sql.append(" AND s.SURVEY_START_DATE >= :startFrom");
			oQueryParameters.put("startFrom", filter.getStartFrom());
		}

		if (filter.getStartTo() != null) {
			sql.append(" AND s.SURVEY_START_DATE < :startTo");
			oQueryParameters.put("startTo", Tools.getFollowingDay(filter.getStartTo()));
		}

		if (filter.getEndFrom() != null) {
			sql.append(" AND s.SURVEY_END_DATE >= :endFrom");
			oQueryParameters.put("endFrom", filter.getEndFrom());
		}

		if (filter.getEndTo() != null) {
			sql.append(" AND s.SURVEY_END_DATE < :endTo");
			oQueryParameters.put("endTo", Tools.getFollowingDay(filter.getEndTo()));
		}

		if (filter.getUser() == null) {
			// Searching public surveys
			sql.append(" AND s.LISTFORM = 1 AND s.LISTFORMVALIDATED = 1 AND s.ISPUBLISHED = true AND s.ACTIVE = true AND (s.SURVEYSECURITY = 'open' or s.SURVEYSECURITY = 'openanonymous') AND (s.SURVEY_END_DATE IS NULL OR s.SURVEY_END_DATE > :now)");
			oQueryParameters.put("now", new Date());
		} else {
			if (filter.getOwner() != null && filter.getOwner().length() > 0) {
				sql.append(" AND (s.OWNER in (SELECT USER_ID FROM USERS WHERE USER_LOGIN LIKE :ownername OR USER_DISPLAYNAME LIKE :ownername))");
				String ownerName = "%" + filter.getOwner().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
				oQueryParameters.put("ownername", ownerName);
			}

			if (filter.getSelector() != null && filter.getSelector().equalsIgnoreCase("any") && filter.getUser().getGlobalPrivileges().get(GlobalPrivilege.FormManagement) > 1) {
				// form administrators can see all surveys
			} else if (filter.getSelector() != null && filter.getSelector().equalsIgnoreCase("my")) {
				// Overriding owner with current user
				sql.append(" AND (s.OWNER = :ownerId)");
				oQueryParameters.put("ownerId", filter.getUser().getId());
			} else if (filter.getSelector() != null && filter.getSelector().equalsIgnoreCase("shared")) {
				// Overriding condition by sharing condition
				if (filter.getUser().getType().equalsIgnoreCase("ECAS")) {
					sql.append(
							" AND (s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE (a.ACCESS_USER = :ownerId OR a.ACCESS_DEPARTMENT IN (SELECT GRPS FROM ECASGROUPS WHERE eg_ID = (SELECT USER_ID FROM ECASUSERS WHERE USER_LOGIN = :login))) AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%')) OR s.SURVEY_UID in (SELECT r.SURVEY FROM RESULTACCESS r WHERE r.RESACC_USER = :ownerId))");
					oQueryParameters.put("ownerId", filter.getUser().getId());
					oQueryParameters.put("login", filter.getUser().getLogin());
				} else {
					sql.append(
							" AND (s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE a.ACCESS_USER = :ownerId AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%')) OR s.SURVEY_UID in (SELECT r.SURVEY FROM RESULTACCESS r WHERE r.RESACC_USER = :ownerId))");
					oQueryParameters.put("ownerId", filter.getUser().getId());
				}
			} else {
				// Searching for the last case, assuming selector is "all"
				if (filter.getUser().getType().equalsIgnoreCase("ECAS")) {
					sql.append(
							" AND (s.OWNER = :ownerId OR s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE (a.ACCESS_USER = :ownerId OR a.ACCESS_DEPARTMENT IN (SELECT GRPS FROM ECASGROUPS WHERE eg_ID = (SELECT USER_ID FROM ECASUSERS WHERE USER_LOGIN = :login))) AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%')) OR s.SURVEY_UID in (SELECT r.SURVEY FROM RESULTACCESS r WHERE r.RESACC_USER = :ownerId))");
					oQueryParameters.put("ownerId", filter.getUser().getId());
					oQueryParameters.put("login", filter.getUser().getLogin());
				} else {
					sql.append(
							" AND (s.OWNER = :ownerId OR s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE a.ACCESS_USER = :ownerId AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%')) OR s.SURVEY_UID in (SELECT r.SURVEY FROM RESULTACCESS r WHERE r.RESACC_USER = :ownerId))");
					oQueryParameters.put("ownerId", filter.getUser().getId());
				}
			}
		}

		if (filter.getKeywords() != null && filter.getKeywords().trim().length() > 0) {
			int i = 0;
			sql.append(" AND (");
			for (String word : filter.getKeywords().split(" ")) {
				if (word.trim().length() > 0) {
					String w = "word" + i++;

					if (i > 1) {
						sql.append(" OR");
					}

					sql.append(" ( s.SURVEYNAME like :").append(w)
							.append(" OR s.TITLE like :").append(w).append(")");
					oQueryParameters.put(w, "%" + word.trim() + "%");
				}
			}
			sql.append(" )");
		}

		if (filter.getLanguages() != null) {
			int i = 0;
			sql.append(" AND (");
			for (String lang : filter.getLanguages()) {
				if (lang.trim().length() > 0) {
					String l = "lang" + i++;

					if (i > 1) {
						sql.append(" OR");
					}

					sql.append(" ( s.LANGUAGE = :").append(l).append(
							" or s.SURVEY_ID in (Select distinct t.SURVEY_ID FROM TRANSLATIONS t WHERE t.SURVEY_ACTIVE = 1 AND t.LANGUAGE = :")
							.append(l).append("))");
					oQueryParameters.put(l, lang.trim());
				}
			}
			sql.append(" )");
		}

		if (filter.getSurveys() != null && filter.getSurveys().equalsIgnoreCase("REPORTED")) {
			sql.append(" AND (s.SURVEY_UID IN (SELECT DISTINCT SURABUSE_SURVEY FROM SURABUSE))");
		}

		if (filter.getMinReported() != null && filter.getMinReported() > 0) {
			sql.append(" AND (abu.abuses >= :abuses)");
			oQueryParameters.put("abuses", filter.getMinReported());
		}

		if ((filter.getSurveys() != null && filter.getSurveys().equalsIgnoreCase("FROZEN"))
				|| filter.getFrozen() != null && filter.getFrozen()) {
			sql.append(" AND (s.FROZEN = 1)");
		} else {
			sql.append(" AND (s.FROZEN = 0)");
		}

		if (filter.getMinContributions() != null) {
			sql.append(" AND (npa.PUBLISHEDANSWERS > :replies)");
			oQueryParameters.put("replies", filter.getMinContributions());
		}

		boolean having = false;
		if (filter.getPublishedFrom() != null) {
			sql.append(
					" HAVING published >= :publishedFrom");
			having = true;
			oQueryParameters.put("publishedFrom", filter.getPublishedFrom());
		}

		if (filter.getPublishedTo() != null) {
			if (having) {
				sql.append(" AND ");
			} else {
				sql.append(" HAVING ");
				having = true;
			}
			sql.append("published < :publishedTo");
			oQueryParameters.put("publishedTo", Tools.getFollowingDay(filter.getPublishedTo()));
		}

		if (filter.getFirstPublishedFrom() != null) {
			if (having) {
				sql.append(" AND ");
			} else {
				sql.append(" HAVING ");
				having = true;
			}
			sql.append("firstPublished >= :firstPublishedFrom");
			oQueryParameters.put("firstPublishedFrom", filter.getFirstPublishedFrom());
		}

		if (filter.getFirstPublishedTo() != null) {
			if (having) {
				sql.append(" AND ");
			} else {
				sql.append(" HAVING ");
			}
			sql.append("firstPublished < :firstPublishedTo");
			oQueryParameters.put("firstPublishedTo", Tools.getFollowingDay(filter.getFirstPublishedTo()));
		}

		if (filter.getSortKey() != null && filter.getSortKey().length() > 0) {
			if (filter.getSortKey().equalsIgnoreCase("replies")) {
				sql.append(" ORDER BY npa.PUBLISHEDANSWERS");

				if (filter.getSortOrder() != null && filter.getSortOrder().length() > 0) {
					sql.append(" ").append(filter.getSortOrder().toUpperCase());
				}
			} else if (filter.getSortKey().equalsIgnoreCase("created")) {
				sql.append(" ORDER BY s.SURVEY_CREATED");

				if (filter.getSortOrder() != null && filter.getSortOrder().length() > 0) {
					sql.append(" ").append(filter.getSortOrder().toUpperCase());
				}
			} else if (filter.getSortKey().equalsIgnoreCase("firstPublished")) {
				sql.append(" ORDER BY firstPublished");

				if (filter.getSortOrder() != null && filter.getSortOrder().length() > 0) {
					sql.append(" ").append(filter.getSortOrder().toUpperCase());
				}
			} else if (filter.getSortKey().equalsIgnoreCase("published")) {
				sql.append(" ORDER BY published");

				if (filter.getSortOrder() != null && filter.getSortOrder().length() > 0) {
					sql.append(" ").append(filter.getSortOrder().toUpperCase());
				}
			} else if (filter.getSortKey().equalsIgnoreCase("reported")) {
				sql.append(" ORDER BY reported");

				if (filter.getSortOrder() != null && filter.getSortOrder().length() > 0) {
					sql.append(" ").append(filter.getSortOrder().toUpperCase());
				}
			} else {
				sql.append(" ORDER BY s.").append(filter.getSortKey());

				if (filter.getSortOrder() != null && filter.getSortOrder().length() > 0) {
					sql.append(" ").append(filter.getSortOrder().toUpperCase());
				} else {
					sql.append(" DESC");
				}
			}
		}

		return sql.toString();
	}

	private void synchronizeSurvey(Survey survey, String languageCode, boolean setSurvey) {
		SurveyHelper.synchronizeSurvey(survey, languageCode, translationService, getLanguage(languageCode), setSurvey);
	}

	@Transactional
	public Survey getSurvey(int id) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(Survey.class, id);
	}

	@Transactional
	public Survey getSurvey(int id, boolean readonly) {
		Session session = sessionFactory.getCurrentSession();
		Survey survey = session.get(Survey.class, id);

		if (survey != null) {
			session.setReadOnly(survey, readonly);
			for (Element e : survey.getElementsRecursive(true)) {
				e = (Element) session.merge(e);
				session.setReadOnly(e, readonly);
			}
		}

		return survey;
	}

	@Transactional(readOnly = true)
	public Survey getSurveyReadOnly(int id, boolean loadTranslations, boolean setSurvey) {
		return getSurvey(id, loadTranslations, true, true, setSurvey);
	}

	@Transactional
	public Survey getSurvey(int id, boolean loadTranslations, boolean readonly) {
		return getSurvey(id, loadTranslations, readonly, true, true);
	}
	
	@Transactional
	public Survey getSurvey(int id, boolean loadTranslations, boolean readonly, boolean synchronizeSurvey,
			boolean setSurvey) {
		return getSurvey(id, loadTranslations, readonly, synchronizeSurvey, setSurvey, false);
	}

	@Transactional
	public Survey getSurvey(int id, boolean loadTranslations, boolean readonly, boolean synchronizeSurvey,
			boolean setSurvey, boolean initialize) {
		Session session = sessionFactory.getCurrentSession();
		Survey survey = session.get(Survey.class, id);
		
		if (initialize) {
			initializeSurvey(survey);
		}
		
		if (survey != null && (survey.getIsDraft() || loadTranslations)) {
			List<String> translations = translationService.getTranslationLanguagesForSurvey(survey.getId());
			survey.setTranslations(translations);
		}
		if (survey != null) {
			if (synchronizeSurvey) {
				synchronizeSurvey(survey, survey.getLanguage().getCode(), setSurvey);
				Hibernate.initialize(survey.getOwner().getRoles());
			}

			session.setReadOnly(survey, readonly);
			for (Element e : survey.getElementsRecursive(true)) {
				e = (Element) session.merge(e);
				session.setReadOnly(e, readonly);
			}
		}

		return survey;
	}

	@Transactional
	public Survey getSurveyByUniqueIdToWrite(String uid) {
		Session session = sessionFactory.getCurrentSession();
		Query<Integer> query = session
				.createQuery(
						"SELECT id FROM Survey s WHERE s.uniqueId = :uid AND s.isDraft = :draft ORDER BY s.id DESC",
				Integer.class)
				.setParameter("uid", uid);
		query.setParameter("draft", true);

		List<Integer> list = query.setReadOnly(true).setMaxResults(1).list();
		if (!list.isEmpty()) {
			return getSurvey(ConversionTools.getValue(list.get(0)));
		}
		return null;
	}


	@Transactional(readOnly = true)
	public Survey getSurveyByAlias(String alias, boolean draft) {
		Session session = sessionFactory.getCurrentSession();
		Query<Integer> query = session.createQuery("SELECT id FROM Survey s WHERE s.shortname = :alias AND s.isDraft = :draft ORDER BY s.id DESC",
				Integer.class).setParameter("alias", alias);
		query.setParameter("draft", draft);

		List<Integer> list = query.setReadOnly(true).setMaxResults(1).list();
		if (list.size() > 0) {
			Survey survey = getSurvey(ConversionTools.getValue(list.get(0)));

			if (survey != null) {
				synchronizeSurvey(survey, survey.getLanguage().getCode(), true);
			}

			session.setReadOnly(survey, true);
			for (Element e : survey.getElementsRecursive(true)) {
				session.setReadOnly(e, true);
			}

			return survey;
		}
		return null;
	}


	@Transactional(readOnly = true)
	public Survey getSurveyByUniqueId(String uid, boolean loadTranslations, boolean draft) {
		Session session = sessionFactory.getCurrentSession();
		Query<Integer> query = session
				.createQuery(
						"SELECT id FROM Survey s WHERE s.uniqueId = :uid AND s.isDraft = :draft ORDER BY s.id DESC",
						Integer.class);
		query.setParameter("uid", uid);
		query.setParameter("draft", draft);

		List<Integer> list = query.setReadOnly(true).setMaxResults(1).list();
		if (!list.isEmpty()) {
			Survey survey = getSurvey(ConversionTools.getValue(list.get(0)));

			if (survey != null && loadTranslations) {
				List<String> translations = translationService.getTranslationLanguagesForSurvey(survey.getId());
				survey.setTranslations(translations);
			}
			if (survey != null) {
				synchronizeSurvey(survey, survey.getLanguage().getCode(), true);
			}

			session.setReadOnly(survey, true);
			for (Element e : survey.getElementsRecursive(true)) {
				session.setReadOnly(e, true);
			}

			return survey;
		}
		return null;
	}

	@Transactional
	public Survey initializeAndMergeSurvey(Survey survey) {
		Session session = sessionFactory.getCurrentSession();
		Survey s = (Survey) session.merge(survey);
		initializeSurvey(s);
		return s;
	}

	public void initializeSkin(Survey survey) {
		if (survey.getSkin() != null) {
			Hibernate.initialize(survey.getSkin().getElements());
		}
	}

	public void initializeSurvey(Survey survey) {
		Hibernate.initialize(survey.getBackgroundDocuments());
		Hibernate.initialize(survey.getUsefulLinks());
		Hibernate.initialize(survey.getPublication().getFilter().getVisibleQuestions());
		Hibernate.initialize(survey.getPublication().getFilter().getExportedQuestions());
		Hibernate.initialize(survey.getPublication().getFilter().getFilterValues());
		Hibernate.initialize(survey.getPublication().getFilter().getLanguages());
		
		Hibernate.initialize(survey.getPublication().getFilter().getVisibleExplanations());
		Hibernate.initialize(survey.getPublication().getFilter().getVisibleDiscussions());
		Hibernate.initialize(survey.getPublication().getFilter().getExportedExplanations());
		Hibernate.initialize(survey.getPublication().getFilter().getExportedDiscussions());

		if (survey.getSkin() != null) {
			Hibernate.initialize(survey.getSkin().getElements());
		}

		for (Element element : survey.getElementsRecursive(true)) {

			if (element instanceof Question) {
				Question q = (Question) element;
				Hibernate.initialize(q.getScoringItems());
			}

			if (element instanceof ChoiceQuestion) {
				ChoiceQuestion question = (ChoiceQuestion) element;
				for (PossibleAnswer answer : question.getPossibleAnswers()) {
					Hibernate.initialize(answer.getDependentElements());
					if (answer.getDependentElements() != null) {
						Hibernate.initialize(answer.getDependentElements().getDependentElements());
					} else {
						logger.warn("answer.getDependentElements() null for answer " + answer.getId());
					}
				}
			} else if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;
				Hibernate.initialize(matrix.getDependentElements());
				Hibernate.initialize(matrix.getChildElements());
				for (DependencyItem dep : matrix.getDependentElements()) {
					Hibernate.initialize(dep.getDependentElements());
				}
			} else if (element instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion) element;
				Hibernate.initialize(rating.getChildElements());
			} else if (element instanceof Table) {
				Table table = (Table) element;
				Hibernate.initialize(table.getChildElements());
			} else if (element instanceof Download) {
				Download download = (Download) element;
				Hibernate.initialize(download.getFiles());
			} else if (element instanceof Confirmation) {
				Confirmation confirmation = (Confirmation) element;
				Hibernate.initialize(confirmation.getFiles());
			} else if (element instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) element;
				Hibernate.initialize(gallery.getFiles());
			} else if (element instanceof RankingQuestion) {
				RankingQuestion ranking = (RankingQuestion) element;
				Hibernate.initialize(ranking.getChildElements());
			} else if (element instanceof ComplexTable){
				ComplexTable table = (ComplexTable) element;
				Hibernate.initialize(table.getChildElements());
			}
		}
	}

	@Transactional(readOnly = true)
	public Survey getSurvey(int id, String language) {
		Survey survey = getSurvey(id, false, true, false, true, false);
		synchronizeSurvey(survey, language, true);
		return survey;
	}
	
	@Transactional(readOnly = true)
	public Survey getSurvey(int id, String language, boolean initialize) {
		Survey survey = getSurvey(id, false, true, false, true, initialize);
		synchronizeSurvey(survey, language, true);
		return survey;
	}

	@Transactional(readOnly = true)
	public Survey getSurveyByShortname(String shortname, boolean isDraft, User u, HttpServletRequest request,
			boolean initElements, boolean checkNotArchived, boolean checkNotDeleted, boolean synchronize)
			throws InvalidURLException {

		if (shortname.equals("noform")) {
			SessionInfo info = (SessionInfo) request.getSession().getAttribute("sessioninfo");

			if (info != null) {
				Survey survey = getSurvey(info.getSurvey(), info.getLanguage());
				if (survey != null)
					return survey;
			}

			throw new InvalidURLException();
		}

		Survey survey = surveyService.getSurvey(shortname, isDraft, false, false, false, null, true, checkNotDeleted,
				true, synchronize);

		if (survey != null) {
			List<String> translations = translationService.getTranslationLanguagesForSurvey(survey.getId());
			survey.setTranslations(translations);

			if (u != null) {
				boolean allowed = false;
				if (!survey.getOwner().getId().equals(u.getId())) {
					if (u.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2) {
						if (u.getLocalPrivileges().get(LocalPrivilege.FormManagement) < 1) {
							if (u.getLocalPrivileges().get(LocalPrivilege.AccessDraft) > 0) {
								allowed = true;
							}
							if (u.getLocalPrivileges().get(LocalPrivilege.AccessResults) > 0) {
								allowed = true;
							}
							if (u.getLocalPrivileges().get(LocalPrivilege.ManageInvitations) > 0) {
								allowed = true;
							}
						}
					} else {
						allowed = true;
					}
				} else {
					allowed = true;
				}

				if (allowed)
					sessionService.updateSessionInfo(survey, u, request);
			}

			if (initElements)
				for (Element element : survey.getElementsRecursive()) {
					element.setSurvey(survey);
				}

			if (checkNotArchived && (survey.getArchived() || survey.getIsDeleted())) {
				throw new InvalidURLException();
			}

			return survey;
		}
		throw new InvalidURLException();
	}

	@Transactional
	public Survey getSurveyWithMissingElements(String uidorshortname, boolean isDraft, boolean checkActive,
			boolean readReplies, boolean useEagerLoading, String language, boolean readonly, boolean synchronize) {
		Survey survey = getSurvey(uidorshortname, isDraft, checkActive, readReplies, useEagerLoading, language,
				readonly, false, false, synchronize);
		if (survey != null)
			checkAndRecreateMissingElements(survey, null);
		return survey;
	}

	@Transactional
	public Survey getSurvey(String uidorshortname, boolean isDraft, boolean checkActive, boolean readReplies,
			boolean useEagerLoading, String language, boolean readonly, boolean synchronize) {
		return getSurvey(uidorshortname, isDraft, checkActive, readReplies, useEagerLoading, language, readonly, true,
				false, synchronize);
	}

	@Transactional
	public Survey getSurvey(String uidorshortname, boolean isDraft, boolean checkActive, boolean readReplies,
			boolean useEagerLoading, String language, boolean readonly, boolean checkNotDeleted, boolean shortnamefirst,
			boolean synchronize) {
		Session session = sessionFactory.getCurrentSession();
		String sql;

		if (shortnamefirst) {
			sql = "SELECT max(s.id) FROM Survey s WHERE (s.shortname = :uid OR s.uniqueId = :uid) AND s.isDraft = :draft";
		} else {
			sql = "SELECT max(s.id) FROM Survey s WHERE (s.uniqueId = :uid OR s.shortname = :uid) AND s.isDraft = :draft";
		}

		if (checkNotDeleted) {
			sql += " AND (s.isDeleted is null OR s.isDeleted = false)";
		}

		Query<Integer> query = session.createQuery(sql, Integer.class).setParameter("uid", uidorshortname).setParameter("draft", isDraft)
				.setReadOnly(true);

		int id = ConversionTools.getValue(query.uniqueResult());

		if (id > 0) {

			Survey survey = session.get(Survey.class, id);

			if (useEagerLoading) {
				initializeSurvey(survey);
			}

			// used e.g. in form runner to check whether disclaimer has to be displayed
			Hibernate.initialize(survey.getOwner().getRoles());

			if (readReplies) {
				survey.setNumberOfAnswerSetsPublished(
						answerService.getNumberOfAnswerSetsPublished(uidorshortname, survey.getUniqueId()));
			}

			if (survey.getIsActive() && survey.getAutomaticPublishing() && survey.getEnd() != null
					&& survey.getEnd().before(new Date())) {
				survey.setIsActive(false);
				session.update(survey);
			} else if (!survey.getIsActive() && survey.getAutomaticPublishing() && survey.getStart() != null
					&& survey.getStart().before(new Date())
					&& (survey.getEnd() == null || survey.getEnd().after(new Date()))) {
				survey.setIsActive(true);
				survey.setIsPublished(true);
				session.update(survey);
			}

			Survey draft;
			if (isDraft) {
				draft = survey;
			} else {
				query = session.createQuery(sql, Integer.class).setParameter("uid", uidorshortname).setParameter("draft", true);
				id = ConversionTools.getValue(query.uniqueResult());

				if (id == 0)
					return null;

				draft = session.get(Survey.class, id);
			}
			
			if (checkActive && !isDraft) {
				if (!draft.getIsActive())
					return null;
			}

			session.setReadOnly(survey, readonly);
			for (Element e : survey.getElementsRecursive(true)) {
				e = (Element) session.merge(e);
				session.setReadOnly(e, readonly);
			}

			if (synchronize) {
				if (language == null) {
					synchronizeSurvey(survey, survey.getLanguage().getCode(), true);
				} else {
					synchronizeSurvey(survey, language, true);
				}
			}
			List<String> translations = translationService.getTranslationLanguagesForSurvey(survey.getId());
			survey.setTranslations(translations);
			return survey;
		}
		return null;
	}

	@Transactional
	public Survey add(Survey survey, int userId) {
		return add(survey, true, userId);
	}

	@Transactional
	public Survey add(Survey survey, boolean synchronize, int userId) {
		survey.setUpdated(new Date());
		survey.setDBVersion(getDBVersion());
		if (survey.getCreated() == null) {
			survey.setCreated(new Date());
			survey.setUpdated(survey.getCreated());
		}
		if (survey.getUniqueId() == null || survey.getUniqueId().length() == 0) {
			survey.setUniqueId(UUID.randomUUID().toString());
		}
		Session session = sessionFactory.getCurrentSession();
		int id = (Integer) session.save(survey);
		Survey result = session.get(Survey.class, id);

		UpdatePossibleAnswers(result);
		session.update(result);

		if (synchronize)
			synchronizeTranslation(survey, userId);

		reportingService.addToDo(ToDo.CHANGEDDRAFTSURVEY, survey.getUniqueId(), null);

		return result;
	}

	private void UpdatePossibleAnswers(Survey survey) {
		for (Element element : survey.getElements()) {
			if (element instanceof ChoiceQuestion) {
				for (PossibleAnswer pa : ((ChoiceQuestion) element).getPossibleAnswers()) {
					pa.setQuestionId(element.getId());
				}
			}
			if (element instanceof ComplexTable){
				ComplexTable table = (ComplexTable) element;
				for (ComplexTableItem item : table.getChildElements()){
					if (item.isChoice()){
						for (PossibleAnswer pa : item.getPossibleAnswers()) {
							pa.setQuestionId(table.getId());
						}
					}
				}
			}
		}
	}

	private void computeTrustScore(Survey survey) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		int score = 0;

		// skip computing if setting does not exist
		String check = settingsService.get(Setting.TrustValueCreatorInternal);
		if (check == null)
			return;

		int trustValueCreatorInternal = Integer.parseInt(settingsService.get(Setting.TrustValueCreatorInternal));
		int trustValuePastSurveys = Integer.parseInt(settingsService.get(Setting.TrustValuePastSurveys));
		int trustValuePrivilegedUser = Integer.parseInt(settingsService.get(Setting.TrustValuePrivilegedUser));
		int trustValueNbContributions = Integer.parseInt(settingsService.get(Setting.TrustValueNbContributions));

		// Rule 0: if the Form Manager is internal
		if (!survey.getOwner().isExternal()) {
			score += trustValueCreatorInternal;
		}

		// Rule 1: if the Form Manager has published in the past 3 or more surveys:
		// - having all reached 10 contributions or more ; and
		// - including at least one survey having been published for one month or more

		SurveyFilter filter = new SurveyFilter();
		filter.setUser(survey.getOwner());
		filter.setOwner(survey.getOwner().getLogin());
		SqlPagination pagination = new SqlPagination(1, 1000);
		List<Survey> surveys = getSurveysIncludingPublicationDates(filter, pagination);

		int countSurveysWith10Contributions = 0;
		boolean found = false;
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -1);
		Date lastmonth = c.getTime();
		for (Survey candidate : surveys) {
			int candidateanswers = answerService.getNumberOfAnswerSetsPublished(candidate.getShortname(),
					candidate.getUniqueId());
			if (candidateanswers >= 10) {
				countSurveysWith10Contributions++;
			}

			if (candidate.getFirstPublished() != null && candidate.getFirstPublished().before(lastmonth)) {
				found = true;
			}

			if (found && countSurveysWith10Contributions >= 3) {
				break;
			}
		}
		if (found && countSurveysWith10Contributions >= 3) {
			score += trustValuePastSurveys;
		}

		// Rule 2: if a privileged user is internal
		List<Access> accesses = this.getAccesses(survey.getId());
		for (Access access : accesses) {
			if (access.getUser() != null && !access.getUser().isExternal()) {
				score += trustValuePrivilegedUser;
				break;
			}
		}

		int answers = answerService.getNumberOfAnswerSetsPublished(survey.getShortname(), survey.getUniqueId());

		// Rule 3: when the survey is reaching 10 contributions
		if (answers >= 10) {
			score += trustValueNbContributions;
		}

		// Rule 4: when the survey is reaching 100 contributions
		if (answers >= 100) {
			score += trustValueNbContributions;
		}

		survey.setTrustScore(score);
		session.saveOrUpdate(survey);
	}
	
	/**
	 * Returns true if the user has ever published a survey before
	 */
	@Transactional
	public boolean getUserHasPublishedSurveys(int userId) {
		Session session = sessionFactory.getCurrentSession();
		
		NativeQuery query = session.createSQLQuery("SELECT SURVEY_ID FROM SURVEYS WHERE  ISDRAFT = 0 AND OWNER = :id");

		@SuppressWarnings("rawtypes")
		List surveys = query.setParameter("id", userId).setMaxResults(1).list();			
		
		return !surveys.isEmpty();
	}

	/**
	 * Creates a copy of the survey and save it as non-draft survey
	 */
	@Transactional
	public Survey publish(Survey originalSurvey, int pnumberOfAnswerSets, int pnumberOfAnswerSetsPublished,
			boolean deactivateAutoPublishing, int userId, boolean resetSourceIds, boolean resetSurvey)
			throws Exception {
		Session session = sessionFactory.getCurrentSession();
		boolean alreadyPublished = !originalSurvey.getIsDraft() || originalSurvey.getIsPublished();

		if (resetSurvey) {
			session.evict(originalSurvey);
			originalSurvey = (Survey) session.merge(originalSurvey);
		}
		
		boolean firstPublicationForUser = !getUserHasPublishedSurveys(originalSurvey.getOwner().getId());

		Survey publishedSurvey = originalSurvey.copy(this, originalSurvey.getOwner(), fileDir, true, pnumberOfAnswerSets,
				pnumberOfAnswerSetsPublished, true, resetSourceIds, true, null, null);
		publishedSurvey.setIsDraft(false); // this means it is not a draft
		if (deactivateAutoPublishing) {
			publishedSurvey.setAutomaticPublishing(false);
		}
		publishedSurvey = update(publishedSurvey, false, true, false, userId);

		this.computeTrustScore(originalSurvey);

		// copy translations
		Map<String, String> publishedSurveyKeys = new HashMap<>();
		for (Element element : publishedSurvey.getElementsRecursive()) {
			publishedSurveyKeys.put(element.getUniqueId(), element.getUniqueId());
			publishedSurveyKeys.put(element.getSourceId().toString(), element.getId().toString());
			if (element instanceof ChoiceQuestion) {
				for (PossibleAnswer answer : ((ChoiceQuestion) element).getPossibleAnswers()) {
					publishedSurveyKeys.put(answer.getUniqueId(), answer.getUniqueId());
					publishedSurveyKeys.put(answer.getSourceId().toString(), answer.getId().toString());
				}
			}
			if (element instanceof Question) {
				Question question = (Question) element;
				if (question.getScoringItems() != null) {
					for (ScoringItem scoringItem : question.getScoringItems()) {
						publishedSurveyKeys.put(scoringItem.getUniqueId(), scoringItem.getUniqueId());
						publishedSurveyKeys.put(scoringItem.getSourceId().toString(), scoringItem.getId().toString());
					}
				}
			}
			if (element instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) element;
				if (gallery.getFiles() != null) {
					for (com.ec.survey.model.survey.base.File file : gallery.getFiles()) {
						publishedSurveyKeys.put(file.getUid(), file.getUid());
					}
				}
			}
			if (element instanceof ComplexTableItem) {
				ComplexTableItem item = (ComplexTableItem) element;
				if (item.isChoice()){
					for (PossibleAnswer answer : item.getPossibleAnswers()){
						publishedSurveyKeys.put(answer.getUniqueId(), answer.getUniqueId());
						publishedSurveyKeys.put(answer.getSourceId().toString(), answer.getId().toString());
					}
				}

			}
		}
		List<Translations> draftTranslationsList = translationService.getTranslationsForSurvey(originalSurvey.getId(), true);
		for (Translations draftTranslations : draftTranslationsList) {
			if (!draftTranslations.getLanguage().getCode().equalsIgnoreCase(originalSurvey.getLanguage().getCode())) {
				Translations translationsCopy = new Translations();
				translationsCopy.setActive(draftTranslations.getActive());
				translationsCopy.setLanguage(draftTranslations.getLanguage());
				translationsCopy.setSurveyId(publishedSurvey.getId());
				translationsCopy.setSurveyUid(publishedSurvey.getUniqueId());
				translationsCopy.setTitle(draftTranslations.getTitle());
				translationsCopy.setComplete(draftTranslations.getComplete());

				for (Translation draftTranslation : draftTranslations.getTranslations()) {
					Translation translationCopy = new Translation();
					translationCopy.setLabel(draftTranslation.getLabel());
					translationCopy.setLanguage(draftTranslation.getLanguage());
					translationCopy.setSurveyId(publishedSurvey.getId());
					translationCopy.setTranslations(translationsCopy);
					if (draftTranslation.getKey().equalsIgnoreCase(Survey.TITLE) 
							|| draftTranslation.getKey().equalsIgnoreCase(Survey.LOGOTEXT)
							|| draftTranslation.getKey().equalsIgnoreCase(Survey.INTRODUCTION)
							|| draftTranslation.getKey().equalsIgnoreCase(Survey.ESCAPEPAGE)
							|| draftTranslation.getKey().equalsIgnoreCase(Survey.ESCAPELINK)
							|| draftTranslation.getKey().equalsIgnoreCase(Survey.CONFIRMATIONPAGE)
							|| draftTranslation.getKey().equalsIgnoreCase(Survey.CONFIRMATIONLINK)
							|| draftTranslation.getKey().equalsIgnoreCase(Survey.HELP)
							|| draftTranslation.getKey().equalsIgnoreCase(Survey.QUIZWELCOMEMESSAGE)
							|| draftTranslation.getKey().equalsIgnoreCase(Survey.QUIZRESULTSMESSAGE)) {
						translationCopy.setKey(draftTranslation.getKey());
						translationsCopy.getTranslations().add(translationCopy);
					} else {
						if (draftTranslation.getKey().endsWith("help")) {
							String draftKey = draftTranslation.getKey().replace("help", "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + "help");
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (draftTranslation.getKey().endsWith("FEEDBACK")) {
							String draftKey = draftTranslation.getKey().replace("FEEDBACK", "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + "FEEDBACK");
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (draftTranslation.getKey().endsWith("UNIT")) {
							String draftKey = draftTranslation.getKey().replace("UNIT", "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + "UNIT");
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (draftTranslation.getKey().endsWith(NumberQuestion.MINLABEL)) {
							String draftKey = draftTranslation.getKey().replace(NumberQuestion.MINLABEL, "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + NumberQuestion.MINLABEL);
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (draftTranslation.getKey().endsWith(NumberQuestion.MAXLABEL)) {
							String draftKey = draftTranslation.getKey().replace(NumberQuestion.MAXLABEL, "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + NumberQuestion.MAXLABEL);
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (draftTranslation.getKey().endsWith("CONFIRMATIONTEXT")) {
							String draftKey = draftTranslation.getKey().replace("CONFIRMATIONTEXT", "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + "CONFIRMATIONTEXT");
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (draftTranslation.getKey().endsWith("CONFIRMATIONLABEL")) {
							String draftKey = draftTranslation.getKey().replace("CONFIRMATIONLABEL", "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + "CONFIRMATIONLABEL");
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (draftTranslation.getKey().endsWith("TABTITLE")) {
							String draftKey = draftTranslation.getKey().replace("TABTITLE", "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + "TABTITLE");
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (draftTranslation.getKey().endsWith(Constants.SHORTNAME)) {
							String draftKey = draftTranslation.getKey().replace(Constants.SHORTNAME, "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + Constants.SHORTNAME);
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (draftTranslation.getKey().endsWith("#backgrounddocument") || draftTranslation.getKey().endsWith("#usefullink")) {
							translationCopy.setKey(draftTranslation.getKey());
							translationsCopy.getTranslations().add(translationCopy);
						} else if (draftTranslation.getKey().endsWith("FIRSTCELL")) {
							String draftKey = draftTranslation.getKey().replace("FIRSTCELL", "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + "FIRSTCELL");
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (draftTranslation.getKey().endsWith("GALLERYTEXT")) {
							String draftKey = draftTranslation.getKey().replace("GALLERYTEXT", "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + "GALLERYTEXT");
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						}  else if (draftTranslation.getKey().endsWith("TITLE")) {
							String draftKey = draftTranslation.getKey().replace("TITLE", "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + "TITLE");
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (draftTranslation.getKey().endsWith("RESULTTEXT")) {
							String draftKey = draftTranslation.getKey().replace("RESULTTEXT", "");
							if (publishedSurveyKeys.containsKey(draftKey)) {
								translationCopy.setKey(publishedSurveyKeys.get(draftKey) + "RESULTTEXT");
								translationsCopy.getTranslations().add(translationCopy);
							} else {
								logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
							}
						} else if (publishedSurveyKeys.containsKey(draftTranslation.getKey())) {
							translationCopy.setKey(publishedSurveyKeys.get(draftTranslation.getKey()));
							translationsCopy.getTranslations().add(translationCopy);
						} else {
							logger.info("key " + draftTranslation.getKey() + " not found in key map for translation");
						}
					}
				}
				translationService.save(translationsCopy);
			} 
		}

		Survey ob = session.get(Survey.class, originalSurvey.getId());

		ob.setIsPublished(true);
		ob.setHasPendingChanges(false);

		if (deactivateAutoPublishing) {
			ob.setAutomaticPublishing(false);
			originalSurvey.setAutomaticPublishing(false);
		}

		originalSurvey.setHasPendingChanges(false);

		session.update(ob);
		originalSurvey.setIsPublished(true);

		if (!alreadyPublished) {
			reportingService.addToDo(ToDo.NEWSURVEY, originalSurvey.getUniqueId(), null);
			chargePublishedSurvey(originalSurvey.getUniqueId(), originalSurvey.getOrganisation());
		}
		
		if (firstPublicationForUser && originalSurvey.getOwner().getGlobalPrivileges().get(GlobalPrivilege.ECAccess) == 0) {
			sendFirstPublishedSurveyMail(originalSurvey);
		}

		return publishedSurvey;
	}
	
	@Transactional
	private void chargePublishedSurvey(String uniqueId, String organisation) {
		Session session = sessionFactory.getCurrentSession();
		PublishedSurvey ps = new PublishedSurvey();
		ps.setSurveyUID(uniqueId);
		ps.setPublished(new Date());
		ps.setOrganisation(organisation);
		session.save(ps);		
	}

	public void sendFirstPublishedSurveyMail(Survey survey) throws Exception {
		String body = "<b>SURVEY MODERATION</b><br />" +
				"<br />" +
				"An external user has published his first survey.<br/>" +
				"<br />" +
				"Runner: <a href=\"" + host	+ "runner/" + survey.getShortname() + "\">" + host + "runner/" + survey.getShortname() + "</a><br />" +
				"<br />" +
				"<table>" +
				"<tr><td><b>Username:</b></td>" + "<td style='padding-left: 10px;'>" + survey.getOwner().getLogin() + "</td></tr>" +
				"<tr><td><b>First name:</b></td>" + "<td style='padding-left: 10px;'>" + survey.getOwner().getGivenName() + "</td></tr>" +
				"<tr><td><b>Surname:</b></td>" + "<td style='padding-left: 10px;'>" + survey.getOwner().getSurName() + "</td></tr>" +
				"<tr><td><b>Email address:</b></td>" + "<td style='padding-left: 10px;'>" + survey.getOwner().getEmail() + "</td></tr>" +
				"<tr><td><b>Survey Title:</b></td>" + "<td style='padding-left: 10px;'>" + survey.getTitle() + "</td></tr>" +
				"<tr><td><b>Survey Alias:</b></td>" + "<td style='padding-left: 10px;'>" + survey.getShortname() + "</td></tr>" +
				"<tr><td><b>Survey UID:</b></td>" + "<td style='padding-left: 10px;'>" + survey.getUniqueId() + "</td></tr>" +
				"</table><br />" +
				"Administration: <a href=\"" + host	+ survey.getShortname() + "/management/overview\">" + host + survey.getShortname() + "/management/overview</a><br />";

		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]", host);

		mailService.SendHtmlMail(monitoringEmail, sender, sender,
				"First survey published by user " + survey.getOwner().getLogin(), text, null);
	}
	
	@Transactional
	public void setNotified(Survey survey) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		
		Survey ob = session.get(Survey.class, survey.getId());
		session.setReadOnly(ob, false);
		ob.setNotified(true);
		session.update(ob);
	}

	@Transactional
	public Survey activate(Survey survey, boolean deactivateAutoPublishing, int userId) throws Exception {
		// this means to create a copy of the survey and save it as non-draft survey
		Session session = sessionFactory.getCurrentSession();

		Survey published = getSurvey(survey.getShortname(), false, false, false, false, null, false, false);

		if (published != null) {
			published.setIsActive(true);
			if (deactivateAutoPublishing) {
				published.setAutomaticPublishing(false);
			}
			update(published, false, false, false, userId);
		} else {
			throw new MessageException("Survey does not exist");
		}

		// hibernate: transactional change of local variable
		Survey ob = session.get(Survey.class, survey.getId());
		session.setReadOnly(ob, false);
		ob.setIsActive(true);
		ob.setIsPublished(true);
		ob.setNotified(false);
		if (deactivateAutoPublishing) {
			ob.setAutomaticPublishing(false);
			survey.setAutomaticPublishing(false);
		}
		session.update(ob);

		survey.setIsPublished(true);
		survey.setIsActive(true);

		computeTrustScore(ob);

		return published;
	}

	@Transactional
	public Survey clearChanges(String shortname, int userId) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		Survey survey = this.getSurvey(shortname, true, false, false, false, null, false, false);

		boolean active = survey.getIsActive();

		Survey publishedSurvey = this.getSurvey(shortname, false, false, false, false, null, true, false);

		// hibernate: transactional change of local variable
		Survey newDraft = publish(publishedSurvey, survey.getNumberOfAnswerSets(),
				survey.getNumberOfAnswerSetsPublished(), false, userId, true, false);
		newDraft.setCreated(survey.getCreated());
		newDraft.setIsDraft(true);
		newDraft.setIsPublished(true);
		newDraft.setIsActive(active);
		newDraft.setIsFrozen(survey.getIsFrozen());
		session.update(newDraft);

		AdaptIDs(survey, newDraft, true);

		// delete original
		deleteSurveyData(survey.getId(), false, false, survey.getUniqueId(), false);
		session.delete(survey);

		return newDraft;
	}

	@Transactional
	public void unpublish(Survey survey, boolean deactivateAutoPublishing, int userId, boolean freeze) {
		Session session = sessionFactory.getCurrentSession();

		Survey publishedSurvey = this.getSurvey(survey.getShortname(), false, false, false, false, null, false, false);

		if (publishedSurvey == null) {
			publishedSurvey = this.getSurvey(survey.getUniqueId(), false, false, false, false, null, false, false);
		}

		if (publishedSurvey != null) {
			publishedSurvey.setIsActive(false);
			if (deactivateAutoPublishing) {
				publishedSurvey.setAutomaticPublishing(false);
				publishedSurvey.setNotificationValue(null);
				publishedSurvey.setNotificationUnit(null);
			}
			update(publishedSurvey, false, false, false, userId);
		}

		Survey ob = session.get(Survey.class, survey.getId());
		session.setReadOnly(ob, false);

		ob.setIsActive(false);
		if (freeze) {
			ob.setIsFrozen(true);
			survey.setIsFrozen(true);
		}

		if (deactivateAutoPublishing) {
			ob.setAutomaticPublishing(false);
			ob.setNotificationValue(null);
			ob.setNotificationUnit(null);
			survey.setAutomaticPublishing(false);
			survey.setNotificationValue(null);
			survey.setNotificationUnit(null);
		}

		survey.setIsActive(false);

		session.update(ob);
	}

	public static Map<Integer, String> getUniqueIdsById(Survey publishedSurvey) {
		HashMap<Integer, String> oldUniqueIdsById = new HashMap<>();
		for (Element element : publishedSurvey.getElementsRecursive(true)) {
			oldUniqueIdsById.put(element.getId(), element.getUniqueId());
		}
		return oldUniqueIdsById;
	}

	private Map<Integer, Integer> getSourceIdsById(Survey publishedSurvey) {
		HashMap<Integer, Integer> oldSourceIdsById = new HashMap<>();
		for (Element element : publishedSurvey.getElementsRecursive()) {
			oldSourceIdsById.put(element.getId(), element.getSourceId());
		}
		return oldSourceIdsById;
	}

	private void AdaptIDs(Survey oldSurvey, Survey newSurvey, boolean isDraft) {
		Session session = sessionFactory.getCurrentSession();

		if (isDraft) {
			// get existing answers and change survey reference
			List<AnswerSet> answers = answerService.getAnswersAndDrafts(oldSurvey.getId());
			for (AnswerSet answerSet : answers) {
				answerSet.setSurvey(newSurvey);
				answerSet.setSurveyId(newSurvey.getId());

				session.update(answerSet);
			}

			// also update draft-specific data

			// update privileges
			List<Access> accesses = getAccesses(oldSurvey.getId());
			for (Access access : accesses) {
				access.setSurvey(newSurvey);
				session.update(access);
			}
			
			//update guest-lists
			List<ParticipationGroup> groups = participationService.getAll(oldSurvey.getId());
			for (ParticipationGroup group : groups) {
				group.setSurveyId(newSurvey.getId());
				session.update(group);
			}
		}
	}

	@Transactional
	public int applyChanges(Survey draftSurvey, boolean deactivateAutoPublishing, int userId, boolean resetSurey)
			throws Exception {
		if (resetSurey) {
			Session session = sessionFactory.getCurrentSession();
			session.evict(draftSurvey);
			draftSurvey = (Survey) session.merge(draftSurvey);
			initializeSurvey(draftSurvey);
		}

		boolean sendListFormMail = draftSurvey.isListFormValidated();

		Survey publishedSurvey = getSurvey(draftSurvey.getShortname(), false, false, false, true, null, true, false);

		Survey newPublishedSurvey = publish(draftSurvey, -1, -1, deactivateAutoPublishing, userId, false, false);
		newPublishedSurvey.setVersion(publishedSurvey.getVersion() + 1);
		newPublishedSurvey.setIsActive(publishedSurvey.getIsActive());
		newPublishedSurvey.setNotified(publishedSurvey.getNotified());
		newPublishedSurvey.setListFormValidated(false);
		draftSurvey.setNotified(publishedSurvey.getNotified());
		draftSurvey.getPublication().setShowCharts(false);
		draftSurvey.getPublication().setShowStatistics(false);
		draftSurvey.getPublication().setShowContent(false);
		newPublishedSurvey.getPublication().setShowCharts(false);
		newPublishedSurvey.getPublication().setShowStatistics(false);
		newPublishedSurvey.getPublication().setShowContent(false);
		AdaptIDs(publishedSurvey, newPublishedSurvey, false);
		draftSurvey.setHasPendingChanges(false);
		draftSurvey.setListFormValidated(false);
		draftSurvey.setPublicationRequestedDate(new Date());

		computeTrustScore(draftSurvey);

		// copy result filters
		List<ResultFilter> publishedSurveyFilters = sessionService.getResultFilterForApplyChanges(publishedSurvey.getId());
		for (ResultFilter publishedSurveyFilter : publishedSurveyFilters) {			
			ResultFilter newPublishedSurveyFilter = publishedSurveyFilter.copy();

			// replace ids inside filter
			if (publishedSurveyFilter.getDefaultQuestions() == null || !publishedSurveyFilter.getDefaultQuestions()) {
				Map<String, String> uidsById = publishedSurvey.getUniqueIDsByID();
				Map<String, String> idsByUid = newPublishedSurvey.getIDsByUniqueID();
				Set<String> newids = new HashSet<>();
				for (String sid : publishedSurveyFilter.getVisibleQuestions()) {
					String uid = uidsById.get(sid);
					if (uid != null) {
						String id = idsByUid.get(uid);
						if (id != null) {
							newids.add(id);
						}
					}
				}
				newPublishedSurveyFilter.setVisibleQuestions(newids);

				newids = new HashSet<>();
				for (String sid : publishedSurveyFilter.getExportedQuestions()) {
					String uid = uidsById.get(sid);
					if (uid != null) {
						String id = idsByUid.get(uid);
						if (id != null) {
							newids.add(id);
						}
					}
				}
				newPublishedSurveyFilter.setExportedQuestions(newids);
			}

			// update result filter of users
			newPublishedSurveyFilter.setSurveyId(newPublishedSurvey.getId());
			if (publishedSurveyFilter.getUserId() != null) {
				sessionService.internalSetLastResultFilter(newPublishedSurveyFilter, publishedSurveyFilter.getUserId(),
						newPublishedSurveyFilter.getSurveyId());
			}			
		}		

		update(draftSurvey, true);

		if (sendListFormMail) {
			sendListFormMail(draftSurvey);
		}

		if (draftSurvey.getIsOPC()) {
			sendOPCApplyChangesMail(draftSurvey, userId);
		}

		fileService.deleteOldSurveyPDFs(publishedSurvey.getUniqueId(), publishedSurvey.getId());
		answerService.deleteStatisticsForSurvey(publishedSurvey.getId());

		reportingService.addToDo(ToDo.CHANGEDSURVEY, draftSurvey.getUniqueId(), null);

		return newPublishedSurvey.getId();
	}

	@Transactional
	public void makeDirty(int id) {
		Session session = sessionFactory.getCurrentSession();
		Survey survey = session.get(Survey.class, id);
		session.setReadOnly(survey, false);
		survey.setHasPendingChanges(true);
		session.update(survey);
	}

	@Transactional(readOnly = false)
	public void makeClean(int id) {
		Session session = sessionFactory.getCurrentSession();

		@SuppressWarnings("unchecked")
		Query<Survey> query = session.createQuery("UPDATE Survey s SET s.hasPendingChanges = false WHERE s.id = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public Survey editSave(Survey oldsurvey, HttpServletRequest request) throws InvalidXHTMLException,
			NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, IOException {
		Session session = sessionFactory.getCurrentSession();

		Map<String, Integer> referencedFiles = oldsurvey.getReferencedFileUIDs(contextpath);
		Map<String, Integer> fileIDsByUID = new HashMap<>();

		// File and dependent elements are recreated during parsing
		for (Element element : oldsurvey.getElements()) {
			if (element instanceof Download) {
				Download download = (Download) element;

				for (File file : download.getFiles()) {
					fileIDsByUID.put(file.getUid(), file.getId());
				}

				download.getFiles().clear();
			} else if (element instanceof Confirmation) {
				Confirmation confirmation = (Confirmation) element;

				for (File file : confirmation.getFiles()) {
					fileIDsByUID.put(file.getUid(), file.getId());
				}

				confirmation.getFiles().clear();
			} else if (element instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) element;

				for (File file : gallery.getFiles()) {
					fileIDsByUID.put(file.getUid(), file.getId());
				}

				gallery.getFiles().clear();
			}
		}

		oldsurvey = (Survey) session.merge(oldsurvey);
		session.update(oldsurvey);

		Survey survey = SurveyHelper.parseSurvey(request, this, fileService, selfassessmentService, servletContext,
				activityService.isEnabled(ActivityRegistry.ID_ELEMENT_ORDER), activityService.isEnabled(ActivityRegistry.ID_ELEMENT_UPDATED), fileIDsByUID);

		Map<Element, Integer> pendingChanges = surveyService.getPendingChanges(survey);

		update(survey, pendingChanges.size() > 0, true, false, sessionService.getCurrentUser(request).getId());

		Map<String, Integer> referencedFilesNew = survey.getReferencedFileUIDs(contextpath);

		for (Entry<String, Integer> entry : referencedFiles.entrySet()) {
			if (!referencedFilesNew.containsKey(entry.getKey())) {
				fileService.delete(entry.getValue());
				fileService.deleteIfNotReferenced(entry.getKey(), survey.getUniqueId());
			}
		}

		activityService.log(survey.getActivitiesToLog(), sessionService.getCurrentUser(request).getId(),
				survey.getUniqueId());

		survey = getSurvey(survey.getId(), false, false);

		return survey;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public Survey update(Survey survey, boolean hasPendingChanges, boolean synchronizeTranslations, boolean mergeFirst,
			int userId) {
		Session session = sessionFactory.getCurrentSession();
		survey.setHasPendingChanges(hasPendingChanges);
		survey.setUpdated(new Date());

		if (survey.getRegistrationForm()) {
			boolean[] result = checkRegistrationFormElements(survey);
			if (!result[0] || !result[1]) {
				survey.setRegistrationForm(false);
			}
		}

		if (mergeFirst) {
			Survey existing = session.get(Survey.class, survey.getId());
			session.evict(existing);
			survey = (Survey) session.merge(survey);
			session.setReadOnly(survey, false);
		}
		session.update(survey);

		session.flush();

		UpdatePossibleAnswers(survey);
		session.update(survey);

		session.flush();

		if (synchronizeTranslations)
			synchronizeTranslation(survey, userId);

		if (survey.getTranslations() != null)
			for (String lang : survey.getTranslations()) {
				try {
					java.io.File target = fileService.getSurveyPDFFile(survey.getUniqueId(), survey.getId(), lang);
					Files.deleteIfExists(target.toPath());
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}

		return survey;
	}

	public boolean[] checkRegistrationFormElements(Survey survey) {
		boolean[] result = new boolean[2];

		// check if name and email questions exist
		for (Element element : survey.getElements()) {
			if (element instanceof Question) {
				Question question = (Question) element;

				if (question.getIsAttribute() && !question.getOptional()) {
					if (question.getAttributeName().equalsIgnoreCase("name"))
						result[0] = true;
					if (question.getAttributeName().equalsIgnoreCase(Constants.EMAIL))
						result[1] = true;
					if (result[0] && result[1])
						break;
				}
			}
		}

		return result;
	}

	@Transactional
	public Survey update(Survey survey, boolean evict) {
		Session session = sessionFactory.getCurrentSession();
		if (evict) {
			session.evict(survey);
			survey = (Survey) session.merge(survey);
		}
		session.update(survey);
		return survey;
	}

	private void synchronizeTranslation(Survey survey, int userId) {
		Map<String, String> oldInfo = null;
		Translations translations = TranslationsHelper.getTranslations(survey, false);
		if (translations != null) {
			Translations originalTranslations = translationService.getTranslations(survey.getId(),
					survey.getLanguage().getCode());
			if (originalTranslations != null) {
				oldInfo = originalTranslations.getInfo();
				Map<String, Translation> originalTranslationsMap = originalTranslations.getTranslationsByKey();
				Map<String, Translation> newTranslationsMap = translations.getTranslationsByKey();
				boolean structureChanges = false;
				for (Translation translation : translations.getTranslations()) {
					if (!originalTranslationsMap.containsKey(translation.getKey())
							&& !translation.getKey().endsWith("help") && !translation.getKey().endsWith("UNIT")
							&& !translation.getKey().endsWith("TABTITLE")) {
						structureChanges = true;
						break;
					}
				}

				List<String> deletedElements = new ArrayList<>();
				for (Translation originalTranslation : originalTranslations.getTranslations()) {
					if (!newTranslationsMap.containsKey(originalTranslation.getKey()) && !originalTranslation.getKey().endsWith("help") && !originalTranslation.getKey().endsWith("UNIT")
							&& !originalTranslation.getKey().endsWith("TABTITLE")
							&& !deletedElements.contains(originalTranslation.getKey())) {
						deletedElements.add(originalTranslation.getKey());
					}					
				}

				List<Translations> allTranslations = translationService.getTranslationsForSurvey(survey.getId(), false);

				if (structureChanges) {
					// invalidate all other translations
					for (Translations invalidTranslations : allTranslations) {
						if (invalidTranslations.getActive() && !invalidTranslations.getLanguage().getCode()
								.equalsIgnoreCase(survey.getLanguage().getCode())) {
							invalidTranslations.setActive(false);
							translationService.save(invalidTranslations);
						}
					}
				}

				for (Translations existingTranslations : allTranslations) {
					boolean changed = false;
					for (String key : deletedElements) {
						if (!existingTranslations.getLanguage().getCode()
								.equalsIgnoreCase(survey.getLanguage().getCode())) {
							changed = changed || existingTranslations.removeTranslationByKey(key);
						}
					}
					if (changed) {
						translationService.save(existingTranslations);
					}
				}
			}

			translationService.deleteTranslations(survey.getId(), survey.getLanguage().getCode());
			translations.setComplete(TranslationsHelper.isComplete(translations, survey));
			translationService.save(translations);

			activityService.logTranslations(ActivityRegistry.ID_TRANSLATION_MODIFIED, translations.getLanguage().getCode(), oldInfo, translations.getInfo(),
					userId, survey.getUniqueId());
		}
	}

	private void deleteSurveyData(int id, boolean deleteAnswers, boolean deleteAccesses, String uid,
			boolean deleteLogs) throws IOException {
		Session session = sessionFactory.getCurrentSession();

		if (deleteAnswers) {
			NativeQuery query0 = session.createSQLQuery(
					"SELECT fi.FILE_UID from FILES fi JOIN ANSWERS_FILES f ON fi.FILE_ID = f.files_FILE_ID JOIN ANSWERS a ON f.ANSWERS_ANSWER_ID = a.ANSWER_ID JOIN ANSWERS_SET an ON a.AS_ID = an.ANSWER_SET_ID WHERE an.SURVEY_ID = :id");
			@SuppressWarnings("unchecked")
			List<String> fileuids = query0.setParameter("id", id).list();

			NativeQuery query = session.createSQLQuery(
					"DELETE d.* from DRAFTS d INNER JOIN ANSWERS_SET an ON d.answerSet_ANSWER_SET_ID = an.ANSWER_SET_ID where an.SURVEY_ID = :id");
			query.setParameter("id", id).executeUpdate();

			NativeQuery query1 = session.createSQLQuery(
					"DELETE f.* from ANSWERS_FILES f JOIN ANSWERS a ON f.ANSWERS_ANSWER_ID = a.ANSWER_ID JOIN ANSWERS_SET an ON a.AS_ID = an.ANSWER_SET_ID WHERE an.SURVEY_ID = :id");
			query1.setParameter("id", id).executeUpdate();

			NativeQuery query2 = session.createSQLQuery(
					"DELETE a.* from ANSWERS a JOIN ANSWERS_SET an ON a.AS_ID = an.ANSWER_SET_ID WHERE an.SURVEY_ID = :id");
			query2.setParameter("id", id).executeUpdate();

			answerExplanationService.deleteExplanationFilesOfSurvey(id);
			answerExplanationService.deleteCommentsOfSurvey(id);
			answerExplanationService.deleteExplanationsOfSurvey(id);

			@SuppressWarnings("unchecked")
			Query<AnswerSet> query3 = session.createQuery("DELETE from AnswerSet a where a.surveyId = :id");
			query3.setParameter("id", id).executeUpdate();

			for (String fileuid : fileuids) {
				java.io.File file = fileService.getSurveyFile(uid, fileuid);
				if (file.exists()) {
					file.delete();
				}
			}

			NativeQuery query4 = session
					.createSQLQuery("SELECT v.VALIDCODE_CODE FROM VALIDCODE v WHERE v.VALIDCODE_SURVEYUID = :uid");
			@SuppressWarnings("unchecked")
			List<String> codes = query4.setParameter("uid", uid).list();
			for (String code : codes) {
				java.io.File folder = fileService.getSurveyFile(uid, code);
				if (folder.exists() && folder.isDirectory()) {
					try {
						FileUtils.deleteDirectory(folder);
					} catch (IOException e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}
			}

			NativeQuery query5 = session.createSQLQuery("DELETE FROM VALIDCODE WHERE VALIDCODE_SURVEYUID = :uid");
			query5.setParameter("uid", uid).executeUpdate();
			
			NativeQuery query6 = session.createSQLQuery("DELETE FROM DELETEDCONTRIBUTIONS WHERE DELETEDCONTRIBUTIONS_SURVEY = :uid");
			query6.setParameter("uid", uid).executeUpdate();
			
		}

		//NativeQuery query = session.createSQLQuery("DELETE FROM TRANSLATION WHERE SURVEY_ID = :id");
		NativeQuery query = session.createSQLQuery("DELETE t FROM TRANSLATION t JOIN TRANSLATIONS ts ON t.TRANS_ID = ts.TRANSLATIONS_ID WHERE ts.SURVEY_ID = :id");
		query.setParameter("id", id).executeUpdate();

		query = session.createSQLQuery("DELETE FROM TRANSLATIONS WHERE SURVEY_ID = :id");
		query.setParameter("id", id).executeUpdate();
		
		query = session.createSQLQuery("DELETE pae.* FROM POSSIBLEANSWER_ELEMENT pae LEFT JOIN ELEMENTS e3 ON e3.ID = pae.dependentElements_ID LEFT JOIN SURVEYS_ELEMENTS se ON se.elements_ID = e3.ID LEFT JOIN SURVEYS s ON s.SURVEY_ID = se.SURVEYS_SURVEY_ID WHERE s.SURVEY_UID = :uid");
		query.setParameter("uid", uid).executeUpdate();

		if (deleteAccesses) {
			List<Access> accesses = getAccesses(id);
			for (Access access : accesses) {
				if (access.getId() > 0)
					session.delete(access);
			}
		}

		exportService.deleteSurveyExports(id);

		if (deleteLogs) {
			activityService.deleteLogsForSurvey(uid);
		}

		List<ParticipationGroup> groups = participationService.getAll(id);
		for (ParticipationGroup group : groups) {
			NativeQuery query6 = session.createSQLQuery("DELETE FROM INVITATIONS WHERE PARTICIPATIONGROUP_ID = :id");
			query6.setParameter("id", group.getId()).executeUpdate();

			session.delete(group);
		}
	}

	@Transactional
	public void delete(int id, boolean deleteLogs, boolean deleteFileMappings) throws Exception {
		deleteNoTransaction(id, deleteLogs, deleteFileMappings);
	}

	public void deleteNoTransaction(int id, boolean deleteLogs, boolean deleteFileMappings) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		Survey s = this.getSurvey(id, false, false, false, false);
		String uid = s.getUniqueId();

		List<Integer> surveyIDs = surveyService.getAllSurveyVersions(s.getShortname(), s.getUniqueId());
		fileService.deleteFilesForSurveys(s.getUniqueId());

		if (contextpath == null || contextpath.trim().length() == 0) {
			throw new MessageException("contextpath empty");
		}

		if (deleteFileMappings) {
			deleteFileMappings(surveyIDs);
		}

		// delete draft
		Map<String, Integer> referencedFiles = s.getReferencedFileUIDs(contextpath);
		deleteSurveyData(id, true, true, s.getUniqueId(), deleteLogs);
		session.delete(s);
	
		for (Entry<String, Integer> entry : referencedFiles.entrySet()) {
			if (entry.getValue() == null) {
				// delete files belonging to images and background documents
				fileService.deleteIfNotReferenced(entry.getKey(), s.getUniqueId());
			}
		}

		// delete published versions
		surveyIDs = this.getAllPublishedSurveyVersions(uid);
		for (Integer sid : surveyIDs) {
			s = this.getSurvey(sid, false, false, false, false);
			referencedFiles = s.getReferencedFileUIDs(contextpath);
			deleteSurveyData(s.getId(), true, true, s.getUniqueId(), deleteLogs);
			session.delete(s);
			for (Entry<String, Integer> entry : referencedFiles.entrySet()) {
				if (entry.getValue() == null) {
					// delete files belonging to images and background documents
					fileService.deleteIfNotReferenced(entry.getKey(), s.getUniqueId());
				}
			}
		}

		if (s.getIsSelfAssessment())
			selfassessmentService.deleteData(s.getUniqueId());

		java.io.File folder = fileService.getSurveyFolder(s.getUniqueId());
		try {
			FileUtils.deleteDirectory(folder);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		reportingService.addToDo(ToDo.DELETEDSURVEY, s.getUniqueId(), null);
	}

	private void deleteFileMappings(List<Integer> surveyIDs) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "delete from ELEMENTS_FILES" + " where ELEMENTS_FILES.ELEMENTS_ID"
				+ " in (select ELEMENTS.ID from ELEMENTS" + " where ELEMENTS.ID"
				+ " in (select SURVEYS_ELEMENTS.elements_ID" + " from SURVEYS_ELEMENTS"
				+ " where SURVEYS_ELEMENTS.SURVEYS_SURVEY_ID = :id));";

		for (int id : surveyIDs) {
			NativeQuery query = session.createSQLQuery(sql);
			query.setParameter("id", id);
			int deleted = query.executeUpdate();
			logger.info("deleted " + deleted + " file mappings for survey " + id);
		}
	}

	@Transactional(readOnly = true)
	public List<Language> getLanguages() {
		Session session = sessionFactory.getCurrentSession();
		Query<Language> query = session.createQuery("FROM Language l order by l.code asc", Language.class);
		return query.list();
	}

	@Transactional(readOnly = true)
	public Language getLanguage(String code) {
		return getLanguage(code, sessionFactory.getCurrentSession());
	}

	public Language getLanguage(String code, Session session) {
		Query<Language> query = session.createQuery("FROM Language l WHERE l.code = :code", Language.class).setParameter("code", code);

		List<Language> result = query.list();

		if (result.isEmpty()) {
			logger.error("ivalid language code: " + code);
			return null;
		}

		return result.get(0);
	}

	@Transactional
	public void saveLanguages(List<Language> langs) {
		Session session = sessionFactory.getCurrentSession();
		for (Language l : langs) {
			try {
				session.save(l);
			} catch (Exception e) {
				logger.error("language " + l.getEnglishName() + " could not be imported");
			}
		}

	}

	public boolean getIsSurveyPublished(String shortname) {
		Survey s = this.getSurvey(shortname, false, false, false, false, null, true, false);
		return s != null;
	}

	@Transactional(readOnly = true)
	public List<String> getLanguageCodes() {
		Session session = sessionFactory.getCurrentSession();
		Query<String> query = session.createQuery("Select l.code FROM Language l", String.class);
		return query.list();
	}

	private Map<String, String> importDraftSurvey(Survey copy, ImportResult result, User user) {
		if (copy.getSkin() != null) {
			Skin skin = skinService.get(copy.getSkin().getId());
			if (skin == null) {
				copy.setSkin(null);
			}
		}

		Map<String, String> oldToNewUniqueIds = new HashMap<>();
		oldToNewUniqueIds.put("", ""); // leave blank for old surveys that have no uniqueIds

		// recreate unique ids
		for (Element elem : copy.getElementsRecursive(true)) {
			String newUniqueId = UUID.randomUUID().toString();
			if (!oldToNewUniqueIds.containsKey(elem.getUniqueId())) {
				oldToNewUniqueIds.put(elem.getUniqueId(), newUniqueId);
			}
			elem.setUniqueId(newUniqueId);
		}

		Map<Integer, Element> elementsBySourceId = copy.getElementsBySourceId();

		if (result.isFromIPM()) {
			// recreate dependencies
			for (int elem : result.getOriginalDependencies().keySet()) {
				PossibleAnswer answer = (PossibleAnswer) elementsBySourceId.get(elem);

				for (String originalId : result.getOriginalDependencies().get(elem)) {
					Element dependent = elementsBySourceId.get(result.getOriginalIdsToNewIds().get(originalId));
					answer.getDependentElements().getDependentElements().add(dependent);

					if (result.getAdditionalElements().containsKey(dependent.getSourceId())) {
						List<Integer> ids = result.getAdditionalElements().get(dependent.getSourceId());
						for (Integer id : ids) {
							Element upload = elementsBySourceId.get(id);
							answer.getDependentElements().getDependentElements().add(upload);
						}
					}
				}
			}

			for (String elems : result.getOriginalMatrixDependencies().keySet()) {
				String[] data = elems.split("#");
				int answerpos = Integer.parseInt(data[1]);
				String questionoriginal = data[0];
				int question = elementsBySourceId.get(result.getOriginalIdsToNewIds().get(questionoriginal)).getId();
				String matrix = data[2];

				Matrix m = (Matrix) elementsBySourceId.get(result.getOriginalIdsToNewIds().get(matrix));

				for (String originalId : result.getOriginalMatrixDependencies().get(elems)) {
					Element dependent = elementsBySourceId.get(result.getOriginalIdsToNewIds().get(originalId));

					DependencyItem dep = new DependencyItem();

					int k = 0;
					for (Element q : m.getQuestions()) {
						if (q.getId() == question) {
							k += answerpos;
							break;
						}

						k += m.getAnswers().size();
					}

					dep.setPosition(k);
					dep.getDependentElements().add(dependent);

					if (result.getAdditionalElements().containsKey(dependent.getSourceId())) {
						List<Integer> ids = result.getAdditionalElements().get(dependent.getSourceId());
						for (Integer id : ids) {
							Element upload = elementsBySourceId.get(id);
							dep.getDependentElements().add(upload);
						}
					}

					m.getDependentElements().add(dep);
				}
			}
		}

		if (result.getActiveSurvey() != null) {
			copy.setIsActive(false);
			copy.setIsPublished(true);
		}

		this.update(copy, false, true, false, user.getId());

		return oldToNewUniqueIds;
	}

	private Survey importOldPublishedSurvey(Survey survey, User user, Map<String, String> oldToNewUniqueIds)
			throws Exception {
		Map<Integer, Integer> oldSourceIdsById2 = getSourceIdsById(survey);

		Survey copyOld = survey.copy(this, user, fileDir, false, -1, -1, false, false, true, null, null);
		copyOld.setIsDraft(false);

		if (copyOld.getSkin() != null) {
			Skin skin = skinService.get(copyOld.getSkin().getId());
			if (skin == null) {
				copyOld.setSkin(null);
			}
		}

		// update source_ids
		for (Element element : copyOld.getElementsRecursive(true)) {
			element.setSourceId(oldSourceIdsById2.get(element.getSourceId()));

			if (oldToNewUniqueIds.containsKey(element.getUniqueId())) {
				element.setUniqueId(oldToNewUniqueIds.get(element.getUniqueId()));
			} else {
				String newUniqueId = UUID.randomUUID().toString();
				oldToNewUniqueIds.put(element.getUniqueId(), newUniqueId);
				element.setUniqueId(newUniqueId);
			}
		}

		this.update(copyOld, false, true, false, user.getId());
		return copyOld;
	}

	private void importPublishedSurvey(Survey copyActive, ImportResult result, User user,
			Map<String, String> oldToNewUniqueIds, Map<Integer, Integer> oldSourceIdsById) {
		copyActive.setIsDraft(false);

		if (copyActive.getSkin() != null) {
			Skin skin = skinService.get(copyActive.getSkin().getId());
			if (skin == null) {
				copyActive.setSkin(null);
			}
		}

		// update source_ids
		for (Element element : copyActive.getElementsRecursive(true)) {
			element.setSourceId(oldSourceIdsById.get(element.getSourceId()));

			if (oldToNewUniqueIds.containsKey(element.getUniqueId())) {
				element.setUniqueId(oldToNewUniqueIds.get(element.getUniqueId()));
			} else {
				String newUniqueId = UUID.randomUUID().toString();
				oldToNewUniqueIds.put(element.getUniqueId(), newUniqueId);
				element.setUniqueId(newUniqueId);
			}
		}

		if (result.getActiveAnswerSets() != null && !result.getActiveAnswerSets().isEmpty()) {
			copyActive.setIsActive(true);
			copyActive.setIsPublished(true);
		}

		this.update(copyActive, false, true, false, user.getId());
	}

	@Transactional
	public void setBrpAccess(Survey survey) {
		if (survey.getIsOPC() && opcusers != null && opcusers.length() > 0) {
			String[] users = opcusers.split(";");
			int counter = 1;
			for (String user : users) {
				if (user.length() > 0) {
					User opcuser = administrationService.getUserForLogin(user);
					if (opcuser != null) {
						Access a = new Access();
						a.setUser(opcuser);
						a.setSurvey(survey);
						
						if (counter == 1) {
							a.getLocalPrivileges().put(LocalPrivilege.AccessResults, 1);
							a.getLocalPrivileges().put(LocalPrivilege.FormManagement, 2);
						} else if (counter == 2) {
							a.getLocalPrivileges().put(LocalPrivilege.AccessResults, 1);
							a.getLocalPrivileges().put(LocalPrivilege.FormManagement, 1);
						} else {
							a.getLocalPrivileges().put(LocalPrivilege.FormManagement, 1);
						}	
						
						counter++;

						surveyService.saveAccess(a);
					}
				}
			}
		}

		if (survey.getIsOPC() && opcdepartments != null && opcdepartments.length() > 0) {
			String[] departments = opcdepartments.split(";");
			for (String department : departments) {
				if (department.length() > 0) {
					String[] opcdepartment = ldapDBService.getDepartments(null, department, false, false);

					if (opcdepartment != null && opcdepartment.length > 0) {
						Access a = new Access();
						a.setDepartment(department);
						a.setSurvey(survey);
						a.getLocalPrivileges().put(LocalPrivilege.AccessDraft, 2);
						a.getLocalPrivileges().put(LocalPrivilege.AccessResults, 1);
						a.getLocalPrivileges().put(LocalPrivilege.FormManagement, 2);//

						surveyService.saveAccess(a);
					}
				}
			}
		}
	}
	
	Map<String, Integer> getEvaluationCriteriaMappingsAndSetToNull(Survey survey) {
		Map<String, Integer> oldEvaluationCriteriaMappings = new HashMap<String, Integer>();
		for (Question question : survey.getQuestions()) {
			if (question instanceof SingleChoiceQuestion) {
				SingleChoiceQuestion scq = (SingleChoiceQuestion) question;
				if (scq.getIsSAQuestion() && scq.getEvaluationCriterion() != null) {
					oldEvaluationCriteriaMappings.put(scq.getUniqueId(), scq.getEvaluationCriterion().getId());
					scq.setEvaluationCriterion(null);
				}
			}
		}
		return oldEvaluationCriteriaMappings;
	}
	
	Map<String, Integer> getUpdatedEvaluationCriteriaMappingsAndSetToNull(Map<String, Integer> oldEvaluationCriteriaMappings, Map<String, String> oldToNewUniqueIds) {
		Map<String, Integer> evaluationCriteriaMappings = new HashMap<String, Integer>();
		for (String oldUid : oldEvaluationCriteriaMappings.keySet()) {
			evaluationCriteriaMappings.put(oldToNewUniqueIds.get(oldUid), oldEvaluationCriteriaMappings.get(oldUid));
		}
		return evaluationCriteriaMappings;
	}

	@Transactional
	public int importSurvey(ImportResult result, User user, boolean isRestore) throws Exception {
		
		//the copy call actually saves the survey to the database so we have to remove the evaluation criteria mappings as they do not exist yet and would create an constraint exception
		Map<String, Integer> oldEvaluationCriteriaMappings = getEvaluationCriteriaMappingsAndSetToNull(result.getSurvey());
		Survey copy = result.getSurvey().copy(this, user, fileDir, false, -1, -1, false, false, true, null, null);
		
		if (isRestore && copy.getIsOPC()) {
			setBrpAccess(copy);
		}

		Survey copyActive;
		try {
			Map<String, String> missingfiles = new HashMap<>();
			Map<String, String> convertedUIDs = surveyService.copyFiles(copy, new HashMap<>(), true, missingfiles,
					result.getSurvey().getUniqueId());

			Map<String, String> oldToNewUniqueIds = importDraftSurvey(copy, result, user);			
			Map<String, Integer> evaluationCriteriaMappings = getUpdatedEvaluationCriteriaMappingsAndSetToNull(oldEvaluationCriteriaMappings, oldToNewUniqueIds);
			Map<Integer, SACriterion> oldIdToNewCriteria = new HashMap<>();
			
			importSurveyData(result, true, copy, oldToNewUniqueIds, result.getSurvey().getId(), convertedUIDs, evaluationCriteriaMappings, oldIdToNewCriteria);

			if (result.getActiveSurvey() != null && result.getActiveAnswerSets() != null
					&& !result.getActiveAnswerSets().isEmpty()) {
				Map<Integer, Integer> oldSourceIdsById = getSourceIdsById(result.getActiveSurvey());
				for (int id : result.getOldSurveys().keySet()) {
					oldEvaluationCriteriaMappings = getEvaluationCriteriaMappingsAndSetToNull(result.getOldSurveys().get(id));
					Survey copyOld = importOldPublishedSurvey(result.getOldSurveys().get(id), user, oldToNewUniqueIds);
					evaluationCriteriaMappings = getUpdatedEvaluationCriteriaMappingsAndSetToNull(oldEvaluationCriteriaMappings, oldToNewUniqueIds);
					importSurveyData(result, false, copyOld, oldToNewUniqueIds, id, convertedUIDs, evaluationCriteriaMappings, oldIdToNewCriteria);
				}
				
				oldEvaluationCriteriaMappings = getEvaluationCriteriaMappingsAndSetToNull(result.getActiveSurvey());
				copyActive = result.getActiveSurvey().copy(this, user, fileDir, false, -1, -1, false, false, true, null,
						null);
				surveyService.copyFiles(copyActive, convertedUIDs, true, missingfiles,
						result.getSurvey().getUniqueId());
				evaluationCriteriaMappings = getUpdatedEvaluationCriteriaMappingsAndSetToNull(oldEvaluationCriteriaMappings, oldToNewUniqueIds);

				importPublishedSurvey(copyActive, result, user, oldToNewUniqueIds, oldSourceIdsById);
				importSurveyData(result, false, copyActive, oldToNewUniqueIds, result.getActiveSurvey().getId(), convertedUIDs, evaluationCriteriaMappings, oldIdToNewCriteria);
			}

			if (missingfiles.size() > 0) {
				Locale locale = new Locale(user.getLanguage().toLowerCase());

				StringBuilder text = new StringBuilder();
				String link = "<a href='" + host + copy.getShortname() + "/management/overview'>" + copy.getShortname()
						+ "</a>";
				text.append(resources.getMessage("error.ProblemDuringRestore", new Object[] { link },
						"Error during operation", locale));

				text.append("<table class='table table-bordered'><tr><th>File Name</th><th>Type</th></tr>");

				for (Entry<String, String> entry : missingfiles.entrySet()) {
					text.append("<tr><td>");
					text.append(entry.getKey());
					text.append("</td><td>");
					text.append(entry.getValue());
					text.append("</td></tr>");
				}

				text.append("</table>");
				text.append(
						resources.getMessage("error.ProblemDuringRestore2", null, "Error during operation", locale));

				Message m = new Message();
				m.setType(4);
				m.setCriticality(3);
				m.setText(text.toString());
				m.setUserId(user.getId());
				systemService.save(m);
			}

		} catch (Exception e) {
			if (isRestore && copy.getId() > 0) {
				// mark survey as archived so it is not visible
				copy.setArchived(true);
				surveyService.update(copy, true);
			}

			if (e instanceof ConstraintViolationException && copy != null && copy.getId() > 0) {
				logger.error(e.getLocalizedMessage(), e);
				throw new SurveyException(copy.getId());
			} else {
				throw e;
			}
		}

		return copy.getId();
	}

	private void importSurveyData(ImportResult result, boolean draft, Survey survey,
			Map<String, String> oldToNewUniqueIds, Integer surveyid, Map<String, String> convertedFileUIDs, Map<String, Integer> evaluationCriteriaMappings, Map<Integer, SACriterion> oldIdToNewCriteria) throws Exception {
		List<Translations> translations;
		List<AnswerSet> answerSets = new ArrayList<>();
		Map<Integer, List<File>> files;
		
		List<AnswerExplanation> explanations;
		List<AnswerComment> comments;
		
		if (draft) {
			translations = result.getTranslations();
			answerSets = result.getAnswerSets();
			files = result.getFiles();
			explanations = result.getExplanations();
			comments = result.getComments();	
			selfassessmentService.importData(result, survey, evaluationCriteriaMappings, oldIdToNewCriteria);
		} else {
			if (surveyid.equals(result.getActiveSurvey().getId())) {
				translations = result.getActiveTranslations();
			} else {
				translations = result.getOldTranslations().get(surveyid);
			}

			if (result.getActiveAnswerSets() != null)
				for (List<AnswerSet> list : result.getActiveAnswerSets()) {
					for (AnswerSet as : list) {
						if (as.getSurveyId() == surveyid) {
							answerSets.add(as);
						}
					}
				}

			files = result.getActiveFiles();
			
			explanations = result.getActiveExplanations();
			comments = result.getActiveComments();
			selfassessmentService.adaptEvaluationCriteria(survey, evaluationCriteriaMappings, oldIdToNewCriteria);
		}
	
		if (translations != null) {
			copyTranslations(translations, survey, oldToNewUniqueIds, result, true, convertedFileUIDs);
		}

		if (answerSets != null && !answerSets.isEmpty()) {
			logger.info("starting import of answers");

			Set<String> rankingQuestions = new HashSet<>();
			Set<String> galleryQuestions = new HashSet<>();
			for (Element element : survey.getElementsRecursive()) {
				if (element instanceof RankingQuestion) {
					rankingQuestions.add(element.getUniqueId());
				} else if (element instanceof GalleryQuestion) {
					galleryQuestions.add(element.getUniqueId());
				}
			}			

			Set<AnswerSet> answerSets2 = new HashSet<>();
			
			Map<Integer, List<AnswerExplanation>> explanationsByAnswerId = new HashMap<>();
			if (explanations != null) {
				for (AnswerExplanation explanation : explanations) {
					if (!explanationsByAnswerId.containsKey(explanation.getAnswerSetId()))
					{
						explanationsByAnswerId.put(explanation.getAnswerSetId(), new ArrayList<>());
					}
					
					explanationsByAnswerId.get(explanation.getAnswerSetId()).add(explanation);
				}
			}
			
			Map<Integer, List<AnswerComment>> commentsByAnswerId = new HashMap<>();
			if (comments != null) {
				for (AnswerComment comment : comments) {
					if (!commentsByAnswerId.containsKey(comment.getAnswerSetId()))
					{
						commentsByAnswerId.put(comment.getAnswerSetId(), new ArrayList<>());
					}
					
					commentsByAnswerId.get(comment.getAnswerSetId()).add(comment);
				}
			}

			while (!answerSets.isEmpty()) {
				AnswerSet a = answerSets.remove(0);

				AnswerSet b = a.copy(survey, files);

				for (Answer an : b.getAnswers()) {
					if (!oldToNewUniqueIds.containsKey(an.getQuestionUniqueId())) {
						//If the old unique id is not available it probably belongs to a deleted question
						//Make up a new id and save it to avoid problems
						oldToNewUniqueIds.put(an.getQuestionUniqueId(), UUID.randomUUID().toString());
					}
					an.setQuestionUniqueId(oldToNewUniqueIds.get(an.getQuestionUniqueId()));

					if (an.getPossibleAnswerUniqueId() != null) {
						if (!galleryQuestions.contains(an.getQuestionUniqueId())) {	
							if (an.getPossibleAnswerUniqueId().equals("TARGETDATASET")) {
								if (result.getOldToNewDatasetIDs().containsKey(an.getValue())) {
									an.setValue(result.getOldToNewDatasetIDs().get(an.getValue()));
								}
							} else {
								if (!oldToNewUniqueIds.containsKey(an.getPossibleAnswerUniqueId())) {
									oldToNewUniqueIds.put(an.getPossibleAnswerUniqueId(), UUID.randomUUID().toString());
								}
								an.setPossibleAnswerUniqueId(oldToNewUniqueIds.get(an.getPossibleAnswerUniqueId()));
							}
						} else {
							if (convertedFileUIDs.containsKey(an.getPossibleAnswerUniqueId())) {
								an.setPossibleAnswerUniqueId(convertedFileUIDs.get(an.getPossibleAnswerUniqueId()));
							}
						}
					}
					
					if (an.getValue() != null && rankingQuestions.contains(an.getQuestionUniqueId())) {
						String[] items = an.getValue().split(";");
						String newValue = "";
						for (String uid : items) {
							newValue += oldToNewUniqueIds.get(uid) + ";";
						}
						an.setValue(newValue);
					}
				}
				
				if (explanationsByAnswerId.containsKey(a.getId())) {
					for (AnswerExplanation explanation : explanationsByAnswerId.get(a.getId())) {						
						AnswerSet.ExplanationData explanationData = new AnswerSet.ExplanationData();
						explanationData.text = explanation.getText();					
						for (File file : explanation.getFiles())
						{
							File copy = file.copy(null);
							explanationData.files.add(copy);
						}
						
						b.getExplanations().put(oldToNewUniqueIds.get(explanation.getQuestionUid()), explanationData);
					}
				}
				
				if (commentsByAnswerId.containsKey(a.getId())) {
					Map<Integer, AnswerComment> commentsByOldId = new HashMap<>();
					
					for (AnswerComment comment : commentsByAnswerId.get(a.getId())) {						
						AnswerComment copy = new AnswerComment();
						copy.setQuestionUid(oldToNewUniqueIds.get(comment.getQuestionUid()));
						copy.setUniqueCode(comment.getUniqueCode());
						copy.setText(comment.getText());
						copy.setDate(comment.getDate());
						
						commentsByOldId.put(comment.getId(), copy);
						
						if (comment.getParent() != null) {
							copy.setParent(commentsByOldId.get(comment.getParent().getId()));
						}
						
						if (!b.getComments().containsKey(oldToNewUniqueIds.get(comment.getQuestionUid())))
						{
							b.getComments().put(oldToNewUniqueIds.get(comment.getQuestionUid()), new ArrayList<>());
						}						
						
						b.getComments().get(oldToNewUniqueIds.get(comment.getQuestionUid())).add(copy);
					}
				}

				answerSets2.add(b);

				if (answerSets2.size() > 1000) {
					logger.info("1000 answers imported");
					SaveAnswerSets(answerSets2, tempFileDir, null);
					answerSets2 = new HashSet<>();
				}
			}

			SaveAnswerSets(answerSets2, tempFileDir, null);
			logger.info("finished import of answers");
		}
		
		
	}

	@Transactional
	public void SaveAnswerSets(Set<AnswerSet> answerSets, String fileDir, String draftid) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		for (AnswerSet answerSet : answerSets) {
			saveAnswerSet(answerSet, fileDir, draftid);
			session.evict(answerSet);
		}
	}

	public void saveAnswerSet(AnswerSet answerSet, String fileDir, String draftid) throws Exception {
		boolean saved = false;

		int counter = 1;

		while (!saved) {
			try {
				answerService.internalSaveAnswerSet(answerSet, fileDir, draftid, false, false);
				saved = true;
			} catch (org.hibernate.exception.LockAcquisitionException ex) {
				logger.info("lock on answerSet table catched; retry counter: " + counter);
				counter++;

				if (counter > 60) {
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}

				Thread.sleep(1000);
			}
		}
	}

	public void copyTranslations(List<Translations> translations, Survey survey, Map<String, String> oldToNewUniqueIds,
			ImportResult result, boolean newTitle, Map<String, String> convertedFileUIDs) {
		Map<Integer, Element> elementsBySourceId = survey.getElementsBySourceId();

		for (Translations tOriginal : translations) {
			Translations t = new Translations();
			t.setActive(tOriginal.getActive());
			
			Language objLang = surveyService.getLanguage(tOriginal.getLanguage().getCode());
			if (objLang == null) {
				objLang = new Language(tOriginal.getLanguage().getCode(), tOriginal.getLanguage().getName(),
						tOriginal.getLanguage().getEnglishName(), false);
			}
			
			t.setLanguage(objLang);
			if (newTitle) {
				if (survey.getLanguage().getCode().equals(tOriginal.getLanguage().getCode())) {
					t.setTitle(survey.getTitle());
				} else {
					t.setTitle("");
				}
			} else {
				t.setTitle(tOriginal.getTitle());
			}
			t.setSurveyId(survey.getId());
			t.setSurveyUid(survey.getUniqueId());
			for (Translation trOriginal : tOriginal.getTranslations()) {
				if (trOriginal.getLabel() != null) {
					Translation tr = new Translation();
					tr.setSurveyId(survey.getId());

					if (result == null || !result.isFromIPM()) {
						tr.setKey(translateKey(trOriginal.getKey(), elementsBySourceId, oldToNewUniqueIds, convertedFileUIDs));
					} else {
						if (trOriginal.getKey().equalsIgnoreCase(Survey.IPMINTRODUCTION)) {
							for (com.ec.survey.model.survey.Element element : survey.getElements()) {
								if (element instanceof Section && element.getPosition() == 0
										&& element.getShortname().equalsIgnoreCase("Introduction")) {
									Translation tr2 = new Translation();
									tr2.setSurveyId(survey.getId());
									tr2.setKey(element.getId().toString());
									tr2.setLabel(element.getTitle());
									tr2.setLanguage(trOriginal.getLanguage());
									tr2.setTranslations(t);
									tr2.setLocked(trOriginal.getLocked() || element.getLocked());
									t.getTranslations().add(tr2);
								} else if (element instanceof Text && element.getShortname() != null
										&& element.getShortname().equalsIgnoreCase("introduction")
										&& element.getPosition() < 2) {
									tr.setKey(element.getUniqueId());
									break;
								}
							}
						} else {

							tr.setKey(translateIPMKey(trOriginal.getKey(), elementsBySourceId, oldToNewUniqueIds,
									result));

							if (trOriginal.getKey().endsWith("desc") && trOriginal.getKey().length() > 4) {

								for (com.ec.survey.model.survey.Element element : survey.getElements()) {
									if (element instanceof Text && element.getShortname() != null
											&& element.getShortname().equals(trOriginal.getKey())) {
										tr.setKey(element.getUniqueId());
										break;
									}
								}
							}
						}
					}

					if (newTitle && trOriginal.getKey().equalsIgnoreCase("TITLE")) {
						if (trOriginal.getLanguage().equalsIgnoreCase(survey.getLanguage().getCode())) {
							tr.setLabel(survey.getTitle());
						} else {
							tr.setLabel(trOriginal.getLabel());
						}
					} else {
						tr.setLabel(trOriginal.getLabel());
					}

					tr.setLanguage(trOriginal.getLanguage());
					tr.setTranslations(t);
					t.getTranslations().add(tr);
				}
			}

			if (result != null && result.isFromIPM()) {

				for (List<Integer> ids : result.getAdditionalElements().values())
					for (Integer id : ids) {
						Element upload = elementsBySourceId.get(id);

						if (upload instanceof Upload) {
							Translation tr = new Translation();
							tr.setSurveyId(survey.getId());
							tr.setKey(elementsBySourceId.get(id).getId().toString());
							tr.setLabel("");
							tr.setLanguage(tOriginal.getLanguage().getCode());
							t.getTranslations().add(tr);
						}
					}
			}

			translationService.deleteTranslations(survey.getId(), t.getLanguage().getCode());
			translationService.add(t, true);
		}
	}

	private String translateKey(String key, Map<Integer, Element> elementsBySourceId,
			Map<String, String> oldToNewUniqueIds, Map<String, String> convertedFileUIDs) {
		if (key == null)
			return key;

		if (key.equalsIgnoreCase(Survey.TITLE))
			return key;
		if (key.equalsIgnoreCase(Survey.LOGOTEXT))
			return key;
		if (key.equalsIgnoreCase(Survey.INTRODUCTION))
			return key;
		if (key.equalsIgnoreCase(Survey.ESCAPEPAGE))
			return key;
		if (key.equalsIgnoreCase(Survey.ESCAPELINK))
			return key;
		if (key.equalsIgnoreCase(Survey.CONFIRMATIONPAGE))
			return key;
		if (key.equalsIgnoreCase(Survey.CONFIRMATIONLINK))
			return key;

		Integer retVal;
		String uid;
		try {
			if (key.endsWith("help")) {
				uid = key.substring(0, key.indexOf("help"));

				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + "help";
				}
				retVal = Integer.parseInt(uid);
				if (elementsBySourceId.containsKey(retVal))
					return elementsBySourceId.get(retVal).getUniqueId() + "help";
			} else if (key.endsWith(NumberQuestion.UNIT)) {
				uid = key.substring(0, key.indexOf(NumberQuestion.UNIT));

				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + NumberQuestion.UNIT;
				}

				retVal = Integer.parseInt(uid);
				if (elementsBySourceId.containsKey(retVal))
					return elementsBySourceId.get(retVal).getUniqueId() + NumberQuestion.UNIT;
			} else if (key.endsWith(NumberQuestion.MINLABEL)) {
				uid = key.substring(0, key.indexOf(NumberQuestion.MINLABEL));

				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + NumberQuestion.MINLABEL;
				}

				retVal = Integer.parseInt(uid);
				if (elementsBySourceId.containsKey(retVal)) {
					return elementsBySourceId.get(retVal).getUniqueId() + NumberQuestion.MINLABEL;
				}
			} else if (key.endsWith(NumberQuestion.MAXLABEL)) {
				uid = key.substring(0, key.indexOf(NumberQuestion.MAXLABEL));

				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + NumberQuestion.MAXLABEL;
				}

				retVal = Integer.parseInt(uid);
				if (elementsBySourceId.containsKey(retVal)) {
					return elementsBySourceId.get(retVal).getUniqueId() + NumberQuestion.MAXLABEL;
				}
			} else if (key.endsWith(Confirmation.LABEL)) {
				uid = key.substring(0, key.indexOf(Confirmation.LABEL));

				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + Confirmation.LABEL;
				}

				retVal = Integer.parseInt(uid);
				if (elementsBySourceId.containsKey(retVal))
					return elementsBySourceId.get(retVal).getUniqueId() + Confirmation.LABEL;
			} else if (key.endsWith(Confirmation.TEXT)) {
				uid = key.substring(0, key.indexOf(Confirmation.TEXT));

				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + Confirmation.TEXT;
				}

				retVal = Integer.parseInt(uid);
				if (elementsBySourceId.containsKey(retVal))
					return elementsBySourceId.get(retVal).getUniqueId() + Confirmation.TEXT;
			} else if (key.endsWith(Section.TABTITLE)) {
				uid = key.substring(0, key.indexOf(Section.TABTITLE));

				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + Section.TABTITLE;
				}

				retVal = Integer.parseInt(uid);
				if (elementsBySourceId.containsKey(retVal))
					return elementsBySourceId.get(retVal).getUniqueId() + Section.TABTITLE;
			} else if (key.endsWith(Constants.SHORTNAME)) {
				uid = key.substring(0, key.indexOf(Constants.SHORTNAME));

				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + Constants.SHORTNAME;
				}

				retVal = Integer.parseInt(uid);

				if (elementsBySourceId.containsKey(retVal))
					return elementsBySourceId.get(retVal).getUniqueId() + Constants.SHORTNAME;
			} else if (key.endsWith("FIRSTCELL")) {
				uid = key.substring(0, key.indexOf("FIRSTCELL"));

				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + "FIRSTCELL";
				}

				retVal = Integer.parseInt(uid);
				if (elementsBySourceId.containsKey(retVal))
					return elementsBySourceId.get(retVal).getUniqueId() + "FIRSTCELL";
			} else if (key.endsWith(GalleryQuestion.TITLE)) {
				uid = key.substring(0, key.indexOf(GalleryQuestion.TITLE));

				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + GalleryQuestion.TITLE;
				} else if (convertedFileUIDs.containsKey(uid)){
					return convertedFileUIDs.get(uid) + GalleryQuestion.TITLE;
				}
				retVal = Integer.parseInt(uid);
				if (elementsBySourceId.containsKey(retVal))
					return elementsBySourceId.get(retVal).getUniqueId() + GalleryQuestion.TITLE;
			} else if (key.endsWith(GalleryQuestion.TEXT)) {
				uid = key.substring(0, key.indexOf(GalleryQuestion.TEXT));
				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + GalleryQuestion.TEXT;
				} else if (convertedFileUIDs.containsKey(uid)){
					return convertedFileUIDs.get(uid) + GalleryQuestion.TEXT;
				}
				retVal = Integer.parseInt(uid);
				if (elementsBySourceId.containsKey(retVal))
					return elementsBySourceId.get(retVal).getUniqueId() + GalleryQuestion.TEXT;
			} else if (key.endsWith("RESULTTEXT")) {
				uid = key.substring(0, key.indexOf("RESULTTEXT"));
				if (oldToNewUniqueIds.containsKey(uid)) {
					return oldToNewUniqueIds.get(uid) + "RESULTTEXT";
				}
				retVal = Integer.parseInt(uid);
				if (elementsBySourceId.containsKey(retVal))
					return elementsBySourceId.get(retVal).getUniqueId() + "RESULTTEXT";
			} else if (oldToNewUniqueIds.containsKey(key)) {
				return oldToNewUniqueIds.get(key);
			} else if (convertedFileUIDs.containsKey(key)) {
				return convertedFileUIDs.get(key);
			} else {
				retVal = Integer.parseInt(key);
				if (elementsBySourceId.containsKey(retVal))
					return elementsBySourceId.get(retVal).getUniqueId();
			}
		} catch (NumberFormatException nfe) {
			logger.info("unknown key " + key + "found in translation");
		}

		return key;
	}

	private String translateIPMKey(String key, Map<Integer, Element> elementsBySourceId,
			Map<String, String> oldToNewUniqueIds, ImportResult result) {
		if (key.equalsIgnoreCase(Survey.TITLE))
			return key;
		if (key.equalsIgnoreCase(Survey.INTRODUCTION))
			return key;
		if (key.equalsIgnoreCase(Survey.ESCAPEPAGE))
			return key;
		if (key.equalsIgnoreCase(Survey.CONFIRMATIONPAGE))
			return key;

		String key2 = key;

		try {
			if (key.endsWith("help")) {
				String id = key.substring(0, key.indexOf("help"));
				if (result.getOriginalIdsToNewIds().containsKey(id)) {
					key2 = result.getOriginalIdsToNewIds().get(id).toString() + "help";
				}
			} else if (key.endsWith(NumberQuestion.UNIT)) {
				String id = key.substring(0, key.indexOf(NumberQuestion.UNIT));
				if (result.getOriginalIdsToNewIds().containsKey(id)) {
					key2 = result.getOriginalIdsToNewIds().get(id).toString() + NumberQuestion.UNIT;
				}
			} else if (key.endsWith(Section.TABTITLE)) {
				String id = key.substring(0, key.indexOf(Section.TABTITLE));
				if (result.getOriginalIdsToNewIds().containsKey(id)) {
					key2 = result.getOriginalIdsToNewIds().get(id).toString() + Section.TABTITLE;
				}
			} else {
				if (result.getOriginalIdsToNewIds().containsKey(key)) {
					key2 = result.getOriginalIdsToNewIds().get(key).toString();
				}
			}
		} catch (Exception e) {
			logger.info("unknown key " + key + "found in translation");
		}

		return translateKey(key2, elementsBySourceId, oldToNewUniqueIds, new HashMap<>());
	}


	@Transactional(readOnly = true)
	public List<Access> getAccesses(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		Query<Access> query = session.createQuery("FROM Access a WHERE a.survey.id = :id", Access.class).setParameter("id", id);
		return query.list();
	}

	@Transactional
	public void saveAccess(Access access) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(access);
	}

	@Transactional(readOnly = true)
	public Access getAccess(int id) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(Access.class, id);
	}

	@Transactional
	public void deleteAccess(Access access) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(access);
	}

	@Transactional(readOnly = true)
	public Access getAccess(Integer id, Integer userId) {
		Session session = sessionFactory.getCurrentSession();
		Query<Access> query = session.createQuery("FROM Access a WHERE a.survey.id = :id AND a.user.id = :userId", Access.class)
				.setParameter("id", id).setParameter("userId", userId);

		List<Access> result = query.list();
		if (!result.isEmpty())
			return result.get(0);
		return null;
	}

	@Transactional(readOnly = true)
	public Access getGroupAccess(Integer id, String groupName) {
		Session session = sessionFactory.getCurrentSession();
		Query<Access> query = session.createQuery("FROM Access a WHERE a.survey.id = :id AND a.department = :groupName", Access.class)
				.setParameter("id", id).setParameter("groupName", groupName);

		List<Access> result = query.list();
		if (!result.isEmpty())
			return result.get(0);
		return null;
	}
	

	@Transactional(readOnly = true)
	public List<ResultAccess> getResultAccesses(ResultAccess resultAccess, String uid, int page, int rows, String name, String email, String order, Locale locale) {
		Session session = sessionFactory.getCurrentSession();
		
		String hql = "FROM ResultAccess a WHERE a.surveyUID = :uid";
		
		if (name != null && name.length() > 0) {
			hql += " AND a.user IN (SELECT u.id FROM User u WHERE concat(COALESCE(u.givenName, ''), ' ', COALESCE(u.surName, ''), ' ', COALESCE(u.login, '')) LIKE :name)";
		}
		
		if (email != null && email.length() > 0) {
			hql += " AND a.user IN (SELECT u.id FROM User u WHERE u.email LIKE :email)";
		}
		
		if (resultAccess != null) {
			hql += " AND a.owner = :user"; 
		}

		//avoid SQL Injection by limiting possible values you can order by
		if (order != null && ResultAccess.getOrderFields().contains(order)) {
			hql += " ORDER BY a." + order + " DESC";
		}
		
		Query<ResultAccess> query = session.createQuery(hql, ResultAccess.class).setParameter("uid", uid);
		
		if (resultAccess != null) {
			query.setParameter("user", resultAccess.getUser());
		}
		
		if (name != null && name.length() > 0) {
			query.setParameter("name", "%" + name + "%");
		}
		
		if (email != null && email.length() > 0) {
			query.setParameter("email", "%" + email + "%");
		}
		
		List<ResultAccess> accesses = query.setFirstResult((page > 1 ? page - 1 : 0) * rows).setMaxResults(rows).list();
		
		for (ResultAccess access : accesses) {
			User user = administrationService.getUser(access.getUser());
			access.setUserName(user.getFirstLastName());
			access.setUserEmail(user.getEmail());
			
			if (access.getResultFilter() != null && !access.getResultFilter().getFilterValues().isEmpty()) {
				
				StringBuilder filter = new StringBuilder();
				
				Survey survey = getSurveyByUniqueId(access.getSurveyUID(), false, true);
				Map<String, Element> elementsByUniqueId = survey.getElementsByUniqueId();
				
				for (String questionUID : access.getResultFilter().getFilterValues().keySet()) {
					
					Element question = elementsByUniqueId.get(questionUID);
					
					if (question != null) {
					
						String value = access.getResultFilter().getFilterValues().get(questionUID);
						
						filter.append("<b>").append(question.getStrippedTitleAtMost100()).append("</b><br />");
						
						if (question instanceof ChoiceQuestion || question instanceof Text) {
							boolean first = true;
							for (String paidanduid : value.split(";")) {
								String pauid = paidanduid.contains("|") ? paidanduid.substring(paidanduid.indexOf("|") + 1) : paidanduid;
								Element answer = elementsByUniqueId.get(pauid);
								if (answer != null) {
									filter.append(answer.getStrippedTitleAtMost100());
								} else {
									filter.append(resources.getMessage("label.UnknownElement", null, "Unknown element", locale));
								}
								if (first) {
									filter.append("; ");
									first = false;
								}
							}
						} else {
							filter.append(value);
						}
						
						filter.append("</br>");
					} else {
						filter.append("<b>").append(resources.getMessage("label.UnknownElement", null, "Unknown element", locale)).append("</b><br />");
					}
				}
				
				access.setFilter(filter.toString());
			}
		}
		
		return accesses;
	}
	

	@Transactional(readOnly = true)
	public ResultAccess getResultAccess(String surveyUID, int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query<ResultAccess> query = session.createQuery("FROM ResultAccess a WHERE a.surveyUID = :uid AND a.user = :id",
				ResultAccess.class).setParameter("uid", surveyUID).setParameter("id", userId);
		List<ResultAccess> accesses = query.list();
		if (accesses.isEmpty()) {
			return null;
		}
		return accesses.get(0);
	}
	
	@Transactional
	public ResultAccess getResultAccess(int id) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(ResultAccess.class, id);
	}

	@Transactional
	public void deleteResultAccess(ResultAccess access) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(access);
	}
	
	@Transactional
	public void saveResultAccess(ResultAccess access) {
		Session session = sessionFactory.getCurrentSession();
		if (access.getDate() == null) {
			access.setDate(new Date());
		}
		session.saveOrUpdate(access);
	}

	@Transactional
	public void save(Template template) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(template);
	}

	@Transactional(readOnly = true)
	public Template getTemplate(int id) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(Template.class, id);
	}

	@Transactional
	public void deleteTemplate(Template template) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(template);
	}


	@Transactional(readOnly = true)
	public List<Template> getTemplates(Integer ownerId) {
		Session session = sessionFactory.getCurrentSession();
		Query<Template> query = session.createQuery("FROM Template t WHERE t.owner.id = :id", Template.class).setParameter("id", ownerId);
		return query.list();
	}

	@Transactional
	public void save(Language objLang) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(objLang);
	}

	@Transactional(readOnly = true)
	public List<Survey> getSurveysToStart() {
		Session session = sessionFactory.getCurrentSession();
		Query<Survey> query = session.createQuery(
				"FROM Survey s WHERE s.isDraft = true AND s.start <= :start AND ((s.end is not null AND s.end > :start) OR (s.end is null)) AND s.automaticPublishing = true AND s.isActive = false",
				Survey.class)
				.setParameter("start", new Date());
		return query.list();
	}

	@Transactional(readOnly = true)
	public List<Survey> getSurveysToStop() {
		Session session = sessionFactory.getCurrentSession();
		Query<Survey> query = session.createQuery(
				"FROM Survey s WHERE s.isDraft = true AND s.end <= :end AND s.automaticPublishing = true AND s.isPublished = true AND s.isActive = true",
				Survey.class)
				.setParameter("end", new Date());
		return query.list();
	}

	@Transactional(readOnly = true)
	public List<Survey> getSurveysToNotify() {
		Session session = sessionFactory.getCurrentSession();
		Query<Survey> query = session.createQuery(
				"FROM Survey s WHERE s.isDraft = true AND s.notified = false AND s.automaticPublishing = true AND s.end != null AND s.notificationValue != null AND s.notificationUnit != null AND s.isActive = true",
				Survey.class);
		List<Survey> surveys = query.list();

		Calendar today = Calendar.getInstance();
		today.setTime(new Date());

		List<Survey> result = new ArrayList<>();
		for (Survey survey : surveys) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(survey.getEnd());

			int val = 0;

			try {
				val = -1 * Integer.parseInt(survey.getNotificationValue());

				if (survey.getNotificationUnit().equalsIgnoreCase("0")) {
					cal.add(Calendar.HOUR_OF_DAY, val);
				} else if (survey.getNotificationUnit().equalsIgnoreCase("1")) {
					cal.add(Calendar.DAY_OF_MONTH, val);
				} else if (survey.getNotificationUnit().equalsIgnoreCase("2")) {
					cal.add(Calendar.WEEK_OF_YEAR, val);
				} else if (survey.getNotificationUnit().equalsIgnoreCase("3")) {
					cal.add(Calendar.MONTH, val);
				}

			} catch (NumberFormatException e) {
				logger.error("invalid notification value found for survey " + survey.getId());
			}

			// check if it is time to notify
			if (today.after(cal)) {
				logger.info("found survey to notify: " + survey.getUniqueId() + " ending at " + survey.getEnd() + " "
						+ survey.getNotificationValue() + " " + survey.getNotificationUnit());
				result.add(survey);
			}
		}

		return result;
	}

	@Transactional(readOnly = true)
	public Map<Element, Integer> getPendingChanges(Survey draftSurvey) {
		Map<Element, Integer> result = new HashMap<>();

		Survey publishedSurvey = getSurvey(draftSurvey.getShortname(), false, false, false, false, draftSurvey.getLanguage().getCode(),
				true, true);

		// Compare elements
		if (draftSurvey != null && publishedSurvey != null) {
			Map<String, Element> publishedElements = publishedSurvey.getElementsByUniqueId();
			for (Element element : draftSurvey.getElements()) {
				if (!publishedElements.containsKey(element.getUniqueId())) {
					// these are new elements
					result.put(element, 0);
				} else {
					Element publishedElement = publishedElements.get(element.getUniqueId());
					if (publishedElement.differsFrom(element)) {
						// these are modified elements
						result.put(element, 1);
					}
				}
			}

			// deleted elements
			Map<String, Element> draftElements = draftSurvey.getElementsByUniqueId();
			for (Element element : publishedSurvey.getElements()) {
				if (!draftElements.containsKey(element.getUniqueId())) {
					// these are deleted elements
					result.put(element, 2);
				}
			}
		} 

		boolean hasPendingChanges = false;

		if (draftSurvey != null && publishedSurvey != null) {
			hasPendingChanges = PropertiesHelper.checkForPendingChanges(draftSurvey, publishedSurvey,
					Survey::getContact,
					Survey::getMultiPaging,
					Survey::getProgressBar,
					Survey::getProgressDisplay,
					Survey::getMotivationPopup,
					Survey::getMotivationType,
					Survey::getMotivationTriggerTime,
					Survey::getMotivationTriggerProgress,
					Survey::getMotivationText,
					Survey::getMotivationPopupTitle,
					Survey::getValidatedPerPage,
					Survey::getPreventGoingBack,
					Survey::getWcagCompliance,
					Survey::getLogoInInfo,
					Survey::getLogoText,
					Survey::getSkin,
					Survey::getSectionNumbering,
					Survey::getQuestionNumbering,
					Survey::getConfirmationPage,
					Survey::getConfirmationPageLink,
					Survey::getConfirmationLink,
					Survey::getIsQuiz,
					Survey::getIsOPC,
					Survey::getIsDelphi,
					Survey::getIsEVote,
					Survey::getQuorum,
					Survey::getMinListPercent,
					Survey::getMaxPrefVotes,
					Survey::getSeatsToAllocate,
					Survey::getShowResultsTestPage,
					Survey::getShowTotalScore,
					Survey::getScoresByQuestion,
					Survey::getShowQuizIcons,
					Survey::getIsUseMaxNumberContribution,
					Survey::getIsUseMaxNumberContributionLink,
					Survey::getMaxNumberContributionText,
					Survey::getMaxNumberContributionLink,
					Survey::getMaxNumberContribution,
					Survey::getIsDelphiShowAnswersAndStatisticsInstantly,
					Survey::getIsDelphiShowStartPage,
					Survey::getIsDelphiShowAnswers,
					Survey::getMinNumberDelphiStatistics,
					Survey::getTimeLimit,
					Survey::getShowCountdown,
					Survey::getSendConfirmationEmail,
					Survey::getDedicatedResultPrivileges,
					Survey::getAllowQuestionnaireDownload,
					Survey::getRegistrationForm,
					Survey::getOrganisation
			);

			if (doesLanguageDiffer(draftSurvey.getUniqueId())){
				hasPendingChanges = true;
			}

			if (!Tools.isFileEqual(draftSurvey.getLogo(), publishedSurvey.getLogo()))
				hasPendingChanges = true;

			if (!Tools.isEqualIgnoreEmptyString(draftSurvey.getQuizWelcomeMessage(), publishedSurvey.getQuizWelcomeMessage()))
				hasPendingChanges = true;
			if (!Tools.isEqualIgnoreEmptyString(draftSurvey.getQuizResultsMessage(), publishedSurvey.getQuizResultsMessage()))
				hasPendingChanges = true;
			
			if (!hasPendingChanges && publicationDiffers(draftSurvey, publishedSurvey)) {
				hasPendingChanges = true;
			}
			
			if (!hasPendingChanges)
				for (String key : draftSurvey.getUsefulLinks().keySet()) {
					if (!publishedSurvey.getUsefulLinks().containsKey(key)
							|| !publishedSurvey.getUsefulLinks().get(key).equals(draftSurvey.getUsefulLinks().get(key))) {
						hasPendingChanges = true;
						break;
					}
				}

			if (!hasPendingChanges)
				for (String key : publishedSurvey.getUsefulLinks().keySet()) {
					if (!draftSurvey.getUsefulLinks().containsKey(key)
							|| !draftSurvey.getUsefulLinks().get(key).equals(publishedSurvey.getUsefulLinks().get(key))) {
						hasPendingChanges = true;
						break;
					}
				}

			if (!hasPendingChanges)
				for (String key : draftSurvey.getBackgroundDocuments().keySet()) {
					if (!publishedSurvey.getBackgroundDocuments().containsKey(key) || !publishedSurvey.getBackgroundDocuments()
							.get(key).equals(draftSurvey.getBackgroundDocuments().get(key))) {
						hasPendingChanges = true;
						break;
					}
				}

			if (!hasPendingChanges)
				for (String key : publishedSurvey.getBackgroundDocuments().keySet()) {
					if (!draftSurvey.getBackgroundDocuments().containsKey(key) || !draftSurvey.getBackgroundDocuments().get(key)
							.equals(publishedSurvey.getBackgroundDocuments().get(key))) {
						hasPendingChanges = true;
						break;
					}
				}
			
			if (hasPendingChanges)
				result.put(new PropertiesElement(), 1);

			// check if the order of elements has changed
			if (!hasPendingChanges && !draftSurvey.getElementsRecursiveUids().equals(publishedSurvey.getElementsRecursiveUids())) {
				hasPendingChanges = true;
				result.put(new PropertiesElement(true), 1);
			}

			List<Translations> draftTranslations = translationService.getActiveTranslationsForSurvey(draftSurvey.getId());
			List<Translations> publishedTranslations = translationService
					.getActiveTranslationsForSurvey(publishedSurvey.getId());

			Translations currentTranslations = TranslationsHelper.getTranslations(draftSurvey, false);
			Map<String, Translation> currentKeys = currentTranslations.getTranslationsByKey();

			if (checkTranslations(draftTranslations, publishedTranslations, currentKeys)
					|| checkTranslations(publishedTranslations, draftTranslations, currentKeys)) {
				result.put(new TranslationsElement(), 1);
			}
		}

		return result;
	}
	
	private boolean publicationDiffers(Survey draftSurvey, Survey publishedSurvey) {
	
		if (draftSurvey.getPublication().isAllQuestions() != publishedSurvey.getPublication().isAllQuestions()) return true;
		if (draftSurvey.getPublication().isAllContributions() != publishedSurvey.getPublication().isAllContributions()) return true;
		if (draftSurvey.getPublication().isShowContent() != publishedSurvey.getPublication().isShowContent()) return true;
		if (draftSurvey.getPublication().isShowStatistics() != publishedSurvey.getPublication().isShowStatistics()) return true;
		
		if (!draftSurvey.getPublication().isAllQuestions()) {
			if (draftSurvey.getPublication().getFilter().getVisibleQuestions().size() != publishedSurvey.getPublication().getFilter().getVisibleQuestions().size()) {
				return true;
			}
			
			Map<String, String> draftElementUIDsByID = draftSurvey.getUniqueIDsByID();			
			Map<String, String> publishedElementIDsByUID = publishedSurvey.getIDsByUniqueID();
									
			for (String id : draftSurvey.getPublication().getFilter().getVisibleQuestions()) {
				String uid = draftElementUIDsByID.get(id);
				
				if (!publishedElementIDsByUID.containsKey(uid)) {
					return true;
				}
				
				String pid = publishedElementIDsByUID.get(uid);
				if (!publishedSurvey.getPublication().getFilter().getVisibleQuestions().contains(pid)) {
					return true;
				}
			}
		}
		
		if (!draftSurvey.getPublication().isAllContributions()) {
			if (draftSurvey.getPublication().getFilter().getFilterValues().size() != publishedSurvey.getPublication().getFilter().getFilterValues().size()) {
				return true;
			}
			
			for (String id : draftSurvey.getPublication().getFilter().getFilterValues().keySet()) {
				if (!publishedSurvey.getPublication().getFilter().getFilterValues().containsKey(id)) {
					return true;
				}

				if (!draftSurvey.getPublication().getFilter().getFilterValues().get(id).equals(publishedSurvey.getPublication().getFilter().getFilterValues().get(id))) {
					return true;
				}
			}
		}
		
		return false;		
	}

	private boolean checkTranslations(List<Translations> first, List<Translations> second,
			Map<String, Translation> currentKeys) {
		Map<String, Translations> secondTranslationsByLanguageCode = new HashMap<>();
		for (Translations secondTranslations : second) {
			secondTranslationsByLanguageCode.put(secondTranslations.getLanguage().getCode(), secondTranslations);
		}

		// Go through the first translations - i.e. through the different languages of the survey
		for (Translations firstTranslations : first) {
			// Check if the firstTranslation is the same in the secondTranslation
			if (!secondTranslationsByLanguageCode.containsKey(firstTranslations.getLanguage().getCode())) {
				return true;
			}

			Translations secondTranslations = secondTranslationsByLanguageCode.get(firstTranslations.getLanguage().getCode());
			Map<String, String> secondTranslationsMap = secondTranslations.getTranslationsMap();
			for (Translation firstFilteredTranslation : firstTranslations.getFilteredTranslations()) {
				// Compare keys and labels
				if (!secondTranslationsMap.containsKey(firstFilteredTranslation.getKey())
						|| !secondTranslationsMap.get(firstFilteredTranslation.getKey()).equals(firstFilteredTranslation.getLabel())) {
							// A label is not the same or a key is not present
							// Check if the key is supposed to be present in the current survey? 
					if (firstFilteredTranslation.getLabel() != null && firstFilteredTranslation.getLabel().trim().length() > 0 && currentKeys.containsKey(firstFilteredTranslation.getKey())
							&& !firstFilteredTranslation.getKey().startsWith("ESCAPE")) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Transactional(readOnly = true)
	public Map<String, String> getLanguageNamesByCode() {
		List<Language> languages = getLanguages();
		Map<String, String> result = new HashMap<>();
		for (Language language : languages) {
			if (!result.containsKey(language.getCode())) {
				result.put(language.getCode(), language.getEnglishName());
			}
		}
		return result;
	}

	@Transactional(readOnly = true)
	public int getDBVersion() {
		Session session = sessionFactory.getCurrentSession();
		Query<Status> statusQuery = session.createQuery("FROM Status", Status.class);
		List<Status> states = statusQuery.setReadOnly(true).list();

		if (states.isEmpty()) {
			return 0;
		}

		Status status = states.get(0);
		return status.getDbversion();
	}

	@Override
	public String getFileDir() {
		return fileDir;
	}

	@Transactional
	public void createStatus(int version) {
		Session session = sessionFactory.getCurrentSession();

		Query<Status> statusQuery = session.createQuery("FROM Status", Status.class);
		List<Status> states = statusQuery.setReadOnly(true).list();

		if (states.isEmpty()) {
			Status status = new Status();
			status.setDbversion(version);
			status.setUpdateDate(new Date());
			session.saveOrUpdate(status);
		}
	}

	public void removeDependencies(Element element) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createSQLQuery("DELETE FROM POSSIBLEANSWER_ELEMENT WHERE dependentElements_ID = :id")
				.setParameter("id", element.getId());
		query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public Element getElement(int id) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(Element.class, id);
	}

	@Transactional
	public void update(Element element) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(element);
	}

	@Transactional(readOnly = true)
	public boolean answerSetExists(String uniqueCode, boolean isDraft, boolean addErrorIfExists) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "select ANSWER_SET_ID, ANSWER_SET_DATE FROM ANSWERS_SET WHERE UNIQUECODE = :uid and ISDRAFT = :draft";
		NativeQuery query = session.createSQLQuery(sql);
		query.setParameter("uid", uniqueCode);
		query.setParameter("draft", isDraft);

		@SuppressWarnings("rawtypes")
		List list = query.list();

		if (addErrorIfExists && !list.isEmpty()) {
			for (Object o : list) {
				Object[] a = (Object[]) o;
				logger.error("Existing answer set for this unique code found: " + uniqueCode + "; id: "
						+ ConversionTools.getValue(a[0]) + "; created: " + a[1]);
			}
		}

		return !list.isEmpty();
	}

	@Transactional(readOnly = true)
	public Element getParentForChildQuestion(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery("SELECT childElements_ID FROM ELEMENTS WHERE ID = :id").setParameter("id",
				id);
		int parentid = ConversionTools.getValue(query.uniqueResult());

		// questions could have been deleted from the parent -> get newest version of
		// the parent
		if (parentid > 0) {
			query = session.createSQLQuery(
					"SELECT MAX(ID) FROM ELEMENTS WHERE ELEM_UID IN (SELECT ELEM_UID FROM ELEMENTS WHERE ID = :id)")
					.setParameter("id", parentid);
			parentid = ConversionTools.getValue(query.uniqueResult());
		}

		return parentid > 0 ? getElement(parentid) : null;
	}

	@Transactional(readOnly = true)
	public Element getNewestElementByUid(String uid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery("SELECT MAX(ID) FROM ELEMENTS WHERE ELEM_UID = :uid").setParameter("uid",
				uid);
		int elementid = ConversionTools.getValue(query.uniqueResult());

		if (elementid > 0) {
			return getElement(elementid);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private List<Object> GetAllQuestionsAndPossibleAnswers(String surveyUid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(
				"SELECT DISTINCT QUESTION_UID, PA_UID FROM ANSWERS a INNER JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = a.AS_ID JOIN SURVEYS s ON  ans.SURVEY_ID =  s.SURVEY_ID WHERE s.ISDRAFT = FALSE AND s.SURVEY_UID = :surveyUid")
				.setParameter("surveyUid", surveyUid);

		return (List<Object>) query.list();
	}

	@SuppressWarnings("unchecked")
	private List<Object> GetAllRankingQuestionAnswers(String surveyUid, Set<String> rankingQuestionUids) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(
				"SELECT DISTINCT QUESTION_UID, VALUE FROM ANSWERS a INNER JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = a.AS_ID JOIN SURVEYS s ON  ans.SURVEY_ID =  s.SURVEY_ID WHERE s.ISDRAFT = FALSE AND s.SURVEY_UID = :surveyUid AND a.QUESTION_UID IN (" 
									+ StringUtils.collectionToCommaDelimitedString(rankingQuestionUids) + ")")
				.setParameter("surveyUid", surveyUid);

		return (List<Object>) query.list();
	}
	
	@Transactional(readOnly = true)
	public void checkAndRecreateMissingElements(Survey survey, ResultFilter filter) {
		List<Element> surveyelements = survey.getElementsRecursive(true);
		Map<String, Element> surveyelementsbyuid = new HashMap<>();
		Map<String, Element> missingelementuids = new HashMap<>();
		Map<String, File> missingfileuids = new HashMap<>();
		Set<String> rankingQuestionUids = new HashSet<>();
		for (Element element : surveyelements) {
			surveyelementsbyuid.put(element.getUniqueId(), element);
			if (element instanceof RankingQuestion) {
				rankingQuestionUids.add("'" + element.getUniqueId() + "'");
			}
		}
		
		// the reporting database is not used here fore performance reasons
		List<Object> res = GetAllQuestionsAndPossibleAnswers(survey.getUniqueId());

		for (Object o : res) {
			Object[] a = (Object[]) o;

			String questionUID = (String) a[0];
			String possibleAnswerUID = (String) a[1];

			if (!surveyelementsbyuid.containsKey(questionUID) && !missingelementuids.containsKey(questionUID)) {
				Element missingquestion = getNewestElementByUid(questionUID);

				if (missingquestion == null) {
					logger.info("unknown question found: " + questionUID);
				} else if (missingquestion instanceof Text) {
					// this comes from a matrix question, also load matrix
					Element parent = getParentForChildQuestion(missingquestion.getId());

					if (parent instanceof Matrix) {
						Matrix parentMatrix = (Matrix) parent;
						if (surveyelementsbyuid.containsKey(parentMatrix.getUniqueId())) {
							// the matrix still exists (only the matrix question was deleted)
							Matrix matrix = (Matrix) surveyelementsbyuid.get(parentMatrix.getUniqueId());
							matrix.getMissingQuestions().add(missingquestion);
							missingelementuids.put(missingquestion.getUniqueId(), missingquestion);
						} else if (!surveyelementsbyuid.containsKey(parentMatrix.getUniqueId())
								&& !missingelementuids.containsKey(parentMatrix.getUniqueId())) {
							survey.getMissingElements().add(parentMatrix);
							missingelementuids.put(parentMatrix.getUniqueId(), parentMatrix);
							if (filter != null) {
								if (!filter.getVisibleQuestions().contains(parentMatrix.getId().toString()))
									filter.getVisibleQuestions().add(parentMatrix.getId().toString());

								if (!filter.getExportedQuestions().contains(parentMatrix.getId().toString()))
									filter.getExportedQuestions().add(parentMatrix.getId().toString());
							}

							// check if matrix contains question
							if (!parentMatrix.containsChild(missingquestion.getId())) {
								parentMatrix.getMissingQuestions().add(missingquestion);
							}
						} else if (!surveyelementsbyuid.containsKey(parentMatrix.getUniqueId())
								&& missingelementuids.containsKey(parentMatrix.getUniqueId())
								&& !parentMatrix.containsChild(missingquestion.getId())) {
							// check if matrix contains question
							parentMatrix.getMissingQuestions().add(missingquestion);
						}
					} else if (parent instanceof RatingQuestion) {
						RatingQuestion parentRating = (RatingQuestion) parent;
						if (surveyelementsbyuid.containsKey(parentRating.getUniqueId())) {
							// the rating element still exists (only the rating question was deleted)
							RatingQuestion rating = (RatingQuestion) surveyelementsbyuid
									.get(parentRating.getUniqueId());
							rating.getMissingQuestions().add(missingquestion);
							missingelementuids.put(missingquestion.getUniqueId(), missingquestion);
						} else if (!surveyelementsbyuid.containsKey(parentRating.getUniqueId())
								&& !missingelementuids.containsKey(parentRating.getUniqueId())) {
							survey.getMissingElements().add(parentRating);
							missingelementuids.put(parentRating.getUniqueId(), parentRating);
							if (filter != null) {
								if (!filter.getVisibleQuestions().contains(parentRating.getId().toString()))
									filter.getVisibleQuestions().add(parentRating.getId().toString());

								if (!filter.getExportedQuestions().contains(parentRating.getId().toString()))
									filter.getExportedQuestions().add(parentRating.getId().toString());
							}

							// check if rating contains question
							if (!(parentRating).containsChild(missingquestion.getId())) {
								(parentRating).getMissingQuestions().add(missingquestion);
							}
						} else if (!surveyelementsbyuid.containsKey(parentRating.getUniqueId())
								&& missingelementuids.containsKey(parentRating.getUniqueId())) {
							// check if rating contains question
							if (!(parentRating).containsChild(missingquestion.getId())) {
								(parentRating).getMissingQuestions().add(missingquestion);
							}
						}
					}
				} else if (missingquestion instanceof ComplexTableItem) {
					Element parent = getParentForChildQuestion(missingquestion.getId());
					
					if (parent instanceof ComplexTable) {
						ComplexTable parentComplexTable = (ComplexTable) parent;
						if (surveyelementsbyuid.containsKey(parentComplexTable.getUniqueId())) {
							// the complextable element still exists (only the cell was deleted)
							ComplexTable table = (ComplexTable) surveyelementsbyuid
									.get(parentComplexTable.getUniqueId());
							table.getMissingChildElements().add((ComplexTableItem)missingquestion);
							missingelementuids.put(missingquestion.getUniqueId(), missingquestion);
						} else if (!surveyelementsbyuid.containsKey(parentComplexTable.getUniqueId())
								&& !missingelementuids.containsKey(parentComplexTable.getUniqueId())) {
							survey.getMissingElements().add(parentComplexTable);
							missingelementuids.put(parentComplexTable.getUniqueId(), parentComplexTable);
							if (filter != null) {
								if (!filter.getVisibleQuestions().contains(parentComplexTable.getId().toString()))
									filter.getVisibleQuestions().add(parentComplexTable.getId().toString());

								if (!filter.getExportedQuestions().contains(parentComplexTable.getId().toString()))
									filter.getExportedQuestions().add(parentComplexTable.getId().toString());
							}

							// check if rating contains question
							if (!(parentComplexTable).containsChild(missingquestion.getId())) {
								(parentComplexTable).getMissingChildElements().add((ComplexTableItem)missingquestion);
							}							
						}
					}
					
				} else {
					survey.getMissingElements().add(missingquestion);
					missingelementuids.put(questionUID, missingquestion);
					
					if (missingquestion instanceof RankingQuestion) {
						rankingQuestionUids.add("'" + missingquestion.getUniqueId() + "'");
					}
					
					if (filter != null) {
						if (!filter.getVisibleQuestions().contains(missingquestion.getId().toString()))
							filter.getVisibleQuestions().add(missingquestion.getId().toString());

						if (!filter.getExportedQuestions().contains(missingquestion.getId().toString()))
							filter.getExportedQuestions().add(missingquestion.getId().toString());
					}

					if (missingquestion instanceof Table) {
						for (Element child : ((Table) missingquestion).getChildElements()) {
							missingelementuids.put(child.getUniqueId(), child);
						}
					}
				}
			} else {
				// check if all table rows/cells are available
				Element question = surveyelementsbyuid.get(questionUID);
				if (question == null && missingelementuids.containsKey(questionUID))
					question = missingelementuids.get(questionUID);

				if (question instanceof MatrixOrTable) {
					Table table = (Table) question;
					if (possibleAnswerUID != null && possibleAnswerUID.contains("#")) {
						String matrixQuestionUID = possibleAnswerUID.substring(0, possibleAnswerUID.indexOf('#'));
						String answerUID = possibleAnswerUID.substring(possibleAnswerUID.indexOf('#') + 1);

						if (!surveyelementsbyuid.containsKey(matrixQuestionUID)
								&& !missingelementuids.containsKey(matrixQuestionUID)) {
							Element missingquestion = getNewestElementByUid(matrixQuestionUID);
							if (missingquestion != null) {
								table.getMissingQuestions().add(missingquestion);
								missingelementuids.put(missingquestion.getUniqueId(), missingquestion);
							} else {
								logger.info("unknown matrix question found: " + matrixQuestionUID);
							}
						}

						if (!surveyelementsbyuid.containsKey(answerUID) && !missingelementuids.containsKey(answerUID)) {
							Element missingquestion = getNewestElementByUid(answerUID);
							if (missingquestion != null) {
								table.getMissingAnswers().add(missingquestion);
								missingelementuids.put(missingquestion.getUniqueId(), missingquestion);
							} else {
								logger.info("unknown matrix answer found: " + answerUID);
							}
						}
					}
				}
			}

			if (possibleAnswerUID != null && possibleAnswerUID.length() > 0 && !possibleAnswerUID.contains("#")
					&& !surveyelementsbyuid.containsKey(possibleAnswerUID)
					&& !missingelementuids.containsKey(possibleAnswerUID)) {
				
				Element pa = getNewestElementByUid(possibleAnswerUID);

				if (pa != null) {
					survey.getMissingElements().add(pa);
					missingelementuids.put(possibleAnswerUID, pa);

					if (pa instanceof PossibleAnswer) {
						// add it to the parent
						if (surveyelementsbyuid.containsKey(questionUID)) {
							Element parent = surveyelementsbyuid.get(questionUID);
							if (parent instanceof ChoiceQuestion) {
								((ChoiceQuestion) parent).getMissingPossibleAnswers().add((PossibleAnswer) pa);
							}
						} else if (missingelementuids.containsKey(questionUID)) {
							Element parent = missingelementuids.get(questionUID);
							if (parent instanceof ChoiceQuestion) {
								((ChoiceQuestion) parent).getMissingPossibleAnswers().add((PossibleAnswer) pa);
							} else if (parent instanceof ComplexTableItem) {
								((ComplexTableItem) parent).getMissingPossibleAnswers().add((PossibleAnswer) pa);
							}
						}
					} else if (pa instanceof Text) {
						// this comes from a parent question, also load parent
						Element parent = getParentForChildQuestion(pa.getId());

						if (parent instanceof Matrix) {
							if (surveyelementsbyuid.containsKey(parent.getUniqueId())) {
								if (parent instanceof Matrix) {
									// the matrix still exists (only the matrix answer was deleted)
									Matrix matrix = (Matrix) surveyelementsbyuid.get(parent.getUniqueId());
									if (!matrix.containsChild(pa.getId())) {
										matrix.getMissingAnswers().add(pa);
									}
								}
							} else if (missingelementuids.containsKey(parent.getUniqueId())) {
								// the matrix still exists (only the matrix answer was deleted)
								Matrix matrix = (Matrix) missingelementuids.get(parent.getUniqueId());
								if (!matrix.containsChild(pa.getId())) {
									matrix.getMissingAnswers().add(pa);
								}
							}
						}
					}
				} else {
					//try gallery file
					Element parent = null;
					if (surveyelementsbyuid.containsKey(questionUID)) {
						parent = surveyelementsbyuid.get(questionUID);
					} else if (missingelementuids.containsKey(questionUID)) {
						parent = missingelementuids.get(questionUID);
					}
					if (parent != null && parent instanceof GalleryQuestion) {
						GalleryQuestion gallery = (GalleryQuestion) parent;
						try {							
							if (gallery.getFileByUid(possibleAnswerUID) == null && !missingfileuids.containsKey(possibleAnswerUID)) {
								File file = fileService.get(possibleAnswerUID);
								((GalleryQuestion) parent).getMissingFiles().add(file);
								missingfileuids.put(possibleAnswerUID, file);
							}							
						} catch (FileNotFoundException e) {
							// ignore;
						}						
					}
				}
			}
		}
		
		if (!rankingQuestionUids.isEmpty()) {
			List<Object> allRankingQuestionAnswers = GetAllRankingQuestionAnswers(survey.getUniqueId(), rankingQuestionUids);
			Set<String> distinctRankingItemUids = new HashSet<>();
			for (Object o : allRankingQuestionAnswers) {
				Object[] a = (Object[]) o;

				String questionUID = (String) a[0];
				String rankingItemOrder = (String) a[1];
				
				for (String uniqueId : rankingItemOrder.split(";")) {
					if (!distinctRankingItemUids.contains(uniqueId)) {
						distinctRankingItemUids.add(uniqueId);
						
						RankingQuestion question = null;
						if (surveyelementsbyuid.containsKey(questionUID))
						{
							question = (RankingQuestion) surveyelementsbyuid.get(questionUID);
						} else if (missingelementuids.containsKey(questionUID))
						{
							question = (RankingQuestion) missingelementuids.get(questionUID);
						} else {
							logger.error("no ranking question found: " + questionUID);
							continue;
						}
						
						if (!question.getChildElementsByUniqueId().containsKey(uniqueId))
						{
							Element missingRankingItem = getNewestElementByUid(uniqueId);
							if (missingRankingItem instanceof RankingItem) {
								question.getMissingElements().add((RankingItem) missingRankingItem);
								missingelementuids.put(missingRankingItem.getUniqueId(), missingRankingItem);
							} else {
								logger.info("unknown ranking item found: " + questionUID);
							}
						}						 
					}				
				}
			}
		}

		survey.setMissingElementsChecked(true);
	}

	@Transactional(readOnly = false)
	public void markAsArchived(String uid) {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		Query<Survey> query = session.createQuery("UPDATE Survey s SET s.archived = 1 WHERE s.uniqueId = :uid").setParameter("uid",
				uid);
		query.executeUpdate();
	}

	@Transactional(readOnly = false)
	public void unmarkAsArchived(String uid) {
		Session session = sessionFactory.getCurrentSession();
		Query<Survey> query = session.createQuery("UPDATE Survey s SET s.archived = 0, isDeleted = 0 WHERE s.uniqueId = :uid")
				.setParameter("uid", uid);
		query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public List<Integer> getAllSurveyVersions(String shortname, String uid) {
		Session session = sessionFactory.getCurrentSession();
		Query query;

		if (shortname != null && shortname.length() > 0 && uid != null && uid.length() > 0) {
			query = session.createSQLQuery(
					"SELECT SURVEY_ID FROM SURVEYS WHERE SURVEYNAME LIKE :shortname AND SURVEY_UID LIKE :uid");
			query.setParameter(Constants.SHORTNAME, "%" + shortname + "%");
			query.setParameter("uid", "%" + uid + "%");
		} else if (shortname != null && shortname.length() > 0) {
			query = session.createSQLQuery("SELECT SURVEY_ID FROM SURVEYS WHERE SURVEYNAME LIKE :shortname");
			query.setParameter(Constants.SHORTNAME, "%" + shortname + "%");
		} else {
			query = session.createSQLQuery("SELECT SURVEY_ID FROM SURVEYS WHERE SURVEY_UID LIKE :uid");
			query.setParameter("uid", "%" + uid + "%");
		}

		@SuppressWarnings("rawtypes")
		List res = query.list();

		List<Integer> result = new ArrayList<>();

		for (Object o : res) {
			result.add(ConversionTools.getValue(o));
		}

		return result;
	}

	@Transactional
	public boolean changeOwner(String surveyuid, String owner, int currentuserid) throws MessageException {
		int ownerid = 0;
		User newowner = null;

		try {
			ownerid = Integer.parseInt(owner);
			newowner = administrationService.getUser(ownerid);
		} catch (NumberFormatException e) {
			newowner = administrationService.getUserForLogin(owner, true);
		}

		if (newowner != null) {
			Survey survey = surveyService.getSurveyByUniqueIdToWrite(surveyuid);
			Integer oldownerid = survey.getOwner().getId();

			if (!Objects.equals(survey.getOwner().getId(), newowner.getId())) {
				survey.setOwner(newowner);
				List<Integer> allsurveyids = surveyService.getAllPublishedSurveyVersions(survey.getId());

				for (int id : allsurveyids) {
					Survey published = surveyService.getSurvey(id);
					published.setOwner(newowner);
				}

				activityService.log(ActivityRegistry.ID_OWNER, oldownerid.toString(), newowner.getId().toString(), currentuserid, surveyuid);

				return true;
			}

		}

		return false;
	}

	@Transactional(readOnly = true)
	public List<Integer> getAllSurveyVersions(int surveyId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(
				"SELECT s1.SURVEY_ID FROM SURVEYS s1 JOIN SURVEYS s2 ON s1.SURVEY_UID = s2.SURVEY_UID AND s1.ISDRAFT = s2.ISDRAFT WHERE s2.SURVEY_ID = :id")
				.setParameter("id", surveyId);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		List<Integer> result = new ArrayList<>();

		for (Object o : res) {
			result.add(ConversionTools.getValue(o));
		}

		return result;
	}

	@Transactional(readOnly = true)
	public List<Integer> getAllPublishedSurveyVersions(int surveyId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(
				"SELECT s1.SURVEY_ID FROM SURVEYS s1 JOIN SURVEYS s2 ON s1.SURVEY_UID = s2.SURVEY_UID WHERE s2.SURVEY_ID = :id AND s1.ISDRAFT=0 ORDER BY s1.SURVEY_ID ASC")
				.setParameter("id", surveyId);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		List<Integer> result = new ArrayList<>();

		for (Object o : res) {
			result.add(ConversionTools.getValue(o));
		}

		return result;
	}

	@Transactional(readOnly = true)
	public List<Integer> getAllPublishedSurveyVersions(String surveyUid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createSQLQuery(
						"SELECT SURVEY_ID FROM SURVEYS WHERE SURVEY_UID = :uid AND ISDRAFT=0 ORDER BY SURVEY_ID ASC")
				.setParameter("uid", surveyUid);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		List<Integer> result = new ArrayList<>();

		for (Object o : res) {
			result.add(ConversionTools.getValue(o));
		}

		return result;
	}

	@Transactional(readOnly = true)
	public Survey getSurveyInOriginalLanguage(Integer id, String shortname, String uid) {
		String code = getLanguageCode(shortname, uid);
		return getSurvey(id, code);
	}

	@Transactional(readOnly = true)
	public String getLanguageCode(String shortname, String uid) {
		Session session = sessionFactory.getCurrentSession();
		Query<String> query = session
				.createQuery("SELECT s.language.code FROM Survey s WHERE s.uniqueId = :uid AND s.isDraft = true", String.class)
				.setParameter("uid", uid);
		String result = query.uniqueResult();

		if (result == null) {
			query = session
					.createQuery("SELECT s.language.code FROM Survey s WHERE s.shortname = :name AND s.isDraft = true", String.class)
					.setParameter("name", shortname);
			result = query.uniqueResult();
		}

		return result;
	}

	@Transactional(readOnly = true)
	public boolean doesLanguageDiffer(String uid){
		Session session = sessionFactory.getCurrentSession();
		Query<Integer> query = session.createQuery("SELECT s.language.id FROM Survey s WHERE s.uniqueId = :uid AND s.isDraft = true ORDER BY s.id DESC", Integer.class).setParameter("uid", uid);
		Query<Integer> query2 = session.createQuery("SELECT s.language.id FROM Survey s WHERE s.uniqueId = :uid AND s.isDraft = false ORDER BY s.id DESC", Integer.class).setParameter("uid", uid);
		List<Integer> list = query.list();
		List<Integer> list2 = query2.list();

		if (list.isEmpty() || list2.isEmpty())
			return false;

		return !list.get(0).equals(list2.get(0));
	}

	@Transactional
	public int repairXHTML(Integer id) {
		// first the (draft) survey itself
		Session session = sessionFactory.getCurrentSession();
		Survey draft = getSurvey(id);
		session.setReadOnly(draft, false);
		for (Element e : draft.getElementsRecursive(true)) {
			session.setReadOnly(e, false);
		}

		DocumentBuilder builder = XHTMLValidator.getBuilder();

		int repairedLabels = repairXHTML(draft, builder);

		// then the translations of the draft
		List<Translations> alltranslations = translationService.getTranslationsForSurvey(id, false);
		for (Translations translations : alltranslations) {
			repairedLabels += repairXHTML(translations, builder);
		}

		// then the published survey (all versions)
		Survey published = getSurveyByUniqueId(draft.getUniqueId(), false, false);

		if (published != null) {
			List<Integer> allSurveys = getAllSurveyVersions(published.getId());
			for (Integer pubid : allSurveys) {
				Survey pub = getSurvey(pubid);
				session.setReadOnly(pub, false);
				for (Element e : pub.getElementsRecursive(true)) {
					session.setReadOnly(e, false);
				}
				repairedLabels += repairXHTML(pub, builder);

				// then the translations of the draft
				alltranslations = translationService.getTranslationsForSurvey(pubid, false);
				for (Translations translations : alltranslations) {
					repairedLabels += repairXHTML(translations, builder);
				}
			}
		}

		return repairedLabels;
	}

	public int repairXHTML(Survey survey, DocumentBuilder builder) {
		int repairedLabels = 0;

		if (!XHTMLValidator.validate(survey.getTitle(), servletContext, builder)) {
			survey.setTitle(HtmlUtils.htmlEscape(survey.getTitle()));
			repairedLabels++;
		}

		Map<String, String> keysToReplace = new HashMap<>();
		for (String label : survey.getBackgroundDocuments().keySet()) {
			if (!XHTMLValidator.validate(label, servletContext, builder)) {
				keysToReplace.put(label, HtmlUtils.htmlEscape(label));
			}
		}
		for (Entry<String, String> entry : keysToReplace.entrySet()) {
			String url = survey.getBackgroundDocuments().get(entry.getKey());
			survey.getBackgroundDocuments().remove(entry.getKey());
			survey.getBackgroundDocuments().put(entry.getValue(), url);
		}

		keysToReplace = new HashMap<>();
		for (String label : survey.getUsefulLinks().keySet()) {
			if (!XHTMLValidator.validate(label, servletContext, builder)) {
				keysToReplace.put(label, HtmlUtils.htmlEscape(label));
			}
		}
		for (Entry<String, String> entry : keysToReplace.entrySet()) {
			String url = survey.getUsefulLinks().get(entry.getKey());
			survey.getUsefulLinks().remove(entry.getKey());
			survey.getUsefulLinks().put(entry.getValue(), url);
		}

		for (Element element : survey.getElementsRecursive(true)) {

			if (!XHTMLValidator.validate(element.getTitle(), servletContext, builder)) {
				element.setTitle(HtmlUtils.htmlEscape(element.getTitle()));
				repairedLabels++;
			}

			if (element instanceof Question) {
				Question q = (Question) element;
				if (!XHTMLValidator.validate(q.getHelp(), servletContext, builder)) {
					q.setHelp(HtmlUtils.htmlEscape(q.getHelp()));
					repairedLabels++;
				}
			}
		}

		return repairedLabels;
	}

	public int repairXHTML(Translations translations, DocumentBuilder builder) {
		int repairedLabels = 0;

		for (Translation translation : translations.getTranslations()) {
			if (!XHTMLValidator.validate(translation.getLabel(), servletContext, builder)) {
				translation.setLabel(HtmlUtils.htmlEscape(translation.getLabel()));
				repairedLabels++;
			}
		}

		return repairedLabels;
	}

	@Transactional
	public List<Survey> getPublicSurveysForValidation(String filteralias, String filterowner,
			String filterrequestdatefrom, String filterrequestdateto) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "FROM Survey s WHERE s.isDraft = true AND s.listForm = true AND (s.listFormValidated = false OR s.listFormValidated = null)";

		if (filteralias != null && filteralias.length() > 0) {
			sql += " AND s.shortname like :alias";
		}

		if (filterowner != null && filterowner.length() > 0) {
			sql += " AND s.owner.login like :owner";
		}

		if (filterrequestdatefrom != null && filterrequestdatefrom.length() > 0) {
			sql += " AND s.publicationRequestedDate >= :datefrom";
		}

		if (filterrequestdateto != null && filterrequestdateto.length() > 0) {
			sql += " AND s.publicationRequestedDate <= :dateto";
		}

		Query<Survey> query = session.createQuery(sql, Survey.class);

		if (filteralias != null && filteralias.length() > 0) {
			query.setParameter("alias", "%" + filteralias + "%");
		}

		if (filterowner != null && filterowner.length() > 0) {
			query.setParameter("owner", "%" + filterowner + "%");
		}

		DateTimeFormatter f = DateTimeFormat.forPattern("dd/MM/yyyy");

		if (filterrequestdatefrom != null && filterrequestdatefrom.length() > 0) {
			query.setParameter("datefrom", DateTime.parse(filterrequestdatefrom, f).toDate());
		}

		if (filterrequestdateto != null && filterrequestdateto.length() > 0) {
			query.setParameter("dateto", Tools.getFollowingDay(DateTime.parse(filterrequestdateto, f).toDate()));
		}


		List<Survey> surveys = query.setReadOnly(true).list();
		return surveys;
	}

	public void sendListFormMail(Survey survey) throws Exception {
		String body = "A request for publishing the survey '" + survey.getShortname()
				+ "' in the list of all public surveys has been made.<br /><br />"
				+ "To see all requests, please follow this link:<br /><a href=\"" + host
				+ "administration/publicsurveys\">" + host + "administration/publicsurveys</a><br /><br />"
				+ "Your EUSurvey Team";

		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]", host);

		mailService.SendHtmlMail(publicsurveynotification, sender, sender,
				"New publication request for the EUSurvey Homepage", text, null);
	}

	public void sendOPCApplyChangesMail(Survey survey, int userId) throws Exception {
		if (survey.getIsOPC() && opcnotify != null && opcnotify.length() > 0) {
			User user = userId > 0 ? administrationService.getUser(userId) : null;

			String body = "The survey <a href=\"" + host + survey.getShortname() + "/management/overview\">"
					+ survey.getShortname() + "</a> has been changed by user "
					+ (user != null ? user.getName() : "SYSTEM") + " at " + new Date() + ".";
			InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
			String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]", host);

			String[] users = opcnotify.split(";");
			for (String opcuser : users) {
				if (opcuser.length() > 0) {
					mailService.SendHtmlMail(opcuser, sender, sender,
							"The online version of a published 'BRP Public Consultation' has been changed", text, null);
				}
			}
		}
	}

	@Transactional(readOnly = true)
	public Survey getSurveyForUploadedFile(int fileid) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT s.SURVEY_UID FROM SURVEYS s JOIN ANSWERS_SET ans ON ans.SURVEY_ID = s.SURVEY_ID JOIN ANSWERS a on a.AS_ID = ans.ANSWER_SET_ID JOIN ANSWERS_FILES af ON af.ANSWERS_ANSWER_ID = a.ANSWER_ID WHERE af.files_FILE_ID = :fileid";
		NativeQuery query = session.createSQLQuery(sql);
		query.setParameter("fileid", fileid);
		String result = (String) query.uniqueResult();

		if (result == null)
			return null;

		return getSurvey(result, true, false, false, false, null, true, false);
	}

	@Transactional(readOnly = true)
	public Survey getSurveyForQuestion(String uid) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT DISTINCT s.SURVEY_UID FROM SURVEYS s JOIN SURVEYS_ELEMENTS se ON se.SURVEYS_SURVEY_ID = s.SURVEY_ID JOIN ELEMENTS e ON e.ID = se.elements_ID WHERE e.ELEM_UID = :uid";
		NativeQuery query = session.createSQLQuery(sql);
		query.setParameter("uid", uid);
		String result = (String) query.uniqueResult();

		if (result == null)
			return null;

		return getSurvey(result, true, false, false, false, null, true, false);
	}

	@Transactional(readOnly = true)
	public String[] getSurveyForFile(File file, String contextpath, String surveyuid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT s.uniqueId, s.shortname FROM Survey s WHERE s.logo.id = :id");
		query.setParameter("id", file.getId());

		@SuppressWarnings("rawtypes")
		List data = query.setMaxResults(1).list();
		if (!data.isEmpty()) {
			Object[] values = (Object[]) data.get(0);
			String[] result = new String[3];
			result[0] = values[0].toString();
			result[1] = values[1].toString();
			result[2] = "logo";
			return result;
		}

		NativeQuery nativeQuery = session.createSQLQuery(
				"SELECT e.type, s.SURVEY_UID, s.SURVEYNAME FROM ELEMENTS e JOIN SURVEYS_ELEMENTS se ON se.elements_ID = e.ID JOIN SURVEYS s ON s.SURVEY_ID = se.SURVEYS_SURVEY_ID JOIN ELEMENTS_FILES ef ON ef.ELEMENTS_ID = e.ID WHERE ef.files_FILE_ID = :id");
		nativeQuery.setParameter("id", file.getId());

		data = nativeQuery.setMaxResults(1).list();
		if (!data.isEmpty()) {
			Object[] values = (Object[]) data.get(0);
			String[] result = new String[3];
			result[0] = values[1].toString();
			result[1] = values[2].toString();
			result[2] = values[0].toString().toLowerCase();

			if (result[2].equalsIgnoreCase("galleryquestion")) {
				result[2] = "image";
			}

			return result;
		}

		if (surveyuid == null) {
			nativeQuery = session.createSQLQuery(
					"SELECT e.type, s.SURVEY_UID, s.SURVEYNAME FROM ELEMENTS e JOIN SURVEYS_ELEMENTS se ON se.elements_ID = e.ID JOIN SURVEYS s ON s.SURVEY_ID = se.SURVEYS_SURVEY_ID WHERE URL = :url");
			nativeQuery.setParameter("url", contextpath + "/files/" + file.getUid());
			data = nativeQuery.setMaxResults(1).list();
			if (!data.isEmpty()) {
				Object[] values = (Object[]) data.get(0);
				if (values[0].toString().equalsIgnoreCase("IMAGE")) {
					String[] result = new String[3];
					result[0] = values[1].toString();
					result[1] = values[2].toString();
					result[2] = "image";
					return result;
				}
			}

			nativeQuery = session.createSQLQuery(
					"SELECT e.type, s.SURVEY_UID, s.SURVEYNAME FROM ELEMENTS e JOIN SURVEYS_ELEMENTS se ON se.elements_ID = e.ID JOIN SURVEYS s ON s.SURVEY_ID = se.SURVEYS_SURVEY_ID WHERE URL LIKE :url");
			nativeQuery.setParameter("url", "%/" + file.getUid());
			data = nativeQuery.setMaxResults(1).list();
			if (!data.isEmpty()) {
				Object[] values = (Object[]) data.get(0);
				if (values[0].toString().equalsIgnoreCase("IMAGE"))
				{
					String[] result = new String[3];
					result[0] = values[1].toString();
					result[1] = values[2].toString();
					result[2] = "image";
					return result;
				}
			}
		} else {
			nativeQuery = session.createSQLQuery(
					"SELECT e.type, s.SURVEY_UID, s.SURVEYNAME FROM ELEMENTS e JOIN SURVEYS_ELEMENTS se ON se.elements_ID = e.ID JOIN SURVEYS s ON s.SURVEY_ID = se.SURVEYS_SURVEY_ID WHERE s.SURVEY_UID = :uid AND URL = :url");
			nativeQuery.setParameter("url", contextpath + "/files/" + surveyuid + Constants.PATH_DELIMITER + file.getUid());
			nativeQuery.setParameter("uid", surveyuid);
			data = nativeQuery.setMaxResults(1).list();
			if (!data.isEmpty()) {
				Object[] values = (Object[]) data.get(0);
				if (values[0].toString().equalsIgnoreCase("IMAGE"))
				{
					String[] result = new String[3];
					result[0] = values[1].toString();
					result[1] = values[2].toString();
					result[2] = "image";
					return result;
				}
			}
		}

		nativeQuery = session.createSQLQuery(
				"SELECT s.SURVEY_UID, s.SURVEYNAME FROM SURVEYS s JOIN Survey_backgroundDocuments sb ON sb.Survey_SURVEY_ID = s.SURVEY_ID WHERE sb.BACKGROUNDDOCUMENTS LIKE :url");
		nativeQuery.setParameter("url", "%" + file.getUid());
		data = nativeQuery.setMaxResults(1).list();
		if (!data.isEmpty()) {
			Object[] values = (Object[]) data.get(0);
			String[] result = new String[3];
			result[0] = values[0].toString();
			result[1] = values[1].toString();
			result[2] = "background document";
			return result;
		}

		return null;
	}

	public java.io.File exportSurvey(String shortname, SurveyService surveyService, boolean answers) {
		return SurveyExportHelper.exportSurvey(shortname, surveyService, answers, translationService, answerService,
				fileDir, sessionService, fileService, sessionFactory.getCurrentSession(), host, answerExplanationService, selfassessmentService);
	}

	@Transactional(readOnly = true)
	public List<Element> getElements(String[] ids) {
		Session session = sessionFactory.getCurrentSession();

		List<Element> result = new ArrayList<>();
		for (String id : ids) {
			Element e = session.get(Element.class, Integer.parseInt(id));
			result.add(e);
		}
		return result;
	}

	public Map<String, String> copyFiles(Survey survey, Map<String, String> convertedUIDs, boolean allowMissingFiles,
			Map<String, String> missingFiles, String originalSurveyUID) throws IOException {

		Map<String, String> result = new HashMap<>();

		if (survey.getLogo() != null) {
			String olduid = survey.getLogo().getUid();
			String newuid = UUID.randomUUID().toString();
			if (convertedUIDs.containsKey(olduid)) {
				newuid = convertedUIDs.get(olduid);
				survey.getLogo().setUid(newuid);
				result.put(olduid, newuid);
			} else {
				try {
					java.io.File source = fileService.getSurveyFile(originalSurveyUID, olduid);
					if (!source.exists()) {
						source = new java.io.File(fileDir + olduid);
						if (source.exists()) {
							fileService.logOldFileSystemUse(fileDir + olduid);
						}
					}

					FileUtils.copyFile(source, fileService.getSurveyFile(survey.getUniqueId(), newuid));
					survey.getLogo().setUid(newuid);
					result.put(olduid, newuid);
				} catch (IOException ex) {
					if (allowMissingFiles) {
						missingFiles.put(survey.getLogo().getName(), "logo");
						survey.setLogo(null);
					} else {
						throw ex;
					}
				}
			}
		}

		if (survey.getBackgroundDocuments() != null) {
			Set<String> labels = new HashSet<>(survey.getBackgroundDocuments().keySet());
			for (String label : labels) {
				String url = survey.getBackgroundDocuments().get(label);
				String olduid = getFileUIDFromUrl(url);
				String newuid = UUID.randomUUID().toString();
				if (convertedUIDs.containsKey(olduid)) {
					newuid = convertedUIDs.get(olduid);
					survey.getBackgroundDocuments().put(label,
							servletContext.getContextPath() + "/files/" + survey.getUniqueId() + Constants.PATH_DELIMITER + newuid);
					result.put(olduid, newuid);
				} else {
					try {
						java.io.File source = fileService.getSurveyFile(originalSurveyUID, olduid);
						if (!source.exists()) {
							source = new java.io.File(fileDir + olduid);
							if (source.exists()) {
								fileService.logOldFileSystemUse(fileDir + olduid);
							}
						}
						FileUtils.copyFile(source, fileService.getSurveyFile(survey.getUniqueId(), newuid));
						File f = new File();
						f.setName(label);
						f.setUid(newuid);
						fileService.add(f);
						survey.getBackgroundDocuments().put(label,
								servletContext.getContextPath() + "/files/" + survey.getUniqueId() + Constants.PATH_DELIMITER + newuid);
						result.put(olduid, newuid);
					} catch (IOException ex) {
						if (allowMissingFiles) {
							survey.getBackgroundDocuments().remove(label);
							missingFiles.put(label, "background document");
						} else {
							throw ex;
						}
					}
				}

			}
		}

		for (Element element : survey.getElements()) {
			if (element instanceof Download) {
				Download download = (Download) element;

				List<File> files = new ArrayList<>(download.getFiles());

				for (File f : files) {
					String olduid = f.getUid();
					String newuid = UUID.randomUUID().toString();
					if (convertedUIDs.containsKey(olduid)) {
						newuid = convertedUIDs.get(olduid);
						f.setUid(newuid);
						result.put(olduid, newuid);
					} else {
						try {
							java.io.File source = fileService.getSurveyFile(originalSurveyUID, olduid);
							if (!source.exists()) {
								source = new java.io.File(fileDir + olduid);
								if (source.exists()) {
									fileService.logOldFileSystemUse(fileDir + olduid);
								}
							}
							FileUtils.copyFile(source, fileService.getSurveyFile(survey.getUniqueId(), newuid));
							f.setUid(newuid);
							result.put(olduid, newuid);
						} catch (IOException ex) {
							if (allowMissingFiles) {
								download.getFiles().remove(f);
								missingFiles.put(f.getName(), "download");
							} else {
								throw ex;
							}
						}
					}

				}
			} else if (element instanceof Confirmation) {
				Confirmation confirmation = (Confirmation) element;

				List<File> files = new ArrayList<>(confirmation.getFiles());

				for (File f : files) {
					String olduid = f.getUid();
					String newuid = UUID.randomUUID().toString();
					if (convertedUIDs.containsKey(olduid)) {
						newuid = convertedUIDs.get(olduid);
						f.setUid(newuid);
						result.put(olduid, newuid);
					} else {
						try {
							java.io.File source = fileService.getSurveyFile(originalSurveyUID, olduid);
							if (!source.exists()) {
								source = new java.io.File(fileDir + olduid);
								if (source.exists()) {
									fileService.logOldFileSystemUse(fileDir + olduid);
								}
							}
							FileUtils.copyFile(source, fileService.getSurveyFile(survey.getUniqueId(), newuid));
							f.setUid(newuid);
							result.put(olduid, newuid);
						} catch (IOException ex) {
							if (allowMissingFiles) {
								confirmation.getFiles().remove(f);
								missingFiles.put(f.getName(), "confirmation");
							} else {
								throw ex;
							}
						}
					}

				}
			} else if (element instanceof Image) {
				Image image = (Image) element;

				if (image.getUrl() != null && !image.getUrl().contains("photo_scenery.png")) {
					String fileUID = getFileUIDFromUrl(image.getUrl());

					if (fileUID.length() > 0) {
						File f = fileService.get(fileUID);
						String newuid = UUID.randomUUID().toString();
						if (convertedUIDs.containsKey(fileUID)) {
							newuid = convertedUIDs.get(fileUID);
							image.setUrl(
									servletContext.getContextPath() + "/files/" + survey.getUniqueId() + Constants.PATH_DELIMITER + newuid);
							result.put(fileUID, newuid);
						} else {
							try {
								java.io.File source = fileService.getSurveyFile(originalSurveyUID, fileUID);
								if (!source.exists()) {
									source = new java.io.File(fileDir + fileUID);
									if (source.exists()) {
										fileService.logOldFileSystemUse(fileDir + fileUID);
									}
								}
								FileUtils.copyFile(source, fileService.getSurveyFile(survey.getUniqueId(), newuid));
								File f2 = f.copy(fileDir);
								f2.setUid(newuid);
								fileService.add(f2);
								image.setUrl(servletContext.getContextPath() + "/files/" + survey.getUniqueId() + Constants.PATH_DELIMITER
										+ newuid);
								result.put(fileUID, newuid);
							} catch (IOException ex) {
								if (allowMissingFiles) {
									image.setUrl(null);
									missingFiles.put(f.getName(), "image");
								} else {
									throw ex;
								}
							}
						}

					}
				}
			} else if (element instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) element;

				List<File> files = new ArrayList<>(gallery.getFiles());

				for (File f : files) {
					String olduid = f.getUid();
					String newuid = UUID.randomUUID().toString();
					if (convertedUIDs.containsKey(olduid)) {
						newuid = convertedUIDs.get(olduid);
						f.setUid(newuid);
						result.put(olduid, newuid);
					} else {
						try {
							java.io.File source = fileService.getSurveyFile(originalSurveyUID, olduid);
							if (!source.exists()) {
								source = new java.io.File(fileDir + olduid);
								if (source.exists()) {
									fileService.logOldFileSystemUse(fileDir + olduid);
								}
							}
							FileUtils.copyFile(source, fileService.getSurveyFile(survey.getUniqueId(), newuid));
							f.setUid(newuid);
							result.put(olduid, newuid);
						} catch (IOException ex) {
							if (allowMissingFiles) {
								gallery.getFiles().remove(f);
								missingFiles.put(f.getName(), "image");
							} else {
								throw ex;
							}
						}
					}

				}
			}
		}

		return result;
	}

	@Transactional
	public void delete(Survey survey) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(survey);
	}

	@Transactional
	public void markDeleted(int surveyid, Integer userid, String shortname, String uniqueId, boolean published) {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		Query<Survey> query = session
				.createQuery("UPDATE Survey s SET s.isDeleted = true, s.deleted = NOW() WHERE s.uniqueId = :uniqueId");
		query.setParameter("uniqueId", uniqueId);
		query.executeUpdate();

		activityService.log(ActivityRegistry.ID_SURVEY_DELETED, shortname, null, userid, uniqueId);

		if (uniqueId != null) {
			int answers = answerService.getNumberOfAnswerSetsPublished(shortname, uniqueId);
			String s = shortname + " (" + (published ? "published" : "draft") + ") " + answers;
			activityService.log(1001, s, null, userid, null);
		}
	}

	@Transactional
	public void unmarkDeleted(int id, String alias) {
		Session session = sessionFactory.getCurrentSession();
		Survey survey = session.get(Survey.class, id);

		survey.setIsDeleted(false);

		if (alias != null && alias.length() > 0) {
			survey.setShortname(alias);			
		}
		
		for (int pid : surveyService.getAllPublishedSurveyVersions(survey.getId())) {
			Survey s = session.get(Survey.class, pid);
			s.setIsDeleted(false);
			if (alias != null && alias.length() > 0) {
				s.setShortname(alias);
			}
			session.saveOrUpdate(s);
		}

		session.saveOrUpdate(survey);
	}

	@Transactional
	public List<Integer> getSurveysMarkedDeleted() {
		Session session = sessionFactory.getCurrentSession();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);

		Date threemonthsago = cal.getTime();
		Query<Integer> query = session.createQuery(
				"SELECT s.id FROM Survey s WHERE s.isDraft = true AND s.isDeleted = true AND s.deleted < :threemonthsago", Integer.class);
		query.setParameter("threemonthsago", threemonthsago);

		return query.list();
	}

	@Transactional
	public void removeFromSessionCache(Survey survey) {
		Session session = sessionFactory.getCurrentSession();
		session.evict(survey);
	}

	@Transactional
	public void propagateNewShortname(Survey draft) {
		List<Integer> surveys = getAllPublishedSurveyVersions(draft.getId());
		Session session = sessionFactory.getCurrentSession();
		for (Integer id : surveys) {
			Survey published = session.get(Survey.class, id);
			published.setShortname(draft.getShortname());
			session.save(published);
		}
	}
	
	@Transactional(readOnly = true)
	public List<Survey> getAllECFSurveys() {
		Session session = sessionFactory.getCurrentSession();
		String sql = "FROM Survey s WHERE s.isECF = true";
		Query<Survey> query = session.createQuery(sql, Survey.class);

		return query.list();
	}
	
	public List<Survey> getAllSurveysForUser(User user) {
		Session session = sessionFactory.getCurrentSession();

		String hql = "FROM Survey s WHERE s.isDraft = true AND s.owner.id = :userid";
		Query<Survey> query = session.createQuery(hql, Survey.class);
		query.setParameter("userid", user.getId());

		return query.list();
	}

	private String getOwnerWhere(String type) {
		String ownerwhere;

		if (type != null && type.equalsIgnoreCase("my")) {
			ownerwhere = "s.OWNER = :userid";
		} else if (type != null && type.equalsIgnoreCase("shared")) {
			ownerwhere = "s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE (a.ACCESS_USER = :userid OR a.ACCESS_DEPARTMENT IN (SELECT GRPS FROM ECASGROUPS WHERE eg_ID = (SELECT USER_ID FROM ECASUSERS WHERE USER_LOGIN = :login))) AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%')) OR s.SURVEY_UID in (SELECT r.SURVEY FROM RESULTACCESS r WHERE r.RESACC_USER = :userid)";
		} else {
			ownerwhere = "s.OWNER = :userid OR s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE (a.ACCESS_USER = :userid OR a.ACCESS_DEPARTMENT IN (SELECT GRPS FROM ECASGROUPS WHERE eg_ID = (SELECT USER_ID FROM ECASUSERS WHERE USER_LOGIN = :login))) AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%')) OR s.SURVEY_UID in (SELECT r.SURVEY FROM RESULTACCESS r WHERE r.RESACC_USER = :userid)";
		}

		return ownerwhere;
	}

	public Map<Integer, String> getAllPublishedSurveysForUser(User user, String sort, String type) {
		Session session = sessionFactory.getCurrentSession();

		String ownerwhere = getOwnerWhere(type);
		String sql = "SELECT s.SURVEY_ID, s.SURVEYNAME from SURVEYS s where s.ISDRAFT = 1 and s.ACTIVE = 1 and ("
				+ ownerwhere
				+ ") and (s.ARCHIVED = 0 or s.ARCHIVED is null) and (s.DELETED = 0 or s.DELETED is null) ORDER BY ";

		if (sort.equalsIgnoreCase("created")) {
			sql += "s.SURVEY_CREATED DESC";
		} else if (sort.equalsIgnoreCase(Constants.EDITED)) {
			sql += "s.SURVEY_UPDATED DESC";
		} else {
			sql += "s.SURVEYNAME ASC";
		}

		Query query = session.createSQLQuery(sql);

		query.setParameter("userid", user.getId());

		if (type == null || !type.equalsIgnoreCase("my")) {
			query.setParameter("login", user.getLogin());
		}

		LinkedHashMap<Integer, String> result = new LinkedHashMap<>();

		@SuppressWarnings("rawtypes")
		List res = query.list();

		for (Object o : res) {
			Object[] a = (Object[]) o;
			result.put(ConversionTools.getValue(a[0]), (String) a[1]);
		}

		return result;
	}

	public List<String> getAllPublishedSurveysUIDsForUser(User user, boolean escapeforsql, boolean includearchived,
			String type) {
		Session session = sessionFactory.getCurrentSession();

		String ownerwhere = getOwnerWhere(type);

		String sql = "SELECT DISTINCT s.SURVEY_UID from SURVEYS s where s.ISDRAFT = 1 and (" + ownerwhere
				+ ") and (s.DELETED = 0 or s.DELETED is null)";

		if (!includearchived) {
			sql += " and (s.ARCHIVED = 0 or s.ARCHIVED is null)";
		}

		Query query = session.createSQLQuery(sql);
		query.setParameter("userid", user.getId());

		if (type == null || !type.equalsIgnoreCase("my")) {
			query.setParameter("login", user.getLogin());
		}

		List<String> result = new ArrayList<>();

		@SuppressWarnings("rawtypes")
		List res = query.list();

		for (Object o : res) {
			if (escapeforsql) {
				result.add("'" + (String) o + "'");
			} else {
				result.add((String) o);
			}
		}

		return result;
	}

	public String[] getMetaDataForUser(User user, String type) throws Exception {
		String[] result = new String[6];

		List<String> surveyUIDs = getAllPublishedSurveysUIDsForUser(user, true, false, type);

		if (!surveyUIDs.isEmpty()) {
			Session session = sessionFactory.getCurrentSession();
			String sql = "SELECT s.SURVEYNAME from SURVEYS s where s.ISDRAFT = 1 and SURVEY_UID IN ("
					+ StringUtils.collectionToCommaDelimitedString(surveyUIDs)
					+ ") ORDER BY s.SURVEY_UPDATED DESC LIMIT 1";

			Query query = session.createSQLQuery(sql);

			String shortname = (String) query.uniqueResult();
			result[0] = shortname != null ? shortname : "";

			sql = "SELECT s.SURVEYNAME FROM ANSWERS_SET ans JOIN SURVEYS s ON ans.SURVEY_ID = s.SURVEY_ID WHERE s.SURVEY_UID IN ("
					+ StringUtils.collectionToCommaDelimitedString(surveyUIDs)
					+ ") ORDER BY ans.ANSWER_SET_DATE DESC LIMIT 1";

			query = session.createSQLQuery(sql);

			String d = (String) query.uniqueResult();
			result[1] = d != null ? d : "";

			sql = "SELECT s.SURVEYNAME, s.DELETED, s.ARCHIVED from SURVEYS s JOIN USERS u ON u.USER_LAST_SURVEY = s.SURVEY_ID where u.USER_ID = :userid";

			query = session.createSQLQuery(sql);
			query.setParameter("userid", user.getId());

			@SuppressWarnings("rawtypes")
			List res = query.list();

			boolean deleted = false;
			boolean archived = false;
			if (res != null && !res.isEmpty()) {
				Object[] a = (Object[]) res.get(0);
				shortname = (String) a[0];
				deleted = (boolean) a[1];
				archived = (boolean) a[2];
			} else {
				shortname = null;
				deleted = true;
			}

			result[2] = shortname != null ? shortname : "";
			result[3] = deleted ? "true" : "false";
			result[4] = archived ? "true" : "false";

		} else {
			// this means that the user has no "existing" survey. Check if there is an
			// archived one

			ArchiveFilter filter = new ArchiveFilter();
			filter.setUserId(user.getId());
			List<Archive> archives = archiveService.getAllArchives(filter, 1, 1, true);

			if (!archives.isEmpty()) {
				result[5] = "true";
			}
		}

		return result;
	}

	public int[] getSurveyStatisticsForUser(User user, String type) {

		int[] result = new int[5];
		// published
		// unpublished
		// archived
		// pending changes

		Session session = sessionFactory.getCurrentSession();

		String ownerwhere = getOwnerWhere(type);

		String sql = "SELECT s.SURVEY_ID, s.ACTIVE, s.ISPUBLISHED, s.ARCHIVED, s.HASPENDINGCHANGES from SURVEYS s where s.ISDRAFT = 1 and ("
				+ ownerwhere + ") and (s.DELETED = 0 or s.DELETED is null)";

		Query query = session.createSQLQuery(sql);
		query.setParameter("userid", user.getId());

		if (type == null || !type.equalsIgnoreCase("my")) {
			query.setParameter("login", user.getLogin());
		}

		@SuppressWarnings("rawtypes")
		List res = query.list();

		for (Object o : res) {
			Object[] a = (Object[]) o;
			boolean active = (boolean) a[1];
			boolean archived = a[3] != null && (boolean) a[3];
			boolean pending = (boolean) a[4];

			if (archived) {
				result[2]++;
			} else {
				if (active) {
					result[0]++;
				} else {
					result[1]++;
				}
				if (pending)
					result[3]++;
			}

			result[4]++;
		}

		if (!type.equalsIgnoreCase("shared")) {
			List<Archive> archives = archiveService.getArchivesForUser(user.getId());
			result[2] += archives.size();
			result[4] += archives.size();
		}

		return result;
	}

	public Map<Date, List<String>> getSurveysWithEndDatesForUser(User user, String type) {
		Session session = sessionFactory.getCurrentSession();

		String ownerwhere = getOwnerWhere(type);
		String sql = "SELECT s.SURVEYNAME, s.SURVEY_END_DATE FROM SURVEYS s where s.ISDRAFT = 1 and (" + ownerwhere
				+ ") and s.SURVEY_END_DATE is not null and s.SURVEY_END_DATE >= CURDATE() and (s.ARCHIVED = 0 or s.ARCHIVED is null) and (s.DELETED = 0 or s.DELETED is null) and s.AUTOMATICPUBLISHING = 1";

		Query query = session.createSQLQuery(sql);
		query.setParameter("userid", user.getId());

		if (type == null || !type.equalsIgnoreCase("my")) {
			query.setParameter("login", user.getLogin());
		}

		Map<Date, List<String>> result = new TreeMap<>();

		@SuppressWarnings("rawtypes")
		List res = query.list();

		for (Object o : res) {
			Object[] a = (Object[]) o;

			if (!result.containsKey((Date) a[1])) {
				result.put((Date) a[1], new ArrayList<String>());
			}

			result.get((Date) a[1]).add((String) a[0]);
		}

		return result;
	}

	public String getSurveyMetaDataXML(Survey survey) throws NamingException {
		int results = 0;
		if (this.isReportingDatabaseEnabled()) {
			results = reportingService.getCount(false, survey.getUniqueId());
		} else {
			results = surveyService.getNumberPublishedAnswersFromMaterializedView(survey.getUniqueId());
		}
		StringBuilder s = new StringBuilder();
		User owner = survey.getOwner();
		EcasHelper.readData(owner, this.ldapService);

		s.append("<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n");
		s.append("<Survey id='").append(survey.getId()).append("' uid='").append(survey.getUniqueId()).append("' alias='").append(survey.getShortname())
				.append("'>\n");

		s.append("<ModifiedDate>").append(ConversionTools.getFullString4Webservice(survey.getUpdated())).append("</ModifiedDate>\n");
		
		s.append("<SurveyType>")
				.append(survey.getIsQuiz() ? "Quiz" : (survey.getIsOPC() ? "BRP Public Consultation" : (survey.getIsDelphi() ? "Delphi" : "Standard")))
				.append("</SurveyType>\n");

		s.append("<Title>").append(survey.getTitle()).append("</Title>\n");

		s.append("<PivotLanguage>").append(survey.getLanguage().getCode().toUpperCase()).append("</PivotLanguage>\n");

		s.append("<Owner>").append(owner.getGivenName()).append(" ").append(owner.getSurName()).append("</Owner>\n");

		s.append("<OwnerDepartment>").append(owner.getDepartment()).append("</OwnerDepartment>\n");

		s.append("<Contact>").append(survey.getContact()).append("</Contact>\n");

		s.append("<Status>").append(survey.getIsPublished() && survey.getIsActive() ? "published" : "unpublished")
				.append("</Status>\n");

		Survey published = surveyService.getSurvey(survey.getShortname(), false, false, false, false, null, true,
				false);
		s.append("<PendingChanges>")
				.append(published != null && surveyService.getPendingChanges(survey).size() > 0 ? "yes" : "no")
				.append("</PendingChanges>\n");

		s.append("<Start>").append(
				survey.getStart() == null ? "Unset" : ConversionTools.getFullString4Webservice(survey.getStart()))
				.append("</Start>\n");
		s.append("<End>")
				.append(survey.getEnd() == null ? "Unset" : ConversionTools.getFullString4Webservice(survey.getEnd()))
				.append("</End>\n");
		s.append("<Results>").append(results).append("</Results>\n");

		s.append("<AutomaticPublishing>").append(survey.getAutomaticPublishing() ? "yes" : "no")
				.append("</AutomaticPublishing>\n");

		s.append("<UseFulLinks>");
		for (String label : survey.getAdvancedUsefulLinks().keySet()) {
			s.append("<UseFulLink>\n");
			s.append("<url>").append(survey.getAdvancedUsefulLinks().get(label)).append("</url>\n");
			s.append("<label>").append(label).append("</label>\n");
			s.append("</UseFulLink>\n");
		}
		s.append("</UseFulLinks>\n");

		s.append("<BackgroundDocuments>");
		for (String label : survey.getBackgroundDocumentsAlphabetical().keySet()) {
			s.append("<BackgroundDocument>\n");
			s.append("<url>").append(survey.getBackgroundDocumentsAlphabetical().get(label)).append("</url>\n");
			s.append("<label>").append(label).append("</label>\n");
			s.append("</BackgroundDocument>\n");
		}
		s.append("</BackgroundDocuments>\n");

		s.append("<Security>");
		if (survey.getSecurity().startsWith("open")) {
			s.append("open");
		} else {
			s.append("secured");
			if (survey.getPassword() != null && survey.getPassword().length() > 0) {
				s.append(";PW:").append(survey.getPassword());
			}
			if (survey.getEcasSecurity()) {
				if (survey.getEcasMode() != null && survey.getEcasMode().equalsIgnoreCase("all")) {
					s.append(";EULogin_all");
				} else {
					s.append(";EULogin_internal");
				}
			}
		}
		s.append("</Security>\n");

		s.append("<Privacy>").append(survey.getSecurity().endsWith("anonymous") ? "yes" : "no").append("</Privacy>\n");

		s.append("<Visibility>").append(survey.getListForm() ? "public" : "private").append("</Visibility>\n");

		s.append("<Captcha>").append(survey.getCaptcha() ? "yes" : "no").append("</Captcha>\n");

		s.append("<Contribution>").append(survey.getChangeContribution() ? "yes" : "no").append("</Contribution>\n");

		s.append("<DownloadContribution>").append(survey.getDownloadContribution() ? "yes" : "no")
				.append("</DownloadContribution>\n");

		s.append("<Draft>").append(survey.getSaveAsDraft() ? "yes" : "no").append("</Draft>\n");

		s.append("<Skin>").append(survey.getSkin() != null ? survey.getSkin().getName() : "no skin")
				.append("</Skin>\n");

		s.append("<PublishedResults>");
		if (survey.getPublication() != null
				&& (survey.getPublication().isShowContent() || survey.getPublication().isShowStatistics())) {
			boolean first = true;
			if (survey.getPublication().isShowContent()) {
				s.append("Answers");
				first = false;
			}
			if (survey.getPublication().isShowStatistics()) {
				if (!first)
					s.append(Constants.PATH_DELIMITER);
				s.append("Statistics");
				first = false;
			}
			if (survey.getPublication().isShowSearch()) {
				if (!first)
					s.append(Constants.PATH_DELIMITER);
				s.append("Search");
			}
		}
		s.append("</PublishedResults>\n");

		s.append("<ConfirmationPage>");
		if (survey.getConfirmationPageLink() != null && survey.getConfirmationPageLink()) {
			s.append(survey.getConfirmationLink());
		} else {
			s.append(survey.getConfirmationPage());
		}
		s.append("</ConfirmationPage>\n");

		s.append("<UnavailabilityPage>");
		if (survey.getEscapePageLink() != null && survey.getEscapePageLink()) {
			s.append(survey.getEscapeLink());
		} else {
			s.append(survey.getEscapePage());
		}
		s.append("</UnavailabilityPage>\n");

		s.append("</Survey>");

		return s.toString();
	}
	
	@Transactional(readOnly = true)
	public String getSurveyUID(int surveyID) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT s.uniqueId FROM Survey s WHERE s.id = :id";

		Query<String> query = session.createQuery(sql, String.class);
		query.setParameter("id", surveyID);

		String result = query.uniqueResult();
		return result;
	}

	@Transactional(readOnly = true)
	public int getHighestSurveyId() {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT MAX(s.SURVEY_ID) FROM SURVEYS s";

		Query query = session.createSQLQuery(sql);

		int result = ConversionTools.getValue(query.uniqueResult());
		return result;
	}

	public void generateAccessInformation(List<Survey> surveys, User user) {
		if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) > 1) {
			return;
		}

		for (Survey survey : surveys) {
			if (!survey.getOwner().getId().equals(user.getId())) {
				Access access = surveyService.getAccess(survey.getId(), user.getId());
				survey.setFullFormManagementRights(
						access != null && access.getLocalPrivileges().get(LocalPrivilege.FormManagement) > 1);
				survey.setFormManagementRights(
						access != null && access.getLocalPrivileges().get(LocalPrivilege.FormManagement) > 0);
				survey.setAccessResultsRights(
						access != null && (access.getLocalPrivileges().get(LocalPrivilege.AccessResults) > 0
								|| access.getLocalPrivileges().get(LocalPrivilege.AccessDraft) > 0));
			}

			String disabled = settingsService.get(Setting.CreateSurveysForExternalsDisabled);
			if (disabled.equalsIgnoreCase("true") && user.getGlobalPrivileges().get(GlobalPrivilege.ECAccess) == 0) {
				survey.setCanCreateSurveys(false);
			}
		}
	}

	@Transactional(readOnly = true)
	public List<String> getAllSurveyUIDs(boolean onlypublished) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT DISTINCT SURVEY_UID FROM SURVEYS";

		if (onlypublished) {
			sql += " WHERE ISPUBLISHED = 1";
		}

		Query query = session.createSQLQuery(sql);

		@SuppressWarnings("unchecked")
		List<String> result = query.list();

		return result;
	}

	@Transactional(readOnly = true)
	public int getNumberOfSurveys(boolean draftSurveys) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT COUNT(DISTINCT SURVEY_UID) FROM SURVEYS WHERE ISDRAFT = " + (draftSurveys ? "1" : "0");
		Query query = session.createSQLQuery(sql);
		return ConversionTools.getValue(query.uniqueResult());
	}

	@Transactional
	public int getAbuseReportsForSurvey(String surveyuid) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT COUNT(DISTINCT SURABUSE_ID) FROM SURABUSE WHERE SURABUSE_SURVEY = :uid";
		Query query = session.createSQLQuery(sql);
		query.setParameter("uid", surveyuid);
		return ConversionTools.getValue(query.uniqueResult());
	}

	@Transactional
	public void reportAbuse(Survey survey, String type, String text, String email)
			throws Exception {
		Session session = sessionFactory.getCurrentSession();

		int count = getAbuseReportsForSurvey(survey.getUniqueId());

		Abuse abuse = new Abuse(survey.getUniqueId(), type, text, email);
		session.saveOrUpdate(abuse);

		// send email
		String maxs = settingsService.get(Setting.MaxReports);
		int max = Integer.parseInt(maxs);
		String mailtext = settingsService.get(Setting.ReportText);
		String recipients = settingsService.get(Setting.ReportRecipients);

		if (count < max) {
			Locale locale = new Locale("EN");
			switch (type) {
			case "fake":
				type = resources.getMessage("info.ReportAbuseFake", null, "Fake", locale);
				break;
			case "propaganda":
				type = resources.getMessage("info.ReportAbusePropaganda", null, "Propaganda", locale);
				break;
			case "hate":
				type = resources.getMessage("info.ReportAbuseHate", null, "Hate", locale);
				break;
			case "images":
				type = resources.getMessage("info.ReportAbuseImages", null, "Images", locale);
				break;
			case "promo":
				type = resources.getMessage("info.ReportAbusePromo", null, "Promo", locale);
				break;
			case "others":
				type = resources.getMessage("info.ReportAbuseOthers", null, "Others", locale) + ":" + text;
				break;
			}

			String link = serverPrefix + "runner/" + survey.getShortname();

			mailtext = mailtext.replace("[ALIAS]", survey.getShortname()).replace("[LINK]", link)
					.replace("[TITLE]", survey.getTitle()).replace("[ID]", survey.getId().toString())
					.replace("[EMAIL]", email).replace("[TYPE]", type).replace("[TEXT]", text)
					.replace("[DATE]", abuse.getCreated().toString()).replace("[COUNT]", Integer.toString(count + 1));

			InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
			mailtext = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", mailtext).replace("[HOST]", host);

			String[] emails = recipients.split(";");
			for (String recipient : emails) {
				if (recipient.trim().length() > 0) {
					mailService.SendHtmlMail(recipient, sender, sender, "Survey Abuse Reported", mailtext, null);
				}
			}
		}
	}

	@Transactional
	public void sendAbuseReportsMailForYesterday() {
		try {
			String recipients = settingsService.get(Setting.ReportRecipients);
			if (recipients == null || recipients.trim().length() == 0)
				return;
			String[] emails = recipients.split(";");

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date today = cal.getTime();
			cal.add(Calendar.DAY_OF_YEAR, -1);
			Date yesterday = cal.getTime();

			Session session = sessionFactory.getCurrentSession();
			String sql = "SELECT SURABUSE_SURVEY, s.SURVEYNAME, s.TITLESORT, COUNT(SURABUSE_ID) FROM SURABUSE sa LEFT JOIN SURVEYS s ON s.SURVEY_ID = (SELECT SURVEY_ID FROM SURVEYS s2 WHERE s2.SURVEY_UID = sa.SURABUSE_SURVEY LIMIT 1) WHERE SURABUSE_DATE >= :yesterday AND SURABUSE_DATE < :today GROUP BY SURABUSE_SURVEY";
			Query query = session.createSQLQuery(sql);
			query.setParameter("today", today);
			query.setParameter("yesterday", yesterday);

			@SuppressWarnings("rawtypes")
			List res = query.list();

			if (res.isEmpty()) {
				return;
			}

			InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
			StringBuilder content = new StringBuilder();

			content.append(
					"Surveys reported the day before:<br /><table><tr><th style='text-align: left'>Survey UID</th><th style='text-align: left'>Alias</th><th style='text-align: left'>Title</th><th style='text-align: left'>Number of Reports</th></tr>");

			for (Object o : res) {
				Object[] a = (Object[]) o;

				content.append("<tr>");
				content.append("<td>").append(a[0]).append("</td>");
				content.append("<td>").append(a[1]).append("</td>");
				content.append("<td>").append(a[2]).append("</td>");
				content.append("<td>").append(ConversionTools.getValue(a[3])).append(" reports</td>");
				content.append("</tr>");
			}

			content.append("</table>");

			String mailtext = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", content.toString())
					.replace("[HOST]", host);

			for (String recipient : emails) {
				if (recipient.trim().length() > 0) {
					mailService.SendHtmlMail(recipient, sender, sender, "Survey Abuse Report Statistics", mailtext,
							null);
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@Transactional
	public void freeze(String surveyId, String mailText) throws Exception {
		Survey survey = getSurvey(Integer.parseInt(surveyId));

		if (survey == null) {
			throw new MessageException("survey does not exist");
		}
	
		unpublish(survey, true, -1, true);
	
		// send email
		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String mailtext = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", mailText).replace("[HOST]", host);
		mailService.SendHtmlMail(survey.getOwner().getEmail(), sender, sender, "Your survey has been blocked", mailtext,
				null);

		mailService.SendHtmlMail(monitoringEmail, sender, sender, "Your survey has been blocked", mailtext, null);
	}

	@Transactional
	public void unfreeze(String surveyId) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		Survey survey = getSurvey(Integer.parseInt(surveyId));

		if (survey == null) {
			throw new MessageException("survey does not exist");
		}

		survey.setIsFrozen(false);
		session.saveOrUpdate(survey);

		String url = "[HOST]/" + survey.getShortname() + "/management/overview";

		String mailText = "Your survey<br />" + survey.getShortname() + "<br /><a href='" + url + "'>" + url + "</a>"
				+ "<br />" + survey.getTitleSort() + "<br />is now available again and can be published.";

		// send email
		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String mailtext = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", mailText).replace("[HOST]", host);
		mailService.SendHtmlMail(survey.getOwner().getEmail(), sender, sender, "Your survey is available again",
				mailtext, null);
	}

	public String getMySurveysXML(SurveyFilter filter, ArchiveFilter archiveFilter) throws Exception {
		StringBuilder s = new StringBuilder();

		s.append("<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n");
		s.append("<Surveys user='").append(filter.getUser().getLogin()).append("'>\n");

		if (archiveFilter != null) {
			List<Archive> archives = archiveService.getAllArchives(archiveFilter, 1, 10000, true);
			for (Archive archive : archives) {
				s.append("<Survey uid='").append(archive.getSurveyUID()).append("' alias='")
						.append(archive.getSurveyShortname()).append("'>\n");
				s.append("<Title>").append(ConversionTools.removeHTML(archive.getSurveyTitle())).append("</Title>\n");
				s.append("</Survey>\n");
			}
		}

		SqlPagination pagination = new SqlPagination(1, 10000);

		StringBuilder stringBuilder = new StringBuilder(512);
		stringBuilder.append("SELECT s.SURVEY_UID");
		stringBuilder.append(" , s.SURVEYNAME");
		stringBuilder.append(" , s.TITLE");
		stringBuilder.append(" , s.OWNER");
		
		stringBuilder.append(
				", (SELECT MIN(SURVEY_CREATED) FROM SURVEYS WHERE ISDRAFT = 0 AND SURVEY_UID = s.SURVEY_UID) as firstPublished");
		stringBuilder.append(
				", (SELECT MAX(SURVEY_CREATED) FROM SURVEYS WHERE ISDRAFT = 0 AND SURVEY_UID = s.SURVEY_UID) as published");
				
		stringBuilder.append(" FROM SURVEYS s");
		stringBuilder.append(" LEFT JOIN MV_SURVEYS_NUMBERPUBLISHEDANSWERS npa on s.SURVEY_UID = npa.SURVEYUID ");

		if (filter.getMinReported() != null && filter.getMinReported() > 0) {
			stringBuilder.append(
					" LEFT JOIN ( SELECT SURABUSE_SURVEY, count(SURABUSE_ID) as abuses FROM SURABUSE GROUP BY SURABUSE_SURVEY) abu ON abu.SURABUSE_SURVEY = s.SURVEY_UID");
		}

		stringBuilder.append(" where");

		if (archiveFilter != null) {
			stringBuilder.append(" (s.ARCHIVED = 1)");
		} else if (filter.getDeleted() != null && filter.getDeleted()) {
			stringBuilder.append(" (s.DELETED = " + (filter.getDeleted() ? "1" : "0") + ")");
		} else if (filter.getFrozen() != null) {
			stringBuilder.append(" (s.FROZEN " + (filter.getFrozen() ? " > 0" : " < 1") + ")");
		} else {
			stringBuilder.append(" (s.ARCHIVED = 0 or s.ARCHIVED is null) and (s.DELETED = 0 or s.DELETED is null)");
		}
		
		if (archiveFilter == null) {
			stringBuilder.append(" AND (s.ISDRAFT = 1)");
		}
		
		String sql = stringBuilder.toString();

		HashMap<String, Object> parameters = new HashMap<>();
		sql += getSql(filter, parameters);

		List<Survey> surveys = new ArrayList<>();
		for (Object[] row : loadSurveysfromDatabase(sql, parameters, pagination)) {
			Survey survey = new Survey();
			survey.setUniqueId((String) row[0]);
			survey.setShortname((String) row[1]);
			survey.setTitle((String) row[2]);

			int ownerId = ConversionTools.getValue(row[3]);
			User owner = administrationService.getUser(ownerId);
			survey.setOwner(owner);
			surveys.add(survey);
		}

		for (Survey survey : surveys) {
			if (filter.getUserDepartment() != null && filter.getUserDepartment().length() > 1) {
				List<String> departments = ldapService.getUserLDAPGroups(survey.getOwner().getLogin());
				if (!departments.contains(filter.getUserDepartment())) {
					continue;
				}
			}

			s.append("<Survey uid='").append(survey.getUniqueId()).append("' alias='").append(survey.getShortname())
					.append("'>\n");
			s.append("<Title>").append(ConversionTools.removeHTML(survey.getTitle())).append("</Title>\n");
			s.append("</Survey>\n");
		}

		s.append("</Surveys>");

		return s.toString();
	}

	@Transactional
	public List<Integer> getSurveysForUser(int userid) {
		Session session = sessionFactory.getCurrentSession();
		NativeQuery query = session.createSQLQuery("SELECT SURVEY_ID FROM SURVEYS WHERE OWNER = :id AND ISDRAFT = 1");

		@SuppressWarnings("rawtypes")
		List surveys = query.setParameter("id", userid).list();
		List<Integer> result = new ArrayList<>();

		for (Object o : surveys) {
			result.add(ConversionTools.getValue(o));
		}

		return result;
	}

	public String getNotificationEmailText(Survey survey, String email, Locale locale) {
		String notificationemailtext = null;
		if (survey.getSendConfirmationEmail() && email != null && email.contains("@")) {
			String[] args = new String[] { email };
			notificationemailtext = resources.getMessage("message.ConfirmationEmailText", args,
					"We’ve sent you a confirmation email to your registered email address: {0}.", locale);
		}
		return notificationemailtext;
	}

	public void sendNotificationEmail(Survey survey, AnswerSet answerSet, String email) throws MessageException, IOException {
		StringBuilder body = new StringBuilder();
		body.append("Dear EUSurvey user,<br /><br />Thank you for having submitted your contribution to the following survey: <b>");
		body.append(survey.shortCleanTitle());
		body.append("</b><br />Your contribution has the following ID: <b>");
		body.append(answerSet.getUniqueCode());
		body.append("</b><br /><br />");
		
		if (survey.getDownloadContribution()) {		
			body.append("To download your contribution, please click on this link: <a href='").append(host).append("home/downloadcontribution?email=").append(email).append("&code=").append(answerSet.getUniqueCode()).append("'>Get PDF</a><br />");
		}
		
		if (survey.getChangeContribution())
		{
			body.append("To edit your contribution, please click on this link: <a href='").append(host).append("home/editcontribution?code=").append(answerSet.getUniqueCode()).append("'>Edit</a><br />");
			body.append("<br />You can edit your contribution only as long as the survey is open and running.");
		}
		
		body.append("<br /><br />The EUSurvey team");
		
		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String mailtext = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body.toString()).replace("[HOST]", host);
		
		mailService.SendHtmlMail(email, sender, sender, "Confirmation of your submission", mailtext, null);		
	}

	@Transactional
	public Set<String> getRankingQuestionUids(int surveyId) {
		Survey survey = getSurvey(surveyId);
		Set<String> result = new HashSet<>();
		for (Element element : survey.getElements()) {
			if (element instanceof RankingQuestion) {
				result.add(element.getUniqueId());
			}
		}
		return result;
	}
	
	@Transactional
	public Set<String> getGalleryQuestionUids(int surveyId) {
		Survey survey = getSurvey(surveyId);
		Set<String> result = new HashSet<>();
		for (Element element : survey.getElements()) {
			if (element instanceof GalleryQuestion) {
				result.add(element.getUniqueId());
			}
		}
		return result;
	}
	
	@Transactional
	public Set<String> getTargetDatasetQuestionUids(int surveyId) {
		Survey survey = getSurvey(surveyId);
		Set<String> result = new HashSet<>();
		if (survey.getIsSelfAssessment()) {
			for (Element element : survey.getElements()) {
				if (element instanceof SingleChoiceQuestion) {
					SingleChoiceQuestion scq = (SingleChoiceQuestion)element;
					if (scq.getIsTargetDatasetQuestion()) {
						result.add(element.getUniqueId());
						break; // there can be only one in a survey
					}
				}
			}
		}
		return result;
	}

	@Transactional
	public void updateCodaLink(String uid, String link){
		Survey draft = getSurvey(uid, true, false, false, false, null, false, false);
		Survey published = getSurvey(uid, false, false, false, false, null, false, false);
		if (draft != null){

			draft.setCodaLink(link.trim());

			User owner = draft.getOwner();

			if (draft.getCodaWaiting()){

				draft.setCodaWaiting(false);

				if (link.trim().length() > 0) {
					String message = resources.getMessage("message.CodaDashboardAvailable", new String[]{}, "Your data analytics dashboard is now available",
							new Locale(owner.getLanguage()));
					systemService.sendUserSuccessMessage(owner.getId(), message);
				} else {
					String message = resources.getMessage("message.CodaRequestError", new String[]{}, "Your data analytics dashboard could not be requested. Try again later",
							new Locale(owner.getLanguage()));
					systemService.sendUserErrorMessage(owner.getId(), message);
				}
			}
		}

		if (published != null){
			published.setCodaLink(link.trim());
			published.setCodaWaiting(false);
		}

	}

	@Transactional
	public void setCodaWaiting(String uid, boolean waiting){
		Survey draft = getSurvey(uid, true, false, false, false, null, false, false);
		Survey published = getSurvey(uid, false, false, false, false, null, false, false);
		if (draft != null){
			draft.setCodaWaiting(waiting);
		}

		if (published != null){
			published.setCodaWaiting(waiting);
		}

	}

	@Transactional
	public void checkSurveyCreationLimit(Integer userid) throws SurveyCreationLimitExceededException {
		int maxSurveysPerUser = Integer.parseInt(settingsService.get(Setting.MaxSurveysPerUser));
		int maxSurveysTimespan = Integer.parseInt(settingsService.get(Setting.MaxSurveysTimespan));
		
		Session session = sessionFactory.getCurrentSession();
		NativeQuery query = session.createSQLQuery("SELECT COUNT(*) FROM SURVEYS WHERE OWNER = :id AND ISDRAFT = 1 AND SURVEY_CREATED > now() - INTERVAL :minutes MINUTE");
		
		Object count = query.setParameter("id", userid).setParameter("minutes", maxSurveysTimespan).uniqueResult();
		
		int result = ConversionTools.getValue(count);
		
		if (result >= maxSurveysPerUser) {
			throw new SurveyCreationLimitExceededException();
		}
	}

	public String getPrivilegedUsersXML(String uniqueId, String alias) {
		StringBuilder s = new StringBuilder();
		
		s.append("<?xml version='1.0' encoding='UTF-8' standalone='no' ?>\n");
		s.append("<PrivilegedUsers uid='").append(uniqueId).append("' alias='").append(alias).append("'>\n");
		
		Survey draft = getSurvey(uniqueId,true, false, false, false, null, true, false, false, false);
		
		// owner
		s.append("<User login='").append(draft.getOwner().getLogin()).append("'>");				
		s.append("<PrivilegeType>OWNER</PrivilegeType>");			
		s.append("<Access>ManageInvitations:2;FormManagement:2;AccessDraft:2;AccessResults:2;</Access>"); //owner has always full access
		s.append("</User>");
		
		// standard access
		List<Access> accesses = getAccesses(draft.getId());		
		for (Access access: accesses) {
			s.append("<User login='").append(access.getUser().getLogin()).append("'>");				
			s.append("<PrivilegeType>STANDARD</PrivilegeType>");			
			s.append("<Access>");
			s.append(access.getPrivileges());
			s.append("</Access>");
			s.append("</User>");
		}
		
		// privileged result access
		List<ResultAccess> resultAccesses = getResultAccesses(null, uniqueId, 1, Integer.MAX_VALUE, null, null, null, null);
		for (ResultAccess access: resultAccesses) {
			User user = administrationService.getUser(access.getUser());			
			s.append("<User login='").append(user.getLogin()).append("'>");				
			s.append("<PrivilegeType>RESULTS</PrivilegeType>");			
			s.append("<Access>");
			s.append("<Access>ManageInvitations:0;FormManagement:0;AccessDraft:0;AccessResults:" + (access.isReadonly() ? "1" : "2") + ";</Access>"); //only access to results
			s.append("</Access>");
			s.append("</User>");
		}
		
		s.append("</PrivilegedUsers>");

		return s.toString();

	}	
	
	private void analyzeCharge(String hql, String code, int year, int month, int monthEnd, boolean v1, boolean v1_2, boolean v2, Map<String, OrganisationCharge> organisations) {
		Session session = sessionFactory.getCurrentSession();
		boolean skipCode =  code.equalsIgnoreCase("all");
		Query<?> query = session.createQuery(hql);
		query.setParameter("year", year);
		if (month > 0) query.setParameter("month", month);
		if (monthEnd > 0) query.setParameter("monthEnd", monthEnd);
		if (!skipCode) query.setParameter("org", code);
		List<?> res = query.list();
		for (Object o : res) {
			Object[] a = (Object[]) o;
			String organisation = (String)a[0];
			
			if (com.mysql.cj.util.StringUtils.isNullOrEmpty(organisation)) {
				organisation = "unset";
			}
			
			Date published = (Date)a[1];
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(published);
			int mon = calendar.get(Calendar.MONTH) + 1;
			String monthPublished = (mon < 10 ? "0" : "") + mon + " " + calendar.get(Calendar.YEAR);
			
			String surveyUID = (String)a[2];
			
			if (!organisations.containsKey(organisation)) {
				OrganisationCharge c = new OrganisationCharge();
				c.name = organisation;				
				organisations.put(organisation, c);
			}
			
			OrganisationCharge c = organisations.get(organisation);
			if (!c.monthly.containsKey(monthPublished)) {
				MonthlyCharge m = new MonthlyCharge();
				c.monthly.put(monthPublished, m);
			}
			
			MonthlyCharge mo = c.monthly.get(monthPublished);
			
			if (v1) {			
				mo.v1++;
			}
			
			if (v1_2) {
				// only count if surveys still exists
				Survey survey = getSurveyByUniqueId(surveyUID, false, true);
				if (survey != null && !survey.getIsDeleted() && !survey.getArchived())
				{
					mo.v1_2++;
				}
			}
			
			if (v2) {			
				mo.v2++;
			}
		}
	}
	
	public String organisationReportJSON(String code, int year, int month, int monthEnd, int minPublishedSurveys) throws JsonProcessingException {	
		Collection<OrganisationCharge> organisations = surveyService.getOrganisationCharges(code, year, month, monthEnd, minPublishedSurveys);
		
		ObjectMapper mapper = new ObjectMapper();
		String stringResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(organisations);
		
		return stringResult;
	}

	public Collection<OrganisationCharge> getOrganisationCharges(String code, int year, int month, int monthEnd, int minPublishedSurveys) {
		Map<String, OrganisationCharge> organisations = new HashMap<String, OrganisationCharge>();			
			
		boolean skipCode =  code.equalsIgnoreCase("all");
		
		String monthPart = "";
		if (monthEnd > 0 && month > 0) {
			monthPart = " AND MONTH(published) >= :month AND MONTH(published) <= :monthEnd";
		} else if (month > 0) {
			monthPart = " AND MONTH(published) = :month";
		}	
		
		// get published surveys
		String hql = "SELECT organisation, published, surveyUID FROM PublishedSurvey WHERE YEAR(published) = :year" + monthPart + (!skipCode ? " AND organisation = :org" : "");
			
		analyzeCharge(hql, code, year, month, monthEnd, true, false, false, organisations);
		
		// get multi-annual published surveys
		hql = "SELECT organisation, published, surveyUID FROM PublishedSurvey WHERE YEAR(published) < :year" + monthPart + (!skipCode ? " AND organisation = :org" : "");
		analyzeCharge(hql, code, year, month, monthEnd, false, true, false, organisations);
				
		// get submitted contributions
		monthPart = "";
		if (monthEnd > 0 && month > 0) {
			monthPart = " AND MONTH(submitted) >= :month AND MONTH(submitted) <= :monthEnd";
		} else if (month > 0) {
			monthPart = " AND MONTH(submitted) = :month";
		}
		hql = "SELECT organisation, submitted, surveyUID FROM SubmittedContribution WHERE YEAR(submitted) = :year" + monthPart + (!skipCode ? " AND organisation = :org" : "");
		analyzeCharge(hql, code, year, month, monthEnd, false, false, true, organisations);
		
		if (minPublishedSurveys == 0) {
			return organisations.values();
		}
		
		Collection<OrganisationCharge> filteredResult = new ArrayList<OrganisationCharge>();
		
		for (OrganisationCharge organisationCharge : organisations.values()) {
			if (organisationCharge.getTotal_v1() >= minPublishedSurveys) {
				filteredResult.add(organisationCharge);
			}
		}
						
		return filteredResult;
	}

	public OrganisationResult getSurveysByOrganisation(String code) {
		OrganisationResult result = new OrganisationResult();
		
		Session session = sessionFactory.getCurrentSession();
		
		boolean skipCode =  code.equalsIgnoreCase("all");
		String codePart = skipCode ? "" : " AND ORGANISATION = :code";
		
		String sql = "SELECT COUNT(*) FROM SURVEYS WHERE ISDRAFT = 1" + codePart;
		NativeQuery query = session.createSQLQuery(sql);
		if (!skipCode) query.setParameter("code", code);	
		result.setAllSurveys(ConversionTools.getValue(query.uniqueResult()));
		
		sql = "SELECT COUNT(*) FROM (SELECT SURVEY_UID, MIN(SURVEY_CREATED) FROM SURVEYS A WHERE ISDRAFT = 0" + codePart + " GROUP BY SURVEY_UID HAVING YEAR(MIN(SURVEY_CREATED)) = YEAR(CURRENT_DATE())) AS T2";
		query = session.createSQLQuery(sql);
		if (!skipCode) query.setParameter("code", code);		
		result.setSurveysThisYear(ConversionTools.getValue(query.uniqueResult()));		
		
		sql = "SELECT COUNT(*) FROM (SELECT SURVEY_UID, MIN(SURVEY_CREATED) FROM SURVEYS A WHERE ISDRAFT = 0" + codePart + " GROUP BY SURVEY_UID HAVING YEAR(MIN(SURVEY_CREATED)) = YEAR(CURRENT_DATE()) - 1) AS T2";
		query = session.createSQLQuery(sql);
		if (!skipCode) query.setParameter("code", code);		
		result.setSurveysLastYear(ConversionTools.getValue(query.uniqueResult()));		

		sql = "SELECT COUNT(*) FROM ANSWERS_SET WHERE ISDRAFT = 0 AND SURVEY_ID IN (SELECT SURVEY_ID FROM SURVEYS WHERE ISDRAFT = 0" + codePart + ")";
		query = session.createSQLQuery(sql);
		if (!skipCode) query.setParameter("code", code);
		result.setAllContributions(ConversionTools.getValue(query.uniqueResult()));
		
		sql = "SELECT COUNT(*) FROM ANSWERS_SET WHERE ISDRAFT = 0 AND SURVEY_ID IN (SELECT MIN(SURVEY_ID) FROM SURVEYS WHERE ISDRAFT = 0" + codePart + " GROUP BY SURVEY_UID HAVING YEAR(MIN(SURVEY_CREATED)) = YEAR(CURRENT_DATE()))";
		query = session.createSQLQuery(sql);
		if (!skipCode) query.setParameter("code", code);
		result.setContributionsThisYear(ConversionTools.getValue(query.uniqueResult()));
		
		sql = "SELECT COUNT(*) FROM ANSWERS_SET WHERE ISDRAFT = 0 AND SURVEY_ID IN (SELECT MIN(SURVEY_ID) FROM SURVEYS WHERE ISDRAFT = 0" + codePart + " GROUP BY SURVEY_UID HAVING YEAR(MIN(SURVEY_CREATED)) = YEAR(CURRENT_DATE()) - 1)";
		query = session.createSQLQuery(sql);
		if (!skipCode) query.setParameter("code", code);
		result.setContributionsLastYear(ConversionTools.getValue(query.uniqueResult()));		
		
		return result;
	}

	public List<List<String>> getSurveysByOrganisation(String code, int year, int month) {
		Session session = sessionFactory.getCurrentSession();
		List<List<String>> result = new ArrayList<List<String>>(); 
		boolean skipCode =  code.equalsIgnoreCase("all");
		
		// get all surveys that were first published in that period
		String firstPublishedSurveys = "SELECT SURVEY_UID, MIN(SURVEY_CREATED) FROM SURVEYS WHERE ISDRAFT = 0 " + (skipCode ? "" : " AND ORGANISATION = :code ") + "GROUP BY SURVEY_UID HAVING YEAR(MIN(SURVEY_CREATED)) = :year " + ((month > 0) ? " AND MONTH(MIN(SURVEY_CREATED)) = :month" : "");
		NativeQuery<?> query = session.createSQLQuery(firstPublishedSurveys);
		if (!skipCode) query.setParameter("code", code);	
		query.setParameter("year", year);	
		if (month > 0) query.setParameter("month", month);	
		List<?> res = query.list();
		Map<String, Date> firstPublishedDates = new HashMap<>();
		for (Object o : res) {
			Object[] a = (Object[]) o;
			firstPublishedDates.put((String)a[0], (Date)a[1]);
		}
		
		// get all surveys that received contributions in that period
		String surveysWithContributions = "SELECT s.SURVEY_UID, MIN(s.SURVEY_CREATED) FROM ANSWERS_SET ans JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID WHERE s.ISDRAFT = 0 AND ans.ISDRAFT = 0 " + (skipCode ? "" : " AND ORGANISATION = :code ") + "AND year(ans.ANSWER_SET_DATE) = :year " + ((month > 0) ? " AND MONTH(ans.ANSWER_SET_DATE) = :month" : "") + " GROUP BY SURVEY_UID" ;
		query = session.createSQLQuery(surveysWithContributions);
		if (!skipCode) query.setParameter("code", code);	
		query.setParameter("year", year);	
		if (month > 0) query.setParameter("month", month);	
		res = query.list();
		for (Object o : res) {
			Object[] a = (Object[]) o;
			if (!firstPublishedDates.containsKey((String)a[0])) {
				firstPublishedDates.put((String)a[0], (Date)a[1]);
			}
		}
		
		String numberResults = "SELECT s.SURVEY_UID, COUNT(*) FROM ANSWERS_SET ans JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID WHERE s.ISDRAFT = 0 AND ans.ISDRAFT = 0 AND s.SURVEY_UID IN (:uids) GROUP BY s.SURVEY_UID";
		query = session.createSQLQuery(numberResults);
		query.setParameter("uids", firstPublishedDates.keySet());
		res = query.list();
		Map<String, Integer> numberOfResults = new HashMap<>();
		for (Object o : res) {
			Object[] a = (Object[]) o;
			numberOfResults.put((String)a[0], ConversionTools.getValue(a[1]));
		}
		
		String numberResultsInPeriod = "SELECT s.SURVEY_UID, COUNT(*) FROM ANSWERS_SET ans JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID WHERE s.ISDRAFT = 0 AND ans.ISDRAFT = 0 AND s.SURVEY_UID IN (:uids) AND year(ans.ANSWER_SET_DATE) = :year " + ((month > 0) ? " AND MONTH(ans.ANSWER_SET_DATE) = :month" : "") + " GROUP BY s.SURVEY_UID";
		query = session.createSQLQuery(numberResultsInPeriod);
		query.setParameter("uids", firstPublishedDates.keySet());
		query.setParameter("year", year);	
		if (month > 0) query.setParameter("month", month);	
		res = query.list();
		Map<String, Integer> numberOfResultsInPeriod = new HashMap<>();
		for (Object o : res) {
			Object[] a = (Object[]) o;
			numberOfResultsInPeriod.put((String)a[0], ConversionTools.getValue(a[1]));
		}		
		
		String surveys = "SELECT s.SURVEY_UID, s.ORGANISATION, s.SURVEYNAME, s.TITLESORT, s.ACTIVE, s.DELETED, s.ARCHIVED, u.USER_LOGIN, u.USER_EMAIL, s.VALIDATOR, s.ECF, s.EVOTE, s.OPC, s.QUIZ, s.DELPHI, s.SELFASSESSMENT FROM SURVEYS s JOIN USERS u ON u.USER_ID = s.OWNER WHERE ISDRAFT = 1 AND SURVEY_UID IN (:uids)";		
		
		query = session.createSQLQuery(surveys);
		query.setParameter("uids", firstPublishedDates.keySet());
		
		res = query.list();
		
		List<String> row = new ArrayList<String>();
		row.add("Organisation");
		row.add("Period");
		row.add("Alias");
		row.add("Title");
		row.add("Status");
		row.add("Owner");
		row.add("Owner email");
		row.add("Validator");
		row.add("Type");	
		row.add("First published");	
		row.add("Total contributions");	
		row.add("Contributions in period");	
		result.add(row);
		
		for (Object o : res) {
			row = new ArrayList<String>();
			Object[] a = (Object[]) o;
			
			String uid = (String)a[0];
			
			row.add((String)a[1]); // organisation
			
			if (month > 0) {
				row.add(Integer.toString(year) + " " + Integer.toString(month)); // period
			} else {			
				row.add(Integer.toString(year)); // period
			}
			
			row.add((String)a[2]); // alias
			row.add((String)a[3]); //title
			
			Boolean active = (Boolean)a[4];
			Boolean deleted = (Boolean)a[5];
			Boolean archived = (Boolean)a[6];
			
			row.add(deleted ? "Deleted" : (archived ? "Archived" : (active ? "Published" : "Unpublished"))); //status
			
			row.add((String)a[7]); // owner login
			row.add((String)a[8]); // owner email
			row.add((String)a[9]); // validator
			
			Boolean ecf = (Boolean)a[10];
			Boolean evote = (Boolean)a[11];
			Boolean opc = (Boolean)a[12];
			Boolean quiz = (Boolean)a[13];
			Boolean delphi = (Boolean)a[14];
			Boolean sa = (Boolean)a[15];
			
			// type
			if (ecf) {
				row.add("ECF");
			} else if (evote) {
				row.add("EVOTE");
			} else if (opc) {
				row.add("BRP");
			} else if (quiz) {
				row.add("Quiz");
			} else if (delphi) {
				row.add("Delphi");
			} else if (sa) {
				row.add("Self Assessment");
			} else {
				row.add("Standard");
			};
			
			row.add(ConversionTools.getFullString(firstPublishedDates.get(uid)));
			row.add(numberOfResults.containsKey(uid) ? Integer.toString(numberOfResults.get(uid)) : "0");
			row.add(numberOfResultsInPeriod.containsKey(uid) ? Integer.toString(numberOfResultsInPeriod.get(uid)) : "0");
						
			result.add(row);
		}
		
		return result;
	}
	
	@Transactional
	public void sendOrganisationValidationEmail(int id, User user) throws MessageException, IOException {
		Survey survey = getSurvey(id);
		internalSendOrganisationValidationEmail(survey, user);
	}

	@Transactional
	public void sendOrganisationValidationEmail(String uid, User user) throws MessageException, IOException {
		Survey survey = getSurvey(uid, true, false, false, false, null, false, false);
		internalSendOrganisationValidationEmail(survey, user);
	}
	
	private void internalSendOrganisationValidationEmail(Survey survey, User user) throws MessageException, IOException {
		survey.setValidationCode(UUID.randomUUID().toString());
		survey.setValidated(false);
		
		String body = "Dear Sir or Madam,<br />" +
				"<br />" +
				"You are receiving this message from the following person:<br/>" +
				"<br />" +
				"Name: " + user.getFirstLastName() + "<br />" +
				"Email: " + user.getEmail() + "<br />" +
				"<br />" +
				"This is a request to confirm that the following survey is being created on behalf of your organisational entity: " + survey.getOrganisation() + "<br /><br />" +
				"<b>" + survey.getTitleSort() + "</b><br /><br />" +
				"To validate this survey, please click on this link: <a href='" + host + "home/validatesurvey?id=" + survey.getId() + "&code=" + survey.getValidationCode() + "'>validate the survey</a>.<br/><br/>" +
				"If you do not recognize this survey, please click on this link to reject it: <a href='" + host + "home/rejectsurvey?id=" + survey.getId() + "&code=" + survey.getValidationCode() + "'>reject</a>.<br/><br/>" +
				"Thank you and regards<br />";

		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]", host);

		mailService.SendHtmlMail(survey.getValidator(), sender, sender,
				"Validation request " + survey.getShortname(), text, null);
	}

	@Transactional
	public boolean validate(int surveyId, String code) {
		Survey survey = surveyService.getSurvey(surveyId);
		
		if (survey != null && survey.getValidationCode() != null && survey.getValidationCode().length() > 0 && survey.getValidationCode().equals(code)) {
			survey.setValidated(true);
			return true;
		}
		
		return false;
	}

	@Transactional
	public boolean rejectsurvey(int surveyId, String code) throws IOException, MessageException {
		Survey survey = surveyService.getSurvey(surveyId);
		
		if (survey != null && survey.getValidationCode() != null && survey.getValidationCode().length() > 0 && survey.getValidationCode().equals(code)) {
			survey.setValidated(false);
			
			// send email to owner
			String body = "Dear Sir or Madam,<br /><br />" +
					"Your request to confirm that the following survey is being created on behalf of your organisational entity was rejected by " + survey.getValidator() + ".<br /><br />" +
					"<b>" + survey.getTitleSort() + "</b><br /><br />";

			InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
			String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]", host);

			mailService.SendHtmlMail(survey.getOwner().getEmail(), sender, sender,
					"Validation request " + survey.getShortname(), text, null);
			
			return true;
		}
		
		return false;
	}

	@Transactional
	public List<Survey> getSurveysMarkedArchived(int limit, int skip) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "FROM Survey s WHERE s.isDraft = true AND s.archived = true and s.isDeleted = false ORDER BY s.id";

		Query<Survey> query = session.createQuery(sql, Survey.class);
		List<Survey> surveys = query.setReadOnly(true).setFirstResult(skip).setMaxResults(limit).list();
		return surveys;
	}
	
	@Transactional
	public List<Survey> getSurveysToBeMarkedArchived() {
		Session session = sessionFactory.getCurrentSession();
		String sql = "FROM Survey s WHERE s.isDraft = true AND s.archived = false and s.isDeleted = false and s.created < :created and s.updated < :updated";
		
		String olderThanMonths = settingsService.get(Setting.ArchiveOlderThan);
		String notChangedInMonths = settingsService.get(Setting.ArchiveNotChangedInLast);
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -1 * Integer.parseInt(olderThanMonths));
		Date created = c.getTime();
		
		c.setTime(new Date());
		c.add(Calendar.MONTH, -1 * Integer.parseInt(notChangedInMonths));
		Date updated = c.getTime();

		Query<Survey> query = session.createQuery(sql, Survey.class).setParameter("created", created).setParameter("updated", updated);
		List<Survey> allSurveys = query.setReadOnly(true).list();
		List<Survey> surveys = new ArrayList<>();
		
		for (Survey survey: allSurveys) {
			Date newestAnswerDate = answerService.getNewestAnswerDate(survey.getId());
			if (newestAnswerDate == null || newestAnswerDate.before(updated)) {
				initializeSurvey(survey);
				surveys.add(survey);
			}
		}
		
		return surveys;
	}

}
