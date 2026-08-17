# ES-029 — How do I import contacts?

## Intent / Description

This article explains how to bulk-import contacts into the address book from a file.

## Applies To

* Role(s): Registered User
* EUSurvey area: Contacts
* Environment: All
* Article type: How-To
* UI location: Address Book page
* Backend location: AddressBookController.importAttendees

## Short Answer

To import contacts in bulk, navigate to the Address Book and click 'Import'. Upload a file (XLS, XLSX, ODS, or CSV) containing contact data. Map the columns to contact fields (name, email, attributes) and confirm the import.

## Prerequisites / Required Permissions

* Authenticated user
* A properly formatted file with contact data

## Procedure

1. Navigate to the Address Book.
2. Click 'Import' button.
3. Select the file to upload (XLS, XLSX, ODS, or CSV).
4. Indicate if the document contains a header row.
5. Map file columns to contact fields (name, email, attributes).
6. Review the preview.
7. Click Import to confirm.
8. Contacts are added to your address book.

## Important Conditions / Limitations

* Supported file formats: XLS, XLSX, ODS, CSV.
* The file can contain a header row (configurable).
* Each contact must have at least a name and email.
* Contacts with existing email addresses are flagged.
* Invalid contacts (missing email or name) are skipped with a warning.
* Column delimiter can be specified for CSV files.

## Troubleshooting

* 'Please check your file for invalid contacts': Some rows have missing or invalid data.
* 'Contact with an e-mail address that is too long': Email addresses are limited to 255 characters.
* 'Contact with a name that is too long': Names are limited to 255 characters.

## Related Articles

* ES-028 — How do I manage the address book?
* ES-025 — How do I create a guest list?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/addressbook.js, src/main/webapp/WEB-INF/views/addressbook/addressbook-import.jsp
* Backend files: src/main/java/com/ec/survey/controller/AddressBookController.java, src/main/java/com/ec/survey/service/AttendeeService.java
* Classes: AddressBookController, AttendeeService
* Methods: importAttendees, importAttendeesCheck, importAttendees2, validateImport1Parameters
* Routes: POST /addressbook/importAttendees
* Message keys: label.ImportContactsStep1, label.ImportContactsStep2, label.ImportContactsStep3, error.invalidContacts, error.ContactEmailTooLong, error.ContactNameTooLong, error.ContactWithoutEmail, error.ContactWithoutName
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Contacts
* EUSurvey area: Address Book
* Feature: Import Contacts
* User intent: How do I import contacts?
* Article type: How-To
* User type: Registered User
* Required permission: Registered User
* Survey status: N/A
* Environment: All
* Keywords: import, contacts, bulk, file, upload, CSV, XLS
* Synonyms: bulk import contacts, upload contact file, mass add contacts
* Acronyms: N/A
* Related entities: Attendee
* Security / privacy relevance: Imported data contains personal information
* Search boost terms: import contacts, bulk upload, add contacts from file
* Source files: src/main/java/com/ec/survey/controller/AddressBookController.java, src/main/java/com/ec/survey/service/AttendeeService.java
* Duplicate status: New
