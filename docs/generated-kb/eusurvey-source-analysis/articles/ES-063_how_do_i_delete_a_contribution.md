# ES-063 — How do I delete a contribution?

## Intent / Description

This article explains how to permanently delete a submitted contribution.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Results
* Environment: All
* Article type: How-To
* UI location: Results page
* Backend location: ContributionController.java

## Short Answer

To delete a contribution, navigate to Results, select the contribution, and click Delete. Deletion is permanent. Only users with read/write AccessResults or survey owners can delete.

## Prerequisites / Required Permissions

* AccessResults read/write privilege or survey owner
* Contributions must exist

## Procedure

1. Navigate to the Results page.
2. Find the contribution.
3. Select it.
4. Click Delete.
5. Confirm.
6. Contribution is permanently removed.

## Important Conditions / Limitations

* Deletion is permanent and cannot be undone.
* eVote contributions may not be deletable.
* Multiple contributions can be deleted at once.
* Statistics need recalculation after deletion.

## Troubleshooting

* Delete button not visible: Need read/write AccessResults.
* No contributions selected error: Select before clicking delete.

## Related Articles

* ES-016 — How do I view survey results?
* ES-017 — How do I export survey results?

## Evidence / Source Traceability

* Frontend: N/A
* Backend: src/main/java/com/ec/survey/controller/ContributionController.java, src/main/java/com/ec/survey/service/AnswerService.java
* Classes: ContributionController, AnswerService
* Methods: deleteContribution, deleteAnswer
* Routes: POST /{shortname}/management/deleteContribution
* Message keys: label.Delete, label.ConfirmDeletion, info.NoContributionsToDelete
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Results
* EUSurvey area: Contribution Management
* Feature: Delete Contribution
* User intent: How do I delete a contribution?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Published
* Environment: All
* Keywords: delete, contribution, remove, answer
* Synonyms: remove submission, delete answer, erase contribution
* Acronyms: N/A
* Related entities: AnswerSet
* Security / privacy relevance: Permanently removes respondent data
* Search boost terms: delete contribution, remove submission
* Source files: src/main/java/com/ec/survey/controller/ContributionController.java, src/main/java/com/ec/survey/service/AnswerService.java
* Duplicate status: New
