package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.Collection;

public class DelphiStructure {
	private final Collection<DelphiSection> sections = new ArrayList<>();

	public Collection<DelphiSection> getSections() {
		return sections;
	}
}
