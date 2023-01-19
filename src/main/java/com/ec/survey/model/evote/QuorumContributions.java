package com.ec.survey.model.evote;

import com.ec.survey.tools.JsonDateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.*;

public class QuorumContributions {
	
	private int surveyId;
	private int numberOfContributions = 0;
	private Map<Date, Integer> answersPerDay = new TreeMap<>();
	
	public int getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}
	private int[] contributionStates;
	
	private long voters;
	
	@JsonIgnore
	public Map<Date, Integer> getAnswersPerDay() {
		return answersPerDay;
	}
	public void setAnswersPerTimeUnit(Map<Date, Integer> answersPerDay, String hourOrDate) {

		Date[] arrlistOfDates = answersPerDay.keySet().toArray(new Date[0]);
		
		if (arrlistOfDates.length == 1)
		{
			 Calendar cal = Calendar.getInstance();
			 cal.setTime(arrlistOfDates[0]);
			 cal.add(Calendar.DATE, -1);
			 this.answersPerDay.put(cal.getTime(), 0);	
		}
		
		if (arrlistOfDates.length < 2) return;

		// calculate the total contributions until each day/hour
		List<Date> resultingDates;
		if(hourOrDate.equalsIgnoreCase("quorumHours")){
			resultingDates = generateHourListBetween(arrlistOfDates[0], arrlistOfDates[arrlistOfDates.length-1]);
		} else {
			resultingDates = generateDateListBetween(arrlistOfDates[0], arrlistOfDates[arrlistOfDates.length-1]);
		}

		this.answersPerDay = answersPerDay;
		
		for (Date d : resultingDates){
			if (!answersPerDay.containsKey(d)){
				answersPerDay.put(d, 0);
			}
		}
		
		// replace 0 votes for a day by sum of previous votes
		int previous = 0;
		for (Date d : answersPerDay.keySet()) {
			int value = answersPerDay.get(d);
			if (value == 0) {
				answersPerDay.put(d, previous);
			} else {
				previous = value;
			}
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
		cal.add(Calendar.DATE, 1);

	    do
	    {
	        resultList.add(cal.getTime());
	        cal.add(Calendar.DATE, 1);
	    }
	    while(cal.getTime().compareTo(endDate) <= 0); //While date is smaller or equal to endDate

	    return resultList;
	}

	@JsonIgnore
	private List<Date> generateHourListBetween(Date startDate, Date endDate)
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
		cal.add(Calendar.HOUR, 1);

		do
		{
			resultList.add(cal.getTime());
			cal.add(Calendar.HOUR, 1);
		}
		while(cal.getTime().compareTo(endDate) <= 0); //While date is smaller or equal to endDate

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

	public int[] getContributionStates() {
		return contributionStates;
	}
	public void setContributionStates(int[] contributionStates) {
		this.contributionStates = contributionStates;
	}
	
	public long getVoters() {
		return voters;
	}
	public void setVoters(long voters) {
		this.voters = voters;
	}

	public int getNumberOfContributions() {
		return numberOfContributions;
	}

	public void setNumberOfContributions(int numberOfContributions) {
		this.numberOfContributions = numberOfContributions;
	}
}
