# WS-040_set_a_dashboard_link_for_a_survey — WS-040 - Set a dashboard link for a survey

## Intent / Description

Explain how to set the dashboard link associated with a survey.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use SetDashboardLink to set the dashboard link for a survey. The endpoint uses PUT and returns no content on success. API summary Status Active Endpoint <base_url>/webservice/SetDashboardLink/[Alias] Supported method(s) PUT Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Body Dashboard URL according to the implementation introduced for this method. Expected output Returns HTTP 204 with no content on success. Main HTTP responses 204 No Content Dashboard link set successfully. 412 Precondition Failed Survey does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

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

* 04-WebServices/WS-040_set_a_dashboard_link_for_a_survey.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-040, set, dashboard, link, for, survey
* Synonyms: How do I set a survey dashboard link through the API?, What does SetDashboardLink return?
* Authority: Document-derived guidance
