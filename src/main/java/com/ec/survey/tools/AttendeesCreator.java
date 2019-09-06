package com.ec.survey.tools;

import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.Attribute;
import com.ec.survey.model.attendees.AttributeName;
import com.ec.survey.service.AttendeeService;

public class AttendeesCreator {

	public static void createDummyAttendees(int ownerId, AttendeeService attendeeService, String sender) {		
		
		//create default attribute names
		attendeeService.add(new AttributeName(-1, "Title"));
		attendeeService.add(new AttributeName(-1, "First Name"));
		attendeeService.add(new AttributeName(-1, "Last Name"));
		attendeeService.add(new AttributeName(-1, "c/o"));
		attendeeService.add(new AttributeName(-1, "Address"));
		attendeeService.add(new AttributeName(-1, "Company"));
		attendeeService.add(new AttributeName(-1, "Position"));
		attendeeService.add(new AttributeName(-1, "Street"));
		attendeeService.add(new AttributeName(-1, "Supplement"));
		attendeeService.add(new AttributeName(-1, "Number"));
		attendeeService.add(new AttributeName(-1, "Zip-Code"));
		attendeeService.add(new AttributeName(-1, "Country"));
		attendeeService.add(new AttributeName(-1, "Region"));
		attendeeService.add(new AttributeName(-1, "Cell-phone"));
		attendeeService.add(new AttributeName(-1, "Phone"));
		attendeeService.add(new AttributeName(-1, "Pager"));
		attendeeService.add(new AttributeName(-1, "Fax"));
		attendeeService.add(new AttributeName(-1, "Birthday"));
		attendeeService.add(new AttributeName(-1, "Website"));
		attendeeService.add(new AttributeName(-1, "PIN"));
		attendeeService.add(new AttributeName(-1, "ICQ"));
		attendeeService.add(new AttributeName(-1, "Skype"));
		attendeeService.add(new AttributeName(-1, "AOL"));
		attendeeService.add(new AttributeName(-1, "Comments"));
		attendeeService.add(new AttributeName(-1, "Remarks"));
		
		
		//create dummy users
		
		AttributeName favCar = new AttributeName(ownerId, "Favorite Car");
		
		Attendee attendee = new Attendee();
		attendee.setOwnerId(ownerId);
		attendee.setName("John Doe");
		attendee.setEmail(sender);
		attendee.getAttributes().add(new Attribute(ownerId, favCar, "Ferrari F50"));
		
		for (int i = 1; i < 30; i++)
		{
			AttributeName an = new AttributeName(ownerId, "Attribute" + i);
			attendee.getAttributes().add(new Attribute(ownerId, an, "Value" + i));
		}		
		
		attendeeService.add(attendee);
		
		attendee = new Attendee();
		attendee.setOwnerId(ownerId);
		attendee.setName("Bob Hope");
		attendee.setEmail(sender);
		attendee.getAttributes().add(new Attribute(ownerId, favCar, "Mini Cooper"));
		attendeeService.add(attendee);			
		
		attendee = new Attendee();
		attendee.setOwnerId(ownerId);
		attendee.setName("Jack Jones");
		attendee.setEmail(sender);
		attendee.getAttributes().add(new Attribute(ownerId, favCar, "Suzuki Swift"));
		attendeeService.add(attendee);	
				
		attendee = new Attendee();
		attendee.setOwnerId(ownerId);
		attendee.setName("Harry Smith");
		attendee.setEmail(sender);
		attendee.getAttributes().add(new Attribute(ownerId, favCar, "Jaguar E-Type"));
		attendeeService.add(attendee);	
					
		attendee = new Attendee();
		attendee.setOwnerId(ownerId);
		attendee.setName("Ron Howard");
		attendee.setEmail(sender);
		attendee.getAttributes().add(new Attribute(ownerId, favCar, "Mercedes"));
		attendeeService.add(attendee);	
				
		attendee = new Attendee();
		attendee.setOwnerId(ownerId);
		attendee.setName("James Bond");
		attendee.setEmail(sender);
		attendee.getAttributes().add(new Attribute(ownerId, favCar, "Aston Martin DB5"));
		attendeeService.add(attendee);	
				
		for (int i = 1; i < 100; i++)
		{
			attendee = new Attendee();
			attendee.setOwnerId(ownerId);
			attendee.setName("Attendee" + i);
			attendee.setEmail(sender);
			attendee.getAttributes().add(new Attribute(ownerId, favCar, "Z" + i));
			attendeeService.add(attendee);	
		}
		
	}		

}
