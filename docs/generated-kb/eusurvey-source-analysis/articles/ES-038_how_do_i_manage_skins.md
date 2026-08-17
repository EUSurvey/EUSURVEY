# ES-038 — How do I manage skins?

## Intent / Description

This article explains how to create and manage visual skins (themes) for surveys.

## Applies To

* Role(s): Registered User
* EUSurvey area: Appearance
* Environment: All
* Article type: How-To
* UI location: Settings > Skins
* Backend location: SkinController

## Short Answer

Skins control the visual appearance of your surveys. Navigate to Settings > Skins to view, create, edit, copy, or delete skins. You can customize colors, fonts, and styles. Default skins (EUSurvey, European Commission, European Court of Auditors) are available for use.

## Prerequisites / Required Permissions

* Authenticated user

## Procedure

1. Navigate to Settings > Skins.
2. View available skins (own and public).
3. Click 'Create a new Skin' to make a custom skin.
4. Set a name for the skin.
5. Customize visual properties: colors, fonts, background.
6. Save the skin.
7. Apply the skin to a survey via the survey Properties page.

## Important Conditions / Limitations

* Default skins are available: EUSurvey, European Commission, European Court of Auditors.
* Custom skins can be created, copied, and edited.
* Skins can be made public for other users.
* Skin names must be unique among public skins.
* Skins in use by a survey cannot be deleted.
* Skins can be exported and imported as .euss files.
* Invalid skin file content is rejected on upload.

## Troubleshooting

* 'This skin is being used in a survey and can therefore not be deleted': Remove the skin from all surveys first.
* 'There is already a public skin using this name': Choose a unique name.
* 'The file format is not correct': Only .euss files can be imported.

## Related Articles

* ES-007 — How do I configure survey properties?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/settings/skins.jsp, src/main/webapp/WEB-INF/views/settings/skin.jsp
* Backend files: src/main/java/com/ec/survey/controller/SkinController.java, src/main/java/com/ec/survey/service/SkinService.java, src/main/java/com/ec/survey/model/Skin.java
* Classes: SkinController, SkinService, Skin
* Methods: skins, newSkin, editSkin, deleteSkin, copySkin, upload, download
* Routes: GET /settings/skin, POST /settings/skin
* Message keys: label.Skins, label.CreateNewSkin, label.EditSkin, error.SkinInUse, error.SkinUnauthorized, error.SkinUploadFailedInvalidFileType, error.SkinUploadFailedInvalidFileContent, info.SkinNameExists, help.SkinNew
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Appearance
* EUSurvey area: Settings
* Feature: Manage Skins
* User intent: How do I manage skins?
* Article type: How-To
* User type: Registered User
* Required permission: Registered User
* Survey status: N/A
* Environment: All
* Keywords: skin, theme, appearance, design, colors, style
* Synonyms: change appearance, customize look, create theme, visual style
* Acronyms: N/A
* Related entities: Skin, Survey
* Security / privacy relevance: None
* Search boost terms: manage skins, customize appearance, survey theme
* Source files: src/main/java/com/ec/survey/controller/SkinController.java, src/main/java/com/ec/survey/service/SkinService.java, src/main/java/com/ec/survey/model/Skin.java
* Duplicate status: New
