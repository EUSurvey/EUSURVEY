package com.ec.survey.service;

import com.ec.survey.model.AnswerExplanation;
import com.ec.survey.model.AnswerSet;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerExplanationService extends BasicService {

    @Transactional
    public void createOrUpdateExplanation(AnswerSet answerSet, String questionUid, String explanationText) {

        AnswerExplanation explanation = getExplanation(answerSet.getId(), questionUid);
    
        if (explanation == null) 
        {
        	explanation = new AnswerExplanation(answerSet.getSurveyId(), questionUid);        	
        }
        
        explanation.setText(explanationText);

        final Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(explanation);
    }

//    @Transactional
//    public void deleteExplanationIfNotReferencedByAnAnswerAnymore(List<Answer> answersToDelete) {
//
//        final List<Integer> answersToDeleteIds = answersToDelete.stream()
//                .map(Answer::getId)
//                .collect(Collectors.toList());
//
//        final Session session = sessionFactory.getCurrentSession();
//
//        final Map<Integer, AnswerExplanation> explanationsToCheck = new HashMap<>();
//        for(Integer answerToDeleteId : answersToDeleteIds) {
//            Query query = session.createQuery("SELECT ex FROM AnswerExplanation ex JOIN ex.answerIds aid WHERE aid = :answerId")
//                    .setInteger("answerId", answerToDeleteId);
//            final AnswerExplanation currentExplanation = (AnswerExplanation) query.uniqueResult();
//            if (currentExplanation == null) {
//                continue;
//            }
//            final Integer currentExplanationId = currentExplanation.getId();
//            final AnswerExplanation explanationAlreadyFound = explanationsToCheck.get(currentExplanationId);
//            if (explanationAlreadyFound == null) {
//                currentExplanation.removeAnswerId(answerToDeleteId);
//                explanationsToCheck.put(currentExplanationId, currentExplanation);
//            } else {
//                explanationAlreadyFound.removeAnswerId(answerToDeleteId);
//            }
//        }
//
//        for(AnswerExplanation explanation : explanationsToCheck.values()) {
//            if (explanation.getAnswerIds().size() == 0) {
//                session.delete(explanation);
//            } else {
//                session.update(explanation);
//            }
//        }
//    }

    @Transactional(readOnly = true)
    public AnswerExplanation getExplanation(int answerSetId, String questionUid) {     
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createQuery("SELECT ex FROM AnswerExplanation ex WHERE answerSetId = :answerSetId AND questionUid = :questionUid")
                .setInteger("answerSetId", answerSetId).setString("questionUid", questionUid);
        return (AnswerExplanation) query.uniqueResult();
    }
}
