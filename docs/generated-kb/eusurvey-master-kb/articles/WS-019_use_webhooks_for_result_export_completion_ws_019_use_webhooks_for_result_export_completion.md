# WS-019_use_webhooks_for_result_export_completion — WS-019 - Use webhooks for result export completion

## Intent / Description

Explain the optional hook parameter available on results preparation endpoints.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

All prepareResultsXXX actions can include an optional hook URL. When the asynchronous export finishes, EUSurvey calls the hook URL using a GET request. The placeholder TASKID in the hook URL is automatically replaced by the ticket ID, which can then be used to call getResults.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. <base_url>/webservice/prepareResultsXML/[Alias]/[IDs]?hook=https://myserver.example/myApp?task=TASKID
2. When finished, EUSurvey calls: https://myserver.example/myApp?task=<ticket>

## Important Conditions / Limitations

* The hook is optional; polling getResults remains supported.
* The receiving system should validate and handle the ticket securely.
* The hook URL should be reachable by EUSurvey.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-018 - Results API overview
* WS-006 - Use asynchronous tickets and getResults

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-019_use_webhooks_for_result_export_completion.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: webhook, hook, TASKID, asynchronous export
* Synonyms: Can EUSurvey notify my system when a result export is ready?, How does the hook parameter work?
* Authority: Document-derived guidance
