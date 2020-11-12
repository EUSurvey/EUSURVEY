package com.ec.survey.service;

import com.ec.survey.model.Answer;
import com.ec.survey.model.AnswerExplanation;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnswerExplanationService extends BasicService {

    @Transactional
    public void createOrUpdateExplanation(List<Answer> oldAnswers, List<Answer> newAnswers, String explanationText) {

        AnswerExplanation existingExplanation = null;
        for (Answer oldAnswer : oldAnswers) {
            existingExplanation = getExplanation(oldAnswer);
            if (existingExplanation.getId() != null) {
                break;
            }
        }

        final List<Integer> newAnswerIds = newAnswers.stream()
                .map(Answer::getId)
                .collect(Collectors.toList());

        final AnswerExplanation explanation = new AnswerExplanation();
        if (existingExplanation != null && existingExplanation.getId() != null) {
            explanation.setId(existingExplanation.getId());
        }
        explanation.setAnswerIds(newAnswerIds);
        explanation.setText(explanationText);

        final Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(explanation);
    }

    @Transactional
    public void deleteExplanationIfNotReferencedByAnAnswerAnymore(List<Answer> answersToDelete) {

        final List<Integer> answersToDeleteIds = answersToDelete.stream()
                .map(Answer::getId)
                .collect(Collectors.toList());

        final Session session = sessionFactory.getCurrentSession();

        final Map<Integer, AnswerExplanation> explanationsToCheck = new HashMap<>();
        for(Integer answerToDeleteId : answersToDeleteIds) {
            Query query = session.createQuery("SELECT ex FROM AnswerExplanation ex JOIN ex.answerIds aid WHERE aid = :answerId")
                    .setInteger("answerId", answerToDeleteId);
            final AnswerExplanation currentExplanation = (AnswerExplanation) query.uniqueResult();
            if (currentExplanation == null) {
                continue;
            }
            final Integer currentExplanationId = currentExplanation.getId();
            final AnswerExplanation explanationAlreadyFound = explanationsToCheck.get(currentExplanationId);
            if (explanationAlreadyFound == null) {
                currentExplanation.removeAnswerId(answerToDeleteId);
                explanationsToCheck.put(currentExplanationId, currentExplanation);
            } else {
                explanationAlreadyFound.removeAnswerId(answerToDeleteId);
            }
        }

        for(AnswerExplanation explanation : explanationsToCheck.values()) {
            if (explanation.getAnswerIds().size() == 0) {
                session.delete(explanation);
            } else {
                session.update(explanation);
            }
        }
    }

    @Transactional(readOnly = true)
    public AnswerExplanation getExplanation(Answer answer) {

        final int answerId = answer.getId();
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createQuery("SELECT ex FROM AnswerExplanation ex JOIN ex.answerIds aid WHERE aid = :answerId")
                .setInteger("answerId", answerId);
        AnswerExplanation result = (AnswerExplanation) query.uniqueResult();
        if (result == null) {
            result = new AnswerExplanation();
            result.setText("");
        }
        return result;
    }
}
