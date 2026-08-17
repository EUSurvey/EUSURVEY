# WS-018_results_api_overview — WS-018 - Results API overview

## Intent / Description

Explain how to export survey results through the EUSurvey Web-services API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Concept
* UI location: Not specified
* Backend location: Not specified

## Short Answer

The Results API allows a system user to prepare and download survey results in XML, PDF, or combined packages. Result preparation is asynchronous: first call a prepareResultsXXX endpoint to receive a ticket, then poll getResults until the prepared file is available. Result extraction requires Results read-only access on the survey.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Grant the system user Results RO on the survey.
2. Choose the export format: XML, PDF, combined XML/PDF/uploaded files, single token, or single contribution ID.
3. Call the corresponding prepare endpoint.
4. Poll getResults with the ticket returned by the prepare endpoint.
5. Download and process the ZIP, XML or PDF result according to the endpoint output format.

## Important Conditions / Limitations

* All prepareResultsXXX actions support an optional hook parameter that EUSurvey calls when the asynchronous export is finished.
* Only the original requesting user can download the prepared data.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-019 - Use webhooks for result export completion
* WS-020 - Prepare XML result exports
* WS-006 - Use asynchronous tickets and getResults

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-018_results_api_overview.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: Results API, export, XML, PDF, asynchronous, Results RO
* Synonyms: How can I export results through the API?, Why do result export calls return tickets?, What privilege is required to extract results?
* Authority: Document-derived guidance
