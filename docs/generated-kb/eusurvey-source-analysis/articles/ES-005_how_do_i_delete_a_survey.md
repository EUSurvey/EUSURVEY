# ES-005 — How do I delete a survey?

## Intent / Description

This article explains how to delete a survey from EUSurvey.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Forms list
* Backend location: ManagementController

## Short Answer

To delete a survey, select it from your Forms list and click Delete. Only unpublished surveys can be deleted directly. Published surveys must first be unpublished. Deleted surveys are soft-deleted and can be restored by administrators. After a grace period, they are permanently removed.

## Prerequisites / Required Permissions

* The user must be the survey owner
* The survey must be unpublished to delete from the Forms page

## Procedure

1. Navigate to the Forms page.
2. Select the survey to delete.
3. Click the Delete button.
4. Confirm the deletion in the dialog.
5. The survey is marked as deleted (soft delete).

## Important Conditions / Limitations

* Only unpublished surveys can be deleted by the owner from the Forms list.
* Published surveys must be unpublished first.
* Deletion is initially a soft-delete; the survey can be restored by an administrator.
* Permanently deleted surveys cannot be recovered.
* Running surveys (with active contributions) should not be deleted.
* Administrators can permanently delete from the admin survey search.

## Troubleshooting

* 'Cannot delete running survey': Unpublish the survey first.
* Survey not appearing after deletion: It has been soft-deleted. Contact admin to restore.

## Related Articles

* ES-008 — How do I publish a survey?
* ES-009 — How do I unpublish a survey?
* ES-036 — How do I archive a survey?
* ES-069 — How does automatic deletion work?

## Evidence / Source Traceability

* Frontend files:
* N/A
* Backend files:
* src/main/java/com/ec/survey/controller/ManagementController.java
* src/main/java/com/ec/survey/service/SurveyService.java
* Classes: ManagementController, SurveyService
* Methods: markDeleted, unmarkDeleted, delete
* Routes: /{shortname}/management
* Message keys: label.Delete, info.SurveyDeleted, info.CannotDeleteRunningSurvey, info.OnlyUnpublishedSurveys
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Management
* EUSurvey area: Survey Lifecycle
* Feature: Delete Survey
* User intent: How do I delete a survey?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Draft or Unpublished
* Environment: All
* Keywords: delete, remove, survey, trash
* Synonyms: remove survey, trash survey, discard survey
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: Deletion removes survey data permanently after grace period
* Search boost terms: delete survey, remove survey, discard form
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
