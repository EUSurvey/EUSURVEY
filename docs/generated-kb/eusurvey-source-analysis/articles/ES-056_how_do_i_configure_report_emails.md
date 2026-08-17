# ES-056 — How do I configure report emails?

## Intent / Description

This article explains how to configure periodic report emails about survey contributions.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Notifications
* Environment: All
* Article type: How-To
* UI location: Properties page
* Backend location: ManagementController.java

## Short Answer

Report emails send periodic summaries of contribution counts to recipients. Enable in Properties by activating Automatic report per e-mail, setting frequency, and specifying recipients.

## Prerequisites / Required Permissions

* Survey owner
* SMTP server configured

## Procedure

1. Open survey Properties.
2. Enable Automatic report per e-mail.
3. Set frequency: daily, weekly, or monthly.
4. Enter recipient emails separated by semicolons.
5. Optionally enable send only if contributions exist.
6. Save.

## Important Conditions / Limitations

* Frequency options: daily, weekly, monthly.
* Multiple recipients separated by semicolons.
* Can send only when new contributions exist.
* Maximum report emails per survey is configurable.

## Troubleshooting

No specific troubleshooting items identified.

## Related Articles

* ES-057 — How do I enable confirmation emails?
* ES-007 — How do I configure survey properties?

## Evidence / Source Traceability

* Frontend: N/A
* Backend: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SchedulerService.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: ManagementController, SchedulerService, SurveyService
* Methods: propertiesPost, sendStatisticalEmails
* Routes: N/A
* Message keys: label.AutomaticReportEmail, label.FrequencyReportEmails, info.AutomaticReportEmail, info.RecipientList, label.ReportEmailOnlyWhenContributionsExist
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Notifications
* EUSurvey area: Reporting
* Feature: Report Email
* User intent: How do I configure report emails?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Published
* Environment: All
* Keywords: report, email, notification, periodic, contributions, summary
* Synonyms: contribution report, periodic email, statistics email
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: report emails, periodic notifications, contribution summaries
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SchedulerService.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
