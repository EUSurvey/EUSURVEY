package com.ec.survey.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/info")
public class InfoController extends BasicController {
	
	@RequestMapping(value = "/maintenance", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView maintenance(HttpServletRequest request){
		return new ModelAndView("info/maintenance");
	}
		
}
