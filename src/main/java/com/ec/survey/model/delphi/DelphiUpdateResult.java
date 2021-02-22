package com.ec.survey.model.delphi;

public class DelphiUpdateResult {
	private String message;
	private String link;
	private boolean open;
	private boolean changedForMedian;
	private boolean changeExplanationText;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public DelphiUpdateResult(String message) {
		this.message = message;
	}

	public Boolean getChangedForMedian() {
		return changedForMedian;
	}
	
	public void setChangedForMedian(Boolean changedForMedian) {
		this.changedForMedian = changedForMedian;
	}

	public boolean isChangeExplanationText() {
		return changeExplanationText;
	}

	public void setChangeExplanationText(boolean changeExplanationText) {
		this.changeExplanationText = changeExplanationText;
	}
}
