package com.ec.survey.model.survey;


import com.ec.survey.model.survey.base.File;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a gallery question in a survey
 */
@Entity
@DiscriminatorValue("GALLERYQUESTION")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class GalleryQuestion extends Question {
	
	private static final long serialVersionUID = 1L;

	public GalleryQuestion() {}
	
	public GalleryQuestion(String title, String shortname, String uid) {
		super(title, shortname, uid);
	}
	
	private Integer columns;
	private Integer limit;
	private boolean selection;
	private boolean numbering;
	private List<File> files = new ArrayList<>();
	
	@Column(name = "COLS")
	public Integer getColumns() {
		return columns;
	}	
	public void setColumns(Integer columns) {
		this.columns = columns;
	}
	
	@Column(name = "SELLIMIT")
	public Integer getLimit() {
		return limit;
	}	
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
	@Column(name = "SELECTION")
	public boolean getSelection() {
		return selection;
	}	
	public void setSelection(boolean selection) {
		this.selection = selection;
	}
	
	@Column(name = "NUMBERING")
	public Boolean getNumbering() {
		return numbering;
	}	
	public void setNumbering(Boolean numbering) {
		this.numbering = numbering != null ? numbering : false;
	}
	
	@SuppressWarnings("deprecation")
	@ManyToMany(targetEntity=File.class, cascade = CascadeType.ALL  ) 
	@JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT))
	@org.hibernate.annotations.ForeignKey(name = "none")
	@Fetch(value = FetchMode.SELECT)
	@OrderBy(value = "position asc")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<File> getFiles() {
		return files;
	}
	public void setFiles(List<File> files) {
		this.files = files;
	}
	
	public GalleryQuestion copy(String fileDir) throws ValidationException
	{
		GalleryQuestion copy = new GalleryQuestion();
		baseCopy(copy);
		copy.limit = limit;
		copy.columns = columns;
		copy.selection = selection;
		copy.setNumbering(this.getNumbering());
		
		for (File file : files) {
			File copyFile = file.copy(fileDir);
			copy.files.add(copyFile);
		}
						
		return copy;
	}
	
	@Transient
	@Override
	public String getCss()
	{
		String css = super.getCss();
		
		css += " gallery";
		
		if (limit != null && limit > 0)
		{
			css += " limit" + limit.toString();
		}
		
		if (columns != null)
		{
			css += " columns" + columns.toString();
		}
		
		if (selection)
		{
			css += " selection";
		}
		
		return css;
	}
	
	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element)) return true;
		
		if (!(element instanceof GalleryQuestion)) return true;
		
		GalleryQuestion gallery = (GalleryQuestion)element;

		if (columns != null && !columns.equals(gallery.columns)) return true;
		if (limit != null && !limit.equals(gallery.limit)) return true;
		if (selection != gallery.selection) return true;
		
		for (File file: files)
		{
			boolean found = false;
			for (File otherFile: gallery.files)
			{
				if (otherFile.getName().equals(file.getName()) && otherFile.getComment().equals(file.getComment()))
				{
					found = true;
					break;
				}
			}
			if (!found) return true;
		}

		for (File file: gallery.files)
		{
			boolean found = false;
			for (File otherFile: files)
			{
				if (otherFile.getName().equals(file.getName()) && otherFile.getComment().equals(file.getComment()))
				{
					found = true;
					break;
				}
			}
			if (!found) return true;
		}
		
		return false;
	}
	
}
