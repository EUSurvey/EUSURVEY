package com.ec.survey.model.evote;

import java.util.LinkedHashMap;
import java.util.Map;

public class eVoteListResult {
	private String uid;
	private int listVotes;
	private int luxListVotes;
	private Map<String, Integer> candidateVotes = new LinkedHashMap<>();
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getListVotes() {
		return listVotes;
	}
	public void setListVotes(int listVotes) {
		this.listVotes = listVotes;
	}
	public int getLuxListVotes() {
		return luxListVotes;
	}
	public void setLuxListVotes(int luxListVotes) {
		this.luxListVotes = luxListVotes;
	}
	public Map<String, Integer> getCandidateVotes() {
		return candidateVotes;
	}
	public void setCandidateVotes(Map<String, Integer> candidateVotes) {
		this.candidateVotes = candidateVotes;
	}
}