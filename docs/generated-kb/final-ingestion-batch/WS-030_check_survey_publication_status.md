# WS-030_check_survey_publication_status — WS-030 - Check survey publication status

## Intent / Description

Explain how to check whether a survey is published.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use getSurveyPublicationStatus to return the current publication status of a survey. The endpoint returns 1 if the survey is published and 0 if it is not published. API summary Status Active Endpoint <base_url>/webservice/getSurveyPublicationStatus/[Alias] Supported method(s) GET Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Expected output Returns 1 when the survey is published. Returns 0 when the survey is not published. Returns empty output in case of a problem. Main HTTP responses 200 OK Publication status returned. 412 Precondition Failed Survey does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

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
* WS-032 - Unpublish a survey

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-030_check_survey_publication_status.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-030, check, survey, publication, status
* Synonyms: How do I check whether a survey is published?, What does getSurveyPublicationStatus return?
* Authority: Document-derived guidance
