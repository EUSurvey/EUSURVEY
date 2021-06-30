package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.Collection;

public class DelphiStructure {
	private final Collection<DelphiSection> sections = new ArrayList<>();
	private boolean unansweredMandatoryQuestions;

	public Collection<DelphiSection> getSections() {
		return sections;
	}

	public boolean isUnansweredMandatoryQuestions() {
		return unansweredMandatoryQuestions;
	}

	public void setUnansweredMandatoryQuestions(boolean unansweredMandatoryQuestions) {
		this.unansweredMandatoryQuestions = unansweredMandatoryQuestions;
	}
}
