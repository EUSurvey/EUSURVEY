# SM-89_analysing_exporting_and_publishing_resul__14_how_are_ranking_question_scores_calculated — 89. How are ranking question scores calculated?

## Intent / Description

This article helps EUSurvey survey managers understand or perform the task: How are ranking question scores calculated.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Analysing, exporting and publishing results
* Environment: All
* Article type: How-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Ranking questions are used to offer the possibility to your survey participants to rank a set of items per order of importance. It is recommended to limit items to rank to 5. If you ask more to your survey participants it might be hard for them to properly rank all the items.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Survey respondents' most preferred choice (which they rank first) get the highest weight, and the least preferred choice (which they rank last) get a weight of 1. The weight is therefore proportionally reversed with respect to the ranking of the item.
2. For example, if a Ranking question is made of 5 items, weights are assigned as follows:
3. The top ranked item has a weight of 5 The second item has a weight of 4 The third item has a weight of 3 The fourth item has a weight of 2 The fifth item has a weight of 1
4. The score is calculated as being the average weight given by survey respondents.

## Important Conditions / Limitations

* Ranking questions are used to offer the possibility to your survey participants to rank a set of items per order of importance. It is recommended to limit items to

## Troubleshooting

Not specified in source document.

## Related Articles

* Where can I find the contributions submitted by my respondents?
* How can I download submitted contributions?
* How can I extract the Draft answers?

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 02-FAQs_SurveyManager/89_analysing_exporting_and_publishing_resul__14_how_are_ranking_question_scores_calculated.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: Analysing, exporting and publishing results
* Keywords: analysing, calculated, exporting, publishing, question, ranking, results, scores
* Synonyms: How are ranking question scores calculated?
* Authority: Document-derived guidance
