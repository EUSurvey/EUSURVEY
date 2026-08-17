# Why a survey freezes when respondents click Next or submit the final page

## Intent / Description

Explains verified reasons why a survey may freeze or become unresponsive when a respondent navigates between pages or submits the final page.

## Applies To

* Role(s): Respondent, Survey Manager
* Feature: Multi-page navigation, Submission
* Context: The survey stops responding when clicking Next, Previous, or the final Submit button

## Short Answer

A survey may freeze on page navigation or final submission for these reasons:

1. **Client-side validation running** — EUSurvey validates answers on the current page before allowing navigation. Complex pages with many questions may take a moment.
2. **Large file attachments** — Uploading files during page transition takes time. The page appears frozen until the upload completes.
3. **Complex dependencies** — Pages with many visibility dependencies require recalculation when navigating.
4. **Session timeout** — If too much time passed on the current page, the session may have expired.
5. **Network interruption** — A brief network issue prevents the page transition request from completing.
6. **Browser resource limits** — Very complex pages with many elements may exhaust browser resources.

## Steps / Procedure

**For respondents:**

1. **Wait**: Give the page at least 30 seconds to process, especially if you uploaded files.
2. **Check for validation errors**: Scroll up to see if any required questions are highlighted with error messages.
3. **Check your internet connection**: Ensure you are still connected.
4. **Save as Draft**: If available, try saving as draft before navigating. This preserves your work.
5. **Do not repeatedly click**: Clicking Next or Submit multiple times may cause issues.
6. **Try again**: If the page does not respond after 30 seconds, try clicking Next/Submit once more.
7. **Reload and resume**: If completely stuck, reload the page. If Save as Draft is enabled and you saved earlier, you can resume from your saved point.
8. **Try a different browser**: If the issue persists, try Chrome, Firefox, or Edge.

**For survey owners investigating reports:**

1. Check survey complexity: How many questions are on the affected page?
2. Check for file upload questions on the page.
3. Check dependencies that trigger on page transition.
4. Test the survey yourself to reproduce the issue.
5. Consider splitting long pages into shorter ones.

## Important Conditions / Limitations

* **Validate per page**: If "Validate per page" is enabled, all mandatory questions on the current page must be answered before proceeding. This validation runs client-side and may appear as a brief pause.
* **File uploads are synchronous**: Files are uploaded during page transition. Large files on slow connections cause the page to appear frozen.
* **No data loss if session is active**: If the server processes the request, data is not lost even if the page appears unresponsive.
* **Local storage backup**: EUSurvey saves some answers locally in the browser as a backup. This may help recover work if the session is lost.
* **Going back is not always possible**: If "Prevent going back" is enabled, the respondent cannot return to previous pages.

## Troubleshooting / Related Cases

* If the freeze happens consistently on the same page: the page may be too complex. Report to the survey owner.
* If it only happens with large files: reduce file size or use a faster connection.
* If it happens on the final Submit: see also KB-EUSURVEY-TECH-001 for submission-specific issues.
* If it happens intermittently: may be network-related. Try a more stable connection.

## Out of Scope / Separate Topics

* Why a survey freezes during submission specifically (see: KB-EUSURVEY-TECH-001)
* General service slowdown (see: KB-EUSURVEY-TECH-008)
* What to do with a blank screen (see: KB-EUSURVEY-TECH-003)
* High concurrent submissions (see: KB-EUSURVEY-TECH-007)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* If the issue is reproducible, contact the survey owner with details about which page and what browser you used.

## Retrieval Metadata

* business_domain: technical_access
* user_role: respondent, survey_manager
* feature: page_navigation, submission
* tags: freeze on next, survey hangs on navigation, stuck on page, unresponsive during submit, page transition freeze
* synonyms: survey freezes when I click next, stuck after clicking submit, page does not advance, cannot go to next page, survey stops responding
* product_terms: Next, Submit, Save as Draft, Validate per page, file upload, local storage
* exclude: blank screen, 403 errors, login issues, editor errors
