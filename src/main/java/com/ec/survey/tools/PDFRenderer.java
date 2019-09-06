package com.ec.survey.tools;

import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.FSEntityResolver;

import com.ec.survey.service.SessionService;
import com.lowagie.text.pdf.BaseFont;

public class PDFRenderer {
	
	private ITextRenderer renderer;
	protected static final Logger logger = Logger.getLogger(PDFRenderer.class);
	private SessionService sessionService;
	
	public PDFRenderer(SessionService sessionService) {
		renderer = new ITextRenderer();
		logger.debug("PDFRender constructor is called  calling MyUserAgentCallback ");
		MyUserAgentCallback uac = new MyUserAgentCallback(renderer.getOutputDevice(),sessionService);
		uac.setSharedContext(renderer.getSharedContext());
		renderer.getSharedContext().setUserAgentCallback(uac);
		
		this.sessionService = sessionService;
		
		try {
        	           
			renderer.getFontResolver().addFont("/Fonts/DejaVuSans.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
            
			renderer.getFontResolver().addFont("/Fonts/DejaVuSans-Bold.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
            
			renderer.getFontResolver().addFont("/Fonts/DejaVuSans-Oblique.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
            
			renderer.getFontResolver().addFont("/Fonts/DejaVuSans-BoldOblique.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
                      
			renderer.getFontResolver().addFont("/Fonts/FreeSans.ttf",
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
            );
			renderer.getFontResolver().addFont ("/Fonts/FreeSansBold.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
			renderer.getFontResolver().addFont ("/Fonts/FreeSansBoldOblique.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
			renderer.getFontResolver().addFont ("/Fonts/FreeSansOblique.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
            
			renderer.getFontResolver().addFont("/Fonts/FreeMono.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
			renderer.getFontResolver().addFont ("/Fonts/FreeMonoBold.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
			renderer.getFontResolver().addFont ("/Fonts/FreeMonoBoldOblique.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
			renderer.getFontResolver().addFont ("/Fonts/FreeMonoOblique.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
            
			renderer.getFontResolver().addFont("/Fonts/FreeSerif.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
			renderer.getFontResolver().addFont ("/Fonts/FreeSerifBold.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
			renderer.getFontResolver().addFont ("/Fonts/FreeSerifBoldItalic.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
			renderer.getFontResolver().addFont ("/Fonts/FreeSerifItalic.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
                );
            
            } catch (Exception e)
            {
            	logger.error(e.getLocalizedMessage(), e); 
            }
	}
	
	
	public void createPDF(String inputurl, OutputStream os) throws Exception
	{

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			dbf.setXIncludeAware(false);
		    dbf.setExpandEntityReferences(false);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			builder.setEntityResolver(FSEntityResolver.instance());
			Document doc = builder.parse(inputurl);			
			
			MetaDataCreationListener mcl = new MetaDataCreationListener();
			mcl.parseMetaTags( doc );

			renderer.setListener( mcl );
			renderer.setDocument(doc, sessionService.getPdfServerPrefix());
			renderer.layout();
			renderer.createPDF(os);

		} catch (Exception e) {
			logger.error("PDFRender.createPDF Error " + e);
			throw e;
		}
				
	}
	
}
