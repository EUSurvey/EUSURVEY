package com.ec.survey.service;

import com.ec.survey.model.Answer;
import com.ec.survey.model.AnswerExplanation;
import com.ec.survey.model.administration.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerExplanationService extends BasicService {

    @Transactional
    public void createOrUpdateExplanation(Answer answer, String explanationText) {

        final AnswerExplanation explanation = new AnswerExplanation();
        explanation.setReferredAnswer(answer);
        explanation.setText(explanationText);

        final Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(explanation);
    }

    @Transactional(readOnly = true)
    public AnswerExplanation getExplanation(Answer answer) {

        int answerId = answer.getId();
        final Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM AnswerExplanation WHERE ANSWER_ID = :answerId")
                .setInteger("answerId", answerId);
        AnswerExplanation result = (AnswerExplanation) query.uniqueResult();
        return result;
    }
}
