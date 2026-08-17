# Why does my export not include the latest responses?

## Intent / Description

Explains why an export of survey results may not contain the most recent contributions.

## Applies To

* Role(s): Survey Manager
* Feature: Results Export
* Context: Export file appears to be missing recent submissions

## Short Answer

EUSurvey uses a separate reporting database for result exports that synchronizes periodically (up to 12 hours). If you just received new submissions, they may not yet be available in exports. Additionally, the export may be filtered, excluding some contributions.

Common causes:

1. **Synchronization delay**: The reporting database updates periodically. New submissions may take up to 12 hours to appear in exports.
2. **Active result filter**: You may have an active filter on the Results page that excludes recent contributions from the export scope.
3. **Draft contributions**: Drafts (incomplete submissions) are not included in standard exports unless you specifically export drafts.
4. **Export was prepared earlier**: If you prepared the export file at an earlier time, it contains only the data available at that moment. Generate a new export for up-to-date data.
5. **Apply Changes**: If you recently applied structural changes to the survey, the export system may need to re-index answers.

## Steps / Procedure

1. **Wait**: If submissions arrived within the last 12 hours, wait for the synchronization cycle.
2. **Clear filters**: On the Results page, ensure no filter is active before exporting.
3. **Generate a fresh export**: Do not reuse a previously generated export file. Click "Export" again to generate a new one with current data.
4. **Check draft vs submitted**: Verify whether the missing contributions are still in draft status (not yet submitted by the respondent).
5. **Check contribution count**: Compare the number shown on the Results page with the number of rows in your export.

## Important Conditions / Limitations

* The maximum synchronization delay is approximately 12 hours.
* If data is older than 12 hours and still not in exports, contact EUSurvey support.
* Exports include only submitted contributions by default. Draft contributions require a separate export.
* Large exports (thousands of contributions) may take time to prepare — the system generates them asynchronously.
* PDF exports show data as of the generation moment. If you need current data, regenerate.

## Troubleshooting / Related Cases

* If the export contains old data consistently: check the "last updated" indicator on the Results page.
* If the count matches but specific answers are empty: the questions may have been added after those contributions were submitted (respondents did not answer the new questions).
* If the export fails or shows an error: see "What causes a technical problem during export".

## Out of Scope / Separate Topics

* Why do Contributions and Results show different numbers (see: KB-EUSURVEY-RESULTS-001)
* How do I download all responses (see: How can I download submitted contributions)
* Why are charts not updated (same synchronization issue)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* If the delay exceeds 12 hours, open a support ticket.

## Retrieval Metadata

* business_domain: results_exports
* user_role: survey_manager
* feature: export, results_synchronization
* tags: export missing data, latest responses not in export, synchronization delay, outdated export, stale results
* synonyms: export not up to date, missing recent submissions in export, export file incomplete, why export does not have latest answers, results export outdated
* product_terms: Export, Results, synchronization, reporting database, PDF export
* exclude: export error messages, report timeouts, slow charts, Results page performance, missing contributions on Results page, general platform slowness
