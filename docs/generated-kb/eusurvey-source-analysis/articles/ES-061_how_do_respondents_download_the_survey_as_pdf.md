# ES-061 — How do respondents download the survey as PDF?

## Intent / Description

This article explains how respondents download the empty survey questionnaire as a PDF.

## Applies To

* Role(s): Respondent
* EUSurvey area: Survey Runner
* Environment: All
* Article type: How-To
* UI location: Runner sidebar
* Backend location: PDFController.java

## Short Answer

If enabled by the survey owner, respondents can download the empty questionnaire as a PDF for offline review. Look for the download link in the survey sidebar.

## Prerequisites / Required Permissions

* Survey.allowQuestionnaireDownload must be enabled
* Survey must be published

## Procedure

1. Access the survey page.
2. Look for Download empty questionnaire as PDF in the sidebar.
3. Click the link.
4. PDF is generated and downloaded.

## Important Conditions / Limitations

* Downloads the EMPTY questionnaire, not answers.
* Must be enabled by survey owner.
* PDF generation is asynchronous.
* PDF can remain available on unavailability page after unpublishing.

## Troubleshooting

* Link not visible: Owner has not enabled questionnaire download.
* PDF not ready: Wait for async generation.

## Related Articles

* ES-015 — How do I download my contribution as PDF?
* ES-012 — How do respondents submit a contribution?

## Evidence / Source Traceability

* Frontend: src/main/webapp/resources/js/menu.js
* Backend: src/main/java/com/ec/survey/controller/PDFController.java, src/main/java/com/ec/survey/service/PDFService.java
* Classes: PDFController, PDFService
* Methods: pubsurvey, survey, createSurveyPDF
* Routes: GET /pdf/pubsurvey/{id}, GET /pdf/survey/{id}
* Message keys: label.AllowQuestionnaireDownloadNew, label.DownloadEmptyPDFversion, info.AllowQuestionnaireDownload, label.ShowPDFOnUnavailabilityPage
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Runner
* EUSurvey area: PDF Download
* Feature: Survey PDF
* User intent: How do respondents download the survey as PDF?
* Article type: How-To
* User type: Respondent
* Required permission: Respondent
* Survey status: Published
* Environment: All
* Keywords: PDF, download, questionnaire, empty, print
* Synonyms: download questionnaire, print survey, get PDF form
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: None
* Search boost terms: download survey PDF, print questionnaire
* Source files: src/main/java/com/ec/survey/controller/PDFController.java, src/main/java/com/ec/survey/service/PDFService.java
* Duplicate status: New
