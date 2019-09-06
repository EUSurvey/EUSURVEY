package com.ec.survey.tools.export;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ec.survey.model.Statistics;
import com.ec.survey.model.survey.ChoiceQuestion;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.GalleryQuestion;
import com.ec.survey.model.survey.Matrix;
import com.ec.survey.model.survey.PossibleAnswer;
import com.ec.survey.model.survey.RatingQuestion;
import com.ec.survey.model.survey.Section;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.tools.ConversionTools;

@Service("docExportCreator")
@Scope("prototype")
public class DocExportCreator extends ExportCreator {

	CustomXWPFDocument document;
	
	@Override
	public void init()
	{

	}

	@Override
	void ExportCharts() throws Exception {
		throw new Exception("Not implemented");		
	}

	@Override
	void ExportContent(boolean sync) throws Exception {
		throw new Exception("Not implemented");
	}
	
	@Override
	void ExportStatisticsQuiz() throws Exception {}

	@Override
	void ExportStatistics() throws IOException {
        document = new CustomXWPFDocument();
        DecimalFormat df = new DecimalFormat("#.##");
        
        Statistics statistics = form.getStatistics();
    	Survey survey = surveyService.getSurvey(form.getSurvey().getId(), false, true);
    	
    	if (export != null && export.isAllAnswers() && !survey.isMissingElementsChecked())
		{
			surveyService.CheckAndRecreateMissingElements(survey,  export.getResultFilter());
		}
				
		form.setSurvey(survey);
        String cellValue;
        
		Set<String> visibleQuestions = null;
		if (export.getResultFilter() != null) visibleQuestions = export.getResultFilter().getExportedQuestions();
		if (visibleQuestions == null || visibleQuestions.size() == 0) visibleQuestions = export.getResultFilter().getVisibleQuestions();
		
        for (Element question : survey.getQuestionsAndSections()) {
        	
        	if (export.getResultFilter() == null || visibleQuestions.size() == 0 || visibleQuestions.contains(question.getId().toString()))
        	{
        		if (question instanceof Section)
        		{
        			XWPFParagraph paragraph = document.createParagraph();
        			        			
        			if (paragraph.getCTP().getPPr() == null) paragraph.getCTP().addNewPPr();
        			
        			XWPFRun run = paragraph.createRun();
        			run.setText(ConversionTools.removeHTMLNoEscape(question.getTitle()));	
        			run.setBold(true);
        		}
        		
				if (question instanceof ChoiceQuestion)
				{
					cellValue = question.getTitle();
					if (export.getShowShortnames())
					{
						cellValue += " (" + question.getShortname() + ")";
					}						
					
					XWPFTable table = CreateTableForAnswer(cellValue);
					ChoiceQuestion choiceQuestion = (ChoiceQuestion)question;
					for (PossibleAnswer possibleAnswer : choiceQuestion.getAllPossibleAnswers()) {
						XWPFTableRow row = table.createRow();				
						
						cellValue = ConversionTools.removeHTMLNoEscape(possibleAnswer.getTitle());
						if (export.getShowShortnames())
						{
							cellValue += " (" + possibleAnswer.getShortname() + ")";
						}						
						
						row.getCell(0).setText(cellValue);
						
						Double percent = statistics.getRequestedRecordsPercent().get(possibleAnswer.getId().toString());
						
						if (percent > 0)
						{						
							XWPFParagraph p = row.getCell(1).getParagraphs().get(0); //.addParagraph();
							
							InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");
							
							try {
								String blipId = p.getDocument().addPictureData(pictureData, Document.PICTURE_TYPE_PNG);
													
								CTInline inline = p.createRun().getCTR().addNewDrawing().addNewInline();
								document.createPicture(blipId, document.getNextPicNameNumber(Document.PICTURE_TYPE_PNG), percent.intValue(), 10, inline);
															
							} catch (InvalidFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						row.getCell(2).setText(statistics.getRequestedRecords().get(possibleAnswer.getId().toString()).toString());
						row.getCell(3).setText(df.format(statistics.getRequestedRecordsPercent().get(possibleAnswer.getId().toString())) + "%");					
					}
					
					//noanswers
					XWPFTableRow row = table.createRow();				
					row.getCell(0).setText("No Answer");
					
					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());
					
					if (percent > 0)
					{						
						XWPFParagraph p = row.getCell(1).getParagraphs().get(0); 						
						InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");						
						try {
							String blipId = p.getDocument().addPictureData(pictureData, Document.PICTURE_TYPE_PNG);
												
							CTInline inline = p.createRun().getCTR().addNewDrawing().addNewInline();
							document.createPicture(blipId, document.getNextPicNameNumber(Document.PICTURE_TYPE_PNG), percent.intValue(), 10, inline);
														
						} catch (InvalidFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}					
					row.getCell(2).setText(statistics.getRequestedRecords().get(question.getId().toString()).toString());
					row.getCell(3).setText(df.format(statistics.getRequestedRecordsPercent().get(question.getId().toString())) + "%");
					
					document.createParagraph();
				} else if (question instanceof GalleryQuestion && ((GalleryQuestion)question).getSelection()) {
					cellValue = question.getTitle();
					if (export.getShowShortnames())
					{
						cellValue += " (" + question.getShortname() + ")";
					}						
					
					XWPFTable table = CreateTableForAnswer(cellValue);
					GalleryQuestion galleryQuestion = (GalleryQuestion)question;
					for (int i = 0; i < galleryQuestion.getFiles().size(); i++) {
						XWPFTableRow row = table.createRow();				
						
						cellValue = ConversionTools.removeHTMLNoEscape(galleryQuestion.getFiles().get(i).getName());
						
						row.getCell(0).setText(cellValue);
						
						Double percent = statistics.getRequestedRecordsPercent().get(galleryQuestion.getId().toString() + "-" + i);
						
						if (percent > 0)
						{						
							XWPFParagraph p = row.getCell(1).getParagraphs().get(0); //.addParagraph();
							
							InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");
							
							try {
								String blipId = p.getDocument().addPictureData(pictureData, Document.PICTURE_TYPE_PNG);
													
								CTInline inline = p.createRun().getCTR().addNewDrawing().addNewInline();
								document.createPicture(blipId, document.getNextPicNameNumber(Document.PICTURE_TYPE_PNG), percent.intValue(), 10, inline);
															
							} catch (InvalidFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						row.getCell(2).setText(statistics.getRequestedRecords().get(galleryQuestion.getId().toString() + "-" + i).toString());
						row.getCell(3).setText(df.format(statistics.getRequestedRecordsPercent().get(galleryQuestion.getId().toString() + "-" + i)) + "%");					
					}
					
					//noanswers
					XWPFTableRow row = table.createRow();				
					row.getCell(0).setText("No Answer");
					
					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());
					
					if (percent > 0)
					{						
						XWPFParagraph p = row.getCell(1).getParagraphs().get(0); 						
						InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");						
						try {
							String blipId = p.getDocument().addPictureData(pictureData, Document.PICTURE_TYPE_PNG);
												
							CTInline inline = p.createRun().getCTR().addNewDrawing().addNewInline();
							document.createPicture(blipId, document.getNextPicNameNumber(Document.PICTURE_TYPE_PNG), percent.intValue(), 10, inline);
														
						} catch (InvalidFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}					
					row.getCell(2).setText(statistics.getRequestedRecords().get(question.getId().toString()).toString());
					row.getCell(3).setText(df.format(statistics.getRequestedRecordsPercent().get(question.getId().toString())) + "%");
					
					document.createParagraph();
				} else if (question instanceof Matrix) {
					
					Matrix matrix = (Matrix)question;
					
					for (Element matrixQuestion: matrix.getQuestions()) {
						
						cellValue = matrix.getTitle() + ": " + matrixQuestion.getTitle();
						if (export.getShowShortnames())
						{
							cellValue += " (" + matrixQuestion.getShortname() + ")";
						}						
						
						XWPFTable table = CreateTableForAnswer(cellValue);

						for (Element matrixAnswer: matrix.getAnswers()) {
							XWPFTableRow row = table.createRow();	
							
							cellValue = ConversionTools.removeHTMLNoEscape(matrixAnswer.getTitle());
							if (export.getShowShortnames())
							{
								cellValue += " (" + matrixAnswer.getShortname() + ")";
							}		
							
							row.getCell(0).setText(cellValue);
							
							Double percent = statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, matrixAnswer);
							
							if (percent > 0)
							{						
								XWPFParagraph p = row.getCell(1).getParagraphs().get(0); //.addParagraph();
								
								InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");
								
								try {
									String blipId = p.getDocument().addPictureData(pictureData, Document.PICTURE_TYPE_PNG);
														
									CTInline inline = p.createRun().getCTR().addNewDrawing().addNewInline();
									document.createPicture(blipId, document.getNextPicNameNumber(Document.PICTURE_TYPE_PNG), percent.intValue(), 10, inline);
																
								} catch (InvalidFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							row.getCell(2).setText(statistics.getRequestedRecordsForMatrix(matrixQuestion, matrixAnswer) + "");
							row.getCell(3).setText(df.format(statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, matrixAnswer)) + "%");
						}
						
						//noanswers
						XWPFTableRow row = table.createRow();				
						row.getCell(0).setText("No Answer");
						
						Double percent = statistics.getRequestedRecordsPercent().get(matrixQuestion.getId().toString());
						
						if (percent > 0)
						{						
							XWPFParagraph p = row.getCell(1).getParagraphs().get(0); 						
							InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");						
							try {
								String blipId = p.getDocument().addPictureData(pictureData, Document.PICTURE_TYPE_PNG);
													
								CTInline inline = p.createRun().getCTR().addNewDrawing().addNewInline();
								document.createPicture(blipId, document.getNextPicNameNumber(Document.PICTURE_TYPE_PNG), percent.intValue(), 10, inline);
															
							} catch (InvalidFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}					
						row.getCell(2).setText(statistics.getRequestedRecords().get(matrixQuestion.getId().toString()).toString());
						row.getCell(3).setText(df.format(statistics.getRequestedRecordsPercent().get(matrixQuestion.getId().toString())) + "%");
						
						document.createParagraph();
					}
				} else if (question instanceof RatingQuestion) {
					
					RatingQuestion rating = (RatingQuestion)question;
					
					for (Element childQuestion: rating.getQuestions()) {
						
						cellValue = rating.getTitle() + ": " + childQuestion.getTitle();
						if (export.getShowShortnames())
						{
							cellValue += " (" + childQuestion.getShortname() + ")";
						}						
						
						XWPFTable table = CreateTableForAnswer(cellValue);

						for (int i = 1; i <= rating.getNumIcons(); i++) {
							XWPFTableRow row = table.createRow();	
							
							cellValue = i + "/" + rating.getNumIcons();		
							
							row.getCell(0).setText(cellValue);
							
							Double percent = statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i);
							
							if (percent > 0)
							{						
								XWPFParagraph p = row.getCell(1).getParagraphs().get(0); //.addParagraph();
								
								InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");
								
								try {
									String blipId = p.getDocument().addPictureData(pictureData, Document.PICTURE_TYPE_PNG);
														
									CTInline inline = p.createRun().getCTR().addNewDrawing().addNewInline();
									document.createPicture(blipId, document.getNextPicNameNumber(Document.PICTURE_TYPE_PNG), percent.intValue(), 10, inline);
																
								} catch (InvalidFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							row.getCell(2).setText(statistics.getRequestedRecordsForRatingQuestion(childQuestion, i) + "");
							row.getCell(3).setText(df.format(statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i)) + "%");
						}
						
						//noanswers
						XWPFTableRow row = table.createRow();				
						row.getCell(0).setText("No Answer");
						
						Double percent = statistics.getRequestedRecordsPercent().get(childQuestion.getId().toString());
						
						if (percent > 0)
						{						
							XWPFParagraph p = row.getCell(1).getParagraphs().get(0); 						
							InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");						
							try {
								String blipId = p.getDocument().addPictureData(pictureData, Document.PICTURE_TYPE_PNG);
													
								CTInline inline = p.createRun().getCTR().addNewDrawing().addNewInline();
								document.createPicture(blipId, document.getNextPicNameNumber(Document.PICTURE_TYPE_PNG), percent.intValue(), 10, inline);
															
							} catch (InvalidFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}					
						row.getCell(2).setText(statistics.getRequestedRecords().get(childQuestion.getId().toString()).toString());
						row.getCell(3).setText(df.format(statistics.getRequestedRecordsPercent().get(childQuestion.getId().toString())) + "%");
						
						document.createParagraph();
					}
				}
        	}
		}       
        document.write(outputStream);		
	}
	
	private XWPFTable CreateTableForAnswer(String title) {	
		XWPFParagraph paragraph = document.createParagraph();
		
		if (paragraph.getCTP().getPPr() == null) paragraph.getCTP().addNewPPr();
		paragraph.getCTP().getPPr().addNewKeepNext().setVal(STOnOff.ON);
		
		XWPFRun run = paragraph.createRun();
		run.setText(ConversionTools.removeHTMLNoEscape(title));	
		run.setBold(true);
		
		XWPFTable table = document.createTable();
		table.setCellMargins(3, 50, 3, 50);
				
		XWPFTableCell cell = table.getRow(0).createCell();
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1640));
		
		cell = table.getRow(0).createCell();
		setCellTextBold(cell, "Answers");
		cell = table.getRow(0).createCell();
		setCellTextBold(cell, "Ratio");
		return table;
	}
	
	private void setCellTextBold(XWPFTableCell cell, String text) {
		cell.removeParagraph(0);
		XWPFParagraph paragraph = cell.addParagraph();
		XWPFRun run = paragraph.createRun();
		run.setBold(true);
		run.setText(text);
	}
	
	@Override
	void ExportAddressBook() throws Exception {}

	@Override
	void ExportActivities() throws Exception {}
	
	@Override
	void ExportTokens() throws Exception {}	

}
