# Can I undo the deletion of a survey in EUSurvey?

## Intent / Description

Explains whether and how a deleted survey can be recovered.

## Applies To

* Role(s): Survey Manager, Survey Owner
* Feature: Survey Deletion Recovery
* Context: A user deleted a survey by mistake and wants to recover it

## Short Answer

There is **no self-service undo option** for survey deletion. Once you delete a survey, it disappears from your survey list immediately. However, deletion is implemented in two phases: the survey is first soft-deleted (marked as deleted with a timestamp), and permanent data removal happens only after a grace period of several days.

**If you act quickly**, EUSurvey support may be able to restore your survey during the grace period. Contact support immediately after realizing the mistake.

## Steps / Procedure

1. Do **not** wait — contact EUSurvey support as soon as possible.
2. Go to https://ec.europa.eu/eusurvey/home/documentation and open a support ticket.
3. Provide:
   - Your username / EU Login email
   - The survey title and/or alias (shortname) if you remember it
   - The approximate date and time of deletion
4. The support team will check whether the survey is still in the grace period and can be recovered.

## Important Conditions / Limitations

* **No self-service restore**: There is no "Recycle Bin" or "Undo" button available in the EUSurvey interface for deleted surveys.
* **Time-limited**: Recovery is only possible during the grace period (a configurable number of days after deletion). Once the grace period expires, the data is permanently deleted and cannot be recovered.
* **Administrator action required**: Only the EUSurvey administration team can restore a soft-deleted survey.
* **No guarantee**: Recovery depends on whether the automatic cleanup job has already processed the survey.
* **Archived ≠ Deleted**: If you archived a survey (not deleted), you can restore it yourself from the Archive section. Check there first.

## Troubleshooting / Related Cases

* If you cannot find your survey but did not explicitly delete it, check the **Archive** section — you may have archived it instead.
* If the survey was automatically deleted due to inactivity, the same grace period applies. See the inactive survey deletion process article.
* Prevention tip: Consider archiving surveys instead of deleting them if you might need the data in the future.

## Out of Scope / Separate Topics

* How to archive/restore a survey (see: archiving feature)
* What happens when I delete a survey (see: KB-EUSURVEY-DELETE-001)
* Automatic inactive survey deletion process (see separate article)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* Open a support ticket immediately — time is critical for recovery.

## Retrieval Metadata

* business_domain: survey_management
* user_role: survey_manager
* feature: delete_survey, recovery
* tags: undo delete, recover survey, restore deleted survey, undelete, grace period
* synonyms: can I get back deleted survey, undo survey deletion, restore removed survey, I accidentally deleted my survey, recover survey EUSurvey
* product_terms: delete, soft-delete, grace period, archive, restore
