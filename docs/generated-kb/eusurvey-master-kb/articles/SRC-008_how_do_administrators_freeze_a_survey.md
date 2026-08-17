# SRC-008 — How do administrators freeze a survey?

## Intent / Description

This article explains how administrators freeze (block) a survey that violates policies.

## Applies To

* Role(s): Administrator
* EUSurvey area: Administration
* Environment: All
* Article type: How-To
* UI location: Administration > Surveys
* Backend location: SurveySearchController.freezesurvey

## Short Answer

To freeze a survey, navigate to Administration > Surveys, find the offending survey, and click 'Freeze'. Freezing blocks the survey from being accessed by respondents and prevents the owner from making changes. An automatic email notification is sent to the survey owner.

## Prerequisites / Required Permissions

* RightManagement global privilege

## Procedure

1. Navigate to Administration > Surveys.
2. Find the survey to freeze.
3. Click 'Freeze' button.
4. Confirm the freeze action.
5. An email is automatically sent to the survey owner explaining the action.

## Important Conditions / Limitations

* Freezing blocks respondent access - the survey shows a 'frozen' error page.
* The survey owner cannot edit, publish, or manage the frozen survey.
* An automatic notification email is sent to the survey owner.
* Frozen surveys can be unfrozen by administrators.
* The survey data is preserved during the freeze.
* Freezing is used for policy violations (abuse, inappropriate content).

## Troubleshooting

* Survey owner contacting support: They need to address the policy violation. Admin can unfreeze when resolved.

## Related Articles

* ES-042 — How do administrators search surveys?
* ES-045 — How do administrators ban a user?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/administration/surveysearch.jsp
* Backend files: src/main/java/com/ec/survey/controller/SurveySearchController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: SurveySearchController, SurveyService
* Methods: freezesurvey, unfreezesurvey, freeze, unfreeze
* Routes: POST /administration/freezesurvey, POST /administration/unfreezesurvey
* Message keys: label.Freeze, label.FreezeSurvey, info.freeze, info.freezeemail, info.SurveyFrozen, info.SurveyUnfrozen, error.FrozenSurvey, label.confirmfreeze
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Administration
* EUSurvey area: Survey Moderation
* Feature: Freeze Survey
* User intent: How do administrators freeze a survey?
* Article type: How-To
* User type: Administrator
* Required permission: Administrator
* Survey status: Published
* Environment: All
* Keywords: freeze, block, suspend, policy, violation
* Synonyms: block survey, suspend survey, freeze for violation
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: Moderation action affecting survey access
* Search boost terms: freeze survey, block survey, suspend access
* Source files: src/main/java/com/ec/survey/controller/SurveySearchController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
