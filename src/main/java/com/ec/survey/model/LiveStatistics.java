package com.ec.survey.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="LIVESTATISTICS")
public class LiveStatistics {
	
	private int id;
	private int possibleAnswerId;	
	private String possibleAnswerUid;
	private int questionId;
	private String questionUid;
	private int count;
	
	@Id
	@Column(name = "LIVESTAT_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
		
	@Column(name="PAID")
	public int getPossibleAnswerId() {
		return possibleAnswerId;
	}
	public void setPossibleAnswerId(int possibleAnswerId) {
		this.possibleAnswerId = possibleAnswerId;
	}
	
	@Column(name="PAUID")
	public String getPossibleAnswerUid() {
		return possibleAnswerUid;
	}
	public void setPossibleAnswerUid(String possibleAnswerUid) {
		this.possibleAnswerUid = possibleAnswerUid;
	}
	
	@Column(name="QID")
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	
	@Column(name="QUID")
	public String getQuestionUid() {
		return questionUid;
	}
	public void setQuestionUid(String questionUid) {
		this.questionUid = questionUid;
	}
	
	@Column(name="NUM")
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
		
}
