package com.ec.survey.model.delphi;

public class DelphiUpdateResult {
	private String message;
	private String link;
	private boolean open;

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
}
