# ES-026 — How do I send invitations to participants?

## Intent / Description

This article explains how a survey owner or invitation manager sends invitation emails to participants.

## Applies To

* Role(s): Survey Owner, Invitation Manager (ManageInvitations privilege)
* EUSurvey area: Participants and Invitations
* Environment: All
* Article type: How-To
* UI location: Participants page (`/{shortname}/management/participants`)
* Backend location: ParticipantsController.sendInvitations()

## Short Answer

To send invitations, navigate to the Participants page of your survey, select or create a guest list, compose your invitation email using the template editor, select the recipients, and click "Send Invitations". Each invited participant receives a unique link to access the survey.

## Prerequisites / Required Permissions

* The user must have `ManageInvitations` local privilege or be the survey owner.
* The survey must be published (invitations contain the survey link).
* A guest list must exist with contacts, EU Login users, or tokens.
* The SMTP server must be configured on the server (property: `smtpserver`).

## Procedure

1. Open the survey in the management area.
2. Navigate to the "Participants" tab.
3. Create a guest list if one does not exist:
   - **Contact list**: Add contacts from the address book.
   - **EU list**: Add EU Login users from the directory.
   - **Token list**: Generate unique access tokens.
4. Select the guest list to use.
5. Click "Send Invitations".
6. Compose the invitation email:
   - Edit the subject line.
   - Edit the email body. Available placeholders: `{Name}`, `{Email}`, `{Host}`, and custom attributes.
   - Optionally save the email as a template for reuse.
7. Select recipients:
   - All contacts in the guest list.
   - Only uninvited contacts.
   - Only contacts who have not yet contributed.
8. Click "Send" to dispatch the invitations.
9. Each recipient receives an email with a unique invitation link.

## Important Conditions / Limitations

* Each invitation generates a unique link. If a respondent uses the link, it is marked as used.
* There is a limit of 1 million tokens per guest list (error: "You exceeded the limit of 1 million tokens per guestlist").
* For EU Login guest lists, the authentication method can be "Personal invitation link" or "EU Login authentication".
* The system can detect suspicious guest lists and send warning emails (via `CheckForSuspiciousGuestList`).
* Email templates support placeholders: `{Name}`, `{Email}`, `{Host}`, and custom address book attributes.
* The guest list must be saved before invitations can be sent.
* Invitations can be deactivated/reactivated per guest list.
* Sending reminders uses the same interface but targets only those who have not yet contributed.
* The mail sending process is asynchronous — a `MailTask` is created and processed by `InvitationMailCreator`.
* Invalid email addresses in the guest list will cause errors for those specific recipients.

## Troubleshooting

* **"You have to provide a name for the new guest list" error**: Enter a name before saving.
* **"The application cannot send an activation link by email due to a missing SMTP server configuration"**: The server SMTP is not configured. Contact the administrator.
* **Invitations not received**: Check spam folders. Verify email addresses are correct. Check that the SMTP server is properly configured.
* **"E-mail address of participant {0} is invalid"**: Remove the participant with invalid email before sending.
* **"The access for this guest-list has not yet been activated"**: Activate the guest list before respondents can use links.

## Related Articles

* ES-025 — How do I create a guest list?
* ES-027 — How do I send reminders?
* ES-028 — How do I manage the address book?
* ES-070 — How do token guest lists work?

## Evidence / Source Traceability

* Backend: `src/main/java/com/ec/survey/controller/ParticipantsController.java` — methods `sendInvitations()`, `sendInvitationsPOST()`, `saveguestlist()`
* Backend: `src/main/java/com/ec/survey/tools/InvitationMailCreator.java` — invitation email construction and sending
* Backend: `src/main/java/com/ec/survey/tools/GuestListCreator.java` — guest list creation logic
* Backend: `src/main/java/com/ec/survey/service/ParticipationService.java` — participation group management
* Frontend: `src/main/webapp/resources/js/sendinvitations.js`
* Frontend: `src/main/webapp/resources/js/participants.js`
* Message keys: `error.MaxTokenNumberExceeded`, `error.ParticipantsGroupNameMissing`, `error.InvalidEmailForParticipant`, `error.SmtpServerNotConfigured`, `info.MailsStarted`, `info.MailsFinished`
* Model: `ParticipationGroup.java`, `Invitation.java`, `Attendee.java`, `MailTask.java`
* Route: POST `/{shortname}/management/sendInvitations`

## Confidence and Review Status

High — behaviour is directly visible in UI and backend code.

## Metadata

* Domain: Participants and Invitations
* EUSurvey area: Invitations
* Feature: Send Invitations
* User intent: How do I send invitations to participants?
* Article type: How-To
* User type: Survey Owner, Invitation Manager
* Required permission: ManageInvitations
* Survey status: Published
* Environment: All
* Keywords: invite, invitation, email, send, participants, guest list, token
* Synonyms: invite people, send survey link, email participants, distribute survey
* Acronyms: N/A
* Related entities: ParticipationGroup, Invitation, Attendee, MailTask
* Security / privacy relevance: Contains personal data (email addresses)
* Search boost terms: send invitations, invite participants, email survey link
* Source files: ParticipantsController.java, InvitationMailCreator.java, GuestListCreator.java
* Duplicate status: New
