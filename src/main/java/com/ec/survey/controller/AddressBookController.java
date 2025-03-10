package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.MessageException;
import com.ec.survey.model.Paging;
import com.ec.survey.model.ParticipationGroup;
import com.ec.survey.model.ParticipationGroupsForAttendee;
import com.ec.survey.model.UploadItem;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.Attribute;
import com.ec.survey.model.attendees.AttributeName;
import com.ec.survey.service.*;
import com.ec.survey.tools.*;
import com.ec.survey.tools.activity.ActivityRegistry;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;

@Controller
@RequestMapping("/addressbook")
public class AddressBookController extends BasicController {
	
	@Resource(name="mailService")
	private MailService mailService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView attendees(HttpServletRequest request) throws Exception {
		User user = sessionService.getCurrentUser(request);		
		int ownerId;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
    	{
			ownerId = -1;
    	} else {
    		ownerId = user.getId();    		
    	}
		
		Paging<Attendee> paging = (Paging<Attendee>) request.getSession().getAttribute("attendees-paging");
		HashMap<String, String> filter = (HashMap<String, String>) request.getSession().getAttribute("attendees-filter");
		
		if (request.getParameter("clear") != null && request.getParameter("clear").equalsIgnoreCase("true"))
		{
			filter = null;
			paging = null;
		}
		
		if (filter == null) filter = new HashMap<>();
		
		if (paging == null)
		{
			paging = new Paging<>();
			paging.setItemsPerPage(50);
			paging.moveTo("1");
		}

		List<Integer> ids = new ArrayList<>();
		int numberOfAttendees = attendeeService.getNumberOfAttendees(ownerId, filter);
		paging.setNumberOfItems(numberOfAttendees);
		
		List<Attendee> attendees = attendeeService.getAttendees(ownerId, filter, paging.getCurrentPage(), paging.getItemsPerPage());
		paging.setItems(attendees);		
		
    	ModelAndView result = new ModelAndView("addressbook/addressbook", "paging", paging);
    	result.addObject("attributeNames", user.getSelectedAttributes());
    	
    	boolean ownerSelected = false;
    	if (user.getSelectedAttributes() != null)
    	{
	    	for (AttributeName name : user.getSelectedAttributes())
	    	{
	    		if (name.getName().equals("Owner"))
	    		{
	    			ownerSelected = true;
	    			break;
	    		}
	    	}
    	}
    	result.addObject("ownerSelected",ownerSelected);    	
    	result.addObject("allAttributeNames", attendeeService.getAllAttributes(ownerId));    	
    	result.addObject(Constants.FILTER, filter);
    	result.addObject("uploadItem", new UploadItem());
    	request.getSession().setAttribute("attendees-paging", paging.clean());
    	request.getSession().setAttribute("attendees-filter", filter);    	
    	request.getSession().removeAttribute("fileheaders");
    	
    	boolean deleted = false;
    	
    	if (request.getParameter("added") != null)
    	{
    		result.addObject("added", true);
    	} else if (request.getParameter(Constants.EDITED) != null && request.getParameter(Constants.EDITED).length() > 0)
		{
			if (!request.getParameter(Constants.EDITED).equalsIgnoreCase("batch"))
			{
				String[] idsstring = request.getParameter(Constants.EDITED).split(";");
				for (String id: idsstring)
				{
					ids.add(Integer.parseInt(id));
				}
			} else {
				result.addObject("editedAttendeesBatch", true);
			}
		} else if (request.getParameter(Constants.DELETED) != null && request.getParameter(Constants.DELETED).length() > 0) {
			if (!request.getParameter(Constants.DELETED).equalsIgnoreCase("batch"))
			{
				String[] idsstring = request.getParameter(Constants.DELETED).split(";");
				for (String id: idsstring)
				{
					ids.add(Integer.parseInt(id));
					deleted = true;
				}
				result.addObject("editedParticipationGroupsDeleted", true);    		
			} else {
				result.addObject("deletedAttendeesBatch", true);
			}
		}
		
		if (!ids.isEmpty())
		{						
    		Set<Integer> attendeeIds = new HashSet<>();
    		for (int id: ids)
    		{
	    		Attendee attendee = attendeeService.get(id);	    		
	    		attendeeIds.add(attendee.getOriginalId());
    		}
	    	List<ParticipationGroupsForAttendee> groups = participationService.getGroupsForAttendees(attendeeIds, user.getId());
	    	
	    	Set<Integer> idcounter = new HashSet<>();
	    	for (ParticipationGroupsForAttendee group : groups)
	    	{
	    		if (!idcounter.contains(group.getParticipationGroupId()))
	    		{
	    			idcounter.add(group.getParticipationGroupId());
	    		}
	    	}
	    	
	    	if (!groups.isEmpty())
	    	{
	    		result.addObject("editedParticipationGroups", groups);
	    		result.addObject("editedParticipationGroupsSize", idcounter.size());
	    	}  

    		result.addObject("editedAttendees", ids.size());
    		if (deleted) result.addObject("deletedcontacts", true);
    		result.addObject("editedattendeesids", StringUtils.collectionToCommaDelimitedString(ids));
		}
    	    	
    	return result;
	}
	
	@PostMapping(value = "/updateguestlists")
	public @ResponseBody String updateguestlists(@RequestParam("ids") String ids, HttpServletRequest request, Locale locale)
	{
		try {
			//replace attendees in guest lists and delete old
			String[] idsarray = ids.split(",");
			User user = sessionService.getCurrentUser(request);		
			for (String id : idsarray) {
				Attendee newAttendee = attendeeService.get(Integer.parseInt(id));
				
				if (newAttendee.getOriginalId().equals(newAttendee.getId()))
				{
					//delete
					Attendee originalAttendee = attendeeService.get(newAttendee.getOriginalId());
					
					List<ParticipationGroup> groups = participationService.getGroupsForAttendee(originalAttendee.getId());
					for (ParticipationGroup participationGroup : groups) {
						
						String oldValue = participationGroup.getId() + " - " + originalAttendee.getName() + " - " + originalAttendee.getEmail();
						String newValue = participationGroup.getId() + " - contact deleted";
														
						activityService.log(ActivityRegistry.ID_GUEST_LIST_CONTACT_CHANGED, oldValue, newValue, user.getId(), participationGroup.getSurveyUid());
					}					
					
					attendeeService.delete(newAttendee.getId());
				} else {
					//edit
					Attendee originalAttendee = attendeeService.get(newAttendee.getOriginalId());
					
					List<ParticipationGroup> groups = participationService.getGroupsForAttendee(originalAttendee.getId());
					for (ParticipationGroup participationGroup : groups) {
						participationGroup.getAttendees().remove(originalAttendee);
						participationGroup.getAttendees().add(newAttendee);
						participationService.update(participationGroup);
						
						String oldValue = participationGroup.getId() + " - " + originalAttendee.getName() + " - " + originalAttendee.getEmail();
						String newValue = participationGroup.getId() + " - " + newAttendee.getName() + " - " + newAttendee.getEmail();
														
						activityService.log(ActivityRegistry.ID_GUEST_LIST_CONTACT_CHANGED, oldValue, newValue, user.getId(), participationGroup.getSurveyUid());
					}
					
					attendeeService.delete(originalAttendee.getId());
					newAttendee.setOriginalId(null);
					attendeeService.update(newAttendee, false);
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return resources.getMessage("error.OperationFailed ", null, "Error during operation", locale);
		}
		
		return "OK";
	}
	
	@RequestMapping(value = "/attendeesjson", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody List<Attendee> attendeesjson(HttpServletRequest request) throws Exception {
		String rows = request.getParameter("rows");		
		int itemsPerPage = Integer.parseInt(rows);
		
		String page = request.getParameter("page");		
		int newPage = Integer.parseInt(page);
		
		User user = sessionService.getCurrentUser(request);		
		int ownerId;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
    	{
			ownerId = -1;
    	} else {
    		ownerId = user.getId();    		
    	}
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> filter = (HashMap<String, String>) request.getSession().getAttribute("attendees-filter");

		return attendeeService.getAttendees(ownerId, filter, newPage, itemsPerPage);
	}
	
	@GetMapping(value = "/checkNewAttendee", headers="Accept=*/*")
	public @ResponseBody String checkNewAttendee(HttpServletRequest request, HttpServletResponse response ) throws Exception {
		Map<String,String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		String email = parameters.get(Constants.EMAIL)[0];
		User user = sessionService.getCurrentUser(request);
		String ownerlogin = parameters.get(Constants.OWNER)[0];
		
		if (user.getGlobalPrivileges().get(GlobalPrivilege.UserManagement) > 1 && ownerlogin.length() > 0)
		{				
			User owner = administrationService.getUserForLogin(ownerlogin);
			if (owner == null)
			{			
				return "OWNERDOESNOTEXIST";
			}					
		}
		
		if (attendeeService.attendeeExists(email, user.getId())) {
			return "ATTENDEEEXISTS";
		}
		
		return "OK";
	}
	
	@PostMapping(value = "/batchEdit")
	public ModelAndView batchEditPOST(HttpServletRequest request, Locale locale) throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		User user = sessionService.getCurrentUser(request);		
		boolean userChanged = false;
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> filter = (HashMap<String, String>) request.getSession().getAttribute("attendees-filter");
		if (filter == null) filter = new HashMap<>();
			
		String owner = null;
		User ownerUser = null;
		String name = null;
		String email = null;
		
		Map<Integer, String> replacements = new HashMap<>();
		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
		for (Entry<String, String[]> entry : parameterMap.entrySet()) {
			if (entry.getKey().startsWith("attribute"))
			{
				String attributeId = entry.getKey().substring(9);
				String value = Tools.escapeHTML(entry.getValue()[0]);
				
				if (value.equalsIgnoreCase("0"))
				{
					//keep value
					continue;
				} else if (value.equalsIgnoreCase("-1"))
				{
					//clear value
					value = null;
				} else {
					//override values
				}
				
				replacements.put(Integer.parseInt(attributeId), value);
			} else if (entry.getKey().startsWith("newattribute"))
			{
				String attributeId = entry.getKey().substring(12);
				String attributeName = Tools.escapeHTML(entry.getValue()[0]);
				String value = parameterMap.get("newvalue" + attributeId)[0];
				
				AttributeName newAttributeName = new AttributeName();
				newAttributeName.setName(attributeName);
				newAttributeName.setOwnerId(user.getId());
				attendeeService.add(newAttributeName);
				
				user.getSelectedAttributes().add(newAttributeName);
				userChanged = true;
				
				replacements.put(newAttributeName.getId(), value);
			} else if (entry.getKey().equals("owner"))
			{
				owner = Tools.escapeHTML(entry.getValue()[0]);
				if (owner != null && owner.trim().length() > 0)
				{
					try {
						ownerUser = administrationService.getUserForLogin(owner);
					} catch (Exception e) {
						return new ModelAndView(Constants.VIEW_ERROR_GENERIC, Constants.MESSAGE, resources.getMessage("error.OwnerNotValid", null, "The selected owner is not a valid user", locale));
					}	
				}
			} else if (entry.getKey().equals("name"))
			{
				String value = Tools.escapeHTML(entry.getValue()[0]);
				if (value.equalsIgnoreCase("0"))
				{
					//keep value
				} else if (value.equalsIgnoreCase("-1"))
				{
					//clear value
					name = null;
				} else {
					//override values
					name = value;
				}
			} else if (entry.getKey().equals(Constants.EMAIL))
			{
				String value = Tools.escapeHTML(entry.getValue()[0]);
				if (value.equalsIgnoreCase("0"))
				{
					//keep value
				} else if (value.equalsIgnoreCase("-1"))
				{
					//clear value
					email = null;
				} else {
					//override values
					email = value;
				}
			}
		}		
		
		AttendeeUpdater updater = (AttendeeUpdater) context.getBean("attendeeUpdater");
		updater.initForUpdate(user, ownerUser, filter, parameterMap, replacements, name, email, locale);
		taskExecutorLong.execute(updater);
		
		if (userChanged)
		{
			administrationService.updateUser(user);
			sessionService.setCurrentUser(request, user);
		}
		
		return new ModelAndView("redirect:/addressbook?edited=batch");
	}

	@PostMapping(value = "/uploadAJAX")
	public void upload(HttpServletRequest request, HttpServletResponse response) {

		PrintWriter writer = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            writer = response.getWriter();
        } catch (IOException ex) {
            logger.error(ex);
        }
		if (writer != null) {
			String filename;

			try {

				if (request instanceof DefaultMultipartHttpServletRequest)
				{
					DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest)request;
					filename = FileUtils.cleanFilename(r.getFile("qqfile").getOriginalFilename());
					is = r.getFile("qqfile").getInputStream();
				} else {
					filename = FileUtils.cleanFilename(request.getHeader("X-File-Name"));
					is = request.getInputStream();
				}

				String uid = UUID.randomUUID().toString();

				User user = sessionService.getCurrentUser(request);
				java.io.File file = fileService.getUsersFile(user.getId(), uid);
				
				fos = new FileOutputStream(file);
				IOUtils.copy(is, fos);

				String delimiter = "comma";
				if (filename.toLowerCase().endsWith("csv"))
				{
					FileInputStream fis = new FileInputStream(file);
					String content = IOUtils.toString(fis);
					fis.close();
					if (StringUtils.countOccurrencesOf(content, ";") > StringUtils.countOccurrencesOf(content, ","))
					{
						delimiter = "semicolon";
					}
				} else {
					delimiter = "hide";
				}

				response.setStatus(HttpServletResponse.SC_OK);
				writer.print("{\"success\": true, \"uid\": '" + uid + "', \"delimiter\": '" + delimiter + "', \"name\": '" + Tools.encodeForJavaScript(filename) + "'}");
			} catch (Exception ex) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.print("{\"success\": false}");
				logger.error(ex.getMessage());
			} finally {
				try {
				    if (fos != null)
				    {
                        fos.close();
                    }
					if (is != null)
                    {
                        is.close();
                    }
				} catch (IOException ignored) {
					//ignore
				}
			}

			writer.flush();
			writer.close();
		}
	}
		
	@SuppressWarnings("unchecked")
	public ModelAndView importAttendees(HttpServletRequest request, Locale locale) {         
         ModelAndView result = null;

         String[] fileheaders = null;
         ArrayList<String[]> rows = new ArrayList<>();
         List<String> messages = new ArrayList<>();
         String error = null;
         
         boolean hasHeaderRow = request.getParameter("header") != null && request.getParameter("header").equalsIgnoreCase("header");
         String delimiter = request.getParameter("delimiter");
         String uid = request.getParameter("file");         
         String filename = request.getParameter("filename");   
         
         try {
        	 User user = sessionService.getCurrentUser(request);
        	 java.io.File file = validateImport1Parameters(uid, user);
                       
             InputStream inputStream = null;
             if (file.exists()) {
            	 
                 if (filename.toLowerCase().endsWith("csv"))
                 {                	
                	 CsvPreference preference = CsvPreference.EXCEL_PREFERENCE;
                	 if (delimiter != null && delimiter.equalsIgnoreCase("semicolon"))
                     {
                    	 preference = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;
                     }
                	 
	                inputStream = new FileInputStream(file);
	                try {

						try (CsvListReader reader = new CsvListReader(new InputStreamReader(inputStream), preference)) {

							if (hasHeaderRow) {
								fileheaders = removeFormulas(reader.getHeader(true));
								if (fileheaders[fileheaders.length - 1].trim().length() == 0) {
									String[] temp = fileheaders.clone();
									fileheaders = new String[temp.length - 1];
									System.arraycopy(temp, 0, fileheaders, 0, temp.length - 1);
								}
							}
							List<String> row;
							while ((row = reader.read()) != null) {								
								String[] a = removeFormulas(row.toArray(new String[row.size()]));
								
								rows.add(a);
								if (fileheaders == null) {
									fileheaders = row.toArray(new String[row.size()]);
								}
							}
						}
		                inputStream.close();              
	                } catch (Exception e)
	                {
	                	logger.error(e.getLocalizedMessage(), e);	                	
	                	messages.add(resources.getMessage("error.FileNotValid", null, "The file is not valid.", locale));
	                	error = resources.getMessage("error.FileNotValid", null, "The file is not valid.", locale);
	                }
                } else if (filename.toLowerCase().endsWith("xls"))
                {
                	inputStream = new FileInputStream(file);
                 	try {	 
                 		HSSFWorkbook wb = new  HSSFWorkbook(inputStream);	            	
            	    	HSSFSheet sheet = wb.getSheetAt(0);
            	    	int numrows = sheet.getPhysicalNumberOfRows();
            	    	
            	    	if (numrows == 0)
            	    	{
            	    		error = resources.getMessage("error.FileNotValid", null, "The file is not valid.", locale);
            	    	} else {
            	    	
	            	    	int start = 0;
	            	    	int numcells =  sheet.getRow(0).getLastCellNum();
	            	    	HSSFRow row;
	            	    	
	            	    	if (hasHeaderRow)
	            	    	{
		        	    		fileheaders = new String[numcells];
		                    	for (int i = 0; i < numcells; i++)
		                    	{
		                    		fileheaders[i] = getText(sheet, 0, i);	                    		
		                    	}
		                    	start = 1;
	            	    	}
		                    
	            	    	for (int r = start; r < numrows; r++) {
	            				row = sheet.getRow(r);
	            				if (row == null) {
	            					continue;
	            				}            				
	            				String[] values = new String[numcells];
	            				boolean empty = true;
	            				for (int i = 0; i < numcells; i++)
		                    	{
	            					values[i] = getText(sheet, r, i);
	            					if (empty && values[i].length() > 0) empty = false;
		                    	}            	
	            				if (!empty)
	            				{
		            				rows.add(values);            
		            				if (fileheaders == null)
		            				{
		            					fileheaders = values;
		            				}
	            				}
	            	    	}	
            	    	}
            	    	   	            
            	    	wb.close();
            	    	inputStream.close();
            	    	
                 	} catch (Exception e)
	                {
	                	logger.error(e.getLocalizedMessage(), e);
	                	messages.add(resources.getMessage("error.FileNotValid", null, "The file is not valid.", locale));
	                	error = resources.getMessage("error.FileNotValid", null, "The file is not valid.", locale);
	                }
                } else if (filename.toLowerCase().endsWith("xlsx"))
                {
                	inputStream = new FileInputStream(file);
                 	try {	 
                 		Workbook wb = new XSSFWorkbook(inputStream);
            	    	Sheet sheet = wb.getSheetAt(0);
            	    	int numrows = sheet.getPhysicalNumberOfRows();
            	    	
            	    	if (numrows == 0)
            	    	{
            	    		error = resources.getMessage("error.FileNotValid", null, "The file is not valid.", locale);
            	    	} else {
            	    	
	            	    	int start = 0;
	            	    	int numcells =  sheet.getRow(0).getLastCellNum();
	            	    	org.apache.poi.ss.usermodel.Row row;
	            	    	
	            	    	if (hasHeaderRow)
	            	    	{
		        	    		fileheaders = new String[numcells];
		                    	for (int i = 0; i < numcells; i++)
		                    	{
		                    		fileheaders[i] = getText(sheet, 0, i);	                    		
		                    	}
		                    	start = 1;
	            	    	}
		                    
	            	    	for (int r = start; r < numrows; r++) {
	            				row = sheet.getRow(r);
	            				if (row == null) {
	            					continue;
	            				}            				
	            				String[] values = new String[numcells];
	            				boolean empty = true;
	            				for (int i = 0; i < numcells; i++)
		                    	{
	            					values[i] = getText(sheet, r, i);
	            					if (empty && values[i].length() > 0) empty = false;
		                    	}            	
	            				if (!empty)
	            				{
		            				rows.add(values);            
		            				if (fileheaders == null)
		            				{
		            					fileheaders = values;
		            				}
	            				}
	            	    	}
            	    	}
            	    	
            	    	wb.close();
            	    	inputStream.close();
            	    	
                 	} catch (Exception e)
	                {
	                	logger.error(e.getLocalizedMessage(), e);
	                	messages.add(resources.getMessage("error.FileNotValid", null, "The file is not valid.", locale));
	                	error = resources.getMessage("error.FileNotValid", null, "The file is not valid.", locale);
	                }
                } else if (filename.toLowerCase().endsWith("ods"))
                {
                	inputStream = new FileInputStream(file);
                 	try {	 
                 		SpreadsheetDocument spreadsheet = SpreadsheetDocument.loadDocument(inputStream);
                	    org.odftoolkit.simple.table.Table sheet = spreadsheet.getSheetByIndex(0);
                 	
            	    	int numrows =  -1 ;
            	    	int maxRowCount = sheet.getRowCount();
            	    	// find first empty value in first column 
            	    	for (int i = 0; i < maxRowCount; i++) {
            	    		if (sheet.getCellByPosition(0, i).getStringValue().isEmpty())
            	    		{
            	    			numrows = i;
            	    			break;
            	    		}
            	    	}
            	    	
            	    	if  (numrows == -1 ) //no empty values  found in first column 
            	    	{
            	    		numrows = maxRowCount;
            	    	}

            	    	int start = 0;
            	    	int numcolumns = -1;
            	    	int maxColumnCount =  sheet.getColumnCount();
            	    	// find first empty value in first row
            	    	for (int i = 0; i < maxColumnCount ; i++) {
            	    		if (sheet.getCellByPosition(i,0).getStringValue().isEmpty())
            	    		{
            	    			numcolumns = i;
            	    			break;
            	    		}
            	    			
            	    	}
            	    	
            	    	if  (numcolumns == -1 ) //no empty values found in first row 
            	    	{
            	    		numcolumns = maxColumnCount;
            	    	}
          	    	
            	    	if (numcolumns <= 0 )
            	    	{
            	    		throw new MessageException ("No columns in ods file: " + file.getPath()); 
            	    	}
            	    	if ((numrows == 1  &&  hasHeaderRow)  ||  numrows <= 0) 
            	    	{
            	    		throw new MessageException ("No rows in ods file " +  file.getPath()); 
            	    	}		
            	    		
        	    		Row row;
        	    		
        	    		if (hasHeaderRow)
        	    		{
	                    	fileheaders = new String[numcolumns];
	                    	for (int i = 0; i < numcolumns; i++)
	                    	{
	                    		fileheaders[i] = sheet.getCellByPosition(i, 0).getStringValue();
	                    	}
	                    	start = 1;
        	    		}
	                    
            	    	for (int r = start; r < numrows; r++) {
            				row = sheet.getRowByIndex(r);
            				if (row == null) {
            					continue;
            				}            				
            				String[] values = new String[numcolumns];
            				for (int i = 0; i < numcolumns; i++)
	                    	{
            					values[i] = sheet.getCellByPosition(i, r).getStringValue();
	                    	}            				
            				rows.add(values);         
            				if (fileheaders == null)
            				{
            					fileheaders = values;
            				}
            	    	}	
            	    	   	                   
            	    	inputStream.close();
            	    	
                 	} catch (Exception e)
	                {
	                	logger.error(e.getLocalizedMessage(), e);
	                	messages.add(resources.getMessage("error.FileNotValid", null, "The file is not valid.", locale));
	                }
                } else {
                	 messages.add(resources.getMessage("error.FileNotValid", null, "The file is not valid.", locale));
                }
                 
                Paging<Attendee> paging = (Paging<Attendee>) request.getSession().getAttribute("attendees-paging");
         		HashMap<String, String> filter = (HashMap<String, String>) request.getSession().getAttribute("attendees-filter");
         		
         		List<AttributeName> attributeNames = user.getSelectedAttributes();
         		
         		paging.setItems(attendeeService.getAttendees(user.getId(), filter, paging.getCurrentPage(), paging.getItemsPerPage()));		
         		         		
         		List<AttributeName> allAttributes = attendeeService.getAllAttributes(user.getId());
         		allAttributes.add(0, new AttributeName(-1, "Name"));
         		allAttributes.add(0, new AttributeName(-1, "Email"));
         	         		         				
             	result = new ModelAndView("addressbook/addressbook", "paging", paging);
             	
             	result.addObject(Constants.FILTER, filter);
             	result.addObject("uploadItem", new UploadItem());
             	
             	result.addObject("fileheaders", fileheaders);
             	request.getSession().setAttribute("fileheaders", fileheaders);
             	
             	HashMap<String, String> headermappings = new HashMap<>();
             	if (fileheaders != null)
             	{
	             	for (String header : fileheaders)
	             	{
	             		if (header.trim().equalsIgnoreCase("name"))
	             		{
	             			headermappings.put(header, "name");
	             		} else if (header.trim().equalsIgnoreCase(Constants.EMAIL))
	             		{
	             			headermappings.put(header, Constants.EMAIL);
	             		} else if (header.trim().equalsIgnoreCase("owner"))
	             		{
	             			headermappings.put(header, "owner");
	             		} else {
	             			if (attributeNames != null)
	             			{
		             			for (AttributeName attributeName : attributeNames) {
			             			if (attributeName.getName().equalsIgnoreCase(header))
			             			{
			             				headermappings.put(header, attributeName.getName());
			             				break;
			             			}
			    				}   
	             			}
	             		}
	             	}
             	}
             	result.addObject("headermappings", headermappings);
             	result.addObject("rows", rows);
             	result.addObject("hasHeaderRow", hasHeaderRow);
             	request.getSession().setAttribute("hasHeaderRow", hasHeaderRow);
             	request.getSession().setAttribute("imported-rows", rows);
             	
             	if (!messages.isEmpty()) 
             	{
             		result.addObject("importmessages", messages);
             	} else {
                 	result.addObject("file", filename);
             	}
             
             	if (error != null)
             	{
             		result.addObject(Constants.ERROR, error);
             	}
             	
            	result.addObject("attributeNames", attributeNames);            	
            	result.addObject("allAttributeNames", allAttributes);
            	result.addObject("target", "importAttendeesCheck");
            	
            	//delete temporary file
            	Files.delete(file.toPath());            	
             }                  
             
         } catch (Exception e) {
        	logger.error(e.getLocalizedMessage(), e);
    		ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
    		model.addObject(Constants.MESSAGE, resources.getMessage("error.ProblemDuringImport", null, "There was a problem during the import process.", locale));
			return model;
         }
      	return result;
     }
	
	private String[] removeFormulas(String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].startsWith("=")) {
				array[i] = "";
			}
		}
		return array;
	}

	private java.io.File validateImport1Parameters(String fileName, User user) throws ValidationException {
		Validator validator = ESAPI.validator();
		String extension = FilenameUtils.getExtension(fileName);
		List<String> allowedExtensions = new ArrayList<>();
		allowedExtensions.add(extension);
		boolean validFileName = validator.isValidFileName("check file name in AddressBookController.import1 method", fileName, allowedExtensions , false);
		if (!validFileName) {
			throw new  ValidationException("Invalid file name: " + fileName,"Invalid file name: " + fileName );
		}				
		
		return fileService.getUsersFile(user.getId(), fileName);
	}
	
	private static String getText(Sheet sheet, int row, int cell)
	{
		if (sheet.getRow(row) == null || sheet.getRow(row).getCell(cell) == null) return "";
		
		String result = "";
		
		try {
		
			if (sheet.getRow(row).getCell(cell).getCellType() == 0){
				double d = sheet.getRow(row).getCell(cell).getNumericCellValue();
				// test if a date!
				if (HSSFDateUtil.isCellDateFormatted(sheet.getRow(row).getCell(cell))) {
					Date date = HSSFDateUtil.getJavaDate(d);
					result = Tools.formatDate(date, ConversionTools.DateFormat);
				} else {
					result = "" + ((int)d);
				}
			} else {
				result = sheet.getRow(row).getCell(cell).getStringCellValue().trim();	
			}
		
		} catch (Exception e) {
			result = sheet.getRow(row).getCell(cell).toString();
		}	
		
		return result;
	}
	
	public ModelAndView importAttendeesCheck(HttpServletRequest request, Locale locale) throws Exception {
		User user = sessionService.getCurrentUser(request);
		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
		
		@SuppressWarnings("unchecked")
		ArrayList<String[]> rows = (ArrayList<String[]>) request.getSession().getAttribute("imported-rows");
		ArrayList<Boolean> valid = new ArrayList<>();
		ArrayList<Boolean> existing = new ArrayList<>();
		
		//get mappings
		SortedMap<Integer, String> mappings = new TreeMap<>();
		for (Entry<String, String[]> entry : parameterMap.entrySet()) {
			if (entry.getKey().startsWith("header"))
			{
				mappings.put(Integer.parseInt(entry.getKey().substring(6)), entry.getValue()[0]);
			}
		}
		
		boolean existingContact = false;
		
		List<String> existingEmailsAddresses = attendeeService.getAttendeeEmailsAddresses(user.getId());
		
		List<String> messages = new ArrayList<>();
		Map<String[], String> invalidAttendees = new HashMap<>();
		for (String[] row: rows)
		{			
			Attendee attendee = new Attendee();
			
			for (Entry<Integer, String> entry: mappings.entrySet())
			{
				if (entry.getValue().equalsIgnoreCase("name"))
				{
					attendee.setName(row[entry.getKey()]);
				} else if (entry.getValue().equalsIgnoreCase(Constants.EMAIL))
				{
					attendee.setEmail(row[entry.getKey()]);
				} 
			}
				
			if (attendee.getName() == null || attendee.getName().trim().length() == 0)
			{
				messages.add(resources.getMessage("error.ContactWithoutName", null, "There is a contact without a name. It will be ignored.", locale));
				invalidAttendees.put(row, "name");
				valid.add(false);
				existing.add(false);
			} else if (attendee.getEmail() == null || attendee.getEmail().trim().length() == 0 || !MailService.isValidEmailAddress(attendee.getEmail().trim()))
			{
				messages.add(resources.getMessage("error.ContactWithoutEmail", null, "There is a contact without valid email address. It will be ignored.", locale));
				invalidAttendees.put(row, Constants.EMAIL);
				valid.add(false);
				existing.add(false);
			} else if (existingEmailsAddresses.contains(attendee.getEmail()))
			{
				existing.add(true);
				existingContact = true;
				valid.add(true);
			} else {
				existing.add(false);
				valid.add(true);
			}			
		}
					
		@SuppressWarnings("unchecked")
		Paging<Attendee> paging = (Paging<Attendee>) request.getSession().getAttribute("attendees-paging");
 		@SuppressWarnings("unchecked")
		HashMap<String, String> filter = (HashMap<String, String>) request.getSession().getAttribute("attendees-filter");
 		
 		int ownerId;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
    	{
			ownerId = -1;
    	} else {
    		ownerId = user.getId();    		
    	}
 		
 		paging.setNumberOfItems( attendeeService.getNumberOfAttendees(ownerId, filter));
		paging.moveTo("1");
		paging.setItems(attendeeService.getAttendees(ownerId, filter, paging.getCurrentPage(), paging.getItemsPerPage()));		
 			
 		ModelAndView result = new ModelAndView("addressbook/addressbook", "paging", paging);
 		result.addObject("attributeNames", user.getSelectedAttributes());
 		List<AttributeName> allAttributes = attendeeService.getAllAttributes(user.getId());
 		allAttributes.add(0, new AttributeName(-1, "Name"));
 		allAttributes.add(0, new AttributeName(-1, "Email"));
    	result.addObject("allAttributeNames", allAttributes);
     	result.addObject(Constants.FILTER, filter);
     	result.addObject("messages", messages);
     	result.addObject("uploadItem", new UploadItem());
          	
     	String[] fileheaders = (String[]) request.getSession().getAttribute("fileheaders");
     	
     	result.addObject("fileheaders", fileheaders);
     	
     	HashMap<String, String> headermappings = new HashMap<>();
     	if (fileheaders != null)
     	{
	     	for (String header : fileheaders)
	     	{
	     		if (header.trim().equalsIgnoreCase("name"))
	     		{
	     			headermappings.put(header, "name");
	     		} else if (header.trim().equalsIgnoreCase(Constants.EMAIL))
	     		{
	     			headermappings.put(header, Constants.EMAIL);
	     		} else if (header.trim().equalsIgnoreCase("owner"))
	     		{
	     			headermappings.put(header, "owner");
	     		} else if (user.getSelectedAttributes() != null) {          		
	     			for (AttributeName attributeName : user.getSelectedAttributes()) {
	         			if (attributeName.getName().equalsIgnoreCase(header))
	         			{
	         				headermappings.put(header, attributeName.getName());
	         				break;
	         			}
					}             		
	     		}
	     	} 
     	}
     	result.addObject("headermappings", headermappings);
     	result.addObject("rows", rows);
     	result.addObject("hasHeaderRow", request.getSession().getAttribute("hasHeaderRow"));
     	result.addObject("target", "importAttendees2");
     	result.addObject("existingContact",existingContact);
     	request.getSession().setAttribute("mappings", mappings);
     	request.getSession().setAttribute("imported-rows", rows);
     	request.getSession().setAttribute("valid", valid);
     	request.getSession().setAttribute("existing", existing);
     	request.getSession().setAttribute("invalidAttendees", invalidAttendees);
     	
     	if (!messages.isEmpty()) 
     	{
     		result.addObject("messages", messages);             	
     	} 
     	
     	return result;
	}
	 
	public ModelAndView importAttendees2(HttpServletRequest request, Locale locale) throws Exception {	 
		User user = sessionService.getCurrentUser(request);
		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
		
		@SuppressWarnings("unchecked")
		ArrayList<String[]> rows = (ArrayList<String[]>) request.getSession().getAttribute("imported-rows");
		 
		//get mappings
		@SuppressWarnings("unchecked")
		SortedMap<Integer, String> mappings = (SortedMap<Integer, String>) request.getSession().getAttribute("mappings");
		List<Attendee> attendees = new ArrayList<>();
		List<String> messages = new ArrayList<>();
		@SuppressWarnings("unchecked")
		Map<String[], String> invalidAttendees = (Map<String[], String>) request.getSession().getAttribute("invalidAttendees");
		
		int rowcounter = 0;
		boolean userChanged = false;
		for (String[] row: rows)
		{
			//check if checked			
			if (parameterMap.containsKey("row" + rowcounter++))
			{
				Attendee attendee = new Attendee();
				attendee.setOwnerId(user.getId());
				
				for (Entry<Integer, String> entry: mappings.entrySet())
				{
					if (entry.getValue().equalsIgnoreCase("name"))
					{
						attendee.setName(row[entry.getKey()]);
					} else if (entry.getValue().equalsIgnoreCase(Constants.EMAIL))
					{
						attendee.setEmail(row[entry.getKey()]);
					} else if (entry.getValue().equalsIgnoreCase("owner"))
					{
						//ignore
					} else {
						Attribute a = new Attribute();
						
						AttributeName attributeName = attendeeService.getAttributeName(entry.getValue(), user.getId());
						
						if (attributeName == null)
						{
							attributeName = new AttributeName();
							attributeName.setName(entry.getValue());
							attributeName.setOwnerId(user.getId());			
							attendeeService.add(attributeName);
							user.getSelectedAttributes().add(attributeName);
							userChanged = true;
						}	
						
						a.setAttributeName(attributeName);
						a.setValue(row[entry.getKey()]);
						
						attendee.getAttributes().add(a);
					}
				}
					
				if (attendee.getName().trim().length() == 0)
				{
					messages.add(resources.getMessage("error.ContactWithoutName", null, "There is a contact without a name. It will be ignored.", locale));
					invalidAttendees.put(row, "name");
				} else if (attendee.getEmail().trim().length() == 0 || !MailService.isValidEmailAddress(attendee.getEmail().trim()))
				{
					messages.add(resources.getMessage("error.ContactWithoutEmail", null, "There is a contact without valid email address. It will be ignored.", locale));
					invalidAttendees.put(row, Constants.EMAIL);
				} else {					
					attendees.add(attendee);
				}							
			}
		}
		
		attendeeService.add(attendees);
			
		messages.add(attendees.size() + " " + resources.getMessage("message.ContactsImported", null, "contacts have been imported.", locale));
		
		if (invalidAttendees.size() > 0)
		{
			messages.add(invalidAttendees.size() + " " + resources.getMessage("message.ContactsIgnored", null, "contacts have been ignored.", locale));
		}
		
		if (userChanged)
		{
			administrationService.updateUser(user);
			sessionService.setCurrentUser(request, user);
		}
		
		@SuppressWarnings("unchecked")
		Paging<Attendee> paging = (Paging<Attendee>) request.getSession().getAttribute("attendees-paging");
 		@SuppressWarnings("unchecked")
		HashMap<String, String> filter = (HashMap<String, String>) request.getSession().getAttribute("attendees-filter");
 		
 		int ownerId;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
    	{
			ownerId = -1;
    	} else {
    		ownerId = user.getId();    		
    	}
 		
 		paging.setNumberOfItems( attendeeService.getNumberOfAttendees(ownerId, filter));
		paging.moveTo("1");
		paging.setItems(attendeeService.getAttendees(ownerId, filter, paging.getCurrentPage(), paging.getItemsPerPage()));		
 			
 		ModelAndView result = new ModelAndView("addressbook/addressbook", "paging", paging);
 		result.addObject("attributeNames", user.getSelectedAttributes());
    	result.addObject("allAttributeNames", attendeeService.getAllAttributes(ownerId));
     	result.addObject(Constants.FILTER, filter);
     	result.addObject("messages", messages);
     	
     	result.addObject("target", "results");
     	
     	if (!invalidAttendees.isEmpty())
     	{
     		StringBuilder summary = new StringBuilder("<table class='table table-bordered'><tr>");
     		for (Entry<Integer, String> entry: mappings.entrySet())
			{
				summary.append("<th>").append(entry.getValue()).append("</th>");
			}     		
     		summary.append("<th>").append(resources.getMessage("label.InvalidAttribute", null, "invalid attribute", locale)).append("</th></tr>");
     		
     		for (Entry<String[], String> entry : invalidAttendees.entrySet())
     		{
     			summary.append("<tr>");
     			for (String value : entry.getKey())
     			{
     				summary.append("<td>").append(value).append("</td>");
     			}     	
     			summary.append("<td>").append(entry.getValue()).append("</td>");
     			summary.append("</tr>");
     		}
     		
     		summary.append("</table>");
     		result.addObject("summary", summary.toString());
     	}
     	
     	return result;
	 }

	
	@SuppressWarnings("unchecked")
	@PostMapping
	public ModelAndView attendeesPOST(HttpServletRequest request, Locale locale) throws Exception {
		
		String target = request.getParameter("target");
		if (target != null)
		{
			if (target.equals("importAttendeesCheck"))
			{
				return importAttendeesCheck(request, locale);
			} else if (target.equals("importAttendees2"))
			{
				return importAttendees2(request, locale);
			}else if (target.equals("importAttendees"))
			{
				return importAttendees(request, locale);
			}
		}		
		
		User user = sessionService.getCurrentUser(request);
		
		int ownerId;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
    	{
			ownerId = -1;
    	} else {
    		ownerId = user.getId();    		
    	}
		
		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
		
		String operation = request.getParameter("operation");
		if (operation != null && operation.equalsIgnoreCase("batchedit"))
		{
			List<Integer> selectedAttendees = new ArrayList<>();
			
			if (request.getParameter("checkAllCheckBox") != null && request.getParameter("checkAllCheckBox").equalsIgnoreCase("true"))
			{
				//this means everything but the unselected one
				HashMap<String, String> filter = (HashMap<String, String>) request.getSession().getAttribute("attendees-filter");
				
				List<String> visible = new ArrayList<>();
				List<String> checked = new ArrayList<>();
				for (Entry<String, String[]> entry : parameterMap.entrySet()) {
					if (entry.getKey().startsWith("visibleAttendee"))
					{
						visible.add(entry.getValue()[0]);
					} else if (entry.getKey().startsWith("selectedAttendee"))
					{
						checked.add(entry.getValue()[0]);
					}
				}					
				
				List<Attendee> attendees = attendeeService.getAttendees(ownerId, filter, 1, Integer.MAX_VALUE);
				for (Attendee attendee : attendees) {
					if (visible.contains(attendee.getId().toString()) && !checked.contains(attendee.getId().toString()))
					{
						//ommit unchecked
					} else {
						selectedAttendees.add(attendee.getId());
					}
				}
			} else {			
				for (Map.Entry<String,String[]> entry : parameterMap.entrySet()) {
					if (entry.getKey().startsWith("selectedAttendee"))
					{
						selectedAttendees.add(Integer.parseInt(entry.getValue()[0]));
					}
				}		
			}
			
			ModelAndView result = attendees(request);
			Map<Integer, List<String>> attributeValues = new HashMap<>();
			if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) < 2)
			{
				attributeValues = attendeeService.getAllAttributeValues(user);
			}
			result.addObject("attributeValues", attributeValues);			
			
			List<Attendee> attendees = attendeeService.getAttendees(selectedAttendees, false);
			result.addObject("batchAttendees", attendees);
			result.addObject("selectedAttendees", selectedAttendees);
			
			List<String> names = new ArrayList<>();
			List<String> emails = new ArrayList<>();
			for (Attendee attendee : attendees) {
				if (selectedAttendees.contains(attendee.getId()))
				{
					if (!names.contains(attendee.getName())) names.add(attendee.getName());
					if (!emails.contains(attendee.getEmail())) emails.add(attendee.getEmail());
				}
			}
			
			result.addObject("names", names);
			result.addObject("emails", emails);		
			
			return result;
		}
		
		String newPage = request.getParameter("newPage");		
		newPage = newPage == null ? "1" : newPage;
		Integer itemsPerPage = ConversionTools.getInt(request.getParameter("itemsPerPage"), 10);		
		HashMap<String, String> filter = new HashMap<>();
		
		for (Entry<String, String[]> entry : parameterMap.entrySet()) {
			if (!entry.getKey().equalsIgnoreCase("newPage") && !entry.getKey().equalsIgnoreCase("itemsPerPage"))
			{
				if (entry.getKey().startsWith("visibleAttendee") || entry.getKey().startsWith("selectedAttendee"))
				{
					//ignore
				} else 
				{
					filter.put(entry.getKey(), entry.getValue()[0]);
				}
			}
		}		
		
		Paging<Attendee> paging = new Paging<>();
		paging.setItemsPerPage(itemsPerPage);
		int numberOfAttendees = attendeeService.getNumberOfAttendees(ownerId, filter);
		paging.setNumberOfItems(numberOfAttendees);
		paging.moveTo(newPage);
		
		List<Attendee> attendees = attendeeService.getAttendees(ownerId, filter, paging.getCurrentPage(), paging.getItemsPerPage());
		paging.setItems(attendees);
				
    	ModelAndView result = new ModelAndView("addressbook/addressbook", "paging", paging);
    	result.addObject("attributeNames", user.getSelectedAttributes());
    	result.addObject("allAttributeNames", attendeeService.getAllAttributes(ownerId));
    	result.addObject(Constants.FILTER, filter);
    	result.addObject("uploadItem", new UploadItem());
    	
    	request.getSession().setAttribute("attendees-paging", paging.clean());
    	request.getSession().setAttribute("attendees-filter", filter);
    	
    	return result;
	}	
	
	@PostMapping(value = "/deleteAttendee")
	public ResponseEntity<String> delete(HttpServletRequest request) {
		String id = request.getParameter("id");
		Attendee attendee = attendeeService.get(Integer.parseInt(id));
		attendee.setOriginalId(attendee.getId());
		attendee.setHidden(true);		
		attendeeService.update(attendee, true);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));

		return new ResponseEntity<>("OK", httpHeaders, HttpStatus.OK);
	}

	@PostMapping(value = "/deleteAttendees")
	public ResponseEntity<String> deleteMultiple(HttpServletRequest request) throws Exception {
		User user = sessionService.getCurrentUser(request);
		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);

		AttendeeUpdater updater = (AttendeeUpdater) context.getBean("attendeeUpdater");
		updater.initForDelete(user, request.getParameter("checkAllCheckBox") != null && request.getParameter("checkAllCheckBox").equalsIgnoreCase("true"), (HashMap<String, String>) request.getSession().getAttribute("attendees-filter"), parameterMap);
		taskExecutorLong.execute(updater);
		request.getSession().removeAttribute("attendees-paging");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));

		return new ResponseEntity<>("OK", httpHeaders, HttpStatus.OK);
	}
		
	@SuppressWarnings("unchecked")
	@RequestMapping( value = "/editAttendee/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public ModelAndView edit(@PathVariable("id") String id, HttpServletRequest request) throws Exception {	
		Attendee attendee = attendeeService.get(Integer.parseInt(id));
		Paging<Attendee> paging = (Paging<Attendee>) request.getSession().getAttribute("attendees-paging");
		HashMap<String, String> filter = (HashMap<String, String>) request.getSession().getAttribute("attendees-filter");
		User user = sessionService.getCurrentUser(request);
		
		int ownerId;

		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
    	{
			ownerId = -1;
    	} else {
			ownerId = user.getId();  
			if (attendee.getOwnerId() != ownerId && !attendeeService.getAccessibleAttendees(ownerId, null).contains(attendee.getId())) {
				throw new ForbiddenURLException();
			}		
		}
		
		paging.setItems(attendeeService.getAttendees(ownerId, filter, paging.getCurrentPage(), paging.getItemsPerPage()));		
 						
    	ModelAndView result = new ModelAndView("addressbook/addressbook", "paging", paging);
    	result.addObject("attributeNames", user.getSelectedAttributes());
    	result.addObject("allAttributeNames", attendeeService.getAllAttributes(ownerId));
    	result.addObject(Constants.FILTER, filter);
    	result.addObject("attendee", attendee);
    	result.addObject("uploadItem", new UploadItem());
    	
    	User owner = administrationService.getUser(attendee.getOwnerId());
    	result.addObject("owner", owner != null ? owner.getLogin() : null);
    	
    	return result;
	}
	
	@PostMapping( value = "/configureAttributes")
	public String configureAttributes(HttpServletRequest request) throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {		
		User user = sessionService.getCurrentUser(request);
		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
		
		user.getSelectedAttributes().clear();
		
		String idsAsString = parameterMap.get("selectedAttributesOrder")[0];
		String[] ids = idsAsString.split(";");
		
		if (request.getParameter("owner") != null && request.getParameter("owner").equals("selected"))
		{
			AttributeName ownerAttributeName = attendeeService.getAttributeName("Owner", user.getId());
        	if (ownerAttributeName == null)
        	{
        		ownerAttributeName = new AttributeName(user.getId(),"Owner");
        		attendeeService.add(ownerAttributeName);
        	}
        	user.getSelectedAttributes().add(ownerAttributeName);
		}
		
		if (idsAsString.trim().length() > 0)
			for (String id : ids) {
				AttributeName attributeName = attendeeService.getAttributeName(Integer.parseInt(id));
				user.getSelectedAttributes().add(attributeName);
			}
		
		administrationService.updateUser(user);
		sessionService.setCurrentUser(request, user);
		
		String source = parameterMap.get("selectedAttributesSource")[0];
		
		if (source.equalsIgnoreCase("attendees"))
		{		
			return "redirect:/addressbook";
		} else {
			return "redirect:/noform/management/participants";
		}
	}
	
	@GetMapping(value = "/configureAttributesJSON", headers="Accept=*/*")
	public @ResponseBody List<AttributeName> configureAttributesJSON(HttpServletRequest request, HttpServletResponse response ) throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		User user = sessionService.getCurrentUser(request);
		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
		
		user.getSelectedAttributes().clear();
		
		String idsAsString = parameterMap.get("selectedAttributesOrder")[0];
		String[] ids = idsAsString.split(";");
		
		if (request.getParameter("owner") != null && request.getParameter("owner").equals("selected"))
		{
			AttributeName ownerAttributeName = attendeeService.getAttributeName("Owner", user.getId());
        	if (ownerAttributeName == null)
        	{
        		ownerAttributeName = new AttributeName(user.getId(),"Owner");
        		attendeeService.add(ownerAttributeName);
        	}
        	user.getSelectedAttributes().add(ownerAttributeName);
		}
		
		if (idsAsString.trim().length() > 0)
			for (String id : ids) {
				AttributeName attributeName = attendeeService.getAttributeName(Integer.parseInt(id));
				user.getSelectedAttributes().add(attributeName);
			}
		
		administrationService.updateUser(user);
		sessionService.setCurrentUser(request, user);
		
		return user.getSelectedAttributes();
	}
	
	@PostMapping( value = "/addAttendee")
	public String add(HttpServletRequest request) throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException, MessageException {			
		
		User user = sessionService.getCurrentUser(request);
		
		Map<String, String[]> parameterMap = Ucs2Utf8.requestToHashMap(request);
		
		Attendee attendee = new Attendee();
		Attendee originalAttendee;
		
		boolean edit = false;
		if (parameterMap.containsKey("id"))
		{
			String id = parameterMap.get("id")[0];
			originalAttendee = attendeeService.get(Integer.parseInt(id));
			edit = true;
			attendee.setOriginalId(originalAttendee.getId());
			attendee.setOwnerId(originalAttendee.getOwnerId());
			originalAttendee.setHidden(true);
		} else {
			attendee.setOwnerId(user.getId());
		}
		
		Map<String, String> keysMap = new HashMap<>();
		Map<String, String> valuesMap = new HashMap<>();

		for (Entry<String, String[]> entry : parameterMap.entrySet()) {
			
			if (entry.getKey().equalsIgnoreCase("name")) {
				attendee.setName(Tools.escapeHTML(entry.getValue()[0]));
			} else if (entry.getKey().equalsIgnoreCase(Constants.EMAIL)) {
				attendee.setEmail(Tools.escapeHTML(entry.getValue()[0]));
			} else if (entry.getKey().equalsIgnoreCase("owner")) {
				if (user.getGlobalPrivileges().get(GlobalPrivilege.UserManagement) > 1 && entry.getValue()[0].length() > 0)
				{				
					User owner = administrationService.getUserForLogin(Tools.escapeHTML(entry.getValue()[0]));
					if (owner != null)
					{
						attendee.setOwnerId(owner.getId());
					} else {					
						throw new MessageException("The user " + entry.getValue()[0] + " does not exist");
					}					
				}
			} else if (entry.getKey().startsWith("key")) {
				keysMap.put(entry.getKey().substring(3), Tools.escapeHTML(entry.getValue()[0]));
			} else if (entry.getKey().startsWith("value")) {
				valuesMap.put(entry.getKey().substring(5), Tools.escapeHTML(entry.getValue()[0]));
			} else if (entry.getKey().startsWith("attribute")) {
				AttributeName attributeName = attendeeService.getAttributeName(Integer.parseInt(entry.getKey().substring(9)));
				Attribute a = new Attribute();
				a.setAttributeName(attributeName);
				a.setValue(Tools.escapeHTML(entry.getValue()[0]));
				attendee.getAttributes().add(a);
			}
		}
		boolean userChanged = false;
		for (Entry<String, String> entry : keysMap.entrySet()) {
			if (entry.getValue().trim().length() > 0 && valuesMap.containsKey(entry.getKey()))
			{
				AttributeName attributeName = null;
				
				try {
					attributeName = attendeeService.getAttributeName(Integer.parseInt(entry.getValue()));
				} catch (NumberFormatException nfe)
				{
					attributeName = null;
				}
				
				if (attributeName == null)
				{
					attributeName = new AttributeName();
					attributeName.setName(entry.getValue());
					attributeName.setOwnerId(user.getId());			
					user.getSelectedAttributes().add(attributeName);
					userChanged = true;
				}				
				
				Attribute a = new Attribute();
				a.setAttributeName(attributeName);
				a.setValue(valuesMap.get(entry.getKey()));
				attendee.getAttributes().add(a);
			}
		}
			
		attendeeService.add(attendee);
		
		if (userChanged)
		{
			String order = request.getParameter("selectedAttributesOrder");
			user.setSelectedAttributesOrder(order);
			administrationService.updateUser(user);
			sessionService.setCurrentUser(request, user);
		}
		
		if (edit)
		{
			return "redirect:/addressbook?edited=" + attendee.getId();
		} else {
			return "redirect:/addressbook?added=" + attendee.getId();
		}		
	}	
			
}
