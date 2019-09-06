package com.ec.survey.model;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "PUBLICATION")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Publication implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private boolean showContent;
	private boolean showStatistics;
	private boolean showCharts;
	private boolean showSearch = true;
	private boolean allQuestions = true;
	private boolean allContributions = true;
	private boolean showUploadedDocuments;
	private ResultFilter filter = new ResultFilter();
	private String password = null;
	
	@Id
	@Column(name = "PUB_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	@Column(name = "PUB_CONT")
	public boolean isShowContent() {
		return showContent;
	}
	public void setShowContent(boolean showContent) {
		this.showContent = showContent;
	}
	
	@Column(name = "PUB_STAT")
	public boolean isShowStatistics() {
		return showStatistics;
	}
	public void setShowStatistics(boolean showStatistics) {
		this.showStatistics = showStatistics;
	}
	
	@Column(name = "PUB_CHARTS")
	public boolean isShowCharts() {
		return showCharts;
	}
	public void setShowCharts(boolean showCharts) {
		this.showCharts = showCharts;
	}
	
	@Column(name = "PUB_SEARCH")
	public boolean isShowSearch() {
		return showSearch;
	}
	public void setShowSearch(boolean showSearch) {
		this.showSearch = showSearch;
	}
	
	@Column(name = "PUB_PASSWORD")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name = "PUB_UPLOADED")
	public Boolean getShowUploadedDocuments() {
		return showUploadedDocuments;
	}
	public void setShowUploadedDocuments(Boolean showUploadedDocuments) {
		this.showUploadedDocuments = showUploadedDocuments != null ? showUploadedDocuments : true;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	public ResultFilter getFilter() {
		return filter;
	}
	public void setFilter(ResultFilter filter) {
		this.filter = filter;
	}
	
	@Transient
	public boolean isSelected(int questionId)
	{
		return filter!= null && filter.getVisibleQuestions().contains(Integer.toString(questionId));
	}
	
	@Transient
	public boolean isSelected(String questionId)
	{
		return filter!= null && filter.getVisibleQuestions().contains(questionId);
	}
	
	@Transient
	public boolean isFiltered(int questionId)
	{
		return filter!=null && filter.getFilterValues().containsKey(Integer.toString(questionId));
	}
	
	@Transient
	public boolean isFiltered(int questionId, int possibleanswerId)
	{
		return filter!=null && filter.contains(Integer.toString(questionId), Integer.toString(possibleanswerId));
	}
	
	@Column(name = "PUB_ALLQ")
	public boolean isAllQuestions() {
		return allQuestions;
	}
	public void setAllQuestions(boolean allQuestions) {
		this.allQuestions = allQuestions;
	}
	
	@Column(name = "PUB_ALLCONT")
	public boolean isAllContributions() {
		return allContributions;
	}
	public void setAllContributions(boolean allContributions) {
		this.allContributions = allContributions;
	}
	
	@Transient
	public boolean isActive() {
		return showContent || showCharts || showStatistics;
	}		
}

