package com.ec.survey.model.chargeback;

import java.util.Map;
import java.util.TreeMap;

public class OrganisationCharge {
	public String name;
	public Map<String, MonthlyCharge> monthly = new TreeMap<>();
	
	public int getTotal_v1() {
		int result = 0;
		for (MonthlyCharge monthlyCharge : monthly.values()) {
			result += monthlyCharge.v1;
		}
		return result;
	}
	
	public int getTotal_v1_2() {
		int result = 0;
		for (MonthlyCharge monthlyCharge : monthly.values()) {
			result += monthlyCharge.v1_2;
		}
		return result;
	}
	
	public int getTotal_v2() {
		int result = 0;
		for (MonthlyCharge monthlyCharge : monthly.values()) {
			result += monthlyCharge.v2;
		}
		return result;
	}
	
}
