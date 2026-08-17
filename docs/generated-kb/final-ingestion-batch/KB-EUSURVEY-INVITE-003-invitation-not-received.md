# Why a participant did not receive an EUSurvey invitation email

## Intent / Description

Explains the verified causes why a participant may not have received an invitation email from EUSurvey, and what steps the survey owner can take.

## Applies To

* Role(s): Survey Manager, Respondent
* Feature: Invitation emails, Participation groups
* Context: A participant reports they did not receive an expected invitation email

## Short Answer

If a participant reports not receiving an invitation email, the most common causes are:

1. The email went to their spam or quarantine folder.
2. The email address in the contact record is incorrect or outdated.
3. The invitation was not actually sent (no Invited date in the participation group).
4. The participant was not included in the selected recipient list.
5. The email was blocked by the recipient's mail server or organisational filtering.

EUSurvey dispatches invitation emails but does not receive delivery confirmations from recipient mail servers.

## Steps / Procedure

**For the survey owner:**

1. Open the survey and go to **Participants**.
2. Select the relevant participation group.
3. Find the participant's entry and check:
   - Is there an **Invited** date? If not, the invitation was never sent to this contact.
   - Is the **email address** correct? Check for typos, spaces, or outdated addresses.
4. If the invitation was sent but not received:
   - Ask the participant to check their spam, junk, or quarantine folder.
   - Verify the email address is still active and correct.
   - Resend the invitation: select the attendee and send again. The system will update the Reminded date.

**For the participant:**

1. Check your spam, junk, or quarantine folder.
2. Search for emails from the survey's reply-to address or containing "EUSurvey" in the subject.
3. If nothing is found, contact the survey owner and confirm your email address.

## Important Conditions / Limitations

* **No delivery receipt**: EUSurvey records that it sent the email but cannot confirm whether it was delivered to the recipient's inbox.
* **Email validation**: EUSurvey validates email format during contact import but cannot verify that the mailbox exists or is actively monitored.
* **Organisational filters**: Corporate mail systems, government gateways, or anti-spam services may quarantine or block the email. The sender address uses the EUSurvey platform domain.
* **Bulk sending**: When invitations are sent to many recipients, they are dispatched sequentially. All selected recipients should receive their email, but delivery depends on each recipient's mail infrastructure.
* **Resending**: Sending the invitation again to the same contact is supported. The system updates the Reminded date and generates a new dispatch, but uses the same invitation link (uniqueId).

## Troubleshooting / Related Cases

* If no Invited date is shown: the contact was not included when the invitation was sent. Add them and send again.
* If the email address has a typo: correct it in the Address Book, then resend.
* If the email is correct and was sent but not received after checking spam: the issue is likely at the recipient's mail infrastructure. Consider asking the participant to whitelist the sender domain or use an alternative email address.
* For repeated delivery failures to the same domain: there may be a domain-level block. This cannot be resolved from within EUSurvey.

## Out of Scope / Separate Topics

* Why invitation emails may go to spam (see: KB-EUSURVEY-INVITE-004)
* How to check invitation status (see: KB-EUSURVEY-INVITE-002)
* How to resend an invitation (see: SM-108)
* Why an invitation link no longer works (see: KB-EUSURVEY-INVITE-006)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* If repeated delivery failures occur, the survey owner may escalate through their organisation's IT service.

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager, respondent
* feature: invitation_email_delivery
* tags: invitation not received, email not delivered, missing invitation, did not get email, resend invitation
* synonyms: participant says no email, invitation email missing, never received survey invitation, where is my invitation email, invitation not arriving
* product_terms: Participants, Invited, Address Book, resend, participation group
* exclude: invitation link errors, deactivated invitation, contribution status, email open tracking
