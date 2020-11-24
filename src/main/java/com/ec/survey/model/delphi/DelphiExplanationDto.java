package com.ec.survey.model.delphi;

import java.util.Date;
import java.util.List;

public class DelphiExplanationDto {
    private List<DelphiExplanationAnswer> answers;
    private String explanation;
    private Date update;

    public List<DelphiExplanationAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<DelphiExplanationAnswer> answers) {
        this.answers = answers;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }
}
