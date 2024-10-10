package com.ec.survey.tools.export;

import com.ec.survey.model.*;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.AttributeName;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.model.FilesByTypes;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.model.survey.ecf.ECFGlobalCompetencyResult;
import com.ec.survey.model.survey.ecf.ECFGlobalResult;
import com.ec.survey.model.survey.ecf.ECFOrganizationalCompetencyResult;
import com.ec.survey.model.survey.ecf.ECFOrganizationalResult;
import com.ec.survey.model.survey.ecf.ECFProfileCompetencyResult;
import com.ec.survey.model.survey.ecf.ECFProfileResult;
import com.ec.survey.service.ExportService;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.mysql.cj.util.StringUtils;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.hibernate.*;
import org.hibernate.query.NativeQuery;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

@Service("xlsExportCreator")
@Scope("prototype")
public class XlsExportCreator extends ExportCreator {

	/** Manipulated in the initialization **/
	CellStyle dateStyle;
	CellStyle questionTitleStyle;
	Workbook wb;

	/** Manipulated in the methods **/
	private int lastSheet;
	private Sheet sheet;
	private Row row;

	/** Manipulated in the methods **/
	private int columnIndexInsertHeader;
	private int rowIndexInsertHeader;
	private Sheet sheetInsertHeader;
	private Row rowInsertHeader;
	private Map<String, CellStyle> cellStyles = new HashMap<>();

	/** Manipulated in the methods **/
	int rowIndex;
	int columnIndex;
	int counter;
	String safeName;
	List<Sheet> sheets = new ArrayList<>();
	int fileCounter;
	DecimalFormat df = new DecimalFormat("#.##");

	@Override
	public void init() {
		initWorkbook();
	}

	private void initWorkbook() {
		wb = new HSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		dateStyle = wb.createCellStyle();
		dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(ConversionTools.DateTimeFormatSmall));
		dateStyle.setAlignment(CellStyle.ALIGN_LEFT);

		questionTitleStyle = wb.createCellStyle();
		questionTitleStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
		Font f = wb.createFont();
		f.setBoldweight(Font.BOLDWEIGHT_BOLD);
		questionTitleStyle.setFont(f);
	}

	private void checkColumnInsertHeader(Export export) {
		if (columnIndexInsertHeader > 253) {
			columnIndexInsertHeader = 0;
			safeName = WorkbookUtil.createSafeSheetName("Content" + sheets.size());
			sheetInsertHeader = wb.createSheet(safeName);
			initWorkbook(sheetInsertHeader, export);
			sheets.add(sheetInsertHeader);
			rowInsertHeader = sheetInsertHeader.createRow(rowIndexInsertHeader - 1);
		}
	}

	private int insertHeader(List<Sheet> sheets, Publication publication, ResultFilter filter, Export export) {
		sheetInsertHeader = sheets.get(0);
		rowIndexInsertHeader = initWorkbook(sheetInsertHeader, export);

		List<Question> questions = form.getSurvey().getQuestions();
		rowInsertHeader = sheetInsertHeader.createRow(rowIndexInsertHeader++);
		columnIndexInsertHeader = 0;
		String cellValue;
		for (Question question : questions) {
			if ((publication != null || filter.exported(question.getId().toString()))
					&& (publication == null || publication.isAllQuestions() || publication.isSelected(question.getId()))
					&& question.isUsedInResults()) {
				if (question instanceof Matrix) {
					Matrix matrix = (Matrix) question;
					for (Element matrixQuestion : matrix.getQuestions()) {
						Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
						cellValue = ConversionTools.removeHTMLNoEscape(ConversionTools
								.removeHTMLNoEscape(matrix.getTitle() + ": " + matrixQuestion.getTitle()));
						if (export != null && export.getShowShortnames()) {
							cellValue += " (" + matrixQuestion.getShortname() + ")";
						}
						cell.setCellValue(cellValue);
						cell.setCellStyle(questionTitleStyle);
						sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
						checkColumnInsertHeader(export);
					}
				} else if (question instanceof RatingQuestion) {
					RatingQuestion rating = (RatingQuestion) question;
					for (Element childQuestion : rating.getQuestions()) {
						Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
						cellValue = ConversionTools.removeHTMLNoEscape(ConversionTools
								.removeHTMLNoEscape(rating.getTitle() + ": " + childQuestion.getTitle()));
						if (export != null && export.getShowShortnames()) {
							cellValue += " (" + childQuestion.getShortname() + ")";
						}
						cell.setCellValue(cellValue);
						cell.setCellStyle(questionTitleStyle);
						sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
						checkColumnInsertHeader(export);
					}
				} else if (question instanceof ComplexTable) {
					ComplexTable table = (ComplexTable) question;
					for (ComplexTableItem childQuestion : table.getQuestionChildElements()) {
						Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
						cellValue = ConversionTools.removeHTMLNoEscape(
								ConversionTools.removeHTMLNoEscape(childQuestion.getResultTitle(table)));
						if (export != null && export.getShowShortnames()) {
							cellValue += " (" + childQuestion.getShortname() + ")";
						}
						cell.setCellValue(cellValue);
						cell.setCellStyle(questionTitleStyle);
						sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
						checkColumnInsertHeader(export);
					}
				} else if (question instanceof Table) {
					Table table = (Table) question;
					for (Element tableQuestion : table.getQuestions()) {
						for (Element tableAnswer : table.getAnswers()) {
							Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);

							cellValue = ConversionTools.removeHTMLNoEscape(
									table.getTitle() + " " + tableQuestion.getTitle() + ":" + tableAnswer.getTitle());
							if (export != null && export.getShowShortnames()) {
								cellValue = ConversionTools.removeHTMLNoEscape(table.getTitle() + " "
										+ tableQuestion.getTitle() + " (" + tableQuestion.getShortname() + "):"
										+ tableAnswer.getTitle() + " (" + tableAnswer.getShortname() + ")");
							}
							cell.setCellValue(cellValue);

							cell.setCellStyle(questionTitleStyle);
							sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
							checkColumnInsertHeader(export);
						}
					}
				} else if (question instanceof Upload && publication != null
						&& !publication.getShowUploadedDocuments()) {
					// skip uploaded files;
				} else {
					Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
					cellValue = ConversionTools.removeHTMLNoEscape(question.getTitle());
					if (export != null && export.getShowShortnames()) {
						cellValue += " (" + question.getShortname() + ")";
					}
					cell.setCellValue(cellValue);
					cell.setCellStyle(questionTitleStyle);
					sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
					checkColumnInsertHeader(export);
				}

				if (form.getSurvey().getIsDelphi() && question.isDelphiElement()
						&& filter.explanationExported(question.getId().toString())) {
					Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
					cell.setCellValue(resources.getMessage("label.Explanation", null, "Explanation", locale));
					cell.setCellStyle(questionTitleStyle);
					sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
					checkColumnInsertHeader(export);

					cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
					cell.setCellValue(resources.getMessage("label.Likes", null, "Likes", locale));
					cell.setCellStyle(questionTitleStyle);
					sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
					checkColumnInsertHeader(export);
				}

				if (form.getSurvey().getIsDelphi() && question.isDelphiElement()
						&& filter.discussionExported(question.getId().toString())) {
					Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
					cell.setCellValue(resources.getMessage("label.Discussion", null, "Discussion", locale));
					cell.setCellStyle(questionTitleStyle);
					sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
					checkColumnInsertHeader(export);
				}
			}
		}

		if (publication == null && filter != null) {
			if (filter.exported("invitation")) {
				Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
				cell.setCellValue(resources.getMessage("label.InvitationNumber", null, "Invitation Number", locale));
				cell.setCellStyle(questionTitleStyle);
				sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
				checkColumnInsertHeader(export);
			}
			if (filter.exported("case")) {
				Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
				cell.setCellValue(resources.getMessage("label.ContributionId", null, "Contribution Id", locale));
				cell.setCellStyle(questionTitleStyle);
				sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
				checkColumnInsertHeader(export);
			}
			if (filter.exported("user")) {
				Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
				cell.setCellValue(resources.getMessage("label.UserName", null, "User Name", locale));
				cell.setCellStyle(questionTitleStyle);
				sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
				checkColumnInsertHeader(export);
			}
			if (filter.exported("created")) {
				Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
				cell.setCellValue(resources.getMessage("label.CreationDate", null, "Creation Date", locale));
				cell.setCellStyle(questionTitleStyle);
				sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
				checkColumnInsertHeader(export);
			}
			if (filter.exported("updated")) {
				Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
				cell.setCellValue(resources.getMessage("label.LastUpdate", null, "Last Update", locale));
				cell.setCellStyle(questionTitleStyle);
				sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
				checkColumnInsertHeader(export);
			}
			if (filter.exported("languages")) {
				Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
				cell.setCellValue(resources.getMessage("label.Languages", null, "Languages", locale));
				cell.setCellStyle(questionTitleStyle);
				sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
				checkColumnInsertHeader(export);
			}

		}

		if (form.getSurvey().getIsQuiz()) {
			Cell cell = rowInsertHeader.createCell(columnIndexInsertHeader++);
			cell.setCellValue(resources.getMessage("label.TotalScore", null, "Total Score", locale));
			cell.setCellStyle(questionTitleStyle);
			sheetInsertHeader.setColumnWidth(columnIndexInsertHeader, 5000);
			checkColumnInsertHeader(export);
		}

		return rowIndexInsertHeader;
	}

	@Override
	void exportContent(boolean sync) throws Exception {
		exportContent(null, sync);
	}

	public void exportContent(Publication publication, boolean sync) throws Exception {
		exportContent(publication, sync, null);
	}

	public void exportContent(Publication publication, boolean sync, ResultFilter resultFilter) throws Exception {
		sheets = new ArrayList<>();

		safeName = WorkbookUtil.createSafeSheetName("Content");
		counter = 0;
		fileCounter = 0;
		Sheet sheet = wb.createSheet(safeName);
		sheets.add(sheet);

		ResultFilter filter;
		if (resultFilter != null) {
			filter = resultFilter;
		} else if (publication != null && export == null) {
			filter = publication.getFilter();
		} else {
			filter = export.getResultFilter().copy();
		}

		/// here starts the db stuff
		Survey survey = surveyService.getSurveyInOriginalLanguage(form.getSurvey().getId(),
				form.getSurvey().getShortname(), form.getSurvey().getUniqueId());
		for (Element element : survey.getElements()) {
			if (element instanceof GalleryQuestion) {
				Hibernate.initialize(((GalleryQuestion) element).getFiles());
			}
		}

		if (export != null && export.isAllAnswers() && !survey.isMissingElementsChecked()) {
			surveyService.checkAndRecreateMissingElements(survey, filter);
			Hibernate.initialize(survey.getMissingElements());
			for (Element e : survey.getMissingElements()) {
				if (e instanceof ChoiceQuestion) {
					Hibernate.initialize(((ChoiceQuestion) e).getPossibleAnswers());
				} else if (e instanceof GalleryQuestion) {
					Hibernate.initialize(((GalleryQuestion) e).getFiles());
				}
			}
		}

		form.setSurvey(survey);

		rowIndex = insertHeader(sheets, publication, filter, export);

		List<File> uploadedFiles = new ArrayList<>();
		if (publication == null || publication.getShowUploadedDocuments()) {
			uploadedFiles = answerService.getAllUploadedFiles(form.getSurvey().getId(), filter, 1, Integer.MAX_VALUE);
		}

		Map<Integer, List<File>> filesByAnswer = new HashMap<>();
		for (File file : uploadedFiles) {
			if (file.getAnswerId() != null) {
				if (!filesByAnswer.containsKey(file.getAnswerId())) {
					filesByAnswer.put(file.getAnswerId(), new ArrayList<>());
				}
				filesByAnswer.get(file.getAnswerId()).add(file);
			}
		}

		FilesByTypes<Integer, String> explanationFilesOfSurvey = answerExplanationService
				.getExplanationFilesByAnswerSetIdAndQuestionUid(form.getSurvey());
		FilesByTypes<String, String> explanationFilesToExport = new FilesByTypes<>();

		Map<String, Map<String, List<File>>> uploadedFilesByCodeAndQuestionUID = new HashMap<>();
		Map<String, String> uploadQuestionNicenames = new HashMap<>();

		Session session;
		Transaction t = null;

		if (sync) {
			session = sessionFactory.getCurrentSession();
		} else {
			session = sessionFactory.openSession();
			t = session.beginTransaction();
		}

		if (export != null)
			session.evict(export);
		session.evict(survey);
		session.evict(filter);
		if (publication != null) {
			session.evict(publication);
		}

		HashMap<String, Object> values = new HashMap<>();

		session.flush();

		if (publication != null) {
			if (publication.isAllQuestions()) {
				for (Element question : survey.getQuestions()) {
					filter.getExportedQuestions().add(question.getId().toString());
				}
			} else {
				for (String question : filter.getVisibleQuestions()) {
					filter.getExportedQuestions().add(question);
				}
			}
		}

		filter.getVisibleQuestions().clear();
		filter.getVisibleQuestions().addAll(filter.getExportedQuestions());

		filter.getVisibleExplanations().clear();
		filter.getVisibleExplanations().addAll(filter.getExportedExplanations());

		filter.getVisibleDiscussions().clear();
		filter.getVisibleDiscussions().addAll(filter.getExportedDiscussions());

		List<List<String>> answersets = null;

		if (export == null || export.isForArchiving() == null || !export.isForArchiving()) {
			answersets = reportingService.getAnswerSets(survey, filter, null, false, true,
					publication == null || publication.getShowUploadedDocuments(), false, false,
					export != null && export.getShowShortnames());
		}
		List<Question> questions = form.getSurvey().getQuestions();

		if (answersets != null) {
			for (List<String> row : answersets) {
				parseAnswerSet(null, row, publication, filter, filesByAnswer, export, questions,
						uploadedFilesByCodeAndQuestionUID, uploadQuestionNicenames, null, null, null,
						explanationFilesOfSurvey, explanationFilesToExport);
			}
		} else {

			// it is not possible to query the database after the result query was executed
			Map<Integer, Map<String, String>> explanations = answerExplanationService
					.getAllExplanations(form.getSurvey());
			Map<Integer, Map<String, String>> discussions = answerExplanationService
					.getAllDiscussions(form.getSurvey());
			Map<String, Integer> likesForExplanations = answerExplanationService.getAllLikesForExplanation(form.getSurvey());

			String sql = "select ans.ANSWER_SET_ID, a.QUESTION_UID, a.VALUE, a.ANSWER_COL, a.ANSWER_ID, a.ANSWER_ROW, a.PA_UID, ans.UNIQUECODE, ans.ANSWER_SET_DATE, ans.ANSWER_SET_UPDATE, ans.ANSWER_SET_INVID, ans.RESPONDER_EMAIL, ans.ANSWER_SET_LANG, ans.SCORE FROM ANSWERS a RIGHT JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID IN ("
					+ answerService.getSql(null, form.getSurvey().getId(), filter, values, true)
					+ ") ORDER BY ans.ANSWER_SET_ID";

			NativeQuery query = session.createSQLQuery(sql);

			query.setReadOnly(true);

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

			query.setFetchSize(Integer.MIN_VALUE);
			ScrollableResults results = query.setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);

			try {

				int lastAnswerSet = 0;
				AnswerSet answerSet = new AnswerSet();
				answerSet.setSurvey(survey);

				session.flush();

				int counter = 0;
				while (results.next()) {

					if (counter > 1000) {
						session.clear();
						counter = 0;
					}
					counter++;

					Object[] a = results.get();
					Answer answer = new Answer();
					answer.setAnswerSetId(ConversionTools.getValue(a[0]));
					answer.setQuestionUniqueId((String) a[1]);
					answer.setValue((String) a[2]);
					answer.setColumn(ConversionTools.getValue(a[3]));
					answer.setId(ConversionTools.getValue(a[4]));
					answer.setRow(ConversionTools.getValue(a[5]));
					answer.setPossibleAnswerUniqueId((String) a[6]);

					if (lastAnswerSet == answer.getAnswerSetId()) {
						answerSet.addAnswer(answer);
					} else {
						if (lastAnswerSet > 0) {
							session.flush();
							parseAnswerSet(answerSet, null, publication, filter, filesByAnswer, export, questions,
									uploadedFilesByCodeAndQuestionUID, uploadQuestionNicenames, explanations,
									discussions, likesForExplanations, explanationFilesOfSurvey, explanationFilesToExport);
						}

						answerSet = new AnswerSet();
						answerSet.setSurvey(survey);
						answerSet.setId(answer.getAnswerSetId());
						lastAnswerSet = answer.getAnswerSetId();
						answerSet.getAnswers().add(answer);
						answerSet.setDate((Date) a[8]);
						answerSet.setUpdateDate((Date) a[9]);
						answerSet.setUniqueCode((String) a[7]);
						answerSet.setInvitationId((String) a[10]);
						answerSet.setResponderEmail((String) a[11]);
						answerSet.setLanguageCode((String) a[12]);
						answerSet.setScore(ConversionTools.getValue(a[13]));
					}
				}
				if (lastAnswerSet > 0)
					parseAnswerSet(answerSet, null, publication, filter, filesByAnswer, export, questions,
							uploadedFilesByCodeAndQuestionUID, uploadQuestionNicenames, explanations, discussions, likesForExplanations,
							explanationFilesOfSurvey, explanationFilesToExport);
			} finally {
				results.close();
			}

		}

		if (!sync) {
			t.commit();
			session.close();
		}

		wb.write(outputStream);

		if (fileCounter > 0 || !uploadedFiles.isEmpty() || explanationFilesToExport.hasFiles()) {
			// there are multiple files
			java.io.File temp = new java.io.File(exportFilePath + ".zip");
			final OutputStream out = new FileOutputStream(temp);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);

			if (publication != null) {
				os.putArchiveEntry(new ZipArchiveEntry(FilenameUtils.getName(exportFilePath + ".xls")));
			} else {
				os.putArchiveEntry(new ZipArchiveEntry(FilenameUtils.getName(exportFilePath)));
			}
			IOUtils.copy(new FileInputStream(exportFilePath), os);
			os.closeArchiveEntry();

			String ext = FilenameUtils.getExtension(exportFilePath);

			try {

				for (int i = 1; i <= fileCounter; i++) {
					String name = exportFilePath.replace("." + ext, "_" + i + "." + ext);

					os.putArchiveEntry(new ZipArchiveEntry(FilenameUtils.getName(name)));
					IOUtils.copy(new FileInputStream(name), os);
					os.closeArchiveEntry();
				}

				for (String code : uploadedFilesByCodeAndQuestionUID.keySet()) {
					for (String questionNiceName : uploadedFilesByCodeAndQuestionUID.get(code).keySet()) {
						for (File file : uploadedFilesByCodeAndQuestionUID.get(code).get(questionNiceName)) {
							java.io.File f = fileService.getSurveyFile(survey.getUniqueId(), file.getUid());

							if (!f.exists()) {
								f = new java.io.File(exportService.getFileDir() + file.getUid());
							}

							if (f.exists()) {
								os.putArchiveEntry(new ZipArchiveEntry(code + Constants.PATH_DELIMITER
										+ questionNiceName + Constants.PATH_DELIMITER + file.getName()));
								IOUtils.copy(new FileInputStream(f), os);
								os.closeArchiveEntry();
							}
						}
					}
				}

				explanationFilesToExport.applyFunctionOnEachFile((answerSetId, questionUid, explanationFile) -> {
					java.io.File file = fileService.getSurveyFile(survey.getUniqueId(), explanationFile.getUid());

					if (!file.exists()) {
						file = new java.io.File(exportService.getFileDir() + explanationFile.getUid());
					}

					if (file.exists()) {
						os.putArchiveEntry(new ZipArchiveEntry(answerSetId + Constants.PATH_DELIMITER + questionUid
								+ Constants.PATH_DELIMITER + explanationFile.getName()));
						IOUtils.copy(new FileInputStream(file), os);
						os.closeArchiveEntry();
					}
				});

			} finally {
				os.close();
			}

			if (export != null)
				export.setZipped(true);
		} else {
			if (export != null)
				export.setZipped(false);
		}

	}

	private Cell checkColumnsParseAnswerSet() {
		// columnIndex starts with 0
		// lastSheet starts with 0

		if (columnIndex / 254 != lastSheet) {
			sheet = sheets.get(columnIndex / 254);
			row = sheet.createRow(rowIndex - 1);
			lastSheet = columnIndex / 254;
		}

		// columns from 1 to 254 mean indices from 0 to 253
		Cell cell = row.createCell(columnIndex % 254);

		columnIndex++;

		return cell;
	}

	CellStyle dateCellStyle = null;

	CellStyle cswrap = null;

	private void parseAnswerSet(AnswerSet answerSet, List<String> answerrow, Publication publication,
			ResultFilter filter, Map<Integer, List<File>> filesByAnswer, Export export, List<Question> questions,
			Map<String, Map<String, List<File>>> uploadedFilesByContributionIDAndQuestionUID,
			Map<String, String> uploadQuestionNicenames, Map<Integer, Map<String, String>> explanations,
			Map<Integer, Map<String, String>> discussions, Map<String, Integer> likesForExplanations,  FilesByTypes<Integer, String> explanationFilesOfSurvey,
			FilesByTypes<String, String> explanationFilesToExport) throws IOException {
		CreationHelper createHelper = wb.getCreationHelper();

		// Excel older than 2007 has a limit on the number of rows
		if (rowIndex > 0 && rowIndex % 20000 == 0) // 65000 == 0)
		{
			fileCounter++;
			wb.write(outputStream);
			outputStream.close();
			wb.close();
			safeName = WorkbookUtil.createSafeSheetName("Content");
			columnIndexInsertHeader = 0;
			String ext = FilenameUtils.getExtension(exportFilePath);

			outputStream = new FileOutputStream(
					ExportService.getExportPathWithSuffix(exportFilePath, ext, fileCounter));

			initWorkbook();

			sheets.clear();
			sheet = wb.createSheet(safeName);
			sheets.add(sheet);
			rowIndex = insertHeader(sheets, publication, filter, export);

			dateCellStyle = null;
			cellStyles.clear();
		}

		if (dateCellStyle == null) {
			dateCellStyle = wb.createCellStyle();
			dateCellStyle.setDataFormat((short) 14);
		}

		sheet = sheets.get(0);
		row = sheet.createRow(rowIndex++);
		lastSheet = 0;
		columnIndex = 0;
		List<Row> addedRows = new ArrayList<>();

		int answerrowcounter = 2; // the first item is the answerset code, the second one the id

		for (Question question : questions) {
			if ((publication != null || filter.exported(question.getId().toString()))
					&& (publication == null || publication.isAllQuestions() || publication.isSelected(question.getId()))
					&& question.isUsedInResults()) {
				if (question instanceof Matrix) {
					Matrix matrix = (Matrix) question;
					for (Element matrixQuestion : matrix.getQuestions()) {
						Cell cell = checkColumnsParseAnswerSet();

						if (answerSet == null) {
							String v = answerrow.get(answerrowcounter++);

							if (v != null) {
								cell.setCellValue(ConversionTools.removeHTMLNoEscape(v));
							}
						} else {
							List<Answer> answers = answerSet.getAnswers(matrixQuestion.getUniqueId());
							StringBuilder cellValue = new StringBuilder();
							for (Answer answer : answers) {
								cellValue.append((cellValue.length() > 0) ? ";" : "")
										.append(ConversionTools.removeHTMLNoEscape(form.getAnswerTitle(answer)));
								if (export != null && export.getShowShortnames()) {
									cellValue.append(" ").append(form.getAnswerShortname(answer));
								}
							}
							cell.setCellValue(ConversionTools.removeHTMLNoEscape(cellValue.toString()));
						}
					}
				} else if (question instanceof RatingQuestion) {
					RatingQuestion rating = (RatingQuestion) question;
					for (Element childQuestion : rating.getQuestions()) {
						Cell cell = checkColumnsParseAnswerSet();

						if (answerSet == null) {
							String v = answerrow.get(answerrowcounter++);

							if (v != null) {
								cell.setCellValue(ConversionTools.removeHTMLNoEscape(v));
							}
						} else {
							List<Answer> answers = answerSet.getAnswers(childQuestion.getUniqueId());

							StringBuilder cellValue = new StringBuilder();
							if (!answers.isEmpty()) {
								cellValue.append((cellValue.length() > 0) ? ";" : "")
										.append(ConversionTools.removeHTMLNoEscape(answers.get(0).getValue()));
							}
							cell.setCellValue(ConversionTools.removeHTMLNoEscape(cellValue.toString()));
						}
					}
				} else if (question instanceof ComplexTable) {
					ComplexTable table = (ComplexTable) question;
					for (ComplexTableItem childQuestion : table.getQuestionChildElements()) {
						Cell cell = checkColumnsParseAnswerSet();

						if (answerSet == null) {
							String v = answerrow.get(answerrowcounter++);

							if (v != null) {
								cell.setCellValue(ConversionTools.removeHTMLNoEscape(v));
							}
						} else {
							List<Answer> answers = answerSet.getAnswers(childQuestion.getUniqueId());
							StringBuilder cellValue = new StringBuilder();
							for (Answer answer : answers) {
								cellValue.append((cellValue.length() > 0) ? ";" : "")
										.append(ConversionTools.removeHTMLNoEscape(form.getAnswerTitle(answer)));
								if (export != null && export.getShowShortnames()) {
									cellValue.append(" ").append(form.getAnswerShortname(answer));
								}
							}
							cell.setCellValue(ConversionTools.removeHTMLNoEscape(cellValue.toString()));
						}
					}
				} else if (question instanceof Table) {
					Table table = (Table) question;

					for (int tableRow = 1; tableRow < table.getAllRows(); tableRow++) {
						for (int tableCol = 1; tableCol < table.getAllColumns(); tableCol++) {
							Cell cell = checkColumnsParseAnswerSet();
							if (answerSet == null) {
								String v = answerrow.get(answerrowcounter++);

								if (v != null) {
									cell.setCellValue(ConversionTools.removeHTMLNoEscape(v));
								}
							} else {
								String answer = answerSet.getTableAnswer(table, tableRow, tableCol, false);
								if (answer == null)
									answer = "";
								cell.setCellValue(ConversionTools.removeHTMLNoEscape(answer));
							}
						}
					}
				} else if (question instanceof Upload) {
					if (publication == null || publication.getShowUploadedDocuments()) {
						Cell cell = checkColumnsParseAnswerSet();
						StringBuilder cellValue = new StringBuilder();
						String linkValue = "";

						if (answerSet == null) {
							String sfiles = answerrow.get(answerrowcounter++);
							if (sfiles != null && sfiles.length() > 0) {
								String[] files = sfiles.split(";");
								for (String sfile : files) {
									if (sfile.length() > 0) {
										String[] data = sfile.split("\\|");
										File file = new File();
										file.setUid(data[0].trim());
										if (data.length > 1) {
											file.setName(data[1]);
										} else {
											file.setName("unknown" + sfile);
										}

										if (!uploadedFilesByContributionIDAndQuestionUID
												.containsKey(answerrow.get(0))) {
											uploadedFilesByContributionIDAndQuestionUID.put(answerrow.get(0),
													new HashMap<String, List<File>>());
										}
										if (!uploadQuestionNicenames.containsKey(question.getUniqueId())) {
											uploadQuestionNicenames.put(question.getUniqueId(),
													"Upload_" + (uploadQuestionNicenames.size() + 1));
										}
										if (!uploadedFilesByContributionIDAndQuestionUID.get(answerrow.get(0))
												.containsKey(uploadQuestionNicenames.get(question.getUniqueId()))) {
											uploadedFilesByContributionIDAndQuestionUID.get(answerrow.get(0)).put(
													uploadQuestionNicenames.get(question.getUniqueId()),
													new ArrayList<File>());
										}
										uploadedFilesByContributionIDAndQuestionUID.get(answerrow.get(0))
												.get(uploadQuestionNicenames.get(question.getUniqueId())).add(file);

										cellValue.append((cellValue.length() > 0) ? ";" : "").append(file.getName());
										linkValue = answerrow.get(0) + Constants.PATH_DELIMITER
												+ uploadQuestionNicenames.get(question.getUniqueId());
									}
								}
							}
						} else {
							List<Answer> answers = answerSet.getAnswers(question.getUniqueId());
							for (Answer answer : answers) {

								if (filesByAnswer.containsKey(answer.getId())) {
									for (File file : filesByAnswer.get(answer.getId())) {

										if (!uploadedFilesByContributionIDAndQuestionUID
												.containsKey(answerSet.getUniqueCode())) {
											uploadedFilesByContributionIDAndQuestionUID.put(answerSet.getUniqueCode(),
													new HashMap<String, List<File>>());
										}
										if (!uploadQuestionNicenames.containsKey(question.getUniqueId())) {
											uploadQuestionNicenames.put(question.getUniqueId(),
													"Upload_" + (uploadQuestionNicenames.size() + 1));
										}
										if (!uploadedFilesByContributionIDAndQuestionUID.get(answerSet.getUniqueCode())
												.containsKey(uploadQuestionNicenames.get(question.getUniqueId()))) {
											uploadedFilesByContributionIDAndQuestionUID.get(answerSet.getUniqueCode())
													.put(uploadQuestionNicenames.get(question.getUniqueId()),
															new ArrayList<File>());
										}
										uploadedFilesByContributionIDAndQuestionUID.get(answerSet.getUniqueCode())
												.get(uploadQuestionNicenames.get(question.getUniqueId())).add(file);

										cellValue.append((cellValue.length() > 0) ? ";" : "").append(file.getName());
										linkValue = answerSet.getUniqueCode() + Constants.PATH_DELIMITER
												+ uploadQuestionNicenames.get(question.getUniqueId());
									}
								}
							}
						}

						cell.setCellValue(cellValue.toString());
						linkCell(cell, linkValue, createHelper);
					}
				} else if (question instanceof GalleryQuestion) {

					Cell cell = checkColumnsParseAnswerSet();
					GalleryQuestion gallery = (GalleryQuestion) question;

					if (answerSet == null) {
						String v = answerrow.get(answerrowcounter++);

						if (v != null) {
							cell.setCellValue(v);
						}
					} else {
						List<Answer> answers = answerSet.getAnswers(question.getUniqueId());

						StringBuilder cellValue = new StringBuilder();
						boolean first = true;
						for (Answer answer : answers) {
							File file = null;
							if (!StringUtils.isNullOrEmpty(answer.getPossibleAnswerUniqueId())) {
								file = gallery.getFileByUid(answer.getPossibleAnswerUniqueId());
							} else if (!StringUtils.isNullOrEmpty(answer.getValue())) {
								file = gallery.getAllFiles().get(Integer.parseInt(answer.getValue()));
							}

							if (!first)
								cellValue.append(", ");
							try {
								cellValue.append(file.getName());
								first = false;
							} catch (Exception e) {
								logger.error(e.getLocalizedMessage(), e);
							}
						}
						cell.setCellValue(cellValue.toString());
					}
				} else if ((question instanceof NumberQuestion || question instanceof FormulaQuestion)
						&& (export == null || !export.getShowShortnames())) {
					Cell cell = checkColumnsParseAnswerSet();

					CellStyle numberCellStyle;

					if (cellStyles.containsKey(question.getUniqueId())) {
						numberCellStyle = cellStyles.get(question.getUniqueId());
					} else {
						numberCellStyle = wb.createCellStyle();

						String format = "0";

						if (question instanceof NumberQuestion) {
							NumberQuestion numberQuestion = (NumberQuestion) question;
							if (numberQuestion.getDecimalPlaces() > 0) {
								format += ".";
								for (int i = 0; i < numberQuestion.getDecimalPlaces(); i++) {
									format += "0";
								}
							}
						} else {
							FormulaQuestion formulaQuestion = (FormulaQuestion) question;
							if (formulaQuestion.getDecimalPlaces() > 0) {
								format += ".";
								for (int i = 0; i < formulaQuestion.getDecimalPlaces(); i++) {
									format += "0";
								}
							}
						}

						numberCellStyle.setDataFormat(wb.createDataFormat().getFormat(format));

						cellStyles.put(question.getUniqueId(), numberCellStyle);
					}

					if (answerSet == null) {
						String v = answerrow.get(answerrowcounter++);
						if (v != null && v.length() > 0) {
							double cellValue = Double.parseDouble(v);
							cell.setCellValue(cellValue);
							cell.setCellStyle(numberCellStyle);
						}
					} else {
						List<Answer> answers = answerSet.getAnswers(question.getUniqueId());
						double cellValue;
						if (!answers.isEmpty()) {
							cellValue = Double.parseDouble(answers.get(0).getValue());
							cell.setCellValue(cellValue);
							cell.setCellStyle(numberCellStyle);
						}
					}
				} else if (question instanceof DateQuestion && (export == null || !export.getShowShortnames())) {
					Cell cell = checkColumnsParseAnswerSet();

					if (answerSet == null) {
						String v = answerrow.get(answerrowcounter++);
						if (v != null && v.length() > 0) {
							Date cellValue = ConversionTools.getDate(v);
							if (cellValue != null) {
								cell.setCellValue(cellValue);
								cell.setCellStyle(dateCellStyle);
							}
						}
					} else {
						List<Answer> answers = answerSet.getAnswers(question.getUniqueId());

						Date cellValue = null;

						if (!answers.isEmpty()) {
							cellValue = ConversionTools.getDate(answers.get(0).getValue());
							if (cellValue != null) {
								cell.setCellValue(cellValue);
								cell.setCellStyle(dateCellStyle);
							} else {
								logger.info("empty value for question " + question.getId() + " in answer "
										+ answerSet.getId());
							}
						}
					}
				} else {
					Cell cell = checkColumnsParseAnswerSet();

					if (answerSet == null) {
						String v = answerrow.get(answerrowcounter++);

						if (v != null) {
							if (question instanceof FreeTextQuestion) {
								cell.setCellValue(v);
							} else {
								cell.setCellValue(ConversionTools.removeHTMLNoEscape(v));
							}
						}
					} else {
						List<Answer> answers = answerSet.getAnswers(question.getUniqueId());

						StringBuilder cellValue = new StringBuilder();
						for (Answer answer : answers) {

							if (question instanceof FreeTextQuestion) {
								cellValue.append((cellValue.length() > 0) ? ";" : "")
										.append(form.getAnswerTitle(answer));
							} else {
								cellValue.append((cellValue.length() > 0) ? ";" : "")
										.append(ConversionTools.removeHTMLNoEscape(form.getAnswerTitle(answer)));
							}

							if (export != null && export.getShowShortnames()) {
								cellValue.append(" ").append(form.getAnswerShortname(answer));
							}
						}
						int additionalRows = 0;
						while (cellValue != null && cellValue.length() > 32767) {
							String shortCellValue = cellValue.substring(0, 32767);
							cell.setCellValue(shortCellValue);
							additionalRows += 1;
							if (addedRows.size() < additionalRows) {
								Row addedrow = sheet.createRow(rowIndex++);
								addedRows.add(addedrow);
							}
							cell = addedRows.get(additionalRows - 1).createCell(columnIndex % 255);
							cellValue = new StringBuilder(cellValue.substring(32767));
						}
						cell.setCellValue(cellValue.toString());
					}
				}

				if (question.isDelphiElement() && filter.explanationExported(question.getId().toString())) {
					final String questionUid = question.getUniqueId();
					final Cell cell = checkColumnsParseAnswerSet();
					String answerSetUid;
					String explanation;

					if (answerSet == null) {
						answerSetUid = answerrow.get(0);
						explanation = ExportCreatorHelper.retrieveExplanationWithFilesFromReportingAnswer(
								answerrow.get(answerrowcounter++), answerSetUid, questionUid, explanationFilesToExport);
					} else {
						answerSetUid = answerSet.getUniqueCode();
						explanation = ExportCreatorHelper.retrieveExplanationWithFilesFromAnswerSetAndExistingFiles(
								answerSet, explanations, questionUid, explanationFilesOfSurvey,
								explanationFilesToExport);
					}

					final List<File> files = explanationFilesToExport.getFiles(answerSetUid, questionUid);
					explanation = ConversionTools.removeHTMLNoEscape(explanation);
					if (!explanation.isEmpty() && !files.isEmpty()) {
						explanation += "\n";
					}
					final Iterator<File> fileIterator = files.iterator();
					while (fileIterator.hasNext()) {
						final File file = fileIterator.next();
						explanation += ConversionTools.removeHTMLNoEscape(file.getNameForExport());
						if (fileIterator.hasNext()) {
							explanation += ";";
						}
					}
					cell.setCellValue(explanation);

					if (!files.isEmpty()) {
						linkCell(cell, answerSetUid + Constants.PATH_DELIMITER + questionUid, createHelper);
					}

					enableLineBreaksInCell(cell);

					Cell cell2 = checkColumnsParseAnswerSet();
					int likes = Integer.MAX_VALUE;
					if (answerSet == null) {
						String row = answerrow.get(answerrowcounter++);
						if (row != "") {
							likes = Integer.valueOf(row);
						}
					} else {
						//likes = answerExplanationService.getLikesForExplanation(answerSet.getId(), questionUid);
						String key = answerSet.getId() + "-" + questionUid;
						if (likesForExplanations.containsKey(key)) {
							likes = likesForExplanations.get(key);
						}
					}

					if (likes != Integer.MAX_VALUE) {
						cell2.setCellValue((double) likes);
						cell2.setCellType(Cell.CELL_TYPE_NUMERIC);

						DataFormat format = sheet.getWorkbook().createDataFormat();
						CellStyle style = sheet.getWorkbook().createCellStyle();
						style.setDataFormat(format.getFormat("0"));

						cell2.setCellStyle(style);
					}
				}

				if (question.isDelphiElement() && filter.discussionExported(question.getId().toString())) {
					Cell cell = checkColumnsParseAnswerSet();

					String discussion = "";

					if (answerSet == null) {
						discussion = answerrow.get(answerrowcounter++);
					} else if (discussions.containsKey(answerSet.getId())
							&& discussions.get(answerSet.getId()).containsKey(question.getUniqueId())) {
						discussion = discussions.get(answerSet.getId()).get(question.getUniqueId());
					}

					if (!discussion.isEmpty()) {
						cell.setCellValue(ConversionTools.removeInvalidHtmlEntities(discussion));

						enableLineBreaksInCell(cell);
					} else {
						cell.setCellValue("");
					}
				}
			}
		}
		if (publication == null && filter != null) {
			if (filter.exported("invitation")) {
				Cell cell = checkColumnsParseAnswerSet();

				if (form.getSurvey().getSecurity().contains("anonymous")) {
					cell.setCellValue("Anonymous");
					answerrowcounter++;
				} else if (answerSet == null) {
					cell.setCellValue(answerrow.get(answerrowcounter++));
				} else {
					cell.setCellValue(answerSet.getInvitationId() != null ? answerSet.getInvitationId() : "");
				}
			}
			if (filter.exported("case")) {
				Cell cell = checkColumnsParseAnswerSet();

				if (form.getSurvey().getSecurity().contains("anonymous")) {
					cell.setCellValue("Anonymous");
					answerrowcounter++;
				} else if (answerSet == null) {
					cell.setCellValue(answerrow.get(answerrowcounter++));
				} else {
					cell.setCellValue(answerSet.getUniqueCode() != null ? answerSet.getUniqueCode() : "");
				}
			}
			if (filter.exported("user")) {
				Cell cell = checkColumnsParseAnswerSet();

				if (form.getSurvey().getSecurity().contains("anonymous")) {
					cell.setCellValue("Anonymous");
					answerrowcounter++;
				} else if (answerSet == null) {
					cell.setCellValue(answerrow.get(answerrowcounter++));
				} else {
					cell.setCellValue(answerSet.getResponderEmail() != null ? answerSet.getResponderEmail() : "");
				}
			}
			if (filter.exported("created")) {
				Cell cell = checkColumnsParseAnswerSet();

				if (answerSet == null) {
					String v = answerrow.get(answerrowcounter++);
					if (v != null && v.length() > 0) {
						Date cellValue = ConversionTools.getDate(v);
						if (cellValue != null) {
							cell.setCellValue(cellValue);
							cell.setCellStyle(dateCellStyle);
						}
					}
				} else {
					if (answerSet.getDate() != null) {
						cell.setCellValue(answerSet.getDate());
						cell.setCellStyle(dateCellStyle);
					}
				}
			}
			if (filter.exported("updated")) {
				Cell cell = checkColumnsParseAnswerSet();

				if (answerSet == null) {
					String v = answerrow.get(answerrowcounter++);
					if (v != null && v.length() > 0) {
						Date cellValue = ConversionTools.getDate(v);
						if (cellValue != null) {
							cell.setCellValue(cellValue);
							cell.setCellStyle(dateCellStyle);
						}
					}
				} else {
					if (answerSet.getUpdateDate() != null) {
						cell.setCellValue(answerSet.getUpdateDate());
						cell.setCellStyle(dateCellStyle);
					}
				}
			}
			if (filter.exported("languages")) {
				Cell cell = checkColumnsParseAnswerSet();

				if (answerSet == null) {
					cell.setCellValue(answerrow.get(answerrowcounter++));
				} else {
					cell.setCellValue(answerSet.getLanguageCode() != null ? answerSet.getLanguageCode() : "");
				}
			}
		}

		if (form.getSurvey().getIsQuiz()) {
			Cell cell = checkColumnsParseAnswerSet();

			if (answerSet == null) {
				String v = answerrow.get(answerrowcounter);
				if (v != null && v.length() > 0) {
					cell.setCellValue(Integer.parseInt(v));
				} else {
					cell.setCellValue(0);
				}
			} else {
				cell.setCellValue(answerSet.getScore() != null ? answerSet.getScore() : 0);
			}
		}
	}

	private void enableLineBreaksInCell(final Cell cell) {
		if (cswrap == null) {
			cswrap = wb.createCellStyle();
			cswrap.setWrapText(true);
		}
		cell.setCellStyle(cswrap);
	}

	private void linkCell(final Cell cell, final String linkValue, final CreationHelper createHelper) {
		final Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_FILE);
		link.setAddress(linkValue);
		cell.setHyperlink((org.apache.poi.ss.usermodel.Hyperlink) link);
	}

	@Override
	void exportStatistics() throws Exception {
		safeName = WorkbookUtil.createSafeSheetName("Content");
		sheet = wb.createSheet(safeName);

		sheet.setColumnWidth(1, 7000);

		Statistics statistics = form.getStatistics();
		Survey survey = surveyService.getSurveyInOriginalLanguage(form.getSurvey().getId(),
				form.getSurvey().getShortname(), form.getSurvey().getUniqueId());

		ResultFilter filter = export.getResultFilter().copy();

		if (export != null && export.isAllAnswers() && !survey.isMissingElementsChecked()) {
			surveyService.checkAndRecreateMissingElements(survey, filter);
		}

		form.setSurvey(survey);
		String cellValue;

		rowIndex = 0;
		row = sheet.createRow(rowIndex++);

		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("Arial");
		font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		font.setItalic(false);

		Font boldfont = wb.createFont();
		boldfont.setFontHeightInPoints((short) 10);
		boldfont.setFontName("Arial");
		boldfont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldfont.setItalic(false);

		CellStyle boldstyle = wb.createCellStyle();
		boldstyle.setFont(boldfont);

		CellStyle percentStyle = wb.createCellStyle();
		percentStyle.setDataFormat(wb.createDataFormat().getFormat("0.000%"));

		CellStyle numberStyle = wb.createCellStyle();
		numberStyle.setDataFormat(wb.createDataFormat().getFormat("0"));

		Drawing drawing = sheet.createDrawingPatriarch();
		CreationHelper helper = wb.getCreationHelper();

		Set<String> visibleQuestions = null;
		if (filter != null)
			visibleQuestions = filter.getExportedQuestions();
		if (visibleQuestions == null || visibleQuestions.isEmpty())
			visibleQuestions = filter.getVisibleQuestions();

		for (Element question : survey.getQuestionsAndSections()) {

			if (question instanceof Section && survey.getIsDelphi()
					&& filter.visibleSection(question.getId(), survey)) {
				Cell cell = row.createCell(0);
				cell.setCellStyle(boldstyle);
				cell.setCellValue(ConversionTools.removeHTMLNoEscape(question.getTitle()));
				rowIndex++;
				row = sheet.createRow(rowIndex++);
			}

			if (filter == null || visibleQuestions.isEmpty()
					|| visibleQuestions.contains(question.getId().toString())) {

				if (question instanceof ChoiceQuestion) {
					cellValue = question.getTitle();
					if (export.getShowShortnames()) {
						cellValue += " (" + question.getShortname() + ")";
					}

					CreateTableForAnswer(cellValue, boldstyle);
					ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
					for (PossibleAnswer possibleAnswer : choiceQuestion.getAllPossibleAnswers()) {
						row = sheet.createRow(rowIndex++);

						cellValue = ConversionTools.removeHTMLNoEscape(possibleAnswer.getTitle());
						if (export.getShowShortnames()) {
							cellValue += " (" + possibleAnswer.getShortname() + ")";
						}

						row.createCell(0).setCellValue(cellValue);

						Double percent = statistics.getRequestedRecordsPercent().get(possibleAnswer.getId().toString());

						if (percent == null)
							percent = 0.0;

						drawChart(percent, helper, drawing);

						if (statistics.getRequestedRecords().get(possibleAnswer.getId().toString()) != null) {
							row.createCell(2).setCellValue(
									statistics.getRequestedRecords().get(possibleAnswer.getId().toString()));
						}
						Cell pcell = row.createCell(3);
						pcell.setCellValue(percent / 100);
						pcell.setCellStyle(percentStyle);
					}
					row = sheet.createRow(rowIndex++);

					// noanswers
					row.createCell(0).setCellValue("No Answer");

					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());

					if (percent == null)
						percent = 0.0;

					if (percent > 0) {
						drawChart(percent, helper, drawing);
					}
					if (statistics.getRequestedRecords().get(question.getId().toString()) != null) {
						row.createCell(2)
								.setCellValue(statistics.getRequestedRecords().get(question.getId().toString()));
					}
					Cell pcell = row.createCell(3);
					pcell.setCellValue(percent / 100);
					pcell.setCellStyle(percentStyle);

					rowIndex++;
					row = sheet.createRow(rowIndex++);
				} else if (question instanceof GalleryQuestion && ((GalleryQuestion) question).getSelection()) {
					cellValue = question.getTitle();
					if (export.getShowShortnames()) {
						cellValue += " (" + question.getShortname() + ")";
					}

					CreateTableForAnswer(cellValue, boldstyle);
					GalleryQuestion galleryQuestion = (GalleryQuestion) question;
					for (File file : galleryQuestion.getAllFiles()) {
						row = sheet.createRow(rowIndex++);

						cellValue = ConversionTools.removeHTMLNoEscape(file.getName());

						row.createCell(0).setCellValue(cellValue);

						Double percent = statistics.getRequestedRecordsPercent()
								.get(galleryQuestion.getId().toString() + "-" + file.getUid());

						if (percent > 0) {
							drawChart(percent, helper, drawing);
						}

						row.createCell(2).setCellValue(statistics.getRequestedRecords()
								.get(galleryQuestion.getId().toString() + "-" + file.getUid()));

						Cell pcell = row.createCell(3);
						pcell.setCellValue(statistics.getRequestedRecordsPercent()
								.get(galleryQuestion.getId().toString() + "-" + file.getUid()) / 100);
						pcell.setCellStyle(percentStyle);
					}

					row = sheet.createRow(rowIndex++);

					// noanswers
					row.createCell(0).setCellValue("No Answer");

					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());

					if (percent > 0) {
						drawChart(percent, helper, drawing);
					}
					row.createCell(2).setCellValue(statistics.getRequestedRecords().get(question.getId().toString()));

					Cell pcell = row.createCell(3);
					pcell.setCellValue(statistics.getRequestedRecordsPercent().get(question.getId().toString()) / 100);
					pcell.setCellStyle(percentStyle);

					rowIndex++;
					row = sheet.createRow(rowIndex++);
				} else if (question instanceof Matrix) {

					Matrix matrix = (Matrix) question;

					for (Element matrixQuestion : matrix.getQuestions()) {

						cellValue = matrix.getTitle() + ": " + matrixQuestion.getTitle();
						if (export.getShowShortnames()) {
							cellValue += " (" + matrixQuestion.getShortname() + ")";
						}

						CreateTableForAnswer(cellValue, boldstyle);

						for (Element matrixAnswer : matrix.getAnswers()) {
							row = sheet.createRow(rowIndex++);

							cellValue = ConversionTools.removeHTMLNoEscape(matrixAnswer.getTitle());
							if (export.getShowShortnames()) {
								cellValue += " (" + matrixAnswer.getShortname() + ")";
							}

							row.createCell(0).setCellValue(cellValue);

							Double percent = statistics.getRequestedRecordsPercentForMatrix(matrixQuestion,
									matrixAnswer);

							if (percent > 0) {
								drawChart(percent, helper, drawing);
							}

							row.createCell(2).setCellValue(
									statistics.getRequestedRecordsForMatrix(matrixQuestion, matrixAnswer));

							Cell pcell = row.createCell(3);
							pcell.setCellValue(
									statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, matrixAnswer) / 100);
							pcell.setCellStyle(percentStyle);
						}

						row = sheet.createRow(rowIndex++);

						// noanswers
						row.createCell(0).setCellValue("No Answer");

						Double percent = statistics.getRequestedRecordsPercent().get(matrixQuestion.getId().toString());

						if (percent > 0) {
							drawChart(percent, helper, drawing);
						}
						row.createCell(2)
								.setCellValue(statistics.getRequestedRecords().get(matrixQuestion.getId().toString()));

						Cell pcell = row.createCell(3);
						pcell.setCellValue(
								statistics.getRequestedRecordsPercent().get(matrixQuestion.getId().toString()) / 100);
						pcell.setCellStyle(percentStyle);

						rowIndex++;
						row = sheet.createRow(rowIndex++);
					}
				} else if (question instanceof ComplexTable) {

					ComplexTable table = (ComplexTable) question;

					for (ComplexTableItem childQuestion : table.getQuestionChildElements()) {
						boolean isChoice = childQuestion.getCellType() == ComplexTableItem.CellType.SingleChoice
								|| childQuestion.getCellType() == ComplexTableItem.CellType.MultipleChoice;
						boolean hasStatistics = isChoice;
						if (!hasStatistics) {
							if (childQuestion.getCellType() == ComplexTableItem.CellType.Number
									|| childQuestion.getCellType() == ComplexTableItem.CellType.Formula) {
								hasStatistics = childQuestion.showStatisticsForNumberQuestion();
							}
						}

						if (hasStatistics) {
							cellValue = childQuestion.getResultTitle(table);
							if (export.getShowShortnames()) {
								cellValue += " (" + childQuestion.getShortname() + ")";
							}

							CreateTableForAnswer(cellValue, boldstyle);
							if (isChoice) {
								for (PossibleAnswer possibleAnswer : childQuestion.getPossibleAnswers()) {
									row = sheet.createRow(rowIndex++);

									cellValue = ConversionTools.removeHTMLNoEscape(possibleAnswer.getTitle());
									if (export.getShowShortnames()) {
										cellValue += " (" + possibleAnswer.getShortname() + ")";
									}

									row.createCell(0).setCellValue(cellValue);

									Double percent = statistics.getRequestedRecordsPercent()
											.get(possibleAnswer.getId().toString());

									if (percent == null)
										percent = 0.0;

									drawChart(percent, helper, drawing);

									if (statistics.getRequestedRecords()
											.get(possibleAnswer.getId().toString()) != null) {
										row.createCell(2).setCellValue(statistics.getRequestedRecords()
												.get(possibleAnswer.getId().toString()));
									}
									Cell pcell = row.createCell(3);
									pcell.setCellValue(percent / 100);
									pcell.setCellStyle(percentStyle);
								}
							} else {
								for (String answer : childQuestion.getPossibleNumberAnswers()) {
									row = sheet.createRow(rowIndex++);

									cellValue = answer;

									Cell icell = row.createCell(0);
									icell.setCellValue(Integer.parseInt(cellValue));
									icell.setCellStyle(numberStyle);

									Double percent = statistics.getRequestedRecordsPercent()
											.get(childQuestion.getAnswerWithPrefix(answer));

									if (percent > 0) {
										drawChart(percent, helper, drawing);
									}

									row.createCell(2).setCellValue(statistics.getRequestedRecords()
											.get(childQuestion.getAnswerWithPrefix(answer)));

									Cell pcell = row.createCell(3);
									pcell.setCellValue(percent / 100);
									pcell.setCellStyle(percentStyle);
								}
							}
							row = sheet.createRow(rowIndex++);

							// noanswers
							row.createCell(0).setCellValue("No Answer");

							Double percent = statistics.getRequestedRecordsPercent()
									.get(childQuestion.getId().toString());

							if (percent == null)
								percent = 0.0;

							if (percent > 0) {
								drawChart(percent, helper, drawing);
							}
							if (statistics.getRequestedRecords().get(childQuestion.getId().toString()) != null) {
								row.createCell(2).setCellValue(
										statistics.getRequestedRecords().get(childQuestion.getId().toString()));
							}
							Cell pcell = row.createCell(3);
							pcell.setCellValue(percent / 100);
							pcell.setCellStyle(percentStyle);

							rowIndex++;
							row = sheet.createRow(rowIndex++);
						}
					}
				} else if (question instanceof RatingQuestion) {

					RatingQuestion rating = (RatingQuestion) question;

					for (Element childQuestion : rating.getQuestions()) {

						cellValue = rating.getTitle() + ": " + childQuestion.getTitle();
						if (export.getShowShortnames()) {
							cellValue += " (" + childQuestion.getShortname() + ")";
						}

						CreateTableForAnswer(cellValue, boldstyle);

						for (int i = 1; i <= rating.getNumIcons(); i++) {
							row = sheet.createRow(rowIndex++);

							cellValue = i + Constants.PATH_DELIMITER + rating.getNumIcons();

							row.createCell(0).setCellValue(cellValue);

							Double percent = statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i);

							if (percent > 0) {
								drawChart(percent, helper, drawing);
							}

							row.createCell(2)
									.setCellValue(statistics.getRequestedRecordsForRatingQuestion(childQuestion, i));

							Cell pcell = row.createCell(3);
							pcell.setCellValue(
									statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i) / 100);
							pcell.setCellStyle(percentStyle);
						}

						row = sheet.createRow(rowIndex++);

						// noanswers
						row.createCell(0).setCellValue("No Answer");

						Double percent = statistics.getRequestedRecordsPercent().get(childQuestion.getId().toString());

						if (percent > 0) {
							drawChart(percent, helper, drawing);
						}
						row.createCell(2)
								.setCellValue(statistics.getRequestedRecords().get(childQuestion.getId().toString()));

						Cell pcell = row.createCell(3);
						pcell.setCellValue(
								statistics.getRequestedRecordsPercent().get(childQuestion.getId().toString()) / 100);
						pcell.setCellStyle(percentStyle);

						rowIndex++;
						row = sheet.createRow(rowIndex++);
					}
				} else if (question instanceof RankingQuestion) {
					RankingQuestion ranking = (RankingQuestion) question;
					int size = ranking.getChildElements().size();

					Cell cell = row.createCell(0);
					cell.setCellStyle(boldstyle);
					cell.setCellValue(ConversionTools.removeHTMLNoEscape(question.getTitle()));

					row = sheet.createRow(rowIndex++);

					for (int i = 1; i <= size; i++) {
						cell = row.createCell(i + 1);
						cell.setCellValue(i);
					}
					cell = row.createCell(size + 2);
					cell.setCellValue("Score");
					row = sheet.createRow(rowIndex++);

					int total = statistics.getRequestedRecordsRankingScore().get(ranking.getId().toString());

					for (Element childQuestion : ranking.getChildElements()) {
						cellValue = ConversionTools.removeHTMLNoEscape(childQuestion.getTitle());
						if (export.getShowShortnames()) {
							cellValue += " (" + childQuestion.getShortname() + ")";
						}
						cell = row.createCell(0);
						cell.setCellValue(cellValue);

						for (int i = 0; i < size; i++) {
							double percent = statistics.getRequestedRecordsRankingPercentScore()
									.get(childQuestion.getId() + "-" + i);
							cell = row.createCell(i + 2);
							cell.setCellStyle(percentStyle);
							cell.setCellValue(percent / 100);
						}
						double score = statistics.getRequestedRecordsRankingPercentScore()
								.get(childQuestion.getId().toString());
						row.createCell(size + 2).setCellValue(score);

						row = sheet.createRow(rowIndex++);

						for (int i = 0; i < size; i++) {
							int value = statistics.getRequestedRecordsRankingScore()
									.get(childQuestion.getId() + "-" + i);
							row.createCell(i + 2).setCellValue(value);
						}
						row.createCell(size + 2).setCellValue(total);
						row = sheet.createRow(rowIndex++);
					}
					row.createCell(0).setCellValue("No Answer");
					cell = row.createCell(2);
					cell.setCellStyle(percentStyle);
					cell.setCellValue(statistics.getRequestedRecordsPercent().get(ranking.getId().toString()) / 100);
					row = sheet.createRow(rowIndex++);
					row.createCell(2).setCellValue(statistics.getRequestedRecords().get(ranking.getId().toString()));
					row = sheet.createRow(rowIndex++);
				} else if (question instanceof NumberQuestion) {
					NumberQuestion number = (NumberQuestion) question;
					if (number.showStatisticsForNumberQuestion()) {

						cellValue = question.getTitle();
						if (export.getShowShortnames()) {
							cellValue += " (" + question.getShortname() + ")";
						}

						CreateTableForAnswer(cellValue, boldstyle);

						for (String answer : number.getAllPossibleAnswers()) {
							row = sheet.createRow(rowIndex++);

							cellValue = answer;

							Cell icell = row.createCell(0);
							icell.setCellValue(Integer.parseInt(cellValue));
							icell.setCellStyle(numberStyle);

							Double percent = statistics.getRequestedRecordsPercent()
									.get(number.getAnswerWithPrefix(answer));

							if (percent > 0) {
								drawChart(percent, helper, drawing);
							}

							row.createCell(2).setCellValue(
									statistics.getRequestedRecords().get(number.getAnswerWithPrefix(answer)));

							Cell pcell = row.createCell(3);
							pcell.setCellValue(percent / 100);
							pcell.setCellStyle(percentStyle);
						}

						row = sheet.createRow(rowIndex++);

						// noanswers
						row.createCell(0).setCellValue("No Answer");

						Double percent = statistics.getRequestedRecordsPercent().get(number.getId().toString());

						if (percent > 0) {
							drawChart(percent, helper, drawing);
						}
						row.createCell(2).setCellValue(statistics.getRequestedRecords().get(number.getId().toString()));

						Cell pcell = row.createCell(3);
						pcell.setCellValue(
								statistics.getRequestedRecordsPercent().get(number.getId().toString()) / 100);
						pcell.setCellStyle(percentStyle);

						rowIndex++;
						row = sheet.createRow(rowIndex++);
					}
				} else if (question instanceof FormulaQuestion) {
					FormulaQuestion formula = (FormulaQuestion) question;
					if (formula.showStatisticsForNumberQuestion()) {

						cellValue = question.getTitle();
						if (export.getShowShortnames()) {
							cellValue += " (" + question.getShortname() + ")";
						}

						CreateTableForAnswer(cellValue, boldstyle);

						for (String answer : formula.getAllPossibleAnswers()) {
							row = sheet.createRow(rowIndex++);

							cellValue = answer;

							Cell icell = row.createCell(0);
							icell.setCellValue(Integer.parseInt(cellValue));
							icell.setCellStyle(numberStyle);

							Double percent = statistics.getRequestedRecordsPercent()
									.get(formula.getAnswerWithPrefix(answer));

							if (percent > 0) {
								drawChart(percent, helper, drawing);
							}

							row.createCell(2).setCellValue(
									statistics.getRequestedRecords().get(formula.getAnswerWithPrefix(answer)));

							Cell pcell = row.createCell(3);
							pcell.setCellValue(percent / 100);
							pcell.setCellStyle(percentStyle);
						}

						row = sheet.createRow(rowIndex++);

						// noanswers
						row.createCell(0).setCellValue("No Answer");

						Double percent = statistics.getRequestedRecordsPercent().get(formula.getId().toString());

						if (percent > 0) {
							drawChart(percent, helper, drawing);
						}
						row.createCell(2)
								.setCellValue(statistics.getRequestedRecords().get(formula.getId().toString()));

						Cell pcell = row.createCell(3);
						pcell.setCellValue(
								statistics.getRequestedRecordsPercent().get(formula.getId().toString()) / 100);
						pcell.setCellStyle(percentStyle);

						rowIndex++;
						row = sheet.createRow(rowIndex++);
					}
				}
			}
		}
		wb.write(outputStream);
	}

	private void drawChart(double percent, CreationHelper helper, Drawing drawing) throws IOException {
		InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");

		int pictureIdx = wb.addPicture(org.apache.poi.util.IOUtils.toByteArray(pictureData), Workbook.PICTURE_TYPE_PNG);

		ClientAnchor anchor = helper.createClientAnchor();
		// set top-left corner for the image
		anchor.setCol1(1);
		anchor.setCol2(1);
		anchor.setRow1(rowIndex - 1);
		anchor.setRow2(rowIndex - 1);

		anchor.setDx1(15);
		anchor.setDx2((int) (percent / 100 * 1020) - 15);

		anchor.setDy1(50);
		anchor.setDy2(220);

		anchor.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);

		// Creates a picture
		drawing.createPicture(anchor, pictureIdx);
	}

	@Override
	void exportStatisticsQuiz() throws Exception {
	}

	private void CreateTableForAnswer(String title, CellStyle boldstyle) {
		Cell cell = row.createCell(0);
		cell.setCellStyle(boldstyle);
		cell.setCellValue(ConversionTools.removeHTMLNoEscape(title));

		row = sheet.createRow(rowIndex++);

		cell = row.createCell(2);
		cell.setCellValue("Answers");

		cell = row.createCell(3);
		cell.setCellValue("Ratio");
	}

	private int initWorkbook(Sheet sheet, Export export) {
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 5000);

		int rowIndex = 0;
		Row localRow = sheet.createRow(rowIndex++);
		localRow.createCell(0).setCellValue("Alias");

		if (export == null) {
			localRow.createCell(1).setCellValue(form.getSurvey().getShortname());
		} else {
			localRow.createCell(1).setCellValue(export.getSurvey().getShortname());
		}

		localRow = sheet.createRow(rowIndex++);
		localRow.createCell(0).setCellValue("Export Date");

		Cell cell = localRow.createCell(1);
		if (export == null) {
			cell.setCellValue(new Date());
		} else {
			cell.setCellValue(export.getDate());
		}

		cell.setCellStyle(dateStyle);
		sheet.createRow(rowIndex++);
		return rowIndex;
	}

	@Override
	void exportAddressBook() throws Exception {

		User user = administrationService.getUser(userId);

		String safeName = WorkbookUtil.createSafeSheetName("Contacts");
		Sheet sheet = wb.createSheet(safeName);

		int rowIndex = 0;
		int cellIndex = 0;
		Row row;
		Cell cell;

		int ownerId;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2) {
			ownerId = -1;
		} else {
			ownerId = user.getId();
		}

		List<Attendee> attendees = attendeeService.getAttendees(ownerId, export.getResultFilter().getFilterValues(), 1,
				Integer.MAX_VALUE);
		List<AttributeName> configuredattributes = user.getSelectedAttributes();

		try {
			row = sheet.createRow(rowIndex++);
			cell = row.createCell(cellIndex++);
			cell.setCellValue("Name");
			cell = row.createCell(cellIndex++);
			cell.setCellValue("Email");

			for (AttributeName att : configuredattributes) {
				cell = row.createCell(cellIndex++);
				cell.setCellValue(att.getName());
			}
			row = sheet.createRow(rowIndex++);
			cellIndex = 0;

			for (Attendee attendee : attendees) {
				cell = row.createCell(cellIndex++);
				cell.setCellValue(attendee.getName());
				cell = row.createCell(cellIndex++);
				cell.setCellValue(attendee.getEmail());

				for (AttributeName att : configuredattributes) {
					cell = row.createCell(cellIndex++);

					if (att.getName().equals("Owner")) {
						cell.setCellValue(attendee.getOwner() != null ? attendee.getOwner().replace("&#160;", "") : "");
					} else {
						cell.setCellValue(attendee.getAttributeValue(att.getId()).replace("&#160;", ""));
					}
				}

				row = sheet.createRow(rowIndex++);
				cellIndex = 0;
			}

			wb.write(outputStream);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	@Override
	void exportActivities() throws Exception {

		String safeName = WorkbookUtil.createSafeSheetName("Activities");
		Sheet sheet = wb.createSheet(safeName);

		int rowIndex = 0;
		int cellIndex = 0;
		Row row;
		Cell cell;

		List<Activity> activities = activityService.get(export.getActivityFilter(), 1, Integer.MAX_VALUE);

		try {
			row = sheet.createRow(rowIndex++);
			ActivityFilter filter = export.getActivityFilter();
			if (filter.exported("date")) {
				cell = row.createCell(cellIndex++);
				cell.setCellValue("Date");
			}

			if (filter.exported("logid")) {
				cell = row.createCell(cellIndex++);
				cell.setCellValue("LogID");
			}

			if (filter.exported("user")) {
				cell = row.createCell(cellIndex++);
				cell.setCellValue("User");
			}

			if (filter.exported("object")) {
				cell = row.createCell(cellIndex++);
				cell.setCellValue("Object");
			}

			if (filter.exported("property")) {
				cell = row.createCell(cellIndex++);
				cell.setCellValue("Property");
			}

			if (filter.exported("event")) {
				cell = row.createCell(cellIndex++);
				cell.setCellValue("Event");
			}

			if (filter.exported("description")) {
				cell = row.createCell(cellIndex++);
				cell.setCellValue("Description");
			}

			if (filter.exported("oldvalue")) {
				cell = row.createCell(cellIndex++);
				cell.setCellValue("OldValue");
			}

			if (filter.exported("newvalue")) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("NewValue");
			}

			row = sheet.createRow(rowIndex++);
			cellIndex = 0;

			for (Activity activity : activities) {
				if (filter.exported("date")) {
					cell = row.createCell(cellIndex++);
					cell.setCellValue(ConversionTools.getFullString(activity.getDate()));
				}

				if (filter.exported("logid")) {
					cell = row.createCell(cellIndex++);
					cell.setCellValue(activity.getLogID());
				}

				if (filter.exported("user")) {
					cell = row.createCell(cellIndex++);
					activity.setUserName(
							activity.getUserId() > 0 ? administrationService.getUser(activity.getUserId()).getName()
									: "");
					cell.setCellValue(activity.getUserName());
				}

				if (filter.exported("object")) {
					cell = row.createCell(cellIndex++);
					cell.setCellValue(activity.getObject());
				}

				if (filter.exported("property")) {
					cell = row.createCell(cellIndex++);
					cell.setCellValue(
							activity.getProperty() != "PivotLanguage" ? activity.getProperty() : "MainLanguage");
				}

				if (filter.exported("event")) {
					cell = row.createCell(cellIndex++);
					cell.setCellValue(activity.getEvent());
				}

				if (filter.exported("description")) {
					cell = row.createCell(cellIndex++);
					cell.setCellValue(resources.getMessage("logging." + activity.getLogID(), null,
							"logging." + activity.getLogID(), locale));
				}

				if (filter.exported("oldvalue")) {
					cell = row.createCell(cellIndex++);
					cell.setCellValue(activity.getOldValue());
				}

				if (filter.exported("newvalue")) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(activity.getNewValue());
				}

				row = sheet.createRow(rowIndex++);
				cellIndex = 0;
			}

			wb.write(outputStream);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	void exportTokens() throws Exception {

		if (dateCellStyle == null) {
			dateCellStyle = wb.createCellStyle();
			dateCellStyle.setDataFormat((short) 22);
		}

		ParticipationGroup participationGroup = participationService.get(export.getParticipationGroup());

		String safeName = WorkbookUtil.createSafeSheetName("Tokens");
		Sheet sheet = wb.createSheet(safeName);

		if (participationGroup.getType() == ParticipationGroupType.Static
				|| participationGroup.getType() == ParticipationGroupType.ECMembers) {
			Map<Integer, Invitation> invitations = attendeeService
					.getInvitationsByAttendeeForParticipationGroup(export.getParticipationGroup());

			sheet.setColumnWidth(0, 5000);
			sheet.setColumnWidth(1, 8000);
			sheet.setColumnWidth(2, 5000);
			sheet.setColumnWidth(3, 5000);
			sheet.setColumnWidth(4, 5000);

			int rowIndex = 0;
			Row row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue("GuestList ID");
			row.createCell(1).setCellValue(export.getParticipationGroup());

			row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue("NAME");
			row.createCell(1).setCellValue("EMAIL");
			if (!export.getSurvey().isAnonymous()) {
				row.createCell(2).setCellValue("INVITATION DATE");
				row.createCell(3).setCellValue("REMINDER DATE");
				row.createCell(4).setCellValue("ANSWERS");
			}

			if (participationGroup.getType() == ParticipationGroupType.Static) {
				for (Attendee attendee : participationGroup.getAttendees()) {
					row = sheet.createRow(rowIndex++);

					Invitation invitation = invitations.get(attendee.getId());
					if (invitation == null) {
						invitation = new Invitation(participationGroup.getId(), attendee.getId());
						attendeeService.add(invitation);
					}

					row.createCell(0).setCellValue(attendee.getName());
					row.createCell(1).setCellValue(attendee.getEmail());
					if (!export.getSurvey().isAnonymous()) {
						row.createCell(2).setCellValue(invitation.getInvited());
						if (invitation.getReminded() != null) {
							row.createCell(3).setCellValue(invitation.getReminded());
						} else {
							row.createCell(3);
						}
						row.createCell(4).setCellValue(invitation.getAnswers());

						row.getCell(2).setCellStyle(dateCellStyle);
						row.getCell(3).setCellStyle(dateCellStyle);
					}
				}
			} else {
				for (EcasUser attendee : participationGroup.getEcasUsers()) {
					row = sheet.createRow(rowIndex++);

					Invitation invitation = invitations.get(attendee.getId());
					if (invitation == null) {
						invitation = new Invitation(participationGroup.getId(), attendee.getId());
						attendeeService.add(invitation);
					}

					row.createCell(0).setCellValue(attendee.getName());
					row.createCell(1).setCellValue(attendee.getEmail());
					if (!export.getSurvey().isAnonymous()) {
						row.createCell(2).setCellValue(invitation.getInvited());
						if (invitation.getReminded() != null) {
							row.createCell(3).setCellValue(invitation.getReminded());
						} else {
							row.createCell(3);
						}
						row.createCell(4).setCellValue(invitation.getAnswers());

						row.getCell(2).setCellStyle(dateCellStyle);
						row.getCell(3).setCellStyle(dateCellStyle);
					}
				}
			}
		} else {

			List<Invitation> invitations = attendeeService
					.getInvitationsForParticipationGroup(export.getParticipationGroup());

			sheet.setColumnWidth(0, 11000);
			sheet.setColumnWidth(1, 11000);
			sheet.setColumnWidth(2, 11000);

			int rowIndex = 0;
			Row row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue("Survey");
			row.createCell(1).setCellValue(export.getSurvey().getUniqueId());
			row.createCell(2).setCellValue(export.getSurvey().getShortname());

			row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue("Guestlist");
			row.createCell(1).setCellValue(export.getParticipationGroup());
			ParticipationGroup group = participationService.get(export.getParticipationGroup());
			row.createCell(2).setCellValue(ConversionTools.getFullString(group.getCreated()));

			rowIndex++;
			row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue("Token");
			if (!export.getSurvey().isAnonymous()) {
				row.createCell(1).setCellValue("Answers");
				row.createCell(2).setCellValue("Creation date");

				for (Invitation invitation : invitations) {
					row = sheet.createRow(rowIndex++);
					row.createCell(0).setCellValue(invitation.getUniqueId());
					row.createCell(1).setCellValue(invitation.getAnswers());
					row.createCell(2).setCellValue(invitation.getInvited());
					row.getCell(2).setCellStyle(dateCellStyle);
				}
			} else {
				row.createCell(1).setCellValue("Creation date");
				for (Invitation invitation : invitations) {
					row = sheet.createRow(rowIndex++);
					row.createCell(0).setCellValue(invitation.getUniqueId());
					row.createCell(1).setCellValue(invitation.getInvited());
					row.getCell(1).setCellStyle(dateCellStyle);
				}
			}
		}
		wb.write(outputStream);
	}

	@Override
	void exportECFGlobalResults() throws Exception {
		Survey survey = surveyService.getSurveyInOriginalLanguage(form.getSurvey().getId(),
				form.getSurvey().getShortname(), form.getSurvey().getUniqueId());
		ResultFilter resultFilter = this.export.getResultFilter();

		int pageNumber = 1;
		int pageSize = Integer.MAX_VALUE;

		SqlPagination sqlPagination = new SqlPagination(pageNumber, pageSize);
		ECFGlobalResult globalResult = this.ecfService.getECFGlobalResult(survey, sqlPagination, resultFilter);
		exportOneECFGlobalResultSheet(globalResult);

		while (globalResult.getNumberOfPages() > pageNumber) {
			pageNumber++;
			sqlPagination = new SqlPagination(pageNumber, pageSize);
			globalResult = this.ecfService.getECFGlobalResult(survey, sqlPagination, resultFilter);
			exportOneECFGlobalResultSheet(globalResult);
		}

		try {
			wb.write(outputStream);
			outputStream.close();
		} catch (IOException e) {
			logger.error(e.getCause());
		} finally {
			wb.close();
			outputStream.close();
		}

	}

	private void exportOneECFGlobalResultSheet(ECFGlobalResult globalResult) {
		String exportSheetName = resources.getMessage("label.ECF.Results", null,
				"Individual Assessment Results and gaps", locale);
		safeName = WorkbookUtil
				.createSafeSheetName(exportSheetName.substring(0, 29) + " " + globalResult.getPageNumber());
		sheet = wb.createSheet(safeName);
		sheets.add(sheet);

		// TABLE HEADER
		// FIRST ROW
		row = sheet.createRow(rowIndex++);
		columnIndex = 0;

		String competenceLabel = resources.getMessage("label.ECF.Competence", null, "Competence", locale);
		Cell firstCellFirstRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		firstCellFirstRow.setCellValue(competenceLabel);

		String targetLabel = resources.getMessage("label.ECF.Target", null, "Target", locale);
		Cell secondCellFirstRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		secondCellFirstRow.setCellValue(targetLabel);
		boolean displayGap = (globalResult.getTotalResults().getTotalGaps() != null && globalResult.getTotalResults()
				.getTotalScores().size() == globalResult.getTotalResults().getTotalGaps().size());

		for (String participantName : globalResult.getIndividualResults().get(0).getParticipantsNames()) {
			Cell participantCell = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
			participantCell.setCellValue("Score for " + participantName);

			if (displayGap) {
				Cell participantGapCell = row.createCell(columnIndex++,
						org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				participantGapCell.setCellValue("Gap for " + participantName);
			}
		}
		// SECOND ROW
		row = sheet.createRow(rowIndex++);
		columnIndex = 0;

		String totalLabel = resources.getMessage("label.TotalUpperCase", null, "TOTAL", locale);
		Cell firstCellSecondRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		firstCellSecondRow.setCellValue(totalLabel);

		Cell secondCellSecondRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
		if (globalResult.getTotalResults() != null && globalResult.getTotalResults().getTotalTargetScore() != null) {
			secondCellSecondRow.setCellValue(globalResult.getTotalResults().getTotalTargetScore());
		} else {
			secondCellSecondRow.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK);
		}

		for (int i = 0; i < globalResult.getTotalResults().getTotalScores().size(); i++) {
			Integer totalScore = globalResult.getTotalResults().getTotalScores().get(i);
			Cell totalScoreCell = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
			totalScoreCell.setCellValue(totalScore);

			if (displayGap) {
				Integer totalGap = globalResult.getTotalResults().getTotalGaps().get(i);
				Cell totalGapCell = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
				totalGapCell.setCellValue(totalGap);
			}
		}

		for (ECFGlobalCompetencyResult competencyResult : globalResult.getIndividualResults()) {
			row = sheet.createRow(rowIndex++);
			columnIndex = 0;

			Cell competencyCell = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
			competencyCell.setCellValue(competencyResult.getCompetencyName());

			Cell targetScoreCell = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
			if (competencyResult.getCompetencyTargetScore() != null) {
				targetScoreCell.setCellValue(competencyResult.getCompetencyTargetScore());
			} else {
				targetScoreCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK);
			}

			displayGap = displayGap && (competencyResult.getCompetencyScoreGaps() != null && competencyResult
					.getCompetencyScoreGaps().size() == competencyResult.getCompetencyScores().size());
			for (int i = 0; i < competencyResult.getCompetencyScores().size(); i++) {
				Integer score = competencyResult.getCompetencyScores().get(i);
				Cell scoreCell = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
				scoreCell.setCellValue(score);

				if (displayGap) {
					Cell gapCell = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
					Integer gap = competencyResult.getCompetencyScoreGaps().get(i);
					gapCell.setCellValue(gap);
				}
			}
		}
	}

	@Override
	void exportECFProfileResults() throws Exception {
		Survey survey = surveyService.getSurveyInOriginalLanguage(form.getSurvey().getId(),
				form.getSurvey().getShortname(), form.getSurvey().getUniqueId());
		ResultFilter resultFilter = this.export.getResultFilter();
		ECFProfileResult ecfProfileResult = this.ecfService.getECFProfileResult(survey, resultFilter);

		String exportSheetName = resources.getMessage("label.ECF.Results2", null, "Profile assessment results and gaps",
				locale);
		safeName = WorkbookUtil.createSafeSheetName(exportSheetName);
		sheet = wb.createSheet(safeName);
		sheets.add(sheet);

		// TABLE HEADER
		// FIRST ROW
		row = sheet.createRow(rowIndex++);
		columnIndex = 0;

		String competenciesLabel = resources.getMessage("label.ECF.Competencies", null, "Competencies", locale);
		Cell firstCellFirstRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		firstCellFirstRow.setCellValue(competenciesLabel);

		String targetLabel = resources.getMessage("label.ECF.Target", null, "Target", locale);
		Cell secondCellFirstRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		secondCellFirstRow.setCellValue(targetLabel);

		String averageLabel = resources.getMessage("label.ECF.Average", null, "Average score", locale);
		Cell thirdCellFirstRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		thirdCellFirstRow.setCellValue(averageLabel);

		String maxLabel = resources.getMessage("label.ECF.Max", null, "Max", locale);
		Cell fourthCellFirstRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		fourthCellFirstRow.setCellValue(maxLabel);

		for (ECFProfileCompetencyResult competencyResult : ecfProfileResult.getCompetencyResults()) {
			row = sheet.createRow(rowIndex++);
			columnIndex = 0;

			Cell firstCellSecondRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
			firstCellSecondRow.setCellValue(competencyResult.getCompetencyName());

			Cell secondCellSecondRow = row.createCell(columnIndex++,
					org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
			if (competencyResult.getCompetencyTargetScore() != null
					&& competencyResult.getCompetencyTargetScore() != 0) {
				secondCellSecondRow.setCellValue(competencyResult.getCompetencyTargetScore());
			} else {
				secondCellSecondRow.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK);
			}

			Cell thirdCellSecondRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
			thirdCellSecondRow.setCellValue(competencyResult.getCompetencyAverageScore());

			boolean displayGap = competencyResult.getCompetencyScoreGap() != null;

			if (displayGap) {
				Cell fourthCellSecondRow = row.createCell(columnIndex++,
						org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				String plusOrMinus = competencyResult.getCompetencyScoreGap() > 0 ? "+" : "";
				fourthCellSecondRow.setCellValue(competencyResult.getCompetencyMaxScore() + " (" + plusOrMinus
						+ competencyResult.getCompetencyScoreGap() + ")");
			} else {
				Cell fourthCellSecondRow = row.createCell(columnIndex++,
						org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
				fourthCellSecondRow.setCellValue(competencyResult.getCompetencyMaxScore());
			}
		}

		try {
			wb.write(outputStream);
			outputStream.close();
		} catch (IOException e) {
			logger.error(e.getCause());
		} finally {
			wb.close();
			outputStream.close();
		}
	}

	@Override
	void exportECFOrganizationalResults() throws Exception {
		Survey survey = surveyService.getSurveyInOriginalLanguage(form.getSurvey().getId(),
				form.getSurvey().getShortname(), form.getSurvey().getUniqueId());
		ECFOrganizationalResult organizationalResult = this.ecfService.getECFOrganizationalResult(survey);

		String exportSheetName = resources.getMessage("label.ECF.Results3", null,
				"Average and maximum score for all profiles", locale);
		safeName = WorkbookUtil.createSafeSheetName(exportSheetName);
		sheet = wb.createSheet(safeName);
		sheets.add(sheet);

		// TABLE HEADER
		// FIRST ROW
		row = sheet.createRow(rowIndex++);
		columnIndex = 0;

		String competenciesLabel = resources.getMessage("label.ECF.Competencies", null, "Competencies", locale);
		Cell firstCellFirstRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		firstCellFirstRow.setCellValue(competenciesLabel);

		String averageTargetLabel = resources.getMessage("label.ECF.AverageTarget", null, "Average target", locale);
		Cell secondCellFirstRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		secondCellFirstRow.setCellValue(averageTargetLabel);

		String averageScoreLabel = resources.getMessage("label.ECF.Average", null, "Average score", locale);
		Cell thirdCellFirstRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		thirdCellFirstRow.setCellValue(averageScoreLabel);

		String maxTargetLabel = resources.getMessage("label.ECF.MaxTarget", null, "Max target", locale);
		Cell fourthCellFirstRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		fourthCellFirstRow.setCellValue(maxTargetLabel);

		String maxLabel = resources.getMessage("label.ECF.Max", null, "Max", locale);
		Cell fifthCellFirstRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
		fifthCellFirstRow.setCellValue(maxLabel);

		for (ECFOrganizationalCompetencyResult competencyResult : organizationalResult.getCompetencyResults()) {
			row = sheet.createRow(rowIndex++);
			columnIndex = 0;

			Cell firstCellSecondRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
			firstCellSecondRow.setCellValue(competencyResult.getCompetencyName());

			Cell secondCellSecondRow = row.createCell(columnIndex++,
					org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
			secondCellSecondRow.setCellValue(competencyResult.getCompetencyAverageTarget());

			Cell thirdCellSecondRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
			thirdCellSecondRow.setCellValue(competencyResult.getCompetencyAverageScore());

			Cell fourthCellSecondRow = row.createCell(columnIndex++,
					org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
			fourthCellSecondRow.setCellValue(competencyResult.getCompetencyMaxTarget());

			Cell fifthCellSecondRow = row.createCell(columnIndex++, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
			fifthCellSecondRow.setCellValue(competencyResult.getCompetencyMaxScore());
		}

		try {
			wb.write(outputStream);
			outputStream.close();
		} catch (IOException e) {
			logger.error(e.getCause());
		} finally {
			wb.close();
			outputStream.close();
		}
	}
	
	@Override
	void exportPDFReport() throws Exception {
		throw new NotImplementedException();
	}
}
