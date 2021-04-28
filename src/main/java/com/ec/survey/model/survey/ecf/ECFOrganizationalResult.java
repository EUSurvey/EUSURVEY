package com.ec.survey.model.survey.ecf;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the Average Target, Average Score, Max Target and Max Score for all answerers
 */
public class ECFOrganizationalResult {

    @JsonProperty("competencyResults")
	private List<ECFOrganizationalCompetencyResult> competencyResults = new ArrayList<>();

	@JsonProperty("competenciesTypes")
	private List<TypeUUIDAndName> competenciesTypes = new ArrayList<>();

	public List<ECFOrganizationalCompetencyResult> getCompetencyResults() {
		return competencyResults;
	}

	public void setCompetencyResults(List<ECFOrganizationalCompetencyResult> competencyResults) {
		this.competencyResults = competencyResults;
	}
	
	public void addCompetencyResult(ECFOrganizationalCompetencyResult competencyResult) {
		this.competencyResults.add(competencyResult);
	}

	public void setCompetenciesTypes(List<TypeUUIDAndName> competenciesTypes) {
        this.competenciesTypes = competenciesTypes;
    }

    public void addCompetenciesType(TypeUUIDAndName competenciesTypesName) {
        this.competenciesTypes.add(competenciesTypesName);
	}
	
	public List<TypeUUIDAndName> getCompetenciesTypes() {
        return competenciesTypes;
    }

	@Override
	public String toString() {
		return "ECFOrganizationalResult [competenciesTypes=" + competenciesTypes + ", competencyResults="
				+ competencyResults + "]";
	}
}