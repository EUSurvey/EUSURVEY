package com.ec.survey.service;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.Export;
import com.ec.survey.model.Export.ExportFormat;
import com.ec.survey.model.Export.ExportState;
import com.ec.survey.model.Export.ExportType;
import com.ec.survey.model.Form;
import com.ec.survey.model.Setting;
import com.ec.survey.model.WebserviceTask;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.ChoiceQuestion;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.Text;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.FileUtils;
import com.ec.survey.tools.activity.ActivityRegistry;
import com.ec.survey.tools.export.*;

import org.hibernate.Hibernate;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.*;

@Service("exportService")
@Configurable
public class ExportService extends BasicService {
	
	private @Value("${export.deleteexportstimeout}") String deleteexportstimeout;
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${sender}") String sender;
	
	@Transactional(readOnly = false)
	public void prepareExport(Form form, Export export) {
		Session session = sessionFactory.getCurrentSession();
		if (export.getResultFilter() != null)
		{
			session.saveOrUpdate(export.getResultFilter());
			Hibernate.initialize(export.getResultFilter().getExportedQuestions());
			Hibernate.initialize(export.getResultFilter().getLanguages());
			Hibernate.initialize(export.getResultFilter().getFilterValues());
			Hibernate.initialize(export.getResultFilter().getVisibleQuestions());
			Hibernate.initialize(export.getResultFilter().getVisibleExplanations());
			Hibernate.initialize(export.getResultFilter().getExportedExplanations());
			Hibernate.initialize(export.getResultFilter().getVisibleDiscussions());
			Hibernate.initialize(export.getResultFilter().getExportedDiscussions());
		}
		session.saveOrUpdate(export);
		session.flush();		
		if (form != null && form.getSurvey() != null)
		{
			Hibernate.initialize(form.getSurvey().getElementsRecursive(true));
		}
		
		if (export.getActivityFilter() != null)
		{
			Hibernate.initialize(export.getActivityFilter().getExportedColumns());
			Hibernate.initialize(export.getActivityFilter().getVisibleColumns());
		}		
	}
	
	@Transactional
	public boolean startExport(Form form, Export export, boolean immediate, MessageSource resources, Locale locale, String uid, String exportFilePath, boolean skipcheckworkerserver) {	
		
		try {
			
			User user = administrationService.getUser(export.getUserId());
			logger.info("Starting export check settings skipcheckworkerserver " + skipcheckworkerserver +" useworkerserver " + useworkerserver +" isworkerserver " + isworkerserver);
			
			if (!immediate && !skipcheckworkerserver && useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("false"))
			{
				logger.info("calling worker server for export " + export.getId());
				
				URL workerurl = new URL(workerserverurl + "worker/start/" + export.getId() + Constants.PATH_DELIMITER + uid);
				
				try {				
					URLConnection wc = workerurl.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(wc.getInputStream()));
					String inputLine;
					StringBuilder result = new StringBuilder();
					while ((inputLine = in.readLine()) != null) 
						result.append(inputLine);
					in.close();
					
					if (result.toString().equals("OK"))
					{
						return true;
					} else {
						logger.error("calling worker server for export " + export.getId() + " returned" + result);
						return false;
					}
				} catch (ConnectException e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
			
			logger.info("Starting export " + export.getName());
			
			if (exportFilePath == null) exportFilePath = getExportFilePath(export, uid);
			ExportCreator exportCreator = null;
			switch (export.getFormat()) {
			case doc: 
				exportCreator = (DocExportCreator) context.getBean("docExportCreator"); break;
			case xls: exportCreator = (XlsExportCreator) context.getBean("xlsExportCreator"); break;
			case odt: 
			case ods: exportCreator = (OdfExportCreator) context.getBean("odfExportCreator"); break;
			case csv: exportCreator = (CsvExportCreator) context.getBean("csvExportCreator"); break;
			case xml: exportCreator = (XmlExportCreator) context.getBean("xmlExportCreator"); break;
			case pdf: exportCreator = (PdfExportCreator) context.getBean("pdfExportCreator"); break;
			case zip: exportCreator = (ZipExportCreator) context.getBean("zipExportCreator"); break;
			case eus: exportCreator = (EusExportCreator) context.getBean("eusExportCreator"); break;
			default: throw new MessageException("Export format not supported");
			}
			
			if (user == null)
			{
				exportCreator.init(0, form, export, exportFilePath, resources, locale, uid, serverPrefix);
			} else {
				exportCreator.init(user.getId(), form, export, exportFilePath, resources, locale, uid, serverPrefix);
			}
			
			switch (export.getType())
			{
				case Statistics:
				case PDFReport:
					activityService.log(ActivityRegistry.ID_STATISTICS_EXPORT, null, export.getId() != null ? export.getId().toString() : "", user != null ? user.getId() : 0, export.getSurvey() != null ? export.getSurvey().getUniqueId() : "");
					break;
				case Content:
					activityService.log(ActivityRegistry.ID_CONTENT_EXPORT, null, export.getId() != null ? export.getId().toString() : "", user != null ? user.getId() : 0, export.getSurvey() != null ? export.getSurvey().getUniqueId() : "");
					break;
				case Activity:
					activityService.log(ActivityRegistry.ID_ACTIVITY_EXPORT, null, export.getId() != null ? export.getId().toString() : "", user != null ? user.getId() : 0, export.getSurvey() != null ? export.getSurvey().getUniqueId() : "");
					break;
			default:
				break;					
			}
			
			if (immediate)
			{
				exportCreator.runSync();
				
				if (export.getEmail() != null)
				{
					export.setValid(true);
					export.setState(ExportState.Finished);
					this.update(export);
					
					Calendar end = Calendar.getInstance();
					end.setTime( new Date());
					end.add(Calendar.MINUTE, 5);
					
					Export finishedExport = exportService.getExport(export.getId(), false);
					if (!finishedExport.isFinished())
					{
						logger.error("export unfinished after execution; mail is not sent"); 
					}
				}
				
			} else {
				if (export.getFormat() == ExportFormat.pdf)
				{
					getPDFPool().execute(exportCreator);
				} else {				
					getPool().execute(exportCreator);
				}
			}	
			
			logger.info(String.format("Export %s started successfully", export.getName()));
			
			return true;
		} catch (Exception ex) {
			export.setState(ExportState.Failed);
			logger.error(ex.getLocalizedMessage(), ex);
			logger.error(String.format("Export %s could not be started.", export.getName()));
			return false;
		}
	}

	public String getExportFilePath(Export export, String uid) {
		
		if (uid != null && uid.length() > 0 && !uid.equals("null"))
		{
			if (export.getSurvey() == null)
			{
				java.io.File folder = fileService.getUsersFolder(export.getUserId());
				return String.format("%s/%s", folder.getPath(), uid);
			}
			
			java.io.File folder = fileService.getSurveyExportsFolder(export.getSurvey().getUniqueId());							
			return String.format("%s/%s", folder.getPath(), uid);
		}
		
		return getExportFilePath(export.getId(),export.getFormat());
	}
	
	private String getExportFilePath(int id, ExportFormat format)
	{
		Export export = get(id,  false);

		if (export.getSurvey() == null)
		{
			java.io.File folder = fileService.getUsersFolder(export.getUserId());
			return String.format("%s/Export%s.%s", folder.getPath(), id, format);
		}

		java.io.File folder = fileService.getSurveyExportsFolder(export.getSurvey().getUniqueId());
		return String.format("%s/Export%s.%s", folder.getPath(), id, format);
	}

	public String getReturnFileName(Export export) {

		ExportFormat format = export.getFormat();
		
		if (export.getType() != ExportType.AddressBook)
		{
			if (export.getZipped() != null && export.getZipped())
			{
				return FileUtils.cleanFilename(String.format("%s_Export_%s_%s.%s", export.getType(), export.getSurvey().getShortname(), export.getName(),"zip"));
			} 
			
			if (format.equals(ExportFormat.pdf) && export.getType().equals(ExportType.Content))
			{
				return FileUtils.cleanFilename(String.format("%s_Export_%s_%s.%s", export.getType(), export.getSurvey().getShortname(), export.getName(),"zip"));
			}
			
			return FileUtils.cleanFilename(String.format("%s_Export_%s_%s.%s", export.getType(), export.getSurvey().getShortname(), export.getName(),format));
		} else {
			if (export.getZipped() != null && export.getZipped())
			{
				return FileUtils.cleanFilename(String.format("%s_Export_%s.%s", export.getType(), export.getName(),"zip"));
			} 
			
			if (format.equals(ExportFormat.pdf) && export.getType().equals(ExportType.Content))
			{
				return FileUtils.cleanFilename(String.format("%s_Export_%s.%s", export.getType(), export.getName(),"zip"));
			}
			
			return FileUtils.cleanFilename(String.format("%s_Export_%s.%s", export.getType(), export.getName(),format));
		}
	}

	@Transactional(readOnly = true)
	public List<Export> getExports(int userId, String sortKey, boolean ascending, boolean determinestate, boolean onlynotnotified) {
		return getExports(userId,sortKey,ascending,-1,-1, false, determinestate, onlynotnotified, false);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Export> getExports(int userId, String sortKey, boolean ascending, int page, int rowsPerPage, boolean eagerloading, boolean determinestate, boolean onlynotnotified, boolean overrideSurveyTitle) {
		Session session = sessionFactory.getCurrentSession();
		
		//I do this for security reasons as we will use string concatenation below (bad practice but hibernate does not allow to use parameters for ordering)
		if (!(sortKey.equals("form") || sortKey.equals("date")))
		{
			sortKey = "name";
		}
		if (sortKey.equals("form"))
		{
			sortKey = "survey.titleSort";
		}
		
		Query query;
		if (userId == -1)
		{
			query = session.createQuery("SELECT e FROM Export e LEFT JOIN e.survey survey WHERE e.userId > 0 ORDER BY e." + sortKey + " " + (ascending? "ASC" : "DESC"));
		} else {
		
			String sql = "SELECT e FROM Export e LEFT JOIN e.survey survey WHERE e.userId = :userId";
			if (onlynotnotified)
			{
				sql += " AND e.state = 2 AND e.notified = false AND e.survey.archived = false";
			}
			
			query = session.createQuery(sql + " ORDER BY e." + sortKey + " " + (ascending? "ASC" : "DESC"));
			query.setInteger("userId", userId);
		}
		
		List<Export> exports = null;
		
		if(page > -1)
		{
			if(rowsPerPage < 0)
				rowsPerPage = 0;
			
			@SuppressWarnings("rawtypes")
			List list = query.setFirstResult(page * rowsPerPage).setMaxResults(rowsPerPage).setReadOnly(true).list();
			exports = list;
		} else {
			
			if (onlynotnotified)
			{
				query.setMaxResults(1);
			}
			
			@SuppressWarnings("rawtypes")
			List list  = query.setReadOnly(true).list();
			exports = list;
		}
		List<Export> result = new ArrayList<>();
		for (Export export : exports) {
			
			if (export.getSurvey() == null || !export.getSurvey().getArchived())
			{
				if (eagerloading)
				{
					if (export.getResultFilter() != null)
					{
						Hibernate.initialize(export.getResultFilter().getLanguages());
						Hibernate.initialize(export.getResultFilter().getFilterValues());
						Hibernate.initialize(export.getResultFilter().getVisibleQuestions());
						Hibernate.initialize(export.getResultFilter().getExportedQuestions());
						Hibernate.initialize(export.getResultFilter().getVisibleExplanations());
						Hibernate.initialize(export.getResultFilter().getExportedExplanations());
						Hibernate.initialize(export.getResultFilter().getVisibleDiscussions());
						Hibernate.initialize(export.getResultFilter().getExportedDiscussions());
					}
					
					if (export.getActivityFilter() != null)
					{
						Hibernate.initialize(export.getActivityFilter().getVisibleColumns());
						Hibernate.initialize(export.getActivityFilter().getExportedColumns());
					}
					
					if (determinestate) determineValidState(export, true);
				} else {
					if (determinestate) determineValidState(export, false);
				}
				
				if (overrideSurveyTitle && export.getSurvey() != null) {
					try {
						export.setSurvey(surveyService.getSurveyInOriginalLanguage(export.getSurvey().getId(), export.getSurvey().getShortname(), export.getSurvey().getUniqueId()));
					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}

				User user = (User) session.get(User.class, export.getUserId());
				if (user != null) {
					export.setDisplayUsername(user.getName());
				}
				
				result.add(export);
			}
		}

		return result;
	}
	
	@Transactional(readOnly = true)
	public List<Export> getSurveyExports(int surveyId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Export e WHERE e.survey.id = :id");
		query.setInteger("id", surveyId);
		
		@SuppressWarnings("unchecked")
		List<Export> exports = query.list();
		return exports;
	}

	@Transactional(readOnly = false)
	public void deleteSurveyExports(int surveyId) throws IOException {
		Session session = sessionFactory.getCurrentSession();
		
		Query query = session.createQuery("FROM Export e WHERE e.survey.id = :id");
		query.setInteger("id", surveyId);
		
		@SuppressWarnings("unchecked")
		List<Export> exports = query.list();
		for (Export export : exports)
		{		
			deleteExportFiles(export);
		}		
		
		query = session.createQuery("DELETE FROM Export e WHERE e.survey.id = :id");
		query.setInteger("id", surveyId);
		
		query.executeUpdate();
	}
	
	@Transactional(readOnly = true)
	public Export getExport(int exportId, boolean eagerloading) {
		try
		{
			Session session = sessionFactory.getCurrentSession();
			
			Export export = (Export) session.get(Export.class, exportId);
			
			determineValidState(export, false);
			
			if (eagerloading)
			{
				Hibernate.initialize(export.getActivityFilter());
				Hibernate.initialize(export.getResultFilter());
				if (export.getResultFilter() != null)
				{
					Hibernate.initialize(export.getResultFilter().getLanguages());
					Hibernate.initialize(export.getResultFilter().getExportedQuestions());
					Hibernate.initialize(export.getResultFilter().getFilterValues());
					Hibernate.initialize(export.getResultFilter().getVisibleQuestions());
					Hibernate.initialize(export.getResultFilter().getVisibleExplanations());
					Hibernate.initialize(export.getResultFilter().getExportedExplanations());
					Hibernate.initialize(export.getResultFilter().getVisibleDiscussions());
					Hibernate.initialize(export.getResultFilter().getExportedDiscussions());
				}
			}
			
			return export;
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Transactional(readOnly = false)
	public void deleteExport(Export export) {
		try
		{
			deleteExportFiles(export);
				
			Session session = sessionFactory.getCurrentSession();
			
			Query query = session.createQuery("delete Export e where e.id = :id");
			query.setInteger("id", export.getId());
			int rowCount = query.executeUpdate();
			
			if (rowCount < 1) logger.error("Deletion of export " + export.getId() +  " not possible!");
	
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);		
		}
	}
		
	private void deleteExportFiles(Export export) throws IOException {
		String filePath = getExportFilePath(export, null);

		File file = new File(filePath);
		Files.deleteIfExists(file.toPath());
		
		if (export.isTypeContent() && export.getFormat() == ExportFormat.xls) {
			int counter = 1;
			File additionalFile = new File(getExportPathWithSuffix(filePath, "xls", counter++));
			while (additionalFile.exists()) {
				Files.delete(additionalFile.toPath());
				additionalFile = new File(getExportPathWithSuffix(filePath, "xls", counter++));
			}
		}
		
		file = new File(filePath + ".zip");
		Files.deleteIfExists(file.toPath());
	}
	
	public static String getExportPathWithSuffix(String exportFilePath, String ext, int fileCounter) {
		return exportFilePath.replace("." + ext, "_" + fileCounter + "." + ext);
	}

	void determineValidState(Export export, boolean evict) {
		try {
			if (export != null)
			{
				if (export.getType().equals(ExportType.AddressBook) || export.getType().equals(ExportType.VoterFiles))
				{
					export.setValid(true);
				} else if(export.getType().equals(ExportType.Activity)) {
					Session session = sessionFactory.getCurrentSession();
					if (evict)
					{
						session.evict(export);
					}
					
					Query query = session.createQuery("SELECT max(a.date) FROM Activity a WHERE a.surveyUID = :surveyUID");
					query.setString("surveyUID", export.getSurvey().getUniqueId());
					
					Date max = (Date)query.uniqueResult();
					
					export.setValid(!(max != null && max.after(export.getDate())));
				} else {		
					Session session = sessionFactory.getCurrentSession();
					
					if (evict)
					{
						session.evict(export);
					}
					
					Query query = session.createQuery("SELECT max(a.updateDate) FROM AnswerSet a WHERE a.surveyId = :surveyId AND a.isDraft = false");
					
					if (export.getSurvey() != null)
					{
						query.setInteger("surveyId", export.getSurvey().getId());
					} else if (export.getResultFilter() != null)
					{
						query.setInteger("surveyId", export.getResultFilter().getSurveyId());
					}
					
					Date max = (Date)query.uniqueResult();
					
					export.setValid(!(max != null && max.after(export.getDate())));
				}
			}		
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * Check if user waits for some exports to finished
	 * @param userID id of user
	 * @return
	 * true if user has pending exports, false otherwise 
	 */
	@Transactional(readOnly = true)
	public boolean hasPendingExports(int userID) {
		
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT count(*) FROM Export e WHERE e.userId = :userId and e.state = :state and e.notified is false" );
		query.setInteger("userId", userID);
		query.setParameter("state", ExportState.Finished);
		long count = (Long) query.uniqueResult();
		return count > 0 ;
	}
	
	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public Export update(Export export) {
		try {
			Session session = sessionFactory.getCurrentSession();
			export = (Export) session.merge(export);
			session.setReadOnly(export, false);
			session.saveOrUpdate(export);
			session.flush();
			return export;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void invalidate(int surveyId) {
		Session session = sessionFactory.getCurrentSession();
		
		try {		
			Query query = session.createQuery("UPDATE Export e SET e.valid = false WHERE e.survey.id = :id AND e.valid = true");
			query.setInteger("id", surveyId);
			
			query.executeUpdate();
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@Transactional
	public void setNotified(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		try {		
			Query query = session.createQuery("UPDATE Export e SET e.notified = true WHERE e.id = :id");
			query.setInteger("id", id);
			
			query.executeUpdate();
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}		
	}

	@Transactional(readOnly = true)
	public Export get(int id, boolean refresh) {
		Session session = sessionFactory.getCurrentSession();
		
		Export export = (Export) session.get(Export.class, id);
		
		if (refresh)
		{
			session.refresh(export);
		}
		
		return export;
	}

	public void recreateExport(Export export, Locale locale, MessageSource resources) throws IOException {
		deleteExportFiles(export);
	
		export.setState(ExportState.Pending);
		export.setDate(new Date());
		Form form = new Form(resources);
		
		Survey oldSurvey = export.getSurvey();
		if (oldSurvey != null)
		{
			Survey newSurvey = surveyService.getSurveyByUniqueId(export.getSurvey().getUniqueId(), true, export.getSurvey().getIsDraft());
			
			if (!oldSurvey.getId().equals(newSurvey.getId()) && !export.getType().equals(ExportType.Files))
			{
				try {				
				
					//get mapping of old ids to source ids
					Map<Integer, String> oldUniqueIdsById = SurveyService.getUniqueIdsById(oldSurvey);
					
					//get mapping of source ids to new ids
					HashMap<String, Integer> newIdsByUniqueId = new HashMap<>();
					for (Element element : newSurvey.getElementsRecursive(true)) {
						newIdsByUniqueId.put(element.getUniqueId(), element.getId());
					}
					
					List<String> newIds = new ArrayList<>();
					for (String sid : export.getResultFilter().getVisibleQuestions()) {
						int id = Integer.parseInt(sid);
						if (newIdsByUniqueId.containsKey(oldUniqueIdsById.get(id)))
						{
							Integer newId = newIdsByUniqueId.get(oldUniqueIdsById.get(id));
							if (newId != null)
							{
								newIds.add(newId.toString());
							}
						}
					}
					for (String id: newIds)
					{
						export.getResultFilter().getVisibleQuestions().add(id);
					}
					
					newIds = new ArrayList<>();
					for (String sid : export.getResultFilter().getExportedQuestions()) {
						int id = Integer.parseInt(sid);
						if (newIdsByUniqueId.containsKey(oldUniqueIdsById.get(id)))
						{
							Integer newId = newIdsByUniqueId.get(oldUniqueIdsById.get(id));
							if (newId != null)
							{
								newIds.add(newId.toString());
							}
						}
					}
					for (String id: newIds)
					{
						export.getResultFilter().getExportedQuestions().add(id);
					}
	
					for (String skey : export.getResultFilter().getFilterValues().keySet())
					{				
						int key = Integer.parseInt(skey);
						
						if (oldUniqueIdsById.containsKey(key) && newIdsByUniqueId.containsKey(oldUniqueIdsById.get(key)))
						{
							Element element = oldSurvey.getElementsById().get(key);
							
							//check if value is also key
							if (element instanceof ChoiceQuestion || element instanceof Text)
							{
								String value = export.getResultFilter().getFilterValues().get(String.valueOf(key));
								String[] values = value.split(";");
								StringBuilder newValues = new StringBuilder();
								for (String svalId : values)
								{
									int valId = Integer.parseInt(svalId);
									
									if (newValues.length() > 0)
									{
										newValues.append(";");
									}
									if (oldUniqueIdsById.containsKey(valId) && newIdsByUniqueId.containsKey(oldUniqueIdsById.get(valId)))
									{
										newValues.append(newIdsByUniqueId.get(oldUniqueIdsById.get(valId)));
									} else {
										newValues.append(valId); // this should not happen but we keep the original id if we cannot find a new one
									}
								}
							}
						}
					}
				} catch (NumberFormatException nfx) {
					//probably not an "old" export with ids instead of uids
				}
			}		
		
			form.setSurvey(newSurvey);
			export.setSurvey(form.getSurvey());
		}
		
		exportService.prepareExport(form, export);
		exportService.startExport(form, export, false, resources, locale, null, null, false);
	}

	@Transactional
	public void applyExportTimeout() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("UPDATE Export e SET e.state = 1 WHERE e.state = 0 AND e.date <= :date");
		
		Calendar cal = Calendar.getInstance();
		int days = Integer.parseInt(exporttimeout);
		cal.add(Calendar.DATE, -1 * days);
		query.setTimestamp("date", cal.getTime());
		
		query.executeUpdate();		
	}
	
	@Transactional
	public void deleteExportZombieFiles() throws IOException {	
		
		if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("false"))
		{
			logger.info("calling worker server for deleting old export zombie files");
			
			try {			
				URL workerurl = new URL(workerserverurl + "worker/deleteExportZombieFiles");
				URLConnection wc = workerurl.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(wc.getInputStream()));
				String inputLine;
				StringBuilder result = new StringBuilder();
				while ((inputLine = in.readLine()) != null) 
					result.append(inputLine);
				in.close();
				
				if (!result.toString().equals("OK"))
				{
					logger.error("calling worker server for deleting old export zombie files returned " + result);
				}
				return;
			
			} catch (ConnectException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}		
		
		
		int minutes = Integer.parseInt(deleteexportstimeout);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, minutes);
		Date endtime = cal.getTime();
		Date currenttime = new Date();
		
		int counter = 0;
		
		int highestSurveyID = surveyService.getHighestSurveyId();
		
		int surveyID = Integer.parseInt(settingsService.get(Setting.LastCheckedSurveyIDForZombieFiles));
		if (surveyID == -1) {
			return; // nothing else to check
		}
		
		Set<String> checkedSurveyUIDs = new HashSet<>();
		while (currenttime.before(endtime) && surveyID <= highestSurveyID)
		{
			String surveyUID = surveyService.getSurveyUID(surveyID);
			
			//not all IDs are used as there could be deleted ones;
			if (surveyUID != null) {
				
				//skip those that were already checked during this run
				if (!checkedSurveyUIDs.contains(surveyUID)) {
					counter += fileService.deleteZombieExportFilesForSurvey(surveyUID);
					checkedSurveyUIDs.add(surveyUID);
				}
			}
			
			surveyID++;
			
			currenttime = new Date();
		}
		
		if (surveyID > highestSurveyID) {
			settingsService.update(Setting.LastCheckedSurveyIDForZombieFiles, "-1"); // -1 means that we successfully checked all existing surveys
		} else {		
			settingsService.update(Setting.LastCheckedSurveyIDForZombieFiles, Integer.toString(surveyID));
		}
		
		logger.info(counter + " old export zombie files deleted");
	}

	@Transactional
	public void deleteOldExports() throws IOException {
		
		if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("false"))
		{
			logger.info("calling worker server for deleting old exports");
			
			try {			
				URL workerurl = new URL(workerserverurl + "worker/deleteOldExports");
				URLConnection wc = workerurl.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(wc.getInputStream()));
				String inputLine;
				StringBuilder result = new StringBuilder();
				while ((inputLine = in.readLine()) != null) 
					result.append(inputLine);
				in.close();
				
				if (!result.toString().equals("OK"))
				{
					logger.error("calling worker server for deleting old exports returned " + result);
				}
				return;
			
			} catch (ConnectException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}		
		
		Session session = sessionFactory.getCurrentSession();
		
		int minutes = Integer.parseInt(deleteexportstimeout);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, minutes);
		Date endtime = cal.getTime();
		Date currenttime = new Date();
	
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);		
		Date date = cal.getTime();
		
		int counter = 0;
		
		while (currenttime.before(endtime))
		{
			Query query = session.createQuery("FROM Export e WHERE e.date < :date");
			query.setTimestamp("date", date).setMaxResults(1000);
			@SuppressWarnings("unchecked")
			List<Export> exports = query.list();
			
			for (Export export : exports) {
				
				deleteExportFiles(export);
				session.delete(export);
			}
			
			if (exports.size() < 1000)
			{
				break;
			}
			
			currenttime = new Date();
		}
		
		logger.info(counter + " old exports deleted");
	}
	
	@Transactional
	public void deleteOldWebserviceExports() throws IOException {
		
		if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("false"))
		{
			logger.info("calling worker server for deleting old webservice exports");
			
			try {			
				URL workerurl = new URL(workerserverurl + "worker/deleteOldWebserviceExports");
				URLConnection wc = workerurl.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(wc.getInputStream()));
				String inputLine;
				StringBuilder result = new StringBuilder();
				while ((inputLine = in.readLine()) != null) 
					result.append(inputLine);
				in.close();
				
				if (!result.toString().equals("OK"))
				{
					logger.error("calling worker server for deleting old webservice exports returned " + result);
				}
				return;
			
			} catch (ConnectException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}		
		
		Session session = sessionFactory.getCurrentSession();
		
		int minutes = Integer.parseInt(deleteexportstimeout);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, minutes);
		Date endtime = cal.getTime();
		Date currenttime = new Date();
	
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);		
		Date date = cal.getTime();
		
		int counter = 0;
		
		while (currenttime.before(endtime))
		{
			Query query = session.createQuery("FROM WebserviceTask t WHERE t.created < :date");
			query.setTimestamp("date", date).setMaxResults(1000);
			@SuppressWarnings("unchecked")
			List<WebserviceTask> tasks = query.list();
			
			for (WebserviceTask task : tasks) {
				if (task.isDone() && !task.isEmpty() && task.getResult() != null && task.getResult().length() > 0 && task.getSurveyUid() != null && task.getSurveyUid().length() > 0)
				{
					java.io.File target = fileService.getSurveyExportFile(task.getSurveyUid(), task.getResult());
					if (target.exists() && target.delete()) {
						counter++;
					}
				}
				session.delete(task);
			}
			
			if (tasks.size() < 1000)
			{
				break;
			}
			
			currenttime = new Date();
		}
		
		logger.info(counter + " old webservice exports deleted");
	}

	@Transactional(readOnly = true)
	public Export getExportByResultFilterID(int id) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Export e WHERE e.resultFilter.id = :id");
		query.setInteger("id", id);
		@SuppressWarnings("unchecked")
		List<Export> exports = query.list();
		if (!exports.isEmpty()) return exports.get(0);
		return null;
	}	
	
}
