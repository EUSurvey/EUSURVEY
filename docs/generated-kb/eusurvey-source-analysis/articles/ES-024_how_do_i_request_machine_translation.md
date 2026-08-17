# ES-024 — How do I request machine translation?

## Intent / Description

This article explains how to request automatic machine translation for survey labels.

## Applies To

* Role(s): Survey Owner, Form Manager
* EUSurvey area: Translations
* Environment: All
* Article type: How-To
* UI location: Translations page
* Backend location: MachineTranslationService.java

## Short Answer

If machine translation is configured on the server, you can request automatic translation of survey labels. Navigate to the Translations page, select the target language, and click 'Request Machine Translation'. The system sends the text to the translation service and updates labels when results are received.

## Prerequisites / Required Permissions

* FormManagement privilege or survey ownership
* Machine translation must be enabled on the server (mt.use.ec.mt or Microsoft translation configured)
* A source language translation must be complete

## Procedure

1. Navigate to the Translations page.
2. Select the language you want to translate.
3. Click 'Request Machine Translation'.
4. Select the source language to translate from.
5. Confirm the request.
6. Wait for the translation service to return results.
7. Review and edit the machine-translated labels.

## Important Conditions / Limitations

* Machine translation requires server-level configuration (feature may not be available in all deployments).
* Two translation services are supported: eTranslation (EC) and Microsoft Translation.
* Machine translation results should be reviewed by a human for accuracy.
* The process is asynchronous — results may take time.
* Active translation requests can be cancelled.

## Troubleshooting

* 'Request for translation failed': The translation service is unavailable or misconfigured.
* Translation not arriving: The service is asynchronous. Check back later or cancel and retry.

## Related Articles

* ES-021 — How do I manage survey translations?
* ES-022 — How do I add a new translation language?
* ES-023 — How do I import a translation file?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/translations.js
* Backend files: src/main/java/com/ec/survey/controller/TranslationController.java, src/main/java/com/ec/survey/service/MachineTranslationService.java, src/main/java/com/ec/survey/service/ETranslationService.java
* Classes: TranslationController, MachineTranslationService, ETranslationService
* Methods: translateTranslations, translateTranlationsWithMicrosoft, sendMessage
* Routes: POST /{shortname}/management/translateTranslations
* Message keys: label.RequestLanguageTranslation, label.RequestTranslation, error.RequestTranslation, info.RequestTranslation, label.MachineTranslationInstructions
* Configuration keys: mt.use.ec.mt, microsoft.translation.client.id, microsoft.translation.client.secret

## Confidence and Review Status

Medium

## Metadata

* Domain: Translations
* EUSurvey area: Translation Management
* Feature: Machine Translation
* User intent: How do I request machine translation?
* Article type: How-To
* User type: Survey Owner, Form Manager
* Required permission: Survey Owner, Form Manager
* Survey status: Any
* Environment: All
* Keywords: machine, translation, automatic, translate, request
* Synonyms: auto translate, machine translation, automatic translation
* Acronyms: N/A
* Related entities: Translations, MachineTranslation
* Security / privacy relevance: Translated text is sent to external service
* Search boost terms: request machine translation, auto translate
* Source files: src/main/java/com/ec/survey/controller/TranslationController.java, src/main/java/com/ec/survey/service/MachineTranslationService.java, src/main/java/com/ec/survey/service/ETranslationService.java
* Duplicate status: New
