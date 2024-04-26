package com.ec.survey.tools.export;

import com.ec.survey.model.Answer;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Export;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.FilesByTypes;
import com.ec.survey.model.FilesByType;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.ComplexTableItem.CellType;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.model.survey.quiz.QuizResult;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.QuizHelper;
import com.ec.survey.tools.Tools;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Hibernate;
import org.hibernate.query.NativeQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.Map.Entry;

@Service("xmlExportCreator")
@Scope("prototype")
public class XmlExportCreator extends ExportCreator {

	private Map<Integer, String> exportedUniqueCodes = new HashMap<>();

	private Date exportedNow = null;

	private static final String ANSWER = "Answer";
	private static final String EXPLANATION = "Explanation";
	private static final String EXPLANATION_FILE = "ExplanationFile";
	private static final String EXPLANATION_LIKE = "ExplanationLike";
	private static final String EXPLANATION_TEXT = "ExplanationText";
	private static final String DISCUSSION = "Discussion";

	private static final String[] POSSIBLE_EXPORTS_ORDERED = {
		"invitation",
		"case",
		"user",
		"created",
		"updated",
		"languages",
		"score"
	};
	
	@Override
	@Transactional
	public void exportContent(boolean sync) throws Exception {
		exportContent(sync, export, false);
	}

	@Transactional
	public void exportContent(boolean sync, Export export, boolean fromWebService) throws Exception {
		exportedUniqueCodes.clear();
		XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");

		writer.writeStartDocument("UTF-8", "1.0");
		writer.writeCharacters("\n");
		writer.writeStartElement("Results");

		exportedNow = new Date();

		writer.writeAttribute("create", Tools.formatDate(exportedNow, "yyyy-MM-dd_HH-mm-ss"));

		writer.writeStartElement("Survey");

		writer.writeAttribute("id", form.getSurvey().getId().toString());
		writer.writeAttribute("uid", form.getSurvey().getUniqueId());
		writer.writeAttribute("alias", form.getSurvey().getShortname());

		Map<String, List<Element>> questionlists = new HashMap<>();

		Session session;
		session = sessionFactory.getCurrentSession();

		if (export != null) {
			session.evict(export.getSurvey());
		}

		form.setSurvey(surveyService.initializeAndMergeSurvey(form.getSurvey()));

		if (export != null && export.isAllAnswers() && !form.getSurvey().isMissingElementsChecked()) {
			surveyService.checkAndRecreateMissingElements(form.getSurvey(), export.getResultFilter());
			Hibernate.initialize(form.getSurvey().getMissingElements());
			for (Element e : form.getSurvey().getMissingElements()) {
				if (e instanceof ChoiceQuestion) {
					Hibernate.initialize(((ChoiceQuestion) e).getPossibleAnswers());
				} else if (e instanceof GalleryQuestion) {
					Hibernate.initialize(((GalleryQuestion) e).getFiles());
				}
			}
		}

		if (form.getSurvey().getTranslations() == null) {
			List<String> translations = translationService.getTranslationLanguagesForSurvey(form.getSurvey().getId());
			form.getSurvey().setTranslations(translations);
		}

		for (String lang : form.getSurvey().getTranslations()) {
			writer.writeStartElement("Elements");
			writer.writeAttribute("lang", lang);

			Survey survey = surveyService.getSurvey(form.getSurvey().getId(), lang);

			List<Element> questionslist = survey.getQuestionsAndSections();
			questionlists.put(lang, questionslist);

			for (Element question : questionslist) {
				if (question instanceof Matrix) {
					Matrix matrix = (Matrix) question;

					writer.writeStartElement("MatrixTitle");
					writer.writeAttribute("id", question.getUniqueId());
					writer.writeAttribute("type", getNiceType(question));

					if (export != null && export.getShowShortnames()) {
						writer.writeAttribute("bid", matrix.getShortname());
					}

					writer.writeCharacters(matrix.getTitle());
					writer.writeEndElement(); // MatrixTitle

					for (Element matrixQuestion : matrix.getQuestions()) {
						writer.writeStartElement("MatrixQuestion");
						writer.writeAttribute("id", matrixQuestion.getUniqueId());
						writer.writeAttribute("type", (matrix.getIsSingleChoice() ? "Single Choice" : "Multiple Choice")
								+ " Matrix Question");

						if (export != null && export.getShowShortnames()) {
							writer.writeAttribute("bid", matrixQuestion.getShortname());
						}

						writer.writeCharacters(matrixQuestion.getTitle());

						writer.writeEndElement(); // MatrixQuestion
					}
					for (Element matrixAnswer : matrix.getAnswers()) {
						writer.writeStartElement("MatrixAnswer");
						writer.writeAttribute("id", matrixAnswer.getUniqueId());
						writer.writeAttribute("type", "Matrix Answer");

						if (export != null && export.getShowShortnames()) {
							writer.writeAttribute("bid", matrixAnswer.getShortname());
						}

						writer.writeCharacters(matrixAnswer.getTitle());
						writer.writeEndElement(); // MatrixAnswer
					}

					int position = 0;
					for (Element matrixQuestion : matrix.getQuestions()) {
						for (Element matrixAnswer : matrix.getAnswers()) {
							writer.writeStartElement("MatrixCell");

							writer.writeAttribute("qid", matrixQuestion.getUniqueId());
							writer.writeAttribute("aid", matrixAnswer.getUniqueId());

							String dependentElements = "";

							for (DependencyItem dep : matrix.getDependentElements()) {
								if (dep != null && dep.getPosition() == position) {
									for (Element element : dep.getDependentElements()) {
										if (dependentElements.length() > 0) {
											dependentElements += ";";
										}
										dependentElements += element.getUniqueId();
									}
								}
							}

							if (dependentElements.length() > 0) {
								writer.writeAttribute("dependentElements", dependentElements);
							}

							writer.writeEndElement(); // MatrixCell

							position++;
						}
					}
				} else if (question instanceof ComplexTable) {
					ComplexTable table = (ComplexTable) question;

					writer.writeStartElement("ComplexTableTitle");
					writer.writeAttribute("id", question.getUniqueId());
					writer.writeAttribute("type", getNiceType(question));

					if (export != null && export.getShowShortnames()) {
						writer.writeAttribute("bid", table.getShortname());
					}

					writer.writeCharacters(table.getTitle());
					writer.writeEndElement(); // ComplexTableTitle

					for (ComplexTableItem child : table.getChildElements()) {
						writer.writeStartElement("Cell");
						writer.writeAttribute("id", child.getUniqueId());
						writer.writeAttribute("type", child.getCellType().toString());
						writer.writeAttribute("row", Integer.toString(child.getRow()));
						writer.writeAttribute("column", Integer.toString(child.getColumn()));

						if (export != null && export.getShowShortnames()) {
							writer.writeAttribute("bid", child.getShortname());
						}

						writer.writeStartElement("CellTitle");
						writer.writeCharacters(child.getTitle());
						writer.writeEndElement(); // CellTitle
						
						writer.writeStartElement("ResultText");
						writer.writeCharacters(child.getResultTitle(table));
						writer.writeEndElement(); // ResultText

						if (child.getCellType() == CellType.SingleChoice || child.getCellType() == CellType.MultipleChoice) {
							for (PossibleAnswer answer : child.getPossibleAnswers()) {
								writer.writeStartElement(ANSWER);
								writer.writeAttribute("id", answer.getUniqueId());
								writer.writeAttribute("type", getNiceType(answer));

								if (export != null && export.getShowShortnames()) {
									writer.writeAttribute("bid", answer.getShortname());
								}
								
								writer.writeCharacters(answer.getTitle());
								writer.writeEndElement(); // Answer
							}
						}					
						
						writer.writeEndElement(); // Cell
					}
				} else if (question instanceof Table) {
					Table table = (Table) question;

					writer.writeStartElement("TableTitle");
					writer.writeAttribute("id", question.getUniqueId());
					writer.writeAttribute("type", getNiceType(question));

					if (export != null && export.getShowShortnames()) {
						writer.writeAttribute("bid", table.getShortname());
					}

					writer.writeCharacters(table.getTitle());
					writer.writeEndElement(); // TableTitle

					for (Element tableQuestion : table.getQuestions()) {
						writer.writeStartElement("TableQuestion");
						writer.writeAttribute("id", tableQuestion.getUniqueId());
						writer.writeAttribute("type", "Table Question");

						if (export != null && export.getShowShortnames()) {
							writer.writeAttribute("bid", tableQuestion.getShortname());
						}

						writer.writeCharacters(tableQuestion.getTitle());

						writer.writeEndElement(); // TableQuestion
					}
					for (Element tableAnswer : table.getAnswers()) {
						writer.writeStartElement("TableAnswer");
						writer.writeAttribute("id", tableAnswer.getUniqueId());
						writer.writeAttribute("type", "Table Answer");

						if (export != null && export.getShowShortnames()) {
							writer.writeAttribute("bid", tableAnswer.getShortname());
						}

						writer.writeCharacters(tableAnswer.getTitle());

						writer.writeEndElement(); // TableAnswer
					}
				} else if (question instanceof Text) {
					writer.writeStartElement("Text");
					writer.writeAttribute("id", question.getUniqueId());
					writer.writeAttribute("type", getNiceType(question));

					if (export != null && export.getShowShortnames()) {
						writer.writeAttribute("bid", question.getShortname());
					}

					writer.writeCharacters(question.getTitle());

					writer.writeEndElement(); // Text
				} else if (question instanceof Image) {
					writer.writeStartElement("Image");
					writer.writeAttribute("id", question.getUniqueId());
					writer.writeAttribute("type", getNiceType(question));

					if (export != null && export.getShowShortnames()) {
						writer.writeAttribute("bid", question.getShortname());
					}

					writer.writeCharacters(question.getTitle());

					writer.writeEndElement(); // Image
				} else if (question instanceof Section) {
					writer.writeStartElement("Section");
					writer.writeAttribute("id", question.getUniqueId());
					writer.writeAttribute("type", getNiceType(question));

					if (export != null && export.getShowShortnames()) {
						writer.writeAttribute("bid", question.getShortname());
					}

					writer.writeCharacters(question.getTitle());

					writer.writeEndElement(); // Section
				} else {
					writer.writeStartElement("Question");
					writer.writeAttribute("id", question.getUniqueId());
					writer.writeAttribute("type", getNiceType(question));

					if (export != null && export.getShowShortnames()) {
						writer.writeAttribute("bid", question.getShortname());
					}

					writer.writeCharacters(question.getTitle());

					writer.writeEndElement(); // Question

					if (question instanceof ChoiceQuestion) {
						ChoiceQuestion choice = (ChoiceQuestion) question;

						for (PossibleAnswer answer : choice.getPossibleAnswers()) {
							writer.writeStartElement(ANSWER);
							writer.writeAttribute("id", answer.getUniqueId());
							writer.writeAttribute("type", getNiceType(answer));

							if (export != null && export.getShowShortnames()) {
								writer.writeAttribute("bid", answer.getShortname());
							}

							String dependentElements = "";
							for (Element element : answer.getDependentElements().getDependentElements()) {
								if (dependentElements.length() > 0) {
									dependentElements += ";";
								}
								dependentElements += element.getUniqueId();
							}
							if (dependentElements.length() > 0) {
								writer.writeAttribute("dependentElements", dependentElements);
							}

							writer.writeCharacters(answer.getTitle());

							writer.writeEndElement(); // Answer
						}
					} else if (question instanceof RatingQuestion) {
						RatingQuestion rating = (RatingQuestion) question;

						for (Element childQuestion : rating.getQuestions()) {
							writer.writeStartElement("RatingQuestion");
							writer.writeAttribute("id", childQuestion.getUniqueId());
							writer.writeAttribute("type", getNiceType(rating));

							if (export != null && export.getShowShortnames()) {
								writer.writeAttribute("bid", childQuestion.getShortname());
							}

							writer.writeCharacters(rating.getTitle() + ": " + childQuestion.getTitle());

							writer.writeEndElement(); // RatingQuestion
						}
					} else if (question instanceof RankingQuestion) {
						RankingQuestion ranking = (RankingQuestion) question;

						for (Element child : ranking.getChildElements()) {
							writer.writeStartElement("RankingItem");
							writer.writeAttribute("id", child.getUniqueId());
							writer.writeAttribute("type", getNiceType(child));

							if (export != null && export.getShowShortnames()) {
								writer.writeAttribute("bid", child.getShortname());
							}

							writer.writeCharacters(child.getTitle());

							writer.writeEndElement(); // RankingQuestion
						}
					}

				}
			}
			writer.writeEndElement(); // elements
		}

		writer.writeEndElement(); // survey

		List<File> uploadedFiles = answerService.getAllUploadedFiles(form.getSurvey().getId(),
				export == null ? null : export.getResultFilter(), 1, Integer.MAX_VALUE);
		Map<Integer, List<File>> filesByAnswer = new HashMap<>();
		for (File file : uploadedFiles) {
			if (file.getAnswerId() != null) {
				if (!filesByAnswer.containsKey(file.getAnswerId())) {
					filesByAnswer.put(file.getAnswerId(), new ArrayList<>());
				}
				filesByAnswer.get(file.getAnswerId()).add(file);
			}
		}

		FilesByTypes<Integer, String> explanationFilesOfSurvey =
				answerExplanationService.getExplanationFilesByAnswerSetIdAndQuestionUid(form.getSurvey());
		FilesByType<String> explanationFilesToExport = new FilesByType<>();

		HashMap<String, Object> values = new HashMap<>();
				
		ResultFilter origFilter = answerService.initialize(export.getResultFilter());
		ResultFilter filterWithMeta = export == null ? null : origFilter.copy();

		if (filterWithMeta != null) {
			filterWithMeta.getVisibleQuestions().clear();
			filterWithMeta.getVisibleQuestions().addAll(filterWithMeta.getExportedQuestions());

			if (filterWithMeta.getVisibleQuestions().isEmpty()) {
				// initially add all questions
				for (Element question : form.getSurvey().getQuestionsAndSections()) {
					filterWithMeta.getVisibleQuestions().add(question.getId().toString());
				}

				filterWithMeta.getVisibleQuestions().add("invitation");
				filterWithMeta.getVisibleQuestions().add("user");
				filterWithMeta.getVisibleQuestions().add("created");
				filterWithMeta.getVisibleQuestions().add("updated");
				filterWithMeta.getVisibleQuestions().add("languages");
			}
			
			filterWithMeta.getVisibleExplanations().clear();
			filterWithMeta.getVisibleExplanations().addAll(filterWithMeta.getExportedExplanations());

			filterWithMeta.getVisibleDiscussions().clear();
			filterWithMeta.getVisibleDiscussions().addAll(filterWithMeta.getExportedDiscussions());
		}

		filterWithMeta.getVisibleQuestions().add("score");

		if (fromWebService) {
			filterWithMeta.getVisibleQuestions().add("case");
		}

		filterWithMeta.setExportedQuestions(filterWithMeta.getVisibleQuestions());

		// for quiz surveys we need the original answerSet instances in order to compute the scores
		List<List<String>> answersets = form.getSurvey().getIsQuiz() ? null : reportingService.getAnswerSets(form.getSurvey(), filterWithMeta, null, false,
				true, true, true, true, false);

		Map<String, List<File>> uploadedFilesByQuestionUID = new HashMap<>();

		writer.writeStartElement("Answers");
		
		Map<String, String> ECASUserLoginsByEmail = null;

		if (export.getAddMeta() || filterWithMeta.exported("user")) {
			ECASUserLoginsByEmail = administrationService.getECASUserLoginsByEmail();
		}

		if (answersets != null) {

			//Exports may be found at different indices depending on which details should be exported
			//This maps all exports to the correct index
			HashMap<String, Integer> rowPosMap = null;

			if (filterWithMeta != null) {
				rowPosMap = new HashMap<>();
				int rowPosCount = answersets.isEmpty() ? 0 : answersets.get(0).size() - 1;
				for (int i = POSSIBLE_EXPORTS_ORDERED.length - 1; i >= 0; i--) {
					String exp = POSSIBLE_EXPORTS_ORDERED[i];
					if (filterWithMeta.exported(exp)) {
						rowPosMap.put(exp, rowPosCount);
						rowPosCount--;
					}
				}
			}

			for (List<String> row : answersets) {
				String lang = row.get(row.size() - 2);
				List<Element> questions = form.getSurvey().getQuestionsAndSections();
				if (questionlists.containsKey(lang)) {
					questions = questionlists.get(lang);
				}
				parseAnswerSet(form.getSurvey(), writer, questions, null, row, row.get(1), filesByAnswer,
						uploadedFilesByQuestionUID, export.getAddMeta(), filterWithMeta, ECASUserLoginsByEmail, null, null, null,
						explanationFilesOfSurvey, explanationFilesToExport, rowPosMap);
			}
		} else {

			//it is not possible to query the database after the result query was executed
			Map<Integer, Map<String, String>> explanations = answerExplanationService.getAllExplanations(form.getSurvey());
			Map<Integer, Map<String, String>> discussions = answerExplanationService.getAllDiscussions(form.getSurvey());
			Map<String, Integer> likesForExplanations = answerExplanationService.getAllLikesForExplanation(form.getSurvey());
					
			String sql = "select ans.ANSWER_SET_ID, a.QUESTION_UID, a.VALUE, a.ANSWER_COL, a.ANSWER_ID, a.ANSWER_ROW, a.PA_UID, ans.UNIQUECODE, ans.ANSWER_SET_DATE, ans.ANSWER_SET_UPDATE, ans.ANSWER_SET_INVID, ans.RESPONDER_EMAIL, ans.ANSWER_SET_LANG, a.AS_ID, ans.SCORE FROM ANSWERS a RIGHT JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID IN ("
					+ answerService.getSql(null, form.getSurvey().getId(),
							export == null ? null : export.getResultFilter(), values, true)
					+ ") ORDER BY ans.ANSWER_SET_ID";

			NativeQuery query = makeQuery(sql, session, values);
			ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

			int lastAnswerSet = 0;
			AnswerSet answerSet = new AnswerSet();
			answerSet.setSurvey(form.getSurvey());

			String list = "";

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
						parseAnswerSet(form.getSurvey(), writer, questionlists.get(answerSet.getLanguageCode()),
								answerSet, null, list, filesByAnswer, uploadedFilesByQuestionUID, export.getAddMeta(),
								filterWithMeta, ECASUserLoginsByEmail, explanations, discussions, likesForExplanations,
								explanationFilesOfSurvey, explanationFilesToExport, null);
					}

					answerSet = new AnswerSet();
					answerSet.setSurvey(form.getSurvey());
					answerSet.setId(answer.getAnswerSetId());
					lastAnswerSet = answer.getAnswerSetId();
					answerSet.getAnswers().add(answer);
					answerSet.setUniqueCode((String) a[7]);
					answerSet.setDate((Date) a[8]);
					answerSet.setUpdateDate((Date) a[9]);
					answerSet.setInvitationId((String) a[10]);
					answerSet.setResponderEmail((String) a[11]);
					answerSet.setLanguageCode((String) a[12]);
					Integer ilist = ConversionTools.getValue(a[13]);
					list = ilist.toString();

					if (answerSet.getLanguageCode() == null
							|| !questionlists.containsKey(answerSet.getLanguageCode())) {
						answerSet.setLanguageCode(questionlists.keySet().toArray()[0].toString());
					}

					answerSet.setScore(ConversionTools.getValue(a[14]));
				}
			}
			if (lastAnswerSet > 0)
				parseAnswerSet(form.getSurvey(), writer, questionlists.get(answerSet.getLanguageCode()), answerSet,
						null, list, filesByAnswer, uploadedFilesByQuestionUID, export.getAddMeta(), filterWithMeta,
						ECASUserLoginsByEmail, explanations, discussions, likesForExplanations, explanationFilesOfSurvey, explanationFilesToExport, null);
			results.close();
		}

		writer.writeEndElement(); // Answers
		writer.writeEndElement(); // Results

		if (!uploadedFiles.isEmpty() || explanationFilesToExport.hasFiles()) {
			// there are multiple files
			java.io.File temp = new java.io.File(exportFilePath + ".zip");
			final OutputStream out = new FileOutputStream(temp);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);

			os.putArchiveEntry(new ZipArchiveEntry(FilenameUtils.getName(exportFilePath)));
			IOUtils.copy(new FileInputStream(exportFilePath), os);
			os.closeArchiveEntry();

			for (Entry<String, List<File>> entry : uploadedFilesByQuestionUID.entrySet()) {
				for (File file : entry.getValue()) {
					java.io.File f = new java.io.File(exportService.getFileDir() + file.getUid());

					if (f.exists()) {
						os.putArchiveEntry(
								new ZipArchiveEntry(entry.getKey() + Constants.PATH_DELIMITER + file.getUid() + "_" + file.getName()));
						IOUtils.copy(new FileInputStream(f), os);
						os.closeArchiveEntry();
					}
				}
			}

			explanationFilesToExport.applyFunctionOnEachFile((questionUid, explanationFile) -> {
				java.io.File file = fileService.getSurveyFile(form.getSurvey().getUniqueId(), explanationFile.getUid());

				if (file.exists()) {
					os.putArchiveEntry(new ZipArchiveEntry(questionUid + Constants.PATH_DELIMITER +
							explanationFile.getUid() + "_" + explanationFile.getName()));
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

	private String getNiceType(Element question) {
		if (question instanceof Section) {
			return "Section";
		} else if (question instanceof FreeTextQuestion) {
			return "Free Text";
		} else if (question instanceof MultipleChoiceQuestion) {
			return "Multiple Choice";
		} else if (question instanceof SingleChoiceQuestion) {
			return "Single Choice";
		} else if (question instanceof PossibleAnswer) {
			return "Choice Answer";
		} else if (question instanceof NumberQuestion) {
			return "Number";
		} else if (question instanceof DateQuestion) {
			return "Date";
		} else if (question instanceof TimeQuestion) {
			return "Time";
		} else if (question instanceof Matrix) {
			return "Matrix";
		} else if (question instanceof Table) {
			return "Table";
		} else if (question instanceof Text) {
			return "Text";
		} else if (question instanceof Image) {
			return "Image";
		} else if (question instanceof Ruler) {
			return "Line";
		} else if (question instanceof Upload) {
			return "File Upload";
		} else if (question instanceof Download) {
			return "File Download";
		} else if (question instanceof EmailQuestion) {
			return "E-mail";
		} else if (question instanceof RegExQuestion) {
			return "Regular Expression";
		} else if (question instanceof GalleryQuestion) {
			return "Gallery";
		} else if (question instanceof Confirmation) {
			return "Confirmation";
		} else if (question instanceof RatingQuestion) {
			return "Rating";
		} else if (question instanceof RankingQuestion) {
			return "Ranking";
		} else if (question instanceof RankingItem) {
			return "Ranking Item";
		} else if (question instanceof ComplexTable) {
			return "Complex Table";
		} else if (question instanceof ComplexTableItem) {
			return "Complex Table Item";
		}

		return question.getType();
	}

	@Transactional
	public void simulateExportContent(boolean sync, Export export) throws Exception {
		exportedUniqueCodes.clear();

		Session session;
		session = sessionFactory.getCurrentSession();

		if (export != null) {
			session.evict(export.getSurvey());
		}

		exportedNow = new Date();
		
		List<List<String>> answersets = reportingService.getAnswerSets(form.getSurvey(), export == null ? null : export.getResultFilter(), null, false,
				true, true, true, true, false);
		

		if (answersets != null) {
			for (List<String> row : answersets) {
				
				int answerSetId = ConversionTools.getValue(row.get(1));
				String answerSetUniqueCode = row.get(0);
								
				if (!exportedUniqueCodes.containsKey(answerSetId)) {
					exportedUniqueCodes.put(answerSetId, answerSetUniqueCode);
				}
			}
		} else {

			HashMap<String, Object> values = new HashMap<>();
			String sql = "select ans.ANSWER_SET_ID, ans.UNIQUECODE FROM ANSWERS a RIGHT JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID IN ("
					+ answerService.getSql(null, form.getSurvey().getId(), export == null ? null : export.getResultFilter(),
							values, true)
					+ ") ORDER BY ans.ANSWER_SET_ID";

			NativeQuery query = makeQuery(sql, session, values);
			ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

			while (results.next()) {
				Object[] a = results.get();
				if (!exportedUniqueCodes.containsKey(ConversionTools.getValue(a[0]))) {
					exportedUniqueCodes.put(ConversionTools.getValue(a[0]), (String) a[1]);
				}
			}
			results.close();		
		}
	}

	private NativeQuery makeQuery(String sql, Session session, HashMap<String, Object> values) {
		NativeQuery query = session.createSQLQuery(sql);

		query.setReadOnly(true);

		for (Entry<String, Object> entry : values.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof String) {
				query.setString(entry.getKey(), (String) value);
			} else if (value instanceof Integer) {
				query.setInteger(entry.getKey(), (Integer) value);
			} else if (value instanceof Date) {
				query.setTimestamp(entry.getKey(), (Date) value);
			}
		}

		query.setFetchSize(Integer.MIN_VALUE);
		return query;
	}

	void parseAnswerSet(Survey survey, XMLStreamWriter writer, List<Element> questions, AnswerSet answerSet,
			List<String> row, String list, Map<Integer, List<File>> filesByAnswer,
			Map<String, List<File>> uploadedFilesByQuestionUID, boolean meta, ResultFilter filter,
			Map<String, String> ECASUserLoginsByEmail, Map<Integer, Map<String, String>> explanations,
			Map<Integer, Map<String, String>> discussions, Map<String, Integer> likesForExplanations, FilesByTypes<Integer, String> explanationFilesOfSurvey,
			FilesByType<String> explanationFilesToExport, HashMap<String, Integer> rowPosMap) throws XMLStreamException {
		writer.writeStartElement("AnswerSet");

		if (meta || filter == null || filter.exported("case")) {
			if (survey.getSecurity().contains("anonymous")) {
				writer.writeAttribute("id", "Anonymous");
			} else if (answerSet == null) {
				writer.writeAttribute("id", row.get(0));
				exportedUniqueCodes.put(Integer.parseInt(row.get(1)), row.get(0));
			} else {
				writer.writeAttribute("id", answerSet.getUniqueCode());
				exportedUniqueCodes.put(answerSet.getId(), answerSet.getUniqueCode());
			}
		}

		if (meta || filter == null || filter.exported("created"))
			writer.writeAttribute("create", answerSet == null ? row.get(rowPosMap.get("created"))
					: Tools.formatDate(answerSet.getDate(), "yyyy-MM-dd_HH-mm-ss"));

		if (meta || filter == null || filter.exported("updated"))
			writer.writeAttribute("last", answerSet == null ? row.get(rowPosMap.get("updated"))
					: Tools.formatDate(answerSet.getUpdateDate(), "yyyy-MM-dd_HH-mm-ss"));

		writer.writeAttribute("list", list);

		if (meta || filter == null || filter.exported("languages"))
			writer.writeAttribute("lang", answerSet == null ? row.get(rowPosMap.get("languages")) : answerSet.getLanguageCode());

		if (meta || filter == null || filter.exported("user"))			
			if (survey.getSecurity().contains("anonymous")) {
				writer.writeAttribute("user", "Anonymous");
			} else {
				writer.writeAttribute("user", answerSet == null ? row.get(rowPosMap.get("user")): answerSet.getResponderEmail() != null ? answerSet.getResponderEmail() : "");
			}

		if (meta || filter == null || filter.exported("invitation"))
			writer.writeAttribute("invitation", answerSet == null ? row.get(rowPosMap.get("invitation"))
					: answerSet.getInvitationId() != null ? answerSet.getInvitationId() : "");
		if (meta || filter == null || filter.exported("user")) {
			if (survey.getSecurity().contains("anonymous")) {
				writer.writeAttribute("userlogin", "Anonymous");
			} else {
				String suser = answerSet == null ? row.get(rowPosMap.get("user")) : answerSet.getResponderEmail();
				if (suser != null && suser.contains("@") && ECASUserLoginsByEmail != null
						&& ECASUserLoginsByEmail.containsKey(suser)) {
					writer.writeAttribute("userlogin", ECASUserLoginsByEmail.get(suser));
				}
			}
		}

		if (survey.getIsQuiz()) {
			writer.writeAttribute("totalscore",
					answerSet == null ? (row.get(row.size() - 1) != null ? row.get(row.size() - 1) : "0")
							: (answerSet.getScore() != null ? answerSet.getScore().toString() : "0"));
		}

		int answerrowcounter = 2;
		for (Element question : questions) {
			if ((filter == null || filter.exported(question.getId().toString())) && question.isUsedInResults()) {
				if (question instanceof Matrix) {
					Matrix matrix = (Matrix) question;
					for (Element matrixQuestion : matrix.getQuestions()) {
						if (answerSet == null) {
							String sanswers = row.get(answerrowcounter++);
							if (sanswers != null) {
								String[] answers = sanswers.split(";");
								for (String answer : answers) {
									if (answer.length() > 0) {
										writer.writeStartElement(ANSWER);
										writer.writeAttribute("aid", answer);
										writer.writeAttribute("qid", matrixQuestion.getUniqueId());
										writer.writeEndElement(); // Answer
									}
								}
							}
						} else {
							List<Answer> answers = answerSet.getAnswers(matrixQuestion.getUniqueId());

							for (Answer answer : answers) {
								writer.writeStartElement(ANSWER);
								writer.writeAttribute("aid",
										answer.getPossibleAnswerUniqueId() != null
												? answer.getPossibleAnswerUniqueId()
												: "");
								writer.writeAttribute("qid", matrixQuestion.getUniqueId());
								writer.writeEndElement(); // Answer
							}
						}
					}

					if (answerSet == null) {
						//catch case: explanation, likes and discussion shown at results page but not included in export
						if (filter != null && filter.explanationVisible(question.getId().toString()) && !filter.explanationExported(question.getId().toString())) {
							answerrowcounter += 2;	//skip explanation and likes of it
						}
						if (filter != null && filter.discussionVisible(question.getId().toString()) && !filter.discussionExported(question.getId().toString())) {
							answerrowcounter++;		//skip discussion
						}
					}
				} else if (question instanceof ComplexTable) {
					ComplexTable table = (ComplexTable) question;
					for (ComplexTableItem childQuestion : table.getQuestionChildElements()) {
						if (answerSet == null) {
							String sanswers = row.get(answerrowcounter++);
							if (sanswers != null) {								
								if (childQuestion.getCellType() == ComplexTableItem.CellType.SingleChoice || childQuestion.getCellType() == ComplexTableItem.CellType.MultipleChoice)
								{
									String[] answers = sanswers.split(";");
									for (String answer : answers) {
										if (answer.length() > 0) {
											writer.writeStartElement(ANSWER);										
											writer.writeAttribute("qid", childQuestion.getUniqueId());
											writer.writeAttribute("aid", answer);
											writer.writeEndElement(); // Answer
										}
									}
								} else {
									writer.writeStartElement(ANSWER);										
									writer.writeAttribute("qid", childQuestion.getUniqueId());
									writer.writeCharacters(sanswers);
									writer.writeEndElement(); // Answer
								}								
							}
						} else {
							List<Answer> answers = answerSet.getAnswers(childQuestion.getUniqueId());
							if (childQuestion.getCellType() == ComplexTableItem.CellType.SingleChoice || childQuestion.getCellType() == ComplexTableItem.CellType.MultipleChoice)
							{
								for (Answer answer : answers) {
									writer.writeStartElement(ANSWER);
									writer.writeAttribute("aid",
											answer.getPossibleAnswerUniqueId() != null
													? answer.getPossibleAnswerUniqueId()
													: "");
									writer.writeAttribute("qid", childQuestion.getUniqueId());
									writer.writeEndElement(); // Answer
								}
							} else {
								if (!answers.isEmpty()) {
									writer.writeStartElement(ANSWER);
									writer.writeAttribute("qid", childQuestion.getUniqueId());
									writer.writeCharacters(answers.get(0).getValue());
									writer.writeEndElement(); // Answer
								}
							}
						}
					}
				} else if (question instanceof RatingQuestion) {
					RatingQuestion rating = (RatingQuestion) question;
					for (Element childQuestion : rating.getQuestions()) {
						if (answerSet == null) {
							String sanswers = row.get(answerrowcounter++);
							if (sanswers != null) {
								writer.writeStartElement(ANSWER);
								writer.writeAttribute("qid", childQuestion.getUniqueId());
								writer.writeCharacters(sanswers);
								writer.writeEndElement(); // Answer
							}
						} else {
							List<Answer> answers = answerSet.getAnswers(childQuestion.getUniqueId());

							if (!answers.isEmpty()) {
								writer.writeStartElement(ANSWER);
								writer.writeAttribute("qid", childQuestion.getUniqueId());
								writer.writeCharacters(answers.get(0).getValue());
								writer.writeEndElement(); // Answer
							}
						}
					}

					if (answerSet == null) {
						//catch case: explanation, likes and discussion shown at results page but not included in export
						if (filter != null && filter.explanationVisible(question.getId().toString()) && !filter.explanationExported(question.getId().toString())) {
							answerrowcounter += 2;	//skip explanation and likes of it
						}
						if (filter != null && filter.discussionVisible(question.getId().toString()) && !filter.discussionExported(question.getId().toString())) {
							answerrowcounter++;		//skip discussion
						}
					}
				} else if (question instanceof Table) {
					Table table = (Table) question;

					for (int tableRow = 1; tableRow < table.getRows(); tableRow++) {
						for (int tableCol = 1; tableCol < table.getColumns(); tableCol++) {
							Element tq = table.getQuestions().get(tableRow - 1);
							Element ta = table.getAnswers().get(tableCol - 1);

							writer.writeStartElement(ANSWER);
							writer.writeAttribute("qid", tq.getUniqueId());
							writer.writeAttribute("aid", ta.getUniqueId());

							if (answerSet == null) {
								String sanswers = row.get(answerrowcounter++);
								if (sanswers != null) {
									writer.writeCharacters(sanswers);
								}
							} else {
								String answer = answerSet.getTableAnswer(table, tableRow, tableCol, false);
								if (answer != null && answer.length() > 0) {
									writer.writeCharacters(answer);
								}
							}

							writer.writeEndElement(); // Answer
						}
					}

					if (answerSet == null) {
						//catch case: explanation, likes and discussion shown at results page but not included in export
						if (filter != null && filter.explanationVisible(question.getId().toString()) && !filter.explanationExported(question.getId().toString())) {
							answerrowcounter += 2;	//skip explanation and likes of it
						}
						if (filter != null && filter.discussionVisible(question.getId().toString()) && !filter.discussionExported(question.getId().toString())) {
							answerrowcounter++;		//skip discussion
						}
					}
				} else if (question instanceof Text) {
					// ignore
				} else if (question instanceof Question) {
					if (answerSet == null) {
						if (row.size() <= answerrowcounter) {
							logger.error("no data for question " + question.getId() + " found");
						} else {

							String sanswers = row.get(answerrowcounter++);

							//catch case: explanation, likes and discussion shown at results page but not included in export
							if (filter != null && filter.explanationVisible(question.getId().toString()) && !filter.explanationExported(question.getId().toString())) {
								answerrowcounter += 2;	//skip explanation and likes of it
							}
							if (filter != null && filter.discussionVisible(question.getId().toString()) && !filter.discussionExported(question.getId().toString())) {
								answerrowcounter++;		//skip discussion
							}

							if (sanswers != null) {

								String[] answers;
								if (question instanceof FreeTextQuestion || question instanceof RankingQuestion) {
									answers = new String[1];
									answers[0] = sanswers;
								} else {
									answers = sanswers.split(";");
								}

								for (String answer : answers) {
									if (answer.length() > 0) {
										answer = answer.trim();
										writer.writeStartElement(ANSWER);
										writer.writeAttribute("qid", question.getUniqueId());
										if (question instanceof ChoiceQuestion) {
											writer.writeAttribute("aid", answer);
										} else if (question instanceof Upload) {
											StringBuilder text = new StringBuilder();
											File file;
											try {

												if (answer.contains("|")) {
													answer = answer.substring(0, answer.indexOf('|'));
												}

												file = fileService.get(answer);
												if (!uploadedFilesByQuestionUID
														.containsKey(question.getUniqueId())) {
													uploadedFilesByQuestionUID.put(question.getUniqueId(),
															new ArrayList<>());
												}
												uploadedFilesByQuestionUID.get(question.getUniqueId()).add(file);

												text.append((text.length() > 0) ? ";" : "").append(file.getName());
											} catch (FileNotFoundException e) {
												logger.error(e.getLocalizedMessage(), e);
											}

											writer.writeCharacters(text.toString());
										} else {
											writer.writeCharacters(
													ConversionTools.removeInvalidHtmlEntities(answer));
										}

										writer.writeEndElement(); // Answer
									}
								}
							}
						}
					} else {
						List<Answer> answers = answerSet.getAnswers(question.getUniqueId());
						for (Answer answer : answers) {
							writer.writeStartElement(ANSWER);
							writer.writeAttribute("qid", question.getUniqueId());

							if (question instanceof ChoiceQuestion) {
								writer.writeAttribute("aid",
										answer.getPossibleAnswerUniqueId() != null
												? answer.getPossibleAnswerUniqueId()
												: "");
							} else if (question instanceof RankingQuestion) {
								writer.writeCharacters(answer.getValue());
							} else if (question instanceof Upload) {
								StringBuilder text = new StringBuilder();
								if (filesByAnswer.containsKey(answer.getId())) {
									for (File file : filesByAnswer.get(answer.getId())) {
										if (!uploadedFilesByQuestionUID.containsKey(question.getUniqueId())) {
											uploadedFilesByQuestionUID.put(question.getUniqueId(),
													new ArrayList<>());
										}
										uploadedFilesByQuestionUID.get(question.getUniqueId()).add(file);

										text.append((text.length() > 0) ? ";" : "").append(file.getName());
									}
								}
								writer.writeCharacters(text.toString());
							} else {
								writer.writeCharacters(form.getAnswerTitleStripInvalidXML(answer));
							}

							writer.writeEndElement(); // Answer
						}
					}
				}
				
				if (question.isDelphiElement() && filter != null && filter.explanationExported(question.getId().toString())) {

					final String questionUid = question.getUniqueId();
					String explanation = "";

					if (answerSet == null) {
						
						final String cellContent = row.get(answerrowcounter++);
						final int lastLineBreakPosition = cellContent.lastIndexOf("\n");
						String filesPart = "";
						if (lastLineBreakPosition == -1) {
							explanation = cellContent;
							
							if (explanation.contains("|"))
							{
								filesPart = cellContent;
								explanation = "";
							}
						} else {
							explanation = cellContent.substring(0, lastLineBreakPosition);
							filesPart = cellContent.substring(lastLineBreakPosition + 1);						
						}
						
						final String[] filesParts = filesPart.split(";");
						for (final String part : filesParts) {
							if (part.contains("|")) {
								final String fileUid = part.substring(0, part.indexOf("|"));
								final String fileName = part.substring(part.indexOf("|") + 1);
								final File file = new File();
								file.setUid(fileUid);
								file.setName(fileName);
								explanationFilesToExport.addFile(questionUid, file);
							}
						}
					} else {
						final int answerSetId = answerSet.getId();

						if (explanations.containsKey(answerSetId) &&
								explanations.get(answerSetId).containsKey(questionUid)) {
							explanation = explanations.get(answerSetId).get(questionUid);
						}

						explanationFilesOfSurvey.getFiles(answerSetId, questionUid)
								.forEach(file -> explanationFilesToExport.addFile(questionUid, file));
					}
					
					if (!explanation.isEmpty() || explanationFilesToExport.hasFiles())
					{
						writer.writeStartElement(EXPLANATION);
						writer.writeAttribute("qid", questionUid);
						if (!explanation.isEmpty()) {
							writer.writeStartElement(EXPLANATION_TEXT);
							writer.writeCharacters(ConversionTools.removeHTMLNoEscape(explanation));
							writer.writeEndElement(); // EXPLANATION_TEXT
						}
						for (final File file : explanationFilesToExport.getFiles(questionUid)) {
							writer.writeStartElement(EXPLANATION_FILE);
							writer.writeCharacters(ConversionTools.removeHTMLNoEscape(file.getNameForExport()));
							writer.writeEndElement(); // EXPLANATION_FILE
						}
						
						int likes = Integer.MAX_VALUE;
						if (answerSet == null) {
							String v = row.get(answerrowcounter++);
							if (v != "") {
								likes = Integer.valueOf(v);
							}
						} else {
							//likes = answerExplanationService.getLikesForExplanation(answerSet.getId(), questionUid);
							String key = answerSet.getId() + "-" + questionUid;
							if (likesForExplanations.containsKey(key)) {
								likes = likesForExplanations.get(key);
							}
						}
						
						writer.writeStartElement(EXPLANATION_LIKE);
						writer.writeCharacters(Integer.toString(likes));
						writer.writeEndElement(); // EXPLANATION_LIKES
						writer.writeEndElement(); // EXPLANATION
					}
				}
				
				if (question.isDelphiElement() && filter != null && filter.discussionExported(question.getId().toString())) {
					String discussion = "";				
					if (answerSet == null) {
						discussion = row.get(answerrowcounter++);
					} else {
						try {
							if (discussions.containsKey(answerSet.getId()) && discussions.get(answerSet.getId()).containsKey(question.getUniqueId()))
							{
								discussion = discussions.get(answerSet.getId()).get(question.getUniqueId());
							}
						} catch (NoSuchElementException ex) {
							//ignore
						}					
					}
					
					if (!discussion.isEmpty())
					{
						writer.writeStartElement(DISCUSSION);
						writer.writeAttribute("qid", question.getUniqueId());					
						writer.writeCharacters(ConversionTools.removeHTMLNoEscape(discussion));					
						writer.writeEndElement(); // Discussion
					}
				}
			}
		}
		
		if (survey.getIsQuiz()) {
			QuizResult quizResult = QuizHelper.getQuizResult(answerSet, survey);
			writer.writeStartElement("Scores");
			
			if (survey.getScoresByQuestion()) {
				for (Element element : survey.getQuestionsAndSections()) {
					if (element instanceof Section) {
						String score = quizResult.getSectionScore(element.getUniqueId());
						if (!score.equals("0/0")) {
							writer.writeStartElement("Section");
							writer.writeAttribute("id", element.getUniqueId());	
							writer.writeCharacters(score);							
							writer.writeEndElement(); // Section
						}
					} else if (element instanceof ChoiceQuestion || element instanceof FreeTextQuestion || element instanceof NumberQuestion || element instanceof DateQuestion) {
						Question question = (Question) element;
						if (question.getScoring() > 0) {
							writer.writeStartElement("Question");
							writer.writeAttribute("id", element.getUniqueId());	
							writer.writeCharacters(quizResult.getQuestionScore(element.getUniqueId()) + "/" + quizResult.getQuestionMaximumScore(element.getUniqueId()));							
							writer.writeEndElement(); // Question
						}
					}		 			
				}
			}
						
			writer.writeEndElement(); // Score
		}

		writer.writeEndElement(); // AnswerSet
	}

	@Override
	void exportStatistics() {
		throw new NotImplementedException();
	}

	@Override
	void exportStatisticsQuiz() {
		throw new NotImplementedException();
	}

	@Override
	void exportAddressBook() {
		throw new NotImplementedException();
	}

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

	@Override
	void exportActivities() {
		throw new NotImplementedException();
	}

	@Override
	void exportTokens() {
		throw new NotImplementedException();
	}

	@Override
	void exportECFGlobalResults() {
		throw new NotImplementedException();
	}

	@Override
	void exportECFProfileResults() {
		throw new NotImplementedException();
	}

	@Override
	void exportECFOrganizationalResults() {
		throw new NotImplementedException();
	}
	
	@Override
	void exportPDFReport() throws Exception {
		throw new NotImplementedException();
	}

}
