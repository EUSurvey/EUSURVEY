package com.ec.survey.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Export;
import com.ec.survey.model.Form;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.WebserviceTask;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.PDFService;
import com.ec.survey.service.ParticipationService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.WebserviceService;
import com.ec.survey.tools.export.XlsExportCreator;
import com.ec.survey.tools.export.XmlExportCreator;

@Service("resultCreator")
@Scope("prototype")
public class ResultCreator implements Runnable {

	protected static final Logger logger = Logger.getLogger(TokenCreator.class);

	private String fileDir;
	private int task;
	private MessageSource resources;
	private Locale locale;
	
	@Resource(name = "fileService")
	protected FileService fileService;
	
	@Resource(name = "participationService")
	protected  ParticipationService participationService;
	
	@Resource(name = "webserviceService")
	protected WebserviceService webserviceService;
	
	@Resource(name = "answerService")
	protected AnswerService answerService;	
	
	@Resource(name = "surveyService")
	protected SurveyService surveyService;	
	
	@Resource(name = "pdfService")
	protected PDFService pdfService;	
	
	@Resource(name = "sessionFactory")
	protected SessionFactory sessionFactory;	 
	
	@Resource(name = "xlsExportCreator")
	private XlsExportCreator xlsExportCreator;
	
	@Resource(name = "xmlExportCreator")
	private XmlExportCreator xmlExportCreator;
	
	public void init(int task, String fileDir, MessageSource resources, Locale locale) {
		this.task = task;
		this.fileDir = fileDir;
		this.resources = resources;
		this.locale = locale;
	}
	
	@Override
	public void run() {
		try {
			String uid = UUID.randomUUID().toString();		
			String uid2 = UUID.randomUUID().toString();		
			
			WebserviceTask t = webserviceService.get(task);
			
	    	Form form = new Form(resources);
	    	Survey survey = surveyService.getSurvey(t.getSurveyId(), false, true);
	    	form.setSurvey(survey);
	    	
	    	Export export = new Export();
	    	export.setSurvey(survey);
	    	export.setDate(new Date());
	    	export.setShowShortnames(t.isShowIDs());
	    	
	    	ResultFilter filter = new ResultFilter();
	    	filter.setInvitation(t.getToken());
	    	export.setResultFilter(filter);	    	
	    
			xlsExportCreator.init(0,form,export,fileDir + uid, resources, locale, "", "");			
			xlsExportCreator.ExportContent(null, false);
			
			xmlExportCreator.init(0,form,export,fileDir + uid, resources, locale, "", "");
			xmlExportCreator.ExportContent(false);
			
			AnswerSet answerSet = answerService.getByInvitationCode(t.getToken());
			java.io.File answerPDF = pdfService.createAnswerPDF(answerSet);
			
			File f = new File();
	    	f.setUid(uid2);
			f.setName("results.zip");	
			
			java.io.File temp = fileService.getSurveyFile(survey.getUniqueId(), uid2); 
			final OutputStream out = new FileOutputStream(temp);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);				
					
			if (export.getZipped())
			{
				//the file already is a zip file -> copy content				
				final ZipArchiveInputStream is = (ZipArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("zip", new FileInputStream(new java.io.File(fileDir + uid)));
				ZipArchiveEntry  entry;
				while ((entry = (ZipArchiveEntry) is.getNextEntry()) != null) {
                	os.putArchiveEntry(new ZipArchiveEntry(entry.getName()));
                	IOUtils.copy(is, os);
                	os.closeArchiveEntry();
                }
                is.close();
                
                os.putArchiveEntry(new ZipArchiveEntry("result.pdf"));
			    IOUtils.copy(new FileInputStream(answerPDF), os);
			    os.closeArchiveEntry();
			} else {
				os.putArchiveEntry(new ZipArchiveEntry("results.xls"));
			    IOUtils.copy(new FileInputStream(new java.io.File(fileDir + uid)), os);
			    os.closeArchiveEntry();
				
			    os.putArchiveEntry(new ZipArchiveEntry("result.pdf"));
			    IOUtils.copy(new FileInputStream(answerPDF), os);
			    os.closeArchiveEntry();
						}
		    os.close();
		    
			fileService.add(f);

			t.setResult(uid2);
			t.setDone(true);
			webserviceService.save(t);
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			try {
				webserviceService.setError(task, e.getLocalizedMessage() != null ?  e.getLocalizedMessage() : e.toString());
			} catch (InterruptedException e1) {
				logger.error(e1.getLocalizedMessage(), e1);
			}
		}		
		logger.info("TokenCreator completed");
	}
	
}
