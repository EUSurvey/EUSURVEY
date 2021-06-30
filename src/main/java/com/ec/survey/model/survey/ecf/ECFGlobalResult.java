package com.ec.survey.model.survey.ecf;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the results for all answerers, and for a specific profile (possibly null)
 * If this is the case, do not display the individualResults gaps
 */
public class ECFGlobalResult {

    @JsonProperty("profileComparisonUid")
    private String profileComparisonUid;
    
    @JsonProperty("profileFilterUid")
    private String profileFilterUid;
    
    @JsonProperty("totalResults")
    private ECFGlobalTotalResult totalResults = new ECFGlobalTotalResult();
    
    @JsonProperty("individualResults")
    private List<ECFGlobalCompetencyResult> individualResults = new ArrayList<>();
    
    @JsonProperty("pageNumber")
    private Integer pageNumber;
    
    @JsonProperty("pageSize")
    private Integer pageSize;
    
    @JsonProperty("numberOfPages")
    private Integer numberOfPages;
    
    @JsonProperty("numberOfResults")
    private Integer numberOfResults;
    
	public String getProfileComparisonUid() {
		return profileComparisonUid;
	}

	public void setProfileComparisonUid(String profileComparisonUid) {
		this.profileComparisonUid = profileComparisonUid;
	}

	public String getProfileFilterUid() {
		return profileFilterUid;
	}

	public void setProfileFilterUid(String profileFilterUid) {
		this.profileFilterUid = profileFilterUid;
	}
	
	public ECFGlobalTotalResult getTotalResults() {
		return totalResults;
	}

	public void setTotalResult(ECFGlobalTotalResult totalResults) {
		this.totalResults = totalResults;
	}
	
	public List<ECFGlobalCompetencyResult> getIndividualResults() {
		return individualResults;
	}

	public void setIndividualResults(List<ECFGlobalCompetencyResult> individualResults) {
		this.individualResults = individualResults;
	}
	
	public void addIndividualResults(ECFGlobalCompetencyResult individualResult) {
		this.individualResults.add(individualResult);
	}
	

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}
	
	public Integer getNumberOfResults() {
		return numberOfResults;
	}

	public void setNumberOfResults(Integer numberOfResults) {
		this.numberOfResults = numberOfResults;
	}

	@Override
	public String toString() {
		return "ECFGlobalResult [profileComparisonUid=" + profileComparisonUid + ", profileFilterUid="
				+ profileFilterUid + ", totalResults=" + totalResults + ", individualResults=" + individualResults
				+ ", pageNumber=" + pageNumber + ", pageSize=" + pageSize + ", numberOfPages=" + numberOfPages
				+ ", numberOfResults=" + numberOfResults + "]";
	}


	
}