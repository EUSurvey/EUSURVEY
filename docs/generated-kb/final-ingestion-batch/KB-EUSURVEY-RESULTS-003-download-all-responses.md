# How to download all EUSurvey responses

## Intent / Description

Explains how a survey owner can export and download all submitted responses from a survey.

## Applies To

* Role(s): Survey Manager
* Feature: Results export
* Context: A survey owner wants to download all contributions in a file

## Short Answer

Survey owners can export all submitted contributions from the Results page. The export is generated asynchronously (in the background) and can be downloaded once ready. Supported formats include XLS, XLSX, CSV, ODS, and PDF.

## Steps / Procedure

1. Open your survey and go to the **Results** page.
2. Ensure no filters are active if you want all contributions (clear any active filters).
3. Click the **Export** button.
4. Select the desired **format** (XLS, XLSX, CSV, ODS, or PDF).
5. Configure export options if available (e.g. include metadata, include file uploads).
6. Start the export. The system generates the file in the background.
7. Once ready, download the export file from the **Exports** section or wait for the notification.

**To check export status:**

1. Navigate to the **Exports** section (accessible from the survey's management area).
2. Your export will show as "Pending", "Running", or "Finished".
3. Once finished, click to download the file.

## Important Conditions / Limitations

* **Asynchronous generation**: Large exports are generated in the background. You do not need to stay on the page.
* **Reporting database delay**: Exports use a reporting database that synchronises periodically (up to 12 hours). Very recent submissions may not be included. For the most current data, ensure you wait for synchronisation.
* **Filters affect scope**: If filters are active on the Results page when you start the export, only the filtered subset is exported.
* **Draft contributions**: Standard exports include only submitted contributions, not drafts.
* **File upload attachments**: If the survey includes file upload questions, a separate "Files" export may be needed to download the uploaded files.
* **Large exports**: Surveys with thousands of contributions may take significant time to generate.
* **Export expiry**: Generated export files may be deleted after a period. Download them promptly.

## Troubleshooting / Related Cases

* If the export does not include recent responses: wait for the reporting database to synchronise (up to 12 hours), then generate a fresh export.
* If the export shows fewer rows than expected: check for active filters.
* If the export fails: see KB-EUSURVEY-RESULTS-005.
* If the export takes too long: for very large datasets, try exporting in CSV format which is typically fastest.

## Out of Scope / Separate Topics

* Why an export does not include latest responses (see: KB-EUSURVEY-RESULTS-002)
* What to do when an export fails (see: KB-EUSURVEY-RESULTS-005)
* How to filter contributions (see: KB-EUSURVEY-RESULTS-004)
* How to retrieve uploaded files (see: SM-88)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: results_exports
* user_role: survey_manager
* feature: results_export
* tags: download all responses, export results, download contributions, export survey data
* synonyms: how to get all answers, download survey results, export all submissions, get contributions file
* product_terms: Results, Export, XLS, XLSX, CSV, ODS, PDF, Exports section
* exclude: export errors, report timeouts, statistics, published results
