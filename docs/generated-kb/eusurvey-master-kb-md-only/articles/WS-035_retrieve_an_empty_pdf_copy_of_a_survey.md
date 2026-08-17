# WS-035_retrieve_an_empty_pdf_copy_of_a_survey — WS-035 - Retrieve an empty PDF copy of a survey

## Intent / Description

Explain how to obtain a blank PDF version of a questionnaire through the API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use getSurveyPDF to return an empty PDF copy of the last published version of the questionnaire. If the survey was not published, the PDF of the draft survey is returned. API summary Status Active Endpoint <base_url>/webservice/getSurveyPDF/[Alias] Supported method(s) GET Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Expected output Returns the PDF file of the published survey, or the PDF of the draft survey if not published. Main HTTP responses 200 OK Survey PDF returned. 412 Precondition Failed Survey does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-029 - Retrieve survey metadata

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-035_retrieve_an_empty_pdf_copy_of_a_survey.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-035, retrieve, empty, pdf, copy, survey
* Synonyms: How do I download a blank questionnaire PDF through the API?, Does getSurveyPDF return the published or draft survey?
* Authority: Document-derived guidance
