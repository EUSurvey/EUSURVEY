package com.ec.survey.model;

import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.*;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Numbering;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.MessageSource;

import java.util.*;

@Configurable
public class Form {
	
	private Survey survey;	
	private User user;
	private Map<String, Language> languages = new HashMap<>();
	private List<AnswerSet> answerSets = new ArrayList<>();
	private Statistics statistics;
	private Language language;
	private String languageTitle;
	private boolean isPublished; 
	private boolean publicationMode;
	private long numberOfAnswerSets;
	private UploadItem uploadItem;
	private HashMap<Element, String> validation = new HashMap<>();
	private int startHour;
	private int endHour;
	private boolean forPDF;
	
    private int varPagesEltNb = 0; 
	private List<String> varPagesEltIds; 
	private List<String> varPagesEltWidths;
	private MessageSource resources; 
	
	private String contextpath;
	
	protected static final Logger logger = Logger.getLogger(Form.class);
	
	public Form() {}
	
	public Form(MessageSource resources) {
		this.resources = resources;
	}
	
	public Form(Survey survey, List<Translations> translations, Language pivot, MessageSource resources, String contextpath) {
		
		survey.computeTriggers();

		this.survey = survey;
		this.language = survey.getLanguage();
		this.languageTitle = pivot.getName();
		this.resources = resources;
		this.contextpath = contextpath;

		setTranslations(translations);
	}
	
	public Form(Survey survey, List<Translations> translations, Language pivot, boolean checkComplete, MessageSource resources, String contextpath) {
		this.survey = survey;
		this.language = survey.getLanguage();
		this.languageTitle = pivot.getName();
		this.resources = resources;
		this.contextpath = contextpath;

		if (checkComplete) {
			setTranslations(translations);
		} else {
			Map<String, Language> languages = new HashMap<>();
			
			for (Translations translation: translations)
			{
				languages.put(translation.getLanguage().getCode(), translation.getLanguage());
			}

			this.languages = languages;
		}
		
	}

	public Form(MessageSource resources, Language language, String contextpath) {
		this.resources = resources;
		this.language = language;
		this.contextpath = contextpath;
	}

	public Form(MessageSource resources, Language language, List<Translations> translations, String contextpath) {
		this.resources = resources;
		this.language = language;
		this.contextpath = contextpath;

		Map<String, Language> languages = new HashMap<>();
		
		for (Translations translation: translations)
		{
			languages.put(translation.getLanguage().getCode(), translation.getLanguage());
		}

		this.languages = languages;
	}

	public void setTranslations(List<Translations> translations)
	{
		Map<String, Language> languages = new HashMap<>();
		
		for (Translations translation: translations)
		{
			if (translation.getComplete())
			languages.put(translation.getLanguage().getCode(), translation.getLanguage());
		}

		this.languages = languages;
	}
	
	public Survey getSurvey() {
		return survey;
	}
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
	
	public UploadItem getUploadItem() {
		return uploadItem;
	}
	public void setUploadItem(UploadItem uploadItem) {
		this.uploadItem = uploadItem;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public Language getLanguage() {
		return language;
	}
	public void setLanguage(Language language) {
		this.language = language;
	}
	
	public String getLanguageTitle() {
		return languageTitle;
	}
	public void setLanguageTitle(String languageTitle) {
		this.languageTitle = languageTitle;
	}	

	public String getAnswerTitleStripInvalidXML(Answer answer) {
		return ConversionTools.removeInvalidHtmlEntities(getAnswerTitle(answer));
	}
	
	public String getAnswerTitle(Answer answer)
	{
		String answerValue = answer.getValue();	
		
		try {

			if (survey != null)
			{
		
				
				Element question = null;

				Map<Integer, Question> questionMap = survey.getQuestionMap();
				
				if (questionMap != null)
				{
					question = questionMap.get(answer.getQuestionId());
				}
				
				if (question == null)
				{
					question = survey.getMatrixMap().get(answer.getQuestionId());
				}
				
				if (question == null)
				{
					question = survey.getQuestionMapByUniqueId().get(answer.getQuestionUniqueId());
				}
				
				if (question == null)
				{
					question = survey.getMissingElementsById().get(answer.getQuestionId());
				}
				
				if (question == null && answer.getQuestionUniqueId() != null && answer.getQuestionUniqueId().length() > 0)
				{
					question = survey.getMissingElementsByUniqueId().get(answer.getQuestionUniqueId());
				}
				
				if (question == null && answer.getPossibleAnswerId() > 0)
				{
					int possibleAnswerId = Integer.parseInt(answerValue);
					if (survey.getMissingElementsById().containsKey(possibleAnswerId))
					{
						return survey.getMissingElementsById().get(possibleAnswerId).getStrippedTitle();
					}

					if (answer.getPossibleAnswerUniqueId() != null && answer.getPossibleAnswerUniqueId().length() > 0 && survey.getElementsByUniqueId().containsKey(answer.getPossibleAnswerUniqueId()))
					{
						return survey.getElementsByUniqueId().get(answer.getPossibleAnswerUniqueId()).getStrippedTitle();
					}

					if (answer.getPossibleAnswerUniqueId() != null && answer.getPossibleAnswerUniqueId().length() > 0 && survey.getMissingElementsByUniqueId().containsKey(answer.getPossibleAnswerUniqueId()))
					{
						return survey.getMissingElementsByUniqueId().get(answer.getPossibleAnswerUniqueId()).getStrippedTitle();
					}
				}
				
				if (question instanceof FreeTextQuestion) {
					if (publicationMode && answerValue != null && answerValue.length() > 0) {
						FreeTextQuestion q = (FreeTextQuestion)question;
						if (q.getIsPassword())
						{
							return "********";
						}
					}
					return answerValue;
				} else if (question instanceof ChoiceQuestion) {
					int possibleAnswerId = Integer.parseInt(answerValue);
					ChoiceQuestion choicequestion = (ChoiceQuestion) question;
					if (choicequestion.getPossibleAnswer(possibleAnswerId) != null)
					{
						return choicequestion.getPossibleAnswer(possibleAnswerId).getStrippedTitle();
					} else {
						if (survey.getMissingElementsById().containsKey(possibleAnswerId))
						{
							return survey.getMissingElementsById().get(possibleAnswerId).getStrippedTitle();
						}
					}
					
					if (choicequestion.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId()) != null)
					{
						return choicequestion.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId()).getStrippedTitle();
					}
					
					return "";
				} else if (question instanceof Text || question instanceof Image || question instanceof EmptyElement)
				{
					//could be inside a matrix
					int possibleAnswerId = Integer.parseInt(answerValue);
					for (Element element: survey.getElements())
					{
						if (element instanceof Matrix)
						{
							for (Element child: ((Matrix)element).getChildElements())
							{
								if (child.getId().equals(possibleAnswerId) || (child.getUniqueId() != null && child.getUniqueId().equalsIgnoreCase(answer.getPossibleAnswerUniqueId())))
								{
									return child.getStrippedTitle();
								}
							}
							for (Element child: ((Matrix)element).getMissingAnswers())
							{
								if (child.getId().equals(possibleAnswerId))
								{
									return child.getStrippedTitle();
								}
							}
						}
					}
					for (Element element: survey.getMissingElements())
					{
						if (element instanceof Matrix)
						{
							for (Element child: ((Matrix)element).getChildElements())
							{
								if (child.getId().equals(possibleAnswerId))
								{
									return child.getStrippedTitle();
								}
							}
							for (Element child: ((Matrix)element).getMissingAnswers())
							{
								if (child.getId().equals(possibleAnswerId))
								{
									return child.getStrippedTitle();
								}
							}
						} else if (element.getId().equals(possibleAnswerId))
						{
							return element.getStrippedTitle();
						}
					}
					
					return "";
				} 
			}
		
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			return "";
		}
			
		return answerValue;		
	}
	
	public String getAnswerShortname(Answer answer)
	{
		String answerValue = answer.getValue();	
		
		try {

			Element question = survey.getQuestionMap().get(answer.getQuestionId());
		
		if (question == null)
		{
			question = survey.getMatrixMap().get(answer.getQuestionId());
		}
		
		if (question == null)
		{
			question = survey.getQuestionMapByUniqueId().get(answer.getQuestionUniqueId());
		}
		
		if (question instanceof FreeTextQuestion) {
			return "";
		} else if (question instanceof ChoiceQuestion) {
			int possibleAnswerId = Integer.parseInt(answerValue);
			
			ChoiceQuestion choice = (ChoiceQuestion) question;
			
			if (choice.getPossibleAnswer(possibleAnswerId) != null)
			{
				return "(" + choice.getPossibleAnswer(possibleAnswerId).getShortname() + ")";
			} else {
				if (survey.getMissingElementsById().containsKey(possibleAnswerId))
				{
					return "(" + survey.getMissingElementsById().get(possibleAnswerId).getShortname()+ ")";
				}
			}
			
			if (answer.getPossibleAnswerUniqueId() != null)
			{
				PossibleAnswer pa = choice.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId());
				String res = pa != null ? pa.getShortname() : null;
				if (res != null) return "(" + res + ")";
			}
			
			return "";
		} else if (question instanceof Text || question instanceof Image || question instanceof EmptyElement)
		{
			//could be inside a matrix
			int possibleAnswerId = Integer.parseInt(answerValue);
			for (Element element: survey.getElements())
			{
				if (element instanceof Matrix)
				{
					for (Element child: ((Matrix)element).getChildElements())
					{
						if (child.getId().equals(possibleAnswerId))
						{
							return "(" + child.getShortname() + ")";
						}
					}
				}
			}
			
			
		} 
		
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
			
		return "";
		
	}
	
	public List<AnswerSet> getAnswerSets() {
		return answerSets;
	}
	
	public void setAnswerSets(List<AnswerSet> answerSets) {
		this.answerSets = answerSets;		
	}

	public Map<String, Language> getLanguages() {
		return languages;
	}
	public void setLanguages(Map<String, Language> languages) {
		this.languages = languages;		
	}
	
	public Map<String, Language> getLanguagesAlphabetical() {
		if (languages == null) return null;
		
		TreeMap<String, Language> result = new TreeMap<>();
		result.putAll(languages);
		return result;
	}
	
	public long getNumberOfAnswerSets() {
		return numberOfAnswerSets;
	}
	public void setNumberOfAnswerSets(long numberOfAnswerSets) {
		this.numberOfAnswerSets = numberOfAnswerSets;
	}
	
	public boolean getIsPublished() {
		return isPublished;
	}
	public void setIsPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}
	
	public ArrayList<ArrayList<Element>> getPages()
	{
		ArrayList<ArrayList<Element>> pages = new ArrayList<>();
		
		Integer currentPage = 0;
		ArrayList<Element> currentList = new ArrayList<>();
		pages.add(currentList);
		for (Element element : survey.getElements()) {
			if (survey.getMultiPaging() && element instanceof Section && ((Section)element).getLevel() == 1)
			{
				if (currentPage == 0 && currentList.size() == 0)
				{
					//this is the very first sections -> no need to break					
				} else {
					currentPage++;
					currentList = new ArrayList<>();
					pages.add(currentList);
				}
				
				currentList.add(element);
				
			} else {
				currentList.add(element);
			}
		}
		
		return pages;
	}
	
	public String getValueStripInvalidXML(Element question) {
		String result = ConversionTools.removeInvalidHtmlEntities(getValue(question));
		result = StringEscapeUtils.escapeXml(result);
		return result;
	}
	
	public int getRatingValue(Element question)
	{
		if (answerSets.size() > 0)
		{
			List<Answer> answers = answerSets.get(0).getAnswers(question.getId(), question.getUniqueId());
			if (answers.size() > 0) {
				String result = answers.get(0).getValue();
				result = result.substring(0, result.indexOf("/"));
				return Integer.parseInt(result);
			}
		}
		
		return 0;
	}

	public String getValue(Element question)
	{
		if (answerSets.size() > 0)
		{
			List<Answer> answers = answerSets.get(0).getAnswers(question.getId(), question.getUniqueId());
			if (answers.size() > 0) {
				
				String result = answers.get(0).getValue();
				
				if (result != null && result.length() > 0 && question instanceof FreeTextQuestion)
				{
					FreeTextQuestion q = (FreeTextQuestion)question;
					if (q.getIsPassword())
					{
						return "********";
					}
				}
				
				return result; 
			}
		} 
		
		return "";
	}
	
	private Set<String> passwordQuestions = null;
	public Set<String> getPasswordQuestions()
	{
		if (passwordQuestions == null)
		{
			passwordQuestions = new HashSet<>();
			for (Element question : survey.getElements())
			{
				if (question instanceof FreeTextQuestion && ((FreeTextQuestion) question).getIsPassword())
				{
					passwordQuestions.add(question.getUniqueId());
				}
			}
		}
		return passwordQuestions;
	}
	
	public List<String> getValues(Element question)
	{
		if (answerSets.size() > 0)
		{
			List<Answer> answers = answerSets.get(0).getAnswers(question.getId(), question.getUniqueId());
			List<String> result = new ArrayList<>();
			for (Answer answer: answers)
			{
				result.add(answer.getValue()); 
				if (answer.getPossibleAnswerUniqueId() != null)
				{
					result.add(answer.getPossibleAnswerUniqueId());
				}
			}
			return result;
		}
		
		return new ArrayList<>();
	}
	
	public String getSectionTitle(Section section)
	{
		String prefix = getSectionNumbering(section);		
				
		if (prefix.length() > 0)
		{
			if (section.getTitle().startsWith("<p"))
			{
				int index = section.getTitle().indexOf(">");				
				return section.getTitle().substring(0, index+1) + "<span class='numbering'>" + prefix + "</span>" + section.getTitle().substring(index+1);
			} else {
				return "<span class='numbering'>" + prefix + "</span>" + section.getTitle();
			}
		} else {
			return section.getTitle();
		}
		
	}
	
	private String getSectionNumbering(Section section)
	{

		if (section == null || survey.getSectionNumbering() == 0) return "";
		
		String result = "";
		Numbering counter = new Numbering();
		
		for (Element element : survey.getElements())
		{
			if (element instanceof Section)
			{
				Section s = (Section) element;
																			
				if (s.getLevel() <= section.getLevel())
				{
					//if section is deeper than s
					switch(s.getLevel())
					{
						case 1:
							counter.setN1(counter.getN1()+1);
							break;
						case 2:
							counter.setN2(counter.getN2()+1);
							break;
						case 3:
							counter.setN3(counter.getN3()+1);
							break;
						case 4:
							counter.setN4(counter.getN4()+1);
							break;
						case 5:
							counter.setN5(counter.getN5()+1);
							break;
					}
				} 
								
				if (s.getId().equals(section.getId()))
				{
					return counter.getCounter(survey.getSectionNumbering(), section.getLevel());
				}	
							
			}
		}
		
		return result;
	}
		
	public String getCleanQuestionTitle(Element element)
	{
		return ConversionTools.removeHTML(getQuestionTitle(element), true);
	}
	
	public String getQuestionTitle(Element question)
	{	
		String title = question.getTitle();
		StringBuilder titlePrefix = new StringBuilder();
		
		if (title == null) title = "";
		
		if (question instanceof Confirmation)
		{
			if (answerSets.size() > 0 && answerSets.get(0).getAnswers(question.getId()).size() > 0)
			{
				if (forPDF)
				{
					titlePrefix.append("<img align='middle' style='margin-right: 7px;' src='").append(contextpath).append("/resources/images/checkboxchecked.png' />");
				} else {				
					titlePrefix.append("<input type='checkbox' checked='checked' class='required check' name='answer").append(question.getId()).append("' /> ");
				}
			} else {
				if (forPDF)
				{
					titlePrefix.append("<img align='middle' style='margin-right: 7px;' src='").append(contextpath).append("/resources/images/checkbox.png' />");
				} else {				
					titlePrefix.append("<input type='checkbox' class='required check' name='answer").append(question.getId()).append("' /> ");
				}
			}
		}		
//		
//		if (question instanceof Question && !((Question)question).getOptional())
//		{
//			titlePrefix.append("<span class='mandatory' aria-label='Mandatory'>*</span>");
//		} else {
//			titlePrefix.append("<span class='optional' aria-label='Optional'>*</span>");
//		}
		
		if (survey.getQuestionNumbering() == 0){
			
			if (title.startsWith("<p"))
			{
				int index = title.indexOf(">");				
				return title.substring(0, index+1) + titlePrefix + " " + title.substring(index+1);
			} else {
				return titlePrefix + title;
			}
		}
				
		Section lastSection = null;
		int counter = 0;
		
		for (Element element : survey.getElements())
		{
			if (element instanceof Section)
			{
				lastSection = (Section) element;
				if (survey.getQuestionNumbering() < 4)
				{
					counter = 0;
				}			
			}
			
			if (element instanceof Question)
			{
				if (!(element instanceof Text) && !(element instanceof Image))
				{
					counter++;
					Question q = (Question)element;
					if (q.getId().equals(question.getId()))
					{
						String result = "";

						if (survey.getQuestionNumbering() < 4)
						{
							result = getSectionNumbering(lastSection);
							if (result.length() > 0) result += ".";
						}
						
						if (!(question instanceof Text) && !(question instanceof Image))
						{
							switch (survey.getQuestionNumbering())
							{
								case 1:
								case 4:
									titlePrefix.append("<span class='numbering'>").append(result).append(counter).append("</span>");
									break;
								case 2:
								case 5:
									titlePrefix.append("<span class='numbering'>").append(result).append(Numbering.getSmallLetter(counter)).append("</span>");
									break;
								case 3:
								case 6:
									titlePrefix.append("<span class='numbering'>").append(result).append(Numbering.getBigLetter(counter)).append("</span>");
							}		
						} else {
							switch (survey.getQuestionNumbering())
							{
								case 1:
								case 4:
									titlePrefix.append(result).append(counter);
									break;
								case 2:
								case 5:
									titlePrefix.append(result).append(Numbering.getSmallLetter(counter));
									break;
								case 3:
								case 6:
									titlePrefix.append(result).append(Numbering.getBigLetter(counter));
							}	
						}
					}	
				}
			}			
		}
		
		String result;
		
		if (title.startsWith("<p"))
		{
			int index = title.indexOf(">");				
			result = title.substring(0, index+1) + titlePrefix + " " + title.substring(index+1);
		} else {
			result = titlePrefix + title;
		}
		
//		if (question instanceof Confirmation && !forPDF)
//		{
//			result += "</label>";
//		}	
		
		return result;
	}
	
	public Statistics getStatistics() {
		return statistics;
	}
	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}
	
	public HashMap<Element, String> getValidation() {
		return validation;
	}
	public void setValidation(HashMap<Element, String> validation) {
		this.validation = validation;
	}
	
	public String getValidationMessage(Element element)
	{
		try {
			for (Element candidate: validation.keySet())
			{
				if (candidate != null && element != null && !(candidate instanceof DraftIDElement) && candidate.getId().equals(element.getId())) return validation.get(candidate);
			}
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "";
	}
	
	public Set<Element> getValidationMessageElements()
	{
		return validation.keySet();
	}	
	
	public String getValidationDraftID()
	{
		for (Element candidate: validation.keySet())
		{
			if (candidate instanceof DraftIDElement)
			{
				return validation.get(candidate);
			}
		}
		return "";
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public int getVarPagesEltNb() {
		return varPagesEltNb;
	}

	public List<String> getVarPagesEltIds() {
		return varPagesEltIds;
	}

	public List<String> getVarPagesEltWidths() {
		return varPagesEltWidths;
	}
	
	public void setVarPageElts(List<String> ids, List<String> widths) {
		varPagesEltIds = ids;
		varPagesEltWidths = widths;
		varPagesEltNb = ids.size(); 
	}

	public void setPublicationMode(boolean publicationMode) {
		this.publicationMode = publicationMode;		
	}

	public boolean translationIsValid(String lang) {
		if (languages != null)
		{
			for (Language language: languages.values())
			{
				if (language.getCode().equalsIgnoreCase(lang))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public String getMessage(String key)
	{
		return getInternalMessage(key, null);
	}
	
	public String getMessage(String key, String arg1)
	{
		return getInternalMessage(key, new Object[]{arg1});
	}
	
	public String getMessage(String key, String arg1, String arg2)
	{
		return getInternalMessage(key, new Object[]{arg1, arg2});
	}

	private String getInternalMessage(String key, Object[] args)
	{
		String locale = "en";

		if (language != null)
		{
			locale = language.getCode().toLowerCase();
		} else if (this.survey != null && this.survey.getLanguage() != null) {
			locale = this.survey.getLanguage().getCode().toLowerCase();
		}
		return resources.getMessage(key, args, key, new Locale(locale));
	}

	public MessageSource getResources()
	{
		return resources;
	}
	
	public void setResources(MessageSource resources) {
		this.resources = resources;		
	}
			
	public boolean isForPDF() {
		return forPDF;
}
	public void setForPDF(boolean forPDF) {
		this.forPDF = forPDF;
	}

	Boolean wcagCompliance = null;
	public void setWcagCompliance(boolean b) {
		wcagCompliance = b;		
	}
	public boolean getWcagCompliance()
	{
		if (wcagCompliance != null) return wcagCompliance;
		if (survey != null) return survey.getWcagCompliance();
		return false;
	}
	
	public String maxColumnWidth(Element e)
	{
		if (e instanceof SingleChoiceQuestion)
		{
			SingleChoiceQuestion s = (SingleChoiceQuestion)e;
			if (s.getNumColumns() > 1)
			{
				return 800 / s.getNumColumns() + "px";
			}
		} else if (e instanceof MultipleChoiceQuestion)
		{
			MultipleChoiceQuestion m = (MultipleChoiceQuestion)e;
			if (m.getNumColumns() > 1)
			{
				return 800 / m.getNumColumns() + "px";
			}
		}
		
		return "none";
	}
			
}
