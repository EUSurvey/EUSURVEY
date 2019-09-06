package com.ec.survey.model.machinetranslation;


public class RequestTranslationMessage {
	

	
	
	
	private String departmentNumber;
	private String documentToTranslate; 
	private String domains; 
	private String externalReference; 
	private String institution;
	private String originalFileName; 
	private String outputFormat; 
	private int priority;
	private String requestType;
	private String sourceLanguage;
	private String targetLanguage;
	private String targetTranslationPath;
	private String textToTranslate;
	private String username;
	
	
	
	public RequestTranslationMessage()
	{
		departmentNumber ="";
		institution="";
		originalFileName="";
		outputFormat ="";
		documentToTranslate ="";
		
		requestType="txt";  
		outputFormat="";
		domains = "all";
		priority =1;
		targetTranslationPath = "none";
		
	}
	public String getDepartmentNumber() {
		return departmentNumber;
	}
	public void setDepartmentNumber(String departmentNumber) {
		this.departmentNumber = departmentNumber;
	}
	public String getDocumentToTranslate() {
		return documentToTranslate;
	}
	public void setDocumentToTranslate(String documentToTranslate) {
		this.documentToTranslate = documentToTranslate;
	}
	public String getDomains() {
		return domains;
	}
	public void setDomains(String domains) {
		this.domains = domains;
	}
	public String getExternalReference() {
		return externalReference;
	}
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}
	
	public String getInstitution() {
		return institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public String getOutputFormat() {
		return outputFormat;
	}
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}
	public int getPriority() {
		return priority;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getSourceLanguage() {
		return sourceLanguage;
	}
	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage.toUpperCase();
	}
	public String getTargetLanguage() {
		return targetLanguage;
	}
	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage.toUpperCase();
	}
	public String getTargetTranslationPath() {
		return targetTranslationPath;
	}
	public void setTargetTranslationPath(String targetTranslationPath) {
		this.targetTranslationPath = targetTranslationPath;
	}
	public String getTextToTranslate() {
		return textToTranslate;
	}
	public void setTextToTranslate(String textToTranslate) {
		this.textToTranslate = textToTranslate;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

}
