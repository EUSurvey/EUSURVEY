# What to do after a 500 internal server error in the EUSurvey editor

## Intent / Description

Explains what to do when a survey owner encounters a 500 Internal Server Error while working in the EUSurvey editor.

## Applies To

* Role(s): Survey Manager
* Feature: Survey editor
* Context: A 500 error appears while editing a survey

## Short Answer

A 500 Internal Server Error in the editor means the server encountered an unexpected condition. This is typically a temporary issue or may be triggered by specific actions on complex survey elements. Your unsaved changes from the current editing session may be lost, but previously saved work is preserved.

## Steps / Procedure

1. **Do not panic about data loss**: The last saved version of your survey is preserved. Only unsaved changes from the current session are lost.
2. **Reload the page**: Close the error and navigate back to the survey editor.
3. **Try the action again**: If the error was triggered by a specific action (saving, adding an element, etc.), try again. It may have been a transient server issue.
4. **Save frequently**: When editing complex surveys, save your work often to minimise the impact of unexpected errors.
5. **Check for problematic content**: If the error occurs consistently when performing a specific action:
   - Check for invalid HTML in free-text elements.
   - Check for extremely long text in fields.
   - Check for recently added elements that may have configuration issues.
6. **Clear browser cache and retry**: Sometimes cached scripts conflict with the server version.
7. **Try a different browser**: Rule out browser-specific issues.
8. **Report if persistent**: If the same action consistently produces a 500 error, report it to EUSurvey support with:
   - The survey shortname
   - The exact action that triggers the error
   - The browser and version used

## Important Conditions / Limitations

* **Previously saved content is safe**: The 500 error does not corrupt your survey. The last saved state remains intact.
* **Autosave is not guaranteed**: EUSurvey does not continuously autosave editor changes. Use the Save button regularly.
* **Complex operations**: Actions involving many elements (bulk operations, large pastes) are more likely to trigger server-side issues.
* **Concurrent editing**: If multiple users edit the same survey simultaneously, conflicts may arise that result in errors.

## Troubleshooting / Related Cases

* If the error occurs on every page load: the survey configuration may have become corrupted. Contact support.
* If the error occurs only on Save: check for invalid characters or extremely long content in text fields.
* If you recently made many changes: try undoing the last change and saving again.

## Out of Scope / Separate Topics

* Why preview mode shows an error (see: KB-EUSURVEY-TECH-004)
* General service slowdown (see: KB-EUSURVEY-TECH-008)
* Survey freezes during submission (respondent-side) (see: KB-EUSURVEY-TECH-001)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* Report persistent 500 errors to EUSurvey support with the survey shortname and reproduction steps.

## Retrieval Metadata

* business_domain: technical_access
* user_role: survey_manager
* feature: survey_editor
* tags: 500 error, internal server error, editor crash, save fails, server error
* synonyms: 500 error in editor, internal error while editing, server error when saving survey, editor shows 500
* product_terms: editor, Save, 500, Internal Server Error
* exclude: submission errors, preview errors, respondent errors, 403 errors
