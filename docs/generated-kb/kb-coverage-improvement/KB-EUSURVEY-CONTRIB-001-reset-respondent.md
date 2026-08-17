# How do I reset a respondent's contribution?

## Intent / Description

Explains how a survey manager can reset a submitted contribution back to draft status, allowing the respondent to modify and resubmit it.

## Applies To

* Role(s): Survey Manager, Form Administrator
* Feature: Contribution Management
* Context: A respondent submitted their contribution but needs to modify it

## Short Answer

As a survey manager with Form Management privileges, you can reset a submitted contribution back to draft status. This does **not** delete the respondent's answers — it converts the submission back into an editable draft. The respondent can then access the draft using their original link and modify their answers before resubmitting.

## Steps / Procedure

1. Go to the **Results** page of your survey.
2. Locate the contribution you want to reset (you can search by contribution ID or filter).
3. Click the **Reset** button for that contribution.
4. The contribution's status changes from "submitted" to "draft".
5. The respondent can now access their draft and modify their answers.
6. The respondent must re-submit for the contribution to count as submitted again.

## Important Conditions / Limitations

* **Permission required**: Only users with Form Management privilege level 2 or higher can reset contributions.
* **Data is preserved**: The respondent's existing answers are not deleted — they remain in the draft.
* **Count impact**: After reset, the contribution is no longer counted in the submitted contributions total. It will count again only after the respondent resubmits.
* **Invitation impact**: If the respondent was invited via a token/invitation, the invitation answer count is decremented by one after reset.
* **eVote restriction**: Contributions to eVote surveys **cannot** be reset. Attempting to do so will fail.
* **No notification**: The system does not automatically notify the respondent that their contribution was reset. You need to contact them separately.
* **Draft link**: After reset, the respondent can access their draft using the same draft link they originally used. If the survey requires authentication (EU Login), they need to log in.

## Troubleshooting / Related Cases

* If the reset button is not visible, verify that you have Form Management privilege level 2 or higher.
* If you want the respondent to start completely fresh (empty form), consider deleting the contribution instead and re-inviting them.
* If the survey is set to "quiz mode", resetting still works — the respondent can modify their answers.

## Out of Scope / Separate Topics

* Deleting a contribution (see: How do I delete a contribution)
* Allowing respondents to edit their own submissions without manager intervention (see: survey security settings)
* What does "incomplete contribution" mean (see: KB-EUSURVEY-CONTRIB-002)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* If you need help, open a support ticket from that page.

## Retrieval Metadata

* business_domain: contribution_management
* user_role: survey_manager
* feature: reset_contribution
* tags: reset, respondent, contribution, draft, resubmit, reopen
* synonyms: reset submission, reopen contribution, put contribution back to draft, undo submission, allow respondent to edit, revert contribution to draft
* product_terms: Reset, contribution, draft, Form Management, submitted
