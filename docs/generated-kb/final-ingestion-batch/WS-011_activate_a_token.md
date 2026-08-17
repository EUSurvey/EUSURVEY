# WS-011_activate_a_token — WS-011 - Activate a token

## Intent / Description

Explain how to reactivate a previously deactivated token.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use activateToken to reactivate a token. The endpoint returns HTTP 204 if the token was reactivated successfully or was already active. API summary Status Active Endpoint <base_url>/webservice/activateToken/[List ID]/[Token] Supported method(s) GET Required privilege Form Management RW Parameters and input List ID ID of the token list. Token Individual token to activate. Expected output No content is returned when the token is active. Main HTTP responses 204 No Content Token reactivated successfully or already active. 412 Precondition Failed List ID or token does not exist, token is not in the list, or survey is archived. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-010 - Deactivate a token
* WS-012 - Delete a token

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-011_activate_a_token.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-011, activate, token
* Synonyms: How do I reactivate an invitation token?, Why does activateToken return HTTP 204?
* Authority: Document-derived guidance
