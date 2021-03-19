package com.ec.survey.model.delphi;

public class DelphiQuestion {
	private String title;
	private String uid;
	private int id;
	private String answer = "";
	private boolean maxDistanceExceeded;
	private boolean changedForMedian;
	private boolean hasUnreadComments;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isMaxDistanceExceeded() {
		return maxDistanceExceeded;
	}

	public void setMaxDistanceExceeded(boolean maxDistanceExceeded) {
		this.maxDistanceExceeded = maxDistanceExceeded;
	}
	
	public Boolean getChangedForMedian() {
		return changedForMedian;
	}
	public void setChangedForMedian(Boolean changedForMedian) {
		this.changedForMedian = changedForMedian;
	}

	public boolean getHasUnreadComments() {
		return hasUnreadComments;
	}

	public void setHasUnreadComments(boolean hasUnreadComments) {
		this.hasUnreadComments = hasUnreadComments;
	}
}
