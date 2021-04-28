package com.ec.survey.model.survey.ecf;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ECFProfileSummaryResult implements Comparable<ECFProfileSummaryResult>{

	@JsonProperty("profileUid")
	private String profileUid;

	@JsonProperty("profileName")
	private String profileName;

	@JsonProperty("numberOfContributions")
	private Integer numberOfContributions;
	
	@JsonProperty("isSelected")
	private Boolean isSelected;
	
	private Integer orderNumber;
	
	public ECFProfileSummaryResult(Integer orderNumber) {
		super();
		this.orderNumber = orderNumber;
	}

	public String getProfileUid() {
		return profileUid;
	}

	public void setProfileUid(String profileUid) {
		this.profileUid = profileUid;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public Integer getNumberOfContributions() {
		return numberOfContributions;
	}

	public void setNumberOfContributions(Integer numberOfContributions) {
		this.numberOfContributions = numberOfContributions;
	}
	
	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public String toString() {
		return "ECFProfileSummaryResult [profileUid=" + profileUid + ", profileName=" + profileName
				+ ", numberOfContributions=" + numberOfContributions + ", isSelected=" + isSelected + ", orderNumber="
				+ orderNumber + "]";
	}
	
	@Override
	public int compareTo(ECFProfileSummaryResult otherObject) {
		return this.getOrderNumber().compareTo(otherObject.getOrderNumber());
	}
	
}
