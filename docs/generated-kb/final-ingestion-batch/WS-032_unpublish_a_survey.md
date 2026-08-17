# WS-032_unpublish_a_survey — WS-032 - Unpublish a survey

## Intent / Description

Explain how to unpublish a survey through the Survey API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use unpublishSurvey to unpublish a survey. The endpoint returns 1 if the survey was successfully unpublished and 0 if it was already unpublished. API summary Status Active Endpoint <base_url>/webservice/unpublishSurvey/[Alias] Supported method(s) GET Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Expected output Returns 1 if successfully unpublished. Returns 0 if already unpublished. Main HTTP responses 200 OK Unpublish operation completed. 412 Precondition Failed Survey does not exist or survey is already unpublished. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-031 - Publish a survey
* WS-033 - Archive or restore a survey

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-032_unpublish_a_survey.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-032, unpublish, survey
* Synonyms: How do I unpublish a survey through the API?, What does unpublishSurvey return?
* Authority: Document-derived guidance
