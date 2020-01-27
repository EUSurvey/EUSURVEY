package com.ec.survey.model.survey;

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
	
	public final static String FEEDBACK = "FEEDBACK";
	
	private static final long serialVersionUID = 1L;
	private String help;
	private boolean optional;
	private boolean readonly;
	private boolean attribute;
	private String attributeName;
	private boolean isUnique;
	private int scoring;
	private int points = 1;
	private List<ScoringItem> scoringItems;
	
	public Question() {}
	
	public Question(Survey survey, String title, String shortname, String uid) {
		this.setTitle(title);
		this.setUniqueId(uid);
		this.setShortname(shortname);
	}
	
	@Column(name = "ISUNIQUE")
	public Boolean getIsUnique() {
		return isUnique;
	}	
	public void setIsUnique(Boolean isUnique) {
		this.isUnique = isUnique != null ? isUnique : false;
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
		this.optional = optional == null ? false : optional;
	}
	
	@Column(name = "QREADONLY")
	public Boolean getReadonly() {
		return readonly;
	}	
	public void setReadonly(Boolean readonly) {
		this.readonly = readonly == null ? false : readonly;
	}	
	
	@Column(name = "QATT")
	public Boolean getIsAttribute() {
		return attribute;
	}	
	public void setIsAttribute(Boolean attribute) {
		this.attribute = attribute == null ? false : attribute;
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
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points != null ? points : 1;
	}
	
	@OneToMany(targetEntity=ScoringItem.class, cascade = CascadeType.ALL)
	@Fetch(value = FetchMode.SELECT)
	@OrderBy(value = "position asc")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<ScoringItem> getScoringItems() {
		return scoringItems;
	}	
	public void setScoringItems(List<ScoringItem> scoringItems) {
		this.scoringItems = scoringItems;
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
		copy.setPoints(points);
		copy.setScoring(scoring);
		copy.setLocked(getLocked());
		copy.setSubType(getSubType());
		copy.setDisplayMode(getDisplayMode());
		
		if (scoringItems != null)
		{
			copy.setScoringItems(new ArrayList<>());
			for (ScoringItem item: scoringItems)
			{
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
			
			if (points != question.points)
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
				
				for (int i = 0; i < scoringItems.size(); i++)
				{
					if (scoringItems.get(i).differsFrom(question.getScoringItems().get(i)))
					{
						return true;
					}
				}
			}
			
			if (!(Objects.equals(getOptional(), question.getOptional()))) return true;			
			if (!(Objects.equals(getReadonly(), question.getReadonly()))) return true;
			
			if (!(Objects.equals(getAttributeName(), question.getAttributeName()))) return true;
			
		} else {
			return true;
		}
		
		return false;
	}
		
}
