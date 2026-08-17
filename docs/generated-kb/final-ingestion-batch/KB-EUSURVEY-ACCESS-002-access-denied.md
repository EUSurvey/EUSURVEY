# Why access to an EUSurvey survey is denied

## Intent / Description

Explains the verified reasons why a user may be denied access to an EUSurvey survey, covering all restriction types.

## Applies To

* Role(s): Respondent
* Feature: Survey access restrictions
* Context: A user tries to open a survey and is told access is denied or restricted

## Short Answer

Access to an EUSurvey survey may be denied because:

1. **Invitation-only survey** — The survey is restricted to invited participants and requires a valid invitation link or token.
2. **EU Login required** — The survey requires authentication via EU Login and the user is not logged in or does not have the required account type.
3. **Password-protected survey** — The survey requires a password that the user has not entered.
4. **Survey not published** — The survey has not been published yet or has been unpublished.
5. **Survey closed** — The survey's end date has passed and it is no longer accepting responses.
6. **Maximum contributions reached** — The survey has reached its maximum number of allowed contributions.
7. **Missing form management privileges** — For back-office access, the user does not have the required role.

## Steps / Procedure

1. **Check if login is required**: If prompted to log in, authenticate with your EU Login account.
2. **Check if you need an invitation**: If the survey requires an invitation, use the link from your invitation email rather than a general URL.
3. **Check if a password is required**: If prompted for a password, enter the survey password provided by the survey owner.
4. **Verify the survey is open**: If you see a message that the survey is not published or has closed, the survey is no longer active. Contact the survey owner.
5. **Contact the survey owner**: If none of the above applies, contact the survey owner using the information on the error page or from your invitation email.

## Important Conditions / Limitations

* **Survey owners control access**: All access restrictions are set by the survey owner in the survey's Security settings.
* **Multiple restrictions may apply**: A survey can require both EU Login AND invitation, or a password AND invitation.
* **Deactivated invitation = denied access**: If your invitation was deactivated by the survey owner, your invitation link will not grant access.
* **No self-service resolution**: If you do not have the required access, only the survey owner can grant it.
* **Different from 403**: An "access denied" page may provide a more specific reason than a raw 403 HTTP error.

## Troubleshooting / Related Cases

* If you see "This survey has not yet been published": the survey is not yet available. Contact the survey owner.
* If you are logged in but still denied: the survey may require a specific EU Login account type or membership in a specific group.
* If the survey worked before but now denies access: it may have been closed, unpublished, or your invitation may have been deactivated.

## Out of Scope / Separate Topics

* 403 Forbidden HTTP error specifically (see: KB-EUSURVEY-ACCESS-001)
* Why an invitation link no longer works (see: KB-EUSURVEY-INVITE-006)
* Invalid credentials error (see: KB-EUSURVEY-ACCESS-004)
* EU Login not recognised (see: KB-EUSURVEY-ACCESS-005)
* How to restrict access to a survey (survey owner) (see: SM-44)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: access_authentication
* user_role: respondent
* feature: survey_access_restriction
* tags: access denied, cannot access, restricted survey, permission required, access blocked
* synonyms: survey says access denied, why can I not open the survey, no access to survey, survey restricted, not allowed to access survey
* product_terms: Security, invitation, EU Login, password, published, closed
* exclude: 403 HTTP error, editor access, API access, form management privileges
