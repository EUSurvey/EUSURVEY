package com.ec.survey.tools;

import com.ec.survey.model.administration.User;
import com.ec.survey.service.LdapService;
import org.apache.log4j.Logger;

import javax.naming.NamingException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EcasHelper {
	
	protected static final Logger logger = Logger.getLogger(EcasHelper.class);
	
	public static String getXmlTagValue(String xml, String tag) {
	    int begin = xml.indexOf("<" + tag + ">") + tag.length() + 2;
	    int end = xml.indexOf("</" + tag + ">");
	    String value = xml.substring(begin, end);
	    value = value.replaceFirst("\\<\\!\\[CDATA\\[","");
	    value = value.replaceFirst("\\]\\]\\>","");
	    return value;
	}
	
	public static String getSourceContents(String urlToRead) {
		
		TrustManager[] trustAllCerts = new TrustManager[]{
			    new X509TrustManager() {
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			            return null;
			        }
			        public void checkClientTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			        public void checkServerTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			    }
			};
		
		try {
		
		 SSLContext sc = SSLContext.getInstance("SSL");
    	 sc.init(null, trustAllCerts, new java.security.SecureRandom());
    	 HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e)
		{
			logger.error("EcasHelper error on getSourceContents " + e);
		}
		
	  logger.info("getSourceContents".toUpperCase() +" SSLContext Initiazed ");	
      URL url;
      HttpURLConnection conn;
      BufferedReader rd;
      String line;
      StringBuilder result = new StringBuilder();
      try {
    	  
    	 logger.info("getSourceContents".toUpperCase() +" TRY TO OPEN CONNECTION TO " + urlToRead);
         url = new URL(urlToRead);
         conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         while ((line = rd.readLine()) != null) {
            result.append(line);
         }
         rd.close();
      } catch (Exception e) {
    	 logger.error("EcasHelper error on getSourceContents when reading answer from URL" + e);
      }
      return result.toString();
   }

	public static void readData(User user, LdapService ldapService) throws NamingException {		
		user.setDisplayName(ldapService.getMoniker(user.getLogin()));
		user.setDepartments(ldapService.getUserLDAPGroups(user.getLogin()));		
	}
}
