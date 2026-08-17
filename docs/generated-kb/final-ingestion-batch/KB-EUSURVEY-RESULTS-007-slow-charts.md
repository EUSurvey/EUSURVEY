# Why EUSurvey charts and statistics may take time to appear

## Intent / Description

Explains why charts and statistics on the Results page may load slowly or appear outdated.

## Applies To

* Role(s): Survey Manager
* Feature: Charts, Statistics
* Context: A survey owner opens the statistics/charts view and it takes a long time to load, or shows data that seems outdated

## Short Answer

Charts and statistics may take time to appear because:

1. **On-demand computation**: Statistics are computed when you request them. For surveys with many contributions, this takes time.
2. **Reporting database synchronisation**: Charts use data from a reporting database that synchronises periodically (up to 12 hours). Very recent contributions may not yet be reflected.
3. **Complex questions**: Questions with many possible answers (large matrices, rankings) require more processing.
4. **Platform load**: During peak usage, statistics generation competes with other operations.

This is different from export generation (which runs in the background) — statistics are typically computed while you wait.

## Steps / Procedure

1. **Wait for loading**: Give the statistics view time to render. Complex surveys may take 10–30 seconds.
2. **Refresh the page**: If charts appear stuck, reload the page to trigger a fresh computation.
3. **Check for recent contributions**: If data appears outdated, recent submissions may not have synchronised to the reporting database yet. Wait up to 12 hours for full synchronisation.
4. **Use filters**: Reducing the dataset with date filters may speed up chart generation.
5. **View one section at a time**: If available, viewing statistics per question or section is faster than all at once.

## Important Conditions / Limitations

* **Not real-time**: Statistics reflect the state of the reporting database, not necessarily the latest second. Delay of up to 12 hours is normal.
* **Differs from the contribution count**: The Results page may show a contribution count that is higher than what statistics report, because the reporting database has not yet processed the newest submissions.
* **Caching**: Once computed, statistics may be cached briefly. A second viewing may be faster.
* **No automatic refresh**: Charts do not auto-update while you view them. Reload the page to see newer data.

## Troubleshooting / Related Cases

* If charts never load and time out: see KB-EUSURVEY-RESULTS-006 for report timeout guidance.
* If charts show different numbers than the Results list: this is the synchronisation delay. Wait for the reporting database to update.
* If specific charts fail but others work: the problematic question may have too many answer variations.

## Out of Scope / Separate Topics

* What to do when generating a report times out (see: KB-EUSURVEY-RESULTS-006)
* Why an export does not include latest responses (see: KB-EUSURVEY-RESULTS-002)
* How to download all responses (see: KB-EUSURVEY-RESULTS-003)
* General service slowdown (see: KB-EUSURVEY-TECH-009)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: results_exports
* user_role: survey_manager
* feature: charts_statistics
* tags: slow charts, statistics loading slowly, charts outdated, statistics delay
* synonyms: charts take long to load, statistics not up to date, slow statistics page, graphs not showing, charts not loading
* product_terms: Statistics, Charts, Results, reporting database, synchronisation
* exclude: export errors, export timeout, contribution synchronisation, general platform slowness
