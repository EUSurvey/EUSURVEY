package com.ec.survey.model;

import com.ec.survey.model.survey.Element;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
@Configurable
@Entity
@Table(name="STATISTICS")
public class Statistics {
	
	private int id;
	private int surveyId;	
	private String filterHash;
	private Map<String, Integer> requestedRecords = new HashMap<>();
	private Map<String, Double> requestedRecordsPercent = new HashMap<>();
	private Map<String, Double> totalsPercent = new HashMap<>();
	private Integer requestID;
	private Map<String, Integer> requestedRecordsScore = new HashMap<>();
	private Map<String, Double> requestedRecordsPercentScore = new HashMap<>();
	private Integer maxScore;
	private Integer bestScore;
	private Double meanScore;
	private Integer total;
	private Map<String, Integer> maxSectionScore = new HashMap<>();
	private Map<String, Double> meanSectionScore = new HashMap<>();	
	private Map<String, Double> bestSectionScore = new HashMap<>();	
	private Boolean invalid;
	
	@Id
	@Column(name = "ACCESS_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
		
	@Column(name="SURVEYID")
	public int getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}
	
	@ElementCollection
	public Map<String, Integer> getRequestedRecords() {
		return requestedRecords;
	}
	public void setRequestedRecords(Map<String, Integer> requestedRecords) {
		this.requestedRecords = requestedRecords;
	}
	
	@ElementCollection
	public Map<String, Double> getRequestedRecordsPercent() {
		return requestedRecordsPercent;
	}
	public void setRequestedRecordsPercent(Map<String, Double> requestedRecordsPrecent) {
		this.requestedRecordsPercent = requestedRecordsPrecent;
	}
	
	@ElementCollection
	public Map<String, Integer> getRequestedRecordsScore() {
		return requestedRecordsScore;
	}
	public void setRequestedRecordsScore(Map<String, Integer> requestedRecordsScore) {
		this.requestedRecordsScore = requestedRecordsScore;
	}
	
	@ElementCollection
	public Map<String, Double> getRequestedRecordsPercentScore() {
		return requestedRecordsPercentScore;
	}
	public void setRequestedRecordsPercentScore(Map<String, Double> requestedRecordsPercentScore) {
		this.requestedRecordsPercentScore = requestedRecordsPercentScore;
	}
	
	@ElementCollection
	public Map<String, Double> getTotalsPercent() {
		return totalsPercent;
	}
	public void setTotalsPercent(Map<String, Double> totalsPercent) {
		this.totalsPercent = totalsPercent;
	}
	
	@ElementCollection
	public Map<String, Integer> getMaxSectionScore() {
		return maxSectionScore;
	}
	public void setMaxSectionScore(Map<String, Integer> maxSectionScore) {
		this.maxSectionScore = maxSectionScore;
	}
	
	@ElementCollection
	public Map<String, Double> getMeanSectionScore() {
		return meanSectionScore;
	}
	public void setMeanSectionScore(Map<String, Double> meanSectionScore) {
		this.meanSectionScore = meanSectionScore;
	}
	
	@ElementCollection
	public Map<String, Double> getBestSectionScore() {
		return bestSectionScore;
	}
	public void setBestSectionScore(Map<String, Double> bestSectionScore) {
		this.bestSectionScore = bestSectionScore;
	}
	
	@Column(name="MAXSCORE")
	public Integer getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}
	
	@Column(name="BESTSCORE")
	public Integer getBestScore() {
		return bestScore;
	}
	public void setBestScore(Integer bestScore) {
		this.bestScore = bestScore;
	}
	
	@Column(name="MEANSCORE")
	public Double getMeanScore() {
		return meanScore;
	}
	public void setMeanScore(Double meanScore) {
		this.meanScore = meanScore;
	}
	
	@Column(name="NUMRESULTS")
	public Integer getTotal() {
		return total != null ? total : 0;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}	
	
	@Column(name="INVALID")
	public Boolean getInvalid() {
		return invalid;
	}
	public void setInvalid(Boolean invalid) {
		this.invalid = invalid;
	}
	
	@Transient
	public int getRequestedRecordsForMatrix(Element question, Element answer)
	{
		String id = question.getId().toString() + answer.getId().toString();
		Object result = requestedRecords.get(id);
		return (int) (result != null ? result : 0);
	}
	
	@Transient
	public double getRequestedRecordsPercentForMatrix(Element question, Element answer)
	{
		String id = question.getId().toString() + answer.getId().toString();
		Object result = requestedRecordsPercent.get(id);
		return (double) (result != null ? result : 0);
	}
	
	@Transient
	public double getTotalsPercentForMatrix(Element question, Element answer)
	{
		String id = question.getId().toString() + answer.getId().toString();
		Object result = totalsPercent.get(id);
		return (double) (result != null ? result : 0);
	}
	
	@Transient
	public int getRequestedRecordsForRatingQuestion(Element question, Integer answer)
	{
		String id = question.getId().toString() + answer.toString();
		Object result = requestedRecords.get(id);
		return (int) (result != null ? result : 0);
	}
	
	@Transient
	public double getRequestedRecordsPercentForRatingQuestion(Element question, Integer answer)
	{
		String id = question.getId().toString() + answer.toString();
		Object result = requestedRecordsPercent.get(id);
		return (double) (result != null ? result : 0);
	}
	
	@Transient
	public double getTotalsPercentForRatingQuestion(Element question, Integer answer)
	{
		String id = question.getId().toString() + answer.toString();
		Object result = totalsPercent.get(id);
		return (double) (result != null ? result : 0);
	}
	
	@Transient
	public int getRequestedRecordsForGallery(Element question, int index)
	{
		String id = question.getId().toString() + "-" + index;
		Object result = requestedRecords.get(id);
		return (int) (result != null ? result : 0);
	}
	
	@Transient
	public double getRequestedRecordsPercentForGallery(Element question, int index)
	{
		String id = question.getId().toString() + "-" + index;
		Object result = requestedRecordsPercent.get(id);
		return (double) (result != null ? result : 0);
	}
	
	@Transient
	public double getTotalsPercentForGallery(Element question, int index)
	{
		String id = question.getId().toString() + "-" + index;
		Object result = totalsPercent.get(id);
		return (double) (result != null ? result : 0);
	}
	
	@Column(name="FILTER")
	public String getFilterHash() {
		return filterHash;
	}
	public void setFilterHash(String filterHash) {
		this.filterHash = filterHash;
	}
	
	@Transient
	public Integer getRequestID() {
		return requestID;
	}
	public void setRequestID(Integer requestID) {
		this.requestID = requestID;
	}
		
}
