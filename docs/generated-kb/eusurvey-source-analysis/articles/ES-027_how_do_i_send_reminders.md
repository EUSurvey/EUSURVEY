# ES-027 — How do I send reminders?

## Intent / Description

This article explains how to send reminder emails to invited participants who have not yet contributed.

## Applies To

* Role(s): Survey Owner, Invitation Manager
* EUSurvey area: Participants
* Environment: All
* Article type: How-To
* UI location: Participants page
* Backend location: ParticipantsController.sendInvitations

## Short Answer

To send reminders, navigate to the Participants page, select a guest list, and use the 'Send Invitations' function targeting only those who have not yet contributed. The system sends reminder emails to participants whose invitation links have not been used.

## Prerequisites / Required Permissions

* ManageInvitations privilege or survey ownership
* A guest list with sent invitations must exist
* The survey must be published

## Procedure

1. Navigate to the Participants page.
2. Select the guest list.
3. Click 'Send Invitations' (or 'Send reminder').
4. Select 'Invited who did not yet contribute' as the recipient filter.
5. Compose the reminder email.
6. Click Send.

## Important Conditions / Limitations

* Reminders use the same mechanism as initial invitations.
* You can filter to send only to those who have not yet contributed.
* The reminder date is tracked per invitation.
* Email content can be customized for reminders vs initial invitations.
* Templates can be reused across reminder rounds.

## Troubleshooting

* Reminder sent to already-contributed participants: Use the 'Select invited who did not yet contribute' filter option.

## Related Articles

* ES-026 — How do I send invitations to participants?
* ES-025 — How do I create a guest list?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/participants.js, src/main/webapp/resources/js/sendinvitations.js
* Backend files: src/main/java/com/ec/survey/controller/ParticipantsController.java, src/main/java/com/ec/survey/tools/InvitationMailCreator.java
* Classes: ParticipantsController, InvitationMailCreator
* Methods: sendInvitations, sendInvitationsPOST
* Routes: POST /{shortname}/management/sendInvitations
* Message keys: label.SendReminder, label.Reminder, label.ReminderDate, label.SelectInvited
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Participants
* EUSurvey area: Invitations
* Feature: Send Reminders
* User intent: How do I send reminders?
* Article type: How-To
* User type: Survey Owner, Invitation Manager
* Required permission: Survey Owner, Invitation Manager
* Survey status: Published
* Environment: All
* Keywords: reminder, resend, follow-up, nudge
* Synonyms: send reminder email, follow up with participants, resend invitation
* Acronyms: N/A
* Related entities: ParticipationGroup, Invitation, MailTask
* Security / privacy relevance: Contains personal email data
* Search boost terms: send reminder, follow up, resend invitation
* Source files: src/main/java/com/ec/survey/controller/ParticipantsController.java, src/main/java/com/ec/survey/tools/InvitationMailCreator.java
* Duplicate status: New
