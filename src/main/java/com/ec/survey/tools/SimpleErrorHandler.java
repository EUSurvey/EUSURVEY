package com.ec.survey.tools;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.List;

public class SimpleErrorHandler implements ErrorHandler {
	
	private List<SAXParseException> exceptions = new ArrayList<>();
	
    public void warning(SAXParseException e) throws SAXException {
        exceptions.add(e);
    }

    public void error(SAXParseException e) throws SAXException {
    	exceptions.add(e);
    }

    public void fatalError(SAXParseException e) throws SAXException {
    	exceptions.add(e);
    }
    
    public List<SAXParseException> getExceptions()
    {
    	return exceptions;
    }
}
