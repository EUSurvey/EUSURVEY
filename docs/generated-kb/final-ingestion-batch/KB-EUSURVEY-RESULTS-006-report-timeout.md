# What to do when generating an EUSurvey report times out

## Intent / Description

Explains what to do when generating a statistics or charts report takes too long and times out.

## Applies To

* Role(s): Survey Manager
* Feature: Statistics, Charts, Reports
* Context: A report or statistics generation request does not complete or shows a timeout error

## Short Answer

Report and statistics generation may time out for surveys with a very large number of contributions or complex question structures. When this happens, try reducing the dataset scope, using filters, or exporting raw data instead.

## Steps / Procedure

1. **Wait and retry**: If the report timed out, try again. The reporting database may have been busy.
2. **Apply date filters**: Reduce the number of contributions being analysed by filtering to a specific date range.
3. **Use question filters**: If available, generate statistics for a subset of questions rather than all at once.
4. **Export raw data instead**: If statistics generation consistently fails, export the raw data (CSV/XLSX) and analyse it externally using spreadsheet software or statistical tools.
5. **Try during off-peak hours**: Platform load may affect report generation time.
6. **Check contribution count**: Surveys with thousands of contributions naturally take longer to process. This is expected behaviour for very large datasets.
7. **Contact support**: If the report consistently times out even with filters, report the issue.

## Important Conditions / Limitations

* **Statistics are computed on demand**: Charts and statistics are generated when requested, using the reporting database.
* **Large datasets take longer**: There is no guaranteed maximum generation time. Processing scales with the number of contributions and questions.
* **No background report generation**: Unlike exports, statistics are typically generated synchronously (you wait for the result).
* **Reporting database dependency**: Statistics rely on the reporting database being up to date and available.
* **Complex questions**: Matrix questions, ranking questions, and complex tables produce more data to process per contribution.

## Troubleshooting / Related Cases

* If charts load for some questions but not others: the problematic questions may have more complex data. Try viewing statistics one section at a time.
* If the timeout is recent (previously worked): the number of contributions may have grown to a point where generation takes longer. Use filters.
* If export works but statistics don't: this confirms the data exists but the computation is too intensive for the statistics interface.

## Out of Scope / Separate Topics

* Why EUSurvey charts take time to appear (see: KB-EUSURVEY-RESULTS-007)
* What to do when an export fails (see: KB-EUSURVEY-RESULTS-005)
* How to download all responses (see: KB-EUSURVEY-RESULTS-003)
* General service slowdown (see: KB-EUSURVEY-TECH-009)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: results_exports
* user_role: survey_manager
* feature: statistics_generation
* tags: report timeout, statistics timeout, report takes too long, charts not loading, generation timeout
* synonyms: statistics generation timed out, report did not finish, charts timeout, cannot generate report, statistics too slow
* product_terms: Results, Statistics, Charts, filter, Export, reporting database
* exclude: export errors, contribution synchronisation, slow page load, general platform slowness
