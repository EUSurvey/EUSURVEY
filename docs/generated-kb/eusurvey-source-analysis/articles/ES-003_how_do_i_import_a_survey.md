# ES-003 — How do I import a survey?

## Intent / Description

This article explains how to import a previously exported survey file into EUSurvey.

## Applies To

* Role(s): Registered User
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Forms page
* Backend location: ManagementController.importSurvey

## Short Answer

To import a survey, navigate to the Forms page and click 'Import Survey'. Select a previously exported EUSurvey archive file (.eus format). The system imports the survey structure including all questions, translations, and configuration. The imported survey is created as a new draft.

## Prerequisites / Required Permissions

* The user must be authenticated
* A valid EUSurvey export file (.eus) must be available

## Procedure

1. Navigate to the Forms page.
2. Click the 'Import Survey' button.
3. A dialog appears for file selection.
4. Select the .eus export file from your computer.
5. Click Import to start the process.
6. The system validates and imports the survey.
7. The imported survey appears as a new draft in your survey list.

## Important Conditions / Limitations

* Only EUSurvey export files (.eus format) can be imported.
* The imported survey is always created as a new draft.
* If the file contains unsupported HTML code, the system escapes it and shows a warning.
* Import may fail if the file is corrupted or was exported from an incompatible version.
* Translations included in the export file are imported along with the survey structure.

## Troubleshooting

* **'The file could not be imported' error**: The file may be corrupted or in an unsupported format. Try re-exporting from the source.
* **'EUSurvey found unsupported HTML code' warning**: The import succeeded but some HTML was escaped. Review the survey for escaped characters.

## Related Articles

* ES-001 — How do I create a new survey?
* ES-002 — How do I copy an existing survey?
* ES-004 — How do I export a survey structure?

## Evidence / Source Traceability

* Frontend files:
* src/main/webapp/WEB-INF/views/import-survey-dialog.jsp
* Backend files:
* src/main/java/com/ec/survey/controller/ManagementController.java
* src/main/java/com/ec/survey/tools/SurveyExportHelper.java
* Classes: ManagementController, SurveyExportHelper
* Methods: importSurvey
* Routes: /{shortname}/management
* Message keys: error.FileImportFailed, error.ProblemDuringImport, info.importsuccessful, info.invalidCodeFound, label.ImportSurvey
* Configuration keys: N/A

## Confidence and Review Status

High — behaviour is directly visible in UI and backend code.

## Metadata

* Domain: Survey Management
* EUSurvey area: Survey Creation
* Feature: Import Survey
* User intent: How do I import a survey?
* Article type: How-To
* User type: Registered User
* Required permission: Registered User
* Survey status: N/A (creates new)
* Environment: All
* Keywords: import, upload, restore, file, survey
* Synonyms: upload survey, load survey file, restore survey
* Acronyms: N/A
* Related entities: Survey, ImportResult
* Security / privacy relevance: None
* Search boost terms: import survey, upload survey file, import eus file
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/tools/SurveyExportHelper.java
* Duplicate status: New
