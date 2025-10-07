package com.ec.survey.model.survey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.ec.survey.model.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.SurveyService;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.util.HtmlUtils;

/**
 * This class represents a survey. It contains a list of elements that represent
 * the different form elements, like questions or sections.
 */
@Entity
@Table(name = "SURVEYS", indexes = { @Index(name = "SH_IDX", columnList = "SURVEYNAME"),
		@Index(name = "DRA_IDX", columnList = "ISDRAFT") })
@Cacheable("com.ec.survey.model.survey.Survey")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
final public class Survey implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TITLE = "TITLE";
	public static final String ESCAPEPAGE = "ESCAPEPAGE";
	public static final String ESCAPELINK = "ESCAPELINK";
	public static final String CONFIRMATIONPAGE = "CONFIRMATIONPAGE";
	public static final String CONFIRMATIONLINK = "CONFIRMATIONLINK";
	public static final String INTRODUCTION = "INTRODUCTION";
	public static final String LOGOTEXT = "LOGOTEXT";
	public static final String HELP = "HELP";
	public static final String QUIZWELCOMEMESSAGE = "QUIZWELCOMEMESSAGE";
	public static final String QUIZRESULTSMESSAGE = "QUIZRESULTSMESSAGE";

	public static final String CONFIRMATIONTEXT = "<span style=\"color: #4caf50; font-size: 200%; font-weight: bold;\">✓</span> <strong style=\"color: black; margin-left: 6px;\"> Contribution successfully submitted</strong><br /><br />Thank you for your contribution!";
	public static final String ESCAPETEXT = "This survey has not yet been published or has already been unpublished in the meantime.";
	public static final String RESULTSMESSAGETEXT = "Thank you for your contribution";

	public static final String MAXNUMBEROFRESULTSTEXT = "This survey has been closed due to the maximum number of contributions reached.";
	public static final String MOTIVATIONPOPUPTEXT = "Motivation Text";
	public static final Integer MOTIVATIONPOPUPPROGRESS  = 50;
	public static final Integer MOTIVATIONPOPUPTIME = 20;

	public enum ReportEmailFrequency
	{
		Never, Daily, Weekly, Monthly
	}

	private Integer id;
	private String uniqueId;
	private int version;
	private int DBVersion;
	private User owner;
	private String password;
	private String title;
	private String titleSort;
	private String shortname;
	private String introduction;
	private Language language;
	private String notificationValue;
	private String notificationUnit;
	private boolean notifyAll;
	private boolean notified;
	private Boolean confirmationPageLink = false;
	private Boolean escapePageLink = false;
	private List<Element> elements = new ArrayList<>();
	private List<Element> missingElements = new ArrayList<>();
	private Map<String, String> usefulLinks = new HashMap<>();
	private Map<String, String> backgroundDocuments = new HashMap<>();
	private Map<String, String> fileNamesForBackgroundDocuments = new HashMap<>();
	private boolean listForm;
	private boolean listFormValidated;
	private Date publicationRequestedDate;
	private String contact;
	private String contactLabel;
	private String security;
	private Date created;
	private Date published;
	private Date firstPublished;
	private String createdString;
	private String startString;
	private String endString;
	private String deletedString;
	private Date updated;
	private Date start;
	private Date end;
	private Date deleted;
	private int numberOfAnswerSets;
	private int numberOfDrafts;
	private int numberOfInvitations;
	private int numberOfAnswerSetsPublished;
	private int numberOfReports;
	private int compulsoryStyle;
	private boolean isActive; // = is currently published
	private boolean isDraft;
	private boolean isPublished; // = was published at least once
	private boolean hasPendingChanges;
	private boolean validatedPerPage;
	private Boolean saveAsDraft;
	private boolean automaticPublishing;
	private boolean changeContribution;
	private boolean downloadContribution;
	private boolean registrationForm;
	private boolean multiPaging;
	private boolean captcha;
	private boolean missingElementsChecked;
	private int sectionNumbering;
	private int questionNumbering;
	private Skin skin;
	private File logo;
	private String confirmationPage = CONFIRMATIONTEXT;
	private String escapePage = ESCAPETEXT;
	private String confirmationLink = "";
	private String escapeLink = "";
	private List<String> translations;
	private List<Language> completeTranslations;
	private Publication publication = new Publication();
	private Map<Integer, String[]> activitiesToLog = new HashMap<>();
	private String audience;
	private boolean wcagCompliance;
	private boolean isArchived;
	private Boolean isDeleted;
	private Boolean isFrozen;
	private boolean ecasSecurity;
	private String ecasMode;
	private Boolean logoInInfo = true;
	private Boolean isQuiz;
	private Boolean isDelphi;
	private Boolean isOPC;
	private Boolean isECF;
	private Boolean isSelfAssessment;
	private Boolean isEVote;
	private Boolean showResultsTestPage = false;
	private Boolean showQuizIcons;
	private Boolean showTotalScore;
	private Boolean scoresByQuestion;
	private String quizWelcomeMessage;
	private String quizResultsMessage = RESULTSMESSAGETEXT;
	private Boolean showPDFOnUnavailabilityPage;
	private Boolean showDocsOnUnavailabilityPage;
	private Boolean allowQuestionnaireDownload;
	private boolean fullFormManagementRights = true;
	private boolean formManagementRights = true;
	private boolean accessResultsRights = true;
	private Integer allowedContributionsPerUser = 1;
	private boolean canCreateSurveys = true;
	private Integer trustScore;
	private Boolean isUseMaxNumberContribution = false;
	private String maxNumberContributionText = MAXNUMBEROFRESULTSTEXT;
	private Long maxNumberContribution = 0L;
	private Boolean isUseMaxNumberContributionLink = false;
	private String maxNumberContributionLink = "";
	private Boolean sendConfirmationEmail = false;
	private Boolean sendReportEmail = false;
	private ReportEmailFrequency reportEmailFrequency = ReportEmailFrequency.Never;
	private String reportEmails = "";		// String with mails separated by semicolons to avoid needing to store mails in separate table
	private Boolean isDelphiShowAnswersAndStatisticsInstantly = false;
	private Boolean isDelphiShowStartPage = true;
	private Boolean isDelphiShowAnswers = false;
	private Integer minNumberDelphiStatistics = 1;
	private String logoText = "";
	private Boolean isShowCountdown = true;
	private String timeLimit;
	private boolean preventGoingBack = false;
	private Boolean criticalComplexity = false;
	private Boolean dedicatedResultPrivileges = false;
	private Boolean progressBar = false;
	private Integer progressDisplay = 0;
	private Boolean motivationPopup = false;
	private Boolean motivationType = false;
	private String motivationText = MOTIVATIONPOPUPTEXT;
	private String motivationPopupTitle = "";
	private Integer motivationTriggerProgress = MOTIVATIONPOPUPPROGRESS;
	private Integer motivationTriggerTime = MOTIVATIONPOPUPTIME;
	private String codaLink;
	private boolean codaWaiting = false;
	private String eVoteTemplate = "";
	private Integer quorum = 6666;
	private Integer minListPercent = 5;
	private Integer maxPrefVotes;
	private Integer seatsToAllocate;
	private String organisation;
	private String validator;
	private String validationCode;
	private Boolean validated;
	private String webhook = "";
	private List<Tag> tags = new ArrayList<>();
	private Boolean doNotDelete;

	@Id
	@Column(name = "SURVEY_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "SURVEY_VERSION")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version != null ? version : 0;
	}

	@Column(name = "SURVEY_UID", nullable = false)
	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Column(name = "DBVERSION")
	public int getDBVersion() {
		return DBVersion;
	}

	public void setDBVersion(int DBVersion) {
		this.DBVersion = DBVersion;
	}

	@Lob
	@Column(name = "TITLE", nullable = false)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		String t = ConversionTools.removeHTML(title);
		this.titleSort = t.length() > 100 ? t.substring(0, 100) : t;
	}

	@Column(name = "TITLESORT", nullable = false)
	public String getTitleSort() {
		return titleSort;
	}

	public void setTitleSort(String titleSort) {
		this.titleSort = titleSort;
	}

	@Column(name = "PASSWORD")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Lob
	@Column(name = "CONFIRMATION", nullable = false, length = 40000)
	public String getConfirmationPage() {
		return this.confirmationPage != null && this.confirmationPage.length() > 0
				? this.confirmationPage
				: CONFIRMATIONTEXT;
	}

	public void setConfirmationPage(String confirmationPage) {
		this.confirmationPage = confirmationPage != null ? Tools.filterHTML(confirmationPage)
				: CONFIRMATIONTEXT;
	}

	@Column(name = "CONFLINK")
	public Boolean getConfirmationPageLink() {
		return confirmationPageLink;
	}

	public void setConfirmationPageLink(Boolean confirmationPageLink) {
		this.confirmationPageLink = confirmationPageLink;
	}

	@Lob
	@Column(name = "ESCAPE", nullable = false, length = 40000)
	public String getEscapePage() {
		return this.escapePage != null && this.escapePage.length() > 0
				? this.escapePage
				: ESCAPETEXT;
	}

	public void setEscapePage(String escapePage) {
		this.escapePage = escapePage != null ? Tools.filterHTML(escapePage)
				: ESCAPETEXT;
	}

	@Column(name = "ESCLINK")
	public Boolean getEscapePageLink() {
		return escapePageLink;
	}

	public void setEscapePageLink(Boolean escapePageLink) {
		this.escapePageLink = escapePageLink;
	}

	@Column(name = "CONFURL")
	public String getConfirmationLink() {
		if (confirmationLink != null && confirmationLink.length() > 0
				&& !confirmationLink.toLowerCase().startsWith("http")) {
			confirmationLink = "http://" + confirmationLink;
		}
		return confirmationLink;
	}

	public void setConfirmationLink(String confirmationLink) {
		this.confirmationLink = confirmationLink;
	}

	@Column(name = "ESCURL")
	public String getEscapeLink() {
		if (escapeLink != null && escapeLink.length() > 0 && !escapeLink.toLowerCase().startsWith("http")) {
			escapeLink = "http://" + escapeLink;
		}
		return escapeLink;
	}

	public void setEscapeLink(String escapeLink) {
		this.escapeLink = escapeLink;
	}

	@ManyToOne
	@JoinColumn(name = "LANGUAGE", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SURVEY_CREATED", nullable = false)
	@DateTimeFormat(pattern = ConversionTools.DateFormat)
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
		this.createdString = Tools.formatDate(created, ConversionTools.DateFormat);
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Transient
	@DateTimeFormat(pattern = ConversionTools.DateFormat)
	public Date getPublished() {
		return published;
	}

	public void setPublished(Date published) {
		this.published = published;
	}

	@Transient
	public String getNicePublished() {
		return published != null ? Tools.formatDate(published, ConversionTools.DateFormat) : "";
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Transient
	@DateTimeFormat(pattern = ConversionTools.DateFormat)
	public Date getFirstPublished() {
		return firstPublished;
	}

	public void setFirstPublished(Date firstPublished) {
		this.firstPublished = firstPublished;
	}

	@Transient
	public String getNiceFirstPublished() {
		return firstPublished != null ? Tools.formatDate(firstPublished, ConversionTools.DateFormat) : "";
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SURVEY_UPDATED", nullable = false)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SURVEY_START_DATE")
	@DateTimeFormat(pattern = ConversionTools.DateTimeFormatSmall)
	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
		this.startString = ConversionTools.getFullStringSmall(start);
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = ConversionTools.DateTimeFormatSmall)
	@Column(name = "SURVEY_END_DATE")
	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
		this.endString = ConversionTools.getFullStringSmall(end);
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = ConversionTools.DateTimeFormatSmall)
	@Column(name = "SURVEY_DELETED")
	public Date getDeleted() {
		return deleted;
	}

	public void setDeleted(Date deleted) {
		this.deleted = deleted;
		this.deletedString = Tools.formatDate(deleted, ConversionTools.DateFormat);
	}

	@ManyToOne
	@JoinColumn(name = "OWNER", nullable = false)
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@ManyToOne
	@JoinColumn(name = "SURVEYSKIN")
	public Skin getSkin() {
		return skin;
	}

	public void setSkin(Skin skin) {
		this.skin = skin;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "LOGO")
	public File getLogo() {
		return logo;
	}

	public void setLogo(File logo) {
		this.logo = logo;
	}

	// supposed to be the short name
	@Column(name = "SURVEYNAME", nullable = false)
	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	@Lob
	@Column(name = "INTRODUCTION", length = 40000) // , columnDefinition = "TEXT")
	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	@Column(name = "USEFULLINKS")
	@ElementCollection()
	public Map<String, String> getUsefulLinks() {
		return usefulLinks;
	}

	public void setUsefulLinks(Map<String, String> usefulLinks) {
		this.usefulLinks = usefulLinks;
	}

	@Transient
	public LinkedHashMap<String, String> getAdvancedUsefulLinks() {
		LinkedHashMap<String, String> result = new LinkedHashMap<>();

		SortedMap<Integer, String> sortedUsefullinks = new TreeMap<>();
		boolean skip = false;
		for (String key : usefulLinks.keySet()) {
			if (key.contains("#")) {
				try {
					int i = Integer.parseInt(key.substring(0, key.indexOf('#')));
					sortedUsefullinks.put(i, key);
				} catch (Exception e) {
					skip = true;
					break;
				}
			} else {
				skip = true;
				break;
			}
		}
		if (!skip) {
			for (String key : sortedUsefullinks.values()) {
				String value = usefulLinks.get(key);

				if (value != null && value.length() > 0 && !value.toLowerCase().startsWith("http")) {
					value = "http://" + value;
				}

				String label = key.substring(key.indexOf('#') + 1);

				if (label.trim().length() == 0 || org.apache.commons.lang3.StringUtils.isNumeric(label)) {
					label = value;
				}

				result.put(label, value);
			}
			return result;
		}

		for (Entry<String, String> entry : usefulLinks.entrySet()) {
			String value = entry.getValue();
			if (value != null && value.length() > 0 && !value.toLowerCase().startsWith("http")) {
				value = "http://" + value;
			}
			result.put(entry.getKey(), value);
		}
		return result;
	}

	@Column(name = "BACKGROUNDDOCUMENTS")
	@ElementCollection()
	public Map<String, String> getBackgroundDocuments() {
		return backgroundDocuments;
	}

	public void setBackgroundDocuments(Map<String, String> backgroundDocuments) {
		this.backgroundDocuments = backgroundDocuments;
	}

	@Transient
	public Map<String, String> getBackgroundDocumentsAlphabetical() {
		return new TreeMap<>(backgroundDocuments);
	}

	@OneToMany(targetEntity = Element.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
			joinColumns = @JoinColumn(name = "SURVEYS_SURVEY_ID"),
			inverseJoinColumns = @JoinColumn(name = "elements_ID"))
	@Fetch(value = FetchMode.SELECT)
	@OrderBy(value = "position asc")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}
	
	@Transient
	public List<Element> getElementsOrdered() {
		List<Element> elements = new ArrayList<>();
		
		Section currentSection = null;
		List<Element> currentSectionElements = new ArrayList<>();
		for (Element element : getElements()) {
			boolean skip = false;
			if (element instanceof Section) {
				Section section = (Section)element;
				if (section.getLevel().equals(1) && section.getOrder().equals(1)) {
					copyOrderedElements(currentSectionElements, elements);
										
					currentSection = section;
					currentSectionElements = new ArrayList<>();
					elements.add(section);
					skip = true;
				} else if (section.getLevel().equals(1)) {
					copyOrderedElements(currentSectionElements, elements);
					currentSectionElements = new ArrayList<>();
					currentSection = null;
				} else if (currentSection != null) {
					// this means we have a sub-section inside a section that is randomized
					// the structure should be the same, so we only randomize until a sub-section occurs
					copyOrderedElements(currentSectionElements, elements);
					currentSectionElements = new ArrayList<>();
					elements.add(section);
					skip = true;
				}
			}
			
			if (!skip) {
				if (currentSection == null) {
					elements.add(element);
				} else {
					currentSectionElements.add(element);
				}
			}			
		}
		
		copyOrderedElements(currentSectionElements, elements);
		
		return elements;
	}
	
	private void copyOrderedElements(List<Element> sectionElements, List<Element> result) {
		List<Element> elementsToRandomize = new ArrayList<>();
		if (!sectionElements.isEmpty()) {
			for (Element element : sectionElements) {
				if (element.getIsTriggerOrDependent()) {
					result.add(element);
				} else {
					elementsToRandomize.add(element);
				}
			}
			
			if (!elementsToRandomize.isEmpty()) {
				Collections.shuffle(elementsToRandomize);
				
				for (Element element : elementsToRandomize) {
					result.add(element);
				}
			}
		}
	}

	@Transient
	public List<Element> getMissingElements() {
		return missingElements;
	}

	public void setMissingElements(List<Element> missingElements) {
		this.missingElements = missingElements;
		missingElementsById = null;
	}

	private Map<Integer, Element> missingElementsById;

	@Transient
	public Map<Integer, Element> getMissingElementsById() {

		if (missingElementsById != null)
			return missingElementsById;

		missingElementsById = new HashMap<>();
		for (Element newElement : missingElements) {
			if (newElement != null) {
				missingElementsById.put(newElement.getId(), newElement);
			}
			if (newElement instanceof MatrixOrTable) {
				for (Element child : ((MatrixOrTable) newElement).missingAnswers) {
					missingElementsById.put(child.getId(), newElement);
				}
			}
		}
		return missingElementsById;
	}

	private Map<String, Element> missingElementsByUniqueId;

	@Transient
	public Map<String, Element> getMissingElementsByUniqueId() {

		if (missingElementsByUniqueId != null)
			return missingElementsByUniqueId;

		missingElementsByUniqueId = new HashMap<>();
		for (Element newElement : missingElements) {
			missingElementsByUniqueId.put(newElement.getUniqueId(), newElement);

			if (newElement instanceof MatrixOrTable) {
				for (Element child : ((MatrixOrTable) newElement).missingAnswers) {
					missingElementsById.put(child.getId(), newElement);
				}
			}
		}
		return missingElementsByUniqueId;
	}

	public void resetElementsRecursive() {
		elementsRecursive = null;
		elementsRecursiveWithAnswers = null;
	}

	@Transient
	public List<Element> getElementsRecursive() {
		return getElementsRecursive(false);
	}

	private List<Element> elementsRecursive = null;
	private List<Element> elementsRecursiveWithAnswers = null;

	@Transient
	public List<Element> getElementsRecursive(boolean answers) {
		if (answers) {
			if (elementsRecursiveWithAnswers != null) {
				return elementsRecursiveWithAnswers;
			}
		} else {
			if (elementsRecursive != null) {
				return elementsRecursive;
			}
		}

		elementsRecursive = new ArrayList<>();
		elementsRecursiveWithAnswers = new ArrayList<>();

		for (Element element : elements) {
			elementsRecursive.add(element);
			elementsRecursiveWithAnswers.add(element);
			if (element instanceof MatrixOrTable) {
				elementsRecursive.addAll(((MatrixOrTable) element).getChildElements());
				elementsRecursiveWithAnswers.addAll(((MatrixOrTable) element).getChildElements());
			}
			if (element instanceof ChoiceQuestion) {
				elementsRecursiveWithAnswers.addAll(((ChoiceQuestion) element).getPossibleAnswers());
			}
			if (element instanceof RatingQuestion) {
				elementsRecursive.addAll(((RatingQuestion) element).getChildElements());
				elementsRecursiveWithAnswers.addAll(((RatingQuestion) element).getChildElements());
			}
			if (element instanceof RankingQuestion) {
				elementsRecursive.addAll(((RankingQuestion) element).getChildElements());
				elementsRecursiveWithAnswers.addAll(((RankingQuestion) element).getChildElements());
			}
			if (element instanceof ComplexTable) {
				ComplexTable table = (ComplexTable) element;
				elementsRecursive.addAll(table.getChildElements());
				elementsRecursiveWithAnswers.addAll(table.getChildElements());
				for (ComplexTableItem item : table.getChildElements()) {
					elementsRecursiveWithAnswers.addAll(item.getPossibleAnswers());
				}
			}
		}

		if (answers) {
			return elementsRecursiveWithAnswers;
		} else {
			return elementsRecursive;
		}
	}
	
	@Transient
	public Map<Element, List<Element>> getElementsForResultAccessFilter() {
		Map<Element, List<Element>> result = new LinkedHashMap<>();
		
		for (Element element : elements) {
			if (element instanceof MatrixOrTable || element instanceof Section || element instanceof Image || element instanceof Download || element instanceof Upload || element instanceof Ruler|| element instanceof Text || element instanceof RankingQuestion || element instanceof RatingQuestion || element instanceof Confirmation || element instanceof GalleryQuestion) {
				continue;
			}
			
			if (element instanceof ChoiceQuestion) {
				if ((element instanceof SingleChoiceQuestion) && (((SingleChoiceQuestion) element).getIsTargetDatasetQuestion())) {
					continue;
				}

				List<Element> children = new ArrayList<>();
				for (Element child : ((ChoiceQuestion) element).getPossibleAnswers()) {
					children.add(child);
				}
				result.put(element, children);
			} else if (element instanceof FreeTextQuestion || element instanceof RegExQuestion || element instanceof NumberQuestion || element instanceof DateQuestion || element instanceof TimeQuestion || element instanceof EmailQuestion) {
				result.put(element, new ArrayList<Element>());
			}	
		}
		
		return result;
	}	

	@Column(name = "LISTFORM")
	public boolean getListForm() {
		return listForm;
	}

	public void setListForm(boolean listForm) {
		this.listForm = listForm;
	}

	@Column(name = "LISTFORMVALIDATED")
	public boolean isListFormValidated() {
		return listFormValidated;
	}

	public void setListFormValidated(Boolean listFormValidated) {
		this.listFormValidated = listFormValidated != null ? listFormValidated : false;
	}

	@Column(name = "LISTFORMREQUESTEDDATE")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getPublicationRequestedDate() {
		return publicationRequestedDate;
	}

	public void setPublicationRequestedDate(Date publicationRequestedDate) {
		this.publicationRequestedDate = publicationRequestedDate;
	}

	@Column(name = "CONTACT")
	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Lob
	@Column(name = "MOTIVATIONSTEXT", nullable = false)
	public String getMotivationText() {
		return this.motivationText != null && this.motivationText.length() > 0
				? this.motivationText
				: MOTIVATIONPOPUPTEXT;
	}

	public void setMotivationText(String motivationText) {
		this.motivationText = motivationText != null ? Tools.filterHTML(motivationText)
				: MOTIVATIONPOPUPTEXT;
	}

	@Column(name = "MOTIVATIONTITLE")
	public String getMotivationPopupTitle() {
		if (this.motivationPopupTitle == null){
			return "";
		}
		return this.motivationPopupTitle;
	}

	public void setMotivationPopupTitle(String motivationTitle) {
		if (motivationTitle == null){
			motivationTitle = "";
		}
		this.motivationPopupTitle = motivationTitle;
	}


	@Column(name = "MOTIVATIONTRIGGERPROGRESS")
	public Integer getMotivationTriggerProgress() {
		return this.motivationTriggerProgress != null && this.motivationTriggerProgress > 0
				? this.motivationTriggerProgress
				: MOTIVATIONPOPUPPROGRESS;
	}

	public void setMotivationTriggerProgress(Integer motivationTriggerProgress) {
		this.motivationTriggerProgress = motivationTriggerProgress != null ? motivationTriggerProgress
				: MOTIVATIONPOPUPPROGRESS;
	}

	@Column(name = "MOTIVATIONTRIGGERTIME")
	public Integer getMotivationTriggerTime() {
		return this.motivationTriggerTime != null && this.motivationTriggerTime > 0
				? this.motivationTriggerTime
				: MOTIVATIONPOPUPTIME;
	}

	public void setMotivationTriggerTime(Integer motivationTriggerTime) {
		/*TODO  filter here for  valid input? */
		this.motivationTriggerTime = motivationTriggerTime != null ? motivationTriggerTime
				: MOTIVATIONPOPUPTIME;
	}

	@Column(name = "CONTACTLABEL")
	public String getContactLabel() {
		return contactLabel;
	}

	public void setContactLabel(String contactLabel) {
		this.contactLabel = contactLabel;
	}

	@Transient
	public String getNiceContact() {
		String result = "";
		if (contact != null && contact.contains("@")) {
			result += "Email: " + contact;
		} else {
			result += "Webpage: ";
			if (contactLabel != null && contactLabel.length() > 0) {
				result += contactLabel + ": " + contact;
			} else {
				result += contact;
			}
		}
		return result;
	}

	@Transient
	public String getFixedContact() {
		if (contact != null && contact.length() > 0 && !contact.contains("@")
				&& !contact.toLowerCase().startsWith("http")) {
			return "http://" + contact;
		}

		return contact;
	}

	@Transient
	public String getFixedContactLabel() {
		if (contactLabel != null && contactLabel.length() > 0 && contact != null && contact.length() > 0
				&& !contact.contains("@")) {
			return contactLabel;
		}
		return contact;
	}

	@Column(name = "SURVEYSECURITY")
	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	@Column(name = "ISDRAFT", nullable = false)
	public boolean getIsDraft() {
		return isDraft;
	}

	public void setIsDraft(boolean draft) {
		this.isDraft = draft;
	}

	@Column(name = "HASPENDINGCHANGES")
	public boolean getHasPendingChanges() {
		return hasPendingChanges;
	}

	public void setHasPendingChanges(boolean hasPendingChanges) {
		this.hasPendingChanges = hasPendingChanges;
	}

	@Column(name = "VALIDATEDPERPAGE")
	public boolean getValidatedPerPage() {
		return validatedPerPage && multiPaging;
	}

	public void setValidatedPerPage(boolean validatedPerPage) {
		this.validatedPerPage = validatedPerPage;
	}
	
	@Column(name = "PREVENTGOINGBACK")
	public Boolean getPreventGoingBack() {
		return preventGoingBack;
	}

	public void setPreventGoingBack(Boolean preventGoingBack) {
		this.preventGoingBack = preventGoingBack != null ? preventGoingBack : false;
	}	

	@Column(name = "MULTIPAGING")
	public boolean getMultiPaging() {
		return multiPaging;
	}

	public void setMultiPaging(boolean multiPaging) {
		this.multiPaging = multiPaging;
	}

	@Column(name = "WCAGCOMPLIANCE")
	public Boolean getWcagCompliance() {
		return wcagCompliance;
	}

	public void setWcagCompliance(Boolean wcagCompliance) {
		this.wcagCompliance = wcagCompliance != null && wcagCompliance;
	}

	@Column(name = "CAPTCHA")
	public boolean getCaptcha() {
		// always activate captcha for open surveys that are public
		if (this.listForm && this.security.startsWith("open")) {
			return true;
		}

		return captcha;
	}

	public void setCaptcha(boolean captcha) {
		this.captcha = captcha;
	}

	@Column(name = "AUTOMATICPUBLISHING")
	public boolean getAutomaticPublishing() {
		return automaticPublishing;
	}

	public void setAutomaticPublishing(boolean automaticPublishing) {
		this.automaticPublishing = automaticPublishing;
	}

	@Column(name = "ACTIVE")
	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive != null && isActive;
	}

	@Column(name = "CHANGECONTRIBUTION")
	public boolean getChangeContribution() {
		return changeContribution;
	}

	public void setChangeContribution(boolean changeContribution) {
		this.changeContribution = changeContribution;
	}

	@Column(name = "SAVEASDRAFT")
	public Boolean getSaveAsDraft() {
		return saveAsDraft;
	}

	public void setSaveAsDraft(Boolean saveAsDraft) {
		this.saveAsDraft = saveAsDraft == null || saveAsDraft;
	}

	@Column(name = "DOWNLOADCONTRIBUTION")
	public Boolean getDownloadContribution() {
		return downloadContribution;
	}

	public void setDownloadContribution(Boolean downloadContribution) {
		this.downloadContribution = downloadContribution != null ? downloadContribution : false;
	}

	@Column(name = "ISREGFORM")
	public boolean getRegistrationForm() {
		return registrationForm;
	}

	public void setRegistrationForm(boolean registrationForm) {
		this.registrationForm = registrationForm;
	}

	@Column(name = "SECTIONNUMBERING")
	public int getSectionNumbering() {
		return sectionNumbering;
	}

	public void setSectionNumbering(int sectionNumbering) {
		this.sectionNumbering = sectionNumbering;
	}

	@Column(name = "QUESTIONNUMBERING")
	public int getQuestionNumbering() {
		return questionNumbering;
	}

	public void setQuestionNumbering(int questionNumbering) {
		this.questionNumbering = questionNumbering;
	}

	@Column(name = "NOTIFICATIONVALUE")
	public String getNotificationValue() {
		if (!automaticPublishing)
			return null;

		return notificationValue;
	}

	public void setNotificationValue(String notificationValue) {
		this.notificationValue = notificationValue;
	}

	@Column(name = "NOTIFICATIONUNIT")
	public String getNotificationUnit() {
		if (!automaticPublishing)
			return null;

		return notificationUnit;
	}

	public void setNotificationUnit(String notificationUnit) {
		this.notificationUnit = notificationUnit;
	}

	@Transient
	public String getNiceNotificationUnit() {
		if (getNotificationUnit() == null)
			return "";
		if (getNotificationUnit().equalsIgnoreCase("0")) {
			return "hours";
		} else if (getNotificationUnit().equalsIgnoreCase("1")) {
			return "days";
		} else if (getNotificationUnit().equalsIgnoreCase("2")) {
			return "weeks";
		} else if (getNotificationUnit().equalsIgnoreCase("3")) {
			return "months";
		}
		return "unknow";
	}

	@Column(name = "NOTIFYALL")
	public boolean getNotifyAll() {
		return notifyAll;
	}

	public void setNotifyAll(boolean notifyAll) {
		this.notifyAll = notifyAll;
	}

	@Column(name = "NOTIFIED")
	public boolean getNotified() {
		return notified;
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	@Transient
	public Map<Integer, Question> getQuestionMap() {
		Map<Integer, Question> result = new HashMap<>();
		for (Element element : elements) {
			if (element instanceof Question) {
				result.put(element.getId(), (Question) element);

				if (element instanceof RatingQuestion) {
					for (Element child : ((RatingQuestion) element).getQuestions()) {
						result.put(child.getId(), (Question) child);
					}
				} else if (element instanceof ComplexTable) {
					for (ComplexTableItem child : ((ComplexTable) element).getChildElements()) {
						result.put(child.getId(), child);
					}
				} 
			}
		}
		for (Element element : missingElements) {
			if (element instanceof Question) {
				result.put(element.getId(), (Question) element);
			}
		}
		return result;
	}

    @Transient
    public Map<String, Question> getQuestionMapByUniqueId() {
        return getQuestionMapByUniqueId(false);
    }

    @Transient
    public Map<String, Question> getQuestionMapByUniqueId(boolean includeMissing) {
        Map<String, Question> result = new HashMap<>();
        List<Element> elementsList = new ArrayList<>(elements);
        if (includeMissing) {
            elementsList.addAll(missingElements);
        }
		for (Element element : elementsList) {
			if (element instanceof Question) {
				result.put(element.getUniqueId(), (Question)element);
			}
			if (element instanceof MatrixOrTable) {
				MatrixOrTable matrix = (MatrixOrTable) element;
				for (Element child : matrix.getChildElements()) {
					if (!(child instanceof EmptyElement))
						result.put(child.getUniqueId(), (Question)child);
				}
			}
			if (element instanceof RatingQuestion) {
				RatingQuestion rating = (RatingQuestion) element;
				for (Element child : rating.getChildElements()) {
					result.put(child.getUniqueId(), (Question)child);
				}
			}
			if (element instanceof ComplexTable) {
				ComplexTable table = (ComplexTable) element;
				for (ComplexTableItem child : table.getQuestionChildElements()) {
					result.put(child.getUniqueId(), child);
				}
			}
		}
		return result;
	}

	@Transient
	public Map<Integer, Element> getMatrixMap() {
		Map<Integer, Element> result = new HashMap<>();
		for (Element element : elements) {
			if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;
				for (Element child : matrix.getAllChildElements()) {
					if (!(child instanceof EmptyElement))
						result.put(child.getId(), child);
				}
			}
		}
		for (Element element : missingElements) {
			if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;
				for (Element child : matrix.getAllChildElements()) {
					if (!(child instanceof EmptyElement))
						result.put(child.getId(), child);
				}
			}
		}
		return result;
	}

	@Transient
	public Map<String, Element> getMatrixMapByUid() {
		Map<String, Element> result = new HashMap<>();
		for (Element element : elements) {
			if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;
				for (Element child : matrix.getAllChildElements()) {
					if (!(child instanceof EmptyElement))
						result.put(child.getUniqueId(), child);
				}
			}
		}
		for (Element element : missingElements) {
			if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;
				for (Element child : matrix.getAllChildElements()) {
					if (!(child instanceof EmptyElement))
						result.put(child.getUniqueId(), child);
				}
			}
		}
		return result;
	}

	@Transient
	public Map<String, Element> getMatrixMapByAlias() {
		Map<String, Element> result = new HashMap<>();
		for (Element element : elements) {
			if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;
				for (Element child : matrix.getAllChildElements()) {
					if (!(child instanceof EmptyElement) && !result.containsKey(child.getShortname()))
						result.put(child.getShortname(), child);
				}
			}
		}
		for (Element element : missingElements) {
			if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;
				for (Element child : matrix.getAllChildElements()) {
					if (!(child instanceof EmptyElement) && !result.containsKey(child.getShortname()))
						result.put(child.getShortname(), child);
				}
			}
		}
		return result;
	}

	@Transient
	public List<Question> getQuestions() {
		List<Question> result = new ArrayList<>();

		for (Element element : missingElements) {
			if (element instanceof Question) {
				result.add((Question) element);
			}
		}

		for (Element element : elements) {
			if (element instanceof Question) {
				result.add((Question) element);
			}
		}

		// sort collection by position
		if (missingElements.size() > 0) {
			result.sort(newElementByPositionComparator());
		}

		return result;
	}

	@Transient
	public List<String> getValidMarkupIDs() {
		List<Question> questions = getQuestions();
		List<String> validIDs = new ArrayList<>();

		for(Question q : questions){
			switch(q.getType()){
				case "Table":
				case "Matrix":
					for(Element e : ((MatrixOrTable) q).getQuestions()){
						validIDs.add(e.getShortname());
					}
					break;
				case "ComplexTable":
					for(Element e : ((ComplexTable) q).getChildElements())     {
						validIDs.add(e.getShortname());
					}
					break;
				case "RatingQuestion":
					for(Element e : ((RatingQuestion) q).getChildElements()){
						validIDs.add(e.getShortname());
					}
					break;
				case "Download":
				case "Confirmation":
				case "":
					break;
				default:
					validIDs.add(q.getShortname()); 
					break;
			}
		}

		return validIDs;
	}

	protected static Comparator<Element> newElementByPositionComparator() {
		return (first, second) -> {

			int result = 0;
			if (first.getPosition() != null && second.getPosition() != null) {
				result = first.getPosition().compareTo(second.getPosition());
			}

			// if both elements have the same position, the older one should be first
			if (result == 0) {
				result = first.getId().compareTo(second.getId());
			}

			return result;
		};
	}
	
	protected static Comparator<File> newFileByPositionComparator() {
		return (first, second) -> {
			
			int result = 0;
			if (first.getPosition() != null && second.getPosition() != null) {
				result = first.getPosition().compareTo(second.getPosition());
			}

			// if both elements have the same position, the older one should be first
			if (result == 0) {
				result = first.getId().compareTo(second.getId());
			}

			return result;
		};
	}

	@Transient
	public List<Element> getQuestionsAndSections() {
		List<Element> result = new ArrayList<>();
		for (Element element : elements) {
			if (element instanceof Question || element instanceof Section) {
				result.add(element);
			}
		}

		for (Element element : missingElements) {
			if (element instanceof Question || element instanceof Section) {
				result.add((Question) element);
			}
		}

		// sort collection by position
		if (!missingElements.isEmpty()) {
			result.sort(newElementByPositionComparator());
		}

		return result;
	}

	@Transient
	public boolean containsMandatoryQuestion() {
		for (Question question : getQuestions()) {
			if (!question.getOptional()) {
				return true;
			}

			if (question instanceof Matrix) {
				for (Element matrixquestion : ((Matrix) question).getQuestions()) {
					if (matrixquestion instanceof Question && !((Question) matrixquestion).getOptional()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Column(name = "ISPUBLISHED")
	public boolean getIsPublished() {
		return isPublished;
	}

	public void setIsPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}

	@Transient
	public int getNumberOfAnswerSets() {
		return numberOfAnswerSets;
	}

	public void setNumberOfAnswerSets(int numberOfAnswerSets) {
		this.numberOfAnswerSets = numberOfAnswerSets;
	}

	@Transient
	public int getNumberOfDrafts() {
		return numberOfDrafts;
	}

	public void setNumberOfDrafts(int numberOfDrafts) {
		this.numberOfDrafts = numberOfDrafts;
	}

	@Transient
	public int getNumberOfInvitations() {
		return numberOfInvitations;
	}

	public void setNumberOfInvitations(int numberOfInvitations) {
		this.numberOfInvitations = numberOfInvitations;
	}

	@Transient
	public int getNumberOfAnswerSetsPublished() {
		return numberOfAnswerSetsPublished > 0 ? numberOfAnswerSetsPublished : 0;
	}

	public void setNumberOfAnswerSetsPublished(int numberOfAnswerSetsPublished) {
		this.numberOfAnswerSetsPublished = numberOfAnswerSetsPublished;
	}

	@Transient
	public int getNumberOfReports() {
		return numberOfReports;
	}

	public void setNumberOfReports(int numberOfReports) {
		this.numberOfReports = numberOfReports;
	}

	// this property is not used anymore but has to stay because of backward
	// compatibility
	@Column(name = "COMPULSORYSTYLE")
	public Integer getCompulsoryStyle() {
		return compulsoryStyle;
	}

	public void setCompulsoryStyle(Integer compulsoryStyle) {
		if (compulsoryStyle != null) {
			this.compulsoryStyle = compulsoryStyle;
		}
	}

	@Column(name = "ARCHIVED")
	public Boolean getArchived() {
		return isArchived;
	}

	public void setArchived(Boolean isArchived) {
		this.isArchived = isArchived != null && isArchived;
	}

	@Column(name = "DELETED")
	public Boolean getIsDeleted() {
		return isDeleted != null && isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted != null && isDeleted;
	}

	@Column(name = "FROZEN")
	public Boolean getIsFrozen() {
		return isFrozen != null && isFrozen;
	}

	public void setIsFrozen(Boolean isFrozen) {
		this.isFrozen = isFrozen != null && isFrozen;
	}

	@Column(name = "ECASSEC")
	public Boolean getEcasSecurity() {
		return ecasSecurity;
	}

	public void setEcasSecurity(Boolean ecasSecurity) {
		this.ecasSecurity = ecasSecurity != null && ecasSecurity;
	}

	@Column(name = "ECASMODE")
	public String getEcasMode() {
		return ecasMode;
	}

	public void setEcasMode(String ecasMode) {
		this.ecasMode = ecasMode != null && ecasMode.length() > 0 ? ecasMode : "all";
	}

	@Column(name = "LOGOPOS")
	public Boolean getLogoInInfo() {
		return logoInInfo != null && logoInInfo;
	}

	public void setLogoInInfo(Boolean logoInInfo) {
		this.logoInInfo = logoInInfo;
	}

	@Column(name = "QUIZ")
	public Boolean getIsQuiz() {
		return isQuiz;
	}

	public void setIsQuiz(Boolean isQuiz) {
		this.isQuiz = isQuiz != null && isQuiz;
	}

	@Column(name = "DELPHI")
	public Boolean getIsDelphi() {
	    return isDelphi;
	}

	public void setIsDelphi(Boolean isDelphi) {
	    this.isDelphi = isDelphi != null && isDelphi;
	}

	@Column(name = "OPC")
	public Boolean getIsOPC() {
		return isOPC;
	}

	public void setIsOPC(Boolean isOPC) {
		this.isOPC = isOPC != null && isOPC;
	}

	@Column(name = "ECF")
	public Boolean getIsECF() {
		return isECF != null ? isECF : false;
	}

	public void setIsECF(Boolean isECF) {
		this.isECF = isECF != null ? isECF : false;
	}

	@Transient
	public Set<ECFProfile> getEcfProfiles() {
		if (this.getIsECF()) {
			Set<Element> questionSet = this.getElements().stream()
					.filter(element -> element instanceof SingleChoiceQuestion).collect(Collectors.toSet());
			for (Element questionElement : questionSet) {
				SingleChoiceQuestion question = (SingleChoiceQuestion) questionElement;

				if (!question.getPossibleAnswers().isEmpty()
						&& question.getPossibleAnswers().get(0).getEcfProfile() != null) {
					List<PossibleAnswer> possibleAnswers = question.getPossibleAnswers();
					return possibleAnswers.stream().map(possibleAnswer -> {
						return possibleAnswer.getEcfProfile();
					}).collect(Collectors.toSet());
				}
			}
		}
		return new HashSet<>();
	}
	
	@Column(name = "SELFASSESSMENT")
	public Boolean getIsSelfAssessment() {
		return isSelfAssessment != null ? isSelfAssessment : false;
	}

	public void setIsSelfAssessment(Boolean isSelfAssessment) {
		this.isSelfAssessment = isSelfAssessment != null ? isSelfAssessment : false;
	}

	@Column(name = "SHOWSCORE")
	public Boolean getShowTotalScore() {
		return showTotalScore == null || showTotalScore;
	}

	public void setShowTotalScore(Boolean showTotalScore) {
		this.showTotalScore = showTotalScore;
	}

	@Column(name = "SHOWICONS")
	public Boolean getShowQuizIcons() {
		return showQuizIcons == null || showQuizIcons;
	}

	public void setShowQuizIcons(Boolean showQuizIcons) {
		this.showQuizIcons = showQuizIcons;
	}

	@Column(name = "SCOREBYQUESTION")
	public Boolean getScoresByQuestion() {
		return scoresByQuestion != null ? scoresByQuestion : true;
	}

	public void setScoresByQuestion(Boolean scoresByQuestion) {
		this.scoresByQuestion = scoresByQuestion;
	}

	@Column(name = "PDFUNAVAIL")
	public Boolean getShowPDFOnUnavailabilityPage() {
		return showPDFOnUnavailabilityPage != null && showPDFOnUnavailabilityPage;
	}

	public void setShowPDFOnUnavailabilityPage(Boolean showPDFOnUnavailabilityPage) {
		this.showPDFOnUnavailabilityPage = showPDFOnUnavailabilityPage;
	}

	@Column(name = "DOCSUNAVAIL")
	public Boolean getShowDocsOnUnavailabilityPage() {
		return showDocsOnUnavailabilityPage != null && showDocsOnUnavailabilityPage;
	}

	public void setShowDocsOnUnavailabilityPage(Boolean showDocsOnUnavailabilityPage) {
		this.showDocsOnUnavailabilityPage = showDocsOnUnavailabilityPage;
	}

	@Column(name = "QUESTIONDWNLD")
	public Boolean getAllowQuestionnaireDownload(){
		return allowQuestionnaireDownload != null && allowQuestionnaireDownload;
	}
	public void setAllowQuestionnaireDownload(Boolean allowQuestionnaireDownload){
		this.allowQuestionnaireDownload = allowQuestionnaireDownload;
	}

	@Lob
	@Column(name = "QUIZWELCOME", length = 40000)
	public String getQuizWelcomeMessage() {
		return quizWelcomeMessage;
	}

	public void setQuizWelcomeMessage(String quizWelcomeMessage) {
		this.quizWelcomeMessage = quizWelcomeMessage;
	}

	@Lob
	@Column(name = "QUIZRESULTS", length = 40000)
	public String getQuizResultsMessage() {
		return quizResultsMessage;
	}

	public void setQuizResultsMessage(String quizResultsMessage) {
		this.quizResultsMessage = quizResultsMessage;
	}

	@Column(name = "ALLOWEDCONTRIBUTIONS")
	public Integer getAllowedContributionsPerUser() {
		return allowedContributionsPerUser != null ? allowedContributionsPerUser : 1;
	}

	public void setAllowedContributionsPerUser(Integer allowedContributionsPerUser) {
		this.allowedContributionsPerUser = allowedContributionsPerUser;
	}

	@Column(name = "TRUSTSCORE")
	public Integer getTrustScore() {
		return trustScore;
	}

	public void setTrustScore(Integer trustScore) {
		this.trustScore = trustScore;
	}

	@Column(name = "DELPHIANSWERSANDSTATISTICSINSTANTLY")
	public Boolean getIsDelphiShowAnswersAndStatisticsInstantly() {
		return isDelphiShowAnswersAndStatisticsInstantly != null ? isDelphiShowAnswersAndStatisticsInstantly : false;
	}

	public void setIsDelphiShowAnswersAndStatisticsInstantly(Boolean isDelphiShowAnswersAndStatisticsInstantly) {
		this.isDelphiShowAnswersAndStatisticsInstantly = isDelphiShowAnswersAndStatisticsInstantly != null ? isDelphiShowAnswersAndStatisticsInstantly : false;
	}
	
	@Column(name = "DELPHISTARTPAGE")
	public Boolean getIsDelphiShowStartPage() {
		return isDelphiShowStartPage != null ? isDelphiShowStartPage : true;
	}

	public void setIsDelphiShowStartPage(Boolean isDelphiShowStartPage) {
		this.isDelphiShowStartPage = isDelphiShowStartPage != null ? isDelphiShowStartPage : true;
	}

	@Column(name = "DELPHIANSWERS")
	public Boolean getIsDelphiShowAnswers() {
		return isDelphiShowAnswers  != null ? isDelphiShowAnswers : false;
	}
	
	public void setIsDelphiShowAnswers(Boolean isDelphiShowAnswers) {
		this.isDelphiShowAnswers = isDelphiShowAnswers != null ? isDelphiShowAnswers : false;
	}

	@Column(name = "DELPHIMINSTATISTICS")
	public Integer getMinNumberDelphiStatistics() {
		return minNumberDelphiStatistics != null ? minNumberDelphiStatistics : 5;
	}

	public void setMinNumberDelphiStatistics(Integer minNumberDelphiStatistics) {
		this.minNumberDelphiStatistics = minNumberDelphiStatistics != null ? Math.max(minNumberDelphiStatistics, 1) : 5;
	}

	@Column(name = "EVOTE")
	public Boolean getIsEVote() {
		return isEVote != null ? isEVote : false;
	}

	public void setIsEVote(Boolean isEVote) {
		this.isEVote = isEVote != null ? isEVote : false;
	}


	@Column(name = "RESULTSTESTPAGE")
	public Boolean getShowResultsTestPage(){
		return showResultsTestPage != null ? showResultsTestPage : false;
	}

	public void setShowResultsTestPage(Boolean showResultsTestPage){
		this.showResultsTestPage = showResultsTestPage != null ? showResultsTestPage : false;
	}
	
	@Column(name = "EVOTETEMPLATE")
	public String geteVoteTemplate() {
		return eVoteTemplate;
	}

	public void seteVoteTemplate(String eVoteTemplate) {
		this.eVoteTemplate = eVoteTemplate;
	}

	@ManyToMany(targetEntity = Tag.class)
	@JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
			joinColumns = @JoinColumn(name = "SURVEY_SURVEY_ID"),
			inverseJoinColumns = @JoinColumn(name = "tags_TAG_ID"))
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	@Transient
	public String geteVoteTemplateTitle() {
		if (eVoteTemplate != null) {
			switch (eVoteTemplate) {
				case "b":
					return "label.Brussels";
				case "i":
					return "label.IspraSeville";
				case "l":
					return "label.Luxembourg";
				case "o":
					return "label.OutsideCommunity";
			}
		}
		return "label.Standard";
	}

	@Transient
	public String serialize(boolean elementOrderOnly) {
		StringBuilder result = new StringBuilder();
		if (!elementOrderOnly) {
			result.append("id: ").append(id).append(";");
			result.append(" contact: ").append(contact).append(";");
			result.append(" automaticPublishing: ").append(automaticPublishing).append(";");
			result.append(" start: ").append(ConversionTools.getFullString(start)).append(";");
			result.append(" end: ").append(ConversionTools.getFullString(end)).append(";");
			result.append(" draft: ").append(isDraft).append(";");
			result.append(" language: ").append(language.getCode()).append(";");
			result.append(" security: ").append(security).append(";");
			result.append(" alias: ").append(shortname).append(";");
			result.append(" title: ").append(title).append(";");
			result.append(" registrationForm: ").append(registrationForm).append(";");
			result.append(" changeContribution: ").append(changeContribution).append(";");
			if (logo != null) {
				result.append(" logo: ").append(logo.getName()).append(";");
				result.append(" logoInInfo: ").append(logoInInfo).append(";");
				result.append(" logoText: ").append(logoText).append(";");
			}

			result.append(" confirmationPage: ").append(confirmationPage).append(";");
			result.append(" confirmationLink: ").append(this.getConfirmationLink()).append(";");
			result.append(" confirmationPageLink: ").append(confirmationPageLink).append(";");
			result.append(" motivationText: ").append(this.getMotivationText()).append(";");
			if (motivationPopupTitle != null){
				result.append(" motivationPopupTitle: ").append(HtmlUtils.htmlEscape(motivationPopupTitle)).append(";");
			}
			result.append(" motivationTriggerProgress: ").append(motivationTriggerProgress).append(";");
			result.append(" motivationTriggerTime: ").append(motivationTriggerTime).append(";");
			result.append(" motivationPopup: ").append(motivationPopup).append(";");
			result.append(" motivationType: ").append(motivationType).append(";");
			result.append(" escapePage: ").append(escapePage).append(";");
			result.append(" escapeLink: ").append(this.getEscapeLink()).append(";");
			result.append(" escapePageLink: ").append(escapePageLink).append(";");
			result.append(" password: ").append(password).append(";");
			result.append(" multiPaging: ").append(multiPaging).append(";");
			result.append(" validatedPerPage: ").append(validatedPerPage).append(";");
			result.append(" captcha: ").append(captcha).append(";");
			result.append(" questionNumbering: ").append(questionNumbering).append(";");
			result.append(" sectionNumbering: ").append(sectionNumbering).append(";");
			result.append(" notificationValue: ").append(notificationValue).append(";");
			result.append(" notificationUnit: ").append(notificationUnit).append(";");
			result.append(" notifyAll: ").append(notifyAll).append(";");
			result.append(" showPDFOnUnavailabilityPage: ").append(showPDFOnUnavailabilityPage).append(";");
			result.append(" showDocsOnUnavailabilityPage: ").append(showDocsOnUnavailabilityPage).append(";");
			result.append(" allowQuestionnaireDownload: ").append(allowQuestionnaireDownload).append(";");
			result.append(" preventGoingBack: ").append(preventGoingBack).append(";");

			try {
				if (backgroundDocuments != null)
					for (Entry<String, String> entry : backgroundDocuments.entrySet()) {
						result.append(" BackgroundDocument: ").append(entry.getKey()).append(" - ")
								.append(entry.getValue()).append(";");
					}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (usefulLinks != null)
					for (Entry<String, String> entry : usefulLinks.entrySet()) {
						result.append(" UsefulLink: ").append(entry.getKey()).append(" - ").append(entry.getValue())
								.append(";");
					}
			} catch (Exception e) {
				// ignore
			}
		}

		result.append(" Elements: ");
		for (Element element : getElementsRecursive()) {
			result.append(element.getUniqueId()).append(" ");
		}
		result.append(";");

		return result.toString();
	}

	/**
	 * Copies the survey into a new one, with its elements and translations
	 */
	@Transient
	public Survey copy(SurveyService surveyService, User powner, String fileDir, boolean copyNumberOfAnswerSets,
			int pnumberOfAnswerSets, int pnumberOfAnswerSetsPublished, boolean copyPublication, boolean resetSourceIds,
			boolean keepuid, String newshortname, String newuid) throws ValidationException {
		Survey copy = new Survey();
		copy.contact = contact;
		copy.contactLabel = contactLabel;
		copy.audience = audience;
		copy.automaticPublishing = automaticPublishing;
		copy.sendConfirmationEmail = sendConfirmationEmail;
		copy.sendReportEmail = sendReportEmail;
		copy.reportEmailFrequency = reportEmailFrequency;
		copy.reportEmails = reportEmails;
		copy.setStart(start);
		copy.setEnd(end);
		copy.introduction = Tools.filterHTML(introduction);
		copy.isDraft = true;
		copy.language = language;
		copy.listForm = listForm;
		copy.setListFormValidated(listFormValidated);
		copy.publicationRequestedDate = publicationRequestedDate;
		copy.owner = powner;
		copy.security = security;
		copy.shortname = newshortname != null ? newshortname : Tools.escapeHTML(shortname);
		copy.uniqueId = newuid != null ? newuid : uniqueId;
		copy.setTitle(Tools.filterHTML(title));
		copy.registrationForm = registrationForm;
		copy.saveAsDraft = saveAsDraft;
		copy.changeContribution = changeContribution;
		copy.downloadContribution = downloadContribution;

		if (logo != null) {
			File copyLogo = logo.copy(fileDir);
			copy.logo = copyLogo;
		}
		copy.logoInInfo = logoInInfo;
		copy.logoText = logoText;

		copy.confirmationPage = Tools.filterHTML(confirmationPage);
		copy.confirmationLink = this.getConfirmationLink();
		copy.confirmationPageLink = confirmationPageLink;
		copy.escapePage = Tools.filterHTML(escapePage);
		copy.escapeLink = this.getEscapeLink();
		copy.escapePageLink = escapePageLink;
		copy.password = password;
		copy.setEcasSecurity(ecasSecurity);
		copy.setEcasMode(ecasMode);
		copy.ecasMode = ecasMode;
		copy.multiPaging = multiPaging;
		copy.progressBar = progressBar;
		copy.motivationPopup = motivationPopup;
		copy.motivationType = motivationType;
		copy.motivationText = Tools.filterHTML(motivationText);
		copy.motivationTriggerProgress = motivationTriggerProgress;
		copy.motivationTriggerTime = motivationTriggerTime;
		copy.motivationPopupTitle = motivationPopupTitle;
		copy.progressDisplay = progressDisplay;
		copy.validatedPerPage = validatedPerPage;
		copy.setWcagCompliance(wcagCompliance);
		copy.captcha = captcha;
		copy.setCompulsoryStyle(compulsoryStyle);
		copy.questionNumbering = questionNumbering;
		copy.sectionNumbering = sectionNumbering;
		copy.notificationValue = notificationValue;
		copy.notificationUnit = notificationUnit;
		copy.notifyAll = notifyAll;
		copy.notified = notified;
		copy.isQuiz = isQuiz;
		copy.quizWelcomeMessage = quizWelcomeMessage;
		copy.quizResultsMessage = quizResultsMessage;
		copy.showQuizIcons = showQuizIcons;
		copy.showTotalScore = showTotalScore;
		copy.scoresByQuestion = scoresByQuestion;
		copy.showPDFOnUnavailabilityPage = showPDFOnUnavailabilityPage;
		copy.allowQuestionnaireDownload = allowQuestionnaireDownload;
		copy.showDocsOnUnavailabilityPage = showDocsOnUnavailabilityPage;
		copy.isOPC = isOPC;
		copy.isECF = isECF;
		copy.isEVote = isEVote;
		copy.isSelfAssessment = isSelfAssessment;
		copy.eVoteTemplate = eVoteTemplate;
		copy.quorum = quorum;
		copy.minListPercent = minListPercent;
		copy.maxPrefVotes = maxPrefVotes;
		copy.seatsToAllocate = seatsToAllocate;
		copy.showResultsTestPage = showResultsTestPage;
		copy.setAllowedContributionsPerUser(allowedContributionsPerUser);
		copy.setIsUseMaxNumberContribution(isUseMaxNumberContribution);
		copy.setIsUseMaxNumberContributionLink(isUseMaxNumberContributionLink);
		copy.setMaxNumberContribution(maxNumberContribution);
		copy.setMaxNumberContributionText(Tools.filterHTML(maxNumberContributionText));
		copy.setMaxNumberContributionLink(Tools.filterHTML(maxNumberContributionLink));
		copy.isDelphi = isDelphi;
		copy.isDelphiShowAnswersAndStatisticsInstantly = isDelphiShowAnswersAndStatisticsInstantly;
		copy.isDelphiShowStartPage = isDelphiShowStartPage;
		copy.isDelphiShowAnswers = isDelphiShowAnswers;
		copy.minNumberDelphiStatistics = minNumberDelphiStatistics;
		copy.timeLimit = timeLimit;
		copy.isShowCountdown = isShowCountdown;
		copy.setPreventGoingBack(preventGoingBack);
		copy.setDedicatedResultPrivileges(dedicatedResultPrivileges);
		copy.criticalComplexity = criticalComplexity;
		copy.codaLink = codaLink;
		copy.codaWaiting = codaWaiting;
		copy.setOrganisation(organisation);
		copy.setValidator(validator);
		copy.setWebhook(webhook);

		if (copyNumberOfAnswerSets) {
			int numberOfAnswerSets1 = pnumberOfAnswerSets > -1 ? pnumberOfAnswerSets : numberOfAnswerSetsPublished;
			copy.numberOfAnswerSets = numberOfAnswerSets1;
			int numberOfAnswerSetsPublished1 = pnumberOfAnswerSetsPublished > -1 ? pnumberOfAnswerSetsPublished
					: numberOfAnswerSetsPublished;
			copy.numberOfAnswerSetsPublished = numberOfAnswerSetsPublished1;
		}

		try {
			if (backgroundDocuments != null)
				for (Entry<String, String> entry : backgroundDocuments.entrySet()) {
					copy.backgroundDocuments.put(entry.getKey(), entry.getValue());
				}
		} catch (Exception e) {
			// ignore
		}

		try {
			if (usefulLinks != null)
				for (Entry<String, String> entry : usefulLinks.entrySet()) {
					copy.usefulLinks.put(entry.getKey(), entry.getValue());
				}
		} catch (Exception e) {
			// ignore
		}

		Map<Integer, Element> elementsBySourceId = copyElements(copy, surveyService, false);

		// this is necessary to generate the ids of the newly created elements
		surveyService.add(copy, false, owner.getId());

		if (copyPublication) {
			copy.publication.setShowContent(publication.isShowContent());
			copy.publication.setShowStatistics(publication.isShowStatistics());
			copy.publication.setShowCharts(publication.isShowCharts());
			copy.publication.setShowSearch(publication.isShowSearch());
			copy.publication.setAllQuestions(publication.isAllQuestions());
			copy.publication.setAllContributions(publication.isAllContributions());
			copy.publication.setPassword(publication.getPassword());

			for (String questionId : publication.getFilter().getVisibleQuestions()) {
				if (elementsBySourceId.containsKey(Integer.parseInt(questionId))) {
					copy.publication.getFilter().getVisibleQuestions()
							.add(elementsBySourceId.get(Integer.parseInt(questionId)).getId().toString());
				}
			}
			for (String questionId : publication.getFilter().getExportedQuestions()) {
				if (elementsBySourceId.containsKey(Integer.parseInt(questionId))) {
					copy.publication.getFilter().getExportedQuestions()
							.add(elementsBySourceId.get(Integer.parseInt(questionId)).getId().toString());
				}
			}
			for (String questionId : publication.getFilter().getFilterValues().keySet()) {
				copy.publication.getFilter().getFilterValues().put(questionId,
						publication.getFilter().getFilterValues().get(questionId));
			}

		}

		try {
			if (skin != null) {
				copy.skin = skin;
			}
		} catch (Exception e) {
			// ignore
		}

		if (resetSourceIds) {
			// this means we are in a clearChanges call
			// we need to set the source id of the elements in the original to that of the
			// copy (the new draft)
			for (Element element : getElementsRecursive(true)) {
				if (elementsBySourceId.containsKey(element.getId())) {
					element.setSourceId(elementsBySourceId.get(element.getId()).getId());
				}
			}
		}

		if (tags != null) {
			for (Tag tag : tags) {
				copy.getTags().add(tag);
			}
		}

		return copy;
	}

	/**
	 * Copies this elements into surveyCopy
	 */
	@Transient
	public Map<Integer, Element> copyElements(Survey surveyCopy, SurveyService surveyService, boolean makeQuestionsLocked)
			throws ValidationException {
		for (Element element : elements) {
			Element copiedElement = element.copy(surveyService.getFileDir());
			copiedElement.setLocked(element.getLocked() || makeQuestionsLocked);
			surveyCopy.elements.add(copiedElement);
		}

		Map<Integer, Element> elementsBySourceId = new HashMap<>();
		for (Element newElement : surveyCopy.elements) {
			elementsBySourceId.put(newElement.getSourceId(), newElement);
			if (newElement instanceof ChoiceQuestion) {
				for (PossibleAnswer answer : ((ChoiceQuestion) newElement).getPossibleAnswers()) {
					elementsBySourceId.put(answer.getSourceId(), answer);
				}
			} else if (newElement instanceof MatrixOrTable) {
				MatrixOrTable t = (MatrixOrTable) newElement;
				for (Element child : t.getChildElements()) {
					elementsBySourceId.put(child.getSourceId(), child);
				}
			} else if (newElement instanceof ComplexTable) {
				ComplexTable table = (ComplexTable) newElement;
				for (ComplexTableItem item : table.getChildElements()){
					if (item.isChoice()){
						for (PossibleAnswer answer : item.getPossibleAnswers()) {
							elementsBySourceId.put(answer.getSourceId(), answer);
						}
					}
				}
			}
		}

		// recreate dependencies
		for (Element element : elements) {
			if (element instanceof ChoiceQuestion) {
				ChoiceQuestion question = (ChoiceQuestion) element;
				for (PossibleAnswer answer : question.getPossibleAnswers()) {
					if (answer.getDependentElements() != null) {
						// find new possible answer
						PossibleAnswer newPossibleAnswer = (PossibleAnswer) elementsBySourceId.get(answer.getId());
						DependencyItem newDep = new DependencyItem();
						newPossibleAnswer.setDependentElements(newDep);
						for (Element dependent : answer.getDependentElements().getDependentElements()) {
							// find new dependent element
							Element newDependent = elementsBySourceId.get(dependent.getId());
							newDep.getDependentElements().add(newDependent);
						}
					}
				}
			} else if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;

				// find new matrix
				Matrix newMatrix = (Matrix) elementsBySourceId.get(matrix.getId());

				newMatrix.setDependentElements(new ArrayList<>());

				for (DependencyItem dep : matrix.getDependentElements()) {
					DependencyItem newDep = new DependencyItem();
					newDep.setPosition(dep.getPosition());

					for (Element elem : dep.getDependentElements()) {
						// find new dependent element
						Element newDependent = elementsBySourceId.get(elem.getId());
						newDep.getDependentElements().add(newDependent);
					}
					newMatrix.getDependentElements().add(newDep);
				}
			}
		}

		return elementsBySourceId;
	}

	@Transient
	public String getState() {

		Date now = new Date();

		if (automaticPublishing && end != null && end.before(now)) {
			return "Finished";
		}
		if (isActive && (!automaticPublishing || start == null || start.before(now))) {
			return "Running";
		}
		return "Draft";
	}

	@Transient
	public List<String> getTranslations() {
		return translations;
	}

	public void setTranslations(List<String> translations) {
		this.translations = translations;
	}

	@Transient
	public List<Language> getCompleteTranslations() {
		return completeTranslations;
	}

	public void setCompleteTranslations(List<Language> translations) {
		this.completeTranslations = translations;
	}

	@Transient
	public boolean containsCompleteTranslations(String code) {
		return completeTranslations != null &&
				completeTranslations.stream().map(t -> t.getCode()).anyMatch(c -> c.equals(code));
	}

	@Transient
	public Map<Element, List<Element>> getTriggersByDependantElement() {
		HashMap<Element, List<Element>> result = new HashMap<>();
		for (Element element : elements) {
			if (element instanceof ChoiceQuestion) {
				for (PossibleAnswer p : ((ChoiceQuestion) element).getPossibleAnswers()) {
					if (!p.getDependentElements().getDependentElements().isEmpty()) {
						List<Element> list;
						for (Element t : p.getDependentElements().getDependentElements()) {
							if (result.containsKey(t)) {
								list = result.get(t);
							} else {
								list = new ArrayList<>();
								result.put(t, list);
							}
							list.add(p);
						}
					}
				}
			}

			if (element instanceof Matrix) {
				Matrix m = (Matrix) element;
				if (!m.getDependentElements().isEmpty()) {
					List<Element> list;
					for (DependencyItem d : m.getDependentElements()) {
						for (Element t : d.getDependentElements()) {
							if (result.containsKey(t)) {
								list = result.get(t);
							} else {
								list = new ArrayList<>();
								result.put(t, list);
							}
							list.add(m);
						}
					}
				}
			}

		}

		return result;
	}

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	public Publication getPublication() {
		return publication;
	}

	public void setPublication(Publication publication) {
		this.publication = publication;
	}

	@Column(name = "LOGOTEXT")
	public String getLogoText() {
		if (logoText == null){
			return "";
		}
		return logoText;
	}

	public void setLogoText(String logoText) {
		if (logoText == null){
			logoText = "";
		}
		this.logoText = logoText;
	}

	@Transient
	public Map<Integer, Element> getElementsBySourceId() {
		Map<Integer, Element> result = new HashMap<>();
		for (Element element : getElementsRecursive(true)) {
			result.put(element.getSourceId(), element);
		}
		return result;
	}

	@Transient
	public Map<String, Element> getElementsByUniqueId() {
		Map<String, Element> result = new HashMap<>();
		for (Element element : getElementsRecursive(true)) {
			result.put(element.getUniqueId(), element);
		}
		return result;
	}

	@Transient
	public Map<String, Element> getElementsByAlias() {
		Map<String, Element> result = new HashMap<>();
		for (Element element : getElementsRecursive(true)) {
			if (element.getShortname() != null && !result.containsKey(element.getShortname())) {
				result.put(element.getShortname(), element);
			}
		}
		return result;
	}

	private Map<Integer, Element> _elementsById = null;

	@Transient
	public Map<Integer, Element> getElementsById() {
		if (_elementsById == null) {
			_elementsById = new HashMap<>();
			for (Element element : getElementsRecursive()) {
				_elementsById.put(element.getId(), element);
			}
		}

		return _elementsById;
	}

	@Transient
	public String cleanTitle() {
		return ConversionTools.removeHTML(title, true);
	}

	@Transient
	public String cleanTitleForMailSubject() {
		String result = ConversionTools.removeHTMLNoEscape(title);

		if (result.length() > 25) {
			return result.substring(0, 25) + "...";
		}

		return result;
	}

	@Transient
	public String shortCleanTitle() {
		String result = cleanTitle();

		if (result.length() > 50) {
			return result.substring(0, 50) + "...";
		}

		return result;
	}

	@Transient
	public String mediumCleanTitle() {
		String result = cleanTitle();

		if (result.length() > 115) {
			return result.substring(0, 115) + "<span class='titletooltip'>...</span>";
		}

		return result;
	}

	@Transient
	public String getCreatedString() {
		return createdString;
	}

	public void setCreatedString(String createdString) {
		this.createdString = createdString;
	}

	@Transient
	public String getStartString() {
		return startString;
	}

	public void setStartString(String startString) {
		this.startString = startString;
	}

	@Transient
	public String getEndString() {
		return endString;
	}

	public void setEndString(String endString) {
		this.endString = endString;
	}

	@Transient
	public String getDeletedString() {
		return deletedString;
	}

	public void setDeletedString(String deletedString) {
		this.deletedString = deletedString;
	}

	@Transient
	public boolean isAnonymous() {
		return security != null && security.endsWith("anonymous");
	}

	@Transient
	public Map<String, String> getUniqueIDsByID() {
		Map<String, String> result = new HashMap<>();

		for (Element element : getElementsRecursive(true)) {
			result.put(element.getId().toString(), element.getUniqueId());
		}

		return result;
	}

	@Transient
	public Map<String, String> getIDsByUniqueID() {
		Map<String, String> result = new HashMap<>();

		for (Element element : getElementsRecursive(true)) {
			result.put(element.getUniqueId(), element.getId().toString());
		}

		return result;
	}

	@Transient
	public boolean isMissingElementsChecked() {
		return missingElementsChecked;
	}

	public void setMissingElementsChecked(boolean missingElementsChecked) {
		this.missingElementsChecked = missingElementsChecked;
	}

	public void clearMissingData() {
		missingElements.clear();
		for (Element element : elements) {
			if (element instanceof MatrixOrTable) {
				((MatrixOrTable) element).missingAnswers.clear();
				((MatrixOrTable) element).missingQuestions.clear();
			} else if (element instanceof ChoiceQuestion) {
				((ChoiceQuestion) element).getMissingPossibleAnswers().clear();
			} else if (element instanceof RatingQuestion) {
				((RatingQuestion) element).getMissingQuestions().clear();
			}
		}
		missingElementsChecked = false;
	}

	@Transient
	public Map<Integer, String[]> getActivitiesToLog() {
		return this.activitiesToLog;
	}

	public void setActivitiesToLog(Map<Integer, String[]> activitiesToLog) {
		this.activitiesToLog = activitiesToLog;
	}

	@Transient
	public Map<String, String> getFileNamesForBackgroundDocuments() {
		return fileNamesForBackgroundDocuments;
	}

	public void setFileNamesForBackgroundDocuments(Map<String, String> fileNamesForBackgroundDocuments) {
		this.fileNamesForBackgroundDocuments = fileNamesForBackgroundDocuments;
	}

	@Transient
	public String getFileNameForBackgroundDocument(String key) {
		if (fileNamesForBackgroundDocuments.containsKey(key)) {
			return fileNamesForBackgroundDocuments.get(key);
		}
		return key;
	}

	/**
	 * @return the audience
	 */
	@Column(name = "AUDIENCE")
	public String getAudience() {
		return audience;
	}

	/**
	 * @param audience the audience to set
	 */
	public void setAudience(String audience) {
		this.audience = audience;
	}

	private Boolean hasUploadElement = null;

	@Transient
	public Boolean getHasUploadElement() {
		if (hasUploadElement == null) {
			hasUploadElement = false;
			for (Element question : elements) {
				if (question.getType().equalsIgnoreCase("UPLOAD")) {
					hasUploadElement = true;
					break;
				}
			}
		}

		return hasUploadElement;
	}

	public void setHasUploadElement(Boolean hasUploadElement) {
		this.hasUploadElement = hasUploadElement;
	}

	@Transient
	public boolean hasNoQuestionsForStatistics() {
		for (Element question : elements) {
			if (question instanceof Matrix || question instanceof ChoiceQuestion
					|| (question instanceof GalleryQuestion && ((GalleryQuestion) question).getSelection())
					|| question instanceof RatingQuestion || question instanceof RankingQuestion) {
				return false;
			}
			
			if (question instanceof NumberQuestion) {
				NumberQuestion number = (NumberQuestion) question;
				if (number.showStatisticsForNumberQuestion()) {
					return false;
				}
			}
			
			if (question instanceof ComplexTable) {
				ComplexTable table = (ComplexTable) question;
				for (ComplexTableItem child: table.getQuestionChildElements()) {
					if (child.getCellType() != ComplexTableItem.CellType.Empty && child.getCellType() != ComplexTableItem.CellType.StaticText) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public void computeTriggers() {
		Map<Integer, String> triggers = new HashMap<>();

		for (Element element : elements) {
			if (element instanceof ChoiceQuestion) {
				for (PossibleAnswer p : ((ChoiceQuestion) element).getPossibleAnswers()) {
					for (Element dependent : p.getDependentElements().getDependentElements()) {
						if (!triggers.containsKey(dependent.getId())) {
							triggers.put(dependent.getId(), p.getId() + ";");
						} else {
							triggers.put(dependent.getId(), triggers.get(dependent.getId()) + p.getId() + ";");
						}
					}
				}
			} else if (element instanceof Matrix) {
				Matrix matrix = (Matrix) element;
				for (DependencyItem dep : matrix.getDependentElements()) {

					for (Element dependent : dep.getDependentElements()) {
						try {
							Element q = matrix.getQuestions().get(dep.getPosition() / matrix.getAnswers().size());
							Element a = matrix.getAnswers().get(dep.getPosition() % matrix.getAnswers().size());

							if (!triggers.containsKey(dependent.getId())) {
								triggers.put(dependent.getId(),
										q.getId().toString() + "|" + a.getId().toString() + ";");
							} else {
								triggers.put(dependent.getId(), triggers.get(dependent.getId()) + q.getId().toString()
										+ "|" + a.getId().toString() + ";");
							}
						} catch (Exception e) {
							// invalid dependency: ignore
						}
					}
				}
			}
		}

		for (Element element : getElementsRecursive()) {
			if (triggers.containsKey(element.getId())) {
				element.setTriggers(triggers.get(element.getId()));
			}
		}
	}

	@Transient
	public String getElementsRecursiveUids() {
		StringBuilder result = new StringBuilder();
		for (Element element : getElementsRecursive(true)) {
			if (!(element instanceof EmptyElement))
				result.append(element.getUniqueId());
		}
		return result.toString();
	}

	@Transient
	public Map<String, Integer> getReferencedFileUIDs(String contextpath) {

		Map<String, Integer> referencedFiles = new HashMap<>();

		if (logo != null) {
			referencedFiles.put(logo.getUid(), logo.getId());
		}

		if (backgroundDocuments != null && backgroundDocuments.size() > 0) {
			for (String url : backgroundDocuments.values()) {
				String uid = url.replace(contextpath + "/files/", "");
				referencedFiles.put(uid, null);
			}
		}

		for (Element element : elements) {
			if (element instanceof Download) {
				Download download = (Download) element;

				for (File f : download.getFiles()) {
					referencedFiles.put(f.getUid(), f.getId());
				}
			} else if (element instanceof Confirmation) {
				Confirmation confirmation = (Confirmation) element;

				for (File f : confirmation.getFiles()) {
					referencedFiles.put(f.getUid(), f.getId());
				}
			} else if (element instanceof Image) {
				Image image = (Image) element;

				if (image.getUrl() != null) {
					String fileUID = image.getUrl().replace(contextpath + "/files/", "");

					if (fileUID.length() > 0) {
						if (fileUID.contains(Constants.PATH_DELIMITER)) {
							fileUID = fileUID.substring(fileUID.indexOf('/') + 1);
						}

						referencedFiles.put(fileUID, null);
					}
				}
			} else if (element instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) element;

				for (File f : gallery.getFiles()) {
					referencedFiles.put(f.getUid(), f.getId());
				}
			}
		}

		return referencedFiles;
	}

	@Transient
	public boolean hasSections() {
		for (Element element : elements) {
			if (element instanceof Section) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public boolean isFullFormManagementRights() {
		return fullFormManagementRights;
	}

	public void setFullFormManagementRights(boolean fullFormManagementRights) {
		this.fullFormManagementRights = fullFormManagementRights;
	}

	@Transient
	public boolean isFormManagementRights() {
		return formManagementRights;
	}

	public void setFormManagementRights(boolean formManagementRights) {
		this.formManagementRights = formManagementRights;
	}

	@Transient
	public boolean isAccessResultsRights() {
		return accessResultsRights;
	}

	public void setAccessResultsRights(boolean accessResultsRights) {
		this.accessResultsRights = accessResultsRights;
	}

	@Transient
	public boolean isCanCreateSurveys() {
		return canCreateSurveys;
	}

	public void setCanCreateSurveys(boolean canCreateSurveys) {
		this.canCreateSurveys = canCreateSurveys;
	}

	public Boolean getIsUseMaxNumberContribution() {
		return this.isUseMaxNumberContribution != null ? this.isUseMaxNumberContribution : false;
	}

	@Column(name = "ISUSEMAXNUMBERCONTRIBUTION")
	public void setIsUseMaxNumberContribution(Boolean useMaxNumberContribution) {
		this.isUseMaxNumberContribution = useMaxNumberContribution != null ? useMaxNumberContribution : false;
	}

	@Column(name = "ISUSEMAXNUMBERCONTRIBUTIONLINK")
	public Boolean getIsUseMaxNumberContributionLink() {
		return this.isUseMaxNumberContributionLink != null ? this.isUseMaxNumberContributionLink : false;
	}
	
	public void setIsUseMaxNumberContributionLink(Boolean useMaxNumberContributionLink) {
		this.isUseMaxNumberContributionLink = useMaxNumberContributionLink != null ? useMaxNumberContributionLink
				: false;
	}
	
	@Column(name = "MAXNUMBERCONTRIBUTIONTEXT", length = 255)
	public String getMaxNumberContributionText() {
		return this.maxNumberContributionText != null && this.maxNumberContributionText.length() > 0
				? this.maxNumberContributionText
				: MAXNUMBEROFRESULTSTEXT;
	}

	public void setMaxNumberContributionText(String maxNumberContributionText) {
		this.maxNumberContributionText = maxNumberContributionText != null ? Tools.filterHTML(maxNumberContributionText)
				: MAXNUMBEROFRESULTSTEXT;
	}

	@Column(name = "MAXNUMBERCONTRIBUTIONLINK", length = 255)
	public String getMaxNumberContributionLink() {
		return this.maxNumberContributionLink != null && this.maxNumberContributionLink.length() > 0
				? this.maxNumberContributionLink
				: "";
	}

	public void setMaxNumberContributionLink(String maxNumberContributionLink) {
		this.maxNumberContributionLink = maxNumberContributionLink != null ? Tools.filterHTML(maxNumberContributionLink)
				: "";
	}

	@Column(name = "MAXNUMBERCONTRIBUTION")
	public Long getMaxNumberContribution() {
		return this.maxNumberContribution != null ? this.maxNumberContribution : 0L;
	}

	public void setMaxNumberContribution(Long maxNumberContribution) {
		this.maxNumberContribution = maxNumberContribution != null && maxNumberContribution >= 0L
				? maxNumberContribution
				: 0L;
	}
	
	@Column(name = "SENDCONFIRMATION")
	public Boolean getSendConfirmationEmail() {
		return sendConfirmationEmail  != null ? sendConfirmationEmail : false;
	}

	public void setSendConfirmationEmail(Boolean sendConfirmationEmail) {
		this.sendConfirmationEmail = sendConfirmationEmail  != null ? sendConfirmationEmail : false;
	}

	@Column(name = "SENDREPORT")
	public Boolean getSendReportEmail() {
		return sendReportEmail  != null ? sendReportEmail : false;
	}

	public void setSendReportEmail(Boolean sendReportEmail) {
		this.sendReportEmail = sendReportEmail  != null ? sendReportEmail : false;
	}

	@Column(name = "SENDREPORTFREQUENCY")
	public ReportEmailFrequency getReportEmailFrequency() {
		return reportEmailFrequency  != null ? reportEmailFrequency : ReportEmailFrequency.Never;
	}

	public void setReportEmailFrequency(ReportEmailFrequency reportEmailFrequency) {
		this.reportEmailFrequency = reportEmailFrequency;
	}

	@Column(name = "SENDREPORTEMAILS")
	public String getReportEmails() {
		return reportEmails != null ? reportEmails : "";
	}

	public void setReportEmails(String reportEmails) {
		this.reportEmails = reportEmails;
	}

	public void reorderElementsByPosition() {
		elements.sort(Comparator.comparing(o -> (o.getPosition())));		
	}

	@Column(name = "SHOWCOUNTDOWN")
	public Boolean getShowCountdown() {
		return isShowCountdown != null ? isShowCountdown : false;
	}

	public void setShowCountdown(Boolean isShowCountdown) {
		this.isShowCountdown = isShowCountdown != null ? isShowCountdown : false;
	}

	@Column(name = "TIMELIMIT")
	public String getTimeLimit() {
		return timeLimit != null ? timeLimit :  "";
	}

	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit != null ? timeLimit :  "";
	}
	
	@Column(name = "CRITICALCOMPLEXITY")
	public Boolean getCriticalComplexity() {
		return criticalComplexity != null ? criticalComplexity : false;
	}

	public void setCriticalComplexity(Boolean criticalComplexity) {
		this.criticalComplexity = criticalComplexity != null ? criticalComplexity : false;
	}
	
	@Column(name = "RESULTPRIVILEGES")
	public Boolean getDedicatedResultPrivileges() {
		return dedicatedResultPrivileges;
	}

	public void setDedicatedResultPrivileges(Boolean dedicatedResultPrivileges) {
		this.dedicatedResultPrivileges = dedicatedResultPrivileges != null ? dedicatedResultPrivileges : false;
	}

	@Transient
	public String tagsAsArray() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		boolean first = true;
		for (Tag e : tags) {
			if (first) {
				first = false;
			} else {
				builder.append(",");
			}
			builder.append("'").append(e.getName()).append("'");
		}
		builder.append("]");
		return builder.toString();
	}
	
	@Transient
	public int getTimeLimitInSeconds() {
		if (timeLimit == null || timeLimit.length() == 0) return -1;
		
		String[] arr = timeLimit.split(":");
		return Integer.parseInt(arr[0]) * 3600 + Integer.parseInt(arr[1]) * 60 + Integer.parseInt(arr[2]);		
	}
	
	@Transient
	public int getMaxCandidatesCount()
	{
		if (!getIsEVote()) {
			return 0;
		}
		
		int result = 0;
		
		for (Question q : getQuestions()) {
			if (q instanceof MultipleChoiceQuestion) {
				int size = ((MultipleChoiceQuestion) q).getPossibleAnswers().size();
				if (size > result) {
					result = size;
				}
			}
		}
		
		return result;
	}

	@Column(name = "PROGRESSBAR")
	public Boolean getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(Boolean progressBar) {
		this.progressBar = progressBar != null ? progressBar : false;
	}

	@Column(name = "PROGRESSDISPLAY")
	public Integer getProgressDisplay() {
		return progressDisplay;
	}

	public void setProgressDisplay(Integer progressDisplay) {
		this.progressDisplay = progressDisplay != null ? progressDisplay : 0;
	}

	@Column(name = "MOTIVATIONPOPUP")
	public Boolean getMotivationPopup() {
		return motivationPopup;
	}

	public void setMotivationPopup(Boolean motivationPopup) {
		this.motivationPopup = motivationPopup != null ? motivationPopup : false;
	}

	@Column(name = "MOTIVATIONTYPE")
	public Boolean getMotivationType() {
		return  this.motivationType != null ? this.motivationType : false;
	}

	public void setMotivationType(Boolean motivationType) {
		this.motivationType = motivationType != null ? motivationType : false;
	}

	@Column(name = "CODA_LINK")
	public String getCodaLink() {
		return codaLink;
	}

	public void setCodaLink(String codaLink) {
		this.codaLink = codaLink;
	}

	@Column(name = "CODA_WAITING")
	public Boolean getCodaWaiting() {
		return codaWaiting;
	}

	public void setCodaWaiting(Boolean codaWaiting) {
		this.codaWaiting = codaWaiting != null ? codaWaiting : false;
	}
	
	@Column(name = "ORGANISATION")
	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	
	@Column(name = "VALIDATOR")
	public String getValidator() {
		return validator;
	}

	public void setValidator(String validator) {
		this.validator = validator;
	}

	@Column(name = "QUORUM")
	public Integer getQuorum() {
		return quorum;
	}

	public void setQuorum(Integer quorum) {
		this.quorum = quorum != null ? quorum : 20000;
	}

	@Column(name = "MINLISTPER")
	public Integer getMinListPercent() {
		return minListPercent;
	}

	public void setMinListPercent(Integer minListPercent) {
		this.minListPercent = minListPercent != null ? minListPercent : 5;
	}

	@Column(name = "VALIDATIONCODE")
	public String getValidationCode() {
		return validationCode;
	}

	public void setValidationCode(String validationCode) {
		this.validationCode = validationCode;
	}

	@Column(name = "VALIDATED")
	public Boolean getValidated() {
		return validated;
	}

	public void setValidated(Boolean validated) {
		this.validated = validated != null ? validated : false;;
	}
	
	@Column(name = "WEBHOOK")
	public String getWebhook() {
		return webhook;
	}

	public void setWebhook(String webhook) {
		this.webhook = webhook;
	}

	@Column(name = "DONOTDELETE")
	public Boolean getDoNotDelete() {
		return doNotDelete;
	}

	public void setDoNotDelete(Boolean doNotDelete) {
		this.doNotDelete = doNotDelete != null ? doNotDelete : false;
	}

	public Integer getMaxPrefVotes() {
		if (maxPrefVotes == null) {
			if (eVoteTemplate != null) {
				if (eVoteTemplate.equals("b") || eVoteTemplate.equals("i")) {
					maxPrefVotes = 27;
				} else {
					maxPrefVotes = 20;
				}
			}
		}
		return maxPrefVotes;
	}

	public void setMaxPrefVotes(Integer maxPrefVotes) {
		this.maxPrefVotes = maxPrefVotes;
	}

	public Integer getSeatsToAllocate() {
		if (seatsToAllocate == null) {
			if (eVoteTemplate != null) {
				if (eVoteTemplate.equals("b") || eVoteTemplate.equals("i")) {
					seatsToAllocate = 27;
				} else {
					seatsToAllocate = 20;
				}
			}
		}
		return seatsToAllocate;
	}

	public void setSeatsToAllocate(Integer seatsToAllocate) {
		this.seatsToAllocate = seatsToAllocate;
	}

	public boolean displayAllSAQuestions() {
		for (Question q : this.getQuestions()) {
			if (q instanceof SingleChoiceQuestion) {
				SingleChoiceQuestion scq = (SingleChoiceQuestion)q;
				if (scq.getIsTargetDatasetQuestion()) {
					return scq.getDisplayAllQuestions();
				}
			}
		}
		
		return true;
	}

}
