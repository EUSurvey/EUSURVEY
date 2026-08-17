# ES-002 — How do I copy an existing survey?

## Intent / Description

This article explains how to create a copy of an existing survey.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Forms list
* Backend location: ManagementController (overview)

## Short Answer

To copy a survey, navigate to the survey's Overview page and use the Copy function. The system creates a duplicate of the survey including all questions, properties, and structure. The copy is created as a new draft survey with a new shortname. You can also copy translations and privileged users along with the survey.

## Prerequisites / Required Permissions

* The user must be the survey owner or have FormManagement privilege
* The survey must exist and be accessible

## Procedure

1. Open the survey you want to copy in the management area.
2. Navigate to the Overview page.
3. Click the 'Copy Survey' option.
4. Optionally choose to copy survey users and their privileges.
5. Optionally choose to copy translations.
6. Enter a new shortname for the copy.
7. Confirm the copy operation.
8. The system creates a new draft survey with the copied content.

## Important Conditions / Limitations

* The copy is always created as a new draft survey regardless of the original's state.
* A new unique shortname must be assigned.
* The 'Do Not Delete' property is automatically deactivated on copies.
* Survey ownership is set to the user performing the copy.
* Contributions/answers are NOT copied - only the survey structure.
* File references (images, downloads) are duplicated.

## Troubleshooting

* **Shortname conflict**: Choose a different shortname for the copy.
* **Missing elements after copy**: Some file references may need to be re-uploaded if the originals are unavailable.

## Related Articles

* ES-001 — How do I create a new survey?
* ES-003 — How do I import a survey?
* ES-004 — How do I export a survey structure?

## Evidence / Source Traceability

* Frontend files:
* src/main/webapp/WEB-INF/views/forms/forms.jsp
* Backend files:
* src/main/java/com/ec/survey/controller/ManagementController.java
* src/main/java/com/ec/survey/service/SurveyService.java
* Classes: ManagementController, SurveyService
* Methods: copiedSurveyApplyTranslations, copiedSurveyApplyPrivileges, copyElements
* Routes: /{shortname}/management/overview
* Message keys: label.CopySurvey, label.CopyUsersAndPrivileges
* Configuration keys: N/A

## Confidence and Review Status

High — behaviour is directly visible in UI and backend code.

## Metadata

* Domain: Survey Management
* EUSurvey area: Survey Creation
* Feature: Copy Survey
* User intent: How do I copy an existing survey?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: copy, duplicate, clone, replicate, survey
* Synonyms: duplicate survey, clone survey, replicate form
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: copy survey, duplicate survey, clone existing survey
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
