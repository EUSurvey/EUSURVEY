# SRC-013 — How do I recalculate quiz scores?

## Intent / Description

This article explains how to recalculate quiz scores after modifying scoring rules.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Quiz
* Environment: All
* Article type: How-To
* UI location: Results page
* Backend location: ManagementController.java

## Short Answer

If scoring rules change after submissions exist, recalculate all scores from the Results page. This updates all contribution scores based on current configuration.

## Prerequisites / Required Permissions

* Survey owner
* Quiz mode enabled
* Contributions must exist

## Procedure

1. Navigate to Results page.
2. Click Recalculate.
3. System recalculates all scores.
4. Updated scores appear in results.

## Important Conditions / Limitations

* Uses current scoring configuration.
* Previous scores are overwritten.
* Runs asynchronously for large datasets.
* Useful after changing correct answers or points.

## Troubleshooting

No specific troubleshooting items identified.

## Related Articles

* ES-033 — How do I enable quiz mode?
* ES-016 — How do I view survey results?

## Evidence / Source Traceability

* Frontend: N/A
* Backend: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/tools/RecalculateScoreExecutor.java, src/main/java/com/ec/survey/tools/QuizHelper.java
* Classes: ManagementController, RecalculateScoreExecutor, QuizHelper
* Methods: recalculateScore
* Routes: N/A
* Message keys: label.Recalculate
* Config keys: N/A

## Confidence and Review Status

Medium

## Metadata

* Domain: Quiz
* EUSurvey area: Results
* Feature: Recalculate Score
* User intent: How do I recalculate quiz scores?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Published
* Environment: All
* Keywords: recalculate, quiz, score, update, correct
* Synonyms: update quiz scores, recalculate points
* Acronyms: N/A
* Related entities: AnswerSet, ScoringItem, QuizResult
* Security / privacy relevance: None
* Search boost terms: recalculate quiz scores, update scoring
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/tools/RecalculateScoreExecutor.java, src/main/java/com/ec/survey/tools/QuizHelper.java
* Duplicate status: New
