# WS-005_handle_api_limits_and_common_http_errors — WS-005 - Handle API limits and common HTTP errors

## Intent / Description

Summarize the global request limit and recurring HTTP responses returned by the EUSurvey Web-services API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Troubleshooting
* UI location: Not specified
* Backend location: Not specified

## Short Answer

For security reasons, the EUSurvey Web-services API is limited to 5000 calls per user per day. Common errors include 403 for wrong credentials or insufficient privileges, 404 for unknown services or missing parameters, 412 when a prerequisite is not met, and 429 when the request limit is exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Log and inspect the HTTP status code for every API call.
2. For 403, verify credentials and survey privileges.
3. For 404, verify the endpoint path and required parameters.
4. For 412, check endpoint-specific preconditions, parameter formats, survey status, and whether the survey is archived.
5. For 429, reduce polling frequency or batch requests to stay within the daily limit.

## Important Conditions / Limitations

* Asynchronous polling must be designed carefully to avoid consuming the daily request allowance.
* Endpoint-specific 412 meanings are more precise than the generic meaning.
* Troubleshooting
* HTTP 403
* Check Basic Authentication and required survey privilege.
* HTTP 404
* Check that the endpoint exists and the full parameter list is present.
* HTTP 412
* Check survey existence, publication status, archived status, parameter validity, date format, IDs, tokens or aliases.
* HTTP 429
* Reduce request volume; the global limit is 5000 calls per user per day.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-006 - Use asynchronous tickets and getResults
* WS-017 - Troubleshoot Prefill API HTTP 412 errors

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-005_handle_api_limits_and_common_http_errors.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: HTTP 403, HTTP 404, HTTP 412, HTTP 429, rate limit, errors
* Synonyms: Why do I receive HTTP 429 from the API?, What do common API HTTP errors mean?, How many API calls can I make per day?
* Authority: Document-derived guidance
