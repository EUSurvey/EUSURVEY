package com.ec.survey.tools;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ec.survey.exception.MessageException;
import com.ec.survey.service.MailService;

@Service("mailSender")
@Scope("prototype")
public class MailSender implements Runnable {

	protected static final Logger logger = Logger.getLogger(MailSender.class);
	
	private @Value("${server.prefix}") String host;
	private @Value("${sender}") String sender;
	private @Value("${smtpserver}") String server;
	private @Value("${smtp.port}") String port;
	
	private String to;
	private String from;
	private String subject;
	private String reply;
	private String body;
	private File attachment;
	private File attachment2;
	private String info;
	private boolean deletefiles;
	
	public void init(String to, String from, String subject, String reply, String body, File attachment, File attachment2, String info, boolean deletefiles) throws MessageException
	{
		if (to == null || to.trim().length() == 0 || !MailService.isValidEmailAddress(to))
		{
			throw new MessageException("Invalid email address");
		}
		
		this.to = to;
		this.from = from;
		this.subject = subject;
		this.reply = reply;
		this.body = body;
		this.attachment = attachment;
		this.attachment2 = attachment2;
		this.info = info;
		this.deletefiles = deletefiles;
	}
	
	@Override
	@Transactional
	public void run() {
		System.setProperty("mail.mime.charset", "utf8");
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(server);
		mailSender.setPort(Integer.parseInt(port));
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true, "UTF-8");
			
			if (to.contains(";"))
			{
				String[] tos = to.split(";");
				for (String t : tos) {
					helper.addTo(t);
				}
			} else {			
				helper.setTo(to);
			}
			helper.setFrom(from);
			helper.setSubject(subject);	
			
			String plain;
			try {
				plain = ConversionTools.removeHTML(body.replace("<br />","[BR]")).replace("[BR]", "\n\r");
				helper.setText(plain, body);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
				helper.setText(body, true);
			}	
			
			helper.setReplyTo(reply);
			
			if (attachment != null)
				helper.addAttachment(adaptFilename(attachment.getName()), attachment);
			
			if (attachment2 != null)
				helper.addAttachment(adaptFilename(attachment2.getName()), attachment2);
			
			mailSender.send(message);
			logger.info("mail sent to " + to + " at " + DateFormat.getInstance().format(new Date()) + (info != null ? " " + info : ""));
			
			if (deletefiles)
			{
				if (attachment != null)
				{
					Files.deleteIfExists(attachment.toPath());
				}
				
				if (attachment2 != null)
				{
					Files.deleteIfExists(attachment2.toPath());
				}
			}			
			
		} catch (MessagingException | IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	private String adaptFilename(String file)
	{
		if (file.startsWith(Constants.ANSWER))
		{
			return "Contribution" + file.substring(6);
		} else if (file.startsWith("quiz"))
		{
			return "QuizResults" + file.substring(4);
		}
		
		return file;
	}
}
