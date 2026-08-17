# ES-007 — How do I configure survey properties?

## Intent / Description

This article explains how to access and modify the configuration settings of a survey.

## Applies To

* Role(s): Survey Owner, Form Manager
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Properties page
* Backend location: ManagementController.properties, propertiesPost

## Short Answer

Survey properties control all aspects of survey behaviour including access settings, contribution rules, appearance, notifications, and special modes. Access the Properties page from the survey management area. Properties are organized into sections covering general settings, security, contributions, notifications, and advanced features.

## Prerequisites / Required Permissions

* The user must be the survey owner or have FormManagement privilege
* The survey must be loaded in the session

## Procedure

1. Open the survey in the management area.
2. Navigate to the Properties page.
3. Configure settings organized by section: general, security, contributions, appearance, notifications, advanced.
4. Modify settings as needed.
5. Click Save to apply the changes.
6. If the survey is published, apply changes to make property updates effective.

## Important Conditions / Limitations

* Properties include: title, alias, contact info, language, start/end dates, security mode, CAPTCHA, draft saving, edit contribution, anonymous mode, quiz mode, multi-paging, progress bar, automatic publishing, notification emails, and more.
* Some properties are only available for certain survey types (Quiz, Delphi, eVote, etc.).
* Changing security settings after publication requires applying changes.
* The shortname cannot be changed after initial creation.

## Troubleshooting

* Properties not saving: Ensure all required fields are valid.
* Feature not visible in properties: The feature may be disabled by a server-level feature flag.

## Related Articles

* ES-006 — How do I edit a survey?
* ES-031 — How do I secure my survey?
* ES-032 — How do I set start and end dates?
* ES-033 — How do I enable quiz mode?

## Evidence / Source Traceability

* Frontend files:
* N/A
* Backend files:
* src/main/java/com/ec/survey/controller/ManagementController.java
* src/main/java/com/ec/survey/model/survey/Survey.java
* Classes: ManagementController, Survey
* Methods: properties, propertiesPost, updateSurvey
* Routes: GET /{shortname}/management/properties, POST /{shortname}/management/properties
* Message keys: label.Properties, label.Save
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Management
* EUSurvey area: Survey Properties
* Feature: Survey Properties
* User intent: How do I configure survey properties?
* Article type: How-To
* User type: Survey Owner, Form Manager
* Required permission: Survey Owner, Form Manager
* Survey status: Any
* Environment: All
* Keywords: properties, settings, configure, options, parameters
* Synonyms: survey settings, configure form, change settings
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: Some properties affect data privacy (anonymous mode, security)
* Search boost terms: survey properties, configure survey, survey settings
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Duplicate status: New
