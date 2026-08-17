# WS-017_troubleshoot_prefill_api_http_412_errors — WS-017 - Troubleshoot Prefill API HTTP 412 errors

## Intent / Description

List the common causes of HTTP 412 Precondition Failed responses from the Prefilling API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Troubleshooting
* UI location: Not specified
* Backend location: Not specified

## Short Answer

The Prefilling API returns HTTP 412 when the request is authenticated but a precondition or input validation rule fails. Common causes include an incorrect token, missing or unpublished survey, duplicate unique code in the call, an existing draft for the token, invalid date or time format, mismatched choice identifiers, missing question identifiers, an empty draft result, missing values for mandatory read-only questions, or an archived survey.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* Version 1.5.3.6 expanded the information returned for HTTP 412 errors in Prefill API calls.
* Troubleshooting
* Incorrect token
* Verify the token or unique code from the guest list export.
* Survey missing or unpublished
* Check the survey alias and ensure the survey has been published at least once.
* Duplicate unique code
* Ensure the token or unique code appears only once in the request.
* Draft already exists
* Check whether a previous prefill call already created a draft for this token.
* Invalid date/time
* Use DD/MM/YYYY for date answers in prefill calls.
* Choice identifier mismatch
* Verify that the answer identifier belongs to the question being prefilled.
* Question identifier not found
* Check the element identifier in the survey editor.
* Empty draft
* Ensure at least one valid identifier-value pair creates content.
* Mandatory read-only question missing
* Provide values for mandatory read-only questions.
* Archived survey
* Archived surveys cannot be used for this operation.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-014 - Prefilling API overview
* WS-015 - Create a prefilled draft contribution

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-017_troubleshoot_prefill_api_http_412_errors.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: HTTP 412, prefill error, validation, draft, token
* Synonyms: Why does the Prefilling API return HTTP 412?, How do I fix a failed prefill call?
* Authority: Document-derived guidance
