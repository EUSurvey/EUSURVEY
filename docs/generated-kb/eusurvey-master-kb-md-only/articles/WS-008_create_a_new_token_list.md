# WS-008_create_a_new_token_list — WS-008 - Create a new token list

## Intent / Description

Explain how to create a token-based guest list through the EUSurvey API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use createNewTokenList to create a new token list for a survey. The service returns a List ID that must be used by later token-management calls. API summary Status Active Endpoint <base_url>/webservice/createNewTokenList/[Alias]/[Active]/ Supported method(s) GET Required privilege Form Management RW Parameters and input Alias Alias of the survey for which the new guest list should be created. Active Boolean flag true/false indicating whether the guest list should be activated immediately. Expected output Returns the List ID of the newly created token list. Main HTTP responses 201 Created Token list created successfully. 412 Precondition Failed List ID does not exist, Active is not a valid boolean value, or the survey has been archived. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-009 - Create tokens asynchronously

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-008_create_a_new_token_list.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-008, create, new, token, list
* Synonyms: How do I create a token list with the API?, What does createNewTokenList return?
* Authority: Document-derived guidance
