# ES-055 — How does automatic survey publishing work?

## Intent / Description

This article explains how automatic survey publishing activates and deactivates surveys based on configured dates.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Lifecycle
* Environment: All
* Article type: Concept
* UI location: Properties page
* Backend location: SchedulerService.java

## Short Answer

If automatic publishing is enabled and a start date is set, EUSurvey automatically activates the survey when the start date is reached. Similarly, if an end date is set, the survey automatically stops accepting contributions. The scheduler checks hourly.

## Prerequisites / Required Permissions

* Survey owner
* automaticPublishing must be enabled
* Start/end dates must be configured

## Procedure

1. Open survey Properties.
2. Enable Automatic survey publishing.
3. Set a Start date.
4. Optionally set an End date.
5. Publish the survey.
6. The system activates/deactivates at the specified dates.

## Important Conditions / Limitations

* Requires automaticPublishing=true AND dates configured.
* Scheduler runs hourly to check dates.
* Survey must be published first for auto-activation.
* End date enforcement works independently.

## Troubleshooting

* Survey not activating: Ensure automatic publishing is enabled AND the survey is published.

## Related Articles

* ES-008 — How do I publish a survey?
* ES-032 — How do I set start and end dates?
* ES-009 — How do I unpublish a survey?

## Evidence / Source Traceability

* Frontend: src/main/webapp/resources/js/runner.js
* Backend: src/main/java/com/ec/survey/service/SchedulerService.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: SchedulerService, SurveyService
* Methods: doHourlySchedule, getSurveysToStart, getSurveysToStop
* Routes: N/A
* Message keys: label.AutomaticSurveyPublishing, label.Autopublish
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Lifecycle
* EUSurvey area: Publication
* Feature: Auto-Publish
* User intent: How does automatic survey publishing work?
* Article type: Concept
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: automatic, publish, schedule, start, end, activate
* Synonyms: auto-publish, scheduled activation, timed survey
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: automatic publishing, scheduled survey, auto-activate
* Source files: src/main/java/com/ec/survey/service/SchedulerService.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
