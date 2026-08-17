# ES-058 — How do I limit the number of contributions?

## Intent / Description

This article explains how to set a maximum number of contributions for a survey.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Properties page
* Backend location: ManagementController.java

## Short Answer

Set a maximum total contributions in Properties. Once reached, new respondents see a custom message or redirect. Configure the limit value and the message displayed when capacity is full.

## Prerequisites / Required Permissions

* Survey owner

## Procedure

1. Open survey Properties.
2. Enable maximum number of contributions.
3. Enter the maximum number.
4. Configure the message or link for when limit is reached.
5. Save.

## Important Conditions / Limitations

* Applies to total contributions across all respondents.
* Custom message or redirect link configurable.
* Limit checked on each submission attempt.
* Per-user limits are a separate setting (allowedContributionsPerUser).

## Troubleshooting

* Respondents blocked: Check if test submissions count toward limit.

## Related Articles

* ES-012 — How do respondents submit a contribution?
* ES-007 — How do I configure survey properties?

## Evidence / Source Traceability

* Frontend: N/A
* Backend: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/tools/SurveyHelper.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Classes: ManagementController, SurveyHelper, Survey
* Methods: propertiesPost, isMaxContributionReached
* Routes: N/A
* Message keys: label.MaxNumberContributions, info.MaxNumberContributions, label.MaxNumberContributionText, info.MaxNumberContributionText, label.ContributionsPerUser, info.ContributionsPerUser
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Management
* EUSurvey area: Limits
* Feature: Max Contributions
* User intent: How do I limit the number of contributions?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: limit, maximum, contributions, cap, restrict
* Synonyms: limit responses, cap submissions, maximum answers
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: limit contributions, maximum submissions, cap responses
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/tools/SurveyHelper.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Duplicate status: New
