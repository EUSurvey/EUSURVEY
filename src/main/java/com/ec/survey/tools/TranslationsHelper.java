package com.ec.survey.tools;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.KeyValue;
import com.ec.survey.model.Language;
import com.ec.survey.model.Translation;
import com.ec.survey.model.Translations;
import com.ec.survey.model.survey.*;
import com.ec.survey.service.BasicService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.SurveyService;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.springframework.context.MessageSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class TranslationsHelper {

	private static final Logger logger = Logger.getLogger(TranslationsHelper.class);

	private static final int maxEmptyOdsLines = 20;

	/**
	 * Loads the Survey's current elements' translation.
	 * That is, loads the Translation in English if the pivot language is English.
	 */
	public static Translations getTranslations(Survey survey, boolean complete) {
		Translations translations = new Translations();

		translations.setLanguage(survey.getLanguage());
		translations.setSurveyId(survey.getId());
		translations.setSurveyUid(survey.getUniqueId());
		translations.setTitle(survey.getTitle());
		translations.setActive(true);

		translations.getTranslations().add(new Translation(Survey.TITLE, survey.getTitle(),
				survey.getLanguage().getCode(), survey.getId(), translations));

		if (complete || notNullOrEmpty(survey.getIntroduction())) {
			translations.getTranslations()
					.add(new Translation(Survey.INTRODUCTION,
							survey.getIntroduction() != null ? survey.getIntroduction() : "",
							survey.getLanguage().getCode(), survey.getId(), translations));
		}
		
		if (complete || notNullOrEmpty(survey.getLogoText())) {
			translations.getTranslations()
					.add(new Translation(Survey.LOGOTEXT,
							survey.getLogoText() != null ? survey.getLogoText() : "",
							survey.getLanguage().getCode(), survey.getId(), translations));
		}

		if (complete || notNullOrEmpty(survey.getEscapePage())) {
			translations.getTranslations()
					.add(new Translation(Survey.ESCAPEPAGE,
							survey.getEscapePage() != null ? survey.getEscapePage() : "",
							survey.getLanguage().getCode(), survey.getId(), translations));
		}

		if (complete || notNullOrEmpty(survey.getEscapeLink())) {
			translations.getTranslations()
					.add(new Translation(Survey.ESCAPELINK,
							survey.getEscapeLink() != null ? survey.getEscapeLink() : "",
							survey.getLanguage().getCode(), survey.getId(), translations));
		}

		if (complete || notNullOrEmpty(survey.getConfirmationPage())) {
			translations.getTranslations()
					.add(new Translation(Survey.CONFIRMATIONPAGE,
							survey.getConfirmationPage() != null ? survey.getConfirmationPage() : "",
							survey.getLanguage().getCode(), survey.getId(), translations));
		}

		if (complete || notNullOrEmpty(survey.getConfirmationLink())) {
			translations.getTranslations()
					.add(new Translation(Survey.CONFIRMATIONLINK,
							survey.getConfirmationLink() != null ? survey.getConfirmationLink() : "",
							survey.getLanguage().getCode(), survey.getId(), translations));
		}

		if (survey.getIsQuiz()) {
			if (complete || notNullOrEmpty(survey.getQuizWelcomeMessage())) {
				translations.getTranslations()
						.add(new Translation(Survey.QUIZWELCOMEMESSAGE,
								survey.getQuizWelcomeMessage() != null ? survey.getQuizWelcomeMessage() : "",
								survey.getLanguage().getCode(), survey.getId(), translations));
			}

			if (complete || notNullOrEmpty(survey.getQuizResultsMessage())) {
				translations.getTranslations()
						.add(new Translation(Survey.QUIZRESULTSMESSAGE,
								survey.getQuizResultsMessage() != null ? survey.getQuizResultsMessage() : "",
								survey.getLanguage().getCode(), survey.getId(), translations));
			}
		}

		for (String key : survey.getUsefulLinks().keySet()) {
			String[] data = key.split("#");
			if (data.length > 1) {
				translations.getTranslations().add(new Translation(data[0] + "#usefullink", data[1],
						survey.getLanguage().getCode(), survey.getId(), translations));
			} else {
				translations.getTranslations().add(new Translation("0#usefullink", data[0],
						survey.getLanguage().getCode(), survey.getId(), translations));
			}
		}

		for (String filename : survey.getBackgroundDocuments().keySet()) {
			String url = survey.getBackgroundDocuments().get(filename);
			String uid = BasicService.getFileUIDFromUrl(url);
			translations.getTranslations().add(new Translation(uid + "#backgrounddocument", filename,
					survey.getLanguage().getCode(), survey.getId(), translations));
		}

		for (Element element : survey.getElements()) {
			if (element instanceof Ruler) {
				continue;
			}

			if (element.getUniqueId() != null) {
				translations.getTranslations()
						.add(new Translation(element.getUniqueId(),
								element.getTitle() != null ? element.getTitle() : "", survey.getLanguage().getCode(),
								survey.getId(), translations, element.getLocked()));

				if (element instanceof Question) {
					Question question = (Question) element;
					if (complete || (question.getHelp() != null && question.getHelp().trim().length() > 0)) {
						translations.getTranslations()
								.add(new Translation(question.getUniqueId() + "help",
										question.getHelp() != null ? question.getHelp() : "",
										survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
					}

					if (question.getScoringItems() != null) {
						for (ScoringItem scoringItem : question.getScoringItems()) {
							if (notNullOrEmpty(scoringItem.getFeedback())) {
								translations.getTranslations()
										.add(new Translation(scoringItem.getUniqueId() + Question.FEEDBACK,
												scoringItem.getFeedback() != null ? scoringItem.getFeedback() : "",
												survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
							}
						}
					}
				}

				if (element instanceof Section) {
					Section section = (Section) element;
					translations.getTranslations()
							.add(new Translation(section.getUniqueId() + Section.TABTITLE,
									section.getTabTitle() != null ? section.getTabTitle() : "",
									survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
				}

				if (element instanceof NumberQuestion) {
					NumberQuestion number = (NumberQuestion) element;
					translations.getTranslations()
							.add(new Translation(number.getUniqueId() + NumberQuestion.UNIT,
									number.getUnit() != null ? number.getUnit() : "", survey.getLanguage().getCode(),
									survey.getId(), translations, element.getLocked()));

					if (number.isSlider()) {
						translations.getTranslations().add(new Translation(
								number.getUniqueId() + NumberQuestion.MINLABEL,
								number.getMinLabel() != null ? number.getMinLabel() : "",
								survey.getLanguage().getCode(),
								survey.getId(),
								translations, element.getLocked()));

						translations.getTranslations().add(new Translation(
								number.getUniqueId() + NumberQuestion.MAXLABEL,
								number.getMaxLabel() != null ? number.getMaxLabel() : "",
								survey.getLanguage().getCode(),
								survey.getId(),
								translations, element.getLocked()));
					}
				}

				if (element instanceof ChoiceQuestion) {
					ChoiceQuestion choice = (ChoiceQuestion) element;
					for (PossibleAnswer answer : choice.getPossibleAnswers()) {
						translations.getTranslations()
								.add(new Translation(answer.getUniqueId(),
										answer.getTitle() != null ? answer.getTitle() : "",
										survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));

						if (answer.getScoring() != null && notNullOrEmpty(answer.getScoring().getFeedback())) {
							translations.getTranslations().add(new Translation(answer.getUniqueId() + Question.FEEDBACK,
									answer.getScoring().getFeedback() != null ? answer.getScoring().getFeedback() : "",
									survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
						}
					}
				}

				if (element instanceof Confirmation) {
					Confirmation confirmation = (Confirmation) element;
					translations.getTranslations().add(new Translation(confirmation.getUniqueId() + Confirmation.TEXT,
							confirmation.getConfirmationtext() != null ? confirmation.getConfirmationtext() : "",
							survey.getLanguage().getCode(), survey.getId(), translations));
					translations.getTranslations().add(new Translation(confirmation.getUniqueId() + Confirmation.LABEL,
							confirmation.getConfirmationlabel() != null ? confirmation.getConfirmationlabel() : "",
							survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
				}

				if (element instanceof Matrix) {
					Matrix matrix = (Matrix) element;

					if (matrix.getFirstCellText() != null && matrix.getFirstCellText().length() > 0) {
						translations.getTranslations()
								.add(new Translation(matrix.getUniqueId() + MatrixOrTable.FIRSTCELL,
										matrix.getFirstCellText() != null ? matrix.getFirstCellText() : "",
										survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
					}

					for (Element child : matrix.getChildElements()) {
						if (child instanceof Text && child.getUniqueId() != null) {
							translations.getTranslations()
									.add(new Translation(child.getUniqueId(),
											child.getTitle() != null ? child.getTitle() : "",
											survey.getLanguage().getCode(), survey.getId(), translations , element.getLocked()));
						}
					}
				}

				if (element instanceof Table) {
					Table table = (Table) element;

					if (table.getFirstCellText() != null && table.getFirstCellText().length() > 0) {
						translations.getTranslations()
								.add(new Translation(table.getUniqueId() + MatrixOrTable.FIRSTCELL,
										table.getFirstCellText() != null ? table.getFirstCellText() : "",
										survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
					}

					for (Element child : table.getChildElements()) {
						if (child instanceof Text) {
							translations.getTranslations()
									.add(new Translation(child.getUniqueId(),
											child.getTitle() != null ? child.getTitle() : "",
											survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
						}
					}
				}
				
				if (element instanceof ComplexTable) {
					ComplexTable table = (ComplexTable) element;

					for (ComplexTableItem child : table.getOrderedChildElements()) {
						if (child.getCellType() != ComplexTableItem.CellType.Empty) {
							translations.getTranslations()
									.add(new Translation(child.getUniqueId(),
											child.getTitle() != null ? child.getTitle() : "",
											survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
							if (child.getCellType() != ComplexTableItem.CellType.StaticText) {
								if (complete || (child.getHelp() != null && child.getHelp().trim().length() > 0)) {
									translations.getTranslations()
											.add(new Translation(child.getUniqueId() + "help",
													child.getHelp() != null ? child.getHelp() : "",
													survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
								}

								if (complete || (child.getResultText() != null && child.getResultText().trim().length() > 0)) {
									translations.getTranslations()
											.add(new Translation(child.getUniqueId() + "RESULTTEXT",
													child.getResultText() != null ? child.getResultText() : "",
													survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
								}
							}

							if (child.isChoice()) {
								for (PossibleAnswer answer : child.getPossibleAnswers()) {
									translations.getTranslations()
											.add(new Translation(answer.getUniqueId(),
													answer.getTitle() != null ? answer.getTitle() : "",
													survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
								}
							}

							if (child.getCellType() == ComplexTableItem.CellType.Number){
								if (complete || (child.getUnit() != null && child.getUnit().trim().length() > 0)) {
									translations.getTranslations()
											.add(new Translation(child.getUniqueId() + "UNIT",
													child.getUnit() != null ? child.getUnit() : "",
													survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
								}
							}
						}
					}
				}

				if (element instanceof RatingQuestion) {
					RatingQuestion rating = (RatingQuestion) element;
					for (Element child : rating.getChildElements()) {
						if (child instanceof Text) {
							translations.getTranslations()
									.add(new Translation(child.getUniqueId(),
											child.getTitle() != null ? child.getTitle() : "",
											survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
						}
					}
				}
				
				if (element instanceof RankingQuestion) {
					RankingQuestion ranking = (RankingQuestion) element;
					for (Element child : ranking.getChildElements()) {
						translations.getTranslations()
							.add(new Translation(child.getUniqueId(),
								child.getTitle() != null ? child.getTitle() : "",
								survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
					}
				}
				
				if (element instanceof GalleryQuestion) {
					//inx
					GalleryQuestion gallery = (GalleryQuestion) element;
					for (com.ec.survey.model.survey.base.File child : gallery.getFiles()) {
						translations.getTranslations()
								.add(new Translation(child.getUid() + GalleryQuestion.TEXT,
										child.getComment() != null ? child.getComment() : "",
										survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
						translations.getTranslations()
							.add(new Translation(child.getUid(),
								child.getDescription() != null ? child.getDescription() : "",
								survey.getLanguage().getCode(), survey.getId(), translations));
						translations.getTranslations()
								.add(new Translation(child.getUid() + GalleryQuestion.TITLE,
										child.getName() != null ? child.getName() : "",
										survey.getLanguage().getCode(), survey.getId(), translations, element.getLocked()));
					}
				}
			} else {
				logger.warn("element without unique id found: " + element.getId());
			}

		}

		return translations;
	}

	public static List<KeyValue> getShortDescriptions(Survey survey) {
		List<KeyValue> result = new ArrayList<>();

		result.add(new KeyValue(Survey.TITLE, "L"));
		result.add(new KeyValue(Survey.CONFIRMATIONPAGE, "L"));
		result.add(new KeyValue(Survey.ESCAPEPAGE, "L"));
		result.add(new KeyValue(Survey.CONFIRMATIONLINK, "L"));
		result.add(new KeyValue(Survey.ESCAPELINK, "L"));
		result.add(new KeyValue(Survey.HELP, "H"));

		result.add(new KeyValue(Survey.QUIZWELCOMEMESSAGE, "L"));
		result.add(new KeyValue(Survey.QUIZRESULTSMESSAGE, "L"));
		
		result.add(new KeyValue(Survey.LOGOTEXT, "L"));		

		for (String key : survey.getUsefulLinks().keySet()) {
			String[] data = key.split("#");
			result.add(new KeyValue(data[0] + "#usefullink", "UL"));
		}

		for (String filename : survey.getBackgroundDocuments().keySet()) {
			String url = survey.getBackgroundDocuments().get(filename);
			String uid = BasicService.getFileUIDFromUrl(url);
			result.add(new KeyValue(uid + "#backgrounddocument", "BD"));
		}

		for (Element element : survey.getElements()) {
			if (element instanceof Ruler) {
				continue;
			}

			if (element instanceof Section) {
				result.add(new KeyValue(element.getUniqueId(), "L"));
				result.add(new KeyValue(element.getUniqueId() + Section.TABTITLE, "T"));
			}

			if (element instanceof Question) {
				result.add(new KeyValue(element.getUniqueId(), "L"));
				Question question = (Question) element;
				if (question.getHelp() != null && question.getHelp().trim().length() > 0) {
					result.add(new KeyValue(element.getUniqueId() + "help", "H"));
				}

				if (question.getScoring() > 0 && question.getScoringItems() != null) {
					for (ScoringItem scoringItem : question.getScoringItems()) {
						result.add(new KeyValue(scoringItem.getUniqueId() + Question.FEEDBACK, "F"));
					}
				}
			}

			if (element instanceof NumberQuestion) {
				result.add(new KeyValue(element.getUniqueId() + NumberQuestion.UNIT, "U"));

				if (((NumberQuestion) element).isSlider()) {
					result.add(new KeyValue(element.getUniqueId() + NumberQuestion.MINLABEL, "MIN"));
					result.add(new KeyValue(element.getUniqueId() + NumberQuestion.MAXLABEL, "MAX"));
				}
			}

			if (element instanceof ChoiceQuestion) {
				ChoiceQuestion choice = (ChoiceQuestion) element;
				for (PossibleAnswer answer : choice.getPossibleAnswers()) {
					result.add(new KeyValue(answer.getUniqueId(), "L"));

					if (answer.getScoring() != null && notNullOrEmpty(answer.getScoring().getFeedback())) {
						result.add(new KeyValue(answer.getUniqueId() + Question.FEEDBACK, "F"));
					}
				}
			}

			if (element instanceof Text) {
				result.add(new KeyValue(element.getUniqueId(), "L"));
			}

			if (element instanceof Confirmation) {
				result.add(new KeyValue(element.getUniqueId() + Confirmation.TEXT, "CT"));
				result.add(new KeyValue(element.getUniqueId() + Confirmation.LABEL, "CL"));
			}

			if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;

				result.add(new KeyValue(element.getUniqueId() + MatrixOrTable.FIRSTCELL, "FC"));

				for (Element child : matrix.getChildElements()) {
					if (child instanceof Text) {
						result.add(new KeyValue(child.getUniqueId(), "L"));
					}
				}
			}

			if (element instanceof Table) {
				Table table = (Table) element;

				result.add(new KeyValue(element.getUniqueId() + MatrixOrTable.FIRSTCELL, "FC"));

				for (Element child : table.getChildElements()) {
					if (child instanceof Text) {
						result.add(new KeyValue(child.getUniqueId(), "L"));
					}
				}
			}

			if (element instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion) element;
				for (Element child : rating.getChildElements()) {
					if (child instanceof Text) {
						result.add(new KeyValue(child.getUniqueId(), "L"));
					}
				}
			}
			
			if (element instanceof RankingQuestion) {
				RankingQuestion ranking = (RankingQuestion) element;
				for (Element child : ranking.getChildElements()) {
					result.add(new KeyValue(child.getUniqueId(), "L"));
				}
			}
			
			if (element instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) element;
				for (com.ec.survey.model.survey.base.File child : gallery.getFiles()) {
					result.add(new KeyValue(child.getUid() + GalleryQuestion.TEXT, "GT"));
					result.add(new KeyValue(child.getUid(), "GDT"));
					result.add(new KeyValue(child.getUid() + GalleryQuestion.TITLE, "GL"));

				}
			}

			if (element instanceof ComplexTable) {
				ComplexTable table = (ComplexTable) element;

				for (ComplexTableItem child : table.getChildElements()) {
					if (child.getCellType() != ComplexTableItem.CellType.Empty) {
						result.add(new KeyValue(child.getUniqueId(), "L"));

						if (notNullOrEmpty(child.getResultText())){
							result.add(new KeyValue(child.getUniqueId() + "RESULTTEXT", "RT"));
						}

						if (notNullOrEmpty(child.getHelp())) {
							result.add(new KeyValue(child.getUniqueId() + "help", "H"));
						}

						if (child.getCellType() == ComplexTableItem.CellType.Number){
							result.add(new KeyValue(child.getUniqueId() + "UNIT", "U"));
						}

						if (child.isChoice()) {
							for (PossibleAnswer answer : child.getPossibleAnswers()) {
								result.add(new KeyValue(answer.getUniqueId(), "L"));

								if (answer.getScoring() != null && notNullOrEmpty(answer.getScoring().getFeedback())) {
									result.add(new KeyValue(answer.getUniqueId() + Question.FEEDBACK, "F"));
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	public static List<KeyValue> getLongDescriptions(Survey survey, MessageSource resources, Locale locale) {
		List<KeyValue> result = new ArrayList<>();

		result.add(new KeyValue(Survey.TITLE, resources.getMessage("label.Title", null, "Title", locale)));

		result.add(new KeyValue(Survey.LOGOTEXT, resources.getMessage("label.LogoAlternativeText", null, "Alternative Text", locale)));
		
		result.add(new KeyValue(Survey.CONFIRMATIONPAGE,
				resources.getMessage("label.ConfirmationPage", null, "Confirmation Page", locale)));
		result.add(new KeyValue(Survey.ESCAPEPAGE,
				resources.getMessage("label.UnavailabilityPage", null, "Unavailability Page", locale)));
		result.add(new KeyValue(Survey.CONFIRMATIONLINK,
				resources.getMessage("label.ConfirmationLink", null, "Confirmation Link", locale)));
		result.add(
				new KeyValue(Survey.ESCAPELINK, resources.getMessage("label.EscapeLink", null, "Escape Link", locale)));
		result.add(new KeyValue(Survey.HELP, resources.getMessage("label.HelpMessage", null, "Help Message", locale)));

		result.add(new KeyValue(Survey.QUIZWELCOMEMESSAGE,
				resources.getMessage("label.QuizStartPage", null, "Quiz Start Page", locale)));
		result.add(new KeyValue(Survey.QUIZRESULTSMESSAGE,
				resources.getMessage("label.QuizResultPage", null, "Quiz Result Page", locale)));

		for (String key : survey.getUsefulLinks().keySet()) {
			String[] data = key.split("#");
			result.add(new KeyValue(data[0] + "#usefullink",
					resources.getMessage("label.UsefulLink", null, "Useful Link", locale)));
		}

		for (String filename : survey.getBackgroundDocuments().keySet()) {
			String url = survey.getBackgroundDocuments().get(filename);
			String uid = BasicService.getFileUIDFromUrl(url);
			result.add(new KeyValue(uid + "#backgrounddocument",
					resources.getMessage("label.BackgroundDocument", null, "Background Document", locale)));
		}

		for (Element element : survey.getElements()) {
			if (element instanceof Ruler) {
				continue;
			}

			result.add(new KeyValue(element.getUniqueId() + Constants.SHORTNAME,
					resources.getMessage("label.Identifier", null, "Identifier", locale)));

			if (element instanceof Section) {
				result.add(new KeyValue(element.getUniqueId(),
						resources.getMessage("label.SectionText", null, "Section Text", locale)));
				result.add(new KeyValue(element.getUniqueId() + Section.TABTITLE,
						resources.getMessage("label.TabTitle", null, "Tab Title", locale)));
			}

			if (element instanceof Question && !(element instanceof Text)) {
				result.add(new KeyValue(element.getUniqueId(),
						resources.getMessage("label.QuestionText", null, "Question Text", locale)));
				result.add(new KeyValue(element.getUniqueId() + "help",
						resources.getMessage("label.HelpMessage", null, "Help Message", locale)));

				Question question = (Question) element;

				if (question.getScoring() > 0 && question.getScoringItems() != null) {
					for (ScoringItem scoringItem : question.getScoringItems()) {
						result.add(new KeyValue(scoringItem.getUniqueId() + Question.FEEDBACK,
								resources.getMessage("label.Feedback", null, "Feedback", locale)));
					}
				}
			}

			if (element instanceof NumberQuestion) {
				result.add(new KeyValue(
						element.getUniqueId() + NumberQuestion.UNIT,
						resources.getMessage("label.Unit", null, "Unit", locale)
				));

				if (((NumberQuestion) element).isSlider()) {
					result.add(new KeyValue(
							element.getUniqueId() + NumberQuestion.MINLABEL,
							resources.getMessage("label.MinLabel", null, "Min Label", locale)
					));

					result.add(new KeyValue(
							element.getUniqueId()+ NumberQuestion.MAXLABEL,
							resources.getMessage("label.MaxLabel", null, "Max Label", locale)
					));
				}
			}

			if (element instanceof ChoiceQuestion) {
				ChoiceQuestion choice = (ChoiceQuestion) element;
				for (PossibleAnswer answer : choice.getPossibleAnswers()) {
					result.add(new KeyValue(answer.getUniqueId(),
							resources.getMessage("label.Answer", null, "Answer", locale)));

					if (answer.getScoring() != null && notNullOrEmpty(answer.getScoring().getFeedback())) {
						result.add(new KeyValue(answer.getUniqueId() + Question.FEEDBACK,
								resources.getMessage("label.Feedback", null, "Feedback", locale)));
					}
				}
			}

			if (element instanceof Confirmation) {
				result.add(new KeyValue(element.getUniqueId() + Confirmation.TEXT,
						resources.getMessage("label.ConfirmationText", null, "Confirmation Text", locale)));
				result.add(new KeyValue(element.getUniqueId() + Confirmation.LABEL,
						resources.getMessage("label.ConfirmationLabel", null, "Confirmation Label", locale)));
			}

			if (element instanceof Text) {
				result.add(
						new KeyValue(element.getUniqueId(), resources.getMessage("label.Text", null, "Text", locale)));
			}

			if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;

				result.add(new KeyValue(element.getUniqueId() + MatrixOrTable.FIRSTCELL,
						resources.getMessage("label.MatrixText", null, "Matrix Text", locale)));

				for (Element child : matrix.getChildElements()) {
					if (child instanceof Text) {
						result.add(new KeyValue(child.getUniqueId(),
								resources.getMessage("label.MatrixText", null, "Matrix Text", locale)));
					}
				}
			}

			if (element instanceof Table) {
				Table table = (Table) element;

				result.add(new KeyValue(element.getUniqueId() + MatrixOrTable.FIRSTCELL,
						resources.getMessage("label.TableText", null, "Table Text", locale)));

				for (Element child : table.getChildElements()) {
					if (child instanceof Text) {
						result.add(new KeyValue(child.getUniqueId(),
								resources.getMessage("label.TableText", null, "Table Text", locale)));
					}
				}
			}

			if (element instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion) element;
				for (Element child : rating.getChildElements()) {
					if (child instanceof Text) {
						result.add(new KeyValue(child.getUniqueId(),
								resources.getMessage("label.Text", null, "Text", locale)));
					}
				}
			}
			
			if (element instanceof RankingQuestion) {
				RankingQuestion ranking = (RankingQuestion) element;
				for (Element child : ranking.getChildElements()) {
					result.add(new KeyValue(child.getUniqueId(),
							resources.getMessage("label.Text", null, "Text", locale)));
				}
			}

			if (element instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) element;
				for (com.ec.survey.model.survey.base.File child : gallery.getFiles()) {
					result.add(new KeyValue(child.getUid(),
							resources.getMessage("label.AlternativeText", null, "Alternative Text", locale)));
					result.add(new KeyValue(child.getUid() + GalleryQuestion.TEXT,
							resources.getMessage("label.Text", null, "Text", locale)));
					result.add(new KeyValue(child.getUid() + GalleryQuestion.TITLE,
							resources.getMessage("label.Title", null, "Title", locale)));
				}
			}

			if (element instanceof ComplexTable) {
				ComplexTable table = (ComplexTable) element;

				for (ComplexTableItem child : table.getChildElements()) {
					if (child.getCellType() != ComplexTableItem.CellType.Empty) {
						result.add(new KeyValue(child.getUniqueId(),
								resources.getMessage("label.Text", null, "Text", locale)));


						result.add(new KeyValue(child.getUniqueId() + "RESULTTEXT",
								resources.getMessage("label.ResultText", null, "Result Text", locale)));

						result.add(new KeyValue(child.getUniqueId() + "help",
								resources.getMessage("label.HelpMessage", null, "Help Message", locale)));


						if (child.getCellType() == ComplexTableItem.CellType.Number){
							result.add(new KeyValue(child.getUniqueId() + "UNIT",
									resources.getMessage("label.Unit", null, "Unit", locale)));
						}

						if (child.isChoice()) {
							for (PossibleAnswer answer : child.getPossibleAnswers()) {
								result.add(new KeyValue(answer.getUniqueId(),
										resources.getMessage("label.Answer", null, "Answer", locale)));

								if (answer.getScoring() != null && notNullOrEmpty(answer.getScoring().getFeedback())) {
									result.add(new KeyValue(answer.getUniqueId() + Question.FEEDBACK,
											resources.getMessage("label.Feedback", null, "Feedback", locale)));
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	private static String getLabel(Element element, String suffix, Map<String, String> translationByKey) {
		try {
			String label = "";
			if (translationByKey.containsKey(element.getUniqueId() + suffix)) {
				label = translationByKey.get(element.getUniqueId() + suffix);
			} else if (translationByKey.containsKey(element.getId().toString() + suffix)) {
				label = translationByKey.get(element.getId().toString() + suffix);
			}
			return label != null ? label : "";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "";
		}
	}
	
	private static String getLabel(com.ec.survey.model.survey.base.File file, String suffix, Map<String, String> translationByKey) {
		try {
			String label = "";
			if (translationByKey.containsKey(file.getUid() + suffix)) {
				label = translationByKey.get(file.getUid() + suffix);
			} else if (translationByKey.containsKey(file.getId().toString() + suffix)) {
				label = translationByKey.get(file.getId().toString() + suffix);
			}
			return label != null ? label : "";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "";
		}
	}

	private static org.w3c.dom.Element getElementNode(Element element, Document doc,
			HashMap<String, String> translationByKey) {
		org.w3c.dom.Element elementNode = doc.createElement("Element");

		Attr attr = doc.createAttribute("type");
		attr.setValue(element.getType());
		elementNode.setAttributeNode(attr);

		attr = doc.createAttribute("key");
		attr.setValue(element.getUniqueId());
		elementNode.setAttributeNode(attr);

		if (element instanceof Table) {
			attr = doc.createAttribute("cols");
			attr.setValue(((Table) element).getColumns() + "");
			elementNode.setAttributeNode(attr);
		}

		if (element instanceof Matrix) {
			attr = doc.createAttribute("cols");
			attr.setValue(((Matrix) element).getColumns() + "");
			elementNode.setAttributeNode(attr);
		}

		String label = getLabel(element, "", translationByKey);

		org.w3c.dom.Element labelNode = doc.createElement(Constants.LABEL);
		labelNode.appendChild(doc.createCDATASection(label));
		elementNode.appendChild(labelNode);

		if (element instanceof Question) {
			Question question = (Question) element;
			if (question.getHelp() != null && question.getHelp().length() > 0) {
				String help = getLabel(element, "help", translationByKey);

				org.w3c.dom.Element helpNode = doc.createElement("Help");
				helpNode.appendChild(doc.createCDATASection(help));
				elementNode.appendChild(helpNode);
			}

			if (question.getScoringItems() != null) {
				for (ScoringItem scoringItem : question.getScoringItems()) {
					label = translationByKey.get(scoringItem.getUniqueId() + Question.FEEDBACK);

					if (notNullOrEmpty(label)) {
						org.w3c.dom.Element feedbackNode = doc.createElement("Feedback");
						feedbackNode.setAttribute("key", scoringItem.getUniqueId());
						feedbackNode.appendChild(doc.createCDATASection(label));
						elementNode.appendChild(feedbackNode);
					}
				}
			}
		}

		if (element instanceof Section) {
			Section section = (Section) element;
			org.w3c.dom.Element tabtitleNode = doc.createElement("TabTitle");
			label = getLabel(section, Section.TABTITLE, translationByKey);

			tabtitleNode.appendChild(doc.createCDATASection(label));
			elementNode.appendChild(tabtitleNode);
		}

		if (element instanceof NumberQuestion) {
			NumberQuestion number = (NumberQuestion) element;
			org.w3c.dom.Element unitNode = doc.createElement("Unit");
			label = getLabel(number, NumberQuestion.UNIT, translationByKey);

			unitNode.appendChild(doc.createCDATASection(label));
			elementNode.appendChild(unitNode);

			if (number.isSlider()) {
				org.w3c.dom.Element minLabelNode = doc.createElement("MinLabel");
				label = getLabel(number, NumberQuestion.MINLABEL, translationByKey);
				minLabelNode.appendChild(doc.createCDATASection(label));
				elementNode.appendChild(minLabelNode);

				org.w3c.dom.Element maxLabelNode = doc.createElement("MaxLabel");
				label = getLabel(number, NumberQuestion.MAXLABEL, translationByKey);
				maxLabelNode.appendChild(doc.createCDATASection(label));
				elementNode.appendChild(maxLabelNode);
			}
		}

		if (element instanceof Confirmation) {
			Confirmation confirmation = (Confirmation) element;
			org.w3c.dom.Element textNode = doc.createElement("ConfirmationText");
			label = getLabel(confirmation, Confirmation.TEXT, translationByKey);

			textNode.appendChild(doc.createCDATASection(label));
			elementNode.appendChild(textNode);

			org.w3c.dom.Element clabelNode = doc.createElement("ConfirmationLabel");
			label = getLabel(confirmation, Confirmation.LABEL, translationByKey);

			clabelNode.appendChild(doc.createCDATASection(label));
			elementNode.appendChild(clabelNode);
		}

		if (element instanceof ChoiceQuestion) {
			ChoiceQuestion choice = (ChoiceQuestion) element;
			for (PossibleAnswer answer : choice.getPossibleAnswers()) {
				org.w3c.dom.Element answerNode = doc.createElement("Answer");

				attr = doc.createAttribute("key");
				attr.setValue(answer.getUniqueId());
				answerNode.setAttributeNode(attr);

				label = getLabel(answer, "", translationByKey);
				labelNode = doc.createElement(Constants.LABEL);
				labelNode.appendChild(doc.createCDATASection(label));
				answerNode.appendChild(labelNode);

				label = getLabel(answer, Question.FEEDBACK, translationByKey);
				org.w3c.dom.Element feedbackNode = doc.createElement("Feedback");
				feedbackNode.appendChild(doc.createCDATASection(label));
				answerNode.appendChild(feedbackNode);

				elementNode.appendChild(answerNode);
			}
		}

		if (element instanceof MatrixOrTable) {

			MatrixOrTable matrix = (MatrixOrTable) element;
			org.w3c.dom.Element childrenElement = doc.createElement("Children");

			label = getLabel(matrix, MatrixOrTable.FIRSTCELL, translationByKey);
			org.w3c.dom.Element firstCellNode = doc.createElement("FirstCell");
			firstCellNode.appendChild(doc.createCDATASection(label));
			elementNode.appendChild(firstCellNode);

			for (Element child : matrix.getChildElements()) {
				if (!(child instanceof EmptyElement)) {
					childrenElement.appendChild(getElementNode(child, doc, translationByKey));
				}
			}
			elementNode.appendChild(childrenElement);
		}

		if (element instanceof RatingQuestion) {

			RatingQuestion rating = (RatingQuestion) element;
			org.w3c.dom.Element childrenElement = doc.createElement("Children");

			for (Element child : rating.getChildElements()) {
				if (!(child instanceof EmptyElement)) {
					childrenElement.appendChild(getElementNode(child, doc, translationByKey));
				}
			}
			elementNode.appendChild(childrenElement);
		}
		
		if (element instanceof RankingQuestion) {

			RankingQuestion ranking = (RankingQuestion) element;
			org.w3c.dom.Element childrenElement = doc.createElement("Children");

			for (Element child : ranking.getChildElements()) {
				childrenElement.appendChild(getElementNode(child, doc, translationByKey));
			}
			elementNode.appendChild(childrenElement);
		}
		
		if (element instanceof GalleryQuestion) {
			GalleryQuestion gallery = (GalleryQuestion) element;
			org.w3c.dom.Element childrenElement = doc.createElement("Children");

			for (com.ec.survey.model.survey.base.File child : gallery.getFiles()) {
				org.w3c.dom.Element galleryImage = doc.createElement("GalleryImage");
				attr = doc.createAttribute("key");
				attr.setValue(child.getUid());
				galleryImage.setAttributeNode(attr);

				org.w3c.dom.Element textNode = doc.createElement("GalleryText");
				label = getLabel(child, GalleryQuestion.TEXT, translationByKey);
				textNode.appendChild(doc.createCDATASection(label));
				galleryImage.appendChild(textNode);

				org.w3c.dom.Element descriptiveTextNode = doc.createElement("GalleryDescriptiveText");
				label = getLabel(child, "", translationByKey);
				descriptiveTextNode.appendChild(doc.createCDATASection(label));
				galleryImage.appendChild(descriptiveTextNode);

				org.w3c.dom.Element titleNode = doc.createElement("GalleryTitle");
				label = getLabel(child, GalleryQuestion.TITLE, translationByKey);
				titleNode.appendChild(doc.createCDATASection(label));
				galleryImage.appendChild(titleNode);

				childrenElement.appendChild(galleryImage);
			}
			elementNode.appendChild(childrenElement);
		}

		if (element instanceof ComplexTable) {

			ComplexTable table = (ComplexTable) element;
			org.w3c.dom.Element childrenElement = doc.createElement("Children");

			for (ComplexTableItem child : table.getChildElements()) {
				childrenElement.appendChild(getElementNode(child, doc, translationByKey));
			}
			elementNode.appendChild(childrenElement);
		}

		if (element instanceof ComplexTableItem){
			ComplexTableItem item = (ComplexTableItem) element;

			attr = doc.createAttribute("ResultText");
			label = getLabel(item, "RESULTTEXT", translationByKey);
			if (label.length() > 0) {
				attr.setValue(label);
				elementNode.setAttributeNode(attr);
			}

			attr = doc.createAttribute("Unit");
			label = getLabel(item, "UNIT", translationByKey);
			if (label.length() > 0) {
				attr.setValue(label);
				elementNode.setAttributeNode(attr);
			}

			if (item.isChoice()) {

				for (PossibleAnswer answer : item.getPossibleAnswers()) {
					org.w3c.dom.Element answerNode = doc.createElement("Answer");

					attr = doc.createAttribute("key");
					attr.setValue(answer.getUniqueId());
					answerNode.setAttributeNode(attr);

					label = getLabel(answer, "", translationByKey);
					labelNode = doc.createElement(Constants.LABEL);
					labelNode.appendChild(doc.createCDATASection(label));
					answerNode.appendChild(labelNode);

					label = getLabel(answer, Question.FEEDBACK, translationByKey);
					org.w3c.dom.Element feedbackNode = doc.createElement("Feedback");
					feedbackNode.appendChild(doc.createCDATASection(label));
					answerNode.appendChild(feedbackNode);

					elementNode.appendChild(answerNode);
				}
			}
		}

		return elementNode;
	}

	public static java.io.File getXML(Survey survey, Translations translations, String xsllink,
			FileService fileService) {
		try {

			HashMap<String, String> translationByKey = new HashMap<>();
			for (Translation translation : translations.getTranslations()) {
				if (translation.getLabel() != null) {
					translationByKey.put(translation.getKey(), translation.getLabel());
				}
			}

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();

			doc.setXmlStandalone(true);
			ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet",
					"type=\"text/xsl\" href=\"" + xsllink + "\"");

			org.w3c.dom.Element rootElement = doc.createElement("SurveyTranslation");
			doc.appendChild(rootElement);

			doc.insertBefore(pi, rootElement);

			org.w3c.dom.Element surveyId = doc.createElement("Survey");
			surveyId.appendChild(doc.createTextNode(survey.getUniqueId()));
			rootElement.appendChild(surveyId);

			org.w3c.dom.Element surveyTitle = doc.createElement("Title");
			String title = "";
			if (translationByKey.containsKey(Survey.TITLE))
				title = translationByKey.get(Survey.TITLE);
			if (title == null)
				title = "";
			surveyTitle.appendChild(doc.createCDATASection(title));
			rootElement.appendChild(surveyTitle);
			
			org.w3c.dom.Element logoText = doc.createElement("LogoText");
			String logotext = "";
			if (translationByKey.containsKey(Survey.LOGOTEXT))
				logotext = translationByKey.get(Survey.LOGOTEXT);
			if (logotext == null)
				logotext = "";
			logoText.appendChild(doc.createCDATASection(logotext));
			rootElement.appendChild(logoText);

			org.w3c.dom.Element surveyQuizWelcome = doc.createElement("QuizWelcomePage");
			String quizwelcome = "";
			if (translationByKey.containsKey(Survey.QUIZWELCOMEMESSAGE)) {
				quizwelcome = translationByKey.get(Survey.QUIZWELCOMEMESSAGE);
				if (quizwelcome == null)
					quizwelcome = "";
				surveyQuizWelcome.appendChild(doc.createCDATASection(quizwelcome));
				rootElement.appendChild(surveyQuizWelcome);
			}

			org.w3c.dom.Element surveyQuizResult = doc.createElement("QuizResultPage");
			String quizresult = "";
			if (translationByKey.containsKey(Survey.QUIZRESULTSMESSAGE)) {
				quizresult = translationByKey.get(Survey.QUIZRESULTSMESSAGE);
				if (quizresult == null)
					quizresult = "";
				surveyQuizResult.appendChild(doc.createCDATASection(quizresult));
				rootElement.appendChild(surveyQuizResult);
			}

			org.w3c.dom.Element surveyEscape = doc.createElement("EscapePage");
			String escape = "";
			if (translationByKey.containsKey(Survey.ESCAPEPAGE))
				escape = translationByKey.get(Survey.ESCAPEPAGE);
			if (escape == null)
				escape = "";
			surveyEscape.appendChild(doc.createCDATASection(escape));
			rootElement.appendChild(surveyEscape);

			org.w3c.dom.Element surveyEscapeLink = doc.createElement("EscapeLink");
			escape = "";
			if (translationByKey.containsKey(Survey.ESCAPELINK))
				escape = translationByKey.get(Survey.ESCAPELINK);
			if (escape == null)
				escape = "";
			surveyEscapeLink.appendChild(doc.createCDATASection(escape));
			rootElement.appendChild(surveyEscapeLink);

			org.w3c.dom.Element surveyConfirmation = doc.createElement("ConfirmationPage");
			String confirm = "";
			if (translationByKey.containsKey(Survey.CONFIRMATIONPAGE))
				confirm = translationByKey.get(Survey.CONFIRMATIONPAGE);
			if (confirm == null)
				confirm = "";
			surveyConfirmation.appendChild(doc.createCDATASection(confirm));
			rootElement.appendChild(surveyConfirmation);

			org.w3c.dom.Element surveyConfirmationLink = doc.createElement("ConfirmationLink");
			confirm = "";
			if (translationByKey.containsKey(Survey.CONFIRMATIONLINK))
				confirm = translationByKey.get(Survey.CONFIRMATIONLINK);
			if (confirm == null)
				confirm = "";
			surveyConfirmationLink.appendChild(doc.createCDATASection(confirm));
			rootElement.appendChild(surveyConfirmationLink);

			for (String key : survey.getUsefulLinks().keySet()) {
				String[] data = key.split("#");
				String newkey = data[0] + "#usefullink";
				org.w3c.dom.Element usefulLink = doc.createElement("UsefulLink");
				String label = "";
				if (translationByKey.containsKey(newkey))
					label = translationByKey.get(newkey);
				if (label == null)
					label = "";
				usefulLink.setAttribute("key", newkey);
				usefulLink.appendChild(doc.createCDATASection(label));
				rootElement.appendChild(usefulLink);
			}

			for (String filename : survey.getBackgroundDocuments().keySet()) {
				String url = survey.getBackgroundDocuments().get(filename);
				String uid = BasicService.getFileUIDFromUrl(url);
				String newkey = uid + "#backgrounddocument";
				org.w3c.dom.Element backgroundDocument = doc.createElement("BackgroundDocument");
				String label = "";
				if (translationByKey.containsKey(newkey))
					label = translationByKey.get(newkey);
				if (label == null)
					label = "";
				backgroundDocument.setAttribute("key", newkey);
				backgroundDocument.appendChild(doc.createCDATASection(label));
				rootElement.appendChild(backgroundDocument);
			}

			org.w3c.dom.Element lang = doc.createElement("Lang");
			lang.appendChild(doc.createTextNode(translations.getLanguage().getCode()));
			rootElement.appendChild(lang);

			for (Element element : survey.getElements()) {
				if (element instanceof Ruler) {
					continue;
				}

				rootElement.appendChild(getElementNode(element, doc, translationByKey));
			}

			java.io.File temp = fileService.createTempFile("translation" + UUID.randomUUID().toString(), ".xml");

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);

			transformer.transform(source, result);

			writer.close();
			String xml = writer.toString().replace("<![CDATA[", "").replace("]]>", "");

			OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(temp), "UTF-8");

			bw.write(xml);
			bw.close();

			return temp;

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	public static java.io.File getXLS(Survey survey, Translations translations, FileService fileService) {

		try {

			HashMap<String, String> translationsByKey = new HashMap<>();
			for (Translation translation : translations.getTranslations()) {
				if (translation.getLabel() != null) {
					translationsByKey.put(translation.getKey(), translation.getLabel());
				}
			}

			List<KeyValue> infos = getShortDescriptions(survey);
			Map<String, String> descriptions = new HashMap<>();
			for (KeyValue keyValue : infos) {
				descriptions.put(keyValue.getKey(), keyValue.getValue());
			}

			Workbook wb = new HSSFWorkbook();
			String safeName = WorkbookUtil.createSafeSheetName("Translation");
			Sheet sheet = wb.createSheet(safeName);
			XlsTranslationCreator creator = new XlsTranslationCreator(sheet, translationsByKey, descriptions);

			createSheet(creator, survey, translations, descriptions, translationsByKey);

			java.io.File temp = fileService.createTempFile("translation" + UUID.randomUUID().toString(), ".xls");

			FileOutputStream out = new FileOutputStream(temp);

			wb.write(out);
			wb.close();
			out.close();

			return temp;

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;

	}

	public static java.io.File getODS(Survey survey, Translations translations,	FileService fileService) {
		try {

			HashMap<String, String> translationsByKey = new HashMap<>();
			for (Translation translation : translations.getTranslations()) {
				if (translation.getLabel() != null) {
					translationsByKey.put(translation.getKey(), translation.getLabel());
				}
			}

			List<KeyValue> infos = getShortDescriptions(survey);
			Map<String, String> descriptions = new HashMap<>();
			for (KeyValue keyValue : infos) {
				descriptions.put(keyValue.getKey(), keyValue.getValue());
			}

			SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
			org.odftoolkit.simple.table.Table sheet = spreadsheet.getSheetByIndex(0);
			sheet.setTableName("Translation");
			OdsTranslationCreator creator = new OdsTranslationCreator(sheet, translationsByKey, descriptions);

			createSheet(creator, survey, translations, descriptions, translationsByKey);

			java.io.File temp = fileService.createTempFile("translation" + UUID.randomUUID().toString(), ".ods");

			FileOutputStream out = new FileOutputStream(temp);

			spreadsheet.save(out);

			out.close();

			return temp;

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	private static void createSheet(SheetTranslationCreator creator, Survey survey, Translations translations, Map<String, String> descriptions, Map<String, String> translationsByKey){
		SheetRow row = creator.nextRow();
		addTextCell(row, 0, survey.getUniqueId());
		addTextCell(row, 1, translations.getLanguage().getCode());

		row = creator.nextRow();
		addTextCell(row, 0, Survey.TITLE);
		addTextCell(row, 1, descriptions.get(Survey.TITLE));
		addTextCell(row, 2, translationsByKey.get(Survey.TITLE) != null ? translationsByKey.get(Survey.TITLE) : "");

		row = creator.nextRow();
		addTextCell(row, 0, Survey.LOGOTEXT);
		addTextCell(row, 1, descriptions.get(Survey.LOGOTEXT));
		addTextCell(row, 2, translationsByKey.get(Survey.LOGOTEXT) != null ? translationsByKey.get(Survey.LOGOTEXT) : "");

		creator.nextAttributeRow(Survey.QUIZWELCOMEMESSAGE);

		creator.nextAttributeRow(Survey.QUIZRESULTSMESSAGE);

		creator.nextAttributeRow(Survey.ESCAPEPAGE);

		creator.nextAttributeRow(Survey.ESCAPELINK);

		creator.nextAttributeRow(Survey.CONFIRMATIONPAGE);

		creator.nextAttributeRow(Survey.CONFIRMATIONLINK);

		for (String key : survey.getUsefulLinks().keySet()) {
			row = creator.nextRow();
			String[] data = key.split("#");
			String newkey = data[0] + "#usefullink";
			addTextCell(row, 0, newkey);
			addTextCell(row, 1, descriptions.get(newkey));
			addTextCell(row, 2, translationsByKey.get(newkey) != null ? translationsByKey.get(newkey) : "");
		}

		for (String filename : survey.getBackgroundDocuments().keySet()) {
			String url = survey.getBackgroundDocuments().get(filename);
			String uid = BasicService.getFileUIDFromUrl(url);
			row = creator.nextRow();
			String newkey = uid + "#backgrounddocument";
			addTextCell(row, 0, newkey);
			addTextCell(row, 1, descriptions.get(newkey));
			addTextCell(row, 2, translationsByKey.get(newkey) != null ? translationsByKey.get(newkey) : "");
		}

		for (Element element : survey.getElements()) {
			if (element instanceof Ruler) {
				continue;
			}

			creator.nextLabelRow(element);

			if (element instanceof Section) {
				Section section = (Section) element;
				creator.nextLabelRow(section, Section.TABTITLE);
			}

			if (element instanceof NumberQuestion) {
				NumberQuestion number = (NumberQuestion) element;

				creator.nextLabelRow(number, NumberQuestion.UNIT);

				if (number.isSlider()) {
					creator.nextLabelRow(number, NumberQuestion.MINLABEL);

					creator.nextLabelRow(number, NumberQuestion.MAXLABEL);
				}
			}

			if (element instanceof Confirmation) {
				Confirmation confirmation = (Confirmation) element;

				creator.nextLabelRow(confirmation, Confirmation.TEXT);
				creator.nextLabelRow(confirmation, Confirmation.LABEL);
			}

			if (element instanceof ChoiceQuestion) {
				ChoiceQuestion choice = (ChoiceQuestion) element;
				for (PossibleAnswer answer : choice.getPossibleAnswers()) {
					creator.nextLabelRow(answer);

					creator.nextLabelRow(answer, Question.FEEDBACK);
				}
			}

			if (element instanceof Question) {
				Question question = (Question) element;
				if (question.getHelp() != null && question.getHelp().length() > 0) {
					creator.nextLabelRow(question, "help");
				}

				if (question.getScoringItems() != null) {
					for (ScoringItem scoringItem : question.getScoringItems()) {
						String label = translationsByKey.get(scoringItem.getUniqueId() + Question.FEEDBACK);
						if (notNullOrEmpty(label)) {
							row = creator.nextRow();
							addTextCell(row, 0, scoringItem.getUniqueId());
							addTextCell(row, 1, descriptions.get(scoringItem.getUniqueId() + Question.FEEDBACK));
							addTextCell(row, 2, label);
						}
					}
				}
			}

			if (element instanceof MatrixOrTable) {
				MatrixOrTable matrix = (MatrixOrTable) element;

				creator.nextLabelRow(matrix, MatrixOrTable.FIRSTCELL);

				for (Element child : matrix.getChildElements()) {
					if (!(child instanceof EmptyElement)) {
						creator.nextLabelRow(child);
					}
				}
			}

			if (element instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion) element;
				for (Element child : rating.getChildElements()) {
					if (!(child instanceof EmptyElement)) {
						creator.nextLabelRow(child);
					}
				}
			}

			if (element instanceof RankingQuestion) {
				RankingQuestion ranking = (RankingQuestion) element;
				for (Element child : ranking.getChildElements()) {
					creator.nextLabelRow(child);
				}
			}

			if (element instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) element;
				for (com.ec.survey.model.survey.base.File child : gallery.getFiles()) {
					creator.nextLabelRow(child, GalleryQuestion.TEXT);

					creator.nextLabelRow(child);

					creator.nextLabelRow(child, GalleryQuestion.TITLE);
				}
			}

			if (element instanceof ComplexTable){
				ComplexTable table = (ComplexTable) element;

				for (ComplexTableItem child : table.getChildElements()) {

					creator.nextLabelRow(child);

					creator.nextLabelRow(child, "RESULTTEXT");
					creator.nextLabelRow(child, "UNIT");
					creator.nextLabelRow(child, "help");

					if (child.isChoice()){
						for (PossibleAnswer answer : child.getPossibleAnswers()) {
							creator.nextLabelRow(answer);
							creator.nextLabelRow(answer, Question.FEEDBACK);
						}
					}
				}
			}
		}
	}

	private static void addTextCell(Row row, int col, String text) {
		Cell cell = row.createCell(col);
		cell.setCellValue(text);
		cell.setCellType(Cell.CELL_TYPE_STRING);
	}

	private static void addTextCell(org.odftoolkit.simple.table.Row row, int col, String text) {
		org.odftoolkit.simple.table.Cell cell = row.getCellByIndex(col);
		cell.setStringValue(text);
	}

	private static void addTextCell(SheetRow row, int col, String text) {
		if (row.isForXls){
			addTextCell((Row) row.rowObject, col, text);
		} else {
			addTextCell((org.odftoolkit.simple.table.Row) row.rowObject, col, text);
		}
	}

	private static HashMap<String, String> getTypeSuffixMapping() {
		HashMap<String, String> typeSuffixByShortType = new HashMap<>();

		typeSuffixByShortType.put("T", "TABTITLE");
		typeSuffixByShortType.put("U", "UNIT");
		typeSuffixByShortType.put("MIN", "MINLABEL");
		typeSuffixByShortType.put("MAX", "MAXLABEL");
		typeSuffixByShortType.put("H", "help");
		typeSuffixByShortType.put("CT", "CONFIRMATIONTEXT");
		typeSuffixByShortType.put("CL", "CONFIRMATIONLABEL");
		typeSuffixByShortType.put("F", "FEEDBACK");
		typeSuffixByShortType.put("FC", "FIRSTCELL");
		typeSuffixByShortType.put("GT", "GALLERYTEXT");
		typeSuffixByShortType.put("GDT", "");
		typeSuffixByShortType.put("GL", "TITLE");
		typeSuffixByShortType.put("RT", "RESULTTEXT");

		return typeSuffixByShortType;
	}

	public static Translations importXML(InputStream inputStream, SurveyService surveyService) {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			docFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			docFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			docFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			docFactory.setXIncludeAware(false);
			docFactory.setExpandEntityReferences(false);

			Translations result = new Translations();

			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(inputStream);

			org.w3c.dom.Element translation = doc.getDocumentElement();

			String surveyIdString = getText(translation.getElementsByTagName("Survey"), "Survey");

			int surveyId = 0;
			try {
				surveyId = Integer.parseInt(surveyIdString);
				result.setSurveyId(surveyId);

				Survey survey = surveyService.getSurvey(surveyId, false, true);
				result.setSurveyUid(survey.getUniqueId());
			} catch (NumberFormatException e) {
				result.setSurveyUid(surveyIdString);

				Survey survey = surveyService.getSurveyByUniqueId(surveyIdString, false, true);
				surveyId = survey.getId();
				result.setSurveyId(surveyId);
			}

			String lang = getText(translation.getElementsByTagName("Lang"), "Lang");

			Language objLang = surveyService.getLanguage(lang);
			if (objLang == null) {
				objLang = new Language(lang, lang, lang, false);
				surveyService.save(objLang);
			}

			result.setLanguage(objLang);

			String title = getText(translation.getElementsByTagName("Title"), "Title");
			result.setTitle(title);
			result.getTranslations().add(new Translation(Survey.TITLE, title, lang, surveyId, result));

			
			try {
				String logoText = getText(translation.getElementsByTagName("LogoText"), "LogoText");
				result.getTranslations()
						.add(new Translation(Survey.LOGOTEXT, logoText, lang, surveyId, result));
			} catch (Exception e) {
				//ignore
			}
			
			try {
				String quizwelcome = getText(translation.getElementsByTagName("QuizWelcomePage"), "QuizWelcomePage");
				result.getTranslations()
						.add(new Translation(Survey.QUIZWELCOMEMESSAGE, quizwelcome, lang, surveyId, result));
			} catch (Exception e) {
				//ignore
			}

			try {
				String quizresult = getText(translation.getElementsByTagName("QuizResultPage"), "QuizResultPage");
				result.getTranslations()
						.add(new Translation(Survey.QUIZRESULTSMESSAGE, quizresult, lang, surveyId, result));
			} catch (Exception e) {
				//ignore
			}

			String escape = getText(translation.getElementsByTagName("EscapePage"), "EscapePage");
			result.getTranslations().add(new Translation(Survey.ESCAPEPAGE, escape, lang, surveyId, result));

			try {
				escape = getText(translation.getElementsByTagName("EscapeLink"), "EscapeLink");
				result.getTranslations().add(new Translation(Survey.ESCAPELINK, escape, lang, surveyId, result));
			} catch (Exception e) {
				//ignore
			}

			String confirmation = getText(translation.getElementsByTagName("ConfirmationPage"), "ConfirmationPage");
			result.getTranslations()
					.add(new Translation(Survey.CONFIRMATIONPAGE, confirmation, lang, surveyId, result));

			try {
				escape = getText(translation.getElementsByTagName("ConfirmationLink"), "ConfirmationLink");
				result.getTranslations().add(new Translation(Survey.CONFIRMATIONLINK, escape, lang, surveyId, result));
			} catch (Exception e) {
				//ignore
			}

			for (int i = 0; i < translation.getElementsByTagName("Element").getLength(); i++) {
				org.w3c.dom.Element element = (org.w3c.dom.Element) translation.getElementsByTagName("Element").item(i);

				String key = Tools.repairXML(element.getAttribute("key"));
				String type = Tools.repairXML(element.getAttribute("type"));
				String label = getText(element.getElementsByTagName(Constants.LABEL), Constants.LABEL);

				result.getTranslations().add(new Translation(key, label, lang, surveyId, result));

				String help = getText(element.getElementsByTagName("Help"), "Help");
				if (help != null)
					result.getTranslations().add(new Translation(key + "help", help, lang, surveyId, result));

				if (type.contains("Section")) {
					String tabTitle = getText(element.getElementsByTagName("TabTitle"), "TabTitle");
					if (tabTitle != null)
						result.getTranslations()
								.add(new Translation(key + Section.TABTITLE, tabTitle, lang, surveyId, result));
				}

				if (type.contains("NumberQuestion")) {
					String unit = getText(element.getElementsByTagName("Unit"), "Unit");
					result.getTranslations()
							.add(new Translation(key + NumberQuestion.UNIT, unit, lang, surveyId, result));

					NodeList minLabelElements = element.getElementsByTagName("MinLabel");
					if (minLabelElements.getLength() > 0) {
						String minLabel = getText(minLabelElements, "MinLabel");
						result.getTranslations().add(new Translation(key + NumberQuestion.MINLABEL, minLabel, lang, surveyId, result));
					}

					NodeList maxLabelElements = element.getElementsByTagName("MaxLabel");
					if (maxLabelElements.getLength() > 0) {
						String maxLabel = getText(maxLabelElements, "MaxLabel");
						result.getTranslations().add(new Translation(key + NumberQuestion.MAXLABEL, maxLabel, lang, surveyId, result));
					}
				}

				if (type.contains("Confirmation")) {
					String text = getText(element.getElementsByTagName("ConfirmationText"), "ConfirmationText");
					result.getTranslations()
							.add(new Translation(key + Confirmation.TEXT, text, lang, surveyId, result));
					text = getText(element.getElementsByTagName("ConfirmationLabel"), "ConfirmationLabel");
					result.getTranslations()
							.add(new Translation(key + Confirmation.LABEL, text, lang, surveyId, result));
				}

				if (type.contains("ChoiceQuestion")) {
					for (int j = 0; j < element.getElementsByTagName("Answer").getLength(); j++) {
						org.w3c.dom.Element answer = (org.w3c.dom.Element) element.getElementsByTagName("Answer")
								.item(j);
						key = answer.getAttribute("key");
						label = getText(answer.getElementsByTagName(Constants.LABEL), Constants.LABEL);
						result.getTranslations().add(new Translation(key, label, lang, surveyId, result));

						try {
							label = getText(answer.getElementsByTagName("Feedback"), "Feedback");
							result.getTranslations()
									.add(new Translation(key + Question.FEEDBACK, label, lang, surveyId, result));
						} catch (Exception e) {
						}
					}
				}

				if (type.contains("ComplexTable")) {

					org.w3c.dom.Element children = (org.w3c.dom.Element) element.getElementsByTagName("Children")
							.item(0);

					for (int j = 0; children != null && j < children.getElementsByTagName("Element").getLength(); j++) {
						org.w3c.dom.Element child = (org.w3c.dom.Element) children.getElementsByTagName("Element")
								.item(j);
						key = Tools.repairXML(child.getAttribute("key"));
						type = Tools.repairXML(child.getAttribute("type"));
						label = getText(child.getElementsByTagName(Constants.LABEL), Constants.LABEL);

						result.getTranslations().add(new Translation(key, label, lang, surveyId, result));

						label = getText(child.getElementsByTagName("Help"), "Help");
						if (label != null)
							result.getTranslations().add(new Translation(key + "help", label, lang, surveyId, result));

						label = child.getAttribute("ResultText");
						if (label.length() > 0){
							result.getTranslations().add(new Translation(key + "RESULTTEXT", label, lang, surveyId, result));
						}

						label = child.getAttribute("Unit");
						if (label.length() > 0){
							result.getTranslations().add(new Translation(key + "UNIT", label, lang, surveyId, result));
						}

						NodeList answers = child.getElementsByTagName("Answer");

						for (int k = 0; k < answers.getLength(); k++) {
							org.w3c.dom.Element answer = (org.w3c.dom.Element) answers.item(k);
							key = answer.getAttribute("key");
							label = getText(answer.getElementsByTagName(Constants.LABEL), Constants.LABEL);
							result.getTranslations().add(new Translation(key, label, lang, surveyId, result));

							try {
								label = getText(answer.getElementsByTagName("Feedback"), "Feedback");
								result.getTranslations()
										.add(new Translation(key + Question.FEEDBACK, label, lang, surveyId, result));
							} catch (Exception ignored) { }
						}
					}
				} else if (type.contains("Matrix") || type.contains("Table")) {

					if (element.getElementsByTagName("FirstCell").getLength() > 0) {
						label = getText(element.getElementsByTagName("FirstCell"), "FirstCell");

						result.getTranslations()
								.add(new Translation(key + MatrixOrTable.FIRSTCELL, label, lang, surveyId, result));
					}

					org.w3c.dom.Element children = (org.w3c.dom.Element) element.getElementsByTagName("Children")
							.item(0);

					for (int j = 0; j < children.getElementsByTagName("Element").getLength(); j++) {
						org.w3c.dom.Element child = (org.w3c.dom.Element) children.getElementsByTagName("Element")
								.item(j);
						key = Tools.repairXML(child.getAttribute("key"));
						type = Tools.repairXML(child.getAttribute("type"));
						label = getText(child.getElementsByTagName(Constants.LABEL), Constants.LABEL);

						result.getTranslations().add(new Translation(key, label, lang, surveyId, result));
					}
				}

				if (type.contains("RatingQuestion")) {
					org.w3c.dom.Element children = (org.w3c.dom.Element) element.getElementsByTagName("Children")
							.item(0);

					for (int j = 0; j < children.getElementsByTagName("Element").getLength(); j++) {
						org.w3c.dom.Element child = (org.w3c.dom.Element) children.getElementsByTagName("Element")
								.item(j);
						key = Tools.repairXML(child.getAttribute("key"));
						label = getText(child.getElementsByTagName(Constants.LABEL), Constants.LABEL);

						result.getTranslations().add(new Translation(key, label, lang, surveyId, result));
					}
				}

				if (type.contains("RankingQuestion")) {
					org.w3c.dom.Element children = (org.w3c.dom.Element) element.getElementsByTagName("Children")
							.item(0);

					for (int j = 0; j < children.getElementsByTagName("Element").getLength(); j++) {
						org.w3c.dom.Element child = (org.w3c.dom.Element) children.getElementsByTagName("Element")
								.item(j);
						key = Tools.repairXML(child.getAttribute("key"));
						label = getText(child.getElementsByTagName(Constants.LABEL), Constants.LABEL);

						result.getTranslations().add(new Translation(key, label, lang, surveyId, result));
					}
				}
				
				if (type.contains("GalleryQuestion")) {
					org.w3c.dom.Element children = (org.w3c.dom.Element) element.getElementsByTagName("Children")
							.item(0);


					for (int j = 0; j < children.getElementsByTagName("GalleryImage").getLength(); j++) {
						org.w3c.dom.Element child = (org.w3c.dom.Element) children.getElementsByTagName("GalleryImage")
								.item(j);
						key = Tools.repairXML(child.getAttribute("key")) + GalleryQuestion.TEXT;
						label = getText(child.getElementsByTagName("GalleryText"), "GalleryText");
						result.getTranslations().add(new Translation(key, label, lang, surveyId, result));

						key = Tools.repairXML(child.getAttribute("key"));
						label = getText(child.getElementsByTagName("GalleryDescriptiveText"), "GalleryDescriptiveText");
						result.getTranslations().add(new Translation(key, label, lang, surveyId, result));

						key = Tools.repairXML(child.getAttribute("key")) + GalleryQuestion.TITLE;
						label = getText(child.getElementsByTagName("GalleryTitle"), "GalleryTitle");
						result.getTranslations().add(new Translation(key, label, lang, surveyId, result));
					}
				}
				
				org.w3c.dom.NodeList feedbackChildren = element.getElementsByTagName("Feedback");

				for (int j = 0; j < feedbackChildren.getLength(); j++) {
					org.w3c.dom.Element feedback = (org.w3c.dom.Element) feedbackChildren.item(j);
					key = Tools.repairXML(feedback.getAttribute("key"));
					label = Tools.repairXML(Tools.filterHTML(feedback.getTextContent()));

					result.getTranslations()
							.add(new Translation(key + Question.FEEDBACK, label, lang, surveyId, result));
				}
			}

			return removeIDs(result);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;

	}

	private static Translations removeIDs(Translations result) {
		for (Translation t : result.getTranslations()) {
			String label = t.getLabel().replaceAll("\\s+(?:id)\\s*=\\s*\"[^\"]*\"", "")
					.replaceAll("\\s+(?:id)\\s*=\\s*\'[^\"]*\'", "");
			t.setLabel(label);
		}
		return result;
	}

	public static Translations importXLS(InputStream inputStream, List<String> invalidKeys, SurveyService surveyService,
			ServletContext servletContext) {

		try {

			Translations result = new Translations();

			HSSFWorkbook wb = new HSSFWorkbook(inputStream);
			HSSFSheet sheet = wb.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();

			int surveyId = 0;
			try {
				surveyId = Integer.parseInt(getText(sheet, 0, 0, Survey.TITLE, invalidKeys, servletContext));
				result.setSurveyId(surveyId);

				Survey survey = surveyService.getSurvey(surveyId, false, true);
				result.setSurveyUid(survey.getUniqueId());
			} catch (NumberFormatException e) {
				result.setSurveyUid(getText(sheet, 0, 0, Survey.TITLE, invalidKeys, servletContext));

				Survey survey = surveyService.getSurveyByUniqueId(result.getSurveyUid(), false, true);
				surveyId = survey.getId();
				result.setSurveyId(surveyId);
			}

			String language = getText(sheet, 0, 1, "language", invalidKeys, servletContext);

			Language objLang = surveyService.getLanguage(language);
			if (objLang == null) {
				objLang = new Language(language, language, language, false);
			}

			result.setLanguage(objLang);

			HashMap<String, String> typeSuffixMapping = getTypeSuffixMapping();
			String label;

			for (int r = 1; r < rows; r++) {
				HSSFRow row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				String key = getText(sheet, r, 0, "key" + r, invalidKeys, servletContext);
				String type = getText(sheet, r, 1, key, invalidKeys, servletContext);

				if (typeSuffixMapping.containsKey(type)) {
					key = key + typeSuffixMapping.get(type);
				}

				label = getText(sheet, r, 2, key, invalidKeys, servletContext);

				if (key.equalsIgnoreCase(Survey.TITLE)) {
					result.setTitle(label);
				}

				result.getTranslations().add(new Translation(key, label, language, surveyId, result));
			}

			wb.close();
			inputStream.close();

			return removeIDs(result);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	public static Translations importODS(InputStream inputStream, List<String> invalidKeys, SurveyService surveyService,
			ServletContext servletContext) {
		try {

			Translations result = new Translations();

			SpreadsheetDocument spreadsheet = SpreadsheetDocument.loadDocument(inputStream);
			org.odftoolkit.simple.table.Table sheet = spreadsheet.getSheetByIndex(0);

			int rows = sheet.getRowCount();

			int surveyId = 0;
			try {
				surveyId = Integer.parseInt(sheet.getCellByPosition(0, 0).getStringValue().trim());
				result.setSurveyId(surveyId);

				Survey survey = surveyService.getSurvey(surveyId, false, true);
				result.setSurveyUid(survey.getUniqueId());
			} catch (NumberFormatException e) {
				String surveyUid = sheet.getCellByPosition(0, 0).getStringValue().trim();
				result.setSurveyUid(surveyUid);

				Survey survey = surveyService.getSurveyByUniqueId(surveyUid, false, true);
				surveyId = survey.getId();
				result.setSurveyId(surveyId);
			}

			String language = getODSText(sheet, 0, 1, "language", invalidKeys, servletContext);

			Language objLang = surveyService.getLanguage(language);
			if (objLang == null) {
				objLang = new Language(language, language, language, false);
			}

			result.setLanguage(objLang);

			HashMap<String, String> typeSuffixMapping = getTypeSuffixMapping();

			int emptyLineCount = 0;

			for (int r = 1; r < rows; r++) {
				org.odftoolkit.simple.table.Row row = sheet.getRowByIndex(r);
				if (row == null) {
					continue;
				}
				if (emptyLineCount > maxEmptyOdsLines) {
					logger.debug(String.format("importODS: Exiting import loop due to %d consecutive empty rows.",
							emptyLineCount));
					break;
				}

				String key = getODSText(sheet, r, 0, "key" + r, invalidKeys, servletContext);
				String type = getODSText(sheet, r, 1, key, invalidKeys, servletContext);

				if (key.isEmpty()) {
					emptyLineCount++;
					continue;
				}
				if (typeSuffixMapping.containsKey(type)) {
					key = key + typeSuffixMapping.get(type);
				}
				String label = getODSText(sheet, r, 2, key, invalidKeys, servletContext);

				result.getTranslations().add(new Translation(key, label, language, surveyId, result));

				if (key.equalsIgnoreCase(Survey.TITLE)) {
					result.setTitle(label);
				}

			}

			inputStream.close();

			return removeIDs(result);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	private static String getODSText(org.odftoolkit.simple.table.Table sheet, int row, int column, String key,
			List<String> invalidKeys, ServletContext servletContext) {
		String result = Tools.filterHTML(sheet.getCellByPosition(column, row).getStringValue().trim());
		return getText(result, key, invalidKeys, servletContext);
	}

	private static String getText(HSSFSheet sheet, int row, int cell, String key, List<String> invalidKeys,
			ServletContext servletContext) {
		try {
			String result;

			if (sheet.getRow(row).getCell(cell).getCellType() == 0) {
				result = "" + ((int) sheet.getRow(row).getCell(cell).getNumericCellValue());
			} else {
				result = Tools.filterHTML(sheet.getRow(row).getCell(cell).getStringCellValue().trim());
			}

			return getText(result, key, invalidKeys, servletContext);
		} catch (Exception e) {
			return "";
		}
	}

	private static String getText(String result, String key, List<String> invalidKeys, ServletContext servletContext) {
		// replace invalid characters
		String re = "\\p{Cntrl}";
		result = result.replaceAll(re, "");

		try {

			// check wellformed-ness

			if (result != null && !XHTMLValidator.validate(result, servletContext, null)) {
				invalidKeys.add(key);
				return "";
			}

			return result;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			invalidKeys.add(key);
		}

		return "";
	}

	public static String serializeXml(org.w3c.dom.Node element) throws TransformerException, TransformerFactoryConfigurationError, UnsupportedEncodingException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(buffer);

		DOMSource source = new DOMSource(element);
		TransformerFactory.newInstance().newTransformer().transform(source, result);

		return new String(buffer.toByteArray(), "utf-8").replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "")
				.replace("<" + element.getNodeName() + ">", "").replace("</" + element.getNodeName() + ">", "")
				.replace("<" + element.getNodeName() + "/>", "").trim();
	}

	private static String getText(NodeList elements, String name) throws MessageException, UnsupportedEncodingException, TransformerException, TransformerFactoryConfigurationError {
		if (elements == null || elements.getLength() < 1) {
			if (name.equalsIgnoreCase("Help") || name.equalsIgnoreCase(Section.TABTITLE))
				return null;
			throw new MessageException(name + " was not found!");
		}

		return Tools.repairXML(Tools.filterHTML(serializeXml(elements.item(0))));
	}

	public static void synchronize(Translations existingTranslations, Translations translations) {

		existingTranslations.setActive(false);
		existingTranslations.setTitle(translations.getTitle());

		Map<String, Translation> existingTranslationsMap = existingTranslations.getTranslationsByKey();

		for (Translation translation : translations.getTranslations()) {
			if (existingTranslationsMap.containsKey(translation.getKey())) {
				existingTranslationsMap.get(translation.getKey()).setLabel(translation.getLabel());
			} else {
				existingTranslations.getTranslations().add(new Translation(translation.getKey(), translation.getLabel(),
						translation.getLanguage(), existingTranslations.getSurveyId(), existingTranslations, translation.getLocked()));
			}
		}

	}

	private static boolean notNullOrEmpty(String s) {
		if (s == null)
			return false;
		return s.trim().length() != 0;
	}

	public static void synchronizePivot(Survey survey, Translations translations) {

		Map<String, Translation> translationsByKey = translations.getTranslationsByKey();

		if (translationsByKey.containsKey(Survey.TITLE)
				&& notNullOrEmpty(translationsByKey.get(Survey.TITLE).getLabel()))
			survey.setTitle(translationsByKey.get(Survey.TITLE).getLabel());
		if (translationsByKey.containsKey(Survey.LOGOTEXT)
				&& notNullOrEmpty(translationsByKey.get(Survey.LOGOTEXT).getLabel()))
			survey.setLogoText(translationsByKey.get(Survey.LOGOTEXT).getLabel());
		if (translationsByKey.containsKey(Survey.INTRODUCTION)
				&& notNullOrEmpty(translationsByKey.get(Survey.INTRODUCTION).getLabel()))
			survey.setIntroduction(translationsByKey.get(Survey.INTRODUCTION).getLabel());
		if (translationsByKey.containsKey(Survey.ESCAPEPAGE)
				&& notNullOrEmpty(translationsByKey.get(Survey.ESCAPEPAGE).getLabel()))
			survey.setEscapePage(translationsByKey.get(Survey.ESCAPEPAGE).getLabel());
		if (translationsByKey.containsKey(Survey.ESCAPELINK)
				&& notNullOrEmpty(translationsByKey.get(Survey.ESCAPELINK).getLabel()))
			survey.setEscapeLink(translationsByKey.get(Survey.ESCAPELINK).getLabel());
		if (translationsByKey.containsKey(Survey.CONFIRMATIONPAGE)
				&& notNullOrEmpty(translationsByKey.get(Survey.CONFIRMATIONPAGE).getLabel()))
			survey.setConfirmationPage(translationsByKey.get(Survey.CONFIRMATIONPAGE).getLabel());
		if (translationsByKey.containsKey(Survey.CONFIRMATIONLINK)
				&& notNullOrEmpty(translationsByKey.get(Survey.CONFIRMATIONLINK).getLabel()))
			survey.setConfirmationLink(translationsByKey.get(Survey.CONFIRMATIONLINK).getLabel());

		Set<String> linkstodelete = new HashSet<>();
		Map<String, String> newlinks = new HashMap<>();
		for (String key : survey.getUsefulLinks().keySet()) {
			String[] data = key.split("#");
			if (translationsByKey.containsKey(data[0] + "#usefullink")
					&& notNullOrEmpty(translationsByKey.get(data[0] + "#usefullink").getLabel())
					&& !translationsByKey.get(data[0] + "#usefullink").getLabel().equals(data[1])) {
				newlinks.put(data[0] + "#" + translationsByKey.get(data[0] + "#usefullink").getLabel(),
						survey.getUsefulLinks().get(key));
				linkstodelete.add(key);
			}
		}
		for (Entry<String, String> link : newlinks.entrySet()) {
			survey.getUsefulLinks().put(link.getKey(), link.getValue());
		}
		for (String key : linkstodelete) {
			survey.getUsefulLinks().remove(key);
		}

		Set<String> backdocstodelete = new HashSet<>();
		Map<String, String> newbackdocs = new HashMap<>();
		for (String filename : survey.getBackgroundDocuments().keySet()) {
			String url = survey.getBackgroundDocuments().get(filename);
			String uid = BasicService.getFileUIDFromUrl(url);
			if (translationsByKey.containsKey(uid + "#backgrounddocument")
					&& notNullOrEmpty(translationsByKey.get(uid + "#backgrounddocument").getLabel())
					&& !translationsByKey.get(uid + "#backgrounddocument").getLabel().equals(filename)) {
				newbackdocs.put(translationsByKey.get(uid + "#backgrounddocument").getLabel(),
						survey.getBackgroundDocuments().get(filename));
				translations.getTranslations()
						.add(new Translation(
								translationsByKey.get(uid + "#backgrounddocument").getLabel() + "#backgrounddocument",
								translationsByKey.get(uid + "#backgrounddocument").getLabel(),
								translations.getLanguage().getCode(), survey.getId(), translations, translationsByKey.get(uid + "#backgrounddocument").getLocked()));
				translations.getTranslations().remove(translationsByKey.get(uid + "#backgrounddocument"));
				backdocstodelete.add(filename);
			}
		}
		for (Entry<String, String> doc : newbackdocs.entrySet()) {
			survey.getBackgroundDocuments().put(doc.getKey(), doc.getValue());
		}
		for (String key : backdocstodelete) {
			survey.getBackgroundDocuments().remove(key);
		}

		for (Element element : survey.getElements()) {
			if (element instanceof Ruler) {
				continue;
			}

			if (translationsByKey.containsKey(element.getId().toString())
					&& notNullOrEmpty(translationsByKey.get(element.getId().toString()).getLabel()))
				element.setTitle(translationsByKey.get(element.getId().toString()).getLabel());
			if (translationsByKey.containsKey(element.getUniqueId())
					&& notNullOrEmpty(translationsByKey.get(element.getUniqueId()).getLabel()))
				element.setTitle(translationsByKey.get(element.getUniqueId()).getLabel());

			if (element instanceof Question) {
				Question question = (Question) element;
				if (translationsByKey.containsKey(element.getId().toString() + "help")
						&& notNullOrEmpty(translationsByKey.get(element.getId().toString() + "help").getLabel()))
					question.setHelp(translationsByKey.get(element.getId().toString() + "help").getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + "help")
						&& notNullOrEmpty(translationsByKey.get(element.getUniqueId() + "help").getLabel()))
					question.setHelp(translationsByKey.get(element.getUniqueId() + "help").getLabel());
			}

			if (element instanceof Section) {
				Section section = (Section) element;
				if (translationsByKey.containsKey(element.getId().toString() + Section.TABTITLE) && notNullOrEmpty(
						translationsByKey.get(element.getId().toString() + Section.TABTITLE).getLabel()))
					section.setTabTitle(
							translationsByKey.get(element.getId().toString() + Section.TABTITLE).getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + Section.TABTITLE)
						&& notNullOrEmpty(translationsByKey.get(element.getUniqueId() + Section.TABTITLE).getLabel()))
					section.setTabTitle(translationsByKey.get(element.getUniqueId() + Section.TABTITLE).getLabel());
			}

			if (element instanceof NumberQuestion) {
				NumberQuestion number = (NumberQuestion) element;
				if (translationsByKey.containsKey(element.getId().toString() + NumberQuestion.UNIT) && notNullOrEmpty(
						translationsByKey.get(element.getId().toString() + NumberQuestion.UNIT).getLabel()))
					number.setUnit(translationsByKey.get(element.getId().toString() + NumberQuestion.UNIT).getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + NumberQuestion.UNIT) && notNullOrEmpty(
						translationsByKey.get(element.getUniqueId() + NumberQuestion.UNIT).getLabel()))
					number.setUnit(translationsByKey.get(element.getUniqueId() + NumberQuestion.UNIT).getLabel());

				if (number.isSlider()) {
					if (translationsByKey.containsKey(element.getId().toString() + NumberQuestion.MINLABEL) && notNullOrEmpty(translationsByKey.get(element.getId().toString() + NumberQuestion.MINLABEL).getLabel())) {
						number.setMinLabel(translationsByKey.get(element.getId().toString() + NumberQuestion.MINLABEL).getLabel());
					}

					if (translationsByKey.containsKey(element.getUniqueId() + NumberQuestion.MINLABEL) && notNullOrEmpty(translationsByKey.get(element.getUniqueId() + NumberQuestion.MINLABEL).getLabel())) {
						number.setMinLabel(translationsByKey.get(element.getUniqueId() + NumberQuestion.MINLABEL).getLabel());
					}

					if (translationsByKey.containsKey(element.getId().toString() + NumberQuestion.MAXLABEL) && notNullOrEmpty(translationsByKey.get(element.getId().toString() + NumberQuestion.MAXLABEL).getLabel())) {
						number.setMaxLabel(translationsByKey.get(element.getId().toString() + NumberQuestion.MAXLABEL).getLabel());
					}

					if (translationsByKey.containsKey(element.getUniqueId() + NumberQuestion.MAXLABEL) && notNullOrEmpty(translationsByKey.get(element.getUniqueId() + NumberQuestion.MAXLABEL).getLabel())) {
						number.setMaxLabel(translationsByKey.get(element.getUniqueId() + NumberQuestion.MAXLABEL).getLabel());
					}
				}
			}

			if (element instanceof Confirmation) {
				Confirmation confirmation = (Confirmation) element;
				if (translationsByKey.containsKey(element.getId().toString() + Confirmation.TEXT) && notNullOrEmpty(
						translationsByKey.get(element.getId().toString() + Confirmation.TEXT).getLabel()))
					confirmation.setConfirmationtext(
							translationsByKey.get(element.getId().toString() + Confirmation.TEXT).getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + Confirmation.TEXT)
						&& notNullOrEmpty(translationsByKey.get(element.getUniqueId() + Confirmation.TEXT).getLabel()))
					confirmation.setConfirmationtext(
							translationsByKey.get(element.getUniqueId() + Confirmation.TEXT).getLabel());

				if (translationsByKey.containsKey(element.getId().toString() + Confirmation.LABEL) && notNullOrEmpty(
						translationsByKey.get(element.getId().toString() + Confirmation.LABEL).getLabel()))
					confirmation.setConfirmationlabel(
							translationsByKey.get(element.getId().toString() + Confirmation.LABEL).getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + Confirmation.LABEL)
						&& notNullOrEmpty(translationsByKey.get(element.getUniqueId() + Confirmation.LABEL).getLabel()))
					confirmation.setConfirmationlabel(
							translationsByKey.get(element.getUniqueId() + Confirmation.LABEL).getLabel());
			}

			if (element instanceof ChoiceQuestion) {
				ChoiceQuestion choice = (ChoiceQuestion) element;
				for (PossibleAnswer answer : choice.getPossibleAnswers()) {
					if (translationsByKey.containsKey(answer.getId().toString())
							&& notNullOrEmpty(translationsByKey.get(answer.getId().toString()).getLabel()))
						answer.setTitle(translationsByKey.get(answer.getId().toString()).getLabel());
					if (translationsByKey.containsKey(answer.getUniqueId())
							&& notNullOrEmpty(translationsByKey.get(answer.getUniqueId()).getLabel()))
						answer.setTitle(translationsByKey.get(answer.getUniqueId()).getLabel());
				}
			}

			if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;

				if (translationsByKey.containsKey(element.getId().toString() + MatrixOrTable.FIRSTCELL)
						&& notNullOrEmpty(
								translationsByKey.get(element.getId().toString() + MatrixOrTable.FIRSTCELL).getLabel()))
					matrix.setFirstCellText(
							translationsByKey.get(element.getId().toString() + MatrixOrTable.FIRSTCELL).getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + MatrixOrTable.FIRSTCELL)
						&& notNullOrEmpty(translationsByKey
								.get(element.getUniqueId() + MatrixOrTable.FIRSTCELL).getLabel()))
					matrix.setFirstCellText(translationsByKey
							.get(element.getUniqueId() + MatrixOrTable.FIRSTCELL).getLabel());

				for (Element child : matrix.getChildElements()) {
					if (child instanceof Text) {
						if (translationsByKey.containsKey(child.getId().toString())
								&& notNullOrEmpty(translationsByKey.get(child.getId().toString()).getLabel()))
							child.setTitle(translationsByKey.get(child.getId().toString()).getLabel());
						if (translationsByKey.containsKey(child.getUniqueId())
								&& notNullOrEmpty(translationsByKey.get(child.getUniqueId()).getLabel()))
							child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());
					}
				}
			}

			if (element instanceof Table) {
				Table table = (Table) element;

				if (translationsByKey.containsKey(element.getId().toString() + MatrixOrTable.FIRSTCELL)
						&& notNullOrEmpty(
								translationsByKey.get(element.getId().toString() + MatrixOrTable.FIRSTCELL).getLabel()))
					table.setFirstCellText(
							translationsByKey.get(element.getId().toString() + MatrixOrTable.FIRSTCELL).getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + MatrixOrTable.FIRSTCELL)
						&& notNullOrEmpty(translationsByKey
								.get(element.getUniqueId() + MatrixOrTable.FIRSTCELL).getLabel()))
					table.setFirstCellText(translationsByKey
							.get(element.getUniqueId() + MatrixOrTable.FIRSTCELL).getLabel());

				for (Element child : table.getChildElements()) {
					if (child instanceof Text) {
						if (translationsByKey.containsKey(child.getId().toString())
								&& notNullOrEmpty(translationsByKey.get(child.getId().toString()).getLabel()))
							child.setTitle(translationsByKey.get(child.getId().toString()).getLabel());
						if (translationsByKey.containsKey(child.getUniqueId())
								&& notNullOrEmpty(translationsByKey.get(child.getUniqueId()).getLabel()))
							child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());
					}
				}
			}

			if (element instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion) element;
				for (Element child : rating.getChildElements()) {
					if (child instanceof Text) {
						if (translationsByKey.containsKey(child.getId().toString())
								&& notNullOrEmpty(translationsByKey.get(child.getId().toString()).getLabel()))
							child.setTitle(translationsByKey.get(child.getId().toString()).getLabel());
						if (translationsByKey.containsKey(child.getUniqueId())
								&& notNullOrEmpty(translationsByKey.get(child.getUniqueId()).getLabel()))
							child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());
					}
				}
			}
			
			if (element instanceof RankingQuestion) {
				RankingQuestion ranking = (RankingQuestion) element;
				for (Element child : ranking.getChildElements()) {
					if (translationsByKey.containsKey(child.getId().toString())
							&& notNullOrEmpty(translationsByKey.get(child.getId().toString()).getLabel()))
						child.setTitle(translationsByKey.get(child.getId().toString()).getLabel());
					if (translationsByKey.containsKey(child.getUniqueId())
							&& notNullOrEmpty(translationsByKey.get(child.getUniqueId()).getLabel()))
						child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());
				}
			}
			
			if (element instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) element;
				for (com.ec.survey.model.survey.base.File child : gallery.getFiles()) {
					if (translationsByKey.containsKey(child.getUid() + GalleryQuestion.TEXT)
							&& notNullOrEmpty(translationsByKey.get(child.getUid()+ GalleryQuestion.TEXT).getLabel()))
						child.setComment(translationsByKey.get(child.getUid() + GalleryQuestion.TEXT).getLabel());

					if (translationsByKey.containsKey(child.getUid())
							&& notNullOrEmpty(translationsByKey.get(child.getUid()).getLabel()))
						child.setDescription(translationsByKey.get(child.getUid()).getLabel());

					if (translationsByKey.containsKey(child.getUid() + GalleryQuestion.TITLE)
							&& notNullOrEmpty(translationsByKey.get(child.getUid() + GalleryQuestion.TITLE).getLabel()))
						child.setName(translationsByKey.get(child.getUid() + GalleryQuestion.TITLE).getLabel());
				}
			}

			if (element instanceof ComplexTable) {
				ComplexTable table = (ComplexTable) element;

				for (ComplexTableItem child : table.getChildElements()) {

					if (translationsByKey.containsKey(child.getUniqueId())
							&& notNullOrEmpty(translationsByKey.get(child.getUniqueId()).getLabel()))
						child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());

					if (translationsByKey.containsKey(child.getUniqueId() + "RESULTTEXT")
							&& notNullOrEmpty(translationsByKey.get(child.getUniqueId() + "RESULTTEXT").getLabel()))
						child.setResultText(translationsByKey.get(child.getUniqueId() + "RESULTTEXT").getLabel());

					if (translationsByKey.containsKey(child.getUniqueId() + "UNIT")
							&& notNullOrEmpty(translationsByKey.get(child.getUniqueId() + "UNIT").getLabel()))
						child.setUnit(translationsByKey.get(child.getUniqueId() + "UNIT").getLabel());

					if (child.isChoice()){
						for (PossibleAnswer possibleAnswer : child.getPossibleAnswers()){
							if (translationsByKey.containsKey(possibleAnswer.getUniqueId())
									&& notNullOrEmpty(translationsByKey.get(possibleAnswer.getUniqueId()).getLabel()))
								possibleAnswer.setTitle(translationsByKey.get(possibleAnswer.getUniqueId()).getLabel());
						}
					}
				}
			}
		}
	}

	public static boolean includeNewKeyTranslations(Survey survey, Translations translations, Iterable<String> keys) {
		Map<String, String> translationsMap = translations.getTranslationsMap();
		boolean changed = false;
		for (String key : keys){
			if (!translationsMap.containsKey(key)){
				changed = true;
				if (key.equals(Survey.LOGOTEXT) && notNullOrEmpty(survey.getLogoText()))
					translations.getTranslations().add(new Translation(key, survey.getLogoText(), translations.getLanguage().getCode(), translations.getSurveyId(), translations));
				if (key.equals(Survey.ESCAPEPAGE) && notNullOrEmpty(survey.getEscapePage()))
					translations.getTranslations().add(new Translation(key, survey.getEscapePage(), translations.getLanguage().getCode(), translations.getSurveyId(), translations));
				if (key.equals(Survey.ESCAPELINK) && notNullOrEmpty(survey.getEscapeLink()))
					translations.getTranslations().add(new Translation(key, survey.getEscapeLink(), translations.getLanguage().getCode(), translations.getSurveyId(), translations));
				if (key.equals(Survey.CONFIRMATIONPAGE) && notNullOrEmpty(survey.getConfirmationPage()))
					translations.getTranslations().add(new Translation(key, survey.getConfirmationPage(), translations.getLanguage().getCode(), translations.getSurveyId(), translations));
				if (key.equals(Survey.CONFIRMATIONLINK) && notNullOrEmpty(survey.getConfirmationLink()))
					translations.getTranslations().add(new Translation(key, survey.getConfirmationLink(), translations.getLanguage().getCode(), translations.getSurveyId(), translations));
			}
		}
		return changed;
	}

	public static boolean isComplete(Translations translations, Survey survey) // TODO: Add all the other mandatory
																				// elements. Check also all elements are
																				// correctly taken into account in the
																				// similar helper functions above
	{
		try {
			Map<String, String> translationMap = translations.getTranslationsMap();

			if ((!translationMap.containsKey(Survey.TITLE) || translationMap.get(Survey.TITLE) == null
					|| translationMap.get(Survey.TITLE).trim().length() == 0))
				return false;
			if (survey.getLogoText() != null && survey.getLogoText().trim().length() > 0
					&& (!translationMap.containsKey(Survey.LOGOTEXT)
							|| translationMap.get(Survey.LOGOTEXT).trim().length() == 0))
				return false;
			if (survey.getIntroduction() != null && survey.getIntroduction().trim().length() > 0
					&& (!translationMap.containsKey(Survey.INTRODUCTION)
							|| translationMap.get(Survey.INTRODUCTION).trim().length() == 0))
				return false;
			if (survey.getEscapePage() != null && survey.getEscapePage().trim().length() > 0
					&& (!translationMap.containsKey(Survey.ESCAPEPAGE)
							|| translationMap.get(Survey.ESCAPEPAGE).trim().length() == 0))
				return false;
			if (survey.getEscapeLink() != null && survey.getEscapeLink().trim().length() > 0
					&& (!translationMap.containsKey(Survey.ESCAPELINK)
							|| translationMap.get(Survey.ESCAPELINK).trim().length() == 0))
				return false;
			if (survey.getConfirmationPage() != null && survey.getConfirmationPage().trim().length() > 0
					&& (!translationMap.containsKey(Survey.CONFIRMATIONPAGE)
							|| translationMap.get(Survey.CONFIRMATIONPAGE).trim().length() == 0))
				return false;
			if (survey.getConfirmationLink() != null && survey.getConfirmationLink().trim().length() > 0
					&& (!translationMap.containsKey(Survey.CONFIRMATIONLINK)
							|| translationMap.get(Survey.CONFIRMATIONLINK).trim().length() == 0))
				return false;

			for (Element element : survey.getElementsRecursive()) {
				if (element instanceof Ruler) {
					continue;
				}

				if (!(element instanceof EmptyElement) && element.getTitle() != null) {
					if (element.getTitle().length() > 0 && (getLabel(element, "", translationMap).length() == 0)) {
						return false;
					}
					if (element instanceof Question) {
						Question question = (Question) element;

						if (question instanceof ChoiceQuestion) {
							ChoiceQuestion choice = (ChoiceQuestion) question;
							for (PossibleAnswer answer : choice.getPossibleAnswers()) {
								if (getLabel(answer, "", translationMap).length() == 0) {
									return false;
								}
							}
						}

						if (question instanceof FreeTextQuestion
								&& getLabel(question, "", translationMap).length() == 0) {
							return false;
						}
					}

					if (element instanceof MatrixOrTable) {
						MatrixOrTable matrix = (MatrixOrTable) element;

						if (matrix.getFirstCellText() != null && matrix.getFirstCellText().trim().length() > 0
								&& getLabel(matrix, MatrixOrTable.FIRSTCELL, translationMap).length() == 0) {
							return false;
						}

						for (Element child : matrix.getChildElements()) {
							if (child instanceof Text && getLabel(child, "", translationMap).length() == 0) {
								return false;
							}
						}
					}

					if (element instanceof RatingQuestion) {
						RatingQuestion rating = (RatingQuestion) element;

						for (Element child : rating.getChildElements()) {
							if (child instanceof Text && getLabel(child, "", translationMap).length() == 0) {
								return false;
							}
						}
					}
					
					if (element instanceof RankingQuestion) {
						RankingQuestion ranking = (RankingQuestion) element;

						for (Element child : ranking.getChildElements()) {
							if (getLabel(child, "", translationMap).length() == 0) {
								return false;
							}
						}
					}

					if (element instanceof ComplexTable){
						ComplexTable table = (ComplexTable) element;
						for (ComplexTableItem child : table.getChildElements()) {
							if (child.isChoice()) {
								for (PossibleAnswer answer : child.getPossibleAnswers()) {
									if (getLabel(answer, "", translationMap).length() == 0) {
										return false;
									}
								}
							}
						}
					}

				}
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return false;
		}
	}

	public static void replaceLockedTranslations(Translations translations, Translations pivot) {
		Map<String, Translation> translationsByKey = translations.getTranslationsByKey();
		for (Translation translation : pivot.getTranslations()) {
			if (translation.getLocked() && translationsByKey.containsKey(translation.getKey())) {
				translationsByKey.get(translation.getKey()).setLabel(translation.getLabel());
			}
		}		
	}

	private static abstract class SheetTranslationCreator {

		private final Map<String, String> descriptions;
		private final Map<String, String> translationsByKey;

		private SheetTranslationCreator(Map<String, String> translationsByKey, Map<String, String> descriptions) {
			this.descriptions = descriptions;
			this.translationsByKey = translationsByKey;
		}

		public abstract SheetRow nextRow();

		public void nextLabelRow(Element element, String suffix){
			String label = getLabel(element, suffix, translationsByKey);
			if (label.trim().length() > 0) {
				SheetRow row = nextRow();
				addTextCell(row, 0, element.getUniqueId());
				addTextCell(row, 1, descriptions.get(element.getUniqueId() + suffix));
				addTextCell(row, 2, label);
			}
		}

		public void nextLabelRow(com.ec.survey.model.survey.base.File file, String suffix){
			String label = getLabel(file, suffix, translationsByKey);
			if (label.trim().length() > 0) {
				SheetRow row = nextRow();
				addTextCell(row, 0, file.getUid());
				addTextCell(row, 1, descriptions.get(file.getUid() + suffix));
				addTextCell(row, 2, label);
			}
		}

		public void nextLabelRow(Element element){
			nextLabelRow(element, "");
		}

		public void nextLabelRow(com.ec.survey.model.survey.base.File file){
			nextLabelRow(file, "");
		}

		public void nextAttributeRow(String key){
			if (translationsByKey.get(key) != null && translationsByKey.get(key).trim().length() > 0) {
				SheetRow row = nextRow();
				addTextCell(row, 0, key);
				addTextCell(row, 1, descriptions.get(key));
				addTextCell(row, 2, translationsByKey.get(key));
			}
		}
	}

	private static class SheetRow {

		private final boolean isForXls;

		private final Object rowObject;

		private SheetRow(Object rowObject){
			this.rowObject = rowObject;
			isForXls = rowObject instanceof Row;
		}
	}

	private static class XlsTranslationCreator extends SheetTranslationCreator {
		
		private final Sheet sheet;
		
		private XlsTranslationCreator(Sheet sheet, Map<String, String> translationsByKey, Map<String, String> descriptions){
			super(translationsByKey, descriptions);
			this.sheet = sheet;
		}

		private boolean anyRows = false;

		@Override
		public SheetRow nextRow(){
			if (!anyRows){
				anyRows = true;
				return new SheetRow(sheet.createRow(0));
			}
			return new SheetRow(sheet.createRow(sheet.getLastRowNum() + 1));
		}
	}

	private static class OdsTranslationCreator extends SheetTranslationCreator {

		private final org.odftoolkit.simple.table.Table sheet;

		private OdsTranslationCreator(org.odftoolkit.simple.table.Table sheet, Map<String, String> translationsByKey, Map<String, String> descriptions){
			super(translationsByKey, descriptions);
			this.sheet = sheet;
		}

		private boolean anyRows = false;

		@Override
		public SheetRow nextRow(){
			if (!anyRows){
				anyRows = true;
				return new SheetRow(sheet.getRowByIndex(0));
			}
			return new SheetRow(sheet.getRowByIndex(sheet.getRowCount()));
		}
	}
}
