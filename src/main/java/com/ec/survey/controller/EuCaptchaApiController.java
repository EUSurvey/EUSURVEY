package com.ec.survey.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/EuCaptchaApi")
public class EuCaptchaApiController extends BasicController {
	
	@RequestMapping(value = "/captchaImg", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String captchaImg(String locale, String capitalized, HttpServletRequest request, HttpServletResponse response) throws IOException {	
		sessionService.initializeProxy();
		
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		
		URL url = new URL(captchaserverprefixtarget + "captchaImg?locale=" + locale + "&captchaLength=8&capitalized=" + capitalized);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		conn.setRequestProperty("xJwtString", captchatoken);	    
		
//		List<String> cookies = conn.getHeaderFields().get("set-cookie");			
//		response.addHeader("original-cookie", cookies == null ? "" : String.join("#", cookies));

		return readData(conn);
	}
	
	@RequestMapping(value = "/reloadCaptchaImg/{captchaId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String reloadCaptchaImg(@PathVariable String captchaId, HttpServletRequest request, HttpServletResponse response) throws IOException {	
		sessionService.initializeProxy();
		
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		
		String locale = request.getParameter("locale");
		String capitalized = request.getParameter("capitalized");
		URL url = new URL(captchaserverprefixtarget + "reloadCaptchaImg/" + captchaId + "?locale=" + locale + "&capitalized=" + capitalized);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		
		conn.setRequestProperty("xJwtString", captchatoken);
		
//		String[] cookies = request.getHeader("original-cookie").split("#");			
//		for (String cookie : cookies) {
//			conn.addRequestProperty("Cookie", cookie);
//		}
		
		return readData(conn);
	}
	
	private String readData(HttpURLConnection conn) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;) {
            sb.append((char)c);
        }
		in.close();
		return sb.toString();
	}

}
