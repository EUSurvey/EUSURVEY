package com.ec.survey.model.delphi;

public abstract class AbstractDelphiGraphData {
    private DelphiQuestionType type;

    public DelphiQuestionType getType() {
        return type;
    }

    public void setType(DelphiQuestionType type) {
        this.type = type;
    }
}
