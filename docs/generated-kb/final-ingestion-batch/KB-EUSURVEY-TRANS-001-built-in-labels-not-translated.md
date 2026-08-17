# Why built-in labels such as Next and Submit remain in another language

## Intent / Description

Explains why system interface labels (Next, Previous, Submit, etc.) may not appear in the expected language, and how to resolve this.

## Applies To

* Role(s): Survey Manager, Respondent
* Feature: Survey translations, Interface language
* Context: A translated survey shows the correct question text but system buttons remain in a different language

## Short Answer

Built-in labels (Next, Previous, Submit, Save as Draft, etc.) are system interface elements separate from survey content. They may appear in a different language because:

1. **The survey's active translation does not cover built-in labels** — Survey translations typically translate question text and answer options but built-in labels are controlled by the platform's language files.
2. **The survey language parameter is not set correctly** — The respondent's browser may not be sending the expected language, or the survey URL does not specify the language.
3. **The translation is incomplete** — The survey owner activated a translation but it does not include all system labels.
4. **Interface language vs survey content language** — Built-in labels follow the interface language setting, which may differ from the survey content language.

## Steps / Procedure

**For survey owners:**

1. Check that the survey has an active **translation** for the desired language. Go to **Translations** and verify the language is activated.
2. Ensure the translation is **complete**. An incomplete translation may leave some labels in the pivot language.
3. **Apply Changes** after making translation updates. Changes are not visible until applied.
4. Test the survey using the language-specific URL (e.g. add `?surveylanguage=FR` to the URL) to force a specific language.
5. Verify the language codes are correct.

**For respondents:**

1. Check the **language selector** on the survey page (if available). Select your preferred language.
2. Try accessing the survey through a language-specific link if the survey owner provided one.
3. Note that the survey owner controls which languages are available. Not all languages may be translated.

## Important Conditions / Limitations

* **System labels vs survey content**: Survey content (questions, answers) and system labels (Next, Submit, etc.) are managed in separate translation layers. A survey can have fully translated content but still show system labels in the pivot language if the interface translation is incomplete.
* **Pivot language**: The survey's original language (pivot) is used as fallback for any untranslated labels.
* **Apply Changes required**: After activating a new translation, the survey owner must apply changes for the translation to take effect on the published survey.
* **Browser language**: In some cases, the system may auto-detect the browser's language preference. This does not always match the respondent's expectation.
* **Not all labels are translatable by the survey owner**: Some system labels are built into the platform and are translated at the platform level. If the platform does not support a specific language, those labels will remain in a default language.

## Troubleshooting / Related Cases

* If only system labels are wrong but content is correct: the interface language detection is using a different language than expected. Try using the language-specific URL.
* If both content and labels are wrong: the survey may be loading in the wrong language entirely. Check the URL parameters.
* If a translation was recently added and does not appear: ensure it was activated and Apply Changes was clicked.

## Out of Scope / Separate Topics

* How to translate survey content (see: UG-014, SM-53)
* How to generate a PDF in a specific language (see: KB-EUSURVEY-TRANS-002)
* How to select the language of invitation emails (see: KB-EUSURVEY-INVITE-007)
* How to publish translations (see: SM-57)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: translations_languages
* user_role: survey_manager, respondent
* feature: built_in_labels, interface_language
* tags: built-in labels wrong language, Next button language, Submit in wrong language, interface not translated
* synonyms: navigation buttons in wrong language, system labels not translated, Next and Previous in English, survey buttons wrong language
* product_terms: Translations, language, Apply Changes, pivot language, interface language, surveylanguage
* exclude: invitation email language, PDF language, content translation procedure, machine translation
