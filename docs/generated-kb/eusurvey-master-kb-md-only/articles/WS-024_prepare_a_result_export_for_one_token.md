# WS-024_prepare_a_result_export_for_one_token — WS-024 - Prepare a result export for one token

## Intent / Description

Explain how to request the result of a single contribution identified by token.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use prepareResultFromToken to start an asynchronous export of one contribution identified by its token. The endpoint returns a ticket; getResults returns a ZIP containing XML, PDF and uploaded files for that token. API summary Status Active Endpoint <base_url>/webservice/prepareResultFromToken/[Alias]/[Token]/[ID] Supported method(s) GET Required privilege Results RO Parameters and input Alias Survey alias. Token Token for which the answer should be exported. ID true/false flag indicating whether question and answer IDs should be added. Expected output Returns HTTP 201 with a ticket. getResults returns a ZIP containing result.xml, one PDF and uploaded files for the token. Main HTTP responses 201 Created Ticket created successfully. 412 Precondition Failed Alias does not exist, alias was never published, date format issue, end date issue, or survey is archived. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-025 - Prepare a result export for one contribution ID
* WS-006 - Use asynchronous tickets and getResults

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-024_prepare_a_result_export_for_one_token.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-024, prepare, result, export, for, one, token
* Synonyms: How do I export one contribution by token?, What does prepareResultFromToken return?
* Authority: Document-derived guidance
