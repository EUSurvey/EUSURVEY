# ES-012 — How do respondents submit a contribution?

## Intent / Description

This article explains how respondents (participants) answer and submit a survey contribution.

## Applies To

* Role(s): Respondent (anonymous, invited, registered)
* EUSurvey area: Survey Runner
* Environment: All
* Article type: How-To
* UI location: Runner page (`/runner/{shortname}`)
* Backend location: RunnerController.processSubmit()

## Short Answer

Respondents access the survey via its URL, fill in the questions, and click "Submit". If the survey uses multi-paging, respondents navigate through pages using Next/Previous buttons. After submission, a confirmation page is displayed with a Contribution ID that can be used to reference or edit the contribution later (if allowed).

## Prerequisites / Required Permissions

* The survey must be published and active (within start/end dates).
* The respondent must have appropriate access:
  - Open surveys: no prerequisite
  - Password-protected: respondent must know the password
  - EU Login secured: respondent must authenticate via EU Login
  - Invitation-only: respondent must use a valid invitation link/token

## Procedure

1. Access the survey URL (e.g., `{server}/runner/{shortname}` or invitation link).
2. If the survey requires authentication, log in or enter the password.
3. If CAPTCHA is enabled, complete the CAPTCHA challenge.
4. Fill in the survey questions (mandatory questions are marked).
5. If multi-paging is enabled, click "Next" to advance through pages.
6. Review answers if desired.
7. Click "Submit" to finalize the contribution.
8. A confirmation page is displayed showing:
   - The Contribution ID
   - Option to download as PDF (if enabled)
   - Option to edit the contribution later (if enabled)
   - Custom confirmation text (if configured by survey owner)

## Important Conditions / Limitations

* Mandatory questions must be answered before submission. Validation errors are highlighted.
* If the survey has a maximum contribution limit (`maxNumberContribution`), submissions are blocked when the limit is reached.
* If the survey restricts contributions per user (`allowedContributionsPerUser`), users cannot exceed the configured limit.
* For EU Login surveys with per-user limits: "This account has already been used to submit a contribution. Multiple submission is prohibited."
* For invitation-based surveys: "This access-link has already been used" appears if the token was already consumed.
* If deactivated guest lists are used: "The access for this guest-list has not yet been activated."
* File upload limits apply for file upload questions.
* Time-limited quizzes enforce submission within the time limit.
* The respondent's browser must have JavaScript and cookies enabled.
* Local storage backup is available (saves progress locally in the browser) unless disabled by the respondent.
* Page-wise validation (if enabled) validates each page before allowing navigation to the next.
* The "prevent going back" option disables the Previous button.
* Draft saving is only available if the survey owner enabled it.

## Troubleshooting

* **"The survey has been closed" error**: The survey end date has passed. Contact the survey owner.
* **"This access-link has already been used"**: The invitation link was already used for a submission.
* **Validation errors on submit**: Fill in all mandatory fields and correct any highlighted errors.
* **"Your session has expired"**: The browser session timed out. Reload the page and try again.
* **"The data could not be saved" (error.holf)**: This can happen if F5 is hit during processing. Check data and save draft again.
* **CAPTCHA not loading**: Ensure internet connection is active. The error "Submitting this survey will not be possible as it uses a CAPTCHA for security" appears when offline.
* **Network problems**: The message "Your browser seems to have no Internet connection" is shown. Restore connectivity and retry.

## Related Articles

* ES-013 — How do I save a survey as draft?
* ES-014 — How do I edit my contribution after submission?
* ES-015 — How do I download my contribution as PDF?
* ES-065 — How does local storage backup work?

## Evidence / Source Traceability

* Backend: `src/main/java/com/ec/survey/controller/RunnerController.java` — methods `runner()`, `processSubmit()`, `loadSurvey()`
* Backend: `src/main/java/com/ec/survey/tools/SurveyHelper.java` — methods `validateAnswerSet()`, `parseAnswerSet()`
* Frontend: `src/main/webapp/resources/js/runner.js` — submission and validation logic
* Frontend: `src/main/webapp/resources/js/runnerviewmodels.js` — Knockout.js view models
* Frontend: `src/main/webapp/WEB-INF/views/runner/`
* Frontend: `src/main/webapp/WEB-INF/views/thanks.jsp` — confirmation page
* Message keys: `error.ContributionClosedSurvey`, `error.InvitationUsed`, `error.InvitationDeactivated`, `error.UserAlreadySubmitted`, `error.UserAlreadySubmitted2`, `error.Session`, `error.holf`, `error.InternetConnection`
* Route: GET `/runner/{shortname}`, POST `/runner/{shortname}`

## Confidence and Review Status

High — behaviour is directly visible in UI and backend code.

## Metadata

* Domain: Survey Runner
* EUSurvey area: Respondent Workflow
* Feature: Submit Contribution
* User intent: How do respondents submit a contribution?
* Article type: How-To
* User type: Respondent
* Required permission: Access depends on survey security settings
* Survey status: Published and Active
* Environment: All
* Keywords: answer, submit, contribution, fill in, respond, complete, survey
* Synonyms: answer survey, fill in questionnaire, complete form, submit answers, respond to survey
* Acronyms: N/A
* Related entities: AnswerSet, Survey, Answer
* Security / privacy relevance: Contribution may be anonymous depending on survey settings
* Search boost terms: submit survey, answer survey, fill survey, complete questionnaire
* Source files: RunnerController.java, SurveyHelper.java, runner.js
* Duplicate status: New
