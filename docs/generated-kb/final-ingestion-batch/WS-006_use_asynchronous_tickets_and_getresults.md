# WS-006_use_asynchronous_tickets_and_getresults — WS-006 - Use asynchronous tickets and getResults

## Intent / Description

Explain how asynchronous API operations return tickets and how integrators retrieve prepared results.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Several EUSurvey API calls are asynchronous. The first call starts a job and returns a ticket. The caller must then poll getResults with that ticket until the prepared output is available or the server returns no content because the job is still running. API summary Status Active Endpoint <base_url>/webservice/getResults/[Ticket]?noempty=[true/false] Supported method(s) GET Required privilege Same authenticated user that created the asynchronous request Parameters and input Ticket Natural number returned by an asynchronous API call. noempty Optional. If true, no ZIP file is returned when the export contains no results. Default is false when omitted. Expected output HTTP 200 returns the requested data in the format defined by the initial asynchronous call. HTTP 204 means the process has not completed yet. Only the user who created the ticket can retrieve the corresponding result. Main HTTP responses 200 OK Prepared data is returned. 204 No Content The asynchronous process is not finished yet. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* Use sensible polling intervals to avoid reaching the 5000 calls per user per day limit.
* This getResults mechanism is used by invitation token generation and results export endpoints.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-009 - Create tokens asynchronously
* WS-020 - Prepare XML result exports

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-006_use_asynchronous_tickets_and_getresults.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-006, use, asynchronous, tickets, and, getresults
* Synonyms: What is a ticket in the EUSurvey API?, How do I retrieve asynchronous API output?, Why does getResults return HTTP 204?
* Authority: Document-derived guidance
