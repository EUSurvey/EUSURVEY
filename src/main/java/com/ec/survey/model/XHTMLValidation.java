package com.ec.survey.model;

import java.io.Serializable;

public class XHTMLValidation implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean invalid;
	private String text;
	
	public XHTMLValidation()
	{}

	public XHTMLValidation(String text, boolean invalid) {
		this.invalid = invalid;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public boolean getInvalid() {
		return invalid;
	}
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
	
}
