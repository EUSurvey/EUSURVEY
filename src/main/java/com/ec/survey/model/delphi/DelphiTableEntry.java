package com.ec.survey.model.delphi;

import com.ec.survey.model.AnswerExplanation;
import com.ec.survey.tools.ConversionTools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DelphiTableEntry {
    private final List<DelphiTableAnswer> answers = new ArrayList<>();
    private DelphiExplanation explanation;

    private String updateString;
    
    private int answerSetId;
    private String answerSetUniqueCode;
    private final List<DelphiComment> comments = new ArrayList<>();
    private final List<DelphiTableFile> files = new ArrayList<>();

    public DelphiExplanation getExplanation() {
        return explanation;
    }

    public void setExplanation(DelphiExplanation explanation) {
        this.explanation = explanation;
    }

    public void initializeExplanation(AnswerExplanation explanation, List<String> likes) {
        this.explanation = new DelphiExplanation();
        this.explanation.setExplanationId(explanation.getId());
        this.explanation.setText(explanation.getText());
        this.explanation.setFileInfoFromFiles(explanation.getFiles());
        this.explanation.setLikes(likes);
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

    public String getAnswer() {
        //manually construct view in results table
        String finalAnswer = "";
        for (DelphiTableAnswer answer : answers) {
            if (answer.getQuestion() != null) {
                finalAnswer = finalAnswer.concat(answer.getQuestion() + ":");
            }
            finalAnswer = finalAnswer.concat(answer.getValue());
        }

        return finalAnswer;
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
