package com.ec.survey.model.survey.ecf;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ECFGlobalCompetencyResult implements Comparable<ECFGlobalCompetencyResult> {

	@JsonProperty("name")
	private String competencyName;

	@JsonProperty("targetScore")
	private Integer competencyTargetScore;

	@JsonProperty("scores")
	private List<Integer> competencyScores = new ArrayList<>();

	@JsonProperty("scoreGaps")
	private List<Integer> competencyScoreGaps = new ArrayList<>();

	@JsonProperty("participantNames")
	private List<String> participantsNames = new ArrayList<>();

	@JsonProperty("participantContributionUIDs")
	private List<String> participantContributionUIDs = new ArrayList<>();

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

	public List<Integer> getCompetencyScores() {
		return competencyScores;
	}

	public void setCompetencyScores(List<Integer> competencyScores) {
		this.competencyScores = competencyScores;
	}
	public void addCompetencyScore(Integer competencyScore) {
		this.competencyScores.add(competencyScore);
	}

	public List<Integer> getCompetencyScoreGaps() {
		return competencyScoreGaps;
	}

	public void setCompetencyScoreGaps(List<Integer> competencyScoreGaps) {
		this.competencyScoreGaps = competencyScoreGaps;
	}
	public void addCompetencyScoreGap(Integer scoreGap) {
		this.competencyScoreGaps.add(scoreGap);
	}

	public List<String> getParticipantsNames() {
		return participantsNames;
	}

	public void setParticipantsNames(List<String> participantsNames) {
		this.participantsNames = participantsNames;
	}
	
	public void addParticipantsName(String participantsName) {
		this.participantsNames.add(participantsName);
	}
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public List<String> getParticipantContributionUIDs() {
		return participantContributionUIDs;
	}

	public void setParticipantContributionUIDs(List<String> participantContributionUIDs) {
		this.participantContributionUIDs = participantContributionUIDs;
	}

	public void addParticipantContributionUID(String participantContributionUID) {
		this.participantContributionUIDs.add(participantContributionUID);
	}

	@Override
	public String toString() {
		return "ECFGlobalCompetencyResult [competencyName=" + competencyName + ", competencyTargetScore="
				+ competencyTargetScore + ", competencyScores=" + competencyScores + ", competencyScoreGaps="
				+ competencyScoreGaps + ", participantsNames=" + participantsNames + ", order=" + order + "]";
	}

	@Override
	public int compareTo(ECFGlobalCompetencyResult otherObject) {
		return this.getOrder().compareTo(otherObject.getOrder());
	}

}