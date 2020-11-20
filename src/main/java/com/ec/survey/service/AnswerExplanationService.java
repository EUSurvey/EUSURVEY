package com.ec.survey.service;

import com.ec.survey.model.AnswerExplanation;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.delphi.DelphiExplanation;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service("answerExplanationService")
public class AnswerExplanationService extends BasicService {

	@Transactional
	public void createOrUpdateExplanation(AnswerSet answerSet, String questionUid, String explanationText) {

		AnswerExplanation explanation;
		try {
			explanation = getExplanation(answerSet.getId(), questionUid);
		} catch (NoSuchElementException ex) {
			explanation = new AnswerExplanation(answerSet.getId(), questionUid);
		}

		explanation.setText(explanationText);

		final Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(explanation);
	}

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
	public List<DelphiExplanation> getDelphiExplanations(String questionUid) {
		String delphiExplanationQuery = "select a.AS_ID as `answerSetId`, a.ANSWER_COL as `column`, ex.TEXT as `explanation`, a.ANSWER_ROW as `row`, aset.ANSWER_SET_UPDATE as `update`, a.VALUE as `value`\n" +
				"from answers a\n" +
				"left join answers_explanations ex on a.QUESTION_UID = ex.QUESTION_UID and ex.ANSWER_SET_ID = a.AS_ID\n" +
				"join answers_set aset on a.AS_ID = aset.ANSWER_SET_ID\n" +
				"where a.QUESTION_UID = :questionUid";

		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session.createSQLQuery(delphiExplanationQuery);
		query.setResultTransformer(Transformers.aliasToBean(DelphiExplanation.class));
		query.setString("questionUid", questionUid);

		@SuppressWarnings("unchecked")
		List<DelphiExplanation> results = query.list();
		return results;
	}
}
