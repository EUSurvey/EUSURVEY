package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.List;

public class DelphiTableEntry {
    private final List<DelphiTableAnswer> answers = new ArrayList<>();
    private String explanation;
    private String update;
    private int answerSetId;
    private final List<DelphiComment> comments = new ArrayList<>();
    private final List<DelphiTableFile> files = new ArrayList<>();

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
    
    public int getAnswerSetId() {
        return answerSetId;
    }

    public void setAnswerSetId(int answerSetId) {
        this.answerSetId = answerSetId;
    }
    
    public List<DelphiComment> getComments() {
        return comments;
    }

    public List<DelphiTableFile> getFiles() {
        return files;
    }
}
