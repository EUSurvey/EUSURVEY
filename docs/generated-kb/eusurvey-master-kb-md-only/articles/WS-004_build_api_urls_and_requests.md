# WS-004_build_api_urls_and_requests — WS-004 - Build API URLs and requests

## Intent / Description

Explain the common URL structure and request conventions for EUSurvey Web-services API calls.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

All Web-service calls are made against the application base URL and the documented /webservice endpoints. For production, the base URL example is https://ec.europa.eu/eusurvey/. Each endpoint must be called with the full required parameter list; missing or incomplete parameters return errors.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Start from the environment base URL, for example https://ec.europa.eu/eusurvey/.
2. Append the documented /webservice endpoint path.
3. Replace placeholders such as [Alias], [List ID], [Token], [Start], or [End] with URL-safe values.
4. Add query parameters only where the endpoint supports them.
5. URL-encode special characters in query parameter values.

## Important Conditions / Limitations

* Most endpoints use GET, but some survey-management endpoints use PATCH, PUT or DELETE.
* Date-time parameters generally use the pattern yyyy-MM-dd_HH-mm-ss.
* Some API examples use placeholders such as <base_url>; these must be replaced with the real EUSurvey environment URL.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-002 - Authenticate to the EUSurvey Web-services API
* WS-005 - Handle API limits and common HTTP errors

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-004_build_api_urls_and_requests.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: base URL, endpoint, parameters, URL encoding, yyyy-MM-dd_HH-mm-ss
* Synonyms: What is the base URL for EUSurvey API calls?, How should I build Web-service URLs?, What happens if parameters are missing?
* Authority: Document-derived guidance
