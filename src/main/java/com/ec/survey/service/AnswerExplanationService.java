package com.ec.survey.service;

import com.ec.survey.model.AnswerComment;
import com.ec.survey.model.AnswerExplanation;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.delphi.DelphiContribution;
import com.ec.survey.model.survey.*;
import com.ec.survey.tools.ConversionTools;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service("answerExplanationService")
public class AnswerExplanationService extends BasicService {

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
		final Query query = session.createQuery("SELECT ex FROM AnswerExplanation ex WHERE answerSetId = :answerSetId AND questionUid = :questionUid")
				.setInteger("answerSetId", answerSetId)
				.setString("questionUid", questionUid);
		AnswerExplanation explanation = (AnswerExplanation) query.uniqueResult();
		if (explanation == null) {
			throw new NoSuchElementException();
		}
		return explanation;
	}
	
	@Transactional(readOnly = true)
	public Map<Integer, Map<String, String>> getAllExplanations(Survey survey) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createSQLQuery("SELECT ex.ANSWER_SET_ID, ex.QUESTION_UID, ex.TEXT FROM ANSWERS_EXPLANATIONS ex JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ex.ANSWER_SET_ID JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID WHERE s.SURVEY_UID = :surveyUid AND s.ISDRAFT = :draft")
				.setBoolean("draft", survey.getIsDraft())
				.setString("surveyUid", survey.getUniqueId());
		
		Map<Integer, Map<String, String>> result = new HashMap<>();		
		
		@SuppressWarnings("rawtypes")
		List res = query.list();
		
		for (Object o : res) {
			Object[] a = (Object[]) o;
					
			int answerSetId = ConversionTools.getValue(a[0]);
			String questionUid = (String)a[1];
			String explanation =  (String)a[2];
			
			if (!result.containsKey(answerSetId))
			{
				result.put(answerSetId, new HashMap<String, String>());
			}
			
			result.get(answerSetId).put(questionUid, explanation);
		}
		
		return result;
	}

	@Transactional(readOnly = true)
	public List<DelphiContribution> getDelphiContributions(ChoiceQuestion question) {
		return getDelphiContributions(Collections.singletonList(question.getUniqueId()), question.getUniqueId(), question.getSurvey().getIsDraft());
	}

	@Transactional(readOnly = true)
	public List<DelphiContribution> getDelphiContributions(Matrix question) {
		List<String> uids = question.getQuestions().stream().map(Element::getUniqueId).collect(Collectors.toList());
		return getDelphiContributions(uids, question.getUniqueId(), question.getSurvey().getIsDraft());
	}

	@Transactional(readOnly = true)
	public List<DelphiContribution> getDelphiContributions(RatingQuestion question) {
		List<String> uids = question.getQuestions().stream().map(Element::getUniqueId).collect(Collectors.toList());
		return getDelphiContributions(uids, question.getUniqueId(), question.getSurvey().getIsDraft());
	}

	@Transactional(readOnly = true)
	public List<DelphiContribution> getDelphiContributions(Table question) {
		return getDelphiContributions(Collections.singletonList(question.getUniqueId()), question.getUniqueId(), question.getSurvey().getIsDraft());
	}

	@Transactional(readOnly = true)
	public List<DelphiContribution> getDelphiContributions(Collection<String> questionUids, String mainQuestionUid, boolean isDraft) {
		String queryText = "select a.AS_ID as `answerSetId`, COALESCE(ex.TEXT, main_explanation.TEXT) as `explanation`, aset.ANSWER_SET_UPDATE as `update`, a.VALUE as `value`, a.PA_UID as `answerUid`, a.QUESTION_UID as `questionUid`, a.ANSWER_COL as `column`, a.ANSWER_ROW as `row`\n" +
				"from ANSWERS a\n" +
				"left join ANSWERS_EXPLANATIONS ex on a.QUESTION_UID = ex.QUESTION_UID and ex.ANSWER_SET_ID = a.AS_ID\n" +
				"left join (\n" +
				"    select TEXT, ANSWER_SET_ID\n" +
				"    from ANSWERS_EXPLANATIONS\n" +
				"    where QUESTION_UID = :mainQuestionUid\n" +
				"    ) as main_explanation on a.AS_ID = main_explanation.ANSWER_SET_ID\n" +
				"join ANSWERS_SET aset on a.AS_ID = aset.ANSWER_SET_ID\n" +
				"join SURVEYS s on aset.SURVEY_ID = s.SURVEY_ID\n" +
				"where a.QUESTION_UID IN :questionUids AND s.ISDRAFT = :isDraft";

		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session.createSQLQuery(queryText);
		query.setResultTransformer(Transformers.aliasToBean(DelphiContribution.class));
		query.setParameterList("questionUids", questionUids);
		query.setBoolean("isDraft", isDraft);
		query.setString("mainQuestionUid", mainQuestionUid);

		@SuppressWarnings("unchecked")
		List<DelphiContribution> results = query.list();
		return results;
	}

	@Transactional
	public void createOrUpdateExplanations(AnswerSet answerSet) {
		for (Question question : answerSet.getSurvey().getQuestions()) {
			if (question.getIsDelphiQuestion()) {
				String explanationtext = answerSet.getExplanations().get(question.getUniqueId());
				if (explanationtext == null) {
					continue;
				}

				AnswerExplanation explanation;
				try {
					explanation = getExplanation(answerSet.getId(), question.getUniqueId());
				} catch (NoSuchElementException ex) {
					if (explanationtext.length() == 0) {
						continue;
					}

					explanation = new AnswerExplanation(answerSet.getId(), question.getUniqueId());
				}

				explanation.setText(explanationtext);

				final Session session = sessionFactory.getCurrentSession();
				session.saveOrUpdate(explanation);
			}
		}
	}
	
	@Transactional
	public void saveComment(AnswerComment comment) {
		final Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(comment);
	}
	
	@Transactional
	public List<AnswerComment> loadComments(int answerSetId, String questionUid) {
		final Session session = sessionFactory.getCurrentSession();
		
		Query query = session.createQuery("FROM AnswerComment WHERE answerSetId = :answerSetId and questionUid = :questionUid ORDER BY date");
		query.setInteger("answerSetId", answerSetId).setString("questionUid", questionUid);
		
		@SuppressWarnings("unchecked")
		List<AnswerComment> list = query.list();
		
		return list;
	}
	
	@Transactional
	public String getDiscussion(int answerSetId, String questionUid, boolean useHtml)
	{
		List<AnswerComment> comments = loadComments(answerSetId, questionUid);
		StringBuilder s = new StringBuilder();
		for (AnswerComment comment : comments) {
			if (comment.getParent() == null)
			{
				if (useHtml)
				{
					s.append("<div class='comment'>").append(comment.getText()).append("</div>");
				} else {
					s.append(comment.getText()).append("\n");
				}
			} else {

				if (useHtml)
				{
					s.append("<div class='reply'>").append(comment.getText()).append("</div>");
				} else {
					s.append(comment.getText()).append("\n");
				}
			}
		}
		return s.toString();
	}
	
	@Transactional(readOnly = true)
	public Map<Integer, Map<String, String>> getAllDiscussions(Survey survey) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createSQLQuery("SELECT ac.ANSWER_SET_ID, ac.QUESTION_UID, ac.TEXT, ac.PARENT FROM ANSWERS_COMMENTS ac JOIN ANSWERS_SET ans ON ans.ANSWER_SET_ID = ac.ANSWER_SET_ID JOIN SURVEYS s ON s.SURVEY_ID = ans.SURVEY_ID WHERE s.SURVEY_UID = :surveyUid AND s.ISDRAFT = :draft ORDER BY ac.COMMENT_DATE")
				.setBoolean("draft", survey.getIsDraft())
				.setString("surveyUid", survey.getUniqueId());
		
		Map<Integer, Map<String, String>> result = new HashMap<>();		
		
		@SuppressWarnings("rawtypes")
		List res = query.list();
		
		for (Object o : res) {
			Object[] a = (Object[]) o;
					
			int answerSetId = ConversionTools.getValue(a[0]);
			String questionUid = (String)a[1];
			String explanation =  (String)a[2];
			int parentId = ConversionTools.getValue(a[3]);
			
			if (!result.containsKey(answerSetId))
			{
				result.put(answerSetId, new HashMap<String, String>());
			}
			
			if (!result.get(answerSetId).containsKey(questionUid))
			{			
				result.get(answerSetId).put(questionUid, explanation);
			} else {
				String old = result.get(answerSetId).get(questionUid);
				result.get(answerSetId).put(questionUid, old + "\n   " + explanation);
			}
		}
		
		return result;
	}

	@Transactional
	public AnswerComment getComment(int id) {
		final Session session = sessionFactory.getCurrentSession();
		return (AnswerComment) session.get(AnswerComment.class, id);
	}

}
