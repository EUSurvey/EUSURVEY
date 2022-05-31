package com.ec.survey.model;

import java.util.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.ec.survey.tools.ConversionTools;

public class FileFilter implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private String surveyUid;
	private String surveyShortname;
	private String filePath;
	private String fileName;
	private String fileUid;
	private Set<String> fileTypes;
	private Set<String> fileExtensions;
	private Date createdFrom;
	private Date createdTo;
	private String filterApplied;
	private String sortKey = "";
	private String sortOrder = "DESC";
	private int userId;
		
	private boolean systemExports;
	private boolean surveyUploads;
	private boolean temporaryFiles;
	private boolean archivedSurveys;
	private boolean surveyFiles;
	
	private boolean onlyUnreferenced;
	private boolean searchInFileSystem = true;
	
	private int page;
	private int itemsPerPage = 50;
	
	@DateTimeFormat(pattern=ConversionTools.DateFormat)
	public Date getCreatedFrom() {
		return createdFrom;
	}
	public void setCreatedFrom(Date createdFrom) {
		this.createdFrom = createdFrom;
	}
	
	@DateTimeFormat(pattern=ConversionTools.DateFormat)
	public Date getCreatedTo() {
		return createdTo;
	}
	public void setCreatedTo(Date createdTo) {
		this.createdTo = createdTo;
	}
	
	public String getSortKey() {
		return sortKey;
	}
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
	
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		if (sortOrder.equalsIgnoreCase("DESC") || sortOrder.equalsIgnoreCase("ASC"))
		{
			this.sortOrder = sortOrder;
		} 
	}
	
	public Set<String> getFileTypes() {
		return fileTypes;
	}
	public void setFileTypes(Set<String> fileTypes) {
		this.fileTypes = fileTypes;
	}
	
	public Set<String> getFileExtensions() {
		return fileExtensions;
	}
	public void setFileExtensions(Set<String> fileExtensions) {
		this.fileExtensions = fileExtensions;
	}
	
	public String getSurveyUid() {
		return surveyUid;
	}
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}	
	
	public String getSurveyShortname() {
		return surveyShortname;
	}
	public void setSurveyShortname(String surveyShortname) {
		this.surveyShortname = surveyShortname;
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileUid() {
		return fileUid;
	}
	public void setFileUid(String fileUid) {
		this.fileUid = fileUid;
	}
	
	public String getFilterApplied() {
		return filterApplied;
	}
	public void setFilterApplied(String filterApplied) {
		this.filterApplied = filterApplied;
	}
	
	public boolean isSystemExports() {
		return systemExports;
	}
	public void setSystemExports(boolean systemExports) {
		this.systemExports = systemExports;
	}
	
	public boolean isSurveyUploads() {
		return surveyUploads;
	}
	public void setSurveyUploads(boolean surveyUploads) {
		this.surveyUploads = surveyUploads;
	}
	
	public boolean isTemporaryFiles() {
		return temporaryFiles;
	}
	public void setTemporaryFiles(boolean temporaryFiles) {
		this.temporaryFiles = temporaryFiles;
	}
	
	public boolean isArchivedSurveys() {
		return archivedSurveys;
	}
	public void setArchivedSurveys(boolean archivedSurveys) {
		this.archivedSurveys = archivedSurveys;
	}
	
	public boolean isVisible(String type) {
		if (fileTypes == null || fileTypes.isEmpty()) return true;
		return fileTypes.contains(type);
	}
	
	public boolean isValidExtension(String extension) {
		if (fileExtensions == null || fileExtensions.isEmpty()) return true;
		return fileExtensions.contains(extension);
	}
	
	public boolean isOnlyUnreferenced() {
		return onlyUnreferenced;
	}
	public void setOnlyUnreferenced(boolean onlyUnreferenced) {
		this.onlyUnreferenced = onlyUnreferenced;
	}
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	
	public int getItemsPerPage() {
		return itemsPerPage;
	}
	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
	
	public boolean isSurveyFiles() {
		return surveyFiles;
	}
	public void setSurveyFiles(boolean surveyFiles) {
		this.surveyFiles = surveyFiles;
	}
	
	public boolean isSearchInFileSystem() {
		return searchInFileSystem;
	}
	public void setSearchInFileSystem(boolean searchInFileSystem) {
		this.searchInFileSystem = searchInFileSystem;
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
