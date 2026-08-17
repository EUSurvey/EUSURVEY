# ES-052 — How do I customize the confirmation page?

## Intent / Description

This article explains how to customize the page shown after a respondent submits their contribution.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Properties/Editor
* Backend location: ManagementController

## Short Answer

The confirmation page is displayed after a respondent submits their contribution. You can customize its content through survey Properties. Available options include custom text, metadata placeholders (Contribution ID, username, date), collected data values, and confirmation file attachments.

## Prerequisites / Required Permissions

* Survey owner

## Procedure

1. Open survey Properties.
2. Navigate to the Confirmation section.
3. Customize the confirmation page text.
4. Use available placeholders: {ContributionID}, {UserName}, {CreationDate}, {LastUpdate}, {Language}, {InvitationNumber}.
5. Include collected data using {IDxx} where xx is the question identifier.
6. Optionally add a confirmation file (document attachment).
7. Optionally configure a confirmation link to redirect respondents.
8. Save properties.

## Important Conditions / Limitations

* Available metadata placeholders: {InvitationNumber}, {ContributionID}, {UserName}, {CreationDate}, {LastUpdate}, {Language}.
* Collected data can be included using {IDxx} syntax where xx is the question identifier.
* A confirmation link can redirect respondents to an external page.
* A file can be attached to the confirmation page.
* An additional confirmation text popup can be configured.
* The confirmation page also shows the Contribution ID for future reference.

## Troubleshooting

* Placeholders not replaced: Ensure you use the exact placeholder syntax with curly braces.
* Data not showing: The referenced question ID may not have been answered.

## Related Articles

* ES-012 — How do respondents submit a contribution?
* ES-057 — How do I enable confirmation emails?
* ES-007 — How do I configure survey properties?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Classes: ManagementController, Survey
* Methods: confirmation, propertiesPost
* Routes: GET /{shortname}/management/confirmation
* Message keys: label.ConfirmationPage, label.ConfirmationText, label.ConfirmationLink, info.ConfirmationMarkUpPage, info.ConfirmationFileInfo, info.ConfirmationTextInfo
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Management
* EUSurvey area: Confirmation
* Feature: Confirmation Page
* User intent: How do I customize the confirmation page?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: confirmation, page, thank you, submit, complete, message
* Synonyms: customize thank you page, confirmation message, after submission page
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: confirmation page, customize after submission, thank you page
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Duplicate status: New
