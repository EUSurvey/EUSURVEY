# ES-001 — How do I create a new survey?

## Intent / Description

This article explains how a registered user creates a new survey in EUSurvey.

## Applies To

* Role(s): Registered User, Survey Owner
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Dashboard / Forms page
* Backend location: ManagementController.createNewSurvey

## Short Answer

To create a new survey, log in to EUSurvey, navigate to the Forms page, and click "Create new Survey". Provide a title, select a main language, and optionally set a shortname (alias). The system creates a new draft survey and opens the survey editor.

## Prerequisites / Required Permissions

* The user must be authenticated (registered account or EU Login).
* External users may be restricted from creating surveys if the property `isCreateSurveysForExternalsDisabled` is set to true.
* There may be a survey creation limit configured (`surveylimit` property) that restricts the number of surveys a user can create in a given time period.

## Procedure

1. Log in to EUSurvey.
2. Navigate to the "Forms" page (My Surveys).
3. Click "Create new Survey" button.
4. Enter the survey title.
5. Select the main language for the survey.
6. Optionally enter a shortname/alias for a user-friendly URL.
7. Select the survey type if applicable (Standard, Quiz, Delphi, eVote, Self-Assessment — depending on enabled features).
8. Click "Create" to confirm.
9. The system creates the survey in draft state and opens the editor.

## Important Conditions / Limitations

* The shortname must be unique across all surveys in the system. The system performs an AJAX check via `SurveyController.shortnameexistsjson`.
* Special characters `&`, `<`, `>` are not allowed in the survey name (error key: `error.NameInvalid`).
* Survey creation limit: If configured, users cannot exceed a maximum number of surveys in a time period. Error message: "You have reached the maximum number of {0} surveys that can be created in a period of time ({1})."
* Survey types like Quiz, Delphi, eVote, Self-Assessment, ECF, and OPC are only available when the corresponding feature flag is enabled in the server configuration.
* External users (non-EU institution) may be prevented from creating surveys based on system configuration.

## Troubleshooting

* **"You have reached the maximum number..." error**: The survey creation limit has been reached. Contact support or wait for the limit period to reset.
* **Shortname already exists**: Choose a different shortname. The system checks uniqueness in real-time.
* **Cannot create surveys**: Check if your account type (external) is restricted. The admin may have disabled survey creation for external users.

## Related Articles

* ES-002 — How do I copy an existing survey?
* ES-003 — How do I import a survey?
* ES-006 — How do I edit a survey?
* ES-008 — How do I publish a survey?

## Evidence / Source Traceability

* Backend: `src/main/java/com/ec/survey/controller/ManagementController.java` — method `createNewSurvey()`
* Backend: `src/main/java/com/ec/survey/controller/SurveyController.java` — method `shortnameexistsjson()`
* Backend: `src/main/java/com/ec/survey/service/SurveyService.java` — method `checkSurveyCreationLimit()`
* Frontend: `src/main/webapp/WEB-INF/views/forms/forms.jsp`
* Frontend: `src/main/webapp/resources/js/includes.js` — function `createNewSurvey()`
* Message keys: `error.surveylimit`, `error.NameInvalid`, `error.ChoseName`
* Configuration: `spring.properties` — survey creation limit settings
* Route: GET/POST `/{shortname}/management`

## Confidence and Review Status

High — behaviour is directly visible in UI and backend code.

## Metadata

* Domain: Survey Management
* EUSurvey area: Survey Creation
* Feature: Create Survey
* User intent: How do I create a new survey?
* Article type: How-To
* User type: Registered User
* Required permission: Authenticated user
* Survey status: N/A (creates new)
* Environment: All
* Keywords: create, new, survey, form, questionnaire, start
* Synonyms: make a survey, build a form, start a questionnaire, new form
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: create new survey, how to make survey, start survey
* Source files: ManagementController.java, SurveyController.java, forms.jsp
* Duplicate status: New
