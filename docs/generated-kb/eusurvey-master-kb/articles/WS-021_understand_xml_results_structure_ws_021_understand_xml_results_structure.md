# WS-021_understand_xml_results_structure — WS-021 - Understand XML results structure

## Intent / Description

Explain the main structure and fields of XML results returned by the Results API.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical reference
* UI location: Not specified
* Backend location: Not specified

## Short Answer

The XML result file contains a definition section and an answer section. The Survey/Elements section describes survey elements in each language, including questions, answers, matrix rows, table rows and columns. The Answers section contains individual AnswerSet entries with metadata and submitted answers. Free-text and table answers contain text; choice, matrix and table selections reference question IDs and answer IDs.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Read the Survey element to identify the survey alias, UID and element definitions.
2. Use Elements lang entries to map element IDs to labels in each language.
3. Read AnswerSet entries to process each submitted contribution.
4. Use qid to identify the question, matrix row, table row or cell-related question.
5. Use aid to identify selected answers, matrix columns or table columns.
6. Use Scores when processing quiz score by section and question.

## Important Conditions / Limitations

* AnswerSet id corresponds to the token number or ual code returned in token-related flows.
* The user attribute may contain the participant email address if known or Anonymous for anonymous surveys.
* The userlogin attribute may contain the participant EU Login username if known or Anonymous for anonymous surveys.
* The uid attribute is a unique UUID for the survey.
* The type attribute contains a readable element type such as Section, Single Choice, Matrix Answer, Complex Table or File Upload.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-020 - Prepare XML result exports
* WS-045 - Interpret table XML in result exports
* WS-046 - Interpret matrix XML in result exports

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-021_understand_xml_results_structure.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: XML, AnswerSet, qid, aid, Survey UID, userlogin, scores
* Synonyms: What does the EUSurvey result XML contain?, How are answers represented in XML?, What do qid and aid mean?
* Authority: Document-derived guidance
