package com.ec.survey.model.delphi;

import javax.persistence.Table;
import javax.persistence.*;

@Entity
@Table(name = "EXPLANATION_LIKES", indexes = {@Index(name = "EXPLANATIONLIKE_IDX", columnList = "ANSWER_EXPLANATION_ID, ANSWER_SET_CODE")})
public class DelphiExplanationLike implements java.io.Serializable  {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer answerExplanationId;    //explanation of which answerset was liked
    private String uniqueCode;      //the code of the explanation of the person that liked this comment

    public DelphiExplanationLike() {}

    public DelphiExplanationLike(Integer explanationId, String uniqueCode) {
        this.answerExplanationId = explanationId;
        this.uniqueCode = uniqueCode;
    }

    @Id
    @Column(name = "EXPLANATION_LIKE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="ANSWER_EXPLANATION_ID")
    public Integer getAnswerExplanationId() {
        return answerExplanationId;
    }
    public void setAnswerExplanationId(Integer explanationId) {
        this.answerExplanationId = explanationId;
    }

    @Column(name="ANSWER_SET_CODE")
    public String getUniqueCode() {
        return uniqueCode;
    }
    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }
}
