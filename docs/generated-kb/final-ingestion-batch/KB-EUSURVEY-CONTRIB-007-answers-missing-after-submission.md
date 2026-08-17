# Why some answers may be missing from a submitted contribution

## Intent / Description

Explains verified reasons why certain answers within a submitted contribution may appear blank or missing.

## Applies To

* Role(s): Survey Manager, Respondent
* Feature: Contributions, Conditional visibility, Validation
* Context: A survey owner reviews a submitted contribution and finds some questions have no answer, or a respondent reports that their answers disappeared

## Short Answer

Some answers may be missing from a submitted contribution for the following verified reasons:

1. **Conditional visibility (dependencies)** — The question was hidden because the respondent's earlier answers did not trigger its visibility condition.
2. **Optional questions** — The question was not mandatory and the respondent skipped it.
3. **Questions added after submission** — The survey owner added new questions after the respondent already submitted. Earlier contributions do not contain answers to questions that did not exist at submission time.
4. **Page navigation without answering** — In multi-page surveys, the respondent may have navigated past a page without filling in all non-mandatory questions.
5. **Respondent editing** — If the respondent edited their contribution after submission (when editing is enabled), they may have cleared or changed some answers.

## Steps / Procedure

**For the survey owner:**

1. Open the contribution from the Results page.
2. Identify which questions have blank answers.
3. Check if those questions have **visibility conditions** (dependencies). If so, the respondent's answers to the trigger questions may not have met the condition.
4. Check if the questions were added **after** the contribution was submitted (compare the question creation date with the submission date).
5. Check if the questions are **mandatory**. Non-mandatory questions may simply have been left blank.
6. If the survey allows contribution editing, check whether the respondent made changes after initial submission.

## Important Conditions / Limitations

* **Conditional visibility is by design**: If a question is set to appear only when a specific answer is selected, and the respondent selected a different answer, the hidden question will have no response. This is correct behaviour.
* **No data loss on submit**: The submission process saves all answered questions. If data was present when the respondent clicked Submit, it is stored.
* **Session-related issues**: If a respondent's session expired mid-survey in rare cases, their work on unsaved pages may be lost. EUSurvey provides local browser backup in some cases, but this is not guaranteed.
* **Validation failures**: If a page fails validation, the respondent must fix the issues before proceeding. This does not cause data loss for other pages.
* **Reset and re-submission**: If a contribution was reset to draft by the survey owner and then resubmitted by the respondent, the respondent may have changed or cleared answers during the re-editing process.

## Troubleshooting / Related Cases

* If all answers on one page are blank: the respondent may have skipped that page (in surveys not using "Validate per page").
* If specific conditional questions are blank: verify the dependency logic in the editor.
* If a respondent says they answered but the answer is not recorded: this may be a session timeout issue. Encourage respondents to save as draft periodically for long surveys.

## Out of Scope / Separate Topics

* Why a submitted contribution is not visible to the owner (see: KB-EUSURVEY-CONTRIB-006)
* How conditional visibility works (see: KB-EUSURVEY-CONFIG-001)
* How a respondent can change submitted answers (see: KB-EUSURVEY-CONTRIB-003)
* What "incomplete contribution" means (see: KB-EUSURVEY-CONTRIB-002)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contribution_management
* user_role: survey_manager, respondent
* feature: contribution_answers, conditional_visibility
* tags: missing answers, blank answers, answers disappeared, empty contribution fields, questions not answered
* synonyms: some answers empty in submission, answers missing from contribution, why are fields blank, respondent answers not saved, partial contribution
* product_terms: Results, dependencies, visibility, mandatory, Submit, session
* exclude: contribution not found, export issues, PDF generation
