# WS-023_prepare_combined_result_exports — WS-023 - Prepare combined result exports

## Intent / Description

Explain how to export XML, PDF and uploaded files together.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use prepareAllResults when an integration needs a single asynchronous export that can include XML results, individual PDF contributions and uploaded files. The XML, PDF and uploaded-file flags control the content of the returned ZIP. API summary Status Active Endpoint <base_url>/webservice/prepareAllResults/[Alias]/[Start]/[End]/[Type]/[XML]/[PDF]/[UPLOADED] Supported method(s) GET Required privilege Results RO Parameters and input Alias Survey alias. Start/End Date range in yyyy-MM-dd_HH-mm-ss. Use 0 for open start or end. Type N for new, U for updated, A for both. Default is N when omitted; 0 uses default implementation. XML true to include result.xml; false otherwise. PDF true to include individual contribution PDFs; false otherwise. UPLOADED true to include files uploaded by participants; false otherwise. Expected output Returns HTTP 202 with a ticket. getResults returns a ZIP named results-<alias>[_<start-date>][_to_<end-date>].zip. Main HTTP responses 202 Accepted Combined export request accepted; ticket returned. 412 Precondition Failed Alias does not exist, alias was never published, specified token is invalid, or survey is archived. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* For the XML format, use the same interpretation as prepareResultsXML.
* If PDF creation fails, an error.txt file can be included in the ZIP.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-020 - Prepare XML result exports
* WS-022 - Prepare PDF result exports

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-023_prepare_combined_result_exports.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-023, prepare, combined, result, exports
* Synonyms: How do I export XML, PDFs and uploaded files together?, What do the XML, PDF and UPLOADED flags mean?
* Authority: Document-derived guidance
