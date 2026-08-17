# SRC-011 — How do I perform bulk operations?

## Intent / Description

This article explains how to perform bulk operations on multiple surveys at once.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Forms page
* Backend location: SurveyController.bulkchange

## Short Answer

From the Forms page, you can select multiple surveys and perform bulk operations including: publish/unpublish, add/remove tags, add/remove privileged users, delete surveys, and transfer ownership. Select surveys using checkboxes and choose the operation from the bulk action menu.

## Prerequisites / Required Permissions

* Survey owner of the selected surveys

## Procedure

1. Navigate to the Forms page.
2. Select multiple surveys using checkboxes.
3. Click the bulk action menu.
4. Choose the operation: publish/unpublish, add/remove tags, add/remove users, delete, change ownership.
5. Configure the operation parameters.
6. Confirm and execute.
7. View the results showing successes and failures.

## Important Conditions / Limitations

* Bulk operations only work on surveys you own.
* Only unpublished surveys can be bulk-deleted.
* Publish operation skips surveys with incomplete translations.
* Results report shows how many surveys were processed and how many failed.
* Tags can be added or removed in bulk.
* Privileged users can be added, removed, or replaced in bulk.
* Ownership transfer sends request emails to the new owners.

## Troubleshooting

* 'The operation failed for [X] survey(s)': Some surveys did not meet the prerequisites for the operation. Check individual survey states.
* 'You can only change the surveys for which you are the owner': Select only surveys you own.

## Related Articles

* ES-005 — How do I delete a survey?
* ES-008 — How do I publish a survey?
* ES-030 — How do I manage access and privileges?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/forms/bulkEditWizard.jsp
* Backend files: src/main/java/com/ec/survey/controller/SurveyController.java, src/main/java/com/ec/survey/tools/BulkExecutor.java, src/main/java/com/ec/survey/model/BulkChange.java
* Classes: SurveyController, BulkExecutor, BulkChange
* Methods: bulkchange, checkBulkChange, ExecutePublishUnpublish, ExecuteAddRemoveTags, ExecuteAddRemovePrivilegedUsers, ExecuteDeleteSurveys, ExecuteChangeOwner
* Routes: POST /forms/bulkchange
* Message keys: label.BulkChange, label.BulkEdit, info.AboutToDeleteSurveys, info.AddRemoveTags, info.AddRemoveUsers, info.PublishUnpublish, info.RequestTransferOwnership, info.OnlyOwnSurveys, info.OnlyUnpublishedSurveys, bulkresultpublish, bulkresultunpublish, bulkresultdeletesurveys, bulkresultfails
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Management
* EUSurvey area: Bulk Operations
* Feature: Bulk Change
* User intent: How do I perform bulk operations?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: bulk, batch, multiple, mass, operations
* Synonyms: bulk edit, batch operation, mass update, multiple surveys
* Acronyms: N/A
* Related entities: BulkChange, Survey
* Security / privacy relevance: None
* Search boost terms: bulk operations, batch edit, mass update surveys
* Source files: src/main/java/com/ec/survey/controller/SurveyController.java, src/main/java/com/ec/survey/tools/BulkExecutor.java, src/main/java/com/ec/survey/model/BulkChange.java
* Duplicate status: New
