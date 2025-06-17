package com.ec.survey.tools;

import com.ec.survey.model.*;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.delphi.DelphiCommentLike;
import com.ec.survey.model.delphi.DelphiExplanationLike;
import com.ec.survey.model.selfassessment.SACriterion;
import com.ec.survey.model.selfassessment.SAReportConfiguration;
import com.ec.survey.model.selfassessment.SAScoreCard;
import com.ec.survey.model.selfassessment.SATargetDataset;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.*;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.beans.XMLDecoder;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.zip.ZipException;

public class SurveyExportHelper {

	private static final Logger logger = Logger.getLogger(SurveyExportHelper.class);
	
	private static void addSurveyData(Survey survey, ArchiveOutputStream os, Session session, FileService fileService, SessionService sessionService, String fileDir, List<String> writtenFiles) throws Exception {
		 //create logo / download files
	    if (survey.getLogo() != null)
	    {
	    	File logo = survey.getLogo();
	    	
	    	java.io.File source = fileService.getSurveyFile(survey.getUniqueId(), logo.getUid());
	    	if (!source.exists())
	    	{
	    		source = new java.io.File(fileDir + logo.getUid());
	    		if (source.exists())
				{
					fileService.logOldFileSystemUse(fileDir + logo.getUid());
				}
	    	}
	    	
	    	if (source.exists())
	    	{
		    	os.putArchiveEntry(new ZipArchiveEntry(logo.getUid() + ".fil"));
		    	FileInputStream fis = null;
		    	try {
		    		fis = new FileInputStream(source);
		    		IOUtils.copy(fis, os);
		    	}
		    	finally {
		    		if (fis != null)
		    		{
		    			fis.close();
		    		}
		    	}
			    os.closeArchiveEntry();
			    writtenFiles.add(logo.getUid() + ".fil");
	    	}
	    }
	    for (com.ec.survey.model.survey.Element question : survey.getElementsRecursive()) {
			if (question instanceof Download)
			{
				for (File file: ((Download)question).getFiles())
				{
					java.io.File source = fileService.getSurveyFile(survey.getUniqueId(), file.getUid());
			    	if (!source.exists())
			    	{
			    		source = new java.io.File(fileDir + file.getUid());
			    		if (source.exists())
						{
							fileService.logOldFileSystemUse(fileDir + file.getUid());
						}
			    	}
					
			    	if (source.exists())
			    	{
				    	os.putArchiveEntry(new ZipArchiveEntry(file.getUid() + ".fil"));
				    	FileInputStream fis = null;
				    	try {
				    		fis = new FileInputStream(source);
				    		IOUtils.copy(fis, os);
				    	}
				    	finally {
				    		if (fis != null)
				    		{
				    			fis.close();
				    		}
				    	}
					    os.closeArchiveEntry();
					    writtenFiles.add(file.getUid() + ".fil");
			    	}
				}
			} else if (question instanceof Confirmation)
			{
				for (File file: ((Confirmation)question).getFiles())
				{
					java.io.File source = fileService.getSurveyFile(survey.getUniqueId(), file.getUid());
			    	if (!source.exists())
			    	{
			    		source = new java.io.File(fileDir + file.getUid());
			    		if (source.exists())
						{
							fileService.logOldFileSystemUse(fileDir + file.getUid());
						}
			    	}
					if (source.exists())
			    	{
				    	os.putArchiveEntry(new ZipArchiveEntry(file.getUid() + ".fil"));
				    	FileInputStream fis = null;
				    	try {
				    		fis = new FileInputStream(source);
				    		IOUtils.copy(fis, os);
				    	}
				    	finally {
				    		if (fis != null)
				    		{
				    			fis.close();
				    		}
				    	}
					    os.closeArchiveEntry();
					    writtenFiles.add(file.getUid() + ".fil");
			    	}
				}
			} else if (question instanceof Image)
			{
				Image image = (Image)question;
				
				if (image.getUrl() != null && !image.getUrl().contains(sessionService.getContextPath() + "/resources/"))
				{
					String fileUID = image.getUrl().substring( image.getUrl().lastIndexOf(Constants.PATH_DELIMITER)+1);
					File f = fileService.get(fileUID, false);
					if (f != null)
					{
						os.putArchiveEntry(new ZipArchiveEntry(fileUID + ".file"));
						    IOUtils.copy(new FileInputStream(getFileForObject(f, session, fileService)), os);
					    os.closeArchiveEntry();
					    writtenFiles.add(fileUID + ".file");
					}
					
					java.io.File source = fileService.getSurveyFile(survey.getUniqueId(), fileUID);
			    	if (!source.exists())
			    	{
			    		source = new java.io.File(fileDir + fileUID);
			    		if (source.exists())
						{
							fileService.logOldFileSystemUse(fileDir + fileUID);
						}
			    	}
					if (source.exists())
			    	{
				    	os.putArchiveEntry(new ZipArchiveEntry(fileUID + ".fil"));
				    	FileInputStream fis = null;
				    	try {
				    		fis = new FileInputStream(source);
				    		IOUtils.copy(fis, os);
				    	}
				    	finally {
				    		if (fis != null)
				    		{
				    			fis.close();
				    		}
				    	}
					    os.closeArchiveEntry();
					    writtenFiles.add(fileUID + ".fil");
			    	}
				}
			} else if (question instanceof GalleryQuestion)
			{
				GalleryQuestion gallery = (GalleryQuestion)question;
				for (File file: gallery.getFiles())
				{
					java.io.File source = fileService.getSurveyFile(survey.getUniqueId(), file.getUid());
			    	if (!source.exists())
			    	{
			    		source = new java.io.File(fileDir + file.getUid());
			    		if (source.exists())
						{
							fileService.logOldFileSystemUse(fileDir + file.getUid());
						}
			    	}
					if (source.exists())
			    	{
				    	os.putArchiveEntry(new ZipArchiveEntry(file.getUid() + ".fil"));
				    	FileInputStream fis = null;
				    	try {
				    		fis = new FileInputStream(source);
				    		IOUtils.copy(fis, os);
				    	}
				    	finally {
				    		if (fis != null)
				    		{
				    			fis.close();
				    		}
				    	}
				    	
					    os.closeArchiveEntry();
					    writtenFiles.add(file.getUid() + ".fil");
			    	}
				}
			}
		}
	    for (String url :  survey.getBackgroundDocuments().values()) {
	    	try {
				String uid = url.substring(url.lastIndexOf(Constants.PATH_DELIMITER) + 1);

				File fi = fileService.get(uid, false);
				if (fi != null) {
					os.putArchiveEntry(new ZipArchiveEntry(uid + ".file"));
					IOUtils.copy(new FileInputStream(getFileForObject(fi, session, fileService)), os);
					os.closeArchiveEntry();
					writtenFiles.add(uid + ".file");
				}

				java.io.File f = fileService.getSurveyFile(survey.getUniqueId(), uid);
				if (!f.exists()) {
					f = new java.io.File(fileDir + uid);
					if (f.exists()) {
						fileService.logOldFileSystemUse(fileDir + uid);
					}
				}
				if (f.exists()) {
					os.putArchiveEntry(new ZipArchiveEntry(uid + ".fil"));
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(f);
						IOUtils.copy(fis, os);
					} finally {
						if (fis != null) {
							fis.close();
						}
					}
					os.closeArchiveEntry();
					writtenFiles.add(uid + ".fil");
				}
			} catch (Exception e){
	    		//continue
			}
		}		   
	}
		
	public static java.io.File exportSurvey(String shortname, SurveyService surveyService, boolean answers,
			TranslationService translationService, AnswerService answerService, String fileDir,
			SessionService sessionService, FileService fileService, Session session, String host,
			AnswerExplanationService answerExplanationService, SelfAssessmentService selfAssessmentService) {
		
		try {
			
			Survey survey = surveyService.getSurvey(shortname,true,false,false,true, null, true, false);
			Survey activeSurvey = surveyService.getSurvey(survey.getShortname(), false, false, true, true, null, true, false);
			
			java.io.File temp = fileService.createTempFile("export" + UUID.randomUUID().toString(), ".zip"); 
			final OutputStream out = new FileOutputStream(temp);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);
		
			List<String> writtenFiles = new ArrayList<>();
			
			 try {
				URL url = new URL(host + "info/version");
				String eusurveyversion = "unknown";
				
				try {                   
					URLConnection wc = url.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(wc.getInputStream()));
					String inputLine;
					StringBuilder result = new StringBuilder();
					while ((inputLine = in.readLine()) != null) result.append(inputLine);
						in.close();
					eusurveyversion = result.toString();
				} catch (ConnectException e) {
					logger.error(e.getLocalizedMessage(), e);
				}                               
				
				int version = surveyService.getDBVersion();
				java.io.File f = fileService.getTemporaryFile();
				FileWriter writer = new FileWriter(f);
				writer.write("exported with EUSurvey version " + eusurveyversion + " DB version " + version + " at " + new Date());
				writer.close();
				os.putArchiveEntry(new ZipArchiveEntry("info.txt"));
				IOUtils.copy(new FileInputStream(f), os);
				os.closeArchiveEntry();
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

			addObjectAsFileToOutputStream(survey, "survey.eus", os, session, fileService);
		    addSurveyData(survey, os, session, fileService, sessionService, fileDir, writtenFiles);		   
		    
		    if (activeSurvey != null) {
				addObjectAsFileToOutputStream(activeSurvey, "survey-active.eus", os, session, fileService);
			    addSurveyData(activeSurvey, os, session, fileService, sessionService, fileDir, writtenFiles);
			    
			    if (answers)
			    {
			    	//also all historical survey versions
			    	List<Integer> allsurveys = surveyService.getAllSurveyVersions(activeSurvey.getId());
			    	for (Integer id : allsurveys) {
						if (!id.equals(survey.getId()) && !id.equals(activeSurvey.getId()))
						{
							Survey s = surveyService.getSurvey(id);
							surveyService.initializeSurvey(s);
							addObjectAsFileToOutputStream(s, "survey-active-" + id + ".eus", os, session, fileService);
						    
						    addSurveyData(s, os, session, fileService, sessionService, fileDir, writtenFiles);
						}
					}
			    }
		    }

		    List<Translations> translations = translationService.getTranslationsForSurvey(survey.getId(), false);
			addObjectAsFileToOutputStream(translations, "translations.eus", os, session, fileService);
		    
		    if (activeSurvey != null) {
		    	translations = translationService.getTranslationsForSurvey(activeSurvey.getId(), false);
				addObjectAsFileToOutputStream(translations, "translations-active.eus", os, session, fileService);
			    
			    if (answers) {
			    	//also all historical survey versions
			    	List<Integer> allsurveys = surveyService.getAllSurveyVersions(activeSurvey.getId());
			    	for (Integer id : allsurveys) {
						if (!id.equals(survey.getId()) && !id.equals(activeSurvey.getId()))
						{
							translations = translationService.getTranslationsForSurvey(id, false);
							addObjectAsFileToOutputStream(translations, "translations-active-" + id + ".eus", os,
									session, fileService);
						}
			    	}
			    }			    
		    }
		    
		    if (survey.getIsSelfAssessment()) {
		    	List<SATargetDataset> datasets = selfAssessmentService.getTargetDatasets(survey.getUniqueId());
		    	addObjectAsFileToOutputStream(datasets, "targetdatasets.eus", os, session, fileService);
		    	
		    	List<SACriterion> criteria = selfAssessmentService.getCriteria(survey.getUniqueId());
		    	addObjectAsFileToOutputStream(criteria, "criteria.eus", os, session, fileService);
		    	
		    	List<SAScoreCard> cards = new ArrayList<>();
		    	for (SATargetDataset dataset : datasets) {
					SAScoreCard card = selfAssessmentService.getScoreCard(dataset.getId(), true);
					if (card != null) {
						cards.add(card);
					}
		    	}		    
		    	addObjectAsFileToOutputStream(cards, "scorecards.eus", os, session, fileService);
		    	
		    	SAReportConfiguration config = selfAssessmentService.getReportConfiguration(survey.getUniqueId());
		    	addObjectAsFileToOutputStream(config, "reportconfiguration.eus", os, session, fileService);
		    }
			
		    //create answers file
		    if (answers) {
		    	List<AnswerSet> answerSets = answerService.getAllAnswers(survey.getId(), null);
				addObjectAsFileToOutputStream(answerSets, "answers.eus", os, session, fileService);
			    
			    Map<Integer, List<File>> files = new HashMap<>();
			    for (AnswerSet set: answerSets)
			    {
			    	for (Answer answer: set.getAnswers())
			    	{
			    		if (answer.getFiles() != null)
			    		{
			    			List<File> tempFiles = new ArrayList<>();
			    			for (File file: answer.getFiles())
			    			{
			    				tempFiles.add(file);
			    				
			    				java.io.File f = fileService.getSurveyFile(survey.getUniqueId(), file.getUid());
			    		    	if (!f.exists())
			    		    	{
			    		    		f = new java.io.File(fileDir + file.getUid());
			    		    		if (f.exists())
									{
										fileService.logOldFileSystemUse(fileDir + file.getUid());
									}
			    		    	}
			    				os.putArchiveEntry(new ZipArchiveEntry("files/" + file.getUid() + ".eus"));
			    				FileInputStream fis = null;
						    	try {
						    		fis = new FileInputStream(f);
						    		IOUtils.copy(fis, os);
						    	}
						    	finally {
						    		if (fis != null)
						    		{
						    			fis.close();
						    		}
						    	}
							    os.closeArchiveEntry();
			    			}
			    			files.put(answer.getId(), tempFiles);
			    		}
			    	}
			    }
			    
			    if (files.size() > 0) {
					addObjectAsFileToOutputStream(files, "files.eus", os, session, fileService);
			    }
			    
			    //also from active survey
			    if (activeSurvey != null)
			    {
			    	files = new HashMap<>();
			    	boolean stop = false;
			    	int counter = 0;
			    	while (!stop)
			    	{
			    		counter++;
						SqlPagination sqlPagination = new SqlPagination(counter, 100);
						answerSets = answerService.getAnswers(activeSurvey, null, sqlPagination, false, true, false);			    		
			    		if (answerSets.isEmpty())
			    		{
			    			stop = true;
			    		} else {
							addObjectAsFileToOutputStream(answerSets, "answers-active" + counter + ".eus", os, session, fileService);
						    
						    for (AnswerSet set: answerSets)
						    {
						    	for (Answer answer: set.getAnswers())
						    	{
						    		if (answer.getFiles() != null)
						    		{
						    			List<File> tempFiles = new ArrayList<>();
						    			for (File file: answer.getFiles())
						    			{
						    				tempFiles.add(file);
						    				
						    				java.io.File f = fileService.getSurveyFile(survey.getUniqueId(), file.getUid());
						    		    	if (!f.exists())
						    		    	{
						    		    		f = new java.io.File(fileDir + file.getUid());
						    		    		if (f.exists())
												{
													fileService.logOldFileSystemUse(fileDir + file.getUid());
												}
						    		    	}
						    				
						    				if (f.exists())
						    				{
							    				os.putArchiveEntry(new ZipArchiveEntry(file.getUid() + ".fil"));
							    				FileInputStream fis = null;
										    	try {
										    		fis = new FileInputStream(f);
										    		IOUtils.copy(fis, os);
										    	}
										    	finally {
										    		if (fis != null)
										    		{
										    			fis.close();
										    		}
										    	}
											    os.closeArchiveEntry();
						    				}
						    			}
						    			files.put(answer.getId(), tempFiles);
						    		}
						    	}
						    }
			    		}
			    	}			    	
				    
				    if (files.size() > 0) {
						addObjectAsFileToOutputStream(files, "files-active.eus", os, session, fileService);
				    }
			    }

			    if (survey.getIsDelphi()) {
					addDelphiAnswerExplanations(os, session, answerService, answerExplanationService, fileService,
							surveyService, survey, activeSurvey);
					addDelphiAnswerComments(os, session, answerExplanationService, fileService, survey, activeSurvey);
				}
		    }
		    
		    os.close();
			
			return temp;
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		} 		
		
		return null;
	}

	private static void addDelphiAnswerExplanations(final ArchiveOutputStream os, final Session session,
			final AnswerService answerService, final AnswerExplanationService answerExplanationService,
			final FileService fileService, final SurveyService surveyService, final Survey survey,
			final Survey activeSurvey) throws Exception {

		List<AnswerExplanation> explanations = answerExplanationService.getExplanationsOfSurvey(survey.getUniqueId(), survey.getIsDraft());
		var likes = answerExplanationService.getAllLikesForExplanations(explanations);

		addObjectAsFileToOutputStream(explanations, "explanations.eus", os, session, fileService);
		addObjectAsFileToOutputStream(likes, "explanation-likes.eus", os, session, fileService);
		addDelphiAnswerExplanationFilesToOutputStream(os, answerService, fileService, surveyService, survey,
				explanations);

		if (activeSurvey != null) {
			explanations = answerExplanationService.getExplanationsOfSurvey(activeSurvey.getUniqueId(), activeSurvey.getIsDraft());
			likes = answerExplanationService.getAllLikesForExplanations(explanations);

			addObjectAsFileToOutputStream(explanations, "explanations-active.eus", os, session, fileService);
			addObjectAsFileToOutputStream(likes, "explanation-likes-active.eus", os, session, fileService);
			addDelphiAnswerExplanationFilesToOutputStream(os, answerService, fileService, surveyService, survey,
					explanations);
		}
	}

	private static void addDelphiAnswerComments(final ArchiveOutputStream os, final Session session,
			final AnswerExplanationService answerExplanationService, final FileService fileService,
			final Survey survey, final Survey activeSurvey) throws Exception {

		List<AnswerComment> comments = answerExplanationService.getCommentsOfSurvey(survey.getUniqueId(), survey.getIsDraft());
		var likes = answerExplanationService.getAllLikesForComments(comments);
		addObjectAsFileToOutputStream(comments, "comments.eus", os, session, fileService);
		addObjectAsFileToOutputStream(likes, "comment-likes.eus", os, session, fileService);
		if (activeSurvey != null) {
			comments = answerExplanationService.getCommentsOfSurvey(activeSurvey.getUniqueId(), activeSurvey.getIsDraft());
			likes = answerExplanationService.getAllLikesForComments(comments);
			addObjectAsFileToOutputStream(comments, "comments-active.eus", os, session, fileService);
			addObjectAsFileToOutputStream(likes, "comment-likes-active.eus", os, session, fileService);
		}
	}

	private static void addDelphiAnswerExplanationFilesToOutputStream(final ArchiveOutputStream os,
			final AnswerService answerService, final FileService fileService, final SurveyService surveyService,
			final Survey survey, final List<AnswerExplanation> explanations) throws Exception {

		final Map<Integer, List<File>> files = new HashMap<>();
		for (final AnswerExplanation explanation : explanations) {
			if (!explanation.getFiles().isEmpty()) {
				final List<File> filesPerExplanation = new ArrayList<>();
				for (final File file: explanation.getFiles()) {
					filesPerExplanation.add(file);				
					java.io.File f = fileService.getSurveyFile(survey.getUniqueId(), file.getUid());
					
					os.putArchiveEntry(new ZipArchiveEntry(file.getUid() + ".fil"));
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(f);
						IOUtils.copy(fis, os);
					}
					finally {
						if (fis != null) {
							fis.close();
						}
					}
					os.closeArchiveEntry();					
				}
				files.put(explanation.getId(), filesPerExplanation);
			}
		}		
	}

	private static void addObjectAsFileToOutputStream(final Object original, final String fileName,
			final ArchiveOutputStream os, final Session session, final FileService fileService) throws Exception {

		os.putArchiveEntry(new ZipArchiveEntry(fileName));
		IOUtils.copy(new FileInputStream(getFileForObject(original, session, fileService)), os);
		os.closeArchiveEntry();
	}
	
	private static java.io.File getFileForObject(Object original, Session session, FileService fileService) throws Exception
	{
		Object o = HibernateCleaner.clean(session, original);
		
		java.io.File f = fileService.createTempFile(Constants.SURVEY + UUID.randomUUID().toString(), ".eus"); 
		ObjectOutput ObjOut = new ObjectOutputStream(new FileOutputStream(f));			 
		ObjOut.writeObject(o);			  
		ObjOut.close();	
		
		return f;
	}

	@SuppressWarnings("unchecked")
	public static ImportResult importSurvey(java.io.File file, FileService fileService, String email) {
		
		ImportResult result = new ImportResult();
		
		String uid = UUID.randomUUID().toString();
		
		try {
			
			ZipFile zipFile = new ZipFile(file);
			Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
	        while (entries.hasMoreElements()) {
	        	ZipArchiveEntry zipEntry = entries.nextElement();
	        	String name = zipEntry.getName();
	        	
	        	if (name.equalsIgnoreCase("survey.eus"))
				{
	        		result.setSurvey((Survey) getObject(zipFile, zipEntry, fileService));
	        		result.getSurvey().setUniqueId(uid);
	        		result.getSurvey().setIsActive(false);
	        		result.getSurvey().setSkin(null);
	        		result.getSurvey().setNumberOfAnswerSets(0);
	        		result.getSurvey().setNumberOfAnswerSetsPublished(0);
	        		result.getSurvey().getPublication().setShowContent(false);
	        		result.getSurvey().getPublication().setShowStatistics(false);
	        		result.getSurvey().getPublication().setShowCharts(false);	        		
	        		if (email != null) result.getSurvey().setContact(email);
	        		if (!fileService.isDelphiEnabled()) result.getSurvey().setIsDelphi(false);
	        		
	        		if (result.getSurvey().getVersion() < 26)
	        		{
	        			for (com.ec.survey.model.survey.Element element : result.getSurvey().getElements())
	        			{
	        				if (element instanceof NumberQuestion)
	        				{
	        					NumberQuestion q = (NumberQuestion)element;
	        					q.upgrade();	        					
	        				}
	        			}
	        		}
				} else if (name.equalsIgnoreCase("survey-active.eus"))
				{
	        		result.setActiveSurvey((Survey) getObject(zipFile, zipEntry, fileService));
	        		result.getActiveSurvey().setUniqueId(uid);
	        		result.getActiveSurvey().setIsActive(false);
	        		result.getActiveSurvey().setSkin(null);
	        		result.getActiveSurvey().setNumberOfAnswerSets(0);
	        		result.getActiveSurvey().setNumberOfAnswerSetsPublished(0);
	        		result.getActiveSurvey().getPublication().setShowContent(false);
	        		result.getActiveSurvey().getPublication().setShowStatistics(false);
	        		result.getActiveSurvey().getPublication().setShowCharts(false);	        		
	        		if (email != null) result.getActiveSurvey().setContact(email);
					if (!fileService.isDelphiEnabled()) result.getSurvey().setIsDelphi(false);
	        		
	        		if (result.getActiveSurvey().getVersion() < 26)
	        		{
	        			for (com.ec.survey.model.survey.Element element : result.getActiveSurvey().getElements())
	        			{
	        				if (element instanceof NumberQuestion)
	        				{
	        					NumberQuestion q = (NumberQuestion)element;
	        					q.upgrade();	        					
	        				}
	        			}
	        		}
				} else if (name.startsWith("survey-active-"))
				{
					Survey oldSurvey = (Survey) getObject(zipFile, zipEntry, fileService);
	        		
					oldSurvey.setUniqueId(uid);
					oldSurvey.setIsActive(false);
					oldSurvey.setSkin(null);
					oldSurvey.setNumberOfAnswerSets(0);
					oldSurvey.setNumberOfAnswerSetsPublished(0);
					oldSurvey.getPublication().setShowContent(false);
					oldSurvey.getPublication().setShowStatistics(false);
					oldSurvey.getPublication().setShowCharts(false);	        		
					if (email != null) oldSurvey.setContact(email);
					if (!fileService.isDelphiEnabled()) oldSurvey.setIsDelphi(false);

	        		if (oldSurvey.getVersion() < 26)
	        		{
	        			for (com.ec.survey.model.survey.Element element : oldSurvey.getElements())
	        			{
	        				if (element instanceof NumberQuestion)
	        				{
	        					NumberQuestion q = (NumberQuestion)element;
	        					q.upgrade();	        					
	        				}
	        			}
	        		}
	        		
	        		result.getOldSurveys().put(oldSurvey.getId(), oldSurvey);
				} else if (name.equalsIgnoreCase("translations.eus"))
				{
					result.setTranslations((List<Translations>)  getObject(zipFile, zipEntry, fileService));
				} else if (name.equalsIgnoreCase("translations-active.eus"))
				{
					result.setActiveTranslations((List<Translations>)  getObject(zipFile, zipEntry, fileService));
				} else if (name.startsWith("translations-active-"))
				{
					int oldid = Integer.parseInt(name.replace("translations-active-", "").replace(".eus", ""));
					List<Translations> oldTranslations = (List<Translations>) getObject(zipFile, zipEntry, fileService);
					result.getOldTranslations().put(oldid, oldTranslations);
				} else if (name.equalsIgnoreCase("answers.eus"))
				{
	        		result.setAnswerSets((List<AnswerSet>) getObject(zipFile, zipEntry, fileService));
				} else if (name.startsWith("answers-active"))
				{
					if (result.getActiveAnswerSets() == null)
					{
						result.setActiveAnswerSets(new ArrayList<>());
					}
	        		result.getActiveAnswerSets().add((List<AnswerSet>) getObject(zipFile, zipEntry, fileService));       		
				} else if (name.equalsIgnoreCase("files.eus"))
				{
	        		result.setFiles((Map<Integer, List<File>>) getObject(zipFile, zipEntry, fileService));
				} else if (name.equalsIgnoreCase("files-active.eus"))
				{
	        		result.setActiveFiles((Map<Integer, List<File>>) getObject(zipFile, zipEntry, fileService));
				} else if (name.equalsIgnoreCase("explanations.eus"))
				{
					result.setExplanations((List<AnswerExplanation>)getObject(zipFile, zipEntry, fileService));	
				} else if (name.equalsIgnoreCase("explanations-active.eus"))
				{
					result.setActiveExplanations((List<AnswerExplanation>)getObject(zipFile, zipEntry, fileService));
				} else if (name.equalsIgnoreCase("comments.eus"))
				{
					result.setComments((List<AnswerComment>)getObject(zipFile, zipEntry, fileService));	
				} else if (name.equalsIgnoreCase("comments-active.eus"))
				{
					result.setActiveComments((List<AnswerComment>)getObject(zipFile, zipEntry, fileService));		
				} else if (name.equalsIgnoreCase("explanation-likes.eus"))
				{
					result.setExplanationLikes((List<DelphiExplanationLike>) getObject(zipFile, zipEntry, fileService));
				} else if (name.equalsIgnoreCase("explanation-likes-active.eus"))
				{
					result.setActiveExplanationLikes((List<DelphiExplanationLike>) getObject(zipFile, zipEntry, fileService));
				} else if (name.equalsIgnoreCase("comment-likes.eus"))
				{
					result.setCommentLikes((List<DelphiCommentLike>) getObject(zipFile, zipEntry, fileService));
				} else if (name.equalsIgnoreCase("comment-likes-active.eus"))
				{
					result.setActiveCommentLikes((List<DelphiCommentLike>) getObject(zipFile, zipEntry, fileService));
				} else if (name.equalsIgnoreCase("targetdatasets.eus")) {
					result.setTargetDatasets((List<SATargetDataset>)getObject(zipFile, zipEntry, fileService));
				} else if (name.equalsIgnoreCase("criteria.eus")) {	
					result.setCriteria((List<SACriterion>)getObject(zipFile, zipEntry, fileService));
				} else if (name.equalsIgnoreCase("scorecards.eus")) {	
					result.setScoreCards((List<SAScoreCard>)getObject(zipFile, zipEntry, fileService));
				} else if (name.equalsIgnoreCase("reportconfiguration.eus")) {	
					result.setReportConfiguration((SAReportConfiguration)getObject(zipFile, zipEntry, fileService));
				} else if (name.endsWith("fil"))
	        	{
					String fileUID = name.replace(".fil", "");
					java.io.File target = fileService.getSurveyFile(uid, fileUID);
	        		saveFile(zipFile, zipEntry, target.getPath());
	        	} else if (name.endsWith("file"))
	        	{
	        		Object o = getObject(zipFile, zipEntry, fileService);
	        		if (o instanceof File)
	        		{
	        			File f = (File)o;
	        			try {
	        				fileService.get(f.getUid());
	        			} catch (Exception e)
	        			{
	        				if (e.getMessage().contains("No file found for uid"))
	        				{
	        					f.setId(null);
	        					fileService.add(f);
	        				}
	        			}
	        		}
	        		
	        	}
	        }
			
	        zipFile.close();
	        
			return result;			
		} catch (ZipException ze) {
			//the zip archive seems to be invalid
			return null;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return null;
	}
	
	private static void saveFile(ZipFile zipFile, ZipArchiveEntry zipEntry, String filePath) throws IOException 
	{
		java.io.File destinationFile = new java.io.File(filePath);
		
		if (!destinationFile.exists())
		{
			byte[] buf = new byte[65536];
			int n;
			
			try (FileOutputStream fos = new FileOutputStream(destinationFile)) {	            
	            InputStream entryContent = zipFile.getInputStream(zipEntry);
	            while ((n = entryContent.read(buf)) != -1) {
	                if (n > 0) {
	                    fos.write(buf, 0, n);
	                }
	            }
	        }
		}
	}
	
	private static Object getObject(ZipFile zipFile, ZipArchiveEntry zipEntry, FileService fileService) throws IOException, ClassNotFoundException
	{
		java.io.File destinationFile = fileService.createTempFile("importSurveyAnswers" + UUID.randomUUID().toString(), ".eus"); 
		byte[] buf = new byte[65536];
		int n;
		
		try (FileOutputStream fos = new FileOutputStream(destinationFile)) {            
            InputStream entryContent = zipFile.getInputStream(zipEntry);
            while ((n = entryContent.read(buf)) != -1) {
                if (n > 0) {
                    fos.write(buf, 0, n);
                }
            }
        }
		
		Object result;
		try (ObjectInputStream obj = new ObjectInputStream(new FileInputStream(destinationFile))) {
			result = obj.readObject();
		}	
		
		return result;
	}
	
}
