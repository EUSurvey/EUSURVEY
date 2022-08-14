package com.ec.survey.model.delphi;

import java.util.HashMap;
import java.util.Map;

public class NumberQuestionStatistics {
	private int numberVotes = 0;
	private Map<String, Integer> valuesMagnitude = new HashMap<>();
	
	public NumberQuestionStatistics() {
		this.numberVotes = 0;
		this.valuesMagnitude = new HashMap<>();
	}
	
	public NumberQuestionStatistics(int numberVotes) {
		this.numberVotes = numberVotes;
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

	public Map<String, Integer> getValuesMagnitude() {
		return valuesMagnitude;
	}
	public void setValuesMagnitude(Map<String, Integer> valuesMagnitude) {
		this.valuesMagnitude = valuesMagnitude;
	}
}