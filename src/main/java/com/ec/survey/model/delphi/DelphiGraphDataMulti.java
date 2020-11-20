package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.Collection;

public class DelphiGraphDataMulti extends AbstractDelphiGraphData {
    private final Collection<DelphiGraphDataSingle> questions = new ArrayList<>();

    public Collection<DelphiGraphDataSingle> getQuestions() {
        return questions;
    }

    public void addQuestion(DelphiGraphDataSingle question) {
        questions.add(question);
    }

    @Override
    public String getType() {
        return "multi";
    }
}
