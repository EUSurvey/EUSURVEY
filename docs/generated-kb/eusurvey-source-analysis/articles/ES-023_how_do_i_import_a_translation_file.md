# ES-023 — How do I import a translation file?

## Intent / Description

This article explains how to import a translation from an external file.

## Applies To

* Role(s): Survey Owner, Form Manager
* EUSurvey area: Translations
* Environment: All
* Article type: How-To
* UI location: Translations page
* Backend location: TranslationController.importtranslation

## Short Answer

To import a translation, navigate to the Translations page and click 'Import Translation'. Select an XLS, ODS, or XML file containing the translations. The system validates the file and imports the labels into the corresponding language.

## Prerequisites / Required Permissions

* FormManagement privilege or survey ownership
* A valid translation file (XLS, ODS, or XML format) must be available
* The file must match the current survey structure

## Procedure

1. Navigate to the Translations page.
2. Click 'Import Translation'.
3. Select the translation file (XLS, ODS, or XML format).
4. The system validates the file against the survey structure.
5. Review any warnings about unrecognized keys.
6. Confirm the import.
7. The translation labels are updated.

## Important Conditions / Limitations

* Supported formats: XLS, ODS, XML.
* The file must match the survey structure (same question keys).
* Unrecognized keys are reported but do not block the import.
* Importing overwrites existing labels for matching keys.
* The language code in the file determines which translation is updated.
* If the language does not yet exist, it may need to be added first.

## Troubleshooting

* 'The provided translation is not valid': The file format is incorrect or corrupted.
* 'The provided translation does not match the loaded survey': The translation file was exported from a different survey.
* 'Language code not recognized': The file contains an unsupported language code.

## Related Articles

* ES-021 — How do I manage survey translations?
* ES-022 — How do I add a new translation language?
* ES-024 — How do I request machine translation?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/translations.js
* Backend files: src/main/java/com/ec/survey/controller/TranslationController.java, src/main/java/com/ec/survey/tools/TranslationsHelper.java
* Classes: TranslationController, TranslationsHelper
* Methods: importtranslation, importtranslation2, importXLS, importODS, importXML
* Routes: POST /{shortname}/management/importtranslation
* Message keys: label.ImportTranslation, error.TranslationFileInvalid, error.TranslationWrongSurvey, error.LanguageCodeNotRecognized, error.LanguageNotRecognized
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Translations
* EUSurvey area: Translation Management
* Feature: Import Translation
* User intent: How do I import a translation file?
* Article type: How-To
* User type: Survey Owner, Form Manager
* Required permission: Survey Owner, Form Manager
* Survey status: Any
* Environment: All
* Keywords: import, translation, file, XLS, ODS, XML, upload
* Synonyms: upload translation file, import translated labels
* Acronyms: N/A
* Related entities: Translations, ImportTranslationResult
* Security / privacy relevance: None
* Search boost terms: import translation, upload translation file
* Source files: src/main/java/com/ec/survey/controller/TranslationController.java, src/main/java/com/ec/survey/tools/TranslationsHelper.java
* Duplicate status: New
