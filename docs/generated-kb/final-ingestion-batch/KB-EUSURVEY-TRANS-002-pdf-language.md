# How to generate an EUSurvey PDF in the required language

## Intent / Description

Explains how to generate a PDF of an EUSurvey survey or contribution in a specific language.

## Applies To

* Role(s): Survey Manager, Respondent
* Feature: PDF generation, Translations
* Context: A user wants to generate or download a PDF in a specific language

## Short Answer

To generate a PDF in a specific language, the survey must have an active translation for that language. The PDF is generated based on the currently selected survey language version. The process differs depending on whether you need:

- A blank PDF of the survey form (for reference or printing)
- A PDF of a specific submitted contribution

## Steps / Procedure

**For a blank survey PDF (survey owners):**

1. Open the survey's **Overview** page.
2. If the survey has translations, select the desired language version.
3. Use the PDF generation option to create a blank PDF of the form.
4. The PDF will contain the survey content in the selected language.

**For a contribution PDF (respondents):**

1. If PDF download is enabled and you accessed the survey in a specific language, the PDF will reflect that language.
2. The language of the contribution PDF is typically the language the respondent used when answering.

**For a contribution PDF (survey owners):**

1. Go to the **Results** page.
2. Open the specific contribution.
3. Generate or download the PDF. The PDF reflects the language of the contribution (the language the respondent used during submission).

**Using language-specific links:**

1. To ensure a PDF is generated in a specific language, access the survey through its language-specific URL (e.g. with the `?surveylanguage=FR` parameter) before generating the PDF.

## Important Conditions / Limitations

* **Translation must be active**: A PDF can only be generated in a language for which an active, published translation exists.
* **Content vs labels**: The PDF includes both survey content (questions, answers) and system labels. Both must be translated for a fully translated PDF.
* **Font support**: PDF generation uses standard fonts. Some character sets may not render correctly if the survey uses specialised characters. This is a rendering issue, not a translation issue.
* **Contribution language is fixed**: A submitted contribution's language is determined at submission time. You cannot retroactively generate a contribution PDF in a different language.
* **PDF of blank form**: This shows the form structure without any answers, useful for reference or offline distribution.

## Troubleshooting / Related Cases

* If the PDF shows mixed languages: the translation may be incomplete. Complete the translation and regenerate.
* If special characters appear as boxes: this is a font/encoding issue in the PDF renderer, not a translation issue. See PM-05_09.
* If you cannot select a language: ensure the translation is activated and Apply Changes has been run.
* If the contribution PDF is in the wrong language: the respondent answered in that language. You cannot change the contribution's language after submission.

## Out of Scope / Separate Topics

* Why built-in labels appear in another language (see: KB-EUSURVEY-TRANS-001)
* How to translate survey content (see: UG-014, SM-53)
* How to select the language of invitation emails (see: KB-EUSURVEY-INVITE-007)
* PDF viewer errors (see: PM-05_08, PM-05_09)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: translations_languages
* user_role: survey_manager, respondent
* feature: pdf_language
* tags: PDF in specific language, generate PDF translation, survey PDF language, contribution PDF language
* synonyms: download survey in French PDF, PDF in another language, change language of PDF, generate translated PDF
* product_terms: PDF, Translations, language, surveylanguage, contribution PDF, blank PDF
* exclude: invitation email language, built-in labels, interface language, export format (XLS/CSV)
