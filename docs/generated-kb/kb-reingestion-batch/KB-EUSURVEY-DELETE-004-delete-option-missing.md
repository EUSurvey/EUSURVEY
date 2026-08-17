# Why is the delete option missing for my survey?

## Intent / Description

Explains reasons why a survey manager cannot find or use the Delete option for a survey.

## Applies To

* Role(s): Survey Manager
* Feature: Survey Deletion
* Context: The delete button is not visible or is greyed out

## Short Answer

The delete option may be missing or unavailable for these reasons:

1. **Insufficient privileges**: You are not the survey owner and do not have Form Management privileges at the required level.
2. **Survey is frozen**: A frozen survey (frozen by an administrator) cannot be deleted by regular users.
3. **Survey is archived**: Archived surveys must be restored before they can be deleted.
4. **The survey belongs to another user**: You can only delete surveys you own or surveys for which you have full administration rights.
5. **You are looking in the wrong place**: The delete action may be in a context menu (right-click or action icon) rather than a visible button.

## Steps / Procedure

1. Verify you are the **owner** of the survey (check the "Owner" column in your survey list).
2. If you are not the owner, ask the owner or an administrator to delete it, or request Form Management privileges.
3. If the survey is **archived**, go to the Archive section, restore the survey, then delete it.
4. If the survey is **frozen**, contact EUSurvey support — only administrators can unfreeze or delete frozen surveys.
5. Look for the Delete option in the **context menu** (three-dot icon or right-click) on the survey row in your survey list.

## Important Conditions / Limitations

* **Owner-only by default**: Only the survey owner can delete a survey unless other users have been granted full access privileges.
* **Frozen surveys**: Administrators can freeze surveys (e.g., due to abuse reports). Frozen surveys cannot be edited, published, or deleted by the owner until unfrozen.
* **Global privilege**: Users with Form Management privilege level 2 (global admin) can delete any survey.
* **No "delete" in editor**: The delete option is on the survey list page, not inside the editor.
* **Protected surveys**: Surveys marked with "do not delete" by administrators are excluded from deletion.

## Troubleshooting / Related Cases

* If the survey was already deleted, it will not appear in your list. Check if it was automatically deleted due to inactivity.
* If you need to delete a survey owned by someone who left the organisation, contact EUSurvey support to transfer ownership first.

## Out of Scope / Separate Topics

* What happens when I delete a survey (see: KB-EUSURVEY-DELETE-001)
* Can I undo survey deletion (see: KB-EUSURVEY-DELETE-002)
* How do I archive a survey (see: archiving feature)
* How do I transfer ownership (see: change survey owner)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: survey_management
* user_role: survey_manager
* feature: delete_survey
* tags: delete missing, cannot delete, delete button gone, no delete option, frozen, privileges
* synonyms: why can't I delete my survey, delete button not showing, delete option greyed out, survey deletion not available, how to delete survey I don't own
* product_terms: Delete, frozen, archive, owner, privileges, Form Management
