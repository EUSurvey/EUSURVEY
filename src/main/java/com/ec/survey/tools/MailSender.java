package com.ec.survey.tools;


import java.io.File;
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
	
	public void init(String to, String from, String subject, String reply, String body, File attachment, File attachment2, String info)
	{
		this.to = to;
		this.from = from;
		this.subject = subject;
		this.reply = reply;
		this.body = body;
		this.attachment = attachment;
		this.attachment2 = attachment2;
		this.info = info;
	}
	
	@Override
	@Transactional
	public void run() {
		System.setProperty("mail.mime.charset", "utf8");
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(server);
		sender.setPort(Integer.parseInt(port));
		
		MimeMessage message = sender.createMimeMessage();
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
			
			sender.send(message);
			logger.info("mail sent to " + to + " at " + DateFormat.getInstance().format(new Date()) + (info != null ? " " + info : ""));
		} catch (MessagingException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	private String adaptFilename(String file)
	{
		if (file.startsWith("answer"))
		{
			return "Contribution" + file.substring(6);
		} else if (file.startsWith("quiz"))
		{
			return "QuizResults" + file.substring(4);
		}
		
		return file;
	}
}
