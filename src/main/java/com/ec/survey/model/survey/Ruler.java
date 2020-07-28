package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.*;

/**
 * Represents a ruler element in a survey
 */
@Entity
@DiscriminatorValue("RULER")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Ruler extends Question {
	
	private static final long serialVersionUID = 1L;
	private Integer height;
	private String color;
	private String style;
	
	public Ruler(String text, String uid) {
		setTitle(text);
		setUniqueId(uid);
	}
	
	public Ruler() {
	}	
	
	@Column(name = "COLOR")
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
		
	@Column(name = "HEIGHT")
	public Integer getHeight() {
		return height;
	}	
	public void setHeight(Integer height) {
		this.height = height;
	}
	
	@Column(name = "STYLE")
	public String getStyle() {
		return style;
	}	
	public void setStyle(String style) {
		this.style = style;
	}
	
	public Ruler copy(String fileDir) throws ValidationException
	{
		Ruler copy = new Ruler();
		baseCopy(copy);
		copy.color = color;
		copy.height = height;
		copy.style = style;
		return copy;
	}
	
	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element)) return true;
		
		if (!(element instanceof Ruler)) return true;
		
		Ruler ruler = (Ruler)element;

		if (color != null && !color.equals(ruler.color)) return true;
		if (height != null && !height.equals(ruler.height)) return true;
		if (style != null && !style.equals(ruler.style)) return true;
	
		return false;
	}
}
