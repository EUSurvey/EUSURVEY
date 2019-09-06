package com.ec.survey.model;

public class SearchAndReplaceResult {

	private int translationId;
	private String[] searchResults;
	private String[] replaceResults;
	private boolean emptyLabels = false;
	
	public int getTranslationId() {
		return translationId;
	}
	public void setTranslationId(int translationId) {
		this.translationId = translationId;
	}
	public String[] getSearchResults() {
		return searchResults;
	}
	public void setSearchResults(String[] searchResults) {
		this.searchResults = searchResults;
	}
	public String[] getReplaceResults() {
		return replaceResults;
	}
	public void setReplaceResults(String[] replaceResults) {
		this.replaceResults = replaceResults;
	}
	public boolean getEmptyLabels() {
		return emptyLabels;
	}
	public void setEmptyLabels(boolean emptyLabels) {
		this.emptyLabels = emptyLabels;
	}
	
}
