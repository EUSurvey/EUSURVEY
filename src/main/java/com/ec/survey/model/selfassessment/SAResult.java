package com.ec.survey.model.selfassessment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SAResult {
	private SATargetDataset comparisonDataset;
	private List<SACriterion> criteria;
	private List<Double> values = new ArrayList<Double>();
	private List<Double> comparisonValues = new ArrayList<Double>();
	private SAReportConfiguration configuration; 
	
	public List<String> getCriteriaNames() {
		return criteria.stream().map(SACriterion::getName)
	              .collect(Collectors.toList());
	}
	
	public SATargetDataset getComparisonDataset() {
		return comparisonDataset;
	}
	public void setComparisonDataset(SATargetDataset comparisonDataset) {
		this.comparisonDataset = comparisonDataset;
	}
	public List<SACriterion> getCriteria() {
		return criteria;
	}
	public void setCriteria(List<SACriterion> criteria) {
		this.criteria = criteria;
	}

	public SAReportConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SAReportConfiguration configuration) {
		this.configuration = configuration;
	}

	public List<Double> getValues() {
		return values;
	}

	public void setValues(List<Double> values) {
		this.values = values;
	}

	public List<Double> getComparisonValues() {
		return comparisonValues;
	}

	public void setComparisonValues(List<Double> comparisonValues) {
		this.comparisonValues = comparisonValues;
	}


}
