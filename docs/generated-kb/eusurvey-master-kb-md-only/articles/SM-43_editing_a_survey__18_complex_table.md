# SM-43_editing_a_survey__18_complex_table — 43. Complex Table

## Intent / Description

This article explains the EUSurvey concept or feature: Complex Table.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Editing a survey
* Environment: All
* Article type: Concept
* UI location: Not specified
* Backend location: Not specified

## Short Answer

'Complex Table' is a table-like survey element that allows you to compose other survey items in a more complex way. It enables visual linking of different questions and layout of text passages (e.g. displaying text in columns).

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. How do I configure a 'Complex Table'?
2. In the survey 'Editor' add a 'Complex Table' element and select it. The 'Element Properties' provide the same setting options as the regular 'Table' element.
3. How do I configure the 'Complex Table' single cells?
4. Select a single cell in the survey preview section.
5. You can specify different question types into different cells. Those types correspond to the standard question types.
6. 'Static Text' can be displayed in columns by using the 'Column Span' property. Note that it can only span subsequent cells, thereby removing the contents of those cells if they were already configured.
7. The other available cell types ('Free Text', 'Formula', 'Single Choice', 'Multiple Choice', 'Number') are basically the same as their regular counterparts outside a 'Complex Table' and can be edited in the same way.
8. How to display text in columns?
9. Text passages can be displayed in columns by splitting the text over several 'Complex Table' cells. Configure the type of the respective cells to 'Static Text'. Under 'Element Properties > Text' the desired text can now be entered for each cell individually.
10. How can cells be configured so that they are not editable?
11. The cell types of the 'Complex Table' that cannot be edited are 'Static Text' and 'Empty'.
12. For other cell types, the 'Element Properties > Read only' can be used to prevent direct user input.
13. How can a cell be configured so that its text spans multiple columns?
14. Text in 'Complex Tables' can be configured to span across multiple columns. Select a cell with the 'Cell Type > Static Text'. Under 'Element Properties', the 'Column Span' option is responsible for how many columns are covered. Note that this function covers subsequent cells, thereby removing the contents of those cells if they were already configured.
15. How can I delete a question in a 'Complex Table' cell?
16. The content of cells in a 'Complex Table' cannot be removed individually using the editor's delete function. Instead, the 'Cell Type' of the cell must be reset to 'Empty'.
17. How can I get a chart in the statistics screen?
18. Min and Max values must be set; At most 10 values are possible.

## Important Conditions / Limitations

* The cell types of the 'Complex Table' that cannot be edited are 'Static Text' and 'Empty'.
* The content of cells in a 'Complex Table' cannot be removed individually using the editor's delete function. Instead, the 'Cell Type' of the cell must be reset to
* Min and Max values must be set; At most 10 values are possible.

## Troubleshooting

Not specified in source document.

## Related Articles

* How do I start the Editor?
* How do I create a questionnaire via the EUSurvey editor?
* How do I add or remove questions to my questionnaire?

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 02-FAQs_SurveyManager/43_editing_a_survey__18_complex_table.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: Editing a survey
* Keywords: complex, editing, survey, table
* Synonyms: Complex Table
* Authority: Document-derived guidance
