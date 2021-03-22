package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.Collection;

public class DelphiSection {
	private final Collection<DelphiQuestion> questions = new ArrayList<>();
	private String title;
	private int level;
	private boolean hasDelphiQuestions;
	private boolean hasDirectDelphiQuestions;
	
	public Collection<DelphiQuestion> getQuestions() {
		return questions;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isHasDelphiQuestions() {
		return hasDelphiQuestions;
	}

	public void setHasDelphiQuestions(boolean hasDelphiQuestions) {
		this.hasDelphiQuestions = hasDelphiQuestions;
	}

	public boolean isHasDirectDelphiQuestions() {
		return hasDirectDelphiQuestions;
	}

	public void setHasDirectDelphiQuestions(boolean hasDirectDelphiQuestions) {
		this.hasDirectDelphiQuestions = hasDirectDelphiQuestions;
	}
}
