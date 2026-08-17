# Why EUSurvey preview mode displays an internal error

## Intent / Description

Explains possible causes when the survey preview function shows an internal error instead of rendering the survey.

## Applies To

* Role(s): Survey Manager
* Feature: Survey preview, Testing
* Context: A survey owner clicks Preview or Test and sees an error message instead of the survey

## Short Answer

If preview mode displays an internal error, it typically means the survey has a configuration issue that prevents rendering. Common causes include:

1. **Unsaved changes** — Changes in the editor have not been saved before previewing.
2. **Invalid HTML or markup** — Custom text elements contain malformed HTML that breaks rendering.
3. **Missing required configuration** — The survey is missing a required element or setting for preview to work.
4. **Circular or broken dependencies** — Visibility rules reference elements that create conflicts.
5. **Server-side processing error** — A temporary issue on the platform.

## Steps / Procedure

1. **Save your work**: Ensure all changes in the editor are saved before clicking Preview.
2. **Check for validation warnings**: The editor may show validation indicators for problematic elements.
3. **Simplify and test**: Temporarily remove recently added elements and preview again to isolate the problematic element.
4. **Check custom HTML**: If you used free-text elements with custom HTML, ensure the markup is valid.
5. **Check dependencies**: Review visibility rules for circular references or references to deleted elements.
6. **Try Test mode**: If Preview fails, try the Test function instead (or vice versa).
7. **Clear browser cache**: Sometimes cached resources cause conflicts.
8. **Contact support**: If the issue persists after checking the above, report it with the survey shortname.

## Important Conditions / Limitations

* **Preview uses the draft version**: Preview renders the editor's current saved state, which may differ from the published version.
* **Published survey may work even if preview fails**: In rare cases, applying changes and testing the live URL may work even if preview has issues.
* **No detailed error message**: The internal error page typically does not provide a specific technical reason visible to the user.
* **Complex surveys**: Surveys with many elements, deeply nested dependencies, or heavy custom content are more likely to encounter preview issues.

## Troubleshooting / Related Cases

* If preview worked before and stopped: identify what was changed since the last working preview.
* If preview fails but the published survey works: the issue is in unsaved or unapplied changes.
* If the error is intermittent: it may be a transient server issue. Try again after a few minutes.

## Out of Scope / Separate Topics

* What to do after a 500 error in the editor (see: KB-EUSURVEY-TECH-005)
* Why a survey shows a blank screen (see: KB-EUSURVEY-TECH-003)
* General service slowdown (see: KB-EUSURVEY-TECH-008)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* If the issue persists, contact EUSurvey support with your survey shortname and a description of recent changes.

## Retrieval Metadata

* business_domain: technical_access
* user_role: survey_manager
* feature: survey_preview
* tags: preview error, internal error preview, test mode fails, preview not working, 500 error preview
* synonyms: preview shows error message, cannot preview survey, internal server error in preview, preview broken
* product_terms: Preview, Test, editor, Save, Apply Changes
* exclude: submission errors, published survey errors, respondent-facing errors, blank screen for respondents
