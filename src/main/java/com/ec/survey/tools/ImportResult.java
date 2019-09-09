package com.ec.survey.tools;

import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Translations;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.base.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportResult {
	
	private Survey survey;
	private Survey activeSurvey;
	private boolean isFromIPM;
	private boolean invalidCodeFound;
	private List<Translations> translations;
	private List<Translations> activeTranslations;
	private List<List<AnswerSet>> activeAnswerSets;
	private List<AnswerSet> answerSets;
	private Map<Integer, List<File>> activeFiles;
	private Map<Integer, List<File>> files;
	private Map<String, Integer> originalIdsToNewIds = new HashMap<>();
	private Map<Integer, List<String>> originalDependencies = new HashMap<>();
	private Map<String, List<String>> originalMatrixDependencies = new HashMap<>();
	private Map<Integer, List<Integer>> additionalElements = new HashMap<>();
	private Map<Integer, Survey> oldSurveys = new HashMap<>();
	private Map<Integer, List<Translations>> oldTranslations = new HashMap<>();
	
	public Survey getSurvey() {
		return survey;
	}
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
	public List<Translations> getTranslations() {
		return translations;
	}
	public void setTranslations(List<Translations> translations) {
		this.translations = translations;
	}
	public List<AnswerSet> getAnswerSets() {
		return answerSets;
	}
	public void setAnswerSets(List<AnswerSet> answerSets) {
		this.answerSets = answerSets;
	}
	public Survey getActiveSurvey() {
		return activeSurvey;
	}
	public void setActiveSurvey(Survey activeSurvey) {
		this.activeSurvey = activeSurvey;
	}
	public List<Translations> getActiveTranslations() {
		return activeTranslations;
	}
	public void setActiveTranslations(List<Translations> activeTranslations) {
		this.activeTranslations = activeTranslations;
	}
	public List<List<AnswerSet>> getActiveAnswerSets() {
		return activeAnswerSets;
	}
	public void setActiveAnswerSets(List<List<AnswerSet>> activeAnswerSets) {
		this.activeAnswerSets = activeAnswerSets;
	}
	public Map<Integer, List<File>> getActiveFiles() {
		return activeFiles;
	}
	public void setActiveFiles(Map<Integer, List<File>> activeFiles) {
		this.activeFiles = activeFiles;
	}
	public Map<Integer, List<File>> getFiles() {
		return files;
	}
	public void setFiles(Map<Integer, List<File>> files) {
		this.files = files;
	}
	public boolean isFromIPM() {
		return isFromIPM;
	}
	public void setFromIPM(boolean isFromIPM) {
		this.isFromIPM = isFromIPM;
	}
	public Map<String, Integer> getOriginalIdsToNewIds() {
		return originalIdsToNewIds;
	}
	public void setOriginalIdsToNewIds(Map<String, Integer> originalIdsToNewIds) {
		this.originalIdsToNewIds = originalIdsToNewIds;
	}
	public Map<Integer, List<String>> getOriginalDependencies() {
		return originalDependencies;
	}
	public void setOriginalDependencies(Map<Integer, List<String>> originalDependencies) {
		this.originalDependencies = originalDependencies;
	}
	public Map<Integer, List<Integer>> getAdditionalElements() {
		return additionalElements;
	}
	public void setAdditionalElements(Map<Integer, List<Integer>> additionalElements) {
		this.additionalElements = additionalElements;
	}
	public boolean isInvalidCodeFound() {
		return invalidCodeFound;
	}
	public void setInvalidCodeFound(boolean invalidCodeFound) {
		this.invalidCodeFound = invalidCodeFound;
	}
	public Map<Integer, Survey> getOldSurveys() {
		return oldSurveys;
	}
	public void setOldSurveys(Map<Integer, Survey>oldSurveys) {
		this.oldSurveys = oldSurveys;
	}	
	public Map<String, List<String>> getOriginalMatrixDependencies() {
		return originalMatrixDependencies;
	}
	public void setOriginalMatrixDependencies(Map<String, List<String>> originalMatrixDependencies) {
		this.originalMatrixDependencies = originalMatrixDependencies;
	}
	public Map<Integer, List<Translations>> getOldTranslations() {
		return oldTranslations;
	}
	public void setOldTranslations(Map<Integer, List<Translations>> oldTranslations) {
		this.oldTranslations = oldTranslations;
	}	

}
