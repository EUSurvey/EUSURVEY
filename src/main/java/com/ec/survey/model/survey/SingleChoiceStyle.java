package com.ec.survey.model.survey;

import java.util.Objects;

public enum SingleChoiceStyle {
    RADIO("radio"),
    SELECT("select"),
    LIKERT("likert"),
    BUTTONS("buttons");

    private final String textRepresentation;

    SingleChoiceStyle(String textRepresentation){
        this.textRepresentation = textRepresentation;
    }

    public String getText() {
        return textRepresentation;
    }

    public static SingleChoiceStyle getFromText(String text){
        for (SingleChoiceStyle t : values()){
            if (Objects.equals(t.textRepresentation, text)){
                return t;
            }
        }
        return null;
    }
}