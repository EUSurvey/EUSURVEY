# WS-046_interpret_matrix_xml_in_result_exports — WS-046 - Interpret matrix XML in result exports

## Intent / Description

Explain how single-choice and multiple-choice matrix answers are represented in EUSurvey XML exports.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical reference
* UI location: Not specified
* Backend location: Not specified

## Short Answer

In XML result exports, a matrix is represented by MatrixTitle, MatrixQuestion rows, MatrixAnswer columns and MatrixCell definitions. Submitted matrix selections are represented by Answer elements that use qid for the row and aid for the selected column. Answers are only given for matrix cells selected by the participant.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Read MatrixTitle to identify the matrix question.
2. Map MatrixQuestion entries to matrix rows.
3. Map MatrixAnswer entries to matrix columns.
4. Use MatrixCell definitions to understand possible row-column combinations.
5. In AnswerSet entries, read each Answer qid/aid pair as a selected cell.

## Important Conditions / Limitations

* The same qid/aid logic applies to both single-choice and multiple-choice matrices.
* In multiple-choice matrices, several Answer entries may refer to the same row because multiple columns can be selected.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-021 - Understand XML results structure
* WS-045 - Interpret table XML in result exports

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-046_interpret_matrix_xml_in_result_exports.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: matrix XML, MatrixTitle, MatrixQuestion, MatrixAnswer, MatrixCell, qid, aid
* Synonyms: How are matrix answers represented in XML?, How do I read a single-choice matrix export?, How do I read a multiple-choice matrix export?
* Authority: Document-derived guidance
