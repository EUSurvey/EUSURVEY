package com.ec.survey.model;

import com.ec.survey.tools.Tools;
import com.ec.survey.tools.activity.ActivityRegistry;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a filter on the activities table of a survey
 */
@Entity
@Table(name = "ACTIVITYFILTER")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ActivityFilter implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private String surveyUid;
	private int logId;
	private Date dateFrom;
	private Date dateTo;
	private String object;
	private String property;
	private String event;
	private String description;
	private String oldValue;
	private String newValue;
	private String sortKey = "date";
	private String sortOrder = "DESC";
	private Set<String> visibleColumns = new HashSet<>();
	private Set<String> exportedColumns = new HashSet<>();
	
	@Id
	@Column(name = "ACFILTER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "ACFILTER_DATEFROM")
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	
	@Column(name = "ACFILTER_DATETO")
	@DateTimeFormat(pattern="dd/MM/yyyy")
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	
	@Column(name = "ACFILTER_SORTKEY")
	public String getSortKey() {
		return sortKey;
	}
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
	
	@Column(name = "ACFILTER_SORTORDER")
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	@Column(name = "ACFILTER_USER")
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	@Column(name = "ACFILTER_LOGID")
	public int getLogId() {
		return logId;
	}
	public void setLogId(int logId) {
		this.logId = logId;
	}
	
	@Column(name = "ACFILTER_SURVEY")
	public String getSurveyUid() {
		return surveyUid;
	}
	public void setSurveyUid(String surveyUid) {
		this.surveyUid = surveyUid;
	}
	
	@Column(name = "ACFILTER_OBJECT")
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	
	@Column(name = "ACFILTER_PROP")
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
	@Column(name = "ACFILTER_DESC")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name = "ACFILTER_OLD")
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	
	@Column(name = "ACFILTER_NEW")
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	
	@Column(name = "ACFILTER_EVENT")
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	
	@ElementCollection
	public Set<String> getVisibleColumns() {
		return visibleColumns;
	}
	public void setVisibleColumns(Set<String> visibleColumns) {
		this.visibleColumns = visibleColumns;
	}
	
	@ElementCollection
	public Set<String> getExportedColumns() {
		return exportedColumns;
	}
	public void setExportedColumns(Set<String> exportedColumns) {
		this.exportedColumns = exportedColumns;
	}
	
	public ActivityFilter copy() {
		ActivityFilter copy = new ActivityFilter();

		copy.logId = logId;
		copy.dateFrom = dateFrom;
		copy.dateTo = dateTo;
		copy.sortKey = sortKey;
		copy.sortOrder = sortOrder;
		copy.description = description;
		copy.event = event;
		copy.newValue = newValue;
		copy.oldValue = oldValue;
		copy.object = object;
		copy.property = property;

		Set<String> newVisibleColumns = new HashSet<>();
		newVisibleColumns.addAll(visibleColumns);
		copy.visibleColumns = newVisibleColumns;

		Set<String> newExportedColumns = new HashSet<>();
		newExportedColumns.addAll(exportedColumns);
		copy.exportedColumns = newExportedColumns;

		copy.userId = userId;
		copy.surveyUid = surveyUid;

		return copy;
	}
	
	@Transient 
	public String getHash()
	{
		String result = String.valueOf(this.logId) +
				this.surveyUid +
				this.userId +
				this.dateFrom +
				this.dateTo +
				this.description +
				this.event +
				this.object +
				this.oldValue +
				this.newValue +
				this.property;
		return Tools.md5hash(result);
	}
	
	@Transient
	public boolean visible(String key)
	{
		return visibleColumns.contains(key);
	}
	
	@Transient
	public boolean exported(String key)
	{
		return exportedColumns.contains(key);
	}
	
	@Transient
	public String[] getAllObjects(){
		String[] result = ActivityRegistry.getAllObjects();
		Arrays.sort(result);
		return result;
	}
	
	@Transient
	public String[] getAllEvents(){
		String[] result = ActivityRegistry.getAllEvents();
		Arrays.sort(result);
		return result;
	}
	
	@Transient
	public String[] getAllProperties(){
		String[] result = ActivityRegistry.getAllProperties();
		Arrays.sort(result);
		return result;
	}
	
}
