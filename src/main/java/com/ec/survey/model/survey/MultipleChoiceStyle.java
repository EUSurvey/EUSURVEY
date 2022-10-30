package com.ec.survey.model.survey;

import java.util.Objects;

public enum MultipleChoiceStyle {
    CHECKBOX("checkbox"),
    LIST("list"),
    EVOTE("evote");

    private final String textRepresentation;

    MultipleChoiceStyle(String textRepresentation){
        this.textRepresentation = textRepresentation;
    }

    public String getText(){
        return textRepresentation;
    }

    public static MultipleChoiceStyle getFromText(String text){
        for (MultipleChoiceStyle t : values()){
            if (Objects.equals(t.textRepresentation, text)){
                return t;
            }
            if (text.startsWith(t.textRepresentation)) {
                //case for evote-brussels, evote-ispra, evote-luxembourg, evote-outside
                return t;
            }
        }
        return null;
    }
}