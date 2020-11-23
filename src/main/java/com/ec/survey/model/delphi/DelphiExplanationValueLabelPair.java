package com.ec.survey.model.delphi;

public class DelphiExplanationValueLabelPair {
    private final String label;
    private final String value;

    public DelphiExplanationValueLabelPair(String label, String value) {
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
