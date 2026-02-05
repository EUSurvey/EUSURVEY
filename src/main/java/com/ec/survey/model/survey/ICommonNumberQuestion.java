package com.ec.survey.model.survey;

import javax.persistence.Transient;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public interface ICommonNumberQuestion {

    DeletedMinMax getDeletedMinMax();
    void setDeletedMinMax(DeletedMinMax minMax);
    Integer getDecimalPlaces();
    Double getMin();
    Double getMax();

    @Transient
    default boolean showStatisticsForNumberQuestion(boolean includingDeleted) {

        var deletedMinMax = getDeletedMinMax();

        if (deletedMinMax == null) {
            includingDeleted = false;
        }

        if (getDecimalPlaces() > 0 || getMin() == null || getMax() == null) {
            return false;
        }

        var min = getMin();
        var max = getMax();

        if (includingDeleted) {
            if (deletedMinMax.getMin() < min) {
                min = deletedMinMax.getMin();
            }
            if (deletedMinMax.getMax() > max) {
                max = deletedMinMax.getMax();
            }
        }

        return (max - min) <= 10;
    }

    @Transient
    default List<String> getAllPossibleAnswers(boolean includingDeleted) {

        var deletedMinMax = getDeletedMinMax();

        if (deletedMinMax == null) {
            includingDeleted = false;
        }

        List<String> answers = new ArrayList<>();

        if (!showStatisticsForNumberQuestion(includingDeleted)) {
            return answers;
        }

        NumberFormat nf = DecimalFormat.getInstance();
        nf.setMaximumFractionDigits(0);

        var min = getMin();
        var max = getMax();

        if (includingDeleted) {
            if (deletedMinMax.getMin() < min) {
                min = deletedMinMax.getMin();
            }
            if (deletedMinMax.getMax() > max) {
                max = deletedMinMax.getMax();
            }
        }

        for(var i = min; i <= max; i++) {
            answers.add(nf.format(i));
        }

        return answers;
    }
}
