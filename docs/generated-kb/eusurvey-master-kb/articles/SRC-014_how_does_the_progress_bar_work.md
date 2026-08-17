# SRC-014 — How does the progress bar work?

## Intent / Description

This article explains how the progress bar shows respondents their completion status.

## Applies To

* Role(s): Respondent
* EUSurvey area: Survey Runner
* Environment: All
* Article type: Concept
* UI location: Runner page
* Backend location: runner.js

## Short Answer

The progress bar shows how far respondents have progressed. Displays as percentage, ratio, or both. Dependencies can cause progress to jump as questions appear/hide.

## Prerequisites / Required Permissions

* progressBar must be enabled by survey owner

## Procedure

The analysed source code does not provide a complete user-facing procedure.

## Important Conditions / Limitations

* Display modes: percentage, ratio, or both.
* Calculated based on answered vs visible questions.
* Dependencies affect calculation dynamically.
* Not recommended with many dependencies.
* Multi-paging affects display.

## Troubleshooting

No specific troubleshooting items identified.

## Related Articles

* ES-049 — How do I enable multi-paging?
* ES-050 — How do I set up dependencies/visibility rules?
* ES-012 — How do respondents submit a contribution?

## Evidence / Source Traceability

* Frontend: src/main/webapp/resources/js/runner.js
* Backend: src/main/java/com/ec/survey/model/survey/Survey.java
* Classes: Survey, runner.js
* Methods: calculateProgressPercentage, updateProgress
* Routes: N/A
* Message keys: label.ProgressBar, info.ProgressBar, label.DisplayProgress
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Runner
* EUSurvey area: Navigation
* Feature: Progress Bar
* User intent: How does the progress bar work?
* Article type: Concept
* User type: Respondent
* Required permission: Respondent
* Survey status: Published
* Environment: All
* Keywords: progress, bar, completion, status, percentage
* Synonyms: completion indicator, progress display
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: progress bar, completion status, survey progress
* Source files: src/main/java/com/ec/survey/model/survey/Survey.java
* Duplicate status: New
