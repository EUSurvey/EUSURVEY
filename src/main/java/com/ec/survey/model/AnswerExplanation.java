package com.ec.survey.model;

import javax.persistence.*;

@Entity
@Table(name = "ANSWERS_EXPLANATIONS")
public class AnswerExplanation implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer answerSetId;
	private String questionUid;
	private String text;

	public AnswerExplanation() {}

	public AnswerExplanation(int answerSetId, String questionUid) {
		this.answerSetId = answerSetId;
		this.questionUid = questionUid;
	}

	@Id
	@Column(name = "ANSWER_EXPLANATION_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="ANSWER_SET_ID")
	public Integer getAnswerSetId() {
		return answerSetId;
	}
	public void setAnswerSetId(Integer answerSetId) {
		this.answerSetId = answerSetId;
	}

	@Column(name="QUESTION_UID")
	public String getQuestionUid() {
		return questionUid;
	}
	public void setQuestionUid(String questionUid) {
		this.questionUid = questionUid;
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
