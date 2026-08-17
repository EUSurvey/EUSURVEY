# ES-035 — How do I preview my survey?

## Intent / Description

This article explains how to preview a survey before publishing.

## Applies To

* Role(s): Survey Owner, Form Manager
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Editor/Overview
* Backend location: ManagementController.test

## Short Answer

To preview a survey, navigate to the Editor or Overview page and click 'Preview' or 'Test'. This opens the survey in a respondent view so you can see how it will appear to participants. Test submissions can be made without affecting real data.

## Prerequisites / Required Permissions

* FormManagement privilege or survey ownership
* Survey must have at least one element

## Procedure

1. Open the survey Editor or Overview page.
2. Click the Preview/Test button.
3. The survey opens in respondent view.
4. Navigate through the survey as a respondent would.
5. Submit a test contribution if desired.
6. Test contributions are marked and can be filtered out from real results.

## Important Conditions / Limitations

* Preview shows the survey exactly as respondents will see it.
* Test contributions submitted through preview are tagged as test data.
* Test answers can be excluded from statistics using the 'No test answers' filter.
* Preview works for both draft and published surveys.
* All question types and dependencies can be tested.

## Troubleshooting

* Preview not showing latest changes: Save the survey in the editor before previewing.

## Related Articles

* ES-006 — How do I edit a survey?
* ES-008 — How do I publish a survey?
* ES-012 — How do respondents submit a contribution?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java
* Classes: ManagementController
* Methods: test, testPOST
* Routes: GET /{shortname}/management/test, POST /{shortname}/management/test
* Message keys: label.Preview, label.AccessFormPreview
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Management
* EUSurvey area: Testing
* Feature: Preview Survey
* User intent: How do I preview my survey?
* Article type: How-To
* User type: Survey Owner, Form Manager
* Required permission: Survey Owner, Form Manager
* Survey status: Any
* Environment: All
* Keywords: preview, test, view, check, verify
* Synonyms: test survey, preview form, check survey appearance
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: preview survey, test survey, check appearance
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java
* Duplicate status: New
