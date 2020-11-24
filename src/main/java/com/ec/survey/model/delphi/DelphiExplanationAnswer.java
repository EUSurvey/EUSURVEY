package com.ec.survey.model.delphi;

public class DelphiExplanationAnswer {
    private final String label;
    private final String value;

    public DelphiExplanationAnswer(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
