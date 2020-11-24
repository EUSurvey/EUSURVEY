package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.List;

public class DelphiTableEntry {
    private final List<DelphiTableAnswer> answers = new ArrayList<>();
    private String explanation;
    private String update;

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public List<DelphiTableAnswer> getAnswers() {
        return answers;
    }
}
