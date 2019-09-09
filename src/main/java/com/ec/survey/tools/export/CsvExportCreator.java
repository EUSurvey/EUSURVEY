package com.ec.survey.tools.export;

import com.ec.survey.model.Activity;
import com.ec.survey.model.ActivityFilter;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.AttributeName;
import com.ec.survey.tools.ConversionTools;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@Service("csvExportCreator")
@Scope("prototype")
public class CsvExportCreator extends ExportCreator {

	@Override
	void ExportContent(boolean sync) throws Exception {}
	
	@Override
	void ExportStatistics() throws Exception {}
	
	@Override
	void ExportStatisticsQuiz() throws Exception {}

	@Override
	void ExportAddressBook() throws Exception {
		
		User user = administrationService.getUser(userId);
				
		int ownerId;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
    	{
			ownerId = -1;
    	} else {
    		ownerId = user.getId();    		
    	}
		
		List<Attendee> attendees = attendeeService.getAttendees(ownerId, export.getResultFilter().getFilterValues(), 1, Integer.MAX_VALUE, sessionFactory, true);
		List<AttributeName> configuredattributes = user.getSelectedAttributes();		
	
		BufferedWriter out = null;
		try	
		{	
			out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			
			writefirst(out, "name");
			write(out, "email");
			
			for (AttributeName att: configuredattributes)
			{
				write(out, att.getName());
			}
			out.newLine(); 
					
			for (Attendee attendee : attendees)
			{				
				writefirst(out, attendee.getName());
				write(out,attendee.getEmail());
				
				for (AttributeName att: configuredattributes)
				{
					
					if (att.getName().equals("Owner"))
					{
						write(out, attendee.getOwner() != null ? attendee.getOwner().replace("&#160;", "") : "");
					} else {
						write(out, attendee.getAttributeValue(att.getId()).replace("&#160;", ""));
					}					
					
				}			
				
				out.newLine(); 
			}  
						
			out.close();
										
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			try {
				if (out != null)
				{
					out.close();
				}
			} catch (IOException e1) {
				// ignore
			}
		}	

	}
	
	private static void write(BufferedWriter out, String value) throws IOException
	{		
		out.write(",\"");
		if (value != null) out.write(value.trim().replace("\"", "\"\""));
		out.write("\"");
	}
	
	private static void writefirst(BufferedWriter out, String value) throws IOException
	{		
		out.write("\"");
		if (value != null) out.write(value.trim().replace("\"", "\"\""));
		out.write("\"");
	}

	@Override
	void ExportActivities() throws Exception {
		List<Activity> activities = activityService.get(export.getActivityFilter(), 1, Integer.MAX_VALUE);	
	
		BufferedWriter out = null;
		try	
		{	
			out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			
			boolean first = true;
			
			ActivityFilter  filter = export.getActivityFilter();
			if (filter.exported("date"))
			{
				if (first)
				{
					writefirst(out, "Date");
					first = false;
				} else {
					write(out, "Date");
				}
			}
			
			if (filter.exported("logid"))
			{
				if (first)
				{
					writefirst(out, "LogID");
					first = false;
				} else {
					write(out, "LogID");
				}
			}
			
			if (filter.exported("user"))
			{
				if (first)
				{
					writefirst(out, "User");
					first = false;
				} else {
					write(out, "User");
				}
			}
			
			if (filter.exported("object"))
			{
				if (first)
				{
					writefirst(out, "Object");
					first = false;
				} else {
					write(out, "Object");
				}
			}
			
			if (filter.exported("property"))
			{
				if (first)
				{
					writefirst(out, "Property");
					first = false;
				} else {
					write(out, "Property");
				}
			}
			
			if (filter.exported("event"))
			{
				if (first)
				{
					writefirst(out, "Event");
					first = false;
				} else {
					write(out, "Event");
				}
			}
			
			if (filter.exported("description"))
			{
				if (first)
				{
					writefirst(out, "Description");
					first = false;
				} else {
					write(out, "Description");
				}
			}
			
			if (filter.exported("oldvalue"))
			{
				if (first)
				{
					writefirst(out, "OldValue");
					first = false;
				} else {
					write(out, "OldValue");
				}
			}
			
			if (filter.exported("newvalue"))
			{
				if (first)
				{
					writefirst(out, "NewValue");
					first = false;
				} else {
					write(out, "NewValue");
				}
			}
					
			for (Activity activity : activities)
			{				
				out.newLine();
				first = true;
				
				if (filter.exported("date"))
				{
					if (first)
					{
						writefirst(out, ConversionTools.getFullString(activity.getDate()));
						first = false;
					} else {
						write(out, ConversionTools.getFullString(activity.getDate()));
					}
				}
				
				if (filter.exported("logid"))
				{
					if (first)
					{
						writefirst(out, Integer.toString(activity.getLogID()));
						first = false;
					} else {
						write(out, Integer.toString(activity.getLogID()));
					}
				}
				
				if (filter.exported("user"))
				{
					activity.setUserName(activity.getUserId() > 0 ? administrationService.getUser(activity.getUserId()).getName() : "");
					if (first)
					{
						writefirst(out, activity.getUserName());
						first = false;
					} else {
						write(out, activity.getUserName());
					}
				}
				
				if (filter.exported("object"))
				{
					if (first)
					{
						writefirst(out, activity.getObject());
						first = false;
					} else {
						write(out, activity.getObject());
					}
				}
				
				if (filter.exported("property"))
				{
					if (first)
					{
						writefirst(out, activity.getProperty());
						first = false;
					} else {
						write(out, activity.getProperty());
					}
				}
				
				if (filter.exported("event"))
				{
					if (first)
					{
						writefirst(out, activity.getEvent());
						first = false;
					} else {
						write(out, activity.getEvent());
					}
				}
				
				if (filter.exported("description"))
				{
					if (first)
					{
						writefirst(out, resources.getMessage("logging." + activity.getLogID(), null, Integer.toString(activity.getLogID()), locale));
						first = false;
					} else {
						write(out, resources.getMessage("logging." + activity.getLogID(), null, Integer.toString(activity.getLogID()), locale));
					}
				}
				
				if (filter.exported("oldvalue"))
				{
					if (first)
					{
						writefirst(out, activity.getOldValue());
						first = false;
					} else {
						write(out, activity.getOldValue());
					}
				}
				
				if (filter.exported("newvalue"))
				{
					if (first)
					{
						writefirst(out, activity.getNewValue());
						first = false;
					} else {
						write(out, activity.getNewValue());
					}
				}			
			}  
						
			out.close();
										
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			try {
				if (out !=null)
				{
					out.close();
				}
			} catch (IOException e1) {
				// ignore
			}
		}			
	}

	@Override
	void ExportTokens() throws Exception {}	

}
