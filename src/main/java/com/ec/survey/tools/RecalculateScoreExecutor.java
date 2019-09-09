package com.ec.survey.tools;

import java.util.List;

import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.hibernate.Query;
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
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.SessionService;
import com.ec.survey.service.SurveyService;

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
				Query query = session.createQuery("FROM AnswerSet a WHERE a.surveyId = :id").setInteger("id", surveyId);
			
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
						session.save(answerSet);
						changes = true;
					}
				}
				
				if (changes)
				{
					answerService.deleteStatisticsForSurvey(surveyId);
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	
		logger.info("recalculation of score of survey " + surveyid + " finished");
	}
	
}
