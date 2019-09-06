package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.*;

/**
 * Represents an image element in a survey
 */
@Entity
@DiscriminatorValue("IMAGE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Image extends Question {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer scale;
	private String align;
	private String url;
	private Integer width;
	private String longdesc;
	private String filename;
	
	public Image(Survey survey, String text, String uid) {
		setTitle(text);
		setUniqueId(uid);
	}
	
	public Image() {
	}	
	
	@Column(name = "LONGDESC")
	public String getLongdesc() {
		return longdesc;
	}
	public void setLongdesc(String longdesc) {
		this.longdesc = longdesc;
	}
		
	@Column(name = "SCALE")
	public Integer getScale() {
		return scale;
	}	
	public void setScale(Integer scale) {
		this.scale = scale;
	}
	
	@Column(name = "IM_WIDTH")
	public Integer getWidth() {
		return width;
	}	
	public void setWidth(Integer width) {
		this.width = width;
	}
	
	@Column(name = "ALIGN")
	public String getAlign() {
		return align;
	}	
	public void setAlign(String align) {
		this.align = align;
	}
	
	@Column(name = "URL")
	public String getUrl() {
		return url;
	}	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Image copy(String fileDir) throws ValidationException, IntrusionException
	{
		Image copy = new Image();
		baseCopy(copy);
		copy.align = align;
		copy.scale = scale;
		copy.longdesc = longdesc;
		copy.url = url;
		copy.width = width;
		return copy;
	}
	
	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element)) return true;
		
		if (!(element instanceof Image)) return true;
		
		Image image = (Image)element;

		if (align != null && !align.equals(image.align)) return true;
		if (scale != null && !scale.equals(image.scale)) return true;
		if (url != null && !url.equals(image.url)) return true;
		if (width != null && !width.equals(image.width)) return true;
		
		return false;
	}
	
	@Transient
	public String titleOrFilename()
	{
		if (getTitle() != null && getTitle().length() > 0) return getTitle();
		
		return url.substring(url.lastIndexOf('/')+1);
	}
	
	@Transient
	public String getFilename() {
		return filename;
	}	
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
