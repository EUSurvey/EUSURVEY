package com.ec.survey.tools;

import com.ec.survey.service.SessionService;

public class PDFRendererPoolFactory {
	  protected static PDFRendererPool pdfPool = null;

	  public static PDFRendererPool getInstance(int max, SessionService sessionService) {
	    if (pdfPool != null) {
	      return pdfPool;
	    }
	    pdfPool = new PDFRendererPool(max, sessionService);
	    return pdfPool;
	  }
	}
