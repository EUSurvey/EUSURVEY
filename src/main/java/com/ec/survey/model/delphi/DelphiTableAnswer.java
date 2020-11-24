package com.ec.survey.model.delphi;

public class DelphiTableAnswer {
    private final String question;
    private final String value;

    public DelphiTableAnswer(String question, String value) {
        this.question = question;
        this.value = value;
    }

    public String getQuestion() {
        return question;
    }

    public String getValue() {
        return value;
    }
}
