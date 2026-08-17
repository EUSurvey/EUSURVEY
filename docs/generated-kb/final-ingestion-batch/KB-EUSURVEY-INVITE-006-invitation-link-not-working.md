# Why an EUSurvey invitation link no longer works

## Intent / Description

Explains the verified causes why an invitation link to an EUSurvey survey may no longer function, and what the participant or survey owner can do.

## Applies To

* Role(s): Respondent, Survey Manager
* Feature: Invitation links, Survey access
* Context: A participant clicks an invitation link and cannot access the survey

## Short Answer

An invitation link may no longer work for several reasons:

1. **Deactivated invitation** — The survey owner deactivated the specific invitation.
2. **Closed survey** — The survey's end date has passed and it is no longer accepting responses.
3. **Unpublished survey** — The survey has been unpublished by the owner.
4. **Deleted survey** — The survey has been deleted.
5. **Incomplete or modified URL** — The link was truncated, modified, or copied incorrectly.
6. **Authentication requirement** — The survey requires login and the participant is not authenticated.
7. **Maximum contributions reached** — The survey has reached its maximum number of contributions for that invitation.

## Steps / Procedure

**For participants:**

1. Check that you copied the full link from the invitation email. Click directly rather than copying if possible.
2. If prompted to log in, complete authentication and try again.
3. If you see an error message, note the exact wording and contact the survey owner.
4. If the survey owner confirms the survey is still open, ask for a new invitation link.

**For survey owners investigating a reported issue:**

1. Go to **Participants** and find the attendee's invitation.
2. Check whether the invitation is **deactivated**. If so, reactivate it.
3. Check whether the survey is still **published** and the end date has not passed.
4. Verify the survey has not been deleted or unpublished.
5. Check whether the participant has already reached the maximum allowed contributions.
6. If all looks correct, ask the participant to try in a different browser or clear their cache.

## Important Conditions / Limitations

* **Unique link per invitation**: Each invitation has a unique link. If the invitation is deactivated, only that specific link stops working.
* **Survey state matters**: Even if the invitation is active, the survey must be published and within its start/end dates for the link to work.
* **No expiration on invitation links**: Invitation links do not expire independently — they remain valid as long as the invitation is active and the survey is accessible.
* **Browser-specific issues**: In rare cases, browser extensions, corporate proxies, or VPN configurations may interfere with access.

## Troubleshooting / Related Cases

* If the participant sees "The survey has not yet been published or has already been closed": the survey is not currently active. Check the survey dates.
* If the participant sees a 403 error: the survey may have access restrictions beyond the invitation.
* If the URL appears truncated: email clients sometimes break long URLs across lines. Ensure the full URL is used.

## Out of Scope / Separate Topics

* Why EUSurvey displays a 403 Forbidden error (see: KB-EUSURVEY-ACCESS-001)
* How to cancel or deactivate an invitation (see: KB-EUSURVEY-INVITE-005)
* Why a participant did not receive an invitation (see: KB-EUSURVEY-INVITE-003)
* What "Page Not Found" means (see: PM-03_02)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* Contact the survey owner using the information in the original invitation email.

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: respondent, survey_manager
* feature: invitation_link_access
* tags: invitation link broken, link not working, cannot access survey, expired link, deactivated link
* synonyms: invitation URL does not work, survey link invalid, cannot open survey from invitation, link stopped working, broken invitation link
* product_terms: invitation link, Participants, Deactivate, published, end date
* exclude: general 403 error, login problems, email delivery issues, Page Not Found
