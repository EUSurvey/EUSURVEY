# Why the contribution summary cannot be opened after submission

## Intent / Description

Explains why a respondent may be unable to access the contribution summary or PDF after submitting, and what alternatives are available.

## Applies To

* Role(s): Respondent
* Feature: Contribution summary, PDF download
* Context: A respondent tries to view or download their contribution summary but cannot access it

## Short Answer

After submitting a contribution, EUSurvey may show a confirmation page with options to view or download a summary. If the contribution summary or PDF cannot be opened, possible reasons include:

1. **PDF download not enabled** — The survey owner has not enabled the "Allow participants to download a PDF copy of their contribution" setting.
2. **PDF still being generated** — PDF generation is asynchronous and may take a few moments for complex surveys.
3. **Session expired** — If you navigated away from the confirmation page and your session expired, the link to the summary may no longer work.
4. **Survey closed or unpublished** — If the survey has since been closed or unpublished, the summary may no longer be accessible.
5. **Browser issue** — Pop-up blockers or PDF viewer issues may prevent the file from opening.

## Steps / Procedure

**For respondents:**

1. After submitting, stay on the confirmation page. If PDF download is available, a button or link will appear.
2. If the PDF is being generated, wait a moment and try again.
3. If you already left the confirmation page:
   - If you received a confirmation email with a download link, use that link.
   - If contribution editing is enabled, access your contribution via the Edit Contribution page and look for a download option.
4. If a pop-up was blocked, check your browser's pop-up blocker settings.
5. If none of the above works, contact the survey owner and provide your Contribution ID.

**For survey owners:**

1. To enable PDF download: go to **Properties** → **Security** and enable **"Allow participants to download a PDF copy of their contribution"**.
2. PDF generation occurs asynchronously. For surveys with many questions or file uploads, generation may take longer.

## Important Conditions / Limitations

* **Survey owner must enable the feature**: PDF download of contributions is not available by default. The `downloadContribution` setting must be enabled.
* **Asynchronous generation**: The PDF is generated server-side after submission. The respondent may need to wait briefly or refresh.
* **One-time access from confirmation page**: If the respondent navigates away from the confirmation page and the survey does not provide a persistent link, they may not be able to return to the summary.
* **Contribution ID preserves access**: If the survey has editing enabled, the respondent can use their Contribution ID to access the contribution again, including any download option.
* **Large contributions**: Contributions with many file uploads may take longer to generate as PDF.

## Troubleshooting / Related Cases

* If the PDF shows "generating" indefinitely: try refreshing the page. If the issue persists, contact the survey owner.
* If the downloaded PDF shows formatting issues (boxes instead of characters): this may be a font issue in the PDF viewer. Try a different PDF reader.
* If the confirmation page showed no download option: the survey owner has not enabled PDF download for this survey.

## Out of Scope / Separate Topics

* How to save a PDF copy of a contribution (see: PM-05_02)
* What confirmation a respondent receives after submission (see: KB-EUSURVEY-CONTRIB-009)
* PDF viewer errors (see: PM-05_08, PM-05_09)
* How to view or print a contribution after submission (see: PM-05_01)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contribution_management
* user_role: respondent
* feature: contribution_summary, pdf_download
* tags: summary not available, cannot download contribution PDF, contribution summary blank, PDF not loading
* synonyms: cannot open contribution summary, PDF after submission not working, where is my submitted contribution summary, download answers after submitting
* product_terms: contribution PDF, confirmation page, Contribution ID, downloadContribution
* exclude: export results as PDF, survey PDF for printing, blank screen errors
