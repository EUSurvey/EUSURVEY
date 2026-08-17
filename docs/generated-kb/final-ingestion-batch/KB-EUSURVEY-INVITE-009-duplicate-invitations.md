# Why a participant received the same EUSurvey invitation more than once

## Intent / Description

Explains the verified causes why a participant may receive duplicate invitation emails from EUSurvey, and how to prevent this.

## Applies To

* Role(s): Survey Manager, Respondent
* Feature: Invitation sending, Participation groups
* Context: A participant reports receiving the same invitation email multiple times

## Short Answer

A participant may receive the same invitation more than once because:

1. **The survey owner sent invitations more than once** to the same participation group — resending generates a reminder for existing invitees.
2. **The same email address appears in multiple participation groups** selected for the survey.
3. **Duplicate contact records** exist in the Address Book with the same email address.
4. **The invitation was sent individually and as part of a group send** in separate actions.

EUSurvey does not automatically deduplicate recipients across different participation groups or across separate send actions.

## Steps / Procedure

**To check for duplicate sending:**

1. Open the survey and go to **Participants**.
2. Check how many participation groups are defined.
3. Within each group, check if the attendee appears more than once.
4. If the same email exists in multiple groups, the person will receive one invitation per group that sent invitations.

**To prevent duplicate invitations:**

1. Before sending, review the recipient list to ensure no email appears in more than one selected participation group.
2. When importing contacts, check for duplicate email addresses. EUSurvey skips duplicates within the same address book during import, but duplicates can exist across different groups.
3. Avoid resending to the entire group if you only intend to invite new additions — select only the specific recipients who need an invitation.

**To check if an invitation is a reminder vs. a new invitation:**

1. In the Participants view, check the **Reminded** date. If it is set, the attendee received a resend/reminder after the initial invitation.

## Important Conditions / Limitations

* **No cross-group deduplication**: EUSurvey does not check whether the same email address has already been invited from a different participation group.
* **Resending is a reminder**: When you send invitations again to a group that already has existing invitations, the system updates the Reminded date for existing invitees rather than creating a new invitation record. However, a new email is dispatched.
* **Same invitation link**: When an attendee is re-invited within the same group, the invitation link (uniqueId) remains the same. The participant does not receive conflicting links.
* **Different groups, different links**: If the same person is in two different participation groups, they receive two different invitation links — both will lead to the same survey.
* **No blocking of repeated sends**: EUSurvey does not prevent the survey owner from sending multiple times.

## Troubleshooting / Related Cases

* If a participant received two different links: they are likely in two participation groups. Both links should work, but only one contribution is usually needed.
* If a participant received the same link twice: the survey owner resent invitations. The participant can ignore the duplicate.
* To clean up duplicates: review participation groups and remove the attendee from any groups where they should not be listed.

## Out of Scope / Separate Topics

* Why contacts are skipped during import (see: KB-EUSURVEY-INVITE-001)
* How to check invitation status (see: KB-EUSURVEY-INVITE-002)
* How to prevent a respondent from submitting twice (see: KB-EUSURVEY-CONTRIB-004)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager, respondent
* feature: invitation_deduplication
* tags: duplicate invitation, received twice, same email multiple times, resend invitation, duplicate contact
* synonyms: why I got the same survey invitation twice, duplicate email sent, survey invitation received again, multiple invitations same survey
* product_terms: Participants, participation group, Reminded, Address Book, resend
* exclude: duplicate submission, duplicate contact import, email delivery issues
