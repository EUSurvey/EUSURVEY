# ES-049 — How do I enable multi-paging?

## Intent / Description

This article explains how to split a survey into multiple pages using sections.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Editor
* Environment: All
* Article type: How-To
* UI location: Properties page
* Backend location: ManagementController.propertiesPost

## Short Answer

To enable multi-paging, go to survey Properties and enable 'Multi-Paging'. Each Section element in your survey becomes a page break. Respondents navigate between pages using Next/Previous buttons.

## Prerequisites / Required Permissions

* Survey owner or FormManagement privilege
* Survey must contain Section elements for page breaks

## Procedure

1. Open survey Properties.
2. Enable 'Multi-Paging' option.
3. Save properties.
4. In the Editor, add Section elements where you want page breaks.
5. Each section becomes a separate page for respondents.

## Important Conditions / Limitations

* Each Section element creates a page break.
* Respondents navigate with Next/Previous buttons.
* Page-wise validation can be enabled to validate each page before allowing navigation.
* The 'Prevent going back' option disables the Previous button.
* Dependencies across pages are evaluated when navigating.
* Progress bar reflects page navigation.
* Multi-paging requires at least one Section element.

## Troubleshooting

* Pages not splitting: Ensure Section elements are placed between questions.
* Validation errors on page change: Enable or disable 'Page-wise Validation' in properties.

## Related Articles

* ES-006 — How do I edit a survey?
* ES-050 — How do I set up dependencies/visibility rules?
* ES-067 — How does the progress bar work?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/runner.js
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Classes: ManagementController, Survey
* Methods: propertiesPost, checkPages, selectPage
* Routes: POST /{shortname}/management/properties
* Message keys: label.MultiPaging, label.PageWiseValidation, label.PreventGoingBack, info.MultiPaging
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Editor
* EUSurvey area: Survey Properties
* Feature: Multi-Paging
* User intent: How do I enable multi-paging?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: multi-paging, pages, sections, navigation, paginate
* Synonyms: split survey into pages, paginate survey, multi-page form
* Acronyms: N/A
* Related entities: Survey, Section
* Security / privacy relevance: None
* Search boost terms: multi-paging, paginate survey, split into pages
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Duplicate status: New
