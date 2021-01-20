package com.ec.survey.model.delphi;

public class DelphiMedian {
	private String medianUid;
	private boolean maxDistanceExceeded;
	
	public String getMedianUid() {
		return medianUid;
	}
	public void setMedianUid(String medianUid) {
		this.medianUid = medianUid;
	}
	
	public boolean isMaxDistanceExceeded() {
		return maxDistanceExceeded;
	}
	public void setMaxDistanceExceeded(boolean maxDistanceExceeded) {
		this.maxDistanceExceeded = maxDistanceExceeded;
	}
}
