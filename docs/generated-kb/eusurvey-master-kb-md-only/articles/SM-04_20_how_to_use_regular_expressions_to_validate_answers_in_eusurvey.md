# SM-04_20_how_to_use_regular_expressions_to_validate_answers_in_eusurvey — SM-04.20 - How to use regular expressions to validate answers in EUSurvey

## Intent / Description

This article answers: SM-04.20 - How to use regular expressions to validate answers in EUSurvey

## Applies To

* Role(s): Survey Owner
* EUSurvey area: FAQs_SurveyManager
* Environment: All
* Article type: Concept + How-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

A regular expression, often shortened to regex, is a sequence of characters that defines a search or validation pattern. In EUSurvey, regex can be used to check and control what respondents can enter in survey fields. The system compares the respondent’s input with the regex pattern and accepts the input only if it matches.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Use regex for validation
2. Identify the survey field where input must follow a specific format.
3. Open the question or field settings in the editor.
4. Add the required regular expression in the validation or regex field available for that element.
5. Test the survey before publication to confirm that valid input is accepted and invalid input is rejected.
6. Common regex examples
7. EU language codes: ^(BG|ES|CS|DA|DE|ET|EL|EN|FR|GA|HR|IT|LV|LT|HU|MT|NL|PL|PT|RO|SK|SL|FI|SV)$
8. Numbers only: ^[0-9]+$
9. Email format: ^[^\s@]+@[^\s@]+\.[^\s@]+$
10. Date YYYY-MM-DD: ^(19|20)\d\d([- /.])(0[1-9]|1[012])\2(0[1-9]|[12][0-9]|3[01])$
11. EU project or grant code: ^[A-Z0-9]+(-[A-Z0-9]+)*$
12. Luxembourg mobile number: ^((+|00\s?)352)?\s?6[269]1(\s?\d{3}){2}$

## Important Conditions / Limitations

* Always test regex validation before publishing the survey.
* Use anchors such as ^ and $ when the whole input must match the pattern.
* Regex validation controls the format of the value, but it does not guarantee that the submitted information is real or authoritative.
* Very strict regex rules may block valid answers if they do not account for common variations in user input.

## Troubleshooting

Not specified in source document.

## Related Articles

* SM-04.03 - How to edit elements in a questionnaire
* SM-04.07 - Can I make a question mandatory?
* SM-04.19 - Formula field

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 02-FAQs_SurveyManager/SM-04_20_how_to_use_regular_expressions_to_validate_answers_in_eusurvey.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: FAQs_SurveyManager
* Keywords: regex, regular expression, validation, text input, field validation, email validation, date validation, EU language code, phone number
* Synonyms: What is a regular expression?, What is regex used for in EUSurvey?, How do I validate text input with regex?
* Authority: Document-derived guidance
