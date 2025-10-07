package com.ec.survey.service;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.Answer;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Form;
import com.ec.survey.model.selfassessment.SACriterion;
import com.ec.survey.model.selfassessment.SAReportConfiguration;
import com.ec.survey.model.selfassessment.SAResult;
import com.ec.survey.model.selfassessment.SAScore;
import com.ec.survey.model.selfassessment.SAScoreCard;
import com.ec.survey.model.selfassessment.SATargetDataset;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.PossibleAnswer;
import com.ec.survey.model.survey.Question;
import com.ec.survey.model.survey.SingleChoiceQuestion;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.tools.ImportResult;

import org.hibernate.query.Query;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import javax.annotation.Resource;

@Service("selfassessmentService")
public class SelfAssessmentService extends BasicService {
	
	@Resource(name = "answerService")
	protected AnswerService answerService;
	
	@Transactional(readOnly = true)
	public List<SACriterion> getCriteria(String surveyUID) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM SACriterion WHERE surveyUID = :surveyUID";
		@SuppressWarnings("unchecked")
		Query<SACriterion> query = session.createQuery(hql).setParameter("surveyUID", surveyUID);
		List<SACriterion> list = query.list();
		return list;
	}
	
	@Transactional(readOnly = false)
	public SACriterion addCriterion(SACriterion criterion) throws MessageException {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(criterion);
		session.flush();
		return criterion;
	}

	@Transactional(readOnly = false)
	public void updateCriterion(int id, String surveyUID, String name, String acronym, String type) throws MessageException {
		Session session = sessionFactory.getCurrentSession();
		SACriterion c = session.get(SACriterion.class, id);		
			
		if (!c.getSurveyUID().equals(surveyUID)) {
			throw new MessageException("wrong survey");
		}
		
		c.setName(name);
		c.setAcronym(acronym);
		c.setType(type);
		
		session.saveOrUpdate(c);
	}
	
	@Transactional(readOnly = false)
	public void deleteCriterion(int id, String surveyUID) throws MessageException {
		Session session = sessionFactory.getCurrentSession();
		SACriterion c = session.get(SACriterion.class, id);		
			
		if (!c.getSurveyUID().equals(surveyUID)) {
			throw new MessageException("wrong survey");
		}
		
		session.delete(c);
	}

	private void deleteCriteria(String surveyUID) {
		Session session = sessionFactory.getCurrentSession();
		Query<SACriterion> query = session.createQuery("DELETE FROM SACriterion WHERE surveyUID = :surveyUID").setParameter("surveyUID", surveyUID);
		query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public String[] getTypesForSurvey(String surveyUID, String term) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "SELECT DISTINCT type FROM SACriterion WHERE type LIKE :term AND surveyUID = :uid";
		@SuppressWarnings("unchecked")
		Query<String> query = session.createQuery(hql).setParameter("uid", surveyUID).setParameter("term", "%" + term + "%");
		List<String> list = query.list();
		return list.toArray(new String[0]);
	}
	
	@Transactional(readOnly = true)
	public Map<Integer, String> getTargetDatasetNames(Survey survey) {
		Map<Integer, String> targetDatasetNames = new HashMap<>();
		if (survey.getIsSelfAssessment()) {
			List<SATargetDataset> datasets = selfassessmentService.getTargetDatasets(survey.getUniqueId());
			for (SATargetDataset dataset : datasets) {
				targetDatasetNames.put(dataset.getId(), dataset.getName());
			}
		}
		return targetDatasetNames;
	}
	
	@Transactional(readOnly = true)
	public List<SATargetDataset> getTargetDatasets(String surveyUID) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM SATargetDataset WHERE surveyUID = :surveyUID";
		@SuppressWarnings("unchecked")
		Query<SATargetDataset> query = session.createQuery(hql).setParameter("surveyUID", surveyUID);
		List<SATargetDataset> list = query.list();
		return list;
	}

	@Transactional(readOnly = false)
	public int addTargetDataset(SATargetDataset t) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(t);
		session.flush();	
		return t.getId();
	}
	
	public SATargetDataset getTargetDataset(int datasetId) {
		Session session = sessionFactory.getCurrentSession();
		SATargetDataset ds = session.get(SATargetDataset.class, datasetId);	
		return ds;
	}

	@Transactional(readOnly = false)
	public void updateTargetDataset(int id, String surveyUID, String name) throws MessageException {
		Session session = sessionFactory.getCurrentSession();
		SATargetDataset ds = session.get(SATargetDataset.class, id);		
				
		if (!ds.getSurveyUID().equals(surveyUID)) {
			throw new MessageException("wrong survey");
		}
		
		ds.setName(name);
		
		session.saveOrUpdate(ds);		
	}
	
	@Transactional(readOnly = false)
	public void deleteTargetDataset(int id, String surveyUID) throws MessageException {
		Session session = sessionFactory.getCurrentSession();
		SATargetDataset ds = session.get(SATargetDataset.class, id);		
		
		if (!ds.getSurveyUID().equals(surveyUID)) {
			throw new MessageException("wrong survey");
		}
		
		session.delete(ds);
	}

	@Transactional(readOnly = true)
	public SAReportConfiguration getReportConfiguration(String surveyUID) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM SAReportConfiguration WHERE surveyUID = :surveyUID";
		@SuppressWarnings("unchecked")
		Query<SAReportConfiguration> query = session.createQuery(hql).setParameter("surveyUID", surveyUID);
		List<SAReportConfiguration> list = query.list();

		if (list.isEmpty()) {
			return setReportConfigurationDefaultText(new SAReportConfiguration(), surveyService.getSurveyByUniqueId(surveyUID, false, true));
		}
		return list.get(0);
	}

	public SAReportConfiguration setReportConfigurationDefaultText(SAReportConfiguration report, Survey survey) {
		Locale localeWithNewMainLanguage = new Locale(survey.getLanguage().getCode());

		String introduction = resources.getMessage("message.introductionSelfAssessment", null, report.INTRODUCTIONSELFASSESSMENT, localeWithNewMainLanguage);
		String customFeedback = resources.getMessage("message.customFeedbackSelfAssessment", null, report.CUSTOMFEEDBACKSELFASSESSMENT, localeWithNewMainLanguage);

		report.setIntroduction(introduction);
		report.setCustomFeedback(customFeedback);

		return report;
	}

	@Transactional(readOnly = false)
	public void updateReportConfiguration(SAReportConfiguration configuration, String surveyUID) throws MessageException {
		Session session = sessionFactory.getCurrentSession();
		
		SAReportConfiguration rc;
		if (configuration.getId() != 0) {
			rc = session.get(SAReportConfiguration.class, configuration.getId());
			
			if (!rc.getSurveyUID().equals(surveyUID)) {
				throw new MessageException("wrong survey");
			}			
		} else {
			rc = new SAReportConfiguration();
			rc.setSurveyUID(surveyUID);
		}
				
		rc.setAlgorithm(configuration.getAlgorithm());
		rc.setCharts(configuration.getCharts());
		rc.setCoefficient(configuration.getCoefficient());
		rc.setCompetencyType(configuration.getCompetencyType());
		rc.setCustomFeedback(configuration.getCustomFeedback());
		rc.setGaps(configuration.getGaps());
		rc.setIntroduction(configuration.getIntroduction());
		rc.setLegend(configuration.getLegend());
		rc.setLimitTableLines(configuration.getLimitTableLines());
		rc.setPerformanceTable(configuration.getPerformanceTable());
		rc.setResultsTable(configuration.getResultsTable());
		rc.setScale(configuration.getScale());
		rc.setSelectedChart(configuration.getSelectedChart());
		rc.setSeparateCompetencyTypes(configuration.getSeparateCompetencyTypes());
		rc.setTargetDatasetSelection(configuration.getTargetDatasetSelection());
		rc.setTargetScores(configuration.getTargetScores());
		
		session.saveOrUpdate(rc);		
	}

	private void deleteReportConfigurations(String surveyUid) {
		Session session = sessionFactory.getCurrentSession();
		Query<Survey> query2 = session.createQuery("DELETE FROM SAReportConfiguration r WHERE r.surveyUID = :uid").setParameter("uid", surveyUid);
		query2.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public SAScoreCard getScoreCard(int datasetID, boolean initialize) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM SAScoreCard WHERE datasetID = :datasetID";
		@SuppressWarnings("unchecked")
		Query<SAScoreCard> query = session.createQuery(hql).setParameter("datasetID", datasetID);
		List<SAScoreCard> list = query.list();
		
		if (list.isEmpty()) return null;
		
		if (initialize) {
			Hibernate.initialize(list.get(0).getScores());
		}
		
		return list.get(0);		
	}	

	@Transactional(readOnly = false)
	public SAScoreCard getScoreCard(int datasetID) {
		return getScoreCard(datasetID, false);
	}

	@Transactional(readOnly = false)
	public void updateScoreCard(SAScoreCard card, int datasetId) {
		Session session = sessionFactory.getCurrentSession();
		SAScoreCard existing = getScoreCard(datasetId);
		
		if (existing == null) {
			//this is a new card
			SAScoreCard newCard = new SAScoreCard();
			newCard.setDatasetID(datasetId);
			
			for (SAScore saScore : card.getScores()) {
				SAScore newScore = new SAScore();
				newScore.setCriterion(saScore.getCriterion());
				newScore.setScore(saScore.getScore());
				newScore.setNotRelevant(saScore.getNotRelevant());
				newScore.setScoreCard(newCard);
				newCard.getScores().add(newScore);
			}
			
			session.saveOrUpdate(newCard);
			
		} else {
			//update existing
			Map<Integer, SAScore> scoresByCriterion = new HashMap<>();
			for (SAScore saScore : card.getScores()) {
				scoresByCriterion.put(saScore.getCriterion(), saScore);
			}
			
			for (SAScore saScore : existing.getScores()) {
				if (scoresByCriterion.containsKey(saScore.getCriterion())) {
					saScore.setScore(scoresByCriterion.get(saScore.getCriterion()).getScore());
					saScore.setNotRelevant(scoresByCriterion.get(saScore.getCriterion()).getNotRelevant());
				}
			}
			
			session.saveOrUpdate(existing);
		}
		
	}

	public void initializeForm(Form form, Set<String> invisibleElements) {		
		AnswerSet answerSet = null;
		if (form.getAnswerSets() != null && form.getAnswerSets().size() > 0) {
			answerSet = form.getAnswerSets().get(0);
		}
		initializeElements(form.getSurvey().getElements(), form.getSurvey(), invisibleElements, answerSet);	
	}

	public void initializeElements(List<Element> elements, Survey survey) {
		initializeElements(elements, survey, new HashSet<String>(), null);
	}
	
	private void initializeElements(List<Element> elements, Survey survey, Set<String> invisibleElements, AnswerSet answerSet) {
		boolean displayAllSAQuestions = survey.displayAllSAQuestions(); 
		
		int selectedTargetDataset = 0;
		for (Element element : elements) {
			if (element instanceof SingleChoiceQuestion) {
				SingleChoiceQuestion sc = (SingleChoiceQuestion)element;
				if (sc.getIsTargetDatasetQuestion()) {
					List<SATargetDataset> datasets = selfassessmentService.getTargetDatasets(survey.getUniqueId());
					for (SATargetDataset dataset : datasets) {
						sc.getTargetDatasets().add(dataset);
					}
					if (answerSet != null) {
						List<Answer> answers = answerSet.getAnswers(sc.getUniqueId());
						if (!answers.isEmpty()) {
							selectedTargetDataset = Integer.parseInt(answers.get(0).getValue());
						}
					}
				} else if (!displayAllSAQuestions && sc.getIsSAQuestion() && sc.getEvaluationCriterion() != null) {
					List<SATargetDataset> datasets = selfassessmentService.getTargetDatasets(survey.getUniqueId());
					for (SATargetDataset dataset : datasets) {
						SAScoreCard card = selfassessmentService.getScoreCard(dataset.getId());
						if (card != null) {
							for (SAScore score : card.getScores()) {
								if (score.getCriterion() == sc.getEvaluationCriterion().getId()) {
									if (score.getNotRelevant()) {
										sc.getHiddenTargetDatasetIds().add(dataset.getId());
										if (selectedTargetDataset == dataset.getId()) {
											invisibleElements.add(sc.getUniqueId());
										}
									}
									break;
								}
							}
						}
					}
				}
			}			
		}		
	}
	
	private void copyCriterion(SACriterion criterion, SACriterion copy) {
		copy.setAcronym(criterion.getAcronym());
		copy.setName(criterion.getName());
		copy.setType(criterion.getType());
	}
	
	private void copyReportConfiguration(SAReportConfiguration configuration, SAReportConfiguration copy) {
		copy.setAlgorithm(configuration.getAlgorithm());
		copy.setCharts(configuration.getCharts());
		copy.setCoefficient(configuration.getCoefficient());
		copy.setCompetencyType(configuration.getCompetencyType());
		copy.setCustomFeedback(configuration.getCustomFeedback());
		copy.setGaps(configuration.getGaps());
		copy.setIntroduction(configuration.getIntroduction());
		copy.setLegend(configuration.getLegend());
		copy.setLimitTableLines(configuration.getLimitTableLines());
		copy.setPerformanceTable(configuration.getPerformanceTable());
		copy.setResultsTable(configuration.getResultsTable());
		copy.setScale(configuration.getScale());
		copy.setSelectedChart(configuration.getSelectedChart());
		copy.setSeparateCompetencyTypes(configuration.getSeparateCompetencyTypes());
		copy.setTargetDatasetSelection(configuration.getTargetDatasetSelection());
		copy.setTargetScores(configuration.getTargetScores());	
	}

	public void copyData(String uidOriginal, Survey surveyCopy) throws MessageException {	
		Map<Integer, Integer> oldToNewDatasetIDs = new HashMap<>();
		Map<Integer, SACriterion> oldIdToNewCriteria = new HashMap<>();
		
		// Copy Evaluation Criteria
		List<SACriterion> criteria = getCriteria(uidOriginal);
		for (SACriterion criterion : criteria) {
			SACriterion copy = new SACriterion();
			copyCriterion(criterion, copy);
			copy.setSurveyUID(surveyCopy.getUniqueId());
			copy = addCriterion(copy);
			oldIdToNewCriteria.put(criterion.getId(), copy);
		}
		
		// Copy Target Datasets	
		List<SATargetDataset> datasets = getTargetDatasets(uidOriginal);
		for (SATargetDataset dataset : datasets) {
			SATargetDataset copy = new SATargetDataset();
			copy.setName(dataset.getName());
			copy.setSurveyUID(surveyCopy.getUniqueId());
			int newId = addTargetDataset(copy);
			oldToNewDatasetIDs.put(dataset.getId(), newId);
			
			// Copy Score Cards
			SAScoreCard card = getScoreCard(dataset.getId());
			if (card != null) {
				SAScoreCard cardcopy = new SAScoreCard();
				cardcopy.setDatasetID(newId);
				
				for (SAScore score : card.getScores()) {
					SAScore scorecopy = new SAScore();
					scorecopy.setCriterion(oldIdToNewCriteria.get(score.getCriterion()).getId());
					scorecopy.setNotRelevant(score.getNotRelevant());
					scorecopy.setScoreCard(cardcopy);
					scorecopy.setScore(score.getScore());
					cardcopy.getScores().add(scorecopy);
				}
				
				updateScoreCard(cardcopy, newId);
			}
		}	
		
		// Copy Evaluation Report
		SAReportConfiguration configuration = getReportConfiguration(uidOriginal);
		SAReportConfiguration copy = new SAReportConfiguration();
		copy.setSurveyUID(surveyCopy.getUniqueId());
		copyReportConfiguration(configuration, copy);	
		updateReportConfiguration(copy, surveyCopy.getUniqueId());
		
		// Update SA questions with new evaluation criteria 
		for (Question question : surveyCopy.getQuestions()) {
			if (question instanceof SingleChoiceQuestion) {
				SingleChoiceQuestion scq = (SingleChoiceQuestion) question;
				if (scq.getIsSAQuestion() && scq.getEvaluationCriterion() != null) {
					scq.setEvaluationCriterion(oldIdToNewCriteria.get(scq.getEvaluationCriterion().getId()));
				}
			}
		}
		
		surveyService.update(surveyCopy, true, true, surveyCopy.getOwner().getId());
	}

	public SAResult getSAResult(int datasetid, String contributionuid) {
		SAResult result = new SAResult();
		
		AnswerSet answerSet = answerService.get(contributionuid);
		
		SATargetDataset dataset = datasetid > 0 ? getTargetDataset(datasetid) : null;
		result.setComparisonDataset(dataset);
		
		SAReportConfiguration config = getReportConfiguration(answerSet.getSurvey().getUniqueId());
		result.setConfiguration(config);		
				
		SAScoreCard card = datasetid > 0 ? selfassessmentService.getScoreCard(datasetid) : null;
		
		List<SACriterion> criteria = selfassessmentService.getCriteria(answerSet.getSurvey().getUniqueId());
		if (card != null) {
			for (SAScore score : card.getScores()) {
				for (SACriterion criterion : criteria) {
					if (!score.getNotRelevant() && criterion.getId() == score.getCriterion()) {
						result.getCriteria().add(criterion);
						break;
					}
				}
			}
		} else {
			result.setCriteria(criteria);
		}
				
		Map<Integer, List<String>> SAQuestionUIDsByCriterion = new HashMap<Integer, List<String>>();
		Map<String, Integer> ScoreByPossibleAnswerUID = new HashMap<String, Integer>();
		for (Question question : answerSet.getSurvey().getQuestions() ) {
			if (question instanceof SingleChoiceQuestion) {
				SingleChoiceQuestion scq = (SingleChoiceQuestion) question;
				
				if (scq.getIsSAQuestion() && scq.getEvaluationCriterion() != null) {
					if (!SAQuestionUIDsByCriterion.containsKey(scq.getEvaluationCriterion().getId())) {
						SAQuestionUIDsByCriterion.put(scq.getEvaluationCriterion().getId(), new ArrayList<String>());
					}
					SAQuestionUIDsByCriterion.get(scq.getEvaluationCriterion().getId()).add(scq.getUniqueId());
					
					for (PossibleAnswer pa : scq.getPossibleAnswers()) {
						ScoreByPossibleAnswerUID.put(pa.getUniqueId(), pa.getEcfScore());
					}
				}
			}
		}
		
		Double allValues = 0.0;
		int allCounter = 0;
		List<Double> values = new ArrayList<>();
		List<Integer> counters = new ArrayList<>();
		
		List<String> types = new ArrayList<>();		
		
		for (SACriterion criterion : result.getCriteria()) {
			if (!types.contains(criterion.getType())) {
				types.add(criterion.getType());
				result.getValuesForTypes().add(new ArrayList<Double>());
				result.getComparisonValuesForTypes().add(new ArrayList<Double>());
			}			
			
			Double value = 0.0;
			if (card != null) {
				for (SAScore score : card.getScores()) {
					if (score.getCriterion() == criterion.getId()) {
						value = (double) score.getScore();
						break;
					}
				}
			}
			result.getComparisonValues().add(value);
			
			int typeIndex = types.indexOf(criterion.getType());
			result.getComparisonValuesForTypes().get(typeIndex).add(value);			
			
			value = 0.0;
			int counter = 0;
			
			if (SAQuestionUIDsByCriterion.containsKey(criterion.getId())) {
				for (String quid : SAQuestionUIDsByCriterion.get(criterion.getId())) {
					List<Answer> answers = answerSet.getAnswers(quid);
					for (Answer answer : answers) {
						if (ScoreByPossibleAnswerUID.containsKey(answer.getPossibleAnswerUniqueId())) {
							int singleValue = ScoreByPossibleAnswerUID.get(answer.getPossibleAnswerUniqueId());
							value += singleValue;
							allValues += singleValue;
							counter++;
							allCounter++;
						}
					}					
				}
			}
			
			values.add(value);
			counters.add(counter);
		}
		
		// Mrat is the mean of all answers of a respondent
		double mrat = config.getAlgorithm().equalsIgnoreCase("MRAT") ? (allCounter != 0 ? allValues / (double)allCounter : 0.0) : 0;
		double coefficient = config.getAlgorithm().equalsIgnoreCase("MRAT") ? config.getCoefficient() : 0;
		
		for (int i = 0; i < result.getCriteria().size(); i++) {
			double value = values.get(i);
			int counter = counters.get(i);
		
			double avg = counter != 0 ? value / (double)counter : 0.0;
			
			//Output data = AVERAGE (variables) – Mrat + 5
			double computedValue = Math.round(10.0 * (avg - mrat + coefficient)) / 10.0;
			result.getValues().add(computedValue);
			int typeIndex = types.indexOf(result.getCriteria().get(i).getType());
			result.getValuesForTypes().get(typeIndex).add(computedValue);	
		}
		
		return result;
	}

	public void importData(ImportResult result, Survey survey, Map<String, Integer> evaluationCriteriaMappings, Map<Integer, SACriterion> oldIdToNewCriteria) throws MessageException {				
		if (result.getCriteria() != null) {
			for (SACriterion criterion : result.getCriteria()) {
				SACriterion copy = new SACriterion();
				copyCriterion(criterion, copy);
				copy.setSurveyUID(survey.getUniqueId());
				copy = addCriterion(copy);
				oldIdToNewCriteria.put(criterion.getId(), copy);
			}
		}
		
		if (result.getTargetDatasets() != null) {
			for (SATargetDataset dataset : result.getTargetDatasets()) {
				SATargetDataset copy = new SATargetDataset();
				copy.setName(dataset.getName());
				copy.setSurveyUID(survey.getUniqueId());
				int newId = addTargetDataset(copy);
				result.getOldToNewDatasetIDs().put(dataset.getId().toString(), Integer.toString(newId));
				
				// Copy Score Cards
				Optional<SAScoreCard> card = result.getScoreCards().stream().filter(c -> c.getDatasetID() == dataset.getId()).findFirst();
				if (card.isPresent()) {
					SAScoreCard cardcopy = new SAScoreCard();
					cardcopy.setDatasetID(newId);
					
					for (SAScore score : card.get().getScores()) {
						SAScore scorecopy = new SAScore();
						scorecopy.setCriterion(oldIdToNewCriteria.get(score.getCriterion()).getId());
						scorecopy.setNotRelevant(score.getNotRelevant());
						scorecopy.setScoreCard(cardcopy);
						scorecopy.setScore(score.getScore());
						cardcopy.getScores().add(scorecopy);
					}
					
					updateScoreCard(cardcopy, newId);
				}
			}	
		}
		
		if (result.getReportConfiguration() != null) {
			SAReportConfiguration copy = new SAReportConfiguration();
			copy.setSurveyUID(survey.getUniqueId());
			copyReportConfiguration(result.getReportConfiguration(), copy);	
			updateReportConfiguration(copy, survey.getUniqueId());
		}
		
		adaptEvaluationCriteria(survey, evaluationCriteriaMappings, oldIdToNewCriteria);
	}
	
	public void adaptEvaluationCriteria(Survey survey, Map<String, Integer> evaluationCriteriaMappings, Map<Integer, SACriterion> oldIdToNewCriteria) {
		for (Question question : survey.getQuestions()) {
			if (question instanceof SingleChoiceQuestion) {
				SingleChoiceQuestion scq = (SingleChoiceQuestion) question;
				if (scq.getIsSAQuestion() && evaluationCriteriaMappings.containsKey(scq.getUniqueId())) {
					scq.setEvaluationCriterion(oldIdToNewCriteria.get(evaluationCriteriaMappings.get(scq.getUniqueId())));
					surveyService.update(scq);
				}
			}
		}
	}

	public void deleteData(String surveyUid) {
		deleteCriteria(surveyUid);
		deleteReportConfigurations(surveyUid);

		List<SATargetDataset> targetDatasets = getTargetDatasets(surveyUid);
		targetDatasets.forEach((td) -> {
			SAScoreCard scoreCard = getScoreCard(td.getId());
			Session session = sessionFactory.getCurrentSession();
			if (scoreCard != null) {
				session.delete(scoreCard);
			}
			session.delete(td);
		});
	}
}
