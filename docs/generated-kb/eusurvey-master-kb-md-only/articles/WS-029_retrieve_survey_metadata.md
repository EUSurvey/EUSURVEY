# WS-029_retrieve_survey_metadata — WS-029 - Retrieve survey metadata

## Intent / Description

Explain how to retrieve detailed metadata for one survey.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use getSurveyMetadata to return XML metadata for a survey, including type, title, pivot language, owner, contact, status, pending changes, start and end dates, number of results, automatic publishing, useful links, background documents, tags, security, privacy, captcha, contribution edit/download options, draft option, skin, published results, confirmation page and unavailability page. API summary Status Active Endpoint <base_url>/webservice/getSurveyMetadata/[Alias] Supported method(s) GET Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Expected output Returns survey metadata XML or empty output in case of a problem. Main HTTP responses 200 OK Metadata returned. 412 Precondition Failed Survey does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-028 - Retrieve the user’s surveys
* WS-030 - Check survey publication status

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-029_retrieve_survey_metadata.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-029, retrieve, survey, metadata
* Synonyms: How do I retrieve survey metadata through the API?, Which survey settings are returned by getSurveyMetadata?
* Authority: Document-derived guidance
