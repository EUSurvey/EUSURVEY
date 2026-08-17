# WS-020_prepare_xml_result_exports — WS-020 - Prepare XML result exports

## Intent / Description

Explain how to request an XML export of survey results.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use prepareResultsXML to start an asynchronous XML result export. The endpoint returns a ticket, and getResults later returns a ZIP containing result.xml and uploaded files for the included contributions. API summary Status Active Endpoint <base_url>/webservice/prepareResultsXML/[Alias]/[IDs]/[Start]/[End]/[Type] Supported method(s) GET Required privilege Results RO Parameters and input Alias Survey alias. IDs true/false flag indicating whether question and answer IDs should be added. Start/End Optional date range in yyyy-MM-dd_HH-mm-ss. Use 0 for open start or open end. Type N for new contributions, U for updated contributions, A for both. Default is N when omitted; 0 uses the default implementation. Expected output Returns HTTP 201 with a ticket. getResults returns a ZIP named results_xml-<alias>[_<start-date>][_to_<end-date>].zip. ZIP contains Results/result.xml and uploaded-file directories for relevant contributions. Main HTTP responses 201 Created Ticket created successfully. 412 Precondition Failed Alias does not exist, alias was never published, IDs is invalid, date format is wrong, end date is earlier than or equal to start date, or survey is archived. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-021 - Understand XML results structure
* WS-006 - Use asynchronous tickets and getResults

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-020_prepare_xml_result_exports.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-020, prepare, xml, result, exports
* Synonyms: How do I export results as XML through the API?, What date format is used for XML result exports?
* Authority: Document-derived guidance
