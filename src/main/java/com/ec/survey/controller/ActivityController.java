package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Ucs2Utf8;

import com.ec.survey.tools.activity.ActivityRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Controller
@RequestMapping("/{shortname}/management")
public class ActivityController extends BasicController {

	@RequestMapping(value = "/activity")
	public ModelAndView activity(@PathVariable String shortname, HttpServletRequest request) throws Exception {
		Form form;
		User user = sessionService.getCurrentUser(request);
	
		Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
		form = new Form(resources);
		form.setSurvey(survey);
		
		User u = sessionService.getCurrentUser(request);
		if (!sessionService.userIsFormManager(form.getSurvey(), u, request))
		{		
			throw new ForbiddenURLException();			
		}
		
		Map<String,String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		ActivityFilter filter = sessionService.getLastActivityFilter(request);
		@SuppressWarnings("unchecked")
		Paging<Activity> paging = (Paging<Activity>) request.getSession().getAttribute("activity-paging");
		String newPage = request.getParameter("newPage");		
		newPage = newPage == null ? "1" : newPage;
		Integer itemsPerPage = ConversionTools.getInt(request.getParameter("itemsPerPage"), 20);		
		
		if (filter == null || !filter.getSurveyUid().equals(survey.getUniqueId()) || request.getMethod().equalsIgnoreCase("POST") || parameters.containsKey("reset"))
		{
			filter = new ActivityFilter();
			filter.setSurveyUid(survey.getUniqueId());
			paging = null;
		}
		
		boolean filtered = false;
		for (Entry<String, String[]> entry : parameters.entrySet())
		{
			String v = entry.getValue()[0];
			
			if (v != null && v.trim().length() > 0)
			{				
				if (entry.getKey().equalsIgnoreCase("metafilteractivityid"))
				{
					filter.setLogId(ConversionTools.getInt(entry.getValue()[0].trim()));
					filtered = true;
				} else if (entry.getKey().equalsIgnoreCase("metafilteractivityuser"))
				{
					filter.setUserId(ConversionTools.getInt(entry.getValue()[0].trim()));
					filtered = true;
				} else if (entry.getKey().equalsIgnoreCase("metafilteractivitydatefrom"))
				{
					filter.setDateFrom(ConversionTools.getDate(entry.getValue()[0].trim()));
					filtered = true;
				} else if (entry.getKey().equalsIgnoreCase("metafilteractivitydateto"))
				{
					filter.setDateTo(ConversionTools.getDate(entry.getValue()[0].trim()));
					filtered = true;
				} else if (entry.getKey().equalsIgnoreCase("metafilteractivityobject"))
				{
					filter.setObject(entry.getValue()[0].trim());
					filtered = true;
				} else if (entry.getKey().equalsIgnoreCase("metafilteractivityproperty"))
				{
					filter.setProperty(entry.getValue()[0].trim());
					filtered = true;
				} else if (entry.getKey().equalsIgnoreCase("metafilteractivityevent"))
				{
					filter.setEvent(entry.getValue()[0].trim());
					filtered = true;
				} else if (entry.getKey().equalsIgnoreCase("metafilteractivityoldvalue"))
				{
					filter.setOldValue(entry.getValue()[0].trim());
					filtered = true;
				} else if (entry.getKey().equalsIgnoreCase("metafilteractivitynewvalue"))
				{
					filter.setNewValue(entry.getValue()[0].trim());
					filtered = true;
				} else if (entry.getKey().equalsIgnoreCase("metafilteractivitydescription"))
				{
					filter.setDescription(entry.getValue()[0].trim());
					filtered = true;
				} else if (entry.getKey().startsWith("selected"))
				{
					filter.getVisibleColumns().add(entry.getKey().substring(8));
				} else if (entry.getKey().startsWith("exportselected"))
				{
					filter.getExportedColumns().add(entry.getKey().substring(14));
				} 
			}
		}
		
		if (filter.getVisibleColumns().isEmpty())
		{
			filter.getVisibleColumns().add("date");
			filter.getVisibleColumns().add("logid");
			filter.getVisibleColumns().add("user");
			filter.getVisibleColumns().add("object");
			filter.getVisibleColumns().add("property");
			filter.getVisibleColumns().add("event");
			filter.getVisibleColumns().add("description");
			filter.getVisibleColumns().add("oldvalue");
			filter.getVisibleColumns().add("newvalue");
		}
		
		if (filter.getExportedColumns().isEmpty())
		{
			filter.getExportedColumns().add("date");
			filter.getExportedColumns().add("user");
			filter.getExportedColumns().add("logid");
			filter.getExportedColumns().add("object");
			filter.getExportedColumns().add("property");
			filter.getExportedColumns().add("event");
			filter.getExportedColumns().add("description");
			filter.getExportedColumns().add("oldvalue");
			filter.getExportedColumns().add("newvalue");
		}
		
		if (paging == null)
		{
			paging = new Paging<>();
		}
		
		paging.setItemsPerPage(itemsPerPage);
		int numberOfActivities = activityService.getNumberActivities(filter);
		paging.setNumberOfItems(numberOfActivities);
		paging.moveTo(newPage);
		
		List<Activity> activities = activityService.get(filter, paging.getCurrentPage(), paging.getItemsPerPage());
		
		paging.setItems(activities);		
		
		for (Activity activity : activities)
		{
			activity.setUserName(activity.getUserId() > 0 ? administrationService.getUser(activity.getUserId()).getName() : "");
		}
		List<Integer> allUserIds = activityService.getAllUsers(survey.getUniqueId());
		List<User> allUsers = new ArrayList<>();
		for (int id : allUserIds)
		{
			if (id > 0) {
				allUsers.add(administrationService.getUser(id));
			}
		}
		
		sessionService.setLastActivityFilter(request, filter);
		
		ModelAndView result = new ModelAndView("management/activity", "paging", paging);
		result.addObject("allActivityIds", ActivityRegistry.getAllActivityIds());
		
		result.addObject(Constants.FILTER, filter);
		result.addObject("filtered", filtered);
		result.addObject("allUsers", allUsers);
		
		request.getSession().setAttribute("activity-paging", paging.clean());
		
		return result;
	}
		
}
