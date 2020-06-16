package com.ec.survey.tools;

import com.ec.survey.model.ParticipationGroup;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.service.AttendeeService;
import com.ec.survey.service.ParticipationService;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("guestListCreator")
@Scope("prototype")
public class GuestListCreator implements Runnable {

	protected static final Logger logger = Logger.getLogger(GuestListCreator.class);

	@Resource(name = "participationService")
	protected ParticipationService participationService;	
	
	@Resource(name = "attendeeService")
	protected AttendeeService attendeeService;	
	
	@Resource(name = "sessionFactory")
	protected SessionFactory sessionFactory;	 
		
	private int groupId;
	private List<Integer> userIDs;
	private List<String> tokens;
	private List<Integer> attendeeIDs;
	private int type;
	
	public void initUsers(int groupId, List<Integer> userIDs) {
		this.groupId = groupId;
		this.userIDs = userIDs;
		type = 1;
	}
	
	public void initAttendees(int groupId, List<Integer> attendeeIDs) {
		this.groupId = groupId;
		this.attendeeIDs = attendeeIDs;
		type = 2;
	}
	
	public void initTokens(int groupId, List<String> tokens) {
		this.groupId = groupId;
		this.tokens = tokens;
		type = 3;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void run() {
		runBasic(true);
	}

	@Transactional
	public void runSync() {
		runBasic(true);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	private void runBasic(boolean sync)
	{
		Session session = sessionFactory.getCurrentSession();
		ParticipationGroup g = participationService.get(groupId);
		
		try {
			
			if (type == 1)
			{
				List<EcasUser> users = new ArrayList<EcasUser>(); //participationService.getUsersForParticipationGroup(g);
				
				for (int id : userIDs)
				{
					EcasUser user = (EcasUser) session.get(EcasUser.class, id);
					users.add(user);
				}
				g.setEcasUsers(users);

			
			} else if (type == 2)
			{
				List<Attendee> attendees = new ArrayList<>();
				for (int intKey : attendeeIDs)
				{
					Attendee attendee = attendeeService.get(intKey);
					attendees.add(attendee);
					
				}
				g.setAttendees(attendees);
			} else if (type == 3)
			{
				try {
					List<Invitation> existingInvitations = attendeeService.getInvitationsForParticipationGroup(g.getId());
			 	 	for (Invitation invitation : existingInvitations)
			 	 	{
			 	 		if (tokens.contains(invitation.getUniqueId()))
			 	 		{
			 	 			tokens.remove(invitation.getUniqueId());
			 	 		}
			 	 	}
										
					attendeeService.addTokens(tokens, g.getId());
				} catch (GenericJDBCException e)
				{
					if (e.getMessage().equalsIgnoreCase("maximum number of invitations per guestlist exceeded"))
					{
						g.setError("2");
					}
				}
			}
		
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			g.setError("1");
		}
		
		g.setInCreation(false);
		participationService.save(g);
	}

}
