# ES-032 — How do I set start and end dates?

## Intent / Description

This article explains how to configure start and end dates for a survey.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Lifecycle
* Environment: All
* Article type: How-To
* UI location: Properties page
* Backend location: ManagementController.propertiesPost

## Short Answer

Set start and end dates in the survey Properties page. The start date determines when the survey automatically becomes active (if automatic publishing is enabled). The end date determines when the survey automatically stops accepting contributions.

## Prerequisites / Required Permissions

* Survey owner
* Survey must exist

## Procedure

1. Open the survey Properties page.
2. Locate the Dates section.
3. Set the Start date (optional).
4. Set the End date (optional).
5. Save the properties.

## Important Conditions / Limitations

* If automatic publishing is enabled, the survey activates on the start date.
* The survey automatically stops accepting contributions on the end date.
* End date is enforced even if the survey is published.
* Start/end dates can be combined with manual publish/unpublish actions.
* The end month must not be smaller than the start month.

## Troubleshooting

* Survey not accessible despite start date: Ensure automatic publishing is enabled.
* Survey still accessible after end date: Check if the time component includes the full day.

## Related Articles

* ES-008 — How do I publish a survey?
* ES-055 — How does automatic survey publishing work?
* ES-007 — How do I configure survey properties?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java, src/main/java/com/ec/survey/service/SchedulerService.java
* Classes: ManagementController, Survey, SchedulerService
* Methods: propertiesPost, getSurveysToStart, getSurveysToStop
* Routes: POST /{shortname}/management/properties
* Message keys: label.StartDate, label.EndDate, label.EndsOn, label.StartsOn, error.EndMonthSmaller
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Lifecycle
* EUSurvey area: Survey Properties
* Feature: Start/End Dates
* User intent: How do I set start and end dates?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: start date, end date, schedule, timing, period, active
* Synonyms: schedule survey, set dates, define active period
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: set start end dates, schedule survey, configure dates
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java, src/main/java/com/ec/survey/service/SchedulerService.java
* Duplicate status: New
