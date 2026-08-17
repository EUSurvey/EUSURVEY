# ES-017 — How do I export survey results?

## Intent / Description

This article explains how a survey owner or results viewer exports contributions (results) from a survey.

## Applies To

* Role(s): Survey Owner, Results Viewer (AccessResults privilege)
* EUSurvey area: Results and Export
* Environment: All
* Article type: How-To
* UI location: Exports page (`/exports/list`)
* Backend location: ExportsController.startExport()

## Short Answer

To export survey results, navigate to the Exports page, select the export type (Content, Statistics, or Uploaded Files), choose a format (XLS, XLSX, ODS, CSV, XML, PDF, or ZIP for files), optionally apply filters, and click Start. The export runs asynchronously. When complete, a download link appears on the Exports page and an email notification is sent.

## Prerequisites / Required Permissions

* The user must have `AccessResults` privilege (read or read/write) on the survey, or be the survey owner.
* The survey must have been published at least once (to have a published structure for export).
* The survey must have at least one contribution to export results.
* A survey must be loaded in the session (via the management interface).

## Procedure

1. Open the survey in the management area.
2. Navigate to the "Exports" page.
3. Select the export type:
   - **Export Content**: Export all individual contributions as a data file.
   - **Export Statistics**: Export aggregated statistics.
   - **Export Uploaded Elements**: Download all files uploaded by respondents.
   - **Export Activities**: Export activity log.
   - **Export Tokens**: Export token/invitation list.
   - **Export Address Book**: Export contacts.
4. Select the output format (XLS, XLSX, ODS, CSV, XML, PDF depending on type).
5. Optionally apply result filters (date range, language, specific answers).
6. Optionally give the export a name for identification.
7. Click "Start" to begin the export.
8. The export runs asynchronously in the background.
9. Check the Exports page for progress. When finished, a download link appears.
10. Alternatively, wait for the notification email with the download link.

## Important Conditions / Limitations

* Exports run asynchronously and may take time for large datasets.
* Exports are automatically deleted after 1 month (message: "Exports will be deleted automatically after 1 month").
* Export timeout is configurable via `export.timeout` property (default: 5 minutes).
* Old exports are cleaned up by `deleteOldExports` scheduled task (configurable via `export.deleteexportstimeout`, default 30 days).
* The export format options vary by type:
  - Content: XLS, XLSX, ODS, CSV, XML
  - Statistics: XLS, XLSX, ODS, PDF (DOCX)
  - Uploaded files: ZIP
* Result filters can limit exported data to specific date ranges, languages, or answer values.
* Maximum 3 filters can be active simultaneously (message: "For performance reasons you can only set a maximum of 3 filters").
* An existing export that is up-to-date shows "Existing export is up-to-date" and can be re-downloaded without regeneration.
* The "Split MCQ answers per columns" option creates separate columns for each multiple-choice answer.

## Troubleshooting

* **Export stays in "Pending" or "Started" state**: The export may be processing a large dataset. Wait for the timeout period. If it persists, contact support.
* **"No Form Loaded" error**: Navigate to the survey management area first to load the survey in your session.
* **Export file is empty**: Check that contributions exist and that your filter settings are not too restrictive.
* **Export link expired**: Exports are deleted after 1 month. Start a new export.
* **"You used too many search filters" error**: Reduce active filters to maximum 3.

## Related Articles

* ES-016 — How do I view results?
* ES-018 — How do I export statistics?
* ES-019 — How do I export uploaded files?
* ES-020 — How do I publish results?

## Evidence / Source Traceability

* Backend: `src/main/java/com/ec/survey/controller/ExportsController.java` — method `startExport()` (line ~45)
* Backend: `src/main/java/com/ec/survey/service/ExportService.java` — methods `prepareExport()`, `startExport()`
* Backend: `src/main/java/com/ec/survey/tools/export/` — all export creator classes (XlsxExportCreator, OdfExportCreator, CsvExportCreator, XmlExportCreator, etc.)
* Frontend: `src/main/webapp/WEB-INF/views/exports/`
* Message keys: `info.ExportsDeletedAutomatically1`, `info.ExportUpToDate`, `error.TooManyFilters`, `info.UpdateExport`
* Configuration: `export.timeout`, `export.deleteexportstimeout`, `export.fileDir`
* Enums: `Export.ExportFormat` (xls, xlsx, ods, csv, xml, pdf, doc), `Export.ExportType` (Content, Statistics, StatisticsQuiz, AddressBook, Activity, Tokens, Files, PDFReport)
* Route: POST `/exports/start/{type}/{format}`

## Confidence and Review Status

High — behaviour is directly visible in UI and backend code.

## Metadata

* Domain: Results and Export
* EUSurvey area: Exports
* Feature: Export Results
* User intent: How do I export survey results?
* Article type: How-To
* User type: Survey Owner, Results Viewer
* Required permission: AccessResults
* Survey status: Published (current or previously)
* Environment: All
* Keywords: export, download, results, contributions, XLS, CSV, XML, ODS, data
* Synonyms: download results, get data, extract answers, export contributions, save results
* Acronyms: XLS, XLSX, ODS, CSV, XML, PDF
* Related entities: Export, AnswerSet, Survey
* Security / privacy relevance: Exported data may contain personal information depending on survey design
* Search boost terms: export results, download contributions, export data, get answers
* Source files: ExportsController.java, ExportService.java, CommonExcelExportCreator.java
* Duplicate status: New
