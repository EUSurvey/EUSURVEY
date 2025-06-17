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
		MyUserAgentCallback uac = new MyUserAgentCallback(renderer.getOutputDevice(),sessionService);
		uac.setSharedContext(renderer.getSharedContext());
		renderer.getSharedContext().setUserAgentCallback(uac);
		
		this.sessionService = sessionService;
		
		try {

			String[] fonts = new String[] {
					"/Fonts/DejaVuSans.ttf",
					"/Fonts/DejaVuSans-Bold.ttf",
					"/Fonts/DejaVuSans-Oblique.ttf",
					"/Fonts/DejaVuSans-BoldOblique.ttf",
					"/Fonts/FreeSans.ttf",
					"/Fonts/FreeSansBold.ttf",
					"/Fonts/FreeSansBoldOblique.ttf",
					"/Fonts/FreeSansOblique.ttf",
					"/Fonts/FreeMono.ttf",
					"/Fonts/FreeMonoBold.ttf",
					"/Fonts/FreeMonoBoldOblique.ttf",
					"/Fonts/FreeMonoOblique.ttf",
					"/Fonts/FreeSerif.ttf",
					"/Fonts/FreeSerifBold.ttf",
					"/Fonts/FreeSerifBoldItalic.ttf",
					"/Fonts/FreeSerifItalic.ttf",
			};
        	           

			for (var fontPath: fonts) {
				renderer.getFontResolver().addFont(
						fontPath,
						BaseFont.IDENTITY_H,
						BaseFont.EMBEDDED
				);
			}

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
