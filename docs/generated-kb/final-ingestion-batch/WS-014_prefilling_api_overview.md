# WS-014_prefilling_api_overview — WS-014 - Prefilling API overview

## Intent / Description

Explain the Prefilling API, its prerequisites, and when to use it.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Concept
* UI location: Not specified
* Backend location: Not specified

## Short Answer

The Prefilling API creates draft contributions containing predefined answers so that individual participants can receive personalized questionnaires. The survey must be secured, the elements to prefill must have identifiers, and the survey must have been published at least once. The API cannot prefill questions that are dependent on a selection of another question.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Secure the survey.
2. Set identifiers for the elements that must be prefilled.
3. Publish the survey at least once before calling the Prefilling API.
4. Call the prefill endpoint once per guest-list entry or token.
5. Send the generated prefilled draft link to the participant.

## Important Conditions / Limitations

* Date answers must use DD/MM/YYYY format.
* A successful call returns HTTP 204 and creates a draft; it does not return the full draft content.
* Dependent questions cannot be prefilled through this API.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-015 - Create a prefilled draft contribution
* WS-016 - Handle Prefilling API special cases
* WS-017 - Troubleshoot Prefill API HTTP 412 errors

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-014_prefilling_api_overview.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: prefill, draft, personalized link, token, identifier
* Synonyms: How can I generate prefilled EUSurvey questionnaires?, What are the prerequisites for the Prefilling API?, Can I prefill dependent questions?
* Authority: Document-derived guidance
