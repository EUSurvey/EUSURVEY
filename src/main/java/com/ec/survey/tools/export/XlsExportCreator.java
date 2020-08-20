package com.ec.survey.tools.export;

import com.ec.survey.model.*;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.AttributeName;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.hibernate.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

@Service("xlsExportCreator")
@Scope("prototype")
public class XlsExportCreator extends ExportCreator {

	CellStyle dateStyle;
	CellStyle questionTitleStyle;
	Workbook wb;

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
			InitWorkbook(sheetInsertHeader, export);
			sheets.add(sheetInsertHeader);
			rowInsertHeader = sheetInsertHeader.createRow(rowIndexInsertHeader - 1);
		}
	}

	private int columnIndexInsertHeader;
	private int rowIndexInsertHeader;
	private Sheet sheetInsertHeader;
	private Row rowInsertHeader;

	private int insertHeader(List<Sheet> sheets, Publication publication, ResultFilter filter, Export export) {
		sheetInsertHeader = sheets.get(0);
		rowIndexInsertHeader = InitWorkbook(sheetInsertHeader, export);

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
	void ExportContent(boolean sync) throws Exception {
		ExportContent(null, sync);
	}

	int rowIndex;
	int columnIndex;
	int counter;
	String safeName;
	List<Sheet> sheets;
	int fileCounter;

	public void ExportContent(Publication publication, boolean sync) throws Exception {
		sheets = new ArrayList<>();

		safeName = WorkbookUtil.createSafeSheetName("Content");
		counter = 0;
		fileCounter = 0;
		Sheet sheet = wb.createSheet(safeName);
		sheets.add(sheet);

		ResultFilter filter;

		if (publication != null && export == null) {
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
			surveyService.CheckAndRecreateMissingElements(survey, filter);
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
		for (String question : filter.getExportedQuestions()) {
			filter.getVisibleQuestions().add(question);
		}

		List<List<String>> answersets = reportingService.getAnswerSets(survey, filter, null, false, true,
				publication == null || publication.getShowUploadedDocuments(), false, false);
		List<Question> questions = form.getSurvey().getQuestions();

		if (answersets != null) {
			for (List<String> row : answersets) {
				parseAnswerSet(null, row, publication, filter, filesByAnswer, export, questions,
						uploadedFilesByCodeAndQuestionUID, uploadQuestionNicenames);
			}
		} else {

			String sql = "select ans.ANSWER_SET_ID, a.QUESTION_ID, a.QUESTION_UID, a.VALUE, a.ANSWER_COL, a.ANSWER_ID, a.ANSWER_ROW, a.PA_ID, a.PA_UID, ans.UNIQUECODE, ans.ANSWER_SET_DATE, ans.ANSWER_SET_UPDATE, ans.ANSWER_SET_INVID, ans.RESPONDER_EMAIL, ans.ANSWER_SET_LANG, ans.SCORE FROM ANSWERS a RIGHT JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID IN ("
					+ answerService.getSql(null, form.getSurvey().getId(), filter, values, true)
					+ ") ORDER BY ans.ANSWER_SET_ID";

			SQLQuery query = session.createSQLQuery(sql);

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
					answer.setQuestionId(ConversionTools.getValue(a[1]));
					answer.setQuestionUniqueId((String) a[2]);
					answer.setValue((String) a[3]);
					answer.setColumn(ConversionTools.getValue(a[4]));
					answer.setId(ConversionTools.getValue(a[5]));
					answer.setRow(ConversionTools.getValue(a[6]));
					answer.setPossibleAnswerId(ConversionTools.getValue(a[7]));
					answer.setPossibleAnswerUniqueId((String) a[8]);

					if (lastAnswerSet == answer.getAnswerSetId()) {
						answerSet.addAnswer(answer);
					} else {
						if (lastAnswerSet > 0) {
							session.flush();
							parseAnswerSet(answerSet, null, publication, filter, filesByAnswer, export, questions,
									uploadedFilesByCodeAndQuestionUID, uploadQuestionNicenames);
						}

						answerSet = new AnswerSet();
						answerSet.setSurvey(survey);
						answerSet.setId(answer.getAnswerSetId());
						lastAnswerSet = answer.getAnswerSetId();
						answerSet.getAnswers().add(answer);
						answerSet.setDate((Date) a[10]);
						answerSet.setUpdateDate((Date) a[11]);
						answerSet.setUniqueCode((String) a[9]);
						answerSet.setInvitationId((String) a[12]);
						answerSet.setResponderEmail((String) a[13]);
						answerSet.setLanguageCode((String) a[14]);
						answerSet.setScore(ConversionTools.getValue(a[15]));
					}
				}
				if (lastAnswerSet > 0)
					parseAnswerSet(answerSet, null, publication, filter, filesByAnswer, export, questions,
							uploadedFilesByCodeAndQuestionUID, uploadQuestionNicenames);
			} finally {
				results.close();
			}

		}

		if (!sync) {
			t.commit();
			session.close();
		}

		wb.write(outputStream);

		if (fileCounter > 0 || !uploadedFiles.isEmpty()) {
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
								os.putArchiveEntry(
										new ZipArchiveEntry(code + Constants.PATH_DELIMITER + questionNiceName + Constants.PATH_DELIMITER + file.getName()));
								IOUtils.copy(new FileInputStream(f), os);
								os.closeArchiveEntry();
							}
						}
					}
				}

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

	private int lastSheet;
	private Sheet sheet;
	private Row row;

	CellStyle dateCellStyle = null;

	private void parseAnswerSet(AnswerSet answerSet, List<String> answerrow, Publication publication,
			ResultFilter filter, Map<Integer, List<File>> filesByAnswer, Export export, List<Question> questions,
			Map<String, Map<String, List<File>>> uploadedFilesByContributionIDAndQuestionUID,
			Map<String, String> uploadQuestionNicenames) throws IOException {
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

			outputStream = new FileOutputStream(exportFilePath.replace("." + ext, "_" + fileCounter + "." + ext));

			initWorkbook();

			sheets.clear();
			sheet = wb.createSheet(safeName);
			sheets.add(sheet);
			rowIndex = insertHeader(sheets, publication, filter, export);

			dateCellStyle = null;
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
					&& question.isUsedInResults())
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
							List<Answer> answers = answerSet.getAnswers(matrixQuestion.getId(),
									matrixQuestion.getUniqueId());
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
							List<Answer> answers = answerSet.getAnswers(childQuestion.getId(),
									childQuestion.getUniqueId());

							StringBuilder cellValue = new StringBuilder();
							if (!answers.isEmpty()) {
								cellValue.append((cellValue.length() > 0) ? ";" : "")
										.append(ConversionTools.removeHTMLNoEscape(answers.get(0).getValue()));
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
										file.setUid(data[0]);
										file.setName(data[1]);

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
							List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
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
						Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_FILE);
						link.setAddress(linkValue);
						cell.setHyperlink((org.apache.poi.ss.usermodel.Hyperlink) link);
					}
				} else if (question instanceof GalleryQuestion) {

					Cell cell = checkColumnsParseAnswerSet();

					if (answerSet == null) {
						String v = answerrow.get(answerrowcounter++);

						if (v != null) {
							cell.setCellValue(v);
						}
					} else {
						List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());

						StringBuilder cellValue = new StringBuilder();
						boolean first = true;
						for (Answer answer : answers) {

							if (answer.getValue() != null && answer.getValue().length() > 0) {
								int index = Integer.parseInt(answer.getValue());
								if (!first)
									cellValue.append(", ");
								try {
									cellValue.append(((GalleryQuestion) question).getFiles().get(index).getName());
									first = false;
								} catch (Exception e) {
									logger.error(e.getLocalizedMessage(), e);
								}
							}
						}
						cell.setCellValue(cellValue.toString());
					}
				} else if (question instanceof NumberQuestion && (export == null || !export.getShowShortnames())) {
					Cell cell = checkColumnsParseAnswerSet();
					if (answerSet == null) {
						String v = answerrow.get(answerrowcounter++);
						if (v != null && v.length() > 0) {
							double cellValue = Double.parseDouble(v);
							cell.setCellValue(cellValue);
						}
					} else {
						List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
						double cellValue;
						if (!answers.isEmpty()) {
							cellValue = Double.parseDouble(answers.get(0).getValue());
							cell.setCellValue(cellValue);
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
						List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());

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
						List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());

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

	@Override
	void ExportStatistics() throws Exception {
		safeName = WorkbookUtil.createSafeSheetName("Content");
		sheet = wb.createSheet(safeName);

		sheet.setColumnWidth(1, 7000);

		Statistics statistics = form.getStatistics();
		Survey survey = surveyService.getSurveyInOriginalLanguage(form.getSurvey().getId(),
				form.getSurvey().getShortname(), form.getSurvey().getUniqueId());

		ResultFilter filter = export.getResultFilter().copy();

		if (export != null && export.isAllAnswers() && !survey.isMissingElementsChecked()) {
			surveyService.CheckAndRecreateMissingElements(survey, filter);
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

		Drawing drawing = sheet.createDrawingPatriarch();
		CreationHelper helper = wb.getCreationHelper();

		Set<String> visibleQuestions = null;
		if (filter != null)
			visibleQuestions = filter.getExportedQuestions();
		if (visibleQuestions == null || visibleQuestions.isEmpty())
			visibleQuestions = filter.getVisibleQuestions();

		for (Element question : survey.getQuestionsAndSections()) {

			if (filter == null || visibleQuestions.isEmpty() || visibleQuestions.contains(question.getId().toString())) {
				if (question instanceof Section) {
					Cell cell = row.createCell(0);
					cell.setCellStyle(boldstyle);
					cell.setCellValue(ConversionTools.removeHTMLNoEscape(question.getTitle()));
					rowIndex++;
					row = sheet.createRow(rowIndex++);
				}

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

						InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");

						int pictureIdx = wb.addPicture(org.apache.poi.util.IOUtils.toByteArray(pictureData),
								Workbook.PICTURE_TYPE_PNG);

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
						InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");

						int pictureIdx = wb.addPicture(org.apache.poi.util.IOUtils.toByteArray(pictureData),
								Workbook.PICTURE_TYPE_PNG);

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
					for (int i = 0; i < galleryQuestion.getFiles().size(); i++) {
						row = sheet.createRow(rowIndex++);

						cellValue = ConversionTools.removeHTMLNoEscape(galleryQuestion.getFiles().get(i).getName());

						row.createCell(0).setCellValue(cellValue);

						Double percent = statistics.getRequestedRecordsPercent()
								.get(galleryQuestion.getId().toString() + "-" + i);

						if (percent > 0) {

							InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");

							int pictureIdx = wb.addPicture(org.apache.poi.util.IOUtils.toByteArray(pictureData),
									Workbook.PICTURE_TYPE_PNG);

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

						row.createCell(2).setCellValue(
								statistics.getRequestedRecords().get(galleryQuestion.getId().toString() + "-" + i));

						Cell pcell = row.createCell(3);
						pcell.setCellValue(statistics.getRequestedRecordsPercent()
								.get(galleryQuestion.getId().toString() + "-" + i) / 100);
						pcell.setCellStyle(percentStyle);
					}

					row = sheet.createRow(rowIndex++);

					// noanswers
					row.createCell(0).setCellValue("No Answer");

					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());

					if (percent > 0) {
						InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");

						int pictureIdx = wb.addPicture(org.apache.poi.util.IOUtils.toByteArray(pictureData),
								Workbook.PICTURE_TYPE_PNG);

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
								InputStream pictureData = servletContext
										.getResourceAsStream("/resources/images/chart.png");

								int pictureIdx = wb.addPicture(org.apache.poi.util.IOUtils.toByteArray(pictureData),
										Workbook.PICTURE_TYPE_PNG);

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
							InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");

							int pictureIdx = wb.addPicture(org.apache.poi.util.IOUtils.toByteArray(pictureData),
									Workbook.PICTURE_TYPE_PNG);

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
						row.createCell(2)
								.setCellValue(statistics.getRequestedRecords().get(matrixQuestion.getId().toString()));

						Cell pcell = row.createCell(3);
						pcell.setCellValue(
								statistics.getRequestedRecordsPercent().get(matrixQuestion.getId().toString()) / 100);
						pcell.setCellStyle(percentStyle);

						rowIndex++;
						row = sheet.createRow(rowIndex++);
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
								InputStream pictureData = servletContext
										.getResourceAsStream("/resources/images/chart.png");

								int pictureIdx = wb.addPicture(org.apache.poi.util.IOUtils.toByteArray(pictureData),
										Workbook.PICTURE_TYPE_PNG);

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
							InputStream pictureData = servletContext.getResourceAsStream("/resources/images/chart.png");

							int pictureIdx = wb.addPicture(org.apache.poi.util.IOUtils.toByteArray(pictureData),
									Workbook.PICTURE_TYPE_PNG);

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
						row.createCell(2)
								.setCellValue(statistics.getRequestedRecords().get(childQuestion.getId().toString()));

						Cell pcell = row.createCell(3);
						pcell.setCellValue(
								statistics.getRequestedRecordsPercent().get(childQuestion.getId().toString()) / 100);
						pcell.setCellStyle(percentStyle);

						rowIndex++;
						row = sheet.createRow(rowIndex++);
					}
				}
			}
		}
		wb.write(outputStream);
	}

	@Override
	void ExportStatisticsQuiz() throws Exception {
	}

	private void CreateTableForAnswer(String title, CellStyle boldstyle) {
		Cell cell = row.createCell(0); // TODO: bold
		cell.setCellStyle(boldstyle);
		cell.setCellValue(ConversionTools.removeHTMLNoEscape(title));

		row = sheet.createRow(rowIndex++);

		cell = row.createCell(2);
		cell.setCellValue("Answers");

		cell = row.createCell(3);
		cell.setCellValue("Ratio");
	}

	private int InitWorkbook(Sheet sheet, Export export) {

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
		sheet.createRow(rowIndex);
		return rowIndex;
	}

	DecimalFormat df = new DecimalFormat("#.##");

	@Override
	void ExportAddressBook() throws Exception {

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
	void ExportActivities() throws Exception {

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
					cell.setCellValue(activity.getProperty());
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
	void ExportTokens() throws Exception {

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
}
