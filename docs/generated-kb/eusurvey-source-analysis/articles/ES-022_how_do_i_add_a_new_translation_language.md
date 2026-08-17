# ES-022 — How do I add a new translation language?

## Intent / Description

This article explains how to add a new language translation to a survey.

## Applies To

* Role(s): Survey Owner, Form Manager
* EUSurvey area: Translations
* Environment: All
* Article type: How-To
* UI location: Translations page
* Backend location: TranslationController.addtranslations

## Short Answer

To add a new language, navigate to the Translations page and use the 'Add New Translation' option. Select the target language from the dropdown. A new empty translation is created that you can then fill in online or import from a file.

## Prerequisites / Required Permissions

* FormManagement privilege or survey ownership
* Survey must exist

## Procedure

1. Navigate to the Translations page.
2. Click 'Add New Translation'.
3. Select the target language from the dropdown list.
4. Click Add/Confirm.
5. The new (empty) translation appears in the translations table.
6. Fill in translations for all labels or import a translation file.

## Important Conditions / Limitations

* EUSurvey supports 24 EU official languages.
* New translations are created as inactive and incomplete.
* All labels must be translated before a translation can be activated.
* The language must not already exist as a translation for this survey.

## Troubleshooting

* 'Please select a language to add': Select a valid language code from the dropdown.
* Language already exists: Each language can only be added once per survey.

## Related Articles

* ES-021 — How do I manage survey translations?
* ES-023 — How do I import a translation file?
* ES-024 — How do I request machine translation?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/translations.js
* Backend files: src/main/java/com/ec/survey/controller/TranslationController.java, src/main/java/com/ec/survey/service/TranslationService.java
* Classes: TranslationController, TranslationService
* Methods: addtranslations
* Routes: POST /{shortname}/management/addtranslations
* Message keys: label.AddNewTranslation, label.SelectLanguageForNewTranslation, error.selectlanguagetoadd
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Translations
* EUSurvey area: Translation Management
* Feature: Add Language
* User intent: How do I add a new translation language?
* Article type: How-To
* User type: Survey Owner, Form Manager
* Required permission: Survey Owner, Form Manager
* Survey status: Any
* Environment: All
* Keywords: add, language, translation, new
* Synonyms: add new language, create translation
* Acronyms: N/A
* Related entities: Translations
* Security / privacy relevance: None
* Search boost terms: add translation language, new language
* Source files: src/main/java/com/ec/survey/controller/TranslationController.java, src/main/java/com/ec/survey/service/TranslationService.java
* Duplicate status: New
