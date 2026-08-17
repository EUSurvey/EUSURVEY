# What confirmation a respondent receives after submitting a contribution

## Intent / Description

Explains what happens after a respondent clicks Submit, including the on-screen confirmation, optional email confirmation, and how to verify successful submission.

## Applies To

* Role(s): Respondent, Survey Manager
* Feature: Submission confirmation, Confirmation email
* Context: A respondent wants to know if their submission was successful, or a survey owner wants to understand what participants see

## Short Answer

After successfully submitting a contribution, the respondent receives:

1. **On-screen confirmation page** — A page confirming the submission was received. This page displays the **Contribution ID** and may offer options to download a PDF or edit the contribution (depending on survey settings).
2. **Confirmation email** (optional) — If the survey owner has enabled the confirmation email feature AND the respondent provided an email address, a confirmation email is sent automatically after submission.
3. **Contribution PDF** (optional) — If enabled by the survey owner, the respondent can download a PDF summary of their answers from the confirmation page.

## Steps / Procedure

**For respondents — verifying successful submission:**

1. After clicking Submit, you should see a confirmation page with a success message.
2. Note your **Contribution ID** shown on this page — this is your proof of submission.
3. If the survey sends confirmation emails and you provided your email, check your inbox.
4. If PDF download is available, download your summary from the confirmation page.

**For survey owners — configuring confirmation:**

1. **Confirmation page**: This is always shown after successful submission. It can be customised via the survey's confirmation page settings.
2. **Confirmation email**: Go to **Properties** and enable **"Send confirmation email to participants"**. The respondent must have provided their email address (e.g. through a required email question or via their invitation).
3. **PDF download**: Enable **"Allow participants to download a PDF copy"** in Security settings.
4. **Contribution editing link**: If **"Allow participants to change their contribution"** is enabled, the confirmation page also shows an editing link.

## Important Conditions / Limitations

* **Confirmation page always appears**: If you see the confirmation page with a Contribution ID, your submission was successful.
* **Confirmation email requires email address**: The system can only send a confirmation email if it has the respondent's email address. In anonymous surveys without an email question, no confirmation email is sent.
* **Confirmation email is optional**: The survey owner must enable this feature. It is disabled by default.
* **Missing email ≠ failed submission**: Not receiving a confirmation email does not mean the submission failed. The on-screen confirmation is the primary indicator of success.
* **Contribution ID is unique**: Each submitted contribution has a unique ID. This can be used to verify or edit the contribution later.
* **Customisable confirmation page**: The survey owner can customise the text and links on the confirmation page (e.g. redirect to an external site).

## Troubleshooting / Related Cases

* If you did not see a confirmation page: your submission may not have completed. Check your browser (did it show an error?). Try submitting again — EUSurvey prevents duplicates.
* If you expected a confirmation email but did not receive one: check your spam folder. Also confirm the survey has this feature enabled and that you provided a valid email.
* If you lost your Contribution ID: check your confirmation email if one was sent. Otherwise, contact the survey owner.

## Out of Scope / Separate Topics

* How to reopen a saved draft (see: KB-EUSURVEY-CONTRIB-005)
* Why the contribution summary cannot be opened (see: KB-EUSURVEY-CONTRIB-008)
* How to edit a submitted contribution (see: KB-EUSURVEY-CONTRIB-003)
* What a Contribution ID is (see: PM-05_04)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contribution_management
* user_role: respondent, survey_manager
* feature: submission_confirmation
* tags: confirmation after submission, confirmation email, contribution ID, proof of submission, successful submit
* synonyms: what happens after submitting, did my submission work, how do I know my answer was submitted, confirmation message, receipt of submission
* product_terms: confirmation page, Contribution ID, confirmation email, Submit, PDF download
* exclude: draft saving, export results, invitation confirmation
