package com.ec.survey.service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.hibernate.query.NativeQuery;
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
import com.ec.survey.model.ResultFilter.ResultFilterSortKey;
import com.ec.survey.model.Setting;
import com.ec.survey.model.SqlPagination;
import com.ec.survey.model.selfassessment.SATargetDataset;
import com.ec.survey.model.survey.ChoiceQuestion;
import com.ec.survey.model.survey.ComplexTable;
import com.ec.survey.model.survey.ComplexTableItem;
import com.ec.survey.model.survey.DateQuestion;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.EmailQuestion;
import com.ec.survey.model.survey.FreeTextQuestion;
import com.ec.survey.model.survey.FormulaQuestion;
import com.ec.survey.model.survey.GalleryQuestion;
import com.ec.survey.model.survey.Matrix;
import com.ec.survey.model.survey.MultipleChoiceQuestion;
import com.ec.survey.model.survey.NumberQuestion;
import com.ec.survey.model.survey.Question;
import com.ec.survey.model.survey.RankingItem;
import com.ec.survey.model.survey.RankingQuestion;
import com.ec.survey.model.survey.RatingQuestion;
import com.ec.survey.model.survey.RegExQuestion;
import com.ec.survey.model.survey.SingleChoiceQuestion;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.Table;
import com.ec.survey.model.survey.TimeQuestion;
import com.ec.survey.model.survey.Upload;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;

import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.NativeQuery;

@Service("reportingService")
public class ReportingService extends BasicService {

	protected static final int MAX_COLUMN_NUMBER_IN_OLAP_TABLE = 1000;
	
	protected static final Logger logger = Logger.getLogger(ReportingService.class);

	@Resource(name="sessionFactoryReporting")
	protected SessionFactory sessionFactoryReporting;
	
	@Resource(name="sessionFactory")
	protected SessionFactory sessionFactory;
		
	@Autowired
	private SqlQueryService sqlQueryService;
	
	protected @Value("${contextpath}") String contextpath;	
	
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
	
	public static String getWhereClause(ResultFilter filter, Map<String, Object> values, Survey survey) throws TooManyFiltersException
	{
		String where = "";
		Map<String, Question> elementsByUniqueID = survey.getQuestionMapByUniqueId();
		
		if (filter != null) {
			
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
				values.put(Constants.UNIQUECODE, filter.getCaseId().trim());
			}

			//ECF
			if (filter.getAnsweredECFProfileUID() != null && filter.getAnsweredECFProfileUID().length() > 0)
			{
				if (where.length() == 0)
				{
					where += " WHERE";
				} else {
					where += " AND";
				}
				
				where += " QECFPROFILEUID = :ecfProfileUid";
				values.put("ecfProfileUid", filter.getAnsweredECFProfileUID().trim());
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
				values.put(Constants.EMAIL, filter.getUser().trim());
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
			} else if (filter.getCreatedOrUpdated() != null && filter.getCreatedOrUpdated() && filter.getGeneratedFrom() != null && filter.getUpdatedFrom() != null) {
				if (where.length() == 0)
				{
					where += " WHERE";
				} else {
					where += " AND";
				}
				
				where += " (QCREATED >= :generatedFrom OR QUPDATED >= :updateDateFrom)";
				values.put("generatedFrom", filter.getGeneratedFrom());
				values.put("updateDateFrom", filter.getUpdatedFrom());		
			} else if (filter.getCreatedOrUpdated() != null && filter.getCreatedOrUpdated() && filter.getGeneratedTo() != null && filter.getUpdatedTo() != null) {
				if (where.length() == 0)
				{
					where += " WHERE";
				} else {
					where += " AND";
				}
				
				where += " (QCREATED <= :generatedTo OR QUPDATED <= :updateDateTo)";
				values.put("generatedTo", filter.getGeneratedTo());
				values.put("updateDateTo", filter.getUpdatedTo());
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
			
			if (filter.getLanguages() != null && !filter.getLanguages().isEmpty())
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
					String questionUid = questionIdAndUid.substring(questionIdAndUid.indexOf('|')+1).replace("from", "").replace("to", "");
					
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
						{
							if (answer.trim().length() > 0)
							{
								if (!first) {
									where += " OR ";
								}
								
								if (question instanceof FreeTextQuestion || question instanceof EmailQuestion || question instanceof RegExQuestion)
								{
									where += columnname + " LIKE :answer" + i;
									values.put(Constants.ANSWER + i, "%" + answer + "%");
								} else if (question instanceof SingleChoiceQuestion) {
									SingleChoiceQuestion scq = (SingleChoiceQuestion)question;
									if (scq.getIsTargetDatasetQuestion()) {
										String datasetid = answer.substring(answer.lastIndexOf('-')+1);
										where += columnname + " = :answer" + i;
										values.put(Constants.ANSWER + i, datasetid);
									} else {									
										String answerUid = answer.substring(answer.indexOf('|')+1);
										where += columnname + " = :answer" + i;
										values.put(Constants.ANSWER + i, answerUid);
									}
								} else if (question instanceof MultipleChoiceQuestion) {
									String answerUid = answer.substring(answer.indexOf('|')+1);
									where += columnname + " LIKE :answer" + i;
									values.put(Constants.ANSWER + i, "%" + answerUid + "%");
								} else if (question instanceof NumberQuestion || question instanceof FormulaQuestion) {
									Double val = ConversionTools.getDouble(answer);
									if (val != null) {
										where += columnname + " = :answer" + i;
										values.put(Constants.ANSWER + i, val);
									}
								} else if (question instanceof DateQuestion) {
									Date val = ConversionTools.getDate(answer);
									if (questionIdAndUid.endsWith("from")) {
										where += columnname + " >= :answer" + i;
									} else if (questionIdAndUid.endsWith("to")) {
										where += columnname + " <= :answer" + i;
									} else {
										where += columnname + " = :answer" + i;
									}
									values.put(Constants.ANSWER + i, val);
								} else if (question instanceof TimeQuestion) {
									where += columnname + " LIKE :answer" + i;
									values.put(Constants.ANSWER + i,  "%" + answer + "%");
								} else if (question instanceof RankingQuestion) {
									where += columnname + " LIKE :answer" + i;
									values.put(Constants.ANSWER + i, answer + "%");		
								} else if (answer.contains("|")) { // Matrices
									String answerUid = answer.substring(answer.indexOf('|')+1);
									where += columnname + " LIKE :answer" + i;
									values.put(Constants.ANSWER + i, "%" + answerUid + "%");
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
									values.put(Constants.ANSWER + i, "%" + answer + "%");								
								} else if (question instanceof GalleryQuestion) {
									where += columnname + " LIKE :answer" + i;
									values.put(Constants.ANSWER + i, "%" + answer + ";%");		
								} else { //Rating
									where += columnname + " LIKE :answer" + i;
									values.put(Constants.ANSWER + i, "%" + answer + "%");
								}
														
								i++;
								first = false;													
							}
						}
						where += " )";
					}
				}			
			}
			
			if (filter.getSortKey() != null) {
				switch (ResultFilterSortKey.parse(filter.getSortKey())) {
					case NAME:
						where += " ORDER BY CASE WHEN QUSER IS NULL THEN QCONTRIBUTIONID ELSE QUSER END "+ filter.getSortOrder();
						break;
					case SCORE:
						where += " ORDER BY QSCORE " + filter.getSortOrder();
						break;
					case CREATED:
						where += " ORDER BY QCREATED " + filter.getSortOrder();
						break;
					case ECFSCORE:
						where += " ORDER BY QECFTOTALSCORE " + filter.getSortOrder();
						break;
					case ECFGAP:
						where += " ORDER BY ABS(QECFTOTALGAP) " + filter.getSortOrder();
						break;
					default :
				}
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
			} else if (question instanceof NumberQuestion || question instanceof FormulaQuestion) {
				columns++;	
			} else if (question instanceof DateQuestion) {
				columns++;
			} else if (question instanceof TimeQuestion) {
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
	public List<List<String>> getAnswerSetsInternal(Survey survey, ResultFilter filter, SqlPagination sqlPagination, boolean addlinks, boolean forexport, boolean showuploadedfiles, boolean doNotReplaceAnswerIDs, boolean useXmlDateFormat, boolean showShortnames) throws Exception {
		return getAnswerSetsInternal(survey, filter, sqlPagination, addlinks, forexport, showuploadedfiles, doNotReplaceAnswerIDs, useXmlDateFormat, showShortnames, false);
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public List<List<String>> getAnswerSetsInternal(Survey survey, ResultFilter filter, SqlPagination sqlPagination, boolean addlinks, boolean forexport, boolean showuploadedfiles, boolean doNotReplaceAnswerIDs, boolean useXmlDateFormat, boolean showShortnames, boolean includeMissing) throws Exception {
		Session session = sessionFactoryReporting.getCurrentSession();

		Map<String, String> usersByUid = answerExplanationService.getUserAliases(survey.getUniqueId());

		Map<String, Object> values = new HashMap<>();
		String where = getWhereClause(filter, values, survey);

		Map<String, Element> visibleQuestions = new LinkedHashMap<>();

		for (Question question : survey.getQuestions()) {
			if (filter.getVisibleQuestions().contains(question.getId().toString())) {
				if (question instanceof Matrix) {
					for (Element child : ((Matrix) question).getQuestions()) {
						visibleQuestions.put(child.getUniqueId(), (Matrix) question);
					}
				} else if (question instanceof Table) {
					Table table = (Table) question;
					String lastCell = "";
					for (Element q : table.getQuestions()) {
						for (Element a : table.getAnswers()) {
							lastCell = Tools.md5hash(q.getUniqueId() + a.getUniqueId());
							visibleQuestions.put(lastCell, null);
						}
					}
					visibleQuestions.put(lastCell, table);

				} else if (question instanceof RatingQuestion) {
					RatingQuestion rating = (RatingQuestion) question;
					for (Element child : rating.getQuestions()) {
						visibleQuestions.put(child.getUniqueId(), rating);
					}
				} else if (question instanceof Upload) {
					if (showuploadedfiles) {
						visibleQuestions.put(question.getUniqueId(), question);
					}
				} else if (question instanceof ComplexTable) {
					for (Element child : ((ComplexTable) question).getQuestionChildElements()) {
						visibleQuestions.put(child.getUniqueId(), child);
					}
				} else if (question.isUsedInResults()) {
					visibleQuestions.put(question.getUniqueId(), question);
				}
			}
		}

		if (filter.getVisibleQuestions().contains("invitation")) {
			visibleQuestions.put("INVITATIONID", null);
		}
		if (filter.getVisibleQuestions().contains("case")) {
			visibleQuestions.put("CONTRIBUTIONID", null);
		}
		if (filter.getVisibleQuestions().contains("user")) {
			visibleQuestions.put("USER", null);
		}
		if (filter.getVisibleQuestions().contains("created")) {
			visibleQuestions.put("CREATED", null);
		}
		if (filter.getVisibleQuestions().contains("updated")) {
			visibleQuestions.put("UPDATED", null);
		}
		if (filter.getVisibleQuestions().contains("languages")) {
			visibleQuestions.put("LANGUAGE", null);
		}
		if (survey.getIsQuiz() || filter.getVisibleQuestions().contains("score")) {
			visibleQuestions.put("SCORE", null);
		}

		// QANSWERSETID is used in all tables (after 1000 columns a new table is created)
		String sql = "SELECT QCONTRIBUTIONID, " + getOLAPTableName(survey) + ".QANSWERSETID";
		for (String question : visibleQuestions.keySet()) {
			if (question.equals("CONTRIBUTIONID")) {
				//automatically selected
			} else if (question.equals("CREATED") || question.equals("UPDATED")) {
				if (useXmlDateFormat) {
					sql += ", DATE_FORMAT(Q" + question.replace("-", "") + ", \"%Y-%m-%d_%H-%i-%s\")";
				} else {
					sql += ", DATE_FORMAT(Q" + question.replace("-", "") + ", \"%Y-%m-%d %H\\:%i\\:%s\")";
				}
			} else {
				sql += ", Q" + question.replace("-", "");
			}
		}

		String tableName = getOLAPTableName(survey);
		sql += " FROM " + tableName;

		if (isSecondTableUsed(survey)) {
			String secondTable = tableName + "_1";
			sql += " LEFT JOIN " + secondTable + " ON " + tableName + ".QANSWERSETID = " + secondTable + ".QANSWERSETID";
		}

		if (where.length() > 10) {
			sql += where;
		}

		NativeQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, values);

		try {
			List<List<String>> rows = new ArrayList<>();

			if (sqlPagination != null) {
				query.setFirstResult(sqlPagination.getFirstResult()).setMaxResults(sqlPagination.getMaxResult());
			}

			@SuppressWarnings("unchecked")
			List<Object> result = query.list();

			for (Object o : result) {
				List<String> row = new ArrayList<>();

				if (o instanceof Integer) {
					//special case: answer set has no answers
					row.add(o.toString());
				} else {
					Object[] answerrow = (Object[]) o;

					row.add(answerrow[0].toString()); //this is the answerset code
					row.add(answerrow[1].toString()); //this is the answerset id

					int counter = 2;
					for (Entry<String, Element> entry : visibleQuestions.entrySet()) {
						String questionuid = entry.getKey();
						if (questionuid.equals("CONTRIBUTIONID")) {
							row.add(answerrow[0].toString());
						} else {

							Object item = answerrow[counter];
							Element question = entry.getValue();

							if (question == null || item == null) {
								row.add(item != null ? ConversionTools.escape(item.toString()) : "");
							} else if (question instanceof ChoiceQuestion) {
								ChoiceQuestion choicequestion = (ChoiceQuestion) question;
								String[] answerids = item.toString().split(";");
								String v = "";
								
								boolean isTargetDatasetQuestion = false; 
								if (survey.getIsSelfAssessment() && question instanceof SingleChoiceQuestion ) {
									SingleChoiceQuestion scq = (SingleChoiceQuestion) question;
									if (scq.getIsTargetDatasetQuestion()) {
										isTargetDatasetQuestion = true;
									}
								}								
								
								for (String answerid : answerids) {
									if (v.length() > 0) v += "; ";
									
									if (isTargetDatasetQuestion) {
										
										if (doNotReplaceAnswerIDs) {
											v += answerid;
										} else {
											SATargetDataset dataset = selfassessmentService.getTargetDataset(Integer.parseInt(answerid));
											v += dataset.getName();
										}
										
									} else {									
										Element answer = choicequestion.getPossibleAnswerByUniqueId(answerid);
										if (answer != null) {
											if (doNotReplaceAnswerIDs) {
												v += answerid;
											} else {
												v += answer.getStrippedTitle();
											}
	
											if (showShortnames) {
												v += "<span class='assignedValue hideme'>(" + answer.getShortname() + ")</span>";
											}
										} else if ("EVOTE-ALL".equals(answerid)) {
											String languageCode = survey.getLanguage().getCode();
											if (languageCode == null)
												languageCode = "en";
											v += resources.getMessage("label.EntireList", new Object[0], "Entire List", new Locale(languageCode));
										}
									}
								}
								row.add(v.length() > 0 ? v : null);
							} else if (question instanceof Matrix) {
								Matrix matrix = (Matrix) question;
								String[] answerids = item.toString().split(";");
								String v = "";
								for (String answerid : answerids) {
									if (v.length() > 0) v += "; ";
									Element answer = matrix.getChildByUniqueId(answerid);
									if (answer == null && includeMissing) {
										answer = matrix.getMissingChildByUniqueId(answerid);
									}
									if (answer != null) {
										if (doNotReplaceAnswerIDs) {
											v += answerid;
										} else {
											v += answer.getStrippedTitle();
										}

										if (showShortnames) {
											v += "<span class='assignedValue hideme'>(" + answer.getShortname() + ")</span>";
										}
									}
								}
								row.add(v.length() > 0 ? v : null);
							} else if (question instanceof GalleryQuestion) {
								GalleryQuestion gallery = (GalleryQuestion) question;
								String[] indexes = item.toString().split(";");
								String v = "";
								for (String index : indexes) {
									File file = null;
									if (index.contains("-")) {
										file = gallery.getFileByUid(index);
									} else {
										int i = Integer.parseInt(index);
										if (gallery.getFiles().size() > i) {
											file = gallery.getFiles().get(i);
										}
									}

									if (file != null) {
										if (v.length() > 0) v += "; ";
										v += file.getName();
									}
								}
								row.add(v.length() > 0 ? v : null);
							} else if (question instanceof Upload) {
								String[] fileuids = item.toString().split(";");

								String v = "";
								for (String fileuid : fileuids) {
									File file = fileService.get(fileuid);
									if (file != null) {
										if (addlinks) {
											v += "<a target='blank' href='" + contextpath + "/files/" + survey.getUniqueId() + Constants.PATH_DELIMITER + file.getUid() + "'>" + file.getNameForExport() + "</a><br />";
										} else if (forexport) {
											if (v.length() > 0) v += " ";
											v += file.getUid() + "|" + file.getNameForExport() + ";";
										} else {
											v += file.getNameForExport() + "<br />";
										}
									}
								}
								row.add(v.length() > 0 ? v : null);
							} else if (question instanceof RankingQuestion) {
								RankingQuestion rankingQuestion = (RankingQuestion) question;
								Map<String, RankingItem> children = rankingQuestion.getChildElementsByUniqueId();
								String[] answerids = item.toString().split(";");
								String v = "";
								for (String uniqueId : answerids) {
									if (v.length() > 0) v += "; ";
									RankingItem child = children.get(uniqueId);
									if (child != null) {
										if (doNotReplaceAnswerIDs) {
											v += uniqueId;
										} else {
											v += child.getTitle();
										}

										if (showShortnames) {
											v += "<span class='assignedValue hideme'>(" + child.getShortname() + ")</span>";
										}
									}
								}
								row.add(v.length() > 0 ? v : null);
							} else if (question instanceof FormulaQuestion) {
								FormulaQuestion formulaQuestion = (FormulaQuestion) question;
								if (formulaQuestion.getDecimalPlaces() != null) {
									row.add(String.format(Locale.ROOT, "%." + formulaQuestion.getDecimalPlaces() + "f", item));
								} else {
									row.add(item.toString());
								}
							} else if (question instanceof ComplexTableItem) {
								ComplexTableItem child = (ComplexTableItem) question;
								if (child.getCellType() == ComplexTableItem.CellType.SingleChoice || child.getCellType() == ComplexTableItem.CellType.MultipleChoice) {
									String[] answerids = item.toString().split(";");
									String v = "";
									for (String answerid : answerids) {
										if (v.length() > 0) v += "; ";
										Element answer = child.getPossibleAnswerByUniqueId(answerid);
										if (answer != null) {
											if (doNotReplaceAnswerIDs) {
												v += answerid;
											} else {
												v += answer.getStrippedTitle();
											}

											if (showShortnames) {
												v += "<span class='assignedValue hideme'>(" + answer.getShortname() + ")</span>";
											}
										}
									}
									row.add(v.length() > 0 ? v : null);
								} else if (child.getCellType() == ComplexTableItem.CellType.Number || child.getCellType() == ComplexTableItem.CellType.Formula) {
									if (child.getDecimalPlaces() != null) {
										row.add(String.format(Locale.ROOT, "%." + child.getDecimalPlaces() + "f", item));
									} else {
										row.add(item.toString());
									}
								} else {
									row.add(ConversionTools.escape(item.toString()));
								}
							} else {
								row.add(ConversionTools.escape(item.toString()));
							}

							if (question == null) {
								logger.info("question not found");
							}

							if (survey.getIsDelphi() && question != null && question.isDelphiElement()) {
								boolean skip = false;

								if (question instanceof Matrix) {
									//explanation and discussion columns are only added once (after the last matrix subquestion)
									Matrix matrix = (Matrix) question;
									List<Element> matrixQuestions = matrix.getQuestions();
									Element lastQuestion = matrixQuestions.get(matrixQuestions.size() - 1);
									if (!lastQuestion.getUniqueId().equals(questionuid)) {
										skip = true;
									}
								} else if (question instanceof RatingQuestion) {
									//explanation and discussion columns are only added once (after the last matrix subquestion)
									RatingQuestion rating = (RatingQuestion) question;
									List<Element> ratingQuestions = rating.getQuestions();
									Element lastQuestion = ratingQuestions.get(ratingQuestions.size() - 1);
									if (!lastQuestion.getUniqueId().equals(questionuid)) {
										skip = true;
									}
								}

								if (!skip && filter.getVisibleExplanations().contains(question.getId().toString())) {
									try {
										String explanation = answerExplanationService.getFormattedExplanationWithFiles(
												ConversionTools.getValue(answerrow[1]), question.getUniqueId(),
												survey.getUniqueId(), !forexport);
										row.add(explanation);

										int likes = answerExplanationService.getLikesForExplanation(
												ConversionTools.getValue(answerrow[1]), question.getUniqueId());
										row.add(String.valueOf(likes));
									} catch (NoSuchElementException ex) {
										row.add("");
										row.add("");
									}
								}

								if (!skip && filter.getVisibleDiscussions().contains(question.getId().toString())) {
									try {
										String discussion = answerExplanationService.getDiscussion(ConversionTools.getValue(answerrow[1]), question.getUniqueId(), !forexport, usersByUid);
										row.add(discussion);
									} catch (NoSuchElementException ex) {
										row.add("");
									}
								}
							}

							counter++;
						}
					}
				}

				rows.add(row);
			}

			return rows;

		} catch(PersistenceException e) {
			if (!(ExceptionUtils.getRootCause(e) instanceof java.sql.SQLSyntaxErrorException)) {
				logger.error(e.getLocalizedMessage(), e);
			}

			return null;
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);

			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public List<Integer> getAnswerSetIDsInternal(Survey survey, ResultFilter filter, SqlPagination sqlPagination) throws Exception {
		Session session = sessionFactoryReporting.getCurrentSession();
		
		Map<String, Object> values = new HashMap<>();
		String where = getWhereClause(filter, values, survey);
		
		String sql = "SELECT QANSWERSETID FROM " + getOLAPTableName(survey);
		
		if (isSecondTableUsed(survey))
		{
			sql += ", " + getOLAPTableName(survey) + "_1";
		}		
		
		if (where.length() > 10)
		{
			sql += where;
		}
		
		NativeQuery query = session.createSQLQuery(sql);		
		sqlQueryService.setParameters(query, values);

		List<Integer> res;
		
		try {			
			res = query.setFirstResult(sqlPagination.getFirstResult()).setMaxResults(sqlPagination.getMaxResult()).list();		
			return res;
		} catch (Exception e) {
			return null;
		}		
	}

	public boolean OLAPTableExistsInternal(Survey survey) {
		return this.OLAPTableExistsInternal(survey, null);
	}

	public boolean OLAPTableExistsInternal(boolean isDraft, String surveyUid) {
		return this.OLAPTableExistsInternal(isDraft, surveyUid, null);
	}

	@Transactional(transactionManager = "transactionManagerReporting")
	public boolean OLAPTableExistsInternal(boolean isDraft, String surveyUid, Integer counter) {
		if (surveyUid == null) {
			throw new IllegalArgumentException("surveyUid is not null");
		}
		if (counter != null && counter < 1) {
			throw new IllegalArgumentException("counter starts at 1");
		}
		try {
			Session sessionReporting = sessionFactoryReporting.getCurrentSession();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT 1 FROM ");
			sql.append(this.getOLAPTableName(!isDraft, surveyUid, counter));
			sql.append(" LIMIT 1");
			NativeQuery queryreporting = sessionReporting.createSQLQuery(sql.toString());
			queryreporting.uniqueResult();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Transactional(transactionManager = "transactionManagerReporting")
	public boolean OLAPTableExistsInternal(Survey survey, Integer counter) {
		return this.OLAPTableExistsInternal(survey.getIsDraft(), survey.getUniqueId(), counter);
	}

	@Transactional(transactionManager = "transactionManagerReporting")
	public boolean OLAPTableExistsInternal(String uid, boolean draft) {	
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		NativeQuery query = sessionReporting.createSQLQuery("SELECT count(*) AS totalTables FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = database() AND TABLE_NAME = 'T" + (draft ? "D" : "") + uid.replace("-", "") + "';");
	 
		return ConversionTools.getValue(query.uniqueResult()) > 0;
	}
	
	@Transactional(transactionManager = "transactionManagerReporting")
	public void deleteOLAPTableInternal(String uid, boolean draftversion, boolean publishedversion) {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		NativeQuery query;
		int counter = 1;
		if (publishedversion)
		{
			query = sessionReporting.createSQLQuery("DROP TABLE IF EXISTS T" + uid.replace("-", ""));
			query.executeUpdate();
			
			while (OLAPTableExistsInternal(uid + "_" + counter, false))
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
			
			while (OLAPTableExistsInternal(uid + "_" + counter, true))
			{
				query = sessionReporting.createSQLQuery("DROP TABLE IF EXISTS TD" + uid.replace("-", "") + "_" + counter);
				query.executeUpdate();
				counter++;
			}
		}
	}
	
	@Transactional(transactionManager = "transactionManagerReporting")
	public void createOLAPTableInternal(String shortname, boolean draftversion, boolean publishedversion) throws Exception {
		if (publishedversion)
		{
			//create published survey table
			Survey survey = surveyService.getSurveyWithMissingElements(shortname, false, false, false, false, null, true, false);
			if (survey != null && !survey.getIsDeleted() && !survey.getArchived() && !OLAPTableExistsInternal(survey.getUniqueId(), false)) {
				createOLAPTable(survey);
			}
		}
		
		if (draftversion)
		{
			//create draft survey table
			Survey draft = surveyService.getSurvey(shortname, true, false, false, false, null, true, false);
			if (draft != null && !draft.getIsDeleted() && !draft.getArchived() && !OLAPTableExistsInternal(draft.getUniqueId(), true)) {
				createOLAPTable(draft);
			}
		}
	}

	private Map<String, String> getColumnNamesAndTypes(Survey survey) {
		if (survey == null) {
			throw new IllegalArgumentException("survey is not null");
		}
		Map<String, String> columnNamesToType = new LinkedHashMap<>();

		// meta info
		columnNamesToType.put("ANSWERSETID", "INT NOT NULL PRIMARY KEY");
		columnNamesToType.put("INVITATIONID", "TEXT");
		columnNamesToType.put("CONTRIBUTIONID", "TEXT");
		columnNamesToType.put("USER", "TEXT");
		columnNamesToType.put("CREATED", "DATETIME");
		columnNamesToType.put("UPDATED", "DATETIME");
		columnNamesToType.put("LANGUAGE", "VARCHAR(2)");
		columnNamesToType.put("SCORE", "INT");
		columnNamesToType.put("ECFPROFILEUID", "VARCHAR(255)");
		columnNamesToType.put("ECFTOTALSCORE", "INT");
		columnNamesToType.put("ECFTOTALGAP", "INT");

		for (Element question : survey.getQuestions()) {
			if (question instanceof FreeTextQuestion) {
				putColumnNameAndType(columnNamesToType, question.getUniqueId(), "TEXT");
			} else if (question instanceof EmailQuestion || question instanceof RegExQuestion) {
				putColumnNameAndType(columnNamesToType, question.getUniqueId(), "TEXT");
			} else if (question instanceof NumberQuestion) {
				putColumnNameAndType(columnNamesToType, question.getUniqueId(), "DOUBLE");
			} else if (question instanceof FormulaQuestion) {
				putColumnNameAndType(columnNamesToType, question.getUniqueId(), "DOUBLE");
			} else if (question instanceof DateQuestion) {
				putColumnNameAndType(columnNamesToType, question.getUniqueId(), "DATE");
			} else if (question instanceof TimeQuestion) {
				putColumnNameAndType(columnNamesToType, question.getUniqueId(), "TIME");
			} else if (question instanceof SingleChoiceQuestion) {
				putColumnNameAndType(columnNamesToType, question.getUniqueId(), "TEXT");
			} else if (question instanceof MultipleChoiceQuestion) {
				putColumnNameAndType(columnNamesToType, question.getUniqueId(), "TEXT");
			} else if (question instanceof Matrix) {
				Matrix matrix = (Matrix) question;
				for (Element child : matrix.getQuestions()) {
					putColumnNameAndType(columnNamesToType, child.getUniqueId(), "TEXT");
				}
			} else if (question instanceof Table) {
				Table table = (Table) question;
				for (Element child : table.getQuestions()) {
					for (Element answer : table.getAnswers()) {
						putColumnNameAndType(columnNamesToType, Tools.md5hash(child.getUniqueId() + answer.getUniqueId()), "TEXT");
					}
				}
			} else if (question instanceof Upload) {
				putColumnNameAndType(columnNamesToType, question.getUniqueId(), "TEXT");
			} else if (question instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) question;

				if (gallery.getSelection())
				{
					putColumnNameAndType(columnNamesToType, question.getUniqueId(), "TEXT");
				}
			} else if (question instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion) question;

				for (Element child : rating.getQuestions()) {
					putColumnNameAndType(columnNamesToType, child.getUniqueId(), "TEXT");
				}
			} else if (question instanceof RankingQuestion) {
				putColumnNameAndType(columnNamesToType, question.getUniqueId(), "TEXT");
			} else if (question instanceof ComplexTable) {
				ComplexTable table = (ComplexTable) question;
				for (ComplexTableItem child : table.getQuestionChildElements()) {
					if (child.getCellType() == ComplexTableItem.CellType.Number || child.getCellType() == ComplexTableItem.CellType.Formula) {
						putColumnNameAndType(columnNamesToType, child.getUniqueId(), "DOUBLE");
					} else {
						putColumnNameAndType(columnNamesToType, child.getUniqueId(), "TEXT");
					}
				}
			}
		}
		return columnNamesToType;
	}
	
	private void putColumnNameAndType(Map<String, String> columnNamesToType, String uid, String type) {
		if (columnNamesToType.containsKey(uid)) {
			return; //Ignore
		}
		
		columnNamesToType.put(uid, type);
	}

	public boolean validateOLAPTableInternal(Survey survey, Integer counter) {
		logger.info("starting reporting table validation for survey UID" + survey.getUniqueId()
		+ (survey.getIsDraft() ? " (draft)" : ""));

		if (!this.OLAPTableExistsInternal(survey, counter)) {
			logger.info("/!\\ OLAP table doesnt exist for " + survey.getUniqueId()
			+ (survey.getIsDraft() ? " (draft)" : "")
			+ (counter == null ? "" : " counter = " + counter));
			return false;
		}

		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		Map<String, String> expectedColumnNamesToType = this.getColumnNamesAndTypes(survey);
		// SELECT COLUMN_NAME, DATA_TYPE FROM information_schema.COLUMNS WHERE TABLE_NAME = 
		// 'te8b255c4a738405083105036263c654a_1'
		// AND COLUMN_NAME in ('QINVITATIONID', 'QCONTRIBUTIONID')
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COLUMN_NAME, DATA_TYPE");
		sql.append(" FROM information_schema.COLUMNS");
		sql.append(" WHERE TABLE_NAME = :tableName");
		sql.append(" AND COLUMN_NAME in (");

		for (String columnName : expectedColumnNamesToType.keySet()) {
			sql.append("\'Q");
			sql.append(columnName.replace("-", ""));
			sql.append("\'");
			sql.append(",");
		}
		String sqlString = sql.substring(0, sql.length() - 1) + ")";
		NativeQuery queryReporting = sessionReporting.createSQLQuery(sqlString);
		queryReporting.setString("tableName", getOLAPTableName(survey, counter));

		List<Object[]> actualColumnNamesAndType = (List<Object[]>) queryReporting.list();
		Map<String, String> actualColumnNameToType = new HashMap<>();
		for (Object[] columnNameAndType: actualColumnNamesAndType) {
			String cName = (String) columnNameAndType[0];
			String cType = (String) columnNameAndType[1];
			actualColumnNameToType.put(cName, cType);
		}

		for (String expectedColumName : expectedColumnNamesToType.keySet()) {
			String realExpectedColumnName = "Q" + expectedColumName.replace("-", "");
			String expectedColumnType = expectedColumnNamesToType.get(expectedColumName);
			// Verifying column name
			if (!actualColumnNameToType.containsKey(realExpectedColumnName)){
				logger.info("/!\\ OLAP table is missing the column " + realExpectedColumnName 
				+ " for " + survey.getUniqueId()
				+ (survey.getIsDraft() ? " (draft)" : "")
				+ (counter == null ? "" : " counter = " + counter));
				return false;
			}
			String actualColumnType = (actualColumnNameToType.get(realExpectedColumnName)).toUpperCase();
			// Verifying column type
			// expected = varchar(2) actual = varchar
			if (!expectedColumnType.startsWith(actualColumnType)) {
				logger.info("/!\\ OLAP table column " + realExpectedColumnName 
				+ " has the wrong type " + actualColumnType
				+ " (should be " + expectedColumnType + ")"
				+ " for " + survey.getUniqueId()
				+ (survey.getIsDraft() ? " (draft)" : "")
				+ (counter == null ? "" : " counter = " + counter));
				return false;
			}
		}

		return true;
	}

	public boolean validateOLAPTableInternal(Survey survey) {
		return this.validateOLAPTableInternal(survey, null);
	}

	public boolean validateOLAPTablesInternal(Survey survey){
		if (survey == null) {
			throw new IllegalArgumentException("survey is null");
		}

		Map<String, String> columnNamesToType = this.getColumnNamesAndTypes(survey);
		double expectedAdditionalNumberOfTablesD = (columnNamesToType.keySet().size() / MAX_COLUMN_NUMBER_IN_OLAP_TABLE) - 1;
		int expectedAdditionalNumberOfTables = (int) Math.ceil(expectedAdditionalNumberOfTablesD);

		if (!this.validateOLAPTableInternal(survey)) {
			return false;
		}

		for (int counter = 1; counter <= expectedAdditionalNumberOfTables; counter++) {
			if (!this.validateOLAPTableInternal(survey, counter)) {
				return false;
			}
		}

		return true;
	}


	public boolean validateOLAPTablesInternal(String surveyUID, boolean isDraft) {
		if (surveyUID == null || surveyUID.isEmpty()) {
			throw new IllegalArgumentException("surveyUID is not null and not empty");
		}
		
		logger.info("starting reporting table validation for survey UID" + surveyUID
				+ (isDraft ? " (draft)" : ""));

		Survey survey = surveyService.getSurveyWithMissingElements(surveyUID, isDraft, false, false, false, null, true, false);
		return this.validateOLAPTablesInternal(survey);
	}


	private void createOLAPTable(Survey survey) throws Exception
	{		
		if (survey == null) return;
		
		logger.info("starting creating reporting table creation for " + survey.getShortname() + (survey.getIsDraft() ? " (draft)" : ""));	
		settingsService.update(Setting.ReportingMigrationSurveyToMigrate, survey.getShortname() + (survey.getIsDraft() ? " (draft)" : ""));
		
		Map<String,String> columns = this.getColumnNamesAndTypes(survey);
				
		if (columns.size() >= 1000)
		{
			logger.info("1000 columns exceeded by " + survey.getUniqueId());
		}
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("CREATE TABLE ").append(getOLAPTableName(survey)).append(" (");
		
		boolean first = true;
		int counter = 0;
		int tablecounter = 1;
		for (Entry<String, String> entry : columns.entrySet())
		{
			if (first)
			{
				first = false;
			} else {
				sql.append(", ");
			}
			
			sql.append("Q").append(entry.getKey().replace("-", "")).append(" ").append(entry.getValue());
			
			counter++;
			if (counter > 1000)
			{
				sql.append(" ) ENGINE=MYISAM");
				executeInternal(sql.toString());
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
		
		executeInternal(sql.toString());
		
		analyseAnswers(survey, null, true);
			
		logger.info("finished creating reporting table creation for " + survey.getShortname());	
		settingsService.update(Setting.ReportingMigrationSurveyToMigrate, "");
	}
	
	@Transactional(transactionManager = "transactionManagerReporting")
	public void updateOLAPTableInternal(String shortname, boolean draftversion, boolean publishedversion) throws Exception {
		if (publishedversion)
		{
			Survey survey = surveyService.getSurveyWithMissingElements(shortname, false, false, false, true, null, true, false);
			if (survey != null && !survey.getIsDeleted() && !survey.getArchived())
			{
				if (!OLAPTableExistsInternal(survey.getUniqueId(), false))
				{
					createOLAPTableInternal(survey.getUniqueId(), false, true);
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
				if (!OLAPTableExistsInternal(draft.getUniqueId(), true))
				{
					createOLAPTableInternal(draft.getUniqueId(), true, false);
				} else {			
					updateOLAPTable(draft);
				}
				
				answerService.deleteStatisticsForSurvey(draft.getId());
			}
		}
	}

	private String getOLAPTableName(Survey survey, Integer counter) {
		String tableName = this.getOLAPTableName(survey);
		if (counter != null) {
			tableName = tableName + "_" + counter;
		}
		return tableName;
	}
	
	private String getOLAPTableName(Survey survey)
	{
		return getOLAPTableName(!survey.getIsDraft(), survey.getUniqueId());
	}
		
	private String getOLAPTableName(boolean publishedSurvey, String uid) {
		return this.getOLAPTableName(publishedSurvey, uid, null);
	}

	private String getOLAPTableName(boolean publishedSurvey, String uid, Integer tableCount) {
		if (uid == null) {
			throw new IllegalArgumentException("Survey uid is not null");
		}

		String uidAsInTableName = uid.replace("-", "");
		String tableName = publishedSurvey ? "T" + uidAsInTableName : "TD" + uidAsInTableName;

		if (tableCount != null) {
			tableName = tableName + "_" + tableCount;
		}
		return tableName;
	}
	
	private void updateOLAPTable(Survey survey) throws Exception {		
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		NativeQuery queryreporting = sessionReporting.createSQLQuery("SELECT MAX(QUPDATED) FROM " + getOLAPTableName(survey));
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
		}
		
		logger.info(results.size() + " new answers copied");
	}
		
	private void executeInternal(String sql) {
		lastQuery = sql;
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		NativeQuery createQuery = sessionReporting.createSQLQuery(sql);
		createQuery.executeUpdate();		
	}

	private void parseAnswerSetForReportingTable(AnswerSet answerSet, boolean create, Survey survey) throws Exception
	{
		List<String> columns = new ArrayList<>();
		Map<String, String> columnByParent = new HashMap<>();
		List<String> values = new ArrayList<>();		
		
		HashMap<String, Object> parameters = new HashMap<>();	

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
		
		columns.add("ECFPROFILEUID");
		values.add(answerSet.getEcfProfileUid() != null && answerSet.getEcfProfileUid().length() > 0 ? "'" + answerSet.getEcfProfileUid() + "'" : null);
		
		columns.add("ECFTOTALSCORE");
		values.add(answerSet.getEcfTotalScore() != null ? answerSet.getEcfTotalScore().toString() : null);
		
		columns.add("ECFTOTALGAP");
		values.add(answerSet.getEcfTotalGap() != null ? answerSet.getEcfTotalGap().toString() : null);
		
		columns.add("ANSWERSETID");
		values.add(answerSet.getId().toString());
		
		for (Element question : survey.getQuestions())
		{
			if (question instanceof FreeTextQuestion || question instanceof EmailQuestion || question instanceof RegExQuestion)
			{
				List<Answer> answers = answerSet.getAnswers(question.getUniqueId());
				columns.add(question.getUniqueId());
				values.add(":value" + parameters.size());
				parameters.put("value" + parameters.size(), !answers.isEmpty() ? shrink(answers.get(0).getValue()) : null);
			} else if (question instanceof NumberQuestion || question instanceof FormulaQuestion) {
				List<Answer> answers = answerSet.getAnswers(question.getUniqueId());				
				Double num = null;
				if (!answers.isEmpty())
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
				List<Answer> answers = answerSet.getAnswers(question.getUniqueId());				
				Date d = null;
				if (!answers.isEmpty())
				{
					d = ConversionTools.getDate(answers.get(0).getValue());						
				}
				columns.add(question.getUniqueId());
				values.add(":value" + parameters.size());
				parameters.put("value" + parameters.size(), d);
			} else if (question instanceof TimeQuestion) {
				List<Answer> answers = answerSet.getAnswers(question.getUniqueId());				
				String d = null;
				if (!answers.isEmpty())
				{
					d = answers.get(0).getValue();
					if (d != null && d.endsWith(":")) {
						d += "00";
					}
				}
				columns.add(question.getUniqueId());
				values.add(":value" + parameters.size());
				parameters.put("value" + parameters.size(), d);			
			} else if (question instanceof ChoiceQuestion) {
				List<Answer> answers = answerSet.getAnswers(question.getUniqueId());				
				columns.add(question.getUniqueId());
				String v = null;
				if (!answers.isEmpty())
				{
					v = "'";
					for (Answer answer : answers)
					{
						if (answer.getPossibleAnswerUniqueId() != null) {
							if (answer.getPossibleAnswerUniqueId().equalsIgnoreCase("TARGETDATASET")) {
								v += answer.getValue();
							} else {
								v += answer.getPossibleAnswerUniqueId();
							}
							if (question instanceof MultipleChoiceQuestion) v += ";";
						}
					}
					v += "'";
				}
				values.add(v);				
			} else if (question instanceof Matrix) {
				Matrix matrix = (Matrix) question;				
				for(Element matrixQuestion: matrix.getQuestions()) {
					List<Answer> answers = answerSet.getAnswers(matrixQuestion.getUniqueId());
					String v = null;
					if (!answers.isEmpty())
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
				List<Answer> answers = answerSet.getAnswers(question.getUniqueId());
				String v = null; 
				if (!answers.isEmpty())
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
					List<Answer> answers = answerSet.getAnswers(question.getUniqueId());
					String v = null; 
					if (!answers.isEmpty())
					{
						v = "'";
						for (Answer answer : answers)
						{
							if (!StringUtils.isEmpty(answer.getPossibleAnswerUniqueId())) {
								v += answer.getPossibleAnswerUniqueId() + ";";
							} else {							
								v += answer.getValue() + ";";
							}
						}
						v += "'";
					}					
					
					columns.add(question.getUniqueId());
					values.add(v);
				}
			} else if (question instanceof RatingQuestion) {
				
				RatingQuestion rating = (RatingQuestion) question;
				
				for(Element ratingQuestion: rating.getQuestions()) {
					List<Answer> answers = answerSet.getAnswers(ratingQuestion.getUniqueId());
					columns.add(ratingQuestion.getUniqueId());		
					values.add(answers.isEmpty() ? null : "'" + answers.get(0).getValue() + "'");
				}
			} else if (question instanceof RankingQuestion) {
				List<Answer> answers = answerSet.getAnswers(question.getUniqueId());				
				String d = null;
				if (!answers.isEmpty())
				{
					d = answers.get(0).getValue();						
				}
				columns.add(question.getUniqueId());
				values.add(":value" + parameters.size());
				parameters.put("value" + parameters.size(), d);	
			} else if (question instanceof ComplexTable) {
				
				ComplexTable table = (ComplexTable) question;
				
				for(ComplexTableItem child: table.getQuestionChildElements()) {				
					if (child.getCellType() == ComplexTableItem.CellType.FreeText)
					{
						List<Answer> answers = answerSet.getAnswers(child.getUniqueId());
						columns.add(child.getUniqueId());
						values.add(":value" + parameters.size());
						parameters.put("value" + parameters.size(), !answers.isEmpty() ? shrink(answers.get(0).getValue()) : null);
					} else if (child.getCellType() == ComplexTableItem.CellType.Number || child.getCellType() == ComplexTableItem.CellType.Formula) {
						List<Answer> answers = answerSet.getAnswers(child.getUniqueId());				
						Double num = null;
						if (!answers.isEmpty())
						{
							try {
								num = Double.parseDouble(answers.get(0).getValue());
							} catch (Exception e) {
								num = 0.0;
							}
						}											
						columns.add(child.getUniqueId());
						values.add(":value" + parameters.size());
						parameters.put("value" + parameters.size(), num);			
					} else if (child.getCellType() == ComplexTableItem.CellType.SingleChoice || child.getCellType() == ComplexTableItem.CellType.MultipleChoice) {
						List<Answer> answers = answerSet.getAnswers(child.getUniqueId());				
						columns.add(child.getUniqueId());
						String v = null;
						if (!answers.isEmpty())
						{
							v = "'";
							for (Answer answer : answers)
							{
								v += answer.getPossibleAnswerUniqueId();
								if (child.getCellType() == ComplexTableItem.CellType.MultipleChoice) v += ";";
							}
							v += "'";
						}
						values.add(v);				
					}					
				}
			}
		}
		
		StringBuilder row = new StringBuilder(); 
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		if (!create)
		{
			row.append("DELETE FROM ");
			row.append(getOLAPTableName(survey));
			row.append(" WHERE QANSWERSETID = ");
			row.append(answerSet.getId());
			
			NativeQuery deleteQuery = sessionReporting.createSQLQuery(row.toString());
			deleteQuery.executeUpdate();
			row = new StringBuilder(); 
			
			if (columns.size() > 1000)
			{
				row.append("DELETE FROM ");
				row.append(getOLAPTableName(survey));
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
				row.append(getOLAPTableName(survey));
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
				NativeQuery createQuery = sessionReporting.createSQLQuery(lastQuery);				
				sqlQueryService.setParameters(createQuery, parameters);
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
			row.append(getOLAPTableName(survey));
			
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
			NativeQuery createQuery = sessionReporting.createSQLQuery(lastQuery);				
			sqlQueryService.setParameters(createQuery, parameters);
			createQuery.executeUpdate();
		}
	}
	
	public static String lastQuery;
	
	public void removeFromOLAPTableInternal(String surveyUID, String code, boolean publishedSurvey) {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		StringBuilder query = new StringBuilder();
		
		if (OLAPTableExistsInternal(surveyUID, !publishedSurvey))
		{
			//get the answerset id first
			query.append("SELECT QANSWERSETID FROM ");
			query.append(getOLAPTableName(publishedSurvey, surveyUID));
			query.append(" WHERE QCONTRIBUTIONID = '");
			query.append(code).append("'");
			NativeQuery selectQuery = sessionReporting.createSQLQuery(query.toString());
			Object result = selectQuery.uniqueResult();
			if (result == null)
			{
				return;
			}
			int answerSetId = ConversionTools.getValue(result);
			query = new StringBuilder();
			
			query.append("DELETE FROM ");
			query.append(getOLAPTableName(publishedSurvey, surveyUID));
			query.append(" WHERE QANSWERSETID = ");
			query.append(answerSetId);
			
			NativeQuery deleteQuery = sessionReporting.createSQLQuery(query.toString());
			deleteQuery.executeUpdate();
			
			//also remove from additional tables
			int counter = 1;
			while (OLAPTableExistsInternal(surveyUID + "_" + counter, !publishedSurvey))
			{
				query = new StringBuilder();
				query.append("DELETE FROM ");
				query.append(getOLAPTableName(publishedSurvey, surveyUID) + "_" + counter);
				query.append(" WHERE QANSWERSETID = ");
				query.append(answerSetId);
				
				deleteQuery = sessionReporting.createSQLQuery(query.toString());
				deleteQuery.executeUpdate();
				counter++;
			}
		}
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getCount(Survey survey) {
		return this.getCountInternal(survey.getIsDraft(), survey.getUniqueId());
	}

	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getCountInternal(boolean isDraft, String surveyUid) {
		return this.getCountInternal(isDraft, surveyUid, null, null);
	}

	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getCountInternal(boolean isDraft, String surveyUid, String where, Map<String, Object> values) {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();

		if (!this.OLAPTableExistsInternal(isDraft, surveyUid)) {
			return 0;
		}

		String sql = "SELECT COUNT(*) FROM " + getOLAPTableName(!isDraft, surveyUid);
		if (where != null) {
			sql += where;
		}

		NativeQuery query = sessionReporting.createSQLQuery(sql);

		if (where != null && values != null && !values.isEmpty()) {
			for (String attrib : values.keySet()) {
				Object value = values.get(attrib);
				if (value instanceof String) {
					query.setString(attrib, (String) values.get(attrib));
				} else if (value instanceof Integer) {
					query.setInteger(attrib, (Integer) values.get(attrib));
				} else if (value instanceof Date) {
					query.setTimestamp(attrib, (Date) values.get(attrib));
				}
			}
		}

		return ConversionTools.getValue(query.uniqueResult());
	}

	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getCountInternal(Survey survey) {
		return this.getCountInternal(survey.getIsDraft(), survey.getUniqueId());
	}

	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getCountInternal(Survey survey, String where, Map<String, Object> values) {
		return this.getCountInternal(survey.getIsDraft(), survey.getUniqueId(), where, values);
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getCountInternal(Survey survey, String quid, String auid, boolean noPrefixSearch, boolean noPostfixSearch, boolean noUUIDs, String where, Map<String, Object> values) {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		String sql = "SELECT COUNT(*) FROM " + getOLAPTableName(survey) + " WHERE Q" + quid.replace("-", "");
		if (auid == null) {
			sql += " IS NOT NULL";
		} else if (noPrefixSearch && noPostfixSearch) {
			sql += " LIKE '" + auid + "'";			
		} else if (noPrefixSearch) {
			sql += " LIKE '" + auid + "%'";
		} else {
			sql += " LIKE '%" + auid + "%'";
		}
		
		if (noUUIDs) {
			sql += " AND Q" + quid.replace("-", "") + " NOT LIKE '%-%'";			
		}
		
		if (where != null)
		{
			sql += " AND QANSWERSETID IN (SELECT QANSWERSETID FROM " + getOLAPTableName(survey) + " " + where + ")";
		}
		
		NativeQuery query = sessionReporting.createSQLQuery(sql);
		
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
	public int getAnswerSetsByQuestionUIDInternal(Survey survey, String quid, Map<Integer, Set<String>> answersByAnswerSetID, String where, Map<String, Object> values) {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		String sql = "SELECT QANSWERSETID, Q" + quid.replace("-", "") + " FROM " + getOLAPTableName(survey);

		if (where != null) {
			sql += where;
		}

		sql += " ORDER BY QCREATED DESC";

		NativeQuery query = sessionReporting.createSQLQuery(sql);

		if (where != null && values != null && !values.isEmpty()) {
			for (String attrib : values.keySet()) {
				Object value = values.get(attrib);
				if (value instanceof String) {
					query.setString(attrib, (String) values.get(attrib));
				} else if (value instanceof Integer) {
					query.setInteger(attrib, (Integer) values.get(attrib));
				} else if (value instanceof Date) {
					query.setTimestamp(attrib, (Date) values.get(attrib));
				}
			}
		}

		int length = 0;
		for (Object row : query.list()) {
			if (!(row instanceof Object[])) {
				continue;
			}
			Object[] rowEntrys = (Object[])row;
			int answerSetID= ConversionTools.getValue(rowEntrys[0]);
			String answerUIDsStringly = (String)rowEntrys[1];
			if (answerUIDsStringly == null) {
				continue;
			}
			String[] answerUIDsA = answerUIDsStringly.split(";");
			Set<String> answerUIDs = new HashSet<>(Arrays.asList(answerUIDsA));
			answersByAnswerSetID.put(answerSetID, answerUIDs);
			length++;
		}
		return length;
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public List<String> getAnswersByQuestionUIDInternal(Survey survey, String quid, String where, Map<String, Object> values) {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		String sql = "SELECT Q" + quid.replace("-", "") + " FROM " + getOLAPTableName(survey);
		
		if (where != null) {
			sql += where;
		}

		NativeQuery query = sessionReporting.createSQLQuery(sql);

		if (where != null && values != null && !values.isEmpty()) {
			for (String attrib : values.keySet()) {
				Object value = values.get(attrib);
				if (value instanceof String) {
					query.setString(attrib, (String) values.get(attrib));
				} else if (value instanceof Integer) {
					query.setInteger(attrib, (Integer) values.get(attrib));
				} else if (value instanceof Double) {
					query.setDouble(attrib, (Double) values.get(attrib));
				} else if (value instanceof Date) {
					query.setTimestamp(attrib, (Date) values.get(attrib));
				}
			}
		}
		
		List<String> result = new ArrayList<>();

		for (Object answer : query.list()) {	
			if (answer != null) {
				result.add(answer.toString());
			}
		}
		return result;
	}

	private String shrink(String input) {
		if (input == null || input.length() < 5001) return input;
		return input.substring(0, 5000);
	}

	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	public void addToDoInternal(ToDo todo, String uid, String code) { 
		this.addToDoInternal(todo, uid, code, false);
	}

	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	public void addToDoInternal(ToDo todo, String uid, String code, boolean executeTodoSynchronously) {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		//check if table exists
		try {
			NativeQuery querytodoexists = sessionReporting.createSQLQuery("SELECT 1 FROM TODO LIMIT 1");
			querytodoexists.uniqueResult();
			//table exists
		} catch (Exception e) {
			//table does not exist: create it:
			NativeQuery querycreate = sessionReporting.createSQLQuery("CREATE TABLE TODO (ID INT NOT NULL AUTO_INCREMENT, TYPE INT, UID VARCHAR(36), CODE VARCHAR(36), PRIMARY KEY (ID))");
			querycreate.executeUpdate();
			
			NativeQuery queryindex = sessionReporting.createSQLQuery("CREATE UNIQUE INDEX IDXUNIQUE ON TODO (TYPE, UID, CODE)");
			queryindex.executeUpdate();
		}

		boolean similarEntryPresent = false;
		
		if (todo == ToDo.NEWCONTRIBUTION || todo == ToDo.NEWTESTCONTRIBUTION) {		
			//check if there is a similar entry
			NativeQuery querytodoexists = sessionReporting.createSQLQuery("SELECT ID FROM TODO WHERE TYPE = :type AND UID = :uid LIMIT 1");
			querytodoexists.setInteger("type", todo.getValue());
			querytodoexists.setString("uid", uid);
			@SuppressWarnings("rawtypes")
			List results = querytodoexists.list();
			
			if (!results.isEmpty()) {
				similarEntryPresent = true;
			}
		}

		if (!similarEntryPresent) {
			NativeQuery queryinsert = sessionReporting.createSQLQuery("INSERT INTO TODO (ID, TYPE, UID, CODE) VALUES (null, :type, :uid, :code)");
			queryinsert.setInteger("type", todo.getValue());
			queryinsert.setString("uid", uid);
			queryinsert.setString("code", code);
			try {
				queryinsert.executeUpdate();
			} catch (ConstraintViolationException ce) {
				//this means there is already the same entry in the table: ignore
				return;
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
				return;
			}
		}
		if (executeTodoSynchronously) {
			ToDoItem todoItem = new ToDoItem(-1, todo.getValue(), uid, code);
			try {
				this.executeToDoInternal(todoItem, true);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public List<ToDoItem> getToDosInternal(int page, int rowsPerPage) {
		List<ToDoItem> todos = new ArrayList<>();
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		//check if TODO table exists
		try {
			NativeQuery querytodoexists = sessionReporting.createSQLQuery("SELECT 1 FROM TODO LIMIT 1");
			querytodoexists.uniqueResult();
			//table exists
		} catch (Exception e) {
			return todos;
		}		
	
		NativeQuery query = sessionReporting.createSQLQuery("SELECT ID, TYPE, UID, CODE FROM TODO ORDER BY ID ASC");
		
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
	public ToDoItem getToDoInternal(int id) {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		NativeQuery query = sessionReporting.createSQLQuery("SELECT ID, TYPE, UID, CODE FROM TODO WHERE ID = :id");
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
	public void executeToDoInternal(ToDoItem todo, boolean removeSimilar) throws Exception
	{
		switch (todo.Type) {
			case NEWSURVEY:
				createOLAPTableInternal(todo.UID, false, true);
				break;
			case NEWCONTRIBUTION:
			case CHANGEDCONTRIBUTION:
				try {
					updateOLAPTableInternal(todo.UID, false, true);
				} catch (SQLGrammarException e) {
					// Hibernate 4
					if (this.validateOLAPTablesInternal(todo.UID, false)) {
						throw e;
					} else {
						deleteOLAPTableInternal(todo.UID, false, true);
						createOLAPTableInternal(todo.UID, false, true);

						// retry
						updateOLAPTableInternal(todo.UID, false, true);
					}
				} catch (PersistenceException e) {
					// Hibernate 5
					if (this.validateOLAPTablesInternal(todo.UID, false)) {
						throw e;
					} else {
						deleteOLAPTableInternal(todo.UID, false, true);
						createOLAPTableInternal(todo.UID, false, true);

						// retry
						updateOLAPTableInternal(todo.UID, false, true);
					}
				}
				break;
			case DELETEDCONTRIBUTION:
				try {
					removeFromOLAPTableInternal(todo.UID, todo.Code, true);
				} catch (SQLGrammarException e) {
					// Hibernate 4
					if (this.validateOLAPTablesInternal(todo.UID, false)) {
						throw e;
					} else {
						deleteOLAPTableInternal(todo.UID, false, true);
						createOLAPTableInternal(todo.UID, false, true);

						// retry
						removeFromOLAPTableInternal(todo.UID, todo.Code, true);
					}
				} catch (PersistenceException e) {
					// Hibernate 5
					if (this.validateOLAPTablesInternal(todo.UID, false)) {
						throw e;
					} else {
						deleteOLAPTableInternal(todo.UID, false, true);
						createOLAPTableInternal(todo.UID, false, true);

						// retry
						removeFromOLAPTableInternal(todo.UID, todo.Code, true);
					}
				}
				break;
			case CHANGEDSURVEY:
				deleteOLAPTableInternal(todo.UID, false, true);
				createOLAPTableInternal(todo.UID, false, true);
				break;
			case DELETEDSURVEY:
				deleteOLAPTableInternal(todo.UID, true, true);
				break;
			case CHANGEDDRAFTSURVEY:
				deleteOLAPTableInternal(todo.UID, true, false);
				createOLAPTableInternal(todo.UID, true, false);
				break;
			case NEWTESTCONTRIBUTION:
			case CHANGEDTESTCONTRIBUTION:
				try {
					updateOLAPTableInternal(todo.UID, true, false);
				} catch (SQLGrammarException e) {
					// Hibernate 4
					if (this.validateOLAPTablesInternal(todo.UID, true)) {
						throw e;
					} else {
						deleteOLAPTableInternal(todo.UID, true, false);
						createOLAPTableInternal(todo.UID, true, false);

						// retry
						updateOLAPTableInternal(todo.UID, true, false);
					}
				} catch (PersistenceException e) {
					// Hibernate 5
					if (this.validateOLAPTablesInternal(todo.UID, true)) {
						throw e;
					} else {
						deleteOLAPTableInternal(todo.UID, true, false);
						createOLAPTableInternal(todo.UID, true, false);

						// retry
						updateOLAPTableInternal(todo.UID, true, false);
					}
				}
				break;
			case DELETEDTESTCONTRIBUTION:
				try {
					removeFromOLAPTableInternal(todo.UID, todo.Code, false);
				} catch (SQLGrammarException e) {
					// Hibernate 4
					if (this.validateOLAPTablesInternal(todo.UID, true)) {
						throw e;
					} else {
						deleteOLAPTableInternal(todo.UID, true, false);
						createOLAPTableInternal(todo.UID, true, false);
						// retry
						removeFromOLAPTableInternal(todo.UID, todo.Code, false);
					}
				} catch (PersistenceException e) {
					// Hibernate 5
					if (this.validateOLAPTablesInternal(todo.UID, true)) {
						throw e;
					} else {
						deleteOLAPTableInternal(todo.UID, true, false);
						createOLAPTableInternal(todo.UID, true, false);
						// retry
						removeFromOLAPTableInternal(todo.UID, todo.Code, false);
					}
				}
				break;
		}
		
		removeToDoInternal(todo, removeSimilar);
	}
		
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getNumberOfToDosInternal() {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		NativeQuery query = sessionReporting.createSQLQuery("SELECT COUNT(*) FROM TODO");
		
		try {		
			return ConversionTools.getValue(query.uniqueResult());
		} catch (Exception e) {
			return 0;
		}		
	}
	
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public int getNumberOfTablesInternal()
	{
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		NativeQuery query = sessionReporting.createSQLQuery("SELECT count(*) AS totalTables FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = database() AND TABLE_NAME != 'todo' AND NOT TABLE_NAME LIKE '%\\_%';");
		
		try {		 
			return ConversionTools.getValue(query.uniqueResult());
		} catch (Exception e) {
			return 0;
		}		
	}
	
	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	public void removeToDoInternal(ToDoItem todo, boolean includesimilar) {
		if (includesimilar)
		{
			Session sessionReporting = sessionFactoryReporting.getCurrentSession();
			String sql = "DELETE FROM TODO WHERE Type = :type AND UID = :uid";
			if (todo.Code == null) {
				sql += " AND Code IS NULL";
			} else {
				sql += " AND Code = :code";
			}
			NativeQuery query = sessionReporting.createSQLQuery(sql);
			query.setInteger("type", todo.Type.value);
			query.setString("uid", todo.UID);
			if (todo.Code != null) {
				query.setString("code", todo.Code);
			}
			query.executeUpdate();
		} else {
			List<ToDoItem> list = new ArrayList<>();
			list.add(todo);
			removeToDosInternal(list);
		}
	}
	
	private void removeToDosInternal(List<ToDoItem> todos) {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		List<Integer> ids = new ArrayList<>();
		for (ToDoItem todo : todos)
		{
			ids.add(todo.Id);
		}
		
		NativeQuery queryremove = sessionReporting.createSQLQuery("DELETE FROM TODO WHERE ID IN (:ids)");
		queryremove.setParameterList("ids", ids);
		queryremove.executeUpdate();
	}
	
	@Transactional(readOnly = false, transactionManager = "transactionManagerReporting")
	public void removeAllToDosInternal() {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		List<ToDoItem> todos = getToDosInternal(-1,-1);
		if (todos.isEmpty()) {
			return;
		}
		
		NativeQuery queryremove = sessionReporting.createSQLQuery("DELETE FROM TODO");
		queryremove.executeUpdate();
	}
		
	@Transactional(readOnly = true, transactionManager = "transactionManagerReporting")
	public Date getLastUpdateInternal(Survey survey) {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		NativeQuery query = sessionReporting.createSQLQuery("SELECT COUNT(*) FROM TODO WHERE UID = :uid");
		query.setString("uid", survey.getUniqueId());
		int result = ConversionTools.getValue(query.uniqueResult());
		
		if (result == 0) return null;
		
		query = sessionReporting.createSQLQuery("SELECT UPDATE_TIME FROM information_schema.TABLES WHERE TABLE_NAME = :name AND TABLE_SCHEMA = database()");
		query.setString("name", getOLAPTableName(survey));
		
		return  (Date) query.uniqueResult();
	}
	
	@Transactional(transactionManager = "transactionManagerReporting")
	public int clearAnswersForQuestionInReportingDatabase(Survey survey, ResultFilter filter, String questionUID, String childUID) throws Exception {
		Session sessionReporting = sessionFactoryReporting.getCurrentSession();
		
		if (!OLAPTableExistsInternal(survey.getUniqueId(), survey.getIsDraft()))
		{
			return 0;
		}
		 
		String column = questionUID;
		
		if (childUID != null)
		{
			column = Tools.md5hash(questionUID + childUID); 
		}
		
		String sql = "UPDATE " + getOLAPTableName(survey) + " SET Q" + column.replace("-", "") + " = NULL";
		
		Map<String, Object> values = new HashMap<>();
		
		if (!filter.isEmpty()) {
		
			String where = getWhereClause(filter, values, survey);
			
			if (where.length() > 10)
			{
				sql += where;
			}		
		}
		
		Query query = sessionReporting.createSQLQuery(sql);
		sqlQueryService.setParameters(query, values);
		
		return query.executeUpdate();		
	}

	
}
