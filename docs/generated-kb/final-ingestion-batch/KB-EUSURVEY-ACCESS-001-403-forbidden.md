# Why EUSurvey displays a 403 Forbidden error when opening a survey

## Intent / Description

Explains the verified causes of a 403 Forbidden error when a user tries to access an EUSurvey survey, and how to resolve it.

## Applies To

* Role(s): Respondent, Survey Manager
* Feature: Survey access, Security
* Context: A user clicks a survey link and sees a "403 Forbidden" error page

## Short Answer

A 403 Forbidden error means the server understood the request but refuses to grant access. In EUSurvey, this occurs when:

1. **The survey requires authentication and the user is not logged in** — or is logged in with insufficient privileges.
2. **The survey is restricted to specific participants** — via guest lists, tokens, or EU Login member restrictions.
3. **The user's session has become invalid** — session corruption or security checks failing.
4. **URL manipulation detected** — the server rejects the request as potentially unsafe.
5. **The user does not have form management or result access privileges** — for back-office pages.

A 403 is different from a 404 (page not found) and from an "access denied" page.

## Steps / Procedure

**For respondents:**

1. Check if the survey requires **login**. If so, log in first and try the link again.
2. If you received an **invitation link**, ensure you are using the exact link from the invitation email (not a modified or general survey URL).
3. Clear your browser cookies and cache, then try again.
4. Try a different browser.
5. If the issue persists, contact the survey owner and describe the error.

**For survey owners investigating a 403 reported by participants:**

1. Check the survey's **security settings**: Is it restricted to invited participants only? Is a password required? Is EU Login required?
2. Check whether the participant's invitation is **active** (not deactivated).
3. Check if the survey is still **published** and within its active dates.
4. If the survey uses EU Login restrictions, ensure the participant has the correct EU Login account type.
5. Try the survey URL yourself (in a private/incognito window) to see if you get the same error.

## Important Conditions / Limitations

* **403 ≠ "Access Denied" page**: EUSurvey has a separate "access denied" page for some restriction scenarios. A raw 403 typically means the request was blocked at a lower level.
* **403 ≠ 404**: A 404 means the page does not exist. A 403 means it exists but access is denied.
* **403 ≠ invalid credentials**: Invalid login credentials produce their own error, not a 403.
* **Security restrictions are survey-specific**: Each survey has its own access configuration. Being logged in to EUSurvey does not automatically grant access to all surveys.
* **Token/invitation validity**: If the survey uses token-based access and the token is invalid or deactivated, access is denied.

## Troubleshooting / Related Cases

* If the error occurs only for certain users: check their privilege level or invitation status.
* If the error occurs for everyone including the owner: there may be a platform-level issue. Try again later.
* If the URL contains unusual characters: ensure the URL has not been modified or truncated.

## Out of Scope / Separate Topics

* Why access to a survey is denied (general) (see: KB-EUSURVEY-ACCESS-002)
* Why a Page Not Found error appears (see: PM-03_02)
* How to resolve invalid credentials (see: KB-EUSURVEY-ACCESS-004)
* Why an invitation link no longer works (see: KB-EUSURVEY-INVITE-006)
* API authentication errors (see: WS-002, WS-005)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: access_authentication
* user_role: respondent, survey_manager
* feature: survey_access, security
* tags: 403, forbidden, access denied, cannot access survey, permission denied
* synonyms: 403 error survey, forbidden when opening survey, permission denied EUSurvey, cannot enter survey 403
* product_terms: 403, Forbidden, Security, guest list, EU Login, token, published
* exclude: 404 Page Not Found, invalid credentials, API 401 Unauthorized, session timeout, invitation link errors
