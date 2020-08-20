package com.ec.survey.tools;

import com.ec.survey.service.SessionService;

public class PDFRendererPool extends ObjectPool<PDFRenderer> {

	private SessionService sessionService;

	public PDFRendererPool(int max, SessionService sessionService) {
		super(max);
		this.sessionService = sessionService;
	}

	@Override
	protected PDFRenderer create() {
		try {
			return new PDFRenderer(sessionService);
		} catch (Exception e) {
			return (null);
		}
	}

	@Override
	public void expire(PDFRenderer p) {
		// nothing to do
	}

	@Override
	public boolean validate(PDFRenderer p) {
		return true;
	}
}
