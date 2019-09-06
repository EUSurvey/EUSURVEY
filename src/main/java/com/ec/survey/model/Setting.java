package com.ec.survey.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SETTINGS")
public class Setting {

	private Integer id;
	private String key;
	private String value;
	private String format;
	
	public final static String LDAPsyncEnabled = "LDAPsyncEnabled";
	public final static String LDAPsyncFrequency = "LDAPsyncFrequency";
	public final static String LDAPsyncStart = "LDAPsyncStart";
	public final static String LDAPsyncTime = "LDAPsyncTime";
	
	public final static String LDAPsync2Enabled = "LDAPsync2Enabled";
	public final static String LDAPsync2Frequency = "LDAPsync2Frequency";
	public final static String LDAPsync2Start = "LDAPsync2Start";
	public final static String LDAPsync2Time = "LDAPsync2Time";
	
	public final static String SurveyMigrateStart = "surveymigratestart";
	public final static String SurveyMigrateTime = "surveymigratetime";
	public final static String LastSurveyToMigrate = "lastsurveytomigrate";
	
	public final static String AnswerPDFDeletionStart = "answerpdfdeletionstart";
	public final static String AnswerPDFDeletionTime = "answerpdfdeletiontime";
	public final static String LastSurveyToDeleteAnswerPDFs = "lastsurveytodeleteanswerpdfs";
	
	public final static String ActivityLoggingEnabled = "ActivityLoggingEnabled";
	public final static String CreateSurveysForExternalsDisabled = "CreateSurveysForExternalsDisabled";	
		
	@Id
	@Column(name = "SETTINGS_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "SETTINGS_KEY", unique = true)
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@Column(name = "SETTINGS_VALUE")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Column(name = "SETTINGS_FORMAT")
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	
	@Transient
	public static List<Integer> ActivityLoggingIds()
	{
		List<Integer> ids = new ArrayList<>();
		for (int i = 101; i < 124; i++) {
			ids.add(i);
		}
		for (int i = 201; i < 229; i++) {
			ids.add(i);
		}
		for (int i = 301; i < 315; i++) {
			ids.add(i);
		}
		ids.add(401);
		ids.add(402);
		ids.add(403);
		ids.add(404);
		ids.add(405);
		ids.add(406);
		for (int i = 501; i < 508; i++) {
			ids.add(i);
		}
		ids.add(601);
		ids.add(602);
		ids.add(603);
		ids.add(701);
		return ids;
	}
	
}
