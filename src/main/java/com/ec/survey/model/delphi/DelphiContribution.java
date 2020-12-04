package com.ec.survey.model.delphi;

import java.util.Date;

public class DelphiContribution {
    private int answerSetId;
    private String answerUid;
    private int column;
    private String explanation;
    private String questionUid;
    private int row;
    private Date update;
    private String value;

    public int getAnswerSetId() {
        return answerSetId;
    }

    public void setAnswerSetId(int answerSetId) {
        this.answerSetId = answerSetId;
    }

    public String getAnswerUid() {
        return answerUid;
    }

    public void setAnswerUid(String answerUid) {
        this.answerUid = answerUid;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getQuestionUid() {
        return questionUid;
    }

    public void setQuestionUid(String questionUid) {
        this.questionUid = questionUid;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
