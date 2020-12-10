package com.ec.survey.model.survey.base;

import com.ec.survey.tools.ConversionTools;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**
 * Represents a file in the file system.
 * Used for example for survey logos, uploaded files,
 * exports etc.
 */
@Entity
@Table(name = "FILES")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class File implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String uid;
	private Integer width = 0;
	private String comment;
	private Integer position;
	private Date deletionDate;
	private Integer answerId;
	private String longdesc;
	private String questionUid;
	
	@Id
	@Column(name = "FILE_ID", nullable = false)
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "FILE_NAME", nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Lob
	@Column(name = "FILE_COMMENT", length = 40000)
	public String getComment() {
		return comment;
	}	
	public void setComment(String comment) {
		this.comment = comment;
	}	
	
	@Transient
	public String getCleanComment()
	{
		return ConversionTools.removeHTML(comment, false);
	}
	
	@Column(name = "FILE_UID", nullable = false)
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Column(name = "FILE_WIDTH", nullable = false)
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	
	@Column(name = "FILE_POS")
	public Integer getPosition() {
		return position;
	}	
	public void setPosition(Integer position) {
		this.position = position;
	}
	
	@Column(name = "FILE_DEL")
	public Date getDeletionDate() {
		return deletionDate;
	}
	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}
	
	//this one is only used for gallery images
	@Column(name = "FILE_LONGDESC")
	public String getLongdesc() {
		return longdesc;
	}
	public void setLongdesc(String longdesc) {
		this.longdesc = longdesc;
	}
	
	@Transient
	public File copy(String fileDir)
	{
		File copyFile = new File();
		copyFile.name = name;
		copyFile.uid = uid;
		copyFile.comment = comment;
		copyFile.position = position;
		copyFile.longdesc = longdesc;

		return copyFile;
	}
	
	@Transient
	public Integer getAnswerId() {
		return answerId;
	}
	public void setAnswerId(Integer answerId) {
		this.answerId = answerId;
	}
	
	@Transient
	public String getNameForExport()
	{
		return getName().replace(";", "").replace("|", "");
	}
	
	@Transient
	public String getQuestionUid() {
		return questionUid;
	}
	public void setQuestionUid(String questionUid) {
		this.questionUid = questionUid;
	}
}
