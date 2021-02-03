package com.ec.survey.service;

import com.ec.survey.model.AnswerComment;
import com.ec.survey.model.AnswerExplanation;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.delphi.DelphiContribution;
import com.ec.survey.model.delphi.DelphiContributions;
import com.ec.survey.model.delphi.DelphiTableOrderBy;
import com.ec.survey.model.FilesByTypes;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
	public List<AnswerExplanation> getExplanationsOfSurvey(final String surveyUid, final boolean draft) {

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createSQLQuery(
				"SELECT f.FILE_ID, f.FILE_NAME, f.FILE_UID, ex.ANSWER_EXPLANATION_ID, ex.ANSWER_SET_ID, ex.QUESTION_UID, ex.TEXT "
				+ "FROM FILES f "
				+ "JOIN ANSWERS_EXPLANATIONS_FILES aef ON aef.files_FILE_ID = f.FILE_ID "
				+ "JOIN ANSWERS_EXPLANATIONS ex ON ex.ANSWER_EXPLANATION_ID = aef.ANSWERS_EXPLANATIONS_ANSWER_EXPLANATION_ID "
				+ "JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ex.ANSWER_SET_ID "
				+ "JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID "
				+ "WHERE s.SURVEY_UID = :surveyUid AND S.ISDRAFT = :draft")
				.setString("surveyUid", surveyUid).setBoolean("draft", draft);

		@SuppressWarnings("rawtypes")
		final List queryResult = query.list();

		Map<Integer, AnswerExplanation> explanations = new HashMap<>();
		for (Object row : queryResult) {
			final Object[] element = (Object[]) row;

			final int fileId = ConversionTools.getValue(element[0]);
			final String fileName = (String) element[1];
			final String fileUid = (String) element[2];
			final int answerExplanationId = ConversionTools.getValue(element[3]);
			final int answerExplanationAnswerSetId = ConversionTools.getValue(element[4]);
			final String answerExplanationQuestionUid = (String) element[5];
			final String answerExplanationText = (String) element[6];

			if (!explanations.containsKey(answerExplanationId)) {
				final AnswerExplanation file = new AnswerExplanation();
				file.setId(answerExplanationId);
				file.setAnswerSetId(answerExplanationAnswerSetId);
				file.setQuestionUid(answerExplanationQuestionUid);
				file.setText(answerExplanationText);
				explanations.put(answerExplanationId, file);
			}

			final File file = new File();
			file.setId(fileId);
			file.setName(fileName);
			file.setUid(fileUid);
			explanations.get(answerExplanationId).addFile(file);
		}

		return new ArrayList<>(explanations.values());
	}

	@Transactional(readOnly = true)
	public List<AnswerComment> getCommentsOfSurvey(final String surveyUid, final boolean draft) {

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createSQLQuery("SELECT ac.ANSWER_COMMENT_ID "
				+ "FROM ANSWERS_COMMENTS ac "
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
		case UpdateAsc:
			orderByClauseInner = "aset.ANSWER_SET_UPDATE ASC";
			orderByClauseOuter = "`update` ASC, answerSetId";
			break;

		case UpdateDesc:
			orderByClauseInner = "aset.ANSWER_SET_UPDATE DESC";
			orderByClauseOuter = "`update` DESC, answerSetId";
			break;

		default:
			throw new IllegalStateException("Unexpected value: " + orderBy);
		}

		String contributionsQueryText = "" + "SELECT\n" + "    aset.ANSWER_SET_ID answerSetId,\n" + "    aset.UNIQUECODE answerSetUniqueCode,\n"
				+ "    aset.ANSWER_SET_UPDATE `update`,\n" + "    a.VALUE value,\n" + "    a.PA_UID answerUid,\n"
				+ "    a.QUESTION_UID questionUid,\n" + "    a.ANSWER_COL `column`,\n" + "    a.ANSWER_ROW row,\n"
				+ "    COALESCE(ex.TEXT, main_explanation.TEXT) explanation\n" + "FROM ANSWERS a\n" + "JOIN (\n" +
				// select all answers sets that are relevant for this query
				"    SELECT\n" + "        aset.ANSWER_SET_ID,\n" + "        aset.UNIQUECODE,\n" + "        aset.ANSWER_SET_UPDATE\n"
				+ "    FROM ANSWERS a\n" + "    JOIN ANSWERS_SET aset ON a.AS_ID = aset.ANSWER_SET_ID\n"
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
				"ORDER BY " + orderByClauseOuter + ", row, `column`";

		Session session = sessionFactory.getCurrentSession();
		SQLQuery contributionsQuery = session.createSQLQuery(contributionsQueryText);
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
	public int getTotalDelphiContributions(Collection<String> questionUids, boolean isDraft) {
		String totalCountQueryText = "" + "SELECT COUNT(DISTINCT aset.ANSWER_SET_ID)\n" + "FROM ANSWERS a\n"
				+ "JOIN ANSWERS_SET aset ON a.AS_ID = aset.ANSWER_SET_ID\n"
				+ "JOIN SURVEYS s ON aset.SURVEY_ID = s.SURVEY_ID\n"
				+ "WHERE a.QUESTION_UID IN :questionUids AND s.ISDRAFT = :isDraft";

		Session session = sessionFactory.getCurrentSession();
		SQLQuery totalCountQuery = session.createSQLQuery(totalCountQueryText);
		totalCountQuery.setParameterList("questionUids", questionUids);
		totalCountQuery.setBoolean("isDraft", isDraft);
		return ((BigInteger) totalCountQuery.uniqueResult()).intValue();
	}

	@Transactional
	public void createUpdateOrDeleteExplanations(AnswerSet answerSet) {
		final Session session = sessionFactory.getCurrentSession();
			
		final Survey survey = answerSet.getSurvey();
		for (Question question : survey.getQuestions()) {
			final String questionUid = question.getUniqueId();
			if (question.getIsDelphiQuestion()) {
				AnswerSet.ExplanationData explanationData = answerSet.getExplanations().get(questionUid);
				if (explanationData == null) {
					continue;
				}

				AnswerExplanation explanation = null;				
				
				try {
					explanation = getExplanation(answerSet.getId(), questionUid);

					// Delete explanation text and files if there is no linked answer.
					final int questionId = question.getId();
					if (answerSet.getAnswers(questionId, questionUid).size() == 0) {
						fileService.deleteExplanationFilesFromDisk(survey.getUniqueId(), answerSet.getUniqueCode(),
								questionId);
						explanation.getFiles().forEach(session::delete);
						session.delete(explanation);
						return;
					}
				} catch (NoSuchElementException ex) {
					if ((explanationData.text.length() == 0) && (explanationData.files.isEmpty())) {
						continue;
					}
					
					explanation = new AnswerExplanation(answerSet.getId(), questionUid);
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
			//this should only happen during a import survey operation for delphi surveys
			
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
	public void deleteCommentsForDeletedAnswers(final AnswerSet answerSet) {

		final Session session = sessionFactory.getCurrentSession();
		for (Question question : answerSet.getSurvey().getQuestions()) {
			if (question.getIsDelphiQuestion()
				&& answerSet.getAnswers(question.getId(), question.getUniqueId()).size() == 0) {

				final Query replyDeletionQuery = session.createQuery("DELETE FROM AnswerComment "
						+ "WHERE answerSetId = :answerSetId AND questionUid = :questionUid AND parent IS NOT NULL")
						.setInteger("answerSetId", answerSet.getId())
						.setString("questionUid", question.getUniqueId());
				replyDeletionQuery.executeUpdate();

				final Query commentDeletionQuery = session.createQuery("DELETE FROM AnswerComment "
						+ "WHERE answerSetId = :answerSetId AND questionUid = :questionUid AND parent IS NULL")
						.setInteger("answerSetId", answerSet.getId())
						.setString("questionUid", question.getUniqueId());
				commentDeletionQuery.executeUpdate();
			}
		}
	}

	@Transactional
	public void deleteCommentsOfSurvey(final int surveyId) {

		final Session session = sessionFactory.getCurrentSession();

		final Query replyDeletionQuery = session.createSQLQuery("DELETE ac.* "
				+ "FROM ANSWERS_COMMENTS ac "
				+ "JOIN ANSWERS_SET an ON an.ANSWER_SET_ID = ac.ANSWER_SET_ID "
				+ "WHERE ac.PARENT IS NOT NULL AND an.SURVEY_ID = :id")
				.setInteger("id", surveyId);
		replyDeletionQuery.executeUpdate();

		final Query commentDeletionQuery = session.createSQLQuery("DELETE ac.* "
				+ "FROM ANSWERS_COMMENTS ac "
				+ "JOIN ANSWERS_SET an ON an.ANSWER_SET_ID = ac.ANSWER_SET_ID "
				+ "WHERE ac.PARENT IS NULL AND an.SURVEY_ID = :id")
				.setInteger("id", surveyId);
		commentDeletionQuery.executeUpdate();
	}

	@Transactional
	public void deleteExplanationFilesOfSurvey(final int surveyId) {

		final Session session = sessionFactory.getCurrentSession();

		final Query explanationFilesRetrievalQuery = session.createSQLQuery("SELECT f.FILE_UID "
				+ "FROM FILES f "
				+ "JOIN ANSWERS_EXPLANATIONS_FILES aef ON aef.files_FILE_ID = f.FILE_ID "
				+ "JOIN ANSWERS_EXPLANATIONS ae ON ae.ANSWER_EXPLANATION_ID = aef.ANSWERS_EXPLANATIONS_ANSWER_EXPLANATION_ID "
				+ "JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ae.ANSWER_SET_ID "
				+ "WHERE ans.SURVEY_ID = :id")
				.setInteger("id", surveyId);
		final List<String> fileUids = explanationFilesRetrievalQuery.list();

		final Query explanationFilesDeletionQuery12 = session.createSQLQuery("DELETE aef.* "
				+ "FROM ANSWERS_EXPLANATIONS_FILES aef "
				+ "JOIN ANSWERS_EXPLANATIONS ae ON ae.ANSWER_EXPLANATION_ID = aef.ANSWERS_EXPLANATIONS_ANSWER_EXPLANATION_ID "
				+ "JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ae.ANSWER_SET_ID "
				+ "WHERE ans.SURVEY_ID = :id")
				.setInteger("id", surveyId);
		explanationFilesDeletionQuery12.executeUpdate();

		if (fileUids.isEmpty()) return;
		final Query explanationFilesDeletionQuery2 = session.createSQLQuery("DELETE f.* "
				+ "FROM FILES f "
				+ "WHERE f.FILE_UID IN :ids")
				.setParameterList("ids", fileUids);
		explanationFilesDeletionQuery2.executeUpdate();
	}

	@Transactional
	public void deleteExplanationsOfSurvey(final int surveyId) {

		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createSQLQuery("DELETE ae.* "
				+ "FROM ANSWERS_EXPLANATIONS ae "
				+ "JOIN ANSWERS_SET an ON ae.ANSWER_SET_ID = an.ANSWER_SET_ID "
				+ "WHERE an.SURVEY_ID = :id")
				.setInteger("id", surveyId);
		query.executeUpdate();
	}

	@Transactional
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
				String userPrefix = usersByUid.get(comment.getUniqueCode()) + ": ";

				if (useHtml) {
					s.append("<div class='");
					if (first) {
						s.append("comment");
						first = false;
					} else {
						s.append("reply");
					}
					s.append("' data-id='")
							.append(comment.getId())
							.append("' data-unique-code='")
							.append(comment.getUniqueCode())
							.append("'>")
							.append("<span>")
							.append(userPrefix)
							.append(comment.getText())
							.append("</span>")
							.append("</div>");
				} else {
					if (first) {
						s.append(userPrefix).append(comment.getText());
						first = false;
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
	public Map<Integer, Map<String, String>> getAllDiscussions(Survey survey) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createSQLQuery("SELECT ac.ANSWER_SET_ID, ac.QUESTION_UID, ac.TEXT, ac.ANSWER_SET_CODE, ac.PARENT FROM ANSWERS_COMMENTS ac JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ac.ANSWER_SET_ID JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID WHERE s.SURVEY_UID = :surveyUid AND s.ISDRAFT = :draft ORDER BY ac.COMMENT_DATE")
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

	@Transactional(readOnly = true)
	public FilesByTypes<Integer, String> getExplanationFilesByAnswerSetIdAndQuestionUid(final Survey survey) {

		final FilesByTypes<Integer, String> result = new FilesByTypes<>();

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createSQLQuery("SELECT f.FILE_ID, f.FILE_NAME, f.FILE_UID, ans.ANSWER_SET_ID, ae.QUESTION_UID " +
				"FROM FILES f " +
				"JOIN ANSWERS_EXPLANATIONS_FILES aef ON aef.files_FILE_ID = f.FILE_ID " +
				"JOIN ANSWERS_EXPLANATIONS ae ON ae.ANSWER_EXPLANATION_ID = aef.ANSWERS_EXPLANATIONS_ANSWER_EXPLANATION_ID " +
				"JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ae.ANSWER_SET_ID " +
				"JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID " +
				"WHERE s.SURVEY_UID = :surveyUid AND s.ISDRAFT = :draft " +
				"ORDER BY f.FILE_NAME")
				.setBoolean("draft", survey.getIsDraft())
				.setString("surveyUid", survey.getUniqueId());

		@SuppressWarnings("rawtypes")
		final List queryResult = query.list();

		for (Object row : queryResult) {
			final Object[] element = (Object[]) row;

			final int fileId = ConversionTools.getValue(element[0]);
			final String fileName = (String)element[1];
			final String fileUid = (String)element[2];
			final int answerSetId = ConversionTools.getValue(element[3]);
			final String questionUid = (String)element[4];

			final File file = new File();
			file.setId(fileId);
			file.setName(fileName);
			file.setUid(fileUid);
			result.addFile(answerSetId, questionUid, file);
		}

		return result;
	}

}
