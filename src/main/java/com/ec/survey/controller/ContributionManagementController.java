package com.ec.survey.controller;

import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Paging;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.SqlPagination;
import com.ec.survey.model.administration.ContributionSearchResult;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.service.mapping.PaginationMapper;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/administration")
public class ContributionManagementController extends BasicController {
	
	@Autowired
	protected PaginationMapper paginationMapper;    
	
	@RequestMapping(value = "/contributionsearch", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView contributionsearch(HttpServletRequest request, Model model) throws Exception {
		ModelAndView result = new ModelAndView("administration/contributionsearch");
		if (request.getParameter("keepfilter") != null)
		{
			ResultFilter filter = (ResultFilter) request.getSession().getAttribute("contributionsearchfilter");
			if (filter == null) filter = new ResultFilter();
			
			String newPage = request.getParameter("newPage");
			newPage = newPage == null ? "1" : newPage;
			Integer itemsPerPage = 50;
			if (request.getParameter("itemsPerPage") != null) ConversionTools.getInt(request.getParameter("itemsPerPage"), 50);
			
			Paging<AnswerSet> paging = new Paging<>();
			paging.setItemsPerPage(itemsPerPage);
			paging.setNumberOfItems(0);
			paging.moveTo(newPage);
			            
			SqlPagination sqlPagination = paginationMapper.toSqlPagination(paging);
			List<AnswerSet> answerSets = answerService.getAnswers(null, filter, sqlPagination, true, false, false);
						
			paging.setItems(answerSets);
			
			result = new ModelAndView("administration/contributionsearch", "paging", paging);
	    	result.addObject("filter", filter);
		
		} else {
			result.addObject("filter", new ResultFilter());
		}
		
    	return result;
	}
	
	@PostMapping(value = "/contributionsearch")
	public ModelAndView contributionsearchPOST(HttpServletRequest request, Model model) throws Exception {
		
		ResultFilter filter = new ResultFilter();		
		filter.setSurveyUid(request.getParameter("surveyUid"));
		filter.setSurveyShortname(request.getParameter("surveyShortname"));
		filter.setSurveyTitle(request.getParameter("surveyTitle"));
		filter.setDraftId(request.getParameter("draftId"));
		filter.setCaseId(request.getParameter("caseId"));
		filter.setNoTestAnswers(true);
		filter.setUpdatedFrom(ConversionTools.getDate(request.getParameter("metafilterupdatefrom").trim()));
		filter.setUpdatedTo(ConversionTools.getDate(request.getParameter("metafilterupdateto").trim()));
		filter.setStatus(request.getParameter("contributiontype"));
		
		String newPage = request.getParameter("newPage");
		newPage = newPage == null ? "1" : newPage;
		Integer itemsPerPage = 50;
		if (request.getParameter("itemsPerPage") != null) ConversionTools.getInt(request.getParameter("itemsPerPage"), 50);
		
		Paging<AnswerSet> paging = new Paging<>();
		paging.setItemsPerPage(itemsPerPage);
		paging.setNumberOfItems(0);
		paging.moveTo(newPage);
		
		SqlPagination sqlPagination = new SqlPagination(paging.getCurrentPage(), paging.getItemsPerPage());
		List<AnswerSet> answerSets = answerService.getAnswers(null, filter, sqlPagination, true, false, false);
		
		paging.setItems(answerSets);
		
		ModelAndView result = new ModelAndView("administration/contributionsearch", "paging", paging);
		result.addObject("filter", filter);
		request.getSession().setAttribute("contributionsearchfilter", filter);
		
		return result;

	}
	
	@RequestMapping(value = "/contributionsearchJSON", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody List<ContributionSearchResult> contributionsearchJSON(HttpServletRequest request) {		
		try {
		
			String rows = request.getParameter("rows");			
			if (rows == null) return null;			
			String page = request.getParameter("page");			
			if (page == null) return null;
					
			ResultFilter filter = (ResultFilter) request.getSession().getAttribute("contributionsearchfilter");
			if (filter == null) return null;
			
			SqlPagination sqlPagination = new SqlPagination(Integer.parseInt(page), Integer.parseInt(rows));
			List<AnswerSet> answerSets = answerService.getAnswers(null, filter, sqlPagination, true, false, false);
			List<ContributionSearchResult> result = new ArrayList<>();
			for (AnswerSet answerSet: answerSets)
			{
				result.add(new ContributionSearchResult(answerSet.getSurvey().getUniqueId(), answerSet.getSurvey().getShortname(), answerSet.getSurvey().cleanTitle(), answerSet.getDraftId(), answerSet.getUniqueCode(), answerSet.getIsDraft(), answerSet.getInvitationId(), answerSet.getUpdateDate()));
			}
			return result;
		}
		catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return null;
	}
	
	@PostMapping(value = "/resetcontribution")
	public @ResponseBody String resetcontribution(Locale locale, HttpServletRequest request) throws Exception {		
		User user = sessionService.getCurrentUser(request);
		String code = request.getParameter("uid");	
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2) {
			//only form administrators are allowed to do this
			return Constants.ERROR;
		}
		
		String uid = answerService.resetContribution(code);
		
		return uid != null ? uid : Constants.ERROR;
	}
		
}
