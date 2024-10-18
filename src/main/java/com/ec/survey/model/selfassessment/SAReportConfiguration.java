package com.ec.survey.model.selfassessment;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SAREPORTCONFIG", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "SAREPORTCONFIG_SURVEY" }, name = "SURVEY_REPORT") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SAReportConfiguration implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String surveyUID;
	private String algorithm = "AVG";
	private String introduction = "";
	private String customFeedback = "";
	private int coefficient = 5;
	private int limitTableLines;
	private String selectedChart = "SPIDER";
	private boolean targetDatasetSelection = true;
	private boolean charts = true;
	private boolean legend = true;
	private boolean scale = true;
	private boolean separateCompetencyTypes = true;
	private boolean resultsTable = true;
	private boolean competencyType = true;
	private boolean targetScores = true;
	private boolean gaps = true;
	private boolean performanceTable = true;

	public static final String INTRODUCTIONSELFASSESSMENT = "<span style=\"color: #4caf50; font-size: 200%; font-weight: bold;\">✓</span> <strong style=\"color: black; margin-left: 6px;\"> Contribution successfully submitted</strong><br /><br />This page displays your results as well as the positive and negative gaps of your scores from the target scores for your current data profile.<br /><br />By selecting a different dataset from the drop-down list below, you can compare your results with the target scores of other profiles.";
	public static final String CUSTOMFEEDBACKSELFASSESSMENT = "<p>Thank you for your contribution!</p>";

	@Id
	@Column(name = "SAREPORTCONFIG_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "SAREPORTCONFIG_SURVEY")
	public String getSurveyUID() {
		return surveyUID;
	}

	public void setSurveyUID(String surveyUID) {
		this.surveyUID = surveyUID;
	}

	@Column(name = "SAREPORTCONFIG_ALGORITHM")
	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@Lob
	@Column(name = "SAREPORTCONFIG_INTRO", nullable = false, columnDefinition="text")
	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	@Lob
	@Column(name = "SAREPORTCONFIG_FEEDBACK", nullable = false, columnDefinition="text")
	public String getCustomFeedback() {
		return customFeedback;
	}

	public void setCustomFeedback(String customFeedback) {
		this.customFeedback = customFeedback;
	}

	@Column(name = "SAREPORTCONFIG_COEFFICIENT")
	public int getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(int coefficient) {
		this.coefficient = coefficient;
	}

	@Column(name = "SAREPORTCONFIG_LIMIT")
	public int getLimitTableLines() {
		return limitTableLines;
	}

	public void setLimitTableLines(int limitTableLines) {
		this.limitTableLines = limitTableLines;
	}

	@Column(name = "SAREPORTCONFIG_CHART")
	public String getSelectedChart() {
		return selectedChart;
	}

	public void setSelectedChart(String selectedChart) {
		this.selectedChart = selectedChart;
	}

	@Column(name = "SAREPORTCONFIG_TDS")
	public boolean getTargetDatasetSelection() {
		return targetDatasetSelection;
	}

	public void setTargetDatasetSelection(boolean targetDatasetSelection) {
		this.targetDatasetSelection = targetDatasetSelection;
	}

	@Column(name = "SAREPORTCONFIG_CHARTS")
	public boolean getCharts() {
		return charts;
	}

	public void setCharts(boolean charts) {
		this.charts = charts;
	}

	@Column(name = "SAREPORTCONFIG_LEGEND")
	public boolean getLegend() {
		return legend;
	}

	public void setLegend(boolean legend) {
		this.legend = legend;
	}

	@Column(name = "SAREPORTCONFIG_SCALE")
	public boolean getScale() {
		return scale;
	}

	public void setScale(boolean scale) {
		this.scale = scale;
	}

	@Column(name = "SAREPORTCONFIG_SCT")
	public boolean getSeparateCompetencyTypes() {
		return separateCompetencyTypes;
	}

	public void setSeparateCompetencyTypes(boolean separateCompetencyTypes) {
		this.separateCompetencyTypes = separateCompetencyTypes;
	}

	@Column(name = "SAREPORTCONFIG_RESULTS")
	public boolean getResultsTable() {
		return resultsTable;
	}

	public void setResultsTable(boolean resultsTable) {
		this.resultsTable = resultsTable;
	}

	@Column(name = "SAREPORTCONFIG_COMP")
	public boolean getCompetencyType() {
		return competencyType;
	}

	public void setCompetencyType(boolean competencyType) {
		this.competencyType = competencyType;
	}

	@Column(name = "SAREPORTCONFIG_TARGETSCORE")
	public boolean getTargetScores() {
		return targetScores;
	}

	public void setTargetScores(boolean targetScores) {
		this.targetScores = targetScores;
	}

	@Column(name = "SAREPORTCONFIG_GAPS")
	public boolean getGaps() {
		return gaps;
	}

	public void setGaps(boolean gaps) {
		this.gaps = gaps;
	}

	@Column(name = "SAREPORTCONFIG_PERFTABLE")
	public boolean getPerformanceTable() {
		return performanceTable;
	}

	public void setPerformanceTable(boolean performanceTable) {
		this.performanceTable = performanceTable;
	}	
	
}
