package com.ec.survey.model;

import java.util.ArrayList;
import java.util.List;

public class DelphiGraphData {
	private List<Object> data = new ArrayList<>();
	private List<String> labels = new ArrayList<>();
	
	public List<Object> getData()
	{
		return data;
	}
	public void setData(List<Object> data)
	{
		this.data = data;
	}
	
	public List<String> getLabels()
	{
		return labels;
	}
	public void setLabels(List<String> labels)
	{
		this.labels = labels;
	}
	
	public void addPoint(Object value, String label)
	{
		data.add(value);
		labels.add(label);
	}
}
