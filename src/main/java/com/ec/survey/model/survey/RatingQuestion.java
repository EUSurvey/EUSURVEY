package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a rating question in a survey
 */
@Entity
@DiscriminatorValue("RATING")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RatingQuestion extends Question {
	
	private static final long serialVersionUID = 1L;
	private List<Element> missingQuestions = new ArrayList<>();

	public RatingQuestion() {}
	
	public RatingQuestion(Survey survey, String title, String shortname, String uid) {
		super(title, shortname, uid);
	}
	
	private int numIcons = 5;
	private int iconType = 0;
	protected List<Element> childElements = new ArrayList<>();
	
	@Column(name = "NUMICONS")
	public int getNumIcons() {
		return numIcons;
	}	
	public void setNumIcons(int numIcons) {
		this.numIcons = numIcons;
	}
	
	@Column(name = "ICONTYPE")
	public int getIconType() {
		return iconType;
	}	
	public void setIconType(int iconType) {
		this.iconType = iconType;
	}
	
	@SuppressWarnings("deprecation")
	@OneToMany(targetEntity=Element.class, cascade = CascadeType.ALL)  
	@Fetch(value = FetchMode.SELECT)
	@OrderBy(value = "position asc")
	@JoinColumn(nullable=true, foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT))
	@org.hibernate.annotations.ForeignKey(name = "none")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Element> getChildElements() {
		return childElements;
	}
	public void setChildElements(List<Element> childElements) {
		this.childElements = childElements;	
	}
	
	@Transient
	public List<Element> getMissingQuestions() {
		return missingQuestions;
	}
	public void setMissingQuestions(List<Element> missingQuestions) {
		this.missingQuestions = missingQuestions;
	}	
	
	@Transient
	public List<Element> getQuestions()
	{
		List<Element> result = new ArrayList<>();

		result.addAll(childElements);
        result.addAll(missingQuestions);
		
		return result;
	}
	
	public boolean containsChild(int id)
	{
		for (Element element :  childElements)
		{
			if (element.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}
	
	public RatingQuestion copy(String fileDir) throws ValidationException
	{
		RatingQuestion copy = new RatingQuestion();
		baseCopy(copy);
		copy.numIcons = numIcons;
		copy.iconType = iconType;	
		
		for (Element element : childElements) {
			Element c = element.copy(fileDir);
			copy.childElements.add(c);
		}
		
		return copy;
	}
	
	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element)) return true;
		
		if (!(element instanceof RatingQuestion)) return true;
		
		RatingQuestion rating = (RatingQuestion)element;

		if (numIcons != rating.numIcons || iconType != rating.iconType) {
			return true;
		}
		
		if (getChildElements().size() != rating.getChildElements().size()) return true;
		
		for (int r = 0; r < getChildElements().size(); r++)
		{
			if (this.getChildElements().get(r).differsFrom(rating.getChildElements().get(r))) return true;
		}
		
		return false;
	}
	
}
