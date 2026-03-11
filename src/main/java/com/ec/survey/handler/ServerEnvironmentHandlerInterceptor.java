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

import com.ec.survey.model.Setting;
import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SurveyService;

/**
 * Global Spring MVC handler that is to push the "server environment" parameter into the model of each Spring Controller
 */
public class ServerEnvironmentHandlerInterceptor extends HandlerInterceptorAdapter {

	protected static final Logger logger = Logger.getLogger(ServerEnvironmentHandlerInterceptor.class);
	
	public @Value("${ui.enableresponsive:true}") String enableresponsive;
	
	public @Value("${server.prefix}") String serverPrefix;
	
	public static final String APPLICATION_SERVER_ENVIRONMENT = "serverEnv";
	public @Value("${app.server.env:}") String serverEnv;
	
	public static final String APPLICATION_CAPTCHA_BYPASS = "captchaBypass";
	public @Value("${captcha.bypass:false}") String captchaBypass;
	
	public static final String APPLICATION_CAPTCHA_KEY = "captchaKey";
	public @Value("${captcha.key:}") String captchakey;
	
	public static final String APPLICATION_CAPTCHA_SERVERPREFIX = "captchaServerPrefix";
	public @Value("${captcha.serverprefix:/eusurvey/EuCaptchaApi/}") String captchaserverprefix;

	public static final String APPLICATION_CAPTCHA_DYNATRACE_SRC = "captchaDynatraceSrc";
	public @Value("${captcha.dynatracesrc:#{null}}") String captchaDynatraceSrc;
	
	public static final String APPLICATION_ARCHIVING = "enablearchiving";
	public @Value("${ui.enablearchiving:true}") String enablearchiving;

	public static final String APPLICATION_DELPHI = "enabledelphi";
	public @Value("${ui.enabledelphi:true}") String enabledelphi;
	
	public static final String APPLICATION_EVOTE = "enableevote";
	public static final String APPLICATION_EVOTE_LUX = "enableevotelux";
	public static final String APPLICATION_EVOTE_BRU = "enableevotebru";
	public static final String APPLICATION_EVOTE_ISPRA = "enableevoteispra";
	public static final String APPLICATION_EVOTE_OUTSIDE = "enableevoteoutside";
	public static final String APPLICATION_EVOTE_STANDARD = "enableevotestandard";
	public static final String APPLICATION_EVOTE_EEAS = "enableevoteeeas";
	public @Value("${ui.enableevote:false}") String enableevote;
	public @Value("${ui.enableevote-lux:false}") String enableevotelux;
	public @Value("${ui.enableevote-bru:false}") String enableevotebru;
	public @Value("${ui.enableevote-ispra:false}") String enableevoteispra;
	public @Value("${ui.enableevote-outside:false}") String enableevoteoutside;
	public @Value("${ui.enableevote-standard:false}") String enableevotestandard;
	public @Value("${ui.enableevote-eeas:false}") String enableevoteeeas;
	
	public static final String APPLICATION_FILEMANAGEMENT = "enablefilemanagement";
	public @Value("${ui.enablefilemanagement:true}") String enablefilemanagement;
	
	public static final String APPLICATION_OPC = "enableopc";
	public @Value("${ui.enableopc:false}") String enableopc;
	
	public static final String APPLICATION_ECF = "enableecf";
	public @Value("${ui.enableecf:false}") String enableecf;
	
	public static final String APPLICATION_SELFASSESSMENT = "enableselfassessment";
	public @Value("${ui.enableselfassessment:false}") String enableselfassessment;
	
	public static final String APPLICATION_PUBLICSURVEYS = "enablepublicsurveys";
	public @Value("${ui.enablepublicsurveys:false}") String enablepublicsurveys;
	
	public static final String APPLICATION_OSS = "oss";
	public @Value("${oss:true}") String oss;
	
	public static final String APPLICATION_PIWIK = "piwik";
	public @Value("${piwik:false}") String piwik;
	
	public static final String APPLICATION_SHOWPRIVACY = "showprivacy";
	public @Value("${show.privacy:true}") String showPrivacy;
	
	private @Value("${monitoring.recipient:#{null}}") String monitoringEmail;
	private @Value("${enablereportingdatabase:false}") String enablereportingdatabase;
	private @Value("${enablecookieconsentkit:false}") String enablecookieconsentkit;

	private @Value("${contextpath:/eusurvey}") String contextpath;
	private @Value("${googlesiteverification:#{null}}") String googlesiteverification;
	
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

		request.setAttribute(APPLICATION_OPC, enableopc != null && enableopc.equalsIgnoreCase("true"));
		request.setAttribute(APPLICATION_ECF, enableecf != null && enableecf.equalsIgnoreCase("true"));
		request.setAttribute(APPLICATION_SELFASSESSMENT, enableselfassessment != null && enableselfassessment.equalsIgnoreCase("true"));
		request.setAttribute(APPLICATION_DELPHI, enabledelphi != null && enabledelphi.equalsIgnoreCase("true"));
		request.setAttribute(APPLICATION_EVOTE, isEVoteAvailable());
		request.setAttribute(APPLICATION_EVOTE_LUX, enableevotelux != null && enableevotelux.equalsIgnoreCase("true"));
		request.setAttribute(APPLICATION_EVOTE_BRU, enableevotebru != null && enableevotebru.equalsIgnoreCase("true"));
		request.setAttribute(APPLICATION_EVOTE_ISPRA, enableevoteispra != null && enableevoteispra.equalsIgnoreCase("true"));
		request.setAttribute(APPLICATION_EVOTE_OUTSIDE, enableevoteoutside != null && enableevoteoutside.equalsIgnoreCase("true"));
		request.setAttribute(APPLICATION_EVOTE_STANDARD, enableevotestandard != null && enableevotestandard.equalsIgnoreCase("true"));
		request.setAttribute(APPLICATION_EVOTE_EEAS, enableevoteeeas != null && enableevoteeeas.equalsIgnoreCase("true"));
		request.setAttribute("languages", surveyService.getLanguages());

		return true;
	}

	private boolean isEVoteAvailable() {
		boolean lux = enableevotelux != null && enableevotelux.equalsIgnoreCase("true");
		boolean bru = enableevotebru != null && enableevotebru.equalsIgnoreCase("true");
		boolean ispra = enableevoteispra != null && enableevoteispra.equalsIgnoreCase("true");
		boolean outside = enableevoteoutside != null && enableevoteoutside.equalsIgnoreCase("true");
		boolean standard = enableevotestandard != null && enableevotestandard.equalsIgnoreCase("true");
		boolean eeas = enableevoteeeas != null && enableevoteeeas.equalsIgnoreCase("true");

		if (!lux & !bru && !ispra && !outside && !standard && !eeas) return false;
		return enableevote != null && enableevote.equalsIgnoreCase("true");
	}
	
	@Override
    public void postHandle(final HttpServletRequest request,
            final HttpServletResponse response, final Object handler,
            final ModelAndView modelAndView) throws Exception {

        if (modelAndView != null && modelAndView.hasView() && !modelAndView.getViewName().startsWith("redirect")) {
        	
        	modelAndView.getModelMap().addAttribute("serverprefix", serverPrefix);	
            modelAndView.getModelMap().addAttribute(APPLICATION_SERVER_ENVIRONMENT, serverEnv);       
        	
            modelAndView.getModelMap().addAttribute(APPLICATION_CAPTCHA_BYPASS, isByPassCaptcha());
            modelAndView.getModelMap().addAttribute(APPLICATION_CAPTCHA_KEY, captchakey);
			modelAndView.getModelMap().addAttribute(APPLICATION_CAPTCHA_SERVERPREFIX, captchaserverprefix);
			modelAndView.getModelMap().addAttribute(APPLICATION_CAPTCHA_DYNATRACE_SRC, captchaDynatraceSrc);
            
            modelAndView.getModelMap().addAttribute(APPLICATION_ARCHIVING, enablearchiving != null && enablearchiving.equalsIgnoreCase("true"));
			modelAndView.getModelMap().addAttribute(APPLICATION_DELPHI, enabledelphi != null && enabledelphi.equalsIgnoreCase("true"));
			modelAndView.getModelMap().addAttribute(APPLICATION_EVOTE, isEVoteAvailable());
			modelAndView.getModelMap().addAttribute(APPLICATION_EVOTE_LUX, enableevotelux != null && enableevotelux.equalsIgnoreCase("true"));
			modelAndView.getModelMap().addAttribute(APPLICATION_EVOTE_BRU, enableevotebru != null && enableevotebru.equalsIgnoreCase("true"));
			modelAndView.getModelMap().addAttribute(APPLICATION_EVOTE_ISPRA, enableevoteispra != null && enableevoteispra.equalsIgnoreCase("true"));
			modelAndView.getModelMap().addAttribute(APPLICATION_EVOTE_OUTSIDE, enableevoteoutside != null && enableevoteoutside.equalsIgnoreCase("true"));
			modelAndView.getModelMap().addAttribute(APPLICATION_EVOTE_STANDARD, enableevotestandard != null && enableevotestandard.equalsIgnoreCase("true"));
			modelAndView.getModelMap().addAttribute(APPLICATION_EVOTE_EEAS, enableevoteeeas != null && enableevoteeeas.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_OPC, enableopc != null && enableopc.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_ECF, enableecf != null && enableecf.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_SELFASSESSMENT, enableselfassessment != null && enableselfassessment.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_PUBLICSURVEYS, enablepublicsurveys != null && enablepublicsurveys.equalsIgnoreCase("true"));
                        
            modelAndView.getModelMap().addAttribute(APPLICATION_FILEMANAGEMENT, enablefilemanagement != null && enablefilemanagement.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_OSS, oss != null && oss.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_PIWIK, piwik != null && piwik.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute(APPLICATION_SHOWPRIVACY, showPrivacy != null && showPrivacy.equalsIgnoreCase("true"));
            
            modelAndView.getModelMap().addAttribute("contextpath", contextpath);
            modelAndView.getModelMap().addAttribute("monitoringEmail", monitoringEmail);
            modelAndView.getModelMap().addAttribute("enablereportingdatabase", enablereportingdatabase);
            
            String enablechargeback = settingsService.get(Setting.EnableChargeback);
    		modelAndView.getModelMap().addAttribute("enablechargeback", enablechargeback != null && enablechargeback.equalsIgnoreCase("true"));
            modelAndView.getModelMap().addAttribute("enablecookieconsentkit", enablecookieconsentkit);
            
            modelAndView.getModelMap().addAttribute("googlesiteverification", googlesiteverification);
            
            Device device = DeviceUtils.getCurrentDevice(request);
            if (!request.getRequestURI().endsWith("management/edit") && (device.isMobile() || device.isTablet()))
            {
				modelAndView.getModelMap().addAttribute("responsive", true);
				if (device.isMobile())
	    		{
					modelAndView.getModelMap().addAttribute("ismobile", true);
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
