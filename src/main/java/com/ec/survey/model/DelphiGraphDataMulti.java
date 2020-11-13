package com.ec.survey.model;

import java.util.HashMap;
import java.util.Map;

public class DelphiGraphDataMulti {
    public final String type = "multi";
    private final Map<String, DelphiGraphDataSingle> questions = new HashMap<>();

    public Map<String, DelphiGraphDataSingle> getQuestions() {
        return new HashMap<>(questions);
    }

    public void addQuestion(String label, DelphiGraphDataSingle question) {
        questions.put(label, question);
    }
}
