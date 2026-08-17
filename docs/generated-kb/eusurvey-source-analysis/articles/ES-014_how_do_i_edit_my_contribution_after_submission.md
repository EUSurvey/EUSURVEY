# ES-014 — How do I edit my contribution after submission?

## Intent / Description

This article explains how respondents can edit their submitted contribution.

## Applies To

* Role(s): Respondent
* EUSurvey area: Survey Runner
* Environment: All
* Article type: How-To
* UI location: Confirmation page
* Backend location: ContributionController.editcontribution

## Short Answer

If the survey owner has enabled the 'Allow participants to change their contribution' setting, respondents can edit their submitted answers. Use the Contribution ID from the confirmation page or the edit link to access and modify your submission.

## Prerequisites / Required Permissions

* The survey must have 'Change Contribution' enabled by the owner (Survey.changeContribution)
* The respondent must have their Contribution ID or edit link
* The survey must still be published and active

## Procedure

1. After submitting, note the Contribution ID from the confirmation page.
2. Navigate to the survey's edit contribution page.
3. Enter your Contribution ID.
4. The system loads your previous answers.
5. Modify your answers as needed.
6. Click Submit to save the changes.

## Important Conditions / Limitations

* The feature must be explicitly enabled by the survey owner.
* Delphi surveys always allow contribution changes (no separate setting needed).
* For invitation-based surveys, the respondent must use the same invitation link.
* The survey must still be active for editing to work.
* eVote contributions cannot be edited after submission.

## Troubleshooting

* 'This survey does not allow to change a contribution' error: The survey owner has not enabled this feature.
* 'The contribution could not be loaded' error: Verify the Contribution ID is correct.
* 'The survey has been closed' error: The survey end date has passed.

## Related Articles

* ES-012 — How do respondents submit a contribution?
* ES-013 — How do I save a survey as draft?
* ES-015 — How do I download my contribution as PDF?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ContributionController.java, src/main/java/com/ec/survey/controller/HomeController.java
* Classes: ContributionController, HomeController
* Methods: editcontribution, editContributionInner, checkValid
* Routes: /preparecontribution/{code}, GET /home/editcontribution
* Message keys: label.EditContribution, label.EditMyContribution, label.AllowChangeContributionNewNew, error.ContributionEditNotAllowed, error.ContributionNotLoaded, error.ContributionClosedSurvey, info.AllowChangeContribution
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Runner
* EUSurvey area: Respondent Workflow
* Feature: Edit Contribution
* User intent: How do I edit my contribution after submission?
* Article type: How-To
* User type: Respondent
* Required permission: Respondent
* Survey status: Published and Active
* Environment: All
* Keywords: edit, change, modify, update, contribution, answer
* Synonyms: change my answers, update submission, modify contribution
* Acronyms: N/A
* Related entities: AnswerSet, Survey
* Security / privacy relevance: Editing exposes previous answers
* Search boost terms: edit contribution, change answers, modify submission
* Source files: src/main/java/com/ec/survey/controller/ContributionController.java, src/main/java/com/ec/survey/controller/HomeController.java
* Duplicate status: New
