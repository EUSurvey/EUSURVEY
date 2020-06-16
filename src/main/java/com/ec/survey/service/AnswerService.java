package com.ec.survey.service;

import com.ec.survey.exception.SmtpServerNotConfiguredException;
import com.ec.survey.exception.TooManyFiltersException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.Attribute;
import com.ec.survey.model.attendees.AttributeName;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.ReportingService.ToDo;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.FileUtils;
import com.ec.survey.tools.InvalidEmailException;
import com.ec.survey.tools.NotAgreedToPsException;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.WeakAuthenticationException;
import com.ec.survey.tools.export.StatisticsCreator;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;

@Service("answerService")
public class AnswerService extends BasicService {

	@Resource(name = "attendeeService")
	private AttendeeService attendeeService;

	@Autowired
	private SqlQueryService sqlQueryService;

	@Resource(name = "validCodesService")
	private ValidCodesService validCodesService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void internalSaveAnswerSet(AnswerSet answerSet, String fileDir, String draftid, boolean invalidateExportsAndStatistics, boolean createAttendees) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		if (answerSet.getSurvey().getShortname().equalsIgnoreCase("NewSelfRegistrationSurvey")) {
			User user = new User();
			user.setPasswordSalt(Tools.newSalt());
			user.setValidated(false);
			user.setComment("Self Registered");

			int nameQuestion = 0;
			int emailQuestion = 0;
			Element emailElement = null;
			int passwordQuestion = 0;
			int languageQuestion = 0;
			int firstNameQuestion = 0;
			int lastNameQuestion = 0;
			SingleChoiceQuestion languageQuestionElement = null;

			for (Element element : answerSet.getSurvey().getElements()) {
				if (element.getShortname().equalsIgnoreCase("name"))
					nameQuestion = element.getId();
				else if (element.getShortname().equalsIgnoreCase("email")) {
					emailQuestion = element.getId();
					emailElement = element;
				} else if (element.getShortname().equalsIgnoreCase("password"))
					passwordQuestion = element.getId();
				else if (element.getShortname().equalsIgnoreCase("language")) {
					languageQuestion = element.getId();
					languageQuestionElement = (SingleChoiceQuestion) element;
				} else if (element.getShortname().equalsIgnoreCase("firstname")) {
					firstNameQuestion = element.getId();
				} else if (element.getShortname().equalsIgnoreCase("lastname")) {
					lastNameQuestion = element.getId();
				}
			}

			if (nameQuestion == 0 || emailQuestion == 0 || passwordQuestion == 0 || languageQuestion == 0) {
				throw new Exception("Not all needed registration questions found!");
			}

			for (Answer answer : answerSet.getAnswers()) {
				if (answer.getQuestionId() == nameQuestion) {
					user.setLogin(answer.getValue());
				} else if (answer.getQuestionId() == emailQuestion) {
					user.setEmail(answer.getValue());
				} else if (answer.getQuestionId() == firstNameQuestion) {
					user.setGivenName(answer.getValue());
				} else if (answer.getQuestionId() == lastNameQuestion) {
					user.setSurName(answer.getValue());
				} else if (answer.getQuestionId() == passwordQuestion) {
					user.setPassword(Tools.hash(answer.getValue() + user.getPasswordSalt()));
				} else if (answer.getQuestionId() == languageQuestion) {
					for (PossibleAnswer possibleAnswer : languageQuestionElement.getPossibleAnswers()) {
						if (possibleAnswer.getId().toString().equalsIgnoreCase(answer.getValue())) {
							user.setLanguage(possibleAnswer.getShortname().toUpperCase());
						}
					}
				}
			}

			List<Role> Roles = administrationService.getAllRoles();
			for (Role role : Roles) {
				if (role.getName().equalsIgnoreCase("Form Manager")) {
					user.getRoles().add(role);
					break;
				}
			}
			user.setType(User.SYSTEM);

			// first check email
			if (!MailService.isValidEmailAddress(user.getEmail())) {
				throw new InvalidEmailException(emailElement, "");
			}

			if (!administrationService.isSmtpServerConfigured()) {
				throw new SmtpServerNotConfiguredException();
			}

			administrationService.createUser(user);
			administrationService.sendValidationEmail(user);

		} else {

			// restore passwords from drafts
			if (draftid != null) {
				Draft draft = getDraft(draftid);
				if (draft != null) {
					for (Element element : answerSet.getSurvey().getElements()) {
						if (element instanceof FreeTextQuestion) {
							FreeTextQuestion q = (FreeTextQuestion) element;
							if (q.getIsPassword()) {
								List<Answer> originalAnswers = draft.getAnswerSet().getAnswers(q.getId());
								if (originalAnswers.size() > 0) {
									List<Answer> currentPasswordAnswers = answerSet.getAnswers(q.getId());
									if (currentPasswordAnswers.size() > 0) {
										String currentAnswer = currentPasswordAnswers.get(0).getValue();
										if (currentAnswer != null && currentAnswer.equalsIgnoreCase("********")) {
											answerSet.getAnswers(q.getId()).get(0).setValue(originalAnswers.get(0).getValue());
										}
									}
								}
							}
						}
					}
				}
			}

			for (Answer answer : answerSet.getAnswers()) {
				answer.setIsDraft(answerSet.getIsDraft());
			}

			answerSet.setUpdateDate(new Date());
			boolean newAnswer = answerSet.getId() == null;
			session.saveOrUpdate(answerSet);
			session.flush();
			if (!answerSet.getSurvey().getIsDraft()) {
				if (newAnswer) {
					reportingService.addToDo(ToDo.NEWCONTRIBUTION, answerSet.getSurvey().getUniqueId(), answerSet.getUniqueCode());
				} else {
					reportingService.addToDo(ToDo.CHANGEDCONTRIBUTION, answerSet.getSurvey().getUniqueId(), answerSet.getUniqueCode());
				}
			} else {
				if (newAnswer) {
					reportingService.addToDo(ToDo.NEWTESTCONTRIBUTION, answerSet.getSurvey().getUniqueId(), answerSet.getUniqueCode());
				} else {
					reportingService.addToDo(ToDo.CHANGEDTESTCONTRIBUTION, answerSet.getSurvey().getUniqueId(), answerSet.getUniqueCode());
				}
			}

			if (invalidateExportsAndStatistics) {
				// mark exports invalid
				exportService.invalidate(answerSet.getSurveyId());

				// delete precomputed statistics and pdfs
				try {
					deleteStatisticsForSurvey(answerSet.getSurvey().getId());

					java.io.File folder = fileService.getSurveyExportsFolder(answerSet.getSurvey().getUniqueId());
					java.io.File target = new java.io.File(String.format("%s/publishedanswer%s.pdf", folder.getPath(), answerSet.getId()));

					if (target.exists())
						target.delete();

					target = new java.io.File(String.format("%s/answer%s.pdf", folder.getPath(), answerSet.getUniqueCode()));

					if (target.exists())
						target.delete();
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
				// delete temporary files folder
				try {
					java.io.File folder = fileService.getSurveyUploadsFolder(answerSet.getSurvey().getUniqueId(), false);
					java.io.File directory = new java.io.File(folder.getPath() + "/" + answerSet.getUniqueCode());
					FileUtils.delete(directory);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}

			if (createAttendees && answerSet.getSurvey().getRegistrationForm()) {
				// this is a registration form -> create attendee
				Attendee attendee = new Attendee();
				attendee.setOwnerId(answerSet.getSurvey().getOwner().getId());
				attendee.setRegFormId(answerSet.getSurvey().getId());

				Map<Integer, Question> questionsById = answerSet.getSurvey().getQuestionMap();

				for (Answer answer : answerSet.getAnswers()) {
					Question question = questionsById.get(answer.getQuestionId());

					if (question != null && question.getIsAttribute()) {
						if (question.getAttributeName().equalsIgnoreCase("name"))
							attendee.setName(answer.getValue());
						else if (question.getAttributeName().equalsIgnoreCase("email"))
							attendee.setEmail(answer.getValue());
						else {
							Attribute a = new Attribute();

							AttributeName attributeName = attendeeService.getAttributeName(question.getAttributeName(), answerSet.getSurvey().getOwner().getId());

							if (attributeName == null) {
								attributeName = new AttributeName();
								attributeName.setName(question.getAttributeName());
								attributeName.setOwnerId(answerSet.getSurvey().getOwner().getId());
								attendeeService.add(attributeName);
							}

							a.setAttributeName(attributeName);

							if (question instanceof ChoiceQuestion) {
								// replace ID by label of the answer
								ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
								PossibleAnswer possibleAnswer = choiceQuestion.getPossibleAnswer(answer.getPossibleAnswerId());
								a.setValue(possibleAnswer.getTitle());
							} else {
								a.setValue(answer.getValue());
							}

							attendee.getAttributes().add(a);
						}
					}
				}

				attendeeService.add(attendee);
			}
		}
	}

	@Transactional(readOnly = true)
	public AnswerSet get(int id) {
		Session session = sessionFactory.getCurrentSession();
		return (AnswerSet) session.get(AnswerSet.class, id);
	}

	@Transactional(readOnly = true)
	public AnswerSet get(int id, boolean initializeFiles) {
		Session session = sessionFactory.getCurrentSession();
		AnswerSet answerSet = (AnswerSet) session.get(AnswerSet.class, id);
		if (initializeFiles) {
			for (Answer answer : answerSet.getAnswers()) {
				Hibernate.initialize(answer.getFiles());
			}
		}
		return answerSet;
	}

	@Transactional(readOnly = true)
	public List<AnswerSet> getDraftAnswers(int surveyId, ResultFilter filter, SqlPagination sqlPagination, boolean loadDraftIds, boolean initFiles) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> parameters = new HashMap<>();

		boolean useSurveysTable = false;
		boolean useDraftSurveysTable = false;

		String sql;

		StringBuilder where = new StringBuilder();

		if (filter.getUser().indexOf(";") > 0) {
			where.append("ans.RESPONDER_EMAIL IN (:emails)");
		} else {
			where.append("(ans.RESPONDER_EMAIL = :email)");
		}

		if (filter.getUpdatedFrom() != null) {
			where.append(" AND ans.ANSWER_SET_UPDATE >= :updateDateFrom");
			parameters.put("updateDateFrom", filter.getUpdatedFrom());
		}

		if (filter.getUpdatedTo() != null) {
			where.append(" AND ans.ANSWER_SET_UPDATE < :updateDateTo");
			parameters.put("updateDateTo", Tools.getFollowingDay(filter.getUpdatedTo()));
		}

		if (filter.getSurveyShortname() != null && filter.getSurveyShortname().length() > 0) {
			useSurveysTable = true;
			where.append(" AND s.SURVEYNAME LIKE :surveyAlias");
			parameters.put("surveyAlias", "%" + filter.getSurveyShortname().trim() + "%");
		}

		if (filter.getSurveyTitle() != null && filter.getSurveyTitle().length() > 0) {
			useSurveysTable = true;
			where.append(" AND s.TITLESORT LIKE :surveyTitle");
			parameters.put("surveyTitle", "%" + filter.getSurveyTitle().trim() + "%");
		}

		if (filter.getSurveyStatus() != null && filter.getSurveyStatus().length() > 0 && !filter.getSurveyStatus().equalsIgnoreCase("All")) {
			useSurveysTable = true;
			useDraftSurveysTable = true;
			where.append(" AND d.ACTIVE = :surveyActive");
			parameters.put("surveyActive", filter.getSurveyStatus().equalsIgnoreCase("Published") ? 1 : 0);
		}

		if (filter.getSurveyEndDateFrom() != null) {
			useSurveysTable = true;
			where.append(" AND s.SURVEY_END_DATE >= :endFrom");
			parameters.put("endFrom", filter.getSurveyEndDateFrom());
		}

		if (filter.getSurveyEndDateTo() != null) {
			useSurveysTable = true;
			where.append(" AND s.SURVEY_END_DATE <= :endTo");
			parameters.put("endTo", filter.getSurveyEndDateTo());
		}

		String joinSurveys = "";
		if (useSurveysTable)
			joinSurveys = " JOIN SURVEYS s ON ans.SURVEY_ID = s.SURVEY_ID";

		if (useDraftSurveysTable)
			joinSurveys += " JOIN SURVEYS d ON d.SURVEY_UID = s.SURVEY_UID AND d.ISDRAFT = 1";

		sql = "SELECT MIN(ans.ANSWER_SET_ID) FROM ANSWERS_SET ans " + joinSurveys + " WHERE " + where.toString()
				+ " GROUP BY ans.UNIQUECODE HAVING min(ans.ISDRAFT) = 1 AND max(ans.ISDRAFT) = 1 ORDER BY ans.ANSWER_SET_UPDATE DESC";

		if (filter.getUser().indexOf(";") > 0) {
			parameters.put("emails", filter.getUser().trim().split(";"));
		} else {
			parameters.put("email", filter.getUser());
		}

		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("rawtypes")
		List res = query.setFirstResult(sqlPagination.getFirstResult()).setMaxResults(sqlPagination.getMaxResult()).list();

		List<AnswerSet> result = new ArrayList<>();

		for (Object o : res) {
			Integer i = ConversionTools.getValue(o);
			AnswerSet answerSet = (AnswerSet) session.get(AnswerSet.class, i);
			result.add(answerSet);
		}

		return result;
	}

	@Transactional(readOnly = true)
	public List<AnswerSet> getAnswers(Survey survey, ResultFilter filter, SqlPagination sqlPagination, boolean loadDraftIds, boolean initFiles, boolean usereportingdatabase) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> parameters = new HashMap<>();

		List<AnswerSet> result = new ArrayList<>();

		if (isReportingDatabaseEnabled() && usereportingdatabase) {
			List<Integer> answerSetIDs = reportingService.getAnswerSetIDs(survey, filter, sqlPagination);

			if (answerSetIDs != null) {
				result = answerService.getAnswers(answerSetIDs, false);
				return result;
			}
		}

		String sql;

		if (loadDraftIds) {
			sql = "select max(ans.ANSWER_SET_ID), max(d.DRAFT_UID), min(ans.ISDRAFT) from ANSWERS_SET ans LEFT JOIN ANSWERS_SET ans2 ON ans.UNIQUECODE = ans2.UNIQUECODE LEFT JOIN DRAFTS d ON ans2.ANSWER_SET_ID = d.answerSet_ANSWER_SET_ID LEFT JOIN SURVEYS s ON ans2.SURVEY_ID = s.SURVEY_ID where ans.ANSWER_SET_ID IN ("
					+ getSql(null, survey == null ? -1 : survey.getId(), filter, parameters, true, true) + ") GROUP BY ans.UNIQUECODE ORDER BY ans.ANSWER_SET_DATE ASC";
		} else {
			sql = getSql(null, survey == null ? -1 : survey.getId(), filter, parameters, true, true);
		}

		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("rawtypes")
		List res = query.setFirstResult(sqlPagination.getFirstResult()).setMaxResults(sqlPagination.getMaxResult()).list();

		boolean checkDraftSubmitted = filter != null && (filter.getUpdatedTo() != null);

		for (Object o : res) {
			Integer i = ConversionTools.getValue(o);
			AnswerSet answerSet = (AnswerSet) session.get(AnswerSet.class, i);

			if (loadDraftIds && answerSet.getIsDraft()) {
				AnswerSet contribution = get(answerSet.getUniqueCode());
				if (contribution != null) {
					answerSet = contribution;
				}
			}

			if (loadDraftIds && ((Object[]) o)[1] != null) {
				answerSet.setDraftId((((Object[]) o)[1]).toString());
			}

			if (loadDraftIds && ConversionTools.getValue(((Object[]) o)[2]) > 0) {
				answerSet.setUniqueCode("");
			}

			if (loadDraftIds && answerSet.getIsDraft() && checkDraftSubmitted) {
				if (surveyService.answerSetExists(answerSet.getUniqueCode(), false, false)) {
					answerSet.setIsDraft(false);
				}
			}

			if (initFiles) {
				for (Answer answer : answerSet.getAnswers()) {
					Hibernate.initialize(answer.getFiles());
				}
			}

			result.add(answerSet);
		}

		return result;
	}

	public String getSql(String prefix, int surveyId, ResultFilter filter, HashMap<String, Object> values, boolean usesjoin, boolean searchallsurveys) throws TooManyFiltersException {
		if (prefix == null || prefix.length() == 0) {
			prefix = "SELECT DISTINCT ans.ANSWER_SET_ID";
		}

		StringBuilder sql = new StringBuilder(prefix + " FROM ANSWERS a1");
		StringBuilder where = new StringBuilder();
		int joincounter = 0;
		boolean useSurveysTable = false;
		boolean useDraftSurveysTable = false;
		boolean usePublicationsTable = false;
		String joinSurveys = "";

		if (surveyId > -1) {
			where = new StringBuilder(" ans.SURVEY_ID = :surveyId AND ans.ISDRAFT = false");

			List<Integer> allVersions = surveyService.getAllSurveyVersions(surveyId);

			if (searchallsurveys && allVersions.size() > 1) {
				where = new StringBuilder(" ans.SURVEY_ID IN (" + StringUtils.collectionToCommaDelimitedString(allVersions) + ") AND ans.ISDRAFT = false");
			} else {
				values.put("surveyId", surveyId);
			}
		} else {

			if (filter.getNoTestAnswers()) {
				useSurveysTable = true;
				where = new StringBuilder("s.ISDRAFT = 0");
			} else {
				where = new StringBuilder("ans.SURVEY_ID > 0");
			}

			if (filter.getSurveyUid() != null && filter.getSurveyUid().length() > 0) {
				useSurveysTable = true;
				where.append(" AND s.SURVEY_UID = :surveyUID");
				values.put("surveyUID", filter.getSurveyUid().trim());
			}

			if (filter.getSurveyShortname() != null && filter.getSurveyShortname().length() > 0) {
				useSurveysTable = true;
				where.append(" AND s.SURVEYNAME LIKE :surveyAlias");
				values.put("surveyAlias", "%" + filter.getSurveyShortname().trim() + "%");
			}

			if (filter.getSurveyTitle() != null && filter.getSurveyTitle().length() > 0) {
				useSurveysTable = true;
				where.append(" AND s.TITLESORT LIKE :surveyTitle");
				values.put("surveyTitle", "%" + filter.getSurveyTitle().trim() + "%");
			}

			if (filter.getSurveyStatus() != null && filter.getSurveyStatus().length() > 0 && !filter.getSurveyStatus().equalsIgnoreCase("All")) {
				useSurveysTable = true;
				useDraftSurveysTable = true;
				where.append(" AND d.ACTIVE = :surveyActive");
				values.put("surveyActive", filter.getSurveyStatus().equalsIgnoreCase("Published") ? 1 : 0);
			}

			if (filter.getSurveyEndDateFrom() != null) {
				useSurveysTable = true;
				where.append(" AND s.SURVEY_END_DATE >= :endFrom");
				values.put("endFrom", filter.getSurveyEndDateFrom());
			}

			if (filter.getSurveyEndDateTo() != null) {
				useSurveysTable = true;
				where.append(" AND s.SURVEY_END_DATE <= :endTo");
				values.put("endTo", filter.getSurveyEndDateTo());
			}

			if (filter.getSurveyPublishedResults() != null && filter.getSurveyPublishedResults().length() > 0 && !filter.getSurveyPublishedResults().equalsIgnoreCase("All")) {
				usePublicationsTable = true;
				useSurveysTable = true;
				where.append(" AND (p.PUB_CONT = 1 || p.PUB_STAT = 1)");
			}
		}

		if (filter != null) {

			if (filter.getFilterValues() != null && filter.getFilterValues().size() > 3) {
				throw new TooManyFiltersException("too many result filters");
			}

			if (filter.getStatus() != null && filter.getStatus().length() > 0 && !filter.getStatus().equalsIgnoreCase("All")) {
				where.append(" AND ans.ISDRAFT = :status");
				values.put("status", filter.getStatus().equalsIgnoreCase("Submitted") ? 0 : 1);
			}

			if (filter.getInvitation() != null && filter.getInvitation().length() > 0) {
				where.append(" AND ans.ANSWER_SET_INVID = :invitationId");
				values.put("invitationId", filter.getInvitation().trim());
			}

			if (filter.getCaseId() != null && filter.getCaseId().length() > 0) {
				where.append(" AND ans.UNIQUECODE = :uniqueCode");
				values.put("uniqueCode", filter.getCaseId().trim());
			}

			if (filter.getDraftId() != null && filter.getDraftId().length() > 0) {
				where.append(" AND d.DRAFT_UID = :draftId");
				values.put("draftId", filter.getDraftId().trim());
			}

			if (filter.getUser() != null && filter.getUser().length() > 0) {
				if (filter.getUser().indexOf(";") > 0) {
					where.append(" AND ans.RESPONDER_EMAIL IN (:emails)");
					values.put("emails", filter.getUser().trim().split(";"));
				} else {
					where.append(" AND ans.RESPONDER_EMAIL = :email");
					values.put("email", filter.getUser().trim());
				}
			}

			if (filter.getCreatedOrUpdated() != null && filter.getCreatedOrUpdated() && filter.getGeneratedFrom() != null && filter.getGeneratedTo() != null && filter.getUpdatedFrom() != null
					&& filter.getUpdatedTo() != null) {
				where.append(
						" AND ((ans.ANSWER_SET_DATE >= :generatedFrom AND ans.ANSWER_SET_DATE < :generatedTo) OR (ans.ANSWER_SET_UPDATE >= :updateDateFrom AND ans.ANSWER_SET_UPDATE < :updateDateTo))");
				values.put("generatedFrom", filter.getGeneratedFrom());
				values.put("generatedTo", Tools.getFollowingDay(filter.getGeneratedTo()));
				values.put("updateDateFrom", filter.getUpdatedFrom());
				values.put("updateDateTo", Tools.getFollowingDay(filter.getUpdatedTo()));
			} else {
				if (filter.getGeneratedFrom() != null) {
					where.append(" AND ans.ANSWER_SET_DATE >= :generatedFrom");
					values.put("generatedFrom", filter.getGeneratedFrom());
				}

				if (filter.getGeneratedTo() != null) {
					where.append(" AND ans.ANSWER_SET_DATE < :generatedTo");
					values.put("generatedTo", Tools.getFollowingDay(filter.getGeneratedTo()));
				}

				if (filter.getUpdatedFrom() != null) {
					where.append(" AND ans.ANSWER_SET_UPDATE >= :updateDateFrom");
					values.put("updateDateFrom", filter.getUpdatedFrom());
				}

				if (filter.getUpdatedTo() != null) {
					where.append(" AND ans.ANSWER_SET_UPDATE < :updateDateTo");
					values.put("updateDateTo", Tools.getFollowingDay(filter.getUpdatedTo()));
				}

				if (filter.getOnlyReallyUpdated() != null && filter.getOnlyReallyUpdated()) {
					where.append(" AND ans.ANSWER_SET_DATE != ans.ANSWER_SET_UPDATE");
				}
			}

			if (filter.getLanguages() != null && filter.getLanguages().size() > 0) {
				int i = 0;
				where.append(" AND (");
				for (String lang : filter.getLanguages()) {
					if (lang.trim().length() > 0) {
						String l = "lang" + i++;

						if (i > 1) {
							where.append(" OR");
						}

						where.append(" ans.ANSWER_SET_LANG like :").append(l);
						values.put(l, lang.trim());
					}
				}
				where.append(" )");
			}

			Map<String, String> filterValues = filter.getFilterValues();
			if (filterValues != null && filterValues.size() > 0) {
				int i = 0;
				for (Entry<String, String> item : filterValues.entrySet()) {
					String questionIdAndUid = item.getKey();
					String questionId = questionIdAndUid.substring(0, questionIdAndUid.indexOf('|'));
					String questionUid = questionIdAndUid.substring(questionIdAndUid.indexOf('|') + 1);

					String answersasstring = item.getValue();

					String[] answers = StringUtils.delimitedListToStringArray(answersasstring, ";");

					if (answersasstring.replace(";", "").trim().length() > 0 && answers.length > 0) {
						where.append(" AND (");

						boolean first = true;

						joincounter++;

						if (joincounter > 1) {
							sql.append(" JOIN ANSWERS a").append(joincounter).append(" ON a1.AS_ID = a").append(joincounter).append(".AS_ID");
						}

						for (String answer : answers)
							if (answer.trim().length() > 0) {
								if (first) {
									where.append(" (");
								} else {
									where.append(" OR (");
								}

								String answerPart;

								// if (answer.length() > 2)
								// {
								// answerPart = "MATCH(a" + joincounter + ".VALUE) AGAINST (:answer" + i + ")";
								// } else {
								answerPart = "a" + joincounter + ".VALUE like :answer" + i;
								// }

								if (answer.contains("|")) {
									String answerUid = answer.substring(answer.indexOf('|') + 1);
									answerPart = "(a" + joincounter + ".PA_UID like :answerUid" + i + ")";
									values.put("answerUid" + i, answerUid);
								} else {
									if (answer.contains("/")) {
										answerPart = "a" + joincounter + ".VALUE LIKE :answer" + i;
										values.put("answer" + i, "%" + answer + "%");
									} else {
										values.put("answer" + i, "%" + answer + "%");
									}
								}

								if (questionId.contains("-")) {
									String[] data = questionId.split("-");

									if (questionUid.length() > 0) {
										where.append(" (a").append(joincounter).append(".ANSWER_ROW = :row").append(i).append(" AND a").append(joincounter).append(".ANSWER_COL = :col").append(i)
												.append(" AND (a").append(joincounter).append(".QUESTION_ID = :questionId").append(i).append(" OR a").append(joincounter)
												.append(".QUESTION_UID = :questionUid").append(i).append(") AND ").append(answerPart).append(")");
										values.put("questionUid" + i, questionUid);
									} else {
										where.append(" (a").append(joincounter).append(".ANSWER_ROW = :row").append(i).append(" AND a").append(joincounter).append(".ANSWER_COL = :col").append(i)
												.append(" AND a").append(joincounter).append(".QUESTION_ID = :questionId").append(i).append(" AND ").append(answerPart).append(")");
									}

									values.put("questionId" + i, data[0]);
									values.put("row" + i, data[1]);
									values.put("col" + i, data[2]);

								} else {
									if (questionUid.length() > 0) {
										where.append(" (a").append(joincounter).append(".QUESTION_UID = :questionUid").append(i).append(" AND ").append(answerPart).append(")");
										values.put("questionUid" + i, questionUid);
									} else {
										where.append("( a").append(joincounter).append(".QUESTION_ID = :questionId").append(i).append(" AND ").append(answerPart).append(")");
										values.put("questionId" + i, questionId);
									}
								}

								i++;
								first = false;

								where.append(" )");
							}
						where.append(" )");
					}
				}
			}

			if (filter.getSortKey() != null && filter.getSortKey().equalsIgnoreCase("score")) {
				where.append(" ORDER BY ans.SCORE ").append(filter.getSortOrder());
			} else if (filter.getSortKey() != null && filter.getSortKey().equalsIgnoreCase("date")) {
				where.append(" ORDER BY ans.ANSWER_SET_DATE ").append(filter.getSortOrder());
			}
		}

		// if flag is set then we need to use the join with the surveys table
		if (useSurveysTable)
			joinSurveys = " JOIN SURVEYS s ON ans.SURVEY_ID = s.SURVEY_ID";

		if (useDraftSurveysTable)
			joinSurveys += " JOIN SURVEYS d ON d.SURVEY_UID = s.SURVEY_UID AND d.ISDRAFT = 1";

		if (usePublicationsTable)
			joinSurveys += " JOIN PUBLICATION p ON p.PUB_ID = s.publication_PUB_ID";

		if (prefix.contains("inv.")) {
			return sql + " RIGHT JOIN ANSWERS_SET ans ON a1.AS_ID = ans.ANSWER_SET_ID " + joinSurveys + " LEFT OUTER JOIN INVITATIONS inv ON inv.INVITATION_ID = ans.ANSWER_SET_INVID WHERE " + where;
		}

		if (prefix.contains("inv.") || filter != null && filter.getDraftId() != null && filter.getDraftId().length() > 0) {
			return sql + " RIGHT JOIN ANSWERS_SET ans ON a1.AS_ID = ans.ANSWER_SET_ID " + joinSurveys + " LEFT JOIN DRAFTS d ON ans.ANSWER_SET_ID = d.answerSet_ANSWER_SET_ID WHERE " + where;
		}

		return sql + " RIGHT JOIN ANSWERS_SET ans ON a1.AS_ID = ans.ANSWER_SET_ID " + joinSurveys + " WHERE " + where;

	}

	@Transactional(readOnly = false)
	public List<String> deleteAnswers(int surveyId, ResultFilter filter) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> parameters = new HashMap<>();

		String sql = getSql("select DISTINCT ans.UNIQUECODE", surveyId, filter, parameters, false, true);

		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("unchecked")
		List<String> answerSetsToDelete = query.list();

		return deleteAnswers(answerSetsToDelete, surveyId);
	}

	@Transactional
	public List<String> deleteAnswers(List<String> answerSetsToDelete, int surveyid) {
		List<String> deletedAnswerSets = new ArrayList<>();

		if (answerSetsToDelete == null || answerSetsToDelete.size() == 0) {
			return deletedAnswerSets;
		}

		Session session = sessionFactory.getCurrentSession();
		StringBuilder sql = new StringBuilder("From AnswerSet a where a.uniqueCode in (");
		int counter = 0;
		for (String uniqueCode : answerSetsToDelete) {
			if (counter > 0) {
				sql.append(", ");
			}
			sql.append("'").append(uniqueCode).append("'");
			counter++;
		}

		sql.append(")");

		Query query = session.createQuery(sql.toString());

		@SuppressWarnings("unchecked")
		List<AnswerSet> answerSets = query.list();

		for (AnswerSet as : answerSets) {
			if (!as.getIsDraft()) {
				session.delete(as);

				if (!as.getIsDraft()) {
					if (as.getSurvey().getIsDraft()) {
						reportingService.addToDo(ToDo.DELETEDTESTCONTRIBUTION, as.getSurvey().getUniqueId(), as.getUniqueCode());
					} else {
						reportingService.addToDo(ToDo.DELETEDCONTRIBUTION, as.getSurvey().getUniqueId(), as.getUniqueCode());
					}
				}

				deletedAnswerSets.add(as.getUniqueCode());
			}
		}

		deleteStatisticsForSurvey(surveyid);

		return deletedAnswerSets;
	}

	@Transactional(readOnly = true)
	public Set<String> getCaseIds(Integer surveyId, ResultFilter filter, int page, int rowsPerPage, boolean searchallsurveys) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> parameters = new HashMap<>();

		String sql = getSql("select DISTINCT ans.UNIQUECODE", surveyId, filter, parameters, false, searchallsurveys);

		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("rawtypes")
		List res = query.setFirstResult((page - 1) * rowsPerPage).setMaxResults(rowsPerPage).list();

		Set<String> result = new HashSet<>();

		for (Object o : res) {
			if (o != null) {
				result.add(o.toString());
			}
		}

		return result;
	}

	@Transactional(readOnly = true)
	public Set<Integer> getAllAnswerIds(Integer surveyId, ResultFilter filter, int page, int maxValue) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> parameters = new HashMap<>();

		String sql = getSql(null, surveyId, filter, parameters, false, true);

		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		Set<Integer> result = new HashSet<>();

		for (Object o : res) {
			result.add(ConversionTools.getValue(o));
		}

		return result;
	}

	@Transactional
	public List<AnswerSet> getAllAnswers(int surveyId, ResultFilter filter) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> parameters = new HashMap<>();

		String answersetsql = getSql(null, surveyId, filter, parameters, false, true);
		String sql = "select a1.AS_ID, a1.QUESTION_ID, a1.QUESTION_UID, a1.VALUE, a1.ANSWER_COL, a1.ANSWER_ID, a1.ANSWER_ROW, a1.PA_ID, a1.PA_UID, ans.UNIQUECODE, ans.ANSWER_SET_DATE, ans.ANSWER_SET_UPDATE, ans.ANSWER_SET_INVID, ans.RESPONDER_EMAIL, ans.ANSWER_SET_LANG FROM ANSWERS a1 JOIN ANSWERS_SET ans ON a1.AS_ID = ans.ANSWER_SET_ID WHERE ans.ANSWER_SET_ID IN ("
				+ answersetsql + ")";

		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		HashMap<Integer, AnswerSet> result = new HashMap<>();

		for (Object o : res) {
			Object[] a = (Object[]) o;
			Answer answer = new Answer();
			answer.setAnswerSetId(ConversionTools.getValue(a[0]));
			answer.setQuestionId(ConversionTools.getValue(a[1]));
			answer.setQuestionUniqueId((String) a[2]);
			answer.setValue((String) a[3]);
			answer.setColumn(ConversionTools.getValue(a[4]));
			answer.setId(ConversionTools.getValue(a[5]));
			answer.setRow(ConversionTools.getValue(a[6]));
			answer.setPossibleAnswerId(ConversionTools.getValue(a[7]));
			answer.setPossibleAnswerUniqueId((String) a[8]);

			if (result.containsKey(answer.getAnswerSetId())) {
				result.get(answer.getAnswerSetId()).addAnswer(answer);
			} else {
				AnswerSet answerSet = new AnswerSet();
				answerSet.setId(answer.getAnswerSetId());
				answerSet.getAnswers().add(answer);
				answerSet.setUniqueCode((String) a[9]);
				answerSet.setDate((Date) a[10]);
				answerSet.setUpdateDate((Date) a[11]);
				answerSet.setInvitationId((String) a[12]);
				answerSet.setResponderEmail((String) a[13]);
				answerSet.setLanguageCode((String) a[14]);
				result.put(answerSet.getId(), answerSet);
			}
		}

		// now check "empty" answers
		query = session.createSQLQuery(answersetsql);
		sqlQueryService.setParameters(query, parameters);
		@SuppressWarnings("unchecked")
		List<Integer> answersetids = query.list();

		for (Integer id : answersetids) {
			if (!result.containsKey(id)) {
				AnswerSet answerSet = get(id);
				result.put(answerSet.getId(), answerSet);
			}
		}

		List<AnswerSet> answerSets = new ArrayList<>(result.values());

		return answerSets;
	}

	@Transactional(readOnly = true)
	public List<File> getAllUploadedFiles(int surveyId, ResultFilter filter, int page, int rowsPerPage) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> parameters = new HashMap<>();

		String sql;

		if (filter.isEmpty()) {
			List<Integer> allVersions = surveyService.getAllSurveyVersions(surveyId);
			sql = "SELECT f.FILE_ID, f.FILE_NAME, f.FILE_UID, af.ANSWERS_ANSWER_ID FROM FILES f JOIN ANSWERS_FILES af ON f.FILE_ID = af.files_FILE_ID JOIN ANSWERS a ON af.ANSWERS_ANSWER_ID = a.ANSWER_ID JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID JOIN SURVEYS s ON ans.SURVEY_ID = s.SURVEY_ID WHERE ans.SURVEY_ID IN ("
					+ StringUtils.collectionToCommaDelimitedString(allVersions) + ") AND ans.ISDRAFT = FALSE";
		} else {
			sql = "SELECT f.FILE_ID, f.FILE_NAME, f.FILE_UID, af.ANSWERS_ANSWER_ID FROM FILES f JOIN ANSWERS_FILES af ON f.FILE_ID = af.files_FILE_ID JOIN ANSWERS a ON af.ANSWERS_ANSWER_ID = a.ANSWER_ID JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID in ("
					+ getSql(null, surveyId, filter, parameters, false, true) + ")";
		}

		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		List<File> result = new ArrayList<>();

		for (Object o : res) {
			Object[] a = (Object[]) o;
			File file = new File();
			file.setId(ConversionTools.getValue(a[0]));
			file.setName((String) a[1]);
			file.setUid((String) a[2]);
			file.setAnswerId(ConversionTools.getValue(a[3]));

			result.add(file);
		}

		return result;
	}

	@Transactional(readOnly = true)
	public Map<String, Map<String, List<File>>> getAllUploadedFilesByContribution(int surveyId, ResultFilter filter, int page, int rowsPerPage) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> parameters = new HashMap<>();

		String sql;

		if (filter.isEmpty()) {
			List<Integer> allVersions = surveyService.getAllSurveyVersions(surveyId);
			sql = "SELECT f.FILE_ID, f.FILE_NAME, f.FILE_UID, af.ANSWERS_ANSWER_ID, ans.UNIQUECODE, a.QUESTION_UID FROM FILES f JOIN ANSWERS_FILES af ON f.FILE_ID = af.files_FILE_ID JOIN ANSWERS a ON af.ANSWERS_ANSWER_ID = a.ANSWER_ID JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID JOIN SURVEYS s ON ans.SURVEY_ID = s.SURVEY_ID WHERE ans.SURVEY_ID IN ("
					+ StringUtils.collectionToCommaDelimitedString(allVersions) + ") AND ans.ISDRAFT = FALSE";
		} else {
			sql = "SELECT f.FILE_ID, f.FILE_NAME, f.FILE_UID, af.ANSWERS_ANSWER_ID, ans.UNIQUECODE, a.QUESTION_UID FROM FILES f JOIN ANSWERS_FILES af ON f.FILE_ID = af.files_FILE_ID JOIN ANSWERS a ON af.ANSWERS_ANSWER_ID = a.ANSWER_ID JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID in ("
					+ getSql(null, surveyId, filter, parameters, false, true) + ")";
		}

		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		Map<String, Map<String, List<File>>> result = new HashMap<>();
		Map<String, String> nicenames = new HashMap<>();

		for (Object o : res) {
			Object[] a = (Object[]) o;
			File file = new File();
			file.setId(ConversionTools.getValue(a[0]));
			file.setName((String) a[1]);
			file.setUid((String) a[2]);
			file.setAnswerId(ConversionTools.getValue(a[3]));

			String code = (String) a[4];
			String questionUID = (String) a[5];

			if (!result.containsKey(code)) {
				result.put(code, new HashMap<String, List<File>>());
			}

			if (!nicenames.containsKey(questionUID)) {
				nicenames.put(questionUID, "Upload_" + (nicenames.size() + 1));
			}

			if (!result.get(code).containsKey(nicenames.get(questionUID))) {
				result.get(code).put(nicenames.get(questionUID), new ArrayList<File>());
			}

			result.get(code).get(nicenames.get(questionUID)).add(file);
		}

		return result;
	}

	@Transactional(readOnly = true)
	public List<File> getUploadedFilesForAnswerset(int answersetId) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT f.FILE_ID, f.FILE_NAME, f.FILE_UID, af.ANSWERS_ANSWER_ID FROM FILES f JOIN ANSWERS_FILES af ON f.FILE_ID = af.files_FILE_ID JOIN ANSWERS a ON af.ANSWERS_ANSWER_ID = a.ANSWER_ID WHERE a.AS_ID = :id";

		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger("id", answersetId);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		List<File> result = new ArrayList<>();

		for (Object o : res) {
			Object[] a = (Object[]) o;
			File file = new File();
			file.setId(ConversionTools.getValue(a[0]));
			file.setName((String) a[1]);
			file.setUid((String) a[2]);
			file.setAnswerId(ConversionTools.getValue(a[3]));

			result.add(file);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	List<LiveStatistics> getLiveStatistics(Survey survey) {
		List<LiveStatistics> statistics = new ArrayList<>();
		List<Integer> choiceAnswers = new ArrayList<>();
		List<IntKeyValue> matrixAnswers = new ArrayList<>();

		for (Question q : survey.getQuestions()) {
			if (q instanceof ChoiceQuestion) {
				ChoiceQuestion choice = (ChoiceQuestion) q;
				for (PossibleAnswer a : choice.getPossibleAnswers()) {
					choiceAnswers.add(a.getId());
				}
			} else if (q instanceof Matrix) {
				Matrix matrix = (Matrix) q;
				for (Element matrixQuestion : matrix.getQuestions()) {
					for (Element matrixAnswer : matrix.getAnswers()) {
						matrixAnswers.add(new IntKeyValue(matrixAnswer.getId(), matrixQuestion.getId()));
					}
				}
			}
		}

		Session session = sessionFactory.getCurrentSession();

		if (choiceAnswers.size() > 0) {
			Query query = session.createQuery("FROM LiveStatistics WHERE PAID IN (:choiceAnswers)");
			query.setParameterList("choiceAnswers", choiceAnswers);
			List<LiveStatistics> stats = query.list();
			statistics.addAll(stats);
		}

		if (matrixAnswers.size() > 0) {
			List<Integer> matrixAnswersKeys = new ArrayList<>();
			for (IntKeyValue keyValue : matrixAnswers) {
				if (!matrixAnswersKeys.contains(keyValue.getKey())) {
					matrixAnswersKeys.add(keyValue.getKey());
				}
			}
			Query query = session.createQuery("FROM LiveStatistics WHERE PAID IN (:matrixAnswersKeys)");
			query.setParameterList("matrixAnswersKeys", matrixAnswersKeys);
			List<LiveStatistics> stats = query.list();
			for (LiveStatistics candidate : stats) {
				for (IntKeyValue intKeyValue : matrixAnswers) {
					if (intKeyValue.getKey().equals(candidate.getPossibleAnswerId()) && intKeyValue.getValue().equals(candidate.getQuestionId())) {
						statistics.add(candidate);
					}
				}
			}
		}

		return statistics;
	}

	@Transactional
	public void getCompleteAnswers4Statistics(Survey survey, ResultFilter filter, HashMap<Integer, Integer> map, HashMap<Integer, HashMap<Integer, Integer>> mapMatrix) {
		Session session = sessionFactory.getCurrentSession();

		for (Question q : survey.getQuestions()) {
			if (q instanceof ChoiceQuestion) {
				ChoiceQuestion choice = (ChoiceQuestion) q;
				for (PossibleAnswer a : choice.getPossibleAnswers()) {
					SQLQuery query = session.createSQLQuery("SELECT NUM FROM LIVESTATISTICS WHERE PAID = :possibleAnswerId");
					query.setInteger("possibleAnswerId", a.getId());
					Object num = query.uniqueResult();
					int result = 0;
					if (num != null)
						if (num instanceof BigInteger) {
							result = ((BigInteger) num).intValue();
						} else if (num instanceof BigDecimal) {
							result = ((BigDecimal) num).intValue();
						} else if (num != null) {
							result = (Integer) num;
						}
					map.put(a.getId(), result);
				}
			} else if (q instanceof Matrix) {
				Matrix matrix = (Matrix) q;
				for (Element matrixQuestion : matrix.getQuestions()) {
					for (Element matrixAnswer : matrix.getAnswers()) {
						SQLQuery query = session.createSQLQuery("SELECT NUM FROM LIVESTATISTICS WHERE PAID = :possibleAnswerId AND QID = :questionId");
						query.setInteger("possibleAnswerId", matrixAnswer.getId());
						query.setInteger("questionId", matrixQuestion.getId());

						Object num = query.uniqueResult();
						int result = 0;
						if (num instanceof BigInteger) {
							result = ((BigInteger) num).intValue();
						} else if (num instanceof BigDecimal) {
							result = ((BigDecimal) num).intValue();
						} else if (num != null) {
							result = (Integer) num;
						}

						if (!mapMatrix.containsKey(matrixQuestion.getId()))
							mapMatrix.put(matrixQuestion.getId(), new HashMap<>());
						mapMatrix.get(matrixQuestion.getId()).put(matrixAnswer.getId(), result);

					}
				}
			}
		}
	}

	@Transactional(readOnly = true)
	public int getNumberOfAnswerSetsPublished(String surveyname, String uid) {
		Session session = sessionFactory.getCurrentSession();
		if (uid != null && uid.length() > 0) {

			SQLQuery query = session.createSQLQuery(
					"SELECT count(ANSWERS_SET.ANSWER_SET_ID) FROM ANSWERS_SET inner join SURVEYS s on  ANSWERS_SET.SURVEY_ID = s.SURVEY_ID where ANSWERS_SET.ISDRAFT = 0 AND s.SURVEY_UID = :uid AND s.ISDRAFT = 0");
			query.setString("uid", uid);

			return ConversionTools.getValue(query.uniqueResult());
		} else {
			SQLQuery query = session.createSQLQuery(
					"SELECT count(ANSWERS_SET.ANSWER_SET_ID) FROM ANSWERS_SET inner join SURVEYS s on  ANSWERS_SET.SURVEY_ID = s.SURVEY_ID where ANSWERS_SET.ISDRAFT = 0 AND  s.SURVEYNAME = :surveyname AND s.ISDRAFT = 0");
			query.setString("surveyname", surveyname);

			return ConversionTools.getValue(query.uniqueResult());
		}
	}

	@Transactional(readOnly = true)
	public int userContributionsToSurvey(Survey survey, User user) {
		Session session = sessionFactory.getCurrentSession();

		String queryString = "SELECT count(ans.ANSWER_SET_ID) from ANSWERS_SET ans inner join SURVEYS s on ans.SURVEY_ID = s.SURVEY_ID WHERE s.SURVEY_UID = :uid AND s.ISDRAFT = 0 AND ans.ISDRAFT = 0 AND (ans.RESPONDER_EMAIL = :mail1 OR ans.RESPONDER_EMAIL = :mail2)";
		SQLQuery query = session.createSQLQuery(queryString);
		query.setString("uid", survey.getUniqueId()).setString("mail1", user.getEmail()).setString("mail2", Tools.md5hash(user.getEmail()));

		int result = ConversionTools.getValue(query.uniqueResult());
		return result;
	}

	@Transactional(readOnly = true)
	public int getNumberOfAnswerSets(Survey survey, ResultFilter filter) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		if (!survey.getIsDraft() && filter.isEmpty())
			return getNumberOfAnswerSetsPublished(survey.getShortname(), survey.getUniqueId());

		HashMap<String, Object> parameters = new HashMap<>();

		String queryString = "";

		queryString = getSql("SELECT count(DISTINCT a1.AS_ID)", survey.getId(), filter, parameters, false, true);

		SQLQuery query = session.createSQLQuery(queryString);
		sqlQueryService.setParameters(query, parameters);

		return ConversionTools.getValue(query.uniqueResult());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void delete(AnswerSet answerSet) {

		// delete precomputed statistics and pdfs
		try {
			deleteStatisticsForSurvey(answerSet.getSurvey().getId());

			java.io.File folder = fileService.getSurveyExportsFolder(answerSet.getSurvey().getUniqueId());
			java.io.File target = new java.io.File(String.format("%s/publishedanswer%s.pdf", folder.getPath(), answerSet.getId()));

			if (target.exists())
				target.delete();

			target = new java.io.File(String.format("%sanswer%s.pdf", tempFileDir, answerSet.getUniqueCode()));
			if (target.exists())
				target.delete();

			for (Answer answer : answerSet.getAnswers()) {
				if (answer.getFiles() != null) {
					for (File f : answer.getFiles()) {
						// new file system
						java.io.File file = fileService.getSurveyFile(answerSet.getSurvey().getUniqueId(), f.getUid());
						if (file.exists()) {
							file.delete();
						}

						// old file system
						file = new java.io.File(fileDir + f.getUid());
						if (file.exists()) {
							file.delete();
						}
					}
				}
			}

			if (!answerSet.getIsDraft() && answerSet.getInvitationId() != null && answerSet.getInvitationId().length() > 0) {
				Invitation invitation = attendeeService.getInvitationByUniqueId(answerSet.getInvitationId());
				if (invitation != null && invitation.getAnswers() > 0) {
					invitation.setAnswers(invitation.getAnswers() - 1);
					attendeeService.update(invitation);
				}
			}

			if (!answerSet.getIsDraft()) {
				if (answerSet.getSurvey().getIsDraft()) {
					reportingService.addToDo(ToDo.DELETEDTESTCONTRIBUTION, answerSet.getSurvey().getUniqueId(), answerSet.getUniqueCode());
				} else {
					reportingService.addToDo(ToDo.DELETEDCONTRIBUTION, answerSet.getSurvey().getUniqueId(), answerSet.getUniqueCode());
				}
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		Session session = sessionFactory.getCurrentSession();
		session.delete(answerSet);
	}

	@Transactional(readOnly = true)
	public AnswerSet get(String uniqueCode) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT a FROM AnswerSet a WHERE a.isDraft = false AND a.uniqueCode = :uniqueCode order by date DESC").setString("uniqueCode", uniqueCode);
		@SuppressWarnings("unchecked")
		List<AnswerSet> list = query.list();
		if (list.size() == 0)
			return null;
		if (list.size() > 1)
			logger.warn("Multiple answerSets found for uniqueCode " + uniqueCode);

		return list.get(0);
	}

	@Transactional(readOnly = true)
	public AnswerSet getByInvitationCode(String invitationId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT a FROM AnswerSet a WHERE a.isDraft = false AND a.invitationId = :invitationId order by date DESC").setString("invitationId", invitationId);
		@SuppressWarnings("unchecked")
		List<AnswerSet> list = query.list();
		if (list.size() == 0)
			return null;
		if (list.size() > 1)
			logger.warn("Multiple answerSets found for invitationId " + invitationId);

		return list.get(0);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void save(WrongAttempts w) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(w);
	}

	@Transactional(readOnly = true)
	public WrongAttempts getWrongAttempts(String ip) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT a FROM WrongAttempts a WHERE a.ip = :ip").setString("ip", ip);
		@SuppressWarnings("unchecked")
		List<WrongAttempts> list = query.list();
		if (list.size() == 0)
			return null;
		if (list.size() > 1)
			throw new Exception("Multiple WrongAttempts found for ip " + ip);

		return list.get(0);
	}

	@Transactional(readOnly = false)
	public void deleteStatisticsForSurvey(int surveyId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("UPDATE Statistics s SET s.invalid = true WHERE s.surveyId = :surveyId").setInteger("surveyId", surveyId);
		query.executeUpdate();

		query = session.createQuery("DELETE FROM ExportCache c WHERE c.surveyId = :surveyId").setInteger("surveyId", surveyId);
		query.executeUpdate();
	}

	@Transactional
	public StatisticsRequest getStatisticRequest(int id) {
		Session session = sessionFactory.getCurrentSession();
		return (StatisticsRequest) session.get(StatisticsRequest.class, id);
	}

	@Transactional
	public void saveStatisticsRequest(StatisticsRequest sr) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(sr);
	}

	@Transactional
	public void deleteStatisticsRequest(StatisticsRequest sr) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(sr);
	}

	@Transactional
	public Statistics getStatisticsForFilterHash(int surveyId, String hash, boolean useEagerLoading) {
		Session session = sessionFactory.getCurrentSession();

		SQLQuery sqlQuery = session.createSQLQuery("SELECT MAX(ACCESS_ID) from STATISTICS WHERE SURVEYID = :surveyId AND FILTER = :hash");
		sqlQuery.setInteger("surveyId", surveyId).setString("hash", hash);

		int id = ConversionTools.getValue(sqlQuery.uniqueResult());
		if (id == 0)
			return null;

		Statistics result = (Statistics) session.get(Statistics.class, id);
		if (result != null) {
			if (result.getInvalid() != null && result.getInvalid()) {
				return null;
			}

			Hibernate.initialize(result.getRequestedRecordsPercent());
			Hibernate.initialize(result.getRequestedRecords());
			Hibernate.initialize(result.getTotalsPercent());
			Hibernate.initialize(result.getRequestedRecordsScore());
			Hibernate.initialize(result.getRequestedRecordsPercentScore());
			Hibernate.initialize(result.getMeanSectionScore());
			Hibernate.initialize(result.getBestSectionScore());
			Hibernate.initialize(result.getMaxSectionScore());
		}

		return result;
	}

	@Transactional(readOnly = false)
	public void save(Statistics s) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(s);
		session.flush();
	}

	@Transactional
	public Statistics getStatistics(int requestid) {
		StatisticsRequest sr = getStatisticRequest(requestid);

		if (sr == null)
			return null;

		Statistics statistics = getStatisticsForFilterHash(sr.getSurveyId(), sr.getFilter().getHash(sr.isAllanswers()), false);

		if (statistics != null) {
			deleteStatisticsRequest(sr);
		}

		return statistics;
	}

	public Statistics getStatistics(Survey survey, ResultFilter filter, boolean useEagerLoading, boolean allanswers, boolean asynchronous) throws Exception {
		filter = answerService.initialize(filter);
		Statistics statistics = getStatisticsForFilterHash(survey.getId(), filter.getHash(allanswers), useEagerLoading);

		if (statistics == null) {
			StatisticsCreator creator = (StatisticsCreator) context.getBean("statisticsCreator");
			creator.init(survey, filter, allanswers);

			if (asynchronous && !allanswers) {
				try {

					for (Runnable r : running) {
						StatisticsCreator s = (StatisticsCreator) r;
						if (s.getFilter().getHash(allanswers).equals(filter.getHash(allanswers))) {
							return null;
						}
					}

				} catch (java.util.ConcurrentModificationException cme) {
					// this can happen when a runner is added or removed while the list is checked
				}

				getAnswerPool().execute(creator);
				return null;

			} else {
				int counter = 0;
				while (statistics == null && counter < 120) {
					boolean found = false;
					try {
						for (Runnable r : running) {
							StatisticsCreator s = (StatisticsCreator) r;
							if (s.getFilter().getHash(allanswers).equals(filter.getHash(allanswers))) {
								found = true;
								break;
							}
						}
					} catch (java.util.ConcurrentModificationException cme) {
						// this can happen when a runner is added or removed while the list is checked
					}

					// if there is no computation running, start one
					if (!found) {
						if (asynchronous) {
							getAnswerPool().execute(creator);
						} else {
							creator.runSync();
						}
						found = true;
					}

					if (found) {
						try {
							Thread.sleep(5000);
							counter++;
						} catch (InterruptedException e) {
							logger.error(e.getLocalizedMessage(), e);
						}

						if (allanswers && !survey.isMissingElementsChecked()) {
							surveyService.CheckAndRecreateMissingElements(survey, filter);
						}

						statistics = getStatisticsForFilterHash(survey.getId(), filter.getHash(allanswers), useEagerLoading);
					}
				}
			}
		}

		return statistics;
	}

	@Transactional(readOnly = true)
	public int getNumberAnswersForValue(String value, int questionId, String questionUid, boolean surveyIsDraft, String answerSetUniqueCode) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(
				"select count(*) from ANSWERS a INNER JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = a.AS_ID INNER JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID where s.ISDRAFT = :isdraft AND ans.UNIQUECODE != :ansuid AND ((a.QUESTION_ID= :questionId and a.VALUE= :value and a.ANSWER_ISDRAFT=0) or (a.QUESTION_UID= :questionUid and a.VALUE= :value and a.ANSWER_ISDRAFT=0))")
				.setBoolean("isdraft", surveyIsDraft).setString("value", value).setString("ansuid", answerSetUniqueCode).setString("questionUid", questionUid).setInteger("questionId", questionId);
		return ConversionTools.getValue(query.uniqueResult());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveDraft(Draft draft) throws Exception {
		boolean saved = false;

		int counter = 1;

		draft.getAnswerSet().setUpdateDate(new Date());

		while (!saved) {
			try {
				internalSaveDraft(draft);

				// delete temporary files folder
				try {
					if (fileDir != null) {
						java.io.File directory = fileService.getSurveyUploadsFolder(draft.getAnswerSet().getSurvey().getUniqueId(), false);
						FileUtils.delete(directory);
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}

				saved = true;
			} catch (org.hibernate.exception.LockAcquisitionException ex) {
				logger.info("lock on draft/answerset table catched; retry counter: " + counter);
				counter++;

				if (counter > 60) {
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}

				Thread.sleep(1000);
			}
		}
	}

	@Transactional(readOnly = false)
	private void internalSaveDraft(Draft draft) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(draft);
	}

	@Transactional
	public Draft getDraft(int id) {
		Session session = sessionFactory.getCurrentSession();
		return (Draft) session.get(Draft.class, id);
	}

	@Transactional(readOnly = true)
	public Draft getDraft(String draftid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Draft d WHERE d.uniqueId = :uniqueId").setString("uniqueId", draftid);
		@SuppressWarnings("unchecked")
		List<Draft> list = query.list();
		if (list.size() == 0)
			return null;
		if (list.size() > 1)
			logger.error("Multiple drafts found for id " + draftid);

		return list.get(0);
	}

	@Transactional(readOnly = true)
	public Draft getDraftByAnswerUID(String uniqueCode) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Draft d WHERE d.answerSet.uniqueCode = :uniqueCode order by answerSet.date DESC").setString("uniqueCode", uniqueCode);
		@SuppressWarnings("unchecked")
		List<Draft> list = query.list();
		if (list.size() == 0)
			return null;
		if (list.size() > 1)
			logger.error("Multiple drafts found for answerset uniqueCode " + uniqueCode);

		return list.get(0);
	}

	@Transactional(readOnly = true)
	public Draft getDraftForInvitation(String uniqueCode) throws Exception {
		return internalGetDraftForInviation(uniqueCode);
	}

	@Transactional(readOnly = true)
	private Draft internalGetDraftForInviation(String uniqueCode) throws InterruptedException {
		Session session = sessionFactory.getCurrentSession();
		boolean saved = false;

		int counter = 1;

		while (!saved) {
			try {
				Query query = session.createQuery("FROM Draft d WHERE d.answerSet.invitationId = :uniqueCode").setString("uniqueCode", uniqueCode);
				@SuppressWarnings("unchecked")
				List<Draft> list = query.list();
				if (list.size() == 0)
					return null;

				return list.get(0);
			} catch (org.hibernate.exception.LockAcquisitionException ex) {
				logger.info("lock on draft table catched; retry counter: " + counter);
				counter++;

				if (counter > 60) {
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}

				Thread.sleep(1000);
			}
		}

		return null;
	}

	public String getFileDir() {
		return fileDir;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String[]> getFilesForQuestion(String uid, boolean draft) {
		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session.createSQLQuery(
				"SELECT ans.UNIQUECODE, f.FILE_UID, f.FILE_NAME FROM FILES f JOIN ANSWERS_FILES af ON f.FILE_ID = af.files_FILE_ID JOIN ANSWERS a ON af.ANSWERS_ANSWER_ID = a.ANSWER_ID JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = a.AS_ID AND ans.ISDRAFT = 0 JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID WHERE a.QUESTION_UID = :uid AND s.ISDRAFT = :draft");
		query.setString("uid", uid).setInteger("draft", draft ? 1 : 0);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String> getQuestionsWithUploadedFiles(Survey survey) {
		Set<String> uids = new HashSet<>();
		for (Element q : survey.getElements()) {
			if (q instanceof Upload) {
				uids.add("'" + q.getUniqueId() + "'");
			}
		}

		if (uids.size() == 0)
			return new ArrayList<>();

		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session.createSQLQuery(
				"SELECT DISTINCT a.QUESTION_UID FROM ANSWERS a LEFT JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID LEFT JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID WHERE ans.ISDRAFT = 0 AND s.ISDRAFT = :draft AND a.QUESTION_UID In ("
						+ StringUtils.collectionToCommaDelimitedString(uids) + ")");
		query.setInteger("draft", survey.getIsDraft() ? 1 : 0);
		return query.list();
	}

	@Transactional(readOnly = false)
	public String resetContribution(String code) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("SELECT a FROM AnswerSet a WHERE a.isDraft = false AND a.uniqueCode = :uniqueCode order by date DESC").setString("uniqueCode", code);
		@SuppressWarnings("unchecked")
		List<AnswerSet> list1 = query.list();

		if (list1.size() == 0)
			return null;

		AnswerSet answerSet = list1.get(0);

		if (answerSet != null) {
			query = session.createQuery("FROM Draft d WHERE d.answerSet.uniqueCode = :uniqueCode order by answerSet.date DESC").setString("uniqueCode", code);
			@SuppressWarnings("unchecked")
			List<Draft> list = query.list();

			if (answerSet.getInvitationId() != null) {
				attendeeService.decreaseInvitationAnswer(answerSet.getInvitationId());
			}

			if (list.size() == 0) {
				// there is no draft
				Draft draft = new Draft();
				String uniqueCode = UUID.randomUUID().toString();
				draft.setUniqueId(uniqueCode);
				answerSet.setIsDraft(true);
				draft.setAnswerSet(answerSet);
				session.saveOrUpdate(draft);

				reportingService.addToDo(ToDo.DELETEDCONTRIBUTION, answerSet.getSurvey().getUniqueId(), answerSet.getUniqueCode());
				return draft.getUniqueId();
			} else {
				Draft draft = list.get(0);

				if (draft != null) {
					String uid = draft.getUniqueId();
					session.delete(draft);
					session.flush();

					draft = new Draft();
					draft.setUniqueId(uid);
					answerSet.setIsDraft(true);
					draft.setAnswerSet(answerSet);

					session.saveOrUpdate(draft);
					reportingService.addToDo(ToDo.DELETEDCONTRIBUTION, answerSet.getSurvey().getUniqueId(), answerSet.getUniqueCode());
					return uid;
				}
			}
		}

		return null;
	}

	@Transactional(readOnly = false)
	public int getNumberOfDrafts(int id) {
		Session session = sessionFactory.getCurrentSession();
		List<Integer> allVersions = surveyService.getAllPublishedSurveyVersions(id);
		if (allVersions.size() == 0)
			return 0;
		String query = "SELECT COUNT(*) FROM ( SELECT * FROM ANSWERS_SET ans WHERE ans.SURVEY_ID IN (" + StringUtils.collectionToCommaDelimitedString(allVersions)
				+ ") GROUP BY ans.UNIQUECODE HAVING MIN(ans.ISDRAFT) = 1	) as dummy";
		Query q = session.createSQLQuery(query);
		return ConversionTools.getValue(q.uniqueResult());
	}

	@Transactional(readOnly = false)
	public int getNumberOfDrafts(String uid) {
		Session session = sessionFactory.getCurrentSession();
		List<Integer> allVersions = surveyService.getAllPublishedSurveyVersions(uid);
		if (allVersions.size() == 0)
			return 0;
		String query = "SELECT COUNT(*) FROM ( SELECT * FROM ANSWERS_SET ans WHERE ans.SURVEY_ID IN (" + StringUtils.collectionToCommaDelimitedString(allVersions)
				+ ") GROUP BY ans.UNIQUECODE HAVING MIN(ans.ISDRAFT) = 1	) as dummy";
		Query q = session.createSQLQuery(query);
		return ConversionTools.getValue(q.uniqueResult());
	}

	@Transactional(readOnly = false)
	public void setPublishingDates(Survey survey) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery("SELECT MIN(SURVEY_CREATED), MAX(SURVEY_CREATED) FROM SURVEYS WHERE SURVEY_UID = :uid AND ISDRAFT = 0");
		@SuppressWarnings("rawtypes")
		List result = query.setString("uid", survey.getUniqueId()).list();
		if (result.size() > 0) {
			Object[] a = (Object[]) result.get(0);
			survey.setFirstPublished((Date) a[0]);
			survey.setPublished((Date) a[1]);
		}

	}

	@Transactional(readOnly = true)
	public String serializeOriginal(Integer id) {
		StringBuilder result = new StringBuilder();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery("SELECT QUESTION_UID, VALUE FROM ANSWERS WHERE AS_ID = :id ORDER BY ANSWER_ID DESC");
		@SuppressWarnings("rawtypes")
		List answers = query.setInteger("id", id).list();

		for (Object o : answers) {
			Object[] a = (Object[]) o;
			result.append(" ").append(a[0]).append(":").append(a[1]).append(";");
		}

		return result.toString();
	}

	@Transactional(readOnly = true)
	public boolean getHasPublishedAnswers(String uid) {
		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session.createSQLQuery(
				"SELECT ANSWERS_SET.ANSWER_SET_ID FROM ANSWERS_SET inner join SURVEYS s on ANSWERS_SET.SURVEY_ID = s.SURVEY_ID where ANSWERS_SET.ISDRAFT = 0 AND s.SURVEY_UID = :uid AND s.ISDRAFT = 0 LIMIT 1");
		query.setString("uid", uid);

		@SuppressWarnings("rawtypes")
		List result = query.list();
		return result.size() > 0;
	}

	@Transactional(readOnly = true)
	public List<AnswerSet> getAnswersAndDrafts(int surveyId) {
		Session session = sessionFactory.getCurrentSession();

		String sql = "select DISTINCT ANSWER_SET_ID from ANSWERS_SET where SURVEY_ID = :surveyId";

		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger("surveyId", surveyId);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		List<AnswerSet> result = new ArrayList<>();

		for (Object o : res) {
			Integer i = ConversionTools.getValue(o);
			AnswerSet answerSet = (AnswerSet) session.get(AnswerSet.class, i);
			result.add(answerSet);
		}

		return result;
	}

	public String getDraftForEcasLogin(Survey survey, HttpServletRequest request) throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT d.DRAFT_UID FROM DRAFTS d JOIN ANSWERS_SET a ON d.answerSet_ANSWER_SET_ID = a.ANSWER_SET_ID WHERE (a.RESPONDER_EMAIL = :email or a.RESPONDER_EMAIL = :email2) AND a.SURVEY_ID IN (:ids)";
		SQLQuery query = session.createSQLQuery(sql);
		User user = sessionService.getCurrentUser(request, false, false);

		if (user == null)
			return null;

		List<Integer> ids = surveyService.getAllSurveyVersions(survey.getId());

		query.setString("email", user.getEmail());
		query.setString("email2", Tools.md5hash(user.getEmail()));
		query.setParameterList("ids", ids);

		@SuppressWarnings("unchecked")
		List<String> result = query.list();
		
		for (String draftuid : result) {
			Draft draft = getDraft(draftuid);			
			
			if (draft != null && !surveyService.answerSetExists(draft.getAnswerSet().getUniqueCode(), false, false))
			{	
				return draftuid;
			}
		}		

		return null;
	}

	public Map<Date, Integer> getAnswersPerDay(int surveyId, String span) {
		Session session = sessionFactory.getCurrentSession();

		List<Integer> allVersions = surveyService.getAllPublishedSurveyVersions(surveyId);

		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH));

		if (span.equalsIgnoreCase("month")) {
			cal.add(Calendar.DATE, -30);
		} else if (span.equalsIgnoreCase("week")) {
			cal.add(Calendar.DATE, -7);
		} else {
			cal.add(Calendar.YEAR, -20);
		}
		
		Date firstDay = cal.getTime();
		
		if (span.equalsIgnoreCase("total"))
		{
			Survey firstPublished = allVersions.size() > 0 ? surveyService.getSurvey(allVersions.get(0), true) : null;
			if (firstPublished != null) {
				firstDay = firstPublished.getCreated();
			}
		}

		String sql = "SELECT DATE(ANSWER_SET_DATE), count(*) FROM ANSWERS_SET WHERE SURVEY_ID IN (" + StringUtils.collectionToCommaDelimitedString(allVersions)
				+ ") AND ISDRAFT = 0 AND ANSWER_SET_DATE > :start GROUP BY DATE(ANSWER_SET_DATE) ORDER BY DATE(ANSWER_SET_DATE)";

		SQLQuery query = session.createSQLQuery(sql);
		query.setDate("start", cal.getTime());

		@SuppressWarnings("rawtypes")
		List res = query.list();

		Map<Date, Integer> result = new TreeMap<Date, Integer>();

		Date first = null;
		Date last = null;
		for (Object o : res) {
			Object[] a = (Object[]) o;
			last = (Date) a[0];
			result.put(last, ConversionTools.getValue(a[1]));

			if (first == null) {
				first = last;
			}
		}

		if (span.equalsIgnoreCase("week") || span.equalsIgnoreCase("month") || span.equalsIgnoreCase("total")) {			
			Date lastDay = DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH);

			if (first == null || first.after(firstDay)) {
				result.put(firstDay, 0);
			}

			if (last == null || last.before(lastDay)) {
				result.put(lastDay, 0);
			}
		}

		return result;
	}

	public int[] getContributionStatisticsForUser(Integer id) {
		Session session = sessionFactory.getCurrentSession();

		User user = administrationService.getUser(id);
		if (user == null)
			return null;

		List<String> allemails = user.getAllEmailAddresses();

		int[] result = new int[3];

		StringBuilder sqlb = new StringBuilder();
		sqlb.append(
				"SELECT count(*) FROM ( SELECT i.INVITATION_ID, i.ATTENDEE_INVITED FROM INVITATIONS i JOIN ATTENDEE a ON i.ATTENDEE_ID = a.ATTENDEE_ID JOIN PARTICIPANTS_ATTENDEE pa ON pa.attendees_ATTENDEE_ID = a.ATTENDEE_ID JOIN PARTICIPANTS p ON p.PARTICIPATION_ID = pa.PARTICIPANTS_PARTICIPATION_ID WHERE ");
		sqlb.append("p.PARTICIPATION_TYPE = 1 AND a.ATTENDEE_EMAIL IN (:emails) AND i.ATTENDEE_ANSWERS = 0");
		sqlb.append(
				" UNION SELECT i.INVITATION_ID, i.ATTENDEE_INVITED FROM INVITATIONS i JOIN ECASUSERS a ON i.ATTENDEE_ID = a.USER_ID JOIN PARTICIPANTS_ECASUSERS pa ON pa.ecasUsers_USER_ID = a.USER_ID JOIN PARTICIPANTS p ON p.PARTICIPATION_ID = pa.PARTICIPANTS_PARTICIPATION_ID WHERE ");
		sqlb.append("p.PARTICIPATION_TYPE = 2 AND a.USER_EMAIL IN (:emails) AND i.ATTENDEE_ANSWERS = 0 ) AS d");

		SQLQuery query = session.createSQLQuery(sqlb.toString());
		query.setParameterList("emails", allemails);
		result[0] = ConversionTools.getValue(query.uniqueResult());

		String sql = "SELECT COUNT(*) FROM ANSWERS_SET WHERE ISDRAFT = 0 AND RESPONDER_EMAIL IN (:emails)";
		query = session.createSQLQuery(sql);
		query.setParameterList("emails", allemails);

		result[1] = ConversionTools.getValue(query.uniqueResult());

		sql = "SELECT COUNT(*) FROM (SELECT COUNT(*) FROM ANSWERS_SET ans WHERE ans.RESPONDER_EMAIL IN (:emails) GROUP BY ans.UNIQUECODE HAVING min(ans.ISDRAFT) = 1 AND max(ans.ISDRAFT) = 1) as x";
		query = session.createSQLQuery(sql);
		query.setParameterList("emails", allemails);

		result[2] = ConversionTools.getValue(query.uniqueResult());

		return result;
	}

	@Transactional(readOnly = true)
	public int[] getAnswerStatistics(int surveyId) {
		int[] result = new int[3];

		Survey survey = surveyService.getSurvey(surveyId);

		List<Integer> allVersions = surveyService.getAllPublishedSurveyVersions(surveyId);

		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT count(*) FROM ANSWERS_SET ans WHERE ans.ISDRAFT = 0 AND ans.SURVEY_ID IN (" + StringUtils.collectionToCommaDelimitedString(allVersions) + ")";
		SQLQuery query = session.createSQLQuery(sql);
		result[0] = ConversionTools.getValue(query.uniqueResult());

		sql = "SELECT count(*) FROM ANSWERS_SET ans WHERE ans.ISDRAFT = 1 AND ans.SURVEY_ID IN (" + StringUtils.collectionToCommaDelimitedString(allVersions) + ")";
		query = session.createSQLQuery(sql);
		result[1] = ConversionTools.getValue(query.uniqueResult());

		sql = "SELECT count(*) FROM INVITATIONS i LEFT JOIN PARTICIPANTS p ON p.PARTICIPATION_ID = i.PARTICIPATIONGROUP_ID WHERE p.PARTICIPATION_SURVEY_UID = :uid AND i.ATTENDEE_ANSWERS = 0";
		query = session.createSQLQuery(sql);
		query.setString("uid", survey.getUniqueId());
		result[2] = ConversionTools.getValue(query.uniqueResult());

		return result;
	}

	@Transactional
	public int deleteOldDrafts(Date date) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT d.id from Draft d where d.answerSet.updateDate < :date");
		query.setDate("date", date).setMaxResults(1000);

		@SuppressWarnings("unchecked")
		List<Integer> drafts = query.list();

		int counter = 0;
		for (Integer draftid : drafts) {
			Draft draft = (Draft) session.get(Draft.class, draftid);
			for (Answer answer : draft.getAnswerSet().getAnswers()) {
				if (answer.getFiles() != null) {
					for (File f : answer.getFiles()) {
						// new file system
						java.io.File file = fileService.getSurveyFile(draft.getAnswerSet().getSurvey().getUniqueId(), f.getUid());
						if (file.exists()) {
							file.delete();
						}

						// old file system
						file = new java.io.File(fileDir + f.getUid());
						if (file.exists()) {
							file.delete();
						}
					}
				}
			}
			session.delete(draft);
			counter++;
		}

		return counter;
	}

	@Transactional
	public int deleteInvalidStatistics() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT s.id from Statistics s where s.invalid = true");

		@SuppressWarnings("unchecked")
		List<Integer> statistics = query.list();

		int counter = 0;
		for (Integer statid : statistics) {
			Statistics stat = (Statistics) session.get(Statistics.class, statid);
			session.delete(stat);
			counter++;
		}

		return counter;
	}

	@Transactional(readOnly = true)
	public List<AnswerSet> getAnswers(List<Integer> answerSetIDs, boolean initFiles) {
		Session session = sessionFactory.getCurrentSession();

		List<AnswerSet> result = new ArrayList<>();

		for (Integer i : answerSetIDs) {
			AnswerSet answerSet = (AnswerSet) session.get(AnswerSet.class, i);

			if (initFiles) {
				for (Answer answer : answerSet.getAnswers()) {
					Hibernate.initialize(answer.getFiles());
				}
			}

			result.add(answerSet);
		}

		return result;
	}

	@Transactional(readOnly = true)
	public Date getNewestTestAnswerDate(int surveyId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT max(a.updateDate) FROM AnswerSet a WHERE a.surveyId = " + surveyId + " AND a.isDraft = 0");
		Date result = (Date) query.uniqueResult();
		return result;
	}

	@Transactional(readOnly = true)
	public Date getNewestAnswerDate(int surveyId) {
		Session session = sessionFactory.getCurrentSession();
		List<Integer> allVersions = surveyService.getAllPublishedSurveyVersions(surveyId);
		Query query = session.createQuery("SELECT max(a.updateDate) FROM AnswerSet a WHERE a.surveyId IN (" + StringUtils.collectionToCommaDelimitedString(allVersions) + ") AND a.isDraft = 0");
		Date result = (Date) query.uniqueResult();
		return result;
	}

	@Transactional(readOnly = true)
	public ResultFilter initialize(ResultFilter filter) {
		if (filter.getId() == 0) {
			return filter;
		}
		Session session = sessionFactory.getCurrentSession();
		session.evict(filter);
		filter = (ResultFilter) session.merge(filter);
		Hibernate.initialize(filter.getFilterValues());
		Hibernate.initialize(filter.getExportedQuestions());
		Hibernate.initialize(filter.getVisibleQuestions());
		Hibernate.initialize(filter.getLanguages());
		return filter;
	}

	public String getDraftURL(AnswerSet answerSet, String draftid, User user) throws Exception {
		Survey survey = answerSet.getSurvey();
		String mode = survey.getIsDraft() ? "test" : "runner";
		String invitationId = answerSet.getInvitationId();
		String url = "";
		if (invitationId != null && invitationId.trim().length() > 0) {
			// the draft comes from an invitation
			Invitation invitation = attendeeService.getInvitationByUniqueId(invitationId);
			ParticipationGroup group = participationService.get(invitation.getParticipationGroupId());

			if (group.getType() == ParticipationGroupType.Token) {
				url = serverPrefix + "runner/" + survey.getUniqueId() + "/" + invitation.getUniqueId();
			} else {
				url = serverPrefix + "runner/invited/" + invitation.getParticipationGroupId() + "/" + invitation.getUniqueId();
			}

		} else if (mode.equalsIgnoreCase("test")) {
			url = serverPrefix + survey.getShortname() + "/management/test?draftid=" + draftid;
		} else if (mode.equalsIgnoreCase("runner")) {
			if (survey.getEcasSecurity() && user != null) {
				url = serverPrefix + "runner/" + survey.getUniqueId();
			} else {
				url = serverPrefix + "runner/" + survey.getUniqueId() + "?draftid=" + draftid;
			}
		}
		return url;
	}
}
