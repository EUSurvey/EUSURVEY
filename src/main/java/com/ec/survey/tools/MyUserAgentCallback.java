package com.ec.survey.tools;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;

import com.ec.survey.service.SessionService;

public class MyUserAgentCallback extends ITextUserAgent
{
	private SessionService sessionService;
	protected static final Logger logger = Logger.getLogger(MyUserAgentCallback.class);
	
    public MyUserAgentCallback(ITextOutputDevice outputDevice, SessionService sessionService)
    {
        super(outputDevice);
        this.sessionService = sessionService;
    }

    @Override
    protected InputStream resolveAndOpenStream(String uri) 
    {    	
    	if (uri != null && (uri.startsWith(sessionService.getPdfServerPrefix() + "/graphics/") || (!uri.startsWith(sessionService.getPdfServerPrefix()) && !uri.startsWith(sessionService.getContextPath() + Constants.PATH_DELIMITER))))
    	{   	
        	sessionService.initializeProxy();
	    	
	        java.io.InputStream is = null;
	        uri = resolveURI(uri);
	        try {
	            URL url = new URL(uri);		            
	            URLConnection uc = url.openConnection();
	            is = uc.getInputStream();
	        }
	        catch (Exception e) {
	        	logger.error("bad URL given: " + uri, e);
	        }
	        return is;
	        
	    } else {	    		
	    	return super.resolveAndOpenStream(uri);
	    }       
    }

}