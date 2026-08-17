# WS-001_eusurvey_web_services_api_overview_for_integrators — WS-001 - EUSurvey Web-services API overview for integrators

## Intent / Description

Explain what the EUSurvey Web-services API is, who it is for, and the main constraints that apply before integration work starts.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Concept
* UI location: Not specified
* Backend location: Not specified

## Short Answer

EUSurvey provides a REST-style Web-services API that allows authenticated system users to automate advanced operations such as managing token invitations, preparing result exports, retrieving survey metadata, and acting on survey lifecycle operations. The API only supports specified URLs, requires the complete parameter list for each call, and returns HTTP error codes when parameters are missing or invalid.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Identify the integration use case: invitations, prefilling, results export, or survey lifecycle automation.
2. Request or use a valid system user, because EU Login users are not supported for API access.
3. Confirm the required survey-level privileges before calling survey-specific endpoints.
4. Use the application base URL followed by the documented /webservice path and endpoint.
5. Handle asynchronous endpoints by polling getResults with the returned ticket.

## Important Conditions / Limitations

* The API is intended for system-to-system integrations, not for normal EU Login interactive users.
* Only documented endpoints are supported. Calling incomplete or unspecified URLs results in an error.
* The production base URL example is https://ec.europa.eu/eusurvey/.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-002 - Authenticate to the EUSurvey Web-services API
* WS-003 - Understand API permissions and privileges
* WS-006 - Use asynchronous tickets and getResults

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-001_eusurvey_web_services_api_overview_for_integrators.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: REST API, integration, webservice, system user, API overview
* Synonyms: What is the EUSurvey REST API?, Can I integrate another system with EUSurvey?, What should I know before calling the API?
* Authority: Document-derived guidance
