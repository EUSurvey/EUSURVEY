package com.ec.survey.tools.export;

import com.ec.survey.exception.TooManyFiltersException;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.Statistics;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.quiz.QuizResult;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.ReportingService;
import com.ec.survey.service.ReportingServiceProxy;
import com.ec.survey.service.SurveyService;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.QuizHelper;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service("statisticsCreator")
@Scope("prototype")
public class StatisticsCreator implements Runnable {
		
	@Resource(name = "answerService")
	protected AnswerService answerService;
	
	@Resource(name = "surveyService")
	protected SurveyService surveyService;
	
	@Resource(name = "sessionFactory")
	protected SessionFactory sessionFactory;
	
	@Resource(name = "reportingServiceProxy")
	protected ReportingServiceProxy reportingService;
	
	protected static final Logger logger = Logger.getLogger(StatisticsCreator.class);
	
	private Survey survey;
	private ResultFilter filter;
	private boolean allanswers;
	
	public Survey getSurvey()
	{
		return survey;
	}
	
	public ResultFilter getFilter()
	{
		return filter;
	}
	
	public void init(Survey survey, ResultFilter filter, boolean allanswers) {
		this.survey = survey;
		this.filter = filter;
		this.allanswers = allanswers;
	}
	
	@Override
	@Transactional
	public void run() {
		try {
		 runSync();
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
	}
		
	@Transactional
	public Statistics runSync() throws Exception {
		
		Statistics statistics = new Statistics();
		statistics.setSurveyId(survey.getId());
		statistics.setFilterHash(filter.getHash(allanswers));
		
		Session session = sessionFactory.getCurrentSession();
		survey = (Survey) session.merge(survey);
		surveyService.initializeSurvey(survey);
		session.evict(survey);
	
		if (allanswers && !survey.isMissingElementsChecked())
		{
			surveyService.CheckAndRecreateMissingElements(survey, filter);
		}	
		
		Map<Integer, Integer> numberOfAnswersMap = new HashMap<>();
		Map<Integer, Map<Integer, Integer>> numberOfAnswersMapMatrix = new HashMap<>();
		Map<Integer, Map<Integer, Integer>> numberOfAnswersMapRatingQuestion = new HashMap<>();
		Map<Integer, Map<Integer, Integer>> numberOfAnswersMapGallery = new HashMap<>();
		Map<Integer, Map<String, Set<String>>> multipleChoiceSelectionsByAnswerset = new HashMap<>();
				
		int total = getAnswers4Statistics(survey, filter, numberOfAnswersMap, numberOfAnswersMapMatrix, numberOfAnswersMapGallery, multipleChoiceSelectionsByAnswerset, numberOfAnswersMapRatingQuestion);
		survey.setNumberOfAnswerSets(total);
		
		List<Question> quizquestions = new ArrayList<>();
		
		for (Element element: survey.getQuestions())
		{
			if (element instanceof ChoiceQuestion)
			{
				addStatistics(survey, (ChoiceQuestion) element, statistics, numberOfAnswersMap, multipleChoiceSelectionsByAnswerset);
			} else if (element instanceof Matrix)
			{
				Matrix matrix = (Matrix)element;
									
				for (Element questionElement : matrix.getQuestions()) {
					for (Element answerElement : matrix.getAnswers()) {
						addStatistics4Matrix(survey, answerElement, questionElement, statistics, numberOfAnswersMapMatrix);
					}
					
					int answered = numberOfAnswersMap.get(questionElement.getId());
					
					statistics.getRequestedRecords().put(questionElement.getId().toString(), survey.getNumberOfAnswerSets() - answered);
					double percent = survey.getNumberOfAnswerSets() == 0 ? 0 : (double)(survey.getNumberOfAnswerSets() - answered) / (double)survey.getNumberOfAnswerSets() * 100;
					statistics.getRequestedRecordsPercent().put(questionElement.getId().toString(), percent);
					statistics.getTotalsPercent().put(questionElement.getId().toString(), percent);
				}
			} else if (element instanceof RatingQuestion)
			{
				RatingQuestion rating = (RatingQuestion)element;
									
				for (Element questionElement : rating.getQuestions()) {					
					for (int i = 1; i <= rating.getNumIcons(); i++)
					{
						addStatistics4RatingQuestion(survey, i, questionElement, statistics, numberOfAnswersMapRatingQuestion);
					}
					
					int answered = numberOfAnswersMap.get(questionElement.getId());
					
					statistics.getRequestedRecords().put(questionElement.getId().toString(), survey.getNumberOfAnswerSets() - answered);
					double percent = survey.getNumberOfAnswerSets() == 0 ? 0 : (double)(survey.getNumberOfAnswerSets() - answered) / (double)survey.getNumberOfAnswerSets() * 100;
					statistics.getRequestedRecordsPercent().put(questionElement.getId().toString(), percent);
					statistics.getTotalsPercent().put(questionElement.getId().toString(), percent);
				}
			} else if (element instanceof GalleryQuestion)
			{
				addStatistics4Gallery(survey, (GalleryQuestion) element, statistics, numberOfAnswersMapGallery, numberOfAnswersMap);
			} else if (survey.getIsQuiz() && element instanceof Question) {
				Question question = (Question) element;
				if (question.getScoring() > 0)
				{
					quizquestions.add(question);
				}
			}
		}
		
		Map<Integer, Map<Integer, Integer>> scorePoints = new HashMap<>();
		
		if (survey.getIsQuiz())
		{
			int bestScore = 0;
			int maxScore = 0;
			int totalScore = 0;
			int counter = 0;
			
			Map<String, Integer> questionMaximumScores = new HashMap<>();
			
			List<AnswerSet> allanswers = answerService.getAllAnswers(survey.getId(), filter);
			for (AnswerSet answerSet : allanswers) {
				QuizResult quizResult = QuizHelper.getQuizResult(answerSet, survey);
				
				totalScore += quizResult.getScore();
				if (quizResult.getScore() > bestScore) bestScore = quizResult.getScore();
				if (maxScore == 0) maxScore = quizResult.getMaximumScore();
				counter++;
				
				for (Question question : quizquestions)
				{
					if (!questionMaximumScores.containsKey(question.getUniqueId()))
					{
						questionMaximumScores.put(question.getUniqueId(), quizResult.getQuestionMaximumScore(question.getUniqueId()));
					}
					
					int score = quizResult.getQuestionScore(question.getUniqueId());
					if (score > 0)
					{
						if (!scorePoints.containsKey(question.getId()))
						{
							scorePoints.put(question.getId(), new HashMap<>());
						}
						if (!scorePoints.get(question.getId()).containsKey(score))
						{
							scorePoints.get(question.getId()).put(score, 1);
						} else {
							scorePoints.get(question.getId()).put(score, scorePoints.get(question.getId()).get(score)+1);
						}
					}
				}
				
				for (String sectionUid : quizResult.getSectionScores().keySet())
				{
					String scorestring = quizResult.getSectionScores().get(sectionUid);
					double score = Double.parseDouble(scorestring.substring(0, scorestring.indexOf("/")));
					int max = Integer.parseInt(scorestring.substring(scorestring.indexOf("/")+1));					
					
					if (!statistics.getMaxSectionScore().containsKey(sectionUid))
					{
						statistics.getMaxSectionScore().put(sectionUid, max);
					}
					
					if (!statistics.getMeanSectionScore().containsKey(sectionUid))
					{
						statistics.getMeanSectionScore().put(sectionUid, 0.0d);
					}
					
					statistics.getMeanSectionScore().put(sectionUid, statistics.getMeanSectionScore().get(sectionUid) + score);
					
					if (!statistics.getBestSectionScore().containsKey(sectionUid))
					{
						statistics.getBestSectionScore().put(sectionUid, score);
					} else {
						double oldscore = statistics.getBestSectionScore().get(sectionUid);
						if (score > oldscore)
						{
							statistics.getBestSectionScore().put(sectionUid, score);
						}
					}
				}
			}
			
			for (String sectionUid : statistics.getMeanSectionScore().keySet())
			{
				statistics.getMeanSectionScore().put(sectionUid, statistics.getMeanSectionScore().get(sectionUid) / total);
			}
						
			statistics.setBestScore(bestScore);
			statistics.setMaxScore(maxScore);
			statistics.setMeanScore(counter == 0 ? 0.0 : ((double)totalScore / total));
			statistics.setTotal(total);
			
			for (Question question : quizquestions)
			{
				addStatistics4Quiz(survey, question, statistics, scorePoints, questionMaximumScores);
			}
		}
		
		for (Element element: survey.getMissingElements())
		{
			if (element instanceof ChoiceQuestion)
			{
				addStatistics(survey, (ChoiceQuestion) element, statistics, numberOfAnswersMap, multipleChoiceSelectionsByAnswerset);
			} else if (element instanceof RatingQuestion)
			{
				RatingQuestion rating = (RatingQuestion)element;
				for (Element questionElement : rating.getQuestions()) {					
					for (int i = 1; i <= rating.getNumIcons(); i++)
					{
						addStatistics4RatingQuestion(survey, i, questionElement, statistics, numberOfAnswersMapRatingQuestion);
					}
					
					int answered = numberOfAnswersMap.get(questionElement.getId());
					
					statistics.getRequestedRecords().put(questionElement.getId().toString(), survey.getNumberOfAnswerSets() - answered);
					double percent = survey.getNumberOfAnswerSets() == 0 ? 0 : (double)(survey.getNumberOfAnswerSets() - answered) / (double)survey.getNumberOfAnswerSets() * 100;
					statistics.getRequestedRecordsPercent().put(questionElement.getId().toString(), percent);
					statistics.getTotalsPercent().put(questionElement.getId().toString(), percent);
				}
			} else if (element instanceof Matrix)
			{
				Matrix matrix = (Matrix)element;
				
				for (Element answerElement : matrix.getAnswers()) {
					for (Element questionElement : matrix.getQuestions()) {
						addStatistics4Matrix(survey, answerElement, questionElement, statistics, numberOfAnswersMapMatrix);
					}
					for (Element questionElement : matrix.getMissingQuestions()) {
						addStatistics4Matrix(survey, answerElement, questionElement, statistics, numberOfAnswersMapMatrix);
					}
				}
				for (Element answerElement : matrix.getMissingAnswers()) {
					for (Element questionElement : matrix.getQuestions()) {
						addStatistics4Matrix(survey, answerElement, questionElement, statistics, numberOfAnswersMapMatrix);
					}
					for (Element questionElement : matrix.getMissingQuestions()) {
						addStatistics4Matrix(survey, answerElement, questionElement, statistics, numberOfAnswersMapMatrix);
					}
				}
				
				for (Element questionElement : matrix.getQuestions()) {
					int answered = numberOfAnswersMap.get(questionElement.getId());
					
					statistics.getRequestedRecords().put(questionElement.getId().toString(), survey.getNumberOfAnswerSets() - answered);
					double percent = survey.getNumberOfAnswerSets() == 0 ? 0 : (double)(survey.getNumberOfAnswerSets() - answered) / (double)survey.getNumberOfAnswerSets() * 100;
					statistics.getRequestedRecordsPercent().put(questionElement.getId().toString(), percent);
					statistics.getTotalsPercent().put(questionElement.getId().toString(), percent);
				}
				
				for (Element questionElement : matrix.getMissingQuestions()) {
					int answered = numberOfAnswersMap.get(questionElement.getId());
					
					statistics.getRequestedRecords().put(questionElement.getId().toString(), survey.getNumberOfAnswerSets() - answered);
					double percent = survey.getNumberOfAnswerSets() == 0 ? 0 : (double)(survey.getNumberOfAnswerSets() - answered) / (double)survey.getNumberOfAnswerSets() * 100;
					statistics.getRequestedRecordsPercent().put(questionElement.getId().toString(), percent);
					statistics.getTotalsPercent().put(questionElement.getId().toString(), percent);
				}
			} 
		}
		
		answerService.save(statistics);
		
		return statistics;
	}
	
	@Transactional
	public int getAnswers4Statistics(Survey survey, ResultFilter filter, Map<Integer,Integer> map, Map<Integer,Map<Integer,Integer>> mapMatrix, Map<Integer, Map<Integer, Integer>> mapGallery, Map<Integer, Map<String, Set<String>>> multipleChoiceSelectionsByAnswerset, Map<Integer, Map<Integer, Integer>> mapRatingQuestion) throws TooManyFiltersException {
				
		boolean quiz = survey.getIsQuiz();
		Set<String> multipleChoiceQuestionUids = new HashSet<>();
		if (quiz)
		{
			for (Question q: survey.getQuestions())
			{
				if (q instanceof MultipleChoiceQuestion)
				{
					multipleChoiceQuestionUids.add(q.getUniqueId());
				}
			}
		}	
	
		
		if (reportingService.OLAPTableExists(survey.getUniqueId(), survey.getIsDraft()))
		{
			
			Map<String, Object> values = new HashMap<>();
			String where = ReportingService.getWhereClause(filter, values, survey);
			
			try {
				for (Question q: survey.getQuestions())
				{
					if (q instanceof ChoiceQuestion)
					{
						ChoiceQuestion choice = (ChoiceQuestion)q;
						for (PossibleAnswer a: choice.getAllPossibleAnswers())            
						{	
							int count = reportingService.getCount(survey, choice.getUniqueId(), a.getUniqueId(), false, where, values);
							map.put(a.getId(), count);
						}
						int count = reportingService.getCount(survey, choice.getUniqueId(), null, false, where, values);
						map.put(q.getId(), count);
					} else if (q instanceof GalleryQuestion)
					{
						GalleryQuestion g = (GalleryQuestion)q;
						if (!mapGallery.containsKey(g.getId()))
						{
							mapGallery.put(g.getId(), new HashMap<>());
						}
						for (int i = 0; i < g.getFiles().size(); i++)
						{
							int count = reportingService.getCount(survey, g.getUniqueId(), Integer.toString(i), false, where, values);
		                    mapGallery.get(g.getId()).put(i, count);
						}
						int count = reportingService.getCount(survey, g.getUniqueId(), null, false, where, values);
						map.put(q.getId(), count);
					} else if (q instanceof Matrix)
					{
						Matrix matrix = (Matrix)q;
						for (Element matrixQuestion: matrix.getQuestions())
						{
							for (Element matrixAnswer: matrix.getAnswers())
							{
								if (!mapMatrix.containsKey(matrixQuestion.getId())) mapMatrix.put(matrixQuestion.getId(), new HashMap<>());
								int count = reportingService.getCount(survey, matrixQuestion.getUniqueId(), matrixAnswer.getUniqueId(), false, where, values);
								mapMatrix.get(matrixQuestion.getId()).put(matrixAnswer.getId(), count);
							}
							int count = reportingService.getCount(survey, matrixQuestion.getUniqueId(), null, false, where, values);
	 						map.put(matrixQuestion.getId(), count);
						}
					} else if (q instanceof RatingQuestion)
					{
						RatingQuestion rating = (RatingQuestion)q;
						for (Element childQuestion: rating.getQuestions())
						{
							for (int i = 1; i <= rating.getNumIcons(); i++)
							{
								if (!mapRatingQuestion.containsKey(childQuestion.getId())) mapRatingQuestion.put(childQuestion.getId(), new HashMap<>());
								int count = reportingService.getCount(survey, childQuestion.getUniqueId(), Integer.toString(i) + "/", true, where, values);
								mapRatingQuestion.get(childQuestion.getId()).put(i, count);
							}
							int count = reportingService.getCount(survey, childQuestion.getUniqueId(), null, false, where, values);
							map.put(childQuestion.getId(), count);
						}
					}
				}
				
				return reportingService.getCount(survey, where, values);
			} catch (Exception e) {
				logger.info(e.getLocalizedMessage(), e);
			}
		} 
		
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> values = new HashMap<>();		
		Map<Integer, String> uniqueIdsById = SurveyService.getUniqueIdsById(survey);
		
		String where =  answerService.getSql(null, survey.getId(), filter, values, true);
		
		String sql = "select a.PA_ID, a.PA_UID, a.QUESTION_ID, a.QUESTION_UID, a.VALUE, ans.ANSWER_SET_ID FROM ANSWERS_SET ans LEFT OUTER JOIN ANSWERS a ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID IN (" + where + ")"  ;
		
		SQLQuery query = session.createSQLQuery(sql);		
		query.setReadOnly(true);
		
		for (String attrib : values.keySet()) {
			Object value = values.get(attrib);
			if (value instanceof String)
			{
				query.setString(attrib, (String)values.get(attrib));
			} else if (value instanceof Integer)
			{
				query.setInteger(attrib, (Integer)values.get(attrib));
			}  else if (value instanceof Date)
			{
				query.setTimestamp(attrib, (Date)values.get(attrib));
			}
		}
		
		query.setFetchSize(Integer.MIN_VALUE);
		ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
		
		Map<String, Integer> counts = new HashMap<>();
		Map<String, Integer> countsUID = new HashMap<>();
		Map<String, Integer> matrixcounts = new HashMap<>();
		Map<String, Integer> matrixcountsUID = new HashMap<>();
		Map<String, Integer> ratingquestioncounts = new HashMap<>();
		Map<String, Integer> ratingquestioncountsUID = new HashMap<>();
		Map<String, Integer> gallerycounts = new HashMap<>();
		Set<Integer> resultSets = new HashSet<>();		
		Map<String, Set<Integer>> answerSetQuestion = new HashMap<>();
			
		while (results != null && results.next()) 
		{
			Object[] a = results.get();
			
			Integer paid = ConversionTools.getValue(a[0]);
			String pauid = (String) a[1];
			Integer qid = ConversionTools.getValue(a[2]);
			String quid = (String) a[3];
			String value = (String) a[4];
			Integer as_id = ConversionTools.getValue(a[5]);
			
			if (!resultSets.contains(as_id))
			{
				resultSets.add(as_id);
			}
			
			if (quiz && multipleChoiceQuestionUids.contains(quid))
			{				
				if (!multipleChoiceSelectionsByAnswerset.containsKey(as_id))
				{
					multipleChoiceSelectionsByAnswerset.put(as_id, new HashMap<>());
				}
				
				if (!multipleChoiceSelectionsByAnswerset.get(as_id).containsKey(quid))
				{
					multipleChoiceSelectionsByAnswerset.get(as_id).put(quid, new HashSet<>());
				}
				
				if (!multipleChoiceSelectionsByAnswerset.get(as_id).get(quid).contains(pauid))
				{
					multipleChoiceSelectionsByAnswerset.get(as_id).get(quid).add(pauid);
				}
			}
			
			if (value != null)
			{			
				if (!answerSetQuestion.containsKey(quid))
				{
					answerSetQuestion.put(quid, new HashSet<>());
				}
				if (!answerSetQuestion.get(quid).contains(as_id))
				{
					answerSetQuestion.get(quid).add(as_id);
				}
				
				//scan
				String key = paid.toString() + "#" + qid.toString();
				if (matrixcounts.containsKey(key))
				{
					matrixcounts.put(key, matrixcounts.get(key) + 1);
				} else {
					matrixcounts.put(key, 1);
				}
				
				if (value.indexOf("/") > 0)
				{
					String key2 = value.substring(0, value.indexOf("/")) + "%" + qid.toString();
					if (ratingquestioncounts.containsKey(key2))
					{
						ratingquestioncounts.put(key2, ratingquestioncounts.get(key2) + 1);
					} else {
						ratingquestioncounts.put(key2, 1);
					}
					
					key2 = value.substring(0, value.indexOf("/")) + "%" + quid;
					if (ratingquestioncountsUID.containsKey(key2))
					{
						ratingquestioncountsUID.put(key2, ratingquestioncountsUID.get(key2) + 1);
					} else {
						ratingquestioncountsUID.put(key2, 1);
					}
				}
							
				key = paid.toString();
				if (paid != 0)
				{
					if (counts.containsKey(key))
					{
						counts.put(key, counts.get(key) + 1);
					} else {
						counts.put(key, 1);
					}
				} else {
					if (value != null && org.apache.commons.lang3.StringUtils.isNumeric(value))
					{
						String galleryKey = qid.toString() + "-" + value;
						if (gallerycounts.containsKey(galleryKey))
						{
							gallerycounts.put(galleryKey, gallerycounts.get(galleryKey) + 1);
						} else {
							gallerycounts.put(galleryKey, 1);
						}
					}
				}
				
				if (uniqueIdsById.containsKey(paid))
				{
					pauid = uniqueIdsById.get(paid);
				}
				
				if (uniqueIdsById.containsKey(qid))
				{
					quid = uniqueIdsById.get(qid);
				}
				
				if (pauid != null)
				{
					key = pauid + "#" + quid;
					if (countsUID.containsKey(key))
					{
						countsUID.put(key, countsUID.get(key) + 1);
					} else {
						countsUID.put(key, 1);
					}
					
					key = pauid + "#" + quid;
					if (matrixcountsUID.containsKey(key))
					{
						matrixcountsUID.put(key, matrixcountsUID.get(key) + 1);
					} else {
						matrixcountsUID.put(key, 1);
					}
				} else {
					if (value != null && org.apache.commons.lang3.StringUtils.isNumeric(value) && quid != null)
					{
						String galleryKey = quid + "-" + value;
						if (gallerycounts.containsKey(galleryKey))
						{
							gallerycounts.put(galleryKey, gallerycounts.get(galleryKey) + 1);
						} else {
							gallerycounts.put(galleryKey, 1);
						}
					}
				}
			}
			
		}
		results.close();
	
		for (Question q: survey.getQuestions())
		{
			if (q instanceof ChoiceQuestion)
			{
				ChoiceQuestion choice = (ChoiceQuestion)q;
				for (PossibleAnswer a: choice.getAllPossibleAnswers())            
				{	
					if (countsUID.containsKey(a.getUniqueId() + "#" + q.getUniqueId()))
					{
						map.put(a.getId(), countsUID.get(a.getUniqueId() + "#" + q.getUniqueId()));
					} else {
                        map.put(a.getId(), counts.getOrDefault(a.getId().toString(), 0));
					}
				}
				map.put(q.getId(), answerSetQuestion.get(q.getUniqueId()) != null ? answerSetQuestion.get(q.getUniqueId()).size() : 0);
			} else if (q instanceof GalleryQuestion)
			{
				GalleryQuestion g = (GalleryQuestion)q;
				if (!mapGallery.containsKey(g.getId()))
				{
					mapGallery.put(g.getId(), new HashMap<>());
				}
				for (int i = 0; i < g.getFiles().size(); i++)
				{
                    mapGallery.get(g.getId()).put(i, gallerycounts.getOrDefault(g.getUniqueId() + "-" + i, 0));
				}
				map.put(q.getId(), answerSetQuestion.get(q.getUniqueId()) != null ? answerSetQuestion.get(q.getUniqueId()).size() : 0);
			} else if (q instanceof Matrix)
			{
				Matrix matrix = (Matrix)q;
				for (Element matrixQuestion: matrix.getQuestions())
				{
					for (Element matrixAnswer: matrix.getAnswers())
					{
						String key = matrixAnswer.getUniqueId() + "#" + matrixQuestion.getUniqueId();
						if (!mapMatrix.containsKey(matrixQuestion.getId())) mapMatrix.put(matrixQuestion.getId(), new HashMap<>());
						if (matrixcountsUID.containsKey(key))
						{
							mapMatrix.get(matrixQuestion.getId()).put(matrixAnswer.getId(), matrixcountsUID.get(key));
						} else {
							key = matrixAnswer.getId() + "#" + matrixQuestion.getId();
                            mapMatrix.get(matrixQuestion.getId()).put(matrixAnswer.getId(), matrixcounts.getOrDefault(key, 0));
						}
					}
					map.put(matrixQuestion.getId(), answerSetQuestion.get(matrixQuestion.getUniqueId()) != null ? answerSetQuestion.get(matrixQuestion.getUniqueId()).size() : 0);
				}
			} else if (q instanceof RatingQuestion)
			{
				RatingQuestion rating = (RatingQuestion)q;
				for (Element childQuestion: rating.getQuestions())
				{
					for (int i = 1; i <= rating.getNumIcons(); i++)
					{
						String key = i + "%" +childQuestion.getUniqueId();
						if (!mapRatingQuestion.containsKey(childQuestion.getId())) mapRatingQuestion.put(childQuestion.getId(), new HashMap<>());
						if (ratingquestioncountsUID.containsKey(key))
						{
							mapRatingQuestion.get(childQuestion.getId()).put(i, ratingquestioncountsUID.get(key));
						} else {
							key = i + "%" + childQuestion.getId();
							mapRatingQuestion.get(childQuestion.getId()).put(i,ratingquestioncounts.getOrDefault(key, 0));
						}
					}
					map.put(childQuestion.getId(), answerSetQuestion.get(childQuestion.getUniqueId()) != null ? answerSetQuestion.get(childQuestion.getUniqueId()).size() : 0);
				}
			}
		}
		
		return resultSets.size();		
	}
	
	private int addStatistics4Matrix(Survey survey, Element answer, Element question, Statistics statistics, Map<Integer, Map<Integer, Integer>> numberOfAnswersMapMatrix)
	{			
		String id = question.getId().toString() + answer.getId().toString();
		int numberOfAnswers = 0;
		int total = survey.getNumberOfAnswerSets();
		double percent = 0;	
		
		if (numberOfAnswersMapMatrix.containsKey(question.getId()))
		{	
			numberOfAnswers = numberOfAnswersMapMatrix.get(question.getId()).getOrDefault(answer.getId(), 0);
			percent = total == 0 ? 0 : (((double)numberOfAnswers) / ((double)total) * 100);
		}
			
		statistics.getRequestedRecords().put(id, numberOfAnswers);
		statistics.getRequestedRecordsPercent().put(id, percent);
		statistics.getTotalsPercent().put(id, percent);	
		
		return numberOfAnswers;
	}

	private int addStatistics4RatingQuestion(Survey survey, Integer answer, Element question, Statistics statistics, Map<Integer, Map<Integer, Integer>> numberOfAnswersMapRatingQuestion)
	{			
		String id = question.getId().toString() + answer.toString();
		int numberOfAnswers = 0;
		int total = survey.getNumberOfAnswerSets();
		double percent = 0;	
		
		if (numberOfAnswersMapRatingQuestion.containsKey(question.getId()))
		{	
			numberOfAnswers = numberOfAnswersMapRatingQuestion.get(question.getId()).getOrDefault(answer, 0);
			percent = total == 0 ? 0 : (((double)numberOfAnswers) / ((double)total) * 100);
		}
			
		statistics.getRequestedRecords().put(id, numberOfAnswers);
		statistics.getRequestedRecordsPercent().put(id, percent);
		statistics.getTotalsPercent().put(id, percent);	
		
		return numberOfAnswers;
	}
	
	private void addStatistics(Survey survey, ChoiceQuestion question, Statistics statistics, Map<Integer, Integer> numberOfAnswersMap, Map<Integer, Map<String, Set<String>>> multipleChoiceSelectionsByAnswerset)
	{	
		boolean quiz = survey.getIsQuiz() && question.getScoring() > 0;
		int total = survey.getNumberOfAnswerSets();
		int correct = 0;
		
		for (PossibleAnswer answer : question.getAllPossibleAnswers()) {
			int numberOfAnswers = numberOfAnswersMap.getOrDefault(answer.getId(), 0);

			double percent = total == 0 ? 0 : (double)numberOfAnswers / (double)total * 100;
			
			statistics.getRequestedRecords().put(answer.getId().toString(), numberOfAnswers);
			statistics.getRequestedRecordsPercent().put(answer.getId().toString(), percent);
			statistics.getTotalsPercent().put(answer.getId().toString(), percent);
			
			if (quiz)
			{
				if (answer.getScoring().isCorrect())
				{
					correct += numberOfAnswers;
				}
			}
		}
		
		if (quiz)
		{
			if (question instanceof MultipleChoiceQuestion)
			{
				//correct means exactly all correct answers are selected
				correct = 0;
				for (int as_id : multipleChoiceSelectionsByAnswerset.keySet())
				{
					int ascorrect = 1;
					if (multipleChoiceSelectionsByAnswerset.get(as_id).containsKey(question.getUniqueId()))
					{
						Set<String> answerUIDs = multipleChoiceSelectionsByAnswerset.get(as_id).get(question.getUniqueId());
						for (PossibleAnswer answer : question.getAllPossibleAnswers()) {
							if ((answer.getScoring().isCorrect() && !answerUIDs.contains(answer.getUniqueId())) || (!answer.getScoring().isCorrect() && answerUIDs.contains(answer.getUniqueId())))
							{
								ascorrect = 0;
								break;
							} 
						}
					}
					correct += ascorrect;
				}
			}			
			
			statistics.getRequestedRecordsScore().put(question.getId().toString(), correct);
			double percent = total == 0 ? 0 : (double)correct / (double)total * 100;
			statistics.getRequestedRecordsPercentScore().put(question.getId().toString(), percent);
		}
		
		int answered = numberOfAnswersMap.get(question.getId());
		
		statistics.getRequestedRecords().put(question.getId().toString(), survey.getNumberOfAnswerSets() - answered);
		double percent = total == 0 ? 0 : (double)(survey.getNumberOfAnswerSets() - answered) / (double)total * 100;
		statistics.getRequestedRecordsPercent().put(question.getId().toString(), percent);
		statistics.getTotalsPercent().put(question.getId().toString(), percent);
	}
	
	private void addStatistics4Quiz(Survey survey, Question question, Statistics statistics, Map<Integer, Map<Integer, Integer>> scorePoints, Map<String, Integer> questionMaximumScores)
	{	
		int total = survey.getNumberOfAnswerSets();
		int correct = 0;
		int maxScore = 0;
		if (questionMaximumScores != null && questionMaximumScores.containsKey(question.getUniqueId())) {
			maxScore =  questionMaximumScores.get(question.getUniqueId());
		}
		
		if (scorePoints.containsKey(question.getId()))
		{
			for (int score : scorePoints.get(question.getId()).keySet())
			{
				if (score == maxScore)
				{
					correct += scorePoints.get(question.getId()).get(score);
				}
			}
		}
		
		statistics.getRequestedRecordsScore().put(question.getId().toString(), correct);
		double percent = total == 0 ? 0 : (double)correct / (double)total * 100;
		statistics.getRequestedRecordsPercentScore().put(question.getId().toString(), percent);
	}
	
	private void addStatistics4Gallery(Survey survey, GalleryQuestion question, Statistics statistics, Map<Integer, Map<Integer, Integer>> numberOfAnswersMap, Map<Integer, Integer> numberOfAnswersMap2)
	{		
		int total = survey.getNumberOfAnswerSets();
		
		for (int i = 0; i < question.getFiles().size(); i++) {
			
			int numberOfAnswers = 0;
			if (numberOfAnswersMap.containsKey(question.getId()) && numberOfAnswersMap.get(question.getId()).containsKey(i))
			{
				numberOfAnswers = numberOfAnswersMap.get(question.getId()).get(i);
			}
			double percent = total == 0 ? 0 : (double)numberOfAnswers / (double)total * 100;
			
			statistics.getRequestedRecords().put(question.getId() + "-" + i, numberOfAnswers);
			statistics.getRequestedRecordsPercent().put(question.getId() + "-" + i, percent);
			statistics.getTotalsPercent().put(question.getId() + "-" + i, percent);
		}
		
		int answered = numberOfAnswersMap2.get(question.getId());
		
		statistics.getRequestedRecords().put(question.getId().toString(), survey.getNumberOfAnswerSets() - answered);
		double percent = total == 0 ? 0 : (double)(survey.getNumberOfAnswerSets() - answered) / (double)total * 100;
		statistics.getRequestedRecordsPercent().put(question.getId().toString(), percent);
		statistics.getTotalsPercent().put(question.getId().toString(), percent);
	}	
	
}
