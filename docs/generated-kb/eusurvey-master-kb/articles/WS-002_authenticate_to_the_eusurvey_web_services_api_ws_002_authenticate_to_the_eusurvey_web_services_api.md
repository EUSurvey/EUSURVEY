# WS-002_authenticate_to_the_eusurvey_web_services_api — WS-002 - Authenticate to the EUSurvey Web-services API

## Intent / Description

Explain how authentication works for technical integrations with the EUSurvey Web-services API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

The EUSurvey Web-services API uses Basic Authentication. The USERNAME and PASSWORD of a valid system user must be sent in the HTTP header of each request. Existing EU Login users are not supported for API access, whether they are internal or external users.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Obtain a valid EUSurvey system user for the integration.
2. Encode the username and password according to Basic Authentication rules in the HTTP Authorization header.
3. Send the Authorization header with every API request.
4. Do not rely on an interactive EU Login session or EU Login account credentials for Web-services API calls.

## Important Conditions / Limitations

* Wrong username or password normally results in HTTP 403.
* Credential handling should follow the security rules of the integrating system.
* Contact EUSurvey support if a system user is needed or if authentication fails unexpectedly.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-003 - Understand API permissions and privileges
* WS-004 - Build API URLs and requests

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-002_authenticate_to_the_eusurvey_web_services_api.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: Basic Authentication, system user, EU Login, credentials, API access
* Synonyms: How do I authenticate to the EUSurvey Web-services API?, Can I use my EU Login account for API calls?, Where should API credentials be sent?
* Authority: Document-derived guidance
