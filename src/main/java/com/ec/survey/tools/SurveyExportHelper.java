package com.ec.survey.tools;

import com.ec.survey.model.*;
import com.ec.survey.model.administration.User;
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
					fileService.LogOldFileSystemUse(fileDir + logo.getUid());
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
							fileService.LogOldFileSystemUse(fileDir + file.getUid());
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
							fileService.LogOldFileSystemUse(fileDir + file.getUid());
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
					String fileUID = image.getUrl().substring( image.getUrl().lastIndexOf("/")+1);
					File f = fileService.get(fileUID);
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
							fileService.LogOldFileSystemUse(fileDir + fileUID);
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
							fileService.LogOldFileSystemUse(fileDir + file.getUid());
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
			String uid = url.substring(url.lastIndexOf("/")+1);
			
			File fi = fileService.get(uid);
			if (fi != null)
			{
				os.putArchiveEntry(new ZipArchiveEntry(uid + ".file"));
				    IOUtils.copy(new FileInputStream(getFileForObject(fi, session, fileService)), os);
			    os.closeArchiveEntry();
			    writtenFiles.add(uid + ".file");
			}
			
			java.io.File f = fileService.getSurveyFile(survey.getUniqueId(), uid);
	    	if (!f.exists())
	    	{
	    		f = new java.io.File(fileDir + uid);
	    		if (f.exists())
				{
					fileService.LogOldFileSystemUse(fileDir + uid);
				}
	    	}
			if (f.exists())
			{
				os.putArchiveEntry(new ZipArchiveEntry(uid + ".fil"));
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
			    writtenFiles.add(uid + ".fil");
			}
		}		   
	}
		
	public static java.io.File exportSurvey(String shortname, SurveyService surveyService, boolean answers, TranslationService translationService, AnswerService answerService, String fileDir, SessionService sessionService, FileService fileService, Session session, String host) {
		
		try {
			
			Survey survey = surveyService.getSurvey(shortname,true,false,false,true, null, true, false);
			Survey activeSurvey = surveyService.getSurvey(survey.getShortname(), false, false, true, true, null, true, false);
			
			java.io.File temp = fileService.createTempFile("export" + UUID.randomUUID().toString(), ".zip"); 
			final OutputStream out = new FileOutputStream(temp);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);
		
			List<String> writtenFiles = new ArrayList<>();
			
			 try {
				URL workerurl = new URL(host + "info/version");
				String eusurveyversion = "unknown";
				
				try {                   
					URLConnection wc = workerurl.openConnection();
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
			
			//create file containing the survey object
			os.putArchiveEntry(new ZipArchiveEntry("survey.eus"));
		    IOUtils.copy(new FileInputStream(getFileForObject(survey, session, fileService)), os);
		    os.closeArchiveEntry();

		    addSurveyData(survey, os, session, fileService, sessionService, fileDir, writtenFiles);		   
		    
		    if (activeSurvey != null)
		    {
		    	os.putArchiveEntry(new ZipArchiveEntry("survey-active.eus"));
			    IOUtils.copy(new FileInputStream(getFileForObject(activeSurvey, session, fileService)), os);
			    os.closeArchiveEntry();
			    
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
							os.putArchiveEntry(new ZipArchiveEntry("survey-active-" + id + ".eus"));
						    IOUtils.copy(new FileInputStream(getFileForObject(s, session, fileService)), os);
						    os.closeArchiveEntry();
						    
						    addSurveyData(s, os, session, fileService, sessionService, fileDir, writtenFiles);
						}
					}
			    }
		    }
						
			//create translations file
		    List<Translations> translations = translationService.getTranslationsForSurvey(survey.getId(), true);
			os.putArchiveEntry(new ZipArchiveEntry("translations.eus"));
		    IOUtils.copy(new FileInputStream(getFileForObject(translations, session, fileService)), os);
		    os.closeArchiveEntry();
		    
		    if (activeSurvey != null)
		    {
		    	translations = translationService.getTranslationsForSurvey(activeSurvey.getId(), true);
			 	os.putArchiveEntry(new ZipArchiveEntry("translations-active.eus"));
			    IOUtils.copy(new FileInputStream(getFileForObject(translations, session, fileService)), os);
			    os.closeArchiveEntry();
			    
			    if (answers)
			    {
			    	//also all historical survey versions
			    	List<Integer> allsurveys = surveyService.getAllSurveyVersions(activeSurvey.getId());
			    	for (Integer id : allsurveys) {
						if (!id.equals(survey.getId()) && !id.equals(activeSurvey.getId()))
						{
							translations = translationService.getTranslationsForSurvey(id, true);
						 	os.putArchiveEntry(new ZipArchiveEntry("translations-active-" + id + ".eus"));
						    IOUtils.copy(new FileInputStream(getFileForObject(translations, session, fileService)), os);
						    os.closeArchiveEntry();
						}
			    	}
			    }			    
		    }
			
		    //create answers file
		    if (answers)
		    {
		    	List<AnswerSet> answerSets = answerService.getAllAnswers(survey.getId(), null);
				os.putArchiveEntry(new ZipArchiveEntry("answers.eus"));
			    IOUtils.copy(new FileInputStream(getFileForObject(answerSets, session, fileService)), os);
			    os.closeArchiveEntry();
			    
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
										fileService.LogOldFileSystemUse(fileDir + file.getUid());
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
			    
			    if (files.size() > 0)
			    {
					os.putArchiveEntry(new ZipArchiveEntry("files.eus"));
				    IOUtils.copy(new FileInputStream(getFileForObject(files, session, fileService)), os);
				    os.closeArchiveEntry();
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
			    		answerSets = null;
						SqlPagination sqlPagination = new SqlPagination(counter, 100);
						answerSets = answerService.getAnswers(activeSurvey, null, sqlPagination, false, true, false);			    		
			    		if (answerSets.size() == 0)
			    		{
			    			stop = true;
			    		} else {
			    			os.putArchiveEntry(new ZipArchiveEntry("answers-active" + counter + ".eus"));
						    IOUtils.copy(new FileInputStream(getFileForObject(answerSets, session, fileService)), os);
						    os.closeArchiveEntry();
						    
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
													fileService.LogOldFileSystemUse(fileDir + file.getUid());
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
				    
				    if (files.size() > 0)
				    {
						os.putArchiveEntry(new ZipArchiveEntry("files-active.eus"));
					    IOUtils.copy(new FileInputStream(getFileForObject(files, session, fileService)), os);
					    os.closeArchiveEntry();
				    }
			    }
		    }
		    
		    os.close();
			
			return temp;
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		} 		
		
		return null;
	}
	
	private static java.io.File getFileForObject(Object original, Session session, FileService fileService) throws Exception
	{
		Object o = HibernateCleaner.clean(session, original);
		
		java.io.File f = fileService.createTempFile("survey" + UUID.randomUUID().toString(), ".eus"); 
		ObjectOutput ObjOut = new ObjectOutputStream(new FileOutputStream(f));			 
		ObjOut.writeObject(o);			  
		ObjOut.close();	
		
		return f;
	}

	@SuppressWarnings("unchecked")
	public static ImportResult importSurvey(java.io.File file, String fileDir, FileService fileService, String email) {
		
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
	
	private static void saveFile(ZipFile zipFile, ZipArchiveEntry zipEntry, String filePath) throws Exception
	{
		java.io.File destinationFile = new java.io.File(filePath);
		
		if (!destinationFile.exists())
		{
			FileOutputStream fos = null;
			byte[] buf = new byte[65536];
			int n;
			
			try {
	            fos = new FileOutputStream(destinationFile);
	            InputStream entryContent = zipFile.getInputStream(zipEntry);
	            while ((n = entryContent.read(buf)) != -1) {
	                if (n > 0) {
	                    fos.write(buf, 0, n);
	                }
	            }
	        } finally {
	            if (fos != null) {
	                fos.close();
	            }
	        }
		}
	}
	
	private static Object getObject(ZipFile zipFile, ZipArchiveEntry zipEntry, FileService fileService) throws Exception
	{
		java.io.File destinationFile = fileService.createTempFile("importSurveyAnswers" + UUID.randomUUID().toString(), ".eus"); 
		FileOutputStream fos = null;
		byte[] buf = new byte[65536];
		int n;
		
		try {
            fos = new FileOutputStream(destinationFile);
            InputStream entryContent = zipFile.getInputStream(zipEntry);
            while ((n = entryContent.read(buf)) != -1) {
                if (n > 0) {
                    fos.write(buf, 0, n);
                }
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
		
		Object result;
		
		if (zipEntry.getName().endsWith("eusx"))
		{
			XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(destinationFile)));
			result = d.readObject();
			d.close();
		} else {		
			ObjectInputStream obj = new ObjectInputStream(new FileInputStream(destinationFile));
			result = obj.readObject();
			obj.close();	
		}
		
		return result;
	}

	public static ImportResult importIPMSurvey(java.io.File f, SurveyService surveyService, User owner, String fileDir, FileService fileService, ServletContext servletContext, String email) throws IOException {
		ImportResult result = new ImportResult();
		result.setFromIPM(true);
		ZipFile zipFile = new ZipFile(f);
		Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
		
		File logo = null;
		String logouid = null;
		String logoname = "";
		
        while (entries.hasMoreElements()) {
        	 ZipArchiveEntry zipEntry = entries.nextElement();
             String name = zipEntry.getName();
             
             if (name.equalsIgnoreCase("export.xml"))
             {	
            	 importIPMSurveyXML(zipFile.getInputStream(zipEntry), surveyService, owner, result, servletContext); 
             } else if (name.equalsIgnoreCase("translations.xml"))
             {
            	 importIPMTranslationXML(zipFile.getInputStream(zipEntry), result, servletContext);
             } else {
            	 //must be the logo
            	 logouid = UUID.randomUUID().toString();
            	 java.io.File target = fileService.createTempFile(logouid, null);
            	 FileOutputStream fos = new FileOutputStream(target);
            	 IOUtils.copy(zipFile.getInputStream(zipEntry), fos);
            	 logoname = name; 	        
             }                          
        }
        
        result.getSurvey().setContact(email);
        
        if (logouid != null && result.getSurvey() != null)
        {
        	 java.io.File target = fileService.getSurveyFile(result.getSurvey().getUniqueId(), logouid);
        	 FileOutputStream fos = new FileOutputStream(target);
             IOUtils.copy(new FileInputStream(fileService.createTempFile(logouid, null)), fos);
             logo = new File();
             logo.setUid(logouid);
             logo.setName(logoname);
 	       
 	         fileService.add(logo);  
	         result.getSurvey().setLogo(logo);
        }
        
        DocumentBuilder builder = XHTMLValidator.getBuilder(servletContext);
        
        int repaired = surveyService.repairXHTML(result.getSurvey(), builder);
        for (Translations translations : result.getTranslations()) {
			repaired += surveyService.repairXHTML(translations, builder);
		}		        
        
        result.setInvalidCodeFound(repaired > 0);
        
        zipFile.close();
        
		return result;
	}

	private static int idcounter;
	private static int counter;
	private static List<Language> languages;
	public static void importIPMSurveyXML(InputStream f, SurveyService surveyService, User owner, ImportResult result, ServletContext servletContext) {
		
		try {
			
			languages = surveyService.getLanguages();
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			docFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			docFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			docFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			docFactory.setXIncludeAware(false);
			docFactory.setExpandEntityReferences(false);
			
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();	 
			Document doc = docBuilder.parse(f);			
			org.w3c.dom.Element Form = doc.getDocumentElement();
			
			org.w3c.dom.Element AdministrationProperties = (Element) Form.getElementsByTagName("AdministrationProperties").item(0);
			org.w3c.dom.Element SurveyProperties = (Element) Form.getElementsByTagName("SurveyProperties").item(0);
			org.w3c.dom.Element SurveyElement = (Element) Form.getElementsByTagName("Survey").item(0);
				
			Survey survey = new Survey();
			survey.setId(0);
			survey.setOwner(owner);
			
			String uid = UUID.randomUUID().toString();
			survey.setUniqueId(uid);
			
			if (SurveyProperties.getElementsByTagName("EmailContact").getLength() > 0)
			{
				org.w3c.dom.Element EmailContact = (Element) SurveyProperties.getElementsByTagName("EmailContact").item(0);
				org.w3c.dom.Element EmailAdr = (Element) EmailContact.getElementsByTagName("EmailAdr").item(0);
				survey.setContact(EmailAdr.getAttribute("Address"));
			} else if (SurveyProperties.getElementsByTagName("WebContact").getLength() > 0)
			{
				org.w3c.dom.Element WebContact = (Element) SurveyProperties.getElementsByTagName("WebContact").item(0);
				org.w3c.dom.Element WebAdr = (Element) WebContact.getElementsByTagName("WebAdr").item(0);
				
				String location = WebAdr.getAttribute("Address");
				if (!location.toLowerCase().startsWith("http"))
    			{
	    			location = "http://" + location;
    			}
				
				survey.setContact(location);
			} 			
			
			survey.setShortname(AdministrationProperties.getElementsByTagName("ShortName").item(0).getTextContent());
		    String lang = AdministrationProperties.getElementsByTagName("PivotLanguage").item(0).getTextContent().toUpperCase();
		    Language language = surveyService.getLanguage(lang);
		    survey.setLanguage(language);
		    
		    org.w3c.dom.Element Accessibility = (Element) AdministrationProperties.getElementsByTagName("Accessibility").item(0);
		    
		    idcounter = 1;
		    
		    if (Accessibility.getElementsByTagName("Anonymous").getLength() > 0)
		    {
		    	survey.setSecurity("open");
		    	
		    	org.w3c.dom.Element AntiRobot = (Element) Accessibility.getElementsByTagName("AntiRobot").item(0);
		    	if (AntiRobot != null && AntiRobot.getTextContent().equalsIgnoreCase("true")) survey.setCaptcha(true);
		    	
		    } else if (Accessibility.getElementsByTagName("SecuredAnonymous").getLength() > 0)
		    {
		    	survey.setSecurity("securedanonymous");
		    } else {
		    	survey.setSecurity("secured");
		    }
		    
		    if (AdministrationProperties.getElementsByTagName("AutoNumbering").getLength() > 0)
		    {
			    org.w3c.dom.Element AutoNumbering = (Element) AdministrationProperties.getElementsByTagName("AutoNumbering").item(0);
			    if (AutoNumbering.getElementsByTagName("SectionNumbering").getLength() > 0)
			    {
			    	org.w3c.dom.Element SectionNumbering = (Element) AutoNumbering.getElementsByTagName("SectionNumbering").item(0);
			    	if (SectionNumbering.getTextContent().equalsIgnoreCase("1")){
			    		survey.setSectionNumbering(1);
			    	} else if (SectionNumbering.getTextContent().equals("a")){
			    		survey.setSectionNumbering(2);
			    	} else if (SectionNumbering.getTextContent().equals("A")){
			    		survey.setSectionNumbering(3);
			    	}
			    }
			    
			    if (AutoNumbering.getElementsByTagName("QuestionNumbering").getLength() > 0)
			    {
			    	org.w3c.dom.Element QuestionNumbering = (Element) AutoNumbering.getElementsByTagName("QuestionNumbering").item(0);
			    	if (QuestionNumbering.getTextContent().equalsIgnoreCase("1")){
			    		survey.setQuestionNumbering(1);
			    	} else if (QuestionNumbering.getTextContent().equals("a")){
			    		survey.setQuestionNumbering(2);
			    	} else if (QuestionNumbering.getTextContent().equals("A")){
			    		survey.setQuestionNumbering(3);
			    	}
			    }
		    }
		    
		    if (SurveyProperties.getElementsByTagName("StartDate").getLength() > 0)
		    {
		    	String start = SurveyProperties.getElementsByTagName("StartDate").item(0).getTextContent();
		    	if (start != null && start.length() > 0)
		    	{
		    		Date startDate = Tools.parseDateString(start, "yyyy-MM-dd");
		    		survey.setStart(startDate);
		    	}
		    }
		    if (SurveyProperties.getElementsByTagName("EndDate").getLength() > 0)
		    {
		    	String end = SurveyProperties.getElementsByTagName("EndDate").item(0).getTextContent();
		    	if (end != null && end.length() > 0)
		    	{
		    		Date endDate = Tools.parseDateString(end, "yyyy-MM-dd");
		    		Calendar cal = Calendar.getInstance();
		    		cal.setTime(endDate);
		    		cal.set(Calendar.HOUR_OF_DAY, 23);
		    		cal.set(Calendar.MINUTE, 59);
		    		cal.set(Calendar.SECOND, 59);
		    		endDate = cal.getTime();
		    		
		    		survey.setEnd(endDate);
		    	}
		    }
		    
		    org.w3c.dom.Element Private = (Element) SurveyProperties.getElementsByTagName("Private").item(0);
		    if (Private.getTextContent().equalsIgnoreCase("false")) survey.setListForm(true);
		    
		    org.w3c.dom.Element AllowMultipage = (Element) SurveyProperties.getElementsByTagName("AllowMultiPage").item(0);
		    if (AllowMultipage.getElementsByTagName("Yes").getLength() > 0)
		    {
		    	survey.setMultiPaging(true);
		    }
		   
		    org.w3c.dom.Element UsefulLinks = (Element) SurveyProperties.getElementsByTagName("UsefulLinks").item(0);
		    if (UsefulLinks != null && UsefulLinks.getElementsByTagName("InformationReference").getLength() > 0)
		    {
		    	NodeList links = UsefulLinks.getElementsByTagName("InformationReference");
				for (int u = 0; u < links.getLength(); u++)
				{
					org.w3c.dom.Element InformationReference = (Element) links.item(u);
		    	
			    	if (InformationReference.getElementsByTagName("InformationLang").getLength() > 0)
					{
			    		org.w3c.dom.Element InformationLang = (Element) InformationReference.getElementsByTagName("InformationLang").item(0);
			    		String label = InformationLang.getElementsByTagName("Label").item(0) != null ? InformationLang.getElementsByTagName("Label").item(0).getTextContent() : "url";
			    		if (InformationLang.getElementsByTagName("Location").item(0) != null)
			    		{
				    		String location = InformationLang.getElementsByTagName("Location").item(0).getTextContent();
				    		if (!location.toLowerCase().startsWith("http"))
			    			{
				    			location = "http://" + location;
			    			}
				    		survey.getUsefulLinks().put(label, location);
			    		}
			    		//TODO: other languages
					}		    	
				}
		    }
		    
		    org.w3c.dom.Element BackgroundDocs = (Element) SurveyProperties.getElementsByTagName("BackgroundDocs").item(0);
		    if (BackgroundDocs != null && BackgroundDocs.getElementsByTagName("InformationReference").getLength() > 0)
		    {
		    	NodeList links = BackgroundDocs.getElementsByTagName("InformationReference");
				for (int u = 0; u < links.getLength(); u++)
				{
					org.w3c.dom.Element InformationReference = (Element) links.item(u);
		    	
			    	if (InformationReference.getElementsByTagName("InformationLang").getLength() > 0)
					{
			    		org.w3c.dom.Element InformationLang = (Element) InformationReference.getElementsByTagName("InformationLang").item(0);
			    		String label = InformationLang.getElementsByTagName("Label").item(0) != null ? InformationLang.getElementsByTagName("Label").item(0).getTextContent() : "url";
			    		
			    		if (InformationLang.getElementsByTagName("Location").item(0) != null)
			    		{
				    		String location = InformationLang.getElementsByTagName("Location").item(0).getTextContent();
				    		if (!location.toLowerCase().startsWith("http"))
			    			{
				    			location = "http://" + location;
			    			}
				    		//in IPM also background documents where external links. In EUSurvey background documents must be internal links
				    		survey.getUsefulLinks().put(label, location);
			    		}
			    		//TODO: other languages
					}
				}
		    }
		    
		    org.w3c.dom.Element ConfirmationPage = (Element) SurveyProperties.getElementsByTagName("ConfirmationPage").item(0);
		    if (ConfirmationPage != null && ConfirmationPage.getElementsByTagName("InternalReference").getLength() > 0)
		    {
		    	org.w3c.dom.Element InternalReference = (Element) ConfirmationPage.getElementsByTagName("InternalReference").item(0);
		    	org.w3c.dom.Element Text = (Element) InternalReference.getElementsByTagName("Text").item(0);
		    	org.w3c.dom.Element Label = (Element) Text.getElementsByTagName("Label").item(0);
		    	survey.setConfirmationPage(Label.getTextContent());
		    	//TODO: other languages
		    	//TODO: external references
		    }
		    
		    org.w3c.dom.Element EscapePage = (Element) SurveyProperties.getElementsByTagName("EscapePage").item(0);
		    if (EscapePage != null && EscapePage.getElementsByTagName("InternalReference").getLength() > 0)
		    {
		    	org.w3c.dom.Element InternalReference = (Element) EscapePage.getElementsByTagName("InternalReference").item(0);
		    	org.w3c.dom.Element Text = (Element) InternalReference.getElementsByTagName("Text").item(0);
		    	org.w3c.dom.Element Label = (Element) Text.getElementsByTagName("Label").item(0);
		    	survey.setEscapePage(Label.getTextContent());
		    	//TODO: other languages
		    	//TODO: external references
		    }
		    
		    org.w3c.dom.Element Headers = (Element) SurveyElement.getElementsByTagName("Headers").item(0);
		    org.w3c.dom.Element AdministrationLabels = (Element) Headers.getElementsByTagName("AdministrationLabels").item(0);
		    survey.setTitle(AdministrationLabels.getElementsByTagName("Name").item(0).getTextContent());
		    
		    org.w3c.dom.Element SurveyLabels = (Element) Headers.getElementsByTagName("SurveyLabels").item(0);
		    String introduction = SurveyLabels.getElementsByTagName("Introduction").item(0) != null ? SurveyLabels.getElementsByTagName("Introduction").item(0).getTextContent() : null;
		    
		    counter = 0;
		    
		    if (introduction != null && introduction.trim().length() > 0)
		    {
		    	if (survey.getMultiPaging())
		    	{
		    		Section section = new Section();
					section.setShortname("Introduction");
					section.setTitle("Introduction");
					section.setPosition(counter++);
					survey.getElements().add(section);
		    	}		    	
		    	
		    	Text text = new Text();
				text.setOptional(true);
				text.setShortname("introduction");
				text.setPosition(counter++);
				text.setTitle(introduction);
				survey.getElements().add(text);
		    }
		    
		    NodeList sections = SurveyElement.getChildNodes(); //SurveyElement.getElementsByTagName("Section");
		  
			for (int s = 0; s < sections.getLength(); s++)
			{
				Node sectionElement = sections.item(s);
				if (sectionElement.getNodeName().equalsIgnoreCase("Section"))
				{
					Section section = parseSection((Element) sectionElement, result, survey, servletContext);
					if (!section.getShortname().equalsIgnoreCase("METAINFOSECTION"))
					{
						survey.getElements().add(section);
					}
				}
			}
		    
			result.setSurvey(survey);			

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
	}

	private static Section parseSection(Element sectionElement, ImportResult result, Survey survey, ServletContext servletContext)
	{
		Section section = new Section();
		section.setShortname(sectionElement.getElementsByTagName("ShortName").item(0).getTextContent());
		if (section.getShortname() != null)
		{
			section.setShortname(section.getShortname().replace("&", "and"));
		}
		section.setTabTitle(section.getShortname());
		boolean dependentSection = (sectionElement.hasAttribute("IsPointedBy") && sectionElement.getAttribute("IsPointedBy").length() > 0);
		
		if (!section.getShortname().equalsIgnoreCase("METAINFOSECTION"))
		{
			section.setId(idcounter);
			result.getOriginalIdsToNewIds().put(sectionElement.getAttribute("id"), idcounter++);
			
			section.setPosition(counter++);
			if (sectionElement.getElementsByTagName("Label").getLength() > 0)
			{
				section.setTitle(sectionElement.getElementsByTagName("Label").item(0).getTextContent());
			}				
			
			String description = getChildElementValue(sectionElement, "Description", servletContext);
			if (description != null)
			{
				Text text = new Text();
				text.setId(idcounter);
				result.getOriginalIdsToNewIds().put(sectionElement.getAttribute("id")+"desc", idcounter++);
				text.setOptional(true);
				text.setShortname(sectionElement.getAttribute("id") + "desc");
				text.setPosition(counter++);
				text.setTitle(description);
				survey.getElements().add(text);
				
				if (!result.getAdditionalElements().containsKey(section.getId()))
				{
					result.getAdditionalElements().put(section.getId(), new ArrayList<>());
				}						
				result.getAdditionalElements().get(section.getId()).add(text.getId());	
			}	
			
			NodeList questions = sectionElement.getChildNodes(); 
			
			for (int q = 0; q < questions.getLength(); q++)
			{
				Node questionElement = questions.item(q);
				Question question = null;
				if (questionElement.getNodeName().equalsIgnoreCase("Question"))
				{
					question = parseQuestion((Element)questionElement, false, result);
					question.setPosition(counter++);
					survey.getElements().add(question);
				
					if (((Element)questionElement).getAttribute("IsUploadAllowed").equalsIgnoreCase("true"))
					{
						Upload uploadQuestion = new Upload();
						uploadQuestion.setShortname(question.getShortname()+"upload");
						uploadQuestion.setTitle("");
						uploadQuestion.setId(idcounter++);
						uploadQuestion.setPosition(counter++);
						uploadQuestion.setOptional(question.getOptional());		
						
						if (!result.getAdditionalElements().containsKey(question.getId()))
						{
							result.getAdditionalElements().put(question.getId(), new ArrayList<>());
						}						
						result.getAdditionalElements().get(question.getId()).add(uploadQuestion.getId());	
					
						survey.getElements().add(uploadQuestion);
					}
					
					if (dependentSection && !((Element)questionElement).hasAttribute("IsPointedBy"))
					{
						if (!result.getAdditionalElements().containsKey(section.getId()))
						{
							result.getAdditionalElements().put(section.getId(), new ArrayList<>());
						}						
						result.getAdditionalElements().get(section.getId()).add(question.getId());	
					}
					
				} else if (questionElement.getNodeName().equalsIgnoreCase("Matrix")) {
					question = parseMatrix((Element)questionElement, result, servletContext);
					question.setPosition(counter++);
					survey.getElements().add(question);
					
					if (dependentSection)
					{
						if (!result.getAdditionalElements().containsKey(section.getId()))
						{
							result.getAdditionalElements().put(section.getId(), new ArrayList<>());
						}						
						result.getAdditionalElements().get(section.getId()).add(question.getId());									
					}
				} else if (questionElement.getNodeName().equalsIgnoreCase("Section")) {
					Section subsection = parseSection((Element)questionElement, result, survey, servletContext);
					subsection.setLevel(section.getLevel() + 1);
					survey.getElements().add(subsection);
					
					if (dependentSection)
					{
						if (!result.getAdditionalElements().containsKey(section.getId()))
						{
							result.getAdditionalElements().put(section.getId(), new ArrayList<>());
						}						
						result.getAdditionalElements().get(section.getId()).add(subsection.getId());									
					}
				}
				
			}
		}
		return section;
	}
	
	private static Question parseMatrix(Element matrixElement, ImportResult result, ServletContext servletContext) {
		Matrix matrix = new Matrix();
		matrix.setOptional(true);
		matrix.setId(idcounter);
		result.getOriginalIdsToNewIds().put(matrixElement.getAttribute("id"), idcounter++);
		int counter = 1;
		matrix.getChildElements().add(new EmptyElement());
		
		org.w3c.dom.Element QuestionsTemplate = (Element) matrixElement.getElementsByTagName("QuestionsTemplate").item(0);
		Question template = parseQuestion(QuestionsTemplate, true, result);
		if (template instanceof ChoiceQuestion)
		{
			for (PossibleAnswer answer: ((ChoiceQuestion)template).getPossibleAnswers())
			{
				Text text = new Text();
				text.setId(answer.getId());
				text.setOptional(true);
				text.setShortname(answer.getShortname());
				text.setPosition(counter++);
				text.setTitle(answer.getTitle());
				matrix.getChildElements().add(text);
			}
		}
		
		if (QuestionsTemplate.getElementsByTagName("RadioButton").getLength() > 0)
		{
			org.w3c.dom.Element RadioButton = (Element) QuestionsTemplate.getElementsByTagName("RadioButton").item(0);
			
			if (RadioButton.getAttribute("Gradation").length() > 0 && RadioButton.getAttribute("Gradation").equalsIgnoreCase("true"))
			{
				matrix.setIsInterdependent(true);
			}
		} else if (QuestionsTemplate.getElementsByTagName("SelectBox").getLength() > 0)
		{
			org.w3c.dom.Element SelectBox = (Element) QuestionsTemplate.getElementsByTagName("SelectBox").item(0);
			
			if (SelectBox.getAttribute("Gradation").length() > 0 && SelectBox.getAttribute("Gradation").equalsIgnoreCase("true"))
			{
				matrix.setIsInterdependent(true);
			}
		}
		
		NodeList questions = matrixElement.getElementsByTagName("Question");		
		
		for (int q = 0; q < questions.getLength(); q++)
		{
			Element questionElement = (Element) questions.item(q);
			Text text = new Text();
			text.setId(idcounter);
			result.getOriginalIdsToNewIds().put(questionElement.getAttribute("id"), idcounter++);
			text.setShortname(questionElement.getElementsByTagName("ShortName").item(0).getTextContent());
			if (text.getShortname() != null)
			{
				text.setShortname(text.getShortname().replace("&", "and"));
			}
			text.setPosition(counter++);
			text.setTitle(questionElement.getElementsByTagName("Label").item(0).getTextContent());
			
			if (questionElement.getElementsByTagName("Description").getLength() > 0)
			{
				String description = questionElement.getElementsByTagName("Description").item(0).getTextContent();
				if (description.length() > 0)
				{
					text.setTitle(text.getTitle() + "<br />" + description);
				}
			}
			
			text.setOptional(questionElement.getAttribute("IsCompulsory") == null || !questionElement.getAttribute("IsCompulsory").equalsIgnoreCase("true"));
			matrix.getChildElements().add(text);
			
			//check for dependencies inside this matrix question
			NodeList answers = questionElement.getElementsByTagName("PossibleAnswer");
			for (int a = 0; a < answers.getLength(); a++)
			{
				Element answerElement = (Element) answers.item(a);
				String label = answerElement.getElementsByTagName("Label").item(0).getTextContent();
				
				if (label != null && label.trim().length() > 0)
				{
					//the answer has already been added during parsing of the matrix' question template
					String answerid = questionElement.getAttribute("id") + "#" + a + "#" + matrixElement.getAttribute("id");
									
					if (answerElement.getElementsByTagName("PointsTo").getLength() > 0)
					{
						NodeList dependencies = answerElement.getElementsByTagName("PointsTo");		
						
						for (int d = 0; d < dependencies.getLength(); d++)
						{
							Element dependencyElement = (Element) dependencies.item(d);
							String depid = dependencyElement.getTextContent();
							if (depid.length() > 0)
							{
								if (!result.getOriginalMatrixDependencies().containsKey(answerid))
								{
									result.getOriginalMatrixDependencies().put(answerid, new ArrayList<>());
								}
								result.getOriginalMatrixDependencies().get(answerid).add(depid);
							}
							
						}
					}
				}
			}
		}

		matrix.setRows(questions.getLength()+1);
		matrix.setColumns(((ChoiceQuestion)template).getPossibleAnswers().size()+1);
		matrix.setIsSingleChoice(template instanceof SingleChoiceQuestion);
		matrix.setTitle(matrixElement.getElementsByTagName("Label").item(0).getTextContent());
		matrix.setShortname(matrixElement.getElementsByTagName("ShortName").item(0).getTextContent());
		if (matrix.getShortname() != null)
		{
			matrix.setShortname(matrix.getShortname().replace("&", "and"));
		}
		
		String description = getChildElementValue(matrixElement, "Description", servletContext);
		if (description != null)
		{
			matrix.setTitle(matrix.getTitle() + "<br />" + description);
		}
		
		return matrix;
	}
	
	private static String getChildElementValue(Element parent, String name, ServletContext servletContext)
	{		
		NodeList children = parent.getChildNodes();		
		
		for (int q = 0; q < children.getLength(); q++)
		{
			Node child = children.item(q);
			if (child.getNodeName().equals(name))
			{
				return child.getTextContent();
			}
		}
		
		return null;
	}

	private static Question parseQuestion(Element questionElement, boolean template, ImportResult result) {

		//TODO: dependencies
		
		Question question = null;
		if (questionElement.getElementsByTagName("RadioButton").getLength() > 0)
		{
			question = new SingleChoiceQuestion();
			((SingleChoiceQuestion)question).setUseRadioButtons(true);
			org.w3c.dom.Element RadioButton = (Element) questionElement.getElementsByTagName("RadioButton").item(0);
			
			if (RadioButton.getAttribute("NberOfColumns").length() > 0)
			{
				int columns = Integer.parseInt(RadioButton.getAttribute("NberOfColumns"));
				((SingleChoiceQuestion)question).setNumColumns(columns);
			}
				
			parsePossibleAnswers(question, RadioButton, result);
			
		} else if (questionElement.getElementsByTagName("SelectBox").getLength() > 0)
		{
			question = new SingleChoiceQuestion();
			((SingleChoiceQuestion)question).setUseRadioButtons(false);
			
			org.w3c.dom.Element SelectBox = (Element) questionElement.getElementsByTagName("SelectBox").item(0);
			
			parsePossibleAnswers(question, SelectBox, result);
		} else if (questionElement.getElementsByTagName("CheckBox").getLength() > 0)
		{
			question = new MultipleChoiceQuestion();
			((MultipleChoiceQuestion)question).setUseCheckboxes(true);
			org.w3c.dom.Element CheckBox = (Element) questionElement.getElementsByTagName("CheckBox").item(0);
			
			if (CheckBox.getAttribute("NberOfColumns").length() > 0)
			{
				int columns = Integer.parseInt(CheckBox.getAttribute("NberOfColumns"));
				((MultipleChoiceQuestion)question).setNumColumns(columns);
			}
				
			parsePossibleAnswers(question, CheckBox, result);
			
			if (CheckBox.getAttribute("MinChoicesAllowed").length() > 0)
			{
				int min = Integer.parseInt(CheckBox.getAttribute("MinChoicesAllowed"));
				((MultipleChoiceQuestion)question).setMinChoices(min);
			}			
			if (CheckBox.getAttribute("MaxChoicesAllowed").length() > 0)
			{
				int max = Integer.parseInt(CheckBox.getAttribute("MaxChoicesAllowed"));
				((MultipleChoiceQuestion)question).setMaxChoices(max);
			}
			
		} else if (questionElement.getElementsByTagName("ListBox").getLength() > 0)
		{
			question = new MultipleChoiceQuestion();
			((MultipleChoiceQuestion)question).setUseCheckboxes(false);
			
			org.w3c.dom.Element ListBox = (Element) questionElement.getElementsByTagName("ListBox").item(0);
			
			parsePossibleAnswers(question, ListBox, result);

			if (ListBox.getAttribute("MinChoicesAllowed").length() > 0)
			{
				int min = Integer.parseInt(ListBox.getAttribute("MinChoicesAllowed"));
				((MultipleChoiceQuestion)question).setMinChoices(min);
			}			
			if (ListBox.getAttribute("MaxChoicesAllowed").length() > 0)
			{
				int max = Integer.parseInt(ListBox.getAttribute("MaxChoicesAllowed"));
				((MultipleChoiceQuestion)question).setMaxChoices(max);
			}
		} else if (questionElement.getElementsByTagName("ShortText").getLength() > 0)
		{
			question = new FreeTextQuestion();
			org.w3c.dom.Element ShortText = (Element) questionElement.getElementsByTagName("ShortText").item(0);
			if (ShortText.getAttribute("NberOfLinesForInput").length() > 0)
			{
				int lines = Integer.parseInt(ShortText.getAttribute("NberOfLinesForInput"));
				((FreeTextQuestion)question).setNumRows(lines);
			}
			if (ShortText.getAttribute("MinLength").length() > 0)
			{
				int min = Integer.parseInt(ShortText.getAttribute("MinLength"));
				((FreeTextQuestion)question).setMinCharacters(min);
			}			
			if (ShortText.getAttribute("MaxLength").length() > 0)
			{
				int max = Integer.parseInt(ShortText.getAttribute("MaxLength"));
				((FreeTextQuestion)question).setMaxCharacters(max);
			}
		} else if (questionElement.getElementsByTagName("FreeText").getLength() > 0)
		{
			question = new FreeTextQuestion();
			org.w3c.dom.Element FreeText = (Element) questionElement.getElementsByTagName("FreeText").item(0);
			if (FreeText.getAttribute("NberOfLinesForInput").length() > 0)
			{
				try {
					int lines = Integer.parseInt(FreeText.getAttribute("NberOfLinesForInput"));
					((FreeTextQuestion)question).setNumRows(lines);
				} catch (NumberFormatException nfe)
				{
					logger.error(nfe.getLocalizedMessage(), nfe);
				}
			}
			if (FreeText.getAttribute("MinLength").length() > 0)
			{ 
				try {
					int min = Integer.parseInt(FreeText.getAttribute("MinLength"));
					((FreeTextQuestion)question).setMinCharacters(min);
				} catch (NumberFormatException nfe)
				{
					logger.error(nfe.getLocalizedMessage(), nfe);
				}
			}			
			if (FreeText.getAttribute("MaxLength").length() > 0)
			{
				try {
					int max = Integer.parseInt(FreeText.getAttribute("MaxLength"));
					((FreeTextQuestion)question).setMaxCharacters(max);
				} catch (NumberFormatException nfe)
				{
					logger.error(nfe.getLocalizedMessage(), nfe);
				}
			}
		} else if (questionElement.getElementsByTagName("Date").getLength() > 0)
		{
			question = new DateQuestion();
			org.w3c.dom.Element DateElement = (Element) questionElement.getElementsByTagName("Date").item(0);
			if (DateElement.getAttribute("StartPeriod").length() > 0)
			{
				Date min = Tools.parseDateString(DateElement.getAttribute("StartPeriod"), ConversionTools.IPMDateFormat);
				((DateQuestion)question).setMin(min);
			}			
			if (DateElement.getAttribute("EndPeriod").length() > 0)
			{
				Date max = Tools.parseDateString(DateElement.getAttribute("EndPeriod"), ConversionTools.IPMDateFormat);
				((DateQuestion)question).setMax(max);
			}
		} else if (questionElement.getElementsByTagName("Integer").getLength() > 0)
		{
			question = new NumberQuestion();
			((NumberQuestion)question).setDecimalPlaces(0);
			org.w3c.dom.Element IntegerElement = (Element) questionElement.getElementsByTagName("Integer").item(0);
			if (IntegerElement.getAttribute("MinValue").length() > 0)
			{
				try {
					int min = Integer.parseInt(IntegerElement.getAttribute("MinValue"));
					((NumberQuestion)question).setMin((double) min);
				} catch (NumberFormatException nfe)
				{
					logger.error(nfe.getLocalizedMessage(), nfe);
				}
			}			
			if (IntegerElement.getAttribute("MaxValue").length() > 0)
			{
				try {
					int max = Integer.parseInt(IntegerElement.getAttribute("MaxValue"));
					((NumberQuestion)question).setMax((double)max);
				} catch (NumberFormatException nfe)
				{
					logger.error(nfe.getLocalizedMessage(), nfe);
				}
			}
		} else if (questionElement.getElementsByTagName("Float").getLength() > 0)
		{
			question = new NumberQuestion();
			((NumberQuestion)question).setDecimalPlaces(2);
			org.w3c.dom.Element FloatElement = (Element) questionElement.getElementsByTagName("Float").item(0);
			if (FloatElement.getAttribute("MinValue").length() > 0)
			{
				try {
					double min = Double.parseDouble(FloatElement.getAttribute("MinValue"));
					((NumberQuestion)question).setMin(min);
				} catch (NumberFormatException nfe)
				{
					logger.error(nfe.getLocalizedMessage(), nfe);
				}
			}			
			if (FloatElement.getAttribute("MaxValue").length() > 0)
			{
				try {
					double max = Double.parseDouble(FloatElement.getAttribute("MaxValue"));
					((NumberQuestion)question).setMax(max);
				} catch (NumberFormatException nfe)
				{
					logger.error(nfe.getLocalizedMessage(), nfe);
				}
			}
			if (FloatElement.getAttribute("NberOfDecimal").length() > 0)
			{
				try {
					int nbr = Integer.parseInt(FloatElement.getAttribute("NberOfDecimal"));
					((NumberQuestion)question).setDecimalPlaces(nbr);
				} catch (NumberFormatException nfe)
				{
					logger.error(nfe.getLocalizedMessage(), nfe);
				}
			}
		} else if (questionElement.getElementsByTagName("Percentage").getLength() > 0)
		{
			question = new NumberQuestion();
			((NumberQuestion)question).setUnit("%");
			org.w3c.dom.Element PercentElement = (Element) questionElement.getElementsByTagName("Percentage").item(0);
			if (PercentElement.getAttribute("MinValue").length() > 0)
			{
				try {
					int min = Integer.parseInt(PercentElement.getAttribute("MinValue"));
					((NumberQuestion)question).setMin((double) min);
				} catch (NumberFormatException nfe)
				{
					logger.error(nfe.getLocalizedMessage(), nfe);
				}
			}			
			if (PercentElement.getAttribute("MaxValue").length() > 0)
			{
				try {
					int max = Integer.parseInt(PercentElement.getAttribute("MaxValue"));
					((NumberQuestion)question).setMax((double)max);
				} catch (NumberFormatException nfe)
				{
					logger.error(nfe.getLocalizedMessage(), nfe);
				}
			}
			if (PercentElement.getAttribute("NberOfDecimal").length() > 0)
			{
				try {
					int nbr = Integer.parseInt(PercentElement.getAttribute("NberOfDecimal"));
					((NumberQuestion)question).setDecimalPlaces(nbr);
				} catch (NumberFormatException nfe)
				{
					logger.error(nfe.getLocalizedMessage(), nfe);
				}
			}
		} else if (questionElement.getElementsByTagName("Password").getLength() > 0)
		{
			question = new FreeTextQuestion();
			((FreeTextQuestion)question).setIsPassword(true);
			//TODO: min/max
		}
		
		if (!template)
		{		
			question.setId(idcounter);
			result.getOriginalIdsToNewIds().put(questionElement.getAttribute("id"), idcounter++);
			question.setShortname(questionElement.getElementsByTagName("ShortName").item(0).getTextContent());
			
			if (question.getShortname() != null)
			{
				question.setShortname(question.getShortname().replace("&", "and"));
			}
			
			question.setTitle(questionElement.getElementsByTagName("Label").item(0).getTextContent());
			
			if (questionElement.getElementsByTagName("Description").getLength() > 0)
			{
				question.setHelp(questionElement.getElementsByTagName("Description").item(0).getTextContent());
			}
			
			if (questionElement.getAttribute("IsCompulsory").equalsIgnoreCase("false"))
			{
				question.setOptional(true);
			}
			
			if (questionElement.getAttribute("IsAttribute").equalsIgnoreCase("true"))
			{
				question.setIsAttribute(true);
			}
		}
		
		return question;
	}
	
	private static void parsePossibleAnswers(Question question, org.w3c.dom.Element questionElement, ImportResult result)
	{
		NodeList answers = questionElement.getElementsByTagName("PossibleAnswer");
	    int counter = 0;
		for (int a = 0; a < answers.getLength(); a++)
		{
			Element answerElement = (Element) answers.item(a);
			String label = answerElement.getElementsByTagName("Label").item(0).getTextContent();
			
			if (label != null && label.trim().length() > 0)
			{
				PossibleAnswer answer = new PossibleAnswer();
				answer.setPosition(counter++);
				answer.setId(idcounter);
				result.getOriginalIdsToNewIds().put(answerElement.getAttribute("id"), idcounter++);
				answer.setTitle(answerElement.getElementsByTagName("Label").item(0).getTextContent());
				if (question instanceof SingleChoiceQuestion)
				{
					((SingleChoiceQuestion)question).getPossibleAnswers().add(answer);
				} else {
					((MultipleChoiceQuestion)question).getPossibleAnswers().add(answer);
				}
				
				if (answerElement.getElementsByTagName("PointsTo").getLength() > 0)
				{
					NodeList dependencies = answerElement.getElementsByTagName("PointsTo");		
					
					for (int d = 0; d < dependencies.getLength(); d++)
					{
						Element dependencyElement = (Element) dependencies.item(d);
						String depid = dependencyElement.getTextContent();
						if (depid.length() > 0)
						{
							if (!result.getOriginalDependencies().containsKey(answer.getId()))
							{
								result.getOriginalDependencies().put(answer.getId(), new ArrayList<>());
							}
							result.getOriginalDependencies().get(answer.getId()).add(depid);
						}
					}
				}
			}
			
		}
	}
	
	private static void importIPMTranslationXML(InputStream f, ImportResult result, ServletContext servletContext) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			docFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			docFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			docFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			docFactory.setXIncludeAware(false);
			docFactory.setExpandEntityReferences(false);
			
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();	 
			Document doc = docBuilder.parse(f);			
			org.w3c.dom.Element TableTranslations = doc.getDocumentElement();
			
			NodeList translationElements = TableTranslations.getElementsByTagName("Translation");		
			int surveyId = result.getSurvey().getId();
			
			result.setTranslations(new ArrayList<>());
			
			for (int t = 0; t < translationElements.getLength(); t++)
			{
				Translations translations = new Translations();
				translations.setSurveyId(surveyId);
				translations.setSurveyUid(result.getSurvey().getUniqueId());
				
				Element translationElement = (Element) translationElements.item(t);
				
				org.w3c.dom.Element SurveyElement = (Element) translationElement.getElementsByTagName("Survey").item(0);
				String language = SurveyElement.getElementsByTagName("Lang").item(0).getTextContent().toUpperCase();
				
				for (Language lang: languages)
				{
					if (lang.getCode().equalsIgnoreCase(language))
					{
						translations.setLanguage(lang);	
						break;
					}
				}
				
				org.w3c.dom.Element ConfirmationPageElement = (Element) translationElement.getElementsByTagName("ConfirmationPage").item(0);
				if (ConfirmationPageElement.getTextContent().length() > 0)
				{
					translations.getTranslations().add(new Translation(Survey.CONFIRMATIONPAGE, ConfirmationPageElement.getTextContent(), language, surveyId, translations));
				}
				
				org.w3c.dom.Element EscapePageElement = (Element) translationElement.getElementsByTagName("EscapePage").item(0);
				if (EscapePageElement.getTextContent().length() > 0)
				{
					translations.getTranslations().add(new Translation(Survey.ESCAPEPAGE, EscapePageElement.getTextContent(), language, surveyId, translations));
				}
			
				org.w3c.dom.Element HeadersElement = (Element) SurveyElement.getElementsByTagName("Headers").item(0);
				org.w3c.dom.Element AdministrationLabelsElement = (Element) HeadersElement.getElementsByTagName("AdministrationLabels").item(0);
				org.w3c.dom.Element NameElement = (Element) AdministrationLabelsElement.getElementsByTagName("Name").item(0);
				translations.getTranslations().add(new Translation(Survey.TITLE, NameElement.getTextContent(), language, surveyId, translations));
				
				org.w3c.dom.Element SurveyLabelsElement = (Element) HeadersElement.getElementsByTagName("SurveyLabels").item(0);
				org.w3c.dom.Element IntroductionElement = (Element) SurveyLabelsElement.getElementsByTagName("Introduction").item(0);
				if (IntroductionElement != null && IntroductionElement.getTextContent() != null && IntroductionElement.getTextContent().length() > 0)
				{
					translations.getTranslations().add(new Translation(Survey.IPMINTRODUCTION, IntroductionElement.getTextContent(), language, surveyId, translations));
				}
				
				NodeList sectionElements = SurveyElement.getElementsByTagName("Section");		
				
				for (int s = 0; s < sectionElements.getLength(); s++)
				{
					Element SectionElement = (Element) sectionElements.item(s);
					String id = SectionElement.getAttribute("id");
					String label = SectionElement.getElementsByTagName("Label").item(0).getTextContent();
					
					String shortName = SectionElement.getElementsByTagName("ShortName").item(0).getTextContent();
					if (shortName != null && shortName.length() > 0)
					{
						shortName = shortName.replace("&", "and");
												
						translations.getTranslations().add(new Translation(id + Section.TABTITLE, shortName, language, surveyId, translations));
					}
					
					String description = getChildElementValue(SectionElement, "Description", servletContext);
									
					translations.getTranslations().add(new Translation(id, label, language, surveyId, translations));
					if (description != null && description.length() > 0)
					{
						translations.getTranslations().add(new Translation(id + "desc", description, language, surveyId, translations));
					}
					
					NodeList matrixElements = SectionElement.getElementsByTagName("Matrix");		
					for (int m = 0; m < matrixElements.getLength(); m++)
					{
						Element MatrixElement = (Element) matrixElements.item(m);
						id = MatrixElement.getAttribute("id");
						label = MatrixElement.getElementsByTagName("Label").item(0).getTextContent();
						description = getChildElementValue(MatrixElement, "Description", servletContext); 
						
						if (description != null && description.length() > 0)
						{
							label = label + "<br />" + description;
						}
						
						translations.getTranslations().add(new Translation(id, label, language, surveyId, translations));
						
						org.w3c.dom.Element QuestionTemplateElement = (Element) MatrixElement.getElementsByTagName("QuestionTemplate").item(0);
						org.w3c.dom.Element child = (Element) QuestionTemplateElement.getChildNodes().item(0);
						
						NodeList answerElements = child.getElementsByTagName("PossibleAnswer");		
						for (int a = 0; a < answerElements.getLength(); a++)
						{
							Element AnswerElement = (Element) answerElements.item(a);
							id = AnswerElement.getAttribute("id");
							label = AnswerElement.getElementsByTagName("Label").item(0).getTextContent();
							translations.getTranslations().add(new Translation(id, label, language, surveyId, translations));
						}					
						
						NodeList questionElements = MatrixElement.getElementsByTagName("Question");		
						for (int q = 0; q < questionElements.getLength(); q++)
						{
							Element QuestionElement = (Element) questionElements.item(q);
							id = QuestionElement.getAttribute("id");
							label = QuestionElement.getElementsByTagName("Label").item(0).getTextContent();
							
							if (QuestionElement.getElementsByTagName("Description").getLength() > 0)
							{
								description = QuestionElement.getElementsByTagName("Description").item(0).getTextContent();
							} else {
								description = "";
							}
							
							if (description != null && description.length() > 0)
							{
								label = label + "<br />" + description;
							}
							
							translations.getTranslations().add(new Translation(id, label, language, surveyId, translations));
						}						
					}
					
					NodeList questionElements = SectionElement.getElementsByTagName("Question");		
					for (int q = 0; q < questionElements.getLength(); q++)
					{
						Element QuestionElement = (Element) questionElements.item(q);
						
						if (QuestionElement.getParentNode().getNodeName().equalsIgnoreCase("Section"))
						{
							id = QuestionElement.getAttribute("id");
							label = QuestionElement.getElementsByTagName("Label").item(0).getTextContent();
							
							if (QuestionElement.getElementsByTagName("Description").getLength() > 0)
							{						
								description = QuestionElement.getElementsByTagName("Description").item(0).getTextContent();
							} else {
								description = "";
							}
							
							translations.getTranslations().add(new Translation(id, label, language, surveyId, translations));
							if (description != null && description.length() > 0)
							{
								translations.getTranslations().add(new Translation(id + "help", description, language, surveyId, translations));
							}
							
							if (QuestionElement.getChildNodes().getLength() > 1)
							{
								NodeList answerElements = QuestionElement.getElementsByTagName("PossibleAnswer");		
								for (int a = 0; a < answerElements.getLength(); a++)
								{
									Element AnswerElement = (Element) answerElements.item(a);
									id = AnswerElement.getAttribute("id");
									label = AnswerElement.getElementsByTagName("Label").item(0).getTextContent();
									if (label != null && label.trim().length() > 0)
									{
										translations.getTranslations().add(new Translation(id, label, language, surveyId, translations));
									}
								}		
							}
						}
					}
				}
				
				translations.setActive(true);
				
				result.getTranslations().add(translations);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
	}

	
}
