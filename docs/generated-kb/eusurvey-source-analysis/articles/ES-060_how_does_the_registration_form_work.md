# ES-060 — How does the registration form work?

## Intent / Description

This article explains how the registration form feature creates contacts from survey submissions.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Management
* Environment: All
* Article type: Concept
* UI location: Properties page
* Backend location: ManagementController.java

## Short Answer

The registration form automatically inserts Name and Email questions and creates a contact in your address book for each submission. Useful for event registrations or building mailing lists.

## Prerequisites / Required Permissions

* Survey owner

## Procedure

1. Open survey Properties.
2. Enable Registration Form / Contact Creation.
3. Save.
4. Survey includes mandatory Name and Email fields.
5. Each submission creates a contact in your address book.

## Important Conditions / Limitations

* Adds two mandatory questions automatically.
* New contacts created in owner's address book.
* Collects personal data by design.

## Troubleshooting

No specific troubleshooting items identified.

## Related Articles

* ES-028 — How do I manage the address book?
* ES-007 — How do I configure survey properties?
* ES-012 — How do respondents submit a contribution?

## Evidence / Source Traceability

* Frontend: N/A
* Backend: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: ManagementController, Survey, SurveyService
* Methods: propertiesPost, checkRegistrationFormElements
* Routes: N/A
* Message keys: label.RegistrationForm, info.ContactsCreated, label.ContactCreation
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Management
* EUSurvey area: Contact Creation
* Feature: Registration Form
* User intent: How does the registration form work?
* Article type: Concept
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: registration, form, contacts, sign-up, email, name
* Synonyms: contact creation, registration survey, sign-up form
* Acronyms: N/A
* Related entities: Survey, Attendee
* Security / privacy relevance: Collects personal data (name, email)
* Search boost terms: registration form, contact creation, sign-up
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
