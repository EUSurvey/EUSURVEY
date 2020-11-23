package com.ec.survey.service;

import com.ec.survey.model.AnswerExplanation;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.survey.Question;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

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

	@Transactional
	public void createOrUpdateExplanations(AnswerSet answerSet) {
		for (Question question : answerSet.getSurvey().getQuestions()) {
			if (question.getIsDelphiQuestion()) {
				String explanationtext = answerSet.getExplanations().get(question.getUniqueId());
				if (explanationtext == null)
				{
					explanationtext = "";
				}
				
				AnswerExplanation explanation;
				try {
					explanation = getExplanation(answerSet.getId(), question.getUniqueId());
				} catch (NoSuchElementException ex) {
					if (explanationtext.length() == 0)
					{
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
