# ES-051 — How do formula questions work?

## Intent / Description

This article explains how formula questions calculate values based on other survey answers.

## Applies To

* Role(s): Survey Owner, Form Manager
* EUSurvey area: Survey Editor
* Environment: All
* Article type: Concept
* UI location: Editor page
* Backend location: FormulaQuestion.java

## Short Answer

Formula questions display a calculated numerical value based on answers to other numerical questions in the survey. You define a formula using question IDs (identifiers) and mathematical operators. The result is calculated in real-time as the respondent fills in the survey.

## Prerequisites / Required Permissions

* FormManagement privilege or survey ownership
* Other numerical questions must exist in the survey for the formula to reference

## Procedure

1. Add a Formula question element in the Editor.
2. In the formula field, enter your calculation using question IDs.
3. Available functions: min(), max(), mean(), basic arithmetic (+, -, *, /).
4. Supported operators and functions are shown in the formula help.
5. Save the element.
6. The formula calculates in real-time as respondents answer.

## Important Conditions / Limitations

* Formula questions reference other questions by their ID (identifier/shortname).
* Supported functions: min(ID1,ID2,...), max(ID1,ID2,...), mean(ID1,ID2,...).
* Basic arithmetic: +, -, *, /, parentheses.
* The result updates dynamically in the runner as respondents fill in referenced questions.
* Formula questions are read-only for respondents.
* Formula results can have configurable decimal places.
* Min/Max limits can be set on the formula result.
* Invalid formulas are detected on save (missing brackets, unknown IDs).

## Troubleshooting

* 'Invalid formula': Check syntax, brackets, and that referenced IDs exist.
* 'Invalid formula: missing brackets': Ensure all parentheses are balanced.
* 'Invalid formula: ID not recognized': The referenced question ID does not exist. Check identifiers.

## Related Articles

* ES-006 — How do I edit a survey?
* ES-050 — How do I set up dependencies/visibility rules?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/runnerviewmodels.js, src/main/webapp/resources/js/edit_properties_helper.js
* Backend files: src/main/java/com/ec/survey/model/survey/FormulaQuestion.java, src/main/java/com/ec/survey/tools/SurveyHelper.java
* Classes: FormulaQuestion, SurveyHelper
* Methods: getFormula, updateFormulas, getVariablesForFormula
* Routes: N/A
* Message keys: label.Formula, info.Formula, info.formulamax, info.formulamean, info.formulamin, error.invalidFormula, error.invalidFormulaBrackets, error.invalidformulaUnknownID, info.ReadonlyFormula
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Editor
* EUSurvey area: Question Types
* Feature: Formula Questions
* User intent: How do formula questions work?
* Article type: Concept
* User type: Survey Owner, Form Manager
* Required permission: Survey Owner, Form Manager
* Survey status: Any
* Environment: All
* Keywords: formula, calculate, compute, math, dynamic, result
* Synonyms: calculated field, formula question, dynamic calculation
* Acronyms: N/A
* Related entities: FormulaQuestion, Element
* Security / privacy relevance: None
* Search boost terms: formula questions, calculated values, dynamic computation
* Source files: src/main/java/com/ec/survey/model/survey/FormulaQuestion.java, src/main/java/com/ec/survey/tools/SurveyHelper.java
* Duplicate status: New
