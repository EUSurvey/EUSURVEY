package com.ec.survey.tools;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.Status;
import com.ec.survey.service.MailService;
import com.ec.survey.service.SchemaService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;

@Service("fsCheckWorker")
@Scope("singleton")
public class FsCheckWorker implements Runnable {

	protected static final Logger logger = Logger.getLogger(FsCheckWorker.class);

	private @Value("${filesystem.surveys}") String surveysDir;
	private @Value("${monitoring.recipient}") String monitoringEmail;
	private @Value("${monitoring.warnFSUsageThreshold:#{null}}") String warnFSUsageThreshold;
	private @Value("${monitoring.warnFSUsageThreshold2:#{null}}") String warnFSUsageThreshold2;
	private @Value("${server.prefix}") String host;
	private @Value("${sender}") String sender;

	@Resource(name = "schemaService")
	private SchemaService schemaService;

	@Resource(name="mailService")
	private MailService mailService;

	protected @Autowired ServletContext servletContext;

	@Autowired
	protected MessageSource resources;

	private void sendWarningEmail(boolean first, String percentageReached) throws IOException, MessageException {

		InetAddress ip = InetAddress.getLocalHost();
		String hostname = ip.getHostName();

		String body = "Dear Administrators,<br /><br />"
				+ "This is an automated message to inform you that the current storage usage on " + surveysDir + " / " + hostname
				+ " has reached " + percentageReached + "% of its total capacity.<br /><br />Details:<br /><ul><li>Storage Name/ID: " + surveysDir + "</li>"
				+ "<li>Current Usage: " + percentageReached + "%</li><li>Date &amp; Time: " + Tools.formatDate(new Date(), "MM/dd/yyyy HH:mm:ss") + "</li></ul>"
				+ "<br />This is an informational alert to help ensure there is ample time to take necessary actions and prevent any potential disruptions due to storage limitations."
				+ "<br /><br />Best regards,<br />EUSurvey";

		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8).replace("[CONTENT]", body).replace("[HOST]",host);

		String subject = "Storage Usage Alert – " + percentageReached + "% Capacity Reached";

		mailService.SendHtmlMail(monitoringEmail, sender, sender, subject, text, null);
	}

	@Override
	public void run() {
		try {
			logger.info("FsCheckWorker started");

			Status status = schemaService.getStatus();

			File surveysFileDir = new java.io.File(surveysDir);

			long total = surveysFileDir.getTotalSpace();
			long free = surveysFileDir.getFreeSpace();
			double usedPercent = (double) (total - free) / total * 100.0;

			double threshold = warnFSUsageThreshold != null && !warnFSUsageThreshold.isEmpty() ? Double.parseDouble(warnFSUsageThreshold) : 0.0;
			double threshold2 = warnFSUsageThreshold2 != null && !warnFSUsageThreshold2.isEmpty() ? Double.parseDouble(warnFSUsageThreshold2) : 0.0;

			if (threshold2 > 0 && usedPercent > threshold2) {
				// we are already over the second threshold
				if (status.getFsCheckState() != Status.FSCheckStates.SecondThresholdEmailSent) {
					sendWarningEmail(false, warnFSUsageThreshold2);
					schemaService.setFsCheckState(Status.FSCheckStates.SecondThresholdEmailSent);
					return;
				}
			} else if (threshold > 0 && usedPercent > threshold) {
				// we are over the first threshold
				if (status.getFsCheckState() == Status.FSCheckStates.Unset) {
					sendWarningEmail(true, warnFSUsageThreshold);
					schemaService.setFsCheckState(Status.FSCheckStates.FirstThresholdEmailSent);
					return;
				}
			} else if (status.getFsCheckState() != Status.FSCheckStates.Unset) {
				// Email had been sent but file system got more free space since then
				// -> reset state
				schemaService.setFsCheckState(Status.FSCheckStates.Unset);
				return;
			}

		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}

		logger.info("FsCheckWorker finished");
	}
	
}
