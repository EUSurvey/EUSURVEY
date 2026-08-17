# ES-066 — How does the motivation popup work?

## Intent / Description

This article explains the motivation popup that encourages respondents to complete the survey.

## Applies To

* Role(s): Respondent
* EUSurvey area: Survey Runner
* Environment: All
* Article type: Concept
* UI location: Runner page
* Backend location: runner.js, Survey.java

## Short Answer

The motivation popup appears during survey completion to encourage respondents. Triggered by progress percentage or elapsed time. Configured by the survey owner in Properties.

## Prerequisites / Required Permissions

* motivationPopup must be enabled by survey owner

## Procedure

The analysed source code does not provide a complete user-facing procedure.

## Important Conditions / Limitations

* Two triggers: progress percentage or time (minutes).
* Appears once per session.
* Custom title and text configurable.
* Configured in survey Properties.

## Troubleshooting

No specific troubleshooting items identified.

## Related Articles

* ES-067 — How does the progress bar work?
* ES-007 — How do I configure survey properties?
* ES-012 — How do respondents submit a contribution?

## Evidence / Source Traceability

* Frontend: src/main/webapp/resources/js/runner.js
* Backend: src/main/java/com/ec/survey/model/survey/Survey.java
* Classes: Survey, runner.js
* Methods: 
* Routes: N/A
* Message keys: label.MotivationPopup, info.MotivationPopup, label.MotivationPopupTitle, label.MotivationPopupThreshold, label.MotivationPopupTrigger
* Config keys: N/A

## Confidence and Review Status

Medium

## Metadata

* Domain: Survey Runner
* EUSurvey area: Engagement
* Feature: Motivation Popup
* User intent: How does the motivation popup work?
* Article type: Concept
* User type: Respondent
* Required permission: Respondent
* Survey status: Published
* Environment: All
* Keywords: motivation, popup, encourage, progress, engagement
* Synonyms: encouragement popup, motivation message
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: motivation popup, encourage completion
* Source files: src/main/java/com/ec/survey/model/survey/Survey.java
* Duplicate status: New
