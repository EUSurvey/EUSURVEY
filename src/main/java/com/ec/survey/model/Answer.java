package com.ec.survey.model;

import com.ec.survey.model.survey.base.File;
import com.ec.survey.tools.Tools;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents one single answer to one single question inside
 * an AnswerSet
 */
@Entity
@Table(name = "ANSWERS", indexes = {@Index(name = "PA_UID_IDX", columnList = "PA_UID, AS_ID ASC"), @Index(name = "Q_UID_IDX", columnList = "QUESTION_UID, AS_ID ASC")})
public class Answer implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String questionUniqueId;
	private String possibleAnswerUniqueId;
	private int questionId;
	private int possibleAnswerId;
	private int sourceQuestionId;
	private int answerSetId;
	private AnswerSet answerSet;
	private String value;
	private String title;
	private List<File> files = new ArrayList<>();
	private int row;
	private int column;
	private Boolean isDraft = false;
	
	@Id
	@Column(name = "ANSWER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Lob
	@Column(name = "VALUE")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		if (value != null && Tools.containsNonUTF83Bytes(value)) {
			this.value = Tools.toUTF83Bytes(value);
		}else {
			this.value = value;
		}
	}
	
	@Transient
	public String getValueEscaped()
	{
		return JSONObject.escape(value);
	}
	
	@Column(name = "QUESTION_UID")
	public String getQuestionUniqueId() {
		return questionUniqueId;
	}
	public void setQuestionUniqueId(String questionUniqueId) {
		this.questionUniqueId = questionUniqueId;
	}

	//this property is not used anymore but we keep it in order not to break the import/export feature (backwards compatibility)
	@Transient
	public Integer getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Integer questionId) {
		this.questionId = questionId != null ? questionId : 0;
	}
	
	@Column(name = "PA_UID")
	public String getPossibleAnswerUniqueId() {
		return possibleAnswerUniqueId;
	}
	public void setPossibleAnswerUniqueId(String possibleAnswerUniqueId) {
		this.possibleAnswerUniqueId = possibleAnswerUniqueId;
	}
	
	//this property is not used anymore but we keep it in order not to break the import/export feature (backwards compatibility)
	@Transient
	public Integer getPossibleAnswerId() {
		return possibleAnswerId;
	}
	public void setPossibleAnswerIdWeg(Integer possibleAnswerId) {
		this.possibleAnswerId = possibleAnswerId != null ?  possibleAnswerId : 0;
	}
	
	//this property is not used anymore but we keep it in order not to break the import/export feature (backwards compatibility)
	@Transient
	public Integer getSourceQuestionId() {
		return sourceQuestionId;
	}
	public void setSourceQuestionId(Integer sourceQuestionId) {
		this.sourceQuestionId = sourceQuestionId != null ?  sourceQuestionId : 0;
	}
	
	@Transient
	public int getAnswerSetId() {
		
		if (answerSet != null) return answerSet.getId();		
		return answerSetId;
	}
	public void setAnswerSetId(int answerSetId) {
		this.answerSetId = answerSetId;
	}

	@ManyToOne  
	@JoinColumn(name="AS_ID")        
	public AnswerSet getAnswerSet() {return answerSet;}  
	public void setAnswerSet(AnswerSet s) {this.answerSet = s;}  

	@OneToMany(targetEntity=File.class, cascade = CascadeType.ALL  )
	@JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
			inverseJoinColumns = @JoinColumn(name = "files_FILE_ID"),
			joinColumns = @JoinColumn(name = "ANSWERS_ANSWER_ID"))
	@Fetch(value = FetchMode.SELECT)
	@OrderBy(value = "name asc")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<File> getFiles() {
		return files;
	}
	public void setFiles(List<File> files) {
		this.files = files;
	}

	@Column(name = "ANSWER_ROW")
	public Integer getRow() {
		return row;
	}
	public void setRow(Integer row) {
		this.row = row != null ? row : 0;
	}

	@Column(name = "ANSWER_COL")
	public Integer getColumn() {
		return column;
	}
	public void setColumn(Integer column) {
		this.column = column != null ? column : 0;
	}

	//this property is not used anymore but we keep it in order not to break the import/export feature (backwards compatibility)
	@Transient
	public Boolean getIsDraft() {
		return isDraft;
	}
	public void setIsDraft(Boolean isDraft) {
		this.isDraft = isDraft;
	}
	
	public Answer copy(AnswerSet b, Map<Integer, List<File>> files) {
		Answer bn = new Answer();
		bn.answerSet = b;
		bn.setColumn(getColumn());
		bn.possibleAnswerUniqueId = possibleAnswerUniqueId;
		bn.questionUniqueId = questionUniqueId;
		bn.setRow(getRow());
		bn.setSourceQuestionId(getSourceQuestionId());
		bn.setValue(value);
		bn.isDraft = isDraft;
		if (files.containsKey(id))
		{
			for (File original: files.get(id))
			{
				File copy = new File();
				copy.setName(original.getName());
				copy.setUid(original.getUid()); //files are recreated in SurveyExportHelper.importSurvey()
				bn.files.add(copy);
			}
		}
		return bn;
	}

	
	@Transient
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}


}
