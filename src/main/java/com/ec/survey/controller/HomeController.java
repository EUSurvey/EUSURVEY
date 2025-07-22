package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.InvalidURLException;
import com.ec.survey.exception.MessageException;
import com.ec.survey.model.*;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
import com.ec.survey.service.mapping.PaginationMapper;
import com.ec.survey.tools.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Controller("homeController")
public class HomeController extends BasicController {
	
	public @Value("${stresstests.createdata}") String createStressData;
	private @Value("${server.prefix}") String host;
	
	private @Value("${support.recipient}") String supportEmail;
	private @Value("${support.recipientinternal}") String supportEmailInternal;

	private @Value("${support.smIncidentHost}") String incidentHost;
	private @Value("${support.smAttachmentHost:#{null}}") String attachmentHost;
	private @Value("${support.smBasicAuth:#{null}}") String smtpAuth;

	@Autowired
	protected PaginationMapper paginationMapper;    
		
	@Resource(name="machineTranslationService")
	MachineTranslationService machineTranslationService;
	
	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;
	
	@Resource(name="mailService")
	private MailService mailService;
	
	@RequestMapping(value = "/home/about", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String about(Locale locale, ModelMap model) {
		model.put("continueWithoutJavascript", true);		
		model.put("oss", super.isOss());		
		return "home/about";
	}
	
	@RequestMapping(value = "/home/about/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String aboutRunner(Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("runnermode", true);
		model.addAttribute("oss", super.isOss());
		return "home/about";
	}
	
	@RequestMapping(value = "/home/delphi", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String delphi(
			final ModelMap model, final @RequestParam(value = "survey", required = false) String uidOrShortname) {

		Survey survey = surveyService.getSurvey(uidOrShortname, false, true, false, false, null, true, true);
		if (survey == null) {
			survey = surveyService.getSurvey(uidOrShortname, true, true, false, false, null, true, true);
		}
		if (survey != null) {
			model.put("contact", survey.getContact());
			model.put("fixedContact", survey.getFixedContact());
		}

		model.put("continueWithoutJavascript", true);
		model.put("oss", super.isOss());
		return "home/delphi";
	}
	
	@RequestMapping(value = "/home/delphi/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String delphiRunner(Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("runnermode", true);
		model.addAttribute("oss", super.isOss());
		return "home/delphi";
	}
	
	@RequestMapping(value = "/home/download", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String download(Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("oss",super.isOss());
		return "home/download";
	}
	
	@RequestMapping(value = "/home/download/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String downloadRunner(Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("runnermode", true);
		model.addAttribute("oss",super.isOss());
		return "home/download";
	}
	
	@RequestMapping(value = "/home/documentation", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String documentation(Locale locale, ModelMap model) {
		if (isShowEcas()) model.addAttribute("showecas", true);
		// CASOSS
		if (isCasOss()) model.addAttribute("casoss", true);
		model.put("continueWithoutJavascript", true);
		return "home/documentation";
	}
	
	@RequestMapping(value = "/home/documentation/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String documentationRunner(Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("runnermode", true);
		return "home/documentation";
	}
	
	private String getBrowserInformation(HttpServletRequest request, Locale locale)
	{
		StringBuilder result = new StringBuilder();
		
		if (request.getParameter(Constants.ERROR) != null)
		{
			String url = (String) request.getSession().getAttribute("lastErrorURL");
			if (url != null)
			{
				result.append("URL: ").append(url).append("\n");
			}
			Integer code = (Integer) request.getSession().getAttribute("lastErrorCode");
			if (code != null)
			{
				result.append("HTTP Code: ").append(code).append("\n");
			}
			Date time = (Date) request.getSession().getAttribute("lastErrorTime");
			if (time != null)
			{
				result.append("Error time: ").append(time).append("\n");
			}
		}
		result.append("Browser: ").append(request.getHeader("User-Agent")).append("\n");
		result.append("Language: ").append(locale.getLanguage()).append("\n");
		return result.toString();
	}
	
	@RequestMapping(value = "/home/support", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String support(HttpServletRequest request, Locale locale, ModelMap model) {
		model.put("continueWithoutJavascript", true);
		model.put("additionalinfo", getBrowserInformation(request, locale));
		
		
		model.addAttribute("oss",super.isOss());
		return "home/support";
	}
	
	@RequestMapping(value = "/home/support/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String supportRunner(HttpServletRequest request, Locale locale, Model model) {
		model.addAttribute("continueWithoutJavascript", true);
		model.addAttribute("runnermode", true);
		model.addAttribute("additionalinfo", getBrowserInformation(request, locale));
		model.addAttribute("oss",super.isOss());
		return "home/support";
	}
	
	@PostMapping(value = "/home/support")
	public String supportPOST(HttpServletRequest request, Locale locale, ModelMap model) throws IOException, MessageException {		
		if (!checkCaptcha(request))
		{
			model.put("wrongcaptcha", true);
			return "home/support";
		}
		
		String email = ConversionTools.removeHTML(request.getParameter(Constants.EMAIL), true);
		
		//String SMTServiceEnabled = settingsService.get(Setting.UseSMTService);
		//if (email.toLowerCase().endsWith("ec.europa.eu") && SMTServiceEnabled != null && SMTServiceEnabled.equalsIgnoreCase("true") && incidentHost != null){
			return sendSupportSmt(request, locale, model, !incidentHost.endsWith("wsdl"));
		//} else {
		//	return sendSupportEmail(request, locale, model);
		//}
	}
	
	private String GetLabelForReason(String reason, Locale locale) {
		switch (reason) {
			case "generalquestion":
				return resources.getMessage("support.GeneralQuestion", null, reason, locale);
			case "technicalproblem":
				return resources.getMessage("support.TechnicalProblem", null, reason, locale);
			case "idea":
				return resources.getMessage("support.idea", null, reason, locale);
			case "assistance":
				return resources.getMessage("support.assistance", null, reason, locale);
			case "accessibility":
				return resources.getMessage("support.Accessibility", null, reason, locale);
			case "dataprotection":
				return resources.getMessage("support.DataProtection", null, reason, locale);
			case "highaudience":
				return resources.getMessage("support.HighAudience", null, reason, locale);
			case "protection":
				return resources.getMessage("support.Protection", null, reason, locale);
			case "organisation":
				return resources.getMessage("support.Organisation", null, reason, locale);
			default:
				return resources.getMessage("support.otherreason", null, reason, locale);
		}			
	}
	
	private String GetSmtLabelForReason(String reason) {
		switch (reason) {
			case "technicalproblem":
				return "INCIDENT";
			case "idea":
				return "REQUEST FOR CHANGE";
			case "assistance":
			case "highaudience":
				return "REQUEST FOR SERVICE";
			default:
				return "REQUEST FOR INFORMATION";
		}		
	}

	private String sendSupportEmail(HttpServletRequest request, Locale locale, ModelMap model) throws IOException, MessageException {
		String reason = ConversionTools.removeHTML(request.getParameter("contactreason"), true);
		String name = ConversionTools.removeHTML(request.getParameter("name"), true);
		String email = ConversionTools.removeHTML(request.getParameter(Constants.EMAIL), true);
		String subject = ConversionTools.removeHTML(request.getParameter("subject"), true);
		String message = ConversionTools.removeHTML(request.getParameter(Constants.MESSAGE), true);
		String additionalinfo  = request.getParameter("additionalinfo");
		String additionalsurveyinfotitle = request.getParameter("additionalsurveyinfotitle");
		String additionalsurveyinfoalias = request.getParameter("additionalsurveyinfoalias");
		String[] uploadedfiles = request.getParameterValues("uploadedfile");

		StringBuilder body = new StringBuilder();
		body.append("Dear helpdesk team,<br />please open a ticket and assign it to DIGIT EUSURVEY SUPPORT.<br />Thank you in advance<br/><hr /><br />");

		body.append("<table>");

		body.append("<tr><td>Affected user:</td><td>").append(name).append("</td></tr>");
		body.append("<tr><td>Email address:</td><td>").append(email).append("</td></tr>");

		body.append("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");

		boolean additioninfo = false;
		if (additionalsurveyinfotitle != null && additionalsurveyinfotitle.length() > 0) {
			body.append("<tr><td>Survey Title:</td><td>").append(additionalsurveyinfotitle).append("</td></tr>");
			additioninfo = true;
		}
		if (additionalsurveyinfoalias != null && additionalsurveyinfoalias.length() > 0) {

			String link = host + "runner/" + additionalsurveyinfoalias;

			body.append("<tr><td>Survey Alias:</td><td><a href='").append(link).append("'>").append(additionalsurveyinfoalias).append("</a></td></tr>");
			additioninfo = true;
		}
		if (additioninfo)
		{
			body.append("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");
		}

		body.append("<tr><td>Reason:</td><td>").append(GetLabelForReason(reason, locale)).append("</td></tr>");
		body.append("<tr><td>Subject:</td><td>").append(subject).append("</td></tr>");

		body.append("</table>");

		body.append("<br />Message text:<br />").append(message).append("<br /><br />");
		if (additionalinfo != null)
		{
			body.append(ConversionTools.escape(additionalinfo).replace("\n", "<br />"));
		}

		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]", host);

		java.io.File attachment1 = null;
		java.io.File attachment2 = null;
		if (uploadedfiles != null && uploadedfiles.length > 0)
		{
			attachment1 = fileService.getTemporaryFile(uploadedfiles[0]);
			if (uploadedfiles.length > 1)
			{
				attachment2 = fileService.getTemporaryFile(uploadedfiles[1]);
			}
		}

		if (email.toLowerCase().endsWith("ec.europa.eu"))
		{
			mailService.SendHtmlMail(supportEmailInternal, sender, sender, subject, text, attachment1, attachment2, null, true);
		} else {
			mailService.SendHtmlMail(supportEmail, sender, sender, subject, text, attachment1, attachment2, null, true);
		}

		model.put("messagesent", true);
		model.put("additionalinfo", getBrowserInformation(request, locale));
		return "home/documentation";
	}

	private String sendSupportSmt(HttpServletRequest request, Locale locale, ModelMap model, boolean useJSON) throws IOException, MessageException {
		String reason = ConversionTools.removeHTML(request.getParameter("contactreason"), true);
		String name = ConversionTools.removeHTML(request.getParameter("name"), true);
		String email = ConversionTools.removeHTML(request.getParameter(Constants.EMAIL), true);
		String subject = ConversionTools.removeHTML(request.getParameter("subject"), true);
		String message = ConversionTools.removeHTML(GetLabelForReason(reason, locale) + ": " + request.getParameter(Constants.MESSAGE), true);
		String additionalinfo  = ConversionTools.removeHTML(request.getParameter("additionalinfo"), true);
		String additionalsurveyinfotitle = ConversionTools.removeHTML(request.getParameter("additionalsurveyinfotitle"), true);
		String additionalsurveyinfoalias = ConversionTools.removeHTML(request.getParameter("additionalsurveyinfoalias"), true);
		String login = "";
		
		boolean external = !email.toLowerCase().endsWith("ec.europa.eu");
		InputStream inputStreamXML = servletContext.getResourceAsStream("/WEB-INF/Content/createIncident.xml");
		InputStream inputStreamJSON = external ? servletContext.getResourceAsStream("/WEB-INF/Content/createIncidentExternal.json") : servletContext.getResourceAsStream("/WEB-INF/Content/createIncident.json");
		String createTemplate = IOUtils.toString(useJSON ? inputStreamJSON : inputStreamXML, "UTF-8");
		
		CloseableHttpClient httpclient = HttpClients.createSystem();	
		
		try {
			
			if (!external) {
				//get login from ldap
				login = ldapService.getLoginForEmail(email);
				createTemplate = createTemplate.replace("[CALLER]", login);
			}				
	
			createTemplate = createTemplate.replace("[MESSAGE]", message);
			createTemplate = createTemplate.replace("[ADDITIONALINFOUSERNAME]", name);
			createTemplate = createTemplate.replace("[ADDITIONALINFOEMAIL]", email);
			createTemplate = createTemplate.replace("[ADDITIONALINFO]", additionalinfo);
			createTemplate = createTemplate.replace("[ADDITIONALINFOSURVEYTITLE]", additionalsurveyinfotitle);
			createTemplate = createTemplate.replace("[ADDITIONALINFOSURVEYALIAS]", additionalsurveyinfoalias);
			createTemplate = createTemplate.replace("[SUBJECT]", subject);		
			createTemplate = createTemplate.replace("[REASON]", GetSmtLabelForReason(reason));
			createTemplate = createTemplate.replace("[BUSINESSSERVICE]", "EU Survey Solutions");
			createTemplate = createTemplate.replace("[SERVICEOFFERING]", "EU Survey - General issue");

			sessionService.initializeProxy();
			
			HttpPost httppost = new HttpPost(incidentHost);
			httppost.addHeader("Content-type", useJSON ? "application/json" : "text/xml;charset=UTF-8");
			
			if (!useJSON) {
				httppost.addHeader("SOAPAction", "Create");
			}

			if (smtpAuth != null) {
				httppost.addHeader("Authorization", "Basic " + smtpAuth);
			}

			httppost.setEntity(new StringEntity(createTemplate));
			
			CloseableHttpResponse response = httpclient.execute(httppost);
			
			try {
			
				HttpEntity entity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();
			
				String strResponse = entity == null ? "" : EntityUtils.toString(entity, "UTF-8");
				if (useJSON) {						
					if (statusCode != 200 && statusCode != 201)
					{
						logger.error(statusCode + " " + strResponse);
						throw new MessageException("Calling ServiceNow UAT failed.");
					}
					
					String[] uploadedfiles = request.getParameterValues("uploadedfile");
					String[] uploadedfilenames = request.getParameterValues("uploadedfilename");
					
					java.io.File attachment1 = null;
					java.io.File attachment2 = null;
					if (uploadedfiles != null && uploadedfiles.length > 0)
					{
						JSONObject jsonResponse = new JSONObject(strResponse).getJSONObject("result");
						String sys_id = jsonResponse.getString("sys_id");
						
						attachment1 = fileService.getTemporaryFile(uploadedfiles[0]);
						addAttachment(sys_id, uploadedfilenames[0], attachment1, httpclient);
						if (uploadedfiles.length > 1)
						{
							attachment2 = fileService.getTemporaryFile(uploadedfiles[1]);
							addAttachment(sys_id, uploadedfilenames[1], attachment2, httpclient);
						}
					}
					
				} else if (!strResponse.toLowerCase().contains("message=\"success\"")) {
					logger.error(statusCode + " " +strResponse);
					throw new MessageException("Calling SMT web service failed.");
				}
				logger.info(statusCode + " " + strResponse);				
			
			} finally{
			   response.close();
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			//fallback to email
			return sendSupportEmail(request, locale, model);
		} finally {
			httpclient.close();
		}

		model.put("messagesent", true);
		model.put("additionalinfo", getBrowserInformation(request, locale));
		return "home/documentation";
	}
	
	private void addAttachment(String sys_id, String filename, java.io.File attachment, CloseableHttpClient httpclient) throws ClientProtocolException, IOException, MessageException {
			
		HttpPost httppostFile = new HttpPost(attachmentHost + sys_id + "&file_name=" + URLEncoder.encode(filename, "UTF-8"));
		httppostFile.setHeader("Content-Type", "application/octet-stream");
	
		if (smtpAuth != null) {
			httppostFile.addHeader("Authorization", "Basic " + smtpAuth);
		}
		
		FileEntity entityAttachment = new FileEntity(attachment, ContentType.APPLICATION_OCTET_STREAM);
		httppostFile.setEntity(entityAttachment);
		
		CloseableHttpResponse responseFile = httpclient.execute(httppostFile);
		
		try {
			
			HttpEntity entityFile = responseFile.getEntity();
			String strResponseFile = entityFile == null ? "" : EntityUtils.toString(entityFile, "UTF-8");
			int statusCodeFile = responseFile.getStatusLine().getStatusCode();
			
			if (statusCodeFile != 200 && statusCodeFile != 201)
			{
				logger.error(statusCodeFile + " " + strResponseFile);
				throw new MessageException("Calling ServiceNow UAT failed.");
			}
		} finally{
		   responseFile.close();
		}
	}
	
	@RequestMapping(value = "/home/support/deletefile", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody String deletefile(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			String uid = request.getParameter("uid");	
			
			java.io.File file = fileService.getTemporaryFile(uid);
			
			if (Files.deleteIfExists(file.toPath()))
			{
				return "{\"success\": true}";
			}			
					
		} catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error(ex.getMessage(), ex);
        }

		return "{\"success\": false}";
	}	
	
	@PostMapping(value = "/home/support/uploadfile")
	public void uploadFile(HttpServletRequest request, HttpServletResponse response) {

		PrintWriter writer = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            writer = response.getWriter();
        } catch (IOException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            return;
        }

        String filename;       
        
        try {
        
	        if (request instanceof DefaultMultipartHttpServletRequest)
	        {
	        	DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest)request;        	
	        	filename = com.ec.survey.tools.FileUtils.cleanFilename(java.net.URLDecoder.decode(r.getFile("qqfile").getOriginalFilename(), "UTF-8"));        	
	        	is = r.getFile("qqfile").getInputStream();        	
	        } else {
	        	filename = com.ec.survey.tools.FileUtils.cleanFilename(java.net.URLDecoder.decode(request.getHeader("X-File-Name"), "UTF-8"));
	        	is = request.getInputStream();
	        }
        	
            java.io.File file = fileService.getTemporaryFile();          
        	String uid = file.getName();
        			
            fos = new FileOutputStream(file);
            IOUtils.copy(is, fos);
                                  
        	response.setStatus(HttpServletResponse.SC_OK);
        	writer.print("{\"success\": true, \"id\": '" + uid + "', \"uid\": '" + uid + "', \"longdesc\": '', \"comment\": '', \"width\": '', \"name\": '" + filename + "'}");
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.print("{\"success\": false}");
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
            	if (fos != null) {
            		fos.close();
            	}
            	if (is != null) {
            		is.close();
            	}
            } catch (IOException ignored) {
            	//ignore
            }
        }

        writer.flush();
        writer.close();
	}
	
	@RequestMapping(value = "/home/helpparticipants", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String helpparticipants(HttpServletRequest request, Locale locale, Model model) {
		return helpparticipantsinternal(request, locale, model);
	}
	
	@RequestMapping(value = "/home/helpparticipants/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String helpparticipantsrunner(HttpServletRequest request, Locale locale, Model model) {
		model.addAttribute("runnermode", true);		
		return helpparticipantsinternal(request, locale, model);
	}
	
	private String helpparticipantsinternal(HttpServletRequest request, Locale locale, Model model) {
		
		model.addAttribute("runnermode", true);
		model.addAttribute("page", "helpparticipants");
		
		if (request.getParameter("faqlanguage") != null)
		{
			String lang = request.getParameter("faqlanguage");
			if (lang.equalsIgnoreCase("de"))
			{
				model.addAttribute("faqlanguage","de");
				return "home/helpparticipantsde";
			} else if (lang.equalsIgnoreCase("fr"))
			{
				model.addAttribute("faqlanguage","fr");
				return "home/helpparticipantsfr";
			} else if (lang.equalsIgnoreCase("en"))
			{
				model.addAttribute("faqlanguage","en");
				return "home/helpparticipants";
			}
		}
		
		if (locale.getLanguage().equals(new Locale("de").getLanguage()))
		{
			model.addAttribute("faqlanguage","de");
			return "home/helpparticipantsde";
		} else if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
		{
			model.addAttribute("faqlanguage","fr");
			return "home/helpparticipantsfr";
		}
		
		model.addAttribute("faqlanguage","en");
		return "home/helpparticipants";
	}
	
	@RequestMapping(value = "/home/helpauthors/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String helpauthorsrunner(HttpServletRequest request, Locale locale, Model model) {
		model.addAttribute("runnermode", true);		
		return helpauthorsinternal(request, locale, model);
	}
	
	@RequestMapping(value = "/home/helpauthors", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String helpauthors(HttpServletRequest request, Locale locale, Model model) {
		return helpauthorsinternal(request, locale, model);
	}
	
	private String helpauthorsinternal(HttpServletRequest request, Locale locale, Model model) {
		
		model.addAttribute("runnermode", true);
		
		if (request.getParameter("faqlanguage") != null)
		{
			String lang = request.getParameter("faqlanguage");
			if (lang.equalsIgnoreCase("de"))
			{
				model.addAttribute("faqlanguage","de");
				return "home/helpauthorsde";
			} else if (lang.equalsIgnoreCase("fr"))
			{
				model.addAttribute("faqlanguage","fr");
				return "home/helpauthorsfr";
			} else if (lang.equalsIgnoreCase("en"))
			{
				model.addAttribute("faqlanguage","en");
				return "home/helpauthors";
			}
		}
		
		if (locale.getLanguage().equals(new Locale("de").getLanguage()))
		{
			model.addAttribute("faqlanguage","de");
			return "home/helpauthorsde";
		} else if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
		{
			model.addAttribute("faqlanguage","fr");
			return "home/helpauthorsfr";
		}
		
		model.addAttribute("faqlanguage","en");
		return "home/helpauthors";
	}
	
	@RequestMapping(value = "/home/privacystatement", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String privacystatement(HttpServletRequest request, Locale locale, Model model) {
		return privacystatementinternal(request, locale, model, "ps");
	}
	
	@RequestMapping(value = "/home/privacystatement/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String privacystatementrunner(HttpServletRequest request, Locale locale, Model model) {
		model.addAttribute("runnermode", true);		
		return privacystatementinternal(request, locale, model, "ps");
	}
	
	@RequestMapping(value = "/home/tos", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String tos(HttpServletRequest request, Locale locale, Model model) {
		return privacystatementinternal(request, locale, model, "tos");
	}
	
	@RequestMapping(value = "/home/tos/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String tosrunner(HttpServletRequest request, Locale locale, Model model) {
		model.addAttribute("runnermode", true);		
		return privacystatementinternal(request, locale, model, "tos");
	}

	@RequestMapping(value = "/home/dpa", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String dpa(HttpServletRequest request, Locale locale, Model model) {
		return privacystatementinternal(request, locale, model, "dpa");
	}

	@RequestMapping(value = "/home/dpa/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String dparunner(HttpServletRequest request, Locale locale, Model model) {
		return privacystatementinternal(request, locale, model, "dpa");
	}
	
	@RequestMapping(value = "/home/accessibilitystatement", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String accessibilitystatement(HttpServletRequest request, Locale locale, Model model) {
		return privacystatementinternal(request, locale, model, "as");
	}
	
	@RequestMapping(value = "/home/accessibilitystatement/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String accessibilitystatementrunner(HttpServletRequest request, Locale locale, Model model) {
		return privacystatementinternal(request, locale, model, "as");
	}
	
	private String privacystatementinternal(HttpServletRequest request, Locale locale, Model model, String internal) {
		if (request.getParameter("language") != null)
		{
			String lang = request.getParameter("language");
			if (lang.equalsIgnoreCase("de"))
			{
				model.addAttribute("language","de");
			} else if (lang.equalsIgnoreCase("fr"))
			{
				model.addAttribute("language","fr");
			} else if (lang.equalsIgnoreCase("en"))
			{
				model.addAttribute("language","en");
			}
		} else {		
			if (locale.getLanguage().equals(new Locale("de").getLanguage()))
			{
				model.addAttribute("language","de");
			} else if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
			{
				model.addAttribute("language","fr");
			}
		}
		
		model.addAttribute("readonly", true);
		model.addAttribute("user",request.getSession().getAttribute("USER"));
		model.addAttribute("oss",super.isOss());

		switch(internal) {
			case "ps":
				return "auth/ps";
			case "tos":
				return "auth/tos";
			case "dpa":
				return "home/dpa";
			case "as":
				return "home/accessibilitystatement";
			default:
				return "error/info";
		}
	}
		
	@RequestMapping(value = "/home/welcome", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView welcome(HttpServletRequest request) {	
		return basicwelcome(request);
	}
	
	@RequestMapping(value = Constants.PATH_DELIMITER, method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView home(HttpServletRequest request) {
		return basicwelcome(request);
	}
	
	@RequestMapping(value = "/home/welcome/runner", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView welcomerunner(HttpServletRequest request) {	
		return basicwelcome(request);
	}
	
	@RequestMapping(value = "/home/editcontribution")
	public String editcontribution (HttpServletRequest request, Locale locale, Model model) throws ForbiddenURLException {
		model.addAttribute("lang", locale.getLanguage());
		model.addAttribute("runnermode", true);
		
		String code = request.getParameter("code");
		if (code == null)
		{
			code = "";
		} else if (!Tools.isUUID(code)) {
			throw new ForbiddenURLException();
		}
		model.addAttribute("uniqueid", code);
		
		return "home/accesscontribution";
	}
	
	private boolean checkValid(WrongAttempts attempts)
	{
		if (attempts == null || attempts.getCounter() < 10 || attempts.getLockDate() == null) return true;
		
		Calendar cal = Calendar.getInstance();  
			
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(attempts.getLockDate());
		cal2.add(Calendar.DAY_OF_MONTH, +1);  
		
		return cal.after(cal2);
	}	
	
	@PostMapping(value = "/home/editcontribution")
	public ModelAndView editcontributionPost(HttpServletRequest request, Locale locale) {		
		try {
			
			String remoteAddr = request.getRemoteAddr();
			if (request.getHeader("X-Forwarded-For") != null && !request.getHeader("X-Forwarded-For").equalsIgnoreCase("0.0.0.0")) remoteAddr += " (" + request.getHeader("X-Forwarded-For") + ")";
						
			WrongAttempts attempts = answerService.getWrongAttempts(remoteAddr);
			
			ModelAndView result = new ModelAndView("home/accesscontribution");
		
			if (!checkValid(attempts))
			{
				result.addObject(Constants.MESSAGE, resources.getMessage("message.IPblocked", null, "Your IP is blocked after 10 wrong attempts!", locale));
				result.addObject("runnermode", true);
				return result;
			}
			
			//check captcha
			if (!checkCaptcha(request))
			{
	        	//after 10 bad requests the IP is locked for 24 hours
	        	if (attempts == null) attempts = new WrongAttempts(remoteAddr);
	        	attempts.increaseCounter();
	        	answerService.save(attempts);
	        	result.addObject("captchaerror", resources.getMessage("message.captchawrongnew", null, "The CAPTCHA code is not correct!", locale));
				result.addObject("runnermode", true);
				return result;
	        }
		
			//get answerset
	        String uniqueCode = request.getParameter(Constants.UNIQUECODE);	
	        
	        if (uniqueCode != null) uniqueCode = uniqueCode.trim();
	        
	        AnswerSet answerSet = null;
			
			if (uniqueCode != null && uniqueCode.length() > 0)
			{			
				answerSet = answerService.get(uniqueCode);
				
				if (answerSet == null)
				{
					answerSet = answerService.getByInvitationCode(uniqueCode);
				}				
			} 
			
			if (answerSet == null) {
				//after 10 bad requests the IP is locked for 24 hours
	        	if (attempts == null) attempts = new WrongAttempts(remoteAddr);
	        	attempts.increaseCounter();
	        	answerService.save(attempts);
				result.addObject(Constants.MESSAGE, resources.getMessage("message.contributionidwrong", null, "The case code is not correct!", locale));
				result.addObject("runnermode", true);
				return result;
	        }

			return new ModelAndView("redirect:/editcontribution/" + answerSet.getUniqueCode());
		
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			ModelAndView result = new ModelAndView("home/accesscontribution", Constants.MESSAGE, resources.getMessage("message.contributionerror", null, "Loading of the contribution not possible!", locale));
			result.addObject("runnermode", true);
			return result;
		}
	}
	
	@RequestMapping(value = "/home/downloadcontribution", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView downloadcontributionGet(HttpServletRequest request) throws Exception {	
		ModelAndView model = new ModelAndView("home/welcome");
		model.addObject("page", "welcome");
		model.addObject("ecasurl", ecashost);
		model.addObject("require2fa", require2fa);
		model.addObject("serviceurl", serverPrefix + "auth/ecaslogin");
		model.addObject("continueWithoutJavascript", true);
		if (isShowEcas())
			model.addObject("showecas", true);
		// CASOSS
		if (isCasOss())
			model.addObject("casoss", true);

		String code = request.getParameter("code");
		model.addObject("code", code);
		
		String email = request.getParameter("email");
		model.addObject("email", email);
		
		model.addObject("showDownloadPdfDialog", true);

		return model;
	}

	@PostMapping(value = "/home/downloadcontribution", headers = "Accept=*/*")
	public @ResponseBody String downloadcontribution(HttpServletRequest request, HttpServletResponse response) {
		try {
			
			if (!checkCaptcha(request))
			{
				return "errorcaptcha";
			}
			
			Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
			String code = parameters.get("caseid")[0];
			
			AnswerSet answerSet = answerService.get(code);
			if (answerSet != null) {			
				
				if (!answerSet.getSurvey().getDownloadContribution())
				{
					return "errorcaseidforbidden";
				}
				
//				if (answerSet.getInvitationId() != null && answerSet.getInvitationId().length() > 0)
//				{
//					return "errorcaseidinvitation";
//				}
				
				String email = parameters.get(Constants.EMAIL)[0];
				
				logger.info("starting creation of answer pdf for contribution " + code + " to be sent to " + email);
								
				if (answerSet.getSurvey().getIsQuiz())
				{
					QuizExecutor export = (QuizExecutor) context.getBean("quizExecutor");
					export.init(answerSet, email, sender, serverPrefix);
					taskExecutor.execute(export);
				} else {				
					AnswerExecutor export = (AnswerExecutor) context.getBean("answerExecutor");
					export.init(answerSet, email, sender, serverPrefix, request.getSession().getAttribute("passwordauthentication") != null);
					taskExecutor.execute(export);
				}
			} else {
				return "errorcaseid";
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return Constants.ERROR;
		}

		return "success";
	}
	
	@RequestMapping(value = "/home/publicsurveys/runner")
	public ModelAndView publicsurveysrunner(HttpServletRequest request) throws Exception {	
		ModelAndView result = publicsurveys(request);
			result.addObject("runnermode", true);
		return result;
	}
	
	@RequestMapping(value = "/home/publicsurveys")
	public ModelAndView publicsurveys(HttpServletRequest request) throws Exception {	
		
		if (!enablepublicsurveys.equalsIgnoreCase("true"))
		{
			throw new InvalidURLException();
		}
		
		SurveyFilter filter = sessionService.getSurveyFilter(request, false);
		filter.setUser(null);		
		String newPage = request.getParameter("newPage");				
		newPage = newPage == null ? "1" : newPage;
			
		int rowsPerPage = 10;
           	
		Paging<Survey> paging = new Paging<>();
		paging.setItemsPerPage(rowsPerPage);
		int numberOfSurveys = 0; 
		paging.setNumberOfItems(numberOfSurveys);
		paging.moveTo(newPage);
		
		String sortKey = request.getParameter("sort");
		if (sortKey != null && sortKey.trim().length() > 1)
		{
			sortKey = sortKey.trim();
			
			if (sortKey.equalsIgnoreCase("publication") || sortKey.equalsIgnoreCase("created") )
				filter.setSortKey("survey_created");
			else if (sortKey.equalsIgnoreCase("expiration"))
			{
				filter.setSortKey("survey_end_date");
				filter.setSortOrder("DESC");
			}
			else if (sortKey.equalsIgnoreCase("popularity"))
				filter.setSortKey("replies");
		} else {
			filter.setSortKey("survey_created");
		}

		SqlPagination sqlPagination = paginationMapper.toSqlPagination(paging);
		List<Survey> surveys = surveyService.getSurveys(filter, sqlPagination);
		paging.setItems(surveys);
		
		request.getSession().setAttribute("lastPublicSurveyFilter", filter);
		
		ModelAndView result = new ModelAndView("home/publicsurveys", "paging", paging);    	
    	result.addObject("isPublic", true);
    	result.addObject(Constants.FILTER, filter);
    	
    	//get most popular surveys
    	SurveyFilter popfilter = new SurveyFilter();
    	popfilter.setUser(null);	
    	List<Survey> popularSurveys = surveyService.getPopularSurveys(popfilter);
    	result.addObject("popularSurveys", popularSurveys);
    	
    	return result;
	}
	
	@RequestMapping(value = "/home/publicsurveysjson", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody List<Survey> publicsurveysjson(HttpServletRequest request) throws Exception {	
		
		if (!enablepublicsurveys.equalsIgnoreCase("true"))
		{
			throw new InvalidURLException();
		}
		
		int itemsPerPage = 10;
		int newPage = 1;
		
		String rows = request.getParameter("rows");	
		try
		{
			if (rows != null)
			{
				itemsPerPage = Integer.parseInt(rows);
			}
		} catch (NumberFormatException e)
		{
			itemsPerPage = 10;
		}		
		
		String page = request.getParameter("page");		
		try
		{
			if (page != null)
			{
				newPage = Integer.parseInt(page);
			}
		} catch (NumberFormatException e)
		{
			newPage = 1;
		}
		
		SurveyFilter filter = (SurveyFilter) request.getSession().getAttribute("lastPublicSurveyFilter");

		SqlPagination sqlPagination = new SqlPagination(newPage, itemsPerPage);
		return surveyService.getSurveysIncludingTranslationLanguages(filter, sqlPagination, false, false);
	}
	
	@RequestMapping(value = "/validate/{id}/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String validate(@PathVariable String id, @PathVariable String code, Locale locale, Model model) {
		
		try {
			if (administrationService.validateUser(Integer.parseInt(id), code))
			{
				return "home/validated";			
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "error/validation";
	}
	
	@RequestMapping(value = "/deleteaccount/{id}/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String deleteaccount(HttpServletRequest request, @PathVariable String id, @PathVariable String code, Locale locale, Model model)  {	
		
		try {
			administrationService.confirmUserDeleteRequest(Integer.parseInt(id), code);
			model.addAttribute(Constants.MESSAGE, resources.getMessage("message.AccountDeleted", null, "Your account has been deleted!", locale));
			return "error/info";
		} catch (NumberFormatException nfe) {
			model.addAttribute(Constants.MESSAGE, resources.getMessage("error.UserNotFound", null, "User not found.", locale));
		} catch (Exception e) {
			switch (e.getMessage()) {
			case "User unknown":
				model.addAttribute(Constants.MESSAGE, resources.getMessage("error.UserNotFound", null, "User not found.", locale));
				break;
			case "Wrong code":
				model.addAttribute(Constants.MESSAGE, resources.getMessage("error.WrongCode", null, "The code is wrong.", locale));
				break;
			case "Request too old":
				model.addAttribute(Constants.MESSAGE, resources.getMessage("error.DeleteCodeOutdated", null, "You did not confirm the account deletion during the corresponding time span.", locale));
				break;
			default:
				logger.error(e.getLocalizedMessage(), e);
				break;
			}
		}
	
		return Constants.VIEW_ERROR_GENERIC;
	}
	
	@RequestMapping(value = "/validateNewEmail/{id}/{code}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public String validateNewEmail(HttpServletRequest request, @PathVariable String id, @PathVariable String code, Locale locale, Model model) {	
		try {
			if (administrationService.validateNewEmail(request, Integer.parseInt(id), code))
			{
				return "home/emailchangevalidated";
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "error/validation";
	}
	
	@GetMapping(value = "home/validatesurvey")
	public ModelAndView validatesurvey(HttpServletRequest request, Locale locale, HttpServletResponse response) throws MessageException {
		String surveyId = request.getParameter("id");
		String code = request.getParameter("code");
		
		if (surveyService.validate(Integer.parseInt(surveyId), code)) {
			ModelAndView model = new ModelAndView("error/info");
			String message = resources.getMessage("info.SurveyValidated", null, "The survey has been validated successfully.", locale);
			
			model.addObject(Constants.MESSAGE, message);
			model.addObject("contextpath", contextpath);
			
			return model;
		} else {
			throw new MessageException(resources.getMessage("error.SurveyValidated", null, "The survey could not be validated.", locale));
		}		
	}
	
	@GetMapping(value = "home/rejectsurvey")
	public ModelAndView rejectsurvey(HttpServletRequest request, Locale locale, HttpServletResponse response) throws MessageException, NumberFormatException, IOException {
		String surveyId = request.getParameter("id");
		String code = request.getParameter("code");
		
		if (surveyService.rejectsurvey(Integer.parseInt(surveyId), code)) {
			ModelAndView model = new ModelAndView("error/info");
			String message = resources.getMessage("info.SurveyRejected", null, "The survey has been rejected successfully.", locale);
			
			model.addObject(Constants.MESSAGE, message);
			model.addObject("contextpath", contextpath);
			
			return model;
		} else {
			throw new MessageException(resources.getMessage("error.SurveyRejected", null, "The survey could not be rejected.", locale));
		}		
	}
	 	
	@PostMapping(value = "home/notifySuccess")
	public void notifySuccess(HttpServletRequest request, Locale locale, HttpServletResponse response) {
		String requestId = request.getParameter("requestId");
		if (requestId == null) {
			requestId = request.getParameter("request-id");
		}
		logger.info("HomeController.notifySuccess called with for request " + requestId); // the actual translation is handled in returnTranslation
	}

	@PostMapping(value = "home/notifyError")
	public void notifyError(HttpServletRequest request) {
		String requestId = request.getParameter("requestId");
		if (requestId == null) {
			requestId = request.getParameter("request-id");
		  }
		String targetLanguage = request.getParameter("target-language");
		if (targetLanguage == null) {
			targetLanguage = "";
		}
		
		String errorCode = request.getParameter("error-code");
		String errorMessage = request.getParameter("error-message");

		logger.error("HomeController.notifyError called for the translation with request ID " + requestId);
		machineTranslationService.saveErrorResponse(requestId,targetLanguage,errorCode,errorMessage);
	}

	@PostMapping(value = "home/returnTranslation")
	public @ResponseBody String returnTranslation(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestId = request.getParameter("requestId");
		if (requestId == null) {
			requestId = request.getParameter("request-id");
		}

		logger.info("HomeController.returnTranslation called for the translation with request ID " + requestId);

		byte[] translationBytes = request.getInputStream().readAllBytes();
		String encodedString = new String(translationBytes);
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		String translationText = new String(decodedBytes);

		machineTranslationService.saveSuccessResponse(requestId, translationText);

		response.setStatus(200);
		return "OK";
	}
	
	@GetMapping(value = "/home/reportAbuse")
	public String reportAbuse (HttpServletRequest request, Locale locale, Model model) throws InvalidURLException {	
		model.addAttribute("lang", locale.getLanguage());
		model.addAttribute("runnermode", true);
		
		String surveyid = request.getParameter(Constants.SURVEY);
		if (surveyid == null || surveyid.trim().length() == 0)
		{
			throw new InvalidURLException();
		}
		
		try {
			int id = Integer.parseInt(surveyid);
			
			Survey survey = surveyService.getSurvey(id);
			
			if (survey == null)
			{
				throw new InvalidURLException();
			}
			
			model.addAttribute("AbuseSurvey", survey.getUniqueId());
			model.addAttribute("AbuseType", "");
			model.addAttribute("AbuseText", "");
			model.addAttribute("AbuseEmail", "");
			
		} catch (NumberFormatException e)
		{
			throw new InvalidURLException();
		}
		
		return "home/reportabuse";
	}
	
	@PostMapping(value = "home/reportAbuse")
	public ModelAndView reportAbusePOST(HttpServletRequest request, Locale locale, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView("home/reportabuse");
		
		String uid = request.getParameter("abuseSurvey");
		String type = request.getParameter("abuseType");
		String text = request.getParameter("abuseText");
		String email = request.getParameter("abuseEmail");
		
		Survey survey = surveyService.getSurveyByUniqueId(uid, false, true);
		
		if (survey == null)
		{
			throw new InvalidURLException();
		}
		
		if (!checkCaptcha(request)) {			
			model.addObject("wrongcaptcha", true);
			model.addObject("contextpath", contextpath);
			
			model.addObject("AbuseSurvey", uid);
			model.addObject("AbuseType", type);
			model.addObject("AbuseText", text);
			model.addObject("AbuseEmail", email);
			
			return model;
		}		
	
		logger.info("HomeController.reportAbuse called with abuseType " + type);
		
		surveyService.reportAbuse(survey, type, text, email);
		
		model = new ModelAndView("error/info");
		String message = resources.getMessage("info.ReportAbuseSent", null, "The abuse has been reported to the team in charge of the service.", locale);
		
		model.addObject(Constants.MESSAGE, message);
		model.addObject("contextpath", contextpath);
		
		String link = serverPrefix + "runner/" + survey.getShortname();
		model.addObject("SurveyLink", link);
		
		return model;
	}
	
}
