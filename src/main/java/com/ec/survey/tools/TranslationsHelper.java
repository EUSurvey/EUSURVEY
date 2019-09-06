package com.ec.survey.tools;

import com.ec.survey.model.KeyValue;
import com.ec.survey.model.Language;
import com.ec.survey.model.Translation;
import com.ec.survey.model.Translations;
import com.ec.survey.model.survey.*;
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
import org.owasp.esapi.errors.IntrusionException;
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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

public class TranslationsHelper {

	private static final Logger logger = Logger.getLogger(TranslationsHelper.class);
	
	private static final int maxEmptyOdsLines = 20;
	
	public static Translations getTranslations(Survey survey, boolean complete)
	{
		Translations translations = new Translations();
		
		translations.setLanguage(survey.getLanguage());
		translations.setSurveyId(survey.getId());
		translations.setSurveyUid(survey.getUniqueId());
		translations.setTitle(survey.getTitle());
		translations.setActive(true);
		
		translations.getTranslations().add(new Translation(Survey.TITLE, survey.getTitle(), survey.getLanguage().getCode(), survey.getId(), translations));
		
		if (complete || notNullOrEmpty(survey.getIntroduction()))
		{
			translations.getTranslations().add(new Translation(Survey.INTRODUCTION, survey.getIntroduction() != null ? survey.getIntroduction() : "", survey.getLanguage().getCode(), survey.getId(), translations));
		}
		
		if (complete || notNullOrEmpty(survey.getEscapePage()))
		{
			translations.getTranslations().add(new Translation(Survey.ESCAPEPAGE, survey.getEscapePage() != null ? survey.getEscapePage() : "", survey.getLanguage().getCode(), survey.getId(), translations));
		}
		
		if (complete || notNullOrEmpty(survey.getEscapeLink()))
		{
			translations.getTranslations().add(new Translation(Survey.ESCAPELINK, survey.getEscapeLink() != null ? survey.getEscapeLink() : "", survey.getLanguage().getCode(), survey.getId(), translations));
		}
		
		if (complete || notNullOrEmpty(survey.getConfirmationPage()))
		{
			translations.getTranslations().add(new Translation(Survey.CONFIRMATIONPAGE, survey.getConfirmationPage() != null ? survey.getConfirmationPage() : "", survey.getLanguage().getCode(), survey.getId(), translations));
		}
		
		if (complete || notNullOrEmpty(survey.getConfirmationLink()))
		{
			translations.getTranslations().add(new Translation(Survey.CONFIRMATIONLINK, survey.getConfirmationLink() != null ? survey.getConfirmationLink() : "", survey.getLanguage().getCode(), survey.getId(), translations));
		}
		
		if (survey.getIsQuiz())
		{
			if (complete || notNullOrEmpty(survey.getQuizWelcomeMessage()))
			{
				translations.getTranslations().add(new Translation(Survey.QUIZWELCOMEMESSAGE, survey.getQuizWelcomeMessage() != null ? survey.getQuizWelcomeMessage() : "", survey.getLanguage().getCode(), survey.getId(), translations));
			}
			
			if (complete || notNullOrEmpty(survey.getQuizResultsMessage()))
			{
				translations.getTranslations().add(new Translation(Survey.QUIZRESULTSMESSAGE, survey.getQuizResultsMessage() != null ? survey.getQuizResultsMessage() : "", survey.getLanguage().getCode(), survey.getId(), translations));
			}
		}
		
		for (String key : survey.getUsefulLinks().keySet())
		{
			String[] data = key.split("#");
			if (data.length > 1)
			{
				translations.getTranslations().add(new Translation(data[0] + "#usefullink", data[1], survey.getLanguage().getCode(), survey.getId(), translations));
			} else {
				translations.getTranslations().add(new Translation("0#usefullink", data[0], survey.getLanguage().getCode(), survey.getId(), translations));
			}
		}
		
		for (String key : survey.getBackgroundDocuments().keySet())
		{
			translations.getTranslations().add(new Translation(key + "#backgrounddocument", key, survey.getLanguage().getCode(), survey.getId(), translations));
		}
		
		for (Element element : survey.getElements())
		{
			if (element.getUniqueId() != null)
			{
				translations.getTranslations().add(new Translation(element.getUniqueId(), element.getTitle() != null ? element.getTitle() : "", survey.getLanguage().getCode(), survey.getId(), translations));
				
				if (element instanceof Question)
				{
					Question question = (Question)element;
					if (complete || (question.getHelp() != null && question.getHelp().trim().length() > 0))
					{
						translations.getTranslations().add(new Translation(question.getUniqueId() + "help", question.getHelp() != null ? question.getHelp() : "", survey.getLanguage().getCode(), survey.getId(), translations));					
					}			
					
					if (question.getScoringItems() != null)
					{
						for (ScoringItem scoringItem : question.getScoringItems()) {
							if (notNullOrEmpty(scoringItem.getFeedback()))
							{
								translations.getTranslations().add(new Translation(scoringItem.getUniqueId() + Question.FEEDBACK, scoringItem.getFeedback() != null ? scoringItem.getFeedback() : "", survey.getLanguage().getCode(), survey.getId(), translations));
							}
						}
					}
				}
				
				if (element instanceof Section)
				{
					Section section = (Section)element;
					translations.getTranslations().add(new Translation(section.getUniqueId() + Section.TABTITLE, section.getTabTitle() != null ? section.getTabTitle() : "", survey.getLanguage().getCode(), survey.getId(), translations));
				}
				
				if (element instanceof NumberQuestion)
				{
					NumberQuestion number = (NumberQuestion)element;
					translations.getTranslations().add(new Translation(number.getUniqueId() + NumberQuestion.UNIT, number.getUnit() != null ? number.getUnit() : "", survey.getLanguage().getCode(), survey.getId(), translations));
				}
				
				if (element instanceof ChoiceQuestion)
				{
					ChoiceQuestion choice = (ChoiceQuestion)element;
					for (PossibleAnswer answer : choice.getPossibleAnswers())
					{
						translations.getTranslations().add(new Translation(answer.getUniqueId(), answer.getTitle() != null ? answer.getTitle() : "", survey.getLanguage().getCode(), survey.getId(), translations));
						
						if (answer.getScoring() != null && notNullOrEmpty(answer.getScoring().getFeedback()))
						{
							translations.getTranslations().add(new Translation(answer.getUniqueId() + Question.FEEDBACK, answer.getScoring().getFeedback() != null ? answer.getScoring().getFeedback() : "", survey.getLanguage().getCode(), survey.getId(), translations));
						}
					}
				}
				
				if (element instanceof Confirmation)
				{
					Confirmation confirmation = (Confirmation)element;
					translations.getTranslations().add(new Translation(confirmation.getUniqueId() + Confirmation.TEXT,confirmation.getConfirmationtext() != null ? confirmation.getConfirmationtext() : "", survey.getLanguage().getCode(), survey.getId(), translations));
					translations.getTranslations().add(new Translation(confirmation.getUniqueId() + Confirmation.LABEL,confirmation.getConfirmationlabel() != null ? confirmation.getConfirmationlabel() : "", survey.getLanguage().getCode(), survey.getId(), translations));
				}
			
				if (element instanceof Matrix)
				{
					Matrix matrix = (Matrix)element;
					
					for (Element child : matrix.getChildElements())
					{
						if (child instanceof Text && child.getUniqueId() != null)
						{
							translations.getTranslations().add(new Translation(child.getUniqueId(), child.getTitle() != null ? child.getTitle() : "", survey.getLanguage().getCode(), survey.getId(), translations));					
						}
					}
				}
				
				if (element instanceof Table)
				{
					Table table = (Table)element;
					for (Element child : table.getChildElements())
					{
						if (child instanceof Text)
						{
							translations.getTranslations().add(new Translation(child.getUniqueId(), child.getTitle() != null ? child.getTitle() : "", survey.getLanguage().getCode(), survey.getId(), translations));					
						}
					}				
				}
				
				if (element instanceof RatingQuestion)
				{
					RatingQuestion rating = (RatingQuestion)element;
					for (Element child : rating.getChildElements())
					{
						if (child instanceof Text)
						{
							translations.getTranslations().add(new Translation(child.getUniqueId(), child.getTitle() != null ? child.getTitle() : "", survey.getLanguage().getCode(), survey.getId(), translations));					
						}
					}				
				}
			} else {
				logger.warn("element without unique id found: " + element.getId());
			}
			
		}
		
		return translations;
	}
	

	public static List<KeyValue> getShortDescriptions(Survey survey, MessageSource resources, Locale locale) {
		List<KeyValue> result = new ArrayList<>();
		
		result.add(new KeyValue(Survey.TITLE, "L"));
		result.add(new KeyValue(Survey.CONFIRMATIONPAGE, "L"));
		result.add(new KeyValue(Survey.ESCAPEPAGE, "L"));
		result.add(new KeyValue(Survey.CONFIRMATIONLINK, "L"));
		result.add(new KeyValue(Survey.ESCAPELINK, "L"));
		result.add(new KeyValue(Survey.HELP, "H"));
		
		result.add(new KeyValue(Survey.QUIZWELCOMEMESSAGE, "L"));
		result.add(new KeyValue(Survey.QUIZRESULTSMESSAGE, "L"));
		
		for (String key : survey.getUsefulLinks().keySet())
		{
			String[] data = key.split("#");
			result.add(new KeyValue(data[0] + "#usefullink", "UL"));
		}
		
		for (String key : survey.getBackgroundDocuments().keySet())
		{
			result.add(new KeyValue(key + "#backgrounddocument", "BD"));
		}
		
		for (Element element : survey.getElements())
		{
			if (element instanceof Section)
			{
				result.add(new KeyValue(element.getUniqueId(), "L"));
				result.add(new KeyValue(element.getUniqueId()+ Section.TABTITLE, "T"));
			}
			
			if (element instanceof Question)
			{
				result.add(new KeyValue(element.getUniqueId(), "L"));
				Question question = (Question)element;
				if (question.getHelp() != null && question.getHelp().trim().length() > 0)
				{
					result.add(new KeyValue(element.getUniqueId()+ "help", "H"));
				}
				
				if (question.getScoringItems() != null)
				{
					for (ScoringItem scoringItem : question.getScoringItems()) {
						result.add(new KeyValue(scoringItem.getUniqueId()+ Question.FEEDBACK, "F"));
					}
				}
			}
			
			if (element instanceof NumberQuestion)
			{
				result.add(new KeyValue(element.getUniqueId()+ NumberQuestion.UNIT, "U"));
			}
			
			if (element instanceof ChoiceQuestion)
			{
				ChoiceQuestion choice = (ChoiceQuestion)element;
				for (PossibleAnswer answer : choice.getPossibleAnswers())
				{
					result.add(new KeyValue(answer.getUniqueId(), "L"));
					
					if (answer.getScoring() != null && notNullOrEmpty(answer.getScoring().getFeedback()))
					{
						result.add(new KeyValue(answer.getUniqueId() + Question.FEEDBACK, "F"));
					}
				}
			}
			
			if (element instanceof Text)
			{
				result.add(new KeyValue(element.getUniqueId(), "L"));
			}
			
			if (element instanceof Confirmation)
			{
				result.add(new KeyValue(element.getUniqueId()+ Confirmation.TEXT, "CT"));
				result.add(new KeyValue(element.getUniqueId()+ Confirmation.LABEL, "CL"));
			}			
			
			if (element instanceof Matrix)
			{
				Matrix matrix = (Matrix)element;
				
				for (Element child : matrix.getChildElements())
				{
					if (child instanceof Text)
					{
						result.add(new KeyValue(child.getUniqueId(), "L"));
					}
				}
			}
			
			if (element instanceof Table)
			{
				Table table = (Table)element;
				for (Element child : table.getChildElements())
				{
					if (child instanceof Text)
					{
						result.add(new KeyValue(child.getUniqueId(), "L"));
					}
				}				
			}
			
			if (element instanceof RatingQuestion)
			{
				RatingQuestion rating = (RatingQuestion)element;
				for (Element child : rating.getChildElements())
				{
					if (child instanceof Text)
					{
						result.add(new KeyValue(child.getUniqueId(), "L"));
					}
				}				
			}
		}
		
		return result;
	}
	
	public static List<KeyValue> getLongDescriptions(Survey survey, MessageSource resources, Locale locale) {
		List<KeyValue> result = new ArrayList<>();
		
		result.add(new KeyValue(Survey.TITLE, resources.getMessage("label.Title", null, "Title", locale)));
		result.add(new KeyValue(Survey.CONFIRMATIONPAGE, resources.getMessage("label.ConfirmationPage", null, "Confirmation Page", locale)));
		result.add(new KeyValue(Survey.ESCAPEPAGE, resources.getMessage("label.UnavailabilityPage", null, "Unavailability Page", locale)));
		result.add(new KeyValue(Survey.CONFIRMATIONLINK, resources.getMessage("label.ConfirmationLink", null, "Confirmation Link", locale)));
		result.add(new KeyValue(Survey.ESCAPELINK, resources.getMessage("label.EscapeLink", null, "Escape Link", locale)));
		result.add(new KeyValue(Survey.HELP, resources.getMessage("label.HelpMessage", null, "Help Message", locale)));
		
		result.add(new KeyValue(Survey.QUIZWELCOMEMESSAGE, resources.getMessage("label.QuizStartPage", null, "Quiz Start Page", locale)));
		result.add(new KeyValue(Survey.QUIZRESULTSMESSAGE, resources.getMessage("label.QuizResultPage", null, "Quiz Result Page", locale)));
		
		for (String key : survey.getUsefulLinks().keySet())
		{
			String[] data = key.split("#");
			result.add(new KeyValue(data[0] + "#usefullink", resources.getMessage("label.UsefulLink", null, "Useful Link", locale)));
		}
		
		for (String key : survey.getBackgroundDocuments().keySet())
		{
			result.add(new KeyValue(key + "#backgrounddocument", resources.getMessage("label.BackgroundDocument", null, "Background Document", locale)));
		}
		
		for (Element element : survey.getElements())
		{
			result.add(new KeyValue(element.getUniqueId()+ "shortname", resources.getMessage("label.Identifier", null, "Identifier", locale)));
						
			if (element instanceof Section)
			{
				result.add(new KeyValue(element.getUniqueId(), resources.getMessage("label.SectionText", null, "Section Text", locale)));
				result.add(new KeyValue(element.getUniqueId() + Section.TABTITLE, resources.getMessage("label.TabTitle", null, "Tab Title", locale)));
			}
			
			if (element instanceof Question && !(element instanceof Text))
			{
				result.add(new KeyValue(element.getUniqueId(), resources.getMessage("label.QuestionText", null, "Question Text", locale)));
				result.add(new KeyValue(element.getUniqueId()+ "help", resources.getMessage("label.HelpMessage", null, "Help Message", locale)));
				
				Question question = (Question) element;
				
				if (question.getScoringItems() != null)
				{
					for (ScoringItem scoringItem : question.getScoringItems()) {
						result.add(new KeyValue(scoringItem.getUniqueId()+ Question.FEEDBACK, resources.getMessage("label.Feedback", null, "Feedback", locale)));
					}
				}
			}
			
			if (element instanceof NumberQuestion)
			{
				result.add(new KeyValue(element.getUniqueId()+ NumberQuestion.UNIT, resources.getMessage("label.Unit", null, "Unit", locale)));
			}
			
			if (element instanceof ChoiceQuestion)
			{
				ChoiceQuestion choice = (ChoiceQuestion)element;
				for (PossibleAnswer answer : choice.getPossibleAnswers())
				{
					result.add(new KeyValue(answer.getUniqueId(), resources.getMessage("label.Answer", null, "Answer", locale)));
					
					if (answer.getScoring() != null && notNullOrEmpty(answer.getScoring().getFeedback()))
					{
						result.add(new KeyValue(answer.getUniqueId() + Question.FEEDBACK, resources.getMessage("label.Feedback", null, "Feedback", locale)));
					}
				}
			}
			
			if (element instanceof Confirmation)
			{
				result.add(new KeyValue(element.getUniqueId()+ Confirmation.TEXT, resources.getMessage("label.ConfirmationText", null, "Confirmation Text", locale)));
				result.add(new KeyValue(element.getUniqueId()+ Confirmation.LABEL, resources.getMessage("label.ConfirmationLabel", null, "Confirmation Label", locale)));
			}
			
			if (element instanceof Text)
			{
				result.add(new KeyValue(element.getUniqueId(), resources.getMessage("label.Text", null, "Text", locale)));
			}
					
			if (element instanceof Matrix)
			{
				Matrix matrix = (Matrix)element;
				
				for (Element child : matrix.getChildElements())
				{
					if (child instanceof Text)
					{
						result.add(new KeyValue(child.getUniqueId(), resources.getMessage("label.MatrixText", null, "Matrix Text", locale)));
					}
				}
			}
			
			if (element instanceof Table)
			{
				Table table = (Table)element;
				for (Element child : table.getChildElements())
				{
					if (child instanceof Text)
					{
						result.add(new KeyValue(child.getUniqueId(), resources.getMessage("label.TableText", null, "Table Text", locale)));
					}
				}				
			}
			
			if (element instanceof RatingQuestion)
			{
				RatingQuestion rating = (RatingQuestion)element;
				for (Element child : rating.getChildElements())
				{
					if (child instanceof Text)
					{
						result.add(new KeyValue(child.getUniqueId(), resources.getMessage("label.Text", null, "Text", locale)));
					}
				}				
			}
		}
		
		return result;
	}
	
	private static String getLabel(Element element, String suffix, Map<String, String> translationByKey)
	{
		try
		{
			String label = ""; 
			if (translationByKey.containsKey(element.getUniqueId() + suffix))
			{
				label = translationByKey.get(element.getUniqueId() + suffix);
			} else if (translationByKey.containsKey(element.getId().toString() + suffix))
			{
				label = translationByKey.get(element.getId().toString() + suffix);
			}
			return label != null ? label : "";
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			return "";
		}
	}
	
	private static org.w3c.dom.Element getElementNode(Element element, Document doc, HashMap<String, String> translationByKey)
	{
		org.w3c.dom.Element elementNode = doc.createElement("Element");
		
		Attr attr = doc.createAttribute("type");
		attr.setValue(element.getType());
		elementNode.setAttributeNode(attr);
		
		attr = doc.createAttribute("key");
		attr.setValue(element.getUniqueId());
		elementNode.setAttributeNode(attr);
		
		if (element instanceof Table)
		{
			attr = doc.createAttribute("cols");
			attr.setValue(((Table)element).getColumns() + "");
			elementNode.setAttributeNode(attr);
		}
		
		if (element instanceof Matrix)
		{
			attr = doc.createAttribute("cols");
			attr.setValue(((Matrix)element).getColumns() + "");
			elementNode.setAttributeNode(attr);
		}
		
		String label = getLabel(element, "", translationByKey);
		
		org.w3c.dom.Element labelNode = doc.createElement("Label");
		labelNode.appendChild(doc.createCDATASection(label));					
		elementNode.appendChild(labelNode);
		
		if (element instanceof Question)
		{
			Question question = (Question) element;
			if (question.getHelp() != null && question.getHelp().length() > 0)
			{
				String help =  getLabel(element, "help", translationByKey);

				org.w3c.dom.Element helpNode = doc.createElement("Help");
				helpNode.appendChild(doc.createCDATASection(help));					
				elementNode.appendChild(helpNode);
			}
			
			if (question.getScoringItems() != null)
			{
				for (ScoringItem scoringItem: question.getScoringItems())
				{
					label = translationByKey.get(scoringItem.getUniqueId() + Question.FEEDBACK); 
					
					if (notNullOrEmpty(label))
					{
						org.w3c.dom.Element feedbackNode = doc.createElement("Feedback");
						feedbackNode.setAttribute("key", scoringItem.getUniqueId());
						feedbackNode.appendChild(doc.createCDATASection(label));					
						elementNode.appendChild(feedbackNode);
					}
				}
			}
		}
		
		if (element instanceof Section)
		{
			Section section = (Section) element;
			org.w3c.dom.Element tabtitleNode = doc.createElement("TabTitle");
			label =  getLabel(section, Section.TABTITLE, translationByKey); 
			
			tabtitleNode.appendChild(doc.createCDATASection(label));
			elementNode.appendChild(tabtitleNode);
		}
		
		if (element instanceof NumberQuestion)
		{
			NumberQuestion number = (NumberQuestion) element;
			org.w3c.dom.Element unitNode = doc.createElement("Unit");
			label =  getLabel(number, NumberQuestion.UNIT, translationByKey); 
			
			unitNode.appendChild(doc.createCDATASection(label));
			elementNode.appendChild(unitNode);
		}
		
		if (element instanceof Confirmation)
		{
			Confirmation confirmation = (Confirmation) element;
			org.w3c.dom.Element textNode = doc.createElement("ConfirmationText");
			label =  getLabel(confirmation, Confirmation.TEXT, translationByKey); 
			
			textNode.appendChild(doc.createCDATASection(label));
			elementNode.appendChild(textNode);
			
			org.w3c.dom.Element clabelNode = doc.createElement("ConfirmationLabel");
			label =  getLabel(confirmation, Confirmation.LABEL, translationByKey); 
			
			clabelNode.appendChild(doc.createCDATASection(label));
			elementNode.appendChild(clabelNode);
		}
					
		if (element instanceof ChoiceQuestion)
		{
			ChoiceQuestion choice = (ChoiceQuestion) element;
			for (PossibleAnswer answer : choice.getPossibleAnswers())
			{
				org.w3c.dom.Element answerNode = doc.createElement("Answer");
				
				attr = doc.createAttribute("key");
				attr.setValue(answer.getUniqueId());
				answerNode.setAttributeNode(attr);
				
				label =  getLabel(answer, "", translationByKey);
				labelNode = doc.createElement("Label");				
				labelNode.appendChild(doc.createCDATASection(label));									
				answerNode.appendChild(labelNode);
				
				label =  getLabel(answer, Question.FEEDBACK, translationByKey);
				org.w3c.dom.Element feedbackNode = doc.createElement("Feedback");				
				feedbackNode.appendChild(doc.createCDATASection(label));									
				answerNode.appendChild(feedbackNode);
				
				elementNode.appendChild(answerNode);
			}
		}
			
		if (element instanceof MatrixOrTable)
		{
			
			MatrixOrTable matrix = (MatrixOrTable)element;
			org.w3c.dom.Element childrenElement = doc.createElement("Children");
			
			for (Element child: matrix.getChildElements())
			{
				if (!(child instanceof EmptyElement))
				{
					childrenElement.appendChild(getElementNode(child, doc, translationByKey));
				}
			}
			elementNode.appendChild(childrenElement);
		}
		
		if (element instanceof RatingQuestion)
		{
			
			RatingQuestion rating = (RatingQuestion)element;
			org.w3c.dom.Element childrenElement = doc.createElement("Children");
			
			for (Element child: rating.getChildElements())
			{
				if (!(child instanceof EmptyElement))
				{
					childrenElement.appendChild(getElementNode(child, doc, translationByKey));
				}
			}
			elementNode.appendChild(childrenElement);
		}
		
		return elementNode;
	}

	public static java.io.File getXML(Survey survey, Translations translations, String xsllink, FileService fileService) {
		try {
			 
			HashMap<String, String> translationByKey = new HashMap<>();
			for (Translation translation: translations.getTranslations())
			{
				if (translation.getLabel() != null)
				translationByKey.put(translation.getKey(), translation.getLabel());
			}
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			
			doc.setXmlStandalone(true);
			ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"" + xsllink + "\"");
			
			org.w3c.dom.Element rootElement = doc.createElement("SurveyTranslation");
			doc.appendChild(rootElement);		
			
			doc.insertBefore(pi, rootElement);
			
			org.w3c.dom.Element surveyId = doc.createElement("Survey");
			surveyId.appendChild(doc.createTextNode(survey.getUniqueId()));
			rootElement.appendChild(surveyId);
			
			org.w3c.dom.Element surveyTitle = doc.createElement("Title");
			String title = ""; 
			if (translationByKey.containsKey(Survey.TITLE)) title = translationByKey.get(Survey.TITLE);
			if (title == null) title = "";
			surveyTitle.appendChild(doc.createCDATASection(title));
			rootElement.appendChild(surveyTitle);
			
			org.w3c.dom.Element surveyQuizWelcome = doc.createElement("QuizWelcomePage");
			String quizwelcome = ""; 
			if (translationByKey.containsKey(Survey.QUIZWELCOMEMESSAGE))
			{
				quizwelcome = translationByKey.get(Survey.QUIZWELCOMEMESSAGE);
				if (quizwelcome == null) quizwelcome = "";
				surveyQuizWelcome.appendChild(doc.createCDATASection(quizwelcome));
				rootElement.appendChild(surveyQuizWelcome);
			}
			
			org.w3c.dom.Element surveyQuizResult = doc.createElement("QuizResultPage");
			String quizresult = ""; 
			if (translationByKey.containsKey(Survey.QUIZRESULTSMESSAGE))
			{
				quizresult = translationByKey.get(Survey.QUIZRESULTSMESSAGE);
				if (quizresult == null) quizresult = "";
				surveyQuizResult.appendChild(doc.createCDATASection(quizresult));
				rootElement.appendChild(surveyQuizResult);
			}
			
			org.w3c.dom.Element surveyEscape = doc.createElement("EscapePage");
			String escape = "";
			if (translationByKey.containsKey(Survey.ESCAPEPAGE)) escape = translationByKey.get(Survey.ESCAPEPAGE);
			if (escape == null) escape = "";
			surveyEscape.appendChild(doc.createCDATASection(escape));
			rootElement.appendChild(surveyEscape);
			
			org.w3c.dom.Element surveyEscapeLink = doc.createElement("EscapeLink");
			escape = "";
			if (translationByKey.containsKey(Survey.ESCAPELINK)) escape = translationByKey.get(Survey.ESCAPELINK);
			if (escape == null) escape = "";
			surveyEscapeLink.appendChild(doc.createCDATASection(escape));
			rootElement.appendChild(surveyEscapeLink);
			
			org.w3c.dom.Element surveyConfirmation = doc.createElement("ConfirmationPage");
			String confirm = "";
			if (translationByKey.containsKey(Survey.CONFIRMATIONPAGE)) confirm = translationByKey.get(Survey.CONFIRMATIONPAGE);
			if (confirm == null) confirm = "";
			surveyConfirmation.appendChild(doc.createCDATASection(confirm));
			rootElement.appendChild(surveyConfirmation);
			
			org.w3c.dom.Element surveyConfirmationLink = doc.createElement("ConfirmationLink");
			confirm = "";
			if (translationByKey.containsKey(Survey.CONFIRMATIONLINK)) confirm = translationByKey.get(Survey.CONFIRMATIONLINK);
			if (confirm == null) confirm = "";
			surveyConfirmationLink.appendChild(doc.createCDATASection(confirm));
			rootElement.appendChild(surveyConfirmationLink);
			
			for (String key : survey.getUsefulLinks().keySet())
			{
				String[] data = key.split("#");
				String newkey = data[0] + "#usefullink";
				org.w3c.dom.Element usefulLink = doc.createElement("UsefulLink");
				String label = "";
				if (translationByKey.containsKey(newkey)) label = translationByKey.get(newkey);
				if (label == null) label = "";
				usefulLink.setAttribute("key", newkey);
				usefulLink.appendChild(doc.createCDATASection(label));
				rootElement.appendChild(usefulLink);
			}
			
			for (String key : survey.getBackgroundDocuments().keySet())
			{
				String newkey = key + "#backgrounddocument";
				org.w3c.dom.Element backgroundDocument = doc.createElement("BackgroundDocument");
				String label = "";
				if (translationByKey.containsKey(newkey)) label = translationByKey.get(newkey);
				if (label == null) label = "";
				backgroundDocument.setAttribute("key", newkey);
				backgroundDocument.appendChild(doc.createCDATASection(label));
				rootElement.appendChild(backgroundDocument);
			}
			
			org.w3c.dom.Element lang = doc.createElement("Lang");
			lang.appendChild(doc.createTextNode(translations.getLanguage().getCode()));
			rootElement.appendChild(lang);
			
			for (Element element: survey.getElements())
			{	
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
    		String xml = writer.toString().replace("<![CDATA[","").replace("]]>","");
    		
    		OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(temp),"UTF-8");
    		
     	    bw.write(xml);
     	    bw.close();
 
    	    return temp;
 
    	} catch(Exception e){
    		logger.error(e.getLocalizedMessage(), e); 
    	}
		
		return null; 
	}
	

	public static java.io.File getXLS(Survey survey, Translations translations, MessageSource resources, Locale locale, FileService fileService) {
		
		try {
			
			HashMap<String, String> translationsByKey = new HashMap<>();
			for (Translation translation: translations.getTranslations())
			{
				if (translation.getLabel() != null)
				translationsByKey.put(translation.getKey(), translation.getLabel());
			}
			
			List<KeyValue> infos =  getShortDescriptions(survey, resources, locale);
			Map<String, String> descriptions = new HashMap<>();
			for (KeyValue keyValue : infos) {
				descriptions.put(keyValue.getKey(), keyValue.getValue());
			}
			
			Workbook wb = new HSSFWorkbook();
			String safeName = WorkbookUtil.createSafeSheetName("Translation");
			Sheet sheet = wb.createSheet(safeName);
	
			// Create a row and put some cells in it. Rows are 0 based.
			int rowIndex = 0;
			Row row = sheet.createRow(rowIndex++);
			addTextCell(row, 0, survey.getUniqueId());
			addTextCell(row, 1, translations.getLanguage().getCode());
			
			row = sheet.createRow(rowIndex++);
			addTextCell(row, 0, Survey.TITLE);
			addTextCell(row, 1, descriptions.get(Survey.TITLE));
			addTextCell(row, 2, translationsByKey.get(Survey.TITLE) != null ? translationsByKey.get(Survey.TITLE) : "");
						
			if (translationsByKey.get(Survey.QUIZWELCOMEMESSAGE) != null && translationsByKey.get(Survey.QUIZWELCOMEMESSAGE).length() > 0)
			{		
				row = sheet.createRow(rowIndex++);
				addTextCell(row, 0, Survey.QUIZWELCOMEMESSAGE);
				addTextCell(row, 1, descriptions.get(Survey.QUIZWELCOMEMESSAGE));
				addTextCell(row, 2, translationsByKey.get(Survey.QUIZWELCOMEMESSAGE) != null ? translationsByKey.get(Survey.QUIZWELCOMEMESSAGE) : "");
			}
			
			if (translationsByKey.get(Survey.QUIZRESULTSMESSAGE) != null && translationsByKey.get(Survey.QUIZRESULTSMESSAGE).length() > 0)
			{		
				row = sheet.createRow(rowIndex++);
				addTextCell(row, 0, Survey.QUIZRESULTSMESSAGE);
				addTextCell(row, 1, descriptions.get(Survey.QUIZRESULTSMESSAGE));
				addTextCell(row, 2, translationsByKey.get(Survey.QUIZRESULTSMESSAGE) != null ? translationsByKey.get(Survey.QUIZRESULTSMESSAGE) : "");
			}			
			
			if (translationsByKey.get(Survey.ESCAPEPAGE) != null && translationsByKey.get(Survey.ESCAPEPAGE).length() > 0)
			{		
				row = sheet.createRow(rowIndex++);
				addTextCell(row, 0, Survey.ESCAPEPAGE);
				addTextCell(row, 1, descriptions.get(Survey.ESCAPEPAGE));
				addTextCell(row, 2, translationsByKey.get(Survey.ESCAPEPAGE) != null ? translationsByKey.get(Survey.ESCAPEPAGE) : "");
			}
			
			if (translationsByKey.get(Survey.ESCAPELINK) != null && translationsByKey.get(Survey.ESCAPELINK).length() > 0)
			{
				row = sheet.createRow(rowIndex++);
				addTextCell(row, 0, Survey.ESCAPELINK);
				addTextCell(row, 1, descriptions.get(Survey.ESCAPELINK));
				addTextCell(row, 2, translationsByKey.get(Survey.ESCAPELINK) != null ? translationsByKey.get(Survey.ESCAPELINK) : "");
			}
			
			if (translationsByKey.get(Survey.CONFIRMATIONPAGE) != null && translationsByKey.get(Survey.CONFIRMATIONPAGE).length() > 0)
			{
				row = sheet.createRow(rowIndex++);
				addTextCell(row, 0, Survey.CONFIRMATIONPAGE);
				addTextCell(row, 1, descriptions.get(Survey.CONFIRMATIONPAGE));
				addTextCell(row, 2, translationsByKey.get(Survey.CONFIRMATIONPAGE) != null ? translationsByKey.get(Survey.CONFIRMATIONPAGE) : "");
			}
			
			if (translationsByKey.get(Survey.CONFIRMATIONLINK) != null && translationsByKey.get(Survey.CONFIRMATIONLINK).length() > 0)
			{
				row = sheet.createRow(rowIndex++);
				addTextCell(row, 0, Survey.CONFIRMATIONLINK);
				addTextCell(row, 1, descriptions.get(Survey.CONFIRMATIONLINK));
				addTextCell(row, 2, translationsByKey.get(Survey.CONFIRMATIONLINK) != null ? translationsByKey.get(Survey.CONFIRMATIONLINK) : "");
			}
			
			for (String key : survey.getUsefulLinks().keySet())
			{
				row = sheet.createRow(rowIndex++);
				String[] data = key.split("#");
				String newkey = data[0] + "#usefullink";
				addTextCell(row, 0, newkey);
				addTextCell(row, 1, descriptions.get(newkey));
				addTextCell(row, 2, translationsByKey.get(newkey) != null ? translationsByKey.get(newkey) : "");
			}
			
			for (String key : survey.getBackgroundDocuments().keySet())
			{
				row = sheet.createRow(rowIndex++);
				String newkey = key + "#backgrounddocument";
				addTextCell(row, 0, newkey);
				addTextCell(row, 1, descriptions.get(newkey));
				addTextCell(row, 2, translationsByKey.get(newkey) != null ? translationsByKey.get(newkey) : "");
			}
			
			for (Element element: survey.getElements())
			{
				String label = getLabel(element, "",translationsByKey); 
				
				if (notNullOrEmpty(label))
				{
					row = sheet.createRow(rowIndex++);
					addTextCell(row, 0, element.getUniqueId());		
					addTextCell(row, 1, descriptions.get(element.getUniqueId()));
					addTextCell(row, 2, label);
				}
				
				if (element instanceof Section)
				{
					Section section = (Section) element;
					label = getLabel(section, Section.TABTITLE,translationsByKey); 
					if (notNullOrEmpty(label))
					{					
						row = sheet.createRow(rowIndex++);
						addTextCell(row, 0, section.getUniqueId());
						addTextCell(row, 1, descriptions.get(section.getUniqueId() + Section.TABTITLE));
						addTextCell(row, 2, label);
					}
				}
			
				if (element instanceof NumberQuestion)
				{
					NumberQuestion number = (NumberQuestion) element;
					label = getLabel(number, NumberQuestion.UNIT,translationsByKey); 
					if (notNullOrEmpty(label))
					{					
						row = sheet.createRow(rowIndex++);
						addTextCell(row, 0, number.getUniqueId());
						addTextCell(row, 1, descriptions.get(number.getUniqueId() + NumberQuestion.UNIT));
						addTextCell(row, 2, label);
					}
				}
				
				if (element instanceof Confirmation)
				{
					Confirmation confirmation = (Confirmation) element;
					label = getLabel(confirmation, Confirmation.TEXT,translationsByKey); 
					if (notNullOrEmpty(label))
					{					
						row = sheet.createRow(rowIndex++);
						addTextCell(row, 0, confirmation.getUniqueId());
						addTextCell(row, 1, descriptions.get(confirmation.getUniqueId() + Confirmation.TEXT));
						addTextCell(row, 2, label);
					}
					label = getLabel(confirmation, Confirmation.LABEL,translationsByKey); 
					if (notNullOrEmpty(label))
					{					
						row = sheet.createRow(rowIndex++);
						addTextCell(row, 0, confirmation.getUniqueId());
						addTextCell(row, 1, descriptions.get(confirmation.getUniqueId() + Confirmation.LABEL));
						addTextCell(row, 2, label);
					}
				}
				
				if (element instanceof ChoiceQuestion)
				{
					ChoiceQuestion choice = (ChoiceQuestion) element;
					for (PossibleAnswer answer : choice.getPossibleAnswers())
					{
						label = getLabel(answer, "",translationsByKey); 
						if (notNullOrEmpty(label))
						{						
							row = sheet.createRow(rowIndex++);
							addTextCell(row, 0, answer.getUniqueId());
							addTextCell(row, 1, descriptions.get(answer.getUniqueId()));
							addTextCell(row, 2, label);
						}
						
						label = getLabel(answer, Question.FEEDBACK,translationsByKey); 
						if (notNullOrEmpty(label))
						{						
							row = sheet.createRow(rowIndex++);
							addTextCell(row, 0, answer.getUniqueId());
							addTextCell(row, 1, descriptions.get(answer.getUniqueId() + Question.FEEDBACK));
							addTextCell(row, 2, label);
						}
					}
				}		
				
				if (element instanceof Question)
				{
					Question question = (Question)element;
					if (question.getHelp() != null && question.getHelp().length() > 0)
					{
						label = getLabel(question, "help",translationsByKey); 
						if (notNullOrEmpty(label))
						{
							row = sheet.createRow(rowIndex++);
							addTextCell(row, 0, question.getUniqueId());
							addTextCell(row, 1, descriptions.get(question.getUniqueId() + "help"));
							addTextCell(row, 2, label);
						}
					}
					
					if (question.getScoringItems() != null)
					{
						for (ScoringItem scoringItem: question.getScoringItems())
						{
							label = translationsByKey.get(scoringItem.getUniqueId() + Question.FEEDBACK); 
							if (notNullOrEmpty(label))
							{
								row = sheet.createRow(rowIndex++);
								addTextCell(row, 0, scoringItem.getUniqueId());
								addTextCell(row, 1, descriptions.get(scoringItem.getUniqueId() + Question.FEEDBACK));
								addTextCell(row, 2, label);
							}
						}
					}
				}	
				
				if (element instanceof MatrixOrTable)
				{
					MatrixOrTable matrix = (MatrixOrTable)element;
					for (Element child: matrix.getChildElements())
					{
						if (!(child instanceof EmptyElement))
						{
							label = getLabel(child, "",translationsByKey); 
							if (notNullOrEmpty(label))
							{
								row = sheet.createRow(rowIndex++);
								addTextCell(row, 0, child.getUniqueId());	
								addTextCell(row, 1, descriptions.get(child.getUniqueId()));
								addTextCell(row, 2, label);
							}
						}
					}
				}
				
				if (element instanceof RatingQuestion)
				{
					RatingQuestion rating = (RatingQuestion)element;
					for (Element child: rating.getChildElements())
					{
						if (!(child instanceof EmptyElement))
						{
							label = getLabel(child, "",translationsByKey); 
							if (notNullOrEmpty(label))
							{
								row = sheet.createRow(rowIndex++);
								addTextCell(row, 0, child.getUniqueId());	
								addTextCell(row, 1, descriptions.get(child.getUniqueId()));
								addTextCell(row, 2, label);
							}
						}
					}
				}
				
			}
			
			java.io.File temp = fileService.createTempFile("translation" + UUID.randomUUID().toString(), ".xls"); 
	  	   
			FileOutputStream out = new FileOutputStream(temp);
			
			wb.write(out);
			wb.close();
			out.close();
	
	 	    return temp;

		} catch(Exception e){
    		logger.error(e.getLocalizedMessage(), e); 
    	}
		
		return null; 
		
	}
	
	public static java.io.File getODS(Survey survey, Translations translations, MessageSource resources, Locale locale, FileService fileService) {
		try {

			HashMap<String, String> translationsByKey = new HashMap<>();
			for (Translation translation: translations.getTranslations())
			{
				if (translation.getLabel() != null)
				translationsByKey.put(translation.getKey(), translation.getLabel());
			}
			
			List<KeyValue> infos =  getShortDescriptions(survey, resources, locale);
			Map<String, String> descriptions = new HashMap<>();
			for (KeyValue keyValue : infos) {
				descriptions.put(keyValue.getKey(), keyValue.getValue());
			}
			
			SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
		    org.odftoolkit.simple.table.Table sheet = spreadsheet.getSheetByIndex(0);
		    sheet.setTableName("Translation");
	
			// Create a row and put some cells in it. Rows are 0 based.
			int rowIndex = 0;
			org.odftoolkit.simple.table.Cell cell;
			
			cell = sheet.getCellByPosition(0, rowIndex);
			cell.setStringValue(survey.getUniqueId());
			cell = sheet.getCellByPosition(1, rowIndex++);
			cell.setStringValue(translations.getLanguage().getCode());
			
			String label = translationsByKey.get(Survey.TITLE) != null ? translationsByKey.get(Survey.TITLE) : "";
			if (notNullOrEmpty(label))
			{
				cell = sheet.getCellByPosition(0, rowIndex);
				cell.setStringValue(Survey.TITLE);
				cell = sheet.getCellByPosition(1, rowIndex);
				cell.setStringValue(descriptions.get(Survey.TITLE));		
				cell = sheet.getCellByPosition(2, rowIndex++);
				cell.setStringValue(label);
			}
			
			label = translationsByKey.get(Survey.QUIZWELCOMEMESSAGE) != null ? translationsByKey.get(Survey.QUIZWELCOMEMESSAGE) : "";
			if (notNullOrEmpty(label))
			{
				cell = sheet.getCellByPosition(0, rowIndex);
				cell.setStringValue(Survey.QUIZWELCOMEMESSAGE);
				cell = sheet.getCellByPosition(1, rowIndex);
				cell.setStringValue(descriptions.get(Survey.QUIZWELCOMEMESSAGE));		
				cell = sheet.getCellByPosition(2, rowIndex++);
				cell.setStringValue(label);
			}
			
			label = translationsByKey.get(Survey.QUIZRESULTSMESSAGE) != null ? translationsByKey.get(Survey.QUIZRESULTSMESSAGE) : "";
			if (notNullOrEmpty(label))
			{
				cell = sheet.getCellByPosition(0, rowIndex);
				cell.setStringValue(Survey.QUIZRESULTSMESSAGE);
				cell = sheet.getCellByPosition(1, rowIndex);
				cell.setStringValue(descriptions.get(Survey.QUIZRESULTSMESSAGE));		
				cell = sheet.getCellByPosition(2, rowIndex++);
				cell.setStringValue(label);
			}
				
			label = translationsByKey.get(Survey.ESCAPEPAGE) != null ? translationsByKey.get(Survey.ESCAPEPAGE) : "";
			if (notNullOrEmpty(label))
			{
				cell = sheet.getCellByPosition(0, rowIndex);
				cell.setStringValue(Survey.ESCAPEPAGE);
				cell = sheet.getCellByPosition(1, rowIndex);
				cell.setStringValue(descriptions.get(Survey.ESCAPEPAGE));		
				cell = sheet.getCellByPosition(2, rowIndex++);
				cell.setStringValue(label);
			}
			
			label = translationsByKey.get(Survey.ESCAPELINK) != null ? translationsByKey.get(Survey.ESCAPELINK) : "";
			if (notNullOrEmpty(label))
			{
				cell = sheet.getCellByPosition(0, rowIndex);
				cell.setStringValue(Survey.ESCAPELINK);
				cell = sheet.getCellByPosition(1, rowIndex);
				cell.setStringValue(descriptions.get(Survey.ESCAPELINK));		
				cell = sheet.getCellByPosition(2, rowIndex++);
				cell.setStringValue(label);
			}
			
			label = translationsByKey.get(Survey.CONFIRMATIONPAGE) != null ? translationsByKey.get(Survey.CONFIRMATIONPAGE) : "";
			if (notNullOrEmpty(label))
			{
				cell = sheet.getCellByPosition(0, rowIndex);			
				cell.setStringValue(Survey.CONFIRMATIONPAGE);
				cell = sheet.getCellByPosition(1, rowIndex);
				cell.setStringValue(descriptions.get(Survey.CONFIRMATIONPAGE));		
				cell = sheet.getCellByPosition(2, rowIndex++);
				cell.setStringValue(label);
			}
			
			label = translationsByKey.get(Survey.CONFIRMATIONLINK) != null ? translationsByKey.get(Survey.CONFIRMATIONLINK) : "";
			if (notNullOrEmpty(label))
			{
				cell = sheet.getCellByPosition(0, rowIndex);
				cell.setStringValue(Survey.CONFIRMATIONLINK);
				cell = sheet.getCellByPosition(1, rowIndex);
				cell.setStringValue(descriptions.get(Survey.CONFIRMATIONLINK));		
				cell = sheet.getCellByPosition(2, rowIndex++);
				cell.setStringValue(label);
			}
			
			for (String key : survey.getUsefulLinks().keySet())
			{
				String[] data = key.split("#");
				String newkey = data[0] + "#usefullink";
				cell = sheet.getCellByPosition(0, rowIndex);
				cell.setStringValue(newkey);
				cell = sheet.getCellByPosition(1, rowIndex);
				cell.setStringValue(descriptions.get(newkey));		
				cell = sheet.getCellByPosition(2, rowIndex++);
				cell.setStringValue(translationsByKey.get(newkey) != null ? translationsByKey.get(newkey) : "");
			}
			
			for (String key : survey.getBackgroundDocuments().keySet())
			{
				String newkey = key + "#backgrounddocument";
				cell = sheet.getCellByPosition(0, rowIndex);
				cell.setStringValue(newkey);
				cell = sheet.getCellByPosition(1, rowIndex);
				cell.setStringValue(descriptions.get(newkey));		
				cell = sheet.getCellByPosition(2, rowIndex++);
				cell.setStringValue(translationsByKey.get(newkey) != null ? translationsByKey.get(newkey) : "");
			}
			
			for (Element element: survey.getElements())
			{
				label = getLabel(element, "",translationsByKey); 
				if (notNullOrEmpty(label))
				{
					cell = sheet.getCellByPosition(0, rowIndex);
					cell.setStringValue(element.getUniqueId());
					cell = sheet.getCellByPosition(1, rowIndex);
					cell.setStringValue(descriptions.get(element.getUniqueId()));		
					cell = sheet.getCellByPosition(2, rowIndex++);
					cell.setStringValue(label);
				}
				
				if (element instanceof Section)
				{
					Section section = (Section) element;
					label = getLabel(section, Section.TABTITLE,translationsByKey); 
					if (notNullOrEmpty(label))
					{
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue(section.getUniqueId());
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.setStringValue(descriptions.get(section.getUniqueId() + Section.TABTITLE));	
						cell = sheet.getCellByPosition(2, rowIndex++);
						cell.setStringValue(label);					
					}
				}
							
				if (element instanceof NumberQuestion)
				{
					NumberQuestion number = (NumberQuestion) element;
					label = getLabel(number, NumberQuestion.UNIT,translationsByKey); 
					if (notNullOrEmpty(label))
					{
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue(number.getUniqueId());
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.setStringValue(descriptions.get(number.getUniqueId() + NumberQuestion.UNIT));	
						cell = sheet.getCellByPosition(2, rowIndex++);
						cell.setStringValue(label);					
					}
				}
				
				if (element instanceof Confirmation)
				{
					Confirmation confirmation = (Confirmation) element;
					label = getLabel(confirmation, Confirmation.TEXT,translationsByKey); 
					if (notNullOrEmpty(label))
					{
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue(confirmation.getUniqueId());
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.setStringValue(descriptions.get(confirmation.getUniqueId() + Confirmation.TEXT));	
						cell = sheet.getCellByPosition(2, rowIndex++);
						cell.setStringValue(label);					
					}
					label = getLabel(confirmation, Confirmation.LABEL,translationsByKey); 
					if (notNullOrEmpty(label))
					{
						cell = sheet.getCellByPosition(0, rowIndex);
						cell.setStringValue(confirmation.getUniqueId());
						cell = sheet.getCellByPosition(1, rowIndex);
						cell.setStringValue(descriptions.get(confirmation.getUniqueId() + Confirmation.LABEL));	
						cell = sheet.getCellByPosition(2, rowIndex++);
						cell.setStringValue(label);					
					}
				}
				
				if (element instanceof ChoiceQuestion)
				{
					ChoiceQuestion choice = (ChoiceQuestion) element;
					for (PossibleAnswer answer : choice.getPossibleAnswers())
					{
						label = getLabel(answer, "",translationsByKey); 
						if (notNullOrEmpty(label))
						{
							cell = sheet.getCellByPosition(0, rowIndex);
							cell.setStringValue(answer.getUniqueId());
							cell = sheet.getCellByPosition(1, rowIndex);
							cell.setStringValue(descriptions.get(answer.getUniqueId()));	
							cell = sheet.getCellByPosition(2, rowIndex++);
							cell.setStringValue(label);
						}
						
						label = getLabel(answer, Question.FEEDBACK,translationsByKey); 
						if (notNullOrEmpty(label))
						{
							cell = sheet.getCellByPosition(0, rowIndex);
							cell.setStringValue(answer.getUniqueId());
							cell = sheet.getCellByPosition(1, rowIndex);
							cell.setStringValue(descriptions.get(answer.getUniqueId() + Question.FEEDBACK));	
							cell = sheet.getCellByPosition(2, rowIndex++);
							cell.setStringValue(label);
						}
					}
				}		
				
				if (element instanceof Question)
				{
					Question question = (Question)element;
					if (question.getHelp() != null && question.getHelp().length() > 0)
					{
						label = getLabel(question, "help",translationsByKey); 
						if (notNullOrEmpty(label))
						{
							cell = sheet.getCellByPosition(0, rowIndex);
							cell.setStringValue(question.getUniqueId());
							cell = sheet.getCellByPosition(1, rowIndex);
							cell.setStringValue(descriptions.get(question.getUniqueId() + "help"));	
							cell = sheet.getCellByPosition(2, rowIndex++);
							cell.setStringValue(label);
						}
					}
					
					if (question.getScoringItems() != null)
					{
						for (ScoringItem scoringItem: question.getScoringItems())
						{
							label = translationsByKey.get(scoringItem.getUniqueId() + Question.FEEDBACK); 
							if (notNullOrEmpty(label))
							{
								cell = sheet.getCellByPosition(0, rowIndex);
								cell.setStringValue(scoringItem.getUniqueId());
								cell = sheet.getCellByPosition(1, rowIndex);
								cell.setStringValue(descriptions.get(scoringItem.getUniqueId() + Question.FEEDBACK));	
								cell = sheet.getCellByPosition(2, rowIndex++);
								cell.setStringValue(label);
							}
						}
					}
				}
				
				if (element instanceof MatrixOrTable)
				{
					MatrixOrTable matrix = (MatrixOrTable)element;
					for (Element child: matrix.getChildElements())
					{
						if (!(child instanceof EmptyElement))
						{
							label = getLabel(child, "",translationsByKey); 
							if (notNullOrEmpty(label))
							{
								cell = sheet.getCellByPosition(0, rowIndex);
								cell.setStringValue(child.getUniqueId());
								cell = sheet.getCellByPosition(1, rowIndex);
								cell.setStringValue(descriptions.get(child.getUniqueId()));	
								cell = sheet.getCellByPosition(2, rowIndex++);
								cell.setStringValue(label);
							}
						}
					}
				}
				
				if (element instanceof RatingQuestion)
				{
					RatingQuestion rating = (RatingQuestion)element;
					for (Element child: rating.getChildElements())
					{
						if (!(child instanceof EmptyElement))
						{
							label = getLabel(child, "",translationsByKey); 
							if (notNullOrEmpty(label))
							{
								cell = sheet.getCellByPosition(0, rowIndex);
								cell.setStringValue(child.getUniqueId());
								cell = sheet.getCellByPosition(1, rowIndex);
								cell.setStringValue(descriptions.get(child.getUniqueId()));	
								cell = sheet.getCellByPosition(2, rowIndex++);
								cell.setStringValue(label);
							}
						}
					}
				}
			}
			
			java.io.File temp = fileService.createTempFile("translation" + UUID.randomUUID().toString(), ".ods"); 
	  	   
			FileOutputStream out = new FileOutputStream(temp);
			
			spreadsheet.save(out);
			
			out.close();
	
	 	    return temp;

		} catch(Exception e){
    		logger.error(e.getLocalizedMessage(), e); 
    	}
		
		return null; 
	}
	
	private static void addTextCell(Row row, int col, String text)
	{
		Cell cell = row.createCell(col);
		cell.setCellValue(text);
		cell.setCellType(Cell.CELL_TYPE_STRING);
	}

	private static HashMap<String, String> getTypeSuffixMapping()
	{
		HashMap<String, String> typeSuffixByShortType = new HashMap<>();
		
		typeSuffixByShortType.put("T", "TABTITLE");
		typeSuffixByShortType.put("U", "UNIT");
		typeSuffixByShortType.put("H", "help");
		typeSuffixByShortType.put("CT", "CONFIRMATIONTEXT");
		typeSuffixByShortType.put("CL", "CONFIRMATIONLABEL");
		typeSuffixByShortType.put("F", "FEEDBACK");
		
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
			} catch (NumberFormatException e)
			{
				result.setSurveyUid(surveyIdString);
				
	    		Survey survey = surveyService.getSurveyByUniqueId(surveyIdString, false, true);
	    		surveyId = survey.getId();
	    		result.setSurveyId(surveyId);
			}
			
			String lang = getText(translation.getElementsByTagName("Lang"), "Lang");
			
			Language objLang = surveyService.getLanguage(lang);
			if (objLang == null)
			{
				objLang = new Language(lang,lang,lang,false);
				surveyService.save(objLang);
			}
			
			result.setLanguage(objLang);
			
			String title = getText(translation.getElementsByTagName("Title"), "Title");
			result.setTitle(title);
			result.getTranslations().add(new Translation(Survey.TITLE, title, lang,surveyId, result));
			
			try {
				String quizwelcome = getText(translation.getElementsByTagName("QuizWelcomePage"), "QuizWelcomePage");
				result.getTranslations().add(new Translation(Survey.QUIZWELCOMEMESSAGE, quizwelcome, lang, surveyId, result));
			} catch (Exception e) {}
			
			try {
				String quizresult = getText(translation.getElementsByTagName("QuizResultPage"), "QuizResultPage");
				result.getTranslations().add(new Translation(Survey.QUIZRESULTSMESSAGE, quizresult, lang, surveyId, result));
			} catch (Exception e) {}			
			
			String escape = getText(translation.getElementsByTagName("EscapePage"), "EscapePage");
			result.getTranslations().add(new Translation(Survey.ESCAPEPAGE, escape, lang, surveyId, result));
			
			try {
				escape = getText(translation.getElementsByTagName("EscapeLink"), "EscapeLink");
				result.getTranslations().add(new Translation(Survey.ESCAPELINK, escape, lang, surveyId, result));
			} catch (Exception e) {}
			
			String confirmation = getText(translation.getElementsByTagName("ConfirmationPage"), "ConfirmationPage");
			result.getTranslations().add(new Translation(Survey.CONFIRMATIONPAGE, confirmation, lang, surveyId, result));
			
			try {
				escape = getText(translation.getElementsByTagName("ConfirmationLink"), "ConfirmationLink");
				result.getTranslations().add(new Translation(Survey.CONFIRMATIONLINK, escape, lang, surveyId, result));
			} catch (Exception e) {}
			
			for (int i = 0; i < translation.getElementsByTagName("Element").getLength(); i++)
			{
				org.w3c.dom.Element element = (org.w3c.dom.Element) translation.getElementsByTagName("Element").item(i);
				
				String key = Tools.repairXML(element.getAttribute("key"));
				String type = Tools.repairXML(element.getAttribute("type"));
				String label = getText(element.getElementsByTagName("Label"), "Label");
				
				result.getTranslations().add(new Translation(key, label, lang, surveyId, result));
												
				String help = getText(element.getElementsByTagName("Help"), "Help");
				if (help != null)
					result.getTranslations().add(new Translation(key + "help", help, lang, surveyId, result));
				
				if (type.contains("Section"))
				{
					String tabTitle = getText(element.getElementsByTagName("TabTitle"), "TabTitle");
					if (tabTitle != null)
						result.getTranslations().add(new Translation(key + Section.TABTITLE, tabTitle, lang, surveyId, result));
				}
				
				if (type.contains("NumberQuestion"))
				{
					String unit = getText(element.getElementsByTagName("Unit"), "Unit");
					result.getTranslations().add(new Translation(key + NumberQuestion.UNIT, unit, lang, surveyId, result));
				}
				
				if (type.contains("Confirmation"))
				{
					String text = getText(element.getElementsByTagName("ConfirmationText"), "ConfirmationText");
					result.getTranslations().add(new Translation(key + Confirmation.TEXT, text, lang, surveyId, result));
					text = getText(element.getElementsByTagName("ConfirmationLabel"), "ConfirmationLabel");
					result.getTranslations().add(new Translation(key + Confirmation.LABEL, text, lang, surveyId, result));
				}
				
				if (type.contains("ChoiceQuestion"))
				for (int j = 0; j < element.getElementsByTagName("Answer").getLength(); j++)
				{
					org.w3c.dom.Element answer = (org.w3c.dom.Element) element.getElementsByTagName("Answer").item(j);
					key = answer.getAttribute("key");
					label = getText(answer.getElementsByTagName("Label"), "Label");					
					result.getTranslations().add(new Translation(key, label, lang, surveyId, result));
					
					try {
						label = getText(answer.getElementsByTagName("Feedback"), "Feedback");					
						result.getTranslations().add(new Translation(key + Question.FEEDBACK, label, lang, surveyId, result));
					} catch (Exception e) {}
				}
				
				if (type.contains("Matrix") || type.contains("Table"))
				{
					org.w3c.dom.Element children = (org.w3c.dom.Element) element.getElementsByTagName("Children").item(0);
					
					for (int j = 0; j < children.getElementsByTagName("Element").getLength(); j++)
					{
						org.w3c.dom.Element child = (org.w3c.dom.Element) children.getElementsByTagName("Element").item(j);
						key = Tools.repairXML(child.getAttribute("key"));
						type = Tools.repairXML(child.getAttribute("type"));
						label = getText(child.getElementsByTagName("Label"), "Label");
						
						result.getTranslations().add(new Translation(key, label, lang, surveyId, result));
					}	
				}		
				
				if (type.contains("RatingQuestion"))
				{
					org.w3c.dom.Element children = (org.w3c.dom.Element) element.getElementsByTagName("Children").item(0);
					
					for (int j = 0; j < children.getElementsByTagName("Element").getLength(); j++)
					{
						org.w3c.dom.Element child = (org.w3c.dom.Element) children.getElementsByTagName("Element").item(j);
						key = Tools.repairXML(child.getAttribute("key"));
						type = Tools.repairXML(child.getAttribute("type"));
						label = getText(child.getElementsByTagName("Label"), "Label");
						
						result.getTranslations().add(new Translation(key, label, lang, surveyId, result));
					}	
				}	
				
				org.w3c.dom.NodeList feedbackChildren = element.getElementsByTagName("Feedback");
				
				for (int j = 0; j < feedbackChildren.getLength(); j++)
				{
					org.w3c.dom.Element feedback = (org.w3c.dom.Element) feedbackChildren.item(j);
					key = Tools.repairXML(feedback.getAttribute("key"));
					label = Tools.repairXML(Tools.filterHTML(feedback.getTextContent()));
					
					result.getTranslations().add(new Translation(key + Question.FEEDBACK, label, lang, surveyId, result));
				}	
			}
			
			return removeIDs(result);
			
		} catch(Exception e){
    		logger.error(e.getLocalizedMessage(), e); 
    	}
		
		return null; 

	}	
	
	private static Translations removeIDs(Translations result) {
		for (Translation t : result.getTranslations())
		{
			String label = t.getLabel().replaceAll("\\s+(?:id)\\s*=\\s*\"[^\"]*\"","").replaceAll("\\s+(?:id)\\s*=\\s*\'[^\"]*\'","");
			t.setLabel(label);
		}
		return result;
	}

	public static Translations importXLS(InputStream inputStream, List<String> invalidKeys, SurveyService surveyService, ServletContext servletContext) {
		
		try {
		
			Translations result = new Translations();
			
			HSSFWorkbook wb = new  HSSFWorkbook(inputStream);	            	
	    	HSSFSheet sheet = wb.getSheetAt(0);
	    	int rows = sheet.getPhysicalNumberOfRows();
	    		    	
	    	int surveyId = 0;
	    	try {
	    		surveyId = Integer.parseInt(getText(sheet, 0, 0, Survey.TITLE, invalidKeys, servletContext));
		    	result.setSurveyId(surveyId);
		    	
		    	Survey survey = surveyService.getSurvey(surveyId, false, true);
		    	result.setSurveyUid(survey.getUniqueId());
	    	} catch (NumberFormatException e)
	    	{
	    		result.setSurveyUid(getText(sheet, 0, 0, Survey.TITLE, invalidKeys, servletContext));

	    		Survey survey = surveyService.getSurveyByUniqueId(result.getSurveyUid(), false, true);
	    		surveyId = survey.getId();
	    		result.setSurveyId(surveyId);
	    	}	    	
	    	
	    	String language = getText(sheet, 0, 1, "language", invalidKeys, servletContext); 
	    	
	    	Language objLang = surveyService.getLanguage(language);
			if (objLang == null)
			{
				objLang = new Language(language,language,language,false);
			}
	    	
	    	result.setLanguage(objLang);
	    	    	
	    	HashMap<String,String> typeSuffixMapping = getTypeSuffixMapping();
	    	String label;
	    	
	    	for (int r = 1; r < rows; r++) {
				HSSFRow row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				String key = getText(sheet, r, 0, "key" + r, invalidKeys, servletContext); 
				String type = getText(sheet, r, 1,key, invalidKeys, servletContext);
				
				if(typeSuffixMapping.containsKey(type))
				{
					key = key + typeSuffixMapping.get(type);
				}				
				
				label = getText(sheet, r, 2, key, invalidKeys, servletContext); 
				
				if (key.equalsIgnoreCase(Survey.TITLE))
				{
					result.setTitle(label);
				} 
				
				result.getTranslations().add(new Translation(key, label, language, surveyId, result));
	    	}	
	           	                   
	    	wb.close();
	    	inputStream.close();
			
			return removeIDs(result);
			
		} catch(Exception e){
    		logger.error(e.getLocalizedMessage(), e); 
    	}
		
		return null; 
	}	
	
	public static Translations importODS(InputStream inputStream, List<String> invalidKeys, SurveyService surveyService, ServletContext servletContext) {
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
	    	} catch (NumberFormatException e)
	    	{
	    		String surveyUid = sheet.getCellByPosition(0, 0).getStringValue().trim();
	    		result.setSurveyUid(surveyUid);
	    		
	    		Survey survey = surveyService.getSurveyByUniqueId(surveyUid, false, true);
	    		surveyId = survey.getId();	
	    		result.setSurveyId(surveyId);
	    	}	    	
	    	
	    	String language = getODSText(sheet, 0, 1, "language", invalidKeys, servletContext); 
	    	
	    	Language objLang = surveyService.getLanguage(language);
			if (objLang == null)
			{
				objLang = new Language(language,language,language,false);
			}
	    	
	    	result.setLanguage(objLang);
	    	
	    	HashMap<String,String> typeSuffixMapping = getTypeSuffixMapping();
	    	
	    	int emptyLineCount = 0;
	    	
	    	for (int r = 1; r < rows; r++) {
	    		org.odftoolkit.simple.table.Row row = sheet.getRowByIndex(r);
				if (row == null) 
				{
					continue;
				}
				if(emptyLineCount > maxEmptyOdsLines) 
				{
					logger.debug(String.format("importODS: Exiting import loop due to %d consecutive empty rows.", emptyLineCount));
					break;
				}
				
				String key = getODSText(sheet, r, 0, "key" + r, invalidKeys, servletContext); 
				String type = getODSText(sheet, r, 1,key, invalidKeys, servletContext);
				
				if(key.isEmpty())
				{
					emptyLineCount++;
					continue;
				}
				if(typeSuffixMapping.containsKey(type))
				{
					key = key + typeSuffixMapping.get(type);
				}
				String label = getODSText(sheet, r, 2, key, invalidKeys, servletContext); 
				
				result.getTranslations().add(new Translation(key, label, language, surveyId, result));
				
				if (key.equalsIgnoreCase(Survey.TITLE))
				{
					result.setTitle(label);
				} 				
				
	    	}	
	           	                   
	    	inputStream.close();
			
			return removeIDs(result);
		} catch(Exception e){
    		logger.error(e.getLocalizedMessage(), e); 
    	}
		
		return null; 
	}

	
	private static String getODSText(org.odftoolkit.simple.table.Table sheet, int row, int column, String key, List<String> invalidKeys, ServletContext servletContext) throws IntrusionException {
		String result =  Tools.filterHTML(sheet.getCellByPosition(column, row).getStringValue().trim());
		return getText(result, key, invalidKeys, servletContext);
	}

	private static String getText(HSSFSheet sheet, int row, int cell, String key, List<String> invalidKeys, ServletContext servletContext)
	{
		try
		{
			String result;
			
			if (sheet.getRow(row).getCell(cell).getCellType() == 0){
				result = "" + ((int)sheet.getRow(row).getCell(cell).getNumericCellValue());
			} else {
				result = Tools.filterHTML(sheet.getRow(row).getCell(cell).getStringCellValue().trim());	
			}
			
			return getText(result, key, invalidKeys, servletContext);
		} catch (Exception e)
		{
			return "";
		}
	}
	
	private static String getText(String result, String key, List<String> invalidKeys, ServletContext servletContext)
		{
		//replace invalid characters
		String re = "\\p{Cntrl}";
		result = result.replaceAll(re, "");
		
		try {
		
			//check wellformed-ness
			
			if (result != null && !XHTMLValidator.validate(result, servletContext, null))
			{
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
	
	public static String serializeXml(org.w3c.dom.Node element) throws Exception
	{
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    StreamResult result = new StreamResult(buffer);

	    DOMSource source = new DOMSource(element);
	    TransformerFactory.newInstance().newTransformer().transform(source, result);

	    return new String(buffer.toByteArray(), "utf-8").replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").replace("<" + element.getNodeName() + ">", "").replace("</" + element.getNodeName() + ">", "").replace("<" + element.getNodeName() + "/>", "").trim();
	}
	
	private static String getText(NodeList elements, String name) throws Exception
	{
		if (elements == null || elements.getLength() < 1) 
		{
			if (name.equalsIgnoreCase("Help") || name.equalsIgnoreCase(Section.TABTITLE)) return null;
			throw new Exception(name + " was not found!"); 
		}

		return Tools.repairXML(Tools.filterHTML(serializeXml(elements.item(0))));
	}

	public static void synchronize(Translations existingTranslations, Translations translations) {
		
		existingTranslations.setActive(false);
		existingTranslations.setTitle(translations.getTitle());
		
		Map<String, Translation> existingTranslationsMap = existingTranslations.getTranslationsByKey();
		
		for (Translation translation: translations.getTranslations())
		{
			if (existingTranslationsMap.containsKey(translation.getKey()))
			{
				existingTranslationsMap.get(translation.getKey()).setLabel(translation.getLabel());
			} else {
				existingTranslations.getTranslations().add(new Translation(translation.getKey(), translation.getLabel(), translation.getLanguage(), existingTranslations.getSurveyId(), existingTranslations));
			}
		}
		
	}
	
	private static boolean notNullOrEmpty(String s)
	{
		if (s == null) return false;
		return s.trim().length() != 0;
	}

	public static void synchronizePivot(Survey survey, Translations translations) {
		
		Map<String, Translation> translationsByKey = translations.getTranslationsByKey();
		
		if (translationsByKey.containsKey(Survey.TITLE) && notNullOrEmpty(translationsByKey.get(Survey.TITLE).getLabel())) survey.setTitle(translationsByKey.get(Survey.TITLE).getLabel());
		if (translationsByKey.containsKey(Survey.INTRODUCTION) && notNullOrEmpty(translationsByKey.get(Survey.INTRODUCTION).getLabel())) survey.setIntroduction(translationsByKey.get(Survey.INTRODUCTION).getLabel());
		if (translationsByKey.containsKey(Survey.ESCAPEPAGE) && notNullOrEmpty(translationsByKey.get(Survey.ESCAPEPAGE).getLabel())) survey.setEscapePage(translationsByKey.get(Survey.ESCAPEPAGE).getLabel());
		if (translationsByKey.containsKey(Survey.ESCAPELINK) && notNullOrEmpty(translationsByKey.get(Survey.ESCAPELINK).getLabel())) survey.setEscapeLink(translationsByKey.get(Survey.ESCAPELINK).getLabel());
		if (translationsByKey.containsKey(Survey.CONFIRMATIONPAGE) && notNullOrEmpty(translationsByKey.get(Survey.CONFIRMATIONPAGE).getLabel())) survey.setConfirmationPage(translationsByKey.get(Survey.CONFIRMATIONPAGE).getLabel());
		if (translationsByKey.containsKey(Survey.CONFIRMATIONLINK) && notNullOrEmpty(translationsByKey.get(Survey.CONFIRMATIONLINK).getLabel())) survey.setConfirmationLink(translationsByKey.get(Survey.CONFIRMATIONLINK).getLabel());
		
		Set<String> linkstodelete = new HashSet<>();
		for (String key : survey.getUsefulLinks().keySet())
		{
			String[] data = key.split("#");
			if (translationsByKey.containsKey(data[0] + "#usefullink") && notNullOrEmpty(translationsByKey.get(data[0] + "#usefullink").getLabel()) && !translationsByKey.get(data[0] + "#usefullink").getLabel().equals(data[1]))
			{
				survey.getUsefulLinks().put(data[0] + "#" + translationsByKey.get(data[0] + "#usefullink").getLabel(), survey.getUsefulLinks().get(key));
				linkstodelete.add(key);
			}
		}
		for (String key: linkstodelete)
		{
			survey.getUsefulLinks().remove(key);
		}
		
		Set<String> backdocstodelete = new HashSet<>();
		for (String key : survey.getBackgroundDocuments().keySet())
		{
			if (translationsByKey.containsKey(key + "#backgrounddocument") && notNullOrEmpty(translationsByKey.get(key + "#backgrounddocument").getLabel()) && !translationsByKey.get(key + "#backgrounddocument").getLabel().equals(key))
			{
				survey.getBackgroundDocuments().put(translationsByKey.get(key + "#backgrounddocument").getLabel(), survey.getBackgroundDocuments().get(key));
				translations.getTranslations().add(new Translation(translationsByKey.get(key + "#backgrounddocument").getLabel() + "#backgrounddocument", translationsByKey.get(key + "#backgrounddocument").getLabel(), translations.getLanguage().getCode() , survey.getId(), translations));
				translations.getTranslations().remove(translationsByKey.get(key + "#backgrounddocument"));
				backdocstodelete.add(key);
			}
		}
		for (String key: backdocstodelete)
		{
			survey.getBackgroundDocuments().remove(key);
		}
		
		for (Element element : survey.getElements())
		{
			if (translationsByKey.containsKey(element.getId().toString()) && notNullOrEmpty(translationsByKey.get(element.getId().toString()).getLabel())) element.setTitle(translationsByKey.get(element.getId().toString()).getLabel());
			if (translationsByKey.containsKey(element.getUniqueId()) && notNullOrEmpty(translationsByKey.get(element.getUniqueId()).getLabel())) element.setTitle(translationsByKey.get(element.getUniqueId()).getLabel());
					
			if (element instanceof Question)
			{
				Question question = (Question)element;
				if (translationsByKey.containsKey(element.getId().toString() + "help") && notNullOrEmpty(translationsByKey.get(element.getId().toString() + "help").getLabel())) question.setHelp(translationsByKey.get(element.getId().toString() + "help").getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + "help") && notNullOrEmpty(translationsByKey.get(element.getUniqueId() + "help").getLabel())) question.setHelp(translationsByKey.get(element.getUniqueId() + "help").getLabel());
			}
			
			if (element instanceof Section)
			{
				Section section = (Section)element;
				if (translationsByKey.containsKey(element.getId().toString() + Section.TABTITLE) && notNullOrEmpty(translationsByKey.get(element.getId().toString() + Section.TABTITLE).getLabel())) section.setTabTitle(translationsByKey.get(element.getId().toString() + Section.TABTITLE).getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + Section.TABTITLE) && notNullOrEmpty(translationsByKey.get(element.getUniqueId() + Section.TABTITLE).getLabel())) section.setTabTitle(translationsByKey.get(element.getUniqueId() + Section.TABTITLE).getLabel());
			}
			
			if (element instanceof NumberQuestion)
			{
				NumberQuestion number = (NumberQuestion)element;
				if (translationsByKey.containsKey(element.getId().toString() + NumberQuestion.UNIT) && notNullOrEmpty(translationsByKey.get(element.getId().toString() + NumberQuestion.UNIT).getLabel())) number.setUnit(translationsByKey.get(element.getId().toString() + NumberQuestion.UNIT).getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + NumberQuestion.UNIT) && notNullOrEmpty(translationsByKey.get(element.getUniqueId() + NumberQuestion.UNIT).getLabel())) number.setUnit(translationsByKey.get(element.getUniqueId() + NumberQuestion.UNIT).getLabel());
			}
			
			if (element instanceof Confirmation)
			{
				Confirmation confirmation = (Confirmation)element;
				if (translationsByKey.containsKey(element.getId().toString() + Confirmation.TEXT) && notNullOrEmpty(translationsByKey.get(element.getId().toString() + Confirmation.TEXT).getLabel())) confirmation.setConfirmationtext(translationsByKey.get(element.getId().toString() + Confirmation.TEXT).getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + Confirmation.TEXT) && notNullOrEmpty(translationsByKey.get(element.getUniqueId() + Confirmation.TEXT).getLabel())) confirmation.setConfirmationtext(translationsByKey.get(element.getUniqueId() + Confirmation.TEXT).getLabel());
	
				if (translationsByKey.containsKey(element.getId().toString() + Confirmation.LABEL) && notNullOrEmpty(translationsByKey.get(element.getId().toString() + Confirmation.LABEL).getLabel())) confirmation.setConfirmationlabel(translationsByKey.get(element.getId().toString() + Confirmation.LABEL).getLabel());
				if (translationsByKey.containsKey(element.getUniqueId() + Confirmation.LABEL) && notNullOrEmpty(translationsByKey.get(element.getUniqueId() + Confirmation.LABEL).getLabel())) confirmation.setConfirmationlabel(translationsByKey.get(element.getUniqueId() + Confirmation.LABEL).getLabel());
			}
			
			if (element instanceof ChoiceQuestion)
			{
				ChoiceQuestion choice = (ChoiceQuestion)element;
				for (PossibleAnswer answer : choice.getPossibleAnswers())
				{
					if (translationsByKey.containsKey(answer.getId().toString()) && notNullOrEmpty(translationsByKey.get(answer.getId().toString()).getLabel())) answer.setTitle(translationsByKey.get(answer.getId().toString()).getLabel());
					if (translationsByKey.containsKey(answer.getUniqueId()) && notNullOrEmpty(translationsByKey.get(answer.getUniqueId()).getLabel())) answer.setTitle(translationsByKey.get(answer.getUniqueId()).getLabel());
				}
			}
			
			if (element instanceof Matrix)
			{
				Matrix matrix = (Matrix)element;
				
				for (Element child : matrix.getChildElements())
				{
					if (child instanceof Text)
					{
						if (translationsByKey.containsKey(child.getId().toString()) && notNullOrEmpty(translationsByKey.get(child.getId().toString()).getLabel())) child.setTitle(translationsByKey.get(child.getId().toString()).getLabel());
						if (translationsByKey.containsKey(child.getUniqueId()) && notNullOrEmpty(translationsByKey.get(child.getUniqueId()).getLabel())) child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());
					}
				}
			}
			
			if (element instanceof Table)
			{
				Table table = (Table)element;
				for (Element child : table.getChildElements())
				{
					if (child instanceof Text)
					{
						if (translationsByKey.containsKey(child.getId().toString()) && notNullOrEmpty(translationsByKey.get(child.getId().toString()).getLabel())) child.setTitle(translationsByKey.get(child.getId().toString()).getLabel());
						if (translationsByKey.containsKey(child.getUniqueId()) && notNullOrEmpty(translationsByKey.get(child.getUniqueId()).getLabel())) child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());
					}
				}				
			}
			
			if (element instanceof RatingQuestion)
			{
				RatingQuestion rating = (RatingQuestion)element;
				for (Element child : rating.getChildElements())
				{
					if (child instanceof Text)
					{
						if (translationsByKey.containsKey(child.getId().toString()) && notNullOrEmpty(translationsByKey.get(child.getId().toString()).getLabel())) child.setTitle(translationsByKey.get(child.getId().toString()).getLabel());
						if (translationsByKey.containsKey(child.getUniqueId()) && notNullOrEmpty(translationsByKey.get(child.getUniqueId()).getLabel())) child.setTitle(translationsByKey.get(child.getUniqueId()).getLabel());
					}
				}				
			}
		}
	}

	public static boolean isComplete(Translations translations, Survey survey) // TODO: Add all the other mandatory elements. Check also all elements are correctly taken into account in the similar helper functions above
	{
		try
		{
			Map<String, String> translationMap = translations.getTranslationsMap();
			
			if ((!translationMap.containsKey(Survey.TITLE) || translationMap.get(Survey.TITLE) == null || translationMap.get(Survey.TITLE).trim().length() == 0)) return false;
			if (survey.getIntroduction() != null && survey.getIntroduction().trim().length() > 0 && (!translationMap.containsKey(Survey.INTRODUCTION) || translationMap.get(Survey.INTRODUCTION).trim().length() == 0)) return false;
			if (survey.getEscapePage() != null && survey.getEscapePage().trim().length() > 0 && (!translationMap.containsKey(Survey.ESCAPEPAGE) || translationMap.get(Survey.ESCAPEPAGE).trim().length() == 0)) return false;
			if (survey.getEscapeLink() != null && survey.getEscapeLink().trim().length() > 0 && (!translationMap.containsKey(Survey.ESCAPELINK) || translationMap.get(Survey.ESCAPELINK).trim().length() == 0)) return false;
			if (survey.getConfirmationPage() != null && survey.getConfirmationPage().trim().length() > 0 && (!translationMap.containsKey(Survey.CONFIRMATIONPAGE) || translationMap.get(Survey.CONFIRMATIONPAGE).trim().length() == 0)) return false;
			if (survey.getConfirmationLink() != null && survey.getConfirmationLink().trim().length() > 0 && (!translationMap.containsKey(Survey.CONFIRMATIONLINK) || translationMap.get(Survey.CONFIRMATIONLINK).trim().length() == 0)) return false;
				
			for (Element element: survey.getElementsRecursive())
			{
				if (!(element instanceof EmptyElement) && element.getTitle() != null)
				{	
					if (element.getTitle().length() > 0 && (getLabel(element,"",translationMap).length() == 0))
					{
						return false;
					}
					if (element instanceof Question)
					{
						Question question = (Question)element;
					
						if (question instanceof ChoiceQuestion)
						{
							ChoiceQuestion choice = (ChoiceQuestion)question;
							for (PossibleAnswer answer: choice.getPossibleAnswers())
							{
								if (getLabel(answer,"",translationMap).length() == 0)
								{
									return false;
								}
							}
						}
	
						if (question instanceof FreeTextQuestion)
	  					{
	 						if (getLabel(question,"",translationMap).length() == 0)
	  						{
	  							return false;
	  						}
	  					}
					}
					
	  				if (element instanceof Matrix)
	  				{
	  					Matrix matrix = (Matrix)element;
						
						for (Element child : matrix.getChildElements())
						{
							if (child instanceof Text)
							{
								if (getLabel(child,"",translationMap).length() == 0)
								{
									return false;
								}
							}
						}
					}
	  				
	  				if (element instanceof RatingQuestion)
	  				{
	  					RatingQuestion rating = (RatingQuestion)element;
						
						for (Element child : rating.getChildElements())
						{
							if (child instanceof Text)
							{
								if (getLabel(child,"",translationMap).length() == 0)
								{
									return false;
								}
							}
						}
					}
				
				}
			}
			return true;
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			return false;
		}
	}

}
