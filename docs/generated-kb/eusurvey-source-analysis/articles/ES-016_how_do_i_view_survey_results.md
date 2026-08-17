# ES-016 — How do I view survey results?

## Intent / Description

This article explains how to view the submitted contributions and statistics for a survey.

## Applies To

* Role(s): Survey Owner, Results Viewer
* EUSurvey area: Results
* Environment: All
* Article type: How-To
* UI location: Results page
* Backend location: ManagementController.results

## Short Answer

To view results, open the survey in the management area and navigate to the Results page. This page shows all submitted contributions in a table format with filtering and sorting options. You can view individual contributions, see charts and statistics, and access export options.

## Prerequisites / Required Permissions

* The user must have AccessResults privilege or be the survey owner
* The survey must have at least one submitted contribution

## Procedure

1. Open the survey in the management area.
2. Navigate to the Results page.
3. View contributions in the table.
4. Use filters to narrow results (date range, language, specific answers).
5. Click on a contribution to view its details.
6. Access charts and statistics from the results toolbar.
7. Use export options to download data.

## Important Conditions / Limitations

* Maximum 3 filters can be active simultaneously.
* Results can be filtered by date range, language, and answer values.
* Test answers can be excluded from results using the filter.
* Results can be sorted by various columns.
* Individual contributions can be viewed, printed, or deleted.
* Statistics show aggregated data per question.

## Troubleshooting

* 'You used too many search filters': Reduce to maximum 3 active filters.
* No results showing: Verify that contributions have been submitted and filters are not too restrictive.
* 'No Form Loaded' error: Navigate to the survey management area first.

## Related Articles

* ES-017 — How do I export survey results?
* ES-018 — How do I export statistics?
* ES-020 — How do I publish results publicly?
* ES-063 — How do I delete a contribution?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/AnswerService.java
* Classes: ManagementController, AnswerService
* Methods: results, resultsJSON, getAnswers
* Routes: GET /{shortname}/management/results
* Message keys: label.Results, label.Contributions, error.TooManyFilters, info.ResultFilterLimit
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Results
* EUSurvey area: Results Viewer
* Feature: View Results
* User intent: How do I view survey results?
* Article type: How-To
* User type: Survey Owner, Results Viewer
* Required permission: Survey Owner, Results Viewer
* Survey status: Published (current or previously)
* Environment: All
* Keywords: results, contributions, answers, statistics, view, data
* Synonyms: see answers, view contributions, check results, view statistics
* Acronyms: N/A
* Related entities: AnswerSet, Statistics, ResultFilter
* Security / privacy relevance: Results may contain personal data
* Search boost terms: view results, see contributions, check answers
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/AnswerService.java
* Duplicate status: New
