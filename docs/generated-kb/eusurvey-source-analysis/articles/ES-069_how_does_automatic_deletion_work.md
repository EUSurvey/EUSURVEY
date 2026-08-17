# ES-069 — How does automatic deletion work?

## Intent / Description

This article explains how EUSurvey automatically archives and deletes inactive surveys.

## Applies To

* Role(s): System
* EUSurvey area: Survey Lifecycle
* Environment: All
* Article type: Concept
* UI location: Background
* Backend location: AutomaticSurveyDeleteWorker.java

## Short Answer

EUSurvey auto-archives inactive surveys and deletes them after a grace period. Three warning emails are sent before permanent deletion. The Do Not Delete flag prevents this.

## Prerequisites / Required Permissions

* System scheduled job (automatic)

## Procedure

The analysed source code does not provide a complete user-facing procedure.

## Important Conditions / Limitations

* Inactive surveys auto-archived after configured period.
* Archived surveys deleted after further time.
* Three warning emails before permanent deletion.
* Do Not Delete flag prevents auto-deletion.
* Flag auto-deactivated on copy/export.
* Reactivated surveys excluded from deletion.

## Troubleshooting

* Survey deleted unexpectedly: Check notification emails. Enable Do Not Delete for important surveys.

## Related Articles

* ES-036 — How do I archive a survey?
* ES-005 — How do I delete a survey?

## Evidence / Source Traceability

* Frontend: N/A
* Backend: src/main/java/com/ec/survey/tools/AutomaticSurveyDeleteWorker.java, src/main/java/com/ec/survey/service/SchedulerService.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: AutomaticSurveyDeleteWorker, SchedulerService, SurveyService
* Methods: run, sendNotificationEmail, getInactiveSurveys, setDeletionMessageDate
* Routes: N/A
* Message keys: label.DoNotDelete, info.DoNotDelete
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Lifecycle
* EUSurvey area: Deletion
* Feature: Auto-Delete
* User intent: How does automatic deletion work?
* Article type: Concept
* User type: System
* Required permission: System
* Survey status: Any
* Environment: All
* Keywords: automatic, deletion, inactive, archive, warning
* Synonyms: auto-delete, inactive survey cleanup, automatic removal
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: Permanently removes survey data
* Search boost terms: automatic deletion, auto-cleanup, inactive survey removal
* Source files: src/main/java/com/ec/survey/tools/AutomaticSurveyDeleteWorker.java, src/main/java/com/ec/survey/service/SchedulerService.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
