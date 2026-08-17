# How to cancel or deactivate a sent EUSurvey invitation

## Intent / Description

Explains whether and how a survey owner can cancel, deactivate, or invalidate an invitation that has already been sent, and what effect this has on the invitation link and existing contributions.

## Applies To

* Role(s): Survey Manager
* Feature: Invitation deactivation, Participation groups
* Context: A survey owner wants to prevent a participant from using a previously sent invitation link

## Short Answer

Yes, EUSurvey allows survey owners to **deactivate** individual invitations. Once deactivated, the unique invitation link included in the email will no longer grant access to the survey. However, the invitation email itself cannot be recalled — it has already been delivered.

Deactivation does not delete existing contributions that were already submitted using that invitation link.

## Steps / Procedure

1. Open your survey.
2. Go to the **Participants** section.
3. Select the relevant participation group (guest list).
4. Locate the attendee whose invitation you want to deactivate.
5. Select the attendee and use the **Deactivate** action.
6. The invitation link for that attendee will no longer work. If the participant clicks it, they will not be able to access the survey through that link.

**To reactivate a deactivated invitation:**

1. Follow the same steps above.
2. Select the deactivated attendee and use the **Activate** action.
3. The original invitation link becomes functional again.

## Important Conditions / Limitations

* **Email cannot be recalled**: Deactivation disables the link, but the email remains in the participant's inbox. You cannot unsend it.
* **Existing contributions are preserved**: If the participant already submitted a contribution before deactivation, that contribution remains in the Results. Deactivation does not delete answers.
* **Draft contributions**: If the participant saved a draft before deactivation, the draft may still exist but they will not be able to continue it through the invitation link.
* **No replacement invitation automatically sent**: Deactivation does not trigger a new email. If you want to issue a replacement, you must reactivate or send a new invitation manually.
* **Per-invitation action**: Deactivation applies to the specific invitation record. Other attendees in the same group are not affected.
* **Token-based invitations**: For token-based participation groups, tokens can also be deactivated individually through the same Participants interface.

## Troubleshooting / Related Cases

* If a participant reports their link no longer works and you deactivated it: reactivate the invitation if access should be restored.
* If you want to completely remove access: deactivate the invitation AND ensure the survey is not accessible through other means (e.g. public access, other guest lists).
* If you accidentally deactivated the wrong invitation: simply reactivate it.

## Out of Scope / Separate Topics

* Why an invitation link no longer works (from the participant's perspective) (see: KB-EUSURVEY-INVITE-006)
* How to send invitations (see: SM-108)
* How to delete a contribution (see: KB-EUSURVEY-DELETE-003)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager
* feature: invitation_deactivation
* tags: cancel invitation, deactivate invitation, disable invitation link, revoke invitation, stop invitation
* synonyms: how to cancel a sent invitation, revoke invitation access, undo sent invitation, disable invitation, prevent access via invitation link
* product_terms: Participants, Deactivate, Activate, participation group, invitation link
* exclude: delete contribution, unpublish survey, close survey, email recall
