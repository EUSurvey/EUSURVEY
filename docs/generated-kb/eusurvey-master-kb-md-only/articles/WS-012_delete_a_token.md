# WS-012_delete_a_token — WS-012 - Delete a token

## Intent / Description

Explain how to delete a token from a token list and the effect of deletion on submitted contributions.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use deleteToken to delete a token from a token list. Deleting a token that has already been used to submit a contribution does not invalidate or remove the submitted answer. Deleting an unused token prevents access through that token. API summary Status Active Endpoint <base_url>/webservice/deleteToken/[List ID]/[Token] Supported method(s) GET Required privilege Form Management RW Parameters and input List ID ID of the token list. Token Individual token to delete. Expected output No content is returned on successful deletion. Main HTTP responses 204 No Content Token deleted successfully. 412 Precondition Failed List ID or token does not exist, token is not in the list, or survey is archived. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* Deleting a token is not the same as deleting a contribution.
* Use deleteContribution for contribution deletion when the required result privilege exists.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-042 - Delete a contribution
* WS-010 - Deactivate a token

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-012_delete_a_token.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-012, delete, token
* Synonyms: How do I delete an invitation token?, Does deleting a token delete the submitted contribution?
* Authority: Document-derived guidance
