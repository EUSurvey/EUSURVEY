# ES-021 — How do I manage survey translations?

## Intent / Description

This article explains how to manage translations of a survey into multiple languages.

## Applies To

* Role(s): Survey Owner, Form Manager
* EUSurvey area: Translations
* Environment: All
* Article type: How-To
* UI location: Translations page
* Backend location: TranslationController.translations

## Short Answer

To manage translations, navigate to the survey's Translations page. Here you can add new languages, edit translations online, import/export translation files, request machine translations, and activate/deactivate translations. Each translation must be complete before the survey can be published with that language.

## Prerequisites / Required Permissions

* FormManagement privilege or survey ownership
* Survey must exist

## Procedure

1. Open the survey management area.
2. Navigate to the Translations page.
3. View existing translations and their status (complete/incomplete).
4. Edit translations by clicking on cells in the translation table.
5. Use the toolbar to add languages, import, export, or request machine translation.
6. Activate or deactivate translations as needed.
7. Ensure all active translations are complete before publishing.

## Important Conditions / Limitations

* Translations must be complete before publishing (all labels filled in).
* The main survey language cannot be deleted.
* At least one complete translation must remain.
* Translations can be edited online in a table interface.
* Translations can be exported as XLS, ODS, or XML for offline editing.
* Machine translation can be requested if configured.
* Pivot language controls which language is shown as reference during editing.

## Troubleshooting

* 'Translation is not complete' when publishing: Fill in all empty labels or deactivate the incomplete translation.
* 'You cannot delete the translation of the main language': The main language translation is required.

## Related Articles

* ES-022 — How do I add a new translation language?
* ES-023 — How do I import a translation file?
* ES-024 — How do I request machine translation?
* ES-008 — How do I publish a survey?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/translations.js
* Backend files: src/main/java/com/ec/survey/controller/TranslationController.java, src/main/java/com/ec/survey/service/TranslationService.java, src/main/java/com/ec/survey/tools/TranslationsHelper.java
* Classes: TranslationController, TranslationService, TranslationsHelper
* Methods: translations, savetranslations, activatetranslations, deactivatetranslations
* Routes: GET /{shortname}/management/translations
* Message keys: label.EditTranslations, info.KeepMainTranslation, info.KeepOneCompleteTranslation, error.MissingTranslation
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Translations
* EUSurvey area: Translation Management
* Feature: Manage Translations
* User intent: How do I manage survey translations?
* Article type: How-To
* User type: Survey Owner, Form Manager
* Required permission: Survey Owner, Form Manager
* Survey status: Any
* Environment: All
* Keywords: translation, language, multilingual, localize, translate
* Synonyms: translate survey, add language, multilingual survey
* Acronyms: N/A
* Related entities: Translations, Survey
* Security / privacy relevance: None
* Search boost terms: manage translations, translate survey, add language
* Source files: src/main/java/com/ec/survey/controller/TranslationController.java, src/main/java/com/ec/survey/service/TranslationService.java, src/main/java/com/ec/survey/tools/TranslationsHelper.java
* Duplicate status: New
