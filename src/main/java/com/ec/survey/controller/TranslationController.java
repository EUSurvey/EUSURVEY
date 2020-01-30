package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.NoFormLoadedException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
import com.ec.survey.tools.*;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/{shortname}/management")
public class TranslationController extends BasicController {
		
	@Resource(name="machineTranslationService")
	MachineTranslationService machineTranslationService;
		
	private @Value("${export.xsllink}") String xsllink;
	
	public @Value("${mt.use.ec.mt}") String useECMT;	
	
	private @Value("${mt.servicewsdl}") String mtServiceWsdl;
	private @Value("${microsoft.translation.client.id}") String msClientId;
	
	public boolean isMTAvailable=true;	
	
	@RequestMapping(value = "/translations", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView translations(@PathVariable String shortname, HttpServletRequest request, Locale locale) throws Exception {
		User u = sessionService.getCurrentUser(request);
		initMTCheck();
		Form form;
		Survey survey = surveyService.getSurveyByShortname(shortname, true, u, request, false, true, true, false);
		form = new Form(resources);
		form.setSurvey(survey);
		if (!sessionService.userIsFormManager(form.getSurvey(), u, request))
		{		
			throw new ForbiddenURLException();			
		}
		
		List<Translations> translations = translationService.getTranslationsForSurvey(form.getSurvey().getId(), true, true);		
		boolean completeTranslationFound = false;
		for (Translations translation: translations)
		{
			if (translation.getComplete())
			{
				completeTranslationFound = true;
				break;
			}
		}
		Map<Integer,String> completedTranslations = new TreeMap<>();
		if (!completeTranslationFound)
		{
			Translations dummy = TranslationsHelper.getTranslations(form.getSurvey(), true);
			dummy.setComplete(true);
			dummy.setLanguage(null);
			translations.add(dummy);
		}
		else 
		{
			for (Translations translation: translations)
			{
				if (translation.getComplete())
				{
					completedTranslations.put(translation.getId(), translation.getLanguage().getEnglishName());
				}
			}			
		}		
		
		ModelAndView result = new ModelAndView("management/translations", "translations", translations);
		result.addObject("form", form);
		result.addObject("isMTAvailable",isMTAvailable);
		List<Language> languages = surveyService.getLanguages();
		
		result.addObject("uploadItem", new UploadItem());
		
		StringBuilder autocomplete = new StringBuilder("[");
		for (Language language : languages)
		{
			autocomplete.append("\"").append(language.getEnglishName()).append(" ").append("(").append(language.getCode()).append(")\",");
		}		
		autocomplete = new StringBuilder(autocomplete.substring(0, autocomplete.length() - 1));
		autocomplete.append("]");
		result.addObject("autocomplete", autocomplete.toString());
		result.addObject("completedTranslations",completedTranslations);		

		List<KeyValue> infos = TranslationsHelper.getLongDescriptions(form.getSurvey(), resources, locale);
		result.addObject("infos", infos);
		
		if (request.getParameter("saved") != null && request.getParameter("saved").equalsIgnoreCase("true"))
		{
			String message = resources.getMessage("message.ChangesSaved", null, "The changes have been saved.", locale);
			result.addObject("message", message);
		}
		
		if (request.getParameter("done") != null && request.getParameter("done").equalsIgnoreCase("1"))
		{
			String message = resources.getMessage("message.TranslationImportedSuccessfully", null, "Translation has been imported successfully.", locale);
    		result.addObject("message", message);
		}
		
		if (request.getParameter("done") != null && request.getParameter("done").equalsIgnoreCase("2"))
		{
			String message = resources.getMessage("message.TranslationAddedSuccessfully", null, "Translation has been added successfully.", locale);
    		result.addObject("message", message);
		}
		
		if (request.getParameter("error") != null && request.getParameter("error").equalsIgnoreCase("RequestTranslation"))
		{
			String message = resources.getMessage("error.RequestTranslation", null, "Request for translation failed", locale);
			result.addObject("error", message);
		}
		
		if (request.getParameter("error") != null && request.getParameter("error").equalsIgnoreCase("LanguageNotRecognized"))
		{
			String message = resources.getMessage("error.LanguageNotRecognized", null, "The language was not recognized.", locale);
			result.addObject("error", message);
		}
		
		return result;
	}
	
	@RequestMapping(value = "/replace", method = RequestMethod.POST)
	public ModelAndView replace(@PathVariable String shortname, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		Form form;
		form = sessionService.getForm(request, shortname, false, false);
		
		try {
			
			User u = sessionService.getCurrentUser(request);
			if (!sessionService.userIsFormAdmin(form.getSurvey(), u, request))
			{		
				throw new ForbiddenURLException();			
			}
		
			Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request); 
			
			String search = parameterMap.get("search")[0]; //request.getParameter("search");
			String replace = parameterMap.get("replace")[0]; //request.getParameter("replace");
			
			String translationId = request.getParameter("id");
			
			int id = Integer.parseInt(translationId);
			
			if (id == 0)
			{
				SurveyHelper.replace(form.getSurvey(), search, replace);
				surveyService.update(form.getSurvey(), true, true, true, u.getId());
			} else {
				Translations translations = translationService.getTranslations(id);
				for (Translation translation: translations.getTranslations())
				{
					if (translation.getLabel() != null)
					if (translation.getLabel().contains(search))
					{
						translation.setLabel(translation.getLabel().replace(search, replace));
						if (translation.getLabel().length() == 0)
						{
							throw new Exception("found empty label after replace!");
						}
					}
				}
				translationService.save(translations);
				surveyService.makeDirty(form.getSurvey().getId());
			}
			
			return new ModelAndView("redirect:/" + shortname + "/management/translations");
			
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			return new ModelAndView("redirect:/errors/500.html");
		}
	}
	@RequestMapping(value = "/deletetranslation", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void deletetranslation(HttpServletRequest request, Locale locale, HttpServletResponse response) {
		
		PrintWriter writer = null;
		
		try {
            writer = response.getWriter();
        } catch (IOException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
		
		boolean success = false;
		String result = "";
		
		try {
			String id = request.getParameter("translationId");
			
			Translations translations = translationService.getTranslations(Integer.parseInt(id));
			Survey survey = surveyService.getSurvey(translations.getSurveyId(), false, true);
			translations.setComplete(TranslationsHelper.isComplete(translations, survey));
			
			User u = sessionService.getCurrentUser(request);
			if (!sessionService.userIsFormAdmin(survey, u, request))
			{		
				throw new ForbiddenURLException();			
			}
			
			List<Translations> alltranslations = translationService.getTranslationsForSurvey(translations.getSurveyId(), true);
			
			boolean blnActive = false;
			
			if (alltranslations.size() == 1)
			{
				result = "{\"success\": false, \"m\": 1}";
			} else {
			
				boolean completeFound = false;
				
				if (translations.getComplete())
				{
					for (Translations translations2 : alltranslations) {
						if (!translations2.getId().equals(translations.getId()) && translations2.getComplete())
						{
							completeFound = true;
						} else if (translations2.getId().equals(translations.getId()))
						{
							if (translations2.getActive())
							{
								blnActive = true;
							}
						}
					}
					
					if (!completeFound)
					{
						result = "{\"success\": false, \"m\": 1}";
					}
				}
								
			}
			
			if (result.length() == 0)
			success = translationService.deleteTranslations(Integer.parseInt(id));	
			
			if (success)
			{
				if (blnActive)
				{
					surveyService.makeDirty(translations.getSurveyId());
				}
				
				result = "{\"success\": true}";
				activityService.log(222, translations.getLanguage().getCode(), null, sessionService.getCurrentUser(request).getId(), survey.getUniqueId());
        	}
		} catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
		
	    response.setStatus(HttpServletResponse.SC_OK);

		if (result.length() == 0) result = "{\"success\": false, \"m\": 0}";
		
        writer.print(result);
        
        writer.flush();
        writer.close();

	}
	
	@RequestMapping(value = "/deletetranslations", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void deletetranslations(HttpServletRequest request, Locale locale, HttpServletResponse response) {
		
		PrintWriter writer = null;
		
		try {
            writer = response.getWriter();
        } catch (IOException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
		
		boolean success = false;
		String result = "";
		
		try {
			
			Survey survey = null;
			
			String idsString = request.getParameter("translationIds");
			String[] ids = idsString.split("\\|");
			
			List<Translations> alltranslations = null;
			Map<Integer, Translations> allTranslationsById = new HashMap<>();
			
			User u = sessionService.getCurrentUser(request);
			
			for (String id : ids) {
				if (id.trim().length() > 0)
				{
					result = "";
					
					Translations translations = translationService.getTranslations(Integer.parseInt(id));
					
					if (alltranslations == null)
					{
						alltranslations = translationService.getTranslationsForSurvey(translations.getSurveyId(), true);
						for (Translations trans : alltranslations) {
							allTranslationsById.put(trans.getId(), trans);
						}
					}					
					
					if (allTranslationsById.size() == 1)
					{
						result = "{\"success\": false, \"m\": 1}";
					} else {
					
						boolean completeFound = false;
						
						if (translations.getComplete())
						{
							for (Integer transId : allTranslationsById.keySet()) {
								if (!transId.equals(translations.getId()) && allTranslationsById.get(transId).getComplete())
								{
									completeFound = true;
									break;
								}
							}
							
							if (!completeFound)
							{
								result = "{\"success\": false, \"m\": 1}";
							}
						}
										
					}
					
					if (result.length() == 0)
					{
						Translations t = translationService.getTranslations(Integer.parseInt(id));
						Survey s =surveyService.getSurvey(t.getSurveyId());
						if (survey == null || !survey.getUniqueId().equals(s.getUniqueId()))
						{
							if (u == null || !sessionService.userIsFormAdmin(s, u, request))
							{		
								throw new ForbiddenURLException();			
							} else {
								survey = s;
							}
						}						
						
						success = translationService.deleteTranslations(Integer.parseInt(id));
						activityService.log(222, translations.getLanguage().getCode(), null, sessionService.getCurrentUser(request).getId(), translations.getSurveyUid());
				        
						if (!success) return;
						allTranslationsById.remove(Integer.parseInt(id));
					}					
				}
			}					
			
			if (success)
			{
				result = "{\"success\": true}";	    
			}
		} catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
		
	    response.setStatus(HttpServletResponse.SC_OK);

		if (result.length() == 0) result = "{\"success\": false, \"m\": 0}";
		
        writer.print(result);
        
        writer.flush();
        writer.close();

	}
	
	@RequestMapping(value = "/translatetranslations", method = RequestMethod.GET)
	public void translateTranslations(HttpServletRequest request, Locale locale, HttpServletResponse response) throws NotAgreedToTosException, ForbiddenURLException, WeakAuthenticationException {
		String idsString = request.getParameter("translationIds");
		String[] ids = idsString.split("\\|");
		User user = sessionService.getCurrentUser(request);
		
		Translations translations = translationService.getTranslations(Integer.parseInt(ids[0]));
		Survey s = surveyService.getSurvey(translations.getSurveyId());
		if (user == null || !sessionService.userIsFormAdmin(s, user, request))
		{		
			throw new ForbiddenURLException();			
		}
		
		boolean translateTranlations;
		try {		
			translateTranlations = machineTranslationService.translateTranlations(ids, user,isUseECMT());
		} catch (Exception e) {
			translateTranlations = false;
		}
		
		PrintWriter writer = null;
		try {
            writer = response.getWriter();
        } catch (IOException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
		String result = "{\"success\":" + Boolean.toString(translateTranlations) + " }";	 
		writer.print(result);
        writer.flush();
        writer.close();
	}
	
	private boolean isUseECMT() {
		return useECMT != null && useECMT.equalsIgnoreCase("true");
	}

	@RequestMapping(value = "/canceltranslation", method = RequestMethod.GET)
	public void cancelTranslation(HttpServletRequest request, Locale locale, HttpServletResponse response) {
		String idString = request.getParameter("translationId");
		if (idString !=null) 
		{
			Integer id  = Integer.valueOf(idString); 
			Translations translations = translationService.getTranslations(id);
			if (translations.getRequested()) 
			{
				translations.setRequested(false);
				translationService.save(translations);
			}
		}
		PrintWriter writer = null;
		try {
            writer = response.getWriter();
        } catch (IOException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
		String result = "{\"success\": true}";	 
		writer.print(result);
        writer.flush();
        writer.close();
	}
	
	@RequestMapping(value = "/activatetranslation", method = RequestMethod.POST)
	public @ResponseBody SimpleResult activatetranslations(HttpServletRequest request, Locale locale) throws NotAgreedToTosException, WeakAuthenticationException {
		String id = request.getParameter("id");
		SimpleResult result = new SimpleResult();
		
		User u = sessionService.getCurrentUser(request);
		Translations t = translationService.getTranslations(Integer.parseInt(id));
		Survey s = surveyService.getSurvey(t.getSurveyId());		
		if (u == null || !sessionService.userIsFormAdmin(s, u, request))
		{		
			result.setSuccess(false);		
			return result;
		}
					
		result.setResult(adaptTranslation(id, true, request, locale));
		if (result.getResult().length() == 0)
		{
			result.setResult(resources.getMessage("message.TranslationMarkedForPublucation", null, "The translation will be part of the next publication.", locale));
			result.setSuccess(true);
			
			int translationId = Integer.parseInt(id);
			Translations translations = translationService.getTranslations(translationId);
			activityService.log(223, null, translations.getLanguage().getCode(), sessionService.getCurrentUser(request).getId(), translations.getSurveyUid());
		      
		} else {
			result.setSuccess(false);
		}
		return result;
	}
	
	@RequestMapping(value = "/deactivatetranslation", method = RequestMethod.POST)
	public @ResponseBody SimpleResult deactivatetranslations(HttpServletRequest request, Locale locale) throws NotAgreedToTosException, WeakAuthenticationException {
		String id = request.getParameter("id");
		SimpleResult result = new SimpleResult();
		
		User u = sessionService.getCurrentUser(request);
		Translations t = translationService.getTranslations(Integer.parseInt(id));
		Survey s = surveyService.getSurvey(t.getSurveyId());		
		if (u == null || !sessionService.userIsFormAdmin(s, u, request))
		{		
			result.setSuccess(false);		
			return result;
		}
		
		result.setResult(adaptTranslation(id,false, request, locale));
		if (result.getResult().length() == 0)
		{
			result.setResult(resources.getMessage("message.TranslationUnMarkedForPublucation", null, "The translation will not be part of the next publication.", locale));
			result.setSuccess(true);
			
			int translationId = Integer.parseInt(id);
			Translations translations = translationService.getTranslations(translationId);
			activityService.log(224, translations.getLanguage().getCode(), null, sessionService.getCurrentUser(request).getId(), translations.getSurveyUid());
	
		} else {
			result.setSuccess(false);
		}
		return result;
	}
	
	private String adaptTranslation(String id, boolean active, HttpServletRequest request, Locale locale)
	{
		Form form;
		try {
			form = sessionService.getForm(request, null, false, false);
		} catch (NoFormLoadedException ne)
		{
			logger.error(ne.getLocalizedMessage(), ne);
			return resources.getMessage("error.NoFormLoaded", null, "You have to load a survey before you can use this page!", locale);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return resources.getMessage("error.NoAccessToSurvey", null, "No Access", locale);
		}
		
		if (id != null)
		{
			int translationId = Integer.parseInt(id);
			Translations translations = translationService.getTranslations(translationId);
			
			if (!translations.getSurveyId().equals(form.getSurvey().getId()))
			{
				return resources.getMessage("error.TranslationWrongSurvey", null, "The translation does not belong to the loaded survey!", locale);
			}
			
			if (!translations.getActive().equals(active))
			{
				if (active)
				{
					if (!TranslationsHelper.isComplete(translations, form.getSurvey()))
					{
						return resources.getMessage("error.MissingTranslation", null, "The translation is not complete. Please add missing labels before activating.", locale);
					}
				}				
				
				translations.setActive(active);
				translationService.save(translations);
				
				surveyService.makeDirty(translations.getSurveyId());
			}
			
		}
		
		return "";
	}
	
	@RequestMapping(value = "/savetranslations", method = RequestMethod.POST)
	public ModelAndView savetranslations(@PathVariable String shortname, HttpServletRequest request, Locale locale) throws Exception {
		
		try {
		
			boolean dirty = false;
			String survey = request.getParameter("survey");
			int surveyId = Integer.parseInt(survey);
			
			User u = sessionService.getCurrentUser(request);
			Survey s = surveyService.getSurvey(surveyId, false, false, false, true);		
			if (u == null || !sessionService.userIsFormAdmin(s, u, request))
			{		
				throw new ForbiddenURLException();
			}
			
			List<Translations> translations = translationService.getTranslationsForSurvey(surveyId, true);
			List<Translations> changedtranslations = new ArrayList<>();
			Map<Integer, Translations> translationsById = new HashMap<>();
			Map<Integer, Map<String, String>> oldInfos = new HashMap<>();
			for (Translations t: translations)
			{
				translationsById.put(t.getId(), t);
				oldInfos.put(t.getId(), t.getInfo());
			}
			
			Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
	
			Iterator<String> iterator = parameterMap.keySet().iterator();
			
			List<Integer> completeTranslations = new ArrayList<>();
			for (Translations translations2 : translations) {
				if (translations2.getComplete())
				{
					completeTranslations.add(translations2.getId());
				}
			}					
			
			// pattern: trans#TranslationsID#KEY#TranslationID
			while (iterator.hasNext()) {
				String key = iterator.next();
				String[] values = parameterMap.get(key);
				if (key.startsWith("trans#")) {
					
					String[] elements = key.split("#");
					
					String strtranslationsId = elements[1];
					Integer translationsId = Integer.parseInt(strtranslationsId);
					key = elements[2];
					String label = "";
					if (values.length > 0) label = values[0];
					
					if (key.equalsIgnoreCase(Survey.CONFIRMATIONLINK) || key.equalsIgnoreCase(Survey.ESCAPELINK))
					{
						if (label.contains("<a"))
						{
							label = label.replaceAll("\\<.*?>","");
						}
					}					
					
					Translations ts = translationsById.get(translationsId);
					
					Translation t = null;
					if (elements.length > 3)
					{
						if (elements[3].equalsIgnoreCase("usefullink") || elements[3].equalsIgnoreCase("backgrounddocument"))
						{
							if (elements.length > 4)
							{
								t = ts.getTranslationById(elements[4]);
							} else {
								key = elements[2] + "#" + elements[3];
							}
						} else {
							t = ts.getTranslationById(elements[3]);
						}
					}
					
					if (label != null)
					{
						if (t == null)
						{
							t = new Translation(key, label, ts.getLanguage().getCode(), ts.getSurveyId(), ts);
							ts.getTranslations().add(t);
						}
						
						if (!changedtranslations.contains(ts))
						{
							changedtranslations.add(ts);
						}
						
						label = Tools.filterHTML(label);
						
						if (label != null && !XHTMLValidator.validate(label, servletContext, null))
						{
							throw new InvalidXHTMLException(label, label);
						}
						
						t.setLabel(label);
					}
				}
			}	
	
			for (Translations t : changedtranslations)
			{
				if (t.getActive())
				{
					dirty = true;
				}
				
				if (!TranslationsHelper.isComplete(t,s))
				{
					t.setActive(false);

					if (completeTranslations.contains(t.getId()))
					{
						completeTranslations.remove(t.getId());
					}
					
					if (completeTranslations.size() == 0)
					{
						ModelAndView result = translations(shortname, request, locale);
						String message = resources.getMessage("info.KeepOneCompleteTranslation", null, "There must be at least one complete translation left!", locale);
						result.addObject("error", message);
						return result;
					}
				}
				
				if (t.getLanguage().getCode().equals(s.getLanguage().getCode()))
				{
					TranslationsHelper.synchronizePivot(s, t);
					surveyService.update(s, false);
				}
				
				translationService.save(t);
				//activityService.log(227, oldInfos.get(t.getId()), t.getInfo(), sessionService.getCurrentUser(request).getId(), s.getUniqueId());
				activityService.logTranslations(227, t.getLanguage().getCode(), oldInfos.get(t.getId()), t.getInfo(), sessionService.getCurrentUser(request).getId(), s.getUniqueId());
			}
			
			if (dirty)
			{
				surveyService.makeDirty(Integer.parseInt(survey));
			}
			
			return new ModelAndView("redirect:/" + shortname + "/management/translations?saved=true");
					
		
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			ModelAndView result = translations(shortname, request, locale);
			String message = resources.getMessage("error.DuringSave", null, "There was a problem during save.", locale);
			result.addObject("error", message);
			return result;
		}
	}
	@RequestMapping(value = "/addtranslation", method = RequestMethod.POST)
	public ModelAndView addtranslations(@PathVariable String shortname, HttpServletRequest request, Locale locale) throws Exception {
		
		Form form;
		form = sessionService.getForm(request, shortname, false, false);
		
		User u = sessionService.getCurrentUser(request);
		if (u == null || !sessionService.userIsFormAdmin(form.getSurvey(), u, request))
		{		
			throw new ForbiddenURLException();
		}
		
		String code = request.getParameter("lang");
		boolean translationRequested = false;
		String parameterTranslationrequested = request.getParameter("request");
		if (parameterTranslationrequested != null && parameterTranslationrequested.equalsIgnoreCase("on"))
		{
			translationRequested = true;
		}		
		
		if (code.equalsIgnoreCase("other")) code =  request.getParameter("code");
		
		List<Language> languages = surveyService.getLanguages();
		
		List<Translations> existingTranslations = translationService.getTranslationsForSurvey(form.getSurvey().getId(),false);
		
		for (Language language : languages)
		{
			if (language.getCode().equalsIgnoreCase(code))
			{
				for (Translations existingTranslation: existingTranslations)
				{
					if (existingTranslation.getLanguage().getCode().equalsIgnoreCase(language.getCode()))
					{
						ModelAndView result = translations(shortname, request, locale);
						String message = resources.getMessage("message.TranslationAlreadyExists", null, "There already exists a translation for this language.", locale);
						result.addObject("message", message);
						return result;
					}
				}
				
				Translations newTranslation = new Translations();
				newTranslation.setLanguage(language);
				newTranslation.setSurveyId(form.getSurvey().getId());
				newTranslation.setSurveyUid(form.getSurvey().getUniqueId());
				if (translationRequested ) 
				{
					if (isUseECMT()) 
					{
						// only request translation for official EU languages
						if (language.isOfficial()) 
						{
							newTranslation.setRequested(true);
						}
					}
					else 
					{
						newTranslation.setRequested(true);
					} 					
				}
				
				if (form.getSurvey().getConfirmationPage().equalsIgnoreCase(Survey.CONFIRMATIONTEXT))
				{
					String confirmation = resources.getMessage("message.confirmation", null, Survey.CONFIRMATIONTEXT, new Locale(language.getCode()));
					newTranslation.getTranslations().add(new Translation(Survey.CONFIRMATIONPAGE, confirmation, language.getCode(), form.getSurvey().getId(), newTranslation));
				}

				if (form.getSurvey().getEscapePage().equalsIgnoreCase(Survey.ESCAPETEXT))
				{
					String escape = resources.getMessage("message.escape", null, Survey.ESCAPETEXT, new Locale(language.getCode()));
					newTranslation.getTranslations().add(new Translation(Survey.ESCAPEPAGE, escape, language.getCode(), form.getSurvey().getId(), newTranslation));
				}
				
				translationService.add(newTranslation);
				if (translationRequested ) 
				{
					Integer sourceTranslationsId = null; 
					List<Translations> allTanslations = translationService.getTranslationsForSurvey(form.getSurvey().getId(),true);
					for (Translations translations : allTanslations) 
					{
						if (translations.getComplete()) {
							sourceTranslationsId = translations.getId();
							break ;
						}
					}
					if (sourceTranslationsId != null) {
						String[] ids = new String[2] ;
						ids[0] = sourceTranslationsId.toString();
						ids[1] = newTranslation.getId().toString();
						User user = sessionService.getCurrentUser(request);
						try {
							if (machineTranslationService.translateTranlations(ids, user,isUseECMT()))
							{
								activityService.log(228, null, language.getCode(), sessionService.getCurrentUser(request).getId(), form.getSurvey().getUniqueId());
							} else {
								ModelAndView result = translations(shortname, request, locale);
								String message = resources.getMessage("error.UnsupportedLanguage", null, "This language is not supported.", locale);
								result.addObject("message", message);
								return result;
							}
						} catch (Exception e) {
							ModelAndView result = translations(shortname, request, locale);
							result.addObject("message", e.getMessage());
							return result;
						}
					}
				}
				activityService.log(221, null, language.getCode(), sessionService.getCurrentUser(request).getId(), form.getSurvey().getUniqueId());
				
				if (isUseECMT() && !language.isOfficial()) {
					return new ModelAndView("redirect:/" + shortname + "/management/translations?error=RequestTranslation");
				} else {
					return new ModelAndView("redirect:/" + shortname + "/management/translations");
				}
			}
		}	
		
		return new ModelAndView("redirect:/" + shortname + "/management/translations?error=LanguageNotRecognized");
	}
	
	@RequestMapping(value = "/importtranslation", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String importtranslation(@PathVariable String shortname, HttpServletRequest request, HttpServletResponse response, Locale locale) throws IOException, NotAgreedToTosException, ForbiddenURLException, WeakAuthenticationException {
		ImportTranslationResult result = new ImportTranslationResult();
		
		ObjectMapper mapper = new ObjectMapper();

		Form form;
		try {
			form = sessionService.getForm(request, shortname, false, false);
		} catch (NoFormLoadedException ne)
		{
			logger.error(ne.getLocalizedMessage(), ne);
			return "";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
        	result.setSuccess(false);
        	
        	String message = resources.getMessage("error.NoAccessToSurvey", null, "No Access", locale);
			
        	result.setMessage(message);
    		return mapper.writeValueAsString(result);    
		}		

		User u = sessionService.getCurrentUser(request);
		if (u == null || !sessionService.userIsFormAdmin(form.getSurvey(), u, request))
		{		
			throw new ForbiddenURLException();
		}
		
		InputStream inputStream = null;
        String filename;
        
        try {
        
	        if (request instanceof DefaultMultipartHttpServletRequest)
	        {
	        	DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest)request;        	
	        	filename = FileUtils.cleanFilename(r.getFile("qqfile").getOriginalFilename());        	
	        	inputStream = r.getFile("qqfile").getInputStream();        	
	        } else {
	        	filename = FileUtils.cleanFilename(request.getHeader("X-File-Name"));
	        	inputStream = request.getInputStream();
	        }                        
            
	        if (inputStream != null) {
	           	 
	        	Translations translations = null;
	        	
	        	List<String> invalidKeys = new ArrayList<>();
	        		        	
                if (filename.toLowerCase().endsWith("xml"))
                {
                	translations = TranslationsHelper.importXML(inputStream, surveyService);
                } else if (filename.toLowerCase().endsWith("xls"))
                {
                	translations = TranslationsHelper.importXLS(inputStream, invalidKeys, surveyService, servletContext);
                } else if (filename.toLowerCase().endsWith("ods"))
                {
                	translations = TranslationsHelper.importODS(inputStream, invalidKeys, surveyService, servletContext);
                }
	        	
            	if (translations == null || !translations.getSurveyId().equals(form.getSurvey().getId()))
            	{
            		result.setSuccess(false);
            		
            		String message = resources.getMessage("error.TranslationFileInvalid", null, "The translation file seems not to be valid for this survey.", locale);
            		result.setMessage(message);
            		
            		return mapper.writeValueAsString(result); 
            	} else {
            	
	            	Translations existingTranslations = translationService.getTranslations(form.getSurvey().getId(), translations.getLanguage().getCode());
	            	
	            	result.setSuccess(true);
            		result.setExists(existingTranslations != null || translations.getLanguage().getCode().equalsIgnoreCase(form.getSurvey().getLanguage().getCode()));
            		
            		ArrayList<String> pivotlabels = new ArrayList<>();
            		ArrayList<String> keys = new ArrayList<>();
            		ArrayList<String> labels = new ArrayList<>();
            		
            		Translations pivot = TranslationsHelper.getTranslations(form.getSurvey(), false);
            		
            		Map<String, Translation> pivotTranslationsByKey = pivot.getTranslationsByKey();
            		Map<String, Translation> translationsByKey = translations.getTranslationsByKey();
            		Map<String, Translation> existingTranslationsByKey = existingTranslations != null ? existingTranslations.getTranslationsByKey() : new HashMap<>();
            		
            		for (Translation translation: pivot.getTranslations())
            		{            			
            			if (existingTranslationsByKey.containsKey(translation.getKey()))
            			{
            				pivotlabels.add(existingTranslationsByKey.get(translation.getKey()).getLabel());
            			} else {
            				pivotlabels.add(translation.getLabel());
            			}
            			
            			if (translationsByKey.containsKey(translation.getKey()))
            			{
            				labels.add(translationsByKey.get(translation.getKey()).getLabel());
            			} else {
            				labels.add("");
            			}           
            			
            			keys.add(translation.getKey());
            		}
            		
            		for (String key: translationsByKey.keySet())
            		{
            			if (!pivotTranslationsByKey.containsKey(key))
            			{
            				result.setIgnored(true);
            				break;
            			}
            		}
            		
            		result.setPivotLabels(pivotlabels.toArray(new String[pivotlabels.size()]));
            		result.setKeys(keys.toArray(new String[keys.size()]));
            		result.setLabels(labels.toArray(new String[labels.size()]));
            		result.setLanguage(translations.getLanguage().getCode());
            		result.setSurveyId(translations.getSurveyId());
            		result.setInvalidKeys(invalidKeys.toArray(new String[invalidKeys.size()]));
            		
            		String uid = UUID.randomUUID().toString();
            		result.setUid(uid);
            		
            		request.getSession().setAttribute(uid, translations);
            		
            		return mapper.writeValueAsString(result);            		
            	}
                
	        }        

        } catch (Exception ex) {
        	logger.error(ex.getMessage(), ex);
        	result.setSuccess(false);
    		String message = resources.getMessage("error.TranslationFileInvalid", null, "The translation file seems not to be valid for this survey.", locale);
    		result.setMessage(message);
    		return mapper.writeValueAsString(result);            
        } finally {
            try {
            	if (inputStream != null )
            	{
            		inputStream.close();
            	}
            } catch (IOException ignored) {
            }
        }
        
        return null;
	}
	
	@RequestMapping(value = "/importtranslation2", method = RequestMethod.POST)
	public ModelAndView importtranslation2(@PathVariable String shortname, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {		
		
		try {
		
			String uid = request.getParameter("uid");
			Translations translations = (Translations) request.getSession().getAttribute(uid);
			
			if (translations != null)
			{
				Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request); 
	
				Iterator<String> iterator = parameterMap.keySet().iterator();
				
				List<String> keys = new ArrayList<>();
				
				while (iterator.hasNext()) {
					String key = iterator.next(); // key is the question id
					String[] values = parameterMap.get(key);
					if (key.startsWith("check")) {
						keys.add(values[0]);
					}
				}
				
				List<Translation> candidates = new ArrayList<>();

                candidates.addAll(translations.getTranslations());
		
				for (Translation translation: candidates)
				{
					if (!keys.contains(translation.getKey()))
					{
						translations.getTranslations().remove(translation);
					}
				}
				
				Form form;
				try {
					form = sessionService.getForm(request, shortname, false, false);
				} catch (NoFormLoadedException ne)
				{
					logger.error(ne.getLocalizedMessage(), ne);
					ModelAndView model = new ModelAndView("error/generic");
					String message = resources.getMessage("error.NoFormLoaded", null, "You have to load a survey before you can use this page!", locale);
					model.addObject("message", message);
					return model;
				}
				
				User u = sessionService.getCurrentUser(request);
				if (u == null || !sessionService.userIsFormAdmin(form.getSurvey(), u, request))
				{		
					throw new ForbiddenURLException();
				}
				
				Translations existingTranslations = translationService.getTranslations(form.getSurvey().getId(), translations.getLanguage().getCode());
				Map<String, String> oldInfo = null;
				if (existingTranslations != null)
            	{
            		oldInfo = existingTranslations.getInfo();
            	}
				if (translations.getLanguage().getCode().equalsIgnoreCase(form.getSurvey().getLanguage().getCode()))
				{
					TranslationsHelper.synchronizePivot(form.getSurvey(), translations);
					surveyService.update(form.getSurvey(), true, true, true, sessionService.getCurrentUser(request).getId());
					//activityService.log(227, oldInfo, translations.getInfo(), sessionService.getCurrentUser(request).getId(), form.getSurvey().getUniqueId());
					activityService.logTranslations(227, translations.getLanguage().getCode(), oldInfo, translations.getInfo(), sessionService.getCurrentUser(request).getId(), form.getSurvey().getUniqueId());
										
					return new ModelAndView("redirect:/" + shortname + "/management/translations?done=1");					
				} else if (existingTranslations == null)
            	{
            		//save it
					translationService.add(translations);
            		activityService.log(221, null, translations.getLanguage().getCode(), sessionService.getCurrentUser(request).getId(), form.getSurvey().getUniqueId());
            		
            		if (translations.getActive())
            		{
            			surveyService.makeDirty(form.getSurvey().getId());
            		}
            		
            		return new ModelAndView("redirect:/" + shortname + "/management/translations?done=2");            		
            	} else {
            		//synchronize
            		TranslationsHelper.synchronize(existingTranslations, translations);
            		boolean dirty = existingTranslations.getActive();
            		if (TranslationsHelper.isComplete(existingTranslations, form.getSurvey()))
            		{
            			dirty = true;
            		}
            		translationService.save(existingTranslations);  
            		if (dirty)
            		{
            			surveyService.makeDirty(form.getSurvey().getId());
            		}
            		//activityService.log(227, oldInfo, existingTranslations.getInfo(), sessionService.getCurrentUser(request).getId(), form.getSurvey().getUniqueId());
            		activityService.logTranslations(227, existingTranslations.getLanguage().getCode(), oldInfo, existingTranslations.getInfo(), sessionService.getCurrentUser(request).getId(), form.getSurvey().getUniqueId());
    				
            		return new ModelAndView("redirect:/" + shortname + "/management/translations?done=1");
            	}      
			}
			return new ModelAndView("redirect:/" + shortname + "/management/translations");
			
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
		
		ModelAndView result = translations(shortname, request, locale);
		String message = resources.getMessage("error.TranslationCouldNotBeSaved", null, "The translation could not be saved!", locale);
    	result.addObject("message", message);
		return result;
	}
	
	@RequestMapping(value = "/downloadtranslation", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView downloadtranslation(@PathVariable String shortname, @RequestParam("id") String id, @RequestParam("format") String format, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		Form form;
		form = sessionService.getForm(request, shortname, false, false);
		
		Translations translations = null;
		if (id.equalsIgnoreCase("0"))
		{
			//export survey itself
			translations = TranslationsHelper.getTranslations(form.getSurvey(), false);
		} else {
			translations = translationService.getTranslations(Integer.parseInt(id));
		}
			
		if (translations != null)
		if (format.equalsIgnoreCase("xml"))
		{
			java.io.File xml = TranslationsHelper.getXML(form.getSurvey(), translations, xsllink, fileService);
			try {
				response.setContentLength((int) xml.length());
				response.setHeader("Content-Disposition","attachment; filename=\"" + form.getSurvey().getShortname() + "-" + translations.getLanguage().getCode() + ".xml\"");
				try {
					FileCopyUtils.copy(new FileInputStream(xml), response.getOutputStream());
				} catch (IOException e) {
					logger.error(e.getLocalizedMessage(), e);
				}		
			} catch (Exception e1) {
				logger.error(e1.getLocalizedMessage(), e1);
			}			
		} else if (format.equalsIgnoreCase("xls"))
		{
			java.io.File xml = TranslationsHelper.getXLS(form.getSurvey(), translations, resources, locale, fileService);
			try {
				response.setContentLength((int) xml.length());
				response.setHeader("Content-Disposition","attachment; filename=\"" + form.getSurvey().getShortname() + "-" + translations.getLanguage().getCode() + ".xls\"");
				try {
					FileCopyUtils.copy(new FileInputStream(xml), response.getOutputStream());
				} catch (IOException e) {
					logger.error(e.getLocalizedMessage(), e);
				}		
			} catch (Exception e1) {
				logger.error(e1.getLocalizedMessage(), e1);
			}			
		} else if (format.equalsIgnoreCase("ods"))
		{
			java.io.File xml = TranslationsHelper.getODS(form.getSurvey(), translations, resources, locale, fileService);
			try {
				response.setContentLength((int) xml.length());
				response.setHeader("Content-Disposition","attachment; filename=\"" + form.getSurvey().getShortname() + "-" + translations.getLanguage().getCode() + ".ods\"");
				try {
					FileCopyUtils.copy(new FileInputStream(xml), response.getOutputStream());
				} catch (IOException e) {
					logger.error(e.getLocalizedMessage(), e);
				}		
			} catch (Exception e1) {
				logger.error(e1.getLocalizedMessage(), e1);
			}			
		}

		return null;
	}


	
	/***
	 * Check if the Automatic Translation is available (for oss)
	 * @return
	 */	
	public void initMTCheck(){
		
		logger.info("TranslationController initMTCheck Called");
		
		if (StringUtils.isEmpty(useECMT)){
			isMTAvailable= false;
			return;
		}
			
		logger.info("TranslationController useECMT Not Empty " + useECMT);
		
		
		// if true then check if microsoft properties is set
		// else if false then check that EC Mt is set
		if (isUseECMT()){
			logger.info("TranslationController useECMT True Check For MTServiceWSDL " + mtServiceWsdl);
			isMTAvailable = StringUtils.isNotEmpty(mtServiceWsdl);	
		}else{
			logger.info("TranslationController useECMT False Check For msClientId " + msClientId + " CHECK " +!msClientId.trim().equalsIgnoreCase("your account here"));
			logger.info("TranslationController useECMT False Check For msClientId " + msClientId + " CHECK result is " +(StringUtils.isNotEmpty(msClientId) && !msClientId.trim().equalsIgnoreCase("your account here")));
			isMTAvailable= (StringUtils.isNotEmpty(msClientId) && !msClientId.trim().equalsIgnoreCase("your account here"));
		}
	}
}
