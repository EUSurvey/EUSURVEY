package com.ec.survey.service;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.MailTask;
import com.ec.survey.tools.InvitationMailCreator;
import com.ec.survey.tools.MailSender;
import org.apache.commons.io.IOUtils;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service("mailService")
public class MailService extends BasicService {

	private @Value("${server.prefix}") String serverPrefix;

	public void SendHtmlMail(String to, String from, String reply, String subject, String body, File attachment, File attachment2, String info, boolean deleteFiles) throws MessageException {
		MailSender sender = (MailSender) context.getBean("mailSender");
		sender.init(to, from, subject, reply, body, attachment, attachment2, info, deleteFiles);		
		getMailPool().execute(sender);
	}
	
	
	public void SendHtmlMail(String to, String from, String reply, String subject, String body, File attachment, String info) throws MessageException {
		MailSender sender = (MailSender) context.getBean("mailSender");
		sender.init(to, from, subject, reply, body, attachment, null, info, false);		
		getMailPool().execute(sender);
	}
	
	public void SendHtmlMail(String to, String from, String reply, String subject, String body, String info) throws MessageException {
		SendHtmlMail(to, from, reply, subject, body, null, info);
	}
	
	public static boolean isNotEmptyAndValidEmailAddress(String email) {
		return email != null && !email.isBlank() && isValidEmailAddress(email.trim());
	}


	public String getEUSurveyMailTemplate(String mailContent) throws IOException {
		return getEUSurveyMailTemplate(mailContent, this.serverPrefix);
	}
	public String getEUSurveyMailTemplate(String mailContent, String host) throws IOException {
		var inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		return IOUtils.toString(inputStream, StandardCharsets.UTF_8).replace("[CONTENT]", mailContent).replace("[HOST]", host);
	}
	
	public static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
	 	 	emailAddr.validate();
	 	 } catch (AddressException ex) {
	 	 	result = false;
	 	 }
	 	 return result;
	}
	
	@Transactional(readOnly = false)
	public void start(MailTask task)
	{
		task.setState(MailTask.WAITING);
		
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(task);
		
		InvitationMailCreator creator = (InvitationMailCreator) context.getBean("invitationMailCreator");
		creator.init(task);
		
		getPool().execute(creator);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void save(MailTask task) {
		Session session = sessionFactory.getCurrentSession();		
		task = (MailTask) session.merge(task);
		session.setReadOnly(task, false);		
		session.saveOrUpdate(task);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<MailTask> get() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM MailTask");
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getParticipationGroupsWithRunningMail(String surveyUid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT m.participationGroupId FROM MailTask m WHERE m.surveyUid = :uid AND m.state = :state").setString("uid", surveyUid).setString("state", MailTask.WAITING);
		return query.list();
	}

	public MailTask getFirstFinishedMailTask(String surveyUid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM MailTask m WHERE m.surveyUid = :uid AND m.notified = false AND m.state != :state").setString("uid", surveyUid).setString("state", MailTask.WAITING);
		List<?> result = query.setMaxResults(1).list();
		
		if (!result.isEmpty()) return (MailTask) result.get(0);
		return null;
	}
	
}
