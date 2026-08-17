# ES-062 — How do I contact the survey owner?

## Intent / Description

This article explains how respondents can contact the survey owner via the built-in contact form.

## Applies To

* Role(s): Respondent
* EUSurvey area: Survey Runner
* Environment: All
* Article type: How-To
* UI location: Runner page
* Backend location: HomeController.java

## Short Answer

Respondents can contact the survey owner using the contact form on the survey page. The form sends a message to the configured contact email address.

## Prerequisites / Required Permissions

* Survey must have contact info configured
* Survey must be accessible

## Procedure

1. Access the survey page.
2. Find the contact form link.
3. Fill in your message.
4. Submit the form.
5. Message is sent to the survey owner.

## Important Conditions / Limitations

* Requires survey contact email to be configured.
* May require respondent name and email.
* Not available if no contact configured.

## Troubleshooting

No specific troubleshooting items identified.

## Related Articles

* ES-012 — How do respondents submit a contribution?
* ES-007 — How do I configure survey properties?

## Evidence / Source Traceability

* Frontend: N/A
* Backend: src/main/java/com/ec/survey/controller/HomeController.java
* Classes: HomeController
* Methods: contactform, contactformPOST
* Routes: GET /runner/contactform, POST /runner/contactform
* Message keys: label.ContactForm, label.ContactSurveyOwner, info.ContactForm, info.ContactFormClose
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Runner
* EUSurvey area: Contact Form
* Feature: Contact Owner
* User intent: How do I contact the survey owner?
* Article type: How-To
* User type: Respondent
* Required permission: Respondent
* Survey status: Published
* Environment: All
* Keywords: contact, owner, message, form, help
* Synonyms: contact survey creator, send message to owner, get help
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: contact survey owner, send message, get help
* Source files: src/main/java/com/ec/survey/controller/HomeController.java
* Duplicate status: New
