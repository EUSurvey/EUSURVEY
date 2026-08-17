# ES-036 — How do I archive a survey?

## Intent / Description

This article explains how to archive a survey for long-term storage.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Lifecycle
* Environment: All
* Article type: How-To
* UI location: Forms list
* Backend location: ArchiveService.archiveSurvey

## Short Answer

To archive a survey, select it from your Forms list and choose 'Archive'. Archiving moves the survey and its contributions to cold storage, freeing up resources. Archived surveys can be restored later.

## Prerequisites / Required Permissions

* Survey owner
* Survey should ideally be unpublished or inactive
* Archiving feature must be enabled (ui.enablearchiving)

## Procedure

1. Select the survey from the Forms list.
2. Click 'Archive Survey'.
3. Confirm the archiving operation.
4. The system creates an archive package containing the survey and contributions.
5. The survey is moved to the Archived Surveys section.

## Important Conditions / Limitations

* Archiving moves the survey to a separate storage area.
* Archived surveys cannot be edited until restored.
* The archive includes survey structure and contributions.
* Missing files during archiving generate a warning.
* Archived surveys are listed in a separate 'Archived Surveys' view.
* The archiving feature must be enabled via the server configuration flag.

## Troubleshooting

* 'Warning - Problem during archiving': Some files could not be found. The archive may be incomplete.

## Related Articles

* ES-037 — How do I restore an archived survey?
* ES-005 — How do I delete a survey?
* ES-069 — How does automatic deletion work?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/service/ArchiveService.java, src/main/java/com/ec/survey/tools/ArchiveExecutor.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: ArchiveService, ArchiveExecutor, SurveyService
* Methods: archiveSurvey, markAsArchived, createArchive
* Routes: POST /{shortname}/management
* Message keys: label.Archive, label.ArchivedSurveys, info.archived, info.ProblemDuringArchiving, info.missingFilesDuringArchiving
* Configuration keys: ui.enablearchiving

## Confidence and Review Status

High

## Metadata

* Domain: Survey Lifecycle
* EUSurvey area: Archiving
* Feature: Archive Survey
* User intent: How do I archive a survey?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Unpublished or Published
* Environment: All
* Keywords: archive, store, backup, cold storage
* Synonyms: archive survey, move to archive, long-term storage
* Acronyms: N/A
* Related entities: Archive, Survey
* Security / privacy relevance: None
* Search boost terms: archive survey, long-term storage
* Source files: src/main/java/com/ec/survey/service/ArchiveService.java, src/main/java/com/ec/survey/tools/ArchiveExecutor.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
