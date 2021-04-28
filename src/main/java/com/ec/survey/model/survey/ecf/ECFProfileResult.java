package com.ec.survey.model.survey.ecf;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the target result, maximum result and average result for all profiles or a specific one
 */
public class ECFProfileResult {

    @JsonProperty("name")
    private String profileName;

    @JsonProperty("competencyResults")
    private List<ECFProfileCompetencyResult> competencyResults = new ArrayList<>();
    
    @JsonProperty("numberOfAnswers")
    private Integer numberOfAnswers;
    
	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public List<ECFProfileCompetencyResult> getCompetencyResults() {
		return competencyResults;
	}

	public void setCompetencyResults(List<ECFProfileCompetencyResult> competencyResults) {
		this.competencyResults = competencyResults;
	}
	
	public void addIndividualResults(ECFProfileCompetencyResult competencyResult) {
		this.competencyResults.add(competencyResult);
	}

	public Integer getNumberOfAnswers() {
		return numberOfAnswers;
	}

	public void setNumberOfAnswers(Integer numberOfAnswers) {
		this.numberOfAnswers = numberOfAnswers;
	}

	@Override
	public String toString() {
		return "ECFProfileResult [profileName=" + profileName + ", competencyResults=" + competencyResults
				+ ", numberOfAnswers=" + numberOfAnswers + "]";
	}
	
	
}