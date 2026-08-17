# ES-054 — How do I add useful links?

## Intent / Description

This article explains how to add useful links that appear in the survey sidebar.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Properties page
* Backend location: ManagementController.propertiesPost

## Short Answer

Useful links are hyperlinks displayed on every page of the survey, typically in a sidebar alongside background documents. Add them through survey Properties.

## Prerequisites / Required Permissions

* Survey owner

## Procedure

1. Open survey Properties.
2. Navigate to the Useful Links section.
3. Click 'Add useful link'.
4. Enter the link URL and display text.
5. Save properties.
6. The link appears on every survey page.

## Important Conditions / Limitations

* Useful links appear in the survey sidebar on every page.
* Multiple links can be added.
* Links open in a new window/tab.
* Links are displayed alongside background documents.
* Advanced useful links can also be configured.

## Troubleshooting

* Link not appearing: Save properties and apply changes if published.

## Related Articles

* ES-053 — How do I add background documents?
* ES-007 — How do I configure survey properties?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Classes: ManagementController, Survey
* Methods: propertiesPost
* Routes: POST /{shortname}/management/properties
* Message keys: label.AddUsefulLink, label.RemoveUsefulLink, info.UsefulLinks
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Management
* EUSurvey area: Documents
* Feature: Useful Links
* User intent: How do I add useful links?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: links, useful, sidebar, reference, URL
* Synonyms: add links to survey, sidebar links, reference URLs
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: useful links, add links to survey, sidebar links
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Duplicate status: New
