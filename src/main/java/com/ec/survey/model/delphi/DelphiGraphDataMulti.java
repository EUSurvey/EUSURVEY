package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.Collection;

public class DelphiGraphDataMulti extends AbstractDelphiGraphData {
    private final Collection<DelphiGraphDataSingle> questions = new ArrayList<>();
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Collection<DelphiGraphDataSingle> getQuestions() {
        return questions;
    }

    public void addQuestion(DelphiGraphDataSingle question) {
        questions.add(question);
    }
}
