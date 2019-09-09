package com.ec.survey.controller;

import com.ec.survey.model.Form;
import com.ec.survey.model.Skin;
import com.ec.survey.model.SkinElement;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.SessionService;
import com.ec.survey.service.SkinService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.tools.FileUtils;
import com.ec.survey.tools.NotAgreedToTosException;
import com.ec.survey.tools.Ucs2Utf8;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("/settings")
public class SkinController extends BasicController {
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="skinService")
	private SkinService skinService;
	
	@Resource(name="sessionService")
	private SessionService sessionService;	
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
		
	@RequestMapping(value = "/skin", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView skins(HttpServletRequest request, Locale locale) throws NotAgreedToTosException{
		User user = sessionService.getCurrentUser(request);
		
		List<Skin> skins = null;
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2)
		{
			skins = skinService.getAll();
		} else if (user.getGlobalPrivileges().get(GlobalPrivilege.ECAccess) >= 1)
		{
			skins = skinService.getAll(user.getId());
		} else {
			skins = skinService.getAllButEC(user.getId());
		}

		ModelAndView result = new ModelAndView("settings/skins","skins", skins);
		
		if (request.getParameter("used") != null && request.getParameter("used").equalsIgnoreCase("true"))
		{
			result.addObject("message", resources.getMessage("error.SkinInUse", null, "This skin is being used in a survey and can therefore not be deleted.", locale));
		}
		
		return result;
	}
	
	@RequestMapping(value = "/skin/publicnameexists", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String exists(HttpServletRequest request)
	{
		String name = request.getParameter("name");
		String id = request.getParameter("id");
		
		List<Integer> ids = skinService.get(name);
		
		if (ids.size() > 0)
		{
			if (id.trim().length() > 0 && ids.size() == 1 && ids.get(0) == Integer.parseInt(id))
			{
				return "0";
			}
			return "1";
		}
		
		return "0";
	}
	
	@RequestMapping(value = "/skin/new", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String newSkin(Locale locale, Model model, HttpServletRequest request) throws NotAgreedToTosException {	
		
		Survey demoSurvey = surveyService.getSurvey("SkinDemo", true, false, false, false, null, true, false);
		Form form = new Form(resources);
		form.setSurvey(demoSurvey);
		
		Skin skin = new Skin();
		skin.setName(skinService.getNameForNewSkin(sessionService.getCurrentUser(request).getLogin()));
		model.addAttribute("skin", skin);
		model.addAttribute("form", form);
		return "settings/skin";
	}
	
	@RequestMapping(value = "/skin/edit/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView editSkin(@PathVariable String id, Locale locale, HttpServletRequest request) throws NotAgreedToTosException {	
		
		User user = sessionService.getCurrentUser(request);
		Survey demoSurvey = surveyService.getSurvey("SkinDemo", true, false, false, false, null, true, false);
		Form form = new Form(resources);
		form.setSurvey(demoSurvey);
		
		Skin skin = skinService.get(Integer.parseInt(id));
		
		if (skin.getOwner().getId().equals(user.getId()) || user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2)
		{
			ModelAndView model = new ModelAndView("settings/skin");
			model.addObject("skin", skin);
			model.addObject("form", form);
			return model;
		}
		
		return new ModelAndView("redirect:/errors/500.html");
	}
	
	@RequestMapping(value = "/skin/delete/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView deleteSkin(@PathVariable String id, Locale locale, HttpServletRequest request) throws NotAgreedToTosException {	
		
		User user = sessionService.getCurrentUser(request);
		
		Skin skin = skinService.get(Integer.parseInt(id));
		
		if (skin.getOwner().getId().equals(user.getId()) || user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2)
		{
			try {
				skinService.delete(skin);
			} catch (org.springframework.dao.DataIntegrityViolationException de)
			{
				return new ModelAndView("redirect:/settings/skin?used=true");
			} catch (Exception e)
			{
				return new ModelAndView("redirect:/errors/500.html");	
			}
			return new ModelAndView("redirect:/settings/skin");
		}
		
		return new ModelAndView("redirect:/errors/500.html");
	}
	
	@RequestMapping(value = "/skin/copy/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView copySkin(@PathVariable String id, Locale locale, HttpServletRequest request) throws NotAgreedToTosException {	
		
		User user = sessionService.getCurrentUser(request);
		Survey demoSurvey = surveyService.getSurvey("SkinDemo", true, false, false, false, null, true, false);
		Form form = new Form(resources);
		form.setSurvey(demoSurvey);
		
		Skin skin = skinService.get(Integer.parseInt(id));
		
		if (skin.getIsPublic() || skin.getOwner().getId().equals(user.getId()) || user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2)
		{
			Skin copy = new Skin();
			copy.setName(skin.getName() + " (copy)");
			copy.getElements().clear();
			for (SkinElement element: skin.getElements())
			{
				copy.getElements().add(element.copy());
			}
			
			ModelAndView model = new ModelAndView("settings/skin");
			model.addObject("skin", copy);
			model.addObject("form", form);
			return model;
		}
		
		return new ModelAndView("redirect:/errors/500.html");
	}
	
	@RequestMapping(value = "/skin/save", method = RequestMethod.POST)
	public ModelAndView saveSkin(@ModelAttribute Skin skin, BindingResult bindingresult, HttpServletRequest request, Locale locale) throws NotAgreedToTosException {	
		
		User user = sessionService.getCurrentUser(request);
		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
		
		for (SkinElement element: skin.getElements())
		{
			if (parameterMap.containsKey("background-color" + element.getName())) {
				element.setBackgroundColor(parameterMap.get("background-color" + element.getName())[0]);
			}
			if (parameterMap.containsKey("color" + element.getName())) {
				element.setColor(parameterMap.get("color" + element.getName())[0]);
			}
			if (parameterMap.containsKey("font-family" + element.getName())) {
				element.setFontFamily(parameterMap.get("font-family" + element.getName())[0]);
			}
			if (parameterMap.containsKey("font-size" + element.getName())) {
				element.setFontSize(parameterMap.get("font-size" + element.getName())[0]);
			}
			if (parameterMap.containsKey("font-weight" + element.getName())) {
				element.setFontWeight(parameterMap.get("font-weight" + element.getName())[0]);
			}
			if (parameterMap.containsKey("font-style" + element.getName())) {
				element.setFontStyle(parameterMap.get("font-style" + element.getName())[0]);
			}
		}	
		
		if (skin.getId() != null && skin.getId() > 0)
		{
			Skin existingSkin = skinService.get(skin.getId());
			if (!existingSkin.getOwner().getId().equals(user.getId()) && user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2)
			{
				return new ModelAndView("error/generic", "message", resources.getMessage("error.SkinUnauthorized", null, "You are not authorized to edit this skin.", locale));
			}
			
			if (!existingSkin.getName().equalsIgnoreCase(skin.getName()))
			{
				if (skinService.nameAlreadyExists(skin.getName(), user.getId()))
				{
					Survey demoSurvey = surveyService.getSurvey("SkinDemo", true, false, false, false, null, true, false);
					Form form = new Form(resources);
					form.setSurvey(demoSurvey);
					ModelAndView model = new ModelAndView("settings/skin");
					model.addObject("skin", skin);
					model.addObject("form", form);
					model.addObject("message", resources.getMessage("error.NameAlreadyUsed", null, "This name already exists. Please choose a unique one.", locale));
					return model;
				}
			}
			
			Session session = sessionFactory.getCurrentSession();
			session.evict(existingSkin);			
		}
		
		if (skin.getName().trim().length() == 0)
		{
			Survey demoSurvey = surveyService.getSurvey("SkinDemo", true, false, false, false, null, true, false);
			Form form = new Form(resources);
			form.setSurvey(demoSurvey);
			ModelAndView model = new ModelAndView("settings/skin");
			model.addObject("skin", skin);
			model.addObject("form", form);
			model.addObject("message", resources.getMessage("error.ChoseName", null, "Please choose a unique name.", locale));
			return model;
		}
		
		if (skin.getOwner() == null)
		{
			skin.setOwner(user);
		}
		
		skinService.save(skin);
		return new ModelAndView("redirect:/settings/skin");
	}
	
	@RequestMapping(value = "/skin/download/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView download(@PathVariable String id, Locale locale, HttpServletRequest request, HttpServletResponse response) throws NotAgreedToTosException {	
		
		User user = sessionService.getCurrentUser(request);
		int skinId = Integer.parseInt(id);
		
		if (skinId > 0)
		{
			Skin skin = skinService.get(skinId);
			
			if (skin.getIsPublic() || skin.getOwner().getId().equals(user.getId()) || user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2)
			{			
				String filename = skin.getName();
				if (!filename.toLowerCase().endsWith(".euss")) filename += ".euss";
				String fileAttHeader = String.format("attachment;filename=%s", filename);
				response.setHeader("Content-Disposition", fileAttHeader);
				try {
					response.getOutputStream().write(skin.getCss().getBytes());
					response.flushBuffer();	
					return null;
				} catch (IOException e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			} else {
				return new ModelAndView("error/generic", "message", resources.getMessage("error.SkinDownloadUnauthorized", null, "You are not authorized to download this skin", locale));
			}
					
		}
		
		return null;
	}
	
	@RequestMapping(value = "/skin/upload")
	public void upload(Locale locale, HttpServletRequest request, HttpServletResponse response) throws NotAgreedToTosException {	
		User user = sessionService.getCurrentUser(request);
		
		String error = resources.getMessage("error.FileImportFailed", null, "The file could not be imported.", locale);
		
		PrintWriter writer = null;
        InputStream is = null;
       
        try {
            writer = response.getWriter();
        } catch (IOException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }

        String filename;
        
        try {
        
	        if (request instanceof DefaultMultipartHttpServletRequest)
	        {
	        	DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest)request;        	
	        	filename = r.getFile("qqfile").getOriginalFilename();        	
	        	is = r.getFile("qqfile").getInputStream();        	
	        } else {
	        	filename = request.getHeader("X-File-Name");
	        	is = request.getInputStream();
	        }
	        
	        filename = FileUtils.cleanFilename(java.net.URLDecoder.decode(filename, "UTF-8"));
	   
	        if (!filename.toLowerCase().endsWith(".euss"))
	        {
	        	writer.print("{\"success\": false, \"message\": \"invalidfiletype\"}");
	        } else {	        
		        filename = filename.replace(".euss","");
		        
		        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		        TreeMap<String, TreeMap<String, String>> elements = scanCSS(bufferedReader);
		        bufferedReader.close();
	
		        Skin skin = new Skin();
		        skin.setName(filename);
		        skin.setOwner(user);
		        
		        boolean allElementsOK = true;
		        for (String element: elements.keySet())
		        {
		        	for (String key: elements.get(element).keySet())
		        	{
		        		String value = elements.get(element).get(key);
		        		boolean elementOK = skin.setElementValue(element, key, value);
		        		allElementsOK = allElementsOK && elementOK;
		        	}
		        }	        
		        
		        //check name	        
		        if (skinService.nameAlreadyExists(skin.getName(), user.getId()))
		        {
		        	int counter = 1;
		        	String name = filename + "(" + counter++ + ")";
		        	while (skinService.nameAlreadyExists(name, user.getId()))
		        	{
		        		name = filename + "(" + counter++ + ")";
		        	}
		        }
		        
		        skinService.save(skin);	        
	                                    
	            response.setStatus(HttpServletResponse.SC_OK);
	            
	            writer.print("{\"success\": true, \"id\": \"" + skin.getId().toString() + "\"}");            
	        }
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.print("{\"success\": false, \"message\": \"" + error + "\"}");
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }

        writer.flush();
        writer.close();
	
	}
	
	public TreeMap<String, TreeMap<String, String>> scanCSS(BufferedReader reader) {
		
		String line;     
		String name;
		String value;
		TreeMap<String, String> currentElementMap = null;
		TreeMap<String, TreeMap<String, String>> elements = new TreeMap<>();
		String currentElement = "";
		try {
		  while ((line = reader.readLine()) != null) {
			  line = line.trim().replace("<", "").replace(">", "");
			  if (!line.startsWith("/*") && line.length() > 0)
		      {
				  if (line.contains("﻿"))
				  {
					  
				  } else if (line.contains("{"))
		    	  {
					  //a new element starts
					  currentElement = line.substring(0, line.indexOf('{')-1).trim();
		    		  currentElementMap = new TreeMap<>();
		    	  } else if (line.contains("}"))
		    	  {
		    		  //an element is closed
		    		  elements.put(currentElement, currentElementMap);
		    		  currentElement = "";
		    		  currentElementMap = null;		    		  
		    	  } else 
		    	  {
		    		  //name: value;
		    		  if (line.contains(":"))
		    		  {
		    			  name = line.substring(0, line.indexOf(':')).trim();
		    			  value = line.substring(line.indexOf(':')+1).replace(";", "").trim();
		    			  currentElementMap.put(name, value);
		    		  }
		    	  }
		      }		                
		  }
		  
		} catch(IOException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
			return null;
		}		
		
		return elements;
	}
		
}
