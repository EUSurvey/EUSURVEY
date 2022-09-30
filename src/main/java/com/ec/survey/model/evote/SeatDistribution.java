package com.ec.survey.model.evote;

public class SeatDistribution {
	
	private String name;
	private int listVotes;
	private int listVotesWeighted;
	private int luxListVotes;
	private int listSeats;
	private double listPercent;
	private double listPercentFinal;
	private int preferentialVotes;
	private int preferentialSeats;
	private double preferentialPercent;
	private double preferentialPercentFinal;
	private double listPercentWeighted;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getListVotes() {
		return listVotes;
	}
	public void setListVotes(int listVotes) {
		this.listVotes = listVotes;
	}
	public int getListVotesWeighted() {
		return listVotesWeighted;
	}
	public void setListVotesWeighted(int listVotesWeighted) {
		this.listVotesWeighted = listVotesWeighted;
	}
	public int getListSeats() {
		return listSeats;
	}
	public void setListSeats(int listSeats) {
		this.listSeats = listSeats;
	}
	public double getListPercent() {
		return listPercent;
	}
	public void setListPercent(double listPercent) {
		this.listPercent = listPercent;
	}
	public int getPreferentialVotes() {
		return preferentialVotes;
	}
	public void setPreferentialVotes(int preferentialVotes) {
		this.preferentialVotes = preferentialVotes;
	}
	public int getPreferentialSeats() {
		return preferentialSeats;
	}
	public void setPreferentialSeats(int preferentialSeats) {
		this.preferentialSeats = preferentialSeats;
	}
	public double getPreferentialPercent() {
		return preferentialPercent;
	}
	public void setPreferentialPercent(double preferentialPercent) {
		this.preferentialPercent = preferentialPercent;
	}
	public double getListPercentWeighted() {
		return listPercentWeighted;
	}
	public void setListPercentWeighted(double listPercentWeighted) {
		this.listPercentWeighted = listPercentWeighted;
	}
	
	public int getTotalWeighted() {
		return listVotesWeighted + preferentialVotes;
	}
	
	// without counting lists with not enough votes
	public double getListPercentFinal() {
		return listPercentFinal;
	}
	public void setListPercentFinal(double listPercentFinal) {
		this.listPercentFinal = listPercentFinal;
	}
	public double getPreferentialPercentFinal() {
		return preferentialPercentFinal;
	}
	public void setPreferentialPercentFinal(double preferentialPercentFinal) {
		this.preferentialPercentFinal = preferentialPercentFinal;
	}
	public int getLuxListVotes() {
		return luxListVotes;
	}
	public void setLuxListVotes(int luxListVotes) {
		this.luxListVotes = luxListVotes;
	}
}
