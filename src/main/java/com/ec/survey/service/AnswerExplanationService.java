package com.ec.survey.service;

import com.ec.survey.model.AnswerExplanation;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.delphi.DelphiExplanation;
import com.ec.survey.model.survey.*;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
	public List<DelphiExplanation> getDelphiExplanations(ChoiceQuestion question) {
		return getDelphiExplanationsInternal(Collections.singletonList(question.getUniqueId()), question.getUniqueId(), question.getSurvey().getIsDraft());
	}

	@Transactional(readOnly = true)
	public List<DelphiExplanation> getDelphiExplanations(Matrix question) {
		List<String> uids = question.getQuestions().stream().map(Element::getUniqueId).collect(Collectors.toList());
		return getDelphiExplanationsInternal(uids, question.getUniqueId(), question.getSurvey().getIsDraft());
	}

	@Transactional(readOnly = true)
	public List<DelphiExplanation> getDelphiExplanations(RatingQuestion question) {
		List<String> uids = question.getQuestions().stream().map(Element::getUniqueId).collect(Collectors.toList());
		return getDelphiExplanationsInternal(uids, question.getUniqueId(), question.getSurvey().getIsDraft());
	}

	@Transactional(readOnly = true)
	protected List<DelphiExplanation> getDelphiExplanationsInternal(Collection<String> questionUids, String mainQuestionUid, boolean isDraft) {
		String delphiExplanationQuery = "select a.AS_ID as `answerSetId`, COALESCE(ex.TEXT, main_explanation.TEXT) as `explanation`, aset.ANSWER_SET_UPDATE as `update`, a.VALUE as `value`, a.QUESTION_ID as `questionId`\n" +
				"from answers a\n" +
				"left join answers_explanations ex on a.QUESTION_UID = ex.QUESTION_UID and ex.ANSWER_SET_ID = a.AS_ID\n" +
				"left join (\n" +
				"    select TEXT, ANSWER_SET_ID\n" +
				"    from answers_explanations\n" +
				"    where QUESTION_UID = :mainQuestionUid\n" +
				"    ) as main_explanation on a.AS_ID = main_explanation.ANSWER_SET_ID\n" +
				"join answers_set aset on a.AS_ID = aset.ANSWER_SET_ID\n" +
				"join surveys s on aset.SURVEY_ID = s.SURVEY_ID\n" +
				"where a.QUESTION_UID IN :questionUids AND s.ISDRAFT = :isDraft";

		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session.createSQLQuery(delphiExplanationQuery);
		query.setResultTransformer(Transformers.aliasToBean(DelphiExplanation.class));
		query.setParameterList("questionUids", questionUids);
		query.setBoolean("isDraft", isDraft);
		query.setString("mainQuestionUid", mainQuestionUid);

		@SuppressWarnings("unchecked")
		List<DelphiExplanation> results = query.list();
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
}
