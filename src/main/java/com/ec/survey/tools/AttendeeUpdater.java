package com.ec.survey.tools;

import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.Attribute;
import com.ec.survey.model.attendees.AttributeName;
import com.ec.survey.service.AttendeeService;
import com.ec.survey.service.SystemService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

@Service("attendeeUpdater")
@Scope("prototype")
public class AttendeeUpdater implements Runnable {

	protected static final Logger logger = Logger.getLogger(AttendeeUpdater.class);
	
	@Resource(name="attendeeService")
	private AttendeeService attendeeService;
	
	@Resource(name="systemService")
	protected SystemService systemService;
	
	@Autowired
	protected MessageSource resources;	
	
	private Map<String, String> filter;
	private Map<String, String[]> parametermap;
	private User user;
	private User ownerUser;
	private Map<Integer, String> replacements;
	private String name;
	private String email;
	private Locale locale;
	private boolean isDelete = false;
	private boolean all;

	public void initForUpdate(User user, User ownerUser, Map<String, String> filter, Map<String, String[]> parametermap, Map<Integer, String> replacements, String name, String email, Locale locale) {
		this.isDelete = false;
		this.filter = filter;
		this.parametermap = parametermap;
		this.user = user;
		this.replacements = replacements;
		this.ownerUser = ownerUser;
		this.name = name;
		this.email = email;
		this.locale = locale;
	}
	
	public void initForDelete(User user, boolean all, HashMap<String, String> filter, Map<String, String[]> parametermap)
	{
		this.isDelete = true;
		this.all = all;
		this.filter = filter;
		this.parametermap = parametermap;
		this.user = user;
	}
	
	@Override
	public void run() {
		try {
			
			logger.info("starting attendee updater, idDelete = " + isDelete);
			
			if (isDelete)
			{
				int numberOfDeletedAttendees = 0;
				if (all)
				{
					numberOfDeletedAttendees = attendeeService.markAttendeesAsDeleted(user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) > 1 ? -1 : user.getId(), filter);
				} else {			
					for (String key : parametermap.keySet()) {
						if (key.startsWith("selectedAttendee"))
						{
							Attendee attendee = attendeeService.get(Integer.parseInt(parametermap.get(key)[0]));
							attendee.setHidden(true);
							attendee.setOriginalId(attendee.getId());
							attendeeService.update(attendee, true);
							numberOfDeletedAttendees++;
						}
					}				
				}
				
				String message = resources.getMessage("message.ContactsDeletedBatchFinished", new String[] {Integer.toString(numberOfDeletedAttendees)}, "Contacts deleted successfully", locale);
				systemService.sendUserSuccessMessage(user.getId(), message);
			} else {
				
				List<Integer> selectedAttendees = new ArrayList<>();
				for (String key : parametermap.keySet()) {
					if (key.startsWith("batchAttendee"))
					{
						selectedAttendees.add(Integer.parseInt(parametermap.get(key)[0]));
					}
				}
			
				List<Attendee> attendees = attendeeService.getAttendees(selectedAttendees, true);
				Map<Integer, String> namesForAttributenameIDs = attendeeService.getNamesForAttributenameIDs(replacements.keySet());
				int numberOfEditedAttendees = 0;
				
				for (Attendee oldAttendee: attendees)
				{
					if (parametermap.get("batchAttendee" + oldAttendee.getId().toString()) != null && parametermap.get("batchAttendee" + oldAttendee.getId().toString())[0].equals(oldAttendee.getId().toString()))
					//only change "own" attendees, not shared ones
					if (oldAttendee.getOwnerId().equals(user.getId()) || user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
					{
						Attendee newAttendee = oldAttendee.copy();
						newAttendee.setOriginalId(oldAttendee.getId());
						
						for (Integer attributeId: replacements.keySet())
						{
							boolean found = false;
							for (Attribute attribute: newAttendee.getAttributes())
							{
								if (attribute.getAttributeName().getId().equals(attributeId) || attribute.getAttributeName().getName().equals(namesForAttributenameIDs.get(attributeId)))
								{
									attribute.setValue(replacements.get(attributeId));
									found = true;
								}
							}
							if (!found && replacements.get(attributeId) != null)
							{
								AttributeName existingAttributeName = attendeeService.getAttributeName(attributeId);
								Attribute newAttribute = new Attribute(user.getId(), existingAttributeName, replacements.get(attributeId));
								newAttendee.getAttributes().add(newAttribute);
							}
						}
						if (ownerUser != null && user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
						{
							newAttendee.setOwnerId(ownerUser.getId());
						}
						
						if (name != null && name.trim().length() > 0) newAttendee.setName(name);
						if (email != null && email.trim().length() > 0) newAttendee.setEmail(email);
						
						attendeeService.update(newAttendee, false);
						oldAttendee.setHidden(true);
						attendeeService.update(oldAttendee, false);
						numberOfEditedAttendees++;
					}
				}
			
				String message = resources.getMessage("message.ContactsUpdatedBatchFinished", new String[] {Integer.toString(numberOfEditedAttendees)}, "Contacts updated successfully", locale);
				systemService.sendUserSuccessMessage(user.getId(), message);
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			String message = resources.getMessage("error.ContactsUpdatedBatchFailed", null, "There was a problem during updating of the contacts.", locale);
			systemService.sendUserErrorMessage(user.getId(), message);
		}
		
		logger.info("finished attendee updater");
	}
	
}
