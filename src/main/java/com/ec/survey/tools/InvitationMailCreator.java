package com.ec.survey.tools;

import com.ec.survey.model.MailTask;
import com.ec.survey.model.ParticipationGroup;
import com.ec.survey.model.ParticipationGroupType;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.Attribute;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("invitationMailCreator")
@Scope("prototype")
public class InvitationMailCreator implements Runnable {

	protected static final Logger logger = Logger.getLogger(InvitationMailCreator.class);

	@Resource(name = "participationService")
	protected ParticipationService participationService;
	
	@Resource(name = "administrationService")
	protected AdministrationService administrationService;
	
	@Resource(name = "attendeeService")
	protected AttendeeService attendeeService;
	
	@Resource(name = "surveyService")
	protected SurveyService surveyService;
	
	@Resource(name = "mailService")
	protected MailService mailService;
	
	@Resource(name = "activityService")
	protected ActivityService activityService;
	
	public @Autowired ServletContext servletContext;
	
	private @Value("${server.prefix}") String host;
	private @Value("${sender}") String sender;
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	
	private MailTask task;
	
	public void init(MailTask task)
	{
		this.task = task;
	}
	
	@Override
	@Transactional
	public void run() {

		StringBuilder message = new StringBuilder();
		
		try {
			
			ParticipationGroup participationGroup = participationService.get(task.getParticipationGroupId());
			User user = administrationService.getUser(task.getUserId());
			Survey survey = surveyService.getSurveyByUniqueId(task.getSurveyUid(), false, true);
			
			if (task.getSelectedAttendee() != null && task.getSelectedAttendee().trim().length() > 0)
			{
				if (participationGroup.getType() == ParticipationGroupType.ECMembers)
				{
					EcasUser ecasUser = participationGroup.getEcasUser(Integer.parseInt(task.getSelectedAttendee())); 
					try {
						createAndSendInvitation(participationGroup, ecasUser, sender, user.getFirstLastName(), task.getSenderAddress(), task.getSenderSubject(), survey, task.getText1(), task.getText2(), task.getMailtemplate());
						message = new StringBuilder("Attendee " + ecasUser.getName() + " has been invited");
						task.setMailsSent(1);
						task.setState(MailTask.FINISHED);
					} catch (MessagingException e) {
						logger.error(e.getMessage(), e);
						message = new StringBuilder("There was a problem during invitation of attendee " + ecasUser.getName() + ". Please contact your support.");
						task.setState(MailTask.ERROR);
					}
				} else {			
				
					Attendee attendee = participationGroup.getAttendee(Integer.parseInt(task.getSelectedAttendee()));			
					try {
						createAndSendInvitation(participationGroup, attendee, sender, user.getFirstLastName(), task.getSenderAddress(), task.getSenderSubject(), survey, task.getText1(), task.getText2(), task.getMailtemplate());
						message = new StringBuilder("Attendee " + attendee.getName() + " has been invited");
						task.setMailsSent(1);
						task.setState(MailTask.FINISHED);
					} catch (MessagingException e) {
						logger.error(e.getMessage(), e);
						message = new StringBuilder("There was a problem during invitation of attendee " + attendee.getName() + ". Please contact your support.");
						task.setState(MailTask.ERROR);
					}
				}
			} else {
				//send to all attendees
				
				List<String> successfullInvitations = new ArrayList<>();
				for (String key : task.getParameters().keySet())
				{
					if (key.startsWith("attendee"))
					{
						int intKey = Integer.parseInt(key.substring(8));
											
						if (participationGroup.getType() == ParticipationGroupType.ECMembers)
						{
							EcasUser ecasUser = participationGroup.getEcasUser(intKey); 
							try {
								createAndSendInvitation(participationGroup, ecasUser, sender, user.getFirstLastName(), task.getSenderAddress(), task.getSenderSubject(), survey, task.getText1(), task.getText2(), task.getMailtemplate());
								successfullInvitations.add(ecasUser.getName());
							} catch (MessagingException e) {
								logger.error(e.getMessage(), e);
								message.append("There was a problem during invitation of attendee ").append(ecasUser.getName()).append("<br />");
								task.setState(MailTask.ERROR);
							}
						} else {		
						
							Attendee attendee = participationGroup.getAttendee(intKey); 						
							try {
								createAndSendInvitation(participationGroup, attendee, sender, user.getFirstLastName(), task.getSenderAddress(), task.getSenderSubject(),survey, task.getText1(), task.getText2(), task.getMailtemplate());
								successfullInvitations.add(attendee.getName());
							} catch (MessagingException e) {
								logger.error(e.getMessage(), e);
								message.append("There was a problem during invitation of attendee ").append(attendee.getName()).append("<br />");
								task.setState(MailTask.ERROR);
							}
						}
					}
				}
				
				message.append(successfullInvitations.size()).append(" attendees have been invited successfully");
				task.setMailsSent(successfullInvitations.size());
				task.setState(MailTask.FINISHED);				
			}			
			
			if (task.getState().equalsIgnoreCase(MailTask.FINISHED))
			{
				activityService.log(506, null, participationGroup.getId().toString(), user.getId(), survey.getUniqueId(), participationGroup.getNiceType());
			}			
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			message = new StringBuilder(e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.toString());
			task.setState("ERROR");			
		}
		
		task.setMessage(message.toString());
		mailService.save(task);		
		
		logger.debug("InvitationMailCreator completed");		
	}
	
	private void createAndSendInvitation(ParticipationGroup participationGroup, EcasUser ecasUser, String senderAddress, String sendername, String reply, String subject, Survey survey, String text1, String text2, String mailtemplate) throws Exception {
		
		//check if there already is an invitation
		Invitation invitation = attendeeService.getInvitationForParticipationGroupAndAttendee(participationGroup.getId(), ecasUser.getId());
		
		if (invitation == null)
		{
			invitation = new Invitation(participationGroup.getId(), ecasUser.getId());
			attendeeService.add(invitation);
		} else {
			invitation.setReminded(new Date());
			attendeeService.update(invitation);
		}	
		
		for (EcasUser u : participationGroup.getEcasUsers()) {
			if (ecasUser.getId().equals(u.getId()))
			{
				u.setInvited(invitation.getInvited());
				u.setReminded(invitation.getReminded());
				u.setAnswers(invitation.getAnswers());
			}
		}
			
		String middleText = host + "runner/invited/" + participationGroup.getId() + "/" + invitation.getUniqueId();
			
		String body = text1 + "<br /><br /><a href='"  + middleText + "'>" + middleText + "</a><br /><br />" + text2;
		body = body.replace("{Name}", ecasUser.getDisplayName());
		
		body = insertDisclaimer(survey, body);
		
		body += "<br/><br/><span style=\"font-size: 9pt; color: #999\">This message was sent by " + sendername + " using EUSurvey's invitation service</span>";
		
		String text = getMailTemplate(mailtemplate).replace("[CONTENT]", body).replace("[HOST]", host);
		
		mailService.SendHtmlMail(ecasUser.getEmail(), senderAddress, reply, subject, text, smtpServer, Integer.parseInt(smtpPort), invitation.getUniqueId());
	}
	
	private String getMailTemplate(String name)
	{
		InputStream inputStream = null;
	     try {
	    	 switch (name)
	    	 {
	    	 	case "eusurvey":
	    	 		inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
	    	 		break;
	    	 	case "ecofficial":
	    	 		inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/EC/mailtemplateecofficial.html");
	    	 		break;
	    	 	default:
	    	 		return "[CONTENT]";	    	         
	    	 }
	    	  	 		
	    	 return IOUtils.toString(inputStream, "UTF-8");
	    	 
	     } catch (Exception e) {
	    	 logger.error(e.getLocalizedMessage(), e); 
	     }	 
	     
    	 return "[CONTENT]";
	}

	private void createAndSendInvitation(ParticipationGroup participationGroup, Attendee attendee, String senderAddress, String sendername, String reply, String subject, Survey survey, String text1, String text2, String mailtemplate) throws Exception
	{		
		//check if there already is an invitation
		Invitation invitation = attendeeService.getInvitationForParticipationGroupAndAttendee(participationGroup.getId(), attendee.getId());
		
		if (invitation == null)
		{
			invitation = new Invitation(participationGroup.getId(), attendee.getId());
			attendeeService.add(invitation);
		} else {
			invitation.setReminded(new Date());
			attendeeService.update(invitation);
		}	
		
		for (Attendee attendee1 : participationGroup.getAttendees()) {
			if (attendee.getId().equals(attendee1.getId()))
			{
				attendee1.setInvited(invitation.getInvited());
				attendee1.setReminded(invitation.getReminded());
				attendee1.setAnswers(invitation.getAnswers());
			}
		}
			
		String middleText = host + "runner/invited/" + participationGroup.getId() + "/" + invitation.getUniqueId();
		
		String body = text1 + "<br /><br /><a href='"  + middleText + "'>" + middleText + "</a><br /><br />" + text2;
		body = body.replace("{Name}", attendee.getName());		
		body = replaceAttributePlaceholders(body, attendee);		
		body = insertDisclaimer(survey, body);		
		body += "<br/><br/><span style=\"font-size: 9pt; color: #999\">This message was sent by " + sendername + " using EUSurvey's invitation service</span>";
				
		String text = getMailTemplate(mailtemplate).replace("[CONTENT]", body).replace("[HOST]", host);
				
		mailService.SendHtmlMail(attendee.getEmail(), senderAddress, reply, subject, text, smtpServer, Integer.parseInt(smtpPort), null);
	}
	
	/**
	 * @param survey
	 * @param body
	 * @return
	 */
	private String insertDisclaimer(Survey survey, String body) {
		//disclaimer
		if((survey.getOwner().getECPrivilege()==0 && survey.getOwner().getType().equals("ECAS")) || survey.getOwner().getType().equals("SYSTEM"))
		{
			body += "<br/><br/>";
			body += "<table style=\"border-size: 1px; border-style=solid; border-color: #ccc;\"><tbody>";
			body += "<tr><td style=\"color: #777; font-weight:bold;\">Disclaimer</td><tr>";
			body += "<tr><td style=\"color: #777; \">The European Commission is not responsible for the content of questionnaires created using the EUSurvey service – it remains the sole responsibility of the form creator and manager."; 
			body += "The use of EUSurvey service does not imply a recommendation or endorsement, by the European Commission, of the views expressed within them.";
			body += "</td><tr>";
			body += "</tbody></table>";
		}
		return body;
	}
	
	private String replaceAttributePlaceholders(String body, Attendee attendee)
	{
		Pattern pattern = Pattern.compile("\\{(.+?)\\}");
		Matcher matcher = pattern.matcher(body);
		HashMap<String,String> replacements = new HashMap<>();
		for (Attribute att : attendee.getAttributes()) {
			if (!replacements.containsKey(att.getAttributeName().getName()))
			{
				replacements.put(att.getAttributeName().getName(), att.getValue());
			}
		}
		replacements.put("email", attendee.getEmail());
		replacements.put("Email", attendee.getEmail());
		replacements.put("Name", attendee.getName());
		replacements.put("name", attendee.getName());
		replacements.put("host", host);
		
		//populate the replacements map ...
		StringBuilder builder = new StringBuilder();
		int i = 0;
		while (matcher.find()) {
		    String replacement = replacements.get(matcher.group(1));
		    
		    if (replacement == null) replacement = "";
		    
		    builder.append(body.substring(i, matcher.start()));
		    builder.append(replacement);
		    i = matcher.end();
		}
		builder.append(body.substring(i, body.length()));
		return builder.toString();		
	}
}
