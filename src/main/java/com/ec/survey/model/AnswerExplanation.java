package com.ec.survey.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ANSWERS_EXPLANATIONS")
public class AnswerExplanation implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private List<Integer> answerIds = new ArrayList<>();
    private String text;

    @Id
    @Column(name = "ANSWER_EXPLANATION_ID")
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ElementCollection
    public List<Integer> getAnswerIds() {
        return answerIds;
    }

    public void setAnswerIds(List<Integer> answerIds) {
        this.answerIds = answerIds;
    }

    public void removeAnswerId(Integer answerId) {
        this.answerIds.remove(answerId);
    }

    @Lob
    @Column(name = "TEXT", nullable = false)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
