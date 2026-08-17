# WS-015_create_a_prefilled_draft_contribution — WS-015 - Create a prefilled draft contribution

## Intent / Description

Explain how to call the Prefilling API to create a draft with predefined answers.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Call the prefill endpoint with the survey alias, token unique code, and identifier-value pairs. If successful, EUSurvey creates a prefilled draft and returns HTTP 204. The participant link follows the invited runner URL pattern with the token list ID and unique code. API summary Status Active Endpoint <base_url>/webservice/prefill/[Alias]?token=uniquecode&identifier1=value1&identifier2=value2&... Supported method(s) GET Required privilege Form management rights on the survey Parameters and input Alias Survey alias configured during survey creation. token Unique code connected to the participant. It can be a token ID or a unique string for address book entries. identifierN Identifier of the question or element to prefill. valueN Value to prefill. Dates must use DD/MM/YYYY. & Separator between several identifier-value pairs.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. <base_url>/webservice/prefill/MySurvey?token=ABC123&NAME=Jane%20Doe&EMAIL=jane.doe@example.eu
2. Draft link pattern: <base_url>/eusurvey/runner/invited/TokenListID/uniquecode
3. Expected output
4. Creates a draft contribution with the prefilled values.
5. Returns HTTP 204 on success.
6. Main HTTP responses
7. 204 No Content
8. Prefill call successful; draft created.
9. 412 Precondition Failed
10. Prefill prerequisite or input validation failed.
11. 403 Forbidden
12. Missing form managing rights or wrong credentials.
13. 403 Forbidden
14. Wrong username/password, missing required privilege, or user not allowed to perform the operation.
15. 404 Not Found
16. Unknown service or missing parameters.
17. 412 Precondition Failed
18. A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier.
19. 429 Too Many Requests
20. Daily API request limit exceeded.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-014 - Prefilling API overview
* WS-016 - Handle Prefilling API special cases

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-015_create_a_prefilled_draft_contribution.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-015, create, prefilled, draft, contribution
* Synonyms: How do I call the Prefilling API?, What link should I send after prefilling a survey?
* Authority: Document-derived guidance
