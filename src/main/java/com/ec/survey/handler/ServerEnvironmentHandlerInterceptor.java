/**
 * 
 */
package com.ec.survey.handler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SurveyService;

/**
 * Global Spring MVC handler that is to push the "server environment" parameter into the model of each Spring Controller
 */
public class ServerEnvironmentHandlerInterceptor extends HandlerInterceptorAdapter {

	protected static final Logger logger = Logger.getLogger(ServerEnvironmentHandlerInterceptor.class);
	
	public @Value("${ui.enableresponsive}") String enableresponsive;	
	
	public static final String APPLICATION_SERVER_ENVIRONMENT = "serverEnv";
	public @Value("${app.server.env}") String serverEnv;	
	
	public static final String APPLICATION_CAPTCHA_BYPASS = "captchaBypass";
	public @Value("${captcha.bypass:@null}") String captchaBypass;
	
	public static final String APPLICATION_CAPTCHA_KEY = "captchaKey";
	public @Value("${captcha.key}") String captchakey;	
	
	public static final String APPLICATION_ARCHIVING = "enablearchiving";
	public @Value("${ui.enablearchiving}") String enablearchiving;
	
	public static final String APPLICATION_FILEMANAGEMENT = "enablefilemanagement";
	public @Value("${ui.enablefilemanagement}") String enablefilemanagement;
	
	public static final String APPLICATION_OPC = "enableopc";
	public @Value("${ui.enableopc}") String enableopc;
	
	public static final String APPLICATION_PUBLICSURVEYS = "enablepublicsurveys";
	public @Value("${ui.enablepublicsurveys}") String enablepublicsurveys;
	
	public static final String APPLICATION_OSS = "oss";
	public @Value("${oss}") String oss;
	
	public static final String APPLICATION_PIWIK = "piwik";
	public @Value("${piwik}") String piwik;
	
	public static final String APPLICATION_SHOWPRIVACY = "showprivacy";
	public @Value("${show.privacy}") String showPrivacy;
	
	private @Value("${monitoring.recipient}") String monitoringEmail;
	private @Value("${enablereportingdatabase}") String enablereportingdatabase;

	private @Value("${contextpath}") String contextpath;	
	
	@Resource(name="settingsService")
	private SettingsService settingsService;
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	public boolean isByPassCaptcha(){
		return captchaBypass !=null && captchaBypass.equalsIgnoreCase("true");
	}
	
	public ServerEnvironmentHandlerInterceptor() {	
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		request.setAttribute(APPLICATION_CAPTCHA_BYPASS, isByPassCaptcha());
		request.setAttribute(APPLICATION_FILEMANAGEMENT, enablefilemanagement != null && enablefilemanagement.equalsIgnoreCase("true"));
        
		return true;
	}
	
	@Override
    public void postHandle(final HttpServletRequest request,
            final HttpServletResponse response, final Object handler,
            final ModelAndView modelAndView) throws Exception {

        if (modelAndView != null && modelAndView.hasView() && !modelAndView.getViewName().startsWith("redirect")) {
            modelAndView.getModelMap().addAttribute(APPLICATION_SERVER_ENVIRONMENT, serverEnv);       
        	
            modelAndView.getModelMap().addAttribute(APPLICATION_CAPTCHA_BYPASS, isByPassCaptcha());
            modelAndView.getModelMap().addAttribute(APPLICATION_CAPTCHA_KEY, captchakey);
            modelAndView.getModelMap().addAttribute(APPLICATION_ARCHIVING, enablearchiving != null && enablearchiving.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_OPC, enableopc != null && enableopc.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_PUBLICSURVEYS, enablepublicsurveys != null && enablepublicsurveys.equalsIgnoreCase("true"));
                        
            modelAndView.getModelMap().addAttribute(APPLICATION_FILEMANAGEMENT, enablefilemanagement != null && enablefilemanagement.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_OSS, oss != null && oss.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_PIWIK, piwik != null && piwik.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_SHOWPRIVACY, showPrivacy != null && showPrivacy.equalsIgnoreCase("true"));
            
            modelAndView.getModelMap().addAttribute("contextpath", contextpath);
            modelAndView.getModelMap().addAttribute("monitoringEmail", monitoringEmail);
            modelAndView.getModelMap().addAttribute("enablereportingdatabase", enablereportingdatabase);
            
            Device device = DeviceUtils.getCurrentDevice(request);
            if (!request.getRequestURI().endsWith("management/edit"))
            {
	            if (device.isMobile() || device.isTablet())
	    		{
	    			//if (enableresponsive != null && enableresponsive.equalsIgnoreCase("true"))
	    			//{
	    				modelAndView.getModelMap().addAttribute("responsive", true);
	    				if (device.isMobile())
	    	    		{
	    					modelAndView.getModelMap().addAttribute("ismobile", true);
	    	    		}
	    			//}
	    		}
            }
            
            String captcha = settingsService.get("captcha");
            modelAndView.getModelMap().addAttribute("captcha", captcha);
            
            String uisessiontimeout = settingsService.get("uisessiontimeout");
            modelAndView.getModelMap().addAttribute("uisessiontimeout", uisessiontimeout);            
            modelAndView.getModelMap().addAttribute("languages", surveyService.getLanguages());            
            modelAndView.getModelMap().addAttribute("origin", request.getRequestURI());
            
            if (request.getParameter("imported") != null && request.getParameter("imported").length() > 0)
        	{
            	modelAndView.getModelMap().addAttribute("imported",request.getParameter("imported"));
        	}
            
            Object id = request.getSession().getAttribute("surveyeditorsaved");
            if (id != null)
            {
            	 modelAndView.getModelMap().addAttribute("surveyeditorsaved", id);
            	 request.getSession().removeAttribute("surveyeditorsaved");
            }
		}
    }	
	
}
