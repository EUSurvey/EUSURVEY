package com.ec.survey.model;

import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.Matrix;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.tools.ConversionTools;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.*;

/**
 * Represents a complete contribution of one specific user to one specific
 * survey. It contains a list of Answer instances.
 */
@Entity
@Table(name = "ANSWERS_SET", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "UNIQUECODE", "ISDRAFT" }, name = "UNIQUECODE_UNIQUE") })
public class AnswerSet implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String invitationId;
	private Date date;
	private Date updateDate;
	private Survey survey;
	private int surveyId;
	private String responderEmail;
	private String uniqueCode;
	private String languageCode;
	private String IP;
	private List<Answer> answers = new ArrayList<>();
	private boolean isDraft = false;
	private String draftId;
	private Boolean disclaimerMinimized;
	private Boolean wcagMode;
	private Integer score;

	@Id
	@Column(name = "ANSWER_SET_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ANSWER_SET_DATE")
	@DateTimeFormat(pattern = ConversionTools.DateTimeFormat)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "ANSWER_SET_UPDATE")
	@DateTimeFormat(pattern = ConversionTools.DateTimeFormat)
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@Column(name = "SURVEY_ID", insertable = false, updatable = false)
	public int getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}

	@ManyToOne
	@JoinColumn(name = "SURVEY_ID")
	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	@Column(name = "RESPONDER_EMAIL")
	public String getResponderEmail() {
		return responderEmail;
	}

	public void setResponderEmail(String responderEmail) {
		this.responderEmail = responderEmail;
	}

	@Column(name = "UNIQUECODE", length = 36)
	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	@Column(name = "ISDRAFT")
	public boolean getIsDraft() {
		return isDraft;
	}

	public void setIsDraft(boolean isDraft) {
		this.isDraft = isDraft;
	}

	@OneToMany(targetEntity = Answer.class, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "answerSet")
	@Fetch(value = FetchMode.SELECT)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	@Transient
	public void addAnswer(Answer answer) {
		answers.add(answer);
	}

	@Transient
	public List<Answer> getAnswers(int questionId) {
		List<Answer> result = new ArrayList<>();
		for (Answer answer : answers) {
			if (answer.getQuestionId().equals(questionId)) {
				result.add(answer);
			}
		}

		return result;
	}

	@Transient
	public List<Answer> getMatrixAnswers(Matrix matrix) {
		List<Answer> result = new ArrayList<>();
		Set<Integer> questionIds = new HashSet<>();
		for (Element question : matrix.getQuestions()) {
			questionIds.add(question.getId());
		}
		for (Answer answer : answers) {
			if (questionIds.contains(answer.getQuestionId())) {
				result.add(answer);
			}
		}

		return result;
	}

	@Transient
	public List<Answer> getAnswers(int questionId, String questionUid) {
		List<Answer> result = new ArrayList<>();
		for (Answer answer : answers) {
			if (answer.getQuestionId().equals(questionId)
					|| (answer.getQuestionUniqueId() != null && answer.getQuestionUniqueId().equals(questionUid))) {
				result.add(answer);
			}
		}

		return result;
	}

	@Transient
	public String getMatrixAnswer(int questionId, int answerId) {
		for (Answer answer : answers) {
			if (answer.getQuestionId().equals(questionId) && answer.getPossibleAnswerId().equals(answerId)) {
				return answer.getValue();
			}
		}

		return null;
	}

	@Transient
	public String getMatrixAnswer(String questionUid, String answerUid) {
		for (Answer answer : answers) {
			if (answer.getQuestionUniqueId() != null && answer.getPossibleAnswerUniqueId() != null
					&& answer.getQuestionUniqueId().equals(questionUid)
					&& answer.getPossibleAnswerUniqueId().equals(answerUid)) {
				return answer.getValue();
			}
		}

		return null;
	}

	@Transient
	public String getTableAnswer(Element question, int row, int col, boolean escape) {
		for (Answer answer : answers) {
			if ((answer.getQuestionId().equals(question.getId()) || (answer.getQuestionUniqueId() != null
					&& answer.getQuestionUniqueId().equals(question.getUniqueId()))) && answer.getRow().equals(row)
					&& answer.getColumn().equals(col)) {
				if (escape) {
					return StringEscapeUtils.escapeXml(answer.getValue());
				}

				return answer.getValue();
			}
		}

		return null;
	}

	@Transient
	public void clearAnswers(Element question) {
		List<Answer> matchinganswers = getAnswers(question.getId());

		answers.removeAll(matchinganswers);

	}

	public void addAnswers(List<Answer> answers) {
		for (Answer answer : answers) {
			addAnswer(answer);
		}
	}

	@Column(name = "ANSWER_SET_INVID")
	public String getInvitationId() {
		return invitationId;
	}

	public void setInvitationId(String invitationId) {
		this.invitationId = invitationId;
	}

	@Column(name = "ANSWER_SET_LANG")
	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	@Column(name = "ANSWER_SET_DISCLAIMER")
	public Boolean getDisclaimerMinimized() {
		return disclaimerMinimized != null && disclaimerMinimized;
	}

	public void setDisclaimerMinimized(Boolean disclaimerMinimized) {
		this.disclaimerMinimized = disclaimerMinimized;
	}

	@Column(name = "ANSWER_SET_WCAG")
	public Boolean getWcagMode() {
		return wcagMode;
	}

	public void setWcagMode(Boolean wcagMode) {
		this.wcagMode = wcagMode;
	}

	@Transient
	public String getNiceDate() {
		return date != null ? ConversionTools.getFullString(date) : "";
	}

	@Transient
	public String getNiceUpdateDate() {
		return updateDate != null ? ConversionTools.getFullString(updateDate) : "";
	}

	@Transient
	public AnswerSet copy(Survey survey, Map<Integer, List<File>> files) {
		AnswerSet copy = new AnswerSet();
		copy.date = date;
		copy.invitationId = invitationId;
		copy.IP = IP;
		copy.setIsDraft(isDraft);
		copy.languageCode = languageCode;
		copy.disclaimerMinimized = disclaimerMinimized;
		copy.wcagMode = wcagMode;
		copy.responderEmail = responderEmail;
		copy.survey = survey;
		copy.surveyId = survey.getId();
		copy.uniqueCode = uniqueCode;
		copy.updateDate = updateDate;
		copy.score = score;

		for (Answer answer : answers) {
			Answer copyanswer = answer.copy(copy, files);
			copy.answers.add(copyanswer);
		}

		return copy;
	}

	@Transient
	public String getDraftId() {
		return draftId;
	}

	public void setDraftId(String draftId) {
		this.draftId = draftId;
	}

	@Transient
	public String serialize() {
		StringBuilder result = new StringBuilder();

		for (Answer answer : answers) {
			result.append(" ").append(answer.getQuestionUniqueId()).append(":").append(answer.getValue()).append(";");
		}

		return result.toString();
	}

	@Column(name = "SCORE")
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	@Transient
	public List<String> getAllFiles() {
		List<String> result = new ArrayList<>();
		for (Answer answer : answers) {
			if (answer.getFiles() != null) {
				for (File file : answer.getFiles()) {
					result.add(file.getUid());
				}
			}
		}
		return result;
	}
}
