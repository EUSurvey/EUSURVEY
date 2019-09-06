package com.ec.survey.model.machinetranslation;

import com.ec.survey.tools.ConversionTools;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author filipsl
 *
 */

@Entity
@Table(name = "MT_REQUEST")
public class Request implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	/**
	 * GUID to identify unique translation request id
	 */
	private String uniqueId;
	/**
	 * source language
	 */
	private String sourceLang;
	
	/**
	 * comma separated list of target languages 
	 */
	private String targetLangs;
	
	/**
	 * text to translate 
	 */
	private String text;
	
	
	/**
	 *  FTP URL of file that need to be translated 
	 */
	private String fileURL;
	
	/**
	 *	login of user that requested translation  
	 */
	private String username;
	
	/**
	 *	date and time when translation request is created 
	 */
	private Date created;
	
	/**
	 *	id of translations that need to be translated
	 */
	private Integer translationsID;
	
	/**
	 *	id of translation that need to be translated if null translate all translations
	 */
	private Integer translationID;
	
	/**
	 * if true send user email user
	 */
	private boolean isNotify;
	
	/**
	 *  email of user that required translation 
	 */
	private String email;
	
	private Set<Response> responses = new HashSet<>();
	
	public Request(){
		created = new Date();
	}
	 
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "UID", nullable = false,unique=true)
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Column(name = "SOURCE_LANG", nullable = false ,length=2)
	public String getSourceLang() {
		return sourceLang;
	}
	public void setSourceLang(String sourceLang) {
		this.sourceLang = sourceLang;
	}
		
	@Column(name = "TARGET_LANGS", nullable = false)
	public String getTargetLangs() {
		return targetLangs;
	}
	public void setTargetLangs(String targetLangs) {
		this.targetLangs = targetLangs;
	}
	
	@Column(name = "TEXT_TO_TRANSLATE",  length=4000)
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@Column(name = "USERNAME", nullable = false)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED", nullable = false)
	@DateTimeFormat(pattern=ConversionTools.DateTimeFormatSmall)
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	@Column(name = "TRANSLATION_ID")
	public Integer getTranslationID() {
		return translationID;
	}
	public void setTranslationID(Integer translationsID) {
		this.translationID = translationsID;
	}
	@Column(name = "IS_NOTIFY")
	public boolean isNotify() {
		return isNotify;
	}
	public void setNotify(boolean notify) {
		this.isNotify = notify;
	}
	@Column(name = "EMAIL")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@OneToMany(mappedBy = "request", cascade={CascadeType.ALL},
	        orphanRemoval=true)
	public Set<Response> getResponses() {
		return responses;
	}
	public void setResponses(Set<Response> responses) {
		this.responses = responses;
	}
	@Column(name = "SOURCE_FILE_URL")
	public String getFileURL() {
		return fileURL;
	}
	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}
	
	@Column(name = "TRANSLATIONS_ID", nullable = false)
	public Integer getTranslationsID() {
		return translationsID;
	}
	public void setTranslationsID(Integer translationsID) {
		this.translationsID = translationsID;
	} 

}
