# How do I delete a contribution in EUSurvey?

## Intent / Description

Explains how a survey manager can permanently delete a specific submitted contribution from the survey results.

## Applies To

* Role(s): Survey Manager, Form Administrator
* Feature: Contribution Deletion
* Context: Need to remove a specific submitted response from results

## Short Answer

Survey managers with sufficient privileges can delete individual contributions from the Results page. Deleting a contribution permanently removes the respondent's answers and any files they uploaded. A deletion record is kept for audit purposes (contribution code, deletion date), but the actual answer data is gone.

## Steps / Procedure

1. Go to the **Results** page of your survey.
2. Locate the contribution you want to delete (use filters or search by contribution ID).
3. Select the contribution(s) to delete.
4. Click the **Delete** button.
5. Confirm the deletion when prompted.
6. The contribution is permanently removed from the results.

## Important Conditions / Limitations

* **Permanent action**: Deleted contributions cannot be recovered. The answer data is removed from the database.
* **Audit trail**: A record of the deletion is kept (contribution code, survey UID, original creation date, deletion date) for compliance purposes.
* **Files deleted**: All files uploaded by the respondent as part of that contribution are deleted from storage.
* **Statistics invalidated**: After deletion, result statistics and charts are recalculated (excluding the deleted contribution).
* **Invitation impact**: If the respondent was invited via a token/invitation, the invitation answer count is decremented.
* **Published results**: If you have published results, the deleted contribution will no longer appear in the published view after re-publication.
* **Delphi surveys**: If the survey uses Delphi features, associated explanations and comments are also deleted.
* **Draft deletion**: If the deleted contribution had an associated draft, the draft is also removed.
* **Bulk deletion**: You can delete multiple contributions at once using the filter and bulk delete features.

## Troubleshooting / Related Cases

* If you want to temporarily remove a contribution from statistics without permanent deletion, consider using result filters to exclude it from your analysis.
* If you need to let a respondent re-submit after deletion, you will need to re-invite them (their original submission link will no longer work).
* If you want to preserve answers but allow the respondent to edit, use the **Reset** feature instead of deletion.

## Out of Scope / Separate Topics

* Resetting a contribution back to draft (see: How do I reset a respondent)
* Bulk deleting answers from survey results (see: How do I bulk delete answers)
* Deleting the entire survey (see: What happens when I delete a survey)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contribution_management
* user_role: survey_manager
* feature: delete_contribution
* tags: delete contribution, remove answer, delete submission, remove response, permanent
* synonyms: how to delete a response, remove submitted answer, delete participant response, erase contribution EUSurvey, delete answer from results
* product_terms: Delete, contribution, Results page, answer set, bulk delete
