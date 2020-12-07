package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.List;

import com.ec.survey.model.survey.base.File;

public class DelphiExplanation {
	
	public static class FileInfo {
		public String name = "";
		public String uid;
	};
	
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
	private List<FileInfo> fileInfo = new ArrayList<>();

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

	public List<FileInfo> getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(List<FileInfo> fileInfo) {
		this.fileInfo = fileInfo;
	}

	public void setFileInfoFromFiles(List<File> files) {
		this.fileInfo.clear();
		for (File file : files) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.name = file.getName();
			fileInfo.uid = file.getUid();
			this.fileInfo.add(fileInfo);			
		}
	}
}
