# ES-031 — How do I secure my survey?

## Intent / Description

This article explains how to restrict who can access and answer a survey.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Access Control
* Environment: All
* Article type: How-To
* UI location: Properties page
* Backend location: ManagementController.propertiesPost

## Short Answer

EUSurvey offers multiple security options: open access (anyone), password protection, EU Login authentication, or invitation-only access via guest lists. Configure security in the survey Properties page under the Security section.

## Prerequisites / Required Permissions

* The user must be the survey owner

## Procedure

1. Open the survey Properties page.
2. Navigate to the Security section.
3. Choose the security mode: Open, Password, EU Login (ECAS), or Guest List.
4. For password: enter the survey password.
5. For EU Login: choose who can access (all EU Login users, EU staff only, or contact list members only).
6. For guest lists: create and configure a guest list on the Participants page.
7. Save the properties.

## Important Conditions / Limitations

* Open: No restrictions, anyone can answer.
* Password: Respondents must enter a password to access the survey.
* EU Login: Respondents must authenticate via EU Login (with optional restrictions).
* Guest List (Invitation): Only participants with a valid invitation link can answer.
* CAPTCHA can be additionally enabled to prevent bot submissions.
* Security settings can be combined with guest lists for additional control.
* EU Login mode options: all EU Login users, EU institution staff only, contact list members only.

## Troubleshooting

* Survey accessible despite security settings: Check if the survey has been re-published with the security changes applied.
* EU Login not showing as option: The EU Login integration may not be configured on this server.

## Related Articles

* ES-030 — How do I manage access and privileges?
* ES-025 — How do I create a guest list?
* ES-026 — How do I send invitations to participants?
* ES-034 — What is anonymous survey mode?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/controller/RunnerController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Classes: ManagementController, RunnerController, Survey
* Methods: propertiesPost, loadSurvey
* Routes: POST /{shortname}/management/properties
* Message keys: label.SecureYourSurvey, label.SecureWithEULogin, label.SecureWithPassword, label.SurveySecurity, info.SecureYourSurveyNew, info.SecureYourSurveyUsers
* Configuration keys: showecas, casoss

## Confidence and Review Status

High

## Metadata

* Domain: Access Control
* EUSurvey area: Security
* Feature: Survey Security
* User intent: How do I secure my survey?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: security, password, EU Login, ECAS, access, restrict, protect
* Synonyms: protect survey, restrict access, password protect, EU Login security
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: Security settings control data access
* Search boost terms: secure survey, restrict access, password protect
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/controller/RunnerController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Duplicate status: New
