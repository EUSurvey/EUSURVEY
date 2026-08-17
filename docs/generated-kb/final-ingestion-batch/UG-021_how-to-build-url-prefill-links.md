# UG-021_how-to-build-url-prefill-links — UG-021 — How to build URL prefill links

## Intent / Description

Explain the URL patterns used to prefill a survey.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Users Guides
* Environment: All
* Article type: How-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Append identifier=value pairs to the survey URL. Use ampersands to join multiple parameters. The URL pattern depends on whether the link is for the test page, published survey, token-secured survey or contact invitation.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* For open surveys or surveys secured with password or EU Login, use the published runner link with parameters.
* For token-secured surveys, include the token before the query parameters.
* For contact invitations, use the invited link pattern with the invitation-specific values.

## Troubleshooting

Not specified in source document.

## Related Articles

* UG-022 — Regular identifier-value pairs
* UG-027 — Hide prefilled questions

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 03-Users Guides/UG-021_how-to-build-url-prefill-links.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: Users Guides
* Keywords: URL pattern, identifier value, query parameter, escape code
* Synonyms: How do I create a prefilled survey link?, How do I add several prefilled answers to the URL?
* Authority: Document-derived guidance
