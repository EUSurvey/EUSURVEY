# WS-031_publish_a_survey_or_schedule_automatic_publication — WS-031 - Publish a survey or schedule automatic publication

## Intent / Description

Explain how to publish a survey immediately or set automatic publishing dates through the API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use publishSurvey to publish a survey immediately when no start or end dates are provided, or to enable automatic publishing when start and end dates are provided. API summary Status Active Endpoint <base_url>/webservice/publishSurvey/[Alias]/[Start]/[End] Supported method(s) GET Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Start/End Optional publication time frame in yyyy-MM-dd_HH-mm-ss. Minutes and seconds should be 00; other values are automatically replaced by 00. Expected output Returns 1 if the survey was published successfully or the automatic publication dates were saved. Returns 0 if the survey is already published. Main HTTP responses 200 OK Publish operation completed. 412 Precondition Failed Survey does not exist or survey is already published. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-032 - Unpublish a survey
* WS-030 - Check survey publication status

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-031_publish_a_survey_or_schedule_automatic_publication.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-031, publish, survey, schedule, automatic, publication
* Synonyms: How do I publish a survey through the API?, Can I schedule survey publication through the API?
* Authority: Document-derived guidance
