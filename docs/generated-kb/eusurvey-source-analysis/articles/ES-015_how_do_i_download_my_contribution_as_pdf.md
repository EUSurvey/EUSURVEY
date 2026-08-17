# ES-015 — How do I download my contribution as PDF?

## Intent / Description

This article explains how respondents can download their submitted contribution as a PDF file.

## Applies To

* Role(s): Respondent
* EUSurvey area: Survey Runner
* Environment: All
* Article type: How-To
* UI location: Confirmation page
* Backend location: RunnerController.createanswerpdf, PDFService

## Short Answer

If the survey owner has enabled the 'Allow participants to print their contribution' setting, respondents can download a PDF of their submitted answers from the confirmation page. The PDF is generated asynchronously and a download link is provided when ready.

## Prerequisites / Required Permissions

* The survey must have 'Download Contribution' enabled (Survey.downloadContribution)
* The respondent must have just submitted or have access to their contribution

## Procedure

1. Submit your contribution to the survey.
2. On the confirmation page, click the PDF download option.
3. Wait for the PDF to be generated (asynchronous process).
4. Download the PDF when the link appears.
5. Alternatively, request to receive the PDF by email.

## Important Conditions / Limitations

* PDF generation is asynchronous and may take a moment.
* The feature must be enabled by the survey owner.
* Delphi surveys do not allow participants to download their contribution as PDF.
* The PDF contains all answers as submitted.
* The PDF can also be sent by email to the respondent.

## Troubleshooting

* PDF not ready: Generation is asynchronous. Wait and check again.
* Download button not visible: The survey owner has not enabled this feature.

## Related Articles

* ES-012 — How do respondents submit a contribution?
* ES-014 — How do I edit my contribution after submission?
* ES-061 — How do respondents download the survey as PDF?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/menu.js
* Backend files: src/main/java/com/ec/survey/controller/RunnerController.java, src/main/java/com/ec/survey/service/PDFService.java, src/main/java/com/ec/survey/controller/WorkerController.java
* Classes: RunnerController, PDFService, WorkerController
* Methods: createanswerpdf, createAnswerPDF
* Routes: /worker/createanswerpdf/{code}, /pdf/answer/{id}
* Message keys: label.DownloadContribution, label.AllowDownloadContributionPDFnewnew, info.AllowDownloadContributionPDFnew, info.AllowDownloadContributionPDFDelphi
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Runner
* EUSurvey area: Respondent Workflow
* Feature: Download PDF
* User intent: How do I download my contribution as PDF?
* Article type: How-To
* User type: Respondent
* Required permission: Respondent
* Survey status: Published and Active
* Environment: All
* Keywords: PDF, download, print, contribution, answers
* Synonyms: print contribution, get PDF, download answers as PDF
* Acronyms: N/A
* Related entities: AnswerSet, Survey
* Security / privacy relevance: PDF contains respondent answers
* Search boost terms: download contribution PDF, print answers
* Source files: src/main/java/com/ec/survey/controller/RunnerController.java, src/main/java/com/ec/survey/service/PDFService.java, src/main/java/com/ec/survey/controller/WorkerController.java
* Duplicate status: New
