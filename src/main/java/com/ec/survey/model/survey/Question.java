package com.ec.survey.model.survey;

import com.ec.survey.model.ECFCompetency;
import com.ec.survey.tools.Tools;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *	This is the base class for all question form elements.
 */

@Entity
@Inheritance (strategy=InheritanceType.SINGLE_TABLE)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class Question extends Element {
	
	public static final String FEEDBACK = "FEEDBACK";
	public static final String FIRSTCELL = "FIRSTCELL";
	
	private static final long serialVersionUID = 1L;
	private String help;
	private boolean optional;
	private boolean readonly;
	private boolean attribute;
	private String attributeName;
	private boolean isUnique;
	private int scoring;
	private int quizPoints = 1;
	private List<ScoringItem> scoringItems;
	private boolean delphiQuestion;
	private ECFCompetency ecfCompetency;
	private DelphiChartType delphiChartType;
	private boolean showExplanationBox;
	private boolean noNegativeScore;

	public Question() {
	}

	public Question(String title, String shortname, String uid) {
		this.setTitle(title);
		this.setUniqueId(uid);
		this.setShortname(shortname);
	}

	@Column(name = "ISUNIQUE")
	public Boolean getIsUnique() {
		return isUnique;
	}	
	public void setIsUnique(Boolean isUnique) {
		this.isUnique = isUnique != null && isUnique;
	}
	
	@Column(name = "QHELP")
	@Lob
	public String getHelp() {
		return help;
	}	
	public void setHelp(String help) {
		this.help = help;
	}	
	
	@Column(name = "QOPTIONAL")
	public Boolean getOptional() {
		return optional;
	}	
	public void setOptional(Boolean optional) {
		this.optional = optional != null && optional;
	}
	
	@Column(name = "QREADONLY")
	public Boolean getReadonly() {
		return readonly;
	}	
	public void setReadonly(Boolean readonly) {
		this.readonly = readonly != null && readonly;
	}	
	
	@Column(name = "QATT")
	public Boolean getIsAttribute() {
		return attribute;
	}	
	public void setIsAttribute(Boolean attribute) {
		this.attribute = attribute != null && attribute;
	}
	
	@Column(name = "QATTNAME")
	public String getAttributeName() {
		if (attributeName == null || attributeName.length() == 0)
		{
			return getShortname();
		}
		
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	
	@Column(name = "SCORING")
	public Integer getScoring() {
		return scoring;
	}
	public void setScoring(Integer scoring) {
		this.scoring = scoring != null ? scoring : 0;
	}
	
	@Column(name = "POINTS")
	public Integer getQuizPoints() {
		return quizPoints;
	}
	public void setQuizPoints(Integer quizPoints) {
		this.quizPoints = quizPoints != null ? quizPoints : 1;
	}

	@Column(name = "DELPHI")
	public Boolean getIsDelphiQuestion() {
		return delphiQuestion;
	}

	public void setIsDelphiQuestion(Boolean delphiQuestion) {
		this.delphiQuestion = delphiQuestion != null && delphiQuestion;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "DELPHICHARTTYPE")
	public DelphiChartType getDelphiChartType() {
		if (delphiQuestion && !(this instanceof FreeTextQuestion) && (delphiChartType == DelphiChartType.None)) {
			return getDefaultDelphiChartType(); 
		}		

		if (!delphiQuestion && (delphiChartType == null || delphiChartType == DelphiChartType.None)) {
			if (this instanceof ChoiceQuestion ||
					this instanceof Matrix ||
					this instanceof NumberQuestion ||
					this instanceof RatingQuestion ||
					this instanceof FormulaQuestion) {
				return DelphiChartType.Pie;
			}

			if (this instanceof FreeTextQuestion) {
				return DelphiChartType.WordCloud;
			}
			
			if (this instanceof RankingQuestion) {
				return DelphiChartType.Bar;
			}
			
			if (this instanceof ComplexTableItem) {
				ComplexTableItem item = (ComplexTableItem) this;
				if (item.isChoice() || item.getCellType() == ComplexTableItem.CellType.Formula || item.getCellType() == ComplexTableItem.CellType.Number){
					return DelphiChartType.Pie;
				}
				else if (item.getCellType() == ComplexTableItem.CellType.FreeText) {
					return DelphiChartType.WordCloud;
				}
			}
		}

		return delphiChartType == null ? getDefaultDelphiChartType() : delphiChartType;
	}

	public void setDelphiChartType(DelphiChartType delphiChartType) {
		this.delphiChartType = delphiChartType == null ? getDefaultDelphiChartType() : delphiChartType;
	}

	@Transient
	public DelphiChartType getDefaultDelphiChartType() {
		return this.delphiQuestion ? DelphiChartType.Bar : DelphiChartType.None;
	}
	
	@Transient
	public DelphiChartDataType getDelphiChartDataType() {
		return DelphiChartDataType.Numerical;
	}

	@Column(name = "DELPHIEXPLANATION")
	public Boolean getShowExplanationBox() {
		return showExplanationBox;
	}

	public void setShowExplanationBox(Boolean showExplanationBox) {
		this.showExplanationBox = showExplanationBox == null ? true : showExplanationBox;
	}

	@OneToMany(targetEntity = ScoringItem.class, cascade = CascadeType.ALL)
	@JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
			inverseJoinColumns = @JoinColumn(name = "scoringItems_ID"),
			joinColumns = @JoinColumn(name = "ELEMENTS_ID"))
	@Fetch(value = FetchMode.SELECT)
	@OrderBy(value = "position asc")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<ScoringItem> getScoringItems() {
		return scoringItems;
	}

	public void setScoringItems(List<ScoringItem> scoringItems) {
		this.scoringItems = scoringItems;
	}

	@ManyToOne
	@JoinColumn(name="ECF_COMPETENCY", nullable = true)    
	public ECFCompetency getEcfCompetency() {
		return ecfCompetency;
	}	
	public void setEcfCompetency(ECFCompetency ecfCompetency) {
		this.ecfCompetency = ecfCompetency;
	}
	
	@Column(name = "NONEGATIVE")
	public Boolean getNoNegativeScore() {
		return noNegativeScore;
	}

	public void setNoNegativeScore(Boolean noNegativeScore) {
		this.noNegativeScore = noNegativeScore != null && noNegativeScore;
	}

	protected void baseCopy(Question copy)
	{
		copy.setIsAttribute(getIsAttribute());
		copy.setAttributeName(getAttributeName());
		copy.setShortname(this.getShortname());
		copy.setUniqueId(getUniqueId());
		copy.setHelp(Tools.filterHTML(getHelp()));
		copy.setOptional(getOptional());
		copy.setReadonly(getReadonly());
		copy.setSourceId(getId());
		copy.setTitle(Tools.filterHTML(getTitle()));
		copy.setPosition(this.getPosition());
		copy.setIsUnique(getIsUnique());
		copy.setQuizPoints(quizPoints);
		copy.setScoring(scoring);
		copy.setLocked(getLocked());
		copy.setSubType(getSubType());
		copy.setDisplayMode(getDisplayMode());
		copy.setIsDelphiQuestion(getIsDelphiQuestion());
		copy.setUseAndLogic(getUseAndLogic());
		copy.setShowExplanationBox(getShowExplanationBox());
		copy.setDelphiChartType(getDelphiChartType());
		copy.setNoNegativeScore(getNoNegativeScore());
		
		if (ecfCompetency != null) {
			copy.setEcfCompetency(this.getEcfCompetency());
		}
		if (scoringItems != null) {
			copy.setScoringItems(new ArrayList<>());
			for (ScoringItem item : scoringItems) {
				copy.getScoringItems().add(item.copy());
			}
		}
	}
	
	@Transient
	public String getCss()
	{
		String css = "";
		
		if (!optional) css = "required";	
		
		if (isUnique)
		{
			css += " unique";
		}
		
		return css;
	}

	@Transient
	@Override
	protected boolean basicDiffersFrom(Element element)
	{
		if (getShortname() != null && !getShortname().equals(element.getShortname())) return true;
		if (getTitle() != null && !getTitle().equals(element.getTitle())) return true;
		
		if (getUseAndLogic() != null && !getUseAndLogic().equals(element.getUseAndLogic())) return true;

		if (element instanceof Question)
		{
			Question question = (Question) element;

			if (help != null && !help.equals(question.help))
			{
				if (help.length() == 0 && question.help == null)
				{
					//this can happen
				} else {
					return true;
				}
			}

			if (scoring != question.scoring)
			{
				return true;
			}

			if (quizPoints != question.quizPoints)
			{
				return true;
			}

			if (scoring > 0 && (scoringItems != null || question.scoringItems != null))
			{
				if (scoringItems != null && question.scoringItems == null)
				{
					return true;
				} else if (scoringItems == null && question.scoringItems != null)
				{
					return true;
				}

				if (scoringItems.size() != question.scoringItems.size())
				{
					return true;
				}

				for (int i = 0; i < scoringItems.size(); i++) {
					if (scoringItems.get(i).differsFrom(question.getScoringItems().get(i))) {
						return true;
					}
				}
			}

			if (!(Objects.equals(getOptional(), question.getOptional()))) return true;
			if (!(Objects.equals(getReadonly(), question.getReadonly()))) return true;

			if (!(Objects.equals(getAttributeName(), question.getAttributeName()))) return true;
			if (!(Objects.equals(getIsDelphiQuestion(), question.getIsDelphiQuestion()))) return true;
			if (!(Objects.equals(getShowExplanationBox(), question.getShowExplanationBox()))) return true;
			if (!(Objects.equals(getDelphiChartType(), question.getDelphiChartType()))) return true;
			if (!(Objects.equals(getNoNegativeScore(), question.getNoNegativeScore()))) return true;
		} else {
			return true;
		}

		return false;
	}
		
}
