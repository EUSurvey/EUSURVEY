package com.ec.survey.tools.export;

import com.ec.survey.model.*;
import com.ec.survey.model.Export.ExportFormat;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.AttributeName;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.model.FilesByTypes;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.SqlQueryService;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.mysql.cj.util.StringUtils;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.*;
import org.hibernate.query.Query;
import org.hibernate.query.NativeQuery;
import org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTableProperties;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.chart.Chart;
import org.odftoolkit.simple.chart.ChartType;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.draw.Textbox;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.LineType;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

@Service("odfExportCreator")
@Scope("prototype")
public class OdfExportCreator extends ExportCreator {

	@Autowired
	private SqlQueryService sqlQueryService;

	int CreateChart(SpreadsheetDocument spreadsheet, String title, String[] labels, double[] stats, int xPos) {
		String[] legends = new String[] { "Percent" };
		double[][] data = new double[][] { stats };

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
	void exportContent(boolean sync) throws Exception {
		ExportContent(null, sync);
	}

	public void ExportContent(Publication publication, boolean sync) throws Exception {
		ExportContent(publication, sync, null);
	}

	private void initHeader(Publication publication, ResultFilter filter, Export export) {
		String cellValue;
		for (Question question : questions) {
			if (publication != null || filter.exported(question.getId().toString()))
				if (publication == null || publication.isAllQuestions() || publication.isSelected(question.getId())) {
					if (question.isUsedInResults()) {
						if (question instanceof Matrix) {
							Matrix matrix = (Matrix) question;
							for (Element matrixQuestion : matrix.getQuestions()) {
								cell = sheet.getCellByPosition(columnIndex++, rowIndex);

								cellValue = ConversionTools
										.removeHTMLNoEscape(matrix.getTitle() + ": " + matrixQuestion.getTitle());
								if (export != null && export.getShowShortnames()) {
									cellValue += " " + matrixQuestion.getShortname();
								}
								cell.setStringValue(cellValue);
								Font font = cell.getFont();
								font.setSize(10);
								font.setFontStyle(FontStyle.BOLD);
								cell.setFont(font);
							}
						} else if (question instanceof RatingQuestion) {
							RatingQuestion rating = (RatingQuestion) question;
							for (Element childQuestion : rating.getQuestions()) {
								cell = sheet.getCellByPosition(columnIndex++, rowIndex);
								cellValue = ConversionTools.removeHTMLNoEscape(ConversionTools
										.removeHTMLNoEscape(rating.getTitle() + ": " + childQuestion.getTitle()));
								if (export != null && export.getShowShortnames()) {
									cellValue += " (" + childQuestion.getShortname() + ")";
								}
								cell.setStringValue(cellValue);
								Font font = cell.getFont();
								font.setSize(10);
								font.setFontStyle(FontStyle.BOLD);
								cell.setFont(font);
							}
						} else if (question instanceof ComplexTable) {
							ComplexTable table = (ComplexTable) question;
							for (ComplexTableItem childQuestion : table.getQuestionChildElements()) {
								cell = sheet.getCellByPosition(columnIndex++, rowIndex);
								cellValue = ConversionTools.removeHTMLNoEscape(
										ConversionTools.removeHTMLNoEscape(childQuestion.getResultTitle(table)));
								if (export != null && export.getShowShortnames()) {
									cellValue += " (" + childQuestion.getShortname() + ")";
								}
								cell.setStringValue(cellValue);
								Font font = cell.getFont();
								font.setSize(10);
								font.setFontStyle(FontStyle.BOLD);
								cell.setFont(font);
							}
						} else if (question instanceof Table) {
							Table table = (Table) question;
							for (Element tableQuestion : table.getQuestions()) {
								for (Element tableAnswer : table.getAnswers()) {
									cell = sheet.getCellByPosition(columnIndex++, rowIndex);

									cellValue = ConversionTools.removeHTMLNoEscape(table.getTitle() + " "
											+ tableQuestion.getTitle() + ":" + tableAnswer.getTitle());
									if (export != null && export.getShowShortnames()) {
										cellValue = ConversionTools.removeHTMLNoEscape(table.getTitle() + " "
												+ tableQuestion.getTitle() + " (" + tableQuestion.getShortname() + "):"
												+ tableAnswer.getTitle() + " (" + tableAnswer.getShortname() + ")");
									}

									cell.setStringValue(cellValue);
									Font font = cell.getFont();
									font.setSize(10);
									font.setFontStyle(FontStyle.BOLD);
									cell.setFont(font);
								}
							}
						} else if (question instanceof Upload && publication != null
								&& !publication.getShowUploadedDocuments()) {
							// skip uploaded files;
						} else {
							cell = sheet.getCellByPosition(columnIndex++, rowIndex);
							cellValue = ConversionTools.removeHTMLNoEscape(question.getTitle());
							if (export != null && export.getShowShortnames()) {
								cellValue += " (" + question.getShortname() + ")";
							}
							cell.setStringValue(cellValue);
							Font font = cell.getFont();
							font.setSize(10);
							font.setFontStyle(FontStyle.BOLD);
							cell.setFont(font);
						}
					}

					if (form.getSurvey().getIsDelphi() && question.isDelphiElement()
							&& filter.explanationExported(question.getId().toString())) {
						cell = sheet.getCellByPosition(columnIndex++, rowIndex);
						cell.setStringValue(resources.getMessage("label.Explanation", null, "Explanation", locale));
						Font font = cell.getFont();
						font.setSize(10);
						font.setFontStyle(FontStyle.BOLD);
						cell.setFont(font);

						cell = sheet.getCellByPosition(columnIndex++, rowIndex);
						cell.setStringValue(resources.getMessage("label.Likes", null, "likes", locale));
						font = cell.getFont();
						font.setSize(10);
						font.setFontStyle(FontStyle.BOLD);
						cell.setFont(font);
					}

					if (form.getSurvey().getIsDelphi() && question.isDelphiElement()
							&& filter.discussionExported(question.getId().toString())) {
						cell = sheet.getCellByPosition(columnIndex++, rowIndex);
						cell.setStringValue(resources.getMessage("label.Discussion", null, "Discussion", locale));
						Font font = cell.getFont();
						font.setSize(10);
						font.setFontStyle(FontStyle.BOLD);
						cell.setFont(font);
					}
				}
		}

		if (publication == null && filter != null) {
			if (filter.exported("invitation")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.InvitationNumber", null, "Invitation Number", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}
			if (filter.exported("case")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.ContributionId", null, "Contribution Id", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}
			if (filter.exported("user")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.UserName", null, "User Name", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}
			if (filter.exported("created")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.CreationDate", null, "Creation Date", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}
			if (filter.exported("updated")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.LastUpdate", null, "Last Update", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}
			if (filter.exported("languages")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				cell.setStringValue(resources.getMessage("label.Languages", null, "Languages", locale));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);
			}
		}

		if (form.getSurvey().getIsQuiz()) {
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

	public void ExportContent(Publication publication, boolean sync, ResultFilter resultFilter) throws Exception {
		spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
		sheet = spreadsheet.getSheetByIndex(0);
		sheet.setTableName("Content");

		fileCounter = 0;

		ResultFilter filter;
		if (resultFilter != null) {
			filter = resultFilter;
		} else if (publication != null && export == null) {
			filter = publication.getFilter();
		} else {
			filter = export.getResultFilter();
		}

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

		rowIndex = InitSpreadsheet(sheet);

		questions = form.getSurvey().getQuestions();
		columnIndex = 0;

		initHeader(publication, filter, export);

		Map<String, Map<String, List<File>>> uploadedFilesByCodeAndQuestionUID = new HashMap<>();
		Map<String, String> uploadQuestionNicenames = new HashMap<>();

		/// here starts the db stuff

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

		HashMap<String, Object> parameters = new HashMap<>();

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

		List<List<String>> answersets = reportingService.getAnswerSets(survey, filter, null, false, true,
				publication == null || publication.getShowUploadedDocuments(), false, false,
				export != null && export.getShowShortnames());

		if (answersets != null) {
			for (List<String> row : answersets) {
				parseAnswerSet(null, row, publication, filter, filesByAnswer, export, uploadedFilesByCodeAndQuestionUID,
						uploadQuestionNicenames, null, null, null, explanationFilesOfSurvey, explanationFilesToExport);
			}
		} else {

			// it is not possible to query the database after the result query was executed
			Map<Integer, Map<String, String>> explanations = answerExplanationService
					.getAllExplanations(form.getSurvey());
			Map<Integer, Map<String, String>> discussions = answerExplanationService
					.getAllDiscussions(form.getSurvey());
			Map<String, Integer> likesForExplanations = answerExplanationService.getAllLikesForExplanation(form.getSurvey());

			String sql = "select ans.ANSWER_SET_ID, a.QUESTION_UID, a.VALUE, a.ANSWER_COL, a.ANSWER_ID, a.ANSWER_ROW, a.PA_UID, ans.UNIQUECODE, ans.ANSWER_SET_DATE, ans.ANSWER_SET_UPDATE, ans.ANSWER_SET_INVID, ans.RESPONDER_EMAIL, ans.ANSWER_SET_LANG, ans.SCORE FROM ANSWERS a RIGHT JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID IN ("
					+ answerService.getSql(null, form.getSurvey().getId(), filter, parameters, true)
					+ ") ORDER BY ans.ANSWER_SET_ID";

			NativeQuery query = session.createSQLQuery(sql);

			query.setReadOnly(true);
			sqlQueryService.setParameters(query, parameters);

			query.setFetchSize(Integer.MIN_VALUE);
			ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

			int lastAnswerSet = 0;
			AnswerSet answerSet = new AnswerSet();
			answerSet.setSurvey(survey);
			while (results.next()) {
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
						parseAnswerSet(answerSet, null, publication, filter, filesByAnswer, export,
								uploadedFilesByCodeAndQuestionUID, uploadQuestionNicenames, explanations, discussions, likesForExplanations,
								explanationFilesOfSurvey, explanationFilesToExport);
						session.flush();
					}

					answerSet = new AnswerSet();
					answerSet.setSurvey(survey);
					answerSet.setId(answer.getAnswerSetId());
					lastAnswerSet = answer.getAnswerSetId();
					answerSet.getAnswers().add(answer);
					answerSet.setDate((Date) a[8]);
					answerSet.setUpdateDate((Date) a[9]);
					answerSet.setLanguageCode((String) a[10]);
					answerSet.setUniqueCode((String) a[7]);
					answerSet.setInvitationId((String) a[10]);
					answerSet.setResponderEmail((String) a[11]);
					answerSet.setLanguageCode((String) a[12]);
					answerSet.setScore(ConversionTools.getValue(a[13]));
				}
			}
			if (lastAnswerSet > 0)
				parseAnswerSet(answerSet, null, publication, filter, filesByAnswer, export,
						uploadedFilesByCodeAndQuestionUID, uploadQuestionNicenames, explanations, discussions, likesForExplanations,
						explanationFilesOfSurvey, explanationFilesToExport);
			results.close();
		}

		if (!sync) {
			t.commit();
			session.close();
		}

		spreadsheet.save(outputStream);

		if (fileCounter > 0 || !uploadedFiles.isEmpty() || explanationFilesToExport.hasFiles()) {
			// there are multiple files
			java.io.File temp = new java.io.File(exportFilePath + ".zip");
			final OutputStream out = new FileOutputStream(temp);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);

			if (publication != null) {
				os.putArchiveEntry(new ZipArchiveEntry(FilenameUtils.getName(exportFilePath + ".ods")));
			} else {
				os.putArchiveEntry(new ZipArchiveEntry(FilenameUtils.getName(exportFilePath)));
			}

			IOUtils.copy(new FileInputStream(exportFilePath), os);
			os.closeArchiveEntry();

			String ext = FilenameUtils.getExtension(exportFilePath);

			for (int i = 1; i <= fileCounter; i++) {
				String name = exportFilePath.replace("." + ext, "_" + i + "." + ext);

				os.putArchiveEntry(new ZipArchiveEntry(FilenameUtils.getName(name)));
				IOUtils.copy(new FileInputStream(name), os);
				os.closeArchiveEntry();
			}

			for (String code : uploadedFilesByCodeAndQuestionUID.keySet()) {
				for (String nicename : uploadedFilesByCodeAndQuestionUID.get(code).keySet()) {
					for (File file : uploadedFilesByCodeAndQuestionUID.get(code).get(nicename)) {
						java.io.File f = fileService.getSurveyFile(survey.getUniqueId(), file.getUid());

						if (!f.exists()) {
							f = new java.io.File(exportService.getFileDir() + file.getUid());
						}

						if (f.exists()) {
							os.putArchiveEntry(new ZipArchiveEntry(code + Constants.PATH_DELIMITER + nicename
									+ Constants.PATH_DELIMITER + file.getName()));
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

			os.close();
			if (export != null)
				export.setZipped(true);
		} else {
			if (export != null)
				export.setZipped(false);
		}
	}

	private void parseAnswerSet(AnswerSet answerSet, List<String> answerrow, Publication publication,
			ResultFilter filter, Map<Integer, List<File>> filesByAnswer, Export export,
			Map<String, Map<String, List<File>>> uploadedFilesByContributionIDAndQuestionUID,
			Map<String, String> uploadQuestionNicenames, Map<Integer, Map<String, String>> explanations,
			Map<Integer, Map<String, String>> discussions, Map<String, Integer> likesForExplanations, FilesByTypes<Integer, String> explanationFilesOfSurvey,
			FilesByTypes<String, String> explanationFilesToExport) throws Exception {
		rowIndex++;
		columnIndex = 0;

		// Excel older than 2007 has a limit on the number of rows
		if (rowIndex > 0 && rowIndex % 65000 == 0) {
			fileCounter++;
			spreadsheet.save(outputStream);
			spreadsheet.close();

			String ext = FilenameUtils.getExtension(exportFilePath);
			outputStream = new FileOutputStream(exportFilePath.replace("." + ext, "_" + fileCounter + "." + ext));

			spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
			sheet = spreadsheet.getSheetByIndex(0);
			sheet.setTableName("Content");

			rowIndex = InitSpreadsheet(sheet);
			columnIndex = 0;

			initHeader(publication, filter, export);
		}

		int answerrowcounter = 2; // the first item is the answerset code, the second one the id

		for (Question question : questions) {
			if (publication != null || filter.exported(question.getId().toString())) {
				if (publication == null || publication.isAllQuestions() || publication.isSelected(question.getId())) {
					if (question.isUsedInResults()) {
						if (question instanceof Matrix) {
							Matrix matrix = (Matrix) question;
							for (Element matrixQuestion : matrix.getQuestions()) {
								cell = sheet.getCellByPosition(columnIndex++, rowIndex);

								if (answerSet == null) {
									String v = answerrow.get(answerrowcounter++);
									if (v != null && v.length() > 0) {
										cell.setStringValue(ConversionTools.removeHTMLNoEscape(v));
										cell.setDisplayText(ConversionTools.removeHTMLNoEscape(v));
									}
								} else {
									List<Answer> answers = answerSet.getAnswers(matrixQuestion.getUniqueId());

									StringBuilder cellValue = new StringBuilder();
									for (Answer answer : answers) {
										cellValue.append((cellValue.length() > 0) ? ";" : "").append(
												ConversionTools.removeHTMLNoEscape(form.getAnswerTitle(answer)));
										if (export != null && export.getShowShortnames()) {
											cellValue.append(" ").append(form.getAnswerShortname(answer));
										}
									}

									cell.setStringValue(ConversionTools.removeHTMLNoEscape(cellValue.toString(), true));
									cell.setDisplayText(ConversionTools.removeHTMLNoEscape(cellValue.toString(), true));
								}
								cell.setValueType(Constants.STRING);
							}
						} else if (question instanceof ComplexTable) {
							ComplexTable table = (ComplexTable) question;
							for (ComplexTableItem childQuestion : table.getQuestionChildElements()) {
								cell = sheet.getCellByPosition(columnIndex++, rowIndex);

								if (answerSet == null) {
									String v = answerrow.get(answerrowcounter++);
									if (v != null && v.length() > 0) {
										cell.setStringValue(ConversionTools.removeHTMLNoEscape(v));
										cell.setDisplayText(ConversionTools.removeHTMLNoEscape(v));
									}
								} else {
									List<Answer> answers = answerSet.getAnswers(childQuestion.getUniqueId());

									StringBuilder cellValue = new StringBuilder();
									for (Answer answer : answers) {
										cellValue.append((cellValue.length() > 0) ? ";" : "").append(
												ConversionTools.removeHTMLNoEscape(form.getAnswerTitle(answer)));
										if (export != null && export.getShowShortnames()) {
											cellValue.append(" ").append(form.getAnswerShortname(answer));
										}
									}

									cell.setStringValue(ConversionTools.removeHTMLNoEscape(cellValue.toString(), true));
									cell.setDisplayText(ConversionTools.removeHTMLNoEscape(cellValue.toString(), true));
								}
								cell.setValueType(Constants.STRING);
							}
						} else if (question instanceof RatingQuestion) {
							RatingQuestion rating = (RatingQuestion) question;
							for (Element childQuestion : rating.getQuestions()) {
								cell = sheet.getCellByPosition(columnIndex++, rowIndex);
								if (answerSet == null) {
									String v = answerrow.get(answerrowcounter++);
									if (v != null && v.length() > 0) {
										cell.setStringValue(ConversionTools.removeHTMLNoEscape(v));
										cell.setDisplayText(ConversionTools.removeHTMLNoEscape(v));
									}
								} else {
									List<Answer> answers = answerSet.getAnswers(childQuestion.getUniqueId());

									StringBuilder cellValue = new StringBuilder();
									if (!answers.isEmpty()) {
										cellValue.append((cellValue.length() > 0) ? ";" : "").append(
												ConversionTools.removeHTMLNoEscape(answers.get(0).getValue(), true));
									}
									cell.setStringValue(ConversionTools.removeHTMLNoEscape(cellValue.toString(), true));
									cell.setDisplayText(ConversionTools.removeHTMLNoEscape(cellValue.toString(), true));
								}
								cell.setValueType(Constants.STRING);
							}
						} else if (question instanceof Upload) {
							if (publication == null || publication.getShowUploadedDocuments()) {
								cell = sheet.getCellByPosition(columnIndex++, rowIndex);
								StringBuilder cellValue = new StringBuilder();
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
														.containsKey(
																uploadQuestionNicenames.get(question.getUniqueId()))) {
													uploadedFilesByContributionIDAndQuestionUID.get(answerrow.get(0))
															.put(uploadQuestionNicenames.get(question.getUniqueId()),
																	new ArrayList<File>());
												}
												uploadedFilesByContributionIDAndQuestionUID.get(answerrow.get(0))
														.get(uploadQuestionNicenames.get(question.getUniqueId()))
														.add(file);

												cellValue.append((cellValue.length() > 0) ? ";" : "")
														.append(file.getName());
											}
										}

										Paragraph p = cell.addParagraph("");
										p.appendHyperlink(cellValue.toString(),
												new URI("../" + answerrow.get(0) + Constants.PATH_DELIMITER
														+ uploadQuestionNicenames.get(question.getUniqueId())));
									}
								} else {
									List<Answer> answers = answerSet.getAnswers(question.getUniqueId());

									for (Answer answer : answers) {

										if (filesByAnswer.containsKey(answer.getId())) {
											for (File file : filesByAnswer.get(answer.getId())) {
												if (!uploadedFilesByContributionIDAndQuestionUID
														.containsKey(answerSet.getUniqueCode())) {
													uploadedFilesByContributionIDAndQuestionUID.put(
															answerSet.getUniqueCode(),
															new HashMap<String, List<File>>());
												}
												if (!uploadQuestionNicenames.containsKey(question.getUniqueId())) {
													uploadQuestionNicenames.put(question.getUniqueId(),
															"Upload_" + (uploadQuestionNicenames.size() + 1));
												}
												if (!uploadedFilesByContributionIDAndQuestionUID
														.get(answerSet.getUniqueCode()).containsKey(
																uploadQuestionNicenames.get(question.getUniqueId()))) {
													uploadedFilesByContributionIDAndQuestionUID
															.get(answerSet.getUniqueCode())
															.put(uploadQuestionNicenames.get(question.getUniqueId()),
																	new ArrayList<File>());
												}
												uploadedFilesByContributionIDAndQuestionUID
														.get(answerSet.getUniqueCode())
														.get(uploadQuestionNicenames.get(question.getUniqueId()))
														.add(file);

												cellValue.append((cellValue.length() > 0) ? ";" : "")
														.append(file.getName());
											}
										}
									}
									Paragraph p = cell.addParagraph("");
									p.appendHyperlink(cellValue.toString(),
											new URI("../" + answerSet.getUniqueCode() + Constants.PATH_DELIMITER
													+ uploadQuestionNicenames.get(question.getUniqueId())));
								}
							}
						} else if (question instanceof Table) {
							Table table = (Table) question;

							for (int tableRow = 1; tableRow < table.getAllRows(); tableRow++) {
								for (int tableCol = 1; tableCol < table.getAllColumns(); tableCol++) {
									cell = sheet.getCellByPosition(columnIndex++, rowIndex);
									if (answerSet == null) {
										String v = answerrow.get(answerrowcounter++);
										if (v != null && v.length() > 0) {
											cell.setStringValue(ConversionTools.removeHTMLNoEscape(v));
											cell.setDisplayText(ConversionTools.removeHTMLNoEscape(v));
										}
									} else {
										String answer = answerSet.getTableAnswer(table, tableRow, tableCol, false);
										if (answer == null)
											answer = "";

										cell.setStringValue(ConversionTools.removeHTMLNoEscape(answer, true));
										cell.setDisplayText(ConversionTools.removeHTMLNoEscape(answer, true));
									}
									cell.setValueType(Constants.STRING);
								}
							}
						} else if (question instanceof GalleryQuestion) {
							cell = sheet.getCellByPosition(columnIndex++, rowIndex);
							if (answerSet == null) {
								String v = answerrow.get(answerrowcounter++);
								if (v != null && v.length() > 0) {
									cell.setStringValue(ConversionTools.removeHTMLNoEscape(v));
									cell.setDisplayText(ConversionTools.removeHTMLNoEscape(v));
								}
							} else {
								List<Answer> answers = answerSet.getAnswers(question.getUniqueId());

								StringBuilder cellValue = new StringBuilder();
								boolean first = true;
								GalleryQuestion gallery = (GalleryQuestion) question;
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

								cell.setStringValue(ConversionTools.removeHTMLNoEscape(cellValue.toString(), true));
								cell.setDisplayText(ConversionTools.removeHTMLNoEscape(cellValue.toString(), true));
							}
							cell.setValueType(Constants.STRING);
						} else if ((question instanceof NumberQuestion || question instanceof FormulaQuestion)
								&& (export == null || !export.getShowShortnames())) {

							cell = sheet.getCellByPosition(columnIndex++, rowIndex);

							String format = "0";
							int decimalPlaces = 0;
							if (question instanceof NumberQuestion) {
								NumberQuestion numberQuestion = (NumberQuestion) question;
								decimalPlaces = numberQuestion.getDecimalPlaces();
							} else {
								FormulaQuestion formulaQuestion = (FormulaQuestion) question;
								decimalPlaces = formulaQuestion.getDecimalPlaces();
							}
							if (decimalPlaces > 0) {
								format += ".";
								for (int i = 0; i < decimalPlaces; i++) {
									format += "0";
								}
							}

							if (answerSet == null) {
								String v = answerrow.get(answerrowcounter++);
								if (v != null && v.length() > 0) {
									double cellValue = Double.parseDouble(v);
									cell.setDoubleValue(cellValue);
									cell.setValueType("float");
									cell.setFormatString(format);
								}
							} else {
								List<Answer> answers = answerSet.getAnswers(question.getUniqueId());

								double cellValue;

								if (!answers.isEmpty()) {
									cellValue = Double.parseDouble(answers.get(0).getValue());
									cell.setDoubleValue(cellValue);
									cell.setValueType("float");
									cell.setFormatString(format);
								}
							}
						} else if (question instanceof DateQuestion
								&& (export == null || !export.getShowShortnames())) {

							cell = sheet.getCellByPosition(columnIndex++, rowIndex);
							if (answerSet == null) {
								String v = answerrow.get(answerrowcounter++);
								if (v != null && v.length() > 0) {
									Date cellValue = ConversionTools.getDate(v);
									if (cellValue != null) {
										Calendar c = Calendar.getInstance();
										c.setTime(cellValue);
										cell.setDateValue(c);
									}
								}
							} else {
								List<Answer> answers = answerSet.getAnswers(question.getUniqueId());

								Date cellValue = null;

								if (!answers.isEmpty()) {
									cellValue = ConversionTools.getDate(answers.get(0).getValue());
									if (cellValue != null) {
										Calendar c = Calendar.getInstance();
										c.setTime(cellValue);
										cell.setDateValue(c);
									}
								}
							}

						} else {
							cell = sheet.getCellByPosition(columnIndex++, rowIndex);
							if (answerSet == null) {
								String v = answerrow.get(answerrowcounter++);
								if (v != null && v.length() > 0) {
									cell.setStringValue(ConversionTools.removeHTMLNoEscape(v));
									cell.setDisplayText(ConversionTools.removeHTMLNoEscape(v));
								}

							} else {
								List<Answer> answers = answerSet.getAnswers(question.getUniqueId());

								StringBuilder cellValue = new StringBuilder();
								for (Answer answer : answers) {
									cellValue.append((cellValue.length() > 0) ? ";" : "")
											.append(ConversionTools.removeHTMLNoEscape(form.getAnswerTitle(answer)));
									if (export != null && export.getShowShortnames()) {
										cellValue.append(" ").append(form.getAnswerShortname(answer));
									}
								}
								cell.setStringValue(ConversionTools.removeHTMLNoEscape(cellValue.toString(), true));
								cell.setDisplayText(ConversionTools.removeHTMLNoEscape(cellValue.toString(), true));
							}
							cell.setValueType(Constants.STRING);
						}
					}
				}
				if (question.isDelphiElement() && filter.explanationExported(question.getId().toString())) {
					final String questionUid = question.getUniqueId();
					cell = sheet.getCellByPosition(columnIndex++, rowIndex);
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

					if (!explanation.isEmpty()) {
						cell.setStringValue(ConversionTools.removeHTMLNoEscape(explanation, true));
						cell.setDisplayText(ConversionTools.removeHTMLNoEscape(explanation, true));
						cell.setTextWrapped(true);
						cell.setValueType(Constants.STRING);
					}
					final List<File> files = explanationFilesToExport.getFiles(answerSetUid, questionUid);
					if (!files.isEmpty()) {
						final Paragraph p = cell.addParagraph("");
						final Iterator<File> fileIterator = files.iterator();
						while (fileIterator.hasNext()) {
							final File file = fileIterator.next();
							p.appendHyperlink(file.getNameForExport(),
									new URI("../" + answerSetUid + Constants.PATH_DELIMITER + questionUid
											+ Constants.PATH_DELIMITER + file.getNameForExport()));
							if (fileIterator.hasNext()) {
								p.appendTextContent(";");
							}
						}
					}

					cell = sheet.getCellByPosition(columnIndex++, rowIndex);
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
						cell.setDoubleValue((double) likes); 
						cell.setValueType("float");
						cell.setFormatString("0");
					}
				}

				if (question.isDelphiElement() && filter.discussionExported(question.getId().toString())) {
					cell = sheet.getCellByPosition(columnIndex++, rowIndex);

					String discussion = "";

					if (answerSet == null) {
						discussion = answerrow.get(answerrowcounter++);
					} else if (discussions.containsKey(answerSet.getId())
							&& discussions.get(answerSet.getId()).containsKey(question.getUniqueId())) {
						discussion = discussions.get(answerSet.getId()).get(question.getUniqueId());
					}

					if (!discussion.isEmpty()) {
						cell.setStringValue(ConversionTools.removeHTMLNoEscape(discussion, true));
						cell.setDisplayText(ConversionTools.removeHTMLNoEscape(discussion, true));
						cell.setTextWrapped(true);
					} else {
						cell.setStringValue("");
					}

					cell.setValueType(Constants.STRING);
				}
			}
		}
		if (publication == null && filter != null) {
			if (filter.exported("invitation")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				if (form.getSurvey().getSecurity().contains("anonymous")) {
					cell.setDisplayText("Anonymous");
					cell.setStringValue("Anonymous");
				} else if (answerSet == null) {
					cell.setStringValue(answerrow.get(answerrowcounter));
					cell.setDisplayText(answerrow.get(answerrowcounter++));
				} else {
					cell.setDisplayText(answerSet.getInvitationId() != null ? answerSet.getInvitationId() : "");
					cell.setStringValue(answerSet.getInvitationId() != null ? answerSet.getInvitationId() : "");
				}
				cell.setValueType(Constants.STRING);
			}
			if (filter.exported("case")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				if (form.getSurvey().getSecurity().contains("anonymous")) {
					cell.setDisplayText("Anonymous");
					cell.setStringValue("Anonymous");
				} else if (answerSet == null) {
					cell.setStringValue(answerrow.get(answerrowcounter));
					cell.setDisplayText(answerrow.get(answerrowcounter++));
				} else {
					cell.setDisplayText(answerSet.getUniqueCode() != null ? answerSet.getUniqueCode() : "");
					cell.setStringValue(answerSet.getUniqueCode() != null ? answerSet.getUniqueCode() : "");
				}
				cell.setValueType(Constants.STRING);
			}
			if (filter.exported("user")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				if (form.getSurvey().getSecurity().contains("anonymous")) {
					cell.setDisplayText("Anonymous");
					cell.setStringValue("Anonymous");
				} else if (answerSet == null) {
					cell.setStringValue(answerrow.get(answerrowcounter));
					cell.setDisplayText(answerrow.get(answerrowcounter++));
				} else {
					cell.setDisplayText(answerSet.getResponderEmail() != null ? answerSet.getResponderEmail() : "");
					cell.setStringValue(answerSet.getResponderEmail() != null ? answerSet.getResponderEmail() : "");
				}
				cell.setValueType(Constants.STRING);
			}
			if (filter.exported("created")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				if (answerSet == null) {
					String v = answerrow.get(answerrowcounter++);
					if (v != null && v.length() > 0) {
						Date cellValue = ConversionTools.getDate(v);
						if (cellValue != null) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(cellValue);
							cell.setDateTimeValue(calendar);
						}
					}
				} else if (answerSet.getDate() != null) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(answerSet.getDate());
					cell.setDateTimeValue(calendar);
				}
			}
			if (filter.exported("updated")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				if (answerSet == null) {
					String v = answerrow.get(answerrowcounter++);
					if (v != null && v.length() > 0) {
						Date cellValue = ConversionTools.getDate(v);
						if (cellValue != null) {
							Calendar c = Calendar.getInstance();
							c.setTime(cellValue);
							cell.setDateTimeValue(c);
						}
					}
				} else if (answerSet.getUpdateDate() != null) {
					Calendar c = Calendar.getInstance();
					c.setTime(answerSet.getUpdateDate());
					cell.setDateTimeValue(c);
				}
			}
			if (filter.exported("languages")) {
				cell = sheet.getCellByPosition(columnIndex++, rowIndex);
				if (answerSet == null) {
					cell.setDisplayText(answerrow.get(answerrowcounter));
					cell.setStringValue(answerrow.get(answerrowcounter++));
				} else {
					cell.setDisplayText(answerSet.getLanguageCode() != null ? answerSet.getLanguageCode() : "");
				}
			}
		}

		if (form.getSurvey().getIsQuiz()) {
			cell = sheet.getCellByPosition(columnIndex++, rowIndex);

			if (answerSet == null) {
				String v = answerrow.get(answerrowcounter);
				if (v != null && v.length() > 0) {
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

		if (export == null) {
			cell.setDisplayText(form.getSurvey().getShortname());
			cell.setStringValue(form.getSurvey().getShortname());
		} else {
			cell.setDisplayText(export.getSurvey().getShortname());
			cell.setStringValue(export.getSurvey().getShortname());
		}

		// this is a workaround for a bug in ODFToolkit or LibreOffice that displays
		// content in empty cells
		cell = sheet.getCellByPosition(2, rowIndex);
		cell.setDisplayText("");

		rowIndex++;

		cell = sheet.getCellByPosition(0, rowIndex);
		cell.setDisplayText("Export Date");
		cell = sheet.getCellByPosition(1, rowIndex);

		if (export == null) {
			cell.setDateTimeValue(Calendar.getInstance());
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(export.getDate());
			cell.setDateTimeValue(calendar);
		}

		// this is a workaround for a bug in ODFToolkit or LibreOffice that displays
		// content in empty cells
		cell = sheet.getCellByPosition(2, rowIndex);
		cell.setDisplayText("");

		rowIndex += 2;
		return rowIndex;
	}

	@Override
	void exportStatisticsQuiz() throws Exception {
	}

	void ExportStatisticsODS() throws Exception {
		SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();

		sheet = spreadsheet.getSheetByIndex(0);
		sheet.setTableName("Statistics");

		sheet.getColumnByIndex(1).setWidth(50);
		sheet.getColumnByIndex(2).setWidth(sheet.getColumnByIndex(0).getWidth());

		DecimalFormat df = new DecimalFormat("#.##");

		Statistics statistics = form.getStatistics();
		Survey survey = surveyService.getSurveyInOriginalLanguage(form.getSurvey().getId(),
				form.getSurvey().getShortname(), form.getSurvey().getUniqueId());

		if (export != null && export.isAllAnswers() && !survey.isMissingElementsChecked()) {
			surveyService.checkAndRecreateMissingElements(survey, export.getResultFilter());
		}

		form.setSurvey(survey);
		String cellValue;

		rowIndex = 0;

		Set<String> visibleQuestions = null;
		if (export.getResultFilter() != null)
			visibleQuestions = export.getResultFilter().getExportedQuestions();
		if (visibleQuestions == null || visibleQuestions.isEmpty())
			visibleQuestions = export.getResultFilter().getVisibleQuestions();

		for (Element question : survey.getQuestionsAndSections()) {

			if (question instanceof Section && survey.getIsDelphi()
					&& export.getResultFilter().visibleSection(question.getId(), survey)) {
				cell = sheet.getCellByPosition(0, rowIndex);
				cell.setStringValue(ConversionTools.removeHTMLNoEscape(question.getTitle()));
				cell.setDisplayText(ConversionTools.removeHTMLNoEscape(question.getTitle()));
				Font font = cell.getFont();
				font.setSize(10);
				font.setFontStyle(FontStyle.BOLD);
				cell.setFont(font);

				rowIndex++;
				rowIndex++;
			}

			if (export.getResultFilter() == null || visibleQuestions.isEmpty()
					|| visibleQuestions.contains(question.getId().toString())) {

				if (question instanceof ChoiceQuestion) {
					cellValue = question.getTitle();
					if (export.getShowShortnames()) {
						cellValue += " (" + question.getShortname() + ")";
					}

					CreateTableForAnswerStat(cellValue);
					ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
					for (PossibleAnswer possibleAnswer : choiceQuestion.getAllPossibleAnswers()) {
						rowIndex++;

						cellValue = ConversionTools.removeHTMLNoEscape(possibleAnswer.getTitle());
						if (export.getShowShortnames()) {
							cellValue += " (" + possibleAnswer.getShortname() + ")";
						}

						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue(cellValue);
						cell.setDisplayText(cellValue);
						cell.setValueType(Constants.STRING);

						Double percent = statistics.getRequestedRecordsPercent().get(possibleAnswer.getId().toString());

						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();

						if (percent > 0) {
							drawImage(percent);
						}

						cell = sheet.getCellByPosition(2, rowIndex);
						cell.setDoubleValue(
								(double) statistics.getRequestedRecords().get(possibleAnswer.getId().toString()));
						cell.setDisplayText(
								statistics.getRequestedRecords().get(possibleAnswer.getId().toString()).toString());

						cell = sheet.getCellByPosition(3, rowIndex);
						cell.setPercentageValue(
								statistics.getRequestedRecordsPercent().get(possibleAnswer.getId().toString()));
						cell.setDisplayText(df.format(
								statistics.getRequestedRecordsPercent().get(possibleAnswer.getId().toString())) + "%");
						addDummyCells();
					}

					rowIndex++;
					cell = sheet.getCellByPosition(0, rowIndex);
					cell.setStringValue("No Answer");
					cell.setDisplayText("No Answer");
					cell.setValueType(Constants.STRING);

					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());

					cell = sheet.getCellByPosition(1, rowIndex);
					cell.removeContent();

					if (percent > 0) {
						drawImage(percent);
					}
					cell = sheet.getCellByPosition(2, rowIndex);
					cell.setDoubleValue((double) statistics.getRequestedRecords().get(question.getId().toString()));
					cell.setDisplayText(statistics.getRequestedRecords().get(question.getId().toString()).toString());

					cell = sheet.getCellByPosition(3, rowIndex);
					cell.setPercentageValue(percent);
					cell.setDisplayText(df.format(percent) + "%");

					rowIndex++;
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.removeContent();
					rowIndex++;
				} else if (question instanceof GalleryQuestion && ((GalleryQuestion) question).getSelection()) {
					cellValue = question.getTitle();
					if (export.getShowShortnames()) {
						cellValue += " (" + question.getShortname() + ")";
					}

					CreateTableForAnswerStat(cellValue);
					GalleryQuestion galleryQuestion = (GalleryQuestion) question;
					for (File file : galleryQuestion.getAllFiles()) {
						rowIndex++;

						cellValue = ConversionTools.removeHTMLNoEscape(file.getName());

						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue(cellValue);
						cell.setDisplayText(cellValue);
						cell.setValueType(Constants.STRING);

						Double percent = statistics.getRequestedRecordsPercent()
								.get(galleryQuestion.getId().toString() + "-" + file.getUid());

						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();

						if (percent > 0) {
							drawImage(percent);
						}

						cell = sheet.getCellByPosition(2, rowIndex);
						cell.setDoubleValue((double) statistics.getRequestedRecords()
								.get(galleryQuestion.getId().toString() + "-" + file.getUid()));
						cell.setDisplayText(statistics.getRequestedRecords()
								.get(galleryQuestion.getId().toString() + "-" + file.getUid()).toString());

						cell = sheet.getCellByPosition(3, rowIndex);
						cell.setPercentageValue(statistics.getRequestedRecordsPercent()
								.get(galleryQuestion.getId().toString() + "-" + file.getUid()));
						cell.setDisplayText(df.format(statistics.getRequestedRecordsPercent()
								.get(galleryQuestion.getId().toString() + "-" + file.getUid())) + "%");

						addDummyCells();
					}

					rowIndex++;
					cell = sheet.getCellByPosition(0, rowIndex);
					cell.setStringValue("No Answer");
					cell.setDisplayText("No Answer");
					cell.setValueType(Constants.STRING);

					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());

					cell = sheet.getCellByPosition(1, rowIndex);
					cell.removeContent();

					if (percent > 0) {
						drawImage(percent);
					}
					cell = sheet.getCellByPosition(2, rowIndex);
					cell.setDoubleValue((double) statistics.getRequestedRecords().get(question.getId().toString()));
					cell.setDisplayText(statistics.getRequestedRecords().get(question.getId().toString()).toString());

					cell = sheet.getCellByPosition(3, rowIndex);
					cell.setPercentageValue(percent);
					cell.setDisplayText(df.format(percent) + "%");

					rowIndex++;
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.removeContent();
					rowIndex++;
				} else if (question instanceof Matrix) {

					Matrix matrix = (Matrix) question;

					for (Element matrixQuestion : matrix.getQuestions()) {

						cellValue = matrix.getTitle() + ": " + matrixQuestion.getTitle();
						if (export.getShowShortnames()) {
							cellValue += " (" + matrixQuestion.getShortname() + ")";
						}

						CreateTableForAnswerStat(cellValue);

						for (Element matrixAnswer : matrix.getAnswers()) {
							rowIndex++;

							cellValue = ConversionTools.removeHTMLNoEscape(matrixAnswer.getTitle());
							if (export.getShowShortnames()) {
								cellValue += " (" + matrixAnswer.getShortname() + ")";
							}

							cell = sheet.getCellByPosition(0, rowIndex);
							cell.setStringValue(cellValue);
							cell.setDisplayText(cellValue);
							cell.setValueType(Constants.STRING);

							Double percent = statistics.getRequestedRecordsPercentForMatrix(matrixQuestion,
									matrixAnswer);

							cell = sheet.getCellByPosition(1, rowIndex);
							cell.removeContent();

							if (percent > 0) {
								drawImage(percent);
							}

							cell = sheet.getCellByPosition(2, rowIndex);
							cell.setDoubleValue(
									(double) statistics.getRequestedRecordsForMatrix(matrixQuestion, matrixAnswer));
							cell.setDisplayText(
									statistics.getRequestedRecordsForMatrix(matrixQuestion, matrixAnswer) + "");

							cell = sheet.getCellByPosition(3, rowIndex);
							cell.setPercentageValue(
									statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, matrixAnswer));
							cell.setDisplayText(df.format(
									statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, matrixAnswer))
									+ "%");

							addDummyCells();
						}

						rowIndex++;
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue("No Answer");
						cell.setDisplayText("No Answer");
						cell.setValueType(Constants.STRING);

						Double percent = statistics.getRequestedRecordsPercent().get(matrixQuestion.getId().toString());
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();
						if (percent > 0) {
							drawImage(percent);
						}
						cell = sheet.getCellByPosition(2, rowIndex);
						cell.setDoubleValue(
								(double) statistics.getRequestedRecords().get(matrixQuestion.getId().toString()));
						cell.setDisplayText(
								statistics.getRequestedRecords().get(matrixQuestion.getId().toString()).toString());

						cell = sheet.getCellByPosition(3, rowIndex);
						cell.setPercentageValue(percent);
						cell.setDisplayText(df.format(percent) + "%");

						rowIndex++;
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();
						rowIndex++;
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

							CreateTableForAnswerStat(cellValue);

							if (isChoice) {
								for (PossibleAnswer possibleAnswer : childQuestion.getPossibleAnswers()) {
									rowIndex++;

									cellValue = ConversionTools.removeHTMLNoEscape(possibleAnswer.getTitle());
									if (export.getShowShortnames()) {
										cellValue += " (" + possibleAnswer.getShortname() + ")";
									}

									cell = sheet.getCellByPosition(0, rowIndex);
									cell.setStringValue(cellValue);
									cell.setDisplayText(cellValue);
									cell.setValueType(Constants.STRING);

									Double percent = statistics.getRequestedRecordsPercent()
											.get(possibleAnswer.getId().toString());

									cell = sheet.getCellByPosition(1, rowIndex);
									cell.removeContent();

									if (percent > 0) {
										drawImage(percent);
									}

									cell = sheet.getCellByPosition(2, rowIndex);
									cell.setDoubleValue((double) statistics.getRequestedRecords()
											.get(possibleAnswer.getId().toString()));
									cell.setDisplayText(statistics.getRequestedRecords()
											.get(possibleAnswer.getId().toString()).toString());

									cell = sheet.getCellByPosition(3, rowIndex);
									cell.setPercentageValue(statistics.getRequestedRecordsPercent()
											.get(possibleAnswer.getId().toString()));
									cell.setDisplayText(df.format(statistics.getRequestedRecordsPercent()
											.get(possibleAnswer.getId().toString())) + "%");

									addDummyCells();
								}
							} else {
								for (String answer : childQuestion.getPossibleNumberAnswers()) {
									rowIndex++;

									cellValue = answer;

									cell = sheet.getCellByPosition(0, rowIndex);
									cell.setStringValue(cellValue);
									cell.setDisplayText(cellValue);
									cell.setValueType(Constants.STRING);

									Double percent = statistics.getRequestedRecordsPercent()
											.get(childQuestion.getAnswerWithPrefix(answer));

									cell = sheet.getCellByPosition(1, rowIndex);
									cell.removeContent();

									if (percent > 0) {
										drawImage(percent);
									}

									cell = sheet.getCellByPosition(2, rowIndex);
									cell.setDoubleValue((double) statistics.getRequestedRecords()
											.get(childQuestion.getAnswerWithPrefix(answer)));
									cell.setDisplayText(statistics.getRequestedRecords()
											.get(childQuestion.getAnswerWithPrefix(answer)).toString());

									cell = sheet.getCellByPosition(3, rowIndex);
									cell.setPercentageValue(percent);
									cell.setDisplayText(df.format(percent) + "%");

									addDummyCells();
								}
							}
							rowIndex++;
							cell = sheet.getCellByPosition(0, rowIndex);
							cell.setStringValue("No Answer");
							cell.setDisplayText("No Answer");
							cell.setValueType(Constants.STRING);

							Double percent = statistics.getRequestedRecordsPercent()
									.get(childQuestion.getId().toString());

							cell = sheet.getCellByPosition(1, rowIndex);
							cell.removeContent();

							if (percent > 0) {
								drawImage(percent);
							}
							cell = sheet.getCellByPosition(2, rowIndex);
							cell.setDoubleValue(
									(double) statistics.getRequestedRecords().get(childQuestion.getId().toString()));
							cell.setDisplayText(
									statistics.getRequestedRecords().get(childQuestion.getId().toString()).toString());

							cell = sheet.getCellByPosition(3, rowIndex);
							cell.setPercentageValue(percent);
							cell.setDisplayText(df.format(percent) + "%");

							rowIndex++;
							cell = sheet.getCellByPosition(1, rowIndex);
							cell.removeContent();
							rowIndex++;
						}
					}
				} else if (question instanceof RatingQuestion) {

					RatingQuestion rating = (RatingQuestion) question;

					for (Element childQuestion : rating.getQuestions()) {

						cellValue = rating.getTitle() + ": " + childQuestion.getTitle();
						if (export.getShowShortnames()) {
							cellValue += " (" + childQuestion.getShortname() + ")";
						}

						CreateTableForAnswerStat(cellValue);

						for (int i = 1; i <= rating.getNumIcons(); i++) {
							rowIndex++;

							cellValue = i + Constants.PATH_DELIMITER + rating.getNumIcons();

							cell = sheet.getCellByPosition(0, rowIndex);
							cell.setStringValue(cellValue);
							cell.setDisplayText(cellValue);
							cell.setValueType(Constants.STRING);

							Double percent = statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i);

							cell = sheet.getCellByPosition(1, rowIndex);
							cell.removeContent();

							if (percent > 0) {
								drawImage(percent);
							}

							cell = sheet.getCellByPosition(2, rowIndex);
							cell.setDoubleValue(
									(double) statistics.getRequestedRecordsForRatingQuestion(childQuestion, i));
							cell.setDisplayText(statistics.getRequestedRecordsForRatingQuestion(childQuestion, i) + "");

							cell = sheet.getCellByPosition(3, rowIndex);
							cell.setPercentageValue(
									statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i));
							cell.setDisplayText(
									df.format(statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i))
											+ "%");

							addDummyCells();
						}

						rowIndex++;
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue("No Answer");
						cell.setDisplayText("No Answer");
						cell.setValueType(Constants.STRING);

						Double percent = statistics.getRequestedRecordsPercent().get(childQuestion.getId().toString());
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();
						if (percent > 0) {
							drawImage(percent);
						}
						cell = sheet.getCellByPosition(2, rowIndex);
						cell.setDoubleValue(
								(double) statistics.getRequestedRecords().get(childQuestion.getId().toString()));
						cell.setDisplayText(
								statistics.getRequestedRecords().get(childQuestion.getId().toString()).toString());

						cell = sheet.getCellByPosition(3, rowIndex);
						cell.setPercentageValue(percent);
						cell.setDisplayText(df.format(percent) + "%");

						rowIndex++;
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();
						rowIndex++;
					}
				} else if (question instanceof RankingQuestion) {
					RankingQuestion ranking = (RankingQuestion) question;
					int size = ranking.getChildElements().size();

					Cell cell = sheet.getCellByPosition(0, rowIndex);
					cell.setStringValue(ConversionTools.removeHTMLNoEscape(question.getTitle()));
					cell.setDisplayText(ConversionTools.removeHTMLNoEscape(question.getTitle()));
					cell.setValueType(Constants.STRING);
					Font font = cell.getFont();
					font.setSize(10);
					font.setFontStyle(FontStyle.BOLD);
					cell.setFont(font);

					rowIndex++;

					for (int i = 1; i <= size; i++) {
						cell = sheet.getCellByPosition(i + 1, rowIndex);
						cell.setDoubleValue((double) i);
					}
					cell = sheet.getCellByPosition(size + 2, rowIndex);
					cell.setStringValue("Score");
					cell.setDisplayText("Score");
					cell.setValueType(Constants.STRING);
					rowIndex++;

					int total = statistics.getRequestedRecordsRankingScore().get(ranking.getId().toString());

					for (Element childQuestion : ranking.getChildElements()) {

						cellValue = ConversionTools.removeHTMLNoEscape(childQuestion.getTitle());
						if (export.getShowShortnames()) {
							cellValue += " (" + childQuestion.getShortname() + ")";
						}
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue(cellValue);
						cell.setDisplayText(cellValue);
						cell.setValueType(Constants.STRING);

						for (int i = 0; i < size; i++) {
							double percent = statistics.getRequestedRecordsRankingPercentScore()
									.get(childQuestion.getId() + "-" + i);
							cell = sheet.getCellByPosition(i + 2, rowIndex);
							cell.setPercentageValue(percent);
							cell.setDisplayText(df.format(percent) + "%");
						}
						double score = statistics.getRequestedRecordsRankingPercentScore()
								.get(childQuestion.getId().toString());

						cell = sheet.getCellByPosition(size + 2, rowIndex);
						cell.setDoubleValue(score);

						rowIndex++;

						for (int i = 0; i < size; i++) {
							int value = statistics.getRequestedRecordsRankingScore()
									.get(childQuestion.getId() + "-" + i);
							cell = sheet.getCellByPosition(i + 2, rowIndex);
							cell.setDoubleValue((double) value);
							cell.setValueType("float");
							cell.setFormatString("0");
						}
						cell = sheet.getCellByPosition(size + 2, rowIndex);
						cell.setDoubleValue((double) total);

						rowIndex++;
					}

					cell = sheet.getCellByPosition(0, rowIndex);
					cell.setStringValue("No Answer");
					cell.setDisplayText("No Answer");
					cell.setValueType(Constants.STRING);
					cell = sheet.getCellByPosition(2, rowIndex);
					cell.setPercentageValue(statistics.getRequestedRecordsPercent().get(ranking.getId().toString()));
					cell.setDisplayText(
							df.format(statistics.getRequestedRecordsPercent().get(ranking.getId().toString())) + "%");
					rowIndex++;

					cell = sheet.getCellByPosition(2, rowIndex);
					cell.setDoubleValue((double) statistics.getRequestedRecords().get(ranking.getId().toString()));
					rowIndex++;
				} else if (question instanceof NumberQuestion) {
					NumberQuestion numberQuestion = (NumberQuestion) question;
					if (numberQuestion.showStatisticsForNumberQuestion()) {

						cellValue = question.getTitle();
						if (export.getShowShortnames()) {
							cellValue += " (" + question.getShortname() + ")";
						}

						CreateTableForAnswerStat(cellValue);

						for (String answer : numberQuestion.getAllPossibleAnswers()) {
							rowIndex++;

							cellValue = answer;

							cell = sheet.getCellByPosition(0, rowIndex);
							cell.setStringValue(cellValue);
							cell.setDisplayText(cellValue);
							cell.setValueType(Constants.STRING);

							Double percent = statistics.getRequestedRecordsPercent()
									.get(numberQuestion.getAnswerWithPrefix(answer));

							cell = sheet.getCellByPosition(1, rowIndex);
							cell.removeContent();

							if (percent > 0) {
								drawImage(percent);
							}

							cell = sheet.getCellByPosition(2, rowIndex);
							cell.setDoubleValue((double) statistics.getRequestedRecords()
									.get(numberQuestion.getAnswerWithPrefix(answer)));
							cell.setDisplayText(statistics.getRequestedRecords()
									.get(numberQuestion.getAnswerWithPrefix(answer)).toString());

							cell = sheet.getCellByPosition(3, rowIndex);
							cell.setPercentageValue(percent);
							cell.setDisplayText(df.format(percent) + "%");

							addDummyCells();
						}
						rowIndex++;
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue("No Answer");
						cell.setDisplayText("No Answer");
						cell.setValueType(Constants.STRING);

						Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());

						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();

						if (percent > 0) {
							drawImage(percent);
						}
						cell = sheet.getCellByPosition(2, rowIndex);
						cell.setDoubleValue((double) statistics.getRequestedRecords().get(question.getId().toString()));
						cell.setDisplayText(
								statistics.getRequestedRecords().get(question.getId().toString()).toString());

						cell = sheet.getCellByPosition(3, rowIndex);
						cell.setPercentageValue(percent);
						cell.setDisplayText(df.format(percent) + "%");

						rowIndex++;
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();
						rowIndex++;
					}
				} else if (question instanceof FormulaQuestion) {
					FormulaQuestion formulaQuestion = (FormulaQuestion) question;
					if (formulaQuestion.showStatisticsForNumberQuestion()) {

						cellValue = question.getTitle();
						if (export.getShowShortnames()) {
							cellValue += " (" + question.getShortname() + ")";
						}

						CreateTableForAnswerStat(cellValue);

						for (String answer : formulaQuestion.getAllPossibleAnswers()) {
							rowIndex++;

							cellValue = answer;

							cell = sheet.getCellByPosition(0, rowIndex);
							cell.setStringValue(cellValue);
							cell.setDisplayText(cellValue);
							cell.setValueType(Constants.STRING);

							Double percent = statistics.getRequestedRecordsPercent()
									.get(formulaQuestion.getAnswerWithPrefix(answer));

							cell = sheet.getCellByPosition(1, rowIndex);
							cell.removeContent();

							if (percent > 0) {
								drawImage(percent);
							}

							cell = sheet.getCellByPosition(2, rowIndex);
							cell.setDoubleValue((double) statistics.getRequestedRecords()
									.get(formulaQuestion.getAnswerWithPrefix(answer)));
							cell.setDisplayText(statistics.getRequestedRecords()
									.get(formulaQuestion.getAnswerWithPrefix(answer)).toString());

							cell = sheet.getCellByPosition(3, rowIndex);
							cell.setPercentageValue(percent);
							cell.setDisplayText(df.format(percent) + "%");

							addDummyCells();
						}
						rowIndex++;
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue("No Answer");
						cell.setDisplayText("No Answer");
						cell.setValueType(Constants.STRING);

						Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());

						cell = sheet.getCellByPosition(1, rowIndex);
						cell.removeContent();

						if (percent > 0) {
							drawImage(percent);
						}
						cell = sheet.getCellByPosition(2, rowIndex);
						cell.setDoubleValue((double) statistics.getRequestedRecords().get(question.getId().toString()));
						cell.setDisplayText(
								statistics.getRequestedRecords().get(question.getId().toString()).toString());

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

	private void addDummyCells() {
		for (int i = 0; i < 10; i++) {
			cell = sheet.getCellByPosition(4 + i, rowIndex);
			cell.setStringValue("");
		}
	}

	private void drawImage(double percent) throws MalformedURLException, URISyntaxException {
		org.odftoolkit.simple.draw.Image image = cell
				.setImage(servletContext.getResource("/resources/images/chart.png").toURI());
		image.getFrame()
				.setRectangle(new FrameRectangle(0.05, 0.05, percent / 100 * 4.9, 0.36, SupportedLinearMeasure.CM));
	}

	private void CreateTableForAnswerStat(String title) {
		cell = sheet.getCellByPosition(0, rowIndex);
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
	void exportStatistics() throws Exception {
		if (export.getFormat() == ExportFormat.ods) {
			ExportStatisticsODS();
			return;
		}

		DecimalFormat df = new DecimalFormat("#.##");

		TextDocument document = TextDocument.newTextDocument();

		Survey survey = surveyService.getSurveyInOriginalLanguage(form.getSurvey().getId(),
				form.getSurvey().getShortname(), form.getSurvey().getUniqueId());

		if (export != null && export.isAllAnswers() && !survey.isMissingElementsChecked()) {
			surveyService.checkAndRecreateMissingElements(survey, export.getResultFilter());
		}

		form.setSurvey(survey);

		Statistics statistics = form.getStatistics();
		String cellValue;

		Set<String> visibleQuestions = null;
		if (export.getResultFilter() != null)
			visibleQuestions = export.getResultFilter().getExportedQuestions();
		if (visibleQuestions == null || visibleQuestions.isEmpty())
			visibleQuestions = export.getResultFilter().getVisibleQuestions();

		for (Element question : survey.getQuestionsAndSections()) {

			if (question instanceof Section && survey.getIsDelphi()
					&& export.getResultFilter().visibleSection(question.getId(), survey)) {
				Paragraph paragraph = document.addParagraph(ConversionTools.removeHTMLNoEscape(question.getTitle()));
				paragraph.getOdfElement().setProperty(OdfParagraphProperties.KeepWithNext, "always");

				Font font = paragraph.getFont();
				font.setFontStyle(FontStyle.BOLD);
				paragraph.setFont(font);
				document.addParagraph("");
			}

			if (export.getResultFilter() == null || visibleQuestions.isEmpty()
					|| visibleQuestions.contains(question.getId().toString())) {

				if (question instanceof ChoiceQuestion) {
					cellValue = ConversionTools.removeHTMLNoEscape(question.getTitle());
					if (export != null && export.getShowShortnames()) {
						cellValue += " (" + question.getShortname() + ")";
					}

					org.odftoolkit.simple.table.Table table = CreateTableForAnswer(document, cellValue);
					ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
					for (PossibleAnswer possibleAnswer : choiceQuestion.getAllPossibleAnswers()) {
						Row row = table.appendRow();

						cellValue = ConversionTools.removeHTMLNoEscape(possibleAnswer.getTitle());
						if (export != null && export.getShowShortnames()) {
							cellValue += " (" + possibleAnswer.getShortname() + ")";
						}

						row.getCellByIndex(0).setStringValue(cellValue);
						row.getCellByIndex(0).setValueType(Constants.STRING);

						Double percent = statistics.getRequestedRecordsPercent().get(possibleAnswer.getId().toString());

						if (percent > 0) {
							drawChart(percent, row);
						}
						row.getCellByIndex(2).setDoubleValue(
								(double) statistics.getRequestedRecords().get(possibleAnswer.getId().toString()));
						row.getCellByIndex(2).setStringValue(Integer
								.toString(statistics.getRequestedRecords().get(possibleAnswer.getId().toString())));
						row.getCellByIndex(3).setPercentageValue(percent);
						row.getCellByIndex(3).setStringValue(df.format(percent) + "%");

					}

					Row row = table.appendRow();
					row.getCellByIndex(0).setStringValue("No Answer");
					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());
					if (percent > 0) {
						drawChart(percent, row);
					}
					row.getCellByIndex(2)
							.setDoubleValue((double) statistics.getRequestedRecords().get(question.getId().toString()));
					row.getCellByIndex(2).setStringValue(
							Integer.toString(statistics.getRequestedRecords().get(question.getId().toString())));

					row.getCellByIndex(3).setStringValue(df.format(percent) + "%");

					row.getCellByIndex(2).setValueType("float");

					document.addParagraph("");
				} else if (question instanceof GalleryQuestion && ((GalleryQuestion) question).getSelection()) {
					cellValue = ConversionTools.removeHTMLNoEscape(question.getTitle());
					if (export != null && export.getShowShortnames()) {
						cellValue += " (" + question.getShortname() + ")";
					}

					org.odftoolkit.simple.table.Table table = CreateTableForAnswer(document, cellValue);
					GalleryQuestion galleryQuestion = (GalleryQuestion) question;
					for (File file : galleryQuestion.getAllFiles()) {
						Row row = table.appendRow();

						cellValue = ConversionTools.removeHTMLNoEscape(file.getName());

						row.getCellByIndex(0).setStringValue(cellValue);

						Double percent = statistics.getRequestedRecordsPercent()
								.get(galleryQuestion.getId().toString() + "-" + file.getUid());

						if (percent > 0) {
							drawChart(percent, row);
						}

						row.getCellByIndex(2).setDoubleValue((double) statistics.getRequestedRecords()
								.get(galleryQuestion.getId().toString() + "-" + file.getUid()));
						row.getCellByIndex(2).setStringValue(Integer.toString(statistics.getRequestedRecords()
								.get(galleryQuestion.getId().toString() + "-" + file.getUid())));

						row.getCellByIndex(3).setStringValue(df.format(percent) + "%");

						row.getCellByIndex(2).setValueType("float");
					}

					Row row = table.appendRow();
					row.getCellByIndex(0).setStringValue("No Answer");
					Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());
					if (percent > 0) {
						drawChart(percent, row);
					}
					row.getCellByIndex(2)
							.setDoubleValue((double) statistics.getRequestedRecords().get(question.getId().toString()));
					row.getCellByIndex(2).setStringValue(
							Integer.toString(statistics.getRequestedRecords().get(question.getId().toString())));

					row.getCellByIndex(3).setStringValue(df.format(percent) + "%");

					row.getCellByIndex(2).setValueType("float");

					document.addParagraph("");
				} else if (question instanceof Matrix) {

					Matrix matrix = (Matrix) question;

					for (Element matrixQuestion : matrix.getQuestions()) {

						cellValue = ConversionTools
								.removeHTMLNoEscape(matrix.getTitle() + ": " + matrixQuestion.getTitle());
						if (export != null && export.getShowShortnames()) {
							cellValue += " (" + matrixQuestion.getShortname() + ")";
						}

						org.odftoolkit.simple.table.Table table = CreateTableForAnswer(document, cellValue);
						for (Element matrixAnswer : matrix.getAnswers()) {
							Row row = table.appendRow();

							cellValue = ConversionTools.removeHTMLNoEscape(matrixAnswer.getTitle());
							if (export != null && export.getShowShortnames()) {
								cellValue += " (" + matrixAnswer.getShortname() + ")";
							}

							row.getCellByIndex(0).setStringValue(cellValue);

							Double percent = statistics.getRequestedRecordsPercentForMatrix(matrixQuestion,
									matrixAnswer);

							if (percent > 0) {
								drawChart(percent, row);
							}

							row.getCellByIndex(2).setDoubleValue(
									(double) statistics.getRequestedRecordsForMatrix(matrixQuestion, matrixAnswer));
							row.getCellByIndex(2).setStringValue(Integer
									.toString(statistics.getRequestedRecordsForMatrix(matrixQuestion, matrixAnswer)));

							row.getCellByIndex(3).setStringValue(df.format(percent) + "%");
							row.getCellByIndex(2).setValueType("float");
						}

						Row row = table.appendRow();
						row.getCellByIndex(0).setStringValue("No Answer");
						Double percent = statistics.getRequestedRecordsPercent().get(matrixQuestion.getId().toString());
						if (percent > 0) {
							drawChart(percent, row);
						}
						row.getCellByIndex(2).setDoubleValue(
								(double) statistics.getRequestedRecords().get(matrixQuestion.getId().toString()));
						row.getCellByIndex(2).setStringValue(Integer
								.toString(statistics.getRequestedRecords().get(matrixQuestion.getId().toString())));

						row.getCellByIndex(3).setStringValue(df.format(percent) + "%");
						row.getCellByIndex(2).setValueType("float");

						document.addParagraph("");
					}
				} else if (question instanceof ComplexTable) {

					ComplexTable complexTable = (ComplexTable) question;

					for (ComplexTableItem childQuestion : complexTable.getQuestionChildElements()) {
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
							cellValue = ConversionTools.removeHTMLNoEscape(childQuestion.getResultTitle(complexTable));
							if (export != null && export.getShowShortnames()) {
								cellValue += " (" + childQuestion.getShortname() + ")";
							}

							org.odftoolkit.simple.table.Table table = CreateTableForAnswer(document, cellValue);

							if (isChoice) {
								for (PossibleAnswer possibleAnswer : childQuestion.getPossibleAnswers()) {
									Row row = table.appendRow();

									cellValue = ConversionTools.removeHTMLNoEscape(possibleAnswer.getTitle());
									if (export != null && export.getShowShortnames()) {
										cellValue += " (" + possibleAnswer.getShortname() + ")";
									}

									row.getCellByIndex(0).setStringValue(cellValue);
									row.getCellByIndex(0).setValueType(Constants.STRING);

									Double percent = statistics.getRequestedRecordsPercent()
											.get(possibleAnswer.getId().toString());

									if (percent > 0) {
										drawChart(percent, row);
									}
									row.getCellByIndex(2).setDoubleValue((double) statistics.getRequestedRecords()
											.get(possibleAnswer.getId().toString()));
									row.getCellByIndex(2).setStringValue(Integer.toString(
											statistics.getRequestedRecords().get(possibleAnswer.getId().toString())));
									row.getCellByIndex(3).setPercentageValue(percent);
									row.getCellByIndex(3).setStringValue(df.format(percent) + "%");
								}
							} else {
								for (String answer : childQuestion.getPossibleNumberAnswers()) {
									Row row = table.appendRow();

									cellValue = answer;
									row.getCellByIndex(0).setStringValue(cellValue);
									row.getCellByIndex(0).setValueType(Constants.STRING);

									Double percent = statistics.getRequestedRecordsPercent()
											.get(childQuestion.getAnswerWithPrefix(answer));

									if (percent > 0) {
										drawChart(percent, row);
									}
									row.getCellByIndex(2).setDoubleValue((double) statistics.getRequestedRecords()
											.get(childQuestion.getAnswerWithPrefix(answer)));
									row.getCellByIndex(2).setStringValue(Integer.toString(statistics
											.getRequestedRecords().get(childQuestion.getAnswerWithPrefix(answer))));
									row.getCellByIndex(3).setPercentageValue(percent);
									row.getCellByIndex(3).setStringValue(df.format(percent) + "%");
								}
							}

							Row row = table.appendRow();
							row.getCellByIndex(0).setStringValue("No Answer");
							Double percent = statistics.getRequestedRecordsPercent()
									.get(childQuestion.getId().toString());
							if (percent > 0) {
								drawChart(percent, row);
							}
							row.getCellByIndex(2).setDoubleValue(
									(double) statistics.getRequestedRecords().get(childQuestion.getId().toString()));
							row.getCellByIndex(2).setStringValue(Integer
									.toString(statistics.getRequestedRecords().get(childQuestion.getId().toString())));

							row.getCellByIndex(3).setStringValue(df.format(percent) + "%");

							row.getCellByIndex(2).setValueType("float");

							document.addParagraph("");
						}
					}
				} else if (question instanceof RatingQuestion) {

					RatingQuestion rating = (RatingQuestion) question;

					for (Element childQuestion : rating.getQuestions()) {

						cellValue = ConversionTools
								.removeHTMLNoEscape(rating.getTitle() + ": " + childQuestion.getTitle());
						if (export != null && export.getShowShortnames()) {
							cellValue += " (" + rating.getShortname() + ")";
						}

						org.odftoolkit.simple.table.Table table = CreateTableForAnswer(document, cellValue);
						for (int i = 1; i <= rating.getNumIcons(); i++) {
							Row row = table.appendRow();

							cellValue = i + Constants.PATH_DELIMITER + rating.getNumIcons();

							row.getCellByIndex(0).setStringValue(cellValue);

							Double percent = statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, i);

							if (percent > 0) {
								drawChart(percent, row);
							}

							row.getCellByIndex(2).setDoubleValue(
									(double) statistics.getRequestedRecordsForRatingQuestion(childQuestion, i));
							row.getCellByIndex(2).setStringValue(Integer
									.toString(statistics.getRequestedRecordsForRatingQuestion(childQuestion, i)));

							row.getCellByIndex(3).setStringValue(df.format(percent) + "%");
							row.getCellByIndex(2).setValueType("float");
						}

						Row row = table.appendRow();
						row.getCellByIndex(0).setStringValue("No Answer");
						Double percent = statistics.getRequestedRecordsPercent().get(childQuestion.getId().toString());
						if (percent > 0) {
							drawChart(percent, row);
						}
						row.getCellByIndex(2).setDoubleValue(
								(double) statistics.getRequestedRecords().get(childQuestion.getId().toString()));
						row.getCellByIndex(2).setStringValue(Integer
								.toString(statistics.getRequestedRecords().get(childQuestion.getId().toString())));

						row.getCellByIndex(3).setStringValue(df.format(percent) + "%");
						row.getCellByIndex(2).setValueType("float");

						document.addParagraph("");
					}
				} else if (question instanceof RankingQuestion) {
					RankingQuestion ranking = (RankingQuestion) question;
					int size = ranking.getChildElements().size();

					cellValue = ConversionTools.removeHTMLNoEscape(ranking.getTitle());
					if (export != null && export.getShowShortnames()) {
						cellValue += " (" + ranking.getShortname() + ")";
					}

					Border border = new Border(Color.BLACK, 0.05, StyleTypeDefinitions.SupportedLinearMeasure.PT);
					border.setInnerLineWidth(0);
					border.setOuterLineWidth(0);
					border.setLineStyle(LineType.SINGLE);

					org.odftoolkit.simple.table.Table table = CreateTableForRankingQuestion(document, cellValue, size,
							border);
					table.setCellStyleInheritance(false);

					Row row;

					int total = statistics.getRequestedRecordsRankingScore().get(ranking.getId().toString());

					for (Element childQuestion : ranking.getChildElements()) {
						row = table.appendRow();

						cellValue = ConversionTools.removeHTMLNoEscape(childQuestion.getTitle());
						if (export.getShowShortnames()) {
							cellValue += " (" + childQuestion.getShortname() + ")";
						}
						row.getCellByIndex(0).setStringValue(cellValue);
						row.getCellByIndex(0).setBorders(CellBordersType.ALL_FOUR, border);

						for (int i = 0; i < size; i++) {
							double percent = statistics.getRequestedRecordsRankingPercentScore()
									.get(childQuestion.getId() + "-" + i);
							row.getCellByIndex(i + 1).setPercentageValue(percent);
							row.getCellByIndex(i + 1).setStringValue(df.format(percent) + "%");
							row.getCellByIndex(i + 1).setBorders(CellBordersType.ALL_FOUR, border);
						}
						double score = statistics.getRequestedRecordsRankingPercentScore()
								.get(childQuestion.getId().toString());
						row.getCellByIndex(size + 1).setDoubleValue(score);
						row.getCellByIndex(size + 1).setValueType("float");
						row.getCellByIndex(size + 1).setBorders(CellBordersType.ALL_FOUR, border);

						row = table.appendRow();
						row.getCellByIndex(0).setBorders(CellBordersType.ALL_FOUR, border);

						for (int i = 0; i < size; i++) {
							int value = statistics.getRequestedRecordsRankingScore()
									.get(childQuestion.getId() + "-" + i);
							row.getCellByIndex(i + 1).setDoubleValue((double) value);
							row.getCellByIndex(i + 1).setValueType("float");
							row.getCellByIndex(i + 1).setFormatString("0");
							row.getCellByIndex(i + 1).setBorders(CellBordersType.ALL_FOUR, border);
						}
						row.getCellByIndex(size + 1).setDoubleValue((double) total);
						row.getCellByIndex(size + 1).setValueType("float");
						row.getCellByIndex(size + 1).setFormatString("0");
						row.getCellByIndex(size + 1).setBorders(CellBordersType.ALL_FOUR, border);
					}
					row = table.appendRow();
					cell = row.getCellByIndex(0);
					cell.setStringValue("No Answer");
					cell.setDisplayText("No Answer");
					cell.setValueType(Constants.STRING);
					cell = row.getCellByIndex(1);
					cell.setPercentageValue(statistics.getRequestedRecordsPercent().get(ranking.getId().toString()));
					cell.setDisplayText(
							df.format(statistics.getRequestedRecordsPercent().get(ranking.getId().toString())) + "%");
					table.getCellRangeByPosition(1, row.getRowIndex(), size + 1, row.getRowIndex()).merge();
					row.getCellByIndex(0).setBorders(CellBordersType.ALL_FOUR, border);
					row.getCellByIndex(1).setBorders(CellBordersType.ALL_FOUR, border);
					row = table.appendRow();

					cell = row.getCellByIndex(1);
					cell.setDoubleValue((double) statistics.getRequestedRecords().get(ranking.getId().toString()));
					row.getCellByIndex(1).setValueType("float");
					row.getCellByIndex(1).setFormatString("0");
					table.getCellRangeByPosition(1, row.getRowIndex(), size + 1, row.getRowIndex()).merge();
					row.getCellByIndex(0).setBorders(CellBordersType.ALL_FOUR, border);
					row.getCellByIndex(1).setBorders(CellBordersType.ALL_FOUR, border);

					document.addParagraph("");
				} else if (question instanceof NumberQuestion) {
					NumberQuestion numberQuestion = (NumberQuestion) question;

					if (numberQuestion.showStatisticsForNumberQuestion()) {
						cellValue = ConversionTools.removeHTMLNoEscape(question.getTitle());
						if (export != null && export.getShowShortnames()) {
							cellValue += " (" + question.getShortname() + ")";
						}

						org.odftoolkit.simple.table.Table table = CreateTableForAnswer(document, cellValue);

						for (String answer : numberQuestion.getAllPossibleAnswers()) {
							Row row = table.appendRow();

							cellValue = answer;
							row.getCellByIndex(0).setStringValue(cellValue);
							row.getCellByIndex(0).setValueType(Constants.STRING);

							Double percent = statistics.getRequestedRecordsPercent()
									.get(numberQuestion.getAnswerWithPrefix(answer));

							if (percent > 0) {
								drawChart(percent, row);
							}
							row.getCellByIndex(2).setDoubleValue((double) statistics.getRequestedRecords()
									.get(numberQuestion.getAnswerWithPrefix(answer)));
							row.getCellByIndex(2).setStringValue(Integer.toString(
									statistics.getRequestedRecords().get(numberQuestion.getAnswerWithPrefix(answer))));
							row.getCellByIndex(3).setPercentageValue(percent);
							row.getCellByIndex(3).setStringValue(df.format(percent) + "%");
						}

						Row row = table.appendRow();
						row.getCellByIndex(0).setStringValue("No Answer");
						Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());
						if (percent > 0) {
							drawChart(percent, row);
						}
						row.getCellByIndex(2).setDoubleValue(
								(double) statistics.getRequestedRecords().get(question.getId().toString()));
						row.getCellByIndex(2).setStringValue(
								Integer.toString(statistics.getRequestedRecords().get(question.getId().toString())));

						row.getCellByIndex(3).setStringValue(df.format(percent) + "%");

						row.getCellByIndex(2).setValueType("float");

						document.addParagraph("");
					}
				} else if (question instanceof FormulaQuestion) {
					FormulaQuestion formulaQuestion = (FormulaQuestion) question;

					if (formulaQuestion.showStatisticsForNumberQuestion()) {
						cellValue = ConversionTools.removeHTMLNoEscape(question.getTitle());
						if (export != null && export.getShowShortnames()) {
							cellValue += " (" + question.getShortname() + ")";
						}

						org.odftoolkit.simple.table.Table table = CreateTableForAnswer(document, cellValue);

						for (String answer : formulaQuestion.getAllPossibleAnswers()) {
							Row row = table.appendRow();

							cellValue = answer;
							row.getCellByIndex(0).setStringValue(cellValue);
							row.getCellByIndex(0).setValueType(Constants.STRING);

							Double percent = statistics.getRequestedRecordsPercent()
									.get(formulaQuestion.getAnswerWithPrefix(answer));

							if (percent > 0) {
								drawChart(percent, row);
							}
							row.getCellByIndex(2).setDoubleValue((double) statistics.getRequestedRecords()
									.get(formulaQuestion.getAnswerWithPrefix(answer)));
							row.getCellByIndex(2).setStringValue(Integer.toString(
									statistics.getRequestedRecords().get(formulaQuestion.getAnswerWithPrefix(answer))));
							row.getCellByIndex(3).setPercentageValue(percent);
							row.getCellByIndex(3).setStringValue(df.format(percent) + "%");
						}

						Row row = table.appendRow();
						row.getCellByIndex(0).setStringValue("No Answer");
						Double percent = statistics.getRequestedRecordsPercent().get(question.getId().toString());
						if (percent > 0) {
							drawChart(percent, row);
						}
						row.getCellByIndex(2).setDoubleValue(
								(double) statistics.getRequestedRecords().get(question.getId().toString()));
						row.getCellByIndex(2).setStringValue(
								Integer.toString(statistics.getRequestedRecords().get(question.getId().toString())));

						row.getCellByIndex(3).setStringValue(df.format(percent) + "%");

						row.getCellByIndex(2).setValueType("float");

						document.addParagraph("");
					}
				}
			}
		}
		document.save(outputStream);
	}

	private void drawChart(double percent, Row row) {
		Paragraph p = row.getCellByIndex(1).addParagraph("");
		Textbox t = p.addTextbox(new FrameRectangle(0, 0, percent / 100 * 5, 0.5, SupportedLinearMeasure.CM));
		t.setBackgroundColor(Color.BLUE);
		Paragraph p2 = t.addParagraph(".");
		Font font = p2.getFont();
		font.setSize(6);
		font.setColor(Color.BLUE);
		p2.setFont(font);
	}

	private org.odftoolkit.simple.table.Table CreateTableForAnswer(TextDocument document, String title) {
		Paragraph paragraph = document.addParagraph(ConversionTools.removeHTMLNoEscape(title));
		paragraph.getOdfElement().setProperty(OdfParagraphProperties.KeepWithNext, "always");

		Font font = paragraph.getFont();
		font.setFontStyle(FontStyle.BOLD);
		paragraph.setFont(font);

		org.odftoolkit.simple.table.Table table = document.addTable(1, 4);
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

	private org.odftoolkit.simple.table.Table CreateTableForRankingQuestion(TextDocument document, String title,
			int children, Border border) {
		Paragraph paragraph = document.addParagraph(ConversionTools.removeHTMLNoEscape(title));
		paragraph.getOdfElement().setProperty(OdfParagraphProperties.KeepWithNext, "always");

		Font font = paragraph.getFont();
		font.setFontStyle(FontStyle.BOLD);
		paragraph.setFont(font);

		org.odftoolkit.simple.table.Table table = document.addTable(1, children + 2);
		table.getOdfElement().setProperty(OdfTableProperties.MayBreakBetweenRows, "false");

		Cell cell;
		for (int i = 1; i <= children; i++) {
			cell = table.getCellByPosition(i, 0);
			cell.setFont(font);
			cell.setStringValue(String.valueOf(i));
			cell.setBorders(CellBordersType.ALL_FOUR, border);
		}

		cell = table.getCellByPosition(children + 1, 0);
		cell.setFont(font);
		cell.setStringValue("Score");
		cell.setBorders(CellBordersType.ALL_FOUR, border);
		return table;
	}

	@Override
	void exportAddressBook() throws Exception {

		User user = administrationService.getUser(userId);

		SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
		org.odftoolkit.simple.table.Table sheet = spreadsheet.getSheetByIndex(0);
		sheet.setTableName("Contacts");

		int rowIndex = 0;
		int cellIndex = 0;
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

			cell = sheet.getCellByPosition(cellIndex++, rowIndex);
			cell.setStringValue("Name");

			cell = sheet.getCellByPosition(cellIndex++, rowIndex);
			cell.setStringValue("Email");

			for (AttributeName att : configuredattributes) {
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue(att.getName());
			}
			rowIndex++;
			cellIndex = 0;

			for (Attendee attendee : attendees) {
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue(attendee.getName());
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue(attendee.getEmail());

				for (AttributeName att : configuredattributes) {
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);

					if (att.getName().equals("Owner")) {
						cell.setStringValue(
								attendee.getOwner() != null ? attendee.getOwner().replace("&#160;", "") : "");
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
	void exportActivities() throws Exception {
		SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
		org.odftoolkit.simple.table.Table sheet = spreadsheet.getSheetByIndex(0);
		sheet.setTableName("Contacts");

		int rowIndex = 0;
		int cellIndex = 0;
		Cell cell;

		List<Activity> activities = activityService.get(export.getActivityFilter(), 1, Integer.MAX_VALUE);

		try {
			ActivityFilter filter = export.getActivityFilter();
			if (filter.exported("date")) {
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("Date");
			}

			if (filter.exported("logid")) {
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("LogID");
			}

			if (filter.exported("user")) {
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("User");
			}

			if (filter.exported("object")) {
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("Object");
			}

			if (filter.exported("property")) {
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("Property");
			}

			if (filter.exported("event")) {
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("Event");
			}

			if (filter.exported("description")) {
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("Description");
			}

			if (filter.exported("oldvalue")) {
				cell = sheet.getCellByPosition(cellIndex++, rowIndex);
				cell.setStringValue("OldValue");
			}

			if (filter.exported("newvalue")) {
				cell = sheet.getCellByPosition(cellIndex, rowIndex);
				cell.setStringValue("NewValue");
			}

			rowIndex++;
			cellIndex = 0;

			for (Activity activity : activities) {
				if (filter.exported("date")) {
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(ConversionTools.getFullString(activity.getDate()));
				}

				if (filter.exported("logid")) {
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(Integer.toString(activity.getLogID()));
				}

				if (filter.exported("user")) {
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					activity.setUserName(
							activity.getUserId() > 0 ? administrationService.getUser(activity.getUserId()).getName()
									: "");
					cell.setStringValue(activity.getUserName());
				}

				if (filter.exported("object")) {
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(activity.getObject());
				}

				if (filter.exported("property")) {
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(
							activity.getProperty() != "PivotLanguage" ? activity.getProperty() : "MainLanguage");
				}

				if (filter.exported("event")) {
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(activity.getEvent());
				}

				if (filter.exported("description")) {
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(resources.getMessage("logging." + activity.getLogID(), null,
							"logging." + activity.getLogID(), locale));
				}

				if (filter.exported("oldvalue")) {
					cell = sheet.getCellByPosition(cellIndex++, rowIndex);
					cell.setStringValue(activity.getOldValue());
				}

				if (filter.exported("newvalue")) {
					cell = sheet.getCellByPosition(cellIndex, rowIndex);
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
	void exportTokens() throws Exception {

		ParticipationGroup participationGroup = participationService.get(export.getParticipationGroup());

		SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
		org.odftoolkit.simple.table.Table sheet = spreadsheet.getSheetByIndex(0);
		sheet.setTableName("Tokens");

		if (participationGroup.getType() == ParticipationGroupType.Static
				|| participationGroup.getType() == ParticipationGroupType.ECMembers) {
			Map<Integer, Invitation> invitations = attendeeService
					.getInvitationsByAttendeeForParticipationGroup(export.getParticipationGroup());
			int rowIndex = 0;

			cell = sheet.getCellByPosition(0, rowIndex);
			cell.setStringValue("GuestList  ID");
			cell = sheet.getCellByPosition(1, rowIndex++);
			cell.setStringValue(export.getParticipationGroup().toString());

			cell = sheet.getCellByPosition(0, rowIndex);
			cell.setStringValue("NAME");
			cell = sheet.getCellByPosition(1, rowIndex);
			cell.setStringValue("EMAIL");

			if (!export.getSurvey().isAnonymous()) {
				cell = sheet.getCellByPosition(2, rowIndex);
				cell.setStringValue("INVITATION DATE");
				cell = sheet.getCellByPosition(3, rowIndex);
				cell.setStringValue("REMINDER DATE");
				cell = sheet.getCellByPosition(4, rowIndex);
				cell.setStringValue("ANSWERS");
			}

			if (participationGroup.getType() == ParticipationGroupType.Static) {
				for (Attendee attendee : participationGroup.getAttendees()) {
					rowIndex++;

					Invitation invitation = invitations.get(attendee.getId());
					if (invitation == null) {
						invitation = new Invitation(participationGroup.getId(), attendee.getId());
						attendeeService.add(invitation);
					}

					cell = sheet.getCellByPosition(0, rowIndex);
					cell.setStringValue(attendee.getName());
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.setStringValue(attendee.getEmail());

					if (!export.getSurvey().isAnonymous()) {
						cell = sheet.getCellByPosition(2, rowIndex);

						Calendar calendar = Calendar.getInstance();
						calendar.setTime(invitation.getInvited());
						cell.setDateTimeValue(calendar);

						cell = sheet.getCellByPosition(3, rowIndex);
						if (invitation.getReminded() != null) {
							calendar.setTime(invitation.getReminded());
							cell.setDateTimeValue(calendar);
						}

						cell = sheet.getCellByPosition(4, rowIndex);
						cell.setStringValue(invitation.getAnswers().toString());
					}
				}
			} else {
				for (EcasUser attendee : participationGroup.getEcasUsers()) {
					rowIndex++;

					Invitation invitation = invitations.get(attendee.getId());
					if (invitation == null) {
						invitation = new Invitation(participationGroup.getId(), attendee.getId());
						attendeeService.add(invitation);
					}

					cell = sheet.getCellByPosition(0, rowIndex);
					cell.setStringValue(attendee.getName());
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.setStringValue(attendee.getEmail());

					if (!export.getSurvey().isAnonymous()) {
						cell = sheet.getCellByPosition(2, rowIndex);

						Calendar calendar = Calendar.getInstance();
						calendar.setTime(invitation.getInvited());
						cell.setDateTimeValue(calendar);

						cell = sheet.getCellByPosition(3, rowIndex);
						if (invitation.getReminded() != null) {
							calendar.setTime(invitation.getReminded());
							cell.setDateTimeValue(calendar);
						}

						cell = sheet.getCellByPosition(4, rowIndex);
						cell.setStringValue(invitation.getAnswers().toString());
					}
				}
			}

		} else {

			List<Invitation> invitations = attendeeService
					.getInvitationsForParticipationGroup(export.getParticipationGroup());

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

			if (!export.getSurvey().isAnonymous()) {

				cell = sheet.getCellByPosition(1, rowIndex);
				cell.setStringValue("Answers");
			}

			cell = sheet.getCellByPosition(2, rowIndex);
			cell.setStringValue("Creation date");

			rowIndex++;

			for (Invitation invitation : invitations) {
				cell = sheet.getCellByPosition(0, rowIndex);
				cell.setStringValue(invitation.getUniqueId());

				if (!export.getSurvey().isAnonymous()) {
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.setStringValue(invitation.getAnswers().toString());

					cell = sheet.getCellByPosition(2, rowIndex);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(invitation.getInvited());
					cell.setDateTimeValue(calendar);

				} else {
					cell = sheet.getCellByPosition(1, rowIndex);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(invitation.getInvited());
					cell.setDateTimeValue(calendar);
				}

				rowIndex++;
			}

		}

		spreadsheet.save(outputStream);
	}

	@Override
	void exportECFGlobalResults() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	void exportECFProfileResults() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	void exportECFOrganizationalResults() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	void exportPDFReport() throws Exception {
		throw new NotImplementedException();
	}
}
