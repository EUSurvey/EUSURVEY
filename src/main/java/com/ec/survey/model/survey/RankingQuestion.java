package com.ec.survey.model.survey;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.owasp.esapi.errors.ValidationException;

import com.ec.survey.tools.Tools;

/**
 * This is a class for the ranking question
 * participant can sort the items/childElements
 */
@Entity
@DiscriminatorValue("RANKINGQUESTION")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RankingQuestion extends Question {

	private static final long serialVersionUID = 1L;

	private List<Element> childElements = new ArrayList<>();
	private List<Element> missingElements = new ArrayList<>();
	private boolean foreditor;

	public RankingQuestion() {}
	
	public RankingQuestion(String title, String shortname, String uid) {
		super(title, shortname, uid);
	}

	@OneToMany(targetEntity=Element.class, cascade = CascadeType.ALL)  
	@Fetch(value = FetchMode.SELECT)
	@OrderBy(value = "position asc")
	@JoinColumn(nullable=true)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Element> getChildElements() {
		return childElements;
	}
	public void setChildElements(List<Element> childElements) {
		this.childElements = childElements;	
	}

	@Transient
	public List<Element> getMissingElements() {
		return missingElements;
	}	
	public void setMissingElements(List<Element> missingElements) {
		this.missingElements = missingElements;
	}
	
	@Transient
	public List<Element> getAllChildElements() {
		
		if (!missingElements.isEmpty())
		{
			List<Element> result = new ArrayList<>();
			for (Element thatElement : missingElements)
			{
				if (!result.contains(thatElement))
				{
					result.add(thatElement);
				}
			}
			
			result.sort(Survey.newElementByPositionComparator());
			
			return result;
		} else {
			return childElements;
		}		
		
	}	
	
	@Transient
	public Element getChildElement(int id) {
		for (Element thatElement : getAllChildElements()) {
			if (thatElement.getId() == id) {
				return thatElement;
			}
		}
		return null;
	}	
	
	@Transient
	public Element getChildElementsByUniqueId(String uid) {
		for (Element thatElement : getAllChildElements ()) {
			if (thatElement.getUniqueId() != null && thatElement.getUniqueId().length() > 0 && thatElement.getUniqueId().equals(uid)) {
				return thatElement;
			}
		}
		return null;
	}

	@Transient
	public boolean isForeditor() {
		return foreditor;
	}
	public void setForeditor(boolean foreditor) {
		this.foreditor = foreditor;
	}
	
	public RankingQuestion copy(String fileDir) throws ValidationException {
		RankingQuestion copy = new RankingQuestion();
		baseCopy(copy);

		for (Element thatElement : getChildElements()) {
			Element elementCopy = thatElement.copy(fileDir);
			copy.getChildElements().add(elementCopy);
		}

		return copy;
	}

	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element))
			return true;

		if (!(element instanceof RankingQuestion))
			return true;

		RankingQuestion other = (RankingQuestion) element;

		if (getChildElements().size() != other.getChildElements().size())
			return true;
		
		for (int i = 0; i < getChildElements().size(); i++) {
			if (!getChildElements().get(i).getTitle().equals(other.getChildElements().get(i).getTitle()))
				return true;
			if (!Tools.isEqual(getChildElements().get(i).getShortname(),
					other.getChildElements().get(i).getShortname()))
				return true;
		}
		return false;
	}

}
