# What happens if a respondent submits twice in EUSurvey?

## Intent / Description

Explains what occurs when a respondent attempts to submit a second contribution to the same survey.

## Applies To

* Role(s): Survey Manager, Respondent
* Feature: Duplicate submissions, Security settings
* Context: Respondent tries to submit more than once, or survey manager sees unexpected duplicates

## Short Answer

What happens depends on the survey's security settings:

1. **Secured survey with EU Login + "Contributions per user" limit**: If the limit is set to 1, the respondent will be blocked from submitting a second time. They will see a message indicating they have already contributed.
2. **Survey with token/invitation access**: Each invitation token allows exactly one submission. Using the same link again will show an error or the already-submitted contribution (if editing is enabled).
3. **Open survey (no authentication)**: The respondent can submit multiple times. Each submission creates a separate contribution. The system does not prevent this unless CAPTCHA or other anti-bot measures are active.
4. **Survey with "Allow participants to change their contribution"**: The respondent can modify their existing submission instead of creating a new one, effectively overwriting their previous answers.

## Steps / Procedure

**For survey managers wanting to prevent duplicate submissions:**

1. Go to **Properties** → **Security**.
2. Enable **"Secure your survey"** and **"Secure with EU Login"**.
3. Set **"Contributions per user"** to 1.
4. This ensures each authenticated user can submit only once.

**Alternative: Use a guest list with tokens:**

1. Create a guest list with one token per participant.
2. Each token can only be used once for submission.

## Important Conditions / Limitations

* **Open/anonymous surveys cannot prevent duplicates** from the same person (only from the same browser session via cookies, which can be cleared).
* **EU Login-secured surveys** reliably prevent duplicates per user account.
* **Token-based surveys**: Each token allows one submission. If a token is reused after submission, the system blocks or shows the already-submitted contribution.
* **CAPTCHA** prevents automated/bot submissions but does not prevent the same person from submitting manually twice.
* **The "Contributions per user" setting** only works when EU Login authentication is enabled.
* **If duplicates already exist** in an open survey, the survey manager can manually delete unwanted duplicate contributions from the Results page.

## Troubleshooting / Related Cases

* If a respondent complains they cannot submit again: their token may be used, or the "Contributions per user" limit is reached. This is expected behavior.
* If you see many duplicates from the same IP: consider enabling EU Login security or CAPTCHA.
* If a respondent needs to resubmit after being blocked: the survey manager can reset their contribution or increase the "Contributions per user" limit.

## Out of Scope / Separate Topics

* How to prevent robots from submitting (see: CAPTCHA/anti-bot settings)
* How to reset a respondent's contribution (see: KB-EUSURVEY-CONTRIB-001)
* How to delete a contribution (see: KB-EUSURVEY-DELETE-003)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contribution_management
* user_role: survey_manager, respondent
* feature: duplicate_submissions, security
* tags: submit twice, duplicate, multiple submissions, already submitted, contributions per user
* synonyms: what if respondent submits twice, duplicate submissions EUSurvey, can I submit more than once, I already submitted can I submit again, prevent double submission
* product_terms: Contributions per user, EU Login, token, guest list, CAPTCHA, duplicate
