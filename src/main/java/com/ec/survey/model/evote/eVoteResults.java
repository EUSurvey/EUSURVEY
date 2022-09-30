package com.ec.survey.model.evote;

import java.util.LinkedHashMap;
import java.util.Map;

public class eVoteResults {	
	private Map<String, eVoteListResult> lists = new LinkedHashMap<>();
	private int blankVotes;
	private int spoiltVotes;
	private int preferentialVotes;

	public Map<String, eVoteListResult> getLists() {
		return lists;
	}

	public void setLists(Map<String, eVoteListResult> lists) {
		this.lists = lists;
	}

	public int getBlankVotes() {
		return blankVotes;
	}

	public void setBlankVotes(int blankVotes) {
		this.blankVotes = blankVotes;
	}

	public int getSpoiltVotes() {
		return spoiltVotes;
	}

	public void setSpoiltVotes(int spoiltVotes) {
		this.spoiltVotes = spoiltVotes;
	}

	public int getPreferentialVotes() {
		return preferentialVotes;
	}

	public void setPreferentialVotes(int preferentialVotes) {
		this.preferentialVotes = preferentialVotes;
	}	
}
