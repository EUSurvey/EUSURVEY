package com.ec.survey.tools.export;

import com.ec.survey.model.Answer;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Export;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.exception.TooManyFiltersException;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("xmlExportCreator")
@Scope("prototype")
public class XmlExportCreator extends ExportCreator {
	
	@Resource(name="administrationService")
	private AdministrationService administrationService;

	private Map<Integer, String> exportedUniqueCodes = new HashMap<>();
	private Map<Integer, String> exportedQuestionsByAnswerId = new HashMap<>();
	
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH);
	private Date exportedNow = null;

	@Override
	void ExportCharts() throws Exception {}

	@Override
	@Transactional
	public void ExportContent(boolean sync) throws Exception {
		ExportContent(sync, export);
	}
		
	@Transactional
	public void ExportContent(boolean sync, Export export) throws Exception {
		exportedUniqueCodes.clear();
		XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");
		
		writer.writeStartDocument("UTF-8", "1.0");
		writer.writeCharacters("\n");
		writer.writeStartElement("Results");
		
		exportedNow = new Date();
		
		writer.writeAttribute("create", df.format(exportedNow));
		
		writer.writeStartElement("Survey");
		
		writer.writeAttribute("id", form.getSurvey().getId().toString());
		writer.writeAttribute("alias", form.getSurvey().getShortname());
		
		Map<String, List<Element>> questionlists = new HashMap<>();
		
		Session session;
		session = sessionFactory.getCurrentSession();
				
		if (export != null)
		{
			session.evict(export.getSurvey());
		}
		
		if (form.getSurvey().getTranslations() == null)
		{
			List<String> translations = translationService.getTranslationLanguagesForSurvey(form.getSurvey().getId());
			form.getSurvey().setTranslations(translations);
		}
				
		for (String lang : form.getSurvey().getTranslations()) {
			writer.writeStartElement("Elements");
			writer.writeAttribute("lang", lang);			
			
			Survey survey = surveyService.getSurvey(form.getSurvey().getId(), lang);
			//session.evict(survey);
			
			List<Element> questionslist = survey.getQuestionsAndSections();
			questionlists.put(lang, questionslist);
			
			for (Element question : questionslist) {	        			
				if (question instanceof Matrix) {
					Matrix matrix = (Matrix)question;
					
					writer.writeStartElement("MatrixTitle");
					writer.writeAttribute("id", question.getUniqueId());
									
					if (export != null && export.getShowShortnames())
					{
						writer.writeAttribute("bid", matrix.getShortname());
					}
				
					writer.writeCharacters(matrix.getTitle());
					writer.writeEndElement(); //MatrixTitle
					
					for(Element matrixQuestion: matrix.getQuestions()) {
						writer.writeStartElement("MatrixQuestion");
						writer.writeAttribute("id", matrixQuestion.getUniqueId());
						
						if (export != null && export.getShowShortnames())
						{
							writer.writeAttribute("bid", matrixQuestion.getShortname());
						}
						
						writer.writeCharacters(matrixQuestion.getTitle());
												
						writer.writeEndElement(); //MatrixQuestion
					}
					for(Element matrixAnswer: matrix.getAnswers()) {
						writer.writeStartElement("MatrixAnswer");
						writer.writeAttribute("id", matrixAnswer.getUniqueId());
						
						if (export != null && export.getShowShortnames())
						{
							writer.writeAttribute("bid", matrixAnswer.getShortname());
						}
						
						writer.writeCharacters(matrixAnswer.getTitle());
						
						writer.writeEndElement(); //MatrixAnswer
					}
				} else if (question instanceof Table) {	
					Table table = (Table)question;
					
					writer.writeStartElement("TableTitle");
					writer.writeAttribute("id", question.getUniqueId());
									
					if (export != null && export.getShowShortnames())
					{
						writer.writeAttribute("bid", table.getShortname());
					}
				
					writer.writeCharacters(table.getTitle());
					writer.writeEndElement(); //TableTitle
					
					for(Element tableQuestion: table.getQuestions()) {
						writer.writeStartElement("TableQuestion");
						writer.writeAttribute("id", tableQuestion.getUniqueId());
						
						if (export != null && export.getShowShortnames())
						{
							writer.writeAttribute("bid", tableQuestion.getShortname());
						}
						
						writer.writeCharacters(tableQuestion.getTitle());
						
						writer.writeEndElement(); //TableQuestion
					}
					for(Element tableAnswer: table.getAnswers()) {	
						writer.writeStartElement("TableAnswer");
						writer.writeAttribute("id", tableAnswer.getUniqueId());
						
						if (export != null && export.getShowShortnames())
						{
							writer.writeAttribute("bid", tableAnswer.getShortname());
						}
						
						writer.writeCharacters(tableAnswer.getTitle());
						
						writer.writeEndElement(); //TableAnswer
					}
				} else if (question instanceof Text) {	
					writer.writeStartElement("Text");
					writer.writeAttribute("id", question.getUniqueId());
									
					if (export != null && export.getShowShortnames())
					{
						writer.writeAttribute("bid", question.getShortname());
					}
					
					writer.writeCharacters(question.getTitle());
					
					writer.writeEndElement(); //Text
				} else if (question instanceof Image) {	
					writer.writeStartElement("Image");
					writer.writeAttribute("id", question.getUniqueId());
									
					if (export != null && export.getShowShortnames())
					{
						writer.writeAttribute("bid", question.getShortname());
					}
					
					writer.writeCharacters(question.getTitle());
					
					writer.writeEndElement(); //Image
				} else if (question instanceof Section) {	
					writer.writeStartElement("Section");
					writer.writeAttribute("id", question.getUniqueId());
									
					if (export != null && export.getShowShortnames())
					{
						writer.writeAttribute("bid", question.getShortname());
					}
					
					writer.writeCharacters(question.getTitle());
					
					writer.writeEndElement(); //Section
				} else {			
					writer.writeStartElement("Question");
					writer.writeAttribute("id", question.getUniqueId());
									
					if (export != null && export.getShowShortnames())
					{
						writer.writeAttribute("bid", question.getShortname());
					}
					
					writer.writeCharacters(question.getTitle());
					
					writer.writeEndElement(); //Question
					
					if (question instanceof ChoiceQuestion)
					{
						ChoiceQuestion choice = (ChoiceQuestion)question;
						
						for (PossibleAnswer answer: choice.getPossibleAnswers())
						{
							writer.writeStartElement("Answer");
							writer.writeAttribute("id", answer.getUniqueId());
											
							if (export != null && export.getShowShortnames())
							{
								writer.writeAttribute("bid", answer.getShortname());
							}
							
							writer.writeCharacters(answer.getTitle());
							
							writer.writeEndElement(); //Answer
						}
					} else if (question instanceof RatingQuestion)
					{
						RatingQuestion rating = (RatingQuestion)question;
						
						for(Element childQuestion: rating.getQuestions()) {
							writer.writeStartElement("RatingQuestion");
							writer.writeAttribute("id", childQuestion.getUniqueId());
							
							if (export != null && export.getShowShortnames())
							{
								writer.writeAttribute("bid", childQuestion.getShortname());
							}
							
							writer.writeCharacters(rating.getTitle() + ": "+ childQuestion.getTitle());
													
							writer.writeEndElement(); //MatrixQuestion
						}
					}
					
				}
			}
			writer.writeEndElement(); //elements
		}
		
		writer.writeEndElement(); //survey
		
		List<File> uploadedFiles = answerService.getAllUploadedFiles(form.getSurvey().getId(), export == null ? null : export.getResultFilter(), 1, Integer.MAX_VALUE);
		Map<Integer, List<File>> filesByAnswer = new HashMap<>();
		for (File file : uploadedFiles) {
			if (file.getAnswerId() != null)
			{
				if (!filesByAnswer.containsKey(file.getAnswerId()))
				{
					filesByAnswer.put(file.getAnswerId(), new ArrayList<>());
				}
				filesByAnswer.get(file.getAnswerId()).add(file);
			}
		}
		
		HashMap<String, Object> values = new HashMap<>();
		
		Map<String, String> ECASUserLoginsByEmail = null;
		
		if (export.getAddMeta())
		{
			ECASUserLoginsByEmail = administrationService.getECASUserLoginsByEmail();
		};		
	
		String sql = "select ans.ANSWER_SET_ID, a.QUESTION_ID, a.QUESTION_UID, a.VALUE, a.ANSWER_COL, a.ANSWER_ID, a.ANSWER_ROW, a.PA_ID, a.PA_UID, ans.UNIQUECODE, ans.ANSWER_SET_DATE, ans.ANSWER_SET_UPDATE, ans.ANSWER_SET_INVID, ans.RESPONDER_EMAIL, ans.ANSWER_SET_LANG, a.AS_ID, ans.SCORE FROM ANSWERS a RIGHT JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID IN (" + answerService.getSql(null, form.getSurvey().getId(), export == null ? null : export.getResultFilter(), values, false, true)+ ") ORDER BY ans.ANSWER_SET_ID";
		
		SQLQuery query = session.createSQLQuery(sql);
		
		query.setReadOnly(true);
				
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
		
		query.setFetchSize(Integer.MIN_VALUE);
		ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
		
		int lastAnswerSet = 0;
		AnswerSet answerSet = new AnswerSet();
		answerSet.setSurvey(form.getSurvey());
		
		writer.writeStartElement("Answers");
		
		Map<String, List<File>> uploadedFilesByQuestionUID = new HashMap<>();
		
		String list = "";
		
		while (results.next()) 
		{
			Object[] a = results.get();
			Answer answer = new Answer();
			answer.setAnswerSetId(ConversionTools.getValue(a[0]));
			answer.setQuestionId(ConversionTools.getValue(a[1]));
			answer.setQuestionUniqueId((String) a[2]);
			answer.setValue((String) a[3]);
			answer.setColumn(ConversionTools.getValue(a[4]));
			answer.setId(ConversionTools.getValue(a[5]));
			answer.setRow(ConversionTools.getValue(a[6]));
			answer.setPossibleAnswerId(ConversionTools.getValue(a[7]));
			answer.setPossibleAnswerUniqueId((String) a[8]);
			
			exportedQuestionsByAnswerId.put(answer.getId(), answer.getQuestionUniqueId());
			
			if (lastAnswerSet == answer.getAnswerSetId())
			{
				answerSet.addAnswer(answer);
			} else {
				if (lastAnswerSet > 0)
				{
					parseAnswerSet(writer, questionlists.get(answerSet.getLanguageCode()), answerSet, list, filesByAnswer, uploadedFilesByQuestionUID, export.getAddMeta(), ECASUserLoginsByEmail);
				}
				
				answerSet = new AnswerSet();
				answerSet.setSurvey(form.getSurvey());
				answerSet.setId(answer.getAnswerSetId());
				lastAnswerSet = answer.getAnswerSetId();
				answerSet.getAnswers().add(answer);
				answerSet.setUniqueCode((String) a[9]);
				answerSet.setDate((Date) a[10]);
				answerSet.setUpdateDate((Date) a[11]);
				answerSet.setInvitationId((String) a[12]);
				answerSet.setResponderEmail((String) a[13]);
				answerSet.setLanguageCode((String) a[14]);
				Integer ilist = ConversionTools.getValue(a[15]);
				list = ilist.toString();
				
				if (answerSet.getLanguageCode() == null || !questionlists.containsKey(answerSet.getLanguageCode()))
				{
					answerSet.setLanguageCode(questionlists.keySet().toArray()[0].toString());
				}
				
				answerSet.setScore(ConversionTools.getValue(a[16]));
				
			}	
		}
		if (lastAnswerSet > 0) parseAnswerSet(writer, questionlists.get(answerSet.getLanguageCode()), answerSet, list, filesByAnswer, uploadedFilesByQuestionUID, export.getAddMeta(), ECASUserLoginsByEmail);
		results.close();
		
		writer.writeEndElement(); //Answers
		writer.writeEndElement(); //Results
		
		if (uploadedFiles.size() > 0)
		{
			//there are multiple files
			java.io.File temp = new java.io.File(exportFilePath + ".zip"); 
			final OutputStream out = new FileOutputStream(temp);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);
			
			os.putArchiveEntry(new ZipArchiveEntry(FilenameUtils.getName(exportFilePath)));
		    IOUtils.copy(new FileInputStream(exportFilePath), os);
		    os.closeArchiveEntry();
		    
			for (String questionUID: uploadedFilesByQuestionUID.keySet())
			{
				for (File file: uploadedFilesByQuestionUID.get(questionUID))
		    	{
					java.io.File f = new java.io.File(exportService.getFileDir() + file.getUid());
		    		
					if (f.exists())
					{
			    		os.putArchiveEntry(new ZipArchiveEntry(questionUID + "/" + file.getUid() + "_" + file.getName()));
					    IOUtils.copy(new FileInputStream(f), os);
					    os.closeArchiveEntry();	
					}
		    	}
			}
					    
		    os.close();
		    		
		    if (export != null) export.setZipped(true);
		} else {
			 if (export != null) export.setZipped(false);
		}
	}
	
	@Transactional
	public void SimulateExportContent(boolean sync, Export export) throws TooManyFiltersException {
		exportedUniqueCodes.clear();
		
		Session session;
		session = sessionFactory.getCurrentSession();
				
		if (export != null)
		{
			session.evict(export.getSurvey());
		}
		
		exportedNow = new Date();
				
		HashMap<String, Object> values = new HashMap<>();
		String sql = "select ans.ANSWER_SET_ID, a.QUESTION_ID, a.QUESTION_UID, a.VALUE, a.ANSWER_COL, a.ANSWER_ID, a.ANSWER_ROW, a.PA_ID, a.PA_UID, ans.UNIQUECODE, ans.ANSWER_SET_DATE, ans.ANSWER_SET_UPDATE, ans.ANSWER_SET_INVID, ans.RESPONDER_EMAIL, ans.ANSWER_SET_LANG FROM ANSWERS a RIGHT JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID IN (" + answerService.getSql(null, form.getSurvey().getId(), export == null ? null : export.getResultFilter(), values, false, true)+ ") ORDER BY ans.ANSWER_SET_ID";
		
		SQLQuery query = session.createSQLQuery(sql);
		
		query.setReadOnly(true);
				
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
		
		query.setFetchSize(Integer.MIN_VALUE);
		ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
		
		while (results.next()) 
		{
			Object[] a = results.get();
			exportedQuestionsByAnswerId.put(ConversionTools.getValue(a[5]), (String)(a[2]));
			
			if (!exportedUniqueCodes.containsKey(ConversionTools.getValue(a[0])))
			{
				exportedUniqueCodes.put(ConversionTools.getValue(a[0]), (String) a[9]);
			}	
		}
		results.close();
	}
	
	void parseAnswerSet(XMLStreamWriter writer, List<Element> questions, AnswerSet answerSet, String list, Map<Integer, List<File>> filesByAnswer, Map<String, List<File>> uploadedFilesByQuestionUID, boolean meta, Map<String, String> ECASUserLoginsByEmail) throws XMLStreamException
	{
		exportedUniqueCodes.put(answerSet.getId(), answerSet.getUniqueCode());
		
		writer.writeStartElement("AnswerSet");
		
		if (answerSet.getSurvey().getSecurity().contains("anonymous"))
		{
			//as.setAttribute("id", "Anonymous");
			writer.writeAttribute("id", "Anonymous");
		} else {
			writer.writeAttribute("id", answerSet.getUniqueCode());
		}
		
		writer.writeAttribute("create", df.format(answerSet.getDate()));
		writer.writeAttribute("list", list);
		writer.writeAttribute("last", df.format(answerSet.getUpdateDate()));
		writer.writeAttribute("lang", answerSet.getLanguageCode());
		
		if (meta)
		{
			writer.writeAttribute("user", answerSet.getResponderEmail() != null ? answerSet.getResponderEmail() : "" );
			writer.writeAttribute("invitation", answerSet.getInvitationId()  != null ? answerSet.getInvitationId() : "");
			if (answerSet.getSurvey().getIsOPC() && answerSet.getResponderEmail() != null && answerSet.getResponderEmail().contains("@"))
			{
				try {
					if (ECASUserLoginsByEmail != null && ECASUserLoginsByEmail.containsKey(answerSet.getResponderEmail()))
					{
						writer.writeAttribute("userlogin", ECASUserLoginsByEmail.get(answerSet.getResponderEmail()) );
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
		}
		
		if (answerSet.getSurvey().getIsQuiz())
		{
			writer.writeAttribute("totalscore", answerSet.getScore() != null ? answerSet.getScore().toString() : "0");
		}

		for (Element question : questions) {
			
			if (question instanceof Matrix) {
				Matrix matrix = (Matrix)question;
				for(Element matrixQuestion: matrix.getQuestions()) {
					List<Answer> answers = answerSet.getAnswers(matrixQuestion.getId(), matrixQuestion.getUniqueId());
	
					for (Answer answer : answers) {
						writer.writeStartElement("Answer");
						writer.writeAttribute("aid", answer.getPossibleAnswerUniqueId() != null ? answer.getPossibleAnswerUniqueId() : "");
						writer.writeAttribute("qid", matrixQuestion.getUniqueId());
						writer.writeEndElement(); //Answer
					}
				}
			} else if (question instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion)question;
				for(Element childQuestion: rating.getQuestions()) {
					List<Answer> answers = answerSet.getAnswers(childQuestion.getId(), childQuestion.getUniqueId());
	
					if (answers.size() > 0) {
						writer.writeStartElement("Answer");
						writer.writeAttribute("qid", childQuestion.getUniqueId());
						
						writer.writeCharacters(answers.get(0).getValue());
						
						writer.writeEndElement(); //Answer
					}
				}
			} else if (question instanceof Table) {
				Table table = (Table)question;
				
				for (int tableRow = 1; tableRow < table.getRows(); tableRow++) {
					for (int tableCol = 1; tableCol < table.getColumns(); tableCol++) {
						String answer = answerSet.getTableAnswer(table, tableRow, tableCol, false);
						
						Element tq = table.getQuestions().get(tableRow-1);
						Element ta = table.getAnswers().get(tableCol-1);
												
						writer.writeStartElement("Answer");
						writer.writeAttribute("qid", tq.getUniqueId());
						writer.writeAttribute("aid", ta.getUniqueId());
						
						if (answer != null && answer.length() > 0)
						{
							writer.writeCharacters(answer);
						}
						writer.writeEndElement(); //Answer
					}
				}
			} else if (question instanceof Question) {
				List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());

				for (Answer answer : answers) {
					writer.writeStartElement("Answer");
					
					writer.writeAttribute("qid", question.getUniqueId());
										
					if (question instanceof ChoiceQuestion)
					{
						writer.writeAttribute("aid", answer.getPossibleAnswerUniqueId() != null ? answer.getPossibleAnswerUniqueId() : "");
					} else if (question instanceof Upload)
					{
						StringBuilder text = new StringBuilder();
						if (filesByAnswer.containsKey(answer.getId()))
						{								
							for (File file: filesByAnswer.get(answer.getId()))
							{
								if (!uploadedFilesByQuestionUID.containsKey(question.getUniqueId()))
								{
									uploadedFilesByQuestionUID.put(question.getUniqueId(), new ArrayList<>());
								}
								uploadedFilesByQuestionUID.get(question.getUniqueId()).add(file);
								
								text.append((text.length() > 0) ? ";" : "").append(file.getName());
							}
						}
						writer.writeCharacters(text.toString());
					} else {
						writer.writeCharacters(form.getAnswerTitleStripInvalidXML(answer));
					}
					
					writer.writeEndElement(); //Answer
				}
			}
		}
		writer.writeEndElement(); //AnswerSet
	}
	
	@Override
	void ExportStatistics() throws Exception {}
	
	@Override
	void ExportStatisticsQuiz() throws Exception {}

	@Override
	void ExportAddressBook() throws Exception {}

	public Date getExportedNow() {
		return exportedNow;
	}
	public void setExportedNow(Date exportedNow) {
		this.exportedNow = exportedNow;
	}
	
	public Map<Integer, String> getExportedUniqueCodes() {
		return exportedUniqueCodes;
	}
	public void setExportedUniqueCodes(Map<Integer, String> exportedUniqueCodes) {
		this.exportedUniqueCodes = exportedUniqueCodes;
	}
	
	public Map<Integer, String> getExportedQuestionsByAnswerId() {
		return exportedQuestionsByAnswerId;
	}
	public void setExportedQuestionsByAnswerId(Map<Integer, String> exportedQuestionsByAnswerId) {
		this.exportedQuestionsByAnswerId = exportedQuestionsByAnswerId;
	}

	@Override
	void ExportActivities() throws Exception {}
	
	@Override
	void ExportTokens() throws Exception {}	
	
}
