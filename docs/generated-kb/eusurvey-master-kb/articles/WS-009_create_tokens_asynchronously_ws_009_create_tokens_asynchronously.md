# WS-009_create_tokens_asynchronously — WS-009 - Create tokens asynchronously

## Intent / Description

Explain how to request the creation of tokens for an existing token list.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use createTokens to start asynchronous creation of access tokens for a token list. The endpoint returns a ticket that must be used with getResults to retrieve the generated token XML. API summary Status Active Endpoint <base_url>/webservice/createTokens/[List ID]/[Number] Supported method(s) GET Required privilege Form Management RW Parameters and input List ID ID of the token list created with createNewTokenList. Number Number of tokens to create. Natural number between 1 and 100,000. Expected output Returns an HTTP 202 ticket identifying the creation process. The final XML contains the tokenList ID and a list of unique access tokens using ual code attributes. Main HTTP responses 202 Accepted Token creation request accepted; ticket returned. 416 Range Not Satisfiable Requested token count is outside the allowed range or exceeds the maximum for the list. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* Poll getResults with the ticket to retrieve the generated tokens.
* The specification mentions a maximum request larger than 1,000,000 as invalid; the parameter description limits a single request to 1 to 100,000 tokens.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-006 - Use asynchronous tickets and getResults
* WS-008 - Create a new token list

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-009_create_tokens_asynchronously.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-009, create, tokens, asynchronously
* Synonyms: How do I generate invitation tokens using the API?, Why does createTokens return a ticket instead of tokens immediately?
* Authority: Document-derived guidance
