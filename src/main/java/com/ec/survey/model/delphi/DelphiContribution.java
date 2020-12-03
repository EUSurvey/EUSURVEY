package com.ec.survey.model.delphi;

import java.util.Date;

public class DelphiContribution {
    private int answerSetId;
    private String explanation;
    private String questionUid;
    private String answerUid;
    private Date update;
    private String value;

    public int getAnswerSetId() {
        return answerSetId;
    }

    public void setAnswerSetId(int answerSetId) {
        this.answerSetId = answerSetId;
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

	public String getAnswerUid() {
		return answerUid;
	}

	public void setAnswerUid(String answerUid) {
		this.answerUid = answerUid;
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
