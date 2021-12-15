package com.ec.survey.model.delphi;

import com.ec.survey.tools.ConversionTools;

public class DelphiTableAnswer {
    private final String question;
    private final String value;

    public DelphiTableAnswer(String question, String value) {
        this.question = ConversionTools.removeHTML(question);
        this.value = ConversionTools.removeHTML(value);
    }

    public String getQuestion() {
        return question;
    }

    public String getValue() {
        return value;
    }
}
