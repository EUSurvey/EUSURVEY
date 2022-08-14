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
 * Represents a download element in a survey
 */
@Entity
@DiscriminatorValue("DOWNLOAD")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Download extends Question {
	
	private static final long serialVersionUID = 1L;

	public Download(){}
	
	private List<File> files = new ArrayList<>();
	
	public Download(String text, String shortname, String uid) {
		setTitle(text);
		setUniqueId(uid);
		setShortname(shortname);
	}
	
	@ManyToMany(targetEntity=File.class, cascade = CascadeType.ALL  )
	@JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
			inverseJoinColumns = @JoinColumn(name = "files_FILE_ID"),
			joinColumns = @JoinColumn(name = "ELEMENTS_ID"))
	@Fetch(value = FetchMode.SELECT)
	@OrderBy(value = "position asc")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<File> getFiles() {
		return files;
	}
	public void setFiles(List<File> files) {
		this.files = files;
	}
	
	public Download copy(String fileDir) throws ValidationException
	{
		Download copy = new Download();
		baseCopy(copy);
	
		try {
		
			for (File file : files)
			{
				File copyFile = file.copy(fileDir);
				copy.files.add(copyFile);
			}
		
		} catch (org.hibernate.LazyInitializationException e)
		{
			//ignore
		}
		
		return copy;
	}
	
	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element)) return true;
		
		if (!(element instanceof Download)) return true;
		
		Download download = (Download)element;
		
		for (File file: files)
		{
			boolean found = false;
			for (File otherFile: download.files)
			{
				if (otherFile.getName().equals(file.getName()) && (otherFile.getComment() == null || otherFile.getComment().equals(file.getComment())))
				{
					found = true;
					break;
				}
			}
			if (!found) return true;
		}

		for (File file: download.files)
		{
			boolean found = false;
			for (File otherFile: files)
			{
				if (otherFile.getName().equals(file.getName()) && (otherFile.getComment() == null || otherFile.getComment().equals(file.getComment())))
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
