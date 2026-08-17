# ES-025 — How do I create a guest list?

## Intent / Description

This article explains how to create a guest list for inviting participants to a survey.

## Applies To

* Role(s): Survey Owner, Invitation Manager
* EUSurvey area: Participants
* Environment: All
* Article type: How-To
* UI location: Participants page
* Backend location: ParticipantsController.participants

## Short Answer

To create a guest list, navigate to the Participants page and click 'Create new guest list'. Choose the type: Contact list (from address book), EU list (EU Login users), or Token list (generated unique codes). Name the list, add members, and save.

## Prerequisites / Required Permissions

* ManageInvitations privilege or survey ownership
* Survey must exist
* For contact lists: contacts must exist in the address book

## Procedure

1. Navigate to the Participants page.
2. Click 'Create new guest list'.
3. Choose the guest list type: Contact list, EU list, or Token list.
4. Enter a name for the guest list.
5. Add participants: select contacts from address book, search for EU Login users, or specify token count.
6. For token lists, specify the number of tokens to generate.
7. Save the guest list.
8. The list is created and available for sending invitations.

## Important Conditions / Limitations

* Three types of guest lists: Contact list, EU list, Token list.
* Contact lists use contacts from your address book.
* EU lists use EU Login accounts (LDAP lookup).
* Token lists generate unique access codes.
* Maximum 1 million tokens per guest list.
* Guest lists can be activated or deactivated.
* Multiple guest lists can exist per survey.
* The guest list name must be provided.

## Troubleshooting

* 'You have to provide a name for the new guest list': Enter a name before saving.
* 'You exceeded the limit of 1 million tokens per guestlist': Reduce the number of tokens.

## Related Articles

* ES-026 — How do I send invitations to participants?
* ES-027 — How do I send reminders?
* ES-028 — How do I manage the address book?
* ES-070 — How do token guest lists work?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/participants.js
* Backend files: src/main/java/com/ec/survey/controller/ParticipantsController.java, src/main/java/com/ec/survey/tools/GuestListCreator.java, src/main/java/com/ec/survey/service/ParticipationService.java
* Classes: ParticipantsController, GuestListCreator, ParticipationService
* Methods: participants, saveguestlist, createTokens
* Routes: GET /{shortname}/management/participants
* Message keys: label.GuestList, label.CreateNewContactGuestlist, label.CreateNewEUGuestlist, label.CreateNewTokenGuestlist, error.ParticipantsGroupNameMissing, error.MaxTokenNumberExceeded, info.GuestListCreatedNew
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Participants
* EUSurvey area: Invitations
* Feature: Create Guest List
* User intent: How do I create a guest list?
* Article type: How-To
* User type: Survey Owner, Invitation Manager
* Required permission: Survey Owner, Invitation Manager
* Survey status: Any
* Environment: All
* Keywords: guest list, create, participants, contacts, tokens, invitations
* Synonyms: create invitation list, add participants, make guest list
* Acronyms: N/A
* Related entities: ParticipationGroup, Attendee
* Security / privacy relevance: Guest lists contain personal data (emails)
* Search boost terms: create guest list, add participants, invitation list
* Source files: src/main/java/com/ec/survey/controller/ParticipantsController.java, src/main/java/com/ec/survey/tools/GuestListCreator.java, src/main/java/com/ec/survey/service/ParticipationService.java
* Duplicate status: New
