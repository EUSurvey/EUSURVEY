# SRC-002 — How do I clear unapplied changes?

## Intent / Description

This article explains how to discard pending changes and revert the draft to match the published version.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Lifecycle
* Environment: All
* Article type: How-To
* UI location: Overview page
* Backend location: ManagementController.clearchanges()

## Short Answer

To discard pending changes, navigate to the Overview page and click 'Clear Changes'. This reverts the draft survey back to match the currently published version, discarding all edits made since the last publication or apply changes.

## Prerequisites / Required Permissions

* The user must be the survey owner
* The survey must have pending changes

## Procedure

1. Navigate to the survey Overview page.
2. Click 'Clear Changes'.
3. Confirm the action.
4. Wait for the operation to complete.
5. The draft is reverted to match the published version.

## Important Conditions / Limitations

* This operation discards all edits made to the draft since the last publication.
* The operation cannot be undone.
* The published version remains unchanged.
* The operation may take time for complex surveys.

## Troubleshooting

* Changes not cleared: Wait at least 5 minutes before retrying.

## Related Articles

* ES-010 — How do I apply changes after publication?
* ES-008 — How do I publish a survey?
* ES-006 — How do I edit a survey?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: ManagementController, SurveyService
* Methods: clearchanges, clearChanges
* Routes: POST /{shortname}/management
* Message keys: label.ClearChanges, info.ClearChanges
* Configuration keys: N/A

## Confidence and Review Status

Medium

## Metadata

* Domain: Survey Lifecycle
* EUSurvey area: Publication
* Feature: Clear Changes
* User intent: How do I clear unapplied changes?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Published with pending changes
* Environment: All
* Keywords: clear, discard, revert, undo, changes
* Synonyms: discard changes, revert edits, undo changes
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: clear changes, discard edits, revert draft
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
