package com.ec.survey.model.survey.dashboard;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ec.survey.tools.JsonDateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class EndDates {
	
	private Map<Date, List<String>> endDates = new TreeMap<Date, List<String>>();
	
	@JsonIgnore
	public Map<Date, List<String>> getEndDates() {
		return endDates;
	}
	public void setEndDates(Map<Date, List<String>> endDates) {
		this.endDates = endDates;
	}
	
	@JsonSerialize(contentUsing = JsonDateSerializer.class)
	public Collection<Date> getDays ()
	{
		return endDates.keySet();
	}
	
	public Collection<List<String>> getSurveyNames ()
	{
		return endDates.values();
	}	
}
