# ES-047 — How does activity logging work?

## Intent / Description

This article explains how EUSurvey tracks and logs user activities on surveys.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Audit
* Environment: All
* Article type: Concept
* UI location: Activity page
* Backend location: ActivityController.activity

## Short Answer

EUSurvey can log user activities performed on surveys, such as edits, publications, deletions, and contribution management. Activity logging must be enabled per survey. Once enabled, the Activity page shows a chronological log of all actions with user, timestamp, and details.

## Prerequisites / Required Permissions

* Survey owner
* Activity logging must be enabled for the survey

## Procedure

1. Enable activity logging in survey settings (if not already enabled).
2. Navigate to the Activity page from the survey management menu.
3. View the chronological log of activities.
4. Use filters to narrow the activity view.
5. Export activities to a file if needed.

## Important Conditions / Limitations

* Activity logging must be explicitly enabled per survey.
* The system tracks: object (what was affected), event (what happened), property (what changed), old value, new value.
* Activities can be exported via the Exports page.
* The activity log includes user identification and timestamps.
* Administrators can configure which activity types are logged system-wide.
* Activity logging can be enabled/disabled from the administration panel.

## Troubleshooting

* No activities showing: Activity logging may not be enabled for this survey. Enable it in survey settings.

## Related Articles

* ES-006 — How do I edit a survey?
* ES-017 — How do I export survey results?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ActivityController.java, src/main/java/com/ec/survey/service/ActivityService.java, src/main/java/com/ec/survey/tools/activity/ActivityRegistry.java, src/main/java/com/ec/survey/model/Activity.java
* Classes: ActivityController, ActivityService, ActivityRegistry, Activity
* Methods: activity, log, isLogEnabled
* Routes: GET /{shortname}/management/activity
* Message keys: label.Activity, label.Activities, label.SurveyActivityLogging, label.EnableActivityLogging, label.DisableActivityLogging, label.ConfigureActivityLogging
* Configuration keys: N/A

## Confidence and Review Status

Medium

## Metadata

* Domain: Audit
* EUSurvey area: Activity Logging
* Feature: Activity Log
* User intent: How does activity logging work?
* Article type: Concept
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: activity, log, audit, tracking, history, changes
* Synonyms: audit log, activity history, track changes
* Acronyms: N/A
* Related entities: Activity, Survey
* Security / privacy relevance: Activity log may contain user actions
* Search boost terms: activity log, audit trail, track changes
* Source files: src/main/java/com/ec/survey/controller/ActivityController.java, src/main/java/com/ec/survey/service/ActivityService.java, src/main/java/com/ec/survey/tools/activity/ActivityRegistry.java, src/main/java/com/ec/survey/model/Activity.java
* Duplicate status: New
