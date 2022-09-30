package com.ec.survey.tools;

import java.util.List;

import javax.annotation.Resource;

import com.ec.survey.model.Form;
import com.ec.survey.service.*;
import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.survey.Survey;

@Service("recalculateScoreExecutor")
@Scope("prototype")
public class RecalculateScoreExecutor implements Runnable {
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="answerService")
	private AnswerService answerService;
	
	@Resource(name="sessionService")
	private SessionService sessionService;
	
	@Resource(name="mailService")
	private MailService mailService;

	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;

	@Resource(name="translationService")
	private TranslationService translationService;

	@Autowired
	protected MessageSource resources;	
	
	private int surveyid;
	
	private static final Logger logger = Logger.getLogger(RecalculateScoreExecutor.class);
	
	public void init(int surveyid)
	{
		this.surveyid = surveyid;
	}
	
	@Transactional
	public void run()
	{
		Session session = sessionFactory.getCurrentSession();
		logger.info("recalculation of score of survey " + surveyid + " started");
		try {
			
			Survey newest = surveyService.getSurvey(surveyid);
			List<Integer> surveyIds = surveyService.getAllSurveyVersions(surveyid);
			
			for (Integer surveyId : surveyIds)
			{
				Survey survey = surveyService.getSurvey(surveyid);
				Query<AnswerSet> query = session.createQuery("FROM AnswerSet a WHERE a.surveyId = :id", AnswerSet.class).setParameter("id", surveyId);
			
				query.setFetchSize(100);
				ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
				boolean changes = false;
				while (results.next()) 
				{
					AnswerSet answerSet = (AnswerSet)results.get(0);		
					int score = QuizHelper.getQuizResult(answerSet, newest).getScore();
					if (answerSet.getScore() == null || score != answerSet.getScore())
					{
						answerSet.setScore(score);
						surveyService.saveAnswerSet(answerSet, answerService.getFileDir(), answerSet.getDraftId());
						session.save(answerSet);
						changes = true;
					}
				}
				
				if (changes)
				{
					answerService.deleteStatisticsForSurvey(surveyId);
					//This is necessary for the reporting db (for some reason)
					new Form(resources, surveyService.getLanguage(survey.getLanguage().getCode()),
							translationService.getActiveTranslationsForSurvey(survey.getId()), sessionService.getContextPath());
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	
		logger.info("recalculation of score of survey " + surveyid + " finished");
	}
	
}
