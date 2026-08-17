# WS-036_change_survey_title_or_contact_information — WS-036 - Change survey title or contact information

## Intent / Description

Explain how to change survey title and contact details through PATCH requests.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use changeSurveyTitle to change the survey title and changeContact to change the survey contact value. Both endpoints use PATCH and pass the new value in the request body. API summary Status Active Endpoint <base_url>/webservice/changeSurveyTitle/[Alias] and <base_url>/webservice/changeContact/[Alias] Supported method(s) PATCH Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Body for title New title in XHTML. Body for contact New contact string. Expected output Returns 1 if the title or contact was changed successfully. Returns empty output in case of a problem. Main HTTP responses 200 OK Update completed. 412 Precondition Failed Survey does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* The specification section for changeContact shows the URL as changeSurveyTitle/[Alias], but the endpoint name and purpose indicate changeContact. Verify the implemented endpoint in the target environment before integration.
* If the survey is already published, applyChanges may be needed to make pending changes visible.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-039 - Apply pending survey changes
* WS-029 - Retrieve survey metadata

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-036_change_survey_title_or_contact_information.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-036, change, survey, title, contact, information
* Synonyms: How do I change a survey title through the API?, How do I update survey contact information through the API?
* Authority: Document-derived guidance
