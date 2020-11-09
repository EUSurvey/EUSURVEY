package com.ec.survey.model;

import com.ec.survey.model.administration.User;

import javax.persistence.*;

@Entity
@Table(name = "ANSWER_EXPLANATION")
public class AnswerExplanation implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private User authorUser;
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
    @JoinColumn(name = "USER_ID")
    public User getAuthorUser() {
        return authorUser;
    }

    public void setAuthorUser(User authorUser) {
        this.authorUser = authorUser;
    }

    @ManyToOne
    @JoinColumn(name = "ANSWER_ID")
    public Answer getReferredAnswer() {
        return referredAnswer;
    }

    public void setReferredAnswer(Answer referredAnswer) {
        this.referredAnswer = referredAnswer;
    }

    @Column(name = "TEXT")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
