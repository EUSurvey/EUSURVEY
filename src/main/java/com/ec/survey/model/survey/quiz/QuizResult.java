package com.ec.survey.model.survey.quiz;

import com.ec.survey.model.survey.ScoringItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a quiz result
 */
public class QuizResult implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer score;
	private Integer maximumScore;
	private Map<String, Integer> questionScores = new HashMap<>();
	private Map<String, Integer> questionMaximumScores = new HashMap<>();
	private Map<String, ScoringItem> questionScoringItems = new HashMap<>();
	private Set<String> partiallyAnswersMultipleChoiceQuestions = new HashSet<>();
	private Map<String, String> sectionScores = new HashMap<>();
	private Map<String, Integer> positionForAnswerUID = new HashMap<>();
	private Map<Integer, String> answerUIDForPosition = new HashMap<>();
	
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	
	public Integer getMaximumScore() {
		return maximumScore;
	}
	public void setMaximumScore(Integer maximumScore) {
		this.maximumScore = maximumScore;
	}
	
	public Map<String, Integer> getQuestionScores() {
		return questionScores;
	}
	public void setQuestionScores(Map<String, Integer> questionScores) {
		this.questionScores = questionScores;
	}
	
	public Map<String, Integer> getQuestionMaximumScores() {
		return questionMaximumScores;
	}
	public void setQuestionMaximumScores(Map<String, Integer> questionMaximumScores) {
		this.questionMaximumScores = questionMaximumScores;
	}
	
	public int getQuestionScore(String uid)
	{
		if (questionScores.containsKey(uid)) return questionScores.get(uid);
		return 0;
	}
	
	public int getQuestionMaximumScore(String uid)
	{
		if (questionMaximumScores.containsKey(uid)) return questionMaximumScores.get(uid);
		return 0;
	}
	
	public Map<String, ScoringItem> getQuestionScoringItems() {
		return questionScoringItems;
	}
	public void setQuestionScoringItems(Map<String, ScoringItem> questionScoringItems) {
		this.questionScoringItems = questionScoringItems;
	}
	
	public ScoringItem getQuestionScoringItem(String uid)
	{
		if (questionScoringItems.containsKey(uid)) return questionScoringItems.get(uid);
		return null;
	}
	
	public Set<String> getPartiallyAnswersMultipleChoiceQuestions() {
		return partiallyAnswersMultipleChoiceQuestions;
	}
	
	public void setPartiallyAnswersMultipleChoiceQuestions(Set<String> partiallyAnswersMultipleChoiceQuestions) {
		this.partiallyAnswersMultipleChoiceQuestions = partiallyAnswersMultipleChoiceQuestions;
	}

	public Map<String, String> getSectionScores() {
		return sectionScores;
	}
	public void setSectionScores(Map<String, String> sectionScores) {
		this.sectionScores = sectionScores;
	}
	
	public String getSectionScore(String uid)
	{
		if (sectionScores.containsKey(uid)) return sectionScores.get(uid);
		return null;
	}
	
	public int getSectionScoreValue(String uid)
	{
		if (sectionScores.containsKey(uid))
		{
			return Integer.parseInt(sectionScores.get(uid).substring(0, sectionScores.get(uid).indexOf('/')));
		}
		return 0;
	}

	public int getMaxSectionScore(String uid)
	{
		if (sectionScores.containsKey(uid)){
			return Integer.parseInt(sectionScores.get(uid).substring(sectionScores.get(uid).indexOf('/')+1));
		}

		return 0;
	}
	
	public Map<String, Integer> getPositionForAnswerUID() {
		return positionForAnswerUID;
	}
	public void setPositionForAnswerUID(Map<String, Integer> positionForAnswerUID) {
		this.positionForAnswerUID = positionForAnswerUID;
	}
	
	public Map<Integer, String> getAnswerUIDForPosition() {
		return answerUIDForPosition;
	}
	public void setAnswerUIDForPosition(Map<Integer, String> answerUIDForPosition) {
		this.answerUIDForPosition = answerUIDForPosition;
	}
}
