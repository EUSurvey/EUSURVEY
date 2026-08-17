# What to do when an EUSurvey results export fails

## Intent / Description

Explains what to do when the export of survey results produces an error or does not complete.

## Applies To

* Role(s): Survey Manager
* Feature: Results export
* Context: An export attempt fails, shows an error, or remains stuck

## Short Answer

If a results export fails, it may be due to the dataset size, a temporary server issue, or a problem with specific contribution data. Steps to resolve include retrying, reducing the export scope, or trying a different format.

## Steps / Procedure

1. **Check export status**: Go to the Exports section and check the status of your export. It may show "Error" or remain in "Running" state indefinitely.
2. **Retry**: Delete the failed export and start a new one. Transient server issues are often resolved by retrying.
3. **Try a different format**: If XLSX fails, try CSV. CSV exports are simpler and less likely to encounter formatting issues.
4. **Reduce the scope**: Apply date filters to export a subset of contributions. If a smaller export succeeds, the issue may be related to data volume.
5. **Check for problematic data**: Contributions with very large file uploads or unusual characters may cause issues in certain export formats.
6. **Wait and retry later**: If the platform is under heavy load, try again during off-peak hours.
7. **Contact support**: If repeated attempts fail across formats and time periods, report the issue with:
   - The survey shortname
   - The export format attempted
   - The approximate number of contributions
   - Any error message shown

## Important Conditions / Limitations

* **Asynchronous processing**: Exports run in the background. A failed export does not block other operations.
* **Timeout for large exports**: Very large exports may time out. Reducing the dataset size helps.
* **Format-specific issues**: Some export formats (PDF reports, for instance) require more server resources than simple tabular exports (CSV).
* **File size limits**: Extremely large exports may exceed file size handling limits.
* **Export expiry**: Completed exports are stored temporarily. If you don't download in time, you need to regenerate.
* **Reporting database**: Exports use the reporting database. If the OLAP table for your survey is being rebuilt, exports may temporarily fail.

## Troubleshooting / Related Cases

* If the export stays in "Running" forever: it likely encountered an issue. Delete it and retry.
* If CSV works but XLSX doesn't: the issue is format-specific. Use CSV as a workaround.
* If all formats fail: the survey's reporting data may need to be rebuilt. Contact support.
* If the export completes but is empty: check whether filters were active or whether the reporting database has synchronised.

## Out of Scope / Separate Topics

* Why an export does not include latest responses (see: KB-EUSURVEY-RESULTS-002)
* How to download all responses (see: KB-EUSURVEY-RESULTS-003)
* What to do when generating a report times out (see: KB-EUSURVEY-RESULTS-006)
* How to unzip export files (see: SM-84)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: results_exports
* user_role: survey_manager
* feature: results_export
* tags: export error, export fails, export not working, export timeout, cannot export results
* synonyms: results export broken, export stuck, error downloading results, export fails with error, cannot generate export
* product_terms: Export, Exports section, CSV, XLSX, XLS, ODS, PDF, reporting database
* exclude: stale export data, slow charts, report timeouts, contribution not visible
