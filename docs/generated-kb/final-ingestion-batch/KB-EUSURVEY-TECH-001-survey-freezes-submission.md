# Why does the survey freeze or fail during submission?

## Intent / Description

Explains possible causes and solutions when a survey appears to freeze, hang, or produce an error during the submission process.

## Applies To

* Role(s): Respondent
* Feature: Survey Submission
* Context: The submit button was clicked but the page does not respond, shows an error, or the submission does not complete

## Short Answer

If the survey freezes, shows a loading spinner that never completes, or displays an error during submission, it is typically caused by one of the following:

1. **Network interruption**: Your internet connection was briefly lost during submission.
2. **Session timeout**: You spent too long on the survey and your session expired (you may need to log in again).
3. **Large file uploads**: If the survey includes file upload questions, large files may take longer or fail due to size limits.
4. **Mandatory questions not answered**: The system may be trying to validate your answers and the form cannot submit because of unfilled mandatory fields (check for error messages above specific questions).
5. **Server-side issue**: Rarely, the EUSurvey platform may experience temporary technical difficulties.

## Steps / Procedure

1. **Wait a moment**: Give the submission up to 30 seconds to complete, especially if you uploaded files.
2. **Check for error messages**: Scroll up through the form to see if any mandatory questions are highlighted with an error.
3. **Check your internet connection**: Ensure you are connected to the internet.
4. **Try again**: If nothing happens, try clicking Submit again (EUSurvey prevents duplicate submissions — clicking Submit twice will not create two contributions).
5. **Save as Draft first**: If Submit keeps failing, try clicking "Save as Draft" to preserve your answers, then try submitting again later.
6. **Clear browser cache**: Close the browser tab, clear your browser cache, and reopen the survey from the original link.
7. **Try a different browser**: If the issue persists, try using a different supported browser (Chrome, Firefox, Edge, or Safari).

## Important Conditions / Limitations

* EUSurvey prevents duplicate submissions: if your first click succeeded silently, clicking again will not create a duplicate.
* If your session expired, you may be redirected to the login page. After logging in, your draft should still be available.
* Browser local storage backup: EUSurvey periodically saves your answers locally in the browser. If you lose your session, some answers may be recoverable from local storage when you reopen the survey.
* Very long surveys (many questions, large file attachments) are more prone to timeouts.
* If the survey end date has passed during your session, you may no longer be able to submit.

## Troubleshooting / Related Cases

* If you see a "technical problem" message, the issue is likely server-side. Try again in a few minutes.
* If you are behind a corporate proxy or VPN, it may be blocking the submission request. Try without VPN if possible.
* If the issue is persistent across multiple browsers and devices, contact the survey owner using the Contact link on the survey page.

## Out of Scope / Separate Topics

* What to do if a contribution was not submitted on time (see separate article)
* Why am I asked to log in again (see: KB-EUSURVEY-TECH-002)
* How to save a draft (see: Where to find answers saved as draft)
* Why the survey shows a blank screen (see: KB-EUSURVEY-TECH-003)
* Why preview mode shows an error (see: KB-EUSURVEY-TECH-004)
* Why the survey freezes during page navigation (see: KB-EUSURVEY-TECH-008)
* 403 Forbidden error (see: KB-EUSURVEY-ACCESS-001)
* General platform slowness (see: KB-EUSURVEY-TECH-009)
* Export failures (see: KB-EUSURVEY-RESULTS-005)
* Survey duplication errors (see: KB-EUSURVEY-TECH-005)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* If the problem persists, contact the survey owner (use the Contact link on the survey page).
* For platform-wide issues, open a support ticket from the documentation page.

## Retrieval Metadata

* business_domain: technical_access
* user_role: respondent
* feature: survey_submission
* tags: freeze during submission, submit button not responding, submission loading, submission timeout
* synonyms: survey freezes when I submit, submission not working, cannot complete survey, survey hangs on submit, submit button stuck
* product_terms: Submit, Save as Draft, session timeout, local storage backup
* exclude: 403 error, CAPTCHA failure, editor errors, preview errors, duplication failure, export failure, general platform slowness, page navigation freeze, blank screen
