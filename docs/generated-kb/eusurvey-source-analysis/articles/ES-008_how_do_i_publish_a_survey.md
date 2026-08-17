# ES-008 — How do I publish a survey?

## Intent / Description

This article explains how a survey owner publishes a survey to make it available for respondents.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Lifecycle
* Environment: All
* Article type: How-To
* UI location: Survey Overview page
* Backend location: ManagementController.publish()

## Short Answer

To publish a survey, navigate to the survey's Overview page and click the "Publish" button. Publishing creates a snapshot of the current draft as the live version. Once published, the survey URL becomes active and respondents can submit contributions. Only the survey owner can publish.

## Prerequisites / Required Permissions

* The user must be the survey owner.
* The survey must be in Draft or Unpublished state.
* The survey must have at least one element.
* All mandatory translations must be complete (if translations exist). Error: "The translation is not complete. Please add missing labels before publishing."
* If a validator is configured, the survey must be validated first.
* If the survey is a List Form, it must be validated.

## Procedure

1. Open the survey in the management area.
2. Navigate to the Overview page.
3. Review the survey for completeness.
4. Click the "Publish" button.
5. The system creates a published version of the survey.
6. The survey becomes accessible at its public URL: `{server.prefix}runner/{shortname}`.
7. If start/end dates are configured, the survey will auto-activate/deactivate accordingly.

## Important Conditions / Limitations

* Publishing creates a copy of the draft survey as the published version. Subsequent edits to the draft create "pending changes" that must be explicitly applied.
* If the survey has translations, all active translations must be complete before publishing.
* A survey can be configured for automatic publishing using the `automaticPublishing` setting with a start date.
* The first publication triggers a `sendFirstPublishedSurveyMail` notification (if configured).
* After first publication, the `firstPublished` date is recorded and cannot be changed.
* If the survey has critical complexity (too many elements/dependencies), a warning is shown but publishing is not blocked.
* The survey URL format is: `{server.prefix}runner/{shortname}`.
* OLAP tables for reporting are updated upon publishing.

## Troubleshooting

* **Publish button not visible**: Only the survey owner can publish. Check if you have the correct role.
* **"Translation is not complete" error**: Complete all active translations or deactivate incomplete ones before publishing.
* **"Survey could not be validated" error**: The survey validator has not yet approved the survey.
* **Survey not accessible after publishing**: Check if start/end dates are set. The survey may not be active yet.

## Related Articles

* ES-009 — How do I unpublish a survey?
* ES-010 — How do I apply changes after publication?
* ES-032 — How do I set start and end dates?
* ES-055 — How does automatic survey publishing work?

## Evidence / Source Traceability

* Backend: `src/main/java/com/ec/survey/controller/ManagementController.java` — method `publish()` (line ~511)
* Backend: `src/main/java/com/ec/survey/service/SurveyService.java` — methods `publish()`, `activate()`, `chargePublishedSurvey()`
* Message keys: `error.MissingTranslation`, `error.SurveyNotValidated`, `label.Publish`
* Configuration: `server.prefix` for URL generation
* Route: POST `/{shortname}/management` with action parameter

## Confidence and Review Status

High — behaviour is directly visible in UI and backend code.

## Metadata

* Domain: Survey Lifecycle
* EUSurvey area: Publication
* Feature: Publish Survey
* User intent: How do I publish a survey?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Draft or Unpublished
* Environment: All
* Keywords: publish, go live, make available, activate, launch
* Synonyms: put survey online, make survey accessible, launch survey, go live
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: Publishing makes survey publicly accessible
* Search boost terms: publish survey, make survey live, activate survey
* Source files: ManagementController.java, SurveyService.java
* Duplicate status: New
