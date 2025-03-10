package com.ec.survey.model.chargeback;

import java.util.Map;
import java.util.TreeMap;

public class OrganisationCharge {
	public String name;
	public Map<String, MonthlyCharge> monthly = new TreeMap<>();
	
	public int getTotal_nb_surveys_published() {
		int result = 0;
		for (MonthlyCharge monthlyCharge : monthly.values()) {
			result += monthlyCharge.nb_surveys_published;
		}
		return result;
	}
	
	public int getTotal_multi_annual_surveys() {
		int result = 0;
		for (MonthlyCharge monthlyCharge : monthly.values()) {
			result += monthlyCharge.multi_annual_surveys;
		}
		return result;
	}
	
	public int getTotal_nb_contributions_received() {
		int result = 0;
		for (MonthlyCharge monthlyCharge : monthly.values()) {
			result += monthlyCharge.nb_contributions_received;
		}
		return result;
	}
	
}
