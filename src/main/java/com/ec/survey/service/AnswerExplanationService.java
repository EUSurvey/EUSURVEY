package com.ec.survey.service;

import com.ec.survey.model.AnswerExplanation;
import com.ec.survey.model.AnswerSet;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
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
}
