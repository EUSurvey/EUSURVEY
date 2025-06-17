package com.ec.survey.service;

import com.ec.survey.model.*;
import com.ec.survey.model.delphi.*;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import org.hibernate.query.Query;
import org.hibernate.query.NativeQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.owasp.esapi.ESAPI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service("answerExplanationService")
public class AnswerExplanationService extends BasicService {

	public static final String DELETED_DELPHI_COMMENT_WITH_REPLIES_TEXT = "[DELETED]";

	@Transactional
	public void deleteExplanationByAnswerSet(AnswerSet answerSet) {

		final Integer answerSetId = answerSet.getId();
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("DELETE FROM AnswerExplanation WHERE answerSetId = :answerId")
				.setInteger("answerId", answerSetId);
		query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public AnswerExplanation getExplanation(int answerSetId, String questionUid) {

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery(
				"SELECT ex FROM AnswerExplanation ex WHERE answerSetId = :answerSetId AND questionUid = :questionUid")
				.setInteger("answerSetId", answerSetId).setString("questionUid", questionUid);
		AnswerExplanation explanation = (AnswerExplanation) query.uniqueResult();
		if (explanation == null) {
			throw new NoSuchElementException();
		}
		return explanation;
	}

	@Transactional(readOnly = true)
	public AnswerExplanation getExplanationIfPossible(int answerSetId, String questionUid) {

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery(
				"SELECT ex FROM AnswerExplanation ex WHERE answerSetId = :answerSetId AND questionUid = :questionUid")
				.setInteger("answerSetId", answerSetId).setString("questionUid", questionUid);
		AnswerExplanation explanation = (AnswerExplanation) query.uniqueResult();
		return explanation;
	}

	@Transactional(readOnly = true)
	public AnswerExplanation getExplanation(int explanationId) {

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery(
				"SELECT ex FROM AnswerExplanation ex WHERE id = :explanationId")
				.setInteger("explanationId", explanationId);
		AnswerExplanation explanation = (AnswerExplanation) query.uniqueResult();
		if (explanation == null) {
			throw new NoSuchElementException();
		}
		return explanation;
	}

	@Transactional(readOnly = true)
	public Map<Integer, Map<String, String>> getAllExplanations(Survey survey) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createSQLQuery(
				"SELECT ex.ANSWER_SET_ID, ex.QUESTION_UID, ex.TEXT FROM ANSWERS_EXPLANATIONS ex JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ex.ANSWER_SET_ID JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID WHERE s.SURVEY_UID = :surveyUid AND s.ISDRAFT = :draft")
				.setBoolean("draft", survey.getIsDraft()).setString("surveyUid", survey.getUniqueId());

		Map<Integer, Map<String, String>> result = new HashMap<>();

		@SuppressWarnings("rawtypes")
		List res = query.list();

		for (Object o : res) {
			Object[] a = (Object[]) o;

			int answerSetId = ConversionTools.getValue(a[0]);
			String questionUid = (String) a[1];
			String explanation = (String) a[2];

			if (!result.containsKey(answerSetId)) {
				result.put(answerSetId, new HashMap<String, String>());
			}

			result.get(answerSetId).put(questionUid, explanation);
		}

		return result;
	}
	
	@Transactional(readOnly = true)
	public Map<String, Integer> getAllLikesForExplanation(Survey survey) {
		Map<String, Integer> result = new HashMap<>();
		
		List<AnswerExplanation> explanations = getExplanationsOfSurvey(survey.getUniqueId(), survey.getIsDraft());
		
		for (AnswerExplanation explanation: explanations) {
			List<DelphiExplanationLike> likes = answerExplanationService.loadExplanationLikesUIDs(explanation.getId());
			result.put(explanation.getAnswerSetId() + "-" + explanation.getQuestionUid(), likes.size());
		}
		
		return result;
	}

	@Transactional(readOnly = true)
	public List<DelphiExplanationLike> getAllLikesForExplanations(List<AnswerExplanation> explanations) {

		var session = sessionFactory.getCurrentSession();
		var query = session.createQuery("FROM DelphiExplanationLike WHERE answerExplanationId in :explanationIds");
		query.setParameterList("explanationIds", explanations.stream().map(ex -> ex.getId()).collect(Collectors.toList()));

		return query.list();
	}

	@Transactional(readOnly = true)
	public List<AnswerExplanation> getExplanationsOfSurvey(final String surveyUid, final boolean draft) {

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createSQLQuery(
				"SELECT f.FILE_ID, f.FILE_NAME, f.FILE_UID, ex.ANSWER_EXPLANATION_ID, ex.ANSWER_SET_ID, ex.QUESTION_UID, ex.TEXT FROM ANSWERS_EXPLANATIONS ex "
						+ "LEFT JOIN ANSWERS_EXPLANATIONS_FILES aef ON ex.ANSWER_EXPLANATION_ID = aef.ANSWERS_EXPLANATIONS_ANSWER_EXPLANATION_ID "
						+ "LEFT JOIN FILES f ON aef.files_FILE_ID = f.FILE_ID "
						+ "JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ex.ANSWER_SET_ID "
						+ "JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID "
						+ "WHERE s.SURVEY_UID = :surveyUid AND s.ISDRAFT = :draft")
				.setString("surveyUid", surveyUid).setBoolean("draft", draft);

		@SuppressWarnings("rawtypes")
		final List queryResult = query.list();

		Map<Integer, AnswerExplanation> explanations = new HashMap<>();
		for (Object row : queryResult) {
			final Object[] element = (Object[]) row;

			final int fileId = ConversionTools.getValue(element[0]);

			final int answerExplanationId = ConversionTools.getValue(element[3]);
			final int answerExplanationAnswerSetId = ConversionTools.getValue(element[4]);
			final String answerExplanationQuestionUid = (String) element[5];
			final String answerExplanationText = (String) element[6];

			if (!explanations.containsKey(answerExplanationId)) {
				final AnswerExplanation explanation = new AnswerExplanation();
				explanation.setId(answerExplanationId);
				explanation.setAnswerSetId(answerExplanationAnswerSetId);
				explanation.setQuestionUid(answerExplanationQuestionUid);
				explanation.setText(answerExplanationText);
				explanations.put(answerExplanationId, explanation);
			}

			if (fileId > 0) {
				final String fileName = (String) element[1];
				final String fileUid = (String) element[2];

				final File file = new File();
				file.setId(fileId);
				file.setName(fileName);
				file.setUid(fileUid);
				explanations.get(answerExplanationId).addFile(file);
			}
		}

		return new ArrayList<>(explanations.values());
	}

	@Transactional(readOnly = true)
	public List<AnswerComment> getCommentsOfSurvey(final String surveyUid, final boolean draft) {

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session
				.createSQLQuery("SELECT ac.ANSWER_COMMENT_ID " + "FROM ANSWERS_COMMENTS ac "
						+ "JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ac.ANSWER_SET_ID "
						+ "JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID "
						+ "WHERE s.SURVEY_UID = :surveyUid AND s.ISDRAFT = :draft")
				.setString("surveyUid", surveyUid).setBoolean("draft", draft);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		final List<AnswerComment> comments = new ArrayList<>();

		for (Object o : res) {
			int commentId = ConversionTools.getValue(o);
			AnswerComment comment = getComment(commentId);
			comments.add(comment);
		}

		return comments;
	}

	@Transactional(readOnly = true)
	public List<AnswerExplanation> getExplanations(int answerSetId) {

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("SELECT ex FROM AnswerExplanation ex WHERE answerSetId = :answerSetId")
				.setInteger("answerSetId", answerSetId);
		@SuppressWarnings("unchecked")
		List<AnswerExplanation> explanations = (List<AnswerExplanation>) query.list();
		return explanations;
	}

	@Transactional(readOnly = true)
	public DelphiContributions getDelphiContributions(ChoiceQuestion question, DelphiTableOrderBy orderBy, int limit,
			int offset) {
		return getDelphiContributions(Collections.singletonList(question.getUniqueId()), question.getUniqueId(),
				question.getSurvey().getIsDraft(), orderBy, limit, offset);
	}

	@Transactional(readOnly = true)
	public DelphiContributions getDelphiContributions(Matrix question, DelphiTableOrderBy orderBy, int limit,
			int offset) {
		List<String> uids = question.getQuestions().stream().map(Element::getUniqueId).collect(Collectors.toList());
		return getDelphiContributions(uids, question.getUniqueId(), question.getSurvey().getIsDraft(), orderBy, limit,
				offset);
	}

	@Transactional(readOnly = true)
	public DelphiContributions getDelphiContributions(RatingQuestion question, DelphiTableOrderBy orderBy, int limit,
			int offset) {
		List<String> uids = question.getQuestions().stream().map(Element::getUniqueId).collect(Collectors.toList());
		return getDelphiContributions(uids, question.getUniqueId(), question.getSurvey().getIsDraft(), orderBy, limit,
				offset);
	}

	@Transactional(readOnly = true)
	public DelphiContributions getDelphiContributions(Table question, DelphiTableOrderBy orderBy, int limit,
			int offset) {
		return getDelphiContributions(Collections.singletonList(question.getUniqueId()), question.getUniqueId(),
				question.getSurvey().getIsDraft(), orderBy, limit, offset);
	}

	@Transactional(readOnly = true)
	public DelphiContributions getDelphiContributions(Collection<String> questionUids, String mainQuestionUid,
			boolean isDraft, DelphiTableOrderBy orderBy, int limit, int offset) {
		String orderByClauseInner;
		String orderByClauseOuter;

		switch (orderBy) {
			case AnswersAsc:
			case AnswersDesc:
			case ExplanationsMostLiked:
			case ExplanationsLessLiked:
				//has to be done later on; using UpdateDesc as default for now

			case UpdateDesc:
				orderByClauseInner = "aset.ANSWER_SET_UPDATE DESC";
				orderByClauseOuter = "`update` DESC, answerSetId";
				break;

			case UpdateAsc:
				orderByClauseInner = "aset.ANSWER_SET_UPDATE ASC";
				orderByClauseOuter = "`update` ASC, answerSetId";
				break;

			default:
				throw new IllegalStateException("Unexpected value: " + orderBy);
		}

		String contributionsQueryText = "" + "SELECT\n" + "    aset.ANSWER_SET_ID answerSetId,\n"
				+ "    aset.UNIQUECODE answerSetUniqueCode,\n" + "    aset.ANSWER_SET_UPDATE `update`,\n"
				+ "    a.VALUE value,\n" + "    a.PA_UID answerUid,\n" + "    a.QUESTION_UID questionUid,\n"
				+ "    a.ANSWER_COL `column`,\n" + "    a.ANSWER_ROW `row`,\n"
				+ "    COALESCE(ex.TEXT, main_explanation.TEXT) explanation\n" + "FROM ANSWERS a\n" + "JOIN (\n" +
				// select all answers sets that are relevant for this query
				"    SELECT\n" + "        aset.ANSWER_SET_ID,\n" + "        aset.UNIQUECODE,\n"
				+ "        aset.ANSWER_SET_UPDATE\n" + "    FROM ANSWERS a\n"
				+ "    JOIN ANSWERS_SET aset ON a.AS_ID = aset.ANSWER_SET_ID\n"
				+ "    JOIN SURVEYS s ON aset.SURVEY_ID = s.SURVEY_ID\n" + "    WHERE a.QUESTION_UID IN :questionUids\n"
				+ "      AND s.ISDRAFT = :isDraft\n" + "    GROUP BY aset.ANSWER_SET_ID, aset.ANSWER_SET_UPDATE\n"
				+ "    ORDER BY " + orderByClauseInner + "\n" +
				// pagination
				"    LIMIT :limit OFFSET :offset\n" + ") AS aset ON a.AS_ID = aset.ANSWER_SET_ID\n" +
				// add explanations
				"LEFT JOIN ANSWERS_EXPLANATIONS ex ON a.QUESTION_UID = ex.QUESTION_UID AND ex.ANSWER_SET_ID = a.AS_ID\n"
				+
				// add explanation of main question (i.e. for ratings)
				"LEFT JOIN (\n" + "    SELECT TEXT, ANSWER_SET_ID\n" + "    FROM ANSWERS_EXPLANATIONS\n"
				+ "    WHERE QUESTION_UID = :mainQuestionUid\n"
				+ ") AS main_explanation ON a.AS_ID = main_explanation.ANSWER_SET_ID\n" +
				// filter by question
				"WHERE a.QUESTION_UID IN :questionUids\n" +
				// sort data as required
				"ORDER BY " + orderByClauseOuter + ", `row`, `column`";

		Session session = sessionFactory.getCurrentSession();
		NativeQuery contributionsQuery = session.createSQLQuery(contributionsQueryText);
		contributionsQuery.setResultTransformer(Transformers.aliasToBean(DelphiContribution.class));
		contributionsQuery.setParameterList("questionUids", questionUids);
		contributionsQuery.setBoolean("isDraft", isDraft);
		contributionsQuery.setString("mainQuestionUid", mainQuestionUid);
		contributionsQuery.setInteger("limit", limit);
		contributionsQuery.setInteger("offset", offset);

		@SuppressWarnings("unchecked")
		List<DelphiContribution> contributions = contributionsQuery.list();

		int totalCount = getTotalDelphiContributions(questionUids, isDraft);
		return new DelphiContributions(totalCount, contributions);
	}
	
	
	@Transactional(readOnly = true)
	public List<String> getDelphiDependentAnswers(String dependentElementUid, int answerSetId) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT VALUE FROM ANSWERS a WHERE a.QUESTION_UID = :questionUid AND a.AS_ID = :answerSetId";
		NativeQuery query = session.createSQLQuery(sql);
		query.setString("questionUid", dependentElementUid);
		query.setInteger("answerSetId", answerSetId);
		
		@SuppressWarnings("unchecked")
		List<String> result = query.list();
		
		return result;
	}

	@Transactional(readOnly = true)
	public int getTotalDelphiContributions(Collection<String> questionUids, boolean isDraft) {
		String totalCountQueryText = "SELECT COUNT(DISTINCT aset.ANSWER_SET_ID) FROM ANSWERS a\n"
				+ "JOIN ANSWERS_SET aset ON a.AS_ID = aset.ANSWER_SET_ID\n"
				+ "JOIN SURVEYS s ON aset.SURVEY_ID = s.SURVEY_ID\n"
				+ "WHERE a.QUESTION_UID IN :questionUids AND s.ISDRAFT = :isDraft";

		Session session = sessionFactory.getCurrentSession();
		NativeQuery totalCountQuery = session.createSQLQuery(totalCountQueryText);
		totalCountQuery.setParameterList("questionUids", questionUids);
		totalCountQuery.setBoolean("isDraft", isDraft);
		return ((BigInteger) totalCountQuery.uniqueResult()).intValue();
	}

	@Transactional
	public void createUpdateOrDeleteExplanations(AnswerSet answerSet) throws Exception {
		final Session session = sessionFactory.getCurrentSession();

		final Survey survey = answerSet.getSurvey();
		final String surveyUid = survey.getUniqueId();
		for (Question question : survey.getQuestions()) {
			final String questionUid = question.getUniqueId();
			if (question.getIsDelphiQuestion()) {
				AnswerSet.ExplanationData explanationData = answerSet.getExplanations().get(questionUid);
				if (explanationData == null) {
					continue;
				}

				List<Answer> answers;
				if (question instanceof Matrix) {
					Matrix matrix = (Matrix) question;
					answers = answerSet.getMatrixAnswers(matrix);
				} else if (question instanceof RatingQuestion) {
					RatingQuestion rating = (RatingQuestion) question;
					answers = answerSet.getRatingAnswers(rating);
				} else {
					answers = answerSet.getAnswers(questionUid);
				}

				AnswerExplanation explanation;
				boolean hasNoLinkedAnswers = answers.isEmpty();
				try {
					explanation = getExplanation(answerSet.getId(), questionUid);

					if (hasNoLinkedAnswers) {
						fileService.deleteUploadedExplanationFiles(surveyUid, answerSet.getUniqueCode(), questionUid);
						fileService.deleteFilesFromDiskAndDatabase(surveyUid, explanation.getFiles());
						session.delete(explanation);
						continue;
					}
				} catch (NoSuchElementException ex) {
					if (hasNoLinkedAnswers) {
						fileService.deleteUploadedExplanationFiles(surveyUid, answerSet.getUniqueCode(), questionUid);
						explanationData.files.clear();
					}
					if (explanationData.text.length() == 0 && explanationData.files.isEmpty()) {
						continue;
					}

					explanation = new AnswerExplanation(answerSet.getId(), questionUid);
				}

				// special case: if the participant adds an explanation why she is outside the
				// median
				DelphiMedian median = null;
				if (explanationData.text != null && explanationData.text.length() > 0
						&& !explanationData.text.equalsIgnoreCase(explanation.getText()) && !answers.isEmpty()) {
					if (question instanceof SingleChoiceQuestion) {
						SingleChoiceQuestion choiceQuestion = (SingleChoiceQuestion) question;
						if (choiceQuestion.getUseLikert() && choiceQuestion.getMaxDistance() > -1) {
							median = answerService.getMedian(survey, choiceQuestion, answers.get(0), null);						
						}
					} else if (question instanceof NumberQuestion) {
						NumberQuestion numberQuestion = (NumberQuestion) question;
						if (numberQuestion.isSlider() && numberQuestion.getMaxDistance() > -1) {
							median = answerService.getMedian(survey, numberQuestion, answers.get(0), null);
						}
					}
					if (median != null && median.isMaxDistanceExceeded()) {
						
						if (answerSet.getMedianWarningVisible())
						{
							explanation.setChangedForMedian(true);
							answerSet.setChangedForMedian(true);
						} else if (explanation.getChangedForMedian() == null || !explanation.getChangedForMedian()) {
							answerSet.setChangeExplanationText(true);
						}
					}
				}

				explanation.setText(explanationData.text);
				explanation.setFiles(explanationData.files);

				session.saveOrUpdate(explanation);
				session.flush();
			}
		}
	}

	@Transactional
	public void createComments(AnswerSet answerSet) {
		if (answerSet.getComments() != null && !answerSet.getComments().isEmpty()) {
			// this should only happen during a import survey operation for delphi surveys

			for (String surveyUid : answerSet.getComments().keySet()) {
				for (AnswerComment comment : answerSet.getComments().get(surveyUid)) {
					comment.setAnswerSetId(answerSet.getId());
					answerExplanationService.saveOrUpdateComment(comment);
				}
			}
		}
	}

	@Transactional
	public void saveOrUpdateComment(AnswerComment comment) {
		final Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(comment);
	}

	@Transactional
	public void saveOrUpdateExplanation(AnswerExplanation explanation) {
		final Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(explanation);
	}

	@Transactional
	public void deleteComment(AnswerComment comment) {
		final Session session = sessionFactory.getCurrentSession();
		session.delete(comment);
	}

	@Transactional(readOnly = true)
	public List<AnswerComment> loadComments(int answerSetId, String questionUid) {
		final Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"FROM AnswerComment WHERE answerSetId = :answerSetId and questionUid = :questionUid ORDER BY id");
		query.setInteger("answerSetId", answerSetId).setString("questionUid", questionUid);

		@SuppressWarnings("unchecked")
		List<AnswerComment> list = query.list();

		return list;
	}

	@Transactional
	public DelphiCommentLike getCommentLike(int answerCommentId, String uniqueCode) {
		final Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"FROM DelphiCommentLike WHERE answerCommentId = :answerCommentId and uniqueCode = :uniqueCode ORDER BY id");
		query.setInteger("answerCommentId", answerCommentId).setString("uniqueCode", uniqueCode);

		return (DelphiCommentLike) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public List<DelphiCommentLike> getAllLikesForComments(List<AnswerComment> comments) {
		var session = sessionFactory.getCurrentSession();

		var query = session.createQuery("FROM DelphiCommentLike WHERE answerCommentId IN :commentIds", DelphiCommentLike.class);
		query.setParameterList("commentIds", comments.stream().map(co -> co.getId()).collect(Collectors.toList()));

		return query.list();
	}

	@Transactional
	public DelphiExplanationLike getExplanationLike(int answerExplanationId, String uniqueCode) {
		final Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"FROM DelphiExplanationLike WHERE answerExplanationId = :answerExplanationId and uniqueCode = :uniqueCode ORDER BY id");
		query.setInteger("answerExplanationId", answerExplanationId).setString("uniqueCode", uniqueCode);

		return (DelphiExplanationLike) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public List<DelphiCommentLike> loadCommentLikes(int answerCommentId) {
		final Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"FROM DelphiCommentLike WHERE answerCommentId = :answerCommentId ORDER BY id");
		query.setInteger("answerCommentId", answerCommentId);

		@SuppressWarnings("unchecked")
		List<DelphiCommentLike> list = query.list();

		return list;
	}

	@Transactional(readOnly = true)
	public List<String> loadCommentLikesUIDs(int answerCommentId) {
		final Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"SELECT c.uniqueCode FROM DelphiCommentLike c WHERE answerCommentId = :answerCommentId ORDER BY id");
		query.setInteger("answerCommentId", answerCommentId);

		@SuppressWarnings("unchecked")
		List<String> list = query.list();

		return list;
	}

	@Transactional(readOnly = true)
	public List<DelphiExplanationLike> loadExplanationLikesUIDs(int answerExplanationId) {
		final Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"FROM DelphiExplanationLike WHERE answerExplanationId = :answerExplanationId");
		query.setInteger("answerExplanationId", answerExplanationId);

		List<DelphiExplanationLike> list = query.list();
		return list;
	}

	@Transactional(readOnly = true)
	public List<String> loadExplanationLikes(int answerExplanationId) {
		final Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"SELECT e.uniqueCode FROM DelphiExplanationLike e WHERE answerExplanationId = :answerExplanationId ORDER BY id");
		query.setInteger("answerExplanationId", answerExplanationId);

		@SuppressWarnings("unchecked")
		List<String> list = query.list();

		return list;
	}

	@Transactional
	public void addCommentLike(DelphiCommentLike delphiCommentLike) {
		final Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(delphiCommentLike);
	}

	@Transactional
	public void addExplanationLike(DelphiExplanationLike delphiExplanationLike) {
		final Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(delphiExplanationLike);
	}

	@Transactional
	public void deleteCommentLike(DelphiCommentLike delphiCommentLike) {
		final Session session = sessionFactory.getCurrentSession();
		session.delete(delphiCommentLike);
	}

	@Transactional
	public void deleteExplanationLike(DelphiExplanationLike delphiExplanationLike) {
		final Session session = sessionFactory.getCurrentSession();
		session.delete(delphiExplanationLike);
	}

	@Transactional
	public void deleteCommentsForDeletedAnswers(final AnswerSet answerSet) {

		final Session session = sessionFactory.getCurrentSession();
		for (Question question : answerSet.getSurvey().getQuestions()) {
			
			if (question.getIsDelphiQuestion()) {

				if (question instanceof Matrix) {
					Matrix matrix = (Matrix) question;
					if (!answerSet.getMatrixAnswers(matrix).isEmpty()) {
						continue;
					}
				} else if (question instanceof RatingQuestion) {
					RatingQuestion rating = (RatingQuestion) question;
					if (!answerSet.getRatingAnswers(rating).isEmpty()) {
						continue;
					}
				} else {
					if (!answerSet.getAnswers(question.getUniqueId()).isEmpty()) {
						continue;
					}
				}
				
				final Query replyDeletionQuery = session.createQuery("DELETE FROM AnswerComment "
						+ "WHERE answerSetId = :answerSetId AND questionUid = :questionUid AND parent IS NOT NULL")
						.setInteger("answerSetId", answerSet.getId()).setString("questionUid", question.getUniqueId());
				replyDeletionQuery.executeUpdate();

				final Query commentDeletionQuery = session
						.createQuery("DELETE FROM AnswerComment "
								+ "WHERE answerSetId = :answerSetId AND questionUid = :questionUid AND parent IS NULL")
						.setInteger("answerSetId", answerSet.getId()).setString("questionUid", question.getUniqueId());
				commentDeletionQuery.executeUpdate();
			}
		}
	}

	@Transactional
	public void deleteCommentsOfSurvey(final int surveyId) {

		final Session session = sessionFactory.getCurrentSession();

		final Query replyDeletionQuery = session.createSQLQuery("DELETE ac.* " + "FROM ANSWERS_COMMENTS ac "
				+ "JOIN ANSWERS_SET an ON an.ANSWER_SET_ID = ac.ANSWER_SET_ID "
				+ "WHERE ac.PARENT IS NOT NULL AND an.SURVEY_ID = :id").setInteger("id", surveyId);
		replyDeletionQuery.executeUpdate();

		final Query commentDeletionQuery = session.createSQLQuery("DELETE ac.* " + "FROM ANSWERS_COMMENTS ac "
				+ "JOIN ANSWERS_SET an ON an.ANSWER_SET_ID = ac.ANSWER_SET_ID "
				+ "WHERE ac.PARENT IS NULL AND an.SURVEY_ID = :id").setInteger("id", surveyId);
		commentDeletionQuery.executeUpdate();
	}

	@Transactional
	public void deleteExplanationFilesOfSurvey(final int surveyId) {

		final Session session = sessionFactory.getCurrentSession();

		final Query explanationFilesRetrievalQuery = session.createSQLQuery("SELECT f.FILE_UID " + "FROM FILES f "
				+ "JOIN ANSWERS_EXPLANATIONS_FILES aef ON aef.files_FILE_ID = f.FILE_ID "
				+ "JOIN ANSWERS_EXPLANATIONS ae ON ae.ANSWER_EXPLANATION_ID = aef.ANSWERS_EXPLANATIONS_ANSWER_EXPLANATION_ID "
				+ "JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ae.ANSWER_SET_ID " + "WHERE ans.SURVEY_ID = :id")
				.setInteger("id", surveyId);
		final List<String> fileUids = explanationFilesRetrievalQuery.list();

		final Query explanationFilesDeletionQuery12 = session.createSQLQuery("DELETE aef.* "
				+ "FROM ANSWERS_EXPLANATIONS_FILES aef "
				+ "JOIN ANSWERS_EXPLANATIONS ae ON ae.ANSWER_EXPLANATION_ID = aef.ANSWERS_EXPLANATIONS_ANSWER_EXPLANATION_ID "
				+ "JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ae.ANSWER_SET_ID " + "WHERE ans.SURVEY_ID = :id")
				.setInteger("id", surveyId);
		explanationFilesDeletionQuery12.executeUpdate();

		if (fileUids.isEmpty())
			return;
		final Query explanationFilesDeletionQuery2 = session
				.createSQLQuery("DELETE f.* " + "FROM FILES f " + "WHERE f.FILE_UID IN :ids")
				.setParameterList("ids", fileUids);
		explanationFilesDeletionQuery2.executeUpdate();
	}

	@Transactional
	public void deleteExplanationsOfSurvey(final int surveyId) {

		final Session session = sessionFactory.getCurrentSession();

		final Query query = session
				.createSQLQuery("DELETE ae.* " + "FROM ANSWERS_EXPLANATIONS ae "
						+ "JOIN ANSWERS_SET an ON ae.ANSWER_SET_ID = an.ANSWER_SET_ID " + "WHERE an.SURVEY_ID = :id")
				.setInteger("id", surveyId);
		query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public Map<String, String> getUserAliases(String surveyUid) {
		final Session session = sessionFactory.getCurrentSession();

		Query query = session.createSQLQuery(
				"SELECT DISTINCT ac.ANSWER_SET_CODE FROM ANSWERS_COMMENTS ac JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ac.ANSWER_SET_ID JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID WHERE s.SURVEY_UID = :surveyuid ORDER BY ac.COMMENT_DATE DESC");
		query.setString("surveyuid", surveyUid);

		@SuppressWarnings("unchecked")
		List<String> list = query.list();

		Map<String, String> result = new HashMap<>();

		for (String code : list) {
			result.put(code, "User " + (result.size() + 1));
		}

		return result;
	}

	@Transactional(readOnly = true)
	public String getDiscussion(int answerSetId, String questionUid, boolean useHtml, Map<String, String> usersByUid) {
		List<AnswerComment> comments = loadComments(answerSetId, questionUid);
		StringBuilder s = new StringBuilder();

		Map<Integer, List<AnswerComment>> commentsByParent = new HashMap<>();

		for (AnswerComment comment : comments) {
			if (comment.getParent() == null) {
				commentsByParent.put(comment.getId(), new ArrayList<>());
				commentsByParent.get(comment.getId()).add(comment);
			} else {
				commentsByParent.get(comment.getParent().getId()).add(comment);
			}
		}

		for (List<AnswerComment> list : commentsByParent.values()) {
			boolean first = true;
			for (AnswerComment comment : list) {
				List<DelphiCommentLike> likes = loadCommentLikes(comment.getId());
				String userPrefix = "";
				
				if (!comment.getText().equalsIgnoreCase(DELETED_DELPHI_COMMENT_WITH_REPLIES_TEXT)) {
					userPrefix = usersByUid.get(comment.getUniqueCode()) + ": ";
				}

				if (useHtml) {
					s.append("<div class='");
					if (first) {
						s.append("comment");
					} else {
						s.append("reply");
					}

					s.append("' data-id='").append(comment.getId()).append("' data-unique-code='")
							.append(comment.getUniqueCode()).append("'>").append("<span>").append("<span>").append(userPrefix)
							.append(ESAPI.encoder().encodeForHTML(comment.getText())).append("</span>");

					if (first) {
						s.append("<div> <span style='white-space:nowrap;'> <span>" + likes.size() + "</span> </span> </div>");
						first = false;
					}

					s.append("</span>").append("</div>");
				} else {
					if (first) {
						if (s.length() != 0) {
							s.append("\n");
						}
						s.append(userPrefix).append(comment.getText());
						first = false;

						s.append("\n   Likes: " + likes.size());
					} else {
						s.append("\n   ").append(userPrefix).append(comment.getText());
					}
				}
			}
		}

		return s.toString();
	}

	@Transactional(readOnly = true)
	public String getFormattedExplanationWithFiles(final int answerSetId, final String questionUid,
			final String surveyUid, final boolean useHtml) {

		final AnswerExplanation explanation = answerExplanationService.getExplanation(answerSetId, questionUid);
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(explanation.getText());
		final List<File> files = explanation.getFiles();
		if (!files.isEmpty() && stringBuilder.length() > 0) {
			if (useHtml) {
				stringBuilder.append("<br />");
			} else {
				stringBuilder.append("\n");
			}
		}
		for (int i = 0; i < files.size(); i++) {
			final File file = files.get(i);
			if (useHtml) {
				stringBuilder.append("<a href='").append(contextpath).append("/files/").append(surveyUid)
						.append(Constants.PATH_DELIMITER).append(file.getUid()).append("'>");
			} else {
				stringBuilder.append(file.getUid()).append("|");
			}
			stringBuilder.append(file.getNameForExport());
			if (useHtml) {
				stringBuilder.append("</a>");
			}
			if (i != files.size() - 1) {
				stringBuilder.append(";");
			}
		}
		return stringBuilder.toString();
	}

	@Transactional(readOnly = true)
	public int getLikesForExplanation(final int answerSetId, final String questionUid) {

		final AnswerExplanation explanation = answerExplanationService.getExplanation(answerSetId, questionUid);
		List<DelphiExplanationLike> likes = answerExplanationService.loadExplanationLikesUIDs(explanation.getId());
		return likes.size();
	}

	@Transactional(readOnly = true)
	public Map<Integer, Map<String, String>> getAllDiscussions(Survey survey) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createSQLQuery(
				"SELECT ac.ANSWER_SET_ID, ac.QUESTION_UID, ac.TEXT, ac.ANSWER_SET_CODE, ac.PARENT FROM ANSWERS_COMMENTS ac JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ac.ANSWER_SET_ID JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID WHERE s.SURVEY_UID = :surveyUid AND s.ISDRAFT = :draft ORDER BY ac.COMMENT_DATE")
				.setBoolean("draft", survey.getIsDraft()).setString("surveyUid", survey.getUniqueId());

		Map<Integer, Map<String, String>> result = new HashMap<>();

		@SuppressWarnings("rawtypes")
		List res = query.list();

		Map<String, String> usersByUid = new HashMap<String, String>();

		for (Object o : res) {
			Object[] a = (Object[]) o;

			int answerSetId = ConversionTools.getValue(a[0]);
			String questionUid = (String) a[1];
			String explanation = (String) a[2];
			String code = (String) a[3];
			int parent = ConversionTools.getValue(a[4]);

			if (!usersByUid.containsKey(code)) {
				usersByUid.put(code, "User " + (usersByUid.size() + 1));
			}

			if (!result.containsKey(answerSetId)) {
				result.put(answerSetId, new HashMap<String, String>());
			}

			String text = "";
			if (!explanation.equals(DELETED_DELPHI_COMMENT_WITH_REPLIES_TEXT)) {
				// Only put the user when the comment with replies has not been deleted.
				text += usersByUid.get(code) + ": ";
			}
			text += explanation;

			if (!result.get(answerSetId).containsKey(questionUid)) {
				result.get(answerSetId).put(questionUid, text);
			} else {
				String old = result.get(answerSetId).get(questionUid);
				if (parent == 0) {
					result.get(answerSetId).put(questionUid, old + "\n" + text);
				} else {
					result.get(answerSetId).put(questionUid, old + "\n   " + text);
				}
			}
		}

		return result;
	}

	@Transactional(readOnly = true)
	public AnswerComment getComment(int id) {
		final Session session = sessionFactory.getCurrentSession();
		return (AnswerComment) session.get(AnswerComment.class, id);
	}

	@Transactional(readOnly = true)
	public boolean hasCommentChildren(int id) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createSQLQuery("SELECT COUNT(*) FROM ANSWERS_COMMENTS WHERE PARENT = :parent")
				.setInteger("parent", id);
		return ((BigInteger) query.uniqueResult()).intValue() > 0;
	}

	/**
	 * Checks whether a user (answer set) needs to be notified about unread comments.
	 * @param uniqueCode ID of answer set (user)
	 * @param questionUid Question UID
	 */
	@Transactional(readOnly = true)
	public boolean hasUnreadComments(String uniqueCode, String questionUid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(""
				+ "SELECT EXISTS "
				+ "( "
				+ "  SELECT * "
				+ "  FROM ANSWERS_COMMENTS ac "
				+ "  LEFT JOIN ANSWERS_COMMENTS parents ON ac.PARENT = parents.ANSWER_COMMENT_ID "
				+ "  JOIN ANSWERS_SET asets ON asets.ANSWER_SET_ID = ac.ANSWER_SET_ID "
				+ "  WHERE ac.QUESTION_UID = :questionUid "
				+ "  AND ("
				+ "    (parents.ANSWER_SET_CODE = :uniqueCode AND (ac.READ_BY_PARENT = FALSE OR ac.READ_BY_PARENT IS NULL)) "
				+ "    OR (asets.UNIQUECODE = :uniqueCode AND (ac.READ_BY_PARTICIPANT = FALSE OR ac.READ_BY_PARTICIPANT IS NULL)) "
				+ "  )"
				+ ")");

		query.setString("uniqueCode", uniqueCode);
		query.setString("questionUid", questionUid);

		return ((BigInteger) query.uniqueResult()).intValue() > 0;
	}

	@Transactional(readOnly = true)
	public FilesByTypes<Integer, String> getExplanationFilesByAnswerSetIdAndQuestionUid(final Survey survey) {

		final FilesByTypes<Integer, String> result = new FilesByTypes<>();

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session
				.createSQLQuery("SELECT f.FILE_ID, f.FILE_NAME, f.FILE_UID, ans.ANSWER_SET_ID, ae.QUESTION_UID "
						+ "FROM FILES f " + "JOIN ANSWERS_EXPLANATIONS_FILES aef ON aef.files_FILE_ID = f.FILE_ID "
						+ "JOIN ANSWERS_EXPLANATIONS ae ON ae.ANSWER_EXPLANATION_ID = aef.ANSWERS_EXPLANATIONS_ANSWER_EXPLANATION_ID "
						+ "JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ae.ANSWER_SET_ID "
						+ "JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID "
						+ "WHERE s.SURVEY_UID = :surveyUid AND s.ISDRAFT = :draft " + "ORDER BY f.FILE_NAME")
				.setBoolean("draft", survey.getIsDraft()).setString("surveyUid", survey.getUniqueId());

		@SuppressWarnings("rawtypes")
		final List queryResult = query.list();

		for (Object row : queryResult) {
			final Object[] element = (Object[]) row;

			final int fileId = ConversionTools.getValue(element[0]);
			final String fileName = (String) element[1];
			final String fileUid = (String) element[2];
			final int answerSetId = ConversionTools.getValue(element[3]);
			final String questionUid = (String) element[4];

			final File file = new File();
			file.setId(fileId);
			file.setName(fileName);
			file.setUid(fileUid);
			result.addFile(answerSetId, questionUid, file);
		}

		return result;
	}

	@Transactional(readOnly = true)
	public File getExplanationFile(final String surveyUid, final String uniqueCode, final String questiondUid,
			final String fileName) {

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session
				.createSQLQuery("SELECT f.FILE_ID, f.FILE_UID "
						+ "FROM FILES f "
						+ "JOIN ANSWERS_EXPLANATIONS_FILES aef ON aef.files_FILE_ID = f.FILE_ID "
						+ "JOIN ANSWERS_EXPLANATIONS ae ON ae.ANSWER_EXPLANATION_ID = aef.ANSWERS_EXPLANATIONS_ANSWER_EXPLANATION_ID "
						+ "JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ae.ANSWER_SET_ID "
						+ "JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID "
						+ "WHERE s.SURVEY_UID = :surveyUid AND ans.UNIQUECODE = :uniqueCode "
							+ "AND ae.QUESTION_UID = :questionUid AND f.FILE_NAME = :fileName")
				.setString("surveyUid", surveyUid)
				.setString("uniqueCode", uniqueCode)
				.setString("questionUid", questiondUid)
				.setString("fileName", fileName);

		@SuppressWarnings("rawtypes")
		final Object result = query.uniqueResult();
		if (result == null)
			return null;
		final Object[] element = (Object[]) result;

		final int fileId = ConversionTools.getValue(element[0]);
		final String fileUid = (String) element[1];

		final File file = new File();
		file.setId(fileId);
		file.setName(fileName);
		file.setUid(fileUid);
		return file;
	}

	@Transactional
	public void removeFileFromExplanation(final int answerSetId, final String questionUid, final String fileUid) {

		final Session session = sessionFactory.getCurrentSession();
		final AnswerExplanation explanation = getExplanation(answerSetId, questionUid);
		explanation.getFiles().removeIf(f -> f.getUid().equals(fileUid));
		session.saveOrUpdate(explanation);
		session.flush();
	}

}
