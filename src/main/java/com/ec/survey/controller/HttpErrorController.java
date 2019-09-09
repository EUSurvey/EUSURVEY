package com.ec.survey.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/errors")
public class HttpErrorController extends BasicController {
	
	@RequestMapping(value = "/403.html")
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public ModelAndView handle403(HttpServletRequest request, HttpServletResponse response){
		request.getSession().setAttribute("lastErrorCode", 403);
		request.getSession().setAttribute("lastErrorTime", new Date());
		request.getSession().setAttribute("lastErrorURL", request.getAttribute("javax.servlet.error.request_uri"));
		return new ModelAndView("error/403","error", 403);
	}
	
	@RequestMapping(value = "/404.html")
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ModelAndView handle404(HttpServletRequest request){
		ModelAndView model = new ModelAndView("error/404","error", 404);
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
		return new ModelAndView("error/405","error", 405);
	}
	
	@RequestMapping(value = "/500.html")
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleException(HttpServletRequest request){
		request.getSession().setAttribute("lastErrorCode", 500);
		request.getSession().setAttribute("lastErrorTime", new Date());
		request.getSession().setAttribute("lastErrorURL", request.getAttribute("javax.servlet.error.request_uri"));
		return new ModelAndView("error/500","error","exception" );
	}
	
	@RequestMapping(value = "/2fa.html")
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handle2fa(HttpServletRequest request){
		return new ModelAndView("error/2fa","error","exception" );
	}	
		
}
