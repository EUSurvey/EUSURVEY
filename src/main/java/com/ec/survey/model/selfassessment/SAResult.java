package com.ec.survey.model.selfassessment;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class SAResult {
	private SATargetDataset comparisonDataset;
	private List<SACriterion> criteria  = new ArrayList<SACriterion>();
	private List<Double> values = new ArrayList<Double>();
	private List<Double> comparisonValues = new ArrayList<Double>();
	private SAReportConfiguration configuration; 
	
	private List<List<Double>> valuesForTypes = new ArrayList<List<Double>>();
	private List<List<Double>> comparisonValuesForTypes = new ArrayList<List<Double>>();
	
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

	public String gap(double v1, double v2) {
		if (v1 == v2) return "";
		if (v1 > v2) return "(+" + (Math.round((v1-v2)*10.0) / 10.0) + ")";
		if (v1 < v2) return "(-" + (Math.round((v2-v1)*10.0) / 10.0) + ")";
		
		return "";
	}
	
	public List<String> criteriaAboveBelowAverage(boolean above, int limitTableLines) {
		var result = new HashMap<String, Double>();
		
		for (int i = 0; i < criteria.size(); i++) {
			double v1 = values.get(i);
			double v2 = comparisonValues.get(i);
			if (above && v1 > v2) {				
				result.put(criteria.get(i).getName(), v2-v1);
			}
			if (!above && v1 < v2) {				
				result.put(criteria.get(i).getName(), v1-v2);
			}
		}

		Comparator<String> valueThenNameComparer = (a, b) -> {
			var valueA = result.get(a);
			var valueB = result.get(b);

			var diff = valueA - valueB;

			//If they are (almost) equal, sort them by name
			if (Math.abs(diff) < 0.0001){
				diff = a.compareTo(b);
			}

			return (int) Math.signum(diff);
		};

		if (limitTableLines == 0) limitTableLines = Integer.MAX_VALUE;

		return result.keySet().stream()
				.sorted(valueThenNameComparer)
				.limit(limitTableLines)
				.collect(Collectors.toList());
	}

	public List<List<Double>> getValuesForTypes() {
		return valuesForTypes;
	}

	public void setValuesForTypes(List<List<Double>> valuesForTypes) {
		this.valuesForTypes = valuesForTypes;
	}

	public List<List<Double>> getComparisonValuesForTypes() {
		return comparisonValuesForTypes;
	}

	public void setComparisonValuesForTypes(List<List<Double>> comparisonValuesForTypes) {
		this.comparisonValuesForTypes = comparisonValuesForTypes;
	}
}
