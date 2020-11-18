package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.Collection;

public class DelphiSection {
	private final Collection<DelphiQuestion> questions = new ArrayList<>();
	private String title;
	
	public Collection<DelphiQuestion> getQuestions() {
		return questions;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
