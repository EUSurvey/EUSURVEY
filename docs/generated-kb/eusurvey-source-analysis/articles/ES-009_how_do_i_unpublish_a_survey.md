# ES-009 — How do I unpublish a survey?

## Intent / Description

This article explains how to unpublish a survey to stop accepting new contributions.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Lifecycle
* Environment: All
* Article type: How-To
* UI location: Overview page
* Backend location: ManagementController.unpublish()

## Short Answer

To unpublish a survey, navigate to the survey Overview page and click 'Unpublish'. This makes the survey unavailable to respondents. The survey URL will show an unavailability page. Existing contributions are preserved and remain accessible to the survey owner.

## Prerequisites / Required Permissions

* The user must be the survey owner
* The survey must be currently published

## Procedure

1. Open the survey in the management area.
2. Navigate to the Overview page.
3. Click the 'Unpublish' button.
4. Confirm the action.
5. The survey is taken offline and respondents can no longer submit contributions.

## Important Conditions / Limitations

* Unpublishing preserves all existing contributions.
* The survey URL shows an unavailability page after unpublishing.
* Background documents and PDF questionnaire can optionally remain available on the unavailability page.
* The survey can be re-published later.
* Unpublishing is required before deleting a published survey.

## Troubleshooting

* Survey still accessible after unpublishing: Clear browser cache or check if start/end dates override the status.

## Related Articles

* ES-008 — How do I publish a survey?
* ES-005 — How do I delete a survey?
* ES-010 — How do I apply changes after publication?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: ManagementController, SurveyService
* Methods: unpublish
* Routes: POST /{shortname}/management
* Message keys: label.PublishUnpublish, info.UnavailabilityPage, label.ShowDocsOnUnavailabilityPage, label.ShowPDFOnUnavailabilityPage
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Lifecycle
* EUSurvey area: Publication
* Feature: Unpublish Survey
* User intent: How do I unpublish a survey?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Published
* Environment: All
* Keywords: unpublish, offline, stop, close, deactivate
* Synonyms: take offline, stop survey, close survey, deactivate survey
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: unpublish survey, take survey offline, stop survey
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
