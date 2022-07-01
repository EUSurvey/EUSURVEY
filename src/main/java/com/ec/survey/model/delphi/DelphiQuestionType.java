package com.ec.survey.model.delphi;

import com.ec.survey.model.survey.*;

public enum DelphiQuestionType {
    SingleChoice,
    MultipleChoice,
    Matrix,
    Rating,
    FreeText,
    Number,
    Formula,
    Date,
    Time,
    RegEx,
    Table,
    Ranking;

    public static DelphiQuestionType from(Question question) {
        if (question instanceof SingleChoiceQuestion) {
            return SingleChoice;
        }

        if (question instanceof MultipleChoiceQuestion) {
            return MultipleChoice;
        }

        if (question instanceof Matrix) {
            return Matrix;
        }

        if (question instanceof RatingQuestion) {
            return Rating;
        }

        if (question instanceof FreeTextQuestion) {
            return FreeText;
        }

        if (question instanceof NumberQuestion) {
            return Number;
        }

        if (question instanceof FormulaQuestion) {
            return Formula;
        }

        if (question instanceof DateQuestion) {
            return Date;
        }

        if (question instanceof TimeQuestion) {
            return Time;
        }

        if (question instanceof RegExQuestion) {
            return RegEx;
        }

        if (question instanceof Table) {
            return Table;
        }

        if (question instanceof RankingQuestion) {
            return Ranking;
        }

        return null;
    }
}
