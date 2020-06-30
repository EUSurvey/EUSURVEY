package com.ec.survey.controller;

import com.ec.survey.model.Form;
import com.ec.survey.model.Paging;
import com.ec.survey.model.SqlPagination;
import com.ec.survey.model.SurveyFilter;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.mapping.PaginationMapper;
import com.ec.survey.tools.ConversionTools;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/forms")
public class SurveyController extends BasicController {
	
	@Autowired
	protected PaginationMapper paginationMapper;       
		
	@RequestMapping()
	public ModelAndView surveys(HttpServletRequest request) throws Exception {	
		
		SurveyFilter filter = sessionService.getSurveyFilter(request, true);			
		
		String delete = request.getParameter("delete");
		String origin = request.getParameter("origin");
		
		boolean deleted = false;
		boolean currentlyloaded = false;
		String shortname = "";
		
		User user = sessionService.getCurrentUser(request);
		
		if (delete != null && delete.trim().length() > 0)
		{
			Survey survey = surveyService.getSurvey(Integer.parseInt(delete), false, true);
			shortname = survey.getShortname();
			
			boolean allowed = sessionService.userIsFormAdmin(survey, user, request);
			
			if (allowed)
			{
				try {
					Form form = sessionService.getForm(request, null, false, false);
					
					if (form != null && form.getSurvey() != null && form.getSurvey().getUniqueId().equals(survey.getUniqueId()))
					{
						currentlyloaded = true;
					}
				} catch (Exception e) {
					//ignore
				}				
				
				surveyService.markDeleted(Integer.parseInt(delete), user.getId(), survey.getShortname(), survey.getUniqueId(), !survey.getIsDraft() || survey.getIsPublished());
				deleted = true;
			} else {
				logger.error("Survey Deletion denied: " + shortname + " user: " + user.getLogin() + " privilege: " + user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement));
			}
		}
		
		String newPage = request.getParameter("newPage");
		newPage = newPage == null ? "1" : newPage;
		Integer itemsPerPage = 10;
		if (request.getParameter("itemsPerPage") != null) ConversionTools.getInt(request.getParameter("itemsPerPage"), 10);
		
		Paging<Survey> paging = new Paging<>();
		paging.setItemsPerPage(itemsPerPage);
		int numberOfSurveys = 0; 
		paging.setNumberOfItems(numberOfSurveys);
		paging.moveTo(newPage);
		
        SqlPagination sqlPagination = paginationMapper.toSqlPagination(paging);
		List<Survey> surveys = surveyService.getSurveysIncludingTranslationLanguages(filter, sqlPagination, false, true);
		
		surveyService.generateAccessInformation(surveys, user);
		
		paging.setItems(surveys);
		
		ModelAndView result = new ModelAndView("forms/forms", "paging", paging);
    	result.addObject("filter", filter);
    	
    	if (filter.getGeneratedFrom() != null || filter.getGeneratedTo() != null || filter.getStartFrom() != null || filter.getStartTo() != null || filter.getEndFrom() != null || filter.getEndTo() != null)
    	{
    		result.addObject("showDates", true);
    	}
    	
    	if (deleted)
    	{
    		result.addObject("deleted", true);
    		result.addObject("deletedShortname", shortname);
    		
    		if (currentlyloaded)
    		{
    			request.getSession().removeAttribute("sessioninfo");
    		}
    		
    		if (origin != null && origin.equalsIgnoreCase("dashboard"))
    		{
    			return new ModelAndView("redirect:/dashboard?deleted=" + shortname);
    		} else if (origin != null && origin.equalsIgnoreCase("surveysearch"))
    		{
    			return new ModelAndView("redirect:/administration/surveysearch?normaldeleted=" + shortname);
    		}
    	}
    	
    	if (request.getParameter("invalidCodeFound") != null && request.getParameter("invalidCodeFound").equalsIgnoreCase("true"))
    	{
    		result.addObject("invalidCodeFound",request.getParameter("invalidCodeFound"));
    	}
    	request.getSession().setAttribute("lastSurveyFilter", filter);
    	
    	return result;
	}	
	
	@RequestMapping(value = "/surveysjson", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody List<Survey> surveysjson(HttpServletRequest request) throws Exception {	
		
		String rows = request.getParameter("rows");		
		int itemsPerPage = Integer.parseInt(rows);
		
		String page = request.getParameter("page");		
		int newPage = Integer.parseInt(page);
		
		SurveyFilter filter = (SurveyFilter) request.getSession().getAttribute("lastSurveyFilter");

        SqlPagination sqlPagination = new SqlPagination(newPage, itemsPerPage);
        List<Survey> result = surveyService.getSurveysIncludingTranslationLanguages(filter, sqlPagination, false, true);
        
        surveyService.generateAccessInformation(result, sessionService.getCurrentUser(request));
        
        return result;
	}
		
	@RequestMapping(value = "/shortnameexistsjson", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Boolean shortnameexistsjson(HttpServletRequest request) {	
		
		String shortname = request.getParameter("name");	
		String id = request.getParameter("id");	
		
		Survey existingSurvey = surveyService.getSurvey(shortname, true, false, false, false, null, true, false);
		
		if (existingSurvey != null)
		{
			if (existingSurvey.getIsDeleted())
			{
				return false;
			}
			
			if (id != null)
			{
				int i = -1;
				try {
					i = Integer.parseInt(id);
					return !existingSurvey.getId().equals(i);
				} catch (NumberFormatException e)
				{
					//ignore
				}
			}
		}
		
		return existingSurvey != null;
	}
		
}
