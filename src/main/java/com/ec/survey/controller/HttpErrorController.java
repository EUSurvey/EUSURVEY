package com.ec.survey.controller;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.ec.survey.model.Setting;
import com.ec.survey.tools.Constants;

@Controller
@RequestMapping("/errors")
public class HttpErrorController extends BasicController {
	
	@RequestMapping(value = "/403.html")
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public ModelAndView handle403(HttpServletRequest request, HttpServletResponse response){
		request.getSession().setAttribute("lastErrorCode", 403);
		request.getSession().setAttribute("lastErrorTime", new Date());
		request.getSession().setAttribute("lastErrorURL", request.getAttribute("javax.servlet.error.request_uri"));
		return new ModelAndView("error/403", Constants.ERROR, 403);
	}
	
	@RequestMapping(value = "/404.html")
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ModelAndView handle404(HttpServletRequest request){
		ModelAndView model = new ModelAndView("error/404", Constants.ERROR, 404);
		model.addObject("is404", true);
		request.getSession().setAttribute("lastErrorCode", 404);
		request.getSession().setAttribute("lastErrorTime", new Date());
		request.getSession().setAttribute("lastErrorURL", request.getAttribute("javax.servlet.error.request_uri"));
		return model;
	}
	
	@RequestMapping(value = "/405.html")
	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	public ModelAndView handle405(HttpServletRequest request){
		request.getSession().setAttribute("lastErrorCode", 405);
		request.getSession().setAttribute("lastErrorTime", new Date());
		request.getSession().setAttribute("lastErrorURL", request.getAttribute("javax.servlet.error.request_uri"));
		return new ModelAndView("error/405", Constants.ERROR, 405);
	}
	
	@RequestMapping(value = "/500.html")
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleException(HttpServletRequest request){
		request.getSession().setAttribute("lastErrorCode", 500);
		request.getSession().setAttribute("lastErrorTime", new Date());
		request.getSession().setAttribute("lastErrorURL", request.getAttribute("javax.servlet.error.request_uri"));
		return new ModelAndView("error/500",Constants.ERROR,"exception" );
	}
	
	@RequestMapping(value = "/2fa.html")
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handle2fa(HttpServletRequest request){
		if (require2fa){
			return new ModelAndView("error/always2fa",Constants.ERROR,"exception" );
		}
		return new ModelAndView("error/2fa",Constants.ERROR,"exception" );
	}
	
	@RequestMapping(value = "/frozen.html")
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handlefrozen(HttpServletRequest request){
		return new ModelAndView("error/frozen",Constants.ERROR,"exception" );
	}	
	
	@RequestMapping(value = "/accessdenied.html")
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleaccessdenied(HttpServletRequest request){
		return new ModelAndView("error/accessdenied",Constants.ERROR,"exception" );
	}
	
	@RequestMapping(value = "/surveylimit.html")
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handlesurveylimit(HttpServletRequest request, Locale locale){
		ModelAndView result = new ModelAndView("error/surveylimit",Constants.ERROR,"exception" );		
		
		result.addObject("MaxSurveysPerUser", settingsService.get(Setting.MaxSurveysPerUser));
		int minutes = Integer.parseInt(settingsService.get(Setting.MaxSurveysTimespan));
		
		String lminutes = resources.getMessage("label.minutes", null, "minutes", locale);
		
		String timespan = minutes + " " + lminutes;
		
		if (minutes >= 1440) {
			String ldays = resources.getMessage("label.days", null, "days", locale);			
			timespan = (minutes / 1440) + " " + ldays;
		} else if (minutes >= 60) {
			String lhours = resources.getMessage("label.hours", null, "hours", locale);	
			timespan = (minutes / 60) + " " + lhours;
		}
		
		result.addObject("MaxSurveysTimespan", timespan);
		
		return result;
	}	
	
	@RequestMapping(value = "/weak.html")
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleWeak(HttpServletRequest request, Locale locale) {
		ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
		String message = resources.getMessage("error.WeakAuthentication", null,
				"Please log in using two factor authentication in order to access the system.", locale);
		model.addObject(Constants.MESSAGE, message);
		model.addObject("contextpath", contextpath);
		return model;
	}
		
	@RequestMapping(value = "/request-rejected")
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleRequestRejected(
	        @RequestAttribute("javax.servlet.error.exception") RequestRejectedException ex,
	        @RequestAttribute("javax.servlet.error.request_uri") String uri, HttpServletRequest request) {
				
	    String msg = ex.getMessage();

	    logger.debug(String.format("Request with URI [%s] rejected. %s", uri, msg));

	    request.getSession().setAttribute("lastErrorCode", 403);
		request.getSession().setAttribute("lastErrorTime", new Date());
		request.getSession().setAttribute("lastErrorURL", request.getAttribute("javax.servlet.error.request_uri"));
		return new ModelAndView("error/403",Constants.ERROR, 403);
	}
}
