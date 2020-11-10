package com.ec.survey.model;

import javax.persistence.*;

@Entity
@Table(name = "ANSWERS_EXPLANATIONS")
public class AnswerExplanation implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Answer referredAnswer;
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

    @ManyToOne
    @JoinColumn(name = "ANSWER_ID")
    public Answer getReferredAnswer() {
        return referredAnswer;
    }

    public void setReferredAnswer(Answer referredAnswer) {
        this.referredAnswer = referredAnswer;
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
