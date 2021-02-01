package com.ec.survey.model.delphi;

public class NumberQuestionStatistics {
	private int numberVotes = 0;
	private boolean questionFound = false;
	
	public NumberQuestionStatistics() {
		this.numberVotes = 0;
		this.questionFound = false;
	}
	
	public NumberQuestionStatistics(int numberVotes, boolean questionFound) {
		this.numberVotes = numberVotes;
		this.questionFound = questionFound;
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
}