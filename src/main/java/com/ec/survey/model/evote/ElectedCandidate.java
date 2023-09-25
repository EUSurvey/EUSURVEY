package com.ec.survey.model.evote;

import com.ec.survey.tools.Tools;

public class ElectedCandidate {
	
	private String list;
	private String name;
	private int votes;
	private int seats;
	private int position; // the index in the list
	private boolean preferentialSeat;
	private boolean reallocatedSeat;
	private boolean ambiguous;
	private boolean listNotAccepted;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isPreferentialSeat() {
		return preferentialSeat;
	}

	public void setPreferentialSeat(boolean preferentialSeat) {
		this.preferentialSeat = preferentialSeat;
	}

	public boolean isReallocatedSeat() {
		return reallocatedSeat;
	}

	public void setReallocatedSeat(boolean reallocatedSeat) {
		this.reallocatedSeat = reallocatedSeat;
	}

	public boolean isAmbiguous() {
		return ambiguous;
	}

	public void setAmbiguous(boolean ambiguous) {
		this.ambiguous = ambiguous;
	}

	public boolean isListNotAccepted() {
		return listNotAccepted;
	}

	public void setListNotAccepted(boolean listNotAccepted) {
		this.listNotAccepted = listNotAccepted;
	}
}
