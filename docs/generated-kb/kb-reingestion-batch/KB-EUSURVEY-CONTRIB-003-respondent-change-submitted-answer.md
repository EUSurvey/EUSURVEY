# Can a respondent change their submitted answers in EUSurvey?

## Intent / Description

Explains whether and how a respondent can modify their answers after submitting a contribution, and what conditions must be met.

## Applies To

* Role(s): Respondent, Survey Manager
* Feature: Edit contribution after submission
* Context: A respondent wants to change answers already submitted

## Short Answer

Whether a respondent can change their answers after submission depends on the survey configuration. There are two mechanisms:

1. **"Allow participants to change their contribution" setting** — If the survey manager has enabled this option, respondents can edit their submitted answers using their contribution ID.
2. **Contribution reset by the survey manager** — A survey manager can manually reset a submission back to draft, allowing the respondent to modify and resubmit.

If neither option is available, the respondent cannot change their answers after clicking Submit.

## Steps / Procedure

**For respondents (if editing is enabled):**

1. Go to: https://ec.europa.eu/eusurvey/home/editcontribution
2. Enter your **Contribution ID** (shown on the confirmation page after submission, or sent via confirmation email if enabled).
3. Click **Edit**.
4. Modify your answers.
5. Click **Submit** again to save changes.

**For survey managers (to enable editing):**

1. Open your survey.
2. Go to **Properties** → **Security** tab.
3. Enable **"Allow participants to change their contribution"**.
4. Save the settings.

**For survey managers (to reset a specific contribution):**

1. Go to the **Results** page.
2. Find the contribution.
3. Click **Reset** to convert it back to a draft.
4. Inform the respondent they can access and modify their draft.

## Important Conditions / Limitations

* **Contribution ID required**: Respondents need their contribution ID to edit. This is displayed on the confirmation page after submission. If they did not save it, they cannot edit unless the survey manager helps.
* **Survey must allow editing**: The setting must be enabled by the survey manager. It is disabled by default.
* **No notification to manager**: When a respondent edits their contribution, no automatic notification is sent to the survey manager.
* **Time constraint**: If the survey has been unpublished or its end date has passed, editing is no longer possible.
* **Anonymous surveys**: In fully anonymous surveys, editing may not be possible because the system cannot link a respondent to their contribution.
* **The Edit Contribution page** is also accessible from the EUSurvey homepage at https://ec.europa.eu/eusurvey/home/welcome.

## Troubleshooting / Related Cases

* If a respondent says they cannot edit: check that the "Allow participants to change their contribution" setting is enabled and the survey is still published.
* If the respondent lost their contribution ID: the survey manager can find it in the Results page and share it (be cautious about data protection).

## Out of Scope / Separate Topics

* How to reset a respondent's contribution (see: KB-EUSURVEY-CONTRIB-001)
* What does "incomplete contribution" mean (see: KB-EUSURVEY-CONTRIB-002)
* How to allow participants to print/download their contribution

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contribution_management
* user_role: respondent, survey_manager
* feature: edit_contribution
* tags: edit after submission, change answer, modify contribution, contribution ID, respondent edit
* synonyms: can I change my answer after submitting, how to edit submitted contribution, modify answers after submission, update my contribution, change submitted response EUSurvey
* product_terms: Edit contribution, Contribution ID, Allow participants to change, Security settings
