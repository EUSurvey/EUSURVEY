package com.ec.survey.model.delphi;

import java.util.Date;
import java.util.List;

public class DelphiExplanationDto {
    private String explanation;
    private Date update;
    private List<String> values;

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

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
