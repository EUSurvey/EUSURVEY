package com.ec.survey.model.survey.ecf;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the for each of the profiles, the number of contributions
 * submitted
 */
public class ECFSummaryResult {

	@JsonProperty("profileResults")
	private List<ECFProfileSummaryResult> profileResults = new ArrayList<>();

	public List<ECFProfileSummaryResult> getProfileResults() {
		return profileResults;
	}

	public void setProfileResults(List<ECFProfileSummaryResult> profileResults) {
		this.profileResults = profileResults;
	}

	public void addProfileResult(ECFProfileSummaryResult profileResult) {
		this.profileResults.add(profileResult);
	}

	@Override
	public String toString() {
		return "ECFSummaryResult [profileResults=" + profileResults + "]";
	}

}
