# WS-007_invitations_api_overview — WS-007 - Invitations API overview

## Intent / Description

Explain what the Invitations API is used for and which privilege it requires.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Concept
* UI location: Not specified
* Backend location: Not specified

## Short Answer

The Invitations API allows an authenticated system user to create token lists, generate tokens, activate or deactivate tokens, delete tokens, retrieve asynchronous token-generation results, and turn a submitted contribution back into draft state. These operations require Form Management read/write access on the survey.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Grant the system user Form Management RW on the survey.
2. Create a token list with createNewTokenList.
3. Generate tokens with createTokens and retrieve them with getResults.
4. Use activateToken, deactivateToken, or deleteToken for token lifecycle management.

## Important Conditions / Limitations

* Token generation is asynchronous because it can be computationally demanding.
* Generated token data can only be downloaded by the user that requested it.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-008 - Create a new token list
* WS-009 - Create tokens asynchronously
* WS-006 - Use asynchronous tickets and getResults

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-007_invitations_api_overview.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: Invitations API, guest list, token list, tokens, Form Management
* Synonyms: How can I manage invitation tokens through the API?, What privilege is required for token list operations?
* Authority: Document-derived guidance
