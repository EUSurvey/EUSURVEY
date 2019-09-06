package com.ec.survey.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ec.survey.model.Export;
import com.ec.survey.model.Form;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.WebserviceTask;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.ExportService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.PDFService;
import com.ec.survey.service.ParticipationService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.TranslationService;
import com.ec.survey.service.WebserviceService;
import com.ec.survey.tools.export.XmlExportCreator;
import com.lowagie.text.pdf.PdfReader;

@Service("resultsCreator")
@Scope("prototype")
public class ResultsCreator implements Runnable, BeanFactoryAware {

	protected static final Logger logger = Logger.getLogger(ResultsCreator.class);

	@Resource(name = "fileService")
	protected FileService fileService;
	
	@Resource(name = "exportService")
	protected ExportService exportService;
	
	@Resource(name = "webserviceService")
	protected WebserviceService webserviceService;
	
	@Resource(name = "answerService")
	protected AnswerService answerService;	
	
	@Resource(name = "surveyService")
	protected SurveyService surveyService;	
	
	@Resource(name = "sessionFactory")
	protected SessionFactory sessionFactory;	
	
	@Resource(name = "participationService")
	protected  ParticipationService participationService;
	
	@Resource(name = "translationService")
	protected  TranslationService translationService;
	
	@Resource(name = "pdfService")
	protected PDFService pdfService;	
	
	private String fileDir;
	private int task;
	
	public Integer getTask() {
		return task;
	}
	public void setTask(int task) {
		this.task = task;
	}

	private MessageSource resources;
	private Locale locale;
	
	private BeanFactory context;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		context = beanFactory;		
	}
	
	public void init(int task, String fileDir, MessageSource resources, Locale locale) {
		this.task = task;
		this.fileDir = fileDir;
		this.resources = resources;
		this.locale = locale;
	}
	
	@Override
	public void run() {
		try {
			webserviceService.setStarted(task);
			WebserviceTask t = webserviceService.get(task);
						
			String uid = UUID.randomUUID().toString();
			
	    	Form form = new Form(resources);
	    	
	    	Survey survey = null;
	    	
	    	if (t.getSurveyUid() != null && t.getSurveyUid().length() > 0)
	    	survey = surveyService.getSurveyByUniqueId(t.getSurveyUid(), true, false);
	    	
	    	if (survey == null)
	    	{
	    		survey = surveyService.getSurvey(t.getSurveyId(), true, true);
	    	}
	    	
	    	if (survey == null)
	    	{
	    		webserviceService.setError(task, "Survey with id " + t.getSurveyId() + " not found");
	    		return;
	    	}
	    	
	    	List<String> translations = translationService.getTranslationLanguagesForSurvey(t.getSurveyId(),false);
			survey.setTranslations(translations);
	    	
	    	form.setSurvey(survey);
	    	
	    	Export export = new Export();
	    	export.setSurvey(survey);
	    	export.setDate(new Date());
	    	
	    	ResultFilter filter = new ResultFilter();
	    	
	    	if (t.getContributionType() != null && t.getContributionType().equalsIgnoreCase("N"))
	    	{
	    		filter.setGeneratedFrom(t.getStart());
		    	filter.setGeneratedTo(t.getEnd());	    	
	    	} else if (t.getContributionType() != null && t.getContributionType().equalsIgnoreCase("U"))
	    	{
	    		filter.setOnlyReallyUpdated(true);
	    		filter.setUpdatedFrom(t.getStart());
	    		filter.setUpdatedTo(t.getEnd());
	    	} else { //A
	    		filter.setCreatedOrUpdated(true);
	    		filter.setUpdatedFrom(t.getStart());
	    		filter.setUpdatedTo(t.getEnd());
	    		filter.setGeneratedFrom(t.getStart());
		    	filter.setGeneratedTo(t.getEnd());	
	    	}
	    	
	    	if (t.getToken() != null && t.getToken().length() > 0)
	    	{
	    		//this is a single answerSet export
	    		filter.setInvitation(t.getToken());
	    	}
	    	
	    	export.setShowShortnames(t.isShowIDs());
	    	export.setAddMeta(t.isAddMeta());
	    	export.setResultFilter(filter);	 
	   	
	    	XmlExportCreator xmlExportCreator = (XmlExportCreator) context.getBean("xmlExportCreator");
	    	xmlExportCreator.init(0,form, null,fileDir + uid, resources, locale, "", "");
	    	
	    	if (t.getExportType() != null && t.getExportType().equals(2))
	    	{
	    		xmlExportCreator.SimulateExportContent(false, export);
	    	} else {
	    		xmlExportCreator.ExportContent(false, export);
	    	}
	    	
	    	Map<Integer, String> uniqueCodesById = xmlExportCreator.getExportedUniqueCodes();
	    	Map<Integer, String> questionIdsByAnswerId = xmlExportCreator.getExportedQuestionsByAnswerId();
	    	Map<String, Element> questionsByUniqueId = survey.getElementsByUniqueId();
	    	
	    	if (uniqueCodesById.size() == 0)
	    	{
	    		t.setEmpty(true);
	    	}     	
	    	
	    	//results_[type]-<alias>[_<start-date>][_to_<end-date>].zip
	    	String zipFileName = "results";
	    	if (t.getExportType() != null)
	    	{
		    	if (t.getExportType().equals(1))
		    	{
		    		zipFileName += "_xml";
		    	} else if (t.getExportType().equals(2))
		    	{
		    		zipFileName += "_pdf";
		    	}
		    	zipFileName += "-" + survey.getShortname();
		    	if (t.getStart() != null)
		    	{
		    		zipFileName += "_" + ConversionTools.getFullString4Webservice(t.getStart());
		    	}
		    	if (t.getEnd() != null)
		    	{
		    		zipFileName += "_" + ConversionTools.getFullString4Webservice(t.getEnd());
		    	} else {
		    		zipFileName += "_" + ConversionTools.getFullString4Webservice(xmlExportCreator.getExportedNow());
		    	}
	    	}
	    	zipFileName += ".zip";
	    	
	    	String uid2 = UUID.randomUUID().toString();	
			
			java.io.File temp = fileService.getSurveyExportFile(survey.getUniqueId(), uid2); 
			final OutputStream out = new FileOutputStream(temp);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);		
			
	    	File f = new File();
	    	f.setUid(uid2);
			f.setName(zipFileName);
	    
	    	if (t.getExportType() == null || t.getExportType().equals(0) || t.getExportType().equals(1))
	    	{
				os.putArchiveEntry(new ZipArchiveEntry("result.xml"));
				IOUtils.copy(new FileInputStream(new java.io.File(fileDir + uid)), os);
				os.closeArchiveEntry();
		    }
			
			int invalidCounter = 0;
			
			for (Integer answerSetId : uniqueCodesById.keySet()) {
				
				String uniqueCode = uniqueCodesById.get(answerSetId);
				
				//this counter should stop creation of PDFs when there is an obvious problem
				if (t.getExportType() == null || t.getExportType().equals(0) || t.getExportType().equals(2))
		    	{
					if (invalidCounter < 3)
					{
						java.io.File answerPDF = pdfService.createAnswerPDF(answerSetId, uniqueCodesById.get(answerSetId), survey.getUniqueId());
						if (answerPDF != null)
						{
							//check validity of file
							PdfReader ReadInputPDF;
						
							try {
								ReadInputPDF = new PdfReader(answerPDF.getPath());
						        if (ReadInputPDF.getInfo().containsKey("Subject"))
						        {
						        	String subject = ReadInputPDF.getInfo().get("Subject").toString();
						        	if (!subject.contains(uniqueCodesById.get(answerSetId)))
						        	{
						        		//possibly an invalid pdf -> recreate
						        		answerPDF.delete();
						        		answerPDF = pdfService.createAnswerPDF(answerSetId, uniqueCodesById.get(answerSetId), survey.getUniqueId());
						        	}
						        } else {
						        	//older file without meta info -> recreate
						        	answerPDF.delete();
						        	answerPDF = pdfService.createAnswerPDF(answerSetId, uniqueCodesById.get(answerSetId), survey.getUniqueId());
						        }
						        ReadInputPDF.close();
							} catch (Exception e)
							{
								//file seems to be corrupt -> recreate
					        	answerPDF.delete();
					        	answerPDF = pdfService.createAnswerPDF(answerSetId, uniqueCodesById.get(answerSetId), survey.getUniqueId());
							}
						
							if (answerPDF != null)
							{
								os.putArchiveEntry(new ZipArchiveEntry(uniqueCode + "/" + uniqueCode + ".pdf"));
								IOUtils.copy(new FileInputStream(answerPDF), os);
								os.closeArchiveEntry();
							} else {
								os.putArchiveEntry(new ZipArchiveEntry(uniqueCode + "/" + uniqueCode + ".error.txt"));
								os.write("The PDF file could not be generated".getBytes());
								os.closeArchiveEntry();
								invalidCounter++;
								
								if (invalidCounter == 3)
								{
									os.putArchiveEntry(new ZipArchiveEntry("error.txt"));
									os.write("The PDF creation was stopped as the files could not be generated".getBytes());
									os.closeArchiveEntry();
								}
							}
						} else {
							os.putArchiveEntry(new ZipArchiveEntry(uniqueCode + "/" + uniqueCode + ".error.txt"));
							os.write("The PDF file could not be generated".getBytes());
							os.closeArchiveEntry();
							invalidCounter++;
							
							if (invalidCounter == 3)
							{
								os.putArchiveEntry(new ZipArchiveEntry("error.txt"));
								os.write("The PDF creation was stopped as the files could not be generated".getBytes());
								os.closeArchiveEntry();
							}
						}
					}
		    	}
				
				List<File> uploadedFiles = answerService.getUploadedFilesForAnswerset(answerSetId);
				
				for (File file: uploadedFiles)
		    	{
		    		java.io.File fup = fileService.getSurveyFile(survey.getUniqueId(),  file.getUid());
		    		
		    		if (!fup.exists())
		    		{
		    			fup = new java.io.File(fileDir + file.getUid());
		    			if (fup.exists())
		    			{
		    				fileService.LogOldFileSystemUse(fileDir + file.getUid());
		    			}
		    		}		
		    		
		    		String folder = file.getUid();
		    		if (file.getAnswerId() != null)
		    		{
		    			if (questionIdsByAnswerId.containsKey(file.getAnswerId()))
		    			{
		    				String questionUniqueId = questionIdsByAnswerId.get(file.getAnswerId());
		    				if (questionsByUniqueId.containsKey(questionUniqueId))
		    				{
		    					folder = questionsByUniqueId.get(questionUniqueId).getShortname();
		    				}
		    			}
		    		}
		    		
		    		os.putArchiveEntry(new ZipArchiveEntry(uniqueCode + "/Uploaded Files/" + folder + "/" + file.getName()));
				    IOUtils.copy(new FileInputStream(fup), os);
				    os.closeArchiveEntry();			    	
		    	}
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
			} catch (Exception e1) {
				logger.error(e1.getLocalizedMessage(), e1);
			} 
		}		
		logger.debug("TokenCreator completed");
	}
	
}
