# How to back up or preserve an EUSurvey survey

## Intent / Description

Explains the available methods for backing up or preserving an EUSurvey survey including its structure, settings, and collected data.

## Applies To

* Role(s): Survey Manager
* Feature: Survey export, Archiving, Backup
* Context: A survey owner wants to create a backup of their survey before making changes or for record-keeping

## Short Answer

EUSurvey provides several ways to preserve a survey:

1. **Copy the survey** — Creates a complete duplicate of the survey form (without contributions).
2. **Export the survey** — Downloads the survey structure and settings as a file that can be re-imported.
3. **Export contributions** — Downloads all submitted responses as XLS, XLSX, CSV, ODS, or PDF.
4. **Generate a blank PDF** — Creates a PDF of the empty form for documentation purposes.
5. **Archive the survey** — Moves the survey to an archive state for long-term preservation.

No single action backs up everything (form + data + settings) in one step. Combine methods for a complete backup.

## Steps / Procedure

**To export the survey structure (form backup):**

1. Open the survey's **Overview** page.
2. Click **Export Survey**.
3. Save the downloaded file. This contains the survey structure, questions, and settings.
4. You can later import this file to recreate the survey.

**To export contributions (data backup):**

1. Go to the **Results** page.
2. Click **Export** and choose your format.
3. Download the generated file when ready.

**To copy the survey:**

1. From the survey list, select **Copy**.
2. A new survey is created with all questions and settings but without contributions.

**To generate a blank PDF:**

1. From the Overview page, use the PDF generation option.
2. The PDF contains the form as it would appear to a respondent (without answers).

**To archive the survey:**

1. From the survey management area, select **Archive**.
2. The survey is moved to archived state. Archived surveys are preserved but not actively accessible to respondents.

## Important Conditions / Limitations

* **Survey export does not include contributions**: The export file contains only the form structure, not submitted answers. Export contributions separately.
* **Copy does not include contributions**: A copied survey starts fresh with zero submissions.
* **Archive preserves everything**: Archiving keeps the survey and its contributions intact but makes the survey inactive.
* **Export file can be re-imported**: If you need to recreate the survey, import the export file into EUSurvey.
* **No single full backup**: There is no "download everything" button. You need to export the form AND export the contributions separately.
* **Translations**: Survey export includes translations. Contribution export does not include translation files.

## Troubleshooting / Related Cases

* If you want to make changes without risk: copy the survey first, make changes on the copy, and only apply to the original if satisfied.
* If you need a permanent record for an audit: export both the survey form and the contributions, and archive the survey.
* If you need the survey offline: generate a blank PDF for the form, and export contributions as spreadsheets.

## Out of Scope / Separate Topics

* How to export a survey (see: SM-21)
* How to copy a survey (see: SM-22)
* How to archive a survey (see: SM-73)
* How to download all responses (see: KB-EUSURVEY-RESULTS-003)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: survey_management
* user_role: survey_manager
* feature: survey_backup, export, archive
* tags: backup survey, preserve survey, save survey copy, export survey data, survey archiving
* synonyms: how to back up my survey, save a copy of the survey, preserve survey before changes, keep survey for records
* product_terms: Export Survey, Copy, Archive, PDF, Results Export
* exclude: publication, deletion, contribution editing
