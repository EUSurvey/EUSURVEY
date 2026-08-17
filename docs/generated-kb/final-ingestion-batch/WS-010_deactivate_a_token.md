# WS-010_deactivate_a_token — WS-010 - Deactivate a token

## Intent / Description

Explain how to deactivate a previously created token.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use deactivateToken to deactivate an individual token in a token list. A deactivated token should no longer allow access until it is reactivated. API summary Status Active Endpoint <base_url>/webservice/deactivateToken/[List ID]/[Token] Supported method(s) GET Required privilege Form Management RW Parameters and input List ID ID of the token list. Token Individual token to deactivate. Expected output No response body is specified; rely on HTTP status. Main HTTP responses 200 OK Token deactivated successfully. 412 Precondition Failed List ID or token does not exist, token is not in the list, or survey is archived. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-011 - Activate a token
* WS-012 - Delete a token

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-010_deactivate_a_token.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-010, deactivate, token
* Synonyms: How do I deactivate an invitation token?, What happens when deactivateToken succeeds?
* Authority: Document-derived guidance
