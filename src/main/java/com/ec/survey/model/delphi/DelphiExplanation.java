package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.List;

import com.ec.survey.model.survey.base.File;

public class DelphiExplanation {

	public DelphiExplanation() {
		responseMessage = "";
		text = "";
	}

	public DelphiExplanation(String responseMessage, String text) {
		this.responseMessage = responseMessage;
		this.text = text;
	}

	private String responseMessage = "";
	private String text = "";
	private List<String> fileList = new ArrayList<>();

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public List<String> getFileList() {
		return fileList;
	}

	public void setFileList(List<String> fileList) {
		this.fileList = fileList;
	}

	public void setFileInfoFromFiles(List<File> files) {
		this.fileList.clear();
		for (File file : files) {
			this.fileList.add(file.getName());
		}
	}
}
