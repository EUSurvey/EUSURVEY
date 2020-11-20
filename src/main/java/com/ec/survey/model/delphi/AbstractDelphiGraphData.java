package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDelphiGraphData {
    private List<DelphiExplanation> explanations = new ArrayList<>();

    public List<DelphiExplanation> getExplanations() {
        return explanations;
    }

    public void setExplanations(List<DelphiExplanation> explanations) {
        this.explanations = explanations;
    }

    public abstract String getType();
}
