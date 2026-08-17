# ES-028 — How do I manage the address book?

## Intent / Description

This article explains how to manage contacts in the EUSurvey address book.

## Applies To

* Role(s): Registered User
* EUSurvey area: Contacts
* Environment: All
* Article type: How-To
* UI location: Address Book page
* Backend location: AddressBookController

## Short Answer

The address book stores contacts that can be used in guest lists for survey invitations. Navigate to Address Book from the main menu to add, edit, delete, search, and organize contacts. Contacts have a name, email, and optional custom attributes.

## Prerequisites / Required Permissions

* Authenticated user

## Procedure

1. Navigate to the Address Book page.
2. View existing contacts in the table.
3. Click 'Create new Contact' to add a contact.
4. Enter name and email (required) and optional attributes.
5. Click Save.
6. Use search to find specific contacts.
7. Select contacts to edit or delete in batch.

## Important Conditions / Limitations

* Contacts require at least a name and a valid email address.
* Custom attributes can be added to contacts.
* Contacts can be shared with other users via Shares.
* The address book is personal to each user.
* Contacts can be imported in bulk from files.
* Contact email addresses must be valid.
* Editing contacts in the address book can optionally update them in guest lists.

## Troubleshooting

* 'A contact with this e-mail address already exists': The email is already in use. Choose to create anyway or skip.
* 'There is a contact without valid e-mail address': Ensure all contacts have a valid email.

## Related Articles

* ES-029 — How do I import contacts?
* ES-025 — How do I create a guest list?
* ES-040 — How do I share my address book?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/addressbook.js, src/main/webapp/WEB-INF/views/addressbook/addressbook.jsp
* Backend files: src/main/java/com/ec/survey/controller/AddressBookController.java, src/main/java/com/ec/survey/service/AttendeeService.java
* Classes: AddressBookController, AttendeeService
* Methods: attendees, add, delete, edit
* Routes: GET /addressbook
* Message keys: label.AddressBook, label.CreateNewContact, error.AttendeeExists, error.ContactWithoutEmail, error.ContactWithoutName, info.UpdateGuestlists
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Contacts
* EUSurvey area: Address Book
* Feature: Manage Contacts
* User intent: How do I manage the address book?
* Article type: How-To
* User type: Registered User
* Required permission: Registered User
* Survey status: N/A
* Environment: All
* Keywords: address book, contacts, email, manage, add
* Synonyms: manage contacts, contact list, email addresses
* Acronyms: N/A
* Related entities: Attendee
* Security / privacy relevance: Contains personal data (names, emails)
* Search boost terms: address book, manage contacts, contact list
* Source files: src/main/java/com/ec/survey/controller/AddressBookController.java, src/main/java/com/ec/survey/service/AttendeeService.java
* Duplicate status: New
