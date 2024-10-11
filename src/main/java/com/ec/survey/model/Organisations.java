package com.ec.survey.model;

import java.util.HashMap;
import java.util.Map;

public class Organisations {

	private Map<String, String> dgs = new HashMap<>();
	private Map<String, String> executiveAgencies = new HashMap<>();
	private Map<String, String> otherEUIs = new HashMap<>();
	private Map<String, String> nonEUIs = new HashMap<>();

	public Map<String, String> getDgs() {
		return dgs;
	}

	public void setDgs(Map<String, String> dgs) {
		this.dgs = dgs;
	}

	public Map<String, String> getExecutiveAgencies() {
		return executiveAgencies;
	}

	public void setExecutiveAgencies(Map<String, String> executiveAgencies) {
		this.executiveAgencies = executiveAgencies;
	}

	public Map<String, String> getOtherEUIs() {
		return otherEUIs;
	}

	public void setOtherEUIs(Map<String, String> otherEUIs) {
		this.otherEUIs = otherEUIs;
	}

	public Map<String, String> getNonEUIs() {
		return nonEUIs;
	}

	public void setNonEUIs(Map<String, String> nonEUIs) {
		this.nonEUIs = nonEUIs;
	}

}
