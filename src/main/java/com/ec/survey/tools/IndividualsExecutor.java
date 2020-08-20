package com.ec.survey.tools;

import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.FileService;
import com.ec.survey.service.MailService;
import com.ec.survey.service.PDFService;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

@Service("individualsExecutor")
@Scope("prototype")
public class IndividualsExecutor implements Runnable {

	@Resource(name = "fileService")
	private FileService fileService;
	
	@Resource(name="pdfService")
	private PDFService pdfService;
	
	@Resource(name="mailService")
	private MailService mailService;
	
	protected static final Logger logger = Logger.getLogger(IndividualsExecutor.class);
	
	private Survey survey;
	private ResultFilter filter;
	private String email;
	private String from;
	private String host;
	
	public void init(Survey survey, ResultFilter filter, String email, String from, String host)
	{
		this.survey = survey;
		this.filter = filter;
		this.email = email;
		this.from = from;
		this.host = host;
	}
	
	public void run()
	{
		String uid = UUID.randomUUID().toString();
		
		pdfService.createAllIndividualResultsPDF(survey, filter, uid); 
			
		File f = new File();
	    f.setUid(uid);
	    f.setName("IndividualResults.zip");

	    fileService.add(f);
	    	
		String link = host + "files/" + uid+ "/";
		
		String body = "Your export of the individual results of survey " + survey.getShortname() + " has finished.<br /><br />You can download it here: <a href=\"" + link + "\">IndividualResults.zip</a>";

		try {
			mailService.SendHtmlMail(email, from, from, "Export finished", body, null);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
