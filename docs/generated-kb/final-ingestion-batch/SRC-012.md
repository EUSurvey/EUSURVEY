# SRC-012 — How do I enable confirmation emails?

## Intent / Description

This article explains how to enable automatic confirmation emails sent to respondents after submission.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Notifications
* Environment: All
* Article type: How-To
* UI location: Properties page
* Backend location: ManagementController.java

## Short Answer

When enabled, EUSurvey sends a confirmation email to respondents after they submit their contribution. The email includes the Contribution ID. Email addresses are taken from EU Login or guest list.

## Prerequisites / Required Permissions

* Survey owner
* SMTP configured
* Survey must use EU Login or guest list for email availability

## Procedure

1. Open survey Properties.
2. Enable Automatic confirmation e-mail.
3. Save.
4. Respondents receive confirmation emails after submission.

## Important Conditions / Limitations

* Email addresses sourced from EU Login or guest list.
* Not available for fully anonymous surveys without email.
* Email includes the Contribution ID.

## Troubleshooting

No specific troubleshooting items identified.

## Related Articles

* ES-052 — How do I customize the confirmation page?
* ES-012 — How do respondents submit a contribution?
* ES-056 — How do I configure report emails?

## Evidence / Source Traceability

* Frontend: N/A
* Backend: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Classes: ManagementController, Survey
* Methods: propertiesPost
* Routes: N/A
* Message keys: label.AutomaticConfirmationEmail, info.AutomaticConfirmationEmail
* Config keys: smtpserver

## Confidence and Review Status

High

## Metadata

* Domain: Notifications
* EUSurvey area: Email
* Feature: Confirmation Email
* User intent: How do I enable confirmation emails?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Published
* Environment: All
* Keywords: confirmation, email, notification, submission, receipt
* Synonyms: submission confirmation, email receipt, auto-reply
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: Sends emails containing contribution data
* Search boost terms: confirmation email, submission receipt
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Duplicate status: New
