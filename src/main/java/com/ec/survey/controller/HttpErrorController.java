package com.ec.survey.controller;

import javax.servlet.http.HttpServletRequest;

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
	public ModelAndView handle403(HttpServletRequest request){
		return new ModelAndView("error/403","error", 403);
	}
	
	@RequestMapping(value = "/404.html")
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ModelAndView handle404(HttpServletRequest request){
		ModelAndView model = new ModelAndView("error/404","error", 404);
		model.addObject("is404", true);
		return model;
	}
	
	@RequestMapping(value = "/405.html")
	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	public ModelAndView handle405(HttpServletRequest request){
		return new ModelAndView("error/405","error", 405);
	}
	
	@RequestMapping(value = "/500.html")
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleException(HttpServletRequest request){
		return new ModelAndView("error/500","error","exception" );
	}
	
		
}
