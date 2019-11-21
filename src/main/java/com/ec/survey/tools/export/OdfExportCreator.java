package com.ec.survey.tools.export;

import com.ec.survey.model.*;
import com.ec.survey.model.Export.ExportFormat;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.AttributeName;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.Image;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.SqlQueryService;
import com.ec.survey.tools.ConversionTools;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.*;
import org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTableProperties;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.chart.Chart;
import org.odftoolkit.simple.chart.ChartType;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.draw.Textbox;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.text.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

@Service("odfExportCreator")
@Scope("prototype")
public class OdfExportCreator extends ExportCreator {

	@Autowired
	private SqlQueryService sqlQueryService;
	
	int CreateChart(SpreadsheetDocument spreadsheet, String title, String[] labels, double[] stats, int xPos) {
		String[] legends = new String[]{"Percent"};
		double[][] data = new double[][]{stats};	
		
		Rectangle rect = new Rectangle();
		rect.x = xPos;
		rect.y = xPos;
		rect.width = 14000;
		rect.height = 6000;
		Chart chart = spreadsheet.createChart(title, labels, legends, data, rect);
		chart.setChartType(ChartType.BAR);
		chart.setUseLegend(false);
		chart.setAxisTitle("x", "Percent");
		chart.refreshChart();
		return xPos + rect.width + 200;
	}

	@Override
	void ExportContent(boolean sync) throws Exception {
		ExportContent(null, sync);
	}	
	
	private void initHeader(Publication publication, ResultFilter filter, Export export)
	{
		String cellValue;
		for (Question question : questions) {	 
			if (publication != null || filter.exported(question.getId().toString()))
			if (publication == null || publication.isAllQuestions() || publication.isSelected(question.getId()))
			{
				if (question.IsUsedInResults())
				{	
					if (question instanceof Matrix) {
						Matrix matrix = (Matrix)question;
						for(Element matrixQuestion: matrix.getQuestions()) {
							cell = sheet.getCellByPosition(columnIndex++, rowIndex);
							
							cellValue = ConversionTools.removeHTMLNoEscape(matrix.getTitle() + ": " + matrixQuestion.getTitle());
							if (export != null && export.getShowShortnames())
							{
								cellValue += " " + matrixQuestion.getShortname();
							}							
							cell.setStringValue(cellValue);
							Font font = cell.getFont();
							font.setSize(10);
							font.setFontStyle(FontStyle.BOLD);
							cell.setFont(font);
						}
					} else if (question instanceof RatingQuestion) {
						RatingQuestion rating = (RatingQuestion)question;
						for(Element childQuestion: rating.getQuestions()) {
							cell = sheet.getCellByPosition(columnIndex++, rowIndex);
							cellValue = ConversionTools.removeHTMLNoEscape(ConversionTools.removeHTMLNoEscape(rating.getTitle() + ": "+ childQuestion.getTitle()));
							if (export != null && export.getShowShortnames())
							{
								cellValue += " (" + childQuestion.getShortname() + ")";
							}
							cell.setStringValue(cellValue);
							Font font = cell.getFont();
							font.setSize(10);
							font.setFontStyle(FontStyle.BOLD);
							cell.setFont(font);
						}
					} else if (question instanceof Table) {	
						Table table = (Table)question;
						for(Element tableQuestion: table.getQuestions()) {
							for(Element tableAnswer: table.getAnswers()) {	
								cell = sheet.getCellByPosition(columnIndex++, rowIndex);
								
								cellValue = ConversionTools.removeHTMLNoEscape(table.getTitle() + " " + tableQuestion.getTitle() + ":" + tableAnswer.getTitle());
								if (export != null && export.getShowShortnames())
								{
									cellValue = ConversionTools.removeHTMLNoEscape(table.getTitle() + " "+ tableQuestion.getTitle() + " (" + tableQuestion.getShortname() + "):" + tableAnswer.getTitle() + " (" + tableAnswer.getShortname() + ")");
								}
																
								cell.setStringValue(cellValue);
								Font font = cell.getFont();
								font.setSize(10);
								font.setFontStyle(FontStyle.BOLD);
								cell.setFont(font);
							}
						}
					} else if (question instanceof Upload && publication != null && !publication.getShowUploadedDocuments()) {
						//skip uploaded files;
					} else {			
						cell = sheet.getCellByPosition(columnIndex++, rowIndex);
						cellValue = ConversionTools.removeHTMLNoEscape(question.getTitle());
						if (export != null && export.getShowShortnames())
						{
							cellValue += " (" + question.getShortname() + ")";
						}
						cell.setStringValue(cellValue);
						Font font = cell.getFont();
						font.setSize(10);
						font.setFontStyle(FontStyle.BOLD);
						cell.setFont(font);
					}
				}
			}
		}
		
		if (publication == null && filter != null)
		{
			if (filter.exported("invitation"))
			{
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.InvitationNumber", null, "Invitation Number", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}
			if (filter.exported("case"))
			{
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.ContributionId", null, "Contribution Id", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}
			if (filter.exported("user"))
			{
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.UserName", null, "User Name", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}
			if (filter.exported("created"))
			{
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.CreationDate", null, "Creation Date", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}
			if (filter.exported("updated"))
			{	cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.LastUpdate", null, "Last Update", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}
			if (filter.exported("languages"))
			{
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.Languages", null, "Languages", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}						
		}
		
		if (form.getSurvey().getIsQuiz())
		{
			cell = sheet.getCellByPosition(columnIndex++, rowIndex);
			cell.setStringValue(resources.getMessage("label.TotalScore", null, "Total Score", locale));
			Font font = cell.getFont();
			font.setSize(10);
			font.setFontStyle(FontStyle.BOLD);
			cell.setFont(font);
		}
	}
	
	int rowIndex;
	int columnIndex;
	Cell cell;
	List<Question> questions;
	org.odftoolkit.simple.table.Table sheet;
	int fileCounter;
	SpreadsheetDocument spreadsheet;
	public void ExportContent(Publication publication, boolean sync) throws Exception {	
        spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
        sheet = spreadsheet.getSheetByIndex(0);
        sheet.setTableName("Content");
        
        fileCounter = 0;
        
        ResultFilter filter;
		
		if (publication != null && export == null)
		{
			filter = publication.getFilter();
		} else {		
			filter = export.getResultFilter();
		}
		
		Survey survey = surveyService.getSurveyInOriginalLanguage(form.getSurvey().getId(), form.getSurvey().getShortname(), form.getSurvey().getUniqueId());
		for (Element element : survey.getElements()) {
			if (element instanceof GalleryQuestion)
			{
				Hibernate.initialize(((GalleryQuestion)element).getFiles());
			}
		}
		
		if (export != null && export.isAllAnswers() && !survey.isMissingElementsChecked())
		{
			surveyService.CheckAndRecreateMissingElements(survey, filter);
			Hibernate.initialize(survey.getMissingElements());
			for (Element e : survey.getMissingElements())
			{
				if (e instanceof ChoiceQuestion)
				{
					Hibernate.initialize(((ChoiceQuestion)e).getPossibleAnswers());
				}
			}
		}
		
		form.setSurvey(survey);
    
        rowIndex = InitSpreadsheet(sheet);
        
		questions = form.getSurvey().getQuestions();
		columnIndex = 0;
		
		initHeader(publication, filter, export);
		
		Map<String, Map<String, List<File>>> uploadedFilesByCodeAndQuestionUID = new HashMap<>();
		Map<String, String> uploadQuestionNicenames = new HashMap<>();
		
		/// here starts the db stuff			
		
		List<File> uploadedFiles = new ArrayList<File>();
		if (publication == null || publication.getShowUploadedDocuments()) 
		{
			uploadedFiles = answerService.getAllUploadedFiles(form.getSurvey().getId(), filter, 1, Integer.MAX_VALUE);
		}
		
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
		
		Session session;
		Transaction t = null;
		
		if (sync)
		{
			session = sessionFactory.getCurrentSession();
		} else {
			session = sessionFactory.openSession(); 
			t = session.beginTransaction();
		}
		
		if (export != null) session.evict(export);
		session.evict(survey);
	
		HashMap<String, Object> parameters = new HashMap<>();
		
		if (publication != null)
		{
			if ( publication.isAllQuestions())
			{
				for (Element question: survey.getQuestions())
				{
					filter.getExportedQuestions().add(question.getId().toString());
				}
			} else {
				for (String question: filter.getVisibleQuestions())
				{
					filter.getExportedQuestions().add(question);
				}
			}
		}	
		
		filter.setVisibleQuestions(filter.getExportedQuestions());
		List<List<String>> answersets = reportingService.getAnswerSets(survey, filter, null, false, true, publication == null || publication.getShowUploadedDocuments(), false);
		
		if (answersets != null)
		{
			for (List<String> row : answersets)
			{
				parseAnswerSet(null, row, publication, filter, filesByAnswer, export, uploadedFilesByCodeAndQuestionUID, uploadQuestionNicenames);
			}
		} else {
	
			String sql = "select ans.ANSWER_SET_ID, a.QUESTION_ID, a.QUESTION_UID, a.VALUE, a.ANSWER_COL, a.ANSWER_ID, a.ANSWER_ROW, a.PA_ID, a.PA_UID, ans.UNIQUECODE, ans.ANSWER_SET_DATE, ans.ANSWER_SET_UPDATE, ans.ANSWER_SET_INVID, ans.RESPONDER_EMAIL, ans.ANSWER_SET_LANG, ans.SCORE FROM ANSWERS a RIGHT JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID IN (" + answerService.getSql(null, form.getSurvey().getId(), filter, parameters, false, true)+ ") ORDER BY ans.ANSWER_SET_ID";
				
			SQLQuery query = session.createSQLQuery(sql);
			
			query.setReadOnly(true);
			sqlQueryService.setParameters(query, parameters);
			
			query.setFetchSize(Integer.MIN_VALUE);
			ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
			
			int lastAnswerSet = 0;
			AnswerSet answerSet = new AnswerSet();
			answerSet.setSurvey(survey);
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
				
				if (lastAnswerSet == answer.getAnswerSetId())
				{
					answerSet.addAnswer(answer);
				} else {
					if (lastAnswerSet > 0)
					{
						parseAnswerSet(answerSet, null, publication, filter, filesByAnswer, export, uploadedFilesByCodeAndQuestionUID, uploadQuestionNicenames);
						session.flush();
					}
					
					answerSet = new AnswerSet();
					answerSet.setSurvey(survey);
					answerSet.setId(answer.getAnswerSetId());
					lastAnswerSet = answer.getAnswerSetId();
					answerSet.getAnswers().add(answer);
					answerSet.setDate((Date) a[10]);
					answerSet.setUpdateDate((Date) a[11]);
					answerSet.setLanguageCode((String) a[14]);
					answerSet.setUniqueCode((String) a[9]);				
					answerSet.setInvitationId((String) a[12]);
					answerSet.setResponderEmail((String) a[13]);
					answerSet.setLanguageCode((String) a[14]);
					answerSet.setScore(ConversionTools.getValue(a[15]));
				}						
			}
			if (lastAnswerSet > 0) parseAnswerSet(answerSet, null, publication, filter, filesByAnswer, export, uploadedFilesByCodeAndQuestionUID, uploadQuestionNicenames);
			results.close();		
		}
		
		if (!sync)
		{
			t.commit();
			session.close();
		}
				
	 spreadsheet.save(outputStream);
	 
	 if (fileCounter > 0 || uploadedFiles.size() > 0)
		{
			//there are multiple files
			java.io.File temp = new java.io.File(exportFilePath + ".zip"); 
			final OutputStream out = new FileOutputStream(temp);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);
			
			if (publication != null)
			{
				os.putArchiveEntry(new ZipArchiveEntry(FilenameUtils.getName(exportFilePath + ".xls")));
			} else {
				os.putArchiveEntry(new ZipArchiveEntry(FilenameUtils.getName(exportFilePath)));
			}
			
		    IOUtils.copy(new FileInputStream(exportFilePath), os);
		    os.closeArchiveEntry();
			
		    String ext = FilenameUtils.getExtension(exportFilePath);
		    
			for (int i = 1; i <= fileCounter; i++)
			{
				String name = exportFilePath.replace("." + ext, "_" + i + "." + ext);
				
				os.putArchiveEntry(new ZipArchiveEntry(FilenameUtils.getName(name)));
			    IOUtils.copy(new FileInputStream(name), os);
			    os.closeArchiveEntry();
			}
			
			for (String code : uploadedFilesByCodeAndQuestionUID.keySet())
			{
				for (String nicename: uploadedFilesByCodeAndQuestionUID.get(code).keySet())
				{
					for (File file: uploadedFilesByCodeAndQuestionUID.get(code).get(nicename))
			    	{
						java.io.File f = fileService.getSurveyFile(survey.getUniqueId(), file.getUid());
			    		
						if (!f.exists())
						{
							f = new java.io.File(exportService.getFileDir() + file.getUid());						
						}
						
						if (f.exists())
						{
				    		os.putArchiveEntry(new ZipArchiveEntry(code + "/" + nicename + "/" + file.getName()));
						    IOUtils.copy(new FileInputStream(f), os);
						    os.closeArchiveEntry();	
						}
			    	}
				}
			}
					    
		    os.close();
		    if (export != null) export.setZipped(true);
		} else {
			if (export != null) export.setZipped(false);
		}
	}
	
	private void parseAnswerSet(AnswerSet answerSet, List<String> answerrow, Publication publication, ResultFilter filter, Map<Integer, List<File>> filesByAnswer, Export export, Map<String, Map<String, List<File>>> uploadedFilesByContributionIDAndQuestionUID, Map<String, String> uploadQuestionNicenames) throws Exception
	{
		rowIndex++;
		columnIndex = 0;
			
		//Excel older than 2007 has a limit on the number of rows
		if (rowIndex > 0 && rowIndex % 65000 == 0)
		{
			fileCounter++;
			spreadsheet.save(outputStream);
			spreadsheet.close();
						
			String ext = FilenameUtils.getExtension(exportFilePath);
			outputStream = new FileOutputStream(exportFilePath.replace( "." + ext, "_" + fileCounter + "." + ext));
			
			spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
		    sheet = spreadsheet.getSheetByIndex(0);
		    sheet.setTableName("Content");
			
			rowIndex = InitSpreadsheet(sheet);
		    columnIndex = 0;
				
			initHeader(publication, filter, export);
		}
		
		int answerrowcounter = 2; //the first item is the answerset code, the second one the id
		
		for (Question question : questions) {
			if (publication != null || filter.exported(question.getId().toString()))
			if (publication == null || publication.isAllQuestions()|| publication.isSelected(question.getId()))
			{
				if (question.IsUsedInResults())
				{	
					if (question instanceof Matrix) {
						Matrix matrix = (Matrix)question;
						for(Element matrixQuestion: matrix.getQuestions()) {
							cell = sheet.getCellByPosition(columnIndex++, rowIndex);
							
							if (answerSet == null)
							{
								cell.setStringValue(ConversionTools.removeHTMLNoEscape(answerrow.get(answerrowcounter)));
								cell.setDisplayText(ConversionTools.removeHTMLNoEscape(answerrow.get(answerrowcounter++)));
							} else {
								List<Answer> answers = answerSet.getAnswers(matrixQuestion.getId(), matrixQuestion.getUniqueId());
				
								StringBuilder cellValue = new StringBuilder();
								for (Answer answer : answers) {
									cellValue.append((cellValue.length() > 0) ? ";" : "").append(ConversionTools.removeHTMLNoEscape(form.getAnswerTitle(answer)));
									if (export != null && export.getShowShortnames())
									{
										cellValue.append(" ").append(form.getAnswerShortname(answer));
									}								
								}							
								
								cell.setStringValue(ConversionTools.removeHTMLNoEscape(cellValue.toString()));
								cell.setDisplayText(ConversionTools.removeHTMLNoEscape(cellValue.toString()));
							}
							cell.setValueType("string");
						}
					} else if (question instanceof RatingQuestion) {
						RatingQuestion rating = (RatingQuestion)question;
						for(Element childQuestion: rating.getQuestions()) {
							cell = sheet.getCellByPosition(columnIndex++, rowIndex);
							if (answerSet == null)
							{
								cell.setStringValue(ConversionTools.removeHTMLNoEscape(answerrow.get(answerrowcounter)));
								cell.setDisplayText(ConversionTools.removeHTMLNoEscape(answerrow.get(answerrowcounter++)));
							} else {
								List<Answer> answers = answerSet.getAnswers(childQuestion.getId(), childQuestion.getUniqueId());
				
								StringBuilder cellValue = new StringBuilder();
								if (answers.size() > 0) {
									cellValue.append((cellValue.length() > 0) ? ";" : "").append(ConversionTools.removeHTMLNoEscape(answers.get(0).getValue()));
								}
								cell.setStringValue(ConversionTools.removeHTMLNoEscape(cellValue.toString()));
								cell.setDisplayText(ConversionTools.removeHTMLNoEscape(cellValue.toString()));
							}
							cell.setValueType("string");
						}
					} else if (question instanceof Upload) {
						if (publication == null || publication.getShowUploadedDocuments())
						{
							cell = sheet.getCellByPosition(columnIndex++, rowIndex);
							StringBuilder cellValue = new StringBuilder();
							if (answerSet == null)
							{
								String sfiles = answerrow.get(answerrowcounter++);
								if (sfiles != null && sfiles.length() > 0)
								{
									String[] files = sfiles.split(";");
									for (String sfile : files)
									{
										if (sfile.length() > 0)
										{
											String[] data = sfile.split("\\|");
											File file = new File();
											file.setUid(data[0]);
											file.setName(data[1]);
											
											if (!uploadedFilesByContributionIDAndQuestionUID.containsKey(answerrow.get(0)))
											{
												uploadedFilesByContributionIDAndQuestionUID.put(answerrow.get(0), new HashMap<String, List<File>>());
											}
											if (!uploadQuestionNicenames.containsKey(question.getUniqueId()))
											{
												uploadQuestionNicenames.put(question.getUniqueId(), "Upload_" + (uploadQuestionNicenames.size() + 1));
											}
											if (!uploadedFilesByContributionIDAndQuestionUID.get(answerrow.get(0)).containsKey(uploadQuestionNicenames.get(question.getUniqueId())))
											{
												uploadedFilesByContributionIDAndQuestionUID.get(answerrow.get(0)).put(uploadQuestionNicenames.get(question.getUniqueId()), new ArrayList<File>());
											}
											uploadedFilesByContributionIDAndQuestionUID.get(answerrow.get(0)).get(uploadQuestionNicenames.get(question.getUniqueId())).add(file);
											
											cellValue.append((cellValue.length() > 0) ? ";" : "").append(file.getName());										
										}
									}
									
									Paragraph p = cell.addParagraph("");
									p.appendHyperlink(cellValue.toString(), new URI("../" + answerrow.get(0) + "/" + uploadQuestionNicenames.get(question.getUniqueId())));
								}
							} else {
								List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());								
								
								for (Answer answer : answers) {
									
									if (filesByAnswer.containsKey(answer.getId()))
									{								
										for (File file: filesByAnswer.get(answer.getId()))
										{
											if (!uploadedFilesByContributionIDAndQuestionUID.containsKey(answerSet.getUniqueCode()))
											{
												uploadedFilesByContributionIDAndQuestionUID.put(answerSet.getUniqueCode(), new HashMap<String, List<File>>());
											}
											if (!uploadQuestionNicenames.containsKey(question.getUniqueId()))
											{
												uploadQuestionNicenames.put(question.getUniqueId(), "Upload_" + (uploadQuestionNicenames.size() + 1));
											}
											if (!uploadedFilesByContributionIDAndQuestionUID.get(answerSet.getUniqueCode()).containsKey(uploadQuestionNicenames.get(question.getUniqueId())))
											{
												uploadedFilesByContributionIDAndQuestionUID.get(answerSet.getUniqueCode()).put(uploadQuestionNicenames.get(question.getUniqueId()), new ArrayList<File>());
											}
											uploadedFilesByContributionIDAndQuestionUID.get(answerSet.getUniqueCode()).get(uploadQuestionNicenames.get(question.getUniqueId())).add(file);
											
											cellValue.append((cellValue.length() > 0) ? ";" : "").append(file.getName());										
										}
									}
								}
								Paragraph p = cell.addParagraph("");
								p.appendHyperlink(cellValue.toString(), new URI("../" + answerSet.getUniqueCode() + "/" + uploadQuestionNicenames.get(question.getUniqueId())));
							}
						}
					} else if (question instanceof Table) {
						Table table = (Table)question;
						
						for (int tableRow = 1; tableRow < table.getAllRows(); tableRow++) {
							for (int tableCol = 1; tableCol < table.getAllColumns(); tableCol++) {
								cell = sheet.getCellByPosition(columnIndex++, rowIndex);
								if (answerSet == null)
								{
									cell.setStringValue(ConversionTools.removeHTMLNoEscape(answerrow.get(answerrowcounter)));
									cell.setDisplayText(ConversionTools.removeHTMLNoEscape(answerrow.get(answerrowcounter++)));
								} else {
									String answer = answerSet.getTableAnswer(table, tableRow, tableCol, false);
									if (answer == null) answer = "";
									
									cell.setStringValue(ConversionTools.removeHTMLNoEscape(answer));
									cell.setDisplayText(ConversionTools.removeHTMLNoEscape(answer));
								}
								cell.setValueType("string");
							}
						}
					} else if (question instanceof GalleryQuestion) {
						cell = sheet.getCellByPosition(columnIndex++, rowIndex);
						if (answerSet == null)
						{
							cell.setStringValue(ConversionTools.removeHTMLNoEscape(answerrow.get(answerrowcounter)));
							cell.setDisplayText(ConversionTools.removeHTMLNoEscape(answerrow.get(answerrowcounter++)));
						} else {
							List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
							
							StringBuilder cellValue = new StringBuilder();
							boolean first = true;
							for (Answer answer : answers) {
								
								if (answer.getValue() != null && answer.getValue().length() > 0)
								{
									int index = Integer.parseInt(answer.getValue());
									if (!first) cellValue.append(", ");
									cellValue.append(((GalleryQuestion) question).getFiles().get(index).getName()).append(" ");
									first = false;
								}
							}
							
							cell.setStringValue(ConversionTools.removeHTMLNoEscape(cellValue.toString()));
							cell.setDisplayText(ConversionTools.removeHTMLNoEscape(cellValue.toString()));
						}
						cell.setValueType("string");
					} else if (question instanceof NumberQuestion && (export == null || !export.getShowShortnames())) {
						
						cell = sheet.getCellByPosition(columnIndex++, rowIndex);
						if (answerSet == null)
						{
							String v = answerrow.get(answerrowcounter++);
							if (v != null && v.length() > 0)
							{
								double cellValue = Double.parseDouble(v);
								cell.setDoubleValue(cellValue);
								cell.setValueType("float");
							}
						} else {
							List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
							
							double cellValue = 0.0;
							
							if (answers.size() > 0)
							{
								cellValue = Double.parseDouble(answers.get(0).getValue());
								cell.setDoubleValue(cellValue);
								cell.setValueType("float");
							}
						}
					} else if (question instanceof DateQuestion && (export == null || !export.getShowShortnames())) {
						
						cell = sheet.getCellByPosition(columnIndex++, rowIndex);
						if (answerSet == null)
						{
							String v = answerrow.get(answerrowcounter++);
							if (v != null && v.length() > 0)
							{
								Date cellValue = ConversionTools.getDate(v);
								if (cellValue!= null)
								{
									Calendar c = Calendar.getInstance();
									c.setTime(cellValue);
									cell.setDateValue(c);
								}	
							}
						} else {	
							List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
							
							Date cellValue = null;
							
							if (answers.size() > 0)
							{
								cellValue = ConversionTools.getDate(answers.get(0).getValue());
								Calendar c = Calendar.getInstance();
								c.setTime(cellValue);
								cell.setDateValue(c);
							}			
						}
						
					} else {
						cell = sheet.getCellByPosition(columnIndex++, rowIndex);
						if (answerSet == null)
						{
							cell.setStringValue(ConversionTools.removeHTMLNoEscape(answerrow.get(answerrowcounter)));
							cell.setDisplayText(ConversionTools.removeHTMLNoEscape(answerrow.get(answerrowcounter++)));
						} else {
							List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
			
							StringBuilder cellValue = new StringBuilder();
							for (Answer answer : answers) {
								cellValue.append((cellValue.length() > 0) ? ";" : "").append(ConversionTools.removeHTMLNoEscape(form.getAnswerTitle(answer)));
								if (export != null && export.getShowShortnames())
								{
									cellValue.append(" ").append(form.getAnswerShortname(answer));
								}
							}
							cell.setStringValue(ConversionTools.removeHTMLNoEscape(cellValue.toString()));
							cell.setDisplayText(ConversionTools.removeHTMLNoEscape(cellValue.toString()));
						}
						cell.setValueType("string");				
					}
				}
			}
		}
		if (publication == null && filter != null)
		{
			if (filter.exported("invitation"))
			{
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				if (form.getSurvey().getSecurity().contains("anonymous"))
				{
					cell.setDisplayText("Anonymous");
					cell.setStringValue("Anonymous");
				} else if (answerSet == null)
				{
					cell.setStringValue(answerrow.get(answerrowcounter));
					cell.setDisplayText(answerrow.get(answerrowcounter++));
				} else {
					cell.setDisplayText(answerSet.getInvitationId() != null ? answerSet.getInvitationId() : "");
					cell.setStringValue(answerSet.getInvitationId() != null ? answerSet.getInvitationId() : "");
				}
				cell.setValueType("string");
			}
			if (filter.exported("case"))
			{
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				if (form.getSurvey().getSecurity().contains("anonymous"))
				{
					cell.setDisplayText("Anonymous");
					cell.setStringValue("Anonymous");
				} else if (answerSet == null)
				{
					cell.setStringValue(answerrow.get(answerrowcounter));
					cell.setDisplayText(answerrow.get(answerrowcounter++));
				} else {
					cell.setDisplayText(answerSet.getUniqueCode() != null ? answerSet.getUniqueCode() : "");
					cell.setStringValue(answerSet.getUniqueCode() != null ? answerSet.getUniqueCode() : "");
				}
				cell.setValueType("string");
			}
			if (filter.exported("user"))
			{
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				if (form.getSurvey().getSecurity().contains("anonymous"))
				{
					cell.setDisplayText("Anonymous");
					cell.setStringValue("Anonymous");
				} else if (answerSet == null)
				{
					cell.setStringValue(answerrow.get(answerrowcounter));
					cell.setDisplayText(answerrow.get(answerrowcounter++));
				} else {
					cell.setDisplayText(answerSet.getResponderEmail() != null ? answerSet.getResponderEmail() : "");
					cell.setStringValue(answerSet.getResponderEmail() != null ? answerSet.getResponderEmail() : "");
				}
				cell.setValueType("string");
			}
			if (filter.exported("created"))
			{
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				if (answerSet == null)
				{
					String v = answerrow.get(answerrowcounter++);
					if (v != null && v.length() > 0)
					{
						Date cellValue = ConversionTools.getDate(v);
						if (cellValue!= null)
						{
							Calendar c = Calendar.getInstance();
							c.setTime(cellValue);
							cell.setDateTimeValue(c);
						}	
					}
				} else if (answerSet.getDate() != null)
				{
					Calendar c = Calendar.getInstance();
					c.setTime(answerSet.getDate());
					cell.setDateTimeValue(c);
					//cell.setFormatString("dd/MM/yyyy hh:mm:ss");
				}
			}
			if (filter.exported("updated"))
			{
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				if (answerSet == null)
				{
					String v = answerrow.get(answerrowcounter++);
					if (v != null && v.length() > 0)
					{
						Date cellValue = ConversionTools.getDate(v);
						if (cellValue!= null)
						{
							Calendar c = Calendar.getInstance();
							c.setTime(cellValue);
							cell.setDateTimeValue(c);
						}	
					}
				} else if (answerSet.getUpdateDate() != null)
				{
					Calendar c = Calendar.getInstance();
					c.setTime(answerSet.getUpdateDate());
					cell.setDateTimeValue(c);
					//cell.setFormatString("dd/MM/yyyy hh:mm:ss");
				}
			}
			if (filter.exported("languages"))
			{
				if (answerSet == null)
				{
					cell.setDisplayText(answerrow.get(answerrowcounter));
					cell.setStringValue(answerrow.get(answerrowcounter++));
				} else {
					cell = sheet.getCellByPosition(columnIndex++, rowIndex);
					cell.setDisplayText(answerSet.getLanguageCode() != null ? answerSet.getLanguageCode() : "");
				}
			}
		}
		
		if (form.getSurvey().getIsQuiz())
		{
			cell = sheet.getCellByPosition(columnIndex++, rowIndex);
			
			if (answerSet == null)
			{
				String v = answerrow.get(answerrowcounter++);
				if (v != null && v.length() > 0)
				{
					cell.setDoubleValue(Double.parseDouble(v));
				} else {
					cell.setDoubleValue(0d);
				}
				cell.setValueType("float");
			} else {			
				cell.setDisplayText(answerSet.getScore() != null ? answerSet.getScore().toString() : "");
				
				double cellValue = answerSet.getScore() != null ? answerSet.getScore() : 0;
				cell.setDoubleValue(cellValue);
				cell.setValueType("float");
			}
		}
	}
	
	
	private int InitSpreadsheet(org.odftoolkit.simple.table.Table sheet) {
		
		int rowIndex = 0;
        Cell cell = sheet.getCellByPosition(0, rowIndex);
		cell.setDisplayText("Alias");
		cell = sheet.getCellByPosition(1, rowIndex);
		
		if (export == null)
		{
			cell.setDisplayText(form.getSurvey().getShortname());
			cell.setStringValue(form.getSurvey().getShortname());
		} else {
			cell.setDisplayText(export.getSurvey().getShortname());
			cell.setStringValue(export.getSurvey().getShortname());
		}
		
		//this is a workaround for a bug in ODFToolkit or LibreOffice that displays content in empty cells
		cell = sheet.getCellByPosition(2, rowIndex);
		cell.setDisplayText("");
		
		rowIndex++;

        cell = sheet.getCellByPosition(0, rowIndex);
		cell.setDisplayText("Export Date");
		cell = sheet.getCellByPosition(1, rowIndex);
		
		if (export == null)
		{
			cell.setDisplayText(new Date().toString());
		} else {
			cell.setDisplayText(export.getDate().toString());		
		}
		
		//this is a workaround for a bug in ODFToolkit or LibreOffice that displays content in empty cells
		cell = sheet.getCellByPosition(2, rowIndex);
		cell.setDisplayText("");
		
		rowIndex+=2;
		return rowIndex;
	}
	
	@Override
	void ExportStatisticsQuiz() throws Exception {}
	
	void ExportStatisticsODS() throws Exception {
		SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
		
	    sheet = spreadsheet.getSheetByIndex(0);
	    sheet.setTableName("Statistics");
		
		sheet.getColumnByIndex(1).setWidth(50);
		sheet.getColumnByIndex(2).setWidth(sheet.getColumnByIndex(0).getWidth());
		
	    DecimalFormat df = new DecimalFormat("#.##");
	        
        Statistics statistics = form.getStatistics();
        Survey survey = surveyService.getSurveyInOriginalLanguage(form.getSurvey().getId(), form.getSurvey().getShortname(), form.getSurvey().getUniqueId());
				
    	if (export != null && export.isAllAnswers() && !survey.isMissingElementsChecked())
		{
			surveyService.CheckAndRecreateMissingElements(survey,  export.getResultFilter());
		}
		    	
		form.setSurvey(survey);
        String cellValue;
        
		rowIndex = 0;
	    
		Set<String> visibleQuestions = null;
		if (export.getResultFilter() != null) visibleQuestions = export.getResultFilter().getExportedQuestions();
		if (visibleQuestions == null || visibleQuestions.size() == 0) visibleQuestions = export.getResultFilter().getVisibleQuestions();
			
        for (Element question : survey.getQuestionsAndSections()) {
        	
        	if (export.getResultFilter() == null || visibleQuestions.size() == 0 || visibleQuestions.contains(question.getId().toString()))
        	{
        		if (question instanceof Section)
        		{
        			cell = sheet.getCellByPosition(0, rowIndex); //TODO: bold
        			cell.setStringValue(ConversionTools.removeHTMLNoEscape(question.getTitle()));
        			cell.setDisplayText(ConversionTools.removeHTMLNoEscape(question.getTitle()));
        			Font font = cell.getFont();
					font.setSize(10);
					font.setFontStyle(FontStyle.BOLD);
					cell.setFont(font);
        			
        			rowIndex++;
        			rowIndex++;
        		}
	        		
				if (question instanceof ChoiceQuestion)
				{
					cellValue = question.getTitle();
					if (export.getShowShortnames())
					{
						cellValue += " (" + question.getShortname() + ")";
					}						
					
					CreateTableForAnswerStat(cellValue);
					ChoiceQuestion choiceQuestion = (ChoiceQuestion)question;
					for (PossibleAnswer possibleAnswer : choiceQuestion.getAllPossibleAnswers()) {
						rowIndex++;				
						
						cellValue = ConversionTools.removeHTMLNoEscape(possibleAnswer.getTitle());
						if (export.getShowShortnames())
						{
							cellValue += " (" + possibleAnswer.getShortname() + ")";
						}						
						
						cell = sheet.getCellByPosition(0, rowIndex); //TODO: bold
						cell.setStringValue(cellValue);
	        			cell.setDisplayText(cellValue);
	        			cell.setValueType("string");
	        			
						Double percent = statistics.getRequestedRecordsPercent().get(possibleAnswer.getId().toString());
						
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();
						
						if (percent > 0)
						{
							org.odftoolkit.simple.draw.Image image = cell.setImage(servletContext.getResource("/resources/images/chart.png").toURI());
							image.getFrame().setRectangle(new FrameRectangle(0.05,0.05,percent / 100 * 4.9,0.36, SupportedLinearMeasure.CM));
						}
					
						cell = sheet.getCellByPosition(2, rowIndex);
						cell.setDoubleValue((double)statistics.getRequestedRecords().get(possibleAnswer.getId().toString()));
	        			cell.setDisplayText(statistics.getRequestedRecords().get(possibleAnswer.getId().toString()).toString());
		        			
	        			cell = sheet.getCellByPosition(3, rowIndex);
	        			cell.setPercentageValue(statistics.getRequestedRecordsPercent().get(possibleAnswer.getId().toString()));
	        			cell.setDisplayText(df.format(statistics.getRequestedRecordsPercent().get(possibleAnswer.getId().toString())) + "%");
		        			
					}
					rowIndex++;
					cell = sheet.getCellByPosition(0, rowIndex);
					cell.setStringValue("No Answer");
        			cell.setDisplayText("No Answer");
        			cell.setValueType("string");
        						
					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());		
					
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.removeContent();
					
					if (percent > 0)
					{
						org.odftoolkit.simple.draw.Image image = cell.setImage(servletContext.getResource("/resources/images/chart.png").toURI());
						image.getFrame().setRectangle(new FrameRectangle(0.05,0.05,percent / 100 * 4.9,0.36, SupportedLinearMeasure.CM));
					}
					cell = sheet.getCellByPosition(2, rowIndex);
					cell.setDoubleValue((double)statistics.getRequestedRecords().get(question.getId().toString()));
					cell.setDisplayText(statistics.getRequestedRecords().get(question.getId().toString()).toString());
        			
					cell = sheet.getCellByPosition(3, rowIndex);
					cell.setPercentageValue(percent);		
					cell.setDisplayText(df.format(percent) + "%");		
					
					rowIndex++;
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.removeContent();
					rowIndex++;	
				} else if (question instanceof GalleryQuestion && ((GalleryQuestion)question).getSelection()) {
					cellValue = question.getTitle();
					if (export.getShowShortnames())
					{
						cellValue += " (" + question.getShortname() + ")";
					}						
					
					CreateTableForAnswerStat(cellValue);
					GalleryQuestion galleryQuestion = (GalleryQuestion)question;
					for (int i = 0; i < galleryQuestion.getFiles().size(); i++) {
						rowIndex++;				
						
						cellValue = ConversionTools.removeHTMLNoEscape(galleryQuestion.getFiles().get(i).getName());
						
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue(cellValue);
	        			cell.setDisplayText(cellValue);
	        			cell.setValueType("string");
	        			
						Double percent = statistics.getRequestedRecordsPercent().get(galleryQuestion.getId().toString() + "-" + i);
						
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();
												
						if (percent > 0)
						{						
							org.odftoolkit.simple.draw.Image image = cell.setImage(servletContext.getResource("/resources/images/chart.png").toURI());
							image.getFrame().setRectangle(new FrameRectangle(0.05,0.05,percent / 100 * 4.9,0.36, SupportedLinearMeasure.CM));
						}
						
						cell = sheet.getCellByPosition(2, rowIndex);
						cell.setDoubleValue((double)statistics.getRequestedRecords().get(galleryQuestion.getId().toString() + "-" + i));
	        			cell.setDisplayText(statistics.getRequestedRecords().get(galleryQuestion.getId().toString() + "-" + i).toString());
	        			
	        			cell = sheet.getCellByPosition(3, rowIndex);
	        			cell.setPercentageValue(statistics.getRequestedRecordsPercent().get(galleryQuestion.getId().toString() + "-" + i));
	        			cell.setDisplayText(df.format(statistics.getRequestedRecordsPercent().get(galleryQuestion.getId().toString() + "-" + i)) + "%");
	        		}
					
					rowIndex++;
					cell = sheet.getCellByPosition(0, rowIndex);
					cell.setStringValue("No Answer");
        			cell.setDisplayText("No Answer");
        			cell.setValueType("string");
        						
					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());		
					
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.removeContent();
					
					if (percent > 0)
					{
						org.odftoolkit.simple.draw.Image image = cell.setImage(servletContext.getResource("/resources/images/chart.png").toURI());
						image.getFrame().setRectangle(new FrameRectangle(0.05,0.05,percent / 100 * 4.9,0.36, SupportedLinearMeasure.CM));
					}
					cell = sheet.getCellByPosition(2, rowIndex);
					cell.setDoubleValue((double)statistics.getRequestedRecords().get(question.getId().toString()));
					cell.setDisplayText(statistics.getRequestedRecords().get(question.getId().toString()).toString());
        			
					cell = sheet.getCellByPosition(3, rowIndex);
					cell.setPercentageValue(percent);		
					cell.setDisplayText(df.format(percent) + "%");		
					
					rowIndex++;
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.removeContent();
					rowIndex++;	
				} else if (question instanceof Matrix) {
					
					Matrix matrix = (Matrix)question;
					
					for (Element matrixQuestion: matrix.getQuestions()) {
						
						cellValue = matrix.getTitle() + ": " + matrixQuestion.getTitle();
						if (export.getShowShortnames())
						{
							cellValue += " (" + matrixQuestion.getShortname() + ")";
						}						
						
						CreateTableForAnswerStat(cellValue);

						for (Element matrixAnswer: matrix.getAnswers()) {
							rowIndex++;		
							
							cellValue = ConversionTools.removeHTMLNoEscape(matrixAnswer.getTitle());
							if (export.getShowShortnames())
							{
								cellValue += " (" + matrixAnswer.getShortname() + ")";
							}		
							
							cell = sheet.getCellByPosition(0, rowIndex);
							cell.setStringValue(cellValue);
		        			cell.setDisplayText(cellValue);
		        			cell.setValueType("string");
				        			
							Double percent = statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, matrixAnswer);
							
							cell = sheet.getCellByPosition(1, rowIndex);
							cell.removeContent();
							
							if (percent > 0)
							{						
								org.odftoolkit.simple.draw.Image image = cell.setImage(servletContext.getResource("/resources/images/chart.png").toURI());
								image.getFrame().setRectangle(new FrameRectangle(0.05,0.05,percent / 100 * 4.9,0.36, SupportedLinearMeasure.CM));
							}
							
							cell = sheet.getCellByPosition(2, rowIndex);
							cell.setDoubleValue((double)statistics.getRequestedRecordsForMatrix(matrixQuestion, matrixAnswer));
		        			cell.setDisplayText(statistics.getRequestedRecordsForMatrix(matrixQuestion, matrixAnswer) + "");
		        			
		        			cell = sheet.getCellByPosition(3, rowIndex);
		        			cell.setPercentageValue(statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, matrixAnswer));
		        			cell.setDisplayText(df.format(statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, matrixAnswer)) + "%");
		        		}
						
						rowIndex++;
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue("No Answer");
	        			cell.setDisplayText("No Answer");
	        			cell.setValueType("string");
	        						
						Double percent = statistics.getRequestedRecordsPercent().get(matrixQuestion.getId().toString());						
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();
						if (percent > 0)
						{
							org.odftoolkit.simple.draw.Image image = cell.setImage(servletContext.getResource("/resources/images/chart.png").toURI());
							image.getFrame().setRectangle(new FrameRectangle(0.05,0.05,percent / 100 * 4.9,0.36, SupportedLinearMeasure.CM));
						}
						cell = sheet.getCellByPosition(2, rowIndex);
						cell.setDoubleValue((double)statistics.getRequestedRecords().get(matrixQuestion.getId().toString()));
						cell.setDisplayText(statistics.getRequestedRecords().get(matrixQuestion.getId().toString()).toString());
	        			
						cell = sheet.getCellByPosition(3, rowIndex);
						cell.setPercentageValue(percent);		
						cell.setDisplayText(df.format(percent) + "%");		
						
						rowIndex++;
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();
						rowIndex++;	
					}
				} else if (question instanceof RatingQuestion) {
					
					RatingQuestion rating = (RatingQuestion)question;
					
					for (Element childQuestion: rating.getQuestions()) {
						
						cellValue = rating.getTitle() + ": " + childQuestion.getTitle();
						if (export.getShowShortnames())
						{
							cellValue += " (" + childQuestion.getShortname() + ")";
						}						
						
						CreateTableForAnswerStat(cellValue);

						for (int i = 1; i < rating.getNumIcons(); i++) {
							rowIndex++;		
							
							cellValue = i + "/" + rating.getNumIcons();	
							
							cell = sheet.getCellByPosition(0, rowIndex);
							cell.setStringValue(cellValue);
		        			cell.setDisplayText(cellValue);
		        			cell.setValueType("string");
				        			
							Double percent = statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i);
							
							cell = sheet.getCellByPosition(1, rowIndex);
							cell.removeContent();
							
							if (percent > 0)
							{						
								org.odftoolkit.simple.draw.Image image = cell.setImage(servletContext.getResource("/resources/images/chart.png").toURI());
								image.getFrame().setRectangle(new FrameRectangle(0.05,0.05,percent / 100 * 4.9,0.36, SupportedLinearMeasure.CM));
							}
							
							cell = sheet.getCellByPosition(2, rowIndex);
							cell.setDoubleValue((double)statistics.getRequestedRecordsForRatingQuestion(childQuestion, i));
		        			cell.setDisplayText(statistics.getRequestedRecordsForRatingQuestion(childQuestion, i) + "");
		        			
		        			cell = sheet.getCellByPosition(3, rowIndex);
		        			cell.setPercentageValue(statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i));
		        			cell.setDisplayText(df.format(statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i)) + "%");
		        		}
						
						rowIndex++;
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue("No Answer");
	        			cell.setDisplayText("No Answer");
	        			cell.setValueType("string");
	        						
						Double percent = statistics.getRequestedRecordsPercent().get(childQuestion.getId().toString());						
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();
						if (percent > 0)
						{
							org.odftoolkit.simple.draw.Image image = cell.setImage(servletContext.getResource("/resources/images/chart.png").toURI());
							image.getFrame().setRectangle(new FrameRectangle(0.05,0.05,percent / 100 * 4.9,0.36, SupportedLinearMeasure.CM));
						}
						cell = sheet.getCellByPosition(2, rowIndex);
						cell.setDoubleValue((double)statistics.getRequestedRecords().get(childQuestion.getId().toString()));
						cell.setDisplayText(statistics.getRequestedRecords().get(childQuestion.getId().toString()).toString());
	        			
						cell = sheet.getCellByPosition(3, rowIndex);
						cell.setPercentageValue(percent);		
						cell.setDisplayText(df.format(percent) + "%");		
						
						rowIndex++;
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();
						rowIndex++;	
					}
				}
        	}
		}       
        spreadsheet.save(outputStream);
	}
	
	private void CreateTableForAnswerStat(String title) {	
		cell = sheet.getCellByPosition(0, rowIndex); //TODO: bold
		cell.setStringValue(ConversionTools.removeHTMLNoEscape(title));
		cell.setDisplayText(ConversionTools.removeHTMLNoEscape(title));
		Font font = cell.getFont();
		font.setSize(10);
		font.setFontStyle(FontStyle.BOLD);
		cell.setFont(font);
		
		rowIndex++; 
	
		cell = sheet.getCellByPosition(2, rowIndex);
		cell.setStringValue(ConversionTools.removeHTMLNoEscape("Answers"));
		cell.setDisplayText(ConversionTools.removeHTMLNoEscape("Answers"));
		
		cell = sheet.getCellByPosition(3, rowIndex);
		cell.setStringValue(ConversionTools.removeHTMLNoEscape("Ratio"));
		cell.setDisplayText(ConversionTools.removeHTMLNoEscape("Ratio"));
	}

	@Override
	void ExportStatistics() throws Exception {
		if (export.getFormat() == ExportFormat.ods)
		{
			ExportStatisticsODS();
			return;
		}
		
		DecimalFormat df = new DecimalFormat("#.##");
		
        TextDocument document = TextDocument.newTextDocument();
        Survey survey = surveyService.getSurveyInOriginalLanguage(form.getSurvey().getId(), form.getSurvey().getShortname(), form.getSurvey().getUniqueId());
		
        if (export != null && export.isAllAnswers() && !survey.isMissingElementsChecked())
		{
			surveyService.CheckAndRecreateMissingElements(survey,  export.getResultFilter());
		}
		        
		form.setSurvey(survey);
        
        Statistics statistics = form.getStatistics();
        String cellValue;
        
		Set<String> visibleQuestions = null;
		if (export.getResultFilter() != null) visibleQuestions = export.getResultFilter().getExportedQuestions();
		if (visibleQuestions == null || visibleQuestions.size() == 0) visibleQuestions = export.getResultFilter().getVisibleQuestions();
		
        for (Element question : survey.getQuestionsAndSections()) {        	
        	if (export.getResultFilter() == null || visibleQuestions.size() == 0 || visibleQuestions.contains(question.getId().toString()))	{
        		
        		if (question instanceof Section)
        		{
        			Paragraph paragraph = document.addParagraph(ConversionTools.removeHTMLNoEscape(question.getTitle()));
        			paragraph.getOdfElement().setProperty(OdfParagraphProperties.KeepWithNext, "always");
        				
        			Font font = paragraph.getFont();			
        			font.setFontStyle(FontStyle.BOLD);
        			paragraph.setFont(font);
        			document.addParagraph("");
        		}
        		
        		if (question instanceof ChoiceQuestion)
				{				
        			cellValue = ConversionTools.removeHTMLNoEscape(question.getTitle());
        			if (export != null && export.getShowShortnames())
					{
						cellValue += " (" + question.getShortname() + ")";
					}		
        			
					org.odftoolkit.simple.table.Table table = CreateTableForAnswer(document, cellValue);
					ChoiceQuestion choiceQuestion = (ChoiceQuestion)question;
					for (PossibleAnswer possibleAnswer : choiceQuestion.getAllPossibleAnswers()) {
						Row row = table.appendRow();
						
						cellValue = ConversionTools.removeHTMLNoEscape(possibleAnswer.getTitle());
	        			if (export != null && export.getShowShortnames())
						{
							cellValue += " (" + possibleAnswer.getShortname() + ")";
						}	
						
						row.getCellByIndex(0).setStringValue(cellValue);
						row.getCellByIndex(0).setValueType("string");
						
						Double percent = statistics.getRequestedRecordsPercent().get(possibleAnswer.getId().toString());
							
						if (percent > 0)
						{
							Paragraph p = row.getCellByIndex(1).addParagraph("");
							Textbox t = p.addTextbox(new FrameRectangle(0,0,percent / 100 * 5,0.5,SupportedLinearMeasure.CM));
							t.setBackgroundColor(Color.BLUE);
							Paragraph p2 = t.addParagraph(".");
							Font font = p2.getFont();
							font.setSize(6);
							font.setColor(Color.BLUE);
							p2.setFont(font);
						}
						row.getCellByIndex(2).setDoubleValue((double)statistics.getRequestedRecords().get(possibleAnswer.getId().toString()));
						row.getCellByIndex(2).setStringValue(Integer.toString(statistics.getRequestedRecords().get(possibleAnswer.getId().toString())));
						row.getCellByIndex(3).setPercentageValue(percent); // / 100);
						row.getCellByIndex(3).setStringValue(df.format(percent) + "%");					
												
						//row.getCellByIndex(2).setValueType("float");
						//row.getCellByIndex(3).setValueType("percentage");						
					}
					
					Row row = table.appendRow();					
					row.getCellByIndex(0).setStringValue("No Answer");					
					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());						
					if (percent > 0)
					{
						Paragraph p = row.getCellByIndex(1).addParagraph("");
						Textbox t = p.addTextbox(new FrameRectangle(0,0,percent / 100 * 5,0.5,SupportedLinearMeasure.CM));
						t.setBackgroundColor(Color.BLUE);
						Paragraph p2 = t.addParagraph(".");
						Font font = p2.getFont();
						font.setSize(6);
						font.setColor(Color.BLUE);
						p2.setFont(font);
					}
					row.getCellByIndex(2).setDoubleValue((double)statistics.getRequestedRecords().get(question.getId().toString()));
					row.getCellByIndex(2).setStringValue(Integer.toString(statistics.getRequestedRecords().get(question.getId().toString())));
										
					//row.getCellByIndex(3).setPercentageValue(percent / 100);
					row.getCellByIndex(3).setStringValue(df.format(percent) + "%");					
					
					row.getCellByIndex(2).setValueType("float");
					//row.getCellByIndex(3).setValueType("percentage");
					
					document.addParagraph("");
				} else if (question instanceof GalleryQuestion && ((GalleryQuestion)question).getSelection()) {
					cellValue = ConversionTools.removeHTMLNoEscape(question.getTitle());
        			if (export != null && export.getShowShortnames())
					{
						cellValue += " (" + question.getShortname() + ")";
					}		
        			
					org.odftoolkit.simple.table.Table table = CreateTableForAnswer(document, cellValue);
					GalleryQuestion galleryQuestion = (GalleryQuestion)question;
					for (int i = 0; i < galleryQuestion.getFiles().size(); i++) {
						Row row = table.appendRow();
						
						cellValue = ConversionTools.removeHTMLNoEscape(galleryQuestion.getFiles().get(i).getName());
	        			
						row.getCellByIndex(0).setStringValue(cellValue);
						
						Double percent = statistics.getRequestedRecordsPercent().get(galleryQuestion.getId().toString() + "-" + i);
						
						if (percent > 0)
						{
							Paragraph p = row.getCellByIndex(1).addParagraph("");
							Textbox t = p.addTextbox(new FrameRectangle(0,0,percent / 100 * 5,0.5,SupportedLinearMeasure.CM));
							t.setBackgroundColor(Color.BLUE);
							Paragraph p2 = t.addParagraph(".");
							Font font = p2.getFont();
							font.setSize(6);
							font.setColor(Color.BLUE);
							p2.setFont(font);
						}						
						
						row.getCellByIndex(2).setDoubleValue((double)statistics.getRequestedRecords().get(galleryQuestion.getId().toString() + "-" + i));
						row.getCellByIndex(2).setStringValue(Integer.toString(statistics.getRequestedRecords().get(galleryQuestion.getId().toString() + "-" + i)));
						
						
						//row.getCellByIndex(3).setPercentageValue(percent / 100);
						row.getCellByIndex(3).setStringValue(df.format(percent) + "%");					
						
						row.getCellByIndex(2).setValueType("float");
						//row.getCellByIndex(3).setValueType("percentage");
					}
					
					Row row = table.appendRow();					
					row.getCellByIndex(0).setStringValue("No Answer");					
					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());						
					if (percent > 0)
					{
						Paragraph p = row.getCellByIndex(1).addParagraph("");
						Textbox t = p.addTextbox(new FrameRectangle(0,0,percent / 100 * 5,0.5,SupportedLinearMeasure.CM));
						t.setBackgroundColor(Color.BLUE);
						Paragraph p2 = t.addParagraph(".");
						Font font = p2.getFont();
						font.setSize(6);
						font.setColor(Color.BLUE);
						p2.setFont(font);
					}
					row.getCellByIndex(2).setDoubleValue((double)statistics.getRequestedRecords().get(question.getId().toString()));
					row.getCellByIndex(2).setStringValue(Integer.toString(statistics.getRequestedRecords().get(question.getId().toString())));
					
					//row.getCellByIndex(3).setPercentageValue(percent / 100);
					row.getCellByIndex(3).setStringValue(df.format(percent) + "%");					
					
					row.getCellByIndex(2).setValueType("float");
					//row.getCellByIndex(3).setValueType("percentage");
					
					document.addParagraph("");
				} else if (question instanceof Matrix) {
					
					Matrix matrix = (Matrix)question;
					
					for (Element matrixQuestion: matrix.getQuestions()) {
						
						cellValue = ConversionTools.removeHTMLNoEscape(matrix.getTitle() + ": " + matrixQuestion.getTitle());
	        			if (export != null && export.getShowShortnames())
						{
							cellValue += " (" + matrixQuestion.getShortname() + ")";
						}			        			
						
						org.odftoolkit.simple.table.Table table = CreateTableForAnswer(document, cellValue);
						for (Element matrixAnswer: matrix.getAnswers()) {
							Row row = table.appendRow();				
							
							cellValue = ConversionTools.removeHTMLNoEscape(matrixAnswer.getTitle());
		        			if (export != null && export.getShowShortnames())
							{
								cellValue += " (" + matrixAnswer.getShortname() + ")";
							}	
							
							row.getCellByIndex(0).setStringValue(cellValue);
							
							Double percent = statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, matrixAnswer);
							
							if (percent > 0)
							{
								Paragraph p = row.getCellByIndex(1).addParagraph("");
								Textbox t = p.addTextbox(new FrameRectangle(0,0,percent / 100 * 5,0.5,SupportedLinearMeasure.CM));
								t.setBackgroundColor(Color.BLUE);
								Paragraph p2 = t.addParagraph(".");
								Font font = p2.getFont();
								font.setSize(6);
								font.setColor(Color.BLUE);
								p2.setFont(font);
							}							
							
							row.getCellByIndex(2).setDoubleValue((double)statistics.getRequestedRecordsForMatrix(matrixQuestion, matrixAnswer));
							row.getCellByIndex(2).setStringValue(Integer.toString(statistics.getRequestedRecordsForMatrix(matrixQuestion, matrixAnswer)));
														
							row.getCellByIndex(3).setStringValue(df.format(percent) + "%");					
							//row.getCellByIndex(3).setPercentageValue(percent / 100);
							row.getCellByIndex(2).setValueType("float");
							//row.getCellByIndex(3).setValueType("percentage");
						}
						
						Row row = table.appendRow();					
						row.getCellByIndex(0).setStringValue("No Answer");					
						Double percent = statistics.getRequestedRecordsPercent().get(matrixQuestion.getId().toString());						
						if (percent > 0)
						{
							Paragraph p = row.getCellByIndex(1).addParagraph("");
							Textbox t = p.addTextbox(new FrameRectangle(0,0,percent / 100 * 5,0.5,SupportedLinearMeasure.CM));
							t.setBackgroundColor(Color.BLUE);
							Paragraph p2 = t.addParagraph(".");
							Font font = p2.getFont();
							font.setSize(6);
							font.setColor(Color.BLUE);
							p2.setFont(font);
						}
						row.getCellByIndex(2).setDoubleValue((double)statistics.getRequestedRecords().get(matrixQuestion.getId().toString()));
						row.getCellByIndex(2).setStringValue(Integer.toString(statistics.getRequestedRecords().get(matrixQuestion.getId().toString())));
						
						row.getCellByIndex(3).setStringValue(df.format(percent) + "%");					
						//row.getCellByIndex(3).setPercentageValue(percent / 100);
						row.getCellByIndex(2).setValueType("float");
						//row.getCellByIndex(3).setValueType("percentage");
						
						document.addParagraph("");
					}
				} else if (question instanceof RatingQuestion) {
					
					RatingQuestion rating = (RatingQuestion)question;
					
					for (Element childQuestion: rating.getQuestions()) {
						
						cellValue = ConversionTools.removeHTMLNoEscape(rating.getTitle() + ": " + childQuestion.getTitle());
	        			if (export != null && export.getShowShortnames())
						{
							cellValue += " (" + rating.getShortname() + ")";
						}			        			
						
						org.odftoolkit.simple.table.Table table = CreateTableForAnswer(document, cellValue);
						for (int i = 1; i <= rating.getNumIcons(); i++) {
							Row row = table.appendRow();				
							
							cellValue = i + "/" + rating.getNumIcons();
							
							row.getCellByIndex(0).setStringValue(cellValue);
							
							Double percent = statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i);
							
							if (percent > 0)
							{
								Paragraph p = row.getCellByIndex(1).addParagraph("");
								Textbox t = p.addTextbox(new FrameRectangle(0,0,percent / 100 * 5,0.5,SupportedLinearMeasure.CM));
								t.setBackgroundColor(Color.BLUE);
								Paragraph p2 = t.addParagraph(".");
								Font font = p2.getFont();
								font.setSize(6);
								font.setColor(Color.BLUE);
								p2.setFont(font);
							}							
							
							row.getCellByIndex(2).setDoubleValue((double)statistics.getRequestedRecordsForRatingQuestion(childQuestion, i));
							row.getCellByIndex(2).setStringValue(Integer.toString(statistics.getRequestedRecordsForRatingQuestion(childQuestion, i)));
														
							row.getCellByIndex(3).setStringValue(df.format(percent) + "%");					
							//row.getCellByIndex(3).setPercentageValue(percent / 100);
							row.getCellByIndex(2).setValueType("float");
							//row.getCellByIndex(3).setValueType("percentage");
						}
						
						Row row = table.appendRow();					
						row.getCellByIndex(0).setStringValue("No Answer");					
						Double percent = statistics.getRequestedRecordsPercent().get(childQuestion.getId().toString());						
						if (percent > 0)
						{
							Paragraph p = row.getCellByIndex(1).addParagraph("");
							Textbox t = p.addTextbox(new FrameRectangle(0,0,percent / 100 * 5,0.5,SupportedLinearMeasure.CM));
							t.setBackgroundColor(Color.BLUE);
							Paragraph p2 = t.addParagraph(".");
							Font font = p2.getFont();
							font.setSize(6);
							font.setColor(Color.BLUE);
							p2.setFont(font);
						}
						row.getCellByIndex(2).setDoubleValue((double)statistics.getRequestedRecords().get(childQuestion.getId().toString()));
						row.getCellByIndex(2).setStringValue(Integer.toString(statistics.getRequestedRecords().get(childQuestion.getId().toString())));
						
						row.getCellByIndex(3).setStringValue(df.format(percent) + "%");					
						//row.getCellByIndex(3).setPercentageValue(percent / 100);
						row.getCellByIndex(2).setValueType("float");
						//row.getCellByIndex(3).setValueType("percentage");
						
						document.addParagraph("");
					}
				}
	        }
		}       
        document.save(outputStream);		
	}
	
	private org.odftoolkit.simple.table.Table CreateTableForAnswer(TextDocument document, String title) {	
		Paragraph paragraph = document.addParagraph(ConversionTools.removeHTMLNoEscape(title));
		paragraph.getOdfElement().setProperty(OdfParagraphProperties.KeepWithNext, "always");
			
		Font font = paragraph.getFont();			
		font.setFontStyle(FontStyle.BOLD);
		paragraph.setFont(font);

		org.odftoolkit.simple.table.Table table = document.addTable(1,4);
		table.getOdfElement().setProperty(OdfTableProperties.MayBreakBetweenRows, "false");
		
		table.getColumnByIndex(1).setWidth(53);
		
		Cell cell = table.getCellByPosition(2, 0);
		cell.setFont(font);
		cell.setStringValue("Answers");
		cell = table.getCellByPosition(3, 0);
		cell.setStringValue("Ratio");
		cell.setFont(font);	
		return table;
	}

	@Override
	void ExportAddressBook() throws Exception {
		
		User user = administrationService.getUser(userId);
		
		SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
	    org.odftoolkit.simple.table.Table sheet = spreadsheet.getSheetByIndex(0);
	    sheet.setTableName("Contacts");
		
		int rowIndex = 0;
		int cellIndex = 0;
		Cell cell;
		
		int ownerId;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
    	{
			ownerId = -1;
    	} else {
    		ownerId = user.getId();    		
    	}
		
		List<Attendee> attendees = attendeeService.getAttendees(ownerId, export.getResultFilter().getFilterValues(), 1, Integer.MAX_VALUE);
		List<AttributeName> configuredattributes = user.getSelectedAttributes();		
	
		try	
		{	
			
			cell = sheet.getCellByPosition(cellIndex++, rowIndex);
			cell.setStringValue("Name");
			
			cell = sheet.getCellByPosition(cellIndex++, rowIndex);
			cell.setStringValue("Email");
			
			for (AttributeName att: configuredattributes)
			{
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue(att.getName());
			}
			rowIndex++;
			cellIndex = 0;
					
			for (Attendee attendee : attendees)
			{				
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue(attendee.getName());
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue(attendee.getEmail());
				
				for (AttributeName att: configuredattributes)
				{
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					
					if (att.getName().equals("Owner"))
					{
						cell.setStringValue(attendee.getOwner() != null ? attendee.getOwner().replace("&#160;", "") : "");
					} else {
						cell.setStringValue(attendee.getAttributeValue(att.getId()).replace("&#160;", ""));
					}	
				}			
				
				rowIndex++;
				cellIndex = 0;
			}  
			
			spreadsheet.save(outputStream);
										
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}	
		
	}

	@Override
	void ExportActivities() throws Exception {
		SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
	    org.odftoolkit.simple.table.Table sheet = spreadsheet.getSheetByIndex(0);
	    sheet.setTableName("Contacts");
		
		int rowIndex = 0;
		int cellIndex = 0;
		Cell cell;
		
		List<Activity> activities = activityService.get(export.getActivityFilter(), 1, Integer.MAX_VALUE);
		
		try	
		{	
			ActivityFilter  filter = export.getActivityFilter();
			if (filter.exported("date"))
			{
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("Date");
			}
			
			if (filter.exported("logid"))
			{
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("LogID");
			}
			
			if (filter.exported("user"))
			{
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("User");
			}
			
			if (filter.exported("object"))
			{
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("Object");
			}
			
			if (filter.exported("property"))
			{
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("Property");
			}
			
			if (filter.exported("event"))
			{
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("Event");
			}
			
			if (filter.exported("description"))
			{
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("Description");
			}
			
			if (filter.exported("oldvalue"))
			{
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("OldValue");
			}
			
			if (filter.exported("newvalue"))
			{
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("NewValue");
			}
			
			rowIndex++;
			cellIndex = 0;
					
			for (Activity activity : activities)
			{				
				if (filter.exported("date"))
				{
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(ConversionTools.getFullString(activity.getDate()));
				}
				
				if (filter.exported("logid"))
				{
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(Integer.toString(activity.getLogID()));
				}
				
				if (filter.exported("user"))
				{
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					activity.setUserName(activity.getUserId() > 0 ? administrationService.getUser(activity.getUserId()).getName() : "");
					cell.setStringValue(activity.getUserName());
				}
				
				if (filter.exported("object"))
				{
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(activity.getObject());
				}
				
				if (filter.exported("property"))
				{
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(activity.getProperty());
				}
				
				if (filter.exported("event"))
				{
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(activity.getEvent());
				}
				
				if (filter.exported("description"))
				{
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(resources.getMessage("logging." + activity.getLogID(), null, "logging." + activity.getLogID(), locale));
				}
				
				if (filter.exported("oldvalue"))
				{
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(activity.getOldValue());
				}
				
				if (filter.exported("newvalue"))
				{
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(activity.getNewValue());
				}			
				
				rowIndex++;
				cellIndex = 0;
			}  
			
			spreadsheet.save(outputStream);
										
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}	
	}
	
	@Override
	void ExportTokens() throws Exception {
		
		ParticipationGroup participationGroup = participationService.get(export.getParticipationGroup());
		
		SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
	    org.odftoolkit.simple.table.Table sheet = spreadsheet.getSheetByIndex(0);
	    sheet.setTableName("Tokens");
		
		if (participationGroup.getType() == ParticipationGroupType.Static || participationGroup.getType() == ParticipationGroupType.ECMembers)
		{
			Map<Integer, Invitation> invitations = attendeeService.getInvitationsByAttendeeForParticipationGroup(export.getParticipationGroup());
			int rowIndex = 0;
			
			cell = sheet.getCellByPosition(0, rowIndex);		
			cell.setStringValue("TokenList ID");
			cell = sheet.getCellByPosition(1, rowIndex++);		
			cell.setStringValue(export.getParticipationGroup().toString());
			
			cell = sheet.getCellByPosition(0, rowIndex);
			cell.setStringValue("UNIQUE CODE");
			cell = sheet.getCellByPosition(1, rowIndex);
			cell.setStringValue("NAME");
			cell = sheet.getCellByPosition(2, rowIndex);
			cell.setStringValue("EMAIL");
			
			if (participationGroup.getType() == ParticipationGroupType.Static)
			{
				for (Attendee attendee : participationGroup.getAttendees())
				{
					rowIndex++;
					
					Invitation invitation = invitations.get(attendee.getId());				
					if (invitation == null)
					{
						invitation = new Invitation(participationGroup.getId(), attendee.getId());
						attendeeService.add(invitation);
					}				
					
					cell = sheet.getCellByPosition(0, rowIndex);
					cell.setStringValue(invitation.getUniqueId());
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.setStringValue(attendee.getName());
					cell = sheet.getCellByPosition(2, rowIndex);
					cell.setStringValue(attendee.getEmail());
				}
			} else {
				for (EcasUser attendee : participationGroup.getEcasUsers())
				{
					rowIndex++;
					
					Invitation invitation = invitations.get(attendee.getId());				
					if (invitation == null)
					{
						invitation = new Invitation(participationGroup.getId(), attendee.getId());
						attendeeService.add(invitation);
					}				
					
					cell = sheet.getCellByPosition(0, rowIndex);
					cell.setStringValue(invitation.getUniqueId());
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.setStringValue(attendee.getName());
					cell = sheet.getCellByPosition(2, rowIndex);
					cell.setStringValue(attendee.getEmail());
				}
			}
			
		} else {
		
			List<String> tokens = participationService.getTokens(1, Integer.MAX_VALUE, export.getParticipationGroup(), false);
			Map<String, Date> datesForTokens = participationService.getDatesForTokens(export.getParticipationGroup());
			
			int rowIndex = 0;
		    
		    cell = sheet.getCellByPosition(0, rowIndex);		
			cell.setStringValue("Survey");
			cell = sheet.getCellByPosition(1, rowIndex);		
			cell.setStringValue(export.getSurvey().getUniqueId());
			cell = sheet.getCellByPosition(2, rowIndex++);		
			cell.setStringValue(export.getSurvey().getShortname());
						
			cell = sheet.getCellByPosition(0, rowIndex);		
			cell.setStringValue("Guestlist");
			cell = sheet.getCellByPosition(1, rowIndex);		
			cell.setStringValue(export.getParticipationGroup().toString());
			cell = sheet.getCellByPosition(2, rowIndex++);		
			ParticipationGroup group = participationService.get(export.getParticipationGroup());			
			cell.setStringValue(ConversionTools.getFullString(group.getCreated()));
			
			rowIndex++;
			
			cell = sheet.getCellByPosition(0, rowIndex);
			cell.setStringValue("Token");
			cell = sheet.getCellByPosition(1, rowIndex);
			cell.setStringValue("Used");

			rowIndex++;
			
			for (String token: tokens)
			{
				cell = sheet.getCellByPosition(0, rowIndex);
				cell.setStringValue(token);
				
				if (datesForTokens.containsKey(token))
				{
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.setStringValue(ConversionTools.getFullString(datesForTokens.get(token)));
				}
				
				rowIndex++;
			}
		
		}
		
		spreadsheet.save(outputStream);	
	}	

}
