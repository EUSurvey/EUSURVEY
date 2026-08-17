# WS-016_handle_prefilling_api_special_cases — WS-016 - Handle Prefilling API special cases

## Intent / Description

Explain special URL encoding and value formats for choice questions, table questions and special characters in prefill calls.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Some prefilling cases require special syntax. For single or multiple choice questions, multiple answers are separated with commas. For table questions, the identifier must include the question identifier and row and column indices separated by plus signs. Special characters must be UTF-8 URL encoded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Choice example: <base_url>/webservice/prefill/ALIAS?token=CODE&CHOICE1=answer1,answer2,answer3
2. Table example: <base_url>/webservice/prefill/ALIAS?token=CODE&MYTABLE+1+2=value
3. Special-character example: use %20 for an empty space in a value.

## Important Conditions / Limitations

* The table syntax MYTABLE+1+2 refers to the second column of the first row of the table with identifier MYTABLE.
* Values containing spaces, #, +, &, or similar characters must be URL encoded.
* The Prefilling API cannot prefill dependent questions.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-015 - Create a prefilled draft contribution
* WS-017 - Troubleshoot Prefill API HTTP 412 errors

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-016_handle_prefilling_api_special_cases.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: choice prefill, table prefill, URL encode, special characters
* Synonyms: How do I prefill multiple-choice questions?, How do I prefill table cells?, How do I send special characters in prefill values?
* Authority: Document-derived guidance
