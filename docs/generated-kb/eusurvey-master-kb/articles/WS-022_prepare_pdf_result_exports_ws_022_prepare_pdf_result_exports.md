# WS-022_prepare_pdf_result_exports — WS-022 - Prepare PDF result exports

## Intent / Description

Explain how to request PDF files for survey contributions through the Results API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use prepareResultsPDF to start an asynchronous export of contributions in PDF format. After polling getResults with the ticket, the returned ZIP contains individual PDF files and uploaded files for the requested contributions. API summary Status Active Endpoint <base_url>/webservice/prepareResultsPDF/[Alias]/[Start]/[End]/[Type] Supported method(s) GET Required privilege Results RO Parameters and input Alias Survey alias. Start/End Optional date range in yyyy-MM-dd_HH-mm-ss. Use 0 for open ranges. Type N for new contributions, U for updated contributions, A for both. Default is N when omitted; 0 uses the default implementation. Expected output Returns HTTP 202 with a ticket. getResults returns a ZIP named results_pdf-<alias>[_<start-date>][_to_<end-date>].zip. ZIP contains one directory per TokenID with a PDF and uploaded files. Main HTTP responses 202 Accepted PDF export request accepted; ticket returned. 412 Precondition Failed Alias does not exist, alias was never published, specified token is invalid, or survey is archived. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* If PDF creation fails, an error.txt file is placed in the root directory of the ZIP.
* PDF creation is stopped after three errors to protect system health; error.txt may contain “more errors…” if processing was stopped.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-006 - Use asynchronous tickets and getResults
* WS-023 - Prepare combined result exports

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-022_prepare_pdf_result_exports.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-022, prepare, pdf, result, exports
* Synonyms: How do I export contributions as PDF through the API?, What happens if a PDF cannot be generated?
* Authority: Document-derived guidance
