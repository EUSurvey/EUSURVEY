package com.ec.survey.model;

import com.ec.survey.tools.activity.ActivityRegistry;
import edu.emory.mathcs.backport.java.util.Arrays;

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
	public static final String EnableEUGuestList = "EnableEUGuestList";
	
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

	public static final String AnswersAnonymWorkerInterval = "AnswersAnonymWorkerInterval";
	public static final String AnswersAnonymWorkerEnabled = "AnswersAnonymWorkerEnabled";
	
	public static final String UseSMTService = "UseSMTService";
	public static final String MaxSurveysPerUser = "MaxSurveysPerUser";
	public static final String MaxSurveysTimespan = "MaxSurveysTimespan";
	public static final String LastCheckedSurveyIDForZombieFiles = "LastCheckedSurveyIDForZombieFiles";
	public static final String AutomaticDraftDeleteExceptions = "AutomaticDraftDeleteExceptions";
	public static final String EULoginWhitelist = "EULoginWhitelist";
	public static final String EnableChargeback = "EnableChargeback";
	public static final String NightlyTaskStart = "NightlyTaskStart";
	public static final String NightlyTaskLimit = "NightlyTaskLimit";
	public static final String NightlyTaskLimitArchiving = "NightlyTaskLimitArchiving";
	public static final String DeleteSurveysAge = "DeleteSurveysAge";
	public static final String ArchiveOlderThan = "ArchiveOlderThan";
	public static final String ArchiveNotChangedInLast = "ArchiveNotChangedInLast";

	public static final String DisableLoginPage = "DisableLoginPage";
	public static final String DisableWebserviceAPI = "DisableWebserviceAPI";

	public static final String ContactGuestlistLimitForExternals = "ContactGuestlistLimitForExternals";
	public static final String ContactGuestlistSizeLimitForExternals = "ContactGuestlistSizeLimitForExternals";

	@Id
	@Column(name = "SETTINGS_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	
}
