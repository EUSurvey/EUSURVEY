package com.ec.survey.tools;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Ucs2Utf8 {

    public static String unconvert(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();

        char[] transl = s.toCharArray();
        byte[] trans2 = new byte[transl.length];
        for (int j = 0; j < transl.length; j++)
        {
            trans2[j] = (byte) transl[j];
        }

        ByteArrayInputStream inp = new ByteArrayInputStream(trans2);
        try {
            InputStreamReader isr = new InputStreamReader(inp, "UTF8");
            Reader in = new BufferedReader(isr);
            int ch;
            while ((ch = in.read()) > -1) {
            	if (ch == 65533) return s;
            	buffer.append((char) ch);
            }
            in.close();
        } catch (IOException ex) {
            //ignore
        }
        return buffer.toString();
    }   
    
    public static Map<String,String[]> requestToHashMap(HttpServletRequest r) {
    	return requestToHashMap(r, false);
    }
    
    public static Map<String,String[]> requestToHashMap(HttpServletRequest r, boolean escape) {
    	String re = "\\p{C}"; 
    	
        HashMap<String,String[]> result = new LinkedHashMap<>();
        @SuppressWarnings("rawtypes")
		Enumeration e = r.getParameterNames();
        while (e.hasMoreElements()) {
            String param = (String) e.nextElement();
            String[] values = r.getParameterValues(param);
            
            
            if (escape)
            {
	            for (int i = 0; i < values.length; i++) {
	                //escape html
	            	if (values[i] != null)
	            	{
	            		values[i] = values[i].replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
	            	}
	            }
            }
            
            if (r.getContentType() != null && !r.getContentType().contains("form-data") && !r.getCharacterEncoding().equalsIgnoreCase("UTF-8"))
            {
	            for (int i = 0; i < values.length; i++) {
	                //convert and replace invalid characters
	            	String val = Ucs2Utf8.unconvert(values[i]).replaceAll(re, "");
	                values[i] = val;
	            }
            }
            result.put(param, values);
        }
        return result;
    }
  
}