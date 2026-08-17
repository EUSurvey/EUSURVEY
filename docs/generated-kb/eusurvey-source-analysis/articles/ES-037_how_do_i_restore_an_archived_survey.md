# ES-037 — How do I restore an archived survey?

## Intent / Description

This article explains how to restore a previously archived survey.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Lifecycle
* Environment: All
* Article type: How-To
* UI location: Archives page
* Backend location: ArchiveService.restore

## Short Answer

To restore an archived survey, navigate to the Archived Surveys section and click 'Restore' on the desired survey. The system restores the survey and its contributions back to the active survey list.

## Prerequisites / Required Permissions

* Survey owner
* The archive must exist and not be corrupted

## Procedure

1. Navigate to the Archived Surveys section.
2. Find the survey to restore.
3. Click 'Restore'.
4. Wait for the restore process to complete.
5. The survey reappears in your active surveys list.

## Important Conditions / Limitations

* Restoring recreates the survey from the archive package.
* The restored survey returns to an unpublished state.
* If files are missing from the archive, a warning is shown after restore.
* The restore process may take time for large surveys.
* You will receive a notification when the restore is complete.

## Troubleshooting

* 'Problem during restore': Some files could not be restored. Check for missing files and re-upload if needed.

## Related Articles

* ES-036 — How do I archive a survey?
* ES-005 — How do I delete a survey?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/service/ArchiveService.java, src/main/java/com/ec/survey/controller/SurveySearchController.java
* Classes: ArchiveService, SurveySearchController
* Methods: restore, startRestore
* Routes: POST /administration/surveysearch
* Message keys: label.Restore, error.ProblemDuringRestore, error.ProblemDuringRestore2
* Configuration keys: ui.enablearchiving

## Confidence and Review Status

High

## Metadata

* Domain: Survey Lifecycle
* EUSurvey area: Archiving
* Feature: Restore Survey
* User intent: How do I restore an archived survey?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Archived
* Environment: All
* Keywords: restore, recover, unarchive, bring back
* Synonyms: restore archived survey, recover survey, unarchive
* Acronyms: N/A
* Related entities: Archive, Survey
* Security / privacy relevance: None
* Search boost terms: restore survey, recover from archive
* Source files: src/main/java/com/ec/survey/service/ArchiveService.java, src/main/java/com/ec/survey/controller/SurveySearchController.java
* Duplicate status: New
