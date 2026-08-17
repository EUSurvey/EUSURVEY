# WS-013_turn_a_contribution_back_into_draft — WS-013 - Turn a contribution back into draft

## Intent / Description

Explain how to allow a participant to edit and resubmit a submitted contribution by returning it to draft state.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use contributionToDraft with the unique contribution code. If successful, the service returns the unique code of the draft, which can be used by the participant to change and resubmit the contribution. API summary Status Active Endpoint <base_url>/webservice/contributionToDraft/[code] Supported method(s) GET Required privilege Right to change answers for the survey Parameters and input code Unique code of the submitted contribution. Expected output Returns the unique code of the draft. Main HTTP responses 200 OK Contribution converted to draft and draft code returned. 412 Precondition Failed The contribution code does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* The user must be allowed to change answers for the survey related to the contribution.
* This endpoint is useful for workflows where a respondent must correct a submitted answer.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-042 - Delete a contribution

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-013_turn_a_contribution_back_into_draft.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-013, turn, contribution, back, into, draft
* Synonyms: How can I reopen a submitted contribution as draft?, What does contributionToDraft return?
* Authority: Document-derived guidance
