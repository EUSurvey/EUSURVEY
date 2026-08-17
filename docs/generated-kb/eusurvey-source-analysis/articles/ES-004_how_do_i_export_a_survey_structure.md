# ES-004 — How do I export a survey structure?

## Intent / Description

This article explains how to export a survey's structure as a file for backup or transfer.

## Applies To

* Role(s): Survey Owner, Form Manager
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Overview page
* Backend location: ManagementController.exportSurvey

## Short Answer

To export a survey structure, navigate to the survey Overview page and select 'Export Survey'. The system creates a .eus archive file containing the survey structure, questions, settings, and optionally answers. This file can be imported later to recreate the survey.

## Prerequisites / Required Permissions

* The user must be the survey owner or have FormManagement privilege
* The survey must be loaded in the session

## Procedure

1. Open the survey in the management area.
2. Navigate to the Overview page.
3. Click 'Export Survey'.
4. Optionally select 'Export with answers' to include contributions.
5. The system generates the .eus export file.
6. Download the file when ready.

## Important Conditions / Limitations

* The export creates a proprietary .eus archive format.
* The export includes survey structure, questions, properties, and translations.
* Including answers makes the export file significantly larger.
* Export files can be used for backup purposes or to transfer surveys between EUSurvey instances.
* The export does not include generated statistics or PDF files.

## Troubleshooting

* **Export file too large**: Exclude answers to reduce file size.
* **Export fails**: Check that the survey is properly loaded in your session.

## Related Articles

* ES-003 — How do I import a survey?
* ES-002 — How do I copy an existing survey?
* ES-017 — How do I export survey results?

## Evidence / Source Traceability

* Frontend files:
* N/A
* Backend files:
* src/main/java/com/ec/survey/controller/ManagementController.java
* src/main/java/com/ec/survey/tools/SurveyExportHelper.java
* src/main/java/com/ec/survey/service/SurveyService.java
* Classes: ManagementController, SurveyExportHelper, SurveyService
* Methods: exportSurvey, addSurveyData
* Routes: /{shortname}/management/overview
* Message keys: label.ExportWithAnswers
* Configuration keys: N/A

## Confidence and Review Status

High — behaviour is directly visible in UI and backend code.

## Metadata

* Domain: Survey Management
* EUSurvey area: Survey Structure
* Feature: Export Survey
* User intent: How do I export a survey structure?
* Article type: How-To
* User type: Survey Owner, Form Manager
* Required permission: Survey Owner, Form Manager
* Survey status: Any
* Environment: All
* Keywords: export, download, backup, structure, archive, eus
* Synonyms: download survey, backup survey, save survey file
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: Export with answers may contain personal data
* Search boost terms: export survey structure, download survey backup, save survey as file
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/tools/SurveyExportHelper.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
