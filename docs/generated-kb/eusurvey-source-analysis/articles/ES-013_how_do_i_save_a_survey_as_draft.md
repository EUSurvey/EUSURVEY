# ES-013 — How do I save a survey as draft?

## Intent / Description

This article explains how respondents can save their partial answers as a draft to continue later.

## Applies To

* Role(s): Respondent
* EUSurvey area: Survey Runner
* Environment: All
* Article type: How-To
* UI location: Runner page
* Backend location: RunnerController.DraftSubmit

## Short Answer

If the survey owner has enabled the 'Save as Draft' feature, respondents can save their partial answers without submitting. Click the 'Save as Draft' button on the survey page. The system provides a Draft ID and optionally a link that can be used to return and complete the survey later.

## Prerequisites / Required Permissions

* The survey must have 'Save as Draft' enabled by the survey owner (Survey.saveAsDraft)
* The survey must be published and active

## Procedure

1. Fill in some or all questions in the survey.
2. Click the 'Save as Draft' button.
3. The system saves your partial answers.
4. Note the Draft ID or bookmark the draft link provided.
5. Return later using the Draft ID or link to continue.
6. Complete and submit when ready.

## Important Conditions / Limitations

* The feature must be explicitly enabled by the survey owner.
* Delphi surveys do not support save as draft (contributions can be changed at any time instead).
* A respondent can have only one active draft per survey (per invitation link or per user).
* Old drafts may be automatically deleted after a configurable period.
* Draft data is stored server-side, not just in the browser.
* The draft link contains a unique code for retrieval.

## Troubleshooting

* Draft not found: The draft may have expired or been deleted. Old drafts are periodically cleaned up.
* 'Draft ID invalid' error: Verify the Draft ID is correct and the draft has not expired.

## Related Articles

* ES-012 — How do respondents submit a contribution?
* ES-014 — How do I edit my contribution after submission?
* ES-065 — How does local storage backup work?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/runner.js
* Backend files: src/main/java/com/ec/survey/controller/RunnerController.java, src/main/java/com/ec/survey/service/AnswerService.java
* Classes: RunnerController, AnswerService
* Methods: DraftSubmit, processDraftSubmit, saveDraft, getDraft
* Routes: POST /runner/{shortname}
* Message keys: label.SaveAsDraft, label.DraftID, label.DraftLink, error.DraftIDInvalid, info.AllowSaveAsDraft, info.AllowSaveAsDraftDelphi
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Runner
* EUSurvey area: Respondent Workflow
* Feature: Save Draft
* User intent: How do I save a survey as draft?
* Article type: How-To
* User type: Respondent
* Required permission: Respondent
* Survey status: Published and Active
* Environment: All
* Keywords: draft, save, partial, continue, later
* Synonyms: save progress, save partial answers, continue later, save and return
* Acronyms: N/A
* Related entities: AnswerSet, Survey
* Security / privacy relevance: Drafts store respondent data
* Search boost terms: save draft, save partial answers, continue later
* Source files: src/main/java/com/ec/survey/controller/RunnerController.java, src/main/java/com/ec/survey/service/AnswerService.java
* Duplicate status: New
