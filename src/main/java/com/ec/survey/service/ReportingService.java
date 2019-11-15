package com.ec.survey.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ec.survey.exception.TooManyFiltersException;
import com.ec.survey.model.Answer;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.Setting;
import com.ec.survey.model.SqlPagination;
import com.ec.survey.model.survey.ChoiceQuestion;
import com.ec.survey.model.survey.DateQuestion;
import com.ec.survey.model.survey.Download;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.EmailQuestion;
import com.ec.survey.model.survey.FreeTextQuestion;
import com.ec.survey.model.survey.GalleryQuestion;
import com.ec.survey.model.survey.Matrix;
import com.ec.survey.model.survey.MultipleChoiceQuestion;
import com.ec.survey.model.survey.NumberQuestion;
import com.ec.survey.model.survey.RatingQuestion;
import com.ec.survey.model.survey.RegExQuestion;
import com.ec.survey.model.survey.Ruler;
import com.ec.survey.model.survey.SingleChoiceQuestion;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.Table;
import com.ec.survey.model.survey.Text;
import com.ec.survey.model.survey.Upload;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.SqlQueryService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;

@Service("reportingService")
public class ReportingService {
	
	protected static final Logger logger = Logger.getLogger(ReportingService.class);

	@Resource(name="sessionFactoryReporting")
	protected SessionFactory sessionFactoryReporting;
	
	@Resource(name="sessionFactory")
	protected SessionFactory sessionFactory;
	
	@Resource(name = "surveyService")
	protected SurveyService surveyService;
	
	@Resource(name = "answerService")
	protected AnswerService answerService;
	
	@Resource(name = "fileService")
	protected FileService fileService;
	
	@Resource(name = "settingsService")
	protected SettingsService settingsService;
	
	@Autowired
	private SqlQueryService sqlQueryService;
	
	protected @Value("${enablereportingdatabase}") String enablereportingdatabase;
	protected @Value("${contextpath}") String contextpath;	
	
	protected boolean isReportingDatabaseEnabled()
	{
		return enablereportingdatabase != null && enablereportingdatabase.equalsIgnoreCase("true");
	}
	
	public enum ToDo {
		NEWSURVEY(0), NEWCONTRIBUTION(1), CHANGEDCONTRIBUTION(2), DELETEDCONTRIBUTION(3), CHANGEDSURVEY(4), DELETEDSURVEY(5), CHANGEDDRAFTSURVEY(6), NEWTESTCONTRIBUTION(7), CHANGEDTESTCONTRIBUTION(8), DELETEDTESTCONTRIBUTION(9);
		
		private int value;
	    private ToDo(int value){
	        this.value = value;
	    }
	    
	    public int getValue() {
	        return value;
	    }
	}
	
	public class ToDoItem {
		public int Id;
		public ToDo Type;
		public String UID;
		public String Code;
		
		public ToDoItem(int id, int type, String uid, String code)
		{
			Id = id;
			Type = ToDo.values()[type];
			UID = uid;
			Code = code;
		}
	}
	
	public String getWhereClause(ResultFilter filter, Map<String, Object> values, Survey survey) throws TooManyFiltersException
	{
		String where = "";
		Map<String, Element> elementsByUniqueID = survey.getQuestionMapByUniqueId();
		
		if (filter != null) {
			
			if ( filter.getFilterValues() != null &&  filter.getFilterValues().size() > 3)
			{
				throw new TooManyFiltersException("too many result filters");
			}			
			
			if (filter.getInvitation() != null && filter.getInvitation().length() > 0)
			{
				if (where.length() == 0)
				{
					where += " WHERE";
				} else {
					where += " AND";
				}
				
				where += " QINVITATIONID = :invitationId";
				values.put("invitationId", filter.getInvitation().trim());
			}
			
			if (filter.getCaseId() != null && filter.getCaseId().length() > 0)
			{
				if (where.length() == 0)
				{
					where += " WHERE";
				} else {
					where += " AND";
				}
				
				where += " QCONTRIBUTIONID = :uniqueCode";
				values.put("uniqueCode", filter.getCaseId().trim());
			}
					
			
			if (filter.getUser() != null && filter.getUser().length() > 0)
			{
				if (where.length() == 0)
				{
					where += " WHERE";
				} else {
					where += " AND";
				}
				
				where += " QUSER = :email";
				values.put("email", filter.getUser().trim());
			}
			
			if (filter.getCreatedOrUpdated() != null && filter.getCreatedOrUpdated() && filter.getGeneratedFrom() != null && filter.getGeneratedTo() != null && filter.getUpdatedFrom() != null && filter.getUpdatedTo() != null)
			{
				if (where.length() == 0)
				{
					where += " WHERE";
				} else {
					where += " AND";
				}
				
				where += " ((QCREATED >= :generatedFrom AND QCREATED < :generatedTo) OR (QUPDATED >= :updateDateFrom AND QUPDATED < :updateDateTo))";
				values.put("generatedFrom", filter.getGeneratedFrom());
				values.put("generatedTo", Tools.getFollowingDay(filter.getGeneratedTo()));
				values.put("updateDateFrom", filter.getUpdatedFrom());
				values.put("updateDateTo", Tools.getFollowingDay(filter.getUpdatedTo()));
			} else {
				if (filter.getGeneratedFrom() != null) {
					if (where.length() == 0)
					{
						where += " WHERE";
					} else {
						where += " AND";
					}
					
					where += " QCREATED >= :generatedFrom";
					values.put("generatedFrom", filter.getGeneratedFrom());
				}
				
				if (filter.getGeneratedTo() != null) {
					if (where.length() == 0)
					{
						where += " WHERE";
					} else {
						where += " AND";
					}					
					
					where += " QCREATED < :generatedTo";
					values.put("generatedTo", Tools.getFollowingDay(filter.getGeneratedTo()));
				}
				
				if (filter.getUpdatedFrom() != null) {
					if (where.length() == 0)
					{
						where += " WHERE";
					} else {
						where += " AND";
					}	
					
					where += " QUPDATED >= :updateDateFrom";
					values.put("updateDateFrom", filter.getUpdatedFrom());
				}
				
				if (filter.getUpdatedTo() != null) {
					if (where.length() == 0)
					{
						where += " WHERE";
					} else {
						where += " AND";
					}	
					
					where += " QUPDATED < :updateDateTo";
					values.put("updateDateTo", Tools.getFollowingDay(filter.getUpdatedTo()));
				}
				
				if (filter.getOnlyReallyUpdated() != null && filter.getOnlyReallyUpdated())
				{
					if (where.length() == 0)
					{
						where += " WHERE";
					} else {
						where += " AND";
					}	
					
					where += " QCREATED != QUPDATED";
				}
			}
			
			if (filter.getLanguages() != null && filter.getLanguages().size() > 0)
			{
				int i = 0;
				if (where.length() == 0)
				{
					where += " WHERE (";
				} else {
					where += " AND (";
				}	
				for (String lang : filter.getLanguages())
				{
					if (lang.trim().length() > 0)
					{
						String l = "lang" + i++;
						
						if (i > 1)
						{
							where += " OR";
						}
						
						where += " QLANGUAGE like :" + l;
						values.put(l, lang.trim());
					}
				}
				where += " )";
			}
			
			Map<String, String> filterValues = filter.getFilterValues();
			if (filterValues != null && filterValues.size() > 0)
			{
				int i = 0;
				for (Entry<String, String> item : filterValues.entrySet())
				{
					String questionIdAndUid = item.getKey();
					String questionId = questionIdAndUid.substring(0, questionIdAndUid.indexOf('|'));
					String questionUid = questionIdAndUid.substring(questionIdAndUid.indexOf('|')+1);
					
					Element question = elementsByUniqueID.get(questionUid);
					
					String columnname = "Q" + questionUid.replace("-", "");
					
					String answersasstring = item.getValue();
					
					String[] answers = StringUtils.delimitedListToStringArray(answersasstring, ";");
					
					if (answersasstring.replace(";","").trim().length() > 0 && answers.length > 0)
					{
						if (where.length() == 0)
						{
							where += " WHERE (";
						} else {
							where += " AND (";
						}
						
						boolean first = true;
						
						for (String answer: answers)
						if (answer.trim().length() > 0)
						{
							if (!first) {
								where += " OR ";
							}
							
							if (question instanceof FreeTextQuestion || question instanceof EmailQuestion || question instanceof RegExQuestion)
							{
								where += columnname + " LIKE :answer" + i;
								values.put("answer" + i, "%" + answer + "%");
							} else if (question instanceof SingleChoiceQuestion) {
								String answerUid = answer.substring(answer.indexOf('|')+1);
								where += columnname + " = :answer" + i;
								values.put("answer" + i, answerUid);
							} else if (question instanceof MultipleChoiceQuestion) {
								String answerUid = answer.substring(answer.indexOf('|')+1);
								where += columnname + " LIKE :answer" + i;
								values.put("answer" + i, "%" + answerUid + "%");
							} else if (question instanceof NumberQuestion) {
								double val = Double.parseDouble(answer);
								where += columnname + " = :answer" + i;
								values.put("answer" + i, val);
							} else if (question instanceof DateQuestion) {
								Date val = ConversionTools.getDate(answer);
								where += columnname + " = :answer" + i;
								values.put("answer" + i, val);
							} else if (answer.contains("|")) { // Matrices
								String answerUid = answer.substring(answer.indexOf('|')+1);
								where += columnname + " LIKE :answer" + i;
								values.put("answer" + i, "%" + answerUid + "%");
							} else if (question instanceof Table) {
								Table table = (Table) question;
								String[] data = questionId.split("-");
								int row = Integer.parseInt(data[1]);
								int col = Integer.parseInt(data[2]);
								
								Element tablequestion = table.getQuestions().get(row-1);
								Element tableanswer = table.getAnswers().get(col-1);
								
								String id = Tools.md5hash(tablequestion.getUniqueId() + tableanswer.getUniqueId()); 
								columnname = "Q" + id;
								
								where += columnname + " LIKE :answer" + i;
								values.put("answer" + i, "%" + answer + "%");								
							} else if (question instanceof GalleryQuestion) {
								where += columnname + " LIKE :answer" + i;
								values.put("answer" + i, "%" + answer + ";%");								
							} else { //Rating
								where += columnname + " LIKE :answer" + i;
								values.put("answer" + i, "%" + answer + "%");
							}
													
							i++;
							first = false;
												
						}
						where += " )";
					}
				}			
			}
			
			if (filter.getSortKey() != null && filter.getSortKey().equalsIgnoreCase("score"))
			{
				where += " ORDER BY QSCORE " + filter.getSortOrder();
			} else if (filter.getSortKey() != null && filter.getSortKey().equalsIgnoreCase("date"))
			{
				where += " ORDER BY QCREATED " + filter.getSortOrder();
			}
		}
		
		return where;
	}
	
	private boolean isSecondTableUsed(Survey survey)
	{
		int columns = 8; //8 meta info columns	
		for (Element question : survey.getElementsRecursive())
		{
			if (question instanceof FreeTextQuestion)
			{
				columns++;
			} else if (question instanceof EmailQuestion || question instanceof RegExQuestion) {
				columns++;
			} else if (question instanceof NumberQuestion) {
				columns++;	
			} else if (question instanceof DateQuestion) {
				columns++;
			} else if (question instanceof SingleChoiceQuestion) {
				columns++;	
			} else if (question instanceof MultipleChoiceQuestion) {
				columns++;
			} else if (question instanceof Matrix) {
				Matrix matrix = (Matrix) question;
				columns += matrix.getQuestions().size();
			} else if (question instanceof Table) {
				Table table = (Table) question;
				columns += table.getQuestions().size() * table.getAnswers().size();
			} else if (question instanceof Upload) {
				//uploaded files cannot be used in filters
			} else if (question instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) question;
				
				if (gallery.getSelection())
					columns++;
			} else if (question instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion)question;
				columns += rating.getChildElements().size();
			}
		}
		
		return columns > 1000;
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public List<List<String>> getAnswerSets(Survey survey, ResultFilter filter, SqlPagination sqlPagination, boolean addlinks, boolean forexport, boolean showuploadedfiles, boolean doNotReplaceAnswerIDs) throws Exception {
		if (!isReportingDatabaseEnabled()) return null;		
		
		Session session = sessionFactoryReporting.getCurrentSession();
		
		Map<String, Object> values = new HashMap<String, Object>();
		String where = getWhereClause(filter, values, survey);
		
		Map<String, Element> visibleQuestions = new LinkedHashMap<String, Element>();
		
		for (Element question : survey.getQuestions())
    	{
    		if (filter.getVisibleQuestions().contains(question.getId().toString()))
    		{
	    		if (question instanceof Matrix)
    			{
	    			for (Element child : ((Matrix)question).getQuestions())
    				{
	    				visibleQuestions.put(child.getUniqueId(), (Matrix)question);
		    		}
    			} else if (question instanceof Table)
    			{
    				Table table = (Table)question;
	    			for (Element q : table.getQuestions())
	    			{
	    				for (Element a : table.getAnswers())
		    			{
	    					visibleQuestions.put(Tools.md5hash(q.getUniqueId() + a.getUniqueId()), null);
		    			}	    				
	    			}	 
    			} else if (question instanceof RatingQuestion)
	    		{
    				RatingQuestion rating = (RatingQuestion)question;
	    			for (Element child : rating.getQuestions())
	    			{
	    				visibleQuestions.put(child.getUniqueId(), child);
	    			}
	    		} else if (question instanceof Upload) 
	    		{
	    			if (showuploadedfiles)
	    			{
	    				visibleQuestions.put(question.getUniqueId(), question);	  
	    			}
    			} else if (!(question instanceof Text) && !(question instanceof Download) && !(question instanceof com.ec.survey.model.survey.Image) && !(question instanceof Ruler)) {
	    			visibleQuestions.put(question.getUniqueId(), question);	    		
	    		}
    		}
    	}
    	
    	if (filter.getVisibleQuestions().contains("invitation"))
		{
    		visibleQuestions.put("INVITATIONID", null);
		}
    	if (filter.getVisibleQuestions().contains("case"))
		{
    		visibleQuestions.put("CONTRIBUTIONID", null);
		}
    	if (filter.getVisibleQuestions().contains("user"))
		{
    		visibleQuestions.put("USER", null);
		}		    	
		if (filter.getVisibleQuestions().contains("created"))
		{
			visibleQuestions.put("CREATED", null);
		}			
		if (filter.getVisibleQuestions().contains("updated"))
		{
			visibleQuestions.put("UPDATED", null);
		}
		if (filter.getVisibleQuestions().contains("languages"))
		{
			visibleQuestions.put("LANGUAGE", null);
		}
	    if (survey.getIsQuiz() || filter.getVisibleQuestions().contains("score"))
	    {
	    	visibleQuestions.put("SCORE", null);
	    }
	    
//	    for (Element missingQuestion : survey.getMissingElements())
//	    {
//	    	if (missingQuestion instanceof Question)
//	    	{
//	    		visibleQuestions.put(missingQuestion.getUniqueId(), missingQuestion);
//	    	}
//	    }
	    
	    String sql = "SELECT QCONTRIBUTIONID, QANSWERSETID";
	    for (String question : visibleQuestions.keySet())
	    {
	    	if (question.equals("CONTRIBUTIONID"))
	    	{
	    		//automatically selected
	    	} else if (question.equals("CREATED") || question.equals("UPDATED"))
	    	{
	    		sql += ", DATE_FORMAT(Q" + question.replace("-", "") + ", \"%Y-%m-%d %H\\:%i\\:%s\")";
	    	} else {
	    		sql += ", Q" + question.replace("-", "");
	    	}
	    }
	    
	    sql += " FROM " + GetOLAPTableName(survey);
	    
	    if (isSecondTableUsed(survey))
		{
			sql += ", " + GetOLAPTableName(survey) + "_1";
		}		
		
		if (where.length() > 10)
		{
			sql += where;
		}
		
		SQLQuery query = session.createSQLQuery(sql);		
		sqlQueryService.setParameters(query, values);
		
		try {
			List<List<String>> rows = new ArrayList<List<String>>();
			
			if (sqlPagination != null)
			{
				query.setFirstResult(sqlPagination.getFirstResult()).setMaxResults(sqlPagination.getMaxResult());
			}
			
			@SuppressWarnings("unchecked")
			List<Object> result = query.list();
			
			for (Object o : result)
			{
				List<String> row = new ArrayList<String>();
				
				if (o instanceof Integer)
				{
					//special case: answer set has no answers
					row.add(o.toString());
				} else {				
					Object[] answerrow = (Object[]) o;
					
					row.add(answerrow[0].toString()); //this is the answerset code
					row.add(answerrow[1].toString()); //this is the answerset id
					
					int counter = 2;
					for (String questionuid : visibleQuestions.keySet())
				    {
						if (questionuid.equals("CONTRIBUTIONID"))
						{
							row.add(answerrow[0].toString());
						} else {						
						
							Object item = answerrow[counter];
							Element question = visibleQuestions.get(questionuid);
							
							if (question == null || item == null)
							{
								row.add(item != null ? item.toString() : "");
							} else if (question instanceof ChoiceQuestion) {
								ChoiceQuestion choicequestion = (ChoiceQuestion)question;						
								String[] answerids = item.toString().split(";");						
								String v = "";
								for (String answerid : answerids)
								{
									if (v.length() > 0) v += ";";
									Element answer = choicequestion.getPossibleAnswerByUniqueId(answerid);
									if (answer != null)
									{
										if (doNotReplaceAnswerIDs)
										{
											v += answerid;
										} else {
											v += answer.getTitle();
										}
									}							
								}						
								row.add(v.length() > 0 ? v : null);
							} else if (question instanceof Matrix) {
								Matrix matrix = (Matrix)question;
								String[] answerids = item.toString().split(";");						
								String v = "";
								for (String answerid : answerids)
								{
									if (v.length() > 0) v += ";";							
									Element answer = matrix.getChildByUniqueId(answerid);
									if (answer != null)
									{
										if (doNotReplaceAnswerIDs)
										{
											v += answerid;
										} else {
											v += answer.getTitle();
										}
									}							
								}						
								row.add(v.length() > 0 ? v : null);
							}  else if (question instanceof GalleryQuestion) {
								GalleryQuestion gallery = (GalleryQuestion)question;
								String[] indexes = item.toString().split(";");	
								String v = "";
								for (String index : indexes)
								{
									if (v.length() > 0) v += ";";		
									int i = Integer.parseInt(index);
									File file = gallery.getFiles().get(i);
									if (file != null)
									{
										v += file.getName();
									}							
								}						
								row.add(v.length() > 0 ? v : null);
							}  else if (question instanceof Upload) {
								String[] fileuids = item.toString().split(";");
								
								String v = "";
								for (String fileuid : fileuids)
								{
									if (v.length() > 0) v += ";";		
								
									File file = fileService.get(fileuid);
									if (file != null)
									{
										if (addlinks)
										{
											v += "<a target='blank' href='" + contextpath + "/files/" + survey.getUniqueId() + "/" + file.getUid() + "'>" + file.getNameForExport() + "</a><br />";
										} else if (forexport) {
											v += file.getUid() + "|" + file.getNameForExport() + ";";
										} else {
											v += file.getNameForExport() + "<br />";
										}
									}							
								}						
								row.add(v.length() > 0 ? v : null);
							} else {
								row.add(item.toString());
							}
							counter++;
						}
				    }
				}
				
				rows.add(row);				
			}
			
			return rows;
			
		} catch (Exception e) {
			return null;
		}	
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public List<Integer> getAnswerSetIDs(Survey survey, ResultFilter filter, SqlPagination sqlPagination) throws Exception {
		if (!isReportingDatabaseEnabled()) return null;		
		
		Session session = sessionFactoryReporting.getCurrentSession();
		
		Map<String, Object> values = new HashMap<String, Object>();
		String where = getWhereClause(filter, values, survey);
		
		String sql = "SELECT QANSWERSETID FROM " + GetOLAPTableName(survey);
		
		if (isSecondTableUsed(survey))
		{
			sql += ", " + GetOLAPTableName(survey) + "_1";
		}		
		
		if (where.length() > 10)
		{
			sql += where;
		}
		
		SQLQuery query = session.createSQLQuery(sql);		
		sqlQueryService.setParameters(query, values);

		List<Integer> res;
		
		try {			
			res = query.setFirstResult(sqlPagination.getFirstResult()).setMaxResults(sqlPagination.getMaxResult()).list();		
			return res;
		} catch (Exception e) {
			return null;
		}		
	}
	
	@Transactional(transactionManager = "transactionManagerReporting")
	public boolean OLAPTableExists(String uid, boolean draft) {
		if (!isReportingDatabaseEnabled()) return false;
		
		try {
			Session sessionReporting = sessionFactoryReporting.getCurrentSession();
			SQLQuery queryreporting = sessionReporting.createSQLQuery("SELECT 1 FROM  T" + (draft ? "D" : "") + uid.replace("-", "") + " LIMIT 1");
			queryreporting.uniqueResult();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Transactional(transactionManager = "transactionManagerReporting")
	public void deleteOLAPTable(String uid, boolean draftversion, boolean publishedversion) throws Exception {
		if (!isReportingDatabaseEnabled()) return;
		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		SQLQuery query;
		int counter = 1;
		if (publishedversion)
		{
			query = sessionReporting.createSQLQuery("DROP TABLE IF EXISTS T" + uid.replace("-", ""));
			query.executeUpdate();
			
			while (OLAPTableExists(uid + "_" + counter, false))
			{
				query = sessionReporting.createSQLQuery("DROP TABLE IF EXISTS T" + uid.replace("-", "") + "_" + counter);
				query.executeUpdate();
				counter++;
			}
		}
		
		counter = 1;
		if (draftversion)
		{
			query = sessionReporting.createSQLQuery("DROP TABLE IF EXISTS TD" + uid.replace("-", ""));
			query.executeUpdate();
			
			while (OLAPTableExists(uid + "_" + counter, true))
			{
				query = sessionReporting.createSQLQuery("DROP TABLE IF EXISTS TD" + uid.replace("-", "") + "_" + counter);
				query.executeUpdate();
				counter++;
			}
		}
	}	
	
	@Transactional(transactionManager = "transactionManagerReporting")
	public void createOLAPTable(String shortname, boolean draftversion, boolean publishedversion) throws Exception {
		if (!isReportingDatabaseEnabled()) return;
		
		if (publishedversion)
		{
			//create published survey table
			Survey survey = surveyService.getSurveyWithMissingElements(shortname, false, false, false, false, null, true, false);
			if (survey != null && !survey.getIsDeleted() && !survey.getArchived()) {
				if (!OLAPTableExists(survey.getUniqueId(), false))
				{
					createOLAPTable(survey);
				}
			}
		}
		
		if (draftversion)
		{
			//create draft survey table
			Survey draft = surveyService.getSurvey(shortname, true, false, false, false, null, true, false);
			if (draft != null && !draft.getIsDeleted() && !draft.getArchived()) {
				if (!OLAPTableExists(draft.getUniqueId(), true))
				{
					createOLAPTable(draft);
				}
			}
		}
	}
	
	private void createOLAPTable(Survey survey) throws Exception
	{		
		if (survey == null) return;
		
		logger.info("starting creating reporting table creation for " + survey.getShortname() + (survey.getIsDraft() ? " (draft)" : ""));	
		settingsService.update(Setting.ReportingMigrationSurveyToMigrate, survey.getShortname() + (survey.getIsDraft() ? " (draft)" : ""));
		
		Map<String, String> columns = new LinkedHashMap<String, String>();		
		
		//meta info
		columns.put("ANSWERSETID", "INT NOT NULL PRIMARY KEY");
		columns.put("INVITATIONID", "TEXT");
		columns.put("CONTRIBUTIONID", "TEXT");
		columns.put("USER", "TEXT");
		columns.put("CREATED", "DATETIME");	
		columns.put("UPDATED", "DATETIME");	
		columns.put("LANGUAGE", "VARCHAR(2)");	
		columns.put("SCORE", "INT");
				
		for (Element question : survey.getQuestions())
		{
			if (question instanceof FreeTextQuestion)
			{
				columns.put(question.getUniqueId(), "TEXT");
			} else if (question instanceof EmailQuestion || question instanceof RegExQuestion) {
				columns.put(question.getUniqueId(), "TEXT");
			} else if (question instanceof NumberQuestion) {
				columns.put(question.getUniqueId(), "DOUBLE");	
			} else if (question instanceof DateQuestion) {
				columns.put(question.getUniqueId(), "DATE");	
			} else if (question instanceof SingleChoiceQuestion) {
				columns.put(question.getUniqueId(), "TEXT");	
			} else if (question instanceof MultipleChoiceQuestion) {
				columns.put(question.getUniqueId(), "TEXT");	
			} else if (question instanceof Matrix) {
				Matrix matrix = (Matrix) question;
				for (Element child : matrix.getQuestions())
				{
					columns.put(child.getUniqueId(), "TEXT");
				}
			} else if (question instanceof Table) {
				Table table = (Table) question;
				for (Element child : table.getQuestions())
				{
					for (Element answer : table.getAnswers())
					{
						columns.put(Tools.md5hash(child.getUniqueId() + answer.getUniqueId()), "TEXT");
					}
				}
			} else if (question instanceof Upload) {
				columns.put(question.getUniqueId(), "TEXT");
			} else if (question instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) question;
				
				if (gallery.getSelection())
				columns.put(question.getUniqueId(), "TEXT");
			} else if (question instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion)question;
				
				for (Element child : rating.getChildElements())
				{
					columns.put(child.getUniqueId(), "TEXT");	
				}
			}
		}
				
		if (columns.size() >= 1000)
		{
			logger.info("1000 columns exceeded by " + survey.getUniqueId());
		}
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("CREATE TABLE ").append(GetOLAPTableName(survey)).append(" (");
		
		boolean first = true;
		int counter = 0;
		int tablecounter = 1;
		for (String col : columns.keySet())
		{
			if (first)
			{
				first = false;
			} else {
				sql.append(", ");
			}
			
			sql.append("Q").append(col.replace("-", "")).append(" ").append(columns.get(col));
			
			counter++;
			if (counter > 1000)
			{
				sql.append(" ) ENGINE=MYISAM");
				execute(sql.toString());
				sql = new StringBuilder();
				sql.append("CREATE TABLE T");
				if (survey.getIsDraft())
				{
					sql.append("D");
				}
				sql.append(survey.getUniqueId().replace("-", "")).append("_").append(tablecounter).append(" (");
				sql.append("QANSWERSETID INT NOT NULL PRIMARY KEY");
				counter = 0;
				tablecounter++;
			}
		}
		
		sql.append(" ) ENGINE=MYISAM");
		
		execute(sql.toString());
		
		analyseAnswers(survey, null, true);
			
		logger.info("finished creating reporting table creation for " + survey.getShortname());	
		settingsService.update(Setting.ReportingMigrationSurveyToMigrate, "");
		return;
	}
	
	@Transactional(transactionManager = "transactionManagerReporting")
	public void updateOLAPTable(String shortname, boolean draftversion, boolean publishedversion) throws Exception {
		if (!isReportingDatabaseEnabled()) return;
		
		if (publishedversion)
		{
			Survey survey = surveyService.getSurveyWithMissingElements(shortname, false, false, false, true, null, true, false);
			if (survey != null && !survey.getIsDeleted() && !survey.getArchived())
			{
				if (!OLAPTableExists(survey.getUniqueId(), false))
				{
					createOLAPTable(survey.getUniqueId(), false, true);
				} else {			
					updateOLAPTable(survey);
				}
				
				answerService.deleteStatisticsForSurvey(survey.getId());
			}
		}
		
		if (draftversion)
		{
			Survey draft = surveyService.getSurvey(shortname, true, false, false, true, null, true, false);
			if (draft != null && !draft.getIsDeleted() && !draft.getArchived())
			{
				if (!OLAPTableExists(draft.getUniqueId(), true))
				{
					createOLAPTable(draft.getUniqueId(), true, false);
				} else {			
					updateOLAPTable(draft);
				}
				
				answerService.deleteStatisticsForSurvey(draft.getId());
			}
		}
	}
	
	private String GetOLAPTableName(Survey survey)
	{
		return GetOLAPTableName(!survey.getIsDraft(), survey.getUniqueId());
	}
		
	private String GetOLAPTableName(boolean publishedSurvey, String uid) {
		if (!publishedSurvey)
		{ 
			return "TD" + uid.replace("-", "");
		} else {
			return "T" + uid.replace("-", "");
		}
	}
	
	private void updateOLAPTable(Survey survey) throws Exception {		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		SQLQuery queryreporting = sessionReporting.createSQLQuery("SELECT MAX(QUPDATED) FROM " + GetOLAPTableName(survey));
		Date lastReportDate = (Date) queryreporting.uniqueResult();
		Date lastAnswerDate = survey.getIsDraft() ? answerService.getNewestTestAnswerDate(survey.getId()) : answerService.getNewestAnswerDate(survey.getId());
		
		if (Tools.isEqual(lastReportDate, lastAnswerDate))
		{
			logger.info("no new answers");
			return;
		}
		
		analyseAnswers(survey, lastReportDate, false);
	}
	
	private void analyseAnswers(Survey survey, Date lastReportDate, boolean create) throws Exception
	{
		Session session;
		session = sessionFactory.getCurrentSession();
		
		List<Integer> allVersions = surveyService.getAllSurveyVersions(survey.getId());
		String hql = "SELECT a.id FROM AnswerSet a WHERE a.isDraft = false AND a.surveyId IN (" + StringUtils.collectionToCommaDelimitedString(allVersions) + ")";		
		
		if (lastReportDate != null)
		{
			hql += " AND a.updateDate > :start";
		}
		
		Query query = session.createQuery(hql);
		
		if (lastReportDate != null)
		{
			query.setTimestamp("start", lastReportDate);
		}
		
		query.setFetchSize(Integer.MIN_VALUE);
		query.setReadOnly(true);
		query.setCacheable(false);
		
		@SuppressWarnings("rawtypes")
		List results = query.list();
			
		for (Object o : results) 
		{
			int id = ConversionTools.getValue(o);
			AnswerSet answerSet = answerService.get(id, true);
			parseAnswerSetForReportingTable(answerSet, create, survey);
			session.evict(answerSet);
			answerSet = null;
		}
		
		logger.info(results.size() + " new answers copied");
	}
		
	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	private void execute(String sql) {
		lastQuery = sql;
		logger.debug(sql);
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		SQLQuery createQuery = sessionReporting.createSQLQuery(sql);
		createQuery.executeUpdate();		
	}

	private void parseAnswerSetForReportingTable(AnswerSet answerSet, boolean create, Survey survey) throws Exception
	{
		List<String> columns = new ArrayList<String>();
		Map<String, String> columnByParent = new HashMap<String, String>();
		List<String> values = new ArrayList<String>();		
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();	

		columns.add("INVITATIONID");
		values.add(answerSet.getInvitationId() != null && answerSet.getInvitationId().length() > 0 ? "'" + answerSet.getInvitationId() + "'" : null);
		
		columns.add("CONTRIBUTIONID");
		values.add(answerSet.getUniqueCode() != null && answerSet.getUniqueCode().length() > 0 ? "'" + answerSet.getUniqueCode() + "'" : null);
		
		columns.add("USER");
		values.add(":value" + parameters.size());
		parameters.put("value" + parameters.size(), answerSet.getResponderEmail() != null && answerSet.getResponderEmail().length() > 0 ? answerSet.getResponderEmail() : null);	
		
		columns.add("CREATED");			
		values.add(":value" + parameters.size());
		parameters.put("value" + parameters.size(), answerSet.getDate());			
		
		columns.add("UPDATED");
		values.add(":value" + parameters.size());
		parameters.put("value" + parameters.size(), answerSet.getUpdateDate());
		
		columns.add("LANGUAGE");
		values.add(answerSet.getLanguageCode() != null && answerSet.getLanguageCode().length() > 0 ? "'" + answerSet.getLanguageCode() + "'" : null);
		
		columns.add("SCORE");
		values.add(answerSet.getScore() != null ? answerSet.getScore().toString() : null);
		
		columns.add("ANSWERSETID");
		values.add(answerSet.getId().toString());
		
		for (Element question : survey.getQuestions())
		{
			if (question instanceof FreeTextQuestion || question instanceof EmailQuestion || question instanceof RegExQuestion)
			{
				List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
				columns.add(question.getUniqueId());
				values.add(":value" + parameters.size());
				parameters.put("value" + parameters.size(), answers.size() > 0 ? shrink(answers.get(0).getValue()) : null);
			} else if (question instanceof NumberQuestion) {
				List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());				
				Double num = null;
				if (answers.size() > 0)
				{
					try {
						num = Double.parseDouble(answers.get(0).getValue());
					} catch (Exception e) {
						num = 0.0;
					}
				}											
				columns.add(question.getUniqueId());
				values.add(":value" + parameters.size());
				parameters.put("value" + parameters.size(), num);			
			} else if (question instanceof DateQuestion) {
				List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());				
				Date d = null;
				if (answers.size() > 0)
				{
					d = ConversionTools.getDate(answers.get(0).getValue());						
				}
				columns.add(question.getUniqueId());
				values.add(":value" + parameters.size());
				parameters.put("value" + parameters.size(), d);				
			} else if (question instanceof ChoiceQuestion) {
				List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());				
				columns.add(question.getUniqueId());
				String v = null;
				if (answers.size() > 0)
				{
					v = "'";
					for (Answer answer : answers)
					{
						v += answer.getPossibleAnswerUniqueId();
						if (question instanceof MultipleChoiceQuestion) v += ";";
					}
					v += "'";
				}
				values.add(v);				
			} else if (question instanceof Matrix) {
				Matrix matrix = (Matrix) question;				
				for(Element matrixQuestion: matrix.getQuestions()) {
					List<Answer> answers = answerSet.getAnswers(matrixQuestion.getId(), matrixQuestion.getUniqueId());
					String v = null;
					if (answers.size() > 0)
					{
						v = "'";
						for (Answer answer : answers)
						{
							v += answer.getPossibleAnswerUniqueId();
							if (!matrix.getIsSingleChoice()) v  +=  ";";
						}
						v += "'";
					}
					
					if (columns.contains(matrixQuestion.getUniqueId()))
					{
						logger.info("multiple table rows with same uid: " + matrixQuestion.getUniqueId() + " - " + columnByParent.get(matrixQuestion.getUniqueId()) + " and " + matrix.getUniqueId());
					} else {
						columns.add(matrixQuestion.getUniqueId());				
						values.add(v);
					}
				}
			} else if (question instanceof Table) {
				Table table = (Table)question;
				int row = 0;
				for (Element tq : table.getQuestions())
				{
					row++;
					int col = 0;
					for (Element ta : table.getAnswers())
					{
						col++;				
						String answer = shrink(answerSet.getTableAnswer(table, row, col, false));							
						String hash = Tools.md5hash(tq.getUniqueId() + ta.getUniqueId());
						
						if (columns.contains(hash))
						{
							logger.info("multiple table rows with same uid: " + hash + " - " + columnByParent.get(hash) + " and " + table.getUniqueId());
						} else {						
							columns.add(hash);						
							columnByParent.put(hash, table.getUniqueId());
							
							values.add(":value" + parameters.size());
							parameters.put("value" + parameters.size(), shrink(answer));						
						}
					}
				}
				
			} else if (question instanceof Upload) {
				List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
				String v = null; 
				if (answers.size() > 0)
				{
					v = "'";
					for (Answer answer : answers)
					{
						for (File file : answer.getFiles())
						{
							v += file.getUid() + ";";
						}
					}
					v += "'";
				}					
				
				columns.add(question.getUniqueId());
				values.add(v);
			} else if (question instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) question;
				if (gallery.getSelection())
				{
					List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
					String v = null; 
					if (answers.size() > 0)
					{
						v = "'";
						for (Answer answer : answers)
						{
							v += answer.getValue() + ";";
						}
						v += "'";
					}					
					
					columns.add(question.getUniqueId());
					values.add(v);
				}
			} else if (question instanceof RatingQuestion) {
				
				RatingQuestion rating = (RatingQuestion) question;
				
				for(Element ratingQuestion: rating.getChildElements()) {
					List<Answer> answers = answerSet.getAnswers(ratingQuestion.getId(), ratingQuestion.getUniqueId());
					columns.add(ratingQuestion.getUniqueId());		
					values.add(answers.size() == 0 ? null : "'" + answers.get(0).getValue() + "'");
				}
			}
		}
		
		StringBuilder row = new StringBuilder(); 
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		if (!create)
		{
			row.append("DELETE FROM ");
			row.append(GetOLAPTableName(survey));
			row.append(" WHERE QANSWERSETID = ");
			row.append(answerSet.getId());
			
			SQLQuery deleteQuery = sessionReporting.createSQLQuery(row.toString());
			deleteQuery.executeUpdate();
			row = new StringBuilder(); 
			
			if (columns.size() > 1000)
			{
				row.append("DELETE FROM ");
				row.append(GetOLAPTableName(survey));
				row.append("_1 WHERE QANSWERSETID = ");
				row.append(answerSet.getId());
				
				deleteQuery = sessionReporting.createSQLQuery(row.toString());
				deleteQuery.executeUpdate();
				row = new StringBuilder(); 
			}
		}
		
		int counter = 0;
		int tablecounter = 0;
		
		StringBuilder cols = new StringBuilder();
		StringBuilder vals = new StringBuilder();		

		boolean first = true;
		for (int i = 0; i < columns.size(); i++)
		{
			String col = columns.get(i);
			String val = values.get(i);
			if (first)
			{
				first = false;
			} else {
				cols.append(", ");
				vals.append(", ");
			}
			cols.append("Q").append(col.replace("-", ""));
			vals.append(val);
			counter++;
			
			if (counter > 1000) {
				row.append("INSERT INTO ");
				row.append(GetOLAPTableName(survey));
				if (tablecounter > 0)
				{
					row.append("_");
					row.append(tablecounter);
				}
				row.append(" (");
				row.append(cols.toString());
				row.append(" ) VALUES ( ");
				row.append(vals.toString());
				row.append(" );");
				
				lastQuery = row.toString();
				SQLQuery createQuery = sessionReporting.createSQLQuery(lastQuery);				
				sqlQueryService.setParameters(createQuery, parameters);
				logger.debug(lastQuery);
				createQuery.executeUpdate();
				
				counter = 0;
				tablecounter++;
				row = new StringBuilder(); 
				cols = new StringBuilder();
				vals = new StringBuilder();
				
				cols.append("QANSWERSETID");
				vals.append(answerSet.getId());
			}
		}
		
		if (counter > 0) {
			row.append("INSERT INTO ");
			row.append(GetOLAPTableName(survey));
			
			if (tablecounter > 0)
			{
				row.append("_");
				row.append(tablecounter);
			}
			row.append(" (");
			row.append(cols.toString());
			row.append(" ) VALUES ( ");
			row.append(vals.toString());
			row.append(" );");
			
			lastQuery = row.toString();
			SQLQuery createQuery = sessionReporting.createSQLQuery(lastQuery);				
			sqlQueryService.setParameters(createQuery, parameters);
			logger.debug(lastQuery);
			createQuery.executeUpdate();
			
			counter = 0;
			tablecounter++;
			row = new StringBuilder(); 
		}
	}
	
	public static String lastQuery;
	
	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	public void removeFromOLAPTable(String uid, String code, boolean publishedSurvey) {
		if (!isReportingDatabaseEnabled()) return;
		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		StringBuilder query = new StringBuilder();
		
		if (OLAPTableExists(uid, !publishedSurvey))
		{		
			query.append("DELETE FROM ");
			query.append(GetOLAPTableName(publishedSurvey, uid));
			query.append(" WHERE QCONTRIBUTIONID = '");
			query.append(code).append("'");
			
			SQLQuery deleteQuery = sessionReporting.createSQLQuery(query.toString());
			deleteQuery.executeUpdate();
			
			//also remove from additional tables
			int counter = 1;
			while (OLAPTableExists(uid + "_" + counter, !publishedSurvey))
			{
				query = new StringBuilder();
				query.append("DELETE FROM ");
				query.append(GetOLAPTableName(publishedSurvey, uid) + "_" + counter);
				query.append(" WHERE QCONTRIBUTIONID = '");
				query.append(code).append("'");
				
				deleteQuery = sessionReporting.createSQLQuery(query.toString());
				deleteQuery.executeUpdate();
				counter++;
			}
		}
	}

	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getCount(Survey survey, String where, Map<String, Object> values) {
		if (!isReportingDatabaseEnabled()) return -1;
		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		String sql = "SELECT COUNT(*) FROM " + GetOLAPTableName(survey);
		
		if (where != null)
		{
			sql += where;
		}
		
		SQLQuery query = sessionReporting.createSQLQuery(sql);
		
		if (where != null)
		{
			for (String attrib : values.keySet()) {
				Object value = values.get(attrib);
				if (value instanceof String)
				{
					query.setString(attrib, (String)values.get(attrib));
				} else if (value instanceof Integer)
				{
					query.setInteger(attrib, (Integer)values.get(attrib));
				}  else if (value instanceof Date)
				{
					query.setTimestamp(attrib, (Date)values.get(attrib));
				}
			}
		}
		
		return ConversionTools.getValue(query.uniqueResult());
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getCount(Survey survey, String quid, String auid, boolean noPrefixSearch, String where, Map<String, Object> values) {
		if (!isReportingDatabaseEnabled()) return -1;
		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		String sql = "SELECT COUNT(*) FROM " + GetOLAPTableName(survey) + " WHERE Q" + quid.replace("-", "");
		if (auid == null) {
			sql += " IS NOT NULL";
		} else if (noPrefixSearch) {
			sql += " LIKE '" + auid + "%'";
		} else {
			sql += " LIKE '%" + auid + "%'";
		}
		
		if (where != null)
		{
			sql += " AND QANSWERSETID IN (SELECT QANSWERSETID FROM " + GetOLAPTableName(survey) + " " + where + ")";
		}
		
		SQLQuery query = sessionReporting.createSQLQuery(sql);
		
		if (where != null)
		{
			for (String attrib : values.keySet()) {
				Object value = values.get(attrib);
				if (value instanceof String)
				{
					query.setString(attrib, (String)values.get(attrib));
				} else if (value instanceof Integer)
				{
					query.setInteger(attrib, (Integer)values.get(attrib));
				}  else if (value instanceof Date)
				{
					query.setTimestamp(attrib, (Date)values.get(attrib));
				}
			}
		}		
		
		return ConversionTools.getValue(query.uniqueResult());
	}	
	
	private String shrink(String input) {
		if (input == null || input.length() < 5001) return input;
		return input.substring(0, 5000);
	}

	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	public void addToDo(ToDo todo, String uid, String code) {
		if (!isReportingDatabaseEnabled()) return;
		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		//check if TODO table exists
		try {
			SQLQuery querytodoexists = sessionReporting.createSQLQuery("SELECT 1 FROM TODO LIMIT 1");
			querytodoexists.uniqueResult();
			//table exists
		} catch (Exception e) {
			//table does not exist: create it:
			SQLQuery querycreate = sessionReporting.createSQLQuery("CREATE TABLE TODO (ID INT NOT NULL AUTO_INCREMENT, TYPE INT, UID VARCHAR(36), CODE VARCHAR(36), PRIMARY KEY (ID))");
			querycreate.executeUpdate();
			
			SQLQuery queryindex = sessionReporting.createSQLQuery("CREATE UNIQUE INDEX IDXUNIQUE ON TODO (TYPE, UID, CODE)");
			queryindex.executeUpdate();
		}
		
		if (todo == ToDo.NEWCONTRIBUTION || todo == ToDo.NEWTESTCONTRIBUTION)
		{		
			//check if there is a similar TODO
			SQLQuery querytodoexists = sessionReporting.createSQLQuery("SELECT ID FROM TODO WHERE TYPE = :type AND UID = :uid LIMIT 1");
			querytodoexists.setInteger("type", todo.getValue());
			querytodoexists.setString("uid", uid);
			@SuppressWarnings("rawtypes")
			List results = querytodoexists.list();
			
			if (results.size() > 0) return;		
		}
		
		SQLQuery queryinsert = sessionReporting.createSQLQuery("INSERT INTO TODO (ID, TYPE, UID, CODE) VALUES (null, :type, :uid, :code)");
		queryinsert.setInteger("type", todo.getValue());
		queryinsert.setString("uid", uid);
		queryinsert.setString("code", code);
		
		try {
			queryinsert.executeUpdate();
		} catch (ConstraintViolationException ce) {
			//this means there is already the same entry in the table: ignore
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}	

	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public List<ToDoItem> getToDos(int page, int rowsPerPage) {
		if (!isReportingDatabaseEnabled()) return null;
		
		List<ToDoItem> todos = new ArrayList<ToDoItem>();
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		//check if TODO table exists
		try {
			SQLQuery querytodoexists = sessionReporting.createSQLQuery("SELECT 1 FROM TODO LIMIT 1");
			querytodoexists.uniqueResult();
			//table exists
		} catch (Exception e) {
			return todos;
		}		
	
		SQLQuery query = sessionReporting.createSQLQuery("SELECT ID, TYPE, UID, CODE FROM TODO ORDER BY ID ASC");
		
		@SuppressWarnings("rawtypes")
		List results;
		
		if (page > -1)
		{
			results = query.setFirstResult(page * rowsPerPage).setMaxResults(rowsPerPage).setReadOnly(true).list();
		} else {
			results = query.setReadOnly(true).list();
		}
		
		for (Object o: results)
		{
			Object[] a = (Object[]) o;
			todos.add(new ToDoItem(ConversionTools.getValue(a[0]), ConversionTools.getValue(a[1]), (String)a[2], (String)a[3]));
		}
		
		return todos;
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public ToDoItem getToDo(int id) {
		if (!isReportingDatabaseEnabled()) return null;
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		SQLQuery query = sessionReporting.createSQLQuery("SELECT ID, TYPE, UID, CODE FROM TODO WHERE ID = :id");
		@SuppressWarnings("rawtypes")
		List result = query.setInteger("id", id).list();
		
		for (Object o: result)
		{
			Object[] a = (Object[]) o;
			return new ToDoItem(ConversionTools.getValue(a[0]), ConversionTools.getValue(a[1]), (String)a[2], (String)a[3]);
		}
		
		return null;
	}
	
	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	public void executeToDo(ToDoItem todo) throws Exception
	{
		switch (todo.Type){
			case NEWSURVEY:
				createOLAPTable(todo.UID, false, true);
				break;
			case NEWCONTRIBUTION:
				updateOLAPTable(todo.UID, false, true);
				break;
			case CHANGEDCONTRIBUTION:
				updateOLAPTable(todo.UID, false, true);
				break;
			case DELETEDCONTRIBUTION:
				removeFromOLAPTable(todo.UID, todo.Code, true);
				break;
			case CHANGEDSURVEY:
				deleteOLAPTable(todo.UID, false, true);
				createOLAPTable(todo.UID, false, true);
				break;
			case DELETEDSURVEY:
				deleteOLAPTable(todo.UID, true, true);
				break;
			case CHANGEDDRAFTSURVEY:
				deleteOLAPTable(todo.UID, true, false);
				createOLAPTable(todo.UID, true, false);
				break;
			case NEWTESTCONTRIBUTION:
				updateOLAPTable(todo.UID, true, false);
				break;
			case CHANGEDTESTCONTRIBUTION:
				updateOLAPTable(todo.UID, true, false);
				break;
			case DELETEDTESTCONTRIBUTION:
				removeFromOLAPTable(todo.UID, todo.Code, false);
				break;
		}
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public List<ToDoItem> getToDos() {
		return getToDos(-1,-1);
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getNumberOfToDos() {
		if (!isReportingDatabaseEnabled()) return 0;
		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		SQLQuery query = sessionReporting.createSQLQuery("SELECT COUNT(*) FROM TODO");
		
		try {		
			return ConversionTools.getValue(query.uniqueResult());
		} catch (Exception e) {
			return 0;
		}		
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getNumberOfTables()
	{
		if (!isReportingDatabaseEnabled()) return 0;
		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		SQLQuery query = sessionReporting.createSQLQuery("SELECT count(*) AS totalTables FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = database() AND TABLE_NAME != 'todo' AND NOT TABLE_NAME LIKE '%\\_%';");
		
		try {		 
			return ConversionTools.getValue(query.uniqueResult());
		} catch (Exception e) {
			return 0;
		}		
	}
	
	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	public void removeToDo(int id) {
		if (!isReportingDatabaseEnabled()) return;
		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		SQLQuery queryremove = sessionReporting.createSQLQuery("DELETE FROM TODO WHERE ID = :id");
		queryremove.setInteger("id", id);
		queryremove.executeUpdate();
	}
	
	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	public void removeToDo(ToDoItem todo, boolean includesimilar) {
		if (!isReportingDatabaseEnabled()) return;		
		
		if (includesimilar)
		{
			Session sessionReporting = sessionFactoryReporting.getCurrentSession();
			String sql = "DELETE FROM TODO WHERE Type = :type AND UID = :uid";
			if (todo.Code == null) {
				sql += " AND Code IS NULL";
			} else {
				sql += " AND Code = :code";
			}
			SQLQuery query = sessionReporting.createSQLQuery(sql);
			query.setInteger("type", todo.Type.value);
			query.setString("uid", todo.UID);
			if (todo.Code != null) {
				query.setString("code", todo.Code);
			}
			query.executeUpdate();
		} else {
			List<ToDoItem> list = new ArrayList<ToDoItem>();
			list.add(todo);
			removeToDos(list);
		}
	}
	
	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	public void removeToDos(List<ToDoItem> todos) {
		if (!isReportingDatabaseEnabled()) return;
		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		List<Integer> ids = new ArrayList<Integer>();
		for (ToDoItem todo : todos)
		{
			ids.add(todo.Id);
		}
		
		SQLQuery queryremove = sessionReporting.createSQLQuery("DELETE FROM TODO WHERE ID IN (:ids)");
		queryremove.setParameterList("ids", ids);
		queryremove.executeUpdate();
	}
	
	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	public void removeAllToDos() {
		if (!isReportingDatabaseEnabled()) return;
		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		List<ToDoItem> todos = getToDos(-1,-1);
		if (todos.size() == 0) return;
		
		SQLQuery queryremove = sessionReporting.createSQLQuery("DELETE FROM TODO");
		queryremove.executeUpdate();
	}

	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public Date getLastUpdate(Survey survey) {
		if (!isReportingDatabaseEnabled()) return null;
		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		SQLQuery query = sessionReporting.createSQLQuery("SELECT COUNT(*) FROM TODO WHERE UID = :uid");
		query.setString("uid", survey.getUniqueId());
		int result = ConversionTools.getValue(query.uniqueResult());
		
		if (result == 0) return null;
		
		query = sessionReporting.createSQLQuery("SELECT UPDATE_TIME FROM information_schema.TABLES WHERE TABLE_NAME = :name");
		query.setString("name", GetOLAPTableName(survey));
		
		return  (Date) query.uniqueResult();
	}
}
