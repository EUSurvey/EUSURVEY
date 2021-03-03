package com.ec.survey.model.delphi;

import java.util.UUID;

public class DelphiTableAnswer {
    private final String question;
    private final String value;
    private final String uid;

    public DelphiTableAnswer(String question, String value) {
        this.question = question;
        this.value = value;
        this.uid = UUID.randomUUID().toString();
    }

    public String getQuestion() {
        return question;
    }

    public String getValue() {
        return value;
    }
    
    public String getUid() {
        return uid;
    }
}
