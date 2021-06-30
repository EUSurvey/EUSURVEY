package com.ec.survey.model.delphi;

import com.ec.survey.tools.ConversionTools;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DelphiTableEntry {
    private final List<DelphiTableAnswer> answers = new ArrayList<>();
    private String explanation;

    private String updateString;
    
    private int answerSetId;
    private String answerSetUniqueCode;
    private final List<DelphiComment> comments = new ArrayList<>();
    private final List<DelphiTableFile> files = new ArrayList<>();

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getUpdate() {
        return updateString;
    }
    
    public void setUpdate(String updateString) {
        this.updateString = updateString;
    }

    public void setUpdateDate(Date update) {
        this.updateString = ConversionTools.getFullString(update);
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

    public String getAnswerSetUniqueCode() {
        return answerSetUniqueCode;
    }

    public void setAnswerSetUniqueCode(String answerSetUniqueCode) {
        this.answerSetUniqueCode = answerSetUniqueCode;
    }

    public List<DelphiComment> getComments() {
        return comments;
    }

    public List<DelphiTableFile> getFiles() {
        return files;
    }
}
