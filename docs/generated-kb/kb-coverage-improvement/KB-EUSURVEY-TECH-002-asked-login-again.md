# Why am I asked to log in again while using EUSurvey?

## Intent / Description

Explains why a user may be unexpectedly redirected to the login page during an EUSurvey session.

## Applies To

* Role(s): Survey Manager, Respondent
* Feature: Authentication, Session Management
* Context: User was working on EUSurvey and suddenly gets redirected to the EU Login page

## Short Answer

EUSurvey sessions have a limited duration. If you are inactive for too long, or if your session expires due to server-side timeout, you will be redirected to the EU Login page. This is a security measure to protect your account and data.

Common reasons you are asked to log in again:

1. **Session timeout**: You were inactive (no page interaction) for an extended period.
2. **Browser cookies cleared**: Your browser cleared cookies or you switched to a private/incognito window.
3. **Server maintenance**: The server was restarted, invalidating active sessions.
4. **Multiple tabs/windows**: Working in multiple browser tabs may sometimes cause session conflicts.
5. **EU Login token expired**: The authentication token from EU Login has a fixed lifetime.

## Steps / Procedure

1. Log in again using your EU Login credentials.
2. After logging in, you should be redirected to where you were (EUSurvey saves your last location).
3. If you were filling in a survey as a respondent, your draft answers may be preserved:
   - If the survey has "Save as Draft" enabled, your progress is saved server-side.
   - Browser local storage may have a backup of your recent answers.
4. If you were editing a survey, unsaved changes in the editor will be lost. Always save your work frequently.

## Important Conditions / Limitations

* Session duration is configured by the server (typically 30–60 minutes of inactivity).
* Respondents filling in surveys that do not require authentication (anonymous surveys) are not affected by EU Login session timeouts.
* For authenticated surveys (requiring EU Login), the session timeout applies to respondents too.
* EUSurvey cannot extend your session indefinitely — this is a security requirement.
* If you use browser plugins that block cookies or scripts, sessions may expire prematurely.

## Troubleshooting / Related Cases

* If you are constantly asked to log in (every few minutes), check:
  - Your browser allows cookies from `ec.europa.eu`
  - You are not in private/incognito mode
  - Your system clock is correct (incorrect time can invalidate session cookies)
  - Your network/proxy is not stripping cookies from responses
* If you lost unsaved work in the editor, there is no recovery — always save frequently.
* If you lost survey answers as a respondent, check if a draft was saved by returning to the survey link.

## Out of Scope / Separate Topics

* How do I change my EU Login password (see: How do I change my password)
* Why do I see a blank screen (see separate article)
* How to save a draft contribution (see: Where to find answers saved as draft)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* For EU Login issues specifically, visit: https://webgate.ec.europa.eu/cas/

## Retrieval Metadata

* business_domain: technical_access
* user_role: survey_manager, respondent
* feature: authentication, session
* tags: login again, session expired, timeout, EU Login, redirect to login, logged out
* synonyms: why was I logged out, session expired EUSurvey, asked to log in again, why does EUSurvey log me out, authentication timeout, session timeout survey
* product_terms: EU Login, session, timeout, cookies, authentication
