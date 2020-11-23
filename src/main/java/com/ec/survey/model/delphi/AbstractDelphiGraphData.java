package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDelphiGraphData {
    private List<DelphiExplanationDto> explanations = new ArrayList<>();
    private DelphiQuestionType type;

    public DelphiQuestionType getType() {
        return type;
    }

    public void setType(DelphiQuestionType type) {
        this.type = type;
    }

    public List<DelphiExplanationDto> getExplanations() {
        return explanations;
    }

    public void setExplanations(List<DelphiExplanationDto> explanations) {
        this.explanations = explanations;
    }

    public enum DelphiQuestionType {
        SingleChoice,
        MultipleChoice,
        Matrix,
        Rating
    }
}
