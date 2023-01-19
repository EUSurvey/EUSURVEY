package com.ec.survey.model.evote;

import java.util.ArrayList;
import java.util.List;

public class SeatCounting {
	
	private int voterCount;
	private int quorum;
	private int participationRate;
	private int votes;
	private int listVotes;
	private int listVotesFinal;
	private int luxListVotes;
	private int luxListVotesFinal;
	private int preferentialVotes; // number of contributions that are not a list vote, not blank and not spoilt
	private int totalPreferentialVotes; // sum of all individual candidate votes
	private int preferentialVotesFinal;
	private int blankVotes;
	private int spoiltVotes;
	private int total;
	private int maxSeats;
	private int minListPercent;
	private int maxCandidatesInLists;
	private int listVotesSeats;
	private int preferentialVotesSeats;
	private int highestVotes;
	private String template;
	private List<SeatDistribution> listSeatDistribution = new ArrayList<>();
	private List<ElectedCandidate> candidatesFromListVotes = new ArrayList<>();
	private List<ElectedCandidate> candidatesFromPreferentialVotes = new ArrayList<>();
	private List<List<ElectedCandidate>> candidateVotes = new ArrayList<>();
	private DHondtEntry[][] dHondtEntries;
	private List<String> reallocationMessages = new ArrayList<>();
	private List<String> reallocationMessagesForLists = new ArrayList<>();
	private boolean ambiguous;

	public int getQuorum() {
		return quorum;
	}
	public void setQuorum(int quorum) {
		this.quorum = quorum;
	}
	
	public int getParticipationRate() {
		return participationRate;
	}
	public void setParticipationRate(int participationRate) {
		this.participationRate = participationRate;
	}
	
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}
	
	public int getListVotes() {
		return listVotes;
	}
	public void setListVotes(int listVotes) {
		this.listVotes = listVotes;
	}
	
	public int getListVotesWeighted() {
		return listVotes * maxSeats;
	}
	
	public int getPreferentialVotes() {
		return preferentialVotes;
	}
	public void setPreferentialVotes(int preferentialVotes) {
		this.preferentialVotes = preferentialVotes;
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
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	public int getTotalVotesWeighted() {
		return getListVotesWeighted() + getTotalPreferentialVotes();
	}
	
	public int getVoterCount() {
		return voterCount;
	}
	public void setVoterCount(int voterCount) {
		this.voterCount = voterCount;
	}
	
	public int getMaxSeats() {
		return maxSeats;
	}
	public void setMaxSeats(int maxSeats) {
		this.maxSeats = maxSeats;
	}
	
	public int getListVotesSeats() {
		return listVotesSeats;
	}
	public void setListVotesSeats(int listVotesSeats) {
		this.listVotesSeats = listVotesSeats;
	}
	
	public int getPreferentialVotesSeats() {
		return preferentialVotesSeats;
	}
	public void setPreferentialVotesSeats(int preferentialVotesSeats) {
		this.preferentialVotesSeats = preferentialVotesSeats;
	}
	
	public List<SeatDistribution> getListSeatDistribution() {
		return listSeatDistribution;
	}
	public void setListSeatDistribution(List<SeatDistribution> listSeatDistribution) {
		this.listSeatDistribution = listSeatDistribution;
	}

	public List<ElectedCandidate> getCandidatesFromListVotes() {
		return candidatesFromListVotes;
	}
	public void setCandidatesFromListVotes(List<ElectedCandidate> candidatesFromListVotes) {
		this.candidatesFromListVotes = candidatesFromListVotes;
	}
	
	public List<ElectedCandidate> getCandidatesFromPreferentialVotes() {
		return candidatesFromPreferentialVotes;
	}
	public void setCandidatesFromPreferentialVotes(List<ElectedCandidate> candidatesFromPreferentialVotes) {
		this.candidatesFromPreferentialVotes = candidatesFromPreferentialVotes;
	}
	
	public int getMaxCandidatesInLists() {
		return maxCandidatesInLists;
	}
	public void setMaxCandidatesInLists(int maxCandidatesInLists) {
		this.maxCandidatesInLists = maxCandidatesInLists;
	}
	
	public List<List<ElectedCandidate>> getCandidateVotes() {
		return candidateVotes;
	}
	public void setCandidateVotes(List<List<ElectedCandidate>> candidateVotes) {
		this.candidateVotes = candidateVotes;
	}
	public int getMinListPercent() {
		return minListPercent;
	}
	public void setMinListPercent(int minListPercent) {
		this.minListPercent = minListPercent;
	}
	
	// list votes without those for lists with not enough votes
	public int getListVotesFinal() {
		return listVotesFinal;
	}
	public void setListVotesFinal(int listVotesFinal) {
		this.listVotesFinal = listVotesFinal;
	}
	public int getPreferentialVotesFinal() {
		return preferentialVotesFinal;
	}
	public void setPreferentialVotesFinal(int preferentialVotesFinal) {
		this.preferentialVotesFinal = preferentialVotesFinal;
	}

	public int getHighestVote() {
		return highestVotes;
	}
	public void setHighestVote(int highestVotes) {
		this.highestVotes = highestVotes;
	}

	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}

	public int getLuxListVotes() {
		return luxListVotes;
	}
	public void setLuxListVotes(int luxListVotes) {
		this.luxListVotes = luxListVotes;
	}

	public int getLuxListVotesFinal() {
		return luxListVotesFinal;
	}
	public void setLuxListVotesFinal(int luxListVotesFinal) {
		this.luxListVotesFinal = luxListVotesFinal;
	}
	
	public int getSumPreferentialVotes() {
		int result = 0;
		for (ElectedCandidate candidate : getCandidatesFromPreferentialVotes()) {
			result += candidate.getVotes();
		}
		return result;
	}
	
	public int getSumListVotes() {
		int result = 0;
		for (ElectedCandidate candidate : getCandidatesFromListVotes()) {
			result += candidate.getVotes();
		}
		return result;
	}
		
	public DHondtEntry[][] getDHondtEntries() {
		return dHondtEntries;
	}
	public void setDHondtEntries(DHondtEntry[][] dHondtEntries) {
		this.dHondtEntries = dHondtEntries;		
	}
	public int getTotalPreferentialVotes() {
		return totalPreferentialVotes;
	}
	public void setTotalPreferentialVotes(int totalPreferentialVotes) {
		this.totalPreferentialVotes = totalPreferentialVotes;
	}
	
	public List<String> getReallocationMessages() {
		return reallocationMessages;
	}
	public void setReallocationMessages(List<String> reallocationMessages) {
		this.reallocationMessages = reallocationMessages;
	}
	
	public List<String> getReallocationMessagesForLists() {
		return reallocationMessagesForLists;
	}
	public void setReallocationMessagesForLists(List<String> reallocationMessagesForLists) {
		this.reallocationMessagesForLists = reallocationMessagesForLists;
	}
	
	//can differ from listVotesSeats if number of candidates too small for all seats
	public int getListVotesSeatsReal() {
		int listVotesSeatsReal = 0;
		for (SeatDistribution list : this.getListSeatDistribution()) {
			listVotesSeatsReal += list.getListSeats();
		}
		return listVotesSeatsReal;
	}
	
	//can differ from preferentialVotesSeats if number of candidates too small for all seats
	public int getPreferentialVotesSeatsReal() {
		int preferentialVotesSeatsReal = 0;
		for (SeatDistribution list : this.getListSeatDistribution()) {
			preferentialVotesSeatsReal += list.getPreferentialSeats();
		}
		return preferentialVotesSeatsReal;
	}
	public boolean isAmbiguous() {
		return ambiguous;
	}
	public void setAmbiguous(boolean ambiguous) {
		this.ambiguous = ambiguous;
	}
}
