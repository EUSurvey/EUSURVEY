package com.ec.survey.model.survey.ecf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the target result, maximum result and average result for all
 * profiles or a specific one
 */
public class ECFProfileCompetencyResult implements Comparable {

	@JsonProperty("competencyName")
	private String competencyName;

	@JsonProperty("competencyTargetScore")
	private Integer competencyTargetScore;

	@JsonProperty("competencyAverageScore")
	private Float competencyAverageScore;

	@JsonProperty("competencyMaxScore")
	private Integer competencyMaxScore;

	@JsonProperty("competencyScoreGap")
	private Integer competencyScoreGap;

	private Integer order;

	public String getCompetencyName() {
		return competencyName;
	}

	public void setCompetencyName(String competencyName) {
		this.competencyName = competencyName;
	}

	public Integer getCompetencyTargetScore() {
		return competencyTargetScore;
	}

	public void setCompetencyTargetScore(Integer competencyTargetScore) {
		this.competencyTargetScore = competencyTargetScore;
	}

	public Float getCompetencyAverageScore() {
		return competencyAverageScore;
	}
	public void setCompetencyAverageScore(Float competencyAverageScore) {
		this.competencyAverageScore = competencyAverageScore;
	}

	public Integer getCompetencyMaxScore() {
		return competencyMaxScore;
	}

	public void setCompetencyMaxScore(Integer competencyMaxScore) {
		this.competencyMaxScore = competencyMaxScore;
	}

	public Integer getCompetencyScoreGap() {
		return this.competencyScoreGap;
	}

	public void setCompetencyScoreGap(Integer competencyScoreGap) {
		this.competencyScoreGap = competencyScoreGap;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "ECFProfileCompetencyResult [competencyName=" + competencyName + ", competencyTargetScore="
				+ competencyTargetScore + ", competencyAverageScore=" + competencyAverageScore + ", competencyMaxScore="
				+ competencyMaxScore + ", competencyScoreGap=" + competencyScoreGap + ", order=" + order + "]";
	}

	@Override
	public int compareTo(Object otherObject) {
		if (otherObject instanceof ECFProfileCompetencyResult) {
			ECFProfileCompetencyResult otherResult = (ECFProfileCompetencyResult) otherObject;
			return this.getOrder().compareTo(otherResult.getOrder());
		} else {
			return 0;
		}
	}
}