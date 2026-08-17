# How to check the status of sent EUSurvey invitations

## Intent / Description

Explains where survey owners can inspect the status of invitations sent to participants, what each status means, and how to identify invitees who have not yet answered.

## Applies To

* Role(s): Survey Manager
* Feature: Invitation management, Participation groups
* Context: Survey owner wants to see whether invitations have been sent, reminded, or answered

## Short Answer

Survey owners can check invitation status through the Participants section of their survey. For each participation group (guest list), the system tracks whether each invitee has been invited, reminded, and how many times they have answered. The statuses available are:

- **Invited** — the date the invitation email was sent
- **Reminded** — the date a reminder was sent (if applicable)
- **Answers** — the number of completed contributions from that invitee

EUSurvey does not provide email delivery confirmation (e.g. whether the recipient opened the email). It only records that the invitation was dispatched.

## Steps / Procedure

1. Open your survey.
2. Go to the **Participants** section.
3. Select the relevant **participation group** (guest list).
4. Review the list of attendees. Each row shows:
   - The attendee's name and email
   - The **Invited** date (when the invitation email was sent)
   - The **Reminded** date (when the last reminder was sent, if any)
   - The **Answers** count (number of submitted contributions)
5. To identify invitees who have not answered, look for entries where the Answers column shows 0.
6. To resend a reminder, select the attendee(s) and use the send invitation function again. The system will update the Reminded date.

## Important Conditions / Limitations

* **No delivery tracking**: EUSurvey records that an invitation email was dispatched but does not confirm whether the recipient received, opened, or read it. There is no "delivered" or "opened" status.
* **Invitation status vs contribution status**: The invitation status tracks the sending action. The contribution status (submitted, draft) is tracked separately on the Results and Contributions pages.
* **Answers count**: This field is incremented when a contribution linked to the invitation is submitted. If the contribution is later reset to draft, the count is decremented.
* **Deactivated invitations**: If an invitation has been deactivated, the invitation link no longer works, but the status remains visible in the list.
* **Multiple groups**: A contact can appear in multiple participation groups with separate invitation statuses for each.

## Troubleshooting / Related Cases

* If an invitee says they did not receive the email but the Invited date is set: the email was dispatched. Check spam/quarantine folders, or verify the email address is correct.
* If the Answers count is 0 but you expect a contribution: the respondent may have saved a draft without submitting, or may have accessed the survey through a different link.
* If you need to verify a specific contribution was submitted: check the Results page and match the contribution to the invitation ID.

## Out of Scope / Separate Topics

* Why a participant did not receive an invitation email (see: KB-EUSURVEY-INVITE-003)
* How to cancel or deactivate a sent invitation (see: KB-EUSURVEY-INVITE-005)
* How to send invitation emails (see: SM-108)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager
* feature: invitation_status, participation_group
* tags: invitation status, check sent invitations, who answered, who did not answer, invited date, reminded date, answers count
* synonyms: how to see who received invitation, check if invitation was sent, invitation tracking, monitor invitations, see who responded to invitation
* product_terms: Participants, Invited, Reminded, Answers, participation group, guest list
* exclude: contribution status, Results page filters, email delivery confirmation, email open tracking
