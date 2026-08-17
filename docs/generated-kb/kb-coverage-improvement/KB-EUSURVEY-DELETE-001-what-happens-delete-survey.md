# What happens when I delete a survey in EUSurvey?

## Intent / Description

Explains the consequences of deleting a survey, what data is permanently removed, and how the deletion process works.

## Applies To

* Role(s): Survey Manager, Survey Owner
* Feature: Survey Deletion
* Context: Before or after deleting a survey from the survey list

## Short Answer

When you delete a survey, it is first **soft-deleted** (marked for deletion) and becomes invisible in your survey list. After a grace period (configured by the platform — typically several days), the survey and all associated data are **permanently deleted**. This includes all submitted contributions, draft answers, translations, uploaded files, participation groups, invitations, and export files. This action cannot be undone after the grace period expires.

## Steps / Procedure

1. Go to your **survey list** (Forms page).
2. Select the survey you want to delete.
3. Click the **Delete** option (or use the context menu).
4. Confirm the deletion when prompted.
5. The survey is immediately marked as deleted and disappears from your survey list.
6. After the grace period, the system permanently removes all data.

## Important Conditions / Limitations

* **Soft-delete phase**: Immediately after deletion, the survey is marked as deleted with a timestamp but data still exists in the database. During this phase, administrators may be able to assist with recovery.
* **Permanent deletion**: After the configured grace period (number of days set by the platform administrators), the system permanently deletes:
  - The survey form and all its versions (draft + published)
  - All submitted contributions and draft answers
  - All translations
  - All uploaded files (respondent uploads and background documents)
  - All participation groups and invitations
  - All exported result files
  - Activity logs (optionally)
* **No undo for end users**: Regular users cannot un-delete a survey themselves. Contact EUSurvey support immediately if you deleted a survey by mistake.
* **Do Not Delete flag**: Surveys marked with the "do not delete" flag are excluded from automatic deletion. This is typically set by administrators for protected surveys.
* **Frozen surveys**: A frozen survey can still be deleted by its owner.
* **Published survey**: Deleting a published survey makes it immediately inaccessible to respondents — they will see a "Page not found" or "Survey not published" message.

## Troubleshooting / Related Cases

* If you cannot find a survey in your list, it may have been deleted or archived. Check the Archive section first.
* If you need to recover a recently deleted survey, contact EUSurvey support immediately — recovery may be possible during the grace period.
* If you want to keep the data but stop accepting responses, consider **unpublishing** the survey instead of deleting it.

## Out of Scope / Separate Topics

* Archiving a survey (non-destructive alternative to deletion)
* Deleting individual contributions (see: How do I delete a contribution)
* Automatic deletion of inactive surveys (see: What is the inactive survey deletion process)
* Can I undo survey deletion (see: KB-EUSURVEY-DELETE-002)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* If you deleted a survey by mistake, open a support ticket immediately.

## Retrieval Metadata

* business_domain: survey_management
* user_role: survey_manager
* feature: delete_survey
* tags: delete survey, remove survey, data loss, permanent deletion, soft delete, grace period
* synonyms: what happens when survey is deleted, survey deletion consequences, is survey deletion permanent, can I recover deleted survey, data removed after survey deletion
* product_terms: Delete, survey list, soft-delete, grace period, permanent deletion
