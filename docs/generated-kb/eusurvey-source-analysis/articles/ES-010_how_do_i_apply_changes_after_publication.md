# ES-010 — How do I apply changes after publication?

## Intent / Description

This article explains how to apply pending changes to a published survey.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Lifecycle
* Environment: All
* Article type: How-To
* UI location: Overview page
* Backend location: ManagementController.applyChanges()

## Short Answer

When you edit a published survey, changes are saved as pending changes in the draft version. To make these changes visible to respondents, navigate to the Overview page and click 'Apply Changes'. This updates the published version with your edits.

## Prerequisites / Required Permissions

* The user must be the survey owner
* The survey must be published
* The survey must have pending changes (hasPendingChanges=true)

## Procedure

1. Edit the published survey in the Editor.
2. Save your changes (they become 'pending changes').
3. Navigate to the Overview page.
4. Click 'Apply Changes'.
5. Wait for the operation to complete.
6. The published survey is updated with your edits.

## Important Conditions / Limitations

* Applying changes creates a new published version incorporating the draft edits.
* The operation may take time for complex surveys.
* Do not start the operation again if it appears slow — wait at least 5 minutes.
* Translations are synchronized during apply changes.
* OLAP reporting tables are updated.
* Pending changes can be viewed via 'Show pending changes' before applying.

## Troubleshooting

* 'Your changes are applied' message takes too long: Wait at least 5 minutes. Contact helpdesk if it persists.
* Changes not visible to respondents: Ensure Apply Changes completed successfully.

## Related Articles

* ES-008 — How do I publish a survey?
* ES-011 — How do I clear unapplied changes?
* ES-006 — How do I edit a survey?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: ManagementController, SurveyService
* Methods: applyChanges, AdaptIDs
* Routes: POST /{shortname}/management
* Message keys: label.ApplyChanges, info.ApplyChanges, label.PendingChanges, label.ShowPendingChanges
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Lifecycle
* EUSurvey area: Publication
* Feature: Apply Changes
* User intent: How do I apply changes after publication?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Published with pending changes
* Environment: All
* Keywords: apply, changes, update, pending, publish
* Synonyms: update published survey, apply edits, make changes live
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: apply changes, update published survey, pending changes
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
