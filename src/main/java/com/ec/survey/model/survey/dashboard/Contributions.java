package com.ec.survey.model.survey.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ec.survey.tools.JsonDateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.emory.mathcs.backport.java.util.Arrays;

public class Contributions {
	
	private int surveyId;
	private int surveyIndex;
	private String surveyTitle;
	private Map<Date, Integer> answersPerDay = new TreeMap<>();
	private Map<Integer, String> surveys;
	private int[] contributionStates;
	
	public int getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}
	
	public int getSurveyIndex() {
		return surveyIndex;
	}
	public void setSurveyIndex(int surveyIndex) {
		this.surveyIndex = surveyIndex;
	}
	
	public String getSurveyTitle() {
		return surveyTitle;
	}
	public void setSurveyTitle(String surveyTitle) {
		this.surveyTitle = surveyTitle;
	}
	
	@JsonIgnore
	public Map<Date, Integer> getAnswersPerDay() {
		return answersPerDay;
	}
	public void setAnswersPerDay(Map<Date, Integer> answersPerDay) {
		this.answersPerDay = answersPerDay;
		
		//add 0 for missing days		
		Date[] arrlistOfDates = answersPerDay.keySet().toArray(new Date[0]);
		@SuppressWarnings("unchecked")
		List<Date> listOfDates = Arrays.asList(arrlistOfDates);
		
		if (arrlistOfDates.length == 1)
		{
			 Calendar cal = Calendar.getInstance();
			 cal.setTime(arrlistOfDates[0]);
			 cal.add(Calendar.DATE, -1);
			 this.answersPerDay.put(cal.getTime(), 0);	
		}
		
		if (arrlistOfDates.length < 2) return;
		
		List<Date> resultingDates = generateDateListBetween(arrlistOfDates[0], arrlistOfDates[arrlistOfDates.length-1]);
		resultingDates.removeAll(listOfDates);
		
		for (Date d : resultingDates)
		{
			this.answersPerDay.put(d, 0);	
		}
	}
	
	@JsonIgnore
	private List<Date> generateDateListBetween(Date startDate, Date endDate)
	{
	    //Flip the input if necessary, to prevent infinite loop
	    if(startDate.after(endDate))
	    {
	        Date temp = startDate;
	        startDate = endDate;
	        endDate = temp;
	    }

	    List<Date> resultList = new ArrayList<>();
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(startDate);

	    do
	    {
	        resultList.add(cal.getTime());
	        cal.add(Calendar.DATE, 1);
	    }
	    while(cal.getTime().before(endDate));

	    return resultList;
	}
	
	@JsonSerialize(contentUsing = JsonDateSerializer.class)
	public Collection<Date> getDays ()
	{
		return answersPerDay.keySet();
	}
	
	public Collection<Integer> getAnswers ()
	{
		return answersPerDay.values();
	}
	
	@JsonIgnore
	public Map<Integer, String> getSurveys() {
		return surveys;
	}
	public void setSurveys(Map<Integer, String> surveys) {
		this.surveys = surveys;
	}
	
	public Collection<Integer> getSurveyIds ()
	{
		return surveys == null ? null : surveys.keySet();
	}
	
	public Collection<String> getSurveyTitles ()
	{
		return surveys.values();
	}
	
	public int[] getContributionStates() {
		return contributionStates;
	}
	public void setContributionStates(int[] contributionStates) {
		this.contributionStates = contributionStates;
	}
	
}
