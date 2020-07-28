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
	
	public static final String LDAPsyncEnabled = "LDAPsyncEnabled";
	public static final String LDAPsyncFrequency = "LDAPsyncFrequency";
	public static final String LDAPsyncStart = "LDAPsyncStart";
	public static final String LDAPsyncTime = "LDAPsyncTime";
	
	public static final String LDAPsync2Enabled = "LDAPsync2Enabled";
	public static final String LDAPsync2Frequency = "LDAPsync2Frequency";
	public static final String LDAPsync2Start = "LDAPsync2Start";
	public static final String LDAPsync2Time = "LDAPsync2Time";
	
	public static final String SurveyMigrateStart = "surveymigratestart";
	public static final String SurveyMigrateTime = "surveymigratetime";
	public static final String LastSurveyToMigrate = "lastsurveytomigrate";
	
	public static final String AnswerPDFDeletionStart = "answerpdfdeletionstart";
	public static final String AnswerPDFDeletionTime = "answerpdfdeletiontime";
	public static final String LastSurveyToDeleteAnswerPDFs = "lastsurveytodeleteanswerpdfs";
	
	public static final String ActivityLoggingEnabled = "ActivityLoggingEnabled";
	public static final String CreateSurveysForExternalsDisabled = "CreateSurveysForExternalsDisabled";	
	
	public static final String ReportingMigrationEnabled = "ReportingMigrationEnabled";
	public static final String ReportingMigrationStart = "ReportingMigrationStart";
	public static final String ReportingMigrationTime = "ReportingMigrationTime";
	public static final String ReportingMigrationSurveyToMigrate = "ReportingMigrationSurveyToMigrate";
	
	public static final String WeakAuthenticationDisabled = "WeakAuthenticationDisabled";
	public static final String MaxReports = "MaxReports";
	public static final String ReportText = "ReportText";
	public static final String ReportRecipients = "ReportRecipients";
	
	public static final String FreezeUserTextAdminBan = "FreezeUserTextAdminBan";
	public static final String FreezeUserTextAdminUnban = "FreezeUserTextAdminUnban";	
	public static final String FreezeUserTextBan = "FreezeUserTextBan";
	public static final String FreezeUserTextUnban = "FreezeUserTextUnban";
	public static final String BannedUserRecipients = "BannedUserRecipients";
	
	public static final String TrustValueCreatorInternal = "TrustValueCreatorInternal";
	public static final String TrustValuePastSurveys = "TrustValuePastSurveys";
	public static final String TrustValuePrivilegedUser = "TrustValuePrivilegedUser";
	public static final String TrustValueNbContributions = "TrustValueNbContributions";
	public static final String TrustValueMinimumPassMark = "TrustValueMinimumPassMark";
	
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
