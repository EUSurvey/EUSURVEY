# WS-042_delete_a_contribution — WS-042 - Delete a contribution

## Intent / Description

Explain how to delete a submitted contribution through the API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use deleteContribution with the contribution ID, also referred to as the token number or ual code in the specification. The operation requires read/write results access for the survey to which the contribution belongs. API summary Status Active Endpoint <base_url>/webservice/deleteContribution/[ID] Supported method(s) GET Required privilege Read-write results access for the survey containing the contribution Parameters and input ID Contribution ID, token number, or ual code. Expected output Returns 1 if the contribution was deleted successfully. Returns empty output in case of a problem. Main HTTP responses 200 OK Contribution deleted successfully. 403 Forbidden Contribution does not belong to a survey for which the user has read/write results access. 412 Precondition Failed Contribution does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* Deleting a contribution is not the same as deleting a token or deleting the whole survey.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-043 - Retrieve deleted contributions
* WS-012 - Delete a token

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-042_delete_a_contribution.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-042, delete, contribution
* Synonyms: How do I delete a contribution through the API?, What permission is required to delete a contribution?
* Authority: Document-derived guidance
