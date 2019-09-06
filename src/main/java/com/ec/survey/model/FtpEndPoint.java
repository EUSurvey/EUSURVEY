package com.ec.survey.model;

import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.text.ParseException;

public class FtpEndPoint {
	private static final String INVALID_FTP_URI = "Invalid ftp uri  ";

	protected static final Logger logger = Logger.getLogger(FtpEndPoint.class);
	
	public static FtpEndPoint createFromCamel(String ftpUri) {
		MessageFormat messageFormat = new MessageFormat(CAMEL_FTP_PATTERN);
		FtpEndPoint result =  new FtpEndPoint();
		try {
			Object[] parse = messageFormat.parse(ftpUri);
			if (parse.length == 5){
				result.protocol = (String) parse[0];
				result.userName = (String) parse[1];
				result.host = (String) parse[2];
				result.path = (String) parse[3];
				result.password = (String) parse[4];
			}
			else {
				throw new IllegalArgumentException(INVALID_FTP_URI + ftpUri);
			}
				
		} catch (ParseException e) {
			throw new IllegalArgumentException(INVALID_FTP_URI + ftpUri);
		}
		return result;
	}

	private String protocol;

	private String userName;

	private String password;

	private String host;

	private String path;

	// Camel URI, format is ftp://user@host/folder?password=secret
	private static final String CAMEL_FTP_PATTERN = "{0}://{1}@{2}//{3}?password=RAW({4})&passiveMode=true&binary=true&stepwise=false";
	
	private static final String CAMEL_CONSUMER_FTP_PATTERN = "{0}://{1}@{2}//{3}?password=RAW({4})&stepwise=false&useList=false&fileName={5}&pollStrategy=#tryFiveTimes";
	
	

	// Camel URI, format is ftp://user:password@host/folder
	private static final String MT_FTP_PATTERN = "{0}://{1}:{2}@{3}/{4}";

	public static FtpEndPoint createFromMT(String ftpUri) {		
		MessageFormat messageFormat = new MessageFormat(MT_FTP_PATTERN);
		FtpEndPoint result = new FtpEndPoint();
		try {
			Object[] parse = messageFormat.parse(ftpUri);
			if (parse.length == 5){
				result.protocol = (String) parse[0];
				result.userName = (String) parse[1];
				result.password = (String) parse[2];
				result.host = (String) parse[3];
				result.path = (String) parse[4];
			}
			else {
				throw new IllegalArgumentException(INVALID_FTP_URI + ftpUri);
			}

		} catch (ParseException e) {
			throw new IllegalArgumentException(INVALID_FTP_URI + ftpUri);
		}
		// use parse and format
		return result;
	}
	
	public String toCamelString() {
		return  MessageFormat.format(CAMEL_FTP_PATTERN,protocol,userName,host,path,password);
	}
	
	public String toCamelConsumerString() {
		int lastIndexOfSlash = path.lastIndexOf('/');
		String dierctory = path.substring(0,lastIndexOfSlash);
		String fileName = path.substring(lastIndexOfSlash+1);
		
		return  MessageFormat.format(CAMEL_CONSUMER_FTP_PATTERN,protocol,userName,host,dierctory,password,fileName);
	}
	
	public String toMTString() {
		return  MessageFormat.format(MT_FTP_PATTERN,protocol,userName,password,host,path);
	}

	public String getHost() {
		return host;
	}

	public String getPassword() {
		return password;
	}

	public String getPath() {
		return path;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getUserName() {
		return userName;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
