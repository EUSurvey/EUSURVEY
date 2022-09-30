package com.ec.survey.model.evote;

public class DHondtEntry {
	
	private double value;
	private int seat;
	private int round;
	private int listIndex;
	private boolean invalid;
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public int getSeat() {
		return seat;
	}
	public void setSeat(int seat) {
		this.seat = seat;
	}
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public int getListIndex() {
		return listIndex;
	}
	public void setListIndex(int listIndex) {
		this.listIndex = listIndex;
	}
	public boolean isInvalid() {
		return invalid;
	}
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
}
