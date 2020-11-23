package com.ec.survey.model.delphi;

import java.util.Date;
import java.util.List;

public class DelphiExplanationDto {
    private String explanation;
    private Date update;
    private List<Object> values;

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

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }
}
