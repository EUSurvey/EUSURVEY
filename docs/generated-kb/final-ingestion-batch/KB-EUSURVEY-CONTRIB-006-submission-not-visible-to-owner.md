# Why a submitted contribution is not visible to the survey owner

## Intent / Description

Explains the verified reasons why a survey owner may not see a contribution that a respondent claims to have submitted.

## Applies To

* Role(s): Survey Manager
* Feature: Results, Contributions
* Context: A respondent says they submitted but the contribution does not appear in the survey owner's view

## Short Answer

If a respondent reports having submitted a contribution but the survey owner cannot find it, the cause is typically one of the following:

1. **The contribution is still a draft** — the respondent saved as draft but did not click Submit.
2. **Active filters** — the Results or Contributions page has filters applied that hide the contribution.
3. **Synchronisation delay** — exports and statistics use a reporting database that may be up to 12 hours behind. The contribution should appear on the Results page immediately, but exports may lag.
4. **Wrong survey** — the respondent submitted to a different version, copy, or similar survey.
5. **Contribution was deleted** — the contribution was removed.
6. **Not yet finalised** — in multi-page surveys, the respondent may have navigated away before completing submission on the final page.

## Steps / Procedure

**For the survey owner to investigate:**

1. Go to the survey's **Results** page. Check if the contribution appears here (this shows submitted contributions in real time).
2. Check whether any **filters** are active on the Results page. Clear all filters and check again.
3. Check the **Contributions** count on the Overview page. Does it match what you see in Results?
4. If the respondent had an invitation: go to **Participants** and check the Answers count for that invitation. A count of 0 means no submission was linked to it.
5. Ask the respondent:
   - Did they click the final **Submit** button?
   - Did they receive a confirmation page or email?
   - Do they have a **Contribution ID**?
6. If the respondent has a Contribution ID: search for it on the Results page.
7. If using exports: remember that exports use the reporting database. Wait up to 12 hours or generate a fresh export.

## Important Conditions / Limitations

* **Results page is real-time**: Submitted contributions appear on the Results page immediately. If it is not there, it was likely never submitted.
* **Exports have a delay**: The separate reporting database synchronises periodically (up to 12 hours). Use the Results page for immediate verification.
* **Drafts are not contributions**: A saved draft is not visible on the Results page. It only appears after the respondent completes submission.
* **No notification by default**: Unless the survey owner enabled report emails, there is no automatic notification when a contribution is submitted.
* **Anonymous surveys**: In anonymous surveys, you cannot link a contribution to a specific person, making it harder to verify whose contribution is missing.

## Troubleshooting / Related Cases

* If the respondent says they saw a confirmation page: the contribution should be in Results. Check filters.
* If the respondent only saved as draft: they need to return and click Submit.
* If the respondent used a different browser/device and the survey requires login: they may have started but not completed submission on both.
* If the Contributions count on Overview matches the count on Results: no contribution is missing from the system.

## Out of Scope / Separate Topics

* Why an export does not include latest responses (see: KB-EUSURVEY-RESULTS-002)
* What "incomplete contribution" means (see: KB-EUSURVEY-CONTRIB-002)
* How to see who has answered (see: KB-EUSURVEY-CONTRIB-010)
* Why Contributions and Results show different counts (see: KB-EUSURVEY-RESULTS-001)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contribution_management
* user_role: survey_manager
* feature: results_visibility, contribution_tracking
* tags: contribution not visible, missing submission, cannot find contribution, respondent submitted but not shown
* synonyms: where is the submitted answer, contribution not appearing, submitted contribution missing, cannot see respondent answer
* product_terms: Results, Contributions, filters, Contribution ID, draft, Submit
* exclude: export synchronisation delays, export errors, report timeouts
