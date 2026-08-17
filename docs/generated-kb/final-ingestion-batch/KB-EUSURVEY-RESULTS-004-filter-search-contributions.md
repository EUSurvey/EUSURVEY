# How to filter or search for specific EUSurvey contributions

## Intent / Description

Explains how a survey owner can filter or search for specific contributions on the Results page.

## Applies To

* Role(s): Survey Manager
* Feature: Results filtering, Contribution search
* Context: A survey owner wants to find specific contributions based on criteria

## Short Answer

The EUSurvey Results page provides filtering capabilities to find specific contributions. You can filter by date range, language, invitation, and specific answer values. You can also search for a contribution by its unique Contribution ID.

## Steps / Procedure

**To filter contributions:**

1. Open the survey and go to the **Results** page.
2. Use the available **filter options** at the top of the results list.
3. Available filter criteria include:
   - **Date range** — filter by submission date (from/to)
   - **Language** — filter by the language used during submission
   - **Invitation** — filter by invitation or participation group
   - **Answer values** — filter by specific answers to specific questions
4. Apply the filter. The results list updates to show only matching contributions.
5. You can combine multiple filters.
6. To export only the filtered subset, click Export while filters are active.

**To find a specific contribution by ID:**

1. If you have the Contribution ID (unique code), use the search or filter function on the Results page.
2. Enter the Contribution ID to locate the specific submission.

**To clear filters:**

1. Remove or reset the active filter criteria.
2. The full list of contributions will be displayed again.

## Important Conditions / Limitations

* **Results page vs Contributions page**: The Results page shows submitted contributions. A separate administrative Contributions search may be available for cross-survey searching.
* **Filter scope**: Filters apply to the current Results view and affect exports generated while filters are active.
* **Answer-based filtering**: Not all question types may support direct answer filtering. Complex elements (matrices, tables) may have limited filter options.
* **Contribution ID is unique**: Each submitted contribution has a unique identifier. This is the most precise way to find a specific submission.
* **No full-text search**: There is no general full-text search across all answer content. Filters work on structured criteria.
* **Performance with many contributions**: Filtering large datasets may take a moment to process.

## Troubleshooting / Related Cases

* If you cannot find a contribution you expect: clear all filters and check the total count. The contribution may be excluded by an active filter.
* If filter shows no results: verify the filter criteria are correct (e.g. date format, correct question selected).
* If you need to find non-respondents: use the Participants section to see who has not answered.

## Out of Scope / Separate Topics

* How to download all responses (see: KB-EUSURVEY-RESULTS-003)
* How to see who has answered (see: KB-EUSURVEY-CONTRIB-010)
* Why a contribution is not visible (see: KB-EUSURVEY-CONTRIB-006)
* Why export does not include latest responses (see: KB-EUSURVEY-RESULTS-002)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: results_exports
* user_role: survey_manager
* feature: results_filtering
* tags: filter contributions, search contribution, find specific response, contribution ID search, results filter
* synonyms: how to search for a specific answer, find contribution by date, filter results by language, look up specific submission
* product_terms: Results, filter, Contribution ID, date range, language, Export
* exclude: export generation, statistics, published results, invitation status
