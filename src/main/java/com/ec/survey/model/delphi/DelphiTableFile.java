package com.ec.survey.model.delphi;

public class DelphiTableFile {

	final private String name;
	final private String uid;

	public DelphiTableFile(String name, String uid) {
		this.name = name;
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public String getUid() {
		return uid;
	}
}
