package com.ec.survey.model.delphi;

import java.util.HashMap;
import java.util.Map;

public class NumberQuestionStatistics {
	private int numberVotes = 0;
	private boolean questionFound = false;
	private Map<String, Integer> valuesMagnitude = new HashMap<>();
	
	public NumberQuestionStatistics() {
		this.numberVotes = 0;
		this.questionFound = false;
		this.valuesMagnitude = new HashMap<>();
	}
	
	public NumberQuestionStatistics(int numberVotes, boolean questionFound) {
		this.numberVotes = numberVotes;
		this.questionFound = questionFound;
		this.valuesMagnitude = new HashMap<>();
	}
	
	public int getNumberVotes() {
		return numberVotes;
	}
	public void setNumberVotes(int numberVotes) {
		this.numberVotes = numberVotes;
	}
	public void incrementNumberVotes() {
		this.numberVotes += 1;
	}
	
	public boolean isQuestionFound() {
		return questionFound;
	}
	public void setQuestionFound(boolean questionFound) {
		this.questionFound = questionFound;
	}

	public Map<String, Integer> getValuesMagnitude() {
		return valuesMagnitude;
	}
	public void setValuesMagnitude(Map<String, Integer> valuesMagnitude) {
		this.valuesMagnitude = valuesMagnitude;
	}
}