package com.ec.survey.controller;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.mapping.PaginationMapper;
import com.ec.survey.tools.*;
import org.joda.time.DateTime;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/forms")
public class SurveyController extends BasicController {
	
	@Autowired
	protected PaginationMapper paginationMapper;

    @Resource(name = "taskExecutor")
    private TaskExecutor taskExecutor;
		
	@RequestMapping()
	public ModelAndView surveys(HttpServletRequest request, Locale locale) throws Exception {
        User user = sessionService.getCurrentUser(request);

        if (user.getFormPrivilege() <= 0) {
            return new ModelAndView("redirect:/dashboard");
        }

		SurveyFilter filter = sessionService.getSurveyFilter(request, true);

		String delete = request.getParameter("delete");
		String origin = request.getParameter("origin");
		
		boolean deleted = false;
		boolean currentlyloaded = false;
		String shortname = "";

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
				
				surveyService.markDeleted(user.getId(), survey.getShortname(), survey.getUniqueId(), !survey.getIsDraft() || survey.getIsPublished());
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
		List<Survey> surveys = surveyService.getSurveysIncludingTranslationLanguages(filter, sqlPagination, false, true, false);

		surveyService.generateAccessInformation(surveys, user);
		
		paging.setItems(surveys);
		
		ModelAndView result = new ModelAndView("forms/forms", "paging", paging);
    	result.addObject(Constants.FILTER, filter);

        List<KeyValue> domains = ldapDBService.getDomains(true, true, resources, locale);
        result.addObject("domains", domains);
    	
    	if (filter.getGeneratedFrom() != null || filter.getGeneratedTo() != null || filter.getStartFrom() != null || filter.getStartTo() != null || filter.getEndFrom() != null || filter.getEndTo() != null)
    	{
    		result.addObject("showDates", true);
    	}
    	
    	if (deleted)
    	{
    		result.addObject(Constants.DELETED, true);
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
        List<Survey> result = surveyService.getSurveysIncludingTranslationLanguages(filter, sqlPagination, false, true, true);
        
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
			
			if (existingSurvey.getArchived())
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

	@RequestMapping(value = "/emailmatchesorganiation", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody Boolean emailmatchesorganiation(HttpServletRequest request) throws NamingException {

		String email = request.getParameter("email");
		String organisation = request.getParameter("organisation");

		List<String> organisationsFromLDAP = ldapService.getOrganisationForEmail(email);

		for (String org : organisationsFromLDAP) {
			if (org.equalsIgnoreCase(organisation)) return true;
		}

		return false;
	}

	@GetMapping("/tags")
	public @ResponseBody String[] getTagsJSON(HttpServletRequest request, Locale locale) {
		try {
			boolean createNewTag = request.getParameter("createNewTag") != null
								  && request.getParameter("createNewTag").equalsIgnoreCase("true");
			String term = request.getParameter("term").replace(" ", "");

			List<String> result = surveyService.getTags(term);
			if (createNewTag && !result.contains(term) && term.length() > 2) {
				result.add(0, term + " (" + resources.getMessage("label.NewTag", null, "new tag", locale) + ")");
			}
			return result.toArray(new String[0]);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

    @PostMapping("/bulkchange")
    public @ResponseBody int bulkchange(@RequestBody Map json, HttpServletRequest request) throws NotAgreedToPsException, NotAgreedToTosException, WeakAuthenticationException {
        String operation = (String) json.get("operation");
        BulkExecutor bulk = (BulkExecutor) context.getBean("bulkExecutor");

        User user = sessionService.getCurrentUser(request);

        ArrayList<Integer> surveyIds = (ArrayList<Integer>) json.get("surveys");
        int[] sids = surveyIds.stream().mapToInt(i -> i).toArray();
        List<Integer> sidslist = Arrays.stream(sids).boxed().collect(Collectors.toList());

        BulkChange change = new BulkChange();
        change.setSurveyIDs(sidslist);
        change.setStarted(new Date());
        change.setUserId(user.getId());

        switch (operation) {
            case "ADDREMOVEUSERS":
                String pmode = (String) json.get("mode");
                switch (pmode) {
                    case "ADD":
                        change.setPrivilegesMode(BulkChange.PrivilegesMode.Add);
                        break;
                    case "REPLACE":
                        change.setPrivilegesMode(BulkChange.PrivilegesMode.Replace);
                        break;
                    case "REMOVE":
                        change.setPrivilegesMode(BulkChange.PrivilegesMode.Remove);
                        break;
                }

                ArrayList<HashMap<String, String>> privileges = (ArrayList<HashMap<String, String>>)json.get("privileges");
                for (HashMap<String, String> map : privileges) {
                    String name = (String)map.get("name");
                    String type = (String)map.get("type");
                    String accessDraft = "0";
                    String accessResults = "0";
                    String formManagement = "0";
                    String manageInvitations = "0";
                    if (map.containsKey("accessDraft")) {
                        accessDraft = (String)map.get("accessDraft");
                    }
                    if (map.containsKey("accessResults")) {
                        accessResults = (String)map.get("accessResults");
                    }
                    if (map.containsKey("formManagement")) {
                        formManagement = (String)map.get("formManagement");
                    }
                    if (map.containsKey("manageInvitations")) {
                        manageInvitations = (String)map.get("manageInvitations");
                    }

                    change.getPrivileges().add(name + "|" + type + "|" + accessDraft + "|" + accessResults + "|" + formManagement + "|" + manageInvitations);
                }

                change.setOperation(BulkChange.Operation.AddRemovePrivilegedUsers);

                break;
            case "ADDREMOVETAGS":
                String tmode = (String) json.get("mode");
                switch (tmode) {
                    case "ADD":
                        change.setTagsMode(BulkChange.TagsMode.Add);
                        break;
                    case "REMOVE":
                        change.setTagsMode(BulkChange.TagsMode.Remove);
                        break;
                    case "REPLACE":
                        change.setTagsMode(BulkChange.TagsMode.Replace);
                        break;
                }

                ArrayList<String> tags = (ArrayList<String>)json.get("tags");
                change.setTags(tags);

                change.setOperation(BulkChange.Operation.AddRemoveTags);
                break;
            case "PUBLISHUNPUBLISH":
                String mode = (String) json.get("mode");
                switch (mode) {
                    case "UNPUBLISHALL":
                        change.setPublishMode(BulkChange.PublishMode.UnpublishAll);
                        break;
                    case "PUBLISHNOPENDINGCHANGES":
                        change.setPublishMode(BulkChange.PublishMode.PublishNoPendingChanges);
                        break;
                    case "PUBLISHAPPLYPENDINGCHANGES":
                        change.setPublishMode(BulkChange.PublishMode.PublishApplyPendingChanges);
                        break;
                }

                change.setOperation(BulkChange.Operation.PublishUnpublish);

                break;
            case "CHANGEOWNER":
                String newOwner = (String) json.get("newOwner");
                boolean addAsFormManager = (boolean) json.get("addAsFormManager");

                change.setOperation(BulkChange.Operation.ChangeOwner);
                change.setNewOwner(newOwner);
                change.setAddAsFormManager(addAsFormManager);

                break;
            case "DELETESURVEYS":
                boolean sendEmails = (boolean) json.get("sendEmails");

                change.setOperation(BulkChange.Operation.DeleteSurveys);
                change.setSendEmails(sendEmails);

                break;
            default:
                return -1;
        }

        surveyService.save(change);
        bulk.init(change);
        taskExecutor.execute(bulk);

        return change.getId();
    }

    private class CheckBulkChangeResult {
        public boolean error;
        public boolean finished;
        public List<Integer> successes;
        public List<Integer> skipped;
        public List<Integer> fails;

        public List<String> successesAliases;
        public List<String> skippedAliases;
        public List<String> failsAliases;
    }

    @GetMapping("/checkBulkChange/{id}")
    public @ResponseBody CheckBulkChangeResult checkBulkChange(@PathVariable int id) throws MessageException {
        if (id < 1) throw new MessageException("invalid change id: " + id);

        var result = new CheckBulkChangeResult();

        BulkChange change = surveyService.getBulkChange(id);
        result.error = change.getError() != null;
        result.finished = change.getFinished();

        result.successes = change.getSuccesses();
        result.fails = change.getFails();
        result.skipped = change.getSurveyIDs();
        result.skipped.removeAll(result.successes);
        result.skipped.removeAll(result.fails);

        result.successesAliases = surveyService.getShortnamesForSurveys(result.successes);
        result.skippedAliases = surveyService.getShortnamesForSurveys(result.skipped);
        result.failsAliases = surveyService.getShortnamesForSurveys(result.fails);

        return result;
    }
}
