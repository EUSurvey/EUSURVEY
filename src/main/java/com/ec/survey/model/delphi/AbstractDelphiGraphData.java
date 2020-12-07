package com.ec.survey.model.delphi;

import com.ec.survey.model.survey.DelphiChartType;

public abstract class AbstractDelphiGraphData {
    private DelphiChartType chartType;
    private DelphiQuestionType questionType;

    public DelphiChartType getChartType() {
        return chartType;
    }

    public void setChartType(DelphiChartType chartType) {
        this.chartType = chartType;
    }

    public DelphiQuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(DelphiQuestionType questionType) {
        this.questionType = questionType;
    }
}
