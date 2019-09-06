package com.ec.survey.tools;

import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.pdf.DefaultPDFCreationListener;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.util.Enumeration;
import java.util.Properties;

public class MetaDataCreationListener extends DefaultPDFCreationListener {

    Properties headMetaTags = new Properties();

    public void parseMetaTags(Document sourceXHTML) {
        //Could also use XPATH, I suppose.
        Element headTag = (Element) sourceXHTML.getDocumentElement()
                                        .getElementsByTagName("head")
                                        .item(0);
        NodeList metaTags = headTag.getElementsByTagName("meta");

        for (int i = 0; i < metaTags.getLength(); ++i) {
            Element thisNode = (Element) metaTags.item(i);
            String name = thisNode.getAttribute("name");
            String content = thisNode.getAttribute("content");
            if (name.length() != 0 && content.length() != 0) {
                this.headMetaTags.setProperty(name, content);
            }
        }

        //No title meta tag given --> take it from title tag
        if ( this.headMetaTags.getProperty("title") == null ) {
            Element titleTag = (Element)headTag.getElementsByTagName("title").item(0);           
            this.headMetaTags.setProperty("title", titleTag.getTextContent() + " X");
        }

    }
    
    @Override
    public void onClose(ITextRenderer iTextRenderer) 
    {
    	@SuppressWarnings("rawtypes")
		Enumeration e = this.headMetaTags.propertyNames();
        
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            PdfString val = new PdfString(this.headMetaTags.getProperty(key), PdfObject.TEXT_UNICODE);
            iTextRenderer.getWriter().setViewerPreferences(PdfWriter.DisplayDocTitle);
            switch (key) {
                case "title":
                    iTextRenderer.getWriter().getInfo().put(PdfName.TITLE, val);
                    break;
                case "author":
                    iTextRenderer.getWriter().getInfo().put(PdfName.AUTHOR, val);
                    break;
                case "subject":
                    iTextRenderer.getWriter().getInfo().put(PdfName.SUBJECT, val);
                    break;
                case "creator":
                    iTextRenderer.getWriter().getInfo().put(PdfName.CREATOR, val);
                    break;
                case "description":
                    iTextRenderer.getWriter().getInfo().put(PdfName.DESC, val);
                    break;
                case "keywords":
                    iTextRenderer.getWriter().getInfo().put(PdfName.KEYWORDS, val);
                    break;
                default:
                /* This line allows for arbitrary meta tags and may not be what we want. */
                    iTextRenderer.getWriter().getInfo().put(new PdfName(key), val);
                    break;
            }
        }
    }
}
