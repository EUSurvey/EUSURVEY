package com.ec.survey.security;

import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;



public class CsrfSecurityRequestMatcher implements RequestMatcher {
	private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS|PATCH|PUT|DELETE)$");
    private RegexRequestMatcher notifySuccessMatcher = new RegexRequestMatcher("/home/notifySuccess.*",  "POST");
    private RegexRequestMatcher notifyErrorMatcher = new RegexRequestMatcher("/home/notifyError.*",  "POST");
    
    
 
    @Override
    public boolean matches(HttpServletRequest request) {
    	
    	if(allowedMethods.matcher(request.getMethod()).matches()){
            return false;
        }
        return !(notifySuccessMatcher.matches(request) || notifyErrorMatcher.matches(request));

    }
    	
}
