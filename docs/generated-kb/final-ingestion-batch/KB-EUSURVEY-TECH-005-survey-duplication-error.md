# What to do when duplicating an EUSurvey survey fails

## Intent / Description

Explains what to do when the survey copy/duplication operation fails or produces an error.

## Applies To

* Role(s): Survey Manager
* Feature: Survey duplication (copy)
* Context: A survey owner tries to copy a survey and receives an error

## Short Answer

Survey duplication may fail due to the survey's size or complexity. When you copy a survey, EUSurvey creates a complete duplicate including all questions, settings, translations, and associated data. If the survey is very large or contains many elements, the operation may time out or encounter a processing error.

## Steps / Procedure

1. **Try again**: Temporary server load may have caused the failure. Wait a few minutes and retry.
2. **Check survey complexity**: Very large surveys with many questions, translations, or file elements are more likely to have duplication issues.
3. **Export and re-import**: As an alternative to direct duplication:
   - Export the survey (Overview → Export Survey).
   - Create a new survey and import the exported file.
4. **Check available quota**: If your account has a limit on the number of surveys, ensure you have not reached it.
5. **Simplify before copying**: If the survey has unnecessary old translations or unused elements, consider cleaning them up before attempting to copy.
6. **Contact support**: If repeated attempts fail, report the issue with the survey shortname.

## Important Conditions / Limitations

* **Complete copy**: Duplication copies all elements, settings, translations, and participation groups structure. It does not copy submitted contributions.
* **Unique shortname**: The duplicate receives a new shortname. If shortname generation conflicts occur, this may cause an issue.
* **Processing time**: Large surveys take longer to duplicate. The operation runs server-side and may time out for extremely complex surveys.
* **Permissions**: You must have appropriate privileges (owner or form management rights) to duplicate a survey.

## Troubleshooting / Related Cases

* If duplication fails consistently: try the export/import alternative.
* If the copy appears but is incomplete: verify all elements and translations were copied correctly.
* If you see a timeout error: the survey may be too large for a single copy operation.

## Out of Scope / Separate Topics

* Why a duplicated private survey cannot be accessed (see: KB-EUSURVEY-ACCESS-003)
* How to export a survey (see: SM-21)
* How to copy a survey (see: SM-22)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* If the issue persists, contact EUSurvey support with the survey shortname and error details.

## Retrieval Metadata

* business_domain: technical_access
* user_role: survey_manager
* feature: survey_duplication
* tags: copy survey fails, duplication error, cannot copy survey, duplicate survey error
* synonyms: survey copy not working, error when duplicating survey, failed to copy survey, duplication timeout
* product_terms: Copy, Export, Import, shortname
* exclude: access to duplicated survey, survey editing, publication
