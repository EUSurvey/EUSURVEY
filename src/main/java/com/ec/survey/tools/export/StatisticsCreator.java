package com.ec.survey.tools.export;

import com.ec.survey.exception.MessageException;
import com.ec.survey.exception.TooManyFiltersException;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.Statistics;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.delphi.NumberQuestionStatistics;
import com.ec.survey.model.selfassessment.SATargetDataset;
import com.ec.survey.model.survey.quiz.QuizResult;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.ReportingService;
import com.ec.survey.service.ReportingServiceProxy;
import com.ec.survey.service.SelfAssessmentService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.QuizHelper;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.query.NativeQuery;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
	
	@Resource(name = "selfassessmentService")
	protected SelfAssessmentService selfassessmentService;

	protected static final Logger logger = Logger.getLogger(StatisticsCreator.class);

	private Survey survey;
	private ResultFilter filter;
	private boolean allanswers;

	public Survey getSurvey() {
		return survey;
	}

	public ResultFilter getFilter() {
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
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@Transactional
	public Statistics runSync() throws Exception {

		Statistics statistics = new Statistics();
		statistics.setSurveyId(survey.getId());

		Session session = sessionFactory.getCurrentSession();
		survey = (Survey) session.merge(survey);
		surveyService.initializeSurvey(survey);
		session.evict(survey);

		if (allanswers && !survey.isMissingElementsChecked()) {
			surveyService.checkAndRecreateMissingElements(survey, filter);
		}

		statistics.setFilterHash(filter.getHash(allanswers));

		Map<Integer, Integer> numberOfAnswersMap = new HashMap<>();
		Map<Integer, Map<Integer, Integer>> numberOfAnswersMapMatrix = new HashMap<>();
		Map<Integer, Map<Integer, Integer>> numberOfAnswersMapRatingQuestion = new HashMap<>();
		Map<Integer, Map<String, Integer>> numberOfAnswersMapGallery = new HashMap<>();
		Map<Integer, Map<String, Set<String>>> multipleChoiceSelectionsByAnswerset = new HashMap<>();
		Map<String, Integer> numberOfNumberAnswersMap = new HashMap<>();
		Map<String, Map<Integer, Integer>> numberOfAnswersMapRankingQuestion = new HashMap<>();
		Map<String, List<String>> rankingQuestionAnswers = new HashMap<>();
		Map<String, Map<Integer, Integer>> mapTargetDatasetQuestion = new HashMap<>();
		
		int total = getAnswers4Statistics(survey, filter, numberOfAnswersMap, numberOfAnswersMapMatrix,
				numberOfAnswersMapGallery, multipleChoiceSelectionsByAnswerset, numberOfAnswersMapRatingQuestion, numberOfNumberAnswersMap, numberOfAnswersMapRankingQuestion, rankingQuestionAnswers, mapTargetDatasetQuestion);
		survey.setNumberOfAnswerSets(total);

		List<Question> quizquestions = new ArrayList<>();

		for (Element element : survey.getQuestions()) {
			if (element instanceof ChoiceQuestion) {
				addChoiceStatistics(survey, (ChoiceQuestion) element, statistics, numberOfAnswersMap,
						multipleChoiceSelectionsByAnswerset, mapTargetDatasetQuestion);
			} else if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;

				for (Element questionElement : matrix.getQuestions()) {
					for (Element answerElement : matrix.getAnswers()) {
						addStatistics4Matrix(survey, answerElement, questionElement, statistics,
								numberOfAnswersMapMatrix);
					}
					
					int answered = numberOfAnswersMap.get(questionElement.getId());

					statistics.getRequestedRecords().put(questionElement.getId().toString(),
							survey.getNumberOfAnswerSets() - answered);
					double percent = survey.getNumberOfAnswerSets() == 0 ? 0
							: (double) (survey.getNumberOfAnswerSets() - answered)
									/ (double) survey.getNumberOfAnswerSets() * 100;
					statistics.getRequestedRecordsPercent().put(questionElement.getId().toString(), percent);
					statistics.getTotalsPercent().put(questionElement.getId().toString(), percent);
					
					Question matrixQuestion = (Question)questionElement;
					if (survey.getIsQuiz() && matrixQuestion.getScoring() > 0) {
						quizquestions.add(matrixQuestion);
					}
				}
			} else if (element instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion) element;

				for (Element questionElement : rating.getQuestions()) {
					for (int i = 1; i <= rating.getNumIcons(); i++) {
						addStatistics4RatingQuestion(survey, i, questionElement, statistics,
								numberOfAnswersMapRatingQuestion);
					}

					int answered = numberOfAnswersMap.get(questionElement.getId());

					statistics.getRequestedRecords().put(questionElement.getId().toString(),
							survey.getNumberOfAnswerSets() - answered);
					double percent = survey.getNumberOfAnswerSets() == 0 ? 0
							: (double) (survey.getNumberOfAnswerSets() - answered)
									/ (double) survey.getNumberOfAnswerSets() * 100;
					statistics.getRequestedRecordsPercent().put(questionElement.getId().toString(), percent);
					statistics.getTotalsPercent().put(questionElement.getId().toString(), percent);
				}
			} else if (element instanceof ComplexTable) {
				ComplexTable table = (ComplexTable) element;

				for (ComplexTableItem child : table.getQuestionChildElements()) {
					if (child.isChoice()) {
						addChoiceStatistics(survey, child, statistics, numberOfAnswersMap,
								multipleChoiceSelectionsByAnswerset, mapTargetDatasetQuestion);
					} else if ((child.getCellType() == ComplexTableItem.CellType.Number || child.getCellType() == ComplexTableItem.CellType.Formula) && child.showStatisticsForNumberQuestion()) {
						addStatistics4NumberQuestion(survey, child, statistics, numberOfNumberAnswersMap, numberOfAnswersMap);
					}
				}
			} else if (element instanceof RankingQuestion) {
				RankingQuestion ranking = (RankingQuestion) element;						
				addStatistics4RankingQuestion(survey, ranking, statistics,
						numberOfAnswersMapRankingQuestion);
			} else if (element instanceof GalleryQuestion) {
				addStatistics4Gallery(survey, (GalleryQuestion) element, statistics, numberOfAnswersMapGallery,
						numberOfAnswersMap);
			} else if (element instanceof NumberQuestion) {
				NumberQuestion number = (NumberQuestion) element;
				if (number.showStatisticsForNumberQuestion()) {
					addStatistics4NumberQuestion(survey, number, statistics, numberOfNumberAnswersMap, numberOfAnswersMap);
				}
				if (survey.getIsQuiz() && number.getScoring() > 0) {
					quizquestions.add(number);
				}
			} else if (element instanceof FormulaQuestion) {
				FormulaQuestion formula = (FormulaQuestion) element;
				if (formula.showStatisticsForNumberQuestion()) {
					addStatistics4NumberQuestion(survey, formula, statistics, numberOfNumberAnswersMap, numberOfAnswersMap);
				}
				if (survey.getIsQuiz() && formula.getScoring() > 0) {
					quizquestions.add(formula);
				}
			} else if (survey.getIsQuiz() && element instanceof Question) {
				Question question = (Question) element;
				if (question.getScoring() > 0) {
					quizquestions.add(question);
				}
			}
		}

		Map<Integer, Map<Integer, Integer>> scorePoints = new HashMap<>();

		if (survey.getIsQuiz()) {
			int bestScore = 0;
			int maxScore = 0;
			int totalScore = 0;
			int counter = 0;

			Map<String, Integer> questionMaximumScores = new HashMap<>();

			List<AnswerSet> allanswers = answerService.getAllAnswers(survey.getId(), filter);
			for (AnswerSet answerSet : allanswers) {
				QuizResult quizResult = QuizHelper.getQuizResult(answerSet, survey);

				totalScore += quizResult.getScore();
				if (quizResult.getScore() > bestScore)
					bestScore = quizResult.getScore();
				if (maxScore == 0)
					maxScore = quizResult.getMaximumScore();
				counter++;

				for (Question question : quizquestions) {
					if (!questionMaximumScores.containsKey(question.getUniqueId())) {
						questionMaximumScores.put(question.getUniqueId(),
								quizResult.getQuestionMaximumScore(question.getUniqueId()));
					}

					int score = quizResult.getQuestionScore(question.getUniqueId());
					if (score > 0) {
						if (!scorePoints.containsKey(question.getId())) {
							scorePoints.put(question.getId(), new HashMap<>());
						}
						if (!scorePoints.get(question.getId()).containsKey(score)) {
							scorePoints.get(question.getId()).put(score, 1);
						} else {
							scorePoints.get(question.getId()).put(score,
									scorePoints.get(question.getId()).get(score) + 1);
						}
					}
				}

				for (String sectionUid : quizResult.getSectionScores().keySet()) {
					String scorestring = quizResult.getSectionScores().get(sectionUid);
					double score = Double.parseDouble(scorestring.substring(0, scorestring.indexOf('/')));
					int max = Integer.parseInt(scorestring.substring(scorestring.indexOf('/') + 1));

					if (!statistics.getMaxSectionScore().containsKey(sectionUid)) {
						statistics.getMaxSectionScore().put(sectionUid, max);
					}

					if (!statistics.getMeanSectionScore().containsKey(sectionUid)) {
						statistics.getMeanSectionScore().put(sectionUid, 0.0d);
					}

					statistics.getMeanSectionScore().put(sectionUid,
							statistics.getMeanSectionScore().get(sectionUid) + score);

					if (!statistics.getBestSectionScore().containsKey(sectionUid)) {
						statistics.getBestSectionScore().put(sectionUid, score);
					} else {
						double oldscore = statistics.getBestSectionScore().get(sectionUid);
						if (score > oldscore) {
							statistics.getBestSectionScore().put(sectionUid, score);
						}
					}
				}
			}

			for (String sectionUid : statistics.getMeanSectionScore().keySet()) {
				statistics.getMeanSectionScore().put(sectionUid,
						total == 0 ? 0 : statistics.getMeanSectionScore().get(sectionUid) / total);
			}

			statistics.setBestScore(bestScore);
			statistics.setMaxScore(maxScore);
			statistics.setMeanScore((counter == 0 || total == 0) ? 0.0 : ((double) totalScore / total));
			statistics.setTotal(total);

			for (Question question : quizquestions) {
				addStatistics4Quiz(survey, question, statistics, scorePoints, questionMaximumScores);
			}
		}

		for (Element element : survey.getMissingElements()) {
			if (element instanceof ChoiceQuestion) {
				addChoiceStatistics(survey, (ChoiceQuestion) element, statistics, numberOfAnswersMap,
						multipleChoiceSelectionsByAnswerset, mapTargetDatasetQuestion);
			} else if (element instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion) element;
				for (Element questionElement : rating.getQuestions()) {
					for (int i = 1; i <= rating.getNumIcons(); i++) {
						addStatistics4RatingQuestion(survey, i, questionElement, statistics,
								numberOfAnswersMapRatingQuestion);
					}

					int answered = numberOfAnswersMap.get(questionElement.getId());

					statistics.getRequestedRecords().put(questionElement.getId().toString(),
							survey.getNumberOfAnswerSets() - answered);
					double percent = survey.getNumberOfAnswerSets() == 0 ? 0
							: (double) (survey.getNumberOfAnswerSets() - answered)
									/ (double) survey.getNumberOfAnswerSets() * 100;
					statistics.getRequestedRecordsPercent().put(questionElement.getId().toString(), percent);
					statistics.getTotalsPercent().put(questionElement.getId().toString(), percent);
				}
			} else if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;

				for (Element answerElement : matrix.getAnswers()) {
					for (Element questionElement : matrix.getQuestions()) {
						addStatistics4Matrix(survey, answerElement, questionElement, statistics,
								numberOfAnswersMapMatrix);
					}
					for (Element questionElement : matrix.getMissingQuestions()) {
						addStatistics4Matrix(survey, answerElement, questionElement, statistics,
								numberOfAnswersMapMatrix);
					}
				}
				for (Element answerElement : matrix.getMissingAnswers()) {
					for (Element questionElement : matrix.getQuestions()) {
						addStatistics4Matrix(survey, answerElement, questionElement, statistics,
								numberOfAnswersMapMatrix);
					}
					for (Element questionElement : matrix.getMissingQuestions()) {
						addStatistics4Matrix(survey, answerElement, questionElement, statistics,
								numberOfAnswersMapMatrix);
					}
				}

				for (Element questionElement : matrix.getQuestions()) {
					int answered = numberOfAnswersMap.get(questionElement.getId());

					statistics.getRequestedRecords().put(questionElement.getId().toString(),
							survey.getNumberOfAnswerSets() - answered);
					double percent = survey.getNumberOfAnswerSets() == 0 ? 0
							: (double) (survey.getNumberOfAnswerSets() - answered)
									/ (double) survey.getNumberOfAnswerSets() * 100;
					statistics.getRequestedRecordsPercent().put(questionElement.getId().toString(), percent);
					statistics.getTotalsPercent().put(questionElement.getId().toString(), percent);
				}

				for (Element questionElement : matrix.getMissingQuestions()) {
					int answered = numberOfAnswersMap.get(questionElement.getId());

					statistics.getRequestedRecords().put(questionElement.getId().toString(),
							survey.getNumberOfAnswerSets() - answered);
					double percent = survey.getNumberOfAnswerSets() == 0 ? 0
							: (double) (survey.getNumberOfAnswerSets() - answered)
									/ (double) survey.getNumberOfAnswerSets() * 100;
					statistics.getRequestedRecordsPercent().put(questionElement.getId().toString(), percent);
					statistics.getTotalsPercent().put(questionElement.getId().toString(), percent);
				}
			}
		}

		try {
			answerService.save(statistics);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return statistics;
	}

	private void addReportingAnswers4Statistics(Question q, Map<Integer, Integer> map,
			Map<Integer, Map<Integer, Integer>> mapMatrix, Map<Integer, Map<String, Integer>> mapGallery,
			Map<Integer, Map<Integer, Integer>> mapRatingQuestion, Map<String, Object> values, String where, Map<String, Integer> mapNumberQuestion, Map<Integer, Map<String, Set<String>>> multipleChoiceSelectionsByAnswerset, Map<String, Map<Integer, Integer>> mapRankingQuestion,  Map<String, Map<Integer, Integer>> mapTargetDatasetQuestion) {
		if (q instanceof ChoiceQuestion) {
			ChoiceQuestion choice = (ChoiceQuestion) q;
			for (PossibleAnswer a : choice.getAllPossibleAnswers()) {
				int count = reportingService.getCount(survey, choice.getUniqueId(), a.getUniqueId(), false, false, false, where, values);
				map.put(a.getId(), count);
			}
			
			if (q instanceof SingleChoiceQuestion) {
				SingleChoiceQuestion scq = (SingleChoiceQuestion) q;
				if (scq.getIsTargetDatasetQuestion()) {
					mapTargetDatasetQuestion.put(scq.getUniqueId(), new HashMap<Integer, Integer>());
					List<SATargetDataset> datasets = selfassessmentService.getTargetDatasets(survey.getUniqueId());
					for (SATargetDataset dataset : datasets) {
						int count = reportingService.getCount(survey, choice.getUniqueId(), dataset.getId().toString(), false, false, false, where, values);
						mapTargetDatasetQuestion.get(scq.getUniqueId()).put(dataset.getId(), count);
					}
				}
			}			
			
			int count = reportingService.getCount(survey, choice.getUniqueId(), null, false, false, false, where, values);
			map.put(q.getId(), count);
			if (q instanceof MultipleChoiceQuestion) {
				Set<String> paUIDs = new HashSet<>();
				for (PossibleAnswer a : choice.getPossibleAnswers()) {
					paUIDs.add(a.getUniqueId());
				}
				String choiceUID = choice.getUniqueId();
				Map<Integer, Set<String>> answersByAnswerSetID = new HashMap<>();
				reportingService.getAnswerSetsByQuestionUID(survey, choiceUID, answersByAnswerSetID, where, values);
				for (Map.Entry<Integer, Set<String>> entry : answersByAnswerSetID.entrySet()) {
					Integer answerSetID = entry.getKey();
					Set<String> answerUIDs = entry.getValue();
					answerUIDs.retainAll(paUIDs);
					Map<String, Set<String>> multipleChoiceSelection = multipleChoiceSelectionsByAnswerset.getOrDefault(answerSetID, new HashMap<>());
					multipleChoiceSelection.put(choiceUID, answerUIDs);
					multipleChoiceSelectionsByAnswerset.put(answerSetID, multipleChoiceSelection);
				}
			}
		} else if (q instanceof GalleryQuestion) {
			GalleryQuestion g = (GalleryQuestion) q;
			if (!mapGallery.containsKey(g.getId())) {
				mapGallery.put(g.getId(), new HashMap<>());
			}
			for (com.ec.survey.model.survey.base.File file : g.getAllFiles()) {
				int countbyuid = reportingService.getCount(survey, g.getUniqueId(), file.getUid(), false, false, false, where,
						values);				
				mapGallery.get(g.getId()).put(file.getUid(), countbyuid);
			}
			
			//also add "old" answers (without file uid in answers)
			for (int i = 0; i < g.getFiles().size(); i++) {
				com.ec.survey.model.survey.base.File file = g.getFiles().get(i);
				int count =  reportingService.getCount(survey, g.getUniqueId(), Integer.toString(i), false, false, true, where,
						values);

				if (count > 0) {
					int oldcount = mapGallery.get(g.getId()).get(file.getUid());
					mapGallery.get(g.getId()).put(file.getUid(), count + oldcount);
				}
			}			
			
			int count = reportingService.getCount(survey, g.getUniqueId(), null, false, false, false, where, values);
			map.put(q.getId(), count);
		} else if (q instanceof Matrix) {
			Matrix matrix = (Matrix) q;
			for (Element matrixQuestion : matrix.getQuestions()) {
				for (Element matrixAnswer : matrix.getAnswers()) {
					if (!mapMatrix.containsKey(matrixQuestion.getId()))
						mapMatrix.put(matrixQuestion.getId(), new HashMap<>());
					int count = reportingService.getCount(survey, matrixQuestion.getUniqueId(),
							matrixAnswer.getUniqueId(), false, false, false, where, values);
					mapMatrix.get(matrixQuestion.getId()).put(matrixAnswer.getId(), count);
				}
				int count = reportingService.getCount(survey, matrixQuestion.getUniqueId(), null, false, false, false, where, values);
				map.put(matrixQuestion.getId(), count);
			}
		} else if (q instanceof RatingQuestion) {
			RatingQuestion rating = (RatingQuestion) q;
			for (Element childQuestion : rating.getQuestions()) {
				for (int i = 1; i <= rating.getNumIcons(); i++) {
					if (!mapRatingQuestion.containsKey(childQuestion.getId()))
						mapRatingQuestion.put(childQuestion.getId(), new HashMap<>());
					int count = reportingService.getCount(survey, childQuestion.getUniqueId(),
							Integer.toString(i) + Constants.PATH_DELIMITER, true, false, false, where, values);
					mapRatingQuestion.get(childQuestion.getId()).put(i, count);
				}
				int count = reportingService.getCount(survey, childQuestion.getUniqueId(), null, false, false, false, where, values);
				map.put(childQuestion.getId(), count);
			}
		} else if (q instanceof RankingQuestion) {
			RankingQuestion ranking = (RankingQuestion) q;
			int size = ranking.getAllChildElements().size();
			
			List<String> answers = reportingService.getAnswersByQuestionUID(survey, ranking.getUniqueId(), where, values);
			
			for (Element childQuestion : ranking.getAllChildElements()) {
				if (!mapRankingQuestion.containsKey(childQuestion.getUniqueId())) {
					HashMap<Integer, Integer> childMap = new HashMap<>();
					for (int i = 0; i < size; i++) {
						childMap.put(i, 0);
					}
					mapRankingQuestion.put(childQuestion.getUniqueId(), childMap);
				}
			}
				
			for (String answer : answers) {
				if (answer != null && answer.length() > 0) {
					String[] items = answer.split(";");
					int counter = 0;
					for (String id : items) {
						mapRankingQuestion.get(id).put(counter, mapRankingQuestion.get(id).get(counter) + 1);
						counter++;
					}
				}
			}
			
		} else if (q instanceof NumberQuestion) {
			NumberQuestion number = (NumberQuestion) q;
			if (number.showStatisticsForNumberQuestion()) {
				for (String answer : number.getAllPossibleAnswers()) {
					int count = reportingService.getCount(survey, number.getUniqueId(), answer, true, true, false, where, values);
					mapNumberQuestion.put(number.getUniqueId() + answer, count);
				}
			}
			int count = reportingService.getCount(survey, number.getUniqueId(), null, false, false, false, where, values);
			map.put(q.getId(), count);
		} else if (q instanceof FormulaQuestion) {
			FormulaQuestion formula = (FormulaQuestion) q;
			if (formula.showStatisticsForNumberQuestion()) {
				for (String answer : formula.getAllPossibleAnswers()) {
					int count = reportingService.getCount(survey, formula.getUniqueId(), answer, true, true, false, where, values);
					mapNumberQuestion.put(formula.getUniqueId() + answer, count);
				}
			}
			int count = reportingService.getCount(survey, formula.getUniqueId(), null, false, false, false, where, values);
			map.put(q.getId(), count);
		} else if (q instanceof ComplexTable) {
			ComplexTable table = (ComplexTable) q;
			for (ComplexTableItem child : table.getQuestionChildElements()) {
				if (child.getCellType() == ComplexTableItem.CellType.SingleChoice || child.getCellType() == ComplexTableItem.CellType.MultipleChoice) {					
					for (PossibleAnswer a : child.getAllPossibleAnswers()) {
						int count = reportingService.getCount(survey, child.getUniqueId(), a.getUniqueId(), false, false, false, where,
								values);
						map.put(a.getId(), count);
					}
					int count = reportingService.getCount(survey, child.getUniqueId(), null, false, false, false, where, values);
					map.put(child.getId(), count);
				} else if (child.getCellType() == ComplexTableItem.CellType.Number || child.getCellType() == ComplexTableItem.CellType.Formula) {
					if (child.showStatisticsForNumberQuestion()) {
						for (String answer : child.getPossibleNumberAnswers()) {
							int count = reportingService.getCount(survey, child.getUniqueId(), answer, true, true, false, where, values);
							mapNumberQuestion.put(child.getUniqueId() + answer, count);
						}
						int count = reportingService.getCount(survey, child.getUniqueId(), null, false, false, false, where, values);
						map.put(child.getId(), count);
					}			
				}
			}
			
		} else if (q instanceof ComplexTableItem) {
			ComplexTableItem child = (ComplexTableItem) q;			
			if (child.getCellType() == ComplexTableItem.CellType.SingleChoice || child.getCellType() == ComplexTableItem.CellType.MultipleChoice) {					
				for (PossibleAnswer a : child.getPossibleAnswers()) {
					int count = reportingService.getCount(survey, child.getUniqueId(), a.getUniqueId(), false, false, false, where,
							values);
					map.put(a.getId(), count);
				}
				int count = reportingService.getCount(survey, child.getUniqueId(), null, false, false, false, where, values);
				map.put(child.getId(), count);
			} else if (child.getCellType() == ComplexTableItem.CellType.Number || child.getCellType() == ComplexTableItem.CellType.Formula) {
				if (child.showStatisticsForNumberQuestion()) {
					for (String answer : child.getPossibleNumberAnswers()) {
						int count = reportingService.getCount(survey, child.getUniqueId(), answer, true, true, false, where, values);
						mapNumberQuestion.put(child.getUniqueId() + answer, count);
					}
					int count = reportingService.getCount(survey, child.getUniqueId(), null, false, false, false, where, values);
					map.put(child.getId(), count);
				}			
			}
		}
	}

	private void addMainAnswers4Statistics(Question q, Map<Integer, Integer> map,
			Map<Integer, Map<Integer, Integer>> mapMatrix, Map<Integer, Map<String, Integer>> mapGallery,
			Map<Integer, Map<Integer, Integer>> mapRatingQuestion,
			Map<String, Integer> countsUID, Map<String, Integer> gallerycounts,
			Map<String, Set<Integer>> answerSetQuestion,
			Map<String, Integer> matrixcountsUID, Map<String, Integer> ratingquestioncounts,
			Map<String, Integer> ratingquestioncountsUID, Map<String, Integer> mapNumberQuestion, Map<String, Map<Integer, Integer>> mapRankingQuestion, Map<String, List<String>> rankingQuestionAnswers,  Map<String, Map<Integer, Integer>> mapTargetDatasetQuestion) {
		if (q instanceof ChoiceQuestion) {
			ChoiceQuestion choice = (ChoiceQuestion) q;
			
			if (q instanceof SingleChoiceQuestion) {
				SingleChoiceQuestion scq = (SingleChoiceQuestion)q;
				if (scq.getIsTargetDatasetQuestion()) {
					List<SATargetDataset> datasets = selfassessmentService.getTargetDatasets(survey.getUniqueId());
					mapTargetDatasetQuestion.put(scq.getUniqueId(), new HashMap<Integer, Integer>());
					for (SATargetDataset dataset : datasets) {
						mapTargetDatasetQuestion.get(scq.getUniqueId()).put(dataset.getId(), countsUID.get(scq.getUniqueId() + "-" + dataset.getId()));
					}
				}
			}
			
			for (PossibleAnswer a : choice.getAllPossibleAnswers()) {
				if (countsUID.containsKey(a.getUniqueId() + "#" + q.getUniqueId())) {
					map.put(a.getId(), countsUID.get(a.getUniqueId() + "#" + q.getUniqueId()));
				}
			}
			map.put(q.getId(),
					answerSetQuestion.get(q.getUniqueId()) != null ? answerSetQuestion.get(q.getUniqueId()).size() : 0);
		} else if (q instanceof GalleryQuestion) {
			GalleryQuestion g = (GalleryQuestion) q;
			if (!mapGallery.containsKey(g.getId())) {
				mapGallery.put(g.getId(), new HashMap<>());
			}
			for (com.ec.survey.model.survey.base.File file : g.getAllFiles()) {
				mapGallery.get(g.getId()).put(file.getUid(), countsUID.getOrDefault(file.getUid() + "#" + g.getUniqueId(), 0));
			}
			//also add "old" answers (without file uid in answers)
			for (int i = 0; i < g.getFiles().size(); i++) {
				com.ec.survey.model.survey.base.File file = g.getFiles().get(i);
				int count = gallerycounts.getOrDefault(g.getUniqueId() + "-" + i, 0);
				if (count > 0) {
					int oldcount = mapGallery.get(g.getId()).get(file.getUid());
					mapGallery.get(g.getId()).put(file.getUid(), count + oldcount);
				}
			}
			map.put(q.getId(),
					answerSetQuestion.get(q.getUniqueId()) != null ? answerSetQuestion.get(q.getUniqueId()).size() : 0);
		} else if (q instanceof Matrix) {
			Matrix matrix = (Matrix) q;
			for (Element matrixQuestion : matrix.getQuestions()) {
				for (Element matrixAnswer : matrix.getAnswers()) {
					String key = matrixAnswer.getUniqueId() + "#" + matrixQuestion.getUniqueId();
					if (!mapMatrix.containsKey(matrixQuestion.getId()))
						mapMatrix.put(matrixQuestion.getId(), new HashMap<>());
					if (matrixcountsUID.containsKey(key)) {
						mapMatrix.get(matrixQuestion.getId()).put(matrixAnswer.getId(), matrixcountsUID.get(key));
					}
				}
				map.put(matrixQuestion.getId(),
						answerSetQuestion.get(matrixQuestion.getUniqueId()) != null
								? answerSetQuestion.get(matrixQuestion.getUniqueId()).size()
								: 0);
			}
		} else if (q instanceof RatingQuestion) {
			RatingQuestion rating = (RatingQuestion) q;
			for (Element childQuestion : rating.getQuestions()) {
				for (int i = 1; i <= rating.getNumIcons(); i++) {
					String key = i + "%" + childQuestion.getUniqueId();
					if (!mapRatingQuestion.containsKey(childQuestion.getId()))
						mapRatingQuestion.put(childQuestion.getId(), new HashMap<>());
					if (ratingquestioncountsUID.containsKey(key)) {
						mapRatingQuestion.get(childQuestion.getId()).put(i, ratingquestioncountsUID.get(key));
					} else {
						key = i + "%" + childQuestion.getId();
						mapRatingQuestion.get(childQuestion.getId()).put(i, ratingquestioncounts.getOrDefault(key, 0));
					}
				}
				map.put(childQuestion.getId(),
						answerSetQuestion.get(childQuestion.getUniqueId()) != null
								? answerSetQuestion.get(childQuestion.getUniqueId()).size()
								: 0);
			}
		} else if (q instanceof RankingQuestion) {
			RankingQuestion ranking = (RankingQuestion) q;
			int size = ranking.getAllChildElements().size();
			for (Element childQuestion : ranking.getAllChildElements()) {
				if (!mapRankingQuestion.containsKey(childQuestion.getUniqueId())) {
					HashMap<Integer, Integer> childMap = new HashMap<>();
					for (int i = 0; i < size; i++) {
						childMap.put(i, 0);
					}
					mapRankingQuestion.put(childQuestion.getUniqueId(), childMap);
				}
			}
				
			for (String answer : rankingQuestionAnswers.get(ranking.getUniqueId())) {
				if (answer != null && answer.length() > 0) {
					String[] items = answer.split(";");
					int counter = 0;
					for (String id : items) {
						if (mapRankingQuestion.get(id) != null) {
							mapRankingQuestion.get(id).put(counter, mapRankingQuestion.get(id).get(counter) + 1);
							counter++;
						}
					}
				}
			}
			
		} else if (q instanceof NumberQuestion || q instanceof FormulaQuestion) {
			map.put(q.getId(),
					answerSetQuestion.get(q.getUniqueId()) != null ? answerSetQuestion.get(q.getUniqueId()).size() : 0);
		} else if (q instanceof ComplexTable) {
			ComplexTable table = (ComplexTable) q;
			for (ComplexTableItem child : table.getQuestionChildElements()) {
				
				if (child.getCellType() == ComplexTableItem.CellType.SingleChoice || child.getCellType() == ComplexTableItem.CellType.MultipleChoice) {					
					for (PossibleAnswer a : child.getAllPossibleAnswers()) {
						if (countsUID.containsKey(a.getUniqueId() + "#" + child.getUniqueId())) {
							map.put(a.getId(), countsUID.get(a.getUniqueId() + "#" + child.getUniqueId()));
						}
					}
				}
				
				map.put(child.getId(),
						answerSetQuestion.get(child.getUniqueId()) != null ? answerSetQuestion.get(child.getUniqueId()).size() : 0);
			}
		} else if (q instanceof ComplexTableItem) {
			ComplexTableItem child = (ComplexTableItem) q;
			
			if (child.getCellType() == ComplexTableItem.CellType.SingleChoice || child.getCellType() == ComplexTableItem.CellType.MultipleChoice) {					
				for (PossibleAnswer a : child.getPossibleAnswers()) {
					if (countsUID.containsKey(a.getUniqueId() + "#" + q.getUniqueId())) {
						map.put(a.getId(), countsUID.get(a.getUniqueId() + "#" + q.getUniqueId()));
					}
				}
			}
			
			map.put(child.getId(),
					answerSetQuestion.get(child.getUniqueId()) != null ? answerSetQuestion.get(child.getUniqueId()).size() : 0);
		}
	}

	private void parseAnswers4Statistics(ScrollableResults results, Set<Integer> resultSets, boolean quiz,
			Set<String> multipleChoiceQuestionUids,
			Map<Integer, Map<String, Set<String>>> multipleChoiceSelectionsByAnswerset,
			Map<String, Set<Integer>> answerSetQuestion, Map<String, Integer> countsUID, Map<String, Integer> matrixcountsUID,
			Map<String, Integer> ratingquestioncountsUID, Map<String, Integer> gallerycounts, Map<Integer, String> uniqueIdsById, Set<String> numberQuestionUids, Map<String, Integer> mapNumberQuestion, Map<String, List<String>> rankingQuestionAnswers) {
		Object[] a = results.get();

		String pauid = (String) a[0];
		String quid = (String) a[1];
		String value = (String) a[2];
		Integer asId = ConversionTools.getValue(a[3]);

		if (!resultSets.contains(asId)) {
			resultSets.add(asId);
		}

		if (quiz && multipleChoiceQuestionUids.contains(quid)) {
			if (!multipleChoiceSelectionsByAnswerset.containsKey(asId)) {
				multipleChoiceSelectionsByAnswerset.put(asId, new HashMap<>());
			}

			if (!multipleChoiceSelectionsByAnswerset.get(asId).containsKey(quid)) {
				multipleChoiceSelectionsByAnswerset.get(asId).put(quid, new HashSet<>());
			}

			if (!multipleChoiceSelectionsByAnswerset.get(asId).get(quid).contains(pauid)) {
				multipleChoiceSelectionsByAnswerset.get(asId).get(quid).add(pauid);
			}
		}

		if (value != null) {
			if (!answerSetQuestion.containsKey(quid)) {
				answerSetQuestion.put(quid, new HashSet<>());
			}
			if (!answerSetQuestion.get(quid).contains(asId)) {
				answerSetQuestion.get(quid).add(asId);
			}

			if (value.indexOf('/') > 0) {
//				String key2 = value.substring(0, value.indexOf('/')) + "%" + qid.toString();
//				if (ratingquestioncounts.containsKey(key2)) {
//					ratingquestioncounts.put(key2, ratingquestioncounts.get(key2) + 1);
//				} else {
//					ratingquestioncounts.put(key2, 1);
//				}

				String key2 = value.substring(0, value.indexOf('/')) + "%" + quid;
				if (ratingquestioncountsUID.containsKey(key2)) {
					ratingquestioncountsUID.put(key2, ratingquestioncountsUID.get(key2) + 1);
				} else {
					ratingquestioncountsUID.put(key2, 1);
				}
			}

//			key = paid.toString();
//			if (paid != 0) {
//				if (counts.containsKey(key)) {
//					counts.put(key, counts.get(key) + 1);
//				} else {
//					counts.put(key, 1);
//				}
//			} else {
//				if (pauid == null && value != null && org.apache.commons.lang3.StringUtils.isNumeric(value)) {
//					//gallery				
//					//"old" style
//					String galleryKey = qid.toString() + "-" + value;
//					if (gallerycounts.containsKey(galleryKey)) {
//						gallerycounts.put(galleryKey, gallerycounts.get(galleryKey) + 1);
//					} else {
//						gallerycounts.put(galleryKey, 1);
//					}
//				}
//			}

//			if (uniqueIdsById.containsKey(paid)) {
//				pauid = uniqueIdsById.get(paid);
//			}

//			if (uniqueIdsById.containsKey(qid)) {
//				quid = uniqueIdsById.get(qid);
//			}

			if (pauid != null) {
				
				if (pauid.equals("TARGETDATASET")) {
					String key = quid + "-" + value;
					if (countsUID.containsKey(key)) {
						countsUID.put(key, countsUID.get(key) + 1);
					} else {
						countsUID.put(key, 1);
					}
				} else {				
					String key = pauid + "#" + quid;
					if (countsUID.containsKey(key)) {
						countsUID.put(key, countsUID.get(key) + 1);
					} else {
						countsUID.put(key, 1);
					}
	
					key = pauid + "#" + quid;
					if (matrixcountsUID.containsKey(key)) {
						matrixcountsUID.put(key, matrixcountsUID.get(key) + 1);
					} else {
						matrixcountsUID.put(key, 1);
					}
				}
			} else {
				if (value != null && org.apache.commons.lang3.StringUtils.isNumeric(value) && quid != null) {
					//gallery										
					String galleryKey = quid + "-" + value;
					if (gallerycounts.containsKey(galleryKey)) {
						gallerycounts.put(galleryKey, gallerycounts.get(galleryKey) + 1);
					} else {
						gallerycounts.put(galleryKey, 1);
					}
				}
			}
			
			if (numberQuestionUids.contains(quid)) {
				String key = quid + Double.valueOf(value).intValue();
				if (mapNumberQuestion.containsKey(key)) {
					mapNumberQuestion.put(key, mapNumberQuestion.get(key) + 1);
				} else {
					mapNumberQuestion.put(key, 1);
				}
			}
		}
		
		if (rankingQuestionAnswers.containsKey(quid)) {
			rankingQuestionAnswers.get(quid).add(value);
		}
	}

	@Transactional
	public NumberQuestionStatistics getAnswers4NumberQuestionStatistics(Survey survey, Question question) throws TooManyFiltersException, MessageException {
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> values = new HashMap<>();
		NumberQuestionStatistics numberQuestionStats = new NumberQuestionStatistics();
		Map<String, Integer> map = new HashMap<>();

		if (!survey.getIsDelphi() && reportingService.OLAPTableExists(survey.getUniqueId(), survey.getIsDraft())) {
			// we try to get the data from the reporting database
			String where = ReportingService.getWhereClause(filter, values, survey);
			List<String> answers = reportingService.getAnswersByQuestionUID(survey, question.getUniqueId(), where, values);

			for (String v : answers) {
				Integer count = map.getOrDefault(v, 0);
				map.put(v, count + 1);
				numberQuestionStats.incrementNumberVotes();
			}
		} else {
			values.put("questionuid", question.getUniqueId());
			String where = answerService.getSql(null, survey.getId(), filter, values, true);
			String sql = "SELECT a.VALUE FROM ANSWERS_SET ans LEFT OUTER JOIN ANSWERS a ON a.AS_ID = ans.ANSWER_SET_ID where a.QUESTION_UID";
			sql += " = :questionuid AND ans.ANSWER_SET_ID IN (" + where + ")";

			NativeQuery query = session.createSQLQuery(sql);
			query.setReadOnly(true);

			for (Entry<String, Object> entry : values.entrySet()) {
				if (entry.getValue() instanceof String) {
					query.setString(entry.getKey(), (String) entry.getValue());
				} else if (entry.getValue() instanceof Integer) {
					query.setInteger(entry.getKey(), (Integer) entry.getValue());
				} else if (entry.getValue() instanceof Date) {
					query.setTimestamp(entry.getKey(), (Date) entry.getValue());
				}
			}

			query.setFetchSize(Integer.MIN_VALUE);
			ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

			while (results != null && results.next()) {
				Object[] a = results.get();
				String value = (String) a[0];
		
				Integer count = map.getOrDefault(value, 0);
				map.put(value, count + 1);
				numberQuestionStats.incrementNumberVotes();
			}
			if (null != results) {
				results.close();
			}

		}

		numberQuestionStats.setValuesMagnitude(map);
		return numberQuestionStats;
	}
	
	@Transactional
	public List<String> getAnswers4FreeTextStatistics(Survey survey, Question question) throws TooManyFiltersException, MessageException {
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> values = new HashMap<>();
		List<String> answers = new ArrayList<>();

		if (!survey.getIsDelphi() && reportingService.OLAPTableExists(survey.getUniqueId(), survey.getIsDraft())) {
			// we try to get the data from the reporting database
			String where = ReportingService.getWhereClause(filter, values, survey);
			answers = reportingService.getAnswersByQuestionUID(survey, question.getUniqueId(), where, values);
		} else {

			String where = answerService.getSql(null, survey.getId(), filter, values, true);
			String sql = "SELECT a.VALUE FROM ANSWERS_SET ans LEFT OUTER JOIN ANSWERS a ON a.AS_ID = ans.ANSWER_SET_ID where a.QUESTION_UID";
			sql += " = :questionuid AND ans.ANSWER_SET_ID IN (" + where + ")";
			values.put("questionuid", question.getUniqueId());

			NativeQuery query = session.createSQLQuery(sql);
			query.setReadOnly(true);

			for (Entry<String, Object> entry : values.entrySet()) {
				if (entry.getValue() instanceof String) {
					query.setString(entry.getKey(), (String) entry.getValue());
				} else if (entry.getValue() instanceof Integer) {
					query.setInteger(entry.getKey(), (Integer) entry.getValue());
				} else if (entry.getValue() instanceof Date) {
					query.setTimestamp(entry.getKey(), (Date) entry.getValue());
				}
			}

			query.setFetchSize(Integer.MIN_VALUE);
			ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

			while (results != null && results.next()) {
				Object[] a = results.get();
				String value = (String) a[0];
				answers.add(value);
			}
			if (null != results) {
				results.close();
			}

		}
		
		return answers;
	}

	@Transactional
	public int getAnswers4Statistics(Survey survey, Question question, Map<Integer, Integer> map,
			Map<Integer, Map<Integer, Integer>> mapMatrix, Map<Integer, Map<String, Integer>> mapGallery,
			Map<Integer, Map<String, Set<String>>> multipleChoiceSelectionsByAnswerset,
			Map<Integer, Map<Integer, Integer>> mapRatingQuestion, Map<String, Integer> mapNumberQuestion, Map<String, Map<Integer, Integer>> mapRankingQuestion, Map<String, Map<Integer, Integer>> mapTargetDatasetQuestion) throws TooManyFiltersException, MessageException {

		boolean quiz = survey.getIsQuiz();
		Set<String> multipleChoiceQuestionUids = new HashSet<>();
		if (quiz) {
			for (Question q : survey.getQuestions()) {
				if (q instanceof MultipleChoiceQuestion) {
					multipleChoiceQuestionUids.add(q.getUniqueId());
				}
			}
		}
		
		//we do not use the reporting database for delphi surveys as the data has to be up to date at any time
		if (!survey.getIsDelphi() && reportingService.OLAPTableExists(survey.getUniqueId(), survey.getIsDraft())) {
			Map<String, Object> values = new HashMap<>();
			String where = ReportingService.getWhereClause(filter, values, survey);

			try {
				addReportingAnswers4Statistics(question, map, mapMatrix, mapGallery, mapRatingQuestion, values, where, mapNumberQuestion, multipleChoiceSelectionsByAnswerset, mapRankingQuestion, mapTargetDatasetQuestion);

				return reportingService.getCount(survey, where, values);
			} catch (Exception e) {
				logger.info(e.getLocalizedMessage(), e);
			}
		}
		
		Set<String> numberQuestionUids = new HashSet<>();
		Map<String, List<String>> rankingQuestionAnswers = new HashMap<>();
		for (Question q : survey.getQuestions()) {
			if (q instanceof NumberQuestion && ((NumberQuestion)q).showStatisticsForNumberQuestion()) {
				numberQuestionUids.add(q.getUniqueId());
			}
			if (q instanceof FormulaQuestion && ((FormulaQuestion)q).showStatisticsForNumberQuestion()) {
				numberQuestionUids.add(q.getUniqueId());
			}
			if (q instanceof RankingQuestion) {
				rankingQuestionAnswers.put(q.getUniqueId(), new ArrayList<>());
			}
			if (q instanceof ComplexTable) {
				ComplexTable table = (ComplexTable) q;
				for (ComplexTableItem item : table.getQuestionChildElements())
				{
					if ((item.getCellType() == ComplexTableItem.CellType.Number || item.getCellType() == ComplexTableItem.CellType.Formula) && item.showStatisticsForNumberQuestion()) {
						numberQuestionUids.add(item.getUniqueId());
					}
				}
			}
		}
		
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> values = new HashMap<>();
		Map<Integer, String> uniqueIdsById = SurveyService.getUniqueIdsById(survey);

		String where = answerService.getSql(null, survey.getId(), filter, values, true);

		String sql = "select a.PA_UID, a.QUESTION_UID, a.VALUE, ans.ANSWER_SET_ID FROM ANSWERS_SET ans LEFT OUTER JOIN ANSWERS a ON a.AS_ID = ans.ANSWER_SET_ID where a.QUESTION_UID";
		
		if (question instanceof Matrix || question instanceof RatingQuestion)
		{
			String questionuids = "";
			
			if (question instanceof Matrix)
			{
				Matrix matrix = (Matrix)question;
				questionuids = matrix.getQuestions().stream().map(Element::getUniqueId).map(s -> "'" + s + "'").collect(Collectors.joining(","));
			} else {
				RatingQuestion rating = (RatingQuestion)question;
				questionuids = rating.getQuestions().stream().map(Element::getUniqueId).map(s -> "'" + s + "'").collect(Collectors.joining(","));
			}
			
			sql += " IN (" + questionuids + ") AND ans.ANSWER_SET_ID IN (" + where + ")";		
		} else {
			sql += " = :questionuid AND ans.ANSWER_SET_ID IN ("
					+ where + ")";
			values.put("questionuid", question.getUniqueId());
		}	
	
		NativeQuery query = session.createSQLQuery(sql);
		query.setReadOnly(true);

		for (Entry<String, Object> entry : values.entrySet()) {
			if (entry.getValue() instanceof String) {
				query.setString(entry.getKey(), (String) entry.getValue());
			} else if (entry.getValue() instanceof Integer) {
				query.setInteger(entry.getKey(), (Integer) entry.getValue());
			} else if (entry.getValue() instanceof Date) {
				query.setTimestamp(entry.getKey(), (Date) entry.getValue());
			}
		}

		query.setFetchSize(Integer.MIN_VALUE);
		ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

		Map<String, Integer> countsUID = new HashMap<>();
		Map<String, Integer> matrixcountsUID = new HashMap<>();
		Map<String, Integer> ratingquestioncounts = new HashMap<>();
		Map<String, Integer> ratingquestioncountsUID = new HashMap<>();
		Map<String, Integer> gallerycounts = new HashMap<>();
		Set<Integer> resultSets = new HashSet<>();
		Map<String, Set<Integer>> answerSetQuestion = new HashMap<>();		

		while (results != null && results.next()) {
			parseAnswers4Statistics(results, resultSets, quiz, multipleChoiceQuestionUids, multipleChoiceSelectionsByAnswerset, answerSetQuestion, countsUID, matrixcountsUID,
					ratingquestioncountsUID, gallerycounts, uniqueIdsById, numberQuestionUids, mapNumberQuestion, rankingQuestionAnswers);
		}
		results.close();

		addMainAnswers4Statistics(question, map, mapMatrix, mapGallery, mapRatingQuestion, countsUID,
				gallerycounts, answerSetQuestion, matrixcountsUID, ratingquestioncounts,
				ratingquestioncountsUID, mapNumberQuestion, mapRankingQuestion, rankingQuestionAnswers, mapTargetDatasetQuestion);		

		return resultSets.size();
	}

	@Transactional
	public int getAnswers4Statistics(Survey survey, ResultFilter filter, Map<Integer, Integer> map,
			Map<Integer, Map<Integer, Integer>> mapMatrix, Map<Integer, Map<String, Integer>> mapGallery,
			Map<Integer, Map<String, Set<String>>> multipleChoiceSelectionsByAnswerset,
			Map<Integer, Map<Integer, Integer>> mapRatingQuestion, Map<String, Integer> mapNumberQuestion, Map<String, Map<Integer, Integer>> mapRankingQuestion, Map<String, List<String>> rankingQuestionAnswers, Map<String, Map<Integer, Integer>> mapTargetDatasetQuestion) throws TooManyFiltersException, MessageException {

		boolean quiz = survey.getIsQuiz();
		Set<String> multipleChoiceQuestionUids = new HashSet<>();
		if (quiz) {
			for (Question q : survey.getQuestions()) {
				if (q instanceof MultipleChoiceQuestion) {
					multipleChoiceQuestionUids.add(q.getUniqueId());
				}
			}
		}
		
		Set<String> numberQuestionUids = new HashSet<>();
		for (Question q : survey.getQuestions()) {
			if (q instanceof NumberQuestion && ((NumberQuestion)q).showStatisticsForNumberQuestion()) {
				numberQuestionUids.add(q.getUniqueId());
			}
			if (q instanceof FormulaQuestion && ((FormulaQuestion)q).showStatisticsForNumberQuestion()) {
				numberQuestionUids.add(q.getUniqueId());
			}
			if (q instanceof RankingQuestion) {
				rankingQuestionAnswers.put(q.getUniqueId(), new ArrayList<>());
			}
			if (q instanceof ComplexTable) {
				ComplexTable table = (ComplexTable) q;
				for (ComplexTableItem child : table.getQuestionChildElements()) {
					if ((child.getCellType() == ComplexTableItem.CellType.Number || child.getCellType() == ComplexTableItem.CellType.Formula) && child.showStatisticsForNumberQuestion()) {
						numberQuestionUids.add(child.getUniqueId());
					}
				}
			}
		}		
		
		if (reportingService.OLAPTableExists(survey.getUniqueId(), survey.getIsDraft())) {

			Map<String, Object> values = new HashMap<>();
			String where = ReportingService.getWhereClause(filter, values, survey);

			try {
				for (Question q : survey.getQuestions()) {
					addReportingAnswers4Statistics(q, map, mapMatrix, mapGallery, mapRatingQuestion, values, where, mapNumberQuestion, multipleChoiceSelectionsByAnswerset, mapRankingQuestion, mapTargetDatasetQuestion);
				}

				return reportingService.getCount(survey, where, values);
			} catch (Exception e) {
				logger.info(e.getLocalizedMessage(), e);
				// as a fallback the statistics are created using the main database
				// therefore we have to reset the maps in order not to count any answer twice
				map.clear();
				mapMatrix.clear();
				mapGallery.clear();
				mapRankingQuestion.clear();
				mapNumberQuestion.clear();
				multipleChoiceSelectionsByAnswerset.clear();
				mapRankingQuestion.clear();
			}
		}

		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> values = new HashMap<>();
		Map<Integer, String> uniqueIdsById = SurveyService.getUniqueIdsById(survey);

		String where = answerService.getSql(null, survey.getId(), filter, values, true);

		String sql = "select a.PA_UID, a.QUESTION_UID, a.VALUE, ans.ANSWER_SET_ID FROM ANSWERS_SET ans LEFT OUTER JOIN ANSWERS a ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID IN ("
				+ where + ")";

		NativeQuery query = session.createSQLQuery(sql);
		query.setReadOnly(true);

		for (Entry<String, Object> entry : values.entrySet()) {
			if (entry.getValue() instanceof String) {
				query.setString(entry.getKey(), (String) entry.getValue());
			} else if (entry.getValue() instanceof Integer) {
				query.setInteger(entry.getKey(), (Integer) entry.getValue());
			} else if (entry.getValue() instanceof Date) {
				query.setTimestamp(entry.getKey(), (Date) entry.getValue());
			}
		}

		query.setFetchSize(Integer.MIN_VALUE);
		ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

		Map<String, Integer> countsUID = new HashMap<>();
		Map<String, Integer> matrixcountsUID = new HashMap<>();
		Map<String, Integer> ratingquestioncounts = new HashMap<>();
		Map<String, Integer> ratingquestioncountsUID = new HashMap<>();
		Map<String, Integer> gallerycounts = new HashMap<>();
		Set<Integer> resultSets = new HashSet<>();
		Map<String, Set<Integer>> answerSetQuestion = new HashMap<>();
		
		while (results != null && results.next()) {
			parseAnswers4Statistics(results, resultSets, quiz, multipleChoiceQuestionUids, multipleChoiceSelectionsByAnswerset, answerSetQuestion, countsUID, matrixcountsUID,
					ratingquestioncountsUID, gallerycounts, uniqueIdsById, numberQuestionUids, mapNumberQuestion, rankingQuestionAnswers);

		}
		results.close();

		for (Question q : survey.getQuestions()) {
			addMainAnswers4Statistics(q, map, mapMatrix, mapGallery, mapRatingQuestion, countsUID,
					gallerycounts, answerSetQuestion, matrixcountsUID, ratingquestioncounts,
					ratingquestioncountsUID, mapNumberQuestion, mapRankingQuestion, rankingQuestionAnswers, mapTargetDatasetQuestion);
		}

		return resultSets.size();
	}

	public int addStatistics4Matrix(Survey survey, Element answer, Element question, Statistics statistics,
			Map<Integer, Map<Integer, Integer>> numberOfAnswersMapMatrix) {
		String id = question.getId().toString() + answer.getId().toString();
		int numberOfAnswers = 0;
		int total = survey.getNumberOfAnswerSets();
		double percent = 0;

		if (numberOfAnswersMapMatrix.containsKey(question.getId())) {
			numberOfAnswers = numberOfAnswersMapMatrix.get(question.getId()).getOrDefault(answer.getId(), 0);
			percent = total == 0 ? 0 : (((double) numberOfAnswers) / ((double) total) * 100);
		}

		statistics.getRequestedRecords().put(id, numberOfAnswers);
		statistics.getRequestedRecordsPercent().put(id, percent);
		statistics.getTotalsPercent().put(id, percent);

		return numberOfAnswers;
	}

	public int addStatistics4RatingQuestion(Survey survey, Integer answer, Element question, Statistics statistics,
			Map<Integer, Map<Integer, Integer>> numberOfAnswersMapRatingQuestion) {
		String id = question.getId().toString() + answer.toString();
		int numberOfAnswers = 0;
		int total = survey.getNumberOfAnswerSets();
		double percent = 0;

		if (numberOfAnswersMapRatingQuestion.containsKey(question.getId())) {
			numberOfAnswers = numberOfAnswersMapRatingQuestion.get(question.getId()).getOrDefault(answer, 0);
			percent = total == 0 ? 0 : (((double) numberOfAnswers) / ((double) total) * 100);
		}

		statistics.getRequestedRecords().put(id, numberOfAnswers);
		statistics.getRequestedRecordsPercent().put(id, percent);
		statistics.getTotalsPercent().put(id, percent);

		return numberOfAnswers;
	}
	
	public void addStatistics4RankingQuestion(Survey survey, RankingQuestion ranking, Statistics statistics, Map<String, Map<Integer, Integer>> numberOfAnswersMapRankingQuestion) {
		int size = ranking.getAllChildElements().size();
		int total = survey.getNumberOfAnswerSets();
		int maxAnswered = 0;
		List<RankingItem> rankingItems = ranking.getAllChildElements();
		for (int j = 0; j < size; j++) {
			Element child = rankingItems.get(j);
			int score = 0;
			int answered = 0;
			for (int i = 0; i < size; i++) {
				int value = numberOfAnswersMapRankingQuestion.get(child.getUniqueId()).get(i);
				statistics.getRequestedRecordsRankingScore().put(child.getId() + "-" + i, value);
				score += (size - i) * value;
				
				answered += value;
			}

			maxAnswered = Math.max(answered, maxAnswered);

			statistics.getRequestedRecordsRankingScore().put(child.getId().toString(), score);
		}

		for (int j = 0; j < size; j++) {
			Element child = rankingItems.get(j);
			for (int i = 0; i < size; i++) {
				int value = statistics.getRequestedRecordsRankingScore().get(child.getId() + "-" + i);
				statistics.getRequestedRecordsRankingPercentScore().put(child.getId() + "-" + i, divideToPercent(value, maxAnswered));
			}
			int score = statistics.getRequestedRecordsRankingScore().get(child.getId().toString());
			statistics.getRequestedRecordsRankingPercentScore().put(child.getId().toString(), divide(score, maxAnswered));
		}

		statistics.getRequestedRecordsRankingScore().put(ranking.getId().toString(), maxAnswered);

		statistics.getRequestedRecords().put(ranking.getId().toString(), total - maxAnswered);
		double percent = total == 0 ? 0 : (double) (total - maxAnswered) / (double) total * 100;
		statistics.getRequestedRecordsPercent().put(ranking.getId().toString(), percent);
	}
	
	private double divide(int a, int b) {
		if (a == 0 || b == 0) return 0;
		double result = Double.valueOf(a) / Double.valueOf(b);
		result = Math.floor(result * 100) / 100;
		return result;
	}
	
	private double divideToPercent(int a, int b) {
		if (a == 0 || b == 0) return 0;
		double result = Double.valueOf(a) / Double.valueOf(b) * 100;
		result = Math.floor(result * 100) / 100;
		return result;
	}

	public void addChoiceStatistics(Survey survey, Question question, Statistics statistics,
			Map<Integer, Integer> numberOfAnswersMap,
			Map<Integer, Map<String, Set<String>>> multipleChoiceSelectionsByAnswerset, Map<String, Map<Integer, Integer>> mapTargetDatasetQuestion) {
		boolean quiz = survey.getIsQuiz() && question.getScoring() > 0;
		int total = survey.getNumberOfAnswerSets();
		int correct = 0;
		
		List<PossibleAnswer> answers = question instanceof ChoiceQuestion ? ((ChoiceQuestion)question).getAllPossibleAnswers() : ((ComplexTableItem)question).getAllPossibleAnswers();
		
		if (survey.getIsSelfAssessment() && question instanceof SingleChoiceQuestion) {
			SingleChoiceQuestion scq = (SingleChoiceQuestion)question;
			if (scq.getIsTargetDatasetQuestion()) {
				List<SATargetDataset> datasets = selfassessmentService.getTargetDatasets(survey.getUniqueId());
				for (SATargetDataset dataset: datasets) {
					int numberOfAnswers = 0;
					
					if (mapTargetDatasetQuestion.containsKey(scq.getUniqueId()) && mapTargetDatasetQuestion.get(scq.getUniqueId()).containsKey(dataset.getId())) {
						if (mapTargetDatasetQuestion.get(scq.getUniqueId()).get(dataset.getId()) != null) {
							numberOfAnswers = mapTargetDatasetQuestion.get(scq.getUniqueId()).get(dataset.getId());
						}
					}
					double percent = total == 0 ? 0 : (double) numberOfAnswers / (double) total * 100;
					
					statistics.getRequestedRecords().put(scq.getUniqueId() + "-" + dataset.getId().toString(), numberOfAnswers);
					statistics.getRequestedRecordsPercent().put(scq.getUniqueId() + "-" + dataset.getId().toString(), percent);
					statistics.getTotalsPercent().put(scq.getUniqueId() + "-" + dataset.getId().toString(), percent);
				}
			}
		}

		for (PossibleAnswer answer : answers) {
			int numberOfAnswers = numberOfAnswersMap.getOrDefault(answer.getId(), 0);

			double percent = total == 0 ? 0 : (double) numberOfAnswers / (double) total * 100;

			statistics.getRequestedRecords().put(answer.getId().toString(), numberOfAnswers);
			statistics.getRequestedRecordsPercent().put(answer.getId().toString(), percent);
			statistics.getTotalsPercent().put(answer.getId().toString(), percent);

			if (quiz && answer.getScoring().isCorrect()) {
				correct += numberOfAnswers;
			}
		}

		if (quiz) {
			if (question instanceof MultipleChoiceQuestion) {
				// correct means exactly all correct answers are selected
				correct = 0;
				for (Entry<Integer, Map<String, Set<String>>> entry : multipleChoiceSelectionsByAnswerset.entrySet()) {
					int ascorrect = 1;
					if (entry.getValue().containsKey(question.getUniqueId())) {
						Set<String> answerUIDs = entry.getValue().get(question.getUniqueId());
						for (PossibleAnswer answer : answers) {
							if ((answer.getScoring().isCorrect() && !answerUIDs.contains(answer.getUniqueId()))
									|| (!answer.getScoring().isCorrect()
											&& answerUIDs.contains(answer.getUniqueId()))) {
								ascorrect = 0;
								break;
							}
						}
					}
					correct += ascorrect;
				}
			}

			statistics.getRequestedRecordsScore().put(question.getId().toString(), correct);
			double percent = total == 0 ? 0 : (double) correct / (double) total * 100;
			statistics.getRequestedRecordsPercentScore().put(question.getId().toString(), percent);
		}

		int answered = numberOfAnswersMap.get(question.getId());

		statistics.getRequestedRecords().put(question.getId().toString(), survey.getNumberOfAnswerSets() - answered);
		double percent = total == 0 ? 0 : (double) (survey.getNumberOfAnswerSets() - answered) / (double) total * 100;
		statistics.getRequestedRecordsPercent().put(question.getId().toString(), percent);
		statistics.getTotalsPercent().put(question.getId().toString(), percent);
	}

	private void addStatistics4Quiz(Survey survey, Question question, Statistics statistics,
			Map<Integer, Map<Integer, Integer>> scorePoints, Map<String, Integer> questionMaximumScores) {
		int total = survey.getNumberOfAnswerSets();
		int correct = 0;
		int maxScore = 0;
		if (questionMaximumScores != null && questionMaximumScores.containsKey(question.getUniqueId())) {
			maxScore = questionMaximumScores.get(question.getUniqueId());
		}

		if (scorePoints.containsKey(question.getId())) {
			for (int score : scorePoints.get(question.getId()).keySet()) {
				if (score == maxScore) {
					correct += scorePoints.get(question.getId()).get(score);
				}
			}
		}

		statistics.getRequestedRecordsScore().put(question.getId().toString(), correct);
		double percent = total == 0 ? 0 : (double) correct / (double) total * 100;
		statistics.getRequestedRecordsPercentScore().put(question.getId().toString(), percent);
	}

	private void addStatistics4Gallery(Survey survey, GalleryQuestion question, Statistics statistics,
			Map<Integer, Map<String, Integer>> numberOfAnswersMap, Map<Integer, Integer> numberOfAnswersMap2) {
		int total = survey.getNumberOfAnswerSets();

		for (com.ec.survey.model.survey.base.File file: question.getAllFiles()) {

			int numberOfAnswers = 0;
			if (numberOfAnswersMap.containsKey(question.getId())
					&& numberOfAnswersMap.get(question.getId()).containsKey(file.getUid())) {
				numberOfAnswers = numberOfAnswersMap.get(question.getId()).get(file.getUid());
			}
			double percent = total == 0 ? 0 : (double) numberOfAnswers / (double) total * 100;

			statistics.getRequestedRecords().put(question.getId() + "-" + file.getUid(), numberOfAnswers);
			statistics.getRequestedRecordsPercent().put(question.getId() + "-" + file.getUid(), percent);
			statistics.getTotalsPercent().put(question.getId() + "-" + file.getUid(), percent);
		}

		int answered = numberOfAnswersMap2.get(question.getId());

		statistics.getRequestedRecords().put(question.getId().toString(), survey.getNumberOfAnswerSets() - answered);
		double percent = total == 0 ? 0 : (double) (survey.getNumberOfAnswerSets() - answered) / (double) total * 100;
		statistics.getRequestedRecordsPercent().put(question.getId().toString(), percent);
		statistics.getTotalsPercent().put(question.getId().toString(), percent);
	}

	private void addStatistics4NumberQuestion(Survey survey, Question number, Statistics statistics,
			Map<String, Integer> numberOfNumberAnswersMap, Map<Integer, Integer> numberOfAnswersMap) {
		int total = survey.getNumberOfAnswerSets();

		List<String> answers;
		if (number instanceof NumberQuestion) {
			answers = ((NumberQuestion)number).getAllPossibleAnswers();
		} else if (number instanceof FormulaQuestion) {
			answers = ((FormulaQuestion)number).getAllPossibleAnswers();
		} else {
			answers = ((ComplexTableItem)number).getPossibleNumberAnswers();
		}
		
		for (String answer : answers) {
			int numberOfAnswers = 0;
			if (numberOfNumberAnswersMap.containsKey(number.getUniqueId() + answer)) {
				numberOfAnswers = numberOfNumberAnswersMap.get(number.getUniqueId() + answer);
			}
			double percent = total == 0 ? 0 : (double) numberOfAnswers / (double) total * 100;

			statistics.getRequestedRecords().put(number.getId() + answer, numberOfAnswers);
			statistics.getRequestedRecordsPercent().put(number.getId() + answer, percent);
			statistics.getTotalsPercent().put(number.getId() + answer, percent);
		}

		int answered = numberOfAnswersMap.get(number.getId());

		statistics.getRequestedRecords().put(number.getId().toString(), survey.getNumberOfAnswerSets() - answered);
		double percent = total == 0 ? 0 : (double) (survey.getNumberOfAnswerSets() - answered) / (double) total * 100;
		statistics.getRequestedRecordsPercent().put(number.getId().toString(), percent);
		statistics.getTotalsPercent().put(number.getId().toString(), percent);
	}

//	private void addStatisticsForFormulaQuestion(Survey survey, Question formula, Statistics statistics,
//											  Map<String, Integer> numberOfNumberAnswersMap, Map<Integer, Integer> numberOfAnswersMap) {
//		int total = survey.getNumberOfAnswerSets();
//
//		for (String answer : formula.getAllPossibleAnswers()) {
//			int numberOfAnswers = 0;
//			if (numberOfNumberAnswersMap.containsKey(formula.getUniqueId() + answer)) {
//				numberOfAnswers = numberOfNumberAnswersMap.get(formula.getUniqueId() + answer);
//			}
//			double percent = total == 0 ? 0 : (double) numberOfAnswers / (double) total * 100;
//
//			statistics.getRequestedRecords().put(formula.getId() + answer, numberOfAnswers);
//			statistics.getRequestedRecordsPercent().put(formula.getId() + answer, percent);
//			statistics.getTotalsPercent().put(formula.getId() + answer, percent);
//		}
//
//		int answered = numberOfAnswersMap.get(formula.getId());
//
//		statistics.getRequestedRecords().put(formula.getId().toString(), survey.getNumberOfAnswerSets() - answered);
//		double percent = total == 0 ? 0 : (double) (survey.getNumberOfAnswerSets() - answered) / (double) total * 100;
//		statistics.getRequestedRecordsPercent().put(formula.getId().toString(), percent);
//		statistics.getTotalsPercent().put(formula.getId().toString(), percent);
//	}
}
