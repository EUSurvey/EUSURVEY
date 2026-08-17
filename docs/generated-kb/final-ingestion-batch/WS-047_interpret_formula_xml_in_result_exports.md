# WS-047_interpret_formula_xml_in_result_exports — WS-047 - Interpret formula XML in result exports

## Intent / Description

Explain how formula and number-slider values are represented in XML result exports.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical reference
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Formula and number-slider elements are represented as Question elements in the XML definition section. Submitted values are represented as Answer elements that use qid to refer to the question. The calculated or entered value is contained as text inside the Answer element.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Map the formula or number-slider Question element in the Survey/Elements section.
2. Find Answer entries with the corresponding qid in each AnswerSet.
3. Read the answer text value as the submitted or calculated result.

## Important Conditions / Limitations

* The annex example shows a formula question referencing a number-slider question; both appear as Question elements and answers are text values linked by qid.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-021 - Understand XML results structure
* WS-048 - Interpret complex table XML in result exports

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-047_interpret_formula_xml_in_result_exports.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: formula XML, number slider, calculated field, qid
* Synonyms: How are formula values represented in result XML?, How do I identify a calculated value in XML?
* Authority: Document-derived guidance
