package com.ec.survey.service;

import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.exception.MessageException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.ReportingService.ToDo;
import com.ec.survey.tools.*;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
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
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
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

	@Transactional(readOnly = true)
	public int getNumberPublishedAnswersFromMaterializedView(String uid) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT npa.PUBLISHEDANSWERS FROM MV_SURVEYS_NUMBERPUBLISHEDANSWERS npa WHERE npa.SURVEYUID = :uid";
		SQLQuery query = session.createSQLQuery(sql);
		query.setString("uid", uid);
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
		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		return query.setFirstResult(sqlPagination.getFirstResult()).setMaxResults(sqlPagination.getMaxResult()).list();
	}

	@Transactional(readOnly = true)
	public List<Survey> getSurveys(SurveyFilter filter, SqlPagination sqlPagination) throws Exception {
		StringBuilder stringBuilder = new StringBuilder(512);
		stringBuilder.append("SELECT s.SURVEY_ID"); // 0
		stringBuilder.append(" ,s.SURVEY_UID");// 1
		stringBuilder.append(" ,s.SURVEYNAME");// 2
		stringBuilder.append(" ,s.TITLE");// 3
		stringBuilder.append(" ,s.SURVEY_CREATED");// 4
		stringBuilder.append(" ,s.SURVEY_END_DATE");// 5
		stringBuilder.append(" ,s.SURVEY_START_DATE");// 6
		stringBuilder.append(" ,s.ISPUBLISHED");// 7
		stringBuilder.append(" ,s.LANGUAGE");
		if (!this.isReportingDatabaseEnabled()) {
			stringBuilder.append(" ,npa.PUBLISHEDANSWERS as replies");
		}
		stringBuilder.append(" ,s.ACTIVE");
		stringBuilder.append(" ,s.OWNER");
		stringBuilder.append(" ,s.CONTACT");
		stringBuilder.append(" ,(SELECT USER_LOGIN FROM USERS u WHERE u.USER_ID = s.OWNER) as ownerlogin");
		stringBuilder.append(" ,(SELECT USER_DISPLAYNAME FROM USERS u WHERE u.USER_ID = s.OWNER) as ownername");
		stringBuilder.append(" ,s.AUTOMATICPUBLISHING");
		stringBuilder.append(" ,s.CONTACTLABEL");
		stringBuilder.append(" ,s.SURVEYSECURITY");
		stringBuilder.append(" ,s.QUIZ");
		stringBuilder.append(" ,s.OPC");
		stringBuilder.append(" ,s.HASPENDINGCHANGES");
		stringBuilder.append(" from SURVEYS s");
		stringBuilder.append(" LEFT JOIN MV_SURVEYS_NUMBERPUBLISHEDANSWERS npa on s.SURVEY_UID = npa.SURVEYUID");
		stringBuilder.append(
				" where s.ISDRAFT = 1 and (s.ARCHIVED = 0 or s.ARCHIVED is null) and (s.DELETED = 0 or s.DELETED is null)");

		String sql = stringBuilder.toString();
		HashMap<String, Object> parameters = new HashMap<>();
		sql += getSql(filter, parameters, false);

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

			if (this.isReportingDatabaseEnabled()) {
				survey.setNumberOfAnswerSetsPublished(this.reportingService.getCount(false, survey.getUniqueId()));
			} else {
				survey.setNumberOfAnswerSetsPublished(ConversionTools.getValue(row[rowIndex++]));// 9
			}

			survey.setIsActive((Boolean) row[rowIndex++]);// 9 or 10

			User user = new User();
			user.setId(ConversionTools.getValue(row[rowIndex++]));// 10 or 11
			survey.setContact((String) row[rowIndex++]);// 11 or 12

			user.setLogin((String) row[rowIndex++]);// 12 or 13
			user.setDisplayName((String) row[rowIndex++]);// 13 or 14
			survey.setOwner(user);

			survey.setAutomaticPublishing((Boolean) row[rowIndex++]);// 14 or 15
			survey.setContactLabel((String) row[rowIndex++]);// 15 or 16

			survey.setSecurity((String) row[rowIndex++]);// 16 or 17
			survey.setIsQuiz((Boolean) row[rowIndex++]);// 17 or 18
			survey.setIsOPC((Boolean) row[rowIndex++]);// 18 or 19

			survey.setHasPendingChanges((Boolean) row[rowIndex]);// 19 or 20

			surveys.add(survey);
		}
		return surveys;
	}

	@Transactional(readOnly = true)
	public List<Survey> getSurveysIncludingTranslationLanguages(SurveyFilter filter, SqlPagination sqlPagination,
			boolean addInvitedAndDrafts, boolean addNumberOfReports) throws Exception {
		List<Survey> surveys = getSurveys(filter, sqlPagination);
		for (Survey survey : surveys) {
			survey.setTranslations(translationService.getTranslationLanguagesForSurvey(survey.getId(), false));
			survey.setCompleteTranslations(this.getCompletedTranslations(survey));

			if (addInvitedAndDrafts) {
				survey.setNumberOfInvitations(participationService.getNumberOfInvitations(survey.getUniqueId()));
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
		SQLQuery query = session.createSQLQuery("Select a.SURVEY FROM SURACCESS a WHERE a.ACCESS_USER = :id");

		@SuppressWarnings("rawtypes")
		List surveys = query.setInteger("id", userid).list();
		List<Integer> result = new ArrayList<>();

		for (Object o : surveys) {
			result.add(ConversionTools.getValue(o));
		}

		return result;
	}

	private List<String> getCompletedTranslations(Survey survey) {
		return translationService.getTranslationsForSurvey(survey.getId(), false).stream()
				.filter(Translations::getActive).map(t -> t.getLanguage().getCode()).collect(toList());
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
		stringBuilder.append(
				", (SELECT COUNT(DISTINCT SURABUSE_ID) FROM SURABUSE WHERE SURABUSE_SURVEY = s.SURVEY_UID) as reported");// 15
		stringBuilder.append(" from SURVEYS s");
		stringBuilder.append(" LEFT JOIN MV_SURVEYS_NUMBERPUBLISHEDANSWERS npa on s.SURVEY_UID = npa.SURVEYUID");
		stringBuilder.append(" where ");
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
		sql += getSql(filter, parameters, true);

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

	private String getSql(SurveyFilter filter, HashMap<String, Object> oQueryParameters, boolean loadpublicationdates) {

		StringBuilder sql = new StringBuilder();

		if (filter == null) {
			return sql.toString();
		}

		if (filter.getUid() != null && filter.getUid().length() > 0) {
			sql.append(" AND s.SURVEY_UID like :uid");
			oQueryParameters.put("uid", "%" + filter.getUid().trim() + "%");
		}

		if (filter.getShortname() != null && filter.getShortname().length() > 0) {
			sql.append(" AND s.SURVEYNAME COLLATE UTF8_GENERAL_CI like :shortname");
			oQueryParameters.put(Constants.SHORTNAME, "%" + filter.getShortname().trim() + "%");
		}

		if (filter.getTitle() != null && filter.getTitle().length() > 0) {
			sql.append(" AND s.TITLE COLLATE UTF8_GENERAL_CI like :title");
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

		
		if (filter.getOwner() != null && filter.getOwner().length() > 0 && filter.getUser() != null
				&& (filter.getUser().getLogin().equals(filter.getOwner())
						|| filter.getUser().getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2)) {
			// Searching by owner, checking if current user is owner or has global priviledge
			sql.append(
					" AND (s.OWNER in (SELECT USER_ID FROM USERS WHERE USER_LOGIN = :ownername OR USER_DISPLAYNAME = :ownername))");
			oQueryParameters.put("ownername", filter.getOwner());
		
		} else if (filter.getUser() == null) {
			// Searching public surveys
			sql.append(
					" AND s.LISTFORM = 1 AND s.LISTFORMVALIDATED = 1 AND s.ISPUBLISHED = true AND s.ACTIVE = true AND (s.SURVEYSECURITY = 'open' or s.SURVEYSECURITY = 'openanonymous') AND (s.SURVEY_END_DATE IS NULL OR s.SURVEY_END_DATE > :now)");
			oQueryParameters.put("now", new Date());
		} 
		
		if (filter.getUser() != null) {
			if (filter.getSelector() != null && filter.getSelector().equalsIgnoreCase("my")) {
				// Overriding owner with current user
				sql.append(" AND (s.OWNER = :ownerId)");
				oQueryParameters.put("ownerId", filter.getUser().getId());
			} else if (filter.getSelector() != null && filter.getSelector().equalsIgnoreCase("shared")) {
				// Overriding condition by sharing condition
				if (filter.getUser().getType().equalsIgnoreCase("ECAS")) {
					sql.append(
							" AND (s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE (a.ACCESS_USER = :ownerId OR a.ACCESS_DEPARTMENT IN (SELECT GRPS FROM ECASGROUPS WHERE eg_ID = (SELECT USER_ID FROM ECASUSERS WHERE USER_LOGIN = :login))) AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%')))");
					oQueryParameters.put("ownerId", filter.getUser().getId());
					oQueryParameters.put("login", filter.getUser().getLogin());
				} else {
					sql.append(
							" AND (s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE a.ACCESS_USER = :ownerId AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%')))");
					oQueryParameters.put("ownerId", filter.getUser().getId());
				}
			} else if (filter.getUser().getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2) {
				// Searching for the last case, assuming selector is "all"
				if (filter.getUser().getType().equalsIgnoreCase("ECAS")) {
					sql.append(
							" AND (s.OWNER = :ownerId OR s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE (a.ACCESS_USER = :ownerId OR a.ACCESS_DEPARTMENT IN (SELECT GRPS FROM ECASGROUPS WHERE eg_ID = (SELECT USER_ID FROM ECASUSERS WHERE USER_LOGIN = :login))) AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%')))");
					oQueryParameters.put("ownerId", filter.getUser().getId());
					oQueryParameters.put("login", filter.getUser().getLogin());
				} else {
					sql.append(
							" AND (s.OWNER = :ownerId OR s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE a.ACCESS_USER = :ownerId AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%')))");
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

					sql.append(" ( s.SURVEYNAME COLLATE UTF8_GENERAL_CI like :").append(w)
							.append(" OR s.TITLE COLLATE UTF8_GENERAL_CI like :").append(w).append(")");
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
		}

		if (filter.getMinContributions() != null) {
			sql.append(" AND (npa.PUBLISHEDANSWERS > :replies)");
			oQueryParameters.put("replies", filter.getMinContributions());
		}

		if (loadpublicationdates) {
			sql.append(" GROUP BY s.SURVEY_UID");

			boolean having = false;
			if (filter.getPublishedFrom() != null) {
				sql.append(
						" HAVING STR_TO_DATE(SUBSTRING(GROUP_CONCAT(s.survey_created),-19), '%Y-%m-%d') >= :publishedFrom");
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
				sql.append("STR_TO_DATE(SUBSTRING(GROUP_CONCAT(s.survey_created),-19), '%Y-%m-%d') <= :publishedTo");
				oQueryParameters.put("publishedTo", filter.getPublishedTo());
			}

			if (filter.getFirstPublishedFrom() != null) {
				if (having) {
					sql.append(" AND ");
				} else {
					sql.append(" HAVING ");
					having = true;
				}
				sql.append(
						"STR_TO_DATE(SUBSTRING(GROUP_CONCAT(s.survey_created),21, 19), '%Y-%m-%d') >= :firstPublishedFrom");
				oQueryParameters.put("firstPublishedFrom", filter.getFirstPublishedFrom());
			}

			if (filter.getFirstPublishedTo() != null) {
				if (having) {
					sql.append(" AND ");
				} else {
					sql.append(" HAVING ");
				}
				sql.append(
						"STR_TO_DATE(SUBSTRING(GROUP_CONCAT(s.survey_created),21, 19), '%Y-%m-%d') <= :firstPublishedTo");
				oQueryParameters.put("firstPublishedTo", filter.getFirstPublishedTo());
			}
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
		return (Survey) session.get(Survey.class, id);
	}

	@Transactional
	public Survey getSurvey(int id, boolean readonly) {
		Session session = sessionFactory.getCurrentSession();
		Survey survey = (Survey) session.get(Survey.class, id);

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
		Session session = sessionFactory.getCurrentSession();
		Survey survey = (Survey) session.get(Survey.class, id);

		if (survey != null && (survey.getIsDraft() || loadTranslations)) {
			List<String> translations = translationService.getTranslationLanguagesForSurvey(survey.getId());
			survey.setTranslations(translations);
		}
		if (survey != null && synchronizeSurvey) {
			synchronizeSurvey(survey, survey.getLanguage().getCode(), setSurvey);

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
		Query query = session
				.createQuery(
						"SELECT id FROM Survey s WHERE s.uniqueId = :uid AND s.isDraft = :draft ORDER BY s.id DESC")
				.setString("uid", uid);
		query.setBoolean("draft", true);

		@SuppressWarnings("unchecked")
		List<Survey> list = query.setReadOnly(true).setMaxResults(1).list();
		if (!list.isEmpty()) {
			return getSurvey(ConversionTools.getValue(list.get(0)));
		}
		return null;
	}

	@Transactional(readOnly = true)
	public Survey getSurveyByUniqueId(String uid, boolean loadTranslations, boolean draft) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery(
						"SELECT id FROM Survey s WHERE s.uniqueId = :uid AND s.isDraft = :draft ORDER BY s.id DESC")
				.setString("uid", uid);
		query.setBoolean("draft", draft);

		@SuppressWarnings("unchecked")
		List<Survey> list = query.setReadOnly(true).setMaxResults(1).list();
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
			}
		}
	}

	@Transactional(readOnly = true)
	public Survey getSurvey(int id, String language) {
		Survey survey = getSurvey(id, false, true, false, true);
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

		Query query = session.createQuery(sql).setString("uid", uidorshortname).setBoolean("draft", isDraft)
				.setReadOnly(true);

		int id = ConversionTools.getValue(query.uniqueResult());

		if (id > 0) {

			Survey survey = (Survey) session.get(Survey.class, id);

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

			if (checkActive && !isDraft) {
				query = session.createQuery(sql).setString("uid", uidorshortname).setBoolean("draft", true);
				id = ConversionTools.getValue(query.uniqueResult());

				if (id == 0)
					return null;

				Survey draft = (Survey) session.get(Survey.class, id);
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
		Survey result = (Survey) session.get(Survey.class, id);

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
	 * Creates a copy of the survey and save it as non-draft survey
	 */
	@Transactional
	public Survey publish(Survey draftSurvey, int pnumberOfAnswerSets, int pnumberOfAnswerSetsPublished,
			boolean deactivateAutoPublishing, int userId, boolean resetSourceIds, boolean resetSurvey)
			throws Exception {
		Session session = sessionFactory.getCurrentSession();
		boolean alreadyPublished = draftSurvey.getIsPublished();

		if (resetSurvey) {
			session.evict(draftSurvey);
			draftSurvey = (Survey) session.merge(draftSurvey);
		}

		Survey publishedSurvey = draftSurvey.copy(this, draftSurvey.getOwner(), fileDir, true, pnumberOfAnswerSets,
				pnumberOfAnswerSetsPublished, true, resetSourceIds, true, null, null);
		publishedSurvey.setIsDraft(false); // this means it is not a draft
		if (deactivateAutoPublishing) {
			publishedSurvey.setAutomaticPublishing(false);
		}
		publishedSurvey = update(publishedSurvey, false, true, false, userId);

		this.computeTrustScore(draftSurvey);

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
		}
		List<Translations> draftTranslationsList = translationService.getTranslationsForSurvey(draftSurvey.getId(), true);
		for (Translations draftTranslations : draftTranslationsList) {
			if (!draftTranslations.getLanguage().getCode().equalsIgnoreCase(draftSurvey.getLanguage().getCode())) {
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

		Survey ob = null;
		ob = (Survey) session.get(Survey.class, draftSurvey.getId());

		ob.setIsPublished(true);
		ob.setHasPendingChanges(false);

		if (deactivateAutoPublishing) {
			ob.setAutomaticPublishing(false);
			draftSurvey.setAutomaticPublishing(false);
		}

		draftSurvey.setHasPendingChanges(false);

		session.update(ob);
		draftSurvey.setIsPublished(true);

		if (!alreadyPublished)
			reportingService.addToDo(ToDo.NEWSURVEY, draftSurvey.getUniqueId(), null);

		return publishedSurvey;
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
		Survey ob = null;
		ob = (Survey) session.get(Survey.class, survey.getId());
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
		session.update(newDraft);

		AdaptIDs(survey, newDraft, true);

		// delete original
		deleteSurveyData(survey.getId(), false, false, survey.getUniqueId(), false);
		session.delete(survey);

		return newDraft;
	}

	@Transactional
	public void unpublish(Survey survey, boolean deactivateAutoPublishing, int userId) {
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

		Survey ob = null;
		ob = (Survey) session.get(Survey.class, survey.getId());
		session.setReadOnly(ob, false);

		ob.setIsActive(false);

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
		}
	}

	public void updateAnswerUids(int id, String uid, boolean pa) throws InterruptedException {
		boolean saved = false;

		int counter = 1;

		while (!saved) {
			try {
				internalUpdateAnswerUids(id, uid, pa);
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

	private void internalUpdateAnswerUids(int id, String uid, boolean pa) {
		Session session = sessionFactory.getCurrentSession();
		Query query;

		if (pa) {
			query = session.createSQLQuery("UPDATE ANSWERS SET PA_UID = :uid WHERE PA_UID IS NULL AND PA_ID = :id");
		} else {
			query = session.createSQLQuery(
					"UPDATE ANSWERS SET QUESTION_UID = :uid WHERE QUESTION_UID IS NULL AND QUESTION_ID = :id");
		}
		query.setInteger("id", id).setString("uid", uid);
		query.executeUpdate();
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
		List<ResultFilter> publishedSurveyFilters = sessionService.getAllResultFilter(publishedSurvey.getId());
		if (publishedSurveyFilters != null) {
			for (ResultFilter publishedSurveyFilter : publishedSurveyFilters) {
				// do not copy filters of exports
				Export export = exportService.getExportByResultFilterID(publishedSurveyFilter.getId());
				if (export == null) {
					ResultFilter newPublishedSurveyFilter = publishedSurveyFilter.copy();

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

					newPublishedSurveyFilter.setSurveyId(newPublishedSurvey.getId());
					if (publishedSurveyFilter.getUserId() != null) {
						sessionService.internalSetLastResultFilter(newPublishedSurveyFilter, publishedSurveyFilter.getUserId(),
								newPublishedSurveyFilter.getSurveyId());
					}
				}
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
		Survey survey = (Survey) session.get(Survey.class, id);
		session.setReadOnly(survey, false);
		survey.setHasPendingChanges(true);
		session.update(survey);
	}

	@Transactional(readOnly = false)
	public void makeClean(int id) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("UPDATE Survey s SET s.hasPendingChanges = false WHERE s.id = :id");
		query.setInteger("id", id);
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

		Survey survey = SurveyHelper.parseSurvey(request, this, fileService, servletContext,
				activityService.isEnabled(217), activityService.isEnabled(220), fileIDsByUID);

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
			Survey existing = (Survey) session.get(Survey.class, survey.getId());
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
		result[0] = false;
		result[1] = false;

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

			activityService.logTranslations(227, translations.getLanguage().getCode(), oldInfo, translations.getInfo(),
					userId, survey.getUniqueId());
		}
	}

	private void deleteSurveyData(int id, boolean deleteAnswers, boolean deleteAccesses, String uid,
			boolean deleteLogs) throws IOException {
		Session session = sessionFactory.getCurrentSession();

		if (deleteAnswers) {
			Query query0 = session.createSQLQuery(
					"SELECT fi.FILE_UID from FILES fi JOIN ANSWERS_FILES f ON fi.FILE_ID = f.files_FILE_ID JOIN ANSWERS a ON f.ANSWERS_ANSWER_ID = a.ANSWER_ID JOIN ANSWERS_SET an ON a.AS_ID = an.ANSWER_SET_ID WHERE an.SURVEY_ID = :id");
			@SuppressWarnings("unchecked")
			List<String> fileuids = query0.setInteger("id", id).list();

			Query query = session.createSQLQuery(
					"DELETE d.* from DRAFTS d INNER JOIN ANSWERS_SET an ON d.answerSet_ANSWER_SET_ID = an.ANSWER_SET_ID where an.SURVEY_ID = :id");
			query.setInteger("id", id).executeUpdate();

			Query query1 = session.createSQLQuery(
					"DELETE f.* from ANSWERS_FILES f JOIN ANSWERS a ON f.ANSWERS_ANSWER_ID = a.ANSWER_ID JOIN ANSWERS_SET an ON a.AS_ID = an.ANSWER_SET_ID WHERE an.SURVEY_ID = :id");
			query1.setInteger("id", id).executeUpdate();

			Query query2 = session.createSQLQuery(
					"DELETE a.* from ANSWERS a JOIN ANSWERS_SET an ON a.AS_ID = an.ANSWER_SET_ID WHERE an.SURVEY_ID = :id");
			query2.setInteger("id", id).executeUpdate();

			Query query3 = session.createQuery("DELETE from AnswerSet a where a.surveyId = :id");
			query3.setInteger("id", id).executeUpdate();

			for (String fileuid : fileuids) {
				java.io.File file = fileService.getSurveyFile(uid, fileuid);
				if (file.exists()) {
					file.delete();
				}
			}

			Query query4 = session
					.createSQLQuery("SELECT v.VALIDCODE_CODE FROM VALIDCODE v WHERE v.VALIDCODE_SURVEYUID = :uid");
			@SuppressWarnings("unchecked")
			List<String> codes = query4.setString("uid", uid).list();
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

			Query query5 = session.createSQLQuery("DELETE FROM VALIDCODE WHERE VALIDCODE_SURVEYUID = :uid");
			query5.setString("uid", uid).executeUpdate();
		}

		List<Translations> translations = translationService.getTranslationsForSurvey(id, false);
		for (Translations translation : translations) {
			if (translation.getId() > 0)
				session.delete(translation);
		}

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
			Query query6 = session.createSQLQuery("DELETE FROM INVITATIONS WHERE PARTICIPATIONGROUP_ID = :id");
			query6.setInteger("id", group.getId()).executeUpdate();

			session.delete(group);
		}
	}

	@Transactional
	public void delete(int id, boolean deleteLogs, boolean deleteFileMappings) throws Exception {
		deleteNoTransaction(id, deleteLogs, deleteFileMappings);
	}

	public void deleteNoTransaction(int id, boolean deleteLogs, boolean deleteFileMappings) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		Survey s = this.getSurvey(id, false, false);

		List<Integer> surveyIDs = surveyService.getAllSurveyVersions(s.getShortname(), s.getUniqueId());
		fileService.deleteFilesForSurveys(surveyIDs);

		if (contextpath == null || contextpath.trim().length() == 0) {
			throw new MessageException("contextpath empty");
		}

		if (deleteFileMappings) {
			deleteFileMappings(surveyIDs);
		}

		// delete draft
		Map<String, Integer> referencedFiles = s.getReferencedFileUIDs(contextpath);
		deleteSurveyData(id, true, true, s.getUniqueId(), deleteLogs);
		session.flush();
		session.delete(s);
		session.flush();

		for (Entry<String, Integer> entry : referencedFiles.entrySet()) {
			if (entry.getValue() == null) {
				// delete files belonging to images and background documents
				fileService.deleteIfNotReferenced(entry.getKey(), s.getUniqueId());
			}
		}

		// delete published versions
		Survey published = getSurveyByUniqueId(s.getUniqueId(), false, false);
		if (published != null) {
			surveyIDs = this.getAllSurveyVersions(published.getId());
			for (Integer sid : surveyIDs) {
				s = this.getSurvey(sid, false, false);
				referencedFiles = s.getReferencedFileUIDs(contextpath);
				deleteSurveyData(s.getId(), true, true, s.getUniqueId(), deleteLogs);
				session.flush();
				session.delete(s);
				session.flush();
				for (Entry<String, Integer> entry : referencedFiles.entrySet()) {
					if (entry.getValue() == null) {
						// delete files belonging to images and background documents
						fileService.deleteIfNotReferenced(entry.getKey(), s.getUniqueId());
					}
				}
			}
		}

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
			Query query = session.createSQLQuery(sql);
			query.setInteger("id", id);
			int deleted = query.executeUpdate();
			logger.info("deleted " + deleted + " file mappings for survey " + id);
		}
	}

	@Transactional(readOnly = true)
	public List<Language> getLanguages() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Language l order by l.code asc");
		@SuppressWarnings("unchecked")
		List<Language> languages = query.list();
		return languages;
	}

	@Transactional(readOnly = true)
	public Language getLanguage(String code) {
		return getLanguage(code, sessionFactory.getCurrentSession());
	}

	public Language getLanguage(String code, Session session) {
		Query query = session.createQuery("FROM Language l WHERE l.code = :code").setString("code", code);

		@SuppressWarnings("rawtypes")
		List result = query.list();

		if (result.isEmpty()) {
			logger.error("ivalid language code: " + code);
			return null;
		}

		return (Language) result.get(0);
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
		Query query = session.createQuery("Select l.code FROM Language l");
		@SuppressWarnings("unchecked")
		List<String> codes = query.list();
		return codes;
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

	@Transactional
	public int importSurvey(ImportResult result, User user, boolean isRestore) throws Exception {
		Survey copy = result.getSurvey().copy(this, user, fileDir, false, -1, -1, false, false, true, null, null);

		if (isRestore && copy.getIsOPC()) {
			setBrpAccess(copy);
		}

		Survey copyActive = null;
		try {
			Map<String, String> missingfiles = new HashMap<>();
			Map<String, String> convertedUIDs = surveyService.copyFiles(copy, new HashMap<>(), true, missingfiles,
					result.getSurvey().getUniqueId());

			Map<String, String> oldToNewUniqueIds = importDraftSurvey(copy, result, user);
			importSurveyData(result, true, copy, oldToNewUniqueIds, result.getSurvey().getId());

			if (result.getActiveSurvey() != null && result.getActiveAnswerSets() != null
					&& !result.getActiveAnswerSets().isEmpty()) {
				Map<Integer, Integer> oldSourceIdsById = getSourceIdsById(result.getActiveSurvey());
				for (int id : result.getOldSurveys().keySet()) {
					Survey copyOld = importOldPublishedSurvey(result.getOldSurveys().get(id), user, oldToNewUniqueIds);
					importSurveyData(result, false, copyOld, oldToNewUniqueIds, id);
				}
				copyActive = result.getActiveSurvey().copy(this, user, fileDir, false, -1, -1, false, false, true, null,
						null);
				surveyService.copyFiles(copyActive, convertedUIDs, true, missingfiles,
						result.getSurvey().getUniqueId());

				importPublishedSurvey(copyActive, result, user, oldToNewUniqueIds, oldSourceIdsById);
				importSurveyData(result, false, copyActive, oldToNewUniqueIds, result.getActiveSurvey().getId());
			}

			for (String fileuid : convertedUIDs.keySet()) {
				fileService.deleteIfNotReferenced(fileuid, copy.getUniqueId());
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
			Map<String, String> oldToNewUniqueIds, Integer surveyid) throws Exception {
		List<Translations> translations = null;
		List<AnswerSet> answerSets = new ArrayList<>();
		Map<Integer, List<File>> files = null;

		if (draft) {
			translations = result.getTranslations();
			answerSets = result.getAnswerSets();
			files = result.getFiles();
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
		}

		if (translations != null) {
			copyTranslations(translations, survey, oldToNewUniqueIds, result, true);
		}

		if (answerSets != null && !answerSets.isEmpty()) {
			logger.info("starting import of answers");

			Map<String, Integer> keys = new HashMap<>();
			for (Element element : survey.getElementsRecursive()) {
				keys.put(element.getSourceId().toString(), element.getId());
				if (element instanceof ChoiceQuestion) {
					for (PossibleAnswer answer : ((ChoiceQuestion) element).getPossibleAnswers()) {
						if (answer.getSourceId() != null) {
							keys.put(answer.getSourceId().toString(), answer.getId());
						}
					}
				}
			}

			Set<AnswerSet> answerSets2 = new HashSet<>();

			while (!answerSets.isEmpty()) {
				AnswerSet a = answerSets.remove(0);

				AnswerSet b = a.copy(survey, files);

				for (Answer an : b.getAnswers()) {
					if (keys.containsKey(an.getQuestionId().toString())) {
						an.setQuestionId(keys.get(an.getQuestionId().toString()));
					}

					an.setQuestionUniqueId(oldToNewUniqueIds.get(an.getQuestionUniqueId()));

					if (an.getPossibleAnswerId() > 0) {
						if (keys.containsKey(an.getPossibleAnswerId().toString())) {
							an.setValue(keys.get(an.getPossibleAnswerId().toString()).toString());
							an.setPossibleAnswerId(keys.get(an.getPossibleAnswerId().toString()));
						}

						an.setPossibleAnswerUniqueId(oldToNewUniqueIds.get(an.getPossibleAnswerUniqueId()));
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
			ImportResult result, boolean newTitle) {
		Map<Integer, Element> elementsBySourceId = survey.getElementsBySourceId();

		for (Translations tOriginal : translations) {
			Translations t = new Translations();
			t.setActive(tOriginal.getActive());
			t.setLanguage(tOriginal.getLanguage());
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
						tr.setKey(translateKey(trOriginal.getKey(), elementsBySourceId, oldToNewUniqueIds));
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
			Map<String, String> oldToNewUniqueIds) {

		if (key == null)
			return key;

		if (key.equalsIgnoreCase(Survey.TITLE))
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
			} else if (oldToNewUniqueIds.containsKey(key)) {
				return oldToNewUniqueIds.get(key);
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

		return translateKey(key2, elementsBySourceId, oldToNewUniqueIds);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Access> getAccesses(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Access a WHERE a.survey.id = :id").setInteger("id", id);
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
		return (Access) session.get(Access.class, id);
	}

	@Transactional
	public void deleteAccess(Access access) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(access);
	}

	@Transactional(readOnly = true)
	public Access getAccess(Integer id, Integer userId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Access a WHERE a.survey.id = :id AND a.user.id = :userId")
				.setInteger("id", id).setInteger("userId", userId);
		@SuppressWarnings("unchecked")
		List<Access> result = query.list();
		if (!result.isEmpty())
			return result.get(0);
		return null;
	}

	@Transactional(readOnly = true)
	public Access getGroupAccess(Integer id, String groupName) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Access a WHERE a.survey.id = :id AND a.department = :groupName")
				.setInteger("id", id).setString("groupName", groupName);
		@SuppressWarnings("unchecked")
		List<Access> result = query.list();
		if (!result.isEmpty())
			return result.get(0);
		return null;
	}

	@Transactional
	public void save(Template template) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(template);
	}

	@Transactional(readOnly = true)
	public Template getTemplate(int id) {
		Session session = sessionFactory.getCurrentSession();
		return (Template) session.get(Template.class, id);
	}

	@Transactional
	public void deleteTemplate(Template template) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(template);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Template> getTemplates(Integer ownerId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Template t WHERE t.owner.id = :id").setInteger("id", ownerId);
		return query.list();
	}

	@Transactional
	public void save(Language objLang) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(objLang);
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Survey> getSurveysToStart() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(
				"FROM Survey s WHERE s.isDraft = true AND s.start <= :start AND ((s.end is not null AND s.end > :start) OR (s.end is null)) AND s.automaticPublishing = true AND s.isActive = false")
				.setTimestamp("start", new Date());
		return query.list();
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Survey> getSurveysToStop() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(
				"FROM Survey s WHERE s.isDraft = true AND s.end <= :end AND s.automaticPublishing = true AND s.isPublished = true AND s.isActive = true")
				.setTimestamp("end", new Date());
		return query.list();
	}

	@Transactional(readOnly = true)
	public List<Survey> getSurveysToNotify() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(
				"FROM Survey s WHERE s.isDraft = true AND s.notified = false AND s.automaticPublishing = true AND s.end != null AND s.notificationValue != null AND s.notificationUnit != null AND s.isActive = true");
		@SuppressWarnings("unchecked")
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
			if (draftSurvey.getContact() != null && !draftSurvey.getContact().equals(publishedSurvey.getContact()))
				hasPendingChanges = true;
			if (draftSurvey.getMultiPaging() != publishedSurvey.getMultiPaging())
				hasPendingChanges = true;
			if (draftSurvey.getValidatedPerPage() != publishedSurvey.getValidatedPerPage())
				hasPendingChanges = true;
			if (!Objects.equals(draftSurvey.getWcagCompliance(), publishedSurvey.getWcagCompliance()))
				hasPendingChanges = true;
			if (!Tools.isFileEqual(draftSurvey.getLogo(), publishedSurvey.getLogo()))
				hasPendingChanges = true;
			if (!draftSurvey.getLogoInInfo().equals(publishedSurvey.getLogoInInfo()))
				hasPendingChanges = true;
			if (!Tools.isEqual(draftSurvey.getSkin(), publishedSurvey.getSkin()))
				hasPendingChanges = true;
			if (draftSurvey.getSectionNumbering() != publishedSurvey.getSectionNumbering())
				hasPendingChanges = true;
			if (draftSurvey.getQuestionNumbering() != publishedSurvey.getQuestionNumbering())
				hasPendingChanges = true;
			if (!Tools.isEqual(draftSurvey.getConfirmationPage(), publishedSurvey.getConfirmationPage()))
				hasPendingChanges = true;
			if (!Tools.isEqual(draftSurvey.getConfirmationPageLink(), publishedSurvey.getConfirmationPageLink()))
				hasPendingChanges = true;
			if (!Tools.isEqual(draftSurvey.getConfirmationLink(), publishedSurvey.getConfirmationLink()))
				hasPendingChanges = true;

			if (!Tools.isEqual(draftSurvey.getIsQuiz(), publishedSurvey.getIsQuiz()))
				hasPendingChanges = true;
			if (!Tools.isEqualIgnoreEmptyString(draftSurvey.getQuizWelcomeMessage(), publishedSurvey.getQuizWelcomeMessage()))
				hasPendingChanges = true;
			if (!Tools.isEqualIgnoreEmptyString(draftSurvey.getQuizResultsMessage(), publishedSurvey.getQuizResultsMessage()))
				hasPendingChanges = true;

			if (!Tools.isEqual(draftSurvey.getShowTotalScore(), publishedSurvey.getShowTotalScore()))
				hasPendingChanges = true;
			if (!Tools.isEqual(draftSurvey.getScoresByQuestion(), publishedSurvey.getScoresByQuestion()))
				hasPendingChanges = true;

			if (!Tools.isEqual(draftSurvey.getShowQuizIcons(), publishedSurvey.getShowQuizIcons()))
				hasPendingChanges = true;

			if (!Tools.isEqual(draftSurvey.getIsUseMaxNumberContribution(), publishedSurvey.getIsUseMaxNumberContribution()))
				hasPendingChanges = true;

			if (!Tools.isEqual(draftSurvey.getIsUseMaxNumberContributionLink(),
					publishedSurvey.getIsUseMaxNumberContributionLink()))
				hasPendingChanges = true;

			if (!Tools.isEqual(draftSurvey.getMaxNumberContributionText(), publishedSurvey.getMaxNumberContributionText()))
				hasPendingChanges = true;

			if (!Tools.isEqual(draftSurvey.getMaxNumberContributionLink(), publishedSurvey.getMaxNumberContributionLink()))
				hasPendingChanges = true;

			if (!Tools.isEqual(draftSurvey.getMaxNumberContribution(), publishedSurvey.getMaxNumberContribution()))
				hasPendingChanges = true;
			
			if (draftSurvey.getSendConfirmationEmail() != publishedSurvey.getSendConfirmationEmail())
				hasPendingChanges = true;

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
				if (hasPendingChanges)
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
		Query statusQuery = session.createQuery("FROM Status");
		@SuppressWarnings("unchecked")
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

		Query statusQuery = session.createQuery("FROM Status");
		@SuppressWarnings("unchecked")
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
				.setInteger("id", element.getId());
		query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public Element getElement(int id) {
		Session session = sessionFactory.getCurrentSession();
		return (Element) session.get(Element.class, id);
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
		SQLQuery query = session.createSQLQuery(sql);
		query.setString("uid", uniqueCode);
		query.setBoolean("draft", isDraft);

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
		Query query = session.createSQLQuery("SELECT childElements_ID FROM ELEMENTS WHERE ID = :id").setInteger("id",
				id);
		int parentid = ConversionTools.getValue(query.uniqueResult());

		// questions could have been deleted from the parent -> get newest version of
		// the parent
		if (parentid > 0) {
			query = session.createSQLQuery(
					"SELECT MAX(ID) FROM ELEMENTS WHERE ELEM_UID IN (SELECT ELEM_UID FROM ELEMENTS WHERE ID = :id)")
					.setInteger("id", parentid);
			parentid = ConversionTools.getValue(query.uniqueResult());
		}

		return parentid > 0 ? getElement(parentid) : null;
	}

	@Transactional(readOnly = true)
	public Element getNewestElementByUid(String uid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery("SELECT MAX(ID) FROM ELEMENTS WHERE ELEM_UID = :uid").setString("uid",
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
				.setString("surveyUid", surveyUid);

		return (List<Object>) query.list();
	}

	@Transactional(readOnly = true)
	public void checkAndRecreateMissingElements(Survey survey, ResultFilter filter) {
		List<Element> surveyelements = survey.getElementsRecursive(true);
		Map<String, Element> surveyelementsbyuid = new HashMap<>();
		Map<String, Element> missingelementuids = new HashMap<>();
		for (Element element : surveyelements) {
			surveyelementsbyuid.put(element.getUniqueId(), element);
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
							// check if matrix contains question
							if (!(parentRating).containsChild(missingquestion.getId())) {
								(parentRating).getMissingQuestions().add(missingquestion);
							}
						}
					}
				} else {
					survey.getMissingElements().add(missingquestion);
					missingelementuids.put(questionUID, missingquestion);
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
				}
			}
		}

		survey.setMissingElementsChecked(true);
	}

	@Transactional(readOnly = false)
	public void markAsArchived(String uid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("UPDATE Survey s SET s.archived = 1 WHERE s.uniqueId = :uid").setString("uid",
				uid);
		query.executeUpdate();
	}

	@Transactional(readOnly = false)
	public void unmarkAsArchived(String uid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("UPDATE Survey s SET s.archived = 0 WHERE s.uniqueId = :uid").setString("uid",
				uid);
		query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public List<Integer> getAllSurveyVersions(String shortname, String uid) {
		Session session = sessionFactory.getCurrentSession();
		Query query;

		if (shortname != null && shortname.length() > 0 && uid != null && uid.length() > 0) {
			query = session.createSQLQuery(
					"SELECT SURVEY_ID FROM SURVEYS WHERE SURVEYNAME LIKE :shortname AND SURVEY_UID LIKE :uid");
			query.setString(Constants.SHORTNAME, "%" + shortname + "%");
			query.setString("uid", "%" + uid + "%");
		} else if (shortname != null && shortname.length() > 0) {
			query = session.createSQLQuery("SELECT SURVEY_ID FROM SURVEYS WHERE SURVEYNAME LIKE :shortname");
			query.setString(Constants.SHORTNAME, "%" + shortname + "%");
		} else {
			query = session.createSQLQuery("SELECT SURVEY_ID FROM SURVEYS WHERE SURVEY_UID LIKE :uid");
			query.setString("uid", "%" + uid + "%");
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

				activityService.log(123, oldownerid.toString(), newowner.getId().toString(), currentuserid, surveyuid);

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
				.setInteger("id", surveyId);

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
				.setInteger("id", surveyId);

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
				.setString("uid", surveyUid);

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
		Query query = session
				.createQuery("SELECT s.language.code FROM Survey s WHERE s.uniqueId = :uid AND s.isDraft = true")
				.setString("uid", uid);
		String result = (String) query.uniqueResult();

		if (result == null) {
			query = session
					.createQuery("SELECT s.language.code FROM Survey s WHERE s.shortname = :name AND s.isDraft = true")
					.setString("name", shortname);
			result = (String) query.uniqueResult();
		}

		return result;
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

		Query query = session.createQuery(sql);

		if (filteralias != null && filteralias.length() > 0) {
			query.setString("alias", "%" + filteralias + "%");
		}

		if (filterowner != null && filterowner.length() > 0) {
			query.setString("owner", "%" + filterowner + "%");
		}

		DateTimeFormatter f = DateTimeFormat.forPattern("dd/MM/yyyy");

		if (filterrequestdatefrom != null && filterrequestdatefrom.length() > 0) {
			query.setDate("datefrom", DateTime.parse(filterrequestdatefrom, f).toDate());
		}

		if (filterrequestdateto != null && filterrequestdateto.length() > 0) {
			query.setDate("dateto", Tools.getFollowingDay(DateTime.parse(filterrequestdateto, f).toDate()));
		}

		@SuppressWarnings("unchecked")
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
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger("fileid", fileid);
		String result = (String) query.uniqueResult();

		if (result == null)
			return null;

		return getSurvey(result, true, false, false, false, null, true, false);
	}

	@Transactional(readOnly = true)
	public Survey getSurveyForQuestion(String uid) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT DISTINCT s.SURVEY_UID FROM SURVEYS s JOIN SURVEYS_ELEMENTS se ON se.SURVEYS_SURVEY_ID = s.SURVEY_ID JOIN ELEMENTS e ON e.ID = se.elements_ID WHERE e.ELEM_UID = :uid";
		SQLQuery query = session.createSQLQuery(sql);
		query.setString("uid", uid);
		String result = (String) query.uniqueResult();

		if (result == null)
			return null;

		return getSurvey(result, true, false, false, false, null, true, false);
	}

	@Transactional(readOnly = true)
	public String[] getSurveyForFile(File file, String contextpath, String surveyuid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT s.uniqueId, s.shortname FROM Survey s WHERE s.logo.id = :id");
		query.setInteger("id", file.getId());

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

		SQLQuery sqlquery = session.createSQLQuery(
				"SELECT e.type, s.SURVEY_UID, s.SURVEYNAME FROM ELEMENTS e JOIN SURVEYS_ELEMENTS se ON se.elements_ID = e.ID JOIN SURVEYS s ON s.SURVEY_ID = se.SURVEYS_SURVEY_ID JOIN ELEMENTS_FILES ef ON ef.ELEMENTS_ID = e.ID WHERE ef.files_FILE_ID = :id");
		sqlquery.setInteger("id", file.getId());

		data = sqlquery.setMaxResults(1).list();
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
			sqlquery = session.createSQLQuery(
					"SELECT e.type, s.SURVEY_UID, s.SURVEYNAME FROM ELEMENTS e JOIN SURVEYS_ELEMENTS se ON se.elements_ID = e.ID JOIN SURVEYS s ON s.SURVEY_ID = se.SURVEYS_SURVEY_ID WHERE URL = :url");
			sqlquery.setString("url", contextpath + "/files/" + file.getUid());
			data = sqlquery.setMaxResults(1).list();
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

			sqlquery = session.createSQLQuery(
					"SELECT e.type, s.SURVEY_UID, s.SURVEYNAME FROM ELEMENTS e JOIN SURVEYS_ELEMENTS se ON se.elements_ID = e.ID JOIN SURVEYS s ON s.SURVEY_ID = se.SURVEYS_SURVEY_ID WHERE URL LIKE :url");
			sqlquery.setString("url", "%/" + file.getUid());
			data = sqlquery.setMaxResults(1).list();
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
			sqlquery = session.createSQLQuery(
					"SELECT e.type, s.SURVEY_UID, s.SURVEYNAME FROM ELEMENTS e JOIN SURVEYS_ELEMENTS se ON se.elements_ID = e.ID JOIN SURVEYS s ON s.SURVEY_ID = se.SURVEYS_SURVEY_ID WHERE URL = :url");
			sqlquery.setString("url", contextpath + "/files/" + surveyuid + Constants.PATH_DELIMITER + file.getUid());
			data = sqlquery.setMaxResults(1).list();
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

		sqlquery = session.createSQLQuery(
				"SELECT s.SURVEY_UID, s.SURVEYNAME FROM SURVEYS s JOIN Survey_backgroundDocuments sb ON sb.Survey_SURVEY_ID = s.SURVEY_ID WHERE sb.BACKGROUNDDOCUMENTS LIKE :url");
		sqlquery.setString("url", "%" + file.getUid());
		data = sqlquery.setMaxResults(1).list();
		if (!data.isEmpty()) {
			Object[] values = (Object[]) data.get(0);
			if (values[0].toString().equalsIgnoreCase("IMAGE"))
			{
				String[] result = new String[3];
				result[0] = values[0].toString();
				result[1] = values[1].toString();
				result[2] = "background document";
				return result;
			}
		}

		return null;
	}

	public java.io.File exportSurvey(String shortname, SurveyService surveyService, boolean answers) {
		return SurveyExportHelper.exportSurvey(shortname, surveyService, answers, translationService, answerService,
				fileDir, sessionService, fileService, sessionFactory.getCurrentSession(), host);
	}

	@Transactional(readOnly = true)
	public List<Element> getElements(String[] ids) {
		Session session = sessionFactory.getCurrentSession();

		List<Element> result = new ArrayList<>();
		for (String id : ids) {
			Element e = (Element) session.get(Element.class, Integer.parseInt(id));
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
			Set<String> labels = survey.getBackgroundDocuments().keySet();
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
		Query query = session
				.createQuery("UPDATE Survey s SET s.isDeleted = true, s.deleted = NOW() WHERE s.uniqueId = :uniqueId");
		query.setParameter("uniqueId", uniqueId);
		query.executeUpdate();

		activityService.log(104, shortname, null, userid, uniqueId);

		if (uniqueId != null) {
			int answers = answerService.getNumberOfAnswerSetsPublished(shortname, uniqueId);
			String s = shortname + " (" + (published ? "published" : "draft") + ") " + answers;
			activityService.log(1001, s, null, userid, null);
		}
	}

	@Transactional
	public void unmarkDeleted(int id, String alias) {
		Session session = sessionFactory.getCurrentSession();
		Survey survey = (Survey) session.get(Survey.class, id);

		survey.setIsDeleted(false);

		if (alias != null && alias.length() > 0) {
			survey.setShortname(alias);

			for (int pid : surveyService.getAllPublishedSurveyVersions(survey.getId())) {
				Survey s = (Survey) session.get(Survey.class, pid);
				s.setIsDeleted(false);
				s.setShortname(alias);
				session.saveOrUpdate(s);
			}
		}

		session.saveOrUpdate(survey);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Integer> getSurveysMarkedDeleted() {
		Session session = sessionFactory.getCurrentSession();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);

		Date threemonthsago = cal.getTime();
		Query query = session.createQuery(
				"SELECT s.id FROM Survey s WHERE s.isDraft = true AND s.isDeleted = true AND s.deleted < :threemonthsago");
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
			Survey published = (Survey) session.get(Survey.class, id);
			published.setShortname(draft.getShortname());
			session.save(published);
		}
	}

	public List<Survey> getAllSurveysForUser(User user) {
		Session session = sessionFactory.getCurrentSession();

		String hql = "FROM Survey s WHERE s.isDraft = true AND s.owner.id = :userid";
		Query query = session.createQuery(hql);
		query.setInteger("userid", user.getId());

		@SuppressWarnings("unchecked")
		List<Survey> result = query.list();

		return result;
	}

	private String getOwnerWhere(String type) {
		String ownerwhere;

		if (type != null && type.equalsIgnoreCase("my")) {
			ownerwhere = "s.OWNER = :userid";
		} else if (type != null && type.equalsIgnoreCase("shared")) {
			ownerwhere = "s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE (a.ACCESS_USER = :userid OR a.ACCESS_DEPARTMENT IN (SELECT GRPS FROM ECASGROUPS WHERE eg_ID = (SELECT USER_ID FROM ECASUSERS WHERE USER_LOGIN = :login))) AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%'))";
		} else {
			ownerwhere = "s.OWNER = :userid OR s.SURVEY_ID in (Select a.SURVEY FROM SURACCESS a WHERE (a.ACCESS_USER = :userid OR a.ACCESS_DEPARTMENT IN (SELECT GRPS FROM ECASGROUPS WHERE eg_ID = (SELECT USER_ID FROM ECASUSERS WHERE USER_LOGIN = :login))) AND (a.ACCESS_PRIVILEGES like '%2%' or a.ACCESS_PRIVILEGES like '%1%'))";
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

		query.setInteger("userid", user.getId());

		if (type == null || !type.equalsIgnoreCase("my")) {
			query.setString("login", user.getLogin());
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
		query.setInteger("userid", user.getId());

		if (type == null || !type.equalsIgnoreCase("my")) {
			query.setString("login", user.getLogin());
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
			query.setInteger("userid", user.getId());

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
		query.setInteger("userid", user.getId());

		if (type == null || !type.equalsIgnoreCase("my")) {
			query.setString("login", user.getLogin());
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

		List<Archive> archives = archiveService.getArchivesForUser(user.getId());
		result[2] += archives.size();
		result[4] += archives.size();

		return result;
	}

	public Map<Date, List<String>> getSurveysWithEndDatesForUser(User user, String type) {
		Session session = sessionFactory.getCurrentSession();

		String ownerwhere = getOwnerWhere(type);
		String sql = "SELECT s.SURVEYNAME, s.SURVEY_END_DATE FROM SURVEYS s where s.ISDRAFT = 1 and (" + ownerwhere
				+ ") and s.SURVEY_END_DATE is not null and s.SURVEY_END_DATE >= CURDATE() and (s.ARCHIVED = 0 or s.ARCHIVED is null) and (s.DELETED = 0 or s.DELETED is null) and s.AUTOMATICPUBLISHING = 1";

		Query query = session.createSQLQuery(sql);
		query.setInteger("userid", user.getId());

		if (type == null || !type.equalsIgnoreCase("my")) {
			query.setString("login", user.getLogin());
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

	public String getSurveyMetaDataXML(Survey survey) {
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
		s.append("<Survey id='").append(survey.getId()).append("' alias='").append(survey.getShortname())
				.append("'>\n");

		s.append("<SurveyType>")
				.append(survey.getIsQuiz() ? "Quiz" : (survey.getIsOPC() ? "BRP Public Consultation" : "Standard"))
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
		query.setString("uid", surveyuid);
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
			query.setTimestamp("today", today);
			query.setTimestamp("yesterday", yesterday);

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
		Session session = sessionFactory.getCurrentSession();
		Survey survey = getSurvey(Integer.parseInt(surveyId));

		if (survey == null) {
			throw new MessageException("survey does not exist");
		}

		survey.setIsFrozen(true);
		session.saveOrUpdate(survey);

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
		String sql = stringBuilder.toString();

		HashMap<String, Object> parameters = new HashMap<>();
		sql += getSql(filter, parameters, true);

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
		SQLQuery query = session.createSQLQuery("SELECT SURVEY_ID FROM SURVEYS WHERE OWNER = :id AND ISDRAFT = 1");

		@SuppressWarnings("rawtypes")
		List surveys = query.setInteger("id", userid).list();
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
}