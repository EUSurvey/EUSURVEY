package com.ec.survey.tools;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.StringReader;

public class XHTMLValidator {
	
	private static final Logger logger = Logger.getLogger(XHTMLValidator.class);
		
	public static DocumentBuilder getBuilder()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);			
		
		DocumentBuilder builder = null;
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			builder = factory.newDocumentBuilder();
			
			ErrorHandler myErrorHandler = new ErrorHandler()
			{
			    public void fatalError(SAXParseException exception)
			    throws SAXException
			    {
			        
			    }
			    
			    public void error(SAXParseException exception)
			    throws SAXException
			    {
			        
			    }
	
			    public void warning(SAXParseException exception)
			    throws SAXException
			    {
			        
			    }
			};
			
			builder.setErrorHandler(myErrorHandler);
		
		} catch (ParserConfigurationException e) {
			//ignore
		}
		
		return builder;
	}
	
	public static boolean validate(String label, ServletContext servletContext, DocumentBuilder builder) {
		try {
		
			if (builder == null) builder = getBuilder();
			
			final InputStream is = servletContext.getResourceAsStream("/WEB-INF/Content/xhtml1-strict.dtd");
			final InputStream is2 = servletContext.getResourceAsStream("/WEB-INF/Content/xhtml-lat1.ent");
			final InputStream is3 = servletContext.getResourceAsStream("/WEB-INF/Content/xhtml-special.ent");
			final InputStream is4 = servletContext.getResourceAsStream("/WEB-INF/Content/xhtml-symbol.ent");
			
			builder.setEntityResolver((publicId, systemId) -> {
                if (systemId.contains("xhtml11.dtd")) {
                    return new InputSource(is);
                } else if (systemId.contains("xhtml-lat1.ent")) {
                      return new InputSource(is2);
                } else if (systemId.contains("xhtml-special.ent")) {
                      return new InputSource(is3);
                } else if (systemId.contains("xhtml-symbol.ent")) {
                      return new InputSource(is4);
                } else {
                    return null;
                }
            });
			
			String xhtml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
			"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">" +
			"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">" +
			"	<head>"+
			"		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
			"		<title>XHTML 1.0 Strict Example</title>" +
			"	</head>" +
			"	<body><div>" + label + "</div></body></html>";
			
			builder.parse(new InputSource(new StringReader(xhtml)));
			builder.reset();
			return true;
		} catch (Exception e1) {
			logger.info(e1.getLocalizedMessage(), e1);
		}
		
		return false;
	}
}