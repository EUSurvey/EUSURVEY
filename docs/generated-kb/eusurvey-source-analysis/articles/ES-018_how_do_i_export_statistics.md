# ES-018 — How do I export statistics?

## Intent / Description

This article explains how to export aggregated statistics from survey results.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Results and Export
* Environment: All
* Article type: How-To
* UI location: Exports page
* Backend location: ExportsController.startExport

## Short Answer

To export statistics, navigate to the Exports page and select 'Export Statistics'. Choose a format (XLS, XLSX, ODS, or PDF/DOCX). The export generates aggregated data showing response distributions, percentages, and charts for each question.

## Prerequisites / Required Permissions

* AccessResults privilege or survey ownership
* Survey must have contributions

## Procedure

1. Navigate to the Exports page.
2. Select 'Export Statistics' type.
3. Choose the output format.
4. Click Start.
5. Wait for the asynchronous export to complete.
6. Download the file.

## Important Conditions / Limitations

* Statistics exports show aggregated response counts and percentages per question.
* Available formats: XLS, XLSX, ODS, PDF (DOCX).
* For quiz surveys, a separate 'Export Statistics Quiz' type is available with scoring data.
* The export runs asynchronously.
* Charts can be included in the export.

## Troubleshooting

* Export not completing: Large surveys may take longer. Wait for the timeout period.

## Related Articles

* ES-017 — How do I export survey results?
* ES-016 — How do I view survey results?
* ES-020 — How do I publish results publicly?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/exports/
* Backend files: src/main/java/com/ec/survey/controller/ExportsController.java, src/main/java/com/ec/survey/tools/export/StatisticsCreator.java
* Classes: ExportsController, StatisticsCreator
* Methods: startExport, exportStatistics
* Routes: POST /exports/start/Statistics/{format}
* Message keys: label.ExportStatistics, label.ExportCharts
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Results and Export
* EUSurvey area: Exports
* Feature: Export Statistics
* User intent: How do I export statistics?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Published
* Environment: All
* Keywords: statistics, charts, aggregated, export, percentages
* Synonyms: download statistics, export charts, get aggregated data
* Acronyms: N/A
* Related entities: Export, Statistics
* Security / privacy relevance: None
* Search boost terms: export statistics, download charts, aggregated results
* Source files: src/main/java/com/ec/survey/controller/ExportsController.java, src/main/java/com/ec/survey/tools/export/StatisticsCreator.java
* Duplicate status: New
