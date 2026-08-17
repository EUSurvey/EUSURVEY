# WS-043_retrieve_deleted_contributions — WS-043 - Retrieve deleted contributions

## Intent / Description

Explain how to list deleted contributions for a survey.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use getDeletedContributions to return XML containing the deleted contributions of a survey. Each contribution entry includes the unique code, submission date and deletion date. API summary Status Active Endpoint <base_url>/webservice/getDeletedContributions/[Alias] Supported method(s) GET Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias or survey UID. Expected output Returns XML with DeletedContributions and Contribution entries. Each contribution includes id, created and deleted date in yyyy-MM-dd_HH-mm-ss format. Main HTTP responses 200 OK Deleted contributions returned. 412 Precondition Failed Survey does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-042 - Delete a contribution

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-043_retrieve_deleted_contributions.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-043, retrieve, deleted, contributions
* Synonyms: How do I list deleted contributions through the API?, What metadata is returned for deleted contributions?
* Authority: Document-derived guidance
