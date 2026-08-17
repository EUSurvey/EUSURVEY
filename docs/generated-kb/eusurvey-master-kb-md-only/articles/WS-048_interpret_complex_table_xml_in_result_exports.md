# WS-048_interpret_complex_table_xml_in_result_exports — WS-048 - Interpret complex table XML in result exports

## Intent / Description

Explain how Complex Table elements and cell values are represented in EUSurvey XML exports.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical reference
* UI location: Not specified
* Backend location: Not specified

## Short Answer

A Complex Table is represented by a ComplexTableTitle and one Cell element for each non-empty cell. Each Cell can contain CellTitle and ResultText. Answers link to the corresponding cell using qid. For Single Choice and Multiple Choice cells, answers also use aid for the selected option. For Free Text, Number or Formula cells, the value is contained as text in the Answer element.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Read ComplexTableTitle to identify the complex table.
2. Read Cell elements to identify non-empty cells and their cell type, row and column.
3. Use CellTitle and ResultText to map labels shown in results.
4. For each Answer, use qid to match the cell.
5. For choice-type cells, use aid to identify the selected answer option.
6. For text, number or formula cells, read the answer text directly.

## Important Conditions / Limitations

* Empty cells are not represented as Cell elements.
* The annex example contains every possible complex-table cell type once, including static text, free text, formula, single choice, multiple choice and number.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-021 - Understand XML results structure
* WS-047 - Interpret formula XML in result exports

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-048_interpret_complex_table_xml_in_result_exports.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: Complex Table XML, Cell, CellTitle, ResultText, qid, aid
* Synonyms: How is a Complex Table represented in XML?, How are complex table cell answers linked to cells?
* Authority: Document-derived guidance
