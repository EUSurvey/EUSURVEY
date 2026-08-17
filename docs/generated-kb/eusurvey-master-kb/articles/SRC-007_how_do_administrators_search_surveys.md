# SRC-007 — How do administrators search surveys?

## Intent / Description

This article explains how administrators search and manage all surveys system-wide.

## Applies To

* Role(s): Administrator
* EUSurvey area: Administration
* Environment: All
* Article type: How-To
* UI location: Administration > Surveys
* Backend location: SurveySearchController

## Short Answer

Administrators can search all surveys in the system from Administration > Surveys. This includes published, unpublished, deleted, archived, frozen, and reported surveys. Administrators can perform actions like freeze, restore, delete permanently, and change ownership.

## Prerequisites / Required Permissions

* RightManagement global privilege

## Procedure

1. Navigate to Administration > Surveys.
2. Use the search form to filter surveys by title, shortname, owner, status, dates, etc.
3. View survey details in the results table.
4. Perform administrative actions: freeze, unfreeze, restore deleted, permanently delete, change owner, export.

## Important Conditions / Limitations

* Administrators can see all surveys regardless of ownership.
* Surveys can be filtered by multiple criteria: title, UID, owner, dates, status, language, type.
* Different tabs show: all surveys, deleted, archived, frozen, reported.
* Administrators can export survey statistics in XLSX or DOCX format.
* Administrators can change survey ownership.
* Administrators can permanently delete soft-deleted surveys.

## Troubleshooting

* No surveys found: Adjust search criteria or check that the survey exists.

## Related Articles

* ES-043 — How do administrators freeze a survey?
* ES-041 — How do administrators manage users?
* ES-045 — How do administrators ban a user?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/administration/surveysearch.jsp
* Backend files: src/main/java/com/ec/survey/controller/SurveySearchController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: SurveySearchController, SurveyService
* Methods: surveysearch, surveysearchPOST, resultsJSON
* Routes: GET /administration/surveysearch, POST /administration/surveysearch
* Message keys: label.SurveySearch, label.DeletedSurveys, label.ArchivedSurveys, label.FrozenSurveys, label.ReportedSurveys
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Administration
* EUSurvey area: Survey Search
* Feature: Admin Survey Search
* User intent: How do administrators search surveys?
* Article type: How-To
* User type: Administrator
* Required permission: Administrator
* Survey status: N/A
* Environment: All
* Keywords: admin, search, surveys, manage, all
* Synonyms: search all surveys, admin survey management, find surveys
* Acronyms: N/A
* Related entities: Survey, SurveyFilter
* Security / privacy relevance: Admin access to all survey data
* Search boost terms: admin survey search, find all surveys
* Source files: src/main/java/com/ec/survey/controller/SurveySearchController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
