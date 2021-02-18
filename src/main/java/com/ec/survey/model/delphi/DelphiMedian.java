package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.List;

public class DelphiMedian {
	private List<String> medianUids;
	private double median;
	private boolean maxDistanceExceeded;	
	
	public boolean isMaxDistanceExceeded() {
		return maxDistanceExceeded;
	}
	public void setMaxDistanceExceeded(boolean maxDistanceExceeded) {
		this.maxDistanceExceeded = maxDistanceExceeded;
	}
	
	public List<String> getMedianUids() {
		return medianUids;
	}
	public void setMedianUids(List<String> medianUids) {
		this.medianUids = medianUids;
	}
	
	public double getMedian() {
		return median;
	}
	public void setMedian(double median) {
		this.median = median;
	}
	
	public DelphiMedian() {
		medianUids = new ArrayList<>();
	}
}
