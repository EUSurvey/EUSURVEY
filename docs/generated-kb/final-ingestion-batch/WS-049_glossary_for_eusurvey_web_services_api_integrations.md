# WS-049_glossary_for_eusurvey_web_services_api_integrations — WS-049 - Glossary for EUSurvey Web-services API integrations

## Intent / Description

Define key terms used in the EUSurvey Web-services API specification.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical reference
* UI location: Not specified
* Backend location: Not specified

## Short Answer

The EUSurvey Web-services API uses several recurring terms. A Guest List is a collection of individual accesses to a survey. A List ID identifies a guest list. A Ticket identifies an asynchronous request. A Token represents an individual access to a secured survey and allows one contribution. A Token List is a guest list containing anonymous token accesses.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Use Guest List when discussing collections of contacts or anonymous accesses to a survey.
2. Use List ID when calling token-list endpoints.
3. Use Ticket when polling for asynchronous results.
4. Use Token when granting individual access to a secured survey.
5. Use Token List when managing many anonymous token accesses through the API.

## Important Conditions / Limitations

* The API currently offers token-related guest-list creation and management through the Invitations API.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-006 - Use asynchronous tickets and getResults
* WS-007 - Invitations API overview

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-049_glossary_for_eusurvey_web_services_api_integrations.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: Guest List, List ID, Ticket, Token, Token List
* Synonyms: What is a guest list?, What is a token list?, What is a ticket in the API?
* Authority: Document-derived guidance
