# WS-039_apply_pending_survey_changes — WS-039 - Apply pending survey changes

## Intent / Description

Explain how to apply changes to the published version of a survey after modifications.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use applyChanges to apply all pending changes of a survey. This is useful after changing properties or other settings that affect an already published survey. API summary Status Active Endpoint <base_url>/webservice/applyChanges/[Alias] Supported method(s) GET Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Expected output Returns 1 if changes were applied successfully. Returns empty output in case of a problem. Main HTTP responses 200 OK Changes applied. 412 Precondition Failed Survey does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-036 - Change survey title or contact information
* WS-037 - Manage background documents
* WS-038 - Manage useful links

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-039_apply_pending_survey_changes.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-039, apply, pending, survey, changes
* Synonyms: How do I apply pending survey changes through the API?, When should I call applyChanges?
* Authority: Document-derived guidance
