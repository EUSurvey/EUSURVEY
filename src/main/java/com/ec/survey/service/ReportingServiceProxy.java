package com.ec.survey.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.SqlPagination;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.ReportingService.ToDo;
import com.ec.survey.service.ReportingService.ToDoItem;

@Service("reportingServiceProxy")
public class ReportingServiceProxy {
	
	protected static final Logger logger = Logger.getLogger(ReportingServiceProxy.class);

	@Resource(name = "reportingService")
	protected ReportingService reportingService;
	
	protected @Value("${enablereportingdatabase}") String enablereportingdatabase;
	
	private boolean isReportingDatabaseEnabled()
	{
		return enablereportingdatabase != null && enablereportingdatabase.equalsIgnoreCase("true");
	}
	
	public List<List<String>> getAnswerSets(Survey survey, ResultFilter filter, SqlPagination sqlPagination, boolean addlinks, boolean forexport, boolean showuploadedfiles, boolean doNotReplaceAnswerIDs, boolean useXmlDateFormat, boolean showShortnames) throws Exception {
		if (!isReportingDatabaseEnabled()) return null;
		return reportingService.getAnswerSetsInternal(survey, filter, sqlPagination, addlinks, forexport, showuploadedfiles, doNotReplaceAnswerIDs, useXmlDateFormat, showShortnames);
	}
	
	public List<Integer> getAnswerSetIDs(Survey survey, ResultFilter filter, SqlPagination sqlPagination) throws Exception {
		if (!isReportingDatabaseEnabled()) return null;		
		return reportingService.getAnswerSetIDsInternal(survey, filter, sqlPagination);
	}
	
	public boolean OLAPTableExists(String uid, boolean draft) {
		if (!isReportingDatabaseEnabled()) return false;
		return reportingService.OLAPTableExistsInternal(uid, draft);
	}
	
	public void deleteOLAPTable(String uid, boolean draftversion, boolean publishedversion) {
		if (!isReportingDatabaseEnabled()) return;
		reportingService.deleteOLAPTableInternal(uid, draftversion, publishedversion);
	}
	
	public void createOLAPTable(String shortname, boolean draftversion, boolean publishedversion) throws Exception {
		if (!isReportingDatabaseEnabled()) return;
		reportingService.createOLAPTableInternal(shortname, draftversion, publishedversion);
	}
		
	public void updateOLAPTable(String shortname, boolean draftversion, boolean publishedversion) throws Exception {
		if (!isReportingDatabaseEnabled()) return;
		reportingService.updateOLAPTableInternal(shortname, draftversion, publishedversion);
	}

	public int getCount(boolean isDraft, String surveyUid) {
		if (!isReportingDatabaseEnabled()) return -1;
		return reportingService.getCountInternal(isDraft , surveyUid);
	}

	public int getCount(boolean isDraft, String surveyUid, String where, Map<String, Object> values) {
		if (!isReportingDatabaseEnabled()) return -1;
		return reportingService.getCountInternal(isDraft , surveyUid, where, values);
	}

	public int getCount(Survey survey) {
		if (!isReportingDatabaseEnabled()) return -1;
		return reportingService.getCountInternal(survey);
	}
	
	public int getCount(Survey survey, String where, Map<String, Object> values) {
		if (!isReportingDatabaseEnabled()) return -1;
		return reportingService.getCountInternal(survey, where, values);
	}
		
	public int getCount(Survey survey, String quid, String auid, boolean noPrefixSearch, String where, Map<String, Object> values)
	{
		if (!isReportingDatabaseEnabled()) return -1;
		return reportingService.getCountInternal(survey, quid, auid, noPrefixSearch, where, values);
	}
	
	public void addToDo(ToDo todo, String uid, String code, boolean executeTodoSync) {
		if (!isReportingDatabaseEnabled()) return;
		reportingService.addToDoInternal(todo, uid, code, executeTodoSync);
	}

	public void addToDo(ToDo todo, String uid, String code) {
		this.addToDo(todo, uid, code, false);
	}
		
	public List<ToDoItem> getToDos(int page, int rowsPerPage) {
		if (!isReportingDatabaseEnabled()) return null;
		return reportingService.getToDosInternal(page, rowsPerPage);		
	}
	
	public ToDoItem getToDo(int id) {
		if (!isReportingDatabaseEnabled()) return null;
		return reportingService.getToDoInternal(id);
	}
	
	public void executeToDo(ToDoItem todo, boolean removeSimilar) throws Exception
	{
		if (!isReportingDatabaseEnabled()) return;
		reportingService.executeToDoInternal(todo, removeSimilar);
	}
		
	public List<ToDoItem> getToDos() {
		if (!isReportingDatabaseEnabled()) return null;
		return reportingService.getToDosInternal(-1,-1);
	}
	
	public int getNumberOfToDos() {
		if (!isReportingDatabaseEnabled()) return 0;
		return reportingService.getNumberOfToDosInternal();
	}
	
	public int getNumberOfTables()
	{
		if (!isReportingDatabaseEnabled()) return 0;
		return reportingService.getNumberOfTablesInternal();
	}
	
	public void removeToDo(ToDoItem todo, boolean includesimilar) {
		if (!isReportingDatabaseEnabled()) return;
		reportingService.removeToDoInternal(todo, includesimilar);
	}
	
	public void removeAllToDos() {
		if (!isReportingDatabaseEnabled()) return;
		reportingService.removeAllToDosInternal();
	}
	
	public Date getLastUpdate(Survey survey) {
		if (!isReportingDatabaseEnabled()) return null;
		return reportingService.getLastUpdateInternal(survey);
	}
		
	public boolean validateOLAPTable(Survey survey) throws Exception {
		if (!isReportingDatabaseEnabled()) return false;
		return reportingService.validateOLAPTableInternal(survey);
	}

	public boolean validateOLAPTable(Survey survey, Integer counter) throws Exception {
		if (!isReportingDatabaseEnabled()) return false;
		return reportingService.validateOLAPTableInternal(survey, counter);
	}

	public boolean validateOLAPTables(Survey survey) throws Exception {
		if (!isReportingDatabaseEnabled()) return false;
		return reportingService.validateOLAPTablesInternal(survey);
	}

	public boolean validateOLAPTables(String surveyUID, boolean isDraft) throws Exception {
		if (!isReportingDatabaseEnabled()) return false;
		return reportingService.validateOLAPTablesInternal(surveyUID, isDraft);
	}

	public void clearAnswersForQuestionInReportingDatabase(Survey survey, ResultFilter filter, String questionUID, String childUID) throws Exception {
		if (!isReportingDatabaseEnabled()) return;
		reportingService.clearAnswersForQuestionInReportingDatabase(survey, filter, questionUID, childUID);
	}

	public void removeFromOLAPTable(String surveyUID, String answerSetUID, boolean surveyIsPublished) {
		if (!isReportingDatabaseEnabled()) return;
		reportingService.removeFromOLAPTableInternal(surveyUID, answerSetUID, surveyIsPublished);
	}

}
