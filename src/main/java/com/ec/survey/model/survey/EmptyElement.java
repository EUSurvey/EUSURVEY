package com.ec.survey.model.survey;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;

import com.ec.survey.tools.Tools;

/**
 * This is a dummy element used inside matrices / tables to represent the first
 * (empty) table cell
 */
@Entity
@DiscriminatorValue("EMPTY")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EmptyElement extends Element {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptyElement(String ptitle, String shortname) {
		setTitle(ptitle);
		setShortname(shortname);
	}
	
	public EmptyElement() {
		this.setUniqueId("");
	}	
	
	public EmptyElement copy(String fileDir) throws ValidationException
	{	
		EmptyElement copy = new EmptyElement();
		copy.setUniqueId(getUniqueId());
		copy.setShortname(this.getShortname());
		copy.setSourceId(this.getId());
		copy.setTitle(Tools.filterHTML(this.getTitle()));
		copy.setPosition(this.getPosition());
		
		return copy;
	}

	@Override
	public boolean differsFrom(Element element) {
		return false;
	}
	
	@Transient
	public String getCss()
	{
		return "";
	}
	
	@Transient
	public boolean getOptional() {
		return true;
	}
	
	@Transient
	public Boolean getReadonly() {
		return false;
	}

}
