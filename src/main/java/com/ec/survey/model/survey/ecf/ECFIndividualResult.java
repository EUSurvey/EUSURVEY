package com.ec.survey.model.survey.ecf;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents one result for one answerer. 
 */
public class ECFIndividualResult {

    @JsonProperty("name")
    private String profileName;

    @JsonProperty("profileUUID")
    private String profileUUID;
    
    @JsonProperty("competencies")
    private List<ECFIndividualCompetencyResult> competencyResultList = new ArrayList<>();

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public List<ECFIndividualCompetencyResult> getCompetencyResultList() {
        return competencyResultList;
    }

    public void setCompetencyResultList(List<ECFIndividualCompetencyResult> competencyResultList) {
        this.competencyResultList = competencyResultList;
    }

    public void addCompetencyResult(ECFIndividualCompetencyResult competencyResult) {
        this.competencyResultList.add(competencyResult);
    }

	public String getProfileUUID() {
		return profileUUID;
	}

	public void setProfileUUID(String profileUUID) {
		this.profileUUID = profileUUID;
	}

	@Override
	public String toString() {
		return "ECFIndividualResult [profileName=" + profileName + ", profileUUID=" + profileUUID
				+ ", competencyResultList=" + competencyResultList + "]";
	}
    
}