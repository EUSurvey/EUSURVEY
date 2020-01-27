package com.ec.survey.model.survey;

import com.ec.survey.tools.Tools;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an answer option inside a (single or multiple) choice question.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PossibleAnswer extends Element {
	
	private static final long serialVersionUID = 1L;
	private int questionId;
	private DependencyItem dependentElements = new DependencyItem();
	private String savedDependentElementsString = "";
	private ScoringItem scoring;
		
	@Column(name="QUESTION_ID")
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	
	public PossibleAnswer copy(String fileDir) throws ValidationException, IntrusionException
	{
		PossibleAnswer copy = new PossibleAnswer();
		copy.setUniqueId(getUniqueId());
		copy.setShortname(this.getShortname());
		copy.setSourceId(this.getId());
		copy.setTitle(Tools.filterHTML(this.getTitle()));
		copy.setPosition(this.getPosition());
		if (scoring != null) copy.setScoring(scoring.copy());
		
		return copy;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public DependencyItem getDependentElements() {
		return dependentElements;
	}
	public void setDependentElements(DependencyItem dependentElements) {
		this.dependentElements = dependentElements;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_score", nullable=true)
	public ScoringItem getScoring() {
		return scoring;
	}
	public void setScoring(ScoringItem scoring) {
		this.scoring = scoring;
	}
	
	@Transient
	public String getDependentElementsString()
	{
		StringBuilder s = new StringBuilder(savedDependentElementsString);
		for (Element element : dependentElements.getDependentElements())
		{
			s.append(element.getId()).append(";");
		}
		return s.toString();
	}
	
	@Transient
	public String getTitleForDisplayMode(Integer displayMode)
	{
		if (displayMode != null)
		{
			switch (displayMode)
			{
				case 0:
					return this.getTitle();
				case 1:
					return this.getShortname();
				case 2:
					return this.getShortname() + " - " + this.getTitle();
				case 3:
					return this.getTitle() + " (" + this.getShortname() + ")";
			}
		}
		return this.getTitle();
	}
	
	@Transient
	public String getDependentElementsUIDString()
	{
		List<String> list = new ArrayList<>();

		for (Element element : dependentElements.getDependentElements())
		{
			list.add(element.getUniqueId());
		}

		Collections.sort(list);
		
		StringBuilder s = new StringBuilder();
		for (String uid : list)
		{
			s.append(uid).append(";");
		}
		
		return s.toString();
	}
	
	public void clearForJSON()
	{
		savedDependentElementsString = getDependentElementsString();
		dependentElements.getDependentElements().clear();
	}
	
	@Override
	public boolean differsFrom(Element element) {
		return false;
	}
}
