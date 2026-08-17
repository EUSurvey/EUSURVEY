# ES-053 — How do I add background documents?

## Intent / Description

This article explains how to add background documents that appear alongside the survey.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Properties page
* Backend location: ManagementController.propertiesPost

## Short Answer

Background documents are files displayed to respondents on every page of the survey, typically in a sidebar. Add them through survey Properties. These can be reference documents, guidelines, or supporting materials.

## Prerequisites / Required Permissions

* Survey owner
* Documents must be in an uploadable format

## Procedure

1. Open survey Properties.
2. Navigate to the Background Documents section.
3. Click 'Add background document'.
4. Upload the file.
5. Save properties.
6. The document appears as a download link on every survey page.

## Important Conditions / Limitations

* Background documents appear on every page of the survey.
* Multiple documents can be added.
* Documents can optionally remain visible on the unavailability page after unpublishing.
* Documents are displayed alphabetically in the sidebar.
* File uploads must meet size limitations.

## Troubleshooting

* Document not visible: Ensure properties were saved and changes applied if published.

## Related Articles

* ES-054 — How do I add useful links?
* ES-007 — How do I configure survey properties?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Classes: ManagementController, Survey
* Methods: propertiesPost
* Routes: POST /{shortname}/management/properties
* Message keys: label.BackgroundDocuments, label.AddBackgroundDocument, label.RemoveBackgroundDocument, info.BackgroundDocuments, label.ShowDocsOnUnavailabilityPage
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Management
* EUSurvey area: Documents
* Feature: Background Documents
* User intent: How do I add background documents?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: background, documents, files, reference, sidebar
* Synonyms: add reference documents, attach files to survey, sidebar documents
* Acronyms: N/A
* Related entities: Survey, File
* Security / privacy relevance: None
* Search boost terms: background documents, add files to survey
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Duplicate status: New
