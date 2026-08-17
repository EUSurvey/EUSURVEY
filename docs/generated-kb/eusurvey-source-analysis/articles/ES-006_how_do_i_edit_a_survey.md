# ES-006 — How do I edit a survey?

## Intent / Description

This article explains how to use the survey editor to add and modify questions and elements.

## Applies To

* Role(s): Survey Owner, Form Manager
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Editor page
* Backend location: ManagementController.edit, editPOST

## Short Answer

To edit a survey, open it from your Forms list and navigate to the Editor page. The editor provides a drag-and-drop interface for adding questions, text elements, sections, and other components. Changes are saved when you click Save. If the survey is already published, edits create pending changes that must be applied separately.

## Prerequisites / Required Permissions

* The user must be the survey owner or have FormManagement local privilege
* The survey must not be archived or frozen

## Procedure

1. Open the survey from the Forms list.
2. Navigate to the Editor page.
3. Use the toolbox on the left to add new elements (drag and drop or click).
4. Click on existing elements to select and modify their properties.
5. Configure element properties in the properties panel.
6. Set mandatory status, help text, and other options as needed.
7. Click Save to persist your changes.
8. If published, navigate to Overview and Apply Changes to make edits live.

## Important Conditions / Limitations

* If the survey is published, edits create pending changes visible via 'Show pending changes'.
* Elements can be reordered via drag-and-drop.
* The editor validates XHTML content on save.
* Undo/redo functionality is available for recent changes.
* Complex surveys may trigger a complexity warning.
* Element shortnames (identifiers) must be unique within the survey.
* Locked elements cannot be modified.

## Troubleshooting

* 'Your input cannot be processed (XHTML invalid)': The text contains invalid HTML. Remove special formatting.
* 'This element is locked': The element was locked by the system and cannot be edited.
* Changes not appearing for respondents: Apply pending changes after editing a published survey.

## Related Articles

* ES-007 — How do I configure survey properties?
* ES-008 — How do I publish a survey?
* ES-010 — How do I apply changes after publication?
* ES-050 — How do I set up dependencies/visibility rules?

## Evidence / Source Traceability

* Frontend files:
* src/main/webapp/resources/js/edit.js
* src/main/webapp/resources/js/edit_actions.js
* src/main/webapp/resources/js/edit_add.js
* src/main/webapp/resources/js/edit_update.js
* src/main/webapp/resources/js/edit_validation.js
* src/main/webapp/resources/js/edit_properties.js
* Backend files:
* src/main/java/com/ec/survey/controller/ManagementController.java
* src/main/java/com/ec/survey/tools/SurveyHelper.java
* Classes: ManagementController, SurveyHelper
* Methods: edit, editPOST, checkXHTML, parseSurvey
* Routes: GET /{shortname}/management/edit, POST /{shortname}/management/edit
* Message keys: label.Editor, label.Save, error.InvalidXHTML, info.ElementLocked, label.EditText
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Management
* EUSurvey area: Survey Editor
* Feature: Edit Survey
* User intent: How do I edit a survey?
* Article type: How-To
* User type: Survey Owner, Form Manager
* Required permission: Survey Owner, Form Manager
* Survey status: Draft or Published (creates pending changes)
* Environment: All
* Keywords: edit, editor, modify, questions, add, elements, drag, drop
* Synonyms: modify survey, change questions, add questions, edit form
* Acronyms: N/A
* Related entities: Survey, Element
* Security / privacy relevance: None
* Search boost terms: edit survey, modify questions, survey editor
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/tools/SurveyHelper.java
* Duplicate status: New
