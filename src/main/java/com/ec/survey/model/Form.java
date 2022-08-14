package com.ec.survey.model;

import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Numbering;
import com.ec.survey.tools.SurveyHelper;
import com.ec.survey.tools.Tools;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Transient;

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
	private Map<Element, String> validation = new HashMap<>();
	private int startHour;
	private int endHour;
	private boolean forPDF;

	private int varPagesEltNb = 0;
	private List<String> varPagesEltIds;
	private List<String> varPagesEltWidths;
	private MessageSource resources;

	private String contextpath;
	private Date startDate = new Date();
	private Date currentDate = new Date();
	private Boolean codaEnabled;

	protected static final Logger logger = Logger.getLogger(Form.class);

	public Form() {
	}

	public Form(MessageSource resources) {
		this.resources = resources;
	}

	public Form(Survey survey, List<Translations> translations, Language pivot, MessageSource resources,
			String contextpath) {

		survey.computeTriggers();

		this.survey = survey;
		this.language = survey.getLanguage();
		this.languageTitle = pivot.getName();
		this.resources = resources;
		this.contextpath = contextpath;

		setTranslations(translations);
	}

	public Form(Survey survey, List<Translations> translations, Language pivot, boolean checkComplete,
			MessageSource resources, String contextpath) {
		this.survey = survey;
		this.language = survey.getLanguage();
		this.languageTitle = pivot.getName();
		this.resources = resources;
		this.contextpath = contextpath;

		if (checkComplete) {
			setTranslations(translations);
		} else {
			Map<String, Language> languages = new HashMap<>();

			for (Translations translation : translations) {
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

		for (Translations translation : translations) {
			languages.put(translation.getLanguage().getCode(), translation.getLanguage());
		}

		this.languages = languages;
	}

	public void setTranslations(List<Translations> translations) {
		Map<String, Language> languages = new HashMap<>();

		for (Translations translation : translations) {
			if (translation.getComplete())
			{
				languages.put(translation.getLanguage().getCode(), translation.getLanguage());
			}
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

	public Boolean getCodaEnabled() { return codaEnabled; }

	public void setCodaEnabled(Boolean codaEnabled) { this.codaEnabled = codaEnabled; }

	public String getAnswerTitleStripInvalidXML(Answer answer) {
		return ConversionTools.removeInvalidHtmlEntities(getAnswerTitle(answer));
	}

	public String getAnswerTitle(Answer answer) {
		return SurveyHelper.getAnswerTitle(survey, answer, publicationMode);
	}

	public String getAnswerShortname(Answer answer) {
		String answerValue = answer.getValue();

		try {

			Element question = survey.getQuestionMapByUniqueId().get(answer.getQuestionUniqueId());

			if (question == null) {
				question = survey.getMatrixMapByUid().get(answer.getQuestionUniqueId());
			}

			if (question instanceof FreeTextQuestion) {
				return "";
			} else if (question instanceof ChoiceQuestion) {
				int possibleAnswerId = Integer.parseInt(answerValue);

				ChoiceQuestion choice = (ChoiceQuestion) question;

				if (choice.getPossibleAnswer(possibleAnswerId) != null) {
					return "(" + choice.getPossibleAnswer(possibleAnswerId).getShortname() + ")";
				} else {
					if (survey.getMissingElementsById().containsKey(possibleAnswerId)) {
						return "(" + survey.getMissingElementsById().get(possibleAnswerId).getShortname() + ")";
					}
				}

				if (answer.getPossibleAnswerUniqueId() != null) {
					PossibleAnswer pa = choice.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId());
					String res = pa != null ? pa.getShortname() : null;
					if (res != null)
						return "(" + res + ")";
				}

				return "";
			} else if (question instanceof Text || question instanceof Image || question instanceof EmptyElement) {
				// could be inside a matrix
				int possibleAnswerId = Integer.parseInt(answerValue);
				for (Element element : survey.getElements()) {
					if (element instanceof Matrix) {
						for (Element child : ((Matrix) element).getChildElements()) {
							if (child.getId().equals(possibleAnswerId)) {
								return "(" + child.getShortname() + ")";
							}
						}
					}
				}

			}

		} catch (Exception e) {
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
		if (languages == null)
			return null;

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

	public List<List<Element>> getPages() {
		ArrayList<List<Element>> pages = new ArrayList<>();

		Integer currentPage = 0;
		ArrayList<Element> currentList = new ArrayList<>();
		pages.add(currentList);
		
		List<Element> elements = null;
		if (forPDF) {
			elements = survey.getElements();
		} else {
			elements = survey.getElementsOrdered();
		}
		
		for (Element element : elements) {
			if (survey.getMultiPaging() && element instanceof Section && ((Section) element).getLevel() == 1) {
				if (currentPage == 0 && currentList.isEmpty()) {
					// this is the very first sections -> no need to break
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

	public int getRatingValue(Element question) {
		if (!answerSets.isEmpty()) {
			List<Answer> answers = answerSets.get(0).getAnswers(question.getUniqueId());
			if (!answers.isEmpty()) {
				String result = answers.get(0).getValue();
				result = result.substring(0, result.indexOf('/'));
				return Integer.parseInt(result);
			}
		}

		return 0;
	}

	public String getValue(Element question) {
		if (!answerSets.isEmpty()) {
			List<Answer> answers = answerSets.get(0).getAnswers(question.getUniqueId());
			if (!answers.isEmpty()) {

				String result = answers.get(0).getValue();

				if (result != null && result.length() > 0 && question instanceof FreeTextQuestion) {
					FreeTextQuestion q = (FreeTextQuestion) question;
					if (q.getIsPassword()) {
						return "********";
					}
				}

				return result;
			}
		}

		return "";
	}
	
	public List<RankingItem> getRankingItems(Element question) {
		List<RankingItem> result = new ArrayList<>();
		RankingQuestion rankingQuestion = (RankingQuestion)question;
		
		if (!answerSets.isEmpty()) {
			String value = getValue(question);
						
			if (value != null && value.length() > 0) {
				Map<String, RankingItem> children = rankingQuestion.getChildElementsByUniqueId();
				String[] answerids = value.split(";");						
				for (String uniqueId : answerids)
				{
					RankingItem child = children.get(uniqueId);
					if (child != null)
					{
						result.add(child);
					}							
				}
				
				return result;				
			}
		}
		
		return rankingQuestion.getOrderedChildElements();
	}

	private Set<String> passwordQuestions = null;

	public Set<String> getPasswordQuestions() {
		if (passwordQuestions == null) {
			passwordQuestions = new HashSet<>();
			for (Element question : survey.getElements()) {
				if (question instanceof FreeTextQuestion && ((FreeTextQuestion) question).getIsPassword()) {
					passwordQuestions.add(question.getUniqueId());
				}
			}
		}
		return passwordQuestions;
	}

	public List<String> getValues(Element question) {
		if (!answerSets.isEmpty()) {
			List<Answer> answers = answerSets.get(0).getAnswers(question.getUniqueId());
			List<String> result = new ArrayList<>();
			for (Answer answer : answers) {
				result.add(answer.getValue());
				if (answer.getPossibleAnswerUniqueId() != null) {
					result.add(answer.getPossibleAnswerUniqueId());
				}
			}
			return result;
		}

		return new ArrayList<>();
	}

	public String getSectionTitle(Section section) {
		String prefix = getSectionNumbering(section);

		if (prefix.length() > 0) {
			if (section.getTitle().startsWith("<p")) {
				int index = section.getTitle().indexOf('>');
				return section.getTitle().substring(0, index + 1) + "<span class='numbering'>" + prefix + "</span>"
						+ section.getTitle().substring(index + 1);
			} else {
				return "<span class='numbering'>" + prefix + "</span>" + section.getTitle();
			}
		} else {
			return section.getTitle();
		}

	}

	public String getCleanSectionTitle(Section section) {
		String prefix = getSectionNumbering(section);

		if (prefix.length() > 0) {

			return prefix + " " + ConversionTools.removeHTML(section.getTitle(), false);

		} else {
			return ConversionTools.removeHTML(section.getTitle(), false);
		}
	}

	private String getSectionNumbering(Section section) {

		if (section == null || survey.getSectionNumbering() == 0)
			return "";

		String result = "";
		Numbering counter = new Numbering();

		for (Element element : survey.getElements()) {
			if (element instanceof Section) {
				Section s = (Section) element;

				if (s.getLevel() <= section.getLevel()) {
					// if section is deeper than s
					switch (s.getLevel()) {
						case 1:
							counter.setN1(counter.getN1() + 1);
							break;
						case 2:
							counter.setN2(counter.getN2() + 1);
							break;
						case 3:
							counter.setN3(counter.getN3() + 1);
							break;
						case 4:
							counter.setN4(counter.getN4() + 1);
							break;
						case 5:
							counter.setN5(counter.getN5() + 1);
							break;
						default:
							break;
					}
				}

				if (s.getId().equals(section.getId())) {
					return counter.getCounter(survey.getSectionNumbering(), section.getLevel());
				}

			}
		}

		return result;
	}

	public String getCleanQuestionTitle(Element element) {
		return ConversionTools.removeHTML(getQuestionTitle(element), true);
	}

	public String getQuestionTitle(Element question) {
		String title = question.getTitle();
		StringBuilder titlePrefix = new StringBuilder();

		if (title == null)
			title = "";

		if (question instanceof Confirmation) {
			if (!answerSets.isEmpty() && !answerSets.get(0).getAnswers(question.getUniqueId()).isEmpty()) {
				if (forPDF) {
					titlePrefix.append("<img align='middle' style='margin-right: 7px;' src='").append(contextpath)
							.append("/resources/images/checkboxchecked.png' />");
				} else {
					titlePrefix.append("<input type='checkbox' checked='checked' class='required check' name='answer")
							.append(question.getId()).append("' aria-labelledby='questiontitle").append(question.getId()).append("' /> ");
				}
			} else {
				if (forPDF) {
					titlePrefix.append("<img align='middle' style='margin-right: 7px;' src='").append(contextpath)
							.append("/resources/images/checkbox.png' />");
				} else {
					titlePrefix.append("<input type='checkbox' class='required check' name='answer")
							.append(question.getId()).append("' aria-labelledby='questiontitle").append(question.getId()).append("' /> ");
				}
			}
		}

		if (survey.getQuestionNumbering() == 0) {

			if (title.startsWith("<p")) {
				int index = title.indexOf('>');
				return title.substring(0, index + 1) + titlePrefix + " " + title.substring(index + 1);
			} else {
				return titlePrefix + title;
			}
		}

		Section lastSection = null;
		int counter = 0;

		for (Element element : survey.getElements()) {
			if (element instanceof Section) {
				lastSection = (Section) element;
				if (survey.getQuestionNumbering() < 4) {
					counter = 0;
				}
			}

			if (element instanceof Question && !(element instanceof Text) && !(element instanceof Image)
					&& !(element instanceof Ruler) && !(element instanceof Confirmation)) {
				counter++;
				Question q = (Question) element;
				if (q.getId().equals(question.getId())) {
					String result = "";

					if (survey.getQuestionNumbering() < 4) {
						result = getSectionNumbering(lastSection);
						if (result.length() > 0)
							result += ".";
					}

					switch (survey.getQuestionNumbering()) {
						case 1:
						case 4:
							titlePrefix.append("<span class='numbering'>").append(result).append(counter).append("</span>");
							break;
						case 2:
						case 5:
							titlePrefix.append("<span class='numbering'>").append(result)
									.append(Numbering.getSmallLetter(counter)).append("</span>");
							break;
						case 3:
						case 6:
							titlePrefix.append("<span class='numbering'>").append(result)
									.append(Numbering.getBigLetter(counter)).append("</span>");
							break;
						default:
							break;
					}
				}
			}
		}

		String result;

		if (title.startsWith("<p")) {
			int index = title.indexOf('>');
			result = title.substring(0, index + 1) + titlePrefix + " " + title.substring(index + 1);
		} else {
			result = titlePrefix + title;
		}

		return result;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}

	public Map<Element, String> getValidation() {
		return validation;
	}

	public void setValidation(Map<Element, String> validation) {
		this.validation = validation;
	}

	public String getValidationMessage(Element element) {
		try {
			for (Entry<Element, String> entry : validation.entrySet()) {
				if (entry.getKey() != null && element != null && !(entry.getKey() instanceof DraftIDElement)
						&& entry.getKey().getId().equals(element.getId()))
					return entry.getValue();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return "";
	}

	public Set<Element> getValidationMessageElements() {
		return validation.keySet();
	}

	public String getValidationDraftID() {
		for (Entry<Element, String> entry : validation.entrySet()) {
			if (entry.getKey() instanceof DraftIDElement) {
				return entry.getValue();
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
		if (languages != null) {
			for (Language language : languages.values()) {
				if (language.getCode().equalsIgnoreCase(lang)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getMessage(String key) {
		return getInternalMessage(key, null);
	}

	public String getMessage(String key, String arg1) {
		return getInternalMessage(key, new Object[] { arg1 });
	}

	public String getMessage(String key, String arg1, String arg2) {
		return getInternalMessage(key, new Object[] { arg1, arg2 });
	}

	private String getInternalMessage(String key, Object[] args) {
		String locale = "en";

		if (language != null) {
			locale = language.getCode().toLowerCase();
		} else if (this.survey != null && this.survey.getLanguage() != null) {
			locale = this.survey.getLanguage().getCode().toLowerCase();
		}
		return resources.getMessage(key, args, key, new Locale(locale));
	}

	public MessageSource getResources() {
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

	public boolean getWcagCompliance() {
		if (wcagCompliance != null)
			return wcagCompliance;
		if (survey != null)
			return survey.getWcagCompliance();
		return false;
	}

	public String maxColumnWidth(Element e) {
		if (e instanceof SingleChoiceQuestion) {
			SingleChoiceQuestion s = (SingleChoiceQuestion) e;
			if (s.getNumColumns() > 1) {
				return 800 / s.getNumColumns() + "px";
			}
		} else if (e instanceof MultipleChoiceQuestion) {
			MultipleChoiceQuestion m = (MultipleChoiceQuestion) e;
			if (m.getNumColumns() > 1) {
				return 800 / m.getNumColumns() + "px";
			}
		}

		return "none";
	}

	@DateTimeFormat(pattern = ConversionTools.DateTimeFormatJS)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@DateTimeFormat(pattern = ConversionTools.DateTimeFormatJS)
	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	
	public long getPassedTimeInSeconds() {
		if (this.startDate == null || this.currentDate == null) {
			return 0;
		}
		
		long milliSeconds = this.currentDate.getTime() - this.startDate.getTime();		
		return milliSeconds / 1000;
	}
	
	/**
	 * Replaces the Hypertext Markup from the Confirmation Page text with the corresponding values at the end of a survey.
	 * Possible Markups: {InvitationNumber} {ContributionID} {UserName} {CreationDate} {LastUpdate} {Language} Question - {IDs}
	 * @return String with all Markups replaced
	 */
	public String replacedMarkupConfirmationPage() {

		if(getAnswerSets().size() <= 0) {
			return survey.getConfirmationPage();
		}

		String confPageText = survey.getConfirmationPage();
		List<List<Element>> pages = getPages();

		if(confPageText.length() <= 0)
			return "";

		Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(confPageText);
		int lastIndex = 0;
		StringBuilder replacedMsg = new StringBuilder();

		while (m.find()) {
			String match = m.group(1);
			switch(match){
				case "InvitationNumber":
					replacedMsg.append(confPageText, lastIndex, m.start()).append(replaceNullByHyphen(getAnswerSets().get(0).getInvitationId()));
					break;
				case "ContributionID":
					replacedMsg.append(confPageText, lastIndex, m.start()).append(replaceNullByHyphen(getAnswerSets().get(0).getUniqueCode()));
					break;
				case "UserName":
					replacedMsg.append(confPageText, lastIndex, m.start()).append(replaceNullByHyphen(getAnswerSets().get(0).getResponderEmail()));
					break;
				case "CreationDate":
					replacedMsg.append(confPageText, lastIndex, m.start()).append(Tools.formatDate(getAnswerSets().get(0).getDate(), ConversionTools.DateFormat));
					break;
				case "LastUpdate":
					replacedMsg.append(confPageText, lastIndex, m.start()).append(Tools.formatDate(getAnswerSets().get(0).getUpdateDate(), ConversionTools.DateFormat));
					break;
				case "Language":
					replacedMsg.append(confPageText, lastIndex, m.start()).append(getAnswerSets().get(0).getLanguageCode());
					break;
				default:
					// is ID?
					for(List<Element> le : pages){
						for(Element e : le){
							switch(e.getType()){
								case "Matrix":
									for(Element c : ((Matrix) e).getQuestions()){
										if(c.getShortname().equals(match))
											replacedMsg.append(confPageText, lastIndex, m.start()).append(getQuestionResult(c, true));
									}
									break;
								case "Table":
									MatrixOrTable table = (MatrixOrTable) e;
									List<Element> tableQuestions = (table.getQuestions());
									for(int i = 0; i < tableQuestions.size(); i++){
										if(tableQuestions.get(i).getShortname().equals(match)){
											AnswerSet as =  getAnswerSets().get(0);
											String res = "";
											for(int l = 1; l < table.getColumns(); l++){
												if(l > 1) res += "; ";
												String cellAnswer = as.getTableAnswer(e,i+1, l, false);
											    res += (cellAnswer == "" || cellAnswer == null) ? "-" : cellAnswer;
											}
											replacedMsg.append(confPageText, lastIndex, m.start()).append(res);    
										}
									}
									break;
								case "ComplexTable":
									for(ComplexTableItem cti : ((ComplexTable) e).getChildElements()){
										if(cti.getShortname().equals(match))
											replacedMsg.append(confPageText, lastIndex, m.start()).append(getQuestionResult(cti, false));
									}
									break;
								case "RatingQuestion":
									for(Element rq : ((RatingQuestion) e).getChildElements()){
										if(rq.getShortname().equals(match))
											replacedMsg.append(confPageText, lastIndex, m.start()).append(getQuestionResult(rq, false));
									}
									break;
								default:
									if(e.getShortname().equals(match))
										replacedMsg.append(confPageText, lastIndex, m.start()).append(getQuestionResult(e, false));
									break;
							}
						}
					}
					break;
			}
			lastIndex = m.end();
		}

		// add text after last replacement
		if(lastIndex <= confPageText.length());
			replacedMsg.append(confPageText, lastIndex, confPageText.length());

		return replacedMsg.toString();
	}
	
	private String replaceNullByHyphen(String value) {
		if (value == null) return "-";
		return value;
	}
	
	/**
	 * Given an Element e, the answers from the user get returned in the correct format from the saved answerset.
	 * @param e the survey element we retrieve the answer from.
	 * @param f the form we retrieve the answers from.
	 * @param isMatrix Element is a Matrix?
	 * @return Result for a given Question Element in the correct format for the contribution page.
	 */
	private String getQuestionResult(Element e, Boolean isMatrix) {

		List<Answer> as = getAnswerSets().get(0).getAnswers(e.getUniqueId());
		String result = "";

		switch(e.getType()){
			case "SingleChoiceQuestion":
			case "MultipleChoiceQuestion":
			case "ComplexTableItem":
				return as.size() > 0 ? ConversionTools.removeHTML(constructAnswerString(as, "; "), true) : "-";
			case "Upload":
				return as.size() > 0 ? as.get(0).getFiles().get(0).getName() : "-";
			case "GalleryQuestion":
				if(as.size() == 0) return "-";

				List<File> files = ((GalleryQuestion) e).getFiles();
				result = "";
				for(int i = 0; i < as.size(); i++){
					if(i > 0) result = result + "; ";
					result = result + files.get(Integer.parseInt(getAnswerTitle(as.get(i)))).getName();
				}
				return ConversionTools.removeHTML(result, true);
			case "Text":
				// Matrix Question
				if(isMatrix)
					return as.size() > 0 ? constructAnswerString(as, "; ") : "-";

				// Ranking Question
				return as.size() > 0 ? as.get(0).getValue() : "-";
			case "RankingQuestion":
				if(as.size() <= 0) {
					return "-";
				}
				return getAnswerTitle(as.get(0));
			default:
				return as.size() > 0 ? ConversionTools.removeHTML(getAnswerTitle(as.get(0)), true) : "-";
		}
	}

	private String constructAnswerString(List<Answer> as, String separator) {
		String res = "";
		for(int i = 0; i < as.size(); i++){
			if(i>0) res = res + separator;
			res = res + getAnswerTitle(as.get(i));
		}
		return res;
	}
}
