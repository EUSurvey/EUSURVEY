package com.ec.survey.model.selfassessment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
	
	public Set<String> criteriaAboveBelowAverage(boolean above, int limitTableLines) {
		Map<String, Double> result = new HashMap<String, Double>();
		
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
		
		Map<String, Double> sortedMap = 
				result.entrySet().stream()
			    .sorted(Entry.comparingByValue())
			    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
			                              (e1, e2) -> e1, LinkedHashMap::new));
				
		
		if (limitTableLines == 0) {
			return sortedMap.keySet();
		}
		
		return sortedMap.keySet().stream().limit(limitTableLines).collect(Collectors.toSet());
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
