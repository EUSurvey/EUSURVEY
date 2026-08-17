# WS-033_archive_or_restore_a_survey — WS-033 - Archive or restore a survey

## Intent / Description

Explain how to archive a survey and restore an archived survey through the API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use archiveSurvey to start asynchronous archiving of a survey, and restoreSurvey to start asynchronous restoration of an archived survey. A survey generally must not be published when archiving. API summary Status Active Endpoint <base_url>/webservice/archiveSurvey/[Alias] and <base_url>/webservice/restoreSurvey/[Alias] Supported method(s) GET Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Expected output archiveSurvey returns 1 if the asynchronous archiving process was started. restoreSurvey returns 1 if the asynchronous restoring process was started. Main HTTP responses 200 OK Archive or restore process started. 412 Precondition Failed For archiving: survey does not exist, is already archived, or is still published. For restore: archive does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* Unpublish a survey before archiving it if required.
* Archive/restore operations are asynchronous processes, but the endpoints return a simple success indicator rather than a standard ticket flow in the specification.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-032 - Unpublish a survey
* WS-034 - Delete a survey

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-033_archive_or_restore_a_survey.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-033, archive, restore, survey
* Synonyms: How do I archive a survey through the API?, How do I restore an archived survey?
* Authority: Document-derived guidance
