# Why do Contributions and Results show different numbers in EUSurvey?

## Intent / Description

Explains why the contribution count may differ between the Contributions page and the Results/Statistics page.

## Applies To

* Role(s): Survey Manager
* Feature: Results, Contributions, Statistics
* Context: Survey manager notices a discrepancy between numbers shown in different views

## Short Answer

The **Contributions** page and the **Results/Statistics** page can show different numbers for several reasons:

1. **Synchronization delay**: Results use a separate reporting database that updates periodically (up to 12 hours delay). New submissions may not yet appear in the Results view.
2. **Draft contributions**: The Contributions page may show both submitted and draft (incomplete) contributions. The Results statistics typically count only submitted contributions.
3. **Deleted contributions**: If contributions were deleted, the Contributions count decreases immediately, but statistics may take time to update.
4. **Survey version differences**: After applying changes (new published version), contributions are re-associated. During this transition, counts may temporarily differ.
5. **Active filters**: The Results page may have active filters applied that limit the displayed count.

## Steps / Procedure

1. Check whether you have active filters on the Results page — clear all filters and compare.
2. Verify whether the Results page shows "last updated" time — if it's more than a few minutes old, the data may not yet reflect recent submissions.
3. Check if there are draft contributions in the Contributions view — drafts are not counted as submitted.
4. If the discrepancy persists for more than 12 hours, contact EUSurvey support.

## Important Conditions / Limitations

* The Results reporting database has a maximum synchronization delay of approximately 12 hours.
* Drafts (incomplete contributions) are never included in the submitted contribution statistics.
* Deleted contributions are recorded in a separate audit table but are excluded from all counts.
* If you just applied changes to the survey, there may be a brief period where counts are being recalculated.
* The number on the Forms/survey list page shows the latest submitted count based on the published survey version.

## Troubleshooting / Related Cases

* If the number mismatch is exactly equal to your number of draft contributions, the difference is normal — drafts are excluded from statistics.
* If you recently deleted contributions, the statistics will update after the next synchronization cycle.
* If the discrepancy is large and persistent (>12 hours), contact support.

## Out of Scope / Separate Topics

* Why are my results not up-to-date (related but covers general staleness)
* How do I download all responses (export-related)
* How can I extract draft answers (separate feature)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* If the issue persists beyond 12 hours, open a support ticket.

## Retrieval Metadata

* business_domain: results_analysis
* user_role: survey_manager
* feature: results, contributions, statistics
* tags: count mismatch, contributions vs results, different numbers, synchronization, delay
* synonyms: why numbers differ between contributions and results, contribution count wrong, results not matching, discrepancy contributions results, statistics not updated
* product_terms: Contributions, Results, Statistics, synchronization, draft, submitted
