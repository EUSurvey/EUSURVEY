# WS-034_delete_a_survey — WS-034 - Delete a survey

## Intent / Description

Explain how to delete a survey through the API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use deleteSurvey to delete a survey. This is a destructive lifecycle operation and should be restricted to integrations that are explicitly allowed to remove surveys. API summary Status Active Endpoint <base_url>/webservice/deleteSurvey/[Alias] Supported method(s) GET Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Expected output Returns 1 if the survey was deleted successfully. Returns empty output in case of a problem. Main HTTP responses 200 OK Survey deleted successfully. 412 Precondition Failed Survey does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* Deleting a survey is different from deleting a contribution.
* Confirm retention and business requirements before automating survey deletion.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-033 - Archive or restore a survey
* WS-042 - Delete a contribution

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-034_delete_a_survey.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-034, delete, survey
* Synonyms: How do I delete a survey through the API?, What does deleteSurvey return?
* Authority: Document-derived guidance
