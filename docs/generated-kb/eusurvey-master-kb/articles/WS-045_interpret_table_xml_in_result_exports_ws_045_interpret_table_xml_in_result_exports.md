# WS-045_interpret_table_xml_in_result_exports — WS-045 - Interpret table XML in result exports

## Intent / Description

Explain how normal table question answers are represented in EUSurvey XML exports.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical reference
* UI location: Not specified
* Backend location: Not specified

## Short Answer

In XML result exports, a table is represented by a TableTitle, one TableQuestion for each row and one TableAnswer for each column. Each submitted cell answer is identified by qid for the row and aid for the column, and the answer text is contained inside the Answer element.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Read the TableTitle element to identify the table.
2. Map each TableQuestion to a row.
3. Map each TableAnswer to a column.
4. For each Answer in the Answers section, combine qid and aid to identify the table cell.
5. Read the text content of the Answer element as the participant’s input for that cell.

## Important Conditions / Limitations

* The annex example shows a table with two columns and two rows and demonstrates the generated XML.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-021 - Understand XML results structure
* WS-046 - Interpret matrix XML in result exports

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-045_interpret_table_xml_in_result_exports.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: table XML, TableTitle, TableQuestion, TableAnswer, qid, aid
* Synonyms: How are table answers represented in XML?, What do qid and aid mean for table cells?
* Authority: Document-derived guidance
