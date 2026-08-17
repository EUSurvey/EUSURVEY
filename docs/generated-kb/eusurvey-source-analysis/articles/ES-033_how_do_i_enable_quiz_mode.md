# ES-033 — How do I enable quiz mode?

## Intent / Description

This article explains how to enable quiz mode for a survey to score respondent answers.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Quiz
* Environment: All
* Article type: How-To
* UI location: Properties page
* Backend location: ManagementController.propertiesPost

## Short Answer

To enable quiz mode, navigate to survey Properties and activate 'Enable Quiz Functionality'. This allows you to assign correct answers and point values to questions. After submission, respondents can see their score and which answers were correct.

## Prerequisites / Required Permissions

* Survey owner

## Procedure

1. Open the survey Properties page.
2. Find the Quiz section.
3. Enable 'Quiz Functionality'.
4. Save properties.
5. In the Editor, configure scoring for each question.
6. Set correct answers and point values.
7. Optionally configure quiz welcome message and results message.
8. Optionally enable score display and quiz icons.

## Important Conditions / Limitations

* Quiz mode allows assigning scores to possible answers.
* Scoring can be per-answer (individual points) or per-question (whole question correct/incorrect).
* A time limit can be set for the quiz.
* Quiz results page shows the score after submission.
* Scores by question can be displayed.
* Quiz icons show correct/incorrect indicators.
* Negative scoring can be enabled or disabled per question.
* Custom feedback messages can be shown based on score ranges.
* Section-level scores are tracked.

## Troubleshooting

* Quiz options not visible: Ensure quiz mode is enabled in properties.
* Scores not displaying: Enable 'Show Total Score' in quiz properties.

## Related Articles

* ES-064 — How do I recalculate quiz scores?
* ES-007 — How do I configure survey properties?
* ES-006 — How do I edit a survey?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java, src/main/java/com/ec/survey/tools/QuizHelper.java, src/main/java/com/ec/survey/model/survey/ScoringItem.java
* Classes: ManagementController, Survey, QuizHelper, ScoringItem
* Methods: propertiesPost, getQuizResult
* Routes: POST /{shortname}/management/properties
* Message keys: label.EnableQuiz, label.Quiz, label.QuizResults, label.ShowScore, label.ShowQuizIcons, label.Score, label.noNegativeScore, info.Quiz, info.ShowTotalScoreNew, info.SetATimeLimit, info.Feedback, info.ForEachAnswer, info.ForWholeQuestion
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Quiz
* EUSurvey area: Survey Properties
* Feature: Enable Quiz
* User intent: How do I enable quiz mode?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: quiz, score, correct, answers, test, points, grading
* Synonyms: enable quiz, create test, add scoring, grade answers
* Acronyms: N/A
* Related entities: Survey, ScoringItem, QuizResult
* Security / privacy relevance: None
* Search boost terms: enable quiz, scoring, test mode, grade answers
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java, src/main/java/com/ec/survey/tools/QuizHelper.java, src/main/java/com/ec/survey/model/survey/ScoringItem.java
* Duplicate status: New
