package com.ec.survey.tools;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class ConversionTools {
	
	public static final String DateFormat = "dd/MM/yyyy";
	public static final String AltDateFormat = "yyyy-MM-dd";
	public static final String SmallDateFormat = "dd/MM/yy";
	public static final String AltSmallDateFormat = "yy-MM-dd";
	public static final String DateTimeFormat = "dd/MM/yyyy HH:mm:ss";
	public static final String DateTimeFormatSmall = "dd/MM/yyyy HH:mm";
	public static final String DateFormatReplacedMarkup = "yyyy-MM-dd";
	public static final String DateTimeFormatWebservice = "yyyy-MM-dd_HH-mm-ss";
	public static final String DateTimeFormatSQL = "yyyy-MM-dd HH:mm:ss";
	public static final String DateTimeFormatJS = "yyyy, MM, dd, HH, mm, ss";
	public static final String TimeFormat = "HH:mm:ss";
		
	public static int getValue(Object num)
	{
		if (num == null) return 0;
		
		if (num instanceof Integer) return (Integer) num;
		if (num instanceof Byte) return ((Byte) num).intValue();
		if (num instanceof Long) return ((Long) num).intValue();
		if (num instanceof BigInteger) return ((BigInteger) num).intValue();	
		if (num instanceof BigDecimal) return ((BigDecimal) num).intValue();	
		if (num instanceof String) return Integer.parseInt((String) num);
		
		try {
			num = ((Object[])num)[0];
			if (num instanceof Integer) return (Integer) num;
			if (num instanceof Byte) return ((Byte) num).intValue();
			if (num instanceof BigInteger) return ((BigInteger) num).intValue();	
			if (num instanceof BigDecimal) return ((BigDecimal) num).intValue();
			if (num instanceof String) return Integer.parseInt((String) num);	
		} catch (Exception e)
		{
			//ignore
		}
		
		return 0;
	}
	
	public static Date getDate(String input)
	{
		if (input == null) return null;
		Date result = null;
		try
		{
			if (input.length() == 19)
			{
				result = Tools.parseDateString(input, DateTimeFormatSQL);
			} else if (input.length() == 8)
			{
				if (input.charAt(2) == '-') {
					result = Tools.parseDateString(input, AltSmallDateFormat);
				} else {
					result = Tools.parseDateString(input, SmallDateFormat);
				}
			} else if (input.length() == 10) {
				if (input.charAt(4) == '-') {
					result = Tools.parseDateString(input, AltDateFormat);
				} else {
					result = Tools.parseDateString(input, DateFormat);
				}
			}
		} catch (Exception e)
		{
			return null;
		}
		
		if (result != null)
		{
			//remove invalid dates that are not allowed by MySQL
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 9999);
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
			cal.set(Calendar.DAY_OF_MONTH, 1);			
			Date max = cal.getTime();			
					
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 1000);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);	
			Date min =cal.getTime();
			
			if (result.after(max) || result.before(min)) {
				return null;
			}
		}
		
		return result;
	}
		
	public static String getFullString(Date input) {
		if (input == null) return null;
		try
		{
			return Tools.formatDate(input, DateTimeFormat);
		} catch (Exception e)
		{
			return null;
		}
	}
	
	public static String getFullString4Webservice(Date input) {
		if (input == null) return null;
		try
		{
			return Tools.formatDate(input, DateTimeFormatWebservice);
		} catch (Exception e)
		{
			return null;
		}
	}
	
	public static String getFullStringSmall(Date input) {
		if (input == null) return null;
		try
		{
			return Tools.formatDate(input, DateTimeFormatSmall);
		} catch (Exception e)
		{
			return null;
		}
	}
	
	public static Integer getInt(String input) {
	    return getInt(input, null);
	}
	
	public static Integer getInt(String input, Integer defaultValue) {
	    try {
		    return new Integer(input);
	    } catch (NumberFormatException e) {
		    return defaultValue;
	    }
	}
	
	public static Double getDouble(String input) {
	    try {
		    return new Double(input);
	    } catch (NumberFormatException e) {
		    return null;
	    }
	}
	
	public static String removeHTML(String htmlString)
    {
		return removeHTML(htmlString, false);
    }
	
	public static String escape(String input)
	{
		return StringEscapeUtils.escapeXml11(input);
	}
	
	public static String removeHTMLNoEscape(String htmlString)
	{
		return removeHTMLNoEscape(htmlString, false);
	}
	
	public static String removeHTMLNoEscape(String htmlString, boolean removeInvalidHtmlEntities)
	{
		if (htmlString == null) return "";
		if (removeInvalidHtmlEntities ) {
			htmlString = removeInvalidHtmlEntities(htmlString);
		}
		return Jsoup.parse(htmlString).text().trim();
	}
	
	public static String removeHTML(String htmlString, boolean escape)
    {
		if (htmlString == null) return "";
		
		String result = "";
		
		if (escape) 
		{
			result = HtmlUtils.htmlEscape(Jsoup.parse(htmlString).text().trim());
			String nbsps = "&nbsp;";
			
			while (result.startsWith(nbsps))
			{
				result = result.substring(6).trim();
			}
		} else {
			char nbsp = (char)160;
			String nbsps = Character.toString(nbsp);
			
			while (result.startsWith(nbsps))
			{
				result = result.substring(1).trim();
			}
			
			result = StringEscapeUtils.escapeXml11(Jsoup.parse(htmlString).text().trim());
		}
		
		return result;
    }

	public static String removeInvalidHtmlEntities(String text) {
		
		if (text == null || text.length() == 0)
		{
			return "";
		}
		
		text = HtmlUtils.htmlUnescape(text);
		
		String xml10pattern = "[^"
                + "\u0009\r\n"
                + "\u0020-\uD7FF"
                + "\uE000-\uFFFD"
                + "\ud800\udc00-\udbff\udfff"
                + "]";
		
		text = text.replaceAll(xml10pattern, "");
		
		return text;
	}
	
	public static Map<Integer,String> mapFromXML(String xml) {
		XStream xstream = new XStream();
        NamedMapConverter namedMapConverter = new NamedMapConverter(xstream.getMapper(),"translation","id",Integer.class,"text",String.class);
        xstream.registerConverter( namedMapConverter);
        @SuppressWarnings("unchecked")
		Map<Integer, String> result = (Map<Integer, String>) xstream.fromXML(xml);
		return result;
	}
	
	public static String mapToXML(Map<Integer,String> map) {
		XStream xstream = new XStream();
        NamedMapConverter namedMapConverter = new NamedMapConverter(xstream.getMapper(),"translation","id",Integer.class,"text",String.class);
        xstream.registerConverter( namedMapConverter);
        return xstream.toXML(map);
	}
	
	public static Map<Integer,String> mapFromHTML(String html) {
		Map<Integer,String> result = new HashMap<>(128);
		Document doc = Jsoup.parse(html);
		Elements articles = doc.getElementsByTag("article");
		for (Element article: articles) {
			 
			String id = article.attr("id");
			if (NumberUtils.isDigits(id)) {
				result.put(Integer.valueOf(id), article.html());
			}
		}
		return result;
	}
	
	public static String mapToHTML(Map<Integer,String> map) {
		StringBuilder result = new StringBuilder();
		result.append("<!DOCTYPE html>");
		result.append("<html>");
		result.append("<head>");
		result.append("</head>");
		result.append("<body>");
		
		for ( Entry<Integer, String> entry : map.entrySet()) {
			result.append("<article id=\"");
			result.append(entry.getKey().toString());
			result.append("\">");
			result.append(entry.getValue());
			result.append("</article>");
		}
		result.append("</body>");
		result.append("</html>");
        return result.toString();
	}

	public static String getStringForBytes(long byteslong) {
		double bytes = byteslong;
		DecimalFormat df = new DecimalFormat("###.##");
		
		if (bytes < 1024) return bytes + " B";		
		bytes = bytes / 1024;
		if (bytes < 1024) return df.format(bytes) + " KB";
		bytes = bytes / 1024;
		if (bytes < 1024) return df.format(bytes) + " MB";
		bytes = bytes / 1024;
		return df.format(bytes) + " GB";
	}

}
