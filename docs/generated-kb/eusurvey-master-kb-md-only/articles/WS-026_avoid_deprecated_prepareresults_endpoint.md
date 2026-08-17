# WS-026_avoid_deprecated_prepareresults_endpoint — WS-026 - Avoid deprecated prepareResults endpoint

## Intent / Description

Explain the status of the older prepareResults endpoint and what to use instead.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

The prepareResults endpoint is marked as deprecated. It starts an asynchronous XML result export similar to prepareResultsXML, but integrations should prefer prepareResultsXML for current implementations. API summary Status Deprecated Endpoint <base_url>/webservice/prepareResults/[Alias]/[IDs]/[Start]/[End]/[Type] Supported method(s) GET Required privilege Results RO Parameters and input Alias Survey alias. IDs true/false flag for adding question and answer IDs. Start/End Optional dates in yyyy-MM-dd_HH-mm-ss or 0 for open ranges. Type N, U or A contribution type filter. Expected output Returns HTTP 201 with a ticket. getResults returns a ZIP named results-<alias>[_<start-date>][_to_<end-date>].zip. Main HTTP responses 201 Created Ticket created successfully. 412 Precondition Failed Alias does not exist, alias was never published, invalid Boolean/date range, or survey is archived. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* New integrations should use prepareResultsXML instead.
* Keep this article only to support legacy integrations that still call the deprecated endpoint.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-020 - Prepare XML result exports

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-026_avoid_deprecated_prepareresults_endpoint.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-026, avoid, deprecated, prepareresults, endpoint
* Synonyms: Is prepareResults still active?, Which endpoint replaces prepareResults?
* Authority: Document-derived guidance
