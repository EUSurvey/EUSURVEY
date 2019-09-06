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
    	logger.debug("MyUserAgentCallback.resolveAndOpenStream TRY TO GET STREAM FOR URI " + uri);
    	if (uri != null && (uri.startsWith(sessionService.getPdfServerPrefix() + "/graphics/") || (!uri.startsWith(sessionService.getPdfServerPrefix()) && !uri.startsWith(sessionService.getContextPath() + "/"))))
    	{   	
    		logger.debug("MyUserAgentCallback.resolveAndOpenStream TRY TO GET STREAM TO BE RESOLVED FOR URI " + uri);
	    	sessionService.initializeProxy();
	    	
	        java.io.InputStream is = null;
	        uri = resolveURI(uri);
	        logger.debug("MyUserAgentCallback.resolveAndOpenStream TRY TO GET STREAM FOR URI AFTER RESOLVEURI  " + uri);
	        try {
	            URL url = new URL(uri);		            
	            URLConnection uc = url.openConnection();
	            logger.debug("MyUserAgentCallback.resolveAndOpenStream OPENCONNECTION DONE AND BEFORE GETINPUTSTREAM " );	            
	            is = uc.getInputStream();
	            logger.debug("MyUserAgentCallback.resolveAndOpenStream AFTER GETINPUTSTREAM " );
	        }
	        catch (Exception e) {
	        	logger.error("bad URL given: " + uri, e);
	        }
	        	        
	        if (is !=null){
	        	logger.error("MyUserAgentCallback.resolveAndOpenStream RETURN WITH IS  FOR URI " + uri  );	
	        }
	        return is;
	        
	    } else {	    		
	    	logger.debug("MyUserAgentCallback.resolveAndOpenStream super resolver called FOR URI " + uri);
	    	return super.resolveAndOpenStream(uri);
	    }       
    }

}